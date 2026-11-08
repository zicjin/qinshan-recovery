#!/usr/bin/env python3
"""Verify exact original DDL/provenance and guarded fixture for email associations."""
import hashlib
import json
from pathlib import Path
import sys
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
import schema_fixture as sf
ROOT=Path(__file__).resolve().parents[2]
manifest=json.loads((ROOT/'api-fixtures/schema/email-config-associations.json').read_text())
source=ROOT/manifest['source']['path'];assert sf.sha(source.read_bytes())==manifest['source']['sha256']
assert sf.sha((ROOT/'scripts/schema_fixture.py').read_bytes())==manifest['generator_sha256']
lines=source.read_text().splitlines(keepends=True);statements=[]
for block in manifest['source_blocks']:
 raw=''.join(lines[block['line_start']-1:block['line_end']]).strip()
 assert sf.sha(raw.encode())==block['sha256'];statements.extend(sf.statements(raw))
expected={'T_CORE_EMAIL_CONFIG_RECEIPENT','T_CORE_EMAIL_CONFIG_REC_ROLE'}
# Local test admission only: never modify the existing seven-table policy/helper.
sf.TABLES=frozenset(expected)
loaded=sf.definitions(statements);assert set(loaded)==expected
fixture=ROOT/manifest['fixture']['path'];assert sf.sha(fixture.read_bytes())==manifest['fixture']['sha256']
assert fixture.read_text()==sf.render(loaded,sf.OWNER)
assert sf.plan(loaded,sf.OWNER)['create']==list(loaded)
metadata={name:{k:v for k,v in data.items()if k!='statements'}for name,data in loaded.items()}
assert sf.plan(loaded,sf.OWNER,metadata)['skip']==list(loaded)
for owner in ['LIMS_BASELINE_LOCAL','SYS','LIMS_RUNTIME_SSS2']:
 try:sf.plan(loaded,owner)
 except sf.FixtureError:pass
 else:raise AssertionError('wrong schema admitted')
for table in loaded:
 invalid=dict(metadata);invalid[table]=dict(metadata[table],columns=[])
 try:sf.plan(loaded,sf.OWNER,invalid)
 except sf.FixtureError:pass
 else:raise AssertionError('incompatible table admitted')
assert 'DELETE FROM'not in fixture.read_text()and 'INSERT INTO'not in fixture.read_text()
print('email association fixture: PASS (original DDL hashes, exact regeneration, missing/equivalent/conflicting metadata, wrong owners, no business DML)')
