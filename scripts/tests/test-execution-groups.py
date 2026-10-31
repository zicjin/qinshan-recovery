#!/usr/bin/env python3
import csv
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
matrix={r['case_id'] for r in csv.DictReader((ROOT/'evidence/vertical-slice-matrix.tsv').open(),delimiter='\t')}
groups=list(csv.DictReader((ROOT/'evidence/vertical-slice-execution-groups.tsv').open(),delimiter='\t'))
ids=[r['case_id'] for r in groups]
assert len(ids)==len(set(ids)) and set(ids)==matrix
assert all(r['execution_group'] and r['runner'] for r in groups)
print('execution groups: PASS (%d contracts, %d shared shards)' % (len(groups), len({r['execution_group'] for r in groups})))
