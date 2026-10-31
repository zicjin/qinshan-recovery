# Owned ObjectUtils contract evidence

`com.sunwayworld.framework.utils.ObjectUtils` now provides all seven observed
public operations: `equals`, `toShortString`, `isEmpty`, `clone`, `toString`,
`defaultIfNull` and `getStackTrace`. The implementation keeps the supplier's
null/empty equality, order-insensitive collection comparison, recursive map
comparison, serialization-first clone behavior, JSON short strings and
redaction markers for binary/stream values.

Run:

```bash
bash scripts/tests/test-object-utils-contract.sh
```

The differential test loads the supplier common JAR only through a separate
read-only classloader. It compares arrays, numbers, temporal values, maps,
collections, null/empty values, serializable and bean clone paths, JSON
redaction, defaults and deterministic stack traces. Current verification:

```text
owned declared public contracts: PASS (40 classes)
supplier runtime rejection scan passed
owned framework build and contract test passed
ObjectUtils differential: PASS (1299 observations)
```

This group does not claim CollectionUtils, NumberUtils or service/page
acceptance; callers that depend on those remain separate contract groups.

The reference and owned classloaders use separate directories: the owned side
receives only the pinned public dependencies and the newly compiled owned
classes. Additional cases verify floating precision, temporal truncation, SQL
time/LOB, binary and reader conversion including close/failure behavior, and
array/list cloning. Supplier code is never a fallback source for the owned loader.


Unsupported non-serializable clone fallback errors preserve the supplier exception boundary: bean property failures raise `BeanException`, constructor failures raise `ClassInstantiationException`, and unsupported top-level values remain `CloneException`. The differential fixture covers non-serializable object-array and list elements.
