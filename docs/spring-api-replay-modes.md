# Spring Web API replay modes

The supplier-exit work has two runtime modes. They share the same owned distribution and HTTP contracts, but their evidence classes are different.

## Development debug mode

Use the existing local WSL application on `18087` with its long-lived Oracle and
Redis. Keep Oracle running and do not reimport data. Reuse resources across cases;
when application source or configuration changes, batch the changes into one
checkpoint and perform the necessary app-only refresh before replay. Finish active
verification and fixture cleanup first. Application refresh is allowed and does
not require an additional approval; avoiding unnecessary restarts is an efficiency
rule, not an uninterrupted-application requirement. Keep supplier `18081` read-only;
sss2 deployment belongs to final delivery acceptance.

Each case must:

1. record the current baseline hash for the tables it can touch;
2. create a small fixture through the real Spring Web API whenever the behavior under test is a business write;
3. call the read/write/error route through HTTP and retain the response status and body summary;
4. restore the fixture through the API, or record a narrowly-scoped `fixture_control` cleanup when the API contract is the behavior being tested;
5. verify the baseline hash and audit sink before moving to the next case.

Reports from this mode are `diagnostic`. They are useful for rapid debugging and browser samples, but they cannot be registered as strict matrix passes.

The existing long-lived runtime helper is [refresh-owned-sss2-debug-app.sh](../scripts/refresh-owned-sss2-debug-app.sh), with the reuse contract documented in [frontend-debug-runtime.md](frontend-debug-runtime.md).

## Strict evidence mode

Reuse the long-lived `lims-owned-sss2-oracle` instance. It contains an immutable,
locked baseline schema and a writable runtime schema in the same Oracle
instance. A milestone resets the runtime schema from baseline once, then runs
foundation/personnel/equipment/consumables/secondary waves without restarting
Oracle or importing from sss2 again. Only an explicit `REFRESH_BASELINE=1` (or
`--refresh-baseline`) may read the existing sss2 clone schema and refresh the
baseline; production remains read-only.

The runtime manifest records `oracle_instance_id`, `baseline_schema`,
`runtime_schema`, metadata hashes, `schema_copy_method`, `baseline_refreshed`,
`oracle_reused`, `runtime_dirty` and `runtime_schema_restored`. A failed or
unrestored runtime is diagnostic-failed and cannot be promoted.

The strict runner records:

- source commit, source-dirty state, distribution and database artifact hashes;
- clone/snapshot identity and baseline table hashes;
- role, organization, mode and API route for every case;
- HTTP request/response observations and API-backed assertions;
- fixture creation/restoration, audit observations, sequence reads and transaction rollback;
- final Oracle, Redis, application, browser, network, volume and temporary-file cleanup.

Strict evidence is promoted only when every declared case is executed against the clone-derived runtime and the full evidence contract passes. A synthetic schema report remains diagnostic even if all of its local checks pass.

## Evidence lifecycle

`lims-recovery/evidence/` is an optional canonical publication area, not a runner
scratch directory. Runners write pending reports below
`.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/`; direct writes to
the canonical root are rejected. If a canonical report is promoted, the
standalone registry `evidence/evidence-index.tsv` is created and records that
report. Until then, a clean standalone clone may have neither the directory nor
the registry. Historical and diagnostic rows are retained in the parent
workspace registry at `.scratch/sunway-supplier-exit-api-first/evidence-archive/evidence-index.tsv`;
those rows are governance material, not deployment inputs or current strict evidence.

Use the governance commands before publishing a report:

```bash
python3 scripts/manage-evidence.py scan
python3 scripts/manage-evidence.py plan --output .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/evidence-migration-plan.tsv
python3 scripts/manage-evidence.py verify
python3 tools/api_route_closure.py \
  --ledger .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/current/api-route-ledger-current-head.json \
  --report .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/<report>.json \
  --output .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/<closure>.json
python3 scripts/manage-evidence.py promote \
  --report .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/<report>.json \
  --closure .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/<closure>.json \
  --output evidence/<report>.json
python3 scripts/manage-evidence.py classify
python3 scripts/manage-evidence.py verify
bash scripts/tests/test-api-evidence-gate.sh
```

Failed, stale, dirty, duplicate and clone-ready lifecycle files stay under the
feature diagnostic/archive areas with their SHA-256 and original path in the
registry. They are never counted as strict passes. `archive`, `rewrite` and
`prune` are dry-run by default; `archive --apply` and `rewrite --apply` are
explicit mutation steps, while `prune --apply` deletes only expired,
unreferenced, non-canonical files. Each action records SHA-256 and the original
path in an audit log, and same-name archive collisions receive a hash suffix.
A migration plan must be reviewed before an archive move; it records each
source/target, class, retention, reference and SHA-256 without changing files.
A diagnostic payload cannot be promoted as strict, and a closure cannot
authorize promotion unless every listed report is current, clean, real-HTTP
strict evidence accepted by the registry.
`prune --apply` writes its deletion preview before unlinking and records the
executed hashes in the prune audit.

Freshness is controlled by `implementation_tree_digest`. `source_commit` and
`repository_head_at_capture` are provenance and may differ after a docs,
ticket, registry or archive-only commit. A changed implementation scope,
nested project commit, runtime/runner contract or distribution input changes the
binding and requires an affected replay. `source_scope` is used to select those
affected runners, never to keep stale behavior evidence publishable.
`source_commit_exception` is not a promotion mechanism. A failed or interrupted
runner is registered in diagnostic storage on the next scan and cannot leave an
unclassified JSON file in the canonical root.

The output guard is unconditional for runners. Setting an environment variable
cannot publish into `lims-recovery/evidence/`; publication always goes through
the explicit `manage-evidence.py promote` command and its registry/closure
checks.

## SQL boundary

Large initialization SQL belongs in versioned `.sql` fixture files. Python and shell runners select the runtime, load the fixture, call HTTP, and collect evidence. Direct SQL is limited to minimal fixture control, sequence/audit reads, rollback checks and final cleanup. A direct SQL insert followed by a direct SQL query cannot close a Spring Web API behavior contract.

The current synthetic fixtures under the browser runners are transitional fast-contract material. New API-ALIGNMENT-GATE work should add clone-derived runners beside them and keep the synthetic reports explicitly diagnostic.

## Evidence output rule

API runners and strict runtime scripts write only to the feature diagnostic
tree by default. The canonical root is a publication area, not a scratch
directory. Use `manage-evidence.py scan` and `verify` before an explicit
`promote`; direct root output, stale source scope, `source_dirty=true`, missing
cleanup, incomplete fixture/audit/rollback or a boundary process is rejected.
`archive` and `prune` retain the registry row, SHA-256, source binding,
references, generation time and supersession reason. Diagnostic evidence stays
available for debugging but never counts as strict closure.

## Owned Java source-only routes

The route inventory keeps project Spring routes and owned Java source-only
routes in separate source-count fields. A source-only/boundary route is simply
an HTTP method and path supplied by an owned Controller/Resource because no
project controller source exists for that pair. `boundary route != boundary
process`: both kinds of route are dispatched by the same Spring application,
with the same wave Oracle and Redis. No compatibility microservice, JNI
replacement daemon, internal HTTP bridge, or extra application runtime may be
introduced. License/MAC/native-loader paths are removed and tested as
`removed-negative`; business behavior is implemented in owned Java.

## Declarative contract runner

Use `scripts/spring_api_contract_runner.py` for API-only cases. A debug replay
may reuse the existing sss2 runtime and must remain diagnostic:

```bash
python3 scripts/spring_api_contract_runner.py \
  --base-url http://127.0.0.1:28088 \
  --cases api-contracts/core/public-boundary.json \
  --runtime-report .scratch/sunway-supplier-exit-api-first/archive/historical/sss2-debug-runtime-manifest-20261027.json \
  --mode debug \
  --output .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/api-contract-debug-core.json
```

The runner records each HTTP response, route disposition, fixture lifecycle,
audit declaration, source binding and phase timing. Business writes must have
HTTP method/path cases; a case marked `write_via=sql` fails before the request.
For an API fixture, provide `setup_requests` and `restore_requests`; the runner
executes both through HTTP and records their responses. A strict write case
cannot claim `setup=executed` or `restore=restored` by putting those words in
JSON.

SQL-control fixtures require `--sql-fixture-executor`. The executor receives
`--fixture` and `--phase` and owns the database credentials; the main runner
records only the fixture hash and redacted output digest. `scripts/api_fixture.py`
rejects fixtures outside `api-fixtures/`, production markers, credentials and
private license material.

Bind reports to the current route ledger with:

```bash
python3 tools/api_route_closure.py \
  --ledger .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/current/api-route-ledger-current-head.json \
  --report .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/api-contract-debug-core.json \
  --output .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/api-route-closure-diagnostic.json
```

Diagnostic reports are retained as observations and leave route behavior
`unknown`. A strict report is promoted only when its source commit matches the
ledger, `source_dirty=false`, `strict_evidence=true`, the clone runtime is
isolated, and the complete cleanup contract is true.

### Persistent runtime reuse details

The reused-runtime helper defaults to keeping `LIMS_RUNTIME_SSS2` in place. A
standalone wave therefore reuses the existing schema and does not run Data Pump.
The first deployment, an explicit `--reset-runtime-schema`, or a baseline
refresh may recreate it. The runtime password is held only on `sss2` in a
0600 secret outside the disposable report directory; it is never copied into a
manifest or evidence file. An exclusive remote lock prevents two writable
strict applications from sharing the runtime schema. If the runtime schema or
its key-data hash differs from the baseline, startup fails and requires an
explicit reset instead of silently producing evidence from dirty state.
