# Combined Oracle installation: partial diagnostic plan

`installation-manifest.json` binds the existing 739-table core package and the
selected ten-table external package to one ordered, portable installation plan.
The combined normal entry is `install.sql`. It deliberately raises `ORA-20100`
before any child SQL or DDL: complete configuration, grants, external logical
domains and full-system installation acceptance remain blocked. This is a
finite integration of two partial packages, not a complete new-device installer.

Validate from the standalone checkout, with Python 3 and no database or private
acquisition inputs:

```bash
python3 scripts/verify-combined-oracle-installation.py --check-plan
```

The integrity check exits 0 only when the pinned package manifests, exact child
files, fixed phase order, shared preflight, object namespaces and final union
verifier agree. It reports `plan_valid: true`, `deployment_ready: false` and
`complete_external_domains: false`. Without `--check-plan`, it exits 2 and must
stop deployment. It never connects to Oracle, executes SQL, emits an unguarded
installer or accepts an override flag. A matching standalone checkout carries all
selected installation inputs; neither a parent planning checkout nor a private
source receipt is required. Source-only distributions also carry the exact
plan dependencies under `installation/`, with both verifiers and a dedicated
README. From the distribution root run:

```bash
python3 installation/scripts/verify-combined-oracle-installation.py --check-plan
```

Its outer manifest hashes all bundled files; the same partial-install guard and
remaining blockers apply.

The single shared preflight is byte-identical to both original preflights. It
requires a dedicated connected user, unchanged `CURRENT_SCHEMA`, no existing
`USER_OBJECTS`, `AL32UTF8` and `AL16UTF16`. Both independent package installers
and their guards remain unchanged. Chaining them is not a combined installation:
the second empty-user check would reject after the first package creates objects.

| Order | Exact phases | Reason |
| --- | --- | --- |
| 1 | `00-preflight.sql` | One empty-user and character-set check before any DDL |
| 2 | `external-schema/20-tables.sql`, `30-indexes.sql`, `35-constraints.sql`, `90-verify.sql` | Preserve the external package order and its exact ten-table, no-business-row verification before core objects exist |
| 3 | `core-schema/10-types.sql`, `20-tables.sql`, `30-indexes.sql`, `35-constraints.sql`, `60-sequences.sql`, `40-routines.sql`, `50-comments.sql` | Preserve the core package order, including sequences before dependent routines |
| 4 | `core-schema/70-bootstrap.sql`, `80-bootstrap-verify.sql`, `90-verify.sql` | Seed and verify the finite technical configuration before administrator initialization or application startup, then run the original core structural verifier |
| 5 | `99-verify.sql` | Check the exact 749-table and logical CREATE-object union, named indexes/constraints, valid objects/indexes and no database links |

The external package's original verifier requires exactly ten tables and no
other logical object types at its phase. Running it after core would fail; its
position is part of this plan's contract. The final verifier checks the combined
set by identity as well as count, so replacing an expected table or routine with
another object of the same type/count fails. Named PK/UQ constraints can create
implicit indexes; these names join the explicit index registry when checking
cross-package collisions. Constraint and index namespaces remain separate, so a
constraint and its own backing index can legitimately share a name.

Oracle assigns names to unnamed core PK/UQ constraints, their backing indexes
and LOB physical objects. Those physical names are excluded from exact name-set
matching; the original core verifier checks their logical column and PK/UQ
relations. All indexes must still be valid. The current selected union has 1,151
explicit CREATE objects, 545 named indexes including implicit named backing
indexes, and 185 named constraints. It does not declare whole external domains
complete or establish real HTTP/business acceptance.

Diagnostic execution of these exact phases is limited to a reviewed disposable
user inside the existing isolated Oracle. Preserve the normal guard and retain
the validated plan/hash receipt and SQLPlus logs. No general guard-bypass mode is
provided. Oracle DDL commits implicitly; a later failure does not roll back prior
DDL. An interrupted or repeated run must be investigated and its disposable user
explicitly cleaned up. It is not an upgrade or data-preserving recovery path.
Business writes, administrator initialization, runtime startup and HTTP/audit/
cleanup acceptance are separate steps; verify the bootstrap before those steps.

See the [core scope](core-schema/README.md), [external scope](external-schema/README.md)
and [clean-device contract](../../docs/deployment/clean-device-contract.md) for
remaining exclusions and deployment prerequisites. Complete restoration remains
blocked after a successful diagnostic phase replay.
