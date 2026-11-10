# LIMS owned source-only distribution

This standalone repository contains the source-only application for the
Sunway supplier exit. Application build/package, a partial production-derived
Oracle DDL/technical bootstrap package, a combined core/external installation
plan and a first-administrator installer are available. Complete restoration on
a new server remains blocked by unfinished schema, configuration, grants and
installation acceptance. See
[the deployment boundary](#clean-device-deployment-boundary)
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
- `deployment/oracle/core-schema/`: partial production-derived DDL and technical
  bootstrap, with a guard against incomplete installation.
- `deployment/oracle/external-schema/`: selected HT/SP/XFP table DDL, defaults,
  indexes, constraints and exact subset verification.
- `deployment/oracle/installation-manifest.json` and `install.sql`: the combined
  partial installation plan and guarded entry for one empty application schema.
- `docs/reconstitution/`: source recovery provenance and compile snapshots.

## Controlled build inputs

The final build does not read the supplier run JAR, CFR, Vineflower or native
forensics tools. Provide these controlled inputs externally:

- a complete JDK 8 through `JAVA_HOME` (including `java`, `javac` and `jar`)
  on both build and runtime hosts; original LCDP Java resources compile during
  application startup and execution;
- Python 3.10+, Maven 3.9.12, Bash, `jq`, `flock` and GNU core utilities;
- public dependency JARs through `LIMS_PUBLIC_DEPENDENCY_DIR`, matching
  `reconstituted/framework/public-dependency-allowlist.json`;
- the hash-pinned Maven plugin seed through
  `LIMS_EXTERNAL_ARTIFACT_ROOT/maven-build-plugin-seed.tar.gz`.

The supplier run JAR remains an optional read-only provenance input for
`verify-source-only-checkpoint.py --run-jar`; it is never a runtime or normal
build dependency.

These dependency directories, Maven and its plugin seed are build-host inputs.
A runtime host using an already built distribution uses its bundled `lib/`
classpath; it does not need Maven, the plugin seed or a separate public-JAR
directory. It still needs the complete JDK 8 because published LCDP Java compiles
at runtime. Python 3.10+ is needed for the bundled installation integrity checks,
and applying the completed Oracle package requires a matching SQLPlus client.

## Build and verify

Run these commands from the standalone repository root. Select the database
access mode before building. The following example selects the approved
same-schema logical clone and renders both CRM Mapper overlays; it does not
create the schema or supply external business data:

```bash
export JAVA_HOME=/opt/jdk1.8.0_391
export LIMS_PUBLIC_DEPENDENCY_DIR=/opt/lims-public-dependencies
export LIMS_EXTERNAL_ARTIFACT_ROOT=/opt/lims-build-artifacts
export EXTERNAL_SCHEMA_PROFILE="$PWD/api-fixtures/schema/external-schema-runtime-profile-local-view.example.json"
export EXTERNAL_SCHEMA_MANIFEST="$PWD/api-fixtures/schema/external-schema-clone-manifest.json"

bash scripts/verify-external-artifacts.sh --source-only
OWNED_DISTRIBUTION_REUSE=1 bash scripts/build-owned-distribution.sh
LIMS_DEPLOY_DIST="$(readlink -f owned-distribution/target/distribution)"
python3 scripts/verify-source-only-checkpoint.py \
  --distribution "$LIMS_DEPLOY_DIST"
SOURCE_ONLY_CHECKPOINT=1 python3 owned-distribution/tests/test-package.py
```

The package gate performs two clean builds and a repackage in temporary
snapshots. Give `TMPDIR` enough disk space for those snapshots and public
libraries; a small RAM-backed `/tmp` can fail with `No space left on device`.
For example, set `TMPDIR` to an existing task-owned directory on the build disk
before running the package gate. This does not change its checks.

Use `LIMS_DEPLOY_DIST` as the resolved deployment/mount directory. Keep it intact
while the application runs; build cache activation may change the
`target/distribution` link. Set exactly one
`owned.external-schema.access-mode=local-view` entry in the private runtime
properties. For a direct-schema deployment use a reviewed `direct-schema`
profile and matching property. A build with both `EXTERNAL_SCHEMA_PROFILE` and
`EXTERNAL_SCHEMA_MANIFEST` unset preserves the original DB-link SQL and requires
`original-db-link`.
The launcher checks the packaged mode and its embedded contract before starting
Java, and rejects a missing or mismatched choice. Setting the property cannot
rewrite an already built artifact. Obtain and verify this distribution before
running its first-administrator command. Start it only after the target schema,
configuration bootstrap and identity installation are ready; follow the
[clean-device deployment sequence](docs/deployment/clean-device-contract.md).

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
| `deployment/oracle/external-schema/` | Portable partial HT/SP/XFP ten-table package with captured defaults, original index links and exact subset verification | Partial; requires empty diagnostic user and rejects full installation |
| `deployment/oracle/installation-manifest.json` and `install.sql` | Combined external/core phase order, namespace checks and exact 747-table union verifier | Partial; normal entry rejects before any child SQL or DDL |

The source-bound DDL for these selected 737 core and ten external tables is
available, together with an integrated installation plan. This is a defined
structural subset; it does not establish complete current-production DDL,
configuration or independent new-server readiness.

The shortest offline plan check is:

```bash
python3 scripts/verify-combined-oracle-installation.py --check-plan
```

It checks the pinned child packages, exact phase order, shared preflight,
cross-package object namespaces and final union verifier without Oracle or
private collection files. Exit 0 means `plan_valid=true`; the result still has
`status=blocked`, `deployment_ready=false` and `complete_external_domains=false`.
Without `--check-plan`, the verifier exits 2 to stop deployment. The normal SQL
entry, `deployment/oracle/install.sql`, raises `ORA-20100` before child SQL or
DDL while these blockers remain. There is no override mode.

The integrated order is one empty-user/charset preflight, external tables /
indexes / constraints / `90-verify.sql`, then the original core DDL order,
technical bootstrap and its verification, core `90-verify.sql`, and finally
`99-verify.sql` for the exact 747-table logical object union. External
`90-verify.sql` requires exactly ten tables, so it must run before core objects
exist. Chaining the two child `install.sql` files also fails their repeated
empty-user preflight. See [the combined plan](deployment/oracle/README.md) for
the precise phases; this order is not permission to bypass the partial guard.
The combined plan places the selected external objects in the application
schema, matching the `local-view` build profile. It does not provision separate
`direct-schema` owners or original database links.

Remaining project work is to complete the ordered, sanitized current-production
core DDL package, its object/grant verifier, and versioned configuration bootstrap
(menus, permissions, dictionaries, workflows and dynamic LCDP resources). An
existing test database or historical release SQL does not supply that contract.
Ordinary business data can be added separately; configuration required to execute
the original application must have a reproducible initialization path.
Delivering that package is remaining project work. It must be included in the
standalone repository or a versioned, hash-pinned deployment bundle; a parent
checkout, private collection directory or a developer's existing Oracle volume
must not become a requirement for new-server installation.

The partial structural and technical bootstrap package is in
[deployment/oracle/core-schema](deployment/oracle/core-schema/README.md).
It can be integrity-checked without private material or the parent checkout:

```bash
python3 scripts/verify-core-schema-package.py --check-package
```

This does not grant deployment readiness. The normal package gate and SQL
installer still reject the incomplete package before applying DDL.
Its finite `70-bootstrap.sql` supplies 4,871 reviewed technical rows across 25
tables, including locale, menu, entity and UI configuration, 28 dictionary
categories, 120 options and their translation dependencies. Captures are bound
separately; empty production categories remain empty.
It additionally carries seven reviewed published LCDP modules for equipment,
folder/order/project/sample information, report lookup and delivery, with ten
menus and the equipment dictionary/selector, plus the two original file-service
Java resources. `lcdp.file.server.FileScript` retains its captured active-only
resource and root sentinel; no published history is invented. The original
upload validator's `FILE-EXTENSIONS` and `FILE-SPACELIMIT` constants are also
source-bound in the bootstrap. The original two import-template definitions,
eleven column configurations and one file metadata row are also included.
The separate [template asset package](deployment/oracle/import-templates/README.md)
supplies one metadata-sanitized header-only workbook and its offline placement
command. The other source template has no file or mapped columns and remains
explicitly blocked. Other dynamic modules,
workflow and permission bootstrap remain incomplete; draft scripts containing
embedded business data are excluded.
The package also preserves 188 captured request-to-menu mappings and six
permission-type definitions, including original suspension flags and seven
references to a missing production menu. It copies no production grants and
does not enable the optional `sunway.api.check-access-permission` setting.
See [access-control.md](docs/deployment/access-control.md) for its matching rules,
permission-panel definitions and installation order.
Complete permissions, workflows and dynamic LCDP initialization remain
incomplete; the phase is not a replacement for the full bootstrap.
The bootstrap also supplies the 41 production-captured workflow service
definitions in `T_CORE_MENU_EXT`, preserving 40 enabled and one disabled entry.
Service definitions are separate from process XML, versions, organization scope
and candidate assignments; those workflow dependencies are not installed by
these metadata rows.
The distribution now includes a one-time
[first-administrator command](docs/deployment/first-administrator.md), accepting
private installer identities, password and policy. It initializes a verified
empty identity set using the original Java password encoder; it is not a public
bootstrap endpoint or a complete application configuration installer.

The separate [workflow installer](docs/deployment/workflows.md) uses the bundled
symbolic package for 20 nonempty latest process definitions and preserves seven
source-empty blockers. Prepare the declared target identities and memberships,
stop the target application and supply a complete private mapping to
`initialize-workflows.sh`. It writes only workflow configuration, preserves
versions and candidate/organization boundaries, and does not grant permissions
or establish business process acceptance.

The source-only distribution includes `installation/`: the exact public Oracle
SQL/metadata and two offline verifiers required by the combined plan, plus its
own README. Every file, including the nested package manifests, is hashed in the
distribution manifest. It carries no parent checkout, private captures or
historical SQL. Verify the shipped materials from the resolved distribution:

```bash
python3 "$LIMS_DEPLOY_DIST/installation/scripts/verify-combined-oracle-installation.py" --check-plan
```

The application JAR and runtime classpath do not consume these files. The bundle
closes the handoff gap for the existing selected SQL; its normal installer still
rejects the incomplete package. It does not yet initialize a complete new
database. Full deployment guidance remains in this standalone checkout under
`docs/deployment/`; completing the missing installation content is project work,
not an instruction for an operator to reconstruct it manually.

Before deployment, an operator must provide one Oracle PDB, one Redis instance,
an application schema/user with grants, a private runtime properties file, the
project's completed schema package for the target version, a verified matching
distribution and a complete JDK 8. A host building from source also supplies the
public dependency JARs and Maven plugin seed described above. Credentials,
license material, private keys, DB-link strings and business data stay outside
Git and the distribution.

The portable [external table package](deployment/oracle/external-schema/README.md)
can now be checked without private receipts or parent files:

```bash
python3 scripts/verify-core-schema-package.py \
  --manifest deployment/oracle/external-schema/manifest.json --check-package
```

It preserves the selected tables' 33 defaults and original constraint/index
linkage. Its exact phases now participate in the combined installation plan.
Complete external non-table DDL, dependencies, grants, charset/collation closure
and runtime access acceptance remain unresolved. Neither this structure check
nor the combined plan check establishes positive external business rows or real
HTTP equivalence. This package does not update an existing runtime schema.

The older external logical-clone fixture is structure-only. It requires an
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

For new installations, complete and use the combined
`deployment/oracle/installation-manifest.json` / `install.sql` contract. The older
`api-fixtures/schema/external-logical-clone.sql` is retained for existing test
fixtures; do not apply both packages to the same schema. Its matching restore
script drops those ten tables **including any rows they contain**. It is a
destructive fixture teardown, not a data-preserving rollback or a new-server
installation step. The intended sequence and its current prerequisites are in
`docs/deployment/clean-device-contract.md`.

`verify-clean-device-contract.py` intentionally rejects deployment while the
core schema/configuration installation package remains incomplete. This is a
deployment blocker, not an optional warning. A successful source-only build or
external-clone structure check must not be reported as complete clean-device
restoration.

The distribution contains one Spring application and requires external
configuration at startup. It contains no supplier run JAR, private
`gikam-core-*` JAR, Sunway native loader, license input, compatibility process,
internal HTTP bridge or JNI daemon.

## Deploy and rollback

For a prepared server, create a persistent writable application working directory
outside the immutable distribution. The original application resolves `files/`,
`temp/` and `multipart/` from the process working directory, so use the same
directory on every start and app rollback. Start with an absolute private
configuration path:

```bash
LIMS_APP_WORKDIR=/var/lib/lims-owned
install -d -m 0700 "$LIMS_APP_WORKDIR"
cd "$LIMS_APP_WORKDIR"
"$LIMS_DEPLOY_DIST/start.sh" /private/path/runtime.properties
```

The account running Java must own this directory. A container must use an explicit
working directory and a writable persistent volume at that location. Preserve
the file volume with the database backup; replacing only the application JAR
does not restore uploaded bytes. Do not use the immutable distribution directory
as the working directory. For installation without an FTP dependency, explicitly
set `owned.files.storage=local`; the original upload service then stores bytes
in that persistent `files/` tree. See [file storage](docs/deployment/file-storage.md)
for permissions, original download security, upload constants and retention.
Without this setting the original FTP manager remains selected and requires its
configured backend.

The launcher defaults to listening on `127.0.0.1`. For direct access from another
host, explicitly choose the intended interface, for example
`LIMS_BIND_ADDRESS=0.0.0.0 "$LIMS_DEPLOY_DIST/start.sh" /private/path/runtime.properties`,
and configure the server/container network accordingly. `LIMS_BIND_ADDRESS`
sets the JVM `server.address` property and overrides the value in the properties
file. The application port comes from `server.port`;
use the same port when forwarding between WSL and its Windows host. Local
development uses 18087 and reuses the existing Oracle/Redis. App-only refresh
is allowed after active verification finishes; rollback starts the previous
immutable distribution with its matching private configuration. This does not
initialize or restore a database.

The private runtime file must include datasource settings and an explicit
`spring.redis.database` matching its Redis URL or host/port configuration.
A URL-only Redis database suffix is insufficient for the observed original
clients. Do not flush an existing application's Redis database. For readiness,
wait until `GET /open/security/public-key` returns a nonempty, parseable RSA
X.509 public key; an empty HTTP 200 during startup is not ready. Then log in and
read `/core/module/sys/logins/account` in the same session. See
[the first-administrator guide](docs/deployment/first-administrator.md) for the
initial identity and login contract.

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
