# Local milestone preparation and proof-bound cleanup

This flow keeps one application, Oracle and Redis. It does not stop an application,
restore data, start another runtime or promote a report. All inputs, snapshots,
private artifact observations and controlled proof-store objects stay outside Git.
The local preparation and verifier are evidence components; they do not establish
strict API coverage by themselves.

The application may be refreshed for a new implementation checkpoint and briefly
stopped/restarted for the existing milestone cleanup contract. Batch these actions
around active verification; keep Oracle and Redis running and do not reimport
data. Capture lifecycle snapshots after any app refresh so application identity
and start time refer to the actual tested process. These proof tools do not
perform the application lifecycle operations themselves.

A clean source checkpoint is required. First acquire a current local observation
and full baseline/runtime content snapshots with the existing read-only tools.
Prepare a private seed JSON containing `before_files` with exactly
`observation_before`, `baseline_before`, `runtime_before` references. Each reference
has `path` and raw `sha256`; relative paths resolve against the seed. Also supply
`distribution_dir`, `milestone_id`, `clone_identity`, and the explicit loopback
`loopback_url`. `distribution_dir` must be the resolved immutable directory mounted
read-only at `/opt/current-owned` by the existing application.

```bash
python3 scripts/lifecycle_proof.py --prepare-input /private/milestone/seed.json \
  --output /private/milestone/prepared.json
```

Preparation inspects the local Unix Docker socket, app/network/Oracle/Redis
identities and the exact read-only bind mount. It hashes the local and actual
container files: `app/lims-owned-application.jar`, `start.sh`, `lib/classpath.txt`
and every JAR named by that classpath. It inspects identities again to reject
races. It emits a private artifact observation and `local-prepared-v1` manifest.
No after hash, restoration, baseline immutability or runtime cleanliness is claimed
in advance: those five fields are null. The report has a default source aggregate
and a separate runtime contract binding for this prepared identity. Preserve that
runtime binding throughout the case execution; completion is a separate proof.
The prepared manifest must be used with the runner's deferred-cleanup mode.

After the actual HTTP batch, perform the authorized API fixture restore and
allowed final cleanup, then capture the three after files while the same app still
runs. Use the existing six-role `local-runtime-milestone-inputs` manifest, with:

- `files`: exactly six distinct relative snapshot paths and raw SHA256 pins;
- `prepared_runtime`: path and raw SHA256 of the original prepared manifest;
- `context`: exact `batch_id`, `milestone_id`, `clone_identity`,
  `application_binding`, `harness_binding`, `runtime_contract_binding` and the
  complete `artifact_sha256` mapping copied from the pending report.

After active verification and fixture cleanup finish, stop the same app in the
batched milestone cleanup window and run the verifier. This necessary app-only
stop/restart requires no additional approval. Its existing double inspection
requires that app to be stopped and the same Oracle/Redis to remain running.
Full baseline contents/columns must
match their before capture; runtime contents/columns must match runtime_before.
Use the same schema policy selected for preparation. Save the exact verifier JSON.
Restore app availability independently of export/finalization success. The
verifier performs no stop/start itself.

```bash
python3 scripts/verify-local-runtime-milestone.py \
  --schema-policy approved-missing-runtime-tables-v1 \
  --manifest /private/milestone/manifest.json > /private/milestone/lifecycle.json
```

A running app, wrong identity, incomplete/changed content, wrong source binding,
wrong prepared artifact or mismatched context leaves this verification failed.
No future after snapshot or declared cleanup boolean substitutes for this step.

Register all business-write proof artifacts using the separate HTTP-proof export
before cleanup finalization. Read-only reports need no invented write/audit proof.
Configure a permanent controlled lifecycle artifact store, then explicitly export
cleanup linked to this report:

```bash
export SUNWAY_LIFECYCLE_PROOF_STORE=/private/permanent-lifecycle-store
python3 scripts/lifecycle_proof.py --export-cleanup \
  --report /private/milestone/pending-registered.json \
  --lifecycle /private/milestone/lifecycle.json \
  --output /private/milestone/cleanup.json
python3 tools/finalize_api_report.py \
  --input /private/milestone/pending-registered.json \
  --cleanup-contract /private/milestone/cleanup.json \
  --output /private/milestone/finalized.json
```

Export revalidates successful execution, current clean application/harness/planning
binding, prepared runtime digest, case bindings and registered executed write
proofs. It archives the exact verifier JSON, input manifest, six snapshots,
prepared manifest and operated artifact observation into an immutable SHA object,
and records exact batch/context registration in `lifecycle-index.tsv`. A raw SHA
pins each file. Registration is an explicit trusted custody boundary; it does not
authenticate a remote server or establish unobserved acquisition history.

Finalization and the local strict per-case promotion gate independently recheck
registration and all embedded raw SHA pins, schema policy, full content framing,
time ordering, batch/source/runtime/artifact identities and both historical stop
inspections. A later same-app restart does not erase an observed prior stop.
Historical replay uses the archived inputs, so it works after original private
capture/distribution paths are removed. Current clean source binding is still
required. Retain/copy both the store objects and registry for another machine;
never copy only an unregistered cleanup JSON.

`executed-pass` remains diagnostic until the normal explicit `manage-evidence.py`
promotion and closure gates pass. This change does not upgrade old reports,
widen source exceptions, prove sequence rollback, or claim temporary private
captures were removed: retained proof artifacts are intentional.

Strict write inputs use HTTP proof version 2 and must declare the current-account
identity request in [HTTP_WRITE_PROOF.md](../scripts/HTTP_WRITE_PROOF.md). The runner
validates the actual login identity and observes the same authenticated session's
principal, selected organization and role assignments before and after each write.
Account responses contain secrets and are retained only as the documented minimal
projection plus raw body hashes; the raw account/login response is never archived.
A declared state projection proves those fields only. The lifecycle before/after
content snapshots remain the complete table/content restoration check. Version 1
write proofs cannot be upgraded by export or cleanup finalization.
