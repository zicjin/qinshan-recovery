# LIMS owned source-only distribution

This standalone repository is the buildable and deployable source asset for the
Sunway supplier exit. It contains the original `project/` source, reconstructed
framework modules, owned Java modules, source-only build/package tooling,
app-only deployment tooling, and the API/evidence gates still required for
closure.

Historical recovery, Data Pump, CFR/Vineflower, native forensics and one-time
migration tools are retained in the parent planning repository under
`.scratch/sunway-supplier-exit-api-first/migration-tooling/`. They are not
build or deployment inputs and are not required by a clean standalone checkout.

## Source layout

- `project/`: original application source and resources.
- `reconstituted/framework/`: tracked reconstructed framework source,
  resources, provenance and module build metadata.
- `owned-*/`: owned Java seams and their resources.
- `owned-distribution/`: deterministic source-only packaging and start entry.
- `scripts/`: build, deployment, runtime and verification entry points.
- `tools/`: API planning/closure and source verification tools.
- `build-support/`: integrity manifests for controlled external build inputs.
- `docs/reconstitution/`: source recovery provenance and compile snapshots.

## Controlled build inputs

The final build does not read the supplier run JAR, CFR, Vineflower or native
forensics tools. Provide these controlled inputs externally:

- JDK 8 through `JAVA_HOME`;
- public dependency JARs through `LIMS_PUBLIC_DEPENDENCY_DIR`, matching
  `reconstituted/framework/public-dependency-allowlist.json`;
- the hash-pinned Maven plugin seed through
  `LIMS_EXTERNAL_ARTIFACT_ROOT/maven-build-plugin-seed.tar.gz`.

The supplier run JAR remains an optional read-only provenance input for
`verify-source-only-checkpoint.py --run-jar`; it is never a runtime or normal
build dependency.

## Build and verify

```bash
export JAVA_HOME=/opt/jdk1.8.0_391
export LIMS_PUBLIC_DEPENDENCY_DIR=/opt/lims-public-dependencies
export LIMS_EXTERNAL_ARTIFACT_ROOT=/opt/lims-build-artifacts

bash scripts/verify-external-artifacts.sh --source-only
bash scripts/build-owned-distribution.sh
python3 scripts/verify-source-only-checkpoint.py \
  --distribution owned-distribution/target/distribution
SOURCE_ONLY_CHECKPOINT=1 python3 owned-distribution/tests/test-package.py
```

## Clean-device deployment boundary

The repository does not currently contain a complete current-production Oracle
DDL/bootstrap package. A clean clone therefore cannot, by itself, recreate the
full LIMS schema on a new server. The historical files under `project/sql/`
are release and upgrade provenance, not an ordered initialization script; do
not execute that directory as a bootstrap.

Before deployment, an operator must provide one Oracle PDB, one Redis instance,
an application schema/user with grants, a private runtime properties file, the
approved core schema package for the target version, Java 8, public dependency
JARs and the Maven plugin seed. Credentials, license material, private keys,
DB-link strings and business data stay outside Git and the distribution.

The current external logical-clone fixture is structure-only. It requires an
existing application schema, creates the ten approved HT/SP/XFP tables and
indexes, creates no DB link and inserts no business rows. Use the following
fail-closed contract check before applying fixtures:

```bash
python3 scripts/verify-clean-device-contract.py
python3 scripts/verify-external-logical-clone-fixture.py
```

Once the operator-managed core schema exists, apply
`api-fixtures/schema/external-logical-clone.sql` through the approved SQLPlus
channel, retain its log, and verify it with
`scripts/verify-external-logical-clone-runtime.py`. The matching restore script
drops only those ten fixture tables and does not restore a baseline or delete
business data. The complete sequence and its limits are documented in
`docs/deployment/clean-device-contract.md`.

`verify-clean-device-contract.py` intentionally reports `deployment_ready:
false` while the approved core schema package is unavailable. This is a
deployment blocker, not an optional warning. A successful source-only build or
external-clone structure check must not be reported as complete clean-device
restoration.

The distribution contains one Spring application and requires external
configuration at startup. It contains no supplier run JAR, private
`gikam-core-*` JAR, Sunway native loader, license input, compatibility process,
internal HTTP bridge or JNI daemon.

## Deploy and rollback

`refresh-owned-sss2-app.sh` refreshes only the owned application on `sss2`.
It preserves supplier `18081`, debug `18088`, the long-lived Oracle and Redis.
Rollback redeploys a retained previous distribution through the same app-only
path:

```bash
bash scripts/refresh-owned-sss2-app.sh
bash scripts/rollback-owned-sss2-app.sh /path/to/previous/distribution
```

See `docs/owned-sss2-deployment.md` for the runtime boundary and report
semantics.

## API and evidence

Fast differential output remains diagnostic. Strict reports are promoted only
after real HTTP execution, current binding checks, fixture/audit/rollback
observations and milestone cleanup. Documentation, registry and archive
changes do not stale behavior evidence; implementation dependency-surface
changes select affected replay through `scripts/source_binding.py`.

The canonical evidence registry is optional in a standalone checkout and is
created only when canonical reports are promoted. Parent `.scratch/` history
is never a build or deployment input.
