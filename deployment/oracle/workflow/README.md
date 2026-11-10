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

## Private target installation input

From the distribution root, with `JAVA_HOME` set to the pinned complete JDK 8:

```bash
./initialize-workflows.sh /private/runtime.properties < /private/workflows.json
```

The properties file contains the target datasource URL, user and password. The
launcher resolves this bundled manifest; no acquisition directory is required.
The JSON has exactly the following shape. Mapping entries are abbreviated here;
supply every symbol in `definitions.json`, using actual target IDs:

```json
{
  "expected_schema": "INSTALL_TARGET",
  "installer_user_id": "target-installer",
  "application_stopped": true,
  "redis_namespace_unused": true,
  "mapping": {
    "roles": {"ROLE_001": "target-numeric-role-id"},
    "users": {"USER_001": "target-user-id"},
    "organizations": {"ORG_001": "target-org-id"}
  }
}
```

Use an approved prepared schema with technical bootstrap and target identities.
Roles must be active with active members, direct users must be active with valid
organizations, and the installation author must be a valid active target user.
Mappings must cover exactly 80 role, nine user and two organization symbols;
different symbols within one category must have different target IDs. No
identity, membership or permission is created by this command. Names are read
from the target database and escaped as literal XML data.

Finish active verification, stop the application using the target schema and
use a dedicated Redis logical database with no previous workflow cache on the
next startup. Cache keys are not schema-qualified. A restart does not clear
Redis; never flush a shared database. The two boolean inputs acknowledge these
operator preconditions; they do not independently inspect processes or Redis.

The command requires all 12 definition/draft/runtime BPMN tables to be empty,
locks them with NOWAIT and holds read locks on referenced identity/service
tables. It writes only DIAGRAM, PROC and PROC_ORG, uses new local IDs and target
authorship, verifies CLOBs and relationships, then commits once. Failure rolls
back; repeat execution refuses existing workflow state. Existing installations
need a reviewed migration rather than this command. Output is always partial:
the seven missing source payloads, actual business authorization/audit/recovery
and complete new-device acceptance remain separate requirements.
