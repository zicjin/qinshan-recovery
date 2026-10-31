# Personnel request graph leaf frontier

`LocalContextHelper` and `DialectRepository` were rechecked against the pinned common-framework JAR with JDK 8 `javap -c -p`.

`LocalContextHelper` is not dependency-closed. Its public state methods use Spring Security's `SecurityContextHolder`, supplier `LoginUser`, `CoreUserBean`, `CoreUserService`, `AuthenticationHelper` native/cache methods, `I18nHelper`, and servlet request/response types. `getLoginUser()` performs service lookup; trustee-name formatting performs localized message lookup. Recreating it as a thread-local DTO holder would change authentication and service behavior.

`DialectRepository` is not dependency-closed. Static initialization calls `ApplicationContextHelper.getEnvironment().getProperty("spring.datasource.driver-class-name")` to set Kingbase/Dameng flags. `getDialect()` dynamically scans and instantiates `Dialect` implementations, checks `ApplicationContextHelper.isProfileActivated`, and falls back to `OracleDialect`. The `Dialect` interface itself requires unresolved SQL metadata, table/entity context, and bound-SQL contracts. A fixed Oracle dialect or silent environment fallback would be an unverified facade.

The owned tree has no `LocalContextHelper` or `DialectRepository` implementation. No partial context, security, license, or dialect facade is added. Existing `EntityContext`/`EntityColumnContext` remain the only closed metadata leaves. Personnel request/page/mapper runtime remains blocked by the cyclic `RestJsonWrapperBean`/`PageRequest`/`MapperParameter` graph and its DAO/service/domain dependencies.
