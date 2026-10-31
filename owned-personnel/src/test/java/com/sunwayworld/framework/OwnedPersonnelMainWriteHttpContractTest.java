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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Comparator;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.sql.DataSource;

/** Generic personnel master-table write seam; Oracle and HTTP are the public boundary. */
public final class OwnedPersonnelMainWriteHttpContractTest {
    private static int checks;
    private static final String BASE = "/secure/emc/module/mdm/emp/emps";
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        Path root = Files.createTempDirectory("owned-personnel-main-write-");
        Path audit = root.resolve("audit.jsonl");
        try {
            DataSource source = start(properties, audit, null);
            seed(source);
            String key = http(port(), "GET", "/open/security/public-key", null, null, null, 200).body;
            String encrypted = encrypt(key, "secret");
            String cookie = http(port(), "POST", "/core/module/sys/login", "id=e-1&password=" + encrypted,
                    null, "application/x-www-form-urlencoded", 200, key).cookie;
            check(cookie != null, "default mode login creates session");
            http(port(), "POST", BASE, body("e-default", "Default denied"), cookie, "application/json", 403);
            check(count(source, "e-default") == 0, "default mode leaves employee table unchanged");
            stop();

            source = start(properties, audit, "isolated");
            seed(source);
            key = http(port(), "GET", "/open/security/public-key", null, null, null, 200).body;
            encrypted = encrypt(key, "secret");
            cookie = http(port(), "POST", "/core/module/sys/login", "id=e-1&password=" + encrypted,
                    null, "application/x-www-form-urlencoded", 200, key).cookie;
            String inserted = http(port(), "POST", BASE, body("e-new", "Written employee"), cookie,
                    "application/json", 200).body.trim();
            check("e-new".equals(stripQuotes(inserted)), "generic POST returns employee id");
            check(count(source, "e-new") == 1 && "Written employee".equals(value(source, "e-new", "USERNAME")),
                    "generic POST commits scoped employee row");
            check("org-a".equals(value(source, "e-new", "ORGID")) && "e-1".equals(value(source, "e-new", "CREATEDBYID")),
                    "insert ignores client scope and records actor");

            String updated = http(port(), "PUT", BASE + "/e-new", body("e-new", "Updated employee"), cookie,
                    "application/json", 200).body.trim();
            check(updated.isEmpty(), "generic PUT returns empty response");
            check("Updated employee".equals(value(source, "e-new", "USERNAME")), "generic PUT updates in-scope row");
            http(port(), "PUT", BASE + "/e-3", body("e-3", "Cross scope"), cookie, "application/json", 403);
            check("Bob".equals(value(source, "e-3", "USERNAME")), "cross-organization PUT is unchanged");

            http(port(), "POST", BASE, body("e-rollback", null), cookie, "application/json", 400);
            check(count(source, "e-rollback") == 0, "invalid body rolls back insert");
            http(port(), "DELETE", BASE, "{\"b\":[\"\",[{\"id\":\"e-new\"}]]}", cookie, "application/json", 200);
            check(count(source, "e-new") == 0, "generic DELETE removes in-scope row");
            http(port(), "DELETE", BASE, "{\"b\":[\"\",[{\"id\":\"e-3\"}]]}", cookie, "application/json", 403);
            check(count(source, "e-3") == 1, "cross-organization DELETE is unchanged");
            String records = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
            check(records.contains("personnel-insert") && records.contains("personnel-update") && records.contains("personnel-delete"),
                    "main-table writes are audited after commit");
            check(!records.contains("secret") && !records.contains(properties.getProperty("password")),
                    "main-table audit omits credentials");
        } finally {
            stop();
            Files.walk(root).sorted(Comparator.reverseOrder()).forEach(path -> { try { Files.deleteIfExists(path); } catch (IOException failure) { throw new java.io.UncheckedIOException(failure); } });
        }
        System.out.println("personnel main write: PASS (" + checks + " checks; isolated Oracle loopback only)");
    }

    private static org.springframework.context.ConfigurableApplicationContext app;
    private static int port() { return ((org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext) app).getWebServer().getPort(); }
    private static DataSource start(Properties p, Path audit, String mode) {
        java.util.List<String> args = new java.util.ArrayList<String>();
        args.add("--server.address=127.0.0.1"); args.add("--server.port=0"); args.add("--spring.config.name=owned-personnel-main-write-test");
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
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE T_EMC_EMP'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
            s.execute("BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CORE'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;");
            s.execute("CREATE TABLE T_EMC_EMP (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), BIRTHDAY DATE, AGE NUMBER, ORGID VARCHAR2(64), MOBILE VARCHAR2(100), SEX VARCHAR2(10), FILTERTEST VARCHAR2(10), PHONE VARCHAR2(100), NATION VARCHAR2(100), STARTWORKDATE DATE, TITLEDATE DATE, PROFESSIONALTITLE VARCHAR2(100), POSITION VARCHAR2(100), REMARK VARCHAR2(500), ELECTRONICSIGNATURE VARCHAR2(255), PIC VARCHAR2(255), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,ORGID,CREATEDTIME) VALUES ('e-1','Alice','org-a',SYSTIMESTAMP)");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,ORGID,CREATEDTIME) VALUES ('e-3','Bob','org-b',SYSTIMESTAMP)");
            s.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
        }
    }
    private static int count(DataSource source, String id) throws SQLException { return Integer.parseInt(value(source, id, "COUNT(*)")); }
    private static String value(DataSource source, String id, String column) throws SQLException {
        String sql = "COUNT(*)".equals(column) ? "SELECT COUNT(*) FROM T_EMC_EMP WHERE ID=?" : "SELECT " + column + " FROM T_EMC_EMP WHERE ID=?";
        try (Connection c = source.getConnection(); java.sql.PreparedStatement q = c.prepareStatement(sql)) { q.setString(1, id); try (java.sql.ResultSet r = q.executeQuery()) { return r.next() ? r.getString(1) : null; } }
    }
    private static String body(String id, String name) { return "{\"b\":[\"\",[{\"id\":\"" + id + "\",\"userName\":" + (name == null ? "null" : "\"" + name + "\"") + "}]]}"; }
    private static String encrypt(String key, String plain) throws Exception { Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key))); cipher.init(Cipher.ENCRYPT_MODE, publicKey); return java.net.URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8))), "UTF-8"); }
    private static String stripQuotes(String body) { return body.startsWith("\"") && body.endsWith("\"") ? body.substring(1, body.length() - 1) : body; }
    private static Reply http(int port, String method, String path, String body, String cookie, String type, int expected) throws Exception { return http(port, method, path, body, cookie, type, expected, null); }
    private static Reply http(int port, String method, String path, String body, String cookie, String type, int expected, String key) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection(); c.setRequestMethod(method); c.setConnectTimeout(5000); c.setReadTimeout(10000); c.setInstanceFollowRedirects(false); if (cookie != null) c.setRequestProperty("Cookie", cookie); if (key != null) c.setRequestProperty("SECURITY-RSA-KEY", key); if (body != null) { c.setDoOutput(true); c.setRequestProperty("Content-Type", type); c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8)); } int status = c.getResponseCode(); java.io.InputStream in = status >= 400 ? c.getErrorStream() : c.getInputStream(); String text = in == null ? "" : new String(read(in), StandardCharsets.UTF_8); check(status == expected, method + " " + path + " expected=" + expected + " actual=" + status + " body=" + text); String set = c.getHeaderField("Set-Cookie"); return new Reply(text, set == null ? cookie : set.split(";", 2)[0]);
    }
    private static byte[] read(java.io.InputStream in) throws IOException { java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(); byte[] b = new byte[4096]; for (int n; (n = in.read(b)) != -1;) out.write(b, 0, n); return out.toByteArray(); }
    private static final class Reply { final String body, cookie; Reply(String body, String cookie) { this.body = body; this.cookie = cookie; } }
}
