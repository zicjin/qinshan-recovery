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
import java.util.stream.Stream;
import javax.sql.DataSource;
import javax.crypto.Cipher;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Original attachment resource and historical main with a disposable Oracle. */
public final class OwnedAttachmentRuntimeHttpTest {
    private static final String BASE = "/secure/emc/module/item/home-attachments";
    private static int checks;
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { p.load(input); }
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
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('100001')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100001')");
            s.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
            s.execute("CREATE TABLE T_EMC_HOME_ATTACHMENT (ID NUMBER PRIMARY KEY, ENDDATE TIMESTAMP, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), ACTIVATEDFLAG VARCHAR2(1), ACTIVATEDBYID VARCHAR2(64), ACTIVATEDBYNAME VARCHAR2(100), ACTIVATEDTIME TIMESTAMP)");
            s.execute("CREATE TABLE T_EMC_HOME_ATTA_PERMISSION (ID NUMBER PRIMARY KEY, ATTACHMENTID NUMBER, USERID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_FILE (ID NUMBER PRIMARY KEY, TARGETID VARCHAR2(100), NAME VARCHAR2(200), FILEEXT VARCHAR2(20), SCOPE VARCHAR2(40), MD5NAME VARCHAR2(64), CREATEDTIME TIMESTAMP, \"SIZE\" NUMBER)");
            s.execute("INSERT INTO T_EMC_HOME_ATTACHMENT VALUES (1,SYSTIMESTAMP+INTERVAL '2' DAY,'allowed','Allowed User',TIMESTAMP '2026-10-20 10:00:00','org-a','Fixture Org A','1','allowed','Allowed User',SYSTIMESTAMP)");
            s.execute("INSERT INTO T_EMC_HOME_ATTA_PERMISSION VALUES (1,1,'allowed')");
            s.execute("INSERT INTO T_CORE_FILE VALUES (101,'T_EMC_HOME_ATTACHMENT$1','fixture.txt','txt','secure','0123456789abcdef0123456789abcdef',TIMESTAMP '2026-10-20 10:00:00',19)");
        }
        java.nio.file.Path files=java.nio.file.Files.createDirectories(java.nio.file.Paths.get(args[1]).getParent().resolve("files"));
        java.nio.file.Path fixture=java.nio.file.Files.createDirectories(files.resolve("secure/20261020/0123456789abcdef0123456789abcdef")).resolve("fixture.txt");
        java.nio.file.Files.write(fixture,"attachment fixture\n".getBytes(StandardCharsets.UTF_8));
        GikamWpjcApplication.main(new String[]{"--server.address=127.0.0.1", "--server.port=0",
            "--spring.config.name=owned-attachments-runtime-test", "--spring.main.banner-mode=off",
            "--spring.main.log-startup-info=false", "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
            "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource", "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
            "--spring.datasource.url=" + p.getProperty("url"), "--spring.datasource.username=" + p.getProperty("user"),
            "--spring.datasource.password=" + p.getProperty("password"), "--sunway.user.password.encoder.type=MD5",
            "--owned.attachments.enabled=true", "--owned.attachments.root="+files, "--owned.attachments.write.mode=isolated", "--owned.identity.attachment-menu-ids=100001", "--owned.identity.audit.type=file", "--owned.identity.audit.path=" + args[1]});
        ServletWebServerApplicationContext app = (ServletWebServerApplicationContext) OwnedApplicationContext.getApplicationContext();
        try {
            int port = app.getWebServer().getPort();
            String key = http(port, "GET", "/open/security/public-key", null, null, null, 200).body;
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key))));
            String password = URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))), "UTF-8");
            String cookie = http(port, "POST", "/core/module/sys/login", "id=allowed&password=" + password, null, key, 200).cookie;
            check(cookie != null, "RSA login creates a real session");
            String createdBody = "{\"b\":[\"\",[{\"activatedFlag\":\"0\",\"endDate\":\"2030-01-01T00:00:00\"}]]}";
            long createdId = Long.parseLong(http(port, "POST", BASE, createdBody, cookie, null, 200).body.trim());
            try (Connection c = ds.getConnection(); Statement s = c.createStatement(); ResultSet rows = s.executeQuery("SELECT CREATEDBYID,CREATEDBYORGID,ACTIVATEDFLAG FROM T_EMC_HOME_ATTACHMENT WHERE ID=" + createdId)) {
                check(rows.next() && "allowed".equals(rows.getString(1)) && "org-a".equals(rows.getString(2)) && "0".equals(rows.getString(3)), "metadata insert stores actor, scope and inactive state");
            }
            http(port, "POST", BASE + "/permissions", "{\"b\":[\"\",[{\"attachmentId\":" + createdId + ",\"userId\":\"low\"},{\"attachmentId\":" + createdId + ",\"userId\":\"allowed\"}]]}", cookie, null, 200);
            check(JSON.parseObject(http(port, "POST", BASE + "/" + createdId + "/permissions/queries", "{}", cookie, null, 200).body).getIntValue("total") == 2, "permission insert and list are scoped");
            multipartUpload(port, "T_EMC_HOME_ATTACHMENT$" + createdId, "attachment-v1.txt", "new attachment\n".getBytes(StandardCharsets.UTF_8), cookie, 200);
            java.nio.file.Path firstUpload = storedPath(ds, createdId, files);
            check(java.nio.file.Files.isRegularFile(firstUpload), "first uploaded physical file exists");
            http(port, "PUT", BASE + "/action/activate", "{\"b\":[\"\",[{\"id\":" + createdId + "}]]}", cookie, null, 200);
            try (Connection c = ds.getConnection(); Statement s = c.createStatement(); ResultSet rows = s.executeQuery("SELECT ACTIVATEDFLAG,ACTIVATEDBYID FROM T_EMC_HOME_ATTACHMENT WHERE ID=" + createdId)) { rows.next(); check("1".equals(rows.getString(1)) && "allowed".equals(rows.getString(2)), "activation records actor"); }
            String createdUrl = http(port, "POST", BASE + "/" + createdId + "/action/get-url", "{}", cookie, null, 200).body.trim();
            check(http(port, "GET", createdUrl, null, cookie, null, 200).body.equals("new attachment\n"), "uploaded attachment bytes are protected and readable");
            multipartUpload(port, "T_EMC_HOME_ATTACHMENT$" + createdId, "attachment-v2.txt", "replacement attachment\n".getBytes(StandardCharsets.UTF_8), cookie, 200);
            java.nio.file.Path replacementFile = storedPath(ds, createdId, files);
            check(!java.nio.file.Files.exists(firstUpload) && java.nio.file.Files.isRegularFile(replacementFile), "replacement removes stale physical file after commit");
            String replacementUrl = http(port, "POST", BASE + "/" + createdId + "/action/get-url", "{}", cookie, null, 200).body.trim();
            check(http(port, "GET", replacementUrl, null, cookie, null, 200).body.equals("replacement attachment\n"), "upload replaces the attachment metadata and bytes");
            Set<String> beforeFailure = regularFiles(files);
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
                s.execute("ALTER TABLE T_CORE_FILE ADD CONSTRAINT OWNED_ATTACHMENT_FAIL CHECK (NAME <> 'attachment-fail.txt')");
            }
            multipartUpload(port, "T_EMC_HOME_ATTACHMENT$" + createdId, "attachment-fail.txt", "failed replacement\n".getBytes(StandardCharsets.UTF_8), cookie, 500);
            check(java.nio.file.Files.isRegularFile(replacementFile), "failed metadata transaction preserves current physical file");
            check(beforeFailure.equals(regularFiles(files)), "failed metadata transaction leaves no orphan physical file");
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) { s.execute("ALTER TABLE T_CORE_FILE DROP CONSTRAINT OWNED_ATTACHMENT_FAIL"); }
            http(port, "PUT", BASE + "/action/deactivate", "{\"b\":[\"\",[{\"id\":" + createdId + "}]]}", cookie, null, 200);
            long permissionId;
            try (Connection c = ds.getConnection(); Statement s = c.createStatement(); ResultSet rows = s.executeQuery("SELECT ID FROM T_EMC_HOME_ATTA_PERMISSION WHERE ATTACHMENTID=" + createdId)) { rows.next(); permissionId = rows.getLong(1); }
            http(port, "DELETE", BASE + "/permissions", "{\"b\":[\"\",[{\"id\":" + permissionId + ",\"attachmentId\":" + createdId + ",\"userId\":\"low\"}]]}", cookie, null, 200);
            http(port, "DELETE", BASE + "", "{\"b\":[\"\",[{\"id\":" + createdId + "}]]}", cookie, null, 200);
            try (Connection c = ds.getConnection(); Statement s = c.createStatement(); ResultSet rows = s.executeQuery("SELECT COUNT(1) FROM T_EMC_HOME_ATTACHMENT WHERE ID=" + createdId)) { rows.next(); check(rows.getInt(1) == 0, "metadata delete removes attachment row"); }
            check(!java.nio.file.Files.exists(replacementFile), "metadata delete removes physical file after commit");
            check(http(port, "GET", BASE + "/page/edit-list", null, cookie, null, 200).body.contains("emc-home-attachment-edit-list.js"), "original attachment page entry renders");
            JSONObject page = JSON.parseObject(http(port, "POST", BASE + "/queries/raw", "{}", cookie, null, 200).body);
            check(page.getIntValue("total") == 1, "authorized attachment list is exposed through original resource");
            String url = http(port, "POST", BASE + "/1/action/get-url", "{}", cookie, null, 200).body.trim();
            check(url.equals("/files/secure/20261020/0123456789abcdef0123456789abcdef/fixture.txt"), "get-url returns a protected content URL");
            Reply downloaded = http(port, "GET", url, null, cookie, null, 200);
            check(downloaded.body.equals("attachment fixture\n"), "authorized content returns exact fixture bytes");
            StringBuilder downloadedHash = new StringBuilder();
            for (byte part : MessageDigest.getInstance("SHA-256").digest(downloaded.body.getBytes(StandardCharsets.UTF_8))) downloadedHash.append(String.format("%02x", part & 255));
            System.out.println("attachment downloaded sha256=" + downloadedHash);
            check(http(port, "GET", url, null, null, null, 403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"), "anonymous content is denied");
            http(port, "POST", BASE + "/queries/raw", "{}", null, null, 403);
            JSONObject detail = JSON.parseObject(http(port, "GET", BASE + "/1", null, cookie, null, 200).body);
            check(detail.getLongValue("id") == 1 && "fixture.txt".equals(detail.getJSONObject("ext$").getString("filename")), "authorized detail preserves original DTO and joined filename");
            JSONObject originalUi = JSON.parseObject(http(port, "POST", BASE + "/queries/raw", "{\"p\":{\"targetType\":\"download\",\"activatedFlag_SEQ\":\"1\"}}", cookie, null, 200).body);
            check(originalUi.getIntValue("total") == 1, "original UI query values are accepted");
            java.nio.file.Files.delete(fixture);
            http(port, "GET", url, null, cookie, null, 404);
            java.nio.file.Files.write(fixture, "attachment fixture\n".getBytes(StandardCharsets.UTF_8));
            for (String change : new String[]{"ACTIVATEDFLAG='0'", "ACTIVATEDFLAG='1',ENDDATE=SYSTIMESTAMP-INTERVAL '1' DAY"}) {
                try (Connection c = ds.getConnection(); Statement s = c.createStatement()) { s.execute("UPDATE T_EMC_HOME_ATTACHMENT SET " + change + " WHERE ID=1"); }
                check(JSON.parseObject(http(port, "POST", BASE + "/queries/raw", "{}", cookie, null, 200).body).getIntValue("total") == 0, "inactive or expired attachment is absent from list");
                http(port, "GET", BASE + "/1", null, cookie, null, 403);
                http(port, "POST", BASE + "/1/action/get-url", "{}", cookie, null, 403);
                http(port, "GET", url, null, cookie, null, 403);
            }
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) { s.execute("UPDATE T_EMC_HOME_ATTACHMENT SET ACTIVATEDFLAG='1',ENDDATE=SYSTIMESTAMP+INTERVAL '1' DAY WHERE ID=1"); }
            String lowCookie = http(port, "POST", "/core/module/sys/login", "id=low&password=" + password, null, key, 200).cookie;
            check(JSON.parseObject(http(port, "POST", BASE + "/queries/raw", "{}", lowCookie, null, 200).body).getIntValue("total") == 0, "user without attachment grant receives empty list");
            http(port, "GET", BASE + "/1", null, lowCookie, null, 403);
            http(port, "POST", BASE + "/1/action/get-url", "{}", lowCookie, null, 403);
            http(port, "GET", url, null, lowCookie, null, 403);
            java.nio.file.Files.delete(fixture);
            java.nio.file.Path outside = files.getParent().resolve("outside.txt");
            java.nio.file.Files.write(outside, "attachment fixture\n".getBytes(StandardCharsets.UTF_8));
            java.nio.file.Files.createSymbolicLink(fixture, outside);
            http(port, "GET", url, null, cookie, null, 400);
            java.nio.file.Files.delete(fixture);
            java.nio.file.Files.write(fixture, "attachment fixture\n".getBytes(StandardCharsets.UTF_8));
            http(port, "GET", "/files/secure/20261020/0123456789abcdef0123456789abcdef/unregistered.txt", null, cookie, null, 403);
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
                s.execute("DELETE FROM T_EMC_HOME_ATTA_PERMISSION WHERE ATTACHMENTID=1");
            }
            check(http(port, "GET", url, null, cookie, null, 403).body.contains("GIKAM.PERMISSION.UNAUTHORIZED"), "revocation after URL acquisition is denied");
            http(port, "GET", BASE + "/1", null, cookie, null, 403);
            http(port, "POST", BASE + "/1/action/get-url", "{}", cookie, null, 403);
            check(JSON.parseObject(http(port, "POST", BASE + "/queries/raw", "{}", cookie, null, 200).body).getIntValue("total") == 0, "revoked grant also removes list entry");
        } finally { app.close(); }
        System.out.println("attachment runtime HTTP: PASS (" + checks + " checks; actual historical main/resource, RSA session and synthetic Oracle only)");
    }

    private static Reply http(int port, String method, String path, String body, String cookie, String key, int expected) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        c.setConnectTimeout(5000); c.setReadTimeout(10000); c.setRequestMethod(method); c.setInstanceFollowRedirects(false);
        if (cookie != null) c.setRequestProperty("Cookie", cookie); if (key != null) c.setRequestProperty("SECURITY-RSA-KEY", key);
        if (body != null) { c.setDoOutput(true); c.setRequestProperty("Content-Type", key == null ? "application/json" : "application/x-www-form-urlencoded"); byte[] bytes = body.getBytes(StandardCharsets.UTF_8); c.setFixedLengthStreamingMode(bytes.length); try (OutputStream output = c.getOutputStream()) { output.write(bytes); } }
        try { int status = c.getResponseCode(); ByteArrayOutputStream bytes = new ByteArrayOutputStream(); InputStream stream = status >= 400 ? c.getErrorStream() : c.getInputStream(); if (stream != null) try (InputStream input = stream) { byte[] buffer = new byte[4096]; int n; while ((n = input.read(buffer)) != -1) bytes.write(buffer, 0, n); } String text = new String(bytes.toByteArray(), StandardCharsets.UTF_8); check(status == expected, path + " returned HTTP " + status + ", expected " + expected + " body=" + text); String set = c.getHeaderField("Set-Cookie"); return new Reply(text, set == null ? cookie : set.split(";", 2)[0]); } finally { c.disconnect(); }
    }
    private static void multipartUpload(int port, String targetId, String filename, byte[] data, String cookie, int expected) throws Exception {
        String boundary = "----ownedAttachment" + System.nanoTime(); HttpURLConnection c = (HttpURLConnection)new URL("http://127.0.0.1:" + port + "/core/module/item/files").openConnection();
        c.setRequestMethod("POST"); c.setDoOutput(true); c.setRequestProperty("Cookie", cookie); c.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"targetId\"\r\n\r\n" + targetId + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"bizCategory\"\r\n\r\ndocument\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\nContent-Type: text/plain\r\n\r\n").getBytes(StandardCharsets.UTF_8)); out.write(data); out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        c.getOutputStream().write(out.toByteArray()); int status = c.getResponseCode(); InputStream input = status >= 400 ? c.getErrorStream() : c.getInputStream(); byte[] body = input == null ? new byte[0] : read(input); check(status == expected, "multipart upload returned HTTP " + status + ", expected " + expected + " body=" + new String(body, StandardCharsets.UTF_8)); c.disconnect();
    }
    private static byte[] read(InputStream input) throws IOException { ByteArrayOutputStream out = new ByteArrayOutputStream(); byte[] buffer = new byte[4096]; int n; while ((n = input.read(buffer)) != -1) out.write(buffer, 0, n); return out.toByteArray(); }
    private static java.nio.file.Path storedPath(DataSource dataSource, long attachmentId, java.nio.file.Path root) throws Exception {
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement("SELECT SCOPE,MD5NAME,NAME,TO_CHAR(CREATEDTIME,'YYYYMMDD') FROM T_CORE_FILE WHERE TARGETID=?")) {
            query.setString(1, "T_EMC_HOME_ATTACHMENT$" + attachmentId);
            try (ResultSet rows = query.executeQuery()) {
                check(rows.next(), "attachment metadata exists for physical path lookup");
                return root.resolve(rows.getString(1)).resolve(rows.getString(4)).resolve(rows.getString(2)).resolve(rows.getString(3));
            }
        }
    }
    private static Set<String> regularFiles(java.nio.file.Path root) throws IOException {
        Set<String> result = new TreeSet<String>();
        try (Stream<java.nio.file.Path> paths = java.nio.file.Files.walk(root)) {
            paths.filter(java.nio.file.Files::isRegularFile).forEach(path -> result.add(root.relativize(path).toString()));
        }
        return result;
    }
    private static final class Reply { final String body, cookie; Reply(String body, String cookie) { this.body = body; this.cookie = cookie; } }
}
