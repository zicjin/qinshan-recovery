package com.sunwayworld.emc.module.mdm.crm.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sunwayworld.emc.module.mdm.crm.service.*;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.*;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmFolderBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityExpensesBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityFoldersBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdersBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdtaskBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityPaymentBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityRequestDTO;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

/**
 * @author zhengxy@sunwayworld.com 2021年08月31日
 */
@Repository
public class CrmOpportunityOpenServiceImpl implements CrmOpportunityOpenService {

    private static final Logger logger = LogManager.getLogger(CrmOpportunityOpenServiceImpl.class);

    private static final String INSERT_OPERATE_TYPE = "insert";
    private static final String UPDATE_OPERATE_TYPE = "update";
    private static final String DELETE_OPERATE_TYPE = "delete";

    private static final String DEFAULT_DOMAIN_ID = "637086691053440";

    @Autowired
    private CrmOpportunityMainService opportunityMainService;

    @Autowired
    private CrmOpportunityContractService opportunityContractService;

    @Autowired
    private CrmOpportunityExpensesService opportunityExpensesService;

    @Autowired
    private CrmOpportunityFoldersService opportunityFoldersService;

    @Autowired
    private CrmOpportunityOrdersService opportunityOrdersService;

    @Autowired
    private CrmOpportunityOrdtaskService opportunityOrdtaskService;

    @Autowired
    private CrmOpportunityPaymentService opportunityPaymentService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private CoreFileManager fileManager;

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcReportService reportService;

    @Autowired
    private CrmFolderService crmFolderService;

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityMain(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CrmOpportunityMainBean> opportunityMainRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CrmOpportunityMainBean>>() {});
        logger.info("[Crm-Opportunity-Main]=======================");
        logger.info(JSON.toJSONString(opportunityMainRequest));
        String operateType = opportunityMainRequest.getOperType();
        List<CrmOpportunityMainBean> dataList = opportunityMainRequest.getData();
        dataList.forEach(op -> {
            op.setCustomerName(op.getExt$Item("customername"));
            op.setBidderName(op.getExt$Item("biddername"));
        });

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
            case UPDATE_OPERATE_TYPE:
                // 如果首次插入推送失败了，数据就无法更新了，更新的时候需要判断是插入还是更新
                List<String> idList = dataList.stream().map(CrmOpportunityMainBean::getId).collect(Collectors.toList());
                List<CrmOpportunityMainBean> existedList = opportunityMainService.getDao().selectListByOneColumnValues(idList, "id", Collections.singletonList("id"));
                List<String> existedIdList = existedList.stream().map(CrmOpportunityMainBean::getId).collect(Collectors.toList());
                List<CrmOpportunityMainBean> insertList = dataList.stream().filter(data -> !existedIdList.contains(data.getId())).collect(Collectors.toList());
                List<CrmOpportunityMainBean> updateList = dataList.stream().filter(data -> existedIdList.contains(data.getId())).collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    opportunityMainService.getDao().insert(insertList);
                }
                if (!updateList.isEmpty()) {
                    opportunityMainService.getDao().update(updateList);
                }
                break;
            case DELETE_OPERATE_TYPE:
                opportunityMainService.getDao().deleteBy(dataList, "id");
                break;
                default:
                    break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityContract(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CrmOpportunityContractBean> opportunityContractRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CrmOpportunityContractBean>>() {});
        logger.info("[Crm-Opportunity-Contract]=======================");
        logger.info(JSON.toJSONString(opportunityContractRequest));
        String operateType = opportunityContractRequest.getOperType();
        List<CrmOpportunityContractBean> dataList = opportunityContractRequest.getData();
        dataList.forEach(contract -> contract.setFinancialFlag(contract.getExt$Item("financialFlag")));

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
            case UPDATE_OPERATE_TYPE:
                // 如果首次插入推送失败了，数据就无法更新了，更新的时候需要判断是插入还是更新
                List<String> idList = dataList.stream().map(CrmOpportunityContractBean::getId).collect(Collectors.toList());
                List<CrmOpportunityContractBean> existedList = opportunityContractService.getDao().selectListByOneColumnValues(idList, "id", Collections.singletonList("id"));
                List<String> existedIdList = existedList.stream().map(CrmOpportunityContractBean::getId).collect(Collectors.toList());
                List<CrmOpportunityContractBean> insertList = dataList.stream().filter(data -> !existedIdList.contains(data.getId())).collect(Collectors.toList());
                List<CrmOpportunityContractBean> updateList = dataList.stream().filter(data -> existedIdList.contains(data.getId())).collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    opportunityContractService.getDao().insert(insertList);
                }
                if (!updateList.isEmpty()) {
                    opportunityContractService.getDao().update(updateList);
                }
                break;
            case DELETE_OPERATE_TYPE:
                opportunityContractService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityExpenses(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CrmOpportunityExpensesBean> opportunityExpensesRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CrmOpportunityExpensesBean>>() {});
        logger.info("[Crm-Opportunity-Expenses]=======================");
        logger.info(JSON.toJSONString(opportunityExpensesRequest));
        String operateType = opportunityExpensesRequest.getOperType();
        List<CrmOpportunityExpensesBean> dataList = opportunityExpensesRequest.getData();

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
            case UPDATE_OPERATE_TYPE:
                // 如果首次插入推送失败了，数据就无法更新了，更新的时候需要判断是插入还是更新
                List<String> idList = dataList.stream().map(CrmOpportunityExpensesBean::getId).collect(Collectors.toList());
                List<CrmOpportunityExpensesBean> existedList = opportunityExpensesService.getDao().selectListByOneColumnValues(idList, "id", Collections.singletonList("id"));
                List<String> existedIdList = existedList.stream().map(CrmOpportunityExpensesBean::getId).collect(Collectors.toList());
                List<CrmOpportunityExpensesBean> insertList = dataList.stream().filter(data -> !existedIdList.contains(data.getId())).collect(Collectors.toList());
                List<CrmOpportunityExpensesBean> updateList = dataList.stream().filter(data -> existedIdList.contains(data.getId())).collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    opportunityExpensesService.getDao().insert(insertList);
                }
                if (!updateList.isEmpty()) {
                    opportunityExpensesService.getDao().update(updateList);
                }
                break;
            case DELETE_OPERATE_TYPE:
                opportunityExpensesService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityPayment(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CrmOpportunityPaymentBean> opportunityPaymentRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CrmOpportunityPaymentBean>>() {});
        logger.info("[Crm-Opportunity-Payment]=======================");
        logger.info(JSON.toJSONString(opportunityPaymentRequest));
        String operateType = opportunityPaymentRequest.getOperType();
        List<CrmOpportunityPaymentBean> dataList = opportunityPaymentRequest.getData();

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
            case UPDATE_OPERATE_TYPE:
                // 如果首次插入推送失败了，数据就无法更新了，更新的时候需要判断是插入还是更新
                List<String> idList = dataList.stream().map(CrmOpportunityPaymentBean::getId).collect(Collectors.toList());
                List<CrmOpportunityPaymentBean> existedList = opportunityPaymentService.getDao().selectListByOneColumnValues(idList, "id", Collections.singletonList("id"));
                List<String> existedIdList = existedList.stream().map(CrmOpportunityPaymentBean::getId).collect(Collectors.toList());
                List<CrmOpportunityPaymentBean> insertList = dataList.stream().filter(data -> !existedIdList.contains(data.getId())).collect(Collectors.toList());
                List<CrmOpportunityPaymentBean> updateList = dataList.stream().filter(data -> existedIdList.contains(data.getId())).collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    opportunityPaymentService.getDao().insert(insertList);
                }
                if (!updateList.isEmpty()) {
                    opportunityPaymentService.getDao().update(updateList);
                }
                break;
            case DELETE_OPERATE_TYPE:
                opportunityPaymentService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityFolders(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CrmOpportunityFoldersBean> opportunityFoldersRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CrmOpportunityFoldersBean>>() {});
        logger.info("[Crm-Opportunity-Folders]=======================");
        logger.info(JSON.toJSONString(opportunityFoldersRequest));
        String operateType = opportunityFoldersRequest.getOperType();
        List<CrmOpportunityFoldersBean> dataList = opportunityFoldersRequest.getData();

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
            case UPDATE_OPERATE_TYPE:
                // 如果首次插入推送失败了，数据就无法更新了，更新的时候需要判断是插入还是更新
                List<String> idList = dataList.stream().map(CrmOpportunityFoldersBean::getId).collect(Collectors.toList());
                List<CrmOpportunityFoldersBean> existedList = opportunityFoldersService.getDao().selectListByOneColumnValues(idList, "id", Collections.singletonList("id"));
                List<String> existedIdList = existedList.stream().map(CrmOpportunityFoldersBean::getId).collect(Collectors.toList());
                List<CrmOpportunityFoldersBean> insertList = dataList.stream().filter(data -> !existedIdList.contains(data.getId())).collect(Collectors.toList());
                List<CrmOpportunityFoldersBean> updateList = dataList.stream().filter(data -> existedIdList.contains(data.getId())).collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    opportunityFoldersService.getDao().insert(insertList);
                }
                if (!updateList.isEmpty()) {
                    opportunityFoldersService.getDao().update(updateList);
                }
                break;
            case DELETE_OPERATE_TYPE:
                opportunityFoldersService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityOrders(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CrmOpportunityOrdersBean> opportunityOrderRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CrmOpportunityOrdersBean>>() {});
        logger.info("[Crm-Opportunity-Orders]=======================");
        logger.info(JSON.toJSONString(opportunityOrderRequest));
        String operateType = opportunityOrderRequest.getOperType();
        List<CrmOpportunityOrdersBean> dataList = opportunityOrderRequest.getData();

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
            case UPDATE_OPERATE_TYPE:
                // 如果首次插入推送失败了，数据就无法更新了，更新的时候需要判断是插入还是更新
                List<String> idList = dataList.stream().map(CrmOpportunityOrdersBean::getId).collect(Collectors.toList());
                List<CrmOpportunityOrdersBean> existedList = opportunityOrdersService.getDao().selectListByOneColumnValues(idList, "id", Collections.singletonList("id"));
                List<String> existedIdList = existedList.stream().map(CrmOpportunityOrdersBean::getId).collect(Collectors.toList());
                List<CrmOpportunityOrdersBean> insertList = dataList.stream().filter(data -> !existedIdList.contains(data.getId())).collect(Collectors.toList());
                List<CrmOpportunityOrdersBean> updateList = dataList.stream().filter(data -> existedIdList.contains(data.getId())).collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    opportunityOrdersService.getDao().insert(insertList);
                }
                if (!updateList.isEmpty()) {
                    opportunityOrdersService.getDao().update(updateList);
                }
                break;
            case DELETE_OPERATE_TYPE:
                opportunityOrdersService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityOrdtask(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CrmOpportunityOrdtaskBean> opportunityOrdtaskRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CrmOpportunityOrdtaskBean>>() {});
        logger.info("[Crm-Opportunity-Ordtask]=======================");
        logger.info(JSON.toJSONString(opportunityOrdtaskRequest));
        String operateType = opportunityOrdtaskRequest.getOperType();
        List<CrmOpportunityOrdtaskBean> dataList = opportunityOrdtaskRequest.getData();

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
            case UPDATE_OPERATE_TYPE:
                // 如果首次插入推送失败了，数据就无法更新了，更新的时候需要判断是插入还是更新
                List<String> idList = dataList.stream().map(CrmOpportunityOrdtaskBean::getId).collect(Collectors.toList());
                List<CrmOpportunityOrdtaskBean> existedList = opportunityOrdtaskService.getDao().selectListByOneColumnValues(idList, "id", Collections.singletonList("id"));
                List<String> existedIdList = existedList.stream().map(CrmOpportunityOrdtaskBean::getId).collect(Collectors.toList());
                List<CrmOpportunityOrdtaskBean> insertList = dataList.stream().filter(data -> !existedIdList.contains(data.getId())).collect(Collectors.toList());
                List<CrmOpportunityOrdtaskBean> updateList = dataList.stream().filter(data -> existedIdList.contains(data.getId())).collect(Collectors.toList());
                if (!insertList.isEmpty()) {
                    opportunityOrdtaskService.getDao().insert(insertList);
                }
                if (!updateList.isEmpty()) {
                    opportunityOrdtaskService.getDao().update(updateList);
                }
                break;
            case DELETE_OPERATE_TYPE:
                opportunityOrdtaskService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public CrmOpportunityResponseDTO receiveOpportunityFiles(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        logger.info("[Crm-Opportunity-Files]=======================");
        logger.info(requestJson);
        JSONObject requestData = JSONObject.parseObject(requestJson);
        String operateType = requestData.getString("operType");
        JSONArray data = requestData.getJSONArray("data");
        List<CoreFileBean> dataList = parseList(data, CoreFileBean.class);

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
                insertFile(dataList);
                break;
            case UPDATE_OPERATE_TYPE:
                coreFileService.getDao().update(dataList);
                break;
            case DELETE_OPERATE_TYPE:
                coreFileService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    @Transactional
    public void syncProjectInfo(List<EmcProjectBean> projectList, String operateType) {
        String crmServerUrl = ApplicationContextHelper.getConstantValue("CRM_SERVER_URL");
        String syncInterface = ApplicationContextHelper.getConstantValue("CRM_SYNC_PROJECT_INFO_INTERFACE");

        if (StringUtils.isEmpty(crmServerUrl) || StringUtils.isEmpty(syncInterface)) {
            return;
        }

        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValues(projectIdList, "projId", Arrays.asList("id", "projId", "completeDate"));

        List<String> contractNoList = projectList.stream().filter(project -> !StringUtils.isEmpty(project.getExt$Item("contractNo"))).map(project -> project.getExt$Item("contractNo")).collect(Collectors.toList());
        List<CrmOpportunityContractBean> contractList = opportunityContractService.getDao().selectListByOneColumnValues(contractNoList, "contractNo", Arrays.asList("id", "contractNo"));
        String operateTypeMatch = Optional.ofNullable(operateType).orElse(INSERT_OPERATE_TYPE);
        List<CrmFolderBean> folderList = buildCrmFolderList(projectList, contractList, reportList,operateTypeMatch);

        syncProjectInfo(crmServerUrl, syncInterface, projectList, folderList, operateType);
    }

    @Override
    public void syncProjectInfo(List<EmcProjectBean> projectList) {
        String crmServerUrl = ApplicationContextHelper.getConstantValue("CRM_SERVER_URL");
        String syncInterface = ApplicationContextHelper.getConstantValue("CRM_SYNC_PROJECT_INFO_INTERFACE");

        if (StringUtils.isEmpty(crmServerUrl) || StringUtils.isEmpty(syncInterface)) {
            return;
        }

        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValues(projectIdList, "projId", Arrays.asList("id", "projId", "completeDate"));

        List<String> contractNoList = projectList.stream().filter(project -> !StringUtils.isEmpty(project.getExt$Item("contractNo"))).map(project -> project.getExt$Item("contractNo")).collect(Collectors.toList());
        List<CrmOpportunityContractBean> contractList = opportunityContractService.getDao().selectListByOneColumnValues(contractNoList, "contractNo", Arrays.asList("id", "contractNo"));

        // 根据 crmSyncState 判断该项目之前是否同步过，如果同步过，则直接更新，否则直接新增
        List<EmcProjectBean> insertProjectList = projectList.stream().filter(project -> !StringUtils.equals(Constant.YES, project.getExt$Item("crmSyncState"))).collect(Collectors.toList());
        List<EmcProjectBean> updateProjectList = projectList.stream().filter(project -> StringUtils.equals(Constant.YES, project.getExt$Item("crmSyncState"))).collect(Collectors.toList());

        List<CrmFolderBean> insertFolderList = buildCrmFolderList(insertProjectList, contractList, reportList,INSERT_OPERATE_TYPE);
        List<CrmFolderBean> updateFolderList = buildCrmFolderList(updateProjectList, contractList, reportList,UPDATE_OPERATE_TYPE);

        syncProjectInfo(crmServerUrl, syncInterface, insertProjectList, insertFolderList, INSERT_OPERATE_TYPE);
        syncProjectInfo(crmServerUrl, syncInterface, updateProjectList, updateFolderList, UPDATE_OPERATE_TYPE);
    }

    @Override
    public void downloadAndSaveFile(List<CoreFileBean> fileList) {
        insertFile(fileList);
    }

    /**
     * 保存文件信息
     * @param fileList 文件列表
     */
    private void insertFile(List<CoreFileBean> fileList) {
        String crmServerUrl = ApplicationContextHelper.getConstantValue("CRM_SERVER_URL");
        fileList.forEach(file -> {
            file.setId(ApplicationContextHelper.getNextIdentity());
            if (!StringUtils.isEmpty(crmServerUrl)) {
                File downloadFile = downloadFile(file, crmServerUrl);
                fileManager.upload(CoreFileUtils.toFilePath(file), downloadFile);
            }
        });

        coreFileService.getDao().insert(fileList);
    }

    /**
     * 解析 JSONArray 数据为 List<T>
     * @param array JSONArray
     * @param clazz 待转换的 class
     * @param <T> 待转换的类型
     * @return List
     */
    private <T> List<T> parseList(JSONArray array, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        IntStream.range(0, array.size()).forEach(index -> {
            JSONObject jsonObject = array.getJSONObject(index);
            T bean = ClassUtils.newInstance(clazz);
            BeanUtils.copyProperties(jsonObject, bean);
            list.add(bean);
        });

        return list;
    }

    /**
     * 从CRM系统下载文件
     * @param file 文件信息
     * @param crmServerUrl CRM系统地址
     * @return File
     */
    private File downloadFile(CoreFileBean file, String crmServerUrl) {
        String downloadUrl = fileManager.getDownloadUrl(file);
        File downloadFile = FileUtils.createTempFile();

        HttpClientManager.getInstance().download(crmServerUrl + downloadUrl, downloadFile);

        return downloadFile;
    }

    /**
     * 构建同步数据
     * @param projectList 项目数据
     * @param contractList 合同数据
     * @param reportList 报告数据
     * @return List
     */
    private List<CrmFolderBean> buildCrmFolderList(List<EmcProjectBean> projectList, List<CrmOpportunityContractBean> contractList, List<EmcReportBean> reportList, String operateType) {
        List<CrmFolderBean> folderList = new ArrayList<>(projectList.size());

        projectList.forEach(project -> {
            CrmFolderBean folder = new CrmFolderBean();
            //如果是更新，不需要随机id
            if(!StringUtils.equals(operateType,UPDATE_OPERATE_TYPE)){
                folder.setId(StringUtils.randomUUID().toLowerCase());
            }else{
                List<CrmFolderBean> crmFolderBeans = crmFolderService.selectListByFilter(SearchFilter.instance().match("projId", project.getId().toString()).filter(MatchPattern.EQ));
                if(!crmFolderBeans.isEmpty()){
                    folder.setId(crmFolderBeans.get(0).getId());
                }
            }
            folder.setProjId(project.getId() + "");
            folder.setProjNo(project.getProjNo());
            folder.setProjAmount(NumberUtils.parseDouble(Optional.ofNullable(project.getExt$Item("projectAmount")).orElse("0")));

            // CRM的固定值
            folder.setDomainId(DEFAULT_DOMAIN_ID);
            contractList.stream().filter(contract -> StringUtils.equals(contract.getContractNo(), project.getExt$Item("contractNo"))).findFirst().ifPresent(contract -> {
                folder.setContractId(contract.getId());
            });

            // 获取报告上的完成日期和交付日期和报告编号
            reportList.stream().filter(report -> ObjectUtils.equals(project.getId(), report.getProjId())).findFirst().ifPresent(report -> {
                folder.setFinishDate(DateTimeUtils.parseLocalDate(report.getExt$Item("completeDate")));
/*                folder.setReportNo(report.getExt$Item("reportNo"));
*/            });
            if(reportList.isEmpty()){
                folder.setFinishDate(DateTimeUtils.parseLocalDate(project.getExt$Item("completedate")));
            }

            folderList.add(folder);
        });

        return folderList;
    }

    /**
     * 同步项目数据
     * @param crmServerUrl CRM系统地址
     * @param syncInterface 同步接口
     * @param projectList 同步的项目列表
     * @param folderList 同步的数据
     * @param operateType 操作类型
     */
    private void syncProjectInfo(String crmServerUrl, String syncInterface, List<EmcProjectBean> projectList, List<CrmFolderBean> folderList, String operateType) {
        if (folderList.isEmpty()) {
            return;
        }

        CrmOpportunityRequestDTO<CrmFolderBean> requestDTO = new CrmOpportunityRequestDTO<>();
        requestDTO.setOperType(Optional.ofNullable(operateType).orElse(INSERT_OPERATE_TYPE));
        requestDTO.setData(folderList);

        String url = crmServerUrl + syncInterface;
        String requestJson = JSON.toJSONString(requestDTO);
        logger.info("[Sync-Project-Info]=======================");
        logger.info(url);
        logger.info(requestJson);

        String result = HttpClientManager.getInstance().sendHttpPost(url, requestJson);
        logger.info(result);

        try {
            // 如果接口返回的数据是错误的，直接解析则会抛出异常
            CrmOpportunityResponseDTO responseDTO = JSON.parseObject(result, CrmOpportunityResponseDTO.class);

            String crmSyncState = (responseDTO.getCode() == 0) ? Constant.YES : Constant.NO;
            projectList.forEach(project -> {
                //如果是项目之前的同步状态不是yes，随便更新yes,no状态；//如果是项目之前的同步状态是yes，不更新状态
                if(!StringUtils.equals(project.getExt$Item("crmSyncState"),Constant.YES)){
                    project.setExt$Item("crmSyncState", crmSyncState);
                }
            });

            projectService.getDao().update(projectList, "crmSyncState");
        } catch (JSONException e) {
            logger.error("[Sync-Project-Info-Failure]=======================");
            logger.error(result);
        }
    }
}
