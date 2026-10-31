# Existing application bootstrap groups and unresolved order

Entry source: `project/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java`
at submodule commit `06c2bcf65fdf73a7c89b2f6faa4dce47848500f5`.
The source's two denied editor routes remain part of the authentication/security
configuration; an isolated boot must retain that customization.

The inherited `GikamCoreApplication` imports these fixed Spring Boot groups:
servlet server/dispatcher/MVC/error/encoding, HTTP converters/Jackson, JMX,
multipart, property placeholders, Thymeleaf, Redis, JDBC datasource and
transactions, RestTemplate, task scheduling/execution, mail, AOP, Elasticsearch
client and repositories. It scans `com.sunwayworld`, enables transaction
management and repository discovery, and is conditional on
`sunway.app.stand-alone.enabled=true` (also matched when missing).

The `SunwayBootApplication` annotation is a `Configuration` with
`SunwayBootCondition`, not a generic `SpringBootApplication`. Its condition
accepts only the main application class selected by `SunwayApplication` and
stored in `MemoryCacheManager`. This main-class identity group must exist
before component scanning.

After identity/annotation selection, the scanned configuration graph includes:

| Group | Public native boundary still requiring owned Java behavior |
| --- | --- |
| initialization/context | `ApplicationConfiguration.initApplicationContext`, `init`; ordering and main-context exposure remain unobserved |
| persistence | datasource, SqlSessionFactory, SqlSessionTemplate, MapperScannerConfigurer; dialect/configuration and transaction behavior need isolated Oracle evidence |
| web | HandlerAdapter, locale resolver/interceptor, message source, multipart, Thymeleaf template resolver |
| async/scheduling | task executor, scheduler registration and uncaught exception handler; isolated startup must disable business task profiles before refresh |
| Redis/cache | template, cache writer, cache manager; serializer/namespace behavior remains unknown |
| client/integration | RestTemplate and public auto-configured mail; local replay-only endpoints and no network side effects are required |
| security | `SunwaySecurityConfiguration`, AuthenticationHelper and servlet context filters/listeners; license APIs are removed while business authentication must be implemented |

`ApplicationConfiguration` has 19 native method declarations in the archived
class contract. The native symbols establish candidate boundaries, not actual
runtime order. The current owned module does not provide the inherited core,
main bootstrap facade, annotated scanner configuration, or this full graph, so
the real entrypoint is not yet an owned-runtime executable. No auto-config group
has been silently excluded to manufacture a successful boot.

Safe next observation is a dependency-closed main-class selection group using
public Spring Boot 2.3.4 and the actual entrypoint source, followed by controlled
configuration refresh with an isolated Oracle/Redis baseline and business
authentication retained. Web startup alone cannot close ticket 07.
