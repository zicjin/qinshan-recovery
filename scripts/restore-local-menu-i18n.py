#!/usr/bin/env python3
"""Restore approved original menu translations through local application HTTP APIs.

Default is diagnostic dry-run. Input is a private JSON list of
{code, description, defaultMessage, messages: {locale: original_translation}}.
Journal contains only hashes, operation identities and created IDs, never messages.
Use --rollback --apply with the same journal to delete only journal-owned keys.
This tool does not produce strict evidence or restore production completeness.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import sys
import stat
from urllib.request import HTTPRedirectHandler, HTTPCookieProcessor, ProxyHandler, build_opener
from urllib.parse import urlsplit

ROOT = '/secure/core/module/sys/i18ns'
MESSAGE_ROOT = '/secure/core/module/sys/i18n-messages'


class RestoreError(RuntimeError):
    pass


class RejectRedirects(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        raise RestoreError('redirects are not allowed for local metadata restore')


def digest(value):
    return hashlib.sha256(json.dumps(value, ensure_ascii=False, sort_keys=True,
                                     separators=(',', ':')).encode()).hexdigest()


def validate_entries(entries):
    if not isinstance(entries, list) or not entries:
        raise RestoreError('input must be a nonempty list')
    seen = set()
    for entry in entries:
        if not isinstance(entry, dict) or set(entry) != {'code', 'description', 'defaultMessage', 'messages'}:
            raise RestoreError('invalid input fields')
        code = entry['code']
        if not isinstance(code, str) or not re.fullmatch(r'CORE\.MENU\.[A-Z0-9_.-]+', code) or code in seen:
            raise RestoreError('invalid or duplicate menu code')
        seen.add(code)
        if not all(isinstance(entry[x], str) for x in ('description', 'defaultMessage')):
            raise RestoreError('invalid original metadata')
        messages = entry['messages']
        if not isinstance(messages, dict) or not messages or not all(
                isinstance(k, str) and re.fullmatch(r'[a-zA-Z]{2,3}-[a-zA-Z]{2,4}', k)
                and isinstance(v, str) and v for k, v in messages.items()):
            raise RestoreError('invalid original translations')
        if len({k.casefold() for k in messages}) != len(messages):
            raise RestoreError('duplicate locale')


def call(request, method, path, body):
    response = request(method, path, body)
    if response.get('status') != 200:
        raise RestoreError('HTTP operation failed')
    return response.get('json')


def rows(response):
    if not isinstance(response, dict) or not isinstance(response.get('rows'), list):
        raise RestoreError('expected paginated rows')
    data = response['rows']
    if not all(isinstance(r, dict) for r in data):
        raise RestoreError('invalid rows')
    if 'total' not in response or int(response['total']) != len(data):
        raise RestoreError('incomplete inventory page')
    return data


def lookup(request, code):
    result = rows(call(request, 'POST', ROOT + '/queries/raw',
                       {'p': {'f': {'code_SEQ': code}, 'n': 1, 's': 100}}))
    if any(r.get('code') != code for r in result) or len(result) > 1:
        raise RestoreError('ambiguous code inventory')
    return result


def restore(request, entries, journal, apply=False, source_sha256=None):
    validate_entries(entries)
    existing = [bool(lookup(request, e['code'])) for e in entries]
    result = {'strict_evidence': False, 'mode': 'apply' if apply else 'dry-run',
              'source_sha256': source_sha256 or digest(entries),
              'requested': len(entries), 'preexisting': sum(existing),
              'missing': len(entries) - sum(existing), 'created': 0}
    if not apply:
        return result
    with Journal(journal, create=True) as log:
        log.add('start', source_sha256=result['source_sha256'])
        try:
            for entry, present in zip(entries, existing):
                if present:
                    continue
                code = entry['code']
                # Recheck immediately before writing; existing rows are never changed.
                if lookup(request, code):
                    raise RestoreError('inventory changed before apply')
                log.add('create-intent', code=code, entry_sha256=digest(entry))
                identity = call(request, 'POST', ROOT, wrapper({
                    k: entry[k] for k in ('code', 'description', 'defaultMessage')}))
                identity = valid_id(identity)
                log.add('created', code=code, id=identity)
                found = lookup(request, code)
                if len(found) != 1 or str(found[0].get('id')) != str(identity):
                    raise RestoreError('created key not verified')
                messages = message_rows(request, identity)
                by_locale = {}
                for message in messages:
                    locale = str(message.get('i18nConfigId', '')).casefold()
                    if not locale or locale in by_locale or str(message.get('i18nId')) != str(identity):
                        raise RestoreError('invalid created message inventory')
                    valid_id(message.get('id'))
                    by_locale[locale] = message
                if not set(k.casefold() for k in entry['messages']).issubset(by_locale):
                    raise RestoreError('required locale unavailable')
                for locale, text in entry['messages'].items():
                    message_id = by_locale[locale.casefold()]['id']
                    log.add('message-intent', code=code, id=identity, message_id=message_id,
                            locale=locale, translation_sha256=digest(text))
                    call(request, 'PUT', MESSAGE_ROOT + '/' + str(message_id),
                         wrapper({'id': message_id, 'message': text}))
                    log.add('message-written', id=identity, message_id=message_id)
                verified = {str(m.get('i18nConfigId', '')).casefold(): m.get('message')
                            for m in message_rows(request, identity)}
                if any(verified.get(k.casefold()) != v for k, v in entry['messages'].items()):
                    raise RestoreError('translation verification failed')
                log.add('verified', code=code, id=identity)
                result['created'] += 1
            log.add('complete', created=result['created'])
        except Exception:
            log.add('failed', reason='operation-or-verification-failed')
            raise RestoreError('restore failed; inspect private journal and rollback created records') from None
    return result


def valid_id(value):
    if isinstance(value, bool) or not isinstance(value, (int, str)) or not re.fullmatch(r'[1-9][0-9]*', str(value)):
        raise RestoreError('missing or invalid returned identity')
    return value


def wrapper(row):
    return {'b': ['', [row]], 'p': {}}


def message_rows(request, identity):
    return rows(call(request, 'POST', ROOT + '/' + str(identity) + '/i18n-messages/queries',
                     {'p': {'n': 1, 's': 100}}))


class Journal:
    """Exclusive private file; each fsynced append hashes its immutable predecessor."""
    def __init__(self, path, create=False):
        self.path = Path(path)
        self.create = create
        self.records = []

    def __enter__(self):
        import fcntl
        flags = os.O_WRONLY | os.O_APPEND | os.O_NOFOLLOW
        flags |= (os.O_CREAT | os.O_EXCL) if self.create else 0
        self.fd = os.open(self.path, flags, 0o600)
        try:
            fcntl.flock(self.fd, fcntl.LOCK_EX | fcntl.LOCK_NB)
            if not stat.S_ISREG(os.fstat(self.fd).st_mode) or os.fstat(self.fd).st_mode & 0o077:
                raise RestoreError('journal must be private')
            if not self.create:
                for line in self.path.read_text().splitlines():
                    record = json.loads(line)
                    supplied = record.pop('record_sha256')
                    expected_previous = self.records[-1]['record_sha256'] if self.records else ''
                    if record.get('previous_sha256') != expected_previous or digest(record) != supplied:
                        raise RestoreError('journal integrity failure')
                    record['record_sha256'] = supplied
                    self.records.append(record)
            return self
        except Exception:
            os.close(self.fd)
            raise

    def add(self, event, **fields):
        record = dict(fields, event=event, sequence=len(self.records),
                      previous_sha256=self.records[-1]['record_sha256'] if self.records else '')
        record['record_sha256'] = digest(record)
        data = (json.dumps(record, sort_keys=True) + '\n').encode()
        written = os.write(self.fd, data)
        if written != len(data):
            raise RestoreError('journal append incomplete')
        os.fsync(self.fd)
        self.records.append(record)

    def __exit__(self, *args):
        os.close(self.fd)


def rollback(request, journal, apply=False):
    with Journal(journal) as log:
        created = {r['code']: r['id'] for r in log.records if r['event'] == 'created'}
        intents = {r['code'] for r in log.records if r['event'] == 'create-intent'}
        deleted = {str(r['id']) for r in log.records if r['event'] == 'deleted-verified'}
        pending = [(code, identity) for code, identity in created.items() if str(identity) not in deleted]
        result = {'strict_evidence': False, 'mode': 'rollback' if apply else 'rollback-dry-run',
                  'pending': len(pending), 'deleted': 0, 'unresolved': len(intents - set(created)),
                  'rollback_complete': False}
        if not apply:
            return result
        try:
            for code, identity in pending:
                valid_id(identity)
                found = lookup(request, code)
                if found:
                    if str(found[0].get('id')) != str(identity):
                        raise RestoreError('rollback identity mismatch')
                    log.add('delete-intent', code=code, id=identity)
                    call(request, 'DELETE', ROOT, wrapper({'id': identity}))
                if lookup(request, code) or message_rows(request, identity):
                    raise RestoreError('rollback absence not verified')
                log.add('deleted-verified', code=code, id=identity)
                result['deleted'] += 1
            result['rollback_complete'] = not result['unresolved']
            log.add('rollback-finished', rollback_complete=result['rollback_complete'],
                    unresolved=result['unresolved'])
        except Exception:
            log.add('rollback-failed', reason='operation-or-verification-failed')
            raise RestoreError('rollback failed; private journal preserved') from None
        return result


def local_url(value):
    try:
        parts = urlsplit(value)
        if (parts.scheme != 'http' or parts.hostname not in ('127.0.0.1', 'localhost')
                or parts.port not in (18087, 18187) or parts.username or parts.password
                or parts.path not in ('', '/') or parts.query or parts.fragment):
            raise ValueError()
    except ValueError:
        raise RestoreError('only local HTTP loopback ports 18087/18187 are allowed') from None
    return value.rstrip('/')


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--base-url', default='http://127.0.0.1:18087')
    parser.add_argument('--input', type=Path, help='private JSON original menu translations')
    parser.add_argument('--journal', type=Path, required=True, help='private append-only JSONL (new for apply)')
    parser.add_argument('--user', required=True)
    parser.add_argument('--password-file', type=Path, required=True, help='existing 0600 TEST_PASSWORD file')
    parser.add_argument('--apply', action='store_true')
    parser.add_argument('--rollback', action='store_true')
    args = parser.parse_args(argv)
    try:
        base_url = local_url(args.base_url)
        entries = None
        source_sha256 = None
        if not args.rollback:
            if args.input is None or not args.input.is_file() or args.input.stat().st_mode & 0o077:
                raise RestoreError('input must be a private file')
            content = args.input.read_bytes()
            source_sha256 = hashlib.sha256(content).hexdigest()
            entries = json.loads(content)
            validate_entries(entries)
        sys.path.insert(0, str(Path(__file__).resolve().parent))
        from fast_differential_api_runner import Session, read_password
        session = Session(base_url, 30)
        session.opener = build_opener(ProxyHandler({}), HTTPCookieProcessor(session.jar), RejectRedirects())
        if session.login(args.user, read_password(args.password_file)).get('status') != 200:
            raise RestoreError('local authentication failed')
        if args.rollback:
            result = rollback(session.request, args.journal, apply=args.apply)
        else:
            result = restore(session.request, entries, args.journal, apply=args.apply,
                             source_sha256=source_sha256)
        print(json.dumps(result, sort_keys=True))
        return 0 if not args.rollback or not args.apply or result['rollback_complete'] else 2
    except Exception:
        # HTTP/JSON errors can contain source translations or auth material.
        print(json.dumps({'strict_evidence': False, 'status': 'failed',
                          'detail': 'operation failed; inspect private input and journal'}), file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
