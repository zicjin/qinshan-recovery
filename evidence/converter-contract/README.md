# ConvertUtils implementation boundary

`ConvertUtils` exposes only three methods, but its supplier static initializer
registers a broad converter graph: identity, number/boolean/string, date and
JSR-310, arrays, collections/maps/JSON, readers/streams, SQL LOBs, Oracle datum,
properties, enums and input streams. The public methods delegate through this
registry for both `convert` and `canConvert`; loading the supplier facade in an
isolated classloader without the JDBC/Oracle classes fails during registry
resolution even for ordinary numeric requests.

The owned framework now provides the dependency-closed registry rather than a scalar-only facade. The registry keeps supplier-supported SQL, Oracle, JSON, reader, date, and factory conversions in one public path.

Known source contract anchors:

- `Convertable`, `Converter`, and `ConverterFactory` declarations are recorded
  in `evidence/framework-contract/classes.jsonl`.
- `ConverterNotFoundException` and `ConvertFailedException` are already owned
  leaves with observed constructor messages.
- Numeric and boolean converter bytecode was inspected read-only; supplier
  behavior includes null/default differences, positive-number boolean rules,
  accepted boolean spellings, and target-specific numeric narrowing.

Closed leaf groups now have independent supplier/owned evidence for numeric and
boolean conversion, primitive/wrapper arrays, JSON and collections, enum and
properties conversion, readers and input streams, JDBC LOBs, Oracle datum leaves,
and the JDK date and JSR-310 factories/formatters. `test-convert-utils.sh` compares
the complete facade through isolated supplier and owned loaders: 8784 observations
in each of UTC, Asia/Shanghai, and America/New_York.

`DateTimeUtils.addDay/getDurationInMilliseconds` and request parsing remain
separate utility seams; `ArrayUtils` and `CollectionUtils` now have their own
48-observation differential at `test-array-collection-utils.sh`.
