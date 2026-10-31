# Disposable database behavior probes

Run the fixed schema and SQL through a new locally available digest-pinned Linux amd64 image:

```sh
python3 scripts/run-database-probe.py --dialect postgresql \
  --image public.ecr.aws/docker/library/postgres@sha256:0ea6700a3b4f0ae6ce746519073558aed4d88a79d8d07622a9a644946c7319c4 \
  --output .scratch/sunway-supplier-exit-api-first/archive/historical/t15-postgresql-probe.json
python3 scripts/run-database-probe.py --dialect oracle \
  --image ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4 \
  --output .scratch/sunway-supplier-exit-api-first/archive/historical/t15-oracle-probe.json
```

The runner never pulls images, reads private configuration, accepts arbitrary SQL or connection strings, reuses containers, or mounts host data. It creates a unique disposable container with network `none` for PostgreSQL or a fresh Docker `--internal` bridge for Oracle (Oracle IPC requires a non-loopback private IP), no published ports, a generated local credential supplied through a mode-0600 temporary env file, and a 3 GiB memory limit. For Oracle it verifies `Internal=true`, the only attached network, and one endpoint matching its own container before SQL runs. Cleanup removes its own container, anonymous volumes and internal network. Evidence records exact image ID/digest, SQL/runner hashes, source revision/dirty state, database version, isolation settings, values emitted by fixed SQL, exit status and cleanup outcome. Missing images and startup errors yield `not-executed`, never passing checks. Startup wait defaults to 600 seconds with progress output.

`oracle.sql` / `postgresql.sql` create synthetic tables. `*-probe.sql` inserts fixed non-production rows and executes actual predicates/queries for active user/wrong digest/inactive user, permission ancestors, personnel pagination, equipment filter, consumable stock and one department aggregate report. Additional SQL exercises a sequence gap after rollback, leap-day timestamp formatting, regex matching, text/binary LOB values, committed/rolled-back inserts, and duplicate-key vendor errors. The literal `known-digest` is a fixture predicate value, not a password encoder or authentication implementation. These are synthetic schema probes, **not LIMS login, authorization, service/mapper, report generation or UI acceptance**. They do not prove the production database's full semantics. The LOB sample is deliberately small; large/streaming LOB behavior, collation, concurrent transactions, identity allocation by business code, time zones, and application error translation remain unverified.

Ticket 15 stays open until actual Oracle/PostgreSQL application slices and the complete behavior matrix have evidence. Existing Oracle remains the supported/fallback baseline; no final mainline decision or validated PostgreSQL stop-loss conclusion is made here. No runtime profile or long-term dual database implementation is added.

Validation:

```sh
python3 scripts/tests/test-database-probe.py
DBPROBE_POSTGRES_IMAGE=public.ecr.aws/docker/library/postgres@sha256:0ea6700a3b4f0ae6ce746519073558aed4d88a79d8d07622a9a644946c7319c4 \
  python3 scripts/tests/test-database-probe.py
```

The optional integration test corrupts only a temporary SQL copy and verifies that a real PostgreSQL syntax/object error produces `executed-fail` and cleanup. The Docker boundary tests verify fail-closed missing/unpinned/wrong-architecture images without claiming SQL behavior from mocked Docker output.

Oracle imported image provenance is recorded in `.scratch/sunway-supplier-exit-api-first/archive/historical/t15-oracle-image-provenance.json`: original source manifest digest `366fe857...`, exact config and six compressed layer hashes/sizes verified in the source archive, and local Docker-normalized manifest digest `cd3dff...`. These digests are distinct. Oracle reports 26ai Free Release 23.26.3. The source manifest contains only public image metadata. Oracle SQL*Plus enables server output after selecting the PDB, so duplicate-key PL/SQL results are actually captured.
