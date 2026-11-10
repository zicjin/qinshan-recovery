#!/usr/bin/env python3
"""Exercise the real workflow installer CLI with a JDBC driver that cannot connect.

Only JDK 8 and the current Java source are compiled into a
temporary directory. No shared target, application, Oracle or Redis is used;
reaching the rejecting driver proves validation, never an installation success.
"""
from __future__ import annotations

import argparse
import copy
import hashlib
import json
from pathlib import Path
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
CLI = ROOT / 'owned-distribution/src/main/java/com/sunwayworld/owned/install/WorkflowInstaller.java'
DB_SECRET = 'WorkflowPrivateDbCanary#2026'
PAYLOAD_CANARY = 'WorkflowPrivatePayloadCanary2026'
GUARD = r'''
package oracle.jdbc;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;
public final class OracleDriver implements Driver {
  static { try { DriverManager.registerDriver(new OracleDriver()); }
           catch (SQLException e) { throw new ExceptionInInitializerError(e); } }
  public boolean acceptsURL(String url) { return url != null && url.startsWith("jdbc:"); }
  public Connection connect(String url, Properties values) throws SQLException {
    if (!acceptsURL(url)) return null;
    System.err.println("WF_TEST_DB_ATTEMPT");
    throw new SQLException("WorkflowPrivateDbCanary#2026 secret JDBC details", "TEST", 99999);
  }
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties values) { return new DriverPropertyInfo[0]; }
  public int getMajorVersion() { return 1; }
  public int getMinorVersion() { return 0; }
  public boolean jdbcCompliant() { return false; }
  public Logger getParentLogger() { return Logger.getGlobal(); }
}
'''

RENDER_CHECK = r'''
import com.sunwayworld.owned.install.WorkflowInstaller;
import java.nio.file.*;
import java.util.*;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
public final class WorkflowRenderCheck {
  static Document parse(String xml) throws Exception {
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    f.setNamespaceAware(true);
    f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    return f.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
  }
  static void require(boolean ok) { if (!ok) throw new AssertionError("materialized XML contract differs"); }
  public static void main(String[] args) throws Exception {
    WorkflowInstaller.Prepared p = WorkflowInstaller.prepare(Files.readAllBytes(Paths.get(args[0])), Paths.get(args[1]));
    String attack = "Literal & <script id=\"escape\">not executable</script> ' \" label";
    Map<String,String> names = new HashMap<>();
    names.put("ROLE_001", attack); names.put("ROLE_002", "Other role");
    names.put("USER_001", "Target user"); names.put("ORG_001", "Target organization");
    String xml = p.materializeXml("SYNTH-FOLDER", false, names);
    String svg = p.materializeXml("SYNTH-FOLDER", true, names);
    require(!xml.contains("__LIMS_") && !svg.contains("__LIMS_"));
    require(!xml.contains("<!DOCTYPE") && !svg.contains("<!DOCTYPE"));
    Document d = parse(xml), s = parse(svg);
    NodeList properties = d.getElementsByTagNameNS("*", "property");
    require(properties.getLength() == 3);
    boolean matched = false;
    for (int i=0;i<properties.getLength();i++) {
      Element e = (Element) properties.item(i);
      if ("101".equals(e.getAttribute("value"))) {
        require(attack.equals(e.getAttribute("name"))); matched = true;
      }
    }
    require(matched && d.getElementsByTagNameNS("*", "script").getLength() == 0);
    require(s.getElementsByTagNameNS("*", "script").getLength() == 0);
    require(s.getElementsByTagNameNS("*", "text").getLength() == 1);
    require(s.getDocumentElement().getTextContent().contains(attack));
    names.remove("ORG_001");
    boolean refused = false;
    try { p.materializeXml("SYNTH-FOLDER", true, names); } catch (Exception expected) { refused = true; }
    require(refused);
    names.put("ORG_001", "Target organization"); names.put("UNKNOWN_001", "Unknown identity");
    refused = false;
    try { p.materializeXml("SYNTH-FOLDER", true, names); } catch (Exception expected) { refused = true; }
    require(refused);
    names.remove("UNKNOWN_001"); names.put("ROLE_001", "__LIMS_ROLE_001_NAME__");
    refused = false;
    try { p.materializeXml("SYNTH-FOLDER", false, names); } catch (Exception expected) { refused = true; }
    require(refused);
    System.out.println("WORKFLOW_RENDER_LITERAL_DATA_VERIFIED");
  }
}
'''


class WorkflowInstallerTest(unittest.TestCase):
    prerequisite_root = ROOT

    @classmethod
    def setUpClass(cls):
        cls.temporary = tempfile.TemporaryDirectory(prefix='workflow-installer-test-')
        cls.temp = Path(cls.temporary.name)
        cls.jdk = cls.prerequisite_root / 'build/jdk1.8.0_391/bin'
        if not all(p.is_file() for p in (CLI, cls.jdk / 'javac', cls.jdk / 'java')):
            raise AssertionError('current WorkflowInstaller.java and pinned JDK 8 are required')
        guard = cls.temp / 'oracle/jdbc/OracleDriver.java'
        guard.parent.mkdir(parents=True)
        guard.write_text(GUARD, encoding='utf-8')
        render = cls.temp / 'WorkflowRenderCheck.java'
        render.write_text(RENDER_CHECK, encoding='utf-8')
        cls.cp = str(cls.temp)
        result = subprocess.run([str(cls.jdk / 'javac'), '-encoding', 'UTF-8',
                                 '-sourcepath', str(cls.temp), '-cp', cls.cp,
                                 '-d', str(cls.temp), str(CLI), str(guard), str(render)],
                                capture_output=True, text=True)
        if result.returncode:
            raise AssertionError('current workflow Java source did not compile:\n' + result.stdout + result.stderr)
        cls.java = [str(cls.jdk / 'java'), '-Dfile.encoding=UTF-8', '-cp', cls.cp,
                    'com.sunwayworld.owned.install.WorkflowInstaller']

    @classmethod
    def tearDownClass(cls):
        cls.temporary.cleanup()

    def setUp(self):
        self.case = tempfile.TemporaryDirectory(prefix='workflow-input-', dir=self.temp)
        self.addCleanup(self.case.cleanup)
        self.directory = Path(self.case.name)
        self.runtime = self.directory / 'runtime.properties'
        self.runtime.write_text('spring.datasource.url=jdbc:oracle:thin:@//127.0.0.1:1/FIXTURE\n'
                                'spring.datasource.username=WORKFLOW_TEST\n'
                                'spring.datasource.password=' + DB_SECRET + '\n', encoding='utf-8')
        self.manifest = self.directory / 'manifest.json'
        self.definitions = {
            'schema': 'owned-workflow-definitions-v1',
            'definitions': [{'proc_code': 'SYNTH-FOLDER', 'proc_version': 9,
                             'service_id': '151001', 'table_name': 'T_EMC_FOLDER',
                             'expression': None,
                             'diagram': '<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL">'
                                        '<process id="Process_fixture"><startEvent id="Start_fixture" statusCode="draft"/>'
                                        '<endEvent id="End_fixture" statusCode="done"/>'
                                        '<sequenceFlow id="Flow_fixture" sourceRef="Start_fixture" targetRef="End_fixture"/>'
                                        '</process></definitions>',
                             'svg': '<svg xmlns="http://www.w3.org/2000/svg"><text>Synthetic folder</text></svg>',
                             'organization_symbols': []}],
            'symbols': {'roles': [], 'users': [], 'organizations': []},
            'blocked': [{'proc_code': 'SYNTH-BLOCKED', 'proc_version': 2,
                         'service_id': '151099', 'table_name': 'T_SYNTH_BLOCKED',
                         'reason': 'latest-source-empty'}],
        }
        self.request = {'expected_schema': 'WORKFLOW_TEST', 'installer_user_id': 'workflow-installer',
                        'application_stopped': True, 'redis_namespace_unused': True,
                        'mapping': {'roles': {}, 'users': {}, 'organizations': {}}}
        self.write_package()

    def write_package(self):
        payload = json.dumps(self.definitions, ensure_ascii=False, separators=(',', ':')).encode('utf-8')
        (self.directory / 'definitions.json').write_bytes(payload)
        self.package = {'schema': 'owned-workflow-package-v1', 'status': 'partial',
                        'deployment_ready': False,
                        'counts': {'definitions': len(self.definitions['definitions']),
                                   'blocked': len(self.definitions['blocked']),
                                   **{k: len(v) for k, v in self.definitions['symbols'].items()}},
                        'definitions': {'path': 'definitions.json', 'sha256': hashlib.sha256(payload).hexdigest(),
                                        'bytes': len(payload)}}
        self.write_manifest()

    def write_manifest(self):
        self.manifest.write_text(json.dumps(self.package), encoding='utf-8')

    def invoke(self, request=None, *, raw=None, args=None):
        if raw is None:
            raw = json.dumps(self.request if request is None else request)
        result = subprocess.run(self.java + ([str(self.runtime), str(self.manifest)] if args is None else args),
                                input=raw, capture_output=True, text=True, timeout=20)
        output = result.stdout + result.stderr
        for private in (DB_SECRET, PAYLOAD_CANARY, 'jdbc:oracle:', 'secret JDBC details'):
            self.assertNotIn(private, output)
        return result, output

    def assert_pre_database_rejection(self, request=None, **kwargs):
        result, output = self.invoke(request, **kwargs)
        self.assertNotEqual(result.returncode, 0, output)
        self.assertNotIn('WF_TEST_DB_ATTEMPT', output)
        self.assertNotIn('"status":"installed"', output)

    def test_valid_source_bound_package_reaches_rejecting_jdbc_driver(self):
        result, output = self.invoke()
        self.assertNotEqual(result.returncode, 0, output)
        self.assertIn('WF_TEST_DB_ATTEMPT', output)
        self.assertIn('DATABASE_REJECTED', output)
        self.assertIn('99999', output)

    def test_request_exact_fields_types_and_explicit_offline_acknowledgments(self):
        for key in self.request:
            with self.subTest(missing=key):
                request = copy.deepcopy(self.request)
                del request[key]
                self.assert_pre_database_rejection(request)
        request = copy.deepcopy(self.request)
        request['password'] = PAYLOAD_CANARY
        self.assert_pre_database_rejection(request)
        for field in ('application_stopped', 'redis_namespace_unused'):
            for value in (False, None, 1, 'true'):
                with self.subTest(field=field, value=value):
                    request = copy.deepcopy(self.request)
                    request[field] = value
                    self.assert_pre_database_rejection(request)
        for key, value in (('expected_schema', 'SYS'), ('expected_schema', 'SYSTEM'),
                           ('expected_schema', 'OTHER.SCHEMA'), ('expected_schema', 'workflow_test'),
                           ('installer_user_id', ''), ('installer_user_id', 'user\ninvalid'),
                           ('installer_user_id', '<script>'), ('installer_user_id', 123)):
            with self.subTest(key=key, value=value):
                request = copy.deepcopy(self.request)
                request[key] = value
                self.assert_pre_database_rejection(request)

    def test_json_is_strict_and_does_not_echo_private_input(self):
        raw = json.dumps(self.request)
        invalid = [raw[:-1] + ',"installer_user_id":"' + PAYLOAD_CANARY + '"}',
                   raw + '{}', raw[:-1] + ',}', '{/*comment*/' + raw[1:],
                   raw.replace('"application_stopped": true', '"application_stopped": NaN'),
                   raw.replace('"expected_schema"', "'expected_schema'", 1),
                   '{"@type":"' + PAYLOAD_CANARY + '",' + raw[1:],
                   '{"broken":"' + PAYLOAD_CANARY + '\n"}', '[]', 'null', '"' + PAYLOAD_CANARY + '"',
                   ' ' * 65537]
        for number, value in enumerate(invalid):
            with self.subTest(number=number):
                self.assert_pre_database_rejection(raw=value)

    def test_missing_usage_runtime_settings_and_target_mismatch_are_rejected(self):
        self.assert_pre_database_rejection(args=[])
        self.assert_pre_database_rejection(args=[str(self.runtime)])
        self.assert_pre_database_rejection(args=[str(self.runtime), str(self.manifest), 'unexpected'])
        for text in ('', 'spring.datasource.url=jdbc:not-oracle:fixture\n',
                     'spring.datasource.url=jdbc:oracle:thin:@//127.0.0.1:1/FIXTURE\n'
                     'spring.datasource.username=DIFFERENT_SCHEMA\n'
                     'spring.datasource.password=' + DB_SECRET + '\n'):
            with self.subTest(runtime_shape=text.splitlines()[:1]):
                self.runtime.write_text(text, encoding='utf-8')
                self.assert_pre_database_rejection()

    def test_package_hash_byte_count_manifest_counts_and_scope_are_bound(self):
        original = copy.deepcopy(self.package)
        variants = []
        for key, value in (('sha256', '0' * 64), ('bytes', original['definitions']['bytes'] + 1),
                           ('bytes', str(original['definitions']['bytes'])), ('path', '../definitions.json'),
                           ('path', '/tmp/definitions.json')):
            candidate = copy.deepcopy(original)
            candidate['definitions'][key] = value
            variants.append(candidate)
        candidate = copy.deepcopy(original)
        candidate['definitions']['unexpected'] = PAYLOAD_CANARY
        variants.append(candidate)
        for key, value in (('schema', 'unknown'), ('status', 'complete'), ('deployment_ready', True)):
            candidate = copy.deepcopy(original)
            candidate[key] = value
            variants.append(candidate)
        for category in original['counts']:
            candidate = copy.deepcopy(original)
            candidate['counts'][category] += 1
            variants.append(candidate)
        candidate = copy.deepcopy(original)
        candidate['counts']['unknown'] = 0
        variants.append(candidate)
        for number, candidate in enumerate(variants):
            with self.subTest(number=number):
                self.package = candidate
                self.write_manifest()
                self.assert_pre_database_rejection()

    def test_package_paths_refuse_symlinks(self):
        original = self.directory / 'definitions.json'
        actual = self.directory / 'actual-definitions.json'
        original.rename(actual)
        original.symlink_to(actual)
        self.assert_pre_database_rejection()

    def test_manifest_symlink_and_definitions_invalid_utf8_refused(self):
        linked = self.directory / 'manifest-link.json'
        linked.symlink_to(self.manifest)
        self.assert_pre_database_rejection(args=[str(self.runtime), str(linked)])
        payload = b'{"schema":"owned-workflow-definitions-v1","invalid":"\xff"}'
        (self.directory / 'definitions.json').write_bytes(payload)
        self.package['definitions'].update(sha256=hashlib.sha256(payload).hexdigest(), bytes=len(payload))
        self.write_manifest()
        self.assert_pre_database_rejection()

    def symbolic_fixture(self):
        self.definitions['symbols'] = {'roles': ['ROLE_001', 'ROLE_002'],
                                       'users': ['USER_001'], 'organizations': ['ORG_001']}
        definition = self.definitions['definitions'][0]
        definition['organization_symbols'] = ['ORG_001']
        definition['diagram'] = definition['diagram'].replace('</process>',
            '<userTask id="Task_fixture" name="Synthetic approval" statusCode="approval">'
            '<candidateRolesElements><properties>'
            '<property value="__LIMS_ROLE_001_ID__" name="__LIMS_ROLE_001_NAME__"/>'
            '<property value="__LIMS_ROLE_002_ID__" name="__LIMS_ROLE_002_NAME__"/>'
            '</properties></candidateRolesElements>'
            '<candidateUsersElements><properties>'
            '<property value="__LIMS_USER_001_ID__" name="__LIMS_USER_001_NAME__"/>'
            '</properties></candidateUsersElements></userTask></process>')
        definition['svg'] = '<svg xmlns="http://www.w3.org/2000/svg"><text>' \
                            '__LIMS_ROLE_001_NAME__ __LIMS_ROLE_002_NAME__ __LIMS_USER_001_NAME__' \
                            '</text></svg>'
        self.request['mapping'] = {'roles': {'ROLE_001': '101', 'ROLE_002': '102'},
                                   'users': {'USER_001': 'fixture-actor'},
                                   'organizations': {'ORG_001': 'fixture-org'}}
        self.write_package()

    def test_complete_symbol_mapping_reaches_only_rejecting_database_guard(self):
        self.symbolic_fixture()
        result, output = self.invoke()
        self.assertNotEqual(result.returncode, 0, output)
        self.assertIn('WF_TEST_DB_ATTEMPT', output)
        self.assertIn('DATABASE_REJECTED', output)

    def test_materialization_preserves_target_display_names_as_literal_xml_data(self):
        self.symbolic_fixture()
        private = self.directory / 'request.json'
        private.write_text(json.dumps(self.request), encoding='utf-8')
        result = subprocess.run([str(self.jdk / 'java'), '-Dfile.encoding=UTF-8', '-cp', self.cp,
                                 'WorkflowRenderCheck', str(private), str(self.manifest)],
                                capture_output=True, text=True, timeout=20)
        output = result.stdout + result.stderr
        self.assertEqual(result.returncode, 0, output)
        self.assertIn('WORKFLOW_RENDER_LITERAL_DATA_VERIFIED', output)
        self.assertNotIn('WF_TEST_DB_ATTEMPT', output)
        self.assertNotIn(DB_SECRET, output)

    def test_mapping_requires_exact_symbols_valid_types_and_injective_targets(self):
        self.symbolic_fixture()
        base = copy.deepcopy(self.request)
        variants = []
        for category in ('roles', 'users', 'organizations'):
            candidate = copy.deepcopy(base)
            del candidate['mapping'][category]
            variants.append(candidate)
            candidate = copy.deepcopy(base)
            first = next(iter(candidate['mapping'][category]))
            del candidate['mapping'][category][first]
            variants.append(candidate)
            candidate = copy.deepcopy(base)
            candidate['mapping'][category]['UNKNOWN_001'] = '109'
            variants.append(candidate)
        for category, symbol, values in [
                ('roles', 'ROLE_001', [101, '00101', '-1', '0', '1.5', '1e3', '1 OR 1=1', '9223372036854775808']),
                ('users', 'USER_001', [None, '', 1, '<script>', 'actor\ninvalid', 'x' * 33]),
                ('organizations', 'ORG_001', [None, '', 1, 'org/invalid', 'x' * 17])]:
            for value in values:
                candidate = copy.deepcopy(base)
                candidate['mapping'][category][symbol] = value
                variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['mapping']['roles']['ROLE_002'] = candidate['mapping']['roles']['ROLE_001']
        variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['mapping']['extra'] = {}
        variants.append(candidate)
        for number, candidate in enumerate(variants):
            with self.subTest(number=number):
                self.assert_pre_database_rejection(candidate)

    def test_definitions_have_exact_schema_fields_and_nonempty_install_scope(self):
        base = copy.deepcopy(self.definitions)
        variants = []
        for key in ('schema', 'definitions', 'symbols', 'blocked'):
            candidate = copy.deepcopy(base)
            del candidate[key]
            variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['unexpected'] = PAYLOAD_CANARY
        variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['definitions'] = []
        variants.append(candidate)
        for key in base['definitions'][0]:
            candidate = copy.deepcopy(base)
            del candidate['definitions'][0][key]
            variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['definitions'][0]['unexpected'] = PAYLOAD_CANARY
        variants.append(candidate)
        for key, value in [('proc_version', '9'), ('proc_version', 1.5), ('proc_version', 0),
                           ('proc_version', True), ('proc_code', ''), ('service_id', '1/invalid'),
                           ('table_name', 'T_EMC_FOLDER;DROP TABLE X'), ('expression', 'true'),
                           ('diagram', ''), ('svg', ''), ('organization_symbols', ['ORG_999'])]:
            candidate = copy.deepcopy(base)
            candidate['definitions'][0][key] = value
            variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['definitions'].append(copy.deepcopy(candidate['definitions'][0]))
        variants.append(candidate)
        for number, candidate in enumerate(variants):
            with self.subTest(number=number):
                self.definitions = candidate
                # Malformed schema shapes are still hash-bound; only the
                # declared-count construction needs its original shape here.
                if set(candidate) >= {'definitions', 'symbols', 'blocked'}:
                    self.write_package()
                else:
                    payload = json.dumps(candidate).encode()
                    (self.directory / 'definitions.json').write_bytes(payload)
                    self.package['definitions'].update(sha256=hashlib.sha256(payload).hexdigest(), bytes=len(payload))
                    self.write_manifest()
                self.assert_pre_database_rejection()

    def test_xml_and_svg_refuse_executable_or_external_material(self):
        base = copy.deepcopy(self.definitions)
        invalid_xml = [
            '<!DOCTYPE definitions [<!ENTITY source "' + PAYLOAD_CANARY + '">]>' + base['definitions'][0]['diagram'],
            '<!DOCTYPE definitions SYSTEM "file:///private/' + PAYLOAD_CANARY + '">' + base['definitions'][0]['diagram'],
            base['definitions'][0]['diagram'].replace('<process ', '<process passScript="class Unsafe {}" ', 1),
            base['definitions'][0]['diagram'].replace('<process ', '<process passCallback="unsafeBean.execute" ', 1),
            base['definitions'][0]['diagram'].replace('<process ', '<process selectBySql="SELECT * FROM T_CORE_USER" ', 1),
            base['definitions'][0]['diagram'].replace('<process ', '<process dynamic="u:actor" ', 1),
            base['definitions'][0]['diagram'].replace('</process>', '<script>unsafe()</script></process>'),
            base['definitions'][0]['diagram'].replace('</process>', '__LIMS_UNKNOWN_001_ID__</process>'),
            '<definitions><broken></definitions>',
        ]
        invalid_svg = [
            '<!DOCTYPE svg SYSTEM "file:///private/' + PAYLOAD_CANARY + '"><svg/>',
            '<svg xmlns="http://www.w3.org/2000/svg"><script>unsafe()</script></svg>',
            '<svg xmlns="http://www.w3.org/2000/svg" onload="unsafe()"/>',
            '<svg xmlns="http://www.w3.org/2000/svg"><image href="https://invalid.example/asset"/></svg>',
            '<svg xmlns="http://www.w3.org/2000/svg"><text>__LIMS_ROLE_999_NAME__</text></svg>',
        ]
        for category, values in [('diagram', invalid_xml), ('svg', invalid_svg)]:
            for number, value in enumerate(values):
                with self.subTest(category=category, number=number):
                    self.definitions = copy.deepcopy(base)
                    self.definitions['definitions'][0][category] = value
                    self.write_package()
                    self.assert_pre_database_rejection()

    def test_symbol_registry_and_candidate_labels_are_exhaustively_bound(self):
        self.symbolic_fixture()
        base = copy.deepcopy(self.definitions)
        variants = []
        candidate = copy.deepcopy(base)
        candidate['symbols']['roles'].append('ROLE_001')
        variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['symbols']['roles'][0] = 'USER_001'
        variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['symbols']['unknown'] = []
        variants.append(candidate)
        for before, after in [
                ('__LIMS_ROLE_001_ID__', '__LIMS_ROLE_999_ID__'),
                ('name="__LIMS_ROLE_001_NAME__"', 'name="Captured private role"'),
                ('name="__LIMS_ROLE_001_NAME__"', 'name="__LIMS_ROLE_002_NAME__"'),
                ('__LIMS_ROLE_001_ID__', '101'),
                ('__LIMS_USER_001_ID__', '__LIMS_ROLE_001_ID__')]:
            candidate = copy.deepcopy(base)
            candidate['definitions'][0]['diagram'] = candidate['definitions'][0]['diagram'].replace(before, after)
            variants.append(candidate)
        candidate = copy.deepcopy(base)
        candidate['definitions'][0]['organization_symbols'] = []
        variants.append(candidate)
        for number, candidate in enumerate(variants):
            with self.subTest(number=number):
                self.definitions = candidate
                self.write_package()
                self.assert_pre_database_rejection()

    def test_unknown_placeholder_in_xml_comment_is_refused_before_database(self):
        original = self.definitions['definitions'][0]['diagram']
        self.definitions['definitions'][0]['diagram'] = original.replace(
            '</definitions>', '<!-- __LIMS_ROLE_999_ID__ --></definitions>')
        self.write_package()
        self.assert_pre_database_rejection()
        self.symbolic_fixture()
        original = self.definitions['definitions'][0]['diagram'].replace('<!-- __LIMS_ROLE_999_ID__ -->', '')
        for prefix, suffix in [('<!-- __LIMS_ROLE_001_ID__ -->', ''),
                               ('', '<!-- __LIMS_ROLE_001_NAME__ -->')]:
            with self.subTest(prefix=bool(prefix)):
                self.definitions['definitions'][0]['diagram'] = prefix + original + suffix
                self.write_package()
                self.assert_pre_database_rejection()

    def test_unused_standard_xlink_namespace_is_allowed_but_href_is_not(self):
        original = self.definitions['definitions'][0]['svg']
        self.definitions['definitions'][0]['svg'] = original.replace('<svg ',
            '<svg xmlns:xlink="http://www.w3.org/1999/xlink" ', 1)
        self.write_package()
        result, output = self.invoke()
        self.assertNotEqual(result.returncode, 0, output)
        self.assertIn('WF_TEST_DB_ATTEMPT', output)
        self.definitions['definitions'][0]['svg'] = self.definitions['definitions'][0]['svg'].replace(
            '<text>', '<text xlink:href="https://invalid.example/asset">', 1)
        self.write_package()
        self.assert_pre_database_rejection()

    def test_user_and_organization_mapping_cannot_collapse_distinct_symbols(self):
        self.symbolic_fixture()
        definition = self.definitions['definitions'][0]
        self.definitions['symbols']['users'].append('USER_002')
        self.definitions['symbols']['organizations'].append('ORG_002')
        definition['organization_symbols'].append('ORG_002')
        definition['diagram'] = definition['diagram'].replace('</candidateUsersElements>',
            '</candidateUsersElements><candidateUsersElements><properties>'
            '<property value="__LIMS_USER_002_ID__" name="__LIMS_USER_002_NAME__"/>'
            '</properties></candidateUsersElements>')
        definition['svg'] = definition['svg'].replace('</text>', ' __LIMS_USER_002_NAME__</text>')
        self.request['mapping']['users']['USER_002'] = 'second-actor'
        self.request['mapping']['organizations']['ORG_002'] = 'second-org'
        self.write_package()
        result, output = self.invoke()
        self.assertIn('WF_TEST_DB_ATTEMPT', output)
        self.assertNotEqual(result.returncode, 0)
        for category in ('users', 'organizations'):
            with self.subTest(category=category):
                request = copy.deepcopy(self.request)
                symbols = self.definitions['symbols'][category]
                request['mapping'][category][symbols[1]] = request['mapping'][category][symbols[0]]
                self.assert_pre_database_rejection(request)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    args, unittest_args = parser.parse_known_args()
    WorkflowInstallerTest.prerequisite_root = args.prerequisite_root.resolve()
    unittest.main(argv=[__file__, *unittest_args])


if __name__ == '__main__':
    main()
