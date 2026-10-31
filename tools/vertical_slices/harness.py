"""Dependency-free public-seam harness for tickets 08-14.

The production runtime is unavailable in this checkout. This harness proves
contract/replay behavior without claiming page-level runtime parity. All
external effects are fixture-only and append an audit event.
"""
from __future__ import annotations
import hashlib, json, os, secrets, time
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Callable, Dict, Iterable, List, Mapping, Optional

class HarnessError(Exception): pass
class AuthenticationError(HarnessError): pass
class PermissionDenied(HarnessError): pass
class IsolationViolation(HarnessError): pass

def _digest(value: str) -> str:
    return hashlib.sha256(value.encode()).hexdigest()

@dataclass(frozen=True)
class DataScope:
    organisations: frozenset[str] = frozenset()
    def allows(self, row: Mapping[str, Any]) -> bool:
        return not self.organisations or row.get("organisation") in self.organisations

@dataclass(frozen=True)
class User:
    user_id: str
    password_digest: str
    roles: frozenset[str]
    scope: DataScope

@dataclass(frozen=True)
class Session:
    token: str
    user_id: str
    roles: frozenset[str]
    scope: DataScope

class AuditLog:
    def __init__(self) -> None: self.records: List[Dict[str, Any]] = []
    def append(self, event: str, *, actor: Optional[str] = None, **details: Any) -> None:
        self.records.append({"at": int(time.time()), "event": event, "actor": actor, "details": details})
    def events(self, event: str) -> List[Dict[str, Any]]:
        return [r for r in self.records if r["event"] == event]
    def write_jsonl(self, path: os.PathLike[str] | str) -> None:
        destination = Path(path); destination.parent.mkdir(parents=True, exist_ok=True)
        with destination.open("w", encoding="utf-8") as stream:
            for record in self.records:
                stream.write(json.dumps(record, ensure_ascii=False, sort_keys=True) + "\n")

class AuthService:
    def __init__(self, users: Iterable[User], audit: AuditLog) -> None:
        self.users = {u.user_id: u for u in users}; self.audit = audit; self.sessions: Dict[str, Session] = {}
    def login(self, user_id: str, password: str) -> Session:
        user = self.users.get(user_id)
        if user is None or user.password_digest != _digest(password):
            self.audit.append("auth.login.failure", actor=user_id, reason="invalid-credentials"); raise AuthenticationError("invalid credentials")
        session = Session(secrets.token_urlsafe(18), user.user_id, user.roles, user.scope); self.sessions[session.token] = session
        self.audit.append("auth.login.success", actor=user_id, session=session.token); return session
    def logout(self, session: Session) -> None:
        self.sessions.pop(session.token, None); self.audit.append("auth.logout", actor=session.user_id, session=session.token)
    def require(self, session: Session, permission: str) -> None:
        if session.token not in self.sessions or permission not in session.roles:
            reason = "session" if session.token not in self.sessions else "role"
            self.audit.append("auth.denied", actor=session.user_id, permission=permission, reason=reason); raise PermissionDenied(permission)

class QueryService:
    def __init__(self, kind: str, rows: Iterable[Mapping[str, Any]], auth: AuthService, audit: AuditLog) -> None:
        self.kind, self.rows, self.auth, self.audit = kind, [dict(r) for r in rows], auth, audit; self.permission = f"query:{kind}"
    def _visible(self, session: Session) -> List[Dict[str, Any]]: return [r for r in self.rows if session.scope.allows(r)]
    def list(self, session: Session, *, contains: Optional[str] = None, page: int = 1, size: int = 20) -> Dict[str, Any]:
        self.auth.require(session, self.permission)
        if page < 1 or size < 1: raise ValueError("page and size must be positive")
        rows = self._visible(session)
        if contains:
            needle = contains.casefold(); rows = [r for r in rows if needle in json.dumps(r, ensure_ascii=False).casefold()]
        result = {"kind": self.kind, "page": page, "size": size, "total": len(rows), "items": rows[(page - 1) * size: page * size]}
        self.audit.append("query.list", actor=session.user_id, kind=self.kind, page=page, size=size, total=len(rows), contains=contains); return result
    def detail(self, session: Session, row_id: str) -> Dict[str, Any]:
        self.auth.require(session, self.permission)
        for row in self._visible(session):
            if str(row.get("id")) == str(row_id):
                self.audit.append("query.detail", actor=session.user_id, kind=self.kind, row_id=row_id); return row
        self.audit.append("query.detail.missing-or-out-of-scope", actor=session.user_id, kind=self.kind, row_id=row_id); raise KeyError(row_id)

class AttachmentService:
    def __init__(self, root: os.PathLike[str] | str, auth: AuthService, audit: AuditLog) -> None:
        self.root = Path(root).resolve(); self.root.mkdir(parents=True, exist_ok=True); self.auth, self.audit = auth, audit; self.attachments: Dict[str, Dict[str, Any]] = {}
    def register(self, attachment_id: str, filename: str, content: bytes, *, permission: str) -> None:
        path = (self.root / attachment_id).resolve()
        if self.root not in path.parents: raise IsolationViolation("attachment path escapes isolated root")
        path.write_bytes(content); self.attachments[attachment_id] = {"path": path, "filename": filename, "permission": permission}; self.audit.append("attachment.register", attachment_id=attachment_id, source="isolated-fixture")
    def fetch(self, session: Session, attachment_id: str) -> bytes:
        record = self.attachments.get(attachment_id)
        if record is None: self.audit.append("attachment.missing", actor=session.user_id, attachment_id=attachment_id); raise FileNotFoundError(attachment_id)
        self.auth.require(session, record["permission"]); path = Path(record["path"]).resolve()
        if self.root not in path.parents or not path.is_file(): raise IsolationViolation("attachment outside isolated root")
        self.audit.append("attachment.fetch", actor=session.user_id, attachment_id=attachment_id, source="isolated-fixture"); return path.read_bytes()

class IntegrationReplay:
    def __init__(self, fixtures: Mapping[str, Iterable[Mapping[str, Any]]], audit: AuditLog) -> None:
        self.fixtures = {k: [dict(v) for v in values] for k, values in fixtures.items()}; self.audit, self.seen_keys = audit, {}
    @staticmethod
    def _reject(endpoint: str) -> None:
        lowered = endpoint.casefold()
        if any(marker in lowered for marker in ("10.", "192.168.", "218.93.", "czjky", "sunwayworld", "exmail")): raise IsolationViolation("production endpoint rejected")
    def call(self, endpoint: str, *, source: str, idempotency_key: str, attempt: int = 1) -> Dict[str, Any]:
        self._reject(endpoint)
        if idempotency_key in self.seen_keys:
            result = dict(self.seen_keys[idempotency_key]); result["replayed"] = True; self.audit.append("integration.idempotent", endpoint=endpoint, source=source, idempotency_key=idempotency_key); return result
        responses = self.fixtures.get(endpoint)
        if not responses: self.audit.append("integration.failure", endpoint=endpoint, source=source, reason="no-fixture"); raise IsolationViolation("no isolated fixture")
        response = responses.pop(0); response.setdefault("source", source); response.setdefault("attempt", attempt); response.setdefault("endpoint", endpoint)
        self.audit.append("integration.response", endpoint=endpoint, source=source, status=response.get("status"), attempt=attempt)
        if response.get("status", 500) < 500: self.seen_keys[idempotency_key] = dict(response)
        return response
    def execute(self, endpoint: str, *, source: str, idempotency_key: str, retries: int = 2) -> Dict[str, Any]:
        for attempt in range(1, retries + 2):
            response = self.call(endpoint, source=source, idempotency_key=idempotency_key, attempt=attempt)
            if response.get("status", 500) < 500: return response
            self.audit.append("integration.retry", endpoint=endpoint, source=source, attempt=attempt)
        self.audit.append("integration.failure", endpoint=endpoint, source=source, reason="retry-exhausted"); raise HarnessError("retries exhausted")

class ReportAdapter:
    def __init__(self, auth: AuthService, replay: IntegrationReplay, audit: AuditLog) -> None: self.auth, self.replay, self.audit = auth, replay, audit
    def run(self, session: Session, report: str, params: Mapping[str, Any], *, endpoint: str, source: str = "report-replay") -> Dict[str, Any]:
        self.auth.require(session, f"report:{report}"); key = f"report:{report}:{json.dumps(dict(params), sort_keys=True)}"
        result = self.replay.execute(endpoint, source=source, idempotency_key=key); self.audit.append("report.result", actor=session.user_id, report=report, status=result.get("status"), authorization_material="third-party-boundary-preserved"); return result

@dataclass
class Task:
    name: str; permission: str; action: Callable[[], Mapping[str, Any]]; external_write: bool = False; enabled: bool = True

class Scheduler:
    def __init__(self, auth: AuthService, audit: AuditLog, *, isolated: bool = True) -> None: self.auth, self.audit, self.isolated, self.tasks, self.rollback_journal = auth, audit, isolated, {}, []
    def register(self, task: Task) -> None: self.tasks[task.name] = task
    def trigger(self, session: Session, task_name: str, *, retries: int = 1) -> Mapping[str, Any]:
        task = self.tasks[task_name]; self.auth.require(session, task.permission)
        if not task.enabled or (self.isolated and task.external_write):
            self.audit.append("scheduler.disabled", actor=session.user_id, task=task_name, status="disabled-in-isolated-env"); raise IsolationViolation("task disabled in isolated environment")
        for attempt in range(1, retries + 2):
            try:
                result = dict(task.action()); self.rollback_journal.append({"task": task_name, "result": result, "attempt": attempt}); self.audit.append("scheduler.success", actor=session.user_id, task=task_name, attempt=attempt, result=result); return result
            except Exception as exc:  # contract records retryable failure
                self.audit.append("scheduler.failure", actor=session.user_id, task=task_name, attempt=attempt, error=type(exc).__name__)
                if attempt > retries: raise
                self.audit.append("scheduler.retry", actor=session.user_id, task=task_name, attempt=attempt)
        raise AssertionError("unreachable")

class VerticalSliceHarness:
    def __init__(self, attachment_root: os.PathLike[str] | str) -> None:
        self.audit = AuditLog(); users = [User("alice", _digest("fixture-password"), frozenset({"query:personnel", "query:equipment", "query:consumables", "attachment:read", "report:inventory", "task:run"}), DataScope(frozenset({"org-a"}))), User("bob", _digest("fixture-password"), frozenset({"query:personnel"}), DataScope(frozenset({"org-b"})))]
        self.auth = AuthService(users, self.audit)
        self.queries = {"personnel": QueryService("personnel", [{"id": "p1", "name": "A", "organisation": "org-a"}, {"id": "p2", "name": "B", "organisation": "org-b"}], self.auth, self.audit), "equipment": QueryService("equipment", [{"id": "e1", "name": "Balance", "organisation": "org-a"}], self.auth, self.audit), "consumables": QueryService("consumables", [{"id": "c1", "name": "Tube", "organisation": "org-a", "stock": 7}], self.auth, self.audit)}
        self.attachments = AttachmentService(attachment_root, self.auth, self.audit); self.replay = IntegrationReplay({"fixture://inventory": [{"status": 503}, {"status": 200, "body": {"count": 7}}], "fixture://report": [{"status": 200, "body": {"rows": 1}}]}, self.audit); self.reports = ReportAdapter(self.auth, self.replay, self.audit); self.scheduler = Scheduler(self.auth, self.audit, isolated=True)

def demo(root: os.PathLike[str] | str) -> Dict[str, Any]:
    h = VerticalSliceHarness(root); alice = h.auth.login("alice", "fixture-password"); h.attachments.register("a1", "fixture.txt", b"isolated", permission="attachment:read"); result: Dict[str, Any] = {"status": "harness-pass", "runtime": "unavailable-runtime", "queries": {}}
    for kind, service in h.queries.items(): result["queries"][kind] = service.list(alice, page=1, size=20)
    result["attachment"] = h.attachments.fetch(alice, "a1").decode(); result["integration"] = h.replay.execute("fixture://inventory", source="inventory-replay", idempotency_key="inventory-1"); result["integration_idempotent"] = h.replay.execute("fixture://inventory", source="inventory-replay", idempotency_key="inventory-1"); result["report"] = h.reports.run(alice, "inventory", {"organisation": "org-a"}, endpoint="fixture://report"); h.scheduler.register(Task("safe", "task:run", lambda: {"changed": 0})); result["scheduler"] = h.scheduler.trigger(alice, "safe"); h.auth.logout(alice); result["audit_events"] = len(h.audit.records); return result

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(); parser.add_argument("--root", default="/tmp/vertical-slice-attachments"); parser.add_argument("--audit"); args = parser.parse_args(); output = demo(args.root); print(json.dumps(output, ensure_ascii=False, sort_keys=True, indent=2))
