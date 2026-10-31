# MapperParameter and wrapper filter contract

The owned compatibility layer now restores the database-independent
`MapperParameter` state seam: query flags, scalar values, standard filter
patterns, target/advanced/multi-property filters, authority flags, ordering,
role selectors, and projection metadata. `RestJsonWrapperBean.extractMapFilter`
now parses the observed `f`, `qf`, `af`, `mf`, `o`, and `r` request parameters,
applies extension filters, rejects unsafe filter identifiers, and supports a
caller-selected mapper parameter subtype.

Database-dialect revision, organization-service expansion, business writes,
and external service resolution remain explicit boundaries. The implementation
does not load the supplier runtime or license inputs.

Validation from the integration working tree:

- `./scripts/build-owned-framework.sh`
- 255 declared public contracts passed.
- `RestJsonWrapperBeanContractTest` passed 36 checks, including standard,
  advanced, multi-property, ordering, options, extension, and unsafe-input
  paths.
- `MapperParameterContractTest` passed 13 checks.
- `test-owned-framework.sh` passed.
- PageRequest differential passed 91 observations; PageRowBounds differential
  passed 58 observations; mapper value differential passed 39 observations.
- Supplier runtime rejection scan and `git diff --check` passed.

These checks close a bounded mapper/request parsing seam. They do not prove
full business query, write, database-dialect, or application equivalence.
