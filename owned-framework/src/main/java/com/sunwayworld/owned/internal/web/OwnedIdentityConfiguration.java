package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.context.OwnedSequenceProvider;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Imported through spring.factories after Boot's datasource registration.
 * Deliberately has no component/configuration stereotype: the application's
 * broad component scan must not evaluate its datasource condition early.
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OwnedIdentityConfiguration {
    @Bean
    @ConditionalOnMissingBean(OwnedSequenceProvider.class)
    public OwnedSequenceProvider ownedSequenceProvider(DataSource dataSource) {
        return new OwnedSequenceProvider(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(JdbcPersonnelIdentity.class)
    public JdbcPersonnelIdentity ownedJdbcPersonnelIdentity(DataSource dataSource, Environment environment) {
        String encoder = environment.getProperty("sunway.user.password.encoder.type");
        if (!"MD5".equals(encoder)) {
            throw new IllegalStateException("sunway.user.password.encoder.type must explicitly select supported MD5 mode");
        }
        String menuConfig = environment.getProperty("owned.identity.attachment-menu-ids", "").trim();
        String[] menuIds = menuConfig.length() == 0 ? new String[0] : menuConfig.split(",");
        for (int i = 0; i < menuIds.length; i++) menuIds[i] = menuIds[i].trim();
        String roleMenuConfig = environment.getProperty("owned.identity.role-menu-admin-menu-ids", "").trim();
        String[] roleMenuIds = roleMenuConfig.length() == 0 ? new String[0] : roleMenuConfig.split(",");
        for (int i = 0; i < roleMenuIds.length; i++) roleMenuIds[i] = roleMenuIds[i].trim();
        String resultConfig = environment.getProperty("owned.identity.result-menu-ids", "").trim();
        String[] resultIds = resultConfig.length() == 0 ? new String[0] : resultConfig.split(",");
        for (int i = 0; i < resultIds.length; i++) resultIds[i] = resultIds[i].trim();
        String orderRetainConfig = environment.getProperty("owned.identity.order-retain-menu-ids", "").trim();
        String[] orderRetainIds = orderRetainConfig.length() == 0 ? new String[0] : orderRetainConfig.split(",");
        for (int i = 0; i < orderRetainIds.length; i++) orderRetainIds[i] = orderRetainIds[i].trim();
        String orderRetainWriteConfig = environment.getProperty("owned.identity.order-retain-write-menu-ids", "").trim();
        String[] orderRetainWriteIds = orderRetainWriteConfig.length() == 0 ? new String[0] : orderRetainWriteConfig.split(",");
        for (int i = 0; i < orderRetainWriteIds.length; i++) orderRetainWriteIds[i] = orderRetainWriteIds[i].trim();
        String resultWriteConfig = environment.getProperty("owned.identity.result-write-menu-ids", "").trim();
        String[] resultWriteIds = resultWriteConfig.length() == 0 ? new String[0] : resultWriteConfig.split(",");
        for (int i = 0; i < resultWriteIds.length; i++) resultWriteIds[i] = resultWriteIds[i].trim();
        String auditConfig = environment.getProperty("owned.identity.audit-menu-ids", "9404").trim();
        String[] auditIds = auditConfig.length() == 0 ? new String[0] : auditConfig.split(",");
        for (int i = 0; i < auditIds.length; i++) auditIds[i] = auditIds[i].trim();
        return new JdbcPersonnelIdentity(dataSource, encoder, menuIds, roleMenuIds, resultIds,
                orderRetainIds, resultWriteIds, orderRetainWriteIds, auditIds);
    }

    @Bean
    @ConditionalOnMissingBean(OwnedIdentityProvider.class)
    public JdbcPersonnelIdentityProvider ownedIdentityProvider(JdbcPersonnelIdentity identity) {
        return new JdbcPersonnelIdentityProvider(identity);
    }

    @Bean
    @ConditionalOnMissingBean(AuditEventSink.class)
    public AuditEventSink ownedAuditEventSink(Environment environment) {
        if (!"file".equals(environment.getProperty("owned.identity.audit.type"))) {
            throw new IllegalStateException("Configure owned.identity.audit.type=file or provide an AuditEventSink bean");
        }
        String path = environment.getProperty("owned.identity.audit.path");
        if (path == null || path.trim().length() == 0) {
            throw new IllegalStateException("owned.identity.audit.path is required");
        }
        return new RotatingFileAuditEventSink(Paths.get(path),
                environment.getProperty("owned.identity.audit.max-bytes", Long.class, 10485760L),
                environment.getProperty("owned.identity.audit.archive-count", Integer.class, 5));
    }

    @Bean
    @ConditionalOnMissingBean(OwnedIdentityHttpBoundary.class)
    public OwnedIdentityHttpBoundary ownedIdentityHttpBoundary(OwnedIdentityProvider provider, AuditEventSink audit) {
        return new OwnedIdentityHttpBoundary(provider, audit);
    }

    @Bean
    @ConditionalOnMissingBean(OwnedRsaLoginCredentialDecoder.class)
    public OwnedRsaLoginCredentialDecoder ownedRsaLoginCredentialDecoder() {
        return new OwnedRsaLoginCredentialDecoder();
    }

    // The original project owns /core/module/sys/logins/account and its
    // login resource. Keep the owned identity facade available for the
    // explicit source-backed seam, but do not register overlapping handler
    // methods in the project application.

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSecurityKeyController.class)
    public OwnedSecurityKeyController ownedSecurityKeyController(OwnedRsaLoginCredentialDecoder decoder) {
        return new OwnedSecurityKeyController(decoder);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMenuController.class)
    public OwnedMenuController ownedMenuController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMenuController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedRoleMenuMatrixController.class)
    public OwnedRoleMenuMatrixController ownedRoleMenuMatrixController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        return new OwnedRoleMenuMatrixController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMenuManagementController.class)
    public OwnedMenuManagementController ownedMenuManagementController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource, Environment environment) {
        return new OwnedMenuManagementController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedRoleUserMatrixController.class)
    public OwnedRoleUserMatrixController ownedRoleUserMatrixController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        return new OwnedRoleUserMatrixController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrganizationManagementController.class)
    public OwnedOrganizationManagementController ownedOrganizationManagementController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource, Environment environment) {
        return new OwnedOrganizationManagementController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedResultRunController.class)
    public OwnedResultRunController ownedResultRunController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource) {
        return new OwnedResultRunController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedResultRunWriteController.class)
    public OwnedResultRunWriteController ownedResultRunWriteController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        return new OwnedResultRunWriteController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedResultItemController.class)
    public OwnedResultItemController ownedResultItemController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource, Environment environment) {
        return new OwnedResultItemController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderRetainController.class)
    public OwnedOrderRetainController ownedOrderRetainController(OwnedIdentityHttpBoundary boundary,
            DataSource dataSource) {
        return new OwnedOrderRetainController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderRetainWriteController.class)
    public OwnedOrderRetainWriteController ownedOrderRetainWriteController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource, Environment environment) {
        return new OwnedOrderRetainWriteController(boundary, dataSource, environment);
    }
    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderRetainHandleController.class)
    public OwnedOrderRetainHandleController ownedOrderRetainHandleController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource, Environment environment) {
        return new OwnedOrderRetainHandleController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.reagent", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(OwnedReagentQueryController.class)
    public OwnedReagentQueryController ownedReagentQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedReagentQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSolQueryController.class)
    public OwnedSolQueryController ownedSolQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedSolQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSubpackageQueryController.class)
    public OwnedSubpackageQueryController ownedSubpackageQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedSubpackageQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedWorkConditionQueryController.class)
    public OwnedWorkConditionQueryController ownedWorkConditionQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedWorkConditionQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSampleRemindQueryController.class)
    public OwnedSampleRemindQueryController ownedSampleRemindQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedSampleRemindQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSampleRecordQueryController.class)
    public OwnedSampleRecordQueryController ownedSampleRecordQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedSampleRecordQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderReceiptQueryController.class)
    public OwnedOrderReceiptQueryController ownedOrderReceiptQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOrderReceiptQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSampleMainReadController.class)
    public OwnedSampleMainReadController ownedSampleMainReadController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedSampleMainReadController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedIndexCategoryReadController.class)
    public OwnedIndexCategoryReadController ownedIndexCategoryReadController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedIndexCategoryReadController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMaterialApplyQueryController.class)
    public OwnedMaterialApplyQueryController ownedMaterialApplyQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMaterialApplyQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMtlPerCheckQueryController.class)
    public OwnedMtlPerCheckQueryController ownedMtlPerCheckQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMtlPerCheckQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMtlCategoryQueryController.class)
    public OwnedMtlCategoryQueryController ownedMtlCategoryQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMtlCategoryQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.reagent", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(OwnedReagentWriteController.class)
    public OwnedReagentWriteController ownedReagentWriteController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource, Environment environment) {
        return new OwnedReagentWriteController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMaterialApplyController.class)
    public OwnedMaterialApplyController ownedMaterialApplyController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource, Environment environment) {
        return new OwnedMaterialApplyController(boundary, dataSource, environment);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.audit", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(OwnedAuditTrailController.class)
    public OwnedAuditTrailController ownedAuditTrailController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedAuditTrailController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedAuditRecordController.class)
    public OwnedAuditRecordController ownedAuditRecordController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedAuditRecordController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrganizationMappingController.class)
    public OwnedOrganizationMappingController ownedOrganizationMappingController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOrganizationMappingController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedEquipmentResourceStatusController.class)
    public OwnedEquipmentResourceStatusController ownedEquipmentResourceStatusController(
            OwnedIdentityHttpBoundary boundary) {
        return new OwnedEquipmentResourceStatusController(boundary);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedEquipmentGenericResourceController.class)
    public OwnedEquipmentGenericResourceController ownedEquipmentGenericResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedEquipmentGenericResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedResultGenericResourceController.class)
    public OwnedResultGenericResourceController ownedResultGenericResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedResultGenericResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedCurveQueryController.class)
    public OwnedCurveQueryController ownedCurveQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedCurveQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedEquipmentCurveConfigQueryController.class)
    public OwnedEquipmentCurveConfigQueryController ownedEquipmentCurveConfigQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedEquipmentCurveConfigQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSampleBottleQueryController.class)
    public OwnedSampleBottleQueryController ownedSampleBottleQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedSampleBottleQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedSampleSetQueryController.class)
    public OwnedSampleSetQueryController ownedSampleSetQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedSampleSetQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedVocQueryController.class)
    public OwnedVocQueryController ownedVocQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedVocQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedPhysChemicQueryController.class)
    public OwnedPhysChemicQueryController ownedPhysChemicQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedPhysChemicQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOilFilmQueryController.class)
    public OwnedOilFilmQueryController ownedOilFilmQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOilFilmQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedTempHumidityQueryController.class)
    public OwnedTempHumidityQueryController ownedTempHumidityQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedTempHumidityQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedRunRemindQueryController.class)
    public OwnedRunRemindQueryController ownedRunRemindQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedRunRemindQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedPretreatmentQueryController.class)
    public OwnedPretreatmentQueryController ownedPretreatmentQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedPretreatmentQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedProjectQueryController.class)
    public OwnedProjectQueryController ownedProjectQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedProjectQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedFolderGenericResourceController.class)
    public OwnedFolderGenericResourceController ownedFolderGenericResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedFolderGenericResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedProjectTaskQueryController.class)
    public OwnedProjectTaskQueryController ownedProjectTaskQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedProjectTaskQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedProjectQcQueryController.class)
    public OwnedProjectQcQueryController ownedProjectQcQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedProjectQcQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedProjectEditLogQueryController.class)
    public OwnedProjectEditLogQueryController ownedProjectEditLogQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedProjectEditLogQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedProjectOtherFeeQueryController.class)
    public OwnedProjectOtherFeeQueryController ownedProjectOtherFeeQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedProjectOtherFeeQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedProjectSchemeQueryController.class)
    public OwnedProjectSchemeQueryController ownedProjectSchemeQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedProjectSchemeQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedProjectChangeQueryController.class)
    public OwnedProjectChangeQueryController ownedProjectChangeQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedProjectChangeQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderTaskResultQueryController.class)
    public OwnedOrderTaskResultQueryController ownedOrderTaskResultQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOrderTaskResultQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderTaskQueryController.class)
    public OwnedOrderTaskQueryController ownedOrderTaskQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOrderTaskQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderContainerQueryController.class)
    public OwnedOrderContainerQueryController ownedOrderContainerQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOrderContainerQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedGisMapQueryController.class)
    public OwnedGisMapQueryController ownedGisMapQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedGisMapQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedCrmReadController.class)
    public OwnedCrmReadController ownedCrmReadController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedCrmReadController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedResultFieldDescriptorController.class)
    public OwnedResultFieldDescriptorController ownedResultFieldDescriptorController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedResultFieldDescriptorController(boundary);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderQueryController.class)
    public OwnedOrderQueryController ownedOrderQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOrderQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedDeliveryOrderLabelQueryController.class)
    public OwnedDeliveryOrderLabelQueryController ownedDeliveryOrderLabelQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedDeliveryOrderLabelQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMobileProjectController.class)
    public OwnedMobileProjectController ownedMobileProjectController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMobileProjectController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedQualityControlChartController.class)
    public OwnedQualityControlChartController ownedQualityControlChartController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedQualityControlChartController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedAdmQuotationGenericResourceController.class)
    public OwnedAdmQuotationGenericResourceController ownedAdmQuotationGenericResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedAdmQuotationGenericResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedTestMethodAliasReadController.class)
    public OwnedTestMethodAliasReadController ownedTestMethodAliasReadController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedTestMethodAliasReadController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMdmSpecializedReadController.class)
    public OwnedMdmSpecializedReadController ownedMdmSpecializedReadController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMdmSpecializedReadController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMdmGenericReadController.class)
    public OwnedMdmGenericReadController ownedMdmGenericReadController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMdmGenericReadController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedClientResourceExtensionController.class)
    public OwnedClientResourceExtensionController ownedClientResourceExtensionController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedClientResourceExtensionController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOrderRetainQueryExtensionController.class)
    public OwnedOrderRetainQueryExtensionController ownedOrderRetainQueryExtensionController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOrderRetainQueryExtensionController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedItemEquipmentResourceController.class)
    public OwnedItemEquipmentResourceController ownedItemEquipmentResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedItemEquipmentResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedEquipmentMdmResourceController.class)
    public OwnedEquipmentMdmResourceController ownedEquipmentMdmResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedEquipmentMdmResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedOaGenericResourceController.class)
    public OwnedOaGenericResourceController ownedOaGenericResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedOaGenericResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedCrmOpportunityOrdtaskController.class)
    public OwnedCrmOpportunityOrdtaskController ownedCrmOpportunityOrdtaskController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedCrmOpportunityOrdtaskController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedReportSampleTaskQueryController.class)
    public OwnedReportSampleTaskQueryController ownedReportSampleTaskQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedReportSampleTaskQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedReportReferenceController.class)
    public OwnedReportReferenceController ownedReportReferenceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedReportReferenceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedReportSendInfoController.class)
    public OwnedReportSendInfoController ownedReportSendInfoController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedReportSendInfoController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedReportResultController.class)
    public OwnedReportResultController ownedReportResultController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedReportResultController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedItemTaskQueryController.class)
    public OwnedItemTaskQueryController ownedItemTaskQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedItemTaskQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedMdmTaskQueryController.class)
    public OwnedMdmTaskQueryController ownedMdmTaskQueryController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedMdmTaskQueryController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedInfoIndexController.class)
    public OwnedInfoIndexController ownedInfoIndexController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedInfoIndexController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedInfoConfigDataController.class)
    public OwnedInfoConfigDataController ownedInfoConfigDataController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedInfoConfigDataController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedInfoTmplController.class)
    public OwnedInfoTmplController ownedInfoTmplController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedInfoTmplController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.source-seams.enabled", havingValue = "true")
    @ConditionalOnMissingBean(OwnedReportTmplController.class)
    public OwnedReportTmplController ownedReportTmplController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedReportTmplController(boundary, dataSource);
    }
}
