# Mapper value contracts

`AdvancedFilterPattern`, `MultiPropertyFilterPattern`, `Order`, `Pair`, and
`TypeValuePair` are now
owned classes with supplier differential coverage. The test covers constructor
defaults, mutable fields, array aliasing, case conversion, null behavior and
the exact direction strings (`" ASC"`/`" DESC"`).

Run:

```bash
bash scripts/tests/test-mapper-value-contract.sh
```

The converter interfaces `Convertable`, `Converter`, and `ConverterFactory` are
also declared for the next conversion group, but no incomplete registry is
claimed. This closes only value objects. `RestJsonWrapperBean`, `PageRequest`,
`MapperParameter`, conversion registries, DAO/service contracts and real
authorization remain open.

Java 8 differential result: 26 observations passed; declared public gate: 38 classes.
The advanced and multi-property objects also drive the unchanged personnel
Mapper XML in the eight-case Oracle observation (`personnel-mapper-reference.md`).
