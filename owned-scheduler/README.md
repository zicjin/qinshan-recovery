# Isolated scheduler test entry

This module supplies an explicit manual test entry at `/secure/owned-scheduler/page`. It is absent unless `owned.scheduler.mode=isolated-test`, a disposable file root, and an access-token file are configured. The test token grants this isolated entry only; no historical scheduler menu permission is inferred from personnel/equipment roles. Every protected operation requires loopback and the current token file; replacing/removing the file revokes access.

The `owned.scheduler.replay` task copies `fixtures/success.bin` (at most 1 MiB) to `outputs/<requestId>.bin` under the configured disposable root. Requests select a source-marked fixture plan: `success` copies immediately, `failure` actually attempts a missing file three times, and `retry` attempts the missing file twice then the valid fixture. Each attempt records a durable result, source marker, time, request ID, attempt number, and successful output SHA-256. There is no network transport or endpoint property. A replay is test infrastructure; it does not claim equivalence with historical file acquisition or result parsing.

The historical source declares four scheduled methods, represented in the task list as `disabled-in-isolated-env`:

| Identifier | Historical declaration | Delay |
| --- | --- | --- |
| `dcuAcquisition` | `PfEquiptAcqFileServiceImpl.dcuAcquisition` | 10 s |
| `dcuSceneAcquisition` | `PfEquiptAcqFileServiceImpl.dcuSceneAcquisition` | 15 s |
| `mergeEquiptAcqFile` | `PfEquiptAcqFileServiceImpl.mergeEquiptAcqFile` | 20 s |
| `emc.dcuAcquisition` | `EmcEquiptAcqFileServiceImpl.dcuAcquisition`, profile `equipt-acq-file` | 15 s |

These identifiers describe method declarations, not invented database job IDs. Requests to them produce a recorded DISABLED outcome and HTTP 403. Unknown job identifiers return 404. Historical implementations and `@Scheduled` wiring are excluded from the module artifact. No business cron or recurring task is enabled.

Build and run reproducible checks from the recovery directory:

```bash
export JAVA_HOME=/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391
export LIMS_PROJECT_ROOT=/home/ts/qinshan/lims-recovery/project
python3 scripts/tests/test-scheduler-runtime.py --output build/scheduler-runtime.json
python3 scripts/tests/test-scheduler-runtime.py --skip-build --browser --output build/scheduler-browser.json
```

The first command builds and runs the actual historical application main plus shipped scheduler JAR in a `bwrap --unshare-net` namespace with no external route and a writable temporary filesystem. The browser run uses a named `agent-browser` session and loopback HTTP. Its evidence explicitly reports that the host network namespace is used; it should not be confused with the first run's network isolation. Both use synthetic files only, preserve original source hashes, and remove the entire fixture root on exit. Neither changes `sss2` or any database. Token values are generated at runtime and omitted from evidence.

POST `/secure/owned-scheduler/tasks/owned.scheduler.replay/trigger` accepts exactly one `scenario` and one `requestId` form parameter. A duplicate request ID returns the prior terminal result after process restart; changing its task/scenario returns HTTP 409. `/tasks` and `/tasks/<task>/runs` expose status and attempt history. `runs.jsonl` is the local change record, limited to 1 MiB/1,000 attempts. Use a private, single-process disposable root; a partial attempt at crash requires a new request ID. This file journal and output copy do not form an atomic distributed transaction. A crash after copying but before journal append leaves an output that blocks that ID until reviewed. Do not point the configured root/token file at production paths.

Ticket 14 remains open: historical acquisition/manual-route equivalence, original database scheduler inventory, `sss2` comparison, and production job retry semantics remain unverified.
