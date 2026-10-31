# Consumable import/export slice (2026-10-22)

The consumables HTTP import/export contract was exercised in `default` mode.

```text
ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Oct 22, 2026 4:19:25 AM org.apache.catalina.core.StandardService startInternal
INFO: Starting service [Tomcat]
Oct 22, 2026 4:19:25 AM org.apache.catalina.core.StandardEngine startInternal
INFO: Starting Servlet engine: [Apache Tomcat/9.0.64]
Oct 22, 2026 4:19:25 AM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring embedded WebApplicationContext
Oct 22, 2026 4:19:28 AM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring DispatcherServlet 'dispatcherServlet'
consumable import/export default mode: PASS (6 checks; write routes denied)
Oct 22, 2026 4:19:28 AM org.apache.catalina.core.StandardService stopInternal
INFO: Stopping service [Tomcat]
Oct 22, 2026 4:19:28 AM org.apache.catalina.core.ApplicationContext log
INFO: Destroying Spring FrameworkServlet 'dispatcherServlet'
```

The isolated mode covers historical XLS template export, valid material/stock import, invalid-row rollback, permission denial and audit. Other consumable workflows remain open.
