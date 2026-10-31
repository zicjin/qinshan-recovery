# Owned personnel query service

Date: 2026-10-20 (Asia/Singapore)

`com.sunwayworld.owned.internal.data.PersonnelQueryService` now provides the
owned read seam used by the personnel vertical slice. It accepts a parsed
`QueryRequest` and a database-issued `JdbcPersonnelIdentity.Principal`; it
does not accept caller supplied SQL, organization scope, or login identity.
Every list and detail request revalidates the current account/menu grant and
uses concrete `T_CORE_USER_PERMISSION` organization grants in the SQL scope.

The disposable Oracle run is recorded in
[`.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-query-oracle.json`](.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-query-oracle.json). The
post-identity merge rerun is recorded in
[`evidence/personnel-query-oracle-20261020-postmerge.json`](personnel-query-oracle-20261020-postmerge.json).
The current service passes 15 checks covering first-page ordering, page two,
case-insensitive name filtering, no-match results, in-scope and out-of-scope
details, concrete organization grants, joined department fields, extension
fields, department and organization filters, an advanced OR group, and
pagination-column hiding. The query returns the recovered broad employee
projection instead of the earlier narrow fixture projection. Each run uses a
digest-pinned Linux amd64 Oracle image on an internal Docker network with no
published ports or bind mounts, and was removed with its network after the
run. No production endpoint or data was accessed.

This evidence covers the owned Oracle read seam only. It does not establish
HTTP login/session behavior, browser acceptance, writes, attachments, or the
full personnel resource graph. The original supplier mapper's broader
`T_EMC_EMP` projection remains a reference boundary for later field coverage.
