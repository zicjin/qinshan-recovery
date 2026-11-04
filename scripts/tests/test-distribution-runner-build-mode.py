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
    assert 'bash "$ROOT/scripts/build-reconstituted-framework.sh"' in builder
    assert 'python3 "$ROOT/scripts/build-source-only-application.py"' in builder
    assert 'source_hash()' in builder
    assert 'find project/src reconstituted/overrides scripts' in builder
    assert 'OWNED_PUBLIC_DEPENDENCY_DIR="$PUBLIC_DEPS"' in builder
    assert 'source-only-reconstituted' in builder
    assert 'build-owned-framework.sh' not in builder
    print("distribution runner build mode: PASS (packaged build owns module compilation)")


if __name__ == "__main__":
    main()
