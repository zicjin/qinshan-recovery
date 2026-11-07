# Local WSL runtime observation

Development and strict milestones may use one local application, Oracle and
Redis. The local identity is explicitly `local-wsl`; local results must never be
reported as sss2 execution. Private configuration, database dumps and credentials
remain external. An imported historical application image is not approved release
material; final distribution still comes from standalone tracked source.

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
  --listen-port 18187 --target-host 172.19.0.4 --target-port 18187
```

The address above is an example, not a fixed identity. The transport binds only
127.0.0.1 and does not interpret HTTP. Bridged requests reach the application from a
Docker gateway: they do not establish that loopback-only write guards will accept
writes. Do not relax write guards or call this a completed strict transport.

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
