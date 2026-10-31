# Owned mapper filter contract evidence

The owned framework now contains the observed public contracts:

- `MatchPattern`: all 48 enum constants, source order, conversion type and
  `getPatternIfPossible` lookup.
- `FilterParamPattern`: both constructors, mutable map/fields, case-insensitive
  row lookup and observed operator behavior.
- `SearchFilter`: public filter state, unmodifiable live getters, fluent
  `Matcher.filter` and `Matcher.special` nested contract.

The differential test loads the supplier common JAR only in a separate
read-only classloader. It never places the supplier class on the owned compile
or runtime classpath. The test covers string, numeric, date, datetime, null,
list and nested fluent state observations, including the supplier's historical
operator differences (`DEQ`/`TEQ` inequality and unsupported operators returning
false).

Run from `lims-recovery/`:

```bash
bash scripts/tests/test-filter-contract.sh
```

Observed verification on Java 8u391:

```text
owned declared public contracts: PASS (31 classes)
supplier runtime rejection scan passed
owned framework build and contract test passed
search filter differential: PASS (3739 observations)
```

This is a framework contract gate and does not close ticket 09. Personnel page,
Oracle query execution, current-user organization filters and real page
behavior remain required before personnel acceptance.

Additional observed conversion cases include collection/map JSON strings,
`Date`/SQL date/timestamp and `LocalTime` inputs, slash/unpadded date text,
ignored numeric timezone suffixes, fractional seconds and SMART date rollover.
`Oracle Datum`, SQL LOB and other untested conversion sources are not claimed
by this contract evidence.
