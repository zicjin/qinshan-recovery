import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.emc.projectfunction.GikamWpjcApplication;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.*;
import javax.crypto.Cipher;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Original equipment resource and actual historical main with a disposable Oracle. */
public final class OwnedEquipmentRuntimeHttpTest {
    private static final String BASE="/secure/emc/module/mdm/equipt/equipts";
    private static int checks;
    private static void check(boolean value,String label){checks++;if(!value)throw new AssertionError(label);}
    public static void main(String[] args) throws Exception {
        Properties p=new Properties();try(FileInputStream input=new FileInputStream(args[0])){p.load(input);}
        OracleDataSource ds=new OracleDataSource();ds.setURL(p.getProperty("url"));ds.setUser(p.getProperty("user"));ds.setPassword(p.getProperty("password"));
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP, PARENTORGID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
            s.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY, ORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            s.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            s.execute("CREATE TABLE T_EMC_ORG_PERMISSION (PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64), ORGID VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_EQUIPT_CATEGORY (ID VARCHAR2(64) PRIMARY KEY, EQUIPTCATEGORY VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_EQUIPT (ID NUMBER PRIMARY KEY, EQUIPTID VARCHAR2(64), EQUIPTNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), FACTORYNO VARCHAR2(100), MANUFACTURER VARCHAR2(100), STATUS VARCHAR2(36), EQUIPTCATEGORYID VARCHAR2(64), USEDDEPARTMENTID VARCHAR2(64), USEDDEPOSTID VARCHAR2(64), SHARING VARCHAR2(36), MAINTAIN VARCHAR2(36), SOURCEMETHOD VARCHAR2(36), SOURCEDATE DATE, EQUIPTBELONG VARCHAR2(36), MAINTAINUSERID VARCHAR2(64), KEEPUSERID VARCHAR2(64), ORGID VARCHAR2(64))");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('101501')");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('20251000001')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('org-b','Fixture Org B')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('post-c','Fixture Post C')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('allowed','Allowed User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('low','Low User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','101501')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
            s.execute("INSERT INTO T_EMC_EQUIPT_CATEGORY VALUES ('11','Microscope')");
            s.execute("INSERT INTO T_EMC_EQUIPT_CATEGORY VALUES ('12','Balance')");
            s.execute("INSERT INTO T_EMC_ORG_PERMISSION VALUES ('T_EMC_EQUIPT_CATEGORY','11','org-a')");
            s.execute("INSERT INTO T_EMC_ORG_PERMISSION VALUES ('T_EMC_EQUIPT_CATEGORY','12','org-b')");
            s.execute("INSERT INTO T_EMC_EQUIPT VALUES (1,'EQ-001','Alpha Scope','M-1','F-1','Maker','inUse','11','org-a','post-c','owned','normal','purchase',DATE '2025-01-01','owned',NULL,NULL,'org-a')");
            s.execute("INSERT INTO T_EMC_EQUIPT VALUES (2,'EQ-002','Beta Balance','B-1','F-2','Maker','inUse','12','org-b','org-b','owned','normal','purchase',DATE '2025-01-02','owned',NULL,NULL,'org-b')");
            s.execute("INSERT INTO T_EMC_EQUIPT VALUES (3,'EQ-003','Alpha Scope','M-2','F-3','Maker','retired','11','org-a','post-c','shared','normal','gift',DATE '2025-01-03','shared',NULL,NULL,'org-b')");
        }
        GikamWpjcApplication.main(new String[]{"--server.address=127.0.0.1","--server.port=0",
                "--spring.config.name=owned-equipment-runtime-test","--spring.main.banner-mode=off","--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver","--spring.datasource.url="+p.getProperty("url"),
                "--spring.datasource.username="+p.getProperty("user"),"--spring.datasource.password="+p.getProperty("password"),
                "--sunway.user.password.encoder.type=MD5","--owned.identity.audit.type=file","--owned.identity.audit.path="+args[1]});
        ServletWebServerApplicationContext app=(ServletWebServerApplicationContext)OwnedApplicationContext.getApplicationContext();
        try {
            int port=app.getWebServer().getPort();
            check(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",null,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "anonymous equipment query is denied");
            String key=http(port,"GET","/open/security/public-key",null,null,null,200).body;
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key))));
            String password=URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))),"UTF-8");
            String cookie=http(port,"POST","/core/module/sys/login","id=allowed&password="+password,null,key,200).cookie;
            check(cookie!=null,"RSA login creates a real session");
            JSONObject page=JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",cookie,null,200).body);
            check(page.getIntValue("total")==2 && page.getJSONArray("rows").size()==1,"original raw route returns scoped page");
            JSONObject row=page.getJSONArray("rows").getJSONObject(0);
            check(row.getLongValue("id")==3 && "EQ-003".equals(row.getString("equiptId"))
                    && row.getLongValue("equiptCategoryId")==11 && !row.containsKey("EQUIPTID"),
                    "original lower camel DTO fields and numeric identifiers");
            check("Fixture Org A".equals(row.getJSONObject("ext$").getString("useddepartmentname"))
                    && "Fixture Post C".equals(row.getJSONObject("ext$").getString("useddepostname"))
                    && "Microscope".equals(row.getJSONObject("ext$").getString("equiptcategoryname")),
                    "distinct department and post labels remain in ext$");
            JSONObject detail=JSON.parseObject(http(port,"GET",BASE+"/1",null,cookie,null,200).body);
            check("Alpha Scope".equals(detail.getString("equiptName"))
                    && "Fixture Post C".equals(detail.getJSONObject("ext$").getString("useddepostname"))
                    && "2025-01-01".equals(detail.getString("sourceDate")),"original detail preserves DTO date and name");
            String deniedDetail=http(port,"GET",BASE+"/2",null,cookie,null,200).body.trim();
            check(deniedDetail.isEmpty() || "null".equals(deniedDetail),"category outside organization scope is suppressed");
            JSONObject direct=JSON.parseObject(http(port,"POST",BASE+"/queries/raw",
                    "{\"f\":{\"EQUIPTNAME_CISC\":\"scope\"},\"n\":2,\"s\":1}",cookie,null,200).body);
            check(direct.getIntValue("total")==2 && direct.getJSONArray("rows").size()==1
                    && direct.getJSONArray("rows").getJSONObject(0).getLongValue("id")==1,
                    "direct transport applies filtering and second page");
            JSONObject empty=JSON.parseObject(http(port,"POST",BASE+"/queries",
                    "{\"f\":{\"EQUIPTNAME_EQ\":\"absent\"}}",cookie,null,200).body);
            check(empty.getIntValue("total")==0 && empty.getJSONArray("rows").isEmpty(),"original no-match page");
            check(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"unknown\":\"x\"}}",cookie,null,400).body.contains("GIKAM.EQUIPMENT.INVALID_REQUEST"),
                    "unknown equipment query field fails explicitly");
            JSONObject outer=new JSONObject(); outer.put("requestJson","{\"p\":{\"f\":{\"EQUIPTID_EQ\":\"EQ-001\"},\"n\":1,\"s\":1}}");
            JSONObject nested=JSON.parseObject(http(port,"POST",BASE+"/queries/raw",outer.toJSONString(),cookie,null,200).body);
            check(nested.getIntValue("total")==1 && nested.getJSONArray("rows").getJSONObject(0).getLongValue("id")==1,
                    "nested requestJson transport applies original filter");
            String queryFilter=URLEncoder.encode("{\"EQUIPTID_EQ\":\"EQ-001\"}","UTF-8");
            JSONObject query=JSON.parseObject(http(port,"POST",BASE+"/queries/raw?f="+queryFilter+"&n=1&s=1","{}",cookie,null,200).body);
            check(query.getIntValue("total")==1 && query.getJSONArray("rows").size()==1,"query parameters retain filter and pagination");
            JSONObject selected=JSON.parseObject(http(port,"POST",BASE+"/queries/raw",
                    "{\"p\":{\"target_type\":\"equipList\",\"orgid_seq\":\"org-a\"}}",cookie,null,200).body);
            check(selected.getIntValue("total")==1,"page organization selection narrows authorized category scope");
            for(String invalid:new String[]{"{\"unknown\":1}","{\"p\":{},\"f\":{}}","{\"s\":501}",
                    "{\"f\":{\"EQUIPTNAME_UNKNOWN\":\"x\"}}","{\"p\":{\"loginUserId\":\"low\"}}"}) {
                check(http(port,"POST",BASE+"/queries/raw",invalid,cookie,null,400).body.contains("GIKAM.EQUIPMENT.INVALID_REQUEST"),
                        "ambiguous or unsupported transport rejected explicitly");
            }
            check(http(port,"POST",BASE+"/queries/raw?n=1&n=2","{}",cookie,null,400).body.contains("GIKAM.EQUIPMENT.INVALID_REQUEST"),
                    "repeated query parameter is rejected");
            check(http(port,"POST",BASE+"/queries/raw?n=1","{\"n\":2}",cookie,null,400).body.contains("GIKAM.EQUIPMENT.INVALID_REQUEST"),
                    "duplicate body and query input is rejected");
            check(http(port,"POST",BASE+"/1/test-methods/queries","{}",cookie,null,501).body.contains("GIKAM.EQUIPMENT.UNSUPPORTED"),
                    "original auxiliary method rejects unsupported flow");
            check(http(port,"POST",BASE+"/1/plans/queries","{}",cookie,null,501).body.contains("GIKAM.EQUIPMENT.UNSUPPORTED"),
                    "original injected auxiliary service rejects unsupported flow");
            check(http(port,"POST",BASE+"/1/records/queries","{}",cookie,null,501).body.contains("GIKAM.EQUIPMENT.UNSUPPORTED"),
                    "original injected maintenance-record service rejects unsupported flow");
            check(http(port,"GET",BASE+"/1/records/maints/queries/selectable",null,cookie,null,501).body.contains("GIKAM.EQUIPMENT.UNSUPPORTED"),
                    "original selectable maintenance-record service rejects unsupported flow");
            check(http(port,"POST",BASE+"/1/per-check-plan/queries","{}",cookie,null,501).body.contains("GIKAM.EQUIPMENT.UNSUPPORTED"),
                    "original period-check plan service rejects unsupported flow");
            check(http(port,"POST",BASE+"/1/per-check-record/queries","{}",cookie,null,501).body.contains("GIKAM.EQUIPMENT.UNSUPPORTED"),
                    "original period-check record service rejects unsupported flow");
            check(http(port,"GET",BASE+"/1/per-check-records/maints/queries/selectable",null,cookie,null,501).body.contains("GIKAM.EQUIPMENT.UNSUPPORTED"),
                    "original period-check selectable service rejects unsupported flow");
            check(http(port,"GET",BASE+"/page/edit-list",null,cookie,null,200).body.contains("emc-equipt-edit-list.js"),
                    "original management page renders for manager");
            try (Connection c=ds.getConnection(); Statement s=c.createStatement()) {
                s.execute("UPDATE T_CORE_ROLE_PERMISSION SET TARGETID='20251000001' WHERE ROLEID=1");
            }
            check(http(port,"GET",BASE+"/page/edit-list?readonly=1",null,cookie,null,200).body.contains("emc-equipt-edit-list.js"),
                    "read-only menu renders original read-only list");
            http(port,"GET",BASE+"/page/edit-list",null,cookie,null,403);
            check(JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{}",cookie,null,200).body).getIntValue("total")==2,
                    "read-only menu independently permits bounded reads");
            try (Connection c=ds.getConnection(); Statement s=c.createStatement()) {
                s.execute("DELETE FROM T_EMC_ORG_PERMISSION WHERE ORGID='org-a'");
            }
            check(JSON.parseObject(http(port,"POST",BASE+"/queries/raw","{}",cookie,null,200).body).getIntValue("total")==0,
                    "revoked category organization grant removes list results");
            String revokedDetail=http(port,"GET",BASE+"/1",null,cookie,null,200).body.trim();
            check(revokedDetail.isEmpty() || "null".equals(revokedDetail),"revoked category organization grant suppresses detail");
            try (Connection c=ds.getConnection(); Statement s=c.createStatement()) {
                s.execute("INSERT INTO T_EMC_ORG_PERMISSION VALUES ('T_EMC_EQUIPT_CATEGORY','11','org-a')");
            }
            String lowCookie=http(port,"POST","/core/module/sys/login","id=low&password="+password,null,key,200).cookie;
            check(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",lowCookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "low privilege equipment query is denied");
            check(http(port,"POST",BASE+"/1/test-methods","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "equipment write route is denied by readonly guard");
            check(http(port,"POST",BASE+"/records","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "maintenance-record write route is denied by readonly guard");
            check(http(port,"POST",BASE+"/per-check-plan","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "period-check plan write route is denied by readonly guard");
            check(http(port,"POST",BASE+"/per-check-record","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "period-check record write route is denied by readonly guard");
            check(http(port,"PUT",BASE+"/records/action/complete","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "maintenance-record completion route is denied by readonly guard");
            check(http(port,"PUT",BASE+"/per-check-records/action/complete","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "period-check completion route is denied by readonly guard");
            check(http(port,"DELETE",BASE+"/records","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "maintenance-record delete route is denied by readonly guard");
            check(http(port,"DELETE",BASE+"/per-check-plan","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "period-check plan delete route is denied by readonly guard");
            check(http(port,"DELETE",BASE+"/per-check-record","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "period-check record delete route is denied by readonly guard");
            check(http(port,"POST",BASE+"/use-records","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "equipment use-record insert route is denied by readonly guard");
            check(http(port,"PUT",BASE+"/use-records","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "equipment use-record update route is denied by readonly guard");
            check(http(port,"DELETE",BASE+"/delete-records","{}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "equipment use-record delete route is denied by readonly guard");
            try (Connection c=ds.getConnection(); Statement s=c.createStatement()) {
                s.execute("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=1");
            }
            check(http(port,"POST",BASE+"/queries/raw","{\"p\":{\"n\":1,\"s\":1}}",cookie,null,403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"),
                    "revoked equipment menu grant is rechecked");
        } finally {app.close();}
        System.out.println("equipment runtime HTTP: PASS ("+checks+" checks; actual historical main/resource, RSA session and synthetic Oracle only)");
    }
    private static Reply http(int port,String method,String path,String body,String cookie,String key,int expected)throws Exception{
        HttpURLConnection c=(HttpURLConnection)new URL("http://127.0.0.1:"+port+path).openConnection();
        c.setConnectTimeout(5000);c.setReadTimeout(10000);c.setRequestMethod(method);c.setInstanceFollowRedirects(false);
        if(cookie!=null)c.setRequestProperty("Cookie",cookie);if(key!=null)c.setRequestProperty("SECURITY-RSA-KEY",key);
        if(body!=null){c.setDoOutput(true);c.setRequestProperty("Content-Type",key==null?"application/json":"application/x-www-form-urlencoded");
            byte[] bytes=body.getBytes(StandardCharsets.UTF_8);c.setFixedLengthStreamingMode(bytes.length);try(OutputStream output=c.getOutputStream()){output.write(bytes);}}
        try{int status=c.getResponseCode();ByteArrayOutputStream bytes=new ByteArrayOutputStream();InputStream stream=status>=400?c.getErrorStream():c.getInputStream();
            if(stream!=null)try(InputStream input=stream){byte[] buffer=new byte[4096];int n;while((n=input.read(buffer))!=-1)bytes.write(buffer,0,n);}
            check(status==expected,path+" returned HTTP "+status+", expected "+expected);
            String set=c.getHeaderField("Set-Cookie");return new Reply(new String(bytes.toByteArray(),StandardCharsets.UTF_8),set==null?cookie:set.split(";",2)[0]);
        }finally{c.disconnect();}
    }
    private static final class Reply{final String body,cookie;Reply(String b,String c){body=b;cookie=c;}}
}
