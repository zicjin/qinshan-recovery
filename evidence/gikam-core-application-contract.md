# GikamCoreApplication contract evidence

The owned framework now provides the supplier-facing `GikamCoreApplication`
superclass used by the recovered application entrypoint. It preserves the
public superclass/interface shape, `main`, protected `configure`, security
customizer, Tomcat web-server customizer, bridge method, and Fastjson double
serializer registration. The owned implementation deliberately omits the
supplier's third-party extractor static side effect; that path is outside the
license-free runtime and has no current isolated behavior evidence.
It retains the base configuration annotations for component scanning,
transaction management, and stand-alone property gating.
The current minimal fixture does not observe a scanned marker bean from the
base annotation, so component scanning remains a declaration boundary rather
than completed business-component discovery.

The test loads supplier and owned classes parentlessly, compares declarations,
then starts a child-loaded `@SunwayBootApplication` fixture through the owned
`SunwayApplication`, verifies a fixture bean registration, and closes the real
Spring context.

```text
bash scripts/tests/test-gikam-core-application.sh
GikamCoreApplication contract: PASS (declarations, configuration annotations, fixture registration, owned Spring context, security customizer)
```

This closes the application-base declaration/context leaf only. The recovered LIMS application's
configuration graph, authentication, database/Redis wiring, and page paths
remain open.
