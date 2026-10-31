# Owned equipment read service: bounded evidence

Ticket 10 remains open. The bounded read service is now connected through the
unchanged recovered equipment resource, service and DAO contracts in a
separate owned-equipment runtime module. Browser acceptance and the remaining
equipment workflow graph are still open.

`EquipmentQueryService` uses only a principal issued by `JdbcPersonnelIdentity`.
Every list/detail request rechecks active account, supported login configuration
and a current role grant to management menu `101501` or read-only query menu
`20251000001`. Both menu IDs come from the existing `QueryAccessFilter`; the
service exposes reads only. This does not implement the filter's page/write
route distinctions.

Category grants come from `T_EMC_ORG_PERMISSION` and concrete current-user
organization grants. The UI's `orgid_seq` selection can further narrow a list;
it cannot replace the category restriction. Detail currently also requires
category scope. The historical detail Mapper lacks that predicate, so this is
a conservative boundary pending authorized/restricted role comparison.

The list retains `E.*` and joined organization/user/category names. Default
order is ID descending. Explicit order uses the archived global Mapper's
`NULLS LAST`. Simple string, numeric/date comparisons and grouped advanced
filters use bound values and an allowlist of columns. The archived global
Mapper explicitly defines `NEQ` as numeric equality. Unsupported operator,
column and Mapper parameters throw instead of being silently ignored.

Unresolved behavior must not be reported as equivalent:

- The historical equipment Mapper joins alias `O` using `E.USEDDEPOSTID = C.ID`
  in both list and detail SQL, while this service uses `O.ID`. The synthetic
  regression now gives the department and post different IDs and verifies that
  the owned service returns the post's own name (`Fixture Post C`) with no row
  multiplication. This proves the owned seam's selected behavior only; it does
  not establish historical runtime equivalence, so the source discrepancy stays
  open for a later authorized clone comparison.
- Original list scope is conditional on `target_type=equipList`; this bounded
  service always requires it for list/detail access. Other target modes are
  unsupported.
- Method/maintenance assignment and overdue filters, remaining operators,
  wildcard escape behavior and remaining columns are not complete.
- The recovered list script includes both `target_type` and `orgid_seq`;
  current actual-resource HTTP regression verifies their combined narrowing
  behavior and the original lower-camel DTO plus `ext$` joined-label shape.
- `emc-equipt-detail.html` is absent from the recovered template directory.
  The list contains an inline detail form, but standalone detail-page behavior
  is unverified; no replacement template has been invented.
- Purchase, maintenance, calibration, stock, imports/exports and writes remain
  unverified.

## Regression

`python3 scripts/tests/test-equipment-runtime-oracle.py` creates a new
synthetic schema in a digest-pinned Oracle container on an internal
single-endpoint network, without published ports or host binds. Its 61
observations exercise the actual recovered `GikamWpjcApplication` and
`EmcEquiptResourceImpl` over loopback with RSA login: lower-camel DTO and
`ext$` projections, list/detail scope, direct/nested/query transport,
filter/no-match/page behavior, anonymous and low-privilege denial, management
versus read-only menu distinction, current menu/category grant revocation,
unsupported auxiliary flows, and malformed/repeated/duplicate transport.
The generated JSON records source and artifact hashes, role/data fixture
through the test-source hash, internal-network isolation and cleanup.

`bash scripts/tests/test-owned-equipment-runtime.sh` rebuilds the owned
framework and equipment module, then the runtime Oracle script runs the
authenticated original resource. `OwnedReadWebConfiguration` is registered
once in the framework auto-configuration; equipment reuses
`OwnedReadRequest.parse` and does not install a duplicate resolver.

On 2026-10-21 the runtime fixture also used a distinct post ID (`post-c`).
The actual historical resource returned `Fixture Post C` in both list `ext$`
and detail projections across 61 checks, with `source_dirty=false`, no Oracle
errors, and isolated-container cleanup. This confirms the selected owned join
through the HTTP DTO path; it still does not resolve the archived mapper's
different join predicate against an authorized historical runtime.

The packaged browser run at the same source boundary passed 55 checks across
equipment and consumables. It covered list/filter/no-match/detail/pagination,
stock and stock-log reads, low-privilege page/API denial, logout races, and
credential-free audit output. The report records `source_dirty=false`, a
single internal Oracle endpoint, closed browser session, and removed container
and network.

The test is still an HTTP/runtime acceptance slice rather than browser
acceptance. The detail scope deliberately retains the category and
organization predicate because the recovered detail mapper's join predicate
is unresolved; no standalone detail template is invented.
