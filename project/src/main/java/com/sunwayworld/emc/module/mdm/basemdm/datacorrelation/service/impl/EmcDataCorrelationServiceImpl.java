package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.dao.EmcDataCorrelationDao;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.EmcDataCorrelationItemService;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.EmcDataCorrelationService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */
@Repository
@GikamBean
public class EmcDataCorrelationServiceImpl implements EmcDataCorrelationService {
    @Autowired
    protected EmcDataCorrelationDao dataCorrelationDao;

    @Autowired
    protected EmcDataCorrelationItemService dataCorrelationItemService;

    @Autowired
    protected EmcItemService itemService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcDataCorrelationDao getDao() {
        return dataCorrelationDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcDataCorrelationBean dataCorrelation = jsonWrapper.parseUnique(EmcDataCorrelationBean.class);
        dataCorrelation.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(dataCorrelation);
        return dataCorrelation.getId();
    }

    @Override
    @Log(value = "查询数据相关性分析项", type = LogType.SELECT)
    public Page<EmcDataCorrelationItemBean> selectDataCorrelationItemByCorrelationId(Long correlationId, RestJsonWrapperBean wrapper) {
        return dataCorrelationItemService.selectPaginationByFilter(SearchFilter.instance().match("CORRELATIONID", correlationId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询数据相关性可选分析项", type = LogType.SELECT)
    public Page<EmcDataCorrelationItemBean> selectChooseableDataCorrelationItem(Long correlationId, RestJsonWrapperBean wrapper) {
        Page<EmcItemBean> itemPage = itemService.selectTestItem(correlationId, wrapper);
        // bean转换
        List<EmcDataCorrelationItemBean> dataCorrelationItemList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(itemPage.getRows())) {
            itemPage.getRows().forEach(item -> {
                EmcDataCorrelationItemBean dataCorrelationItem = new EmcDataCorrelationItemBean();
                dataCorrelationItem.setId(item.getId());
                dataCorrelationItem.setExt$Item("testcategoryid", item.getExt$Item("testcategoryid"));
                dataCorrelationItem.setExt$Item("testcategorydesc", item.getExt$Item("testcategorydesc"));
                dataCorrelationItem.setTestId(NumberUtils.parseLong(item.getExt$Item("testid")));
                dataCorrelationItem.setExt$Item("testname", item.getExt$Item("testname"));
                dataCorrelationItem.setMethodId(NumberUtils.parseLong(item.getExt$Item("methodid")));
                dataCorrelationItem.setExt$Item("methodname", item.getExt$Item("methodname"));
                dataCorrelationItem.setItemId(item.getId());
                dataCorrelationItem.setExt$Item("itemname", item.getItemName());
                dataCorrelationItem.setExt$Item("itemaliasname", item.getItemAliasName());
                dataCorrelationItemList.add(dataCorrelationItem);
            });
        }
        Page<EmcDataCorrelationItemBean> page = dataCorrelationItemService.selectPagination(new RestJsonWrapperBean());
        return new Pagination<>(page, dataCorrelationItemList);
    }


    @Override
    @Log(value = "更新数据相关性分析项", type = LogType.UPDATE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateDataCorrelationItem(Long correlationId, RestJsonWrapperBean wrapper) {
        // 先删除原先数据
        List<EmcDataCorrelationItemBean> dataCorrelationItemList = dataCorrelationItemService.selectListByFilter(SearchFilter.instance().match("CORRELATIONID", correlationId).filter(MatchPattern.EQ), wrapper);
        if(!CollectionUtils.isEmpty(dataCorrelationItemList))
            dataCorrelationItemService.getDao().deleteBy(dataCorrelationItemList);
        // 再插入新数据
        dataCorrelationItemService.insertMul(wrapper);
    }
}
