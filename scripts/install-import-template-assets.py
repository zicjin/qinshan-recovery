#!/usr/bin/env python3
"""Verify or install the finite source-bound import template file, without SQL."""
from __future__ import annotations

import argparse
import datetime
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import secrets
import sys

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_MANIFEST = ROOT / 'deployment/oracle/import-templates/manifest.json'
HASH = re.compile(r'[0-9a-f]{64}\Z')
ID = re.compile(r'[1-9][0-9]{0,31}\Z')
ASSET_FIELDS = {'template_code', 'file_id', 'target_id', 'source', 'relative_path',
                'bytes', 'sha256', 'source_sha256'}


class InvalidPackage(ValueError):
    pass


def require(condition, message):
    if not condition:
        raise InvalidPackage(message)


def no_duplicate_keys(pairs):
    result = {}
    for key, value in pairs:
        require(key not in result, 'duplicate manifest key')
        result[key] = value
    return result


def checked_file(path):
    require(all(not part.is_symlink() for part in (path, *path.parents)),
            'package path may not use symbolic links')
    require(path.is_file(), 'listed package file is missing')
    return path


def verify(manifest_path):
    path = checked_file(Path(manifest_path).absolute())
    require(path.stat().st_size <= 65536, 'manifest exceeds finite capacity')
    value = json.loads(path.read_text(encoding='utf-8'), object_pairs_hook=no_duplicate_keys)
    require(isinstance(value, dict), 'manifest must be an object')
    required = {'schema', 'status', 'deployment_ready', 'source_class', 'assets', 'blocked'}
    require(required <= value.keys() <= required | {'source_artifacts', 'transformations', 'parts', 'limitations'},
            'manifest field scope differs')
    require(value['schema'] == 'owned-import-template-assets-v1'
            and value['status'] == 'partial' and value['deployment_ready'] is False
            and value['source_class'] == 'current-production-at-capture',
            'manifest must preserve its partial production-bound contract')
    require(value['blocked'] == [{'template_code': 'grjl',
                                 'reason': 'source-no-file-and-no-mapped-columns'}],
            'source-empty template blocker differs')
    assets = value['assets']
    require(isinstance(assets, list) and len(assets) == 1, 'finite asset scope differs')
    asset = assets[0]
    require(isinstance(asset, dict) and asset.keys() == ASSET_FIELDS, 'asset field scope differs')
    require(asset['template_code'] == 'reportSendInfo' and isinstance(asset['file_id'], str)
            and ID.fullmatch(asset['file_id']) is not None, 'template or file identity differs')
    require(isinstance(asset['target_id'], str)
            and re.fullmatch(r'T_CORE_IMPORT_TMPL\$[1-9][0-9]{0,31}', asset['target_id']) is not None,
            'file target identity differs')
    require(asset['source'] == 'assets/reportSendInfo.xlsx', 'asset source path differs')
    relative = asset['relative_path']
    require(isinstance(relative, str) and '\\' not in relative
            and PurePosixPath(relative).as_posix() == relative,
            'asset target path is not portable')
    parts = PurePosixPath(relative).parts
    require(len(parts) == 5 and parts[:2] == ('files', 'imp')
            and re.fullmatch(r'[0-9]{8}', parts[2]) is not None
            and re.fullmatch(r'[0-9a-f]{32}', parts[3]) is not None,
            'asset target path scope differs')
    require(parts[4] not in ('.', '..') and parts[4].endswith('.xlsx')
            and not any(ord(character) < 32 for character in parts[4])
            and len(parts[4].encode('utf-8')) <= 255,
            'asset filename differs')
    datetime.datetime.strptime(parts[2], '%Y%m%d')
    require(type(asset['bytes']) is int and 0 < asset['bytes'] <= 1048576,
            'asset byte count differs')
    require(all(isinstance(asset[key], str) and HASH.fullmatch(asset[key]) is not None
                for key in ('sha256', 'source_sha256')), 'asset digest differs')
    source = checked_file(path.parent / asset['source'])
    require(source.stat().st_size == asset['bytes'], 'asset byte count does not match')
    raw = source.read_bytes()
    require(hashlib.sha256(raw).hexdigest() == asset['sha256'], 'asset digest does not match')
    return asset, raw


def install(workdir, asset, raw):
    require(workdir.is_absolute() and '..' not in workdir.parts,
            'workdir must be an absolute existing directory')
    require(all(not part.is_symlink() for part in (workdir, *workdir.parents))
            and workdir.is_dir(), 'workdir may not use symbolic links')
    relative = PurePosixPath(asset['relative_path'])
    target = workdir.joinpath(*relative.parts)
    # Validate every existing component and conflicting destination before mkdir
    # or copying. No SQL or metadata update is part of this file installation.
    current = workdir
    for component in relative.parts[:-1]:
        current = current / component
        require(not current.is_symlink() and (not current.exists() or current.is_dir()),
                'target directory is unsafe')
    require(not target.is_symlink(), 'target file may not be a symbolic link')
    if target.exists():
        require(target.is_file() and target.stat().st_size == len(raw) and target.read_bytes() == raw,
                'existing target differs; overwriting is forbidden')
        return 0, 1

    directory = os.open(workdir, os.O_RDONLY | os.O_DIRECTORY | os.O_NOFOLLOW)
    temporary = None
    try:
        for component in relative.parts[:-1]:
            try:
                os.mkdir(component, 0o700, dir_fd=directory)
            except FileExistsError:
                pass
            child = os.open(component, os.O_RDONLY | os.O_DIRECTORY | os.O_NOFOLLOW,
                            dir_fd=directory)
            os.close(directory)
            directory = child
        temporary = '.import-template-' + secrets.token_hex(12)
        descriptor = os.open(temporary, os.O_WRONLY | os.O_CREAT | os.O_EXCL | os.O_NOFOLLOW,
                             0o600, dir_fd=directory)
        with os.fdopen(descriptor, 'wb') as output:
            output.write(raw)
            output.flush()
            os.fsync(output.fileno())
        # Linking a fully written file publishes it atomically without replacing
        # a target created by another process after the preflight.
        os.link(temporary, relative.parts[-1], src_dir_fd=directory,
                dst_dir_fd=directory, follow_symlinks=False)
        os.unlink(temporary, dir_fd=directory)
        temporary = None
        os.fsync(directory)
    finally:
        if temporary is not None:
            os.unlink(temporary, dir_fd=directory)
        os.close(directory)
    return 1, 0


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--manifest', type=Path, default=DEFAULT_MANIFEST)
    parser.add_argument('--check-package', action='store_true')
    parser.add_argument('--workdir', type=Path)
    args = parser.parse_args()
    try:
        asset, raw = verify(args.manifest)
        require((args.check_package and args.workdir is None)
                or (not args.check_package and args.workdir is not None),
                'select --check-package or provide an installation workdir')
        receipt = {'status': 'package-valid-partial', 'assets': 1,
                   'deployment_ready': False, 'strict_evidence': False}
        if not args.check_package:
            installed, unchanged = install(args.workdir, asset, raw)
            receipt.update(status='installed-partial', installed=installed, unchanged=unchanged)
        print(json.dumps(receipt))
        return 0
    except (ValueError, OSError, UnicodeError, TypeError, KeyError):
        print(json.dumps({'status': 'rejected', 'deployment_ready': False,
                          'strict_evidence': False}))
        print('import-template-assets: package or target validation failed', file=sys.stderr)
        return 1


if __name__ == '__main__':
    raise SystemExit(main())
