#!/usr/bin/env python3
"""Contract tests for the source-to-owned business API inventory."""
import tempfile
import unittest
import hashlib
import json
import subprocess
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools"))
from business_api_inventory import (extract_routes, build_inventory,
                                    build_owned_boundary_inventory,
                                    _owned_route_is_concrete,
                                    _owned_alias_routes)  # noqa: E402


class BusinessApiInventoryTest(unittest.TestCase):
    def test_explicit_bean_returned_mapped_controllers_remain_conditional_candidates(self):
        roots = [ROOT / name / "src/main/java" for name in
                 ("owned-consumables", "owned-scheduler")]
        rows = build_owned_boundary_inventory(roots, [])
        scheduler = [row for row in rows if row["path"].startswith("/secure/owned-scheduler")]
        self.assertEqual(5, len(scheduler))
        stock = next(row for row in rows if row["path"].endswith("/stocks/concents/queries"))
        for row in scheduler + [stock]:
            self.assertEqual("unverified", row["runtime_registration"])
            candidate = next(c for c in row["handler_candidates"]
                             if c["class"].endswith(("OwnedSchedulerIsolatedController", "OwnedMdmStockReadController")))
            self.assertTrue(candidate["bean_factory_sources"])
            self.assertIn("ConditionalOnMissingBean", candidate["conditions"])
        with tempfile.TemporaryDirectory() as raw:
            source = Path(raw) / "Unregistered.java"
            source.write_text('@RequestMapping("/unregistered")\n'
                              'public class Unregistered { @GetMapping("/one") String one(){return "x";} }')
            self.assertEqual([], build_owned_boundary_inventory([Path(raw)], []))

    def test_explicit_trace_is_not_unrestricted_any(self):
        rows = extract_routes('''
@RequestMapping("/files")
public class Files {
 @RequestMapping(method={RequestMethod.TRACE}) void insert() {}
 @RequestMapping(value="/headers", method={RequestMethod.HEAD, RequestMethod.OPTIONS}) void headers() {}
}
''', "Files.java")
        self.assertEqual({("TRACE", "/files"), ("HEAD", "/files/headers"),
                          ("OPTIONS", "/files/headers")},
                         {(row["method"], row["path"]) for row in rows})

    def test_cli_scans_compiler_main_roots_and_preserves_old_project_id(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            def write(path, text):
                path = root / path
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_text(text, encoding="utf-8")
                return path
            write("scripts/build-source-only-application.py", '''
owned_source_roots = [ROOT / name / "src/main/java"
                      for name in ("owned-demo",)]
''')
            write("docs/reconstitution/framework-module-registry.json", json.dumps({
                "modules": [{"module": "owned-core-demo"}]
            }))
            source = write("project/src/main/java/demo/ProjectResource.java", '''
package demo;
@RequestMapping("/project")
public interface ProjectResource {
 @GetMapping("/one") String one();
}
''')
            write("owned-demo/src/main/java/demo/MainController.java", '''
package demo;
@RestController
public class MainController {
 @GetMapping("/owned-main") String main() {return "main";}
 @GetMapping("/project/one") String alias() {return "one";}
}
''')
            write("owned-demo/src/main/java/demo/GenericOrphan.java", '''
package demo;
public interface GenericOrphan { @PutMapping("/activate") void activate(); }
''')
            write("owned-demo/src/test/java/demo/TestController.java", '''
package demo;
@RestController
public class TestController {
 @GetMapping("/test-only") String main() {return "test";}
}
''')
            base = "reconstituted/framework/owned-core-demo/src/main/java/"
            write(base + "demo/GenericTree.java", '''
package demo;
public interface GenericTree<T> {
 @PostMapping("/tree/{parentId}") Object tree();
}
''')
            write(base + "demo/Selector.java", '''
package demo;
public interface Selector<T> extends GenericTree<java.util.Map<String,java.util.List<T>>> {
 @GetMapping("/selectable") Object select();
}
''')
            write(base + "demo/test/FrameworkResource.java", '''
package demo.test;
import demo.Selector;
@RequestMapping("/framework")
public interface FrameworkResource extends Selector<java.util.Map<String,java.util.List<Long>>> {}
''')
            write(base + "demo/test/FrameworkResourceImpl.java", '''
package demo.test;
@RestController
@ConditionalOnProperty("enabled")
public class FrameworkResourceImpl implements FrameworkResource {}
''')
            write(base + "demo/UnimplementedResource.java", '''
package demo;
@RequestMapping("/no-bean")
public interface UnimplementedResource extends Selector<String> {}
''')
            write("project/src/main/java/demo/PageImpl.java", '''
package demo;
import demo.ParentPage;
@Controller
public class PageImpl implements ParentPage {
 @GetMapping("/detail") String detail() {return "detail";}
}
''')
            write(base + "demo/ParentPage.java", '''
package demo;
@RequestMapping("/page")
public interface ParentPage { String detail(); }
''')
            output = root / "inventory.json"
            result = subprocess.run([
                sys.executable, "-B", str(ROOT / "tools/business_api_inventory.py"),
                "--source-root", str(source.parents[1]), "--output", str(output),
            ], text=True, capture_output=True)
            self.assertEqual(0, result.returncode, result.stderr)
            payload = json.loads(output.read_text())
        project = payload["routes"]
        original = next(row for row in project if row["path"] == "/project/one")
        self.assertEqual("demo/ProjectResource.java", original["source"])
        self.assertEqual(hashlib.sha256(b"GET /project/one demo.ProjectResource").hexdigest()[:16], original["id"])
        self.assertIn(hashlib.sha256(b"owned-boundary GET /project/one").hexdigest()[:16],
                      original.get("source_route_ids", []))
        owned_alias = next(alias for alias in original["source_route_aliases"]
                           if alias["alias_kind"] == "owned-boundary")
        self.assertEqual(owned_alias["target_route_id"], original["id"])
        self.assertEqual((owned_alias["method"], owned_alias["path"]), ("GET", "/project/one"))
        self.assertEqual(owned_alias["provenance_status"], "source-row-retained")
        page = next(row for row in project if row["path"] == "/page/detail")
        wrong_id = hashlib.sha256(b"GET /detail demo.PageImpl").hexdigest()[:16]
        self.assertNotIn(wrong_id, page.get("source_route_ids", []))
        self.assertIn(wrong_id, {row["id"] for row in payload["removed_source_rows"]})
        removed = next(row for row in payload["removed_source_rows"] if row["path"] == "/activate")
        self.assertIn("rootless", removed["reason"])
        boundary = payload["boundary_routes"]
        self.assertEqual({("GET", "/owned-main"), ("GET", "/framework/selectable"),
                          ("POST", "/framework/tree/{parentId}")},
                         {(row["method"], row["path"]) for row in boundary})
        selected = next(row for row in boundary if row["path"] == "/framework/selectable")
        self.assertEqual("unverified", selected["runtime_registration"])
        self.assertEqual(["ConditionalOnProperty"], selected["handler_candidates"][0]["conditions"])

    def test_compiled_framework_interfaces_include_observed_inherited_routes(self):
        framework = ROOT / "reconstituted/framework"
        roots = [path for path in sorted(framework.glob("*/src/main/java"))]
        rows = build_owned_boundary_inventory(roots, [])
        keys = {(row["method"], row["path"]) for row in rows}
        expected = {
            ("GET", "/secure/core/module/mdm/orgs/queries/selectable"),
            ("POST", "/secure/core/module/mdm/orgs/queries/choosable"),
            ("GET", "/core/module/sys/role-permission-types/queries/selectable"),
            ("GET", "/secure/core/module/mdm/user-permission-types/queries/selectable"),
            ("ANY", "/secure/core/les/les-dses/page/edit-list"),
            ("ANY", "/secure/core/les/les-templates/page/edit-list"),
            ("ANY", "/secure/core/les/les-templates/page/audit-list"),
            ("ANY", "/secure/core/module/sys/audit/audit-logs/page/search-list"),
            ("POST", "/secure/core/module/mdm/orgs/tree/{parentId}"),
            ("POST", "/secure/core/module/mdm/orgs/pop-tree/{parentId}"),
            ("POST", "/secure/core/module/sys/tables/{table}/columns/queries/choosable"),
        }
        self.assertFalse(expected - keys, sorted(expected - keys))
        selected = [row for row in rows if (row["method"], row["path"]) in expected]
        self.assertTrue(all(row["behavior_status"] == "unknown" for row in selected))
        self.assertTrue(all(row["handler_candidates"] for row in selected))
        self.assertTrue(all(row["runtime_registration"] == "unverified" for row in selected))

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

    def test_terminal_mapping_array_keeps_path_variables(self):
        source = '''
@RequestMapping("/reports")
public class Reports {
 @PostMapping(value={"/{id}/action/generator"})
 void generate() {}
 @GetMapping(path={"/sources/{id}", "/sources/{id}/columns"})
 void source() {}
}
'''
        self.assertEqual([
            ("POST", "/reports/{id}/action/generator"),
            ("GET", "/reports/sources/{id}"),
            ("GET", "/reports/sources/{id}/columns"),
        ], [(r["method"], r["path"]) for r in extract_routes(source, "Reports.java")])

    def test_explicit_slash_child_differs_from_empty_child(self):
        source = '''
@RequestMapping("/documents")
public class Documents {
 @GetMapping(value={"/"}) void slash() {}
 @GetMapping(value={""}) void root() {}
}
'''
        self.assertEqual([
            ("GET", "/documents/"),
            ("GET", "/documents"),
        ], [(r["method"], r["path"]) for r in extract_routes(source, "Documents.java")])

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

    def test_inherited_page_routes_follow_transitive_controller_interfaces(self):
        with tempfile.TemporaryDirectory() as raw:
            source_root = Path(raw)
            (source_root / "BasePages.java").write_text('''
public interface BasePages extends GenericEditListPageController,
        GenericAuditListPageController, GenericSearchListPageController {}
''', encoding="utf-8")
            (source_root / "EquipmentController.java").write_text('''
@RequestMapping("/secure/equipment/page")
public interface EquipmentController extends BasePages {}
''', encoding="utf-8")
            (source_root / "EquipmentControllerImpl.java").write_text('''
public class EquipmentControllerImpl implements EquipmentController {}
''', encoding="utf-8")
            rows = build_inventory(source_root, set())
        self.assertEqual([
            ("ANY", "/secure/equipment/page/audit-list"),
            ("ANY", "/secure/equipment/page/edit-list"),
            ("ANY", "/secure/equipment/page/search-list"),
        ], [(row["method"], row["path"]) for row in rows])
        self.assertTrue(all(row["source"] == "EquipmentController.java" for row in rows))
        self.assertTrue(all(row["behavior_status"] == "unknown" for row in rows))
        self.assertTrue(all(row["annotation"].startswith("inherited.Generic") for row in rows))

    def test_explicit_page_override_is_not_duplicated(self):
        with tempfile.TemporaryDirectory() as raw:
            source_root = Path(raw)
            (source_root / "EquipmentController.java").write_text('''
@RequestMapping("/secure/equipment/page")
public interface EquipmentController extends GenericEditListPageController {
    @RequestMapping("/edit-list") String editListPage();
}
''', encoding="utf-8")
            rows = build_inventory(source_root, set())
        self.assertEqual(1, len(rows))
        self.assertEqual("RequestMapping", rows[0]["annotation"])

    def test_unmapped_child_does_not_repeat_parent_page_routes(self):
        with tempfile.TemporaryDirectory() as raw:
            source_root = Path(raw)
            (source_root / "EquipmentController.java").write_text('''
@RequestMapping("/secure/equipment/page")
public interface EquipmentController extends GenericEditListPageController {}
''', encoding="utf-8")
            (source_root / "CustomizedEquipmentController.java").write_text('''
public interface CustomizedEquipmentController extends EquipmentController {}
''', encoding="utf-8")
            rows = build_inventory(source_root, set())
        self.assertEqual(1, len(rows))
        self.assertEqual("EquipmentController.java", rows[0]["source"])

    def test_redeclared_page_base_merges_source_provenance(self):
        with tempfile.TemporaryDirectory() as raw:
            source_root = Path(raw)
            for name in ("EquipmentController", "CustomizedEquipmentController"):
                (source_root / (name + ".java")).write_text(
                    '@RequestMapping("/secure/equipment/page")\n'
                    'public interface ' + name + ' extends GenericEditListPageController {}\n',
                    encoding="utf-8",
                )
            rows = build_inventory(source_root, set())
        self.assertEqual(1, len(rows))
        self.assertEqual({"EquipmentController.java", "CustomizedEquipmentController.java"},
                         {row["source"] for row in rows[0]["inherited_page_sources"]})

    def test_page_inheritance_contract_matches_reconstructed_source(self):
        with tempfile.TemporaryDirectory() as raw:
            source_root = Path(raw)
            controllers = ROOT / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/controller"
            for controller in sorted(controllers.glob("Generic*PageController.java")):
                name = controller.stem
                (source_root / "DemoController.java").write_text(
                    '@RequestMapping("/demo/page")\n'
                    'public interface DemoController extends ' + name + ' {}\n',
                    encoding="utf-8",
                )
                expected = [(row["method"], "/demo/page" + row["path"].rstrip("/"))
                            for row in extract_routes(controller.read_text(), controller.name)]
                actual = [(row["method"], row["path"]) for row in build_inventory(source_root, set())]
                with self.subTest(controller=name):
                    self.assertTrue(expected)
                    self.assertEqual(sorted(expected), sorted(actual))

    def test_unbound_generic_pages_are_not_owned_boundary_routes(self):
        with tempfile.TemporaryDirectory() as raw:
            source_root = Path(raw)
            (source_root / "GenericEditListPageController.java").write_text('''
public interface GenericEditListPageController {
    @RequestMapping("/edit-list") String editListPage();
}
''', encoding="utf-8")
            (source_root / "GenericChoosePageController.java").write_text('''
public interface GenericChoosePageController {
    @RequestMapping("/choose-list") String chooseListPage();
    @RequestMapping("/simple-choose-list") default String simpleChooseListPage() { throw new RuntimeException(); }
}
''', encoding="utf-8")
            rows = build_owned_boundary_inventory([source_root], [])
        self.assertEqual([], rows)

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

    def test_test_only_owned_routes_are_not_application_boundary_routes(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            runtime = root / "owned/src/main/java/com/example/RuntimeController.java"
            runtime.parent.mkdir(parents=True)
            runtime.write_text(
                '@RestController\n'
                'public class RuntimeController { @GetMapping("/runtime-only") public String read(){return "ok";} }\n',
                encoding="utf-8",
            )
            test = root / "owned/src/test/java/com/example/TestOnlyController.java"
            test.parent.mkdir(parents=True)
            test.write_text(
                '@RestController\n'
                'public class TestOnlyController { @GetMapping("/test-only") public String read(){return "ok";} }\n',
                encoding="utf-8",
            )
            rows = build_owned_boundary_inventory([root / "owned/src"], [])
        self.assertEqual([("/runtime-only", "GET")], [(row["path"], row["method"]) for row in rows])

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
