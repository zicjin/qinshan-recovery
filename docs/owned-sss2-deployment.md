# Owned supplier-exit deployment on `sss2`

`sss2` is the writable test host. `sss` is the supplier baseline and remains
read-only. The deployment creates a separate internal Docker network, Oracle,
Redis, and owned application containers. Existing `lims-replay-demo-s1-*`
containers are never stopped or replaced.

An owned Java source-only route in the API inventory is not a second service:
it is a Controller/Resource in this same Spring application and shares its
Oracle/Redis. The inventory distinction is for source accounting only; no
compatibility process or internal HTTP bridge is created.

Run from the standalone `lims-recovery` checkout. Use Python 3.9+ for the
inventory/evidence tools and inject restricted build artifacts through
`LIMS_EXTERNAL_ARTIFACT_ROOT` before building:

```bash
LIMS_EXTERNAL_ARTIFACT_ROOT=/path/to/external-artifacts \
bash scripts/verify-external-artifacts.sh
LIMS_EXTERNAL_ARTIFACT_ROOT=/path/to/external-artifacts \
bash scripts/build-prep.sh
LIMS_EXTERNAL_ARTIFACT_ROOT=/path/to/external-artifacts \
HOST_PORT=18082 bash scripts/deploy-owned-sss2-remote.sh
```

The migration reads the existing `sss2` application schema with a temporary
read-only `expdp` connection to its `XEPDB1` service, then imports into a new
owned XE schema with `REMAP_SCHEMA` and the source tablespace remapped to
`USERS`. It does not connect to `sss` or any production host. Attachment tables
(`T_CORE_FILE`, `T_EMC_EQUIPT_ACQ_FILE`) and large history/operational tables
are excluded. `T_CORE_FILE` and `T_CORE_AT_RECORD_LINE` are therefore created
as empty schema-compatible owned compatibility tables, with the file primary
key and audit-line primary key/index, so report-file and audit-line queries
return an explicit empty page instead of a missing-table error. No source rows
or attachment bytes are copied into these tables, so this does not prove
report-file, attachment, or audit-history parity. `T_EMC_EQUIPT_ACQ_FILE` and
Redis business cache state are not migrated.
The bounded import may retain Oracle `ORA-39346` comment charset warnings and
`ORA-39082` function compilation warnings; these are recorded in the report
and do not claim exact data parity.

The owned application stays on the internal Docker network. A small Python
forwarder binds only `127.0.0.1:<port>` on `sss2`, so the existing supplier
containers remain untouched while the owned instance is reachable locally.
The latest verified instance used port `18087`; choose another free port when
replaying the deployment.
The remote report records the source/target schema, exclusions, loopback URL,
container IDs, warning codes, synthetic compatibility tables, and cleanup
boundary without credentials. The report keeps the compatibility-table source
data flag false and retains the audit-line behavior as diagnostic until a
synthetic line fixture and strict replay are completed.

The distribution copies project static resources/templates and then layers the
read-only supplier frontend capture from
`owned-web/src/main/resources/frontend-original/`. The `/` route keeps the
production resource contract (`/static/gikam/main/css|js`) and serves the
original compressed Gikam/LCDP bundles; the removed simplified Gikam shell is
not a default input. Original project controllers for personnel, equipment and
consumables retain their template names, while their referenced Spring API
routes remain subject to the API closure gate. Browser evidence must still
expand beyond resource parity before the frontend parity ticket can close.

The remote report records `source_commit`, `source_dirty`, the resource probe
statuses and migration warnings. A browser report is kept separately so a
successful process/resource probe cannot be mistaken for page or API parity.

The owned app is available only on the selected `sss2` host loopback port. To
remove only this deployment:

```bash
  bash scripts/cleanup-owned-sss2-remote.sh
```

The cleanup command does not touch the existing application, Oracle, Redis,
network, or data volume.

The owned application sets `OWNED_SSS2_SESSION` as its session cookie name.
This is intentional: browser cookies are scoped by host and path, not by TCP
port. Without a distinct name, a simultaneous supplier `18081` or debug
`18088` tab could overwrite the owned session and make the original frontend
silently return to the login page.

To refresh only the owned application after a Java/API or frontend change,
reuse the existing clone-derived Oracle and Redis:

```bash
LIMS_EXTERNAL_ARTIFACT_ROOT=/path/to/external-artifacts \
OWNED_DISTRIBUTION_REUSE=0 bash scripts/build-owned-distribution.sh
bash scripts/refresh-owned-sss2-app.sh
```

This replaces only `lims-owned-sss2-app`, preserves 18081/18088 and the
database containers, and writes a diagnostic `app-refresh-report.json` on
`sss2`. It does not publish strict evidence.

## Frontend input boundary

The distribution uses the read-only supplier frontend capture under
`owned-web/src/main/resources/frontend-original/` as its default Gikam shell.
`build-owned-web.sh` layers project assets, the imported original bundle, and
owned business assets; the removed simplified Gikam files cannot override the
original `chunk-common`, `chunk-vendors`, `home`, or `workspace` resources.
`frontend-original/provenance.tsv` records the 18081 URL, byte count, SHA-256 and
capture provenance. The build manifest records the same hashes and marks API
closure as pending until the referenced Spring routes are closed.

The imported `home.js` and `workspace.js` have supplier license endpoints
removed. Generic UI or business `license` text is not treated as a runtime
license path. The package gate rejects native/license files and loader paths;
failed scans remain diagnostic and never become deployment evidence.
