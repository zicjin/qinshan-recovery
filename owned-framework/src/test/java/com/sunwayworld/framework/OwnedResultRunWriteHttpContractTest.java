package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.InMemoryAuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpController;
import com.sunwayworld.owned.internal.web.OwnedResultRunController;
import com.sunwayworld.owned.internal.web.OwnedResultRunWriteController;
import com.sunwayworld.owned.internal.web.OwnedRsaLoginCredentialDecoder;
import com.sunwayworld.owned.internal.web.OwnedSecurityKeyController;
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
import java.sql.Statement;
import java.util.Base64;
import java.util.Properties;
import javax.sql.DataSource;
import javax.crypto.Cipher;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/** Red contract for the bounded result-run isolated status write seam. */
public final class OwnedResultRunWriteHttpContractTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("oracle properties path required");
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setURL(properties.getProperty("url")); dataSource.setUser(properties.getProperty("user")); dataSource.setPassword(properties.getProperty("password"));
        seed(dataSource);
        ProbeConfiguration.DATA_SOURCE = dataSource;
        ProbeConfiguration.PROVIDER = new JdbcPersonnelIdentityProvider(new JdbcPersonnelIdentity(
                dataSource, "MD5", new String[0], new String[0], new String[] {"300100"}, new String[0], new String[] {"300101"}));
        System.setProperty("owned.result.write.mode", "isolated");
        AnnotationConfigWebApplicationContext application = new AnnotationConfigWebApplicationContext();
        application.register(ProbeConfiguration.class, ResultConfiguration.class); application.refresh();
        Tomcat tomcat = new Tomcat(); File base = new File(System.getProperty("java.io.tmpdir"), "owned-result-run-write"); base.mkdirs();
        tomcat.setBaseDir(base.getAbsolutePath()); tomcat.setPort(0); tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addContext("", base.getAbsolutePath()); Tomcat.addServlet(context, "dispatcher", new DispatcherServlet(application)); context.addServletMappingDecoded("/", "dispatcher"); tomcat.start();
        int port = tomcat.getConnector().getLocalPort();
        try {
            String manager = login(port, "manager", "secret"); String reader = login(port, "reader", "secret");
            String query = request(port, "POST", "/secure/emc/module/bp/result/runs/queries/raw", "{\"p\":{\"n\":1,\"s\":10}}", manager, 200);
            check(query.contains("columnTypeMap") && query.contains("totalPages"), "supplier result-run query envelope is exposed on emc alias");
            String statusProbe = request(port, "GET", "/secure/emc/module/bp/result/runs/info/status", "", manager, 200);
            check(statusProbe.contains("auditStatus") && statusProbe.contains("ext$"), "supplier result-run status envelope is exposed on emc alias");
            String detail = request(port, "GET", "/secure/emc/module/bp/result/runs/1", "", manager, 200);
            check(detail.contains("\"id\":\"1\""), "supplier result-run detail is exposed on emc alias");
            String updated = request(port, "PUT", "/secure/core/module/bp/result/runs/1", "{\"p\":{\"status\":\"done\"}}", manager, 200);
            check(updated.contains("\"updated\":1"), "same-organization result status update returns count");
            check(status(dataSource, 1).equals("done"), "same-organization result status persisted");
            request(port, "PUT", "/secure/core/module/bp/result/runs/2", "{\"p\":{\"status\":\"approved\"}}", manager, 403);
            check(status(dataSource, 2).equals("done"), "foreign-organization result status remains unchanged");
            request(port, "PUT", "/secure/core/module/bp/result/runs/1", "{\"p\":{\"status\":\"bad status\"}}", manager, 400);
            check(status(dataSource, 1).equals("done"), "invalid result status rolls back");
            request(port, "PUT", "/secure/core/module/bp/result/runs/1", "{\"p\":{\"status\":\"audit\"}}", reader, 403);
            System.clearProperty("owned.result.write.mode");
            request(port, "PUT", "/secure/core/module/bp/result/runs/1", "{\"p\":{\"status\":\"audit\"}}", manager, 403);
            boolean allowed = false, denied = false;
            for (AuditEvent event : ProbeConfiguration.SINK.events()) {
                if ("result-run-update".equals(event.getAction()) && event.getOutcome() == AuditEvent.Outcome.ALLOWED) allowed = true;
                if ("result.write".equals(event.getResource()) && event.getOutcome() == AuditEvent.Outcome.DENIED) denied = true;
            }
            check(allowed, "result status update audit is recorded"); check(denied, "result status denial audit is recorded");
            System.out.println("RESULT_RUN_WRITE|manager|org-a|isolated|PASS");
            System.out.println("owned result-run read/write HTTP: PASS (11 checks; synthetic Oracle only)");
        } finally {
            tomcat.stop(); tomcat.destroy(); application.close(); System.clearProperty("owned.result.write.mode"); cleanup(dataSource);
        }
    }
    private static void seed(DataSource d) throws Exception { try (Connection c=d.getConnection(); Statement s=c.createStatement()) {
        s.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY,LOGINORG VARCHAR2(1),LOGINROLE VARCHAR2(1),LOGINTENANT VARCHAR2(1),LOGINAUTHMETHOD VARCHAR2(36),CAPTCHA VARCHAR2(1),ADDOMAINSUPPORT VARCHAR2(1),SHOWLOGINAUTHOPTION VARCHAR2(1),QCCODE VARCHAR2(1))");
        s.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY,MAXFAILEDATTEMPTS NUMBER,EXPIRYINTERVAL NUMBER,EXPIREDPOLICY VARCHAR2(36),LASTSUSPENDEDFLAG VARCHAR2(1))");
        s.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY,PASSWORD VARCHAR2(120),ORGID VARCHAR2(64),ORGNAME VARCHAR2(100),STATUS VARCHAR2(20),LASTSUSPENDEDFLAG VARCHAR2(1),EXPIRYDATE TIMESTAMP)");
        s.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY,ROLENAME VARCHAR2(100),LASTSUSPENDEDFLAG VARCHAR2(1))"); s.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER,USERID VARCHAR2(64))"); s.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER,PERMISSIONTYPEID VARCHAR2(64),TARGETID VARCHAR2(64))"); s.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)"); s.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64),PERMISSIONTYPEID VARCHAR2(64),TARGETID VARCHAR2(64))");
        s.execute("CREATE TABLE T_EMC_RUN (ID NUMBER PRIMARY KEY,RUNNO VARCHAR2(64),STATUS VARCHAR2(36),ORGID VARCHAR2(64))");
        s.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')"); s.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
        s.execute("INSERT INTO T_CORE_USER VALUES ('manager','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Org A','activated','0',NULL)"); s.execute("INSERT INTO T_CORE_USER VALUES ('reader','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Org A','activated','0',NULL)");
        s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'Manager','0')"); s.execute("INSERT INTO T_CORE_ROLE VALUES (2,'Reader','0')"); s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'manager')"); s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (2,'reader')"); s.execute("INSERT INTO T_CORE_MENU VALUES ('300100')"); s.execute("INSERT INTO T_CORE_MENU VALUES ('300101')"); s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','300100')"); s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','300101')"); s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (2,'T_CORE_MENU','300100')");
        s.execute("INSERT INTO T_EMC_RUN VALUES (1,'RUN-A','audit','org-a')"); s.execute("INSERT INTO T_EMC_RUN VALUES (2,'RUN-B','done','org-b')");
    }}
    private static String status(DataSource d, int id) throws Exception { try (Connection c=d.getConnection(); java.sql.PreparedStatement q=c.prepareStatement("SELECT STATUS FROM T_EMC_RUN WHERE ID=?")){q.setInt(1,id);try(java.sql.ResultSet r=q.executeQuery()){if(!r.next())throw new AssertionError("missing run");return r.getString(1);}}}
    private static void cleanup(DataSource d) throws Exception { try(Connection c=d.getConnection();Statement s=c.createStatement()){for(String t:new String[]{"T_EMC_RUN","T_CORE_ROLE_PERMISSION","T_CORE_ROLE_USER","T_CORE_MENU","T_CORE_ROLE","T_CORE_USER_PERMISSION","T_CORE_USER","T_CORE_PASSWORD_POLICY","T_CORE_LOGIN_CONFIG"})try{s.execute("DROP TABLE "+t+" CASCADE CONSTRAINTS");}catch(Exception ignored){}}}
    private static void check(boolean v,String label){if(!v)throw new AssertionError(label);}
    private static String login(int port,String user,String password)throws Exception{String key=request(port,"GET","/open/security/public-key","",null,200);PublicKey publicKey=KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");cipher.init(Cipher.ENCRYPT_MODE,publicKey);String enc=Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));return post(port,"/core/module/sys/login","id="+user+"&password="+ URLEncoder.encode(enc,"UTF-8"),key,200,null);}
    private static String post(int port,String path,String body,String key,int expected,String cookie)throws Exception{return request(port,"POST",path,body,cookie,expected,key);}
    private static String request(int port,String method,String path,String body,String cookie,int expected)throws Exception{return request(port,method,path,body,cookie,expected,null);}
    private static String request(int port,String method,String path,String body,String cookie,int expected,String rsa)throws Exception{HttpURLConnection c=(HttpURLConnection)new URL("http://127.0.0.1:"+port+path).openConnection();c.setRequestMethod(method);c.setRequestProperty("Content-Type",rsa==null?"application/json":"application/x-www-form-urlencoded");if(rsa!=null)c.setRequestProperty("SECURITY-RSA-KEY",rsa);if(cookie!=null)c.setRequestProperty("Cookie",cookie);if(!"GET".equals(method)){c.setDoOutput(true);byte[] b=body.getBytes(StandardCharsets.UTF_8);c.setFixedLengthStreamingMode(b.length);try(OutputStream o=c.getOutputStream()){o.write(b);}}int status=c.getResponseCode();if(status!=expected)throw new AssertionError(path+" status "+status+" expected "+expected);java.io.InputStream in=status>=400?c.getErrorStream():c.getInputStream();java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();if(in!=null){byte[] x=new byte[1024];int n;while((n=in.read(x))!=-1)out.write(x,0,n);}String result=new String(out.toByteArray(),StandardCharsets.UTF_8);String set=c.getHeaderField("Set-Cookie");c.disconnect();return set==null?result:set.substring(0,set.indexOf(';')<0?set.length():set.indexOf(';'));}
    @Configuration public static class ProbeConfiguration { static DataSource DATA_SOURCE; static com.sunwayworld.owned.internal.security.OwnedIdentityProvider PROVIDER; static final InMemoryAuditEventSink SINK=new InMemoryAuditEventSink(); @Bean public DataSource dataSource(){return DATA_SOURCE;} @Bean public OwnedIdentityHttpBoundary boundary(){return new OwnedIdentityHttpBoundary(PROVIDER,SINK);}@Bean public OwnedRsaLoginCredentialDecoder decoder(){return new OwnedRsaLoginCredentialDecoder();}@Bean public OwnedIdentityHttpController identity(OwnedIdentityHttpBoundary b,OwnedRsaLoginCredentialDecoder d){return new OwnedIdentityHttpController(b,d);}@Bean public OwnedSecurityKeyController key(OwnedRsaLoginCredentialDecoder d){return new OwnedSecurityKeyController(d);} }
    @Configuration public static class ResultConfiguration { @Bean public OwnedResultRunController read(OwnedIdentityHttpBoundary b,DataSource d){return new OwnedResultRunController(b,d);}@Bean public OwnedResultRunWriteController write(OwnedIdentityHttpBoundary b,DataSource d,Environment e){return new OwnedResultRunWriteController(b,d,e);} }
}
