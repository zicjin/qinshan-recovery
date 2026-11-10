package com.sunwayworld.framework.mybatis.mapper;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.*;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.context.support.StaticApplicationContext;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.*;
import com.sunwayworld.framework.database.context.instance.*;
import com.sunwayworld.framework.database.dialect.*;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.support.domain.Persistable;

/** Exercises the existing Mapper interfaces; H2 is only a local JDBC semantic fixture. */
public class OracleDaoClobCheck {
  static final String URL="jdbc:h2:mem:dao;MODE=Oracle;DB_CLOSE_DELAY=-1";
  static final String LONG=new String(new char[10050]).replace('\0','x');
  static final OracleDialect DIALECT=new OracleDialect();
  static TableContext table;
  static EntityContext entity;
  static String lastSql;
  static int comparisons;
  static int assertions;
  static String expected;
  static String label;
  static PrintWriter oracle;
  public static Integer compare(Clob a, Clob b) throws Exception {
    if(a==null||b==null) return null;
    try(Reader x=a.getCharacterStream(); Reader y=b.getCharacterStream()) {
      for(;;) { int i=x.read(),j=y.read(); if(i!=j) return i<j?-1:1; if(i==-1) return 0; }
    }
  }
  @Intercepts(@Signature(type=StatementHandler.class,method="prepare",args={Connection.class,Integer.class}))
  public static class Capture implements Interceptor {
    public Object intercept(Invocation call) throws Throwable {
      BoundSql bound=((StatementHandler)call.getTarget()).getBoundSql();
      lastSql=bound.getSql().replaceAll("\\s+"," ").trim();
      // H2 accepts CLOB IN by implicit conversion; Oracle rejects it with
      // ORA-22848. Enforce that dialect restriction at the JDBC boundary.
      if(lastSql.matches("(?is).*\"PROJNAME\"\\s+IN\\s*\\(.*"))
        throw new AssertionError("Oracle ORA-22848: CLOB membership cannot use IN: "+lastSql);
      if(lastSql.contains("DBMS_LOB.COMPARE")) {
        comparisons++;
        int clobBindings=0;
        for(ParameterMapping p:bound.getParameterMappings()) if(p.getJdbcType()==JdbcType.CLOB) clobBindings++;
        expect(lastSql.split("DBMS_LOB\\.COMPARE",-1).length-1,clobBindings);
      }
      if(oracle!=null) emit(bound);
      return call.proceed();
    }
    public Object plugin(Object target) { return Plugin.wrap(target,this); }
    public void setProperties(Properties p) {}
  }
  public static class Project implements Persistable<Long> {
    Long id; String projName,projNo; Map<String,String> ext$=new HashMap<>();
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getProjName(){return projName;} public void setProjName(String v){projName=v;}
    public String getProjNo(){return projNo;} public void setProjNo(String v){projNo=v;}
    public Map<String,String> getExt$(){return ext$;} public void setExt$(Map<String,String> v){ext$=v;}
    public void setExt$Item(String k,String v){ext$.put(k,v);}
  }
  static void expect(Object expected,Object actual) {
    assertions++;
    if(!Objects.equals(expected,actual)) throw new AssertionError("expected "+expected+", got "+actual);
  }
  static Map<String,Object> values(String name,String no) {
    Map<String,Object> m=new LinkedHashMap<>(); m.put("PROJNAME",name); if(no!=null)m.put("PROJNO",no); return m;
  }
  static EntityContextInstance item(Map<String,Object> v) {
    Project p=new Project(); p.setProjName((String)v.get("PROJNAME")); p.setProjNo((String)v.get("PROJNO"));
    return EntityContextInstance.instance(entity,p,v.keySet().toArray(new String[0]));
  }
  static TableContextInstance map(Map<String,Object> v) { return TableContextInstance.of(table,v); }
  static List<EntityContextInstance> members(String column,String... candidates) {
    List<EntityContextInstance> items=new ArrayList<>();
    for(String candidate:candidates) {
      Map<String,Object> v=new LinkedHashMap<>();v.put(column,candidate);items.add(item(v));
    }
    return items;
  }
  static void fixture() throws Exception {
    try(Connection c=DriverManager.getConnection(URL); Statement s=c.createStatement()) {
      s.execute("CREATE SCHEMA DBMS_LOB");
      s.execute("CREATE ALIAS DBMS_LOB.COMPARE FOR 'com.sunwayworld.framework.mybatis.mapper.OracleDaoClobCheck.compare'");
      s.execute("CREATE TABLE T_EMC_PROJECT_CHANGE(ID NUMBER(12), PROJNAME CLOB, PROJNO VARCHAR2(100))");
      String[][] rows={{LONG+"A","n1"},{LONG+"A","n2"},{LONG+"B","n1"},{null,"n1"},{"short","n1"},{"项目' OR '1'='1","n1"}};
      try(PreparedStatement q=c.prepareStatement("INSERT INTO T_EMC_PROJECT_CHANGE VALUES(?,?,?)")) {
        for(int i=0;i<rows.length;i++) {q.setLong(1,i+1);q.setClob(2,rows[i][0]==null?null:new StringReader(rows[i][0]));q.setString(3,rows[i][1]);q.addBatch();} q.executeBatch();
      }
      // Derive the contexts from real JDBC column metadata, retaining the datatype
      // and dialect's CLOB flag rather than treating every String as a CLOB.
      table=new TableContext(); table.setTableName("T_EMC_PROJECT_CHANGE");
      table.setColumnContextList(new ArrayList<ColumnContext>());
      entity=new EntityContext(); entity.setTableName(table.getTableName());
      entity.setColumnContextList(new ArrayList<EntityColumnContext>());
      try(ResultSet r=s.executeQuery("SELECT * FROM T_EMC_PROJECT_CHANGE WHERE 1=0")) {
        ResultSetMetaData md=r.getMetaData();
        for(int i=1;i<=md.getColumnCount();i++) {
          ColumnContext column=new ColumnContext(); column.setColumnName(md.getColumnName(i)); column.setDataType(md.getColumnType(i)==Types.CLOB?"CLOB":md.getColumnTypeName(i));
          column.setType(DIALECT.getJavaType(column.getColumnName(),column.getDataType(),md.getScale(i)));column.setClob(DIALECT.isClob(column));
          table.getColumnContextList().add(column);
          EntityColumnContext ec=new EntityColumnContext();ec.setColumnName(column.getColumnName());ec.setEntityClass(Project.class);ec.setType(column.getType());ec.setClob(column.isClob());
          entity.getColumnContextList().add(ec);
          if(column.getColumnName().equals("ID")){table.setIdColumnContext(column);entity.setIdContext(ec);}
        }
      }
      expect(true,table.getColumnContextList().get(1).isClob());expect(false,table.getColumnContextList().get(2).isClob());
    }
  }
  static void ids(String wanted,List<Map<String,Object>> rows) {
    List<String> ids=new ArrayList<>();for(Map<String,Object> r:rows) ids.add(Long.toString(((Number)r.get("ID")).longValue()));
    expect(wanted,String.join(",",ids));
  }
  static void lobSql() {
    if(!lastSql.contains("DBMS_LOB.COMPARE")) throw new AssertionError("Oracle CLOB read equality must compare full bound CLOB, actual: "+lastSql);
    if(lastSql.toUpperCase(Locale.ROOT).contains("SUBSTR")||lastSql.contains("TO_CHAR")) throw new AssertionError("Truncated LOB: "+lastSql);
  }
  static void scalarMembershipSql() {
    if(!lastSql.contains(" IN ")||lastSql.contains("DBMS_LOB")) throw new AssertionError("Scalar membership must retain IN: "+lastSql);
  }
  static String literal(Object v,boolean clob) {
    if(v==null)return "NULL";
    if(v instanceof Number)return v.toString();
    String text=v.toString();StringBuilder out=new StringBuilder();
    for(int i=0;i<text.length();i+=1000) {
      if(i>0)out.append(" || ");String part="'"+text.substring(i,Math.min(text.length(),i+1000)).replace("'","''")+"'";
      out.append(clob?"TO_CLOB("+part+")":part);
    }
    return out.length()==0?"NULL":out.toString();
  }
  static void emit(BoundSql bound) {
    String sql=lastSql;int start=0;StringBuilder rendered=new StringBuilder();
    org.apache.ibatis.reflection.MetaObject params=org.apache.ibatis.reflection.SystemMetaObject.forObject(bound.getParameterObject());
    for(ParameterMapping p:bound.getParameterMappings()) {
      int q=sql.indexOf('?',start);rendered.append(sql.substring(start,q));
      Object value=bound.hasAdditionalParameter(p.getProperty())?bound.getAdditionalParameter(p.getProperty()):params.getValue(p.getProperty());
      rendered.append(literal(value,p.getJdbcType()==JdbcType.CLOB));start=q+1;
    }
    rendered.append(sql.substring(start));
    oracle.println("PROMPT "+label+"; expected "+expected);
    // UNION only scalar seed data; the CLOB is constructed in the outer CTE.
    oracle.println("WITH seed AS (SELECT 1 ID, 'A' TAIL, 'n1' PROJNO FROM DUAL UNION ALL SELECT 2,'A','n2' FROM DUAL UNION ALL SELECT 3,'B','n1' FROM DUAL UNION ALL SELECT 4,NULL,'n1' FROM DUAL UNION ALL SELECT 5,'short','n1' FROM DUAL UNION ALL SELECT 6,'quoted','n1' FROM DUAL), T_EMC_PROJECT_CHANGE AS (SELECT ID, PROJNO, CASE WHEN ID<=3 THEN TO_CLOB(RPAD('x',4000,'x')) || TO_CLOB(RPAD('x',4000,'x')) || TO_CLOB(RPAD('x',2050,'x')) || TAIL WHEN ID=4 THEN TO_CLOB(NULL) WHEN ID=5 THEN TO_CLOB('short') ELSE TO_CLOB('项目'' OR ''1''=''1') END PROJNAME FROM seed)");
    // Reduce complete projections for SQLPlus readability only. The JDBC test
    // already verifies the complete returned LOB independently.
    String query=rendered.toString();
    if(query.startsWith("SELECT \"ID\" , \"PROJNAME\"")) query=query.replaceFirst("SELECT .*? FROM ","SELECT ID FROM ");
    if(query.startsWith("SELECT COUNT(1)"))query=query.replaceFirst("SELECT COUNT\\(1\\)","SELECT COUNT(1) ID");
    String assertion="SELECT '"+label+"' CASE_ID, 1 / CASE WHEN NVL(LISTAGG(TO_CHAR(ID), ',') WITHIN GROUP (ORDER BY ID), '~') = '"+(expected.isEmpty()?"~":expected)+"' THEN 1 ELSE 0 END ASSERTION FROM ("+query+")";
    oracle.println(assertion.replace(" || "," ||\n")+";");
  }
  public static void main(String[] args) throws Exception {
    StaticApplicationContext app=new StaticApplicationContext();
    app.getEnvironment().getSystemProperties().put("spring.datasource.driver-class-name","oracle.jdbc.OracleDriver");
    ApplicationContextHelper.setApplicationContext(app);DialectRepository.setDialect(DIALECT);
    fixture();
    Configuration config=new Configuration(new Environment("fixture",new JdbcTransactionFactory(),new UnpooledDataSource("org.h2.Driver",URL,"", "")));
    config.setLocalCacheScope(LocalCacheScope.STATEMENT);
    config.addInterceptor(new Capture());
    if(args.length>2) {
      oracle=new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[2]),"UTF-8"));
      oracle.println("WHENEVER OSERROR EXIT FAILURE\nWHENEVER SQLERROR EXIT SQL.SQLCODE\nSET SQLBLANKLINES ON\nSET DEFINE OFF\nSET LINESIZE 200\nSET PAGESIZE 500");
    }
    for(String file:Arrays.asList(args[0],args[1])) try(InputStream in=new FileInputStream(file)){new XMLMapperBuilder(in,config,file,config.getSqlFragments()).parse();}
    try(SqlSession session=new SqlSessionFactoryBuilder().build(config).openSession()) {
      DaoMapper dao=session.getMapper(DaoMapper.class); MapDaoMapper maps=session.getMapper(MapDaoMapper.class);
      List<String> projection=Arrays.asList("ID"); List<Order> asc=Arrays.asList(Order.asc("ID")),desc=Arrays.asList(Order.desc("ID"));
      label="Dao CLOB membership full text";expected="1,2";
      ids("1,2",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME",LONG+"A"),projection,asc));lobSql();
      label="Dao CLOB membership distinct tail";expected="3";
      ids("3",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME",LONG+"B"),projection,asc));lobSql();
      label="Dao CLOB membership absent tail";expected="";
      ids("",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME",LONG+"C"),projection,asc));lobSql();
      label="Dao CLOB membership multiple values";expected="1,2,5";
      ids("1,2,5",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME",LONG+"A","short"),projection,asc));lobSql();
      label="Dao CLOB membership quotes exact";expected="6";
      ids("6",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME","项目' OR '1'='1"),projection,asc));lobSql();
      label="Dao CLOB membership quotes absent";expected="";
      ids("",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME","project' OR '1'='1"),projection,asc));lobSql();
      label="Dao CLOB membership null empty and actual";expected="1,2,6";
      ids("1,2,6",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME",null,"",LONG+"A","项目' OR '1'='1"),projection,asc));lobSql();
      label="Dao CLOB membership case insensitive column and sort";expected="1,2";
      ids("2,1",dao.selectByOneColumnValues(entity,"projname",members("PROJNAME",LONG+"A"),projection,desc));lobSql();
      for(String candidate:new String[]{null,""}) {
        label="Dao CLOB membership "+(candidate==null?"null":"empty")+" does not match null row";expected="";
        ids("",dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME",candidate),projection,asc));
      }
      label="Dao CLOB membership complete projection";expected="1,2";
      List<Map<String,Object>> complete=dao.selectByOneColumnValues(entity,"PROJNAME",members("PROJNAME",LONG+"A"),null,asc);
      ids("1,2",complete);lobSql();
      Clob membershipValue=(Clob)complete.get(0).get("PROJNAME");
      expect(LONG+"A",membershipValue.getSubString(1,(int)membershipValue.length()));
      expect(new HashSet<>(Arrays.asList("ID","PROJNAME","PROJNO")),complete.get(0).keySet());
      label="Dao scalar membership in table with CLOB";expected="2";
      ids("2",dao.selectByOneColumnValues(entity,"PROJNO",members("PROJNO","n2"),projection,asc));scalarMembershipSql();
      label="Dao scalar membership multiple values";expected="1,2,3,4,5,6";
      ids("1,2,3,4,5,6",dao.selectByOneColumnValues(entity,"projno",members("PROJNO","n1","n2"),projection,asc));scalarMembershipSql();
      label="Dao scalar membership null empty and actual";expected="2";
      ids("2",dao.selectByOneColumnValues(entity,"PROJNO",members("PROJNO",null,"","n2"),projection,asc));scalarMembershipSql();
      label="Dao scalar membership null and empty only";expected="";
      ids("",dao.selectByOneColumnValues(entity,"PROJNO",members("PROJNO",null,""),projection,asc));scalarMembershipSql();
      label="Dao number membership in table with CLOB";expected="2";
      Project numbered=new Project();numbered.setId(2L);
      ids("2",dao.selectByOneColumnValues(entity,"ID",Arrays.asList(EntityContextInstance.instance(entity,numbered,"ID")),projection,asc));scalarMembershipSql();
      for(String column:Arrays.asList("PROJNAME","PROJNO")) {
        label="Dao membership empty list fails closed "+column;expected="";
        ids("",dao.selectByOneColumnValues(entity,column,Collections.<EntityContextInstance>emptyList(),projection,asc));
        label="Dao membership null list fails closed "+column;expected="";
        ids("",dao.selectByOneColumnValues(entity,column,null,projection,asc));
      }
      // Exercise both existing Mapper contracts, using real framework context factories.
      for(boolean isMap:new boolean[]{false,true}) {
        String who=isMap?"MapDao":"Dao";
        Map<String,Object> v=values(LONG+"A",null);
        List<Map<String,Object>> rows;
        label=who+" full text same prefix different tail";expected="1,2";
        rows=isMap?maps.selectByInstance(map(v),projection,asc):dao.select(item(v),projection,asc);
        ids("1,2",rows); lobSql();
        label=who+" AND scalar";expected="1";v=values(LONG+"A","n1");
        rows=isMap?maps.selectByInstance(map(v),projection,asc):dao.select(item(v),projection,asc);
        ids("1",rows);lobSql();
        if(!lastSql.contains("AND")||!lastSql.contains("\"PROJNO\" = ?")) throw new AssertionError(lastSql);
        label=who+" union OR scalar";expected="1,2,3,4,5,6";
        rows=isMap?maps.selectUnionByInstance(map(v),projection,asc):dao.selectUnion(item(v),projection,asc);
        ids("1,2,3,4,5,6",rows);lobSql();
        label=who+" count";expected="1";
        expect(1,isMap?maps.countByInstance(map(v)):dao.count(item(v)));lobSql();
        List<Map<String,Object>> values=Arrays.asList(values(LONG+"A","n2"),values(LONG+"B","n1"));
        List<EntityContextInstance> items=Arrays.asList(item(values.get(0)),item(values.get(1)));
        List<TableContextInstance> instances=Arrays.asList(map(values.get(0)),map(values.get(1)));
        label=who+" batch OR of AND";expected="2,3";
        rows=isMap?maps.selectByInstanceList(table,instances,projection,asc):dao.batchSelect(entity,items,projection,asc);
        ids("2,3",rows);lobSql();
        label=who+" batch count";expected="2";
        expect(2,isMap?maps.countUnionByInstanceList(table,instances):dao.batchCount(entity,items));lobSql();
        label=who+" first after descending sort";expected="2";v=values(LONG+"A",null);
        Map<String,Object> first=isMap?maps.selectFirstByInstance(map(v),projection,desc):dao.selectFirst(item(v),projection,desc);
        expect(2L,((Number)first.get("ID")).longValue());expect(Collections.singleton("ID"),first.keySet());lobSql();
        for(String name:new String[]{"absent","project' OR '1'='1",LONG+"C"}) {
          label=who+" no match or quoted input";expected="";v=values(name,null);
          rows=isMap?maps.selectByInstance(map(v),projection,asc):dao.select(item(v),projection,asc);
          ids("",rows);lobSql();
        }
        label=who+" quoted exact text";expected="6";v=values("项目' OR '1'='1",null);
        rows=isMap?maps.selectByInstance(map(v),projection,asc):dao.select(item(v),projection,asc);
        ids("6",rows);lobSql();
        for(String name:new String[]{null,""}) {
          label=who+" null or empty remains IS NULL";expected="4";v=values(name,null);
          rows=isMap?maps.selectByInstance(map(v),projection,asc):dao.select(item(v),projection,asc);
          ids("4",rows);
          if(!lastSql.contains("\"PROJNAME\" IS NULL")||lastSql.contains("DBMS_LOB")) throw new AssertionError(lastSql);
        }
        label=who+" scalar only";expected="2";v=new HashMap<>();v.put("PROJNO","n2");
        rows=isMap?maps.selectByInstance(map(v),projection,asc):dao.select(item(v),projection,asc);
        ids("2",rows);
        if(!lastSql.contains("\"PROJNO\" = ?")||lastSql.contains("DBMS_LOB")) throw new AssertionError(lastSql);
        label=who+" complete projection";expected="1,2";v=values(LONG+"A",null);
        rows=isMap?maps.selectByInstance(map(v),null,asc):dao.select(item(v),null,asc);
        ids("1,2",rows);lobSql();
        Clob projected=(Clob)rows.get(0).get("PROJNAME");
        expect(LONG+"A",projected.getSubString(1,(int)projected.length()));
        expect(new HashSet<>(Arrays.asList("ID","PROJNAME","PROJNO")),rows.get(0).keySet());
      }
    }
    if(oracle!=null){oracle.println("EXIT SUCCESS");oracle.close();}
    System.out.println("Oracle DAO full CLOB Mapper/JDBC regression PASS; assertions="+assertions+", LOB queries="+comparisons);
  }
}
