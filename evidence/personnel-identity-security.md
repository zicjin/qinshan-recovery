# Owned personnel identity seam

The owned adapter is `com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity`. It authenticates an exact `T_CORE_USER.ID` with the evidenced MD5 encoding, produces an immutable database-backed principal, re-reads account and role/menu grants for personnel menu `100001`, and returns only concrete organization grants joined to `T_CORE_ORG`. `refreshPrincipal` re-reads the current organization snapshot.

The owned HTTP/session seam is `OwnedIdentityHttpController` and
`OwnedIdentityHttpBoundary`. The observed routes are retained as
`POST /core/module/sys/login` (form fields `id`, `password`, `orgId`, `roleId`,
`loginType`; `SECURITY-RSA-KEY` and optional `sunway.tenant` headers) and
`POST /core/module/sys/logout`. Login stores an opaque `OwnedPrincipal` in the
servlet session, rotates the session id, and wipes the request credential;
protected calls refresh the provider identity before checking
`OwnedIdentityProvider.PERSONNEL_READ` and loading the concrete organization
scope. `AuditEventSink` receives login, session, authorization, scope, and
logout outcomes. `JdbcPersonnelIdentityProvider` is the adapter that preserves
the issued JDBC principal without exposing it to session callers.

The adapter fails closed when `T_CORE_LOGIN_CONFIG` is missing or selects organization, role, tenant, captcha, AD, QR, authentication-option, or non-`system` authentication modes. It also rejects active password policy rows that require failed-attempt lockout, expiry, or mandatory expiry handling. The HTTP boundary rejects non-empty organization, role, tenant, and non-`system` login selectors rather than silently ignoring them. The owned `OwnedRsaLoginCredentialDecoder` implements the credential seam and the observed `/open/security/public-key` route; it accepts only the runtime's public key and never falls back to plaintext. Real page acceptance remains open.

Verification is recorded in [personnel-identity-oracle-20261020.json](personnel-identity-oracle-20261020.json): a fresh digest-pinned Oracle container on an internal network, no ports or bind mounts, 63 assertions including the controller/session bridge and audit sink, plus loopback embedded-Tomcat and real Spring Boot checks. Container, network, and temporary session cleanup are enforced. This remains a synthetic identity and HTTP/session check; it does not establish trustee/SSO/captcha behavior, full browser protocol equivalence, real page acceptance, or production access.

The follow-up starts the actual Spring Boot application through the packaged
`META-INF/spring.factories` configuration. It verifies that the scanned
public-key, login, and logout routes are registered with a Boot-created Oracle
`DataSource`, that an existing session id rotates on login, and that login and
logout events reach a private, size-limited rotating audit file. Missing or
unsupported encoder and audit settings reject startup. Servlet startup without
a `DataSource` stays active and omits identity routes. The component-scan
regression first returned HTTP 404 for the login route; replacing the draft
with the ordered auto-configuration makes the same real Boot test pass. No
fixture defines a production route.

The runner hashes the bridge, provider, configuration, resources, credential
transport, audit, and Boot/Oracle test sources. The archived report identifies
the exact clean source commit and built JAR hash. The final run tested
`5994dba7a58fdcb71cac6379e0d1cb0a24b7065c`, including integration tip
`4b8c6ef3a8c090afb6f85596d392b9c82e516bce`, with `source_dirty: false`,
63 Oracle identity checks and 183 real Boot checks. The 216-class declared
contract build and supplier-runtime rejection scan, the 22-check public HTTP
boundary contract, and unrelated `/owned-runtime/health` HTTP bootstrap also
pass. The personnel template script could not run in this worktree because
the nested `project` repository is not initialized; it is not claimed as new
page evidence.

For a servlet runtime with a `DataSource`, explicitly set
`sunway.user.password.encoder.type=MD5`. This property is evidenced in the
earlier isolated [page acceptance](t06-page-acceptance-20261012.md); missing
values and other algorithms are rejected by this bounded implementation.
Configure `owned.identity.audit.type=file` and `owned.identity.audit.path` or
provide an application `AuditEventSink` bean. File auditing defaults to
10 MiB per file and five archives; override `owned.identity.audit.max-bytes`
and `owned.identity.audit.archive-count` for the deployment's retention policy.
Use a writable persistent path owned by one runtime. New POSIX files have mode
0600, each event is forced to disk, and write failures deny the operation.
The local retention bound deliberately discards the oldest archive; use an
application sink for centralized or longer retention. `InMemoryAuditEventSink`
is only an explicitly constructed fixture and is never the runtime default.

Query integration constructs `JdbcPersonnelIdentityProvider(identity)` and
`OwnedIdentityHttpBoundary(provider, auditSink)`. Protected endpoints use the
boundary's session/permission checks, then pass only
`provider.delegatePrincipal(boundary.currentPrincipal(session))` to the owned
personnel query service. This follow-up registers identity routes and their
real RSA transport; personnel/equipment page handlers, list/detail query
wiring, browser flow, and write auditing remain separately unverified.

## Current-user menu ID follow-up (2026-10-21)

The owned runtime now registers `GET /secure/core/module/sys/menus`. It reads
only menu IDs present in `T_CORE_MENU` and granted through the current user's
active role/menu permission rows; wildcard target `0` is honored and results
are ordered by menu ID. The session is refreshed before the query, so role
revocation, role suspension, account suspension, logout, and anonymous access
fail closed. The response is explicitly `Cache-Control: no-store` and does not
claim menu names, hierarchy, administration, or a complete permission tree.

The clean Oracle/Boot report is
[owned-menu-oracle-integration-20261020.json](owned-menu-oracle-integration-20261020.json):
63 JDBC checks and 281 real Boot checks, source commit `95dbf7d`,
`source_dirty=false`, internal single-endpoint network, no ports or bind
mounts, and container/volume/network cleanup. The PostgreSQL stop-loss also
rechecked this route; because Oracle-specific login `NVL` failed before a
principal was created, PostgreSQL returned the anonymous `403` boundary. This
is not permission-tree compatibility evidence.

Archived supplier references were checked against the framework inventory: `gikam-core-common-14.1.5.jar` SHA-256 `1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`; `gikam-core-web-14.1.5.jar` SHA-256 `26ee3d912789318d3cc0a23136a17382e34cfede3cd65d83d4dbc113b8245628`. These are read-only behavioral references, not runtime dependencies.

## Bounded query guard follow-up (2026-10-20)

OwnedQueryAccessConfiguration is loaded from the packaged spring.factories
after OwnedIdentityConfiguration. It registers a servlet filter for current
HTTP sessions rather than relying on early component-scan conditions. The
filter covers exactly these domain prefixes (including each prefix root):

| Prefix | Management menu | Read-only query menu |
| --- | --- | --- |
| /secure/emc/module/mdm/emp/emps | 100001 | 100001 |
| /secure/emc/module/mdm/equipt/equipts | 101501 | 20251000001 |
| /secure/emc/module/item/consume/consumes | 430201 | 20250800003 |

The same configured OwnedIdentityHttpBoundary and JDBC identity/provider
refresh the session and current account/role/menu state on protected calls.
Read-only equipment and consumable grants permit direct detail GET/HEAD/OPTIONS,
observed POST query shapes, and /page/edit-list only with exactly one
readonly=1 parameter. Management pages require the management menu. Personnel
retains its one-menu distinction. Unsupported write methods and query-looking
actions are rejected even for management/all-menu users; this guard implements
no business writes. Missing sessions/backend return HTTP 403 with
{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}.

Path inspection repeatedly decodes only to identify a protected target, normalizes
separator, matrix and dot-segment ambiguity, then rejects any changed protected
path rather than rewriting it. Multi-encoded prefixes, duplicate/trailing slashes,
matrix parameters and dot-segment paths are denied. Malformed encoding is rejected
closed; valid unrelated routes and sibling prefix names are not authorized by
this bounded guard. Boundary permission/session decisions and guard-specific path
and unsupported-write rejections reach the configured audit sink without URLs,
query parameters, session IDs or credentials in guard-specific audit resources.

The existing real Boot fixture now adds a test-only servlet marker under
/secure/* to show whether the packaged production filter allows a request to
reach the next servlet. It is not an implementation of personnel, equipment or
consumable resources and does not assert their page/data behavior. The fixture
uses real RSA login, Boot's DataSource and disposable synthetic Oracle tables.
Red observations were anonymous page HTTP 200, low-permission page HTTP 200,
equipment reader detail HTTP 403, unknown management write HTTP 200, a
query-looking write HTTP 200 and a double-encoded protected prefix HTTP 200.
The completed HTTP matrix passes after the guard changes, including same-session
menu revocation, role suspension and account suspension. Audit records retain
permission denial and path rejection; an account rejected during refresh loses
its stored identity and requires a fresh login after reactivation. Template-only
startup still omits login/key routes and now also denies protected marker calls.

The runner hashes both new guard/configuration sources along with the packaged
resource and existing identity/security test sources. Final clean-source report
is archived as query-access-guard-oracle-20261020.json; its exact commit, build
hash, counts and cleanup flags are authoritative. The earlier loopback servlet
identity test and 63-check JDBC contract remain in the same run. Ticket 08 and
its acceptance checkboxes stay open: global URL authorization, real browser
page/data acceptance, complete write auditing and the remaining identity modes
are outside this bounded follow-up. No frontend or owned-personnel sources change.

Final clean run: source commit b1ebe18dc429e7280b9f43ab09ba6ae44fcfafe8,
including integration 879b38285a21d10c0b84075352d0539add4108ef, with
source_dirty=false, 63 JDBC checks and 256 Boot checks. Built JAR SHA-256:
b36fcceca52891e04149dcf9e4db97e60883d3c419088c3506d0aff5305f8263.
Both guard sources are in the report's source hash manifest. Internal-network,
single-endpoint, no-port/no-bind isolation and container/anonymous-volume/network
cleanup all pass. The 22-check HTTP boundary, RSA credential contract, 248-class
declared contracts and supplier-runtime rejection pass; the unrelated runtime
health smoke also passes. Compact red assertions and transcript/fixture hashes
are archived in query-access-guard-red-20261020.json. An earlier Docker operation
and removal timed out; its exact disposable container/network were removed
explicitly before the successful reruns. Cleanup exceptions now preserve the
report and failed cleanup flags instead of suppressing evidence.
