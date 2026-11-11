#!/usr/bin/env python3
"""Merge owned module jars without classpath masking or conflicting duplicates."""
import hashlib
import importlib.util
import io
import json
import os
from pathlib import Path
import re
import shutil
import sys
import zipfile

REPO_ROOT = Path(__file__).resolve().parents[1]
EXTERNAL_SCHEMA_CONTRACT = 'META-INF/lims-external-schema.json'
EXTERNAL_SCHEMA_PROPERTIES = 'META-INF/lims-external-schema.properties'
EXTERNAL_SCHEMA_MAPPERS = frozenset((
    'com/sunwayworld/emc/module/bp/project/project/mapper/conf/pf-project-oracle-mapper.xml',
    'com/sunwayworld/emc/module/mdm/contract/mapper/conf/emc-contract-oracle-mapper.xml',
))

def digest(data):
    return hashlib.sha256(data).hexdigest()


def external_schema_contract(entries, required):
    """Verify the contract against the final merged resource selection."""
    if not required:
        return None

    def fail(message):
        raise SystemExit('external schema build contract: ' + message)

    def portable(value):
        return (isinstance(value, str) and bool(value)
                and not value.startswith(('/', '\\')) and '\\' not in value
                and ':' not in value and not any(part in ('', '.', '..', 'private')
                                               for part in value.split('/')))

    def is_hash(value):
        return isinstance(value, str) and re.fullmatch(r'[a-f0-9]{64}', value) is not None

    if EXTERNAL_SCHEMA_CONTRACT not in entries or EXTERNAL_SCHEMA_PROPERTIES not in entries:
        fail('embedded metadata is missing')
    raw = entries[EXTERNAL_SCHEMA_CONTRACT][1]
    try:
        contract = json.loads(raw.decode('utf-8'))
    except (UnicodeDecodeError, json.JSONDecodeError):
        fail('embedded JSON is invalid')
    expected_keys = {'schema', 'mode', 'profile', 'profile_sha256', 'manifest',
                     'manifest_sha256', 'renderer', 'renderer_sha256', 'mappers', 'dynamic_sql'}
    if not isinstance(contract, dict) or set(contract) != expected_keys:
        fail('metadata fields differ from the supported schema')
    if contract.get('schema') != 'external-schema-build-contract-v1':
        fail('unsupported schema')
    mode = contract.get('mode')
    if mode not in ('original-db-link', 'direct-schema', 'local-view'):
        fail('invalid mode')
    for name in ('profile', 'manifest', 'renderer'):
        if mode == 'original-db-link':
            if contract[name] is not None or contract[name + '_sha256'] is not None:
                fail('original SQL must not claim unused profile inputs')
        elif not portable(contract[name]) or not is_hash(contract[name + '_sha256']):
            fail('profile provenance is missing or non-portable')
    dynamic = contract['dynamic_sql']
    if mode == 'original-db-link':
        if dynamic is not None:
            fail('original SQL must not claim dynamic logical access')
    else:
        if not isinstance(dynamic, dict) or set(dynamic) != {'qualifier_schemas', 'objects_by_qualifier'}:
            fail('dynamic SQL profile fields differ')
        schemas, objects = dynamic['qualifier_schemas'], dynamic['objects_by_qualifier']
        identifier = re.compile(r'[A-Z][A-Z0-9_$#]{0,29}\Z')
        if (not isinstance(schemas, dict) or not schemas or not isinstance(objects, dict)
                or set(schemas) != set(objects)):
            fail('dynamic SQL profile coverage differs')
        for qualifier, schema in schemas.items():
            if (not isinstance(qualifier, str) or not identifier.fullmatch(qualifier)
                    or not isinstance(schema, str) or not identifier.fullmatch(schema)
                    or not isinstance(objects[qualifier], list) or not objects[qualifier]
                    or any(not isinstance(name, str) or not identifier.fullmatch(name)
                           for name in objects[qualifier])
                    or objects[qualifier] != sorted(set(objects[qualifier]))):
                fail('dynamic SQL profile identifiers differ')
    records = contract.get('mappers')
    if not isinstance(records, list) or len(records) != len(EXTERNAL_SCHEMA_MAPPERS):
        fail('mapper coverage is incomplete')
    seen = set()
    for record in records:
        if (not isinstance(record, dict) or set(record) != {'path', 'sha256'}
                or not isinstance(record.get('path'), str)
                or record['path'] not in EXTERNAL_SCHEMA_MAPPERS
                or record['path'] in seen or not is_hash(record.get('sha256'))):
            fail('mapper identity or hash is invalid')
        seen.add(record['path'])
        selected = entries.get(record['path'])
        if selected is None or digest(selected[1]) != record['sha256']:
            fail('packaged mapper hash mismatch: ' + record['path'])
    sha = digest(raw)
    properties = ('mode=' + mode + '\ncontract_sha256=' + sha + '\n').encode('ascii')
    if entries[EXTERNAL_SCHEMA_PROPERTIES][1] != properties:
        fail('embedded properties differ from JSON')
    return {**contract, 'contract_sha256': sha}


def package_installation(output):
    """Copy only the finite, verified public Oracle installation dependency set."""
    spec = importlib.util.spec_from_file_location(
        'combined_oracle_installation', REPO_ROOT / 'scripts/verify-combined-oracle-installation.py')
    assert spec and spec.loader
    verifier = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(verifier)
    plan_path = REPO_ROOT / 'deployment/oracle/installation-manifest.json'
    try:
        verifier.verify(plan_path)
        plan = json.loads(plan_path.read_text(encoding='utf-8'))
        inputs = {'deployment/oracle/installation-manifest.json',
                  'scripts/verify-core-schema-package.py',
                  'scripts/verify-combined-oracle-installation.py'}
        inputs.update('deployment/oracle/' + entry['path'] for entry in plan['files'])
        for package in plan['packages']:
            manifest = REPO_ROOT / 'deployment/oracle' / package['path']
            inputs.add(manifest.relative_to(REPO_ROOT).as_posix())
            metadata = json.loads(manifest.read_text(encoding='utf-8'))
            inputs.update((manifest.parent / entry['path']).relative_to(REPO_ROOT).as_posix()
                          for entry in metadata['files'])
        workflow_paths = ('deployment/oracle/workflow/manifest.json',
                          'deployment/oracle/workflow/definitions.json',
                          'deployment/oracle/workflow/README.md')
        workflow_files = {path: verifier.core.package_file(REPO_ROOT, path) for path in workflow_paths}
        workflow_manifest = json.loads(workflow_files[workflow_paths[0]].read_text(encoding='utf-8'))
        workflow_raw = workflow_files[workflow_paths[1]].read_bytes()
        workflow_definitions = json.loads(workflow_raw)
        verifier.core.require(isinstance(workflow_manifest, dict) and isinstance(workflow_definitions, dict)
            and set(workflow_definitions) == {'schema', 'definitions', 'symbols', 'blocked'},
            'workflow manifest or definitions shape differs')
        verifier.core.require(workflow_manifest.get('schema') == 'owned-workflow-package-v1'
            and workflow_manifest.get('status') == 'partial' and workflow_manifest.get('deployment_ready') is False
            and workflow_definitions.get('schema') == 'owned-workflow-definitions-v1',
            'workflow package must retain its partial schema and status')
        reference = workflow_manifest.get('definitions', {})
        verifier.core.require(isinstance(reference, dict) and set(reference) == {'path', 'sha256', 'bytes'}
            and reference.get('path') == 'definitions.json'
            and reference.get('sha256') == digest(workflow_raw)
            and type(reference.get('bytes')) is int and reference['bytes'] == len(workflow_raw),
            'workflow definitions hash, byte count or strict path differs')
        definitions, blocked, symbols = (workflow_definitions.get('definitions'),
            workflow_definitions.get('blocked'), workflow_definitions.get('symbols'))
        verifier.core.require(isinstance(definitions, list) and isinstance(blocked, list)
            and isinstance(symbols, dict) and set(symbols) == {'roles', 'users', 'organizations'}
            and all(isinstance(value, list) for value in symbols.values()), 'workflow arrays differ')
        counts = workflow_manifest.get('counts')
        expected_counts = {'definitions': len(definitions), 'blocked': len(blocked),
                           **{kind: len(value) for kind, value in symbols.items()}}
        verifier.core.require(isinstance(counts, dict) and set(counts) == set(expected_counts)
            and all(type(counts[key]) is int and counts[key] == value for key, value in expected_counts.items()),
            'workflow counts do not match the exact arrays')
        inputs.update(workflow_paths)
        template_script = verifier.core.package_file(REPO_ROOT, 'scripts/install-import-template-assets.py')
        template_spec = importlib.util.spec_from_file_location('import_template_assets', template_script)
        assert template_spec and template_spec.loader
        template_installer = importlib.util.module_from_spec(template_spec)
        template_spec.loader.exec_module(template_installer)
        template_manifest = verifier.core.package_file(REPO_ROOT, 'deployment/oracle/import-templates/manifest.json')
        template_asset, _ = template_installer.verify(template_manifest)
        bootstrap_path = verifier.core.package_file(REPO_ROOT, 'deployment/oracle/core-schema/bootstrap-provenance.json')
        bootstrap = json.loads(bootstrap_path.read_text(encoding='utf-8'))
        verifier.core.require(bootstrap.get('import_template', {}).get('asset_manifest') == {
            'path': 'deployment/oracle/import-templates/manifest.json',
            'sha256': digest(template_manifest.read_bytes()),
        }, 'template asset manifest differs from the source-bound SQL bootstrap')
        inputs.update(('scripts/install-import-template-assets.py',
                       'deployment/oracle/import-templates/manifest.json',
                       'deployment/oracle/import-templates/README.md',
                       'deployment/oracle/import-templates/' + template_asset['source']))
        # The optional report engine itself stays an operator-supplied third-party
        # input. Package only owned integration source and source-bound assets.
        report_script = verifier.core.package_file(REPO_ROOT, 'scripts/install-report-engine.py')
        report_spec = importlib.util.spec_from_file_location('report_installation', report_script)
        assert report_spec and report_spec.loader
        report_installer = importlib.util.module_from_spec(report_spec)
        report_spec.loader.exec_module(report_installer)
        report_manifest = json.loads((REPO_ROOT / 'deployment/report-engine/manifest.json').read_text(encoding='utf-8'))
        report_installer.verify_configuration(report_manifest)
        for asset in report_manifest['assets']:
            report_installer.checked_file(REPO_ROOT / 'deployment/report-engine' / asset['path'], asset, 'report template')
            report_installer.checked_file(REPO_ROOT / 'deployment/report-engine' / asset['metadata_source'],
                {'bytes': asset['metadata_bytes'], 'sha256': asset['metadata_sha256']}, 'report metadata')
        inputs.add('scripts/install-report-engine.py')
        inputs.update(path.relative_to(REPO_ROOT).as_posix()
                      for path in (REPO_ROOT / 'deployment/report-engine').rglob('*')
                      if path.is_file())
        installation = output / 'installation'
        verifier.core.require(not installation.is_symlink(), 'installation output may not be a symbolic link')
        readme = verifier.core.package_file(REPO_ROOT, 'owned-distribution/installation-README.md')
        if installation.exists():
            shutil.rmtree(installation)
        for relative in sorted(inputs):
            source = verifier.core.package_file(REPO_ROOT, relative)
            target = installation / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(source, target)
        shutil.copyfile(readme, installation / 'README.md')
    except (OSError, ValueError, TypeError, KeyError) as exc:
        raise SystemExit('installation bundle integrity failed: ' + type(exc).__name__) from exc
    return {'path': 'installation', 'status': 'partial', 'deployment_ready': False,
            'complete_external_domains': False,
            'manifest': 'installation/deployment/oracle/installation-manifest.json',
            'integrity_verifier': 'installation/scripts/verify-combined-oracle-installation.py --check-plan',
            'workflow': {'manifest': 'installation/deployment/oracle/workflow/manifest.json',
                         'status': 'partial', 'deployment_ready': False, 'counts': counts},
            'import_templates': {'manifest': 'installation/deployment/oracle/import-templates/manifest.json',
                                 'status': 'partial', 'deployment_ready': False, 'assets': 1, 'blocked': 1}}


def normalized_jar(source):
    """Copy a module archive with stable ZIP metadata for reproducible packages."""
    with zipfile.ZipFile(source) as archive:
        with io.BytesIO() as output:
            with zipfile.ZipFile(output, 'w', compression=zipfile.ZIP_DEFLATED) as target:
                for info in sorted(archive.infolist(), key=lambda item: item.filename):
                    if info.filename == 'META-INF/MANIFEST.MF':
                        continue
                    entry = zipfile.ZipInfo(info.filename, date_time=(1980, 1, 1, 0, 0, 0))
                    entry.compress_type = zipfile.ZIP_DEFLATED
                    entry.external_attr = 0o644 << 16
                    target.writestr(entry, archive.read(info.filename))
            return output.getvalue()


def parse_factories(data):
    """Read Spring's continuation-based factories properties into ordered values."""
    text = data.decode('utf-8')
    logical = []
    current = ''
    for line in text.splitlines():
        line = line.rstrip()
        if not line or line.lstrip().startswith(('#', '!')):
            continue
        if line.endswith('\\'):
            current += line[:-1]
            continue
        logical.append(current + line)
        current = ''
    if current:
        logical.append(current)
    values = {}
    for line in logical:
        separator = next((index for index, char in enumerate(line) if char in '=:'), -1)
        if separator < 0:
            raise SystemExit('invalid META-INF/spring.factories entry: ' + line)
        key = line[:separator].strip()
        entries = [value.strip() for value in line[separator + 1:].split(',')]
        values.setdefault(key, [])
        for entry in entries:
            if entry and entry not in values[key]:
                values[key].append(entry)
    return values


def render_factories(values):
    lines = []
    for key in sorted(values):
        entries = values[key]
        if not entries:
            lines.append(key + '=')
            continue
        lines.append(key + '=\\')
        for index, entry in enumerate(entries):
            suffix = ',' if index + 1 < len(entries) else ''
            lines.append(entry + suffix + ('\\' if index + 1 < len(entries) else ''))
    return ('\n'.join(lines) + '\n').encode('utf-8')

def main():
    output = Path(sys.argv[1])
    jars = [Path(item) for item in sys.argv[2:]]
    app = output / 'app/lims-owned-application.jar'
    modules = output / 'modules'
    lib = output / 'lib'
    output.mkdir(parents=True, exist_ok=True)
    shutil.rmtree(modules, ignore_errors=True)
    shutil.rmtree(lib, ignore_errors=True)
    if app.exists():
        app.unlink()
    modules.mkdir(parents=True, exist_ok=True)
    lib.mkdir(parents=True, exist_ok=True)
    entries = {}
    duplicates = []
    resource_overrides = []
    factories = {}
    factory_sources = []
    source_hashes = {}
    for source in jars:
        normalized = normalized_jar(source)
        source_hashes[source.name] = digest(normalized)
        (modules / source.name).write_bytes(normalized)
        with zipfile.ZipFile(source) as archive:
            for name in archive.namelist():
                if name.endswith('/') or name == 'META-INF/MANIFEST.MF':
                    continue
                data = archive.read(name)
                if name == 'META-INF/spring.factories':
                    factory_sources.append(source.name)
                    for key, values in parse_factories(data).items():
                        factories.setdefault(key, [])
                        for value in values:
                            if value not in factories[key]:
                                factories[key].append(value)
                    continue
                if name in entries:
                    prior_source, prior_data = entries[name]
                    if prior_data != data:
                        if name.endswith('.class'):
                            raise SystemExit('conflicting duplicate runtime entry: ' + name + ' from ' + prior_source + ' and ' + str(source))
                        resource_overrides.append({
                            'entry': name,
                            'selected_source': prior_source,
                            'shadowed_source': source.name,
                            'selected_sha256': digest(prior_data),
                            'shadowed_sha256': digest(data),
                            'policy': 'first-source-wins for application resource overrides',
                        })
                        continue
                    duplicates.append({'entry': name, 'sources': [prior_source, source.name], 'sha256': digest(data)})
                    continue
                entries[name] = (source.name, data)
    if factories:
        entries['META-INF/spring.factories'] = ('merged:' + ','.join(factory_sources),
                                                render_factories(factories))
    external_schema = external_schema_contract(
        entries, os.environ.get('OWNED_DISTRIBUTION_MODE') == 'source-only-reconstituted')
    if external_schema is not None:
        provenance = output / 'provenance'
        provenance.mkdir(parents=True, exist_ok=True)
        (provenance / 'external-schema.properties').write_bytes(entries[EXTERNAL_SCHEMA_PROPERTIES][1])
    app.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(app, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
        def write_entry(name, data):
            info = zipfile.ZipInfo(name, date_time=(1980, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            archive.writestr(info, data)
        directories = set()
        for name in entries:
            parent = Path(name).parent
            while str(parent) != '.':
                directories.add(str(parent) + '/')
                parent = parent.parent
        for name in sorted(directories):
            write_entry(name, b'')
        for name in sorted(entries):
            write_entry(name, entries[name][1])
    dependency_dirs = []
    configured_dependency_dir = os.environ.get('OWNED_PUBLIC_DEPENDENCY_DIR')
    if configured_dependency_dir:
        dependency_dirs.append(Path(configured_dependency_dir))
    else:
        dependency_dirs.append(jars[0].parent / 'public-deps')
    dependency_dirs.extend(
        Path(value)
        for value in os.environ.get('OWNED_ADDITIONAL_DEPENDENCY_DIRS', '').split(os.pathsep)
        if value
    )
    for dependency_dir in dependency_dirs:
        if not dependency_dir.is_dir():
            raise SystemExit('missing pinned dependency directory: ' + str(dependency_dir))
    dependency_hashes = {}
    excluded_dependencies = {}
    dependency_sources = {}
    for dependency_dir in dependency_dirs:
        for source in sorted(dependency_dir.glob('*.jar')):
            if source.name == 'javax.servlet-api-4.0.1.jar':
                # Embedded Tomcat is the runtime servlet provider. The standalone
                # compile API has differing duplicate classes and is not a runtime input.
                excluded_dependencies[source.name] = {
                    'sha256': digest(source.read_bytes()),
                    'reason': 'compile-only servlet API; embedded Tomcat supplies the runtime API'}
                continue
            if source.name in dependency_sources:
                prior = dependency_sources[source.name]
                if digest(prior.read_bytes()) != digest(source.read_bytes()):
                    raise SystemExit(
                        'conflicting dependency JAR: ' + source.name + ' from ' +
                        str(prior) + ' and ' + str(source)
                    )
                continue
            dependency_sources[source.name] = source
    allowlist_path = REPO_ROOT / 'reconstituted/framework/public-dependency-allowlist.json'
    allowlist_order = {}
    if allowlist_path.is_file():
        allowlist = json.loads(allowlist_path.read_text())
        allowlist_order = {
            str(item['filename']): int(item.get('archive_order', index))
            for index, item in enumerate(allowlist.get('entries', []))
        }
    dependency_classpath = sorted(
        dependency_sources,
        key=lambda name: (allowlist_order.get(name, 1 << 30), name),
    )
    for name in dependency_classpath:
        source = dependency_sources[name]
        shutil.copy2(source, lib / name)
        dependency_hashes[name] = digest(source.read_bytes())
    (lib / 'classpath.txt').write_text('\n'.join(dependency_classpath) + '\n')
    class_entries = {}
    dependency_class_duplicates = []
    dependency_class_shadows = []
    for jar_path in [app] + [lib / name for name in dependency_classpath]:
        jar_label = str(jar_path.relative_to(output))
        with zipfile.ZipFile(jar_path) as archive:
            for name in archive.namelist():
                if not name.endswith('.class'):
                    continue
                if name == 'module-info.class' or name.endswith('/module-info.class'):
                    # JPMS descriptors are archive-local metadata. Java 8 uses
                    # the physical classpath and never loads these entries.
                    continue
                data = archive.read(name)
                if name in class_entries:
                    prior_path, prior_data = class_entries[name]
                    if prior_data != data:
                        prior_relative = str(Path(prior_path).relative_to(output))
                        current_relative = str(jar_path.relative_to(output))
                        if prior_relative.startswith('app/') or current_relative.startswith('app/'):
                            raise SystemExit('conflicting duplicate application class: ' + name +
                                             ' from ' + prior_path + ' and ' + str(jar_path))
                        dependency_class_shadows.append({
                            'class': name,
                            'selected': prior_relative,
                            'shadowed': current_relative,
                            'selected_sha256': digest(prior_data),
                            'shadowed_sha256': digest(data),
                        })
                        continue
                    dependency_class_duplicates.append({'class': name,
                                                        'sources': [str(Path(prior_path).relative_to(output)), jar_label],
                                                        'sha256': digest(data)})
                else:
                    class_entries[name] = (str(jar_path), data)
    installation = (package_installation(output)
                    if os.environ.get('OWNED_DISTRIBUTION_MODE') == 'source-only-reconstituted' else None)
    files = {}
    for path in sorted(output.rglob('*')):
        if path.is_file() and path != output / 'manifest.json':
            files[str(path.relative_to(output))] = digest(path.read_bytes())
    manifest = {
        'schema': 1,
        'distribution_mode': os.environ.get(
            'OWNED_DISTRIBUTION_MODE', 'merged-modules'
        ),
        'main_class': 'com.sunwayworld.emc.projectfunction.GikamWpjcApplication',
        'app_jar': str(app.relative_to(output)),
        'module_jars': [str((modules / source.name).relative_to(output)) for source in jars],
        'source_jar_sha256': source_hashes,
        'source_jar_hash_convention': 'normalized module archives; fixed ZIP timestamps, no generated manifest',
        'dependency_jar_sha256': dependency_hashes,
        'dependency_classpath': dependency_classpath,
        'dependency_classpath_file': 'lib/classpath.txt',
        'dependency_source_directories': [
            (
                str(path.resolve().relative_to(REPO_ROOT))
                if path.resolve().is_relative_to(REPO_ROOT)
                else path.name
            )
            for path in dependency_dirs
        ],
        'excluded_compile_dependencies': excluded_dependencies,
        'duplicate_conflicts': [],
        'identical_duplicate_entries': duplicates,
        'dependency_class_shadows': dependency_class_shadows,
        'resource_overrides': resource_overrides,
        'identical_duplicate_classes': dependency_class_duplicates,
        'merged_resources': {
            'META-INF/spring.factories': {'sources': factory_sources,
                                          'sha256': digest(entries['META-INF/spring.factories'][1])}
        } if factories else {},
        'files': files,
        'supplier_runtime_included': False,
        'external_configuration_required': True,
    }
    if installation is not None:
        manifest['installation'] = installation
    if external_schema is not None:
        manifest['external_schema'] = external_schema
    provenance_path = os.environ.get('OWNED_THIRD_PARTY_DEPENDENCY_PROVENANCE')
    if provenance_path:
        provenance = json.loads(Path(provenance_path).read_text(encoding='utf-8'))
        manifest['third_party_dependency_provenance'] = provenance.get('entries', [])
    else:
        manifest['third_party_dependency_provenance'] = []
    (output / 'manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
    print('owned distribution package merged entries=' + str(len(entries)) + ' identical_duplicates=' + str(len(duplicates)))

if __name__ == '__main__':
    main()
