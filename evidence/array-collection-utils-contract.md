# Array and collection utility contract

`ArrayUtils` and `CollectionUtils` are now owned compatibility facades under
their supplier-facing packages. They cover array type and slicing helpers,
concatenation, unique-type selection, case-insensitive collection/map access,
conversion through the owned `ConvertUtils` registry, set-like operations,
key-case mutation, blank-value insertion, ordering, and empty containers.

`scripts/tests/test-array-collection-utils.sh` runs the public methods through
parentless supplier and owned class loaders with the pinned public dependencies.
The current fixture compares 48 observations and rejects missing dependency
errors before accepting equality. The declaration gate reports 152 selected
supplier-compatible classes and the supplier-runtime scan remains clean.

This evidence covers utility behavior only. Reflection, `BeanPropertyDescriptor`,
`NumberUtils`, request parsing, and application data-access context remain
separate seams with unresolved dependency graphs; `DateTimeUtils` is now restored.
