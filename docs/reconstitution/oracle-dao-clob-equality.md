# Shared Oracle DAO CLOB read equality

Source checkpoint: `02ffaa09`. A read-only diagnostic of the original project
change query reported ORA-22848 at `DaoMapper.select`. Root verified that
`T_EMC_PROJECT_CHANGE.PROJNAME` is CLOB, `PROJNO` is VARCHAR2 and `ID` is NUMBER.
The original service first searches project number, then project name. Its
shared DAO XML previously emitted `"PROJNAME" = ?` even when existing metadata
identified the column as CLOB. No route-specific service fallback is added.

Both Oracle DAO resource files now select `DBMS_LOB.COMPARE(column, bound CLOB)
= 0` for read-side equality when `columnContext.clob` is true. The bound value
uses `jdbcType=CLOB`; no scalar conversion, substring, text cap or SQL literal
interpolation is introduced. The preceding null/empty branch remains `IS NULL`.
Ordinary scalar equality, active-column handling, original AND/OR grouping,
projection, sorting, first-row and count structure remain intact. This covers
`select`, `selectUnion`, `selectFirst`, `batchSelect`, `count`, `batchCount` and
the six equivalent MapDao statements. Writes, ID queries, membership (`IN`)
queries and other dialects retain their original statements.

The existing `DatabaseManager` already derives CLOB flags from JDBC metadata
and transfers them to entity contexts. No Java metadata implementation changes
are needed. Resource provenance retains original source JAR hashes/byte counts;
only the two affected destination hashes and transformation descriptions change.
No Java source hash or source-tree digest changed. The root integration build
must regenerate output JAR hashes and compile reports after merging; this
isolated worktree does not claim a new distribution build.

## Reproducible regression

Use explicit local paths for JDK 8, public dependencies, previously compiled
source-only common classes and H2 2.2.224:

```bash
python3 scripts/tests/test-oracle-dao-clob.py \
  --jdk /path/to/jdk1.8.0_391 \
  --public-deps /path/to/reconstituted-public-deps \
  --framework-classes /path/to/owned-core-common/target/classes \
  --h2 /path/to/h2-2.2.224.jar \
  --emit-oracle-sql /tmp/oracle-dao-clob-assertions.sql
```

The Java fixture compiles into an automatically removed temporary directory,
loads the tracked XML with MyBatis, invokes the existing Mapper interfaces and
executes the SQL through JDBC. Contexts are created through framework factories
using actual JDBC fixture metadata and Oracle dialect type rules. H2 calls its
CLOB type `CHARACTER LARGE OBJECT`; the fixture normalizes JDBC `Types.CLOB` to
Oracle's CLOB name while retaining other actual type names. H2's adapter reads
both CLOB streams completely. Captured BoundSql must use DBMS_LOB.COMPARE and a
CLOB parameter mapping because H2 alone silently accepts Oracle-invalid CLOB
`=`. The first regression failed on exactly that SQL before the resource repair.

There are 62 assertions over 28 actual Mapper queries, including 24 CLOB
queries. Cases cover >10,000-character values differing only at the tail,
exact quoted/multibyte input, nonmatching input, null/empty, scalar-only input,
AND/OR, multiple batch branches, count, first after descending order, restricted
projection and full returned LOB values. The local fixture is a semantic check;
it does not claim Oracle syntax or HTTP acceptance.

The optional exporter renders those captured SQL statements over SELECT-only
Oracle CTE fixtures. Each query automatically compares returned IDs/count to
its expected value and raises division by zero on a mismatch. SQL*Plus error
exit directives, SQLBLANKLINES, DEFINE OFF and bounded line lengths prevent
silent continuation or oversized SQL*Plus input. It does not create tables or
write business data. Run it separately against the authorized long-lived
Oracle, using an existing credential channel; do not save credentials here.

No application, Oracle, Redis or supplier process is started or restarted by
the regression. No strict evidence is promoted and no API route is closed by
this source repair. Root owns app-only refresh, actual HTTP differential,
source binding freeze and later strict replay.

## Verification at this repair

- Local Mapper/JDBC fixture: PASS, 62 assertions, 28 queries, 24 LOB queries.
- Root executed the emitted 28 autoasserting SELECT-only CTEs against the real
  Oracle: PASS, exit 0, no ORA/SP2 errors; integration-local log is
  `build/oracle-dao-clob-assertions.log`. This is diagnostic SQL verification,
  separate from HTTP and strict closure.
- Framework source/resource verifier, execution plan (3 tests), contract
  skeleton (10), Spring API runner (18), fast differential runner (31), owned
  runtime contract (11), Python syntax and `git diff --check`: PASS.
- Route ledger test could not resolve its historical archive input from the
  isolated worktree, including with the feature-root environment override;
  root should run that gate in the integration checkout with its archive.
- Distribution/package and compiled verifier are intentionally assigned to the
  root's single integrated build; they were not run against shared targets.
