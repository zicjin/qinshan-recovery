# Production-derived external table subset

This is a portable **partial** structure package for ten selected HT/SP/XFP
tables. It preserves 1,303 columns, 33 captured default expressions, 26
constraints and 45 logical indexes. The additional defaults and thirteen
constraint-to-index links were obtained through metadata-only, read-only
production queries. No business rows or connection definitions were read for
this package. Raw receipts remain private; their paths and SHA-256 values are
provenance references, never installation dependencies.

Run from the standalone checkout:

```bash
python3 scripts/verify-core-schema-package.py \
  --manifest deployment/oracle/external-schema/manifest.json --check-package
```

The existing package verifier checks every shipped file, CREATE object and
registry entry without reading parent/private inputs. The default command
without `--check-package` exits 2. `install.sql` rejects with ORA-20100 before
including any child SQL. Do not remove that guard to install a complete system.

| Phase | Scope |
| --- | --- |
| `00-preflight.sql` | Dedicated empty session user; rejects SYS/SYSTEM and altered CURRENT_SCHEMA |
| `20-tables.sql` | Original column order, logical types, BYTE/CHAR, captured defaults and named non-null constraints |
| `30-indexes.sql` | All 45 captured logical indexes, including NONUNIQUE backing indexes |
| `35-constraints.sql` | Thirteen PK/UNIQUE constraints with explicit USING INDEX and original validation state |
| `90-verify.sql` | Exact columns/defaults, constraint definitions and backing links, indexes and live empty-table counts |

The table phases target the **connected installation user**, with no fixed
schema, PDB, container or production endpoint. They are for an empty diagnostic
schema; they are not an in-place upgrade for the current application schema.
Oracle DDL commits implicitly. Repeated installation must stop at the empty-user
preflight; a failed partial application requires inspection and explicit cleanup,
not a claimed transaction rollback.

`structure.json` contains the sanitized selected metadata. The renderer records
the explicit conversion of source system-generated NOT NULL names to user-named
constraints. Source index names, UNIQUENESS and actual P/U backing associations
are retained; a NONUNIQUE index can legitimately enforce a primary/unique key.
The one source ENABLE NOVALIDATE unique constraint remains NOVALIDATE.

This package supersedes the old table-only fixture **for new installation
preparation**. The existing `api-fixtures/schema/external-logical-clone.sql` and
its receipts remain transitional provenance and are not silently modified or
applied to the running schema. Its older aggregate runtime verifier is not an
exact verifier for this package.

Complete non-table objects, cross-domain dependencies, grants, character-set/
collation closure, integration into the core installer, runtime access mapping,
business fixtures and real HTTP acceptance remain outstanding. This package's
integrity or disposable-schema test does not establish complete external-domain
or new-device restoration. Overall deployment readiness remains false; follow
the [clean-device contract](../../../docs/deployment/clean-device-contract.md).
