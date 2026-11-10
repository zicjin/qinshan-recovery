#!/usr/bin/env python3
"""Read-only verification of the local external logical-clone schema.

The command only inspects the named local Oracle container with SQL*Plus as
SYSDBA. It never creates users/objects, writes rows, creates links, or starts
containers. Business rows are intentionally required to remain zero until an
approved HTTP/source fixture exists.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import re
import subprocess
from typing import Mapping

ROOT = Path(__file__).resolve().parents[1]
TABLES = (
    "T_CRM_CONTRACT", "T_CRM_INVOICE_PROJ", "T_CRM_INVOICE",
    "T_CRM_PAY_PROJECT", "T_CRM_PAYMENT", "T_LIMS_CLEARANCEFORM",
    "T_LIMS_FOLDERS", "T_XFP_ORDER_TASK", "T_XFP_PROJECT", "T_XFP_REPORT",
)
IDENT = re.compile(r"^[A-Za-z][A-Za-z0-9_-]{0,62}$")


def sql_text() -> str:
    quoted = ",".join("'" + table + "'" for table in TABLES)
    return f"""set heading off feedback off pages 0 trimspool on
alter session set container=FREEPDB1;
select 'tables='||count(*) from all_tables where owner='LIMS_RUNTIME_LOCAL' and table_name in ({quoted});
select 'columns='||count(*) from all_tab_columns where owner='LIMS_RUNTIME_LOCAL' and table_name in ({quoted});
select 'constraints='||count(*) from all_constraints where owner='LIMS_RUNTIME_LOCAL' and table_name in ({quoted});
select 'indexes='||count(*) from all_indexes where owner='LIMS_RUNTIME_LOCAL' and table_name in ({quoted});
select 'rows='||nvl(sum(num_rows),0) from all_tables where owner='LIMS_RUNTIME_LOCAL' and table_name in ({quoted});
select 'links='||count(*) from all_db_links where owner='LIMS_RUNTIME_LOCAL';
exit
"""


def parse_receipt(text: str) -> Mapping[str, int]:
    result: dict[str, int] = {}
    for raw in text.splitlines():
        match = re.fullmatch(r"\s*(tables|columns|constraints|indexes|rows|links)=(\d+)\s*", raw)
        if match:
            result[match.group(1)] = int(match.group(2))
    return result


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--oracle-container", default="lims-local-runtime-oracle")
    args = parser.parse_args()
    if not IDENT.fullmatch(args.oracle_container):
        raise SystemExit("external-logical-clone-runtime: invalid container name")
    command = [
        "docker", "exec", "-i", "-e", "NLS_LANG=.AL32UTF8",
        args.oracle_container, "sh", "-c",
        'exec "$ORACLE_HOME/bin/sqlplus" -s / as sysdba',
    ]
    result = subprocess.run(command, input=sql_text(), text=True, capture_output=True, check=False, timeout=60)
    if result.returncode:
        raise SystemExit("external-logical-clone-runtime: SQL read failed: " + result.stderr[-1000:])
    if re.search(r"ORA-|SP2-", result.stdout + result.stderr):
        raise SystemExit("external-logical-clone-runtime: Oracle returned an error")
    receipt = parse_receipt(result.stdout)
    required = {"tables", "columns", "constraints", "indexes", "rows", "links"}
    if set(receipt) != required:
        raise SystemExit("external-logical-clone-runtime: incomplete read-only receipt")
    if receipt["tables"] != len(TABLES):
        raise SystemExit("external-logical-clone-runtime: table closure is incomplete")
    if receipt["rows"] != 0 or receipt["links"] != 0:
        raise SystemExit("external-logical-clone-runtime: business rows or DB links are present")
    print(json.dumps({
        "status": "pass", "container": args.oracle_container,
        "owner": "LIMS_RUNTIME_LOCAL", "container_name": "FREEPDB1",
        "tables": len(TABLES), "columns": receipt["columns"],
        "constraints": receipt["constraints"], "indexes": receipt["indexes"],
        "business_rows": receipt["rows"], "db_links": receipt["links"],
        "read_only": True,
    }, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
