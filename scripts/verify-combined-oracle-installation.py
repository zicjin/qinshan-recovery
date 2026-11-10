#!/usr/bin/env python3
"""Check the finite core/external Oracle installation plan without executing SQL.

--check-plan validates the shipped diagnostic phases. Normal deployment remains
blocked (exit 2); this command has no install, override or guard-bypass mode.
"""
from __future__ import annotations

import argparse
import importlib.util
import json
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_MANIFEST = ROOT / 'deployment/oracle/installation-manifest.json'
_spec = importlib.util.spec_from_file_location('core_schema_package', ROOT / 'scripts/verify-core-schema-package.py')
assert _spec and _spec.loader
core = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(core)

# External verification requires exactly its ten tables, before core exists.
# Preserve both original package phase orders and exact bootstrap verification.
PACKAGE_ORDER = ('external', 'core')
PACKAGE_DIRS = {'external': 'external-schema', 'core': 'core-schema'}
PACKAGE_PHASES = {
    'external': ('20-tables.sql', '30-indexes.sql', '35-constraints.sql', '90-verify.sql'),
    'core': ('10-types.sql', '20-tables.sql', '30-indexes.sql', '35-constraints.sql',
             '60-sequences.sql', '40-routines.sql', '50-comments.sql', '70-bootstrap.sql',
             '80-bootstrap-verify.sql', '90-verify.sql'),
}


def namespace_union(root: Path, manifests: dict) -> tuple[list[tuple[str, str]], set[str], set[str]]:
    """Check Oracle namespaces, including implicit indexes of named PK/UQ.

    Constraint and index namespaces are separate. A PK constraint may therefore
    share its name with its own backing index; objects of different types in
    Oracle's shared schema namespace may not share a name.
    """
    objects, indexes, constraints = {}, {}, {}
    shared = {'TABLE', 'VIEW', 'SEQUENCE', 'SYNONYM', 'PROCEDURE', 'FUNCTION', 'PACKAGE', 'TYPE', 'MATERIALIZED VIEW'}
    object_keys = {}
    for package in PACKAGE_ORDER:
        manifest = manifests[package]
        for entry in manifest['objects']:
            kind, name = entry['object_type'], entry['name']
            key = ('SHARED' if kind in shared else kind, name)
            core.require(key not in object_keys, 'CREATE object namespace collision across packages')
            object_keys[key] = package
            objects[(kind, name)] = package
            if kind == 'INDEX':
                core.require(name not in indexes, 'index namespace collision across packages')
                indexes[name] = package
        for phase in PACKAGE_PHASES[package]:
            if phase not in ('20-tables.sql', '35-constraints.sql'):
                continue
            text = (root / PACKAGE_DIRS[package] / phase).read_text(encoding='utf-8')
            sql = core.masked_sql(text)
            for match in re.finditer(r'\bCONSTRAINT\s+(' + core.IDENTIFIER + r')', sql, re.I):
                name = core.identifier(match.group(1))
                core.require(name not in constraints, 'named constraint namespace collision across packages')
                constraints[name] = package
            # A named inline P/U with bare USING INDEX creates an index bearing
            # its constraint name. Deferred constraints use existing indexes.
            if phase == '20-tables.sql':
                pattern = (r'\bCONSTRAINT\s+(' + core.IDENTIFIER
                           + r')\s+(?:PRIMARY\s+KEY|UNIQUE)\s*\([^)]*\)\s*'
                             r'USING\s+INDEX\s+(?:ENABLE|DISABLE)\b')
                for match in re.finditer(pattern, sql, re.I):
                    name = core.identifier(match.group(1))
                    core.require(name not in indexes, 'implicit backing index namespace collision across packages')
                    indexes[name] = package
    return sorted(objects), set(indexes), set(constraints)


def union_verifier(identities: list[tuple[str, str]], indexes: set[str], constraints: set[str]) -> str:
    """Exact logical object/name sets, with Oracle-generated names excluded.

    LOB physical objects and unnamed PK/UQ backing indexes have Oracle-assigned
    names. Their logical columns/relations remain covered by the original core
    verifier; they must not be guessed in the installation registry.
    """
    def literal(value: str) -> str:
        return "'" + value.replace("'", "''") + "'"

    expected = [('expected_objects', kind + '|' + name) for kind, name in identities if kind != 'INDEX']
    expected += [('expected_indexes', name) for name in sorted(indexes)]
    expected += [('expected_constraints', name) for name in sorted(constraints)]
    assignments = ''.join('  ' + group + '(' + literal(name) + ') := 1;\n' for group, name in expected)
    return '''WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
DECLARE
  TYPE names IS TABLE OF PLS_INTEGER INDEX BY VARCHAR2(300);
  expected_objects names;
  expected_indexes names;
  expected_constraints names;
  n NUMBER;
  key_name VARCHAR2(300);
BEGIN
''' + assignments + '''  SELECT COUNT(*) INTO n FROM USER_TABLES;
  IF n <> %d THEN RAISE_APPLICATION_ERROR(-20301, 'Combined table count differs'); END IF;
  FOR o IN (SELECT OBJECT_TYPE, OBJECT_NAME, STATUS FROM USER_OBJECTS
            WHERE OBJECT_TYPE NOT IN ('INDEX','LOB')) LOOP
    key_name := o.OBJECT_TYPE || '|' || o.OBJECT_NAME;
    IF NOT expected_objects.EXISTS(key_name) THEN
      RAISE_APPLICATION_ERROR(-20301, 'Unexpected combined object'); END IF;
    IF o.STATUS <> 'VALID' THEN RAISE_APPLICATION_ERROR(-20301, 'Invalid combined object'); END IF;
    expected_objects.DELETE(key_name);
  END LOOP;
  IF expected_objects.COUNT <> 0 THEN RAISE_APPLICATION_ERROR(-20301, 'Missing combined object'); END IF;
  SELECT COUNT(*) INTO n FROM USER_INDEXES WHERE STATUS <> 'VALID';
  IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20301, 'Invalid combined index'); END IF;
  FOR i IN (SELECT i.INDEX_NAME, i.STATUS FROM USER_INDEXES i
            WHERE i.INDEX_TYPE <> 'LOB' AND NOT EXISTS (
              SELECT 1 FROM USER_CONSTRAINTS c WHERE c.INDEX_NAME=i.INDEX_NAME
              AND c.INDEX_OWNER=USER AND c.CONSTRAINT_TYPE IN ('P','U')
              AND c.GENERATED='GENERATED NAME')) LOOP
    IF NOT expected_indexes.EXISTS(i.INDEX_NAME) THEN
      RAISE_APPLICATION_ERROR(-20301, 'Unexpected combined named index'); END IF;
    IF i.STATUS <> 'VALID' THEN RAISE_APPLICATION_ERROR(-20301, 'Invalid combined index'); END IF;
    expected_indexes.DELETE(i.INDEX_NAME);
  END LOOP;
  IF expected_indexes.COUNT <> 0 THEN RAISE_APPLICATION_ERROR(-20301, 'Missing combined named index'); END IF;
  FOR c IN (SELECT CONSTRAINT_NAME FROM USER_CONSTRAINTS WHERE GENERATED='USER NAME') LOOP
    IF NOT expected_constraints.EXISTS(c.CONSTRAINT_NAME) THEN
      RAISE_APPLICATION_ERROR(-20301, 'Unexpected combined named constraint'); END IF;
    expected_constraints.DELETE(c.CONSTRAINT_NAME);
  END LOOP;
  IF expected_constraints.COUNT <> 0 THEN RAISE_APPLICATION_ERROR(-20301, 'Missing combined named constraint'); END IF;
  SELECT COUNT(*) INTO n FROM USER_DB_LINKS;
  IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20301, 'Database links are outside this installation'); END IF;
END;
/
PROMPT Combined selected structure verified; full-system installation remains blocked.
''' % sum(kind == 'TABLE' for kind, _ in identities)


def verify(manifest_path: Path) -> dict:
    core.require(not manifest_path.is_symlink(), 'installation manifest may not be a symbolic link')
    root = manifest_path.parent.resolve()
    plan = json.loads(manifest_path.read_text(encoding='utf-8'))
    core.require(isinstance(plan, dict) and plan.get('schema') == 'combined-oracle-installation-v1',
                 'unsupported combined installation manifest')
    core.require(plan.get('status') == 'partial' and plan.get('deployment_ready') is False
                 and plan.get('complete_external_domains') is False,
                 'finite installation plan must retain partial deployment and external-domain scope')
    core.require(plan.get('target') == {'owner': 'current-session-user', 'requires_empty_schema': True},
                 'combined installation must target a dedicated empty session user')
    core.require(not core.unsafe_material(json.dumps(plan, ensure_ascii=False)),
                 'installation manifest contains unsafe material')
    core.require(isinstance(plan.get('blockers'), list) and bool(plan['blockers']),
                 'partial installation must retain blockers')
    packages = plan.get('packages')
    core.require(isinstance(packages, list) and [p.get('id') for p in packages] == list(PACKAGE_ORDER),
                 'combined plan must contain exactly external then core packages')
    manifests, counts, objects, phase_entries = {}, {}, [], []
    for package in packages:
        name = package['id']
        prefix = PACKAGE_DIRS[name] + '/'
        core.require(package.get('path') == prefix + 'manifest.json', 'unexpected package manifest path')
        manifest_file = core.package_file(root, package['path'])
        core.check_hash(package.get('sha256'), 'package manifest hash')
        core.require(core.digest(manifest_file.read_bytes()) == package['sha256'], 'package manifest hash mismatch')
        core.verify(manifest_file)
        manifest = json.loads(manifest_file.read_text(encoding='utf-8'))
        manifests[name] = manifest
        files = {entry['path']: entry for entry in manifest['files']}
        # Cover every original child once except the package preflights, which
        # are checked for equality and executed as one shared upfront phase.
        sql = {path for path in files if path.endswith('.sql')}
        core.require(sql == set(PACKAGE_PHASES[name]) | {'install.sql', '00-preflight.sql'},
                     'package SQL phase set changed; review combined installation order')
        initializer = core.masked_sql((manifest_file.parent / 'install.sql').read_text())
        children = re.findall(r'^\s*@@([^\s]+)\s*$', initializer, re.M)
        core.require(children == ['00-preflight.sql', *PACKAGE_PHASES[name]],
                     'package phase order differs from combined installation contract')
        for path in PACKAGE_PHASES[name]:
            phase_entries.append({**files[path], 'path': prefix + path, 'package': name})
        counts[name] = sum(entry['object_type'] == 'TABLE' for entry in manifest['objects'])
        objects.extend((name, entry['object_type'], entry['name']) for entry in manifest['objects'])

    listed = plan.get('files')
    core.require(isinstance(listed, list) and [f.get('path') for f in listed]
                 == ['install.sql', '00-preflight.sql', '99-verify.sql'],
                 'combined local files must be exactly initializer, preflight and union verifier')
    texts, local = {}, {}
    for entry in listed:
        name = entry['path']
        raw = core.package_file(root, name).read_bytes()
        core.check_hash(entry.get('sha256'), 'combined file hash')
        core.require(core.digest(raw) == entry['sha256'] and entry.get('bytes') == len(raw),
                     'combined file hash or byte count mismatch')
        texts[name] = raw.decode('utf-8')
        core.require(not core.unsafe_material(texts[name]), 'combined SQL contains unsafe material')
        local[name] = entry
    core.require([entry.get('role') for entry in listed] == ['initializer', 'preflight', 'verifier'],
                 'combined files have unexpected roles')
    for package in PACKAGE_ORDER:
        original = (root / PACKAGE_DIRS[package] / '00-preflight.sql').read_text(encoding='utf-8')
        core.require(texts['00-preflight.sql'] == original,
                     'combined preflight must preserve both original empty-user and charset checks')

    phases = [local['00-preflight.sql'], *phase_entries, local['99-verify.sql']]
    core.require(plan.get('phases') == phases, 'combined phases differ in order, role, hash or exact coverage')
    paths = [entry['path'] for entry in phases]
    core.check_initializer(texts['install.sql'], 'partial', set(paths), '00-preflight.sql')
    masked = core.masked_sql(texts['install.sql'])
    includes = re.findall(r'^\s*@@([^\s]+)\s*$', masked, re.M)
    core.require(includes == paths, 'combined SQL includes differ from the exact phase order')
    guard = re.search(r'\bBEGIN\s+RAISE_APPLICATION_ERROR\s*\(\s*-20100\s*,\s*\)\s*;\s*END\s*;\s*/', masked, re.I)
    core.require(guard is not None, 'combined initializer must retain its unconditional partial guard')
    tail = re.sub(r'^\s*@@[^\s]+\s*$', '', masked[guard.end():], flags=re.M)
    core.require(not tail.strip(), 'combined initializer may only include exact phases after its guard')

    identities, indexes, constraints = namespace_union(root, manifests)
    tables = [name for kind, name in identities if kind == 'TABLE']
    core.require(texts['99-verify.sql'] == union_verifier(identities, indexes, constraints), 'union verifier does not cover exact object, index and constraint union')
    return {
        'status': 'blocked', 'plan_valid': True, 'deployment_ready': False,
        'complete_external_domains': False, 'packages': len(packages),
        'package_table_counts': counts, 'tables': len(tables), 'create_objects': len(objects),
        'named_indexes': len(indexes), 'named_constraints': len(constraints),
        'phases': len(phases), 'blockers': plan['blockers'],
        'scope': 'selected external tables and core package only; no full-system acceptance',
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--manifest', type=Path, default=DEFAULT_MANIFEST)
    parser.add_argument('--check-plan', action='store_true', help='offline phase integrity only; deployment remains blocked')
    args = parser.parse_args()
    try:
        result = verify(args.manifest)
    except (core.InvalidPackage, OSError, UnicodeError, ValueError, TypeError, KeyError, AttributeError) as exc:
        reason = str(exc) if isinstance(exc, core.InvalidPackage) else type(exc).__name__
        print(json.dumps({'status': 'failed', 'plan_valid': False, 'deployment_ready': False, 'reason': reason}))
        return 1
    print(json.dumps(result))
    return 0 if args.check_plan else 2


if __name__ == '__main__':
    sys.exit(main())
