#!/usr/bin/env python3
"""Verify the source-pinned LES schema/metadata and LES/PDF bootstrap inputs."""
import hashlib
import json
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
import schema_fixture as sf


def sha(data):
    return hashlib.sha256(data).hexdigest()


def normalize(statement):
    parts = re.split(r"('(?:''|[^'])*')", statement)
    for position in range(0, len(parts), 2):
        parts[position] = re.sub(r'\s+', ' ', parts[position]).replace(' add ORDERNO', ' ADD ORDERNO')
    return ''.join(parts).strip()


def source_blocks(manifest):
    sources = {}
    for item in manifest['sources']:
        raw = (ROOT / item['path']).read_bytes()
        assert sha(raw) == item['sha256'], item['path']
        sources[item['path']] = raw.decode().splitlines(keepends=True)
    output = []
    for block in manifest['source_blocks']:
        raw = ''.join(sources[block['source']][block['line_start'] - 1:block['line_end']]).strip()
        assert sha(raw.encode()) == block['source_sha256']
        output.append(raw)
    return output


base = ROOT / 'api-fixtures/schema'
manifest = json.loads((base / 'les-data-source-restoration.json').read_text())
assert manifest['owner'] == 'LIMS_RUNTIME_LOCAL'
assert sha((ROOT / 'scripts/schema_fixture.py').read_bytes()) == manifest['generator_sha256']
statements = []
for raw, block in zip(source_blocks(manifest), manifest['source_blocks']):
    original = sf.statements(raw)
    assert [sha(value.encode()) for value in original] == block['statement_sha256']
    rendered = [normalize(value) for value in original]
    assert [sha(value.encode()) for value in rendered] == block['normalized_sha256']
    statements.extend(rendered)
assert len(statements) == 35
# This fixture has its own finite admission. The shared seven-table policy and
# helper source are unchanged; the assignment exists only in this test process.
sf.TABLES = frozenset({'T_CORE_LES_DS', 'T_CORE_LES_DS_FIELD'})
loaded = sf.definitions(statements)
assert set(loaded) == sf.TABLES
assert {name: len(value['columns']) for name, value in loaded.items()} == {
    'T_CORE_LES_DS': 15, 'T_CORE_LES_DS_FIELD': 12,
}
assert loaded['T_CORE_LES_DS_FIELD']['columns'][-1]['name'] == 'ORDERNO'
assert loaded['T_CORE_LES_DS_FIELD']['indexes'] == [
    {'name': 'IDX_CORE_LES_DS_FIELD_DSID', 'column': 'LESDSID'},
]
fixture = ROOT / manifest['fixture']['path']
assert sha(fixture.read_bytes()) == manifest['fixture']['sha256']
assert fixture.read_text() == sf.render(loaded, sf.OWNER)
assert sf.plan(loaded, sf.OWNER)['create'] == list(loaded)
metadata = {name: {key: value for key, value in data.items() if key != 'statements'}
            for name, data in loaded.items()}
assert sf.plan(loaded, sf.OWNER, metadata)['skip'] == list(loaded)
for owner in ['LIMS_BASELINE_LOCAL', 'SYS', 'LIMS_RUNTIME_SSS2']:
    try:
        sf.plan(loaded, owner)
    except sf.FixtureError:
        pass
    else:
        raise AssertionError('wrong schema admitted')
for name in loaded:
    broken = dict(metadata, **{name: dict(metadata[name], columns=[])})
    try:
        sf.plan(loaded, sf.OWNER, broken)
    except sf.FixtureError:
        pass
    else:
        raise AssertionError('incompatible table admitted')
assert 'INSERT INTO' not in fixture.read_text() and 'DELETE FROM' not in fixture.read_text()

metadata_manifest = json.loads((base / 'les-data-source-metadata.json').read_text())
metadata_source = source_blocks(metadata_manifest)
assert len(metadata_source) == 1
assert metadata_source[0].count('INSERT INTO T_CORE_TABLE (') == 2
assert 'INSERT INTO t_core_table_hierarchy' in metadata_source[0]
metadata_sql = (ROOT / metadata_manifest['fixture']['path']).read_text()
assert sha(metadata_sql.encode()) == metadata_manifest['fixture']['sha256']
assert metadata_sql.index('SAVEPOINT les_metadata_fixture') > metadata_sql.index('Conflicting LES field hierarchy')
for fragment in [
    "CURRENT_SCHEMA') <> 'LIMS_RUNTIME_LOCAL'",
    "SESSION_USER') NOT IN ('SYS', 'LIMS_RUNTIME_LOCAL')",
    "UPPER(ID) = 'T_CORE_LES_DS'",
    "UPPER(ID) = 'T_CORE_LES_DS_FIELD'",
    "UPPER(TABLENAME) = 'T_CORE_LES_DS_FIELD'",
    "MASTERTABLENAME = 'T_CORE_LES_DS' AND REFERCOLUMN = 'LESDSID'",
    'ROLLBACK TO les_metadata_fixture', 'SEQ_CORE.NEXTVAL',
]:
    assert fragment in metadata_sql, fragment
assert metadata_sql.count('INSERT INTO T_CORE_TABLE (') == 2
assert metadata_sql.count('INSERT INTO T_CORE_TABLE_HIERARCHY (') == 1
assert not re.search(r'\b(?:UPDATE|DELETE|DROP|TRUNCATE|CREATE TABLE|ALTER TABLE)\b', metadata_sql)

bootstrap = json.loads((base / 'les-pdf-select-config-restoration.json').read_text())
blocks = source_blocks(bootstrap)
assert len(blocks) == 2
assert 'les-template-category' in blocks[0] and 'lesTemplateCategory' in blocks[0]
assert 'file-acq-preprocess-operation' in blocks[1] and 'fileAcqPreprocessOperation' in blocks[1]
assert bootstrap['execution_mode'] == 'root-coordinated-existing-HTTP-APIs'
assert bootstrap['SQL_business_writes'] is False
entries = bootstrap['categories']
assert [item['id'] for item in entries] == ['lesTemplateCategory', 'fileAcqPreprocessOperation']
assert all(item['source_values']['codeClassId'] == 'biz' for item in entries)
assert entries[0]['options'] == []
assert [(item['code'], item['codeName'], item['orderNo']) for item in entries[1]['options']] == [
    ('remove', '删除', 1), ('removeRow', '删除行', 2), ('replace', '替换', 3),
]
for entry in entries:
    mapping = entry['select_config']
    assert mapping['category'] in ['les-template-category', 'file-acq-preprocess-operation']
    assert mapping['url'] == '/secure/core/module/sys/codes/queries/selectable?codeCategoryId=' + entry['id']
    assert (mapping['valueField'], mapping['textField']) == ('code', 'codeName')
    assert mapping['category'] in ''.join(blocks)
    assert all(option['lastSuspendedFlag'] == '0' for option in entry['options'])
assert bootstrap['HTTP_sequence'][0]['operation'] == 'read-preflight'
assert bootstrap['HTTP_sequence'][-1]['operation'] == 'read-verify'
assert any(item['purpose'] == 'Restore source biz class after original POST forces sys'
           for item in bootstrap['HTTP_sequence'])
assert any(item['purpose'] == 'Rename the actual automatically created camelCase select config to source hyphenated alias'
           for item in bootstrap['HTTP_sequence'])
print('LES/PDF restoration: PASS (original source hashes, exact two-table DDL, guards, '
      'finite metadata and actual HTTP bootstrap contract; no database execution)')
