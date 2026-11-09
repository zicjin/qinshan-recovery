package auditqueryfixture;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.sunwayworld.framework.restful.resolver.wrapper.ToRestJsonWrapperResolver;
import com.sunwayworld.owned.internal.security.*;
import com.sunwayworld.owned.internal.web.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** DispatcherServlet regression; uses actual wrapper resolver and JDBC rows. */
public class AuditQueryMvcCheck {
    static final String RECORDS="/secure/core/module/sys/at-records/queries";
    static final long LARGE_ID=2030254965053728L;
    static final String LINES="/secure/core/module/sys/at-records/record-lines/queries";
    static int checks;
    static org.mybatis.spring.SqlSessionTemplate mapperSession;
    static com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary auditBoundary;
    static Object recordTarget,lineTarget;
    static com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService originalRecords;
    static com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService originalLines;
    static class Identity implements OwnedIdentityProvider {
        boolean deny, stale, fail;
        public OwnedPrincipal authenticate(String id,char[] password) { return new OwnedPrincipal("user-a","org-a","Org A"); }
        public OwnedPrincipal refresh(OwnedPrincipal p) throws java.sql.SQLException {
            if (fail) throw new java.sql.SQLException("fixture identity unavailable");
            if (stale) throw new AccessDeniedException("revoked");
            return p;
        }
        public void authorize(OwnedPrincipal p,String permission) { if(deny) throw new AccessDeniedException("revoked grant"); }
        public Set<String> permittedOrganizationIds(OwnedPrincipal p) { return Collections.emptySet(); }
        public Map<String,Object> accountProfile(OwnedPrincipal p) { return Collections.emptyMap(); }
    }
    static void equal(Object expected,Object actual,String label) {
        checks++; if(!Objects.equals(expected,actual)) throw new AssertionError(label+": expected "+expected+", got "+actual);
    }
    static JSONObject query(MockMvc mvc,MockHttpSession session,String path,String body,int status) throws Exception {
        MvcResult result=mvc.perform(post(path).session(session).contentType("application/json").accept("application/json").content(body)).andReturn();
        equal(status,result.getResponse().getStatus(),"HTTP status "+path);
        return JSON.parseObject(result.getResponse().getContentAsString());
    }
    static void multiSeqQueries(MockMvc mvc,MockHttpSession session,String path,String separator) throws Exception {
        equal(2,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"UPDATE\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"multi SEQ equality union "+path);
        equal(1,query(mvc,session,path,"{\"p\":{\"qf\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"NO_SUCH_AUDIT\"}}}",200).getIntValue("total"),"qf mixed matching and absent values");
        equal(0,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"ABSENT"+separator+"NO_SUCH_AUDIT\"}}}",200).getIntValue("total"),"multi SEQ no match");
        equal(2,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"UPDATE"+separator+"DELETE\"}}}",200).getIntValue("total"),"three values retain exact organization scope");
        equal(1,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"DELETE\"},\"qf\":{\"targetId_SEQ\":\"target-a\"}}}",200).getIntValue("total"),"union AND other criterion");
        equal(0,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"UPDATE\"},\"qf\":{\"targetId_SEQ\":\"NO-MATCH\"}}}",200).getIntValue("total"),"union cannot bypass conflicting criterion");
        equal(1,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT\"},\"qf\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"UPDATE\"}}}",200).getIntValue("total"),"f and qf on same field remain AND");
        equal(1,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"UPDATE\",\"auditTrailName_SEQ\":\"create item"+separator+"NO-MATCH\"}}}",200).getIntValue("total"),"separate multi value fields remain AND groups");
        equal(1,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"update\"}}}",200).getIntValue("total"),"SEQ stays case sensitive");
        equal(2,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_CISEQ\":\"insert"+separator+"update\"}}}",200).getIntValue("total"),"CISEQ keeps case insensitive union");
        equal(2,query(mvc,session,path,"{\"p\":{\"f\":{\"id_SEQ\":\"2030254965053728"+separator+"2\"}}}",200).getIntValue("total"),"multi SEQ preserves 16 digit ID precision");
        equal(2,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"UPDATE\",\"createdByOrgId_SEQ\":\"org-a"+separator+"org-b\",\"ownedAuditOrgId\":\"org-b\"}}}",200).getIntValue("total"),"multi org client filter cannot broaden scope");
        equal(0,query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"DELETE\"},\"qf\":{\"createdByOrgId_SEQ\":\"org-b\"}}}",200).getIntValue("total"),"foreign org criterion AND server scope");
        JSONObject page=query(mvc,session,path,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT"+separator+"UPDATE\"},\"o\":[{\"ID\":\"asc\"}],\"n\":2,\"s\":1}}",200);
        equal(2,page.getIntValue("total"),"union count before paging");
        equal(1,page.getJSONArray("rows").size(),"union page size");
        equal(LARGE_ID,page.getJSONArray("rows").getJSONObject(0).getLongValue("id"),"union sorted second page");
    }
    static void targetLineQueries(MockMvc mvc,MockHttpSession session,JdbcTemplate db,String fullText,String separator) throws Exception {
        // Original core-at-record-detail.js sends targetId_EQ=tableName+'$'+id,
        // without a recordId. Use actual wrapper/service/Mapper and parent rows.
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='T_DEMO$code-A' WHERE ID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='T_DEMO$code-A' WHERE RECORDID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='T_DEMO$code-B' WHERE ID=3");
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='T_DEMO$code-B' WHERE RECORDID=3");
        // Same exact target in another parent organization must remain invisible,
        // even when the child incorrectly carries the local organization.
        db.update("INSERT INTO T_CORE_AT_RECORD VALUES(4,'UPDATE','foreign exact target','T_DEMO$code-A','org-b',TIMESTAMP '2026-01-04 12:00:00','note')");
        db.update("INSERT INTO T_CORE_AT_RECORD_LINE(ID,RECORDID,TARGETID,CREATEDBYORGID,TABLENAME,NEWVALUE,OLDVALUE,\"COLUMN\") VALUES(14,4,'T_DEMO$code-A','org-a','T_DEMO','foreign secret','old','FIELD_A')");
        JSONObject page=query(mvc,session,LINES,"{\"p\":{\"f\":{\"targetId_EQ\":\"T_DEMO$code-A\"},\"o\":[{\"ID\":\"asc\"}],\"n\":1,\"s\":20}}",200);
        equal(2,page.getIntValue("total"),"original UI targetId_EQ admits exact target without recordId");
        equal(fullText,page.getJSONArray("rows").getJSONObject(0).getString("newValue"),"target query preserves full actual CLOB");
        equal(false,page.toJSONString().contains("foreign secret"),"same target foreign parent excluded despite local child org");
        equal(false,page.toJSONString().contains("ownedAuditOrg"),"target query never projects trusted scope");
        for (String container:Arrays.asList("f","qf")) {
            for (String match:Arrays.asList("EQ","SEQ")) {
                equal(2,query(mvc,session,LINES,"{\"p\":{\""+container+"\":{\"tableName_SEQ\":\"T_DEMO\",\"targetId_"+match+"\":\"T_DEMO$code-A\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"exact target "+container+" "+match);
            }
        }
        equal(0,query(mvc,session,LINES,"{\"p\":{\"f\":{\"targetId_EQ\":\"T_DEMO$code-A\",\"tableName_SEQ\":\"T_OTHER\"}}}",200).getIntValue("total"),"original table criterion remains AND");
        equal(1,query(mvc,session,LINES,"{\"p\":{\"f\":{\"targetId_EQ\":\"T_DEMO$code-A\",\"column_SEQ\":\"FIELD_A\"}}}",200).getIntValue("total"),"original column criterion remains AND");
        equal(0,query(mvc,session,LINES,"{\"p\":{\"f\":{\"targetId_EQ\":\"T_DEMO$code-A\"},\"qf\":{\"targetId_SEQ\":\"T_DEMO$code-B\"}}}",200).getIntValue("total"),"conflicting exact target criteria remain AND, not first match wins");
        equal(0,query(mvc,session,LINES,"{\"p\":{\"f\":{\"targetId_EQ\":\"T_DEMO$code-B\",\"ownedAuditOrgId\":\"org-b\",\"loginOrgId\":\"org-b\"},\"ownedAuditOrgId\":\"org-b\"}}",200).getIntValue("total"),"foreign target cannot spoof trusted organization");
        page=query(mvc,session,LINES,"{\"p\":{\"f\":{\"targetId_SEQ\":\"T_DEMO$code-A\"},\"o\":[{\"ID\":\"asc\"}],\"n\":2,\"s\":1}}",200);
        equal(2,page.getIntValue("total"),"target pagination total remains scoped");
        equal(1,page.getJSONArray("rows").size(),"target pagination second page size");
        equal(12L,page.getJSONArray("rows").getJSONObject(0).getLongValue("id"),"target pagination preserves ordering and page");
        for (String value:Arrays.asList("T_DEMO$%","T_DEMO$_","T_DEMO$' OR 1=1 --","T_DEMO$NO-MATCH")) {
            JSONObject body=new JSONObject();JSONObject p=new JSONObject();JSONObject f=new JSONObject();
            f.put("targetId_EQ",value);p.put("f",f);body.put("p",p);
            equal(0,query(mvc,session,LINES,body.toJSONString(),200).getIntValue("total"),"EQ target value remains literal bound data "+value);
        }
        for (String value:Arrays.asList("", " ", "VOID", "T_DEMO", "T_DEMO$", "$code-A")) {
            JSONObject body=new JSONObject();JSONObject p=new JSONObject();JSONObject f=new JSONObject();
            f.put("targetId_EQ",value);p.put("f",f);body.put("p",p);
            query(mvc,session,LINES,body.toJSONString(),400);
        }
        for (String filter:Arrays.asList("{\"tableName_SEQ\":\"T_DEMO\"}","{\"targetId_CISC\":\"T_DEMO$\"}","{\"targetId_SC\":\"T_DEMO$code-A\"}","{\"targetId_IN\":\"T_DEMO$code-A\"}","{\"targetId\":\"T_DEMO$code-A\"}","{\"targetId_EQ\":null}")) {
            query(mvc,session,LINES,"{\"p\":{\"f\":"+filter+"}}",400);
        }
        JSONObject body=new JSONObject();JSONObject p=new JSONObject();JSONObject f=new JSONObject();
        f.put("targetId_SEQ","T_DEMO$code-A"+separator+"T_DEMO$code-B");p.put("f",f);body.put("p",p);
        query(mvc,session,LINES,body.toJSONString(),400);
        query(mvc,session,LINES,"{\"p\":{\"recordId_SEQ\":\"bad\",\"f\":{\"targetId_EQ\":\"T_DEMO$code-A\"}}}",400);
        query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":\"bad\",\"targetId_EQ\":\"T_DEMO$code-A\"}}}",400);
        query(mvc,session,LINES,"{\"p\":{\"qf\":{\"recordId_SEQ\":\"bad\"},\"f\":{\"targetId_EQ\":\"T_DEMO$code-A\"}}}",400);
        equal(2,query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":2030254965053728,\"targetId_EQ\":\"T_DEMO$code-A\"}}}",200).getIntValue("total"),"recordId anchor remains compatible with exact target criterion");
        equal(0,query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":3,\"targetId_EQ\":\"T_DEMO$code-A\"}}}",200).getIntValue("total"),"recordId cannot be bypassed by same-org target");
        db.update("DELETE FROM T_CORE_AT_RECORD_LINE WHERE ID=14");db.update("DELETE FROM T_CORE_AT_RECORD WHERE ID=4");
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='target-a' WHERE ID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='target-a' WHERE RECORDID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='target-b' WHERE ID=3");
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='target-b' WHERE RECORDID=3");
    }
    static com.sunwayworld.framework.database.context.EntityContext entity(Class<?> type, String table, DataSource ds) throws Exception {
        com.sunwayworld.framework.database.context.EntityContext entity=new com.sunwayworld.framework.database.context.EntityContext();
        entity.setTableName(table);
        List<com.sunwayworld.framework.database.context.EntityColumnContext> columns=new ArrayList<>();
        try(java.sql.Connection c=ds.getConnection(); java.sql.ResultSet rs=c.getMetaData().getColumns(null,null,table,null)) {
            while(rs.next()) {
                com.sunwayworld.framework.database.context.EntityColumnContext column=new com.sunwayworld.framework.database.context.EntityColumnContext();
                column.setEntityClass(type); column.setColumnName(rs.getString("COLUMN_NAME"));
                int sqlType=rs.getInt("DATA_TYPE");
                column.setType(sqlType==java.sql.Types.BIGINT?Long.class:sqlType==java.sql.Types.TIMESTAMP?java.time.LocalDateTime.class:String.class);
                column.setClob(sqlType==java.sql.Types.CLOB); columns.add(column);
                if(column.getColumnName().equals("ID")) entity.setIdContext(column);
            }
        }
        entity.setColumnContextList(columns); return entity;
    }
    static class RecordDao extends com.sunwayworld.module.sys.at.dao.impl.CoreAuditTrailRecordDaoImpl {
        com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordMapper mapper;
        com.sunwayworld.framework.database.context.EntityContext entity;
        public com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordMapper getMapper() { return mapper; }
        public com.sunwayworld.framework.database.context.EntityContext getEntityContext() { return entity; }
    }
    static class LineDao extends com.sunwayworld.module.sys.at.dao.impl.CoreAuditTrailRecordLineDaoImpl {
        com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordLineMapper mapper;
        com.sunwayworld.framework.database.context.EntityContext entity;
        public com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordLineMapper getMapper() { return mapper; }
        public com.sunwayworld.framework.database.context.EntityContext getEntityContext() { return entity; }
    }
    static AuditTrailReadOperations original(DataSource ds,String root) throws Exception {
        org.springframework.context.support.GenericApplicationContext context=new org.springframework.context.support.GenericApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new org.springframework.core.env.MapPropertySource("fixture",Collections.singletonMap("spring.datasource.driver-class-name","org.h2.Driver")));
        String configuredSeparator=System.getProperty("audit.fixture.separator");
        if (configuredSeparator != null) context.getEnvironment().getPropertySources().addFirst(new org.springframework.core.env.MapPropertySource("original wrapper defaults",Collections.singletonMap("sunway.search.multi-value-matching-seperator",configuredSeparator)));
        context.refresh();
        com.sunwayworld.framework.context.ApplicationContextHelper.setApplicationContext(context);
        com.sunwayworld.framework.database.dialect.DialectRepository.setDialect(new com.sunwayworld.framework.database.dialect.OracleDialect());
        org.apache.ibatis.session.Configuration config=new org.apache.ibatis.session.Configuration();
        config.addInterceptor(new com.sunwayworld.framework.mybatis.interceptor.MybatisInterceptor());
        config.getTypeHandlerRegistry().register(com.sunwayworld.framework.mybatis.type.ClobTypeHandler.class);
        String base=root+"/reconstituted/framework/owned-core-common/src/main/resources/";
        String[] resources={"com/sunwayworld/framework/mybatis/mapper/conf/global-oracle-mapper.xml", "com/sunwayworld/module/sys/at/mapper/conf/core-audit-trail-record-oracle-mapper.xml", "com/sunwayworld/module/sys/at/mapper/conf/core-audit-trail-record-line-oracle-mapper.xml"};
        org.mybatis.spring.SqlSessionFactoryBean factory=new org.mybatis.spring.SqlSessionFactoryBean();
        factory.setConfiguration(config); factory.setDataSource(ds);
        factory.setMapperLocations(Arrays.stream(resources).map(r -> new org.springframework.core.io.FileSystemResource(base+r)).toArray(org.springframework.core.io.Resource[]::new));
        org.mybatis.spring.SqlSessionTemplate template=new org.mybatis.spring.SqlSessionTemplate(factory.getObject());
        mapperSession=template;
        RecordDao records=new RecordDao(); records.mapper=template.getMapper(com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordMapper.class);
        records.entity=entity(com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean.class,"T_CORE_AT_RECORD",ds);
        LineDao lines=new LineDao(); lines.mapper=template.getMapper(com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordLineMapper.class);
        lines.entity=entity(com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean.class,"T_CORE_AT_RECORD_LINE",ds);
        com.sunwayworld.module.sys.at.service.impl.CoreAuditTrailRecordServiceImpl recordService=new com.sunwayworld.module.sys.at.service.impl.CoreAuditTrailRecordServiceImpl();
        com.sunwayworld.module.sys.at.service.impl.CoreAuditTrailRecordLineServiceImpl lineService=new com.sunwayworld.module.sys.at.service.impl.CoreAuditTrailRecordLineServiceImpl();
        com.sunwayworld.emc.framework.security.OriginalAuditTrailReadScope scope=new com.sunwayworld.emc.framework.security.OriginalAuditTrailReadScope(auditBoundary);
        org.springframework.test.util.ReflectionTestUtils.setField(recordService,"auditReadScope",scope);
        org.springframework.test.util.ReflectionTestUtils.setField(lineService,"auditReadScope",scope);
        recordTarget=recordService; lineTarget=lineService;
        // Fixture dependency assembly only: exercised methods are the actual inherited service/DAO implementations.
        org.springframework.test.util.ReflectionTestUtils.setField(recordService,"recordDao",records);
        org.springframework.test.util.ReflectionTestUtils.setField(recordService,"recordLineService",lineService);
        org.springframework.test.util.ReflectionTestUtils.setField(lineService,"recordLineDao",lines);
        org.springframework.transaction.interceptor.TransactionInterceptor transactions=new org.springframework.transaction.interceptor.TransactionInterceptor(
                new org.springframework.jdbc.datasource.DataSourceTransactionManager(ds), new org.springframework.transaction.annotation.AnnotationTransactionAttributeSource());
        org.springframework.aop.framework.ProxyFactory recordProxy=new org.springframework.aop.framework.ProxyFactory(recordService);
        recordProxy.addAdvice(transactions);
        org.springframework.aop.framework.ProxyFactory lineProxy=new org.springframework.aop.framework.ProxyFactory(lineService);
        lineProxy.addAdvice(transactions);
        originalRecords=(com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService)recordProxy.getProxy();
        originalLines=(com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService)lineProxy.getProxy();
        return new com.sunwayworld.emc.framework.security.OriginalAuditTrailReadOperations(
                (com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService)recordProxy.getProxy(),
                (com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService)lineProxy.getProxy());
    }
    public static void main(String[] args) throws Exception {
        DataSource ds=new DriverManagerDataSource("jdbc:h2:mem:auditquery;MODE=Oracle;DB_CLOSE_DELAY=-1","sa","");
        JdbcTemplate db=new JdbcTemplate(ds);
        db.execute("CREATE TABLE T_CORE_AT_RECORD(ID BIGINT PRIMARY KEY,AUDITTRAILTYPE VARCHAR(30),AUDITTRAILNAME VARCHAR(100),TARGETID VARCHAR(100),CREATEDBYORGID VARCHAR(100),CREATEDTIME TIMESTAMP, \"COMMENT\" VARCHAR(100))");
        db.execute("CREATE TABLE T_CORE_AT_RECORD_LINE(ID BIGINT PRIMARY KEY,RECORDID BIGINT,TARGETID VARCHAR(100),CREATEDBYORGID VARCHAR(100),CREATEDTIME TIMESTAMP,NEWVALUE CLOB,OLDVALUE CLOB,\"COLUMN\" VARCHAR(100))");
        db.execute("ALTER TABLE T_CORE_AT_RECORD_LINE ADD TABLENAME VARCHAR(100)");
        db.update("INSERT INTO T_CORE_AT_RECORD VALUES(1,'INSERT','create item','target-a','org-a',TIMESTAMP '2026-01-01 12:00:00','note')");
        db.update("INSERT INTO T_CORE_AT_RECORD VALUES(2,'UPDATE','update item','target-a','org-a',TIMESTAMP '2026-01-02 12:00:00','note')");
        db.execute("CREATE TABLE T_CORE_ORG(ID VARCHAR(100),ROOTORGID VARCHAR(100))");
        db.update("INSERT INTO T_CORE_ORG VALUES('org-a','root-shared'),('org-b','root-shared')");
        db.update("INSERT INTO T_CORE_AT_RECORD VALUES(3,'DELETE','delete item','target-b','org-b',TIMESTAMP '2026-01-03 12:00:00','note')");
        String fullText=String.join("",Collections.nCopies(6000,"audit-value-"));
        db.update("INSERT INTO T_CORE_AT_RECORD_LINE(ID,RECORDID,TARGETID,CREATEDBYORGID,CREATEDTIME,NEWVALUE,OLDVALUE,\"COLUMN\") VALUES(11,1,'target-a','org-b',TIMESTAMP '2026-01-01 12:00:00',?,?,?)",fullText,"old text","FIELD_A");
        db.update("INSERT INTO T_CORE_AT_RECORD_LINE(ID,RECORDID,TARGETID,CREATEDBYORGID,CREATEDTIME,NEWVALUE,OLDVALUE,\"COLUMN\") VALUES(12,1,'target-a','org-a',TIMESTAMP '2026-01-01 12:00:00','short','old','FIELD_B')");
        db.update("INSERT INTO T_CORE_AT_RECORD_LINE(ID,RECORDID,TARGETID,CREATEDBYORGID,CREATEDTIME,NEWVALUE,OLDVALUE,\"COLUMN\") VALUES(13,3,'target-b','org-a',TIMESTAMP '2026-01-03 12:00:00','secret','old','FIELD_A')");
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET RECORDID=? WHERE RECORDID=1",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD SET ID=? WHERE ID=1",LARGE_ID);
        Identity identity=new Identity();
        OwnedIdentityHttpBoundary boundary=new OwnedIdentityHttpBoundary(identity,event -> {});
        MockHttpSession session=new MockHttpSession();
        boundary.login(session,new OwnedLoginRequest("user-a","fixture".toCharArray()));
        OwnedAuditTrailController controller=new OwnedAuditTrailController(boundary,ds);
        auditBoundary=boundary;
        AuditTrailReadOperations operations=original(ds,args[0]);
        controller.setReadOperations(operations);
        MockMvc mvc=MockMvcBuilders.standaloneSetup(controller).setCustomArgumentResolvers(new ToRestJsonWrapperResolver()).setMessageConverters(new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8), new FastJsonHttpMessageConverter()).build();
        com.sunwayworld.module.sys.at.resource.impl.CoreAuditTrailRecordResourceImpl resource=new com.sunwayworld.module.sys.at.resource.impl.CoreAuditTrailRecordResourceImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(resource,"recordService",originalRecords);
        org.springframework.test.util.ReflectionTestUtils.setField(resource,"recordLineService",originalLines);
        MockMvc originalMvc=MockMvcBuilders.standaloneSetup(resource).setCustomArgumentResolvers(new ToRestJsonWrapperResolver()).setMessageConverters(new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8),new FastJsonHttpMessageConverter()).build();
        equal(2,query(originalMvc,session,RECORDS,"{}",200).getIntValue("total"),"original raw read excludes foreign organization");
        equal(2,query(originalMvc,session,RECORDS+"/raw","{}",200).getIntValue("total"),"original raw scoped");
        equal(2,query(originalMvc,session,RECORDS+"/searchable","{}",200).getIntValue("total"),"original searchable scoped");
        for(String path:Arrays.asList(RECORDS,RECORDS+"/raw",RECORDS+"/searchable")) {
            equal(0,query(originalMvc,session,path,"{\"p\":{\"f\":{\"createdByOrgId_SEQ\":\"org-b\",\"ownedAuditOrgId\":\"org-b\"}}}",200).getIntValue("total"),"forged org cannot broaden original");
        }
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TABLENAME='T_DEMO'");
        equal(3,originalRecords.selectPagination(new com.sunwayworld.framework.restful.data.RestJsonWrapperBean()).getTotal(),"non HTTP service read preserved");
        String auditBase=RECORDS.substring(0,RECORDS.length()-8);
        equal("",originalMvc.perform(get(auditBase+"/3").session(session).accept("application/json")).andReturn().getResponse().getContentAsString(),"foreign detail suppressed");
        equal("create item",JSON.parseObject(originalMvc.perform(get(auditBase+"/"+LARGE_ID).session(session).accept("application/json")).andReturn().getResponse().getContentAsString()).getString("auditTrailName"),"own detail retained");
        equal(0,JSON.parseArray(originalMvc.perform(post(RECORDS+"/searchable/T_DEMO/none").session(session).contentType("application/json").accept("application/json").content("{}")).andReturn().getResponse().getContentAsString()).size(),"empty derived id list cannot broaden");
        // Direct original custom queries retain original list/null envelopes.
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='T_DEMO$1' WHERE ID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='T_DEMO$3' WHERE ID=3");
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='T_DEMO$1' WHERE RECORDID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='T_DEMO$3' WHERE RECORDID=3");
        equal(1,JSON.parseArray(originalMvc.perform(post(RECORDS+"/atrecord/T_DEMO/1").session(session).contentType("application/json").accept("application/json").content("{}")).andReturn().getResponse().getContentAsString()).size(),"own custom target retained");
        equal("",originalMvc.perform(post(RECORDS+"/atrecord/T_DEMO/3").session(session).contentType("application/json").accept("application/json").content("{}")).andReturn().getResponse().getContentAsString(),"foreign custom target null");
        equal("",originalMvc.perform(post(RECORDS+"/record-lines/3").session(session).accept("application/json").contentType("application/json").content("{}")).andReturn().getResponse().getContentAsString(),"foreign parent table names suppressed");
        equal(1,JSON.parseArray(originalMvc.perform(post(RECORDS+"/record-lines/"+LARGE_ID).session(session).accept("application/json").contentType("application/json").content("{}")).andReturn().getResponse().getContentAsString()).size(),"own parent table names retained");
        db.execute("CREATE TABLE T_DEMO(ID VARCHAR(20),NAME VARCHAR(100))");
        db.update("INSERT INTO T_DEMO VALUES('1','own'),('3','foreign')");
        equal(1,mapperSession.getMapper(com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordMapper.class)
                .selectTraceOverview(null,"T_DEMO","NAME","org-a").size(),"trace overview Mapper binds organization");
        equal(2,mapperSession.getMapper(com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordMapper.class)
                .selectTraceOverview(null,"T_DEMO","NAME",null).size(),"internal trace Mapper keeps original scope");
        equal(1,query(originalMvc,session,auditBase+"/atrecord/T_DEMO/queries","{}",200).getIntValue("total"),"custom table pagination scoped");
        equal(1,JSON.parseArray(originalMvc.perform(post(RECORDS+"/searchable/T_DEMO/1").session(session).contentType("application/json").accept("application/json").content("{}")).andReturn().getResponse().getContentAsString()).size(),"derived id list uses bound OIN");
        equal("",originalMvc.perform(get(auditBase+"/record-lines/13").session(session).accept("application/json")).andReturn().getResponse().getContentAsString(),"foreign parent line suppressed despite local child org");
        equal("create item",JSON.parseObject(originalMvc.perform(get(auditBase+"/record-lines/11").session(session).accept("application/json")).andReturn().getResponse().getContentAsString()).getJSONObject("ext$").getString("audittrailname"),"own parent permits foreign child org");
        identity.deny=true;
        try { originalMvc.perform(get(auditBase+"/"+LARGE_ID).session(session).accept("application/json")); throw new AssertionError("missing grant accepted detail"); }
        catch(org.springframework.web.util.NestedServletException denied) { if (!(denied.getRootCause() instanceof AccessDeniedException)) throw denied; checks++; }
        for(String path:Arrays.asList(RECORDS,RECORDS+"/raw",RECORDS+"/searchable",RECORDS+"/atrecord/T_DEMO/1")) {
            try { originalMvc.perform(post(path).session(session).contentType("application/json").accept("application/json").content("{}")); throw new AssertionError("missing grant accepted "+path); }
            catch(org.springframework.web.util.NestedServletException denied) {
                if (!(denied.getRootCause() instanceof AccessDeniedException)) throw denied;
                checks++;
            }
        }
        equal(200,originalMvc.perform(get(auditBase+"/info/status").session(session).accept("application/json")).andReturn().getResponse().getStatus(),"original empty status has no audit grant requirement");
        identity.deny=false;
        org.springframework.test.util.ReflectionTestUtils.setField(recordTarget,"auditReadScope",null);
        try { originalMvc.perform(post(RECORDS).session(session).accept("application/json").contentType("application/json").content("{}")); throw new AssertionError("absent provider accepted"); }
        catch(org.springframework.web.util.NestedServletException denied) { if (!(denied.getRootCause() instanceof AccessDeniedException)) throw denied; checks++; }
        org.springframework.test.util.ReflectionTestUtils.setField(recordTarget,"auditReadScope",new com.sunwayworld.emc.framework.security.OriginalAuditTrailReadScope(boundary));
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='target-a' WHERE ID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD SET TARGETID='target-b' WHERE ID=3");
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='target-a' WHERE RECORDID=?",LARGE_ID);
        db.update("UPDATE T_CORE_AT_RECORD_LINE SET TARGETID='target-b' WHERE RECORDID=3");
        String separator=System.getProperty("audit.fixture.separator",",");
        if(separator.trim().isEmpty()) separator=",";
        multiSeqQueries(mvc,session,RECORDS,separator);
        for(String path:Arrays.asList(RECORDS,RECORDS+"/raw",RECORDS+"/searchable")) multiSeqQueries(originalMvc,session,path,separator);
        db.update("UPDATE T_CORE_AT_RECORD SET AUDITTRAILNAME=?,\"COMMENT\"=NULL WHERE ID=2","update'_% item");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"auditTrailName_SEQ\":\"update'_% item"+separator+"ABSENT\"}}}",200).getIntValue("total"),"quote and wildcard characters bind as equality literals");
        equal(0,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"auditTrailName_SEQ\":\"' OR 1=1 --"+separator+"ABSENT\"}}}",200).getIntValue("total"),"SQL-looking value remains bound data");
        equal(0,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"auditTrailName_SEQ\":\"update%"+separator+"ABSENT\"}}}",200).getIntValue("total"),"SEQ does not become LIKE");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"comment_SEQ\":\"VOID\"}}}",200).getIntValue("total"),"single VOID retains IS NULL semantics");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"comment_SEQ\":\"\"}}}",200).getIntValue("total"),"single empty value retains IS NULL semantics");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"comment_SEQ\":\"note"+separator+"VOID\"}}}",200).getIntValue("total"),"VOID within a list stays a literal");
        db.update("UPDATE T_CORE_AT_RECORD SET AUDITTRAILNAME='update item',\"COMMENT\"='note' WHERE ID=2");
        equal(2,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"auditTrailName_SC\":\"create"+separator+"update\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"configured/default multi value string filter");
        JSONObject result=query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"INSERT\"},\"n\":1,\"s\":20}}",200);
        equal(1,result.getIntValue("total"),"original audit type filter");
        equal("INSERT",result.getJSONArray("rows").getJSONObject(0).getString("auditTrailType"),"original typed camelCase field");
        equal(false,result.getJSONArray("rows").getJSONObject(0).containsKey("audittrailtype"),"no lowercase compound alias");
        equal("create item",result.getJSONArray("rows").getJSONObject(0).getString("auditTrailName"),"typed audit name");
        equal("target-a",result.getJSONArray("rows").getJSONObject(0).getString("targetId"),"typed target");
        equal(true,result.getJSONArray("rows").getJSONObject(0).containsKey("createdTime"),"typed time");
        equal(0,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"auditTrailType_SEQ\":\"NO-MATCH\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"no matching audit type");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"qf\":{\"auditTrailName_SEQ\":\"update item\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"qf name filter");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"af\":[{\"children\":[{\"name\":\"AUDITTRAILTYPE\",\"match\":\"SEQ\",\"value\":\"UPDATE\"}]}],\"n\":1,\"s\":20}}",200).getIntValue("total"),"advanced filter");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"mf\":{\"name\":\"AUDITTRAILNAME,TARGETID\",\"value\":\"update\",\"caseSensitive\":true},\"n\":1,\"s\":20}}",200).getIntValue("total"),"multi property filter");
        equal(1,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"targetId_SEQ\":\"target-a\",\"createdTime_TGOE\":\"2026-01-02 00:00:00\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"target and time criteria");
        result=query(mvc,session,RECORDS,"{\"p\":{\"o\":[{\"ID\":\"asc\"}],\"n\":2,\"s\":1}}",200);
        equal(2,result.getIntValue("total"),"page count scoped exact org");
        equal(LARGE_ID,result.getJSONArray("rows").getJSONObject(0).getLongValue("id"),"explicit ascending second page");
        equal(2,result.getIntValue("pageNumber"),"original page number");
        equal(2,result.getIntValue("totalPages"),"original total pages");
        result=query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"createdByOrgId_SEQ\":\"org-b\",\"loginOrgId\":\"org-b\",\"ownedAuditOrgId\":\"org-b\"},\"n\":1,\"s\":20}}",200);
        equal(0,result.getIntValue("total"),"caller org filter AND server org");
        equal(2,query(mvc,session,RECORDS,"{\"p\":{\"f\":{\"loginOrgId\":\"org-b\",\"ownedAuditOrgId\":\"org-b\"},\"ownedAuditOrgId\":\"org-b\",\"n\":1,\"s\":20}}",200).getIntValue("total"),"spoofed scope cannot broaden records");
        result=query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":2030254965053728},\"o\":[{\"ID\":\"asc\"}],\"n\":1,\"s\":20}}",200);
        equal(2,result.getIntValue("total"),"parent org permits child from different org");
        equal(fullText,result.getJSONArray("rows").getJSONObject(0).getString("newValue"),"full CLOB value preserved");
        equal("INSERT",result.getJSONArray("rows").getJSONObject(0).getJSONObject("ext$").getString("audittrailtype"),"original parent projection extension preserved");
        equal(false,result.toJSONString().contains("ownedAuditOrg"),"scope parameter never projected");
        equal(0,query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":2030254965053728,\"column_SEQ\":\"NO-MATCH\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"line column no match");
        equal(1,query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":2030254965053728,\"targetId_SEQ\":\"target-a\",\"column_SEQ\":\"FIELD_A\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"line original target and column filters");
        equal(1,query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":2030254965053728},\"qf\":{\"auditTrailName_SEQ\":\"create item\"},\"n\":2,\"s\":1}}",200).getJSONArray("rows").size(),"line parent metadata filter and second page");
        equal(2,query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":\"2030254965053728\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"large string filtered record ID");
        equal(2,query(mvc,session,LINES,"{\"p\":{\"qf\":{\"recordId_SEQ\":\"2030254965053728\"},\"n\":1,\"s\":20}}",200).getIntValue("total"),"large string qf record ID");
        result=query(mvc,session,LINES,"{\"p\":{\"recordId_SEQ\":2030254965053728,\"n\":1,\"s\":20}}",200);
        equal(2,result.getIntValue("total"),"legacy direct recordId accepted");
        result=query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":3,\"ownedAuditOrgId\":\"org-b\",\"loginOrgId\":\"org-b\"},\"ownedAuditOrgId\":\"org-b\",\"n\":1,\"s\":20}}",200);
        equal(0,result.getIntValue("total"),"same root foreign parent excluded despite child org and spoofed special scope");
        equal(0,query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":2030254965053728},\"qf\":{\"recordId_SEQ\":3},\"n\":1,\"s\":20}}",200).getIntValue("total"),"conflicting record filters remain AND");
        query(mvc,session,LINES,"{}",400);
        query(mvc,session,LINES,"{\"p\":{\"f\":{\"recordId_SEQ\":\"bad\"}}}",400);
        targetLineQueries(mvc,session,db,fullText,separator);
        for(String path:Arrays.asList(RECORDS,LINES)) {
            query(mvc,new MockHttpSession(),path,"{}",403);
            identity.deny=true; query(mvc,session,path,"{}",403); identity.deny=false;
            identity.stale=true; query(mvc,session,path,"{}",403); identity.stale=false;
            // A stale identity invalidates the session principal; establish a fresh one for the next case.
            boundary.login(session,new OwnedLoginRequest("user-a","fixture".toCharArray()));
            identity.fail=true; query(mvc,session,path,"{}",500); identity.fail=false;
            boundary.login(session,new OwnedLoginRequest("user-a","fixture".toCharArray()));
        }
        query(mvc,session,"/secure/emc/module/sys/at-records/queries","{}",200);
        query(mvc,session,"/secure/emc/module/sys/at-records/record-lines/queries","{\"p\":{\"f\":{\"recordId_SEQ\":2030254965053728}}}",200);
        identity.deny=true;
        for(String path:Arrays.asList("/secure/emc/module/bp/audit-records/info/status","/secure/emc/module/bp/audit-records/1/info/status")) {
            equal(200,mvc.perform(get(path).session(session).accept("application/json")).andReturn().getResponse().getStatus(),"status authentication only");
        }
        for(String org:Arrays.asList(null,"","  ")) {
            try { operations.records(new com.sunwayworld.framework.restful.data.RestJsonWrapperBean(),org); throw new AssertionError("blank trusted org accepted"); }
            catch(IllegalArgumentException expected) { checks++; }
            try { operations.lines(new com.sunwayworld.framework.restful.data.RestJsonWrapperBean(),org); throw new AssertionError("blank trusted line org accepted"); }
            catch(IllegalArgumentException expected) { checks++; }
        }
        com.sunwayworld.framework.mybatis.mapper.MapperParameter mapperInput=new com.sunwayworld.framework.mybatis.mapper.MapperParameter();
        mapperInput.put("ownedAuditOrgId",null);
        equal(0,mapperSession.getMapper(com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordLineMapper.class).selectByCondition(mapperInput).size(),"present null Mapper scope denies");
        mapperInput.put("ownedAuditOrgId","");
        equal(0,mapperSession.getMapper(com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordLineMapper.class).selectByCondition(mapperInput).size(),"present blank Mapper scope denies");
        mapperInput.remove("ownedAuditOrgId");
        equal(3,mapperSession.getMapper(com.sunwayworld.module.sys.at.mapper.CoreAuditTrailRecordLineMapper.class).selectByCondition(mapperInput).size(),"unrelated original Mapper caller remains unchanged");
        identity.deny=false;
        controller.setReadOperations(null);
        query(mvc,session,RECORDS,"{}",500);
        System.out.println("audit query MVC/JDBC regression PASS ("+checks+" checks)");
    }
}
