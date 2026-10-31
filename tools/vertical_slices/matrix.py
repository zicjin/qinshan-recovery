"""Declarative vertical-slice evidence matrix.

The matrix is intentionally small: it describes the observable case boundary
that a runner must execute, while the referenced JSON evidence retains the
actual HTTP transcript, source identity and cleanup proof.
"""
from dataclasses import dataclass
import hashlib
from pathlib import Path
import subprocess
import sys
from typing import Dict, Iterable, List, Optional, Sequence, Tuple

SCRIPTS_ROOT = Path(__file__).resolve().parents[2] / "scripts"
sys.path.insert(0, str(SCRIPTS_ROOT))
from evidence_output import current_implementation_binding  # noqa: E402

REQUIRED_COLUMNS = (
    "case_id", "route", "role", "organization", "mode", "seed",
    "expected_result", "audit", "cleanup", "evidence",
)
ALLOWED_RESULTS = {"allowed", "forbidden", "empty", "rejected", "unsupported"}
RUNNER_COLUMNS = ("case_id", "runner", "source_scopes", "contract")
ALLOWED_CONTRACTS = {"legacy", "strict"}
VERIFIER_PATHS = (
    "tools/vertical_slices/matrix.py", "scripts/replay-vertical-slice.py",
    "scripts/tests/test-strict-vertical-slice.py", "evidence/vertical-slice-runners.tsv",
)


def validate_routes(route: str) -> List[str]:
    import re

    routes = route.split(";")
    for item in routes:
        if not re.fullmatch(r"(?:GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS) /[^\s;]*", item):
            raise ValueError("route must be METHOD /absolute/path: %s" % item)
    return routes


@dataclass(frozen=True)
class MatrixCase:
    case_id: str
    route: str
    role: str
    organization: str
    mode: str
    seed: str
    expected_result: str
    audit: str
    cleanup: str
    evidence: str


@dataclass(frozen=True)
class RunnerRegistration:
    case_id: str
    runner: str
    source_scopes: Tuple[str, ...]
    contract: str


def load_matrix(path: Path) -> List[MatrixCase]:
    rows = path.read_text(encoding="utf-8").splitlines()
    if not rows:
        raise ValueError("matrix is empty")
    header = rows[0].split("\t")
    if tuple(header) != REQUIRED_COLUMNS:
        raise ValueError("matrix columns do not match the declared interface")
    result: List[MatrixCase] = []
    seen = set()
    for line_number, line in enumerate(rows[1:], 2):
        if not line.strip() or line.lstrip().startswith("#"):
            continue
        values = line.split("\t")
        if len(values) != len(REQUIRED_COLUMNS):
            raise ValueError("matrix row %d has %d columns" % (line_number, len(values)))
        item = MatrixCase(*values)
        if not item.case_id or item.case_id in seen:
            raise ValueError("matrix case id is empty or duplicated: %s" % item.case_id)
        if item.expected_result not in ALLOWED_RESULTS:
            raise ValueError("unsupported expected result: %s" % item.expected_result)
        validate_routes(item.route)
        if not item.evidence.endswith(".json"):
            raise ValueError("matrix evidence must be JSON: %s" % item.evidence)
        seen.add(item.case_id)
        result.append(item)
    if not result:
        raise ValueError("matrix contains no cases")
    return result


def load_runner_registry(path: Path) -> Dict[str, RunnerRegistration]:
    """Load explicit runner associations without executing any command."""
    rows = path.read_text(encoding="utf-8").splitlines()
    if not rows:
        raise ValueError("runner registry is empty")
    if tuple(rows[0].split("\t")) != RUNNER_COLUMNS:
        raise ValueError("runner registry columns do not match the declared interface")
    result: Dict[str, RunnerRegistration] = {}
    for line_number, line in enumerate(rows[1:], 2):
        if not line.strip() or line.lstrip().startswith("#"):
            continue
        values = line.split("\t")
        if len(values) != len(RUNNER_COLUMNS):
            raise ValueError("runner registry row %d has %d columns" % (line_number, len(values)))
        case_id, runner, scopes, contract = values
        source_scopes = tuple(item for item in scopes.split(";") if item)
        if not case_id or case_id in result:
            raise ValueError("runner registry case id is empty or duplicated: %s" % case_id)
        if not runner or not source_scopes or contract not in ALLOWED_CONTRACTS:
            raise ValueError("runner registry row %d is invalid" % line_number)
        result[case_id] = RunnerRegistration(case_id, runner, source_scopes, contract)
    if not result:
        raise ValueError("runner registry contains no cases")
    return result


def _git(repo: Path, arguments: Sequence[str], binary: bool = False):
    result = subprocess.run(["git"] + list(arguments), cwd=str(repo),
                            stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                            check=False)
    if result.returncode:
        message = result.stderr.decode("utf-8", "replace").strip()
        raise ValueError("git evidence lookup failed: %s" % message)
    return result.stdout if binary else result.stdout.decode("utf-8")


def _git_root(root: Path) -> Path:
    return Path(_git(root, ["rev-parse", "--show-toplevel"]).strip())


def _commit_exists(repo: Path, commit: str) -> bool:
    repo = _git_root(repo)
    result = subprocess.run(["git", "cat-file", "-e", commit + "^{commit}"],
                            cwd=str(repo), stdout=subprocess.DEVNULL,
                            stderr=subprocess.DEVNULL, check=False)
    return result.returncode == 0


def _repo_path(root: Path, path: str) -> Tuple[Path, str]:
    repo = _git_root(root)
    git_path = (root.resolve() / path).relative_to(repo.resolve()).as_posix()
    return repo, git_path


def _tree_paths(root: Path, commit: str, scopes: Sequence[str]) -> List[str]:
    repo = _git_root(root)
    prefix = root.resolve().relative_to(repo.resolve()).as_posix()
    result = set()
    for scope in scopes:
        git_scope = (Path(prefix) / scope).as_posix() if prefix != "." else scope
        output = _git(repo, ["ls-tree", "-r", "--name-only", commit, "--", git_scope])
        for item in output.splitlines():
            if not item:
                continue
            if prefix and item.startswith(prefix + "/"):
                item = item[len(prefix) + 1:]
            result.add(item)
    return sorted(result)


def _worktree_paths(root: Path, scopes: Sequence[str]) -> List[str]:
    result = set()
    for scope in scopes:
        candidate = root / scope
        if candidate.is_file():
            result.add(candidate.relative_to(root).as_posix())
        elif candidate.is_dir():
            result.update(path.relative_to(root).as_posix() for path in candidate.rglob('*')
                         if path.is_file() and '.git' not in path.parts and '__pycache__' not in path.parts and path.suffix != '.pyc')
    return sorted(result)


def _sha_at_commit(root: Path, commit: str, path: str) -> str:
    repo, git_path = _repo_path(root, path)
    return hashlib.sha256(_git(repo, ["show", commit + ":" + git_path], binary=True)).hexdigest()


def _sha_at_worktree(root: Path, path: str) -> str:
    candidate = root / path
    if not candidate.is_file():
        raise ValueError("source file is missing from current worktree: %s" % path)
    return hashlib.sha256(candidate.read_bytes()).hexdigest()

def _validate_strict_evidence(case: MatrixCase, payload: Dict[str, object],
                              registration: RunnerRegistration, root: Path,
                              git_root: Path) -> None:
    if registration.contract != "strict":
        return
    if payload.get("runner") != registration.runner:
        raise ValueError("runner association does not match for %s" % case.case_id)
    source_commit = payload.get("source_commit")
    if not isinstance(source_commit, str) or not source_commit or not _commit_exists(git_root, source_commit):
        raise ValueError("strict evidence source_commit is unavailable for %s" % case.case_id)
    repo_root = _git_root(git_root)
    # The implementation binding is the behaviour identity.  The complete
    # repository HEAD remains provenance only, so documentation, ticket and
    # evidence-registry commits do not invalidate a report whose implementation
    # digest is unchanged.
    current_binding = current_implementation_binding(repo_root)
    implementation_digest = payload.get("implementation_tree_digest")
    if implementation_digest:
        if implementation_digest != current_binding["implementation_tree_digest"]:
            raise ValueError("strict evidence implementation binding is stale for %s" % case.case_id)
        if payload.get("implementation_binding") != current_binding["binding_id"]:
            raise ValueError("strict evidence implementation_binding is stale for %s" % case.case_id)
        if payload.get("implementation_dirty") is not False or current_binding["implementation_dirty"]:
            raise ValueError("strict evidence implementation scope is dirty for %s" % case.case_id)
    else:
        # Reports without an implementation binding are legacy material.  The
        # matrix keeps those reports through a legacy runner registration, but
        # never accepts one as strict current evidence and never expands a
        # source_commit_exception into a strict pass.
        raise ValueError("strict evidence lacks implementation binding; legacy report is historical-only for %s" % case.case_id)
    # Binding-backed reports compare verifier files to the current worktree.
    has_source_scope_exception = True
    if payload.get("source_dirty") is not False:
        raise ValueError("strict evidence must be source-dirty=false for %s" % case.case_id)

    # A shared execution shard may emit one report for several behavior
    # contracts.  Prefer an explicit per-case result when present, while
    # retaining the original top-level contract for single-case reports.
    contract_payload = payload
    batch_results = payload.get("batch_case_results")
    if isinstance(batch_results, list):
        matches = [item for item in batch_results
                   if isinstance(item, dict) and item.get("case_id") == case.case_id]
        if len(matches) > 1:
            raise ValueError("strict evidence has duplicate batch result for %s" % case.case_id)
        if matches:
            contract_payload = matches[0]
            if contract_payload.get("fixture_restore") is not True:
                raise ValueError("strict batch fixture was not restored for %s" % case.case_id)
            if contract_payload.get("audit_observed") is not True:
                raise ValueError("strict batch audit was not observed for %s" % case.case_id)
    for field in ("role", "organization", "mode", "fixture"):
        actual = contract_payload.get(field, payload.get(field))
        if actual != (case.seed if field == "fixture" else getattr(case, field)):
            raise ValueError("strict evidence %s does not match matrix for %s" % (field, case.case_id))

    verifier_hashes = payload.get("verifier_sha256")
    if not isinstance(verifier_hashes, dict) or set(verifier_hashes) != set(VERIFIER_PATHS):
        raise ValueError("strict evidence has incomplete verifier hashes for %s" % case.case_id)
    for verifier_path, recorded_hash in verifier_hashes.items():
        expected_verifier_hash = (_sha_at_worktree(root, verifier_path)
                                  if has_source_scope_exception
                                  else _sha_at_commit(root, source_commit, verifier_path))
        if recorded_hash != expected_verifier_hash:
            raise ValueError("strict verifier hash does not match evidence commit: %s" % verifier_path)
        if _sha_at_worktree(root, verifier_path) != recorded_hash:
            raise ValueError("strict verifier hash is stale for %s: %s" % (case.case_id, verifier_path))

    coverage = payload.get("source_coverage")
    if not isinstance(coverage, dict):
        raise ValueError("strict evidence has no source coverage for %s" % case.case_id)
    roots = coverage.get("roots")
    if tuple(roots or ()) != registration.source_scopes:
        raise ValueError("strict source scope registration mismatch for %s" % case.case_id)
    files = coverage.get("files")
    if not isinstance(files, dict):
        raise ValueError("strict evidence source coverage has no files for %s" % case.case_id)
    expected_paths = set(_tree_paths(root, source_commit, registration.source_scopes))
    declared_paths = set(files)
    if declared_paths != expected_paths:
        missing = sorted(expected_paths - declared_paths)
        extra = sorted(declared_paths - expected_paths)
        raise ValueError("strict source coverage differs for %s (missing=%s extra=%s)" %
                         (case.case_id, missing[:3], extra[:3]))
    for path in sorted(expected_paths):
        recorded = files.get(path)
        if not isinstance(recorded, str) or recorded != _sha_at_commit(root, source_commit, path):
            raise ValueError("strict source hash does not match evidence commit for %s: %s" %
                             (case.case_id, path))
        if recorded != _sha_at_worktree(root, path):
            raise ValueError("strict source hash does not match current file for %s: %s" %
                             (case.case_id, path))
    current_paths = set(_worktree_paths(root, registration.source_scopes))
    added = sorted(current_paths - expected_paths)
    deleted = sorted(expected_paths - current_paths)
    source_diff = coverage.get("diff")
    if not isinstance(source_diff, dict) or source_diff.get("added") != added or source_diff.get("deleted") != deleted:
        raise ValueError("strict source added/deleted coverage is inconsistent for %s" % case.case_id)
    if added or deleted:
        raise ValueError("strict evidence is stale for %s (added=%s deleted=%s)" %
                         (case.case_id, added[:3], deleted[:3]))

    observed_cases = payload.get("http_cases")
    if not isinstance(observed_cases, list):
        raise ValueError("strict evidence has no structured HTTP observations for %s" % case.case_id)
    acceptable = {"allowed": range(200, 300), "empty": range(200, 300),
                  "forbidden": (401, 403), "rejected": (400, 409, 422),
                  "unsupported": (501,)}[case.expected_result]
    for route in validate_routes(case.route):
        matches = [item for item in observed_cases
                   if isinstance(item, dict) and item.get("route") == route
                   and item.get("role") == case.role
                   and item.get("organization") == case.organization
                   and item.get("mode") == case.mode
                   and item.get("status") in acceptable
                   and item.get("expected_status") == item.get("status")]
        if not matches:
            raise ValueError("strict route/role/organization/mode observation missing for %s: %s" %
                             (case.case_id, route))

    audit = payload.get("audit")
    if not isinstance(audit, dict) or not isinstance(audit.get("events"), list):
        raise ValueError("strict evidence has no structured audit for %s" % case.case_id)
    expected_outcome = "ALLOWED" if case.expected_result in ("allowed", "empty") else "DENIED"
    if not any(isinstance(event, dict) and event.get("action") == case.audit
               and event.get("outcome") == expected_outcome for event in audit["events"]):
        raise ValueError("strict audit observation missing for %s" % case.case_id)
    # A shared execution shard may carry one top-level audit stream for many
    # behavior contracts.  Each batch case can therefore declare its own
    # expected audit event; single-case reports continue to use the legacy
    # top-level expectation.
    expected_audit = contract_payload.get("audit_expected")
    if expected_audit is None:
        expected_audit = audit.get("expected")
    if not isinstance(expected_audit, list) or not expected_audit:
        raise ValueError("strict audit expectation missing for %s" % case.case_id)
    matched_case_expectation = False
    for expected in expected_audit:
        if not isinstance(expected, dict) or not isinstance(expected.get("action"), str):
            raise ValueError("strict audit expectation is malformed for %s" % case.case_id)
        if expected.get("outcome") not in {"ALLOWED", "DENIED"}:
            raise ValueError("strict audit expectation is malformed for %s" % case.case_id)
        if not any(all(event.get(key) == value for key, value in expected.items())
                   for event in audit["events"] if isinstance(event, dict)):
            raise ValueError("strict audit expectation does not match observation for %s" % case.case_id)
        if expected.get("action") == case.audit and expected.get("outcome") == expected_outcome:
            matched_case_expectation = True
    if not matched_case_expectation:
        raise ValueError("strict audit expectation for case is missing for %s" % case.case_id)
    cleanup = payload.get("cleanup")
    required_cleanup = ("browser_session_closed", "temporary_files_removed",
                        "container_removed", "anonymous_volumes_removed", "network_removed")
    if not isinstance(cleanup, dict) or any(cleanup.get(name) is not True for name in required_cleanup):
        raise ValueError("strict cleanup proof is incomplete for %s" % case.case_id)


def validate_evidence(cases: Iterable[MatrixCase], root: Path,
                      runner_registry: Optional[Dict[str, RunnerRegistration]] = None,
                      git_root: Optional[Path] = None) -> Dict[str, int]:
    import json

    counts = {"cases": 0, "pass": 0}
    for case in cases:
        counts["cases"] += 1
        evidence = root.parent / case.evidence if case.evidence.startswith(".scratch/") else root / case.evidence
        if not evidence.is_file():
            raise ValueError("missing evidence for %s: %s" % (case.case_id, evidence))
        payload = json.loads(evidence.read_text(encoding="utf-8"))
        if payload.get("status") != "executed-pass":
            raise ValueError("evidence is not executed-pass for %s" % case.case_id)
        if payload.get("production_access") is not False:
            raise ValueError("evidence does not prove production isolation for %s" % case.case_id)
        cleanup = payload.get("cleanup")
        if not cleanup:
            raise ValueError("evidence has no cleanup proof for %s" % case.case_id)
        if isinstance(cleanup, dict):
            failed = [name for name, value in cleanup.items()
                      if (name.endswith(("_removed", "_closed", "_stopped", "_restored"))
                          and value is not True)]
            if failed:
                raise ValueError("evidence cleanup failed for %s: %s" % (case.case_id, failed))
        # New runners expose observed HTTP cases. Require each declared route
        # to match the observed role/mode/result while retaining older evidence.
        if "http_cases" in payload:
            acceptable = {"allowed": range(200, 300), "empty": range(200, 300),
                          "forbidden": (401, 403), "rejected": (400, 409, 422),
                          "unsupported": (501,)}[case.expected_result]
            for route in validate_routes(case.route):
                observed = [item for item in payload["http_cases"]
                            if item.get("route") == route and item.get("mode") == case.mode
                            and item.get("role") == case.role
                            and item.get("status") in acceptable
                            and item.get("expected_status") == item.get("status")]
                if not observed:
                    raise ValueError("route/role/mode/result not observed for %s: %s" % (case.case_id, route))
        if runner_registry and case.case_id in runner_registry:
            _validate_strict_evidence(case, payload, runner_registry[case.case_id],
                                      root, git_root or root)
        counts["pass"] += 1
    return counts
