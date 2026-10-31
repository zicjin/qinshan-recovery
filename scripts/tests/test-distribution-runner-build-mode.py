#!/usr/bin/env python3
"""Guard the boundary between module and packaged distribution runners.

Distribution mode must let the complete distribution builder own all module
compilation.  This is a static gate so a future runner change cannot silently
reintroduce a second round of personnel/equipment/consumables/Web builds.
"""
import ast
from pathlib import Path


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

    package_test = (ROOT / "owned-distribution/tests/test-package.py").read_text(encoding="utf-8")
    assert "OWNED_DISTRIBUTION_REUSE" not in package_test
    builder = (ROOT / "scripts/build-owned-distribution.sh").read_text(encoding="utf-8")
    assert '"$ROOT/scripts/build-owned-framework.sh"' in builder
    assert 'OWNED_SKIP_DEPENDENCY_BUILD=1' in builder
    assert 'hash_source_inputs()' in builder
    assert 'find owned-framework/src owned-personnel/src' in builder
    assert 'PROJECT_DIRTY=1' in builder
    assert '&& [ "$PROJECT_DIRTY" = 0 ]' in builder
    for relative in (
        "scripts/build-owned-personnel.sh", "scripts/build-owned-consumables.sh",
        "scripts/build-owned-attachments.sh", "scripts/build-owned-report.sh",
        "scripts/build-owned-scheduler.sh",
    ):
        assert "OWNED_SKIP_DEPENDENCY_BUILD" in (ROOT / relative).read_text(encoding="utf-8"), relative
    print("distribution runner build mode: PASS (packaged build owns module compilation)")


if __name__ == "__main__":
    main()
