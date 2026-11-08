#!/usr/bin/env python3
"""Exercise actual ledger MyBatis SQL/DTO mapping and pagination against an in-memory fixture.

No network/Oracle/runtime is accessed. SQL writes only create this ephemeral unit
fixture. Existing public/source-only build artifacts are read, compiled output is
isolated. Test-only SQLite pagination keeps the real count/interceptor path; the
Oracle dialect pagination wrapper is separately asserted.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile
ROOT = Path(__file__).resolve().parents[2]
JAVA = r'''
import java.io.*;import java.sql.*;import java.util.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;import org.apache.ibatis.session.*;import org.apache.ibatis.mapping.*;import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.springframework.context.support.GenericApplicationContext;import org.springframework.core.env.MapPropertySource;
import com.sunwayworld.framework.context.ApplicationContextHelper;import com.sunwayworld.framework.database.dialect.*;
import com.sunwayworld.framework.database.sql.SqlHelper;import com.sunwayworld.framework.mybatis.interceptor.MybatisInterceptor;import com.sunwayworld.framework.mybatis.page.*;import com.sunwayworld.framework.mybatis.mapper.*;import com.sunwayworld.framework.data.page.*;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.*;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.*;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.*;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl.*;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.alibaba.fastjson.JSON;
public class EquipmentLedgerProjectionCheck {
 static final String NS="com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptMapper.";
 static void need(boolean b,String m){if(!b)throw new AssertionError(m);}
 static void load(Configuration c,String p)throws Exception{try(InputStream in=new FileInputStream(p)){new XMLMapperBuilder(in,c,p,c.getSqlFragments()).parse();}}
 static class UnitDateHandler extends org.apache.ibatis.type.BaseTypeHandler<java.time.LocalDateTime> {public void setNonNullParameter(PreparedStatement p,int i,java.time.LocalDateTime v,org.apache.ibatis.type.JdbcType j)throws SQLException{p.setTimestamp(i,Timestamp.valueOf(v));}public java.time.LocalDateTime getNullableResult(ResultSet r,String c)throws SQLException{Timestamp t=r.getTimestamp(c);return t==null?null:t.toLocalDateTime();}public java.time.LocalDateTime getNullableResult(ResultSet r,int c)throws SQLException{Timestamp t=r.getTimestamp(c);return t==null?null:t.toLocalDateTime();}public java.time.LocalDateTime getNullableResult(CallableStatement r,int c)throws SQLException{Timestamp t=r.getTimestamp(c);return t==null?null:t.toLocalDateTime();}}
 static class UnitDialect extends OracleDialect {public String getLimitSql(String s,int o,int n){return s+" LIMIT "+n+" OFFSET "+o;}}
 static MapperParameter params(String org,String filter,String dir){MapperParameter p=new MapperParameter();p.put("isolateAbandon","1");p.put("ledgerParentOrgId",org);if(filter!=null)p.addFilterParamPatterns(new FilterParamPattern("CISC","equiptNo",filter));if(dir!=null)p.setOrderParam("equiptNo",dir);return p;}
 static PageRowBounds bounds(int page){Map<String,String> p=new HashMap<>();p.put("n",String.valueOf(page));p.put("s","1");return new com.sunwayworld.framework.restful.data.RestJsonWrapperBean(p,null,null).extractPageRowBounds();}
 static Page<?> page(SqlSession s,String stmt,MapperParameter p,int number){return MybatisPageHelper.get(bounds(number),()->{PfEquiptMapper m=s.getMapper(PfEquiptMapper.class);if(stmt.equals("selectLedgerPlan"))return (List)m.selectLedgerPlan(p);if(stmt.equals("selectLedgerPerCheck"))return (List)m.selectLedgerPerCheck(p);return (List)m.selectLedgerUser(p);});}
 static String number(Object v){if(v instanceof PfEquiptPlanDTO)return ((PfEquiptPlanDTO)v).getEquiptNo();if(v instanceof PfEquiptPerCheckDTO)return ((PfEquiptPerCheckDTO)v).getEquiptNo();return ((PfEquiptUserDTO)v).getEquiptNo();}
 public static void main(String[] a)throws Exception{
  GenericApplicationContext ctx=new GenericApplicationContext();ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture",Collections.singletonMap("spring.datasource.driver-class-name","org.sqlite.JDBC")));ctx.refresh();ApplicationContextHelper.setApplicationContext(ctx);DialectRepository.setDialect(new UnitDialect());
  String url="jdbc:sqlite:file:ledgerfixture?mode=memory&cache=shared";Class.forName("org.sqlite.JDBC");try(Connection fixture=DriverManager.getConnection(url)){
   Statement x=fixture.createStatement();x.execute("CREATE TABLE T_EMC_EQUIPT(ID INTEGER PRIMARY KEY,EQUIPTID TEXT,EQUIPTNAME TEXT,SPECIFICATION TEXT,ORGID TEXT,KEEPUSERID TEXT,USEDUSERNAME TEXT,CREATEDBYORGID TEXT)");
   x.execute("CREATE TABLE T_CORE_USER(ID TEXT PRIMARY KEY,USERNAME TEXT)");x.execute("INSERT INTO T_CORE_USER VALUES('keeper','Original Keeper')");
   x.execute("INSERT INTO T_EMC_EQUIPT VALUES(1,'EQ-Z','Z instrument','model-z','org-a','keeper','User Z','org-a'),(2,'EQ-A','A instrument','model-a','org-a','keeper','User A','org-a'),(3,'EQ-X','Foreign instrument','model-x','org-b','keeper','Other User','org-b')");
   x.execute("CREATE TABLE T_EMC_EQUIPT_PLAN(ID INTEGER PRIMARY KEY,EQUIPTID INTEGER,VERIFYORG TEXT,CYCLE TEXT,REMARK TEXT,CREATEDBYORGID TEXT)");x.execute("INSERT INTO T_EMC_EQUIPT_PLAN VALUES(11,1,'VZ','12','Z remark','org-a'),(12,2,'VA','6','A remark','org-a'),(13,3,'VX','1','foreign','org-b'),(14,999,'orphan','1','orphan','org-a')");
   x.execute("CREATE TABLE T_EMC_EQUIPT_PER_CHECK_PLAN(ID INTEGER PRIMARY KEY,EQUIPTID INTEGER,MAINTDATE TIMESTAMP,CHECKWAY TEXT,CHECKREASON TEXT,CHECKUSERID TEXT,CHECKUSERNAME TEXT,REMARK TEXT,CREATEDBYORGID TEXT)");x.execute("INSERT INTO T_EMC_EQUIPT_PER_CHECK_PLAN VALUES(21,1,NULL,'Z-way','Z-reason','z','Z checker','Z remark','org-a'),(22,2,NULL,'A-way','A-reason','a','A checker','A remark','org-a'),(23,3,NULL,'X-way','X-reason','x','X checker','foreign','org-b')");
   Configuration c=new Configuration(new Environment("unit",new JdbcTransactionFactory(),new UnpooledDataSource("org.sqlite.JDBC",url,null)));c.setLocalCacheScope(LocalCacheScope.STATEMENT);c.getTypeHandlerRegistry().register(java.time.LocalDateTime.class,new UnitDateHandler());c.addInterceptor(new MybatisInterceptor());load(c,a[0]);load(c,a[1]);load(c,a[2]);SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(c);
   for(String stmt:new String[]{"selectLedgerPlan","selectLedgerPerCheck","selectLedgerUser"}){
    need(c.hasStatement(NS+stmt),"actual ledger statement missing: "+stmt);
    try(SqlSession s=factory.openSession()){
     Page<?> p1=page(s,stmt,params("org-a",null,"asc"),1);need(p1.getTotal()==2&&p1.getRows().size()==1&&"EQ-A".equals(number(p1.getRows().get(0))),stmt+" ascending/count/projection");
     Page<?> p2=page(s,stmt,params("org-a",null,"asc"),2);need(p2.getTotal()==2&&p2.getPageNumber()==2&&"EQ-Z".equals(number(p2.getRows().get(0))),stmt+" second page order before limit");
     Page<?> desc=page(s,stmt,params("org-a",null,"desc"),1);need("EQ-Z".equals(number(desc.getRows().get(0))),stmt+" descending");
     Page<?> exact=page(s,stmt,params("org-a","eq-a","asc"),1);need(exact.getTotal()==1&&"EQ-A".equals(number(exact.getRows().get(0))),stmt+" case-insensitive display-code filter");
     Page<?> none=page(s,stmt,params("org-a","QA261108A-NOMATCH","asc"),1);need(none.getTotal()==0&&none.getRows().isEmpty(),stmt+" original no-match stimulus");
     Page<?> foreign=page(s,stmt,params("org-a","EQ-X","asc"),1);need(foreign.getTotal()==0,stmt+" foreign organization leaked");
     Page<?> missing=page(s,stmt,params(null,null,"asc"),1);need(missing.getTotal()==0,stmt+" null server org must fail closed");
     Page<?> empty=page(s,stmt,params("",null,"asc"),1);need(empty.getTotal()==0,stmt+" empty server org must fail closed");
     Object row=p1.getRows().get(0);need(!JSON.toJSONString(row).contains("orgId")&&!JSON.toJSONString(row).contains("createdByOrgId")&&!JSON.toJSONString(row).contains("\"id\""),"typed DTO must not expose SQL scope/tie-breaker columns");if(row instanceof PfEquiptPlanDTO){PfEquiptPlanDTO v=(PfEquiptPlanDTO)row;need("VA".equals(v.getVerifyOrg())&&"Original Keeper".equals(v.getKeepUserName()),"plan DTO source fields");need(v.getMaintLastDate()==null&&v.getMaintPlanDate()==null,"unproven date fields must not be invented");}else if(row instanceof PfEquiptPerCheckDTO){PfEquiptPerCheckDTO v=(PfEquiptPerCheckDTO)row;need("A-way".equals(v.getCheckWay())&&"A checker".equals(v.getCheckUserName()),"per-check DTO fields");need(v.getCheckIndex()==null&&v.getMaintLastDate()==null,"unproven check/date fields must not be invented");}else need("User A".equals(((PfEquiptUserDTO)row).getUsers()),"users retain USEDUSERNAME source");
     MapperParameter q=params("org-a","EQ-A","asc");BoundSql bs=c.getMappedStatement(NS+stmt).getBoundSql(q);String sql=SqlHelper.cleanExtPrefix(bs.getSql());need(sql.contains("EQUIPTNO")&&sql.contains("ORGID = ?"),"actual SQL projected alias/server org");String count=SqlHelper.getSelectTotalSql(sql,new String[0],new String[0]);need(count.contains("EQUIPTNO")&&!count.toUpperCase().contains("ORDER BY"),"count must use same filtered projection");String oracle=new OracleDialect().getLimitSql(sql,1,1);need(oracle.contains("ROWNUM <= 2")&&oracle.contains("RN_ > 1")&&oracle.indexOf("ORDER BY")<oracle.indexOf("WHERE ROWNUM"),"Oracle order before page wrapper");
    }
    System.out.println("PASS actual Mapper + typed DTO + count/filter/org/order/page: "+stmt);
   }

   // Equal display codes retain deterministic ID-desc tie order before page
   // boundaries. Data belongs only to this unit's in-memory database.
   x.execute("INSERT INTO T_EMC_EQUIPT VALUES(4,'EQ-DUP','older duplicate','model','org-a','keeper','Older','org-a'),(5,'EQ-DUP','newer duplicate','model','org-a','keeper','Newer','org-a')");
   try(SqlSession s=factory.openSession()){
    Page<?> tie1=page(s,"selectLedgerUser",params("org-a","EQ-DUP","asc"),1);
    Page<?> tie2=page(s,"selectLedgerUser",params("org-a","EQ-DUP","asc"),2);
    need(tie1.getTotal()==2&&"Newer".equals(((PfEquiptUserDTO)tie1.getRows().get(0)).getUsers())&&"Older".equals(((PfEquiptUserDTO)tie2.getRows().get(0)).getUsers()),"duplicate sort-key page stability");
   }
   for(java.lang.reflect.Method method:EmcEquiptDao.class.getMethods())need(!method.getName().startsWith("selectLedger"),"generic equipment DAO contract expanded");
   need(PfEquiptLedgerDao.class.getDeclaredMethods().length==3,"dedicated ledger interface must contain exactly three DTO queries");
   need(PfEquiptLedgerDao.class.isAssignableFrom(com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl.EmcEquiptDaoImpl.class),"real DAO must expose dedicated ledger seam");
   // Execute the actual service with a DAO boundary proxy; client cannot choose
   // the trusted organization or force GenericService map->equipment conversion.
   CoreUserBean user=new CoreUserBean();user.setId("fixture-user");user.setUserType("ordinary");user.setExt$Item("parentOrgId","org-a");
   new LocalContextHelper().setCoreUserService((CoreUserService)java.lang.reflect.Proxy.newProxyInstance(EquipmentLedgerProjectionCheck.class.getClassLoader(),new Class[]{CoreUserService.class},(o,m,v)->{if(m.getName().equals("selectById"))return user;throw new AssertionError("unexpected user call "+m.getName());}));
   LoginUser login=new LoginUser("fixture-user","");login.setOrgId("org-a");LocalContextHelper.setUserLogin(login);
   try(SqlSession ss=factory.openSession()){
    PfEquiptServiceImpl service=new PfEquiptServiceImpl();java.lang.reflect.Field field=PfEquiptServiceImpl.class.getDeclaredField("equiptLedgerDao");field.setAccessible(true);
    field.set(service,java.lang.reflect.Proxy.newProxyInstance(EquipmentLedgerProjectionCheck.class.getClassLoader(),new Class[]{PfEquiptLedgerDao.class},(o,m,v)->{if(m.getName().startsWith("selectLedger")){MapperParameter z=(MapperParameter)v[0];need("org-a".equals(z.get("ledgerParentOrgId")),"client scope override escaped");PfEquiptMapper mapper=ss.getMapper(PfEquiptMapper.class);return EmcEquiptMapper.class.getMethod(m.getName(),MapperParameter.class).invoke(mapper,z);}throw new AssertionError("Unexpected generic DAO path "+m.getName());}));
    Map<String,String> wrapper=new HashMap<>();wrapper.put("f","{\"ledgerParentOrgId\":\"org-b\"}");wrapper.put("qf","{\"equiptNo_CISC\":\"EQ-A\"}");wrapper.put("o","[{\"equiptNo\":\"asc\"}]");wrapper.put("n","1");wrapper.put("s","1");
    com.sunwayworld.framework.restful.data.RestJsonWrapperBean w=new com.sunwayworld.framework.restful.data.RestJsonWrapperBean(wrapper,null,null);
    for(Page<?> result:Arrays.asList(service.selectEquiptPlan(w),service.selectEquiptPerCheck(w),service.selectEquiptUser(w))){need(result.getTotal()==1&&result.getRows().size()==1&&"EQ-A".equals(number(result.getRows().get(0))),"actual service typed/page/filter preservation");}
   }finally{LocalContextHelper.removeUserLogin();}
   System.out.println("PASS actual service -> dedicated ledger DAO -> inherited Pf mapper, client scope override rejected");
  }ApplicationContextHelper.setApplicationContext(null);ctx.close();
 }
}
'''
def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--dependency-root',type=Path,required=True)
    args=parser.parse_args();dep=args.dependency_root.resolve();dist=(dep/'owned-distribution/target/distribution').resolve();jdk=dep/'build/jdk1.8.0_391'
    with tempfile.TemporaryDirectory(prefix='equipment-ledger-projection-') as folder:
        out=Path(folder);java=out/'EquipmentLedgerProjectionCheck.java';java.write_text(JAVA,encoding='utf-8')
        cp=':'.join(map(str,[out,dist/'app/lims-owned-application.jar',dist/'lib/*',dep/'build/reconstituted-public-deps/sqlite-jdbc-3.31.1.jar']))
        subprocess.run([str(jdk/'bin/javac'),'-source','8','-target','8','-encoding','UTF-8','-cp',cp,'-d',str(out),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/service/impl/PfEquiptServiceImpl.java'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/dao/EmcEquiptDao.java'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/dao/PfEquiptLedgerDao.java'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/mapper/EmcEquiptMapper.java'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/mapper/PfEquiptMapper.java'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/dao/impl/EmcEquiptDaoImpl.java'),str(java)],check=True)
        subprocess.run([str(jdk/'bin/java'),'-cp',cp,'EquipmentLedgerProjectionCheck',str(ROOT/'reconstituted/framework/owned-core-common/src/main/resources/com/sunwayworld/framework/mybatis/mapper/conf/global-oracle-mapper.xml'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/mapper/conf/emc-equipt-oracle-mapper.xml'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/mapper/conf/pf-equipt-oracle-mapper.xml')],check=True)
if __name__=='__main__':main()
