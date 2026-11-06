#!/usr/bin/env python3
import importlib.util
import json
import hashlib
import tempfile
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
import sys
sys.path.insert(0, str(ROOT / "scripts"))
from source_binding import REPO_ROOT, is_standalone_root, capture_binding
MODULE_PATH = ROOT / "tools" / "api_contract_skeleton.py"
spec = importlib.util.spec_from_file_location("api_contract_skeleton", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class ContractSkeletonTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix="api-family-skeleton-")
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.controller = self.root / "owned-framework/src/main/java/fixture/SharedController.java"
        self.controller.parent.mkdir(parents=True)
        self.controller.write_text('''package fixture;
import fixture.QueryService;
public class SharedController {
    private final QueryService service;
    @PostMapping(path = {"/people/queries", "/people/queries/raw", "/people/queries/searchable",
                         "/equipment/queries", "/equipment/queries/raw"})
    public Object query(String body) { return service.query(body); }
    @GetMapping(path = "/people/info/status")
    public Object status() { return null; }
}
''')
        service = self.controller.with_name("QueryService.java")
        service.write_text('''package fixture;
public class QueryService {
    private final QueryDao dao;
    public Object query(String body) { return dao.readPage(body); }
}
''')
        self.controller.with_name("QueryDao.java").write_text('''package fixture;
public class QueryDao {
    public Object readPage(String body) { return null; }
}
''')
        project = self.root / "project/src/main/java/fixture/people/resource/PeopleResource.java"
        project.parent.mkdir(parents=True)
        project.write_text("package fixture.people.resource; public interface PeopleResource {}")
        mapper = project.parent.parent / "mapper/conf/people-oracle-mapper.xml"
        mapper.parent.mkdir(parents=True)
        mapper.write_text('''<mapper namespace="fixture.PeopleMapper"><select id="selectByCondition">
SELECT A.ID,A.NAME FROM T_PEOPLE A
<include refid="GlobalMapper.appendWhereClausesIfPresent"/>
</select></mapper>''')
        rows = []
        for route_id, base, path, method in [
            ("p-query", "/people", "/people/queries", "POST"),
            ("p-raw", "/people", "/people/queries/raw", "POST"),
            ("p-search", "/people", "/people/queries/searchable", "POST"),
            ("e-query", "/equipment", "/equipment/queries", "POST"),
            ("e-raw", "/equipment", "/equipment/queries/raw", "POST"),
            ("p-status", "/people", "/people/info/status", "GET"),
            ("missing-query", "/missing", "/missing/queries", "POST"),
            ("missing-raw", "/missing", "/missing/queries/raw", "POST"),
        ]:
            rows.append({"id": route_id, "method": method, "path": path, "class_base": base,
                         "class": "fixture.people.resource.PeopleResource",
                         "source": "fixture/people/resource/PeopleResource.java",
                         "family": "personnel" if base == "/people" else "equipment",
                         "route_kind": "inherited-api", "owned_sources": [str(self.controller.relative_to(self.root))]})
        self.ledger = {"routes": rows, "boundary_routes": [], "source_commit": "fixture-head"}
        self.plan = {"assignment": {row["id"]: "group-fixture" for row in rows}}

    def build(self):
        return module.build(self.ledger, self.plan, source_root=self.root)

    def test_all_routes_are_assigned_to_ordered_waves(self):
        output = self.build()
        # The boundary inventory is generated from the current owned source
        # roots and can grow when a new owned compatibility seam is added.
        # Assert consistency with the generated wave rows rather than freezing
        # yesterday's route total.
        self.assertEqual(output["route_count"], sum(wave["route_count"] for wave in output["waves"]))
        self.assertEqual([wave["wave"] for wave in output["waves"]], list(module.WAVE_ORDER))
        cases = [case for wave in output["waves"] for case in wave["cases"]]
        self.assertEqual(len(cases), output["route_count"])
        self.assertEqual(len({case["id"] for case in cases}), output["route_count"])
        self.assertTrue(all(case["execution_group"] for case in cases))
        self.assertEqual([wave["runtime_id"] for wave in output["waves"]],
                         ["api-wave-" + wave for wave in module.WAVE_ORDER])
        self.assertTrue(all(case["wave_runtime_id"] == "api-wave-" + case["wave"]
                            for case in cases))

    def test_skeleton_does_not_promote_behavior(self):
        output = self.build()
        cases = [case for wave in output["waves"] for case in wave["cases"]]
        self.assertTrue(all(case["disposition"] == "unknown" for case in cases))
        self.assertTrue(all(case["unknown_reason"] for case in cases))
        self.assertTrue(all(wave["strict_ready"] is False for wave in output["waves"]))

    def test_shared_handler_groups_query_aliases_but_not_other_resources(self):
        output = self.build()
        families = output["behavior_families"]
        people = next(f for f in families if "p-query" in f["alias_route_ids"])
        self.assertEqual(people["alias_route_ids"], ["p-query", "p-raw", "p-search"])
        self.assertEqual(people["source_handler"]["class"], "fixture.SharedController")
        self.assertEqual(people["source_handler"]["method"], "query")
        equipment = next(f for f in families if "e-query" in f["alias_route_ids"])
        self.assertEqual(equipment["alias_route_ids"], ["e-query", "e-raw"])
        self.assertNotEqual(people["id"], equipment["id"])
        self.assertNotEqual(people["id"], next(f for f in families if "p-status" in f["alias_route_ids"])["id"])
        cases = [c for wave in output["waves"] for c in wave["cases"]]
        self.assertTrue(all(case["behavior_family_id"] for case in cases))
        self.assertEqual(sorted(c["route_id"] for c in cases), sorted(self.plan["assignment"]))

    def test_route_specific_actions_do_not_become_query_aliases(self):
        text = self.controller.read_text().replace('"/equipment/queries/raw"}', '"/equipment/queries/raw", "/people/queries/tasks"}')
        self.controller.write_text(text)
        task = dict(self.ledger["routes"][0], id="p-tasks", path="/people/queries/tasks")
        self.ledger["routes"].append(task)
        self.plan["assignment"]["p-tasks"] = "group-fixture"
        families = self.build()["behavior_families"]
        family = next(f for f in families if "p-tasks" in f["alias_route_ids"])
        self.assertEqual(family["alias_route_ids"], ["p-tasks"])
        self.assertEqual(family["path_template"], "/people/queries/tasks")

    def test_missing_handler_stays_route_specific_and_unknown(self):
        output = self.build()
        for route_id in ("missing-query", "missing-raw"):
            family = next(f for f in output["behavior_families"] if route_id in f["alias_route_ids"])
            self.assertEqual(family["alias_route_ids"], [route_id])
            self.assertIsNone(family["source_handler"])
            self.assertIn("source handler", " ".join(family["unknown_reasons"]))

    def test_source_chain_and_mapper_are_hashed_static_provenance(self):
        family = next(f for f in self.build()["behavior_families"] if "p-query" in f["alias_route_ids"])
        source = next(f for f in family["source_provenance"] if f["path"].endswith("SharedController.java"))
        self.assertEqual(source["sha256"], hashlib.sha256(self.controller.read_bytes()).hexdigest())
        self.assertEqual({node["class"] for node in family["dependency_chain"]},
                         {"fixture.SharedController", "fixture.QueryService", "fixture.QueryDao"})
        mapper = family["mapper_statements"][0]
        self.assertEqual(mapper["namespace"], "fixture.PeopleMapper")
        self.assertEqual(mapper["statement_id"], "selectByCondition")
        self.assertIn("T_PEOPLE", mapper["tables"])
        self.assertIn("A.ID,A.NAME", mapper["sql_projection"])
        self.assertEqual(family["permission_policy"], "unknown")
        self.assertEqual(family["organization_policy"], "unknown")
        self.assertTrue(any("permission" in reason for reason in family["unknown_reasons"]))
        self.assertTrue(all(node["evidence_kind"] == "static-source-reference" for node in family["dependency_chain"]))
        self.assertTrue(family["family_fingerprint"].startswith("fam-"))
        self.assertEqual(family["family_fingerprint_schema"], 1)
        self.assertIn("framework_seams", family["family_fingerprint_inputs"])

    def test_family_fingerprint_changes_only_for_changed_dependency_inputs(self):
        first = self.build()
        first_family = next(f for f in first["behavior_families"] if "p-query" in f["alias_route_ids"])
        first_equipment = next(f for f in first["behavior_families"] if "e-query" in f["alias_route_ids"])
        self.controller.with_name("QueryService.java").write_text(
            "package fixture; public class QueryService { public Object query(String body) { return null; } }\n"
        )
        second = self.build()
        second_family = next(f for f in second["behavior_families"] if "p-query" in f["alias_route_ids"])
        second_equipment = next(f for f in second["behavior_families"] if "e-query" in f["alias_route_ids"])
        self.assertNotEqual(first_family["family_fingerprint"], second_family["family_fingerprint"])
        self.assertNotEqual(
            first_equipment["family_fingerprint"],
            second_equipment["family_fingerprint"],
        )

        cases = [case for wave in second["waves"] for case in wave["cases"]]
        self.assertTrue(all(case["family_fingerprint"].startswith("fam-") for case in cases))

    def test_parameterized_jobs_keep_each_alias_unexecuted(self):
        output = self.build()
        jobs = output["behavior_family_jobs"]
        people = next(j for j in jobs if any(p["route_id"] == "p-query" for p in j["parameters"]))
        self.assertEqual({p["path"] for p in people["parameters"]},
                         {"/people/queries", "/people/queries/raw", "/people/queries/searchable"})
        self.assertTrue(all(job["strict_evidence"] is False and job["strict_ready"] is False for job in jobs))
        self.assertTrue(all(f["disposition"] == "unknown" and f["alias_equivalence"] == "unverified"
                            for f in output["behavior_families"]))
        self.assertIn("milestone", output["route_execution_model"]["application"])
        self.assertIn("persistent", output["route_execution_model"]["oracle"])
        self.assertTrue(all("no Oracle" in wave["runtime_policy"] for wave in output["waves"]))

    def test_ambiguous_handlers_cannot_merge_route_coverage(self):
        with self.controller.open("a") as out:
            out.write('\n@PostMapping(path="/people/queries") public Object duplicate(String body) { return null; }\n')
        family = next(f for f in self.build()["behavior_families"] if "p-query" in f["alias_route_ids"])
        self.assertEqual(family["alias_route_ids"], ["p-query"])
        self.assertIsNone(family["source_handler"])
        self.assertTrue(any("ambiguous" in reason for reason in family["unknown_reasons"]))

    def test_fast_skeleton_jobs_are_executable_diagnostic_metadata(self):
        fast_path = ROOT / "tools" / "fast_contract_skeleton.py"
        fast_spec = __import__("importlib.util").util.spec_from_file_location("fast_contract_skeleton", fast_path)
        fast = __import__("importlib.util").util.module_from_spec(fast_spec)
        assert fast_spec and fast_spec.loader
        fast_spec.loader.exec_module(fast)
        output = fast.build(self.ledger, families={"personnel"}, route_kinds={"inherited-api", "api-resource"},
                            limit=10, role="manager", organization="org-a", plan=self.plan)
        self.assertTrue(output["jobs"])
        job = output["jobs"][0]
        self.assertEqual(job["runner"], "lims-recovery/scripts/fast_differential_api_runner.py")
        self.assertTrue(job["probe_ids"])
        self.assertIn("--profiles", job["command_template"])
        self.assertTrue(all("source_class" in case for case in output["cases"]))


if __name__ == "__main__":
    unittest.main()
