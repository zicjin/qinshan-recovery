#!/usr/bin/env python3
"""Merge owned module jars without classpath masking or conflicting duplicates."""
import hashlib
import io
import json
from pathlib import Path
import shutil
import sys
import zipfile

def digest(data):
    return hashlib.sha256(data).hexdigest()


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
                        raise SystemExit('conflicting duplicate runtime entry: ' + name + ' from ' + prior_source + ' and ' + str(source))
                    duplicates.append({'entry': name, 'sources': [prior_source, source.name], 'sha256': digest(data)})
                    continue
                entries[name] = (source.name, data)
    if factories:
        entries['META-INF/spring.factories'] = ('merged:' + ','.join(factory_sources),
                                                render_factories(factories))
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
    framework_lib = jars[0].parent / 'public-deps'
    if not framework_lib.is_dir():
        raise SystemExit('missing pinned public dependency directory: ' + str(framework_lib))
    dependency_hashes = {}
    excluded_dependencies = {}
    for source in sorted(framework_lib.glob('*.jar')):
        if source.name == 'javax.servlet-api-4.0.1.jar':
            # Embedded Tomcat is the runtime servlet provider. The standalone
            # compile API has differing duplicate classes and is not a runtime input.
            excluded_dependencies[source.name] = {
                'sha256': digest(source.read_bytes()),
                'reason': 'compile-only servlet API; embedded Tomcat supplies the runtime API'}
            continue
        shutil.copy2(source, lib / source.name)
        dependency_hashes[source.name] = digest(source.read_bytes())
    class_entries = {}
    dependency_class_duplicates = []
    for jar_path in [app] + sorted(lib.glob('*.jar')):
        jar_label = str(jar_path.relative_to(output))
        with zipfile.ZipFile(jar_path) as archive:
            for name in archive.namelist():
                if not name.endswith('.class'):
                    continue
                data = archive.read(name)
                if name in class_entries:
                    prior_path, prior_data = class_entries[name]
                    if prior_data != data:
                        raise SystemExit('conflicting duplicate class: ' + name +
                                         ' from ' + prior_path + ' and ' + str(jar_path))
                    dependency_class_duplicates.append({'class': name,
                                                        'sources': [str(Path(prior_path).relative_to(output)), jar_label],
                                                        'sha256': digest(data)})
                else:
                    class_entries[name] = (str(jar_path), data)
    files = {}
    for path in sorted(output.rglob('*')):
        if path.is_file() and path.name != 'manifest.json':
            files[str(path.relative_to(output))] = digest(path.read_bytes())
    manifest = {
        'schema': 1,
        'main_class': 'com.sunwayworld.emc.projectfunction.GikamWpjcApplication',
        'app_jar': str(app.relative_to(output)),
        'module_jars': [str((modules / source.name).relative_to(output)) for source in jars],
        'source_jar_sha256': source_hashes,
        'source_jar_hash_convention': 'normalized module archives; fixed ZIP timestamps, no generated manifest',
        'dependency_jar_sha256': dependency_hashes,
        'excluded_compile_dependencies': excluded_dependencies,
        'duplicate_conflicts': [],
        'identical_duplicate_entries': duplicates,
        'identical_duplicate_classes': dependency_class_duplicates,
        'merged_resources': {
            'META-INF/spring.factories': {'sources': factory_sources,
                                          'sha256': digest(entries['META-INF/spring.factories'][1])}
        } if factories else {},
        'files': files,
        'supplier_runtime_included': False,
        'external_configuration_required': True,
    }
    (output / 'manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
    print('owned distribution package merged entries=' + str(len(entries)) + ' identical_duplicates=' + str(len(duplicates)))

if __name__ == '__main__':
    main()
