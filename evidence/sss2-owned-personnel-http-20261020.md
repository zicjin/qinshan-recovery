# sss2 owned personnel page HTTP smoke

Date: 2026-10-20 (Asia/Singapore)

The page-only owned artifact was built from the project-owned framework, pinned
public dependencies, the recovered `GikamWpjcApplication`, the personnel page
controller, templates, and scripts. The local transfer bundle SHA-256 was
`831efebf421858735b424f3bd99e07b7f90ba492f724ba7e1927d4e750440e13`.

The bundle was loaded as image `sha256:6d5b5c18b83a8482368a0feea4b543d82ad8a55fb0dfb72400cc54f5b86e281f`
and run temporarily as container
`130400fcd89cb82a49c9c8ce5278e785d8a20c59d354dead34d32875042756db` at
`10.87.0.5` on the existing `internal=true` network
`lims-replay-demo-s1-net`.

Read-only requests from the `sss2` host returned HTTP 200 for all five routes:

| Route suffix | Response bytes |
| --- | ---: |
| `edit-list` | 261 |
| `detail` | 256 |
| `choose-list` | 284 |
| `report-users/choose-list` | 297 |
| `sample/choose-list` | 297 |

The original `emc-emp-edit-list.js` was served successfully (SHA-256
`04545cd9c10ca2f3dbfcefe08e7f37a64505ced032e31f9176196198d0afc076`). The
temporary container had no mounts, no published host ports, and ran as UID
`65534:65534`. It was removed together with its image and staging files after
the check. The existing supplier-backed application, Oracle, and Redis
containers remained running and unchanged.

This proves owned entrypoint, controller, template, and static-resource
behavior inside the `sss2` isolated network. It does not prove login,
authorization, request-wrapper parsing, resource/service/DAO behavior, or
Oracle/Redis data queries; no production data was imported or modified.
