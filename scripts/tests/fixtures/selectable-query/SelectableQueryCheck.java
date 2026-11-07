package selectablefixture;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.*;
import com.sunwayworld.framework.database.dialect.*;
import com.sunwayworld.framework.mybatis.interceptor.MybatisInterceptor;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.dao.impl.CoreOrgDaoImpl;
import com.sunwayworld.module.mdm.org.mapper.CoreOrgMapper;
import com.sunwayworld.module.mdm.org.service.impl.CoreOrgServiceImpl;
import java.util.*;
import javax.sql.DataSource;
import org.mybatis.spring.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.*;

/** Actual inherited public selectable service and DAO with original Oracle Mapper XML.
 * Only the JDBC fixture, request and dependency assembly are local; no live HTTP/Oracle claim. */
public final class SelectableQueryCheck {
    static int checks;
    static class OrgDao extends CoreOrgDaoImpl {
        CoreOrgMapper mapper; EntityContext entity;
        @Override public CoreOrgMapper getMapper() { return mapper; }
        @Override public EntityContext getEntityContext() { return entity; }
    }
    static EntityContext entity(DataSource ds) throws Exception {
        EntityContext context=new EntityContext(); context.setTableName("T_CORE_ORG");
        List<EntityColumnContext> columns=new ArrayList<>();
        try(java.sql.Connection c=ds.getConnection(); java.sql.ResultSet rs=c.getMetaData().getColumns(null,null,"T_CORE_ORG",null)) {
            while(rs.next()) {
                EntityColumnContext col=new EntityColumnContext(); col.setEntityClass(CoreOrgBean.class);
                col.setColumnName(rs.getString("COLUMN_NAME")); col.setType(col.getColumnName().equals("ORDERNO")?Long.class:String.class);
                columns.add(col); if(col.getColumnName().equals("ID")) context.setIdContext(col);
            }
        }
        context.setColumnContextList(columns); return context;
    }
    static void expect(CoreOrgServiceImpl service,String expected,String label,String... params) {
        MockHttpServletRequest request=new MockHttpServletRequest("GET","/secure/core/module/mdm/orgs/queries/selectable");
        for(int i=0;i<params.length;i+=2) request.addParameter(params[i],params[i+1]);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        com.sunwayworld.framework.context.LocalContextHelper.setServletRequest(request);
        try {
            List<String> ids=new ArrayList<>(); for(CoreOrgBean row:service.selectSelectableList()) ids.add(row.getId());
            Collections.sort(ids); checks++;
            if(!expected.equals(String.join(",",ids))) throw new AssertionError(label+": expected "+expected+", got "+ids);
        } finally { RequestContextHolder.resetRequestAttributes(); com.sunwayworld.framework.context.LocalContextHelper.removeServletRequest(); }
    }
    public static void main(String[] args) throws Exception {
        try(GenericApplicationContext context=new GenericApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new org.springframework.core.env.MapPropertySource("fixture",Collections.singletonMap("spring.datasource.driver-class-name","org.h2.Driver")));
            context.refresh(); ApplicationContextHelper.setApplicationContext(context);
            DialectRepository.setDialect(new OracleDialect());
            DataSource ds=new DriverManagerDataSource("jdbc:h2:mem:selectable;MODE=Oracle;DB_CLOSE_DELAY=-1","sa","");
            JdbcTemplate db=new JdbcTemplate(ds);
            db.execute("CREATE TABLE T_CORE_ORG(ID VARCHAR(100),ORGNAME VARCHAR(100),PARENTID VARCHAR(100),SAMPLEDEPARTMENT VARCHAR(1),ORDERNO BIGINT)");
            db.update("INSERT INTO T_CORE_ORG VALUES('a','Alpha','org-a','1',1),('b','Beta','org-a','1',2),('foreign','Foreign','org-b','1',3),('other','Other','org-a','0',4)");
            db.execute("CREATE TABLE T_SELECTABLE_LINK(ID VARCHAR(100),KIND VARCHAR(20))");
            db.update("INSERT INTO T_SELECTABLE_LINK VALUES('a','allowed')");
            org.apache.ibatis.session.Configuration config=new org.apache.ibatis.session.Configuration();
            config.addInterceptor(new MybatisInterceptor());
            SqlSessionFactoryBean factory=new SqlSessionFactoryBean(); factory.setConfiguration(config); factory.setDataSource(ds);
            String base=args[0]+"/reconstituted/framework/owned-core-common/src/main/resources/";
            factory.setMapperLocations(new FileSystemResource(base+"com/sunwayworld/framework/mybatis/mapper/conf/global-oracle-mapper.xml"),new FileSystemResource(base+"com/sunwayworld/module/mdm/org/mapper/conf/core-org-oracle-mapper.xml"));
            OrgDao dao=new OrgDao(); dao.mapper=new SqlSessionTemplate(factory.getObject()).getMapper(CoreOrgMapper.class); dao.entity=entity(ds);
            CoreOrgServiceImpl service=new CoreOrgServiceImpl(); ReflectionTestUtils.setField(service,"orgDao",dao);
            expect(service,"a","single a","sampleDepartment_SEQ","1","id_SEQ","a");
            expect(service,"b","single b","sampleDepartment_SEQ","1","id_SEQ","b");
            expect(service,"a,b","multi SEQ union","sampleDepartment_SEQ","1","id_SEQ","a,b");
            expect(service,"a","matching plus missing","sampleDepartment_SEQ","1","id_SEQ","a,missing");
            expect(service,"","no matches","sampleDepartment_SEQ","1","id_SEQ","missing,absent");
            expect(service,"a","outer sample department AND","sampleDepartment_SEQ","1","id_SEQ","a,other");
            expect(service,"","conflicting outer criterion","sampleDepartment_SEQ","0","id_SEQ","a,b");
            expect(service,"a,b","explicit IN unchanged","sampleDepartment_SEQ","1","id_IN","a,b");
            expect(service,"a,b","explicit OR unchanged","sampleDepartment_SEQ","1","id_OR","a,b");
            expect(service,"","multi case insensitive conjunction unchanged","sampleDepartment_SEQ","1","id_CISEQ","A,B");
            expect(service,"a","single case insensitive unchanged","id_CISEQ","A");
            expect(service,"a","SQL-looking literal stays bound","id_SEQ","a,x' OR '1'='1");
            expect(service,"","SEQ case sensitivity","sampleDepartment_SEQ","1","id_SEQ","A,B");
            expect(service,"","multi SC preserves original conjunction","orgName_SC","Al,Be");
            expect(service,"foreign,other","multi SNE preserves original conjunction","id_DIFFER","a,b");
            expect(service,"","default EQ preserves original conjunction","id","a,b");
            expect(service,"a,b","organization criterion remains AND","sampleDepartment_SEQ","1","id_SEQ","a,b,foreign","parentId_SEQ","org-a");
            expect(service,"foreign","foreign organization explicit criterion","sampleDepartment_SEQ","1","id_SEQ","a,b,foreign","parentId_SEQ","org-b");
            expect(service,"a,b","unrecognized filter_ special key remains non-column parameter","sampleDepartment_SEQ","1","id_SEQ","a,b","filter_scheme_only","1");
            String target="{\"type\":\"assigned\",\"targetTable\":\"T_SELECTABLE_LINK\",\"targetMatchColumn\":\"ID\",\"thisMatchColumn\":\"ID\",\"filter\":\"[{\\\"targetFilterColumn\\\":\\\"KIND\\\",\\\"targetFilterValue\\\":\\\"allowed\\\"}]\"}";
            expect(service,"a","target_filter remains outside equality union","id_SEQ","a,b","target_filter",target);
            db.execute("SHUTDOWN");
        } finally { ApplicationContextHelper.setApplicationContext(null); }
        System.out.println("selectable query actual Service/DAO/Oracle XML/JDBC: PASS ("+checks+" checks)");
    }
}
