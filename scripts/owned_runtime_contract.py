#!/usr/bin/env python3
"""Validate debug and clone-ready runtime manifests for API runners."""
from __future__ import annotations

import json
from pathlib import Path
from typing import Any, Dict, Mapping
from urllib.parse import urlsplit
import sys
sys.path.insert(0, str(Path(__file__).resolve().parent))
from source_binding import IMPLEMENTATION_SCOPE, DISTRIBUTION_INPUT_SCOPE, capture_binding
import re


class RuntimeContractError(ValueError):
    pass


LOOPBACK_HOSTS = {"127.0.0.1", "localhost", "::1", "[::1]"}


def load_report(path: Path) -> Dict[str, Any]:
    if not path.is_file():
        raise RuntimeContractError("runtime report does not exist: " + str(path))
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as error:
        raise RuntimeContractError("invalid runtime report: " + str(error)) from error
    if not isinstance(value, dict):
        raise RuntimeContractError("runtime report must be a JSON object")
    return value


def _require(report: Mapping[str, Any], key: str, expected: Any = None) -> None:
    if key not in report:
        raise RuntimeContractError("runtime report missing " + key)
    if expected is not None and report.get(key) != expected:
        raise RuntimeContractError("runtime report %s must be %r" % (key, expected))


def _loopback_url(report: Mapping[str, Any]) -> None:
    value = report.get("loopback_url")
    if not isinstance(value, str):
        raise RuntimeContractError("runtime report lacks loopback_url")
    parsed = urlsplit(value)
    if parsed.scheme != "http" or parsed.hostname not in LOOPBACK_HOSTS or parsed.port is None:
        raise RuntimeContractError("runtime report loopback_url must be an explicit loopback HTTP URL")


def _implementation_binding(report: Mapping[str, Any], mode: str, *, required: bool = False) -> Dict[str, Any]:
    if mode != "strict":
        return {}
    # Preserve validation compatibility for pre-migration unit fixtures and
    # historical diagnostic manifests.  New runtime manifests emitted by the
    # scripts always carry the complete binding and are checked below.
    if not required and not any(key in report for key in ("implementation_binding", "implementation_tree_digest", "implementation_scope")):
        return {}
    for key in ("repository_head_at_capture", "implementation_binding",
                "implementation_tree_digest", "implementation_scope", "implementation_dirty"):
        _require(report, key)
    if not isinstance(report.get("repository_head_at_capture"), str) or not report["repository_head_at_capture"]:
        raise RuntimeContractError("runtime implementation binding lacks repository_head_at_capture")
    if not isinstance(report.get("implementation_binding"), str) or not report["implementation_binding"].startswith("impl-"):
        raise RuntimeContractError("runtime implementation_binding is invalid")
    if not isinstance(report.get("implementation_tree_digest"), str) or len(report["implementation_tree_digest"]) != 64:
        raise RuntimeContractError("runtime implementation_tree_digest is invalid")
    if not isinstance(report.get("implementation_scope"), list) or not report["implementation_scope"]:
        raise RuntimeContractError("runtime implementation_scope is missing")
    declared_scope = set(str(item) for item in report["implementation_scope"])
    if not set(IMPLEMENTATION_SCOPE).issubset(declared_scope) or not set(DISTRIBUTION_INPUT_SCOPE).issubset(declared_scope):
        raise RuntimeContractError("runtime implementation_scope omits canonical inputs")
    if report.get("implementation_dirty") is not False:
        raise RuntimeContractError("runtime implementation_dirty must be false")
    current = capture_binding()
    if report.get("implementation_binding") != current["binding_id"]:
        raise RuntimeContractError("runtime implementation_binding is stale")
    if report.get("implementation_tree_digest") != current["implementation_tree_digest"]:
        raise RuntimeContractError("runtime implementation_tree_digest is stale")
    if report.get("implementation_scope") and not set(current["implementation_scope"]).issubset(declared_scope):
        raise RuntimeContractError("runtime implementation_scope does not cover current inputs")
    if current["implementation_dirty"]:
        raise RuntimeContractError("current implementation scope is dirty")
    return {
        "implementation_binding": report["implementation_binding"],
        "implementation_tree_digest": report["implementation_tree_digest"],
        "implementation_scope": list(report["implementation_scope"]),
        "implementation_dirty": False,
        "repository_head_at_capture": report["repository_head_at_capture"],
    }


PREPARED_SCHEMA = "local-prepared-v1"
PREPARED_STATUS = "local-prepared-unverified"


def _prepared_local(report: Mapping[str, Any]) -> Dict[str, Any]:
    """Admit observed before state; lifecycle outcomes belong to finalization."""
    expected = {
        "runtime_manifest_schema": PREPARED_SCHEMA,
        "status": PREPARED_STATUS, "execution": PREPARED_STATUS,
        "lifecycle_phase": "prepared", "strict_evidence": False, "production_access": False,
        "existing_application_preserved": True, "runtime_profile": "local-wsl",
        "execution_location": "local-wsl-docker", "oracle_reused": True,
        "oracle_instance_id": "lims-local-runtime-oracle",
        "oracle_container": "lims-local-runtime-oracle",
        "redis_container": "lims-local-runtime-redis", "redis_reused": True,
        "baseline_schema": "LIMS_BASELINE_LOCAL", "runtime_schema": "LIMS_RUNTIME_LOCAL",
        "baseline_refreshed": False,
        "schema_copy_method": "supplied-runtime-before-content-restore-target",
    }
    for key, value in expected.items():
        if type(report.get(key)) is not type(value) or report.get(key) != value:
            raise RuntimeContractError("prepared runtime " + key + " mismatch")
    if report.get("source_dirty") is not False:
        raise RuntimeContractError("prepared runtime source_dirty must be false")
    for key in ("application_stopped", "lifecycle_verified", "cleanup"):
        if key in report:
            raise RuntimeContractError("prepared runtime cannot declare future cleanup: " + key)
    # Null is explicit pending state. Never accept a predicted final hash or a
    # successful restore/immutability/cleanliness outcome before execution.
    for key in ("runtime_schema_hash_after", "runtime_key_data_hash_after",
                "baseline_schema_immutable", "runtime_schema_restored", "runtime_dirty"):
        if key not in report or report[key] is not None:
            raise RuntimeContractError("prepared runtime future outcome must be pending: " + key)
    for key in ("baseline_schema_hash", "runtime_schema_hash_before",
                "baseline_key_data_hash", "runtime_key_data_hash_before"):
        if not isinstance(report.get(key), str) or re.fullmatch(r"[a-f0-9]{64}", report[key]) is None:
            raise RuntimeContractError("prepared runtime lacks valid " + key)
    model = report.get("route_execution_model")
    if not isinstance(model, Mapping) or any(model.get(key) is not False for key in
            ("boundary_process", "internal_http_bridge", "jni_replacement_process")):
        raise RuntimeContractError("prepared runtime cannot contain a boundary process, JNI process or HTTP bridge")
    binding = _implementation_binding(report, "strict", required=True)
    current = capture_binding()
    for key in ("application_binding", "harness_binding", "planning_binding"):
        if report.get(key) != current.get(key):
            raise RuntimeContractError("prepared runtime " + key + " is missing or stale")
    import local_schema_policy as policy
    try:
        descriptor, _ = policy.definition(report.get("schema_policy_id"))
        if report.get("schema_policy_sha256") != policy.digest(descriptor):
            raise policy.PolicyError("schema policy pin mismatch")
    except policy.PolicyError:
        raise RuntimeContractError("prepared runtime schema policy identity or provenance mismatch") from None
    try:
        from lifecycle_proof import validate_prepared
        verified = validate_prepared(report)
    except (ValueError, OSError, KeyError, TypeError, ImportError) as error:
        raise RuntimeContractError("prepared runtime before proof rejected") from error
    return {**{key: value for key, value in report.items() if not key.startswith("_")},
            **verified, **binding, "mode": "strict", "strict_evidence": False,
            "reuse": True, "cleanup_pending": True}


def validate_report(report: Mapping[str, Any], mode: str) -> Dict[str, Any]:
    if mode not in {"debug", "strict"}:
        raise RuntimeContractError("mode must be debug or strict")
    _require(report, "production_access", False)
    _loopback_url(report)
    if mode == "debug":
        if report.get("status") != "diagnostic-running":
            raise RuntimeContractError("debug runtime must be diagnostic-running")
        _require(report, "strict_evidence", False)
        for key in ("reused_oracle", "reused_redis"):
            _require(report, key)
        if report.get("supplier_port_preserved") != 18081:
            raise RuntimeContractError("debug runtime must preserve supplier port 18081")
        return {
            "mode": "debug",
            "strict_evidence": False,
            "runtime_identity": report.get("container", "debug-runtime"),
            "reuse": True,
            "source_commit": report.get("source_commit"),
        }

    if report.get("runtime_manifest_schema") == PREPARED_SCHEMA:
        return _prepared_local(report)
    if report.get("runtime_profile") == "local-wsl":
        raise RuntimeContractError("new strict local runtime requires an observed prepared manifest")
    if report.get("status") != "clone-ready-unverified":
        raise RuntimeContractError("strict runtime must be clone-ready-unverified before API replay")
    _require(report, "execution", "clone-ready-unverified")
    _require(report, "strict_evidence", False)
    _require(report, "existing_application_preserved", True)
    binding = _implementation_binding(report, mode)
    profile = report.get("runtime_profile", "sss2")
    if not isinstance(profile, str) or profile not in {"sss2", "local-wsl"}:
        raise RuntimeContractError("unknown runtime_profile")
    if profile == "local-wsl":
        _require(report, "oracle_reused", True)
    if report.get("oracle_reused") is True:
        oracle = "lims-local-runtime-oracle" if profile == "local-wsl" else "lims-owned-sss2-oracle"
        _require(report, "oracle_instance_id", oracle)
        _require(report, "oracle_container", oracle)
        profile_identity = {"runtime_profile": profile, "oracle_instance_id": oracle, "oracle_container": oracle}
        if profile == "local-wsl":
            _require(report, "baseline_schema", "LIMS_BASELINE_LOCAL")
            _require(report, "runtime_schema", "LIMS_RUNTIME_LOCAL")
            _require(report, "redis_container", "lims-local-runtime-redis")
            _require(report, "redis_reused", True)
            profile_identity.update(redis_container=report["redis_container"], redis_reused=True)
            if "schema_policy_id" in report or "schema_policy_sha256" in report:
                import local_schema_policy as policy
                try:
                    descriptor, _ = policy.definition(report.get("schema_policy_id"))
                    if report.get("schema_policy_sha256") != policy.digest(descriptor):
                        raise policy.PolicyError("schema policy pin mismatch")
                except policy.PolicyError:
                    raise RuntimeContractError("local schema policy identity or provenance mismatch") from None
                profile_identity.update(schema_policy_id=descriptor["id"],
                                        schema_policy_sha256=policy.digest(descriptor))

        _require(report, "baseline_schema")
        _require(report, "runtime_schema")
        for key in ("baseline_schema_hash", "runtime_schema_hash_before", "runtime_schema_hash_after",
                    "baseline_key_data_hash", "runtime_key_data_hash_before", "runtime_key_data_hash_after"):
            value = report.get(key)
            if not isinstance(value, str) or len(value) != 64:
                raise RuntimeContractError("strict persistent runtime lacks valid " + key)
        _require(report, "schema_copy_method")
        _require(report, "baseline_refreshed")
        if report.get("runtime_dirty") is not False or report.get("baseline_schema_immutable") is not True:
            raise RuntimeContractError("strict persistent runtime baseline/runtime state is not clean")
        model = report.get("route_execution_model")
        if not isinstance(model, dict):
            raise RuntimeContractError("strict runtime lacks route_execution_model")
        if model.get("boundary_process") is not False or model.get("internal_http_bridge") is not False:
            raise RuntimeContractError("strict runtime cannot contain a boundary process or internal HTTP bridge")
        identity = "%s:%s:%s:%s" % (report["oracle_instance_id"], report["baseline_schema"], report["runtime_schema"], report["baseline_schema_hash"])
        return {
            "mode": "strict",
            "strict_evidence": False,
            "runtime_identity": identity,
            "reuse": True,
            "oracle_reused": True,
            "baseline_schema": report["baseline_schema"],
            "runtime_schema": report["runtime_schema"],
            "baseline_schema_hash": report["baseline_schema_hash"],
            "runtime_schema_hash_before": report["runtime_schema_hash_before"],
            "runtime_schema_hash_after": report["runtime_schema_hash_after"],
            "baseline_key_data_hash": report["baseline_key_data_hash"],
            "runtime_key_data_hash_before": report["runtime_key_data_hash_before"],
            "runtime_key_data_hash_after": report["runtime_key_data_hash_after"],
            "schema_copy_method": report["schema_copy_method"],
            "baseline_refreshed": report["baseline_refreshed"],
            "baseline_schema_immutable": report["baseline_schema_immutable"],
            "runtime_schema_restored": report["runtime_schema_restored"],
            "runtime_dirty": report["runtime_dirty"],
            "source_commit": report.get("source_commit"),
            **profile_identity,
            **binding,
        }
    _require(report, "source_schema")
    _require(report, "target_schema")
    baseline = report.get("clone_baseline")
    if not isinstance(baseline, dict):
        raise RuntimeContractError("strict runtime lacks clone_baseline")
    source_hash = baseline.get("source_dump_sha256") or baseline.get("source_schema_metadata_sha256")
    if not isinstance(source_hash, str) or len(source_hash) != 64:
        raise RuntimeContractError("strict runtime clone_baseline lacks source dump/schema metadata hash")
    target_hash = baseline.get("target_schema_metadata_sha256")
    if not isinstance(target_hash, str) or len(target_hash) != 64:
        raise RuntimeContractError("strict runtime clone_baseline lacks valid target_schema_metadata_sha256")
    if baseline.get("source_read_only") is not True or baseline.get("target_isolated") is not True:
        raise RuntimeContractError("strict runtime clone baseline is not read-only/isolated")
    model = report.get("route_execution_model")
    if not isinstance(model, dict):
        raise RuntimeContractError("strict runtime lacks route_execution_model")
    if model.get("boundary_process") is not False or model.get("internal_http_bridge") is not False:
        raise RuntimeContractError("strict runtime cannot contain a boundary process or internal HTTP bridge")
    identity = "%s:%s:%s" % (report["source_schema"], report["target_schema"], target_hash)
    return {
        "mode": "strict",
        "strict_evidence": False,
        "runtime_identity": identity,
        "reuse": False,
        "clone_baseline": baseline,
        "baseline_schema": report.get("source_schema"),
        "runtime_schema": report.get("target_schema"),
        "baseline_schema_hash": baseline.get("source_dump_sha256") or baseline.get("source_schema_metadata_sha256"),
        "runtime_schema_hash_before": target_hash,
        "runtime_schema_hash_after": target_hash,
        "schema_copy_method": "clone-derived-schema",
        "baseline_refreshed": False,
        "baseline_schema_immutable": baseline.get("source_read_only") is True,
        "runtime_schema_restored": baseline.get("target_isolated") is True,
        "runtime_dirty": False,
        "source_commit": report.get("source_commit"),
        **binding,
    }


def validate_file(path: Path, mode: str) -> Dict[str, Any]:
    report = load_report(path)
    report["_manifest_path"] = str(path.absolute())
    return validate_report(report, mode)


if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--report", type=Path, required=True)
    parser.add_argument("--mode", choices=("debug", "strict"), required=True)
    args = parser.parse_args()
    result = validate_file(args.report, args.mode)
    print(json.dumps(result, ensure_ascii=False, sort_keys=True))
