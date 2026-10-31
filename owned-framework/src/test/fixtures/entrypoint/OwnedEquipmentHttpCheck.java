import com.sunwayworld.GikamCoreApplication;
import com.sunwayworld.emc.module.mdm.equipt.equipt.controller.EmcEquiptController;
import com.sunwayworld.emc.projectfunction.GikamWpjcApplication;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Actual entrypoint and equipment page templates over loopback HTTP. */
public final class OwnedEquipmentHttpCheck {
    private static String get(int port, String path) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        connection.setConnectTimeout(3000); connection.setReadTimeout(5000); connection.setInstanceFollowRedirects(false);
        try {
            if (connection.getResponseCode() != 200) throw new AssertionError(path + " returned HTTP " + connection.getResponseCode());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (InputStream body = connection.getInputStream()) { byte[] buffer = new byte[4096]; for (int n; (n = body.read(buffer)) != -1;) bytes.write(buffer, 0, n); }
            return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        } finally { connection.disconnect(); }
    }
    public static void main(String[] args) throws Exception {
        GikamWpjcApplication.main(new String[]{"--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-equipment-http-probe", "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false", "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration"});
        ServletWebServerApplicationContext context = (ServletWebServerApplicationContext) OwnedApplicationContext.getApplicationContext();
        if (context == null) throw new AssertionError("actual entrypoint did not publish a web context");
        try {
            if (!context.isActive() || context.getBeansOfType(EmcEquiptController.class).size() != 1) throw new AssertionError("equipment controller was not discovered");
            int port = context.getWebServer().getPort(); String base = "/secure/emc/module/mdm/equipt/equipts/page/";
            String[][] pages = {{"edit-list", "emc-equipt-edit-list.js"}, {"choose-list", "emc-equipt-choose-list.js"}, {"simple-choose-list", "emc-equipt-simple-choose-list.js"}};
            for (String[] page : pages) { String body = get(port, base + page[0]); if (!body.contains(page[1]) || body.contains("[[${param}]]")) throw new AssertionError("template was not rendered for " + page[0]); }
            String script = get(port, "/static/emc/module/mdm/equipt/equipts/emc-equipt-edit-list.js");
            if (!script.contains("/queries/raw") || !script.contains("target_type") || !script.contains("equipList")) throw new AssertionError("equipment list script was not served");
            System.out.println("owned equipment HTTP: PARTIAL (actual main, 3 rendered templates, original list script; detail template absent and left unverified)");
        } finally { context.close(); }
        if (OwnedApplicationContext.getApplicationContext() != null) throw new AssertionError("web context was not cleared");
    }
}
