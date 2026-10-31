# BeanPropertyDescriptor contract evidence

`BeanPropertyDescriptor` is implemented under the project-owned compatibility
framework after the owned `ReflectionUtils` and `ConvertUtils` seams closed.
The differential uses parentless supplier and owned class loaders and a shared
fixture with private and annotated properties. It covers `of(Class,String)` and
`of(Field)`, descriptor caching, metadata, annotation lookup, equality/hash
behavior, getter/setter invocation, conversion through the owned converter
registry, and missing-property/error behavior.

Validation:

```text
bash scripts/tests/test-bean-property-descriptor.sh
BeanPropertyDescriptor differential: PASS (public metadata, cache, read/write, conversion, annotation, errors)
```

This evidence closes the descriptor leaf only. The event/listener/interceptor
chain, real application bootstrap, and page-level behavior remain open.
