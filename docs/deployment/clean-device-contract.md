# Clean-device deployment contract

This document defines the boundary for deploying the owned source-only
distribution on a new server. It is deliberately explicit about the current
gap: this repository does **not** contain a complete current-production Oracle
schema bootstrap. A clean clone therefore cannot be advertised as a complete
from-scratch LIMS restoration until an approved, source-bound core schema
package is delivered.

`project/sql/` contains historical release and upgrade SQL. Its files are not a
current production DDL snapshot and must not be executed as a directory-wide
initialization script. The files under `api-fixtures/schema/` are targeted,
versioned fixtures. They assume an existing application schema and do not
create an Oracle user, PDB, grants, Redis instance, or business rows.

The current external logical-clone fixture is structure-only. It creates the
ten approved HT/SP/XFP tables and indexes in the application schema, creates no
DB link, and inserts no rows. It is valid for a `local-view` access path after
the application schema exists:

```bash
python3 scripts/verify-clean-device-contract.py
python3 scripts/verify-external-logical-clone-fixture.py
```

Apply it only through an approved SQLPlus channel connected to the intended
runtime PDB and schema. Oracle DDL commits implicitly; retain the SQLPlus log.
To remove only this fixture, use:

```bash
scripts/verify-clean-device-contract.py
# after confirming the target is the disposable runtime schema:
sqlplus ... @api-fixtures/schema/external-logical-clone-restore.sql
```

The restore script drops only the ten fixture tables with cascading
constraints. It does not restore a baseline or delete business data.

## Required operator-provided prerequisites

Before building or starting the application, the deployment operator must
provide, outside Git and outside the distribution:

- a supported Java 8 runtime;
- one Oracle instance/PDB and one Redis instance, with network reachability;
- an application schema/user, password, grants, charset and connection pool
  configuration;
- the approved **core schema bootstrap package** for the target software
  version; this package is currently unavailable in this repository;
- a private runtime properties file containing datasource, Redis, session and
  required feature settings;
- public dependency JARs and the Maven plugin seed listed in `README.md`.

The runtime configuration must contain at least datasource URL/user/password,
Redis URL or host/port, `server.port`, and `sunway.user.password.encoder.type`.
Do not copy production credentials, DB-link strings, license bodies, private
keys, or business rows. `owned-distribution/example/owned-application.properties`
is a shape example, not a deployable credential file.

## Reproducible sequence once the core package exists

1. Install and start the operator-managed Oracle/PDB and Redis services.
2. Create the application schema/user and grants using the operator-managed
   core schema package. Do not run all files under `project/sql/`.
3. Run the core schema verifier supplied with that package and retain its hash
   receipt.
4. Apply `api-fixtures/schema/external-logical-clone.sql` to the same runtime
   schema if the selected profile uses `local-view`; otherwise apply the
   approved direct-schema package and use its explicit mapper profile.
5. Run `verify-external-logical-clone-runtime.py` against the disposable
   runtime. It is read-only and checks object closure, row count and DB-link
   absence.
6. Build and verify the source-only distribution:

   ```bash
   bash scripts/verify-external-artifacts.sh --source-only
   bash scripts/build-owned-distribution.sh
   python3 scripts/verify-source-only-checkpoint.py \
     --distribution owned-distribution/target/distribution
   SOURCE_ONLY_CHECKPOINT=1 python3 owned-distribution/tests/test-package.py
   ```

7. Start one Spring application with `owned-distribution/start.sh
   /private/path/runtime.properties`, then check the public-key/readiness
   endpoint and perform a narrow authenticated smoke. The application does
   not create Oracle schemas or Redis data automatically.
8. Retain the distribution and runtime configuration as the rollback pair.
   Rollback replaces only the application distribution; it does not recreate
   Oracle or Redis.

The contract verifier validates the repository contract and reports
`deployment_ready: false` while the core schema package is unavailable. A
caller must treat that field as a deployment gate; a zero process exit only
means that the fail-closed contract was read successfully. The block is
truthful, not an optional warning. The current state is therefore:
source-only build/package is available; targeted schema fixtures and the
external logical clone are available; complete clean-device Oracle restoration
remains blocked.
