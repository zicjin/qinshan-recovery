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
import oracle.jdbc.pool.OracleDataSource;

/** Actual original consumables HTTP routes, real RSA session and disposable Oracle. */
public final class OwnedConsumablesRuntimeHttpTest {
    private static int checks;
    private static final String BASE = "/secure/emc/module/item/consume/consumes";
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        OracleDataSource fixtureDataSource = new OracleDataSource();
        fixtureDataSource.setURL(properties.getProperty("url")); fixtureDataSource.setUser(properties.getProperty("user")); fixtureDataSource.setPassword(properties.getProperty("password"));
        try (Connection connection = fixtureDataSource.getConnection(); Statement sql = connection.createStatement()) {
            sql.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP)");
            sql.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, LASTSUSPENDEDFLAG VARCHAR2(1))");
            sql.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            sql.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            sql.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
            sql.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY, ORGNAME VARCHAR2(100))");
            sql.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            sql.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            sql.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            sql.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            sql.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            sql.execute("INSERT INTO T_CORE_MENU VALUES ('430201')");
            sql.execute("INSERT INTO T_CORE_MENU VALUES ('20250800003')");
            sql.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_CORE_ORG VALUES ('org-b','Fixture Org B')");
            sql.execute("INSERT INTO T_CORE_USER VALUES ('allowed','Allowed User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL)");
            sql.execute("INSERT INTO T_CORE_USER VALUES ('low','Low User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL)");
            sql.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')");
            sql.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            sql.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','430201')");
            sql.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
            sql.execute("CREATE TABLE T_EMC_CONSUME (ID VARCHAR2(64) PRIMARY KEY, MTLNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), STOCKQTY NUMBER, MTLCATEGORYID VARCHAR2(64), HARMFULNESS VARCHAR2(36), PRODUCTNO VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            sql.execute("INSERT INTO T_EMC_CONSUME VALUES ('CSM-001','Alpha Bottle','100 ml',7,'glass','normal','P-001','allowed','Allowed User',TIMESTAMP '2025-01-01 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CONSUME VALUES ('CSM-002','Hidden Solvent','500 ml',3,'chemical','harmful','P-002','allowed','Allowed User',TIMESTAMP '2025-01-02 09:00:00','org-b','Fixture Org B')");
            sql.execute("INSERT INTO T_EMC_CONSUME VALUES ('CSM-003','Beta Bottle','250 ml',0,'glass','normal','P-003','allowed','Allowed User',TIMESTAMP '2025-01-03 09:00:00','org-a','Fixture Org A')");
            sql.execute("CREATE TABLE T_EMC_CSM_STOCK (ID NUMBER PRIMARY KEY, MTLID VARCHAR2(64), BATCHNO VARCHAR2(64), STOCKQTY NUMBER, UNITNAME VARCHAR2(64), EXPIREDATE TIMESTAMP, LOWLIMIT NUMBER, STATUS VARCHAR2(4), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK VALUES (11,'CSM-001','A-1',5,'bottle',TIMESTAMP '2026-01-01 00:00:00',1,'0','allowed','Allowed User',TIMESTAMP '2025-01-04 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK VALUES (12,'CSM-001','A-2',0,'bottle',TIMESTAMP '2026-02-01 00:00:00',1,'1','allowed','Allowed User',TIMESTAMP '2025-01-05 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK VALUES (13,'CSM-002','H-1',8,'litre',TIMESTAMP '2026-03-01 00:00:00',1,'0','allowed','Allowed User',TIMESTAMP '2025-01-06 09:00:00','org-b','Fixture Org B')");
            sql.execute("CREATE TABLE T_EMC_CSM_STOCK_LOG (ID NUMBER PRIMARY KEY, STOCKID NUMBER, LOGTYPE VARCHAR2(36), PREVIOUSQTY NUMBER, QTY NUMBER, VENDORID VARCHAR2(64), PRICE NUMBER, LOGCONTENT VARCHAR2(200), RECEIVEDBYID VARCHAR2(64), RECEIVEDBYORGID VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK_LOG VALUES (101,11,'stockin',0,5,'vendor-a',2.5,'initial','allowed','org-a','allowed','Allowed User',TIMESTAMP '2025-01-04 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK_LOG VALUES (102,11,'receive',5,4,NULL,0,'use','allowed','org-a','allowed','Allowed User',TIMESTAMP '2025-01-05 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK_LOG VALUES (103,13,'stockin',0,8,'vendor-b',3,'hidden','allowed','org-b','allowed','Allowed User',TIMESTAMP '2025-01-06 09:00:00','org-b','Fixture Org B')");
            sql.execute("CREATE TABLE T_EMC_MTL (ID VARCHAR2(64) PRIMARY KEY, MTLNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), STOCKQTY NUMBER, MTLCATEGORYID NUMBER, HARMFULNESS VARCHAR2(36), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), ORGID VARCHAR2(64))");
            sql.execute("CREATE TABLE T_EMC_STOCK (ID NUMBER PRIMARY KEY, MTLID VARCHAR2(64), BATCHNO VARCHAR2(100), STOCKQTY NUMBER, UNITNAME VARCHAR2(64), EXPIREDATE TIMESTAMP, LOWLIMIT NUMBER, STATUS VARCHAR2(1), FINENESS VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), VERDORNAME VARCHAR2(100), REMINDDAYS NUMBER)");
            sql.execute("CREATE TABLE T_EMC_STOCK_LOG (ID NUMBER PRIMARY KEY, STOCKID NUMBER, LOGTYPE VARCHAR2(36), PREVIOUSQTY NUMBER, QTY NUMBER, VENDORID VARCHAR2(64), PRICE NUMBER, LOGCONTENT VARCHAR2(200), RECEIVEDBYID VARCHAR2(64), RECEIVEDBYORGID VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            sql.execute("INSERT INTO T_EMC_MTL VALUES ('m-1','MDM Alpha','1 ml',4,1,'normal','allowed','Allowed User',TIMESTAMP '2025-01-01 00:00:00','org-a','Fixture Org A','org-a')");
            sql.execute("INSERT INTO T_EMC_MTL VALUES ('m-2','MDM Hidden','1 ml',8,1,'normal','allowed','Allowed User',TIMESTAMP '2025-01-01 00:00:00','org-b','Fixture Org B','org-b')");
            sql.execute("INSERT INTO T_EMC_STOCK VALUES (201,'m-1','MDM-A',4,'bottle',TIMESTAMP '2026-01-01 00:00:00',1,'0','99','allowed','Allowed User',TIMESTAMP '2025-01-01 00:00:00','org-a','Fixture Org A','Vendor A',30)");
            sql.execute("INSERT INTO T_EMC_STOCK VALUES (202,'m-2','MDM-B',8,'bottle',TIMESTAMP '2026-01-01 00:00:00',1,'0','99','allowed','Allowed User',TIMESTAMP '2025-01-01 00:00:00','org-b','Fixture Org B','Vendor B',30)");
            sql.execute("INSERT INTO T_EMC_STOCK_LOG VALUES (301,201,'添加',0,4,'vendor-a',2,'mdm initial','allowed','org-a','allowed','Allowed User',TIMESTAMP '2025-01-01 00:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_STOCK_LOG VALUES (302,202,'添加',0,8,'vendor-b',2,'mdm hidden','allowed','org-b','allowed','Allowed User',TIMESTAMP '2025-01-01 00:00:00','org-b','Fixture Org B')");
        }
        java.nio.file.Path base = java.nio.file.Files.createTempDirectory("owned-consumables-http-");
        java.nio.file.Path audit = base.resolve("audit.jsonl");
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-consumables-test",
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
            check(http(port,"POST",BASE+"/queries/raw","{}",null,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"anonymous read is denied");
            String key = http(port,"GET","/open/security/public-key",null,null,null,200).body;
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key))));
            String encrypted=URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))),"UTF-8");
            String cookie=http(port,"POST","/core/module/sys/login","id=allowed&password="+encrypted,null,key,200).cookie;
            check(cookie!=null,"RSA login creates session");
            JSONObject page=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",cookie,null,200).body);
            check(page.getIntValue("total")==2 && page.getJSONArray("rows").size()==1,"original consumables raw route returns scoped Page");
            JSONObject searchable=JSON.parseObject(http(port,"POST",BASE+"/queries/searchable","{\"p\":{\"n\":1,\"s\":1}}",cookie,null,200).body);
            check(searchable.getIntValue("total")==2 && searchable.getJSONArray("rows").size()==1
                    && searchable.containsKey("columnTypeMap") && searchable.containsKey("totalPages"),
                    "consumables searchable route preserves the full pagination envelope");
            JSONObject first=page.getJSONArray("rows").getJSONObject(0);
            check("CSM-003".equals(first.getString("id")) && "Beta Bottle".equals(first.getString("mtlName")) && first.getDoubleValue("stockQty")==0 && "2025-01-03".equals(first.getString("createdTime")),"original DTO fields, numeric quantity and date projection");
            JSONObject filtered=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"f\":{\"MTLNAME_CISC\":\"bottle\",\"MTLCATEGORYID_EQ\":\"glass\"},\"n\":2,\"s\":1}",cookie,null,200).body);
            check(filtered.getIntValue("total")==2 && "CSM-001".equals(filtered.getJSONArray("rows").getJSONObject(0).getString("id")),"direct page envelope filters by name/category and pages");
            JSONObject empty=JSON.parseObject(http(port,"POST",BASE+"/queries","{\"p\":{\"f\":{\"MTLNAME_EQ\":\"absent\"}}}",cookie,null,200).body);
            check(empty.getIntValue("total")==0 && empty.getJSONArray("rows").isEmpty(),"original no-match page");
            JSONObject outer=new JSONObject();outer.put("requestJson","{\"p\":{\"f\":{\"PRODUCTNO_EQ\":\"P-001\"}}}");
            check(JSON.parseObject(http(port,"POST",BASE+"/queries/raw",outer.toJSONString(),cookie,null,200).body).getIntValue("total")==1,"nested requestJson preserves original product filter");

            JSONObject detail=JSON.parseObject(http(port,"GET",BASE+"/CSM-001",null,cookie,null,200).body);
            check("Alpha Bottle".equals(detail.getString("mtlName")),"original consumables detail returns DTO");
            String hidden=http(port,"GET",BASE+"/CSM-002",null,cookie,null,200).body.trim();
            check(hidden.isEmpty() || "null".equals(hidden),"out-of-scope consumables detail is suppressed");
            JSONObject stocks=JSON.parseObject(http(port,"POST",BASE+"/CSM-001/stocks/queries","{\"p\":{\"status_SEQ\":\"0\"}}",cookie,null,200).body);
            check(stocks.getIntValue("total")==1 && stocks.getJSONArray("rows").getJSONObject(0).getLongValue("id")==11,"stock route delegates trusted MTLID filter");
            JSONObject allMaterials=JSON.parseObject(http(port,"POST",BASE+"/0/stocks/queries","{\"p\":{\"n\":1,\"s\":10}}",cookie,null,200).body);
            check(allMaterials.getIntValue("total")==2 && allMaterials.getJSONArray("rows").size()==2
                    && allMaterials.getJSONArray("rows").getJSONObject(0).getLongValue("id")==12
                    && allMaterials.getJSONArray("rows").getJSONObject(1).getLongValue("id")==11,
                    "id=0 stock route returns all in-scope material stocks with pagination");
            JSONObject logs=JSON.parseObject(http(port,"POST",BASE+"/stocks/11/logs/queries","{}",cookie,null,200).body);
            check(logs.getIntValue("total")==2,"stock log route delegates trusted STOCKID filter");
            JSONObject stock=stocks.getJSONArray("rows").getJSONObject(0);
            check("A-1".equals(stock.getString("batchNo")) && stock.getDoubleValue("stockQty")==5 && "bottle".equals(stock.getString("unitName")) && "2026-01-01".equals(stock.getString("expireDate")),"visible stock fields keep original DTO projection");
            JSONObject all=JSON.parseObject(http(port,"POST",BASE+"/CSM-001/stocks/queries","{\"p\":{\"status_SEQ\":null,\"stockQty_NG\":null}}",cookie,null,200).body);
            check(all.getIntValue("total")==2,"show-all stock includes depleted/abandoned row");
            JSONObject log=logs.getJSONArray("rows").getJSONObject(0);
            check(log.getLongValue("id")==102 && log.getLongValue("stockId")==11 && "receive".equals(log.getString("logType")) && log.getDoubleValue("previousQty")==5 && log.getDoubleValue("qty")==4,"stock log fields and numeric identifiers retain original DTO");
            check("Allowed User".equals(log.getJSONObject("ext$").getString("username")) && "Fixture Org A".equals(log.getJSONObject("ext$").getString("orgname")),"stock history receiver and organization labels remain visible");
            JSONObject mdmStock=JSON.parseObject(http(port,"POST","/secure/emc/module/mdm/basemdm/mtls/m-1/stocks/queries","{\"p\":{\"n\":1,\"s\":10}}",cookie,null,200).body);
            check(mdmStock.getIntValue("total")==1 && mdmStock.getJSONArray("rows").getJSONObject(0).getLongValue("id")==201L,"mdm stock route returns the in-scope stock projection");
            JSONObject mdmAll=JSON.parseObject(http(port,"POST","/secure/emc/module/mdm/basemdm/mtls/0/stocks/queries","{}",cookie,null,200).body);
            check(mdmAll.getIntValue("total")==1,"mdm id=0 stock route suppresses the cross-organization stock");
            JSONObject mdmLogs=JSON.parseObject(http(port,"POST","/secure/emc/module/mdm/basemdm/mtls/stocks/201/logs/queries","{}",cookie,null,200).body);
            check(mdmLogs.getIntValue("total")==1 && mdmLogs.getJSONArray("rows").getJSONObject(0).getLongValue("stockId")==201L,"mdm stock-log route preserves the scoped log projection");
            JSONObject mdmMaterials=JSON.parseObject(http(port,"POST","/secure/emc/module/mdm/basemdm/mtls/queries/raw","{}",cookie,null,200).body);
            check(mdmMaterials.getIntValue("total")==1 && "m-1".equals(mdmMaterials.getJSONArray("rows").getJSONObject(0).getString("id")),"mdm material raw route returns the in-scope material");
            JSONObject mdmChoosable=JSON.parseObject(http(port,"POST","/secure/emc/module/mdm/basemdm/mtls/queries/choosable","{}",cookie,null,200).body);
            check(mdmChoosable.getIntValue("total")==1,"mdm material chooser suppresses the cross-organization material");
            JSONObject mdmDetail=JSON.parseObject(http(port,"GET","/secure/emc/module/mdm/basemdm/mtls/m-1",null,cookie,null,200).body);
            check("MDM Alpha".equals(mdmDetail.getString("mtlName")) && "org-a".equals(mdmDetail.getString("orgId")),"mdm material detail preserves DTO fields and organization scope");
            http(port,"POST","/secure/emc/module/mdm/basemdm/mtls/m-1/general-mtl-stocks","{\"b\":[\"\",[{\"qty\":1}]]}",cookie,"application/json",403);
            check(JSON.parseObject(http(port,"POST",BASE+"/CSM-002/stocks/queries","{}",cookie,null,200).body).getIntValue("total")==0,"hidden consumable stocks remain suppressed");
            check(JSON.parseObject(http(port,"POST",BASE+"/stocks/13/logs/queries","{}",cookie,null,200).body).getIntValue("total")==0,"hidden consumable history remains suppressed");
            check(http(port,"GET","/static/emc/module/item/consume/consumes/emc-consume-edit-list.js",null,cookie,null,200).body.contains("emcConsume.editPage"),"original page script is served at its original URL");

            check(http(port,"POST",BASE,"{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"generic insert is rejected by query guard");
            check(http(port,"PUT",BASE+"/CSM-001","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"generic update is rejected by query guard");
            check(http(port,"POST",BASE+"/CSM-001/stocks","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"stock write is rejected by query guard");
            check(http(port,"POST",BASE+"/queries/raw","{\"unknown\":1}",cookie,null,400).body.contains("GIKAM.CONSUMABLES.INVALID_REQUEST"),"unknown query envelope is rejected");
            check(http(port,"POST",BASE+"/queries/raw?n=1&n=2","{}",cookie,null,400).body.contains("GIKAM.CONSUMABLES.INVALID_REQUEST"),"repeated query parameter is rejected");
            check(http(port,"GET",BASE+"/page/edit-list?readonly=1",null,cookie,null,200).body.contains("emc-consume-edit-list.js"),"original consumables page renders");

            for(String invalid:new String[]{"{\"p\":{},\"n\":1}","{\"p\":{\"loginUserId\":\"low\"}}","{\"f\":{\"MTLNAME_UNKNOWN\":\"x\"}}","[]","null","{"}) {
                http(port,"POST",BASE+"/queries/raw",invalid,cookie,null,400);
            }
            http(port,"POST",BASE+"/queries/raw?n=1","{\"n\":2}",cookie,null,400);
            http(port,"POST",BASE+"/queries/raw?unknown=1","{}",cookie,null,400);
            http(port,"PUT",BASE+"/stocks/11/action/stockin","{}",cookie,null,403);
            http(port,"DELETE",BASE+"/CSM-001/stocks","{}",cookie,null,403);
            http(port,"POST",BASE+"/CSM-001/stocks/queries/unknown","{}",cookie,null,403);
            String low=http(port,"POST","/core/module/sys/login","id=low&password="+encrypted,null,key,200).cookie;
            http(port,"POST",BASE+"/queries/raw","{}",low,null,403);
            try (Connection connection=ds.getConnection(); Statement sql=connection.createStatement()) {
                sql.execute("DELETE FROM T_CORE_USER_PERMISSION WHERE USERID='allowed'");
            }
            check(JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{}",cookie,null,200).body).getIntValue("total")==0,"current organization revocation removes list rows");
            check(JSON.parseObject(http(port,"POST",BASE+"/CSM-001/stocks/queries","{}",cookie,null,200).body).getIntValue("total")==0,"current organization revocation removes stock rows");
            check(JSON.parseObject(http(port,"POST",BASE+"/stocks/11/logs/queries","{}",cookie,null,200).body).getIntValue("total")==0,"current organization revocation removes history rows");
            try (Connection connection=ds.getConnection(); Statement sql=connection.createStatement()) {
                sql.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
                sql.execute("UPDATE T_CORE_ROLE_PERMISSION SET TARGETID='20250800003' WHERE ROLEID=1");
            }

            check(JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{}",cookie,null,200).body).getIntValue("total")==2,"read-only menu independently permits bounded reads");
            http(port,"GET",BASE+"/page/edit-list",null,cookie,null,403);
            http(port,"GET",BASE+"/page/edit-list?readonly=1",null,cookie,null,200);
            http(port,"POST",BASE+"/CSM-001/stocks/queries","{}",cookie,null,200);
            http(port,"POST",BASE+"/stocks/11/logs/queries","{}",cookie,null,200);

            try (Connection connection=ds.getConnection(); Statement sql=connection.createStatement()) {
                sql.execute("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=1");
            }
            check(http(port,"POST",BASE+"/queries/raw","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),"revoked menu grant is rechecked");
        } finally {
            app.close();
            java.nio.file.Files.walk(base).sorted(java.util.Comparator.reverseOrder()).forEach(path -> { try { java.nio.file.Files.deleteIfExists(path); } catch(IOException e) { throw new java.io.UncheckedIOException(e); } });
        }
        System.out.println("consumables runtime HTTP: PASS ("+checks+" checks; Boot original application, loopback HTTP and synthetic Oracle only)");
    }
    private static Response http(int port,String method,String path,String body,String cookie,String key,int expected) throws Exception {
        HttpURLConnection c=(HttpURLConnection)new URL("http://127.0.0.1:"+port+path).openConnection();
        c.setConnectTimeout(5000); c.setReadTimeout(10000);
        c.setRequestMethod(method); c.setInstanceFollowRedirects(false);
        if(cookie!=null)c.setRequestProperty("Cookie",cookie);
        if(key!=null)c.setRequestProperty("SECURITY-RSA-KEY",key);
        if(body!=null){c.setDoOutput(true); c.setRequestProperty("Content-Type",key!=null?"application/x-www-form-urlencoded":"application/json");
            byte[] bytes=body.getBytes(StandardCharsets.UTF_8); c.setFixedLengthStreamingMode(bytes.length);try(OutputStream o=c.getOutputStream()){o.write(bytes);}}
        int status=c.getResponseCode();
        ByteArrayOutputStream bytes=new ByteArrayOutputStream(); InputStream stream=status>=400?c.getErrorStream():c.getInputStream();
        if(stream!=null)try(InputStream input=stream){byte[] b=new byte[1024];int n;while((n=input.read(b))!=-1)bytes.write(b,0,n);}
        check(status==expected,path+" returned HTTP "+status+", expected "+expected+" body="+new String(bytes.toByteArray(),StandardCharsets.UTF_8));
        String set=c.getHeaderField("Set-Cookie"); c.disconnect();
        return new Response(new String(bytes.toByteArray(),StandardCharsets.UTF_8),set==null?cookie:set.split(";",2)[0]);
    }
    private static final class Response {final String body,cookie;Response(String b,String c){body=b;cookie=c;}}
}
