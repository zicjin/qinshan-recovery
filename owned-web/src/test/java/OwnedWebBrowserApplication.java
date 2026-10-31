import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;
import javax.sql.DataSource;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Test lifecycle only; all page, identity and data routes are shipped runtime. */
public final class OwnedWebBrowserApplication {
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(args[0]))) { p.load(input); }
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
            "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-browser-test",
            "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
            "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
            "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
            "--spring.datasource.url="+p.getProperty("url"), "--spring.datasource.username="+p.getProperty("user"),
            "--spring.datasource.password="+p.getProperty("password"), "--sunway.user.password.encoder.type=MD5",
            "--owned.identity.audit.type=file", "--owned.identity.audit.path="+args[3],
            "--owned.personnel.write.mode=isolated"});
        ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
        try {
            DataSource ds = app.getBean(DataSource.class);
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
                s.execute("INSERT INTO T_CORE_USER (ID,PASSWORD,ORGID,ORGNAME,STATUS,LASTSUSPENDEDFLAG,PARENTORGID) VALUES ('e-4','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0','org-a')");
            }
            // Extra, explicitly synthetic employees exercise real UI pagination.
            try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(
                    "INSERT INTO T_EMC_EMP (ID,USERNAME,CREATEDTIME,ORGID,AGE,REMARK) VALUES (?,?,TIMESTAMP '2025-01-01 00:00:00','org-a',25,'synthetic pagination fixture')")) {
                for (int i=1;i<=21;i++) { s.setString(1,String.format("page-%03d",i)); s.setString(2,String.format("Fixture %03d",i)); s.executeUpdate(); }
            }
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
                s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP_QUAL'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
                s.execute("CREATE TABLE T_EMC_EMP_QUAL (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), QUALNO VARCHAR2(100), QUALNAME VARCHAR2(200), QUALORG VARCHAR2(200), VALIDDATE DATE, AUTHDATE DATE, EXPIREDATE DATE, WARNINGDAYS NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
                s.execute("INSERT INTO T_EMC_EMP_QUAL (ID,EMPID,QUALNO,QUALNAME,QUALORG,AUTHDATE,EXPIREDATE,WARNINGDAYS) VALUES (101,'e-1','Q-101','Browser qualification','Fixture issuer',DATE '2026-01-01',DATE '2027-01-01',30)");
                s.execute("CREATE TABLE T_CORE_FILE (ID NUMBER PRIMARY KEY, NAME VARCHAR2(200), TARGETID VARCHAR2(100))");
                s.execute("CREATE TABLE T_EMC_EMP_TEST (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), TESTID NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
                s.execute("CREATE TABLE T_EMC_TEST_METHOD (ID NUMBER PRIMARY KEY, TESTID NUMBER, TESTCATEGORYID NUMBER, METHODID NUMBER, TESTCLASS VARCHAR2(32))");
                s.execute("CREATE TABLE T_EMC_TEST_CATEGORY (ID NUMBER PRIMARY KEY, TESTCATEGORYDESC VARCHAR2(64))");
                s.execute("CREATE TABLE T_EMC_TEST (ID NUMBER PRIMARY KEY, TESTNAME VARCHAR2(64))");
                s.execute("CREATE TABLE T_EMC_METHOD (ID NUMBER PRIMARY KEY, METHODNAME VARCHAR2(64), TESTMETHODBASIS VARCHAR2(100))");
                s.execute("INSERT INTO T_EMC_EMP_TEST (ID,EMPID,TESTID) VALUES (201,'e-2',501)");
                s.execute("INSERT INTO T_EMC_TEST_METHOD VALUES (501,601,701,801,'metals')");
                s.execute("INSERT INTO T_EMC_TEST_METHOD VALUES (502,602,701,801,'organics')");
                s.execute("INSERT INTO T_EMC_TEST_CATEGORY VALUES (701,'Water')");
                s.execute("INSERT INTO T_EMC_TEST VALUES (601,'Water metals')");
                s.execute("INSERT INTO T_EMC_TEST VALUES (602,'Water organics')");
                s.execute("INSERT INTO T_EMC_METHOD VALUES (801,'Fixture method','Fixture basis')");
                for (int i=503;i<=523;i++) {
                    s.execute("INSERT INTO T_EMC_TEST_METHOD VALUES ("+i+",602,701,801,'organics')");
                }
            }
            int port = ((ServletWebServerApplicationContext)app).getWebServer().getPort();
            Files.write(Paths.get(args[1]),String.valueOf(port).getBytes(StandardCharsets.UTF_8));
            long deadline = System.currentTimeMillis()+600000;
            while (!Files.exists(Paths.get(args[2])) && System.currentTimeMillis()<deadline) Thread.sleep(200);
        } finally { app.close(); }
        System.exit(0);
    }
}
