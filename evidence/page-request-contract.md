# PageRequest value compatibility

The owned `PageRequest` now implements its public value, factory, navigation,
filter-map, sorting, and aggregate-metadata methods. `RestJsonWrapperBean`
provides only the pure `tf`, `cp`, and `op` metadata getters needed by the
wrapper factory. Context, security, mapper extraction, and DAO methods remain
explicit unsupported boundaries.

The supplier reference is extracted read-only from the recorded run archive.
The runner verifies `gikam-core-common-14.1.5.jar` against the framework
inventory SHA-256 `1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`
and compares independent parentless supplier/owned class loaders. The owned
runtime does not load that reference.

Validation on integration source `239d4e6` passed:

- `test-page-request-contract.sh`: direct public checks plus 91 supplier
  observations covering defaults, zero/negative paging, overflow, navigation,
  metadata aliasing, filter JSON outcomes, and wrapper numeric inputs.
- `test-page-row-bounds-contract.sh`: 58 observations.
- `test-numeric-converters.sh`: 2,828 observations.
- `test-lob-converters.sh`: 76 observations.
- The 252-class declaration gate, framework contract test, and supplier runtime
  rejection scan passed during these sequential builds.

The review's converter concerns were checked against the supplier before
changing behavior. Empty primitive byte conversion raises `ClassCastException`,
BigDecimal/BigInteger use the historical double/long conversion paths, and
Clob failure metadata names Blob/byte-array types. Those observed quirks remain
for compatibility; `ReviewedValueContractTest` records their public outcomes
and JDBC cause retention. Navigation likewise copies only size/filter, and
offset retains the observed integer multiplication behavior.

Repeat sequentially from the recovery root:

```bash
bash scripts/tests/test-page-request-contract.sh
bash scripts/tests/test-page-row-bounds-contract.sh
bash scripts/tests/test-numeric-converters.sh
bash scripts/tests/test-lob-converters.sh
```

These are bounded value-contract results. They do not establish full request
wrapper, mapper, application, page, or write compatibility.
