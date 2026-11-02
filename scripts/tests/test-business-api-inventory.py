#!/usr/bin/env python3
"""Contract tests for the source-to-owned business API inventory."""
import tempfile
import unittest
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools"))
from business_api_inventory import (extract_routes, build_inventory,
                                    build_owned_boundary_inventory,
                                    _owned_route_is_concrete,
                                    _owned_alias_routes)  # noqa: E402


class BusinessApiInventoryTest(unittest.TestCase):
    def test_extracts_class_and_method_routes(self):
        source = '''
@RequestMapping("/secure/emc/module/demo")
public interface DemoResource {
    @PostMapping("/queries")
    Page<?> query(RestJsonWrapperBean request);
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    void update(@PathVariable String id, RestJsonWrapperBean request);
}
'''
        routes = extract_routes(source, "DemoResource.java")
        self.assertEqual([
            ("POST", "/secure/emc/module/demo/queries"),
            ("PUT", "/secure/emc/module/demo/{id}"),
        ], [(row["method"], row["path"]) for row in routes])
        self.assertTrue(all(row["source"] == "DemoResource.java" for row in routes))

    def test_extracts_multiline_path_arrays(self):
        source = '''
@RequestMapping("/secure/emc/module/demo")
public class DemoController {
            @PostMapping(path = {"/queries", "/queries/{id}/raw"},
            consumes = "application/json")
    public void query() {}
}
'''
        routes = extract_routes(source, "DemoController.java")
        self.assertEqual(
            [("POST", "/secure/emc/module/demo/queries"),
             ("POST", "/secure/emc/module/demo/queries/{id}/raw")],
            [(row["method"], row["path"]) for row in routes],
        )

    def test_inventory_marks_packaged_class_and_unknown_route(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            source = root / "project/src/main/java/com/example/DemoResource.java"
            source.parent.mkdir(parents=True)
            source.write_text('''
@RequestMapping("/demo")
public interface DemoResource {
    @GetMapping("/one") String one();
    @GetMapping("/two") String two();
}
''', encoding="utf-8")
            result = build_inventory(root / "project/src/main/java", {"com/example/DemoResource.class"})
        self.assertEqual(2, len(result))
        self.assertEqual("owned-class-present", result[0]["runtime_status"])
        self.assertEqual("owned-class-present", result[1]["runtime_status"])
        self.assertEqual("unknown", result[0]["behavior_status"])

    def test_inventory_includes_generic_auditable_inherited_routes(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            source = root / "project/src/main/java/com/example/DemoResource.java"
            source.parent.mkdir(parents=True)
            source.write_text('''
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
@RequestMapping("/secure/demo")
public interface DemoResource extends GenericResource<Object,Object,Long>, GenericAuditableResource<Object,Object,Long> {}
''', encoding="utf-8")
            result = build_inventory(root / "project/src/main/java", set())
        paths = {(row["method"], row["path"], row["annotation"]) for row in result}
        self.assertIn(("PUT", "/secure/demo/action/complete-task", "inherited.GenericAuditableResource"), paths)
        self.assertIn(("POST", "/secure/demo/queries/raw", "inherited.GenericResource"), paths)

    def test_inventory_matches_owned_route_by_method_and_path(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            source = root / "project/src/main/java/com/example/DemoResource.java"
            source.parent.mkdir(parents=True)
            source.write_text('@RequestMapping("/demo")\npublic interface DemoResource { @GetMapping("/one") String one(); }\n', encoding="utf-8")
            owned = root / "owned/src/main/java/com/example/OwnedDemoController.java"
            owned.parent.mkdir(parents=True)
            owned.write_text('@RestController\npublic class OwnedDemoController { @GetMapping("/demo/one") public String one(){return "ok";} }\n', encoding="utf-8")
            result = build_inventory(root / "project/src/main/java", set(), {"GET /demo/one": [str(owned)]})
        self.assertEqual("owned-class-present", result[0]["runtime_status"])
        self.assertEqual([str(owned)], result[0]["owned_sources"])

    def test_generic_owned_routes_do_not_match_unrelated_project_resources(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            source = root / "project/src/main/java/com/example/DemoResource.java"
            source.parent.mkdir(parents=True)
            source.write_text(
                '@RequestMapping("/demo")\n'
                'public interface DemoResource { @PostMapping("/queries") void query(); }\n',
                encoding="utf-8",
            )
            generic = root / "owned/src/main/java/com/example/GenericResource.java"
            generic.parent.mkdir(parents=True)
            generic.write_text(
                'public interface GenericResource {\n'
                '  @PostMapping("/queries") void query();\n'
                '  @PutMapping("/{id}") void save();\n'
                '}\n',
                encoding="utf-8",
            )
            concrete = root / "owned/src/main/java/com/example/OwnedDemoController.java"
            concrete.write_text(
                '@RequestMapping("/demo")\n'
                'public class OwnedDemoController { @PostMapping("/queries") void query(){} }\n',
                encoding="utf-8",
            )
            generic_routes = extract_routes(generic.read_text(), str(generic.relative_to(root / "owned/src/main/java")))
            self.assertTrue(generic_routes)
            self.assertFalse(any(_owned_route_is_concrete(route) for route in generic_routes))
            owned_map = {}
            for path in (generic, concrete):
                relative = str(path.relative_to(root / "owned/src/main/java")).replace("\\", "/")
                for route in extract_routes(path.read_text(), relative):
                    if _owned_route_is_concrete(route):
                        owned_map.setdefault(route["method"] + " " + route["path"], []).append(str(path.parent / path.name))
            result = build_inventory(root / "project/src/main/java", set(), owned_map)
        self.assertEqual(1, len(result))
        self.assertEqual([str(concrete)], result[0]["owned_sources"])

    def test_owned_only_routes_are_kept_in_boundary_inventory(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            owned = root / "owned/src/main/java/com/example/OwnedController.java"
            owned.parent.mkdir(parents=True)
            owned.write_text(
                '@RestController\n'
                'public class OwnedController { @GetMapping("/owned-only") public String read(){return "ok";} }\n',
                encoding="utf-8",
            )
            rows = build_owned_boundary_inventory([root / "owned/src/main/java"], [])
        self.assertEqual(1, len(rows))
        self.assertEqual("owned-source-only", rows[0]["boundary_scope"])
        self.assertEqual("GET", rows[0]["method"])
        self.assertEqual("/owned-only", rows[0]["path"])

    def test_expands_bounded_equipment_status_aliases(self):
        routes = _owned_alias_routes({
            "simple": "OwnedEquipmentResourceStatusController",
            "relative": "com/example/OwnedEquipmentResourceStatusController.java",
            "qualified": "com.example.OwnedEquipmentResourceStatusController",
        })
        paths = {row["method"] + " " + row["path"] for row in routes}
        self.assertEqual(8, len(paths))
        self.assertIn("GET /secure/emc/module/bp/equipt-sets/info/status", paths)
        self.assertIn("GET /secure/emc/module/bp/equipt-temp-sets/info/status", paths)

    def test_expands_personnel_configuration_wiring_aliases(self):
        routes = _owned_alias_routes({
            "simple": "OwnedPersonnelReadDao",
            "relative": "com/example/OwnedPersonnelReadDao.java",
            "qualified": "com.example.OwnedPersonnelReadDao",
        })
        paths = {row["method"] + " " + row["path"] for row in routes}
        self.assertEqual({
            "POST /secure/emc/module/mdm/emp/emps/queries",
            "POST /secure/emc/module/mdm/emp/emps/queries/raw",
        }, paths)


if __name__ == "__main__":
    unittest.main()
