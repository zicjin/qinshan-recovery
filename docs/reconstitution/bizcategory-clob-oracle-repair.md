# Business category permission-name CLOB aggregation repair

The source checkpoint is standalone `02ffaa0922ea09d9dbabbcf4d9f926aa5e6d23cd`.
The original `EmcBizCategoryMapper.selectByCondition` is shared by the existing
business category queries/raw/searchable entry points. Root's real HTTP
inspection found ORA-22848. Read-only Oracle datatype inspection confirmed
`T_EMC_ORG_PERMISSION.ORGNAME` is CLOB, while TARGETID and PERMISSIONTYPEID are
VARCHAR2; `T_CORE_ORG.ORGNAME` is VARCHAR2. The original scalar subquery applies
`LISTAGG(DISTINCT A.ORGNAME, ',') WITHIN GROUP (ORDER BY A.ORGNAME)` to that CLOB,
which Oracle rejects before the endpoints can return their ordinary result.

Only that original Mapper query changes. Permission rows retain both original predicates,
`A.TARGETID = C.ID` and `A.PERMISSIONTYPEID = 'T_EMC_BIZ_CATEGORY'`.
A derived table inside the original correlated scalar query aggregates them
into an XML document. Keeping the correlated equality matters: category ID is
NUMBER while permission TARGETID is VARCHAR2, so `101` and `0101` must still
belong to the same category without duplicating category rows. The derived
table is necessary: nesting the aggregate directly inside XMLQUERY is rejected
by Oracle's grouping rules.
The outer projection applies distinct-values, ordering and comma joining to
complete XML text nodes, then XMLCAST returns CLOB. There is no scalar cast,
substring extraction, maximum-name length, duplicate dropping beyond the
original exact distinct-name behavior, permission widening or new route/DAO.
XML construction escapes markup characters, and XQuery text extraction returns
the original characters rather than their serialized entities.

The scalar query preserves categories without a matching permission.
XMLEXISTS checks whether the XML contains a name text node before joining it;
otherwise the CASE returns SQL NULL, rather than a non-null empty CLOB.
All-null names likewise return NULL. The scalar SELECT has the Oracle
`NO_XML_QUERY_REWRITE` hint: real Oracle execution without the hint raised
ORA-01706 for long names because XML rewrite materialized a scalar. An explicit
XQuery text constructor did not avoid that failure. The inner hint preserves
LOB evaluation and passes the long-value check; no outer-query hint is needed. Original category columns, three organization-name aliases, global
filter fragments, selectable-user permission, organization isolation, original
ordering and the statement's count/pagination entry points remain intact.
`selectDetail` is unchanged. This is a source-backed original-query repair;
no process, application, compatibility service or external runtime is added.

## Collation boundary

Root verified local Oracle `NLS_SORT=BINARY` and `NLS_COMP=BINARY`. XQuery uses
its Unicode codepoint collation. The Oracle fixture compares short names with
the same session's valid VARCHAR2 LISTAGG baseline, including accented and CJK
text; the original CLOB LISTAGG has no successful result to compare. Arbitrary
linguistic NLS collations are not verified by this repair. No implicit CLOB to
VARCHAR2 NLSSORT conversion is introduced, since that would truncate long
names. Root's local read-only inspection found no XML-invalid control names;
XML-invalid input is not assigned a silent escaping/truncation policy here.

## Real Oracle regression

The agreed seam is the actual MyBatis Mapper statement. The test loads the
tracked complete original Oracle global mapper and business-category mapper,
produces BoundSql for real parameter maps, and uses actual SqlHelper total SQL
and OracleDialect pagination. Its generated Oracle CTE shadows the tables with
SELECT-only fixtures. It does not connect to a database or perform business or
fixture writes. SQLPlus exits on SQL failure; each result assertion divides by
zero when its independent expected value disagrees. H2 is not used.

A tracer fixture generated before implementation reproduced ORA-22848 in the
original LISTAGG on root's authorized Oracle session. Root retains the local
diagnostic log `build/bizcategory-oracle-red.log`; it is not canonical evidence.
The repaired fixture contains ten scenarios:

- sorted distinct names, duplicate and null names, XML special characters,
  permission-type exclusion and the original three organization aliases;
- two 10,001-character names identical through character 10,000, preserving both
  complete tails and a duplicate-free 20,003-character comma-delimited result;
- all-null, absent and only-unrelated permission rows yielding null names;
- actual framework count, scalar filter/no-match and custom-order page offset;
- original selectable user scope/count, unassigned user and sysAdmin behavior;
- original root-organization isolation; a permission TARGETID `0101` matches
  numeric category ID `101` without returning a duplicate category.

Run generation using explicit read-only source-only classes and public jars:

```bash
python3 scripts/tests/test-bizcategory-clob-oracle.py \
  --jdk /path/to/jdk1.8.0_391 \
  --public-deps /path/to/reconstituted-public-deps \
  --framework-classes /path/to/owned-core-common/target/classes \
  --emit-oracle-fixture /tmp/bizcategory-clob.sql
```

Execute the generated file in an authorized Oracle session with the correct
PDB already selected. It sets DEFINE OFF and SQLBLANKLINES ON and prints the
session NLS parameters. Generation alone is not a database test pass.

All temporary Java compiler outputs are isolated; existing targets are only
read prerequisites. Root owns the consolidated source-only build/package,
app-only refresh and real HTTP verification. No live database connection,
remote operation, shared build, strict promotion or binding/closure rewrite
is performed by this isolated implementation. Supplier differential and strict
API closure remain separate gates.

Root executed the final generated fixture on the existing Oracle. All ten
scenarios passed, including the complete 20,003-character result and the leading
zero TARGETID coercion; the diagnostic log is
`build/bizcategory-oracle-green-final.log`. This is real Oracle Mapper-query
regression evidence, not a real HTTP or strict pass. The focused fixture
generator compiles and emits all ten scenarios. Existing
execution-plan (3), contract-skeleton (10), Spring contract runner (18), fast
differential runner (31) and owned-runtime contract (11) tests pass. Python
syntax compilation and `git diff --check` pass. The route-ledger test has a
hard-coded parent archive fixture path and fails in this isolated worktree;
setting SUNWAY_FEATURE_ROOT does not change that path. Root owns its final
merged-checkout ledger/package gates. No test failure is counted as a pass.
