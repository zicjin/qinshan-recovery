#!/usr/bin/env python3
"""Execute the three actual MyBatis statements on H2 Oracle-mode fixture data.

Public test prerequisites are explicit; no application/database runtime is used.
H2 detects invalid grouping and verifies query grain, but is not Oracle acceptance.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
MAPPER = ROOT/'project/src/main/java/com/sunwayworld/emc/module/bp/sample/sample/mapper/conf/pf-sample-oracle-mapper.xml'
JAVA = r'''
import java.io.*;
import java.sql.*;
import java.util.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

public class MobileLabelOracleCheck {
  static Connection connection;
  static Configuration configuration;
  static void execute(String sql) throws Exception {
    try (Statement statement = connection.createStatement()) { statement.execute(sql); }
  }
  static List<Map<String,Object>> query(String id, Map<String,Object> parameters) throws Exception {
    BoundSql bound = configuration.getMappedStatement("mobile." + id).getBoundSql(parameters);
    if (!id.equals("selectMobileScanner")) oracleGrouping(bound.getSql());
    try (PreparedStatement statement = connection.prepareStatement(bound.getSql())) {
      for (int i = 0; i < bound.getParameterMappings().size(); i++) {
        statement.setObject(i + 1, parameters.get(bound.getParameterMappings().get(i).getProperty()));
      }
      List<Map<String,Object>> rows = new ArrayList<>();
      try (ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          Map<String,Object> row = new LinkedHashMap<>();
          for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
            row.put(result.getMetaData().getColumnLabel(i), result.getObject(i));
          }
          rows.add(row);
        }
      }
      return rows;
    }
  }
  // H2 accepts alias grouping and functional dependencies that Oracle rejects.
  // Verify the Oracle rule on the actual BoundSql: every nonaggregate projected
  // expression must be present in GROUP BY, without relying on a select alias.
  static List<String> expressions(String sql) {
    List<String> result = new ArrayList<>(); int depth = 0, start = 0; boolean quote = false;
    for (int i = 0; i < sql.length(); i++) {
      char c = sql.charAt(i);
      if (c == '\'') quote = !quote;
      if (!quote) {
        if (c == '(') depth++; else if (c == ')') depth--;
        else if (c == ',' && depth == 0) { result.add(sql.substring(start, i).trim()); start = i + 1; }
      }
    }
    result.add(sql.substring(start).trim()); return result;
  }
  static String normalized(String sql) { return sql.replaceAll("\\s+", "").toUpperCase(Locale.ROOT); }
  static void oracleGrouping(String sql) {
    int projectionStart = sql.indexOf("SELECT A.ID") + "SELECT ".length();
    int projectionEnd = sql.indexOf("FROM T_EMC_SAMPLE", projectionStart);
    int groupStart = sql.lastIndexOf("GROUP BY") + "GROUP BY".length();
    int groupEnd = sql.lastIndexOf(") T");
    Set<String> grouping = new HashSet<>();
    for (String group : expressions(sql.substring(groupStart, groupEnd))) grouping.add(normalized(group));
    for (String expression : expressions(sql.substring(projectionStart, projectionEnd))) {
      if (expression.startsWith("LISTAGG(")) continue;
      expression = expression.replaceFirst("(?i)\\s+AS\\s+LABELID$", "").replaceFirst("(?i)\\s+STATUS$", "");
      if (!grouping.contains(normalized(expression))) throw new AssertionError("Oracle nongrouped projection: " + expression);
    }
  }
  static void expect(Object wanted, Object actual) {
    if (!Objects.equals(wanted, actual)) throw new AssertionError("Expected " + wanted + ", got " + actual);
  }
  static void fixture() throws Exception {
    execute("CREATE TABLE T_EMC_SAMPLE (ID VARCHAR(20), STATUS VARCHAR(20), PROCESSSTATUS VARCHAR(20), SAMPLENO VARCHAR(20), ORDERCATEGORY VARCHAR(20), PROJID VARCHAR(20), CREATEDBYNAME VARCHAR(20), REMARK VARCHAR(100))");
    execute("CREATE TABLE T_EMC_PROJECT (ID VARCHAR(20), PROJNO VARCHAR(20), PROJNAME VARCHAR(20), TESTORGNAME VARCHAR(20), BIZCATEGORYID VARCHAR(20), STATUS VARCHAR(20))");
    execute("CREATE TABLE T_EMC_ORDER (ID VARCHAR(20), SAMPLEID VARCHAR(20), ORDERNO VARCHAR(20), SAMPLEBEGINDATE VARCHAR(20), ORDERDESC VARCHAR(20), FOLDERID VARCHAR(20))");
    execute("CREATE TABLE T_EMC_ORDER_TASK (ORDERID VARCHAR(20), CONTAINERCATEGORYCODE VARCHAR(20), CONTAINERGROUP VARCHAR(20), SCANNED VARCHAR(1), TESTNAME VARCHAR(20))");
    execute("CREATE TABLE T_EMC_FOLDER (ID VARCHAR(20), MONITORPOINTNAME VARCHAR(20))");
    execute("INSERT INTO T_EMC_PROJECT VALUES ('p1','P1','project','lab','biz','active'),('p2','P2','cancelled','lab','biz','cancel')");
    execute("INSERT INTO T_EMC_FOLDER VALUES ('f1','point')");
    execute("INSERT INTO T_EMC_SAMPLE VALUES ('s1','preLogged','done','S1','cat','p1','creator','note'),('s2','preLogged','done','S2','cat','p1','creator','note'),('s3','preLogged','done','S3','cat','p2','creator','note'),('s4','accepted','done','S4','cat','p1','creator','note'),('s5','preLogged','pending','S5','cat','p1','creator','note')");
    execute("INSERT INTO T_EMC_ORDER VALUES ('o1','s1','1234567890123','2026-11-07','desc','f1'),('o2','s2','1234567890124','2026-11-07','desc','f1'),('o3','s3','1234567890125','2026-11-07','desc','f1'),('o4','s4','1234567890126','2026-11-07','desc','f1'),('o5','s5','1234567890127','2026-11-07','desc','f1')");
    // Three test tasks share one scanned physical label; another label is unscanned.
    execute("INSERT INTO T_EMC_ORDER_TASK VALUES ('o1','B','1','1','Alpha'),('o1','B','1','1','Alpha'),('o1','B','1','1','Beta'),('o1','B','2','0','Gamma'),('o1',NULL,'3','1','Excluded'),('o2','B',NULL,NULL,'Delta'),('o3','B','1','1','Alpha'),('o4','B','1','1','Alpha'),('o5','B','1','1','Alpha')");
  }
  static void scanner() throws Exception {
    List<Map<String,Object>> rows = query("selectMobileScanner", Collections.emptyMap());
    expect(2, rows.size());
    Map<String,Map<String,Object>> byId = new HashMap<>();
    for (Map<String,Object> row : rows) byId.put((String)row.get("ID"), row);
    expect(new HashSet<>(Arrays.asList("s1","s2")), byId.keySet());
    Map<String,Object> first = byId.get("s1");
    expect(2L, ((Number)first.get("TOTAL")).longValue());
    expect(1L, ((Number)first.get("SUMNUMBER")).longValue());
    expect("1/2", first.get("NUM"));
    expect("S1", first.get("SAMPLENO"));
    expect("project", first.get("PROJNAME"));
    expect("0/1", byId.get("s2").get("NUM"));
    execute("DELETE FROM T_EMC_ORDER_TASK");
    expect(0, query("selectMobileScanner", Collections.emptyMap()).size());
  }
  static void detail() throws Exception {
    Map<String,Object> parameters = new HashMap<>(); parameters.put("id", "s1");
    List<Map<String,Object>> rows = query("selectDetailLabel", parameters);
    expect(2, rows.size());
    Map<String,Map<String,Object>> byLabel = new HashMap<>();
    for (Map<String,Object> row : rows) byLabel.put((String)row.get("LABELID"), row);
    Map<String,Object> first = byLabel.get("1234567890123B1");
    expect("Alpha,Beta", first.get("TESTNAME")); expect("已扫描", first.get("STATUS"));
    expect("creator", first.get("CREATEDBYNAME")); expect("note", first.get("REMARK"));
    expect("未扫描", byLabel.get("1234567890123B2").get("STATUS"));
    parameters.put("id", "s2");
    expect("未扫描", query("selectDetailLabel", parameters).get(0).get("STATUS"));
    parameters.put("id", "absent"); expect(0, query("selectDetailLabel", parameters).size());
  }
  static void label() throws Exception {
    Map<String,Object> parameters = new HashMap<>(); parameters.put("labelId", "1234567890123B1");
    List<Map<String,Object>> rows = query("selectLabelDetail", parameters); expect(1, rows.size());
    expect("Alpha,Beta", rows.get(0).get("TESTNAME")); expect("point", rows.get(0).get("MONITORPOINTNAME"));
    expect("P1", rows.get(0).get("PROJNO")); expect("1234567890123B1", rows.get(0).get("LABELID"));
    parameters.put("labelId", "1234567890124B"); expect(1, query("selectLabelDetail", parameters).size());
    parameters.put("labelId", "1234567890123B9"); expect(0, query("selectLabelDetail", parameters).size());
    parameters.put("labelId", "1234567890123B1' OR '1'='1"); expect(0, query("selectLabelDetail", parameters).size());
  }
  public static void main(String[] args) throws Exception {
    configuration = new Configuration();
    try (InputStream input = new FileInputStream(args[0])) {
      new XMLMapperBuilder(input, configuration, args[0], configuration.getSqlFragments()).parse();
    }
    try (Connection database = DriverManager.getConnection("jdbc:h2:mem:mobile;MODE=Oracle")) {
      connection = database; fixture();
      if (args[1].equals("scanner")) scanner();
      else if (args[1].equals("detail")) detail();
      else label();
    }
    System.out.println(args[1] + ": PASS (real MyBatis BoundSql and JDBC query results)");
  }
}
'''

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--mybatis', type=Path, default=ROOT/'build/reconstituted-public-deps/mybatis-3.5.6.jar')
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--case', choices=['scanner', 'detail', 'label', 'all'], default='all')
    args = parser.parse_args()
    for path in [args.jdk/'bin/javac', args.mybatis, args.h2]:
        if not path.is_file(): parser.error('missing public test prerequisite: ' + str(path))
    # Isolate the three complete original select elements, so unrelated unresolved
    # includes in other mapper statements cannot mask these query regressions.
    mapper = ET.parse(MAPPER).getroot()
    selected = ET.Element('mapper', {'namespace': 'mobile'})
    for name in ['selectMobileScanner', 'selectDetailLabel', 'selectLabelDetail']:
        element = mapper.find("select[@id='" + name + "']")
        if element is None: raise AssertionError('Missing statement: ' + name)
        selected.append(element)
    with tempfile.TemporaryDirectory(prefix='mobile-label-oracle-') as directory:
        folder = Path(directory)
        xml = folder/'mapper.xml'
        xml.write_text('<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">\n' + ET.tostring(selected, encoding='unicode'))
        java = folder/'MobileLabelOracleCheck.java'; java.write_text(JAVA)
        cp = ':'.join(map(str, [folder, args.mybatis, args.h2]))
        subprocess.run([str(args.jdk/'bin/javac'), '-encoding', 'UTF-8', '-cp', cp, '-d', str(folder), str(java)], check=True)
        for case in ['scanner', 'detail', 'label'] if args.case == 'all' else [args.case]:
            subprocess.run([str(args.jdk/'bin/java'), '-cp', cp, 'MobileLabelOracleCheck', str(xml), case], check=True)
    return 0

if __name__ == '__main__':
    raise SystemExit(main())
