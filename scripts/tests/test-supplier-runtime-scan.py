import subprocess
import tempfile
import unittest
import zipfile
from pathlib import Path

SCANNER = Path(__file__).resolve().parents[2] / 'scripts/reject-supplier-runtime.sh'

class RuntimeScan(unittest.TestCase):
    def scan(self, path):
        return subprocess.run(['bash', str(SCANNER), str(path)], capture_output=True, text=True)

    def test_compressed_nested_route_is_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            inner = root / 'inner.zip'
            with zipfile.ZipFile(inner, 'w', zipfile.ZIP_DEFLATED) as archive:
                archive.writestr('config.bin', b'/core/module/sys/licenses')
            outer = root / 'candidate.jar'
            with zipfile.ZipFile(outer, 'w', zipfile.ZIP_DEFLATED) as archive:
                archive.write(inner, 'BOOT-INF/lib/component.jar')
            self.assertNotEqual(self.scan(outer).returncode, 0)

    def test_private_jar_name_rejected_without_class_tokens(self):
        with tempfile.TemporaryDirectory() as directory:
            candidate = Path(directory) / 'app.jar'
            with zipfile.ZipFile(candidate, 'w') as archive:
                archive.writestr('BOOT-INF/lib/gikam-core-common-14.1.5.jar', b'irrelevant')
            self.assertNotEqual(self.scan(candidate).returncode, 0)

    def test_exact_native_name_rejected_even_when_empty(self):
        with tempfile.TemporaryDirectory() as directory:
            (Path(directory) / 'sunway-app.so').touch()
            self.assertNotEqual(self.scan(Path(directory)).returncode, 0)

    def test_business_field_and_third_party_license_allowed(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / 'business.json').write_text('{"license":"business certificate"}')
            (root / 'cell-license.xml').write_text('<License>third-party</License>')
            (root / 'license.lic').write_text('third-party input')
            (root / 'vendor.lic').write_text('third-party input')
            (root / 'NOTICE').write_text('Licensed under the Apache License')
            self.assertEqual(self.scan(root).returncode, 0)

    def test_bad_archive_fails_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            candidate = Path(directory) / 'bad.jar'
            candidate.write_bytes(b'not a zip')
            self.assertNotEqual(self.scan(candidate).returncode, 0)

if __name__ == '__main__':
    unittest.main()
