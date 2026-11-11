#!/usr/bin/env python3
"""Validate the standalone distribution's public build/start boundary."""
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[2]


def file_hashes(directory):
    return {str(path.relative_to(directory)): hashlib.sha256(path.read_bytes()).hexdigest()
            for path in directory.rglob('*') if path.is_file()}


def verify_package_determinism(root, distribution, sources, environment):
    # The active distribution may be an immutable cache mounted by a running
    # application. Copy only the builder-supplied files; regenerate all package
    # outputs so stale files cannot make the comparison pass.
    before = file_hashes(distribution)
    with tempfile.TemporaryDirectory(prefix='owned-distribution-repackage-') as temporary:
        output = Path(temporary) / 'distribution'
        output.mkdir()
        for path in distribution.iterdir():
            if path.name in ('app', 'modules', 'lib', 'manifest.json', 'installation'):
                continue
            destination = output / path.name
            if path.is_dir():
                shutil.copytree(path, destination)
            else:
                shutil.copy2(path, destination)
        result = subprocess.run(
            [sys.executable, str(root / 'owned-distribution/package.py'), str(output)]
            + [str(source) for source in sources],
            text=True, capture_output=True, env=environment,
        )
        assert result.returncode == 0, result.stderr
        assert file_hashes(output)['app/lims-owned-application.jar'] == before[
            'app/lims-owned-application.jar'], 'application JAR merge is not deterministic'
        assert file_hashes(output) == before, 'distribution differs after repackaging'
    assert file_hashes(distribution) == before, 'repackaging changed the active distribution'


def prepare_build_snapshot(source_root, root, inherited_environment):
    """Snapshot repository inputs and keep profile paths inside that snapshot."""
    source_root = Path(source_root).resolve()
    root = Path(root).resolve()
    for name in ('project', 'reconstituted', 'scripts', 'docs', 'build-support',
                 'api-fixtures/schema', 'deployment/oracle', 'deployment/report-engine',
                 *[path.name for path in source_root.glob('owned-*') if path.is_dir()]):
        shutil.copytree(source_root / name, root / name,
                        ignore=shutil.ignore_patterns('target', '__pycache__', '.git'))
    environment = dict(inherited_environment)
    for key in ('EXTERNAL_SCHEMA_PROFILE', 'EXTERNAL_SCHEMA_MANIFEST'):
        selected = environment.get(key)
        if not selected:
            continue
        path = Path(selected).resolve()
        assert path.is_file(), key + ' input is missing: ' + str(path)
        if path.is_relative_to(source_root):
            destination = root / path.relative_to(source_root)
            # Explicit repository inputs may be outside the curated source
            # roots (for example one private deployment profile). Copy only
            # that selected file rather than reading the original checkout
            # while a supposedly isolated clean build is running.
            if not destination.is_file():
                destination.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(path, destination)
            environment[key] = str(destination)
        else:
            # External operator inputs remain explicit absolute paths. Never
            # discover an alternative from the parent planning repository.
            environment[key] = str(path)
    environment.update({
        # Neither clean build may short-circuit via the checkout's cache.
        'OWNED_DISTRIBUTION_REUSE': '0',
        'JAVA_HOME': str(Path(environment.get('JAVA_HOME', source_root / 'build/jdk1.8.0_391')).resolve()),
        'LIMS_PUBLIC_DEPENDENCY_DIR': str(Path(environment.get(
            'LIMS_PUBLIC_DEPENDENCY_DIR', source_root / 'build/reconstituted-public-deps')).resolve()),
        'LIMS_EXTERNAL_ARTIFACT_ROOT': str(Path(environment.get(
            'LIMS_EXTERNAL_ARTIFACT_ROOT', source_root / '.external-materials')).resolve()),
        'LIMS_PROJECT_ROOT': str(root / 'project'),
    })
    return environment


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
    # All compilation outputs and cache activation belong to this disposable
    # source snapshot, never the checkout's target or a mounted distribution.
    with tempfile.TemporaryDirectory(prefix='owned-distribution-build-') as temporary:
        root = Path(temporary) / 'source'
        environment = prepare_build_snapshot(ROOT, root, os.environ)
        verify_distribution(root, environment)


def verify_distribution(ROOT, build_environment):
    DIST = ROOT / 'owned-distribution/target/distribution'
    builder = ROOT / 'scripts/build-owned-distribution.sh'
    assert builder.is_file(), 'standalone distribution builder must exist'
    result = subprocess.run([str(builder)], text=True, capture_output=True, env=build_environment)
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
    assert (DIST / 'initialize-admin.sh').is_file(), 'one-time first-admin command must ship'
    assert (DIST / 'initialize-workflows.sh').is_file(), 'finite workflow command must ship'
    assert os.access(DIST / 'initialize-workflows.sh', os.X_OK), 'workflow launcher must be executable'
    workflow_guard = subprocess.run([str(DIST / 'initialize-workflows.sh')], text=True, capture_output=True)
    assert workflow_guard.returncode == 2, 'workflow launcher must reject missing private inputs before Java/DB'
    assert manifest['installation']['deployment_ready'] is False
    installation = DIST / 'installation'
    for arguments, expected_exit in ((['--check-plan'], 0), ([], 2)):
        checked = subprocess.run([
            sys.executable, str(installation / 'scripts/verify-combined-oracle-installation.py'), *arguments,
        ], cwd=installation, text=True, capture_output=True)
        assert checked.returncode == expected_exit, checked.stdout + checked.stderr
        assert json.loads(checked.stdout)['deployment_ready'] is False
    for name in ('core-schema', 'external-schema'):
        assert 'installation/deployment/oracle/' + name + '/manifest.json' in manifest['files']
    workflow_manifest = json.loads((installation / 'deployment/oracle/workflow/manifest.json').read_text())
    workflow_definitions = json.loads((installation / 'deployment/oracle/workflow/definitions.json').read_text())
    assert workflow_manifest['schema'] == 'owned-workflow-package-v1'
    assert workflow_definitions['schema'] == 'owned-workflow-definitions-v1'
    assert manifest['installation']['workflow']['counts'] == workflow_manifest['counts']
    assert manifest['installation']['workflow']['deployment_ready'] is False
    assert workflow_manifest['status'] == 'partial' and workflow_manifest['deployment_ready'] is False
    assert 'installation/deployment/oracle/workflow/manifest.json' in manifest['files']
    template_script = installation / 'scripts/install-import-template-assets.py'
    template_check = subprocess.run([sys.executable, str(template_script), '--check-package'],
                                    cwd=installation, text=True, capture_output=True)
    assert template_check.returncode == 0, template_check.stdout + template_check.stderr
    assert json.loads(template_check.stdout)['deployment_ready'] is False
    assert manifest['installation']['import_templates']['assets'] == 1
    assert manifest['installation']['import_templates']['blocked'] == 1
    assert 'installation/deployment/oracle/import-templates/assets/reportSendInfo.xlsx' in manifest['files']
    assert 'installation/deployment/report-engine/assets/commission-summary.cpt' in manifest['files']
    assert 'installation/deployment/report-engine/assets/contract-summary.cpt' in manifest['files']
    assert 'installation/deployment/report-engine/dependencies/external-contract-packprice-20261111.sql' in manifest['files']
    assert 'installation/scripts/install-report-engine.py' in manifest['files']
    assert not any('fine-core-' in name or name.endswith('FanRuan.lic') for name in manifest['files'])
    schema_contract = manifest['external_schema']
    expected_schema_mode = 'original-db-link'
    if build_environment.get('EXTERNAL_SCHEMA_PROFILE'):
        expected_schema_mode = json.loads(Path(
            build_environment['EXTERNAL_SCHEMA_PROFILE']).read_text())['mode']
    assert schema_contract['mode'] == expected_schema_mode
    assert 'provenance/external-schema.properties' in manifest['files']
    assert manifest['main_class'] == 'com.sunwayworld.emc.projectfunction.GikamWpjcApplication'
    assert manifest['duplicate_conflicts'] == [], 'runtime classpath may not hide differing classes'
    assert manifest['source_jar_hash_convention'].startswith('normalized module archives')
    assert len(manifest['module_jars']) == 8
    assert manifest['dependency_class_shadows'], 'original duplicate dependency classes must be documented'
    assert manifest['third_party_dependency_provenance'][0]['filename'] == 'qiyuesuo-sdk-vendored.jar'
    app = DIST / 'app/lims-owned-application.jar'
    with zipfile.ZipFile(app) as jar:
        embedded = jar.read('META-INF/lims-external-schema.json')
        assert hashlib.sha256(embedded).hexdigest() == schema_contract['contract_sha256']
        assert jar.read('META-INF/lims-external-schema.properties') == (
            DIST / 'provenance/external-schema.properties').read_bytes()
        for mapper in schema_contract['mappers']:
            assert hashlib.sha256(jar.read(mapper['path'])).hexdigest() == mapper['sha256']
        assert jar.namelist().count('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class') == 1
        assert 'com/sunwayworld/boot/ApplicationConfiguration.class' in jar.namelist()
        assert 'com/sunwayworld/owned/install/FirstAdministrator.class' in jar.namelist()
        assert 'com/sunwayworld/owned/install/WorkflowInstaller.class' in jar.namelist()
        # Source-only distribution must carry owned seams in the application
        # archive. Building the transitional owned-framework JAR alone does
        # not affect the deployed app classpath.
        assert 'com/sunwayworld/owned/internal/web/OwnedReagentWriteController.class' in jar.namelist()
        assert 'com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.class' in jar.namelist()
        assert not any('/src/test/' in name or name.endswith('ContractTest.class') for name in jar.namelist())
        assert not any(name.endswith('OwnedWebBrowserApplication.class') or name.endswith('OwnedInventoryBrowserApplication.class') for name in jar.namelist())
        assert len(jar.read('static/gikam/main/js/chunk-common.js')) > 1_000_000
        for frontend_entry in ('static/gikam/main/js/home.js', 'static/gikam/main/js/workspace.js'):
            assert b'/core/module/sys/licenses' not in jar.read(frontend_entry)
            assert b'/open/security/licenses' not in jar.read(frontend_entry)
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
    sources = [
        ROOT / 'build/source-only-application/lims-owned-application.jar',
        *[
            ROOT / 'reconstituted/framework' / module['output_jar']
            for module in json.loads(
                (ROOT / 'docs/reconstitution/framework-module-registry.json').read_text()
            )['modules']
        ],
    ]
    package_environment = dict(build_environment)
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
    verify_package_determinism(ROOT, DIST, sources, package_environment)
    # A complete clean rebuild must reproduce every shipped file, including provenance.
    first = file_hashes(DIST)
    clean_environment = dict(build_environment, OWNED_DISTRIBUTION_REUSE='0')
    result = subprocess.run([str(builder)], text=True, capture_output=True, env=clean_environment)
    assert result.returncode == 0, 'second clean build failed: ' + result.stderr[-2000:]
    second = file_hashes(DIST)
    assert first == second, 'distribution differs after clean rebuild: ' + str(sorted(
        key for key in set(first) | set(second) if first.get(key) != second.get(key)))
    # A differing class with the same name must stop packaging instead of
    # allowing classpath order to select one implementation silently.
    with tempfile.TemporaryDirectory(prefix='owned-distribution-conflict-') as temporary:
        conflict = Path(temporary) / 'distribution-conflicting-class.jar'
        conflicting_app = Path(temporary) / 'distribution-conflicting-app.jar'
        with zipfile.ZipFile(conflict, 'w') as jar:
            jar.writestr('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class', b'conflicting')
        with zipfile.ZipFile(conflicting_app, 'w') as jar:
            jar.writestr('com/sunwayworld/emc/projectfunction/GikamWpjcApplication.class', b'primary')
        result = subprocess.run([
            sys.executable, str(ROOT / 'owned-distribution/package.py'),
            str(Path(temporary) / 'distribution'), str(conflicting_app), str(conflict)],
            text=True, capture_output=True, env=package_environment)
        assert result.returncode != 0 and 'conflicting duplicate' in result.stderr
    print('owned distribution package: PASS')


if __name__ == '__main__':
    main()
