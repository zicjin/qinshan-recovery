#!/usr/bin/env python3
"""Finite template asset installation through the offline CLI, temporary files only."""
import hashlib
import io
import json
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/install-import-template-assets.py'


class ImportTemplateAssetsTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix='import-template-assets-')
        self.root = Path(self.temporary.name)
        self.package = self.root / 'checkout/deployment/oracle/import-templates'
        self.package.mkdir(parents=True)
        self.workdir = self.root / 'application-work'
        self.workdir.mkdir()
        raw = io.BytesIO()
        with zipfile.ZipFile(raw, 'w', zipfile.ZIP_DEFLATED) as archive:
            archive.writestr('xl/sharedStrings.xml', '<sst><si><t>Task</t></si></sst>')
        self.payload = raw.getvalue()
        self.source = self.package / 'assets/reportSendInfo.xlsx'
        self.source.parent.mkdir()
        self.source.write_bytes(self.payload)
        self.relative = 'files/imp/20250218/' + 'a' * 32 + '/模板.xlsx'
        self.manifest = {
            'schema': 'owned-import-template-assets-v1', 'status': 'partial',
            'deployment_ready': False, 'source_class': 'current-production-at-capture',
            'assets': [{
                'template_code': 'reportSendInfo', 'file_id': '1234567',
                'target_id': 'T_CORE_IMPORT_TMPL$2345678',
                'source': 'assets/reportSendInfo.xlsx', 'relative_path': self.relative,
                'bytes': len(self.payload),
                'sha256': hashlib.sha256(self.payload).hexdigest(), 'source_sha256': 'b' * 64,
            }],
            'blocked': [{'template_code': 'grjl', 'reason': 'source-no-file-and-no-mapped-columns'}],
            'source_artifacts': [{'path': 'private/technical-capture.json', 'sha256': 'c' * 64}],
            'transformations': ['Remove private author and editing metadata.'],
        }
        self.save()

    def tearDown(self):
        self.temporary.cleanup()

    def save(self):
        (self.package / 'manifest.json').write_text(json.dumps(self.manifest, ensure_ascii=False), encoding='utf-8')

    def run_cli(self, *arguments, script=SCRIPT, cwd=None):
        return subprocess.run([sys.executable, str(script), '--manifest', str(self.package / 'manifest.json'),
                               *map(str, arguments)], text=True, capture_output=True, cwd=cwd)

    def assert_rejected_without_files(self, *arguments):
        result = self.run_cli(*arguments)
        self.assertNotEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertFalse((self.workdir / 'files').exists())
        return result

    def test_check_package_is_read_only_and_works_without_parent_or_private(self):
        relocated = self.root / 'checkout/scripts' / SCRIPT.name
        relocated.parent.mkdir()
        shutil.copyfile(SCRIPT, relocated)
        before = sorted(str(path.relative_to(self.root / 'checkout'))
                        for path in (self.root / 'checkout').rglob('*'))
        result = subprocess.run([sys.executable, str(relocated), '--check-package'],
                                cwd=self.root, text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt['status'], 'package-valid-partial')
        self.assertEqual(receipt['assets'], 1)
        self.assertFalse(receipt['deployment_ready'])
        self.assertFalse(receipt['strict_evidence'])
        self.assertEqual(before, sorted(str(path.relative_to(self.root / 'checkout'))
                         for path in (self.root / 'checkout').rglob('*')))
        self.assertFalse((self.root / 'checkout/private').exists())

    def test_install_preserves_exact_asset_and_repeated_run_is_idempotent(self):
        result = self.run_cli('--workdir', self.workdir)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt['status'], 'installed-partial')
        self.assertEqual(receipt['installed'], 1)
        self.assertFalse(receipt['deployment_ready'])
        target = self.workdir / self.relative
        self.assertEqual(target.read_bytes(), self.payload)
        before = target.stat()
        result = self.run_cli('--workdir', self.workdir)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt['installed'], 0)
        self.assertEqual(receipt['unchanged'], 1)
        self.assertEqual(target.stat().st_mtime_ns, before.st_mtime_ns)
        self.assertEqual(target.stat().st_ino, before.st_ino)
        self.assertEqual([path for path in self.workdir.rglob('*') if path.is_file()], [target])

    def test_actual_public_package_verifies_from_relocated_installation(self):
        installation = self.root / 'relocated-installation'
        script = installation / 'scripts' / SCRIPT.name
        script.parent.mkdir(parents=True)
        shutil.copyfile(SCRIPT, script)
        shutil.copytree(ROOT / 'deployment/oracle/import-templates',
                        installation / 'deployment/oracle/import-templates')
        result = subprocess.run([sys.executable, str(script), '--check-package'],
                                cwd=self.root, capture_output=True, text=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual(json.loads(result.stdout)['assets'], 1)
        self.assertFalse((installation / 'private').exists())
        self.assertEqual(list(installation.rglob('__pycache__')), [])

    def test_malformed_or_tampered_package_rejects_before_target_creation(self):
        original = json.loads(json.dumps(self.manifest))
        changes = [
            ('status', lambda m: m.update(status='ready')),
            ('deployment-ready', lambda m: m.update(deployment_ready=True)),
            ('wrong-source', lambda m: m.update(source_class='stock-reference')),
            ('missing-blocker', lambda m: m.update(blocked=[])),
            ('extra-asset', lambda m: m['assets'].append(dict(m['assets'][0]))),
            ('unexpected-field', lambda m: m['assets'][0].update(download_url='/fake-success')),
            ('source-traversal', lambda m: m['assets'][0].update(source='../reportSendInfo.xlsx')),
            ('target-traversal', lambda m: m['assets'][0].update(relative_path='files/imp/../../escape.xlsx')),
            ('target-absolute', lambda m: m['assets'][0].update(relative_path='/tmp/escape.xlsx')),
            ('target-backslash', lambda m: m['assets'][0].update(relative_path='files\\imp\\escape.xlsx')),
            ('invalid-date', lambda m: m['assets'][0].update(relative_path=self.relative.replace('20250218', '20250231'))),
            ('invalid-id', lambda m: m['assets'][0].update(file_id='1 OR 1=1')),
            ('wrong-target-table', lambda m: m['assets'][0].update(target_id='T_CORE_USER$123')),
            ('byte-count', lambda m: m['assets'][0].update(bytes=len(self.payload) + 1)),
            ('boolean-byte-count', lambda m: m['assets'][0].update(bytes=True)),
            ('digest', lambda m: m['assets'][0].update(sha256='0' * 64)),
            ('source-digest', lambda m: m['assets'][0].update(source_sha256='bad')),
        ]
        for name, mutate in changes:
            with self.subTest(name=name):
                self.manifest = json.loads(json.dumps(original))
                mutate(self.manifest)
                self.save()
                self.assert_rejected_without_files('--workdir', self.workdir)
        self.manifest = original
        self.save()
        self.source.write_bytes(self.payload + b'tamper')
        self.assert_rejected_without_files('--workdir', self.workdir)

    def test_duplicate_json_keys_reject_before_target_creation(self):
        path = self.package / 'manifest.json'
        path.write_text(path.read_text().replace('"status": "partial"',
                        '"status": "ready", "status": "partial"'))
        self.assert_rejected_without_files('--workdir', self.workdir)

    def test_source_symlinks_are_rejected(self):
        external = self.root / 'external.xlsx'
        external.write_bytes(self.payload)
        self.source.unlink()
        self.source.symlink_to(external)
        self.assert_rejected_without_files('--workdir', self.workdir)
        self.source.unlink()
        self.source.parent.rmdir()
        outside = self.root / 'outside-assets'
        outside.mkdir()
        (outside / 'reportSendInfo.xlsx').write_bytes(self.payload)
        self.source.parent.symlink_to(outside, target_is_directory=True)
        self.assert_rejected_without_files('--workdir', self.workdir)

    def test_manifest_symlink_is_rejected(self):
        path = self.package / 'manifest.json'
        outside = self.root / 'outside-manifest.json'
        path.rename(outside)
        path.symlink_to(outside)
        self.assert_rejected_without_files('--workdir', self.workdir)

    def test_check_package_cannot_also_install_and_install_requires_existing_absolute_workdir(self):
        self.assert_rejected_without_files('--check-package', '--workdir', self.workdir)
        self.assert_rejected_without_files()
        self.assert_rejected_without_files('--workdir', 'relative-workdir')
        self.assert_rejected_without_files('--workdir', self.root / 'missing-workdir')

    def test_workdir_or_existing_target_symlinks_never_write_outside_target(self):
        outside = self.root / 'outside-work'
        outside.mkdir()
        self.workdir.rmdir()
        self.workdir.symlink_to(outside, target_is_directory=True)
        result = self.run_cli('--workdir', self.workdir)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(list(outside.iterdir()), [])
        self.workdir.unlink()
        self.workdir.mkdir()
        (self.workdir / 'files').symlink_to(outside, target_is_directory=True)
        result = self.run_cli('--workdir', self.workdir)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(list(outside.iterdir()), [])
        (self.workdir / 'files').unlink()
        target = self.workdir / self.relative
        target.parent.mkdir(parents=True)
        external = outside / 'keep.xlsx'
        external.write_bytes(b'original')
        target.symlink_to(external)
        result = self.run_cli('--workdir', self.workdir)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(external.read_bytes(), b'original')
        self.assertTrue(target.is_symlink())

    def test_conflicting_existing_target_is_retained_and_no_temporary_file_remains(self):
        target = self.workdir / self.relative
        target.parent.mkdir(parents=True)
        target.write_bytes(b'operator-owned original')
        original = target.stat()
        result = self.run_cli('--workdir', self.workdir)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(target.read_bytes(), b'operator-owned original')
        self.assertEqual(target.stat().st_mtime_ns, original.st_mtime_ns)
        self.assertEqual([path for path in self.workdir.rglob('*') if path.is_file()], [target])

    def test_nondirectory_target_component_rejects_without_extra_files(self):
        (self.workdir / 'files').write_bytes(b'keep')
        result = self.run_cli('--workdir', self.workdir)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(list(self.workdir.iterdir()), [self.workdir / 'files'])
        self.assertEqual((self.workdir / 'files').read_bytes(), b'keep')


if __name__ == '__main__':
    unittest.main()
