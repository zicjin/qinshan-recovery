# Supplier artifact reconstitution

This directory records source-reconstitution provenance and diagnostic gate
results. It is not canonical API evidence.

## Inputs

- `.external-materials/gikam-third-czjky-14.1.5-run.jar` is read-only and
  externally injected.
- `tools/cfr-0.152.jar` is the primary Java decompiler and runs on JDK 8.
- Vineflower 1.11.1 is the class-level fallback supplied through
  `VINEFLOWER_JAR`. Its launcher requires Java 17 or newer; generated source is
  compiled with JDK 8.
- `docs/reconstitution/decompiler-transforms.json` contains the tracked,
  deterministic source repairs applied only to Vineflower fallback output.
  Every operation has an exact expected match count. A decompiler upgrade that
  changes the input shape fails the run instead of silently modifying source.
- Public libraries come only from non-`gikam-core-*` nested JARs. No supplier
  run JAR or private `gikam-core-*` JAR is placed on the javac classpath.

## Generate the complete artifact manifest

```bash
scripts/reconstitute-framework-source.sh inventory \
  --output-dir /tmp/lims-artifact-inventory
```

The output contains `artifact-manifest.json` and `artifact-summary.json`.
The complete manifest records outer members, private framework members,
nested JAR identities, kinds, hashes, source recovery requirements and final
distribution policy. It is generated data and should be retained in controlled
artifact storage rather than pasted into Git.

## Run a source closure pilot

```bash
VINEFLOWER_JAR=/path/to/vineflower-1.11.1.jar \
VINEFLOWER_JAVA=/path/to/java-17-or-newer \
scripts/reconstitute-framework-source.sh pilot \
  --target-source project/src/main/java/com/sunwayworld/emc/framework/mybatis/mapper/EmcGlobalMapper.java \
  --output-dir /tmp/lims-source-pilot \
  --extract-web-resources
```

The pilot computes transitive private-framework class references, decompiles
with CFR first, applies Vineflower only to files that fail Java 8 compilation,
and repeats that targeted fallback until compilation converges. It writes
`web-resources.json` with per-path SHA-256 and a deterministic resource-tree
hash when `--extract-web-resources` is selected.

The current `EmcGlobalMapper` seed produces:

- 531 private framework classes and 399 top-level Java sources;
- 351 CFR sources, 48 Vineflower fallback sources and 12 files receiving a
  tracked VF transformation;
- 351 generated sources normalized to remove decompiler trailing whitespace
  and duplicate end-of-file blank lines;
- zero missing external framework classes, zero decompile failures and zero
  Java 8 compile errors;
- no supplier run JAR or private `gikam-core-*` JAR on `javac`'s classpath;
- 5,302 `gikam-core-web` non-class resources for provenance and later
  distribution allowlisting.

The tracked materialized pilot is under
`reconstituted/pilot/owned-core-common/`. It remains isolated from the existing
runtime `owned-framework` module until the complete dependency graph,
duplicate-class disposition and license/MAC/native removal gates are complete.

## Verify

```bash
python3 scripts/verify-reconstituted-framework.py \
  --run-jar .external-materials/gikam-third-czjky-14.1.5-run.jar \
  --output-dir /tmp/lims-source-pilot
```

The verifier checks artifact identity, member hashes, nested JAR policy,
generated-source provenance and hashes before and after transformations,
decompiler exit state, resource hashes, Java 8 compilation metadata and absence
of a private `gikam-core-*` JAR on javac's classpath. A
`pilot-compile-failed` report remains a truthful diagnostic result.

This source-only compile gate does not prove runtime startup, route behavior or
API closure. Fast-equivalent HTTP results, class presence, compilation and
process startup remain separate from strict API evidence.
