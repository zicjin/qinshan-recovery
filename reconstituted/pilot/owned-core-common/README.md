# Reconstituted framework pilot: core common

This directory contains the first tracked, provenance-bound source
reconstitution output for Ticket 20. It is a compile pilot, not a promoted
runtime module and not API evidence.

- Supplier input:
  `.external-materials/gikam-third-czjky-14.1.5-run.jar`
- Supplier input SHA-256:
  `4a720797810fd2f8cca6e22604657028cb87830ca0e24bb55a5f42f040f6ee9f`
- Source input:
  `BOOT-INF/lib/gikam-core-common-14.1.5.jar`
- Seed:
  `com.sunwayworld.framework.spring.annotation.GikamBean`, reached from
  `EmcGlobalMapper`
- Closure: 531 private framework classes represented by 399 top-level Java
  sources
- Decompiler: Vineflower 1.11.1 running on Java 21, with Java 8 as the compile
  target
- Compile gate: passed with zero errors and no `gikam-core-*` JAR on the
  compiler classpath

`module-provenance.json` records every source JAR member, class hash, generated
source hash before and after transformations, decompiler identity, deterministic
transformation IDs and the compile result. The transformation rules live in
`docs/reconstitution/decompiler-transforms.json`.

The source remains isolated from the existing `owned-framework` runtime module
until the framework-module dependency graph, duplicate-class disposition and
license/MAC/native seam removal gates are complete.
