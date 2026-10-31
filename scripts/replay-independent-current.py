#!/usr/bin/env python3
"""Rebuild and replay representative owned behavior from independent Git objects."""
import argparse
import datetime
import hashlib
import json
import os
from pathlib import Path
import shutil
import stat
import subprocess
import tempfile


ROOT = Path(__file__).resolve().parents[1]
TOP = ROOT.parent
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'


def run(command, cwd, label, timeout=1800, env=None):
    result = subprocess.run([str(item) for item in command], cwd=cwd, env=env,
                            text=True, capture_output=True, timeout=timeout)
    if result.returncode:
        raise RuntimeError(label + ' failed: ' + (result.stdout + result.stderr)[-3000:])
    return result.stdout + result.stderr


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def make_writable(path):
    """Make an immutable content-addressed tree removable."""
    if not path.exists():
        return
    for current, directories, files in os.walk(path, topdown=False):
        for name in files:
            try: os.chmod(Path(current) / name, stat.S_IRUSR | stat.S_IWUSR)
            except FileNotFoundError: pass
        for name in directories:
            try: os.chmod(Path(current) / name, stat.S_IRUSR | stat.S_IWUSR | stat.S_IXUSR)
            except FileNotFoundError: pass
        try: os.chmod(current, stat.S_IRUSR | stat.S_IWUSR | stat.S_IXUSR)
        except FileNotFoundError: pass


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path,
                        default=ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/independent-replay-current-20261024.json')
    args = parser.parse_args()
    source_commit = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=TOP, text=True).strip()
    project_commit = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=ROOT / 'project', text=True).strip()
    report = {
        'schema': 1, 'status': 'executed-fail', 'production_access': False,
        'scope': 'independent Git-object clean rebuild, owned distribution, personnel read, equipment create and consumable material lifecycle; synthetic Oracle and Redis only',
        'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
        'source_commit': source_commit, 'historical_project_commit': project_commit,
        'image_reference': IMAGE, 'checks': [], 'runner_reports': {},
    }
    base = Path(tempfile.mkdtemp(prefix='lims-independent-current-', dir='/home/ts/.cache'))
    base.chmod(0o700)
    checkout = base / 'checkout'
    try:
        run(['git', 'clone', '--no-hardlinks', '--no-checkout', '--', TOP, checkout], TOP, 'clone root')
        run(['git', 'checkout', '--detach', source_commit], checkout, 'checkout root')
        recovery = checkout / 'lims-recovery'
        project = recovery / 'project'
        project_backup = project.with_name('project-source')
        project.rename(project_backup)
        run(['git', 'clone', '--no-hardlinks', '--no-checkout', '--', ROOT / 'project', project], checkout, 'clone historical project')
        run(['git', 'checkout', '--detach', project_commit], project, 'checkout historical project')
        for alternate in (checkout / '.git/objects/info/alternates', project / '.git/objects/info/alternates'):
            if alternate.exists():
                raise RuntimeError('independent clone has Git alternates')
        if any(path.exists() for path in recovery.glob('owned-*/target')):
            raise RuntimeError('independent clone contains target cache before rebuild')
        if subprocess.check_output(['git', 'status', '--porcelain'], cwd=checkout, text=True).strip():
            raise RuntimeError('independent checkout is dirty before rebuild')
        report['checks'].append('independent Git objects, empty targets and clean source')
        env = dict(os.environ)
        for key in ('JAVA_HOME', 'LIMS_PROJECT_ROOT', 'ROOT'):
            env.pop(key, None)
        env['LC_ALL'] = 'C'
        env['PYTHONIOENCODING'] = 'ascii'
        run(['bash', 'scripts/build-prep.sh'], recovery, 'ASCII build preparation', 1800, env)
        package_env = dict(os.environ)
        for key in ('JAVA_HOME', 'LIMS_PROJECT_ROOT', 'ROOT', 'LC_ALL', 'PYTHONIOENCODING'):
            package_env.pop(key, None)
        run(['python3', 'owned-distribution/tests/test-package.py'], recovery,
            'owned distribution package', 1800, package_env)
        report['checks'].append('owned distribution deterministic rebuild')

        runner_jobs = [
            ('personnel-read', ['python3', 'owned-distribution/tests/run-browser.py',
                                '--output', '../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/independent-personnel-browser.json'], {}),
            ('personnel-qualification-distribution', ['python3', 'owned-web/src/test/browser/run-browser.py',
                                '--personnel-qualification-write', '--distribution',
                                '--output', '../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/independent-personnel-qualification-distribution.json'], {}),
            ('equipment-distribution-create', ['python3', 'owned-distribution/tests/run-equipment-browser.py',
                                  '--output', '../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/independent-equipment-distribution-browser.json'], {}),
            ('equipment-create', ['python3', 'owned-web/src/test/browser/run-equipment-browser.py',
                                  '--output', '../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/independent-equipment-browser.json'], {}),
            ('consumable-material-lifecycle', ['python3', 'owned-web/src/test/browser/run-inventory-browser.py',
                                               '--distribution', '--material-lifecycle-only',
                                               '--output', '../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/independent-consumable-material-browser.json'], {}),
            ('consumable-stock-lifecycle-serialized', ['python3', 'owned-web/src/test/browser/run-inventory-browser.py',
                                                       '--distribution', '--stock-only',
                                                       '--output', '../.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/independent-consumable-stock-browser.json'],
             {'SPRING_APPLICATION_JSON': '{"server":{"tomcat":{"threads":{"max":1}}}}'}),
        ]
        for name, command, overrides in runner_jobs:
            evidence = recovery / command[command.index('--output') + 1]
            try:
                runner_env = dict(package_env)
                runner_env.update(overrides)
                run(command, recovery, name, 1800, runner_env)
            except Exception as failure:
                if evidence.is_file():
                    failed_payload = json.loads(evidence.read_text(encoding='utf-8'))
                    report['runner_reports'][name] = {
                        'status': failed_payload.get('status'),
                        'reason': failed_payload.get('reason'),
                        'checks': len(failed_payload.get('checks', [])),
                        'diagnostic': failed_payload.get('diagnostic'),
                        'diagnostic_traceback': failed_payload.get('diagnostic_traceback'),
                        'application_diagnostic': failed_payload.get('application_diagnostic'),
                        'historical_project_commit': failed_payload.get('historical_project_commit'),
                        'diagnostic_log_tail': failed_payload.get('application_log_tail') or failed_payload.get('diagnostic_log_tail'),
                    }
                raise RuntimeError(str(failure) + '; runner report retained in private checkout')
            payload = json.loads(evidence.read_text(encoding='utf-8'))
            if payload.get('status') != 'executed-pass' or payload.get('production_access') is not False:
                raise RuntimeError(name + ' did not produce executed-pass isolated evidence')
            report['runner_reports'][name] = {
                'status': payload['status'], 'source_commit': payload.get('source_commit'),
                'source_dirty': payload.get('source_dirty'), 'checks': len(payload.get('checks', [])),
                'evidence_sha256': sha(evidence),
                'cleanup': payload.get('cleanup'),
            }
            report['checks'].append(name + ' executed-pass')
        report['artifact_sha256'] = {
            'manifest.json': sha(recovery / 'owned-distribution/target/distribution/manifest.json'),
            'app/lims-owned-application.jar': sha(recovery / 'owned-distribution/target/distribution/app/lims-owned-application.jar'),
        }
        report['status'] = 'executed-pass'
        report['cleanup'] = {'independent_checkout_removed': True, 'private_temporary_directory_removed': True}
    except Exception as failure:
        report['reason'] = str(failure)
        report['cleanup'] = {'independent_checkout_removed': False, 'private_temporary_directory_removed': False}
    finally:
        make_writable(base)
        shutil.rmtree(base, ignore_errors=True)
        report['cleanup'] = {'independent_checkout_removed': not checkout.exists(),
                             'private_temporary_directory_removed': not base.exists()}
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('independent current replay: ' + report['status'])
    if report.get('reason'):
        print(report['reason'])
    return 0 if report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
