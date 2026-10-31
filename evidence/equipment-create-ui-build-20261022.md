# Equipment create UI build boundary (2026-10-22)

The owned equipment workbench now contains an explicit isolated create dialog and capability probe. The original equipment POST route remains protected by the existing equipment management grant, loopback and isolated-mode request guard, and category organization scope in the DAO. The capability probe also checks loopback and isolated mode; no production endpoint is used.

Validation passed:

```text
./scripts/tests/test-owned-equipment-runtime.sh --build-only
owned declared public contracts: PASS (255 classes)
rest json wrapper contract checks passed: 36
mapper parameter contract checks passed: 13
supplier runtime rejection scan passed
owned equipment main/runtime module build passed
owned equipment runtime compile passed
```

JavaScript syntax and owned-web resource packaging passed. The prior current-source Oracle HTTP equipment replay remains `equipment-runtime-oracle-current-20261022.json` (121 checks, source `7fed11e`).

The equipment browser create flow is now verified in a dedicated two-mode replay: 48 checks cover default denial, authorized category selection, cross-organization rejection, minimal required creation, sequence and server organization, list/detail visibility, audit actor, low permission denial, logout, and full baseline restoration. Evidence is [equipment-only-browser-20261022.json](equipment-only-browser-20261022.json); the fixed replay bundle is [replay-bundle-equipment-create-browser-20261022.json](replay-bundle-equipment-create-browser-20261022.json). Earlier rejected reports remain diagnostic and are not registered as passing evidence. Procurement, calibration, acquisition, full equipment editing/deletion and sss2 write/rollback observations remain open.
