/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogActivityBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnVerticalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogHorizontalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogInsertDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemVerticalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogTraceOverviewDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogVerticalRepresentation;
import java.util.List;
import org.springframework.lang.Nullable;

public interface CoreAuditLogService
extends GenericService<CoreAuditLogBean, Long> {
    public void insertAuditLogActivity(List<CoreAuditLogInsertDTO> var1);

    public Long insertAuditLogCascadeInNewTransaction(CoreAuditConfigBean var1, String var2);

    public CoreAuditLogDTO selectAuditLogRoot(String var1, String var2);

    public List<CoreAuditLogItemDTO> selectAuditLogItemList(@Nullable String var1, String var2, @Nullable Long var3, @Nullable Long var4);

    public CoreAuditLogItemVerticalDTO selectAuditLogItemActivityVertical(Long var1);

    public List<CoreAuditLogColumnVerticalDTO> selectAuditLogItemActivityColumnVerticalList(Long var1);

    public CoreAuditLogItemHorizontalDTO selectAuditLogItemActivityHorizontal(Long var1);

    public CoreAuditLogVerticalRepresentation selectAuditLogVertical(String var1, String var2);

    public CoreAuditLogHorizontalRepresentation selectAuditLogHorizontal(String var1, String var2);

    public Page<CoreAuditLogTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean var1);

    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(String var1, String var2);

    public Page<CoreAuditLogActivityBean> selectActivityQueryMorePagination(RestJsonWrapperBean var1);
}
