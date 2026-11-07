#!/usr/bin/env python3
"""Repair the two known local organization selectable configurations via HTTP.

Diagnostic dry-run by default. --apply needs a new private journal; --rollback
--apply restores the journaled fields after checking current record identity.
No SQL, permission changes, supplier writes, or strict evidence promotion.

Source: CoreAdminSelectConfigResource inherits GenericResource PUT /{id};
GenericService.save parses b=['', [{id, ...}]]. CoreAdminSelectConfigService
selectAll keeps the first category from DAO order (ID DESC). The original org
configuration uses the inherited CoreOrgResource selectable and id/orgName.
"""
import argparse
from pathlib import Path
import runpy
import sys
from urllib.request import HTTPCookieProcessor, ProxyHandler, build_opener

# Reuse the established private hash-chain journal, wrapper, and local URL guard.
_SHARED = runpy.run_path(str(Path(__file__).with_name('restore-local-menu-i18n.py')))
RepairError = _SHARED['RestoreError']
Journal = _SHARED['Journal']
wrapper = _SHARED['wrapper']
local_url = _SHARED['local_url']
ROOT = '/secure/core/module/admin/select-configs'
TARGET = '/secure/core/module/mdm/orgs/queries/selectable'
ORIGINAL_ID = 2399
CONFLICT_ID = 1497029569951488
FIELDS = ('url', 'valueField', 'textField')
EXPECTED = dict(zip(FIELDS, (TARGET, 'id', 'orgName')))
WRONG = dict(zip(FIELDS, ('/secure/core/module/sys/codes/queries/selectable?codeCategoryId=org',
                         'code', 'codeName')))


def local_session(base_url, timeout=30):
    """HTTP adapter; CLI applies the fixed local port guard before authentication."""
    sys.path.insert(0, str(Path(__file__).resolve().parent))
    from fast_differential_api_runner import Session
    session = Session(base_url, timeout)
    session.opener = build_opener(ProxyHandler({}), HTTPCookieProcessor(session.jar),
                                 _SHARED['RejectRedirects']())
    return session


def call(request, method, path, body=None):
    result = request(method, path, body)
    if result.get('status') != 200:
        raise RepairError('HTTP operation failed')
    return result.get('json')


def inventory(request):
    data = call(request, 'POST', ROOT + '/queries/raw',
                {'p': {'f': {'category_SEQ': 'org'}, 'n': 1, 's': 100}})
    rows = _SHARED['rows'](data)
    if not rows or any(row.get('category') != 'org' for row in rows):
        raise RepairError('invalid organization inventory')
    by_id = {}
    for row in rows:
        identity = int(_SHARED['valid_id'](row.get('id')))
        if identity in by_id:
            raise RepairError('duplicate organization identity')
        by_id[identity] = row
    if set(by_id) != {ORIGINAL_ID, CONFLICT_ID}:
        raise RepairError('unrecognized organization configuration identities')
    # The earliest original is authoritative only for this exact source-backed target.
    original = by_id[min(by_id)]
    if any(original.get(k) != v for k, v in EXPECTED.items()):
        raise RepairError('unrecognized original organization configuration')
    conflict = by_id[CONFLICT_ID]
    actual = {k: conflict.get(k) for k in FIELDS}
    if actual not in (EXPECTED, WRONG):
        raise RepairError('unrecognized conflicting organization configuration')
    if any(not isinstance(r.get(k), str) for r in rows for k in ('name', 'cacheable')):
        raise RepairError('incomplete organization identity')
    return by_id


def verify_target(request):
    data = call(request, 'GET', TARGET + '?sampleDepartment_SEQ=1')
    if (not isinstance(data, list) or not data or
            any(not isinstance(row, dict) or row.get('id') is None or
                not isinstance(row.get('orgName'), str) or not row['orgName'] for row in data)):
        raise RepairError('organization target did not return real selectable rows')
    return len(data)


def verify_effective(request):
    data = call(request, 'GET', ROOT + '/queries/selectable')
    if not isinstance(data, list) or any(not isinstance(row, dict) for row in data):
        raise RepairError('invalid effective selectable configuration')
    org = [row for row in data if row.get('category') == 'org']
    if (len(org) != 1 or str(org[0].get('id')) != str(CONFLICT_ID) or
            any(org[0].get(k) != v for k, v in EXPECTED.items())):
        raise RepairError('effective organization configuration not repaired')
    return verify_target(request)


def repair(request, journal, apply=False):
    before = inventory(request)
    current = before[CONFLICT_ID]
    planned = int(any(current[k] != v for k, v in EXPECTED.items()))
    result = {'strict_evidence': False, 'mode': 'apply' if apply else 'dry-run',
              'planned': planned, 'updated': 0}
    # Retain sampleDepartment_SEQ; failure here must prevent all writes.
    result['target_rows'] = verify_target(request)
    if not apply:
        return result
    with Journal(journal, create=True) as log:
        log.add('start', tool='org-select-config-v1', original_id=ORIGINAL_ID,
                conflict_id=CONFLICT_ID)
        try:
            if planned:
                if inventory(request) != before:
                    raise RepairError('inventory changed before update')
                after = dict(current, **EXPECTED)
                log.add('update-intent', id=CONFLICT_ID, before=current, after=after)
                # Only the three broken fields are updated; all other columns stay intact.
                call(request, 'PUT', ROOT + '/' + str(CONFLICT_ID),
                     wrapper(dict(id=current['id'], **EXPECTED)))
                verified = inventory(request)
                if verified != {ORIGINAL_ID: before[ORIGINAL_ID], CONFLICT_ID: after}:
                    raise RepairError('updated configuration not verified')
                log.add('update-verified', id=CONFLICT_ID)
                result['updated'] = 1
            result['target_rows'] = verify_effective(request)
            log.add('complete', updated=result['updated'])
        except Exception:
            log.add('failed', reason='operation-or-verification-failed')
            raise RepairError('repair failed; private journal preserved for rollback') from None
    return result


def rollback(request, journal, apply=False):
    with Journal(journal) as log:
        if not log.records or log.records[0].get('tool') != 'org-select-config-v1':
            raise RepairError('journal belongs to another operation')
        intents = [r for r in log.records if r['event'] == 'update-intent']
        if len(intents) > 1:
            raise RepairError('ambiguous repair journal')
        result = {'strict_evidence': False, 'mode': 'rollback' if apply else 'rollback-dry-run',
                  'restored': 0, 'rollback_complete': False}
        if not intents:
            result['rollback_complete'] = True
            return result
        intent = intents[0]
        before, after = intent['before'], intent['after']
        current = inventory(request)[CONFLICT_ID]
        if (intent.get('id') != CONFLICT_ID or before.get('id') != current.get('id') or
                any(before.get(k) != WRONG[k] for k in FIELDS) or
                after != dict(before, **EXPECTED) or current not in (before, after)):
            raise RepairError('rollback current identity or fields changed')
        if not apply:
            return result
        try:
            if current != before:
                log.add('rollback-intent', id=CONFLICT_ID)
                call(request, 'PUT', ROOT + '/' + str(CONFLICT_ID),
                     wrapper(dict(id=before['id'], **{k: before[k] for k in FIELDS})))
            if inventory(request)[CONFLICT_ID] != before:
                raise RepairError('rollback fields not verified')
            result.update(restored=int(current != before), rollback_complete=True)
            log.add('rollback-verified', id=CONFLICT_ID)
        except Exception:
            log.add('rollback-failed', reason='operation-or-verification-failed')
            raise RepairError('rollback failed; private journal preserved') from None
        return result


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--base-url', default='http://127.0.0.1:18087')
    parser.add_argument('--journal', type=Path, required=True)
    parser.add_argument('--user', required=True)
    parser.add_argument('--password-file', type=Path, required=True)
    parser.add_argument('--apply', action='store_true')
    parser.add_argument('--rollback', action='store_true')
    args = parser.parse_args(argv)
    try:
        base_url = local_url(args.base_url)
        from fast_differential_api_runner import read_password
        session = local_session(base_url)
        if session.login(args.user, read_password(args.password_file)).get('status') != 200:
            raise RepairError('local authentication failed')
        operation = rollback if args.rollback else repair
        result = operation(session.request, args.journal, apply=args.apply)
        import json
        print(json.dumps(result, sort_keys=True))
        return 0
    except Exception:
        print('{"strict_evidence": false, "status": "failed", '
              '"detail": "operation failed; inspect private journal"}', file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
