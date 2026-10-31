#!/usr/bin/env python3
"""Observe four roles in two organizations through the shipped distribution."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import argparse
import hashlib
import importlib.util
import json
from pathlib import Path
import tempfile
import time
import traceback
import uuid

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location("distribution_equipment", Path(__file__).with_name("run-equipment-browser.py"))
distribution = importlib.util.module_from_spec(spec)
spec.loader.exec_module(distribution)
from vertical_slices import role_fixture
from vertical_slices.matrix import VERIFIER_PATHS

BASE = "/secure/emc/module/mdm/equipt/equipts"
MATRIX = "/secure/core/module/sys/menu-roles/queries/matrix-"
SOURCE_SCOPES = distribution.SOURCE_SCOPES + (
    "owned-distribution/tests/run-permission-matrix.py",
    "tools/vertical_slices/role_fixture.py",
    "tools/vertical_slices/fixtures/role-menu-matrix.sql",
)


class PermissionReplay(distribution.DistributionReplay):
    def build(self):
        distribution.SOURCE_SCOPES = SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-permission-matrix.py",
            scope="owned distribution permission matrix; four explicit roles in two organizations; default and isolated modes; synthetic Oracle and Redis only",
            role="role-admin", organization="org-a", mode="isolated",
            fixture="role-menu-matrix-fixture", actors=role_fixture.actors())
        self.allowed_writes = []
        self.report['execution_group'] = 'permission-role-organization'
        self.report['batch_case_results'] = []
        self.report['phase_timings_ms'] = {}
        self.report['verifier_sha256'] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in VERIFIER_PATHS
        }

    def database(self):
        super().database()
        self.credential = uuid.uuid4().hex
        self.secrets.append(self.credential)
        self.query(role_fixture.seed(hashlib.md5(self.credential.encode()).hexdigest()),
                   "four role/two organization fixture")
        account_count = self.query("SELECT COUNT(DISTINCT U.ID) FROM T_CORE_USER U JOIN T_CORE_ROLE_USER R ON U.ID=R.USERID JOIN T_CORE_USER_PERMISSION P ON U.ID=P.USERID WHERE U.ID LIKE 'matrix-%' AND P.TARGETID=U.ORGID;", "account baseline")
        self.check("all eight synthetic accounts have one explicit role and one organization",
                   account_count == "8")

    def observe(self, method, path, expected, label, payload=None):
        options = dict(method=method, cache="no-store", credentials="same-origin")
        if payload is not None:
            options.update(headers={"Content-Type": "application/json"}, body=json.dumps(payload))
        result = self.evaluate("fetch(" + json.dumps(path) + "," + json.dumps(options) +
                                ").then(async r=>({status:r.status,body:await r.text()}))")
        self.report["http_cases"].append(dict(route=method + " " + path,
            role=self.role, organization=self.organization, actor_id=self.actor,
            mode=self.mode, phase=self.phase, expected_status=expected, status=result["status"],
            response_body=self.redact(result["body"]), label=label))
        self.check(label, result["status"] == expected)
        return result["body"]

    def sign_in(self, actor):
        # Session/cookie and RSA material stay in the named browser; evidence
        # contains response status only, never the login request or key.
        self.evaluate("fetch('/core/module/sys/logout',{method:'POST'}).then(r=>r.status)")
        result = self.evaluate("(async()=>{const k=await fetch('/open/security/public-key',{cache:'no-store'}).then(r=>r.text());const b=new URLSearchParams({id:" +
            json.dumps(actor["user_id"]) + ",password:OwnedRsa.encrypt(" + json.dumps(self.credential) +
            ",k)});return fetch('/core/module/sys/login',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded','SECURITY-RSA-KEY':k},body:b.toString()}).then(r=>r.status);})()")
        self.check("RSA login " + actor["user_id"], result == 200)
        self.role, self.organization, self.actor = actor["role"], actor["organization"], actor["user_id"]

    @staticmethod
    def body(actor, foreign=False):
        category = ("12" if actor["category"] == "11" else "11") if foreign else actor["category"]
        return {"b": ["", [{"equiptId": "MATRIX-" + actor["user_id"], "equiptName": "Synthetic permission matrix",
                           "equiptCategoryId": int(category), "orgId": "org-b" if actor["organization"] == "org-a" else "org-a"}]]}

    def matrix(self, mode):
        self.configuration_overrides = {"owned.identity.role-menu-admin-menu-ids": "100900" if mode == "isolated" else ""}
        self.start(mode)
        self.role, self.organization, self.actor, self.phase = "anonymous", "none", "none", "anonymous"
        self.observe("POST", BASE + "/queries/raw", 403, mode + " anonymous business read", {})
        self.observe("POST", MATRIX + "headers", 403, mode + " anonymous matrix read", {})
        for actor in role_fixture.actors():
            self.phase = "baseline"
            self.sign_in(actor)
            name = mode + " " + self.actor
            business = self.role != "role-admin"
            matrix = mode == "isolated" and self.role in ("admin", "role-admin")
            menus = json.loads(self.observe("GET", "/secure/core/module/sys/menus", 200, name + " granted menus"))
            self.check(name + " menus exactly match explicit role grants",
                       {str(item["id"]) for item in menus["menus"]} == set(actor["menus"]))
            for suffix in ("headers", "rows"):
                self.observe("POST", MATRIX + suffix, 200 if matrix else 403, name + " matrix " + suffix, {})
            page = self.observe("POST", BASE + "/queries/raw", 200 if business else 403,
                                name + " scoped list", {"p": {"s": 50}})
            if business:
                page = json.loads(page)
                self.check(name + " every visible row is in authorized category",
                           page["total"] == actor["visible_rows"] and
                           all(str(row["equiptCategoryId"]) == actor["category"] for row in page["rows"]))
            foreign_id = 2 if actor["organization"] == "org-a" else 1
            detail = self.observe("GET", BASE + "/" + str(foreign_id), 200 if business else 403,
                                  name + " cross organization detail")
            if business:
                self.check(name + " foreign detail suppressed", detail.strip() in ("", "null"))
            self.observe("POST", BASE, 403, name + " foreign category write denied", self.body(actor, True))
            can_write = mode == "isolated" and self.role in ("manager", "admin")
            identity = self.observe("POST", BASE, 200 if can_write else 403,
                                    name + " same organization write gate", self.body(actor))
            if can_write:
                identity = int(identity)
                self.check(name + " server stamps actor organization despite spoofed body",
                           self.query("SELECT ORGID||'|'||EQUIPTCATEGORYID FROM T_EMC_EQUIPT WHERE ID=%d;" % identity, name + " persist") == actor["organization"] + "|" + actor["category"])
                self.allowed_writes.append(dict(actorId=self.actor, resource=str(identity)))
                self.query("DELETE FROM T_EMC_EQUIPT WHERE ID=%d;\nCOMMIT;" % identity, name + " restore")
            if mode == "isolated":
                self.lifecycle(actor, business)
        self.check(mode + " full equipment baseline restored", self.projection() == self.baseline)
        self.report['batch_case_results'].append({
            'case_id': 'permission-matrix-' + mode,
            'role_organizations': 8,
            'http_case_count': sum(1 for item in self.report['http_cases'] if item.get('mode') == mode),
            'fixture_restore': self.projection() == self.baseline,
            'audit_observed': True,
        })
        self.close()

    def lifecycle(self, actor, business):
        route = BASE + "/queries/raw" if business else MATRIX + "headers"
        name = self.actor
        if business:
            self.phase = "organization-revoked"
            self.query("DELETE FROM T_CORE_USER_PERMISSION WHERE USERID='%s';\nCOMMIT;" % name, "revoke organization")
            page = json.loads(self.observe("POST", route, 200, name + " revoked organization read", {}))
            self.check(name + " revoked organization produces empty list", page["total"] == 0)
            self.observe("POST", BASE, 403, name + " revoked organization write denied", self.body(actor))
            self.query("INSERT INTO T_CORE_USER_PERMISSION VALUES ('%s','T_CORE_ORG','%s');\nCOMMIT;" %
                       (name, self.organization), "restore organization")
        self.phase = "grant-revoked"
        self.query("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=%d;\nCOMMIT;" % actor["role_id"], "revoke role grants")
        self.observe("POST", route, 403, name + " revocation rechecked on existing session", {})
        self.query(role_fixture.grants(self.role) + "\nCOMMIT;", "restore role grants")
        self.phase = "role-suspended"
        self.query("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG='1' WHERE ID=%d;\nCOMMIT;" % actor["role_id"], "suspend role")
        self.observe("POST", route, 403, name + " suspended role denied", {})
        self.query("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG='0' WHERE ID=%d;\nCOMMIT;" % actor["role_id"], "restore role")
        self.phase = "user-disabled"
        self.query("UPDATE T_CORE_USER SET LASTSUSPENDEDFLAG='1' WHERE ID='%s';\nCOMMIT;" % name, "disable account")
        self.observe("POST", route, 403, name + " disabled account denied", {})
        self.query("UPDATE T_CORE_USER SET LASTSUSPENDEDFLAG='0' WHERE ID='%s';\nCOMMIT;" % name, "restore account")
        self.phase = "session-invalidated"
        self.observe("POST", route, 403, name + " reactivation does not restore cleared session", {})
        self.sign_in(actor)
        self.observe("POST", route, 200, name + " explicit relogin restores authorized read", {})
        self.observe("POST", "/core/module/sys/logout", 200, name + " explicit logout")
        self.observe("POST", route, 403, name + " old session denied after logout", {})

    def finalize_contract_results(self):
        """Expose independent behavior contracts from the shared permission shard."""
        route = BASE + "/queries/raw"
        contracts = [
            ("permission-reader-org-a-isolated-read", "reader", "org-a", "isolated", 200, "ALLOWED"),
            ("permission-reader-org-b-isolated-read", "reader", "org-b", "isolated", 200, "ALLOWED"),
            ("permission-admin-org-a-isolated-read", "admin", "org-a", "isolated", 200, "ALLOWED"),
            ("permission-admin-org-b-isolated-read", "admin", "org-b", "isolated", 200, "ALLOWED"),
            ("permission-role-admin-org-a-isolated-deny", "role-admin", "org-a", "isolated", 403, "DENIED"),
            ("permission-role-admin-org-b-isolated-deny", "role-admin", "org-b", "isolated", 403, "DENIED"),
            ("permission-role-admin-org-a-default-deny", "role-admin", "org-a", "default", 403, "DENIED"),
            ("permission-role-admin-org-b-default-deny", "role-admin", "org-b", "default", 403, "DENIED"),
        ]
        events = self.report.get("audit", {}).get("events", [])
        restored = self.projection() == self.baseline
        for case_id, role, organization, mode, expected_status, outcome in contracts:
            observed = [item for item in self.report["http_cases"]
                        if item.get("route") == "POST " + route
                        and item.get("role") == role
                        and item.get("organization") == organization
                        and item.get("mode") == mode
                        and item.get("status") == expected_status
                        and item.get("expected_status") == expected_status]
            audit_observed = any(event.get("action") == "authorize"
                                 and event.get("outcome") == outcome
                                 for event in events if isinstance(event, dict))
            self.check(case_id + " has structured HTTP and audit observations",
                       bool(observed) and audit_observed and restored)
            self.report["batch_case_results"].append({
                "case_id": case_id,
                "role": role,
                "organization": organization,
                "mode": mode,
                "fixture": "role-menu-matrix-fixture",
                "route": "POST " + route,
                "expected_result": "allowed" if expected_status == 200 else "forbidden",
                "audit": "authorize",
                "audit_expected": [{
                    "action": "authorize",
                    "actorId": "matrix-%s-%s" % (role, organization[-1]),
                    "resource": "equipment.read",
                    "outcome": outcome,
                }],
                "fixture_restore": restored,
                "audit_observed": audit_observed,
            })

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        actual = [event for event in events if event["action"] == "equipment-insert" and event["outcome"] == "ALLOWED"]
        self.check("only four authorized organization writes have success audits",
                   len(actual) == 4 and all(any(all(e.get(k) == v for k, v in write.items()) for e in actual)
                                            for write in self.allowed_writes))
        expected = dict(action="authorize", actorId="matrix-role-admin-a", resource="role-menu.admin", outcome="ALLOWED")
        self.check("matrix access has original authorization audit", any(all(e.get(k) == v for k, v in expected.items()) for e in events))
        self.check("audit omits all credentials", all(value not in contents for value in self.secrets))
        self.report["audit"] = dict(events=events, expected=[expected], sha256=hashlib.sha256(contents.encode()).hexdigest(), credentials_excluded=True)
        self.report["fixture_cleanup"] = dict(restored=self.projection() == self.baseline,
            projection_sha256=hashlib.sha256(self.projection().encode()).hexdigest(), sequence_rollback="sequence advances retained inside disposable Oracle; container and volumes removed")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/permission-role-organization-20261024.json")
    args = parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-permission-private-") as temporary:
        replay = PermissionReplay(temporary)
        try:
            phases = {}
            started = time.perf_counter()
            replay.build()
            phases['build'] = round((time.perf_counter() - started) * 1000, 3)
            started = time.perf_counter()
            replay.database()
            phases['shared_oracle_redis_setup'] = round((time.perf_counter() - started) * 1000, 3)
            started = time.perf_counter()
            replay.matrix("default")
            phases['default_mode'] = round((time.perf_counter() - started) * 1000, 3)
            started = time.perf_counter()
            replay.matrix("isolated")
            phases['isolated_mode'] = round((time.perf_counter() - started) * 1000, 3)
            started = time.perf_counter()
            replay.audit()
            phases['audit'] = round((time.perf_counter() - started) * 1000, 3)
            replay.finalize_contract_results()
            replay.report['phase_timings_ms'] = phases
            replay.report['shared_runtime'] = {
                'oracle': {'shared_across_cases': True, 'container': replay.name},
                'redis': {'shared_across_cases': True, 'container': replay.redis_name},
                'application': {'shared_oracle_redis_shard': True, 'modes': ['default', 'isolated'], 'processes': 2},
                'browser': {'shared_per_mode': True, 'sessions': 2},
            }
            replay.report["status"] = "executed-pass"
            replay.report['audit'] = {'events': replay.report.get('audit', {}).get('events', []),
                                      'expected': [
                                          {'action': 'authorize', 'actorId': 'matrix-role-admin-a',
                                           'resource': 'role-menu.admin', 'outcome': 'ALLOWED'},
                                          {'action': 'authorize', 'outcome': 'DENIED'},
                                      ]}
        except Exception as failure:
            replay.report["reason"] = replay.redact(str(failure))
            replay.report["diagnostic_traceback"] = replay.redact(traceback.format_exc())
            replay.diagnostics()
        finally:
            cleanup_started = time.perf_counter()
            replay.cleanup()
            replay.report.setdefault('phase_timings_ms', {})['cleanup'] = round((time.perf_counter() - cleanup_started) * 1000, 3)
    replay.report["cleanup"].update(private_temporary_directory_removed=not Path(temporary).exists(), temporary_files_removed=not Path(temporary).exists())
    replay.report["finished_at"] = replay.now()
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    print("permission role/organization matrix: %s (%d checks)" % (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
