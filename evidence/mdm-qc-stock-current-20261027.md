# Consumables and MDM stock write slice (2026-10-27)

The original consumables and MDM stock HTTP write contracts were exercised in `isolated` mode.

```text
ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Oct 27, 2026 12:50:37 AM org.apache.catalina.core.StandardService startInternal
INFO: Starting service [Tomcat]
Oct 27, 2026 12:50:37 AM org.apache.catalina.core.StandardEngine startInternal
INFO: Starting Servlet engine: [Apache Tomcat/9.0.64]
Oct 27, 2026 12:50:37 AM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring embedded WebApplicationContext
Oct 27, 2026 12:50:40 AM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring DispatcherServlet 'dispatcherServlet'
Oct 27, 2026 12:51:03 AM org.apache.catalina.core.StandardService stopInternal
INFO: Stopping service [Tomcat]
Oct 27, 2026 12:51:03 AM org.apache.catalina.core.ApplicationContext log
INFO: Destroying Spring FrameworkServlet 'dispatcherServlet'
consumable stock write: PASS (108 checks; isolated Oracle loopback only)
```

The isolated mode covers material creation, stock creation, stock-in, inventory, receive, uniqueness, abandon and delete. Imports, maintenance/concentration/QC workflows, page forms and strict matrix replay remain open.
