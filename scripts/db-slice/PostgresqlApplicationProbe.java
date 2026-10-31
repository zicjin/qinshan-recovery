import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.*;
import javax.crypto.Cipher;
import javax.sql.DataSource;

/** Actual owned application login and HTTP stop-loss attempt against disposable PostgreSQL. */
public final class PostgresqlApplicationProbe {
    private static final String[] ROUTES = {
        "/secure/emc/module/mdm/emp/emps/queries/raw",
        "/secure/emc/module/mdm/equipt/equipts/queries/raw",
        "/secure/emc/module/item/consume/consumes/queries/raw",
        "/secure/core/module/sys/report-configs/lines/1",
        "/secure/core/module/sys/menus"
    };
    private static String json(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " "); }
    private static void emit(String line) { System.out.println(line); System.out.flush(); }
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(args[0]))) { p.load(in); }
        String url = p.getProperty("url"), user = p.getProperty("user"), password = p.getProperty("password");
        try {
            org.postgresql.ds.PGSimpleDataSource ds = new org.postgresql.ds.PGSimpleDataSource();
            ds.setURL(url); ds.setUser(user); ds.setPassword(password);
            JdbcPersonnelIdentity identity = new JdbcPersonnelIdentity(ds, "MD5");
            try { identity.authenticate("e-1", "secret".toCharArray()); emit("DIRECT|login|unexpected-success"); }
            catch (RuntimeException failure) { emit("DIRECT|login|" + failure.getClass().getName() + "|" + safe(failure)); }
            catch (SQLException failure) { emit("DIRECT|login|SQLException|" + safe(failure)); }
            try { new com.sunwayworld.owned.report.OwnedReportReadDao(ds).find("1", "e-1"); emit("DIRECT|report-dao|unexpected-success"); }
            catch (SQLException failure) { emit("DIRECT|report-dao|SQLException|" + safe(failure)); }
        } catch (Throwable failure) { emit("DIRECT|probe|" + failure.getClass().getName() + "|" + safe(failure)); }
        try {
            com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[] {
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-postgresql-stoploss",
                "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=org.postgresql.Driver", "--spring.datasource.url=" + url,
                "--spring.datasource.username=" + user, "--spring.datasource.password=" + password,
                "--sunway.user.password.encoder.type=MD5", "--owned.identity.audit.type=file",
                "--owned.identity.audit.path=" + args[1]
            });
            org.springframework.context.ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
            try {
                int port = ((org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext) app).getWebServer().getPort();
                emit("APP|started|" + port);
                Response key = http(port, "GET", "/open/security/public-key", null, null, null);
                emit("HTTP|public-key|" + key.status + "|" + (key.body.matches("[A-Za-z0-9+/=]+") ? "base64-x509" : "unexpected"));
                String encrypted = encrypt("secret", key.body);
                Response login = http(port, "POST", "/core/module/sys/login", "id=e-1&password=" + URLEncoder.encode(encrypted, "UTF-8"), null, key.body);
                emit("HTTP|login|" + login.status + "|" + bodyToken(login.body));
                for (String route : ROUTES) {
                    String method = route.contains("report-configs") || route.endsWith("/menus") ? "GET" : "POST";
                    Response attempt = http(port, method, route, method.equals("GET") ? null : "{}", login.cookie, null);
                    emit("HTTP|" + route + "|" + attempt.status + "|" + bodyToken(attempt.body));
                }
                Response logout = http(port, "POST", "/core/module/sys/logout", null, login.cookie, null);
                emit("HTTP|logout|" + logout.status + "|" + bodyToken(logout.body));
            } finally { app.close(); }
        } catch (Throwable failure) { emit("APP|startup|" + failure.getClass().getName() + "|" + safe(failure)); }
    }
    private static String encrypt(String plaintext, String pem) throws Exception {
        String body = pem.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s+", "");
        PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(body)));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8)));
    }
    private static Response http(int port, String method, String path, String body, String cookie, String key) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        c.setConnectTimeout(5000); c.setReadTimeout(10000); c.setRequestMethod(method); c.setInstanceFollowRedirects(false);
        if (cookie != null) c.setRequestProperty("Cookie", cookie);
        if (key != null) c.setRequestProperty("SECURITY-RSA-KEY", key);
        if (body != null) { c.setDoOutput(true); c.setRequestProperty("Content-Type", key == null ? "application/json" : "application/x-www-form-urlencoded"); byte[] b = body.getBytes(StandardCharsets.UTF_8); c.setFixedLengthStreamingMode(b.length); try (OutputStream out = c.getOutputStream()) { out.write(b); } }
        int status = c.getResponseCode(); InputStream stream = status >= 400 ? c.getErrorStream() : c.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (stream != null) try (InputStream in = stream) { byte[] b = new byte[1024]; int n; while ((n = in.read(b)) >= 0) out.write(b, 0, n); }
        String setCookie = c.getHeaderField("Set-Cookie"); c.disconnect();
        return new Response(status, new String(out.toByteArray(), StandardCharsets.UTF_8), setCookie == null ? cookie : setCookie.split(";", 2)[0]);
    }
    private static String bodyToken(String body) {
        String s = body == null ? "" : body.replaceAll("[\\r\\n\\t ]+", " ").trim();
        if (s.contains("GIKAM.SECURITY.LOGIN.UNAVAILABLE")) return "LOGIN_UNAVAILABLE";
        if (s.contains("GIKAM.PERMISSION.UNAUTHORIZED")) return "PERMISSION_UNAUTHORIZED";
        if (s.contains("REPORT_UNAUTHORIZED")) return "REPORT_UNAUTHORIZED";
        if (s.startsWith("<html>")) return "html-error";
        return s.length() > 120 ? s.substring(0, 120) : s;
    }
    private static String safe(Throwable failure) {
        String state = failure instanceof SQLException ? ((SQLException) failure).getSQLState() : null;
        String message = failure.getMessage();
        String function = message != null && message.contains("nvl(") ? ";function=nvl" : message != null && message.contains("to_char(") ? ";function=to_char" : "";
        return (state == null ? "" : "SQLSTATE=" + state + ";") + failure.getClass().getSimpleName() + function;
    }
    private static final class Response { final int status; final String body, cookie; Response(int s, String b, String c) { status=s; body=b; cookie=c; } }
}
