#!/usr/bin/env python3
"""Guard the boundary between module and packaged distribution runners.

Distribution mode must let the complete distribution builder own all module
compilation.  This is a static gate so a future runner change cannot silently
reintroduce a second round of personnel/equipment/consumables/Web builds.
"""
import ast
import importlib.util
from pathlib import Path
import shlex
import tempfile


ROOT = Path(__file__).resolve().parents[2]


def function_source(path, class_name, function_name):
    tree = ast.parse(path.read_text(encoding="utf-8"), filename=str(path))
    for node in ast.walk(tree):
        if isinstance(node, ast.ClassDef) and node.name == class_name:
            for child in node.body:
                if isinstance(child, ast.FunctionDef) and child.name == function_name:
                    return ast.get_source_segment(path.read_text(encoding="utf-8"), child)
    raise AssertionError("missing %s.%s in %s" % (class_name, function_name, path))


def main():
    equipment = ROOT / "owned-distribution/tests/run-equipment-browser.py"
    distribution_build = function_source(equipment, "DistributionReplay", "build")
    assert "super().build(compile_modules=False)" in distribution_build
    assert "build-owned-personnel.sh" not in distribution_build
    assert "build-owned-equipment.sh" not in distribution_build
    assert "build-owned-consumables.sh" not in distribution_build
    assert "build-owned-distribution.sh" in distribution_build
    assert "cwd=self.temporary" in function_source(equipment, "DistributionReplay", "start")

    for relative in (
        "owned-web/src/test/browser/run-browser.py",
        "owned-web/src/test/browser/run-inventory-browser.py",
        "owned-web/src/test/browser/run-attachment-browser.py",
    ):
        source = (ROOT / relative).read_text(encoding="utf-8")
        assert "if not args.distribution:" in source, relative
        assert "OWNED_DISTRIBUTION_REUSE='1'" in source, relative
        assert "cwd=temporary" in source, relative

    package_path = ROOT / "owned-distribution/tests/test-package.py"
    package_test = package_path.read_text(encoding="utf-8")
    assert "verify_distribution(root, environment)" in package_test
    # Exercise the existing snapshot boundary with tiny inputs. An inherited
    # reuse setting must not redirect clean package validation to shared caches.
    spec = importlib.util.spec_from_file_location("package_gate", package_path)
    assert spec and spec.loader
    package_gate = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(package_gate)
    with tempfile.TemporaryDirectory(prefix="distribution-runner-mode-") as temporary:
        source = Path(temporary) / "source"
        snapshot = Path(temporary) / "snapshot"
        for name in ("project", "reconstituted", "scripts", "docs", "build-support",
                     "api-fixtures/schema", "deployment/oracle", "deployment/report-engine",
                     "owned-distribution"):
            directory = source / name
            directory.mkdir(parents=True)
            (directory / "input.txt").write_text("fixture input\n")
        (source / "project/target").mkdir()
        (source / "project/target/stale.txt").write_text("shared output\n")
        environment = package_gate.prepare_build_snapshot(source, snapshot, {"OWNED_DISTRIBUTION_REUSE": "1"})
        assert environment["OWNED_DISTRIBUTION_REUSE"] == "0"
        assert environment["LIMS_PROJECT_ROOT"] == str(snapshot / "project")
        assert (snapshot / "deployment/oracle/input.txt").is_file()
        assert not (snapshot / "project/target").exists()
    builder = (ROOT / "scripts/build-owned-distribution.sh").read_text(encoding="utf-8")
    assert 'bash "$ROOT/scripts/build-reconstituted-framework.sh"' in builder
    assert 'python3 "$ROOT/scripts/build-source-only-application.py"' in builder
    assert 'source_hash()' in builder
    # Check membership of the declared cache inputs without depending on shell
    # line wrapping or the order of find arguments.
    source_hash = builder.split('source_hash()', 1)[1].split('BUILD_KEY=', 1)[0]
    inputs = set(shlex.split(source_hash))
    for required in ('project/src', 'reconstituted/framework', 'reconstituted/overrides',
                     'owned-distribution/package.py', 'deployment/oracle',
                     'scripts/verify-core-schema-package.py',
                     'scripts/verify-combined-oracle-installation.py',
                     'owned-distribution/installation-README.md'):
        assert required in inputs, 'missing builder cache input: ' + required
    assert 'OWNED_PUBLIC_DEPENDENCY_DIR="$PUBLIC_DEPS"' in builder
    assert 'source-only-reconstituted' in builder
    for duplicate in ('build-owned-framework.sh', 'build-owned-personnel.sh',
                      'build-owned-equipment.sh', 'build-owned-consumables.sh'):
        assert duplicate not in builder, 'duplicate module build: ' + duplicate
    print("distribution runner build mode: PASS (packaged build owns module compilation)")


if __name__ == "__main__":
    main()
