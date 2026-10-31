#!/usr/bin/env python3
"""Replay equipment creation through the shipped distribution launcher.

This runner deliberately reuses the equipment browser contract, but starts the
application with ``owned-distribution/target/distribution/start.sh`` rather
than a test-only Java entry point.  The Oracle fixture and browser assertions
remain the same so the packaged and module-level contracts are directly
comparable.
"""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import importlib.util
import json
import os
from pathlib import Path
import socket
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[2]
SOURCE_SCOPES = (
    "owned-framework/src", "owned-personnel/src", "owned-equipment/src",
    "owned-consumables/src", "owned-attachments/src", "owned-report/src",
    "owned-scheduler/src", "owned-web/src", "owned-distribution/start.sh",
    "owned-distribution/package.py", "owned-distribution/tests/run-equipment-browser.py",
    "owned-distribution/example/owned-application.properties",
    "scripts/build-owned-framework.sh", "scripts/build-owned-personnel.sh",
    "scripts/build-owned-equipment.sh", "scripts/build-owned-consumables.sh",
    "scripts/build-owned-attachments.sh", "scripts/build-owned-report.sh",
    "scripts/build-owned-scheduler.sh", "scripts/build-owned-distribution.sh",
    "scripts/prepare-owned-public-deps.py", "scripts/reject-supplier-runtime.sh",
    "owned-web/build-owned-web.sh", "owned-web/original-personnel-sha256.json",
    "owned-web/original-inventory-sha256.json",
)


def load_base():
    path = ROOT / "owned-web/src/test/browser/run-equipment-browser.py"
    spec = importlib.util.spec_from_file_location("owned_equipment_browser_base", path)
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


BASE = load_base()
run = BASE.run
REDIS_IMAGE = "public.ecr.aws/docker/library/redis:7.4-alpine@sha256:57e4255cc971b1c8d6f8a6ca283ac01bc3ecdd914273ef6bfaede87aaecd6541"


class DistributionReplay(BASE.Replay):
    def __init__(self, temporary):
        super().__init__(temporary)
        self.redis_name = self.name + "-redis"
        self.redis_created = False
        self.redis_endpoint = None
        self.redis_image_id = None

    def build(self):
        BASE.SOURCE_SCOPES = SOURCE_SCOPES
        # The packaged runner owns the complete distribution build.  Do not
        # compile personnel/equipment/web first; build-owned-distribution.sh
        # already builds all seven modules and the web artifact exactly once.
        super().build(compile_modules=False)
        self.report["scope"] = (
            "owned distribution equipment browser through the shipped launcher; "
            "default-denied and isolated modes; synthetic Oracle and Redis only"
        )
        self.jdk = Path(os.environ.get("JAVA_HOME", str(ROOT / "build/jdk1.8.0_391")))
        self.passed(run([ROOT / "scripts/build-owned-distribution.sh"], timeout=1800,
                        env=dict(os.environ, OWNED_DISTRIBUTION_REUSE="1")),
                   "owned distribution build")
        self.record_built_artifacts()
        self.distribution = ROOT / "owned-distribution/target/distribution"
        self.check("distribution launcher and packaged application exist",
                   (self.distribution / "start.sh").is_file()
                   and (self.distribution / "app/lims-owned-application.jar").is_file()
                   and (self.distribution / "manifest.json").is_file())
        self.report["runner"] = str(Path(__file__).relative_to(ROOT))
        self.report["source_coverage"] = {
            "roots": list(SOURCE_SCOPES),
            "files": {},
            "diff": {"added": [], "deleted": []},
        }
        listing = self.passed(run(["git", "ls-files", "-z", "--", *SOURCE_SCOPES], cwd=ROOT),
                              "distribution source inventory")
        files = [ROOT / item for item in listing.split("\0") if item]
        self.report["source_sha256"].update({str(path.relative_to(ROOT)): BASE.digest(path) for path in files})
        self.report["source_coverage"]["files"] = {
            str(path.relative_to(ROOT)): BASE.digest(path) for path in files
        }
        self.report["distribution_manifest_sha256"] = BASE.digest(self.distribution / "manifest.json")
        self.report["distribution_artifact_sha256"] = {
            "app/lims-owned-application.jar": BASE.digest(
                self.distribution / "app/lims-owned-application.jar"),
            "manifest.json": BASE.digest(self.distribution / "manifest.json"),
        }

    def start(self, mode):
        self.mode = mode
        self.role = "anonymous"
        self.report.setdefault("runtime_configuration", {})[mode] = {
            "launcher": "owned-distribution/target/distribution/start.sh",
            "bind_address": "127.0.0.1",
            "equipment_write_property": "isolated" if mode == "isolated" else "unset",
            "redis_url": "redis://" + str(self.redis_endpoint) + ":6379",
            "redis_image_id": self.redis_image_id,
        }
        self.audit_path = self.temporary / (mode + ".distribution.audit.jsonl")
        self.audit_paths.append(self.audit_path)
        self.log = self.temporary / (mode + ".distribution.log")
        self.log_handle = self.log.open("w")
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listener:
            listener.bind(("127.0.0.1", 0))
            port = listener.getsockname()[1]
        config = self.temporary / (mode + ".distribution.properties")
        jdbc = dict(line.split("=", 1) for line in self.properties.read_text().splitlines() if "=" in line)
        values = [
            "server.address=127.0.0.1", "server.port=" + str(port),
            "spring.main.banner-mode=off", "spring.main.log-startup-info=false",
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
            "spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
            "spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
            "spring.datasource.url=" + jdbc["url"],
            "spring.datasource.username=" + jdbc["user"],
            "spring.datasource.password=" + jdbc["password"],
            "spring.redis.url=redis://" + str(self.redis_endpoint) + ":6379",
            "spring.redis.timeout=5000",
            "sunway.user.password.encoder.type=MD5",
            "owned.identity.audit.type=file",
            "owned.identity.audit.path=" + str(self.audit_path),
            "owned.attachments.enabled=false", "owned.erp.mode=disabled",
            "owned.report.replay.mode=disabled", "owned.scheduler.mode=disabled",
        ]
        if mode == "isolated":
            values.extend([
                "owned.equipment.write.mode=isolated",
                "owned.personnel.write.mode=isolated",
                "owned.consumables.write.mode=isolated",
            ])
        values.extend(key + "=" + str(value) for key, value in
                      getattr(self, "configuration_overrides", {}).items())
        config.write_text("\n".join(values) + "\n")
        config.chmod(0o600)
        self.config = config
        self.process = subprocess.Popen(
            [str(self.distribution / "start.sh"), str(config)],
            stdout=self.log_handle, stderr=subprocess.STDOUT, cwd=self.temporary,
            env=dict(os.environ, JAVA_HOME=str(self.jdk)),
        )
        ready = BASE.wait_until(
            mode + " packaged application loopback readiness",
            lambda: self.process.poll() is None and self._port_accepts(port),
            timeout=180, interval=.25)
        self.report.setdefault("phase_timings_ms", {})[
            "application_readiness_" + mode] = ready["elapsed_ms"]
        self.check(mode + " packaged distribution starts on loopback",
                   self.process.poll() is None and self._port_accepts(port))
        self.url = "http://127.0.0.1:" + str(port) + "/"
        # Keep the session name short: agent-browser derives a Unix socket
        # path from it and rejects long names before opening the page.
        self.browser = "eqd-" + BASE.uuid.uuid4().hex[:8]
        self.ab("open", self.url)
        self.ab("wait", "#login-submit")

    @staticmethod
    def _port_accepts(port):
        try:
            with socket.create_connection(("127.0.0.1", port), timeout=.3):
                return True
        except OSError:
            return False

    def close(self):
        if self.browser:
            self.ab("close")
            self.browser = None
        if self.process and self.process.poll() is None:
            self.process.terminate()
            try:
                self.process.wait(timeout=30)
            except subprocess.TimeoutExpired:
                self.process.kill()
                self.process.wait(timeout=10)
        self.check(self.mode + " packaged application closes cleanly",
                   self.process is not None and self.process.returncode in (0, -15, 143))
        self.process = None
        if self.log_handle:
            self.log_handle.close()
            self.log_handle = None
        errors = sorted(set(BASE.re.findall(r"ORA-\d{5}", self.log.read_text(errors="replace"))))
        self.report.setdefault("oracle_errors", {})[self.mode] = errors
        self.check(self.mode + " packaged application has no Oracle errors", not errors)

    def database(self):
        super().database()
        image = json.loads(self.passed(run(["docker", "image", "inspect", REDIS_IMAGE]), "Redis image inspect"))[0]
        self.redis_image_id = image["Id"]
        self.check("pinned Linux amd64 Redis image", image["Os"] == "linux" and image["Architecture"] == "amd64")
        self.passed(run(["docker", "create", "--name", self.redis_name, "--network", self.network,
                         "--network-alias", "redis", "--restart", "no", "--memory", "256m", "--cpus", "1",
                         REDIS_IMAGE, "redis-server", "--save", "", "--appendonly", "no"]), "Redis container create")
        self.redis_created = True
        self.passed(run(["docker", "start", self.redis_name]), "Redis container start")
        def redis_ready():
            result = run(["docker", "exec", self.redis_name, "redis-cli", "ping"])
            return result.returncode == 0 and result.stdout.strip() == "PONG"

        ready = BASE.wait_until("Redis readiness", redis_ready, timeout=30, interval=.5)
        self.report.setdefault("phase_timings_ms", {})["redis_readiness"] = ready["elapsed_ms"]
        redis_container = json.loads(self.passed(run(["docker", "inspect", self.redis_name]), "Redis container inspect"))[0]
        network = json.loads(self.passed(run(["docker", "network", "inspect", self.network]), "joint network inspect"))[0]
        endpoint = network["Containers"][redis_container["Id"]]["IPv4Address"].split("/", 1)[0]
        self.redis_endpoint = endpoint
        self.check("Redis has one internal network without published ports or bind mounts",
                   network["Internal"] is True and len(redis_container["NetworkSettings"]["Networks"]) == 1
                   and not redis_container["HostConfig"]["PortBindings"]
                   and sum(mount["Type"] == "bind" for mount in redis_container.get("Mounts", [])) == 0)
        self.check("host reaches isolated Redis endpoint", run(["redis-cli", "-h", endpoint, "-p", "6379", "ping"]).stdout.strip() == "PONG")
        self.report["redis"] = {
            "image_reference": REDIS_IMAGE,
            "image_id": self.redis_image_id,
            "endpoint": endpoint,
            "port": 6379,
            "container_id": redis_container["Id"],
            "network_internal": network["Internal"],
            "attached_network_count": len(redis_container["NetworkSettings"]["Networks"]),
            "port_bindings": redis_container["HostConfig"]["PortBindings"],
            "bind_mount_count": sum(mount["Type"] == "bind" for mount in redis_container.get("Mounts", [])),
        }

    def cleanup(self):
        if self.redis_created:
            removed = run(["docker", "rm", "--force", "--volumes", self.redis_name]).returncode == 0
            self.report["cleanup"]["redis_container_removed"] = removed and run(["docker", "container", "inspect", self.redis_name]).returncode != 0
            self.report["cleanup"]["redis_anonymous_volumes_removed"] = removed
            self.redis_created = False
        else:
            self.report["cleanup"].setdefault("redis_container_removed", True)
            self.report["cleanup"].setdefault("redis_anonymous_volumes_removed", True)
        super().cleanup()


def main():
    parser = BASE.argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path,
                        default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/equipment-distribution-browser-20261023.json")
    args = parser.parse_args(); guard_output_path(args.output)
    temporary = BASE.tempfile.TemporaryDirectory(prefix="lims-equipment-distribution-private-")
    replay = DistributionReplay(temporary.name)
    try:
        replay.build()
        replay.database()
        replay.denied_mode()
        replay.isolated_mode()
        replay.audit()
        replay.report["status"] = "executed-pass"
    except Exception as failure:
        replay.report["reason"] = replay.redact(str(failure))
        replay.report["diagnostic_traceback"] = replay.redact(BASE.traceback.format_exc())
        replay.diagnostics()
    finally:
        replay.cleanup()
        temporary.cleanup()
        replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary.name).exists()
        replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]
        replay.report["finished_at"] = replay.now()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    print("equipment distribution browser: %s (%d checks)" %
          (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
