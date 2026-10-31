# Consumable stock distribution regression (2026-10-23)

```text
python3 owned-distribution/tests/test-package.py
owned distribution package: PASS
3ad19e4768008726b668b0b09cefca9bbf113c005a451bb97688b482ab4162ae  owned-distribution/target/distribution/manifest.json
48b4a66234537641e70a7d86ed4deb18967544adf71fd6fb22523740d163c6f8  owned-distribution/target/distribution/app/lims-owned-application.jar
```

The package verifier still passes after adding the consumable stock workbench seam. Browser stock mutation remains diagnostic only; the existing 44-check isolated HTTP contract is the passing write evidence.
