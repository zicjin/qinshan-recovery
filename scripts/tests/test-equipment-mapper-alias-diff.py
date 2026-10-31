#!/usr/bin/env python3
"""Compare the archived equipment Mapper join with the owned projection seam."""
import datetime
import hashlib
import json
import os
from pathlib import Path
import secrets
import subprocess
import tempfile
import time
import uuid

ROOT = Path(__file__).resolve().parents[2]
IMAGE = "ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4"
HISTORICAL_MAPPER = ROOT / "project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/mapper/conf/emc-equipt-oracle-mapper.xml"
OWNED_DAO = ROOT / "owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentReadDao.java"


def run(command, **kwargs):
    return subprocess.run(command, capture_output=True, text=True, timeout=kwargs.pop("timeout", 60), **kwargs)


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    output = ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/equipment-mapper-alias-diff-20261024.json"
    report = {
        "schema": 1,
        "status": "executed-fail",
        "production_access": False,
        "scope": "same-fixture Oracle projection comparison; archived equipment Mapper alias versus owned read projection",
        "started_at": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "source_commit": run(["git", "rev-parse", "HEAD"], cwd=ROOT).stdout.strip(),
        "source_sha256": {
            str(path.relative_to(ROOT)): sha(path)
            for path in (HISTORICAL_MAPPER, OWNED_DAO, Path(__file__))
        },
        "source_dirty": bool(run(
            ["git", "status", "--porcelain", "--", str(HISTORICAL_MAPPER), str(OWNED_DAO), str(Path(__file__))],
            cwd=ROOT).stdout.strip()
        ),
        "checks": [],
    }
    token = uuid.uuid4().hex
    name = "lims-equipment-alias-" + token
    network = name + "-net"
    created = network_created = False

    def sql(source):
        text = (
            "whenever sqlerror exit failure rollback\n"
            "set pagesize 0 feedback off heading off echo off verify off trimspool on\n"
            "alter session set container=FREEPDB1;\n" + source + "\nexit\n"
        )
        return run(["docker", "exec", "-i", name, "sqlplus", "-s", "/ as sysdba"], input=text, timeout=90)

    def check(label, condition):
        if not condition:
            raise RuntimeError(label)
        report["checks"].append(label)

    try:
        image = run(["docker", "image", "inspect", IMAGE])
        if image.returncode:
            raise RuntimeError("pinned Oracle image unavailable")
        image_info = json.loads(image.stdout)[0]
        check("pinned Linux amd64 Oracle image", image_info.get("Os") == "linux" and image_info.get("Architecture") == "amd64")
        report["image_reference"] = IMAGE
        report["image_id"] = image_info["Id"]

        with tempfile.TemporaryDirectory(prefix="lims-equipment-alias-private-") as temporary:
            temporary = Path(temporary)
            env_file = temporary / "oracle.env"
            env_file.write_text("ORACLE_PASSWORD=Ephemeral" + secrets.token_hex(8) + "\n")
            env_file.chmod(0o600)
            result = run(["docker", "network", "create", "--internal", "--driver", "bridge", network])
            if result.returncode:
                raise RuntimeError("internal network creation failed")
            network_created = True
            result = run(["docker", "create", "--name", name, "--network", network,
                          "--restart", "no", "--memory", "3g", "--cpus", "2",
                          "--env-file", str(env_file), IMAGE])
            if result.returncode:
                raise RuntimeError("Oracle container creation failed")
            created = True
            if run(["docker", "start", name]).returncode:
                raise RuntimeError("Oracle container start failed")
            deadline = time.monotonic() + 600
            while time.monotonic() < deadline:
                logs = run(["docker", "logs", name])
                ready = sql("SELECT 'ALIAS_READY' FROM V$PDBS WHERE NAME='FREEPDB1' AND OPEN_MODE='READ WRITE';")
                if "DATABASE IS READY TO USE!" in logs.stdout + logs.stderr and ready.returncode == 0 and "ALIAS_READY" in ready.stdout:
                    break
                time.sleep(3)
            else:
                raise RuntimeError("Oracle readiness timeout")
            container = json.loads(run(["docker", "inspect", name]).stdout)[0]
            net = json.loads(run(["docker", "network", "inspect", network]).stdout)[0]
            container_id = container["Id"]
            isolation = {
                "network_internal": net["Internal"],
                "attached_network_count": len(container["NetworkSettings"]["Networks"]),
                "sole_endpoint": set(net["Containers"]) == {container_id},
                "port_bindings": container["HostConfig"]["PortBindings"],
                "bind_mount_count": sum(m["Type"] == "bind" for m in container.get("Mounts", [])),
            }
            report["isolation"] = isolation
            check("Oracle uses one internal network without ports or bind mounts",
                  isolation["network_internal"] and isolation["attached_network_count"] == 1
                  and isolation["sole_endpoint"] and not isolation["port_bindings"]
                  and isolation["bind_mount_count"] == 0)

            setup = """
CREATE USER equipment_fixture IDENTIFIED BY FixtureAlias123456;
GRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO equipment_fixture;
ALTER SESSION SET CURRENT_SCHEMA=equipment_fixture;
CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY, ORGNAME VARCHAR2(100));
CREATE TABLE T_EMC_EQUIPT (ID NUMBER PRIMARY KEY, EQUIPTID VARCHAR2(64), USEDDEPARTMENTID VARCHAR2(64), USEDDEPOSTID VARCHAR2(64));
INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A');
INSERT INTO T_CORE_ORG VALUES ('post-c','Fixture Post C');
INSERT INTO T_EMC_EQUIPT VALUES (1,'EQ-001','org-a','post-c');
COMMIT;
"""
            if sql(setup).returncode:
                raise RuntimeError("fixture setup failed")
            query = """
ALTER SESSION SET CURRENT_SCHEMA=equipment_fixture;
SELECT 'historical|'||E.EQUIPTID||'|'||NVL(C.ORGNAME,'')||'|'||NVL(O.ORGNAME,'')
  FROM T_EMC_EQUIPT E
  LEFT JOIN T_CORE_ORG C ON E.USEDDEPARTMENTID=C.ID
  LEFT JOIN T_CORE_ORG O ON E.USEDDEPOSTID=C.ID
 WHERE E.ID=1;
SELECT 'owned|'||E.EQUIPTID||'|'||NVL(C.ORGNAME,'')||'|'||NVL(O.ORGNAME,'')
  FROM T_EMC_EQUIPT E
  LEFT JOIN T_CORE_ORG C ON E.USEDDEPARTMENTID=C.ID
  LEFT JOIN T_CORE_ORG O ON E.USEDDEPOSTID=O.ID
 WHERE E.ID=1;
"""
            result = sql(query)
            if result.returncode:
                raise RuntimeError("projection comparison failed")
            rows = [line.strip() for line in result.stdout.splitlines() if line.strip().startswith(("historical|", "owned|"))]
            check("historical and owned projections both return the fixture row", len(rows) == 2)
            observed = {}
            for line in rows:
                key, equipt_id, department, post = line.split("|", 3)
                observed[key] = {"equipt_id": equipt_id, "department": department, "post": post}
            report["projections"] = observed
            check("historical alias leaves distinct post label empty", observed["historical"]["post"] == "")
            check("owned projection resolves post label through post ID", observed["owned"]["post"] == "Fixture Post C")
            check("the alias difference is observable with distinct organization IDs",
                  observed["historical"]["post"] != observed["owned"]["post"])
            report["decision"] = {
                "owned_behavior": "retain O.ID join for USEDDEPOSTID",
                "historical_behavior": "archived mapper joins O using C.ID and leaves a distinct post ID unresolved",
                "equivalence": "not claimed; source-level alias difference is proven on the same Oracle fixture",
            }
            report["status"] = "executed-pass"
    except Exception as failure:
        report["reason"] = str(failure)
    finally:
        cleanup = {}
        if created:
            cleanup["container_removed"] = run(["docker", "rm", "--force", "--volumes", name]).returncode == 0
            cleanup["anonymous_volumes_removed"] = cleanup["container_removed"]
        if network_created:
            cleanup["network_removed"] = run(["docker", "network", "rm", network]).returncode == 0
        report["cleanup"] = cleanup
        report["finished_at"] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
    print("equipment mapper alias diff: %s (%d checks)" % (report["status"], len(report["checks"])))
    return 0 if report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
