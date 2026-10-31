package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Comparator;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.sql.DataSource;

/** Generic visible-test assignment/delete boundary; Oracle and HTTP are the public seam. */
public final class OwnedPersonnelVisibleTestWriteHttpContractTest {
    private static int checks;
    private static final String BASE = "/secure/emc/module/mdm/emp/emps";
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        Path root = Files.createTempDirectory("owned-personnel-test-write-");
        Path audit = root.resolve("audit.jsonl");
        try {
            DataSource source = start(properties, audit, null);
            seed(source);
            String key = http(port(), "GET", "/open/security/public-key", null, null, 200).body;
            String cookie = login(key, "e-1");
            http(port(), "GET", "/owned/personnel/e-1/unassigned-tests", null, cookie, 403);
            http(port(), "POST", BASE + "/e-1/tests", body(501L), cookie, 403);
            check(count(source, "e-1", 501L) == 0, "default mode leaves visible-test rows unchanged");
            stop();

            source = start(properties, audit, "isolated");
            seed(source);
            key = http(port(), "GET", "/open/security/public-key", null, null, 200).body;
            cookie = login(key, "e-1");
            String choices = http(port(), "GET", "/owned/personnel/e-1/unassigned-tests?n=1&s=20", null, cookie, 200).body;
            check(choices.contains("Water metals") && choices.contains("501"), "isolated test-method picker returns unassigned joined projection");
            http(port(), "POST", BASE + "/e-1/tests", body(501L), cookie, 200);
            long id = latestId(source, "e-1", 501L);
            check(id >= 1000L && count(source, "e-1", 501L) == 1, "visible-test insert uses SEQ_CORE and employee scope");
            check("e-1".equals(actor(source, id)), "visible-test insert stores server actor");
            http(port(), "POST", BASE + "/e-3/tests", body(501L), cookie, 403);
            check(count(source, "e-3", 501L) == 0, "cross-organization visible-test insert is unchanged");
            http(port(), "POST", BASE + "/e-1/tests", body(null), cookie, 400);
            check(count(source, "e-1", 501L) == 1, "invalid visible-test body rolls back");
            check(http(port(), "DELETE", BASE + "/e-1/tests", deleteBody(id), cookie, 200).body.trim().isEmpty(),
                    "visible-test delete commits through original route");
            check(count(source, "e-1", 501L) == 0, "visible-test delete removes assigned row");
            insertDirect(source, 2001L, "e-3", 501L);
            http(port(), "DELETE", BASE + "/e-1/tests", deleteBody(2001L), cookie, 403);
            check(countId(source, 2001L) == 1, "cross-employee visible-test delete is unchanged");
            String records = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
            check(records.contains("personnel-test-insert") && records.contains("personnel-test-delete"),
                    "visible-test writes are audited after commit");
            check(!records.contains("secret") && !records.contains(properties.getProperty("password")),
                    "visible-test audit omits credentials");
        } finally {
            stop();
            Files.walk(root).sorted(Comparator.reverseOrder()).forEach(path -> {
                try { Files.deleteIfExists(path); } catch (IOException failure) { throw new java.io.UncheckedIOException(failure); }
            });
        }
        System.out.println("personnel visible-test write: PASS (" + checks + " checks; isolated Oracle loopback only)");
    }

    private static org.springframework.context.ConfigurableApplicationContext app;
    private static int port() { return ((org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext) app).getWebServer().getPort(); }
    private static DataSource start(Properties p, Path audit, String mode) {
        java.util.List<String> args = new java.util.ArrayList<String>();
        args.add("--server.address=127.0.0.1"); args.add("--server.port=0"); args.add("--spring.config.name=owned-personnel-test-write-test");
        args.add("--spring.main.banner-mode=off"); args.add("--spring.main.log-startup-info=false");
        args.add("--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration");
        args.add("--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource");
        args.add("--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver");
        args.add("--spring.datasource.url=" + p.getProperty("url")); args.add("--spring.datasource.username=" + p.getProperty("user")); args.add("--spring.datasource.password=" + p.getProperty("password"));
        args.add("--sunway.user.password.encoder.type=MD5"); args.add("--owned.identity.audit.type=file"); args.add("--owned.identity.audit.path=" + audit.toAbsolutePath());
        if (mode != null) args.add("--owned.personnel.write.mode=" + mode);
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(args.toArray(new String[0]));
        app = OwnedApplicationContext.getApplicationContext();
        return app.getBean(DataSource.class);
    }
    private static void stop() { if (app != null) { app.close(); app = null; } }
    private static void seed(DataSource source) throws SQLException {
        try (Connection c = source.getConnection(); Statement s = c.createStatement()) {
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP_TEST'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_TEST_METHOD'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_TEST_CATEGORY'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_TEST'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_METHOD'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CORE'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;");
            s.execute("CREATE TABLE T_EMC_EMP_TEST (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), TESTID NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_TEST_METHOD (ID NUMBER PRIMARY KEY, TESTID NUMBER, TESTCATEGORYID NUMBER, METHODID NUMBER, TESTCLASS VARCHAR2(32))");
            s.execute("CREATE TABLE T_EMC_TEST_CATEGORY (ID NUMBER PRIMARY KEY, TESTCATEGORYDESC VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_TEST (ID NUMBER PRIMARY KEY, TESTNAME VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_METHOD (ID NUMBER PRIMARY KEY, METHODNAME VARCHAR2(64), TESTMETHODBASIS VARCHAR2(100))");
            s.execute("INSERT INTO T_EMC_TEST_METHOD VALUES (501,601,701,801,'metals')");
            s.execute("INSERT INTO T_EMC_TEST_CATEGORY VALUES (701,'Water')");
            s.execute("INSERT INTO T_EMC_TEST VALUES (601,'Water metals')");
            s.execute("INSERT INTO T_EMC_METHOD VALUES (801,'ICP-MS','Fixture basis')");
            s.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
        }
    }
    private static void insertDirect(DataSource source, long id, String employee, long test) throws SQLException {
        try (Connection c = source.getConnection(); java.sql.PreparedStatement q = c.prepareStatement("INSERT INTO T_EMC_EMP_TEST (ID,EMPID,TESTID) VALUES (?,?,?)")) {
            q.setLong(1, id); q.setString(2, employee); q.setLong(3, test); q.executeUpdate();
        }
    }
    private static int count(DataSource source, String employee, long test) throws SQLException {
        try (Connection c = source.getConnection(); java.sql.PreparedStatement q = c.prepareStatement("SELECT COUNT(*) FROM T_EMC_EMP_TEST WHERE EMPID=? AND TESTID=?")) {
            q.setString(1, employee); q.setLong(2, test); try (ResultSet r = q.executeQuery()) { r.next(); return r.getInt(1); }
        }
    }
    private static int countId(DataSource source, long id) throws SQLException {
        try (Connection c = source.getConnection(); java.sql.PreparedStatement q = c.prepareStatement("SELECT COUNT(*) FROM T_EMC_EMP_TEST WHERE ID=?")) {
            q.setLong(1, id); try (ResultSet r = q.executeQuery()) { r.next(); return r.getInt(1); }
        }
    }
    private static long latestId(DataSource source, String employee, long test) throws SQLException {
        try (Connection c = source.getConnection(); java.sql.PreparedStatement q = c.prepareStatement("SELECT MAX(ID) FROM T_EMC_EMP_TEST WHERE EMPID=? AND TESTID=?")) {
            q.setString(1, employee); q.setLong(2, test);
            try (ResultSet r = q.executeQuery()) { r.next(); return r.getLong(1); }
        }
    }
    private static String actor(DataSource source, long id) throws SQLException {
        try (Connection c = source.getConnection(); java.sql.PreparedStatement q = c.prepareStatement("SELECT CREATEDBYID FROM T_EMC_EMP_TEST WHERE ID=?")) {
            q.setLong(1, id); try (ResultSet r = q.executeQuery()) { return r.next() ? r.getString(1) : null; }
        }
    }
    private static String body(Long testId) { return "{\"b\":[\"\",[{\"testId\":" + (testId == null ? "null" : testId) + "}]]}"; }
    private static String deleteBody(long id) { return "{\"b\":[\"\",[{\"id\":" + id + "}]]}"; }
    private static String login(String key, String user) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String encrypted = java.net.URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))), "UTF-8");
        return http(port(), "POST", "/core/module/sys/login", "id=" + user + "&password=" + encrypted, null, 200, key).cookie;
    }
    private static Reply http(int port, String method, String path, String body, String cookie, int expected) throws Exception { return http(port, method, path, body, cookie, expected, null); }
    private static Reply http(int port, String method, String path, String body, String cookie, int expected, String key) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection(); c.setRequestMethod(method); c.setConnectTimeout(5000); c.setReadTimeout(10000); c.setInstanceFollowRedirects(false);
        if (cookie != null) c.setRequestProperty("Cookie", cookie); if (key != null) c.setRequestProperty("SECURITY-RSA-KEY", key);
        if (body != null) { c.setDoOutput(true); c.setRequestProperty("Content-Type", key == null ? "application/json" : "application/x-www-form-urlencoded"); c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8)); }
        int status = c.getResponseCode(); java.io.InputStream stream = status >= 400 ? c.getErrorStream() : c.getInputStream();
        String text = stream == null ? "" : new String(read(stream), StandardCharsets.UTF_8); check(status == expected, method + " " + path + " expected=" + expected + " actual=" + status + " body=" + text);
        String set = c.getHeaderField("Set-Cookie"); return new Reply(text, set == null ? cookie : set.split(";", 2)[0]);
    }
    private static byte[] read(java.io.InputStream in) throws IOException { java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(); byte[] b = new byte[4096]; for (int n; (n = in.read(b)) != -1;) out.write(b, 0, n); return out.toByteArray(); }
    private static final class Reply { final String body, cookie; Reply(String body, String cookie) { this.body = body; this.cookie = cookie; } }
}
