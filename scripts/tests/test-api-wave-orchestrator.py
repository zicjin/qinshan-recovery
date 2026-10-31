#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest import mock
from types import SimpleNamespace

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "api_wave_orchestrator.py"
spec = importlib.util.spec_from_file_location("api_wave_orchestrator", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


def standalone_head(*suffix):
    return subprocess.check_output(
        ["git", "-C", str(module.REPO_ROOT), "rev-parse", *suffix],
        text=True,
    ).strip()


class ApiWaveOrchestratorTest(unittest.TestCase):
    def test_standalone_resolves_parent_feature_and_source_paths(self):
        self.assertTrue(str(module.FEATURE_ROOT).endswith(
            ".scratch/sunway-supplier-exit-api-first"))
        self.assertEqual(
            module._repo_path("lims-recovery/api-contracts/core/read.json"),
            module.REPO_ROOT / "api-contracts/core/read.json",
        )
        self.assertEqual(
            module._repo_path(".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/jobs.json"),
            module.FEATURE_ROOT / "diagnostic-evidence/pending/jobs.json",
        )

    def test_plan_exposes_one_runtime_per_wave(self):
        head = standalone_head("HEAD")
        plan = {
            "source_commit": head, "source_dirty": False,
            "wave_order": list(module.WAVE_ORDER), "runtime_count": 5,
            "wave_counts": {wave: 1 for wave in module.WAVE_ORDER}, "route_count": 5,
            "groups": [{"wave": wave, "case_ids": [wave]} for wave in module.WAVE_ORDER],
            "route_execution_model": {
                "boundary_process": False,
                "internal_http_bridge": False,
            },
        }
        module._validate_plan(plan)
        self.assertEqual(module._route_waves(plan)["foundation"], "foundation")
        self.assertEqual(module._wave_for_family("core-reagent-write-boundary"), "foundation")

    def test_stale_plan_is_rejected_even_when_scope_unchanged(self):
        parent = standalone_head("HEAD^")
        plan = {
            "source_commit": parent, "source_dirty": False,
            "wave_order": list(module.WAVE_ORDER), "runtime_count": 5,
            "wave_counts": {wave: 1 for wave in module.WAVE_ORDER}, "route_count": 5,
            "groups": [{"wave": wave, "case_ids": [wave]} for wave in module.WAVE_ORDER],
            "route_execution_model": {
                "boundary_process": False,
                "internal_http_bridge": False,
            },
        }
        with self.assertRaises(ValueError):
            module._validate_plan(plan)

    def test_job_validation_rejects_cross_wave_route(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            cases = root / "cases.json"
            cases.write_text(json.dumps({"family": "personnel", "cases": [{
                "id": "p", "route_id": "personnel-route"
            }]}), encoding="utf-8")
            jobs = {"schema": 1, "wave": "foundation", "jobs": [{
                "cases": str(cases), "output": str(root / "report.json")
            }]}
            with self.assertRaises(ValueError):
                module.validate_jobs(jobs, wave="foundation",
                                     route_waves={"personnel-route": "personnel"})

    def test_job_validation_rejects_boundary_process_model(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            cases = root / "cases.json"
            cases.write_text(json.dumps({"family": "core", "cases": [{
                "id": "boundary", "route_id": "boundary-route",
                "route_origin": "owned-java-source-only",
                "runtime_model": "separate-process",
                "boundary_semantics": "owned Java route"
            }]}), encoding="utf-8")
            jobs = {"schema": 1, "wave": "foundation", "jobs": [{
                "cases": str(cases), "output": str(root / "report.json")
            }]}
            with self.assertRaises(ValueError):
                module.validate_jobs(jobs, wave="foundation",
                                     route_waves={"boundary-route": "foundation"})

    def test_finalize_report_promotes_only_after_cleanup(self):
        with tempfile.TemporaryDirectory() as temp:
            report = Path(temp) / "report.json"
            report.write_text(json.dumps({"status": "executed-pass-pending-cleanup",
                                          "cleanup_pending": True}), encoding="utf-8")
            cleanup = {key: True for key in module.REQUIRED_CLEANUP}
            result = module.finalize_report(report, cleanup, wave="foundation",
                                            runtime_id="api-wave-foundation")
            self.assertEqual(result["status"], "executed-pass")
            self.assertFalse(result["cleanup_pending"])
            self.assertEqual(result["wave_runtime_id"], "api-wave-foundation")

    def test_tunnel_command_targets_remote_loopback(self):
        with mock.patch.object(module.subprocess, "Popen") as popen:
            process = popen.return_value
            process.poll.return_value = None
            with mock.patch.object(module.socket, "create_connection"):
                _, url = module._start_ssh_tunnel("sss2", "http://127.0.0.1:18089", 18090)
            command = popen.call_args.args[0]
            self.assertIn("18090:127.0.0.1:18089", command)
            self.assertEqual(url, "http://127.0.0.1:18090")

    def test_milestone_starts_and_cleans_once_for_multiple_waves(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            manifest = root / "milestone.json"
            manifest.write_text(json.dumps({"schema": 1, "lifecycle_id": "m1",
                                            "waves": [{"wave": "foundation", "jobs": "foundation.json"},
                                                      {"wave": "personnel", "jobs": "personnel.json"}]}))
            (root / "foundation.json").write_text("{}")
            (root / "personnel.json").write_text("{}")
            runtime = root / "runtime.json"
            runtime.write_text(json.dumps({"loopback_url": "http://127.0.0.1:18089"}))
            reports = []
            for wave in ("foundation", "personnel"):
                report = root / (wave + ".report.json")
                report.write_text(json.dumps({"status": "executed-pass-pending-cleanup", "wave": wave}))
                reports.append(report)
            head = standalone_head("HEAD")
            plan = {"source_commit": head, "source_dirty": False,
                    "wave_order": list(module.WAVE_ORDER), "runtime_count": 5,
                    "wave_counts": {wave: 1 for wave in module.WAVE_ORDER}, "route_count": 5,
                    "groups": [{"wave": wave, "case_ids": [wave]} for wave in module.WAVE_ORDER],
                    "assignment": {wave: wave for wave in module.WAVE_ORDER},
                    "route_execution_model": {"boundary_process": False, "internal_http_bridge": False}}
            plan_path = root / "plan.json"; plan_path.write_text(json.dumps(plan))
            cleanup = {key: True for key in module.REQUIRED_CLEANUP}
            start = subprocess.CompletedProcess([], 0, "", "")
            cleanup_result = subprocess.CompletedProcess([], 0, json.dumps(cleanup) + "\n", "")
            with mock.patch.object(module, "_run", side_effect=[start, cleanup_result]) as run_cmd, \
                 mock.patch.object(module, "_start_ssh_tunnel", return_value=(None, "http://127.0.0.1:19089")), \
                 mock.patch.object(module, "_module", return_value=SimpleNamespace(validate_file=lambda *args: {})), \
                 mock.patch.object(module, "MILESTONE_ROOT", root / "lifecycle"), \
                 mock.patch.object(module, "run_wave", side_effect=[
                     {"reports": [str(reports[0])], "failures": []},
                     {"reports": [str(reports[1])], "failures": []},
                 ]):
                result = module.run_milestone(
                    manifest_path=manifest, plan_path=plan_path, runtime_report=runtime,
                    start_script=Path("start.sh"), cleanup_script=Path("cleanup.sh"),
                    artifacts=[], timeout=1, host_port="18089", ssh_host="sss2", local_port=19089)
            self.assertEqual(result["status"], "executed-pass")
            self.assertEqual(run_cmd.call_count, 2)
            self.assertEqual(run_cmd.call_args_list[0].args[0], ["start.sh"])
            self.assertEqual(run_cmd.call_args_list[1].args[0], ["cleanup.sh"])

    def test_milestone_rejects_duplicate_lifecycle_lock(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            manifest = root / "milestone.json"
            manifest.write_text(json.dumps({"schema": 1, "lifecycle_id": "duplicate-lock",
                                            "waves": [{"wave": "foundation", "jobs": "foundation.json"}]}))
            plan = root / "plan.json"
            head = standalone_head("HEAD")
            plan.write_text(json.dumps({"source_commit": head, "source_dirty": False,
                                        "wave_order": list(module.WAVE_ORDER), "runtime_count": 5,
                                        "wave_counts": {wave: 1 for wave in module.WAVE_ORDER}, "route_count": 5,
                                        "groups": [{"wave": wave, "case_ids": [wave]} for wave in module.WAVE_ORDER],
                                        "assignment": {wave: wave for wave in module.WAVE_ORDER},
                                        "route_execution_model": {"boundary_process": False, "internal_http_bridge": False}}))
            lock = module.MILESTONE_ROOT / "duplicate-lock.lock"
            module.MILESTONE_ROOT.mkdir(parents=True, exist_ok=True)
            lock.write_text("running\n")
            try:
                with self.assertRaises(ValueError):
                    module.run_milestone(manifest_path=manifest, plan_path=plan,
                                        runtime_report=root / "runtime.json", start_script=Path("start.sh"),
                                        cleanup_script=Path("cleanup.sh"), artifacts=[], timeout=1,
                                        host_port="18089", ssh_host="sss2", local_port=19089)
            finally:
                lock.unlink(missing_ok=True)


if __name__ == "__main__":
    unittest.main()
