# sss2 runtime baseline

Date: 2026-10-20 (Asia/Singapore)

This is a read-only observation of the existing `sss2` deployment. No
production write, restart, deployment, or external integration call was made.

## Runtime

The host has one internal Docker network, `lims-replay-demo-s1-net`, with
`internal=true`. The following three containers were running at observation
time:

| Component | Container | State |
| --- | --- | --- |
| application | `lims-replay-demo-s1-app` | running |
| Redis | `lims-replay-demo-s1-redis` | running |
| Oracle | `lims-replay-demo-s1-oracle` | running |

The application container is reachable through the host's existing local
forwarder on port 18081. Read-only HTTP observations were:

- `/` returned HTTP 200 and an HTML application shell.
- `/secure/emc/module/mdm/emp/emps` returned the application's unauthorised
  response without a session.

## Provenance and boundary

The running application artifact has SHA-256
`c423fa1aa21e12226509bd1dde8bc036759af5e87f9d7b2a5173bec940a77329`.
The application image also contains `sunway-app.so` and the two read-only
license input mounts. This is therefore the existing supplier-backed behavior
baseline, not the final runtime required by
`.scratch/sunway-framework-rebuild/spec.md`.

The baseline is useful for comparing login, permissions, pages, queries, and
database behavior. The owned-runtime acceptance must use a separately built
artifact and must prove that those supplier inputs are absent from its final
runtime.
