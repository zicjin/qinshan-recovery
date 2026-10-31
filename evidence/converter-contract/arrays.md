# Primitive/wrapper array converter family

The owned implementation restores both supplier interfaces and all sixteen
public singleton enum leaves (eight primitive-to-wrapper and eight reverse
converters). Conversion uses a fresh array and preserves element order and
values. The reverse supplier contract has a recorded quirk: its public target
generic is primitive-array-shaped, but its implementation allocates an array
using the source wrapper component, so the runtime result remains a wrapper
array. Null input and empty arrays are preserved; incompatible inputs and
wrapper nulls retain Java assignment/error behavior.

`scripts/tests/test-array-converter-contract.sh` runs the public conversion,
source/target type, assignability, fresh-array, input-preservation, wrong-input
and enum-singleton observations in isolated supplier and owned classloaders,
then executes the full declaration gate and supplier-residue scan. The current
test passes with Java 8. Other converter families remain independent.
