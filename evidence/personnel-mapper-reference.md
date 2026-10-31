# Personnel Oracle mapper reference observation

Run from `lims-recovery/`:

```bash
JAVA_HOME=/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391 \
  bash scripts/tests/test-personnel-mapper-reference.sh
```

The test loads the supplier `GlobalMapper` XML read-only and the unchanged
`EmcEmpMapper` Oracle XML, emits MyBatis `BoundSql`, then executes those exact
SQL statements against a disposable Oracle Free container. The container uses
an internal network with one endpoint, no ports and no bind mounts. The source
XML hashes and run result are retained in
`.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-mapper-reference.json`. Fresh command output is written to
`build/.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-mapper-reference.json`; an explicit `EVIDENCE_OUT` can archive a
verified run. The archived run binds clean integration source `1f81a86`.

Observed synthetic rows:

- unfiltered list: 2 rows;
- `org_filter` for `u-a`: 1 row (`org-a`);
- unmatched department filter: 0 rows;
- standard owned `FilterParamPattern("EQ", "NAME", "Alice")`: 1 row;
- advanced `EQ(NAME, Bob)`: 1 row;
- explicit `NAME DESC` order: first row `Bob`;
- case-insensitive multi-property `NAME/ID` filter for `ali`: 1 row;
- detail `e-1`: `Alice`;
- detail `e-2` while the same `org_filter` context is present: `Bob`.

The last result is a concrete boundary: the current `selectDetail` mapper uses
`WHERE A.ID = #{id}` and does not apply the list organization filter. This is
reference behavior, not an authorization acceptance or a recommendation to
broaden data access. Login, menu permissions, page rendering and service-level
authorization remain unverified.

This evidence does not claim that the owned runtime can yet execute the
personnel route. The next closed implementation group is the shared mapper
parameter/page contract needed to connect the existing service to the owned
framework without introducing a competing route.
