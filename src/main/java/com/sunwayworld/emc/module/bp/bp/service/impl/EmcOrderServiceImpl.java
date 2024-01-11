package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.bp.bp.dao.EmcFolderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean.EmcContainerCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.EmcContainerCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public class EmcOrderServiceImpl implements EmcOrderService {
    @Autowired
    private EmcOrderDao orderDao;

    @Autowired
    private EmcFolderDao folderDao;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    private EmcContainerCategoryService containerCategoryService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderDao getDao() {
        return orderDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void copyCertTestMethod(RestJsonWrapperBean wrapper) {
        // 选中的样品信息(包含 orderId 和 要修改的信息)
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        getDao().updateIfChanged(orderList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long projectId, RestJsonWrapperBean wrapper) {
        EmcOrderBean order = wrapper.parse(EmcOrderBean.class).get(0);
        order.setProjId(projectId);

        EmcFolderBean folder = folderService.insertByOrder(order);

        order.setId(ApplicationContextHelper.getNextIdentity());
        order.setFolderId(folder.getId());
        order.setOrderNo(folder.getFolderNo() + "001");
        if (order.getLabelNo() == null) {
            order.setLabelNo(order.getOrderNo());
        }
        order.setSendDate(LocalDate.now());
        order.setSendTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
        order.setOrderNum(EmcConstant.ONE);
        order.setCycle(EmcConstant.ONE);
        order.setFrequency(EmcConstant.ONE);
        order.setOrderNum(EmcConstant.ONE);
        order.setTermination(Constant.NO);
        getDao().insert(order);

        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        // 委托送样默认容器类型，针对送样的任务，默认设置固定的容器信息
        List<EmcContainerCategoryBean> containerCategoryList = containerCategoryService.getDao().selectListByOneColumnValue("WS", "CONTAINERCATEGORYID", Arrays.asList("ID", "CONTAINERCATEGORYID", "CONTAINERCATEGORYNAME"));
        orderItemGroupList.forEach(itemGroup -> {
            // 容器组为999，固定剂为/
            itemGroup.setContainerGroup("999");
            itemGroup.setFixative("/");
            itemGroup.setStoreCondition("/");
            if (!containerCategoryList.isEmpty()) {
                itemGroup.setContainerCategoryId(containerCategoryList.get(0).getId());
                itemGroup.setContainerCategoryCode(containerCategoryList.get(0).getContainerCategoryId());
                itemGroup.setContainerCategoryName(containerCategoryList.get(0).getContainerCategoryName());
            }
        });

        orderItemGroupService.insert(Arrays.asList(order), orderItemGroupList);

        return order.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderBean> oldOrderList = jsonWrapper.parse(EmcOrderBean.class);
        int copies = NumberUtils.parseInt(jsonWrapper.getParamValue("copies"));// 复制份数

        List<Long> oldOrderIdList = oldOrderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");
        List<EmcOrderItemBean> oldOrderItemList = orderItemService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();

        List<Long> folderIdList = oldOrderList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        List<EmcFolderBean> oldFolderList = folderService.getDao().selectListByOneColumnValues(folderIdList, "ID");

        Long projId = oldOrderList.get(0).getProjId();
        EmcProjectBean project = projectService.selectById(projId);
        int folderCount = oldFolderList.size();
        List<String> newFolderNoList = folderService.generateFolderNosByProjId(projId, project.getProjNo(), folderCount * copies);

        List<EmcFolderBean> insertFolderList = new ArrayList<>();

        for (int j = 0; j < copies; j++) {

            for (int i = 0; i < folderCount; i++) {
                // 点位
                EmcFolderBean oldFolder = oldFolderList.get(i);
                EmcFolderBean insertFolder = new EmcFolderBean();
                BeanUtils.copyProperties(oldFolder, insertFolder);
                Long newFolderId = ApplicationContextHelper.getNextIdentity();
                String newFolderNo = newFolderNoList.get(j * folderCount + i);
                insertFolder.setId(newFolderId);
                insertFolder.setFolderNo(newFolderNo);
                insertFolderList.add(insertFolder);

                // 样品
                EmcOrderBean oldOrder = oldOrderList.stream().filter(e -> ObjectUtils.equals(oldFolder.getId(), e.getFolderId())).findFirst().orElse(null);
                EmcOrderBean newOrder = new EmcOrderBean();
                BeanUtils.copyProperties(oldOrder, newOrder);
                Long newOrderId = ApplicationContextHelper.getNextIdentity();
                newOrder.setId(newOrderId);
                //String newOrderNo = newFolderNo + "001";
                //解决编号中间带-，类似HZSJ2100080-001-001这种
                String newOrderNo = newFolderNo + "001";
                if(newFolderNo.contains("-")){
                    newOrderNo = newFolderNo + "-001";
                }
                newOrder.setOrderNo(newOrderNo);
                newOrder.setLabelNo(newOrderNo);
                newOrder.setOrderNum(1L);
                newOrder.setFolderId(newFolderId);
                newOrderList.add(newOrder);

                oldOrderItemGroupList.stream().filter(t -> ObjectUtils.equals(t.getOrderId(), oldOrder.getId())).forEach(t -> {
                    Long newOrderItemGroupId = ApplicationContextHelper.getNextIdentity();
                    oldOrderItemList.stream().filter(r -> ObjectUtils.equals(r.getOrderItemGroupId(), t.getId())).forEach(r -> {
                        EmcOrderItemBean newOrderItem = new EmcOrderItemBean();
                        BeanUtils.copyProperties(r, newOrderItem);
                        newOrderItem.setId(ApplicationContextHelper.getNextIdentity());
                        newOrderItem.setOrderId(newOrderId);
                        newOrderItem.setOrderItemGroupId(newOrderItemGroupId);
                        newOrderItemList.add(newOrderItem);
                    });

                    EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
                    BeanUtils.copyProperties(t, newOrderItemGroup);
                    newOrderItemGroup.setId(newOrderItemGroupId);
                    newOrderItemGroup.setOrderId(newOrderId);
                    //送样任务复制不要容器相关信息
                    if(!"1".equals(ObjectUtils.toString(project.getSample()))){
                        newOrderItemGroup.setContainerCategoryId(null);
                        newOrderItemGroup.setContainerCategoryCode(null);
                        newOrderItemGroup.setContainerCategoryName(null);
                        newOrderItemGroup.setContainerGroup("1");
                    }
                    newOrderItemGroupList.add(newOrderItemGroup);
                });
            }
        }

        folderService.getDao().insert(insertFolderList);
        getDao().insert(newOrderList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
        orderItemService.getDao().insert(newOrderItemList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyQc(RestJsonWrapperBean jsonWrapper) {

        List<EmcOrderBean> oldOrderList = jsonWrapper.parse(EmcOrderBean.class);

        List<Long> oldOrderIdList = oldOrderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");
        List<EmcOrderItemBean> oldOrderItemList = orderItemService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");

        List<EmcItemBean> itemList = itemService.selectListByIds(oldOrderItemList.stream().map(e -> e.getItemId()).distinct().collect(Collectors.toList()));

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();

        List<Long> folderIdList = oldOrderList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        List<EmcFolderBean> oldFolderList = folderService.getDao().selectListByOneColumnValues(folderIdList, "ID");

        Long projId = oldOrderList.get(0).getProjId();
        EmcProjectBean project = projectService.selectById(projId);
        int folderCount = oldFolderList.size();
        List<String> newFolderNoList = folderService.generateFolderNosByProjId(projId, project.getProjNo(), folderCount);

        List<EmcFolderBean> insertFolderList = new ArrayList<>();

        for (int i = 0; i < folderCount; i++) {
            // 点位
            EmcFolderBean oldFolder = oldFolderList.get(i);
            EmcFolderBean insertFolder = new EmcFolderBean();
            BeanUtils.copyProperties(oldFolder, insertFolder);
            Long newFolderId = ApplicationContextHelper.getNextIdentity();
            String newFolderNo = newFolderNoList.get(i);
            insertFolder.setId(newFolderId);
            insertFolder.setFolderNo(newFolderNo);
            insertFolderList.add(insertFolder);

            // 样品
            EmcOrderBean newOrder = new EmcOrderBean();
            EmcOrderBean oldOrder = oldOrderList.stream().filter(e -> ObjectUtils.equals(oldFolder.getId(), e.getFolderId())).findFirst().orElse(null);
            String oldOrderNo = oldOrder.getOrderNo();
            Long newOrderId = ApplicationContextHelper.getNextIdentity();

            oldOrderItemGroupList.stream().filter(t -> ObjectUtils.equals(t.getOrderId(), oldOrder.getId())).forEach(t -> {
                Long newOrderItemGroupId = ApplicationContextHelper.getNextIdentity();

                oldOrderItemList.stream().filter(r -> ObjectUtils.equals(r.getOrderItemGroupId(), t.getId())).forEach(r -> {
                    EmcItemBean item = itemList.stream().filter(e -> ObjectUtils.equals(e.getId(), r.getItemId())).findFirst().get();

                    if (!StringUtils.isEmpty(item.getQcCategoryId()) && ArrayUtils.contains(StringUtils.split(item.getQcCategoryId(), ","), "" + oldOrder.getQcCategoryId())) {
                        r.setId(ApplicationContextHelper.getNextIdentity());
                        r.setOrderId(newOrderId);
                        r.setOrderItemGroupId(newOrderItemGroupId);
                        newOrderItemList.add(r);
                    }
                });

                t.setId(newOrderItemGroupId);
                t.setOrderId(newOrderId);
                t.setQcCategoryId(oldOrder.getQcCategoryId());
                t.setQcCategoryCode(oldOrder.getQcCategoryCode());
                t.setQcName(oldOrder.getQcName());
                newOrderItemGroupList.add(t);
            });

            BeanUtils.copyProperties(oldOrder, newOrder);
            newOrder.setFolderId(newFolderId);
            newOrder.setId(newOrderId);
            newOrder.setOrderNo(newFolderNo + "001");
            if(newFolderNo.contains("-")){
                newOrder.setOrderNo(newFolderNo + "-001");
            }
            newOrder.setParentOrderNo(oldOrderNo);
            newOrderList.add(newOrder);
        }

        folderService.getDao().insert(insertFolderList);
        getDao().insert(newOrderList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
        orderItemService.getDao().insert(newOrderItemList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void edit(RestJsonWrapperBean wrapper) {

        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderItemGroupBean> newOrderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        if (!oldOrderItemGroupList.isEmpty()) {
            orderItemGroupService.delete(oldOrderItemGroupList);
        }

        newOrderItemGroupList.forEach(itemGroup -> itemGroup.setMonitorTmplId(orderList.get(0).getMonitorTmplId()));

        orderItemGroupService.insert(orderList, newOrderItemGroupList);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void editTest(RestJsonWrapperBean wrapper) {
        String[] ids = StringUtils.split(wrapper.getParamValue("ids"), ",");
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class, 1);

        List<Long> orderIdList = Arrays.asList(ids).stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
        List<EmcOrderBean> orderList = getDao().selectListByIds(orderIdList);
        List<EmcOrderItemGroupBean> allOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 删除没有的，添加新增的
        List<EmcOrderTaskBean> allOldOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderTaskBean> deleteOldOrderTaskList = new ArrayList<>();
        List<EmcOrderItemGroupBean> allNewOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemGroupBean> allDeleteOrderItemGroupList = new ArrayList<>();

        orderList.forEach(o -> {
            List<EmcOrderTaskBean> oldOrderTaskList = allOldOrderTaskList.stream().filter(e -> ObjectUtils.equals(o.getId(), e.getOrderId())).collect(Collectors.toList());

            List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

            // 删除未添加的样品测试
            oldOrderTaskList.forEach(t -> {

                EmcOrderItemGroupBean matchOrderItemGroup = orderItemGroupList.stream().filter(e -> ObjectUtils.equals(t.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                if (matchOrderItemGroup == null) {
                    deleteOldOrderTaskList.add(t);
                    List<EmcOrderItemGroupBean> deleteOrderItemGroupList = allOrderItemGroupList.stream()
                            .filter(a -> ObjectUtils.equals(t.getOrderId(), a.getOrderId()) && ObjectUtils.equals(t.getTestMethodId(), a.getTestMethodId())).collect(Collectors.toList());
                    allDeleteOrderItemGroupList.addAll(deleteOrderItemGroupList);
                }
            });

            // 筛选非重复的所有的新的样品测试
            orderItemGroupList.forEach(i -> {

                EmcOrderTaskBean matchOrderTask = oldOrderTaskList.stream().filter(e -> ObjectUtils.equals(i.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                if (matchOrderTask == null) {

                    EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
                    BeanUtils.copyProperties(i, newOrderItemGroup);
                    newOrderItemGroup.setOrderId(o.getId());
                    newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                    newOrderItemGroupList.add(newOrderItemGroup);
                }
            });
            allNewOrderItemGroupList.addAll(newOrderItemGroupList);

        });

        List<Long> orderTaskIdList = deleteOldOrderTaskList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());

        List<EmcOrderTaskResultBean> delOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        // 生成主业务流程数据
        orderTaskService.setDataByOrderList(orderList, allOldOrderTaskList, allNewOrderItemGroupList, EmcBizConstant.ORDER_TASK_LOG_SAMPLING);

        // 插入样品分析项组、样品分析项表
        // 上面orderTaskService.setDataByOrderList() 方法会插入样品分析项组数据，此处再插入会重复
        // orderItemGroupService.insert(orderList, allNewOrderItemGroupList, false);

        // 删除样品分析项组、样品分析项表
        orderItemGroupService.delete(allDeleteOrderItemGroupList);

        // 插入样品测试记录表
        orderTaskLogService.insert(deleteOldOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_DELETE_TEST, EmcBizConstant.ORDER_TASK_LOG_SAMPLING);

        // 删除样品测试
        orderTaskService.getDao().deleteByIdList(orderTaskIdList);

        // 删除样品测试结果
        orderTaskResultService.getDao().deleteByIdList(delOrderTaskResultList.stream().map(e -> e.getId()).collect(Collectors.toList()));

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editMethod(Long orderItemGroupId, RestJsonWrapperBean wrapper) {
        orderItemGroupService.editMethod(orderItemGroupId, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editItem(Long orderItemGroupId, RestJsonWrapperBean wrapper) {
        orderItemService.editItem(orderItemGroupId, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void terminateOrder(RestJsonWrapperBean wrapper) {
        String termination = wrapper.getParamValue("termination");
        String remark = Constant.YES.equals(termination) ? "终止样品" : "取消终止样品";
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> idList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderBean> updateOrderList = getDao().selectListByIds(idList);
        updateOrderList.stream().forEach(e -> {
            e.setTermination(termination);
            e.setRemark(remark);
        });

        getDao().update(updateOrderList, "TERMINATION", "REMARK");
    }

    @Override
    public Page<EmcOrderItemGroupBean> selectItemGroupPagationByOrder(Long id, RestJsonWrapperBean wrapper) {
        return orderItemGroupService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long addOrder(Long id, RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        EmcSampleBean sampleBean = new EmcSampleBean();
        sampleBean.setId(id);
        sampleBean.setOrderCategory(orderList.get(0).getOrderCategory());

        sampleService.getDao().update(sampleBean, "ORDERCATEGORY");

        orderList.stream().forEach(o -> {
            o.setSampleId(id);
        });
        getDao().updateIfChanged(orderList);
        return orderList.get(0).getId();
    }

    /**
     * 更新某采样单中样品的信息
     *
     * @param wrapper
     * @return
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateOrder(RestJsonWrapperBean wrapper, String... updateColNames) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        getDao().update(orderList, updateColNames);
    }

    @Override
    public Page<EmcOrderItemBean> selectItemPagationByOrderItemGroup(Long orderItemGroupId, RestJsonWrapperBean wrapper) {
        return orderItemService.selectPaginationByFilter(SearchFilter.instance().match("ORDERITEMGROUPID", orderItemGroupId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertBpDataByProject(List<Long> projectIdList) {
        List<EmcOrderBean> orderList = getDao().selectListByOneColumnValues(projectIdList, "PROJID");
        if (!orderList.isEmpty()) {
            orderTaskService.insertByOrder(orderList);
        }
    }

    @Override
    public Page<EmcOrderTaskBean> selectContainerInfoByOrderId(Long id, RestJsonWrapperBean wrapper) {
        return orderTaskService.selectContainerInfoByOrderId(id, wrapper);
    }

    @Override
    public Page<EmcOrderTaskResultBean> selectOrderTaskResult(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectPagination(wrapper);
    }

    @Override
    public void updateContainerInfo(RestJsonWrapperBean jsonWrapper) {
        orderTaskService.updateContainerInfo(jsonWrapper);
    }

    @Override
    public void updatePartake(RestJsonWrapperBean jsonWrapper) {
        orderTaskService.updatePartake(jsonWrapper);
    }

    @Override
    public void initTaskResult(Long id, RestJsonWrapperBean wrapper) {
        orderTaskResultService.initTaskResult(id, wrapper);
    }

    @Override
    public void setDoneTaskResult(RestJsonWrapperBean wrapper) {
        orderTaskResultService.setDoneTaskResult(wrapper);
    }

    @Override
    public void setDoneOrder(RestJsonWrapperBean wrapper) {
        orderTaskResultService.setDoneOrder(wrapper);
    }

    @Override
    public void setDoneSampleOrder(RestJsonWrapperBean wrapper) {
        orderTaskResultService.setDoneSampleOrder(wrapper);
    }

    @Override
    public Page<EmcOrderBean> selectOrderList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List<EmcOrderBean> orderList = getDao().selectOrderList(parameter);
        return new Pagination<>(orderList);
    }

    @Override
    public void setDataInputDefaultTaskResult(Long id, RestJsonWrapperBean wrapper) {
        orderTaskResultService.setDataInputDefaultTaskResult(id, wrapper);
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskEquipt(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectPagination(wrapper);
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectOrderTaskPagationByOrder(wrapper);
    }

    @Override
    public void addRepeatNo(Long id, RestJsonWrapperBean wrapper) {
        orderTaskResultService.addRepeatNo(id, wrapper);
    }

    @Override
    public void deleteRepeatNo(RestJsonWrapperBean wrapper) {
        orderTaskResultService.deleteRepeatNo(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        orderItemGroupService.editSubcontractor(wrapper);
    }

    @Override
    public void syncTmplInfo(RestJsonWrapperBean wrapper) {
        orderTaskResultService.syncTmplInfo(wrapper);
    }

    @Override
    public void removeFomula(RestJsonWrapperBean wrapper) {
        orderTaskResultService.removeFomula(wrapper);
    }

    @Override
    public void setDefaultTaskResult(Long id, RestJsonWrapperBean wrapper) {
        orderTaskResultService.setDefaultTaskResult(id, wrapper);
    }

    @Override
    public void addCheckProjectItemGroup(Long id, RestJsonWrapperBean wrapper) {

        List<EmcOrderItemGroupBean> newOrderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        EmcOrderBean order = getDao().selectById(id);
        ArrayList<EmcOrderBean> orderList = new ArrayList<>();
        orderList.add(order);

        newOrderItemGroupList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setOrderId(order.getId());
            e.setMonitorTmplId(order.getMonitorTmplId());
            e.setOrderCategory(order.getOrderCategory());
        });
        orderTaskService.setDataByOrderList(orderList, new ArrayList<>(), newOrderItemGroupList, EmcBizConstant.ORDER_TASK_LOG_CHECK_PROJECT_EDIT,
                EmcBizConstant.ORDERTASK_STATUS_LOGGED);
        //是否插入orderItem表
        orderItemGroupService.insertByCheckProjectItemGroup(orderList, newOrderItemGroupList, false);
    }

    @Override
    public void delCheckProjectItemGroup(RestJsonWrapperBean wrapper) {

        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderItemGroupBean> filterList = new ArrayList<>();
        orderTaskList.forEach(a -> {
            EmcOrderItemGroupBean filterTemp = new EmcOrderItemGroupBean();
            filterTemp.setOrderId(a.getOrderId());
            filterTemp.setTestMethodId(a.getTestMethodId());
            filterList.add(filterTemp);
        });
        List<EmcOrderItemGroupBean> delList = orderItemGroupService.getDao().selectList(filterList, Arrays.asList("ORDERID", "TESTMETHODID"), Collections.emptyList());
        orderItemGroupService.delete(delList);
        orderTaskService.delete(wrapper);
    }

    @Override
    public void editCheckProjectItem(Long orderTaskId, RestJsonWrapperBean wrapper) {
        orderTaskService.editResult(orderTaskId, wrapper);
    }

    @Override
    public void editCheckMethodItem(Long orderTaskId, RestJsonWrapperBean wrapper) {
        orderTaskService.editMethod(orderTaskId, wrapper);
    }

    @Override
    public void copyOrders(RestJsonWrapperBean wrapper) {
        EmcOrderBean order = wrapper.parseUnique(EmcOrderBean.class);
        List<Long> orderIdList = new ArrayList<>();
        orderIdList.add(order.getId());

        // 查询样品信息
        order = getDao().selectById(order.getId());

        List<EmcOrderBean> orderList = getDao().selectListByOneColumnValue(order.getFolderId(), "FOLDERID");
        Optional<EmcOrderBean> orderOp = orderList.stream().max(Comparator.comparing(EmcOrderBean::getOrderNo));
        EmcOrderBean maxOrderNo = orderOp.get();

        List<EmcFolderBean> folderlist = folderDao.selectListByOneColumnValue(order.getFolderId(),"ID");

        //复制样品生成编号规则重写
        String newOrderNo="";
        if(!folderlist.isEmpty()){
            String folderno = folderlist.get(0).getFolderNo();
            String maxOrdno = maxOrderNo.getOrderNo();
            if(maxOrdno.substring(folderno.length()).contains("-")){
                newOrderNo = folderno + "-" + String.format("%03d", Long.parseLong(maxOrdno.substring(folderno.length()+1)) + 1);
            }else{
                newOrderNo = folderno + String.format("%03d", Long.parseLong(maxOrdno.substring(folderno.length())) + 1);
            }
        }else{
            newOrderNo = maxOrderNo.getOrderNo().substring(0, 11) + String.format("%03d", Long.parseLong(maxOrderNo.getOrderNo().substring(11)) + 1);
        }

        EmcOrderBean newOrder = new EmcOrderBean();
        BeanUtils.copyProperties(order, newOrder);

        Long newOrderId = ApplicationContextHelper.getNextIdentity();
        newOrder.setId(newOrderId);
        newOrder.setOrderNo(newOrderNo);
        newOrder.setLabelNo(newOrderNo);
        getDao().insert(newOrder);

        // 查询样品下的对应测试信息和测试结果信息
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 查询样品分析项组
        List<EmcOrderItemGroupBean> copiedOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        // 复制样品分析项组
        copyOrderItemGroup(copiedOrderItemGroupList, order, newOrderId, newOrderItemGroupList);

        for (EmcOrderTaskBean orderTask : orderTaskList) {
            if (order.getId().equals(orderTask.getOrderId())) {
                EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                BeanUtils.copyProperties(orderTask, newOrderTask);

                Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                newOrderTask.setId(newOrderTaskId);
                newOrderTask.setOrderId(newOrderId);
                newOrderTask.setEquiptAcquNo(newOrderNo);
                newOrderTaskList.add(newOrderTask);

                for (EmcOrderTaskResultBean orderTaskResult : orderTaskResultList) {
                    if (orderTask.getId().equals(orderTaskResult.getOrderTaskId())) {
                        EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                        BeanUtils.copyProperties(orderTaskResult, newOrderTaskResult);

                        newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                        newOrderTaskResult.setOrderId(newOrderId);
                        newOrderTaskResult.setOrderTaskId(newOrderTaskId);
                        newOrderTaskResult.setOrderNo(newOrderNo);
                        newOrderTaskResultList.add(newOrderTaskResult);
                    }
                }
            }
        }
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
    }

    private void copyOrderItemGroup(List<EmcOrderItemGroupBean> copiedOrderItemGroupList, EmcOrderBean copiedOrder, Long newOrderId, List<EmcOrderItemGroupBean> newOrderItemGroupList) {
        List<EmcOrderItemGroupBean> currentOrderItemGroupList = copiedOrderItemGroupList.stream().filter(e -> ObjectUtils.equals(copiedOrder.getId(), e.getOrderId())).collect(Collectors.toList());
        currentOrderItemGroupList.forEach(copiedOrderItemGroup -> {
            EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
            com.sunwayworld.framework.utils.BeanUtils.copyProperties(copiedOrderItemGroup, newOrderItemGroup, EmcConstant.CREATED_COLUMNS);
            newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
            newOrderItemGroup.setOrderId(newOrderId);
            newOrderItemGroupList.add(newOrderItemGroup);
        });
    }
}
