package com.sunwayworld.emc.module.bp.sample.sample.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.*;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.framework.word.ReportingServiceUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderTaskDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.delivery.bean.EmcDeliveryBean;
import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.emc.module.bp.delivery.service.EmcDeliveryService;
import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.service.EmcOrderRetainService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPretreatmentBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcPretreatmentService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.dao.EmcSampleDao;
import com.sunwayworld.emc.module.bp.sample.sample.service.PfSampleService;
import com.sunwayworld.emc.module.bp.sample.sample.validator.PfSampleAcceptValidator;
import com.sunwayworld.emc.module.bp.sample.sample.validator.PfSampleCompleteValidator;
import com.sunwayworld.emc.module.bp.sample.sample.validator.PfSampleSubmitValidator;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;

/**
 * @author luoc@sunwayworld.com 2021/3/26
 */
@Repository
@GikamBean
public class PfSampleServiceImpl extends EmcSampleServiceImpl implements PfSampleService {

    @Autowired
    private EmcSampleDao sampleDao;

    @Autowired
    @Lazy
    private PfOrderService orderService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskDao orderTaskDao;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    @Lazy
    private EmcReportService reportService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcSubcontractRecordService subcontractRecordService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    @Lazy
    private EmcOrderRetainService orderRetainService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcPretreatmentService pretreatmentService;

    @Autowired
    private EmcDeliveryService deliveryService;

    @Autowired
    @Lazy
    private EmcOrderContainerService orderContainerService;

    @Autowired
    @Lazy
    private EmcQcCategoryService qcCategoryService;

    @Autowired
    private CoreConstantService coreConstantService;

    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Autowired
    @Lazy
    private CoreFileService coreFileService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Autowired
    @Lazy
    private EmcTestCapacityService emcTestCapacityService;

    @Autowired
    private EmcAuditRecordService auditRecordService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSampleDao getDao() {
        return sampleDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void addOrder(Long id, RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(folderIdList, "id");
        Set<String> sampleUserIdSet = new HashSet<>();
        Set<String> sampleUserNameSet = new HashSet<>();
        Set<String> sampleSceneUserIdSet = new HashSet<>();
        Set<String> sampleSceneUserNameSet = new HashSet<>();
        folderList.forEach(folder -> {
            sampleUserIdSet.addAll(Arrays.asList(StringUtils.split(folder.getSampleUserId(), ",")));
            sampleUserNameSet.addAll(Arrays.asList(StringUtils.split(folder.getSampleUserName(), ",")));
            sampleSceneUserIdSet.addAll(Arrays.asList(StringUtils.split(folder.getSceneUserId(), ",")));
            sampleSceneUserNameSet.addAll(Arrays.asList(StringUtils.split(folder.getSceneUserName(), ",")));
        });

        EmcOrderBean addOrder = orderList.get(0);

        EmcSampleBean sampleBean = new EmcSampleBean();
        sampleBean.setId(id);
        sampleBean.setOrderCategory(addOrder.getOrderCategory());
        sampleBean.setMonitorTmplId(addOrder.getMonitorTmplId());
        sampleBean.setExt$Item("sampleUserId", String.join(",", sampleUserIdSet));
        sampleBean.setExt$Item("sampleUserName", String.join(",", sampleUserNameSet));
        sampleBean.setSampleSceneUserId(String.join(",", sampleSceneUserIdSet));
        sampleBean.setSampleSceneUserName(String.join(",", sampleSceneUserNameSet));
        getDao().update(sampleBean);

        // 根据(monitorTmplid) id 查询 tmplid
        EmcMonitorTmplBean monitorTmplBean = monitorTmplService.selectByIdIfPresent(addOrder.getMonitorTmplId());
        if (monitorTmplBean == null) {
            throw new UnexpectedException(I18nHelper.getMessage("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.NOT_EXISTS_TMPL", addOrder.getMonitorTmplId()));
        }
        EmcPropertyDataBean propertyDataBean = new EmcPropertyDataBean();
        propertyDataBean.setBizId(id);
        List<EmcPropertyDataBean> dataList = propertyDataService.selectListByFilter(SearchFilter.instance().match("BIZID", id).filter(MatchPattern.SEQ), wrapper);
        if (dataList.size() > 0) {
            propertyDataBean.setTmplId(monitorTmplBean.getPropertyTmplId());
            propertyDataBean.setId(dataList.get(0).getId());
            propertyDataService.update(propertyDataBean);
        }

        // 根据样品类型查询监测模板表的默认采样单模板id，更新采样单表的默认采样单模板id
        EmcSampleBean reportConfigSampleBean = getDao().selectById(id);
        Long reportConfigLineId = monitorTmplBean.getReportConfigLineId();
        if (StringUtils.isEmpty(reportConfigSampleBean.getReportConfigLineId()) && !ObjectUtils.isEmpty(reportConfigLineId)) {
            reportConfigSampleBean.setReportConfigLineId(reportConfigLineId.toString());
            getDao().update(reportConfigSampleBean, "reportConfigLineId");
        }

        // 更新采样单表的默认交接单模板ID
        String receiveReportLineId = monitorTmplBean.getReceiveReportLineId();
        if (StringUtils.isEmpty(reportConfigSampleBean.getReceiveReportLineId()) && !ObjectUtils.isEmpty(receiveReportLineId)) {
            reportConfigSampleBean.setReceiveReportLineId(receiveReportLineId);
            getDao().update(reportConfigSampleBean, "receiveReportLineId");
        }

        EmcOrderBean updateOrder = new EmcOrderBean();
        updateOrder.setSampleId(id);

        orderService.getDao().updateByIds(updateOrder, orderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "SAMPLEID");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByProject(List<Long> projectIdList, String status) {
        List<EmcProjectBean> projectList = projectService.getDao().selectListByIds(projectIdList);
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");

        // 根据monitorTmplId和projectId去重
        List<EmcOrderBean> distinctOrderList = orderList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMonitorTmplId() + ";" + o.getProjId()))), ArrayList::new));
        int qty = distinctOrderList.size();
        List<String> sampleNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), qty);

        List<EmcSampleBean> insertSampleList = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            EmcSampleBean sample = new EmcSampleBean();

            EmcOrderBean order = distinctOrderList.get(i);
            Long projectId = order.getProjId();
            String monitorTmplId = order.getMonitorTmplId();
            String orderCategory = order.getOrderCategory();

            Long id = ApplicationContextHelper.getNextIdentity();
            sample.setId(id);
            sample.setSampleNo(sampleNoList.get(i));
            sample.setProjId(projectId);

            EmcProjectBean project = projectList.stream().filter(f -> ObjectUtils.equals(f.getId(), projectId)).findFirst().orElse(null);
            if (project != null) {
                sample.setProjNo(project.getProjNo());
                sample.setRemark(project.getRemark());
            }
            if (!StringUtils.isEmpty(status)) {
                sample.setStatus(status);
            }
            sample.setMonitorTmplId(monitorTmplId);
            sample.setOrderCategory(orderCategory);
            sample.setSource(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION);
            sample.setProcessStatus(ProcessStatus.DRAFT.name().toLowerCase());
            sample.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentorgid"));
            sample.setOrgName(LocalContextHelper.getLoginUser().getExt$Item("parentorgname"));
            if ("preLogged".equals(status)) {
                sample.setAcceptStatus("1");
            }

            insertSampleList.add(sample);

            orderList.stream().filter(e -> ObjectUtils.equals(e.getProjId(), projectId) && ObjectUtils.equals(e.getMonitorTmplId(), monitorTmplId)).forEach(e -> {
                e.setSampleId(id);
            });
        }
        if (!orderList.isEmpty()) {
            orderService.getDao().update(orderList, "SAMPLEID");
        }

        getDao().insert(insertSampleList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    @ValidateDataWith(PfSampleSubmitValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        // 现场采样或者采样补录提交
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = startProcessBasic(wrapper);
        List<Long> sampleIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(sampleIdList);
        List<Long> projIdList = sampleList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());

        // 采样单的状态改成approve
        List<EmcSampleBean> updateSampleList = new ArrayList<EmcSampleBean>();
        sampleList.forEach(e -> {
            EmcSampleBean updateSample = new EmcSampleBean();
            updateSample.setId(e.getId());
            updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_APPROVE);
            updateSampleList.add(updateSample);
        });
        getDao().update(updateSampleList, "STATUS");

        // 把类型为采样的ordtask状态改成approve（提交到采样互审）
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");
        if (!orderList.isEmpty()) {

            List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
            if (!orderTaskList.isEmpty()) {
                List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_SAMPLING, e.getAnalysisType()))
                        .collect(Collectors.toList());
                List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<EmcOrderTaskBean>();
                if (!sampleOrderTaskList.isEmpty()) {
                    sampleOrderTaskList.forEach(e -> {
                        EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                        updateOrderTask.setId(e.getId());
                        updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE);
                        EmcOrderBean order = orderList.stream().filter(o -> ObjectUtils.equals(o.getId(), e.getOrderId())).findFirst().orElse(null);
                        updateOrderTask.setAnalysisDate(order.getSampleBeginDate());
                        updateOrderTask.setConfirmUserName(LocalContextHelper.getLoginUser().getUserName());
                        updateOrderTaskList.add(updateOrderTask);
                    });
                    orderTaskService.getDao().update(updateOrderTaskList, "ANALYSISDATE", "STATUS", "CONFIRMUSERNAME");

                    // 将样品测试结果表的记录日志标志置为"1"
                    List<Long> orderTaskIdList = sampleOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
                    List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
                    orderTaskResultList.forEach(a -> {
                        a.setSubmitFlag(Constant.YES);
                    });
                    orderTaskResultService.getDao().update(orderTaskResultList, "SUBMITFLAG");

                    // 插入样品测试操作记录表
                    orderTaskLogService.insert(sampleOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_RESULT_INPUT, EmcBizConstant.ORDER_TASK_LOG_SAMPLING);

                }

                // 已终止样品结束流程
                this.executeTermination(sampleIdList, orderList);
            }
        }

        // 现场采样提交插入审核记录
        this.insertAuditRecordBySample(sampleIdList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);
        ProjectNodeUtils.updateNodes(projIdList);

        // 合并采样记录
        String loginUserId = LocalContextHelper.getLoginUserId();
        ExecutorHelper.submit(() -> composeSampleRecordPdf(sampleList, loginUserId));
        return coreBpmnInstanceStatusDTOList;
    }

    //合成采样记录pdf
    private void composeSampleRecordPdf(List<EmcSampleBean> sampleList, String loginUserId) {
        if (CollectionUtils.isEmpty(sampleList)) return;

        // 通过系统参数COMPOSE_PDF来配置是否需要合成PDF
        CoreConstantBean composePdfConstant = coreConstantService.selectByIdIfPresent("COMPOSE_PDF");
        if (composePdfConstant == null || !Constant.YES.equals(composePdfConstant.getConstantValue())) {
            return;
        }

        // 查询采样单对应的报表模板
        List<CoreReportConfigLineBean> configLineBeanList = coreReportConfigLineService
                .selectListByIds(sampleList.stream().filter(r -> r.getReportConfigLineId() != null).map(e -> NumberUtils.parseLong(e.getReportConfigLineId())).collect(Collectors.toList()));
        if (configLineBeanList.isEmpty()) {
            return;
        }

        // 生成的PDF默认名称
        String defaultPdfFileName = I18nHelper.getMessage("T_EMC_RUN.DOWNLOAD.REPORT_PDF_NAME");

        // Reporting Service 报表模板
        List<CoreReportConfigLineBean> rsConfigLineList = configLineBeanList.stream().filter(r -> "reportingService".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!rsConfigLineList.isEmpty()) {
            // 获取配置的Reporting Service 服务器地址
            String reportingServiceServer = ApplicationContextHelper.getConstantValue("REPORTING_SERVICE_SERVER_URL");
            if (StringUtils.isEmpty(reportingServiceServer)) {
                return;
            }

            Map<String, CoreFileBean> pdfReportMap = new LinkedHashMap<>();
            sampleList.stream().forEach(r -> {
                Optional<CoreReportConfigLineBean> reportConfigLineBeanOptional = rsConfigLineList.stream().filter(l -> ObjectUtils.equals(r.getReportConfigLineId(), l.getId())).findFirst();
                if (reportConfigLineBeanOptional.isPresent()) {
                    CoreReportConfigLineBean reportConfigLineBean = reportConfigLineBeanOptional.get();
                    String reportPath = reportConfigLineBean.getExt$Item("reportpath");

                    if (!StringUtils.isEmpty(reportPath)) {
                        StringBuilder reportUrl = new StringBuilder();

                        String reportFilePath = reportPath.substring(0, reportPath.indexOf("&") > 0 ? reportPath.indexOf("&") : reportPath.length());
                        try {
                            reportUrl.append(reportingServiceServer).append("?").append(URLEncoder.encode(reportFilePath, "UTF-8")).append("&rs:Command=Render&rs:Format=PDF");
                            String paramStr = parseReportParam(r, reportConfigLineBean.getParam());
                            if (!StringUtils.isEmpty(paramStr)) {
                                reportUrl.append("&").append(paramStr);
                            }
                            CoreFileBean coreFileBean = new CoreFileBean();
                            coreFileBean.setTargetId("T_EMC_SAMPLE" + "$" + r.getId());
                            coreFileBean.setFileExt("pdf");
                            coreFileBean.setScope(FileScope.secure.name());
                            coreFileBean.setExt("sampleRecordPdf");
                            coreFileBean.setName(r.getSampleNo()+ "-采样记录" + ".pdf");
                            pdfReportMap.put(reportUrl.toString(), coreFileBean);
                        } catch (UnsupportedEncodingException e) {
                            throw new UnsupportedException();
                        }
                    }
                }
            });
            coreFileService.getDao().deleteBy(new ArrayList<>(pdfReportMap.values()), "TARGETID", "EXT");
            pdfReportMap.entrySet().stream().forEach(e -> {
                File reportPdfFile = downloadFile(e.getKey(), defaultPdfFileName);
                if (reportPdfFile != null) {
                    coreFileService.upload(e.getValue(), reportPdfFile);
                }
            });
        }

        // 帆软报表模板
        List<CoreReportConfigLineBean> frConfigLineBeanList = configLineBeanList.stream().filter(r -> "fanruan".equals(r.getReportCategory())).collect(Collectors.toList());
        if (!frConfigLineBeanList.isEmpty()) {
            List<String> configReportLineIdList = frConfigLineBeanList.stream().map(r -> coreReportConfigLineService.getDao().getTable() + "$" + r.getId()).collect(Collectors.toList());
            List<CoreFileBean> coreFileBeanList = coreFileService.getDao().selectListByOneColumnValues(configReportLineIdList, "TARGETID");

            Map<String, CoreFileBean> pdfReportMap = new LinkedHashMap<>();
            sampleList.stream().forEach(r -> {
                Optional<CoreFileBean> coreFileBeanOptional = coreFileBeanList.stream()
                        .filter(l -> ObjectUtils.equals(coreReportConfigLineService.getDao().getTable() + "$" + r.getReportConfigLineId(), l.getTargetId())).findFirst();
                Optional<CoreReportConfigLineBean> configLineBeanOptional = frConfigLineBeanList.stream().filter(m -> ObjectUtils.equals(m.getId(), r.getReportConfigLineId())).findFirst();
                if (coreFileBeanOptional.isPresent() && configLineBeanOptional.isPresent()) {
                    CoreFileBean coreFileBean = coreFileBeanOptional.get();
                    String targetId = coreFileBean.getTargetId();
                    String fileName = targetId.substring(targetId.indexOf("$") + 1) + "/" + coreFileBean.getName();
                    if (!StringUtils.isEmpty(fileName)) {
                        StringBuilder reportUrl = new StringBuilder();
                        reportUrl.append(ApplicationContextHelper.getServerAddress()).append("/ReportServer?reportlet=").append(fileName).append("&format=pdf");
                        String paramStr = parseReportParam(r, configLineBeanOptional.get().getParam());
                        if (!StringUtils.isEmpty(paramStr)) {
                            reportUrl.append("&").append(paramStr);
                        }
                        coreFileBean = new CoreFileBean();
                        coreFileBean.setTargetId("T_EMC_SAMPLE" + "$" + r.getId());
                        coreFileBean.setFileExt("pdf");
                        coreFileBean.setScope(FileScope.secure.name());
                        coreFileBean.setExt("sampleRecordPdf");
                        coreFileBean.setName(r.getSampleNo()+ "-采样记录" + ".pdf");
                        pdfReportMap.put(reportUrl.toString(), coreFileBean);
                    }
                }
            });
            coreFileService.getDao().deleteBy(new ArrayList<>(pdfReportMap.values()), "TARGETID", "EXT");
            pdfReportMap.entrySet().stream().forEach(e -> {
                File reportPdfFile = downloadFile(e.getKey(), defaultPdfFileName);
                if (reportPdfFile != null) {
                    coreFileService.upload(e.getValue(), reportPdfFile);
                }
            });
        }
    }

    /**
     * 解析报表参数，忽略参数名大小写
     */
    private String parseReportParam(EmcSampleBean sample, String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }

        String[] values = StringUtils.split(param, "&");

        StringBuilder sb = new StringBuilder();

        Arrays.stream(values).forEach(expr -> {
            String[] pair = StringUtils.split(expr, "=");

            if (pair.length > 1) {
                Object value = BeanUtils.getPropertyValue(sample, pair[1]);

                if (value != null) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }

                    sb.append(pair[0]).append("=").append(ObjectUtils.toString(value));
                }
            }
        });

        return sb.toString();
    }

    /**
     * 下载文件，并保存到指定目录下
     */
    private File downloadFile(String url, String fileName) {
        File tempFile = FileUtils.createTempFile();
        if (StringUtils.startsWithIgnoreCase(url, "https://")) {
            HttpClientManager.getInstance().download(url, tempFile);
        } else {
            HttpClientManager.getInstance().download(url, tempFile);
        }

        return tempFile;
    }

    //现场采样交接
    @Override
    @Transactional
    public List<EmcSampleBean> handSample(RestJsonWrapperBean wrapper) {
        String acceptedDateStr = wrapper.getParamValue("acceptedDate");
        LocalDateTime acceptedDate = DateTimeUtils.parseLocalDateTime(acceptedDateStr);
        String acceptedTime = wrapper.getParamValue("acceptedTime");
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "ORDERID");
        List<EmcOrderTaskBean> orderTaskAnalysisList = allOrderTaskList.stream().filter(a -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(a.getAnalysisType())).collect(Collectors.toList());

        String currentDate = DateTimeUtils.formatLocalDate(LocalDate.now());

        sampleList.forEach(b -> {
            b.setHandStatus(EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
            b.setExt$Item("RECEIVEDATE", currentDate);
            b.setAcceptedDate(acceptedDate);
            b.setAcceptedTime(acceptedTime);
        });
        getDao().update(sampleList, "HANDSTATUS", "RECEIVEDATE", "ACCEPTEDDATE", "ACCEPTEDTIME");

        if (!orderTaskAnalysisList.isEmpty()) {
            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
            updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED);
            orderTaskService.getDao().updateByIds(updateOrderTask, orderTaskAnalysisList.stream().map(e -> e.getId()).collect(Collectors.toList()), "STATUS");
        }

        // 根据采样单创建交接单
        deliveryService.createDelivery(sampleList);

        return sampleList;
    }

    @Override
    @Transactional
    public void partHandover(RestJsonWrapperBean wrapper) {
        EmcSampleBean sample = wrapper.parseUnique(EmcSampleBean.class);
        List<EmcOrderContainerBean> targetContainerList = wrapper.parse(EmcOrderContainerBean.class);

        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        EmcDeliveryBean delivery = new EmcDeliveryBean();
        delivery.setId(ApplicationContextHelper.getNextIdentity());
        delivery.setOrgId(parentOrgId);
        delivery.setDeliveryNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        delivery.setProjId(sample.getProjId());
        delivery.setStatus("draft");
        delivery.setSource(sample.getSource());
        deliveryService.getDao().insert(delivery);

        targetContainerList.forEach(container -> container.setDeliveryId(delivery.getId()));
        orderContainerService.getDao().update(targetContainerList, "deliveryId");

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    @ValidateDataWith(PfSampleCompleteValidator.class)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {

        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = completeTaskBasic(wrapper);

        List<Long> sampleIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(sampleIdList);
        List<Long> projIdList = sampleList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");

        List<EmcSampleBean> updateSampleList = new ArrayList<>();
        List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();

        // 现场数据校核：把采样单状态改成audit
        // 现场数据审核： 把采样单状态改成done
        //采样开始时间
        String taskStatus = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
        //采样复核默认采样开始时间，若手填以手填为准
        //采样审核默认当天日期，若手填以手填为准
        if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_MUTUAL, taskStatus)) {
            // 采样单的状态改成audit
            sampleList.forEach(e -> {
                EmcOrderBean basisOrder = orderList.stream().filter(order -> ObjectUtils.equals(order.getSampleId(), e.getId()) && !ObjectUtils.isEmpty(order.getSampleCompleteDate())).findFirst().orElseGet(EmcOrderBean::new);

                EmcSampleBean updateSample = new EmcSampleBean();
                updateSample.setId(e.getId());
                updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_AUDIT);
                if (StringUtils.isEmpty(e.getExt$Item("REVIEWDATE"))) {
                    updateSample.setExt$Item("REVIEWDATE", DateTimeUtils.formatLocalDate(Optional.ofNullable(basisOrder.getSampleCompleteDate()).orElse(LocalDate.now())));
                } else {
                    updateSample.setExt$Item("REVIEWDATE", e.getExt$Item("REVIEWDATE"));
                }
                updateSampleList.add(updateSample);
            });
            getDao().update(updateSampleList, "STATUS", "REVIEWDATE");

            // 把类型为采样的ordtask状态改成audit（互审到审核），对应采样单的状态改成audit
            if (!orderList.isEmpty()) {
                List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
                if (!orderTaskList.isEmpty()) {
                    List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_SAMPLING, e.getAnalysisType()))
                            .collect(Collectors.toList());
                    if (!sampleOrderTaskList.isEmpty()) {
                        sampleOrderTaskList.forEach(e -> {
                            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                            updateOrderTask.setId(e.getId());
                            updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_AUDIT);
                            updateOrderTaskList.add(updateOrderTask);
                        });
                        orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

                        // 插入样品测试操作记录表
                        orderTaskLogService.insert(sampleOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_GROUP_AUDIT, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_MUTUAL_AUDIT);
                    }
                }
            }
        } else if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_AUDIT, taskStatus)) {
            //获取当前时间
            String currentDate = DateTimeUtils.formatLocalDate(LocalDate.now());

            // 采样单的状态改成done
            sampleList.forEach(e -> {
                EmcSampleBean updateSample = new EmcSampleBean();
                updateSample.setId(e.getId());
                updateSample.setStatus(EmcBizConstant.SAMPLE_STATUS_DONE);
                if (StringUtils.isEmpty(e.getExt$Item("AUDITDATE"))) {
                    updateSample.setExt$Item("AUDITDATE", currentDate);
                } else {
                    updateSample.setExt$Item("AUDITDATE", e.getExt$Item("AUDITDATE"));
                }
                updateSampleList.add(updateSample);
            });
            getDao().update(updateSampleList, "STATUS", "AUDITDATE");

            // 把类型为分析的ordtask状态改成preLogged（样品接收）
            // 把类型为采样的ordtask状态改成done
            if (!orderList.isEmpty()) {
                List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
                if (!orderTaskList.isEmpty()) {
                    //采样
                    List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_SAMPLING, e.getAnalysisType()))
                            .collect(Collectors.toList());
                    //分析
                    //List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, e.getAnalysisType()))
                    //        .collect(Collectors.toList());
                    if (!sampleOrderTaskList.isEmpty()) {
                        sampleOrderTaskList.forEach(e -> {
                            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                            updateOrderTask.setId(e.getId());
                            updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);
                            updateOrderTaskList.add(updateOrderTask);
                        });
                    }
                    /*if (!analysisOrderTaskList.isEmpty()) {
                        analysisOrderTaskList.forEach(e -> {
                            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                            updateOrderTask.setId(e.getId());
                            updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED);
                            updateOrderTaskList.add(updateOrderTask);
                        });
                    }*/
                    orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

                    // 插入样品测试操作记录表
                    orderTaskLogService.insert(sampleOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_AUDIT);

                    // 更新点位状态
                    folderService.updateStatus(orderTaskList);
                    // 判断对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为待录入
                    reportService.updateReportStatus(orderTaskList);
                    // 判断是否修改项目表状态
                    updateProject(projIdList);
                }
            }
        }

        // 采样管理审核插入审核记录
        insertAuditRecordBySample(sampleIdList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
        ProjectNodeUtils.updateNodes(projIdList);

        // 合并采样记录
        String loginUserId = LocalContextHelper.getLoginUserId();
        ExecutorHelper.submit(() -> composeSampleRecordPdf(sampleList, loginUserId));
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
    public void bizUndo(RestJsonWrapperBean wrapper) {
        List<Long> idList = wrapper.parse(EmcSampleBean.class).stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcSampleBean> sampleList = getDao().selectListByIds(idList);
        List<Long> projIdList = sampleList.stream().map(EmcSampleBean::getProjId).distinct().collect(Collectors.toList());
        // 采样退回插入审核记录(先生成日志再删采样单)
        insertAuditRecordBySample(idList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), null);

        // 样品登记
        List<EmcSampleBean> orderRegistrationSampleList = sampleList.stream().filter(f -> EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(f.getSource())).collect(Collectors.toList());
        if (!orderRegistrationSampleList.isEmpty()) {
            orderRegistrationSampleList.forEach(sample -> {
                sample.setStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT);
                sample.setHandStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT);
                sample.setProcessStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT);
            });
            getDao().update(orderRegistrationSampleList, "status", "HANDSTATUS", "processStatus");

            /*List<Long> projectIdList = orderRegistrationSampleList.stream().map(r -> r.getProjId()).distinct().collect(Collectors.toList());

            // 回退项目后，手动提交审核
            List<List<String>> bodyList = new ArrayList<>();
            List<String> firstBodyList = new ArrayList<>();
            projectIdList.forEach(e -> {
                Map<String, Long> hashMap = new HashMap<>();
                hashMap.put("id", e);
                String jsonString = JSONObject.toJSONString(hashMap);
                firstBodyList.add(jsonString);
            });
            bodyList.add(firstBodyList);
            List<GenericService<?, ?>> bodyServiceList = new ArrayList<>();
            bodyServiceList.add(projectService);
            RestJsonWrapperBean newRestJsonWrapper = new RestJsonWrapperBean(null, bodyList, bodyServiceList);
            newRestJsonWrapper.setParamValue("bpmn_comment", wrapper.getParamValue("bpmn_comment"));
            projectService.undoBasic(newRestJsonWrapper);

            // 将项目节点状态置为样品登记
            EmcProjectBean updateProject = new EmcProjectBean();
            updateProject.setProjNode(EmcBizConstant.PROJECT_CN_NODE_FABZ);
            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);
            projectService.getDao().updateByIds(updateProject, projectIdList, "PROJNODE", "STATUS");

            // 删除采样表
            List<EmcSampleBean> deleteSampleList = getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            getDao().deleteBy(deleteSampleList);

            // 删除业务表：样品测试、样品结果
            List<EmcOrderTaskBean> deleteTaskList = orderTaskService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
            List<Long> folderIdList = deleteTaskList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
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
                updateBpmnComment.setTargetId(projectService.getDao().getTable() + "$" + id);
                updateBpmnComment.setStatus("UNDO");
                updateBpmnComment.setTaskName(EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT);
                updateBpmnComment.setComment(wrapper.getParamValue("bpmn_comment"));
                updateBpmnCommentList.add(updateBpmnComment);
            });
            coreBpmnCommentService.getDao().update(updateBpmnCommentList, ArrayUtils.asList("taskName"), "targetId", "status");*/
        }

        // 非样品登记的修改样品测试表状态
        List<EmcSampleBean> notOrderRegistrationSampleList = sampleList.stream().filter(f -> !EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(f.getSource())).collect(Collectors.toList());
        if (!notOrderRegistrationSampleList.isEmpty()) {
            List<Long> notOrderRegistrationSampleIdList = notOrderRegistrationSampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(notOrderRegistrationSampleIdList, "SAMPLEID", Collections.singletonList("ID"));
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Collections.singletonList("ID"));

            if (!orderTaskList.isEmpty()) {
                orderTaskList.forEach(orderTask -> orderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DRAFT));
                orderTaskService.getDao().update(orderTaskList, "STATUS");
            }

            // 对于已经走完采样三级审核的采样单，样品接收退回时需要执行工作流退回
            List<Long> doneSampleIdList = notOrderRegistrationSampleList.stream().filter(sample -> StringUtils.equalsIgnoreCase(ProcessStatus.DONE.name(), sample.getProcessStatus())).map(EmcSampleBean::getId).collect(Collectors.toList());
            if (!doneSampleIdList.isEmpty()) {
                CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
                parameter.setComment(wrapper.getParamValue("bpmn_comment"));
                this.undo(doneSampleIdList, parameter);
            }

            List<Long> approveSampleIdList = notOrderRegistrationSampleList.stream().filter(sample -> StringUtils.startsWithIgnoreCase(sample.getProcessStatus(), ProcessStatus.APPROVE.name())).map(EmcSampleBean::getId).collect(Collectors.toList());
            if (!approveSampleIdList.isEmpty()) {
                CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
                parameter.setComment(wrapper.getParamValue("bpmn_comment"));
                this.rejectTask(approveSampleIdList, parameter);
            }

            // 将sample表status置为draft
            sampleList.forEach(sample -> {
                sample.setStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT);
                sample.setHandStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT);
            });
            getDao().update(sampleList, "STATUS", "HANDSTATUS");
        }
        // 修改项目节点状态
        ProjectNodeUtils.updateNodes(projIdList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        // 采样互审、采样审核拒绝
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = rejectTaskBasic(wrapper);
        List<Long> sampleIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID");
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "ORDERID");

        ProcessStatus processStatus = coreBpmnInstanceStatusDTOList.get(0).getProcessStatus();
        List<EmcSampleBean> sampleList = getDao().selectListByIds(sampleIdList);
        List<Long> projIdList = sampleList.stream().map(EmcSampleBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcSampleBean> updateSampleList = sampleList.stream().filter(a -> !a.getStatus().equals(EmcBizConstant.SAMPLE_STATUS_REPORTUNDO)).collect(Collectors.toList());
        // 拒绝到结果录入
        if (ObjectUtils.equals(processStatus, ProcessStatus.DRAFT_REJECTED)) {
            updateSampleList.forEach(a -> a.setStatus(EmcBizConstant.SAMPLE_STATUS_DRAFT));
            getDao().update(updateSampleList, "STATUS");

            List<EmcOrderTaskBean> sampleOrderTaskList = allOrderTaskList.stream().filter(e -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(e.getAnalysisType())).collect(Collectors.toList());
            sampleOrderTaskList.forEach(e -> e.setStatus(EmcBizConstant.ORDERTASK_STATUS_DRAFT));
            orderTaskService.getDao().update(sampleOrderTaskList, "STATUS");
        }

        // 拒绝到采样互审
        if (ObjectUtils.equals(processStatus, ProcessStatus.APPROVE_REJECTED)) {

            updateSampleList.forEach(a -> a.setStatus(EmcBizConstant.SAMPLE_STATUS_APPROVE));
            getDao().update(updateSampleList, "STATUS");

            List<EmcOrderTaskBean> sampleOrderTaskList = allOrderTaskList.stream().filter(e -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(e.getAnalysisType())).collect(Collectors.toList());
            sampleOrderTaskList.forEach(e -> e.setStatus(EmcBizConstant.ORDERTASK_STATUS_APPROVE));
            orderTaskService.getDao().update(sampleOrderTaskList, "STATUS");
        }

        if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_AUDIT, coreBpmnInstanceStatusDTOList.get(0).getStatusCode())) {
            // 插入样品测试操作记录表
            orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT_UNDO, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_AUDIT);
        }
        if (ObjectUtils.equals(EmcBizConstant.SAMPLE_STATUS_MUTUAL, coreBpmnInstanceStatusDTOList.get(0).getStatusCode())) {
            // 插入样品测试操作记录表
            orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_GROUP_AUDIT_UNDO, EmcBizConstant.ORDER_TASK_LOG_SAMPLIE_MUTUAL_AUDIT);
        }
        ProjectNodeUtils.updateNodes(projIdList);
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public Page<EmcItemBean> selectEditItem(Long id) {
        EmcOrderTaskBean orderTask = orderTaskService.selectById(id);
        // 取样品分析项组
        EmcOrderItemGroupBean filter = new EmcOrderItemGroupBean();
        filter.setSchemeName(orderTask.getSchemeName());
        filter.setTestId(orderTask.getTestId());
        filter.setTestMethodId(orderTask.getTestMethodId());
        filter.setMonitorTmplId(orderTask.getMonitorTmplId());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectList(filter);
        if (orderItemGroupList.isEmpty()) {
            return null;
        }
        // 根据分析项组获取分析项
        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList, Collections.singletonList("itemGroupId"));
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());

        // 筛选类型是采样且启用的分析项
        List<EmcItemBean> itemList = itemService.selectListByFilter(SearchFilter.instance().match("id", itemIdList).filter(MatchPattern.OR)
                .match("analysisCategory", "sampling").filter(MatchPattern.EQ)
                .match("activatedFlag", Constant.YES).filter(MatchPattern.EQ));

        return new Pagination<>(itemList);
    }

    //根据样品类型与模板模板名称相匹配找到模板组
    @Override
    public String selectCategoryCode(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);

        String monitorTmplId = sampleList.get(0).getMonitorTmplId();
        if (monitorTmplId == null) {
            return null;
        }
        ;
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByFilter(SearchFilter.instance().match("id", monitorTmplId).filter(MatchPattern.EQ));
        //根据模板id找到orderCategoryCode
        EmcMonitorTmplBean monitorTmpl = monitorTmplList.get(0);
       /* Long monitorTmplGroupId = monitorTmpl.getMonitorTmplGroupId();
        List<EmcMonitorTmplGroupBean> monitorTmplGroupList = monitorTmplGroupService.selectListByFilter(SearchFilter.instance().match("id", monitorTmplGroupId).filter(MatchPattern.EQ));*/
        String orderCategoryCode = monitorTmpl.getExt$Item("ordercategorycode");
        return orderCategoryCode;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void syncTestItem(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> allOrderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId");
        List<EmcOrderBean> orderList = allOrderList.stream().filter(order -> ObjectUtils.isEmpty(order.getSampleId())).collect(Collectors.toList());

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> taskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");

        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByIds(capacityItemGroupIdList);

        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.selectListByIds(itemIdList);

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newTaskResultList = new ArrayList<>();

        folderList.forEach(folder -> {
            List<EmcOrderBean> folderOrderList = orderList.stream().filter(order -> ObjectUtils.equals(folder.getId(), order.getFolderId())).collect(Collectors.toList());
            folderOrderList.forEach(order -> {
                List<EmcOrderItemGroupBean> groupList = orderItemGroupList.stream().filter(group -> ObjectUtils.equals(order.getId(), group.getOrderId())).collect(Collectors.toList());

                List<EmcOrderTaskBean> taskList = orderTaskList.stream().filter(task -> ObjectUtils.equals(order.getId(), task.getOrderId())).collect(Collectors.toList());
                Map<Long, List<EmcOrderTaskBean>> testTaskMap = taskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestMethodId));

                List<Long> taskIdList = taskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskResultBean> resultList = taskResultList.stream().filter(result -> taskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());

                testTaskMap.forEach((testMethodId, curTaskList) -> {
                    List<Long> curCapacityItemGroupIdList = groupList.stream().filter(group -> ObjectUtils.equals(group.getTestMethodId(), testMethodId)).map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
                    List<Long> curItemGroupIdList = capacityItemGroupList.stream().filter(group -> curCapacityItemGroupIdList.contains(group.getId())).map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
                    List<Long> curItemIdList = groupItemList.stream().filter(groupItem -> curItemGroupIdList.contains(groupItem.getItemGroupId())).map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
                    List<EmcItemBean> curItemList = itemList.stream().filter(item -> curItemIdList.contains(item.getId())).collect(Collectors.toList());
                    Map<String, List<EmcItemBean>> curItemMap = curItemList.stream().collect(Collectors.groupingBy(EmcItemBean::getAnalysisCategory));

                    curItemMap.forEach((analysisCategory, items) -> {
                        EmcOrderTaskBean partOrderTask = curTaskList.stream().filter(task -> StringUtils.equals(task.getAnalysisType(), analysisCategory)).findFirst().orElse(null);
                        if (partOrderTask == null) {
                            EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                            BeanUtils.copyProperties(curTaskList.get(0), newOrderTask);
                            newOrderTask.setId(ApplicationContextHelper.getNextIdentity());
                            newOrderTask.setAnalysisType(analysisCategory);
                            newOrderTaskList.add(newOrderTask);

                            items.forEach(item -> newTaskResultList.add(createTaskResult(order, newOrderTask, item)));
                            return;
                        }

                        List<EmcOrderTaskResultBean> partResultList = resultList.stream().filter(result -> ObjectUtils.equals(partOrderTask.getId(), result.getOrderTaskId())).collect(Collectors.toList());
                        List<EmcItemBean> diffItemList = items.stream().filter(item -> partResultList.stream().noneMatch(result -> ObjectUtils.equals(result.getItemId(), item.getId()))).collect(Collectors.toList());
                        diffItemList.forEach(item -> newTaskResultList.add(createTaskResult(order, partOrderTask, item)));
                    });
                });
            });
        });

        if (!newOrderTaskList.isEmpty()) {
            orderTaskService.getDao().insert(newOrderTaskList);
        }

        if (!newTaskResultList.isEmpty()) {
            orderTaskResultService.getDao().insert(newTaskResultList);
        }
        System.out.println(newOrderTaskList.size());
        System.out.println(newTaskResultList.size());
    }

    private EmcOrderTaskResultBean createTaskResult(EmcOrderBean order, EmcOrderTaskBean orderTask, EmcItemBean item) {
        EmcOrderTaskResultBean newResult = new EmcOrderTaskResultBean();
        newResult.setId(ApplicationContextHelper.getNextIdentity());
        newResult.setFolderId(orderTask.getFolderId());
        newResult.setOrderId(orderTask.getOrderId());
        newResult.setOrderTaskId(orderTask.getId());
        newResult.setProjId(orderTask.getProjId());
        newResult.setTestMethodId(orderTask.getTestMethodId());
        newResult.setTestId(orderTask.getTestId());
        newResult.setMethodId(orderTask.getMethodId());
        newResult.setMethodVersionId(orderTask.getMethodVersionId());
        newResult.setItemId(item.getId());
        newResult.setItemOrderNo(item.getOrderNo());
        newResult.setItemName(item.getItemName());
        newResult.setItemAliasName(item.getItemAliasName());
        newResult.setRevisionRule(item.getRevisionRule());
        newResult.setUnitName(item.getUnitName());
        newResult.setRepeatNo(item.getRepeatItem());
        newResult.setRepeatQty(item.getRepeatItem());
        newResult.setOriginalResult(item.getDefaultResult());
        newResult.setReportDisplay(item.getReportDisplay());
        newResult.setAnalysisType(item.getAnalysisCategory());
        newResult.setLowLimit(item.getLowLimit());
        newResult.setHighLimit(item.getHighLimit());
        newResult.setOrderNo(order.getOrderNo());
        newResult.setCycle(order.getCycle());
        newResult.setFrequency(order.getFrequency());
        newResult.setQcCategoryId(order.getQcCategoryId());
        newResult.setQcCategoryCode(order.getQcCategoryCode());
        newResult.setQcName(order.getQcName());
        newResult.setScriptFlag(item.getScriptFlag());
        newResult.setResultCategory(item.getResultCategory());

        return newResult;
    }


    @Override
    @Transactional
    public String submitReminds(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);

        // 1、验证是否有数据
        List<Long> sampleIdList = sampleList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR));
        String reminds = null;

        if (!orderList.isEmpty()) {
            // 过滤终止的样品
            List<EmcOrderBean> normalOrderList = orderList.stream().filter(f -> !Constant.YES.equals(f.getTermination())).collect(Collectors.toList());
            List<Long> orderIdList = normalOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            if (!orderIdList.isEmpty()) {
                String firstParamPointResult = "样品编号: ";
                String secondParamPointResult = "样品编号: ";
                String thirdParamPointResult = "样品编号: ";

                // 筛选指派仪器信息 -- 分析人，仪器不能为空
                // 过滤出现场空白的样品测试，对于现场空白样品测试不需要校验仪器
                // 过滤出已经分包的样品测试，对于已分包的样品测试不需要校验仪器
                List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR)
                        .match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ)
                        .match("SUBCONTRACTORID", null).filter(MatchPattern.SEQ)
                        .match("QCCATEGORYCODE", "P-BLANK").filter(MatchPattern.DIFFER), wrapper);

                String equiptId = "";
                String analysisUserId = "";

                String equiptIdPoint = "";
                String analysisUserIdPoint = "";
                for (int i = 0; i < orderTaskList.size(); i++) {

                    if (orderTaskList.get(i).getEquiptId() == null) {
                        equiptId += orderTaskList.get(i).getExt$Item("ORDERNO") + ",";
                        equiptIdPoint = "仪器未选择 ";
                    }
                    if (orderTaskList.get(i).getAnalysisUserId() == null) {
                        analysisUserId += orderTaskList.get(i).getExt$Item("ORDERNO") + ",";
                        analysisUserIdPoint = "分析人未选择 ";
                    }
                }
                if (!StringUtils.isEmpty(equiptIdPoint)) {
                    firstParamPointResult += equiptId + equiptIdPoint + "</br>";
                }
                if (!StringUtils.isEmpty(analysisUserIdPoint)) {
                    secondParamPointResult += analysisUserId + analysisUserIdPoint + "</br>";
                }

                // 筛选结果信息 -- 原始结果不能为空
                if (!orderTaskList.isEmpty()) {
                    List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                    List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("ORDERTASKID", orderTaskIdList).filter(MatchPattern.OR)
                            .match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ), wrapper);

                    String reportResult = "";

                    String reportResultPoint = "";
                    for (int i = 0; i < orderTaskResultList.size(); i++) {

                        if (orderTaskResultList.get(i).getReportResult() == null) {
                            reportResult += orderTaskResultList.get(i).getOrderNo() + ",";
                            reportResultPoint = "原始结果未填写 ";
                        }

                    }
                    if (!StringUtils.isEmpty(reportResultPoint)) {
                        // 过滤重复的样品编号
                        String[] array = reportResult.trim().split(",");
                        List<String> list = Arrays.asList(array);
                        Set<String> set = new HashSet<String>(list);
                        String[] rid = set.toArray(new String[0]);
                        thirdParamPointResult += StringUtils.join(rid, ",") + reportResultPoint + "</br>";
                    }

                    if (orderTaskResultList.stream().anyMatch(e -> (StringUtils.isEmpty(e.getReportResult())))) {
                        reminds = thirdParamPointResult;
                    }
                }
                if (orderTaskList.stream().anyMatch(e -> (ObjectUtils.isEmpty(e.getEquiptId())) || StringUtils.isEmpty(e.getAnalysisUserId()))) {
                    if (firstParamPointResult.length() > 6 && secondParamPointResult.length() > 6) {
                        reminds += firstParamPointResult + secondParamPointResult;
                    } else if (firstParamPointResult.length() == 6) {
                        reminds += secondParamPointResult;
                    } else if (secondParamPointResult.length() == 6) {
                        reminds += firstParamPointResult;
                    }
                }
            }
        }
        return reminds;
    }

    @Override
    public void submitDelivery(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        String currentDate = DateTimeUtils.formatLocalDate(LocalDate.now());
        sampleList.forEach(sample -> {
            sample.setStatus(EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
            sample.setHandStatus(EmcBizConstant.SAMPLE_STATUS_PRELOGGED);
            sample.setExt$Item("RECEIVEDATE", currentDate);
        });

        getDao().update(sampleList, "status", "HANDSTATUS", "RECEIVEDATE");
        deliveryService.createDelivery(sampleList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @ValidateDataWith(PfSampleAcceptValidator.class)
    public void accept(RestJsonWrapperBean jsonWrapper) {
        List<EmcSampleBean> sampleList = jsonWrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcSampleBean> submitSampleList = getDao().selectListByOneColumnValues(sampleIdList, "ID", Arrays.asList("ID", "PROJID", "SOURCE", "SEPSAMPLETIME", "acceptedUserId", "accepteUserName", "acceptedDate"));

        // 样品接受人和接收时间
        String acceptedUserId = LocalContextHelper.getLoginUserId();
        String acceptedUserName = LocalContextHelper.getLoginUser().getUserName();

        submitSampleList.stream().filter(sample -> ObjectUtils.isEmpty(sample.getAcceptedDate())).forEach(sample -> {
            if (StringUtils.isEmpty(sample.getAcceptedUserId())) {
                sample.setAcceptedUserId(acceptedUserId);
            }
            if (StringUtils.isEmpty(sample.getAccepteUserName())) {
                sample.setAccepteUserName(acceptedUserName);
            }
        });

        getDao().update(submitSampleList, "ACCEPTEDUSERID", "ACCEPTEUSERNAME");

        // 过滤出样品登记生成的采样单
        List<EmcSampleBean> orderRegistrationSampleList = submitSampleList.stream().filter(s -> StringUtils.equals(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION, s.getSource())).collect(Collectors.toList());
        if (!orderRegistrationSampleList.isEmpty()) {
            orderRegistrationSampleList.forEach(s -> {
                // 更改采样单状态
                s.setStatus(EmcBizConstant.SAMPLE_PROCESSSTATUS_DONE);
                // 更改采样单工作流状态
                s.setProcessStatus(EmcBizConstant.SAMPLE_PROCESSSTATUS_DONE);
            });

            this.getDao().update(orderRegistrationSampleList, "STATUS", "PROCESSSTATUS");
            // 样品接收提交之后将送样d分析类型为采样的样品测试的状态置为done
            List<Long> sendSampleIdList = orderRegistrationSampleList.stream().map(s -> s.getId()).collect(Collectors.toList());
            SearchFilter filterSend = SearchFilter.instance().match("SAMPLEID", sendSampleIdList).filter(MatchPattern.OR);
            filterSend.match("TERMINATION", Constant.YES).filter(MatchPattern.DIFFER);
            List<EmcOrderBean> sendOrderList = orderService.selectListByFilter(filterSend);

            if (!sendOrderList.isEmpty()) {
                List<Long> sendOrderIdList = sendOrderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());

                List<EmcOrderTaskBean> sendOrderTaskList = orderTaskService.selectListByFilter(
                        SearchFilter.instance().match("ORDERID", sendOrderIdList).filter(MatchPattern.OR));
                List<EmcOrderTaskBean> sendSampleOrderTaskList = sendOrderTaskList.stream().filter(a -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(a.getAnalysisType())).collect(Collectors.toList());
                // 更新数据
                if (!sendSampleOrderTaskList.isEmpty()) {

                    EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();

                    updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);
                    updateOrderTask.setExt$Item("ADDTESTSTATUS", "1");


                    orderTaskService.getDao().updateByIds(updateOrderTask, sendSampleOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList()), "STATUS", "ADDTESTSTATUS");
                }
            }
        }

        // 样品接收提交之后将分析类型为分析的样品测试的状态置为logged
        SearchFilter filter = SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR);
        filter.match("TERMINATION", Constant.YES).filter(MatchPattern.DIFFER);
        List<EmcOrderBean> orderList = orderService.selectListByFilter(filter);

        if (!orderList.isEmpty()) {
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderToChangeAll = orderService.getDao().selectListByIds(orderIdList);
            List<Long> orderSampleIdList = orderList.stream().map(EmcOrderBean::getSampleId).distinct().collect(Collectors.toList());
            List<EmcSampleBean> orderSampleList = getDao().selectListByIds(orderSampleIdList);
            // 样品表更新接收人和接收时间
            List<EmcOrderBean> filterOrderList = new ArrayList<>();
            orderIdList.forEach(orderId -> {
                //找到sampleid，根据sampleid找到接收日期
                List<EmcOrderBean> orderToChange = orderToChangeAll.stream().filter(order -> ObjectUtils.equals(orderId, order.getId())).collect(Collectors.toList());
                Long sampleId = orderToChange.get(0).getSampleId();//根据oderid只会找到一条
                List<EmcSampleBean> getDateList = orderSampleList.stream().filter(sample -> ObjectUtils.equals(sampleId, sample.getId())).collect(Collectors.toList());
                LocalDateTime acceptDate = getDateList.get(0).getAcceptedDate();
                EmcOrderBean orderD = new EmcOrderBean();
                orderD.setId(orderId);
                orderD.setAcceptedUserId(getDateList.get(0).getAcceptedUserId());
                orderD.setAccepteUserName(getDateList.get(0).getAccepteUserName());
                orderD.setAcceptedDate(acceptDate);
                filterOrderList.add(orderD);
            });
            //根据采样单来给样品插入接收日期
            orderService.getDao().update(filterOrderList, "ACCEPTEDUSERID", "ACCEPTEUSERNAME", "ACCEPTEDDATE");

            List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));

            List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskList.stream().filter(a -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(a.getAnalysisType())).collect(Collectors.toList());
            List<EmcOrderTaskBean> sampleOrderTaskList = orderTaskList.stream().filter(a -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(a.getAnalysisType())).collect(Collectors.toList());
            if (!sampleOrderTaskList.isEmpty()) {
                sampleOrderTaskList.forEach(ot -> {
                    List<EmcOrderBean> order2SampleList = orderList.stream().filter(o -> ObjectUtils.equals(ot.getOrderId(), o.getId())).collect(Collectors.toList());
                    List<Long> order2SampleIdList = order2SampleList.stream().map(EmcOrderBean::getSampleId).distinct().collect(Collectors.toList());
                    List<EmcSampleBean> sampleFilter2List = submitSampleList.stream().filter(sam -> ObjectUtils.equals(sam.getId(), order2SampleIdList.get(0))).collect(Collectors.toList());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime time = sampleFilter2List.get(0).getAcceptedDate();
                    ot.setExt$Item("ASSIGNDATE", df.format(time));
                    ot.setExt$Item("ADDTESTSTATUS", "1");
                });
                orderTaskService.getDao().update(sampleOrderTaskList, "ASSIGNDATE", "ADDTESTSTATUS");
            }

            // 插入留样管理
            addOrderRetain(orderList, orderTaskList);
            // 更新数据
            if (!analysisOrderTaskList.isEmpty()) {
                //更新容器表的接样日期、接样人
                List<Long> analysisOrderContainerIdList = analysisOrderTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId)
                        .distinct().collect(Collectors.toList());
                List<EmcOrderContainerBean> analysisOrderContainerList = orderContainerService.selectListByIds(analysisOrderContainerIdList);
                if (!CollectionUtils.isEmpty(analysisOrderContainerList)) {
                    List<EmcOrderContainerBean> updateOrderContainerList = new ArrayList<>();
                    analysisOrderContainerList.forEach(orderContainer -> {
                        EmcOrderBean order = orderToChangeAll.stream().filter(e -> ObjectUtils.equals(e.getId(), orderContainer.getOrderId()))
                                .findFirst().orElse(null);
                        if (!ObjectUtils.isEmpty(order) && !ObjectUtils.isEmpty(order.getSampleId())) {
                            EmcSampleBean sampleBean = orderSampleList.stream().filter(e -> ObjectUtils.equals(e.getId(), order.getSampleId()))
                                    .findFirst().orElse(null);
                            if (!ObjectUtils.isEmpty(sampleBean)) {
                                orderContainer.setAcceptedDate(sampleBean.getAcceptedDate());
                                orderContainer.setAcceptedTime(sampleBean.getAcceptedTime());
                                orderContainer.setAcceptedUserId(acceptedUserId);
                                orderContainer.setAcceptedUserName(acceptedUserName);
                                updateOrderContainerList.add(orderContainer);
                            }
                        }
                    });

                    orderContainerService.getDao().fastUpdate(updateOrderContainerList, "acceptedDate", "acceptedTime", "acceptedUserId", "acceptedUserName");
                }

                EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();

                updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
                updateOrderTask.setExt$Item("ADDTESTSTATUS", "1");
                orderTaskService.getDao().updateByIds(updateOrderTask, analysisOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList()), "STATUS", "ADDTESTSTATUS");
            }

            // 将分包的样品测试的状态置为done
            List<EmcOrderTaskBean> subcontractorOrderTaskList = orderTaskList.stream().filter(e -> !ObjectUtils.isEmpty(e.getSubcontractorId())).collect(Collectors.toList());

            if (!subcontractorOrderTaskList.isEmpty()) {
                List<Long> subOrderTaskIdList = subcontractorOrderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());

                EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);

                orderTaskService.getDao().updateByIds(updateOrderTask, subOrderTaskIdList, "STATUS");

                // 只保留分包测试的在报告中显示的分析项，并且去掉计算公式，其他分析项删掉
                List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao()
                        .selectListByOneColumnValues(subOrderTaskIdList, "ORDERTASKID");
                /*List<Long> deleteOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.isEmpty(e.getReportDisplay()) || ObjectUtils.equals(Constant.NO, e.getReportDisplay()))
                        .map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                orderTaskResultService.getDao().deleteByIdList(deleteOrderTaskResultIdList);*/

                List<Long> updateOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.equals(Constant.YES, e.getReportDisplay())).map(e -> e.getId()).collect(Collectors.toList());
                EmcOrderTaskResultBean updateOrderTaskResult = new EmcOrderTaskResultBean();
                updateOrderTaskResult.setScriptFlag("0");
                orderTaskResultService.getDao().updateByIds(updateOrderTaskResult, updateOrderTaskResultIdList, "SCRIPT");

                // 分包的数据在分包数据录入能录入
                List<EmcSubcontractRecordBean> existedRecordList = subcontractRecordService.getDao().selectListByOneColumnValues(subOrderTaskIdList, "orderTaskId", Arrays.asList("id", "orderTaskId"));
                List<Long> existedTaskIdList = existedRecordList.stream().map(EmcSubcontractRecordBean::getOrderTaskId).distinct().collect(Collectors.toList());

                subcontractorOrderTaskList.stream().filter(task -> !existedTaskIdList.contains(task.getId())).map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
                List<EmcOrderTaskBean> newSubOrderTaskList = subcontractorOrderTaskList.stream().filter(task -> !existedTaskIdList.contains(task.getId())).collect(Collectors.toList());
                ArrayList<EmcOrderTaskBean> newUniqueSubTaskList = newSubOrderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getId))), ArrayList::new));

                subcontractRecordService.insert(newUniqueSubTaskList);
            }

            // 数据共享的数据不走采样和分析
            List<EmcOrderTaskBean> dataSharingOrderTaskList = orderTaskList.stream()
                    .filter(e -> ObjectUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, e.getAnalysisType()) && ObjectUtils.equals(Constant.YES, e.getDataSharing()))
                    .collect(Collectors.toList());

            if (!dataSharingOrderTaskList.isEmpty()) {

                List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();

                dataSharingOrderTaskList.forEach(e -> {

                    EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
                    updateOrderTask.setId(e.getId());
                    updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);
                    updateOrderTaskList.add(updateOrderTask);
                });

                orderTaskService.getDao().update(updateOrderTaskList, "STATUS");

                // 只保留分包测试的在报告中显示的分析项，并且去掉计算公式，其他分析项删掉
                List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao()
                        .selectListByOneColumnValues(dataSharingOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList()), "ORDERTASKID");
                List<Long> deleteOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.isEmpty(e.getReportDisplay()) || ObjectUtils.equals(Constant.NO, e.getReportDisplay()))
                        .map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                orderTaskResultService.getDao().deleteByIdList(deleteOrderTaskResultIdList);

                List<Long> updateOrderTaskResultIdList = orderTaskResultList.stream().filter(e -> ObjectUtils.equals(Constant.YES, e.getReportDisplay())).map(EmcOrderTaskResultBean::getId)
                        .collect(Collectors.toList());
                EmcOrderTaskResultBean updateOrderTaskResult = new EmcOrderTaskResultBean();
                updateOrderTaskResult.setScriptFlag("0");
                orderTaskResultService.getDao().updateByIds(updateOrderTaskResult, updateOrderTaskResultIdList, "SCRIPT");

            }

            // 判断更新的样品测试对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为preLogged
            updateReportStatus(sampleIdList, orderList, orderTaskList);

            // 更新测试前处理状态
            updatePretreatmentStatus(orderTaskList);

            // 插入样品测试操作记录表
            List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList()), "ORDERID");
            orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT, "");

            // 更新项目节点
            List<Long> projIdList = orderList.stream().map(EmcOrderBean::getProjId).distinct().collect(Collectors.toList());
            ProjectNodeUtils.updateNodes(projIdList);
        }
        // 样品接收提交插入审核记录
        insertAuditRecordBySample(sampleIdList, "PROCESS_ACCEPT", "接收", null);
        List<Long> projIdSList = submitSampleList.stream().map(EmcSampleBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projectSBeans = projectService.selectListByIds(projIdSList);
        //项目下得所有样品
        List<EmcOrderBean> orderAllList = orderService.getDao().selectListByOneColumnValues(projIdSList, "projid");
        List<EmcProjectBean> projectSUpdateBeans = new ArrayList<>();
        projectSBeans.forEach(p -> {
            List<EmcOrderBean> orderSingleProjList = orderAllList.stream().filter(o -> ObjectUtils.equals(p.getId(), o.getProjId())).distinct().collect(Collectors.toList());
            if (!orderSingleProjList.isEmpty()) {
                List<LocalDateTime> allAcceptDateList = orderSingleProjList.stream().filter(o -> !ObjectUtils.isEmpty(o.getAcceptedDate())).map(EmcOrderBean::getAcceptedDate).distinct().collect(Collectors.toList());
                if (!allAcceptDateList.isEmpty()) {
                    CollectionUtils.sort(allAcceptDateList, (o1, o2) -> o1.compareTo(o2));
                    String LasterAcceptDateStr = allAcceptDateList.get(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    p.setExt$Item("LASTESTACCEPTEDDATE", LasterAcceptDateStr);
                    projectSUpdateBeans.add(p);//需要更新日期得项目
                    //projectService.getDao().update(p,"LASTESTACCEPTEDDATE");
                } else {
                    LocalDateTime date = LocalDateTime.now();
                    String LasterAcceptDateStrNow = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    p.setExt$Item("LASTESTACCEPTEDDATE", LasterAcceptDateStrNow);
                    //projectService.getDao().update(projOne,"LASTESTACCEPTEDDATE");
                    projectSUpdateBeans.add(p);//需要更新日期得项目
                }

            }
        });
        projectService.getDao().update(projectSUpdateBeans, "LASTESTACCEPTEDDATE");
    }

    private void updatePretreatmentStatus(List<EmcOrderTaskBean> orderTaskList) {
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "TESTMETHODTYPE"));
        List<Long> dioxinTestMethodIdList = testMethodList.stream().filter(tm -> StringUtils.equals(tm.getTestMethodType(), "dioxin")).map(EmcTestMethodBean::getId).collect(Collectors.toList());

        // 只有二噁英需要进行前处理
        orderTaskList.forEach(ot -> {
            boolean isDioxin = dioxinTestMethodIdList.contains(ot.getTestMethodId());
            ot.setExt$Item("pretreatmentStatus", isDioxin ? Constant.NO : Constant.YES);
        });

        List<EmcOrderTaskBean> dioxinOrderTaskList = orderTaskList.stream().filter(ot -> dioxinTestMethodIdList.contains(ot.getTestMethodId())).collect(Collectors.toList());

        //查询上一次本基地前处理提交的相同样品类型、项目方法的前处理记录
        List<Long> dioxinOrderTaskIdList = dioxinOrderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        RestJsonWrapperBean wrapper = new RestJsonWrapperBean();
        wrapper.setFilterValue("orderTaskIdList", dioxinOrderTaskIdList);
        List<EmcPretreatmentBean> lastPretreatmentList = CollectionUtils.isEmpty(dioxinOrderTaskIdList) ? Collections.emptyList() :
                pretreatmentService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ)
                        .match("status", Constant.YES).filter(MatchPattern.SEQ), wrapper);

        // 查询默认的静态方法
        List<CoreCodeBean> purifyMethodCodeList = coreCodeService.getDao().selectListByOneColumnValue("purifyMethod", "codeCategoryId");

        //按照时间排序
        lastPretreatmentList.sort(Comparator.comparing(EmcPretreatmentBean::getCreatedTime).reversed());

        Map<String, List<EmcPretreatmentBean>> lastPretreatmentMap = lastPretreatmentList.stream().collect(Collectors.groupingBy(o -> o.getExt$Item("monitortmplid") + "$" + o.getExt$Item("testmethodid")));

        List<EmcPretreatmentBean> pretreatmentList = new ArrayList<>(dioxinOrderTaskList.size());
        dioxinOrderTaskList.forEach(ot -> {
            String key = ot.getMonitorTmplId() + "$" + ot.getTestMethodId();
            EmcPretreatmentBean pretreatment = new EmcPretreatmentBean();
            //如果找得到上一次的前处理记录，则把上一次提交的前处理记录的值，默认复制到本次前处理记录
            if (lastPretreatmentMap.containsKey(key)) {
                BeanUtils.copyProperties(lastPretreatmentMap.get(key).get(0), pretreatment, EmcConstant.CREATED_COLUMNS);
            }
            pretreatment.setId(ApplicationContextHelper.getNextIdentity());
            pretreatment.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
            pretreatment.setProjId(ot.getProjId());
            pretreatment.setFolderId(ot.getFolderId());
            pretreatment.setOrderId(ot.getOrderId());
            pretreatment.setOrderTaskId(ot.getId());
            pretreatment.setStatus(Constant.NO);

            // 设置默认的净化方法
            if (!purifyMethodCodeList.isEmpty()) {
                pretreatment.setPurifyMethod(purifyMethodCodeList.get(0).getCodeName());
            }

            pretreatmentList.add(pretreatment);
        });

        orderTaskService.getDao().update(orderTaskList, "pretreatmentStatus");
        pretreatmentService.getDao().insert(pretreatmentList);
    }

    private void addOrderRetain(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> taskList) {

        List<Long> testMethodIdList = taskList.stream().map(a -> a.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.selectListByIds(testMethodIdList);
        List<Integer> retainTimes = testMethodList.stream().filter(a -> !StringUtils.isEmpty(a.getRetainTime())).distinct().map(b -> NumberUtils.parseInt(b.getRetainTime())).collect(Collectors.toList());
        int retainHours = retainTimes.isEmpty() ? 0 : retainTimes.stream().min(Comparator.naturalOrder()).get();

        List<EmcOrderRetainBean> insertList = new ArrayList<>();
        for (EmcOrderBean o : orderList) {
            EmcOrderRetainBean retain = new EmcOrderRetainBean();
            retain.setId(ApplicationContextHelper.getNextIdentity());
            retain.setOrderId(o.getId());
            retain.setProjId(o.getProjId());
            retain.setFolderId(o.getFolderId());
            retain.setRetainQty(1L);
            retain.setRetainUserId(LocalContextHelper.getLoginUserId());
            LocalDate date = o.getSampleBeginDate();
            if (date != null) {
                String time = StringUtils.isEmpty(o.getSampleBeginTime()) ? "00:00:00" : o.getSampleBeginTime();
                String dateTime = date.toString() + " " + time;
                LocalDateTime sampleDateTime = DateTimeUtils.parseLocalDateTime(dateTime);
                sampleDateTime = sampleDateTime.plusHours(retainHours);
                retain.setRetainTimeLimit(sampleDateTime);
            }
            insertList.add(retain);
        }
        orderRetainService.getDao().insert(insertList);
    }


    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void invalidWeightResult(RestJsonWrapperBean wrapper) {
        // 执行之后把原始结果和报出结果都更新成/
        // 未录结果（原始结果为空的数据）就是当前选中的样品下所有样品结果中所有没录的分析项（分析类型为采样）
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        String runId = wrapper.getFilterValue("runId");
        List<EmcOrderTaskResultBean> orderTaskResultList = new ArrayList<>();
        orderTaskList.stream().forEach(e -> {
            EmcOrderTaskResultBean result = new EmcOrderTaskResultBean();
            result.setOrderId(e.getOrderId());
            result.setRunId(runId);
            orderTaskResultList.add(result);
        });

        List<EmcOrderTaskResultBean> orderTaskResultAllList = orderTaskResultService.getDao().selectList(orderTaskResultList, Arrays.asList("ORDERID", "RUNID"), Collections.emptyList());
        orderTaskResultAllList.stream().forEach(e -> {
            if (StringUtils.isEmpty(e.getOriginalResult())) {
                e.setOriginalResult("/");
                e.setReportResult("/");
                e.setResultStatus(EmcBizConstant.ORDERTASKRESULT_STATUS_DONE);
            }
        });
        orderTaskResultService.getDao().update(orderTaskResultAllList, "originalResult", "reportResult", "resultStatus");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void undoAccept(RestJsonWrapperBean jsonWrapper) {
        List<EmcSampleBean> sampleList = jsonWrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcSampleBean> undoSampleList = getDao().selectListByOneColumnValues(sampleIdList, "ID", Arrays.asList("ID", "SOURCE", "SEPSAMPLETIME"));

        // 样品接受人和接收时间置为空
        undoSampleList.stream().filter(sample -> ObjectUtils.isEmpty(sample.getAcceptedDate())).forEach(sample -> {
            sample.setAcceptedUserId("");
            sample.setAccepteUserName("");
            sample.setAcceptedDate(null);
        });

        getDao().update(undoSampleList, "ACCEPTEDUSERID", "ACCEPTEUSERNAME", "ACCEPTEDDATE");


        // 样品接收提交之后将分析类型为分析的样品测试的状态置为logged
        SearchFilter filter = SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR);
        filter.match("TERMINATION", Constant.YES).filter(MatchPattern.DIFFER);
        List<EmcOrderBean> orderList = orderService.selectListByFilter(filter);

        if (!orderList.isEmpty()) {
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
            // 样品表更新接收人和接收时间
            EmcOrderBean order = new EmcOrderBean();
            order.setAcceptedUserId("");
            order.setAccepteUserName("");
            order.setAcceptedDate(null);
            orderService.getDao().updateByIds(order, orderIdList, "ACCEPTEDUSERID", "ACCEPTEUSERNAME", "ACCEPTEDDATE");
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(
                    SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR));
            List<EmcOrderTaskBean> analysisOrderTaskList = orderTaskList.stream().filter(a -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(a.getAnalysisType())).collect(Collectors.toList());

            // 删除留样管理数据
            List<EmcOrderRetainBean> orderRetainDeleteList = orderRetainService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
            orderRetainService.getDao().deleteBy(orderRetainDeleteList);

            if (!analysisOrderTaskList.isEmpty()) {

                EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();

                updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED);

                orderTaskService.getDao().updateByIds(updateOrderTask, analysisOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList()), "STATUS");
            }
            // 判断更新的样品测试对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为preLogged
            updateReportStatus(sampleIdList, orderList, orderTaskList);

            // 插入样品测试操作记录表
            List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList()), "ORDERID");
            orderTaskLogService.insert(allOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_SAMPLE_ACCEPT, "");

            // 更新项目节点
            List<Long> projIdList = orderList.stream().map(EmcOrderBean::getProjId).distinct().collect(Collectors.toList());
            ProjectNodeUtils.updateNodes(projIdList);
        }
        // 样品接收提交插入审核记录
        insertAuditRecordBySample(sampleIdList, "PROCESS_ACCEPT", "回退", null);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void syncAssignDate(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        //找到采样单下的样品及样品测试，找到类型为采样，状态未done的测试，赋予他们指派时间
        String assignDate = sampleList.get(0).getExt$Item("assigndate");
        Long sampleId = sampleList.get(0).getId();
        if (assignDate == null || assignDate == "") {
            return;
        }
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(sampleId, "sampleId");
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderTaskBean> orderTaskUnFinishSampleList = orderTaskList.stream().filter(ot -> StringUtils.equals("logged", ot.getStatus())).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskAddTestSampleList = orderTaskUnFinishSampleList.stream().filter(ot -> !StringUtils.equals("1", ot.getExt$Item("addteststatus"))).collect(Collectors.toList());
        if (orderTaskAddTestSampleList.isEmpty()) {
            return;
        }
        orderTaskAddTestSampleList.forEach(ot -> {
            ot.setExt$Item("assigndate", assignDate);
        });
        orderTaskService.getDao().update(orderTaskAddTestSampleList, "assigndate");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void confirmAddTest(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        //找到采样单下的样品及样品测试，找到类型为采样，状态未done的测试，赋予他们指派时间
        List<Long> sampleIdList = sampleList.stream().map(s -> s.getId()).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "sampleId");
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderTaskBean> orderTaskUnFinishSampleList = orderTaskList.stream().filter(ot -> StringUtils.equals("logged", ot.getStatus())).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskAddTestSampleList = orderTaskUnFinishSampleList.stream().filter(ot -> !StringUtils.equals("1", ot.getExt$Item("addteststatus"))).collect(Collectors.toList());
        if (orderTaskAddTestSampleList.isEmpty()) {
            return;
        }
        orderTaskAddTestSampleList.forEach(ot -> {
            ot.setExt$Item("addTestStatus", "2");
        });
        orderTaskService.getDao().update(orderTaskAddTestSampleList, "addTestStatus");
    }

    @Override
    @Transactional
    public String passReminds(RestJsonWrapperBean wrapper) {
        String str = "";
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = sampleList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR));
        // 过滤终止的样品
        List<EmcOrderBean> normalOrderList = orderList.stream().filter(f -> !Constant.YES.equals(f.getTermination())).collect(Collectors.toList());
        List<Long> orderIdList = normalOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ));

        List<String> orderNeedOilFilmList = new ArrayList<>();
        for (EmcOrderBean o : normalOrderList) {
            List<EmcOrderTaskBean> filterOrderTaskList = orderTaskList.stream().filter(a -> ObjectUtils.equals(o.getId(), a.getOrderId())).collect(Collectors.toList());
            //如果样品含有颗粒物需判断是否有滤筒信息
            //单样品下的测试信息
            List<EmcOrderTaskBean> filterSingleOrderTaskList = filterOrderTaskList.stream().filter(ok -> ObjectUtils.equals(ok.getOrderId(), o.getId())).collect(Collectors.toList());
            List<EmcOrderTaskBean> filterSingleOrderTaskMatchList = filterSingleOrderTaskList.stream()
                    .filter(ok -> ok.getOrderCategory().contains(PfConstant.ORDERCATEGORY_GAS))
                    .filter(ok -> ok.getTestName().contains(PfConstant.PARTICULATE_MATTER))
                    .collect(Collectors.toList());
            //如果样品的类型是气的，且测试名称包含颗粒物(那么样品需要滤筒信息)
            if (!filterSingleOrderTaskMatchList.isEmpty()) {
                //检查样品的滤筒信息是否填写
                if (StringUtils.isEmpty(o.getExt$Item("oilfilmno"))) {
                    orderNeedOilFilmList.add(o.getOrderNo());
                }
            }

        }
        if (!orderNeedOilFilmList.isEmpty()) {
            String orderNoStr = StringUtils.join(orderNeedOilFilmList, ",");
            String message = I18nHelper.getMessage("EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.NOOILFILMNO", orderNoStr);
            return message;
        }
        return str;
    }

    @Override
    public EmcSampleBean selectSampleRecordDetail(RestJsonWrapperBean wrapper) {
        String orderId = wrapper.getParamValue("orderId");
        if (!ObjectUtils.isEmpty(orderId)) {
            EmcOrderBean order = orderService.selectByIdIfPresent(NumberUtils.parseLong(orderId));
            if (!ObjectUtils.isEmpty(order)) {
                // 判断是否是实验室质控
                String qcCategoryCode = order.getQcCategoryCode();
                if (!ObjectUtils.equals(EmcConstant.N_A, qcCategoryCode)) {
                    EmcQcCategoryBean qcCategoryBean = qcCategoryService.getDao().selectListByOneColumnValue(qcCategoryCode, "qcCategoryId").get(0);
                    if (!ObjectUtils.isEmpty(qcCategoryBean) && ObjectUtils.equals(EmcConstant.QCCATEGORY_USECATEGORY_LAB, qcCategoryBean.getUseCategory())) {
                        throw new AuthenticationServiceException("所选样品为实验室质控【" + qcCategoryBean.getQcName() + "】,无采样单！");
                    }
                }

                // 判断是否是采样任务
                Long projId = order.getProjId();
                String sample = projectService.selectColumnById(projId, "sample");
                if (ObjectUtils.equals(sample, Constant.NO)) {
                    throw new AuthenticationServiceException("所选样品为送样任务,无采样单！");
                }

                Long sampleId = order.getSampleId();
                if (ObjectUtils.isEmpty(order)) {
                    throw new AuthenticationServiceException("所选样品的采样单号为空！");
                }
                return selectByIdIfPresent(sampleId);

            }
        }
        return null;
    }

    public String exportOriginalRecordExcelZip(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> selectedSampleList = wrapper.parse(EmcSampleBean.class);
        if (selectedSampleList.isEmpty()) {
            return "";
        }
        List sampleIdList = selectedSampleList.stream().map(EmcSampleBean::getId).filter(sampleId -> !ObjectUtils.isEmpty((Object)sampleId)).distinct().collect(Collectors.toList());
        List<EmcSampleBean> sampleList = this.getDao().selectListByIds(sampleIdList, new Order[0]);
        if (sampleList.isEmpty()) {
            return "";
        }
        List<Long> reportConfigLineIdList = sampleList.stream().map(EmcSampleBean::getReportConfigLineId).filter(reportConfigLineId -> !StringUtils.isEmpty((String)reportConfigLineId)).map(NumberUtils::parseLong).distinct().collect(Collectors.toList());
        List<CoreReportConfigLineBean> configLineList = this.coreReportConfigLineService.selectListByIds(reportConfigLineIdList);
        if (configLineList.isEmpty()) {
            return "";
        }
        String reportingServiceServer = ApplicationContextHelper.getConstantValue((String)"REPORTING_SERVICE_SERVER_URL");
        String fineBaseUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-server.url");
        ArrayList<Path> exportPathList = new ArrayList<Path>();
        try {
            for (EmcSampleBean sample : sampleList) {
                if (StringUtils.isEmpty((String)sample.getReportConfigLineId())) continue;
                Long reportConfigLineId2 = NumberUtils.parseLong((Object)sample.getReportConfigLineId());
                Optional<CoreReportConfigLineBean> configLineOptional = configLineList.stream().filter(configLine -> ObjectUtils.equals((Object)configLine.getId(), (Object)reportConfigLineId2)).findFirst();
                if (!configLineOptional.isPresent()) continue;
                CoreReportConfigLineBean configLine2 = configLineOptional.get();
                String downloadUrl = null;
                if ("reportingService".equals(configLine2.getReportCategory())) {
                    downloadUrl = ReportingServiceUtils.getDownloadUrl(reportingServiceServer, configLine2, (Object)sample, "EXCEL");
                } else if ("fanruan".equals(configLine2.getReportCategory())) {
                    List<String> configReportLineIdList = Collections.singletonList(this.coreReportConfigLineService.getDao().getTable() + "$" + configLine2.getId());
                    List configFileList = this.coreFileService.getDao().selectListByOneColumnValues(configReportLineIdList, "TARGETID", new Order[0]);
                    Optional configFileOptional = configFileList.stream().findFirst();
                    if (configFileOptional.isPresent()) {
                        downloadUrl = this.buildFanruanOriginalRecordExcelUrl(fineBaseUrl, (CoreFileBean)configFileOptional.get(), configLine2, sample);
                    }
                }
                if (StringUtils.isEmpty((String)downloadUrl)) continue;
                String bizNo = StringUtils.isEmpty((String)sample.getSampleNo()) ? sample.getId() + "" : sample.getSampleNo();
                exportPathList.add(this.downloadToTempPath(downloadUrl, "\u91c7\u6837\u539f\u59cb\u8bb0\u5f55_" + bizNo + ".xls"));
            }
            if (exportPathList.isEmpty()) {
                return "";
            }
            FilePathDTO filePath = FilePathDTO.of((String)FileScope.temp.name(), (LocalDateTime)LocalDateTime.now(), (String)StringUtils.randomUUID((int)16), (String)"\u91c7\u6837\u539f\u59cb\u8bb0\u5f55\u6279\u91cf\u5bfc\u51fa.zip");
            ArchiveUtils.zip((Path)FilePathManager.getLocalPath((FilePathDTO)filePath), (Path[])exportPathList.toArray(new Path[0]));
            CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
            fileManager.upload(filePath, FilePathManager.getLocalPath((FilePathDTO)filePath));
            return fileManager.getDownloadUrl(filePath);
        }
        catch (Exception e) {
            throw new FileException((Throwable)e);
        }
    }

    private Path downloadToTempPath(String url, String fileName) {
        FilePathDTO filePath = FilePathDTO.of((String)FileScope.temp.name(), (LocalDateTime)LocalDateTime.now(), (String)StringUtils.randomUUID((int)16), (String)fileName);
        try {
            Files.createDirectories(FilePathManager.getLocalDirPath((FilePathDTO)filePath), new FileAttribute[0]);
        }
        catch (IOException e) {
            throw new FileException((Throwable)e);
        }
        HttpClientManager.getInstance().download(url, FilePathManager.getLocalPath((FilePathDTO)filePath).toFile());
        return FilePathManager.getLocalPath((FilePathDTO)filePath);
    }

    private String buildFanruanOriginalRecordExcelUrl(String fineBaseUrl, CoreFileBean configFile, CoreReportConfigLineBean configLine, EmcSampleBean source) {
        if (StringUtils.isEmpty((String)fineBaseUrl) || configFile == null) {
            return null;
        }
        if (StringUtils.isEmpty((String)configFile.getName())) {
            return null;
        }
        StringBuilder reportUrl = new StringBuilder();
        try {
            String fileName = configLine.getReportConfigLineNo() + "/" + configFile.getName();
            reportUrl.append(fineBaseUrl).append("/decision/view/report?viewlet=").append(URLEncoder.encode(fileName, "UTF-8")).append("&format=excel");
            String paramStr = this.parseReportParam(source, configLine.getParam());
            if (!StringUtils.isEmpty((String)paramStr)) {
                reportUrl.append("&").append(paramStr);
            }
            return reportUrl.toString();
        }
        catch (UnsupportedEncodingException e) {
            throw new FileException((Throwable)e);
        }
    }
    @Override
    @Transactional
    public void moveTask(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> wrapperOrderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderContainerBean> wrapperOrderContainerList = wrapper.parse(EmcOrderContainerBean.class);
        List<Long> orderIdList = wrapperOrderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderContainerBean> allOrderContainerList = orderContainerService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", new Order[0]);
        for (EmcOrderBean order : wrapperOrderList) {
            List<EmcOrderContainerBean> currentOrderContainerList = allOrderContainerList.stream()
                    .filter(container -> ObjectUtils.equals(order.getId(), container.getOrderId()))
                    .collect(Collectors.toList());
            if (currentOrderContainerList.size() == wrapperOrderContainerList.size()) {
                throw new InvalidDataException("不能移动样品下所有测试");
            }
        }

        List<Long> wrapperOrderContainerIdList = wrapperOrderContainerList.stream().map(EmcOrderContainerBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> wrapperOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(wrapperOrderContainerIdList, "ORDERCONTAINERID", new Order[0]);
        List<Long> testMethodIdList = wrapperOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        MapperParameter mapperParameter = new MapperParameter();
        mapperParameter.setFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR)
                .match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskBean> orderTaskList = orderTaskDao.selectOrderTaskList(mapperParameter);
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID", new Order[0]);
        List<Long> orderContainerIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId).distinct().collect(Collectors.toList());
        List<EmcOrderContainerBean> orderContainerList = allOrderContainerList.stream()
                .filter(container -> orderContainerIdList.contains(container.getId())).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.selectListByFilter(
                SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR)
                        .match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR), new Order[0]);

        Long folderId = wrapperOrderList.get(0).getFolderId();
        EmcFolderBean folder = folderService.selectById(folderId);
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(folderId, "FOLDERID", new Order[0]);
        String maxOrderNo = orderList.stream().max(Comparator.comparing(EmcOrderBean::getOrderNo))
                .map(EmcOrderBean::getOrderNo).orElse(null);
        int orderIndex = NumberUtils.parseInt(maxOrderNo.substring(folder.getFolderNo().length(), folder.getFolderNo().length() + 3));

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>();
        List<EmcOrderContainerBean> deleteOrderContainerList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcOrderTaskResultBean> deleteOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>();

        for (EmcOrderBean order : wrapperOrderList) {
            EmcOrderBean newOrder = new EmcOrderBean();
            BeanUtils.copyProperties(order, newOrder);
            String newOrderNo = folder.getFolderNo() + StringUtils.leftPad(String.valueOf(++orderIndex), 3);
            Long newOrderId = ApplicationContextHelper.getNextIdentity();
            newOrder.setId(newOrderId);
            newOrder.setOrderNo(newOrderNo);
            newOrder.setLabelNo(newOrderNo);
            newOrderList.add(newOrder);

            List<EmcOrderContainerBean> currentOrderContainerList = orderContainerList.stream()
                    .filter(container -> ObjectUtils.equals(order.getId(), container.getOrderId())).collect(Collectors.toList());
            deleteOrderContainerList.addAll(currentOrderContainerList);
            List<EmcOrderTaskBean> currentOrderTaskList = orderTaskList.stream()
                    .filter(task -> ObjectUtils.equals(order.getId(), task.getOrderId())).collect(Collectors.toList());
            deleteOrderTaskList.addAll(currentOrderTaskList);
            List<EmcOrderTaskResultBean> currentOrderTaskResultList = orderTaskResultList.stream()
                    .filter(result -> ObjectUtils.equals(order.getId(), result.getOrderId())).collect(Collectors.toList());
            deleteOrderTaskResultList.addAll(currentOrderTaskResultList);
            List<EmcOrderItemGroupBean> currentOrderItemGroupList = orderItemGroupList.stream()
                    .filter(group -> ObjectUtils.equals(order.getId(), group.getOrderId())).collect(Collectors.toList());
            deleteOrderItemGroupList.addAll(currentOrderItemGroupList);
            orderService.copyOrderItemGroup(currentOrderItemGroupList, order, newOrderId, newOrderItemGroupList);

            for (EmcOrderTaskBean orderTask : currentOrderTaskList) {
                EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                BeanUtils.copyProperties(orderTask, newOrderTask);
                Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                newOrderTask.setId(newOrderTaskId);
                newOrderTask.setOrderId(newOrderId);
                newOrderTask.setEquiptAcquNo(newOrderNo);
                newOrderTaskList.add(newOrderTask);
                List<EmcOrderTaskResultBean> currentTaskResultList = currentOrderTaskResultList.stream()
                        .filter(result -> ObjectUtils.equals(orderTask.getId(), result.getOrderTaskId())).collect(Collectors.toList());
                for (EmcOrderTaskResultBean orderTaskResult : currentTaskResultList) {
                    EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                    BeanUtils.copyProperties(orderTaskResult, newOrderTaskResult);
                    newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                    newOrderTaskResult.setOrderId(newOrderId);
                    newOrderTaskResult.setOrderTaskId(newOrderTaskId);
                    newOrderTaskResult.setOrderNo(newOrderNo);
                    newOrderTaskResultList.add(newOrderTaskResult);
                }
            }
            orderContainerService.createOrderContainer(Collections.singletonList(newOrder), newOrderTaskList);
        }

        orderService.getDao().insert(newOrderList);
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskService.getDao().deleteBy(deleteOrderTaskList, new String[0]);
        orderContainerService.getDao().deleteBy(deleteOrderContainerList, new String[0]);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderTaskResultService.getDao().deleteBy(deleteOrderTaskResultList, new String[0]);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, new String[0]);
    }

    @Override
    public void equiptCollect(RestJsonWrapperBean wrapper) {
        List<Long> fileIdList = wrapper.parse(CoreFileBean.class).stream().map(CoreFileBean::getId).collect(Collectors.toList());
        List<CoreFileBean> fileList = this.coreFileService.getDao().selectListByIds(fileIdList, new Order[0]);
        String equiptDirString = ApplicationContextHelper.getEnvironment().getProperty("sunway.equipt.acq.scene.file.local.path");
        if (StringUtils.isEmpty(equiptDirString)) {
            return;
        }
        fileList.forEach(file -> {
            File inputFile = CoreFileUtils.getLocalPath(file).toFile();
            File newFile = new File(equiptDirString, file.getName());
            try (java.io.FileInputStream fis = new java.io.FileInputStream(inputFile)) {
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                if (newFile.exists()) {
                    newFile.delete();
                }
                FileUtils.write(newFile, fis);
            } catch (Exception e) {
                throw new com.sunwayworld.framework.exception.FileException(e);
            }
        });
    }

    @Override
    @Transactional
    public void undoHandSample(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        if (sampleList.stream().anyMatch(e -> "2".equals(e.getAcceptStatus()))) {
            throw new InvalidDataException("采样单已接收，无法撤回");
        }
        sampleList.forEach(sample -> {
            sample.setHandStatus("draft");
            sample.setExt$Item("RECEIVEDATE", null);
            sample.setAcceptedDate(null);
            sample.setAcceptedTime(null);
        });
        this.getDao().update(sampleList, "HANDSTATUS", "RECEIVEDATE", "ACCEPTEDDATE", "ACCEPTEDTIME");
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = this.orderService.getDao().selectListByOneColumnValues(sampleIdList, "SAMPLEID", new Order[0]);
        List<EmcOrderTaskBean> allOrderTaskList = this.orderTaskService.getDao().selectListByOneColumnValues(
                orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList()), "ORDERID", new Order[0]);
        List<EmcOrderTaskBean> analysisTasks = allOrderTaskList.stream()
                .filter(task -> "analysis".equals(task.getAnalysisType())).collect(Collectors.toList());
        if (!analysisTasks.isEmpty()) {
            EmcOrderTaskBean updateTask = new EmcOrderTaskBean();
            updateTask.setStatus("draft");
            this.orderTaskService.getDao().updateByIds(updateTask,
                    analysisTasks.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList()), "STATUS");
        }
        List<Long> containerIdList = allOrderTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId)
                .filter(id -> !ObjectUtils.isEmpty(id)).distinct().collect(Collectors.toList());
        List<EmcOrderContainerBean> updateContainerList = new ArrayList<>();
        containerIdList.forEach(id -> {
            EmcOrderContainerBean container = new EmcOrderContainerBean();
            container.setId(id);
            container.setDeliveryId(null);
            updateContainerList.add(container);
        });
        this.orderContainerService.getDao().update(updateContainerList, "deliveryId");
    }

    @Override
    @Transactional
    public void withDraw(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        sampleList.forEach(this::withDraw);
    }

    @Transactional
    public void withDraw(EmcSampleBean sample) {
        Long projId = sample.getProjId();
        List<?> reportList = this.reportService.getDao().selectListByOneColumnValue(projId, "projId", new Order[0]);
        if (reportList.stream().anyMatch(report -> !"draft".equals(((com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean) report).getStatus()))) {
            throw new InvalidDataException("已出报告，无法撤回！");
        }
        List<Long> sampleIdList = Collections.singletonList(sample.getId());
        String comment = "撤回";
        this.undo(sampleIdList, comment);
        this.startProcess(sampleIdList, comment);
        this.completeTask(sampleIdList, comment);
        EmcProjectBean project = new EmcProjectBean();
        project.setId(projId);
        project.setStatus("auditFinished");
        this.projectService.getDao().update(project, "status");
        EmcSampleBean updateSample = new EmcSampleBean();
        updateSample.setId(sample.getId());
        updateSample.setStatus("audit");
        this.getDao().update(updateSample, "status");
        List<EmcOrderTaskBean> taskList = this.orderTaskService.selectListByFilter(
                SearchFilter.instance().match("sampleid", sample.getId()).filter(MatchPattern.SEQ)
                        .match("AnalysisType", "sampling").filter(MatchPattern.SEQ), new Order[0]);
        taskList.forEach(task -> task.setStatus("audit"));
        this.orderTaskService.getDao().update(taskList, "status");
        ProjectNodeUtils.updateNodes(Collections.singletonList(projId));
        EmcAuditRecordBean audit = new EmcAuditRecordBean();
        audit.setId(ApplicationContextHelper.getNextIdentity());
        audit.setProjId(sample.getProjId());
        audit.setProjNo(sample.getProjNo());
        audit.setSampleId(sample.getId());
        audit.setSampleNo(sample.getSampleNo());
        audit.setComments(comment);
        audit.setLogDesc("采样单:" + sample.getSampleNo());
        audit.setBizCategory("sample");
        audit.setStepCode("3");
        audit.setAuditCategory("采样管理");
        audit.setTaskName("报告编制");
        audit.setOperateCategory(comment);
        audit.setNextTaskName("采样审核");
        this.auditRecordService.getDao().insert(audit);
    }

}
