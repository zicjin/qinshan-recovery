import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.sql.DataSource;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Test lifecycle only; report mode is explicitly isolated replay. */
public final class OwnedReportBrowserApplication {
    public static void main(String[] args) throws Exception {
        System.out.println("REPORT_APP_BEFORE_MAIN"); System.out.flush();
        Properties p = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(args[0]))) { p.load(input); }
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-report-test",
                "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
                "--spring.datasource.url=" + p.getProperty("url"), "--spring.datasource.username=" + p.getProperty("user"),
                "--spring.datasource.password=" + p.getProperty("password"), "--sunway.user.password.encoder.type=MD5",
                "--owned.identity.audit.type=file", "--owned.identity.audit.path=" + args[3],
                "--owned.report.replay.mode=" + args[5], "--owned.report.replay.root=" + args[4]});
        System.out.println("REPORT_APP_AFTER_MAIN"); System.out.flush();
        ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
        try {
            int port = ((ServletWebServerApplicationContext) app).getWebServer().getPort();
            Files.write(Paths.get(args[1]), String.valueOf(port).getBytes(StandardCharsets.UTF_8));
            while (!Files.exists(Paths.get(args[2]))) Thread.sleep(200);
        } finally { app.close(); }
        System.exit(0);
    }
}
