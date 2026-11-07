# Executed HTTP write proof version 1

Ticket 09 tooling slice. `http_write_proof_version=1` is independent of the
20-case read-only fingerprint corpus. Changed write acceptance is a harness
semantic change. Old reports are never upgraded. Write proof cannot use
`harness_semantics_unchanged=true` or `revalidated-by-fingerprint`.

`run_cases` executes a write workflow and stores a private, exclusive-create
0600 JSON transcript alongside pending output. Public reports contain only its
schema/storage/path/SHA256 reference. The transcript binds case, batch,
application/harness/runtime bindings, artifact hashes, runtime identity, URL,
role, organization and successfully authenticated principal (`auth_user`).
Strict write proof requires successful runner authentication; role labels alone
are not authentication proof. Focused tests substitute that authentication API
boundary and use actual HTTP for all business/audit/readback operations. It recomputes real request and response
fingerprints and observes phase timestamps. Hashed cookie session context is
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
role, org, a timestamp in the operation window, and exact changed field old/new
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

## Pending and promotion boundary

Version 1 supports JSON HTTP observations and fixture response projections.
It has no external audit artifact ingestion, async polling, arbitrary text or
binary response proof, source-to-principal inference, controlled failure
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

Focused proof corpus: 20 tests using a real loopback HTTP fixture, with only
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
