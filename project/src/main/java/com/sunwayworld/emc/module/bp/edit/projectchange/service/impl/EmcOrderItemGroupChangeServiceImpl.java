package com.sunwayworld.emc.module.bp.edit.projectchange.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcOrderItemGroupChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderItemGroupChangeService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;

@Repository
@GikamBean
public class EmcOrderItemGroupChangeServiceImpl implements EmcOrderItemGroupChangeService {

    @Autowired
    private EmcOrderItemGroupChangeDao orderItemGroupDao;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    @Lazy
    private EmcOrderChangeService orderService;

    @Autowired
    private CoreTableService tableService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderItemGroupChangeDao getDao() {
        return orderItemGroupDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOrderItemGroupChangeBean emcOrderItemGroup = jsonWrapper.parseUnique(EmcOrderItemGroupChangeBean.class);
        emcOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcOrderItemGroup);
        return emcOrderItemGroup.getId();
    }

    @Override
    public void insert(List<EmcOrderChangeBean> orderList, List<EmcOrderItemGroupChangeBean> orderItemGroupList) {
        this.insert(orderList, orderItemGroupList, true);
    }

    /**
     * 添加itemFlag参数，控制是否插入orderItem表
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(List<EmcOrderChangeBean> orderList, List<EmcOrderItemGroupChangeBean> orderItemGroupList, boolean itemFlag) {
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(orderItemGroupList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList()),
                "ITEMGROUPID");

        List<EmcOrderItemBean> insertOrderItemList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> insertOrderItemGroupList = new ArrayList<>();

        orderList.forEach(order -> {
            orderItemGroupList.forEach(o -> {
                // 样品分析项组
                EmcOrderItemGroupChangeBean orderItemGroup = new EmcOrderItemGroupChangeBean();
                BeanUtils.copyProperties(o, orderItemGroup);
                orderItemGroup.setOrderId(order.getId());

                Long orderItemGroupId = ApplicationContextHelper.getNextIdentity();
                orderItemGroup.setId(orderItemGroupId);
                insertOrderItemGroupList.add(orderItemGroup);

                if (itemFlag) {
                    // 样品分析项
                    List<Long> itemIdList = groupItemList.stream().filter(e -> ObjectUtils.equals(e.getItemGroupId(), o.getItemGroupId())).map(e -> e.getItemId()).collect(Collectors.toList());

                    itemIdList.forEach(itemId -> {

                        EmcOrderItemBean orderItem = new EmcOrderItemBean();

                        orderItem.setId(ApplicationContextHelper.getNextIdentity());
                        orderItem.setItemId(itemId);
                        orderItem.setOrderId(order.getId());
                        orderItem.setOrderItemGroupId(orderItemGroupId);

                        insertOrderItemList.add(orderItem);

                    });
                }

            });
        });
        if (itemFlag) {
            orderItemService.getDao().insert(insertOrderItemList);
        }
        getDao().insert(insertOrderItemGroupList);

    }

    /**
     * 添加itemFlag参数，控制是否插入orderItem表--------检测项目修改添加测试，不插入EmcOrderItemGroupChangeBean记录
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByCheckProjectItemGroup(List<EmcOrderChangeBean> orderList, List<EmcOrderItemGroupChangeBean> orderItemGroupList, boolean itemFlag) {

        List<Long> testMethodIdList = orderItemGroupList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());

        List<EmcTestMethodOrderCategoryBean> filterTestMethodOrderCategoryList = testMethodIdList.stream().map(testMethodId -> {
            EmcTestMethodOrderCategoryBean testMethodOrderCategory = new EmcTestMethodOrderCategoryBean();

            testMethodOrderCategory.setTestMethodId(testMethodId);
            testMethodOrderCategory.setMonitorTmplId(orderList.get(0).getMonitorTmplId());

            return testMethodOrderCategory;
        }).collect(Collectors.toList());

        List<Long> testMethodOrderCategoryIdList = testMethodOrderCategoryService.getDao()
                .selectList(filterTestMethodOrderCategoryList, Arrays.asList("TESTMETHODID", "MONITORTMPLID"), Arrays.asList("ID")).stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryList = testMethodOrderCategoryIdList.isEmpty() ? Collections.emptyList()
                : testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("ID", testMethodOrderCategoryIdList).filter(MatchPattern.OR));

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(orderItemGroupList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList()),
                "ITEMGROUPID");

        List<EmcOrderItemBean> insertOrderItemList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> insertOrderItemGroupList = new ArrayList<>();

        orderList.forEach(order -> {

            orderItemGroupList.stream().forEach(o -> {

                // 添加样品测试容器信息
                List<EmcTestMethodOrderCategoryBean> orderCategoryList = testMethodOrderCategoryList.stream().filter(c -> ObjectUtils.equals(o.getTestMethodId(), c.getTestMethodId()))
                        .collect(Collectors.toList());

                if (!orderCategoryList.isEmpty()) {

                    EmcTestMethodOrderCategoryBean orderCategory = orderCategoryList.get(0);

                    o.setContainerCategoryId(orderCategory.getContainerCategoryId());
                    o.setContainerCategoryCode(orderCategory.getExt$Item("containercategorycode"));
                    o.setContainerCategoryName(orderCategory.getExt$Item("containercategoryname"));
                    o.setContainerGroup(orderCategory.getContainerGroup());
                    o.setFixative(orderCategory.getFixative());

                }

                // 样品分析项组
                EmcOrderItemGroupChangeBean orderItemGroup = new EmcOrderItemGroupChangeBean();
                BeanUtils.copyProperties(o, orderItemGroup);
                orderItemGroup.setOrderId(order.getId());

                Long orderItemGroupId = ApplicationContextHelper.getNextIdentity();
                orderItemGroup.setId(orderItemGroupId);
                insertOrderItemGroupList.add(orderItemGroup);

                if (itemFlag) {
                    // 样品分析项
                    List<Long> itemIdList = groupItemList.stream().filter(e -> ObjectUtils.equals(e.getItemGroupId(), o.getItemGroupId())).map(e -> e.getItemId()).collect(Collectors.toList());

                    itemIdList.forEach(itemId -> {

                        EmcOrderItemBean orderItem = new EmcOrderItemBean();

                        orderItem.setId(ApplicationContextHelper.getNextIdentity());
                        orderItem.setItemId(itemId);
                        orderItem.setOrderId(order.getId());
                        orderItem.setOrderItemGroupId(orderItemGroupId);

                        insertOrderItemList.add(orderItem);

                    });
                }

            });
        });
        if (itemFlag) {
            orderItemService.getDao().insert(insertOrderItemList);
        }
        //getDao().insert(insertOrderItemGroupList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(List<EmcOrderItemGroupChangeBean> orderItemGroupList) {

        List<Long> deleteOrderItemGroupIdList = orderItemGroupList.stream().map(e -> e.getId()).collect(Collectors.toList());

        deleteOrderItemGroupIdList.forEach(i -> tableService.deleteCascade(getDao().getTable(), i));
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        List<EmcOrderItemGroupChangeBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupChangeBean.class);
        List<Long> orderIdList = orderService.getDao().selectListByOneColumnValue(orderService.selectById(orderItemGroupList.get(0).getOrderId()).getProjId(), "PROJID", Arrays.asList("ID")).stream()
                .map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderItemGroupChangeBean> subcontractorOrderItemGroupList = getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Arrays.asList("ID", "SUBCONTRACTORID", "SUBPACKAGENO")).stream()
                .filter(e -> !ObjectUtils.isEmpty(e.getSubcontractorId())).collect(Collectors.toList());

        EmcOrderItemGroupChangeBean updateOrderItemGroup = new EmcOrderItemGroupChangeBean();
        updateOrderItemGroup.setSubcontractorId(orderItemGroupList.get(0).getSubcontractorId());
        // 更新分包单号
        if (!subcontractorOrderItemGroupList.isEmpty()) {
            updateOrderItemGroup.setSubPackageNo(subcontractorOrderItemGroupList.get(0).getSubPackageNo());
        } else {
            updateOrderItemGroup.setSubPackageNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        }

        getDao().updateByIds(updateOrderItemGroup, orderItemGroupList.stream().map(e -> e.getId()).collect(Collectors.toList()), "subPackageNo", "subcontractorId");
    }

}
