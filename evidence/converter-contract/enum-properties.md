# Enum and properties converter leaves

The owned framework now supplies `StringToEnumConverterFactory` (including its
returned converter contract) and `PropertiesToStringConverter`. These are
complete leaves; the broader conversion registry remains open.

`scripts/tests/test-enum-properties-converters.sh` extracts the hash-pinned common
JAR from the local reference artifact and compares 52 public observations in
separate supplier and owned classloaders. The owned loader has no supplier URL.
Enum observations cover trimming, case sensitivity, empty/null inputs, invalid
names and target types, bridge type failures, fresh converters, source/target
types and assignability. Properties observations cover escaping, Unicode,
defaults, empty/null inputs, non-string keys and wrapped I/O failures.

Properties output contains a wall-clock timestamp comment. The test validates
that comment's standard structure and compares the complete remaining output;
it does not require two consecutive conversions to occur in the same second.
The Java 8 declaration gate and supplier-residue rejection run as prerequisites.
