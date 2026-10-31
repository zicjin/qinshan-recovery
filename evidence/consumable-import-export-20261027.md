# Consumable import/export slice (2026-10-22)

The consumables HTTP import/export contract was exercised in `isolated` mode.

```text
ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Oct 26, 2026 4:16:21 PM org.apache.catalina.core.StandardService startInternal
INFO: Starting service [Tomcat]
Oct 26, 2026 4:16:21 PM org.apache.catalina.core.StandardEngine startInternal
INFO: Starting Servlet engine: [Apache Tomcat/9.0.64]
Oct 26, 2026 4:16:21 PM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring embedded WebApplicationContext
Oct 26, 2026 4:16:24 PM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring DispatcherServlet 'dispatcherServlet'
Oct 26, 2026 4:16:27 PM org.apache.catalina.core.StandardService stopInternal
INFO: Stopping service [Tomcat]
Oct 26, 2026 4:16:27 PM org.apache.catalina.core.ApplicationContext log
INFO: Destroying Spring FrameworkServlet 'dispatcherServlet'
consumable import/export: PASS (23 checks; isolated Oracle loopback only)
```

The isolated mode covers historical XLS template export, valid material/stock import, invalid-row rollback, permission denial and audit. Other consumable workflows remain open.
