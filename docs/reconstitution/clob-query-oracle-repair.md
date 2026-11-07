# CRM and patrol complete CLOB query repair, 2026-11-07

This repair starts at source checkpoint `b54b4bc3`. Root's real HTTP diagnostic
reported ORA-00932 from `CrmOpportunityOrdtaskMapper.selectByCondition` and
ORA-22848 from `EmcPatrolMapper.selectByCondition`. Root verified the actual
Oracle schema with read-only datatype inspection: CRM `SCHEMENAME` is VARCHAR2,
`TESTNO` and `TESTMETHODS` are CLOB, and `TESTCODE` is VARCHAR2; patrol
`PATROLUSERID` and `PATROLUSERNAME` are CLOB, `PATROLDATE` is VARCHAR2 and
`PATROLTIME` is DATE. No live database operation was performed in this worktree.

CRM's original CASE returned CLOB `TESTNO` for the default scheme and VARCHAR2
`SCHEMENAME` otherwise. `TO_CLOB(T.SCHEMENAME)` makes both result branches CLOB.
The scalar default comparison is preserved, together with all original query
filters, sorting and result aliases. The full TESTNO/TESTMETHODS projections are
preserved; no LOB truncation, substring extraction or scalar length cap is used.

The original patrol service adds a current-month filter and a login-user EQ
filter, then calls `GenericService.selectPaginationByFilter`. The generic EQ
fragment compares PATROLUSERID with `=`, which Oracle forbids for a CLOB. The
service now supplies a `patrolUserIdExact` special filter from the same login
context. The original Oracle select adds full
`DBMS_LOB.COMPARE(T.PATROLUSERID, TO_CLOB(#{patrolUserIdExact})) = 0`.
The SQL Server select handles the same key with an unbounded
`CONVERT(NVARCHAR(MAX), T.PATROLUSERID)` equality, retaining that dialect's
identity restriction when the shared Java service uses the special filter.
No new route, service facade, mapper statement or runtime was introduced.

The special key is present even when login ID is null. Both mappers explicitly
return no rows for a present null/empty identity, while ordinary queries without
that key keep their original scope. `GenericService.selectPaginationByFilter`
extracts request filters first and then calls `MapperParameter.setFilter`;
its `putAll` applies the server special value last. Request input therefore
cannot replace that identity. Data and total queries use the same original
mapped statement; framework count generation wraps that SQL and retains the
identity predicate. Existing organization, permission, additional scalar filter,
sort and pagination paths are preserved.

The real BoundSql regression also found an existing month-filter defect:
`MatchPattern.SC` already adds LIKE wildcards, and `OracleDialect` escapes
caller-provided percent characters. Passing `yyyy-MM%` searched for a literal
percent and excluded real `yyyy-MM-dd` values. Only the three original month
selectors now pass `yyyy-MM`; their original SC pattern and identity/handle-user
filters remain. The handle-user filter is otherwise unchanged.

## Verification

`scripts/tests/test-clob-query-oracle.py` compiles the actual changed patrol
service into a temporary output directory and loads the tracked complete
MyBatis mapper XML, including the original global fragment. H2 Oracle-mode
fixtures declare the real CLOB column types. Small public-function adapters
provide TO_CLOB and complete character-stream comparison for H2 only; they
are test code and do not claim Oracle execution. An explicit actual-BoundSql
constraint rejects Oracle's mixed CLOB/VARCHAR2 CASE and requires the scoped
full LOB comparison. Both regressions were observed red before their repair.

Coverage includes 10,000-character Unicode CRM text, default/named/null CASE
results, original result aliases, order-ID filter and actual framework count;
patrol complete long IDs differing only beyond 10,000 characters, null/empty
identity, unmatched and injection-shaped IDs, nullable row identity, unchanged
month query scope, original organization restriction, additional scalar filter,
sorting, actual OracleDialect page offset and SqlHelper count generation.
The original service/GenericService/MapperParameter path is exercised with a
caller-supplied identity and null server identity to prove fail-closed overwrite.
SQL Server actual BoundSql checks its full equality and null/empty protection.
The three month service methods retain their original companion filters.

Run with explicit existing source-only compiled classes and public jars:

```bash
python3 scripts/tests/test-clob-query-oracle.py \
  --jdk /path/to/jdk1.8.0_391 \
  --public-deps /path/to/public-dependency-directory \
  --framework-classes /path/to/owned-core-common/target/classes \
  --project-classes /path/to/project/target/classes \
  --h2 /path/to/h2-2.2.224.jar
```

JDK compilation output is isolated under a temporary directory. Existing class
outputs and public jars are read-only prerequisites; no shared target is built.
H2 is a public test prerequisite, not a distribution dependency. This result is
source/query regression evidence only. Root owns consolidated source-only build,
local 18087 app-only refresh and real Oracle HTTP verification. Supplier 18081
was reported unavailable by root; differential remains blocked until it is
available. No strict pass, closure, source binding or canonical evidence is
created or promoted here. API-ALIGNMENT-GATE remains open.

The focused CRM/patrol test passes for both cases. Additional existing narrow
tests passed: execution plan (3), contract skeleton (10), Spring contract runner
(18), fast differential runner (31) and owned runtime contract (11).
`git diff --check` and Python syntax compilation also pass. The route-ledger
script cannot locate its parent archive fixture in this isolated worktree;
package verification was attempted once and stopped at its evidence-lifecycle
precondition. Neither failure is counted as validation success. Root owns the
merged checkout's final ledger/package gates; no repeated package test or
shared-target build was performed here.
