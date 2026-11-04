#!/usr/bin/env python3
"""Unit tests for the guarded, parallel sss2 owned deployment helper."""
from __future__ import annotations

import json
import os
from pathlib import Path
import stat
import tempfile
import unittest
from unittest import mock
import importlib.util


ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "sss2-owned-deploy.py"
SPEC = importlib.util.spec_from_file_location("sss2_owned_deploy", MODULE_PATH)
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC and SPEC.loader
SPEC.loader.exec_module(MODULE)


class Sss2OwnedDeployTest(unittest.TestCase):
    def test_remote_clone_report_is_unverified_and_hashed(self):
        script = (ROOT / "scripts" / "owned-sss2-remote-run.sh").read_text(encoding="utf-8")
        self.assertIn('"clone-ready-unverified"', script)
        self.assertIn("SOURCE_DUMP_SHA256", script)
        self.assertIn("TARGET_SCHEMA_METADATA_SHA256", script)
        self.assertIn("'strict_evidence':False", script)
        self.assertIn("T_EMC_ORDER_ITEM_GROUP", script)

    def test_owned_app_refresh_preserves_loopback_write_boundary(self):
        script = (ROOT / "scripts" / "refresh-owned-sss2-app.sh").read_text(encoding="utf-8")
        self.assertIn("nsenter -n -t", script)
        self.assertIn("port-forward-app.pid", script)
        self.assertIn("loopback_namespace_relay", script)
        self.assertIn('bind_host=sys.argv[4]', script)
        self.assertIn("container-exited-before-readiness", script)
        self.assertIn("ready_streak >= 6", script)
        self.assertIn("post-readiness-stability-failed", script)
        self.assertIn('docker inspect "$APP" -f \'{{.State.Status}}\'', script)
        self.assertIn('"status":"diagnostic-ready"', script)
        self.assertIn('"implementation_tree_digest":"$BINDING_DIGEST"', script)
        self.assertEqual(script.count("--env SPRING_PROFILES_ACTIVE=oracle"), 2)
        self.assertIn("--env SUNWAY_LCDP_ENABLE=false", script)
        self.assertIn("rsync -a --delete --checksum", script)

    def test_plan_scans_supplier_free_distribution(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            (root / "app").mkdir()
            (root / "lib").mkdir()
            (root / "start.sh").write_text("#!/bin/sh\n")
            (root / "app" / "lims-owned-application.jar").write_bytes(b"owned")
            (root / "manifest.json").write_text('{"vendor_runtime":false}\n')
            scan = MODULE.scan_distribution(root)
            self.assertFalse(scan["supplier_runtime"])
            self.assertEqual(scan["file_count"], 3)

    def test_plan_rejects_forbidden_distribution_marker(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            (root / "app").mkdir()
            (root / "start.sh").write_text("#!/bin/sh\n")
            (root / "app" / "lims-owned-application.jar").write_bytes(b"sunway-app.so")
            (root / "manifest.json").write_text('{"vendor_runtime":false}\n')
            with self.assertRaises(SystemExit):
                MODULE.scan_distribution(root)

    def test_run_requires_both_explicit_gates(self):
        args = type("Args", (), {
            "container_name": "lims-owned-sss2-clone",
            "distribution": str(ROOT / "owned-distribution" / "target" / "distribution"),
            "config": "/tmp/missing",
            "network": "network",
            "output": "/tmp/out.json",
        })()
        with mock.patch.dict(os.environ, {}, clear=True):
            with self.assertRaises(SystemExit):
                MODULE.run_deployment(args)

    def test_config_must_be_private_and_isolated(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "config.properties"
            path.write_text("owned.sss2.write.mode=isolated\n")
            path.chmod(stat.S_IRUSR | stat.S_IWUSR)
            MODULE.ensure_private_file(path)
            path.write_text("owned.sss2.write.mode=isolated\nsunway-app.so\n")
            with self.assertRaises(SystemExit):
                MODULE.ensure_private_file(path)


if __name__ == "__main__":
    unittest.main()
