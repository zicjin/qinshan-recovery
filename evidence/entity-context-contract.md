# Entity metadata context contract

`EntityContext` and `EntityColumnContext` are dependency-closed metadata leaves for the owned personnel boundary. Their supplier declarations and bytecode were inspected from the pinned common-framework JAR. The owned implementation preserves serial UIDs, nullable metadata setters, numeric-ID detection, property-descriptor fallback to `ext$`, and ordinary versus extension-map value lookup.

The parentless differential probe now exercises 51 observations against separate supplier and owned loaders. It covers every public getter/setter, serial UIDs, default and nullable metadata, list/reference semantics, primitive/wrapper/string/null numeric-ID detection, ordinary and case-insensitive property descriptors, `ext$` fallback, ordinary and extension-map `getValue`, unknown properties, null inputs, and wrong-target errors. The supplier common JAR is hash-bound before loading, and the supplier and owned framework classes are asserted to come from separate parentless loaders and artifacts. It passes with JDK 8u391:

```text
entity context differential: PASS (51 observations; 1 supplier cold-context error boundary recorded)
```

One bounded difference is recorded rather than hidden: an ordinary property read with a wrong target object causes the supplier's cold `ReflectionUtils` error-construction path to throw `NullPointerException` at `I18nHelper.getMessage`, while the owned path returns its `ApplicationRuntimeException` wrapping `IllegalArgumentException`. The regression gate checks the exact error origin and cause for this bounded difference. The owned metadata leaf itself performs the same descriptor and value dispatch; the mismatch belongs to the unresolved application exception/i18n context. Extension-path null and wrong-target behavior otherwise matches.

This closes metadata leaves only. It does not claim `PageRequest`, `MapperParameter`, `RestJsonWrapperBean`, `ApplicationContextHelper`, DAO/service, dialect, or personnel page acceptance.
