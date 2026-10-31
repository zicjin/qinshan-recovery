# Deterministic build and replay supplement（r0930deterministic）

日期：2026-09-30（Asia/Singapore）。本记录补充 AC-02/AC-03/AC-09 的确定性构建验证。

## Deterministic build

- `scripts/build.sh` now runs `tools/normalize_jar.py` after Maven packaging. The normalizer sorts ZIP entry
  names and fixes archive timestamps while preserving entry bytes/compression and executable layout.
- Main and clean source trees both produce exactly:
  `c423fa1aa21e12226509bd1dde8bc036759af5e87f9d7b2a5173bec940a77329`.
- Before normalization, the two builds had different outer JAR hashes but all 7,868 entry contents matched;
  after normalization the outer hashes match byte-for-byte.
- `scripts/tests/test-build-normalization.sh` passes; source inventory remains
  `changed=2`, `same-code=281`, `identical=2,844`, `eol-only=2,045`, `src-only=13`.
- A second complete `bash scripts/build.sh` on the unchanged main tree also produced
  `c423fa1aa21e12226509bd1dde8bc036759af5e87f9d7b2a5173bec940a77329` (`BUILD SUCCESS`), confirming repeatability
  across both clean-tree and same-tree rebuilds.

## Replay

- Clean-built image: `lims-app:14.1.5-deterministic`, image ID
  `sha256:c8cb07efea0a988964bd3dec17aade01227bce07febce48a6f8266ee2b85e533`.
- New restore/replay instance: `r0930deterministic`, `127.0.0.1:18095`, internal subnet `10.99.0.0/16`.
- Manifest source-only identity: root `sha256:0e04405c9b115701bddf914352a42eff6abf30791291d610927c1b7bb6918fc1`,
  project `sha256:8af57689f414c2c81799fd3720d9a627d76c67ab4f1a912c38e39d9975f5111c`.
  Receipt: `evidence/source-handoff-r0930deterministic.txt`.
- Manifest launcher hashes: stack `974be4cc…`, oracle snapshot `c8728c3a…`, isolation `6c5051e8…`; archive hash and
  Oracle/archiver image labels match the verified snapshot.
- Container JAR SHA equals the deterministic clean-build SHA; application log records
  `Tomcat started on port(s): 8081` and `Started GikamWpjcApplication`.
- Isolation-check PASS; authorized page shows `共206条` and `材料名称（构建验证）`.

AC-04 full data/attachment inventory and AC-10 external handoff requirements remain open.
