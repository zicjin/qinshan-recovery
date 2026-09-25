package com.sunwayworld.emc.module.bp.project.project.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.*;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.service.PfFolderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.PfProjectTaskProcessDTO;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectOtherFeeService;
import com.sunwayworld.emc.module.bp.project.project.service.PfProjectService;
import com.sunwayworld.emc.module.bp.project.project.validator.EmcProjectDeleteValidator;
import com.sunwayworld.emc.module.bp.project.project.validator.PfProjectCompleteTaskValidator;
import com.sunwayworld.emc.module.bp.project.project.validator.PfUndoDeliveryProjectValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.erp.service.ErpContractOrderService;
import com.sunwayworld.emc.module.erp.service.ErpOrderService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmWriteOffBean;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOpenService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmWriteOffService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.audit.constant.AuditConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mail.MailHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceParamDTO;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;

/**
 * @author zhengxy@sunwayworld.com 2021年06月01日
 */
@Repository
@GikamBean
public class PfProjectServiceImpl extends EmcProjectServiceImpl implements PfProjectService {

    private static final Logger logger = LogManager.getLogger(PfProjectServiceImpl.class);

    @Override
    @Transactional
    public void reportAssignSubmit(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        projectList.forEach(p -> p.setReportAssignStatus("1"));
        getDao().update(projectList, "reportAssignStatus");
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>(projectList.size());
        projectList.forEach(project -> {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setProjId(project.getId());
            auditRecord.setProjNo(project.getProjNo());
            auditRecord.setComments("提交");
            auditRecord.setLogDesc("项目:" + project.getProjNo());
            auditRecord.setBizCategory("report");
            auditRecord.setStepCode("6");
            auditRecord.setAuditCategory("报告管理");
            auditRecord.setTaskName("报告指派");
            auditRecord.setNextTaskName("报告编制");
            auditRecord.setOperateCategory("提交");
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecordList.add(auditRecord);
        });
        auditRecordService.getDao().insert(auditRecordList);
    }

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Autowired
    private PfProjSchemeService projSchemeService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    @Lazy
    private PfFolderService folderService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    private CoreSequenceService sequenceService;

    @Autowired
    private EmcClientService clientService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private CoreBpmnCommentService coreBpmnCommentService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcReportService reportService;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private CrmWriteOffService crmWriteOffService;

    @Autowired
    @Lazy
    private EmcTestMethodService testMethodService;

    @Autowired
    @Lazy
    private EmcItemGroupService itemGroupService;

    @Autowired
    @Lazy
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    @Lazy
    private EmcGroupItemService groupItemService;

    @Autowired
    @Lazy
    private EmcRunService runService;

    @Autowired
    @Lazy
    private EmcAuditRecordService auditRecordService;

    @Autowired
    @Lazy
    private CoreFileService coreFileService;

    @Autowired
    @Lazy
    private EmcTestCapacityService testCapacityService;

    @Autowired
    @Lazy
    private CoreCodeService codeService;

    @Autowired
    @Lazy
    private EmcTestService testService;

    @Autowired
    @Lazy
    private EmcMethodService methodService;

    @Autowired
    private CrmOpportunityMainService opportunityMainService;

    @Autowired
    private CrmOpportunityContractService opportunityContractService;

    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    @Lazy
    private CrmOpportunityOpenService opportunityOpenService;

    @Autowired
    private ErpOrderService erpOrderService;

    @Autowired
    @Lazy
    private ErpContractOrderService erpContractOrderService;

    @Autowired
    private EmcReportSendInfoService reportSendInfoService;

    @Autowired
    private EmcProjectOtherFeeService projectOtherFeeService;

    @Autowired
    private EmcCapacityItemService capacityItemService;

    private final static String SAMPLING_CONTACT_RECORD_NEW_FILES = "samplingContactRecordFilesNew";
    private final static String DELIVERY_RECORD_NEW_FILES = "deliveryRecordFilesNew";

    private final static String PDF_SUFFIX = ".pdf";
    private final static String DOC_SUFFIX = ".doc";

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcProjectBean emcProject = jsonWrapper.parseUnique(EmcProjectBean.class);
        emcProject.setId(ApplicationContextHelper.getNextIdentity());
        emcProject.setRegistDate(LocalDateTime.now());
        emcProject.setExt$Item("qualitycontrol", Constant.NO);
        emcProject.setExt$Item("detect", Constant.YES);
        emcProject.setExt$Item("subpackage", Constant.NO);
        emcProject.setUrgent(Constant.NO);
        //赋值所属单位信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        String parentOrgName = user.getExt$Item("parentorgname");
        emcProject.setOrgId(parentOrgId);
        emcProject.setOrgName(parentOrgName);

        EmcBizCategoryBean bizCategory = bizCategoryService.selectById(emcProject.getBizCategoryId());
        // 设置受检单位默认字段
        Long testOrgId = bizCategory.getTestOrgId();
        if (testOrgId != null) {
            EmcClientBean testClient = clientService.getDao().selectById(testOrgId);
            emcProject.setTestOrgId(testClient.getClientNo());
            emcProject.setTestOrgName(testClient.getClientName());
            emcProject.setTestOrgAddress(testClient.getAddress());
            emcProject.setTestOrgPhone(testClient.getPhone());
        }
        // 设置委托单位默认字段
        Long entrustOrgId = bizCategory.getEntrustOrgId();
        if (entrustOrgId != null) {
            EmcClientBean entrustClient = clientService.getDao().selectById(entrustOrgId);
            emcProject.setEntrustOrgId(entrustClient.getClientNo());
            emcProject.setEntrustOrgName(entrustClient.getClientName());
            emcProject.setEntrustOrgAddress(entrustClient.getAddress());
            emcProject.setEntrustOrgPhone(entrustClient.getPhone());
        }
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String orgCode = Optional.ofNullable(parentOrg.getExt$Item("orgShortName")).orElse("XX");
        int nowYear = LocalDate.now().getYear();
        int year = Integer.parseInt(Optional.ofNullable(parentOrg.getExt$Item("quayear")).orElse(nowYear + "")) ;
        int month = emcProject.getTaskDate().getMonthValue();

        List<String> newProjectNoList = generateProjNos(orgCode, year, nowYear, month, 1);

        emcProject.setProjNo(newProjectNoList.get(0));
        emcProject.setReportOrgId(bizCategory.getReportEditOrgId());
        emcProject.setBizCategoryCode(bizCategory.getBizCategoryId());
        emcProject.setProjCreatedTime(LocalDateTime.now());
        getDao().insert(emcProject);

        // 创建报告发送信息
        createReportSendInfo(emcProject);

        // 创建其他费用信息
        projectOtherFeeService.createOtherFee(emcProject);

        return emcProject.getId();
    }

    public List<String> generateProjNos(String orgCode, int year, int nowYear,int month, int num) {
        String projNo = orgCode + EmcBizConstant.NUMFORENG[nowYear-year] + EmcBizConstant.NUMFORENG[month-1];

        RestJsonWrapperBean wrapper = new RestJsonWrapperBean();
        wrapper.setFilterValue("targetType", "modifyProjectInfo");
        List<EmcProjectBean> existedProjectList = selectListByFilter(SearchFilter.instance().match("projNo", projNo).filter(MatchPattern.SB), wrapper);

        EmcProjectBean maxNoProject = existedProjectList.stream().max(Comparator.comparing(p -> NumberUtils.parseInt(StringUtils.replace(p.getProjNo(), projNo, "")))).orElse(null);

        List<String> newProjectNoList = new ArrayList<>();
        String firstProjNo = "";
        if (ObjectUtils.isEmpty(maxNoProject)) {
            firstProjNo = projNo + "001";
            newProjectNoList.add(firstProjNo);
        } else {
            firstProjNo = maxNoProject.getProjNo().substring(0, maxNoProject.getProjNo().length() - 3) + String.format("%03d", Long.parseLong(maxNoProject.getProjNo().substring(maxNoProject.getProjNo().length() - 3)) + 1);
            newProjectNoList.add(firstProjNo);
        }

        if (num > 1) {
            for (int i = 0; i < num - 1; i++) {
                String beforeProjNo = newProjectNoList.get(i);
                String nextProjNo = beforeProjNo.substring(0, beforeProjNo.length() - 3) + String.format("%03d", Long.parseLong(beforeProjNo.substring(beforeProjNo.length() - 3)) + 1);
                newProjectNoList.add(nextProjNo);
            }
        }

        return newProjectNoList;
    }

    @Override
    public void copy(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> copyEmcProjectBeanList = wrapper.parse(EmcProjectBean.class);
        if (copyEmcProjectBeanList.isEmpty()) {
            return;
        }
        EmcProjectBean emcProjectBean = copyEmcProjectBeanList.get(0);
        String copyQtyStr = emcProjectBean.getExt$Item("copyqty");
        if (StringUtils.isEmpty(copyQtyStr) || !NumberUtils.isNumber(copyQtyStr)) {
            return;
        }
        //查询当前用户的所属单位信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        String parentOrgName = user.getExt$Item("parentorgname");

        int copyQty = Integer.parseInt(copyQtyStr);
        int projectQty = copyEmcProjectBeanList.size();
        List<EmcProjectBean> oldEmcProjectBeanList = getDao().selectListByIds(copyEmcProjectBeanList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()));
        // 拷贝列表项属性
        oldEmcProjectBeanList.forEach(m -> {
            copyEmcProjectBeanList.stream().filter(c -> ObjectUtils.equals(c.getId(), m.getId())).forEach(t -> {
                m.setBizCategoryId(t.getBizCategoryId());
                m.setTaskDate(t.getTaskDate());
                m.setSample(t.getSample());

                m.setExt$Item("contractId", t.getExt$Item("contractId"));
                m.setExt$Item("contractNo", t.getExt$Item("contractNo"));
                m.setExt$Item("biddername", t.getExt$Item("biddername"));
                m.setSplitContractId(t.getSplitContractId());
                m.setEntrustOrgId(t.getEntrustOrgId());
                m.setEntrustOrgName(t.getEntrustOrgName());
                m.setEntrustOrgPhone(t.getEntrustOrgPhone());
                m.setEntrustOrgAddress(t.getEntrustOrgAddress());
                m.setEntrustOrgContact(t.getEntrustOrgContact());
                m.setTestOrgId(t.getTestOrgId());
                m.setTestOrgPhone(t.getTestOrgPhone());
                m.setTestOrgContact(t.getTestOrgContact());
                m.setTestOrgAddress(t.getTestOrgAddress());
                m.setTestOrgName(t.getTestOrgName());
                m.setReportPostAddress(t.getReportPostAddress());
                m.setAddressee(t.getAddressee());
                m.setRecipientPhone(t.getRecipientPhone());
                m.setProjCreatedTime(LocalDateTime.now());
                m.setErpOrderNumber(null);
                m.setErpGbId(null);
                m.setErpReviewId(null);
            });
        });

        // 批量获取项目编号，数量为项目数 * 复制份数
        List<String> newProjectNoList = new ArrayList<>();
        LocalDateTime taskDate = emcProjectBean.getTaskDate() == null ? LocalDateTime.now() : emcProjectBean.getTaskDate();
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String orgCode = Optional.ofNullable(parentOrg.getExt$Item("orgShortName")).orElse("null");
        int year = Integer.parseInt(Optional.ofNullable(parentOrg.getExt$Item("quayear")).orElse("null")) ;
        int nowYear = LocalDate.now().getYear();
        int month = taskDate.getMonthValue();
        newProjectNoList = generateProjNos(orgCode,year,nowYear,month,projectQty * copyQty);

        // 采样业务项目
        List<EmcProjectBean> emcSampleProjectList = oldEmcProjectBeanList.stream().filter(m -> ObjectUtils.equals(m.getSample(), "1")).collect(Collectors.toList());
        if (!emcSampleProjectList.isEmpty()) {
            List<EmcProjectBean> newEmcProjectBeanList = new ArrayList<>();
            List<EmcProjSchemeBean> newEmcProjSchemeBeanList = new ArrayList<>();
            List<EmcProjOrderBean> newEmcProjOrderBeanList = new ArrayList<>();

            List<EmcProjSchemeBean> emcProjSchemeBeanList = projSchemeService.getDao()
                    .selectListByOneColumnValues(emcSampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()), "PROJID");
            List<EmcProjOrderBean> emcProjOrderBeanList = projOrderService.getDao()
                    .selectListByOneColumnValues(emcProjSchemeBeanList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList()), "PROJSCHEMEID");

            // 过滤掉测试方法被删除的测试
            List<Long> capacityItemGroupIdList = emcProjOrderBeanList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id", Arrays.asList("id", "testMethodId", "testCapacityId"));
            List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).distinct().collect(Collectors.toList());
            List<EmcTestCapacityBean> existedTestCapacityList = testCapacityService.getDao().selectListByOneColumnValues(testCapacityIdList, "id", Collections.singletonList("id"));
            List<Long> existedTestCapacityIdList = existedTestCapacityList.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());
            List<Long> existedCapacityItemGroupIdList = capacityItemGroupList.stream().filter(cig -> existedTestCapacityIdList.contains(cig.getTestCapacityId())).map(EmcCapacityItemGroupBean::getId).collect(Collectors.toList());

            List<EmcProjOrderBean> validProjOrderList = emcProjOrderBeanList.stream().filter(po -> existedCapacityItemGroupIdList.contains(po.getItemGroupId())).collect(Collectors.toList());

            // 开始复制项目
            for (int i = 0; i < emcSampleProjectList.size(); i++) {
                EmcProjectBean c = emcSampleProjectList.get(i);
                List<EmcProjSchemeBean> schemeBeanList = emcProjSchemeBeanList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), c.getId())).collect(Collectors.toList());
                for (int j = 0; j < copyQty; j++) {
                    EmcProjectBean projectBean = new EmcProjectBean();
                    BeanUtils.copyProperties(c, projectBean);
                    projectBean.setProcessStatus(EmcBizConstant.REPORT_STATUS_DRAFT);
                    long newProjectId = ApplicationContextHelper.getNextIdentity();
                    LocalDateTime now = LocalDateTime.now();
                    projectBean.setId(newProjectId);
                    String newProjectNo = newProjectNoList.get(i * copyQty + j);
                    projectBean.setProjNo(newProjectNo);
                    projectBean.setRegistDate(now);
                    projectBean.setAcceptDate(now);
                    projectBean.setCreatedById(LocalContextHelper.getLoginUserId());
                    projectBean.setCreatedByName(LocalContextHelper.getLoginUserName());
                    projectBean.setCreatedTime(LocalDateTime.now());
                    projectBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                    projectBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                    projectBean.setSource("");
                    projectBean.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
                    projectBean.setProjNode(EmcBizConstant.PROJECT_CN_NODE_XMDJ);
                    //赋值所属单位信息
                    projectBean.setOrgId(parentOrgId);
                    projectBean.setOrgName(parentOrgName);

                    newEmcProjectBeanList.add(projectBean);

                    int qty = schemeBeanList.size();
                    List<String> folderNoList = folderService.generateFolderNosByProjId(newProjectId, newProjectNo, schemeBeanList.get(0).getMonitorTmplId(), qty);
                    // 复制项目对应的方案
                    for (int p = 0; p < qty; p++) {
                        EmcProjSchemeBean schemeBean = new EmcProjSchemeBean();
                        EmcProjSchemeBean oldschemeBean = schemeBeanList.get(p);
                        BeanUtils.copyProperties(oldschemeBean, schemeBean);
                        schemeBean.setProjId(newProjectId);
                        schemeBean.setFolderNo(folderNoList.get(p));
                        schemeBean.setSortNum(Long.parseLong(folderNoList.get(p).substring(folderNoList.get(p).length() - 3)));
                        long newSchemeId = ApplicationContextHelper.getNextIdentity();
                        List<EmcProjOrderBean> filterProjOrderList = validProjOrderList.stream().filter(m -> ObjectUtils.equals(oldschemeBean.getId(), m.getProjSchemeId())).collect(Collectors.toList());
                        for (int k = 0; k < filterProjOrderList.size(); k++) {
                            EmcProjOrderBean orderBean = new EmcProjOrderBean();
                            BeanUtils.copyProperties(filterProjOrderList.get(k), orderBean);
                            orderBean.setId(ApplicationContextHelper.getNextIdentity());
                            orderBean.setProjSchemeId(newSchemeId);
                            orderBean.setSortNum(k + 1L);
                            newEmcProjOrderBeanList.add(orderBean);
                        }
                        schemeBean.setId(newSchemeId);
                        newEmcProjSchemeBeanList.add(schemeBean);
                    }
                }
            }
            this.getDao().insert(newEmcProjectBeanList);
            projSchemeService.getDao().insert(newEmcProjSchemeBeanList);
            projOrderService.getDao().insert(newEmcProjOrderBeanList);
        }

        // 送样业务项目
        List<EmcProjectBean> emcOrderProjectList = oldEmcProjectBeanList.stream().filter(m -> ObjectUtils.equals(m.getSample(), "0")).collect(Collectors.toList());
        if (!emcOrderProjectList.isEmpty()) {
            // 查询项目对应的所有样品
            List<EmcOrderBean> emcOrderBeanList = orderService.getDao().selectListByOneColumnValues(emcOrderProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList()), "PROJID");
            // 查询点位列表
            List<EmcFolderBean> emcFolderBeanList = folderService.getDao()
                    .selectListByIds(emcOrderBeanList.stream().filter(r -> r.getFolderId() != null).map(EmcOrderBean::getFolderId).collect(Collectors.toList()));
            if (emcFolderBeanList.isEmpty()) {
                // 无点位信息，不进行复制，此数据有问题
                return;
            }

            List<EmcProjectBean> newEmcProjectBeanList = new ArrayList<>();
            List<Long> emcOrderIdList = emcOrderBeanList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> emcOrderItemGroupBeanList = orderItemGroupService.getDao().selectListByOneColumnValues(emcOrderIdList, "ORDERID");
            List<EmcOrderItemBean> emcOrderItemBeanList = orderItemService.getDao().selectListByOneColumnValues(emcOrderIdList, "ORDERID");

            // 过滤掉测试方法被删除的测试
            List<Long> testMethodIdList = emcOrderItemGroupBeanList.stream().map(EmcOrderItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
            List<EmcTestMethodBean> existedTestMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Collections.singletonList("id"));
            List<Long> existedTestMethodIdList = existedTestMethodList.stream().map(EmcTestMethodBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> validOrderItemGroupList = emcOrderItemGroupBeanList.stream().filter(oig -> existedTestMethodIdList.contains(oig.getTestMethodId())).collect(Collectors.toList());

            List<EmcFolderBean> newEmcFolderBeanList = new ArrayList<>();
            List<EmcOrderBean> newEmcOrderBeanList = new ArrayList<>();
            List<EmcOrderItemGroupBean> newEmcOrderItemGroupBeanList = new ArrayList<>();
            List<EmcOrderItemBean> newEmcOrderItemBeanList = new ArrayList<>();

            // 开始复制项目
            for (int i = 0; i < emcOrderProjectList.size(); i++) {
                EmcProjectBean c = emcOrderProjectList.get(i);
                // 当前项目需要复制的点位列表
                List<EmcFolderBean> folderBeans = emcFolderBeanList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), c.getId())).collect(Collectors.toList());

                if (folderBeans.isEmpty()) {
                    // 无点位信息，不进行复制，此数据有问题
                    continue;
                }
                for (int j = 0; j < copyQty; j++) {
                    EmcProjectBean projectBean = new EmcProjectBean();
                    BeanUtils.copyProperties(c, projectBean);
                    projectBean.setProcessStatus(EmcBizConstant.REPORT_STATUS_DRAFT);
                    long newProjectId = ApplicationContextHelper.getNextIdentity();
                    LocalDateTime now = LocalDateTime.now();
                    projectBean.setId(newProjectId);
                    String newProjectNo = newProjectNoList.get(emcSampleProjectList.size() * copyQty + i * copyQty + j);
                    projectBean.setProjNo(newProjectNo);
                    projectBean.setRegistDate(now);
                    projectBean.setAcceptDate(now);
                    projectBean.setCreatedById(LocalContextHelper.getLoginUserId());
                    projectBean.setCreatedByName(LocalContextHelper.getLoginUserName());
                    projectBean.setCreatedTime(LocalDateTime.now());
                    projectBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                    projectBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                    projectBean.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
                    //赋值所属单位信息
                    projectBean.setOrgId(parentOrgId);
                    projectBean.setOrgName(parentOrgName);
                    newEmcProjectBeanList.add(projectBean);

                    // 批量生成新的点位编号
                    int folderSize = folderBeans.size();
                    List<String> newFolderNoList = folderService.generateFolderNosByProjId(newProjectId, newProjectNo, folderBeans.get(0).getMonitorTmplId(), folderSize);

                    for (int k = 0; k < folderSize; k++) {

                        EmcFolderBean f = folderBeans.get(k);
                        EmcFolderBean newFolder = new EmcFolderBean();
                        BeanUtils.copyProperties(f, newFolder);
                        Long newFolderId = ApplicationContextHelper.getNextIdentity();
                        String newFolderNo = newFolderNoList.get(k);
                        newFolder.setId(newFolderId);
                        newFolder.setFolderNo(newFolderNo);
                        newFolder.setProjId(newProjectId);
                        newEmcFolderBeanList.add(newFolder);

                        // 当前项目需要复制的样品列表
                        List<EmcOrderBean> orderBeans = emcOrderBeanList.stream().filter(e -> ObjectUtils.equals(e.getFolderId(), f.getId())).collect(Collectors.toList());
                        for (int m = 0; m < orderBeans.size(); m++) {

                            EmcOrderBean t = orderBeans.get(m);
                            EmcOrderBean newOrderBean = new EmcOrderBean();
                            BeanUtils.copyProperties(t, newOrderBean);
                            newOrderBean.setOrderNo(newFolderNo + "A" + String.format("%03d", m + 1));
                            newOrderBean.setLabelNo(newOrderBean.getOrderNo());
                            Long newOrderId = ApplicationContextHelper.getNextIdentity();
                            newOrderBean.setId(newOrderId);
                            newOrderBean.setFolderId(newFolderId);
                            newOrderBean.setProjId(newProjectId);
                            newOrderBean.setOrderNum(Long.parseLong(newFolderNo.substring(newFolderNo.length() - 3)));
                            newOrderBean.setSampleId(null);
                            newOrderBean.setOrderDesc(null);
                            newOrderBean.setAcceptedDate(null);
                            newOrderBean.setAcceptedUserId(null);
                            newOrderBean.setAccepteUserName(null);
                            newOrderBean.setErpGbId(null);
                            newOrderBean.setExt$Item("packing", null);
                            newOrderBean.setSendDate(null);
                            newOrderBean.setSendTime(null);
                            newEmcOrderBeanList.add(newOrderBean);

                            List<EmcOrderItemGroupBean> filterOrderItemGroupList = validOrderItemGroupList.stream().filter(o -> ObjectUtils.equals(o.getOrderId(), t.getId())).collect(Collectors.toList());

                            for (int q = 0; q < filterOrderItemGroupList.size(); q++) {
                                EmcOrderItemGroupBean itemGroupBean = new EmcOrderItemGroupBean();
                                BeanUtils.copyProperties(filterOrderItemGroupList.get(q), itemGroupBean);
                                itemGroupBean.setOrderId(newOrderId);
                                Long newItemGroupId = ApplicationContextHelper.getNextIdentity();
                                itemGroupBean.setId(newItemGroupId);
                                itemGroupBean.setSortNum(q + 1L);
                                newEmcOrderItemGroupBeanList.add(itemGroupBean);

                                int finalQ = q;
                                emcOrderItemBeanList.stream().filter(o -> ObjectUtils.equals(o.getOrderItemGroupId(), filterOrderItemGroupList.get(finalQ).getId())).forEach(p -> {
                                    EmcOrderItemBean itemBean = new EmcOrderItemBean();
                                    BeanUtils.copyProperties(p, itemBean);
                                    itemBean.setOrderId(newOrderId);
                                    itemBean.setOrderItemGroupId(newItemGroupId);
                                    itemBean.setId(ApplicationContextHelper.getNextIdentity());
                                    newEmcOrderItemBeanList.add(itemBean);
                                });
                            }
                        }
                    }
                }
            }

            if (newEmcOrderItemBeanList.size() > 100000) {
                String projNos = copyEmcProjectBeanList.stream().map(EmcProjectBean::getProjNo).collect(Collectors.joining(","));
                logger.warn("项目{}复制出现问题，数据异常 -> {}", projNos, newEmcOrderItemBeanList.size());
                return;
            }

            this.getDao().insert(newEmcProjectBeanList);
            folderService.getDao().insert(newEmcFolderBeanList);
            orderService.getDao().insert(newEmcOrderBeanList);
            orderItemGroupService.getDao().insert(newEmcOrderItemGroupBeanList);
            orderItemService.getDao().insert(newEmcOrderItemBeanList);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(PfProjectCompleteTaskValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = startProcessBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcProjectBean> projectList = selectListByIds(idList);

        String statusCode = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();

        projectList.forEach(p -> {
            p.setStatus(EmcBizConstant.PROJECT_STATUS_SCHEMEAUDIT);
            p.setProjNode("方案审核");
            if (StringUtils.equalsIgnoreCase(ProcessStatus.DRAFT.name(), statusCode)) {
                p.setProjCreatedTime(LocalDateTime.now());
            }
        });
        // 采样任务
        List<EmcProjectBean> sampleProjectList = projectList.stream().filter(e -> Constant.YES.equals(e.getSample())).collect(Collectors.toList());
        List<Long> sampleProjIdList = sampleProjectList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjSchemeBean> projSchemeList = projSchemeService.selectListByFilter(SearchFilter.instance().match("PROJID", sampleProjIdList).filter(MatchPattern.OR));
        Map<Long, List<EmcProjSchemeBean>> projSchemeMap = projSchemeList.stream().collect(Collectors.groupingBy(r -> r.getProjId()));
        // 送样任务
        List<EmcProjectBean> noSampleProjectList = projectList.stream().filter(e -> !Constant.YES.equals(e.getSample())).collect(Collectors.toList());
        List<Long> noSampleProjIdList = noSampleProjectList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcFolderBean> noSampleFolderList = folderService.selectListByFilter(SearchFilter.instance().match("PROJID", noSampleProjIdList).filter(MatchPattern.OR));
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("PROJID", noSampleProjIdList).filter(MatchPattern.OR));
        Map<Long, List<EmcOrderBean>> orderMap = orderList.stream().collect(Collectors.groupingBy(r -> r.getProjId()));

        List<EmcProjSchemeBean> updateSchemeList = new ArrayList<>();
        List<EmcFolderBean> updateFolderList = new ArrayList<>();
        List<EmcOrderBean> updateOrderList = new ArrayList<>();
        // 采样任务根据监测方案的排序码生成folderNo，送样任务的根据送样的排序码生成orderNo
        projSchemeMap.forEach((projId, v) -> {
            List<EmcProjSchemeBean> sortProjSchemeList = v.stream().sorted(Comparator.comparing(EmcProjSchemeBean::getSortNum)).collect(Collectors.toList());
            EmcProjectBean projBean = sampleProjectList.stream().filter(e -> ObjectUtils.equals(e.getId(), projId)).findFirst().get();
            List<String> folderNoList = folderService.generateFolderNosByProjId(projId, projBean.getProjNo(), v.get(0).getMonitorTmplId(), v.size());
            for (int i = 0; i < sortProjSchemeList.size(); i++) {
                sortProjSchemeList.get(i).setFolderNo(folderNoList.get(i));
            }
            updateSchemeList.addAll(sortProjSchemeList);
        });

        orderMap.forEach((projId, v) -> {
            List<EmcOrderBean> normalOrderList = v.stream().filter(e -> "N/A".equals(e.getQcCategoryCode())).sorted(Comparator.comparing(EmcOrderBean::getOrderNum)).collect(Collectors.toList());
            EmcProjectBean projBean = noSampleProjectList.stream().filter(e -> ObjectUtils.equals(e.getId(), projId)).findFirst().get();
            // 给个不存在的projId，让其重新生成folderNo
            List<String> folderNoList = folderService.generateFolderNosByProjId(1L, projBean.getProjNo(), normalOrderList.get(0).getMonitorTmplId(), normalOrderList.size());
            List<EmcFolderBean> findFolderList = noSampleFolderList.stream().filter(e -> ObjectUtils.equals(projId, e.getProjId())).collect(Collectors.toList());
            for (int i = 0; i < normalOrderList.size(); i++) {
                normalOrderList.get(i).setOrderNo(folderNoList.get(i) + "001");
                normalOrderList.get(i).setLabelNo(folderNoList.get(i) + "001");
                for (int j = 0; j < findFolderList.size(); j++) {
                    if (ObjectUtils.equals(findFolderList.get(j).getId(), normalOrderList.get(i).getFolderId())) {
                        EmcFolderBean updateFolder = new EmcFolderBean();
                        updateFolder.setId(findFolderList.get(j).getId());
                        updateFolder.setFolderNo(folderNoList.get(i));
                        updateFolderList.add(updateFolder);
                    }
                }
            }
            updateOrderList.addAll(normalOrderList);
        });

        getDao().update(projectList, "STATUS", "PROJNODE", "PROJCREATEDTIME");
        projSchemeService.getDao().update(updateSchemeList, "FOLDERNO");
        // folderNo唯一键，先设置成其它值，再设置为正常值
        List<EmcFolderBean> cloneFolderList = ObjectUtils.clone(updateFolderList);
        cloneFolderList.forEach(f -> {
            f.setFolderNo(f.getFolderNo() + "-u");
        });
        folderService.getDao().update(cloneFolderList, "FOLDERNO");
        folderService.getDao().update(updateFolderList, "FOLDERNO");
        orderService.getDao().update(updateOrderList, "ORDERNO", "LABELNO");
        insertAuditRecordByProject(projectList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = completeTaskBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcProjectBean> projectList = selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));

        LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
        ExecutorHelper.submit(() -> {
            try {
                LocalContextHelper.setUserLogin(loginUser);
                // 计划合同项目
                List<EmcProjectBean> planContractProjectList = projectList.stream().filter(p -> !ObjectUtils.isEmpty(p.getSplitContractId())).collect(Collectors.toList());
                List<EmcProjectBean> normalProjectList = projectList.stream().filter(p -> ObjectUtils.isEmpty(p.getSplitContractId())).collect(Collectors.toList());

                if (!planContractProjectList.isEmpty()) {
                    // 对于计划合同项目，需要ERP系统进行下单确认后才能生成业务数据
                    planContractProjectList.forEach(p -> p.setStatus("erpConfirm"));
                    getDao().update(planContractProjectList, "STATUS");
                }

                logger.info("计划合同项目数量 -> {}", planContractProjectList.size());

                // 样品登记的项目提交时插入业务流程表
                // 筛选不采样的数据进行处理
                List<EmcProjectBean> noSampleProjectList = normalProjectList.stream().filter(e -> Constant.NO.equals(e.getSample())).collect(Collectors.toList());
                // 样品登记的项目提交完成项目流程结束
                if (!noSampleProjectList.isEmpty()) {
                    List<EmcProjectBean> updateProjectList = new ArrayList<>();
                    List<Long> noSampleProjectIdList = noSampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

                    orderService.insertBpDataByProject(noSampleProjectIdList);

                    // 删除无效审核记录
                    List<CoreBpmnCommentBean> filterBpmnCommentList = new ArrayList<>();
                    noSampleProjectIdList.forEach(id -> {
                        CoreBpmnCommentBean filterBpmnComment = new CoreBpmnCommentBean();
                        filterBpmnComment.setTargetId(getDao().getTable() + "$" + id);
                        filterBpmnComment.setStatusCode("schemeedit");
                        filterBpmnCommentList.add(filterBpmnComment);
                    });

                    // 样品测试status置为preLogged
                    List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(noSampleProjectIdList, "PROJID");
                    orderTaskList.forEach(a -> a.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED));
                    orderTaskService.getDao().update(orderTaskList, "STATUS");

                    coreBpmnCommentService.getDao().deleteBy(filterBpmnCommentList, "statusCode");
                    //sampleService.insertByProject(noSampleProjectIdList, EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
                    // 新增报告数据,不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                    reportService.insertByProject(noSampleProjectIdList, false);
                    // 更新folder表状态
                    List<Long> folderIdList = orderTaskList.stream().filter(t -> !ObjectUtils.isEmpty(t.getFolderId())).map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
                    List<EmcFolderBean> folderList = new ArrayList<>();
                    folderIdList.forEach(e -> {
                        EmcFolderBean folder = new EmcFolderBean();
                        folder.setId(e);
                        folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
                        folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DONE);
                        folderList.add(folder);
                    });
                    folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
                    // 更新项目节点状态
                    noSampleProjectIdList.forEach(id -> {
                        EmcProjectBean updateProject = new EmcProjectBean();
                        updateProject.setId(id);
                        updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                        updateProjectList.add(updateProject);
                    });

                    // 更新项目节点状态
                    getDao().update(updateProjectList, "STATUS");
                }
                logger.info("普通送样项目数量 -> {}", planContractProjectList.size());

                // 采样任务
                List<EmcProjectBean> sampleProjectList = normalProjectList.stream().filter(e -> Constant.YES.equals(e.getSample())).collect(Collectors.toList());
                if (!sampleProjectList.isEmpty()) {
                    List<Long> sampleProjectIdList = sampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
                    List<EmcProjectBean> updateProjectList = new ArrayList<>();

                    if (!sampleProjectIdList.isEmpty()) {
                        // 不存在过质量控制的项目，新增主业务流程数据
                        projSchemeService.insertBpData(sampleProjectIdList);
                        // 新增报告数据，不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                        reportService.insertByProject(sampleProjectIdList, false);

                        // 更新项目节点状态
                        sampleProjectIdList.forEach(i -> {
                            EmcProjectBean updateProject = new EmcProjectBean();
                            updateProject.setId(i);
                            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                            updateProjectList.add(updateProject);
                        });
                    }

                    // 更新项目节点状态
                    getDao().update(updateProjectList, "STATUS");
                }

                logger.info("普通采样项目数量 -> {}", planContractProjectList.size());

                insertAuditRecordByProject(normalProjectList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
                List<Long> normalProjectIdList = normalProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

                ProjectNodeUtils.updateNodes(normalProjectIdList);

                logger.info("更新数据状态");
                projectList.forEach(p -> p.setExt$Item("dataStatus", ""));
                getDao().update(projectList, "dataStatus");

                // 给ERP发送方案审核结果
                logger.info("给ERP发送方案审核结果");
                erpOrderService.reviewOrderContract(projectList, false, null);

                // 推送项目信息到ERP
                logger.info("推送项目信息到ERP");
                projectList.forEach(project -> erpContractOrderService.pushPlanContractOrder(project));
            } catch (Exception e) {
                logger.error(e);
                String projectNos = projectList.stream().filter(p -> !StringUtils.isEmpty(p.getProjNo())).map(EmcProjectBean::getProjNo).collect(Collectors.joining(","));
                logger.error("项目【{}】业务数据生成失败，准备回退业务流程，错误原因为：{}", projectNos, e.getMessage());
                e.printStackTrace();
                undoBasic(wrapper);
                logger.info("项目【{}】业务业务流程回退成功", projectNos);
            }
        });
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        projectList = selectListByIds(projectIdList);

        // 方案审核退回时，向ERP发送审核结果
        erpOrderService.reviewOrderContract(projectList, true, wrapper.getParamValue("bpmn_comment"));

        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = rejectTaskBasic(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        EmcProjectBean updateProject = new EmcProjectBean();
        updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);

        getDao().updateByIds(updateProject, idList, "STATUS");
        ProjectNodeUtils.updateNodes(idList);

        return coreBpmnInstanceStatusDTOList;
    }

    private String getSequenceNo(int year, String key) {
        String projectNoPrefix = (year + "").substring(2) + key;
        List<EmcBizCategoryBean> bizCategoryList = bizCategoryService.getDao().selectListByOneColumnValue(key, "BIZCATEGORYID", Collections.singletonList("ID"));
        if (!bizCategoryList.isEmpty()) {
            List<EmcProjectBean> projectList = getDao().selectListByOneColumnValue(bizCategoryList.get(0).getId(), "BIZCATEGORYID", Collections.singletonList("PROJNO"));
            List<String> seqNoList = projectList.stream().filter(p -> p.getProjNo().startsWith(projectNoPrefix)).map(p -> p.getProjNo().replace(projectNoPrefix, "")).collect(Collectors.toList());
            OptionalInt maxValue = seqNoList.stream().filter(NumberUtils::isNumber).mapToInt(NumberUtils::parseInt).max();

            return  maxValue.isPresent() ? key + StringUtils.leftPad((maxValue.getAsInt() + 1) + "", 4) : key + "0001";
        }

        return getSequenceNo(year, key, 1).get(0);
    }

    private List<String> getSequenceNo(int year, String key, int qty) {
        String tableName = getDao().getTable();
        String id = tableName + "_" + year;
        CoreSequenceBean sequence = sequenceService.getDao().selectByIdIfPresent(id);
        if (sequence == null) {
            List<String> sequenceList = null;
            CoreSequenceBean basicSequence = sequenceService.getDao().selectByIdIfPresent(tableName);
            if (basicSequence != null) {
                sequence = new CoreSequenceBean();
                sequence.setId(id);
                sequence.setPattern(basicSequence.getPattern());
                sequenceList = ApplicationContextHelper.getNextSequenceList(tableName, key, qty);

                List<CoreSequenceParamDTO> paramList = new ArrayList<>();
                paramList.add(getNewSequenceParam(basicSequence.getPattern(), key, qty));
                sequence.setJsonParam(JSON.toJSONString(paramList));

                basicSequence.setJsonParam(null);
                sequenceService.getDao().insert(sequence);
                sequenceService.getDao().update(basicSequence, "JSONPARAM");
            }

            return sequenceList;
        }

        return ApplicationContextHelper.getNextSequenceList(id, key, qty);
    }

    private CoreSequenceParamDTO getNewSequenceParam(String pattern, String key, int qty) {
        CoreSequenceParamDTO param = new CoreSequenceParamDTO();

        List<String> substringList = StringUtils.substringsBetween(pattern, "[", "]");

        substringList.forEach(s -> {
            if ("key".equals(s)) {
                param.setKey(key);
            } else if ("yyyy".equals(s)) {
                param.setYyyy(String.format("%04d", LocalDate.now().getYear()));
            } else if ("yy".equals(s)) {
                param.setYy(String.format("%02d", LocalDate.now().getYear() % 100));
            } else if ("mm".equals(s)) {
                param.setMm(String.format("%02d", LocalDate.now().getMonthValue()));
            } else if ("dd".equals(s)) {
                param.setDd(String.format("%02d", LocalDate.now().getDayOfMonth()));
            } else if (StringUtils.repeatOf(s, "n")) {
                param.setN(qty);
            }
        });

        return param;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    @ValidateDataWith(PfUndoDeliveryProjectValidator.class)
    public void undoDelivery(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

        // 回退项目后，手动提交审核
        this.undoBasic(wrapper);

        // 将项目节点状态置为样品登记
        EmcProjectBean updateProject = new EmcProjectBean();
        updateProject.setProjNode(EmcBizConstant.PROJECT_CN_NODE_FABZ);
        updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
        getDao().updateByIds(updateProject, projectIdList, "PROJNODE", "STATUS");

        // 删除采样表
        List<EmcSampleBean> deleteSampleList = sampleService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
        sampleService.getDao().deleteBy(deleteSampleList);

        if (!deleteSampleList.isEmpty()) {
            List<Long> deleteSampleIdList = deleteSampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(deleteSampleIdList, "sampleId", Collections.singletonList("id"));
            orderList.forEach(order -> order.setSampleId(null));
            orderService.getDao().update(orderList, "sampleId");
        }

        // 删除业务表：样品测试、样品结果
        List<EmcOrderTaskBean> deleteTaskList = orderTaskService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
        List<Long> folderIdList = deleteTaskList.stream().map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = new ArrayList<>();
        folderIdList.forEach(e -> {
            EmcFolderBean folder = new EmcFolderBean();
            folder.setId(e);
            folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAFT);
            folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DRAFT);
            folderList.add(folder);
        });
        folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
        List<EmcOrderTaskResultBean> deleteResultList = orderTaskResultService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
        orderTaskService.getDao().deleteBy(deleteTaskList);
        orderTaskResultService.getDao().deleteBy(deleteResultList);

        // 删除report表
        List<EmcReportBean> deleteReportList = reportService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
        reportService.getDao().deleteBy(deleteReportList);

        // 手动在审核记录添加样品接收审核节点
        List<CoreBpmnCommentBean> updateBpmnCommentList = new ArrayList<>();
        projectIdList.forEach(id -> {
            CoreBpmnCommentBean updateBpmnComment = new CoreBpmnCommentBean();
            updateBpmnComment.setTargetId(this.getDao().getTable() + "$" + id);
            updateBpmnComment.setStatus("UNDO");
            updateBpmnComment.setTaskName(EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT);
            updateBpmnComment.setComment(wrapper.getParamValue("bpmn_comment"));
            updateBpmnCommentList.add(updateBpmnComment);
        });
        coreBpmnCommentService.getDao().update(updateBpmnCommentList, ArrayUtils.asList("taskName"), "targetId", "status");
    }

    @Override
    @Transactional
    public void schemeEditSubmit(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

        List<EmcProjSchemeBean> schemeList = projSchemeService.selectListByFilter(SearchFilter.instance().match("projId", projectIdList).filter(MatchPattern.OR).match("status", "1").filter(MatchPattern.SEQ));
        if (schemeList.isEmpty()) {
            return;
        }

        // 生成主业务流程数据
        projSchemeService.insertBpDataByScheme(schemeList);

        // 重置监测方案的状态
        schemeList.forEach(scheme -> scheme.setExt$Item("status", null));
        projSchemeService.getDao().update(schemeList, "status");

        List<Long> validProjectIdList = schemeList.stream().map(EmcProjSchemeBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(validProjectIdList, "projId");

        // 对于新生成的点位，采样日期、现场负责人和采样人设置为和其他点位的一致
        List<String> folderNoList = schemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<EmcFolderBean> newFolderList = folderList.stream().filter(folder -> folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        if (newFolderList.isEmpty()) {
            return;
        }

        List<EmcFolderBean> originalFolderList = folderList.stream().filter(folder -> !folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        newFolderList.forEach(folder -> originalFolderList.stream().filter(originalFolder -> !StringUtils.isEmpty(originalFolder.getSceneUserId())
                && !StringUtils.isEmpty(originalFolder.getSampleUserId())
                && !ObjectUtils.isEmpty(originalFolder.getPlanSampleDate())
                && !StringUtils.equals(originalFolder.getStatus(), "draft")).findFirst().ifPresent(originalFolder -> {
            folder.setPlanSampleDate(originalFolder.getPlanSampleDate());
            folder.setSceneUserId(originalFolder.getSceneUserId());
            folder.setSceneUserName(originalFolder.getSceneUserName());
            folder.setSampleUserId(originalFolder.getSampleUserId());
            folder.setSampleUserName(originalFolder.getSampleUserName());
        }));

        // 如果新点位没有设置采样日期、现场负责人和采样人(实际可只用判断一个字段)，说明该任务下其他点位都位于采样安排，则不自动提交新点位
        List<EmcFolderBean> submitFolderList = newFolderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getSceneUserId())).collect(Collectors.toList());
        if (submitFolderList.isEmpty()) {
            return;
        }

        List<Long> submitFolderIdList = submitFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("方案修改提交");

        folderService.getDao().update(submitFolderList, "planSampleDate", "sceneUserId", "sceneUserName", "sampleUserId", "sampleUserName");
        folderService.startProcess(submitFolderIdList, parameter);
    }

    @Override
    public String checkMoney(RestJsonWrapperBean wrapper) {
    	String flag = "more";
        EmcProjectBean objectProject = wrapper.parseUnique(EmcProjectBean.class);
        EmcProjectBean project = getDao().selectById(objectProject.getId());
        String contractNo = project.getExt$Item("contractno");
        //找到该合同下所有项目（除去当前项目）,所有的金额
        List<EmcProjectBean> allProjectBeans = selectListByFilter(SearchFilter.instance().match("contractNo", contractNo).filter(MatchPattern.OR)
                .match("id", project.getId()).filter(MatchPattern.DIFFER));
        List<Double> allProjectAmountList = new ArrayList<>();
        allProjectBeans.forEach(p ->{
            allProjectAmountList.add(Double.valueOf(p.getExt$Item("projectamount")));
        });
        //合同已开案金额总和(除去当前修改的项目)
        Double sumProjectAmount = allProjectAmountList.stream().collect(Collectors.summarizingDouble(value -> value)).getSum();

        List<CrmOpportunityContractBean> crmContractList = opportunityContractService.getDao().selectListByOneColumnValue(contractNo,"contractNo");
        Double contractAmountAll = crmContractList.get(0).getContractAmount();


        String editStrAmount = wrapper.getFilterValue("editAmount");
        if(editStrAmount == null ||editStrAmount == ""){
        	flag = "less";
        	return flag;
        }
        double editAmount=Double.valueOf(editStrAmount);
        Long id = objectProject.getId();
        String longId = id.toString();
        //需要的值 type 1开票合同金额 --type2收款金额 再修改金额是根据projid查到他的两个值
        List<CrmWriteOffBean>  typeOneList = crmWriteOffService.selectListByFilter(SearchFilter.instance().match("projId", longId).filter(MatchPattern.OR)
    			.match("type", "1").filter(MatchPattern.OR));
        List<CrmWriteOffBean>  typeTwoList = crmWriteOffService.selectListByFilter(SearchFilter.instance().match("projId", longId).filter(MatchPattern.OR)
    			.match("type", "2").filter(MatchPattern.OR));
        Double contractAmount = typeOneList.stream().collect(Collectors.summingDouble(CrmWriteOffBean ::getWriteoffamount));
        Double amount =typeTwoList.stream().collect(Collectors.summingDouble(CrmWriteOffBean ::getWriteoffamount));
        int res = contractAmount.compareTo(amount);
        Double compareAmount = 0.0D;
        //取开案合同金额和收款金额的最大值
        if(res>=0){
        	compareAmount = contractAmount;
        }else{
        	compareAmount = amount;
        }
        int comp = compareAmount.compareTo(editAmount);
        if(comp>0){
        	flag += "less";
        }
        //查找该项目的合同的合同总额与开案金额比较
        int compE = contractAmountAll.compareTo(editAmount);
        if(compE<0){
            flag += "error";
        }
        //合同已开案金额总和(除去当前修改的项目) +当前修改项目的金额 必须小于合同金额
        int compA  = contractAmountAll.compareTo(sumProjectAmount+editAmount);
        if(compA<0){
            flag += "over";
        }
        return flag;
    }

    @Override
    public String cancelCheck(RestJsonWrapperBean wrapper) {
    	List<EmcProjectBean> projList = wrapper.parse(EmcProjectBean.class);
        getDao().update(projList, "beforeStatus", "status", "cancelReason");
        return null;
    }

    @Override
    public String submitBeforeCheck(RestJsonWrapperBean wrapper) {
    	List<EmcProjectBean> projList = wrapper.parse(EmcProjectBean.class);
        List<EmcProjectBean> normalProjectList = projList.stream().filter(p -> !StringUtils.endsWith(p.getProjNo(), "X")).collect(Collectors.toList());
        if (normalProjectList.isEmpty()) {
            return "";
        }

        // 1、方法被停用；2、方法被误删除；3、方法下没有分析项组；4、分析项组下没有添加任务分析项的
    	String str1 ="";
    	String str2 ="";
    	String finalMessage = "";
    	//送样的项目
        List<EmcProjectBean> noSampleProjectList = normalProjectList.stream().filter(e -> Constant.NO.equals(e.getSample())).collect(Collectors.toList());
        if(!noSampleProjectList.isEmpty()){
        	List<Long> projIdList = noSampleProjectList.stream().map(EmcProjectBean :: getId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projIdList, "projId");
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
            List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
            //List<Long> methodIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean :: getMethodId).distinct().collect(Collectors.toList());

            List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean :: getItemGroupId).distinct().collect(Collectors.toList());
            String target2 = haveItemGroup(orderItemGroupIdList);
            if(!target2.isEmpty()){
            	str1 += target2;
            }
            //分析项组是否有对应的分析项
            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(orderItemGroupIdList);
        	List<Long> itemGroupIdList = capacityItemGroupList.stream().map(c ->c.getItemGroupId()).distinct().collect(Collectors.toList());
            List<EmcItemGroupBean> itemGroupOrgList = itemGroupService.getDao().selectListByIds(itemGroupIdList);

            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
            List<Long> testMethodIdList = itemGroupOrgList.stream().map(EmcItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());
            //orderItemGroupList
            List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByIds(testMethodIdList);
            List<Long> existTestMethodList = testMethodList.stream().map(EmcTestMethodBean :: getId).distinct().collect(Collectors.toList());
            //方法是否停用,是否方法被误删除
            if(testMethodList.size()!=testMethodList.size()){
              	//不存在的testmethodid
            	List<Long> testMethodIdDeleteList = testMethodIdList.stream().filter(t ->!existTestMethodList.contains(t)).distinct().collect(Collectors.toList());
            	List<EmcOrderItemGroupBean> orderDeleteItemGroupList =  orderItemGroupList.stream().filter(oi ->testMethodIdDeleteList.contains(oi.getTestMethodId())).collect(Collectors.toList());
            	List<String> deleteNote = new ArrayList<>();
            	orderDeleteItemGroupList.forEach(oi ->{
            		String deleteSingleNotes = oi.getMethodName()+"下的:"+oi.getTestName()+"被删除";
            		if(!deleteNote.contains(deleteSingleNotes)){
                		deleteNote.add(deleteSingleNotes);
            		}
            	});
            	String deleteNotes = StringUtils.join(deleteNote, ",");
            	str1 += deleteNotes;//方法被删除
            }

            String target = isNoActivedMethod(testMethodList);
            if(!StringUtils.isEmpty(target)){
            	str1 += target;
            }
            if(groupItemList.isEmpty()){
            	str1 += "分析项组下都无分析项";
            }
            List<Long> testIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean :: getTestId).distinct().collect(Collectors.toList());
            List<EmcTestBean> testList = testService.getDao().selectListByIds(testIdList);
            //应该根据样品类型来（相同的项目相同的样品类型不能出现这种情况）
            //不匹配的测试
            List<String> differErrorList = new ArrayList<>();
            //送样的
            projIdList.forEach(p ->{
                //单个项目下的样品
                List<EmcOrderBean> orderSingleList = orderList.stream().filter(o ->ObjectUtils.equals(p,o.getProjId())).collect(Collectors.toList());
                //根据样品类型来执行判断
                List<String> orderCategoryList =orderSingleList.stream().map(EmcOrderBean :: getOrderCategory).distinct().collect(Collectors.toList());
                orderCategoryList.forEach(oc ->{
                    //单个项目一种样品类型的list
                    List<EmcOrderBean> orderForCategoryList = orderSingleList.stream().filter(o ->StringUtils.equals(oc,o.getOrderCategory())).collect(Collectors.toList());
                    //一步步找到这些样品下是否具有相同测试不同方法的情况
                    List<Long> orderIdForOcList = orderForCategoryList.stream().map(EmcOrderBean ::getId).distinct().collect(Collectors.toList());
                    List<EmcOrderItemGroupBean> orderItemGroupForOcList = orderItemGroupList.stream().filter(oig ->orderIdForOcList.contains(oig.getOrderId())).collect(Collectors.toList());
                    List<Long> itemGroupIdF = orderItemGroupForOcList.stream().map(EmcOrderItemGroupBean ::getItemGroupId).distinct().collect(Collectors.toList());
                    //根据分析项组循环，相同分析项组的不同测试抛出异常
                    itemGroupIdF.forEach(ig ->{
                        //找到对应监测能力库分析项组
                        List<EmcCapacityItemGroupBean> capacityItemGroupForOcList = capacityItemGroupList.stream().filter(cig ->ObjectUtils.equals(ig,cig.getId())).collect(Collectors.toList());
                        //找到此对应的测试id，和测试方法id
                        List<Long> testIdForOcList = capacityItemGroupForOcList.stream().map(EmcCapacityItemGroupBean ::getTestId).distinct().collect(Collectors.toList());
                        List<Long> testMethodIdForOcList = capacityItemGroupForOcList.stream().map(EmcCapacityItemGroupBean ::getTestMethodId).distinct().collect(Collectors.toList());
                        List<EmcTestMethodBean> testMethodForOcList = testMethodList.stream().filter(tm ->testMethodIdForOcList.contains(tm.getId())).collect(Collectors.toList());
                        List<EmcTestBean> testForOcList = testList.stream().filter(t ->testIdForOcList.contains(t.getId())).collect(Collectors.toList());
                        String differError = isDifferMethodError(testForOcList,testMethodForOcList);
                        if(!StringUtils.isEmpty(differError)){
                            differErrorList.add(differError);
                        }
                    });

                });

            });
            //去掉空的
            String differErrorListStr ="";
            if(!differErrorList.isEmpty()){
                differErrorListStr = StringUtils.join(differErrorList,";");
                str1 += differErrorListStr;
            }

        }


        //采样的项目
        List<EmcProjectBean> sampleProjectList = normalProjectList.stream().filter(e -> Constant.YES.equals(e.getSample())).collect(Collectors.toList());
        if(sampleProjectList.isEmpty()){
        	return str1+str2;
        };
        List<Long> sampleProjIdList = sampleProjectList.stream().map(p ->p.getId()).collect(Collectors.toList());
        List<EmcProjSchemeBean> projSchemeList = projSchemeService.getDao().selectListByOneColumnValues(sampleProjIdList, "projId");
        List<Long> projSchemeIdList = projSchemeList.stream().map(ps ->ps.getId()).collect(Collectors.toList());
        List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId");

        List<Long> sampleOrderItemGroupIdlist = projOrderList.stream().map(po ->po.getItemGroupId()).distinct().collect(Collectors.toList());
        String targetThree = haveItemGroup(sampleOrderItemGroupIdlist);
        if(!targetThree.isEmpty()){
        	str2 += targetThree;
        }
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(sampleOrderItemGroupIdlist);
        if(!capacityItemGroupList.isEmpty()){
        	List<Long> itemGroupIdList = capacityItemGroupList.stream().map(c ->c.getItemGroupId()).distinct().collect(Collectors.toList());
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
            if(groupItemList.isEmpty()){
            	str2 += "分析项组下都无分析项";
            }
            //
            List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByIds(itemGroupIdList);
            List<Long> sampleTestMethodIdList  = itemGroupList.stream().map(i ->i.getTestMethodId()).distinct().collect(Collectors.toList());
            List<EmcTestMethodBean> sampleTestMethodList = testMethodService.getDao().selectListByIds(sampleTestMethodIdList);
            if(sampleTestMethodList.isEmpty()){
            	str2 += "项目下的测试都被删除了";

            }

            String targetTwo = isNoActivedMethod(sampleTestMethodList);
            if(!targetTwo.isEmpty()){
            	str2  += targetTwo;
            }
            List<Long> sampleTestIdList = projOrderList.stream().map(EmcProjOrderBean :: getTestId).distinct().collect(Collectors.toList());
            List<EmcTestBean> testSampleList = testService.getDao().selectListByIds(sampleTestIdList);
            //应该根据样品类型来（相同的项目相同的样品类型不能出现这种情况）
            //不匹配的测试
            List<String> differSampleErrorList = new ArrayList<>();
            sampleProjIdList.forEach(p ->{
                //单个项目下的项目方案
                List<EmcProjSchemeBean> projSchemeSingleList = projSchemeList.stream().filter(ps ->ObjectUtils.equals(p,ps.getProjId())).collect(Collectors.toList());
                //监测模板
                List<String> monitorTmplIdList = projSchemeSingleList.stream().map(EmcProjSchemeBean ::getMonitorTmplId).distinct().collect(Collectors.toList());


                //根据监测模板循环来执行判断
                monitorTmplIdList.forEach(ml ->{
                    //一步步找到这些样品下是否具有相同测试不同方法的情况
                    List<EmcProjSchemeBean> projSchemeForMLList = projSchemeSingleList.stream().filter(ps ->StringUtils.equals(ml,ps.getMonitorTmplId())).collect(Collectors.toList());
                    List<Long> projSchemeIdForMlList = projSchemeForMLList.stream().map(ps ->ps.getId()).distinct().collect(Collectors.toList());
                    List<EmcProjOrderBean> projOrderForMlList = projOrderList.stream().filter(po ->projSchemeIdForMlList.contains(po.getProjSchemeId())).collect(Collectors.toList());
                    List<Long> itemGroupIdM = projOrderForMlList.stream().map(EmcProjOrderBean ::getItemGroupId).distinct().collect(Collectors.toList());
                    //找到对应监测能力库分析项组
                    //根据分析项组循环，相同分析项组的不同测试抛出异常
                    itemGroupIdM.forEach(ig ->{
                        List<EmcCapacityItemGroupBean> capacityItemGroupForMLList = capacityItemGroupList.stream().filter(cig ->ObjectUtils.equals(ig,cig.getId())).collect(Collectors.toList());
                        //找到此对应的测试id，和测试方法id
                        List<Long> testIdForOcList = capacityItemGroupForMLList.stream().map(EmcCapacityItemGroupBean ::getTestId).distinct().collect(Collectors.toList());
                        List<Long> testMethodIdForMLList = capacityItemGroupForMLList.stream().map(EmcCapacityItemGroupBean ::getTestMethodId).distinct().collect(Collectors.toList());
                        List<EmcTestMethodBean> testMethodForMLList = sampleTestMethodList.stream().filter(tm ->testMethodIdForMLList.contains(tm.getId())).collect(Collectors.toList());
                        List<EmcTestBean> testForMLList = testSampleList.stream().filter(t ->testIdForOcList.contains(t.getId())).collect(Collectors.toList());
                        String differError = isDifferMethodError(testForMLList,testMethodForMLList);
                        if(!StringUtils.isEmpty(differError)){
                            differSampleErrorList.add(differError);
                        }
                    });

                });

            });
            //去掉空的
            String differSampleErrorListStr ="";
            if(!differSampleErrorList.isEmpty()){
                differSampleErrorListStr = StringUtils.join(differSampleErrorList,";");
                str2 += differSampleErrorList;
            }
        }

        finalMessage =str1+str2;
    	return finalMessage;
    }

    private String isNoActivedMethod(List<EmcTestMethodBean> testMethodList){
    	String flag = "";
        List<EmcTestMethodBean> testMethodNoActiveList = testMethodList.stream().filter(m ->!StringUtils.equals(m.getActivatedFlag(),"1")).collect(Collectors.toList());
        if(!testMethodNoActiveList.isEmpty()){
        	List<String> noActives = new ArrayList<>();
        	testMethodNoActiveList.forEach(tm ->{
        		String noActive ="";
        		EmcTestBean test = testService.selectById(tm.getTestId());
        		EmcMethodBean method = methodService.selectById(tm.getMethodId());
        		noActive = method.getMethodNo()+":下的"+test.getTestName()+"未启用";
        		noActives.add(noActive);
        	});
            flag = StringUtils.join(noActives, ";");//方法未启用
        	return flag;
        }
    	return flag;
    }
    //找出当前项目下相同测试但是方法不同的不允许提交
    private String isDifferMethodError(List<EmcTestBean> testList,List<EmcTestMethodBean> testMethodIdAllList){
        List<Long> testIdList = testList.stream().map(EmcTestBean :: getId).distinct().collect(Collectors.toList());
        List<String> diffNotes = new ArrayList<>();
        String diffNoteStr = "";
        testIdList.forEach(t ->{
            List<EmcTestMethodBean> testMethodSingleList  = testMethodIdAllList.stream().filter(tm ->ObjectUtils.equals(tm.getTestId(),t)).collect(Collectors.toList());
            List<Long> methodIdList = testMethodSingleList.stream().map(EmcTestMethodBean ::getMethodId).distinct().collect(Collectors.toList());
            if(methodIdList.size()>1){
               String testname =  testList.stream().filter(test ->ObjectUtils.equals(test.getId(),t)).collect(Collectors.toList()).get(0).getTestName();
               String differNote = testname +"测试有不同的方法";
                diffNotes.add(differNote);
            }
        });
        diffNoteStr = StringUtils.join(diffNotes, ";");//相同测试不同方法
        return diffNoteStr;
    }

    private String haveItemGroup(List<Long> orderItemGroupIdList){
    	String note = "";
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(orderItemGroupIdList);
        List<Long> capacityItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean :: getId).collect(Collectors.toList());
        if(!capacityItemGroupList.isEmpty()){
        	if(capacityItemGroupList.size()==orderItemGroupIdList.size()){
                List<Long> itemGroupIdList = capacityItemGroupList.stream().map(c ->c.getItemGroupId()).distinct().collect(Collectors.toList());
                List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByIds(itemGroupIdList);

                if(!itemGroupList.isEmpty()){
                    if(itemGroupList.size()!=itemGroupIdList.size()){
                        List<Long> itemGroupIdExistList = itemGroupList.stream().map(i ->i.getId()).distinct().collect(Collectors.toList());
                        List<Long> needItemGroupIdList =  itemGroupIdList.stream().filter(t ->!itemGroupIdExistList.contains(t)).distinct().collect(Collectors.toList());
                        List<EmcCapacityItemGroupBean> capacityNeedItemGroupList = capacityItemGroupList.stream()
                        		.filter(cig ->needItemGroupIdList.contains(cig.getItemGroupId())).collect(Collectors.toList());
                        List<Long> testMethodIdList = capacityNeedItemGroupList.stream().map(EmcCapacityItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());
                        List<EmcTestMethodBean> testMethodNoItemGroupList = testMethodService.getDao().selectListByIds(testMethodIdList);
                        if(!testMethodNoItemGroupList.isEmpty()){
                        	List<String> noItemGroups = new ArrayList<>();
                        	testMethodNoItemGroupList.forEach(tm ->{
                        		String noItemGroup ="";
                        		EmcTestBean test = testService.selectById(tm.getTestId());
                        		EmcMethodBean method = methodService.selectById(tm.getMethodId());
                        		noItemGroup = method.getMethodNo()+":下的"+test.getTestName()+"测试管理缺少分析项";
                        		noItemGroups.add(noItemGroup);
                        	});
                        	note = StringUtils.join(noItemGroups,"");//方法下无分析项组
                        	return note;
                        }
                    }
                }else{
                	note = "项目所有方法测试管理都无对应分析项组";//方法下无分析项组
                	return note;
                }


        	}else{
        		List<Long> extraOrderItemGroupIdList= orderItemGroupIdList.stream().filter(oi ->!capacityItemGroupIdList.contains(oi)).distinct().collect(Collectors.toList());
        		List<EmcOrderItemGroupBean> extraOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(extraOrderItemGroupIdList, "itemGroupId");

            	List<String> extraNotes = new ArrayList<>();
            	extraOrderItemGroupList.forEach(oi ->{
            		String extraNote = oi.getMethodName()+"下的:"+oi.getTestName()+"在监测能力库的分析项组被删除，请联系管理员";
            		if(!extraNotes.contains(extraNote)){
            			extraNotes.add(extraNote);
            		}
            	});
            	note = StringUtils.join(extraNotes, ",");//方法下无分析项组
            	return note;
        	}

        }else{
        	note = "项目所有方法监测能力库都无对应分析项组";//方法下无分析项组
        	return note;
        }
    	return note;
    }

    @Override
    public Page<PfProjectTaskProcessDTO> selectProjectTaskProcess(RestJsonWrapperBean wrapper) {
        List<PfProjectTaskProcessDTO> list = new ArrayList<>();
        //所有的项目
        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");
        Page<EmcProjectBean> taskProjectPage  =  selectPaginationByFilter(SearchFilter.instance()
    			.match("ORGID",parentOrgId).filter(MatchPattern.OR),wrapper);
        List<EmcProjectBean> projectList = taskProjectPage.getRows();

        //任务进度查询要排除其他公司的去除报告完成的项目吗?
        if (!CollectionUtils.isEmpty(projectList)) {
            List<Long> proIds = projectList.stream()
                    .map(EmcProjectBean::getId).distinct().collect(Collectors.toList());
            //关联的样品
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(proIds, "projId",Arrays.asList("id","projId","sampleId","monitorTmplId","sampleBeginDate","sampleCompleteDate","orderCategory"));
            List<Long> orderIds = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());


            //关联的审核记录
        	List<EmcAuditRecordBean> emcAuditList = auditRecordService.getDao().selectListByOneColumnValues(proIds, "projId",Arrays.asList("id", "auditCategory","projId","operateCategory","taskName","comments","createdByName","createdTime"));

        	//关联的样品测试
/*    		List<EmcOrderTaskBean> emcOldOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance()
        			.match("projId", proIds).filter(MatchPattern.OR));
    		*/
        	List<EmcOrderTaskBean> emcOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance()
        			.match("projId", proIds).filter(MatchPattern.OR).match("status", "preLogged").filter(MatchPattern.DIFFER)
        			.match("status", "draw").filter(MatchPattern.DIFFER)
        			);

        	List<String> runOldIdList = emcOrderTaskList.stream().map(EmcOrderTaskBean :: getRunId).collect(Collectors.toList());
        	List<String> runIdList =runOldIdList.stream().filter(r ->!StringUtils.isEmpty(r)).collect(Collectors.toList());
        	List<EmcRunBean> emcRunList = new ArrayList<>();
        	if(!runIdList.isEmpty()){
            	List<EmcRunBean> emcRunOldList = runService.getDao().selectListByIds(runIdList,Arrays.asList("id","auditedUserName","auditedDate"));

            	emcRunList.addAll(emcRunOldList);
        	}


            //关联的报告
            List<EmcReportBean> emcReportList= reportService.getDao().selectListByOneColumnValues(proIds, "projId",Arrays.asList("id","projId"));

            //关联的模板
        	//List<EmcMonitorTmplBean> emcMonitorTmpllist = monitorTmplService.getDao().selectListByIds(monitorTmplIds);
            //关联的文件
        	List<Long> reportIdList =emcReportList.stream().map(EmcReportBean :: getId).collect(Collectors.toList());
           	List<CoreFileBean> reportFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", "T_EMC_REPORT$").filter(MatchPattern.SB)
        			.match("bizcategory", "").filter(MatchPattern.DIFFER));
        	List<CoreFileBean> reportNewFileList  = new ArrayList<>();
        	if(!reportFileList.isEmpty()){
            	reportIdList.forEach(r ->{
            		CoreFileBean coreFile = new CoreFileBean();
            		List<CoreFileBean> coreFileList = reportFileList.stream().filter(rp ->StringUtils.equals("T_EMC_REPORT$"+r,rp.getTargetId())).collect(Collectors.toList());
            		if(!coreFileList.isEmpty()){
                		coreFile = coreFileList.get(0);
            		}
            		reportNewFileList.add(coreFile);
            	});
        	}


            //关联的orderitemgroup
        	//List<EmcOrderItemGroupBean> orderItemOldGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIds, "orderId",Arrays.asList("id","orderId","testMethodId","testName","schemeName"));
        	//List<EmcOrderItemGroupBean> orderItemGroupList = orderItemOldGroupList.stream().filter(oi ->StringUtils.equals("default", oi.getSchemeName())).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIds).filter(MatchPattern.OR)
            		.match("schemeName", "default").filter(MatchPattern.SEQ));

        	List<Long> testMethodIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());

        	List<EmcTestCapacityBean>  testCapacityList= testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
            		.match("orgId", parentOrgId).filter(MatchPattern.SEQ));

        	List<String> testItemSortList = new ArrayList<>();
        	testCapacityList.forEach(tc ->{
        		String testItemSort = tc.getExt$Item("TESTITEMSORT");
        		if(!testItemSortList.contains(testItemSort)){
            		testItemSortList.add(testItemSort);
        		}
        	});
        	List<CoreCodeBean> coreCodeList = codeService.getDao().selectListByOneColumnValue("testitemsort", "codeCategoryId",Arrays.asList("id","codeCategoryId","code","codeName"));
        	List<CoreCodeBean> coreCodeNewList = coreCodeList.stream().filter(cc ->testItemSortList.contains(cc.getCode())).collect(Collectors.toList());

        	//关联的点位
            List<EmcFolderBean> emcFolderList = folderService.selectListByFilter(SearchFilter.instance().match("PROJID", proIds).filter(MatchPattern.OR));

            //相同项目有不同类型样品的需要根据样品类型重复显示
            projectList.forEach(EmcProjectBean -> {
            	List<EmcOrderBean> orders = orderList.stream().filter(o ->ObjectUtils.equals(EmcProjectBean.getId(),o.getProjId())).collect(Collectors.toList());
            	List<EmcOrderBean> sampleOrders = orders.stream().filter(o ->o.getSampleBeginDate()!=null)
            			.filter(o ->o.getSampleCompleteDate()!=null).collect(Collectors.toList());
            	if(!orders.isEmpty()){
	            	List<String> orderCategoryList = orders.stream().map(EmcOrderBean :: getOrderCategory).distinct().collect(Collectors.toList());
	            	List<Long> orderIdPList = orders.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());


	        		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	            	if(orderCategoryList.size()<=1){
	            		PfProjectTaskProcessDTO pfProjectTaskProcessDTO = new PfProjectTaskProcessDTO();
	            		//项目编号
	                	pfProjectTaskProcessDTO.setProjNo(EmcProjectBean.getProjNo());
	                	//项目名称
	                	pfProjectTaskProcessDTO.setProjName(EmcProjectBean.getProjName());
	                	//销售
	                	pfProjectTaskProcessDTO.setBiddername(EmcProjectBean.getExt$Item("biddername"));
	                	//开案日期
	                	pfProjectTaskProcessDTO.setCreatedTime(EmcProjectBean.getCreatedTime());

	                	//样品类型
	                	if(orderCategoryList.isEmpty()){
		                	pfProjectTaskProcessDTO.setOrderCategory("/");
	                	}else{
		                	pfProjectTaskProcessDTO.setOrderCategory(orderCategoryList.get(0));
	                	}
	                	if(!EmcProjectBean.getSample().equals("0")&&!sampleOrders.isEmpty()){
	                		//采样查询最早的采样开始日期及最晚的采样完成日期
	                    	List<Long> folderIdList = sampleOrders.stream().map(EmcOrderBean ::getFolderId).distinct().collect(Collectors.toList());
	                    	//List<Long> sampleIdList = orders.stream().map(EmcOrderBean ::getSampleId).distinct().collect(Collectors.toList());
	                    	//List<EmcSampleBean> sampleList = emcSampleList.stream().filter(s ->sampleIdList.contains(s.getId())).collect(Collectors.toList());
	                    	List<EmcFolderBean> folderList = emcFolderList.stream().filter(s ->folderIdList.contains(s.getId())).collect(Collectors.toList());
	                    	List<String> sampleUserList = folderList.stream().map(EmcFolderBean :: getSampleUserName).distinct().collect(Collectors.toList());
	                    	String sampleUsers = StringUtils.join(sampleUserList, ",");

	                			EmcOrderBean minSampleBeginDateOrder = sampleOrders.stream().min(Comparator.comparing(EmcOrderBean::getSampleBeginDate)).get();
		                		EmcOrderBean maxSampleCompleteOrder = sampleOrders.stream().max(Comparator.comparing(EmcOrderBean::getSampleCompleteDate)).get();
		                		LocalDate minSampleBeginDateOld = minSampleBeginDateOrder.getSampleBeginDate();
		                		LocalDate maxSampleCompleteDateOld = maxSampleCompleteOrder.getSampleCompleteDate();
		                		if(maxSampleCompleteDateOld!=null&&minSampleBeginDateOld!=null){
		                			String minSampleBeginDate = minSampleBeginDateOld.format(fmt);
		 	                        String maxSampleCompleteDate = maxSampleCompleteDateOld.format(fmt);
		 	                        //采样开始
		 	                		pfProjectTaskProcessDTO.setSampleBegin(sampleUsers+minSampleBeginDate);
		 	                		//采样结束
		 	                		pfProjectTaskProcessDTO.setSampleEnd(sampleUsers+maxSampleCompleteDate);
		                		}




	                		List<EmcAuditRecordBean> auditList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
	                		List<EmcAuditRecordBean> auditSampleList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("提交", au.getOperateCategory()))
	                				.filter(au ->StringUtils.equals("采样审核", au.getTaskName())).filter(au ->StringUtils.contains("提交", au.getComments())).collect(Collectors.toList());
	                		if(!auditSampleList.isEmpty()){
		                		EmcAuditRecordBean maxCreatedByNameRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
		                		EmcAuditRecordBean maxCreatedTimeRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
		                		String sampleAssignUser = maxCreatedByNameRecord.getCreatedByName();
		                		LocalDateTime sampleAssignTimeOld = maxCreatedTimeRecord.getCreatedTime();
		                		if(sampleAssignUser!=null&&sampleAssignTimeOld!=null){
		                			String sampleAssignTime = sampleAssignTimeOld.format(fmt);
		  	                        //采样分配
		  	                		pfProjectTaskProcessDTO.setSampleAssign(sampleAssignUser+sampleAssignTime);
		                		}
	                		}



	                		List<EmcAuditRecordBean> auditSampleEndList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("审核", au.getOperateCategory()))
	                				.filter(au ->StringUtils.equals("采样审核", au.getTaskName())).filter(au ->StringUtils.contains("通过", au.getComments())).collect(Collectors.toList());
	                		if(!auditSampleEndList.isEmpty()){
		                		EmcAuditRecordBean maxCreatedByNameEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
		                		EmcAuditRecordBean maxCreatedTimeEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();

		                   		String sampleEndAssignUser = maxCreatedByNameEndRecord.getCreatedByName();
		                		LocalDateTime sampleEndAssignTimeOld = maxCreatedTimeEndRecord.getCreatedTime();

		                		if(sampleEndAssignUser!=null&&sampleEndAssignTimeOld!=null){
			                        String sampleEndAssignTime = sampleEndAssignTimeOld.format(fmt);
			                        //采样审核
			                		pfProjectTaskProcessDTO.setSampleAudit(sampleEndAssignUser+sampleEndAssignTime);
		                        }

	                		}



	                	}else{
	                		pfProjectTaskProcessDTO.setSampleBegin("/");
	                		pfProjectTaskProcessDTO.setSampleEnd("/");
	                		pfProjectTaskProcessDTO.setSampleAssign("/");

	                	}
	            		List<EmcAuditRecordBean> auditOrderReceiptList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
	            		List<EmcAuditRecordBean> orderReceiptList = auditOrderReceiptList.stream().filter(au ->StringUtils.equals("样品管理", au.getAuditCategory())).filter(au ->StringUtils.equals("接收", au.getOperateCategory()))
	            				.filter(au ->StringUtils.equals("样品接收", au.getTaskName())).filter(au ->StringUtils.contains("接收", au.getComments())).collect(Collectors.toList());
	            		if(!orderReceiptList.isEmpty()){
	            			EmcAuditRecordBean maxCreatedByNameOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
		            		EmcAuditRecordBean maxCreatedTimeOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
		            		String sampleHandvor = maxCreatedByNameOrderReceiptBean.getCreatedByName();
		            		LocalDateTime sampleHandvorTimeOld = maxCreatedTimeOrderReceiptBean.getCreatedTime();
		                    String sampleHandvorTime = sampleHandvorTimeOld.format(fmt);
		                    //样品交接
		            		pfProjectTaskProcessDTO.setSampleHandover(sampleHandvor+sampleHandvorTime);
		            		pfProjectTaskProcessDTO.setTaskAssign(sampleHandvor+sampleHandvorTime);
	            		}


	                	//只有一个样品类型模板tmpl也是一样的
	                	String monitorTmplId = orders.get(0).getMonitorTmplId();
	                	List<EmcOrderTaskBean> orderTaskList = emcOrderTaskList.stream().filter(ot ->ObjectUtils.equals(EmcProjectBean.getId(), ot.getProjId()))
	                			.filter(ot ->ObjectUtils.equals(monitorTmplId, ot.getMonitorTmplId())).collect(Collectors.toList());

	                    if(!emcRunList.isEmpty()){
                        	//理化批
		                	List<EmcOrderTaskBean> orderTaskMatchPhysList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
		                	List<String> newRunIdPhysList = new ArrayList<>();
		                	if(!orderTaskMatchPhysList.isEmpty()){
			                	List<String> runIdPhysList = orderTaskMatchPhysList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
			                	newRunIdPhysList.addAll(runIdPhysList);
		                	}
		                	//金属批
		                	List<EmcOrderTaskBean> orderTaskMatchMetalList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020202",ok.getAnalysisOrgId())).collect(Collectors.toList());
		                	List<String> newRunIdMetalList = new ArrayList<>();
		                	if(!orderTaskMatchMetalList.isEmpty()){
			                	List<String> runIdMetalList = orderTaskMatchMetalList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
			                	newRunIdMetalList.addAll(runIdMetalList);
		                	}
		                	//有机批
		                	List<EmcOrderTaskBean> orderTaskMatchOrganicList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020201",ok.getAnalysisOrgId())).collect(Collectors.toList());
		                	List<String> newRunIdOrganicList = new ArrayList<>();
		                	if(!orderTaskMatchOrganicList.isEmpty()){
			                	List<String> runIdOrganicList = orderTaskMatchOrganicList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
			                	newRunIdOrganicList.addAll(runIdOrganicList);
		                	}
		                	//二噁英批
		                	List<EmcOrderTaskBean> orderTaskMatchDiohyingList =  orderTaskList.stream().filter(ok ->StringUtils.equals("050601020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
		                	List<String> newRunIdDiohyingList = new ArrayList<>();
		                	if(!orderTaskMatchDiohyingList.isEmpty()){
			                	List<String> runIdDiohyingList = orderTaskMatchDiohyingList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
			                	newRunIdDiohyingList.addAll(runIdDiohyingList);
		                	}
                        	//理化
		                	if(!newRunIdPhysList.isEmpty()){
	                        	List<EmcRunBean> runPhysMatchList = emcRunList.stream().filter(r ->newRunIdPhysList.contains(r.getId()))
	                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
	                        			.filter(r ->r.getAuditedDate()!=null)
	                        			.collect(Collectors.toList());
	                        	if(!runPhysMatchList.isEmpty()){
		                        	EmcRunBean physAuditorMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
		                        	EmcRunBean physAuditedDateMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
		                    		String physAuditorMax = physAuditorMaxBean.getAuditedUserName();
		                    		LocalDateTime physAuditedDateMaxOld = physAuditedDateMaxBean.getAuditedDate();
		                    		if(physAuditorMax!=null&&physAuditedDateMaxOld!=null){
			                            String physAuditedDateMax = physAuditedDateMaxOld.format(fmt);
			                            pfProjectTaskProcessDTO.setPhysAudit(physAuditorMax+physAuditedDateMax);
		                    		}

	                        	}
		                	}

		                	if(!newRunIdMetalList.isEmpty()){
	                        	//金属
	                        	List<EmcRunBean> runMetalMatchList = emcRunList.stream().filter(r ->newRunIdMetalList.contains(r.getId()))
	                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
	                        			.filter(r ->r.getAuditedDate()!=null)
	                        			.collect(Collectors.toList());
	                           	if(!runMetalMatchList.isEmpty()){
	                           		EmcRunBean metalAuditorMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
		                        	EmcRunBean metalAuditedDateMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
		                    		String metalAuditorMax = metalAuditorMaxBean.getAuditedUserName();
		                    		LocalDateTime metalAuditedDateMaxOld = metalAuditedDateMaxBean.getAuditedDate();
		                    		if(metalAuditorMax!=null&&metalAuditedDateMaxOld!=null){
		                    			String metalAuditedDateMax = metalAuditedDateMaxOld.format(fmt);
			                            pfProjectTaskProcessDTO.setMetalAudit(metalAuditorMax+metalAuditedDateMax);
			                        }
	                           	}
		                	}
		                	if(!newRunIdOrganicList.isEmpty()){
	                        	//有机
	                        	List<EmcRunBean> runOrganicMatchList = emcRunList.stream().filter(r ->newRunIdOrganicList.contains(r.getId()))
	                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
	                        			.filter(r ->r.getAuditedDate()!=null)
	                        			.collect(Collectors.toList());
	                           	if(!runOrganicMatchList.isEmpty()){
	                           		EmcRunBean organicAuditorMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
		                        	EmcRunBean organicAuditedDateMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
		                    		String organicAuditorMax = organicAuditorMaxBean.getAuditedUserName();
		                    		LocalDateTime organicAuditedDateMaxOld = organicAuditedDateMaxBean.getAuditedDate();
		                    		if(organicAuditorMax!=null&&organicAuditedDateMaxOld!=null){
			                            String organicAuditedDateMax = organicAuditedDateMaxOld.format(fmt);
			                            pfProjectTaskProcessDTO.setMetalAudit(organicAuditorMax+organicAuditedDateMax);
		                    		}
	                           	}
		                	}

                            if(!newRunIdDiohyingList.isEmpty()){
	                        	//二噁英
	                        	List<EmcRunBean> runDiohyingMatchList = emcRunList.stream().filter(r ->newRunIdDiohyingList.contains(r.getId()))
	                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
	                        			.filter(r ->r.getAuditedDate()!=null)
	                        			.collect(Collectors.toList());
	                           	if(!runDiohyingMatchList.isEmpty()){
	                           		EmcRunBean diohyingAuditorMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
		                        	EmcRunBean diohyingAuditedDateMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
		                    		String diohyingAuditorMax = diohyingAuditorMaxBean.getAuditedUserName();
		                    		LocalDateTime diohyingAuditedDateMaxOld = diohyingAuditedDateMaxBean.getAuditedDate();
		                    		if(diohyingAuditorMax!=null&&diohyingAuditedDateMaxOld!=null){
			                            String diohyingAuditedDateMax = diohyingAuditedDateMaxOld.format(fmt);
			                            pfProjectTaskProcessDTO.setDiohyingAudit(diohyingAuditorMax+diohyingAuditedDateMax);
		                    		}
	                           	}
                            }



                        }

	                    //报告编制
	                    List<EmcOrderTaskBean> orderTaskDoneList  = orderTaskList.stream().filter(ot ->StringUtils.equals("done", ot.getStatus())).collect(Collectors.toList());
	                    if(orderTaskDoneList.isEmpty()){
	                    	pfProjectTaskProcessDTO.setReportEdit("/");
	                    }else{
	                        List<EmcReportBean> reportList = emcReportList.stream().filter(rp ->ObjectUtils.equals(EmcProjectBean.getId(), rp.getId())).collect(Collectors.toList());
	                        if(!reportList.isEmpty()){
	                        	//报告文件
	                        	if(!reportNewFileList.isEmpty()){
	                            	List<CoreFileBean> reportMatchFileList = reportNewFileList.stream().filter(cf ->StringUtils.equals("T_EMC_REPORT$"+reportList.get(0).getId(),cf.getTargetId())).collect(Collectors.toList()) ;

	                            	CoreFileBean maxCreatedTimeBean  = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedTime)).get();
	                            	CoreFileBean maxCreatedByNameBean = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedByName)).get();
	                            	String maxCreatedByName = maxCreatedByNameBean.getCreatedByName();
	                        		LocalDateTime maxCreatedTimeOld = maxCreatedTimeBean.getCreatedTime();
	                                String maxCreatedTime = maxCreatedTimeOld.format(fmt);
	                                //报告编制
	                                pfProjectTaskProcessDTO.setReportEdit(maxCreatedByName+maxCreatedTime);
	                        	}

	                        }
	                    }
	                    //检测项目
	                    List<EmcOrderItemGroupBean> orderNewItemGroupList = orderItemGroupList.stream().filter(oi ->orderIdPList.contains(oi.getOrderId())).collect(Collectors.toList());
	                    if(!orderNewItemGroupList.isEmpty()){
	                    	List<Long> testMethodNewIdList = orderNewItemGroupList.stream().map(EmcOrderItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());
		                	List<EmcTestCapacityBean>  testCapacityOlderList= testCapacityList.stream().filter(tc ->testMethodNewIdList.contains(tc.getTestMethodId())).distinct().collect(Collectors.toList());
		                	List<EmcTestCapacityBean>  testCapacityNewList= testCapacityOlderList.stream().filter(tc ->!StringUtils.isEmpty(tc.getExt$Item("TESTITEMSORT"))).distinct().collect(Collectors.toList());

		                	//无检测项目分类就直接取orderitemgroup表的testname
		                	List<String> tests = new ArrayList<>();
		                	if(!testCapacityNewList.isEmpty()){
			                	testCapacityNewList.forEach(tc ->{
			            			String testName = "";
			                		if(tc.getExt$Item("TESTITEMSORT").isEmpty()){
			                			//取样品分析项表的testname
			                			List<EmcOrderItemGroupBean>  orderMatchItemGroupList = orderNewItemGroupList.stream().filter(oi ->ObjectUtils.equals(tc.getTestMethodId(), oi.getTestMethodId())).collect(Collectors.toList());
			                			if(!orderMatchItemGroupList.isEmpty()){
				                			testName = orderMatchItemGroupList.get(0).getTestName();
			                			}
			                		}else{
			                			List<CoreCodeBean> coreCodeMatchList = coreCodeNewList.stream().filter(cc ->StringUtils.equals(tc.getExt$Item("TESTITEMSORT"), cc.getCode())).collect(Collectors.toList());
			                			if(!coreCodeMatchList.isEmpty()){
				                			testName = coreCodeMatchList.get(0).getCodeName();
			                			}
			                		}
			                		if(!tests.contains(testName)){
				                		tests.add(testName);
			                		}
			                	});
			                	if(!tests.isEmpty()){
				                	String detectionMetrics = StringUtils.join(tests, ",");
				                	//检测指标
				                	pfProjectTaskProcessDTO.setDetectionMetrics(detectionMetrics);
			                	}

		                	}
	                    }

	                	list.add(pfProjectTaskProcessDTO);
	            	}else{
	            		orderCategoryList.forEach(oc ->{
	                		PfProjectTaskProcessDTO pfProjectTaskProcessDTO = new PfProjectTaskProcessDTO();
	                		//项目编号
	                    	pfProjectTaskProcessDTO.setProjNo(EmcProjectBean.getProjNo());
	                    	//项目名称
	                    	pfProjectTaskProcessDTO.setProjName(EmcProjectBean.getProjName());
	                    	//销售
	                    	pfProjectTaskProcessDTO.setBiddername(EmcProjectBean.getExt$Item("biddername"));
		                	//开案日期
		                	pfProjectTaskProcessDTO.setCreatedTime(EmcProjectBean.getCreatedTime());
		                	//样品类型
		                	if(oc.isEmpty()){
			                	pfProjectTaskProcessDTO.setOrderCategory("/");
		                	}else{
			                	pfProjectTaskProcessDTO.setOrderCategory(oc);
		                	}
	                    	if(!EmcProjectBean.getSample().equals("0")&&!sampleOrders.isEmpty()){
	                    		//采样查询最早的采样开始日期及最晚的采样完成日期
	                        	List<Long> folderIdList = sampleOrders.stream().map(EmcOrderBean ::getFolderId).distinct().collect(Collectors.toList());
	                        	//List<Long> sampleIdList = orders.stream().map(EmcOrderBean ::getSampleId).distinct().collect(Collectors.toList());
	                        	//List<EmcSampleBean> sampleList = emcSampleList.stream().filter(s ->sampleIdList.contains(s.getId())).collect(Collectors.toList());

	                        	List<EmcFolderBean> folderList = emcFolderList.stream().filter(s ->folderIdList.contains(s.getId())).collect(Collectors.toList());
	                        	List<String> sampleUserList = folderList.stream().map(EmcFolderBean :: getSampleUserName).distinct().collect(Collectors.toList());
	                        	String sampleUsers = StringUtils.join(sampleUserList, ",");
		                			EmcOrderBean minSampleBeginDateOrder = sampleOrders.stream().min(Comparator.comparing(EmcOrderBean::getSampleBeginDate)).get();
			                		EmcOrderBean maxSampleCompleteOrder = sampleOrders.stream().max(Comparator.comparing(EmcOrderBean::getSampleCompleteDate)).get();
		                    		LocalDate minSampleBeginDateOld = minSampleBeginDateOrder.getSampleBeginDate();
		                    		LocalDate maxSampleCompleteDateOld = maxSampleCompleteOrder.getSampleCompleteDate();
			                		if(maxSampleCompleteDateOld!=null&&minSampleBeginDateOld!=null){
			                			String minSampleBeginDate = minSampleBeginDateOld.format(fmt);
			 	                        String maxSampleCompleteDate = maxSampleCompleteDateOld.format(fmt);
			 	                        //采样开始
			 	                		pfProjectTaskProcessDTO.setSampleBegin(sampleUsers+minSampleBeginDate);
			 	                		//采样结束
			 	                		pfProjectTaskProcessDTO.setSampleEnd(sampleUsers+maxSampleCompleteDate);
			                		}



	                    		List<EmcAuditRecordBean> auditList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
	                    		List<EmcAuditRecordBean> auditSampleList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("提交", au.getOperateCategory()))
	                    				.filter(au ->StringUtils.equals("采样安排", au.getTaskName())).filter(au ->StringUtils.contains("提交", au.getComments())).collect(Collectors.toList());
	                    		if(!auditSampleList.isEmpty()){
		                    		EmcAuditRecordBean maxCreatedByNameRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
		                    		EmcAuditRecordBean maxCreatedTimeRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
		                    		String sampleAssignUser = maxCreatedByNameRecord.getCreatedByName();
		                    		LocalDateTime sampleAssignTimeOld = maxCreatedTimeRecord.getCreatedTime();
		                    		if(sampleAssignUser!=null &&sampleAssignTimeOld!=null){
			                            String sampleAssignTime = sampleAssignTimeOld.format(fmt);
			                            //采样分配
			                    		pfProjectTaskProcessDTO.setSampleAssign(sampleAssignUser+sampleAssignTime);

		                    		}

	                    		}

	                    		List<EmcAuditRecordBean> auditSampleEndList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("审核", au.getOperateCategory()))
	                    				.filter(au ->StringUtils.equals("采样审核", au.getTaskName())).filter(au ->StringUtils.contains("通过", au.getComments())).collect(Collectors.toList());
	                    		if(!auditSampleEndList.isEmpty()){
		                    		EmcAuditRecordBean maxCreatedByNameEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
		                    		EmcAuditRecordBean maxCreatedTimeEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
		                    		String sampleEndAssignUser = maxCreatedByNameEndRecord.getCreatedByName();
		                    		LocalDateTime sampleEndAssignTimeOld = maxCreatedTimeEndRecord.getCreatedTime();
		                    		if(sampleEndAssignUser!=null&&sampleEndAssignTimeOld!=null){
		                    			   String sampleEndAssignTime = sampleEndAssignTimeOld.format(fmt);
		   	                            //采样审核
		   	                    		pfProjectTaskProcessDTO.setSampleAssign(sampleEndAssignUser+sampleEndAssignTime);
		                    		}
	                    		}



	                    	}else{
	                    		pfProjectTaskProcessDTO.setSampleBegin("/");
	                    		pfProjectTaskProcessDTO.setSampleEnd("/");
	                    		pfProjectTaskProcessDTO.setSampleAssign("/");
	                    		pfProjectTaskProcessDTO.setSampleAudit("/");

	                    	}
	                		List<EmcAuditRecordBean> auditOrderReceiptList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
	                		List<EmcAuditRecordBean> orderReceiptList = auditOrderReceiptList.stream().filter(au ->StringUtils.equals("样品管理", au.getAuditCategory())).filter(au ->StringUtils.equals("接收", au.getOperateCategory()))
	                				.filter(au ->StringUtils.equals("样品接收", au.getTaskName())).filter(au ->StringUtils.contains("接收", au.getComments())).collect(Collectors.toList());
	                		if(!orderReceiptList.isEmpty()){
	                			EmcAuditRecordBean maxCreatedByNameOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
		                		EmcAuditRecordBean maxCreatedTimeOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
		                		String sampleHandvor = maxCreatedByNameOrderReceiptBean.getCreatedByName();
		                		LocalDateTime sampleHandvorTimeOld = maxCreatedTimeOrderReceiptBean.getCreatedTime();
		                        String sampleHandvorTime = sampleHandvorTimeOld.format(fmt);
		                        //样品交接
		                		pfProjectTaskProcessDTO.setSampleHandover(sampleHandvor+sampleHandvorTime);
		                		pfProjectTaskProcessDTO.setTaskAssign(sampleHandvor+sampleHandvorTime);
	                		}

	                    	//只有一个样品类型模板tmpl也是一样的
	                    	String monitorTmplId = orders.get(0).getMonitorTmplId();
	                    	List<EmcOrderTaskBean> orderTaskList = emcOrderTaskList.stream().filter(ot ->ObjectUtils.equals(EmcProjectBean.getId(), ot.getProjId()))
	                    			.filter(ot ->ObjectUtils.equals(monitorTmplId, ot.getMonitorTmplId())).collect(Collectors.toList());

	                        if(!emcRunList.isEmpty()){
	                        	//理化批
			                	List<EmcOrderTaskBean> orderTaskMatchPhysList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdPhysList = new ArrayList<>();
			                	if(!orderTaskMatchPhysList.isEmpty()){
				                	List<String> runIdPhysList = orderTaskMatchPhysList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdPhysList.addAll(runIdPhysList);
			                	}
			                	//金属批
			                	List<EmcOrderTaskBean> orderTaskMatchMetalList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020202",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdMetalList = new ArrayList<>();
			                	if(!orderTaskMatchMetalList.isEmpty()){
				                	List<String> runIdMetalList = orderTaskMatchMetalList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdMetalList.addAll(runIdMetalList);
			                	}
			                	//有机批
			                	List<EmcOrderTaskBean> orderTaskMatchOrganicList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020201",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdOrganicList = new ArrayList<>();
			                	if(!orderTaskMatchOrganicList.isEmpty()){
				                	List<String> runIdOrganicList = orderTaskMatchOrganicList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdOrganicList.addAll(runIdOrganicList);
			                	}
			                	//二噁英批
			                	List<EmcOrderTaskBean> orderTaskMatchDiohyingList =  orderTaskList.stream().filter(ok ->StringUtils.equals("050601020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdDiohyingList = new ArrayList<>();
			                	if(!orderTaskMatchDiohyingList.isEmpty()){
				                	List<String> runIdDiohyingList = orderTaskMatchDiohyingList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdDiohyingList.addAll(runIdDiohyingList);
			                	}
	                        	//理化
			                	if(!newRunIdPhysList.isEmpty()){
		                        	List<EmcRunBean> runPhysMatchList = emcRunList.stream().filter(r ->newRunIdPhysList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                        	if(!runPhysMatchList.isEmpty()){
			                        	EmcRunBean physAuditorMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean physAuditedDateMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String physAuditorMax = physAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime physAuditedDateMaxOld = physAuditedDateMaxBean.getAuditedDate();
			                    		if(physAuditorMax!=null&&physAuditedDateMaxOld!=null){
				                            String physAuditedDateMax = physAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setPhysAudit(physAuditorMax+physAuditedDateMax);
			                    		}

		                        	}
			                	}

			                	if(!newRunIdMetalList.isEmpty()){
		                        	//金属
		                        	List<EmcRunBean> runMetalMatchList = emcRunList.stream().filter(r ->newRunIdMetalList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                           	if(!runMetalMatchList.isEmpty()){
		                           		EmcRunBean metalAuditorMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean metalAuditedDateMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String metalAuditorMax = metalAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime metalAuditedDateMaxOld = metalAuditedDateMaxBean.getAuditedDate();
			                    		if(metalAuditorMax!=null&&metalAuditedDateMaxOld!=null){
			                    			String metalAuditedDateMax = metalAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setMetalAudit(metalAuditorMax+metalAuditedDateMax);
				                        }
		                           	}
			                	}
			                	if(!newRunIdOrganicList.isEmpty()){
		                        	//有机
		                        	List<EmcRunBean> runOrganicMatchList = emcRunList.stream().filter(r ->newRunIdOrganicList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                           	if(!runOrganicMatchList.isEmpty()){
		                           		EmcRunBean organicAuditorMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean organicAuditedDateMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String organicAuditorMax = organicAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime organicAuditedDateMaxOld = organicAuditedDateMaxBean.getAuditedDate();
			                    		if(organicAuditorMax!=null&&organicAuditedDateMaxOld!=null){
				                            String organicAuditedDateMax = organicAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setMetalAudit(organicAuditorMax+organicAuditedDateMax);
			                    		}
		                           	}
			                	}

	                            if(!newRunIdDiohyingList.isEmpty()){
		                        	//二噁英
		                        	List<EmcRunBean> runDiohyingMatchList = emcRunList.stream().filter(r ->newRunIdDiohyingList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                           	if(!runDiohyingMatchList.isEmpty()){
		                           		EmcRunBean diohyingAuditorMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean diohyingAuditedDateMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String diohyingAuditorMax = diohyingAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime diohyingAuditedDateMaxOld = diohyingAuditedDateMaxBean.getAuditedDate();
			                    		if(diohyingAuditorMax!=null&&diohyingAuditedDateMaxOld!=null){
				                            String diohyingAuditedDateMax = diohyingAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setDiohyingAudit(diohyingAuditorMax+diohyingAuditedDateMax);
			                    		}
		                           	}
	                            }



	                        }

	                        //报告编制
	                        List<EmcOrderTaskBean> orderTaskDoneList  = orderTaskList.stream().filter(ot ->StringUtils.equals("done", ot.getStatus())).collect(Collectors.toList());
	                        if(orderTaskDoneList.size()!=orderTaskList.size()){
	                        	pfProjectTaskProcessDTO.setReportEdit("/");
	                        }else{
	                            List<EmcReportBean> reportList = emcReportList.stream().filter(rp ->ObjectUtils.equals(EmcProjectBean.getId(), rp.getId())).collect(Collectors.toList());
	                            if(!reportList.isEmpty()){
	                            	//报告文件
	                            	if(!reportNewFileList.isEmpty()){
	                                	List<CoreFileBean> reportMatchFileList = reportNewFileList.stream().filter(cf ->StringUtils.equals("T_EMC_REPORT$"+reportList.get(0).getId(),cf.getTargetId())).collect(Collectors.toList()) ;

	                                	CoreFileBean maxCreatedTimeBean  = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedTime)).get();
	                                	CoreFileBean maxCreatedByNameBean = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedByName)).get();
	                                	String maxCreatedByName = maxCreatedByNameBean.getCreatedByName();
	                            		LocalDateTime maxCreatedTimeOld = maxCreatedTimeBean.getCreatedTime();
	                                    String maxCreatedTime = maxCreatedTimeOld.format(fmt);
	                                    //报告编制
	                                    pfProjectTaskProcessDTO.setReportEdit(maxCreatedByName+maxCreatedTime);
	                            	}

	                            }
	                        }

		                    //检测项目
		                    List<EmcOrderItemGroupBean> orderNewItemGroupList = orderItemGroupList.stream().filter(oi ->orderIdPList.contains(oi.getOrderId())).collect(Collectors.toList());
		                    if(!orderNewItemGroupList.isEmpty()){
		                    	List<Long> testMethodNewIdList = orderNewItemGroupList.stream().map(EmcOrderItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());
		                    	List<EmcTestCapacityBean>  testCapacityOlderList= testCapacityList.stream().filter(tc ->testMethodNewIdList.contains(tc.getTestMethodId())).distinct().collect(Collectors.toList());
			                	List<EmcTestCapacityBean>  testCapacityNewList= testCapacityOlderList.stream().filter(tc ->!StringUtils.isEmpty(tc.getExt$Item("TESTITEMSORT"))).distinct().collect(Collectors.toList());
			                    //无检测项目分类就直接取orderitemgroup表的testname
			                	List<String> tests = new ArrayList<>();
			                	if(!testCapacityNewList.isEmpty()){
				                	testCapacityNewList.forEach(tc ->{
				            			String testName = "";
				                		if(tc.getExt$Item("TESTITEMSORT").isEmpty()){
				                			//取样品分析项表的testname
				                			List<EmcOrderItemGroupBean>  orderMatchItemGroupList = orderNewItemGroupList.stream().filter(oi ->ObjectUtils.equals(tc.getTestMethodId(), oi.getTestMethodId())).collect(Collectors.toList());
				                			if(!orderMatchItemGroupList.isEmpty()){
					                			testName = orderMatchItemGroupList.get(0).getTestName();
				                			}
				                		}else{
				                			List<CoreCodeBean> coreCodeMatchList = coreCodeNewList.stream().filter(cc ->StringUtils.equals(tc.getExt$Item("TESTITEMSORT"), cc.getCode())).collect(Collectors.toList());
				                			if(!coreCodeMatchList.isEmpty()){
					                			testName = coreCodeMatchList.get(0).getCodeName();
				                			}
				                		}
				                		tests.add(testName);
				                	});
				                	if(!tests.isEmpty()){
					                	String detectionMetrics = StringUtils.join(tests, ",");
					                	//检测指标
					                	pfProjectTaskProcessDTO.setDetectionMetrics(detectionMetrics);
				                	}

			                	}
		                    }
	                    	list.add(pfProjectTaskProcessDTO);
	            		});
	            	}
	            }else{
	            	PfProjectTaskProcessDTO pfProjectTaskProcessDTO = new PfProjectTaskProcessDTO();
            		//项目编号
                	pfProjectTaskProcessDTO.setProjNo(EmcProjectBean.getProjNo());
                	//项目名称
                	pfProjectTaskProcessDTO.setProjName(EmcProjectBean.getProjName());
                	//销售
                	pfProjectTaskProcessDTO.setBiddername(EmcProjectBean.getExt$Item("biddername"));

                	//开案日期
                	pfProjectTaskProcessDTO.setCreatedTime(EmcProjectBean.getCreatedTime());

                	list.add(pfProjectTaskProcessDTO);
	            }

            });
        }
        return new Pagination<>(taskProjectPage,list);

    }

    @Override
    public Page<PfProjectTaskProcessDTO> selectAssignProjectTaskProcess(String analysisOrgid,RestJsonWrapperBean wrapper) {

	        CoreUserBean user = LocalContextHelper.getLoginUser();
	        //赋值所属单位和流程类型
	        String parentOrgId = user.getExt$Item("parentorgid");

    		//未建批的测试数据
        	List<EmcOrderTaskBean> orderTaskOrgList = orderTaskService.selectListByFilter(SearchFilter.instance()
        			.match("analysisOrgId", analysisOrgid).filter(MatchPattern.SEQ).match("status", "preLogged").filter(MatchPattern.SEQ)
        			);
        	if(orderTaskOrgList.isEmpty()){
                return new Pagination<>();
            }
        	List<Long> projectOrgIdList = orderTaskOrgList.stream().map(EmcOrderTaskBean :: getProjId).distinct().collect(Collectors.toList());
        	//找到当前分析科室样品接收提交但是还未分析的项目

	        Page<EmcProjectBean> assignProjectPage  =  selectPaginationByFilter(SearchFilter.instance()
        			.match("status", "auditFinished").filter(MatchPattern.SEQ).match("id",projectOrgIdList).filter(MatchPattern.OR),wrapper);
    	    List<EmcProjectBean> projectList = assignProjectPage.getRows();

	        List<PfProjectTaskProcessDTO> list = new ArrayList<>();
	        if (!CollectionUtils.isEmpty(projectList)) {
	            List<Long> proIds = projectList.stream().map(EmcProjectBean::getId).distinct().collect(Collectors.toList());
	            //关联的样品
	            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(proIds, "projId",Arrays.asList("id","projId","sampleId","monitorTmplId","sampleBeginDate","sampleCompleteDate","orderCategory"));
	            List<Long> orderIds = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());


	            //关联的审核记录
	        	List<EmcAuditRecordBean> emcAuditList = auditRecordService.getDao().selectListByOneColumnValues(proIds, "projId",Arrays.asList("id", "auditCategory","projId","operateCategory","taskName","comments","createdByName","createdTime"));
	        	//关联的样品测试
/*				List<EmcOrderTaskBean> emcOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance()
	        			.match("projId", proIds).filter(MatchPattern.OR).match("status", "draw").filter(MatchPattern.DIFFER)
	        			.match("status", "preLogged").filter(MatchPattern.DIFFER));*/

	        	List<EmcOrderTaskBean> emcOrderTaskList = orderTaskOrgList;
	        	List<String> runOldIdList = emcOrderTaskList.stream().map(EmcOrderTaskBean :: getRunId).collect(Collectors.toList());
	        	List<String> runIdList =runOldIdList.stream().filter(r ->!StringUtils.isEmpty(r)).collect(Collectors.toList());
	        	List<EmcRunBean> emcRunList = new ArrayList<>();
	        	if(!runIdList.isEmpty()){
	            	List<EmcRunBean> emcRunOldList = runService.getDao().selectListByIds(runIdList,Arrays.asList("id","auditedUserName","auditedDate"));
	            	emcRunList.addAll(emcRunOldList);
	        	}


	            //关联的报告
	            List<EmcReportBean> emcReportList= reportService.getDao().selectListByOneColumnValues(proIds, "projId",Arrays.asList("id","projId"));

	            //关联的文件
	        	List<Long> reportIdList =emcReportList.stream().map(EmcReportBean :: getId).distinct().collect(Collectors.toList());
	        	List<CoreFileBean> reportFileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", "T_EMC_REPORT$").filter(MatchPattern.SB)
	        			.match("bizcategory", "").filter(MatchPattern.DIFFER));

	        	List<CoreFileBean> reportNewFileList  = new ArrayList<>();
	        	if(!reportFileList.isEmpty()){
	            	reportIdList.forEach(r ->{
	            		CoreFileBean coreFile = new CoreFileBean();
	            		List<CoreFileBean> coreFileList = reportFileList.stream().filter(rp ->StringUtils.equals("T_EMC_REPORT$"+r,rp.getTargetId())).collect(Collectors.toList());
	            		if(!coreFileList.isEmpty()){
	                		coreFile = coreFileList.get(0);
	            		}
	            		reportNewFileList.add(coreFile);
	            	});
	        	}


	            //关联的orderitemgroup
	        	//List<EmcOrderItemGroupBean> orderItemOldGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIds, "orderId",Arrays.asList("id","orderId","testMethodId","testName","schemeName"));
	        	//List<EmcOrderItemGroupBean> orderItemGroupList = orderItemOldGroupList.stream().filter(oi ->StringUtils.equals("default", oi.getSchemeName())).collect(Collectors.toList());
	            //关联的orderitemgroup
                List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIds).filter(MatchPattern.OR)
                		.match("schemeName", "default").filter(MatchPattern.SEQ));

	        	List<Long> testMethodIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());

	        	//List<EmcTestCapacityBean>  testCapacityList= testCapacityService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId",Arrays.asList("id","testMethodId","TESTITEMSORT"));

	        	List<EmcTestCapacityBean>  testCapacityList= testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
                		.match("orgId", parentOrgId).filter(MatchPattern.SEQ));
	        	List<String> testItemSortList = new ArrayList<>();
	        	testCapacityList.forEach(tc ->{
	        		String testItemSort = tc.getExt$Item("TESTITEMSORT");
	        		if(!testItemSortList.contains(testItemSort)){
	            		testItemSortList.add(testItemSort);
	        		}
	        	});
	        	List<CoreCodeBean> coreCodeList = codeService.getDao().selectListByOneColumnValue("testitemsort", "codeCategoryId",Arrays.asList("id","codeCategoryId","code","codeName"));
	        	List<CoreCodeBean> coreCodeNewList = coreCodeList.stream().filter(cc ->testItemSortList.contains(cc.getCode())).distinct().collect(Collectors.toList());

	        	//关联的点位
	            List<EmcFolderBean> emcFolderList = folderService.selectListByFilter(SearchFilter.instance().match("PROJID", proIds).filter(MatchPattern.OR));

	            //相同项目有不同类型样品的需要根据样品类型重复显示
	            projectList.forEach(EmcProjectBean -> {
	            	List<EmcOrderBean> orders = orderList.stream().filter(o ->ObjectUtils.equals(EmcProjectBean.getId(),o.getProjId())).collect(Collectors.toList());
	            	List<EmcOrderBean> sampleOrders = orders.stream().filter(o ->o.getSampleBeginDate()!=null)
	            			.filter(o ->o.getSampleCompleteDate()!=null).collect(Collectors.toList());
	            	if(!orders.isEmpty()){
		            	List<String> orderCategoryList = orders.stream().map(EmcOrderBean :: getOrderCategory).distinct().collect(Collectors.toList());
		            	List<Long> orderIdPList = orders.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());


		        		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		            	if(orderCategoryList.size()<=1){
		            		PfProjectTaskProcessDTO pfProjectTaskProcessDTO = new PfProjectTaskProcessDTO();
		            		//项目编号
		                	pfProjectTaskProcessDTO.setProjNo(EmcProjectBean.getProjNo());
		                	//项目名称
		                	pfProjectTaskProcessDTO.setProjName(EmcProjectBean.getProjName());
		                	//销售
		                	pfProjectTaskProcessDTO.setBiddername(EmcProjectBean.getExt$Item("biddername"));
		                	//开案日期
		                	pfProjectTaskProcessDTO.setCreatedTime(EmcProjectBean.getCreatedTime());

		                	//样品类型
		                	if(orderCategoryList.isEmpty()){
			                	pfProjectTaskProcessDTO.setOrderCategory("/");
		                	}else{
			                	pfProjectTaskProcessDTO.setOrderCategory(orderCategoryList.get(0));
		                	}
		                	if(!EmcProjectBean.getSample().equals("0")&&!sampleOrders.isEmpty()){
		                		//采样查询最早的采样开始日期及最晚的采样完成日期
		                    	List<Long> folderIdList = sampleOrders.stream().map(EmcOrderBean ::getFolderId).distinct().collect(Collectors.toList());
		                    	//List<Long> sampleIdList = orders.stream().map(EmcOrderBean ::getSampleId).distinct().collect(Collectors.toList());
		                    	//List<EmcSampleBean> sampleList = emcSampleList.stream().filter(s ->sampleIdList.contains(s.getId())).collect(Collectors.toList());
		                    	List<EmcFolderBean> folderList = emcFolderList.stream().filter(s ->folderIdList.contains(s.getId())).collect(Collectors.toList());
		                    	List<String> sampleUserList = folderList.stream().map(EmcFolderBean :: getSampleUserName).distinct().collect(Collectors.toList());
		                    	String sampleUsers = StringUtils.join(sampleUserList, ",");

		                			EmcOrderBean minSampleBeginDateOrder = sampleOrders.stream().min(Comparator.comparing(EmcOrderBean::getSampleBeginDate)).get();
			                		EmcOrderBean maxSampleCompleteOrder = sampleOrders.stream().max(Comparator.comparing(EmcOrderBean::getSampleCompleteDate)).get();
			                		LocalDate minSampleBeginDateOld = minSampleBeginDateOrder.getSampleBeginDate();
			                		LocalDate maxSampleCompleteDateOld = maxSampleCompleteOrder.getSampleCompleteDate();
			                		if(maxSampleCompleteDateOld!=null&&minSampleBeginDateOld!=null){
			                			String minSampleBeginDate = minSampleBeginDateOld.format(fmt);
			 	                        String maxSampleCompleteDate = maxSampleCompleteDateOld.format(fmt);
			 	                        //采样开始
			 	                		pfProjectTaskProcessDTO.setSampleBegin(sampleUsers+minSampleBeginDate);
			 	                		//采样结束
			 	                		pfProjectTaskProcessDTO.setSampleEnd(sampleUsers+maxSampleCompleteDate);
			                		}




		                		List<EmcAuditRecordBean> auditList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
		                		List<EmcAuditRecordBean> auditSampleList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("提交", au.getOperateCategory()))
		                				.filter(au ->StringUtils.equals("采样审核", au.getTaskName())).filter(au ->StringUtils.contains("提交", au.getComments())).collect(Collectors.toList());
		                		if(!auditSampleList.isEmpty()){
			                		EmcAuditRecordBean maxCreatedByNameRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
			                		EmcAuditRecordBean maxCreatedTimeRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
			                		String sampleAssignUser = maxCreatedByNameRecord.getCreatedByName();
			                		LocalDateTime sampleAssignTimeOld = maxCreatedTimeRecord.getCreatedTime();
			                		if(sampleAssignUser!=null&&sampleAssignTimeOld!=null){
			                			String sampleAssignTime = sampleAssignTimeOld.format(fmt);
			  	                        //采样分配
			  	                		pfProjectTaskProcessDTO.setSampleAssign(sampleAssignUser+sampleAssignTime);
			                		}
		                		}



		                		List<EmcAuditRecordBean> auditSampleEndList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("审核", au.getOperateCategory()))
		                				.filter(au ->StringUtils.equals("采样审核", au.getTaskName())).filter(au ->StringUtils.contains("通过", au.getComments())).collect(Collectors.toList());
		                		if(!auditSampleEndList.isEmpty()){
			                		EmcAuditRecordBean maxCreatedByNameEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
			                		EmcAuditRecordBean maxCreatedTimeEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();

			                   		String sampleEndAssignUser = maxCreatedByNameEndRecord.getCreatedByName();
			                		LocalDateTime sampleEndAssignTimeOld = maxCreatedTimeEndRecord.getCreatedTime();

			                		if(sampleEndAssignUser!=null&&sampleEndAssignTimeOld!=null){
				                        String sampleEndAssignTime = sampleEndAssignTimeOld.format(fmt);
				                        //采样审核
				                		pfProjectTaskProcessDTO.setSampleAssign(sampleEndAssignUser+sampleEndAssignTime);
			                        }

		                		}



		                	}else{
		                		pfProjectTaskProcessDTO.setSampleBegin("/");
		                		pfProjectTaskProcessDTO.setSampleEnd("/");
		                		pfProjectTaskProcessDTO.setSampleAssign("/");

		                	}
		            		List<EmcAuditRecordBean> auditOrderReceiptList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
		            		List<EmcAuditRecordBean> orderReceiptList = auditOrderReceiptList.stream().filter(au ->StringUtils.equals("样品管理", au.getAuditCategory())).filter(au ->StringUtils.equals("接收", au.getOperateCategory()))
		            				.filter(au ->StringUtils.equals("样品接收", au.getTaskName())).filter(au ->StringUtils.contains("接收", au.getComments())).collect(Collectors.toList());
		            		if(!orderReceiptList.isEmpty()){
		            			EmcAuditRecordBean maxCreatedByNameOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
			            		EmcAuditRecordBean maxCreatedTimeOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
			            		String sampleHandvor = maxCreatedByNameOrderReceiptBean.getCreatedByName();
			            		LocalDateTime sampleHandvorTimeOld = maxCreatedTimeOrderReceiptBean.getCreatedTime();
			                    String sampleHandvorTime = sampleHandvorTimeOld.format(fmt);
			                    //样品交接
			            		pfProjectTaskProcessDTO.setSampleHandover(sampleHandvor+sampleHandvorTime);
			            		pfProjectTaskProcessDTO.setTaskAssign(sampleHandvor+sampleHandvorTime);
		            		}


		                	//只有一个样品类型模板tmpl也是一样的
		                	String monitorTmplId = orders.get(0).getMonitorTmplId();
		                	List<EmcOrderTaskBean> orderTaskList = emcOrderTaskList.stream().filter(ot ->ObjectUtils.equals(EmcProjectBean.getId(), ot.getProjId()))
		                			.filter(ot ->ObjectUtils.equals(monitorTmplId, ot.getMonitorTmplId())).collect(Collectors.toList());

		                    if(!emcRunList.isEmpty()){
	                        	//理化批
			                	List<EmcOrderTaskBean> orderTaskMatchPhysList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdPhysList = new ArrayList<>();
			                	if(!orderTaskMatchPhysList.isEmpty()){
				                	List<String> runIdPhysList = orderTaskMatchPhysList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdPhysList.addAll(runIdPhysList);
			                	}
			                	//金属批
			                	List<EmcOrderTaskBean> orderTaskMatchMetalList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020202",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdMetalList = new ArrayList<>();
			                	if(!orderTaskMatchMetalList.isEmpty()){
				                	List<String> runIdMetalList = orderTaskMatchMetalList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdMetalList.addAll(runIdMetalList);
			                	}
			                	//有机批
			                	List<EmcOrderTaskBean> orderTaskMatchOrganicList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020201",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdOrganicList = new ArrayList<>();
			                	if(!orderTaskMatchOrganicList.isEmpty()){
				                	List<String> runIdOrganicList = orderTaskMatchOrganicList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdOrganicList.addAll(runIdOrganicList);
			                	}
			                	//二噁英批
			                	List<EmcOrderTaskBean> orderTaskMatchDiohyingList =  orderTaskList.stream().filter(ok ->StringUtils.equals("050601020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
			                	List<String> newRunIdDiohyingList = new ArrayList<>();
			                	if(!orderTaskMatchDiohyingList.isEmpty()){
				                	List<String> runIdDiohyingList = orderTaskMatchDiohyingList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
				                	newRunIdDiohyingList.addAll(runIdDiohyingList);
			                	}
	                        	//理化
			                	if(!newRunIdPhysList.isEmpty()){
		                        	List<EmcRunBean> runPhysMatchList = emcRunList.stream().filter(r ->newRunIdPhysList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                        	if(!runPhysMatchList.isEmpty()){
			                        	EmcRunBean physAuditorMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean physAuditedDateMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String physAuditorMax = physAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime physAuditedDateMaxOld = physAuditedDateMaxBean.getAuditedDate();
			                    		if(physAuditorMax!=null&&physAuditedDateMaxOld!=null){
				                            String physAuditedDateMax = physAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setPhysAudit(physAuditorMax+physAuditedDateMax);
				                            if(analysisOrgid.equals("05060102020203")){
					                            pfProjectTaskProcessDTO.setAnalyzeAudit(physAuditorMax+physAuditedDateMax);
				                            }
			                    		}

		                        	}
			                	}

			                	if(!newRunIdMetalList.isEmpty()){
		                        	//金属
		                        	List<EmcRunBean> runMetalMatchList = emcRunList.stream().filter(r ->newRunIdMetalList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                           	if(!runMetalMatchList.isEmpty()){
		                           		EmcRunBean metalAuditorMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean metalAuditedDateMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String metalAuditorMax = metalAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime metalAuditedDateMaxOld = metalAuditedDateMaxBean.getAuditedDate();
			                    		if(metalAuditorMax!=null&&metalAuditedDateMaxOld!=null){
			                    			String metalAuditedDateMax = metalAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setMetalAudit(metalAuditorMax+metalAuditedDateMax);
				                            if(analysisOrgid.equals("05060102020202")){
					                            pfProjectTaskProcessDTO.setAnalyzeAudit(metalAuditorMax+metalAuditedDateMax);
				                            }
				                        }
		                           	}
			                	}
			                	if(!newRunIdOrganicList.isEmpty()){
		                        	//有机
		                        	List<EmcRunBean> runOrganicMatchList = emcRunList.stream().filter(r ->newRunIdOrganicList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                           	if(!runOrganicMatchList.isEmpty()){
		                           		EmcRunBean organicAuditorMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean organicAuditedDateMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String organicAuditorMax = organicAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime organicAuditedDateMaxOld = organicAuditedDateMaxBean.getAuditedDate();
			                    		if(organicAuditorMax!=null&&organicAuditedDateMaxOld!=null){
				                            String organicAuditedDateMax = organicAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setMetalAudit(organicAuditorMax+organicAuditedDateMax);
				                            if(analysisOrgid.equals("05060102020201")){
					                            pfProjectTaskProcessDTO.setAnalyzeAudit(organicAuditorMax+organicAuditedDateMax);
				                            }
			                    		}
		                           	}
			                	}

	                            if(!newRunIdDiohyingList.isEmpty()){
		                        	//二噁英
		                        	List<EmcRunBean> runDiohyingMatchList = emcRunList.stream().filter(r ->newRunIdDiohyingList.contains(r.getId()))
		                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
		                        			.filter(r ->r.getAuditedDate()!=null)
		                        			.collect(Collectors.toList());
		                           	if(!runDiohyingMatchList.isEmpty()){
		                           		EmcRunBean diohyingAuditorMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
			                        	EmcRunBean diohyingAuditedDateMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
			                    		String diohyingAuditorMax = diohyingAuditorMaxBean.getAuditedUserName();
			                    		LocalDateTime diohyingAuditedDateMaxOld = diohyingAuditedDateMaxBean.getAuditedDate();
			                    		if(diohyingAuditorMax!=null&&diohyingAuditedDateMaxOld!=null){
				                            String diohyingAuditedDateMax = diohyingAuditedDateMaxOld.format(fmt);
				                            pfProjectTaskProcessDTO.setDiohyingAudit(diohyingAuditorMax+diohyingAuditedDateMax);
				                            if(analysisOrgid.equals("050601020203")){
					                            pfProjectTaskProcessDTO.setAnalyzeAudit(diohyingAuditorMax+diohyingAuditedDateMax);
				                            }
			                    		}
		                           	}
	                            }



	                        }

		                    //报告编制
		                    List<EmcOrderTaskBean> orderTaskDoneList  = orderTaskList.stream().filter(ot ->StringUtils.equals("done", ot.getStatus())).collect(Collectors.toList());
		                    if(orderTaskDoneList.size()!=orderTaskList.size()){
		                    	pfProjectTaskProcessDTO.setReportEdit("/");
		                    }else{
		                        List<EmcReportBean> reportList = emcReportList.stream().filter(rp ->ObjectUtils.equals(EmcProjectBean.getId(), rp.getId())).collect(Collectors.toList());
		                        if(!reportList.isEmpty()){
		                        	//报告文件
		                        	if(!reportNewFileList.isEmpty()){
		                            	List<CoreFileBean> reportMatchFileList = reportNewFileList.stream().filter(cf ->StringUtils.equals("T_EMC_REPORT$"+reportList.get(0).getId(),cf.getTargetId())).collect(Collectors.toList()) ;

		                            	CoreFileBean maxCreatedTimeBean  = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedTime)).get();
		                            	CoreFileBean maxCreatedByNameBean = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedByName)).get();
		                            	String maxCreatedByName = maxCreatedByNameBean.getCreatedByName();
		                        		LocalDateTime maxCreatedTimeOld = maxCreatedTimeBean.getCreatedTime();
		                                String maxCreatedTime = maxCreatedTimeOld.format(fmt);
		                                //报告编制
		                                pfProjectTaskProcessDTO.setReportEdit(maxCreatedByName+maxCreatedTime);
		                        	}

		                        }
		                    }
		                    //检测项目
		                    List<EmcOrderItemGroupBean> orderNewItemGroupList = orderItemGroupList.stream().filter(oi ->orderIdPList.contains(oi.getOrderId())).collect(Collectors.toList());
		                    if(!orderNewItemGroupList.isEmpty()){
		                    	List<Long> testMethodNewIdList = orderNewItemGroupList.stream().map(EmcOrderItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());
			                	List<EmcTestCapacityBean>  testCapacityOlderList= testCapacityList.stream().filter(tc ->testMethodNewIdList.contains(tc.getTestMethodId())).distinct().collect(Collectors.toList());
			                	List<EmcTestCapacityBean>  testCapacityNewList= testCapacityOlderList.stream().filter(tc ->!StringUtils.isEmpty(tc.getExt$Item("TESTITEMSORT"))).distinct().collect(Collectors.toList());

			                	//无检测项目分类就直接取orderitemgroup表的testname
			                	List<String> tests = new ArrayList<>();
			                	if(!testCapacityNewList.isEmpty()){
				                	testCapacityNewList.forEach(tc ->{
				            			String testName = "";
				                		if(tc.getExt$Item("TESTITEMSORT").isEmpty()){
				                			//取样品分析项表的testname
				                			List<EmcOrderItemGroupBean>  orderMatchItemGroupList = orderNewItemGroupList.stream().filter(oi ->ObjectUtils.equals(tc.getTestMethodId(), oi.getTestMethodId())).distinct().collect(Collectors.toList());
				                			if(!orderMatchItemGroupList.isEmpty()){
					                			testName = orderMatchItemGroupList.get(0).getTestName();
				                			}
				                		}else{
				                			List<CoreCodeBean> coreCodeMatchList = coreCodeNewList.stream().filter(cc ->StringUtils.equals(tc.getExt$Item("TESTITEMSORT"), cc.getCode())).distinct().collect(Collectors.toList());
				                			if(!coreCodeMatchList.isEmpty()){
					                			testName = coreCodeMatchList.get(0).getCodeName();
				                			}
				                		}
				                		tests.add(testName);
				                	});
				                	if(!tests.isEmpty()){
					                	String detectionMetrics = StringUtils.join(tests, ",");
					                	//检测指标
					                	pfProjectTaskProcessDTO.setDetectionMetrics(detectionMetrics);
				                	}

			                	}
		                    }

		                	list.add(pfProjectTaskProcessDTO);
		            	}else{
		            		orderCategoryList.forEach(oc ->{
		                		PfProjectTaskProcessDTO pfProjectTaskProcessDTO = new PfProjectTaskProcessDTO();
		                		//项目编号
		                    	pfProjectTaskProcessDTO.setProjNo(EmcProjectBean.getProjNo());
		                    	//项目名称
		                    	pfProjectTaskProcessDTO.setProjName(EmcProjectBean.getProjName());
		                    	//销售
		                    	pfProjectTaskProcessDTO.setBiddername(EmcProjectBean.getExt$Item("biddername"));
			                	//开案日期
			                	pfProjectTaskProcessDTO.setCreatedTime(EmcProjectBean.getCreatedTime());
			                	//样品类型
			                	if(oc.isEmpty()){
				                	pfProjectTaskProcessDTO.setOrderCategory("/");
			                	}else{
				                	pfProjectTaskProcessDTO.setOrderCategory(oc);
			                	}
		                    	if(!EmcProjectBean.getSample().equals("0")&&!sampleOrders.isEmpty()){
		                    		//采样查询最早的采样开始日期及最晚的采样完成日期
		                        	List<Long> folderIdList = sampleOrders.stream().map(EmcOrderBean ::getFolderId).distinct().collect(Collectors.toList());
		                        	//List<Long> sampleIdList = orders.stream().map(EmcOrderBean ::getSampleId).distinct().collect(Collectors.toList());
		                        	//List<EmcSampleBean> sampleList = emcSampleList.stream().filter(s ->sampleIdList.contains(s.getId())).collect(Collectors.toList());

		                        	List<EmcFolderBean> folderList = emcFolderList.stream().filter(s ->folderIdList.contains(s.getId())).collect(Collectors.toList());
		                        	List<String> sampleUserList = folderList.stream().map(EmcFolderBean :: getSampleUserName).distinct().collect(Collectors.toList());
		                        	String sampleUsers = StringUtils.join(sampleUserList, ",");
			                			EmcOrderBean minSampleBeginDateOrder = sampleOrders.stream().min(Comparator.comparing(EmcOrderBean::getSampleBeginDate)).get();
				                		EmcOrderBean maxSampleCompleteOrder = sampleOrders.stream().max(Comparator.comparing(EmcOrderBean::getSampleCompleteDate)).get();
			                    		LocalDate minSampleBeginDateOld = minSampleBeginDateOrder.getSampleBeginDate();
			                    		LocalDate maxSampleCompleteDateOld = maxSampleCompleteOrder.getSampleCompleteDate();
				                		if(maxSampleCompleteDateOld!=null&&minSampleBeginDateOld!=null){
				                			String minSampleBeginDate = minSampleBeginDateOld.format(fmt);
				 	                        String maxSampleCompleteDate = maxSampleCompleteDateOld.format(fmt);
				 	                        //采样开始
				 	                		pfProjectTaskProcessDTO.setSampleBegin(sampleUsers+minSampleBeginDate);
				 	                		//采样结束
				 	                		pfProjectTaskProcessDTO.setSampleEnd(sampleUsers+maxSampleCompleteDate);
				                		}



		                    		List<EmcAuditRecordBean> auditList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
		                    		List<EmcAuditRecordBean> auditSampleList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("提交", au.getOperateCategory()))
		                    				.filter(au ->StringUtils.equals("采样安排", au.getTaskName())).filter(au ->StringUtils.contains("提交", au.getComments())).collect(Collectors.toList());
		                    		if(!auditSampleList.isEmpty()){
			                    		EmcAuditRecordBean maxCreatedByNameRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
			                    		EmcAuditRecordBean maxCreatedTimeRecord = auditSampleList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
			                    		String sampleAssignUser = maxCreatedByNameRecord.getCreatedByName();
			                    		LocalDateTime sampleAssignTimeOld = maxCreatedTimeRecord.getCreatedTime();
			                    		if(sampleAssignUser!=null &&sampleAssignTimeOld!=null){
				                            String sampleAssignTime = sampleAssignTimeOld.format(fmt);
				                            //采样分配
				                    		pfProjectTaskProcessDTO.setSampleAssign(sampleAssignUser+sampleAssignTime);

			                    		}

		                    		}

		                    		List<EmcAuditRecordBean> auditSampleEndList = auditList.stream().filter(au ->StringUtils.equals("采样管理", au.getAuditCategory())).filter(au ->StringUtils.equals("审核", au.getOperateCategory()))
		                    				.filter(au ->StringUtils.equals("采样审核", au.getTaskName())).filter(au ->StringUtils.contains("通过", au.getComments())).collect(Collectors.toList());
		                    		if(!auditSampleEndList.isEmpty()){
			                    		EmcAuditRecordBean maxCreatedByNameEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
			                    		EmcAuditRecordBean maxCreatedTimeEndRecord = auditSampleEndList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
			                    		String sampleEndAssignUser = maxCreatedByNameEndRecord.getCreatedByName();
			                    		LocalDateTime sampleEndAssignTimeOld = maxCreatedTimeEndRecord.getCreatedTime();
			                    		if(sampleEndAssignUser!=null&&sampleEndAssignTimeOld!=null){
			                    			   String sampleEndAssignTime = sampleEndAssignTimeOld.format(fmt);
			   	                            //采样审核
			   	                    		pfProjectTaskProcessDTO.setSampleAudit(sampleEndAssignUser+sampleEndAssignTime);
			                    		}
		                    		}



		                    	}else{
		                    		pfProjectTaskProcessDTO.setSampleBegin("/");
		                    		pfProjectTaskProcessDTO.setSampleEnd("/");
		                    		pfProjectTaskProcessDTO.setSampleAssign("/");
		                    		pfProjectTaskProcessDTO.setSampleAudit("/");

		                    	}
		                		List<EmcAuditRecordBean> auditOrderReceiptList = emcAuditList.stream().filter(au ->ObjectUtils.equals(EmcProjectBean.getId(), au.getProjId())).collect(Collectors.toList());
		                		List<EmcAuditRecordBean> orderReceiptList = auditOrderReceiptList.stream().filter(au ->StringUtils.equals("样品管理", au.getAuditCategory())).filter(au ->StringUtils.equals("接收", au.getOperateCategory()))
		                				.filter(au ->StringUtils.equals("样品接收", au.getTaskName())).filter(au ->StringUtils.contains("接收", au.getComments())).collect(Collectors.toList());
		                		if(!orderReceiptList.isEmpty()){
		                			EmcAuditRecordBean maxCreatedByNameOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedByName)).get();
			                		EmcAuditRecordBean maxCreatedTimeOrderReceiptBean= orderReceiptList.stream().max(Comparator.comparing(EmcAuditRecordBean::getCreatedTime)).get();
			                		String sampleHandvor = maxCreatedByNameOrderReceiptBean.getCreatedByName();
			                		LocalDateTime sampleHandvorTimeOld = maxCreatedTimeOrderReceiptBean.getCreatedTime();
			                        String sampleHandvorTime = sampleHandvorTimeOld.format(fmt);
			                        //样品交接
			                		pfProjectTaskProcessDTO.setSampleHandover(sampleHandvor+sampleHandvorTime);
				            		pfProjectTaskProcessDTO.setTaskAssign(sampleHandvor+sampleHandvorTime);
		                		}

		                    	//只有一个样品类型模板tmpl也是一样的
		                    	String monitorTmplId = orders.get(0).getMonitorTmplId();
		                    	List<EmcOrderTaskBean> orderTaskList = emcOrderTaskList.stream().filter(ot ->ObjectUtils.equals(EmcProjectBean.getId(), ot.getProjId()))
		                    			.filter(ot ->ObjectUtils.equals(monitorTmplId, ot.getMonitorTmplId())).collect(Collectors.toList());

		                        if(!emcRunList.isEmpty()){
		                        	//理化批
				                	List<EmcOrderTaskBean> orderTaskMatchPhysList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
				                	List<String> newRunIdPhysList = new ArrayList<>();
				                	if(!orderTaskMatchPhysList.isEmpty()){
					                	List<String> runIdPhysList = orderTaskMatchPhysList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
					                	newRunIdPhysList.addAll(runIdPhysList);
				                	}
				                	//金属批
				                	List<EmcOrderTaskBean> orderTaskMatchMetalList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020202",ok.getAnalysisOrgId())).collect(Collectors.toList());
				                	List<String> newRunIdMetalList = new ArrayList<>();
				                	if(!orderTaskMatchMetalList.isEmpty()){
					                	List<String> runIdMetalList = orderTaskMatchMetalList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
					                	newRunIdMetalList.addAll(runIdMetalList);
				                	}
				                	//有机批
				                	List<EmcOrderTaskBean> orderTaskMatchOrganicList =  orderTaskList.stream().filter(ok ->StringUtils.equals("05060102020201",ok.getAnalysisOrgId())).collect(Collectors.toList());
				                	List<String> newRunIdOrganicList = new ArrayList<>();
				                	if(!orderTaskMatchOrganicList.isEmpty()){
					                	List<String> runIdOrganicList = orderTaskMatchOrganicList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
					                	newRunIdOrganicList.addAll(runIdOrganicList);
				                	}
				                	//二噁英批
				                	List<EmcOrderTaskBean> orderTaskMatchDiohyingList =  orderTaskList.stream().filter(ok ->StringUtils.equals("050601020203",ok.getAnalysisOrgId())).collect(Collectors.toList());
				                	List<String> newRunIdDiohyingList = new ArrayList<>();
				                	if(!orderTaskMatchDiohyingList.isEmpty()){
					                	List<String> runIdDiohyingList = orderTaskMatchDiohyingList.stream().map(EmcOrderTaskBean ::getRunId).filter(r ->!StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
					                	newRunIdDiohyingList.addAll(runIdDiohyingList);
				                	}
		                        	//理化
				                	if(!newRunIdPhysList.isEmpty()){
			                        	List<EmcRunBean> runPhysMatchList = emcRunList.stream().filter(r ->newRunIdPhysList.contains(r.getId()))
			                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
			                        			.filter(r ->r.getAuditedDate()!=null)
			                        			.collect(Collectors.toList());
			                        	if(!runPhysMatchList.isEmpty()){
				                        	EmcRunBean physAuditorMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
				                        	EmcRunBean physAuditedDateMaxBean = runPhysMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
				                    		String physAuditorMax = physAuditorMaxBean.getAuditedUserName();
				                    		LocalDateTime physAuditedDateMaxOld = physAuditedDateMaxBean.getAuditedDate();
				                    		if(physAuditorMax!=null&&physAuditedDateMaxOld!=null){
					                            String physAuditedDateMax = physAuditedDateMaxOld.format(fmt);
					                            pfProjectTaskProcessDTO.setPhysAudit(physAuditorMax+physAuditedDateMax);
					                            if(analysisOrgid.equals("05060102020203")){
						                            pfProjectTaskProcessDTO.setAnalyzeAudit(physAuditorMax+physAuditedDateMax);
					                            }
				                    		}

			                        	}
				                	}

				                	if(!newRunIdMetalList.isEmpty()){
			                        	//金属
			                        	List<EmcRunBean> runMetalMatchList = emcRunList.stream().filter(r ->newRunIdMetalList.contains(r.getId()))
			                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
			                        			.filter(r ->r.getAuditedDate()!=null)
			                        			.collect(Collectors.toList());
			                           	if(!runMetalMatchList.isEmpty()){
			                           		EmcRunBean metalAuditorMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
				                        	EmcRunBean metalAuditedDateMaxBean = runMetalMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
				                    		String metalAuditorMax = metalAuditorMaxBean.getAuditedUserName();
				                    		LocalDateTime metalAuditedDateMaxOld = metalAuditedDateMaxBean.getAuditedDate();
				                    		if(metalAuditorMax!=null&&metalAuditedDateMaxOld!=null){
				                    			String metalAuditedDateMax = metalAuditedDateMaxOld.format(fmt);
					                            pfProjectTaskProcessDTO.setMetalAudit(metalAuditorMax+metalAuditedDateMax);
					                            if(analysisOrgid.equals("05060102020202")){
						                            pfProjectTaskProcessDTO.setAnalyzeAudit(metalAuditorMax+metalAuditedDateMax);
					                            }
					                        }
			                           	}
				                	}
				                	if(!newRunIdOrganicList.isEmpty()){
			                        	//有机
			                        	List<EmcRunBean> runOrganicMatchList = emcRunList.stream().filter(r ->newRunIdOrganicList.contains(r.getId()))
			                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
			                        			.filter(r ->r.getAuditedDate()!=null)
			                        			.collect(Collectors.toList());
			                           	if(!runOrganicMatchList.isEmpty()){
			                           		EmcRunBean organicAuditorMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
				                        	EmcRunBean organicAuditedDateMaxBean = runOrganicMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
				                    		String organicAuditorMax = organicAuditorMaxBean.getAuditedUserName();
				                    		LocalDateTime organicAuditedDateMaxOld = organicAuditedDateMaxBean.getAuditedDate();
				                    		if(organicAuditorMax!=null&&organicAuditedDateMaxOld!=null){
					                            String organicAuditedDateMax = organicAuditedDateMaxOld.format(fmt);
					                            pfProjectTaskProcessDTO.setMetalAudit(organicAuditorMax+organicAuditedDateMax);
					                            if(analysisOrgid.equals("05060102020201")){
						                            pfProjectTaskProcessDTO.setAnalyzeAudit(organicAuditorMax+organicAuditedDateMax);
					                            }
				                    		}
			                           	}
				                	}

		                            if(!newRunIdDiohyingList.isEmpty()){
			                        	//二噁英
			                        	List<EmcRunBean> runDiohyingMatchList = emcRunList.stream().filter(r ->newRunIdDiohyingList.contains(r.getId()))
			                        			.filter(r ->!StringUtils.isEmpty(r.getAuditedUserName()))
			                        			.filter(r ->r.getAuditedDate()!=null)
			                        			.collect(Collectors.toList());
			                           	if(!runDiohyingMatchList.isEmpty()){
			                           		EmcRunBean diohyingAuditorMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedUserName)).get();
				                        	EmcRunBean diohyingAuditedDateMaxBean = runDiohyingMatchList.stream().max(Comparator.comparing(EmcRunBean::getAuditedDate)).get();
				                    		String diohyingAuditorMax = diohyingAuditorMaxBean.getAuditedUserName();
				                    		LocalDateTime diohyingAuditedDateMaxOld = diohyingAuditedDateMaxBean.getAuditedDate();
				                    		if(diohyingAuditorMax!=null&&diohyingAuditedDateMaxOld!=null){
					                            String diohyingAuditedDateMax = diohyingAuditedDateMaxOld.format(fmt);
					                            pfProjectTaskProcessDTO.setDiohyingAudit(diohyingAuditorMax+diohyingAuditedDateMax);
					                            if(analysisOrgid.equals("050601020203")){
						                            pfProjectTaskProcessDTO.setAnalyzeAudit(diohyingAuditorMax+diohyingAuditedDateMax);
					                            }
				                    		}
			                           	}
		                            }



		                        }

		                        //报告编制
		                        List<EmcOrderTaskBean> orderTaskDoneList  = orderTaskList.stream().filter(ot ->StringUtils.equals("done", ot.getStatus())).collect(Collectors.toList());
		                        if(orderTaskDoneList.isEmpty()){
		                        	pfProjectTaskProcessDTO.setReportEdit("/");
		                        }else{
		                            List<EmcReportBean> reportList = emcReportList.stream().filter(rp ->ObjectUtils.equals(EmcProjectBean.getId(), rp.getId())).collect(Collectors.toList());
		                            if(!reportList.isEmpty()){
		                            	//报告文件
		                            	if(!reportNewFileList.isEmpty()){
		                                	List<CoreFileBean> reportMatchFileList = reportNewFileList.stream().filter(cf ->StringUtils.equals("T_EMC_REPORT$"+reportList.get(0).getId(),cf.getTargetId())).collect(Collectors.toList()) ;

		                                	CoreFileBean maxCreatedTimeBean  = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedTime)).get();
		                                	CoreFileBean maxCreatedByNameBean = reportMatchFileList.stream().max(Comparator.comparing(CoreFileBean::getCreatedByName)).get();
		                                	String maxCreatedByName = maxCreatedByNameBean.getCreatedByName();
		                            		LocalDateTime maxCreatedTimeOld = maxCreatedTimeBean.getCreatedTime();
		                                    String maxCreatedTime = maxCreatedTimeOld.format(fmt);
		                                    //报告编制
		                                    pfProjectTaskProcessDTO.setReportEdit(maxCreatedByName+maxCreatedTime);
		                            	}

		                            }
		                        }

			                    //检测项目
			                    List<EmcOrderItemGroupBean> orderNewItemGroupList = orderItemGroupList.stream().filter(oi ->orderIdPList.contains(oi.getOrderId())).collect(Collectors.toList());
			                    if(!orderNewItemGroupList.isEmpty()){
			                    	List<Long> testMethodNewIdList = orderNewItemGroupList.stream().map(EmcOrderItemGroupBean :: getTestMethodId).distinct().collect(Collectors.toList());
			                    	List<EmcTestCapacityBean>  testCapacityOlderList= testCapacityList.stream().filter(tc ->testMethodNewIdList.contains(tc.getTestMethodId())).distinct().collect(Collectors.toList());
				                	List<EmcTestCapacityBean>  testCapacityNewList= testCapacityOlderList.stream().filter(tc ->!StringUtils.isEmpty(tc.getExt$Item("TESTITEMSORT"))).distinct().collect(Collectors.toList());
				                    //无检测项目分类就直接取orderitemgroup表的testname
				                	List<String> tests = new ArrayList<>();
				                	if(!testCapacityNewList.isEmpty()){
					                	testCapacityNewList.forEach(tc ->{
					            			String testName = "";
					                		if(tc.getExt$Item("TESTITEMSORT").isEmpty()){
					                			//取样品分析项表的testname
					                			List<EmcOrderItemGroupBean>  orderMatchItemGroupList = orderNewItemGroupList.stream().filter(oi ->ObjectUtils.equals(tc.getTestMethodId(), oi.getTestMethodId())).distinct().collect(Collectors.toList());
					                			if(!orderMatchItemGroupList.isEmpty()){
						                			testName = orderMatchItemGroupList.get(0).getTestName();
					                			}
					                		}else{
					                			List<CoreCodeBean> coreCodeMatchList = coreCodeNewList.stream().filter(cc ->StringUtils.equals(tc.getExt$Item("TESTITEMSORT"), cc.getCode())).distinct().collect(Collectors.toList());
					                			if(!coreCodeMatchList.isEmpty()){
						                			testName = coreCodeMatchList.get(0).getCodeName();
					                			}
					                		}
					                		if(!tests.contains(testName)){
						                		tests.add(testName);
					                		}
					                	});
					                	if(!tests.isEmpty()){
						                	String detectionMetrics = StringUtils.join(tests, ",");
						                	//检测指标
						                	pfProjectTaskProcessDTO.setDetectionMetrics(detectionMetrics);
					                	}

				                	}
			                    }
		                    	list.add(pfProjectTaskProcessDTO);
		            		});
		            	}
		            }else{
		            	PfProjectTaskProcessDTO pfProjectTaskProcessDTO = new PfProjectTaskProcessDTO();
	            		//项目编号
	                	pfProjectTaskProcessDTO.setProjNo(EmcProjectBean.getProjNo());
	                	//项目名称
	                	pfProjectTaskProcessDTO.setProjName(EmcProjectBean.getProjName());
	                	//销售
	                	pfProjectTaskProcessDTO.setBiddername(EmcProjectBean.getExt$Item("biddername"));

	                	//开案日期
	                	pfProjectTaskProcessDTO.setCreatedTime(EmcProjectBean.getCreatedTime());

	                	list.add(pfProjectTaskProcessDTO);
		            }

	            });
	        }
	        return new Pagination<>(assignProjectPage,list);


    }

    @Override
    public void sendReport(RestJsonWrapperBean wrapper) {
        List<CoreFileBean> coreFileList = wrapper.parse(CoreFileBean.class);
        EmcProjectBean project = wrapper.parseUnique(EmcProjectBean.class);

        String subject = String.join("", project.getProjNo(), project.getProjName(), "电子版报告已发，请查收!");
        File[] files = coreFileList.stream().map(coreFile -> FilePathManager.getLocalPath(CoreFileUtils.toFilePath(coreFile)).toFile()).toArray(File[]::new);

        // 是否为测试模式，测试模式下，不发送电子版报告到客户邮箱
        String enableCustomerMail = ApplicationContextHelper.getConstantValue("ENABLE_SEND_REPORT_CUSTOMER_MAIL");
        if (!Constant.YES.equals(enableCustomerMail)) {
            String testMail = ApplicationContextHelper.getConstantValue("SEND_REPORT_TEST_EMAIL_ADDRESS");
            if (StringUtils.isEmpty(testMail)) {
                throw new UnsupportedException("未配置测试邮箱地址，无法发送邮件");
            }

            MailHelper.sendHtmlMail(subject, subject, Collections.singletonList(testMail), files);

            return;
        }

        String contractNo = project.getExt$Item("contractNo");
        List<CrmOpportunityContractBean> contractList = opportunityContractService.getDao().selectListByOneColumnValue(contractNo, "contractNo", Arrays.asList("id", "opportunityId"));
        if (contractList.isEmpty()) {
            throw new UnsupportedException("合同不存在，无法查询客户邮箱地址");
        }

        String opportunityId = contractList.get(0).getOpportunityId();
        CrmOpportunityMainBean opportunityMain = opportunityMainService.getDao().selectById(opportunityId);
        String customerMail = opportunityMain.getCustomerMail();
        if (StringUtils.isEmpty(customerMail)) {
            throw new UnsupportedException("客户邮箱地址未配置，无法发送邮件");
        }

        String fromMail = ApplicationContextHelper.getEnvironment().getProperty("spring.mail.username");
        if (StringUtils.equals(fromMail, customerMail)) {
            throw new UnsupportedException("客户邮箱地址配置错误，不能与发件人邮箱地址相同");
        }

        MailHelper.sendHtmlMail(subject, subject, Collections.singletonList(customerMail), files);
    }

    @Override
    public Page<CoreFileBean> queryReportFile(RestJsonWrapperBean wrapper) {
        String projectId = wrapper.getFilterValue("projectId");
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValue(projectId, "projId");

        List<String> targetIdList = reportList.stream().map(r -> reportService.getDao().getTable() + "$" + r.getId() + "F").collect(Collectors.toList());
        List<CoreFileBean> coreFileList = coreFileService.getDao().selectListByOneColumnValues(targetIdList, "targetId");

        return new Pagination<>(coreFileList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String sampleContractFile(RestJsonWrapperBean wrapper) {
    	EmcProjectBean projectBean = wrapper.parseUnique(EmcProjectBean.class);
        List<CoreReportConfigLineBean> configLineBeforeList = wrapper.parse(CoreReportConfigLineBean.class);
        List<CoreReportConfigLineBean> normalConfigLineBeanList = configLineBeforeList.stream().filter(r -> !StringUtils.equals("fanruan", r.getReportCategory())).collect(Collectors.toList());
        List<String> fieldList = new ArrayList<>();
        String fieldIdOne = archiveSamplingContactRecordFiles(projectBean,normalConfigLineBeanList);
        // 帆软报表模板
        List<CoreReportConfigLineBean> frConfigLineBeanList = configLineBeforeList.stream().filter(r -> "fanruan".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!frConfigLineBeanList.isEmpty()) {
            List<String> configReportLineIdList = frConfigLineBeanList.stream().map(r -> coreReportConfigLineService.getDao().getTable() + "$" + r.getId()).collect(Collectors.toList());
            List<CoreFileBean> coreFileBeanList = coreFileService.getDao().selectListByOneColumnValues(configReportLineIdList, "TARGETID");
            deleteExistedFile(projectBean.getId(),SAMPLING_CONTACT_RECORD_NEW_FILES);

            Map<String, CoreFileBean> pdfReportMap = new LinkedHashMap<>();
            String projInfo = projectBean.getProjNo()+"-"+projectBean.getProjName();
            frConfigLineBeanList.forEach(fc ->{
            	String ddds =  coreReportConfigLineService.getDao().getTable() + "$" +fc.getId();
                Optional<CoreFileBean> coreFileBeanOptional = coreFileBeanList.stream()
                        .filter(l -> StringUtils.equals(ddds, l.getTargetId())).findFirst();
                if (!ObjectUtils.isEmpty(fc)) {
                    CoreFileBean coreFileBean = coreFileBeanOptional.get();
                    //String targetId = coreFileBean.getTargetId();
                    String fileName = "/"+fc.getReportConfigLineNo() + "/" + coreFileBean.getName();
                    if (!StringUtils.isEmpty(fileName)) {
                        StringBuilder reportUrl = new StringBuilder();
                        reportUrl.append(ApplicationContextHelper.getServerAddress()).append("/ReportServer?reportlet=").append(fileName).append("&format=word");
                        String paramStr = parseReportParam(projectBean, fc.getParam());
                        if (!StringUtils.isEmpty(paramStr)) {
                            reportUrl.append("&").append(paramStr);
                        }
                        logger.info(reportUrl.toString());
                        coreFileBean = new CoreFileBean();
                        coreFileBean.setTargetId("T_EMC_PROJECT" + "$" + projectBean.getId());
                        coreFileBean.setScope(FileScope.secure.name());
                        coreFileBean.setExt("composePdf");
                        coreFileBean.setBizCategory(SAMPLING_CONTACT_RECORD_NEW_FILES);
                        coreFileBean.setExt$Item("reportName", fc.getReportName());
                        pdfReportMap.put(reportUrl.toString(), coreFileBean);
                    }
                }


            });
            pdfReportMap.entrySet().stream().forEach(e -> {
                File reportPdfFile = downloadFile(e.getKey(), projInfo.replaceAll("/","-")+"-"+e.getValue().getExt$Item("reportName")+ DOC_SUFFIX);
                if (reportPdfFile != null) {
                    coreFileService.upload(e.getValue(), reportPdfFile);
                    List<CoreFileBean> fileNewList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", e.getValue().getTargetId()).filter(MatchPattern.OR)
                    		.match("bizCategory",SAMPLING_CONTACT_RECORD_NEW_FILES).filter(MatchPattern.SEQ));
                    List<Long> fileIdOldList = fileNewList.stream().map(CoreFileBean :: getId).distinct().collect(Collectors.toList());
                    fileIdOldList.forEach(f ->{
                    	fieldList.add(f.toString());
                    });

                }
            });

        }
        String fieldIds = "";
        if(!fieldList.isEmpty()){
        	fieldIds = StringUtils.join(fieldList,",")+","+fieldIdOne;
        }else{
        	fieldIds = fieldIdOne;
        }
        return fieldIds;
    }

    /**
     * 复写删除方法，解决删除慢的问题
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    @Audit(AuditConstant.DELETE)
    @ValidateDataWith(EmcProjectDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> deleteList = wrapper.parse(EmcProjectBean.class);
        if (deleteList.isEmpty()) {
            return;
        }

        // 删除送样任务生成的点位数据，T_EMC_FOLDER
        List<EmcFolderBean> filterFolderList = deleteList.stream().map(p -> {
            EmcFolderBean folder = new EmcFolderBean();
            folder.setProjId(p.getId());
            return folder;
        }).collect(Collectors.toList());
        folderService.getDao().deleteBy(filterFolderList, "PROJID");

        // 删除送样样品数据及关联表数据，涉及T_EMC_ORDER、T_EMC_ORDER_ITEM、T_EMC_ORDER_ITEM_GROUP
        List<Long> projectIdList = deleteList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> deleteOrderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "PROJID", Collections.singletonList("ID"));
        //T_EMC_ORDER、T_EMC_ORDER_ITEM、T_EMC_ORDER_ITEM_GROUP表删除
        List<Long> orderIdList = deleteOrderList.stream().map(EmcOrderBean ::getId).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        if(!orderItemGroupList.isEmpty()){
            List<EmcOrderItemBean>   orderItemList = orderItemService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
            orderItemService.getDao().deleteBy(orderItemList);
            orderItemGroupService.getDao().deleteBy(orderItemGroupList);
        }

        //区分采样送样
        List<EmcProjSchemeBean> projSchemeList = projSchemeService.getDao().selectListByOneColumnValues(projectIdList, "projId");
        if(!projSchemeList.isEmpty()){
        	   List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeBean ::getId).distinct().collect(Collectors.toList());
               List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId");
               projSchemeService.getDao().deleteBy(projSchemeList);
               projOrderService.getDao().deleteBy(projOrderList);
        }
        //只有项目登记可以删除项目
        orderService.getDao().deleteBy(deleteOrderList);
        //删除项目审核表
        List<EmcAuditRecordBean> auditRecordList = auditRecordService.getDao().selectListByOneColumnValues(projectIdList, "projId");
        auditRecordService.getDao().deleteBy(auditRecordList);
        getDao().deleteByIdList(projectIdList);
    }

    @Override
    public void syncProjInfoAfterEdit(RestJsonWrapperBean wrapper) {
        //项目信息修改- 修改了基本信息的项目
        EmcProjectBean projectBean = wrapper.parseUnique(EmcProjectBean.class);
        Long projId = projectBean.getId();
        logger.info(PfConstant.EDIT_PROJECT_AMOUNT_LOG+projId);

        List<EmcProjectBean> updateProjectList = new ArrayList<>();
        EmcProjectBean updateProject = selectById(projId);
        updateProjectList.add(updateProject);
        //先查项目的状态如果完成状态不是1,不需要发送信息
        if(!StringUtils.equals("1",updateProject.getExt$Item("completestatus"))){
            logger.info(PfConstant.EDIT_PROJECT_AMOUNT_FAIL);
            return;
        }
        if(!updateProjectList.isEmpty()){
            logger.info(PfConstant.EDIT_PROJECT_AMOUNT_SUCCESS);
            opportunityOpenService.syncProjectInfo(updateProjectList);
        }
        logger.info(PfConstant.EDIT_PROJECT_AMOUNT_END_LOG+projId);

    }

    @Override
    public String haveWriteOff(RestJsonWrapperBean wrapper) {
        String reason ="";
        List<EmcProjectBean> projectBean = wrapper.parse(EmcProjectBean.class);
        List<Long> projIdList = projectBean.stream().map(EmcProjectBean :: getId).distinct().collect(Collectors.toList());
        List<String> projIdStrList = new ArrayList<>();
        projIdList.forEach(p ->{
            projIdStrList.add(p.toString());
        });
        List<CrmWriteOffBean>  typeList = crmWriteOffService.selectListByFilter(SearchFilter.instance().match("projId", projIdStrList).filter(MatchPattern.OR));
        List<String> projNoList = new ArrayList<>();
        projectBean.forEach(p ->{
            List<CrmWriteOffBean>  writeOffList = typeList.stream().filter(t ->StringUtils.equals(p.getId().toString(),t.getProjid())).distinct().collect(Collectors.toList());
            if(!writeOffList.isEmpty()){
                projNoList.add(p.getProjNo());
            }
        });
        if(!projNoList.isEmpty()){
            String projNoStrs = StringUtils.join(projNoList,",");
            return  projNoStrs;
        }
        return  reason;
    }

    @Override
    public void repairProjectData(RestJsonWrapperBean wrapper) {
        // 为了修复ERP推送过来的送样任务，重新生成业务数据
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<EmcProjectBean> erpProjectList = projectList.stream().filter(p -> !Constant.YES.equals(p.getSample()) && !ObjectUtils.isEmpty(p.getErpGbId()) && "auditFinished".equals(p.getStatus())).collect(Collectors.toList());
        List<Long> erpProjectIdList = erpProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(erpProjectIdList, "projId", Arrays.asList("id", "projId"));
        List<Long> existedProjectIdList = orderTaskList.stream().map(EmcOrderTaskBean::getProjId).distinct().collect(Collectors.toList());
        List<Long> targetProjectIdList = erpProjectIdList.stream().filter(id -> !existedProjectIdList.contains(id)).collect(Collectors.toList());
        if (targetProjectIdList.isEmpty()) {
            return;
        }

        List<EmcOrderBean> targetOrderList = orderService.getDao().selectListByOneColumnValues(targetProjectIdList, "projId", Collections.singletonList("id"));
        List<Long> targetOrderIdList = targetOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(targetOrderIdList, "orderId", Arrays.asList("id", "orderId", "itemGroupId"));
        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList);
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        Map<Long, Long> itemGroupIdMap = capacityItemGroupList.stream().collect(Collectors.toMap(EmcCapacityItemGroupBean::getItemGroupId, EmcCapacityItemGroupBean::getId, (v1, v2) -> v1));

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        groupItemList.forEach(groupItem -> groupItem.setExt$Item("capacityItemGroupId", itemGroupIdMap.get(groupItem.getItemGroupId()) + ""));

        // 删除已经存在的
        List<EmcOrderItemBean> existedOrderItemList = orderItemService.getDao().selectListByOneColumnValues(targetOrderIdList, "orderId", Collections.singletonList("id"));
        orderItemService.getDao().deleteBy(existedOrderItemList, "id");

        List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();
        targetOrderList.forEach(order -> {
            List<EmcOrderItemGroupBean> curOrderItemGroupList = orderItemGroupList.stream().filter(group -> ObjectUtils.equals(group.getOrderId(), order.getId())).collect(Collectors.toList());
            curOrderItemGroupList.forEach(group -> {
                List<EmcGroupItemBean> curGroupItemList = groupItemList.stream().filter(item -> StringUtils.equals(item.getExt$Item("capacityItemGroupId"), group.getItemGroupId() + "")).collect(Collectors.toList());
                curGroupItemList.forEach(item -> {
                    EmcOrderItemBean orderItem = new EmcOrderItemBean();
                    orderItem.setId(ApplicationContextHelper.getNextIdentity());
                    orderItem.setItemId(item.getItemId());
                    orderItem.setOrderItemGroupId(group.getId());
                    orderItem.setOrderId(order.getId());
                    newOrderItemList.add(orderItem);
                });
            });
        });

        if (newOrderItemList.size() > 100000) {
            String projNos = projectList.stream().map(EmcProjectBean::getProjNo).collect(Collectors.joining(","));
            logger.warn("修复项目数据{}出现问题，数据异常 -> {}", projNos, newOrderItemList.size());
            throw new UnsupportedException("数据异常");
        }

        orderItemService.getDao().insert(newOrderItemList);

        // 生成业务数据
        orderService.insertBpDataByProject(targetProjectIdList);

        // 样品测试status置为preLogged
        List<EmcOrderTaskBean> updateOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(targetProjectIdList, "PROJID", Collections.singletonList("id"));
        updateOrderTaskList.forEach(a -> a.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED));
        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");
    }

    @Override
    public void createReportSendInfo(EmcProjectBean project) {
        EmcReportSendInfoBean reportSendInfo = new EmcReportSendInfoBean();
        reportSendInfo.setProjId(project.getId());
        reportSendInfo.setId(ApplicationContextHelper.getNextIdentity());

        reportSendInfoService.getDao().insert(reportSendInfo);
    }

    @Override
    public void createProjectBpData(List<EmcProjectBean> projectList) {
        LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
        logger.info("当前登录用户为 -> {}", JSON.toJSONString(loginUser));
        List<Long> normalProjectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        ExecutorHelper.submit(() -> {
            try {
                LocalContextHelper.setUserLogin(loginUser);

                // 样品登记的项目提交时插入业务流程表
                // 筛选不采样的数据进行处理
                List<EmcProjectBean> noSampleProjectList = projectList.stream().filter(e -> Constant.NO.equals(e.getSample())).collect(Collectors.toList());
                // 样品登记的项目提交完成项目流程结束
                if (!noSampleProjectList.isEmpty()) {
                    logger.info("生成送样业务数据");
                    List<EmcProjectBean> updateProjectList = new ArrayList<>();
                    List<Long> noSampleProjectIdList = noSampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

                    orderService.insertBpDataByProject(noSampleProjectIdList);

                    // 删除无效审核记录
                    List<CoreBpmnCommentBean> filterBpmnCommentList = new ArrayList<>();
                    noSampleProjectIdList.forEach(id -> {
                        CoreBpmnCommentBean filterBpmnComment = new CoreBpmnCommentBean();
                        filterBpmnComment.setTargetId(getDao().getTable() + "$" + id);
                        filterBpmnComment.setStatusCode("schemeedit");
                        filterBpmnCommentList.add(filterBpmnComment);
                    });

                    // 样品测试status置为preLogged
                    List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(noSampleProjectIdList, "PROJID");
                    orderTaskList.forEach(a -> a.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED));
                    orderTaskService.getDao().update(orderTaskList, "STATUS");

                    coreBpmnCommentService.getDao().deleteBy(filterBpmnCommentList, "statusCode");
                    //sampleService.insertByProject(noSampleProjectIdList, EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
                    // 新增报告数据,不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                    reportService.insertByProject(noSampleProjectIdList, false);
                    // 更新folder表状态
                    List<Long> folderIdList = orderTaskList.stream().filter(t -> !ObjectUtils.isEmpty(t.getFolderId())).map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
                    List<EmcFolderBean> folderList = new ArrayList<>();
                    folderIdList.forEach(e -> {
                        EmcFolderBean folder = new EmcFolderBean();
                        folder.setId(e);
                        folder.setStatus(EmcBizConstant.FOLDER_STATUS_DRAW);
                        folder.setProcessStatus(EmcBizConstant.FOLDER_PROCESSSTATUS_DONE);
                        folderList.add(folder);
                    });
                    folderService.getDao().update(folderList, "STATUS", "PROCESSSTATUS");
                    // 更新项目节点状态
                    noSampleProjectIdList.forEach(id -> {
                        EmcProjectBean updateProject = new EmcProjectBean();
                        updateProject.setId(id);
                        updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                        updateProjectList.add(updateProject);
                    });

                    // 更新项目节点状态
                    getDao().update(updateProjectList, "STATUS");
                }

                // 采样任务
                List<EmcProjectBean> sampleProjectList = projectList.stream().filter(e -> Constant.YES.equals(e.getSample())).collect(Collectors.toList());
                if (!sampleProjectList.isEmpty()) {
                    logger.info("生成采样业务数据");
                    List<Long> sampleProjectIdList = sampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
                    List<EmcProjectBean> updateProjectList = new ArrayList<>();

                    if (!sampleProjectIdList.isEmpty()) {
                        // 不存在过质量控制的项目，新增主业务流程数据
                        projSchemeService.insertBpData(sampleProjectIdList);
                        // 新增报告数据，不可见，也就是项目监测的样品类型表，因为禅城是按照样品类型进行出报告，所以这里把t_emc_report作为报告数据主表了
                        reportService.insertByProject(sampleProjectIdList, false);

                        // 更新项目节点状态
                        sampleProjectIdList.forEach(i -> {
                            EmcProjectBean updateProject = new EmcProjectBean();
                            updateProject.setId(i);
                            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_AUDITFINISHED);
                            updateProjectList.add(updateProject);
                        });
                    }

                    // 更新项目节点状态
                    getDao().update(updateProjectList, "STATUS");
                }

                List<CoreBpmnInstanceStatusDTO<Long>> bpmnInstanceList = new ArrayList<>(projectList.size());
                projectList.forEach(project -> {
                    CoreBpmnInstanceStatusDTO<Long> bpmnInstance = new CoreBpmnInstanceStatusDTO<>();
                    bpmnInstance.setStatusCode("erpConfirm");
                    bpmnInstance.setId(project.getId());
                    bpmnInstanceList.add(bpmnInstance);
                });

                logger.info("生成审核记录信息");
                insertAuditRecordByProject(projectList, "PROCESS_COMPLETE", "ERP下单确认通过", bpmnInstanceList);

                ProjectNodeUtils.updateNodes(normalProjectIdList);
            } catch (Exception e) {
                String projectNos = projectList.stream().filter(p -> !StringUtils.isEmpty(p.getProjNo())).map(EmcProjectBean::getProjNo).collect(Collectors.joining(","));
                logger.error("ERP下单确认项目【{}】业务数据生成失败，准备回退业务流程，错误原因为：{}", projectNos, e.getMessage());
                e.printStackTrace();
                CoreBpmnParameterDTO bpmnParameter = new CoreBpmnParameterDTO();
                bpmnParameter.setComment("业务数据生成失败");
                undo(normalProjectIdList, bpmnParameter);
                logger.info("ERP下单确认项目【{}】业务业务流程回退成功", projectNos);
            }
        });
    }

    @Override
    public void insertAuditRecordByProject(List<EmcProjectBean> projectList, String bpmnStatus, String comment, List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList) {
        List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();
        for (EmcProjectBean project : projectList) {
            EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
            auditRecord.setId(ApplicationContextHelper.getNextIdentity());
            auditRecord.setProjId(project.getId());
            auditRecord.setProjNo(project.getProjNo());
            auditRecord.setComments(comment);

            auditRecord.setLogDesc("项目:" + project.getProjNo());
            auditRecord.setBizCategory("proj");

            if ("PROCESS_START".contentEquals(bpmnStatus)) {
                auditRecord.setStepCode("1");
                auditRecord.setAuditCategory("项目登记");
                auditRecord.setTaskName("项目登记");
                auditRecord.setOperateCategory("提交");
                auditRecord.setNextTaskName("方案审核");
            } else if ("PROCESS_COMPLETE".equals(bpmnStatus)) {
                auditRecord.setStepCode("2");
                auditRecord.setAuditCategory("项目管理");
                auditRecord.setOperateCategory("审核");
                @SuppressWarnings("rawtypes")
                CoreBpmnInstanceStatusDTO bpmnInstance = coreBpmnInstanceStatusDTOList.stream().filter(f -> ObjectUtils.equals(f.getId(), project.getId())).findFirst().orElseGet(CoreBpmnInstanceStatusDTO::new);
                if ("schemeaudit".equals(bpmnInstance.getStatusCode())) {
                    if(Constant.NO.equals(project.getSample())){
                        auditRecord.setTaskName("方案审核");
                        auditRecord.setNextTaskName("客户样品接收");
                    } else {
                        auditRecord.setTaskName("方案审核");
                        auditRecord.setNextTaskName("采样安排");
                    }
                } else if ("erpConfirm".equals(bpmnInstance.getStatusCode())) {
                    auditRecord.setTaskName("方案审核");
                    auditRecord.setNextTaskName("ERP下单确认");
                } else {
                    auditRecord.setTaskName("方案编制");
                    auditRecord.setNextTaskName("方案审核");
                }
            } else if ("PROCESS_UNDO".equals(bpmnStatus)) {
                auditRecord.setStepCode("3");
                auditRecord.setAuditCategory("采样管理");
                auditRecord.setTaskName("采样安排");
                auditRecord.setOperateCategory("退回");
                auditRecord.setNextTaskName("项目登记");
            }

            auditRecordList.add(auditRecord);
        }
        auditRecordService.getDao().insert(auditRecordList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void moveOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderBean> findOrderList = orderService.selectListByIds(orderIdList);
        List<EmcOrderBean> updateOrderList = new ArrayList<>();
        orderList.forEach(o -> {
            EmcOrderBean updateOrder = findOrderList.stream().filter(u -> ObjectUtils.equals(o.getId(), u.getId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(updateOrder)) {
                updateOrder.setOrderNum(o.getOrderNum());
                updateOrderList.add(updateOrder);
            }
        });
        orderService.getDao().update(updateOrderList,"ORDERNUM");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void moveOrderItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> findOrderItemGroupList = orderItemGroupService.selectListByIds(orderItemGroupIdList);
        List<EmcOrderItemGroupBean> updateOrderItemGroupList = new ArrayList<>();
        orderItemGroupList.forEach(oig -> {
            EmcOrderItemGroupBean updateBean = findOrderItemGroupList.stream().filter(u -> ObjectUtils.equals(oig.getId(), u.getId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(updateBean)) {
                updateBean.setSortNum(oig.getSortNum());
                updateOrderItemGroupList.add(updateBean);
            }
        });
        orderItemGroupService.getDao().update(updateOrderItemGroupList,"SORTNUM");
    }

    private String archiveSamplingContactRecordFiles(EmcProjectBean project,List<CoreReportConfigLineBean> configLineBeforeList) {
        if (configLineBeforeList.isEmpty()) {
            return "";
        }
        List<CoreReportConfigLineBean> configLineList = configLineBeforeList.stream().filter(cf ->!StringUtils.isEmpty(cf.getExt$Item("reportpath")))
        		.filter(cf ->StringUtils.equals("1", cf.getActivatedFlag())).collect(Collectors.toList());
        String reportingServiceServerUrl = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
        if (StringUtils.isEmpty(reportingServiceServerUrl)) {
            return "";
        }
        deleteExistedFile(project.getId(),DELIVERY_RECORD_NEW_FILES);
        String projInfo = project.getProjNo()+"-"+project.getProjName();
        configLineList.forEach(cf ->{
        	 Pair<String, CoreFileBean> resultPair = getDownloadUrl(reportingServiceServerUrl, project.getId(), cf, project, DELIVERY_RECORD_NEW_FILES, cf.getReportName());
             if (StringUtils.isEmpty(resultPair.getFirst())) {
                 return;
             }


             File file = downloadFile(resultPair.getFirst(), projInfo+"-"+cf.getReportName() + PDF_SUFFIX);
             if (file != null) {
                 coreFileService.upload(resultPair.getSecond(), file);
/*                 File templateFile = FileHelper.getAbsolutePath(FileRepresentation.of(fileNew.getScope(), DateTimeUtils.getDateSerialNumber(fileNew.getCreatedTime()),
                		 fileNew.getMd5Name(), fileNew.getName())).toFile();
                  exportWord(templateFile);*/

             }
        });
        String targetId = "T_EMC_PROJECT"+"$"+project.getId();
        List<CoreFileBean> fileNewList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId",targetId ).filter(MatchPattern.OR)
        		.match("bizCategory",DELIVERY_RECORD_NEW_FILES ).filter(MatchPattern.SEQ));

        List<Long> fileIdOldList = fileNewList.stream().map(CoreFileBean :: getId).distinct().collect(Collectors.toList());
        List<String> fileIdNewList = new ArrayList<>();
        fileIdOldList.forEach(f ->{
        	fileIdNewList.add(f.toString());
        });

        if(!fileIdNewList.isEmpty()){
            return StringUtils.join(fileIdNewList,",");
        }else{
            return "";
        }

    }


    protected Pair<String, CoreFileBean> getDownloadUrl(String reportingServiceServerUrl, long projectId, CoreReportConfigLineBean configLine, Object object, String bizCategory, String remark) {
        StringBuilder reportUrl = new StringBuilder();
        String reportPath = configLine.getExt$Item("reportpath");

        String reportFilePath = reportPath.substring(0, reportPath.indexOf("&") > 0 ? reportPath.indexOf("&") : reportPath.length());
        try {
            reportUrl.append(reportingServiceServerUrl).append("?").append(URLEncoder.encode(reportFilePath, "UTF-8")).append("&rs:Command=Render&rs:Format=PDF");
            String paramStr = parseReportParam(object, configLine.getParam());
            if (!StringUtils.isEmpty(paramStr)) {
                reportUrl.append("&").append(paramStr);
            }

            logger.info(reportUrl.toString());
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_PROJECT" + "$" + projectId);
            coreFileBean.setScope(FileScope.secure.name());
            coreFileBean.setExt("composePdf");
            coreFileBean.setBizCategory(bizCategory);
            coreFileBean.setRemark(remark);

            return Pair.of(reportUrl.toString(), coreFileBean);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }

        return Pair.of(null, null);
    }

    protected void deleteExistedFile(Long projectId, String bizCategory) {
        CoreFileBean deleteFilterFile = new CoreFileBean();
        deleteFilterFile.setTargetId("T_EMC_PROJECT" + "$" + projectId);
        deleteFilterFile.setExt("composePdf");
        deleteFilterFile.setBizCategory(bizCategory);
        coreFileService.getDao().deleteBy(deleteFilterFile, "TARGETID", "EXT", "BIZCATEGORY");
    }

    /**
     * 下载文件，并保存到指定目录下
     */
    protected File downloadFile(String url, String fileName) {
        File tempFile = FileUtils.createTempFile();
        if (StringUtils.startsWithIgnoreCase(url, "https://")) {
        	HttpClientManager.getInstance().download(url, tempFile);
        } else {
        	HttpClientManager.getInstance().download(url, tempFile);
        }

        return tempFile;
    }

    /**
     * 解析报表参数，忽略参数名大小写
     */
    protected String parseReportParam(Object object, String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }

        String[] values = StringUtils.split(param, "&");

        StringBuilder sb = new StringBuilder();

        if (values.length > 0) {
            String[] pair = StringUtils.split(param, "=");

            if (pair.length > 1) {
                Object value = BeanUtils.getPropertyValue(object, pair[1]);

                if (value != null) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }

                    sb.append(pair[0]).append("=").append(ObjectUtils.toString(value));
                }
            }
        }

        return sb.toString();
    }

    protected void exportWord(File templateFile) {
        try (FileInputStream inputStream = new FileInputStream(templateFile);
             XWPFDocument document = new XWPFDocument(inputStream);
             FileOutputStream outputStream = new FileOutputStream(templateFile)) {
             document.write(outputStream);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }




}
