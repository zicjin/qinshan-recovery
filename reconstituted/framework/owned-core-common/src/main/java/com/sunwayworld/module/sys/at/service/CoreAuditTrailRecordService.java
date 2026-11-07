/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.service;

import com.sunwayworld.module.sys.at.resource.CoreAuditTrailRecordResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface CoreAuditTrailRecordService
extends GenericService<CoreAuditTrailRecordBean, Long> {
    public Long insert(CoreAuditTrailRecordBean var1);

    public List<CoreAuditTrailRecordDTO> selectRecordList(String var1, String var2);

    public List<CoreAuditTrailRecordBean> selectAtRecordList(String var1, String var2);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(@PathVariable Long var1);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineList(@PathVariable Long var1, @PathVariable String var2);

    public Page<CoreAuditTrailRecordLineBean> selectRecordLinePaginationList(RestJsonWrapperBean var1);

    public CoreAuditTrailRecordLineBean selectRecordLineDetail(@PathVariable Long var1);

    public List<CoreAuditTrailRecordBean> selectAtRecordList(String var1, RestJsonWrapperBean var2);

    public Page<CoreAuditTrailRecordBean> selectAtRecordPaginationList(String var1, RestJsonWrapperBean var2);

    public Page<CoreAuditTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean var1);

    public List<CoreAuditTrailRecordLineDTO> selectRecordLineDTOByTableNameAndTargetId(String var1, String var2);
    /** Scope only public audit reads; business audit writers and non-HTTP callers remain unchanged. */
    public final class ReadScope {
        private ReadScope() { }
        public interface Provider { String requireOrganization(); }
    
        public static String organization(Provider provider) {
            if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes)) return null;
            Object handler=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest()
                    .getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
            if (!(handler instanceof HandlerMethod)) return null;
            HandlerMethod method=(HandlerMethod)handler;
            if (!CoreAuditTrailRecordResource.class.isAssignableFrom(method.getBeanType())
                    || !method.getMethod().getName().startsWith("select")
                    || "selectStatus".equals(method.getMethod().getName())) return null;
            if (provider == null) throw new AccessDeniedException("audit read identity unavailable");
            String organization=provider.requireOrganization();
            if (organization == null || organization.trim().isEmpty()) throw new AccessDeniedException("audit organization required");
            return organization;
        }
    }
}
