# Owned bootstrap contract boundary

The recovered application entrypoint is
`com.sunwayworld.emc.projectfunction.GikamWpjcApplication`. Its observed public
path is `main` -> `SunwayApplication.run(Class,String...)`; the application
extends `GikamCoreApplication` and carries `@SunwayBootApplication`.

Read-only classfile evidence shows that `SunwayApplication` extends Spring
Boot's `SpringApplication`, stores the main application class in
`MemoryCacheManager`, and exposes `run(Class,String...)`,
`run(Class[],String[])`, and `getSunwayMainApplicationClass`. The owned slice
therefore implements only the proven plain-Java `MemoryCacheManager` dependency
(`com/sunwayworld/framework/cache/memory/MemoryCacheManager`) and tests put/get,
supplier memoization, null sentinel, presence and remove semantics. Its source
class is bound to SHA-256
`2ecec92e38f7db6547a530221a51566a42e9dbfaf7233ffcdfc6f003580784db` in the
contract inventory.

The owned framework now supplies the `GikamCoreApplication` base contract,
including component scanning, transaction management, and stand-alone property
gating annotations, and starts a child-loaded Spring fixture through the owned
`SunwayApplication`.
The child-loaded fixture registers its own marker bean through the observed
main-class selection path; this verifies fixture configuration registration but
does not prove business component scanning.
The supplier's third-party extractor static side effect is intentionally absent
from the owned base because it is outside the license-free runtime boundary and
has no current isolated behavior evidence. The recovered LIMS application still
cannot be claimed started: its `ApplicationConfiguration`/security graph,
business component scan, database and Redis wiring remain unresolved, and that
configuration class declares 19 native methods. This evidence therefore closes
the base-class leaf without claiming a working application deployment.

The minimal owned fixture also did not discover a test component through the
base `ComponentScan` annotation. Business component discovery remains an
explicit bootstrap gap rather than a consequence inferred from annotation
presence.
