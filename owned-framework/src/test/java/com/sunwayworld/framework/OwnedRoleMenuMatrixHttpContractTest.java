package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.InMemoryAuditEventSink;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpController;
import com.sunwayworld.owned.internal.web.OwnedMenuController;
import com.sunwayworld.owned.internal.web.OwnedRoleMenuMatrixController;
import com.sunwayworld.owned.internal.web.OwnedRoleUserMatrixController;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/** Loopback HTTP contract for a bounded global menu-role matrix read. */
public final class OwnedRoleMenuMatrixHttpContractTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("oracle properties path required");
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setURL(properties.getProperty("url"));
        dataSource.setUser(properties.getProperty("user"));
        dataSource.setPassword(properties.getProperty("password"));
        seed(dataSource);
        ProbeConfiguration.DATA_SOURCE = dataSource;
        ProbeConfiguration.PROVIDER = new JdbcPersonnelIdentityProvider(
                new JdbcPersonnelIdentity(dataSource, "MD5", new String[0], new String[] { "100900" }));
        System.setProperty("owned.role-menu.write.mode", "isolated");
        System.setProperty("owned.role-user.write.mode", "isolated");
        AnnotationConfigWebApplicationContext application = new AnnotationConfigWebApplicationContext();
        application.register(ProbeConfiguration.class, MatrixConfiguration.class);
        application.refresh();
        Tomcat tomcat = new Tomcat();
        File base = new File(System.getProperty("java.io.tmpdir"), "owned-role-menu-matrix");
        base.mkdirs();
        tomcat.setBaseDir(base.getAbsolutePath()); tomcat.setPort(0);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addContext("", base.getAbsolutePath());
        Tomcat.addServlet(context, "dispatcher", new DispatcherServlet(application));
        context.addServletMappingDecoded("/", "dispatcher");
        tomcat.start();
        int port = tomcat.getConnector().getLocalPort();
        try {
            String admin = login(port, "admin", "secret");
            String low = login(port, "low", "secret");
            String roleAdmin = login(port, "role-admin", "secret");
            String manager = login(port, "manager", "secret");
            String reader = login(port, "reader", "secret");
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", low, 403, "unauthorized matrix");
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", low, 403, "unauthorized rows");
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", manager, 403, "manager default denied matrix");
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", reader, 403, "reader default denied matrix");
            String headers = expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", admin, 200, "headers");
            if (!headers.contains("\"field\":\"T_CORE_ROLE$ROLENAME$1\"")
                    || !headers.contains("\"field\":\"T_CORE_ROLE$ROLENAME$4\"")
                    || !headers.contains("\"field\":\"T_CORE_ROLE$ROLENAME$5\"")
                    || !headers.contains("\"field\":\"T_CORE_ROLE$ROLENAME$6\"")
                    || !headers.contains("\"readonly\":true"))
                throw new AssertionError("role headers missing supplier field contract: " + headers);
            String rows = expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", admin, 200, "rows");
            if (!rows.contains("100001") || !rows.contains("\"T_CORE_ROLE$ROLENAME$1\":\"1\""))
                throw new AssertionError("matrix rows missing grant: " + rows);
            if (!rows.contains("\"T_CORE_ROLE$ROLENAME$3\":\"2\""))
                throw new AssertionError("matrix parent partial state missing: " + rows);
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", roleAdmin, 200, "role-admin matrix");
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", roleAdmin, 200, "role-admin rows");
            String grant = request(port, "PUT", "/secure/core/module/sys/menu-roles/instant",
                    "{\"p\":{\"id\":\"100901\",\"T_CORE_ROLE$ROLENAME$5\":\"1\"}}", roleAdmin, 200);
            if (!grant.contains("\"updated\":1")) throw new AssertionError("matrix grant response missing update count: " + grant);
            String grantedRows = expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", admin, 200, "rows after grant");
            if (!rowHasGrant(grantedRows, "100901", "T_CORE_ROLE$ROLENAME$5", "1"))
                throw new AssertionError("matrix grant was not visible: " + grantedRows);
            String revoke = request(port, "PUT", "/secure/core/module/sys/menu-roles/instant",
                    "{\"p\":{\"id\":\"100901\",\"T_CORE_ROLE$ROLENAME$5\":\"0\"}}", roleAdmin, 200);
            if (!revoke.contains("\"updated\":1")) throw new AssertionError("matrix revoke response missing update count: " + revoke);
            String revokedRows = expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", admin, 200, "rows after revoke");
            if (rowHasGrant(revokedRows, "100901", "T_CORE_ROLE$ROLENAME$5", "1"))
                throw new AssertionError("matrix revoke remained visible: " + revokedRows);
            expect(port, "PUT", "/secure/core/module/sys/menu-roles/instant", reader, 403, "reader matrix write denied");
            request(port, "PUT", "/secure/core/module/sys/menu-roles/instant",
                    "{\"p\":{\"id\":\"100901\",\"T_CORE_ROLE$ROLENAME$5\":\"2\"}}", roleAdmin, 400);
            String rollback = request(port, "PUT", "/secure/core/module/sys/menu-roles/instant",
                    "{\"p\":[{\"id\":\"100901\",\"T_CORE_ROLE$ROLENAME$5\":\"1\"},{\"id\":\"100901\",\"T_CORE_ROLE$ROLENAME$999\":\"1\"}]}", roleAdmin, 400);
            if (!rollback.contains("INVALID_REQUEST")) throw new AssertionError("matrix rollback response missing invalid request: " + rollback);
            String rolledBackRows = expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", admin, 200, "rows after rollback");
            if (rowHasGrant(rolledBackRows, "100901", "T_CORE_ROLE$ROLENAME$5", "1"))
                throw new AssertionError("matrix transaction did not roll back: " + rolledBackRows);

            String roleUserHeaders = expect(port, "POST", "/secure/core/module/sys/role-users/queries/matrix-headers", roleAdmin, 200, "role-user headers");
            if (!roleUserHeaders.contains("T_CORE_ROLE$ROLENAME$4") || !roleUserHeaders.contains("\"readonly\":true"))
                throw new AssertionError("role-user headers missing contract: " + roleUserHeaders);
            String roleUserRows = expect(port, "POST", "/secure/core/module/sys/role-users/queries/matrix-rows", roleAdmin, 200, "role-user rows");
            if (!roleUserRows.contains("\"id\":\"low\"") || !roleUserRows.contains("T_CORE_ROLE$ROLENAME$1"))
                throw new AssertionError("role-user rows missing user grant: " + roleUserRows);
            String roleUserGrant = request(port, "PUT", "/secure/core/module/sys/role-users/instant",
                    "{\"p\":{\"id\":\"low\",\"T_CORE_ROLE$ROLENAME$6\":\"1\"}}", roleAdmin, 200);
            if (!roleUserGrant.contains("\"updated\":1")) throw new AssertionError("role-user grant response missing update count: " + roleUserGrant);
            String grantedRoleUsers = expect(port, "POST", "/secure/core/module/sys/role-users/queries/matrix-rows", roleAdmin, 200, "role-user rows after grant");
            if (!rowHasGrant(grantedRoleUsers, "low", "T_CORE_ROLE$ROLENAME$6", "1"))
                throw new AssertionError("role-user grant was not visible: " + grantedRoleUsers);
            request(port, "PUT", "/secure/core/module/sys/role-users/instant",
                    "{\"p\":{\"id\":\"low\",\"T_CORE_ROLE$ROLENAME$6\":\"0\"}}", reader, 403);
            request(port, "PUT", "/secure/core/module/sys/role-users/instant",
                    "{\"p\":{\"id\":\"other\",\"T_CORE_ROLE$ROLENAME$6\":\"1\"}}", roleAdmin, 403);
            request(port, "PUT", "/secure/core/module/sys/role-users/instant",
                    "{\"p\":{\"id\":\"low\",\"T_CORE_ROLE$ROLENAME$6\":\"0\"}}", roleAdmin, 200);
            request(port, "PUT", "/secure/core/module/sys/role-users/instant",
                    "{\"p\":{\"id\":\"low\",\"T_CORE_ROLE$ROLENAME$6\":\"2\"}}", roleAdmin, 400);
            post(port, "/core/module/sys/logout", "", null, 200, roleAdmin);
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", roleAdmin, 403, "invalidated role-admin session");
            roleAdmin = login(port, "role-admin", "secret");
            try (Connection connection = dataSource.getConnection(); Statement update = connection.createStatement()) {
                update.executeUpdate("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=4 AND TARGETID='100900'");
            }
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", roleAdmin, 403, "revoked role-admin grant");
            try (Connection connection = dataSource.getConnection(); Statement update = connection.createStatement()) {
                update.executeUpdate("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (4,'T_CORE_MENU','100900')");
                update.executeUpdate("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG='1' WHERE ID=4");
            }
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", roleAdmin, 403, "suspended role-admin");
            try (Connection connection = dataSource.getConnection(); Statement update = connection.createStatement()) {
                update.executeUpdate("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=3 AND TARGETID='100900'");
            }
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-rows", admin, 403, "revoked matrix");
            try (Connection connection = dataSource.getConnection(); Statement update = connection.createStatement()) {
                update.executeUpdate("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (3,'T_CORE_MENU','100900')");
                update.executeUpdate("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG='1' WHERE ID=3");
            }
            expect(port, "POST", "/secure/core/module/sys/menu-roles/queries/matrix-headers", admin, 403, "suspended matrix");
            boolean auditSeen = false;
            String auditActor = null;
            for (AuditEvent event : ProbeConfiguration.SINK.events()) {
                if ("authorize".equals(event.getAction())
                        && "role-menu.admin".equals(event.getResource())
                        && event.getOutcome() == AuditEvent.Outcome.ALLOWED) {
                    auditSeen = true;
                    auditActor = event.getActorId();
                    break;
                }
            }
            if (!auditSeen) throw new AssertionError("role-menu authorization audit was not observed");
            boolean writeAuditSeen = false;
            for (AuditEvent event : ProbeConfiguration.SINK.events()) {
                if ("role-menu-write".equals(event.getAction())
                        && "role-menu.write".equals(event.getResource())
                        && event.getOutcome() == AuditEvent.Outcome.ALLOWED) {
                    writeAuditSeen = true; break;
                }
            }
            if (!writeAuditSeen) throw new AssertionError("role-menu write audit was not observed");
            boolean roleUserWriteAuditSeen = false;
            for (AuditEvent event : ProbeConfiguration.SINK.events()) {
                if ("role-user-write".equals(event.getAction())
                        && "role-user.write".equals(event.getResource())
                        && event.getOutcome() == AuditEvent.Outcome.ALLOWED) {
                    roleUserWriteAuditSeen = true; break;
                }
            }
            if (!roleUserWriteAuditSeen) throw new AssertionError("role-user write audit was not observed");
            System.out.println("ROLE_MENU_WRITE|role-admin|grant-revoke-rollback|PASS");
            System.out.println("ROLE_USER_WRITE|role-admin|same-org-grant-revoke-cross-org-denial|PASS");
            System.out.println("ROLE_MENU_AUDIT|" + auditActor + "|role-menu-matrix-read|ALLOWED");
            System.out.println("owned role-menu matrix HTTP: PASS (38 checks; synthetic Oracle only)");
        } finally {
            tomcat.stop(); tomcat.destroy(); application.close();
            System.clearProperty("owned.role-menu.write.mode");
            System.clearProperty("owned.role-user.write.mode");
            cleanup(dataSource);
        }
    }

    private static void seed(DataSource dataSource) throws Exception {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            s.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP)");
            s.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, ROLENAME VARCHAR2(100), LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY, MENUNAME VARCHAR2(100), MENUINITNAME VARCHAR2(100), HREF VARCHAR2(255), PARENTID VARCHAR2(64), ORDERNO NUMBER)");
            s.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            s.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('admin','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org','activated','0',NULL)");
            s.execute("INSERT INTO T_CORE_USER VALUES ('low','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org','activated','0',NULL)");
            s.execute("INSERT INTO T_CORE_USER VALUES ('role-admin','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org','activated','0',NULL)");
            s.execute("INSERT INTO T_CORE_USER VALUES ('manager','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org','activated','0',NULL)");
            s.execute("INSERT INTO T_CORE_USER VALUES ('reader','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org','activated','0',NULL)");
            s.execute("INSERT INTO T_CORE_USER VALUES ('other','5ebe2294ecd0e0f08eab7690d2a6ee69','org-b','Other Org','activated','0',NULL)");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'Inventory Manager','0')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (2,'Read Only','0')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (3,'System Administrator','0')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (4,'Role Administrator','0')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (5,'Manager','0')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (6,'Reader','0')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'low')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (3,'admin')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (4,'role-admin')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (5,'manager')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (6,'reader')");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('100001','Personnel','Personnel','/personnel',NULL,1)");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('100002','Equipment','Equipment','/equipment',NULL,2)");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('100900','Role Menu Matrix','Role Menu Matrix','/menu-roles',NULL,3)");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('100901','Matrix Child','Matrix Child','/menu-roles/child','100900',1)");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100001')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (3,'T_CORE_MENU','100900')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (3,'T_CORE_MENU','100001')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (4,'T_CORE_MENU','100900')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (5,'T_CORE_MENU','100001')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (6,'T_CORE_MENU','100001')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('admin','T_CORE_ORG','org-a')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('low','T_CORE_ORG','org-a')");
        }
    }

    private static void cleanup(DataSource dataSource) throws Exception {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            for (String table : new String[] {"T_CORE_ROLE_PERMISSION", "T_CORE_ROLE_USER", "T_CORE_MENU", "T_CORE_ROLE", "T_CORE_USER_PERMISSION", "T_CORE_USER", "T_CORE_PASSWORD_POLICY", "T_CORE_LOGIN_CONFIG"}) {
                try { s.execute("DROP TABLE " + table + " CASCADE CONSTRAINTS"); } catch (Exception ignored) { }
            }
        }
    }

    private static String login(int port, String user, String password) throws Exception {
        String key = text(port, "/open/security/public-key", null);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String encoded = Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
        return post(port, "/core/module/sys/login", "id=" + user + "&password=" + URLEncoder.encode(encoded, "UTF-8"), key, 200, null);
    }

    private static String expect(int port, String method, String path, String cookie, int expected, String label) throws Exception {
        return request(port, method, path, "{}", cookie, expected);
    }

    private static String request(int port, String method, String path, String body, String cookie, int expected) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        c.setRequestMethod(method); c.setDoOutput(true);
        c.setRequestProperty("Content-Type", "application/json");
        if (cookie != null) c.setRequestProperty("Cookie", cookie);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8); c.setFixedLengthStreamingMode(bytes.length);
        try (OutputStream output = c.getOutputStream()) { output.write(bytes); }
        int status = c.getResponseCode();
        if (status != expected) throw new AssertionError(path + " returned HTTP " + status + ", expected " + expected);
        java.io.InputStream source = status >= 400 ? c.getErrorStream() : c.getInputStream();
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        if (source != null) { byte[] buffer = new byte[1024]; int size; while ((size = source.read(buffer)) != -1) output.write(buffer, 0, size); }
        String result = new String(output.toByteArray(), StandardCharsets.UTF_8);
        c.disconnect(); return result;
    }

    private static boolean rowHasGrant(String json, String rowId, String field, String value) {
        String marker = "\"id\":\"" + rowId + "\"";
        int start = json.indexOf(marker);
        if (start < 0) return false;
        int end = json.indexOf("}", start);
        if (end < 0) end = json.length();
        return json.substring(start, end).contains("\"" + field + "\":\"" + value + "\"");
    }

    private static String post(int port, String path, String body, String rsaKey, int expected, String cookie) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        c.setRequestMethod("POST"); c.setDoOutput(true);
        c.setRequestProperty("Content-Type", rsaKey == null ? "application/json" : "application/x-www-form-urlencoded");
        if (rsaKey != null) c.setRequestProperty("SECURITY-RSA-KEY", rsaKey);
        if (cookie != null) c.setRequestProperty("Cookie", cookie);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8); c.setFixedLengthStreamingMode(bytes.length);
        try (OutputStream output = c.getOutputStream()) { output.write(bytes); }
        int status = c.getResponseCode();
        if (status != expected) throw new AssertionError(path + " returned HTTP " + status + ", expected " + expected);
        java.io.InputStream source = status >= 400 ? c.getErrorStream() : c.getInputStream();
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        if (source != null) { byte[] buffer = new byte[1024]; int size; while ((size = source.read(buffer)) != -1) output.write(buffer, 0, size); }
        String result = new String(output.toByteArray(), StandardCharsets.UTF_8);
        String setCookie = c.getHeaderField("Set-Cookie"); c.disconnect();
        return setCookie == null ? result : setCookie.substring(0, setCookie.indexOf(';') < 0 ? setCookie.length() : setCookie.indexOf(';'));
    }

    private static String text(int port, String path, String cookie) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        if (cookie != null) c.setRequestProperty("Cookie", cookie); if (c.getResponseCode() != 200) throw new AssertionError(path + " unavailable");
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream(); byte[] buffer = new byte[1024]; int size;
        try (java.io.InputStream input = c.getInputStream()) { while ((size = input.read(buffer)) != -1) output.write(buffer, 0, size); }
        c.disconnect(); return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    @Configuration public static class ProbeConfiguration {
        static DataSource DATA_SOURCE; static OwnedIdentityProvider PROVIDER;
        static final InMemoryAuditEventSink SINK = new InMemoryAuditEventSink();
        @Bean public DataSource dataSource() { return DATA_SOURCE; }
        @Bean public OwnedIdentityHttpBoundary boundary() { return new OwnedIdentityHttpBoundary(PROVIDER, SINK); }
        @Bean public OwnedRsaLoginCredentialDecoder decoder() { return new OwnedRsaLoginCredentialDecoder(); }
        @Bean public OwnedIdentityHttpController identity(OwnedIdentityHttpBoundary b, OwnedRsaLoginCredentialDecoder d) { return new OwnedIdentityHttpController(b, d); }
        @Bean public OwnedSecurityKeyController key(OwnedRsaLoginCredentialDecoder d) { return new OwnedSecurityKeyController(d); }
    }
    @Configuration public static class MatrixConfiguration {
        @Bean public OwnedMenuController menu(OwnedIdentityHttpBoundary b, DataSource d) { return new OwnedMenuController(b, d); }
        @Bean public OwnedRoleMenuMatrixController matrix(OwnedIdentityHttpBoundary b, DataSource d, Environment e) {
            return new OwnedRoleMenuMatrixController(b, d, e);
        }
        @Bean public OwnedRoleUserMatrixController roleUsers(OwnedIdentityHttpBoundary b, DataSource d, Environment e) {
            return new OwnedRoleUserMatrixController(b, d, e);
        }
    }
}
