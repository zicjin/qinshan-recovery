#!/usr/bin/env python3
from __future__ import annotations

import importlib.util
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import zipfile


ROOT = Path(__file__).resolve().parents[2]
BUILDER_PATH = ROOT / "scripts" / "build-source-only-application.py"


def load_builder():
    spec = importlib.util.spec_from_file_location("build_source_only_application", BUILDER_PATH)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


def verify_external_schema_contract(builder, root: Path) -> None:
    """Build and package real mapper resources without compiling Java."""
    resources = root / "contract-resources"
    mapper_paths = (
        "com/sunwayworld/emc/module/bp/project/project/mapper/conf/pf-project-oracle-mapper.xml",
        "com/sunwayworld/emc/module/mdm/contract/mapper/conf/emc-contract-oracle-mapper.xml",
    )
    for relative in mapper_paths:
        target = resources / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(ROOT / "project/src/main/java" / relative, target)
    contract = builder.write_external_schema_build_contract(resources)
    assert contract["schema"] == "external-schema-build-contract-v1"
    assert contract["mode"] == "original-db-link"
    assert contract["profile_sha256"] is None
    assert contract["manifest_sha256"] is None
    assert contract["renderer_sha256"] is None
    assert {record["path"] for record in contract["mappers"]} == set(mapper_paths)
    for record in contract["mappers"]:
        assert record["sha256"] == hashlib.sha256((resources / record["path"]).read_bytes()).hexdigest()
    try:
        builder.write_external_schema_build_contract(
            resources, manifest=ROOT / "api-fixtures/schema/external-schema-clone-manifest.json")
    except SystemExit as error:
        assert "requires an explicit profile" in str(error)
    else:
        raise AssertionError("manifest without profile was accepted")

    for profile_name in ("external-schema-runtime-profile.example.json",
                         "external-schema-runtime-profile-local-view.example.json"):
        profile = ROOT / "api-fixtures/schema" / profile_name
        builder.render_external_logical_mapper_overlay(resources, profile)
        contract = builder.write_external_schema_build_contract(resources, profile)
        assert contract["mode"] == json.loads(profile.read_text())["mode"]
        assert contract["profile"] == "api-fixtures/schema/" + profile_name
        assert contract["profile_sha256"] == hashlib.sha256(profile.read_bytes()).hexdigest()
        manifest = ROOT / "api-fixtures/schema/external-schema-clone-manifest.json"
        assert contract["manifest_sha256"] == hashlib.sha256(manifest.read_bytes()).hexdigest()
        assert contract["renderer_sha256"] == hashlib.sha256(
            (ROOT / "scripts/render-external-logical-mappers.py").read_bytes()).hexdigest()

    external_profile = root / "deployment-profile.json"
    external_manifest = root / "clone-manifest.json"
    shutil.copyfile(ROOT / "api-fixtures/schema/external-schema-runtime-profile-local-view.example.json",
                    external_profile)
    shutil.copyfile(ROOT / "api-fixtures/schema/external-schema-clone-manifest.json", external_manifest)
    contract = builder.write_external_schema_build_contract(resources, external_profile, external_manifest)
    assert contract["profile"] == "deployment-profile.json"
    assert contract["manifest"] == "clone-manifest.json"
    assert str(root) not in json.dumps(contract)

    embedded = resources / "META-INF/lims-external-schema.json"
    raw_contract = embedded.read_bytes()
    assert json.loads(raw_contract) == contract
    expected_properties = "mode=local-view\ncontract_sha256=" + hashlib.sha256(raw_contract).hexdigest() + "\n"
    assert (resources / "META-INF/lims-external-schema.properties").read_text() == expected_properties
    classes = root / "contract-classes"
    classes.mkdir()
    source_jar = root / "contract-source.jar"
    builder.build_application_jar(classes, resources, source_jar)
    dependencies = root / "contract-public-deps"
    dependencies.mkdir()
    environment = dict(os.environ, OWNED_PUBLIC_DEPENDENCY_DIR=str(dependencies),
                       OWNED_ADDITIONAL_DEPENDENCY_DIRS="",
                       OWNED_THIRD_PARTY_DEPENDENCY_PROVENANCE="",
                       OWNED_DISTRIBUTION_MODE="source-only-reconstituted")

    def package(jar, name, mode="source-only-reconstituted"):
        destination = root / name
        result = subprocess.run(
            [sys.executable, str(ROOT / "owned-distribution/package.py"), str(destination), str(jar)],
            env=dict(environment, OWNED_DISTRIBUTION_MODE=mode), capture_output=True, text=True)
        return result, destination

    result, distribution = package(source_jar, "contract-good")
    assert result.returncode == 0, result.stderr
    package_manifest = json.loads((distribution / "manifest.json").read_text())
    contract_sha = hashlib.sha256(raw_contract).hexdigest()
    assert package_manifest["external_schema"] == {**contract, "contract_sha256": contract_sha}
    properties = distribution / "provenance/external-schema.properties"
    assert properties.read_text() == "mode=local-view\ncontract_sha256=" + contract_sha + "\n"
    assert package_manifest["files"]["provenance/external-schema.properties"] == hashlib.sha256(properties.read_bytes()).hexdigest()

    def changed_jar(name, change):
        destination = root / name
        with zipfile.ZipFile(source_jar) as source, zipfile.ZipFile(destination, "w") as target:
            for entry in source.namelist():
                raw = change(entry, source.read(entry))
                if raw is not None:
                    target.writestr(entry, raw)
        return destination

    missing = changed_jar("contract-missing.jar", lambda path, raw:
                          None if path == "META-INF/lims-external-schema.json" else raw)
    result, _ = package(missing, "contract-missing")
    assert result.returncode != 0 and "external schema" in result.stderr.lower(), result.stderr
    result, _ = package(missing, "generic-no-contract", "merged-modules")
    assert result.returncode == 0, result.stderr
    tampered = changed_jar("contract-tampered.jar", lambda path, raw:
                           raw + b"\n<!-- changed -->\n" if path == mapper_paths[0] else raw)
    result, _ = package(tampered, "contract-tampered")
    assert result.returncode != 0 and "external schema" in result.stderr.lower(), result.stderr
    tampered_properties = changed_jar("contract-tampered-properties.jar", lambda path, raw:
                                      b"mode=original-db-link\ncontract_sha256=" + b"0" * 64 + b"\n"
                                      if path == "META-INF/lims-external-schema.properties" else raw)
    result, _ = package(tampered_properties, "contract-tampered-properties")
    assert result.returncode != 0 and "embedded properties differ" in result.stderr.lower(), result.stderr
    bad_mode = changed_jar("contract-invalid-mode.jar", lambda path, raw:
                          json.dumps({**contract, "mode": "guess"}).encode()
                          if path == "META-INF/lims-external-schema.json" else raw)
    result, _ = package(bad_mode, "contract-invalid-mode")
    assert result.returncode != 0 and "external schema" in result.stderr.lower(), result.stderr
    absolute_reference = changed_jar("contract-private-path.jar", lambda path, raw:
                                    json.dumps({**contract, "profile": "/private/runtime.json"}).encode()
                                    if path == "META-INF/lims-external-schema.json" else raw)
    result, _ = package(absolute_reference, "contract-private-path")
    assert result.returncode != 0 and "non-portable" in result.stderr.lower(), result.stderr


def main() -> int:
    builder = load_builder()
    with tempfile.TemporaryDirectory(prefix="source-only-application-test-") as temp_name:
        root = Path(temp_name)
        java_root = root / "project/src/main/java/com/example"
        resource_root = root / "project/src/main/resources"
        java_root.mkdir(parents=True)
        resource_root.mkdir(parents=True)
        source = java_root / "Example.java"
        source.write_text(
            "package com.example; public class Example {}\n",
            encoding="utf-8",
        )
        assert builder.source_fqn(source, root / "project/src/main/java") == "com.example.Example"

        framework = root / "framework/owned-core-common"
        framework.mkdir(parents=True)
        (framework / "module-provenance.json").write_text(
            json.dumps(
                {
                    "source_files": [
                        {
                            "source_class_path": (
                                "com/sunwayworld/framework/utils/FileUtils.class"
                            )
                        }
                    ]
                }
            ),
            encoding="utf-8",
        )
        assert builder.framework_source_fqns(root / "framework") == {
            "com.sunwayworld.framework.utils.FileUtils"
        }

        (resource_root / "static").mkdir()
        (resource_root / "static/app.js").write_text("window.ok = true;\n", encoding="utf-8")
        (resource_root / "lib").mkdir()
        (resource_root / "lib/private-sdk.jar").write_bytes(b"third-party-sdk")
        copied_root = root / "resources"
        records = builder.copy_resource_tree(resource_root, copied_root)
        assert [record["path"] for record in records] == ["static/app.js"]
        assert not (copied_root / "lib/private-sdk.jar").exists()

        frontend_overlay = root / "frontend-original"
        for relative in ("static/gikam/main/js/home.js", "static/gikam/main/js/workspace.js"):
            path = frontend_overlay / relative
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text("window.original = true;\n", encoding="utf-8")
        overlay_records = builder.copy_license_clean_frontend_overlay(copied_root, frontend_overlay)
        assert {record["path"] for record in overlay_records} == {
            "static/gikam/main/js/home.js",
            "static/gikam/main/js/workspace.js",
        }
        (frontend_overlay / "static/gikam/main/js/home.js").write_text(
            "fetch('/core/module/sys/licenses');\n",
            encoding="utf-8",
        )
        try:
            builder.copy_license_clean_frontend_overlay(copied_root, frontend_overlay)
        except SystemExit as error:
            assert "retains removed license route" in str(error)
        else:
            raise AssertionError("license route was accepted in frontend overlay")

        (frontend_overlay / "static/gikam/main/js/home.js").write_text(
            "window.original = true;\n",
            encoding="utf-8",
        )
        (frontend_overlay / "static/gikam/main/js/workspace.js").write_text(
            "return v.initEln(),n.next=3\n",
            encoding="utf-8",
        )
        try:
            builder.copy_license_clean_frontend_overlay(copied_root, frontend_overlay)
        except SystemExit as error:
            assert "missing ELN preload resource" in str(error)
        else:
            raise AssertionError("missing ELN preload was enabled in frontend overlay")

        mapper_resources = root / "mapper-resources"
        overlay_records = builder.render_external_logical_mapper_overlay(
            mapper_resources,
            ROOT / "api-fixtures/schema/external-schema-runtime-profile.example.json",
        )
        assert len(overlay_records) == 2
        rendered = next(mapper_resources.rglob("pf-project-oracle-mapper.xml")).read_text()
        assert "@LIMS_CZJKY_" not in rendered
        assert "LIMS_EXT_HT.T_CRM_CONTRACT" in rendered

        verify_external_schema_contract(builder, root)

    print("source-only application builder: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
