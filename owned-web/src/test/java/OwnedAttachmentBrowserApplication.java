import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Synthetic setup and lifecycle only; all user routes come from packaged runtime. */
public final class OwnedAttachmentBrowserApplication {
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(args[0]))) { p.load(input); }
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(p.getProperty("url")); ds.setUser(p.getProperty("user")); ds.setPassword(p.getProperty("password"));
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
            s.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('allowed','Allowed User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('low','Low User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
            s.execute("CREATE TABLE T_EMC_HOME_ATTACHMENT (ID NUMBER PRIMARY KEY, ENDDATE TIMESTAMP, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), ACTIVATEDFLAG VARCHAR2(1), ACTIVATEDBYID VARCHAR2(64), ACTIVATEDBYNAME VARCHAR2(100), ACTIVATEDTIME TIMESTAMP)");
            s.execute("CREATE TABLE T_EMC_HOME_ATTA_PERMISSION (ID NUMBER PRIMARY KEY, ATTACHMENTID NUMBER, USERID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_FILE (ID NUMBER PRIMARY KEY, TARGETID VARCHAR2(100), NAME VARCHAR2(200), FILEEXT VARCHAR2(20), SCOPE VARCHAR2(40), MD5NAME VARCHAR2(64), CREATEDTIME TIMESTAMP, \"SIZE\" NUMBER)");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('900001')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','900001')");
            s.execute("INSERT INTO T_EMC_HOME_ATTACHMENT VALUES (1,SYSTIMESTAMP+INTERVAL '2' DAY,'allowed','Allowed User',TIMESTAMP '2026-10-20 10:00:00','org-a','Fixture Org A','1','allowed','Allowed User',SYSTIMESTAMP)");
            s.execute("INSERT INTO T_EMC_HOME_ATTA_PERMISSION VALUES (1,1,'allowed')");
            s.execute("INSERT INTO T_CORE_FILE VALUES (101,'T_EMC_HOME_ATTACHMENT$1','fixture.txt','txt','secure','0123456789abcdef0123456789abcdef',TIMESTAMP '2026-10-20 10:00:00',19)");
        }
        Path files = Paths.get(args[4]);
        Path fixture = Files.createDirectories(files.resolve("secure/20261020/0123456789abcdef0123456789abcdef")).resolve("fixture.txt");
        Files.write(fixture, "attachment fixture\n".getBytes(StandardCharsets.UTF_8));
        if (args.length > 5 && "--seed-only".equals(args[5])) {
            System.exit(0);
            return;
        }
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
            "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-browser-test",
            "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
            "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
            "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
            "--spring.datasource.url="+p.getProperty("url"), "--spring.datasource.username="+p.getProperty("user"),
            "--spring.datasource.password="+p.getProperty("password"), "--sunway.user.password.encoder.type=MD5",
            "--owned.attachments.enabled=true", "--owned.attachments.root="+files,
            "--owned.attachments.write.mode=isolated", "--owned.identity.attachment-menu-ids=900001",
            "--owned.identity.audit.type=file", "--owned.identity.audit.path="+args[3]});
        ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
        try {
            int port = ((ServletWebServerApplicationContext)app).getWebServer().getPort();
            Files.write(Paths.get(args[1]),String.valueOf(port).getBytes(StandardCharsets.UTF_8));
            long deadline = System.currentTimeMillis()+600000;
            while (!Files.exists(Paths.get(args[2])) && System.currentTimeMillis()<deadline) Thread.sleep(200);
        } finally { app.close(); }
        System.exit(0);
    }
}
