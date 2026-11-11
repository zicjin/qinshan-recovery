# Original numbering configuration

The finite core bootstrap carries 45 current-production numbering definitions
from `T_CORE_SEQUENCE` and two original segment definitions from
`T_CORE_SEQUENCE_LINE`. Original IDs, NULL names, patterns, segment codes and
ordering remain unchanged. Source rule IDs can be logical aliases or dated
rules rather than table names; the installer preserves them without creating
new tables or guessing missing rules.

The source projection excludes mutable `JSONPARAM` allocation state and all
created-by, organization and timestamp fields. The generated INSERT explicitly
sets `JSONPARAM` to NULL as an **empty-installation transformation**. Source row
hashes bind only the original fields; statement hashes bind this transformation
as well. This is not a migration or a counter-reset procedure for an existing
database.

Use the existing combined Oracle installation order. `70-bootstrap.sql` checks
that all selected configuration tables are empty before writing any row and
commits once. `80-bootstrap-verify.sql` checks every selected field, including
NULL counter state, before identities are initialized or the application starts.
After the application allocates a number, counter state legitimately changes;
do not reseed the configuration to make an installation signature match again.
The normal full-system installer still rejects the partial package.

The original `CoreSequenceAbstractService` creates a default pattern when a
requested rule is absent. A successful HTTP create can therefore produce a
different number on an incompletely initialized device. Restoring captured
definitions prevents that drift for these selected rules. The captured project
change rule is `[yyyy][mm][dd][nnn]`, and the return-delivery rule
`T_EMC_REPORT_SEND_INFO_HQ` uses the same date and width pattern. Other rules
retain their independently captured patterns. Historical release rules missing
from current production are not added.

`sunway.sequence.continuous` selects the original service's transaction mode.
Its default is false; allocations then run in `REQUIRES_NEW` transactions.
Deleting or compensating a business record does not rewind an allocated number.
Do not decrement counters during fixture cleanup or call API compensation a
transaction rollback. For a new installation with separately imported business
rows, collision checks and an approved allocation-state initialization are
additional prerequisites; NULL counters are valid only for the empty business
target covered here.

Real application acceptance must inspect the number returned/read back through
the original business API, its date/key/width, and continued allocation after
deletion. Configuration hashes, a list response or a single successful create
do not establish all numbering families, concurrency, rollback or strict API
acceptance.
