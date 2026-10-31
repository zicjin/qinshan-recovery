import com.sunwayworld.GikamCoreApplication;
import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.sql.DataSource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/** Boots the packaged runtime. A test-only marker observes guard passage, not resource behavior. */
@SunwayBootApplication
@EnableAutoConfiguration
public class OwnedIdentityBootContractTest extends GikamCoreApplication {
    private static int checks;
    private static final String PERSONNEL = "/secure/emc/module/mdm/emp/emps";
    private static final String CONSUMABLE = "/secure/emc/module/item/consume/consumes";
    private static final String EQUIPMENT = "/secure/emc/module/mdm/equipt/equipts";

    @Bean public ServletRegistrationBean<HttpServlet> guardObservationMarker() {
        return new ServletRegistrationBean<HttpServlet>(new HttpServlet() {
            @Override protected void service(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("test-only-guard-passage");
            }
        }, "/secure/emc/*", "/secure/unrelated-marker");
    }

    public static void main(String[] args) {
        try { verify(args); }
        catch (Exception failure) { throw new IllegalStateException("Boot identity contract failed", failure); }
    }

    private static void verify(String[] args) throws Exception {
        Properties database = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { database.load(input); }
        Path audit = java.nio.file.Paths.get(args[1]);
        rejectConfiguration(database, audit, null, "file", "sunway.user.password.encoder.type");
        rejectConfiguration(database, audit, "BCrypt", "file", "sunway.user.password.encoder.type");
        rejectConfiguration(database, audit, "MD5", null, "owned.identity.audit.type");
        rejectConfiguration(database, audit, "MD5", "memory", "owned.identity.audit.type");
        rejectConfiguration(database, null, "MD5", "file", "owned.identity.audit.path");
        try (ConfigurableApplicationContext application = start(database, audit)) {
            check(application.getBeansOfType(DataSource.class).size() == 1, "Boot auto-configures one datasource");
            DataSource source = application.getBean(DataSource.class);
            try (Connection connection = source.getConnection(); Statement update = connection.createStatement()) {
                // Only the synthetic grant removed by the preceding servlet check.
                update.executeUpdate("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=1 AND TARGETID IN ('100002','100003')");
                update.executeUpdate("DELETE FROM T_CORE_MENU WHERE ID IN ('100002','100003')");
                update.executeUpdate("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100001')");
                for (String menu : new String[]{"101501", "20251000001", "430201", "20250800003"})
                    update.executeUpdate("INSERT INTO T_CORE_MENU (ID,MENUNAME,MENUINITNAME,HREF,PARENTID,ORDERNO,ICON,TODOICON,I18NCODE) VALUES ('"
                            + menu + "',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL)");
                String[][] grants = {{"equipment-reader", "4", "20251000001"},
                        {"consumable-reader", "5", "20250800003"}, {"equipment-manager", "6", "101501"},
                        {"consumable-manager", "7", "430201"}};
                for (String[] grant : grants) {
                    update.executeUpdate("INSERT INTO T_CORE_USER VALUES ('" + grant[0]
                            + "','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org','activated','0',NULL)");
                    update.executeUpdate("INSERT INTO T_CORE_ROLE VALUES (" + grant[1] + ",'0',NULL)");
                    update.executeUpdate("INSERT INTO T_CORE_ROLE_USER VALUES (" + grant[1] + ",'" + grant[0] + "')");
                    update.executeUpdate("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (" + grant[1] + ",'T_CORE_MENU','" + grant[2] + "')");
                }
            }
            int port = ((ServletWebServerApplicationContext) application).getWebServer().getPort();
            Reply anonymous = request(port, "GET", PERSONNEL + "/page/edit-list", null, null, null);
            check(anonymous.status == 403 && anonymous.body.equals("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"),
                    "anonymous protected page is denied before test marker: HTTP " + anonymous.status);
            deny(port, "GET", "/secure/core/module/sys/menus", null, "anonymous menu request is denied");
            Reply key = request(port, "GET", "/open/security/public-key", null, null, null);
            check(key.status == 200, "scanned public-key route is registered: HTTP " + key.status);
            check("no-store".equals(key.cacheControl), "public key is not cached");
            Cipher client = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            client.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(key.body))));
            String encoded = Base64.getEncoder().encodeToString(client.doFinal("secret".getBytes(StandardCharsets.UTF_8)));
            String form = "id=allowed&password=" + URLEncoder.encode(encoded, "UTF-8");
            Reply login = request(port, "POST", "/core/module/sys/login", form, key.body, null);
            check(login.status == 200, "scanned login route authenticates against Oracle: HTTP " + login.status);
            check(login.cookie != null, "login establishes an HTTP session");
            Reply second = request(port, "POST", "/core/module/sys/login", form, key.body, login.cookie);
            check(second.status == 200 && second.cookie != null && !login.cookie.equals(second.cookie),
                    "successful login rotates the existing session id");
            menuIds(port, second.cookie, "100001");
            verifyGuard(port, source, audit, encoded, key.body, second.cookie);
            Reply logout = request(port, "POST", "/core/module/sys/logout", "", null, second.cookie);
            check(logout.status == 200, "scanned logout route clears the session");
            deny(port, "GET", "/secure/core/module/sys/menus", second.cookie, "logged-out menu request is denied");
            Reply staleLogout = request(port, "POST", "/core/module/sys/logout", "", null, second.cookie);
            check(staleLogout.status == 200, "logout remains idempotent for an invalidated session");
            for (int index = 0; index < 45; index++) {
                Reply retry = request(port, "POST", "/core/module/sys/login", form, key.body, null);
                check(retry.status == 200, "configured audit supports repeated real HTTP login");
                check(request(port, "POST", "/core/module/sys/logout", "", null, retry.cookie).status == 200,
                        "configured audit supports repeated real HTTP logout");
            }
        }
        check(Files.isRegularFile(audit) && Files.size(audit) > 0, "configured audit sink persists HTTP outcomes");
        String records = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
        check(records.contains("\"action\":\"login\"") && records.contains("\"action\":\"logout\"")
                && records.contains("\"actorId\":\"allowed\""), "login and session logout retain auditable identity");
        check(!records.contains("secret"), "audit records omit credentials");
        check(Files.isRegularFile(audit.resolveSibling(audit.getFileName() + ".1"))
                && Files.isRegularFile(audit.resolveSibling(audit.getFileName() + ".2"))
                && !Files.exists(audit.resolveSibling(audit.getFileName() + ".3")), "audit rotation retains the configured archive count");
        for (int index = 0; index <= 2; index++) {
            Path file = index == 0 ? audit : audit.resolveSibling(audit.getFileName() + "." + index);
            check(Files.size(file) <= 4096, "audit file size respects its configured limit");
            if (Files.getFileStore(file).supportsFileAttributeView("posix")) {
                check(Files.getPosixFilePermissions(file).equals(java.nio.file.attribute.PosixFilePermissions.fromString("rw-------")),
                        "new audit files are private to the runtime user");
            }
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                check(com.alibaba.fastjson.JSON.parseObject(line).containsKey("occurredAt"), "rotated audit records remain valid JSON");
            }
        }
        try (ConfigurableApplicationContext templates = start(database, audit, "MD5", "file", false)) {
            check(templates.getBeansOfType(DataSource.class).isEmpty(), "template startup has no datasource");
            int port = ((ServletWebServerApplicationContext) templates).getWebServer().getPort();
            deny(port, "GET", PERSONNEL + "/page/edit-list", null, "missing backend still protects pages");
            check(request(port, "GET", "/open/security/public-key", null, null, null).status == 404,
                    "template startup omits the identity key route");
            check(request(port, "POST", "/core/module/sys/login", "", null, null).status == 404,
                    "template startup omits the login route");
            check(request(port, "GET", "/secure/core/module/sys/menus", null, null, null).status == 404,
                    "template startup omits the menu route");
        }
        System.out.println("owned identity Boot HTTP: PASS (" + checks + " checks; scanned routes, Boot datasource, Oracle, configuration rejection, bounded audit, no-datasource startup)");
    }

    private static void verifyGuard(int port, DataSource source, Path audit, String encoded, String key, String personnel) throws Exception {
        String low = login(port, "low", encoded, key);
        String equipmentReader = login(port, "equipment-reader", encoded, key);
        String consumableReader = login(port, "consumable-reader", encoded, key);
        String equipmentManager = login(port, "equipment-manager", encoded, key);
        String consumableManager = login(port, "consumable-manager", encoded, key);
        String admin = login(port, "admin", encoded, key);
        menuIds(port, low);
        menuIds(port, equipmentReader, "20251000001");
        menuIds(port, admin, "100001", "101501", "20250800003", "20251000001", "430201");
        allow(port, "GET", PERSONNEL + "/page/edit-list", personnel, "personnel menu permits page");
        allow(port, "POST", PERSONNEL + "/queries/raw", personnel, "personnel menu permits query");
        for (String prefix : new String[]{PERSONNEL, EQUIPMENT, CONSUMABLE}) {
            deny(port, "GET", prefix, null, "anonymous root denied");
            deny(port, "GET", prefix + "/123", low, "unprivileged session denied");
            deny(port, "POST", prefix + "/queries/raw", null, "anonymous query denied");
            allow(port, "GET", prefix + "/page/edit-list", admin, "all-menu grant permits management page");
            for (String method : new String[]{"POST", "PUT", "DELETE"})
                deny(port, method, prefix + "/unknown-write", admin, "unknown writes denied even for manager");
            deny(port, "POST", prefix + "/queries-delete", admin, "query-looking write denied");
            deny(port, "POST", prefix + "/queries/delete", admin, "unsupported query suffix denied");
            deny(port, "POST", prefix + "/delete/queries", admin, "unknown nested action denied");
        }
        String[] prefixes = {EQUIPMENT, CONSUMABLE};
        String[] readers = {equipmentReader, consumableReader};
        String[] managers = {equipmentManager, consumableManager};
        for (int index = 0; index < prefixes.length; index++) {
            String prefix = prefixes[index], reader = readers[index], manager = managers[index];
            allow(port, "GET", prefix + "/123", reader, "query menu permits detail");
            allow(port, "HEAD", prefix + "/123", reader, "query menu permits detail HEAD");
            allow(port, "OPTIONS", prefix + "/123", reader, "query menu permits detail OPTIONS");
            allow(port, "POST", prefix + "/queries/raw", reader, "query menu permits POST raw query");
            allow(port, "GET", prefix + "/page/edit-list?readonly=1", reader, "query menu permits explicit readonly page");
            deny(port, "GET", prefix + "/page/edit-list", reader, "query menu cannot open management page");
            deny(port, "GET", prefix + "/page/edit-list?readonly=0", reader, "query menu cannot disable readonly");
            deny(port, "GET", prefix + "/page/edit-list?readonly=1&readonly=0", reader, "ambiguous readonly flag denied");
            deny(port, "GET", prefix + "/page/add", reader, "query menu cannot open add page");
            allow(port, "GET", prefix + "/page/edit-list", manager, "management grant permits edit page");
            allow(port, "GET", prefix + "/page/add", manager, "management grant permits add page");
            allow(port, "POST", prefix + "/queries", manager, "management grant also permits reads");
            deny(port, "POST", prefix + "/queries/raw", personnel, "personnel grant does not authorize other domains");
        }
        allow(port, "POST", EQUIPMENT + "/123/test-items/queries", equipmentReader, "equipment nested read query");
        allow(port, "POST", CONSUMABLE + "/123/stocks/queries", consumableReader, "consumable stock read query");
        allow(port, "POST", CONSUMABLE + "/stocks/456/logs/queries", consumableReader, "consumable stock log read query");
        deny(port, "GET", CONSUMABLE + "/123", equipmentReader, "equipment menu cannot read consumables");
        String[][] paths = {{"/secure/emc/module/mdm/emp/%2565mps/page/edit-list"},
                {"/secure/emc/module/mdm/emp/%65mps/page/edit-list"},
                {PERSONNEL + "//123"}, {PERSONNEL + ";x=1/123"}, {PERSONNEL + "/123/"},
                {"/secure/emc/module/mdm/emp/%252565mps/123"}, {PERSONNEL + "/./123"},
                {"/secure/emc/module/mdm/emp/other/../emps/123"}};
        for (String[] path : paths) deny(port, "GET", path[0], admin, "ambiguous protected path denied");
        check(readAudit(audit).contains("non-canonical-path"), "ambiguous path denial is audited");
        allow(port, "GET", "/secure/unrelated-marker", null, "bounded guard leaves unrelated route alone");
        allow(port, "GET", PERSONNEL + "-unrelated/123", null, "prefix boundary does not capture sibling names");
        try (Connection connection = source.getConnection(); Statement update = connection.createStatement()) {
            update.executeUpdate("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=4");
            update.executeUpdate("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG='1' WHERE ID=5");
        }
        deny(port, "GET", EQUIPMENT + "/123", equipmentReader, "same session loses revoked menu immediately");
        deny(port, "POST", CONSUMABLE + "/queries/raw", consumableReader, "same session loses suspended role immediately");
        menuIds(port, equipmentReader);
        menuIds(port, consumableReader);
        String records = readAudit(audit);
        check(records.contains("equipment.read") && records.contains("consumable.read")
                && records.contains("\"outcome\":\"DENIED\""), "current-session permission denials reach configured audit");
        try (Connection connection = source.getConnection(); Statement update = connection.createStatement()) {
            update.executeUpdate("UPDATE T_CORE_USER SET LASTSUSPENDEDFLAG='1' WHERE ID='equipment-manager'");
        }
        deny(port, "GET", "/secure/core/module/sys/menus", equipmentManager, "suspended identity cannot read menus");
        deny(port, "GET", EQUIPMENT + "/page/edit-list", equipmentManager, "same session loses suspended account");
        try (Connection connection = source.getConnection(); Statement update = connection.createStatement()) {
            update.executeUpdate("UPDATE T_CORE_USER SET LASTSUSPENDEDFLAG='0' WHERE ID='equipment-manager'");
        }
        deny(port, "GET", EQUIPMENT + "/123", equipmentManager, "invalidated identity requires a new login");
    }

    private static String login(int port, String user, String encoded, String key) throws Exception {
        Reply reply = request(port, "POST", "/core/module/sys/login", "id=" + user + "&password="
                + URLEncoder.encode(encoded, "UTF-8"), key, null);
        check(reply.status == 200 && reply.cookie != null, "synthetic role user authenticates: " + user);
        return reply.cookie;
    }

    private static void menuIds(int port, String cookie, String... expected) throws Exception {
        Reply reply = request(port, "GET", "/secure/core/module/sys/menus", null, null, cookie);
        check(reply.status == 200, "Boot registers the current-user menu controller: HTTP " + reply.status);
        check("no-store".equals(reply.cacheControl), "current menu grants are not cached");
        com.alibaba.fastjson.JSONArray menus = com.alibaba.fastjson.JSON.parseObject(reply.body).getJSONArray("menus");
        List<String> actual = new ArrayList<String>();
        for (int index = 0; index < menus.size(); index++) actual.add(menus.getJSONObject(index).getString("id"));
        check(actual.equals(Arrays.asList(expected)), "current role grants determine menu IDs: " + actual);
    }

    private static void allow(int port, String method, String path, String cookie, String label) throws Exception {
        Reply reply = request(port, method, path, "POST".equals(method) ? "" : null, null, cookie);
        check(reply.status == 200 && ("HEAD".equals(method) || "test-only-guard-passage".equals(reply.body)),
                label + ": HTTP " + reply.status);
    }

    private static void deny(int port, String method, String path, String cookie, String label) throws Exception {
        Reply reply = request(port, method, path, "POST".equals(method) || "PUT".equals(method) ? "" : null, null, cookie);
        check(reply.status == 403 && reply.body.equals("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"),
                label + ": HTTP " + reply.status);
    }

    private static String readAudit(Path audit) throws Exception {
        StringBuilder records = new StringBuilder();
        for (int index = 0; index <= 2; index++) {
            Path path = index == 0 ? audit : audit.resolveSibling(audit.getFileName() + "." + index);
            if (Files.isRegularFile(path)) records.append(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
        }
        return records.toString();
    }

    private static ConfigurableApplicationContext start(Properties database, Path audit) {
        return start(database, audit, "MD5", "file", true);
    }

    private static void rejectConfiguration(Properties database, Path audit, String encoder, String sink, String property) {
        try (ConfigurableApplicationContext ignored = start(database, audit, encoder, sink, true)) {
            throw new AssertionError("missing or unsupported configuration was accepted: " + property);
        } catch (RuntimeException failure) {
            boolean found = false;
            for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                if (cause.getMessage() != null && cause.getMessage().contains(property)) found = true;
            }
            check(found, "startup rejects configuration with an actionable property name: " + property);
        }
    }

    private static ConfigurableApplicationContext start(Properties database, Path audit, String encoder, String sink, boolean datasource) {
        List<String> options = new ArrayList<String>(Arrays.asList(
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-identity-boot-test",
                "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
                        + (datasource ? "" : ",org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"),
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
                "--spring.datasource.url=" + database.getProperty("url"),
                "--spring.datasource.username=" + database.getProperty("user"),
                "--spring.datasource.password=" + database.getProperty("password"),
                "--owned.identity.audit.max-bytes=4096", "--owned.identity.audit.archive-count=2"));
        if (audit != null) options.add("--owned.identity.audit.path=" + audit.toAbsolutePath());
        if (encoder != null) options.add("--sunway.user.password.encoder.type=" + encoder);
        if (sink != null) options.add("--owned.identity.audit.type=" + sink);
        return SunwayApplication.run(OwnedIdentityBootContractTest.class, options.toArray(new String[0]));
    }

    private static Reply request(int port, String method, String path, String form, String key, String cookie) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        connection.setConnectTimeout(5000); connection.setReadTimeout(5000);
        connection.setRequestMethod(method); connection.setInstanceFollowRedirects(false);
        if (key != null) connection.setRequestProperty("SECURITY-RSA-KEY", key);
        if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        if (form != null) {
            connection.setDoOutput(true); connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] content = form.getBytes(StandardCharsets.UTF_8); connection.setFixedLengthStreamingMode(content.length);
            try (OutputStream output = connection.getOutputStream()) { output.write(content); }
        }
        try {
            int status = connection.getResponseCode();
            String body = "";
            InputStream input = status < 400 ? connection.getInputStream() : connection.getErrorStream();
            if (input != null) try (InputStream in = input; ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024]; int size;
                while ((size = in.read(buffer)) != -1) bytes.write(buffer, 0, size);
                body = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            }
            String session = connection.getHeaderField("Set-Cookie");
            if (session != null && session.indexOf(';') >= 0) session = session.substring(0, session.indexOf(';'));
            return new Reply(status, body, session, connection.getHeaderField("Cache-Control"));
        } finally { connection.disconnect(); }
    }
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    private static final class Reply {
        final int status; final String body, cookie, cacheControl;
        Reply(int status, String body, String cookie, String cacheControl) {
            this.status = status; this.body = body; this.cookie = cookie; this.cacheControl = cacheControl;
        }
    }
}
