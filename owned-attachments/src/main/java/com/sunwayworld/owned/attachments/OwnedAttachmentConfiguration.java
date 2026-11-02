package com.sunwayworld.owned.attachments;
import com.sunwayworld.emc.module.item.homeattachment.resource.impl.EmcHomeAttachmentResourceImpl;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import javax.sql.DataSource;
import java.nio.file.Paths;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
/** Main artifact wiring; explicit activation and storage are required. */
@AutoConfigureAfter(OwnedIdentityConfiguration.class)
@ConditionalOnBean({DataSource.class, OwnedIdentityHttpBoundary.class})
@ConditionalOnProperty(name = "owned.attachments.enabled", havingValue = "true")
public class OwnedAttachmentConfiguration {
    @Bean public OwnedAttachmentReadDao ownedAttachmentReadDao(DataSource ds, OwnedIdentityHttpBoundary identity) { return new OwnedAttachmentReadDao(ds, identity); }
    @Bean public OwnedAttachmentReadService ownedAttachmentReadService(OwnedAttachmentReadDao dao, DataSource ds,
            OwnedIdentityHttpBoundary identity, AuditEventSink audit, Environment environment) {
        return new OwnedAttachmentReadService(dao, ds, identity, audit, environment);
    }
    @Bean @ConditionalOnMissingBean(EmcHomeAttachmentResourceImpl.class)
    public EmcHomeAttachmentResourceImpl emcHomeAttachmentResource() { return new EmcHomeAttachmentResourceImpl(); }
    @Bean
    public OwnedAttachmentReadAliasController ownedAttachmentReadAliasController(
            OwnedAttachmentReadService service, OwnedIdentityHttpBoundary identity) {
        return new OwnedAttachmentReadAliasController(service, identity);
    }
}
