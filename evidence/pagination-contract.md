# Pagination contract observation

The public `Pagination` seam was observed from the embedded
`gikam-core-common-14.1.5.jar` with Java 8 reflection in an isolated
`URLClassLoader`; no application startup, native loading, or license input was
used. The supplier jar SHA-256 is recorded in
`evidence/framework-contract/jars.json`.

Observed plain Java behavior is differential-tested against the owned class by
`scripts/tests/test-pagination-contract.sh`: default, list, `(int,int,int,List)`
and `(Page,List)` constructors; null and mutable list/map seams; negative and
zero totals/page sizes/numbers; navigation calculations; and setter behavior.
The supplier's three-int constructor order is `(total, pageSize, pageNumber,
rows)`, and the implementation preserves direct list aliases. `setColumnTypeMap`
merges into the existing map; the other collection setters retain the supplied
reference. Null list constructors and null page-copy sources throw
`NullPointerException`.

The expanded test now compares 61 observations, including mutable default
collections, null copy rows, the five-argument total-map constructor and its
alias/null semantics. A page-copy total is the maximum of source total and new
row count. `Pagination` remains extensible as in the supplier declaration.

The Spring Data `Page` constructor is implemented and observed with actual
public Spring Data interfaces in separate loaders. Tests cover explicit page
size/number, content aliases, null source and narrowing a total above
`Integer.MAX_VALUE` to `int`. `PageRowBounds(Pageable)` and
`Pagination(PageRowBounds,List)` are implemented with pinned MyBatis. A separate
58-observation differential test covers offset/limit narrowing, page enablement,
array aliases and null defaults, `QTY_` total extraction, null totals,
total-map copying and null Pageable. It does not prove mapper interceptors or a
live database.

The owned loader contains only owned classes and hash-pinned public libraries.
The supplier reference is separately loaded and its framework hash is checked
against `framework-contract/jars.json` before execution. No supplier classes
are on the owned compile/runtime classpath. This test does not start LIMS,
connect a database, or prove page/HTTP compatibility.

The row-bounds suite also exercises `Pagination(PageRowBounds,List)` for totals
below row count, metadata/list aliases, option setters and null inputs. Its
supplier loader includes the hash-pinned Oracle JDBC driver because the
supplier conversion registry resolves Oracle DATE metadata even for numeric
offsets; the owned loader does not need that driver and no connection is made.
