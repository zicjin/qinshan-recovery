package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.web.InMemoryAuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpController;
import com.sunwayworld.owned.internal.web.OwnedRsaLoginCredentialDecoder;
import com.sunwayworld.owned.internal.web.OwnedSecurityKeyController;
import com.sunwayworld.owned.internal.web.OwnedMenuController;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import oracle.jdbc.pool.OracleDataSource;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/** Real loopback servlet proof for the owned login/session/logout seam. */
public final class OwnedIdentityServletContractTest {
    public static void main(String[] args) throws Exception {
        OracleDataSource dataSource = null;
        if (args.length != 0) {
            Properties properties = new Properties();
            try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
            dataSource = new OracleDataSource();
            dataSource.setURL(properties.getProperty("url"));
            dataSource.setUser(properties.getProperty("user"));
            dataSource.setPassword(properties.getProperty("password"));
            restoreFixtureState(dataSource);
            ProbeConfiguration.PROVIDER = new JdbcPersonnelIdentityProvider(new JdbcPersonnelIdentity(dataSource, "MD5"));
        }
        AnnotationConfigWebApplicationContext application = new AnnotationConfigWebApplicationContext();
        application.register(ProbeConfiguration.class);
        if (dataSource != null) {
            ProbeConfiguration.DATA_SOURCE = dataSource;
            application.register(MenuProbeConfiguration.class);
        }
        application.refresh();
        Tomcat tomcat = new Tomcat();
        File base = new File(System.getProperty("java.io.tmpdir"), "owned-identity-servlet");
        base.mkdirs();
        tomcat.setBaseDir(base.getAbsolutePath());
        tomcat.setPort(0);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addContext("", base.getAbsolutePath());
        DispatcherServlet dispatcher = new DispatcherServlet(application);
        Tomcat.addServlet(context, "dispatcher", dispatcher);
        context.addServletMappingDecoded("/", "dispatcher");
        tomcat.start();
        int port = tomcat.getConnector().getLocalPort();
        try {
            String userId = dataSource == null ? "u-a" : "allowed";
            String publicKey = getText(port, "/open/security/public-key");
            Cipher client = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            client.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey))));
            String ciphertext = Base64.getEncoder().encodeToString(client.doFinal("secret".getBytes(StandardCharsets.UTF_8)));
            post(port, "/core/module/sys/login", "id=" + userId + "&password=plaintext", publicKey, null, 400);
            post(port, "/core/module/sys/login", "id=" + userId + "&password=" + URLEncoder.encode(ciphertext, "UTF-8"), "unknown-key", null, 400);
            String cookie = post(port, "/core/module/sys/login", "id=" + userId + "&password="
                    + URLEncoder.encode(ciphertext, "UTF-8"), publicKey, null, 200);
            if (cookie == null) throw new AssertionError("login did not establish an HTTP session");
            get(port, "/secure/identity-probe", cookie, 200);
            if (dataSource != null) {
                assertMenu(port, cookie, "100001");
                assertMenuTree(port, cookie);
                try (Connection connection = dataSource.getConnection(); Statement update = connection.createStatement()) {
                    update.executeUpdate("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=1 AND TARGETID='100001'");
                }
                assertMenuTreeWithoutUnauthorizedParent(port, cookie);
                try (Connection connection = dataSource.getConnection(); Statement update = connection.createStatement()) {
                    update.executeUpdate("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=1");
                }
                assertMenu(port, cookie, "\"menus\":[]");
                get(port, "/secure/identity-probe", cookie, 403);
            }
            post(port, "/core/module/sys/logout", "", null, cookie, 200);
            get(port, "/secure/identity-probe", cookie, 403);
            ProbeConfiguration.assertAtLeast(4);
        } finally {
            tomcat.stop();
            tomcat.destroy();
            application.close();
        }
        System.out.println("owned identity servlet: PASS (loopback RSA login, invalid transport rejection, session permission, logout" + (dataSource == null ? "" : "; Oracle revocation") + ")");
    }

    private static void restoreFixtureState(OracleDataSource dataSource) throws Exception {
        try (Connection connection = dataSource.getConnection(); Statement update = connection.createStatement()) {
            // The prior API check intentionally removes configuration and grants. Restore only the synthetic rows.
            update.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            update.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            update.execute("UPDATE T_CORE_USER SET LASTSUSPENDEDFLAG='0' WHERE ID='allowed'");
            update.execute("ALTER TABLE T_CORE_MENU ADD (MENUNAME VARCHAR2(100), MENUINITNAME VARCHAR2(100), HREF VARCHAR2(255), PARENTID VARCHAR2(64), ORDERNO NUMBER, ICON VARCHAR2(100), TODOICON VARCHAR2(100), I18NCODE VARCHAR2(100))");
            update.execute("UPDATE T_CORE_MENU SET MENUNAME='Fixture parent', MENUINITNAME='Fixture parent', ORDERNO=1 WHERE ID='100001'");
            update.execute("INSERT INTO T_CORE_MENU (ID,MENUNAME,MENUINITNAME,HREF,PARENTID,ORDERNO,ICON,TODOICON,I18NCODE) VALUES ('100002','Later child','Later child','/later','100001',3,'later.svg',NULL,NULL)");
            update.execute("INSERT INTO T_CORE_MENU (ID,MENUNAME,MENUINITNAME,HREF,PARENTID,ORDERNO,ICON,TODOICON,I18NCODE) VALUES ('100003','Earlier child','Earlier child','/earlier','100001',2,'earlier.svg',NULL,NULL)");
            update.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100001')");
            update.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100002')");
            update.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100003')");
        }
    }

    private static String post(int port, String path, String form, String key, String cookie, int expected) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        connection.setRequestMethod("POST"); connection.setDoOutput(true); connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (key != null) connection.setRequestProperty("SECURITY-RSA-KEY", key);
        if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        byte[] body = form.getBytes(StandardCharsets.UTF_8); connection.setFixedLengthStreamingMode(body.length);
        try (OutputStream output = connection.getOutputStream()) { output.write(body); }
        int status = connection.getResponseCode();
        if (status != expected) throw new AssertionError(path + " returned HTTP " + status);
        String setCookie = connection.getHeaderField("Set-Cookie");
        connection.disconnect();
        return setCookie == null ? cookie : setCookie.substring(0, setCookie.indexOf(';') < 0 ? setCookie.length() : setCookie.indexOf(';'));
    }

    private static void get(int port, String path, String cookie, int expected) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        int status = connection.getResponseCode();
        if (status != expected) throw new AssertionError(path + " returned HTTP " + status + ", expected " + expected);
        connection.disconnect();
    }

    private static void assertMenu(int port, String cookie, String marker) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + "/secure/core/module/sys/menus").openConnection();
        if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        int status = connection.getResponseCode();
        if (status != 200) throw new AssertionError("menu route returned HTTP " + status);
        try (java.io.InputStream input = connection.getInputStream(); java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[512]; int size;
            while ((size = input.read(buffer)) != -1) bytes.write(buffer, 0, size);
            String body = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            if (!body.contains(marker)) throw new AssertionError("menu route missing marker " + marker + ": " + body);
        } finally { connection.disconnect(); }
    }

    private static void assertMenuTree(int port, String cookie) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port
                + "/secure/core/module/sys/menus/tree/root").openConnection();
        connection.setRequestMethod("POST"); connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        try (OutputStream output = connection.getOutputStream()) { output.write("{}".getBytes(StandardCharsets.UTF_8)); }
        int status = connection.getResponseCode();
        if (status != 200) throw new AssertionError("menu tree route returned HTTP " + status);
        try (java.io.InputStream input = connection.getInputStream(); java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[512]; int size;
            while ((size = input.read(buffer)) != -1) bytes.write(buffer, 0, size);
            String body = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            com.alibaba.fastjson.JSONArray roots = com.alibaba.fastjson.JSON.parseArray(body);
            if (roots.isEmpty()) throw new AssertionError("menu tree route returned no authorized menus");
            com.alibaba.fastjson.JSONObject parent = roots.getJSONObject(0);
            if (!"100001".equals(parent.getString("id")) || !"Fixture parent".equals(parent.getString("text"))
                    || parent.getIntValue("lvl") != 1 || parent.getIntValue("childQty") != 2) {
                throw new AssertionError("menu tree parent contract differs: " + body);
            }
            com.alibaba.fastjson.JSONArray children = parent.getJSONArray("children");
            if (!"100003".equals(children.getJSONObject(0).getString("id"))
                    || !"100002".equals(children.getJSONObject(1).getString("id"))
                    || children.getJSONObject(0).getIntValue("lvl") != 2
                    || !"earlier.svg".equals(children.getJSONObject(0).getString("icon"))
                    || !parent.containsKey("ext$") || parent.containsKey("tags")
                    || parent.containsKey("menuIcon") || parent.containsKey("clientScript")) {
                throw new AssertionError("menu tree hierarchy, order, or metadata differs: " + body);
            }
            if (!parent.getJSONObject("ext$").containsKey("menuiconurl")) {
                throw new AssertionError("menu tree extension shape differs: " + body);
            }
            java.util.Set<String> expectedKeys = new HashSet<String>(Arrays.asList(
                    "checked", "childQty", "children", "expanded", "ext$", "href", "i18nCode",
                    "icon", "id", "lazyLoad", "lvl", "menuInitName", "menuName", "menuParam",
                    "orderNo", "parentId", "publishTime", "publishUserName", "resourceId",
                    "resourceName", "resourcePath", "text", "todoIcon"));
            if (!expectedKeys.equals(new HashSet<String>(parent.keySet()))) {
                throw new AssertionError("menu tree field shape differs: " + body);
            }
        } finally { connection.disconnect(); }
    }

    private static void assertMenuTreeWithoutUnauthorizedParent(int port, String cookie) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port
                + "/secure/core/module/sys/menus/tree/root").openConnection();
        connection.setRequestMethod("POST"); connection.setDoOutput(true);
        if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        try (OutputStream output = connection.getOutputStream()) { output.write("{}".getBytes(StandardCharsets.UTF_8)); }
        int status = connection.getResponseCode();
        if (status != 200) throw new AssertionError("menu tree route returned HTTP " + status);
        try (java.io.InputStream input = connection.getInputStream(); java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[512]; int size;
            while ((size = input.read(buffer)) != -1) bytes.write(buffer, 0, size);
            String body = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            com.alibaba.fastjson.JSONArray roots = com.alibaba.fastjson.JSON.parseArray(body);
            if (roots.size() != 2 || !"100001".equals(roots.getJSONObject(0).getString("parentId")))
                throw new AssertionError("unauthorized parent was unexpectedly restored: " + body);
        } finally { connection.disconnect(); }
    }

    private static String getText(int port, String path) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        try {
            if (connection.getResponseCode() != 200) throw new AssertionError("public key unavailable");
            if (!"no-store".equals(connection.getHeaderField("Cache-Control"))) throw new AssertionError("public key cache policy");
            try (java.io.InputStream input = connection.getInputStream(); java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024]; int size;
                while ((size = input.read(buffer)) != -1) bytes.write(buffer, 0, size);
                return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            }
        } finally { connection.disconnect(); }
    }

    @Configuration
    public static class ProbeConfiguration {
        private static final InMemoryAuditEventSink SINK = new InMemoryAuditEventSink();
        private static OwnedIdentityProvider PROVIDER = new FixtureIdentity();
        private static DataSource DATA_SOURCE;
        @Bean public OwnedIdentityHttpBoundary identityBoundary() { return new OwnedIdentityHttpBoundary(PROVIDER, SINK); }
        @Bean public OwnedRsaLoginCredentialDecoder credentialDecoder() { return new OwnedRsaLoginCredentialDecoder(); }
        @Bean public OwnedSecurityKeyController securityKeyController(OwnedRsaLoginCredentialDecoder decoder) {
            return new OwnedSecurityKeyController(decoder);
        }
        @Bean public OwnedIdentityHttpController identityController(OwnedIdentityHttpBoundary boundary, OwnedRsaLoginCredentialDecoder decoder) {
            return new OwnedIdentityHttpController(boundary, decoder);
        }
        @Bean public IdentityProbeController identityProbe(OwnedIdentityHttpBoundary boundary) {
            return new IdentityProbeController(boundary);
        }
        static void assertAtLeast(int count) { if (SINK.events().size() < count) throw new AssertionError("audit events missing"); }
    }

    @Configuration
    public static class MenuProbeConfiguration {
        @Bean public DataSource menuDataSource() { return ProbeConfiguration.DATA_SOURCE; }
        @Bean public OwnedMenuController menuController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
            return new OwnedMenuController(boundary, dataSource);
        }
    }

    @RestController
    public static class IdentityProbeController {
        private final OwnedIdentityHttpBoundary boundary;
        IdentityProbeController(OwnedIdentityHttpBoundary boundary) { this.boundary = boundary; }
        @GetMapping("/secure/identity-probe")
        public ResponseEntity<String> probe(HttpServletRequest request) throws SQLException {
            try {
                boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
                return ResponseEntity.ok("identity-probe");
            } catch (org.springframework.security.access.AccessDeniedException denied) {
                return ResponseEntity.status(403).body(denied.getMessage());
            }
        }
    }

    private static final class FixtureIdentity implements OwnedIdentityProvider {
        private final OwnedPrincipal principal = new OwnedPrincipal("u-a", "org-a", "Fixture Org");
        public OwnedPrincipal authenticate(String userId, char[] password) {
            if (!"u-a".equals(userId) || !java.util.Arrays.equals("secret".toCharArray(), password))
                throw new org.springframework.security.authentication.BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
            return principal;
        }
        public OwnedPrincipal refresh(OwnedPrincipal value) { return value; }
        public void authorize(OwnedPrincipal value, String permission) {
            if (!PERSONNEL_READ.equals(permission)) throw new org.springframework.security.access.AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        }
        public java.util.Map<String, Object> accountProfile(OwnedPrincipal value) {
            java.util.Map<String, Object> profile = new java.util.LinkedHashMap<String, Object>();
            profile.put("id", value.getId()); profile.put("orgId", value.getOrgId());
            profile.put("orgName", value.getOrgName());
            profile.put("ext$", new java.util.LinkedHashMap<String, Object>());
            return profile;
        }
        public Set<String> permittedOrganizationIds(OwnedPrincipal value) {
            return Collections.unmodifiableSet(new LinkedHashSet<String>(Collections.singleton("org-a")));
        }
    }
}
