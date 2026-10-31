import com.sunwayworld.emc.projectfunction.GikamWpjcApplication;

/** Minimal long-lived server used only for the disposable sss2 page smoke. */
public final class OwnedPersonnelServer {
    public static void main(String[] args) {
        GikamWpjcApplication.main(new String[]{
            "--server.address=0.0.0.0", "--server.port=8081",
            "--spring.config.name=owned-personnel-sss2",
            "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration"
        });
    }
}
