# Source-bound workflow definitions: partial symbolic installation input

This package contains 20 nonempty latest published workflow definitions and seven
`latest-source-empty` blocked entries from the same source scope. It does not
install a database or complete workflow/application readiness. `manifest.json`
remains `status=partial` and `deployment_ready=false`.

`definitions.json` uses `owned-workflow-definitions-v1`. Accepted definitions keep
the original process code/version and technical audit service/table references,
plus XML/SVG with typed role/user identity placeholders. The package declares
80 role, nine user and two organization symbols. Production identity IDs and
identity display names are omitted from binding fields; no accounts, roles,
organizations, memberships, grants, process instances or history are included.
Private target mapping and identity creation remain separate operator inputs.

Candidate/notified XML properties use `__LIMS_ROLE_001_ID__` and
`__LIMS_ROLE_001_NAME__` (USER/ORG follow the same convention). SVG display slots
are replaced only through exact task-owned candidate references. Three source
start-event titles collide with identity labels elsewhere; their original
node/`_label` relation proves they are technical display text, so they are
preserved. Numeric geometry, BPMN technical references and source status codes
are never globally replaced. All other unresolved identity labels block the
corresponding definition without publishing its payload.

The manifest pins `definitions.json`, source artifacts, original XML/SVG hashes
and transformation counts. Private acquisition files are provenance inputs to
the offline renderer only; they are not target installation dependencies.
The separate private source-symbol map is never shipped. Target installation
must provide every declared role/user/organization symbol, preserve restricted
organization scope and validate target identity existence/memberships before
writes. Unmapped scopes cannot silently become global. Seven empty latest source
payloads remain blocked; older versions and placeholders are not substitutes.

Only accepted definitions may enter a separately validated finite installer.
Its required remaining checks include explicit technical authorship, empty
workflow runtime state, exact content verification and safe failure behavior.
Real HTTP process selection/version/diagram, candidates, organization scope,
notifications, business lifecycle/audit/rollback and cleanup remain separate
acceptance. This package is not included in the guarded combined Oracle
installation plan and does not change its readiness status.
