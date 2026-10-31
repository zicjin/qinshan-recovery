# JsonUtils contract

`JsonUtils` preserves the common framework JSON facade for serialization,
simple values, object/list conversion, nested values, and JSON shape checks.
The owned implementation uses the already-owned property descriptor and
conversion seams, including extension fields on `BaseData`; it does not load
the supplier application context or native code.

`scripts/tests/test-json-utils.sh` compares isolated supplier and owned class
loaders for eight observations covering strings, simple values, object/list
shape, invalid input, lists, and a bean with scalar properties.
