# Tickets 08-14 vertical-slice harness evidence

This record belongs to the owned-framework rebuild. It is bound to the source
commit that contains `tools/vertical_slices/harness.py` and
`scripts/tests/test-vertical-slices-08-14.sh`.

## Verification command

```bash
cd lims-recovery
bash scripts/tests/test-vertical-slices-08-14.sh
```

Expected output is `vertical slices 08-14 contract/replay PASS`. The test uses
only in-memory fixtures and a temporary attachment directory. It does not read
production configuration, connect to an external endpoint, or mutate the
`sss2` clone.

## Covered public seams

| Ticket | Harness evidence | Runtime status |
| --- | --- | --- |
| 08 | Login/logout, role permission checks, organisation data scope, denied-event audit | `unavailable-runtime` |
| 09 | Personnel list, filter, no-match, detail and out-of-scope denial | `unavailable-runtime` |
| 10 | Equipment permission boundary and query seam | `unavailable-runtime`; purchase/maintenance remain unknown |
| 11 | Consumables query and stock field; permission boundary | `unavailable-runtime`; stock movement remains unknown |
| 12 | Isolated attachment registration/read/missing error; fixture integration success, retry, idempotency and production endpoint rejection | `unavailable-runtime` |
| 13 | Report permission, parameters, replay result and preserved third-party authorization boundary | `unavailable-runtime`; page entry/third-party service not live |
| 14 | Manual task trigger, failure/retry, rollback journal and blocked external write | `unavailable-runtime`; scheduled trigger remains disabled in isolation |

The harness deliberately reports `runtime: unavailable-runtime`; a passing
contract test is not a claim that the real LIMS page, Oracle schema, or vendor
report service has been replaced. Page-level acceptance still requires an
isolated deployment and real browser evidence. Disabled tasks are recorded as
`disabled-in-isolated-env`, never as a successful production run.

## Isolation properties

- Integration endpoints are fixture URIs. Known production/private endpoint
  markers are rejected before a request can be made.
- Attachment paths are resolved beneath a temporary isolated root and reject
  traversal.
- Scheduler tasks marked `external_write` are blocked when isolation is on.
- Audit records include actor, operation, source/fixture and retry/idempotency
  details; the test writes them to a temporary JSONL file only.
- Fixtures contain no credentials, license content, MAC input or Sunway
  compatibility API.
