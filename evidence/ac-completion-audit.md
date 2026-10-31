# AC Completion Audit

Date: 2026-09-30 (Asia/Singapore; deterministic build/replay supplement added after the initial audit).

This is a current-state audit, not a claim that the recovery is complete. It
separates verified results from tooling that is ready but has not received its
required production-read-only inputs.

| Acceptance condition | Current evidence | Status |
| --- | --- | --- |
| AC-01 material/version baseline | Material verification and the latest source inventory are recorded in `evidence/t02-build.md`/`t02-source-inventory.md`; deterministic clean build and final replay are recorded in `t07-deterministic-build-r0930deterministic.md`. | Verified for the recovered build baseline; not a publishable clean handoff package. |
| AC-02 build | Main and clean source trees independently run `build-prep.sh`/`build.sh`; deterministic normalized JAR is `c423fa1a...`, and source/class inventories match. | Verified. |
| AC-03 isolated start | `r0930final3` verifies the latest launcher; `r0930deterministic` additionally runs the deterministic clean-built JAR `c423fa1a...` with exact IDs, snapshot marker, internal RFC1918 network, and isolation PASS. | Verified for the clean-tree replay; full handoff remains open. |
| AC-04 data and attachments | One personnel-signature sample is copied, hash-bound, database-linked, and served from an isolated volume. No full `T_CORE_FILE`, equipment-acquisition metadata, production file tree, or DB-to-files reconciliation exists. | Open. |
| AC-05 login and permissions | `r0930final3` records authorized login plus machine-readable low-privilege 403 responses for personnel, equipment, consumables pages and raw query paths; attachment authorization is also recorded. | Verified for the recorded clean replay scope; full business authorization matrix remains out of scope. |
| AC-06 personnel query | `r0930final3` records 323-row list, 50/page pagination change, matching and no-match filters, selected detail, and low-privilege rejection; restricted DOM/screenshot summary is in the final3 diagnostics receipt. | Verified for the latest clean replay sample. |
| AC-07 equipment query | `r0930final3` records 72-row list, 100/page behavior (one applicable page), matching and no-match filters, selected detail fields, and low-privilege rejection. | Verified for the latest clean replay sample. |
| AC-08 consumables query | `r0930final3` records 206-row list, AC-09 header, matching and no-match filters, selected detail/stock fields, empty inventory/history state, and low-privilege rejection. | Verified for the latest clean replay sample. |
| AC-09 second development | Content-equivalent `r0930final3` replay and independently deterministic clean-built `c423fa1a...` replay both show `材料名称（构建验证）`; details are in `t07-current-r0930final3.md` and `t07-deterministic-build-r0930deterministic.md`. | Verified. |
| AC-10 handoff | `r0930final3` proves a clean source-tree rebuild/replay with pinned inputs and final launcher guards. No another-developer/host rebuild, credential/license rotation, approved history cleanup, complete data/attachments, or distributable clean package exists. | Open. |

## Required External Inputs

- A DBA-provisioned dedicated SELECT-only production account and an approved
  SCN or explicit consistency-window reference for
  `scripts/t06-attachment-inventory.sh`.
- The fixed inventory helper has a local fake-JDBC integration test; this is
  tool proof only and does not satisfy the production inventory requirement.
- A storage-approved snapshot/window for
  `scripts/t06-filesystem-inventory.sh`; its output is metadata only and does
  not copy or hash attachment contents.
- An approved full database/file reconciliation run, including distinct
  treatment for soft-delete (`X`-prefixed) and unknown target identifiers.
  `tools/t06-reconcile/reconcile.py` now provides the local-only reconciliation
  step once both restricted inventories exist; it intentionally reports
  unresolved acquisition mappings rather than guessing paths.
- Credential/license rotation and owner-authorized Git-history cleanup before
  creating a distributable source package.
- Another developer or host must repeat the clean-source replay and handoff
  checks; the recorded `r0930final3` run is on this work machine/daemon.

## Current Local Constraint

Docker access is currently available and the latest image-pin, source-identity,
subnet, properties, snapshot-marker, isolation, browser, and attachment checks
were executed in `r0930final3`. Earlier `r06c2final4`/`r0930latest` evidence
remains historical and is not mixed with the final manifest. Remaining blockers
are full production-read-only data/file inventory, credential/license rotation,
authorized Git-history cleanup, a distributable handoff package, and another
developer/host replay.
