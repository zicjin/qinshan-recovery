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
| `90-verify.sql` | Per-table logical columns, PK/UQ column relations and named index checks |

The actual order is in `install.sql`: sequences precede procedures that reference
`NEXTVAL`. This is an empty-user package, not an upgrade, a replacement for a
database backup, or a way to reset existing sequences. No business rows, users,
passwords, database links or production connection strings are included.

`manifest.json`, `object-registry.tsv` and `blocked-objects.tsv` retain the source
and rendered hashes, transformations and exclusions. Source artifact paths are
provenance only; raw captures remain private. Exact owner identifier qualifiers
are removed so objects belong to the connected installation user; SQL literals
are preserved. Oracle-generated hidden columns and LOB physical objects are not
copied as application DDL.

Remaining blockers include two tables with invalid original identifier bytes,
two originally INVALID functions, one endpoint-bearing function requiring an
isolated configuration decision, complete target grants and external logical
domains, and application configuration bootstrap (menus, permissions,
dictionaries, workflows, dynamic LCDP and initial administrator). The original
invalid identifiers are not guessed or silently renamed. Excluded routines are
not replaced with successful stubs. See `manifest.json` for exact dispositions.

The package was tested as structural DDL in a disposable user within the existing
local Oracle. That is separate from full application startup, real HTTP,
write/audit/recovery acceptance and another-device restoration. Complete system
readiness remains false; see `docs/deployment/clean-device-contract.md`.
