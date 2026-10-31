# String/date converter leaves

The owned framework restores four supplier-facing seams alongside the restored public `DateTimeUtils` and `ConvertUtils` facades:

- `StringToDateConverterFactory`
- `StringToJSR310ConverterFactory`
- `DateToStringConverterFactory`
- `JSR310ToStringGenericConverter`

The implementation uses Java 8 date APIs internally. Formatter state lives in the project-owned `com.sunwayworld.owned.internal.converter.DateTimeState`; this preserves supplier ThreadLocal formatter behavior while sharing the restored public utility seams. Supplier behavior is read from the pinned `gikam-core-common-14.1.5.jar` (`1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`) in a separate reference classloader. The `StringToDateConverterFactory` boxed-`Long` constructor lookup is preserved: ordinary `Date` and JDBC date classes have no public `Long` constructor and therefore report the supplier's converter-not-found result for nonempty input, while a fixture date with that constructor converts successfully.

`test-string-date-converters.sh` compares 649 observations per run for UTC, Asia/Shanghai, and America/New_York using parent-null supplier and owned URLClassLoaders. It also changes the default timezone after formatter initialization and compares the retained formatter result. The cases cover null/empty/whitespace, normalized date separators and offsets, dates and datetimes, 1/3/9/10-digit fractions, serial forms, malformed and lenient calendar values, DST gap/overlap inputs, null targets, unsupported targets, JDBC date/time formatting, JSR-310 formatting, and recursive conversion failure causes. All timezone and lifecycle runs passed, with no supplier classes on the owned runtime classpath.

The expanded matrix exposed a supplier error-boundary difference: oversized fractions in LocalDate/LocalDateTime end in `ConvertFailedException`, while LocalTime preserves `IllegalArgumentException` from the formatter pattern width. The owned implementation preserves those results. The lifecycle regression first failed at observation 161: after initialization in UTC and switching defaults to Honolulu, supplier epoch output stayed `1970-01-01 00:00:00`; a fresh-per-call owned formatter gave `1969-12-31 14:00:00`. Shared thread-local state fixes this difference. A separate per-thread test now verifies existing threads retain their primed defaults while fresh threads use mutated defaults, across the same three zones.

`test-owned-public-contract-boundary.py` checks temporary artifact copies. A helper under the exact `com/sunwayworld/owned/internal/` prefix is accepted; an extra class under `com/sunwayworld/framework/` and a lookalike `owned/internals/` prefix are both rejected. Supplier residue scanning still covers the complete artifact.
