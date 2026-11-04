/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.service.impl;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageRepresentative;
import com.sunwayworld.cloud.module.mobile.menu.persistent.dao.MobilePageDao;
import com.sunwayworld.cloud.module.mobile.menu.service.MobilePageService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class MobilePageServiceImpl
implements MobilePageService {
    @Autowired
    private MobilePageDao mobilePageDao;

    @Override
    public MobilePageDao getDao() {
        return this.mobilePageDao;
    }

    @Override
    @Transactional
    public void insertSameLevelPage(Long menuId, RestJsonWrapperBean wrapper) {
        List<MobilePageBean> sourcepageList = wrapper.parse(MobilePageBean.class);
        if (sourcepageList.isEmpty()) {
            MobilePageBean targetPage = new MobilePageBean();
            targetPage.setMenuId(menuId);
            targetPage.setId(ApplicationContextHelper.getNextIdentity());
            targetPage.setPageNo("1");
            targetPage.setOrderNo(1L);
            targetPage.setLvl(1);
            this.getDao().insert(targetPage);
            return;
        }
        MobilePageBean sourcePage = wrapper.parseUnique(MobilePageBean.class);
        MobilePageBean page = (MobilePageBean)this.getDao().selectByIdIfPresent(sourcePage.getId());
        MobilePageBean searchPage = new MobilePageBean();
        searchPage.setMenuId(menuId);
        searchPage.setParentId(page.getParentId());
        List<MobilePageBean> pageList = this.getDao().selectList(searchPage, new Order[0]);
        if (1 == page.getLvl()) {
            pageList = pageList.stream().filter(t -> 1 == t.getLvl()).collect(Collectors.toList());
        }
        Long orderNo = pageList.stream().mapToLong(MobilePageBean::getOrderNo).max().getAsLong();
        MobilePageBean targetPage = new MobilePageBean();
        targetPage.setId(ApplicationContextHelper.getNextIdentity());
        targetPage.setMenuId(page.getMenuId());
        targetPage.setParentId(page.getParentId());
        targetPage.setLvl(page.getLvl());
        targetPage.setOrderNo(orderNo + 1L);
        if (1 == page.getLvl()) {
            targetPage.setPageNo(targetPage.getOrderNo() + "");
        } else {
            MobilePageBean parentPage = (MobilePageBean)this.getDao().selectByIdIfPresent(page.getParentId());
            targetPage.setPageNo(parentPage.getPageNo() + "." + targetPage.getOrderNo());
        }
        this.getDao().insert(targetPage);
    }

    @Override
    @Transactional
    public void insertLowerLevelPage(Long menuId, RestJsonWrapperBean wrapper) {
        MobilePageBean sourcePage = wrapper.parseUnique(MobilePageBean.class);
        MobilePageBean parentPage = (MobilePageBean)this.getDao().selectByIdIfPresent(sourcePage.getId());
        MobilePageBean searchPage = new MobilePageBean();
        searchPage.setParentId(parentPage.getId());
        List<MobilePageBean> lowerpageList = this.getDao().selectList(searchPage, new Order[0]);
        Long orderNo = 0L;
        if (!lowerpageList.isEmpty()) {
            orderNo = lowerpageList.stream().mapToLong(MobilePageBean::getOrderNo).max().getAsLong();
        }
        MobilePageBean targetPage = new MobilePageBean();
        targetPage.setId(ApplicationContextHelper.getNextIdentity());
        targetPage.setMenuId(parentPage.getMenuId());
        targetPage.setParentId(parentPage.getId());
        targetPage.setLvl(parentPage.getLvl() + 1);
        targetPage.setOrderNo(orderNo + 1L);
        targetPage.setPageNo(parentPage.getPageNo() + "." + targetPage.getOrderNo());
        this.getDao().insert(targetPage);
    }

    @Override
    @Transactional
    public void deletePage(Long menuId, RestJsonWrapperBean wrapper) {
        this.delete(wrapper);
        MobilePageBean searchPage = new MobilePageBean();
        searchPage.setMenuId(menuId);
        List<MobilePageBean> pageList = this.getDao().selectList(searchPage, Order.asc("LVL"), Order.asc("ORDERNO"));
        if (pageList.isEmpty()) {
            return;
        }
        List firstLevelpageList = pageList.stream().filter(t -> t.getLvl() == 1).collect(Collectors.toList());
        for (int i = 0; i < firstLevelpageList.size(); ++i) {
            MobilePageBean firstLevel = (MobilePageBean)firstLevelpageList.get(i);
            firstLevel.setOrderNo((long)i + 1L);
            firstLevel.setPageNo(firstLevel.getOrderNo() + "");
        }
        HashMap<Long, MobilePageBean> parentPageMap = new HashMap<Long, MobilePageBean>();
        HashMap<Long, Long> subPageMaxOrderNoMap = new HashMap<Long, Long>();
        for (int i = 0; i < pageList.size(); ++i) {
            MobilePageBean page = pageList.get(i);
            parentPageMap.put(page.getId(), page);
            subPageMaxOrderNoMap.put(page.getId(), 0L);
            if (1 == page.getLvl() || !parentPageMap.containsKey(page.getParentId())) continue;
            page.setOrderNo((Long)subPageMaxOrderNoMap.get(page.getParentId()) + 1L);
            page.setPageNo(((MobilePageBean)parentPageMap.get(page.getParentId())).getPageNo() + "." + page.getOrderNo());
            subPageMaxOrderNoMap.put(page.getParentId(), page.getOrderNo());
        }
        this.getDao().update(pageList, new String[]{"ORDERNO", "PAGENO"});
    }

    @Override
    @Transactional
    public Page<MobilePageRepresentative> selectMobilePageTreeList(String filterKey, Long filterValue, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match(filterKey, filterValue).filter(MatchPattern.EQ));
        List<Map<String, Object>> mapList = this.getDao().selectMobilePageTreeList(parameter);
        if (mapList.isEmpty()) {
            return new Pagination<MobilePageRepresentative>(Collections.emptyList());
        }
        List mobilePageRepresentative = mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, MobilePageRepresentative.class)).collect(Collectors.toList());
        TreeDescriptor<MobilePageRepresentative> descriptor = new TreeDescriptor<MobilePageRepresentative>("id", "parentId", "pageName");
        descriptor.setOrderComparator((v1, v2) -> v1.compareTo((MobilePageRepresentative)v2));
        descriptor.addConsumer((t, n) -> n.setExpanded(true));
        List<MobilePageRepresentative> treeNodeList = TreeHelper.parseTreeNode(mobilePageRepresentative, descriptor, MobilePageRepresentative.class);
        return new Pagination<MobilePageRepresentative>(treeNodeList);
    }

    @Override
    @Transactional
    public void switchPageColumn(Long firstId, Long secondId) {
        MobilePageBean first = (MobilePageBean)this.selectByIdIfPresent(firstId);
        MobilePageBean second = (MobilePageBean)this.selectByIdIfPresent(secondId);
        MobilePageBean pageSearch = new MobilePageBean();
        pageSearch.setMenuId(first.getMenuId());
        List<MobilePageBean> pageList = this.getDao().selectList(pageSearch, new Order[0]);
        List<MobilePageBean> firstSubpageList = pageList.stream().filter(t -> t.getPageNo().startsWith(first.getPageNo()) && t.getLvl() > first.getLvl()).collect(Collectors.toList());
        List<MobilePageBean> secondSubpageList = pageList.stream().filter(t -> t.getPageNo().startsWith(second.getPageNo()) && t.getLvl() > second.getLvl()).collect(Collectors.toList());
        String firstPageNo = first.getPageNo();
        Long firstOrderNo = first.getOrderNo();
        String secondPageNo = second.getPageNo();
        Long secondOrderNo = second.getOrderNo();
        firstSubpageList.forEach(f -> f.setPageNo(secondPageNo + f.getPageNo().substring(firstPageNo.length())));
        secondSubpageList.forEach(s -> s.setPageNo(firstPageNo + s.getPageNo().substring(secondPageNo.length())));
        first.setOrderNo(secondOrderNo);
        first.setPageNo(secondPageNo);
        second.setOrderNo(firstOrderNo);
        second.setPageNo(firstPageNo);
        ArrayList<MobilePageBean> updateList = new ArrayList<MobilePageBean>();
        updateList.add(first);
        updateList.addAll(firstSubpageList);
        updateList.add(second);
        updateList.addAll(secondSubpageList);
        this.getDao().update(updateList, new String[]{"ORDERNO", "PAGENO"});
    }
}
