#!/usr/bin/env python3
"""Offline core DDL package contract; temporary files only, no Oracle."""
import hashlib
import json
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/verify-core-schema-package.py'


def sha(raw):
    return hashlib.sha256(raw).hexdigest()


class CoreSchemaPackageTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(prefix='core-schema-package-')
        self.root = Path(self.tmp.name)
        self.write('00-preflight.sql', '''DECLARE n NUMBER;
BEGIN
  IF SYS_CONTEXT('USERENV', 'SESSION_USER') IN ('SYS', 'SYSTEM') OR
     SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') <> SYS_CONTEXT('USERENV', 'SESSION_USER') THEN
    RAISE_APPLICATION_ERROR(-20110, 'Connect as the dedicated installation user');
  END IF;
  SELECT COUNT(*) INTO n FROM USER_OBJECTS;
  IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20111, 'Schema must be empty'); END IF;
  SELECT COUNT(*) INTO n FROM NLS_DATABASE_PARAMETERS
    WHERE PARAMETER='NLS_CHARACTERSET' AND VALUE='AL32UTF8';
  IF n <> 1 THEN RAISE_APPLICATION_ERROR(-20112, 'Wrong character set'); END IF;
END;
/
''')
        self.ddl = 'CREATE TABLE "T_CORE_EXAMPLE" ("ID" NUMBER NOT NULL, "TEXT" VARCHAR2(20 CHAR));\n'
        self.write('20-tables.sql', '-- BEGIN OBJECT TABLE T_CORE_EXAMPLE\n' + self.ddl
                   + '-- END OBJECT TABLE T_CORE_EXAMPLE\n')
        self.write('30-indexes.sql', '-- No indexes acquired yet.\n')
        self.write('40-routines.sql', '-- No routines acquired yet.\n')
        self.write('50-comments.sql', 'COMMENT ON COLUMN "T_CORE_EXAMPLE"."TEXT" IS \'CREATE TABLE is example text\';\n')
        self.write('90-verify.sql', "BEGIN NULL; END;\n/\n")
        self.write('object-registry.tsv', 'object_type\tname\tpath\tsource_class\n'
                   'TABLE\tT_CORE_EXAMPLE\t20-tables.sql\tcurrent-production-at-capture\n')
        self.write('install.sql', "WHENEVER SQLERROR EXIT FAILURE ROLLBACK\nSET DEFINE OFF\n"
                   "BEGIN\n  RAISE_APPLICATION_ERROR(-20100, 'Core schema package is partial');\nEND;\n/\n"
                   '@@00-preflight.sql\n@@20-tables.sql\n@@30-indexes.sql\n@@40-routines.sql\n@@50-comments.sql\n@@90-verify.sql\n')
        self.manifest = {
            'schema': 'core-schema-package-v1', 'status': 'partial', 'deployment_ready': False,
            'source_class': 'current-production-at-capture', 'captured_at': '2026-11-09',
            'target': {'owner': 'current-session-user', 'requires_empty_schema': True},
            # Provenance references do not become installation dependencies.
            'source_artifacts': [{'path': 'private/captured-ddl.json', 'sha256': 'a' * 64}],
            'objects': [{'object_type': 'TABLE', 'name': 'T_CORE_EXAMPLE', 'path': '20-tables.sql',
                         'source_sha256': 'b' * 64, 'rendered_sha256': sha(self.ddl.encode()),
                         'transformations': ['Remove exact source owner qualifier.']}],
            'excluded_objects': [], 'blockers': [{'id': 'missing-bootstrap', 'reason': 'Not acquired.'}],
            'files': [], 'registry': {'path': 'object-registry.tsv', 'sha256': ''}, 'counts': {},
        }
        self.pin()

    def tearDown(self):
        self.tmp.cleanup()

    def write(self, name, text):
        (self.root / name).write_text(text, encoding='utf-8')

    def pin(self):
        roles = {'install.sql': 'initializer', '00-preflight.sql': 'preflight',
                 '20-tables.sql': 'tables', '30-indexes.sql': 'indexes',
                 '40-routines.sql': 'stored-code', '50-comments.sql': 'comments',
                 '90-verify.sql': 'verifier', 'object-registry.tsv': 'registry'}
        self.manifest['files'] = [{'path': name, 'role': role,
                                  'sha256': sha((self.root / name).read_bytes()),
                                  'bytes': (self.root / name).stat().st_size}
                                 for name, role in roles.items()]
        self.manifest['registry']['sha256'] = sha((self.root / 'object-registry.tsv').read_bytes())
        self.write('manifest.json', json.dumps(self.manifest, ensure_ascii=False, indent=2) + '\n')

    def run_verifier(self, check=True):
        return subprocess.run([sys.executable, str(SCRIPT), '--manifest', str(self.root / 'manifest.json')]
                              + (['--check-package'] if check else []), capture_output=True, text=True)

    def assert_rejected(self):
        result = self.run_verifier()
        self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
        self.assertFalse(json.loads(result.stdout)['package_valid'])

    def test_partial_integrity_is_verifiable_without_private_or_parent(self):
        result = self.run_verifier()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        receipt = json.loads(result.stdout)
        self.assertTrue(receipt['package_valid'])
        self.assertFalse(receipt['deployment_ready'])
        self.assertEqual(receipt['objects'], 1)
        self.assertFalse((self.root / 'private').exists())

    def test_actual_portable_package_without_parent_or_private(self):
        package = ROOT / 'deployment/oracle/core-schema'
        self.assertTrue((package / 'manifest.json').is_file())
        checkout = self.root / 'isolated-checkout'
        (checkout / 'scripts').mkdir(parents=True)
        shutil.copy2(SCRIPT, checkout / 'scripts' / SCRIPT.name)
        shutil.copytree(package, checkout / 'deployment/oracle/core-schema')
        result = subprocess.run([sys.executable, str(checkout / 'scripts' / SCRIPT.name), '--check-package'],
                                cwd=checkout, capture_output=True, text=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        receipt = json.loads(result.stdout)
        self.assertTrue(receipt['package_valid'])
        self.assertGreater(receipt['objects'], 0)
        self.assertFalse(receipt['deployment_ready'])
        self.assertFalse((checkout / 'private').exists())
        self.assertFalse((checkout / '.scratch').exists())

    def test_external_package_in_standalone_checkout_without_private_inputs(self):
        checkout = self.root / 'external-isolated-checkout'
        script = checkout / 'scripts' / SCRIPT.name
        script.parent.mkdir(parents=True)
        shutil.copy2(SCRIPT, script)
        package = checkout / 'deployment/oracle/external-schema'
        shutil.copytree(ROOT / 'deployment/oracle/external-schema', package)
        command = [sys.executable, str(script), '--manifest', str(package / 'manifest.json')]
        result = subprocess.run(command + ['--check-package'], cwd=checkout, capture_output=True, text=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt['objects'], 55)
        self.assertTrue(receipt['package_valid'])
        self.assertFalse(receipt['deployment_ready'])
        result = subprocess.run(command, cwd=checkout, capture_output=True, text=True)
        self.assertEqual(result.returncode, 2, result.stdout + result.stderr)
        self.assertFalse((checkout / 'private').exists())
        self.assertFalse((checkout / '.scratch').exists())
        # Missing packaged SQL must not be replaced by an acquisition receipt.
        (package / '35-constraints.sql').unlink()
        result = subprocess.run(command + ['--check-package'], cwd=checkout, capture_output=True, text=True)
        self.assertNotEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertFalse(json.loads(result.stdout)['package_valid'])

    def test_bootstrap_provenance_rehashed_count_or_statement_tamper_is_rejected(self):
        package = self.root / 'copied-package'
        shutil.copytree(ROOT / 'deployment/oracle/core-schema', package)
        manifest = json.loads((package / 'manifest.json').read_text())
        reference = manifest.get('technical_bootstrap')
        self.assertIsNotNone(reference)
        provenance_path = package / reference['path']
        original = json.loads(provenance_path.read_text())
        for kind in ('count', 'statement'):
            provenance = json.loads(json.dumps(original))
            if kind == 'count':
                table = next(iter(provenance['row_counts']))
                provenance['row_counts'][table] += 1
            else:
                provenance['rows'][0]['statement_sha256'] = 'f' * 64
            provenance_path.write_text(json.dumps(provenance, ensure_ascii=False) + '\n')
            raw = provenance_path.read_bytes()
            for entry in manifest['files']:
                if entry['path'] == reference['path']:
                    entry['sha256'], entry['bytes'] = sha(raw), len(raw)
            (package / 'manifest.json').write_text(json.dumps(manifest))
            result = subprocess.run([sys.executable, str(SCRIPT), '--manifest', str(package / 'manifest.json'),
                                     '--check-package'], capture_output=True, text=True)
            self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
            self.assertFalse(json.loads(result.stdout)['package_valid'])

    def test_sqlplus_error_and_display_directives_before_guard_are_safe(self):
        initializer = (self.root / 'install.sql').read_text()
        self.write('install.sql', 'WHENEVER OSERROR EXIT FAILURE ROLLBACK\n'
                   + initializer.replace('SET DEFINE OFF', 'SET DEFINE OFF\nSET SERVEROUTPUT ON\nSET SQLBLANKLINES ON'))
        self.pin()
        result = self.run_verifier()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertFalse(json.loads(result.stdout)['deployment_ready'])

    def test_ready_status_requires_no_blockers_and_no_partial_rejection(self):
        self.manifest['status'] = 'ready'
        self.manifest['deployment_ready'] = True
        self.manifest['blockers'] = []
        self.pin()
        self.assert_rejected()
        initializer = (self.root / 'install.sql').read_text()
        beginning = initializer.index('BEGIN\n')
        ending = initializer.index('/\n', beginning) + 2
        self.write('install.sql', initializer[:beginning] + initializer[ending:])
        self.pin()
        result = self.run_verifier(check=False)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        receipt = json.loads(result.stdout)
        self.assertTrue(receipt['package_valid'])
        self.assertTrue(receipt['schema_ready'])
        self.assertIn('separate prerequisites', receipt['scope'])

    def test_default_gate_stops_shell_before_deployment(self):
        result = self.run_verifier(check=False)
        self.assertEqual(result.returncode, 2, result.stdout + result.stderr)
        result = subprocess.run(['bash', '-ec', 'python3 "$1" --manifest "$2"; echo continued',
                                 'bash', str(SCRIPT), str(self.root / 'manifest.json')],
                                capture_output=True, text=True)
        self.assertEqual(result.returncode, 2)
        self.assertNotIn('continued', result.stdout)

    def test_content_hash_tamper_is_rejected(self):
        self.write('20-tables.sql', self.ddl + '-- changed\n')
        self.assert_rejected()

    def test_rehashed_rendered_object_tamper_is_rejected(self):
        text = (self.root / '20-tables.sql').read_text().replace('20 CHAR', '40 CHAR')
        self.write('20-tables.sql', text)
        self.pin()
        self.assert_rejected()

    def test_registry_missing_duplicate_or_unregistered_object_is_rejected(self):
        for registry in ('object_type\tname\tpath\tsource_class\n',
                         (self.root / 'object-registry.tsv').read_text()
                         + 'TABLE\tT_CORE_EXAMPLE\t20-tables.sql\tcurrent-production-at-capture\n'):
            with self.subTest(registry=registry):
                self.write('object-registry.tsv', registry)
                self.pin()
                self.assert_rejected()

    def test_unregistered_create_outside_block_is_rejected(self):
        with (self.root / '30-indexes.sql').open('a') as stream:
            stream.write('CREATE INDEX EXTRA ON T_CORE_EXAMPLE(ID);\n')
        self.pin()
        self.assert_rejected()

    def test_partial_initializer_must_reject_before_any_ddl_or_child(self):
        good = (self.root / 'install.sql').read_text()
        for bad in (good.replace('RAISE_APPLICATION_ERROR', 'NULL; -- RAISE_APPLICATION_ERROR'),
                    '@@20-tables.sql\n' + good,
                    'CREATE TABLE EARLY(ID NUMBER);\n' + good,
                    good.replace('WHENEVER SQLERROR EXIT FAILURE ROLLBACK', 'WHENEVER SQLERROR CONTINUE'),
                    good.replace('BEGIN\n', 'BEGIN\n IF 1=0 THEN\n').replace('END;\n/', 'END IF;\nEND;\n/')):
            with self.subTest(bad=bad):
                self.write('install.sql', bad)
                self.pin()
                self.assert_rejected()

    def test_preflight_must_precede_ddl_and_cannot_change_target(self):
        initializer = (self.root / 'install.sql').read_text()
        self.write('install.sql', initializer.replace('@@00-preflight.sql\n@@20-tables.sql',
                                                     '@@20-tables.sql\n@@00-preflight.sql'))
        self.pin()
        self.assert_rejected()
        self.write('install.sql', initializer)
        good = (self.root / '00-preflight.sql').read_text()
        for forbidden in ('DELETE FROM T_CORE_EXAMPLE;\n', 'ALTER SESSION SET CURRENT_SCHEMA = SYSTEM;\n',
                          'EXECUTE IMMEDIATE \'DROP TABLE T_CORE_EXAMPLE\';\n', '@@20-tables.sql\n'):
            with self.subTest(forbidden=forbidden):
                self.write('00-preflight.sql', good + forbidden)
                self.pin()
                self.assert_rejected()
        for bad in (good.replace('USER_OBJECTS', 'USER_TABLES'),
                    good.replace('USER_OBJECTS', 'USER_TABLES') + '-- USER_OBJECTS\n',
                    good.replace("'SESSION_USER'", "'CLIENT_IDENTIFIER'") + '-- SESSION_USER\n'):
            with self.subTest(bad=bad):
                self.write('00-preflight.sql', bad)
                self.pin()
                self.assert_rejected()

    def test_bootstrap_allows_only_reviewed_static_configuration_in_one_transaction(self):
        bootstrap = '''WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
DECLARE n NUMBER;
BEGIN
 SELECT (SELECT COUNT(*) FROM T_CORE_I18N_CONFIG) + (SELECT COUNT(*) FROM T_CORE_MENU) INTO n FROM DUAL;
 IF n<>0 THEN RAISE_APPLICATION_ERROR(-20120, 'Bootstrap target configuration must be empty'); END IF;
END;
/
INSERT INTO T_CORE_I18N_CONFIG (ID, LOCALENAME) VALUES ('zh-cn', '中文');
INSERT INTO T_CORE_MENU (ID, MENUNAME, HREF) VALUES ('sample', 'UPDATE DELETE; literal
with ''quoted'' text', NULL);
COMMIT;
'''
        def pin_bootstrap(text):
            self.write('70-bootstrap.sql', text)
            self.pin()
            raw = (self.root / '70-bootstrap.sql').read_bytes()
            self.manifest['files'].append({'path': '70-bootstrap.sql', 'role': 'bootstrap',
                                          'sha256': sha(raw), 'bytes': len(raw)})
            self.write('manifest.json', json.dumps(self.manifest))

        initializer = (self.root / 'install.sql').read_text()
        self.write('install.sql', initializer.replace('@@90-verify.sql', '@@70-bootstrap.sql\n@@90-verify.sql'))
        pin_bootstrap(bootstrap)
        result = self.run_verifier()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        for bad in (bootstrap.replace('T_CORE_MENU (ID', 'T_CORE_USER (ID'),
                    bootstrap.replace('VALUES (\'sample\'', 'SELECT (\'sample\''),
                    bootstrap.replace('COMMIT;', 'DELETE FROM T_CORE_MENU;\nCOMMIT;'),
                    bootstrap.replace('IF n<>0', 'IF n=0'),
                    bootstrap.replace('(SELECT COUNT(*) FROM T_CORE_MENU)', '(SELECT COUNT(*) FROM T_CORE_TABLE)'),
                    bootstrap.replace('INSERT INTO T_CORE_MENU', 'COMMIT;\nINSERT INTO T_CORE_MENU'),
                    bootstrap.replace('COMMIT;', 'MERGE INTO T_CORE_MENU;\nCOMMIT;'),
                    bootstrap.replace('COMMIT;', 'CREATE TABLE OUTSIDE(ID NUMBER);\nCOMMIT;')):
            with self.subTest(bad=bad):
                pin_bootstrap(bad)
                self.assert_rejected()

    def test_secret_endpoint_user_and_unlisted_file_are_rejected(self):
        good = (self.root / '50-comments.sql').read_text()
        for bad in ('-- jdbc:oracle:thin:@//example.invalid:1521/service\n',
                    '-- https://example.invalid/private\n', 'CREATE USER app IDENTIFIED BY password;\n'):
            with self.subTest(bad=bad):
                self.write('50-comments.sql', good + bad)
                self.pin()
                self.assert_rejected()
        self.write('50-comments.sql', good)
        self.write('unlisted.sql', 'CREATE TABLE HIDDEN(ID NUMBER);\n')
        self.pin()
        self.assert_rejected()

    def test_dictionary_bootstrap_remains_finite_technical_configuration(self):
        bootstrap = '''WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
DECLARE n NUMBER;
BEGIN
 SELECT (SELECT COUNT(*) FROM T_CORE_CODE_CLASS) + (SELECT COUNT(*) FROM T_CORE_CODE_CATEGORY) + (SELECT COUNT(*) FROM T_CORE_CODE) INTO n FROM DUAL;
 IF n<>0 THEN RAISE_APPLICATION_ERROR(-20120, 'Bootstrap target configuration must be empty'); END IF;
END;
/
INSERT INTO T_CORE_CODE_CLASS (ID,CODECLASSNAME,ORDERNO) VALUES ('sys','System',1);
INSERT INTO T_CORE_CODE_CATEGORY (ID,CODECATEGORYNAME,CODECLASSID,ORDERNO) VALUES ('yesorno','Yes or no','sys',1);
INSERT INTO T_CORE_CODE (ID,CODE,CODENAME,CODECATEGORYID,ORDERNO,LASTSUSPENDEDFLAG,I18NCODE) VALUES (1,'yes','Yes','yesorno',1,'0',NULL);
COMMIT;
'''
        self.write('70-bootstrap.sql', bootstrap)
        initializer = (self.root / 'install.sql').read_text()
        self.write('install.sql', initializer.replace('@@90-verify.sql', '@@70-bootstrap.sql\n@@90-verify.sql'))
        self.pin()
        raw = (self.root / '70-bootstrap.sql').read_bytes()
        self.manifest['files'].append({'path':'70-bootstrap.sql','role':'bootstrap',
                                      'sha256':sha(raw),'bytes':len(raw)})
        self.write('manifest.json',json.dumps(self.manifest))
        result = self.run_verifier()
        self.assertEqual(result.returncode,0,result.stdout + result.stderr)

        changed = bootstrap.replace('(ID,CODECLASSNAME,ORDERNO)', '(ID,CODECLASSNAME,CREATEDBYNAME)')
        self.write('70-bootstrap.sql',changed)
        raw = (self.root / '70-bootstrap.sql').read_bytes()
        entry = next(item for item in self.manifest['files'] if item['path']=='70-bootstrap.sql')
        entry['sha256'],entry['bytes'] = sha(raw),len(raw)
        self.write('manifest.json',json.dumps(self.manifest))
        self.assert_rejected()

    def test_lcdp_bootstrap_projection_excludes_identity_and_allows_only_public_dtd(self):
        bootstrap = '''WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
DECLARE n NUMBER;
BEGIN
 SELECT (SELECT COUNT(*) FROM T_LCDP_RESOURCE) INTO n FROM DUAL;
 IF n<>0 THEN RAISE_APPLICATION_ERROR(-20120, 'Bootstrap target configuration must be empty'); END IF;
END;
/
INSERT INTO T_LCDP_RESOURCE (ID,RESOURCENAME,RESOURCECATEGORY,PARENTID,ORDERNO,CONTENT,EFFECTVERSION,PATH,DELETEFLAG,DATATYPE,CLASSCONTENT,CLASSNAME) VALUES (1,'Mapper','mapper',0,1,TO_CLOB('<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">'),1,'emc.equiptInOut.mapper.ExampleOracleMapper','0',NULL,NULL,NULL);
COMMIT;
'''
        self.write('install.sql',(self.root/'install.sql').read_text().replace('@@90-verify.sql','@@70-bootstrap.sql\n@@90-verify.sql'))
        def pin_bootstrap(text):
            self.write('70-bootstrap.sql',text)
            self.pin()
            raw=(self.root/'70-bootstrap.sql').read_bytes()
            self.manifest['files'].append({'path':'70-bootstrap.sql','role':'bootstrap','sha256':sha(raw),'bytes':len(raw)})
            self.write('manifest.json',json.dumps(self.manifest))
        pin_bootstrap(bootstrap)
        result=self.run_verifier()
        self.assertEqual(result.returncode,0,result.stdout+result.stderr)
        for bad in (bootstrap.replace('CONTENT,EFFECTVERSION','CREATEDBYID,EFFECTVERSION'),
                    bootstrap.replace('http://mybatis.org/dtd/mybatis-3-mapper.dtd','http://example.invalid/private.dtd'),
                    bootstrap.replace('<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">',
                                      'http://mybatis.org/dtd/mybatis-3-mapper.dtd')):
            pin_bootstrap(bad)
            self.assert_rejected()

    def test_bootstrap_verifier_accepts_read_cursors_and_rejects_mutations(self):
        good = '''WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
DECLARE n NUMBER;
BEGIN
 SELECT COUNT(*) INTO n FROM T_CORE_MENU;
 FOR r IN (SELECT ID FROM T_CORE_MENU) LOOP
  IF r.ID IS NULL THEN RAISE_APPLICATION_ERROR(-20122, 'INSERT DELETE are message text'); END IF;
 END LOOP;
END;
/
'''
        initializer = (self.root / 'install.sql').read_text()
        self.write('install.sql', initializer.replace('@@90-verify.sql', '@@80-bootstrap-verify.sql\n@@90-verify.sql'))

        def pin_verifier(text):
            self.write('80-bootstrap-verify.sql', text)
            self.pin()
            raw = (self.root / '80-bootstrap-verify.sql').read_bytes()
            self.manifest['files'].append({'path': '80-bootstrap-verify.sql', 'role': 'bootstrap-verifier',
                                          'sha256': sha(raw), 'bytes': len(raw)})
            self.write('manifest.json', json.dumps(self.manifest))

        pin_verifier(good)
        result = self.run_verifier()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        for bad in ('INSERT INTO T_CORE_MENU(ID) VALUES(1);', 'UPDATE T_CORE_MENU SET ID=1;',
                    'DELETE FROM T_CORE_MENU;', 'CREATE TABLE BAD(ID NUMBER);', 'COMMIT;', 'ROLLBACK;',
                    "EXECUTE IMMEDIATE 'DROP TABLE BAD';", '@@70-bootstrap.sql'):
            with self.subTest(bad=bad):
                pin_verifier(good + bad + '\n')
                self.assert_rejected()

    def test_replacement_unicode_and_ipv4_are_rejected_without_rejecting_numbers(self):
        good = (self.root / '50-comments.sql').read_text()
        for bad in ('-- 10.100.2.196\n', '-- 127.0.0.1:18087\n',
                    '-- 255.255.255.255\n', '-- corrupted \ufffd identifier\n'):
            with self.subTest(bad=bad):
                self.write('50-comments.sql', good + bad)
                self.pin()
                self.assert_rejected()
        self.write('50-comments.sql', good)
        for value in ('10.100.2.196', 'bad \ufffd identifier'):
            with self.subTest(value=value):
                self.manifest['blockers'][0]['reason'] = value
                self.pin()
                self.assert_rejected()
        self.manifest['blockers'][0]['reason'] = 'Not acquired.'
        self.write('50-comments.sql', good + '-- Width 32767; NUMBER(20,15); 9999999999; 1.234; 256.999.999.999.\n')
        self.pin()
        result = self.run_verifier()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)

    def test_manifest_cannot_escape_package_or_claim_false_readiness(self):
        self.manifest['files'][0]['path'] = '../outside.sql'
        self.write('manifest.json', json.dumps(self.manifest))
        self.assert_rejected()
        self.pin()
        self.manifest['status'] = 'ready'
        self.manifest['deployment_ready'] = True
        self.write('manifest.json', json.dumps(self.manifest))
        self.assert_rejected()

    def test_symlink_and_duplicate_manifest_or_block_are_rejected(self):
        original = (self.root / '20-tables.sql').read_text()
        outside = self.root / 'outside.txt'
        outside.write_text(original)
        (self.root / '20-tables.sql').unlink()
        (self.root / '20-tables.sql').symlink_to(outside)
        self.assert_rejected()
        (self.root / '20-tables.sql').unlink()
        self.write('20-tables.sql', original)
        self.manifest['objects'].append(dict(self.manifest['objects'][0]))
        self.pin()
        self.assert_rejected()
        self.manifest['objects'].pop()
        self.write('20-tables.sql', original + original)
        self.pin()
        self.assert_rejected()

    def test_sequence_and_constraint_assets_preserve_phase_order(self):
        constraint = 'ALTER TABLE "T_CORE_EXAMPLE" ADD CONSTRAINT "T_CORE_EXAMPLE_PK" PRIMARY KEY ("ID");\n'
        self.write('35-constraints.sql', constraint)
        sequence = 'CREATE SEQUENCE "CORE_SEQUENCE" MINVALUE 1 MAXVALUE 999999999 START WITH 1 INCREMENT BY 1 NOCYCLE;\n'
        self.write('60-sequences.sql', '-- BEGIN OBJECT SEQUENCE CORE_SEQUENCE\n' + sequence
                   + '-- END OBJECT SEQUENCE CORE_SEQUENCE\n')
        self.manifest['objects'].append({
            'object_type': 'SEQUENCE', 'name': 'CORE_SEQUENCE', 'path': '60-sequences.sql',
            'source_sha256': 'd' * 64, 'rendered_sha256': sha(sequence.encode()),
            'transformations': ['Fresh-empty-schema seed at captured MIN_VALUE, not captured allocation state.'],
        })
        registry = (self.root / 'object-registry.tsv').read_text()
        self.write('object-registry.tsv', registry
                   + 'SEQUENCE\tCORE_SEQUENCE\t60-sequences.sql\tcurrent-production-at-capture\n')
        initializer = (self.root / 'install.sql').read_text().replace(
            '@@40-routines.sql', '@@35-constraints.sql\n@@40-routines.sql').replace(
            '@@90-verify.sql', '@@60-sequences.sql\n@@90-verify.sql')
        self.write('install.sql', initializer)
        self.pin()
        for path, role in (('35-constraints.sql', 'constraints'), ('60-sequences.sql', 'sequences')):
            raw = (self.root / path).read_bytes()
            self.manifest['files'].append({'path': path, 'role': role, 'sha256': sha(raw), 'bytes': len(raw)})
        self.write('manifest.json', json.dumps(self.manifest))
        result = self.run_verifier()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual(json.loads(result.stdout)['objects'], 2)

    def test_package_body_quoted_case_and_oracle_literal_create_text(self):
        body = '''CREATE OR REPLACE EDITIONABLE PACKAGE BODY "mixed_name" AS
  PROCEDURE EXAMPLE IS
    EXAMPLE_SQL VARCHAR2(100) := q'[CREATE TABLE SHOULD_NOT_BE_COUNTED(ID NUMBER)]';
  BEGIN NULL; END;
END;
/
'''
        self.write('40-routines.sql', '-- BEGIN OBJECT PACKAGE BODY mixed_name\n' + body
                   + '-- END OBJECT PACKAGE BODY mixed_name\n')
        self.manifest['objects'].append({
            'object_type': 'PACKAGE BODY', 'name': 'mixed_name', 'path': '40-routines.sql',
            'source_sha256': 'e' * 64, 'rendered_sha256': sha(body.encode()), 'transformations': [],
        })
        registry = (self.root / 'object-registry.tsv').read_text()
        self.write('object-registry.tsv', registry
                   + 'PACKAGE BODY\tmixed_name\t40-routines.sql\tcurrent-production-at-capture\n')
        self.pin()
        result = self.run_verifier()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual(json.loads(result.stdout)['objects'], 2)


if __name__ == '__main__':
    unittest.main()
