# Frontend debug and evidence runtime modes

Frontend investigation uses two separate modes.

The development debug mode reuses one long-lived disposable owned runtime for
multiple Ticket 11 cases. The active runtime is the `ssh sss2` debug deployment
on `127.0.0.1:18088`; the older owned instance on 18087 and the supplier
baseline on 18081 remain available and are not stopped or restarted. A debug
case may navigate between the workspace, menu, template and static-resource
routes without starting another Oracle, Redis or application. Each case must
reset its fixture before execution, check baseline restoration afterward, and
compare the relevant supplier observation before the next case.

The 18087 owned app uses `OWNED_SSS2_SESSION`; this debug app uses
`OWNED_SSS2_DEBUG_SESSION`. Browser cookies do not include the port in their
scope, so distinct names are required when 18081, 18087 and 18088 are open in
one browser profile. A cookie collision otherwise looks like a successful
login followed by an unexplained return to `/`.

The evidence mode is the existing execution-group/strict-matrix path. It may
start one clean Oracle/Redis/application lifecycle per execution group and must
record each case's HTTP observations, fixture restoration, audit observations
and final cleanup. Evidence mode is used for matrix refreshes, milestones and
release handoff only.

Reports from the debug mode are diagnostic. They cannot be copied into a
strict evidence path or promoted through `source_commit_exception`; a strict
report must still satisfy the current source, verifier, artifact, role,
organization, fixture and cleanup contract.

The read-only baseline collector is
[`frontend-baseline-readonly.py`](../scripts/frontend-baseline-readonly.py).
It compares the production root and initial Gikam resource URLs with the
supplier test port without credentials and writes a diagnostic report. It
does not perform login, writes, migration or production mutation.

Before starting a new replay, check for an existing matrix/runner process and
the `sss2` runtime containers. If the owned Oracle, Redis and application are
healthy, keep them running and reuse the same runtime for the next diagnostic
case. A route or page change is not a reason to restart Oracle. The current
`sss2` check showed the supplier 18081 process and the owned 18088 debug process
alive with their Oracle/Redis containers healthy; no strict runner was active.

To refresh the application code without replacing that runtime, build the
distribution and run:

```bash
bash lims-recovery/scripts/build-owned-distribution.sh
FORCE_REFRESH=1 HOST_PORT=18088 bash lims-recovery/scripts/refresh-owned-sss2-debug-app.sh
```

The helper reuses `lims-owned-sss2-oracle`, `lims-owned-sss2-redis` and their
internal network, and starts only a separate diagnostic app on 18088. It
leaves 18081 and 18087 untouched. The remote `debug-app-report.json` is
diagnostic and is never a strict matrix report. Remove only this optional app
with `ssh sss2 'docker rm --force lims-owned-sss2-debug-app'` after debugging.

When a permitted test account is available, collect an authenticated sss2
diagnostic without restarting the runtime:

```bash
python3 lims-recovery/scripts/frontend-authenticated-baseline-sss2.py \
  --username "$TEST_USER" \
  --password-file lims-recovery/private/test-account.env \
  --output .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/frontend-authenticated-baseline-sss2-YYYYMMDD.json
```

The collector keeps only status codes, hashes, resource URLs and menu IDs. It
does not attempt production authentication and cannot be promoted to strict
evidence.

## Original frontend resource check

The debug runtime must load the imported original bundle from the owned
Spring application. A read-only comparison against 18081 currently records:
`chunk-common.js` 1,868,329 bytes, `chunk-vendors.js` 2,640,202 bytes,
`home.js` 75,611 bytes and `workspace.js` 275,265 bytes before the targeted
license-endpoint cleanup. The historical 18087/18088 stub sizes (253/344/285
bytes) are not acceptable default frontend inputs. After the original overlay
refresh, both owned ports must serve the imported bundle sizes recorded by the
inventory. Use the generated frontend inventory, manifest hashes and a
resource-only diff; this check does not count as API strict evidence.
