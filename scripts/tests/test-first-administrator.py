#!/usr/bin/env python3
"""Compile the real first-admin CLI and test validation without any database.

A test-only JDBC driver rejects every connection before the network. Current
Java sources and existing source-built classes/public dependencies are used;
no application, Oracle, Redis, shared target or supplier JAR is started.
"""
from __future__ import annotations

import argparse
import copy
import json
from pathlib import Path
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
CLI = ROOT / 'owned-distribution/src/main/java/com/sunwayworld/owned/install/FirstAdministrator.java'
COMMON = ROOT / 'reconstituted/framework/owned-core-common/src/main/java'
SECRET = 'FixtureOnly2026#PrivateCanary'
DB_SECRET = 'DbPrivateCanary2026#'

HARNESS = r'''
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Enumeration;
import java.util.logging.Logger;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class FirstAdministratorGuard {
  public static void main(String[] args) throws Exception {
    if (args.length == 1 && "--encoder-contract".equals(args[0])) {
      Method helper = com.sunwayworld.owned.install.FirstAdministrator.class.getDeclaredMethod("encoder", Properties.class);
      helper.setAccessible(true);
      for (String type : new String[]{"SM3", "MD5"}) {
        Properties properties = new Properties();
        properties.setProperty("sunway.user.password.encoder.type", type);
        PasswordEncoder actual = (PasswordEncoder) helper.invoke(null, properties);
        StaticApplicationContext context = new StaticApplicationContext();
        Map<String,Object> values = new HashMap<>();
        values.put("sunway.user.password.encoder.type", type);
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", values));
        context.getBeanFactory().registerSingleton("objectPostProcessor", new ObjectPostProcessor<Object>() {
          public <O extends Object> O postProcess(O object) { return object; }
        });
        com.sunwayworld.boot.security.SunwaySecurityConfiguration original =
            new com.sunwayworld.boot.security.SunwaySecurityConfiguration();
        original.setApplicationContext(context);
        PasswordEncoder expected = original.passwordEncoder();
        String raw = "FixtureOnly2026#PrivateCanary";
        String encoded = actual.encode(raw);
        if (encoded.equals(raw) || !encoded.equals(expected.encode(raw)) || !expected.matches(raw, encoded)) {
          throw new AssertionError("original password encoder contract differs");
        }
        context.close();
      }
      System.out.println("FIRST_ADMIN_ORIGINAL_ENCODER_MATCHED");
      return;
    }
    // Force JDBC service initialization, then remove real drivers. The CLI's
    // later Class.forName sees the already initialized Oracle class and cannot
    // put a network driver ahead of this guard.
    Enumeration<Driver> existing = DriverManager.getDrivers();
    while (existing.hasMoreElements()) DriverManager.deregisterDriver(existing.nextElement());
    DriverManager.registerDriver(new Driver() {
      public boolean acceptsURL(String url) { return url != null && url.startsWith("jdbc:"); }
      public Connection connect(String url, Properties values) throws SQLException {
        if (!acceptsURL(url)) return null;
        System.err.println("FIRST_ADMIN_TEST_DB_ATTEMPT");
        throw new SQLException("private guard JDBC URL/credentials must never be printed");
      }
      public DriverPropertyInfo[] getPropertyInfo(String url, Properties values) { return new DriverPropertyInfo[0]; }
      public int getMajorVersion() { return 1; }
      public int getMinorVersion() { return 0; }
      public boolean jdbcCompliant() { return false; }
      public Logger getParentLogger() { return Logger.getGlobal(); }
    });
    com.sunwayworld.owned.install.FirstAdministrator.main(args);
  }
}
'''


class FirstAdministratorTest(unittest.TestCase):
    prerequisite_root = ROOT

    @classmethod
    def setUpClass(cls):
        cls.temporary = tempfile.TemporaryDirectory(prefix='first-administrator-test-')
        cls.temp = Path(cls.temporary.name)
        cls.jdk = cls.prerequisite_root / 'build/jdk1.8.0_391/bin'
        framework = sorted((cls.prerequisite_root / 'reconstituted/framework').glob('*/target/classes'))
        deps = sorted((cls.prerequisite_root / 'build/reconstituted-public-deps').glob('*.jar'))
        required = [CLI, cls.jdk / 'javac', cls.jdk / 'java']
        if len(framework) != 7 or not deps or any(not path.is_file() for path in required):
            raise AssertionError('first-admin source and seven precompiled source-only framework modules/public dependencies are required')
        if any(path.name.startswith('gikam-') or path.name.endswith('-run.jar') for path in deps):
            raise AssertionError('supplier JAR cannot be a test classpath input')
        sources = [CLI, COMMON / 'com/sunwayworld/boot/security/SunwaySecurityConfiguration.java',
                   COMMON / 'com/sunwayworld/framework/utils/EncryptUtils.java',
                   COMMON / 'com/sunwayworld/module/sys/password/service/impl/CorePasswordPolicyServiceImpl.java']
        harness = cls.temp / 'FirstAdministratorGuard.java'
        harness.write_text(HARNESS)
        sources.append(harness)
        owned_classes = cls.prerequisite_root / 'owned-framework/target/classes'
        cls.cp = ':'.join(map(str, [cls.temp, owned_classes, *framework, *deps]))
        logging = cls.temp / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Loggers><Root level="OFF"/></Loggers></Configuration>')
        cls.java = [str(cls.jdk / 'java'), '-Dlog4j.configurationFile=' + str(logging), '-cp', cls.cp]
        result = subprocess.run([str(cls.jdk / 'javac'), '-encoding', 'UTF-8', '-parameters',
                                 '-sourcepath', str(cls.temp), '-cp', cls.cp, '-d', str(cls.temp),
                                 *map(str, sources)], capture_output=True, text=True)
        if result.returncode:
            raise AssertionError('current first-admin Java sources did not compile:\n' + result.stdout + result.stderr)

    @classmethod
    def tearDownClass(cls):
        cls.temporary.cleanup()

    def setUp(self):
        self.config = self.temp / 'runtime.properties'
        self.config.write_text('spring.datasource.url=jdbc:oracle:thin:@//127.0.0.1:1/FIXTURE\n'
                               'spring.datasource.username=INSTALL_TEST\n'
                               'spring.datasource.password=' + DB_SECRET + '\n'
                               'sunway.user.password.encoder.type=SM3\n')
        self.request = {
            'expected_schema': 'INSTALL_TEST', 'user_id': 'install-fixture', 'user_name': 'Installer fixture',
            'company_id': 'INSTALLCOMP', 'company_name': 'Install company',
            'department_id': 'INSTALLDEPT', 'department_name': 'Install department',
            'role_name': 'Installation administrator', 'password': SECRET,
            'policy': {'min_length': 16, 'max_length': 64, 'min_number': 1, 'min_lowercase': 1,
                       'min_uppercase': 1, 'min_special_characters': 1, 'composition': 4,
                       'max_failed_attempts': 5, 'expiry_days': 90, 'expiry_warning_days': 7,
                       'history_length': 5, 'reuse_days': 30,
                       'expired_policy': 'mandatory', 'expired_max_uses': 0},
        }

    def invoke(self, request=None, *, raw=None, args=None):
        if raw is None:
            raw = json.dumps(self.request if request is None else request)
        try:
            result = subprocess.run(self.java + ['FirstAdministratorGuard']
                                    + ([str(self.config)] if args is None else args),
                                    input=raw, capture_output=True, text=True, timeout=20)
        except subprocess.TimeoutExpired:
            self.fail('first-admin validation or database guard exceeded the bounded timeout')
        output = result.stdout + result.stderr
        self.assertNotIn(SECRET, output)
        self.assertNotIn(DB_SECRET, output)
        self.assertNotIn('jdbc:oracle:', output)
        self.assertNotIn('private guard JDBC URL/credentials', output)
        return result, output

    def assert_rejected_before_database(self, request=None, *, code=None, **kwargs):
        result, output = self.invoke(request, **kwargs)
        self.assertNotEqual(result.returncode, 0)
        self.assertNotIn('FIRST_ADMIN_TEST_DB_ATTEMPT', output)
        if code:
            self.assertIn(code, output)

    def test_missing_or_unrecognized_input_fields_rejected_before_database(self):
        for missing in ('expected_schema', 'user_id', 'password', 'policy'):
            with self.subTest(missing=missing):
                request = copy.deepcopy(self.request)
                del request[missing]
                self.assert_rejected_before_database(request, code='INVALID_INPUT')
        request = copy.deepcopy(self.request)
        request['unknown_setting'] = True
        self.assert_rejected_before_database(request, code='INVALID_INPUT')

    def test_reserved_schema_and_user_rejected_before_database(self):
        for key, value in (('expected_schema', 'SYS'), ('expected_schema', 'SYSTEM'),
                           ('expected_schema', 'BAD.SCHEMA'), ('user_id', 'ADMIN')):
            with self.subTest(key=key, value=value):
                request = copy.deepcopy(self.request)
                request[key] = value
                self.assert_rejected_before_database(request, code='INVALID_USER_ID' if key == 'user_id'
                                                      else 'INVALID_TARGET_SCHEMA')

    def test_missing_inconsistent_or_unsatisfied_policy_rejected_before_database(self):
        for change in ({'min_length': 0}, {'max_length': 8}, {'composition': 5},
                       {'max_failed_attempts': 0}, {'expiry_days': 1, 'expiry_warning_days': 2}):
            with self.subTest(change=change):
                request = copy.deepcopy(self.request)
                request['policy'].update(change)
                self.assert_rejected_before_database(request, code='INVALID_POLICY')
        request = copy.deepcopy(self.request)
        del request['policy']['history_length']
        self.assert_rejected_before_database(request, code='INVALID_POLICY')
        request = copy.deepcopy(self.request)
        request['password'] = 'short'
        self.assert_rejected_before_database(request, code='PASSWORD_POLICY_REJECTED')

    def test_malformed_json_and_missing_runtime_config_are_rejected_safely(self):
        self.assert_rejected_before_database(raw='{"password":"' + SECRET + '",broken', code='INSTALLATION_FAILED')
        self.assert_rejected_before_database(args=[], code='USAGE_RUNTIME_PROPERTIES_AND_PRIVATE_JSON_STDIN')
        self.config.write_text('spring.datasource.password=' + DB_SECRET + '\n')
        self.assert_rejected_before_database(code='EXPLICIT_ENCODER_REQUIRED')

    def test_valid_explicit_install_request_reaches_guard_without_network_or_secrets(self):
        result, output = self.invoke()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('FIRST_ADMIN_TEST_DB_ATTEMPT', output)

    def test_original_encoder_matches_cli_helper_without_printing_hash(self):
        result, output = self.invoke(args=['--encoder-contract'])
        self.assertEqual(result.returncode, 0, output)
        self.assertIn('FIRST_ADMIN_ORIGINAL_ENCODER_MATCHED', output)
        self.assertNotRegex(output, r'\b[0-9a-fA-F]{32,64}\b')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    args, unittest_args = parser.parse_known_args()
    FirstAdministratorTest.prerequisite_root = args.prerequisite_root.resolve()
    unittest.main(argv=[__file__, *unittest_args])


if __name__ == '__main__':
    main()
