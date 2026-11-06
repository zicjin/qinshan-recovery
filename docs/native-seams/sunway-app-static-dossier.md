# `sunway-app.so` native seam dossier

This is a redacted, offline static dossier for the supplier native library. It
records observations that can guide owned Java replacement work. It is not
runtime evidence, an API contract, or permission to load the library.

## Provenance and identity

- Controlled input: external artifact at
  `external-provenance://materials/sunway-app.so`.
- Analysis copy used for this dossier:
  `.worktrees/ticket01-native-api-inventory/lims-recovery/materials/sunway-app.so`.
- SHA-256: `dc06e05d55252211e44b3a177267b68efc970b095d74c7ebd815057f5393d40a`.
- Size: `186512` bytes.
- ELF identity: ELF64, little-endian, x86-64, SYSV, shared object (`DYN`),
  entry point `0x4300`, not stripped.
- GNU build ID: `41a9487cddc6f9ccf4cb0accb2173926730d23b7`.
- Dynamic dependencies observed: `libstdc++.so.6`, `libc.so.6`.
- Static hardening observations: a non-executable GNU stack segment and a
  GNU_RELRO segment are present. No `BIND_NOW`/DFLAGS marker was observed in
  the dynamic table, so full RELRO is not claimed.
- The binary was never executed, loaded into a JVM, attached to a process, or
  used against Oracle, Redis, supplier, or production. License bodies and
  credential material were not read.

The standalone migration manifests establish that this binary is external
provenance only and is excluded from the standalone source tree and owned
distribution. The dossier does not copy or retain the binary.

## Inventory cross-check

Existing repository evidence, read as input to this dossier:

| Evidence | Observation |
| --- | --- |
| `.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/native-exports.jsonl` | 128 defined exports recorded; 54 names use the Java JNI export convention. |
| `.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/native-declarations.jsonl` | 48 Java native declarations recovered from selected supplier JARs. |
| `.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/native-correspondence.jsonl` | 54 correspondence rows; 47 declaration matches and 7 unmatched-in-selected-jars rows. Runtime observation is unknown for every row. |
| `.scratch/sunway-supplier-exit-api-first/evidence-archive/license-removal/manifest.json` | Records native/license deletion boundaries, external provenance, and static security markers without retaining license values. |
| `docs/standalone-external-materials.json` | Marks the native library and supplier run JAR as external/non-runtime material. |
| `com.sunwayworld.boot.thirdparty.sunwaylibrary.SunwayLibraryExtractor` | Static resource paths for platform-specific supplier native files were observed; runtime invocation remains unknown. |

## Selected export-to-declaration mappings

These mappings come from `native-correspondence.jsonl`. A declaration match only
establishes a name and descriptor correspondence; it does not establish that a
method was called in any runtime.

| JNI export | Java declaration | Static replacement seam |
| --- | --- | --- |
| `Java_com_sunwayworld_boot_ApplicationConfiguration_dataSource` | `ApplicationConfiguration.dataSource(DataSourceProperties): DataSource` | Spring DataSource configuration |
| `Java_com_sunwayworld_boot_ApplicationConfiguration_redisTemplate` | `ApplicationConfiguration.redisTemplate(RedisConnectionFactory): RedisTemplate` | Spring Redis template configuration |
| `Java_com_sunwayworld_boot_ApplicationConfiguration_sqlSessionFactory` | `ApplicationConfiguration.sqlSessionFactory(): SqlSessionFactory` | MyBatis session-factory configuration |
| `Java_com_sunwayworld_boot_ApplicationMvcConfiguration_addResourceHandlers` | `ApplicationMvcConfiguration.addResourceHandlers(ResourceHandlerRegistry): void` | Spring MVC static-resource handlers |
| `Java_com_sunwayworld_boot_ApplicationMvcConfiguration_addInterceptors` | `ApplicationMvcConfiguration.addInterceptors(InterceptorRegistry): void` | Spring MVC interceptor registration |
| `Java_com_sunwayworld_framework_tenant_TenantManager_dataSource` | `TenantManager.dataSource(String,String,String,String): DataSource` | tenant-scoped DataSource seam |
| `Java_com_sunwayworld_framework_security_authentication_AuthenticationHelper_logout` | `AuthenticationHelper.logout(String,String,String): void` | session/cache invalidation and logout audit |

## Static pseudocode and confidence

The following is a cautious translation of selected x86-64 disassembly and
nearby symbol/string references. It is **static approximation**, not recovered
source code. Confidence labels mean confidence in the observed call pattern,
not confidence in runtime reachability.

### `ApplicationConfiguration.dataSource` — medium confidence

Observed at `0xbffc` for the controlled SHA-256:

```text
checkInitialize(env, receiver)
checkNative(env, receiver)
if globalDataSourceProperties is null:
    globalDataSourceProperties = NewGlobalRef(DataSourceProperties argument)
resolve DataSourceProperties.initializeDataSourceBuilder()
resolve setUrl(String), setDriverClassName(String), setUsername(String), setPassword(String)
read environment strings and configure the builder
build a javax.sql.DataSource (Hikari-related symbols are present)
```

The call sequence and cached method IDs are directly visible. The exact
environment key selection and exception behavior are not established here.

### `ApplicationConfiguration.redisTemplate` — medium confidence

Observed at `0x5282`:

```text
checkInitialize(env, receiver)
checkNative(env, receiver)
resolve RedisTemplate class and its constructor
NewObject(RedisTemplate, RedisConnectionFactory)
resolve setter methods and call them with Redis serializers/configuration
return the configured template
```

The binary contains references to `RedisTemplate`,
`StringRedisSerializer`, cache writers, and Redis cache configuration. Exact
serializer choices and all setter calls require a broader, type-aware analysis.

### `ApplicationConfiguration.sqlSessionFactory` — medium confidence

Observed at `0x60ac`:

```text
checkInitialize(env, receiver)
checkNative(env, receiver)
FindClass(SqlSessionFactoryBean)
resolve its constructor and setDataSource method
read an environment boolean and configure the bean
create/configure the MyBatis session factory
return the resulting SqlSessionFactory
```

The `SqlSessionFactoryBean`, `setDataSource`, and MyBatis class references are
static observations. Mapper locations, aliases, plugins, and transaction
settings are not decoded by this dossier.

### `ApplicationMvcConfiguration.addResourceHandlers` — medium confidence for registration, low for paths

Observed at `0xe11c`:

```text
checkInitialize(env, receiver)
checkNative(env, receiver)
resolve ResourceHandlerRegistry.addResourceHandler(...)
build one-string arrays from static constants
invoke registry methods for one or more resource handler patterns
```

The presence of `ResourceHandlerRegistry`, `ResourceHandlerRegistration`, and
array-building calls is direct static evidence. The complete URL pattern and
location list is intentionally unknown; use the supplier HTTP resource
inventory for path truth.

### `ApplicationMvcConfiguration.addInterceptors` — low-to-medium confidence

Observed at `0xfb8f`:

```text
checkInitialize(env, receiver)
checkNative(env, receiver)
resolve an interceptor class and InterceptorRegistry.addInterceptor(...)
assemble several string values from static data
register/configure interceptor entries
```

The registration seam is visible, but the exact interceptor classes, path
patterns, and ordering are not decoded.

### `TenantManager.dataSource` — high confidence for delegation

Observed at `0x168bb`:

```text
return createDataSource(env, arg1, arg2, arg3, arg4)
```

The function is a short direct delegation to the native `createDataSource`
helper. Its tenant selection semantics and error behavior remain unknown.

### `AuthenticationHelper.logout` — medium confidence for cache/audit seam

Observed at `0x15844`:

```text
checkRuntime(env, receiver)
obtain the global security-provider service
assemble cache/key strings from static values
use cache/security-provider and login-log service symbols
invoke a pre-logout path, cache eviction, and logout/login-log operations
```

Static symbols include a security-provider service, cache manager, cache
eviction helper, `CoreLoginLogService`, and a `preLogout` method ID. The full
method order, key format, and failure handling remain unknown.

## License/MAC/native deletion boundary

Filtered static evidence includes category markers such as MAC mismatch and
global license state. These markers are retained here only as redacted
deletion-boundary observations. They are not a license implementation and do
not justify a Java “always valid” replacement.

The owned implementation must therefore:

- remove `sunway-app.so`, Sunway native loader resources, MAC binding, and
  Sunway license activation/check routes;
- preserve legitimate business fields named `license` and independent
  third-party licensing only where the allowlist permits them;
- expose removed-license behavior as an explicit negative contract;
- implement non-license DataSource, Redis, MyBatis, MVC, identity, and session
  behavior directly in the existing Spring application.

`boundary route != boundary process`: none of these seams justify a second
process, compatibility service, JNI daemon, loopback HTTP bridge, or second
Oracle/Redis/application runtime.

## Unknowns and non-evidence

- No REA provider, Ghidra database, or complete native call graph was available
  in this run; `file`/`readelf`/`nm`/`objdump` output is ordinary offline static
  evidence.
- No JNI export was invoked, so runtime reachability, class-loader behavior,
  exception paths, and native initialization order are unknown.
- Indirect calls and dynamically selected Java methods are not resolved.
- The dossier does not prove route behavior, response envelopes, permissions,
  organization scope, or API compatibility. Those require real HTTP tests in
  the owned Spring application.
- License bodies, credentials, private runtime material, and production data
  were not inspected or copied.

## Owned replacement and deletion path

The binary remains external-only and must not be an input to the owned build.
The supplier extractor and platform native resources are provenance for the
deletion scan, not runtime dependencies. The Java replacement work should use
the seam observations above as implementation hints, then prove behavior with
the shared Spring Web API, fixture restore, audit/rollback checks, and strict
cleanup evidence. This dossier itself is diagnostic documentation and cannot
be promoted to strict API evidence.

## Reproduction helper

`tools/native-seams/inspect-sunway-app-static.sh` regenerates a redacted report
from an explicitly supplied `.so` path. It validates a regular file, emits
identity/ELF/exports/filtered-string/disassembly sections, and never executes
or loads the input. Its output should be stored outside the canonical evidence
root unless separately registered as diagnostic provenance.
