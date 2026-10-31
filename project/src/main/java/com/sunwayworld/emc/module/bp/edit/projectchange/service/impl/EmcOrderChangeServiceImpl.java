package com.sunwayworld.emc.module.bp.edit.projectchange.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcFolderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcOrderChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcFolderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderItemGroupChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjectChangeService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
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
public class EmcOrderChangeServiceImpl implements EmcOrderChangeService {

    @Autowired
    private EmcOrderChangeDao orderDao;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcProjectChangeService projectService;

    @Autowired
    @Lazy
    private EmcFolderChangeService folderService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcOrderItemGroupChangeService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderChangeDao getDao() {
        return orderDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long projectId, RestJsonWrapperBean wrapper) {
        EmcOrderChangeBean order = wrapper.parse(EmcOrderChangeBean.class).get(0);
        order.setProjId(projectId);

        EmcFolderChangeBean folder = folderService.insertByOrder(order);

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

        List<EmcOrderItemGroupChangeBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupChangeBean.class);

        // 委托送样默认容器类型，针对送样的任务，默认设置固定的容器信息
        orderItemGroupList.forEach(itemGroup -> {
            // 容器组为999，固定剂为/
            itemGroup.setContainerGroup("999");
            itemGroup.setFixative("/");
            itemGroup.setStoreCondition("/");
        });

        orderItemGroupService.insert(Collections.singletonList(order), orderItemGroupList);

        return order.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderChangeBean> oldOrderList = jsonWrapper.parse(EmcOrderChangeBean.class);
        int copies = NumberUtils.parseInt(jsonWrapper.getParamValue("copies"));
        String operateType = jsonWrapper.getParamValue("operateType");
        boolean schemeEdit = StringUtils.equals(operateType, "schemeEdit");

        List<Long> oldOrderIdList = oldOrderList.stream().map(EmcOrderChangeBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupChangeBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");
        List<EmcOrderItemBean> oldOrderItemList = orderItemService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");

        List<EmcOrderChangeBean> newOrderList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();

        List<Long> folderIdList = oldOrderList.stream().map(EmcOrderChangeBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderChangeBean> oldFolderList = folderService.getDao().selectListByOneColumnValues(folderIdList, "ID");

        Long projId = oldOrderList.get(0).getProjId();
        EmcProjectChangeBean project = projectService.selectById(projId);

        List<String> monitorTmplIdList = oldOrderList.stream().map(EmcOrderChangeBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByIds(monitorTmplIdList);

        Map<String, String> orderCategoryCodeMap = monitorTmplList.stream().collect(HashMap::new, (map, item) -> map.put(item.getMonitorTmplName(), item.getExt$Item("orderCategoryCode")), HashMap::putAll);

        List<EmcFolderChangeBean> insertFolderList = new ArrayList<>();
        Map<String, List<EmcOrderChangeBean>> orderListMap = oldOrderList.stream().collect(Collectors.groupingBy(EmcOrderChangeBean::getOrderCategory));
        orderListMap.forEach((orderCategory, curOrderList) -> {
            List<String> curNewFolderNoList = generateFolderNosByProjId(project.getId(), project.getProjNo(), curOrderList.size() * copies);
            List<Long> curFolderIdList = curOrderList.stream().map(EmcOrderChangeBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderChangeBean> curFolderList = oldFolderList.stream().filter(f -> curFolderIdList.contains(f.getId())).collect(Collectors.toList());
            int curFolderCount = curFolderList.size();
            IntStream.range(0, copies).forEach(copyIndex -> {
                IntStream.range(0, curFolderList.size()).forEach(folderIndex -> {
                    // 点位
                    EmcFolderChangeBean oldFolder = curFolderList.get(folderIndex);
                    EmcFolderChangeBean insertFolder = new EmcFolderChangeBean();
                    BeanUtils.copyProperties(oldFolder, insertFolder);
                    Long newFolderId = ApplicationContextHelper.getNextIdentity();
                    String newFolderNo = curNewFolderNoList.get(copyIndex * curFolderCount + folderIndex);
                    insertFolder.setId(newFolderId);
                    insertFolder.setFolderNo(newFolderNo);
                    insertFolderList.add(insertFolder);

                    // 样品
                    EmcOrderChangeBean oldOrder = curOrderList.stream().filter(e -> ObjectUtils.equals(oldFolder.getId(), e.getFolderId())).findFirst().orElseGet(EmcOrderChangeBean::new);
                    EmcOrderChangeBean newOrder = new EmcOrderChangeBean();
                    BeanUtils.copyProperties(oldOrder, newOrder);
                    Long newOrderId = ApplicationContextHelper.getNextIdentity();
                    newOrder.setId(newOrderId);
                    String newOrderNo = newFolderNo + "A001";
                    newOrder.setOrderNo(newOrderNo);
                    newOrder.setLabelNo(newOrderNo);
                    newOrder.setOrderNum(1L);
                    newOrder.setFolderId(newFolderId);
                    newOrder.setExt$Item("syncStatus", schemeEdit ? Constant.YES : null);
                    newOrder.setSource(schemeEdit ? "schemeEdit" : oldOrder.getSource());
                    newOrder.setSampleId(schemeEdit ? null : oldOrder.getSampleId());
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

                        EmcOrderItemGroupChangeBean newOrderItemGroup = new EmcOrderItemGroupChangeBean();
                        BeanUtils.copyProperties(t, newOrderItemGroup);
                        newOrderItemGroup.setId(newOrderItemGroupId);
                        newOrderItemGroup.setOrderId(newOrderId);
                        newOrderItemGroup.setExt$Item("syncStatus", schemeEdit ? Constant.YES : null);
                        newOrderItemGroup.setExt$Item("source", schemeEdit ? "schemeEdit" : null);
                        newOrderItemGroupList.add(newOrderItemGroup);
                    });
                });
            });
        });

        folderService.getDao().insert(insertFolderList);
        getDao().insert(newOrderList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
        orderItemService.getDao().insert(newOrderItemList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyQc(RestJsonWrapperBean jsonWrapper) {

        List<EmcOrderChangeBean> oldOrderList = jsonWrapper.parse(EmcOrderChangeBean.class);

        List<Long> oldOrderIdList = oldOrderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderItemGroupChangeBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");
        List<EmcOrderItemBean> oldOrderItemList = orderItemService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");

        List<EmcItemBean> itemList = itemService.selectListByIds(oldOrderItemList.stream().map(e -> e.getItemId()).distinct().collect(Collectors.toList()));

        List<EmcOrderChangeBean> newOrderList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();

        List<Long> folderIdList = oldOrderList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        List<EmcFolderChangeBean> oldFolderList = folderService.getDao().selectListByOneColumnValues(folderIdList, "ID");

        Long projId = oldOrderList.get(0).getProjId();
        EmcProjectChangeBean project = projectService.selectById(projId);
        int folderCount = oldFolderList.size();
        List<String> newFolderNoList = folderService.generateFolderNosByProjId(projId, project.getProjNo(), folderCount);

        List<EmcFolderChangeBean> insertFolderList = new ArrayList<>();

        for (int i = 0; i < folderCount; i++) {
            // 点位
            EmcFolderChangeBean oldFolder = oldFolderList.get(i);
            EmcFolderChangeBean insertFolder = new EmcFolderChangeBean();
            BeanUtils.copyProperties(oldFolder, insertFolder);
            Long newFolderId = ApplicationContextHelper.getNextIdentity();
            String newFolderNo = newFolderNoList.get(i);
            insertFolder.setId(newFolderId);
            insertFolder.setFolderNo(newFolderNo);
            insertFolderList.add(insertFolder);

            // 样品
            EmcOrderChangeBean newOrder = new EmcOrderChangeBean();
            EmcOrderChangeBean oldOrder = oldOrderList.stream().filter(e -> ObjectUtils.equals(oldFolder.getId(), e.getFolderId())).findFirst().orElse(null);
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

        List<EmcOrderChangeBean> orderList = wrapper.parse(EmcOrderChangeBean.class);
        List<EmcOrderItemGroupChangeBean> newOrderItemGroupList = wrapper.parse(EmcOrderItemGroupChangeBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderChangeBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupChangeBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
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
        List<EmcOrderItemGroupChangeBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupChangeBean.class, 1);

        List<Long> orderIdList = Arrays.asList(ids).stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
        List<EmcOrderChangeBean> orderList = getDao().selectListByIds(orderIdList);
        List<EmcOrderItemGroupChangeBean> allOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 删除没有的，添加新增的
        List<EmcOrderTaskBean> allOldOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderTaskBean> deleteOldOrderTaskList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> allNewOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> allDeleteOrderItemGroupList = new ArrayList<>();

        orderList.forEach(o -> {
            List<EmcOrderTaskBean> oldOrderTaskList = allOldOrderTaskList.stream().filter(e -> ObjectUtils.equals(o.getId(), e.getOrderId())).collect(Collectors.toList());

            List<EmcOrderItemGroupChangeBean> newOrderItemGroupList = new ArrayList<>();

            // 删除未添加的样品测试
            oldOrderTaskList.forEach(t -> {

                EmcOrderItemGroupChangeBean matchOrderItemGroup = orderItemGroupList.stream().filter(e -> ObjectUtils.equals(t.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                if (matchOrderItemGroup == null) {
                    deleteOldOrderTaskList.add(t);
                    List<EmcOrderItemGroupChangeBean> deleteOrderItemGroupList = allOrderItemGroupList.stream()
                            .filter(a -> ObjectUtils.equals(t.getOrderId(), a.getOrderId()) && ObjectUtils.equals(t.getTestMethodId(), a.getTestMethodId())).collect(Collectors.toList());
                    allDeleteOrderItemGroupList.addAll(deleteOrderItemGroupList);
                }
            });

            // 筛选非重复的所有的新的样品测试
            orderItemGroupList.forEach(i -> {

                EmcOrderTaskBean matchOrderTask = oldOrderTaskList.stream().filter(e -> ObjectUtils.equals(i.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                if (matchOrderTask == null) {

                    EmcOrderItemGroupChangeBean newOrderItemGroup = new EmcOrderItemGroupChangeBean();
                    BeanUtils.copyProperties(i, newOrderItemGroup);
                    newOrderItemGroup.setOrderId(o.getId());
                    newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                    newOrderItemGroupList.add(newOrderItemGroup);
                }
            });
            allNewOrderItemGroupList.addAll(newOrderItemGroupList);

        });

        // 删除样品分析项组、样品分析项表
        orderItemGroupService.delete(allDeleteOrderItemGroupList);

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
        List<EmcOrderChangeBean> orderList = wrapper.parse(EmcOrderChangeBean.class);
        List<Long> idList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderChangeBean> updateOrderList = getDao().selectListByIds(idList);
        updateOrderList.stream().forEach(e -> {
            e.setTermination(termination);
            e.setRemark(remark);
        });

        getDao().update(updateOrderList, "TERMINATION", "REMARK");
    }

    @Override
    public Page<EmcOrderItemGroupChangeBean> selectItemGroupPagationByOrder(Long id, RestJsonWrapperBean wrapper) {
        return orderItemGroupService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long addOrder(Long id, RestJsonWrapperBean wrapper) {
        List<EmcOrderChangeBean> orderList = wrapper.parse(EmcOrderChangeBean.class);

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
        List<EmcOrderChangeBean> orderList = wrapper.parse(EmcOrderChangeBean.class);
        getDao().update(orderList, updateColNames);
    }

    @Override
    public Page<EmcOrderItemBean> selectItemPagationByOrderItemGroup(Long orderItemGroupId, RestJsonWrapperBean wrapper) {
        return orderItemService.selectPaginationByFilter(SearchFilter.instance().match("ORDERITEMGROUPID", orderItemGroupId).filter(MatchPattern.EQ), wrapper);
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
    public Page<EmcOrderChangeBean> selectOrderList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List<EmcOrderChangeBean> orderList = getDao().selectOrderList(parameter);
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
    public void copyOrders(RestJsonWrapperBean wrapper) {
        EmcOrderChangeBean order = wrapper.parseUnique(EmcOrderChangeBean.class);
        List<Long> orderIdList = new ArrayList<>();
        orderIdList.add(order.getId());

        // 查询样品信息
        order = getDao().selectById(order.getId());

        List<EmcOrderChangeBean> orderList = getDao().selectListByOneColumnValue(order.getFolderId(), "FOLDERID");
        Optional<EmcOrderChangeBean> orderOp = orderList.stream().max(Comparator.comparing(EmcOrderChangeBean::getOrderNo));
        EmcOrderChangeBean maxOrderNo = orderOp.get();

        List<EmcFolderChangeBean> folderlist = folderService.getDao().selectListByOneColumnValue(order.getFolderId(),"ID");

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

        EmcOrderChangeBean newOrder = new EmcOrderChangeBean();
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
        List<EmcOrderItemGroupChangeBean> copiedOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> newOrderItemGroupList = new ArrayList<>();

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

    private void copyOrderItemGroup(List<EmcOrderItemGroupChangeBean> copiedOrderItemGroupList, EmcOrderChangeBean copiedOrder, Long newOrderId, List<EmcOrderItemGroupChangeBean> newOrderItemGroupList) {
        List<EmcOrderItemGroupChangeBean> currentOrderItemGroupList = copiedOrderItemGroupList.stream().filter(e -> ObjectUtils.equals(copiedOrder.getId(), e.getOrderId())).collect(Collectors.toList());
        currentOrderItemGroupList.forEach(copiedOrderItemGroup -> {
            EmcOrderItemGroupChangeBean newOrderItemGroup = new EmcOrderItemGroupChangeBean();
            BeanUtils.copyProperties(copiedOrderItemGroup, newOrderItemGroup, EmcConstant.CREATED_COLUMNS);
            newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
            newOrderItemGroup.setOrderId(newOrderId);
            newOrderItemGroupList.add(newOrderItemGroup);
        });
    }

    public List<String> generateFolderNosByProjId(Long projId, String projectNo, int num) {
        List<EmcFolderChangeBean> schemeOfProjectList = folderService.getDao().selectListByOneColumnValue(projId, "PROJID");
        List<String> newFolderNoList = new ArrayList<>();
        String firstFolderNo = "";
        if (schemeOfProjectList.isEmpty()) {
            firstFolderNo = projectNo + "001";
            newFolderNoList.add(firstFolderNo);

        } else {

            String maxFolderNo = schemeOfProjectList.stream().max(Comparator.comparing(EmcFolderChangeBean::getFolderNo)).get().getFolderNo();
            firstFolderNo = maxFolderNo.substring(0, maxFolderNo.length() - 3) + String.format("%03d", Long.parseLong(maxFolderNo.substring(maxFolderNo.length() - 3)) + 1);
            newFolderNoList.add(firstFolderNo);
        }

        if (num > 1) {
            for (int i = 0; i < num - 1; i++) {
                String beforeFolderNo = newFolderNoList.get(i);
                String nextFolderNo = beforeFolderNo.substring(0, beforeFolderNo.length() - 3) + String.format("%03d", Long.parseLong(beforeFolderNo.substring(beforeFolderNo.length() - 3)) + 1);
                newFolderNoList.add(nextFolderNo);
            }
        }

        return newFolderNoList;
    }
}
