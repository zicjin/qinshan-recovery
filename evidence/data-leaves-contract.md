# Data and annotation leaf contracts

The owned framework restores these supplier-facing classes from
`com.sunwayworld.framework.data`:

- `ObjectHolder`, with public no-argument/value constructors, mutable object
  storage, generic `get`, and null-aware `getOrDefault`;
- `ResultEntity`, including the supplier's intentionally misspelled
  `sucess` property, private constructor, `success()` and `fail(String)`
  factories, accessors, and exact serial version identifier;
- `Triple`, a serializable mutable three-value tuple with a private
  constructor, `of` factory, accessors, and exact serial version identifier;
- `TriConsumer<T,U,V>`, a `@FunctionalInterface` with the single `accept`
  method.

The restored annotations are `Blob`, `Clob`, `NotNull`, `ReadOnly`, and
`Timestamp` under `com.sunwayworld.framework.data.annotation`. All retain
runtime visibility and target fields and methods. `Blob`, `Clob`, and
`Timestamp` are documented; `ReadOnly` and `NotNull` are not. `NotNull`
retains the supplier's `defaultValue()` member with an empty-string default.

`scripts/tests/test-data-leaves.sh` extracts the pinned supplier
`BOOT-INF/lib/gikam-core-common-14.1.5.jar` and compares parentless supplier
and owned class loaders. The fixture performs 36 observations covering class
and method declarations, constructor and field visibility, holder defaults and
mutation, result factories/setters/null messages and serial streams, tuple
factory/getters/setters/private constructor and serial streams,
functional-interface metadata and invocation, and every annotation's
annotation/documented/retention/target/member metadata. It also transfers
`ResultEntity` and `Triple` through both directions of cross-loader Java
serialization. The run passes with no supplier or missing-dependency residue.

The reference SHA-256 is
`1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`, pinned
in `evidence/framework-contract/jars.json`. The test uses JDK 8u391, accesses
no production or external service, and has no dependency beyond the Java
runtime. The owned declaration gate reports 163 public contracts after these
nine classes are added; the supplier runtime rejection scan and framework
contract test pass.
