# Reconstituted framework provenance

This directory contains tracked source-recovery provenance and compile
snapshots. It is source/build coverage, not API evidence.

The final standalone build consumes:

- `framework-module-registry.json`;
- `decompiler-transforms.json`;
- generated Java source and original resources under
  `reconstituted/framework/`;
- per-module `module-provenance.json`, `resource-provenance.json`,
  `compile-report.json`, `public-dependency-allowlist.json` and
  `forbidden-content-report.json`.

The complete source closure snapshots are retained for reproducibility:

- `artifact-inventory-current.json`;
- `all-framework-source-closure-current.json`;
- `project-source-closure-current.json`;
- `pilot-result-20261104.json`.

The one-time supplier extraction and CFR/Vineflower pipeline is archived in
the parent repository at
`.scratch/sunway-supplier-exit-api-first/migration-tooling/`. Those tools and
the supplier run JAR are not required to build the final source-only
distribution.

## Final build

```bash
export JAVA_HOME=/opt/jdk1.8.0_391
export LIMS_PUBLIC_DEPENDENCY_DIR=/opt/lims-public-dependencies
export LIMS_EXTERNAL_ARTIFACT_ROOT=/opt/lims-build-artifacts
bash scripts/build-owned-distribution.sh
python3 scripts/verify-source-only-checkpoint.py \
  --run-jar /opt/read-only-provenance/gikam-third-czjky-14.1.5-run.jar \
  --distribution owned-distribution/target/distribution
```

Omit `--run-jar` when the external stock artifact is not available. The
checkpoint then verifies the tracked artifact identity and source/module
snapshots without hashing the external JAR.

`verify-reconstituted-framework-modules.py --compiled` verifies every tracked
generated source/resource hash and the Java 8 source-only module classpath.
Compilation and source provenance never count as strict API passes.
