package com.sunwayworld.framework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.web.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.sql.DataSource;

/** Actual original personnel HTTP routes, real RSA session and disposable Oracle. */
public final class OwnedPersonnelDataHttpContractTest {
    private static int checks;
    private static final String BASE = "/secure/emc/module/mdm/emp/emps";
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        java.nio.file.Path base = java.nio.file.Files.createTempDirectory("owned-personnel-http-");
        java.nio.file.Path audit = base.resolve("audit.jsonl");
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-personnel-test",
                "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
                "--spring.datasource.url="+properties.getProperty("url"), "--spring.datasource.username="+properties.getProperty("user"),
                "--spring.datasource.password="+properties.getProperty("password"), "--sunway.user.password.encoder.type=MD5",
                "--owned.identity.audit.type=file", "--owned.identity.audit.path="+audit.toAbsolutePath()});
        org.springframework.context.ConfigurableApplicationContext app = com.sunwayworld.owned.internal.context.OwnedApplicationContext.getApplicationContext();
        DataSource ds=app.getBean(DataSource.class);
        int port=((org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext)app).getWebServer().getPort();
        try {
            seedPersonnelMaster(ds);
            check(app.getBeansOfType(com.sunwayworld.emc.module.mdm.emp.emp.controller.EmcEmpController.class).size()==1,"original personnel page controller is packaged and scanned");
            check(app.getBeansOfType(DataSource.class).size()==1,"Boot creates one datasource");
            check(app.getBeansOfType(com.sunwayworld.emc.module.mdm.emp.emp.resource.impl.EmcEmpResourceImpl.class).size()==1,"one unchanged original personnel resource");
            check(app.getBeansOfType(com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService.class).size()==1,"one original personnel service contract implementation");
            org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter mvc=app.getBean(org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.class);
            int resolvers=0,converters=0;
            for(org.springframework.web.method.support.HandlerMethodArgumentResolver r:mvc.getArgumentResolvers()) if(r instanceof OwnedReadWebConfiguration.WrapperArgumentResolver)resolvers++;
            for(org.springframework.http.converter.HttpMessageConverter<?> c:mvc.getMessageConverters()) if(c instanceof OwnedReadWebConfiguration.JsonMessageConverter)converters++;
            check(resolvers==1 && converters==1,"shared read transport is registered once");
            check(http(port,"POST",BASE+"/queries/raw","{}",null,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"anonymous read is denied");
            String key = http(port,"GET","/open/security/public-key",null,null,null,200).body;
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key))));
            String encrypted=URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))),"UTF-8");
            String cookie=http(port,"POST","/core/module/sys/login","id=e-1&password="+encrypted,null,key,200).cookie;
            check(cookie!=null,"RSA login creates session");
            JSONObject page=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",cookie,null,200).body);
            check(page.getIntValue("total")==2 && page.getJSONArray("rows").size()==1,"original raw query returns scoped Page");
            JSONObject chooser=JSON.parseObject(http(port,"POST",BASE+"/queries/choosable","{\"p\":{\"n\":1,\"s\":10}}",cookie,null,200).body);
            check(chooser.getIntValue("total")==2 && chooser.getJSONArray("rows").size()==2,"base personnel chooser returns scoped employees");
            JSONObject row=page.getJSONArray("rows").getJSONObject(0);
            check("e-1".equals(row.getString("id")) && "Alice".equals(row.getString("userName")) && row.getIntValue("age")==30,"actual personnel DTO names and numeric conversion");
            check("org-a".equals(row.getJSONObject("ext$").getString("orgid")) && "extension-a".equals(row.getJSONObject("ext$").getString("customfield")),"extension map keeps joined and custom fields");
            JSONObject second=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"n\":2,\"s\":1}",cookie,null,200).body);
            check("e-2".equals(second.getJSONArray("rows").getJSONObject(0).getString("id")),"direct UI envelope selects second page");
            JSONObject hints=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"f\":{\"userOrg_filter\":\"org-b\",\"orgid_SEQ\":\"org-b\"},\"n\":1,\"s\":100}",cookie,null,200).body);
            check(hints.getIntValue("total")==0,"forged UI organization hint cannot widen server scope");
            JSONObject detail=JSON.parseObject(http(port,"GET",BASE+"/e-2",null,cookie,null,200).body);
            check("Alicia".equals(detail.getString("userName")) && "Fixture Org B".equals(detail.getJSONObject("ext$").getString("departmentname")),"original detail returns DTO projection");
            String outScope=http(port,"GET",BASE+"/e-3",null,cookie,null,200).body.trim();
            check(outScope.isEmpty() || "null".equals(outScope),"out-of-scope detail is suppressed");
            JSONObject filtered=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1,\"f\":{\"USERNAME_CISC\":\"ali\"}}}",cookie,null,200).body);
            check(filtered.getIntValue("total")==2 && filtered.getJSONArray("rows").size()==1,"original raw query keeps filter and page semantics");
            JSONObject empty=JSON.parseObject(http(port,"POST",BASE+"/queries","{\"p\":{\"f\":{\"USERNAME_EQ\":\"nobody\"}}}",cookie,null,200).body);
            check(empty.getIntValue("total")==0 && empty.getJSONArray("rows").isEmpty(),"original query returns no-match page");
            seedAuxiliary(ds);
            JSONObject reportChoosers=JSON.parseObject(http(port,"POST",BASE+"/report-users/queries/choosable","{\"f\":{\"USERNAME_CISC\":\"ali\"}}",cookie,null,200).body);
            check(reportChoosers.getIntValue("total")==2,"report-user chooser keeps scoped personnel pagination");
            JSONObject reportAlice=findRow(reportChoosers,"e-1");
            JSONObject reportAlicia=findRow(reportChoosers,"e-2");
            check(reportAlice!=null && "2".equals(reportAlice.getJSONObject("ext$").getString("unfinishedprojectnum")),"report-user chooser counts unfinished projects with completed/cancelled exclusions");
            check(reportAlicia!=null && "0".equals(reportAlicia.getJSONObject("ext$").getString("unfinishedprojectnum")),"report-user chooser returns zero for an employee without unfinished projects");
            JSONObject sampleChoosers=JSON.parseObject(http(port,"POST",BASE+"/sample/queries/choosable","{\"f\":{\"planSampleDate\":\"2026-10-27\"}}",cookie,null,200).body);
            JSONObject sampleAlice=findRow(sampleChoosers,"e-1");
            check(sampleChoosers.getIntValue("total")==2 && sampleAlice!=null,"sample chooser keeps scoped personnel page for a requested date");
            check("P-701".equals(sampleAlice.getJSONObject("ext$").getString("projno"))
                    && "Project 701".equals(sampleAlice.getJSONObject("ext$").getString("projname"))
                    && java.time.LocalDate.now().toString().equals(sampleAlice.getJSONObject("ext$").getString("plansampledate")),"sample chooser enriches CSV assignees with same-organization project metadata");
            JSONObject sampleNone=JSON.parseObject(http(port,"POST",BASE+"/sample/queries/choosable","{\"f\":{\"planSampleDate\":\"2026-11-01\"}}",cookie,null,200).body);
            check(findRow(sampleNone,"e-1").getJSONObject("ext$").getString("projno")==null,"sample chooser leaves no-match date un-enriched");
            JSONObject training=JSON.parseObject(http(port,"POST",BASE+"/e-1/trains/queries","{}",cookie,null,200).body);
            check(training.getIntValue("total")==1 && "Safety refresher".equals(training.getJSONArray("rows").getJSONObject(0).getString("trainContent")),"training query returns the in-scope employee record");
            JSONObject hiddenTraining=JSON.parseObject(http(port,"POST",BASE+"/e-3/trains/queries","{}",cookie,null,200).body);
            check(hiddenTraining.getIntValue("total")==0 && hiddenTraining.getJSONArray("rows").isEmpty(),"training query suppresses a cross-organization employee");
            JSONObject overdue=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"overdueFilter\":\"1\"}}",cookie,null,200).body);
            check(overdue.getIntValue("total")==1 && overdue.getJSONArray("rows").getJSONObject(0).getString("id").equals("e-1"),"qualification-expiry checkbox filters overdue employees");
            JSONObject historicalOverdue=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"remind\":\"overdue\"}}",cookie,null,200).body);
            check(historicalOverdue.getIntValue("total")==1 && historicalOverdue.getJSONArray("rows").getJSONObject(0).getString("id").equals("e-2"),"historical certificate overdue parameter filters employees");
            JSONObject qualifications=JSON.parseObject(http(port,"POST",BASE+"/e-1/quals/queries","{\"n\":1,\"s\":1}",cookie,null,200).body);
            check(qualifications.getIntValue("total")==2 && qualifications.getJSONArray("rows").size()==1,"qualification tab has original ID-desc paging");
            JSONObject qualification=qualifications.getJSONArray("rows").getJSONObject(0);
            check(qualification.getLongValue("id")==102 && "Safety permit".equals(qualification.getString("qualName"))
                    && "2026-01-01".equals(qualification.getString("authDate"))
                    && "safety.pdf".equals(qualification.getJSONObject("ext$").getString("filename")),"qualification DTO and joined file metadata");
            JSONObject tests=JSON.parseObject(http(port,"POST",BASE+"/e-1/tests/queries","{}",cookie,null,200).body);
            check(tests.getIntValue("total")==1 && "Water metals".equals(tests.getJSONArray("rows").getJSONObject(0).getJSONObject("ext$").getString("testname"))
                    && "Water".equals(tests.getJSONArray("rows").getJSONObject(0).getJSONObject("ext$").getString("testcategory")),"visible test joins the test method, category and test");
            JSONObject credentials=JSON.parseObject(http(port,"POST",BASE+"/e-1/credentials/queries","{}",cookie,null,200).body);
            check(credentials.getIntValue("total")==1 && "Analyst certificate".equals(credentials.getJSONArray("rows").getJSONObject(0).getString("certName")),"credential tab returns original credential DTO");
            JSONObject methods=JSON.parseObject(http(port,"POST",BASE+"/301/methods/queries","{}",cookie,null,200).body);
            JSONObject method=methods.getJSONArray("rows").getJSONObject(0);
            check(methods.getIntValue("total")==1 && "ICP-MS".equals(method.getJSONObject("ext$").getString("methodname"))
                    && "Water metals".equals(method.getJSONObject("ext$").getString("testname"))
                    && "analysis".equals(method.getString("type")),"credential methods retain method, test and category projection");
            for(String path:new String[]{"/e-3/quals/queries","/e-3/tests/queries","/e-3/credentials/queries","/302/methods/queries","/999/methods/queries"}) {
                JSONObject hidden=JSON.parseObject(http(port,"POST",BASE+path,"{}",cookie,null,200).body);
                check(hidden.getIntValue("total")==0 && hidden.getJSONArray("rows").isEmpty(),"auxiliary rows inherit employee organization scope: "+path);
            }
            JSONObject selectedQualification=JSON.parseObject(http(port,"POST",BASE+"/e-1/quals/queries","{\"f\":{\"QUALNAME_CISC\":\"chem\"}}",cookie,null,200).body);
            check(selectedQualification.getIntValue("total")==1 && selectedQualification.getJSONArray("rows").getJSONObject(0).getLongValue("id")==101,"auxiliary standard text filter narrows rows");
            JSONObject forgedEmployee=JSON.parseObject(http(port,"POST",BASE+"/e-1/quals/queries","{\"f\":{\"EMPID_EQ\":\"e-3\"}}",cookie,null,200).body);
            check(forgedEmployee.getIntValue("total")==0,"child filter cannot replace route employee ID");
            http(port,"POST",BASE+"/e-1/quals/queries","{\"f\":{\"targetTable\":\"T_CORE_USER\"}}",cookie,null,400);
            check(http(port,"POST",BASE+"/e-1/quals","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"query guard rejects unsupported write");
            check(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"unknown\":\"x\"}}",cookie,null,400).body.contains("GIKAM.PERSONNEL.INVALID_REQUEST"),"unknown query input fails explicitly");
            for(String invalid: new String[]{"{\"unknown\":1}","{\"p\":{},\"n\":2}","{\"p\":{\"loginUserId\":\"e-3\"}}","null","[]","{"}) {
                check(http(port,"POST",BASE+"/queries/raw",invalid,cookie,null,400).body.contains("GIKAM.PERSONNEL.INVALID_REQUEST"),"malformed or forged input is rejected");
            }
            JSONObject queryPage=JSON.parseObject(http(port,"POST",BASE+"/queries/raw?n=2&s=1","{}",cookie,null,200).body);
            check("e-2".equals(queryPage.getJSONArray("rows").getJSONObject(0).getString("id")),"query string pagination is parsed");
            http(port,"POST",BASE+"/queries/raw?n=2","{\"n\":1}",cookie,null,400);
            http(port,"POST",BASE+"/queries/raw?unknown=x","{}",cookie,null,400);
            http(port,"POST",BASE+"/queries/raw?n=1&n=2","{}",cookie,null,400);
            check(http(port,"POST",BASE,"{}",cookie,null,403).body.contains("UNAUTHORIZED"),"generic insert is denied by query guard");
            http(port,"DELETE",BASE,"{}",cookie,null,403);
            http(port,"PUT",BASE+"/instant","{}",cookie,null,403);
            http(port,"PUT",BASE+"/e-1","{}",cookie,null,403);
            String lowCookie=http(port,"POST","/core/module/sys/login","id=other&password="+encrypted,null,key,200).cookie;
            check(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",lowCookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"low-role user is denied");
            http(port,"POST",BASE+"/e-1/credentials/queries","{}",lowCookie,null,403);
            try (Connection connection=ds.getConnection(); Statement statement=connection.createStatement()) {
                statement.executeUpdate("DELETE FROM T_CORE_USER_PERMISSION WHERE USERID='e-1'");
            }
            JSONObject revoked=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{}",cookie,null,200).body);
            check(revoked.getIntValue("total")==0,"current organization grant revocation suppresses rows");
            JSONObject revokedMethods=JSON.parseObject(http(port,"POST",BASE+"/301/methods/queries","{}",cookie,null,200).body);
            check(revokedMethods.getIntValue("total")==0,"organization revocation suppresses credential methods in current session");
            try (Connection connection=ds.getConnection(); Statement statement=connection.createStatement()) {
                statement.executeUpdate("DELETE FROM T_CORE_ROLE_USER WHERE USERID='e-1'");
            }
            check(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"revoked menu grant is rechecked");
            String records=new String(java.nio.file.Files.readAllBytes(audit),StandardCharsets.UTF_8);
            check(records.contains("\"action\":\"authorize\"") && records.contains("\"outcome\":\"DENIED\"") && records.contains("unauthenticated"),"allow, anonymous and permission denials are audited");
            check(!records.contains("secret") && !records.contains(properties.getProperty("password")),"audit omits credentials");
        } finally {
            app.close();
            java.nio.file.Files.walk(base).sorted(java.util.Comparator.reverseOrder()).forEach(path -> { try { java.nio.file.Files.deleteIfExists(path); } catch(IOException e) { throw new java.io.UncheckedIOException(e); } });
        }
        System.out.println("personnel data HTTP: PASS ("+checks+" checks; Boot original application, loopback HTTP and synthetic Oracle only)");
    }
    private static void seedPersonnelMaster(DataSource ds) throws SQLException {
        try (Connection c=ds.getConnection(); Statement s=c.createStatement()) {
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("CREATE TABLE T_EMC_EMP (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), BIRTHDAY DATE, AGE NUMBER, ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), MOBILE VARCHAR2(100), SEX VARCHAR2(10), FILTERTEST VARCHAR2(10), REPORTAUDIT VARCHAR2(10), REPORTISSUE VARCHAR2(10), PHONE VARCHAR2(100), NATION VARCHAR2(100), STARTWORKDATE DATE, TITLEDATE DATE, PROFESSIONALTITLE VARCHAR2(100), POSITION VARCHAR2(100), REMARK VARCHAR2(500), ELECTRONICSIGNATURE VARCHAR2(255), PIC VARCHAR2(255), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), CUSTOMFIELD VARCHAR2(100), STATION VARCHAR2(100))");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,ORGID,ORGNAME,AGE,MOBILE,FILTERTEST,REPORTAUDIT,REPORTISSUE,CUSTOMFIELD,STATION,CREATEDTIME) VALUES ('e-1','Alice','org-a','Fixture Org A',30,'123','1','0','1','extension-a','Analyst',SYSTIMESTAMP)");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,ORGID,ORGNAME,AGE,MOBILE,REPORTAUDIT,REPORTISSUE,CUSTOMFIELD,CREATEDTIME) VALUES ('e-2','Alicia','org-a','Fixture Org A',40,'456','1','0','extension-b',SYSTIMESTAMP-1)");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,ORGID,ORGNAME,CREATEDTIME) VALUES ('e-3','Bob','org-b','Fixture Org B',SYSTIMESTAMP)");
        }
    }
    private static void seedAuxiliary(DataSource ds) throws SQLException {
        try(Connection c=ds.getConnection();Statement s=c.createStatement()) {
            // The runner executes the framework query contract in this same
            // disposable schema first; replace its minimal overdue fixture
            // with the full auxiliary projection used by these HTTP checks.
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP_QUAL'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_CERT'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("CREATE TABLE T_EMC_EMP_QUAL (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), QUALNO VARCHAR2(32), QUALNAME VARCHAR2(64), QUALORG VARCHAR2(64), VALIDDATE DATE, AUTHDATE DATE, EXPIREDATE DATE, WARNINGDAYS NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(64), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_CORE_FILE (ID NUMBER PRIMARY KEY, NAME VARCHAR2(64), TARGETID VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_EMP_TEST (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), TESTID NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(64), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_TEST_METHOD (ID NUMBER PRIMARY KEY, TESTID NUMBER, TESTCATEGORYID NUMBER, TESTCLASS VARCHAR2(32))");
            s.execute("CREATE TABLE T_EMC_TEST_CATEGORY (ID NUMBER PRIMARY KEY, TESTCATEGORYDESC VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_TEST (ID NUMBER PRIMARY KEY, TESTNAME VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_CERT (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), CERTNO VARCHAR2(32), CERTNAME VARCHAR2(64), AUTHDATE DATE, EXPIREDATE DATE, WARNINGDAYS NUMBER, STATUS VARCHAR2(32), ORDERNO NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(64), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_CERT_TEST_METHOD (ID NUMBER PRIMARY KEY, CERTID NUMBER, TESTID NUMBER, METHODID NUMBER, REMARK VARCHAR2(100), TESTMETHODID NUMBER, ITEMGROUP VARCHAR2(64), TYPE VARCHAR2(32), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(64), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_METHOD (ID NUMBER PRIMARY KEY, METHODNAME VARCHAR2(64), TESTMETHODBASIS VARCHAR2(100))");
            s.execute("INSERT INTO T_EMC_EMP_QUAL (ID,EMPID,QUALNO,QUALNAME,QUALORG,AUTHDATE,EXPIREDATE,WARNINGDAYS) VALUES (101,'e-1','Q-101','Chemistry permit','Fixture issuer',DATE '2026-01-01',SYSDATE-2,0)");
            s.execute("INSERT INTO T_EMC_EMP_QUAL (ID,EMPID,QUALNO,QUALNAME,QUALORG,AUTHDATE,EXPIREDATE,WARNINGDAYS) VALUES (102,'e-1','Q-102','Safety permit','Fixture issuer',DATE '2026-01-01',DATE '2027-01-01',30)");
            s.execute("INSERT INTO T_EMC_EMP_QUAL (ID,EMPID,QUALNAME) VALUES (103,'e-3','Hidden permit')");
            s.execute("INSERT INTO T_CORE_FILE VALUES (901,'safety.pdf','T_EMC_EMP_QUAL$102')");
            s.execute("INSERT INTO T_EMC_EMP_TEST (ID,EMPID,TESTID) VALUES (201,'e-1',501)");
            s.execute("INSERT INTO T_EMC_EMP_TEST (ID,EMPID,TESTID) VALUES (202,'e-3',501)");
            s.execute("INSERT INTO T_EMC_TEST_METHOD VALUES (501,601,701,'metals')");
            s.execute("INSERT INTO T_EMC_TEST_CATEGORY VALUES (701,'Water')");
            s.execute("INSERT INTO T_EMC_TEST VALUES (601,'Water metals')");
            s.execute("INSERT INTO T_EMC_CERT (ID,EMPID,CERTNO,CERTNAME,AUTHDATE,EXPIREDATE,WARNINGDAYS) VALUES (301,'e-1','C-301','Analyst certificate',DATE '2026-01-01',SYSDATE+30,0)");
            s.execute("INSERT INTO T_EMC_CERT (ID,EMPID,CERTNO,CERTNAME,EXPIREDATE,WARNINGDAYS) VALUES (303,'e-2','C-303','Reviewer certificate',SYSDATE-2,0)");
            s.execute("INSERT INTO T_EMC_CERT (ID,EMPID,CERTNO,CERTNAME) VALUES (302,'e-3','C-302','Hidden certificate')");
            s.execute("INSERT INTO T_EMC_CERT_TEST_METHOD (ID,CERTID,TESTID,METHODID,TESTMETHODID,TYPE,REMARK) VALUES (401,301,601,801,501,'analysis','Certified')");
            s.execute("INSERT INTO T_EMC_CERT_TEST_METHOD (ID,CERTID,TESTID,METHODID,TESTMETHODID,TYPE) VALUES (402,302,601,801,501,'analysis')");
            s.execute("INSERT INTO T_EMC_METHOD VALUES (801,'ICP-MS','Fixture standard')");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_FOLDER'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_REPORT'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_PROJECT'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("CREATE TABLE T_EMC_PROJECT (ID NUMBER PRIMARY KEY, REPORTEDITUSERID VARCHAR2(64), STATUS VARCHAR2(32), ORGID VARCHAR2(64), PROJNO VARCHAR2(64), PROJNAME VARCHAR2(128))");
            s.execute("CREATE TABLE T_EMC_REPORT (ID NUMBER PRIMARY KEY, PROJID NUMBER, STATUS VARCHAR2(32))");
            s.execute("CREATE TABLE T_EMC_FOLDER (ID NUMBER PRIMARY KEY, PROJID NUMBER, PLANSAMPLEDATE DATE, PLANSAMPLEUSERID VARCHAR2(256))");
            s.execute("CREATE TABLE T_EMC_EMP_TRAIN (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), TRAINTIME DATE, TRAINCONTENT VARCHAR2(200), TRAINRESULT VARCHAR2(100), COURSECODE VARCHAR2(64), COURSENAME VARCHAR2(128), TRAINDEPT VARCHAR2(128), TRAINDEPTCODE VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), TRAINBEGINTIME DATE, TRAINENDTIME DATE)");
            s.execute("INSERT INTO T_EMC_PROJECT VALUES (701,'e-1','reportEdit','org-a','P-701','Project 701')");
            s.execute("INSERT INTO T_EMC_PROJECT VALUES (702,'e-1','reportEdit','org-a','P-702','Project 702')");
            s.execute("INSERT INTO T_EMC_PROJECT VALUES (703,'e-1','create','org-a','P-703','Project 703')");
            s.execute("INSERT INTO T_EMC_PROJECT VALUES (704,'e-3','reportEdit','org-b','P-704','Project 704')");
            s.execute("INSERT INTO T_EMC_PROJECT VALUES (705,'e-1','cancel','org-a','P-705','Cancelled 705')");
            s.execute("INSERT INTO T_EMC_REPORT VALUES (901,701,'draft')");
            s.execute("INSERT INTO T_EMC_REPORT VALUES (902,702,'issued')");
            s.execute("INSERT INTO T_EMC_REPORT VALUES (904,704,'issued')");
            s.execute("INSERT INTO T_EMC_FOLDER VALUES (801,701,DATE '2026-10-27','e-1,e-3')");
            s.execute("INSERT INTO T_EMC_FOLDER VALUES (802,704,DATE '2026-10-27','e-3')");
            s.execute("INSERT INTO T_EMC_FOLDER VALUES (803,701,DATE '2026-10-26','e-1')");
            s.execute("INSERT INTO T_EMC_EMP_TRAIN (ID,EMPID,TRAINTIME,TRAINCONTENT,TRAINRESULT,COURSECODE,COURSENAME) VALUES (601,'e-1',DATE '2026-10-01','Safety refresher','passed','SAFE-1','Safety refresher')");
            s.execute("INSERT INTO T_EMC_EMP_TRAIN (ID,EMPID,TRAINTIME,TRAINCONTENT,TRAINRESULT) VALUES (602,'e-3',DATE '2026-10-01','Hidden training','passed')");
        }
    }
    private static JSONObject findRow(JSONObject page, String id) {
        if (page == null || page.getJSONArray("rows") == null) return null;
        for (Object value : page.getJSONArray("rows")) {
            JSONObject row = (JSONObject) value;
            if (id.equals(row.getString("id"))) return row;
        }
        return null;
    }
    private static Response http(int port,String method,String path,String body,String cookie,String key,int expected) throws Exception {
        HttpURLConnection c=(HttpURLConnection)new URL("http://127.0.0.1:"+port+path).openConnection();
        c.setConnectTimeout(5000); c.setReadTimeout(10000);
        c.setRequestMethod(method); c.setInstanceFollowRedirects(false);
        if(cookie!=null)c.setRequestProperty("Cookie",cookie);
        if(key!=null)c.setRequestProperty("SECURITY-RSA-KEY",key);
        if(body!=null){c.setDoOutput(true); c.setRequestProperty("Content-Type",key!=null?"application/x-www-form-urlencoded":"application/json");
            byte[] bytes=body.getBytes(StandardCharsets.UTF_8); c.setFixedLengthStreamingMode(bytes.length);try(OutputStream o=c.getOutputStream()){o.write(bytes);}}
        int status=c.getResponseCode(); check(status==expected,path+" returned HTTP "+status+", expected "+expected);
        ByteArrayOutputStream bytes=new ByteArrayOutputStream(); InputStream stream=status>=400?c.getErrorStream():c.getInputStream();
        if(stream!=null)try(InputStream input=stream){byte[] b=new byte[1024];int n;while((n=input.read(b))!=-1)bytes.write(b,0,n);}
        String set=c.getHeaderField("Set-Cookie"); c.disconnect();
        return new Response(new String(bytes.toByteArray(),StandardCharsets.UTF_8),set==null?cookie:set.split(";",2)[0]);
    }
    private static final class Response {final String body,cookie;Response(String b,String c){body=b;cookie=c;}}
}
