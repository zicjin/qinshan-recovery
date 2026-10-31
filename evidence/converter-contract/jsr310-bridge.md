# JSR-310 temporal bridge

`JSR310ToJSR310ConverterFactory` is restored as a complete owned factory. Its
supplier implementation first converts a `Temporal` to primitive `long` epoch
milliseconds through the converter registry, then converts the boxed `Long` to
the requested target. The owned implementation composes the already verified
JSR-310/epoch leaves and preserves the observed precision loss, system-zone
handling, null behavior, unsupported source and target exceptions, primitive
target messages, and raw target-class bridge casts.

```text
scripts/tests/test-jsr310-bridge.sh
JSR310 bridge differential: PASS (993 observations)  # UTC
JSR310 bridge differential: PASS (993 observations)  # Asia/Shanghai
JSR310 bridge differential: PASS (993 observations)  # America/New_York
owned declared public contracts: PASS (135 classes)
supplier runtime rejection scan passed
```

The supplier classloader uses the pinned common-framework hash and the pinned
`ojdbc8-19.3.0.0.jar` hash because supplier `ConvertUtils` initializes Oracle
datum converter entries. The owned loader has no supplier framework fallback.

Oracle datum conversion remains outside this slice. `DATE` and `TIMESTAMP` can
read a datum directly, while `TIMESTAMPTZ` and `TIMESTAMPLTZ` call
`MybatisHelper.getConnection()`. That method obtains a Spring `DataSource`
through the native `ApplicationContextHelper`; reproducing it requires the
owned application context, connection scope, and Oracle datum behavior. The
work therefore records the dependency boundary instead of adding a fake
connection context or a partial `MybatisHelper` facade.
