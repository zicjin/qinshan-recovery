# Date and JSR-310 converter families

The owned framework restores the dependency-closed date bridge leaves that use the restored `DateTimeUtils` and `ConvertUtils` seams without introducing a new dependency cycle:

- `DateToJSR310ConverterFactory`
- `LongToJSR310ConverterFactory`
- `JSR310ToDateGenericConverter` and its twelve `LocalDate`, `LocalDateTime`,
  and `LocalTime` targets
- `JSR310ToLongGenericConverter` and its three temporal source leaves

The implementations use only Java 8 date/time APIs, `java.util.Date`, JDBC
date types, and the owned converter exceptions. SQL `Date` normalization,
system-zone conversion, `LocalTime` SQL semantics, nulls, unsupported target
types, constructor failures, DST transitions, and extreme epoch values are
covered by the isolated supplier/owned differential test.

```text
scripts/tests/test-date-converters.sh
date converter differential: PASS (1566 observations)
owned declared public contracts: PASS
supplier runtime rejection scan passed
```

The test extracts the pinned `gikam-core-common-14.1.5.jar` by its recorded
SHA-256 and supplies the pinned public dependency directory to the supplier
loader. No production or external database access is used. String/date
formatting and parsing families remain separate because their supplier
implementation routes through `DateTimeUtils`.
