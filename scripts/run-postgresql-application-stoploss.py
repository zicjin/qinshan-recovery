#!/usr/bin/env python3
"""Run the owned login/query HTTP seam against a disposable PostgreSQL database.

This is a stop-loss experiment, not a PostgreSQL compatibility layer.  The
application is allowed to expose its first real incompatibility and the report
keeps only status/error-class markers, never credentials or unrestricted logs.
"""
import argparse
import datetime
import hashlib
import json
import os
from pathlib import Path
import secrets
import shutil
import zipfile
import subprocess
import tempfile
import time
import uuid

ROOT = Path(__file__).resolve().parents[1]
IMAGE = "public.ecr.aws/docker/library/postgres@sha256:0ea6700a3b4f0ae6ce746519073558aed4d88a79d8d07622a9a644946c7319c4"
JDK = Path(os.environ.get("JAVA_HOME", str(ROOT / "build/jdk1.8.0_391")))
PG_JAR = ROOT / "build/owned-contract-reference/postgresql-42.2.16.jar"
PG_SHA256 = "82230367c0e9507be45981ce2aa059f7291d906f56ad820d0bab3db0cf1523cb"
SQL = ROOT / "scripts/db-slice/postgresql-application.sql"
PROBE = ROOT / "scripts/db-slice/PostgresqlApplicationProbe.java"


def run(args, *, timeout=180, **kwargs):
    return subprocess.run([str(x) for x in args], capture_output=True, text=True,
                          timeout=timeout, **kwargs)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ROOT / "build/t15-postgresql-application-stoploss-20261020.json")
    args = parser.parse_args()
    output = args.output
    report = {
        "schema": 1,
        "status": "not-executed",
        "dialect": "postgresql",
        "image_reference": IMAGE,
        "scope": "owned application login, permission/session guard, personnel, equipment, consumables, report and menu HTTP attempts; disposable PostgreSQL only",
        "production_access": False,
        "final_mainline_decision": None,
        "application_compatibility": "not-observed",
        "started_at": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "checks": [],
    }
    name = "lims-pg-app-" + uuid.uuid4().hex
    network = name + "-net"
    created = network_created = False
    classes = None
    try:
        image = json.loads(run(["docker", "image", "inspect", IMAGE], timeout=30).stdout)[0]
        report["image_id"] = image["Id"]
        report["database_version"] = None
        if image.get("Os") != "linux" or image.get("Architecture") != "amd64":
            raise RuntimeError("pinned-image-not-linux-amd64")
        # Serialize owned builds because each build script owns framework/target.
        env = dict(os.environ)
        project = ROOT / "project"
        if not (project / "src/main/java").exists():
            project = Path("/home/ts/qinshan/lims-recovery/project")
        env["LIMS_PROJECT_ROOT"] = os.environ.get("LIMS_PROJECT_ROOT", str(project))
        with zipfile.ZipFile(ROOT / "materials/gikam-third-czjky-14.1.5-run.jar") as archive:
            driver = archive.read("BOOT-INF/lib/postgresql-42.2.16.jar")
        if hashlib.sha256(driver).hexdigest() != PG_SHA256:
            raise RuntimeError("postgresql-driver-hash-mismatch")
        PG_JAR.parent.mkdir(parents=True, exist_ok=True)
        PG_JAR.write_bytes(driver)
        report["postgresql_driver_sha256"] = PG_SHA256
        report["historical_source_commit"] = run(["git", "rev-parse", "HEAD"], cwd=env["LIMS_PROJECT_ROOT"]).stdout.strip()
        report["historical_source_dirty"] = bool(run(["git", "status", "--porcelain"], cwd=env["LIMS_PROJECT_ROOT"]).stdout.strip())
        for script in ("build-owned-personnel.sh", "build-owned-equipment.sh", "build-owned-consumables.sh", "build-owned-report.sh"):
            built = run([ROOT / "scripts" / script], timeout=300, env=env)
            if built.returncode:
                raise RuntimeError("build-failed-" + script)
        classes = Path(tempfile.mkdtemp(prefix="lims-pg-app-classes-"))
        compiled = run([JDK / "bin" / "javac", "-source", "8", "-target", "8", "-cp",
             ":".join([str(ROOT / "owned-framework/target/classes"),
                       str(ROOT / "owned-personnel/target/classes"),
                       str(ROOT / "owned-equipment/target/classes"),
                       str(ROOT / "owned-consumables/target/classes"),
                       str(ROOT / "owned-report/target/classes"),
                       str(ROOT / "owned-framework/target/public-deps/*"), str(PG_JAR)]),
             "-d", classes, PROBE], timeout=60)
        if compiled.returncode:
            raise RuntimeError("probe-compile-failed")
        runtime_sources = sorted(p for folder in ("owned-framework/src", "owned-personnel/src", "owned-equipment/src", "owned-consumables/src", "owned-report/src") for p in (ROOT / folder).rglob("*") if p.is_file() and "__pycache__" not in p.parts)
        runtime_sources += [SQL, PROBE, Path(__file__), ROOT / "scripts/prepare-owned-public-deps.py", ROOT / "scripts/reject-supplier-runtime.sh"]
        runtime_sources += sorted((ROOT / "scripts").glob("build-owned-*.sh"))
        report["source_commit"] = run(["git", "rev-parse", "HEAD"], cwd=ROOT).stdout.strip()
        report["source_dirty"] = bool(run(["git", "status", "--porcelain", "--"] + runtime_sources,
                                           cwd=ROOT).stdout.strip())
        report["source_sha256"] = {str(p.relative_to(ROOT)): digest(p) for p in runtime_sources}
        report["artifact_sha256"] = {str(p.relative_to(ROOT)): digest(p) for p in (
            ROOT / "owned-framework/target/lims-owned-framework-1.0.0.jar",
            ROOT / "owned-personnel/target/lims-owned-personnel-1.0.0.jar",
            ROOT / "owned-equipment/target/lims-owned-equipment-1.0.0.jar",
            ROOT / "owned-consumables/target/lims-owned-consumables-1.0.0.jar",
            ROOT / "owned-report/target/lims-owned-report-1.0.0.jar")}
        created_network = run(["docker", "network", "create", "--internal", "--driver", "bridge", network], timeout=30)
        if created_network.returncode:
            raise RuntimeError("internal-network-create-failed")
        network_created = True
        password = "Ephemeral" + secrets.token_hex(8)
        started = run(["docker", "run", "-d", "--name", name, "--network", network,
                       "--restart", "no", "--memory", "2g", "--cpus", "2",
                       "-e", "POSTGRES_PASSWORD=" + password, IMAGE], timeout=60)
        if started.returncode:
            raise RuntimeError("container-create-failed")
        created = True
        for _ in range(120):
            ready = run(["docker", "exec", name, "pg_isready", "-U", "postgres"], timeout=10)
            logs = run(["docker", "logs", name], timeout=10)
            if ready.returncode == 0 and "PostgreSQL init process complete; ready for start up." in logs.stdout + logs.stderr:
                break
            time.sleep(1)
        else:
            raise RuntimeError("database-not-ready")
        inspected = json.loads(run(["docker", "inspect", name], timeout=30).stdout)[0]
        netinfo = json.loads(run(["docker", "network", "inspect", network], timeout=30).stdout)[0]
        ip = next(iter(inspected["NetworkSettings"]["Networks"].values()))["IPAddress"]
        report["container_id"] = inspected["Id"]
        report["network_name"] = network
        report["network_mode"] = "isolated-internal"
        report["isolation"] = {
            "network_internal": bool(netinfo.get("Internal")),
            "sole_endpoint": set(netinfo.get("Containers", {})) == {inspected["Id"]},
            "port_bindings": inspected["HostConfig"].get("PortBindings"),
            "bind_mount_count": sum(m.get("Type") == "bind" for m in inspected.get("Mounts", [])),
            "host_mounts": [m.get("Destination") for m in inspected.get("Mounts", []) if m.get("Type") == "bind"],
        }
        if not report["isolation"]["network_internal"] or not report["isolation"]["sole_endpoint"] or report["isolation"]["port_bindings"] or report["isolation"]["bind_mount_count"]:
            raise RuntimeError("isolation-mismatch")
        seeded = run(["docker", "exec", "-i", name, "psql", "-U", "postgres", "-v", "ON_ERROR_STOP=1"], input=SQL.read_text(), timeout=60)
        if seeded.returncode:
            raise RuntimeError("schema-seed-failed")
        negative = run(["docker", "exec", name, "psql", "-U", "postgres", "-v", "ON_ERROR_STOP=1", "-v", "VERBOSITY=verbose", "-tAc", "select * from t_stoploss_missing_object"], timeout=30)
        report["negative_control"] = {
            "invalid_object_rejected": negative.returncode != 0,
            "sqlstate": "42P01" if "42P01" in negative.stderr else None,
            "error_class": "undefined-table" if "does not exist" in negative.stderr else "unknown",
        }
        if not report["negative_control"]["invalid_object_rejected"]:
            raise RuntimeError("negative-control-unexpected-success")
        version = run(["docker", "exec", name, "psql", "-U", "postgres", "-tAc", "select version()"], timeout=30)
        report["database_version"] = version.stdout.strip()
        with tempfile.TemporaryDirectory(prefix="lims-pg-app-private-") as tmp:
            tmp = Path(tmp)
            props = tmp / "jdbc.properties"
            props.write_text("url=jdbc:postgresql://" + ip + ":5432/postgres\nuser=postgres\npassword=" + password + "\n")
            props.chmod(0o600)
            audit = tmp / "audit.jsonl"
            cp = ":".join([str(classes), str(ROOT / "owned-framework/target/classes"),
                            str(ROOT / "owned-personnel/target/classes"), str(ROOT / "owned-equipment/target/classes"),
                            str(ROOT / "owned-consumables/target/classes"), str(ROOT / "owned-report/target/classes"),
                            str(ROOT / "owned-framework/target/public-deps/*"), str(PG_JAR)])
            java = run([JDK / "bin" / "java", "-cp", cp, "PostgresqlApplicationProbe", props, audit], timeout=180)
            markers = []
            for line in (java.stdout + "\n" + java.stderr).splitlines():
                if line.startswith("DIRECT|") or line.startswith("APP|") or line.startswith("HTTP|"):
                    markers.append(line[:500])
            report["application_markers"] = markers
            report["application_exit_code"] = java.returncode
            direct_sqlstate = next((x.split("SQLSTATE=", 1)[1].split(";", 1)[0]
                                    for x in markers if x.startswith("DIRECT|login|") and "SQLSTATE=" in x), None)
            report["application_compatibility"] = ("failed-at-login-sqlstate-" + direct_sqlstate
                                                     if direct_sqlstate else "login-failure-unclassified")
            report["final_mainline_decision"] = "oracle-fallback"
            report["checks"] = [{"name": "application-attempt", "status": "pass" if markers else "fail"},
                                {"name": "direct-login-reached-postgresql", "status": "pass" if any(x.startswith("DIRECT|login|") for x in markers) else "fail"},
                                {"name": "report-dao-reached-postgresql", "status": "pass" if any(x.startswith("DIRECT|report-dao|") for x in markers) else "fail"},
                                {"name": "rsa-key-contract", "status": "pass" if "HTTP|public-key|200|base64-x509" in markers else "fail"},
                                {"name": "http-login-attempted", "status": "pass" if any(x.startswith("HTTP|login|") for x in markers) else "fail"},
                                {"name": "personnel-http-attempted", "status": "pass" if any("/mdm/emp/emps/queries/raw" in x for x in markers) else "fail"},
                                {"name": "equipment-http-attempted", "status": "pass" if any("/mdm/equipt/equipts/queries/raw" in x for x in markers) else "fail"},
                                {"name": "consumables-http-attempted", "status": "pass" if any("/item/consume/consumes/queries/raw" in x for x in markers) else "fail"},
                                {"name": "report-http-attempted", "status": "pass" if any("report-configs/lines/1|403|REPORT_UNAUTHORIZED" in x for x in markers) else "fail"},
                                {"name": "anonymous-menu-denied", "status": "pass" if any("/menus|403|PERMISSION_UNAUTHORIZED" in x for x in markers) else "fail"}]
        report["status"] = "executed-pass" if all(x["status"] == "pass" for x in report["checks"]) else "executed-fail"
        report["reason"] = "application-stop-loss-first-incompatibility-retained"
    except Exception as failure:
        report["status"] = "executed-fail"
        report["reason"] = str(failure)
    finally:
        if classes is not None:
            shutil.rmtree(classes)
        if created:
            inspection = run(["docker", "inspect", name], timeout=30)
            if inspection.returncode == 0 and "container_id" not in report:
                report["container_id"] = json.loads(inspection.stdout)[0]["Id"]
            cleaned = run(["docker", "rm", "--force", "--volumes", name], timeout=30)
            report["cleanup"] = {"container_removed": cleaned.returncode == 0, "anonymous_volumes_removed": cleaned.returncode == 0}
        if network_created:
            report.setdefault("cleanup", {})["network_removed"] = run(["docker", "network", "rm", network], timeout=30).returncode == 0
        report["finished_at"] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
    print("postgresql application stop-loss: " + report["status"])
    return 0 if report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
