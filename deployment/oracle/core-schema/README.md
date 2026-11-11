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
| `20-tables.sql` | 737 tables with original types/defaults plus two explicitly transformed portable tables; explicit VARCHAR2/CHAR length semantics |
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

The technical bootstrap contains 17,493 rows across thirty-one explicitly selected
configuration tables: 2 locales, 5,339 internationalization keys, 5,397 messages,
392 static and 11 reviewed published LCDP menus, 467 entity metadata rows, 170 hierarchy
relations, one system UI config, one login UI config and 37 source-referenced
select configs, plus 2 dictionary classes, 224 categories and 1,056 options.
Seven published LCDP modules contribute 37 resource/ancestor nodes, 29 published
histories, 229 components, 123 page translations and 242 dependent translation keys.
The two original file-service Java resources and their ancestors add four resource
nodes and one genuine published history, for 41 resources and 30 histories before the courier addition.
The published `emc.reportSendExpress` closure adds its module node, three
resources (page v20, Oracle mapper v14, Java service v23), three effective
histories, 29 components, 33 page translations, 26 page i18n-code rows and menu
`20250200002`. Its shared `emc` category is reused. Overall totals are 45
resources and 33 histories. The preceding 17,397 INSERT statements and original
row hashes remain unchanged; this addition contributes 96 rows. Its Java mail
footer is the one declared source transformation, documented in
[report email configuration](../../../docs/deployment/report-send-mail-footer.md).
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
`T_CORE_MENU_EXT` now includes all 41 captured `audit_def` service definitions,
with their exact service labels and structural table references. Forty remain
enabled and one remains disabled; `EXT` is NULL in every captured row. These
definitions let the original workflow selectors and process queries resolve the
business service. They contain no process instances, candidate identities,
organizations, role grants or workflow XML. The original list and generic detail
queries apply `ENABLE='1'`; a disabled definition is not made visible by this
installation.
The four audit configuration tables add 186 table switches, 3,038 old-audit field
switches, six new-audit table definitions and 106 new-audit column switches.
They preserve every captured row, including disabled switches, duplicate targets
and original stale references. The three SQL configuration fields are all NULL;
no audit records or actor/organization/time provenance are included. This seed
changes neither runtime profiles nor operation switches. See
[audit configuration](../../../docs/deployment/audit-configuration.md).

Forty-five numbering definitions and two segment definitions retain their original
IDs, NULL names, patterns, codes and ordering. Only fresh empty installation sets
`JSONPARAM` to NULL; production allocation state is not read or copied. Five source
rule IDs are not present as core tables and remain intact. See
[numbering configuration](../../../docs/deployment/numbering-configuration.md).

`bootstrap-provenance.json` pins every emitted statement and selected
source row. Identity/audit-origin columns are not copied. No users, organizations,
roles, passwords or permission grants are copied from production.

Dictionary selection follows the explicit `codeCategoryId` references in the
shipped select configs and the literal consumers in original Java, Mapper and
page resources, not an assumption that every `sys` row is technical.
Actual `sys`/`biz` classification and suspension flags are preserved, with each
parent class and translation dependency. The source categories `dsCategory`,
`dsType`, `fieldCategory` and `publishState` have no options and remain empty;
no synthetic defaults are inserted. The additional source-consumed projection
preserves the empty `contentResult`, `queryGroup` and `reportRemark` categories.
Its 196 categories and 936 options are bound to read-only production receipts
and the original consumer inventory in `bootstrap-provenance.json` under
`source_dictionary`; all preceding bootstrap statements and row hashes remain
unchanged. The 25 literal categories absent from production are recorded there
without fabricated definitions. Dynamically constructed categories and other
unreviewed dictionary consumers remain outside this finite closure.

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
in addition to the shipped file-service resources. Two original import-template
definitions, eleven configuration rows and one technical file metadata row are
included. The original unmapped rows are preserved; `grjl` has no linked file or
mapped columns and remains blocked. The separately shipped
[template asset package](../import-templates/README.md) supplies the reviewed
`reportSendInfo` workbook and explicit file placement. Production authors are
excluded, the fresh download count is zero, and the delivered file size reflects
metadata sanitization. Template installation does not prove business import/export
or missing detail paths work.

The bootstrap checks all selected target tables are empty before any INSERT,
then commits the technical configuration together. It is an initial-install
phase, not an idempotent upgrade: rerunning it rejects with `ORA-20120` before
inserting rows. A deployment must resolve remaining bootstrap exclusions first.
Three conflicting translation keys have no arbitrary winner; equal duplicate
messages retain their original rows. The complete technical language capture adds
the namespaces omitted by the earlier CORE/GIKAM/EMC prefix selection, including
table-field labels, LCDP and other framework labels. The previous 8,254 rows and
their INSERT statement bytes are unchanged. The supplier license-capacity login
message remains excluded; three explicit license-related field labels contain
UI text only and carry an allowlist in `i18n-runtime-source.json`.
That source metadata records exact reviewed code, description, default message
and unique locale values for a separate versioned existing-database upgrade;
it contains source references and exclusions, not an HTTP message-map fallback.
It does not overwrite existing translations or make the full installer ready.
Three
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

`CSFF` and `QYJWDDR` are now included through an explicit identifier conversion.
Production stores nine column names as GBK bytes in an AL32UTF8 dictionary;
`DBMS_METADATA` replaces those bytes and cannot provide usable source names.
The source RAW bytes, Oracle GBK-to-UTF8 result and reverse conversion all agree.
The package transforms those nine names to quoted UTF8 identifiers and preserves
six ASCII names. All fifteen VARCHAR2 BYTE widths, ordinals and nullable/default
contracts are retained. `portable-identifier-provenance.json` records source and
target hashes; this is not unchanged replay of the invalid source bytes. Existing
737-table CREATE bodies and unrelated configuration remain unchanged.

Remaining blockers include dynamic SQL/configuration consumer acceptance of
those mapped names, two originally INVALID functions, one endpoint-bearing function requiring an
isolated configuration decision, complete target grants and external logical
domains, and application configuration bootstrap (remaining menus, permissions,
dictionaries and workflows, plus dynamic LCDP dependencies). The original
identifier conversion is explicit and reversible from its source metadata. Excluded routines are
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
