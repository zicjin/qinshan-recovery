# Local WSL runtime observation

Development and strict milestones may use one local application, Oracle and
Redis. The local identity is explicitly `local-wsl`; local results must never be
reported as sss2 execution. Private configuration, database dumps and credentials
remain external. An imported historical application image is not approved release
material; final distribution still comes from standalone tracked source.

The user tests at `http://127.0.0.1:18087`. Daily refreshes stay local; sss2 is
reserved for the delivery smoke. Mount a resolved immutable distribution path,
not the mutable `target/distribution` alias: replacing that directory while it is
bind-mounted can leave the running container without readable JAR/resources.

The read-only observer requires these running containers, each attached only to
the internal `lims-local-runtime-isolated` Docker network:

- `lims-local-runtime-app`
- `lims-local-runtime-oracle`
- `lims-local-runtime-redis`

Oracle uses FREEPDB1 with locked `LIMS_BASELINE_LOCAL` and open
`LIMS_RUNTIME_LOCAL`. The observer pins the local Unix Docker socket and SQLPlus
encoding. It neither creates schemas nor imports, starts, stops or resets services.

Run from the standalone root, saving observations outside Git:

```bash
mkdir -p private/local-runtime
chmod 700 private/local-runtime
umask 077
python3 scripts/observe-local-runtime.py > private/local-runtime/before.json
# Run the selected HTTP diagnostic cases.
python3 scripts/observe-local-runtime.py \
  --before private/local-runtime/before.json > private/local-runtime/after.json
```

An observation validates container/network identity, account state, normalized
column metadata and actual COUNT(*) coverage for every table. It rejects empty,
truncated or invalid SQL results. Quoted lowercase and Unicode identifiers retain
their exact spelling. No row bodies, credentials or Docker environment are emitted.
The output can still contain internal table/column names; keep full snapshots private.

The default `equal-schema` policy requires identical table sets. The approved
source fixture adds exactly seven runtime-only tables (105 columns). Opt in on
each capture and on the offline verifier; an observation cannot opt itself in:

```bash
python3 scripts/observe-local-runtime.py \
  --schema-policy approved-missing-runtime-tables-v1 \
  > private/local-runtime/before.json
python3 scripts/observe-local-runtime.py \
  --schema-policy approved-missing-runtime-tables-v1 \
  --before private/local-runtime/before.json > private/local-runtime/after.json
# Only after the separately authorized milestone restore/stop lifecycle:
python3 scripts/verify-local-runtime-milestone.py \
  --schema-policy approved-missing-runtime-tables-v1 \
  --manifest private/local-runtime/milestone-inputs.json \
  > private/local-runtime/lifecycle-verification.json
```

This policy reuses `schema_fixture.load_fixture()` provenance and
`schema_fixture.checks()` logical predicates. It pins the fixture SQL hash,
manifest hash, source hashes and seven exact definition hashes. Baseline tables
must remain present in runtime, baseline must contain none of the fixture tables,
and runtime must contain exactly baseline plus all seven. Row-count coverage uses
each owner's table inventory independently; baseline/runtime content need not
match each other.

The observer executes 314 metadata SELECT predicates for exact columns, positions,
types, nullable/default/hidden/virtual properties, PK and check constraints,
indexes and index columns, table/column comments and absent triggers. It executes
no fixture DDL. The output contains the exact predicates, counts, definition pins
and query/result hashes. The offline verifier recomputes the source provenance,
pins, predicates, query hashes and full result coverage on both observations;
arbitrary allowlists or claimed match booleans are rejected. Shared column
metadata must also agree with baseline. `schema_policy_id` and
`schema_policy_sha256` enter the runtime contract digest; the helper, fixture and
original source inputs enter the canonical harness scope.

These are sequential observations, not a transactional snapshot. Unchanged row
counts cannot prove unchanged row contents. Account locking cannot prove baseline
immutability. This tool therefore always emits diagnostic evidence with restoration,
content verification and strict cleanup unverified. It is not a strict-ready runtime
manifest. A milestone still needs content/key hashes, API fixture restoration,
audit/rollback observations and lifecycle cleanup before promotion. Error responses
can themselves create error-log rows; reads do not guarantee a pristine schema.

For an internal-only Docker network whose publish option does not expose a host
port, the foreground byte transport can provide local diagnostic access:

```bash
# Obtain the actual application IPv4 from Docker inspect first.
python3 scripts/local-runtime-transport.py \
  --listen-port 18087 --target-host 172.19.0.4 --target-port 18187
```

The address above is an example, not a fixed identity. The transport binds only
127.0.0.1 and does not interpret HTTP. Bridged requests reach the application from a
Docker gateway: they do not establish that loopback-only write guards will accept
writes. Do not relax write guards or call this a completed strict transport.

## Content and milestone verification

`tools/api/OracleContentFingerprint.java` reads every table in one read-only
Oracle transaction. Compile it with the external JDK 8 and use an external JDBC
driver. Its stdin properties must contain exactly `url`, `user`, `password` and
`schema`; only `LIMS_BASELINE_LOCAL` and `LIMS_RUNTIME_LOCAL` are allowed. Keep
those properties private, and never pass credentials on the command line. SYS
connections are rejected. An authorized read-only observer account can read the
locked baseline without unlocking it.

```bash
mkdir -p private/local-runtime/content-classes
"$JAVA_HOME/bin/javac" -encoding UTF-8 \
  -d private/local-runtime/content-classes tools/api/OracleContentFingerprint.java
# LIMS_JDBC_JAR points to the externally supplied Oracle JDBC driver.
"$JAVA_HOME/bin/java" -Xmx512m \
  -cp "private/local-runtime/content-classes:$LIMS_JDBC_JAR" \
  OracleContentFingerprint < private/local-runtime/fingerprint.properties \
  > private/local-runtime/content.json
```

The content format is `oracle-content-sha256-v1`: typed, length-framed row hashes
retain duplicate rows and ignore retrieval order. LOBs stream; sorting retains
row hashes in memory. Unsupported column types fail the entire capture without
a partial success report. The report contains metadata, counts and hashes, not
row values. Take independent before/after captures for both baseline and runtime.

`scripts/verify-local-runtime-milestone.py` accepts a private manifest with six
distinct relative file paths, each pinned by raw SHA-256:

```json
{
  "schema_version": 1,
  "kind": "local-runtime-milestone-inputs",
  "files": {
    "observation_before": {"path": "before.json", "sha256": "<64 hex characters>"},
    "observation_after": {"path": "after.json", "sha256": "<64 hex characters>"},
    "baseline_before": {"path": "baseline-before.json", "sha256": "<64 hex characters>"},
    "baseline_after": {"path": "baseline-after.json", "sha256": "<64 hex characters>"},
    "runtime_before": {"path": "runtime-before.json", "sha256": "<64 hex characters>"},
    "runtime_after": {"path": "runtime-after.json", "sha256": "<64 hex characters>"}
  }
}
```

Replace each placeholder with the corresponding file's actual `sha256sum`.
The two observations are captured while the application is running. After an
authorized milestone has restored its fixtures and stopped that application,
run the read-only verifier:

```bash
python3 scripts/verify-local-runtime-milestone.py \
  --manifest private/local-runtime/milestone-inputs.json \
  > private/local-runtime/lifecycle-verification.json
```

It rechecks file pins, table/column coverage and schema digests, compares baseline
before/after separately from runtime before/after, then inspects local Docker
twice. Runtime-before is the restoration target; it may legitimately differ from
the locked baseline after versioned API configuration repairs. Audit/error tables
are included. Oracle/Redis identity and start time must remain unchanged, and
the application must be stopped. A running user application causes a failed
report; the verifier never stops it. Do not stop the user's test application just
to obtain a passing report.

Even a verified lifecycle component has `strict_evidence=false` and `real_http=false`.
It does not prove snapshot acquisition authenticity, fixture/audit/rollback,
stored code or API behavior. General metadata remains limited to observed
columns; the optional approved fixture proof additionally checks its exact DDL
properties for those seven tables. Case HTTP evidence,
source binding and the full promotion contract remain separate requirements.

The current-head identity gate validates application/harness/planning bindings
against current source and runtime against the report's own runtime contract.
HEAD remains provenance. Governance-only changes do not invalidate behavior;
changed harness semantics, missing runtime contracts and old composite-only
reports remain stale. Identity validation never creates a strict pass.

The source-only package gate builds twice in a disposable source snapshot and
compares all shipped hashes. It never repackages into the mounted active distribution
or immutable cache:

```bash
# Choose a disk-backed temporary directory with enough space for both builds.
mkdir -p build/package-gate-tmp
TMPDIR="$PWD/build/package-gate-tmp" SOURCE_ONLY_CHECKPOINT=1 \
  python3 owned-distribution/tests/test-package.py
```

`SOURCE_ONLY_CHECKPOINT=1` explicitly selects build/package validation without the
canonical API evidence lifecycle prerequisite. It does not approve API closure or
release. Run the ordinary package/evidence gates separately for final release.
