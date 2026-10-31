# RestJsonWrapperBean state contract

The owned `RestJsonWrapperBean` now restores the supplier-observed request
state operations that are independent of mapper execution: JSON filter value
read/write, authority and query flags, aggregate field metadata, paging
extraction and disabling, extension filters, LCDP metadata, and `clearParam`.
The `TargetTableFilterData` value object and mapper-class selection seam are
also restored. The body envelope and body parsing contract remains covered by the existing
wrapper checks. Mapper extraction, business writes, and external service
resolution remain outside this slice.

The supplier reference was read only from the recorded run archive. The
reference `gikam-core-common-14.1.5.jar` has SHA-256
`1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`. The
owned implementation does not load that reference at runtime.

Validation on the integration working tree for this change:

- `./scripts/build-owned-framework.sh`
- 254 declared public contracts passed.
- `RestJsonWrapperBeanContractTest` passed 29 checks, including filter state,
  authority/query flags, paging extraction, extension filter immutability,
  LCDP metadata, and parameter clearing.
- Supplier runtime rejection scan passed.
- `git diff --check` passed.

These results cover the bounded wrapper state seam. They do not establish full
`RestJsonWrapperBean` mapper compatibility or full application behavior.
