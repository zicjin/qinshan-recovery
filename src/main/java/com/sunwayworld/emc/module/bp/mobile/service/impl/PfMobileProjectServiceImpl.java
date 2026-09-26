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
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.service.PfFolderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderService;
import com.sunwayworld.emc.module.bp.delivery.bean.EmcDeliveryBean;
import com.sunwayworld.emc.module.bp.delivery.service.EmcDeliveryService;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileFileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobilePaginationDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcOperationType;
import com.sunwayworld.emc.module.bp.mobile.service.PfMobileProjectService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean.EmcContainerCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.EmcContainerCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service.EmcEquiptRequisitionService;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcMonitorTmplGroupService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.EmcTestCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestItemService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasParamService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasPreAnalyService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEquiptAcqScriptService;
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
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
 * @author zhengxy@sunwayworld.com 2019年11月19日
 */
@Repository
@GikamBean
public class PfMobileProjectServiceImpl extends EmcMobileProjectServiceImpl implements PfMobileProjectService {

    @SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(PfMobileProjectServiceImpl.class);

    /** 项目附件 */
    private static final String PROJECT_FILE = "project";
    /** 采样单附件 */
    private static final String SAMPLE_FILE = "sample";
    /** 点位附件 */
    private static final String FOLDER_FILE = "folder";
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
    private PfFolderService folderService;

    @Autowired
    private PfOrderService orderService;

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
    private EmcOrderItemService orderItemService;

    @Autowired
    private CoreReportConfigLineService reportConfigLineService;

    @Autowired
    private EmcTestMethodOrderCategoryService orderCategoryService;

    @Autowired
    private EmcTestCapacityService testCapacityService;

    @Autowired
    private EmcEquiptRequisitionService requisitionService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    @Autowired
    private EmcEquiptAcqScriptService acqScriptService;

    @Autowired
    private EmcEasParamService easParamService;

    @Autowired
    private EmcEasPreAnalyService easPreAnalyService;

    @Autowired
    private EmcEasResultService easResultService;

    @Autowired
    private EmcEquiptTestItemService equiptTestItemService;

    @Autowired
    private EmcDeliveryService deliveryService;

    @Override
    public EmcMobileDTO selectProjectListByCond(RestJsonWrapperBean wrapper) {
        String userId = LocalContextHelper.getLoginUserId();

        wrapper.setFilterValue("projectType", "folderProject");
        wrapper.setFilterValue("targetType", "sample");
        wrapper.setFilterValue("sampleUserId", userId);
        wrapper.setFilterValue("processStatus_SEQ", EmcBizConstant.PROJECT_PROCESSSTATUS_DONE);
        wrapper.setFilterValue("folderStatus", EmcBizConstant.FOLDER_STATUS_DRAW);
        wrapper.setFilterValue("status_IN", EmcBizConstant.PROJECT_STATUS_AUDITFINISHED +","+ EmcBizConstant.PROJECT_STATUS_REPORTEDIT);
        wrapper.setFilterValue("orgId_SB", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));

        List<EmcProjectBean> projectList = projectService.selectListByFilter(SearchFilter.instance(), wrapper);

        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        mobileDTO.setProjectList(projectList);

        return mobileDTO;
    }

    @Override
    public EmcMobileDTO selectProjectData(String id, RestJsonWrapperBean wrapper) {
        EmcMobileDTO mobileDTO = new EmcMobileDTO();
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

        // 查询采样单数据，只能下载还未提交的采样单
        List<EmcSampleBean> sampleList = sampleService.selectListByFilter(SearchFilter.instance().match("projId", id).filter(MatchPattern.EQ)
                .match("createdById", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ)
                .match("status", EmcBizConstant.SAMPLE_STATUS_DRAFT).filter(MatchPattern.EQ));
        if (!sampleList.isEmpty()) {
            List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> sampleOrderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "sampleId");
            orderList.addAll(sampleOrderList);

            List<Long> sampleFolderIdList = sampleOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
            List<Long> diffFolderIdList = sampleFolderIdList.stream().filter(folderId -> !folderIdList.contains(folderId)).collect(Collectors.toList());
            if (!diffFolderIdList.isEmpty()) {
                List<EmcFolderBean> sampleFolderList = folderService.selectListByIds(diffFolderIdList);
                List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(diffFolderIdList, "FOLDERID");
                folderList.addAll(sampleFolderList);
                orderTaskList.addAll(sampleOrderTaskList);
            }
            List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(sampleIdList, "bizId");
            mobileDTO.setPropertyDataList(propertyDataList);
        }

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

        //查询气的采样参数主业务数据
        List<EmcFolderBean> sampleParamFolderList = folderService.selectListByFilter(SearchFilter.instance().match("PARAMPROJID", id).filter(MatchPattern.SEQ));
        List<Long> sampleParamFolderIdList = sampleParamFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        List<EmcOrderBean> sampleParamOrderList = orderService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID");
        List<Long> sampleParamOrderIdList = sampleParamOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> sampleParamOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(sampleParamOrderIdList, "orderId");

        List<EmcOrderTaskBean> sampleParamOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID");

        List<Long> sampleParamOrderTaskIdList = sampleParamOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> sampleParamOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(sampleParamOrderTaskIdList, "ORDERTASKID");

        mobileDTO.setFolderList(folderList);
        mobileDTO.setOrderList(orderList);
        mobileDTO.setOrderTaskList(orderTaskList);
        mobileDTO.setOrderTaskResultList(orderTaskResultList);
        mobileDTO.setProjectFileList(projectFileList);
        mobileDTO.setOrderItemGroupList(orderItemGroupList);
        mobileDTO.setSampleList(sampleList);
        mobileDTO.setSampleParamFolderList(sampleParamFolderList);
        mobileDTO.setSampleParamOrderList(sampleParamOrderList);
        mobileDTO.setSampleParamOrderItemGroupList(sampleParamOrderItemGroupList);
        mobileDTO.setSampleParamOrderTaskList(sampleParamOrderTaskList);
        mobileDTO.setSampleParamOrderTaskResultList(sampleParamOrderTaskResultList);

        return mobileDTO;
    }

    @Override
    public EmcMobilePaginationDTO selectProjectDataByPagination(Long id, RestJsonWrapperBean wrapper) {
        String operationTypeStr = wrapper.getParamValue("operationType");
        EmcMobilePaginationDTO mobileDTO = new EmcMobilePaginationDTO();
        if (StringUtils.isEmpty(operationTypeStr)) {
            mobileDTO.setMessage(I18nHelper.getMessage("EMC.MODULE.BP.MOBILE.OPERATIONTYPE.TIP.IS_EMPTY"));
            return mobileDTO;
        }

        String userId = LocalContextHelper.getLoginUserId();
        RestJsonWrapperBean folderWrapper = new RestJsonWrapperBean();
        folderWrapper.setFilterValue("projId_SEQ", id);
        folderWrapper.setFilterValue("sampleUserId", userId);
        folderWrapper.setFilterValue("sceneUserId", userId);
        folderWrapper.setFilterValue("targetType", "padSource");
        folderWrapper.setFilterValue("projectstatus_DIFFER", EmcBizConstant.PROJECT_STATUS_CANCEL);
        folderWrapper.setFilterValue("status_SEQ", EmcBizConstant.FOLDER_STATUS_DRAW);
        folderWrapper.setFilterValue("processStatus_SEQ", EmcBizConstant.PROJECT_PROCESSSTATUS_DONE);
        folderWrapper.setParamValue("n", "1");
        folderWrapper.setParamValue("s", "1000");

        // 点位下载全部数据
        Page<EmcFolderBean> folderPage = folderService.selectPaginationByFilter(SearchFilter.instance(), folderWrapper);
        List<Long> folderIdList = folderPage.getRows().stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        //查询气的采样参数主业务数据
        RestJsonWrapperBean sampleParamFolderWrapper = new RestJsonWrapperBean();
        sampleParamFolderWrapper.setFilterValue("paramProjid_SEQ", id);
        sampleParamFolderWrapper.setParamValue("n", "1");
        sampleParamFolderWrapper.setParamValue("s", "1000");
        Page<EmcFolderBean> sampleParamFolderPage = folderService.selectPaginationByFilter(SearchFilter.instance(),sampleParamFolderWrapper);
        List<Long> sampleParamFolderIdList = sampleParamFolderPage.getRows().stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        switch(operationTypeStr) {
            case "folder" :
                mobileDTO.setFolderPage(folderPage);
                break;
            case "order" :
                Page<EmcOrderBean> orderPage = orderService.selectPagination(() -> orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId"), wrapper.extractPageRowBounds());
                mobileDTO.setOrderPage(orderPage);
                break;
            case "orderContainer" :
                Page<EmcOrderContainerBean> orderContainerPage = orderContainerService.selectPagination(() -> orderContainerService.getDao().selectListByOneColumnValues(folderIdList, "folderId"), wrapper.extractPageRowBounds());
                mobileDTO.setOrderContainerPage(orderContainerPage);
                break;
            case "sample" :
                Page<EmcSampleBean> samplePage = sampleService.selectPaginationByFilter(SearchFilter.instance().match("projId", id).filter(MatchPattern.EQ)
                        .match("createdById", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ)
                        .match("status", EmcBizConstant.SAMPLE_STATUS_DRAFT).filter(MatchPattern.EQ), wrapper);
                mobileDTO.setSamplePage(samplePage);
                break;
            case "orderTask" :
                //Page<EmcOrderTaskBean> orderTaskPage = orderTaskService.selectPagination(() -> orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId"), wrapper.extractPageRowBounds());
                Page<EmcOrderTaskBean> orderTaskPage = orderTaskService.selectPaginationByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR), wrapper);
                // 过滤掉已设置分包的数据//去除采样前分包
                List<EmcOrderTaskBean> validTaskList = orderTaskPage.getRows().stream()
                        .filter(task -> !StringUtils.equals("beforeSampling",task.getExt$Item("subpackageType"))).collect(Collectors.toList());
                orderTaskPage.setRows(validTaskList);
                mobileDTO.setOrderTaskPage(orderTaskPage);
                break;
            case "orderTaskResult" :
                Page<EmcOrderTaskResultBean> resultPage = folderIdList.isEmpty() ? new Pagination<>() : orderTaskResultService.selectPaginationByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR).match("analysisType", EmcBizConstant.ANALYSISCATEGORY_SAMPLING).filter(MatchPattern.SEQ), wrapper);
                mobileDTO.setOrderTaskResultPage(resultPage);
                break;
            case "orderItemGroup" :
                List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Collections.singletonList("id"));
                List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                Page<EmcOrderItemGroupBean> orderItemGroupPage = orderItemGroupService.selectPagination(() -> orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId"), wrapper.extractPageRowBounds());
                mobileDTO.setOrderItemGroupPage(orderItemGroupPage);
                break;
            case "orderItem" :
                List<EmcOrderBean> curOrderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Collections.singletonList("id"));
                List<Long> curOrderIdList = curOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                mobileDTO.setOrderItemPage(orderItemService.selectPagination(() -> orderItemService.getDao().selectListByOneColumnValues(curOrderIdList, "orderId"), wrapper.extractPageRowBounds()));
                break;
            case "projectFile" :
                String targetId = projectService.getDao().getTable() + "$" + id;
                mobileDTO.setProjectFilePage(fileService.selectPagination(() -> fileService.getDao().selectListByOneColumnValue(targetId, "TARGETID"), wrapper.extractPageRowBounds()));
                break;
            case "propertyData" :
                List<EmcSampleBean> sampleList = sampleService.selectListByFilter(SearchFilter.instance().match("projId", id).filter(MatchPattern.EQ)
                        .match("createdById", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ)
                        .match("status", EmcBizConstant.SAMPLE_STATUS_DRAFT).filter(MatchPattern.EQ));
                List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
                mobileDTO.setPropertyDataPage(propertyDataService.selectPagination(() -> propertyDataService.getDao().selectListByOneColumnValues(sampleIdList, "bizId"), wrapper.extractPageRowBounds()));
                break;
            //气的采样参数平板数据下载
            case "sampleParamFolder" :
                mobileDTO.setSampleParamFolderPage(sampleParamFolderPage);
                break;
            case "sampleParamOrder" :
                Page<EmcOrderBean> sampleParamOrderPage = orderService.selectPagination(() -> orderService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "folderId"), wrapper.extractPageRowBounds());
                mobileDTO.setSampleParamOrderPage(sampleParamOrderPage);
                break;
            case "sampleParamOrderItemGroup" :
                List<EmcOrderBean> sampleParamOrderList = orderService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID");
                List<Long> sampleParamOrderIdList = sampleParamOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                Page<EmcOrderItemGroupBean> sampleParamOrderItemGroupPage = orderItemGroupService.selectPagination(() -> orderItemGroupService.getDao().selectListByOneColumnValues(sampleParamOrderIdList, "orderId"), wrapper.extractPageRowBounds());
                mobileDTO.setSampleParamOrderItemGroupPage(sampleParamOrderItemGroupPage);
                break;
            case "sampleParamOrderTask" :
                Page<EmcOrderTaskBean> sampleParamOrderTaskPage = orderTaskService.selectPagination(() -> orderTaskService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID"), wrapper.extractPageRowBounds());
                mobileDTO.setSampleParamOrderTaskPage(sampleParamOrderTaskPage);
                break;
            case "sampleParamOrderTaskResult" :
                Page<EmcOrderTaskResultBean> sampleParamOrderTaskResultPage = orderTaskResultService.selectPagination(() -> orderTaskResultService.getDao().selectListByOneColumnValues(sampleParamFolderIdList, "FOLDERID"), wrapper.extractPageRowBounds());
                mobileDTO.setSampleParamOrderTaskResultPage(sampleParamOrderTaskResultPage);
                break;
            default:
                break;
        }

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
        String operationTypeStr = wrapper.getParamValue("operationType");
        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        if (StringUtils.isEmpty(operationTypeStr)) {
            mobileDTO.setMessage(I18nHelper.getMessage("EMC.MODULE.BP.MOBILE.OPERATIONTYPE.TIP.IS_EMPTY"));
            return mobileDTO;
        }

        EmcOperationType operationType = EmcOperationType.getOperationType(operationTypeStr);
        if (operationType == null) {
            mobileDTO.setMessage(I18nHelper.getMessage("EMC.MODULE.BP.MOBILE.OPERATIONTYPE.TIP.NOT_EXIST"));
            return mobileDTO;
        }
        switch (operationType) {
        case EQUIPT:
            mobileDTO.setEquiptList(equiptService.selectListByFilter(SearchFilter.instance().match("STATUS", "inUse").filter(MatchPattern.EQ)));
            break;
        case EQUIPT_TEST_METHOD:
            List<EmcEquiptTestMethodBean> equiptTestMethodList = equiptTestMethodService.selectListByFilter(SearchFilter.instance());
            mobileDTO.setEquiptTestMethodList(equiptTestMethodList);
            break;
        case EMP:
            mobileDTO.setEmpList(empService.selectListByFilter(SearchFilter.instance()));
            break;
        case PROPERTY_FIELD:
            mobileDTO.setPropertyFieldList(propertyFieldService.selectAll());
            break;
        case MONITOR_TMPL:
            mobileDTO.setMonitorTmplList(monitorTmplService.selectAll());
            break;
        case CORE_CODE:
            mobileDTO.setCodeList(codeService.selectAll());
            break;
        case ITEM_RESULT:
            mobileDTO.setItemResultList(itemResultService.selectAll());
            break;
        case MONITOR_POINT:
            mobileDTO.setMonitorPointList(monitorPointService.selectListByFilter(SearchFilter.instance()));
            break;
        case CLIENT:
            mobileDTO.setClientList(clientService.selectAll());
            break;
        case MONITOR_TMPL_GROUP:
            mobileDTO.setMonitorTmplGroupList(monitorTmplGroupService.selectAll());
            break;
        case MONITOR_SCHEME:
            mobileDTO.setMonitorSchemeList(monitorSchemeService.selectAll());
            break;
        case TEST:
            mobileDTO.setTestList(testService.selectAll());
            break;
        case TEST_CATEGORY:
            mobileDTO.setTestCategoryList(testCategoryService.selectAll());
            break;
        case TEST_METHOD:
            mobileDTO.setTestMethodList(testMethodService.selectAll());
            break;
        case METHOD:
            mobileDTO.setMethodList(methodService.selectAll());
            break;
        case ITEM:
            mobileDTO.setItemList(itemService.selectAll());
            break;
        case ITEMGROUP:
            mobileDTO.setItemGroupList(itemGroupService.selectAll());
            break;
        case GROUPITEM:
            mobileDTO.setGroupItemList(groupItemService.selectAll());
            break;
        case CONTAINER:
            mobileDTO.setContainerCategoryBeanList(containerCategoryService.selectAll());
            break;
        case CALIBRATION:
            LocalDate startDate = DateTimeUtils.plusDays(LocalDate.now(), -7L);
            List<EmcEquiptCalibrationBean> calibrationList = calibrationService.selectListByFilter(SearchFilter.instance().match("CALIBRATIONDATE", startDate).filter(MatchPattern.DGOE));
            List<Long> calibrationIdList = calibrationList.stream().map(EmcEquiptCalibrationBean::getId).collect(Collectors.toList());
            List<EmcEquiptCalibrationLineBean> calibrationLineList = calibrationLineService.getDao().selectListByOneColumnValues(calibrationIdList, "CALIBRATIONID");
            mobileDTO.setCalibrationList(calibrationList);
            mobileDTO.setCalibrationLineList(calibrationLineList);
            break;
        case EQUIPTCATEGORY:
            mobileDTO.setEquiptCategoryList(equiptCategoryService.selectAll());
            break;
        case UNIT:
            mobileDTO.setUnitList(unitService.selectAll());
            break;
        case QC_CATEGORY:
            List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByOneColumnValue("scene", "USECATEGORY");
            mobileDTO.setQcCategoryList(qcCategoryList);
            break;
        case TEST_ORDER_CATEGORY:
            List<EmcTestMethodOrderCategoryBean> testOrderCategoryList = orderCategoryService.selectAll();
            mobileDTO.setTestOrderCategoryList(testOrderCategoryList);
            default:
            break;
        }

        return mobileDTO;
    }

    @Override
    public EmcMobilePaginationDTO selectStaticDataByPagination(RestJsonWrapperBean wrapper) {
        String operationTypeStr = wrapper.getParamValue("operationType");
        EmcMobilePaginationDTO mobileDTO = new EmcMobilePaginationDTO();
        if (StringUtils.isEmpty(operationTypeStr)) {
            mobileDTO.setMessage(I18nHelper.getMessage("EMC.MODULE.BP.MOBILE.OPERATIONTYPE.TIP.IS_EMPTY"));
            return mobileDTO;
        }

        EmcOperationType operationType = EmcOperationType.getOperationType(operationTypeStr);
        if (operationType == null) {
            mobileDTO.setMessage(I18nHelper.getMessage("EMC.MODULE.BP.MOBILE.OPERATIONTYPE.TIP.NOT_EXIST"));
            return mobileDTO;
        }

        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        switch (operationType) {
            case EQUIPT:
                mobileDTO.setEquiptPage(equiptService.selectPaginationByFilter(SearchFilter.instance().match("STATUS", "inUse").filter(MatchPattern.EQ).match("orgId", parentOrgId).filter(MatchPattern.SB), wrapper));
                break;
            case EQUIPT_TEST_METHOD:
                Page<EmcEquiptTestMethodBean> equiptTestMethodPage = equiptTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ), wrapper);
                mobileDTO.setEquiptTestMethodPage(equiptTestMethodPage);
                break;
            case EMP:
                mobileDTO.setEmpPage(empService.selectPaginationByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SB), wrapper));
                break;
            case PROPERTY_FIELD:
                mobileDTO.setPropertyFieldPage(propertyFieldService.selectPagination(() -> propertyFieldService.getDao().selectList(new EmcPropertyFieldBean()), wrapper.extractPageRowBounds()));
                break;
            case MONITOR_TMPL:
                mobileDTO.setMonitorTmplPage(monitorTmplService.selectPaginationByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SB), wrapper));
                break;
            case CORE_CODE:
                mobileDTO.setCoreCodePage(codeService.selectPagination(() -> codeService.getDao().selectList(new CoreCodeBean()), wrapper.extractPageRowBounds()));
                break;
            case ITEM_RESULT:
                mobileDTO.setItemResultPage(itemResultService.selectPagination(() -> itemResultService.getDao().selectList(new EmcItemResultBean()), wrapper.extractPageRowBounds()));
                break;
            case MONITOR_POINT:
                mobileDTO.setMonitorPointPage(monitorPointService.selectPagination(() -> monitorPointService.getDao().selectList(new EmcMonitorPointBean()), wrapper.extractPageRowBounds()));
                break;
            case CLIENT:
                mobileDTO.setClientPage(clientService.selectPagination(() -> clientService.getDao().selectList(new EmcClientBean()), wrapper.extractPageRowBounds()));
                break;
            case MONITOR_TMPL_GROUP:
                mobileDTO.setMonitorTmplGroupPage(monitorTmplGroupService.selectPagination(() -> monitorTmplGroupService.getDao().selectList(new EmcMonitorTmplGroupBean()), wrapper.extractPageRowBounds()));
                break;
            case MONITOR_SCHEME:
                List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SB));
                List<String> monitorTmplIdList = monitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
                mobileDTO.setMonitorSchemePage(monitorSchemeService.selectPaginationByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR), wrapper));
                break;
            case TEST:
                mobileDTO.setTestPage(testService.selectPagination(() -> testService.getDao().selectList(new EmcTestBean()), wrapper.extractPageRowBounds()));
                break;
            case TEST_CATEGORY:
                mobileDTO.setTestCategoryPage(testCategoryService.selectPagination(() -> testCategoryService.getDao().selectList(new EmcTestCategoryBean()), wrapper.extractPageRowBounds()));
                break;
            case TEST_METHOD:
                mobileDTO.setTestMethodPage(testMethodService.selectPagination(() -> testMethodService.getDao().selectList(new EmcTestMethodBean()), wrapper.extractPageRowBounds()));
                break;
            case METHOD:
                mobileDTO.setMethodPage(methodService.selectPagination(() -> methodService.getDao().selectList(new EmcMethodBean()), wrapper.extractPageRowBounds()));
                break;
            case ITEM:
                Page<EmcItemBean> itemPage = itemService.selectPagination(() -> itemService.getDao().selectList(new EmcItemBean()), wrapper.extractPageRowBounds());
                mobileDTO.setItemPage(itemPage);
                break;
            case ITEMGROUP:
                mobileDTO.setItemGroupPage(itemGroupService.selectPagination(() -> itemGroupService.getDao().selectList(new EmcItemGroupBean()), wrapper.extractPageRowBounds()));
                break;
            case GROUPITEM:
                mobileDTO.setGroupItemPage(groupItemService.selectPagination(() -> groupItemService.getDao().selectList(new EmcGroupItemBean()), wrapper.extractPageRowBounds()));
                break;
            case CONTAINER:
                mobileDTO.setContainerPage(containerCategoryService.selectPagination(() -> containerCategoryService.getDao().selectList(new EmcContainerCategoryBean()), wrapper.extractPageRowBounds()));
                break;
            case CALIBRATION:
                LocalDate startDate = DateTimeUtils.plusDays(LocalDate.now(), -7L);
                Page<EmcEquiptCalibrationBean> calibrationPage = calibrationService.selectPaginationByFilter(SearchFilter.instance().match("CALIBRATIONDATE", startDate).filter(MatchPattern.DGOE), wrapper);
                List<Long> calibrationIdList = calibrationPage.getRows().stream().map(EmcEquiptCalibrationBean::getId).collect(Collectors.toList());
                List<EmcEquiptCalibrationLineBean> calibrationLineList = calibrationLineService.getDao().selectListByOneColumnValues(calibrationIdList, "CALIBRATIONID");
                mobileDTO.setCalibrationPage(calibrationPage);
                mobileDTO.setCalibrationLinePage(new Pagination<>(calibrationLineList));
                break;
            case EQUIPTCATEGORY:
                mobileDTO.setEquiptCategoryPage(equiptCategoryService.selectPagination(() -> equiptCategoryService.getDao().selectList(new EmcEquiptCategoryBean()), wrapper.extractPageRowBounds()));
                break;
            case UNIT:
                mobileDTO.setUnitPage(unitService.selectPagination(() -> unitService.getDao().selectList(new EmcUnitBean()), wrapper.extractPageRowBounds()));
                break;
            case QC_CATEGORY:
                mobileDTO.setQcCategoryPage(qcCategoryService.selectPagination(() -> qcCategoryService.getDao().selectListByOneColumnValue("scene", "USECATEGORY"), wrapper.extractPageRowBounds()));
                break;
            case TEST_ORDER_CATEGORY:
                mobileDTO.setTestOrderCategoryPage(orderCategoryService.selectPagination(() -> orderCategoryService.getDao().selectList(new EmcTestMethodOrderCategoryBean()), wrapper.extractPageRowBounds()));
                break;
            case TEST_CAPACITY:
                mobileDTO.setTestCapacityPage(testCapacityService.selectPagination(() -> testCapacityService.getDao().selectListByOneColumnValue(parentOrgId, "orgId"), wrapper.extractPageRowBounds()));
                break;
            case ACQ_SCRIPT:
                mobileDTO.setAcqScriptPage(acqScriptService.selectPaginationByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ).match("source", "sampling").filter(MatchPattern.SEQ), wrapper));
                break;
            case EAS_PARAM:
                mobileDTO.setEasParamPage(easParamService.selectPagination(wrapper));
                break;
            case EAS_PRE_ANALY:
                mobileDTO.setEasPreAnalyPage(easPreAnalyService.selectPagination(wrapper));
                break;
            case EAS_RESULT:
                mobileDTO.setEasResultPage(easResultService.selectPagination(wrapper));
                break;
            case EQUIPT_TEST_ITEM:
                List<EmcEquiptAcqScriptBean> acqScriptList = acqScriptService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ).match("source", "sampling").filter(MatchPattern.SEQ));
                List<Long> acqScriptIdList = acqScriptList.stream().map(EmcEquiptAcqScriptBean::getId).collect(Collectors.toList());
                List<EmcEquiptBean> equiptList = equiptService.getDao().selectListByOneColumnValues(acqScriptIdList, "acqScriptId", Collections.singletonList("id"));
                List<Long> acqEquiptIdList = equiptList.stream().map(EmcEquiptBean::getId).collect(Collectors.toList());
                mobileDTO.setEquiptTestItemPage(acqEquiptIdList.isEmpty() ? new Pagination<>() : equiptTestItemService.selectPaginationByFilter(SearchFilter.instance().match("equiptId", acqEquiptIdList).filter(MatchPattern.OR), wrapper));
                break;
            default:
                break;
        }

        return mobileDTO;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public EmcMobileDTO uploadProjectInfo(RestJsonWrapperBean wrapper) {
        EmcMobileDTO mobileDTO = parse(wrapper);
        if (mobileDTO == null) {
            return new EmcMobileDTO();
        }

        List<EmcSampleBean> sampleList = mobileDTO.getSampleList();
        List<EmcOrderBean> orderList = mobileDTO.getOrderList();

        if (sampleList.isEmpty() || orderList.isEmpty()) {
            return new EmcMobileDTO();
        }

        // 平板端一次只会上传一个采样单，对于平板端重复上传的情况，如果采样单不在现场采样阶段即status为draft，则直接忽略掉
        List<String> sampleMobileIdList = sampleList.stream().map(EmcSampleBean::getMobileId).collect(Collectors.toList());
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcSampleBean> existSampleList = sampleService.getDao().selectListByOneColumnValues(sampleIdList, "id", Arrays.asList("ID", "STATUS"));
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

            List<String> existMobileIdList = sampleService.getDao().selectListByIds(relatedSampleIdList, Arrays.asList("id", "mobileId")).stream().map(EmcSampleBean::getMobileId).collect(Collectors.toList());
            // 如果重复上传时，样品关联的采样单不是之前的采样单，则直接忽略
            if (!sampleMobileIdList.isEmpty() && !existMobileIdList.isEmpty() && !StringUtils.equals(sampleMobileIdList.get(0), existMobileIdList.get(0))) {
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
        // 不更新项目数据
        /*updateProject(mobileDTO, projectIdMap);*/

        Map<Long, EmcFolderBean> folderMap = new HashMap<>();
        updateFolder(mobileDTO, projectIdMap, folderMap);

        Map<Long, Long> calibrationIdMap = new HashMap<>();

        Map<Long, Long> sampleIdMap = new HashMap<>();
        List<EmcSampleBean> newSampleList = updateSample(mobileDTO, projectIdMap, sampleIdMap, calibrationIdMap);

        updateCalibration(mobileDTO, calibrationIdMap, sampleIdMap);

        Map<Long, EmcOrderBean> orderMap = new HashMap<>();
        updateOrder(mobileDTO, sampleIdMap, folderMap, orderMap);

        Map<Long, EmcOrderTaskBean> orderTaskMap = new HashMap<>();
        updateOrderTask(mobileDTO, orderMap, orderTaskMap);

        updateOrderTaskResult(mobileDTO, orderMap, orderTaskMap);

        //更新气的采样参数数据
        updateSampleParamData(mobileDTO,projectIdMap);

        updateEquiptRequisition(mobileDTO, sampleIdMap, calibrationIdMap);

        EmcMobileDTO resultMobileDTO = new EmcMobileDTO();
        resultMobileDTO.setSampleList(newSampleList);

        return resultMobileDTO;
    }

    /**
     * 因为采样参数的数据差异，新建一个单独的方法更新采样参数
     *
     * @param mobileDTO
     * @param projectIdMap
     */
    public void updateSampleParamData(EmcMobileDTO mobileDTO,Map<Long, Long> projectIdMap) {
        Map<Long, EmcFolderBean> folderMap = new HashMap<>();

        //平板不涉及采样参数点位的新增,只存在更新操作，调用核心方法即可
        updateFolder(mobileDTO, projectIdMap, folderMap, Constant.YES);

        //采样参数样涉及新增、更新,且更新的字段内容没有业务数据那么多，单独新建一个单独的方法
        Map<Long, EmcOrderBean> orderMap = new HashMap<>();
        updateSampleParamOrder(mobileDTO, folderMap, orderMap);

        //与核心方法需求一致，调用核心方法即可
        updateOrderItemGroup(mobileDTO , orderMap, Constant.YES);

        //与核心方法需求一致，调用核心方法即可
        Map<Long, EmcOrderTaskBean> orderTaskMap = new HashMap<>();
        updateOrderTask(mobileDTO, orderMap, orderTaskMap, Constant.YES);

        //与核心方法需求一致，调用核心方法即可
        updateOrderTaskResult(mobileDTO, orderMap, orderTaskMap, Constant.YES);
    }

    /**
     * 更新采样参数的样品分析项组信息
     * @param mobileDTO
     * @param orderMap
     */
    protected void updateOrderItemGroup(EmcMobileDTO mobileDTO, Map<Long, EmcOrderBean> orderMap,String ... sampleParamFlag) {
        List<EmcOrderItemGroupBean> mobileSampleParamOrderItemGroupList = sampleParamFlag.length == 0 ? mobileDTO.getOrderItemGroupList() : mobileDTO.getSampleParamOrderItemGroupList();
        if (mobileSampleParamOrderItemGroupList == null || mobileSampleParamOrderItemGroupList.isEmpty()) {
            return;
        }

        //padWorkable  active正常的 unactive删除的
        List<EmcOrderItemGroupBean> deleteList = mobileSampleParamOrderItemGroupList.stream()
                .filter(p -> !StringUtils.isEmpty(p.getExt$Item("padworkable"))
                        && ObjectUtils.equals(p.getExt$Item("padworkable"), "unactive"))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteList)) {
            mobileSampleParamOrderItemGroupList.removeAll(deleteList);
//            orderItemGroupService.getDao().deleteBy(deleteList, "ID");

            List<Long> deleteIdList = new ArrayList<>();
            //mobileId不为空，是平板新增的数据，删除用MobileId来删除。MobileId为空是pc端新增的数据，删除用id删除
            List<String> deleteMobileIdList = deleteList.stream().filter(e -> !ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderItemGroupBean :: getMobileId).distinct().collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(deleteMobileIdList)){
                List<Long> idByMobileIdList = orderItemGroupService.getDao().selectListByOneColumnValues(deleteMobileIdList,"MOBILEID").
                        stream().map(EmcOrderItemGroupBean :: getId).collect(Collectors.toList());
                deleteIdList.addAll(idByMobileIdList);
            }

            List<Long> nullMobileIdList = deleteList.stream().filter(e -> ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderItemGroupBean :: getId).distinct().collect(Collectors.toList());
            deleteIdList.addAll(nullMobileIdList);

            orderItemGroupService.getDao().deleteByIdList(deleteIdList);
        }

        // 区分需要更新或是新增的样品分析项组，mobileId 为空即表示为待更新的样品分析项组
        List<EmcOrderItemGroupBean> allUpdateOrderItemGroupList = mobileSampleParamOrderItemGroupList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        List<Long> allUpdateOrderItemGroupIdList = allUpdateOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> existUpdateOrderItemGroupList = orderItemGroupService.getDao().selectListByIds(allUpdateOrderItemGroupIdList, Collections.singletonList("ID"));
        List<Long> existUpdateOrderItemGroupIdList = existUpdateOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
        // 过滤掉平板上存在但系统中不存在的样品分析项组
        List<EmcOrderItemGroupBean> updateOrderItemGroupList = allUpdateOrderItemGroupList.stream().filter(group -> existUpdateOrderItemGroupIdList.contains(group.getId())).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> varOrderItemGroupList = mobileSampleParamOrderItemGroupList.stream().filter(f -> !StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        if (varOrderItemGroupList.isEmpty()) {
            orderItemGroupService.getDao().updateIfChanged(updateOrderItemGroupList);
            return;
        }

        List<String> mobileIdList = varOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getMobileId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> existOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getMobileId).collect(Collectors.toList());

        // 处理重复上传平板端复制或添加的样品测试
        varOrderItemGroupList.forEach(mobileOrderItemGroup -> existOrderItemGroupList.stream().filter(existOrderItemGroup -> StringUtils.equals(existOrderItemGroup.getMobileId(), mobileOrderItemGroup.getMobileId())).findFirst().ifPresent(existOrderItemGroup -> {
            mobileOrderItemGroup.setId(existOrderItemGroup.getId());
            mobileOrderItemGroup.setOrderId(orderMap.get(mobileOrderItemGroup.getOrderId()).getId());
            mobileOrderItemGroup.setQcCategoryId(existOrderItemGroup.getQcCategoryId());
            mobileOrderItemGroup.setQcCategoryCode(existOrderItemGroup.getQcCategoryCode());
            mobileOrderItemGroup.setQcName(existOrderItemGroup.getQcName());
        }));

        updateOrderItemGroupList.addAll(varOrderItemGroupList.stream().filter(f -> existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList()));
        List<EmcOrderItemGroupBean> mobileNewOrderItemGroupList = varOrderItemGroupList.stream().filter(f -> !existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList());
        if (mobileNewOrderItemGroupList.isEmpty()) {
            orderItemGroupService.getDao().updateIfChanged(updateOrderItemGroupList);
            return;
        }

        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        mobileNewOrderItemGroupList.stream().forEach(t -> {
            EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
            BeanUtils.copyProperties(t, newOrderItemGroup);
            if (!orderMap.isEmpty()) {
                EmcOrderBean newOrder = orderMap.get(t.getOrderId());
                if (newOrder != null) {
                    newOrderItemGroup.setOrderId(newOrder.getId());
                    newOrderItemGroup.setQcCategoryId(newOrder.getQcCategoryId());
                    newOrderItemGroup.setQcCategoryCode(newOrder.getQcCategoryCode());
                    newOrderItemGroup.setQcName(newOrder.getQcName());
                }
            }
            newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());

            newOrderItemGroupList.add(newOrderItemGroup);
        });

        orderItemGroupService.getDao().insert(newOrderItemGroupList);
        orderItemGroupService.getDao().updateIfChanged(updateOrderItemGroupList);
    }

    /**
     * 更新采样参数的样品信息
     * @param mobileDTO
     * @param folderMap
     * @param orderMap
     */
    protected void updateSampleParamOrder(EmcMobileDTO mobileDTO, Map<Long, EmcFolderBean> folderMap, Map<Long, EmcOrderBean> orderMap) {
        List<EmcOrderBean> mobileOrderList = mobileDTO.getSampleParamOrderList();
        if (mobileOrderList == null || mobileOrderList.isEmpty()) {
            return;
        }

        //padWorkable  active正常的 unactive删除的
        List<EmcOrderBean> deleteList = mobileOrderList.stream()
                .filter(p -> !StringUtils.isEmpty(p.getExt$Item("padworkable"))
                        && ObjectUtils.equals(p.getExt$Item("padworkable"), "unactive"))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteList)) {
            mobileOrderList.removeAll(deleteList);
//            orderService.getDao().deleteBy(deleteList, "ID");
            List<Long> deleteIdList = new ArrayList<>();
            //mobileId不为空，是平板新增的数据，删除用MobileId来删除。MobileId为空是pc端新增的数据，删除用id删除
            List<String> deleteMobileIdList = deleteList.stream().filter(e -> !ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderBean :: getMobileId).distinct().collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(deleteMobileIdList)){
                List<Long> idByMobileIdList = orderService.getDao().selectListByOneColumnValues(deleteMobileIdList,"MOBILEID").
                        stream().map(EmcOrderBean :: getId).collect(Collectors.toList());
                deleteIdList.addAll(idByMobileIdList);
            }

            List<Long> nullMobileIdList = deleteList.stream().filter(e -> ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
            deleteIdList.addAll(nullMobileIdList);

            orderService.getDao().deleteByIdList(deleteIdList);
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
            updateFolderStatus(updateOrderList);
            orderService.getDao().updateIfChanged(updateOrderList);
            return;
        }

        Map<String, String> oldNewOrderNoMap = new HashMap<>();

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        for (int i = 0; i < mobileNewOrderList.size(); i++) {
            EmcOrderBean oldOrder = mobileNewOrderList.get(i);
            EmcOrderBean newOrder = new EmcOrderBean();
            BeanUtils.copyProperties(oldOrder, newOrder);
            PersistableHelper.resetBasicProperties(newOrder);

            if (!folderMap.isEmpty()) {
                EmcFolderBean newFolder = folderMap.get(oldOrder.getFolderId());
                if (newFolder != null) {
                    newOrder.setFolderId(newFolder.getId());
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

        updateFolderStatus(updateOrderList);

        orderService.getDao().insert(newOrderList);
        orderService.getDao().updateIfChanged(updateOrderList);
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
        } else if (FOLDER_FILE.equals(bizCategory)) {
            // 点位附件
            if (StringUtils.isEmpty(mobileId)) {
                newFile.setTargetId(folderService.getDao().getTable() + "$" + mobileFile.getTargetId());
            } else {
                List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValue(mobileId, "MOBILEID");
                if (!folderList.isEmpty()) {
                    newFile.setTargetId(folderService.getDao().getTable() + "$" + folderList.get(0).getId());
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
    @Override
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
    @Override
    protected void updateFolder(EmcMobileDTO mobileDTO, Map<Long, Long> projectIdMap, Map<Long, EmcFolderBean> folderMap,String ... sampleParamFlag) {
        List<EmcFolderBean> mobileFolderList = sampleParamFlag.length == 0 ? mobileDTO.getFolderList() : mobileDTO.getSampleParamFolderList();
        if (mobileFolderList == null || mobileFolderList.isEmpty()) {
            return;
        }

        //padWorkable  active正常的 unactive删除的
        List<EmcFolderBean> deleteList = mobileFolderList.stream()
                .filter(p -> !StringUtils.isEmpty(p.getExt$Item("padworkable"))
                        && ObjectUtils.equals(p.getExt$Item("padworkable"), "unactive"))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteList)) {
            mobileFolderList.removeAll(deleteList);
            folderService.getDao().deleteBy(deleteList, "ID");
        }

        // mobileId 为空即表示为待更新的点位
        List<EmcFolderBean> updateFolderList = mobileFolderList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());

        // 查询已完成采样的点位，对于已完成的点位重复上传不再更新信息
        // TODO 修改流程状态了可能数据筛选不准确
        List<Long> folderIdList = mobileFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        // 这里通过状态来查询已完成采样的点位是有问题的，应该根据点位下的所有样品是否都有采样单来判断
        /*List<EmcFolderBean> completedFolderList = folderService.getDao().selectListByIds(folderIdList).stream()
                .filter(e -> EmcBizConstant.FOLDER_STATUS_DRAW.equals(e.getStatus()) || EmcBizConstant.FOLDER_STATUS_DONE.equals(e.getStatus())).collect(Collectors.toList());*/

        List<Long> completedFolderIdList = new ArrayList<>();
        List<EmcOrderBean> folderOrderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", Arrays.asList("ID", "SAMPLEID"));
        folderOrderList.stream().filter(o -> !ObjectUtils.isEmpty(o.getFolderId())).collect(Collectors.groupingBy(EmcOrderBean::getFolderId)).forEach((k, v) -> {
            if (v.stream().noneMatch(o -> ObjectUtils.isEmpty(o.getSampleId()))) {
                completedFolderIdList.add(k);
            }
        });
        List<EmcFolderBean> completedFolderList = folderService.getDao().selectListByIds(completedFolderIdList);

        List<EmcFolderBean> varFolderList = mobileFolderList.stream().filter(f -> !StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        if (varFolderList.isEmpty()) {
            folderService.getDao().update(filterCompletedFolder(updateFolderList, completedFolderList));
            return;
        }

        List<String> mobileIdList = varFolderList.stream().map(EmcFolderBean::getMobileId).collect(Collectors.toList());
        List<EmcFolderBean> existFolderList = folderService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existFolderList.stream().map(EmcFolderBean::getMobileId).collect(Collectors.toList());

        updateFolderList.addAll(varFolderList.stream().filter(f -> existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList()));
        List<EmcFolderBean> mobileNewFolderList = varFolderList.stream().filter(f -> !existMobileIdList.contains(f.getMobileId())).collect(Collectors.toList());
        if (mobileNewFolderList.isEmpty()) {
            folderService.getDao().update(filterCompletedFolder(updateFolderList, completedFolderList));
            return;
        }

        List<EmcFolderBean> newFolderList = new ArrayList<>();
        int newFolderSize = mobileNewFolderList.size();
        Long projectId = mobileNewFolderList.get(0).getProjId();
        EmcProjectBean project = projectService.getDao().selectById(projectId);
        List<String> newFolderNoList = folderService.generateFolderNosByProjId(projectId, project.getProjNo(), mobileNewFolderList.get(0).getMonitorTmplId(), newFolderSize);

        for (int i = 0; i < newFolderSize; i++) {
            EmcFolderBean folderBean = mobileNewFolderList.get(i);
            EmcFolderBean newFolder = new EmcFolderBean();
            BeanUtils.copyProperties(folderBean, newFolder);
            if (!projectIdMap.isEmpty()) {
                Long newProjectId = projectIdMap.get(folderBean.getProjId());
                if (newProjectId != null) {
                    newFolder.setProjId(newProjectId);
                }
            }
            newFolder.setId(ApplicationContextHelper.getNextIdentity());
            newFolder.setFolderNo(newFolderNoList.get(i));
            newFolder.setSource(SOURCE_FROM_PAD);
            newFolder.setProcessStatus("done");
            // 对于平板新增的点位，直接更改状态为已完成
            newFolder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);

            newFolderList.add(newFolder);
            folderMap.put(folderBean.getId(), newFolder);
        }

        folderService.getDao().insert(newFolderList);
        folderService.getDao().update(filterCompletedFolder(updateFolderList, completedFolderList));
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
    @Override
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

        if (mobileSampleList.isEmpty()) {
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
    @Override
    protected void updateOrder(EmcMobileDTO mobileDTO, Map<Long, Long> sampleIdMap, Map<Long, EmcFolderBean> folderMap, Map<Long, EmcOrderBean> orderMap) {
        List<EmcOrderBean> mobileOrderList = mobileDTO.getOrderList();
        if (mobileOrderList == null || mobileOrderList.isEmpty()) {
            return;
        }

        EmcOrderBean maxExtOrder = mobileOrderList.stream().max(Comparator.comparing(order -> order.getExt$().size())).orElseGet(EmcOrderBean::new);
        List<String> updateColNames = getUpdateColNames(maxExtOrder);

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
            orderService.getDao().update(updateOrderList, updateColNames);
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
            orderService.getDao().update(updateOrderList, updateColNames);
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
        orderService.getDao().update(updateOrderList, updateColNames);
    }

    protected void convertSampleId(List<EmcOrderBean> orderList, Map<Long, Long> sampleIdMap) {
        orderList.stream().forEach(o -> {
            Long newSampleId = sampleIdMap.get(o.getSampleId());
            if (newSampleId != null) {
                o.setSampleId(newSampleId);
            }
        });
    }

    protected void convertCalibrationId(List<EmcSampleBean> sampleList, Map<Long, Long> calibrationIdMap, List<CoreReportConfigLineBean> calibrationReportList) {
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

    protected void copyOrderItemGroup(List<EmcOrderBean> newOrderList, Map<String, Long> orderNoIdMap) {
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
    protected void updateFolderStatus(List<EmcOrderBean> orderList) {
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
    @Override
    protected void updateOrderTask(EmcMobileDTO mobileDTO, Map<Long, EmcOrderBean> orderMap, Map<Long, EmcOrderTaskBean> orderTaskMap,String ... sampleParamFlag) {
        List<EmcOrderTaskBean> orderTaskList = sampleParamFlag.length == 0 ? mobileDTO.getOrderTaskList() : mobileDTO.getSampleParamOrderTaskList();
        if (orderTaskList == null || orderTaskList.isEmpty()) {
            return;
        }
        //padWorkable  active正常的 unactive删除的
        List<EmcOrderTaskBean> deleteList = orderTaskList.stream()
                .filter(p -> !StringUtils.isEmpty(p.getExt$Item("padworkable"))
                        && ObjectUtils.equals(p.getExt$Item("padworkable"), "unactive"))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteList)) {
            orderTaskList.removeAll(deleteList);
//            orderTaskService.getDao().deleteBy(deleteList, "ID");
            List<Long> deleteIdList = new ArrayList<>();
            //mobileId不为空，是平板新增的数据，删除用MobileId来删除。MobileId为空是pc端新增的数据，删除用id删除
            List<String> deleteMobileIdList = deleteList.stream().filter(e -> !ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderTaskBean :: getMobileId).distinct().collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(deleteMobileIdList)){
                List<Long> idByMobileIdList = orderTaskService.getDao().selectListByOneColumnValues(deleteMobileIdList,"MOBILEID").
                        stream().map(EmcOrderTaskBean :: getId).collect(Collectors.toList());
                deleteIdList.addAll(idByMobileIdList);
            }

            List<Long> nullMobileIdList = deleteList.stream().filter(e -> ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderTaskBean :: getId).distinct().collect(Collectors.toList());
            deleteIdList.addAll(nullMobileIdList);

            orderTaskService.getDao().deleteByIdList(deleteIdList);
        }

        // 过滤出平板上删除的测试
        List<EmcOrderTaskBean> deletedOrderTaskList = orderTaskList.stream().filter(task -> StringUtils.equals(task.getExt$Item("padWorkable"), "unactive")).collect(Collectors.toList());
        List<Long> deletedOrderTaskIdList = deletedOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> deletedResultList = new ArrayList<>();
        deletedOrderTaskIdList.forEach(taskId -> {
            EmcOrderTaskResultBean deletedResult = new EmcOrderTaskResultBean();
            deletedResult.setOrderTaskId(taskId);
            deletedResultList.add(deletedResult);
        });
        orderTaskService.getDao().deleteByIdList(deletedOrderTaskIdList);
        orderTaskResultService.getDao().deleteBy(deletedResultList, "orderTaskId");

        // 平板上更新或新增的测试
        List<EmcOrderTaskBean> mobileOrderTaskList = orderTaskList.stream().filter(task -> !StringUtils.equals(task.getExt$Item("padWorkable"), "unactive")).collect(Collectors.toList());

        // 区分需要更新或是新增的样品测试，mobileId 为空即表示为待更新的样品测试
        List<EmcOrderTaskBean> allUpdateOrderTaskList = mobileOrderTaskList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        List<Long> allUpdateOrderTaskIdList = allUpdateOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> existUpdateOrderTaskList = orderTaskService.getDao().selectListByIds(allUpdateOrderTaskIdList, Arrays.asList("id", "subcontractorId"));

        // 过滤掉系统已设置分包，但是平板未及时同步的测试
        List<EmcOrderTaskBean> validUpdateTaskList = existUpdateOrderTaskList.stream().filter(task -> ObjectUtils.isEmpty(task.getSubcontractorId())).collect(Collectors.toList());

        List<Long> existUpdateOrderTaskIdList = validUpdateTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        // 过滤掉平板上存在但系统中不存在的样品测试
        List<EmcOrderTaskBean> updateOrderTaskList = allUpdateOrderTaskList.stream().filter(task -> existUpdateOrderTaskIdList.contains(task.getId())).collect(Collectors.toList());

        updateOrderTaskList.forEach(updateOrderTask -> orderTaskMap.put(updateOrderTask.getId(), updateOrderTask));

        List<EmcOrderTaskBean> varOrderTaskList = mobileOrderTaskList.stream().filter(f -> !StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        if (varOrderTaskList.isEmpty()) {
            orderTaskService.getDao().updateIfChanged(updateOrderTaskList);
            return;
        }

        List<String> mobileIdList = varOrderTaskList.stream().map(EmcOrderTaskBean::getMobileId).collect(Collectors.toList());
        List<EmcOrderTaskBean> existOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(mobileIdList, "MOBILEID");
        List<String> existMobileIdList = existOrderTaskList.stream().map(EmcOrderTaskBean::getMobileId).collect(Collectors.toList());

        // 处理重复上传平板端复制或添加的样品测试
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
                    newOrderTask.setEquiptAcquNo(newOrder.getOrderNo());
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

        // 新添加测试的分析项组
        List<Long> newOrderIdList = newOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<Long> newTestMethodIdList = newOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = mobileDTO.getOrderItemGroupList();
        ArrayList<EmcOrderItemGroupBean> uniqueItemGroupList = orderItemGroupList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(group -> group.getOrderId() + "$" + group.getTestMethodId() + "$" + group.getItemGroupId()))), ArrayList::new));

        List<EmcOrderItemGroupBean> newOrderItemGroupList = uniqueItemGroupList.stream().filter(group -> newOrderIdList.contains(group.getOrderId()) && newTestMethodIdList.contains(group.getTestMethodId())).collect(Collectors.toList());
        newOrderItemGroupList.forEach(group -> group.setId(ApplicationContextHelper.getNextIdentity()));

        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskService.getDao().updateIfChanged(updateOrderTaskList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
    }

    /**
     * 更新样品测试结果
     *
     * @param mobileDTO
     *            上传数据
     * @param orderTaskMap
     *            新样品测试
     */
    @Override
    protected void updateOrderTaskResult(EmcMobileDTO mobileDTO, Map<Long, EmcOrderBean> orderMap, Map<Long, EmcOrderTaskBean> orderTaskMap,String ... sampleParamFlag) {
        List<EmcOrderTaskResultBean> mobileOrderTaskResultList = sampleParamFlag.length == 0 ? mobileDTO.getOrderTaskResultList() : mobileDTO.getSampleParamOrderTaskResultList();
        if (mobileOrderTaskResultList == null || mobileOrderTaskResultList.isEmpty()) {
            return;
        }

        //padWorkable  active正常的 unactive删除的
        List<EmcOrderTaskResultBean> deleteList = mobileOrderTaskResultList.stream()
                .filter(p -> !StringUtils.isEmpty(p.getExt$Item("padworkable"))
                        && ObjectUtils.equals(p.getExt$Item("padworkable"), "unactive"))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteList)) {
            mobileOrderTaskResultList.removeAll(deleteList);
//            orderTaskResultService.getDao().deleteBy(deleteList, "ID");
            List<Long> deleteIdList = new ArrayList<>();
            //mobileId不为空，是平板新增的数据，删除用MobileId来删除。MobileId为空是pc端新增的数据，删除用id删除
            List<String> deleteMobileIdList = deleteList.stream().filter(e -> !ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderTaskResultBean :: getMobileId).distinct().collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(deleteMobileIdList)){
                List<Long> idByMobileIdList = orderTaskResultService.getDao().selectListByOneColumnValues(deleteMobileIdList,"MOBILEID").
                        stream().map(EmcOrderTaskResultBean :: getId).collect(Collectors.toList());
                deleteIdList.addAll(idByMobileIdList);
            }

            List<Long> nullMobileIdList = deleteList.stream().filter(e -> ObjectUtils.isEmpty(e.getMobileId()))
                    .map(EmcOrderTaskResultBean :: getId).distinct().collect(Collectors.toList());
            deleteIdList.addAll(nullMobileIdList);

            orderTaskResultService.getDao().deleteByIdList(deleteIdList);
        }

        // 区分需要更新或是新增的样品测试结果，mobileId 为空即表示为待更新的样品测试结果
        List<EmcOrderTaskResultBean> allUpdateOrderTaskResultList = mobileOrderTaskResultList.stream().filter(f -> StringUtils.isEmpty(f.getMobileId())).collect(Collectors.toList());
        List<Long> allUpdateResultIdList = allUpdateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> existUpdateResultList = orderTaskResultService.getDao().selectListByIds(allUpdateResultIdList, Collections.singletonList("ID"));
        List<Long> existUpdateResultIdList = existUpdateResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        // 过滤掉平板上存在但是系统中不存在的样品测试结果
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = allUpdateOrderTaskResultList.stream().filter(result -> existUpdateResultIdList.contains(result.getId())).collect(Collectors.toList());

        // 过滤掉系统已设置分包，但是平板端未及时同步的测试数据，这部分数据不更新
        List<Long> validTaskIdList = orderTaskMap.values().stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        updateOrderTaskResultList = updateOrderTaskResultList.stream().filter(r -> validTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());

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
            Optional.ofNullable(orderTaskMap.get(mobileTaskResult.getOrderTaskId())).ifPresent(task -> mobileTaskResult.setOrderTaskId(task.getId()));
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
    protected void updateCalibration(EmcMobileDTO mobileDTO, Map<Long, Long> calibrationIdMap, Map<Long, Long> sampleIdMap) {
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
            Long sampleId = sampleIdMap.get(Long.valueOf(cali.getExt$Item("sampleId")));
            newCalibration.setExt$Item("sampleId", ObjectUtils.isEmpty(sampleId) ? null : sampleId.toString());
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

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public EmcMobileDTO createFolder(RestJsonWrapperBean wrapper) {
        EmcFolderBean folder = parse(wrapper, EmcFolderBean.class);
        if (folder == null) {
            return new EmcMobileDTO();
        }

        // 查询点位信息
        EmcFolderBean selectedFolder = folderService.selectById(folder.getId());

        EmcFolderBean newFolder = new EmcFolderBean();
        BeanUtils.copyProperties(selectedFolder, newFolder);
        PersistableHelper.resetBasicProperties(newFolder);

        // 查询样品类型代码
        EmcMonitorTmplBean monitorTmpl = monitorTmplService.selectById(selectedFolder.getMonitorTmplId());

        Long newFolderId = ApplicationContextHelper.getNextIdentity();
        newFolder.setId(newFolderId);
        newFolder.setSource("COPY");
        Long projId = selectedFolder.getProjId();

        EmcProjectBean project = projectService.selectById(projId);
        String newFolderNo = folderService.generateFolderNo(project, selectedFolder.getMonitorTmplId());
        newFolder.setFolderNo(newFolderNo);

        // 查询对应点位下的样品信息，这里复制该点位下的所有样品，不过滤未关联采样单的样品
        List<EmcOrderBean> copiedOrderList = orderService.getDao().selectListByOneColumnValue(selectedFolder.getId(), "FOLDERID");
        //List<EmcOrderBean> copiedOrderList = folderOrderList.stream().filter(order -> ObjectUtils.isEmpty(order.getSampleId())).collect(Collectors.toList());
        List<Long> orderIdList = copiedOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        // 查询样品下的对应测试信息
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 查询样品下对应的结果信息
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 查询样品对应的分析项组
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        for (int i = 0; i < copiedOrderList.size(); i++) {
            EmcOrderBean oldOrder = copiedOrderList.get(i);
            EmcOrderBean newOrder = new EmcOrderBean();

            BeanUtils.copyProperties(oldOrder, newOrder, PfConstant.CREATED_COLUMNS);
            PersistableHelper.resetBasicProperties(newOrder);

            newOrder.setId(ApplicationContextHelper.getNextIdentity());
            newOrder.setFolderId(newFolder.getId());
            newOrder.setSampleId(null);

            newOrder.setOrderNo(newFolderNo + StringUtils.leftPad((i + 1) + "", 2));
            newOrder.setLabelNo(newOrder.getOrderNo());
            newOrderList.add(newOrder);

            for (EmcOrderTaskBean orderTask : orderTaskList) {
                if (oldOrder.getId().equals(orderTask.getOrderId())) {
                    EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                    BeanUtils.copyProperties(orderTask, newOrderTask);
                    PersistableHelper.resetBasicProperties(newOrderTask);

                    newOrderTask.setId(ApplicationContextHelper.getNextIdentity());
                    newOrderTask.setOrderId(newOrder.getId());
                    newOrderTask.setFolderId(newFolder.getId());
                    newOrderTask.setEquiptAcquNo(newOrder.getOrderNo());
                    newOrderTaskList.add(newOrderTask);

                    for (EmcOrderTaskResultBean orderTaskResult : orderTaskResultList) {
                        if (orderTask.getId().equals(orderTaskResult.getOrderTaskId())) {
                            EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(orderTaskResult, newOrderTaskResult);

                            newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                            newOrderTaskResult.setFolderId(newFolderId);
                            newOrderTaskResult.setOrderId(newOrder.getId());
                            newOrderTaskResult.setOrderTaskId(newOrderTask.getId());
                            newOrderTaskResult.setOrderNo(newOrder.getOrderNo());
                            newOrderTaskResultList.add(newOrderTaskResult);
                        }
                    }
                }
            }

            List<EmcOrderItemGroupBean> oldItemGroupList = orderItemGroupList.stream().filter(group -> ObjectUtils.equals(oldOrder.getId(), group.getOrderId())).collect(Collectors.toList());
            oldItemGroupList.forEach(group -> {
                EmcOrderItemGroupBean newItemGroup = new EmcOrderItemGroupBean();
                BeanUtils.copyProperties(group, newItemGroup, EmcConstant.CREATED_COLUMNS);
                newItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                newItemGroup.setOrderId(newOrder.getId());
                newOrderItemGroupList.add(newItemGroup);
            });
        }

        folderService.getDao().insert(newFolder);
        orderService.getDao().insert(newOrderList);
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);

        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        mobileDTO.setFolderList(Collections.singletonList(newFolder));
        mobileDTO.setOrderList(newOrderList);
        mobileDTO.setOrderTaskList(newOrderTaskList);
        mobileDTO.setOrderTaskResultList(newOrderTaskResultList);
        mobileDTO.setOrderItemGroupList(newOrderItemGroupList);

        return mobileDTO;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public EmcMobileDTO createOrder(RestJsonWrapperBean wrapper) {
        EmcOrderBean order = parse(wrapper, EmcOrderBean.class);
        if (order == null) {
            return new EmcMobileDTO();
        }

        // 要添加的样品的QC类型
        String qcCategoryCode = order.getQcCategoryCode();
        List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByOneColumnValue(qcCategoryCode, "qcCategoryId");
        EmcQcCategoryBean targetQcCategory = qcCategoryList.isEmpty() ? new EmcQcCategoryBean() : qcCategoryList.get(0);

        // 查询样品信息
        order = orderService.getDao().selectById(order.getId());

        EmcFolderBean folder = folderService.selectById(order.getFolderId());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(order.getFolderId(), "FOLDERID");

        String maxOrderNo = orderList.stream().filter(o ->!o.getOrderNo().contains("-H")).max(Comparator.comparing(EmcOrderBean::getOrderNo)).map(EmcOrderBean::getOrderNo).orElse(null);
        String nextOrderNo = StringUtils.isEmpty(maxOrderNo) ? folder.getFolderNo() + "01" : folder.getFolderNo() + StringUtils.leftPad((NumberUtils.parseInt(maxOrderNo.substring(folder.getFolderNo().length() + 1)) + 1) + "", 3);

        // 判断是否为后管
        boolean isBlabQc = PfConstant.QC_L_BLAB.equals(qcCategoryCode);
        // 判断是否为尘氟
        boolean isCfQc = PfConstant.QC_P_CF.equals(qcCategoryCode);
        String newOrderNo = (isBlabQc || isCfQc) ? order.getOrderNo() + "-" + (isBlabQc ? PfConstant.QC_L_BLAB_ORDER_NO_SUFFIX : PfConstant.QC_P_CF_ORDER_NO_SUFFIX) : nextOrderNo;

        EmcOrderBean newOrder = new EmcOrderBean();
        BeanUtils.copyProperties(order, newOrder);

        Long newOrderId = ApplicationContextHelper.getNextIdentity();
        newOrder.setId(newOrderId);
        newOrder.setOrderNo(newOrderNo);
        newOrder.setLabelNo(newOrderNo);
        newOrder.setQcCategoryId(targetQcCategory.getId());
        newOrder.setQcCategoryCode(Optional.ofNullable(targetQcCategory.getQcCategoryId()).orElse(EmcConstant.N_A));
        newOrder.setQcName(Optional.ofNullable(targetQcCategory.getQcName()).orElse(EmcConstant.N_A));
        if(StringUtils.equals(newOrder.getQcCategoryCode(),EmcConstant.N_A)) {
            newOrder.setFrequency(order.getFrequency()+1);
        }
        boolean updateParent = Constant.YES.equals(targetQcCategory.getUpdateParNo());
        if (updateParent) {
            newOrder.setParentOrderNo(order.getOrderNo());
        }

        // 如果复制的是正常样品，则清空 sampleId
        if (EmcConstant.N_A.equals(qcCategoryCode)) {
            newOrder.setSampleId(null);
        }

        // 查询样品下的对应测试信息和测试结果信息
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");

        // 查询样品分析项组
        List<EmcOrderItemGroupBean> copiedOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        List<Long> resultItemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> resultItemList = itemService.getDao().selectListByOneColumnValues(resultItemIdList, "id", Arrays.asList("id", "qcCategoryId"));
        Map<Long, String> itemQcMap = resultItemList.stream().filter(item -> !StringUtils.isEmpty(item.getQcCategoryId())).collect(Collectors.toMap(EmcItemBean::getId, EmcItemBean::getQcCategoryId));

        // 复制样品分析项组
        orderService.copyOrderItemGroup(copiedOrderItemGroupList, order, newOrderId, newOrderItemGroupList);

        for (EmcOrderTaskBean orderTask : orderTaskList) {
            if (order.getId().equals(orderTask.getOrderId())) {
                EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                BeanUtils.copyProperties(orderTask, newOrderTask);

                Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                newOrderTask.setId(newOrderTaskId);
                newOrderTask.setOrderId(newOrderId);
                newOrderTask.setEquiptAcquNo(newOrderNo);
                newOrderTask.setQcCategoryId(targetQcCategory.getId());
                newOrderTask.setQcCategoryCode(Optional.ofNullable(targetQcCategory.getQcCategoryId()).orElse(EmcConstant.N_A));
                newOrderTask.setQcName(Optional.ofNullable(targetQcCategory.getQcName()).orElse(EmcConstant.N_A));
                if (updateParent) {
                    newOrderTask.setParentId(orderTask.getId());
                }

                List<EmcOrderTaskResultBean> curResultList = orderTaskResultList.stream().filter(r -> ObjectUtils.equals(r.getOrderTaskId(), orderTask.getId())).collect(Collectors.toList());
                // 如果有分析项在该QC中登录，则添加结果数据
                if(!StringUtils.equals(newOrder.getQcCategoryCode(),EmcConstant.N_A)){
                    List<EmcOrderTaskResultBean> validResultList = curResultList.stream().filter(r -> ArrayUtils.contains(StringUtils.split(itemQcMap.get(r.getItemId()), ","), targetQcCategory.getId() + "")).collect(Collectors.toList());
                    if (!validResultList.isEmpty()) {
                        newOrderTaskList.add(newOrderTask);

                        validResultList.forEach(result -> {
                            EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(result, newOrderTaskResult);


                            newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                            newOrderTaskResult.setOrderId(newOrderId);
                            newOrderTaskResult.setOrderTaskId(newOrderTaskId);
                            newOrderTaskResult.setOrderNo(newOrderNo);
                            newOrderTaskResult.setQcCategoryId(targetQcCategory.getId());
                            newOrderTaskResult.setQcCategoryCode(Optional.ofNullable(targetQcCategory.getQcCategoryId()).orElse(EmcConstant.N_A));
                            newOrderTaskResult.setQcName(Optional.ofNullable(targetQcCategory.getQcName()).orElse(EmcConstant.N_A));
                            //全程序空白不需要复制数据
                            if(StringUtils.equals(newOrder.getQcCategoryCode(),EmcConstant.QCCATEGORY_QCCATEGORYID_P_BLANK)){
                                newOrderTaskResult.setOriginalResult("");
                                newOrderTaskResult.setReportResult("");
                            }
                            newOrderTaskResultList.add(newOrderTaskResult);
                        });
                    }
                }else {
                    newOrderTaskList.add(newOrderTask);
                    curResultList.forEach(result -> {
                            EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                            BeanUtils.copyProperties(result, newOrderTaskResult);

                            newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                            newOrderTaskResult.setOrderId(newOrderId);
                            newOrderTaskResult.setOrderTaskId(newOrderTaskId);
                            newOrderTaskResult.setOrderNo(newOrderNo);
                            newOrderTaskResult.setQcCategoryId(targetQcCategory.getId());
                            newOrderTaskResult.setQcCategoryCode(Optional.ofNullable(targetQcCategory.getQcCategoryId()).orElse(EmcConstant.N_A));
                            newOrderTaskResult.setQcName(Optional.ofNullable(targetQcCategory.getQcName()).orElse(EmcConstant.N_A));

                            newOrderTaskResultList.add(newOrderTaskResult);
                        });

                }

            }
        }

        orderService.getDao().insert(newOrder);
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);

        List<Long>  testMethodIdList = newOrderTaskList.stream().map(EmcOrderTaskBean :: getTestMethodId).distinct().collect(Collectors.toList());
        // 如果添加的是后管或尘氟，则需要将父样的采集编号加上后缀-Q，表示父样为前管样品
        if (isBlabQc || isCfQc) {
            String acqNoSuffix = "-" + PfConstant.QC_L_ALAB_ORDER_NO_SUFFIX;
            String orderNo = order.getOrderNo();
            orderTaskList.stream().filter(task -> !StringUtils.endsWith(task.getEquiptAcquNo(), acqNoSuffix))
            		.filter(task ->testMethodIdList.contains(task.getTestMethodId()))
            		.forEach(task -> task.setEquiptAcquNo(orderNo + acqNoSuffix));

            orderTaskService.getDao().update(orderTaskList, "equiptAcquNo");
        }

        EmcMobileDTO mobileDTO = new EmcMobileDTO();
        mobileDTO.setOrderList(Collections.singletonList(newOrder));
        mobileDTO.setOrderTaskList(newOrderTaskList);
        mobileDTO.setOrderTaskResultList(newOrderTaskResultList);
        mobileDTO.setOrderItemGroupList(newOrderItemGroupList);

        return mobileDTO;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public EmcMobileDTO deleteFolder(RestJsonWrapperBean wrapper) {
        EmcFolderBean folder = parse(wrapper, EmcFolderBean.class);
        if (folder == null) {
            return new EmcMobileDTO();
        }

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(folder.getId(), "folderId", Collections.singletonList("id"));
        folderService.getDao().delete(folder.getId());
        deleteOrder(orderList);

        return new EmcMobileDTO();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public EmcMobileDTO deleteOrder(RestJsonWrapperBean wrapper) {
        EmcOrderBean order = parse(wrapper, EmcOrderBean.class);
        if (order == null) {
            return new EmcMobileDTO();
        }

        deleteOrder(Collections.singletonList(order));

        return new EmcMobileDTO();
    }

    private void deleteOrder(List<EmcOrderBean> orderList) {
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        orderService.getDao().deleteByIdList(orderIdList);

        List<EmcOrderTaskBean> deleteTaskList = new ArrayList<>();
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>();
        List<EmcOrderTaskResultBean> deleteOrderTaskResultList = new ArrayList<>();

        orderIdList.forEach(orderId -> {
            EmcOrderTaskBean deleteTask = new EmcOrderTaskBean();
            deleteTask.setOrderId(orderId);
            deleteTaskList.add(deleteTask);

            EmcOrderItemGroupBean deleteGroup = new EmcOrderItemGroupBean();
            deleteGroup.setOrderId(orderId);
            deleteOrderItemGroupList.add(deleteGroup);

            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderId(orderId);
            deleteOrderTaskResultList.add(deleteResult);
        });

        orderTaskService.getDao().deleteBy(deleteTaskList, "orderId");
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "orderId");
        orderTaskResultService.getDao().deleteBy(deleteOrderTaskResultList, "orderId");

        // 判断删除的是否为后管或者尘氟样品，则需要更新父样的仪器采集编号
        List<String> blabOrderNoList = orderList.stream().filter(order -> PfConstant.QC_L_BLAB.equals(order.getQcCategoryCode())
                || PfConstant.QC_P_CF.equals(order.getQcCategoryCode())).map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
        if (!blabOrderNoList.isEmpty()) {
            List<String> parentOrderNoList = blabOrderNoList.stream().map(orderNo -> StringUtils.replace(orderNo, "-" + PfConstant.QC_L_BLAB_ORDER_NO_SUFFIX, ""))
                    .map(orderNo -> StringUtils.replace(orderNo, "-" + PfConstant.QC_P_CF_ORDER_NO_SUFFIX, "")).collect(Collectors.toList());

            List<EmcOrderBean> parentOrderList = orderService.getDao().selectListByOneColumnValues(parentOrderNoList, "orderNo", Arrays.asList("id", "orderNo"));
            List<Long> parentOrderIdList = parentOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> parentOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(parentOrderIdList, "orderId", Arrays.asList("id", "equiptAcquNo"));
            String acqNoSuffix = "-" + PfConstant.QC_L_ALAB_ORDER_NO_SUFFIX;
            List<EmcOrderTaskBean> updateTaskList = parentOrderTaskList.stream().filter(task -> StringUtils.endsWith(task.getEquiptAcquNo(), acqNoSuffix)).collect(Collectors.toList());
            updateTaskList.forEach(task -> task.setEquiptAcquNo(StringUtils.replace(task.getEquiptAcquNo(), acqNoSuffix, "")));
            orderTaskService.getDao().update(updateTaskList, "equiptAcquNo");
        }
    }

    @Override
    public void updateEquiptRequisition(EmcMobileDTO mobileDTO, Map<Long, Long> calibrationIdMap, Map<Long, Long> sampleIdMap) {
        List<EmcEquiptRequisitionBean> requisitionList = mobileDTO.getRequisitionList();
        if (CollectionUtils.isEmpty(requisitionList)) {
            return;
        }

        requisitionList.forEach(r -> {
            r.setId(ApplicationContextHelper.getNextIdentity());
            r.setSampleId(sampleIdMap.get(r.getSampleId()));

            String[] originalCalibrationIds = StringUtils.split(r.getCalibrationId(), ",");
            List<Long> originalCalibrationIdList = Stream.of(originalCalibrationIds).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());

            List<String> convertedCalibrationIdList = new ArrayList<>();
            originalCalibrationIdList.forEach(oid -> Optional.ofNullable(calibrationIdMap.get(oid)).ifPresent(cid -> convertedCalibrationIdList.add(cid.toString())));

            r.setCalibrationId(String.join(",", convertedCalibrationIdList));
        });

        // 是否需要更新
        /*List<Long> sampleIdList = requisitionList.stream().map(EmcEquiptRequisitionBean::getSampleId).distinct().collect(Collectors.toList());
        List<Long> calibrationIdList = requisitionList.stream().map(EmcEquiptRequisitionBean::getCalibrationId).distinct().collect(Collectors.toList());
        if (!sampleIdList.isEmpty() && !calibrationIdList.isEmpty()) {
            List<EmcEquiptRequisitionBean> existedRequisitionList = requisitionService.selectListByFilter(SearchFilter.instance().match("sampleId", sampleIdList).filter(MatchPattern.OR).match("calibrationId", calibrationIdList).filter(MatchPattern.OR));
            List<Long> existedSampleIdList = existedRequisitionList.stream().map(EmcEquiptRequisitionBean::getSampleId).distinct().collect(Collectors.toList());
            List<Long> existedCalibrationIdList = existedRequisitionList.stream().map(EmcEquiptRequisitionBean::getCalibrationId).distinct().collect(Collectors.toList());

            List<EmcEquiptRequisitionBean> updateList = requisitionList.stream().filter(r -> existedSampleIdList.contains(r.getSampleId()) && existedCalibrationIdList.contains(r.getCalibrationId())).collect(Collectors.toList());
            updateList.forEach(r -> existedRequisitionList.stream().filter(er -> ObjectUtils.equals(er.getSampleId(), r.getSampleId()) && ObjectUtils.equals(er.getCalibrationId(), r.getCalibrationId())).findFirst().ifPresent(er -> r.setId(er.getId())));

            List<EmcEquiptRequisitionBean> insertList = requisitionList.stream().filter(r -> !existedSampleIdList.contains(r.getSampleId()) || !existedCalibrationIdList.contains(r.getCalibrationId())).collect(Collectors.toList());

            requisitionService.getDao().update(updateList);
            requisitionService.getDao().insert(insertList);
        } */

        requisitionService.getDao().insert(requisitionList);
    }

    @Override
    public EmcMobileDTO createDelivery(RestJsonWrapperBean wrapper) {
        EmcMobileDTO mobileDTO = parse(wrapper);
        if (mobileDTO == null) {
            return new EmcMobileDTO();
        }

        List<EmcOrderContainerBean> orderContainerList = mobileDTO.getOrderContainerList();
        if (orderContainerList.isEmpty()) {
            return new EmcMobileDTO();
        }

        List<EmcOrderContainerBean> newOrderContainerList = orderContainerList.stream().filter(oc -> !StringUtils.isEmpty(oc.getMobileId())).collect(Collectors.toList());
        List<EmcOrderContainerBean> updateOrderContainerList = orderContainerList.stream().filter(oc -> StringUtils.isEmpty(oc.getMobileId())).collect(Collectors.toList());

        EmcDeliveryBean delivery = new EmcDeliveryBean();
        delivery.setId(ApplicationContextHelper.getNextIdentity());
        delivery.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        delivery.setDeliveryNo(ApplicationContextHelper.getNextSequence(deliveryService.getDao().getTable()));
        delivery.setProjId(orderContainerList.get(0).getProjId());
        delivery.setStatus("draft");

        updateOrderContainerList.forEach(oc -> oc.setDeliveryId(delivery.getId()));

        deliveryService.getDao().insert(delivery);
        orderContainerService.getDao().update(orderContainerList, "deliveryId");

        if (!newOrderContainerList.isEmpty()) {
            newOrderContainerList.forEach(oc -> oc.setId(ApplicationContextHelper.getNextIdentity()));
            orderContainerService.getDao().insert(newOrderContainerList);
        }

        EmcMobileDTO returnDTO = new EmcMobileDTO();
        returnDTO.setOrderContainerList(orderContainerList);

        return returnDTO;
    }
}
