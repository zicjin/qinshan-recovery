#!/usr/bin/env python3
"""Replay the owned reagent query family on the long-lived sss2 debug app.

This runner is deliberately diagnostic.  It reuses the existing Oracle,
Redis, supplier instance and owned debug application, inserts only synthetic
reagent/composition rows, and removes exactly those rows before returning.
It never registers a strict matrix pass.
"""
import argparse
import base64
import datetime as dt
import hashlib
import json
import os
from pathlib import Path
import shlex
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]

REMOTE_CODE = r'''import base64
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import tempfile
import uuid


def run(command, **kwargs):
    return subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                          universal_newlines=True, **kwargs)


tmp = Path(tempfile.mkdtemp(prefix="reagent-query-debug-"))
tmp.chmod(0o700)
cred = tmp / "db-credentials"
cred.touch(mode=0o600)
fixture_created = False
ids = []
composition_id = None


def read_db_credentials():
    with cred.open("w") as output:
        result = run([
            "docker", "exec", "lims-owned-sss2-debug-app", "sh", "-c",
            "grep -E '^(spring.datasource.username|spring.datasource.password)' "
            "/run/config/application.properties",
        ], check=True)
        output.write(result.stdout)
    values = {}
    for line in cred.read_text().splitlines():
        key, value = line.split("=", 1)
        values[key] = value
    return values["spring.datasource.username"], values["spring.datasource.password"]


def sql(user, password, statements):
    text = (
        "whenever sqlerror exit failure rollback\n"
        "set heading off feedback off pagesize 0 linesize 300 trimspool on\n"
        "alter session set container= XEPDB1;\n"
        "connect %s/%s@//localhost/XEPDB1\n" % (user, password)
    )
    text += "\n".join(statements)
    text += "\ncommit;\nexit\n"
    result = run([
        "docker", "exec", "-i", "lims-owned-sss2-oracle", "sh", "-c",
        "export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE; "
        "export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s /nolog",
    ], input=text, check=False)
    if result.returncode:
        raise RuntimeError((result.stdout + result.stderr)[-2000:])
    return result.stdout


def cleanup(user, password):
    if not ids or composition_id is None:
        return {"attempted": False, "reagent_rows_remaining": None,
                "composition_rows_remaining": None}
    try:
        sql(user, password, [
            "delete from T_EMC_REAGENT_COMPOSITION where ID=%s;" % composition_id,
            "delete from T_EMC_REAGENT where ID in (%s);" % ",".join(map(str, ids)),
        ])
        verify = sql(user, password, [
            "select count(*) from T_EMC_REAGENT where ID in (%s);" % ",".join(map(str, ids)),
            "select count(*) from T_EMC_REAGENT_COMPOSITION where ID=%s;" % composition_id,
        ])
        numbers = [int(value) for value in re.findall(r"(?m)^\s*([0-9]+)\s*$", verify)]
        return {
            "attempted": True,
            "reagent_rows_remaining": numbers[-2] if len(numbers) >= 2 else None,
            "composition_rows_remaining": numbers[-1] if numbers else None,
        }
    except Exception as failure:
        return {"attempted": True, "error": str(failure),
                "reagent_rows_remaining": None, "composition_rows_remaining": None}


def curl(root, path, method="GET", raw=None, headers=(), form=()):
    body = root / (hashlib.sha256((method + path + str(raw)).encode()).hexdigest() + ".body")
    cookies = root / "cookies"
    command = [
        "curl", "-ksS", "--max-time", "20", "-o", str(body), "-w", "%{http_code}",
        "-b", str(cookies), "-c", str(cookies),
    ]
    if method != "GET":
        command.extend(["-X", method])
    for header in headers:
        command.extend(["-H", header])
    if raw is not None:
        command.extend(["--data", raw])
    for key, value in form:
        command.extend(["--data-urlencode", key + "=" + value])
    command.append("http://127.0.0.1:%s%s" % (os.environ["AUTH_PORT"], path))
    result = run(command, check=False)
    try:
        status = int(result.stdout.strip() or "0")
    except ValueError:
        status = 0
    return status, body.read_bytes() if body.exists() else b"", result.stderr


def query(root, path, payload):
    status, body, error = curl(
        root, path, method="POST",
        raw=json.dumps(payload, separators=(",", ":")),
        headers=("Content-Type: application/json",),
    )
    try:
        parsed = json.loads(body.decode()) if body else {}
    except Exception:
        parsed = {}
    rows = parsed.get("rows") if isinstance(parsed, dict) else None
    return {
        "status": status,
        "total": parsed.get("total") if isinstance(parsed, dict) else None,
        "pageNumber": parsed.get("pageNumber") if isinstance(parsed, dict) else None,
        "pageSize": parsed.get("pageSize") if isinstance(parsed, dict) else None,
        "totalPages": parsed.get("totalPages") if isinstance(parsed, dict) else None,
        "numberOfElements": parsed.get("numberOfElements") if isinstance(parsed, dict) else None,
        "row_ids": [row.get("id") for row in rows if isinstance(row, dict)]
                   if isinstance(rows, list) else [],
        "rows": rows if isinstance(rows, list) else [],
        "error": error[-200:] if status == 0 and error else None,
    }


user, password = read_db_credentials()
try:
    nonce = 990000000000000 + int(uuid.uuid4().int % 1000000)
    ids = [nonce + offset for offset in range(3)]
    composition_id = nonce + 99
    sql(user, password, [
        "insert into T_EMC_REAGENT (ID,REAGENTNAME,PREPAREDATE,REAGENTSPECIFICATION,"
        "DISHWEIGHT,DISHREAGENTWEIGHT,REAGENTWEIGHT,CONSTANTVOLUME,PREPARECONCENT,"
        "APPLICABLEITEM,VALIDDATE,REMARK,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,"
        "CREATEDBYORGID,PREPAREBASIS,ENVIRONMENT,PREPAREVOLUME,SOLVENT,AUDITORID,"
        "AUDITORNAME,EQUIPTID,EQUIPTNAME,TEMPERATURE,HUMIDITY,METHODID,CREATEDBYORGNAME) "
        "values (%s,'Diag Acid A',date '2026-01-01','spec-a',1,2,3,4,'conc-a','item-a',"
        "date '2027-01-01','remark-a','00001','Test',sysdate,'dept_2054','basis-a',"
        "'env-a','vol-a','solvent-a','aud-1','Auditor A','eq-1','Equipment A','20','40',"
        "42,'Org A');" % ids[0],
        "insert into T_EMC_REAGENT (ID,REAGENTNAME,PREPAREDATE,REAGENTSPECIFICATION,"
        "METHODID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) "
        "values (%s,'Diag Acid B',date '2026-02-01','spec-b',42,'00001','Test',sysdate,"
        "'dept_2054','Org A');" % ids[1],
        "insert into T_EMC_REAGENT (ID,REAGENTNAME,METHODID,CREATEDBYID,CREATEDBYNAME,"
        "CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) values (%s,'Hidden Other Org',42,"
        "'00001','Test',sysdate,'dept_4','Other');" % ids[2],
        "insert into T_EMC_REAGENT_COMPOSITION (ID,REAGENTID,REAGENTNAME,REAGENTFACTORY,"
        "REAGENTBATCHNO,WEIGHTVOLUME,REMARK,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,"
        "CREATEDBYORGID,CREATEDBYORGNAME) values (%s,%s,'Diag Acid A','Factory A',"
        "'Batch A','10ml','comp-a','00001','Test',sysdate,'dept_2054','Org A');"
        % (composition_id, ids[0]),
    ])
    fixture_created = True

    probe = tmp / "probe"
    probe.mkdir()
    key_status, key_body, _ = curl(probe, "/open/security/public-key")
    (probe / "key.der").write_bytes(base64.b64decode(key_body.strip()))
    run([
        "openssl", "rsa", "-pubin", "-inform", "DER", "-in", str(probe / "key.der"),
        "-out", str(probe / "key.pem"),
    ], check=True)
    encrypted = subprocess.run([
        "openssl", "pkeyutl", "-encrypt", "-pubin", "-inkey", str(probe / "key.pem"),
        "-pkeyopt", "rsa_padding_mode:pkcs1",
    ], input=os.environ["AUTH_PASSWORD"].encode(), stdout=subprocess.PIPE, check=True).stdout
    encoded = base64.b64encode(encrypted).decode()
    login_status, _, _ = curl(
        probe, "/core/module/sys/login", method="POST",
        headers=("SECURITY-RSA-KEY: " + key_body.decode().strip(),
                 "Content-Type: application/x-www-form-urlencoded"),
        form=(("id", os.environ["AUTH_USER"]), ("password", encoded)),
    )
    cases = {
        "all": query(probe, "/secure/emc/module/bp/reagents/queries/raw", {}),
        "exact_owned": query(probe, "/secure/emc/module/bp/reagents/queries/raw",
                              {"p": {"f": {"ID_SEQ": ids[0]}}}),
        "method42": query(probe, "/secure/emc/module/bp/reagents/queries/raw",
                           {"p": {"methodId_SEQ": 42}}),
        "name_page2": query(probe, "/secure/emc/module/bp/reagents/queries/raw",
                             {"p": {"n": 2, "s": 1,
                                    "f": {"REAGENTNAME_CISC": "acid"},
                                    "o": [{"ID": "asc"}]}}),
        "no_match": query(probe, "/secure/emc/module/bp/reagents/queries/raw",
                           {"p": {"f": {"REAGENTNAME_CISC": "no-such-fixture"}}}),
        "composition": query(
            probe, "/secure/emc/module/bp/reagents/%s/compositions/queries" % ids[0], {}),
    }
    exact_rows = cases["exact_owned"]["rows"]
    extension = exact_rows[0].get("ext$") if exact_rows else None
    print(json.dumps({
        "ids": ids,
        "composition_id": composition_id,
        "key_status": key_status,
        "login_status": login_status,
        "cases": cases,
        "extension_keys": sorted(extension.keys()) if isinstance(extension, dict) else [],
        "extension_values": {
            key: extension.get(key) for key in (
                "preparebasis", "preparevolume", "solvent", "auditorname",
                "equiptname", "environment",
            )
        } if isinstance(extension, dict) else {},
        "hidden_org_visible": ids[2] in cases["all"]["row_ids"],
    }, ensure_ascii=True, separators=(",", ":")))
finally:
    restored = cleanup(user, password)
    print("FIXTURE_RESTORE=" + json.dumps(restored, sort_keys=True, separators=(",", ":")))
    shutil.rmtree(tmp, ignore_errors=True)
'''


def read_password(path):
    path = Path(path)
    if not path.is_file() or path.stat().st_mode & 0o077:
        raise SystemExit("password file must be a regular 0600 file")
    for line in path.read_text(encoding="utf-8").splitlines():
        if line.startswith("TEST_PASSWORD="):
            value = line.split("=", 1)[1]
            if value:
                return value
    raise SystemExit("password file has no TEST_PASSWORD entry")


def collect(host, port, username, password):
    command = (
        "export AUTH_USER=%s AUTH_PORT=%s AUTH_PASSWORD=%s\n"
        "python3 - <<'PY'\n%s\nPY\n"
    ) % (shlex.quote(username), shlex.quote(str(port)), shlex.quote(password), REMOTE_CODE)
    result = subprocess.run(
        ["ssh", "-o", "BatchMode=yes", "-o", "ConnectTimeout=10", host, "bash", "-s"],
        input=command, universal_newlines=True, stdout=subprocess.PIPE,
        stderr=subprocess.PIPE, timeout=180, check=False,
    )
    if result.returncode:
        raise RuntimeError("sss2 reagent diagnostic failed: " + result.stderr[-1000:])
    payload_line = None
    restore_line = None
    for line in result.stdout.splitlines():
        if line.startswith("FIXTURE_RESTORE="):
            restore_line = json.loads(line.split("=", 1)[1])
        elif line.strip().startswith("{"):
            payload_line = line.strip()
    if payload_line is None:
        raise RuntimeError("sss2 reagent diagnostic returned no payload")
    payload = json.loads(payload_line)
    payload["fixture_restore"] = restore_line or {"attempted": False}
    return payload


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--password-file", type=Path, required=True)
    parser.add_argument("--username", required=True)
    parser.add_argument("--host", default="sss2")
    parser.add_argument("--port", type=int, default=18088)
    args = parser.parse_args()

    source_paths = [
        Path(__file__),
        ROOT / "owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedReagentQueryController.java",
        ROOT / "owned-framework/src/main/java/com/sunwayworld/owned/internal/web/QueryRequest.java",
        ROOT / "owned-framework/src/main/java/com/sunwayworld/owned/internal/data/JdbcValue.java",
        ROOT / "scripts/frontend-authenticated-baseline-sss2.py",
    ]
    source_sha256 = {
        str(path.relative_to(ROOT)): hashlib.sha256(path.read_bytes()).hexdigest()
        for path in source_paths
    }
    source_dirty = bool(subprocess.check_output(
        ["git", "status", "--porcelain", "--"] + [str(path) for path in source_paths],
        cwd=ROOT, universal_newlines=True,
    ).strip())
    started = dt.datetime.now(dt.timezone.utc).isoformat()
    report = {
        "schema": 1,
        "status": "diagnostic-fail",
        "mode": "sss2-debug-reuse-authenticated-fixture",
        "strict_evidence": False,
        "production_access": False,
        "credentials_used": True,
        "credential_material_stored": False,
        "source_commit": subprocess.check_output(
            ["git", "rev-parse", "HEAD"], cwd=ROOT, universal_newlines=True
        ).strip(),
        "source_dirty": source_dirty,
        "source_sha256": source_sha256,
        "runtime": {
            "host": args.host,
            "owned_port": args.port,
            "supplier_port_preserved": 18081,
            "oracle_container": "lims-owned-sss2-oracle",
            "redis_container": "lims-owned-sss2-redis",
            "debug_app_container": "lims-owned-sss2-debug-app",
            "oracle_restarted": False,
            "redis_restarted": False,
            "supplier_restarted": False,
            "debug_app_restarted": False,
        },
        "started_at": started,
    }
    try:
        observed = collect(args.host, args.port, args.username, read_password(args.password_file))
        report["observed"] = observed
        cases = observed.get("cases", {})
        exact = cases.get("exact_owned", {})
        extension = observed.get("extension_values", {})
        expected_extension = {
            "preparebasis": "basis-a",
            "preparevolume": "vol-a",
            "solvent": "solvent-a",
            "auditorname": "Auditor A",
            "equiptname": "Equipment A",
            "environment": "env-a",
        }
        conditions = [
            observed.get("key_status") == 200,
            observed.get("login_status") == 200,
            all(cases.get(name, {}).get("status") == 200 for name in (
                "all", "exact_owned", "method42", "name_page2", "no_match", "composition",
            )),
            observed.get("ids", [None])[0] in exact.get("row_ids", []),
            not observed.get("hidden_org_visible", True),
            extension == expected_extension,
            exact.get("numberOfElements") == 1,
            cases.get("composition", {}).get("numberOfElements") == 1,
            observed.get("fixture_restore", {}).get("reagent_rows_remaining") == 0,
            observed.get("fixture_restore", {}).get("composition_rows_remaining") == 0,
        ]
        report["assertions"] = {
            "all_required_cases_http_200": conditions[2],
            "exact_owned_row_visible": conditions[3],
            "cross_organization_row_hidden": conditions[4],
            "extension_values_match": conditions[5],
            "exact_row_cardinality": conditions[6],
            "composition_row_cardinality": conditions[7],
            "fixture_restored": conditions[8] and conditions[9],
        }
        report["status"] = "diagnostic-pass" if all(conditions) and not source_dirty else "diagnostic-fail"
    except Exception as failure:
        report["error"] = str(failure)
    report["finished_at"] = dt.datetime.now(dt.timezone.utc).isoformat()
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
    print("reagent query diagnostic: %s (%s)" % (report["status"], args.output))
    if report["status"] != "diagnostic-pass":
        raise SystemExit(1)


if __name__ == "__main__":
    main()
