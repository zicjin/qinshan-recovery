# Source-backed local schema restoration

`missing-runtime-tables.sql` contains only 128 original Oracle CREATE TABLE,
ALTER TABLE, COMMENT and CREATE INDEX statements for these seven missing tables:

- `T_EMC_INVOICE` and its read-query dependency `T_EMC_RETURNED`;
- `T_EMC_QC_ITEM`;
- `T_EMC_SEAL_RULE` and `T_EMC_SEAL_RULE_DETAILS`;
- `T_CORE_EMAIL_TEMPLATE` and `T_CORE_EMAIL_CONFIG`.

The JSON manifest binds both whole original source files, each original statement's
line range and SHA-256, the rendered statement SHA-256 and the complete fixture
SHA-256. Statements preserve original text. The only transformation replaces the
fullwidth semicolon terminating the invoice `INVOICEPROJNAME` ALTER with ASCII `;`.
No seed records or other DML are included. `COPYFLAG` has no matching original
invoice Oracle DDL and is deliberately absent; projected `FILENAME` and
`TEMPLATENAME` fields are not table columns to invent.

Run from the standalone repository root:

```bash
python3 scripts/schema_fixture.py --plan
python3 scripts/schema_fixture.py --render > /tmp/lims-local-schema-restore.sql
```

The helper verifies provenance before emitting a plan or script. `--plan` without
`--metadata` lists candidate tables, not observed database state. For offline
metadata testing, `--plan --metadata /private/metadata.json` accepts a complete
mapping of target names to `null` (absent) or exact logical table definitions;
incomplete or incompatible entries fail. The helper opens no connection and has
no credential, endpoint, arbitrary-SQL or process-control argument.

Execute the rendered script only through the approved local Docker SQLPlus
channel. Set `CURRENT_SCHEMA` to `LIMS_RUNTIME_LOCAL` using the existing authorized
session. The generated script does not alter the current schema itself. It
requires `SESSION_USER` to be `SYS` or `LIMS_RUNTIME_LOCAL`, and session
`NLS_LENGTH_SEMANTICS=BYTE`; explicit BYTE declarations and original unspecified
VARCHAR2 declarations then have a deterministic meaning. The dictionary checks
require Oracle 12c or later (`ALL_CONSTRAINTS.SEARCH_CONDITION_VC`). Local-instance
identity remains the executor/observer's responsibility: a schema name alone
cannot establish which Oracle instance a human connected to.

The SQLPlus script fails on any SQL/OS error and disables substitution. Before
any DDL it verifies every existing target table, including ordered columns,
types/precision/length/nullability/default absence, primary keys, normal indexes,
comments, constraints and absence of triggers. Unexpected objects or planned
constraint/index name collisions fail preflight. Equivalent tables are skipped,
including their comments and indexes; absent tables are created and verified.
Physical storage, tablespaces, statistics and LOB storage indexes are outside the
logical equivalence comparison. Applications and other schema writers must stay
idle during execution; the script rechecks absence immediately before creation
but does not provide a lock against concurrent DDL.

**Oracle DDL automatically commits.** SQLPlus `ROLLBACK` cannot undo a successful
CREATE/ALTER/COMMENT/INDEX. Preflight prevents known conflicts before creation,
but unexpected permission/storage/concurrent errors may leave partial schema
changes. Preserve the failed execution log and inspect the local schema; the
helper rejects partially defined tables on the next run and never patches or
drops them automatically. There is no automatic DDL cleanup or atomicity claim.

After execution, use the existing local observer to record baseline immutability,
runtime schema hash changes and runtime reuse, then use real HTTP to diagnose
behavior. These fixtures create no business rows. The original framework caches
column lists in Redis `TC` and entity metadata in `EC`; an application restart
alone does not prove shared Redis metadata is fresh. Diagnose and invalidate
only the corresponding source-backed metadata keys through an approved targeted
mechanism when needed; this helper never flushes Redis.

The helper, fixtures, manifest and original source SQL files are central harness
binding inputs. Observed schema hashes remain runtime-contract inputs. This work
does not change application source, provide API equivalence, promote strict
evidence or close `API-ALIGNMENT-GATE`.

`invoice-returned-hierarchy.sql` is a separate metadata DML fixture, not part of
this directory's seven-table schema DDL fixture. It restores only the three
original invoice/return `T_CORE_TABLE` and `T_CORE_TABLE_HIERARCHY` records needed
by the original cascade deletion path. Its adjacent JSON binds the original
statements and fixture hashes. All three absence/equivalence/collision checks
run before any INSERT; equivalent rows are skipped and conflicts fail. Execute
only through the authorized local runtime SQLPlus session with no pending work;
the script requires `CURRENT_SCHEMA=LIMS_RUNTIME_LOCAL` and session user `SYS`
or `LIMS_RUNTIME_LOCAL`, and commits the metadata DML together. This fixture
writes no business rows and performs no DDL or cache operations. Real HTTP
cascade replay and targeted metadata cache diagnosis remain separate work.
