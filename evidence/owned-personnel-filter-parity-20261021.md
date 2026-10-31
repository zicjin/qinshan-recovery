# Owned personnel filter parity

Date: 2026-10-21 (Asia/Singapore)

The owned personnel read seam now follows the recovered Oracle mapper for the
filters used by the personnel page. The service keeps the trusted current
identity and organization row scope, while applying request filters only to a
whitelisted personnel projection. Case-insensitive prefix/contains/suffix and
exact filters uppercase both the selected column and the value. Multi-field
`OR` filters remain disjunctive, multi-property filters preserve their
case-insensitive behavior, explicit ordering adds Oracle `NULLS LAST`, and
numeric/date/time inclusive bounds use their recovered comparison semantics.

The fixture also covers the active personnel list search fields observed in
`emc-emp-edit-list.js`: `FILTERTEST`, `REPORTAUDIT`, `REPORTISSUE`, and the
`overdueFilter=1` certificate-expiry checkbox (plus the historical
`remind=overdue` selector). The former reads `T_EMC_EMP_QUAL`; the historical
selector reads `T_EMC_CERT`, matching their separate mapper clauses. The
existing baseline output remains
`personnel query service: PASS (15 checks; ...)`
so browser/distribution runners that consume that line remain compatible. The
additional filter assertions are reported separately as
`personnel filter parity: PASS (19 checks; synthetic Oracle only)`, including
case-insensitive OR exact matching and rejection of an unsupported column.

Run from `lims-recovery/` (serially):

```bash
export JAVA_HOME=/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391
python3 scripts/tests/test-personnel-query-oracle.py \
  --output /tmp/personnel-filter-parity.json
```

Observed result for the merged integration source (the runner records the
exact commit and source hashes in its JSON report):

```text
owned framework build and contract test passed
personnel query service: PASS (15 checks; synthetic Oracle only)
personnel filter parity: PASS (19 checks; synthetic Oracle only)
```

The disposable Oracle network was internal-only, had one endpoint, no host
ports or bind mounts, and was removed with its anonymous volumes after the
run. The runner report records `status=executed-pass`, `checks=15`,
`filter_checks=19`, and `production_access=false`.

This slice does not claim all generic `MatchPattern` values. `SPECIAL`,
`MULTIPLE`, `IN`, and `OIN` remain outside the personnel page seam and are
rejected rather than silently treated as equality. Chooser-specific
`targetType`, `batchinputusername`, and sample-plan branches from the
historical mapper are not part of the personnel edit-list query path covered
here. HTTP/browser filter assertions remain in the shared personnel
application test owned by the integration agent.
