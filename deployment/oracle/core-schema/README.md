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

The technical bootstrap contains 4,816 rows across twenty-one explicitly selected
configuration tables: 2 locales, 1,350 internationalization keys, 1,375 messages,
392 static and 10 reviewed published LCDP menus, 467 entity metadata rows, 170 hierarchy
relations, one system UI config, one login UI config and 37 source-referenced
select configs, plus 2 dictionary classes, 28 categories and 120 options.
Seven published LCDP modules contribute 37 resource/ancestor nodes, 29 published
histories, 229 components, 123 page translations and 242 dependent translation keys.
The two original file-service Java resources and their ancestors add four resource
nodes and one genuine published history, for 41 resources and 30 histories overall.
`lcdp.file.server.FileScript` is the captured active-only resource with parent `0`;
the original runtime explicitly permits its absent history. The bootstrap preserves
that exception without creating a history or changing another resource's contract.
Two exact production technical constants, `FILE-EXTENSIONS` and `FILE-SPACELIMIT`,
provide the original upload validator's allowed extensions and size limit. Their
fields and source rows are pinned; no other system constants are copied.
The access configuration adds all 188 captured request-to-menu mappings and
three definitions each for role and user permission types. The two suspended
definitions stay suspended; selectable queries return only `LASTSUSPENDEDFLAG=0`.
Seven original request mappings refer to menu `9302`, absent from the captured
production menu inventory. They remain recorded as source orphan references;
the installer neither invents that menu nor drops its mappings. Permission
panel GRID settings are limited to the reviewed declarative column grammar.
No accounts, memberships or grants are copied, and this seed does not turn on
`sunway.api.check-access-permission`. That original aspect is explicitly opt-in;
the captured external production configuration does not set it. The separate
`T_CORE_MENU_EXT` audit definitions remain outside this subset.
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

The published equipment-in/out, folder/order/project/sample information,
report lookup and report delivery Java, Mapper and page sources are preserved
with their original versions and component graphs. Only these reviewed published
closures are seeded. Draft history, locks, checkout identities and production
`appuuid` are excluded. Long source CLOBs use bounded `TO_CLOB` expressions with
explicit CR/LF characters; the verifier compares full lengths and every chunk.
The exact public MyBatis DTD is allowed as source syntax, not a deployment
endpoint. Other URLs and secret material remain rejected.

Source limitations remain: one page references an undefined keyboard callback;
ten dependent translation keys were absent from the acquired global/page keys.
The source scan method uses request operation-user fields, and its password
check depends on the original deterministic encoder. These are not changed by
the installer or represented as fully accepted business/security behavior.
Five additional title keys were confirmed absent by exact production metadata
reads. Five detail page targets referenced by the source are also absent from
the acquired resource inventory. File upload controls require storage acceptance
in addition to the shipped file-service resources. Excel import/export also
requires actual import template records, configuration and file bytes, which this
subset does not include. A page DTO does not prove those controls or missing
detail paths work.

The bootstrap checks all selected target tables are empty before any INSERT,
then commits the technical configuration together. It is an initial-install
phase, not an idempotent upgrade: rerunning it rejects with `ORA-20120` before
inserting rows. A deployment must resolve remaining bootstrap exclusions first.
Two conflicting translation keys have no arbitrary winner; equal duplicate
messages retain their original rows. One other dynamic-resource menu, three
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

The source-only distribution carries the exact selected SQL/metadata and offline
verifiers under `installation/`, with a dedicated README and outer file hashes.
The bundle needs no parent or private source captures; it retains the partial
guard and cannot yet initialize the complete application on a new database.
Retain the matching standalone checkout for the full deployment instructions.
