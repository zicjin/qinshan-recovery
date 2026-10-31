# Consumable stock write slice (2026-10-21)

The consumables HTTP write contract was exercised in `default` mode.

```text
ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Oct 21, 2026 12:50:52 AM org.apache.catalina.core.StandardService startInternal
INFO: Starting service [Tomcat]
Oct 21, 2026 12:50:52 AM org.apache.catalina.core.StandardEngine startInternal
INFO: Starting Servlet engine: [Apache Tomcat/9.0.64]
Oct 21, 2026 12:50:52 AM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring embedded WebApplicationContext
Oct 21, 2026 12:50:55 AM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring DispatcherServlet 'dispatcherServlet'
consumable stock default mode: PASS (9 checks; write routes denied)
Oct 21, 2026 12:50:56 AM org.apache.catalina.core.StandardService stopInternal
INFO: Stopping service [Tomcat]
Oct 21, 2026 12:50:56 AM org.apache.catalina.core.ApplicationContext log
INFO: Destroying Spring FrameworkServlet 'dispatcherServlet'
```

The isolated mode covers material creation, stock creation, stock-in, inventory, receive, abandon and delete. Imports and other consumable workflows remain open.
