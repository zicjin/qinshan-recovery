"""Executed HTTP write proof, independently checked by the promotion gate.

Private transcripts are separate 0600 artifacts, never canonical JSON inputs.
They contain non-sensitive fixture response JSON and allowlisted identity
projections. Raw account/login bodies and credentials are never retained.
A digest is integrity/linkage evidence, not remote-server authentication.
"""
from __future__ import annotations
import argparse
import csv
import base64
import datetime as dt
import hashlib
import json
import os
from pathlib import Path
from urllib.parse import parse_qsl, urlsplit
from evidence_fingerprint import SENSITIVE_HEADERS, request_fingerprint, response_fingerprint

VERSION = 2
CONTEXT_FIELDS = ('batch_id', 'clone_identity', 'application_binding', 'runtime_contract_binding',
                  'harness_binding', 'artifact_sha256', 'role', 'organization', 'principal', 'base_url')


def digest(value):
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(',', ':'), ensure_ascii=True).encode()).hexdigest()


def at(value, path):
    for part in str(path).split('.'):
        if not part:
            continue
        value = value[int(part)] if isinstance(value, list) else value[part]
    return value


def validate_read_request(request):
    # Lazy import avoids the evidence_output/fast-runner module import cycle.
    from fast_differential_api_runner import route_safe
    if request.get('operation') != 'read':
        raise ValueError('HTTP proof observation requires explicit operation=read')
    if request.get('method', 'GET') == 'GET' and request.get('path') == '/core/module/sys/logins/account':
        return  # CoreLoginController account is a source-backed read-only identity seam.
    safe, reason = route_safe({'method': request.get('method', 'GET'),
                               'path': request.get('path', ''),
                               'route_kind': request.get('route_kind', 'api-resource')})
    if not safe:
        raise ValueError('HTTP proof observation is not admitted read-only: ' + reason)


def business_write(case):
    route = case.get('route', case)
    return (str(route.get('method', 'GET')).upper() in {'PUT', 'PATCH', 'DELETE'}
            or str(route.get('operation', case.get('operation', 'read'))).lower() in {'write', 'create', 'update', 'delete'})


def context(report, case):
    result = {field: report.get(field) for field in CONTEXT_FIELDS}
    result['case_id'] = case.get('id')
    result['authentication_sha256'] = digest(report.get('authentication'))
    return result


def _check_sensitive(value):
    if isinstance(value, dict):
        forbidden = {'password', 'token', 'authorization', 'cookie', 'setcookie',
                     'privatekey', 'securityrsakey', 'accesstoken', 'refreshtoken',
                     'logintoken', 'apikey', 'clientsecret', 'licensebody', 'licensekey'}
        forbidden.update(''.join(char for char in key if char.isalnum()) for key in SENSITIVE_HEADERS)
        for key, item in value.items():
            normalized = ''.join(char for char in str(key).lower() if char.isalnum())
            if normalized in forbidden:
                raise ValueError('HTTP proof refuses sensitive transcript fields')
            _check_sensitive(item)
    elif isinstance(value, list):
        for item in value:
            _check_sensitive(item)


def _check_auth(auth):
    # The runner supplies only one-way session-cookie digests. Imported raw
    # credentials are never an admissible transcript representation.
    if not isinstance(auth, dict) or set(auth) != {'cookies'} or not isinstance(auth['cookies'], dict):
        raise ValueError('HTTP proof auth must contain hashed session context')
    if any(not isinstance(value, str) or len(value) != 64
           or any(char not in '0123456789abcdef' for char in value)
           for value in auth['cookies'].values()):
        raise ValueError('HTTP proof refuses raw session credentials')


def _check_request_privacy(request):
    _check_auth(request.get('auth'))
    _check_sensitive(request.get('headers', {}))
    _check_sensitive(request.get('body', {}))
    for key in ('path', 'path_template'):
        _check_sensitive(dict(parse_qsl(urlsplit(str(request.get(key, ''))).query, keep_blank_values=True)))
    _check_sensitive(request.get('query', {}))
    if request.get('body_raw') is not None:
        raise ValueError('HTTP proof requires structured JSON business requests')


# CoreLoginServiceImpl.getAccount serializes CoreRoleUserBean assignments
# into ext$.roles. Only these source selectors and output fields are admitted.
IDENTITY_SELECTORS = {'principal': 'id', 'account_organization': 'orgId',
                      'organization': 'ext$.loginorgid', 'roles': 'ext$.roles'}
IDENTITY_ADAPTER = 'sunway-account-v1'


def identity_projection(body, declaration):
    if declaration != {'adapter': IDENTITY_ADAPTER, 'selectors': IDENTITY_SELECTORS}:
        raise ValueError('identity proof requires source-backed allowlist adapter/selectors')
    account = json.loads(body)
    def identifier(value):
        if isinstance(value, bool) or not isinstance(value, (str, int)) or str(value) == '':
            raise ValueError('identity proof requires nonempty server identifiers')
        return str(value)
    result = {key: identifier(at(account, path)) for key, path in IDENTITY_SELECTORS.items() if key != 'roles'}
    assignments = json.loads(at(account, IDENTITY_SELECTORS['roles']))
    if not isinstance(assignments, list) or not assignments:
        raise ValueError('identity proof requires nonempty server role assignments')
    result['roles'] = []
    for assignment in assignments:
        if not isinstance(assignment, dict):
            raise ValueError('identity proof requires actual user-role assignments')
        result['roles'].append({'role_id': identifier(assignment['roleId']),
                                'principal': identifier(assignment['userId'])})
    validate_identity(result)
    return result


def validate_identity(value, *, principal=None, role=None, organization=None):
    if not isinstance(value, dict) or set(value) != set(IDENTITY_SELECTORS):
        raise ValueError('identity proof projection fields differ from allowlist')
    if any(not isinstance(value[k], str) or not value[k] for k in ('principal', 'organization', 'account_organization')):
        raise ValueError('identity proof requires nonempty principal and organization')
    roles = value['roles']
    if (not isinstance(roles, list) or not roles or any(not isinstance(r, dict)
            or set(r) != {'role_id', 'principal'} or not isinstance(r['role_id'], str)
            or not r['role_id'] or r['principal'] != value['principal'] for r in roles)):
        raise ValueError('identity proof invalid server role assignment')
    if principal is not None and value['principal'] != str(principal):
        raise ValueError('identity proof authenticated principal mismatch')
    if organization is not None and value['organization'] != str(organization):
        raise ValueError('identity proof authenticated organization mismatch')
    if role is not None and not any(r['role_id'] == str(role) for r in roles):
        raise ValueError('identity proof authenticated role missing or revoked')


def _sha(value):
    return isinstance(value, str) and len(value) == 64 and all(c in '0123456789abcdef' for c in value)


def projected_response(body, declaration):
    projection = identity_projection(body, declaration)
    return {'kind': 'allowlist-json-projection', **declaration,
            'raw_body_sha256': hashlib.sha256(body).hexdigest(), 'raw_body_bytes': len(body),
            'projection_sha256': digest(projection), 'raw_body_recomputable': False,
            'trust': 'executed-runner-source-bound-producer'}, projection


def validate_projected_response(metadata, projection, fingerprint):
    if (not isinstance(metadata, dict) or set(metadata) != {'kind', 'adapter', 'selectors', 'raw_body_sha256',
            'raw_body_bytes', 'projection_sha256', 'raw_body_recomputable', 'trust'}
            or metadata['kind'] != 'allowlist-json-projection'
            or metadata['adapter'] != IDENTITY_ADAPTER or metadata['selectors'] != IDENTITY_SELECTORS
            or metadata['raw_body_recomputable'] is not False
            or metadata['trust'] != 'executed-runner-source-bound-producer'
            or not _sha(metadata['raw_body_sha256'])
            or not isinstance(metadata['raw_body_bytes'], int) or isinstance(metadata['raw_body_bytes'], bool)
            or metadata['raw_body_bytes'] <= 0
            or metadata['projection_sha256'] != digest(projection)
            or not isinstance(fingerprint, str) or not fingerprint.startswith('res-') or not _sha(fingerprint[4:])):
        raise ValueError('identity proof projection metadata/hash mismatch')
    validate_identity(projection)


def transcript(request, status, headers, body, started, finished):
    # Session context consists solely of hashed cookies, never raw credentials.
    request = dict(request)
    request['headers'] = {k: v for k, v in request.get('headers', {}).items()}
    _check_request_privacy(request)
    declaration = request.get('_identity_projection')
    if declaration is not None:
        metadata, projection = projected_response(body, declaration)
    else:
        parsed = json.loads(body) if body else None
        _check_sensitive(parsed)
    safe_request = {key: request[key] for key in ('method', 'path', 'path_template', 'operation', 'route_kind', 'body',
                    'body_raw', 'headers', 'auth', 'role', 'organization') if key in request}
    result = {'request': safe_request, 'status': status,
            'response_headers': {'content-type': headers.get('content-type', '')},
            'body_base64': base64.b64encode(body).decode('ascii'),
            'started_at': started, 'finished_at': finished,
            'request_fingerprint': request_fingerprint(request),
            'response_fingerprint': response_fingerprint(status, headers, body)}
    if declaration is not None:
        result.pop('body_base64')
        result.update(response_projection=projection, projection_metadata=metadata)
    return result


def write_artifact(path, payload):
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    data = json.dumps(payload, ensure_ascii=True, sort_keys=True, separators=(',', ':')).encode()
    descriptor = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
    with os.fdopen(descriptor, 'wb') as stream:
        stream.write(data)
    return {'schema': VERSION, 'storage': 'local-pending', 'path': str(path.resolve()), 'sha256': hashlib.sha256(data).hexdigest()}


def _rows(body, spec):
    value = at(body, spec.get('records_path', 'rows'))
    if not isinstance(value, list):
        raise ValueError('audit proof records must be a list')
    return value


def validate_payload(payload, report, case):
    """Verify retained raw observations and source-bound privacy projections."""
    if payload.get('schema') != VERSION or payload.get('context') != context(report, case):
        raise ValueError('HTTP proof context mismatch')
    if any(not payload['context'].get(key) for key in CONTEXT_FIELDS + ('case_id',)):
        raise ValueError('HTTP proof context incomplete')
    if (case.get('audit') != {'required': True, 'status': 'observed', 'source': 'executed-http-proof'}
            or case.get('rollback', {}).get('status') != 'observed'
            or case.get('fixture', {}).get('state_restore_proof') != 'executed-http-proof'):
        raise ValueError('HTTP proof contradicts public observations')
    if any(case.get(key) != report.get(key) for key in ('batch_id', 'application_binding', 'harness_binding', 'runtime_contract_binding')):
        raise ValueError('HTTP proof case binding mismatch')
    if report.get('authentication', {}).get('authenticated') is not True:
        raise ValueError('HTTP write proof requires successfully authenticated principal')
    authentication = report['authentication']
    validate_projected_response(authentication['projection_metadata'], authentication['identity'], authentication['response_fingerprint'])
    validate_identity(authentication['identity'], principal=report['principal'], role=report['role'], organization=report['organization'])
    _check_auth(authentication['session_context'])
    if not authentication['session_context']['cookies']:
        raise ValueError('identity proof requires authenticated session cookies')
    observations = payload['observations']
    def observed(key):
        item = observations[key]
        projected = key in {'identity_before', 'identity_after'}
        if projected:
            if 'body_base64' in item:
                raise ValueError('identity proof refuses raw account response')
            validate_projected_response(item['projection_metadata'], item['response_projection'], item['response_fingerprint'])
            parsed = item['response_projection']
            validate_identity(parsed, principal=report['principal'], role=report['role'], organization=report['organization'])
            raw = None
        else:
            if 'projection_metadata' in item or 'response_projection' in item:
                raise ValueError('HTTP proof projection only admitted for identity observations')
            raw = base64.b64decode(item['body_base64'], validate=True)
            parsed = json.loads(raw) if raw else None
        if item['request_fingerprint'] != request_fingerprint(item['request']):
            raise ValueError('HTTP proof request fingerprint mismatch')
        if not projected and item['response_fingerprint'] != response_fingerprint(item['status'], item['response_headers'], raw):
            raise ValueError('HTTP proof response fingerprint mismatch')
        start = dt.datetime.fromisoformat(item['started_at'])
        end = dt.datetime.fromisoformat(item['finished_at'])
        if not (dt.datetime.fromisoformat(report['started_at']) <= start <= end
                <= dt.datetime.fromisoformat(report['finished_at'])):
            raise ValueError('HTTP proof timestamp outside batch')
        request = item['request']
        _check_request_privacy(request)
        _check_sensitive(parsed)
        if request['auth'] != authentication['session_context']:
            raise ValueError('identity proof session context mismatch')
        if projected and (request.get('method') != 'GET' or request.get('path') != '/core/module/sys/logins/account'
                          or not 200 <= item['status'] < 300):
            raise ValueError('identity proof requires successful current account request')
        if request.get('role') != report['role'] or request.get('organization') != report['organization']:
            raise ValueError('HTTP proof principal scope mismatch')
        return item, parsed
    for key in observations:
        item, _ = observed(key)
        if not isinstance(item['status'], int) or isinstance(item['status'], bool) or not 100 <= item['status'] <= 599:
            raise ValueError('HTTP proof invalid response status')
        if key != 'operation' and not key.startswith('restore_'):
            validate_read_request(item['request'])
    for phase in ('identity_before', 'identity_after'):
        observed(phase)
    op, _ = observed('operation')
    if op['request_fingerprint'] != case['request_fingerprint'] or op['response_fingerprint'] != case['response_fingerprint']:
        raise ValueError('HTTP proof operation mismatch')
    if op['request']['method'].upper() != case['route']['method'].upper() or op['request']['path'] != case['path']:
        raise ValueError('HTTP proof route mismatch')
    audit = payload['audit']
    expected = audit['expected']
    entity_binding = audit['entity_binding']
    entity_source = op['request'] if entity_binding['source'] == 'request' else json.loads(base64.b64decode(op['body_base64'])) if entity_binding['source'] == 'response' else None
    if entity_source is None:
        raise ValueError('audit proof entity differs from actual business operation')
    raw_entity = at(entity_source, entity_binding['path'])
    audit_entity = raw_entity
    if 'format' in entity_binding:
        formatting = entity_binding['format']
        if (not isinstance(formatting, dict) or set(formatting) != {'prefix', 'convert'}
                or formatting['convert'] != 'string' or not isinstance(formatting['prefix'], str)
                or isinstance(raw_entity, bool) or not isinstance(raw_entity, (str, int, float))):
            raise ValueError('audit proof unsupported entity format')
        audit_entity = formatting['prefix'] + (raw_entity if isinstance(raw_entity, str)
                                               else json.dumps(raw_entity, allow_nan=False))
    if audit_entity != expected.get('entity'):
        raise ValueError('audit proof entity differs from actual business operation')
    if set(expected) != {'entity', 'event', 'principal', 'role', 'organization'} or any(v in (None, '') for v in expected.values()):
        raise ValueError('audit proof needs entity/event/principal/role/organization')
    declared_operation = str(case['route'].get('operation', '')).lower()
    event_for_operation = {'create': 'I', 'update': 'U', 'delete': 'D'}
    if declared_operation in event_for_operation and expected['event'] != event_for_operation[declared_operation]:
        raise ValueError('audit proof event contradicts business operation')
    if expected['event'] not in {'I', 'U', 'D'} or expected['role'] != report['role'] or expected['organization'] != report['organization'] or expected['principal'] != report['principal']:
        raise ValueError('audit proof event/principal scope mismatch')
    fields = audit['fields']
    if not (set(expected) - {'role'}).union({'record_id', 'timestamp'}).issubset(fields):
        raise ValueError('audit proof field mapping incomplete')
    event_values = audit.get('event_values', {'I': 'I', 'U': 'U', 'D': 'D'})
    if set(event_values) != {'I', 'U', 'D'} or len(set(event_values.values())) != 3:
        raise ValueError('audit proof event enum mapping incomplete or ambiguous')
    matched_expected = {**expected, 'event': event_values[expected['event']]}
    def matches(key):
        item, body = observed(key)
        if not 200 <= item['status'] < 300:
            raise ValueError('audit proof read failed')
        return [row for row in _rows(body, audit) if all(at(row, fields[k]) == v for k, v in matched_expected.items() if k in fields)]
    before = matches('audit_before')
    after = matches('audit_after')
    rollback = payload['rollback']
    kind = rollback.get('kind')
    if kind not in {'compensation', 'rejected-write'}:
        raise ValueError('rollback proof must distinguish compensation and rejected-write')
    expected_count = audit['expected_records']
    if not isinstance(expected_count, int) or isinstance(expected_count, bool):
        raise ValueError('audit proof requires exact record count')
    if before or len(after) != expected_count:
        raise ValueError('audit proof requires before absence and exact after presence')
    if kind == 'compensation' and expected_count != 1:
        raise ValueError('positive audit proof requires one new record')
    if kind == 'rejected-write' and expected_count != 0:
        raise ValueError('rejected-write audit proof must demonstrate no new success record')
    ids = []
    for row in after:
        record_time = dt.datetime.fromisoformat(at(row, fields['timestamp']))
        time_policy = audit.get('timestamp_policy', {'precision': 'microseconds'})
        precision = time_policy['precision']
        if record_time.tzinfo is None:
            from zoneinfo import ZoneInfo
            if not time_policy.get('timezone'):
                raise ValueError('audit proof naive timestamp requires declared database timezone')
            record_time = record_time.replace(tzinfo=ZoneInfo(time_policy['timezone']))
        record_time = record_time.astimezone(dt.timezone.utc)
        window_start = dt.datetime.fromisoformat(op['started_at']).astimezone(dt.timezone.utc)
        window_end = dt.datetime.fromisoformat(observations['audit_after']['finished_at']).astimezone(dt.timezone.utc)
        if precision == 'seconds':
            if record_time.microsecond:
                raise ValueError('audit proof timestamp contradicts declared second precision')
            window_start, window_end = window_start.replace(microsecond=0), window_end.replace(microsecond=0)
        elif precision != 'microseconds':
            raise ValueError('audit proof unsupported timestamp precision')
        if not (window_start <= record_time <= window_end):
            raise ValueError('audit proof record timestamp outside operation')
        rid = at(row, fields['record_id'])
        if not rid or rid in ids:
            raise ValueError('audit proof record IDs missing/duplicated')
        ids.append(rid)
        if 'audit_lines' in observations:
            line_http, line_body = observed('audit_lines')
            if not 200 <= line_http['status'] < 300:
                raise ValueError('audit proof line HTTP query failed')
            all_lines = at(line_body, audit['line_records_path'])
            if not isinstance(all_lines, list):
                raise ValueError('audit proof line records must be a list')
            lines = [line for line in all_lines if at(line, audit['line_fields']['record_id']) == rid]
        else:
            lines = at(row, fields['lines'])
        changes = audit['changes']
        if not changes or len(lines) != audit['expected_lines'] or len(lines) != len(changes):
            raise ValueError('audit proof line count mismatch')
        lf = audit['line_fields']
        actual = []
        line_ids = []
        for line in lines:
            lid = at(line, lf['line_id'])
            if not lid or lid in line_ids:
                raise ValueError('audit proof line IDs missing/duplicated')
            line_ids.append(lid)
            actual.append({k: at(line, lf[k]) for k in ('field', 'old', 'new')})
        declared_changes = [{k: change[k] for k in ('field', 'old', 'new')} for change in changes]
        if sorted(actual, key=digest) != sorted(declared_changes, key=digest):
            raise ValueError('audit proof old/new fields mismatch')
    state = payload['state']
    projection_fields = state.get('projection_fields')
    if projection_fields is not None:
        required_fields = {state['entity_field']} | {change.get('state_field', change['field'])
                                                    for change in audit['changes']}
        if (not isinstance(projection_fields, list) or not projection_fields
                or any(not isinstance(field, str) or not field for field in projection_fields)
                or len(set(projection_fields)) != len(projection_fields)
                or not required_fields.issubset(projection_fields)):
            raise ValueError('state proof projection must include entity and all changed fields')
    def state_read(key, expected_state):
        item, body = observed(key)
        if not 200 <= item['status'] < 300 or not isinstance(expected_state, dict) or not expected_state:
            raise ValueError('state proof needs successful HTTP and explicit values')
        values = {p: at(body, p) for p in expected_state}
        if projection_fields is not None:
            records_path = state['records_path']
            rows = values.get(records_path)
            expected_rows = expected_state.get(records_path)
            if (not isinstance(rows, list) or not isinstance(expected_rows, list)
                    or any(not isinstance(row, dict) or set(row) != set(projection_fields) for row in expected_rows)):
                raise ValueError('state proof needs exact declared row projection')
            values[records_path] = [{field: at(row, field) for field in projection_fields} for row in rows]
        if values != expected_state:
            raise ValueError('state proof readback mismatch')
        return values
    before_state = state_read('state_before', state['expected'])
    restored = state_read('state_restored', state['expected'])
    if before_state != restored:
        raise ValueError('restore proof differs from before state')
    state_read('state_after', state['expected_after'])
    records_path = state['records_path']
    entity_field = state['entity_field']
    if records_path not in state['expected'] or records_path not in state['expected_after']:
        raise ValueError('state proof must assert complete entity row projection')
    before_rows = state['expected'][records_path]
    after_rows = state['expected_after'][records_path]
    if not isinstance(before_rows, list) or not isinstance(after_rows, list):
        raise ValueError('state proof entity rows must be lists')
    def entity_rows(rows):
        return [row for row in rows if at(row, entity_field) == raw_entity]
    prior, resulting = entity_rows(before_rows), entity_rows(after_rows)
    if kind == 'compensation':
        event = expected['event']
        if (len(prior), len(resulting)) != {'I': (0, 1), 'U': (1, 1), 'D': (1, 0)}[event]:
            raise ValueError('state proof does not show entity mutation')
        for change in audit['changes']:
            state_field = change.get('state_field', change['field'])
            old = at(prior[0], state_field) if prior else None
            new = at(resulting[0], state_field) if resulting else None
            if 'value_type' in change:
                if change['value_type'] != 'string':
                    raise ValueError('audit proof unsupported state value conversion')
                def string_value(value):
                    if value is None or isinstance(value, str):
                        return value
                    if isinstance(value, bool) or not isinstance(value, (int, float)):
                        raise ValueError('audit proof unsupported state value conversion')
                    return json.dumps(value, allow_nan=False)
                old, new = string_value(old), string_value(new)
            if (old, new) != (change['old'], change['new']):
                raise ValueError('audit proof old/new differs from business state')
    if kind == 'rejected-write':
        if state['expected_after'] != state['expected']:
            raise ValueError('rejected write must preserve exact before projection')
        failure = rollback.get('failure_values')
        failure_status = rollback.get('failure_status')
        if (op['status'] != failure_status or not failure
                or any(at(json.loads(base64.b64decode(op['body_base64'])), p) != v for p, v in failure.items())):
            raise ValueError('rollback proof needs explicit failure status and semantics')
        if state_read('state_after', state['expected']) != before_state:
            raise ValueError('rejected write changed business state')
    elif not 200 <= op['status'] < 300:
        raise ValueError('compensation proof needs successful operation')
    # Every phase is ordered around the actual business stimulus.
    def ended(key): return dt.datetime.fromisoformat(observations[key]['finished_at'])
    def began(key): return dt.datetime.fromisoformat(observations[key]['started_at'])
    for key in ('identity_before', 'state_before', 'audit_before'):
        if ended(key) > began('operation'):
            raise ValueError('HTTP proof before observation occurred after write')
    for key in ('identity_after', 'audit_after', 'state_restored', 'state_after') + (('audit_lines',) if 'audit_lines' in observations else ()):
        if began(key) < ended('operation'):
            raise ValueError('HTTP proof after observation occurred before write')
    restore_keys = [key for key in observations if key.startswith('restore_')]
    if not restore_keys:
        raise ValueError('compensation/fixture restore proof requires executed HTTP restore')
    for key in restore_keys:
        item, _ = observed(key)
        if item['request']['method'].upper() not in {'POST', 'PUT', 'PATCH', 'DELETE'} or not 200 <= item['status'] < 300:
            raise ValueError('restore proof requires successful business HTTP operation')
        if began(key) < ended('identity_after'):
            raise ValueError('identity proof after observation must precede restoration')
        if began(key) < ended('state_after') or ended(key) > began('state_restored'):
            raise ValueError('restore proof must follow after-state and precede restored readback')
    if ended('state_after') > began('state_restored'):
        raise ValueError('rollback proof was observed after restoration')
    return kind


STORE_ENV = 'SUNWAY_HTTP_PROOF_STORE'
REGISTRY_FIELDS = ('artifact_ref', 'sha256', 'storage', 'retention', 'batch_id', 'case_id', 'context_sha256')


def _store_root():
    value = os.environ.get(STORE_ENV)
    if not value:
        raise ValueError('HTTP proof controlled artifact store is not configured')
    root = Path(value)
    if not root.is_absolute() or not root.is_dir() or root.is_symlink() or root.resolve() != root:
        raise ValueError('HTTP proof store must be an absolute real directory without symlinks')
    return root


def _object_path(root, sha):
    if not isinstance(sha, str) or len(sha) != 64 or any(c not in '0123456789abcdef' for c in sha):
        raise ValueError('HTTP proof invalid artifact digest')
    directory = root / 'sha256'
    if directory.is_symlink():
        raise ValueError('HTTP proof store objects directory cannot be a symlink')
    path = directory / sha
    if path.is_symlink():
        raise ValueError('HTTP proof stored object cannot be a symlink')
    return path


def _registry_rows(root):
    registry = root / 'evidence-index.tsv'
    if registry.is_symlink():
        raise ValueError('HTTP proof registry cannot be a symlink')
    if not registry.exists():
        return []
    with registry.open(newline='', encoding='utf-8') as stream:
        reader = csv.DictReader(stream, delimiter='\t')
        if reader.fieldnames != list(REGISTRY_FIELDS):
            raise ValueError('HTTP proof store registry schema mismatch')
        return list(reader)


def _registration(ref, report, case):
    return {'artifact_ref': 'sha256:' + ref['sha256'], 'sha256': ref['sha256'],
            'storage': 'artifact', 'retention': 'permanent', 'batch_id': report['batch_id'],
            'case_id': case['id'], 'context_sha256': digest(context(report, case))}


def _registered_artifact(ref, report, case):
    if ref.get('storage') != 'artifact' or ref.get('artifact_ref') != 'sha256:' + ref['sha256']:
        raise ValueError('HTTP proof local-only artifact is not registered for strict promotion')
    root = _store_root()
    expected = _registration(ref, report, case)
    rows = [row for row in _registry_rows(root) if row['artifact_ref'] == ref['artifact_ref']
            and row['batch_id'] == report['batch_id'] and row['case_id'] == case['id']]
    if rows != [expected]:
        raise ValueError('HTTP proof controlled artifact registration missing, duplicate or incompatible')
    return _object_path(root, ref['sha256'])


def register_pending_report(report):
    """Explicit local publication boundary, never invoked by the HTTP runner.

    The operator-configured store and its TSV registry are trusted publication
    metadata, matching existing storage=artifact/retention vocabulary. Objects
    and registration rows are immutable; raw hashes prove integrity, not server
    authenticity. This only exports proof artifacts and does not promote cases.
    """
    import copy
    import fcntl
    result = copy.deepcopy(report)
    root = _store_root()
    lock_path = root / '.publication.lock'
    if lock_path.is_symlink():
        raise ValueError('HTTP proof store lock cannot be symlink')
    with lock_path.open('a') as lock:
        fcntl.flock(lock, fcntl.LOCK_EX)
        rows = _registry_rows(root)
        for case in result.get('cases', []):
            if not business_write(case):
                continue
            valid, reason = validate_case_proof(result, case, require_registered=False)
            if not valid:
                raise ValueError(reason)
            ref = case['http_write_proof']
            source = Path(ref['path'])
            raw = source.read_bytes()
            if hashlib.sha256(raw).hexdigest() != ref['sha256']:
                raise ValueError('HTTP proof changed during publication')
            target = _object_path(root, ref['sha256'])
            target.parent.mkdir(mode=0o700, exist_ok=True)
            if target.exists():
                if hashlib.sha256(target.read_bytes()).hexdigest() != ref['sha256']:
                    raise ValueError('HTTP proof stored object digest mismatch')
            else:
                fd = os.open(target, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
                with os.fdopen(fd, 'wb') as stream:
                    stream.write(raw)
            entry = _registration(ref, result, case)
            matching = [row for row in rows if row['artifact_ref'] == entry['artifact_ref']
                        and row['batch_id'] == entry['batch_id'] and row['case_id'] == entry['case_id']]
            if matching and matching != [entry]:
                raise ValueError('HTTP proof immutable registration conflict')
            if not matching:
                rows.append(entry)
            case['http_write_proof'] = {'schema': VERSION, 'storage': 'artifact',
                                       'artifact_ref': entry['artifact_ref'], 'sha256': ref['sha256']}
        registry = root / 'evidence-index.tsv'
        temporary = root / ('.registry-' + str(os.getpid()))
        fd = os.open(temporary, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
        with os.fdopen(fd, 'w', newline='', encoding='utf-8') as stream:
            writer = csv.DictWriter(stream, fieldnames=REGISTRY_FIELDS, delimiter='\t')
            writer.writeheader()
            writer.writerows(rows)
        os.replace(temporary, registry)
    result['proof_registration_pending'] = False
    return result


def validate_case_proof(report, case, *, require_registered=True):
    try:
        ref = case['http_write_proof']
        path = _registered_artifact(ref, report, case) if require_registered else Path(ref['path'])
        if path.is_symlink() or not path.is_file():
            raise ValueError('HTTP proof artifact missing or symlink')
        raw = path.read_bytes()
        if report.get('http_write_proof_version') != VERSION:
            raise ValueError('HTTP write proof version missing or incompatible')
        if report.get('harness_semantics_unchanged') is True:
            raise ValueError('read-only golden corpus cannot certify write proof semantics unchanged')
        if case.get('validation_mode') != 'executed':
            raise ValueError('HTTP write proof semantics require executed validation')
        if ref.get('schema') != VERSION or hashlib.sha256(raw).hexdigest() != ref['sha256']:
            raise ValueError('HTTP proof artifact hash mismatch')
        kind = validate_payload(json.loads(raw), report, case)
        if case.get('rollback', {}).get('kind') != kind:
            raise ValueError('HTTP proof rollback kind mismatch')
        return True, 'executed HTTP write proof valid'
    except (KeyError, ValueError, TypeError, IndexError, OSError, AttributeError) as error:
        return False, 'invalid HTTP write proof: ' + str(error)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Export pending HTTP proofs to the explicitly configured controlled store; never promote reports')
    parser.add_argument('--register-pending', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    from evidence_output import guard_output_path
    guard_output_path(args.output)
    exported = register_pending_report(json.loads(args.register_pending.read_text(encoding='utf-8')))
    data = json.dumps(exported, ensure_ascii=True, indent=2).encode()
    descriptor = os.open(args.output, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
    with os.fdopen(descriptor, 'wb') as stream:
        stream.write(data)
