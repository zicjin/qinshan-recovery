# Clean-device deployment contract

This document defines the boundary for deploying the owned source-only
distribution on a new server. It is deliberately explicit about the current
gap: this repository does **not** contain a complete current-production Oracle
schema bootstrap. A clean clone therefore cannot be advertised as a complete
from-scratch LIMS restoration until an approved, source-bound core schema
and configuration package is completed and accepted.

`deployment/oracle/core-schema/` now contains the first source-bound structural
package: 737 tables, ordered indexes/constraints, types, selected routines and
per-table verification. It also ships a finite technical bootstrap for locale,
translations, static menus, entity metadata and safe UI/select configuration,
plus exact verification of that subset. The distribution provides the separate
first-administrator installer. The finite bootstrap also restores the original
four-table [audit configuration](audit-configuration.md), preserving enabled and
disabled states without enabling runtime profiles or copying audit history.
Its schema manifest remains partial and the SQL
initializer rejects before applying any DDL. Run
`python3 scripts/verify-core-schema-package.py --check-package` to validate shipped
files without private inputs; this does not establish complete deployment readiness.

The selected core and external DDL now have one integrated plan:
`deployment/oracle/installation-manifest.json` pins both child packages and
`deployment/oracle/install.sql` is its normal SQL entry. The plan covers 737
core plus ten external tables, with a final exact 747-table union verifier.
These selected DDL materials are available; complete current-production schema,
configuration and new-server restoration remain unproven.

The bootstrap content verifier is an installation-phase check: run it immediately
following the seed and before initializing identities or starting the application.
The original LCDP ready listener normalizes page component configuration during
startup, and the first-administrator command adds a target-specific organization
constant. A later exact bootstrap check may therefore report intentional runtime
changes. Preserve its failure and compare against the relevant original startup
behavior; do not overwrite runtime configuration just to restore seed hashes.

The source-only application distribution includes an `installation/` directory
with the exact public Oracle plan inputs, both offline verifiers and a dedicated
README. From its root, run
`python3 installation/scripts/verify-combined-oracle-installation.py --check-plan`.
It works without the standalone or parent checkout and without private captures;
the outer distribution manifest hashes every bundled file, including nested
package manifests. The application does not load this directory at runtime.
The bundle remains partial and guarded, so copying it does not yet initialize
the complete system. Retain the matching standalone checkout for these full
deployment instructions and the remaining project installation deliverables.

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

Its phases are integrated with core in the combined plan. The shortest offline
check, requiring no Oracle or private acquisition inputs, is:

```bash
python3 scripts/verify-combined-oracle-installation.py --check-plan
```

Exit 0 establishes `plan_valid: true` for pinned package/file hashes, fixed phase
order, the shared preflight, cross-package namespaces and exact union verifier.
The result remains `status: blocked`, `deployment_ready: false` and
`complete_external_domains: false`. Without `--check-plan`, the command exits 2
to stop deployment. It executes no SQL and offers no guard-bypass mode.

The combined SQL initializer and both child installers retain their unconditional
partial guard. The normal combined entry raises `ORA-20100` before any child SQL
or DDL. Its declared phase order is:

| Order | Phase | Required boundary |
| --- | --- | --- |
| 1 | Combined `00-preflight.sql` | Dedicated empty session user, unchanged `CURRENT_SCHEMA`, `AL32UTF8` and `AL16UTF16` |
| 2 | External `20-tables.sql`, `30-indexes.sql`, `35-constraints.sql`, `90-verify.sql` | External verifier requires exactly its ten tables and no core objects yet |
| 3 | Core `10-types.sql`, `20-tables.sql`, `30-indexes.sql`, `35-constraints.sql`, `60-sequences.sql`, `40-routines.sql`, `50-comments.sql` | Preserve original dependency order, including sequences before routines |
| 4 | Core `70-bootstrap.sql`, `80-bootstrap-verify.sql`, `90-verify.sql` | Verify the finite technical bootstrap before identities or runtime changes |
| 5 | Combined `99-verify.sql` | Exact 747-table/logical object union, named indexes and constraints, valid objects/indexes and no DB links |

Run external verification before core; reversing them violates the exact
ten-table precondition. Do not chain the two independent `install.sql` files:
their second empty-user preflight would reject existing objects. The combined
plan uses one shared preflight and preserves the child phase order. The final
union verifier checks object identities as well as counts; a 747-table count
alone is insufficient. Oracle-generated physical names are handled by the
child logical structure checks, as described in the
[combined plan](../../deployment/oracle/README.md).
This combined target puts the selected external tables in the same application
schema and matches the `local-view` Mapper profile. Separate `direct-schema`
owners or original DB-link transport require their own reviewed provisioning
contract; this plan creates neither.

The selected ten tables now have source-bound NLS, collation, table/column
attribute and constraint semantics checked by the external verifier. It does not
change session NLS to make the target pass. Complete production-reachable root
discovery, external non-table DDL, dependencies, target grants, runtime logical
access and business/HTTP acceptance remain incomplete. Plan
validation and structural verification do not establish positive external data
or real application equivalence. See the external package's
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
- a verified matching distribution; a host building it from source also needs
  the public dependency JARs, Maven and plugin seed listed in `README.md`.

An already built distribution carries its public runtime dependencies under
`lib/`, selected by `lib/classpath.txt`; the runtime host needs no separate
dependency directory or Maven plugin seed. Keep Python 3.10+ for the installation
integrity checks and a matching SQLPlus client for the completed Oracle package.
The complete JDK 8 remains a runtime prerequisite for dynamic LCDP compilation.

The runtime configuration must contain at least datasource URL/user/password,
Redis URL or host/port plus an explicit `spring.redis.database`, `server.port`,
`sunway.user.password.encoder.type`, and an explicit
`owned.external-schema.access-mode` matching the built artifact. Keep Redis's
database property consistent with its URL. The observed original clients did not
honor a URL-only database suffix; never flush an existing application's database.
Do not copy production credentials, DB-link strings, license bodies, private
keys, or business rows. `owned-distribution/example/owned-application.properties`
is a shape example, not a deployable credential file.

The optional `verify-clean-device-contract.py --runtime-config` check currently
checks only the presence of the three datasource keys. It does not validate
credentials, Redis isolation, the encoder, the port or the artifact access mode;
those settings still require installation acceptance. The SQL preflight checks
database character sets, not the SQLPlus client encoding or application session
NLS settings. Execute UTF-8 installation SQL with a matching client encoding
(`NLS_LANG=.AL32UTF8` for SQLPlus) and verify the application's comparison/sort
settings against the captured `NLS_COMP=BINARY` and `NLS_SORT=BINARY` contract.
Logical table restoration excludes source hidden system/unused columns; it is
not a claim of identical physical storage.

## Reproducible sequence after installation blockers close

The integrated partial phase order above is available now. The following is the
required sequence for a completed deployment package; it is not an available
successful new-server installation procedure. Full schema/grants, configuration
and installation acceptance remain incomplete. Do not
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
3. Create the dedicated empty application schema/user and grants from the
   completed project installation contract. Validate the matching combined
   plan with `python3 scripts/verify-combined-oracle-installation.py --check-plan`
   and retain its manifest/hash receipt. The canonical SQL entry is
   `deployment/oracle/install.sql`, through an approved SQLPlus connection to
   that user. Its current partial guard stops before DDL; do not bypass it.
   Once the package is completed, preserve the declared external-first,
   core-second, final-union verification order above. Do not run all files under
   `project/sql/`, chain child installers or apply the older external fixture
   alongside the portable external package.
4. Verify the versioned configuration bootstrap before initializing identities
   or starting the application. The combined plan already places
   `80-bootstrap-verify.sql` immediately after `70-bootstrap.sql`; do not reseed
   configuration after runtime changes to make hashes match. Preserve the phase
   receipts and SQLPlus logs. Oracle DDL commits implicitly, so a later failure
   does not roll back earlier phases. This empty-schema plan supplies neither
   an existing-schema upgrade nor data-preserving recovery. Complete external
   domain/access and full configuration acceptance remain project work. The
   older fixture's aggregate statistics are not proof of empty tables or a
   replacement for the combined installation verifiers.
   Install the separately bundled [import template bytes](../../deployment/oracle/import-templates/README.md)
   into the target application's existing persistent working directory before
   startup. Its database file metadata is part of the matching technical
   bootstrap; copying bytes alone does not create those rows:

   ```bash
   python3 "$LIMS_DEPLOY_DIST/installation/scripts/install-import-template-assets.py" \
     --workdir /var/lib/lims-owned
   ```

   Create and assign that directory to the application account first, as in
   step 6. This command refuses conflicting bytes and symbolic-link targets;
   the source-empty `grjl` template remains blocked.
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
8. Prepare every target workflow role, user, organization, membership and
   required permission through the original authenticated management APIs, or a
   separately reviewed identity bootstrap. The first administrator alone is not
   the workflow identity closure. Do this after the initial start and login above
   when using HTTP; do not require HTTP identity creation before an application
   has started.
9. Finish active verification and fixtures, then stop only this target
   application. Use the separate [workflow configuration command](workflows.md)
   with the complete private identity mapping:

   ```bash
   "$LIMS_DEPLOY_DIST/initialize-workflows.sh" /private/path/runtime.properties \
     < /private/path/workflows.json
   ```

   It preserves the 20 nonempty latest definitions and refuses existing workflow
   state; seven source-empty definitions remain blocked. The combined structural
   SQL plan does not invoke this command. Before restarting the same application
   as in step 6, select a dedicated Redis logical database not previously used
   for workflow caches, with matching URL/database properties. Restarting an
   application does not clear Redis; never flush a shared logical database.
   Oracle and Redis stay running. Repeat step 7 for the restarted application,
   then verify the intended workflow candidates, organization boundaries and
   actual business process. Installing definitions alone does not accept those
   behaviors.
10. Retain the distribution and runtime configuration as the rollback pair.
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
remains blocked. The combined selected 747-table installation plan is available
for offline integrity checking, with a guarded normal entry. Outstanding source
identifier/routine dispositions, full configuration bootstrap, target grants,
complete external logical domains and real full-system installation acceptance
must be resolved before declaring a new server ready. A diagnostic structural
replay, synthetic SQL check or empty HTTP 200 cannot close those gates.
