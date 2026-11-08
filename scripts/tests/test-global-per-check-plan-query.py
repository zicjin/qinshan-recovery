#!/usr/bin/env python3
"""Exercise the 407001 read seam through actual MyBatis and an in-memory fixture.

Reads existing source-only build dependencies; never connects to Oracle or HTTP.
Compiler output uses an explicit persistent temporary root, separate from target/.
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
import com.sunwayworld.framework.context.*;import com.sunwayworld.framework.database.dialect.*;
import com.sunwayworld.framework.database.sql.SqlHelper;import com.sunwayworld.framework.mybatis.interceptor.MybatisInterceptor;import com.sunwayworld.framework.mybatis.page.*;import com.sunwayworld.framework.mybatis.mapper.*;import com.sunwayworld.framework.data.page.*;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptPerCheckPlanMapper;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.*;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl.EmcEquiptPerCheckPlanDaoImpl;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl.*;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import org.springframework.web.bind.annotation.*;
public class GlobalPerCheckPlanQueryCheck {
 static final String NS="com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptPerCheckPlanMapper.";
 public interface InheritedPlanMapper extends EmcEquiptPerCheckPlanMapper {}
 static void need(boolean b,String m){if(!b)throw new AssertionError(m);}
 static void load(Configuration c,String p)throws Exception{try(InputStream in=new FileInputStream(p)){new XMLMapperBuilder(in,c,p,c.getSqlFragments()).parse();}}
 static void inject(Object o,Class<?> owner,String name,Object value)throws Exception{java.lang.reflect.Field f=owner.getDeclaredField(name);f.setAccessible(true);f.set(o,value);}
 static class UnitDialect extends OracleDialect {public String getLimitSql(String s,int o,int n){return s+" LIMIT "+n+" OFFSET "+o;}}
 static MapperParameter params(String org,String user){MapperParameter p=new MapperParameter();p.put("isolateAbandon","1");p.put("globalPlanParentOrgId",org);p.put("globalPlanLoginUserId",user);return p;}
 static PageRowBounds bounds(int page){Map<String,String> p=new HashMap<>();p.put("n",String.valueOf(page));p.put("s","1");return new RestJsonWrapperBean(p,null,null).extractPageRowBounds();}
 static RestJsonWrapperBean wrapper(String f,String qf,String order,int page){Map<String,String> p=new HashMap<>();p.put("f",f);p.put("qf",qf);p.put("o",order);p.put("n",String.valueOf(page));p.put("s","1");return new RestJsonWrapperBean(p,null,null);}
 static String ext(EmcEquiptPerCheckPlanBean row,String key){return row.getExt$Item(key);}
 static Page<EmcEquiptPerCheckPlanBean> execute(EmcEquiptPerCheckPlanServiceImpl service,EmcEquiptPerCheckPlanQueryDao dao,MapperParameter p,int n){return service.selectPagination(()->dao.selectGlobalPerCheckPlans(p),bounds(n));}
 public static void main(String[] a)throws Exception{
  GenericApplicationContext ctx=new GenericApplicationContext();ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture",Collections.singletonMap("spring.datasource.driver-class-name","org.sqlite.JDBC")));ctx.refresh();ApplicationContextHelper.setApplicationContext(ctx);DialectRepository.setDialect(new UnitDialect());
  Class.forName("org.sqlite.JDBC");String url="jdbc:sqlite:file:globalpercheck?mode=memory&cache=shared";
  try(Connection fixture=DriverManager.getConnection(url)){
   Statement x=fixture.createStatement();
   x.execute("CREATE TABLE T_EMC_EQUIPT(ID INTEGER PRIMARY KEY,EQUIPTID TEXT,EQUIPTNAME TEXT,SPECIFICATION TEXT,ORGID TEXT,EQUIPTCATEGORYID INTEGER)");
   x.execute("INSERT INTO T_EMC_EQUIPT VALUES(1,'EQ-Z','Z equipment','Z model','org-a',101),(2,'EQ-A','A equipment','A model','org-a',101),(3,'EQ-F','Foreign equipment','F model','org-b',101),(4,'EQ-U','Ungranted category','U model','org-a',102)");
   x.execute("CREATE TABLE T_EMC_EQUIPT_PER_CHECK_PLAN(ID INTEGER PRIMARY KEY,EQUIPTID INTEGER,MAINTID INTEGER,CYCLE TEXT,REMARK TEXT,CREATEDBYORGID TEXT)");
   x.execute("INSERT INTO T_EMC_EQUIPT_PER_CHECK_PLAN VALUES(11,1,201,'12','Z remark','org-a'),(12,2,201,'6','A remark','org-a'),(13,3,201,'1','Foreign remark','org-b'),(14,4,201,'1','Denied remark','org-a'),(15,999,201,'1','Orphan remark','org-a')");
   x.execute("CREATE TABLE T_EMC_EQUIPT_MAINT(ID INTEGER,MAINTEVENT TEXT)");x.execute("INSERT INTO T_EMC_EQUIPT_MAINT VALUES(201,'duringCheck')");
   x.execute("CREATE TABLE T_CORE_CODE(CODE TEXT,CODECATEGORYID TEXT,CODENAME TEXT)");x.execute("INSERT INTO T_CORE_CODE VALUES('duringCheck','maintainEvent','Period check'),('duringCheck','other','Wrong category')");
   x.execute("CREATE TABLE T_EMC_ORG_PERMISSION(PERMISSIONTYPEID TEXT,TARGETID INTEGER,ORGID TEXT)");x.execute("INSERT INTO T_EMC_ORG_PERMISSION VALUES('T_EMC_EQUIPT_CATEGORY',101,'grant-org'),('T_EMC_EQUIPT_CATEGORY',101,'grant-org')");
   x.execute("CREATE TABLE T_CORE_USER_PERMISSION(USERID TEXT,PERMISSIONTYPEID TEXT,TARGETID TEXT)");x.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES('fixture-user','T_CORE_ORG','grant-org'),('fixture-user','T_CORE_ORG','grant-org')");
   Configuration c=new Configuration(new Environment("unit",new JdbcTransactionFactory(),new UnpooledDataSource("org.sqlite.JDBC",url,null)));c.setLocalCacheScope(LocalCacheScope.STATEMENT);c.addInterceptor(new MybatisInterceptor());load(c,a[0]);load(c,a[1]);need(c.hasStatement(NS+"selectGlobalPerCheckPlans"),"independent global Plan statement missing");c.addMapper(InheritedPlanMapper.class);
   try(SqlSession ss=new SqlSessionFactoryBuilder().build(c).openSession(true)){
    EmcEquiptPerCheckPlanDaoImpl dao=new EmcEquiptPerCheckPlanDaoImpl();inject(dao,EmcEquiptPerCheckPlanDaoImpl.class,"emcEquiptPerCheckPlanMapper",ss.getMapper(InheritedPlanMapper.class));
    EmcEquiptPerCheckPlanServiceImpl plans=new EmcEquiptPerCheckPlanServiceImpl();inject(plans,EmcEquiptPerCheckPlanServiceImpl.class,"emcEquiptPerCheckPlanDao",dao);
    MapperParameter asc=params("org-a","fixture-user");asc.setOrderParam("equiptNo","asc");Page<EmcEquiptPerCheckPlanBean> p1=execute(plans,dao,asc,1);need(p1.getTotal()==2&&p1.getRows().size()==1,"hard org/category count with duplicate grants must not multiply Plan rows");EmcEquiptPerCheckPlanBean row=p1.getRows().get(0);need(row.getId()==12L&&row.getEquiptId()==2L&&row.getMaintId()==201L&&"EQ-A".equals(ext(row,"equiptno"))&&"A equipment".equals(ext(row,"equiptname"))&&"A model".equals(ext(row,"specification"))&&"Period check".equals(ext(row,"codename"))&&"duringCheck".equals(row.getMaintEvent()),"original PlanBean plus exact equipment/code projection");
    Page<EmcEquiptPerCheckPlanBean> def=execute(plans,dao,params("org-a","fixture-user"),1);need(def.getTotal()==2&&def.getRows().get(0).getId()==12L,"default ID-desc ordering");MapperParameter desc=params("org-a","fixture-user");desc.setOrderParam("equiptNo","desc");need(execute(plans,dao,desc,1).getRows().get(0).getId()==11L,"explicit descending equipment code");
    Page<EmcEquiptPerCheckPlanBean> p2=execute(plans,dao,asc,2);need(p2.getTotal()==2&&p2.getPageNumber()==2&&p2.getRows().get(0).getId()==11L,"ordering before pagination");
    MapperParameter filter=params("org-a","fixture-user");filter.addFilterParamPatterns(new FilterParamPattern("CISC","ext$.codename","period"),new FilterParamPattern("CISC","ext$.equiptname","A equipment"));need(execute(plans,dao,filter,1).getTotal()==1,"CODENAME and equipment filter share actual SQL projection");
    MapperParameter none=params("org-a","fixture-user");none.addFilterParamPatterns(new FilterParamPattern("CISC","equiptNo","NO-MATCH"));need(execute(plans,dao,none,1).getTotal()==0,"no-match result");
    MapperParameter foreign=params("org-a","fixture-user");foreign.addFilterParamPatterns(new FilterParamPattern("CISC","equiptNo","EQ-F"));need(execute(plans,dao,foreign,1).getTotal()==0,"foreign organization not visible");
    MapperParameter category=params("org-a","fixture-user");category.addFilterParamPatterns(new FilterParamPattern("CISC","equiptNo","EQ-U"));need(execute(plans,dao,category,1).getTotal()==0,"ungranted category excluded even in same parent org");
    need(execute(plans,dao,params(null,"fixture-user"),1).getTotal()==0&&execute(plans,dao,params("","fixture-user"),1).getTotal()==0,"null/empty parent org fail closed");need(execute(plans,dao,params("org-a",null),1).getTotal()==0&&execute(plans,dao,params("org-a",""),1).getTotal()==0&&execute(plans,dao,params("org-a","ungranted-user"),1).getTotal()==0,"null/ungranted actual principal fail closed");
    BoundSql bs=c.getMappedStatement(NS+"selectGlobalPerCheckPlans").getBoundSql(filter);String sql=SqlHelper.cleanExtPrefix(bs.getSql());String count=SqlHelper.getSelectTotalSql(sql,new String[0],new String[0]);need(count.contains("EQUIPTORGID")&&count.contains("CODENAME")&&!count.toUpperCase().contains("ORDER BY"),"count preserves scope and projected predicates");String oracle=new OracleDialect().getLimitSql(sql,1,1);need(oracle.contains("ROWNUM <= 2")&&oracle.contains("RN_ > 1")&&oracle.indexOf("ORDER BY")<oracle.indexOf("WHERE ROWNUM"),"Oracle pagination orders before rownum");
    System.out.println("PASS actual PlanService/DAO/inherited MyBatis map-to-PlanBean, count/filter/order/page/hard-org/category");
    // Duplicate original code rows retain original mapper cardinality; permission
    // duplicates above use EXISTS and must not multiply it.
    x.execute("INSERT INTO T_CORE_CODE VALUES('duringCheck','maintainEvent','Period check duplicate')");need(execute(plans,dao,asc,1).getTotal()==4,"original code-join cardinality must not be silently deduplicated");x.execute("DELETE FROM T_CORE_CODE WHERE CODENAME='Period check duplicate'");
    for(java.lang.reflect.Method m:EmcEquiptPerCheckPlanDao.class.getMethods())need(!m.getName().equals("selectGlobalPerCheckPlans"),"generic Plan DAO contract expanded");need(EmcEquiptPerCheckPlanQueryDao.class.getDeclaredMethods().length==1&&EmcEquiptPerCheckPlanQueryDao.class.isAssignableFrom(dao.getClass()),"query-only interface implemented by real DAO");need(!EmcEquiptPerCheckPlanQueryDao.class.isAssignableFrom(com.sunwayworld.owned.equipment.OwnedEquipmentPerCheckPlanDao.class),"transitional DAO must not need this query contract");
    CoreUserBean user=new CoreUserBean();user.setId("fixture-user");user.setUserType("ordinary");user.setExt$Item("parentOrgId","org-a");new LocalContextHelper().setCoreUserService((CoreUserService)java.lang.reflect.Proxy.newProxyInstance(GlobalPerCheckPlanQueryCheck.class.getClassLoader(),new Class[]{CoreUserService.class},(o,m,v)->{if(m.getName().equals("selectById"))return user;throw new AssertionError("unexpected user boundary "+m.getName());}));LoginUser login=new LoginUser("fixture-user","");login.setOrgId("org-a");LocalContextHelper.setUserLogin(login);
    PfEquiptServiceImpl service=new PfEquiptServiceImpl();inject(service,PfEquiptServiceImpl.class,"perCheckPlanService",plans);inject(service,PfEquiptServiceImpl.class,"perCheckPlanQueryDao",dao);
    String spoof="{\"isolateAbandon\":\"1\",\"globalPlanParentOrgId\":\"org-b\",\"globalPlanLoginUserId\":\"ungranted-user\",\"target_type\":\"ignored\"}";
    Page<EmcEquiptPerCheckPlanBean> trusted=service.selectGlobalPerCheckPlans(wrapper(spoof,"{\"ext$.codename_CISC\":\"period\",\"ext$.equiptno_CISC\":\"eq-a\"}","[{\"ext$.equiptno\":\"asc\"}]",1));need(trusted.getTotal()==1&&trusted.getRows().get(0).getId()==12L,"actual Pf service overrides client org/principal and preserves wrapper filter");
    Page<EmcEquiptPerCheckPlanBean> noflags=service.selectGlobalPerCheckPlans(wrapper("{\"isolateAbandon\":\"1\"}","{}","[]",1));need(noflags.getTotal()==2,"server scope and category apply without any client equipList flag");
    com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl.PfEquiptResourceImpl controller=new com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl.PfEquiptResourceImpl();inject(controller,controller.getClass(),"pfEquiptService",service);need(controller.selectGlobalPerCheckPlans(wrapper(spoof,"{}","[]",1)).getTotal()==2,"real Resource delegates original Pf/Plan chain");
    Page<EmcEquiptPerCheckPlanBean> loginSpoof=service.selectGlobalPerCheckPlans(wrapper("{\"isolateAbandon\":\"1\",\"globalPlanParentOrgId\":\"org-b\",\"globalPlanLoginUserId\":\"ungranted-user\",\"loginUser\":{\"id\":\"ungranted-user\"}}","{}","[]",1));need(loginSpoof.getTotal()==2,"client loginUser cannot alter independent actual-principal permission");
    user.setExt$Item("parentOrgId",null);need(service.selectGlobalPerCheckPlans(wrapper(spoof,"{}","[]",1)).getTotal()==0,"actual service null org cannot use spoofed org");user.setExt$Item("parentOrgId","");need(service.selectGlobalPerCheckPlans(wrapper(spoof,"{}","[]",1)).getTotal()==0,"actual emptyorg failclosed");user.setExt$Item("parentOrgId","org-a");user.setId(null);need(service.selectGlobalPerCheckPlans(wrapper(spoof,"{}","[]",1)).getTotal()==0,"actual missingprincipal failclosed");user.setId("");need(service.selectGlobalPerCheckPlans(wrapper(spoof,"{}","[]",1)).getTotal()==0,"actual blankprincipal failclosed");user.setId("ungranted-user");need(service.selectGlobalPerCheckPlans(wrapper(spoof,"{}","[]",1)).getTotal()==0,"actual ungranted session not replaced by client");LocalContextHelper.removeUserLogin();
    java.lang.reflect.Method resource=com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl.PfEquiptResourceImpl.class.getMethod("selectGlobalPerCheckPlans",RestJsonWrapperBean.class);RequestMapping mapping=resource.getAnnotation(RequestMapping.class);need(Arrays.equals(mapping.value(),new String[]{"/per-check-plans/queries"})&&Arrays.equals(mapping.method(),new RequestMethod[]{RequestMethod.POST}),"one explicit read-only mapping");
    System.out.println("PASS actual Pf service client override/null/session permission; original CRUD interface unchanged");
   }
  }finally{LocalContextHelper.removeUserLogin();ApplicationContextHelper.setApplicationContext(null);ctx.close();}
 }
}
'''
def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--dependency-root',type=Path,required=True)
    parser.add_argument('--temp-root',type=Path,required=True)
    args=parser.parse_args();dep=args.dependency_root.resolve();dist=dep/'owned-distribution/target/distribution';jdk=dep/'build/jdk1.8.0_391'
    args.temp_root.mkdir(parents=True,exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='global-per-check-plan-',dir=str(args.temp_root)) as folder:
        out=Path(folder);java=out/'GlobalPerCheckPlanQueryCheck.java';java.write_text(JAVA,encoding='utf-8')
        cp=':'.join(map(str,[out,dist/'app/lims-owned-application.jar',dist/'lib/*',dep/'build/reconstituted-public-deps/sqlite-jdbc-3.31.1.jar']))
        sources=[
            'service/PfEquiptService.java','service/impl/PfEquiptServiceImpl.java',
            'resource/PfEquiptResource.java','resource/impl/PfEquiptResourceImpl.java',
            'dao/EmcEquiptPerCheckPlanQueryDao.java','dao/impl/EmcEquiptPerCheckPlanDaoImpl.java',
            'mapper/EmcEquiptPerCheckPlanMapper.java',
            'dao/EmcEquiptPerCheckPlanDao.java','service/impl/EmcEquiptPerCheckPlanServiceImpl.java',
            'service/EmcEquiptPerCheckPlanService.java',

        ]
        base=ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt'
        subprocess.run([str(jdk/'bin/javac'),'-source','8','-target','8','-encoding','UTF-8','-cp',cp,'-d',str(out),*[str(base/name) for name in sources],str(ROOT/'owned-equipment/src/main/java/com/sunwayworld/owned/equipment/OwnedEquipmentPerCheckPlanDao.java'),str(java)],check=True)
        subprocess.run([str(jdk/'bin/java'),'-cp',cp,'GlobalPerCheckPlanQueryCheck',str(ROOT/'reconstituted/framework/owned-core-common/src/main/resources/com/sunwayworld/framework/mybatis/mapper/conf/global-oracle-mapper.xml'),str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/mapper/conf/emc-equipt-per-check-plan-oracle-mapper.xml')],check=True)
if __name__=='__main__':main()
