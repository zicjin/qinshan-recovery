/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisDraftService;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigLineBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftLineBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageDraftDao;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigLineService;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigService;
import com.sunwayworld.module.admin.page.service.CoreAdminPageDraftLineService;
import com.sunwayworld.module.admin.page.service.CoreAdminPageDraftService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAdminPageDraftServiceImpl
implements CoreAdminPageDraftService {
    @Autowired
    private CoreAdminPageDraftDao dao;
    @Lazy
    @Autowired
    private CoreAdminPageDraftLineService draftLineService;
    @Lazy
    @Autowired
    private CoreAdminPageConfigService configService;
    @Lazy
    @Autowired
    private CoreAdminPageConfigLineService configLineService;
    @Lazy
    private CoreAdminMybatisDraftService mybatisDraftService;

    @Override
    public CoreAdminPageDraftDao getDao() {
        return this.dao;
    }

    @Override
    public CoreAdminPageDraftBean selectDraftIfPresent(@Nullable String menuId, String uri) {
        CoreAdminPageDraftBean filter = BeanUtils.getPropertyListenerProxy(CoreAdminPageDraftBean.class);
        filter.setMenuId(menuId);
        filter.setRequestUri(uri);
        return this.getDao().selectFirstIfPresent(filter, new Order[0]);
    }

    @Override
    public Long insertOrUpdateDraft(RestJsonWrapperBean wrapper) {
        String menuId = wrapper.getParamValue("menuId");
        String requestUri = wrapper.getParamValue("requestUri");
        String jsUri = wrapper.getParamValue("jsUri");
        String content = wrapper.getParamValue("content");
        CoreAdminPageDraftBean filter = BeanUtils.getPropertyListenerProxy(CoreAdminPageDraftBean.class);
        filter.setMenuId(menuId);
        filter.setRequestUri(requestUri);
        CoreAdminPageDraftBean draft = this.getDao().selectOneIfPresent(filter, new String[0]);
        if (draft == null) {
            CoreAdminPageDraftBean insertDraft = new CoreAdminPageDraftBean();
            insertDraft.setId(ApplicationContextHelper.getNextIdentity());
            insertDraft.setMenuId(menuId);
            insertDraft.setRequestUri(requestUri);
            this.getDao().insert(insertDraft);
            CoreAdminPageDraftLineBean line = new CoreAdminPageDraftLineBean();
            line.setId(ApplicationContextHelper.getNextIdentity());
            line.setDraftId(insertDraft.getId());
            line.setJsUri(jsUri);
            line.setContent(content);
            this.draftLineService.getDao().insert((CoreAdminPageDraftLineBean)line);
            return insertDraft.getId();
        }
        CoreAdminPageDraftLineBean lineFilter = new CoreAdminPageDraftLineBean();
        lineFilter.setDraftId(draft.getId());
        lineFilter.setJsUri(jsUri);
        CoreAdminPageDraftLineBean draftLine = this.draftLineService.getDao().selectOneIfPresent((CoreAdminPageDraftLineBean)lineFilter, new String[0]);
        if (draftLine == null) {
            CoreAdminPageDraftLineBean line = new CoreAdminPageDraftLineBean();
            line.setId(ApplicationContextHelper.getNextIdentity());
            line.setDraftId(draft.getId());
            line.setJsUri(jsUri);
            line.setContent(content);
            this.draftLineService.getDao().insert((CoreAdminPageDraftLineBean)line);
        } else {
            CoreAdminPageDraftLineBean proxy = BeanUtils.getPropertyListenerProxy(draftLine);
            proxy.setContent(content);
            this.draftLineService.update(proxy);
        }
        return draft.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value={"T_CORE_ADMIN_PAGE_CONFIG_LINE.ACTIVATED"}, keyGenerator="pageConfigLineKeyGenerator")
    public void release(Long id) {
        CoreAdminPageDraftBean draft = (CoreAdminPageDraftBean)this.getDao().selectById(id);
        CoreAdminPageDraftLineBean draftLineFilter = BeanUtils.getPropertyListenerProxy(CoreAdminPageDraftLineBean.class);
        draftLineFilter.setDraftId(id);
        List<CoreAdminPageDraftLineBean> draftLineList = this.draftLineService.selectList(draftLineFilter, new Order[0]);
        CoreAdminPageConfigBean configFilter = BeanUtils.getPropertyListenerProxy(CoreAdminPageConfigBean.class);
        configFilter.setMenuId(draft.getMenuId());
        configFilter.setRequestUri(draft.getRequestUri());
        int qty = this.configService.getDao().countBy((CoreAdminPageConfigBean)configFilter, new String[0]);
        if (qty > 0) {
            CoreAdminPageConfigBean activatedConfigFilter = BeanUtils.getPropertyListenerProxy(CoreAdminPageConfigBean.class);
            activatedConfigFilter.setMenuId(draft.getMenuId());
            activatedConfigFilter.setRequestUri(draft.getRequestUri());
            activatedConfigFilter.setActivatedFlag("1");
            CoreAdminPageConfigBean activatedConfig = this.configService.getDao().selectFirstIfPresent((CoreAdminPageConfigBean)activatedConfigFilter, new Order[0]);
            if (activatedConfig != null) {
                CoreAdminPageConfigBean proxy = BeanUtils.getPropertyListenerProxy(activatedConfig);
                PersistableHelper.assignActivatedUserData(proxy);
                proxy.setActivatedFlag("0");
                this.configService.update(proxy);
            }
        }
        CoreAdminPageConfigBean config = new CoreAdminPageConfigBean();
        config.setId(ApplicationContextHelper.getNextIdentity());
        config.setMenuId(draft.getMenuId());
        config.setRequestUri(draft.getRequestUri());
        config.setVersion((long)qty + 1L);
        config.setActivatedFlag("1");
        PersistableHelper.assignActivatedUserData(config);
        this.configService.getDao().insert((CoreAdminPageConfigBean)config);
        ArrayList<CoreAdminPageConfigLineBean> configLineList = new ArrayList<CoreAdminPageConfigLineBean>();
        for (CoreAdminPageDraftLineBean draftLine : draftLineList) {
            CoreAdminPageConfigLineBean configLine = new CoreAdminPageConfigLineBean();
            configLine.setId(ApplicationContextHelper.getNextIdentity());
            configLine.setConfigId(config.getId());
            configLine.setJsUri(draftLine.getJsUri());
            configLine.setContent(draftLine.getContent());
            configLineList.add(configLine);
        }
        this.configLineService.getDao().insert(configLineList);
        this.mybatisDraftService.release(draft.getMenuId());
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ADMIN_PAGE_CONFIG_LINE.ACTIVATED", this.getRedisKey(draft));
            }
        });
    }

    @Bean
    public KeyGenerator pageConfigLineKeyGenerator() {
        return (t, m, p) -> {
            Long id = (Long)p[0];
            CoreAdminPageDraftBean draft = (CoreAdminPageDraftBean)this.getDao().selectById(id);
            return this.getRedisKey(draft);
        };
    }

    private String getRedisKey(CoreAdminPageDraftBean draft) {
        return draft.getMenuId() + "-" + draft.getRequestUri();
    }
}
