# Owned business Redis cache evidence gap

This is a diagnostic design record, not a passing runtime report. The current
owned business services do not expose a reliable business-cache contract:

- `RedisHelper` provides generic key/value and list operations, but no owned
  personnel, equipment or consumables DAO/service calls it.
- `OwnedReadSupport` delegates directly to the configured read DAO; the query
  paths currently execute Oracle reads without a cache seam.
- Existing `Cache-Control: no-store` headers describe HTTP response policy and
  do not prove Redis business caching.
- No source or historical evidence fixes the business key namespace,
  serialization format, TTL, organization/role scope, invalidation owner, or
  Redis outage policy.

Adding a cache without those facts would invent observable behavior and could
freeze revoked permissions or leak an organization-scoped result. The owned
runtime therefore remains explicitly `unknown` for business Redis hit,
invalidation and outage semantics.

## Executable test design once a contract is recovered

Use one disposable Oracle/Redis/application/browser shard with a synthetic
manager in `org-a`, a manager in `org-b`, and a reader. Record all HTTP, Oracle
query counts, Redis commands/keys, audit events, fixture restoration and final
cleanup in one report.

1. Seed one organization-scoped row and clear the Redis namespace. Execute the
   same authenticated list/detail request twice. The first request must record
   the declared miss and population; the second must record a Redis hit and no
   corresponding Oracle query.
2. Perform the declared isolated update/delete through the real HTTP path.
   Assert the specified invalidation (key deletion/version change) and verify
   the next read returns the Oracle value rather than stale data.
3. Repeat with `org-b` and the reader. Assert key scope and response scope do
   not allow cross-organization or low-permission reuse.
4. Stop or isolate Redis and run the declared failure policy (fail closed,
   bounded fallback, or explicit error). Record the status, audit and Oracle
   query behavior instead of assuming a fallback.
5. Exercise the declared TTL with an injectable clock or bounded wait, then
   restore the Oracle projection and remove Redis keys, containers, volumes,
   network, browser session and temporary files.

The contract must first supply the cache namespace, key inputs, serialization,
TTL, scope dimensions, invalidation triggers and outage policy. Until then,
there is no safe owned implementation or passing evidence to register.
