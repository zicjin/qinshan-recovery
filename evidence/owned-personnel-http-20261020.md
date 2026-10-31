# Owned personnel page HTTP smoke

Date: 2026-10-20 (Asia/Singapore)

`scripts/tests/test-owned-personnel-http.sh` compiles the recovered
`GikamWpjcApplication`, the existing personnel page controller, and the
recovered personnel templates and scripts against the owned framework. It
starts the real application entrypoint with embedded Tomcat and no datasource,
Redis, security, or transaction-manager endpoint.

The smoke passed with the owned artifact and pinned public dependencies:

- the actual entrypoint published an active servlet context;
- exactly one `EmcEmpController` bean was discovered;
- all five personnel page routes returned HTTP 200 with rendered template
  output (`edit-list`, `detail`, `choose-list`, report-user choose, and sample
  choose);
- the original personnel edit-list JavaScript was served and retained its
  `/queries/raw` and `userOrg_filter` behavior;
- the owned artifact and the compiled application classes passed the supplier
  runtime rejection scan.

This is a page/controller/template acceptance slice only. It does not claim
login, authorization, request-wrapper parsing, resource/service/DAO execution,
Oracle/Redis wiring, or list/filter/detail data behavior. Those remain blocked
by the dependency frontier in `personnel-owned-slice-blocker.md`.
