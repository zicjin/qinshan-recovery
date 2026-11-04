/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisDraftBean;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisStatementBean;
import com.sunwayworld.module.admin.mybatis.dao.CoreAdminMybatisDraftDao;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisDraftService;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisStatementService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAdminMybatisDraftServiceImpl
implements CoreAdminMybatisDraftService {
    @Autowired
    private CoreAdminMybatisDraftDao draftMapperDao;
    @Autowired
    private CoreAdminMybatisStatementService statementService;

    @Override
    public CoreAdminMybatisDraftDao getDao() {
        return this.draftMapperDao;
    }

    @Override
    public CoreAdminMybatisDraftBean selectDraftForModification(String menuId, String mybatisStatementId) {
        int lastDotIndex = mybatisStatementId.lastIndexOf(".");
        String mapperId = mybatisStatementId.substring(0, lastDotIndex);
        String statementId = mybatisStatementId.substring(lastDotIndex + 1);
        CoreAdminMybatisDraftBean draftFilter = new CoreAdminMybatisDraftBean();
        draftFilter.setMenuId(menuId);
        draftFilter.setMapperId(mapperId);
        draftFilter.setStatementId(statementId);
        CoreAdminMybatisDraftBean selectedDraft = this.getDao().selectOneIfPresent(draftFilter, new String[0]);
        if (selectedDraft != null) {
            return selectedDraft;
        }
        CoreAdminMybatisStatementBean statementFilter = new CoreAdminMybatisStatementBean();
        statementFilter.setMenuId(menuId);
        statementFilter.setMapperId(mapperId);
        statementFilter.setStatementId(statementId);
        CoreAdminMybatisStatementBean statement = (CoreAdminMybatisStatementBean)this.statementService.selectFirstByFilter(SearchFilter.instance().match("MENUID", menuId).filter(MatchPattern.EQ).match("MAPPERID", mapperId).filter(MatchPattern.EQ).match("STATEMENTID", statementId).filter(MatchPattern.EQ), Order.desc("VERSION"));
        CoreAdminMybatisDraftBean draft = new CoreAdminMybatisDraftBean();
        draft.setMenuId(menuId);
        draft.setMapperId(mapperId);
        draft.setStatementId(statementId);
        if (statement != null) {
            draft.setStatementXml(statement.getStatementXml());
        } else {
            draft.setStatementXml(MybatisHelper.getMapperContent(ClassUtils.getClass(mapperId)));
        }
        return draft;
    }

    @Override
    @Transactional
    public Long insertOrUpdateDraft(RestJsonWrapperBean wrapper) {
        CoreAdminMybatisDraftBean draft = wrapper.parseUnique(CoreAdminMybatisDraftBean.class);
        CoreAdminMybatisDraftBean draftFilter = new CoreAdminMybatisDraftBean();
        draftFilter.setMenuId(draft.getMenuId());
        draftFilter.setMapperId(draft.getMapperId());
        draftFilter.setStatementId(draft.getStatementId());
        CoreAdminMybatisDraftBean selectedDraft = this.getDao().selectOneIfPresent(draftFilter, new String[0]);
        if (selectedDraft == null) {
            draft.setId(ApplicationContextHelper.getNextIdentity());
            this.getDao().insert(draft);
        } else {
            this.getDao().update(draft, new String[]{"STATEMENTXML"});
        }
        return draft.getId();
    }

    @Override
    @Transactional
    public void release(String menuId) {
        CoreAdminMybatisDraftBean draftFilter = new CoreAdminMybatisDraftBean();
        draftFilter.setMenuId(menuId);
        List<CoreAdminMybatisDraftBean> draftList = this.getDao().selectList(draftFilter, new Order[0]);
        if (!draftList.isEmpty()) {
            List<CoreAdminMybatisStatementBean> statementList = this.statementService.selectActiveList(menuId, null);
            ArrayList<String> mapperIdList = new ArrayList<String>();
            ArrayList<CoreAdminMybatisStatementBean> insertStatementList = new ArrayList<CoreAdminMybatisStatementBean>();
            for (CoreAdminMybatisDraftBean draft : draftList) {
                CoreAdminMybatisStatementBean statement = statementList.stream().filter(s -> s.getMapperId().equals(draft.getMapperId()) && s.getStatementId().equals(draft.getStatementId())).findAny().orElse(null);
                CoreAdminMybatisStatementBean insertStatement = new CoreAdminMybatisStatementBean();
                BeanUtils.copyProperties((Object)draft, (Object)insertStatement, new String[0]);
                insertStatement.setId(ApplicationContextHelper.getNextIdentity());
                if (statement != null) {
                    insertStatement.setVersion(statement.getVersion() + 1L);
                }
                insertStatementList.add(insertStatement);
                if (mapperIdList.contains(draft.getMapperId())) continue;
                mapperIdList.add(draft.getMapperId());
            }
            this.getDao().deleteByIdList(draftList.stream().map(d -> d.getId()).collect(Collectors.toList()));
            this.statementService.getDao().insert(insertStatementList);
            mapperIdList.forEach(mapperId -> {
                MybatisHelper.removeDraftMapper(menuId, mapperId);
                MybatisHelper.updateRuntimeMapper(menuId, mapperId);
            });
        }
    }
}
