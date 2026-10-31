package com.sunwayworld.owned.attachments.replay;
import java.net.URI;
import java.nio.file.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
/** Explicit replay-only wiring; merchant comes solely from trusted server configuration. */
@ConditionalOnProperty(name="owned.erp.mode",havingValue="replay")
public class OwnedErpReplayConfiguration {
    @Bean public OwnedErpReplayFileService ownedErpReplayFileService(Environment environment) {
        String base=required(environment,"owned.erp.replay.origin");
        String merchant=required(environment,"owned.erp.replay.merchant-id");
        String journal=required(environment,"owned.erp.replay.journal");
        int retries=environment.getProperty("owned.erp.replay.max-retries",Integer.class,0);
        return new OwnedErpReplayFileService(merchant,new OwnedErpReplayHttpTransport(URI.create(base)),Paths.get(journal),retries);
    }
    private static String required(Environment environment,String name) {
        String value=environment.getProperty(name);
        if(value==null||value.trim().isEmpty())throw new IllegalStateException("Explicit replay configuration required: "+name);
        return value;
    }
}
