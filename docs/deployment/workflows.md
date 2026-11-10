# One-time workflow configuration installation

The source-only distribution includes `initialize-workflows.sh` and
`installation/deployment/oracle/workflow/`. The finite package preserves 20
nonempty latest production process definitions, their codes and versions, and
seven explicit `latest-source-empty` blockers. It does not supply missing source
payloads or complete new-device restoration.

This command installs technical workflow configuration into an otherwise empty
workflow state. It starts no Spring application or HTTP server. Apply and verify
the structural/technical bootstrap and initialize the first administrator first.
Prepare the target roles, users, memberships and organizations through the
original authenticated management APIs or a separately reviewed identity
bootstrap. The workflow command creates none of them and grants no permissions.

Stop the application using this target schema after its active verification and
fixtures finish. Start it afterward with a dedicated Redis logical database that
has not held workflow caches. The original cache keys are not schema-qualified;
an application restart does not clear Redis. Never flush a shared logical DB.
An existing installation with workflow state needs a reviewed migration rather
than this first-installation command.

Run from any working directory with a complete JDK 8 and private inputs:

```bash
export JAVA_HOME=/opt/jdk1.8.0_391
"$LIMS_DEPLOY_DIST/initialize-workflows.sh" /private/runtime.properties \
  < /private/workflows.json
```

The launcher resolves the matching bundled manifest internally. The properties
file supplies the exact target datasource URL, user and password. The JSON has
exactly these fields:

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

This is an abbreviated shape, not a runnable mapping. Supply every symbol listed
in the bundled `definitions.json`: currently 80 roles, nine users and two
organizations. Missing or extra keys and merging different symbols onto one
target within a category are rejected. Repeated references to the same symbol
retain one target across every definition. The private source-symbol map is
acquisition material, not an installation dependency or a file to ship.

The installer verifies target identities in Oracle. Roles must be active and
have an active member; direct candidate users must be active and reference an
existing organization. The installation author must also be an active target
user with an existing organization. Display names come from the target database.
Candidate and notification bindings in XML and their corresponding SVG labels
are materialized as literal data, with XML escaping. Organization restrictions
are preserved; missing mappings cannot turn a restricted process into a global
process. Mapping validity does not establish equivalent job responsibilities or
permissions; those require actual role/organization HTTP acceptance.

Before writing, the command verifies the definitions hash and byte count,
strict schemas and counts, all mappings, XML/SVG syntax and allowed token
positions, and the enabled `audit_def` service/table relationships. It locks the
12 definition/draft/runtime BPMN tables with `NOWAIT` and requires them empty.
The independent preset-comment configuration is not reset. Target identity,
membership and service-definition tables are read-locked during installation.

Only `T_CORE_BPMN_DIAGRAM`, `T_CORE_BPMN_PROC` and `T_CORE_BPMN_PROC_ORG` receive
rows. IDs are allocated locally after the empty-state check; production database
IDs and authors are not copied. Inserts, exact relationships and CLOB readback
checks run in one transaction. Any failure rolls it back; a second run refuses
existing workflow state. The command changes no DDL, sequence, business entity,
task, instance or audit history. Error output contains fixed categories and
Oracle numeric codes, not SQL, credentials, mappings or XML.

`installed-partial` means the declared nonempty definitions were installed. It
still reports `deployment_ready=false` and `strict_evidence=false`; the seven
source-empty definitions remain blocked. After startup, verify original process
selection, code/version, service/table joins, XML/SVG, organization scope and
candidate sets through real HTTP, then exercise permitted and denied actors on
the actual business process. Do not count diagram reads, compilation or mapping
existence as workflow, audit, rollback or full API completion.
