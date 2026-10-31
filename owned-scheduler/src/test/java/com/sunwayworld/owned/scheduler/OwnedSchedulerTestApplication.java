package com.sunwayworld.owned.scheduler;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Lifecycle only: every task/page route is supplied by the shipped module. */
public final class OwnedSchedulerTestApplication {
    public static void main(String[] args) throws Exception {
        Properties p=new Properties();
        try(InputStream in=Files.newInputStream(Paths.get(args[0]))){p.load(in);}
        List<String> options=new ArrayList<String>(Arrays.asList(
            "--server.address=127.0.0.1","--server.port=0","--spring.config.name=owned-scheduler-test",
            "--spring.main.banner-mode=off","--spring.main.log-startup-info=false",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"));
        for(String key:p.stringPropertyNames())options.add("--"+key+"="+p.getProperty(key));
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(options.toArray(new String[0]));
        ConfigurableApplicationContext app=OwnedApplicationContext.getApplicationContext();
        try {
            int port=((ServletWebServerApplicationContext)app).getWebServer().getPort();
            Files.write(Paths.get(args[1]),String.valueOf(port).getBytes(StandardCharsets.UTF_8));
            long deadline=System.currentTimeMillis()+600000;
            while(!Files.exists(Paths.get(args[2]))&&System.currentTimeMillis()<deadline)Thread.sleep(100);
        } finally {app.close();}
    }
}
