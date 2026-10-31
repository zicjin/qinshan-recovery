import com.sunwayworld.emc.module.mdm.emp.emp.controller.EmcEmpController;
import com.sunwayworld.emc.projectfunction.GikamWpjcApplication;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Actual entrypoint, controller and templates over HTTP; excludes business services. */
public final class OwnedPersonnelHttpCheck {
    private static String get(int port, String path) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(5000);
        connection.setInstanceFollowRedirects(false);
        try {
            if (connection.getResponseCode() != 200) {
                throw new AssertionError(path + " returned HTTP " + connection.getResponseCode());
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (InputStream body = connection.getInputStream()) {
                byte[] buffer = new byte[4096];
                for (int count; (count = body.read(buffer)) != -1;) bytes.write(buffer, 0, count);
            }
            return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            connection.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        GikamWpjcApplication.main(new String[]{
            "--server.address=127.0.0.1", "--server.port=0",
            "--spring.config.name=owned-personnel-http-probe",
            "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration"
        });
        ServletWebServerApplicationContext context = (ServletWebServerApplicationContext) OwnedApplicationContext.getApplicationContext();
        if (context == null) throw new AssertionError("actual entrypoint did not publish a web context");
        try {
            if (!context.isActive() || context.getBeansOfType(EmcEmpController.class).size() != 1) {
                throw new AssertionError("personnel controller was not discovered by the actual application");
            }
            int port = context.getWebServer().getPort();
            String base = "/secure/emc/module/mdm/emp/emps/page/";
            String[][] pages = {
                {"edit-list", "emc-emp-edit-list.js", "emcEmp.editPage.init"},
                {"detail", "emc-emp-detail.js", "emcEmp.detailPage.init"},
                {"choose-list", "emc-emp-choose-list.js", "emcEmp.choosePage.init"},
                {"report-users/choose-list", "emc-emp-report-choose-list.js", "emcEmp.reportChoosePage.init"},
                {"sample/choose-list", "emc-emp-sample-choose-list.js", "emcEmp.sampleChoosePage.init"}
            };
            for (String[] page : pages) {
                String body = get(port, base + page[0]);
                if (!body.contains(page[1]) || body.contains("[[${param}]]")) {
                    throw new AssertionError("template was not rendered for " + page[0]);
                }
            }
            String script = get(port, "/static/emc/module/mdm/emp/emps/emc-emp-edit-list.js");
            if (!script.contains("/queries/raw") || !script.contains("userOrg_filter")) {
                throw new AssertionError("recovered personnel script was not served");
            }
            System.out.println("owned personnel HTTP: PASS (actual main, 5 rendered templates, original list script)");
        } finally {
            context.close();
        }
        if (OwnedApplicationContext.getApplicationContext() != null) throw new AssertionError("web context was not cleared");
    }
}
