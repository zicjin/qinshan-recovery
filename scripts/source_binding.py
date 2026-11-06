#!/usr/bin/env python3
"""Stable dependency-surface bindings shared by runners and evidence tools.

Repository HEAD is provenance only. Behaviour evidence is valid when its
actual dependency surfaces still match. Documentation, registry and archive
changes are intentionally outside the behaviour surfaces and therefore do not
force HTTP replay.
"""
from __future__ import annotations

import hashlib
import json
import os
from pathlib import Path
import subprocess
from typing import Any, Iterable, Mapping, Optional, Sequence


SCRIPT_ROOT = Path(__file__).resolve().parents[1]
BINDING_SCHEMA_VERSION = 2
STIMULUS_CONTRACT_VERSION = "3"
NORMALIZER_VERSION = "2"
PLANNING_SCHEMA_VERSION = "2"
GOVERNANCE_SCHEMA_VERSION = "2"
RUNTIME_CONTRACT_VERSION = "persistent-oracle-v3"

BINDING_SURFACES = (
    "application",
    "harness",
    "runtime_contract",
    "planning",
    "governance",
)
REPLAY_SURFACES = (
    "application",
    "harness",
    "runtime_contract",
    "planning",
)


def _git_root(path: Path) -> Optional[Path]:
    result = subprocess.run(
        ["git", "-C", str(path), "rev-parse", "--show-toplevel"],
        cwd=path, text=True, capture_output=True, check=False,
    )
    if result.returncode:
        return None
    value = result.stdout.strip()
    return Path(value).resolve() if value else None


def discover_repo_root() -> Path:
    """Find the standalone repository or the historical parent checkout."""
    if (SCRIPT_ROOT / ".git").exists():
        return SCRIPT_ROOT.resolve()
    return _git_root(SCRIPT_ROOT) or SCRIPT_ROOT.resolve().parent


REPO_ROOT = discover_repo_root()
FEATURE_SLUG = "sunway-supplier-exit-api-first"


def is_standalone_root(repo_root: Path = REPO_ROOT) -> bool:
    root = Path(repo_root).resolve()
    return (
        (root / ".git").exists()
        and (root / "project").is_dir()
        and (root / "owned-framework").is_dir()
        and not (root / "lims-recovery").is_dir()
    )


def feature_root(repo_root: Path = REPO_ROOT,
                 feature_slug: str = FEATURE_SLUG) -> Path:
    """Return the planning/diagnostic tree without coupling source to it."""
    override = os.environ.get("SUNWAY_FEATURE_ROOT")
    if override:
        return Path(override).expanduser().resolve()

    root = Path(repo_root).resolve()
    if is_standalone_root(root):
        parent_candidate = root.parent / ".scratch" / feature_slug
        if parent_candidate.exists():
            return parent_candidate.resolve()
        return (root / ".scratch" / feature_slug).resolve()
    return (root / ".scratch" / feature_slug).resolve()


def workspace_root(repo_root: Path = REPO_ROOT) -> Path:
    root = Path(repo_root).resolve()
    if is_standalone_root(root):
        return feature_root(root).parent.parent
    return root


def resolve_workspace_path(value: str | Path,
                           repo_root: Path = REPO_ROOT) -> Path:
    """Resolve legacy parent-workspace paths in standalone mode."""
    path = Path(value)
    if path.is_absolute():
        return path
    raw = path.as_posix()
    root = Path(repo_root).resolve()
    if is_standalone_root(root) and raw == "lims-recovery":
        return root
    if is_standalone_root(root) and raw.startswith("lims-recovery/"):
        return root / raw.removeprefix("lims-recovery/")
    if raw == ".scratch" or raw.startswith(".scratch/"):
        return workspace_root(root) / raw
    return root / raw


def project_root(repo_root: Path = REPO_ROOT) -> Path:
    root = Path(repo_root).resolve()
    return root / "project" if is_standalone_root(root) else root / "lims-recovery" / "project"


# Application inputs determine deployed behaviour and build output.
_APPLICATION_SUFFIXES = (
    "project/src/main/java",
    "project/src/main/resources",
    "owned-framework/src",
    "owned-personnel/src",
    "owned-equipment/src",
    "owned-consumables/src",
    "owned-attachments/src",
    "owned-report/src",
    "owned-scheduler/src",
    "owned-web/src",
    "reconstituted",
    "docs/reconstitution/decompiler-transforms.json",
    "project/pom.xml",
    "owned-framework/pom.xml",
    "owned-personnel/pom.xml",
    "owned-equipment/pom.xml",
    "owned-consumables/pom.xml",
    "owned-attachments/pom.xml",
    "owned-report/pom.xml",
    "owned-scheduler/pom.xml",
    "owned-web/pom.xml",
    "owned-distribution/package.py",
    "owned-distribution/start.sh",
    "owned-distribution/example",
    "owned-web/build-owned-web.sh",
    "scripts/build-owned-distribution.sh",
    "scripts/build-owned-framework.sh",
    "scripts/build-owned-personnel.sh",
    "scripts/build-owned-equipment.sh",
    "scripts/build-owned-consumables.sh",
    "scripts/build-owned-attachments.sh",
    "scripts/build-owned-report.sh",
    "scripts/build-owned-scheduler.sh",
    "scripts/build-reconstituted-framework.sh",
    "scripts/build-source-only-application.py",
    "scripts/prepare-reconstituted-maven-repo.py",
    "scripts/record-reconstituted-framework-build.py",
    "scripts/verify-reconstituted-framework-modules.py",
    "scripts/verify-external-artifacts.sh",
    "build-support/external-artifacts.sha256",
    "build-support/t01-manifest.sha256",
    "build-support/t02-deps-manifest.sha256",
    "build-support/t02-build-plugin-seed.sha256",
    "build/oracle-free-source-manifest.json",
)

# Harness inputs construct stimuli, execute fixtures, normalize responses,
# classify outcomes and manage the test runtime lifecycle.
_HARNESS_SUFFIXES = (
    "scripts/source_binding.py",
    "scripts/evidence_batch.py",
    "scripts/evidence_fingerprint.py",
    "scripts/evidence_output.py",
    "scripts/spring_api_contract_runner.py",
    "scripts/fast_differential_api_runner.py",
    "scripts/run-fast-differential-jobs.py",
    "scripts/api_wave_orchestrator.py",
    "scripts/owned_runtime_contract.py",
    "scripts/api_fixture.py",
    "scripts/remote-sql-fixture-executor.py",
    "scripts/start-strict-sss2-api-runtime.sh",
    "scripts/start-reused-sss2-api-runtime.sh",
    "scripts/cleanup-strict-sss2-api-runtime.sh",
    "scripts/refresh-owned-sss2-app.sh",
    "scripts/rollback-owned-sss2-app.sh",
    "scripts/owned_readiness.py",
    "scripts/observe-sss2-clone-runtime.sh",
    "scripts/replay-vertical-slice.py",
    "scripts/app-ctl.sh",
    "scripts/verify-harness-semantics.py",
    "scripts/tests/fixtures/harness-golden-transcript-corpus.jsonl",
    "scripts/tests/fixtures/harness-golden-transcript-expected.json",
)

# Planning inputs define route inventory, alias mapping and replay grouping.
_PLANNING_SUFFIXES = (
    "tools/business_api_inventory.py",
    "tools/api_route_ledger.py",
    "tools/api_route_execution_plan.py",
    "tools/api_contract_skeleton.py",
    "tools/fast_contract_skeleton.py",
    "tools/framework_contract_inventory.py",
    "tools/source_inventory.py",
    "tools/vertical_slices/matrix.py",
    "tools/function_catalog/build_candidate_closure.py",
    "scripts/generate-fast-strict-contracts.py",
    "scripts/select-affected-runners.py",
)

# Governance inputs recompute bookkeeping views. A change here may require a
# registry/closure rebuild, but never an HTTP replay by itself.
_GOVERNANCE_SUFFIXES = (
    "scripts/manage-evidence.py",
    "scripts/verify-owned-native-license-removal.py",
    "scripts/verify-source-only-checkpoint.py",
    "scripts/reject-supplier-runtime.sh",
    "tools/api_route_closure.py",
    "tools/finalize_api_report.py",
)

_DISTRIBUTION_INPUT_SUFFIXES = (
    "project/pom.xml",
    "owned-framework/pom.xml",
    "owned-personnel/pom.xml",
    "owned-equipment/pom.xml",
    "owned-consumables/pom.xml",
    "owned-attachments/pom.xml",
    "owned-report/pom.xml",
    "owned-scheduler/pom.xml",
    "owned-web/pom.xml",
    "owned-distribution/package.py",
    "owned-distribution/start.sh",
    "owned-distribution/example",
    "owned-web/build-owned-web.sh",
    "scripts/build-owned-distribution.sh",
    "scripts/build-owned-framework.sh",
    "scripts/build-owned-personnel.sh",
    "scripts/build-owned-equipment.sh",
    "scripts/build-owned-consumables.sh",
    "scripts/build-owned-attachments.sh",
    "scripts/build-owned-report.sh",
    "scripts/build-owned-scheduler.sh",
    "scripts/verify-external-artifacts.sh",
    "build-support/external-artifacts.sha256",
    "build-support/t01-manifest.sha256",
    "build-support/t02-deps-manifest.sha256",
    "build-support/t02-build-plugin-seed.sha256",
    "build/oracle-free-source-manifest.json",
)

RUNTIME_CONTRACT_SCOPE = (
    "runtime.contract_version",
    "runtime.oracle_instance_id",
    "runtime.oracle_container",
    "runtime.baseline_schema",
    "runtime.runtime_schema",
    "runtime.baseline_schema_hash",
    "runtime.runtime_schema_hash_before",
    "runtime.runtime_schema_hash_after",
    "runtime.baseline_key_data_hash",
    "runtime.runtime_key_data_hash_before",
    "runtime.runtime_key_data_hash_after",
    "runtime.schema_copy_method",
    "runtime.baseline_refreshed",
    "runtime.oracle_reused",
    "runtime.redis_reused",
    "runtime.jvm_major",
    "runtime.container_runtime",
    "runtime.database",
    "runtime.db_pool",
    "runtime.db_pool_version",
    "runtime.cleanup_contract_version",
    "runtime.baseline_schema_immutable",
    "runtime.runtime_schema_restored",
    "runtime.application_instances",
    "runtime.oracle_instances",
    "runtime.redis_instances",
    "runtime.boundary_process",
    "runtime.internal_http_bridge",
    "runtime.jni_replacement_process",
)

_DEFAULT_RUNTIME_CONTRACT: dict[str, Any] = {
    "contract_version": RUNTIME_CONTRACT_VERSION,
    "oracle_instance_id": "lims-owned-sss2-oracle",
    "oracle_container": "lims-owned-sss2-oracle",
    "baseline_schema": "LIMS_BASELINE_SSS2",
    "runtime_schema": "LIMS_RUNTIME_SSS2",
    "baseline_schema_hash": "unbound",
    "runtime_schema_hash_before": "unbound",
    "runtime_schema_hash_after": "unbound",
    "baseline_key_data_hash": "unbound",
    "runtime_key_data_hash_before": "unbound",
    "runtime_key_data_hash_after": "unbound",
    "schema_copy_method": "baseline-copy-same-oracle-instance",
    "baseline_refreshed": False,
    "oracle_reused": True,
    "redis_reused": True,
    "jvm_major": 8,
    "container_runtime": "docker",
    "database": "Oracle",
    "db_pool": "HikariCP",
    "db_pool_version": "3.4.5",
    "cleanup_contract_version": "persistent-oracle-v3",
    "baseline_schema_immutable": True,
    "runtime_schema_restored": True,
    "application_instances": 1,
    "oracle_instances": 1,
    "redis_instances": 1,
    "boundary_process": False,
    "internal_http_bridge": False,
    "jni_replacement_process": False,
}


def _scoped_paths(repo_root: Path, suffixes: Sequence[str]) -> tuple[str, ...]:
    prefix = "" if is_standalone_root(repo_root) else "lims-recovery/"
    return tuple(
        suffix if suffix.startswith("runtime.") else prefix + suffix
        for suffix in suffixes
    )


def _unique(values: Iterable[str]) -> tuple[str, ...]:
    return tuple(dict.fromkeys(value for value in values if value))


APPLICATION_SCOPE = _scoped_paths(REPO_ROOT, _APPLICATION_SUFFIXES)
HARNESS_SCOPE = _scoped_paths(REPO_ROOT, _HARNESS_SUFFIXES)
PLANNING_SCOPE = _scoped_paths(REPO_ROOT, _PLANNING_SUFFIXES)
GOVERNANCE_SCOPE = _scoped_paths(REPO_ROOT, _GOVERNANCE_SUFFIXES)
IMPLEMENTATION_SCOPE = _unique((
    *APPLICATION_SCOPE,
    *HARNESS_SCOPE,
    *RUNTIME_CONTRACT_SCOPE,
    *PLANNING_SCOPE,
    *GOVERNANCE_SCOPE,
))
DISTRIBUTION_INPUT_SCOPE = _scoped_paths(REPO_ROOT, _DISTRIBUTION_INPUT_SUFFIXES)


def _run(args: Sequence[str], cwd: Path) -> str:
    result = subprocess.run(args, cwd=cwd, text=True, capture_output=True, check=False)
    return result.stdout.strip() if not result.returncode else ""


def repository_head(repo_root: Path = REPO_ROOT) -> str:
    return _run(["git", "rev-parse", "HEAD"], repo_root) or "unknown"


def nested_project_commit(repo_root: Path = REPO_ROOT) -> str:
    project = project_root(repo_root)
    if not (project / ".git").exists() and not (project / ".git").is_file():
        for manifest in (
            repo_root / "docs" / "standalone-root-migration.json",
            repo_root / "docs" / "standalone-migration-manifest.json",
        ):
            try:
                data = json.loads(manifest.read_text(encoding="utf-8"))
            except (OSError, json.JSONDecodeError):
                continue
            value = data.get("project_source_commit")
            if isinstance(value, str) and value:
                return value
        return "materialized-tree"
    return _run(["git", "rev-parse", "HEAD"], project) or "unknown"


def _files_for(path: Path) -> list[Path]:
    if path.is_file():
        return [path]
    if not path.is_dir():
        return []
    ignored_parts = {".git", "target", "__pycache__"}
    return sorted(
        item
        for item in path.rglob("*")
        if item.is_file()
        and not any(part in ignored_parts for part in item.parts)
        and item.suffix not in {".pyc", ".pyo"}
    )


def _manifest(repo_root: Path, scopes: Iterable[str]) -> list[dict[str, str]]:
    items: list[dict[str, str]] = []
    for scope in _unique(scopes):
        if scope.startswith("runtime."):
            continue
        root = repo_root / scope
        for path in _files_for(root):
            rel = path.relative_to(repo_root).as_posix()
            digest = hashlib.sha256(path.read_bytes()).hexdigest()
            items.append({"path": rel, "sha256": digest})
    return sorted(items, key=lambda item: item["path"])


def _canonical_digest(value: Any) -> str:
    payload = json.dumps(value, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(payload.encode("utf-8")).hexdigest()


def _status_for_scope(repo_root: Path, scope: str) -> list[str]:
    if scope.startswith("runtime."):
        return []
    if scope.startswith("lims-recovery/project/") and not is_standalone_root(repo_root):
        nested = repo_root / "lims-recovery" / "project"
        nested_scope = scope[len("lims-recovery/project/"):]
        if not nested.exists():
            return []
        result = subprocess.run(
            ["git", "status", "--porcelain", "--untracked-files=all", "--", nested_scope],
            cwd=nested, text=True, capture_output=True, check=False,
        )
        return [line for line in result.stdout.splitlines() if line.strip()]
    result = subprocess.run(
        ["git", "status", "--porcelain", "--untracked-files=all", "--", scope],
        cwd=repo_root, text=True, capture_output=True, check=False,
    )
    return [line for line in result.stdout.splitlines() if line.strip()]


def scope_dirty(repo_root: Path = REPO_ROOT, scope_name: str = "implementation") -> bool:
    scopes = {
        "application": _scoped_paths(repo_root, _APPLICATION_SUFFIXES),
        "harness": _scoped_paths(repo_root, _HARNESS_SUFFIXES),
        "planning": _scoped_paths(repo_root, _PLANNING_SUFFIXES),
        "governance": _scoped_paths(repo_root, _GOVERNANCE_SUFFIXES),
        "implementation": _unique((
            *_scoped_paths(repo_root, _APPLICATION_SUFFIXES),
            *_scoped_paths(repo_root, _HARNESS_SUFFIXES),
            *_scoped_paths(repo_root, _PLANNING_SUFFIXES),
            *_scoped_paths(repo_root, _GOVERNANCE_SUFFIXES),
        )),
    }
    try:
        selected = scopes[scope_name]
    except KeyError as error:
        raise ValueError("unknown binding scope: " + scope_name) from error
    return any(_status_for_scope(repo_root, scope) for scope in selected)


def implementation_dirty(repo_root: Path = REPO_ROOT,
                         implementation_scope: Iterable[str] = IMPLEMENTATION_SCOPE) -> bool:
    values = tuple(implementation_scope)
    if values != IMPLEMENTATION_SCOPE:
        raise ValueError("implementation binding scope is centralized")
    return scope_dirty(repo_root, "implementation")


def distribution_input_digest(repo_root: Path = REPO_ROOT,
                              input_scope: Iterable[str] = DISTRIBUTION_INPUT_SCOPE) -> str:
    scope = _unique(input_scope)
    return _canonical_digest({
        "schema": BINDING_SCHEMA_VERSION,
        "surface": "distribution",
        "scope": list(scope),
        "files": _manifest(repo_root, scope),
    })


def _scope_digest(repo_root: Path,
                  surface: str,
                  scope: Iterable[str],
                  context: Mapping[str, Any]) -> str:
    values = _unique(scope)
    return _canonical_digest({
        "schema": BINDING_SCHEMA_VERSION,
        "surface": surface,
        "scope": list(values),
        "files": _manifest(repo_root, values),
        "context": dict(context),
    })


def _normalized_runtime_contract(value: Optional[Mapping[str, Any]]) -> dict[str, Any]:
    result = dict(_DEFAULT_RUNTIME_CONTRACT)
    if value is None:
        return result
    if not isinstance(value, Mapping):
        raise TypeError("runtime_contract must be a mapping")
    for key in _DEFAULT_RUNTIME_CONTRACT:
        if key in value:
            candidate = value[key]
            if isinstance(candidate, (str, int, float, bool)) or candidate is None:
                result[key] = candidate
    model = value.get("route_execution_model")
    if isinstance(model, Mapping):
        for key in ("boundary_process", "internal_http_bridge", "jni_replacement_process"):
            if key in model:
                result[key] = model[key]
    cleanup = value.get("cleanup")
    if isinstance(cleanup, Mapping) and cleanup.get("cleanup_mode"):
        result["cleanup_contract_version"] = str(cleanup["cleanup_mode"])
    return result


def runtime_contract_digest(runtime_contract: Optional[Mapping[str, Any]] = None) -> str:
    return _canonical_digest({
        "schema": BINDING_SCHEMA_VERSION,
        "surface": "runtime_contract",
        "scope": list(RUNTIME_CONTRACT_SCOPE),
        "contract": _normalized_runtime_contract(runtime_contract),
    })


def implementation_tree_digest(repo_root: Path = REPO_ROOT,
                               implementation_scope: Iterable[str] = IMPLEMENTATION_SCOPE,
                               input_scope: Iterable[str] = DISTRIBUTION_INPUT_SCOPE,
                               nested_commit: Optional[str] = None,
                               distribution_digest: Optional[str] = None,
                               runtime_contract: Optional[Mapping[str, Any]] = None) -> str:
    """Return the legacy aggregate digest over all dependency surfaces."""
    root = Path(repo_root).resolve()
    if _unique(implementation_scope) != _unique(_scoped_paths(root, (
        *_APPLICATION_SUFFIXES,
        *_HARNESS_SUFFIXES,
        *RUNTIME_CONTRACT_SCOPE,
        *_PLANNING_SUFFIXES,
        *_GOVERNANCE_SUFFIXES,
    ))):
        raise ValueError("implementation binding scope is centralized")
    if _unique(input_scope) != _unique(_scoped_paths(root, _DISTRIBUTION_INPUT_SUFFIXES)):
        raise ValueError("distribution binding scope is centralized")
    nested = nested_commit if nested_commit is not None else nested_project_commit(root)
    distribution = distribution_digest if distribution_digest is not None else distribution_input_digest(root)
    application_digest = _scope_digest(root, "application", _scoped_paths(root, _APPLICATION_SUFFIXES), {
        "nested_project_commit": nested,
        "distribution_input_digest": distribution,
    })
    harness_digest = _scope_digest(root, "harness", _scoped_paths(root, _HARNESS_SUFFIXES), {
        "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
        "normalizer_version": NORMALIZER_VERSION,
    })
    runtime_digest = runtime_contract_digest(runtime_contract)
    planning_digest = _scope_digest(root, "planning", _scoped_paths(root, _PLANNING_SUFFIXES), {
        "planning_schema_version": PLANNING_SCHEMA_VERSION,
    })
    governance_digest = _scope_digest(root, "governance", _scoped_paths(root, _GOVERNANCE_SUFFIXES), {
        "governance_schema_version": GOVERNANCE_SCHEMA_VERSION,
    })
    return _canonical_digest({
        "schema": BINDING_SCHEMA_VERSION,
        "surface": "implementation_compatibility",
        "application": application_digest,
        "harness": harness_digest,
        "runtime_contract": runtime_digest,
        "planning": planning_digest,
        "governance": governance_digest,
    })


def capture_binding(repo_root: Path = REPO_ROOT,
                    runtime_contract: Optional[Mapping[str, Any]] = None) -> dict[str, Any]:
    """Capture all orthogonal bindings behind one stable interface."""
    root = Path(repo_root).resolve()
    application_scope = _scoped_paths(root, _APPLICATION_SUFFIXES)
    harness_scope = _scoped_paths(root, _HARNESS_SUFFIXES)
    planning_scope = _scoped_paths(root, _PLANNING_SUFFIXES)
    governance_scope = _scoped_paths(root, _GOVERNANCE_SUFFIXES)
    distribution_scope = _scoped_paths(root, _DISTRIBUTION_INPUT_SUFFIXES)
    nested = nested_project_commit(root)
    distribution = distribution_input_digest(root, distribution_scope)
    runtime = _normalized_runtime_contract(runtime_contract)

    application_digest = _scope_digest(root, "application", application_scope, {
        "nested_project_commit": nested,
        "distribution_input_digest": distribution,
    })
    harness_digest = _scope_digest(root, "harness", harness_scope, {
        "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
        "normalizer_version": NORMALIZER_VERSION,
    })
    runtime_digest = runtime_contract_digest(runtime)
    planning_digest = _scope_digest(root, "planning", planning_scope, {
        "planning_schema_version": PLANNING_SCHEMA_VERSION,
    })
    governance_digest = _scope_digest(root, "governance", governance_scope, {
        "governance_schema_version": GOVERNANCE_SCHEMA_VERSION,
    })

    implementation_scope = _unique((
        *application_scope,
        *harness_scope,
        *RUNTIME_CONTRACT_SCOPE,
        *planning_scope,
        *governance_scope,
    ))
    aggregate_digest = implementation_tree_digest(
        root,
        implementation_scope=implementation_scope,
        input_scope=distribution_scope,
        nested_commit=nested,
        distribution_digest=distribution,
        runtime_contract=runtime,
    )

    dirty = {
        "application": scope_dirty(root, "application"),
        "harness": scope_dirty(root, "harness"),
        "runtime_contract": False,
        "planning": scope_dirty(root, "planning"),
        "governance": scope_dirty(root, "governance"),
    }
    dirty["implementation"] = any(dirty[name] for name in BINDING_SURFACES)

    return {
        "schema_version": BINDING_SCHEMA_VERSION,
        "application_binding": "app-" + application_digest[:16],
        "harness_binding": "harness-" + harness_digest[:16],
        "runtime_contract_binding": "rt-" + runtime_digest[:16],
        "planning_binding": "plan-" + planning_digest[:16],
        "governance_binding": "gov-" + governance_digest[:16],
        "implementation_binding": "impl-" + aggregate_digest[:16],
        "binding_id": "impl-" + aggregate_digest[:16],
        "application_tree_digest": application_digest,
        "harness_tree_digest": harness_digest,
        "runtime_contract_digest": runtime_digest,
        "planning_tree_digest": planning_digest,
        "governance_tree_digest": governance_digest,
        "implementation_tree_digest": aggregate_digest,
        "application_scope": list(application_scope),
        "harness_scope": list(harness_scope),
        "runtime_contract_scope": list(RUNTIME_CONTRACT_SCOPE),
        "planning_scope": list(planning_scope),
        "governance_scope": list(governance_scope),
        "implementation_scope": list(implementation_scope),
        "application_dirty": dirty["application"],
        "harness_dirty": dirty["harness"],
        "runtime_contract_dirty": dirty["runtime_contract"],
        "planning_dirty": dirty["planning"],
        "governance_dirty": dirty["governance"],
        "implementation_dirty": dirty["implementation"],
        "runtime_contract": runtime,
        "nested_project_commit": nested,
        "distribution_input_digest": distribution,
        "repository_head_at_capture": repository_head(root),
    }


def binding_delta(before: Mapping[str, Any],
                  after: Optional[Mapping[str, Any]] = None) -> dict[str, bool]:
    """Return dependency-surface changes without consulting repository HEAD."""
    current = after if after is not None else capture_binding()
    return {
        surface: before.get(surface + "_binding") != current.get(surface + "_binding")
        for surface in BINDING_SURFACES
    }


def binding_metadata(binding: Optional[Mapping[str, Any]] = None) -> dict[str, Any]:
    """Return the common provenance/binding fields for reports and manifests."""
    current = dict(binding or capture_binding())
    keys = (
        "repository_head_at_capture",
        "application_binding",
        "harness_binding",
        "runtime_contract_binding",
        "planning_binding",
        "governance_binding",
        "implementation_binding",
        "binding_id",
        "implementation_tree_digest",
        "implementation_dirty",
        "implementation_scope",
        "application_scope",
        "harness_scope",
        "runtime_contract_scope",
        "planning_scope",
        "governance_scope",
        "nested_project_commit",
        "distribution_input_digest",
    )
    return {key: current[key] for key in keys if key in current}


def replay_surfaces(before: Mapping[str, Any],
                    after: Optional[Mapping[str, Any]] = None,
                    *,
                    harness_semantics_unchanged: Optional[bool] = None) -> tuple[str, ...]:
    delta = binding_delta(before, after)
    selected = []
    for surface in REPLAY_SURFACES:
        if not delta[surface]:
            continue
        if surface == "harness" and harness_semantics_unchanged is True:
            continue
        selected.append(surface)
    return tuple(selected)


def binding_from_payload(payload: Mapping[str, Any]) -> Optional[str]:
    value = payload.get("implementation_binding")
    if isinstance(value, Mapping):
        value = value.get("binding_id")
    if isinstance(value, str) and value:
        return value
    digest = payload.get("implementation_tree_digest")
    if isinstance(digest, str) and digest:
        return "impl-" + digest[:16]
    return None


if __name__ == "__main__":
    print(json.dumps(capture_binding(), ensure_ascii=False, indent=2, sort_keys=True))
