# Current-production core Oracle DDL: partial installation package

This package contains executable, sanitized structural SQL derived from the
2026-11-09 production metadata capture. It is **partial, not deployment-ready**.
It does not initialize the full application. The normal installer deliberately
rejects before any child SQL or DDL while `manifest.json` has blockers.

Check the package from the standalone repository root:

```bash
python3 scripts/verify-core-schema-package.py --check-package
```

This verifies shipped files and per-object hashes, object registry coverage,
portable paths and the partial-install guard. It reads no private acquisition
file and needs no parent checkout or database. The default command, without
`--check-package`, exits 2 until readiness is established. Do not bypass the
installer guard for an application schema; individual phases were executed only
in a dedicated disposable validation schema.

| Phase | Contents |
| --- | --- |
| `00-preflight.sql` | Dedicated empty session user and captured character sets |
| `10-types.sql` | One source TYPE and its TYPE BODY, emitted once |
| `20-tables.sql` | 737 tables with original types/defaults and explicit VARCHAR2/CHAR length semantics |
| `30-indexes.sql` | 344 index definitions; no duplicate PK/UQ backing indexes |
| `35-constraints.sql` | Two constraints separated from composite Oracle metadata |
| `60-sequences.sql` | One sequence with explicit empty-install MIN_VALUE seed |
| `40-routines.sql` | Seven valid functions and three procedures |
| `50-comments.sql` | Reviewed table and column comments |
| `70-bootstrap.sql` | Finite production-derived locale, translations, internal static menus, entity metadata and safe UI configuration |
| `80-bootstrap-verify.sql` | Exact selected configuration counts and field content, using only target-schema reads |
| `90-verify.sql` | Per-table logical columns, PK/UQ column relations and named index checks |

The actual order is in `install.sql`: sequences precede procedures that reference
`NEXTVAL`. This is an empty-user package, not an upgrade, a replacement for a
database backup, or a way to reset existing sequences. No business rows, users,
passwords, database links or production connection strings are included.

The technical bootstrap contains 4,118 rows across seventeen explicitly selected
configuration tables: 2 locales, 1,350 internationalization keys, 1,375 messages,
392 static and 4 published equipment menus, 467 entity metadata rows, 170 hierarchy
relations, one system UI config, one login UI config and 37 source-referenced
select configs, plus 2 dictionary classes, 28 categories and 120 options.
The equipment LCDP module contributes 10 resource/ancestor nodes, 8 published
histories, 78 components, 61 page translations and 12 dependent translation keys.
`bootstrap-provenance.json` pins every emitted statement and selected
source row. Identity/audit-origin columns are not copied. No users, organizations,
roles, passwords or permission grants are copied from production.

Dictionary selection follows the explicit `codeCategoryId` references in the
shipped select configs, not an assumption that every `sys` row is technical.
Actual `sys`/`biz` classification and suspension flags are preserved, with each
parent class and translation dependency. The source categories `dsCategory`,
`dsType`, `fieldCategory` and `publishState` have no options and remain empty;
no synthetic defaults are inserted. Generic selectors and other categories are
outside this finite dictionary closure.

The published `emc.equiptInOut` Java, Oracle Mapper and page source is preserved
with its original versions and component graph. Only its reviewed published
closure is seeded. Draft history, locks, checkout identities and production
`appuuid` are excluded. Long source CLOBs use bounded `TO_CLOB` expressions with
explicit CR/LF characters; the verifier compares full lengths and every chunk.
The exact public MyBatis DTD is allowed as source syntax, not a deployment
endpoint. Other URLs and secret material remain rejected.

Source limitations remain: one page references an undefined keyboard callback;
ten dependent translation keys were absent from the acquired global/page keys.
The source scan method uses request operation-user fields, and its password
check depends on the original deterministic encoder. These are not changed by
the installer or represented as fully accepted business/security behavior.

The bootstrap checks all selected target tables are empty before any INSERT,
then commits the technical configuration together. It is an initial-install
phase, not an idempotent upgrade: rerunning it rejects with `ORA-20120` before
inserting rows. A deployment must resolve remaining bootstrap exclusions first.
Two conflicting translation keys have no arbitrary winner; equal duplicate
messages retain their original rows. Seven other dynamic-resource menus, three
external-target menus and entity relationships outside the structural closure
remain excluded. Other select configs can still require dictionaries/workflows and
other dependencies that this finite subset does not supply.

`manifest.json`, `object-registry.tsv` and `blocked-objects.tsv` retain the source
and rendered hashes, transformations and exclusions. Source artifact paths are
provenance only; raw captures remain private. Exact owner identifier qualifiers
are removed so objects belong to the connected installation user; SQL literals
are preserved. Oracle-generated hidden columns and LOB physical objects are not
copied as application DDL.

The distribution's [first-administrator command](../../../docs/deployment/first-administrator.md)
is now implemented. It creates new installation identities and password policy
from private operator inputs after the structural and technical configuration
phases; it copies no production accounts. The configuration blocker recorded
when this partial package was generated is not a claim that this command is
still missing. Complete application configuration remains outstanding.

Remaining blockers include two tables with invalid original identifier bytes,
two originally INVALID functions, one endpoint-bearing function requiring an
isolated configuration decision, complete target grants and external logical
domains, and application configuration bootstrap (remaining menus, permissions,
dictionaries and workflows, plus dynamic LCDP dependencies). The original
invalid identifiers are not guessed or silently renamed. Excluded routines are
not replaced with successful stubs. See `manifest.json` for exact dispositions.

The package was tested as structural DDL in a disposable user within the existing
local Oracle. That is separate from full application startup, real HTTP,
write/audit/recovery acceptance and another-device restoration. Complete system
readiness remains false; see the [clean-device deployment contract](../../../docs/deployment/clean-device-contract.md).

The current application distribution does not include this SQL directory or its
verifiers. Retain the matching standalone checkout as installation material;
copying only the application distribution cannot initialize a new database.
