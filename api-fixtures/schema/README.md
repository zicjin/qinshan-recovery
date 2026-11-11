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

`external-index-alignment-20261112.sql` replaces exactly four legacy UNIQUE
constraint-name backing indexes with the formal NONUNIQUE index names in
`deployment/oracle/external-schema/{structure.json,30-indexes.sql,35-constraints.sql}`.
Validate the complete source/SQL contract offline first:

```sh
python3 scripts/verify-external-index-alignment.py
```

With the application and every other schema writer stopped, retain an approved
backup and run the SQL in a clean `LIMS_RUNTIME_LOCAL` SQL*Plus session on
`FREEPDB1` using `@api-fixtures/schema/external-index-alignment-20261112.sql`.
The principal must already have SELECT access to `DBA_CONSTRAINTS` so the script
can reject incoming foreign keys across all owners before migration DDL. The
script grants no permissions. It stages index visibility, keeps the old index
while the constraint is disabled, enables and validates the constraint using the
formal target index, and drops the old index only after verifying no constraint
references. DDL commits implicitly; preserve logs and inspect a failure before
rerunning. Only the explicitly guarded intermediate states resume. Existing rows
and the separate `UKT_LIMS_FOLDERS_FOLDERNO` NOVALIDATE constraint are preserved;
record full content hashes and metadata before/after and repeat the script to
verify the final no-op state. This migration does not refresh the application.

`external-schema-clone-manifest.json` is a separate production dependency
inventory for Oracle domains reached through `@LIMS_CZJKY_HT`,
`@LIMS_CZJKY_SP` and `@LIMS_CZJKY_XFP`. It records the production link
topology as provenance, source-bound remote object names, dictionary metadata
counts and blocked gates. The final owned runtime must reproduce the logical
object/column/query contract, but does not have to recreate Oracle DB-link
transport: an approved isolated direct schema or local-view clone may be used,
with explicit qualifier rewrites only. It is not a DDL or bootstrap fixture:
production connection definitions, credentials, endpoints and business rows
remain private, and the existing `schema_fixture.py` local-owner allowlist is
unchanged. Provisioning requires an approved isolated target package before
any logical clone DDL is applied.

`production-ddl-alignment-20261109.sql` and
`production-ddl-field-alignment-20261109.sql` are reviewed, one-time incremental
alignment scripts for `LIMS_RUNTIME_LOCAL` in `FREEPDB1`. Their adjacent manifests
bind a sanitized production metadata subset in
`production-ddl-source-20261109.json`; raw extraction and execution receipts stay
private. Production access used metadata SELECT within READ ONLY transactions,
not a business-data export. Run the source-pinning regression with
`python3 scripts/tests/test-production-ddl-alignment.py`.

The first script adds the missing acquisition-file table and four indexes,
widens eight VARCHAR2 columns and removes three narrower NUMBER precision
restrictions. The second aligns nine VARCHAR2 widths after checking existing
value lengths, replaces REMARK with CLOB only when the file table is empty and
the column has no index/constraint dependency, and restores the production
STRINGTOSCIENCE function's text-buffer widths. These scripts are preconditioned
one-time migrations, not repeatable bootstrap scripts: preserve logs and inspect
partial state on failure. DDL implicitly commits. The empty REMARK conversion
changes its column ordinal. Do not run against baseline or production, reset
sequences, copy database links, or remove test extensions. Two populated audit-ID
columns retain their broader NUMBER type pending a separate precision migration;
invalid source identifiers and Oracle-generated hidden columns are not guessed.
Existing schema policies are unchanged, so this runtime delta is diagnostic
until explicitly covered by a subsequent milestone contract.

The original `project/sql/` release files are historical inputs, not a current
production DDL snapshot; see [their scope](../../project/sql/README.md). In
particular, `report-config-category-restoration.sql` adds a stock 14.1.5
compatibility column. The 2026-11-09 production metadata also lacks CATEGORY,
so this fixture must not be described as restoring a column lost during import.
Its historical CREATE TABLE source and Mapper pins stay unchanged. Current
production alignment and explicit stock/test extensions remain separate.

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

`external-folder-unique-alignment-20261111.sql` is the narrow local correction
for the earlier external logical-clone fixture's missing
`IDX_T_LIMS_FOLDERS_FOLDERNO` / `UKT_LIMS_FOLDERS_FOLDERNO` pair. Its adjacent
manifest pins the exact production-derived index and constraint statements in
`deployment/oracle/external-schema/30-indexes.sql` and `35-constraints.sql`.
Run `python3 scripts/tests/test-external-folder-unique-alignment.py` first.

Execute only through the approved existing local Oracle SQLPlus channel,
connected as `LIMS_RUNTIME_LOCAL` in `FREEPDB1` with unchanged current schema and
application/schema writers idle. Before either DDL, the fixture checks the exact
source `FOLDERNO` column, any existing index and any named or equivalent single
column unique constraint. An absent index is created as the original normal,
nonunique, ascending one-column index; an absent constraint is added using that
index with the original **ENABLE NOVALIDATE** state. Existing equivalent
objects are skipped, while conflicts reject without replacement. Source
duplicate groups are counted and must remain unchanged; no row is inserted,
deleted, merged or rewritten, and NOVALIDATE is not promoted to VALIDATE.

Oracle DDL commits implicitly. If the index succeeds and a later step fails,
SQLPlus ROLLBACK cannot remove it; retain the error and inspect the exact state.
A repeat is safe only for the declared equivalent or absent paths. This is not a
new-device installer, an automatic repair of arbitrary schema drift, a baseline
change or API/strict evidence. It restarts no application, Oracle or Redis and
changes no cache or privilege.

`external-production-structure-alignment-20261112.sql` is **stage 1 only** of
aligning the existing local external clone to the formal production-derived
package. It restores all 33 formal default expressions without changing rows,
constraints, indexes, the empty-schema installer or runtime acceptance policy.
The adjacent JSON pins tracked formal structure, package manifest and SQL files;
no private receipt is needed. Verify it from a standalone checkout:

```bash
python3 scripts/verify-external-logical-clone-fixture.py \
  --alignment-manifest api-fixtures/schema/external-production-structure-alignment-20261112.json
python3 scripts/tests/test-external-production-structure-alignment.py
```

The coordinator must stop the application and other schema writers, establish
the approved local target and retain a backup plus before/after content hashes.
Execute through the existing approved SQLPlus channel only as
`SESSION_USER=CURRENT_SCHEMA=LIMS_RUNTIME_LOCAL`, `CON_NAME=FREEPDB1`. Before any
ALTER TABLE, the script checks all ten tables and 1,303 column definitions,
including byte/character semantics, nullable, collation, identity/virtual flags,
all default expressions, ordinary writable table attributes, absent triggers and
formal NLS. Only the 33 source defaults may be absent; additional or conflicting
defaults reject, including explicit `DEFAULT NULL`. Full LONG expressions are
compared after trimming outer spaces, matching the formal verifier. Existing
source-equivalent defaults skip, and missing defaults are reread immediately
before their individual ALTER. The terminal preflight requires all 33 defaults.
The script executes no business-data SELECT or DML; independent content hashes
remain the coordinator's evidence that rows stayed unchanged.

Oracle DDL **implicitly commits**. All known conflicts fail before the first
ALTER, but permissions, storage or concurrent changes can still cause a partial
application. Save the SQLPlus log and inspect all 33 defaults after failure.
With writers still stopped, rerun only if every default is missing or exactly
source-equivalent and the complete preflight still passes; completed items skip.
Any other state stops the migration. Restore from the retained approved backup
when recovery is required; no automatic inverse migration is supplied. SQLPlus
`ROLLBACK` cannot undo successful ALTER statements.

The **13 redundant generated NOT NULL checks and four differing PK/UNIQUE
backing-index definitions/links remain unresolved**. This stage deliberately
performs no drop, index rebuild, constraint disable/recreate or nullability
change. A separate reviewed sequence must preserve constraint semantics before
those differences can be removed. Successful default restoration does not make
the local schema identical to the formal package, grant strict evidence or
relax a runtime policy. No application, Oracle or Redis restart is part of this
fixture.

`external-clearance-integer-alignment-20261112.sql` is a separate **stage 0**
prerequisite discovered when stage 1 correctly rejected the two clearance
columns `CCID` (position 104) and `VERSION` (105). Formal `20-tables.sql` lines
405–406 declare `NUMBER(*,0)`; the earlier clone had unconstrained `NUMBER`.
Verify the source pins and reviewed guarded SQL before approved local execution:

```bash
python3 scripts/verify-external-logical-clone-fixture.py \
  --integer-alignment-manifest api-fixtures/schema/external-clearance-integer-alignment-20261112.json
python3 scripts/tests/test-external-clearance-integer-alignment.py
```

Use a dedicated clean SQLPlus session as `LIMS_RUNTIME_LOCAL` in `FREEPDB1`,
with unchanged current schema, application/schema writers stopped and retained
backup/content hashes. All identity, table, two-column shape, empty-table and
constraint/index dependency checks finish before any ALTER. Only exact nullable
NUMBER columns with precision NULL and scale NULL or 0, no default and the source
positions qualify. The table must have **zero rows even on an equivalent rerun**.
Unconstrained columns become `NUMBER(*,0)`; already exact columns skip. No row is
converted or written. The exclusive preflight lock is released by DDL commits;
stopped writers remain a requirement. An explicit final COMMIT releases the lock
when both columns already match, so this must never share pending session work.

DDL implicitly commits. Preserve original failure and execution logs. After a
partial failure, inspect both columns and row count; only the same empty allowed
states permit rerun, which skips completed columns. Other states require explicit
backup recovery, never a claimed SQL rollback. Then rerun unchanged stage 1 to
check all 1,303 source columns and restore defaults. Neither stage changes the
formal installer or relaxes NOT NULL/index or strict acceptance requirements.
