# Mobile sample/container label Oracle repair, 2026-11-07

The local real HTTP screening reported ORA-00923 for
`PfSampleMapper.selectMobileScanner` and ORA-00979 for
`selectDetailLabel`. Inspection found the same nongrouped projection in
`selectLabelDetail`. These three statements are repaired together in
`project/src/main/java/com/sunwayworld/emc/module/bp/sample/sample/mapper/conf/pf-sample-oracle-mapper.xml`.

The scanner had an unquoted `NUMBER` alias, `U.* GROUP BY NUMBER`, then
`S.* GROUP BY S.ID`. Those expressions cannot identify a deterministic row
or valid Oracle aggregate. The original SQL Server counterpart confirms the
intended two levels (container-label deduplication, then sample totals), although
its scanner statement shares the grouping defect. The repaired inner query groups
by the existing container label expression and sample/project projection, and
reduces the string scanned flag to a numeric 0/1. The outer query counts physical
labels and sums scanned labels per sample, preserving `TOTAL`, `SUMNUMBER`, `NUM`
and sample/project fields. Duplicate analysis tasks no longer inflate counts.
The removed `NUMBER` and raw `SCANNED` columns were arbitrary per-label values in
a per-sample result and are not used by the original service.

`PfMobileProjectServiceImpl.updateSannerByLabelId` updates every order task for
one order/container category/group to scanned='1', supporting a single flag per
physical label. `MAX(CASE SCANNED WHEN '1' THEN 1 ELSE 0 END)` counts a label once
if any task already records that scan; null and other flags count zero. The
original preLogged/done/noncancelled and nonnull-container predicates remain.

The two label queries retain their original projections, `LISTAGG(DISTINCT ...)`,
ID/label parameters and filters. They now group every nonaggregate projection by
its actual expression instead of grouping only the `LABELID` alias. Detail status
continues to distinguish 已扫描 and 未扫描, as in the original SQL Server query.
No sample acceptance, permissions, business write or routing code changes.

Provenance:

- Approved stock run JAR SHA256:
  `4a720797810fd2f8cca6e22604657028cb87830ca0e24bb55a5f42f040f6ee9f`.
- Stock `BOOT-INF/classes/com/sunwayworld/emc/module/bp/sample/sample/mapper/conf/pf-sample-oracle-mapper.xml`
  SHA256: `af614d5ffca2659ae94cfae9d37507813ce4419b584cd3f45fb72f9c6134e8c0`.
- Tracked pre-repair input at standalone `2bbe5206` SHA256:
  `055278f31a0ec2f05bdd431a845e6e73119ee7bbc10c73de73340f9637ef014d`.
- Repaired source SHA256:
  `e5d7534673da39fd3c0d58c3a100cd80676cf10b7bd0b101928963867f63f11c`.
- This is an owned SQL correction to original application source, not byte-identical
  stock resource recovery or API equivalence evidence.

Regression uses the actual three XML select elements, MyBatis 3.5.6 BoundSql and
JDBC execution on public H2 2.2.224 in Oracle mode. In-memory fixtures cover duplicate
tasks, two labels on one sample, scanned/unscanned/null flags, null container group,
project/status/process exclusions, missing sample/label, distinct test names,
projection fields and parameter binding. H2 permits some alias/functional-dependency
grouping that Oracle rejects, so the test additionally checks every actual BoundSql
nonaggregate label projection against its grouping expression. This is a focused
Oracle constraint check, not a general SQL parser or substitute for Oracle execution.

Each case was observed red before its fix: scanner rejected string SUM; detail and
label rejected nongrouped A.ID under the Oracle projection check. All three pass
after repair. No application build, runtime refresh or application DB operation was
performed here. Root retains deployed real Oracle/HTTP verification responsibility.

Run with read-only public prerequisites (paths are caller-selected):

```bash
python3 scripts/tests/test-mobile-label-oracle.py \
  --jdk /path/to/jdk1.8.0_391 \
  --mybatis /path/to/mybatis-3.5.6.jar \
  --h2 /path/to/h2-2.2.224.jar
```

H2 public Maven artifact:
`https://repo.maven.apache.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar`,
SHA256 `b9d8f19358ada82a4f6eb5b174c6cfe320a375b5a9cb5a4fe456d623e6e55497`.
It is a test prerequisite, not a runtime dependency or distributed binary.
