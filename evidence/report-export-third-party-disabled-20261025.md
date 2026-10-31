# Report third-party export boundary (diagnostic)

The owned report browser replay already executes the local synthetic HTML export and asserts that the PDF path returns HTTP 501 (`REPORT_EXPORT_DISABLED`). The controller source deliberately accepts only `format=html`; PDF, Excel and other renderer paths are outside the owned adapter.

This record is diagnostic boundary evidence, not third-party renderer equivalence. PDF is runtime-observed as disabled; Excel/PDF renderer parity, templates, editing and third-party authorization remain `unknown`/open. No new matrix contract is registered from this record.
