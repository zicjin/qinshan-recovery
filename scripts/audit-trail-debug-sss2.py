#!/usr/bin/env python3
"""Replay the owned audit-trail query seam on the long-lived sss2 runtime.

This is deliberately diagnostic.  It reuses the existing owned Oracle,
Redis, supplier 18081 and debug application, creates only synthetic audit
records/lines, and removes those rows before returning.  The large source
T_CORE_AT_RECORD_LINE data set is not migrated; an empty compatibility table
is created when needed so the original endpoint can be exercised safely.
"""
import argparse
import base64
import datetime as dt
import hashlib
import json
from pathlib import Path
import os
import re
import shlex
import shutil
import subprocess
import sys
import tempfile
import uuid

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


tmp = Path(tempfile.mkdtemp(prefix="audit-trail-debug-"))
tmp.chmod(0o700)
cred = tmp / "db-credentials"
cred.touch(mode=0o600)
fixture_created = False
record_ids = []
line_ids = []
synthetic_table_created = False


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
        "set heading off feedback off pagesize 0 linesize 400 trimspool on\n"
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
        raise RuntimeError((result.stdout + result.stderr)[-3000:])
    return result.stdout


def numbers(output):
    return [int(value) for value in re.findall(r"(?m)^\s*([0-9]+)\s*$", output)]


def ensure_line_table(user, password):
    output = sql(user, password, [
        "declare",
        "  present number;",
        "begin",
        "  select count(*) into present from user_tables where table_name='T_CORE_AT_RECORD_LINE';",
        "  if present=0 then",
        "    execute immediate 'create table T_CORE_AT_RECORD_LINE (ID NUMBER NOT NULL, RECORDID NUMBER NOT NULL, TARGETID VARCHAR2(129 BYTE), RECORDTYPE VARCHAR2(12 BYTE), \"COLUMN\" VARCHAR2(60 BYTE), OLDVALUE CLOB, NEWVALUE CLOB, TABLENAME VARCHAR2(192 BYTE), CREATEDBYID VARCHAR2(36 BYTE), CREATEDBYNAME VARCHAR2(60 BYTE), CREATEDTIME DATE, CREATEDBYORGID VARCHAR2(36 BYTE), CREATEDBYORGNAME VARCHAR2(270 BYTE))';",
        "    execute immediate 'alter table T_CORE_AT_RECORD_LINE add constraint PK_T_CORE_AT_RECORD_LINE primary key (ID)';",
        "    execute immediate 'create index IDX_CORE_ATRECORDLINE_RECORDID on T_CORE_AT_RECORD_LINE (RECORDID)';",
        "  end if;",
        "end;",
        "/",
        "select count(*) from user_tables where table_name='T_CORE_AT_RECORD_LINE';",
    ])
    return numbers(output)[-1] == 1


def cleanup(user, password):
    if not record_ids and not line_ids:
        return {"attempted": False, "records_remaining": None, "lines_remaining": None}
    try:
        if record_ids:
            sql(user, password, [
                "delete from T_CORE_AT_RECORD_LINE where RECORDID in (%s);" % ",".join(map(str, record_ids)),
                "delete from T_CORE_AT_RECORD where ID in (%s);" % ",".join(map(str, record_ids)),
            ])
        checks = sql(user, password, [
            "select count(*) from T_CORE_AT_RECORD where ID in (%s);" % ",".join(map(str, record_ids)),
            "select count(*) from T_CORE_AT_RECORD_LINE where RECORDID in (%s);" % ",".join(map(str, record_ids)),
        ])
        values = numbers(checks)
        return {
            "attempted": True,
            "records_remaining": values[-2] if len(values) >= 2 else None,
            "lines_remaining": values[-1] if values else None,
        }
    except Exception as failure:
        return {"attempted": True, "error": str(failure),
                "records_remaining": None, "lines_remaining": None}


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
        "numberOfElements": parsed.get("numberOfElements") if isinstance(parsed, dict) else None,
        "row_ids": [row.get("id") for row in rows if isinstance(row, dict)] if isinstance(rows, list) else [],
        "rows": rows if isinstance(rows, list) else [],
        "error": error[-200:] if status == 0 and error else None,
    }


user, password = read_db_credentials()
try:
    synthetic_table_created = ensure_line_table(user, password)
    nonce = 990100000000000 + int(uuid.uuid4().int % 1000000)
    record_ids = [nonce, nonce + 1]
    line_ids = [nonce + 100]
    sql(user, password, [
        "insert into T_CORE_AT_RECORD (ID,AUDITTRAILTYPE,AUDITTRAILNAME,\"COMMENT\",CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,TARGETID) values (%s,'UPDATE','Diagnostic audit','owned diagnostic','00001','Test',sysdate,'dept_2054','Org A','T_EMC_REAGENT$diag-a');" % record_ids[0],
        "insert into T_CORE_AT_RECORD (ID,AUDITTRAILTYPE,AUDITTRAILNAME,\"COMMENT\",CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,TARGETID) values (%s,'UPDATE','Hidden audit','hidden diagnostic','00002','Other',sysdate,'dept_4','Org B','T_EMC_REAGENT$diag-b');" % record_ids[1],
        "insert into T_CORE_AT_RECORD_LINE (ID,RECORDID,TARGETID,RECORDTYPE,\"COLUMN\",OLDVALUE,NEWVALUE,TABLENAME,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) values (%s,%s,'T_EMC_REAGENT$diag-a','UPDATE','REAGENTNAME','old','new','T_EMC_REAGENT','00001','Test',sysdate,'dept_2054','Org A');" % (line_ids[0], record_ids[0]),
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
        "records_all": query(probe, "/secure/core/module/sys/at-records/queries", {}),
        "records_owned": query(probe, "/secure/core/module/sys/at-records/queries",
                                {"p": {"targetId_SEQ": "T_EMC_REAGENT$diag-a"}}),
        "records_filter_envelope": query(probe, "/secure/core/module/sys/at-records/queries",
                                          {"p": {"f": {"targetId_SEQ": "T_EMC_REAGENT$diag-a"}}}),
        "lines_owned": query(probe, "/secure/core/module/sys/at-records/record-lines/queries",
                              {"p": {"recordId_SEQ": str(record_ids[0])}}),
        "lines_filter_envelope": query(probe, "/secure/core/module/sys/at-records/record-lines/queries",
                                        {"p": {"f": {"recordId_SEQ": str(record_ids[0])}}}),
        "lines_without_record": query(probe, "/secure/core/module/sys/at-records/record-lines/queries", {}),
    }
    print(json.dumps({
        "record_ids": record_ids,
        "line_ids": line_ids,
        "synthetic_table_created": synthetic_table_created,
        "key_status": key_status,
        "login_status": login_status,
        "cases": cases,
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
        raise RuntimeError("sss2 audit diagnostic failed: " + result.stderr[-1200:])
    payload_line = None
    restore_line = None
    for line in result.stdout.splitlines():
        if line.startswith("FIXTURE_RESTORE="):
            restore_line = json.loads(line.split("=", 1)[1])
        elif line.strip().startswith("{"):
            payload_line = line.strip()
    if payload_line is None:
        raise RuntimeError("sss2 audit diagnostic returned no payload")
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
        ROOT / "owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedAuditTrailController.java",
        ROOT / "owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpBoundary.java",
        ROOT / "owned-framework/src/main/java/com/sunwayworld/owned/internal/web/QueryRequest.java",
        ROOT / "owned-framework/src/main/java/com/sunwayworld/owned/internal/data/JdbcValue.java",
    ]
    source_sha256 = {
        str(path.relative_to(ROOT)): hashlib.sha256(path.read_bytes()).hexdigest()
        for path in source_paths
    }
    source_dirty = bool(subprocess.check_output(
        ["git", "status", "--porcelain", "--"] + [str(path) for path in source_paths],
        cwd=ROOT, universal_newlines=True,
    ).strip())
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
        "started_at": dt.datetime.now(dt.timezone.utc).isoformat(),
    }
    try:
        observed = collect(args.host, args.port, args.username, read_password(args.password_file))
        report["observed"] = observed
        cases = observed.get("cases", {})
        records_owned = cases.get("records_owned", {})
        lines_owned = cases.get("lines_owned", {})
        conditions = [
            observed.get("synthetic_table_created") is True,
            observed.get("key_status") == 200,
            observed.get("login_status") == 200,
            records_owned.get("status") == 200,
            cases.get("records_filter_envelope", {}).get("status") == 200,
            lines_owned.get("status") == 200,
            cases.get("lines_filter_envelope", {}).get("status") == 200,
            records_owned.get("total") == 1,
            cases.get("records_filter_envelope", {}).get("total") == 1,
            lines_owned.get("total") == 1,
            cases.get("lines_filter_envelope", {}).get("total") == 1,
            lines_owned.get("row_ids") == observed.get("line_ids"),
            cases.get("lines_filter_envelope", {}).get("row_ids") == observed.get("line_ids"),
            cases.get("lines_without_record", {}).get("status") == 400,
            observed.get("fixture_restore", {}).get("records_remaining") == 0,
            observed.get("fixture_restore", {}).get("lines_remaining") == 0,
        ]
        report["assertions"] = {
            "synthetic_compatibility_table_present": conditions[0],
            "authentication_succeeded": conditions[1] and conditions[2],
            "owned_record_query_http_200": conditions[3],
            "filter_envelope_record_query_http_200": conditions[4],
            "owned_line_query_http_200": conditions[5],
            "filter_envelope_line_query_http_200": conditions[6],
            "organization_scoped_record_cardinality": conditions[7] and conditions[8],
            "line_cardinality": conditions[9] and conditions[10],
            "line_fixture_visible": conditions[11] and conditions[12],
            "missing_record_id_rejected": conditions[13],
            "fixture_restored": conditions[14] and conditions[15],
        }
        report["status"] = "diagnostic-pass" if all(conditions) and not source_dirty else "diagnostic-fail"
    except Exception as failure:
        report["error"] = str(failure)
    report["finished_at"] = dt.datetime.now(dt.timezone.utc).isoformat()
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
    print("audit trail diagnostic: %s (%s)" % (report["status"], args.output))
    if report["status"] != "diagnostic-pass":
        raise SystemExit(1)


if __name__ == "__main__":
    main()
