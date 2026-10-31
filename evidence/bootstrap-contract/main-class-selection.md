# Main-class selection dependency group

The owned `SunwayApplication` now extends pinned public Spring Boot 2.3.4,
returns `ConfigurableApplicationContext` from both observed static `run`
overloads and registers Spring Boot's deduced main class in the owned cache
before launch. It defines no license API, input or native capability.

`SunwayBootApplication` retains `@Configuration`, `@Conditional`, runtime
retention, TYPE target and `@Documented`. Its package-private condition selects
the cached main application class. The actual Spring registration differential
runs supplier and owned classes in isolated classloaders, selecting each of two
annotated configuration classes in turn. Both produce
`[Selected:true:false, Other:false:true]`. Declared public contracts are checked
by the normal owned build against the archived supplier class inventory.

Run with Java 8:

```sh
JAVA_HOME=build/jdk1.8.0_391 scripts/tests/test-sunway-application-selection.sh
```

The fixed Spring Boot artifact SHA-256 values are in
`scripts/prepare-owned-public-deps.py`; supplier class/artifact hashes are in
`evidence/framework-contract/`. This test refreshes a real local Spring
configuration context, but it deliberately does not start the recovered LIMS
application. `GikamCoreApplication`, business scanning, native configuration
replacement, authentication, Oracle and Redis remain the next integration
boundary. The group cannot close ticket 07.


The run-contract gate additionally invokes both `SunwayApplication.run(Class, String...)` and `run(Class[], String[])` against a real Spring context. It verifies command-line argument forwarding, active context lifecycle, cached main-class selection, and supplier/owned differential execution with the supplier common JAR isolated.

```sh
JAVA_HOME=build/jdk1.8.0_391 scripts/tests/test-sunway-application-run-contract.sh
```
