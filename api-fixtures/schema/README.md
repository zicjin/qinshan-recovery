# Source-backed local schema restoration

`les-data-source-restoration.sql` is the separate R02 restoration for the
original `T_CORE_LES_DS` and `T_CORE_LES_DS_FIELD` tables, including the later
`ORDERNO` field patch. Its adjacent manifest pins both original release SQL
files, line ranges, statement hashes and the unchanged schema renderer. Run
`python3 scripts/tests/test-les-source-restoration.py` before any application.
The fixture uses the existing exact missing/equivalent/conflicting metadata
checks and local owner/session guards. It contains no data-source business rows
or metadata DML. Oracle DDL implicitly commits; retain logs on partial failure.
This fixture does not extend the seven-table runtime policy or grant lifecycle
acceptance. The coordinator must establish a truthful new schema contract.

`les-data-source-metadata.sql` separately restores the two original
`T_CORE_TABLE` rows and the field-to-source `LESDSID` hierarchy. It checks all
three for absence or source equivalence before metadata INSERTs, preserves an
existing equivalent generated hierarchy ID and rejects conflicting or duplicate
relationships. Use a dedicated approved local runtime session while metadata
administration is idle. Its savepoint covers metadata DML, not DDL or sequence
allocation. Neither fixture changes the baseline or invalidates Redis caches.

`les-pdf-select-config-restoration.json` is a finite source-pinned HTTP plan for
the missing `les-template-category` and `file-acq-preprocess-operation` mappings,
their two code categories and the three original PDF preprocessing options.
The original category POST forces class `sys` and creates a camelCase mapping;
the plan requires actual readback and minimal HTTP PUTs to restore source class
`biz` and the hyphenated UI mapping. It is not an automatic SQL bootstrap or a
general runner. No LES template option values are defined in the original
release block, so none are invented. The coordinator owns preflight, real HTTP
execution, recorded generated IDs, configuration retention and actual page
verification. Prepared fixtures and empty-query success are not API closure.

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

`email-config-associations.sql` restores two original missing association tables,
`T_CORE_EMAIL_CONFIG_RECEIPENT` and `T_CORE_EMAIL_CONFIG_REC_ROLE`, needed by the
original email configuration queries and cascade deletion. Its manifest pins the
original source SQL ranges, generated guarded SQL and existing schema renderer.
Run `python3 scripts/tests/test-email-config-associations.py` before execution.
It uses the same local owner/session, exact metadata and name-collision guards;
Oracle DDL implicitly commits. It contains no business DML, does not change the
baseline, and does not expand the existing seven-table lifecycle policy. An
existing equivalent table skips; inspect retained logs after any partial failure.
The actual HTTP delete failure and after-repair replay are separate diagnostic
observations. A runtime with these additional tables needs its own truthful schema
contract before any strict promotion; this fixture does not grant one.

`core-file-restoration.sql` repairs the historical empty ten-column `T_CORE_FILE`
after real queries referencing `size`, `version`, `permanent` and `createdByOrgId`
failed with ORA-00904. Its manifest pins the original 21-column DDL, comments and
TARGETID index, plus the original SIZE patch and historical ten-column seam.
Run `python3 scripts/tests/test-core-file-restoration.py` first. Apply only in the
existing local `FREEPDB1 / LIMS_RUNTIME_LOCAL`, with the application and jobs idle
until all postchecks finish. The exclusive preflight lock is released by Oracle
DDL commits and cannot protect later statements from concurrent writers.

The fixture requires the exact empty ten-column table and its constraints,
indexes, comments and dependencies before any DDL. It adds eleven columns and
modifies nine existing columns in place, preserving the original table, ID,
existing physical column positions and `PK_OWNED_CORE_FILE` name. The complete
source types, lengths, nullability, 19 comments and TARGETID index are restored;
the manifest explicitly records the different physical column order. An exact
restored empty table is a verified no-op; nonempty, unknown or partial states
fail. No business rows, attachment bytes, baseline changes or atomic DDL rollback
are provided. Preserve execution logs on any failure.

After targeted CoreFile TC/EC metadata invalidation and app recovery, rerun the
original HTTP requests. The explicit `approved-runtime-core-file-v3` observation
policy pins the baseline's ten columns and runtime's restored 21 columns, plus
the existing nine added tables; all other shared metadata must match. Its 492
predicates verify both CoreFile definitions. Existing equal/v1/v2 policies retain
their original contracts and reject this new schema difference. A successful
fixture, observer or differential remains separate from strict HTTP evidence.
