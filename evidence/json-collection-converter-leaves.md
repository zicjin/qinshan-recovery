# JSON and collection converter leaf contracts

Implemented the four observed public converter leaves:

- `CollectionToStringConverter`
- `MapToStringConverter`
- `MapToJSONObjectConverter`
- `JSONObjectToMapConverter`

Each enum preserves `INSTANCE`, source/target descriptors and bridge
`convert(Object)` methods. Implementations use the pinned Fastjson dependency
and do not add conversion defaults or supplier runtime classes.

Run:

```bash
bash scripts/tests/test-json-collection-converters.sh
```

The test loads supplier and owned implementations through separate classloaders;
the owned loader receives only pinned public dependencies and owned classes. It
covers nulls, aliases, insertion ordering, case-sensitive keys, nested values,
iterables, maps, invalid JSON, arrays, type descriptors, `canConvert` matrices,
and map/JSON mutation identity. Current result:

```text
owned declared public contracts: PASS (56 classes)
supplier runtime rejection scan passed
owned framework build and contract test passed
JSON/collection converter differential: PASS (363 observations)
```

String-to-map/JSON, numeric grouping and array converter families remain
separate contract groups.
