package com.sunwayworld.owned.scheduler;

import java.io.IOException;
import java.nio.file.Paths;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

/** No component stereotype: enable solely through Boot auto-configuration. */
@ConditionalOnProperty(name="owned.scheduler.mode",havingValue="isolated-test")
public class OwnedSchedulerConfiguration {
    @Bean public OwnedSchedulerService ownedSchedulerService(@Value("${owned.scheduler.root}") String root,@Value("${owned.scheduler.max-attempts:3}")int attempts)throws IOException{return new OwnedSchedulerService(Paths.get(root),attempts);}
    @Bean @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(OwnedSchedulerIsolatedController.class) public OwnedSchedulerIsolatedController ownedSchedulerIsolatedController(OwnedSchedulerService service,@Value("${owned.scheduler.token-file}")String tokenFile)throws IOException{return new OwnedSchedulerIsolatedController(service,Paths.get(tokenFile));}
}
