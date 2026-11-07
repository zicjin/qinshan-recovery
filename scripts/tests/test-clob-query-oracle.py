#!/usr/bin/env python3
"""Real MyBatis/JDBC query regression; Oracle-only LOB rules are checked separately.

H2's LOB function adapters model full-text semantics, not Oracle acceptance.
Prerequisites are caller-selected public jars/compiled source-only framework.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
JAVA = r'''
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.sql.rowset.serial.SerialClob;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.SqlHelper;
import org.springframework.context.support.StaticApplicationContext;
import com.sunwayworld.framework.mybatis.mapper.*;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.data.page.*;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.emc.module.item.patrol.bean.EmcPatrolBean;
import com.sunwayworld.emc.module.item.patrol.service.impl.EmcPatrolServiceImpl;

public class ClobQueryOracleCheck {
  static Configuration config = new Configuration();
  static Connection db;
  public static Clob toClob(String value) throws Exception { return value == null ? null : new SerialClob(value.toCharArray()); }
  public static Integer compare(Clob first, Clob second) throws Exception {
    if (first == null || second == null) return null;
    try (Reader a = first.getCharacterStream(); Reader b = second.getCharacterStream()) {
      for (;;) { int x = a.read(), y = b.read(); if (x != y) return x < y ? -1 : 1; if (x == -1) return 0; }
    }
  }
  static void execute(String sql) throws Exception { try (Statement s = db.createStatement()) { s.execute(sql); } }
  static void parse(String file) throws Exception {
    try (InputStream in = new FileInputStream(file)) { new XMLMapperBuilder(in, config, file, config.getSqlFragments()).parse(); }
  }
  static String text(Object value) throws Exception {
    if (value instanceof Clob) { Clob c = (Clob)value; return c.getSubString(1, Math.toIntExact(c.length())); }
    return value == null ? null : value.toString();
  }
  static void expect(Object wanted, Object actual) {
    if (!Objects.equals(wanted, actual)) throw new AssertionError("Expected " + wanted + ", got " + actual);
  }
  static List<Map<String,Object>> query(String statement, Map<String,Object> p, boolean count) throws Exception {
    BoundSql bound = config.getMappedStatement(statement).getBoundSql(p);
    String sql = bound.getSql();
    if (statement.contains("CrmOpportunityOrdtask")) {
      // Actual schema: TESTNO CLOB, SCHEMENAME VARCHAR2. H2 silently promotes this
      // invalid Oracle mixed CASE, so reject its unconverted scalar result arm.
      if (sql.matches("(?s).*ELSE\\s+T\\.SCHEMENAME\\s+END.*"))
        throw new AssertionError("Oracle CASE mixes CLOB TESTNO with VARCHAR2 SCHEMENAME");
    }
    if (p.get("patrolUserIdExact") != null && !p.get("patrolUserIdExact").toString().isEmpty()) {
      String compact = sql.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
      if (!compact.contains("DBMS_LOB.COMPARE(T.PATROLUSERID,TO_CLOB(?))=0"))
        throw new AssertionError("Patrol user query lacks complete CLOB equality");
    }
    if (count) sql = SqlHelper.getSelectTotalSql(sql, new String[0], new String[0]);
    else if(p.containsKey("fixtureOffset")) sql=new com.sunwayworld.framework.database.dialect.OracleDialect().getLimitSql(sql,((Number)p.get("fixtureOffset")).intValue(),1);
    List<Map<String,Object>> rows = new ArrayList<>();
    try (PreparedStatement s = db.prepareStatement(sql)) {
      new DefaultParameterHandler(config.getMappedStatement(statement), p, bound).setParameters(s);
      try (ResultSet r = s.executeQuery()) {
        while (r.next()) {
          Map<String,Object> row = new LinkedHashMap<>();
          for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) row.put(r.getMetaData().getColumnLabel(i), r.getObject(i));
          rows.add(row);
        }
      }
    }
    return rows;
  }
  static String longText() { StringBuilder b = new StringBuilder(); for (int i=0;i<5000;i++) b.append("巡检"); return b.toString(); }
  static void crm() throws Exception {
    String id = "com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityOrdtaskMapper.selectByCondition";
    execute("CREATE TABLE T_CRM_OPPORTUNITY_ORDTASK (ID VARCHAR(30), SCHEMENAME VARCHAR(40), TESTNO CLOB, TESTMETHODS CLOB, TESTCODE VARCHAR(30), ORDERSID VARCHAR(30))");
    try (PreparedStatement s = db.prepareStatement("INSERT INTO T_CRM_OPPORTUNITY_ORDTASK VALUES (?,?,?,?,?,?)")) {
      String[][] values = {{"default", "default", longText(), longText(), "test1", "o1"}, {"named", "方案", "ignored", "method", "test2", "o1"}, {"null-name", null, "ignored", null, "test3", "o1"}, {"null-test", "default", null, null, "test4", "o1"}, {"other", "other", "other", null, "test5", "o2"}};
      for (String[] row : values) { for (int i=0;i<row.length;i++) s.setString(i+1,row[i]); s.addBatch(); } s.executeBatch();
    }
    Map<String,Object> p = new HashMap<>(); p.put("ordersId", "o1");
    List<Map<String,Object>> rows = query(id,p,false); expect(4, rows.size());
    Map<String,Map<String,Object>> byId = new HashMap<>(); for (Map<String,Object> row:rows) byId.put(text(row.get("ID")),row);
    expect(longText(), text(byId.get("default").get("GROUPFIELDS")));
    expect(longText(), text(byId.get("default").get("TESTNAME")));
    expect(longText(), text(byId.get("default").get("METHODNAME")));
    expect("方案", text(byId.get("named").get("GROUPFIELDS")));
    expect(null, text(byId.get("null-name").get("GROUPFIELDS")));
    expect(null, text(byId.get("null-test").get("GROUPFIELDS")));
    expect(4L, ((Number)query(id,p,true).get(0).get("QTY_")).longValue());
  }

  static Map<String,Object> pattern(String match, String name, String value) {
    Map<String,Object> p = new HashMap<>(); p.put("matchPattern",match); p.put("map",Collections.singletonMap(name,value)); return p;
  }
  static final String PATROL = "com.sunwayworld.emc.module.item.patrol.mapper.EmcPatrolMapper.selectByCondition";
  static void patrol() throws Exception {
    execute("CREATE TABLE T_EMC_PATROL (ID BIGINT, PATROLDATE VARCHAR(30), PATROLUSERID CLOB, PATROLUSERNAME CLOB, CREATEDBYORGID VARCHAR(30))");
    execute("CREATE TABLE T_CORE_ORG (ID VARCHAR(30), ROOTORGID VARCHAR(30))");
    execute("INSERT INTO T_CORE_ORG VALUES ('lab1','root1'),('lab2','root2')");
    String[][] fixture = {{"1","2026-11-07","alice",longText(),"lab1"}, {"2","2026-11-08","alice","Alice 2","lab1"}, {"3","2026-11-07","bob","Bob","lab1"}, {"4","2026-10-07","alice","Old","lab1"}, {"5","2026-11-07",null,null,"lab1"}, {"6","2026-11-07",longText()+"A",longText(),"lab1"}, {"7","2026-11-07",longText()+"B",longText(),"lab1"}, {"8","2026-11-07","alice","Other org","lab2"}};
    try (PreparedStatement q=db.prepareStatement("INSERT INTO T_EMC_PATROL VALUES(?,?,?,?,?)")) {
      for(String[] row:fixture) { for(int i=0;i<row.length;i++) q.setString(i+1,row[i]); q.addBatch(); } q.executeBatch();
    }
    Map<String,Object> p=new HashMap<>();
    p.put("filterParamPatterns",new Object[]{pattern("SC","patrolDate","2026-11")});
    p.put("patrolUserIdExact","alice");
    List<Map<String,Object>> rows=query(PATROL,p,false); expect(3,rows.size()); expect(1L,((Number)rows.get(0).get("ID")).longValue());
    expect(longText(),text(rows.get(0).get("PATROLUSERNAME")));
    expect(3L,((Number)query(PATROL,p,true).get(0).get("QTY_")).longValue());
    // Preserve the original organization permission include and ordering.
    p.put("isolateAbandon","0"); p.put("loginUser",new Object()); p.put("loginOrgId","lab1");
    Map<String,Object> order=new HashMap<>(); order.put("first","ID"); order.put("second","desc"); p.put("orderParams",Arrays.asList(order));
    rows=query(PATROL,p,false); expect(2,rows.size()); expect(2L,((Number)rows.get(0).get("ID")).longValue());
    expect(2L,((Number)query(PATROL,p,true).get(0).get("QTY_")).longValue());
    p.put("fixtureOffset",1); rows=query(PATROL,p,false); expect(1,rows.size()); expect(1L,((Number)rows.get(0).get("ID")).longValue());
    expect(2L,((Number)query(PATROL,p,true).get(0).get("QTY_")).longValue()); p.remove("fixtureOffset");
    // Restrict to one original scalar filter without changing exact identity.
    p.put("filterParamPatterns",new Object[]{pattern("SC","patrolDate","2026-11"),pattern("EQ","ID","1")});
    expect(1,query(PATROL,p,false).size());
    p.put("filterParamPatterns",new Object[]{pattern("SC","patrolDate","2026-11")});
    p.put("patrolUserIdExact","absent"); expect(0,query(PATROL,p,false).size());
    p.put("patrolUserIdExact","alice' OR '1'='1"); expect(0,query(PATROL,p,false).size());
    p.put("patrolUserIdExact",longText()+"A"); rows=query(PATROL,p,false); expect(1,rows.size()); expect(6L,((Number)rows.get(0).get("ID")).longValue());
    expect(longText()+"A",text(rows.get(0).get("PATROLUSERID")));
    // No identity must not broaden to unassigned rows or all users.
    p.put("patrolUserIdExact",null); expect(0,query(PATROL,p,false).size());
    expect(0L,((Number)query(PATROL,p,true).get(0).get("QTY_")).longValue());
    p.put("patrolUserIdExact",""); expect(0,query(PATROL,p,false).size());
    p.remove("patrolUserIdExact"); expect(6,query(PATROL,p,false).size()); // ordinary month queries retain original scope
  }
  // DB adapter at the service's existing MapperParameter boundary, executing the
  // actual statement and framework total SQL. No business collaborator is mocked.
  public static class FixturePatrolService extends EmcPatrolServiceImpl {
    public Page<EmcPatrolBean> selectPagination(MapperParameter p, PageRowBounds bounds) {
      try {
        expect(true,p.containsKey("patrolUserIdExact")); expect(null,p.get("patrolUserIdExact"));
        BoundSql sql=config.getMappedStatement(PATROL).getBoundSql(p);
        if(sql.getSql().contains("\\%")) throw new AssertionError("Month selector searches for a literal percent");
        expect(0,query(PATROL,p,false).size());
        int total=((Number)query(PATROL,p,true).get(0).get("QTY_")).intValue();
        return new Pagination<EmcPatrolBean>(total,10,1,Collections.emptyList());
      } catch(Exception error) { throw new RuntimeException(error); }
    }
  }

  public static class MonthFixtureService extends EmcPatrolServiceImpl {
    boolean handle;
    public Page<EmcPatrolBean> selectPagination(MapperParameter p, PageRowBounds bounds) {
      boolean month=false, handleFilter=false;
      for(FilterParamPattern pattern:p.getFilterParamPatterns()) {
        for(Map.Entry<String,Object> e:pattern.getMap().entrySet()) {
          if(e.getKey().equalsIgnoreCase("patrolDate")) {
            expect("SC",pattern.getMatchPattern());
            expect(new java.text.SimpleDateFormat("yyyy-MM").format(new java.util.Date()),e.getValue()); month=true;
          }
          if(e.getKey().equalsIgnoreCase("handleUserIds")) handleFilter=true;
        }
      }
      expect(true,month); expect(handle,handleFilter);
      return new Pagination<EmcPatrolBean>(Collections.emptyList());
    }
  }
  static void serverFilter() throws Exception {
    // Caller-provided special value is overwritten by server null identity via
    // real GenericService.selectPaginationByFilter and MapperParameter.setFilter.
    RestJsonWrapperBean wrapper=new RestJsonWrapperBean();
    wrapper.setFilterValue("patrolUserIdExact","alice");
    expect(0,new FixturePatrolService().selectMonthRawPaginationByPatrolUserId(wrapper).getTotal());
    MonthFixtureService month=new MonthFixtureService(); month.selectMonthRawPagination(new RestJsonWrapperBean());
    month.handle=true; month.selectMonthRawPaginationByHandleUserId(new RestJsonWrapperBean());
    MapperParameter p=new MapperParameter(); p.put("patrolUserIdExact","bob");
    SearchFilter server=SearchFilter.instance().match("patrolUserIdExact","alice").special(); p.setFilter(server);
    expect("alice",p.get("patrolUserIdExact")); expect(4,query(PATROL,p,false).size());
  }

  static void sqlserver(String global, String patrol) throws Exception {
    config=new Configuration(); parse(global); parse(patrol);
    Map<String,Object> p=new HashMap<>(); p.put("patrolUserIdExact","alice");
    String sql=config.getMappedStatement(PATROL).getBoundSql(p).getSql().replaceAll("\\s+","").toUpperCase(Locale.ROOT);
    if(!sql.contains("CONVERT(NVARCHAR(MAX),T.PATROLUSERID)=?")) throw new AssertionError("SQL Server lost full identity equality");
    for(String identity:new String[]{null,""}) {
      p.put("patrolUserIdExact",identity);
      sql=config.getMappedStatement(PATROL).getBoundSql(p).getSql().replaceAll("\\s+","");
      if(!sql.contains("1=2")) throw new AssertionError("SQL Server null identity must fail closed");
    }
    p.remove("patrolUserIdExact");
    sql=config.getMappedStatement(PATROL).getBoundSql(p).getSql();
    if(sql.contains("PATROLUSERID")||sql.contains("1 = 2")) throw new AssertionError("Ordinary SQL Server query scope changed");
  }
  public static void main(String[] args) throws Exception {
    StaticApplicationContext app = new StaticApplicationContext();
    app.getEnvironment().getSystemProperties().put("spring.datasource.driver-class-name", "oracle.jdbc.OracleDriver");
    ApplicationContextHelper.setApplicationContext(app);
    com.sunwayworld.framework.database.dialect.DialectRepository.setDialect(new com.sunwayworld.framework.database.dialect.OracleDialect());
    parse(args[0]); parse(args[1]); parse(args[2]);
    try(Connection database = DriverManager.getConnection("jdbc:h2:mem:clob;MODE=Oracle")) {
      db = database; execute("CREATE ALIAS TO_CLOB FOR 'ClobQueryOracleCheck.toClob'");
      execute("CREATE SCHEMA DBMS_LOB"); execute("CREATE ALIAS DBMS_LOB.COMPARE FOR 'ClobQueryOracleCheck.compare'");
      if (args[3].equals("crm")) crm();
      else { patrol(); serverFilter(); sqlserver(args[4],args[5]); }
    }
    System.out.println(args[3] + ": PASS (real MyBatis BoundSql/JDBC; complete LOB values, scope, count, null and server parameter ownership)");
  }
}
'''

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, required=True)
    parser.add_argument('--public-deps', type=Path, required=True)
    parser.add_argument('--framework-classes', type=Path, required=True)
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--project-classes', type=Path, required=True)
    parser.add_argument('--case', choices=['crm','patrol','all'], default='all')
    args = parser.parse_args()
    for p in [args.jdk/'bin/javac', args.framework_classes, args.project_classes, args.public_deps, args.h2]:
        if not p.exists(): parser.error('missing explicit prerequisite: ' + str(p))
    common = ROOT/'reconstituted/framework/owned-core-common'
    with tempfile.TemporaryDirectory(prefix='clob-query-oracle-') as tmp:
        folder=Path(tmp)
        java=folder/'ClobQueryOracleCheck.java'; java.write_text(JAVA)
        cp=':'.join(map(str,[folder,args.framework_classes,args.project_classes,args.h2,args.public_deps/'*']))
        subprocess.run([str(args.jdk/'bin/javac'),'-encoding','UTF-8','-cp',cp,'-d',str(folder),str(java),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/item/patrol/service/impl/EmcPatrolServiceImpl.java')],check=True)
        for case in ['crm','patrol'] if args.case == 'all' else [args.case]:
            subprocess.run([str(args.jdk/'bin/java'),'-cp',cp,'ClobQueryOracleCheck',str(common/'src/main/resources/com/sunwayworld/framework/mybatis/mapper/conf/global-oracle-mapper.xml'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/crm/mapper/conf/crm-opportunity-ordtask-oracle-mapper.xml'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/item/patrol/mapper/conf/emc-patrol-oracle-mapper.xml'),case,str(common/'src/main/resources/com/sunwayworld/framework/mybatis/mapper/conf/global-sqlserver-mapper.xml'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/item/patrol/mapper/conf/emc-patrol-sqlserver-mapper.xml')],check=True)

if __name__=='__main__': main()
