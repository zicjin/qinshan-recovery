package com.sunwayworld.emc.module.bp.mobile.service.impl;

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

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileFileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcOperationType;
import com.sunwayworld.emc.module.bp.mobile.service.EmcMobileProjectService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.EmcContainerCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcMonitorTmplGroupService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.EmcTestCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodEquiptBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationLineService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
 * @author zhengxy@sunwayworld.com 2019年11月19日
 */
@Repository
@GikamBean
public class EmcMobileProjectServiceImpl implements EmcMobileProjectService {

    /** 项目附件 */
    private static final String PROJECT_FILE = "project";
    /** 采样单附件 */
    private static final String SAMPLE_FILE = "sample";
    /** 来自平板 */
    private static final String SOURCE_FROM_PAD = "pad";
    /** 文件类型 */
    private static final String[] BIZ_CATEGORY_CODES = {"点位示意图", "pointDiagram", "签名", "signature", "图片", "photo", "视频", "video", "文档", "document"};

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcEquiptService equiptService;

    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    private EmcEmpService empService;

    @Autowired
    private EmcPropertyFieldService propertyFieldService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcItemResultService itemResultService;

    @Autowired
    private EmcMonitorPointService monitorPointService;

    @Autowired
    private EmcClientService clientService;

    @Autowired
    private EmcMonitorTmplGroupService monitorTmplGroupService;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Autowired
    private EmcTestService testService;

    @Autowired
    private EmcTestCategoryService testCategoryService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcMethodService methodService;

    @Autowired
    private CoreCodeService codeService;

    @Autowired
    private CoreFileService fileService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcContainerCategoryService containerCategoryService;

    @Autowired
    private EmcQcCategoryService qcCategoryService;

    @Autowired
    private EmcEquiptCalibrationService calibrationService;

    @Autowired
    private EmcEquiptCalibrationLineService calibrationLineService;

    @Autowired
    private EmcEquiptCategoryService equiptCategoryService;

    @Autowired
    private EmcUnitService unitService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private CoreReportConfigLineService reportConfigLineService;

    @Autowired
    private EmcMonitorPointCategoryService monitorPointCategoryService;

    @Override
    public EmcMobileDTO selectProjectListByCond(RestJsonWrapperBean wrapper) {
        String userId = LocalContextHelper.getLoginUserId();

        wrapper.setFilterValue("projectType", "folderProject");
        wrapper.setFilterValue("targetType", "sample");
        wrapper.setFilterValue("sampleUserId", userId);
        wrapper.setFilterValue("processStatus_SEQ", EmcBizConstant.PROJECT_PROCESSSTATUS_DONE);
        wrapper.setFilterValue("folderStatus", EmcBizConstant.FOLDER_STATUS_DRAW);
        wrapper.setFilterValue("status_SEQ", EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);

        List<EmcProjectBean> projectList = projectService.selectListByFilter(SearchFilter.instance(), wrapper);

        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        mobileDTO.setProjectList(projectList);

        return mobileDTO;
    }

    @Override
    public EmcMobileDTO selectProjectData(String id, RestJsonWrapperBean wrapper) {
        String userId = LocalContextHelper.getLoginUserId();

        wrapper.setFilterValue("projId_SEQ", id);
        wrapper.setFilterValue("sampleUserId", userId);
        wrapper.setFilterValue("sceneUserId", userId);
        wrapper.setFilterValue("targetType", "sampleprepare");
        wrapper.setFilterValue("projectstatus_DIFFER", EmcBizConstant.PROJECT_STATUS_CANCEL);
        wrapper.setFilterValue("status_SEQ", EmcBizConstant.FOLDER_STATUS_DRAW);
        wrapper.setFilterValue("processStatus_SEQ", EmcBizConstant.PROJECT_PROCESSSTATUS_DONE);

        List<EmcFolderBean> folderList = folderService.selectListByFilter(SearchFilter.instance(), wrapper);
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID");

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID");

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        String targetId = projectService.getDao().getTable() + "$" + id;
        List<CoreFileBean> projectFileList = fileService.getDao().selectListByOneColumnValue(targetId, "TARGETID");

        // 拼接测试名称
        List<EmcOrderTaskBean> filterOrderTask = new ArrayList<>();
        orderList.forEach(o -> {
            EmcOrderTaskBean taskBean = new EmcOrderTaskBean();
            taskBean.setOrderId(o.getId());
            filterOrderTask.add(taskBean);
        });
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectList(filterOrderTask, Collections.singletonList("ORDERID"), Arrays.asList("ORDERID", "TESTNAME"));
        orderList.forEach(o -> {
            StringBuilder sb = new StringBuilder();
            allOrderTaskList.stream().filter(t -> ObjectUtils.equals(t.getOrderId(), o.getId())).forEach(t -> sb.append(t.getTestName()).append(","));
            if (!StringUtils.isBlank(sb.toString())) {
                o.setExt$Item("testname", sb.toString().substring(0, sb.length() - 1));
            }
        });

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");

        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        mobileDTO.setFolderList(folderList);
        mobileDTO.setOrderList(orderList);
        mobileDTO.setOrderTaskList(orderTaskList);
        mobileDTO.setOrderTaskResultList(orderTaskResultList);
        mobileDTO.setProjectFileList(projectFileList);
        mobileDTO.setOrderItemGroupList(orderItemGroupList);

        return mobileDTO;
    }

    @Override
    public EmcMobileDTO selectOperationTypeByCond(RestJsonWrapperBean wrapper) {
        List<String> operationTypeList = Arrays.stream(EmcOperationType.values()).map(EmcOperationType::getType).collect(Collectors.toList());
        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        mobileDTO.setOperateTypeList(operationTypeList);

        return mobileDTO;
    }

    @Override
    public EmcMobileDTO selectStaticDataByCond(RestJsonWrapperBean wrapper) {
        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        return mobileDTO;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public EmcMobileDTO uploadProjectInfo(RestJsonWrapperBean wrapper) {
        List<List<String>> bodyList = wrapper.getBodyList();
        if (bodyList.isEmpty()) {
            return new EmcMobileDTO();
        }

        List<String> dataList = bodyList.get(0);
        if (dataList.isEmpty()) {
            return new EmcMobileDTO();
        }

        EmcMobileDTO mobileDTO = JsonUtils.parse(dataList.get(0), EmcMobileDTO.class);

        List<EmcSampleBean> sampleList = mobileDTO.getSampleList();
        List<EmcOrderBean> orderList = mobileDTO.getOrderList();

        if (sampleList.isEmpty() || orderList.isEmpty()) {
            return new EmcMobileDTO();
        }

        // 平板端一次只会上传一个采样单，对于平板端重复上传的情况，如果采样单不在现场采样阶段即status为draft，则直接忽略掉
        List<String> sampleMobileIdList = sampleList.stream().map(EmcSampleBean::getMobileId).collect(Collectors.toList());
        List<EmcSampleBean> existSampleList = sampleService.getDao().selectListByOneColumnValues(sampleMobileIdList, "mobileId", Arrays.asList("ID", "STATUS"));
        boolean notDraftStatus = existSampleList.stream().anyMatch(sample -> !"draft".equals(sample.getStatus()));
        if (!existSampleList.isEmpty() && notDraftStatus) {
            return new EmcMobileDTO();
        }

        // 当一个样品已经关联了采样单后，再次上传时，如果不是之前的采样单，直接被忽略掉
        List<String> orderNoList = orderList.stream().map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
        List<EmcOrderBean> existOrderList = orderService.getDao().selectListByOneColumnValues(orderNoList, "orderNo", Arrays.asList("ID", "SAMPLEID", "QCCATEGORYCODE"));
        List<Long> relatedSampleIdList = existOrderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleId())).map(EmcOrderBean::getSampleId).distinct().collect(Collectors.toList());
        if (!relatedSampleIdList.isEmpty()) {
            // 如果本次上传的样品下关联了多个采样单，则直接忽略掉
            if (relatedSampleIdList.size() > 1) {
                return new EmcMobileDTO();
            }

            List<String> existMobileIdList = sampleService.getDao().selectListByIds(relatedSampleIdList, Collections.singletonList("mobileId")).stream().map(EmcSampleBean::getMobileId).collect(Collectors.toList());
            // 如果重复上传时，样品关联的采样单不是之前的采样单，则直接忽略
            if (!StringUtils.equals(sampleMobileIdList.get(0), existMobileIdList.get(0))) {
                return new EmcMobileDTO();
            }
        }

        // 重复上传时，如果相同样品号的NA样品的ID与系统中的不一致(有可能是从采样安排退回到了项目登记)，直接忽略掉本次上传的采样单
        List<Long> mobileOrderIdList = orderList.stream().filter(order -> EmcConstant.N_A.equals(order.getQcCategoryCode())).map(EmcOrderBean::getId).collect(Collectors.toList());
        List<Long> systemOrderIdList = existOrderList.stream().filter(order -> EmcConstant.N_A.equals(order.getQcCategoryCode())).map(EmcOrderBean::getId).collect(Collectors.toList());
        boolean existDifference = mobileOrderIdList.stream().anyMatch(orderId -> !systemOrderIdList.contains(orderId));
        if (existDifference) {
            return new EmcMobileDTO();
        }

        Map<Long, Long> projectIdMap = new HashMap<>();
        updateProject(mobileDTO, projectIdMap);

        Map<Long, EmcFolderBean> folderMap = new HashMap<>();
        updateFolder(mobileDTO, projectIdMap, folderMap);

        Map<Long, Long> calibrationIdMap = new HashMap<>();
        updateCalibration(mobileDTO, calibrationIdMap);

        Map<Long, Long> sampleIdMap = new HashMap<>();
        List<EmcSampleBean> newSampleList = updateSample(mobileDTO, projectIdMap, sampleIdMap, calibrationIdMap);

        Map<Long, EmcOrderBean> orderMap = new HashMap<>();
        updateOrder(mobileDTO, sampleIdMap, folderMap, orderMap);

        Map<Long, EmcOrderTaskBean> orderTaskMap = new HashMap<>();
        updateOrderTask(mobileDTO, orderMap, orderTaskMap);

        updateOrderTaskResult(mobileDTO, orderMap, orderTaskMap);

        EmcMobileDTO resultMobileDTO = new EmcMobileDTO();
        resultMobileDTO.setSampleList(newSampleList);

        return resultMobileDTO;
    }

    @Override
    public EmcMobileDTO uploadProjectFile(EmcMobileFileDTO mobileFile, MultipartFile file) {
        // bizCategory用于区分是项目附件或采样单附件
        String bizCategory = mobileFile.getBizCategory();
        if (StringUtils.isEmpty(bizCategory) || file == null) {
            return new EmcMobileDTO();
        }
        String mobileId = mobileFile.getExt();
        Long fileId = null;
        CoreFileBean newFile = new CoreFileBean();
        newFile.setExt(mobileFile.getExt());
        newFile.setName(mobileFile.getName());

        // 附件类型
        int typeIndex = Arrays.asList(BIZ_CATEGORY_CODES).indexOf(mobileFile.getType());
        String bizCategoryCode = typeIndex >= 0 && typeIndex <= BIZ_CATEGORY_CODES.length - 2 ? BIZ_CATEGORY_CODES[typeIndex + 1] : mobileFile.getType();
        newFile.setBizCategory(bizCategoryCode);

        // 项目附件
        if (PROJECT_FILE.equals(bizCategory)) {
            if (StringUtils.isEmpty(mobileId)) {
                newFile.setTargetId(projectService.getDao().getTable() + "$" + mobileFile.getTargetId());
            } else {
                List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValue(mobileId, "MOBILEID");
                if (!projectList.isEmpty()) {
                    newFile.setTargetId(projectService.getDao().getTable() + "$" + projectList.get(0).getId());
                }
            }
            fileId = fileService.upload(newFile, file);
        } else if (SAMPLE_FILE.equals(bizCategory)) {
            // 采样单附件
            if (StringUtils.isEmpty(mobileId)) {
                newFile.setTargetId(sampleService.getDao().getTable() + "$" + mobileFile.getTargetId());
            } else {
                List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValue(mobileId, "MOBILEID");
                if (!sampleList.isEmpty()) {
                    newFile.setTargetId(sampleService.getDao().getTable() + "$" + sampleList.get(0).getId());
                }
            }
            fileId = fileService.upload(newFile, file);
        }

        // 附件上传成功后，将附件信息返回给客户端
        if (fileId != null) {
            String remark = mobileFile.getRemark();
            if (!StringUtils.isEmpty(remark)) {
                // 更新备注信息
                fileService.updateColumn(fileId, "remark", remark);
            }
            List<CoreFileBean> fileList = new ArrayList<>();
            CoreFileBean fileBean = new CoreFileBean();
            fileBean.setId(fileId);
            fileList.add(fileBean);
            EmcMobileDTO mobileDTO = new EmcMobileDTO();
            mobileDTO.setProjectFileList(fileList);
            return mobileDTO;
        }

        return new EmcMobileDTO();
    }

    /**
     * 更新项目
     *
     * @param mobileDTO
     * @param projectIdMap
     */
    public void updateProject(EmcMobileDTO mobileDTO, Map<Long, Long> projectIdMap) {
        List<EmcProjectBean> mobileProjectList = mobileDTO.getProjectList();
        if (mobileProjectList == null || mobileProjectList.isEmpty()) {
            return;
        }

        // mobileId 为空即表示为待更新的项目
        List<EmcProjectBean> updateProjectList = mobileProjectList.stream().filter(p -> StringUtils.isEmpty(p.getMobileId())).collect(Collectors.toList());
        List<EmcProjectBean> varProjectList = mobileProjectList.stream().filter(p -> !StringUtils.isEmpty(p.getMobileId())).collect(Collectors.toList());
        if (varProjectList.isEmpty()) {
            projectService.getDao().update(updateProjectList);
            return;
        }

        // 查询系统中已存在的项目
        List<String> mobileIdList = varProjectList.stream().map(EmcProjectBean::getMobileId).collect(Collectors.toList());
        List<EmcProjectBean> existProjectList = projectService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existProjectList.stream().map(EmcProjectBean::getMobileId).collect(Collectors.toList());

        updateProjectList.addAll(varProjectList.stream().filter(f -> existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList()));
        List<EmcProjectBean> mobileNewProjectList = varProjectList.stream().filter(f -> !existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList());
        if (mobileNewProjectList.isEmpty()) {
            projectService.getDao().update(updateProjectList);
            return;
        }

        // 查询项目对应的业务类型
        List<Long> bizCategoryIdList = mobileNewProjectList.stream().map(EmcProjectBean::getBizCategoryId).distinct().collect(Collectors.toList());
        List<EmcBizCategoryBean> bizCategoryList = bizCategoryService.getDao().selectListByIds(bizCategoryIdList);

        List<EmcProjectBean> newProjectList = new ArrayList<>();
        mobileNewProjectList.stream().forEach(p -> {
            EmcProjectBean newProject = new EmcProjectBean();
            BeanUtils.copyProperties(p, newProject);
            newProject.setId(ApplicationContextHelper.getNextIdentity());
            newProject.setRegistDate(LocalDateTime.now());

            LocalDateTime taskDate = p.getTaskDate() == null ? LocalDateTime.now() : p.getTaskDate();
            String taskDateStr = DateTimeUtils.formatLocalDateTime(taskDate, "yy");
            StringBuilder sb = new StringBuilder();
            bizCategoryList.stream().filter(b -> ObjectUtils.equals(b.getId(), p.getBizCategoryId())).findFirst().ifPresent(b -> {
                sb.append(b.getBizCategoryId());
                newProject.setReportOrgId(b.getReportEditOrgId());
            });
            // 项目编号按业务类型区分
            String bizCategoryId = sb.toString();
            String projectNo = ApplicationContextHelper.getNextSequence(projectService.getDao().getTable(), bizCategoryId);
            sb.append(taskDateStr).append(projectNo);
            newProject.setProjNo(sb.toString());
            newProject.setSource(SOURCE_FROM_PAD);

            newProjectList.add(newProject);
            projectIdMap.put(p.getId(), newProject.getId());
        });

        projectService.getDao().insert(newProjectList);
        projectService.getDao().update(updateProjectList);
    }

    /**
     * 更新点位信息
     *
     * @param mobileDTO
     *            上传数据
     * @param projectIdMap
     *            新项目
     * @param folderMap
     *            新点位
     */
    protected void updateFolder(EmcMobileDTO mobileDTO, Map<Long, Long> projectIdMap, Map<Long, EmcFolderBean> folderMap,String ... sampleParamFlag) {
        // 生产版本保留该扩展点，但当前移动端上传流程不更新点位。
    }

    /**
     * 过滤已完成的点位
     *
     * @param updateFolderList
     *            待更新的点位
     * @param completedFolderList
     *            已完成的点位
     * @return
     */
    private List<EmcFolderBean> filterCompletedFolder(List<EmcFolderBean> updateFolderList, List<EmcFolderBean> completedFolderList) {
        List<EmcFolderBean> folderList = new ArrayList<>();
        updateFolderList.stream().forEach(f -> {
            if (completedFolderList.stream().noneMatch(m -> ObjectUtils.equals(m.getId(), f.getId()))) {
                folderList.add(f);
            }
        });
        return folderList;
    }

    /**
     * 更新采样单
     *
     * @param mobileDTO
     *            上传数据
     * @param projectIdMap
     *            新项目
     * @param sampleIdMap
     *            新采样单
     */
    protected List<EmcSampleBean> updateSample(EmcMobileDTO mobileDTO, Map<Long, Long> projectIdMap, Map<Long, Long> sampleIdMap, Map<Long, Long> calibrationIdMap) {
        List<EmcSampleBean> mobileSampleList = mobileDTO.getSampleList();
        mobileSampleList.forEach(m->{
            //赋值所属单位
            CoreUserBean user = LocalContextHelper.getLoginUser();
            String parentOrgId = user.getExt$Item("parentorgid");
            String parentOrgName = user.getExt$Item("parentorgname");
            m.setOrgId(parentOrgId);
            m.setOrgName(parentOrgName);
        });

        if (mobileSampleList == null || mobileSampleList.isEmpty()) {
            return Collections.emptyList();
        }

        List<EmcPropertyDataBean> propertyDataList = mobileDTO.getPropertyDataList();

        // 查询仪器校准报表模板
        List<CoreReportConfigLineBean> calibrationReportList = reportConfigLineService.getDao().selectListByOneColumnValue("calibrationReport", "REPORTCONFIGID", Collections.singletonList("ID"));

        // 区分需要更新或是新增的采样单，mobileId 为空即表示为待更新的采样单
        List<EmcSampleBean> updateSampleList = mobileSampleList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        List<EmcSampleBean> varSampleList = mobileSampleList.stream().filter(f -> !StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        if (varSampleList.isEmpty()) {
            convertCalibrationId(updateSampleList, calibrationIdMap, calibrationReportList);
            updateSampleProperty(updateSampleList, propertyDataList);
            sampleService.getDao().update(updateSampleList);
            return Collections.emptyList();
        }

        List<String> mobileIdList = varSampleList.stream().map(EmcSampleBean::getMobileId).collect(Collectors.toList());
        List<EmcSampleBean> existSampleList = sampleService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existSampleList.stream().map(EmcSampleBean::getMobileId).collect(Collectors.toList());

        Map<String, Long> mobileSampleIdMap = existSampleList.stream().collect(Collectors.toMap(EmcSampleBean::getMobileId, EmcSampleBean::getId));
        varSampleList.stream().forEach(s -> {
            Long sampleId = mobileSampleIdMap.get(s.getMobileId());
            if (sampleId != null) {
                EmcSampleBean sampleBean = new EmcSampleBean();
                BeanUtils.copyProperties(s, sampleBean);
                sampleBean.setId(sampleId);
                updateSampleList.add(sampleBean);

                sampleIdMap.put(s.getId(), sampleId);
            }
        });

        List<EmcSampleBean> mobileNewSampleList = varSampleList.stream().filter(f -> !existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList());
        if (mobileNewSampleList.isEmpty()) {
            convertCalibrationId(updateSampleList, calibrationIdMap, calibrationReportList);
            updateSampleProperty(updateSampleList, propertyDataList);
            sampleService.getDao().update(updateSampleList);
            return Collections.emptyList();
        }

        // 查询监测模板
        List<String> monitorTmplIdList = mobileNewSampleList.stream().filter(s -> !StringUtils.isEmpty(s.getMonitorTmplId())).map(EmcSampleBean::getMonitorTmplId).collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.getDao().selectListByIds(monitorTmplIdList);

        List<EmcSampleBean> newSampleList = new ArrayList<>();
        List<EmcPropertyDataBean> newPropertyDataList = new ArrayList<>();
        int newSampleSize = mobileNewSampleList.size();
        List<String> newSampleNoList = ApplicationContextHelper.getNextSequenceList(sampleService.getDao().getTable(), newSampleSize);
        for (int i = 0; i < newSampleSize; i++) {
            EmcSampleBean sampleBean = mobileNewSampleList.get(i);
            EmcSampleBean newSample = new EmcSampleBean();
            BeanUtils.copyProperties(sampleBean, newSample);
            if (!projectIdMap.isEmpty()) {
                Long newProjectId = projectIdMap.get(sampleBean.getProjId());
                if (newProjectId != null) {
                    newSample.setProjId(newProjectId);
                }
            }
            newSample.setId(ApplicationContextHelper.getNextIdentity());
            newSample.setSampleNo(newSampleNoList.get(i));
            newSample.setSource(SOURCE_FROM_PAD);

            // 属性数据
            EmcPropertyDataBean propertyDataBean = new EmcPropertyDataBean();
            if (propertyDataList != null && !propertyDataList.isEmpty()) {
                propertyDataList.stream().filter(d -> ObjectUtils.equals(d.getBizId(), sampleBean.getId())).findFirst().ifPresent(d -> BeanUtils.copyProperties(d, propertyDataBean));
            }
            propertyDataBean.setTableName(sampleService.getDao().getTable());
            propertyDataBean.setBizId(newSample.getId());
            propertyDataBean.setId(ApplicationContextHelper.getNextIdentity());

            // 监测模板，更新采样单表的默认采样单模板ID
            if (monitorTmplList != null && !monitorTmplList.isEmpty()) {
                monitorTmplList.stream().filter(t -> StringUtils.equals(t.getId(), sampleBean.getMonitorTmplId())).findFirst().ifPresent(t -> {
                    newSample.setReportConfigLineId(ObjectUtils.isEmpty(t.getReportConfigLineId()) ? null : t.getReportConfigLineId().toString());
                    newSample.setReceiveReportLineId(t.getReceiveReportLineId());
                    propertyDataBean.setTmplId(t.getPropertyTmplId());
                });
            }

            newSampleList.add(newSample);
            sampleIdMap.put(sampleBean.getId(), newSample.getId());
            newPropertyDataList.add(propertyDataBean);
        }

        convertCalibrationId(newSampleList, calibrationIdMap, calibrationReportList);

        sampleService.getDao().insert(newSampleList);
        propertyDataService.getDao().insert(newPropertyDataList);
        sampleService.getDao().update(updateSampleList);

        return newSampleList;
    }

    /**
     * 更新采样单详细信息
     * @param sampleList
     * @param propertyDataList
     */
    private void updateSampleProperty(List<EmcSampleBean> sampleList, List<EmcPropertyDataBean> propertyDataList) {
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcPropertyDataBean> samplePropertyDataList = propertyDataList.stream().filter(data -> sampleIdList.contains(data.getBizId())).collect(Collectors.toList());
        if (samplePropertyDataList.isEmpty()) {
            return;
        }

        List<Long> bizIdList = samplePropertyDataList.stream().map(EmcPropertyDataBean::getBizId).collect(Collectors.toList());
        List<EmcPropertyDataBean> existedPropertyDataList = propertyDataService.getDao().selectListByOneColumnValues(bizIdList, "BIZID");
        if (existedPropertyDataList.isEmpty()) {
            return;
        }

        existedPropertyDataList.forEach(existedProperty -> samplePropertyDataList.stream().filter(data -> ObjectUtils.equals(data.getBizId(),
                existedProperty.getBizId())).findFirst().ifPresent(data -> BeanUtils.copyProperties(data, existedProperty, "ID")));

        propertyDataService.getDao().update(existedPropertyDataList);
    }

    /**
     * 更新样品
     *
     * @param mobileDTO
     *            上传数据
     * @param sampleIdMap
     *            新采样单
     * @param folderMap
     *            新点位
     * @param orderMap
     *            新样品
     */
    protected void updateOrder(EmcMobileDTO mobileDTO, Map<Long, Long> sampleIdMap, Map<Long, EmcFolderBean> folderMap, Map<Long, EmcOrderBean> orderMap) {
        List<EmcOrderBean> mobileOrderList = mobileDTO.getOrderList();
        if (mobileOrderList == null || mobileOrderList.isEmpty()) {
            return;
        }

        // 区分需要更新或是新增的样品，mobileId 为空即表示为待更新的样品
        List<EmcOrderBean> allUpdateOrderList = mobileOrderList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        List<Long> updateOrderIdList = allUpdateOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> existUpdateOrderList = orderService.getDao().selectListByIds(updateOrderIdList, Collections.singletonList("ID"));
        List<Long> existUpdateOrderIdList = existUpdateOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        // 过滤掉平板中存在但是系统中不存在的样品
        List<EmcOrderBean> updateOrderList = allUpdateOrderList.stream().filter(order -> existUpdateOrderIdList.contains(order.getId())).collect(Collectors.toList());

        List<EmcOrderBean> varOrderList = mobileOrderList.stream().filter(f -> !StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        if (varOrderList.isEmpty()) {
            updateOrderList.forEach(updateOrder -> orderMap.put(updateOrder.getId(), updateOrder));
            convertSampleId(updateOrderList, sampleIdMap);
            updateFolderStatus(updateOrderList);
            orderService.getDao().updateIfChanged(updateOrderList);
            return;
        }

        List<String> mobileIdList = varOrderList.stream().map(EmcOrderBean::getMobileId).collect(Collectors.toList());
        List<EmcOrderBean> existOrderList = orderService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existOrderList.stream().map(EmcOrderBean::getMobileId).collect(Collectors.toList());

        updateOrderList.forEach(updateOrder -> orderMap.put(updateOrder.getId(), updateOrder));

        // 针对重复上传平板端复制的样品
        varOrderList.forEach(mobileOrder -> existOrderList.stream().filter(existOrder -> StringUtils.equals(existOrder.getMobileId(), mobileOrder.getMobileId())).findFirst().ifPresent(existOrder -> {
            orderMap.put(mobileOrder.getId(), existOrder);
            mobileOrder.setId(existOrder.getId());
            mobileOrder.setOrderNo(existOrder.getOrderNo());
            mobileOrder.setLabelNo(existOrder.getLabelNo());
            mobileOrder.setParentOrderNo(existOrder.getParentOrderNo());
        }));

        updateOrderList.addAll(varOrderList.stream().filter(f -> existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList()));
        List<EmcOrderBean> mobileNewOrderList = varOrderList.stream().filter(f -> !existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList());
        if (mobileNewOrderList.isEmpty()) {
            convertSampleId(updateOrderList, sampleIdMap);
            updateFolderStatus(updateOrderList);
            orderService.getDao().updateIfChanged(updateOrderList);
            return;
        }

        // 关于样品编号的处理，需要先查出所有关联点位的所有样品
        // 然后筛选出最大的样品编号，对于新增样品然后递增即可
        List<Long> folderIdList = mobileOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        if (!folderMap.isEmpty()) {
            folderIdList.addAll(folderMap.values().stream().map(EmcFolderBean::getId).distinct().collect(Collectors.toList()));
        }
        List<EmcOrderBean> orderBeanList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", Arrays.asList("ID", "FOLDERID", "ORDERNO"));
        Map<Long, List<EmcOrderBean>> folderOrderMap = orderBeanList.stream().collect(Collectors.groupingBy(EmcOrderBean::getFolderId));
        Map<Long, String> maxFolderOrderNoMap = new HashMap<>();
        folderOrderMap.forEach((k, v) -> {
            EmcOrderBean orderBean = v.stream().max(Comparator.comparing(EmcOrderBean::getOrderNo)).get();
            maxFolderOrderNoMap.put(k, orderBean.getOrderNo());
        });

        // 样品编号-ID对应关系集合
        Map<String, Long> orderNoIdMap = orderBeanList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, EmcOrderBean::getId));

        // 查询现场的QC类型
        List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByOneColumnValue("scene", "USECATEGORY");

        //
        Map<String, String> oldNewOrderNoMap = new HashMap<>();

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        for (int i = 0; i < mobileNewOrderList.size(); i++) {
            EmcOrderBean oldOrder = mobileNewOrderList.get(i);
            EmcOrderBean newOrder = new EmcOrderBean();
            BeanUtils.copyProperties(oldOrder, newOrder);
            PersistableHelper.resetBasicProperties(newOrder);

            if (!sampleIdMap.isEmpty()) {
                Long newSampleId = sampleIdMap.get(oldOrder.getSampleId());
                if (newSampleId != null) {
                    newOrder.setSampleId(newSampleId);
                }
            }

            if (!folderMap.isEmpty()) {
                EmcFolderBean newFolder = folderMap.get(oldOrder.getFolderId());
                if (newFolder != null) {
                    newOrder.setFolderId(newFolder.getId());
                    newOrder.setProjId(newFolder.getProjId());
                }
            }

            // 获取点位下最大的样品编号，然后依次递增
            String orderNo = maxFolderOrderNoMap.get(newOrder.getFolderId());
            if (StringUtils.isEmpty(orderNo)) {
                EmcFolderBean newFolder = folderMap.get(oldOrder.getFolderId());
                String maxOrderNo = newFolder.getFolderNo() + "001";
                newOrder.setOrderNo(maxOrderNo);
                newOrder.setLabelNo(maxOrderNo);
                maxFolderOrderNoMap.put(newOrder.getFolderId(), maxOrderNo);
            } else {
                String orderNoPrefix = orderNo.substring(0, orderNo.length() - 3);
                String orderNoSuffix = orderNo.substring(orderNo.length() - 3);
                String maxOrderNo = orderNoPrefix + StringUtils.leftPad((NumberUtils.parseInt(orderNoSuffix) + 1) + "", 3);
                newOrder.setOrderNo(maxOrderNo);
                newOrder.setLabelNo(maxOrderNo);
                maxFolderOrderNoMap.put(newOrder.getFolderId(), maxOrderNo);
            }

            qcCategoryList.stream().filter(qc -> StringUtils.equals(qc.getQcCategoryId(), newOrder.getQcCategoryCode())).findFirst().ifPresent(qc -> {
                newOrder.setQcCategoryId(qc.getId());
                if (StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_P_BLANK, newOrder.getQcCategoryCode())) {
                    // 这里保存父样编号到ext主要是为了后面能够正常复制样品分析项组，如果设置为null了，就无法复制父样的分析项组了
                    newOrder.setExt$Item("parentorderno", newOrder.getParentOrderNo());
                    newOrder.setParentOrderNo(null);
                }
            });

            // 平板复制出来的正常样品也不需要保存父样编号
            if (StringUtils.equals(EmcConstant.N_A, newOrder.getQcCategoryCode())) {
                // 这里保存父样编号到ext主要是为了后面能够正常复制样品分析项组，如果设置为null了，就无法复制父样的分析项组了
                newOrder.setExt$Item("parentorderno", newOrder.getParentOrderNo());
                newOrder.setParentOrderNo(null);
            } else if (!StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_P_BLANK, newOrder.getQcCategoryCode())) {
                // 处理平板上复制出来的新样品，当添加现场平行时，父样编号不正确问题
                String parentOrderNo = newOrder.getParentOrderNo();
                if (!StringUtils.isEmpty(oldNewOrderNoMap.get(parentOrderNo))) {
                    newOrder.setParentOrderNo(oldNewOrderNoMap.get(parentOrderNo));
                }
            }

            newOrder.setSource(SOURCE_FROM_PAD);
            newOrder.setId(ApplicationContextHelper.getNextIdentity());
            newOrder.setSendDate(LocalDate.now());
            newOrder.setSendTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
            newOrder.setOrderNum(EmcConstant.ONE);
            newOrder.setCycle(EmcConstant.ONE);
            newOrder.setFrequency(EmcConstant.ONE);
            newOrder.setOrderNum(EmcConstant.ONE);
            newOrder.setTermination(Constant.NO);
            newOrderList.add(newOrder);
            orderMap.put(oldOrder.getId(), newOrder);
            oldNewOrderNoMap.put(oldOrder.getOrderNo(), newOrder.getOrderNo());
        }

        convertSampleId(updateOrderList, sampleIdMap);
        updateFolderStatus(updateOrderList);

        copyOrderItemGroup(newOrderList, orderNoIdMap);

        orderService.getDao().insert(newOrderList);
        orderService.getDao().updateIfChanged(updateOrderList);
    }

    private void convertSampleId(List<EmcOrderBean> orderList, Map<Long, Long> sampleIdMap) {
        orderList.stream().forEach(o -> {
            Long newSampleId = sampleIdMap.get(o.getSampleId());
            if (newSampleId != null) {
                o.setSampleId(newSampleId);
            }
        });
    }

    private void convertCalibrationId(List<EmcSampleBean> sampleList, Map<Long, Long> calibrationIdMap, List<CoreReportConfigLineBean> calibrationReportList) {
        // 过滤出校准仪器不为空的
        sampleList.stream().filter(sample -> !StringUtils.isEmpty(sample.getCalibrationId())).forEach(sample -> {
            String[] calibrationIds = StringUtils.split(sample.getCalibrationId(), ",");
            List<Long> convertedIdList = new ArrayList<>();
            Arrays.stream(calibrationIds).forEach(id -> convertedIdList.add(Optional.ofNullable(calibrationIdMap.get(NumberUtils.parseLong(id))).orElse(NumberUtils.parseLong(id))));
            sample.setCalibrationId(StringUtils.join(convertedIdList, ","));
            if (!calibrationReportList.isEmpty()) {
                sample.setCaliReportLineId(calibrationReportList.get(0).getId() + "");
            }
        });
    }

    private void copyOrderItemGroup(List<EmcOrderBean> newOrderList, Map<String, Long> orderNoIdMap) {
        List<String> parentOrderNoList = newOrderList.stream().map(order -> Optional.ofNullable(order.getParentOrderNo()).orElseGet(() -> order.getExt$Item("parentorderno"))).distinct().collect(Collectors.toList());
        // 此处有两种情况：1、通过系统中的NA样复制出的现场平行和空白（可以正常复制分析项组）；
        // 2、通过系统中的NA样先复制出NA（NA可以正常复制分析项组），然后通过复制的NA样品再复制现场平行和空白（平行和空白无法正常复制分析项组，这种情况不支持）
        List<Long> parentOrderIdList = orderNoIdMap.entrySet().stream().filter(entry -> parentOrderNoList.contains(entry.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(parentOrderIdList, "ORDERID");

        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        newOrderList.forEach(newOrder -> {
            String parentOrderNo = Optional.ofNullable(newOrder.getParentOrderNo()).orElseGet(() -> newOrder.getExt$Item("parentorderno"));
            Optional.ofNullable(orderNoIdMap.get(parentOrderNo)).ifPresent(parentOrderId -> orderItemGroupList.stream().filter(itemGroup -> ObjectUtils.equals(itemGroup.getOrderId(), parentOrderId)).forEach(itemGroup -> {
                EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
                BeanUtils.copyProperties(itemGroup, newOrderItemGroup);
                newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                newOrderItemGroup.setOrderId(newOrder.getId());
                newOrderItemGroupList.add(newOrderItemGroup);
            }));
        });

        if (!newOrderItemGroupList.isEmpty()) {
            orderItemGroupService.getDao().insert(newOrderItemGroupList);
        }
    }

    /**
     * 更新点位状态
     *
     * @param orderList
     *            样品列表
     */
    private void updateFolderStatus(List<EmcOrderBean> orderList) {
        // 判断点位下所有的样品是否都关联了采样单，若是，则将点位状态更改为已采样
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> allOrderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID");
        List<Long> completedFolderIdList = new ArrayList<>();
        folderIdList.stream().forEach(i -> {
            List<EmcOrderBean> collect = allOrderList.stream().filter(o -> ObjectUtils.equals(o.getFolderId(), i)).filter(o -> ObjectUtils.isEmpty(o.getSampleId())).collect(Collectors.toList());
            boolean samplingCompleted = collect.stream().allMatch(o -> orderList.stream().anyMatch(m -> ObjectUtils.equals(m.getId(), o.getId()) && !ObjectUtils.isEmpty(m.getSampleId())));
            if (samplingCompleted) {
                completedFolderIdList.add(i);
            }
        });

        if (!completedFolderIdList.isEmpty()) {
            EmcFolderBean updateFolder = new EmcFolderBean();
            updateFolder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
            folderService.getDao().updateByIds(updateFolder, completedFolderIdList, "STATUS");
        }
    }

    /**
     * 更新样品测试
     *
     * @param mobileDTO
     *            上传数据
     * @param orderMap
     *            新样品
     * @param orderTaskMap
     *            新样品测试
     */
    protected void updateOrderTask(EmcMobileDTO mobileDTO, Map<Long, EmcOrderBean> orderMap, Map<Long, EmcOrderTaskBean> orderTaskMap,String ... sampleParamFlag) {
        List<EmcOrderTaskBean> mobileOrderTaskList = mobileDTO.getOrderTaskList();

        // 区分需要更新或是新增的样品测试，mobileId 为空即表示为待更新的样品测试
        List<EmcOrderTaskBean> allUpdateOrderTaskList = mobileOrderTaskList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        List<Long> allUpdateOrderTaskIdList = allUpdateOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> existUpdateOrderTaskList = orderTaskService.getDao().selectListByIds(allUpdateOrderTaskIdList, Collections.singletonList("ID"));
        List<Long> existUpdateOrderTaskIdList = existUpdateOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        // 过滤掉平板上存在但系统中不存在的样品测试
        List<EmcOrderTaskBean> updateOrderTaskList = allUpdateOrderTaskList.stream().filter(task -> existUpdateOrderTaskIdList.contains(task.getId())).collect(Collectors.toList());

        List<EmcOrderTaskBean> varOrderTaskList = mobileOrderTaskList.stream().filter(f -> !StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        if (varOrderTaskList.isEmpty()) {
            orderTaskService.getDao().updateIfChanged(updateOrderTaskList);
            return;
        }

        List<String> mobileIdList = varOrderTaskList.stream().map(EmcOrderTaskBean::getMobileId).collect(Collectors.toList());
        List<EmcOrderTaskBean> existOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existOrderTaskList.stream().map(EmcOrderTaskBean::getMobileId).collect(Collectors.toList());

        // 处理重复上传平板端复制或添加的样品测试
        updateOrderTaskList.forEach(updateOrderTask -> orderTaskMap.put(updateOrderTask.getId(), updateOrderTask));
        varOrderTaskList.forEach(mobileOrderTask -> existOrderTaskList.stream().filter(existOrderTask -> StringUtils.equals(existOrderTask.getMobileId(), mobileOrderTask.getMobileId())).findFirst().ifPresent(existOrderTask -> {
            orderTaskMap.put(mobileOrderTask.getId(), existOrderTask);
            mobileOrderTask.setId(existOrderTask.getId());
            mobileOrderTask.setOrderId(orderMap.get(mobileOrderTask.getOrderId()).getId());
            mobileOrderTask.setQcCategoryId(existOrderTask.getQcCategoryId());
            mobileOrderTask.setQcCategoryCode(existOrderTask.getQcCategoryCode());
            mobileOrderTask.setQcName(existOrderTask.getQcName());
            mobileOrderTask.setEquiptAcquNo(existOrderTask.getEquiptAcquNo());
            mobileOrderTask.setParentId(existOrderTask.getParentId());
        }));

        updateOrderTaskList.addAll(varOrderTaskList.stream().filter(f -> existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList()));
        List<EmcOrderTaskBean> mobileNewOrderTaskList = varOrderTaskList.stream().filter(f -> !existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList());
        if (mobileNewOrderTaskList.isEmpty()) {
            orderTaskService.getDao().updateIfChanged(updateOrderTaskList);
            return;
        }

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        mobileNewOrderTaskList.stream().forEach(t -> {
            EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
            BeanUtils.copyProperties(t, newOrderTask);
            if (!orderMap.isEmpty()) {
                EmcOrderBean newOrder = orderMap.get(t.getOrderId());
                if (newOrder != null) {
                    // 现场空白和现场平行不复制现场分析项
                    boolean isSceneQc = EmcConstant.QCCATEGORY_QCCATEGORYID_P_BLANK.equals(newOrder.getQcCategoryCode()) || EmcConstant.QCCATEGORY_QCCATEGORYID_P_DUP.equals(newOrder.getQcCategoryCode());
                    if (EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(newOrderTask.getAnalysisType()) && isSceneQc) {
                        return;
                    }
                    newOrderTask.setOrderId(newOrder.getId());
                    newOrderTask.setFolderId(newOrder.getFolderId());
                    newOrderTask.setProjId(newOrder.getProjId());
                    newOrderTask.setEquiptAcquNo(newOrder.getLabelNo());
                    newOrderTask.setQcCategoryId(newOrder.getQcCategoryId());
                    newOrderTask.setQcCategoryCode(newOrder.getQcCategoryCode());
                    newOrderTask.setQcName(newOrder.getQcName());

                    EmcOrderTaskBean parentOrderTask = orderTaskMap.get(newOrderTask.getParentId());
                    if (parentOrderTask != null) {
                        newOrderTask.setParentId(parentOrderTask.getId());
                    }
                }
            }
            newOrderTask.setId(ApplicationContextHelper.getNextIdentity());
            newOrderTask.setSource(SOURCE_FROM_PAD);

            newOrderTaskList.add(newOrderTask);
            orderTaskMap.put(t.getId(), newOrderTask);
        });

        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskService.getDao().updateIfChanged(updateOrderTaskList);
    }

    /**
     * 更新样品测试结果
     *
     * @param mobileDTO
     *            上传数据
     * @param orderTaskMap
     *            新样品测试
     */
    protected void updateOrderTaskResult(EmcMobileDTO mobileDTO, Map<Long, EmcOrderBean> orderMap, Map<Long, EmcOrderTaskBean> orderTaskMap,String ... sampleParamFlag) {
        List<EmcOrderTaskResultBean> mobileOrderTaskResultList = mobileDTO.getOrderTaskResultList();

        // 区分需要更新或是新增的样品测试结果，mobileId 为空即表示为待更新的样品测试结果
        List<EmcOrderTaskResultBean> allUpdateOrderTaskResultList = mobileOrderTaskResultList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        List<Long> allUpdateResultIdList = allUpdateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> existUpdateResultList = orderTaskResultService.getDao().selectListByIds(allUpdateResultIdList, Collections.singletonList("ID"));
        List<Long> existUpdateResultIdList = existUpdateResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        // 过滤掉平板上存在但是系统中不存在的样品测试结果
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = allUpdateOrderTaskResultList.stream().filter(result -> existUpdateResultIdList.contains(result.getId())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> varOrderTaskResultList = mobileOrderTaskResultList.stream().filter(f -> !StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        if (varOrderTaskResultList.isEmpty()) {
            orderTaskResultService.getDao().updateIfChanged(updateOrderTaskResultList);
            computeResult(updateOrderTaskResultList);
            return;
        }

        List<String> mobileIdList = varOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getMobileId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> existOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getMobileId).collect(Collectors.toList());

        // 处理平板端重复上传复制或新添加的样品测试结果
        varOrderTaskResultList.forEach(mobileTaskResult -> existOrderTaskResultList.stream().filter(existResult -> StringUtils.equals(existResult.getMobileId(), mobileTaskResult.getMobileId())).findFirst().ifPresent(existResult -> {
            mobileTaskResult.setId(existResult.getId());
            EmcOrderBean currentOrder = orderMap.get(mobileTaskResult.getOrderId());
            mobileTaskResult.setOrderNo(currentOrder.getOrderNo());
            mobileTaskResult.setOrderId(currentOrder.getId());
            mobileTaskResult.setOrderTaskId(orderTaskMap.get(mobileTaskResult.getOrderTaskId()).getId());
            mobileTaskResult.setQcCategoryId(currentOrder.getQcCategoryId());
            mobileTaskResult.setQcCategoryCode(currentOrder.getQcCategoryCode());
            mobileTaskResult.setQcName(currentOrder.getQcName());
        }));

        updateOrderTaskResultList.addAll(varOrderTaskResultList.stream().filter(f -> existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList()));
        List<EmcOrderTaskResultBean> mobileNewOrderTaskResultList = varOrderTaskResultList.stream().filter(f -> !existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList());
        if (mobileNewOrderTaskResultList.isEmpty()) {
            orderTaskResultService.getDao().updateIfChanged(updateOrderTaskResultList);
            computeResult(updateOrderTaskResultList);
            return;
        }

        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        mobileNewOrderTaskResultList.forEach(r -> {
            EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
            BeanUtils.copyProperties(r, newOrderTaskResult);

            if (!orderMap.isEmpty()) {
                EmcOrderBean newOrder = orderMap.get(r.getOrderId());
                if (newOrder != null) {
                    // 现场空白和现场平行不复制现场分析项
                    boolean isSceneQc = EmcConstant.QCCATEGORY_QCCATEGORYID_P_BLANK.equals(newOrder.getQcCategoryCode()) || EmcConstant.QCCATEGORY_QCCATEGORYID_P_DUP.equals(newOrder.getQcCategoryCode());
                    if (EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(newOrderTaskResult.getAnalysisType()) && isSceneQc) {
                        return;
                    }
                    newOrderTaskResult.setOrderNo(newOrder.getOrderNo());
                }
            }

            if (!orderTaskMap.isEmpty()) {
                EmcOrderTaskBean newOrderTask = orderTaskMap.get(r.getOrderTaskId());
                if (newOrderTask != null) {
                    newOrderTaskResult.setOrderId(newOrderTask.getOrderId());
                    newOrderTaskResult.setOrderTaskId(newOrderTask.getId());
                    newOrderTaskResult.setFolderId(newOrderTask.getFolderId());
                    newOrderTaskResult.setProjId(newOrderTask.getProjId());
                    newOrderTaskResult.setQcCategoryId(newOrderTask.getQcCategoryId());
                    newOrderTaskResult.setQcCategoryCode(newOrderTask.getQcCategoryCode());
                    newOrderTaskResult.setQcName(newOrderTask.getQcName());
                }
            }
            newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
            newOrderTaskResult.setSource(SOURCE_FROM_PAD);
            newOrderTaskResultList.add(newOrderTaskResult);
        });

        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderTaskResultService.getDao().updateIfChanged(updateOrderTaskResultList);

        List<EmcOrderTaskResultBean> taskResultList = new ArrayList<>(newOrderTaskResultList);
        taskResultList.addAll(updateOrderTaskResultList);
        computeResult(taskResultList);
    }

    /**
     * 更新采样仪器校准记录
     * @param mobileDTO
     * @param calibrationIdMap
     */
    protected void updateCalibration(EmcMobileDTO mobileDTO, Map<Long, Long> calibrationIdMap) {
        List<EmcEquiptCalibrationBean> calibrationList = mobileDTO.getCalibrationList();
        List<EmcEquiptCalibrationLineBean> calibrationLineList = mobileDTO.getCalibrationLineList();
        if (CollectionUtils.isEmpty(calibrationList) || CollectionUtils.isEmpty(calibrationLineList)) {
            return;
        }

        // 无mobileId即为系统创建的，过滤出从系统下载的仪器校准记录，对于从系统中下载的校准记录，上传时不做任何更新
        List<EmcEquiptCalibrationBean> originalCalibrationList = calibrationList.stream().filter(cali -> StringUtils.isEmpty(cali.getMobileId())).collect(Collectors.toList());
        originalCalibrationList.forEach(cali -> calibrationIdMap.put(cali.getId(), cali.getId()));

        // 有mobileId的即为平板端创建的，这里分两种情况：之前已经上传过和从未上传
        List<EmcEquiptCalibrationBean> mobileCalibrationList = calibrationList.stream().filter(cali -> !StringUtils.isEmpty(cali.getMobileId())).collect(Collectors.toList());
        List<String> mobileIdList = mobileCalibrationList.stream().map(EmcEquiptCalibrationBean::getMobileId).collect(Collectors.toList());
        List<EmcEquiptCalibrationBean> existedMobileCalibrationList = calibrationService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID", Arrays.asList("ID", "MOBILEID"));

        List<String> existedMobileIdList = existedMobileCalibrationList.stream().map(EmcEquiptCalibrationBean::getMobileId).collect(Collectors.toList());
        Map<String, Long> existedMobileIdMap = existedMobileCalibrationList.stream().collect(Collectors.toMap(EmcEquiptCalibrationBean::getMobileId, EmcEquiptCalibrationBean::getId));

        List<EmcEquiptCalibrationBean> updateCalibrationList = new ArrayList<>();
        mobileCalibrationList.stream().filter(cali -> existedMobileIdList.contains(cali.getMobileId())).forEach(cali -> {
            EmcEquiptCalibrationBean updateCalibration = new EmcEquiptCalibrationBean();
            BeanUtils.copyProperties(cali, updateCalibration);
            updateCalibration.setId(existedMobileIdMap.get(cali.getMobileId()));
            updateCalibrationList.add(updateCalibration);

            calibrationIdMap.put(cali.getId(), existedMobileIdMap.get(cali.getMobileId()));
        });

        List<EmcEquiptCalibrationBean> newCalibrationList = new ArrayList<>();
        List<EmcEquiptCalibrationBean> notExistedMobileCalibrationList = mobileCalibrationList.stream().filter(cali -> !existedMobileIdList.contains(cali.getMobileId())).collect(Collectors.toList());
        notExistedMobileCalibrationList.forEach(cali -> {
            EmcEquiptCalibrationBean newCalibration = new EmcEquiptCalibrationBean();
            BeanUtils.copyProperties(cali, newCalibration);
            newCalibration.setId(ApplicationContextHelper.getNextIdentity());
            newCalibrationList.add(newCalibration);

            calibrationIdMap.put(cali.getId(), newCalibration.getId());
        });

        // 过滤从系统下载的校准记录，mobileId不为空即为平板端创建的
        List<EmcEquiptCalibrationLineBean> mobileCalibrationLineList = calibrationLineList.stream().filter(line -> !StringUtils.isEmpty(line.getMobileId())).collect(Collectors.toList());
        List<String> lineMobileIdList = mobileCalibrationLineList.stream().map(EmcEquiptCalibrationLineBean::getMobileId).collect(Collectors.toList());
        List<EmcEquiptCalibrationLineBean> existedMobileCaliLineList = calibrationLineService.getDao().selectListByOneColumnValues(lineMobileIdList, "MOBILEID", Arrays.asList("ID", "MOBILEID"));

        List<String> existedLineMobileIdList = existedMobileCaliLineList.stream().map(EmcEquiptCalibrationLineBean::getMobileId).collect(Collectors.toList());
        Map<String, Long> existedLineMobileIdMap = existedMobileCaliLineList.stream().collect(Collectors.toMap(EmcEquiptCalibrationLineBean::getMobileId, EmcEquiptCalibrationLineBean::getId));
        List<EmcEquiptCalibrationLineBean> updateCalibrationLineList = new ArrayList<>();
        mobileCalibrationLineList.stream().filter(line -> existedLineMobileIdList.contains(line.getMobileId())).forEach(line -> {
            EmcEquiptCalibrationLineBean updateCaliLine = new EmcEquiptCalibrationLineBean();
            BeanUtils.copyProperties(line, updateCaliLine);
            updateCaliLine.setId(existedLineMobileIdMap.get(line.getMobileId()));
            updateCaliLine.setCalibrationId(calibrationIdMap.get(line.getCalibrationId()));

            updateCalibrationLineList.add(updateCaliLine);
        });

        List<EmcEquiptCalibrationLineBean> newCalibrationLineList = new ArrayList<>();
        List<EmcEquiptCalibrationLineBean> notExistedMobileCaliLineList = mobileCalibrationLineList.stream().filter(line -> !existedLineMobileIdList.contains(line.getMobileId())).collect(Collectors.toList());
        notExistedMobileCaliLineList.forEach(line -> {
            EmcEquiptCalibrationLineBean newCaliLine = new EmcEquiptCalibrationLineBean();
            BeanUtils.copyProperties(line, newCaliLine);
            newCaliLine.setId(ApplicationContextHelper.getNextIdentity());
            newCaliLine.setCalibrationId(calibrationIdMap.get(line.getCalibrationId()));

            newCalibrationLineList.add(newCaliLine);
        });

        // 更新或新增
        calibrationService.getDao().update(updateCalibrationList);
        calibrationService.getDao().insert(newCalibrationList);
        calibrationLineService.getDao().update(updateCalibrationLineList);
        calibrationLineService.getDao().insert(newCalibrationLineList);
    }

    /**
     * 触发计算公式
     * @param orderTaskResultList
     */
    private void computeResult(List<EmcOrderTaskResultBean> orderTaskResultList) {
        List<Long> calcResultIdList = orderTaskResultList.stream().filter(r -> !ObjectUtils.isEmpty(r.getOriginalResult())).map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        if (!calcResultIdList.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(calcResultIdList).compute();
        }
    }

    @Override
    public Page<EmcSampleBean> selectMobileScanner(RestJsonWrapperBean wrapper) {
        return sampleService.selectMobileScanner(wrapper);
    }

    @Override
    public Page<EmcSampleBean> selectDetailLabel(RestJsonWrapperBean wrapper) {
        return sampleService.selectDetailLabel(wrapper);
    }

    @Override
    public Page<EmcSampleBean> selectLabelDetail(RestJsonWrapperBean wrapper) {
        return sampleService.selectLabelDetail(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public String updateSannerByLabelId(RestJsonWrapperBean wrapper) {
        String labelId = wrapper.getFilterValue("labelId");
        String orderNo = labelId.substring(0,13);
        String tail = labelId.substring(13);
        JSONObject jsonObj = new JSONObject();
        // 查询order
        EmcOrderBean orderTemp = new EmcOrderBean();
        orderTemp.setOrderNo(orderNo);
        EmcOrderBean order = orderService.getDao().selectOneIfPresent(orderTemp);

        if (order != null && order.getSampleId() != null) {
            // 查询sample，判断是否在样品接收状态
            EmcSampleBean sample = sampleService.getDao().selectById(order.getSampleId());
            if (sample != null) {
                if (EmcBizConstant.SAMPLE_STATUS_PRELOGGED.equals(sample.getStatus()) && EmcBizConstant.SAMPLE_PROCESSSTATUS_DONE.equals(sample.getProcessStatus())) {

                    List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");
                    List<EmcOrderTaskBean> updateList = new ArrayList<>();
                    orderTaskList.forEach(a -> {
                        String str = a.getContainerCategoryCode() + a.getContainerGroup();
                        if(tail.equals(str)) {
                            EmcOrderTaskBean temp = new EmcOrderTaskBean();
                            temp.setId(a.getId());
                            temp.setScanned("1");
                            updateList.add(temp);
                        }
                    });
                    if(updateList.isEmpty()) {
                        jsonObj.put("retCode", "1");
                        return jsonObj.toString();
                    }
                    orderTaskService.getDao().update(updateList, "SCANNED");
                    jsonObj.put("retCode", "0");
                    return jsonObj.toString();

                }
                jsonObj.put("retCode", "2");
                return jsonObj.toString();
            }
        }
        jsonObj.put("retCode", "1");
        return jsonObj.toString();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateRemarkByLabelId(RestJsonWrapperBean wrapper) {

        String labelId = wrapper.getFilterValue("labelId");
        String remarks = wrapper.getFilterValue("remarks");
        String orderNo = labelId.substring(0,13);
        String tail = labelId.substring(13);
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(orderNo, "ORDERNO");
        List<Long> orderIdList = orderList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<EmcOrderTaskBean> updateList = new ArrayList<>();
        orderTaskList.forEach(a -> {
            String str = a.getContainerCategoryCode() + a.getContainerGroup();
            if(tail.equals(str)) {
                a.setRemarks(remarks);
                updateList.add(a);
            }
        });
        orderTaskService.getDao().update(updateList, "REMARKS");
    }
}
