#!/usr/bin/env python3
"""Render one exact seal-rule audit cleanup from a private journal; never connect."""
from __future__ import annotations
import argparse
from datetime import datetime
import hashlib
import json
from pathlib import Path
import re
import sys

OWNER = 'LIMS_RUNTIME_LOCAL'
TABLE = 'T_EMC_SEAL_RULE'
BASE = '/secure/emc/module/mdm/basemdm/seal-rules/'
CONFIG = '/secure/core/module/sys/at-configs'
GROUPS = (('records', 'T_CORE_AT_RECORD', 'recordIds'),
          ('lines', 'T_CORE_AT_RECORD_LINE', 'lineIds'),
          ('configLines', 'T_CORE_AT_CONFIG_LINE', 'configLineIds'))
TEMPLATE = Path(__file__).resolve().parents[1] / 'api-fixtures/audit/cleanup-seal-audit-journal.sql'


class JournalError(ValueError):
    pass


def require(condition, message):
    if not condition:
        raise JournalError(message)


def exact(value, keys):
    require(isinstance(value, dict) and set(value) == set(keys.split()), 'unexpected or missing journal fields')


def number(value):
    require(type(value) is int and 0 < value < 10**38, 'expected positive numeric journal ID')
    return str(value)


def text(value, maximum=384):
    require(isinstance(value, str) and 0 < len(value) <= maximum and
            not any(ord(c) < 32 for c in value) and not any(c in value for c in "';&\n\r"),
            'unsupported journal text')
    return "'" + value + "'"


def digest(value):
    require(isinstance(value, str) and re.fullmatch('[0-9a-f]{64}', value), 'expected SHA256')
    return value


def timestamp(value):
    require(isinstance(value, str), 'expected timestamp')
    parsed = datetime.strptime(value, '%Y-%m-%d %H:%M:%S')
    require(parsed.strftime('%Y-%m-%d %H:%M:%S') == value, 'expected second precision timestamp')
    return parsed


def ids(values):
    require(isinstance(values, list), 'expected ID snapshot')
    for value in values:
        number(value)
    require(len(values) == len(set(values)), 'duplicate journal IDs')
    return values


def sha(data):
    return hashlib.sha256(data).hexdigest()


def unique_object(pairs):
    result = {}
    for key, value in pairs:
        require(key not in result, 'duplicate JSON key')
        result[key] = value
    return result


def load(journal_path):
    raw = journal_path.read_bytes()
    j = json.loads(raw, object_pairs_hook=unique_object)
    exact(j, 'version owner table targetId marker principal organization startedAt finishedAt baseline records lines configLines apiRestore')
    require(type(j['version']) is int and j['version'] == 1 and j['owner'] == OWNER and j['table'] == TABLE, 'unapproved version, owner or table')
    number(j['targetId'])
    require(isinstance(j['marker'], str) and re.fullmatch(r'owned-strict-[a-z0-9-]{1,80}', j['marker']), 'unapproved batch marker')
    text(j['principal'], 36)
    text(j['organization'], 36)
    start, finish = timestamp(j['startedAt']), timestamp(j['finishedAt'])
    require(start <= finish, 'reversed journal interval')
    exact(j['baseline'], 'recordIds lineIds configLineIds')
    target = TABLE + '$' + number(j['targetId'])
    fields = {
        'records': 'id targetId auditTrailType auditTrailName commentHash',
        'lines': 'id recordId tableName targetId recordType column oldValueHash newValueHash',
        'configLines': 'id configId columnId auditTrail',
    }
    for group, _, baseline_key in GROUPS:
        prior = ids(j['baseline'][baseline_key])
        rows = j[group]
        require(isinstance(rows, list) and 0 < len(rows) <= 1000, 'missing or excessive candidate rows')
        for row in rows:
            exact(row, fields[group] + ' createdById createdByOrgId createdTime')
            require(row['createdById'] == j['principal'] and row['createdByOrgId'] == j['organization'], 'principal or organization mismatch')
            require(start <= timestamp(row['createdTime']) <= finish, 'candidate timestamp outside batch')
            for key, value in row.items():
                if key.endswith('Hash'):
                    if value is not None:
                        digest(value)
                elif key in ('id', 'recordId'):
                    number(value)
                else:
                    text(value)
            if group != 'configLines':
                require(row['targetId'] == target, 'target mismatch')
            if group == 'records':
                require(row['auditTrailType'] in ('INSERT', 'UPDATE', 'DELETE'), 'unsupported audit type')
            if group == 'lines':
                require(row['tableName'] == TABLE and row['recordType'] in ('INSERT', 'UPDATE', 'DELETE'), 'line table or type mismatch')
                require(re.fullmatch('[A-Z][A-Z0-9_]{0,59}', row['column']), 'invalid audit column')
            if group == 'configLines':
                require(row['configId'] == TABLE and row['auditTrail'] in ('0', '1'), 'config parent or flag mismatch')
                require(re.fullmatch('[A-Z][A-Z0-9_]{0,35}', row['columnId']), 'invalid config column')
        new = ids([r['id'] for r in rows])
        require(not set(prior) & set(new), 'candidate ID exists in baseline')
    record_ids = {r['id'] for r in j['records']}
    require({r['recordId'] for r in j['lines']} == record_ids, 'missing or unexpected parent audit link')
    marker_hash = sha(j['marker'].encode('utf-8'))
    require(any(r['column'] == 'NAME' and marker_hash in (r['oldValueHash'], r['newValueHash']) for r in j['lines']), 'missing exact batch marker field hash')
    proof = j['apiRestore']
    exact(proof, 'path sha256 businessRemoved configParentRemoved')
    require(proof['businessRemoved'] is True and proof['configParentRemoved'] is True, 'API restore not completed')
    require(isinstance(proof['path'], str), 'expected private transcript path')
    relative = Path(proof['path'])
    require(not relative.is_absolute() and '..' not in relative.parts, 'transcript must be alongside private journal')
    proof_path = (journal_path.parent / relative).resolve()
    require(proof_path.is_relative_to(journal_path.parent.resolve()) and proof_path != journal_path.resolve(), 'invalid transcript path')
    data = proof_path.read_bytes()
    require(sha(data) == digest(proof['sha256']), 'HTTP transcript hash mismatch')
    p = json.loads(data, object_pairs_hook=unique_object)
    exact(p, 'version baseUrl principal organization marker authentication requests')
    require(type(p['version']) is int and p['version'] == 1 and re.fullmatch(r'http://127\.0\.0\.1:180(?:87|88|89)', p['baseUrl']), 'HTTP proof must use local owned runtime')
    require(all(p[k] == j[k] for k in ('principal', 'organization', 'marker')), 'HTTP proof identity or marker mismatch')
    exact(p['authentication'], 'sessionCookieSha256 identity')
    digest(p['authentication']['sessionCookieSha256'])
    exact(p['authentication']['identity'], 'id organizationId')
    require(p['authentication']['identity'] == {'id': j['principal'], 'organizationId': j['organization']}, 'HTTP authenticated identity mismatch')
    require(isinstance(p['requests'], list), 'missing HTTP requests')
    observed = {}
    for request in p['requests']:
        exact(request, 'method path status requestBody rawBody bodySha256')
        require(isinstance(request['rawBody'], str) and sha(request['rawBody'].encode('utf-8')) == digest(request['bodySha256']), 'HTTP response body hash mismatch')
        body = json.loads(request['rawBody'], object_pairs_hook=unique_object) if request['rawBody'] else None
        require(type(request['status']) is int, 'invalid HTTP status')
        require(request['method'] in ('GET', 'POST', 'PUT', 'DELETE') and isinstance(request['path'], str), 'invalid HTTP observation')
        key = (request['method'], request['path'], request['status'])
        require(key not in observed, 'duplicate HTTP restore observation')
        observed[key] = body
    business = BASE + str(j['targetId'])
    collection = BASE.rstrip('/')
    required = {('DELETE', collection, 200), ('GET', business, 200),
                ('POST', BASE + 'queries', 200), ('DELETE', CONFIG, 200)}
    require(required <= observed.keys(), 'missing successful API removal and absent business proof')
    before = observed[('GET', business, 200)]
    require(isinstance(before, dict) and before.get('id') == j['targetId'] and before.get('name') == j['marker'], 'HTTP fixture marker read mismatch')
    page = observed[('POST', BASE + 'queries', 200)]
    require(isinstance(page, dict) and type(page.get('total')) is int and page['total'] == 0 and
            page.get('rows') == [] and not ({'error', 'exception', 'login', 'success'} & page.keys()), 'HTTP business not empty')
    expected_bodies = {
        ('GET', business, 200): None,
        ('DELETE', collection, 200): {'b': ['', [{'id': j['targetId']}]], 'p': {}},
        ('DELETE', CONFIG, 200): {'b': ['', [{'id': TABLE}]], 'p': {}},
        ('POST', BASE + 'queries', 200): {'p': {'n': 1, 's': 10, 'f': {'id_SEQ': j['targetId']}}},
    }
    for request in p['requests']:
        key = (request['method'], request['path'], request['status'])
        require(key in expected_bodies and request['requestBody'] == expected_bodies[key],
                'HTTP restore request body or route mismatch')
    sequence = [(r['method'], r['path'], r['status']) for r in p['requests']]
    require(sequence.index(('GET', business, 200)) < sequence.index(('DELETE', collection, 200)) <
            sequence.index(('POST', BASE + 'queries', 200)), 'HTTP restore observations out of order')
    for path in (collection, CONFIG):
        result = observed[('DELETE', path, 200)]
        require(result is None or (type(result) is int and result >= 0), 'unexpected HTTP removal body')
    return j, sha(raw), proof['sha256']


def count_check(query, count, label):
    return (f'    SELECT COUNT(*) INTO n FROM {query};\n'
            f"    IF n<>{count} THEN RAISE_APPLICATION_ERROR(-20002,'{label}'); END IF;")


def set_check(table, values):
    out = [count_check(OWNER + '.' + table, len(values), table + ' count mismatch')]
    if values:
        # Oracle permits at most 1000 IN-list expressions; use per-ID exact checks.
        out.extend(count_check(OWNER + '.' + table + ' WHERE ID=' + number(v), 1,
                               table + ' ID mismatch') for v in values)
    return '\n'.join(out)


def predicate(row):
    columns = {'createdById': 'CREATEDBYID', 'createdByOrgId': 'CREATEDBYORGID',
               'createdTime': 'CREATEDTIME', 'targetId': 'TARGETID', 'auditTrailType': 'AUDITTRAILTYPE',
               'auditTrailName': 'AUDITTRAILNAME', 'recordId': 'RECORDID', 'tableName': 'TABLENAME',
               'recordType': 'RECORDTYPE', 'column': '"COLUMN"', 'configId': 'CONFIGID',
               'columnId': 'COLUMNID', 'auditTrail': 'AUDITTRAIL'}
    terms = ['ID=' + number(row['id'])]
    for key, value in row.items():
        if key == 'id':
            continue
        if key.endswith('Hash'):
            col = {'commentHash': '"COMMENT"', 'oldValueHash': 'OLDVALUE', 'newValueHash': 'NEWVALUE'}[key]
            terms.append(col + ' IS NULL' if value is None else
                         f"LOWER(RAWTOHEX(DBMS_CRYPTO.HASH(TO_CLOB({col}),4)))='{value}'")
        elif key == 'auditTrailName':
            # Live Oracle declares this field CLOB; never compare it with '='.
            terms.append("LOWER(RAWTOHEX(DBMS_CRYPTO.HASH(TO_CLOB(AUDITTRAILNAME),4)))='" +
                         sha(value.encode('utf-8')) + "'")
        elif key == 'createdTime':
            terms.append("CREATEDTIME=TO_DATE(" + text(value) + ",'YYYY-MM-DD HH24:MI:SS')")
        else:
            terms.append(columns[key] + '=' + (number(value) if key == 'recordId' else text(value)))
    return ' AND '.join(terms)


def render(j, journal_sha, http_sha):
    locks, preflight, deletes, post = [], [], [], []
    # Candidate row locks only. Baseline rows never participate in mutation.
    for group, table, prior in GROUPS:
        for row in sorted(j[group], key=lambda r: r['id']):
            locks.append(f'  SELECT ID INTO locked_id FROM {OWNER}.{table} WHERE ID={row["id"]} FOR UPDATE NOWAIT;')
        preflight.append(set_check(table, j['baseline'][prior] + [r['id'] for r in j[group]]))
        for row in j[group]:
            preflight.append(count_check(OWNER + '.' + table + ' WHERE ' + predicate(row), 1, 'Candidate field mismatch'))
        post.append(set_check(table, j['baseline'][prior]))
    for row in j['records']:
        preflight.append(count_check(OWNER + '.T_CORE_AT_RECORD_LINE WHERE RECORDID=' + number(row['id']),
                                     sum(r['recordId'] == row['id'] for r in j['lines']), 'Unexpected child audit link'))
    target = TABLE + '$' + str(j['targetId'])
    preflight += [count_check(OWNER + '.T_CORE_AT_RECORD WHERE TARGETID=' + text(target), len(j['records']), 'Unexpected target record'),
                  count_check(OWNER + '.T_CORE_AT_RECORD_LINE WHERE TARGETID=' + text(target), len(j['lines']), 'Unexpected target line'),
                  count_check(OWNER + '.T_CORE_AT_CONFIG_LINE WHERE CONFIGID=' + text(TABLE), len(j['configLines']), 'Unexpected config line')]
    absence = [count_check(OWNER + '.' + TABLE, 0, 'Business fixture not empty'),
               count_check(OWNER + '.T_CORE_AT_CONFIG WHERE ID=' + text(TABLE), 0, 'Config parent still present')]
    preflight += absence
    post += absence
    for group, table, _ in (GROUPS[1], GROUPS[0], GROUPS[2]):
        for row in j[group]:
            deletes += [f'  DELETE FROM {OWNER}.{table} WHERE ' + predicate(row) + ';',
                        "  IF SQL%ROWCOUNT<>1 THEN RAISE_APPLICATION_ERROR(-20003,'Exact delete count mismatch'); END IF;"]
    sql = TEMPLATE.read_text(encoding='utf-8')
    replacements = {'JOURNAL_SHA256': journal_sha, 'HTTP_SHA256': http_sha,
                    'PREFLIGHT': '\n'.join(preflight), 'LOCKS': '\n'.join(locks),
                    'DELETES': '\n'.join(deletes), 'POSTCHECKS': '\n'.join(post)}
    sql = re.sub(r'@([A-Z0-9_]+)@', lambda match: replacements[match[1]], sql)
    return sql


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--journal', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    try:
        require(args.output.resolve() != args.journal.resolve(), 'output would overwrite journal')
        j, journal_sha, http_sha = load(args.journal)
        require(args.output.resolve() != (args.journal.parent / j['apiRestore']['path']).resolve(), 'output would overwrite HTTP proof')
        sql = render(j, journal_sha, http_sha)
        with args.output.open('x', encoding='utf-8') as stream:
            args.output.chmod(0o600)
            stream.write(sql)
        print(json.dumps({'rendered': True, 'strict_evidence': False, 'database_executed': False,
                          'journal_sha256': journal_sha, 'sql_sha256': sha(sql.encode('utf-8'))}))
        return 0
    except (ValueError, OSError, KeyError, TypeError) as exc:
        print('cleanup refused: ' + str(exc), file=sys.stderr)
        return 2


if __name__ == '__main__':
    sys.exit(main())
