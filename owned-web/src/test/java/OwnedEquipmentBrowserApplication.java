import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Minimal equipment-only browser lifecycle; consumables are intentionally absent. */
public final class OwnedEquipmentBrowserApplication {
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(args[0]))) { p.load(input); }
        List<String> options = new ArrayList<String>(Arrays.asList(
            "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-equipment-browser-test",
            "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
            "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
            "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver", "--spring.datasource.url="+p.getProperty("url"),
            "--spring.datasource.username="+p.getProperty("user"), "--spring.datasource.password="+p.getProperty("password"),
            "--server.tomcat.threads.max=12", "--server.tomcat.threads.min-spare=1",
            "--sunway.user.password.encoder.type=MD5", "--owned.identity.audit.type=file",
            "--owned.identity.audit.path="+args[3]));
        if (args.length > 4 && "isolated".equals(args[4])) options.add("--owned.equipment.write.mode=isolated");
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(options.toArray(new String[0]));
        ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
        try {
            int port = ((ServletWebServerApplicationContext)app).getWebServer().getPort();
            Files.write(Paths.get(args[1]),String.valueOf(port).getBytes(StandardCharsets.UTF_8));
            long deadline = System.currentTimeMillis()+600000;
            while (!Files.exists(Paths.get(args[2])) && System.currentTimeMillis()<deadline) Thread.sleep(200);
        } finally { app.close(); }
    }
}
