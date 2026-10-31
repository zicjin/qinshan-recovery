# Equipment isolated write slice (2026-10-21)

The original equipment creation, generic choosable, DCU configuration detail/validation, maintenance-plan, maintenance-record, period-check summary/plan/record, calibration/verification/period-check choice, use-record, and test-method/item routes now have explicit isolated seams. The filter permits writes only when `owned.equipment.write.mode=isolated` and the client is loopback; the services recheck the equipment management grant and category organization scope, use an Oracle transaction and `SEQ_CORE`, and record audit events where applicable. The default configuration continues to reject equipment writes.

Validation uses the original `GikamWpjcApplication`, a disposable digest-pinned Oracle container on an internal network, synthetic rows, and a real HTTP client:

```text
ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Oct 26, 2026 10:21:02 PM org.apache.catalina.core.StandardService startInternal
INFO: Starting service [Tomcat]
Oct 26, 2026 10:21:02 PM org.apache.catalina.core.StandardEngine startInternal
INFO: Starting Servlet engine: [Apache Tomcat/9.0.64]
Oct 26, 2026 10:21:02 PM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring embedded WebApplicationContext
Oct 26, 2026 10:21:06 PM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring DispatcherServlet 'dispatcherServlet'
Oct 26, 2026 10:21:38 PM org.apache.catalina.core.StandardService stopInternal
INFO: Stopping service [Tomcat]
Oct 26, 2026 10:21:38 PM org.apache.catalina.core.ApplicationContext log
INFO: Destroying Spring FrameworkServlet 'dispatcherServlet'
equipment test-method/item write: PASS (157 checks; isolated Oracle loopback only)
```

The run verifies sequence-backed inserts, scoped queries, generic choosable/DCU/validation and summary DTO projections, calibration/verification/period-check choice routes, selectable maintenance events, period-check completion with linked plan updates, completed-record deletion rejection, maintenance-record completion and linked equipment validity updates, use-record update/delete, test-method/item insert/query/delete, auxiliary deletes, DTO field persistence, cross-scope rejection, invalid-body rollback, low-privilege denial, audit output, credential omission, and container/network cleanup. Procurement, calibration data maintenance, acquisition, collection-file, plan update, and complete equipment-field workflows remain open. The historical dynamic `maintLastDate` and `checkIndex` fields remain null because their source columns are not present in the owned schema.
