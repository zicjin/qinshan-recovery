# Audit query original Service/Mapper restoration

Source checkpoint: standalone `f74c2d12cc6dae541de5abb890a46e5c4d6e7770`.

The owned audit controller's custom JDBC query ignored `auditTrailType` and
lowercased Oracle compound column names. It now retains its existing
`audit.read`/refreshed-session guard and delegates the complete original request
wrapper through an in-process `AuditTrailReadOperations` port, implemented by a
project Spring component using the original record and line services. The
controller accepts an unannotated `RestJsonWrapperBean`, allowing the original
MVC argument resolver to preserve `f`, `qf`, `af`, `mf`, ordering, paging and
options. It returns the original typed `Page` to normal serialization. There is
no SQL fallback; a missing integration bean returns the controlled audit
unavailable response. EMC query aliases and authentication-only status aliases
remain registered.

Record scope is an appended server `CREATEDBYORGID EQ` filter. Line scope is a
server `RECORDID EQ` filter and `ownedAuditOrgId` special parameter applied by
`selectPaginationByFilter` **after** client filter extraction. The four original
line Mapper dialect resources add an output-neutral parent-record `EXISTS`
clause when that key is present. A present null/empty key returns no rows;
key absence preserves unrelated original callers. Resource provenance retains
the supplier input hashes and records transformed destination hashes. No scope
alias is projected into the original `ext$` response. Lines still require a
numeric record ID, accepting normal `f`/`qf` equality criteria and the previous
direct `p.recordId_SEQ` form. Conflicting filters remain ANDed.

The public MVC/MyBatis/JDBC fixture first reproduced `auditTrailType=INSERT`
returning two organization-visible rows instead of the expected one. The repaired
path returns one typed row. The fixture compiles changed controller/port/adapter
sources into a temporary directory and runs the original wrapper resolver,
Service pagination, DAO query, Mapper XML, MyBatis interceptor and service
transaction advice against disposable H2 Oracle-mode tables. Only dependency
assembly uses fixture DAO subclasses and Spring field injection; queries and
entity/page conversion are original implementations. The schema models two
organizations sharing a root, parent/child organization disagreement, and full
72,000-character CLOB content. No application or database server is started.

Run from the standalone root, supplying existing public build inputs:

```bash
python3 scripts/tests/test-audit-query-mvc.py \
  --jdk "$AUDIT_TEST_JDK" \
  --public-deps "$AUDIT_TEST_PUBLIC_DEPS" \
  --framework-classes "$AUDIT_TEST_FRAMEWORK_CLASSES" \
  --app-classes "$AUDIT_TEST_APP_CLASSES" \
  --h2 "$AUDIT_TEST_H2" \
  --spring-test "$AUDIT_TEST_SPRING_TEST"
```

Coverage includes original filter forms, type/name/target/time and no-match,
sorting/page/count, typed camelCase fields, retained parent metadata extensions,
CLOB content, anonymous/no-grant/revoked/identity-refresh failure, exact parent
organization scope, spoofed direct/special scope, missing/invalid record ID,
EMC/status aliases, blank trusted organization and missing port. Direct public
Mapper checks verify absent/null/empty scope semantics. Existing standalone
owned-framework contracts now exercise the public typed-wrapper signature and
missing-adapter behavior instead of the deleted private parser.

H2 checks do not prove Oracle deployment or strict API closure. The main agent
must build/package once, refresh only the owned application and record real
HTTP/Oracle diagnostics before any strict promotion. Original raw/custom/detail
routes still have their pre-existing weaker access policy; this repair does not
close that family-wide bypass surface. API-ALIGNMENT-GATE remains OPEN; strict
passes added by this fixture: zero.
