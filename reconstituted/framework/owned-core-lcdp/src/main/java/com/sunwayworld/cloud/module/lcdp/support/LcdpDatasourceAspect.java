/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.support;

import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
@Component
@Order(value=50)
@Profile(value={"lcdp-tenant"})
public class LcdpDatasourceAspect {
    @Autowired
    private PlatformTransactionManager txManager;
    public static final String DEV_DATABASE_TENANT_NAME = "lcdp";

    @Pointcut(value="within(com.sunwayworld.framework.support.base.service.GenericService) || within(com.sunwayworld.framework.support.base.dao.GenericDao)")
    private void coreInterface() {
    }

    @Pointcut(value="(within(com.sunwayworld..lcdp..service..*) || within(com.sunwayworld..lcdp..dao..*)|| proBpmnDatasource())&& !within(com.sunwayworld..lcdp.base..*) && !execution(* com.sunwayworld.cloud.module.lcdp.resource.service.impl.LcdpResourceServiceImpl.callScript(..)) && !execution(* com.sunwayworld.cloud.module.lcdp.resource.service.impl.LcdpResourceServiceImpl.manualSave(..))&& !execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpTableServiceImpl.*PhysicalTable(..))&& !execution(* com.sunwayworld.cloud.module.lcdp.table.persistent.dao.impl.LcdpDatabaseDaoImpl.*(..))&& !execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpDatabaseServiceImpl.*(..))")
    private void proDataSource() {
    }

    @Pointcut(value="execution(* com.sunwayworld.module.sys.bpmn.service.impl.CoreBpmnDiagramServiceImpl.*(..)) || execution(* com.sunwayworld.module.sys.bpmn.service.impl.CoreBpmnDraftOrgServiceImpl.*(..))|| execution(* com.sunwayworld.module.sys.bpmn.service.impl.CoreBpmnDraftServiceImpl.*(..))|| execution(* com.sunwayworld.module.sys.bpmn.service.impl.CoreBpmnProcOrgServiceImpl.*(..))|| execution(* com.sunwayworld.module.sys.bpmn.service.impl.CoreBpmnProcServiceImpl.*(..))|| execution(* com.sunwayworld.module.sys.bpmn.service.impl.CoreBpmnDiagramServiceImpl.*(..))")
    private void proBpmnDatasource() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="within(com.sunwayworld..resource..*) && !(within(com.sunwayworld..lcdp.resource..*) && !within(com.sunwayworld..lcdp.resource.resource..*))")
    public Object changeResourceDataSourceByEnv(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null || !"development".equals(request.getHeader("lcdp-data-env"))) {
            return pjp.proceed();
        }
        String tenant = TenantContext.getTenant();
        TenantContext.setTenant(DEV_DATABASE_TENANT_NAME);
        try {
            Object object = pjp.proceed();
            return object;
        }
        finally {
            TenantContext.setTenant(tenant);
        }
    }

    @Around(value="coreInterface() || proDataSource()")
    public Object executeWithProDataSource(ProceedingJoinPoint pjp) throws Throwable {
        String tenant = TenantContext.getTenant();
        if (StringUtils.equals(DEV_DATABASE_TENANT_NAME, tenant)) {
            Object result;
            TenantContext.removeTenant();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(3);
            TransactionStatus transaction = this.txManager.getTransaction(def);
            try {
                result = pjp.proceed();
                this.txManager.commit(transaction);
            }
            catch (Exception e) {
                this.txManager.rollback(transaction);
                throw e;
            }
            finally {
                TenantContext.setTenant(tenant);
            }
            return result;
        }
        return pjp.proceed();
    }

    @Around(value="execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpTableServiceImpl.createPhysicalTable(..)) || execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpTableServiceImpl.dropPhysicalTable(..)) || execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpTableServiceImpl.alterPhysicalTable(..))|| execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpDatabaseServiceImpl.createPhysicalView(..))|| execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpDatabaseServiceImpl.alterPhysicalView(..))|| execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpDatabaseServiceImpl.dropPhysicalView(..))")
    public Object handleDDLExecution(ProceedingJoinPoint pjp) throws Throwable {
        return this.synchronizeExecute(pjp);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpDatabaseServiceImpl.isExistData(..)) || execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpDatabaseServiceImpl.isExistNullDataInColumn(..)) || execution(* com.sunwayworld.cloud.module.lcdp.table.service.impl.LcdpDatabaseServiceImpl.isExistNotNullDataInColumn(..))")
    public Object executeValidateFunction(ProceedingJoinPoint pjp) throws Throwable {
        Object resultDev;
        Object result;
        String tenant = TenantContext.getTenant();
        try {
            result = pjp.proceed();
            if (StringUtils.equals(tenant, DEV_DATABASE_TENANT_NAME)) {
                Object object = result;
                return object;
            }
            TenantContext.setTenant(DEV_DATABASE_TENANT_NAME);
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(3);
            TransactionStatus transaction = this.txManager.getTransaction(def);
            try {
                resultDev = pjp.proceed();
                this.txManager.commit(transaction);
            }
            catch (Exception e) {
                this.txManager.rollback(transaction);
                throw e;
            }
        }
        finally {
            TenantContext.setTenant(tenant);
        }
        return (Boolean)result != false || (Boolean)resultDev != false;
    }

    @Around(value="(target(com.sunwayworld.module.sys.sequence.service.impl.CoreSequenceServiceImpl)   && (execution(* com.sunwayworld.module.sys.sequence.service.impl.CoreSequenceAbstractService.deleteLine(..))        || execution(* com.sunwayworld.module.sys.sequence.service.impl.CoreSequenceAbstractService.swapLine(..))        || execution(* com.sunwayworld.module.sys.sequence.service.impl.CoreSequenceAbstractService.insert(..))        || execution(* com.sunwayworld.framework.support.base.service.GenericService.delete(..))        || execution(* com.sunwayworld.framework.support.base.service.GenericService.instantSave(..)))) || (target(com.sunwayworld.module.sys.sequence.dao.impl.CoreSequenceLineDaoImpl)   && execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.insert(..)))")
    public Object syncSequence(ProceedingJoinPoint pjp) throws Throwable {
        return this.synchronizeExecute(pjp);
    }

    @Around(value="within(com.sunwayworld.module.sys.i18n.dao.impl.*) && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.select*(..))  && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.get*(..))  && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.count*(..)) ")
    public Object syncI18n(ProceedingJoinPoint pjp) throws Throwable {
        return this.synchronizeExecute(pjp);
    }

    @Around(value="within(com.sunwayworld.module.admin.config.dao.impl.*) && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.select*(..))  && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.get*(..))  && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.count*(..)) ")
    public Object syncAdminConfig(ProceedingJoinPoint pjp) throws Throwable {
        return this.synchronizeExecute(pjp);
    }

    @Around(value="within(com.sunwayworld.module.sys.code.dao.impl.*) && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.select*(..))  && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.get*(..))  && !execution(* com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport.count*(..)) ")
    public Object syncCode(ProceedingJoinPoint pjp) throws Throwable {
        return this.synchronizeExecute(pjp);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object synchronizeExecute(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        String tenant = TenantContext.getTenant();
        try {
            result = pjp.proceed();
            if (StringUtils.equals(tenant, DEV_DATABASE_TENANT_NAME)) {
                Object object = result;
                return object;
            }
            TenantContext.setTenant(DEV_DATABASE_TENANT_NAME);
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(3);
            TransactionStatus transaction = this.txManager.getTransaction(def);
            try {
                pjp.proceed();
                this.txManager.commit(transaction);
            }
            catch (Exception e) {
                this.txManager.rollback(transaction);
                throw e;
            }
        }
        finally {
            TenantContext.setTenant(tenant);
        }
        return result;
    }
}
