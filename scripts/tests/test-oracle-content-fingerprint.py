#!/usr/bin/env python3
"""Public CLI tests against a synthetic JDBC driver; never connects to Oracle."""
import json
from pathlib import Path
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
DRIVER = r'''
package oracle.jdbc;
import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.io.*;
public final class OracleDriver implements Driver {
  static { try { DriverManager.registerDriver(new OracleDriver()); } catch(SQLException e) { throw new RuntimeException(e); } }
  static String scenario;
  static boolean readonly, manual, snapshot;
  interface Call { Object call(String name, Object[] args) throws Throwable; }
  static Object proxy(Class<?> type, Call fn) {
    return Proxy.newProxyInstance(OracleDriver.class.getClassLoader(), new Class<?>[]{type}, (p,m,a)-> {
      if(m.getName().equals("close")) return null;
      return fn.call(m.getName(), a == null ? new Object[0] : a);
    });
  }
  static Object fail(String name) { throw new AssertionError("Unexpected JDBC call: " + name); }
  public Connection connect(String url, Properties props) throws SQLException {
    if(!url.startsWith("jdbc:oracle:thin:@fixture-")) throw new AssertionError("bad URL");
    if(!props.getProperty("password").equals("private-fixture-password")) throw new AssertionError("bad password");
    scenario = url.substring(url.lastIndexOf('-')+1);
    if(scenario.equals("sqlerror")) throw new SQLException("private-fixture-password private-endpoint row-body", "secret-state", 999);
    return (Connection)proxy(Connection.class, (n,a)-> {
      if(n.equals("setReadOnly")) { readonly = (Boolean)a[0]; return null; }
      if(n.equals("setAutoCommit")) { manual = !(Boolean)a[0]; return null; }
      if(n.equals("rollback")) return null;
      if(n.equals("createStatement")) return statement();
      if(n.equals("prepareStatement")) {
        if(!snapshot) throw new AssertionError("inventory outside snapshot");
        if(!a[0].equals("SELECT table_name FROM all_tables WHERE owner = ?")) throw new AssertionError("bad inventory SQL");
        return proxy(PreparedStatement.class, (pn,pa)-> {
          if(pn.equals("setString")) { if(!pa[1].equals("LIMS_RUNTIME_LOCAL") && !pa[1].equals("LIMS_BASELINE_LOCAL")) throw new AssertionError("bad schema"); return null; }
          if(pn.equals("executeQuery")) return rows(scenario.equals("empty") ? new Object[0][] : new Object[][]{{"检验\"表"}}, null);
          return fail(pn);
        });
      }
      return fail(n);
    });
  }
  static Object statement() {
    return proxy(Statement.class, (n,a)-> {
      if(n.equals("execute")) {
        if(!readonly || !manual || !a[0].equals("SET TRANSACTION READ ONLY")) throw new AssertionError("not readonly snapshot");
        snapshot = true; return false;
      }
      if(n.equals("setFetchSize")) return null;
      if(n.equals("executeQuery")) {
        if(!snapshot || !a[0].toString().matches("SELECT \\* FROM \\\"LIMS_(RUNTIME|BASELINE)_LOCAL\\\"\\.\\\"检验\\\"\\\"表\\\"")) throw new AssertionError("bad quoted SQL: " + a[0]);
        int[] types = {Types.VARCHAR, Types.NUMERIC};
        Object[][] data = {{null, new BigDecimal("1.00")}, {"", new BigDecimal("2.0")}, {"中文", new BigDecimal("0.00")}, {null, new BigDecimal("1")}};
        if(scenario.equals("reverse")) { Collections.reverse(Arrays.asList(data)); }
        if(scenario.equals("changed")) data[0][1] = new BigDecimal("3");
        if(scenario.equals("dedup")) data = Arrays.copyOf(data, 3);
        if(scenario.equals("unsupported")) { types = new int[]{Types.OTHER}; data = new Object[0][]; }
        if(scenario.equals("lob")) { types = new int[]{Types.BLOB, Types.CLOB}; data = new Object[][]{{new byte[]{0,1,(byte)255}, "A中文\ud83d\ude00"}, {null, ""}}; }
        if(scenario.equals("dates")) { types = new int[]{Types.DATE,Types.TIMESTAMP}; data = new Object[][]{{Timestamp.valueOf("2026-11-07 12:34:56"), Timestamp.valueOf("2026-11-07 12:34:56.123456789")}}; }
        if(scenario.equals("zerorows")) data = new Object[0][];
        if(scenario.equals("truncated")) throw new SQLException("private-fixture-password");
        return rows(data, types);
      }
      return fail(n);
    });
  }
  static ResultSet rows(Object[][] data, int[] types) {
    final int[] index = {-1};
    return (ResultSet)proxy(ResultSet.class, (n,a)-> {
      if(n.equals("next")) { if(types != null && scenario.equals("rowerror") && index[0] == 1) throw new SQLException("row-body private-fixture-password"); return ++index[0] < data.length; }
      if(n.equals("getMetaData")) return proxy(ResultSetMetaData.class, (mn,ma)-> {
        int c = ma.length > 0 ? ((Integer)ma[0])-1 : 0;
        if(mn.equals("getColumnCount")) return scenario.equals("nocolumns") ? 0 : types.length;
        if(mn.equals("getColumnName")) return c == 0 ? "列\"名" : "AMOUNT";
        if(mn.equals("getColumnType")) return types[c];
        if(mn.equals("getColumnTypeName")) return types[c] == Types.OTHER ? "UNSUPPORTED_SECRET_TYPE" : JDBCType.valueOf(types[c]).getName();
        if(mn.equals("getPrecision")) return 38;
        if(mn.equals("getScale")) return 2;
        if(mn.equals("isNullable")) return ResultSetMetaData.columnNullable;
        return fail(mn);
      });
      if(n.equals("getString") || n.equals("getBigDecimal")) return data[index[0]][((Integer)a[0])-1];
      if(n.equals("getBinaryStream")) { Object v=data[index[0]][((Integer)a[0])-1]; return v == null ? null : new ByteArrayInputStream((byte[])v) { public synchronized int read(byte[] b,int o,int n) { return super.read(b,o,Math.min(n,1)); } }; }
      if(n.equals("getCharacterStream") || n.equals("getNCharacterStream")) { Object v=data[index[0]][((Integer)a[0])-1]; return v == null ? null : new StringReader((String)v) { public int read(char[] b,int o,int n) throws IOException { return super.read(b,o,Math.min(n,1)); } }; }
      if(n.equals("getTimestamp")) {
        // A real driver interprets the Oracle wall clock using the supplied Calendar.
        Timestamp t=(Timestamp)data[index[0]][((Integer)a[0])-1];
        java.time.LocalDateTime local=t.toLocalDateTime();
        return Timestamp.from(local.toInstant(java.time.ZoneOffset.UTC));
      }
      return fail(n);
    });
  }
  public boolean acceptsURL(String s) { return true; }
  public DriverPropertyInfo[] getPropertyInfo(String s,Properties p) { return new DriverPropertyInfo[0]; }
  public int getMajorVersion(){return 1;} public int getMinorVersion(){return 0;}
  public boolean jdbcCompliant(){return false;}
  public java.util.logging.Logger getParentLogger(){return java.util.logging.Logger.getGlobal();}
}
'''


class OracleContentFingerprintTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temp = tempfile.TemporaryDirectory()
        cls.addClassCleanup(cls.temp.cleanup)
        cls.classes = Path(cls.temp.name)
        driver = cls.classes / 'oracle/jdbc/OracleDriver.java'
        driver.parent.mkdir(parents=True)
        driver.write_text(DRIVER)
        result = subprocess.run(['javac', '--release', '8', '-encoding', 'UTF-8', '-d', str(cls.classes),
                                 str(driver), str(ROOT / 'tools/api/OracleContentFingerprint.java')],
                                capture_output=True, text=True)
        if result.returncode:
            raise AssertionError(result.stderr)

    def run_cli(self, scenario='valid', schema='LIMS_RUNTIME_LOCAL', extra=(), zone='UTC', user='system'):
        config = f'url=jdbc:oracle:thin:@fixture-{scenario}\nuser={user}\npassword=private-fixture-password\nschema={schema}\n'
        return subprocess.run(['java', f'-Duser.timezone={zone}', '-cp', str(self.classes),
                               'OracleContentFingerprint', *extra], input=config, text=True,
                              capture_output=True, timeout=30)

    def test_rows_are_content_hashed_without_leaking_values(self):
        result = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stderr)
        report = json.loads(result.stdout)
        self.assertEqual(report['tables'][0]['row_count'], 4)
        self.assertEqual(report['tables'][0]['name'], '检验"表')
        self.assertNotIn('private-fixture-password', result.stdout + result.stderr)
        self.assertNotIn('中文', result.stdout + result.stderr)
        self.assertEqual(report['tables'][0]['content_sha256'], '804d80fdeddae9c458ba8c927115eeb2b5b4861a6378da2a38a94fae976a473c')

    def test_order_scale_and_owner_do_not_change_digest_but_content_and_duplicates_do(self):
        def report(scenario='valid', **kwargs):
            result = self.run_cli(scenario, **kwargs)
            self.assertEqual(result.returncode, 0, result.stderr)
            return json.loads(result.stdout)
        expected = report()
        self.assertEqual(expected['schema_sha256'], report('reverse')['schema_sha256'])
        self.assertEqual(expected['schema_sha256'], report(schema='LIMS_BASELINE_LOCAL')['schema_sha256'])
        self.assertNotEqual(expected['schema_sha256'], report('changed')['schema_sha256'])
        self.assertNotEqual(expected['schema_sha256'], report('dedup')['schema_sha256'])

    def test_blob_clob_streams_have_fixed_content_hash(self):
        result = self.run_cli('lob')
        self.assertEqual(result.returncode, 0, result.stderr)
        table = json.loads(result.stdout)['tables'][0]
        self.assertEqual(table['content_sha256'],
                         '1763d92a55144e87e8662849960d0af0037698c5727e5aa7cd6e7c94df8cc3b2')

    def test_dates_preserve_time_and_timestamp_nanos_independent_of_default_timezone(self):
        results = [self.run_cli('dates', zone=zone) for zone in ['UTC', 'Asia/Shanghai', 'America/New_York']]
        for result in results:
            self.assertEqual(result.returncode, 0, result.stderr)
            self.assertEqual(json.loads(result.stdout)['tables'][0]['content_sha256'],
                             '2370999ff55f5b9e20fcbcd51741dfcafb3e5dd4b78a1edef4f0c3816072cf0e')
        self.assertEqual(len({result.stdout for result in results}), 1)

    def test_fails_closed_without_partial_output_or_sql_error_details(self):
        for scenario, code in [('sqlerror', 'SQL_ERROR'), ('truncated', 'SQL_ERROR'), ('rowerror', 'SQL_ERROR'),
                               ('empty', 'EMPTY_OR_DUPLICATE_TABLE_METADATA'),
                               ('nocolumns', 'EMPTY_COLUMN_METADATA'),
                               ('unsupported', 'UNSUPPORTED_JDBC_TYPE:1111')]:
            with self.subTest(scenario=scenario):
                result = self.run_cli(scenario)
                self.assertEqual(result.returncode, 1)
                self.assertEqual(result.stdout, '')
                self.assertEqual(result.stderr.strip(), 'fingerprint-failed:' + code)

    def test_empty_tables_still_hash_metadata_and_report_complete_type_coverage(self):
        result = self.run_cli('zerorows')
        self.assertEqual(result.returncode, 0, result.stderr)
        report = json.loads(result.stdout)
        self.assertEqual(report['tables'][0]['row_count'], 0)
        self.assertEqual(report['type_coverage'], [2, 12])
        self.assertEqual(len(report['schema_sha256']), 64)
        self.assertEqual(len(report['tables'][0]['columns']), 2)

    def test_only_explicit_local_owners_and_stdin_credentials_are_accepted(self):
        for kwargs, code in [({'schema': 'SYS'}, 'SCHEMA_NOT_ALLOWED'),
                             ({'schema': 'LIMS_RUNTIME_LOCAL;DELETE'}, 'SCHEMA_NOT_ALLOWED'),
                             ({'user': 'SYS'}, 'SYS_LOGIN_NOT_ALLOWED'),
                             ({'user': 'sys as sysoper'}, 'SYS_LOGIN_NOT_ALLOWED'),
                             ({'user': '"SYS"'}, 'SYS_LOGIN_NOT_ALLOWED'),
                             ({'extra': ('--password=private-fixture-password',)}, 'STDIN_PROPERTIES_REQUIRED')]:
            with self.subTest(kwargs=kwargs):
                result = self.run_cli(**kwargs)
                self.assertEqual(result.returncode, 1)
                self.assertEqual(result.stdout, '')
                self.assertEqual(result.stderr.strip(), 'fingerprint-failed:' + code)


if __name__ == '__main__':
    unittest.main()
