#!/usr/bin/env python3
"""Validate the standalone distribution's public build/start boundary."""
import hashlib
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[2]
DIST = ROOT / 'owned-distribution/target/distribution'


def main():
    if os.environ.get('SOURCE_ONLY_CHECKPOINT') != '1':
        lifecycle = subprocess.run(
            ['bash', str(ROOT / 'scripts/tests/test-api-evidence-gate.sh')],
            text=True, capture_output=True,
        )
        assert lifecycle.returncode == 0, (
            'evidence lifecycle gate failed: '
            + (lifecycle.stdout + lifecycle.stderr)[-2000:]
        )
    builder = ROOT / 'scripts/build-owned-distribution.sh'
    assert builder.is_file(), 'standalone distribution builder must exist'
    result = subprocess.run([str(builder)], text=True, capture_output=True)
    assert result.returncode == 0, 'standalone distribution build failed: ' + result.stderr[-2000:]
    with tempfile.TemporaryDirectory(prefix='owned-native-license-gate-') as temporary:
        gate_report = Path(temporary) / 'native-license-removal.json'
        source_roots = [
            ROOT / 'project/src/main/java',
            ROOT / 'project/src/main/resources',
            ROOT / 'owned-framework/src/main',
            ROOT / 'reconstituted/framework',
        ]
        gate = subprocess.run([
            sys.executable,
            str(ROOT / 'scripts/verify-owned-native-license-removal.py'),
            *sum((["--source-root", str(path)] for path in source_roots), []),
            '--distribution', str(DIST),
            '--output', str(gate_report),
        ], text=True, capture_output=True)
        assert gate.returncode == 0, 'native/license removal gate failed: ' + gate.stderr[-2000:]
        assert json.loads(gate_report.read_text())['findings'] == []
    manifest = json.loads((DIST / 'manifest.json').read_text())
    assert manifest['distribution_mode'] == 'source-only-reconstituted'
    assert manifest['main_class'] == 'com.sunwayworld.emc.projectfunction.GikamWpjcApplication'
    assert manifest['duplicate_conflicts'] == [], 'runtime classpath may not hide differing classes'
    assert manifest['source_jar_hash_convention'].startswith('normalized module archives')
    assert len(manifest['module_jars']) == 8
    assert manifest['dependency_class_shadows'], 'original duplicate dependency classes must be documented'
    assert manifest['third_party_dependency_provenance'][0]['filename'] == 'qiyuesuo-sdk-vendored.jar'
    app = DIST / 'app/lims-owned-application.jar'
    with zipfile.ZipFile(app) as jar:
        assert jar.namelist().count('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class') == 1
        assert 'com/sunwayworld/boot/ApplicationConfiguration.class' in jar.namelist()
        assert not any('/src/test/' in name or name.endswith('ContractTest.class') for name in jar.namelist())
        assert not any(name.endswith('OwnedWebBrowserApplication.class') or name.endswith('OwnedInventoryBrowserApplication.class') for name in jar.namelist())
        assert len(jar.read('static/gikam/main/js/chunk-common.js')) > 1_000_000
    classpath_file = DIST / 'lib/classpath.txt'
    classpath = [line for line in classpath_file.read_text().splitlines() if line]
    assert classpath == manifest['dependency_classpath']
    assert 'private-sdk-RELEASE.jar' not in classpath
    assert 'qiyuesuo-sdk-vendored.jar' in classpath
    classes = {}
    identical_classes = []
    for jar_path in [app] + [DIST / 'lib' / name for name in classpath]:
        with zipfile.ZipFile(jar_path) as jar:
            for name in jar.namelist():
                if name.endswith('.class'):
                    if name == 'module-info.class' or name.endswith('/module-info.class'):
                        continue
                    data = jar.read(name)
                    if name in classes:
                        prior_jar, prior_data = classes[name]
                        if prior_data != data:
                            assert not prior_jar.startswith('app/') and jar_path.relative_to(DIST).as_posix() != 'app/lims-owned-application.jar'
                            continue
                        identical_classes.append(name)
                    else:
                        classes[name] = (jar_path.relative_to(DIST).as_posix(), data)
    assert len(identical_classes) == len(manifest['identical_duplicate_classes'])
    for relative, expected in manifest['files'].items():
        assert hashlib.sha256((DIST / relative).read_bytes()).hexdigest() == expected, relative
    assert not any(p.name in ('application.properties', 'jdbc.properties', 'oracle.env') for p in DIST.rglob('*'))
    rejected = subprocess.run([str(DIST / 'start.sh')], text=True, capture_output=True)
    assert rejected.returncode != 0 and 'configuration' in rejected.stderr.lower(), 'external configuration is required'
    example = (DIST / 'example/owned-application.properties').read_text()
    assert 'owned.attachments.enabled=false' in example
    assert 'owned.erp.mode=disabled' in example
    assert 'owned.report.replay.mode=disabled' in example
    assert 'owned.scheduler.mode=disabled' in example
    # Re-merging unchanged module inputs must produce the same application JAR.
    before = hashlib.sha256(app.read_bytes()).digest()
    sources = [
        ROOT / 'build/source-only-application/lims-owned-application.jar',
        *[
            ROOT / 'reconstituted/framework' / module['output_jar']
            for module in json.loads(
                (ROOT / 'docs/reconstitution/framework-module-registry.json').read_text()
            )['modules']
        ],
    ]
    package_environment = dict(os.environ)
    package_environment.update({
        'OWNED_PUBLIC_DEPENDENCY_DIR': str(ROOT / 'build/reconstituted-public-deps'),
        'OWNED_ADDITIONAL_DEPENDENCY_DIRS': str(
            ROOT / 'build/source-only-application/third-party-deps'
        ),
        'OWNED_THIRD_PARTY_DEPENDENCY_PROVENANCE': str(
            ROOT / 'build/source-only-application/third-party-dependencies.json'
        ),
        'OWNED_DISTRIBUTION_MODE': 'source-only-reconstituted',
    })
    result = subprocess.run(
        [sys.executable, str(ROOT / 'owned-distribution/package.py'), str(DIST)]
        + [str(source) for source in sources],
        text=True,
        capture_output=True,
        env=package_environment,
    )
    assert result.returncode == 0, result.stderr
    assert hashlib.sha256(app.read_bytes()).digest() == before, 'application JAR merge is not deterministic'
    # A complete clean rebuild must reproduce every shipped file, including provenance.
    first = {str(p.relative_to(DIST)): hashlib.sha256(p.read_bytes()).hexdigest()
             for p in DIST.rglob('*') if p.is_file()}
    result = subprocess.run([str(builder)], text=True, capture_output=True)
    assert result.returncode == 0, 'second clean build failed: ' + result.stderr[-2000:]
    second = {str(p.relative_to(DIST)): hashlib.sha256(p.read_bytes()).hexdigest()
              for p in DIST.rglob('*') if p.is_file()}
    assert first == second, 'distribution differs after clean rebuild: ' + str(sorted(
        key for key in set(first) | set(second) if first.get(key) != second.get(key)))
    # A differing class with the same name must stop packaging instead of
    # allowing classpath order to select one implementation silently.
    framework_target = ROOT / 'owned-framework/target'
    conflict = framework_target / 'distribution-conflicting-class.jar'
    conflicting_app = framework_target / 'distribution-conflicting-app.jar'
    try:
        with zipfile.ZipFile(conflict, 'w') as jar:
            jar.writestr('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class', b'conflicting')
        with zipfile.ZipFile(conflicting_app, 'w') as jar:
            jar.writestr('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class', b'primary')
        with tempfile.TemporaryDirectory(prefix='owned-distribution-conflict-') as temporary:
            result = subprocess.run([
                sys.executable, str(ROOT / 'owned-distribution/package.py'), temporary,
                str(conflicting_app), str(conflict)],
                text=True, capture_output=True, env=package_environment)
            assert result.returncode != 0 and 'conflicting duplicate' in result.stderr
    finally:
        conflict.unlink(missing_ok=True)
        conflicting_app.unlink(missing_ok=True)
    print('owned distribution package: PASS')


if __name__ == '__main__':
    main()
