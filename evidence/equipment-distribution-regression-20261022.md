# Equipment distribution regression (2026-10-22)

```text
python3 owned-distribution/tests/test-package.py
owned distribution package: PASS
aa0d2392a7f22bd4531e5ce3c2428911f7027f1c8e3dc92814396631dd99f763  owned-distribution/target/distribution/manifest.json
0b29e57f60f4fb0e49062f898036084219d7a0d383a2e90a118813489aea63fb  owned-distribution/target/distribution/app/lims-owned-application.jar
```

The verifier covered deterministic clean rebuilds, duplicate class conflict rejection, required external configuration, supplier-runtime rejection, and exclusion of test/browser classes from the shipped application JAR.
