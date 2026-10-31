# StringUtils dependency closure blocker (resolved)

The earlier blocker covered two unresolved edges: `read(InputStream)` used a
heuristic instead of the supplier frequency table, and
`encodeChineseFileName` depended on the servlet request/context graph. Both are
now closed. `EncodingUtils` preserves the supplier detector, and the owned
`OwnedRequestContext` bridge supplies the request seam without restoring the
broader `ServletUtils` or `LocalContextHelper` facade.

The completed contract is recorded in
`lims-recovery/evidence/string-utils-contract.md`; its parentless differential
now passes 2,734 observations, including no-request/request-error behavior,
all user-agent branches, UTF-16 input, stream errors and request-scope cleanup.
Production remains read-only and no supplier fallback or classpath masking is
used.
