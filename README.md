# LIMS owned source-only distribution

This standalone repository contains the source-only application for the
Sunway supplier exit. Application build/package is available; complete restoration
on a new server is blocked by the missing core Oracle schema and configuration
bootstrap package. See [the deployment boundary](#clean-device-deployment-boundary)
before provisioning a new environment. It contains the original `project/`
source, reconstructed
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

- JDK 8 through `JAVA_HOME` (including `java`, `javac` and `jar`);
- Python 3.10+, Maven 3.9.12, Bash, `jq`, `flock` and GNU core utilities;
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

The default build preserves the original DB-link SQL. Choose the database access
mode explicitly for deployment. For the approved same-schema logical clone, build
both CRM Mapper overlays with:

```bash
export EXTERNAL_SCHEMA_PROFILE="$PWD/api-fixtures/schema/external-schema-runtime-profile-local-view.example.json"
export EXTERNAL_SCHEMA_MANIFEST="$PWD/api-fixtures/schema/external-schema-clone-manifest.json"
OWNED_DISTRIBUTION_REUSE=1 bash scripts/build-owned-distribution.sh
LIMS_DEPLOY_DIST="$(readlink -f owned-distribution/target/distribution)"
```

Use `LIMS_DEPLOY_DIST` as the resolved deployment/mount directory. Keep it intact
while the application runs; build cache activation may change the
`target/distribution` link. Set exactly one
`owned.external-schema.access-mode=local-view` entry in the private runtime
properties. For a direct-schema deployment use a reviewed `direct-schema`
profile and matching property; unchanged DB-link SQL uses `original-db-link`.
The launcher checks the packaged mode and its embedded contract before starting
Java, and rejects a missing or mismatched choice. Setting the property cannot
rewrite an already built artifact. Start the packaged launcher:

```bash
"$LIMS_DEPLOY_DIST/start.sh" /private/path/runtime.properties
```

The compile provenance and package manifest record the selected mode, profile,
effective manifest, renderer and rendered Mapper hashes. These records establish
build inputs; the Oracle schema, data, permissions and real HTTP behavior require
their own verification.

## Clean-device deployment boundary

The repository does not currently contain a complete current-production Oracle
DDL/bootstrap package. A clean clone therefore cannot, by itself, recreate the
full LIMS schema on a new server. The historical files under `project/sql/`
are release and upgrade provenance, not an ordered initialization script; do
not execute that directory as a bootstrap.

| SQL material | Current use | Initializes an empty LIMS database? |
| --- | --- | --- |
| `project/sql/` | Historical releases, upgrades and source provenance | No |
| `api-fixtures/schema/production-ddl-*-20261109.sql` | Reviewed incremental alignment of an existing test schema | No |
| Other `api-fixtures/schema/*.sql` | Targeted test restoration and metadata fixtures with explicit preconditions | No |
| `api-fixtures/schema/external-logical-clone.sql` | Ten HT/SP/XFP table structures in an existing local runtime schema | No |
| `deployment/oracle/core-schema/` | Production-derived partial structural package: 737 tables, ordered indexes/types/routines and an object verifier | Partial; normal installer rejects while blockers remain |

The remaining deployment input is an ordered, sanitized current-production core
DDL package, its object/grant verifier, and versioned configuration bootstrap
(menus, permissions, dictionaries, workflows and dynamic LCDP resources). An
existing test database or historical release SQL does not supply that contract.
Ordinary business data can be added separately; configuration required to execute
the original application must have a reproducible initialization path.
Delivering that package is remaining project work. It must be included in the
standalone repository or a versioned, hash-pinned deployment bundle; a parent
checkout, private collection directory or a developer's existing Oracle volume
must not become a requirement for new-server installation.

The first structural package is now in
[deployment/oracle/core-schema](deployment/oracle/core-schema/README.md).
It can be integrity-checked without private material or the parent checkout:

```bash
python3 scripts/verify-core-schema-package.py --check-package
```

This does not grant deployment readiness. The normal package gate and SQL
installer still reject the incomplete package before applying DDL.

Before deployment, an operator must provide one Oracle PDB, one Redis instance,
an application schema/user with grants, a private runtime properties file, the
approved core schema package for the target version, JDK 8, public dependency
JARs and the Maven plugin seed. Credentials, license material, private keys,
DB-link strings and business data stay outside Git and the distribution.

The current external logical-clone fixture is structure-only. It requires an
existing application schema, creates the ten approved HT/SP/XFP tables and
indexes, creates no DB link and inserts no business rows. This readiness check
currently prints `status=blocked`, `deployment_ready=false` and exits **2**, so
a shell deployment sequence stops before applying fixtures:

```bash
python3 scripts/verify-clean-device-contract.py
```

For offline validation of the repository's declared limitations only, use the
explicit contract-check mode. Its exit 0 means the contract is consistent;
`deployment_ready` still remains false:

```bash
python3 scripts/verify-clean-device-contract.py --check-contract
python3 scripts/verify-external-logical-clone-fixture.py
```

The fixture verifier also reads its original private production metadata
receipt. It is currently a provenance audit command for the recovery workspace,
not a standalone clean-clone deployment verifier. That dependency must be
removed from the eventual deployable schema package's verification path.

Once the operator-managed core schema exists, apply
`api-fixtures/schema/external-logical-clone.sql` through the approved SQLPlus
channel, retain its log, and verify it with
`scripts/verify-external-logical-clone-runtime.py`. The matching restore script
drops those ten tables **including any rows they contain**. It is a destructive
fixture teardown, not a data-preserving rollback. The intended sequence and its
current prerequisites are documented in
`docs/deployment/clean-device-contract.md`.

`verify-clean-device-contract.py` intentionally rejects deployment while the
approved core schema package is unavailable. This is a
deployment blocker, not an optional warning. A successful source-only build or
external-clone structure check must not be reported as complete clean-device
restoration.

The distribution contains one Spring application and requires external
configuration at startup. It contains no supplier run JAR, private
`gikam-core-*` JAR, Sunway native loader, license input, compatibility process,
internal HTTP bridge or JNI daemon.

## Deploy and rollback

For a prepared server, start the resolved distribution with the private
configuration file as shown above. The application port comes from `server.port`;
use the same port when forwarding between WSL and its Windows host. Local
development uses 18087 and reuses the existing Oracle/Redis. App-only refresh
is allowed after active verification finishes; rollback starts the previous
immutable distribution with its matching private configuration. This does not
initialize or restore a database.

`refresh-owned-sss2-app.sh` is an existing-environment operation that refreshes
only the owned application on `sss2`; it does not provision a new server.
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
