#!/usr/bin/env python3
"""Test report staging integrity offline; optional integration uses authorized JARs.

Default tests use tiny, explicitly synthetic library inputs and replace only
Java compilation. Asset, SQL, provenance, path and digest validation execute
the real staging implementation. No engine is started and no SQL is executed.
"""
import argparse
import hashlib
import importlib.util
import json
from pathlib import Path
import shutil
import tempfile
from types import SimpleNamespace
import unittest
from unittest import mock

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/install-report-engine.py'
SOURCE = ROOT / 'deployment/report-engine'
SPEC = importlib.util.spec_from_file_location('report_engine_stager', SCRIPT)
STAGER = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(STAGER)


def fingerprint(path):
    raw = path.read_bytes()
    return {'bytes': len(raw), 'sha256': hashlib.sha256(raw).hexdigest()}


class ReportInstallationTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix='report-installation-')
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.materials = self.root / 'materials'
        shutil.copytree(SOURCE, self.materials)
        self.manifest_path = self.materials / 'manifest.json'
        self.manifest = json.loads(self.manifest_path.read_text(encoding='utf-8'))
        self.engine = self.root / 'authorized-engine-fixture'
        self.public = self.root / 'public-fixture'
        self.engine.mkdir()
        self.public.mkdir()
        for group, directory in ((self.manifest['third_party_engine']['libraries'], self.engine),
                                 (self.manifest['public_libraries'], self.public)):
            for entry in group:
                path = directory / entry['name']
                path.write_bytes(('synthetic-offline-library-fixture:' + entry['name']).encode())
                entry.update(fingerprint(path))
        self.output = self.root / 'webroot'
        self.output.mkdir()
        self.config = self.root / 'application-private.properties'
        self.config.write_text('spring.datasource.username=REPORT_INSTALL_FIXTURE\n'
                               'spring.datasource.password=do-not-copy-fixture-value\n')
        self.save_manifest()
        self.compile_calls = 0

    def save_manifest(self):
        self.manifest_path.write_text(json.dumps(self.manifest, indent=2) + '\n', encoding='utf-8')

    def compile_fixture(self, javac, libraries, classes):
        self.compile_calls += 1
        self.assertEqual(21, len(libraries))
        output = classes / 'com/sunwayworld/owned/report/engine/session/PortalReportSessionListener.class'
        output.parent.mkdir(parents=True)
        output.write_bytes(b'explicit-synthetic-compiler-fixture')

    def stage(self):
        args = SimpleNamespace(output=self.output, engine_lib=self.engine,
                               public_lib=[self.public], application_config=self.config,
                               javac=Path('unused-fixture-javac'))
        with mock.patch.object(STAGER, 'MATERIALS', self.materials), \
                mock.patch.object(STAGER, 'compile_listener', self.compile_fixture):
            return STAGER.stage(args)

    def rejected(self, pattern):
        with self.assertRaisesRegex(STAGER.StageError, pattern):
            self.stage()
        self.assertEqual([], list(self.output.iterdir()), 'failed staging must publish no partial webroot')

    def assets(self):
        return self.manifest.get('assets', [self.manifest['asset']])

    def configuration_entry(self, role):
        configuration = self.manifest.get('configuration')
        self.assertIsNotNone(configuration, 'report configuration bootstrap must be a declared installation input')
        return next(entry for entry in configuration['files'] if entry['role'] == role)

    def repin_configuration_provenance(self):
        entry = self.configuration_entry('provenance')
        entry.update(fingerprint(self.materials / entry['path']))
        self.save_manifest()

    def test_one_and_two_template_stage_resolve_only_declared_inputs(self):
        for count in (1, 2):
            with self.subTest(templates=count):
                if any(self.output.iterdir()):
                    shutil.rmtree(self.output)
                    self.output.mkdir()
                self.manifest['assets'] = json.loads((SOURCE / 'manifest.json').read_text())['assets'][:count]
                self.save_manifest()
                result = self.stage()
                self.assertEqual(count, result['templates'])
                self.assertFalse(result['database_connected'])
                self.assertFalse(result['license_or_private_config_copied'])
                receipt = json.loads((self.output / 'staging-manifest.json').read_text())
                self.assertEqual(count, len(receipt['assets']))
                self.assertEqual(count, len(receipt['metadata_sql_files']))
                for entry in receipt['assets']:
                    asset = self.output / entry['path']
                    self.assertEqual(entry['sha256'], fingerprint(asset)['sha256'])
                    self.assertNotIn(b'${OWNED_REPORT_', asset.read_bytes())
                    self.assertNotIn(b'do-not-copy-fixture-value', asset.read_bytes())
                for entry in receipt['metadata_sql_files']:
                    sql = self.output / entry['path']
                    self.assertEqual(entry['sha256'], fingerprint(sql)['sha256'])
                    self.assertNotIn(b'${', sql.read_bytes())
                self.assertFalse((self.output / self.config.name).exists())
                self.assertEqual(13, len(list((self.output / 'WEB-INF/lib').glob('fine-*.jar'))))
                self.assertFalse(list(self.output.rglob('*.lic')))

    def test_all_home_templates_stage_with_metadata(self):
        self.assertEqual(27, len(self.assets()))
        self.assertEqual(27, self.stage()['templates'])
        receipt = json.loads((self.output / 'staging-manifest.json').read_text())
        self.assertEqual(27, len(receipt['metadata_sql_files']))
        self.assertTrue(any('fxryjxtj/' in entry['path'] for entry in receipt['assets']))

    def test_asset_hash_tamper_is_rejected(self):
        asset = self.materials / self.assets()[0]['path']
        asset.write_bytes(asset.read_bytes() + b'\n<!-- tampered -->\n')
        self.rejected('input hash mismatch')

    def test_metadata_sql_tamper_is_rejected(self):
        for entry in self.assets():
            with self.subTest(metadata=entry.get('metadata_source', 'install-metadata.sql')):
                metadata = self.materials / entry.get('metadata_source', 'install-metadata.sql')
                original = metadata.read_bytes()
                metadata.write_bytes(original + b'\n-- tampered metadata fixture\n')
                self.rejected('hash mismatch|integrity|metadata')
                metadata.write_bytes(original)

    def test_metadata_source_digest_is_required(self):
        for count in (1, 2):
            with self.subTest(templates=count):
                self.manifest['assets'] = json.loads((SOURCE / 'manifest.json').read_text())['assets'][:count]
                entry = self.manifest['assets'][-1]
                entry.pop('metadata_sha256')
                if count == 1:
                    self.manifest['asset'].pop('metadata_sha256', None)
                self.save_manifest()
                with self.assertRaises((STAGER.StageError, KeyError)):
                    self.stage()
                self.assertEqual([], list(self.output.iterdir()))
                self.manifest['asset'] = json.loads((SOURCE / 'manifest.json').read_text())['asset']

    def test_provenance_tamper_is_rejected(self):
        dependency = next(item for item in self.manifest['dependencies'] if item['role'] == 'provenance')
        source = self.materials / dependency['path']
        source.write_bytes(source.read_bytes() + b' ')
        self.rejected('input hash mismatch')

    def test_library_hash_inventory_and_nonempty_output_rejections(self):
        lib = self.engine / self.manifest['third_party_engine']['libraries'][0]['name']
        original = lib.read_bytes()
        lib.write_bytes(original + b'tampered')
        self.rejected('input hash mismatch')
        lib.write_bytes(original)
        extra = self.engine / 'fine-unreviewed-11.0.jar'
        extra.write_bytes(b'unreviewed')
        self.rejected('inventory differs')
        extra.unlink()
        protected = self.output / 'must-not-overwrite'
        protected.write_bytes(b'original')
        with self.assertRaisesRegex(STAGER.StageError, 'not empty'):
            self.stage()
        self.assertEqual(b'original', protected.read_bytes())
        self.assertEqual([protected], list(self.output.iterdir()))

    def test_duplicate_asset_and_parent_path_are_rejected(self):
        self.manifest['assets'][1]['install_path'] = self.manifest['assets'][0]['install_path']
        self.save_manifest()
        self.rejected('invalid or duplicate')
        self.manifest['assets'][1]['install_path'] = '../outside.cpt'
        self.save_manifest()
        self.rejected('invalid or duplicate')

    def test_invalid_schema_does_not_stage(self):
        self.config.write_text('spring.datasource.username=BAD-SCHEMA\n')
        self.rejected('schema must match')

    def test_configuration_sql_and_provenance_are_verified_and_staged(self):
        result = self.stage()
        self.assertEqual('staged', result['status'])
        for entry in self.manifest['configuration']['files']:
            path = entry['path']
            self.assertEqual((self.materials / path).read_bytes(), (self.output / path).read_bytes())
        receipt = json.loads((self.output / 'staging-manifest.json').read_text())
        self.assertEqual(2, len(receipt['configuration_files']))
        self.assertEqual({'T_CORE_REPORT_CONFIG': 1, 'T_CORE_REPORT_CONFIG_LINE': 27},
                         self.manifest['configuration']['row_counts'])

    def test_configuration_sql_tamper_is_rejected(self):
        path = self.materials / self.configuration_entry('configuration')['path']
        path.write_bytes(path.read_bytes() + b'\n-- tampered bootstrap\n')
        self.rejected('hash mismatch|integrity|configuration')

    def test_configuration_provenance_tamper_is_rejected(self):
        path = self.materials / self.configuration_entry('provenance')['path']
        path.write_bytes(path.read_bytes() + b' ')
        self.rejected('hash mismatch|integrity|configuration')

    def test_missing_configuration_is_rejected(self):
        self.manifest.pop('configuration')
        self.save_manifest()
        self.rejected('configuration')

    def test_configuration_row_tamper_with_rehashed_file_is_rejected(self):
        path = self.materials / self.configuration_entry('provenance')['path']
        value = json.loads(path.read_text())
        value['rows'][1]['values']['REPORTCONFIGLINENO'] = 'unreviewed-report'
        path.write_text(json.dumps(value) + '\n')
        self.repin_configuration_provenance()
        self.rejected('configuration|provenance|row|digest|hash')

    def test_configuration_sql_link_tamper_with_rehashed_provenance_is_rejected(self):
        path = self.materials / self.configuration_entry('provenance')['path']
        value = json.loads(path.read_text())
        value['installation_sql']['sha256'] = '0' * 64
        path.write_text(json.dumps(value) + '\n')
        self.repin_configuration_provenance()
        self.rejected('configuration|provenance|SQL|hash')

    def test_configuration_duplicate_or_parent_file_is_rejected(self):
        files = self.manifest['configuration']['files']
        files.append(dict(files[0]))
        self.save_manifest()
        self.rejected('configuration|file|scope')
        files.pop()
        files[0]['path'] = '../install-config.sql'
        self.save_manifest()
        self.rejected('configuration|path|file|scope')


def integration(args):
    if args.engine_lib is None or not args.public_lib:
        raise SystemExit('--integration requires --engine-lib and at least one --public-lib')
    with tempfile.TemporaryDirectory(prefix='report-installation-real-', dir=ROOT / 'private') as temporary:
        work = Path(temporary)
        config = work / 'fixture-private.properties'
        config.write_text('spring.datasource.username=REPORT_INSTALL_FIXTURE\n')
        output = work / 'webroot'
        output.mkdir()
        result = STAGER.stage(SimpleNamespace(output=output, engine_lib=args.engine_lib,
            public_lib=args.public_lib, application_config=config, javac=args.javac))
        assert result['database_connected'] is False
        assert result['license_or_private_config_copied'] is False
        assert result['templates'] == len(json.loads((SOURCE / 'manifest.json').read_text())['assets'])
        receipt = json.loads((output / 'staging-manifest.json').read_text())
        assert len(receipt['libraries']) == 21
        assert list((output / 'WEB-INF/classes').rglob('PortalReportSessionListener.class'))
        manifest = json.loads((SOURCE / 'manifest.json').read_text())
        assert len(receipt['configuration_files']) == 2
        for entry in manifest['configuration']['files']:
            assert fingerprint(output / entry['path']) == {'bytes': entry['bytes'], 'sha256': entry['sha256']}
        assert not list(output.rglob('*.lic'))
        print('Authorized offline report staging integration passed; no engine or database started.')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--integration', action='store_true')
    parser.add_argument('--engine-lib', type=Path)
    parser.add_argument('--public-lib', type=Path, action='append')
    parser.add_argument('--javac', type=Path, default=ROOT / 'build/jdk1.8.0_391/bin/javac')
    args = parser.parse_args()
    if args.integration:
        integration(args)
    result = unittest.TextTestRunner(verbosity=2).run(
        unittest.defaultTestLoader.loadTestsFromTestCase(ReportInstallationTest))
    return 0 if result.wasSuccessful() else 1


if __name__ == '__main__':
    raise SystemExit(main())
