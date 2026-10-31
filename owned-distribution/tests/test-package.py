#!/usr/bin/env python3
"""Validate the standalone distribution's public build/start boundary."""
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[2]
DIST = ROOT / 'owned-distribution/target/distribution'


def main():
    lifecycle = subprocess.run(
        ['bash', str(ROOT / 'scripts/tests/test-api-evidence-gate.sh')],
        text=True, capture_output=True,
    )
    assert lifecycle.returncode == 0, 'evidence lifecycle gate failed: ' + lifecycle.stderr[-2000:]
    builder = ROOT / 'scripts/build-owned-distribution.sh'
    assert builder.is_file(), 'standalone distribution builder must exist'
    result = subprocess.run([str(builder)], text=True, capture_output=True)
    assert result.returncode == 0, 'standalone distribution build failed: ' + result.stderr[-2000:]
    manifest = json.loads((DIST / 'manifest.json').read_text())
    assert manifest['main_class'] == 'com.sunwayworld.emc.projectfunction.GikamWpjcApplication'
    assert manifest['duplicate_conflicts'] == [], 'runtime classpath may not hide differing classes'
    assert manifest['source_jar_hash_convention'].startswith('normalized module archives')
    app = DIST / 'app/lims-owned-application.jar'
    with zipfile.ZipFile(app) as jar:
        assert jar.namelist().count('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class') == 1
        assert not any('/src/test/' in name or name.endswith('ContractTest.class') for name in jar.namelist())
        assert not any(name.endswith('OwnedWebBrowserApplication.class') or name.endswith('OwnedInventoryBrowserApplication.class') for name in jar.namelist())
        assert 'static/index.html' in jar.namelist()
        factories = jar.read('META-INF/spring.factories').decode('utf-8')
        assert 'OwnedReportConfiguration' in factories and 'OwnedSchedulerConfiguration' in factories
        assert 'OwnedAttachmentConfiguration' in factories
    classes = {}
    identical_classes = []
    for jar_path in [app] + sorted((DIST / 'lib').glob('*.jar')):
        with zipfile.ZipFile(jar_path) as jar:
            for name in jar.namelist():
                if name.endswith('.class'):
                    data = jar.read(name)
                    if name in classes:
                        prior_jar, prior_data = classes[name]
                        assert prior_data == data, 'runtime conflicting duplicate class: ' + name
                        identical_classes.append(name)
                    else:
                        classes[name] = (jar_path.name, data)
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
    sources = [ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar',
               ROOT / 'owned-personnel/target/lims-owned-personnel-1.0.0.jar',
               ROOT / 'owned-equipment/target/lims-owned-equipment-1.0.0.jar',
               ROOT / 'owned-consumables/target/lims-owned-consumables-1.0.0.jar',
               ROOT / 'owned-attachments/target/lims-owned-attachments-1.0.0.jar',
               ROOT / 'owned-report/target/lims-owned-report-1.0.0.jar',
               ROOT / 'owned-scheduler/target/lims-owned-scheduler-1.0.0.jar',
               ROOT / 'owned-web/target/lims-owned-web-1.0.0.jar']
    result = subprocess.run([sys.executable, str(ROOT / 'owned-distribution/package.py'), str(DIST)]
                            + [str(source) for source in sources], text=True, capture_output=True)
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
    try:
        with zipfile.ZipFile(conflict, 'w') as jar:
            jar.writestr('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class', b'conflicting')
        with tempfile.TemporaryDirectory(prefix='owned-distribution-conflict-') as temporary:
            result = subprocess.run([
                sys.executable, str(ROOT / 'owned-distribution/package.py'), temporary,
                str(ROOT / 'owned-personnel/target/lims-owned-personnel-1.0.0.jar'), str(conflict)],
                text=True, capture_output=True)
            assert result.returncode != 0 and 'conflicting duplicate' in result.stderr
    finally:
        conflict.unlink(missing_ok=True)
    print('owned distribution package: PASS')


if __name__ == '__main__':
    main()
