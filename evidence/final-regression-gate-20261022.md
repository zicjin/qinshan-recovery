# Final local regression gate (2026-10-22)

All commands below passed from the clean integration worktree, with the nested `project` checkout clean:

- `./scripts/tests/test-vertical-slice-matrix.sh` — 37 cases
- `./scripts/tests/test-function-catalog.sh`
- `./scripts/tests/test-vertical-slices-08-14.sh`
- `python3 scripts/tests/test-full-function-catalog.py`
- `./scripts/tests/test-replay-bundle-manifest.sh`
- `./scripts/tests/test-replay-source-identity.sh`
- `./scripts/tests/test-replay-config-guard.sh`
- `bash scripts/tests/test-source-handoff-readiness.sh`
- `./scripts/tests/test-sss2-observation.sh`
- `python3 owned-distribution/tests/test-package.py`
- `bash scripts/reject-supplier-runtime.sh owned-distribution/target/distribution/app/lims-owned-application.jar owned-distribution/target/distribution/lib owned-distribution/target/distribution/modules`
- Python syntax, JavaScript syntax, root `git diff --check`, and nested `project` `git diff --check`

Result: `final regression gate: PASS`.

This gate validates the registered evidence and packaging boundaries. It does not claim full business parity, a complete role matrix, training/approval/low-frequency workflows, broad attachment association, or real `sss2` write observation.

Post-copy personnel browser replay: `personnel-browser-visible-test-copy-20261022.json` (`executed-pass`, 57 checks), covering visible-test copy target pagination/filtering, duplicate rejection and target cleanup in addition to the existing personnel checks.

The final personnel visible-test browser replay was rerun after removing temporary diagnostic waits; source `a623e9d` is clean and the same 57 checks pass.
