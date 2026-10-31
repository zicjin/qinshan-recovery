# Converter leaf implementations

This slice implements five exact supplier registry leaves with their observed
public enum/class shapes and Java 8 behavior:

- `IdentityConverter`: returns the source unchanged.
- `StringToBooleanConverter`: accepts true/yes/on/1 and false/no/off/0,
  case-insensitively; null yields false; other input raises
  `ConvertFailedException`.
- `BooleanToStringConverter`: null remains null, otherwise `Boolean.toString()`.
- `ReaderToStringConverter`: reads lines, appends a newline after each line,
  closes the buffered reader, and wraps I/O failures.
- `StringToPropertiesConverter`: UTF-8 loads Java `Properties` and wraps I/O
  failures.

`scripts/tests/test-converter-leaves.sh` runs 21 supplier/owned observations
through separate classloaders and then the full owned public declaration gate.
The supplier common JAR is read-only reference input. Remaining converter
families (numeric/date/JSR-310/arrays/SQL/Oracle/JSON/etc.) are not implied by
this slice and remain separate implementation work.
