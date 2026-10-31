# Owned personnel browser evidence

Environment: disposable synthetic Oracle and loopback Spring Boot application from integration `d6b2f02`; production access was not used. The browser ran in a named `agent-browser` session and was closed after the run.

The browser opened the packaged `/` shell, fetched the guarded original `page/edit-list?readonly=1` fragment, and executed the unchanged recovered `emc-emp-edit-list.js` configuration through the bounded owned Gikam facade. RSA PKCS#1 v1.5 login created a session for the synthetic permitted user.

Observed checks:

- list: 23 synthetic rows rendered with the original columns and server response values;
- detail: selecting `e-2` rendered `Alicia`, age `40`, department `Fixture Org B`, and the fixture remark;
- filter: `nobody` rendered `暂无数据`, then `Alice` rendered one row;
- pagination: clearing the filter rendered 23 rows, then page 2 rendered `page-003` through `page-001`;
- unsupported tabs and write controls stayed visibly unavailable or disabled;
- logout returned to the login form and cleared the workspace;
- the low-permission synthetic account was rejected with `当前账号无人员查询权限`.

The disposable Oracle/query service run passed 15 checks and cleaned its internal network, container, and anonymous volumes. The application lifecycle launcher and browser transcript are test-only; no credentials or production data are retained.

Reproducible integration rerun (2026-10-20): the committed runner at source
`ae5a73e18a2b5806a4a39cfc8f187f36df10d12a` completed 22 checks with
`source_dirty=false`. The machine-readable report is
`.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-browser-integration-20261020.json`; it includes source and
artifact hashes, response-only browser transcript, isolation inspection and
confirmed container/anonymous-volume/network removal. This validates the bounded
personnel read flow; personnel writes and auxiliary flows remain open.
