#!/usr/bin/env python3
"""Repair exact source-backed stale menu hrefs through local HTTP only.

Default: diagnostic dry-run. --apply creates a new private hash-chain journal;
--rollback reuses it to restore only this tool's old hrefs. No strict evidence.
The journal stores approved paths/queries and hashes, never menu bodies or auth.
"""
import argparse
import importlib.util
import json
from pathlib import Path
import re
import sys
from urllib.error import HTTPError
from urllib.request import Request, build_opener, HTTPCookieProcessor, ProxyHandler

_helper_spec = importlib.util.spec_from_file_location(
    'local_menu_metadata', Path(__file__).with_name('restore-local-menu-i18n.py'))
_helper = importlib.util.module_from_spec(_helper_spec)
_helper_spec.loader.exec_module(_helper)
Journal = _helper.Journal
RestoreError = _helper.RestoreError
RejectRedirects = _helper.RejectRedirects
local_url = _helper.local_url
wrapper = _helper.wrapper
digest = _helper.digest
valid_id = _helper.valid_id
call = _helper.call
ROOT = '/secure/core/module/sys/menus'

# Controllers + original template initializers are the source of truth. No
# normalization, aliases, project-run-list, or invented generic pages.
TARGETS = {}
INITIALIZERS = {}
for family, name in (('file-modifications', 'emcFileModification'),
                     ('equipt/equip-checks', 'emcEquipCheck'),
                     ('equipt/equip-verifications', 'emcEquipVerification')):
    for page, kind in (('edit-list', 'editPage'), ('audit-list', 'auditPage'),
                       ('search-list', 'searchPage')):
        target = '/secure/emc/module/mdm/' + family + '/page/' + page
        TARGETS[target.replace('/emc/', '/emc/dingan/', 1)] = target
        INITIALIZERS[target] = name + '.' + kind + '.init'
for page, kind in (('appearance-list', 'editPage'), ('check-list', 'choosePage')):
    target = '/secure/emc/module/mdm/equipt/equip-checks/page/' + page
    TARGETS[target.replace('/emc/', '/emc/dingan/', 1)] = target
    INITIALIZERS[target] = 'emcEquipAppearance.' + kind + '.init'
target = '/secure/emc/module/mdm/basemdm/mtl-use-records/page/edit-list'
TARGETS[target.replace('/emc/', '/emc/dingan/', 1)] = target
INITIALIZERS[target] = 'emcMtlUseRecord.editPage.init'
for family, page, initializer in (
        ('les-dses', 'edit-list', 'coreLesDs.editPage.init'),
        ('les-templates', 'edit-list', 'coreLesTemplate.editListPage.init'),
        ('les-templates', 'audit-list', 'coreLesTemplate.auditListPage.init')):
    target = '/secure/core/les/' + family + '/page/' + page
    TARGETS['/secure/core/module/item/' + family + '/page/' + page] = target
    INITIALIZERS[target] = initializer
target = '/secure/core/module/sys/audit/audit-logs/page/search-list'
TARGETS['/secure/core/audit/audit-logs/page/search-list'] = target
INITIALIZERS[target] = 'coreAuditLog.searchPage.init'


def replacement(href):
    if not isinstance(href, str):
        return None
    path, question, query = href.partition('?')
    if path not in TARGETS:
        return None
    # The audited menu query is a workflow code, not arbitrary user data.
    if question and query not in {
            'bpmn_statusCode=fileAudit', 'bpmn_statusCode=fileConfirm',
            'bpmn_statusCode=equiptCheck', 'bpmn_statusCode=equiptVerification'}:
        raise RestoreError('unapproved menu query')
    return TARGETS[path] + (question + query if question else '')


def inventory(request):
    found, seen, expected, page = [], set(), None, 1
    while True:
        # Default ORDERNO sort is nonunique; ID order prevents page overlap.
        result = call(request, 'POST', ROOT + '/queries/raw',
                      {'p': {'n': page, 's': 100, 'o': [{'id': 'asc'}]}})
        if (not isinstance(result, dict) or not isinstance(result.get('rows'), list)
                or type(result.get('total')) is not int or result['total'] < 0):
            raise RestoreError('invalid menu inventory')
        if expected is None:
            expected = result['total']
        if result['total'] != expected:
            raise RestoreError('menu inventory changed')
        for row in result['rows']:
            if not isinstance(row, dict):
                raise RestoreError('invalid menu row')
            identity = str(valid_id(row.get('id')))
            if identity in seen:
                raise RestoreError('duplicate menu identity')
            seen.add(identity)
            found.append(row)
        if len(found) == expected:
            return found
        if len(found) > expected or not result['rows']:
            raise RestoreError('incomplete menu inventory')
        page += 1


def detail(request, identity):
    valid_id(identity)
    row = call(request, 'GET', ROOT + '/' + str(identity), None)
    if not isinstance(row, dict) or str(row.get('id')) != str(identity):
        raise RestoreError('menu identity drift')
    return row


def request_with_fragments(session):
    """Session observation plus full text only for fixed original page targets."""
    def request(method, path, body=None):
        if method != 'GET' or path.partition('?')[0] not in INITIALIZERS:
            return session.request(method, path, body)
        # Uses the authenticated no-redirect local opener. Never logs payload.
        try:
            with session.opener.open(Request(session.base_url + path), timeout=session.timeout) as response:
                payload = response.read()
                result = session._observation(response.status, response.headers, payload)
                result['text'] = payload.decode('utf-8', errors='replace')
                return result
        except HTTPError as error:
            error.close()
            raise RestoreError('target fragment HTTP failed') from None
    return request


def preflight(request, href):
    path = href.partition('?')[0]
    response = request('GET', href, None)
    text = response.get('text', response.get('text_prefix', ''))
    initializer = INITIALIZERS[path]
    if (response.get('status') != 200 or response.get('content_type') != 'text/html'
            or 'json' in response or not isinstance(text, str)
            or not re.search(r'<script\b', text, re.I)
            or not re.search(re.escape(initializer) + r'\s*\(', text)):
        raise RestoreError('target original page fragment not verified')


def repair(request, journal, apply=False):
    rows = inventory(request)
    plans = []
    for row in rows:
        target = replacement(row.get('href'))
        if target:
            current = detail(request, row['id'])
            if current.get('href') != row['href']:
                raise RestoreError('menu href changed since inventory')
            preflight(request, target)
            plans.append((current, target))
    result = {'strict_evidence': False, 'mode': 'apply' if apply else 'dry-run',
              'inventory': len(rows), 'planned': len(plans), 'updated': 0}
    if not apply:
        return result
    with Journal(journal, create=True) as log:
        log.add('start', tool='repair-local-menu-hrefs-v1', inventory=len(rows))
        try:
            for original, target in plans:
                identity = original['id']
                current = detail(request, identity)
                if current != original:
                    raise RestoreError('menu changed before apply')
                preflight(request, target)
                log.add('update-intent', id=identity, before_href=original['href'],
                        after_href=target, before_href_sha256=digest(original['href']),
                        nonhref_sha256=digest({k: v for k, v in original.items() if k != 'href'}))
                # GenericResource.save -> GenericService.save ->
                # MybatisDaoSupport.updateIfChanged clones the old row and applies
                # supplied changed/non-null fields. Submit only id + href, then
                # independently verify every other detail field survived.
                call(request, 'PUT', ROOT + '/' + str(identity), wrapper({'id': identity, 'href': target}))
                log.add('update-returned', id=identity)
                if detail(request, identity) != dict(original, href=target):
                    raise RestoreError('menu readback mismatch')
                log.add('update-verified', id=identity)
                result['updated'] += 1
            log.add('complete', updated=result['updated'])
        except Exception:
            log.add('failed', reason='operation-or-verification-failed')
            raise RestoreError('repair failed; inspect private journal and rollback owned changes') from None
    return result


def rollback(request, journal, apply=False):
    with Journal(journal) as log:
        if (not log.records or log.records[0].get('event') != 'start'
                or log.records[0].get('tool') != 'repair-local-menu-hrefs-v1'):
            raise RestoreError('unknown journal')
        intents = {}
        done = set()
        for record in log.records:
            if record['event'] == 'update-intent':
                identity = str(valid_id(record.get('id')))
                if (identity in intents or replacement(record.get('before_href')) != record.get('after_href')
                        or record.get('before_href_sha256') != digest(record['before_href'])
                        or not re.fullmatch(r'[0-9a-f]{64}', record.get('nonhref_sha256', ''))):
                    raise RestoreError('invalid repair intent')
                intents[identity] = record
            if record['event'] == 'rollback-verified':
                identity = str(valid_id(record.get('id')))
                if identity not in intents:
                    raise RestoreError('unknown rollback identity')
                done.add(identity)
        pending = [r for identity, r in intents.items() if identity not in done]
        result = {'strict_evidence': False, 'mode': 'rollback' if apply else 'rollback-dry-run',
                  'pending': len(pending), 'restored': 0, 'rollback_complete': False}
        if not apply:
            return result
        try:
            for record in reversed(pending):
                identity = record['id']
                current = detail(request, identity)
                if digest({k: v for k, v in current.items() if k != 'href'}) != record['nonhref_sha256']:
                    raise RestoreError('rollback non-href metadata changed')
                if current.get('href') not in (record['before_href'], record['after_href']):
                    raise RestoreError('rollback href changed by another writer')
                if current['href'] == record['after_href']:
                    log.add('rollback-intent', id=identity)
                    call(request, 'PUT', ROOT + '/' + str(identity),
                         wrapper({'id': identity, 'href': record['before_href']}))
                    log.add('rollback-returned', id=identity)
                if detail(request, identity) != dict(current, href=record['before_href']):
                    raise RestoreError('rollback readback mismatch')
                log.add('rollback-verified', id=identity)
                result['restored'] += 1
            result['rollback_complete'] = True
            log.add('rollback-finished', restored=result['restored'])
        except Exception:
            log.add('rollback-failed', reason='operation-or-verification-failed')
            raise RestoreError('rollback failed; private journal preserved') from None
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
        sys.path.insert(0, str(Path(__file__).resolve().parent))
        from fast_differential_api_runner import Session, read_password
        session = Session(base_url, 30)
        session.opener = build_opener(ProxyHandler({}), HTTPCookieProcessor(session.jar), RejectRedirects())
        if session.login(args.user, read_password(args.password_file)).get('status') != 200:
            raise RestoreError('local authentication failed')
        request = request_with_fragments(session)
        result = (rollback if args.rollback else repair)(request, args.journal, apply=args.apply)
        print(json.dumps(result, sort_keys=True))
        return 0
    except Exception:
        print(json.dumps({'strict_evidence': False, 'status': 'failed',
                          'detail': 'operation failed; inspect private journal'}), file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
