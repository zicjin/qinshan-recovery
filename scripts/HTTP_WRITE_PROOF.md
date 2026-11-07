# Executed HTTP write proof version 2

Ticket 09 tooling slice. `http_write_proof_version=2` is independent of the
20-case read-only fingerprint corpus. Changed write acceptance is a harness
semantic change. Old reports are never upgraded. Write proof cannot use
`harness_semantics_unchanged=true` or `revalidated-by-fingerprint`.

`run_cases` executes a write workflow and stores a private, exclusive-create
0600 JSON transcript alongside pending output. Public reports contain only its
schema/storage/path/SHA256 reference. The transcript binds case, batch,
application/harness/runtime bindings, artifact hashes, runtime identity, URL,
role, organization and successfully authenticated principal (`auth_user`).
Strict write proof requires successful runner authentication; role labels alone
are not authentication proof. Focused tests execute RSA login, current account,
business, audit and readback over a real loopback HTTP fixture. All non-identity
request/response fingerprints are recomputed from retained observations. Identity
observations use the explicit privacy projection described below. Phase timestamps
and one nonempty hashed cookie session are checked across every observation. Hashed cookie session context is
allowed; raw authentication and sensitive fixture fields are refused.

## Declarative input

A strict business write (PUT/PATCH/DELETE or operation write/create/update/delete)
requires these fields, in addition to the existing fixture/API declaration:

```json
{
  "audit_requests": {
    "before": {"method": "GET", "operation": "read", "path": "/audit/queries"},
    "after": {"method": "GET", "operation": "read", "path": "/audit/queries"},
    "lines": {"method": "GET", "operation": "read", "path": "/audit-lines/queries"}
  },
  "audit": {
    "records_path": "rows",
    "entity_binding": {"source": "request", "path": "body.id"},
    "expected": {"entity": "fixture-id", "event": "I", "principal": "manager", "role": "manager", "organization": "org-1"},
    "fields": {"entity": "entityId", "event": "event", "principal": "userId", "role": "role", "organization": "orgId", "record_id": "id", "timestamp": "createdAt"},
    "timestamp_policy": {"timezone": "Asia/Shanghai", "precision": "seconds"},
    "expected_records": 1,
    "line_records_path": "rows",
    "line_fields": {"record_id": "auditId", "line_id": "id", "field": "field", "old": "oldValue", "new": "newValue"},
    "expected_lines": 1,
    "changes": [{"field": "name", "old": null, "new": "created"}]
  },
  "state_proof": {
    "records_path": "rows", "entity_field": "id",
    "before_request": {"method": "GET", "operation": "read", "path": "/fixture-state/queries"},
    "after_request": {"method": "GET", "operation": "read", "path": "/fixture-state/queries"},
    "restored_request": {"method": "GET", "operation": "read", "path": "/fixture-state/queries"},
    "expected": {"rows": []},
    "expected_after": {"rows": [{"id": "fixture-id", "name": "created"}]}
  },
  "rollback": {"kind": "compensation"}
}
```

Observation requests require `operation=read` and the existing fast-runner
read admission policy, including rejection of the three source-identified
read-or-create property-data families.

This is a schema example, not a real API contract or evidence. Route paths,
field mappings, roles and entity values must come from source and executed
responses. `${fixture.<capture>.<field>}` expansion is supported; operation
capture can bind the generated entity through `entity_binding.source=response`.
The audit-before query may capture a broad fixture namespace before a generated
ID exists. The validator checks absence of the exact resulting entity/event
before, then presence/count afterward. Audit-after can use `capture_json` and
`id` so a separate lines query references its captured record ID. Omit `lines`
when each record embeds its lines; use `fields.lines` instead.

Source enum values may be declared through `audit.event_values`, for example
`{"I":"INSERT","U":"UPDATE","D":"DELETE"}`. The canonical semantic event
remains I/U/D and must agree with the business operation; mappings must be
complete and unambiguous.

The exact audit record must have unique record/line IDs, event I/U/D, principal,
org, and either its own matching role or the verified identity role assignments; a timestamp in the operation window, and exact changed field old/new
values matching business before/after state. Seconds are compared only after
explicit precision quantization; naive database timestamps require a declared
IANA timezone. No unconditional tolerance or timezone assumption is applied.
Before/after state contains an exact entity row projection, including ID and
all audited fields. Create/update/delete require the corresponding 0→1, 1→1,
1→0 entity cardinality. HTTP restoration must execute after after-state and be
followed by exact restored-state readback. A 2xx restore alone never suffices.

A rejected write uses `rollback.kind=rejected-write`, exact `failure_status`
and nonempty `failure_values` (JSON paths to expected error values), identical
before/after projections and `audit.expected_records=0` for the attempted
success event. This proves only that precise rejected stimulus. It does not
claim audit-storage-failure rollback. Positive API cleanup is compensation,
not transactional rollback. `fixture.restore_requests` is still executed in
finally; no SQL business writes are introduced.

## Identity role proof and privacy boundary

Every strict write declares this exact source-backed account observation. It is
executed in the fresh login session before and after the business stimulus:

```json
{
  "identity_request": {
    "method": "GET", "operation": "read", "path": "/core/module/sys/logins/account",
    "projection": {
      "adapter": "sunway-account-v1",
      "selectors": {"principal": "id", "account_organization": "orgId", "organization": "ext$.loginorgid", "roles": "ext$.roles"}
    }
  }
}
```

`CoreLoginServiceImpl.getAccount` in the reconstructed
`owned-core-base-service` returns `UserAccountDTO` with account organization and
login organization; its role branch serializes `CoreRoleUserBean` assignments
as a JSON string in `ext$.roles`. The wire ext map normalizes `loginOrgId` to
`loginorgid`. The fixed adapter parses that string and copies only `roleId` and
`userId` into `roles: [{"role_id": "...", "principal": "..."}]`. It copies only
`id`, `orgId` and `ext$.loginorgid` for the three scalar identity fields. Unknown
selectors, empty roles, role-name-only lists, another assignment principal and
missing identifiers fail closed. A selected login organization may legitimately
differ from the account organization: declared organization matches the former.
Declared role must be an actual role ID assigned to the authenticated principal.
`audit.fields.role` may be absent only because both identity observations carry
that verified role. Other audit identity/event fields remain mandatory.

HTTP 200 alone does not authenticate. RSA login must return the same minimal
account shape, the requested principal, nonempty organizations/role assignments
and session cookies before the runner transfers its fresh session. Strict write
admission additionally checks declared role and selected organization. A failed
login envelope at HTTP 200 never grants authenticated status. Account raw bodies,
`loginToken`, arbitrary account/role fields and raw session credentials are never
persisted in reports, transcripts or captures. Identity requests cannot capture
raw account JSON or text. Restoration still executes in `finally` on post-write
identity failure; a failed proof remains failed.

Identity transcript observations omit `body_base64`. `projection_metadata` records
adapter/version, the exact selector allowlist, raw body SHA256 and byte count,
minimal projection SHA256, `raw_body_recomputable=false` and
`trust=executed-runner-source-bound-producer`. The actual raw response fingerprint
is retained. The verifier independently checks projection shape/hash and its
identity semantics, adapter version/selectors, metadata format, request fingerprint,
session equality, context, and phase ordering. It **cannot recompute** a raw
response hash/fingerprint from a redacted projection or authenticate the remote
server from a digest. Linking this lossy projection to its raw response hash is
an assertion by the source-bound executed runner, the trusted observation
producer; artifact registration adds integrity/custody only. Authentication
metadata is digest-bound into the proof context. Version 1 payloads/references
are rejected; no old report is upgraded or revalidated as version 2.

For source audit keys built from table plus business ID, declare
`entity_binding.format={"prefix":"T_EMC_SEAL_RULE$","convert":"string"}`.
The verifier compares that formatted value to the audit entity while locating
state rows by the unformatted numeric ID. No expression evaluation is supported.
For database column `NAME` versus JSON property `name`, a change may declare
`{"field":"NAME","state_field":"name","value_type":"string","old":"before","new":"after"}`.
Only explicit string conversion is admitted; old/new comparisons remain exact.

`state_proof.projection_fields=["id","name"]` projects each raw HTTP row before
comparison to the exact declared rows. The list must be unique and include the
entity field and every audited `state_field` (or `field` if omitted); expected
rows must contain exactly those keys. This proves restoration of that declared
projection. Full row/table restoration is established separately by the lifecycle
content snapshot gate. Raw non-sensitive state responses remain in the transcript
so the verifier can recompute the projection. Empty successful operation/restore
responses retain their raw body/fingerprint/status; audit and state reads must
still provide the declared JSON evidence.

## Pending and promotion boundary

Version 2 supports JSON HTTP observations, empty operation/restore responses, and fixture response projections.
It has no external audit artifact ingestion, async polling, arbitrary text or
binary response proof, arbitrary identity adapters, controlled failure
injection or live audit-storage-failure claim. Unsupported shapes fail closed.
Fixtures must be non-sensitive. Legitimate business license fields are not
blanket forbidden; license bodies/keys, tokens, credentials and private keys
are forbidden. No raw proof or credentials enter canonical reports or Git.

Runner self-validation explicitly uses `require_registered=False`. Every write
report remains `executed-pass-pending-cleanup` with
`proof_registration_pending=true`, even if a cleanup mapping was supplied.
The public strict_case_contract gate requires registered artifact custody.
Configure an operator-controlled, absolute, real directory in
`SUNWAY_HTTP_PROOF_STORE`, then explicitly export:

```bash
SUNWAY_HTTP_PROOF_STORE=/controlled/proof-store python3 scripts/http_write_proof.py \
  --register-pending /private/pending.json --output /private/registered-pending.json
```

The exporter checks pending semantic proof, creates immutable 0600
`sha256/<digest>` objects, and publishes `evidence-index.tsv` rows with
`storage=artifact`, `retention=permanent`, exact raw SHA, batch/case and context
hash. Registration uses the existing evidence registry vocabulary in a separate
controlled-store registry. Its operator-configured root is the trust boundary;
a caller-supplied JSON registry is never authoritative. A locked, atomic TSV
update preserves existing rows. Canonical proof references contain only
schema/storage/artifact_ref/SHA, so another machine can verify against a
replicated controlled store without the developer's private path. The resolver
requires exact registration and raw SHA and rejects symlink roots/objects,
traversal, missing or duplicate rows and incompatible retention.

Export leaves report status pending; it does not finalize lifecycle cleanup or
promote evidence. Artifact hashes and controlled publication establish integrity
and custody, not authentication of remote business behavior. The runner remains
the trusted executed-observation producer. Immutable batch materialization
retains the exported reference through the case payload; the controlled store
must be retained and replicated separately from Git. Store custody/permissions
and distribution remain operator responsibilities.

Cleanup finalization must additionally bind the verified lifecycle report,
snapshots and runtime/source/artifact to this batch. This checkpoint does not
change the finalizer or claim strict closure, application/DB replay, supplier
parity or lifecycle cleanup. No existing report is registered or upgraded.

## Validation checkpoint

Focused proof corpus now includes actual RSA login/account with sensitive response
fields, missing/revoked roles, wrong principal/organization, session/context/hash
tampering, selector allowlist rejection and source-shaped audit/state cases.
The historical initial proof corpus had 20 tests using a real loopback HTTP fixture, with only
runtime/source/authentication admission boundaries substituted. Covers positive
compensation, rejected write unchanged state, separate audit lines, source event
enum mapping, naive second timestamps, privacy, local-only promotion rejection,
portable registered store proof, retention/traversal/symlink/hash/context/time
mismatch, read-or-create denial and read-only golden revalidation rejection.
Runner 18, evidence policy 7, fast differential 35, closure 12, plan 3, skeleton
10 and runtime contract 11 tests passed during this slice. The route-ledger
script could not find its historical parent `.scratch` fixture in this standalone
worktree; no output was forged. Full package/release and runtime replay remain
integration milestone work. No application, database, container or parent
checkout was changed.
