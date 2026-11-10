# Clean-device deployment contract

This document defines the boundary for deploying the owned source-only
distribution on a new server. It is deliberately explicit about the current
gap: this repository does **not** contain a complete current-production Oracle
schema bootstrap. A clean clone therefore cannot be advertised as a complete
from-scratch LIMS restoration until an approved, source-bound core schema
package is delivered.

`deployment/oracle/core-schema/` now contains the first source-bound structural
package: 737 tables, ordered indexes/constraints, types, selected routines and
per-table verification. It also ships a finite technical bootstrap for locale,
translations, static menus, entity metadata and safe UI/select configuration,
plus exact verification of that subset. The distribution provides the separate
first-administrator installer. Its schema manifest remains partial and the SQL
initializer rejects before applying any DDL. Run
`python3 scripts/verify-core-schema-package.py --check-package` to validate shipped
files without private inputs; this does not establish complete deployment readiness.

The bootstrap content verifier is an installation-phase check: run it immediately
following the seed and before initializing identities or starting the application.
The original LCDP ready listener normalizes page component configuration during
startup, and the first-administrator command adds a target-specific organization
constant. A later exact bootstrap check may therefore report intentional runtime
changes. Preserve its failure and compare against the relevant original startup
behavior; do not overwrite runtime configuration just to restore seed hashes.

The current application distribution does not include the schema directory or
these deployment documents. Deliver the matching standalone checkout alongside
the distribution, or a future versioned, hash-pinned installation bundle carrying
the schema, verifier and instructions. A parent planning checkout and private
acquisition directory must not be installation dependencies.

`project/sql/` contains historical release and upgrade SQL. Its files are not a
current production DDL snapshot and must not be executed as a directory-wide
initialization script. The files under `api-fixtures/schema/` are targeted,
versioned fixtures. They assume an existing application schema and do not
create an Oracle user, PDB, grants, Redis instance, or business rows.
Some files are explicitly metadata DML fixtures; their scope is described in
`api-fixtures/schema/README.md`. Applying all fixtures is not a bootstrap path.

`deployment/oracle/external-schema/` now ships a separately verifiable partial
ten-table package, including captured defaults and actual constraint/index
links. Its source references are hash-only provenance and do not require private
files at installation. Its read-only SQL verifier checks the selected structure
and live empty-table counts as the connected user, without a fixed schema/PDB:

```bash
python3 scripts/verify-core-schema-package.py \
  --manifest deployment/oracle/external-schema/manifest.json --check-package
```

This package targets an empty diagnostic schema and retains an unconditional
partial-install guard. Integration with core installation, complete external
domain DDL/grants and application acceptance remain incomplete. See its
[scope and phases](../../deployment/oracle/external-schema/README.md).

The older external logical-clone fixture is structure-only. It creates the
ten approved HT/SP/XFP tables and indexes in the application schema, creates no
DB link, and inserts no rows. It is valid for a `local-view` access path after
the application schema exists:

```bash
python3 scripts/verify-clean-device-contract.py --check-contract
python3 scripts/verify-external-logical-clone-fixture.py
```

The fixture verifier currently requires the private source receipt referenced
by `external-logical-clone.json`. It validates acquisition provenance in this
workspace and cannot yet serve as a clean-clone deployment gate. The eventual
schema package must ship enough sanitized, hash-pinned structure metadata to
validate deployment without that private directory or a parent checkout.

Apply it only through an approved SQLPlus channel connected to the intended
runtime PDB and schema. Oracle DDL commits implicitly; retain the SQLPlus log.
To remove only this fixture, use:

```bash
python3 scripts/verify-clean-device-contract.py --check-contract
# after confirming the target is the disposable runtime schema:
sqlplus ... @api-fixtures/schema/external-logical-clone-restore.sql
```

The restore script drops the ten fixture tables with cascading constraints,
including any rows they contain. Use it only for an approved disposable fixture;
it does not restore a baseline or preserve business data.

## Required operator-provided prerequisites

The complete schema and configuration bootstrap are remaining project
deliverables, not instructions for an operator to reconstruct them manually.
They must ship as tracked standalone SQL/metadata or a versioned, hash-pinned
deployment bundle, with initialization order, preconditions, exact object
verification and upgrade/data-preserving recovery instructions. Existing private
collection material is a source for that work, not an installation dependency.

An operator supplies the target environment and private inputs:

- a complete supported JDK 8 with `java`, `jar`, `javac` and the system Java
  compiler on both build and runtime hosts; published LCDP Java is dynamically
  compiled by the original application;
- one Oracle instance/PDB and one Redis instance, with network reachability;
- an application schema/user, password, grants, charset and connection pool
  configuration;
- the approved **core schema and configuration bootstrap package** for the
  target software version, once delivered by this project; configuration must
  cover dictionaries, menus, permissions, workflows, published LCDP resources
  and initial administrator setup without production secrets or business rows;
- a private runtime properties file containing datasource, Redis, session and
  required feature settings;
- a persistent writable application working directory with its `files/` volume,
  kept outside the immutable distribution and preserved across app rollback;
- public dependency JARs and the Maven plugin seed listed in `README.md`.

The runtime configuration must contain at least datasource URL/user/password,
Redis URL or host/port plus an explicit `spring.redis.database`, `server.port`,
`sunway.user.password.encoder.type`, and an explicit
`owned.external-schema.access-mode` matching the built artifact. Keep Redis's
database property consistent with its URL. The observed original clients did not
honor a URL-only database suffix; never flush an existing application's database.
Do not copy production credentials, DB-link strings, license bodies, private
keys, or business rows. `owned-distribution/example/owned-application.properties`
is a shape example, not a deployable credential file.

## Reproducible sequence once the core package exists

This is the required sequence for the future complete deployment package.
The full schema/grants and configuration phases remain incomplete. Do not
replace them by replaying the historical SQL directory or applying every fixture.

Run the deployment readiness gate first:

```bash
python3 scripts/verify-clean-device-contract.py
```

It currently exits 2 and must stop a new-device deployment. `--check-contract`
only validates this explicit blocked state offline; it is not a deployment gate.

1. Choose the intended database access mode, then build and verify the
   source-only distribution, or obtain an already verified matching immutable
   distribution. Keep the standalone schema/docs available too. Run from the
   standalone repository root with the build prerequisites in `README.md`.
   For the same-schema `local-view` installation, include the explicit Mapper
   profile on every build:

   ```bash
   bash scripts/verify-external-artifacts.sh --source-only
   export EXTERNAL_SCHEMA_PROFILE="$PWD/api-fixtures/schema/external-schema-runtime-profile-local-view.example.json"
   export EXTERNAL_SCHEMA_MANIFEST="$PWD/api-fixtures/schema/external-schema-clone-manifest.json"
   OWNED_DISTRIBUTION_REUSE=1 bash scripts/build-owned-distribution.sh
   LIMS_DEPLOY_DIST="$(readlink -f owned-distribution/target/distribution)"
   python3 scripts/verify-source-only-checkpoint.py \
     --distribution "$LIMS_DEPLOY_DIST"
   SOURCE_ONLY_CHECKPOINT=1 python3 owned-distribution/tests/test-package.py
   ```

   Set `owned.external-schema.access-mode=local-view` once in the private runtime
   properties. `direct-schema` requires its reviewed profile and matching config;
   a build without a profile preserves `original-db-link` SQL. The launcher rejects
   mismatched modes before Java. It does not infer a mode from database state.
2. Provision the operator-managed Oracle/PDB and Redis services on a new server.
   Reuse existing services during development and validation; do not restart
   Oracle or reimport data per check. Production remains strictly read-only.
3. Create the dedicated application schema/user and grants using the completed
   project-supplied installation package. Apply its ordered DDL and exact object
   verifier, retaining its hash receipt. Apply and verify versioned configuration
   bootstrap separately from ordinary business data. The current partial package
   is guarded; do not bypass it or run all files under `project/sql/`.
4. Apply and verify the completed portable external logical-domain package
   matching the chosen access mode. `deployment/oracle/external-schema/` is the
   new-installation package under development; its own `90-verify.sql` checks
   exact selected structures and live empty-table counts as the installation
   user. Core integration, non-table dependencies and grants remain incomplete,
   so its normal installer still rejects. Do not apply both that package and
   the older `api-fixtures/schema/external-logical-clone.sql` to the same schema.
   The older fixture is retained for existing test environments. Its verifier is
   specific to Docker SQLPlus, `FREEPDB1` and `LIMS_RUNTIME_LOCAL`; it checks
   aggregate metadata counts and schema-owned DB-link absence. Its row figure
   comes from `NUM_ROWS` statistics, not a live `COUNT(*)`. It is not a portable
   exact schema/grants verifier or proof of empty tables. Completing this
   installation/verification path remains project work.
5. Prepare the private runtime file for the exact schema, Redis database and
   artifact mode. Use the already obtained distribution's one-time installer:

   ```bash
   "$LIMS_DEPLOY_DIST/initialize-admin.sh" /private/path/runtime.properties \
     < /private/path/first-admin.json
   ```

   See [first-administrator.md](first-administrator.md) for its required private
   JSON and password policy. It requires empty identity tables and technical
   prerequisites; it does not complete remaining workflow/LCDP/configuration
   bootstrap.
6. Start one Spring application from its stable writable working directory,
   owned by the application account:

   ```bash
   LIMS_APP_WORKDIR=/var/lib/lims-owned
   install -d -m 0700 "$LIMS_APP_WORKDIR"
   cd "$LIMS_APP_WORKDIR"
   "$LIMS_DEPLOY_DIST/start.sh" /private/path/runtime.properties
   ```

   Use absolute distribution/configuration paths. The original application
   resolves `files/`, `temp/` and `multipart/` from this working directory;
   `start.sh` does not change it. Containers require an explicit working
   directory and a writable persistent volume. Do not start in the immutable
   distribution. File storage must be configured and accepted separately.
   For the explicit local backend set `owned.files.storage=local` in private
   properties and persist `files/`; see [file-storage.md](file-storage.md).
   With no selection the original FTP manager remains in use.

   The launcher listens on `127.0.0.1` by default. To permit direct remote
   access, choose the intended interface explicitly, for example
   `LIMS_BIND_ADDRESS=0.0.0.0 "$LIMS_DEPLOY_DIST/start.sh" /private/path/runtime.properties`,
   and configure container/server networking. This environment variable sets
   JVM `server.address` and overrides that property in the runtime file;
   `server.port` still selects the port. Use the same port for WSL/Windows
   forwarding. Mount the resolved immutable directory; never mount the mutable
   `target/distribution` alias. Startup does not install Oracle schemas.
7. Wait for `GET /open/security/public-key` to return a nonempty, parseable RSA
   X.509 public key. An empty HTTP 200 during ordered startup callbacks is not
   readiness. Do not repeatedly restart an initializing application. Then use
   real RSA login and read `/core/module/sys/logins/account` in the same session
   to confirm principal, organization and roles; verify protected requests and
   menus as described in the first-administrator guide.
8. Retain the distribution and runtime configuration as the rollback pair.
   Rollback replaces only the application distribution; it does not recreate
   Oracle or Redis. Reuse the same working directory and persistent file volume.
   Data recovery requires matching database and file backups, separately from
   app rollback.

The contract verifier reports `status: blocked`, `contract_valid: true` and
`deployment_ready: false` while the core schema/configuration installation package
remains incomplete. The
default exits 2 to stop deployment; only explicit `--check-contract` exits 0 for
offline validation of the blocked contract. The current state is therefore:
source-only build/package is available; targeted schema fixtures and the
external logical clone are available; complete clean-device Oracle restoration
remains blocked.
