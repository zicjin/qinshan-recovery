# Owned report adapter

This module implements one bounded historical report line: the `homePageReport`
`reportingService` line is opened through the protected common-report page,
accepts its two declared values (`ORGID` and `REPORTDATE`), applies the current
session and line-level user/role grants, and renders a digest-addressed fixture
from an explicitly configured isolated replay root.

The replay client has no HTTP client and no production endpoint fallback. A
fixture must be a regular file named by the SHA-256 of the canonical request
and must carry `source=synthetic-isolated-replay`; non-200 fixtures are visible
bounded failures. The result is rendered in a sandboxed iframe with a restrictive
content security policy.

This is adapter evidence, not proof that any third-party report server is
available. FanRuan lines, report editing, report workflow, exports, and other
report categories remain `unknown` or `disabled-in-isolated-env`. Third-party
authorization materials remain outside the Sunway license removal scope; this
module contains no license mock, license API, or copied authorization secret.
