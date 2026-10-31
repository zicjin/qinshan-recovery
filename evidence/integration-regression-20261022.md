# Integration regression gate (2026-10-22)

The final local regression gate passed after the personnel, consumables, attachments and equipment evidence commits:

```text
./scripts/tests/test-vertical-slices-08-14.sh
vertical slices 08-14 contract/replay PASS

./scripts/tests/test-function-catalog.sh
function catalog test PASS

./scripts/tests/test-vertical-slice-matrix.sh
vertical slice matrix: PASS (39 cases; evidence and cleanup references valid)

python3 owned-distribution/tests/test-package.py
owned distribution package: PASS

Python browser harness compilation, JavaScript syntax checks, git diff --check, and git -C project diff --check
PASS
```

The package manifest and application JAR produced by the local package test have SHA-256 values recorded at capture time. The nested recovered project is pinned at commit `45296d2933e054462939af3b731b50b2c5c0a9eb`.

This gate validates the current contract/replay and packaging boundaries. It does not close the full-function specification: unknown business capabilities, broad role matrices, independent clean application startup, `sss2` write observation and other explicitly open ticket items remain open.

Captured artifact hashes:

- `owned-distribution/target/distribution/manifest.json`: `aa0d2392a7f22bd4531e5ce3c2428911f7027f1c8e3dc92814396631dd99f763`
- `owned-distribution/target/distribution/app/lims-owned-application.jar`: `0b29e57f60f4fb0e49062f898036084219d7a0d383a2e90a118813489aea63fb`

Equipment create browser replay: `.scratch/sunway-supplier-exit-api-first/archive/historical/equipment-only-browser-20261022.json` (`executed-pass`, 48 checks), covering default and isolated modes, authorized category filtering, cross-organization and low-permission denial, minimal required creation, sequence/server organization/audit, list/detail visibility, logout and exact full-baseline restoration.

Fixed equipment create replay bundle: `.scratch/sunway-supplier-exit-api-first/archive/historical/replay-bundle-equipment-create-browser-20261022.json`.

Additional personnel visible-test browser replay: `.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-browser-visible-test-write-20261022.json` (`executed-pass`, 47 checks), covering the isolated picker, batch assignment, cancellation, deletion, Oracle baseline restoration, permission boundaries and credential-free audit.

Independent clean checkout replay: `.scratch/sunway-supplier-exit-api-first/archive/historical/owned-distribution-clean-rebuild-current-20261022.json` and `.scratch/sunway-supplier-exit-api-first/archive/historical/browser-owned-clean-checkout-20261022.json` bind parent `97bc726`, nested project `45296d2`, independent Git objects, empty targets, ASCII build preparation, deterministic package validation and 19 browser checks; all temporary resources were removed.

Final local gate: `final-regression-gate-20261022.md` records the complete command set, including the 39-case matrix, source handoff, replay guards, sss2 receipt test, package rebuild, supplier rejection scan, syntax checks and both diff checks; all passed.

Post-copy regression: 39-case matrix, function catalog, vertical-slice replay, package rebuild, supplier rejection, syntax and diff checks passed after the visible-test copy implementation. The 57-check browser evidence is `.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-browser-visible-test-copy-20261022.json`.

Current equipment isolated write replay: `.scratch/sunway-supplier-exit-api-first/archive/historical/equipment-runtime-oracle-current-20261022.json` passes 121 checks from source `7fed11e`; no Oracle errors and cleanup passed.

Equipment create UI package boundary: `equipment-create-distribution-package-20261022.md` records a passing distribution package test after the isolated capability/create implementation. The bounded equipment create browser slice is now verified; procurement, calibration, acquisition, full editing/deletion and sss2 write/rollback remain open.


Post-equipment-create shared regression: `evidence/equipment-create-shared-regression-20261022.md` records `test-vertical-slices-08-14.sh` passing after the browser create implementation.
