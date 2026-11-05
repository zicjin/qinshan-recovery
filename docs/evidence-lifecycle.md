# Evidence lifecycle

`lims-recovery/evidence/` is the canonical publication area. It contains the
machine-readable registry and only reports that are explicitly classified as
`canonical-strict` or `canonical-diagnostic`. Runner output belongs in
`.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/` until it has
passed the appropriate contract.

The registry is generated and checked with:

```bash
python3 scripts/manage-evidence.py scan
python3 scripts/manage-evidence.py plan --output .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/evidence-migration-plan.tsv
python3 scripts/manage-evidence.py classify
python3 scripts/manage-evidence.py verify
bash scripts/tests/test-evidence-lifecycle.sh
bash scripts/tests/test-api-evidence-gate.sh
```

The registry is authoritative for JSON in the publication root. A root JSON
must have class `canonical-strict` or `canonical-diagnostic`; an unregistered
or diagnostic root file is a gate failure. The registry also records
`referenced_by` and `generated_at`, so retention and supersession decisions do
not depend on filename conventions.

Published evidence must bind the current implementation tree digest and
`implementation_dirty=false`. `repository_head_at_capture` and the legacy
`source_commit` field are provenance only: a documentation, ticket, registry or
archive commit may advance repository HEAD without invalidating behavior
evidence when `implementation_tree_digest` is unchanged. A changed implementation
scope, nested project commit, runtime/runner contract or distribution input
changes the binding and requires affected replay. `source_scope` remains an
affected-runner selection hint, never a publication exception. Neither
`source_commit_exception` nor a docs-only explanation can promote a report with
a stale implementation binding. The closure, wave and fast-differential
validators apply the same implementation-tree rule.

The registry classes are:

- `canonical-strict`: clean current source, real HTTP, complete fixture/audit/rollback/cleanup, and accepted by closure;
- `canonical-diagnostic`: current clean screening or planning evidence that never counts as strict behavior;
- `diagnostic-failed`: failed, blocked, dirty or incomplete execution;
- `historical-superseded`: older source binding retained for audit;
- `runtime-lifecycle`: clone-ready or cleanup-pending runtime manifest;
- `duplicate-or-unreferenced`: redundant material awaiting archive.

The initial 2026-10-29 migration scanned 785 JSON artifacts before publication
cleanup. It moved eight stale root strict reports after a 67-reference dry-run
rewrite, deleted nothing, and left zero JSON files in the canonical root. The
move plan and SHA-256 audit remain under
`.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/` and `archive/`.

The previous `fdc60aba` inventory is recorded at
`.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/evidence-inventory-current-fdc60aba.tsv`.
It contains 845 registered JSON artifacts: 9 `canonical-strict`, 21
`canonical-diagnostic`, 147 `diagnostic-failed`, 532
`historical-superseded`, 53 `runtime-lifecycle`, and 83
`duplicate-or-unreferenced`. The canonical root contains 9 JSON files, all
registered as `canonical-strict`; the five current planning/fast artifacts are
diagnostic files below the feature tree. The companion migration snapshot is
`.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/evidence-lifecycle-current-fdc60aba.md`.

After the source-owned audit-record implementation at `5e3c4a50`, the 9
previous strict reports were correctly demoted and moved to archive. The
current snapshot is
`.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/evidence-lifecycle-current-5e3c4a50.md`:
853 registered JSON artifacts, 0 canonical-root JSON files, 4 current
canonical-diagnostic planning artifacts, and no current strict behavior report.

The next current-head replay at `f081acf0` promoted one audit-record report and
its closure. The latest snapshot is
`.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/evidence-lifecycle-current-f081acf0.md`:
870 registered JSON artifacts, 2 canonical-strict root reports, 3
canonical-diagnostic planning artifacts, and 2 diagnostic failed replays kept
outside the root.

The exact source-binding correction at `5d111f3b2b984bd94bf37ece941ecb631f530b04`
reclassified that snapshot before publication: the two `f081acf0` reports were
stale relative to the current implementation binding and were moved to
`archive/historical/` after a one-file, six-replacement reference rewrite.
No JSON was deleted. The migration started with 870 artifacts and two root
JSON files (`2 canonical-strict`, `3 canonical-diagnostic` in the registry's
pre-migration snapshot) and ends with 871 registered artifacts, zero root JSON,
729 historical, 86 failed and 56 runtime-lifecycle records. The two moved
reports and their pending copies are classified historical; duplicate hashes
remain recorded through `superseded_by`. The plan, inventory, SHA-256 move audit and migration summary are
stored under `diagnostic-evidence/`; the migration-time inventory recorded 868
tracked and 3 untracked JSON artifacts, while the post-commit verification scan
reports 871 tracked and 0 untracked. A new strict replay must be explicitly
promoted before any root behavior report reappears.

The current HEAD revalidation on `f0c1eb0c` scanned 897 JSON artifacts (879
tracked and 18 newly generated diagnostic files). The registry now records 3
`canonical-diagnostic`, 90 `diagnostic-failed`, 748 `historical-superseded`
and 56 `runtime-lifecycle` rows; the canonical root has zero JSON files. The
four order-retain attempts are retained as failed diagnostics and the updated
secondary runtime manifest is lifecycle-only. These counts describe evidence
governance, not API route completion.

The governance commit `56c34beeba7f` then advanced HEAD. The three planning
artifacts bound to `f0c1eb0c` were demoted immediately; the current registry
now has 897 rows: 751 `historical-superseded`, 90 `diagnostic-failed` and 56
`runtime-lifecycle`, with zero current canonical rows. `verify` rejects stale
canonical-strict and canonical-diagnostic rows directly, so a source change
cannot leave an apparently current publication until a later gate happens to
run.

The pre-commit `db698cc1` migration scanned 970 JSON artifacts (897 tracked
and 73 newly generated), with 10 canonical-strict reports in the root. After a
dry-run plan, 1,847 references across 220 active/pending files were rewritten
and the 10 root reports were moved to `archive/historical/`; no JSON was
deleted. The post-migration registry contains 970 rows: 778 historical, 106
failed, 67 runtime-lifecycle and 14 duplicate/unreferenced artifacts. Five
planning rows were also demoted historical before the governance commit. A new
current-head strict replay is required before the root can contain JSON again.
After the commit advanced HEAD, the automatic exact-binding reclassification
settled the registry at 970 tracked rows: 813 historical-superseded, 90
diagnostic-failed and 67 runtime-lifecycle, with no duplicate or unregistered
JSON. The class shift is bookkeeping only; it does not promote any old report.

To publish a report, first run closure against its pending diagnostic path, then
promote it explicitly:

```bash
python3 tools/api_route_closure.py \
  --ledger .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/current/api-route-ledger-current-head.json \
  --report .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/report.json \
  --output .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/closure.json
python3 scripts/manage-evidence.py promote \
  --report .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/report.json \
  --closure .scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/closure.json \
  --output evidence/report.json
```

`archive`, `rewrite` and `prune` are dry-run by default. They never remove canonical or
referenced material, never move an in-retention failure during prune, and every
move records the original path and SHA-256 in an audit log. `prune --apply`
deletes only unreferenced non-canonical files whose retention period has
expired, removes their registry rows, and records the action in
`archive/evidence-prune-audit.tsv`. References must be rewritten before an
explicitly selected archive move; use `archive --apply` or `rewrite --apply`
only after reviewing the complete dry-run output. Same-name archive targets
receive a hash suffix instead of overwriting an existing artifact.
`boundary route != boundary process` remains part of the strict route
execution model.

`plan` is the required first step for a migration. It is read-only and emits a
TSV containing source and destination paths, class, SHA-256, references,
retention and reason for every scanned JSON artifact. A reviewed plan may then
be applied with `archive --apply`/`rewrite --apply`; same-name destinations are
always hash-suffixed and never overwritten. A `canonical-diagnostic` payload
cannot claim `strict_evidence=true`, and a closure must pass current-source,
no-failure, same-Spring-app and registry-reference checks before it can
authorize report promotion.
`prune` writes `archive/evidence-prune-dry-run.tsv` before any `--apply` unlink,
then appends the executed actions to `evidence-prune-audit.tsv`.

Runner defaults are diagnostic-only: strict runtime manifests go under
`diagnostic-evidence/runtime/`, wave reports go under `diagnostic-evidence/pending/`,
and a runner cannot use a canonical root path as its source for promotion. The
output guard has no environment-variable bypass; only `manage-evidence.py
promote` can publish a report. A
strict write case is accepted only when the report contains HTTP business
write, API restore, rollback and audit observations for that case.

The 2026-11-05 `impl-4b7a3b416f41ac85` milestone published one combined
read-behavior report and closure after 5,247 representative HTTP cases and a
single runtime cleanup. Alias mapping closed 2,087 of 6,806 routes; 4,719
require further evidence. `verify` reports 4,289 registered rows, two
`canonical-strict` rows and zero `canonical-diagnostic` rows. The 11
canonical-strict artifacts from the superseded binding are retained under the
historical archive with their original SHA-256 and move audit. A subsequent
evidence-only commit can change repository HEAD while retaining the same
implementation binding; closure and registry freshness are checked against
`implementation_tree_digest`.
