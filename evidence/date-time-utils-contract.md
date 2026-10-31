# DateTimeUtils supplier contract

The owned framework now supplies the complete public
`com.sunwayworld.framework.utils.DateTimeUtils` class. The implementation is
kept in the supplier package because application classes reference this
surface directly; formatter and parser state remains project-owned under
`com.sunwayworld.owned.internal.converter.DateTimeState`.

The restored method families are:

- serial numbers for `LocalDate` and `LocalDateTime`;
- `Date`, `LocalDate`, `LocalDateTime`, and `LocalTime` formatting, including
  pattern overloads;
- parsing to `Date`, `LocalDate`, `LocalDateTime`, and `LocalTime`;
- temporal duration and fixed-millisecond `addDay` operations;
- year, month, week, day, hour, and minute plus operations;
- date/date-time comparisons and `max`/`min`;
- `isLocalDate` and `isLocalDateTime` validation helpers.

The null and error boundaries are part of the contract. Serial methods retain
the supplier's direct dereference behavior. Pattern overloads use the default
pattern for a null or empty pattern. Plus operations return null when either
input is null; before/after return false for nulls; equality returns true only
for two nulls or equal values; and min/max return the non-null argument when
the other side is null. Duration converts both temporals through the owned
`ConvertUtils` registry and subtracts the first converted value from the
second. `addDay` applies `86400000L * days` and converts back to the original
runtime class.

`scripts/tests/test-date-time-utils.sh` extracts the supplier reference from
the pinned application artifact and compares parentless supplier and owned
class loaders. The fixture records exactly 204 observations per run. Runs in
UTC, `Asia/Shanghai`, and `America/New_York` therefore cover 612 differential
observations, all passing. The matrix includes null and malformed inputs,
separator and fraction normalization, serial forms, DST-sensitive `Date`
formatting, unsupported temporal conversions, plus operations, comparison
nulls, and validation results.

The reference dependency is
`BOOT-INF/lib/gikam-core-common-14.1.5.jar`, SHA-256
`1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`, as
recorded in `evidence/framework-contract/jars.json`. The extracted Oracle
JDBC dependency is pinned at SHA-256
`a66d27a14f3adee484427cc4de008af85a5c3e78e2e3285a4dba1277332978a5`.
The checks run with JDK 8u391 and do not access production or external
services. The owned build gate also reports 152 declared public contracts,
passes the supplier runtime rejection scan, and passes the framework contract
test.

The dependency closure is intentionally narrow: `DateTimeUtils` delegates
shared formatting/parsing behavior to `DateTimeState` and temporal conversion
and duration behavior to the owned `ConvertUtils` registry. No supplier class
is placed on the owned runtime classpath. Oracle datum behavior that requires a
native/application connection context remains outside this utility fixture.
