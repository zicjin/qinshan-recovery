package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderContainerDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public class EmcOrderContainerServiceImpl implements EmcOrderContainerService {

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderContainerDao orderContainerDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderContainerDao getDao() {
        return orderContainerDao;
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
        List<EmcOrderContainerBean> orderList = wrapper.parse(EmcOrderContainerBean.class);
        getDao().update(orderList, updateColNames);
    }

    @Override
    @Transactional
    public Long updateDataRecordVersionBySample(RestJsonWrapperBean wrapper) {

        Long oldBaseRecordVId = NumberUtils.parseLong(wrapper.getParamValue("oldBaseRecordVId"));
        if (oldBaseRecordVId == null || oldBaseRecordVId == 0L) {
            return 1L;
        }

        Long sampleId = NumberUtils.parseLong(wrapper.getParamValue("sampleId"));
        List<EmcOrderContainerBean> orderContainerList = this.getDao().selectListByOneColumnValue(sampleId, "sampleId");
        List<Long> folderIdList = orderContainerList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
        if (folderIdList.isEmpty()) {
            return 1L;
        }

        return 1L;
    }

    @Override
    public void createOrderContainer(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList) {
        if (orderTaskList.isEmpty()) {
            return;
        }

        orderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getAnalysisType).thenComparing(EmcOrderTaskBean::getTestId).reversed());

        Map<Long, Integer> orderContainerNoSuffixMap = new HashMap<>(orderList.size());

        // 按样品和容器信息进行分组
        Map<String, List<EmcOrderTaskBean>> taskMap = orderTaskList.stream().collect(Collectors.groupingBy(task -> String.join("$",
                task.getOrderId().toString(),
                ObjectUtils.isEmpty(task.getContainerCategoryId()) ? null : task.getContainerCategoryId().toString(),
                task.getContainerCategoryCode(),
                task.getContainerGroup(),
                task.getContainerCategoryName(),
                task.getFixative(),
                task.getStoreCondition()), LinkedHashMap::new, Collectors.toList()));

        String orgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        List<EmcOrderContainerBean> orderContainerList = new ArrayList<>(taskMap.size());
        Map<Long, List<Long>> orderContainerTaskIdMap = new HashMap<>(taskMap.size());

        AtomicLong orderIndex = new AtomicLong();
        taskMap.forEach((key, taskList) -> {
            EmcOrderTaskBean basicTask = taskList.get(0);
            EmcOrderContainerBean orderContainer = new EmcOrderContainerBean();
            orderContainer.setId(ApplicationContextHelper.getNextIdentity());
            orderContainer.setProjId(basicTask.getProjId());
            orderContainer.setFolderId(basicTask.getFolderId());
            orderContainer.setOrderId(basicTask.getOrderId());
            orderContainer.setContainerCategoryId(basicTask.getContainerCategoryId());
            orderContainer.setContainerCategoryCode(basicTask.getContainerCategoryCode());
            orderContainer.setContainerCategoryName(basicTask.getContainerCategoryName());
            orderContainer.setContainerGroup(basicTask.getContainerGroup());
            orderContainer.setFixative(basicTask.getFixative());
            orderContainer.setStoreCondition(basicTask.getStoreCondition());
            orderContainer.setMonitorTmplId(basicTask.getMonitorTmplId());
            orderContainer.setOrderCategory(basicTask.getOrderCategory());
            orderContainer.setQcCategoryId(basicTask.getQcCategoryId());
            orderContainer.setQcCategoryCode(basicTask.getQcCategoryCode());
            orderContainer.setQcName(basicTask.getQcName());
            orderContainer.setOrgId(orgId);
            orderContainer.setOrderNum(orderIndex.incrementAndGet());

            Long orderId = basicTask.getOrderId();
            Integer curSuffix = Optional.ofNullable(orderContainerNoSuffixMap.get(orderId)).orElse(1);
            orderList.stream().filter(order -> ObjectUtils.equals(order.getId(), orderId)).findFirst().ifPresent(order -> {
                orderContainer.setFrequency(order.getFrequency());
                orderContainer.setCycle(order.getCycle());
                orderContainer.setSampleNum(order.getSampleNum());
                orderContainer.setOrderNo(order.getOrderNo());
                orderContainer.setLabelNo(order.getLabelNo());
                orderContainer.setClientOrderName(order.getClientOrderName());
                orderContainer.setParentOrderNo(order.getParentOrderNo());
                String orderContainerNo = order.getOrderNo() + StringUtils.leftPad(curSuffix + "", 3);
                orderContainer.setOrderContainerNo(orderContainerNo);

                orderContainerList.add(orderContainer);
                orderContainerNoSuffixMap.put(orderId, curSuffix + 1);

                orderContainerTaskIdMap.put(orderContainer.getId(), taskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList()));
            });
        });

        if (!orderContainerList.isEmpty()) {
            getDao().insert(orderContainerList);
            orderContainerTaskIdMap.forEach((containerId, curTaskIdList) -> orderTaskList.stream().filter(task ->
                    curTaskIdList.contains(task.getId())).forEach(task -> task.setOrderContainerId(containerId)));
        }
    }

    @Override
    public void deleteContainer(List<EmcOrderBean> orderList) {
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderContainerBean> deleteFilterList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderContainerBean deleteFilter = new EmcOrderContainerBean();
            deleteFilter.setOrderId(orderId);
            deleteFilterList.add(deleteFilter);
        });

        getDao().deleteBy(deleteFilterList, "orderId");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteContainer(RestJsonWrapperBean wrapper) {
        List<EmcOrderContainerBean> containerList = wrapper.parse(EmcOrderContainerBean.class);
        List<Long> containerIdList = containerList.stream().map(EmcOrderContainerBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(containerIdList, "ORDERCONTAINERID");
        List<Long> orderTaskIdList=orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> resultList=orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        this.getDao().deleteBy(containerList, "ID");
        orderTaskService.getDao().deleteBy(orderTaskList, "ID");
        orderTaskResultService.getDao().deleteBy(resultList, "ID");
    }

    @Override
    public Page<EmcOrderContainerBean> selectOrderContainerList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return MybatisPageHelper.get(rowBounds, () -> getDao().selectOrderContainerList(parameter));
    }

    @Override
    public Page<EmcOrderContainerBean> selectOrderReceiveContainerList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return MybatisPageHelper.get(rowBounds, () -> getDao().selectOrderReceiveContainerList(parameter));
    }

    @Override
    public List<EmcOrderContainerBean> selectOrderContainerListByRunId(RestJsonWrapperBean wrapper) {
        String runId = wrapper.getParamValue("runId");
        if(!StringUtils.isEmpty(runId)) {
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("RUNID", runId).filter(MatchPattern.EQ));
            List<Long> orderContainerIdList = orderTaskList.stream().map(m->m.getOrderContainerId()).distinct().collect(Collectors.toList());
            List<EmcOrderContainerBean> orderContainerList = this.selectListByIds(orderContainerIdList);
            return orderContainerList;
        }
        return null;
    }

}
