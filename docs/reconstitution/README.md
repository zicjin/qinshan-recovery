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
- Private framework JARs are identified by actual `com/sunwayworld/*.class`
  content as well as the established `gikam-core-*` naming convention. Public
  libraries are the remaining content-classified dependency JARs. No supplier
  run JAR or private framework JAR is placed on the javac classpath.

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

## Compile the complete imported project closure

```bash
VINEFLOWER_JAR=/path/to/vineflower-1.11.1.jar \
VINEFLOWER_JAVA=/path/to/java-17-or-newer \
scripts/reconstitute-framework-source.sh pilot \
  --project-closure \
  --target-source project/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java \
  --output-dir /tmp/lims-project-source-closure \
  --extract-web-resources
```

`--project-closure` imports every `com.sunwayworld.*` import from all
`project/src/main/java` sources, uses every content-classified private
framework JAR, and compiles the complete project source tree. The current
result is recorded in `project-source-closure-current.json`:

- 229 direct project imports converge to 748 framework classes represented by
  603 generated Java sources;
- 543 sources use CFR and 60 use Vineflower fallback across three targeted
  compile rounds, with 12 tracked transformations;
- all 3,038 project source files compile under Java 8 with zero errors, zero
  missing framework symbols and no supplier/private JAR on the compiler
  classpath;
- 5,302 core-web resources retain deterministic source JAR and resource-tree
  hashes.

The full-closure source tree is currently diagnostic output. It must be split
into dependency-directed framework modules with tracked provenance before it
becomes distribution source.

## Compile every private framework source

```bash
VINEFLOWER_JAR=/path/to/vineflower-1.11.1.jar \
VINEFLOWER_JAVA=/path/to/java-17-or-newer \
scripts/reconstitute-framework-source.sh pilot \
  --all-framework-sources \
  --target-source project/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java \
  --output-dir /tmp/lims-all-framework-source-closure \
  --extract-web-resources
```

`--all-framework-sources` decompiles every top-level class from all
content-classified private framework JARs. It excludes exact project-source
framework overlays from the javac targets so supplier bytecode is the source of
record for those framework class names. The output is the input for
dependency-directed framework module assembly and records the full per-class
decompiler and deterministic-transformation provenance. It remains diagnostic
source coverage until tracked modules build independently and the original
project application builds against them.

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
