#!/usr/bin/env python3
import importlib.util
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location("select_affected", ROOT / "scripts/select-affected-runners.py")
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

equipment = module.select(["owned-equipment/src/main/java/com/example/Equipment.java"])
assert not equipment["fast_gate_only"]
assert {item["execution_group"] for item in equipment["execution_shards"]} == {
    "attachments", "consumables", "equipment", "results", "role-menu"
}
assert len(equipment["execution_shards"]) == 15
assert all(item["cases"] for item in equipment["execution_shards"])

metadata = module.select(["evidence/vertical-slice-matrix.tsv"])
assert metadata["fast_gate_only"] and metadata["fast_gate_paths"] == ["evidence/vertical-slice-matrix.tsv"]
assert metadata["execution_shards"] == []

print("affected runner selector: PASS (source scope selection and metadata fast gate)")
