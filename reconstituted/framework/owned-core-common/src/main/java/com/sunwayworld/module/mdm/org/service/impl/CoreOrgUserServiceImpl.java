/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgUserBean;
import com.sunwayworld.module.mdm.org.dao.CoreOrgUserDao;
import com.sunwayworld.module.mdm.org.service.CoreOrgUserService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreOrgUserServiceImpl
implements CoreOrgUserService {
    @Autowired
    private CoreOrgUserDao coreOrgUserDao;

    @Override
    public CoreOrgUserDao getDao() {
        return this.coreOrgUserDao;
    }

    @Override
    public Page<CoreOrgUserBean> selectPaginationByOrg(String orgId, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper == null ? new MapperParameter() : wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("ORGID", orgId).filter(MatchPattern.EQ));
        PageRowBounds rowBounds = wrapper == null ? null : wrapper.extractPageRowBounds();
        return this.selectPagination(parameter, rowBounds);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertByUserId(String userId, List<CoreOrgUserBean> orgUserList) {
        orgUserList.forEach(u -> {
            u.setId(ApplicationContextHelper.getNextIdentity());
            u.setUserId(userId);
        });
        this.getDao().insert(orgUserList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteByUserId(String userId, String ... orgIds) {
        CoreOrgUserBean filter = new CoreOrgUserBean();
        filter.setUserId(userId);
        if (!ArrayUtils.hasElement(orgIds)) {
            this.getDao().deleteBy(filter, "USERID");
        } else {
            List<CoreOrgUserBean> orgUserList = this.getDao().selectList(filter, new Order[0]);
            this.getDao().deleteByIdList(orgUserList.stream().filter(r -> Arrays.stream(orgIds).anyMatch(i -> i.equals(r.getOrgId()))).map(CoreOrgUserBean::getId).collect(Collectors.toList()));
        }
    }
}
