package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.service.*;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/** Auto-configuration only; the historical application scans the unchanged resource/controller. */
@AutoConfigureAfter(OwnedIdentityConfiguration.class)
@ConditionalOnBean({DataSource.class, JdbcPersonnelIdentity.class, JdbcPersonnelIdentityProvider.class, OwnedIdentityHttpBoundary.class})
public class OwnedEquipmentConfiguration {
    @Bean public OwnedEquipmentReadDao ownedEquipmentReadDao(DataSource source, JdbcPersonnelIdentity identity,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        return new OwnedEquipmentReadDao(source,identity,provider,boundary,audit);
    }
    @Bean public OwnedEquipmentReadService ownedEquipmentReadService(OwnedEquipmentReadDao dao) { return new OwnedEquipmentReadService(dao); }
    @Bean public OwnedEquipmentPlanDao ownedEquipmentPlanDao(DataSource source, JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) { return new OwnedEquipmentPlanDao(source, provider, boundary, audit); }
    @Bean public EmcEquiptPlanService ownedEquipmentPlanService(OwnedEquipmentPlanDao dao, Environment environment) {
        return "isolated".equals(environment.getProperty("owned.equipment.write.mode"))
                ? new OwnedEquipmentPlanService(dao) : unsupported(EmcEquiptPlanService.class);
    }
    @Bean public OwnedEquipmentRecordDao ownedEquipmentRecordDao(DataSource source, JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) { return new OwnedEquipmentRecordDao(source, provider, boundary, audit); }
    @Bean public EmcEquiptRecordService ownedEquipmentRecordService(OwnedEquipmentRecordDao dao, Environment environment) {
        return "isolated".equals(environment.getProperty("owned.equipment.write.mode"))
                ? new OwnedEquipmentRecordService(dao) : unsupported(EmcEquiptRecordService.class);
    }
    @Bean public OwnedEquipmentUseRecordDao ownedEquipmentUseRecordDao(DataSource source, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        return new OwnedEquipmentUseRecordDao(source, provider, boundary, audit);
    }
    @Bean public EmcEquiptUseRecordService ownedEquipmentUseRecordService(OwnedEquipmentUseRecordDao dao, Environment environment) {
        return "isolated".equals(environment.getProperty("owned.equipment.write.mode"))
                ? new OwnedEquipmentUseRecordService(dao) : unsupported(EmcEquiptUseRecordService.class);
    }
    @Bean public OwnedEquipmentTestMethodDao ownedEquipmentTestMethodDao() { return new OwnedEquipmentTestMethodDao(); }
    @Bean public EmcEquiptTestMethodService ownedEquipmentTestMethodService(OwnedEquipmentTestMethodDao dao,
            DataSource source, JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary,
            AuditEventSink audit, Environment environment) {
        return "isolated".equals(environment.getProperty("owned.equipment.write.mode"))
                ? new OwnedEquipmentTestMethodService(dao, source, provider, boundary, audit, environment)
                : unsupported(EmcEquiptTestMethodService.class);
    }
    @Bean public OwnedEquipmentPerCheckPlanDao ownedEquipmentPerCheckPlanDao(DataSource source, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        return new OwnedEquipmentPerCheckPlanDao(source, provider, boundary, audit);
    }
    @Bean public EmcEquiptPerCheckPlanService ownedEquipmentPerCheckPlanService(OwnedEquipmentPerCheckPlanDao dao, Environment environment) {
        return "isolated".equals(environment.getProperty("owned.equipment.write.mode"))
                ? new OwnedEquipmentPerCheckPlanService(dao) : unsupported(EmcEquiptPerCheckPlanService.class);
    }
    @Bean public OwnedEquipmentPerCheckRecordDao ownedEquipmentPerCheckRecordDao(DataSource source, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        return new OwnedEquipmentPerCheckRecordDao(source, provider, boundary, audit);
    }
    @Bean public EmcEquiptPerCheckRecordService ownedEquipmentPerCheckRecordService(OwnedEquipmentPerCheckRecordDao dao, Environment environment) {
        return "isolated".equals(environment.getProperty("owned.equipment.write.mode"))
                ? new OwnedEquipmentPerCheckRecordService(dao) : unsupported(EmcEquiptPerCheckRecordService.class);
    }
    @Bean public OwnedEquipmentMaintDao ownedEquipmentMaintDao(DataSource source, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary) { return new OwnedEquipmentMaintDao(source, provider, boundary); }
    @Bean public EmcEquiptMaintService ownedEquipmentMaintService(OwnedEquipmentMaintDao dao,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit,
            Environment environment) {
        return "isolated".equals(environment.getProperty("owned.equipment.write.mode"))
                ? new OwnedEquipmentMaintService(dao, provider, boundary, audit, environment)
                : unsupported(EmcEquiptMaintService.class);
    }
    @Bean public OwnedEquipmentMaintainRepairDao ownedEquipmentMaintainRepairDao(DataSource source, JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit, Environment environment) { return new OwnedEquipmentMaintainRepairDao(source, provider, boundary, audit, environment); }
    @Bean public EmcEquiptMaintainRepairService ownedEquipmentMaintainRepairService(OwnedEquipmentMaintainRepairDao dao) { return new OwnedEquipmentMaintainRepairService(dao); }

    /** These original dependencies are wired solely to reject every unsupported operation. */
    private static <T> T unsupported(Class<T> contract) {
        return contract.cast(Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(proxy,method,args)->{
            if(method.getDeclaringClass()==Object.class) {
                if("toString".equals(method.getName())) return "Unsupported equipment auxiliary service: "+contract.getSimpleName();
                if("hashCode".equals(method.getName())) return System.identityHashCode(proxy);
                if("equals".equals(method.getName())) return proxy==args[0];
            }
            throw new UnsupportedOperationException("Equipment auxiliary flow is not implemented: "+contract.getSimpleName()+"."+method.getName());
        }));
    }
}
