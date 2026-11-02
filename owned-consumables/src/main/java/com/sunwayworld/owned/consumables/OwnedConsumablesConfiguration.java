package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.resource.impl.EmcConsumeResourceImpl;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Main/runtime wiring for unchanged original consumables contracts. */
@AutoConfigureAfter(OwnedIdentityConfiguration.class)
@ConditionalOnBean({DataSource.class, JdbcPersonnelIdentity.class, JdbcPersonnelIdentityProvider.class, OwnedIdentityHttpBoundary.class})
public class OwnedConsumablesConfiguration {
    @Bean public OwnedConsumablesReads ownedConsumablesReads(DataSource source, JdbcPersonnelIdentity identity, JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary) { return new OwnedConsumablesReads(source, identity, provider, boundary); }
    @Bean public OwnedConsumablesReadDao ownedConsumablesReadDao(OwnedConsumablesReads reads, DataSource source,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        return new OwnedConsumablesReadDao(reads, source, provider, boundary, audit);
    }
    @Bean public OwnedConsumablesStockDao ownedConsumablesStockDao(OwnedConsumablesReads reads, DataSource source,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        return new OwnedConsumablesStockDao(reads, source, provider, boundary, audit);
    }
    @Bean public OwnedConsumablesStockLogDao ownedConsumablesStockLogDao(OwnedConsumablesReads reads) { return new OwnedConsumablesStockLogDao(reads); }
    @Bean public OwnedConsumablesReadService ownedConsumablesReadService(OwnedConsumablesReadDao dao) { return new OwnedConsumablesReadService(dao); }
    @Bean public OwnedConsumablesStockService ownedConsumablesStockService(OwnedConsumablesStockDao dao) { return new OwnedConsumablesStockService(dao); }
    @Bean public OwnedConsumablesStockLogService ownedConsumablesStockLogService(OwnedConsumablesStockLogDao dao) { return new OwnedConsumablesStockLogService(dao); }
    @Bean @ConditionalOnMissingBean(OwnedMdmStockReadController.class)
    public OwnedMdmStockReadController ownedMdmStockReadController(DataSource source,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary,
            org.springframework.core.env.Environment environment, AuditEventSink audit) {
        return new OwnedMdmStockReadController(source, provider, boundary, environment, audit);
    }
    @Bean @ConditionalOnMissingBean(OwnedMtlUseRecordReadController.class)
    public OwnedMtlUseRecordReadController ownedMtlUseRecordReadController(
            OwnedIdentityHttpBoundary boundary, DataSource source) {
        return new OwnedMtlUseRecordReadController(boundary, source);
    }
    @Bean @ConditionalOnMissingBean(OwnedPfStockReadController.class)
    public OwnedPfStockReadController ownedPfStockReadController(
            OwnedIdentityHttpBoundary boundary, OwnedMdmStockReadController delegate) {
        return new OwnedPfStockReadController(boundary, delegate);
    }
    @Bean @ConditionalOnMissingBean(EmcConsumeResourceImpl.class) public EmcConsumeResourceImpl emcConsumeResource() { return new EmcConsumeResourceImpl(); }
    @Bean @ConditionalOnMissingBean(OwnedConsumablesExceptionHandler.class) public OwnedConsumablesExceptionHandler ownedConsumablesExceptionHandler() { return new OwnedConsumablesExceptionHandler(); }
}
