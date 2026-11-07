#!/usr/bin/env python3
"""Source-shaped audit and state declarations through validate_payload."""
import json
from pathlib import Path
import sys
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from http_write_proof import CONTEXT_FIELDS, VERSION, IDENTITY_ADAPTER, IDENTITY_SELECTORS, context, transcript, validate_payload


class SourceShapesTest(unittest.TestCase):
    def response(self, payload, phase, body):
        observed = payload['observations'][phase]
        payload['observations'][phase] = transcript(observed['request'], observed['status'],
            observed['response_headers'], json.dumps(body).encode(), observed['started_at'], observed['finished_at'])

    def proof(self):
        report = {key: key + '-test' for key in CONTEXT_FIELDS}
        report.update(role='manager', organization='org-1', principal='manager',
                      authentication={'authenticated': True},
                      started_at='2026-11-07T00:00:00+00:00',
                      finished_at='2026-11-07T00:00:20+00:00')
        case = {key: report[key] for key in ('batch_id', 'application_binding', 'harness_binding', 'runtime_contract_binding')}
        case.update(id='seal-rule-update', route={'method': 'POST', 'operation': 'update'}, path='/seal-rule/update',
                    audit={'required': True, 'status': 'observed', 'source': 'executed-http-proof'},
                    rollback={'status': 'observed'}, fixture={'state_restore_proof': 'executed-http-proof'})

        def observation(method, path, operation, body, second, request_body=None):
            request = {'method': method, 'path': path, 'operation': operation, 'body': request_body,
                       'headers': {}, 'auth': {'cookies': {'SESSION': 'a' * 64}}, 'role': 'manager', 'organization': 'org-1'}
            if path == '/core/module/sys/logins/account':
                request['_identity_projection'] = {'adapter': IDENTITY_ADAPTER, 'selectors': IDENTITY_SELECTORS}
            instant = '2026-11-07T00:00:%02d+00:00' % second
            return transcript(request, 200, {'content-type': 'application/json'},
                              json.dumps(body).encode(), instant, instant)

        before = {'rows': [{'id': 71, 'name': 'before'}]}
        after = {'rows': [{'id': 71, 'name': 'after'}]}
        record = {'id': 81, 'entity': 'T_EMC_SEAL_RULE$71', 'event': 'UPDATE',
                  'principal': 'manager', 'role': 'manager', 'org': 'org-1',
                  'at': '2026-11-07T00:00:03+00:00',
                  'lines': [{'id': 91, 'field': 'name', 'old': 'before', 'new': 'after'}]}
        identity_body = {'id': 'manager', 'orgId': 'org-1',
                         'ext$': {'loginorgid': 'org-1', 'roles': '[{"roleId":"manager","userId":"manager"}]'}}
        observations = {
            'identity_before': observation('GET', '/core/module/sys/logins/account', 'read', identity_body, 0),
            'state_before': observation('GET', '/seal-rule/queries', 'read', before, 1),
            'audit_before': observation('GET', '/audit/queries', 'read', {'rows': []}, 2),
            'operation': observation('POST', '/seal-rule/update', 'update', {'ok': True}, 3,
                                     {'id': 71, 'name': 'after'}),
            'audit_after': observation('GET', '/audit/queries', 'read', {'rows': [record]}, 4),
            'state_after': observation('GET', '/seal-rule/queries', 'read', after, 5),
            'restore_0': observation('POST', '/seal-rule/update', 'update', {'ok': True}, 6,
                                     {'id': 71, 'name': 'before'}),
            'state_restored': observation('GET', '/seal-rule/queries', 'read', before, 7),
            'identity_after': observation('GET', '/core/module/sys/logins/account', 'read', identity_body, 5),
        }
        identity = observations['identity_before']
        report['authentication'].update(identity=identity['response_projection'],
                                         projection_metadata=identity['projection_metadata'],
                                         response_fingerprint=identity['response_fingerprint'],
                                         session_context=identity['request']['auth'])
        case.update({key: observations['operation'][key] for key in ('request_fingerprint', 'response_fingerprint')})
        audit = {'entity_binding': {'source': 'request', 'path': 'body.id',
                                    'format': {'prefix': 'T_EMC_SEAL_RULE$', 'convert': 'string'}},
                 'expected': {'entity': 'T_EMC_SEAL_RULE$71', 'event': 'U', 'principal': 'manager',
                              'role': 'manager', 'organization': 'org-1'},
                 'fields': {'entity': 'entity', 'event': 'event', 'principal': 'principal', 'role': 'role',
                            'organization': 'org', 'record_id': 'id', 'timestamp': 'at', 'lines': 'lines'},
                 'event_values': {'I': 'INSERT', 'U': 'UPDATE', 'D': 'DELETE'},
                 'line_fields': {'line_id': 'id', 'field': 'field', 'old': 'old', 'new': 'new'},
                 'expected_records': 1, 'expected_lines': 1,
                 'changes': [{'field': 'name', 'old': 'before', 'new': 'after'}]}
        payload = {'schema': VERSION, 'context': context(report, case), 'observations': observations, 'audit': audit,
                   'state': {'records_path': 'rows', 'entity_field': 'id', 'expected': before, 'expected_after': after},
                   'rollback': {'kind': 'compensation'}}
        return payload, report, case

    def test_numeric_business_entity_binds_prefixed_audit_target(self):
        self.assertEqual(validate_payload(*self.proof()), 'compensation')

    def test_audit_column_name_maps_to_business_state_field(self):
        payload, report, case = self.proof()
        payload['audit']['changes'][0].update(field='NAME', state_field='name')
        audit_http = payload['observations']['audit_after']
        body = {'rows': [{'id': 81, 'entity': 'T_EMC_SEAL_RULE$71', 'event': 'UPDATE',
                         'principal': 'manager', 'role': 'manager', 'org': 'org-1',
                         'at': '2026-11-07T00:00:03+00:00',
                         'lines': [{'id': 91, 'field': 'NAME', 'old': 'before', 'new': 'after'}]}]}
        payload['observations']['audit_after'] = transcript(audit_http['request'], 200,
            audit_http['response_headers'], json.dumps(body).encode(), audit_http['started_at'], audit_http['finished_at'])
        self.assertEqual(validate_payload(payload, report, case), 'compensation')

    def test_numeric_state_values_match_declared_string_audit_values(self):
        payload, report, case = self.proof()
        payload['audit']['changes'] = [{'field': 'COUNT', 'state_field': 'count',
                                        'value_type': 'string', 'old': '1', 'new': '2'}]
        payload['state']['expected'] = {'rows': [{'id': 71, 'count': 1}]}
        payload['state']['expected_after'] = {'rows': [{'id': 71, 'count': 2}]}
        for phase, count in [('state_before', 1), ('state_after', 2), ('state_restored', 1)]:
            self.response(payload, phase, {'rows': [{'id': 71, 'count': count}]})
        self.response(payload, 'audit_after', {'rows': [{'id': 81, 'entity': 'T_EMC_SEAL_RULE$71', 'event': 'UPDATE',
            'principal': 'manager', 'role': 'manager', 'org': 'org-1', 'at': '2026-11-07T00:00:03+00:00',
            'lines': [{'id': 91, 'field': 'COUNT', 'old': '1', 'new': '2'}]}]})
        self.assertEqual(validate_payload(payload, report, case), 'compensation')

    def test_explicit_state_projection_excludes_dynamic_response_fields(self):
        payload, report, case = self.proof()
        payload['state']['projection_fields'] = ['id', 'name']
        for phase, name, dynamic in [('state_before', 'before', 1), ('state_after', 'after', 2),
                                     ('state_restored', 'before', 3)]:
            self.response(payload, phase, {'rows': [{'id': 71, 'name': name, 'updatedAt': dynamic,
                                                    'ext$': {'display': 'generated'}}]})
        self.assertEqual(validate_payload(payload, report, case), 'compensation')

    def test_wrong_target_entity_or_unsupported_format_is_refused(self):
        for formatting, entity in [({'prefix': 'OTHER$', 'convert': 'string'}, 'T_EMC_SEAL_RULE$71'),
                                   ({'prefix': 'T_EMC_SEAL_RULE$', 'convert': 'eval'}, 'T_EMC_SEAL_RULE$71'),
                                   ({'prefix': 'T_EMC_SEAL_RULE$', 'convert': 'string', 'ignore': True}, 'T_EMC_SEAL_RULE$71'),
                                   ({'prefix': 'T_EMC_SEAL_RULE$', 'convert': 'string'}, 'T_EMC_SEAL_RULE$72')]:
            with self.subTest(formatting=formatting, entity=entity):
                payload, report, case = self.proof()
                payload['audit']['entity_binding']['format'] = formatting
                payload['audit']['expected']['entity'] = entity
                with self.assertRaises(ValueError):
                    validate_payload(payload, report, case)

    def test_state_must_match_raw_business_entity(self):
        payload, report, case = self.proof()
        for phase, name in [('state_before', 'before'), ('state_after', 'after'), ('state_restored', 'before')]:
            self.response(payload, phase, {'rows': [{'id': 'T_EMC_SEAL_RULE$71', 'name': name}]})
        payload['state']['expected'] = {'rows': [{'id': 'T_EMC_SEAL_RULE$71', 'name': 'before'}]}
        payload['state']['expected_after'] = {'rows': [{'id': 'T_EMC_SEAL_RULE$71', 'name': 'after'}]}
        with self.assertRaisesRegex(ValueError, 'entity mutation'):
            validate_payload(payload, report, case)

    def test_projection_cannot_omit_id_or_changed_state_field(self):
        for fields in [['id'], ['name'], ['id', 'name', 'name'], 'id,name', []]:
            with self.subTest(fields=fields):
                payload, report, case = self.proof()
                payload['state']['projection_fields'] = fields
                with self.assertRaisesRegex(ValueError, 'projection'):
                    validate_payload(payload, report, case)

    def test_projection_preserves_exact_declared_values_and_fields(self):
        for mutation in ['wrong-value', 'missing-field', 'extra-expected-field']:
            with self.subTest(mutation=mutation):
                payload, report, case = self.proof()
                payload['state']['projection_fields'] = ['id', 'name']
                if mutation == 'extra-expected-field':
                    payload['state']['expected']['rows'][0]['extra'] = 'undeclared'
                else:
                    row = {'id': 71, 'updatedAt': 'dynamic'}
                    if mutation == 'wrong-value':
                        row['name'] = 'wrong'
                    self.response(payload, 'state_before', {'rows': [row]})
                with self.assertRaises((ValueError, KeyError)):
                    validate_payload(payload, report, case)

    def test_dynamic_fields_require_explicit_projection(self):
        payload, report, case = self.proof()
        self.response(payload, 'state_before', {'rows': [{'id': 71, 'name': 'before', 'updatedAt': 'dynamic'}]})
        with self.assertRaisesRegex(ValueError, 'readback mismatch'):
            validate_payload(payload, report, case)

    def test_unsupported_value_conversion_is_refused(self):
        payload, report, case = self.proof()
        payload['audit']['changes'][0]['value_type'] = 'eval'
        with self.assertRaisesRegex(ValueError, 'unsupported state value conversion'):
            validate_payload(payload, report, case)


if __name__ == '__main__':
    unittest.main()
