package com.sunwayworld.framework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Comparator;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.sql.DataSource;

/** Isolated qualification write contract; production/default mode remains fail-closed. */
public final class OwnedPersonnelQualificationWriteHttpContractTest {
    private static int checks;
    private static final String BASE = "/secure/emc/module/mdm/emp/emps";
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        Path root = Files.createTempDirectory("owned-personnel-write-");
        Path audit = root.resolve("audit.jsonl");
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-personnel-write-test",
                "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
                "--spring.datasource.url=" + properties.getProperty("url"),
                "--spring.datasource.username=" + properties.getProperty("user"),
                "--spring.datasource.password=" + properties.getProperty("password"),
                "--sunway.user.password.encoder.type=MD5",
                "--owned.identity.audit.type=file", "--owned.identity.audit.path=" + audit.toAbsolutePath(),
                "--owned.personnel.write.mode=isolated"});
        org.springframework.context.ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
        int port = ((org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext) app).getWebServer().getPort();
        try {
            DataSource dataSource = app.getBean(DataSource.class);
            seedWriteFixture(dataSource);
            String key = http(port, "GET", "/open/security/public-key", null, null, null, 200).body;
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String encrypted = URLEncoder.encode(Base64.getEncoder().encodeToString(
                    cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))), "UTF-8");
            String cookie = http(port, "POST", "/core/module/sys/login", "id=e-1&password=" + encrypted,
                    null, null, 200, key).cookie;
            check(cookie != null, "isolated write login creates session");
            String body = "{\"b\":[\"\",[{\"qualNo\":\"Q-WRITE\",\"qualName\":\"Written permit\",\"qualOrg\":\"Fixture issuer\",\"validDate\":\"2026-01-01\",\"authDate\":\"2026-01-02\",\"expireDate\":\"2027-01-01\",\"warningDays\":30}]]}";
            Reply inserted = http(port, "POST", BASE + "/e-1/quals", body, cookie, "application/json", 200);
            long id = Long.parseLong(inserted.body.trim());
            check(id >= 1000L, "qualification uses owned Oracle sequence");
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT EMPID,QUALNAME,CREATEDBYID,WARNINGDAYS FROM T_EMC_EMP_QUAL WHERE ID=" + id)) {
                check(rows.next() && "e-1".equals(rows.getString(1)) && "Written permit".equals(rows.getString(2))
                        && "e-1".equals(rows.getString(3)) && rows.getLong(4) == 30L, "qualification write stores scoped fields and audit actor");
            }
            http(port, "POST", BASE + "/e-3/quals", body, cookie, "application/json", 403);
            check(http(port, "DELETE", BASE + "/e-1/quals", "{\"b\":[\"\",[{\"id\":" + id + "}]]}", cookie,
                    "application/json", 200).body.trim().isEmpty(), "qualification delete commits through original route");
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EMP_QUAL WHERE ID=" + id)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted qualification is absent");
            }
            String trainingBody = "{\"b\":[\"\",[{\"trainTime\":\"2026-01-01\",\"trainContent\":\"Safety refresher\",\"trainResult\":\"passed\",\"courseCode\":\"SAFE-1\",\"courseName\":\"Safety refresher\"}]]}";
            http(port, "POST", BASE + "/e-3/trains", trainingBody, cookie, "application/json", 403);
            http(port, "POST", BASE + "/e-1/trains", trainingBody, cookie, "application/json", 200);
            long trainingId;
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT ID,EMPID,TRAINCONTENT,CREATEDBYID FROM T_EMC_EMP_TRAIN WHERE EMPID='e-1' ORDER BY ID DESC")) {
                check(rows.next() && "e-1".equals(rows.getString(2)) && "Safety refresher".equals(rows.getString(3))
                        && "e-1".equals(rows.getString(4)) && rows.getLong(1) >= 1000L, "training write stores scoped fields, sequence and actor");
                trainingId = rows.getLong(1);
            }
            String invalidTraining = "{\"b\":[\"\",[{\"trainContent\":\"valid row\"},{\"trainContent\":\"" + repeat('x', 201) + "\"}]]}";
            http(port, "POST", BASE + "/e-1/trains", invalidTraining, cookie, "application/json", 400);
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EMP_TRAIN WHERE EMPID='e-1' AND TRAINCONTENT='valid row'")) {
                rows.next(); check(rows.getInt(1) == 0, "invalid training batch rolls back all rows");
            }
            http(port, "DELETE", BASE + "/e-1/trains", "{\"b\":[\"\",[{\"id\":802}]]}", cookie, "application/json", 403);
            check(http(port, "DELETE", BASE + "/e-1/trains", "{\"b\":[\"\",[{\"id\":" + trainingId + "}]]}", cookie,
                    "application/json", 200).body.trim().isEmpty(), "training delete commits through original route");
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EMP_TRAIN WHERE ID=" + trainingId)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted training is absent");
            }
            String methodBody = "{\"b\":[\"\",[{\"testId\":601,\"methodId\":801,\"testMethodId\":501,\"type\":\"analysis\",\"remark\":\"Certified\"}]]}";
            http(port, "PUT", BASE + "/301/cert-test-methods", methodBody, cookie, "application/json", 200);
            long certMethodId;
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT ID,CERTID,TESTMETHODID,CREATEDBYID FROM T_EMC_CERT_TEST_METHOD WHERE CERTID=301 AND TESTMETHODID=501")) {
                check(rows.next() && rows.getLong(2) == 301L && rows.getLong(3) == 501L && "e-1".equals(rows.getString(4)), "certificate method write stores scoped fields and actor");
                certMethodId = rows.getLong(1);
            }
            check(http(port, "DELETE", BASE + "/cert-test-methods", "{\"b\":[\"\",[{\"id\":" + certMethodId + "}]]}", cookie, "application/json", 200).body.trim().isEmpty(), "certificate method delete commits through original route");
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_CERT_TEST_METHOD WHERE ID=" + certMethodId)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted certificate method is absent");
            }
            String records = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
            check(records.contains("personnel-qualification-insert") && records.contains("personnel-qualification-delete")
                    && records.contains("personnel-cert-test-method-insert") && records.contains("personnel-cert-test-method-delete"), "qualification and certificate method writes are audited");
            check(!records.contains("secret") && !records.contains(properties.getProperty("password")), "write audit omits credentials");
        } finally {
            app.close();
            Files.walk(root).sorted(Comparator.reverseOrder()).forEach(path -> { try { Files.deleteIfExists(path); } catch (IOException failure) { throw new java.io.UncheckedIOException(failure); } });
        }
        System.out.println("personnel qualification write: PASS (" + checks + " checks; isolated Oracle loopback only)");
    }

    private static void seedWriteFixture(DataSource source) throws SQLException {
        try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP_QUAL'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            statement.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_CERT_TEST_METHOD'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            statement.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP_TRAIN'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            statement.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_CERT'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            statement.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            statement.execute("BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CORE'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;");
            statement.execute("CREATE TABLE T_EMC_EMP_QUAL (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), QUALNO VARCHAR2(100), QUALNAME VARCHAR2(200), QUALORG VARCHAR2(200), VALIDDATE DATE, AUTHDATE DATE, EXPIREDATE DATE, WARNINGDAYS NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EMP (ID VARCHAR2(64) PRIMARY KEY, ORGID VARCHAR2(64))");
            statement.execute("CREATE TABLE T_EMC_CERT (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64))");
            statement.execute("CREATE TABLE T_EMC_CERT_TEST_METHOD (ID NUMBER PRIMARY KEY, CERTID NUMBER, TESTID NUMBER, METHODID NUMBER, REMARK VARCHAR2(100), TESTMETHODID NUMBER, ITEMGROUP VARCHAR2(64), TYPE VARCHAR2(32), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(64), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EMP_TRAIN (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), TRAINTIME DATE, TRAINCONTENT VARCHAR2(200), TRAINRESULT VARCHAR2(100), COURSECODE VARCHAR2(64), COURSENAME VARCHAR2(128), TRAINDEPT VARCHAR2(128), TRAINDEPTCODE VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), TRAINBEGINTIME DATE, TRAINENDTIME DATE)");
            statement.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
            statement.execute("INSERT INTO T_EMC_EMP (ID,ORGID) VALUES ('e-1','org-a')");
            statement.execute("INSERT INTO T_EMC_CERT (ID,EMPID) VALUES (301,'e-1')");
            statement.execute("INSERT INTO T_EMC_EMP_QUAL (ID,EMPID,QUALNAME) VALUES (101,'e-3','Hidden permit')");
            statement.execute("INSERT INTO T_EMC_EMP_TRAIN (ID,EMPID,TRAINCONTENT) VALUES (802,'e-3','Hidden training')");
        }
    }

    private static String repeat(char value, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; i++) result.append(value);
        return result.toString();
    }

    private static Reply http(int port, String method, String path, String body, String cookie, String type, int expected) throws Exception {
        return http(port, method, path, body, cookie, type, expected, null);
    }
    private static Reply http(int port, String method, String path, String body, String cookie, String type, int expected, String rsaKey) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        connection.setRequestMethod(method); connection.setConnectTimeout(5000); connection.setReadTimeout(10000);
        connection.setInstanceFollowRedirects(false); if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        if (rsaKey != null) connection.setRequestProperty("SECURITY-RSA-KEY", rsaKey);
        if (body != null) { connection.setDoOutput(true); connection.setRequestProperty("Content-Type", type == null ? "application/x-www-form-urlencoded" : type); connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8)); }
        int status = connection.getResponseCode(); java.io.InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
        String text = stream == null ? "" : new String(read(stream), StandardCharsets.UTF_8); check(status == expected, method + " " + path + " HTTP " + expected + " actual=" + status + " body=" + text);
        String set = connection.getHeaderField("Set-Cookie");
        return new Reply(status, text, set == null ? cookie : set.split(";", 2)[0]);
    }
    private static byte[] read(java.io.InputStream input) throws IOException { java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(); byte[] buffer = new byte[4096]; int size; while ((size = input.read(buffer)) != -1) out.write(buffer, 0, size); return out.toByteArray(); }
    private static final class Reply { final int status; final String body, cookie; Reply(int status, String body, String cookie) { this.status = status; this.body = body; this.cookie = cookie; } }
}
