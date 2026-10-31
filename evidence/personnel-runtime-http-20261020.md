# Personnel runtime HTTP read slice

Date: 2026-10-20 (Asia/Singapore)

The committed personnel runtime is a bounded adapter around the unchanged
recovered `EmcEmpResource`, `EmcEmpService`, `EmcEmpDao`, DTOs and page
controller. `OwnedPersonnelReadService` supplies only list/raw and detail;
auxiliary personnel flows throw an explicit unsupported exception. The adapter
uses the same `JdbcPersonnelIdentity` instance as the identity provider,
refreshes the current session and permission before each read, and binds the
current organization grant into SQL. The original `GikamWpjcApplication` is
compiled from recovered source; no competing personnel controller is added.

`OwnedReadWebConfiguration` is a single framework MVC registration for the
wrapper argument resolver and JSON converter. `OwnedReadRequest.parse` accepts
the observed nested `p` envelope and direct `f`/`n`/`s` query envelope,
captures single-valued query parameters, and rejects mixed, duplicate,
malformed, or unknown transport input. `RestJsonWrapperBean(String)` retains
the observed legacy `f` parameter meaning; raw JSON uses its four-argument
constructor. The generic resource maps the observed delete/insert/instant/save
paths; the query guard denies unsupported writes before the resource.

The clean committed-source report is
[personnel-runtime-oracle-20261020.json](personnel-runtime-oracle-20261020.json).
It records 62 packaged original-application HTTP checks and 15 Oracle service
checks, `source_dirty=false`, the recovered project commit and 23 original
source/resource hashes, 306 owned source hashes, both owned JAR hashes and all
public dependency hashes. The Oracle container used one internal network
endpoint with no host ports or bind mounts; container, anonymous volumes and
network cleanup all passed. The report contains no credentials.

This slice does not claim browser/page acceptance, full personnel writes,
photo/file locators, certificate/training behavior, or completion of the
personnel ticket. Those remain open for the frontend and later vertical slices.

Integration regression on commit `33786394d5563d2d08175c9316f6a850fd030289` passed the same 62 HTTP and 15 Oracle checks with `source_dirty=false`. See `personnel-runtime-integration-rerun.json`; temporary container, volumes and internal network cleanup all passed.
