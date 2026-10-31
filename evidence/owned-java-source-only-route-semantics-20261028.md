# Owned Java source-only route semantics

This note is the current route/runtime interpretation for the supplier-exit
API closure. It is bound to implementation source `9ee55ca7` (the later
documentation-only commit does not change the source scope).

The inventory separates 6,597 project Spring routes from 124 owned Java
source-only routes because the latter have no matching project controller
source. The second count is a source ownership category. It does not create a
compatibility boundary in the deployment topology: `boundary route != boundary
process`.

Every source-only route must resolve directly to an owned Java
Controller/Resource and its Service, Mapper/DAO, Spring configuration and
identity/tenant seams. Project and source-only routes share the same Spring
application, Oracle and Redis within their API wave. The runner rejects a
source-only case that declares a separate runtime, compatibility process,
internal HTTP bridge or JNI replacement daemon.

License, MAC binding, native-loader and `sunway-app.so` paths are removed and
are tested through an explicit `removed-negative` HTTP disposition. Other
business behavior is implemented and verified through the same real Spring
Web API contract as project routes. This document is semantic evidence only;
it does not promote any route from `unknown` to a strict pass.
