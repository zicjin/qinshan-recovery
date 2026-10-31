# CaseInsensitiveLinkedMap contract

`com.sunwayworld.framework.data.CaseInsensitiveLinkedMap` is dependency-closed through `LinkedHashMap`, Java 8 functional interfaces, and the owned `CloneException`/`ObjectUtils` seams. The owned implementation matches the pinned supplier's public constructors and mutators. Supplier serial UID `9078487774509185313` is retained. The supplier's three-argument constructor ignores the `accessOrder` flag and delegates only `(initialCapacity, loadFactor)`; the owned implementation preserves that behavior.

`scripts/tests/test-case-insensitive-linked-map.sh` loads supplier and owned classes through separate parent-null URL classloaders and compares constructors, lower-case alias lookup, `getOrDefault`, Java 8 `putIfAbsent`/`replace`/`compute`/`merge`, null keys, copy/putAll, null map arguments, access-order constructor behavior, serialization, callback behavior, and default-locale changes including Turkish. Result:

```text
case-insensitive linked map differential: PASS (20 observations, public methods, two-way serialization)
```

The serialization check writes each implementation's map and deserializes it through the other parent-null loader, checks the destination classloader, and verifies order, null keys, nested list contents, and mixed-case lookup. The map constructor and `putAll` also compare nonserializable-value clone failures. Public bridge methods are included in the declared-method comparison.

The custom project-bean map fallback remains outside this test's closure: the supplier fallback loads additional private dependencies not present in the pinned public-only test classpath. No behavior claim is made for that branch. No supplier classes or production materials are used by the owned path.
