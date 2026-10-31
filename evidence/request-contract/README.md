# Request wrapper prerequisite graph

The hash-bound declarations and symbolic class references in
`dependency-report.json` come directly from the archived read-only classfile
inventory. `RestJsonWrapperBean` has 42 public/protected declarations,
`PageRequest` 26, and `MapperParameter` 40. This records candidate linkage and
public API requirements; constant-pool references do not prove actual execution.

The group is currently cyclic:

```text
PageRequest.of(RestJsonWrapperBean) -> RestJsonWrapperBean
RestJsonWrapperBean.extractPageRequest() -> PageRequest
RestJsonWrapperBean.extractMapFilter() -> MapperParameter
MapperParameter(RestJsonWrapperBean) -> RestJsonWrapperBean
```

The cycle's external missing dependencies include context/principal access,
generic DAO/service behavior, entity column/table metadata, dialect/SQL helpers,
bean conversion/property listeners, and nested request/filter/LCDP types.
`MapperParameter` also directly references organisation service and process
status types. Implementing empty methods for these contracts would falsely
claim a complete wrapper. No partial wrapper, page-request or mapper-parameter
class is added by this evidence commit.

The next coherent candidates are the missing leaf value/exception/conversion
contracts, then a dependency-closed request parsing and filter group with
differential JSON/error/authority/SQL behavior. Actual resource acceptance still
requires the unchanged business resource/service chain and isolated database.

## Current personnel owned-slice frontier

A fresh source-tree audit after the utility leaves confirms the cycle remains open:

- `PageRequest` has only four symbolic dependencies in its supplier bytecode: `RestJsonWrapperBean`, `ClassUtils`, `NumberUtils`, and `StringUtils`. The first is absent and NumberUtils remains blocked by its rounding subsystem.
- `MapperParameter` references `BeanPropertyDescriptor`/events/listeners, context and local-principal helpers, `Pair`, entity metadata, dialect repository, nested wrapper data, array/collection/bean/conversion utilities, organisation service, user bean, and process status. These are not a dependency-closed leaf set.
- `RestJsonWrapperBean` closes the cycle and additionally requires SQL helpers, JSON deserialization, generic DAO/service contracts, nested filter/LCDP classes, and NumberUtils.
- Supplier `ApplicationContextHelper` exposes native/license methods plus broad bean/service/sequence helpers. The owned context bridge deliberately does not provide this public compatibility facade; adding fixed license values or a mock would violate the private-framework boundary.

Therefore personnel source compilation can proceed only for isolated DTO/value slices; the request/page/mapper runtime remains blocked pending a complete context, request, metadata, and domain-contract phase.
