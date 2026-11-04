#!/usr/bin/env python3
"""Stable implementation binding shared by runners and evidence validators.

The repository HEAD is provenance only.  Behaviour evidence is bound to the
implementation tree and its build inputs, so documentation, ticket and
evidence-registry commits do not invalidate an otherwise unchanged runtime.
"""
from __future__ import annotations

import hashlib
import json
import os
from pathlib import Path
import subprocess
from typing import Any, Iterable, Mapping, Optional, Sequence


SCRIPT_ROOT = Path(__file__).resolve().parents[1]


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
    """Find the repository owning this script in parent or standalone mode.

    In the historical parent checkout ``lims-recovery`` is an ordinary
    directory below the parent Git repository.  In the migrated checkout it
    is itself the Git root and ``project`` is an ordinary source directory.
    ``git rev-parse`` is authoritative; the directory fallback keeps isolated
    unit fixtures usable.
    """
    if (SCRIPT_ROOT / ".git").exists():
        return SCRIPT_ROOT.resolve()
    return _git_root(SCRIPT_ROOT) or SCRIPT_ROOT.resolve().parent


REPO_ROOT = discover_repo_root()

FEATURE_SLUG = "sunway-supplier-exit-api-first"


def feature_root(repo_root: Path = REPO_ROOT,
                 feature_slug: str = FEATURE_SLUG) -> Path:
    """Return the authoritative planning/diagnostic feature directory.

    The standalone recovery repository owns implementation and canonical
    evidence, while the parent checkout owns ``.scratch`` planning and
    diagnostic artifacts.  During a clean standalone clone there may be no
    parent worktree, so fall back to a repository-local ``.scratch`` tree.
    ``SUNWAY_FEATURE_ROOT`` remains an explicit override for CI or an
    external artifact workspace.
    """
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
    """Return the checkout root used to resolve workspace-relative inputs."""
    root = Path(repo_root).resolve()
    if is_standalone_root(root):
        return feature_root(root).parent.parent
    return root


def resolve_workspace_path(value: str | Path,
                           repo_root: Path = REPO_ROOT) -> Path:
    """Resolve paths used by jobs/manifests in parent or standalone mode.

    Existing manifests use ``lims-recovery/...`` for source inputs and
    ``.scratch/...`` for parent-workspace artifacts.  Accept both forms after
    the root migration without creating a second copy of either tree.
    """
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


def is_standalone_root(repo_root: Path = REPO_ROOT) -> bool:
    root = Path(repo_root).resolve()
    return (root / ".git").exists() and (root / "project").is_dir() and \
        (root / "owned-framework").is_dir() and not (root / "lims-recovery").is_dir()


def project_root(repo_root: Path = REPO_ROOT) -> Path:
    root = Path(repo_root).resolve()
    return root / "project" if is_standalone_root(root) else root / "lims-recovery" / "project"


_IMPLEMENTATION_SUFFIXES = (
    "project/src/main/java",
    "owned-framework/src",
    "owned-personnel/src",
    "owned-equipment/src",
    "owned-consumables/src",
    "owned-attachments/src",
    "owned-report/src",
    "owned-scheduler/src",
    "owned-web/src",
    "reconstituted",
    "scripts/source_binding.py",
    "scripts/evidence_output.py",
    "scripts/spring_api_contract_runner.py",
    "scripts/fast_differential_api_runner.py",
    "scripts/api_wave_orchestrator.py",
    "scripts/owned_runtime_contract.py",
    "scripts/api_fixture.py",
    "scripts/remote-sql-fixture-executor.py",
    "scripts/start-strict-sss2-api-runtime.sh",
    "scripts/start-reused-sss2-api-runtime.sh",
    "scripts/cleanup-strict-sss2-api-runtime.sh",
    "scripts/refresh-owned-sss2-app.sh",
    "scripts/owned_readiness.py",
    "scripts/reconstitute-framework-source.sh",
    "scripts/resolve-java-source-closure.py",
    "scripts/verify-reconstituted-framework.py",
    "docs/reconstitution/decompiler-transforms.json",
    "tools/business_api_inventory.py",
    "tools/api_route_ledger.py",
    "tools/api_route_execution_plan.py",
    "tools/api_contract_skeleton.py",
    "tools/api_route_closure.py",
    "tools/finalize_api_report.py",
    "tools/vertical_slices/matrix.py",
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
    "scripts/build-prep.sh",
    "scripts/verify-external-artifacts.sh",
    "evidence/external-artifacts.sha256",
    "build/oracle-free-source-manifest.json",
    "docker/Dockerfile.built",
)


def _scoped_paths(repo_root: Path, suffixes: Sequence[str]) -> tuple[str, ...]:
    prefix = "" if is_standalone_root(repo_root) else "lims-recovery/"
    return tuple(prefix + suffix for suffix in suffixes)


# These are behaviour/runtime inputs. Documentation, evidence and generated
# diagnostic trees are deliberately outside this list. The paths are dynamic
# so the same helper works before and after the root migration.
IMPLEMENTATION_SCOPE = _scoped_paths(REPO_ROOT, _IMPLEMENTATION_SUFFIXES)
DISTRIBUTION_INPUT_SCOPE = _scoped_paths(REPO_ROOT, _DISTRIBUTION_INPUT_SUFFIXES)


def _run(args: Sequence[str], cwd: Path) -> str:
    result = subprocess.run(args, cwd=cwd, text=True, capture_output=True, check=False)
    if result.returncode:
        return ""
    return result.stdout.strip()


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
    return sorted(item for item in path.rglob("*") if item.is_file() and ".git" not in item.parts)


def _manifest(repo_root: Path, scopes: Iterable[str]) -> list[dict[str, str]]:
    items: list[dict[str, str]] = []
    for scope in scopes:
        root = repo_root / scope
        for path in _files_for(root):
            rel = path.relative_to(repo_root).as_posix()
            digest = hashlib.sha256(path.read_bytes()).hexdigest()
            items.append({"path": rel, "sha256": digest})
    return sorted(items, key=lambda item: item["path"])


def _status_for_scope(repo_root: Path, scope: str) -> list[str]:
    # project is a nested repository; its worktree state must be checked from
    # that repository rather than through the parent checkout's gitlink.
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


def implementation_dirty(repo_root: Path = REPO_ROOT,
                         implementation_scope: Iterable[str] = IMPLEMENTATION_SCOPE) -> bool:
    return any(_status_for_scope(repo_root, scope) for scope in implementation_scope)


def distribution_input_digest(repo_root: Path = REPO_ROOT,
                              input_scope: Iterable[str] = DISTRIBUTION_INPUT_SCOPE) -> str:
    payload = {"scope": list(input_scope), "files": _manifest(repo_root, input_scope)}
    return hashlib.sha256(json.dumps(payload, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


def implementation_tree_digest(repo_root: Path = REPO_ROOT,
                               implementation_scope: Iterable[str] = IMPLEMENTATION_SCOPE,
                               input_scope: Iterable[str] = DISTRIBUTION_INPUT_SCOPE,
                               nested_commit: Optional[str] = None,
                               distribution_digest: Optional[str] = None) -> str:
    scope = tuple(implementation_scope)
    inputs = tuple(input_scope)
    nested = nested_commit if nested_commit is not None else nested_project_commit(repo_root)
    distribution = distribution_digest if distribution_digest is not None else distribution_input_digest(repo_root, inputs)
    payload = {
        "implementation_scope": list(scope),
        "files": _manifest(repo_root, scope),
        "nested_project_commit": nested,
        "distribution_input_digest": distribution,
    }
    return hashlib.sha256(json.dumps(payload, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


def capture_binding(repo_root: Path = REPO_ROOT,
                    extra_scope: Iterable[str] = ()) -> dict[str, Any]:
    extra = tuple(dict.fromkeys(str(item) for item in extra_scope if str(item)))
    implementation_scope = _scoped_paths(repo_root, _IMPLEMENTATION_SUFFIXES)
    distribution_scope = _scoped_paths(repo_root, _DISTRIBUTION_INPUT_SUFFIXES)
    scope = tuple(dict.fromkeys((*implementation_scope, *distribution_scope, *extra)))
    nested = nested_project_commit(repo_root)
    distribution = distribution_input_digest(repo_root, distribution_scope)
    digest = implementation_tree_digest(
        repo_root, implementation_scope=scope, nested_commit=nested,
        distribution_digest=distribution,
    )
    dirty = implementation_dirty(repo_root, scope)
    return {
        "binding_id": "impl-" + digest[:16],
        "implementation_tree_digest": digest,
        "implementation_scope": list(scope),
        "implementation_dirty": dirty,
        "nested_project_commit": nested,
        "distribution_input_digest": distribution,
        "repository_head_at_capture": repository_head(repo_root),
    }


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
