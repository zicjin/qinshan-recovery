package com.sunwayworld.emc.module.bp.project.project.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcOrderItemGroupDao;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;

@Repository
@GikamBean
public class EmcOrderItemGroupServiceImpl implements EmcOrderItemGroupService {
    @Autowired
    private EmcOrderItemGroupDao orderItemGroupDao;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    private CoreTableService tableService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderItemGroupDao getDao() {
        return orderItemGroupDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOrderItemGroupBean emcOrderItemGroup = jsonWrapper.parseUnique(EmcOrderItemGroupBean.class);
        emcOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcOrderItemGroup);
        return emcOrderItemGroup.getId();
    }

    @Override
    public void insert(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList) {
        this.insert(orderList, orderItemGroupList, true);
    }

    /**
     * 添加itemFlag参数，控制是否插入orderItem表
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList, boolean itemFlag) {

        List<Long> testMethodIdList = orderItemGroupList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());

        List<EmcTestMethodOrderCategoryBean> filterTestMethodOrderCategoryList = testMethodIdList.stream().map(testMethodId -> {
            EmcTestMethodOrderCategoryBean testMethodOrderCategory = new EmcTestMethodOrderCategoryBean();

            testMethodOrderCategory.setTestMethodId(testMethodId);
            testMethodOrderCategory.setMonitorTmplId(orderList.get(0).getMonitorTmplId());

            return testMethodOrderCategory;
        }).collect(Collectors.toList());

        List<Long> testMethodOrderCategoryIdList = testMethodOrderCategoryService.getDao()
                .selectList(filterTestMethodOrderCategoryList, Arrays.asList("TESTMETHODID", "MONITORTMPLID"), Arrays.asList("ID"), new Order[0]).stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryList = testMethodOrderCategoryIdList.isEmpty() ? Collections.emptyList()
                : testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("ID", testMethodOrderCategoryIdList).filter(MatchPattern.OR), new Order[0]);

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(orderItemGroupList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList()),
                "ITEMGROUPID", new Order[0]);

        List<EmcOrderItemBean> insertOrderItemList = new ArrayList<>();
        List<EmcOrderItemGroupBean> insertOrderItemGroupList = new ArrayList<>();

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
                EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
                BeanUtils.copyProperties(o, orderItemGroup, new String[0]);
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
     * 添加itemFlag参数，控制是否插入orderItem表--------检测项目修改添加测试，不插入EmcOrderItemGroupBean记录
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByCheckProjectItemGroup(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList, boolean itemFlag) {

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
        List<EmcOrderItemGroupBean> insertOrderItemGroupList = new ArrayList<>();

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
                EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
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
    public void delete(List<EmcOrderItemGroupBean> orderItemGroupList) {

        List<Long> deleteOrderItemGroupIdList = orderItemGroupList.stream().map(e -> e.getId()).collect(Collectors.toList());

        deleteOrderItemGroupIdList.forEach(i -> tableService.deleteCascade(getDao().getTable(), i));
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void editMethod(Long id, RestJsonWrapperBean wrapper) {

        EmcOrderItemGroupBean orderItemGroup = wrapper.parseUnique(EmcOrderItemGroupBean.class);

        EmcOrderItemGroupBean oldOrderItemGroup = selectById(id);

        List<EmcOrderItemGroupBean> insertOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> insertOrderItemList = new ArrayList<>();

        // 修改方法

        // 1.先删除当前测试方法下样品分析项组
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = getDao().selectList(oldOrderItemGroup, Arrays.asList("SCHEMENAME", "TESTMETHODID", "ORDERID"), Arrays.asList("ID"));
        delete(deleteOrderItemGroupList);

        // 2.根据当前选择的测试方法查询分析项组
        List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByOneColumnValue(orderItemGroup.getTestMethodId(), "TESTMETHODID");

        // 3.根据分析项组ID查询分析项
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupList.stream().map(e -> e.getId()).collect(Collectors.toList()), "ITEMGROUPID");

        // 4.插入样品分析项组、样品分析项
        itemGroupList.forEach(e -> {
            EmcOrderItemGroupBean insertOrderItemGroup = new EmcOrderItemGroupBean();
            BeanUtils.copyProperties(oldOrderItemGroup, insertOrderItemGroup);

            Long orderItemGroupId = ApplicationContextHelper.getNextIdentity();
            insertOrderItemGroup.setId(orderItemGroupId);
            insertOrderItemGroup.setTestMethodId(orderItemGroup.getTestMethodId());
            insertOrderItemGroup.setItemGroupId(e.getId());
            insertOrderItemGroup.setItemGroup(e.getItemGroup());
            insertOrderItemGroup.setMethodId(orderItemGroup.getMethodId());
            insertOrderItemGroup.setMethodName(orderItemGroup.getMethodName());
            insertOrderItemGroup.setTestMethodBasis(orderItemGroup.getTestMethodBasis());
            insertOrderItemGroup.setMethodVersionId(orderItemGroup.getMethodVersionId());
            insertOrderItemGroup.setAnalysisOrgId(orderItemGroup.getAnalysisOrgId());
            insertOrderItemGroup.setAnalysisOrgName(orderItemGroup.getAnalysisOrgName());
            insertOrderItemGroup.setInputUserId(orderItemGroup.getInputUserId());
            insertOrderItemGroup.setInputUserName(orderItemGroup.getInputUserName());

            insertOrderItemGroupList.add(insertOrderItemGroup);

            groupItemList.stream().filter(i -> ObjectUtils.equals(i.getItemGroupId(), e.getId())).forEach(g -> {

                EmcOrderItemBean insertOrderItem = new EmcOrderItemBean();

                insertOrderItem.setId(ApplicationContextHelper.getNextIdentity());
                insertOrderItem.setOrderId(insertOrderItemGroup.getOrderId());
                insertOrderItem.setOrderItemGroupId(orderItemGroupId);
                insertOrderItem.setItemId(g.getItemId());

                insertOrderItemList.add(insertOrderItem);

            });

        });

        orderItemService.getDao().insert(insertOrderItemList);
        getDao().insert(insertOrderItemGroupList);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        List<Long> orderIdList = orderService.getDao().selectListByOneColumnValue(orderService.selectById(orderItemGroupList.get(0).getOrderId()).getProjId(), "PROJID", Arrays.asList("ID")).stream()
                .map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> subcontractorOrderItemGroupList = getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Arrays.asList("ID", "SUBCONTRACTORID", "SUBPACKAGENO")).stream()
                .filter(e -> !ObjectUtils.isEmpty(e.getSubcontractorId())).collect(Collectors.toList());

        EmcOrderItemGroupBean updateOrderItemGroup = new EmcOrderItemGroupBean();
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
