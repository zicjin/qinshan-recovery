#!/usr/bin/env python3
from __future__ import annotations

import importlib.util
import json
from pathlib import Path
import sys
import tempfile


ROOT = Path(__file__).resolve().parents[2]
BUILDER_PATH = ROOT / "scripts" / "build-source-only-application.py"


def load_builder():
    spec = importlib.util.spec_from_file_location("build_source_only_application", BUILDER_PATH)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


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

    print("source-only application builder: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
