#!/usr/bin/env python3
"""Compare declared source-facing class contracts with the recorded inventory."""
import json
from pathlib import Path
import sys
import zipfile

root = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(root / "tools"))
from framework_contract_inventory import class_info

references = {row["name"]: row for row in map(json.loads, (root.parent / ".scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/classes.jsonl").open())}
artifact = Path(sys.argv[1]) if len(sys.argv) > 1 else root / "owned-framework/target/lims-owned-framework-1.0.0.jar"
# Visibility, static/final, bridge, varargs, abstract and synthetic flags matter
# to a source-facing method declaration. Native implementation is intentionally
# excluded because owned Java implementations may replace native methods.
method_flags = 0x0001 | 0x0004 | 0x0008 | 0x0010 | 0x0040 | 0x0080 | 0x0400 | 0x1000
class_flags = 0x0001 | 0x0010 | 0x0200 | 0x0400 | 0x2000 | 0x4000
count = 0
with zipfile.ZipFile(artifact) as archive:
    for name in sorted(archive.namelist()):
        if not name.endswith(".class"):
            continue
        actual = class_info(archive.read(name))
        if actual["name"].startswith("com/sunwayworld/owned/internal/"):
            # Project-owned implementation helpers are outside the supplier compatibility namespace.
            continue
        if actual["name"] == "com/sunwayworld/framework/context/ApplicationContextHelper":
            # This facade intentionally removes supplier license/MAC methods;
            # its replacement contract is exercised by ApplicationContextHelperContractTest.
            continue
        expected = references.get(actual["name"])
        if expected is None:
            raise SystemExit("owned class has no selected supplier contract: " + name)
        for field in ("super", "interfaces"):
            if actual[field] != expected[field]:
                raise SystemExit("class relationship differs: " + name + " " + field)
        if actual["access"] & class_flags != expected["access"] & class_flags:
            raise SystemExit("class declaration flags differ: " + name)
        def methods(record):
            return {(method["name"], method["descriptor"], method["access"] & method_flags)
                    for method in record["public_protected_methods"]}
        missing = methods(expected) - methods(actual)
        extra = methods(actual) - methods(expected)
        if missing or extra:
            raise SystemExit("method contract differs: " + name + " missing=" + repr(sorted(missing)) + " extra=" + repr(sorted(extra)))
        def fields(record):
            return {(item["name"], item["descriptor"], item["access"] & method_flags)
                    for item in record["fields"] if item["access"] & (0x0001 | 0x0004)}
        if fields(actual) != fields(expected):
            raise SystemExit("public/protected field contract differs: " + name)
        count += 1
print("owned declared public contracts: PASS (" + str(count) + " classes)")
