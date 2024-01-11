package com.sunwayworld.emc.module.erp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcFolderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.erp.bean.ErpProjectCompareVO;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpCompareService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author zhengxy@sunwayworld.com 2023年10月24日
 */
@Repository
public class ErpCompareServiceImpl implements ErpCompareService {

    @Override
    public void compareProjectInfo(ErpProjectCompareVO compareVo) {
        // 暂时未实现
    }

    @Override
    public void compareProjScheme(ErpProjectCompareVO compareVo) {
        List<EmcProjSchemeBean> projSchemeList = compareVo.getProjSchemeList();
        List<EmcProjSchemeChangeBean> projSchemeChangeList = compareVo.getProjSchemeChangeList();

        // 使用监测模板+点位编号+erpGbId作为监测方案的唯一标识进行判定
        List<String> originalSchemeKeyList = projSchemeList.stream().map(scheme -> String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + "")).collect(Collectors.toList());
        List<String> changedSchemeKeyList = projSchemeChangeList.stream().map(scheme -> String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + "")).collect(Collectors.toList());

        // 新增的方案
        List<EmcProjSchemeChangeBean> newSchemeChangeList = projSchemeChangeList.stream().filter(scheme -> !originalSchemeKeyList.contains(String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + ""))).collect(Collectors.toList());
        // 删除的方案
        List<EmcProjSchemeBean> deleteSchemeList = projSchemeList.stream().filter(scheme -> !changedSchemeKeyList.contains(String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + ""))).collect(Collectors.toList());

        // 对于新增的方案和未改变的方案进行状态标记
        List<Long> newSchemeChangeIdList = newSchemeChangeList.stream().map(EmcProjSchemeChangeBean::getId).collect(Collectors.toList());
        projSchemeChangeList.forEach(scheme -> scheme.setChangeStatus(newSchemeChangeIdList.contains(scheme.getId()) ? ErpConstant.INSERT_STATUS : ErpConstant.NO_CHANGE_STATUS));

        // 将需要删除的方案添加到方案变更记录中，同时状态标记为删除
        Long projectId = compareVo.getProjectChange().getId();
        deleteSchemeList.forEach(deleteScheme -> {
            EmcProjSchemeChangeBean deleteSchemeChange = new EmcProjSchemeChangeBean();
            BeanUtils.copyProperties(deleteScheme, deleteSchemeChange, EmcConstant.CREATED_COLUMNS);
            deleteSchemeChange.setId(ApplicationContextHelper.getNextIdentity());
            deleteSchemeChange.setProjId(projectId);
            deleteSchemeChange.setChangeStatus(ErpConstant.DELETE_STATUS);
            deleteSchemeChange.setExt$Item("originalSchemeId", deleteScheme.getId() + "");
            projSchemeChangeList.add(deleteSchemeChange);
        });

        // 将改变后的方案变更记录赋值
        compareVo.setProjSchemeChangeList(projSchemeChangeList);
    }

    @Override
    public void compareProjOrder(ErpProjectCompareVO compareVo) {
        List<EmcProjSchemeChangeBean> projSchemeChangeList = compareVo.getProjSchemeChangeList();
        List<EmcProjOrderChangeBean> projOrderChangeList = compareVo.getProjOrderChangeList();
        // 对于需要新增的方案，只需要改变一下方案下的测试状态标记
        List<Long> insertSchemeChangeIdList = projSchemeChangeList.stream().filter(scheme -> ErpConstant.INSERT_STATUS.equals(scheme.getChangeStatus())).map(EmcProjSchemeChangeBean::getId).collect(Collectors.toList());
        projOrderChangeList.stream().filter(order -> insertSchemeChangeIdList.contains(order.getProjSchemeId())).forEach(order -> order.setChangeStatus(ErpConstant.INSERT_STATUS));

        // 对于未改变的方案，需要比对方案下的测试是否有新增或删除
        List<EmcProjSchemeBean> projSchemeList = compareVo.getProjSchemeList();
        List<EmcProjOrderBean> projOrderList = compareVo.getProjOrderList();
        List<EmcProjSchemeChangeBean> noChangeSchemeList = projSchemeChangeList.stream().filter(scheme -> ErpConstant.NO_CHANGE_STATUS.equals(scheme.getChangeStatus())).collect(Collectors.toList());
        noChangeSchemeList.forEach(schemeChange -> {
            List<EmcProjOrderChangeBean> curProjOrderChangeList = projOrderChangeList.stream().filter(order -> ObjectUtils.equals(schemeChange.getId(), order.getProjSchemeId())).collect(Collectors.toList());
            String schemeKey = String.join("$", schemeChange.getMonitorTmplId() + "", schemeChange.getFolderNo(), schemeChange.getErpGbId() + "");

            EmcProjSchemeBean originalProjScheme = projSchemeList.stream().filter(scheme -> schemeKey.equals(String.join("$", scheme.getMonitorTmplId() + "", scheme.getFolderNo(), scheme.getErpGbId() + ""))).findFirst().orElseGet(EmcProjSchemeBean::new);
            List<EmcProjOrderBean> originalProjOrderList = projOrderList.stream().filter(order -> ObjectUtils.equals(originalProjScheme.getId(), order.getProjSchemeId())).collect(Collectors.toList());

            // 比对 curProjOrderChangeList 和 originalProjOrderList，找出新增和删除的
            List<String> changedProjOrderKeyList = curProjOrderChangeList.stream().map(order -> String.join("$", order.getTestId() + "", order.getItemGroupId() + "")).collect(Collectors.toList());
            List<String> originalProjOrderKeyList = originalProjOrderList.stream().map(order -> String.join("$", order.getTestId() + "", order.getItemGroupId() + "")).collect(Collectors.toList());

            // 新增的测试
            List<EmcProjOrderChangeBean> newProjOrderChangeList = curProjOrderChangeList.stream().filter(order -> !originalProjOrderKeyList.contains(String.join("$", order.getTestId() + "", order.getItemGroupId() + ""))).collect(Collectors.toList());

            // 删除的测试
            List<EmcProjOrderBean> deleteProjOrderList = originalProjOrderList.stream().filter(order -> !changedProjOrderKeyList.contains(String.join("$", order.getTestId() + "", order.getItemGroupId() + ""))).collect(Collectors.toList());

            // 对新增的和未改变的测试进行状态标记
            List<Long> newProjOrderChangeIdList = newProjOrderChangeList.stream().map(EmcProjOrderChangeBean::getId).collect(Collectors.toList());
            projOrderChangeList.forEach(projOrder -> projOrder.setChangeStatus(newProjOrderChangeIdList.contains(projOrder.getId()) ? ErpConstant.INSERT_STATUS : ErpConstant.NO_CHANGE_STATUS));

            // 将需要删除的测试到测试变更记录中，同时标记状态为删除
            deleteProjOrderList.forEach(dpo -> {
                EmcProjOrderChangeBean deleteOrderChange = new EmcProjOrderChangeBean();
                BeanUtils.copyProperties(dpo, deleteOrderChange, EmcConstant.CREATED_COLUMNS);
                deleteOrderChange.setId(ApplicationContextHelper.getNextIdentity());
                deleteOrderChange.setProjSchemeId(schemeChange.getId());
                deleteOrderChange.setChangeStatus(ErpConstant.DELETE_STATUS);
                projOrderChangeList.add(deleteOrderChange);
            });
        });

        // 对于需要删除的方案，需要把原方案下测试添加上，同时标记状态为删除
        List<EmcProjSchemeChangeBean> deleteSchemeChangeList = projSchemeChangeList.stream().filter(scheme -> ErpConstant.DELETE_STATUS.equals(scheme.getChangeStatus())).collect(Collectors.toList());
        deleteSchemeChangeList.forEach(dsc -> {
            long originalSchemeId = NumberUtils.parseLong(dsc.getExt$Item("originalSchemeId"));
            List<EmcProjOrderBean> deleteProjOrderList = projOrderList.stream().filter(projOrder -> ObjectUtils.equals(originalSchemeId, projOrder.getProjSchemeId())).collect(Collectors.toList());
            deleteProjOrderList.forEach(dpo -> {
                EmcProjOrderChangeBean deleteOrderChange = new EmcProjOrderChangeBean();
                BeanUtils.copyProperties(dpo, deleteOrderChange, EmcConstant.CREATED_COLUMNS);
                deleteOrderChange.setId(ApplicationContextHelper.getNextIdentity());
                deleteOrderChange.setProjSchemeId(dsc.getId());
                deleteOrderChange.setChangeStatus(ErpConstant.DELETE_STATUS);
                projOrderChangeList.add(deleteOrderChange);
            });
        });

        // 将改变后的样品测试变更记录赋值
        compareVo.setProjOrderChangeList(projOrderChangeList);
    }

    @Override
    public void compareOrder(ErpProjectCompareVO compareVo) {
        List<EmcOrderBean> orderList = compareVo.getOrderList();
        List<EmcOrderChangeBean> orderChangeList = compareVo.getOrderChangeList();

        // 使用样品类型+样品编号+erpGbId作为样品的唯一标识进行判定
        List<String> originalOrderKeyList = orderList.stream().map(order -> String.join("$", order.getOrderCategory(), order.getOrderNo(), order.getErpGbId() + "")).collect(Collectors.toList());
        List<String> changeOrderKeyList = orderChangeList.stream().map(order -> String.join("$", order.getOrderCategory(), order.getOrderNo(), order.getErpGbId() + "")).collect(Collectors.toList());

        // 新增的样品
        List<EmcOrderChangeBean> newOrderChangeList = orderChangeList.stream().filter(oc -> !originalOrderKeyList.contains(String.join("$", oc.getOrderCategory(), oc.getOrderNo(), oc.getErpGbId() + ""))).collect(Collectors.toList());
        // 删除的样品
        List<EmcOrderBean> deleteOrderList = orderList.stream().filter(order -> !changeOrderKeyList.contains(String.join("$", order.getOrderCategory(), order.getOrderNo(), order.getErpGbId() + ""))).collect(Collectors.toList());

        // 对新增的样品和未改变的样品进行状态标记
        List<Long> newOrderChangeIdList = newOrderChangeList.stream().map(EmcOrderChangeBean::getId).collect(Collectors.toList());
        orderChangeList.forEach(orderChange -> orderChange.setChangeStatus(newOrderChangeIdList.contains(orderChange.getId()) ? ErpConstant.INSERT_STATUS : ErpConstant.NO_CHANGE_STATUS));

        // 将需要删除的样品添加到样品变更记录中，同时状态标记为删除
        Long projectId = compareVo.getProjectChange().getId();
        List<EmcFolderBean> folderList = compareVo.getFolderList();
        List<EmcFolderChangeBean> folderChangeList = compareVo.getFolderChangeList();

        deleteOrderList.forEach(deleteOrder -> {
            EmcFolderBean curFolder = folderList.stream().filter(folder -> ObjectUtils.equals(folder.getId(), deleteOrder.getFolderId())).findFirst().orElseGet(EmcFolderBean::new);
            EmcFolderChangeBean deleteFolder = new EmcFolderChangeBean();
            BeanUtils.copyProperties(curFolder, deleteFolder, EmcConstant.CREATED_COLUMNS);
            deleteFolder.setId(ApplicationContextHelper.getNextIdentity());
            deleteFolder.setProjId(projectId);
            deleteFolder.setFolderNo(ApplicationContextHelper.getNextSequence("T_EMC_FOLDER"));
            deleteFolder.setChangeStatus(ErpConstant.DELETE_STATUS);
            folderChangeList.add(deleteFolder);

            EmcOrderChangeBean deleteOrderChange = new EmcOrderChangeBean();
            BeanUtils.copyProperties(deleteOrder, deleteOrderChange, EmcConstant.CREATED_COLUMNS);
            deleteOrderChange.setId(ApplicationContextHelper.getNextIdentity());
            deleteOrderChange.setProjId(projectId);
            deleteOrderChange.setFolderId(deleteFolder.getId());
            deleteOrderChange.setChangeStatus(ErpConstant.DELETE_STATUS);
            deleteOrderChange.setExt$Item("originalOrderId", deleteOrder.getId() + "");
            orderChangeList.add(deleteOrderChange);
        });

        // 将改变后的样品变更记录赋值
        compareVo.setFolderChangeList(folderChangeList);
        compareVo.setOrderChangeList(orderChangeList);
    }

    @Override
    public void compareOrderItemGroup(ErpProjectCompareVO compareVo) {
        List<EmcOrderChangeBean> orderChangeList = compareVo.getOrderChangeList();
        List<EmcOrderItemGroupChangeBean> orderItemGroupChangeList = compareVo.getOrderItemGroupChangeList();
        // 对于需要新增的样品，只需要改变一下样品分析项组状态标记
        List<EmcOrderChangeBean> insertOrderChangeList = orderChangeList.stream().filter(oc -> StringUtils.equals(oc.getChangeStatus(), ErpConstant.INSERT_STATUS)).collect(Collectors.toList());
        List<Long> insertOrderChangeIdList = insertOrderChangeList.stream().map(EmcOrderChangeBean::getId).collect(Collectors.toList());
        orderItemGroupChangeList.stream().filter(itemGroup -> insertOrderChangeIdList.contains(itemGroup.getOrderId())).forEach(itemGroup -> itemGroup.setChangeStatus(ErpConstant.INSERT_STATUS));

        // 对于未改变的样品，需要比对样品下的分析项组是否有新增或删除
        List<EmcOrderItemGroupBean> orderItemGroupList = compareVo.getOrderItemGroupList();
        List<EmcOrderChangeBean> noChangeOrderList = orderChangeList.stream().filter(oc -> StringUtils.equals(oc.getChangeStatus(), ErpConstant.NO_CHANGE_STATUS)).collect(Collectors.toList());
        List<EmcOrderBean> orderList = compareVo.getOrderList();
        noChangeOrderList.forEach(orderChange -> {
            List<EmcOrderItemGroupChangeBean> curOigcList = orderItemGroupChangeList.stream().filter(oigc -> ObjectUtils.equals(orderChange.getId(), oigc.getOrderId())).collect(Collectors.toList());
            String orderKey = String.join("$", orderChange.getOrderCategory(), orderChange.getOrderNo(), orderChange.getErpGbId() + "");
            EmcOrderBean originalOrder = orderList.stream().filter(order -> StringUtils.equals(String.join("$", order.getOrderCategory(), order.getOrderNo(), order.getErpGbId() + ""), orderKey)).findFirst().orElseGet(EmcOrderBean::new);
            List<EmcOrderItemGroupBean> originalOigList = orderItemGroupList.stream().filter(oig -> ObjectUtils.equals(oig.getOrderId(), originalOrder.getId())).collect(Collectors.toList());

            // 比对 curOigcList 和 originalOigList，找出新增和删除的
            List<String> changedOigKeyList = curOigcList.stream().map(oigc -> String.join("$", oigc.getTestMethodId() + "", oigc.getItemGroupId() + "")).collect(Collectors.toList());
            List<String> originalOigKeyList = originalOigList.stream().map(oigc -> String.join("$", oigc.getTestMethodId() + "", oigc.getItemGroupId() + "")).collect(Collectors.toList());

            // 新增的样品分析项组
            List<EmcOrderItemGroupChangeBean> newOigcList = curOigcList.stream().filter(oigc -> !originalOigKeyList.contains(String.join("$", oigc.getTestMethodId() + "", oigc.getItemGroupId() + ""))).collect(Collectors.toList());

            // 删除的分析项组
            List<EmcOrderItemGroupBean> deleteOigList = originalOigList.stream().filter(oigc -> !changedOigKeyList.contains(String.join("$", oigc.getTestMethodId() + "", oigc.getItemGroupId() + ""))).collect(Collectors.toList());

            // 对新增的和未改变的样品分析项组进行状态标记
            List<Long> newOigcIdList = newOigcList.stream().map(EmcOrderItemGroupChangeBean::getId).collect(Collectors.toList());
            orderItemGroupChangeList.forEach(oigc -> oigc.setChangeStatus(newOigcIdList.contains(oigc.getId()) ? ErpConstant.INSERT_STATUS : ErpConstant.NO_CHANGE_STATUS));

            // 将需要删除的分析项组添加到分析项组变更记录中，同时标记状态为删除
            deleteOigList.forEach(oig -> {
                EmcOrderItemGroupChangeBean deleteOigc = new EmcOrderItemGroupChangeBean();
                BeanUtils.copyProperties(oig, deleteOigc, EmcConstant.CREATED_COLUMNS);
                deleteOigc.setId(ApplicationContextHelper.getNextIdentity());
                deleteOigc.setOrderId(orderChange.getId());
                deleteOigc.setChangeStatus(ErpConstant.DELETE_STATUS);
                orderItemGroupChangeList.add(deleteOigc);
            });
        });

        // 对于需要删除的样品，需要把分析项组添加上，同时标记状态为删除
        List<EmcOrderChangeBean> deleteOrderChangeList = orderChangeList.stream().filter(oc -> StringUtils.equals(oc.getChangeStatus(), ErpConstant.DELETE_STATUS)).collect(Collectors.toList());
        List<EmcOrderItemGroupChangeBean> deleteOigcList = new ArrayList<>();
        deleteOrderChangeList.forEach(doc -> {
            long originalOrderId = NumberUtils.parseLong(doc.getExt$Item("originalOrderId"));
            List<EmcOrderItemGroupBean> originalOigList = orderItemGroupList.stream().filter(itemGroup -> ObjectUtils.equals(originalOrderId, itemGroup.getOrderId())).collect(Collectors.toList());
            originalOigList.forEach(oig -> {
                EmcOrderItemGroupChangeBean deleteOigc = new EmcOrderItemGroupChangeBean();
                BeanUtils.copyProperties(oig, deleteOigc, EmcConstant.CREATED_COLUMNS);
                deleteOigc.setId(ApplicationContextHelper.getNextIdentity());
                deleteOigc.setOrderId(doc.getId());
                deleteOigc.setExt$Item("originalId", oig.getId() + "");
                deleteOigc.setChangeStatus(ErpConstant.DELETE_STATUS);
                deleteOigcList.add(deleteOigc);
            });
        });
        orderItemGroupChangeList.addAll(deleteOigcList);

        // 将改变后的样品分析项变更记录赋值
        compareVo.setOrderItemGroupChangeList(orderItemGroupChangeList);
    }
}
