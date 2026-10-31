package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.OwnedOrganizationManagementController;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/** Loopback Oracle contract for the owned global organization management seam. */
public final class OwnedOrganizationManagementHttpContractTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("oracle properties path required");
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setURL(properties.getProperty("url")); dataSource.setUser(properties.getProperty("user")); dataSource.setPassword(properties.getProperty("password"));
        seed(dataSource);
        OwnedRoleMenuMatrixHttpContractTest.ProbeConfiguration.DATA_SOURCE = dataSource;
        OwnedRoleMenuMatrixHttpContractTest.ProbeConfiguration.PROVIDER = new JdbcPersonnelIdentityProvider(
                new JdbcPersonnelIdentity(dataSource, "MD5", new String[0], new String[] { "100900" }));
        System.setProperty("owned.organization.write.mode", "isolated");
        System.setProperty("owned.role.write.mode", "isolated");
        AnnotationConfigWebApplicationContext application = new AnnotationConfigWebApplicationContext();
        application.register(OwnedRoleMenuMatrixHttpContractTest.ProbeConfiguration.class, OrganizationConfiguration.class);
        application.refresh();
        Tomcat tomcat = new Tomcat(); File base = new File(System.getProperty("java.io.tmpdir"), "owned-organization-management"); base.mkdirs();
        tomcat.setBaseDir(base.getAbsolutePath()); tomcat.setPort(0); tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addContext("", base.getAbsolutePath()); Tomcat.addServlet(context, "dispatcher", new DispatcherServlet(application)); context.addServletMappingDecoded("/", "dispatcher"); tomcat.start();
        int port = tomcat.getConnector().getLocalPort();
        try {
            String roleAdmin = login(port, "role-admin", "secret"); String reader = login(port, "reader", "secret");
            expect(port, "POST", "/secure/core/module/mdm/orgs/queries/raw", "{}", reader, 403);
            String initial = request(port, "POST", "/secure/core/module/mdm/orgs/queries/raw", "{}", roleAdmin, 200);
            check(initial.contains("org-a") && initial.contains("org-b"), "organization query returns both organizations");
            expect(port, "POST", "/secure/core/module/mdm/orgs", "{\"p\":{\"id\":\"org-c\",\"orgName\":\"Child C\",\"parentId\":\"org-a\"}}", roleAdmin, 200);
            String afterInsert = request(port, "POST", "/secure/core/module/mdm/orgs/queries/raw", "{}", roleAdmin, 200);
            check(afterInsert.contains("Child C"), "organization insert is visible");
            expect(port, "PUT", "/secure/core/module/mdm/orgs/org-c", "{\"p\":{\"id\":\"org-c\",\"orgName\":\"Child C Updated\"}}", roleAdmin, 200);
            String detail = request(port, "GET", "/secure/core/module/mdm/orgs/org-c", "", roleAdmin, 200);
            check(detail.contains("Child C Updated"), "organization update is visible in detail");
            expect(port, "PUT", "/secure/core/module/mdm/orgs/org-b", "{\"p\":{\"id\":\"org-b\",\"orgName\":\"Other Org Updated\"}}", roleAdmin, 200);
            expect(port, "DELETE", "/secure/core/module/mdm/orgs", "{\"ids\":[\"org-a\"]}", roleAdmin, 400);
            expect(port, "DELETE", "/secure/core/module/mdm/orgs", "{\"ids\":[\"org-c\"]}", roleAdmin, 200);
            String removed = request(port, "POST", "/secure/core/module/mdm/orgs/queries/raw", "{}", roleAdmin, 200);
            check(!removed.contains("org-c"), "organization delete restores baseline");
            expect(port, "POST", "/secure/core/module/sys/roles", "{\"p\":{\"roleName\":\"Browser Role\"}}", roleAdmin, 200);
            String roles = request(port, "POST", "/secure/core/module/sys/roles/queries/raw", "{}", roleAdmin, 200);
            check(roles.contains("Browser Role"), "role insert is visible");
            long roleId;
            try (Connection connection = dataSource.getConnection(); PreparedStatement lookup = connection.prepareStatement("SELECT ID FROM T_CORE_ROLE WHERE ROLENAME='Browser Role'")) {
                try (java.sql.ResultSet result = lookup.executeQuery()) { if (!result.next()) throw new AssertionError("inserted role id missing"); roleId = result.getLong(1); }
            }
            expect(port, "PUT", "/secure/core/module/sys/roles/" + roleId, "{\"p\":{\"id\":" + roleId + ",\"roleName\":\"Browser Role Updated\"}}", roleAdmin, 200);
            expect(port, "DELETE", "/secure/core/module/sys/roles", "{\"ids\":[" + roleId + "]}", roleAdmin, 200);
            expect(port, "PUT", "/secure/core/module/sys/roles/4", "{\"p\":{\"id\":4,\"roleName\":\"Role Administrator\",\"lastSuspendedFlag\":\"1\"}}", roleAdmin, 403);
            expect(port, "DELETE", "/secure/core/module/sys/roles", "{\"ids\":[4]}", roleAdmin, 400);
            System.clearProperty("owned.organization.write.mode");
            expect(port, "POST", "/secure/core/module/mdm/orgs", "{\"p\":{\"id\":\"org-d\",\"orgName\":\"Denied\"}}", roleAdmin, 403);
            boolean audit = false;
            for (AuditEvent event : OwnedRoleMenuMatrixHttpContractTest.ProbeConfiguration.SINK.events()) {
                if ("organization-insert".equals(event.getAction()) && event.getOutcome() == AuditEvent.Outcome.ALLOWED) audit = true;
            }
            check(audit, "organization write audit is recorded");
            System.out.println("ORGANIZATION_MANAGEMENT|role-admin|multi-org-read-write-delete|PASS");
            System.out.println("ROLE_MANAGEMENT|role-admin|create-update-delete-suspend-protection|PASS");
            System.out.println("owned organization management HTTP: PASS (17 checks; synthetic Oracle only)");
        } finally {
            tomcat.stop(); tomcat.destroy(); application.close(); System.clearProperty("owned.organization.write.mode"); System.clearProperty("owned.role.write.mode"); cleanup(dataSource);
        }
    }

    private static void seed(DataSource d) throws Exception { try (Connection c=d.getConnection(); Statement s=c.createStatement()) {
        s.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY,LOGINORG VARCHAR2(1),LOGINROLE VARCHAR2(1),LOGINTENANT VARCHAR2(1),LOGINAUTHMETHOD VARCHAR2(36),CAPTCHA VARCHAR2(1),ADDOMAINSUPPORT VARCHAR2(1),SHOWLOGINAUTHOPTION VARCHAR2(1),QCCODE VARCHAR2(1))");
        s.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY,MAXFAILEDATTEMPTS NUMBER,EXPIRYINTERVAL NUMBER,EXPIREDPOLICY VARCHAR2(36),LASTSUSPENDEDFLAG VARCHAR2(1))");
        s.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY,PASSWORD VARCHAR2(120),ORGID VARCHAR2(64),ORGNAME VARCHAR2(100),STATUS VARCHAR2(20),LASTSUSPENDEDFLAG VARCHAR2(1),EXPIRYDATE TIMESTAMP)");
        s.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
        s.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY,ROLENAME VARCHAR2(100),LASTSUSPENDEDFLAG VARCHAR2(1))"); s.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER,USERID VARCHAR2(64))"); s.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER,PERMISSIONTYPEID VARCHAR2(64),TARGETID VARCHAR2(64))"); s.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
        s.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64),PERMISSIONTYPEID VARCHAR2(64),TARGETID VARCHAR2(64))"); s.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY,ORGNAME VARCHAR2(100),PARENTID VARCHAR2(64),ORGCATEGORY VARCHAR2(64),ORDERNO NUMBER,LASTSUSPENDEDFLAG VARCHAR2(1))");
        s.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')"); s.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
        s.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A',NULL,'company',1,'0')"); s.execute("INSERT INTO T_CORE_ORG VALUES ('org-b','Fixture Org B',NULL,'company',2,'0')");
        s.execute("INSERT INTO T_CORE_USER VALUES ('role-admin','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL)"); s.execute("INSERT INTO T_CORE_USER VALUES ('reader','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL)");
        s.execute("INSERT INTO T_CORE_ROLE VALUES (4,'Role Administrator','0')"); s.execute("INSERT INTO T_CORE_ROLE VALUES (6,'Reader','0')"); s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (4,'role-admin')"); s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (6,'reader')"); s.execute("INSERT INTO T_CORE_MENU VALUES ('100900')"); s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (4,'T_CORE_MENU','100900')"); s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('role-admin','T_CORE_ORG','org-a')");
    }}
    private static void cleanup(DataSource d) throws Exception { try(Connection c=d.getConnection();Statement s=c.createStatement()){ for(String t:new String[]{"T_CORE_ROLE_PERMISSION","T_CORE_ROLE_USER","T_CORE_MENU","T_CORE_ROLE","T_CORE_USER_PERMISSION","T_CORE_USER","T_CORE_ORG","T_CORE_PASSWORD_POLICY","T_CORE_LOGIN_CONFIG"}) try{s.execute("DROP TABLE "+t+" CASCADE CONSTRAINTS");}catch(Exception ignored){} }}
    private static void check(boolean condition,String label){if(!condition)throw new AssertionError(label);}
    private static String expect(int port,String method,String path,String body,String cookie,int expected)throws Exception{return request(port,method,path,body,cookie,expected);}
    private static String login(int port,String user,String password)throws Exception{String key=request(port,"GET","/open/security/public-key","",null,200);PublicKey publicKey=KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");cipher.init(Cipher.ENCRYPT_MODE,publicKey);String enc=Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));return post(port,"/core/module/sys/login","id="+user+"&password="+ URLEncoder.encode(enc,"UTF-8"),key,200);}
    private static String post(int port,String path,String body,String key,int expected)throws Exception{HttpURLConnection c=(HttpURLConnection)new URL("http://127.0.0.1:"+port+path).openConnection();c.setRequestMethod("POST");c.setDoOutput(true);c.setRequestProperty("Content-Type","application/x-www-form-urlencoded");c.setRequestProperty("SECURITY-RSA-KEY",key);byte[] bytes=body.getBytes(StandardCharsets.UTF_8);c.setFixedLengthStreamingMode(bytes.length);try(OutputStream o=c.getOutputStream()){o.write(bytes);}int status=c.getResponseCode();if(status!=expected)throw new AssertionError(path+" status "+status);String cookie=c.getHeaderField("Set-Cookie");c.disconnect();return cookie.substring(0,cookie.indexOf(';'));}
    private static String request(int port,String method,String path,String body,String cookie,int expected)throws Exception{HttpURLConnection c=(HttpURLConnection)new URL("http://127.0.0.1:"+port+path).openConnection();c.setRequestMethod(method);if(cookie!=null)c.setRequestProperty("Cookie",cookie);if(!"GET".equals(method)){c.setDoOutput(true);c.setRequestProperty("Content-Type","application/json");byte[] b=body.getBytes(StandardCharsets.UTF_8);c.setFixedLengthStreamingMode(b.length);try(OutputStream o=c.getOutputStream()){o.write(b);}}int status=c.getResponseCode();if(status!=expected)throw new AssertionError(path+" status "+status+" expected "+expected);java.io.InputStream in=status>=400?c.getErrorStream():c.getInputStream();java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();if(in!=null){byte[] b=new byte[1024];int n;while((n=in.read(b))!=-1)out.write(b,0,n);}c.disconnect();return new String(out.toByteArray(),StandardCharsets.UTF_8);}
    @Configuration public static class OrganizationConfiguration { @Bean public OwnedOrganizationManagementController organizations(com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary b,DataSource d,Environment e){return new OwnedOrganizationManagementController(b,d,e);} }
}
