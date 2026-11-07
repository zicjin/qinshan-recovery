# Exact seal-rule audit cleanup

`cleanup-seal-audit-journal.sql` is a versioned SQLPlus template. Render it with
`scripts/audit_fixture_cleanup.py`; the renderer never connects to Oracle or
performs HTTP. It supports only `LIMS_RUNTIME_LOCAL.T_EMC_SEAL_RULE` and only
final cleanup of rows introduced by one `owned-strict-...` fixture. It does not
remove business rows or the audit configuration parent. Those are removed first
through their original authenticated HTTP APIs.

```bash
python3 scripts/audit_fixture_cleanup.py \
  --journal private/local-runtime/seal-cleanup-journal.json \
  --output private/local-runtime/seal-cleanup.sql
python3 scripts/tests/test-audit-fixture-cleanup.py
```

Output must not exist; successful output is created with mode `0600`. Keep the
journal, raw HTTP transcript and rendered SQL outside Git and canonical evidence.
They contain private runtime identities and IDs. Console output includes hashes,
`database_executed=false` and `strict_evidence=false`, never private row data.
The older `cleanup-seal-audit-config-lines.sql` handles a single config line only;
it is not the full audit restoration procedure.

The journal has these exact keys (the synthetic `example()` in the CLI test
contains a complete executable format example):

- `version`: integer `1`; `owner`: `LIMS_RUNTIME_LOCAL`; `table`: `T_EMC_SEAL_RULE`.
- `targetId`: positive JSON integer for the fixture business row, not an ID
  threshold; `marker`: `owned-strict-` followed by lowercase letters/digits/dashes.
- `principal`, `organization`: the authenticated fixture identity. No password,
  cookie, token, license body or endpoint other than the allowed loopback base.
- `startedAt`, `finishedAt`: database-clock timestamps, `YYYY-MM-DD HH:MM:SS`.
  Oracle DATE has no timezone; capture and compare in the same database clock
  convention. Every candidate creation time must lie within the interval.
- `baseline`: `recordIds`, `lineIds`, `configLineIds`, each the complete numeric
  ID inventory of its corresponding audit table captured **before** the fixture.
  Empty inventories are valid; missing inventories and duplicate IDs are not.
- `records`: new `T_CORE_AT_RECORD` rows, each with `id`, `targetId`,
  `auditTrailType`, `auditTrailName`, `commentHash`, `createdById`,
  `createdByOrgId`, `createdTime`.
- `lines`: new `T_CORE_AT_RECORD_LINE` rows, each with `id`, `recordId`,
  `tableName`, `targetId`, `recordType`, `column`, `oldValueHash`, `newValueHash`,
  `createdById`, `createdByOrgId`, `createdTime`.
- `configLines`: new `T_CORE_AT_CONFIG_LINE` rows, each with `id`, `configId`,
  `columnId`, `auditTrail`, `createdById`, `createdByOrgId`, `createdTime`.
- `apiRestore`: `path` relative to the private journal directory, `sha256` of
  the raw transcript file, `businessRemoved=true`, `configParentRemoved=true`.
  The booleans alone never suffice: the referenced transcript is also verified.

All three new-row groups must be nonempty. Baseline IDs are excluded from their
corresponding delete group. Record and line targets are exactly
`T_EMC_SEAL_RULE$<targetId>`; line parents must exhaust the new record IDs.
Configuration lines must belong to that table. Uppercase database column names
are required. `auditTrail` accepts the captured `0` or `1`, never a wildcard.
At least one NAME old/new hash must equal the exact marker's UTF-8 SHA256.
`auditTrailName` remains the observed text in the journal. The live Oracle schema
stores it as CLOB (unlike the historical initialization DDL); the renderer hashes
its complete UTF-8 text and compares the Oracle CLOB hash, never CLOB equality
or a text prefix. This also supports Unicode without embedding the name in SQL.
All hashes are lowercase hexadecimal SHA256 over the **complete** value. JSON
null means Oracle NULL; it differs from a non-null empty CLOB. Oracle uses
`DBMS_CRYPTO.HASH(TO_CLOB(value),4)` and performs no truncation. Capture hashes
using the same Oracle CLOB semantics; DBMS_CRYPTO converts CLOBs to AL32UTF8.

The private HTTP transcript has exact keys `version=1`, `baseUrl`, `principal`,
`organization`, `marker`, `authentication`, `requests`. `baseUrl` allows only
`http://127.0.0.1:18087`, `18088` or `18089`.
`authentication` contains `sessionCookieSha256` (hash only) and `identity` with
`id` and `organizationId` matching the journal. Each request observation contains
`method`, `path`, `requestBody` (the exact parsed JSON request, null for GET),
integer `status`, `rawBody` (actual response text, including
whitespace), and `bodySha256` (SHA256 of those UTF-8 bytes). The required sequence:

1. GET `/secure/emc/module/mdm/basemdm/seal-rules/<targetId>` returns 200 JSON
   with the exact numeric `id` and `name=marker` before deletion.
2. DELETE `/secure/emc/module/mdm/basemdm/seal-rules` with
   `requestBody={"b":["",[{"id":<numeric targetId>}]],"p":{}}` returns 200 with
   empty body, JSON null or a nonnegative integer result. The original
   `GenericResource.delete` maps DELETE at the collection path; an item DELETE
   path is not supported.
3. POST `/secure/emc/module/mdm/basemdm/seal-rules/queries` returns 200 JSON
   with `requestBody={"p":{"n":1,"s":10,"f":{"id_SEQ":<numeric targetId>}}}`
   and integer `total=0`, `rows=[]`; normal Page pagination fields are accepted.
4. DELETE `/secure/core/module/sys/at-configs` with
   `requestBody={"b":["",[{"id":"T_EMC_SEAL_RULE"}]],"p":{}}` returns 200 with
   empty body, JSON null or a nonnegative integer result.

The GET observation must have `requestBody=null`. All four observations require
exact request body shapes; extra IDs, unrelated routes, unfiltered queries and
missing bodies are rejected. The transcript file SHA256 binds these parsed
request bodies along with the original response text and response hashes.

Capture observations from the actual authenticated runner; do not fabricate or
rewrite responses to fit the format. A JSON file and cookie hash are integrity
records, not authentication or execution attestation. The renderer cannot prove
that the HTTP occurred or that a supplied baseline was captured beforehand. The
runner must retain its authenticated identity and request/response evidence,
fixture baseline and lifecycle binding separately. The SQL independently checks
the **entire** business table is empty and the config parent is absent, so a
filtered empty query or a login response cannot authorize business-row deletion.
This tool creates no strict evidence or automatic restoration claim.

The generated block locks only exact new audit IDs using `FOR UPDATE NOWAIT`.
Before any DELETE it verifies the full baseline-plus-new ID inventories, every
candidate's identity/org/time/type/target/column/value hashes, exact child record
links, and complete target/config candidate counts. It then deletes line rows,
record rows and orphan config lines by exact numeric ID **and** pinned fields;
every statement must affect exactly one row. It checks baseline inventories and
business/config absence again, commits once, or rolls back the complete block.
It contains no business INSERT/UPDATE/DELETE, table lock, dynamic SQL, DDL or
schema reset.

Use a fresh dedicated SQLPlus transaction with the current schema explicitly
`LIMS_RUNTIME_LOCAL`, SYS or that schema as session user, and DBMS_CRYPTO EXECUTE
permission. The target table must remain empty. Preserve all baseline audit
rows; any extra or missing global audit ID causes refusal/rollback. Candidate
locks do not block unrelated user activity. Concurrent inserts can occur after
the final check, because this deliberately avoids table locks: the caller must
stop the milestone fixture producer and verify the post-cleanup runtime snapshot
before declaring restoration. Baseline ID equality preserves old rows from this
cleanup but does not attest their contents; the lifecycle's full content hashes
remain the restoration authority. This revision is covered by CLI tests only;
Oracle execution, rollback behavior and final lifecycle restoration must be
observed by the isolated milestone runner before promoting evidence.
