#!/usr/bin/env python3
from __future__ import annotations

import hashlib
import importlib.util
import io
import json
from pathlib import Path
import re
import subprocess
import sys
import tempfile
import zipfile


ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "resolve-java-source-closure.py"
VERIFY_PATH = ROOT / "scripts" / "verify-reconstituted-framework.py"
ASSEMBLER_PATH = ROOT / "scripts" / "assemble-reconstituted-framework-modules.py"


def load_module(name: str, path: Path):
    spec = importlib.util.spec_from_file_location(name, path)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    sys.modules[name] = module
    spec.loader.exec_module(module)
    return module


def compile_classes(work: Path) -> dict[str, bytes]:
    source = work / "source"
    classes = work / "classes"
    source.mkdir()
    classes.mkdir()
    a = source / "A.java"
    b = source / "B.java"
    b.write_text(
        "package com.sunwayworld.framework; public class B { public static int value() { return 2; } }\n",
        encoding="utf-8",
    )
    a.write_text(
        "package com.sunwayworld.framework; public class A { public int value() { return B.value(); } }\n",
        encoding="utf-8",
    )
    subprocess.run(
        [
            "javac",
            "-source",
            "8",
            "-target",
            "8",
            "-d",
            str(classes),
            str(a),
            str(b),
        ],
        check=True,
        capture_output=True,
        text=True,
    )
    return {
        "A": (classes / "com" / "sunwayworld" / "framework" / "A.class").read_bytes(),
        "B": (classes / "com" / "sunwayworld" / "framework" / "B.class").read_bytes(),
    }


def build_run_jar(work: Path, classes: dict[str, bytes]) -> Path:
    private = io.BytesIO()
    with zipfile.ZipFile(private, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        archive.writestr("com/sunwayworld/framework/A.class", classes["A"])
        archive.writestr("com/sunwayworld/framework/B.class", classes["B"])
        archive.writestr(
            "templates/example.html",
            "<html><body>fixture</body></html>",
        )

    public = io.BytesIO()
    with zipfile.ZipFile(public, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        archive.writestr(
            "META-INF/maven/org.example/public-lib/pom.properties",
            "groupId=org.example\nartifactId=public-lib\nversion=1.0\n",
        )
        archive.writestr("org/example/Public.class", b"not-real-bytecode")

    web = io.BytesIO()
    with zipfile.ZipFile(web, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        archive.writestr("static/fixture.css", "body { color: black; }\n")

    vendor = io.BytesIO()
    with zipfile.ZipFile(vendor, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        archive.writestr(
            "com/sunwayworld/cloud/fixture/VendorComponent.class",
            b"not-real-bytecode",
        )
        archive.writestr("fixture/config.properties", "vendor=true\n")

    run_jar = work / "supplier-run.jar"
    with zipfile.ZipFile(run_jar, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        archive.writestr("BOOT-INF/classes/com/sunwayworld/emc/app/Main.class", classes["A"])
        archive.writestr("BOOT-INF/classes/application.yml", "fixture: true\n")
        archive.writestr(
            "BOOT-INF/lib/gikam-core-test.jar",
            private.getvalue(),
        )
        archive.writestr(
            "BOOT-INF/lib/public-lib-1.0.jar",
            public.getvalue(),
        )
        archive.writestr(
            "BOOT-INF/lib/gikam-core-web-14.1.5.jar",
            web.getvalue(),
        )
        archive.writestr(
            "BOOT-INF/lib/lims2-report-template-14.1.5.jar",
            vendor.getvalue(),
        )
        archive.writestr("org/springframework/boot/loader/Launcher.class", b"loader")
    return run_jar


def verify_tracked_pilot() -> None:
    module = ROOT / "reconstituted" / "pilot" / "owned-core-common"
    manifest = json.loads(
        (module / "module-provenance.json").read_text(encoding="utf-8")
    )
    assert manifest["schema"] == "supplier-reconstituted-module-v1"
    assert manifest["status"] == "pilot-compile-pass"
    assert manifest["source_file_count"] == 399
    assert manifest["source_decompilers"] == {
        "cfr": 351,
        "vineflower_fallback": 48,
    }
    assert manifest["transformed_source_count"] == 12
    assert manifest["compile"]["exit_code"] == 0
    assert manifest["compile"]["supplier_private_jars_on_classpath"] is False

    source_root = module / "src" / "main" / "java"
    actual_files = sorted(source_root.rglob("*.java"))
    assert len(actual_files) == manifest["source_file_count"]
    tree = hashlib.sha256()
    for record in sorted(
        manifest["source_files"], key=lambda item: item["generated_source_path"]
    ):
        relative = Path(record["generated_source_path"])
        assert not relative.is_absolute()
        path = module / relative
        assert path.is_file()
        digest = hashlib.sha256(path.read_bytes()).hexdigest()
        assert digest == record["source_sha256"]
        tree.update(record["generated_source_path"].encode())
        tree.update(b"\0")
        tree.update(bytes.fromhex(digest))
    assert tree.hexdigest() == manifest["source_tree_sha256"]
    transform_manifest = ROOT / manifest["transformation_manifest"]
    assert (
        hashlib.sha256(transform_manifest.read_bytes()).hexdigest()
        == manifest["transformation_manifest_sha256"]
    )


def verify_tracked_full_closure_snapshot() -> None:
    inventory = json.loads(
        (ROOT / "docs" / "reconstitution" / "artifact-inventory-current.json").read_text(
            encoding="utf-8"
        )
    )
    artifact = inventory["artifact"]
    assert artifact["artifact_sha256"] == (
        "4a720797810fd2f8cca6e22604657028cb87830ca0e24bb55a5f42f040f6ee9f"
    )
    assert artifact["private_framework_jar_count"] == 16
    assert artifact["private_framework_distinct_class_count"] == 4876
    assert artifact["public_dependency_jar_count"] == 386

    closure = json.loads(
        (
            ROOT
            / "docs"
            / "reconstitution"
            / "project-source-closure-current.json"
        ).read_text(encoding="utf-8")
    )
    assert closure["status"] == "diagnostic-project-source-compile-pass"
    assert closure["canonical"] is False
    assert closure["strict_evidence"] is False
    assert closure["api_evidence"] is False
    assert closure["framework_closure"]["direct_project_import_count"] == 229
    assert closure["framework_closure"]["closure_class_count"] == 748
    assert closure["framework_closure"]["generated_source_file_count"] == 603
    assert closure["framework_closure"]["source_decompilers"] == {
        "CFR": 543,
        "Vineflower": 60,
    }
    assert closure["framework_closure"]["source_tree_sha256"] == (
        "6768199e89c25ac8cdb5f1863459080c22bd46842fe4bdcc10b2858a325e7c85"
    )
    assert closure["framework_closure"]["missing_external_framework_classes"] == []
    assert closure["project_compile"]["project_source_file_count"] == 3038
    assert closure["project_compile"]["exit_code"] == 0
    assert closure["project_compile"]["error_count"] == 0
    assert closure["project_compile"]["missing_symbol_queue"] == []
    assert closure["project_compile"]["source_only_framework_classpath"] is True
    assert closure["project_compile"]["supplier_private_jars_on_classpath"] is False


def verify_tracked_all_framework_snapshot() -> None:
    snapshot = json.loads(
        (
            ROOT
            / "docs"
            / "reconstitution"
            / "all-framework-source-closure-current.json"
        ).read_text(encoding="utf-8")
    )
    assert snapshot["schema"] == "supplier-all-framework-source-closure-current-v1"
    assert snapshot["status"] == "diagnostic-all-framework-source-compile-pass"
    assert snapshot["canonical"] is False
    assert snapshot["strict_evidence"] is False
    assert snapshot["api_evidence"] is False
    assert snapshot["artifact"]["sha256"] == (
        "4a720797810fd2f8cca6e22604657028cb87830ca0e24bb55a5f42f040f6ee9f"
    )
    recovery = snapshot["source_recovery"]
    assert recovery["closure_class_count"] == 4876
    assert recovery["generated_top_level_source_count"] == 4576
    assert recovery["source_decompilers"] == {"CFR": 4274, "Vineflower": 302}
    assert recovery["transform_rule_count"] == 45
    assert recovery["transformed_source_count"] == 39
    assert recovery["source_tree_sha256"] == (
        "7bfe1f1cf055e01ac8379d8a72c66be973dc32609f33611a40a190c3cf5c8b31"
    )
    assert (
        hashlib.sha256((ROOT / recovery["transform_manifest"]).read_bytes()).hexdigest()
        == recovery["transform_manifest_sha256"]
    )
    project = snapshot["project_compile"]
    assert project["project_source_file_count"] == 3038
    assert project["compile_target_source_file_count"] == 3033
    assert project["project_framework_overlay_count"] == 5
    assert project["exit_code"] == 0
    assert project["error_count"] == 0
    assert project["missing_symbol_queue"] == []
    assert project["source_only_framework_classpath"] is True
    assert project["supplier_private_jars_on_classpath"] is False
    assert snapshot["resources"]["web_resource_count"] == 5302
    assert snapshot["verification"]["status"] == "pass"


def verify_tracked_module_provenance_statuses() -> None:
    registry = json.loads(
        (
            ROOT
            / "docs"
            / "reconstitution"
            / "framework-module-registry.json"
        ).read_text(encoding="utf-8")
    )
    assert registry["module_count"] == len(registry["modules"])
    for summary in registry["modules"]:
        module_root = ROOT / "reconstituted" / "framework" / summary["module"]
        provenance = json.loads(
            (module_root / "module-provenance.json").read_text(encoding="utf-8")
        )
        assert len(provenance["source_files"]) == summary["source_file_count"]
        for record in provenance["source_files"]:
            assert record["compile_status"] == "pass", record["path"]
            assert record["source_jar"]
            assert record["source_class_path"]
            assert isinstance(record["transformations"], list)
            if record["source_jar"] == "tracked-override":
                assert record["decompiler"] == "tracked-override"
            else:
                assert record["class_sha256"]
                assert record["source_sha256_before_transform"]
                assert record["decompiler_version"]

        resources = json.loads(
            (module_root / "resource-provenance.json").read_text(encoding="utf-8")
        )
        assert len(resources["resources"]) == summary["resource_file_count"]
        for record in resources["resources"]:
            assert record["compile_status"] == "not-applicable-resource", record["path"]
            assert record["source_jar"]
            assert record["sha256"]


def verify_file_utils_source_contract() -> None:
    source = (
        ROOT
        / "project"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "utils"
        / "FileUtils.java"
    ).read_text(encoding="utf-8")
    for signature in (
        r"public\s+static\s+final\s+void\s+write\s*\(\s*Path\s+regularFilePath\s*,\s*String\s+content\s*\)",
        r"public\s+static\s+final\s+void\s+encode\s*\(\s*Path\s+srcPath\s*,\s*Path\s+destPath\s*,\s*Charset\s+targetCharset\s*\)",
        r"public\s+static\s+final\s+void\s+createFile\s*\(\s*Path\s+path\s*\)",
        r"public\s+static\s+File\s+getTempDir\s*\(\s*\)",
        r"public\s+static\s+String\s+getFileNameWithNewExtension\s*\(\s*String\s+fileName\s*,\s*String\s+newExtension\s*\)",
        r"public\s+static\s+boolean\s+isImage\s*\(\s*File\s+file\s*\)",
        r"public\s+static\s+boolean\s+isExtension\s*\(\s*File\s+file\s*,\s*String\s+extension\s*\)",
    ):
        assert re.search(signature, source), f"missing supplier-compatible signature: {signature}"
    assert '"jpg".equals(extension)' in source
    assert '"jpeg".equals(extension)' in source
    assert '"png".equals(extension)' in source
    assert '"bmp".equals(extension)' in source
    assert '"gif".equals(extension)' in source
    assert "extension.equalsIgnoreCase(getFileExtension(file).toLowerCase())" in source


def verify_lcdp_database_conditions_are_null_safe() -> None:
    condition_root = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-lcdp"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "cloud"
        / "module"
        / "lcdp"
        / "table"
        / "helper"
        / "impl"
    )
    for filename in ("LcdpOracleCondition.java", "LcdpPgsqlCondition.java"):
        source = (condition_root / filename).read_text(encoding="utf-8")
        assert "Boolean.TYPE" not in source
        assert 'getProperty("kingbase.enabled", Boolean.class)' in source
        assert "Boolean.TRUE.equals(kingbaseEnabled)" in source
        assert "activeProfiles != null" in source


def verify_mapper_scanner_covers_both_supplier_conventions() -> None:
    scanner_source = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "boot"
        / "GikamMapperScannerConfigurer.java"
    ).read_text(encoding="utf-8")
    assert "new AnnotationTypeFilter(GikamBean.class)" in scanner_source
    assert (
        'Pattern.compile("^com\\\\.sunwayworld\\\\..+\\\\.mapper\\\\..*Mapper$")'
        in scanner_source
    )
    configuration = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "boot"
        / "ApplicationConfiguration.java"
    ).read_text(encoding="utf-8")
    assert "new GikamMapperScannerConfigurer()" in configuration
    assert "setAnnotationClass(" not in configuration
    mvc_configuration = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "boot"
        / "ApplicationMvcConfiguration.java"
    ).read_text(encoding="utf-8")
    assert "extends WebMvcConfigurationSupport" in mvc_configuration
    assert "implements WebMvcConfigurer" not in mvc_configuration
    assert "public void configureContentNegotiation" in mvc_configuration
    security_configuration = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "boot"
        / "security"
        / "SunwaySecurityConfiguration.java"
    ).read_text(encoding="utf-8")
    assert (
        'Boolean.TRUE.equals(env.getProperty("sunway.security.headers.xss", '
        "Boolean.class))"
    ) in security_configuration
    prepared_listener = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "boot"
        / "listener"
        / "ApplicationPreparedListener.java"
    ).read_text(encoding="utf-8")
    assert "${sunway.cache.post-start.evict.name:}" in prepared_listener
    assert "implements ApplicationListener<ApplicationReadyEvent>" in prepared_listener
    assert "@Order(Ordered.HIGHEST_PRECEDENCE)" in prepared_listener
    exception_handler = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "exception"
        / "advice"
        / "GlobalExceptionHandler.java"
    ).read_text(encoding="utf-8")
    # The source-only runtime follows the approved supplier configuration so
    # diagnostic HTTP errors retain the same stacktrace envelope as 18081.
    assert "${sunway.exception.viewable:true}" in exception_handler
    assert "${sentry.enabled:false}" in exception_handler
    firewall = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "security"
        / "firewall"
        / "SecurityFirewallFilter.java"
    ).read_text(encoding="utf-8")
    assert "${sunway.security.access-host-list:}" in firewall
    i18n_helper = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "i18n"
        / "I18nHelper.java"
    ).read_text(encoding="utf-8")
    assert "@Lazy\n    @Autowired\n    public void setMessageSource" in i18n_helper
    reflections = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "utils"
        / "ReflectionUtils.java"
    ).read_text(encoding="utf-8")
    assert "return findMethod(clazz, name, new Class<?>[0]);" in reflections
    assert "return invokeMethod(method, target, new Object[0]);" in reflections
    generic_service = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "support"
        / "base"
        / "service"
        / "GenericService.java"
    ).read_text(encoding="utf-8")
    assert (
        "@Transactional(readOnly = true)\n"
        "   default List<T> selectList(MapperParameter parameter)"
    ) in generic_service
    assert generic_service.count(
        "@Transactional(readOnly = true)\n"
        "   default List<T> selectListByFilter("
    ) == 2
    assert (
        "@Transactional(readOnly = true)\n"
        "   default T selectByIdIfPresent(ID id)"
    ) in generic_service
    dynamic_class_loader = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "jdk"
        / "tools"
        / "DynamicClassLoader.java"
    ).read_text(encoding="utf-8")
    assert "protected Class<?> findClass(String name)" in dynamic_class_loader
    assert (
        "Thread.currentThread().setContextClassLoader(this);"
        in dynamic_class_loader
    )
    bean_context = (
        ROOT
        / "reconstituted"
        / "framework"
        / "owned-core-common"
        / "src"
        / "main"
        / "java"
        / "com"
        / "sunwayworld"
        / "framework"
        / "context"
        / "GikamBeanContext.java"
    ).read_text(encoding="utf-8")
    assert (
        "Thread.currentThread().setContextClassLoader("
        "DynamicClassLoader.getInstance());"
    ) in bean_context


def verify_deterministic_assembler_helpers() -> None:
    assembler = load_module("assemble_reconstituted_framework_modules", ASSEMBLER_PATH)
    with tempfile.TemporaryDirectory(prefix="supplier-assembler-test-") as temp_name:
        work = Path(temp_name)
        jars = work / "jars"
        jars.mkdir()
        source_jars = assembler.MODULE_SOURCE_JARS["owned-core-report"]
        for index, source_jar in enumerate(source_jars):
            with zipfile.ZipFile(jars / source_jar, "w") as archive:
                archive.writestr("META-INF/MANIFEST.MF", "fixture\n")
                archive.writestr("shared/config.txt", f"source-{index}\n")
                archive.writestr(f"source-{index}.txt", f"unique-{index}\n")
                if index == 0:
                    archive.writestr(
                        "third-party/sunway-app/linux/sunway-app.so",
                        b"forbidden-native-fixture",
                    )

        records, source_provenance, payloads, duplicates, excluded = (
            assembler.collect_module_resources(
                jars,
                "owned-core-report",
                source_jars,
            )
        )
        assert len(source_provenance) == len(source_jars)
        assert excluded == ["third-party/sunway-app/linux/sunway-app.so"]
        assert all(record["path"] != "META-INF/MANIFEST.MF" for record in records)
        assert len(duplicates) == 1
        duplicate = duplicates[0]
        assert duplicate["path"] == "shared/config.txt"
        assert duplicate["selected_source_jar"] == source_jars[-1]
        assert duplicate["selected_sha256"] == hashlib.sha256(b"source-4\n").hexdigest()
        assert payloads["shared/config.txt"][1] == b"source-4\n"
        duplicate_records = [
            record for record in records if record["path"] == "shared/config.txt"
        ]
        assert len(duplicate_records) == len(source_jars)
        assert all(
            record["destination_sha256"] == duplicate["selected_sha256"]
            for record in duplicate_records
        )

        module_root = work / "module"
        java_root = module_root / "src/main/java/com/example"
        java_root.mkdir(parents=True)
        example = java_root / "Example.java"
        removed = java_root / "Removed.java"
        example.write_text("class Example { int value = 1; }\n", encoding="utf-8")
        removed.write_text("class Removed {}\n", encoding="utf-8")
        source_records = [
            {
                "path": "src/main/java/com/example/Example.java",
                "sha256": hashlib.sha256(example.read_bytes()).hexdigest(),
                "transformations": [],
            },
            {
                "path": "src/main/java/com/example/Removed.java",
                "sha256": hashlib.sha256(removed.read_bytes()).hexdigest(),
                "transformations": [],
            },
        ]
        override_root = work / "overrides"
        override_java = override_root / "fixture-module/src/main/java/com/example"
        override_java.mkdir(parents=True)
        replacement = override_java / "Example.java"
        replacement.write_text(
            "class Example { int value = 2; }\n",
            encoding="utf-8",
        )
        added_override = override_java / "AddedByOverride.java"
        added_override.write_text(
            "class AddedByOverride { int value = 3; }\n",
            encoding="utf-8",
        )
        (override_root / "fixture-module/removed-files.txt").write_text(
            "com/example/Removed.java\n",
            encoding="utf-8",
        )
        result = assembler.apply_source_overrides(
            module_root,
            "fixture-module",
            source_records,
            override_root,
        )
        assert example.read_text(encoding="utf-8") == replacement.read_text(encoding="utf-8")
        assert not removed.exists()
        assert len(result["overridden"]) == 2
        assert len(result["removed"]) == 1
        assert len(source_records) == 2
        records_by_path = {record["path"]: record for record in source_records}
        example_record = records_by_path["src/main/java/com/example/Example.java"]
        assert example_record["sha256"] == hashlib.sha256(example.read_bytes()).hexdigest()
        assert "overlay:src/main/java/com/example/Example.java" in example_record[
            "transformations"
        ]
        added_record = records_by_path[
            "src/main/java/com/example/AddedByOverride.java"
        ]
        assert added_record["source_jar"] == "tracked-override"
        assert added_record["sha256"] == hashlib.sha256(
            added_override.read_bytes()
        ).hexdigest()
        assert "source_sha256_before_overlay" not in added_record

        benign = module_root / "ThirdPartyType.java"
        forbidden = module_root / "SunwayLibraryExtractor.java"
        benign.write_text("enum ThirdPartyType { VALUE }\n", encoding="utf-8")
        forbidden.write_text(
            "class SunwayLibraryExtractor { public native void load(); }\n",
            encoding="utf-8",
        )
        report = assembler.scan_forbidden(
            "fixture-module",
            module_root,
            [benign, forbidden],
            [],
        )
        assert report["status"] == "open-native-removal"
        assert report["loader_reference_files"] == ["SunwayLibraryExtractor.java"]
        assert report["native_declaration_files"] == ["SunwayLibraryExtractor.java"]


def main() -> int:
    verify_file_utils_source_contract()
    verify_lcdp_database_conditions_are_null_safe()
    verify_mapper_scanner_covers_both_supplier_conventions()
    verify_tracked_full_closure_snapshot()
    verify_tracked_all_framework_snapshot()
    verify_tracked_module_provenance_statuses()
    verify_deterministic_assembler_helpers()
    module = load_module("resolve_java_source_closure", MODULE_PATH)
    verify_module = load_module("verify_reconstituted_framework", VERIFY_PATH)
    with tempfile.TemporaryDirectory(prefix="supplier-artifact-test-") as temp_name:
        work = Path(temp_name)
        classes = compile_classes(work)
        run_jar_path = build_run_jar(work, classes)
        project_source = work / "project-src"
        main_source = (
            project_source
            / "com"
            / "sunwayworld"
            / "emc"
            / "app"
            / "Main.java"
        )
        main_source.parent.mkdir(parents=True)
        main_source.write_text(
            "package com.sunwayworld.emc.app;\n"
            "import com.sunwayworld.framework.A;\n"
            "import com.sunwayworld.emc.local.NotFramework;\n"
            "public class Main { A a; }\n",
            encoding="utf-8",
        )

        with module.RunJar(run_jar_path) as run_jar:
            manifest = run_jar.manifest(project_source)
            assert manifest["artifact_sha256"] == hashlib.sha256(
                run_jar_path.read_bytes()
            ).hexdigest()
            assert manifest["nested_jar_count"] == 4
            assert manifest["private_framework_jar_count"] == 3
            assert manifest["public_dependency_jar_count"] == 1
            assert manifest["application_top_level_class_count"] == 1
            assert manifest["application_top_level_source_present"] == 1
            assert manifest["private_framework_distinct_class_count"] == 3
            assert (
                run_jar.nested_jar_info[
                    "BOOT-INF/lib/lims2-report-template-14.1.5.jar"
                ].private_framework
                is True
            )

            entries = run_jar.private_class_entries(("gikam-core-test.jar",))
            closure, edges, missing = module.resolve_closure(
                run_jar,
                entries,
                ["com.sunwayworld.framework.A"],
            )
            assert closure == {
                "com.sunwayworld.framework.A",
                "com.sunwayworld.framework.B",
            }, closure
            assert edges["com.sunwayworld.framework.A"] == [
                "com.sunwayworld.framework.B"
            ]
            assert missing == []

        imports = module.project_imports([main_source])
        assert imports == {"com.sunwayworld.framework.A": [str(main_source)]}

        transform_manifest = module.load_transform_manifest(
            module.DEFAULT_TRANSFORM_MANIFEST
        )
        assert len(transform_manifest["rules"]) == 45
        transform_source = (
            work
            / "transform-src"
            / "com"
            / "sunwayworld"
            / "framework"
            / "converter"
            / "number"
            / "NumberToNumberConverterFactory.java"
        )
        transform_source.parent.mkdir(parents=True)
        transform_source.write_text(
            "\n".join(
                f"return (T)source.{value}Value();"
                for value in ("byte", "short", "int", "long", "float", "double")
            ),
            encoding="utf-8",
        )
        transformed, applied = module.apply_transform_rules(
            transform_source,
            work / "transform-src",
            transform_manifest,
            "Vineflower",
        )
        assert applied == [
            "number-converter-primitive-cast",
            "normalization:generated-source",
        ]
        assert transformed.count("return (T)(Object)source.") == 6

        output = work / "output"
        output.mkdir()
        (output / "artifact-manifest.json").write_text(
            json.dumps(manifest),
            encoding="utf-8",
        )
        (output / "source-closure.json").write_text(
            json.dumps(
                {
                    "run_jar_sha256": manifest["artifact_sha256"],
                    "gate_status": "pilot-compile-failed",
                    "closure_class_count": 0,
                    "source_file_count": 0,
                    "source_tree_sha256": hashlib.sha256().hexdigest(),
                    "web_resource_extracted_count": 0,
                    "provenance": [],
                    "missing_external_framework_classes": [],
                    "compile": {
                        "java_version": "8",
                        "source_only_framework_classpath": True,
                        "supplier_private_jars_on_classpath": False,
                        "exit_code": 1,
                        "error_count": 1,
                    },
                }
            ),
            encoding="utf-8",
        )
        (output / "javac.log").write_text(
            "fixture error without private JAR path\n",
            encoding="utf-8",
        )
        verification = verify_module.verify(run_jar_path, output)
        assert verification["status"] == "pass", verification

        manifest["artifact_sha256"] = "0" * 64
        (output / "artifact-manifest.json").write_text(
            json.dumps(manifest),
            encoding="utf-8",
        )
        failed = verify_module.verify(run_jar_path, output)
        assert failed["status"] == "fail"
        assert "run JAR SHA-256 mismatch" in failed["errors"]

        target = project_source / "com" / "sunwayworld" / "emc" / "app" / "Target.java"
        target.write_text(
            "package com.sunwayworld.emc.app;\n"
            "import com.sunwayworld.framework.A;\n"
            "public class Target { A value; }\n",
            encoding="utf-8",
        )
        pilot_output = work / "pilot-output"
        original_pilot_jars = module.PILOT_PRIVATE_JARS
        module.PILOT_PRIVATE_JARS = ("gikam-core-test.jar",)
        try:
            result = module.main(
                [
                    "pilot",
                    "--run-jar",
                    str(run_jar_path),
                    "--target-source",
                    str(target),
                    "--output-dir",
                    str(pilot_output),
                    "--extract-web-resources",
                    "--java8",
                    str(module.DEFAULT_JDK / "bin" / "java"),
                    "--javac",
                    str(module.DEFAULT_JDK / "bin" / "javac"),
                    "--cfr-jar",
                    str(module.DEFAULT_CFR),
                ]
            )
        finally:
            module.PILOT_PRIVATE_JARS = original_pilot_jars
        assert result == 0
        pilot_report = json.loads(
            (pilot_output / "source-closure.json").read_text(encoding="utf-8")
        )
        assert pilot_report["gate_status"] == "pilot-compile-pass"
        assert pilot_report["compile"]["error_count"] == 0
        assert pilot_report["web_resource_extracted_count"] == 1
        web_manifest = json.loads(
            (pilot_output / "web-resources.json").read_text(encoding="utf-8")
        )
        assert web_manifest["resource_count"] == 1
        assert web_manifest["resources"][0]["path"] == "static/fixture.css"
        assert (pilot_output / "resources" / "static" / "fixture.css").is_file()

    verify_tracked_pilot()
    print("supplier artifact/source reconstitution: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
