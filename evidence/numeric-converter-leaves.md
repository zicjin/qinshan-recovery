# Numeric converter leaf contracts

Implemented the observed numeric converter leaves and factories:

- `NumberToStringConverter`
- `NumberToBooleanConverter`
- `NumberToNumberConverterFactory`
- `StringToNumberConverterFactory`
- `BooleanToNumberConverterFactory`

The owned implementation keeps all conversion behavior private to these
converter leaves and does not introduce a partial `NumberUtils`. The
differential test uses separate supplier and owned dependency directories, so
the owned loader cannot fall back to supplier classes.

```text
owned declared public contracts: PASS
supplier runtime rejection scan passed
numeric converters differential: PASS (2784 observations)
```

The matrix covers null/positive/negative/fractional values, overflow,
scientific and hexadecimal text, commas, invalid text, NaN/infinities,
BigInteger/BigDecimal and unsupported target classes. Primitive targets (including observed null/empty outcomes) and the factory
`canConvert` matrix are included. The broader `NumberUtils` public utility remains
outside this leaf group.


The matrix intentionally excludes custom `Number` subclasses from `NumberToStringConverter`: the supplier delegates unsupported subclasses to `NumberUtils`, whose error path consults the supplier I18n context. That context is outside this owned leaf contract; no supplier-context exception is fabricated.
