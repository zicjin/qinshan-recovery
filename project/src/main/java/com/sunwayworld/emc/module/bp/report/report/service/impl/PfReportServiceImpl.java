package com.sunwayworld.emc.module.bp.report.report.service.impl;

import com.gexin.fastjson.JSON;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.TextSelection;
import com.spire.doc.fields.TextRange;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateService;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.QRCodeGenerator;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.utils.FileSealUtils;
import com.sunwayworld.emc.framework.utils.SpireOfficeUtil;
import com.sunwayworld.emc.framework.word.JSTIeSealHelper;
import com.sunwayworld.emc.framework.word.Word2024Utils;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.archive.service.EmcArchiveCategoryService;
import com.sunwayworld.emc.module.bp.archive.service.PfArchiveFileService;
import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.project.service.PfProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportLineBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportRecordDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO.MergedColumn;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO.MergedRow;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportLineService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportResultService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.bp.report.report.service.PfReportService;
import com.sunwayworld.emc.module.bp.report.report.template.impl.ComplexWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.impl.DioxinQcWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.impl.DioxinWordTemplate2;
import com.sunwayworld.emc.module.bp.report.report.template.impl.QualityControlWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.validator.EmcViewDocumentValidator;
import com.sunwayworld.emc.module.bp.report.report.validator.PfGenerateReportResultValidator;
import com.sunwayworld.emc.module.bp.report.report.validator.PfViewReportValidator;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcQcRelatedOrderService;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.erp.service.ErpReportService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyTmplService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitConvertBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitConvertService;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOpenService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaOrgService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class PfReportServiceImpl extends EmcReportServiceImpl implements PfReportService {
   DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
   @Autowired
   private EmcOaOrgService emcOaOrgService;
   @Autowired
   @Lazy
   private EmcProjectService emcProjectService;
   private static final Logger logger = LogManager.getLogger(PfReportServiceImpl.class);
   private static final double REPORT_WORD_MIN_INFLATE_RATIO = 0.005;
   private static final Object POI_ZIP_SECURE_FILE_LOCK = new Object();
   @Autowired
   @Lazy
   private PfProjectService projectService;
   @Autowired
   private EmcContractService contractService;
   @Autowired
   @Lazy
   private EmcFolderService folderService;
   @Autowired
   @Lazy
   private EmcSampleService sampleService;
   @Autowired
   @Lazy
   private EmcRunService runService;
   @Autowired
   @Lazy
   private EmcOrderService orderService;
   @Autowired
   private EmcItemGroupService itemGroupService;
   @Autowired
   private EmcGroupItemService groupItemService;
   @Autowired
   @Lazy
   private EmcOrderTaskService orderTaskService;
   @Autowired
   private EmcOrderTaskResultService orderTaskResultService;
   @Autowired
   private EmcReportResultService reportResultService;
   @Autowired
   private CoreFileService coreFileService;
   @Autowired
   private CoreFileManager fileManager;
   @Autowired
   private EmcPropertyDataService propertyDataService;
   @Autowired
   private EmcPropertyTmplService propertyTmplService;
   @Autowired
   private EmcPropertyFieldService propertyFieldService;
   @Autowired
   private EmcEquiptService equiptService;
   @Autowired
   private EmcMonitorTmplService monitorTmplService;
   @Autowired
   private EmcMonitorSchemeService monitorSchemeService;
   @Autowired
   private CoreBpmnCommentService bpmnCommentService;
   @Autowired
   private CoreOrgService coreOrgService;
   @Autowired
   private CoreUserService coreUserService;
   @Autowired
   private CoreCodeService coreCodeService;
   @Autowired
   private EmcSubcontractRecordService subcontractRecordService;
   @Autowired
   @Lazy
   private CrmOpportunityOpenService opportunityOpenService;
   @Autowired
   private EmcItemRevisionService itemRevisionService;
   @Autowired
   private EmcItemService itemService;
   @Autowired
   @Lazy
   private CrmOpportunityMainService opportunityMainService;
   @Autowired
   @Lazy
   private CrmOpportunityContractService opportunityContractService;
   @Autowired
   private PfRunTempHumidityService tempHumidityService;
   @Autowired
   private EmcReportLineService reportLineService;
   @Autowired
   private EmcAuditRecordService auditRecordService;
   @Autowired
   private EmcQcRelatedOrderService qcRelatedOrderService;
   @Autowired
   private EmcIndexCategoryService indexCategoryService;
   @Autowired
   private EmcQcCategoryService qcCategoryService;
   @Autowired
   private EmcEquiptPerCheckPlanService perCheckPlanService;
   @Autowired
   private EmcStockService stockService;
   @Autowired
   @Lazy
   private EmcTestService testService;
   @Autowired
   private EmcTestMethodService testMethodService;
   @Autowired
   @Lazy
   private EmcTestCapacityService testCapacityService;
   @Autowired
   private ErpReportService erpReportService;
   @Autowired
   private EmcReportSendInfoService reportSendInfoService;
   @Autowired
   private EmcUnitService unitService;
   @Autowired
   private EmcUnitConvertService unitConvertService;
   @Autowired
   private EmcItemQcIndexService itemQcIndexService;
   @Autowired
   private LimsReportTemplateService limsReportTemplateService;
   @Autowired
   @Lazy
   private PfArchiveFileService pfArchiveFileService;
   @Autowired
   @Lazy
   private EmcArchiveCategoryService emcArchiveCategoryService;

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      EmcReportBean report = (EmcReportBean)jsonWrapper.parseUnique(EmcReportBean.class);
      List<EmcReportBean> allReportList = this.selectListByFilter(
         SearchFilter.instance().match("PROJID", report.getProjId()).filter(MatchPattern.OR), new Order[0]
      );
      List<Long> allReportIdList = allReportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      List<Long> deleteReportResultIdList = this.reportResultService
         .getDao()
         .selectListByOneColumnValues(allReportIdList, "reportId", Collections.singletonList("ID"), new Order[0])
         .stream()
         .<Long>map(EmcReportResultBean::getId)
         .collect(Collectors.toList());
      if (!deleteReportResultIdList.isEmpty()) {
         this.reportResultService.getDao().deleteByIdList(deleteReportResultIdList);
      }

      if (!ObjectUtils.isEmpty(report) && !ObjectUtils.isEmpty(report.getProjId())) {
         List<EmcOrderTaskResultBean> orderTaskResultList = this.orderTaskResultService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("PROJID", report.getProjId())
                  .filter(MatchPattern.OR)
                  .match("REPORTDISPLAY", "1")
                  .filter(MatchPattern.SEQ)
                  .match("RESULTSTATUS", "Done")
                  .filter(MatchPattern.SEQ),
               new Order[0]
            );
         if (orderTaskResultList.isEmpty()) {
            return 0L;
         } else {
            List<EmcOrderBean> orderList = this.orderService
               .selectListByFilter(SearchFilter.instance().match("PROJID", report.getProjId()).filter(MatchPattern.OR), new Order[0]);
            Map<Long, EmcOrderBean> orderIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));
            List<Long> itemIdList = orderTaskResultList.stream().<Long>map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
            List<EmcItemRevisionBean> itemRevisionList = this.itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID", new Order[0]);
            List<EmcItemBean> itemList = this.itemService.getDao().selectListByIds(itemIdList, new Order[0]);
            List<EmcReportResultBean> reportResultList = new ArrayList<>();
            boolean existLampblack = orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), "油烟"));
            List<EmcOrderTaskResultBean> finalResultList = this.calcParallelAverageValue(orderTaskResultList, itemRevisionList, itemList);
            Map<Long, Long> taskReportResultIdMap = new HashMap<>();
            finalResultList.forEach(result -> {
               EmcReportResultBean reportResult = new EmcReportResultBean();
               reportResult.setId(ApplicationContextHelper.getNextIdentity());
               reportResult.setReportId(report.getId());
               reportResult.setDataType("sample");
               this.copyProperties(reportResult, orderIdMap.get(result.getOrderId()), result);
               reportResultList.add(reportResult);
               if (existLampblack) {
                  taskReportResultIdMap.put(result.getId(), reportResult.getId());
               }
            });
            boolean calcStatistic = "1".equals(report.getIfStatistics());
            if (calcStatistic) {
               Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
               totalTaskResultMap.forEach(
                  (key, resultList) -> {
                     resultList.forEach(result -> this.replaceNdHdValue(result, result.getReportResult()));
                     Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream()
                        .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                     dayResultListMap.forEach(
                        (day, dayResultList) -> {
                           List<String> dayResultValueList = dayResultList.stream()
                              .<String>map(EmcOrderTaskResultBean::getReportResult)
                              .collect(Collectors.toList());
                           dayResultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                              EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                              BeanUtils.copyProperties(dayResultList.get(0), totalResult, new String[0]);
                              totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));
                              this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                              EmcReportResultBean reportResult = new EmcReportResultBean();
                              reportResult.setId(ApplicationContextHelper.getNextIdentity());
                              reportResult.setReportId(report.getId());
                              reportResult.setDataType("dayaverage");
                              EmcOrderBean currentOrder = orderIdMap.get(totalResult.getOrderId());
                              this.copyProperties(reportResult, currentOrder, totalResult);
                              reportResultList.add(reportResult);
                           });
                        }
                     );
                  }
               );
            }

            List<Long> invalidLampblackResultIdList = new ArrayList<>();
            if (calcStatistic) {
               Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
               totalTaskResultMap.forEach(
                  (key, resultList) -> {
                     resultList.forEach(result -> this.replaceNdHdValue(result, result.getReportResult()));
                     List<String> resultValueList = resultList.stream().<String>map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());
                     EmcOrderBean currentOrder = orderIdMap.get(resultList.get(0).getOrderId());
                     if (resultList.size() == 1) {
                        EmcReportResultBean reportResult = new EmcReportResultBean();
                        reportResult.setId(ApplicationContextHelper.getNextIdentity());
                        reportResult.setReportId(report.getId());
                        reportResult.setDataType("totalaverage");
                        this.copyProperties(reportResult, currentOrder, resultList.get(0));
                        reportResultList.add(reportResult);
                     } else if (StringUtils.contains(currentOrder.getOrderCategory(), "油烟")) {
                        Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream()
                           .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                        dayResultListMap.forEach((cycle, dayResultList) -> {
                           String lampblackAvgResult = this.calcLampblackTotalAvgResult((List<EmcOrderTaskResultBean>)dayResultList);
                           if (lampblackAvgResult != null) {
                              EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                              BeanUtils.copyProperties(dayResultList.get(0), totalResult, new String[0]);
                              totalResult.setOriginalResult(NumberUtils.formatNumber(NumberUtils.parseDouble(lampblackAvgResult)));
                              this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                              EmcReportResultBean reportResult = new EmcReportResultBean();
                              reportResult.setId(ApplicationContextHelper.getNextIdentity());
                              reportResult.setReportId(report.getId());
                              reportResult.setDataType("dayaverage");
                              this.copyProperties(reportResult, orderIdMap.get(dayResultList.get(0).getOrderId()), totalResult);
                              reportResultList.add(reportResult);
                           }
                        });
                        resultList.stream().filter(r -> "1".equals(r.getExt$Item("invalidresult"))).forEach(r -> invalidLampblackResultIdList.add(r.getId()));
                     } else {
                        resultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                           EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                           BeanUtils.copyProperties(resultList.get(0), totalResult, new String[0]);
                           totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));
                           this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                           EmcReportResultBean reportResult = new EmcReportResultBean();
                           reportResult.setId(ApplicationContextHelper.getNextIdentity());
                           reportResult.setReportId(report.getId());
                           reportResult.setDataType("totalaverage");
                           this.copyProperties(reportResult, currentOrder, totalResult);
                           reportResultList.add(reportResult);
                        });
                     }
                  }
               );
            }

            if (!reportResultList.isEmpty()) {
               if (!invalidLampblackResultIdList.isEmpty()) {
                  List<Long> invalidReportResultIdList = new ArrayList<>();
                  invalidLampblackResultIdList.forEach(id -> invalidReportResultIdList.add(taskReportResultIdMap.get(id)));
                  reportResultList.stream()
                     .filter(result -> invalidReportResultIdList.contains(result.getId()))
                     .forEach(result -> result.setInvalidResult("1"));
               }

               this.reportResultService.getDao().insert(reportResultList);
            }

            return report.getId();
         }
      } else {
         return 0L;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   public void delete(RestJsonWrapperBean wrapper) {
      List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class);
      List<Long> reportIdList = reportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      this.getDao().deleteByIdList(reportIdList);
      List<EmcReportResultBean> deletedReportResultList = new ArrayList<>(reportIdList.size());
      List<EmcReportLineBean> deletedReportLineList = new ArrayList<>(reportIdList.size());
      reportIdList.forEach(reportId -> {
         EmcReportResultBean reportResult = new EmcReportResultBean();
         reportResult.setReportId(reportId);
         deletedReportResultList.add(reportResult);
         EmcReportLineBean reportLine = new EmcReportLineBean();
         reportLine.setReportId(reportId);
         deletedReportLineList.add(reportLine);
      });
      this.reportResultService.getDao().deleteBy(deletedReportResultList, new String[]{"reportId"});
      this.reportLineService.getDao().deleteBy(deletedReportLineList, new String[]{"reportId"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
      List<EmcReportBean> selectReportList = wrapper.parse(EmcReportBean.class);
      List<Long> selectReportIdList = selectReportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      selectReportList = this.getDao().selectListByIds(selectReportIdList, new Order[0]);
      List<Long> projectIdList = selectReportList.stream().<Long>map(EmcReportBean::getProjId).collect(Collectors.toList());
      List<EmcProjectBean> projectList = this.projectService.getDao().selectListByIds(projectIdList, new Order[0]);
      List<EmcReportBean> projectReportList = this.selectListByFilter(
         SearchFilter.instance().match("projId", projectIdList).filter(MatchPattern.OR), new Order[0]
      );
      List<EmcReportBean> validReportList = projectReportList.stream().filter(reportx -> !"draft".equals(reportx.getStatus())).collect(Collectors.toList());
      List<Long> reportIdList = validReportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      List<String> targetIds = new ArrayList<>();

      for (Long reportId : reportIdList) {
         String targetId = "T_EMC_REPORT$" + reportId;
         targetIds.add(targetId);
      }

      List<CoreFileBean> files = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance()
               .match("targetId", targetIds)
               .filter(MatchPattern.OR)
               .match("bizCategory", "wordReport")
               .filter(MatchPattern.SEQ)
               .match("fileExt", "docx")
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      if (CollectionUtils.isEmpty(files)) {
         throw new InvalidDataException("报告提交失败，请先上传报告docx的word报告文档");
      } else {
         CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
         parameter.setComment("提交");
         List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = this.startProcessBasic(reportIdList, parameter);
         List<EmcReportLineBean> reportLineList = this.reportLineService.getDao().selectListByOneColumnValues(reportIdList, "reportId", new Order[0]);
         List<Long> reportOrderTaskIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
         List<EmcOrderTaskBean> orderTaskList = this.orderTaskService.selectListByIds(reportOrderTaskIdList);
         List<EmcReportBean> distinctReportList = validReportList.stream()
            .collect(
               Collectors.collectingAndThen(
                  Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMonitorTmplId() + ";" + o.getProjId()))), ArrayList::new
               )
            );
         List<Long> deleteOrderTaskIdList = new ArrayList<>();
         distinctReportList.forEach(
            r -> {
               List<Long> orderTaskIdList = orderTaskList.stream()
                  .filter(
                     o -> ObjectUtils.equals(r.getProjId(), o.getProjId())
                           && ObjectUtils.equals(r.getMonitorTmplId(), o.getMonitorTmplId())
                           && !ObjectUtils.isEmpty(o.getSubcontractorId())
                  )
                  .map(o -> o.getId())
                  .collect(Collectors.toList());
               deleteOrderTaskIdList.addAll(orderTaskIdList);
            }
         );
         List<Long> deleteSubcontractRecordIdList = this.subcontractRecordService
            .getDao()
            .selectListByOneColumnValues(deleteOrderTaskIdList, "ORDERTASKID", new Order[0])
            .stream()
            .map(e -> e.getId())
            .collect(Collectors.toList());
         this.subcontractRecordService.getDao().deleteByIdList(deleteSubcontractRecordIdList);
         validReportList.forEach(reportx -> {
            if ("1".equals(reportx.getToArchivingFlag())) {
               reportx.setStatus("reportSeal");
            } else {
               reportx.setStatus("reportAudit");
            }
         });
         this.getDao().update(validReportList, new String[]{"STATUS"});
         List<EmcReportBean> toArchivingReportList = validReportList.stream().filter(e -> "1".equals(e.getToArchivingFlag())).collect(Collectors.toList());
         List<Long> toArchivingProjIdList = toArchivingReportList.stream().map(e -> e.getProjId()).distinct().collect(Collectors.toList());
         if (!toArchivingProjIdList.isEmpty()) {
            List<EmcProjectBean> updateProjectList = new ArrayList<>();

            for (Long projId : toArchivingProjIdList) {
               EmcProjectBean updateProject = new EmcProjectBean();
               updateProject.setId(projId);
               updateProject.setStatus("reportAudit");
               updateProject.setProjNode("报告发送");
               updateProject.setReportEditUserId(LocalContextHelper.getLoginUserId());
               updateProject.setReportEditUserName(LocalContextHelper.getLoginUserName());
               EmcProjectBean project = projectList.stream().filter(e -> ObjectUtils.equals(projId, e.getId())).findFirst().orElse(new EmcProjectBean());
               LocalDateTime firstIssueDateTime = project.getFirstIssueDateTime();
               if (firstIssueDateTime == null) {
                  firstIssueDateTime = LocalDateTime.now();
               }

               updateProject.setFirstIssueDateTime(firstIssueDateTime);
               updateProjectList.add(updateProject);
            }

            this.projectService
               .getDao()
               .update(updateProjectList, new String[]{"status", "reportEditUserId", "reportEditUserName", "projnode", "firstIssueDateTime"});
            List<EmcAuditRecordBean> auditRecordList = new ArrayList<>();

            for (EmcReportBean report : toArchivingReportList) {
               EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
               auditRecord.setId(ApplicationContextHelper.getNextIdentity());
               auditRecord.setProjId(report.getProjId());
               auditRecord.setProjNo(report.getExt$Item("projno"));
               auditRecord.setComments("提交");
               auditRecord.setLogDesc("项目:" + report.getExt$Item("projno"));
               auditRecord.setBizCategory("report");
               auditRecord.setStepCode("6");
               auditRecord.setAuditCategory("报告管理");
               auditRecord.setTaskName("报告编制");
               auditRecord.setOperateCategory("提交");
               auditRecord.setNextTaskName("报告发送");
               auditRecordList.add(auditRecord);
            }

            this.auditRecordService.getDao().insert(auditRecordList);
         }

         List<EmcReportBean> toAuditReportList = validReportList.stream().filter(e -> !"1".equals(e.getToArchivingFlag())).collect(Collectors.toList());
         List<Long> toAuditProjIdList = toAuditReportList.stream().map(e -> e.getProjId()).distinct().collect(Collectors.toList());
         if (!toAuditProjIdList.isEmpty()) {
            EmcProjectBean updateProject = new EmcProjectBean();
            updateProject.setStatus("reportAudit");
            updateProject.setProjNode("报告审核");
            updateProject.setReportEditUserId(LocalContextHelper.getLoginUserId());
            updateProject.setReportEditUserName(LocalContextHelper.getLoginUserName());
            this.projectService
               .getDao()
               .updateByIds(updateProject, toAuditProjIdList, new String[]{"status", "reportEditUserId", "reportEditUserName", "projnode"});
            this.insertAuditRecordByReport(toAuditReportList, "PROCESS_START", wrapper.getParamValue("bpmn_comment"), null);
         }

         for (EmcReportBean report : selectReportList) {
            this.setSignatureInfo(report);
         }

         return coreBpmnInstanceStatusDTOList;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
      List<EmcReportBean> selectReportList = wrapper.parse(EmcReportBean.class);
      List<Long> selectReportIdList = selectReportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      List<EmcReportBean> validReportList = this.selectListByFilter(
         SearchFilter.instance().match("id", selectReportIdList).filter(MatchPattern.OR), new Order[0]
      );
      List<Long> selectProjectIdList = validReportList.stream().<Long>map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
      List<EmcProjectBean> projectBeanList = this.projectService.selectListByIds(selectProjectIdList);
      List<Long> reportIdList = validReportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(wrapper.getParamValue("comment"));
      List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = this.completeTask(reportIdList, parameter);
      String statusCode = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
      if ("reportIssue".equals(statusCode)) {
         this.validateReportIssueSealId(validReportList);
      }

      List<EmcReportBean> updateReportList = new ArrayList<>();
      List<EmcProjectBean> updateProjectList = new ArrayList<>();
      validReportList.forEach(e -> {
         EmcReportBean updateReport = new EmcReportBean();
         EmcProjectBean updateProject = new EmcProjectBean();
         updateProject.setId(e.getProjId());
         updateReport.setId(e.getId());
         if ("reportCheck".equals(statusCode)) {
            updateReport.setStatus("reportAudit");
         } else if ("reportAudit".equals(statusCode)) {
            updateReport.setStatus("reportIssue");
            updateProject.setProjNode("报告签发");
            updateProjectList.add(updateProject);
         } else if ("reportIssue".equals(statusCode)) {
            updateReport.setStatus("reportPrint");
            updateProject.setExt$Item("reportissueuserid", LocalContextHelper.getLoginUserId());
            updateProject.setExt$Item("reportissueusername", LocalContextHelper.getLoginUserName());
            updateProject.setReportIssueTime(LocalDateTime.now());
            updateReport.setExt$Item("completedate", LocalDateTime.now().toString());
            updateProject.setProjNode("报告打印");
            updateProjectList.add(updateProject);
         } else if ("reportPrint".equals(statusCode)) {
            updateReport.setStatus("reportSeal");
            updateProject.setProjNode("报告发送");
            EmcProjectBean project = projectBeanList.stream().filter(p -> ObjectUtils.equals(e.getProjId(), p.getId())).findFirst().orElse(null);
            LocalDateTime firstIssueDateTime = project.getFirstIssueDateTime();
            if (firstIssueDateTime == null) {
               firstIssueDateTime = LocalDateTime.now();
            }

            updateProject.setFirstIssueDateTime(firstIssueDateTime);
            updateProjectList.add(updateProject);
         } else if ("reportSeal".equals(statusCode)) {
            updateReport.setStatus("reportSend");
            updateProject.setProjNode("报告发送");
            updateProjectList.add(updateProject);
         } else if ("reportSend".equals(statusCode)) {
            updateReport.setStatus("reportArchiving");
            EmcProjectBean project = projectBeanList.stream().filter(p -> ObjectUtils.equals(e.getProjId(), p.getId())).findFirst().orElse(null);
            String deliverydate = project.getExt$Item("deliverydate");
            if (deliverydate == null) {
               deliverydate = LocalDateTime.now().toString();
            }

            updateProject.setExt$Item("deliverydate", deliverydate);
            updateProject.setProjNode("报告归档");
            updateProjectList.add(updateProject);
         } else if ("reportArchiving".equals(statusCode)) {
            updateReport.setStatus("done");
         }

         updateReportList.add(updateReport);
      });
      if ("reportIssue".equals(statusCode)) {
         this.getDao().update(updateReportList, new String[]{"STATUS", "COMPLETEDATE"});
      } else {
         this.getDao().update(updateReportList, new String[]{"STATUS"});
      }

      if (!CollectionUtils.isEmpty(updateProjectList)) {
         if ("reportIssue".equals(statusCode)) {
            this.projectService.getDao().update(updateProjectList, new String[]{"reportissueuserid", "reportissueusername", "reportissuetime", "projnode"});
         } else if ("reportPrint".equals(statusCode)) {
            this.projectService.getDao().update(updateProjectList, new String[]{"projnode", "FIRSTISSUEDATETIME"});
         } else if ("reportSend".equals(statusCode)) {
            this.projectService.getDao().update(updateProjectList, new String[]{"projnode", "deliverydate"});
         } else {
            this.projectService.getDao().update(updateProjectList, new String[]{"projnode"});
         }
      }

      if ("reportAudit".equals(statusCode) || "reportIssue".equals(statusCode)) {
         for (EmcReportBean report : validReportList) {
            this.setSignatureInfo(report);
         }
      }

      this.insertAuditRecordByReport(validReportList, "PROCESS_COMPLETE", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
      if ("reportSend".equals(statusCode)) {
         ExecutorHelper.submitAfterCommitTransaction(() -> {
            try {
               this.projectService.pushProjectToNc(projectBeanList);
            } catch (Exception var5x) {
               var5x.printStackTrace();
            }

            List<EmcArchiveCategoryBean> archiveCategoryBeanList = this.emcArchiveCategoryService.selectAll();

            for (EmcProjectBean projectBean : projectBeanList) {
               this.pfArchiveFileService.archive(archiveCategoryBeanList, projectBean, true, true);
            }
         });
      }

      return coreBpmnInstanceStatusDTOList;
   }

   private void wordToPdf(List<EmcReportBean> reportList) {
      for (EmcReportBean reportBean : reportList) {
         CoreFileBean reportWordCoreFileBean = (CoreFileBean)this.coreFileService
            .selectFirstByFilter(
               SearchFilter.instance()
                  .match("targetId", this.getDao().getTable() + "$" + reportBean.getId())
                  .filter(MatchPattern.SEQ)
                  .match("bizCategory", "wordReport")
                  .filter(MatchPattern.SEQ),
               new Order[0]
            );
         if (reportWordCoreFileBean != null) {
            File reportWordFile = CoreFileUtils.getLocalPath(reportWordCoreFileBean).toFile();
            FilePathDTO archivePdfPathDTO = CoreFileUtils.toFilePath(FileScope.temp, FileUtils.getFileBaseName(reportWordFile.getName()) + ".pdf");
            Path pdfFilePath = FilePathManager.getLocalPath(archivePdfPathDTO);
            FileUtils.makeDirs(pdfFilePath);
            SpireOfficeUtil.wordToPdf(reportWordFile, pdfFilePath.toFile());
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId(this.getDao().getTable() + "$" + reportBean.getId());
            coreFileBean.setScope(FileScope.secure.name());
            coreFileBean.setExt("wordReportPdf");
            this.coreFileService.upload(coreFileBean, pdfFilePath.toFile());
         }
      }
   }

   public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
      List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = this.rejectTaskBasic(wrapper);
      List<EmcReportBean> selectReportList = wrapper.parse(EmcReportBean.class);
      List<Long> selectReportIdList = selectReportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      List<EmcReportBean> validReportList = this.selectListByFilter(
         SearchFilter.instance().match("id", selectReportIdList).filter(MatchPattern.OR), new Order[0]
      );
      List<Long> selectProjectIdList = validReportList.stream().<Long>map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
      List<Long> reportIdList = validReportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
      parameter.setComment(wrapper.getParamValue("bpmn_comment"));
      List<Long> updateProjectIdList = new ArrayList<>();
      String targetStatusCode = (String)wrapper.getParamMap().get("bpmn_targetStatusCode");
      List<EmcReportBean> updateReportList = new ArrayList<>();
      validReportList.forEach(e -> {
         EmcReportBean updateReport = new EmcReportBean();
         updateReport.setId(e.getId());
         if (StringUtils.startsWithIgnoreCase(targetStatusCode, "draft")) {
            updateReport.setStatus("reportEdit");
            updateProjectIdList.add(e.getProjId());
         } else {
            updateReport.setStatus(targetStatusCode);
         }

         updateReport.setArchivedFlag("0");
         updateReportList.add(updateReport);
      });
      this.getDao().update(updateReportList, new String[]{"STATUS", "archivedFlag"});
      this.insertAuditRecordByReport(validReportList, "PROCESS_UNDO", wrapper.getParamValue("bpmn_comment"), coreBpmnInstanceStatusDTOList);
      if (!updateProjectIdList.isEmpty()) {
         EmcProjectBean updateProject = new EmcProjectBean();
         updateProject.setStatus("reportEdit");
         this.projectService.getDao().updateByIds(updateProject, updateProjectIdList, new String[]{"status"});
      }

      String statusCode = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
      if ("reportAudit".equals(statusCode)) {
         this.erpReportService.auditOrderReport(validReportList, true, parameter.getComment());
      }

      if (StringUtils.startsWithIgnoreCase(targetStatusCode, "draft")
         || StringUtils.startsWithIgnoreCase(targetStatusCode, "reportAudit")
         || StringUtils.startsWithIgnoreCase(targetStatusCode, "reportIssue")) {
         validReportList.forEach(e -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId(this.getDao().getTable() + "$" + e.getId());
            coreFileBean.setExt("wordReportPdf");
            this.coreFileService.getDao().deleteBy(coreFileBean, new String[]{"targetid", "ext"});
         });
      }

      return coreBpmnInstanceStatusDTOList;
   }

   public Page<EmcOrderTaskBean> selectUndoableAnalysisOrgDataTypePagination(RestJsonWrapperBean wrapper) {
      String reportId = wrapper.getFilterValue("reportId");
      List<EmcReportLineBean> reportLineList = this.reportLineService
         .getDao()
         .selectListByOneColumnValue(reportId, "reportId", Arrays.asList("id", "orderTaskId"), new Order[0]);
      List<Long> orderTaskIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
      List<List<Long>> naPartitionList = ListUtils.partition(orderTaskIdList, 200);
      List<EmcOrderTaskBean> orderTaskList = new ArrayList<>();
      IntStream.range(0, naPartitionList.size())
         .forEach(
            index -> {
               List<Long> curNaPartitionList = naPartitionList.get(index);
               List<EmcOrderTaskBean> taskList = this.orderTaskService
                  .getDao()
                  .selectListByOneColumnValues(curNaPartitionList, "id", Arrays.asList("id", "analysisOrgId", "analysisType", "analysisOrgName"), new Order[0]);
               orderTaskList.addAll(taskList);
            }
         );
      ArrayList<EmcOrderTaskBean> uniqueTaskList = orderTaskList.stream()
         .collect(
            Collectors.collectingAndThen(
               Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> String.join("$", task.getAnalysisType(), task.getAnalysisOrgId())))),
               ArrayList::new
            )
         );
      return new Pagination(uniqueTaskList);
   }

   private void createUndoSampleRecord(Long projId, String projNo, List<Long> sampleIdList, String undoReason) {
      List<EmcAuditRecordBean> recordList = new ArrayList<>(sampleIdList.size());
      List<EmcSampleBean> sampleList = this.sampleService
         .getDao()
         .selectListByOneColumnValues(sampleIdList, "id", Arrays.asList("id", "sampleNo"), new Order[0]);
      sampleList.forEach(sample -> {
         EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
         auditRecord.setId(ApplicationContextHelper.getNextIdentity());
         auditRecord.setProjId(projId);
         auditRecord.setProjNo(projNo);
         auditRecord.setComments(undoReason);
         auditRecord.setLogDesc("采样单:" + sample.getSampleNo());
         auditRecord.setBizCategory("sample");
         auditRecord.setStepCode("3");
         auditRecord.setAuditCategory("采样管理");
         auditRecord.setTaskName("报告管理");
         auditRecord.setOperateCategory("退回");
         auditRecord.setNextTaskName("主任审核-按采样单");
         recordList.add(auditRecord);
      });
      this.auditRecordService.getDao().insert(recordList);
   }

   private void createUndoRunRecord(List<String> runIdList, String undoReason) {
      List<EmcAuditRecordBean> recordList = new ArrayList<>(runIdList.size());
      List<EmcRunBean> runList = this.runService.getDao().selectListByOneColumnValues(runIdList, "id", Arrays.asList("id", "projNo"), new Order[0]);
      Set<String> projNoSet = new HashSet<>();
      runList.stream()
         .filter(run -> !StringUtils.isEmpty(run.getProjNo()))
         .forEach(run -> Stream.of(StringUtils.split(run.getProjNo(), ",")).forEach(projNoSet::add));
      List<EmcProjectBean> projectList = this.projectService
         .getDao()
         .selectListByOneColumnValues(new ArrayList<>(projNoSet), "projNo", Arrays.asList("id", "projNo"), new Order[0]);
      runList.forEach(
         run -> Stream.of(StringUtils.split(run.getProjNo(), ","))
               .forEach(projNo -> projectList.stream().filter(p -> StringUtils.equals(p.getProjNo(), projNo)).findFirst().ifPresent(p -> {
                     EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
                     auditRecord.setId(ApplicationContextHelper.getNextIdentity());
                     auditRecord.setProjId(p.getId());
                     auditRecord.setProjNo(p.getProjNo());
                     auditRecord.setComments(undoReason);
                     auditRecord.setLogDesc("批号:" + run.getId());
                     auditRecord.setBizCategory("run");
                     auditRecord.setStepCode("5");
                     auditRecord.setAuditCategory("结果录入");
                     auditRecord.setTaskName("报告管理");
                     auditRecord.setOperateCategory("退回");
                     auditRecord.setNextTaskName("分析审核");
                     recordList.add(auditRecord);
                  }))
      );
      this.auditRecordService.getDao().insert(recordList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   public void bizUndo(RestJsonWrapperBean wrapper) {
      String projectId = wrapper.getParamValue("projId");
      if (NumberUtils.isNumber(projectId)) {
         String projectNo = wrapper.getParamValue("projNo");
         String refuseReason = wrapper.getParamValue("refuseReason");
         EmcProjectBean projectJ = (EmcProjectBean)this.projectService.selectById(NumberUtils.parseLong(projectId));
         List<EmcOrderTaskBean> undoOrderTaskList = new ArrayList<>();
         List<EmcSampleBean> undoSampleList = wrapper.parse(EmcSampleBean.class);
         List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
         List<Long> undoSampleFolderIdList = new ArrayList<>();
         if (!undoSampleList.isEmpty()) {
            List<Long> undoSampleIdList = undoSampleList.stream().<Long>map(EmcSampleBean::getId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> undoSampleOrderList = this.orderService
               .getDao()
               .selectListByOneColumnValues(undoSampleIdList, "sampleId", Arrays.asList("id", "sampleId", "folderId"), new Order[0]);
            List<Long> undoSampleOrderIdList = undoSampleOrderList.stream().<Long>map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
            undoSampleFolderIdList.addAll(undoSampleOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList()));
            List<EmcOrderTaskBean> sampleOrderTaskList = this.orderTaskService
               .getDao()
               .selectListByOneColumnValues(
                  undoSampleOrderIdList,
                  "orderId",
                  Arrays.asList("id", "analysisOrgId", "analysisType", "runId", "orderId", "folderId", "status"),
                  new Order[0]
               );
            undoOrderTaskList.addAll(sampleOrderTaskList.stream().filter(task -> "sampling".equals(task.getAnalysisType())).collect(Collectors.toList()));
         }

         if (!runList.isEmpty()) {
            List<String> runIdList = runList.stream()
               .filter(r -> !StringUtils.isEmpty(r.getId()))
               .<String>map(EmcRunBean::getId)
               .distinct()
               .collect(Collectors.toList());
            undoOrderTaskList.addAll(
               this.orderTaskService
                  .getDao()
                  .selectListByOneColumnValues(
                     runIdList, "runId", Arrays.asList("id", "analysisOrgId", "analysisType", "runId", "orderId", "folderId", "status"), new Order[0]
                  )
            );
         }

         List<Long> undoOrderIdList = undoOrderTaskList.stream().<Long>map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
         List<EmcOrderBean> undoOrderList = this.orderService
            .getDao()
            .selectListByOneColumnValues(undoOrderIdList, "id", Arrays.asList("id", "termination", "sampleId"), new Order[0]);
         List<EmcOrderBean> validUndoOrderList = undoOrderList.stream()
            .filter(order -> StringUtils.equals("0", order.getTermination()))
            .collect(Collectors.toList());
         List<Long> validUndoOrderIdList = validUndoOrderList.stream().<Long>map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
         List<EmcOrderTaskBean> validUndoOrderTaskList = undoOrderTaskList.stream()
            .filter(task -> validUndoOrderIdList.contains(task.getOrderId()))
            .collect(Collectors.toList());
         List<EmcOrderTaskBean> analysisTaskList = validUndoOrderTaskList.stream()
            .filter(task -> "analysis".equals(task.getAnalysisType()) && "done".equals(task.getStatus()))
            .collect(Collectors.toList());
         List<EmcOrderTaskBean> samplingTaskList = validUndoOrderTaskList.stream()
            .filter(task -> "sampling".equals(task.getAnalysisType()) && "done".equals(task.getStatus()))
            .collect(Collectors.toList());
         analysisTaskList.forEach(task -> task.setStatus("audit"));
         samplingTaskList.forEach(task -> task.setStatus("audit"));
         List<EmcOrderTaskBean> updateTaskList = new ArrayList<>(analysisTaskList);
         updateTaskList.addAll(samplingTaskList);
         this.orderTaskService.getDao().update(updateTaskList, new String[]{"status"});
         boolean allAnalysis = validUndoOrderTaskList.stream().<String>map(EmcOrderTaskBean::getAnalysisType).distinct().allMatch("analysis"::equals);
         boolean existUndoSample = !undoSampleList.isEmpty();
         List<Long> samplingOrderIdList = samplingTaskList.stream().<Long>map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
         List<EmcOrderBean> samplingOrderList = validUndoOrderList.stream()
            .filter(order -> samplingOrderIdList.contains(order.getId()))
            .collect(Collectors.toList());
         List<Long> sampleIdList = allAnalysis && existUndoSample
            ? undoSampleList.stream().<Long>map(EmcSampleBean::getId).collect(Collectors.toList())
            : samplingOrderList.stream().<Long>map(EmcOrderBean::getSampleId).distinct().collect(Collectors.toList());
         if (!sampleIdList.isEmpty()) {
            CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
            parameter.setComment(refuseReason);
            this.sampleService.undo(sampleIdList, parameter);
            parameter.setComment("backData");
            this.sampleService.startProcess(sampleIdList, parameter);
            this.sampleService.completeTask(sampleIdList, parameter);
            List<String> tartgetIdList = new ArrayList<>();

            for (Long si : sampleIdList) {
               tartgetIdList.add(this.sampleService.getDao().getTable() + "$" + si);
            }

            List<CoreBpmnCommentBean> coreBpmnCommentBeanList = this.bpmnCommentService
               .selectListByFilter(
                  SearchFilter.instance().match("targetId", tartgetIdList).filter(MatchPattern.OR).match("comment", "backData").filter(MatchPattern.SEQ),
                  new Order[0]
               );
            this.bpmnCommentService.getDao().deleteBy(coreBpmnCommentBeanList, new String[0]);
            EmcSampleBean updateSample = new EmcSampleBean();
            updateSample.setStatus("audit");
            updateSample.setUndoFlag("1");
            updateSample.setUndoReason(refuseReason);
            this.sampleService.getDao().updateByIds(updateSample, sampleIdList, new String[]{"STATUS", "UNDOFLAG", "UNDOREASON"});
            CoreFileBean deleteFilter = new CoreFileBean();
            deleteFilter.setTargetId("T_EMC_PROJECT$" + projectId);
            deleteFilter.setExt("sampleRecordPdf");
            this.coreFileService.getDao().deleteBy(deleteFilter, new String[]{"targetId", "ext"});
            this.createUndoSampleRecord(NumberUtils.parseLong(projectId), projectNo, sampleIdList, refuseReason);
         }

         List<String> runIdList = analysisTaskList.stream().<String>map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
         if (!runIdList.isEmpty()) {
            EmcRunBean updateRun = new EmcRunBean();
            updateRun.setStatus("audit");
            updateRun.setAuditedUserId("");
            updateRun.setAuditedUserName("");
            updateRun.setAuditedDate(null);
            updateRun.setUndoReason(refuseReason);
            updateRun.setUndoFlag("1");
            this.runService
               .getDao()
               .updateByIds(updateRun, runIdList, new String[]{"STATUS", "auditedUserId", "auditedUserName", "auditedDate", "undoReason", "undoFlag"});
            CoreFileBean deleteFilter = new CoreFileBean();
            deleteFilter.setTargetId("T_EMC_PROJECT$" + projectId);
            deleteFilter.setExt("analysisRecordPdf");
            this.coreFileService.getDao().deleteBy(deleteFilter, new String[]{"targetId", "ext"});
            this.createUndoRunRecord(runIdList, refuseReason);
         }

         List<EmcOrderTaskBean> projOrderTaskList = this.orderTaskService.getDao().selectListByOneColumnValue(projectJ.getId(), "PROJID", new Order[0]);
         boolean onlyUndoSample = undoOrderTaskList.isEmpty() && existUndoSample;
         List<Long> folderIdList = onlyUndoSample
            ? undoSampleFolderIdList
            : undoOrderTaskList.stream().<Long>map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
         List<Long> folderIdForUpdateList = new ArrayList<>();
         folderIdList.forEach(fi -> folderIdForUpdateList.add(fi));
         EmcFolderBean updateFolder = new EmcFolderBean();
         updateFolder.setStatus("draw");
         this.folderService.getDao().updateByIds(updateFolder, folderIdForUpdateList, new String[]{"STATUS"});
         List<EmcReportBean> projectReportList = this.selectListByFilter(
            SearchFilter.instance().match("projId", projectId).filter(MatchPattern.SEQ).match("Status", "draft").filter(MatchPattern.DIFFER), new Order[0]
         );
         List<EmcReportResultBean> deleteReportResultList = new ArrayList<>();
         List<EmcReportLineBean> deleteReportLineList = new ArrayList<>();

         for (EmcReportBean p : projectReportList) {
            EmcReportResultBean deleteReportResult = new EmcReportResultBean();
            deleteReportResult.setReportId(p.getId());
            deleteReportResultList.add(deleteReportResult);
            EmcReportLineBean deleteReportLine = new EmcReportLineBean();
            deleteReportLine.setReportId(p.getId());
            deleteReportLineList.add(deleteReportLine);
         }

         this.getDao().deleteBy(projectReportList, new String[0]);
         this.reportResultService.getDao().deleteBy(deleteReportResultList, new String[]{"reportId"});
         this.reportLineService.getDao().deleteBy(deleteReportLineList, new String[]{"reportId"});
         List<EmcFolderBean> projectFolderList = this.folderService
            .getDao()
            .selectListByOneColumnValue(projectId, "projId", Arrays.asList("id", "status"), new Order[0]);
         boolean existDoneFolder = projectFolderList.stream().anyMatch(folder -> "done".equals(folder.getStatus()));
         EmcProjectBean updateProject = new EmcProjectBean();
         updateProject.setId(NumberUtils.parseLong(projectId));
         updateProject.setStatus("auditFinished");
         this.projectService.getDao().update(updateProject, new String[]{"status"});
         List<String> reportStatusList = Arrays.asList("reportEdit", "reportAudit", "reportIssue", "reportPrint", "reportSend", "reportArchiving");
         List<String> reportNodeList = Arrays.asList("报告编制", "报告审核", "报告签发", "报告打印", "报告发送", "报告归档");
         String reportStatus = "reportEdit";
         if (!projectReportList.isEmpty()) {
            reportStatus = projectReportList.get(0).getStatus();
         }

         int statusIndex = reportStatusList.indexOf(reportStatus);
         String taskName = statusIndex < 0 ? reportNodeList.get(0) : reportNodeList.get(statusIndex);
         EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
         auditRecord.setProjId(NumberUtils.parseLong(projectId));
         auditRecord.setProjNo(projectNo);
         auditRecord.setComments(refuseReason);
         auditRecord.setLogDesc("项目:" + projectNo);
         auditRecord.setBizCategory("report");
         auditRecord.setStepCode("6");
         auditRecord.setAuditCategory("报告管理");
         auditRecord.setTaskName(taskName);
         auditRecord.setOperateCategory("退回");
         if (!samplingTaskList.isEmpty()) {
            EmcAuditRecordBean insertAuditRecord = new EmcAuditRecordBean();
            BeanUtils.copyProperties(auditRecord, insertAuditRecord, new String[0]);
            insertAuditRecord.setNextTaskName("主任审核-按采样单");
            insertAuditRecord.setId(ApplicationContextHelper.getNextIdentity());
            this.auditRecordService.getDao().insert(insertAuditRecord);
         }

         if (!analysisTaskList.isEmpty()) {
            EmcAuditRecordBean insertAuditRecord = new EmcAuditRecordBean();
            BeanUtils.copyProperties(auditRecord, insertAuditRecord, new String[0]);
            insertAuditRecord.setNextTaskName("分析审核");
            insertAuditRecord.setId(ApplicationContextHelper.getNextIdentity());
            this.auditRecordService.getDao().insert(insertAuditRecord);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   public Long projectUndo(RestJsonWrapperBean wrapper) {
      EmcProjectBean project = (EmcProjectBean)wrapper.parseUnique(EmcProjectBean.class);
      Long projectId = project.getId();
      List<EmcOrderTaskBean> projectOrderTaskList = this.orderTaskService.getDao().selectListByOneColumnValue(projectId, "projId", new Order[0]);
      List<String> runIdList = projectOrderTaskList.stream()
         .filter(r -> !StringUtils.isEmpty(r.getRunId()))
         .map(e -> e.getRunId())
         .distinct()
         .collect(Collectors.toList());
      List<EmcOrderTaskBean> undoOrderTaskList = this.orderTaskService.getDao().selectListByOneColumnValues(runIdList, "runId", new Order[0]);
      List<Long> undoOrderIdList = undoOrderTaskList.stream().<Long>map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
      List<EmcOrderBean> undoOrderList = this.orderService.getDao().selectListByOneColumnValues(undoOrderIdList, "id", new Order[0]);
      List<EmcOrderBean> validUndoOrderList = undoOrderList.stream()
         .filter(order -> StringUtils.equals("0", order.getTermination()))
         .collect(Collectors.toList());
      List<Long> validUndoOrderIdList = validUndoOrderList.stream().<Long>map(EmcOrderBean::getId).collect(Collectors.toList());
      List<EmcOrderTaskBean> validUndoOrderTaskList = undoOrderTaskList.stream()
         .filter(task -> validUndoOrderIdList.contains(task.getOrderId()))
         .collect(Collectors.toList());
      List<EmcOrderTaskBean> analysisTaskList = validUndoOrderTaskList.stream()
         .filter(task -> "analysis".equals(task.getAnalysisType()))
         .collect(Collectors.toList());
      analysisTaskList.forEach(task -> task.setStatus("audit"));
      this.orderTaskService.getDao().update(analysisTaskList, new String[]{"status"});
      EmcRunBean updateRun = new EmcRunBean();
      updateRun.setStatus("audit");
      updateRun.setAuditedUserId("");
      updateRun.setAuditedUserName("");
      updateRun.setAuditedDate(null);
      this.runService.getDao().updateByIds(updateRun, runIdList, new String[]{"STATUS", "auditedUserId", "auditedUserName", "auditedDate"});
      List<Long> folderIdList = undoOrderTaskList.stream().<Long>map(EmcOrderTaskBean::getFolderId).distinct().collect(Collectors.toList());
      EmcFolderBean updateFolder = new EmcFolderBean();
      updateFolder.setStatus("draw");
      this.folderService.getDao().updateByIds(updateFolder, folderIdList, new String[]{"STATUS"});
      List<EmcReportBean> projReportList = this.selectListByFilter(
         SearchFilter.instance().match("projId", projectId).filter(MatchPattern.SEQ).match("monitorTmplId", null).filter(MatchPattern.SEQ), new Order[0]
      );
      List<Long> deleteReportIdList = projReportList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
      List<EmcReportResultBean> deleteReportResultList = new ArrayList<>();
      List<EmcReportLineBean> deleteReportLineList = new ArrayList<>();

      for (Long reportId : deleteReportIdList) {
         EmcReportResultBean deleteReportResult = new EmcReportResultBean();
         deleteReportResult.setReportId(reportId);
         deleteReportResultList.add(deleteReportResult);
         EmcReportLineBean deleteReportLine = new EmcReportLineBean();
         deleteReportLine.setReportId(reportId);
         deleteReportLineList.add(deleteReportLine);
      }

      this.getDao().deleteBy(projReportList, new String[]{"ID"});
      this.reportResultService.getDao().deleteBy(deleteReportResultList, new String[]{"reportId"});
      this.reportLineService.getDao().deleteBy(deleteReportLineList, new String[]{"reportId"});
      EmcProjectBean updateProject = new EmcProjectBean();
      updateProject.setId(NumberUtils.parseLong(projectId));
      updateProject.setStatus("auditFinished");
      this.projectService.getDao().update(updateProject, new String[]{"status"});
      EmcAuditRecordBean auditRecord = new EmcAuditRecordBean();
      auditRecord.setProjId(projectId);
      auditRecord.setProjNo(project.getProjNo());
      auditRecord.setLogDesc("项目:" + project.getProjNo());
      auditRecord.setBizCategory("report");
      auditRecord.setStepCode("6");
      auditRecord.setAuditCategory("报告管理");
      auditRecord.setTaskName("报告编制");
      auditRecord.setOperateCategory("退回");
      auditRecord.setNextTaskName("分析审核");
      auditRecord.setId(ApplicationContextHelper.getNextIdentity());
      this.auditRecordService.getDao().insert(auditRecord);
      return 1L;
   }

   @Transactional
   @ValidateDataWith(EmcViewDocumentValidator.class)
   public Long viewDocument(RestJsonWrapperBean wrapper) {
      EmcReportBean report = (EmcReportBean)wrapper.parseUnique(EmcReportBean.class);
      Long projectId = report.getProjId();
      List<EmcReportBean> reportList = this.getDao().selectListByOneColumnValue(projectId, "PROJID", new Order[0]);
      List<String> targetIdList = reportList.stream().map(r -> this.getDao().getTable() + "$" + r.getId()).collect(Collectors.toList());
      List<CoreFileBean> wordReportFileList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("bizCategory", "wordReport").filter(MatchPattern.EQ),
            new Order[0]
         );
      EmcProjectBean project = (EmcProjectBean)this.projectService
         .selectFirstByFilter(SearchFilter.instance().match("id", projectId).filter(MatchPattern.SEQ), new Order[0]);
      if (!wordReportFileList.isEmpty()) {
         List<CoreFileBean> updateCoreFileList = new ArrayList<>();
         wordReportFileList.forEach(e -> {
            CoreFileBean updateCoreFile = new CoreFileBean();
            updateCoreFile.setId(e.getId());
            updateCoreFile.setTargetId("X" + e.getTargetId());
            updateCoreFileList.add(updateCoreFile);
         });
         this.coreFileService.getDao().update(updateCoreFileList, new String[]{"TARGETID"});
         CoreFileBean wordTemplateConfigFile = wordReportFileList.get(0);
         CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
         fileManager.download(wordTemplateConfigFile);
         File templateFile = CoreFileUtils.getLocalPath(wordTemplateConfigFile).toFile();
         this.generateWordReport(templateFile, templateFile, report, project, true);
         List<CoreFileBean> newCoreFileList = this.saveWordReportCoreFile(wordTemplateConfigFile.getName(), templateFile, reportList, "wordReport");
         return newCoreFileList.get(0).getId();
      } else {
         Long reportConfigLineId = report.getReportConfigLineId();
         List<CoreFileBean> wordTemplateConfigFileList = this.coreFileService
            .getDao()
            .selectListByOneColumnValue("T_CORE_REPORT_CONFIG_LINE$" + reportConfigLineId, "targetId", new Order[0]);
         if (!wordTemplateConfigFileList.isEmpty()) {
            CoreFileBean wordTemplateConfigFile = wordTemplateConfigFileList.get(0);
            CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
            fileManager.download(wordTemplateConfigFile);
            File templateFile = CoreFileUtils.getLocalPath(wordTemplateConfigFile).toFile();
            String wordReportFileName = FileUtils.getFileBaseName(wordTemplateConfigFile.getName())
               + "-"
               + System.currentTimeMillis()
               + "."
               + FileUtils.getFileExtension(wordTemplateConfigFile.getName());
            File wordReportFile = FilePathManager.getLocalPath(
                  FilePathDTO.of(wordTemplateConfigFile.getScope(), "", wordTemplateConfigFile.getExt(), wordReportFileName)
               )
               .toFile();
            this.generateWordReport(templateFile, wordReportFile, report, project, false);
            List<CoreFileBean> newCoreFileList = this.saveWordReportCoreFile(wordReportFileName, wordReportFile, reportList, "wordReport");
            return newCoreFileList.get(0).getId();
         } else {
            return 0L;
         }
      }
   }

   private CoreCodeBean getContractCode(EmcProjectBean project) {
      CoreCodeBean coreCode = new CoreCodeBean();
      Optional.ofNullable(project.getExt$Item("contractid"))
         .ifPresent(
            c -> {
               List<Long> contractIdList = Arrays.stream(c.split(","))
                  .filter(NumberUtils::isNumber)
                  .map(s -> NumberUtils.parseLong(s.trim()))
                  .collect(Collectors.toList());
               List<EmcContractBean> contractList = this.contractService.getDao().selectListByIds(contractIdList, new Order[0]);
               if (!contractList.isEmpty()) {
                  EmcContractBean contract = contractList.get(0);
                  Optional.ofNullable(contract.getExt$Item("DETECTCATEGORY"))
                     .ifPresent(
                        d -> {
                           CoreCodeBean filter = new CoreCodeBean();
                           filter.setCodeCategoryId("detectCategory");
                           filter.setCode(d);
                           CoreCodeBean code = (CoreCodeBean)this.coreCodeService
                              .getDao()
                              .selectFirstIfPresent(filter, Arrays.asList("CODECATEGORYID", "CODE"), Collections.singletonList("CODENAME"), new Order[0]);
                           if (code != null) {
                              coreCode.setCodeName(code.getCodeName());
                           }
                        }
                     );
               }
            }
         );
      return coreCode;
   }

   private List<Long> parseMultiEquipt(List<String> equiptIdList) {
      List<String> parsedEquiptIdList = new ArrayList<>();
      equiptIdList.forEach(multiEquiptId -> {
         if (StringUtils.contains(multiEquiptId, ",")) {
            parsedEquiptIdList.addAll(Arrays.asList(StringUtils.split(multiEquiptId, ",")));
         } else {
            parsedEquiptIdList.add(multiEquiptId);
         }
      });
      return parsedEquiptIdList.stream().filter(NumberUtils::isNumber).distinct().<Long>map(NumberUtils::parseLong).collect(Collectors.toList());
   }

   public List<CoreFileBean> saveWordReportCoreFile(String wordReportFileName, File wordReportFile, List<EmcReportBean> reportList, String bizCategory) {
      List<CoreFileBean> insertCoreFileList = new ArrayList<>();
      CoreFileBean copyFile = new CoreFileBean();
      copyFile.setName(wordReportFileName);
      copyFile.setFileExt(FileUtils.getFileExtension(wordReportFileName));
      copyFile.setScope(FileScope.export.name());
      copyFile.setTimes(0L);
      copyFile.setDownloadable("1");
      copyFile.setCreatedTime(LocalDateTime.now());
      copyFile.setBizCategory(bizCategory);
      copyFile.setSize(wordReportFile.length());
      List<Long> fileIdList = ApplicationContextHelper.getNextIdentityList(reportList.size());
      IntStream.range(0, reportList.size()).forEach(i -> {
         EmcReportBean oldReport = reportList.get(i);
         CoreFileBean insertCoreFile = new CoreFileBean();
         BeanUtils.copyProperties(copyFile, insertCoreFile, new String[0]);
         Long fileId = fileIdList.get(i);
         insertCoreFile.setId(fileId);
         insertCoreFile.setTargetId(this.getDao().getTable() + "$" + oldReport.getId());
         insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(fileId + "$" + wordReportFileName));
         this.fileManager.upload(CoreFileUtils.toFilePath(insertCoreFile), wordReportFile);
         insertCoreFileList.add(insertCoreFile);
      });
      this.coreFileService.getDao().insert(insertCoreFileList);
      return insertCoreFileList;
   }

   private void validateReportIssueSealId(List<EmcReportBean> reportList) {
      boolean requireQys = reportList.stream().anyMatch(this::isQysIssueSignMode);
      if (requireQys) {
         CoreUserBean issuer = (CoreUserBean)this.coreUserService.selectById(LocalContextHelper.getLoginUserId());
         this.getIssueSealId(issuer);
      }
   }

   public void setSignatureInfo(EmcReportBean report) {
      CoreFileBean reportFile = (CoreFileBean)this.coreFileService
         .selectFirstByFilter(
            SearchFilter.instance()
               .match("targetId", this.getDao().getTable() + "$" + report.getId())
               .filter(MatchPattern.SEQ)
               .match("bizCategory", "wordReport")
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      if (reportFile != null) {
         CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
         fileManager.download(reportFile);
         File inputFile = CoreFileUtils.getLocalPath(reportFile).toFile();
         File outputFile = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(reportFile)).toFile();
         File wordTemp = FileUtils.createTempFile("docx");

         try (FileInputStream fis = new FileInputStream(inputFile)) {
            XWPFDocument document = this.createReportDocument(fis);
            Throwable var10 = null;

            try (FileOutputStream fos = new FileOutputStream(wordTemp)) {
               this.setSubmitInformation(document, report);
               document.write(fos);
            } catch (Throwable var61) {
               var10 = var61;
               throw var61;
            } finally {
               if (document != null) {
                  if (var10 != null) {
                     try {
                        document.close();
                     } catch (Throwable var57) {
                        var10.addSuppressed(var57);
                     }
                  } else {
                     document.close();
                  }
               }
            }
         } catch (IOException var65) {
            throw new FileException(var65);
         }

         this.changeFont(wordTemp, outputFile);
         this.coreFileService.upload(reportFile, outputFile);
         this.coreFileService.getDao().deleteBy(reportFile, new String[0]);
      }
   }

   private XWPFDocument createReportDocument(InputStream inputStream) throws IOException {
      synchronized (POI_ZIP_SECURE_FILE_LOCK) {
         double minInflateRatio = ZipSecureFile.getMinInflateRatio();

         XWPFDocument var5;
         try {
            if (minInflateRatio > 0.005) {
               ZipSecureFile.setMinInflateRatio(0.005);
            }

            var5 = new XWPFDocument(inputStream);
         } finally {
            ZipSecureFile.setMinInflateRatio(minInflateRatio);
         }

         return var5;
      }
   }

   private void setSubmitInformation(XWPFDocument document, EmcReportBean report) {
      List<CoreBpmnCommentBean> commentList = this.bpmnCommentService
         .selectListByFilter(
            SearchFilter.instance()
               .match("targetId", this.getDao().getTable() + "$" + report.getId())
               .filter(MatchPattern.EQ)
               .match("status", "REJECT")
               .filter(MatchPattern.DIFFER),
            new Order[0]
         );
      CoreBpmnCommentBean submitComment = commentList.stream()
         .filter(e -> ObjectUtils.equals("Draft", e.getStatusCode()))
         .sorted(Comparator.comparing((CoreBpmnCommentBean e) -> e.getCreatedTime()).reversed())
         .limit(1L)
         .findFirst()
         .orElse(null);
      CoreBpmnCommentBean auditComment = commentList.stream()
         .filter(e -> ObjectUtils.equals("reportAudit", e.getStatusCode()))
         .sorted(Comparator.comparing((CoreBpmnCommentBean e) -> e.getCreatedTime()).reversed())
         .limit(1L)
         .findFirst()
         .orElse(null);
      CoreBpmnCommentBean issueComment = commentList.stream()
         .filter(e -> ObjectUtils.equals("reportIssue", e.getStatusCode()))
         .sorted(Comparator.comparing((CoreBpmnCommentBean e) -> e.getCreatedTime()).reversed())
         .limit(1L)
         .findFirst()
         .orElse(null);
      List<XWPFTable> tableList = document.getTables();
      XWPFTable lastTable = tableList.isEmpty() ? null : tableList.get(tableList.size() - 1);
      if (submitComment != null) {
         this.addSignaturePicture(lastTable, 2, submitComment, null);
      }

      if (auditComment != null) {
         this.addSignaturePicture(lastTable, 6, auditComment, null);
      }

      if (issueComment != null) {
         boolean insertIssueSignaturePicture = this.isPictureIssueSignMode(report);
         if (report.getReportTemplateId() != null) {
            if (insertIssueSignaturePicture) {
               this.addSignaturePicture(lastTable, 10, issueComment, null);
            }

            LocalDateTime issueDate = issueComment.getCreatedTime();
            Word2024Utils.insertContentAndImageWithSizeToTable(lastTable, 13, null, issueDate.getYear() + "");
            Word2024Utils.insertContentAndImageWithSizeToTable(lastTable, 15, null, issueDate.getMonth().getValue() + "");
            Word2024Utils.insertContentAndImageWithSizeToTable(lastTable, 17, null, issueDate.getDayOfMonth() + "");
            XWPFTable secondTable = tableList.get(0);
            Word2024Utils.insertContentAndImageToTable(secondTable, 10, null, DateTimeUtils.formatLocalDateTime(issueDate, "yyyy年MM月dd日"), 16, "宋体 (中文正文)");
         } else if (!tableList.isEmpty() && tableList.size() > 1) {
            XWPFTable secondTable = tableList.get(1);
            XWPFTableRow row = !secondTable.getRows().isEmpty() && secondTable.getRows().size() > 4 ? secondTable.getRow(4) : null;
            XWPFTableCell cell = !row.getTableCells().isEmpty() && row.getTableCells().size() > 1 ? row.getCell(1) : null;
            XWPFParagraph xwpfParagraph = !cell.getParagraphs().isEmpty() && cell.getParagraphs().size() > 0
               ? (XWPFParagraph)cell.getParagraphs().get(0)
               : null;
            String text = xwpfParagraph.getText();
            if (text != null && text.contains("reportDate")) {
               if (insertIssueSignaturePicture) {
                  this.addSignaturePicture(lastTable, 10, issueComment, null);
               }

               LocalDateTime issueDate = issueComment.getCreatedTime();
               Word2024Utils.insertContentAndImageWithSizeToTable(lastTable, 13, null, issueDate.getYear() + "");
               Word2024Utils.insertContentAndImageWithSizeToTable(lastTable, 15, null, issueDate.getMonth().getValue() + "");
               Word2024Utils.insertContentAndImageWithSizeToTable(lastTable, 17, null, issueDate.getDayOfMonth() + "");
               Word2024Utils.insertContentAndImageToTable(secondTable, 10, null, DateTimeUtils.formatLocalDateTime(issueDate, "yyyy年MM月dd日"), 16, "宋体 (中文正文)");
            }
         }
      }
   }

   public void signIssuePdfIfRequired(CoreFileBean pdfCoreFile) {
      Long reportId = this.parseReportId(pdfCoreFile);
      if (reportId != null) {
         EmcReportBean report = (EmcReportBean)this.getDao().selectByIdIfPresent(reportId);
         if (report != null && this.isQysIssueSignMode(report)) {
            CoreBpmnCommentBean issueComment = this.selectLatestReportIssueComment(reportId);
            if (issueComment == null) {
               throw new InvalidDataException("未找到报告签发记录，无法进行契约锁报告签发章签署");
            } else {
               CoreUserBean issuer = (CoreUserBean)this.coreUserService.selectByIdIfPresent(issueComment.getCreatedById());
               Long sealId = this.getIssueSealId(issuer);
               this.fileManager.download(pdfCoreFile);
               File originalFile = CoreFileUtils.getLocalPath(pdfCoreFile).toFile();
               FilePathDTO signedPdfPathDTO = CoreFileUtils.toFilePath(FileScope.temp, FileUtils.getFileBaseName(pdfCoreFile.getName()) + "-issue-sign.pdf");
               Path signedPdfPath = FilePathManager.getLocalPath(signedPdfPathDTO);
               FileUtils.makeDirs(signedPdfPath);
               FileSealUtils.signReportIssue(originalFile.toString(), signedPdfPath.toString(), sealId);
               this.fileManager.upload(CoreFileUtils.toFilePath(pdfCoreFile), signedPdfPath.toFile());
               pdfCoreFile.setSize(signedPdfPath.toFile().length());
               this.coreFileService.getDao().update(pdfCoreFile, new String[]{"SIZE"});
               this.fileManager.download(pdfCoreFile);
            }
         }
      }
   }

   private Long parseReportId(CoreFileBean pdfCoreFile) {
      if (pdfCoreFile != null && StringUtils.equals("wordReportPdf", pdfCoreFile.getExt())) {
         String targetId = pdfCoreFile.getTargetId();
         String prefix = this.getDao().getTable() + "$";
         if (!StringUtils.isBlank(targetId) && targetId.startsWith(prefix)) {
            String reportIdText = targetId.substring(prefix.length());
            return !NumberUtils.isNumber(reportIdText) ? null : NumberUtils.parseLong(reportIdText);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private CoreBpmnCommentBean selectLatestReportIssueComment(Long reportId) {
      return this.bpmnCommentService
         .selectListByFilter(
            SearchFilter.instance()
               .match("targetId", this.getDao().getTable() + "$" + reportId)
               .filter(MatchPattern.EQ)
               .match("status", "REJECT")
               .filter(MatchPattern.DIFFER),
            new Order[0]
         )
         .stream()
         .filter(e -> ObjectUtils.equals("reportIssue", e.getStatusCode()))
         .sorted(Comparator.comparing((CoreBpmnCommentBean e) -> e.getCreatedTime()).reversed())
         .limit(1L)
         .findFirst()
         .orElse(null);
   }

   private boolean isQysIssueSignMode(EmcReportBean report) {
      return !this.isPictureIssueSignMode(report);
   }

   private boolean isPictureIssueSignMode(EmcReportBean report) {
      return report != null && ObjectUtils.equals("picture", report.getIssueSignMode());
   }

   private Long getIssueSealId(CoreUserBean issuer) {
      if (issuer == null) {
         throw new InvalidDataException("未找到报告签发人，无法进行契约锁报告签发章签署");
      } else {
         String sealId = issuer.getExt$Item("sealid");
         if (StringUtils.isBlank(sealId)) {
            sealId = issuer.getExt$Item("sealId");
         }

         if (StringUtils.isBlank(sealId)) {
            sealId = issuer.getExt$Item("SEALID");
         }

         if (StringUtils.isBlank(sealId)) {
            throw new InvalidDataException("签发人[" + issuer.getUserName() + "]未维护契约锁签章ID");
         } else if (!NumberUtils.isNumber(sealId)) {
            throw new InvalidDataException("签发人[" + issuer.getUserName() + "]维护的契约锁签章ID不是有效数字");
         } else {
            return NumberUtils.parseLong(sealId);
         }
      }
   }

   private void signDioXinCommonReport(
      XWPFDocument document, CoreBpmnCommentBean submitComment, CoreBpmnCommentBean auditComment, CoreBpmnCommentBean issueComment
   ) {
      List<XWPFTable> tableList = document.getTables();
      XWPFTable lastTable = tableList.get(2);
      if (submitComment != null) {
         this.addSignaturePicture(lastTable, 2, submitComment, null);
      }

      if (auditComment != null) {
         this.addSignaturePicture(lastTable, 4, auditComment, null);
      }

      if (issueComment != null) {
         this.addSignaturePicture(lastTable, 6, issueComment, null);
         LocalDateTime issueDate = issueComment.getCreatedTime();
         Word2024Utils.insertContentAndImageToTable(lastTable, 8, null, DateTimeUtils.formatLocalDateTime(issueDate, "yyy-MM-dd"), 12, "宋体");
      }
   }

   private void signDioxinQcReport(XWPFDocument document, CoreBpmnCommentBean submitComment, CoreBpmnCommentBean auditComment, CoreBpmnCommentBean issueComment) {
      List<XWPFTable> tableList = document.getTables();
      XWPFTable signTable = tableList.get(1);
      if (submitComment != null) {
         this.addSignaturePicture(signTable, 2, submitComment, null);
      }

      if (auditComment != null) {
         this.addSignaturePicture(signTable, 4, auditComment, null);
      }

      if (issueComment != null) {
         this.addSignaturePicture(signTable, 6, issueComment, null);
         LocalDateTime createdTime = issueComment.getCreatedTime();
         Word2024Utils.insertContentAndImageToTable(signTable, 8, null, DateTimeUtils.formatLocalDateTime(createdTime, "yyy-MM-dd"), 12, "宋体");
      }
   }

   private void addSignaturePicture(XWPFTable table, int cellNum, CoreBpmnCommentBean comment, String content) {
      if (table != null) {
         String creatorId = comment.getCreatedById();
         List<CoreFileBean> userSignatureFileList = this.coreFileService
            .getDao()
            .selectListByOneColumnValue("T_CORE_USER$" + creatorId, "targetId", new Order[0]);
         if (!userSignatureFileList.isEmpty()) {
            CoreFileBean userSignatureFile = userSignatureFileList.get(0);
            String userSignatureFileUrl = CoreFileUtils.getLocalPath(userSignatureFile).toString();
            if (!new File(userSignatureFileUrl).exists()) {
               return;
            }

            Word2024Utils.insertContentAndImageWithSizeToTable(table, cellNum, userSignatureFileUrl, content);
         }
      }
   }

   private void addWatermarkToReport(List<EmcReportBean> reportList) {
      Map<Long, List<EmcReportBean>> projectReportMap = reportList.stream().collect(Collectors.groupingBy(EmcReportBean::getProjId));
      List<EmcReportBean> projectReportList = projectReportMap.values().stream().map(list -> list.get(0)).collect(Collectors.toList());
      List<String> targetIdList = projectReportList.stream().map(report -> "T_EMC_REPORT$" + report.getId()).collect(Collectors.toList());
      List<CoreFileBean> reportFileList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance()
               .match("targetId", targetIdList)
               .filter(MatchPattern.OR)
               .match("bizCategory", Arrays.asList("wordReport", "watermarkReport"))
               .filter(MatchPattern.OR),
            new Order[0]
         );
      List<CoreFileBean> watermarkReportList = reportFileList.stream()
         .filter(file -> StringUtils.equals(file.getBizCategory(), "watermarkReport"))
         .collect(Collectors.toList());
      if (!watermarkReportList.isEmpty()) {
         watermarkReportList.forEach(file -> file.setTargetId("X" + file.getTargetId()));
         this.coreFileService.getDao().update(watermarkReportList, new String[]{"TARGETID"});
      }

      List<EmcProjectBean> projectList = this.projectService.selectListByIds(new ArrayList<>(projectReportMap.keySet()));
      String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
      CoreOrgBean parentOrg = (CoreOrgBean)this.coreOrgService.selectById(parentOrgId);
      Map<Long, String> projectIdNoMap = projectList.stream().collect(Collectors.toMap(EmcProjectBean::getId, EmcProjectBean::getProjNo));
      List<CoreFileBean> wordFileList = reportFileList.stream()
         .filter(file -> StringUtils.equals(file.getBizCategory(), "wordReport") && StringUtils.equals(file.getFileExt(), "docx"))
         .collect(Collectors.toList());
      projectReportMap.forEach(
         (projectId, curReportList) -> wordFileList.stream()
               .filter(file -> StringUtils.equals(file.getTargetId(), "T_EMC_REPORT$" + ((EmcReportBean)curReportList.get(0)).getId()))
               .findFirst()
               .ifPresent(file -> {
                  File wordFile = new File(FilePathManager.getLocalPath(CoreFileUtils.toFilePath(file)).toString());
                  String fileName = file.getName().substring(0, file.getName().indexOf("-") + 1) + System.currentTimeMillis() + ".pdf";
                  File watermarkFile = JSTIeSealHelper.addWatermark(wordFile, projectId, projectIdNoMap.get(projectId), fileName, parentOrg.getOrgName());
                  this.saveWordReportCoreFile(watermarkFile.getName(), watermarkFile, curReportList, "watermarkReport");
               })
      );
   }

   private void addStampToReport(List<EmcReportBean> reportList) {
      Map<Long, List<EmcReportBean>> projectReportMap = reportList.stream().collect(Collectors.groupingBy(EmcReportBean::getProjId));
      List<EmcReportBean> projectReportList = projectReportMap.values().stream().map(list -> list.get(0)).collect(Collectors.toList());
      List<String> targetIdList = projectReportList.stream().map(report -> "T_EMC_REPORT$" + report.getId()).collect(Collectors.toList());
      List<CoreFileBean> reportFileList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("ext", "wordReportPdf").filter(MatchPattern.SEQ),
            new Order[0]
         );
      this.seal(reportFileList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public String completeReport(RestJsonWrapperBean wrapper) {
      Long projId = Long.parseLong(wrapper.getFilterValue("projId"));
      String deliveryDate = wrapper.getFilterValue("deliverydate");
      String completeDate = wrapper.getFilterValue("completedate");
      List<EmcReportBean> updateReportList = new ArrayList<>();
      List<EmcReportBean> updateReportDoneList = new ArrayList<>();
      List<EmcProjectBean> projList = this.projectService.selectListByFilter(SearchFilter.instance().match("id", projId).filter(MatchPattern.OR), new Order[0]);
      String projNo = projList.get(0).getProjNo();
      List<EmcReportBean> isExistList = this.selectListByFilter(SearchFilter.instance().match("projId", projId).filter(MatchPattern.OR), new Order[0]);
      List<EmcReportBean> isDoneList = this.selectListByFilter(
         SearchFilter.instance().match("projId", projId).filter(MatchPattern.OR).match("COMPLETESTATUS", "done").filter(MatchPattern.OR), new Order[0]
      );
      String isDone = null;
      if (!isExistList.isEmpty()) {
         if (isDoneList.size() >= 1) {
            isDone = "done";
            isExistList.forEach(e -> {
               EmcReportBean updateReport = new EmcReportBean();
               updateReport.setId(e.getId());
               updateReport.setExt$Item("DELIVERYDATE", deliveryDate);
               updateReport.setExt$Item("COMPLETEDATE", completeDate);
               updateReport.setReportNo(projNo);
               updateReport.setExt$Item("COMPLETESTATUS", "done");
               updateReportDoneList.add(updateReport);
            });
            this.getDao().update(updateReportDoneList, new String[]{"DELIVERYDATE", "COMPLETEDATE", "REPORTNO", "COMPLETESTATUS"});
            List<Long> projectIdList = isDoneList.stream().<Long>map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
            List<EmcProjectBean> projectList = this.projectService.selectListByIds(projectIdList);
            List<EmcProjectBean> updateProjList = new ArrayList<>();
            projectList.forEach(p -> {
               p.setExt$Item("COMPLETESTATUS", "1");
               p.setExt$Item("COMPLETEDATE", completeDate);
               p.setExt$Item("DELIVERYDATE", deliveryDate);
               updateProjList.add(p);
            });
            this.projectService.getDao().update(updateProjList, new String[]{"COMPLETESTATUS", "COMPLETEDATE", "DELIVERYDATE"});
            return isDone;
         }

         isExistList.forEach(e -> {
            EmcReportBean updateReport = new EmcReportBean();
            updateReport.setId(e.getId());
            updateReport.setExt$Item("COMPLETEDATE", completeDate);
            updateReport.setExt$Item("DELIVERYDATE", deliveryDate);
            updateReport.setExt$Item("COMPLETESTATUS", "done");
            updateReportList.add(updateReport);
         });
         this.getDao().update(updateReportList, new String[]{"DELIVERYDATE", "COMPLETEDATE", "REPORTNO", "COMPLETESTATUS"});
         List<Long> projectIdList = isExistList.stream().<Long>map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
         List<EmcProjectBean> projectList = this.projectService.selectListByIds(projectIdList);
         this.opportunityOpenService.syncProjectInfo(projectList);
         List<EmcProjectBean> updateProjList = new ArrayList<>();
         projectList.forEach(p -> {
            p.setExt$Item("COMPLETEDATE", completeDate);
            p.setExt$Item("DELIVERYDATE", deliveryDate);
            p.setExt$Item("COMPLETESTATUS", "1");
            updateProjList.add(p);
         });
         this.projectService.getDao().update(updateProjList, new String[]{"COMPLETESTATUS", "COMPLETEDATE", "DELIVERYDATE"});
      } else {
         List<EmcReportBean> reportList = new ArrayList<>();
         EmcReportBean report = new EmcReportBean();
         report.setId(ApplicationContextHelper.getNextIdentity());
         report.setProjId(projId);
         report.setExt$Item("DELIVERYDATE", deliveryDate);
         report.setExt$Item("COMPLETEDATE", completeDate);
         report.setReportNo(projNo);
         report.setExt$Item("COMPLETESTATUS", "done");
         reportList.add(report);
         this.getDao().insert(reportList);
         List<Long> projectIdList = reportList.stream().<Long>map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
         List<EmcProjectBean> projectList = this.projectService.selectListByIds(projectIdList);
         this.opportunityOpenService.syncProjectInfo(projectList);
         List<EmcProjectBean> updateProjList = new ArrayList<>();
         projectList.forEach(p -> {
            p.setExt$Item("COMPLETESTATUS", "1");
            p.setExt$Item("COMPLETEDATE", completeDate);
            p.setExt$Item("DELIVERYDATE", deliveryDate);
            updateProjList.add(p);
         });
         this.projectService.getDao().update(updateProjList, new String[]{"COMPLETESTATUS", "COMPLETEDATE", "DELIVERYDATE"});
      }

      return isDone;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   public String selectCompleteDate(RestJsonWrapperBean wrapper) {
      Long projId = Long.parseLong(wrapper.getFilterValue("projId"));
      List<EmcReportBean> reportBeforeList = this.getDao().selectListByOneColumnValue(projId, "projId", new Order[0]);
      List<EmcReportBean> reportList = reportBeforeList.stream()
         .filter(r -> StringUtils.equals("done", r.getExt$Item("COMPLETESTATUS")))
         .collect(Collectors.toList());
      String spliDate = null;
      if (!reportList.isEmpty()) {
         String deliveryDate = reportList.get(0).getExt$Item("DELIVERYDATE");
         String completeDate = reportList.get(0).getExt$Item("COMPLETEDATE");
         return deliveryDate + '+' + completeDate;
      } else {
         return spliDate;
      }
   }

   private String calcLampblackTotalAvgResult(List<EmcOrderTaskResultBean> resultList) {
      List<String> resultValueList = resultList.stream().<String>map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());
      OptionalDouble maxReportResult = resultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).max();
      if (!maxReportResult.isPresent()) {
         return null;
      } else {
         double quarterValue = ArithUtils.div(maxReportResult.getAsDouble(), 4.0);
         List<EmcOrderTaskResultBean> validResultList = resultList.stream()
            .filter(result -> NumberUtils.isNumber(result.getReportResult()))
            .filter(result -> NumberUtils.parseDouble(result.getReportResult()) >= quarterValue)
            .collect(Collectors.toList());
         List<Long> validResultIdList = validResultList.stream().<Long>map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
         resultList.stream().filter(result -> !validResultIdList.contains(result.getId())).forEach(result -> result.setExt$Item("invalidresult", "1"));
         OptionalDouble totalAvgValue = validResultList.stream()
            .map(EmcOrderTaskResultBean::getReportResult)
            .filter(NumberUtils::isNumber)
            .mapToDouble(NumberUtils::parseDouble)
            .average();
         return totalAvgValue.isPresent() ? totalAvgValue.getAsDouble() + "" : null;
      }
   }

   protected List<EmcOrderTaskResultBean> calcParallelAverageValue(
      boolean sampling,
      List<Long> orderIdList,
      List<EmcOrderTaskResultBean> orderResultList,
      List<EmcItemRevisionBean> itemRevisionList,
      List<EmcItemBean> itemList
   ) {
      List<EmcOrderTaskResultBean> pdupResultList = orderResultList.stream().filter(f -> "P-DUP".equals(f.getQcCategoryCode())).collect(Collectors.toList());
      List<Long> pdupOrderTaskIdList = pdupResultList.stream().<Long>map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
      List<EmcOrderTaskBean> pdupOrderTaskList = this.orderTaskService
         .getDao()
         .selectListByIds(pdupOrderTaskIdList, Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE"), new Order[0]);
      List<EmcOrderTaskResultBean> orderResultPhList = orderResultList.stream().filter(r -> r.getItemName().contains("pH")).collect(Collectors.toList());
      if (!orderResultPhList.isEmpty()) {
         List<Long> orderTaskIdList = orderResultList.stream().<Long>map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
         List<EmcOrderTaskBean> orderSearchTaskList = this.orderTaskService
            .getDao()
            .selectListByIds(orderTaskIdList, Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE", "MONITORTMPLID"), new Order[0]);
         List<EmcMonitorTmplBean> monitorTmplList = this.monitorTmplService
            .selectListByFilter(SearchFilter.instance().match("MONITORTMPLNAME", "固体废物").filter(MatchPattern.OR), new Order[0]);
         List<String> monitorTmplIdList = monitorTmplList.stream().<String>map(EmcMonitorTmplBean::getId).distinct().collect(Collectors.toList());
         List<EmcOrderTaskBean> orderTaskSoildWasteList = orderSearchTaskList.stream()
            .filter(ot -> monitorTmplIdList.contains(ot.getMonitorTmplId()))
            .collect(Collectors.toList());
         if (!orderTaskSoildWasteList.isEmpty()) {
            List<Long> orderTaskSolidIdList = orderTaskSoildWasteList.stream().<Long>map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
            orderResultPhList = orderResultPhList.stream().filter(r -> !orderTaskSolidIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());
         }

         List<Long> exceptSoildWastePhOrderTaskResultIdList = orderResultPhList.stream()
            .<Long>map(EmcOrderTaskResultBean::getId)
            .distinct()
            .collect(Collectors.toList());
         orderResultList = orderResultList.stream().filter(r -> !exceptSoildWastePhOrderTaskResultIdList.contains(r.getId())).collect(Collectors.toList());
      }

      List<EmcOrderTaskResultBean> naResultList = sampling
         ? orderResultList.stream().filter(f -> "N/A".equals(f.getQcCategoryCode())).collect(Collectors.toList())
         : orderResultList.stream().filter(r -> orderIdList.contains(r.getOrderId())).collect(Collectors.toList());
      List<EmcOrderTaskResultBean> naPhResultList = sampling
         ? orderResultPhList.stream().filter(f -> "N/A".equals(f.getQcCategoryCode())).collect(Collectors.toList())
         : orderResultPhList.stream().filter(r -> orderIdList.contains(r.getOrderId())).collect(Collectors.toList());
      Map<String, List<Pair<String, Double>>> unitConvertMap = this.queryUnitConvert();
      List<EmcOrderTaskResultBean> finalResultList = new ArrayList<>(naResultList.size());
      naResultList.forEach(
         result -> {
            String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
            if (NumberUtils.isScientificNumber(finalValue)) {
               BigDecimal finalValueB = new BigDecimal(finalValue);
               finalValue = finalValueB.toPlainString();
            }

            result.setOriginalResult(finalValue);
            this.reviseOriginalValue(result, itemRevisionList, itemList);
            if (StringUtils.equals(result.getResultCategory(), "number")
               && NumberUtils.isNumber(result.getLowLimit())
               && NumberUtils.isNumber(result.getOriginalResult())
               && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
               result.setReportResult("ND");
            }

            finalResultList.add(result);
         }
      );
      naPhResultList.forEach(
         result -> {
            String finalValue = result.getReportResult();
            finalValue = NumberUtils.isScientificNumber(finalValue) ? NumberUtils.formatNumber(NumberUtils.parseDouble(finalValue)) : finalValue;
            if (!StringUtils.isEmpty(result.getReason()) && "/".equals(finalValue)) {
               finalValue = "?";
            }

            result.setOriginalResult(finalValue);
            this.reviseOriginalValue(result, itemRevisionList, itemList);
            if (StringUtils.equals(result.getResultCategory(), "number")
               && NumberUtils.isNumber(result.getLowLimit())
               && NumberUtils.isNumber(result.getOriginalResult())
               && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
               result.setReportResult("ND");
            }

            finalResultList.add(result);
         }
      );
      return finalResultList;
   }

   protected List<EmcOrderTaskResultBean> calcDioxinParallelAverageValue(
      boolean sampling,
      List<Long> orderIdList,
      List<EmcOrderTaskResultBean> orderResultList,
      List<EmcItemRevisionBean> itemRevisionList,
      List<EmcItemBean> itemList
   ) {
      List<EmcOrderTaskResultBean> naResultList = sampling
         ? orderResultList.stream().filter(f -> "N/A".equals(f.getQcCategoryCode()) || "P-DUP".equals(f.getQcCategoryCode())).collect(Collectors.toList())
         : orderResultList.stream().filter(r -> orderIdList.contains(r.getOrderId())).collect(Collectors.toList());
      List<EmcOrderTaskResultBean> finalResultList = new ArrayList<>(naResultList.size());
      naResultList.forEach(
         result -> {
            String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
            finalValue = NumberUtils.isScientificNumber(finalValue) ? NumberUtils.formatNumber(NumberUtils.parseDouble(finalValue)) : finalValue;
            if (!StringUtils.isEmpty(result.getReason()) && "/".equals(finalValue)) {
               finalValue = "?";
            }

            result.setOriginalResult(finalValue);
            this.reviseOriginalValue(result, itemRevisionList, itemList);
            if (StringUtils.equals(result.getResultCategory(), "number")
               && NumberUtils.isNumber(result.getLowLimit())
               && NumberUtils.isNumber(result.getOriginalResult())
               && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
               result.setReportResult("ND");
            }

            finalResultList.add(result);
         }
      );
      return finalResultList;
   }

   private boolean isEffectiveResult(EmcOrderTaskResultBean result) {
      String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
      return NumberUtils.isNumber(finalValue) || "ND".equals(finalValue) || "HD".equals(finalValue);
   }

   private void replaceAvgOrReportResultNdHdValue(EmcOrderTaskResultBean result) {
      String finalValue = StringUtils.isEmpty(result.getAvgResult()) ? result.getReportResult() : result.getAvgResult();
      this.replaceNdHdValue(result, finalValue);
   }

   private void replaceNdHdValue(EmcOrderTaskResultBean result, String finalValue) {
      if (!NumberUtils.isNumber(finalValue)) {
         if ("HD".equals(finalValue) && NumberUtils.isNumber(result.getHighLimit())) {
            result.setReportResult(result.getHighLimit());
         }
      }
   }

   private void reviseOriginalValue(EmcOrderTaskResultBean result, List<EmcItemRevisionBean> itemRevisionList, List<EmcItemBean> itemList) {
      List<EmcItemRevisionBean> filterItemRevisionList = itemRevisionList.stream()
         .filter(f -> ObjectUtils.equals(result.getItemId(), f.getItemId()))
         .collect(Collectors.toList());
      EmcItemBean item = itemList.stream().filter(f -> ObjectUtils.equals(result.getItemId(), f.getId())).findFirst().orElseGet(EmcItemBean::new);
      this.orderTaskResultService.setReportResultValue(result, item, filterItemRevisionList);
      if (StringUtils.equals(result.getResultCategory(), "number")
         && NumberUtils.isNumber(result.getLowLimit())
         && NumberUtils.isNumber(result.getOriginalResult())
         && NumberUtils.parseDouble(result.getOriginalResult()) < NumberUtils.parseDouble(result.getLowLimit())) {
         result.setReportResult("ND");
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   public String selectCompleteStatus(RestJsonWrapperBean wrapper) {
      Long projId = Long.parseLong(wrapper.getFilterValue("projId"));
      List<EmcReportBean> reportList = this.selectListByFilter(SearchFilter.instance().match("projId", projId).filter(MatchPattern.OR), new Order[0]);
      String completeStatus = null;
      return !reportList.isEmpty() ? reportList.get(0).getExt$Item("COMPLETESTATUS") : completeStatus;
   }

   @Transactional
   public void createReport(RestJsonWrapperBean wrapper) {
      List<EmcFolderBean> selectedFolderList = wrapper.parse(EmcFolderBean.class);
      String projId = wrapper.getParamValue("projid");
      List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
      List<Long> selectedTestIdList = selectedOrderTaskList.stream().<Long>map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());
      if (!selectedFolderList.isEmpty()) {
         List<Long> folderIdList = selectedFolderList.stream().<Long>map(EmcFolderBean::getId).collect(Collectors.toList());
         EmcProjectBean project = (EmcProjectBean)this.projectService.selectById(selectedFolderList.get(0).getProjId());
         String needSplitReport = Optional.ofNullable(project.getNeedSplitReport()).orElse("0");
         String erpOrderNumber = Optional.ofNullable(project.getErpOrderNumber()).orElse(project.getProjNo());
         List<EmcReportBean> reportList = this.getDao()
            .selectListByOneColumnValue(project.getId(), "projId", Arrays.asList("id", "reportNo", "orderCategory"), new Order[0]);
         List<String> reportNoList = reportList.stream()
            .filter(r -> StringUtils.isEmpty(r.getOrderCategory()))
            .<String>map(EmcReportBean::getReportNo)
            .filter(no -> !StringUtils.isEmpty(no))
            .distinct()
            .collect(Collectors.toList());
         String reportNo;
         if ("0".equals(needSplitReport)) {
            int maxSeqNo = reportNoList.stream()
               .map(no -> StringUtils.replace(no, project.getProjNo(), "").replace("-", ""))
               .filter(NumberUtils::isNumber)
               .mapToInt(NumberUtils::parseInt)
               .max()
               .orElse(0);
            String nextSeqNo = reportNoList.isEmpty() ? "" : "-" + (maxSeqNo + 1);
            reportNo = erpOrderNumber + nextSeqNo;
         } else {
            int maxSeqNo = reportNoList.stream()
               .map(no -> StringUtils.replace(no, erpOrderNumber + "C", ""))
               .filter(NumberUtils::isNumber)
               .mapToInt(NumberUtils::parseInt)
               .max()
               .orElse(0);
            String nextSeqNo = reportNoList.isEmpty() ? "1" : ObjectUtils.toString(maxSeqNo + 1);
            reportNo = erpOrderNumber + "C" + nextSeqNo;
         }

         EmcReportBean newReport = new EmcReportBean();
         newReport.setId(ApplicationContextHelper.getNextIdentity());
         newReport.setProjId(selectedFolderList.get(0).getProjId());
         newReport.setStatus("reportEdit");
         newReport.setReportNo(reportNo);
         newReport.setReportName(project.getProjName());
         newReport.setRemark(project.getExt$Item("reportremark"));
         newReport.setReportAptitude(project.getReportAptitude());
         if (StringUtils.contains(project.getProjNo(), "T")) {
            newReport.setQcReport("1");
         } else {
            newReport.setQcReport("0");
         }

         List<EmcOrderTaskBean> orderTaskList = this.orderTaskService
            .getDao()
            .selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "projId", "folderId", "orderId", "testId"), new Order[0]);
         List<EmcOrderTaskBean> validOrderTaskList = selectedTestIdList.isEmpty()
            ? orderTaskList
            : orderTaskList.stream().filter(task -> selectedTestIdList.contains(task.getTestId())).collect(Collectors.toList());
         List<EmcReportLineBean> reportLineList = new ArrayList<>(validOrderTaskList.size());
         validOrderTaskList.forEach(task -> {
            EmcReportLineBean reportLine = new EmcReportLineBean();
            reportLine.setId(ApplicationContextHelper.getNextIdentity());
            reportLine.setReportId(newReport.getId());
            reportLine.setProjId(task.getProjId());
            reportLine.setFolderId(task.getFolderId());
            reportLine.setOrderId(task.getOrderId());
            reportLine.setOrderTaskId(task.getId());
            reportLineList.add(reportLine);
         });
         List<String> monitorTmplIdList = validOrderTaskList.stream().map(e -> e.getMonitorTmplId()).distinct().collect(Collectors.toList());
         List<String> orderCategoryList = validOrderTaskList.stream().map(e -> e.getOrderCategory()).distinct().collect(Collectors.toList());
         newReport.setMonitorTmplId(StringUtils.join(monitorTmplIdList, ","));
         newReport.setOrderCategory(StringUtils.join(orderCategoryList, ","));
         this.getDao().insert(newReport);
         this.reportLineService.getDao().insert(reportLineList);
      } else {
         EmcProjectBean projectx = (EmcProjectBean)this.projectService.selectById(Long.valueOf(projId));
         String needSplitReportx = Optional.ofNullable(projectx.getNeedSplitReport()).orElse("0");
         String erpOrderNumberx = Optional.ofNullable(projectx.getErpOrderNumber()).orElse(projectx.getProjNo());
         List<EmcReportBean> reportListx = this.getDao()
            .selectListByOneColumnValue(projectx.getId(), "projId", Arrays.asList("id", "reportNo", "orderCategory"), new Order[0]);
         List<String> reportNoListx = reportListx.stream()
            .filter(r -> StringUtils.isEmpty(r.getOrderCategory()))
            .<String>map(EmcReportBean::getReportNo)
            .filter(no -> !StringUtils.isEmpty(no))
            .distinct()
            .collect(Collectors.toList());
         String reportNox;
         if ("0".equals(needSplitReportx)) {
            int maxSeqNo = reportNoListx.stream()
               .map(no -> StringUtils.replace(no, projectx.getProjNo(), "").replace("-", ""))
               .filter(NumberUtils::isNumber)
               .mapToInt(NumberUtils::parseInt)
               .max()
               .orElse(0);
            String nextSeqNo = reportNoListx.isEmpty() ? "" : "-" + (maxSeqNo + 1);
            reportNox = erpOrderNumberx + nextSeqNo;
         } else {
            int maxSeqNo = reportNoListx.stream()
               .map(no -> StringUtils.replace(no, erpOrderNumberx + "C", ""))
               .filter(NumberUtils::isNumber)
               .mapToInt(NumberUtils::parseInt)
               .max()
               .orElse(0);
            String nextSeqNo = reportNoListx.isEmpty() ? "1" : ObjectUtils.toString(maxSeqNo + 1);
            reportNox = erpOrderNumberx + "C" + nextSeqNo;
         }

         EmcReportBean newReport = new EmcReportBean();
         newReport.setId(ApplicationContextHelper.getNextIdentity());
         newReport.setProjId(projectx.getId());
         newReport.setStatus("reportEdit");
         newReport.setReportNo(reportNox);
         newReport.setRemark(projectx.getExt$Item("reportremark"));
         this.getDao().insert(newReport);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @ValidateDataWith(PfGenerateReportResultValidator.class)
   public void generateReportResult(RestJsonWrapperBean wrapper) {
      EmcReportBean report = (EmcReportBean)wrapper.parseUnique(EmcReportBean.class);
      List<EmcReportLineBean> reportLineList = this.reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId", new Order[0]);
      List<Long> orderTaskIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderTaskId).collect(Collectors.toList());
      if (!orderTaskIdList.isEmpty()) {
         EmcReportResultBean deletedReportResult = new EmcReportResultBean();
         deletedReportResult.setReportId(report.getId());
         this.reportResultService.getDao().deleteBy(Collections.singletonList(deletedReportResult), new String[]{"reportId"});
         List<List<Long>> naPartitionList = ListUtils.partition(orderTaskIdList, 200);
         List<EmcOrderTaskBean> orderTaskList = new ArrayList<>();
         IntStream.range(0, naPartitionList.size())
            .forEach(
               index -> {
                  List<Long> curNaPartitionList = naPartitionList.get(index);
                  List<EmcOrderTaskBean> taskList = this.orderTaskService
                     .getDao()
                     .selectListByOneColumnValues(curNaPartitionList, "id", Arrays.asList("id", "virtual"), new Order[0]);
                  orderTaskList.addAll(taskList);
               }
            );
         List<Long> validOrderTaskIdList = orderTaskList.stream()
            .filter(task -> !StringUtils.equals(task.getExt$Item("virtual"), "1"))
            .<Long>map(EmcOrderTaskBean::getId)
            .collect(Collectors.toList());
         if (validOrderTaskIdList.size() > 0) {
            List<EmcOrderTaskResultBean> orderTaskResultList = this.orderTaskResultService
               .selectListByFilter(
                  SearchFilter.instance()
                     .match("orderTaskId", validOrderTaskIdList)
                     .filter(MatchPattern.OR)
                     .match("REPORTDISPLAY", "1")
                     .filter(MatchPattern.SEQ)
                     .match("RESULTSTATUS", "Done")
                     .filter(MatchPattern.SEQ),
                  new Order[0]
               );
            if (orderTaskResultList.isEmpty()) {
               return;
            }

            List<Long> orderIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderList = this.orderService
               .selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR), new Order[0]);
            Map<Long, EmcOrderBean> orderIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));
            List<Long> itemIdList = orderTaskResultList.stream().<Long>map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
            List<EmcItemRevisionBean> itemRevisionList = this.itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID", new Order[0]);
            List<EmcItemBean> itemList = this.itemService.getDao().selectListByIds(itemIdList, new Order[0]);
            List<EmcReportResultBean> reportResultList = new ArrayList<>();
            boolean existLampblack = orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), "油烟"));
            String bizCategoryId = report.getExt$Item("bizCategoryId");
            boolean isDioxin = StringUtils.equalsIgnoreCase(bizCategoryId, "C");
            boolean sampling = StringUtils.equals("1", report.getExt$Item("sample"));
            List<EmcOrderTaskResultBean> finalResultList = isDioxin
               ? this.calcDioxinParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList)
               : this.calcParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList);
            Map<Long, Long> taskReportResultIdMap = new HashMap<>();
            finalResultList.forEach(result -> {
               EmcReportResultBean reportResult = new EmcReportResultBean();
               reportResult.setId(ApplicationContextHelper.getNextIdentity());
               reportResult.setReportId(report.getId());
               reportResult.setDataType("sample");
               this.copyProperties(reportResult, orderIdMap.get(result.getOrderId()), result);
               reportResultList.add(reportResult);
               if (existLampblack) {
                  taskReportResultIdMap.put(result.getId(), reportResult.getId());
               }
            });
            boolean calcStatistic = "1".equals(report.getIfStatistics());
            if (calcStatistic) {
               Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
               totalTaskResultMap.forEach(
                  (key, resultList) -> {
                     resultList.forEach(result -> this.replaceNdHdValue(result, result.getReportResult()));
                     Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream()
                        .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                     dayResultListMap.forEach(
                        (day, dayResultList) -> {
                           List<String> dayResultValueList = dayResultList.stream()
                              .<String>map(EmcOrderTaskResultBean::getReportResult)
                              .collect(Collectors.toList());
                           dayResultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                              EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                              BeanUtils.copyProperties(dayResultList.get(0), totalResult, new String[0]);
                              totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));
                              this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                              EmcReportResultBean reportResult = new EmcReportResultBean();
                              reportResult.setId(ApplicationContextHelper.getNextIdentity());
                              reportResult.setReportId(report.getId());
                              reportResult.setDataType("dayaverage");
                              EmcOrderBean currentOrder = orderIdMap.get(((EmcOrderTaskResultBean)dayResultList.get(0)).getOrderId());
                              this.copyProperties(reportResult, currentOrder, totalResult);
                              reportResultList.add(reportResult);
                           });
                        }
                     );
                  }
               );
            }

            List<Long> invalidLampblackResultIdList = new ArrayList<>();
            if (calcStatistic) {
               Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
               totalTaskResultMap.forEach(
                  (key, resultList) -> {
                     resultList.forEach(result -> this.replaceNdHdValue(result, result.getReportResult()));
                     List<String> resultValueList = resultList.stream().<String>map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());
                     EmcOrderBean currentOrder = orderIdMap.get(resultList.get(0).getOrderId());
                     if (resultList.size() == 1) {
                        EmcReportResultBean reportResult = new EmcReportResultBean();
                        reportResult.setId(ApplicationContextHelper.getNextIdentity());
                        reportResult.setReportId(report.getId());
                        reportResult.setDataType("totalaverage");
                        this.copyProperties(reportResult, currentOrder, resultList.get(0));
                        reportResultList.add(reportResult);
                     } else if (StringUtils.contains(currentOrder.getOrderCategory(), "油烟")) {
                        Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream()
                           .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                        dayResultListMap.forEach((cycle, dayResultList) -> {
                           String lampblackAvgResult = this.calcLampblackTotalAvgResult((List<EmcOrderTaskResultBean>)dayResultList);
                           if (lampblackAvgResult != null) {
                              EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                              BeanUtils.copyProperties(dayResultList.get(0), totalResult, new String[0]);
                              totalResult.setOriginalResult(NumberUtils.formatNumber(NumberUtils.parseDouble(lampblackAvgResult)));
                              this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                              EmcReportResultBean reportResult = new EmcReportResultBean();
                              reportResult.setId(ApplicationContextHelper.getNextIdentity());
                              reportResult.setReportId(report.getId());
                              reportResult.setDataType("dayaverage");
                              this.copyProperties(reportResult, orderIdMap.get(dayResultList.get(0).getOrderId()), totalResult);
                              reportResultList.add(reportResult);
                           }
                        });
                        resultList.stream().filter(r -> "1".equals(r.getExt$Item("invalidresult"))).forEach(r -> invalidLampblackResultIdList.add(r.getId()));
                     } else {
                        resultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                           EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                           BeanUtils.copyProperties(resultList.get(0), totalResult, new String[0]);
                           totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));
                           this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                           EmcReportResultBean reportResult = new EmcReportResultBean();
                           reportResult.setId(ApplicationContextHelper.getNextIdentity());
                           reportResult.setReportId(report.getId());
                           reportResult.setDataType("totalaverage");
                           this.copyProperties(reportResult, currentOrder, totalResult);
                           reportResultList.add(reportResult);
                        });
                     }
                  }
               );
            }

            if (!reportResultList.isEmpty()) {
               if (!invalidLampblackResultIdList.isEmpty()) {
                  List<Long> invalidReportResultIdList = new ArrayList<>();
                  invalidLampblackResultIdList.forEach(id -> invalidReportResultIdList.add(taskReportResultIdMap.get(id)));
                  reportResultList.stream()
                     .filter(result -> invalidReportResultIdList.contains(result.getId()))
                     .forEach(result -> result.setInvalidResult("1"));
               }

               this.reportResultService.getDao().insert(reportResultList);
               reportResultList.clear();
               orderTaskResultList.clear();
            }
         }
      }
   }

   @ValidateDataWith(PfViewReportValidator.class)
   @Transactional
   public Long viewReport(RestJsonWrapperBean wrapper) {
      EmcReportBean report = (EmcReportBean)wrapper.parseUnique(EmcReportBean.class);
      Long projectId = report.getProjId();
      String targetId = this.getDao().getTable() + "$" + report.getId();
      List<CoreFileBean> wordReportFileList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.SEQ).match("bizCategory", "wordReport").filter(MatchPattern.EQ),
            new Order[0]
         );
      EmcProjectBean project = (EmcProjectBean)this.projectService
         .selectFirstByFilter(SearchFilter.instance().match("id", projectId).filter(MatchPattern.SEQ), new Order[0]);
      List<EmcReportBean> reportList = Collections.singletonList(report);
      LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
      ExecutorHelper.submit(() -> {
         LocalContextHelper.setUserLogin(loginUser);
         this.composeReportRecordPdf(reportList);
         LocalContextHelper.removeUserLogin();
      });
      if (!wordReportFileList.isEmpty()) {
         List<CoreFileBean> updateCoreFileList = new ArrayList<>();
         wordReportFileList.forEach(e -> {
            CoreFileBean updateCoreFile = new CoreFileBean();
            updateCoreFile.setId(e.getId());
            updateCoreFile.setTargetId("X" + e.getTargetId());
            updateCoreFileList.add(updateCoreFile);
         });
         this.coreFileService.getDao().update(updateCoreFileList, new String[]{"TARGETID"});
         CoreFileBean wordTemplateConfigFile = wordReportFileList.get(0);
         CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
         fileManager.download(wordTemplateConfigFile);
         File templateFile = CoreFileUtils.getLocalPath(wordTemplateConfigFile).toFile();
         this.generateWordReport(templateFile, templateFile, report, project, true);
         List<CoreFileBean> newCoreFileList = this.saveWordReportCoreFile(wordTemplateConfigFile.getName(), templateFile, reportList, "wordReport");
         return newCoreFileList.get(0).getId();
      } else {
         Long reportConfigLineId = report.getReportConfigLineId();
         List<CoreFileBean> wordTemplateConfigFileList = this.coreFileService
            .getDao()
            .selectListByOneColumnValue("T_CORE_REPORT_CONFIG_LINE$" + reportConfigLineId, "targetId", new Order[0]);
         if (!wordTemplateConfigFileList.isEmpty()) {
            CoreFileBean wordTemplateConfigFile = wordTemplateConfigFileList.get(0);
            String reportNo = StringUtils.isEmpty(report.getReportNo()) ? project.getProjNo() : report.getReportNo();
            report.setReportNo(reportNo);
            String wordReportFileName = FileUtils.getFileBaseName(wordTemplateConfigFile.getName())
               + "-"
               + reportNo
               + "."
               + FileUtils.getFileExtension(wordTemplateConfigFile.getName());
            CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
            fileManager.download(wordTemplateConfigFile);
            File templateFile = CoreFileUtils.getLocalPath(wordTemplateConfigFile).toFile();
            File wordReportFile = FilePathManager.getLocalPath(
                  FilePathDTO.of(wordTemplateConfigFile.getScope(), "", wordTemplateConfigFile.getExt(), wordReportFileName)
               )
               .toFile();
            this.generateWordReport(templateFile, wordReportFile, report, project, false);
            List<CoreFileBean> newCoreFileList = this.saveWordReportCoreFile(wordReportFileName, wordReportFile, reportList, "wordReport");
            return newCoreFileList.get(0).getId();
         } else {
            return 0L;
         }
      }
   }

   @Transactional
   @ValidateDataWith(PfViewReportValidator.class)
   public Long viewNewReport(RestJsonWrapperBean wrapper) {
      EmcReportBean report = (EmcReportBean)wrapper.parseUnique(EmcReportBean.class);
      if (ObjectUtils.isEmpty(report.getReportTemplateId())) {
         throw new InvalidDataException("请先选择WORD报告模板");
      } else {
         Map<String, String> param = new HashMap<>();
         param.put("id", String.valueOf(report.getId()));
         String targetId = this.getDao().getTable() + "$" + report.getId();
         List<CoreFileBean> wordReportFileList = this.coreFileService
            .selectListByFilter(
               SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.SEQ).match("bizCategory", "wordReport").filter(MatchPattern.EQ),
               new Order[0]
            );
         List<CoreFileBean> updateCoreFileList = new ArrayList<>();
         wordReportFileList.forEach(e -> {
            CoreFileBean updateCoreFile = new CoreFileBean();
            updateCoreFile.setId(e.getId());
            updateCoreFile.setTargetId("X" + e.getTargetId());
            updateCoreFileList.add(updateCoreFile);
         });
         this.coreFileService.getDao().update(updateCoreFileList, new String[]{"TARGETID"});
         CoreFileBean docxFile = this.limsReportTemplateService
            .generatorReport(report.getReportTemplateId(), this.getDao().getTable(), new RestJsonWrapperBean(JSON.toJSONString(param)));
         return docxFile.getId();
      }
   }

   private void composeReportRecordPdf(List<EmcReportBean> reportList) {
      if (!CollectionUtils.isEmpty(reportList)) {
         this.composeReportRunRecordPdf(reportList);
         this.composeReportSampleRecordPdf(reportList);
      }
   }

   private void composeReportSampleRecordPdf(List<EmcReportBean> reportList) {
      List<Long> projIdList = reportList.stream()
         .filter(e -> !ObjectUtils.isEmpty(e.getProjId()))
         .<Long>map(EmcReportBean::getProjId)
         .distinct()
         .collect(Collectors.toList());
      List<CoreFileBean> existFilesFilter = reportList.stream().map(report -> {
         CoreFileBean coreFileBean = new CoreFileBean();
         coreFileBean.setTargetId("T_EMC_REPORT$" + report.getId());
         coreFileBean.setExt("sampleRecordPdf");
         return coreFileBean;
      }).distinct().collect(Collectors.toList());
      List<CoreFileBean> existFiles = this.coreFileService
         .getDao()
         .selectList(existFilesFilter, Arrays.asList("TARGETID", "EXT"), Collections.emptyList(), new Order[0]);
      if (CollectionUtils.isEmpty(existFiles)) {
         List<EmcSampleBean> sampleList = this.sampleService
            .selectListByFilter(SearchFilter.instance().match("projid", projIdList).filter(MatchPattern.OR), new Order[0]);
         List<CoreFileBean> fileFilters = new ArrayList<>();
         sampleList.forEach(r -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_SAMPLE$" + r.getId());
            coreFileBean.setExt("sampleRecordPdf");
            fileFilters.add(coreFileBean);
         });
         List<CoreFileBean> allSampleFileList = this.coreFileService
            .getDao()
            .selectList(fileFilters, Arrays.asList("TARGETID", "EXT"), Collections.emptyList(), new Order[0]);
         if (!org.thymeleaf.util.ListUtils.isEmpty(allSampleFileList)) {
            reportList.forEach(
               report -> {
                  Long projId = report.getProjId();
                  List<CoreFileBean> curReportFileList = allSampleFileList.stream()
                     .filter(file -> StringUtils.contains(file.getTargetId(), projId + ""))
                     .collect(Collectors.toList());
                  if (!curReportFileList.isEmpty()) {
                     List<FilePathDTO> fileRepresentations = curReportFileList.stream()
                        .<FilePathDTO>map(CoreFileUtils::toFilePath)
                        .collect(Collectors.toList());
                     List<FilePathDTO> validFileRepresentations = fileRepresentations.stream()
                        .filter(fr -> FilePathManager.getLocalDirPath(fr).toFile().exists())
                        .collect(Collectors.toList());
                     if (!validFileRepresentations.isEmpty()) {
                        String projectReportPdfName = "采样记录-" + report.getReportNo() + ".pdf";
                        FilePathDTO composedPdfFile = FilePathDTO.of(
                           FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName
                        );
                        this.coreFileService.mergePdfList(validFileRepresentations, composedPdfFile);
                        String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
                        if (!StringUtils.isEmpty(tempFilePath)) {
                           CoreFileBean coreFileBean = new CoreFileBean();
                           coreFileBean.setTargetId("T_EMC_REPORT$" + report.getId());
                           coreFileBean.setScope(FileScope.secure.name());
                           coreFileBean.setExt("sampleRecordPdf");
                           this.coreFileService.upload(coreFileBean, new File(tempFilePath));
                        }
                     }
                  }
               }
            );
         }
      }
   }

   private List<CoreFileBean> composeReportRunRecordPdf(List<EmcReportBean> reportList) {
      List<Long> projIdList = reportList.stream()
         .filter(e -> !ObjectUtils.isEmpty(e.getProjId()))
         .<Long>map(EmcReportBean::getProjId)
         .distinct()
         .collect(Collectors.toList());
      List<CoreFileBean> existFilesFilter = reportList.stream().map(report -> {
         CoreFileBean coreFileBean = new CoreFileBean();
         coreFileBean.setTargetId("T_EMC_REPORT$" + report.getId());
         coreFileBean.setExt("analysisRecordPdf");
         return coreFileBean;
      }).distinct().collect(Collectors.toList());
      List<CoreFileBean> existFiles = this.coreFileService
         .getDao()
         .selectList(existFilesFilter, Arrays.asList("TARGETID", "EXT"), Collections.emptyList(), new Order[0]);
      if (!CollectionUtils.isEmpty(existFiles)) {
         return existFiles;
      } else {
         List<EmcOrderTaskBean> orderTaskList = this.orderTaskService
            .selectListByFilter(
               SearchFilter.instance().match("projid", projIdList).filter(MatchPattern.OR).match("analysisType", "analysis").filter(MatchPattern.EQ),
               new Order[0]
            );
         List<EmcOrderTaskBean> distinctOrderTaskList = orderTaskList.stream()
            .filter(e -> !ObjectUtils.isEmpty(e.getProjId()) && !ObjectUtils.isEmpty(e.getRunId()))
            .collect(
               Collectors.collectingAndThen(
                  Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProjId() + "#" + o.getRunId()))), ArrayList::new
               )
            );
         List<CoreFileBean> fileFilters = new ArrayList<>();
         distinctOrderTaskList.forEach(r -> {
            CoreFileBean coreFileBean = new CoreFileBean();
            coreFileBean.setTargetId("T_EMC_RUN$" + r.getRunId() + "-" + r.getProjId());
            coreFileBean.setExt("composePdf");
            fileFilters.add(coreFileBean);
         });
         List<CoreFileBean> allRunFileList = this.coreFileService
            .getDao()
            .selectList(fileFilters, Arrays.asList("TARGETID", "EXT"), Collections.emptyList(), new Order[0]);
         if (org.thymeleaf.util.ListUtils.isEmpty(allRunFileList)) {
            return Collections.emptyList();
         } else {
            List<Long> fileIdList = new ArrayList<>();
            reportList.forEach(
               report -> {
                  Long projId = report.getProjId();
                  List<CoreFileBean> curReportFileList = allRunFileList.stream()
                     .filter(file -> StringUtils.contains(file.getTargetId(), projId + ""))
                     .collect(Collectors.toList());
                  if (!curReportFileList.isEmpty()) {
                     List<FilePathDTO> fileRepresentations = curReportFileList.stream()
                        .<FilePathDTO>map(CoreFileUtils::toFilePath)
                        .collect(Collectors.toList());
                     List<FilePathDTO> validFileRepresentations = fileRepresentations.stream()
                        .filter(fr -> FilePathManager.getLocalDirPath(fr).toFile().exists())
                        .collect(Collectors.toList());
                     if (!validFileRepresentations.isEmpty()) {
                        String projectReportPdfName = "分析记录-" + report.getReportNo() + ".pdf";
                        FilePathDTO composedPdfFile = FilePathDTO.of(
                           FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName
                        );
                        this.coreFileService.mergePdfList(validFileRepresentations, composedPdfFile);
                        String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
                        if (!StringUtils.isEmpty(tempFilePath)) {
                           CoreFileBean coreFileBean = new CoreFileBean();
                           coreFileBean.setTargetId("T_EMC_REPORT$" + report.getId());
                           coreFileBean.setScope(FileScope.secure.name());
                           coreFileBean.setExt("analysisRecordPdf");
                           fileIdList.add(this.coreFileService.upload(coreFileBean, new File(tempFilePath)));
                        }
                     }
                  }
               }
            );
            return this.coreFileService.getDao().selectListByIds(fileIdList, new Order[0]);
         }
      }
   }

   @ValidateDataWith(PfViewReportValidator.class)
   public Long viewQcReport(RestJsonWrapperBean wrapper) {
      EmcReportBean report = (EmcReportBean)wrapper.parseUnique(EmcReportBean.class);
      Long projectId = report.getProjId();
      String targetId = this.getDao().getTable() + "$" + report.getId();
      List<CoreFileBean> wordReportFileList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.SEQ).match("bizCategory", "qcReport").filter(MatchPattern.EQ), new Order[0]
         );
      EmcProjectBean project = (EmcProjectBean)this.projectService
         .selectFirstByFilter(SearchFilter.instance().match("id", projectId).filter(MatchPattern.SEQ), new Order[0]);
      List<EmcReportBean> reportList = Collections.singletonList(report);
      if (!wordReportFileList.isEmpty()) {
         List<CoreFileBean> updateCoreFileList = new ArrayList<>();
         wordReportFileList.forEach(e -> {
            CoreFileBean updateCoreFile = new CoreFileBean();
            updateCoreFile.setId(e.getId());
            updateCoreFile.setTargetId("X" + e.getTargetId());
            updateCoreFileList.add(updateCoreFile);
         });
         this.coreFileService.getDao().update(updateCoreFileList, new String[]{"TARGETID"});
         CoreFileBean wordTemplateConfigFile = wordReportFileList.get(0);
         File templateFile = FilePathManager.getLocalPath(
               FilePathDTO.of(
                  wordTemplateConfigFile.getScope(),
                  DateTimeUtils.getDateSerialNumber(wordTemplateConfigFile.getCreatedTime()),
                  wordTemplateConfigFile.getMd5Name(),
                  wordTemplateConfigFile.getName()
               )
            )
            .toFile();
         this.generateWordReport(templateFile, templateFile, report, project, true);
         List<CoreFileBean> newCoreFileList = this.saveWordReportCoreFile(wordTemplateConfigFile.getName(), templateFile, reportList, "qcReport");
         return newCoreFileList.get(0).getId();
      } else {
         long reportConfigLineId = NumberUtils.parseLong(report.getExt$Item("qcReportLineId"));
         List<CoreFileBean> wordTemplateConfigFileList = this.coreFileService
            .getDao()
            .selectListByOneColumnValue("T_CORE_REPORT_CONFIG_LINE$" + reportConfigLineId, "targetId", new Order[0]);
         if (!wordTemplateConfigFileList.isEmpty()) {
            CoreFileBean wordTemplateConfigFile = wordTemplateConfigFileList.get(0);
            String wordReportFileName = FileUtils.getFileBaseName(wordTemplateConfigFile.getName())
               + "-"
               + report.getReportNo()
               + "."
               + FileUtils.getFileExtension(wordTemplateConfigFile.getName());
            File templateFile = FilePathManager.getLocalPath(
                  FilePathDTO.of(wordTemplateConfigFile.getScope(), "", wordTemplateConfigFile.getExt(), wordTemplateConfigFile.getName())
               )
               .toFile();
            File wordReportFile = FilePathManager.getLocalPath(
                  FilePathDTO.of(wordTemplateConfigFile.getScope(), "", wordTemplateConfigFile.getExt(), wordReportFileName)
               )
               .toFile();
            this.generateWordReport(templateFile, wordReportFile, report, project, false);
            List<CoreFileBean> newCoreFileList = this.saveWordReportCoreFile(wordReportFileName, wordReportFile, reportList, "qcReport");
            return newCoreFileList.get(0).getId();
         } else {
            return 0L;
         }
      }
   }

   public RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper) {
      List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class);
      List<Long> reportIdList = reportList.stream().<Long>map(EmcReportBean::getId).collect(Collectors.toList());
      List<EmcReportLineBean> reportLineList = this.reportLineService
         .getDao()
         .selectListByOneColumnValues(reportIdList, "reportId", Arrays.asList("id", "orderTaskId"), new Order[0]);
      List<Long> reportTaskIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
      List<List<Long>> naPartitionList = ListUtils.partition(reportTaskIdList, 200);
      List<EmcOrderTaskBean> orderTaskList = new ArrayList<>();
      IntStream.range(0, naPartitionList.size())
         .forEach(
            index -> {
               List<Long> curNaPartitionList = naPartitionList.get(index);
               List<EmcOrderTaskBean> taskList = this.orderTaskService
                  .getDao()
                  .selectListByOneColumnValues(curNaPartitionList, "id", Arrays.asList("id", "runId"), new Order[0]);
               orderTaskList.addAll(taskList);
            }
         );
      List<String> runIdList = orderTaskList.stream()
         .filter(ok -> !StringUtils.isEmpty(ok.getRunId()))
         .<String>map(EmcOrderTaskBean::getRunId)
         .distinct()
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> orderTaskResultList = this.orderTaskResultService
         .getDao()
         .selectListByOneColumnValues(
            runIdList, "runId", Arrays.asList("ID", "orderNo", "reportResult", "resultStatus", "qcCategoryCode", "lowLimit"), new Order[0]
         );
      if (orderTaskResultList.isEmpty()) {
         return new RestValidationResultBean(true);
      } else {
         List<EmcOrderTaskResultBean> oosResultList = orderTaskResultList.stream()
            .filter(result -> StringUtils.equals("OOS", result.getResultStatus()))
            .collect(Collectors.toList());
         List<String> blankQcList = Arrays.asList("P-BLANK", "L-BLANK", "P-YSKB", "zcsbkb");
         List<EmcOrderTaskResultBean> nonNdResultList = orderTaskResultList.stream()
            .filter(r -> blankQcList.contains(r.getQcCategoryCode()) && !StringUtils.isEmpty(r.getLowLimit()))
            .filter(r -> !StringUtils.equals(r.getReportResult(), "ND"))
            .collect(Collectors.toList());
         if (oosResultList.isEmpty() && nonNdResultList.isEmpty()) {
            return new RestValidationResultBean(true);
         } else {
            List<String> messages = new ArrayList<>();
            if (!oosResultList.isEmpty()) {
               String orderNos = oosResultList.stream().<CharSequence>map(EmcOrderTaskResultBean::getOrderNo).distinct().collect(Collectors.joining("、"));
               messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.RESULT_OOS", new String[]{orderNos}));
            }

            if (!nonNdResultList.isEmpty()) {
               String orderNos = nonNdResultList.stream().<CharSequence>map(EmcOrderTaskResultBean::getOrderNo).distinct().collect(Collectors.joining("、"));
               messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.RESULT_NON_ND", new String[]{orderNos}));
            }

            messages.add(I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.CONTINUE_GENERATE_QC_REPORT", new String[0]));
            return new RestValidationResultBean(false, String.join("，", messages));
         }
      }
   }

   public void generateWordReport(File templateFile, File wordReportFile, EmcReportBean report, EmcProjectBean project, boolean exist) {
      File wordTemp = FileUtils.createTempFile("docx");

      try (FileInputStream inputStream = new FileInputStream(templateFile)) {
         XWPFDocument document = this.createReportDocument(inputStream);
         Throwable var10 = null;

         try (FileOutputStream outputStream = new FileOutputStream(wordTemp)) {
            if (!exist) {
               this.writeDataToReport2024(templateFile, document, report, project);
            }

            this.setSubmitInformation(document, report);
            document.write(outputStream);
         } catch (Throwable var61) {
            var10 = var61;
            throw var61;
         } finally {
            if (document != null) {
               if (var10 != null) {
                  try {
                     document.close();
                  } catch (Throwable var57) {
                     var10.addSuppressed(var57);
                  }
               } else {
                  document.close();
               }
            }
         }
      } catch (IOException var65) {
         throw new FileException(var65);
      }

      this.changeFont(wordTemp, wordReportFile);
   }

   public void writeDataToReport2024(File templateFile, XWPFDocument document, EmcReportBean report, EmcProjectBean project) {
      List<EmcReportLineBean> reportLineList = this.reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId", new Order[0]);
      List<Long> reportFolderIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getFolderId).distinct().collect(Collectors.toList());
      List<Long> reportOrderTaskIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderTaskId).collect(Collectors.toList());
      if (!reportFolderIdList.isEmpty() && !reportOrderTaskIdList.isEmpty()) {
         List<EmcFolderBean> allFolderList = this.folderService
            .selectListByFilter(
               SearchFilter.instance().match("id", reportFolderIdList).filter(MatchPattern.OR).match("termination", "0").filter(MatchPattern.EQ), new Order[0]
            );
         List<Long> allFolderIdList = allFolderList.stream().<Long>map(EmcFolderBean::getId).collect(Collectors.toList());
         List<EmcSampleBean> sampleList = this.sampleService
            .getDao()
            .selectListByOneColumnValue(
               project.getId(),
               "PROJID",
               Arrays.asList(
                  "ID", "SAMPLINGEQUIPTID", "SAMPLINGEQUIPTNAME", "ACCEPTEDDATE", "ACCEPTEUSERNAME", "ORDERCATEGORY", "PROPERTYTMPLID", "SAMPLEUSERNAME"
               ),
               new Order[0]
            );
         List<String> qcCategoryCodeList = new ArrayList<>();
         qcCategoryCodeList.add("N/A");
         String bizCategoryCode = project.getBizCategoryCode();
         boolean isDioxin = StringUtils.equalsIgnoreCase(bizCategoryCode, "C");
         if (isDioxin) {
            qcCategoryCodeList.add("P-DUP");
         }

         boolean sampling = StringUtils.equals("1", project.getSample());
         List<EmcOrderBean> orderList = sampling
            ? this.orderService
               .selectListByFilter(
                  SearchFilter.instance()
                     .match("folderId", allFolderIdList)
                     .filter(MatchPattern.OR)
                     .match("termination", "0")
                     .filter(MatchPattern.EQ)
                     .match("QCCATEGORYCODE", qcCategoryCodeList)
                     .filter(MatchPattern.OR),
                  new Order[0]
               )
            : this.orderService.getDao().selectListByOneColumnValues(allFolderIdList, "folderId", new Order[0]);
         List<Long> folderIdList = orderList.stream().<Long>map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
         List<EmcFolderBean> folderList = allFolderList.stream().filter(f -> folderIdList.contains(f.getId())).collect(Collectors.toList());
         List<EmcOrderTaskBean> orderTaskList = this.orderTaskService
            .selectListByFilter(SearchFilter.instance().match("id", reportOrderTaskIdList).filter(MatchPattern.OR), new Order[0]);
         List<EmcOrderTaskResultBean> orderTaskResultList = this.orderTaskResultService
            .selectListByFilter(SearchFilter.instance().match("orderTaskId", reportOrderTaskIdList).filter(MatchPattern.OR), new Order[0]);
         List<String> runIdList = orderTaskList.stream()
            .filter(a -> !StringUtils.isEmpty(a.getRunId()))
            .<String>map(EmcOrderTaskBean::getRunId)
            .distinct()
            .collect(Collectors.toList());
         List<Long> testMethodIdList = orderTaskList.stream()
            .filter(task -> !ObjectUtils.isEmpty(task.getTestMethodId()))
            .<Long>map(EmcOrderTaskBean::getTestMethodId)
            .distinct()
            .collect(Collectors.toList());
         List<EmcTestCapacityBean> testCapacityList = this.testCapacityService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("ORGID", project.getOrgId())
                  .filter(MatchPattern.SEQ)
                  .match("TESTMETHODID", testMethodIdList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         List<EmcItemGroupBean> itemGroupList = this.itemGroupService
            .getDao()
            .selectListByOneColumnValues(testMethodIdList, "testMethodId", Arrays.asList("ID", "TESTMETHODID", "ITEMGROUP", "ACTIVATEDFLAG"), new Order[0]);
         List<Long> itemGroupIdList = itemGroupList.stream()
            .filter(group -> "1".equals(group.getActivatedFlag()))
            .<Long>map(EmcItemGroupBean::getId)
            .collect(Collectors.toList());
         List<EmcGroupItemBean> groupItemList = this.groupItemService
            .getDao()
            .selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Arrays.asList("ITEMGROUPID", "ITEMID"), new Order[0]);
         Map<Long, String> itemGroupMap = itemGroupList.stream()
            .filter(g -> !StringUtils.isEmpty(g.getItemGroup()))
            .collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));
         Map<Long, Integer> itemGroupSortMap = itemGroupList.stream()
            .filter(g -> g.getSortNo() != null)
            .collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getSortNo, (v1, v2) -> v1));
         List<EmcRunBean> runList = new ArrayList<>();
         if (!runIdList.isEmpty()) {
            runList = this.runService.selectListByFilter(SearchFilter.instance().match("Id", runIdList).filter(MatchPattern.OR), new Order[0]);
         }

         List<String> dataTypeList = Arrays.asList("sample", "dayaverage", "totalaverage");
         List<EmcReportResultBean> reportBeforeResultList = this.reportResultService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("reportId", report.getId())
                  .filter(MatchPattern.SEQ)
                  .match("reportDisplay", "1")
                  .filter(MatchPattern.EQ)
                  .match("DATATYPE", dataTypeList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         ArrayList<EmcReportResultBean> reportResultList = reportBeforeResultList.stream()
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(r -> r.getId()))), ArrayList::new));
         List<EmcTestMethodBean> testMethodList = this.testMethodService
            .getDao()
            .selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "reportLimitType"), new Order[0]);
         testMethodList.forEach(
            testMethod -> reportResultList.stream()
                  .filter(r -> ObjectUtils.equals(r.getTestMethodId(), testMethod.getId()))
                  .forEach(r -> r.setExt$Item("reportLimitType", testMethod.getReportLimitType()))
         );
         testCapacityList.forEach(
            testCapacity -> reportResultList.stream()
                  .filter(r -> ObjectUtils.equals(r.getTestMethodId(), testCapacity.getTestMethodId()))
                  .forEach(r -> r.setExt$Item("reportTestName", testCapacity.getReportTestName()))
         );
         reportResultList.stream()
            .filter(r -> StringUtils.isEmpty(r.getExt$Item("reportTestName")))
            .forEach(r -> r.setExt$Item("reportTestName", r.getTestName()));
         reportResultList.stream()
            .filter(r -> "1".equals(r.getReportDisplay()))
            .forEach(r -> groupItemList.stream().filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId())).findFirst().ifPresent(g -> {
                  r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId()));
                  if (itemGroupSortMap.get(g.getItemGroupId()) != null) {
                     r.setExt$Item("itemGroupSort", itemGroupSortMap.get(g.getItemGroupId()) + "");
                  } else {
                     r.setExt$Item("itemGroupSort", "0");
                  }
               }));
         reportResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("itemGroup"))).forEach(r -> r.setExt$Item("itemGroup", r.getTestName()));
         reportResultList.stream()
            .filter(r -> !StringUtils.isEmpty(r.getExt$Item("testmethodbasis")))
            .forEach(
               r -> {
                  Long testMethodId = r.getTestMethodId();
                  EmcTestCapacityBean testCapacity = testCapacityList.stream()
                     .filter(e -> ObjectUtils.equals(e.getTestMethodId(), testMethodId))
                     .findFirst()
                     .orElse(new EmcTestCapacityBean());
                  String testMethodBasis = r.getExt$Item("testmethodbasis");
                  String methodScope = testCapacity.getExt$Item("methodscope");
                  if (StringUtils.isBlank(testMethodBasis)) {
                     testMethodBasis = methodScope;
                  } else if (!StringUtils.isBlank(methodScope)) {
                     testMethodBasis = testMethodBasis + " " + methodScope;
                  }

                  r.setExt$Item("newtestmethodbasis", testMethodBasis);
               }
            );
         List<String> monitorTmplIdList = orderList.stream()
            .filter(f -> !StringUtils.isEmpty(f.getMonitorTmplId()))
            .<String>map(EmcOrderBean::getMonitorTmplId)
            .distinct()
            .collect(Collectors.toList());
         List<EmcMonitorTmplBean> monitorTmplList = this.monitorTmplService.getDao().selectListByIds(monitorTmplIdList, new Order[0]);
         List<Long> sampleIdList = reportResultList.stream().<Long>map(EmcReportResultBean::getSampleId).distinct().collect(Collectors.toList());
         List<EmcPropertyDataBean> propertyDataList = this.propertyDataService.getDao().selectListByOneColumnValues(sampleIdList, "bizId", new Order[0]);
         List<Long> propertyTmplIdList = propertyDataList.stream()
            .filter(e -> e.getTmplId() != null)
            .map(e -> e.getTmplId())
            .distinct()
            .collect(Collectors.toList());
         List<EmcPropertyTmplBean> propertyTmplList = this.propertyTmplService.getDao().selectListByOneColumnValues(propertyTmplIdList, "Id", new Order[0]);
         List<EmcPropertyFieldBean> propertyFieldList = this.propertyFieldService
            .getDao()
            .selectListByOneColumnValues(propertyTmplIdList, "tmplId", new Order[0]);
         List<String> targetIdList = sampleIdList.stream().map(i -> "T_EMC_SAMPLE$" + i).collect(Collectors.toList());
         List<CoreFileBean> coreFileList = new ArrayList<>();
         if (!targetIdList.isEmpty()) {
            coreFileList = this.coreFileService
               .selectListByFilter(
                  SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR).match("BIZCATEGORY", "pointDiagram").filter(MatchPattern.EQ),
                  new Order[0]
               );
         }

         List<EmcReportBean> reportList = this.getDao().selectListByOneColumnValue(project.getId(), "PROJID", new Order[0]);
         List<String> sampleEquipmentIdList = orderTaskList.stream()
            .filter(task -> !StringUtils.isEmpty(task.getSamplingEquiptId()))
            .<String>map(EmcOrderTaskBean::getSamplingEquiptId)
            .collect(Collectors.toList());
         List<Long> equiptIdList = orderTaskList.stream()
            .filter(task -> !ObjectUtils.isEmpty(task.getEquiptId()))
            .<Long>map(EmcOrderTaskBean::getEquiptId)
            .collect(Collectors.toList());
         List<Long> parsedEquiptIdList = this.parseMultiEquipt(sampleEquipmentIdList);
         parsedEquiptIdList.addAll(equiptIdList);
         List<String> sampleAssistEquiptIdList = orderTaskList.stream()
            .filter(task -> !ObjectUtils.isEmpty(task.getAssistEquiptId()))
            .<String>map(EmcOrderTaskBean::getAssistEquiptId)
            .collect(Collectors.toList());
         parsedEquiptIdList.addAll(this.parseMultiEquipt(sampleAssistEquiptIdList));
         List<Long> runEquiptIdList = runList.stream()
            .filter(r -> !ObjectUtils.isEmpty(r.getEquiptId()))
            .<Long>map(EmcRunBean::getEquiptId)
            .collect(Collectors.toList());
         parsedEquiptIdList.addAll(runEquiptIdList);
         List<String> weighingEquiptIdList = runList.stream()
            .filter(run -> !StringUtils.isEmpty(run.getAssistEquiptId()))
            .map(run -> run.getAssistEquiptId())
            .distinct()
            .collect(Collectors.toList());
         parsedEquiptIdList.addAll(this.parseMultiEquipt(weighingEquiptIdList));
         List<EmcEquiptBean> equiptList = this.equiptService
            .selectListByFilter(SearchFilter.instance().match("ID", parsedEquiptIdList).filter(MatchPattern.OR), new Order[0]);
         CoreUserBean loginUser = LocalContextHelper.getLoginUser();
         String parentOrgId = loginUser.getExt$Item("parentOrgId");
         CoreOrgBean coreOrg = (CoreOrgBean)this.coreOrgService.selectById(parentOrgId);
         List<CoreFileBean> logoFileList = this.coreFileService.getDao().selectListByOneColumnValue("T_CORE_ORG$" + parentOrgId, "targetId", new Order[0]);
         if (!logoFileList.isEmpty()) {
            logoFileList.forEach(file -> file.setExt$Item("orgLogo", "1"));
            coreFileList.addAll(logoFileList);
         }

         List<CoreFileBean> qrCodeFileList = this.coreFileService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("targetId", "T_EMC_PROJECT$" + project.getId())
                  .filter(MatchPattern.SEQ)
                  .match("bizCategory", Arrays.asList("qrCode", "photo"))
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         if (!qrCodeFileList.isEmpty()) {
            coreFileList.addAll(qrCodeFileList);
         }

         List<CoreCodeBean> pretreatmentCodeList = this.coreCodeService
            .getDao()
            .selectListByOneColumnValue("pretreatmentRemark", "codeCategoryId", new Order[0]);
         pretreatmentCodeList.stream()
            .filter(c -> StringUtils.equals(c.getCode(), project.getPretreatment()))
            .findFirst()
            .ifPresent(c -> project.setPretreatment(c.getCodeName()));
         List<EmcMonitorSchemeBean> monitorSchemeList = this.monitorSchemeService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("monitorTmplId", monitorTmplIdList)
                  .filter(MatchPattern.OR)
                  .match("schemeName", "default")
                  .filter(MatchPattern.SEQ)
                  .match("testMethodId", testMethodIdList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         WordTemplateSourceDTO templateSourceDTO = new WordTemplateSourceDTO();
         templateSourceDTO.setProject(project);
         templateSourceDTO.setFolderList(folderList);
         templateSourceDTO.setSampleList(sampleList);
         templateSourceDTO.setOrderList(orderList);
         templateSourceDTO.setOrderTaskList(orderTaskList);
         templateSourceDTO.setOrderTaskResultList(orderTaskResultList);
         templateSourceDTO.setItemGroupList(itemGroupList);
         templateSourceDTO.setGroupItemList(groupItemList);
         templateSourceDTO.setTestCapacityList(testCapacityList);
         templateSourceDTO.setRunList(runList);
         templateSourceDTO.setReport(report);
         templateSourceDTO.setReportList(reportList);
         templateSourceDTO.setReportResultList(reportResultList);
         templateSourceDTO.setMonitorTmplList(monitorTmplList);
         templateSourceDTO.setPropertyDataList(propertyDataList);
         templateSourceDTO.setPropertyTmplList(propertyTmplList);
         templateSourceDTO.setPropertyFieldList(propertyFieldList);
         templateSourceDTO.setEquiptList(equiptList);
         templateSourceDTO.setCoreFileList(coreFileList);
         templateSourceDTO.setCoreOrg(coreOrg);
         new CrmOpportunityMainBean();
         List<CrmOpportunityContractBean> opportunityContractList = this.opportunityContractService
            .getDao()
            .selectListByOneColumnValue(project.getExt$Item("contractNo"), "contractNo", Arrays.asList("id", "opportunityId"), new Order[0]);
         if (!opportunityContractList.isEmpty()) {
            CrmOpportunityMainBean crmOpportunityMain = (CrmOpportunityMainBean)this.opportunityMainService
               .getDao()
               .selectById(opportunityContractList.get(0).getOpportunityId());
            templateSourceDTO.setOpportunityMain(crmOpportunityMain);
         }

         List<PfRunTempHumidityBean> filterTempList = new ArrayList<>(runIdList.size());
         runIdList.forEach(runId -> {
            PfRunTempHumidityBean filterTemp = new PfRunTempHumidityBean();
            filterTemp.setRunId(runId);
            filterTemp.setProjId(project.getId());
            filterTempList.add(filterTemp);
         });
         List<PfRunTempHumidityBean> tempHumidityList = this.tempHumidityService
            .getDao()
            .selectList(filterTempList, Arrays.asList("runId", "projId"), Collections.emptyList(), new Order[0]);
         templateSourceDTO.setTempHumidityList(tempHumidityList);
         Map<String, Object> dataMap = new HashMap<>();
         LocalDateTime localDate = LocalDateTime.now();
         dataMap.put("reportNo", report.getReportNo());
         String detectCategory = project.getExt$Item("bizcategoryname");
         if ("监督性检测/临时性检测".equals(detectCategory)) {
            detectCategory = StringUtils.contains(project.getEntrustOrgName(), "新北") ? "临时性监测" : "监督性检测";
         }

         dataMap.put("detectCategory", detectCategory);
         List<String> orderCategoryList = reportResultList.stream()
            .filter(e -> e.getOrderCategory() != null)
            .map(e -> e.getOrderCategory())
            .distinct()
            .collect(Collectors.toList());
         dataMap.put("detectType", StringUtils.join(orderCategoryList, ","));
         dataMap.put("entrustOrgName", project.getEntrustOrgName());
         dataMap.put("entrustOrgContact", project.getEntrustOrgContact());
         dataMap.put("entrustOrgPhone", project.getEntrustOrgPhone());
         String testProjectName = project.getTestProjectName();
         String testOrgName = project.getTestOrgName();
         String realProjectTitle = "";
         String realProjectName = "";
         if (!StringUtils.isBlank(testProjectName)) {
            realProjectTitle = "项目名称";
            realProjectName = testProjectName;
         } else {
            realProjectTitle = "受检单位";
            realProjectName = testOrgName;
         }

         dataMap.put("testProjectName", testProjectName);
         dataMap.put("realProjectTitle", realProjectTitle);
         dataMap.put("realProjectName", realProjectName);
         dataMap.put("testOrgName", testOrgName);
         dataMap.put("testOrgContact", project.getTestOrgContact());
         dataMap.put("testOrgPhone", project.getTestOrgPhone());
         dataMap.put("detectPurpose", project.getDetectPurpose());
         String remark = "“ND”表示未检出，即检测结果低于检出限。";
         dataMap.put("projectRemark", remark);
         dataMap.put("sampleDate", this.getSampleDate(project, orderList));
         dataMap.put(
            "analysisDate",
            !StringUtils.isEmpty(this.getAnalysisDate(templateSourceDTO)) ? this.getAnalysisDate(templateSourceDTO) : this.getSampleDate(project, orderList)
         );
         dataMap.put("controlNumber", coreOrg.getExt$Item("controlNumber"));
         dataMap.put("samplePlace", project.getTestOrgAddress());
         if (sampling) {
            this.getSampleUserNameNew(sampleList, dataMap);
         } else {
            this.getSendUserName(orderList, dataMap);
         }

         this.mapNullToString(dataMap, "");
         Word2024Utils.replaceAll(document, dataMap);
         String var71 = templateFile.getName();
         switch (var71) {
            case "综合报告.docx":
               this.queryIndexCategory(templateSourceDTO);
               this.initQcReportDataSource(templateSourceDTO);
               if (!"1".equals(project.getSample())) {
                  templateSourceDTO.getFolderList()
                     .forEach(
                        folder -> templateSourceDTO.getOrderList()
                              .stream()
                              .filter(order -> ObjectUtils.equals(order.getFolderId(), folder.getId()))
                              .findFirst()
                              .ifPresent(order -> folder.setMonitorTmplId(order.getMonitorTmplId()))
                     );
               }

               this.createSampleTestEquiptTable(document, templateSourceDTO);
               this.createSampleResultTable(document, templateSourceDTO);
               this.createResultDescTable(document, templateSourceDTO, false);
               createPointImage(document, coreFileList);
               this.createCnasImage(document, report.getReportAptitude());
               this.insertStampImage(report, document, false);
               break;
            case "综合报告（送样）.docx":
               this.queryIndexCategory(templateSourceDTO);
               this.initQcReportDataSource(templateSourceDTO);
               if (!"1".equals(project.getSample())) {
                  templateSourceDTO.getFolderList()
                     .forEach(
                        folder -> templateSourceDTO.getOrderList()
                              .stream()
                              .filter(order -> ObjectUtils.equals(order.getFolderId(), folder.getId()))
                              .findFirst()
                              .ifPresent(order -> folder.setMonitorTmplId(order.getMonitorTmplId()))
                     );
               }

               this.createSendTestEquiptTable(document, templateSourceDTO);
               this.createSendResultTable(document, templateSourceDTO);
               this.createResultDescTable(document, templateSourceDTO, true);
               createPointImage(document, coreFileList);
               this.createCnasImage(document, report.getReportAptitude());
               this.insertStampImage(report, document, false);
            case "二噁英报告.docx":
            case "二噁英质控报告.docx":
         }
      }
   }

   private void insertStampImage(EmcReportBean report, XWPFDocument document, Boolean type) {
      String lims_server_url = ApplicationContextHelper.getConstantValue("LIMS_DOMAIN_URL");
      String QRUrl = lims_server_url + "/open/emc/module/bp/report/reports/page/select-report-test-conclusion?reportId=" + report.getId();
      File file = QRCodeGenerator.getQrPic("报告结论.png", QRUrl, null, 100, 100, 0);
      XWPFTable table = (XWPFTable)document.getTables().get(2);
      Word2024Utils.insertContentAndImageToTable(table, 1, file.getAbsolutePath(), null, 12, "宋体");
   }

   private static void createPointImage(XWPFDocument document, List<CoreFileBean> coreFileList) {
      coreFileList = coreFileList.stream().filter(s -> "pointDiagram".equals(s.getBizCategory())).collect(Collectors.toList());
      if (!coreFileList.isEmpty()) {
         CoreFileBean f = coreFileList.get(0);
         String pointDiagramUrl = CoreFileUtils.getLocalPath(f).toString();
         Word2024Utils.insertImageAfterParagraphOfKey("附图：检测布点平面示意图", document, pointDiagramUrl);
      } else {
         Word2024Utils.deleteParagraph(document, "附图：检测布点平面示意图");
      }
   }

   private void createCnasImage(XWPFDocument document, String reportAptitude) {
      if (!StringUtils.isBlank(reportAptitude)) {
         String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
         List<CoreFileBean> allFileList = this.coreFileService.selectFileList("T_CORE_ORG", parentOrgId);
         if ("1".equals(reportAptitude)) {
            this.insertCnasImage(document, allFileList, "CMA", 4);
         } else if ("2".equals(reportAptitude)) {
            this.insertCnasImage(document, allFileList, "CNAS", 4);
         } else if ("3".equals(reportAptitude)) {
            this.insertCnasImage(document, allFileList, "CNAS", 3);
            this.insertCnasImage(document, allFileList, "CMA", 4);
         }
      }
   }

   private void insertCnasImage(XWPFDocument document, List<CoreFileBean> allFileList, String bizCategory, int cellNum) {
      CoreFileBean file = allFileList.stream().filter(e -> ObjectUtils.equals(bizCategory, e.getBizCategory())).findFirst().orElse(null);
      if (file != null) {
         String fileUrl = CoreFileUtils.getLocalPath(file).toString();
         if (new File(fileUrl).exists()) {
            XWPFTable table = (XWPFTable)document.getTables().get(0);
            Word2024Utils.insertContentAndImageToTable(table, cellNum, fileUrl, null, 12, "宋体");
         }
      }
   }

   private void getSampleUserName(List<EmcFolderBean> folderList, Map<String, Object> dataMap) {
      List<String> sampleUserNamesList = folderList.stream()
         .filter(e -> e.getSampleUserName() != null)
         .map(e -> e.getSampleUserName())
         .distinct()
         .collect(Collectors.toList());
      List<String> sampleUserNameList = new ArrayList<>();

      for (String names : sampleUserNamesList) {
         sampleUserNameList.addAll(Arrays.asList(StringUtils.split(names, ",")));
      }

      sampleUserNameList = sampleUserNameList.stream().distinct().collect(Collectors.toList());
      if (!sampleUserNameList.isEmpty()) {
         dataMap.put("sampleUserName", StringUtils.join(sampleUserNameList, ","));
      } else {
         dataMap.put("sampleUserName", "");
      }
   }

   private void getSampleUserNameNew(List<EmcSampleBean> sampleList, Map<String, Object> dataMap) {
      List<String> sampleUserNamesList = sampleList.stream()
         .filter(e -> e.getExt$Item("sampleusername") != null)
         .map(e -> e.getExt$Item("sampleusername"))
         .distinct()
         .collect(Collectors.toList());
      List<String> sampleUserNameList = new ArrayList<>();

      for (String names : sampleUserNamesList) {
         sampleUserNameList.addAll(Arrays.asList(StringUtils.split(names, ",")));
      }

      sampleUserNameList = sampleUserNameList.stream().distinct().collect(Collectors.toList());
      if (!sampleUserNameList.isEmpty()) {
         dataMap.put("sampleUserName", StringUtils.join(sampleUserNameList, ","));
      } else {
         dataMap.put("sampleUserName", "");
      }
   }

   private void getSendUserName(List<EmcOrderBean> orderList, Map<String, Object> dataMap) {
      List<String> sampleUserNamesList = orderList.stream()
         .filter(e -> e.getExt$Item("sender") != null)
         .map(e -> e.getExt$Item("sender"))
         .distinct()
         .collect(Collectors.toList());
      List<String> sampleUserNameList = new ArrayList<>();

      for (String names : sampleUserNamesList) {
         sampleUserNameList.addAll(Arrays.asList(StringUtils.split(names, ",")));
      }

      sampleUserNameList = sampleUserNameList.stream().distinct().collect(Collectors.toList());
      if (!sampleUserNameList.isEmpty()) {
         dataMap.put("sampleUserName", StringUtils.join(sampleUserNameList, ","));
      } else {
         dataMap.put("sampleUserName", "");
      }
   }

   private void getSamplePlace(List<EmcFolderBean> folderList, Map<String, Object> dataMap) {
      EmcFolderBean folder = folderList.stream().filter(e -> e.getMonitorPointName() != null).findFirst().orElse(null);
      if (folder != null) {
         dataMap.put("samplePlace", folder.getMonitorPointName());
      }
   }

   private void createSampleTestEquiptTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      this.sampleEquiptTable(document, templateSourceDTO);
      this.equiptRecordTable(document, templateSourceDTO);
      Word2024Utils.removeKey(document, "monitorTable");
   }

   private void createSendTestEquiptTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      this.sendEquiptTableNew(document, templateSourceDTO);
      this.equiptRecordTable(document, templateSourceDTO);
      Word2024Utils.removeKey(document, "monitorTable");
   }

   private void equiptRecordTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测类别");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("主要仪器");
      firstRowDataList.add("检定/校准有效期");
      firstRowDataList.add("仪器编号");
      firstRowDataList.add("仪器归属");
      tableDataList.add(firstRowDataList);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcEquiptBean> equiptList = templateSourceDTO.getEquiptList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<String> orderCategoryList = sampleReportResultList.stream()
         .filter(e -> e.getOrderCategory() != null)
         .map(e -> e.getOrderCategory())
         .distinct()
         .collect(Collectors.toList());
      List<MergedRow> mergedRowList = new ArrayList<>();
      AtomicInteger orderCategoryRowIndex = new AtomicInteger(1);
      AtomicInteger testNameRowIndex = new AtomicInteger(1);

      for (String orderCategory : orderCategoryList) {
         int orderCategoryMergedRowStartIndex = orderCategoryRowIndex.get();
         List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
            .filter(e -> StringUtils.equals(orderCategory, e.getOrderCategory()))
            .collect(Collectors.toList());
         orderCategoryReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderCategoryReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (itemGroupName, curReportResultList) -> {
               int testNameMergedRowStartIndex = testNameRowIndex.get();
               List<Long> equiptIdList = new ArrayList<>();
               List<String> sampleEquiptIdList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getExt$Item("sampleequiptid")))
                  .distinct()
                  .map(e -> e.getExt$Item("sampleequiptid"))
                  .collect(Collectors.toList());
               equiptIdList.addAll(this.parseMultiEquipt(sampleEquiptIdList));
               List<String> analysisEquiptIdList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getExt$Item("analysisequiptid")))
                  .distinct()
                  .map(e -> e.getExt$Item("analysisequiptid"))
                  .collect(Collectors.toList());
               equiptIdList.addAll(this.parseMultiEquipt(analysisEquiptIdList));
               List<String> analysisAssistEquiptIdList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getExt$Item("analysisassistequiptid")))
                  .distinct()
                  .map(e -> e.getExt$Item("analysisassistequiptid"))
                  .collect(Collectors.toList());
               equiptIdList.addAll(this.parseMultiEquipt(analysisAssistEquiptIdList));
               List<String> sampleAssistEquiptIdList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getExt$Item("sampleassisteequiptid")))
                  .distinct()
                  .map(e -> e.getExt$Item("sampleassisteequiptid"))
                  .collect(Collectors.toList());
               equiptIdList.addAll(this.parseMultiEquipt(sampleAssistEquiptIdList));

               for (EmcEquiptBean equipt : equiptList.stream().filter(e -> equiptIdList.contains(e.getId())).collect(Collectors.toList())) {
                  List<Object> equiptInfoRowDataList = new ArrayList<>();
                  equiptInfoRowDataList.add(orderCategory);
                  equiptInfoRowDataList.add(itemGroupName);
                  equiptInfoRowDataList.add(equipt.getSpecification() + " " + equipt.getEquiptName());
                  equiptInfoRowDataList.add(
                     StringUtils.isEmpty(equipt.getExt$Item("equiptrecorddate"))
                        ? ""
                        : DateTimeUtils.parseLocalDate(equipt.getExt$Item("equiptrecorddate")).toString()
                  );
                  equiptInfoRowDataList.add(equipt.getEquiptId());
                  equiptInfoRowDataList.add(equipt.getEquiptBelong());
                  tableDataList.add(equiptInfoRowDataList);
                  orderCategoryRowIndex.incrementAndGet();
                  testNameRowIndex.incrementAndGet();
               }

               int testNameMergedRowEndIndex = testNameRowIndex.get() - 1;
               mergedRowList.add(new MergedRow(1, testNameMergedRowStartIndex, testNameMergedRowEndIndex));
            }
         );
         int orderCategoryMergedRowEndIndex = orderCategoryRowIndex.get() - 1;
         mergedRowList.add(new MergedRow(0, orderCategoryMergedRowStartIndex, orderCategoryMergedRowEndIndex));
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("monitorTable");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(1200, 1500, 3200, 1700, 1300, 0));
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      wordTemplateDynamicTableDTO.setTableTitle("表 2 仪器检定/校准有效期");
      XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      equiptTable.getRow(0).setRepeatHeader(true);
   }

   private void sampleEquiptTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测类别");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测方法");
      firstRowDataList.add("检出限");
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcTestCapacityBean> testCapacityList = templateSourceDTO.getTestCapacityList();
      List<EmcEquiptBean> equiptList = templateSourceDTO.getEquiptList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<String> orderCategoryList = sampleReportResultList.stream()
         .filter(e -> e.getOrderCategory() != null)
         .map(e -> e.getOrderCategory())
         .distinct()
         .collect(Collectors.toList());
      List<MergedRow> mergedRowList = new ArrayList<>();
      AtomicInteger rowIndex = new AtomicInteger(1);

      for (String orderCategory : orderCategoryList) {
         int mergedRowStartIndex = rowIndex.get();
         List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
            .filter(e -> StringUtils.equals(orderCategory, e.getOrderCategory()))
            .collect(Collectors.toList());
         orderCategoryReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("newtestmethodbasis")));
         Map<String, List<EmcReportResultBean>> methodReportResultListMap = orderCategoryReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("newtestmethodbasis")));
         methodReportResultListMap.forEach(
            (method, methodReportResultList) -> {
               methodReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = methodReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
               itemGroupReportResultListMap.forEach(
                  (itemGroupName, curReportResultList) -> {
                     List<Object> equiptInfoRowDataList = new ArrayList<>();
                     equiptInfoRowDataList.add(orderCategory);
                     equiptInfoRowDataList.add(itemGroupName);
                     equiptInfoRowDataList.add(method);
                     if (curReportResultList.stream()
                        .anyMatch(
                           e -> "1".equals(e.getExt$Item("multipleitemgroup"))
                                 || "192".equals(e.getTestId() + "")
                                 || "总α放射性".equals(e.getTestName())
                                 || "总β放射性".equals(e.getTestName())
                        )) {
                        equiptInfoRowDataList.add("详见检测结果表");
                     } else {
                        List<EmcReportResultBean> lowLimitNotEmptyList = curReportResultList.stream()
                           .filter(e -> !StringUtils.isEmpty(e.getLowLimit()))
                           .collect(Collectors.toList());
                        EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : lowLimitNotEmptyList.get(0);
                        String limitValue = StringUtils.isBlank(limitResult.getLowLimit())
                           ? "-"
                           : limitResult.getLowLimit() + "" + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
                        equiptInfoRowDataList.add(limitValue);
                     }

                     tableDataList.add(equiptInfoRowDataList);
                     rowIndex.incrementAndGet();
                  }
               );
            }
         );
         int var17 = rowIndex.get() - 1;
      }

      tableDataList.add(0, firstRowDataList);

      for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
         int biginIndex = 1;

         for (int index = 2; index <= tableDataList.size(); index++) {
            if (index == tableDataList.size() || !ObjectUtils.equals(tableDataList.get(index).get(columnIndex), tableDataList.get(index - 1).get(columnIndex))) {
               mergedRowList.add(new MergedRow(columnIndex, biginIndex, index - 1));
               biginIndex = index;
            }
         }
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("monitorTable");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(1400, 1600, 6000, 0));
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      wordTemplateDynamicTableDTO.setTableTitle("表 1 检测方法");
      XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      equiptTable.getRow(0).setRepeatHeader(true);
   }

   @Deprecated
   private void sendEquiptTableOld(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测类别");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测方法");
      firstRowDataList.add("主要仪器");
      firstRowDataList.add("检出限");
      tableDataList.add(firstRowDataList);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcTestCapacityBean> testCapacityList = templateSourceDTO.getTestCapacityList();
      List<EmcEquiptBean> equiptList = templateSourceDTO.getEquiptList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      sampleReportResultList.stream().forEach(r -> r.setExt$Item("itemGroup", r.getTestName()));
      sampleReportResultList.sort((a, b) -> {
         if ("192".equals(a.getTestId() + "")) {
            return -1;
         } else {
            return "192".equals(b.getTestId() + "") ? 1 : 0;
         }
      });
      List<String> orderCategoryList = sampleReportResultList.stream()
         .filter(e -> e.getOrderCategory() != null)
         .map(e -> e.getOrderCategory())
         .distinct()
         .collect(Collectors.toList());
      List<MergedRow> mergedRowList = new ArrayList<>();
      AtomicInteger rowIndex = new AtomicInteger(1);

      for (String orderCategory : orderCategoryList) {
         int mergedRowStartIndex = rowIndex.get();
         List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
            .filter(e -> StringUtils.equals(orderCategory, e.getOrderCategory()))
            .collect(Collectors.toList());
         orderCategoryReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("itemGroup")));
         orderCategoryReportResultList.sort((a, b) -> {
            if ("192".equals(a.getTestId() + "")) {
               return -1;
            } else {
               return "192".equals(b.getTestId() + "") ? 1 : 0;
            }
         });
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderCategoryReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
         itemGroupReportResultListMap.forEach(
            (itemGroupName, curReportResultList) -> {
               List<Long> equiptIdList = new ArrayList<>();
               List<String> sampleEquiptIdList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getExt$Item("sampleequiptid")))
                  .distinct()
                  .map(e -> e.getExt$Item("sampleequiptid"))
                  .collect(Collectors.toList());
               equiptIdList.addAll(this.parseMultiEquipt(sampleEquiptIdList));
               List<String> analysisEquiptIdList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getExt$Item("analysisequiptid")))
                  .distinct()
                  .map(e -> e.getExt$Item("analysisequiptid"))
                  .collect(Collectors.toList());
               equiptIdList.addAll(this.parseMultiEquipt(analysisEquiptIdList));
               List<String> analysisAssistEquiptIdList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getExt$Item("analysisassistequiptid")))
                  .distinct()
                  .map(e -> e.getExt$Item("analysisassistequiptid"))
                  .collect(Collectors.toList());
               equiptIdList.addAll(this.parseMultiEquipt(analysisAssistEquiptIdList));

               for (EmcEquiptBean equipt : equiptList.stream().filter(e -> equiptIdList.contains(e.getId())).collect(Collectors.toList())) {
                  List<Object> equiptInfoRowDataList = new ArrayList<>();
                  equiptInfoRowDataList.add(orderCategory);
                  equiptInfoRowDataList.add(itemGroupName);
                  Long testMethodId = curReportResultList.get(0).getTestMethodId();
                  EmcTestCapacityBean testCapacity = testCapacityList.stream()
                     .filter(e -> ObjectUtils.equals(e.getTestMethodId(), testMethodId))
                     .findFirst()
                     .orElse(new EmcTestCapacityBean());
                  String testMethodBasis = curReportResultList.get(0).getExt$Item("testmethodbasis");
                  String methodScope = testCapacity.getExt$Item("methodscope");
                  if (StringUtils.isBlank(testMethodBasis)) {
                     testMethodBasis = methodScope;
                  } else if (!StringUtils.isBlank(methodScope)) {
                     testMethodBasis = testMethodBasis + " " + methodScope;
                  }

                  equiptInfoRowDataList.add(testMethodBasis);
                  equiptInfoRowDataList.add(equipt.getSpecification() + " " + equipt.getEquiptName());
                  if ("192".equals(curReportResultList.get(0).getTestId() + "")) {
                     int dioxinTableSize = NumberUtils.parseInt(curReportResultList.stream().map(e -> e.getOrderNo()).distinct().count(), 0);
                     equiptInfoRowDataList.add("详见表3至表" + (dioxinTableSize + 2));
                  } else {
                     List<EmcReportResultBean> lowLimitNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getLowLimit()))
                        .collect(Collectors.toList());
                     EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : lowLimitNotEmptyList.get(0);
                     String limitValue = StringUtils.isBlank(limitResult.getLowLimit())
                        ? "-"
                        : limitResult.getLowLimit() + "\n" + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
                     equiptInfoRowDataList.add(limitValue);
                  }

                  tableDataList.add(equiptInfoRowDataList);
                  rowIndex.incrementAndGet();
               }
            }
         );
         int mergedRowEndIndex = rowIndex.get() - 1;
         mergedRowList.add(new MergedRow(0, mergedRowStartIndex, mergedRowEndIndex));
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("monitorTable");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(1000, 0, 4000, 0, 0, 1000));
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      wordTemplateDynamicTableDTO.setTableTitle("表 1 检测方法及仪器");
      XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      equiptTable.getRow(0).setRepeatHeader(true);
   }

   private void sendEquiptTableNew(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<List<Object>> tableDataList2 = new ArrayList<>();
      List<EmcReportResultBean> allSendDateReportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> specReportResultList = allSendDateReportResultList.stream()
         .filter(s -> "总α放射性".equals(s.getTestName()) || "总β放射性".equals(s.getTestName()))
         .collect(Collectors.toList());
      int tableIndex = 1;
      if (!CollectionUtils.isEmpty(specReportResultList)) {
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测类别");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("检测方法");
         firstRowDataList.add("探测下限");
         this.sendEquiptTableNew(document, templateSourceDTO, tableDataList, firstRowDataList, specReportResultList, tableIndex);
         tableIndex++;
      }

      List<EmcReportResultBean> reportResultList = allSendDateReportResultList.stream()
         .filter(s -> !"总α放射性".equals(s.getTestName()) && !"总β放射性".equals(s.getTestName()))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(reportResultList)) {
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测类别");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("检测方法");
         firstRowDataList.add("检出限");
         this.sendEquiptTableNew(document, templateSourceDTO, tableDataList2, firstRowDataList, reportResultList, tableIndex);
      }
   }

   private void sendEquiptTableNew(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      List<List<Object>> tableDataList,
      List<Object> firstRowDataList,
      List<EmcReportResultBean> reportResultList,
      int tableIndex
   ) {
      List<EmcTestCapacityBean> testCapacityList = templateSourceDTO.getTestCapacityList();
      List<EmcEquiptBean> equiptList = templateSourceDTO.getEquiptList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<String> orderCategoryList = sampleReportResultList.stream()
         .filter(e -> e.getOrderCategory() != null)
         .map(e -> e.getOrderCategory())
         .distinct()
         .collect(Collectors.toList());
      List<MergedRow> mergedRowList = new ArrayList<>();

      for (String orderCategory : orderCategoryList) {
         List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
            .filter(e -> StringUtils.equals(orderCategory, e.getOrderCategory()))
            .collect(Collectors.toList());
         orderCategoryReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("newtestmethodbasis")));
         Map<String, List<EmcReportResultBean>> methodReportResultListMap = orderCategoryReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("newtestmethodbasis")));
         methodReportResultListMap.forEach(
            (method, methodReportResultList) -> {
               methodReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = methodReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
               itemGroupReportResultListMap.forEach(
                  (testName, curReportResultList) -> {
                     List<Object> equiptInfoRowDataList = new ArrayList<>();
                     equiptInfoRowDataList.add(orderCategory);
                     equiptInfoRowDataList.add(testName);
                     equiptInfoRowDataList.add(method);
                     if ("192".equals(curReportResultList.get(0).getTestId() + "")) {
                        int dioxinTableSize = NumberUtils.parseInt(curReportResultList.stream().map(e -> e.getOrderNo()).distinct().count(), 0);
                        equiptInfoRowDataList.add("详见表3至表" + (dioxinTableSize + 2));
                     } else if (curReportResultList.stream()
                        .anyMatch(e -> "1".equals(e.getExt$Item("multipleitemgroup")) || "总α放射性".equals(e.getTestName()) || "总β放射性".equals(e.getTestName()))) {
                        equiptInfoRowDataList.add("详见检测结果表");
                     } else {
                        List<EmcReportResultBean> lowLimitNotEmptyList = curReportResultList.stream()
                           .filter(e -> !StringUtils.isEmpty(e.getLowLimit()))
                           .collect(Collectors.toList());
                        EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : lowLimitNotEmptyList.get(0);
                        String limitValue = StringUtils.isBlank(limitResult.getLowLimit())
                           ? "-"
                           : limitResult.getLowLimit() + "" + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
                        equiptInfoRowDataList.add(limitValue);
                     }

                     tableDataList.add(equiptInfoRowDataList);
                  }
               );
            }
         );
         Map<String, List<EmcReportResultBean>> testNameReportResultListMap = orderCategoryReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         testNameReportResultListMap.forEach((testName, curReportResultList) -> {
         });
      }

      tableDataList.add(0, firstRowDataList);

      for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
         int biginIndex = 1;

         for (int index = 2; index <= tableDataList.size(); index++) {
            if (index == tableDataList.size() || !ObjectUtils.equals(tableDataList.get(index).get(columnIndex), tableDataList.get(index - 1).get(columnIndex))) {
               mergedRowList.add(new MergedRow(columnIndex, biginIndex, index - 1));
               biginIndex = index;
            }
         }
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("monitorTable");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(1400, 1600, 6000, 0));
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + tableIndex + " 检测方法");
      XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      equiptTable.getRow(0).setRepeatHeader(true);
   }

   private void sendEquiptTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测类别");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测方法");
      firstRowDataList.add("主要仪器");
      firstRowDataList.add("检出限");
      tableDataList.add(firstRowDataList);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcTestCapacityBean> testCapacityList = templateSourceDTO.getTestCapacityList();
      List<EmcEquiptBean> equiptList = templateSourceDTO.getEquiptList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> reportRowDataList = new ArrayList<>();

      for (EmcReportResultBean r : sampleReportResultList) {
         EmcTestCapacityBean testCapacity = testCapacityList.stream()
            .filter(ex -> ObjectUtils.equals(ex.getTestMethodId(), r.getTestMethodId()))
            .findFirst()
            .orElse(new EmcTestCapacityBean());
         String testMethodBasis = r.getExt$Item("testmethodbasis");
         String methodScope = testCapacity.getExt$Item("methodscope");
         if (StringUtils.isBlank(testMethodBasis)) {
            testMethodBasis = methodScope;
         } else if (!StringUtils.isBlank(methodScope)) {
            testMethodBasis = testMethodBasis + " " + methodScope;
         }

         r.setExt$Item("testmethodbasisreportdata", testMethodBasis);
         List<String> equiptIdStrList = new ArrayList<>();
         List<String> sampleEquiptIdList = (List<String>)(StringUtils.isBlank(r.getExt$Item("sampleequiptid"))
            ? new ArrayList<>()
            : Arrays.asList(StringUtils.split(r.getExt$Item("sampleequiptid"), ",")));
         equiptIdStrList.addAll(sampleEquiptIdList);
         List<String> analysisEquiptIdList = (List<String>)(StringUtils.isBlank(r.getExt$Item("analysisequiptid"))
            ? new ArrayList<>()
            : Arrays.asList(StringUtils.split(r.getExt$Item("analysisequiptid"), ",")));
         equiptIdStrList.addAll(analysisEquiptIdList);
         List<String> analysisAssistEquiptIdList = (List<String>)(StringUtils.isBlank(r.getExt$Item("analysisassistequiptid"))
            ? new ArrayList<>()
            : Arrays.asList(StringUtils.split(r.getExt$Item("analysisassistequiptid"), ",")));
         equiptIdStrList.addAll(analysisAssistEquiptIdList);
         List<Long> equiptIdList = equiptIdStrList.stream()
            .filter(NumberUtils::isNumber)
            .distinct()
            .<Long>map(NumberUtils::parseLong)
            .collect(Collectors.toList());
         List<EmcEquiptBean> curEquiptList = equiptList.stream().filter(ex -> equiptIdList.contains(ex.getId())).collect(Collectors.toList());
         if (!StringUtils.isBlank(r.getRunId()) && StringUtils.isBlank(r.getExt$Item("analysisequiptid"))) {
            EmcEquiptBean equipt = new EmcEquiptBean();
            equipt.setEquiptName("/");
            equipt.setEquiptId("/");
            equipt.setSpecification("");
            curEquiptList.add(equipt);
         }

         for (EmcEquiptBean e : curEquiptList) {
            r.setExt$Item("equiptnamereportdata", e.getSpecification() + " " + e.getEquiptName());
            reportRowDataList.add((EmcReportResultBean)ObjectUtils.clone(r));
         }

         if (curEquiptList.isEmpty()) {
            r.setExt$Item("equiptnamereportdata", "/");
            reportRowDataList.add((EmcReportResultBean)ObjectUtils.clone(r));
         }
      }

      reportRowDataList.sort(Comparator.comparing(ex -> ex.getTestName(), Comparator.nullsLast(Comparator.naturalOrder())));
      reportRowDataList.sort((a, b) -> {
         if ("192".equals(a.getTestId() + "")) {
            return -1;
         } else {
            return "192".equals(b.getTestId() + "") ? 1 : 0;
         }
      });
      reportRowDataList.sort(Comparator.comparing(ex -> ex.getExt$Item("testmethodbasisreportdata"), Comparator.nullsLast(Comparator.naturalOrder())));
      reportRowDataList.sort(Comparator.comparing(ex -> ex.getExt$Item("equiptnamereportdata"), Comparator.nullsLast(Comparator.naturalOrder())));
      reportRowDataList.sort(Comparator.comparing(ex -> ex.getOrderCategory(), Comparator.nullsLast(Comparator.naturalOrder())));
      List<MergedRow> mergedRowList = new ArrayList<>();

      for (String orderCategory : reportRowDataList.stream()
         .filter(ex -> ex.getOrderCategory() != null)
         .map(ex -> ex.getOrderCategory())
         .distinct()
         .collect(Collectors.toList())) {
         List<EmcReportResultBean> orderCategoryReportResultList = reportRowDataList.stream()
            .filter(ex -> StringUtils.equals(orderCategory, ex.getOrderCategory()))
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> equiptNameReportResultListMap = orderCategoryReportResultList.stream()
            .collect(Collectors.groupingBy(ex -> ex.getExt$Item("equiptnamereportdata")));
         equiptNameReportResultListMap.forEach(
            (equiptName, equiptNameReportResultList) -> {
               Map<String, List<EmcReportResultBean>> testMethodBasisReportResultListMap = equiptNameReportResultList.stream()
                  .collect(Collectors.groupingBy(ex -> ex.getExt$Item("testmethodbasisreportdata")));
               testMethodBasisReportResultListMap.forEach(
                  (testMethodBasisx, testMethodBasisReportResultList) -> {
                     Map<String, List<EmcReportResultBean>> testNameReportResultListMap = testMethodBasisReportResultList.stream()
                        .collect(Collectors.groupingBy(ex -> ex.getTestName()));
                     testNameReportResultListMap.forEach(
                        (testName, testNameReportResultList) -> {
                           List<Object> rowDataList = new ArrayList<>();
                           rowDataList.add(orderCategory);
                           rowDataList.add(testName);
                           rowDataList.add(testMethodBasisx);
                           rowDataList.add(equiptName);
                           if ("192".equals(testNameReportResultList.get(0).getTestId() + "")) {
                              List<EmcReportResultBean> dioxinReportResultList = orderCategoryReportResultList.stream()
                                 .filter(ex -> StringUtils.equals(testName, ex.getTestName()))
                                 .collect(Collectors.toList());
                              int dioxinTableSize = NumberUtils.parseInt(dioxinReportResultList.stream().map(ex -> ex.getOrderNo()).distinct().count(), 0);
                              rowDataList.add("详见表3至表" + (dioxinTableSize + 2));
                           } else {
                              List<EmcReportResultBean> lowLimitNotEmptyList = testNameReportResultList.stream()
                                 .filter(ex -> !StringUtils.isEmpty(ex.getLowLimit()))
                                 .collect(Collectors.toList());
                              EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : lowLimitNotEmptyList.get(0);
                              String limitValue = StringUtils.isBlank(limitResult.getLowLimit())
                                 ? "-"
                                 : limitResult.getLowLimit()
                                    + "\n"
                                    + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
                              rowDataList.add(limitValue);
                           }

                           tableDataList.add(rowDataList);
                        }
                     );
                  }
               );
            }
         );
      }

      for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
         int biginIndex = 1;

         for (int index = 2; index <= tableDataList.size(); index++) {
            if (index == tableDataList.size() || !ObjectUtils.equals(tableDataList.get(index).get(columnIndex), tableDataList.get(index - 1).get(columnIndex))) {
               mergedRowList.add(new MergedRow(columnIndex, biginIndex, index - 1));
               biginIndex = index;
            }
         }
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("monitorTable");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(1100, 2000, 4000, 2000, 0));
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      wordTemplateDynamicTableDTO.setTableTitle("表 1 检测方法及仪器");
      XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      equiptTable.getRow(0).setRepeatHeader(true);
   }

   private void createSampleResultTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      int tableCount = 2;
      List<EmcReportResultBean> dioxinReportResultList = sampleReportResultList.stream()
         .filter(e -> "192".equals(e.getTestId() + ""))
         .collect(Collectors.toList());
      if (!dioxinReportResultList.isEmpty()) {
         tableCount = this.dioxinSumTable(document, templateSourceDTO, tableCount, dioxinReportResultList);
         this.dioxinReportTable(document, templateSourceDTO, tableCount, dioxinReportResultList);
      }

      int dioxinTableSize = NumberUtils.parseInt(dioxinReportResultList.stream().map(e -> e.getOrderNo()).distinct().count(), 0);
      tableCount += dioxinTableSize;
      List<EmcReportResultBean> commonReportResultList = sampleReportResultList.stream()
         .filter(e -> !"192".equals(e.getTestId() + ""))
         .collect(Collectors.toList());
      List<String> orderCategoryList = commonReportResultList.stream()
         .filter(e -> e.getOrderCategory() != null)
         .map(e -> e.getOrderCategory())
         .distinct()
         .collect(Collectors.toList());
      List<String> sortList = Arrays.asList("废水", "地下水", "有组织废气");
      orderCategoryList = orderCategoryList.stream()
         .sorted(Comparator.comparing(e -> (String)e, Comparator.comparing(sortList::indexOf)))
         .collect(Collectors.toList());

      for (int i = 0; i < orderCategoryList.size(); i++) {
         String orderCategory = orderCategoryList.get(i);
         if ("废水".equals(orderCategory)) {
            this.feishuiSampleReportTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("生活饮用水".equals(orderCategory)) {
            tableCount = this.shyysSampleReportTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("有组织废气".equals(orderCategory)) {
            this.youzuzhiSampleTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("土壤".equals(orderCategory)) {
            tableCount = this.sampleCommonReportTableNew(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("噪声".equals(orderCategory)) {
            this.zsCommonReportTableNew(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("无组织废气".equals(orderCategory)) {
            tableCount = this.wzzfqReportTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("地表水".equals(orderCategory) || "地下水".equals(orderCategory) || "固废浸出".equals(orderCategory) || "固废含量".equals(orderCategory)) {
            tableCount = this.sampleCommonSplitReportTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("城镇污水处理厂污泥".equals(orderCategory)) {
            tableCount = this.wnSampleTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("沉积物".equals(orderCategory)) {
            tableCount = this.sampleCommonSplitReportTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("环境空气".equals(orderCategory)) {
            tableCount = this.hjkqSampleTable(document, templateSourceDTO, tableCount, orderCategory);
         } else {
            this.sampleCommonReportTable(document, templateSourceDTO, tableCount, orderCategory);
         }
      }

      Word2024Utils.removeKey(document, "resultTables");
   }

   private int wzzfqReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableCount, String orderCategory) {
      int tableIndex = 1;
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory) && StringUtils.equals(e.getDataType(), "sample"))
         .collect(Collectors.toList());

      for (String testName : sampleReportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
         List<EmcReportResultBean> curReportResultList = sampleReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
            .collect(Collectors.toList());
         int sampleNum = curReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getExt$Item("samplenum"), 1)).max().orElse(1);
         int frequency = curReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getFrequency(), 1)).max().orElse(1);

         for (EmcReportResultBean r : curReportResultList) {
            r.setExt$Item("maxfrequency", frequency + "");
            r.setExt$Item("maxsamplenum", sampleNum + "");
         }
      }

      List<EmcReportResultBean> commonReportResultList = sampleReportResultList.stream()
         .filter(e -> !ObjectUtils.equals(e.getExt$Item("reportTestName"), "挥发性有机物"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> frequencyReportResultList = commonReportResultList.stream()
         .filter(e -> NumberUtils.parseInt(e.getExt$Item("maxfrequency"), 1) > 1)
         .collect(Collectors.toList());
      List<Integer> frequencyList = frequencyReportResultList.stream()
         .map(e -> NumberUtils.parseInt(e.getExt$Item("maxfrequency")))
         .distinct()
         .collect(Collectors.toList());
      frequencyList.sort(Comparator.naturalOrder());

      for (Integer frequency : frequencyList) {
         List<EmcReportResultBean> curReportResultList = frequencyReportResultList.stream()
            .filter(e -> frequency == NumberUtils.parseInt(e.getExt$Item("maxfrequency")))
            .collect(Collectors.toList());
         tableIndex = this.wzzfqFrequencyReportTable(document, templateSourceDTO, frequency, tableCount, tableIndex, curReportResultList);
      }

      List<EmcReportResultBean> samplenumReportResultList = commonReportResultList.stream()
         .filter(e -> NumberUtils.parseInt(e.getExt$Item("maxfrequency"), 1) == 1)
         .collect(Collectors.toList());
      List<Integer> samplenumList = samplenumReportResultList.stream()
         .map(e -> NumberUtils.parseInt(e.getExt$Item("maxsamplenum")))
         .distinct()
         .collect(Collectors.toList());
      samplenumList.sort(Comparator.naturalOrder());

      for (Integer samplenum : samplenumList) {
         List<EmcReportResultBean> curReportResultList = samplenumReportResultList.stream()
            .filter(e -> samplenum == NumberUtils.parseInt(e.getExt$Item("maxsamplenum")))
            .collect(Collectors.toList());
         tableIndex = this.wzzfqSamplenumReportTable(document, templateSourceDTO, samplenum, tableCount, tableIndex, curReportResultList);
      }

      List<EmcReportResultBean> hfxyjwReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), "挥发性有机物"))
         .collect(Collectors.toList());
      this.hfxyjwSamplenumReportTable(document, templateSourceDTO, 3, tableCount, tableIndex, hfxyjwReportResultList);
      return tableCount;
   }

   private int wzzfqFrequencyReportTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int frequency,
      int tableCount,
      int secondTableCount,
      List<EmcReportResultBean> reportResultList
   ) {
      if (reportResultList.isEmpty()) {
         return secondTableCount;
      } else {
         for (String testName : reportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            List<EmcReportResultBean> testNameReportResultList = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
               .collect(Collectors.toList());
            List<String> sampleBeginTimeList = testNameReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getFrequency(), 1L) && ObjectUtils.equals("采样开始时间", e.getItemName()))
               .map(e -> e.getReportResult())
               .distinct()
               .collect(Collectors.toList());

            for (EmcReportResultBean r : testNameReportResultList) {
               r.setExt$Item("samplebegintimesize", sampleBeginTimeList.size() + "");
            }
         }

         List<Integer> timesizeList = reportResultList.stream()
            .map(e -> NumberUtils.parseInt(e.getExt$Item("samplebegintimesize")))
            .distinct()
            .collect(Collectors.toList());
         timesizeList.sort(Comparator.reverseOrder());

         for (Integer timesize : timesizeList) {
            List<EmcReportResultBean> curReportResultList = reportResultList.stream()
               .filter(e -> timesize == NumberUtils.parseInt(e.getExt$Item("samplebegintimesize")))
               .collect(Collectors.toList());
            secondTableCount = this.wzzfqFrequencyTimeReportTable(
               document, templateSourceDTO, frequency, tableCount, secondTableCount, timesize, curReportResultList
            );
         }

         return secondTableCount;
      }
   }

   private int wzzfqFrequencyTimeReportTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int frequency,
      int tableCount,
      int secondTableCount,
      int timesize,
      List<EmcReportResultBean> reportResultList
   ) {
      if (reportResultList.isEmpty()) {
         return secondTableCount;
      } else {
         if (timesize > 1) {
            for (String testName : reportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
               List<EmcReportResultBean> testNameReportResultList = reportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
                  .collect(Collectors.toList());
               List<String> sampleBeginTimeList = testNameReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getFrequency(), 1L) && ObjectUtils.equals("采样开始时间", e.getItemName()))
                  .map(e -> e.getReportResult())
                  .distinct()
                  .collect(Collectors.toList());

               for (int i = 0; i < timesize; i++) {
                  String sampleBeginTime = sampleBeginTimeList.get(i);
                  List<EmcReportResultBean> sampleBeginTimeReportResultList = testNameReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getReportResult(), sampleBeginTime))
                     .collect(Collectors.toList());
                  List<Long> folderIdList = sampleBeginTimeReportResultList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
                  List<EmcReportResultBean> curReportResultList = testNameReportResultList.stream()
                     .filter(e -> folderIdList.contains(e.getFolderId()))
                     .collect(Collectors.toList());
                  this.wzzfqFrequencyFolderReportTable(document, templateSourceDTO, frequency, tableCount, secondTableCount, curReportResultList);
                  secondTableCount++;
               }
            }
         } else {
            this.wzzfqFrequencyFolderReportTable(document, templateSourceDTO, frequency, tableCount, secondTableCount, reportResultList);
            secondTableCount++;
         }

         return secondTableCount;
      }
   }

   private void wzzfqFrequencyFolderReportTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int frequency,
      int tableCount,
      int secondTableCount,
      List<EmcReportResultBean> reportResultList
   ) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedColumn> mergedColumnList = new ArrayList<>();
      List<MergedRow> mergedRowList = new ArrayList<>();
      EmcReportBean report = templateSourceDTO.getReport();
      String showlimit = report.getExt$Item("showlimit");
      boolean showLimit = "1".equals(showlimit);
      boolean showIndex = "1".equals(report.getAddInfoIndexFlag());
      AtomicInteger rowIndex = new AtomicInteger(0);
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("采样日期");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测点位");

      for (int i = 1; i <= frequency; i++) {
         firstRowDataList.add("检测结果");
      }

      if (showLimit) {
         firstRowDataList.add("标准限值");
      }

      rowIndex.incrementAndGet();
      tableDataList.add(firstRowDataList);
      mergedColumnList.add(new MergedColumn(0, 3, 3 + frequency - 1));
      List<Object> secondRowDataList = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
         secondRowDataList.add("");
      }

      for (int i = 1; i <= frequency; i++) {
         final int currentFrequency = i;
         List<EmcReportResultBean> frequencyReportResultList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getFrequency()), currentFrequency))
            .collect(Collectors.toList());
         EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
            .filter(e -> ObjectUtils.equals("采样开始时间", e.getItemName()))
            .findFirst()
            .orElse(new EmcReportResultBean());
         EmcReportResultBean emcReportResultBean2 = frequencyReportResultList.stream()
            .filter(e -> ObjectUtils.equals("采样结束时间", e.getItemName()))
            .findFirst()
            .orElse(new EmcReportResultBean());
         secondRowDataList.add(
            this.numberToChinese(i)
               + "时段（"
               + Optional.ofNullable(emcReportResultBean.getReportResult()).orElse("")
               + (emcReportResultBean2.getReportResult() == null ? "" : "-" + emcReportResultBean2.getReportResult())
               + "）"
         );
      }

      if (showLimit) {
         secondRowDataList.add("");
         mergedRowList.add(new MergedRow(frequency + 3, 0, 1));
      }

      rowIndex.incrementAndGet();
      tableDataList.add(secondRowDataList);
      mergedRowList.add(new MergedRow(0, 0, 1));
      mergedRowList.add(new MergedRow(1, 0, 1));
      mergedRowList.add(new MergedRow(2, 0, 1));
      List<String> testNameList = reportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList());
      testNameList.sort(Comparator.comparing(String::toString));

      for (String testName : testNameList) {
         int startRowIndex = rowIndex.get();
         List<EmcReportResultBean> testNameReportResultList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
            .collect(Collectors.toList());
         List<String> monitorPointNameList = testNameReportResultList.stream().map(e -> e.getMonitorPointName()).distinct().collect(Collectors.toList());
         monitorPointNameList.sort(Comparator.comparing(String::toString));
         Map<Integer, String> maxResultMap = new HashMap<>();

         for (String monitorPointName : monitorPointNameList) {
            List<EmcReportResultBean> monitorPointReportResultList = testNameReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getMonitorPointName(), monitorPointName))
               .collect(Collectors.toList());
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(DateTimeUtils.formatLocalDate(monitorPointReportResultList.get(0).getSampleBeginDate(), "yyyy年MM月dd日"));
            String itemgroup = monitorPointReportResultList.get(0).getExt$Item("reportTestName");
            EmcReportResultBean curReportResult = monitorPointReportResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
            if (curReportResult == null) {
               curReportResult = monitorPointReportResultList.stream()
                  .filter(e -> e.getUnitName() != null)
                  .findFirst()
                  .orElse(monitorPointReportResultList.get(0));
            }

            String resultTitle = StringUtils.isBlank(curReportResult.getUnitName()) ? itemgroup : itemgroup + "(" + curReportResult.getUnitName() + ")";
            rowDataList.add(resultTitle);
            rowDataList.add(monitorPointReportResultList.get(0).getMonitorPointName() + "〇" + monitorPointReportResultList.get(0).getMonitorPointNo());

            for (int i = 1; i <= frequency; i++) {
               int finalFrequency = i;
               List<EmcReportResultBean> frequencyReportResultList = monitorPointReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getFrequency()), finalFrequency))
                  .collect(Collectors.toList());
               EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
                  .filter(e -> ObjectUtils.equals("样品浓度", e.getItemName()))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               String reportResult = emcReportResultBean.getReportResult();
               rowDataList.add(this.transScientificCounting(reportResult));
               String maxResult = maxResultMap.get(i);
               if (NumberUtils.isNumber(reportResult) && (maxResult == null || NumberUtils.parseDouble(reportResult) > NumberUtils.parseDouble(maxResult))) {
                  maxResultMap.put(i, reportResult);
               }
            }

            if (showLimit) {
               EmcReportResultBean indexResult = monitorPointReportResultList.stream()
                  .filter(e -> e.getIndexLowLimit() != null)
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               rowDataList.add(indexResult.getIndexLowLimit());
            }

            tableDataList.add(rowDataList);
            rowIndex.incrementAndGet();
         }

         List<Object> maxRowDataList = new ArrayList<>();

         for (int ix = 0; ix < 2; ix++) {
            maxRowDataList.add("");
         }

         maxRowDataList.add("最大值");

         for (int ix = 1; ix <= frequency; ix++) {
            String maxResult = maxResultMap.get(ix);
            maxRowDataList.add(maxResult);
         }

         if (showLimit) {
            maxRowDataList.add("/");
         }

         tableDataList.add(maxRowDataList);
         mergedRowList.add(new MergedRow(0, startRowIndex, rowIndex.get()));
         mergedRowList.add(new MergedRow(1, startRowIndex, rowIndex.get()));
         rowIndex.incrementAndGet();
      }

      if (showIndex) {
         List<Object> remarkRowDataList = new ArrayList<>();
         remarkRowDataList.add("备注");
         EmcReportResultBean indexResult = reportResultList.stream()
            .filter(e -> e.getExt$Item("indexname") != null)
            .findFirst()
            .orElse(new EmcReportResultBean());

         for (int ix = 1; ix < firstRowDataList.size(); ix++) {
            remarkRowDataList.add(indexResult.getExt$Item("indexname"));
         }

         tableDataList.add(remarkRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, firstRowDataList.size() - 1));
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableCount + 1) + "-" + secondTableCount + " " + reportResultList.get(0).getOrderCategory() + "检测结果");
      wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
      table.getRow(1).setRepeatHeader(true);
   }

   private int wzzfqSamplenumReportTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int samplenum,
      int tableCount,
      int secondTableCount,
      List<EmcReportResultBean> reportResultList
   ) {
      if (reportResultList.isEmpty()) {
         return secondTableCount;
      } else {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         EmcReportBean report = templateSourceDTO.getReport();
         String showlimit = report.getExt$Item("showlimit");
         boolean showLimit = "1".equals(showlimit);
         boolean showIndex = "1".equals(report.getAddInfoIndexFlag());
         AtomicInteger rowIndex = new AtomicInteger(0);
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("采样日期");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("检测点位");

         for (int i = 1; i <= samplenum; i++) {
            firstRowDataList.add("检测结果");
         }

         if (showLimit) {
            firstRowDataList.add("标准限值");
         }

         rowIndex.incrementAndGet();
         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(0, 3, 3 + samplenum - 1));
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 3; i++) {
            secondRowDataList.add("");
         }

         for (int i = 1; i <= samplenum; i++) {
            final int currentSampleNum = i;
            List<EmcReportResultBean> frequencyReportResultList = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getExt$Item("samplenum")), currentSampleNum))
               .collect(Collectors.toList());
            EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
               .filter(e -> ObjectUtils.equals("采样开始时间", e.getItemName()))
               .findFirst()
               .orElse(new EmcReportResultBean());
            EmcReportResultBean emcReportResultBean2 = frequencyReportResultList.stream()
               .filter(e -> ObjectUtils.equals("采样结束时间", e.getItemName()))
               .findFirst()
               .orElse(new EmcReportResultBean());
            secondRowDataList.add(
               "第"
                  + this.numberToChinese(i)
                  + "次（"
                  + Optional.ofNullable(emcReportResultBean.getReportResult()).orElse("")
                  + (emcReportResultBean2.getReportResult() == null ? "" : "-" + emcReportResultBean2.getReportResult())
                  + "）"
            );
         }

         if (showLimit) {
            secondRowDataList.add("");
            mergedRowList.add(new MergedRow(samplenum + 3, 0, 1));
         }

         rowIndex.incrementAndGet();
         tableDataList.add(secondRowDataList);
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         mergedRowList.add(new MergedRow(2, 0, 1));
         List<String> testNameList = reportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList());
         testNameList.sort(Comparator.comparing(String::toString));

         for (String testName : testNameList) {
            int startRowIndex = rowIndex.get();
            List<EmcReportResultBean> testNameReportResultList = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
               .collect(Collectors.toList());
            List<String> monitorPointNameList = testNameReportResultList.stream().map(e -> e.getMonitorPointName()).distinct().collect(Collectors.toList());
            monitorPointNameList.sort(Comparator.comparing(String::toString));
            Map<Integer, String> maxResultMap = new HashMap<>();

            for (String monitorPointName : monitorPointNameList) {
               List<EmcReportResultBean> monitorPointReportResultList = testNameReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getMonitorPointName(), monitorPointName))
                  .collect(Collectors.toList());
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(DateTimeUtils.formatLocalDate(monitorPointReportResultList.get(0).getSampleBeginDate(), "yyyy年MM月dd日"));
               String itemgroup = monitorPointReportResultList.get(0).getExt$Item("reportTestName");
               EmcReportResultBean curReportResult = monitorPointReportResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
               if (curReportResult == null) {
                  curReportResult = monitorPointReportResultList.stream()
                     .filter(e -> e.getUnitName() != null)
                     .findFirst()
                     .orElse(monitorPointReportResultList.get(0));
               }

               String resultTitle = StringUtils.isBlank(curReportResult.getUnitName()) ? itemgroup : itemgroup + "(" + curReportResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               rowDataList.add(monitorPointReportResultList.get(0).getMonitorPointName() + "〇" + monitorPointReportResultList.get(0).getMonitorPointNo());

               for (int i = 1; i <= samplenum; i++) {
                  int finalSamplenum = i;
                  List<EmcReportResultBean> frequencyReportResultList = monitorPointReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getExt$Item("samplenum")), finalSamplenum))
                     .collect(Collectors.toList());
                  EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
                     .filter(e -> ObjectUtils.equals("样品浓度", e.getItemName()))
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  String reportResult = emcReportResultBean.getReportResult();
                  rowDataList.add(this.transScientificCounting(reportResult));
                  String maxResult = maxResultMap.get(i);
                  if (NumberUtils.isNumber(reportResult) && (maxResult == null || NumberUtils.parseDouble(reportResult) > NumberUtils.parseDouble(maxResult))) {
                     maxResultMap.put(i, reportResult);
                  }
               }

               if (showLimit) {
                  EmcReportResultBean indexResult = monitorPointReportResultList.stream()
                     .filter(e -> e.getIndexLowLimit() != null)
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  rowDataList.add(indexResult.getIndexLowLimit());
               }

               tableDataList.add(rowDataList);
               rowIndex.incrementAndGet();
            }

            List<Object> maxRowDataList = new ArrayList<>();

            for (int ix = 0; ix < 2; ix++) {
               maxRowDataList.add("");
            }

            maxRowDataList.add("最大值");

            for (int ix = 1; ix <= samplenum; ix++) {
               String maxResult = maxResultMap.get(ix);
               maxRowDataList.add(maxResult);
            }

            if (showLimit) {
               maxRowDataList.add("/");
            }

            tableDataList.add(maxRowDataList);
            mergedRowList.add(new MergedRow(0, startRowIndex, rowIndex.get()));
            mergedRowList.add(new MergedRow(1, startRowIndex, rowIndex.get()));
            rowIndex.incrementAndGet();
         }

         if (showIndex) {
            List<Object> remarkRowDataList = new ArrayList<>();
            remarkRowDataList.add("备注");
            EmcReportResultBean indexResult = reportResultList.stream()
               .filter(e -> e.getExt$Item("indexname") != null)
               .findFirst()
               .orElse(new EmcReportResultBean());

            for (int ix = 1; ix < firstRowDataList.size(); ix++) {
               remarkRowDataList.add(indexResult.getExt$Item("indexname"));
            }

            tableDataList.add(remarkRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, firstRowDataList.size() - 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableCount + 1) + "-" + secondTableCount + " " + reportResultList.get(0).getOrderCategory() + "检测结果");
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
         return secondTableCount + 1;
      }
   }

   private void hfxyjwSamplenumReportTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int frequency,
      int tableCount,
      int secondTableCount,
      List<EmcReportResultBean> reportResultList
   ) {
      if (!reportResultList.isEmpty()) {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         EmcReportBean report = templateSourceDTO.getReport();
         String showlimit = report.getExt$Item("showlimit");
         boolean showLimit = "1".equals(showlimit);
         boolean showIndex = "1".equals(report.getAddInfoIndexFlag());
         AtomicInteger rowIndex = new AtomicInteger(0);
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("采样日期");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("检测点位");

         for (int i = 1; i <= frequency; i++) {
            firstRowDataList.add("检测结果");
         }

         if (showLimit) {
            firstRowDataList.add("标准限值");
         }

         rowIndex.incrementAndGet();
         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(0, 3, 3 + frequency - 1));
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 3; i++) {
            secondRowDataList.add("");
         }

         for (int i = 1; i <= frequency; i++) {
            final int currentFrequency = i;
            List<EmcReportResultBean> frequencyReportResultList = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getFrequency()), currentFrequency))
               .collect(Collectors.toList());
            EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
               .filter(e -> ObjectUtils.equals("采样开始时间", e.getItemName()))
               .findFirst()
               .orElse(new EmcReportResultBean());
            EmcReportResultBean emcReportResultBean2 = frequencyReportResultList.stream()
               .filter(e -> ObjectUtils.equals("采样结束时间", e.getItemName()))
               .findFirst()
               .orElse(new EmcReportResultBean());
            secondRowDataList.add(
               this.numberToChinese(i)
                  + "时段（"
                  + Optional.ofNullable(emcReportResultBean.getReportResult()).orElse("")
                  + (emcReportResultBean2.getReportResult() == null ? "" : "-" + emcReportResultBean2.getReportResult())
                  + "）"
            );
         }

         if (showLimit) {
            secondRowDataList.add("");
            mergedRowList.add(new MergedRow(frequency + 3, 0, 1));
         }

         rowIndex.incrementAndGet();
         tableDataList.add(secondRowDataList);
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         mergedRowList.add(new MergedRow(2, 0, 1));
         List<String> testNameList = reportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList());
         testNameList.sort(Comparator.comparing(String::toString));

         for (String testName : testNameList) {
            int startRowIndex = rowIndex.get();
            List<EmcReportResultBean> testNameReportResultList = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
               .collect(Collectors.toList());
            List<String> monitorPointNameList = testNameReportResultList.stream().map(e -> e.getMonitorPointName()).distinct().collect(Collectors.toList());
            monitorPointNameList.sort(Comparator.comparing(String::toString));
            Map<Integer, String> maxResultMap = new HashMap<>();

            for (String monitorPointName : monitorPointNameList) {
               List<EmcReportResultBean> monitorPointReportResultList = testNameReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getMonitorPointName(), monitorPointName))
                  .collect(Collectors.toList());
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(DateTimeUtils.formatLocalDate(monitorPointReportResultList.get(0).getSampleBeginDate(), "yyyy年MM月dd日"));
               String itemgroup = monitorPointReportResultList.get(0).getExt$Item("reportTestName");
               List<EmcReportResultBean> unitNotEmptyList = monitorPointReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                  .collect(Collectors.toList());
               EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
               String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               rowDataList.add(monitorPointReportResultList.get(0).getMonitorPointName() + "〇" + monitorPointReportResultList.get(0).getMonitorPointNo());

               for (int i = 1; i <= frequency; i++) {
                  int finalFrequency = i;
                  List<EmcReportResultBean> frequencyReportResultList = monitorPointReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getFrequency()), finalFrequency))
                     .collect(Collectors.toList());
                  EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
                     .filter(e -> ObjectUtils.equals("VOCs总量", e.getItemName()))
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  String reportResult = emcReportResultBean.getReportResult();
                  rowDataList.add(this.transScientificCounting(reportResult));
                  String maxResult = maxResultMap.get(i);
                  if (NumberUtils.isNumber(reportResult) && (maxResult == null || NumberUtils.parseDouble(reportResult) > NumberUtils.parseDouble(maxResult))) {
                     maxResultMap.put(i, reportResult);
                  }
               }

               if (showLimit) {
                  EmcReportResultBean indexResult = monitorPointReportResultList.stream()
                     .filter(e -> e.getIndexLowLimit() != null)
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  rowDataList.add(indexResult.getIndexLowLimit());
               }

               tableDataList.add(rowDataList);
               rowIndex.incrementAndGet();
            }

            List<Object> maxRowDataList = new ArrayList<>();

            for (int ix = 0; ix < 2; ix++) {
               maxRowDataList.add("");
            }

            maxRowDataList.add("最大值");

            for (int ix = 1; ix <= frequency; ix++) {
               String maxResult = maxResultMap.get(ix);
               maxRowDataList.add(maxResult);
            }

            if (showLimit) {
               maxRowDataList.add("/");
            }

            tableDataList.add(maxRowDataList);
            mergedRowList.add(new MergedRow(0, startRowIndex, rowIndex.get()));
            mergedRowList.add(new MergedRow(1, startRowIndex, rowIndex.get()));
            rowIndex.incrementAndGet();
         }

         if (showIndex) {
            List<Object> remarkRowDataList = new ArrayList<>();
            remarkRowDataList.add("备注");
            EmcReportResultBean indexResult = reportResultList.stream()
               .filter(e -> e.getExt$Item("indexname") != null)
               .findFirst()
               .orElse(new EmcReportResultBean());

            for (int ix = 1; ix < firstRowDataList.size(); ix++) {
               remarkRowDataList.add(indexResult.getExt$Item("indexname"));
            }

            tableDataList.add(remarkRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, firstRowDataList.size() - 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle(
            "表 " + (tableCount + 1) + (secondTableCount == 0 ? "" : "-" + secondTableCount) + " " + reportResultList.get(0).getOrderCategory() + "检测结果"
         );
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
      }
   }

   private void createSendResultTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      int tableCount = 2;
      List<EmcReportResultBean> dioxinReportResultList = sampleReportResultList.stream()
         .filter(e -> "192".equals(e.getTestId() + ""))
         .collect(Collectors.toList());
      if (!dioxinReportResultList.isEmpty()) {
         this.dioxinReportTable(document, templateSourceDTO, tableCount, dioxinReportResultList);
      }

      int dioxinTableSize = NumberUtils.parseInt(dioxinReportResultList.stream().map(e -> e.getOrderNo()).distinct().count(), 0);
      tableCount += dioxinTableSize;
      List<EmcReportResultBean> commonReportResultList = sampleReportResultList.stream()
         .filter(e -> !"192".equals(e.getTestId() + ""))
         .collect(Collectors.toList());
      List<String> orderCategoryList = commonReportResultList.stream()
         .filter(e -> e.getOrderCategory() != null)
         .map(e -> e.getOrderCategory())
         .distinct()
         .collect(Collectors.toList());
      List<String> orderCategorySortList = Arrays.asList("土壤", "沉积物", "固废浸出", "固废含量");
      orderCategoryList = orderCategoryList.stream()
         .sorted(Comparator.comparing(e -> (String)e, Comparator.comparing(orderCategorySortList::indexOf)))
         .collect(Collectors.toList());

      for (int i = 0; i < orderCategoryList.size(); i++) {
         String orderCategory = orderCategoryList.get(i);
         if (orderCategorySortList.contains(orderCategory)) {
            tableCount = this.tushuiSendReportTable(document, templateSourceDTO, tableCount, orderCategory);
         } else if ("有组织废气".equals(orderCategory)) {
            tableCount = this.youzuzhiSendTable(document, templateSourceDTO, tableCount, orderCategory);
         } else {
            tableCount = this.sendCommonReportTable(document, templateSourceDTO, tableCount, orderCategory);
         }
      }

      Word2024Utils.removeKey(document, "resultTables");
   }

   private int tushuiSendReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      int littleTableCount = 0;
      List<EmcReportResultBean> multipleReportResultList = orderCategoryReportResultList.stream()
         .filter(e -> "1".equals(e.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!multipleReportResultList.isEmpty()) {
         littleTableCount = this.tushuiMultipleSendReportTable(
            document, templateSourceDTO, tableCount, littleTableCount, multipleReportResultList, orderCategory
         );
      }

      List<EmcReportResultBean> commonReportResultList = orderCategoryReportResultList.stream()
         .filter(e -> !"1".equals(e.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!commonReportResultList.isEmpty()) {
         List<EmcReportResultBean> specReportResultList = commonReportResultList.stream()
            .filter(s -> "总α放射性".equals(s.getTestName()) || "总β放射性".equals(s.getTestName()))
            .collect(Collectors.toList());
         if (!CollectionUtils.isEmpty(specReportResultList)) {
            littleTableCount = this.tushuiCommonSendReportTable(
               document, templateSourceDTO, tableCount, littleTableCount, specReportResultList, orderCategory, true
            );
         }

         List<EmcReportResultBean> normalReportResultList = commonReportResultList.stream()
            .filter(s -> !"总α放射性".equals(s.getTestName()) && !"总β放射性".equals(s.getTestName()))
            .collect(Collectors.toList());
         if (!CollectionUtils.isEmpty(normalReportResultList)) {
            littleTableCount = this.tushuiCommonSendReportTable(
               document, templateSourceDTO, tableCount, littleTableCount, normalReportResultList, orderCategory, false
            );
         }
      }

      return tableCount + 1;
   }

   private int fsxSampleReportTable(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableCount, List<EmcReportResultBean> reportResultList, String orderCategory
   ) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedRow> mergedRowList = new ArrayList<>();
      List<MergedColumn> mergedColumnList = new ArrayList<>();
      AtomicInteger rowIndex = new AtomicInteger(0);
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("点位名称");
      firstRowDataList.add("样品状态");

      for (int i = 0; i < 3; i++) {
         firstRowDataList.add("检测项目及结果");
      }

      mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, 4));
      tableDataList.add(firstRowDataList);
      List<Object> secondRowDataList = new ArrayList<>();
      rowIndex.incrementAndGet();

      for (int i = 0; i < 2; i++) {
         secondRowDataList.add("");
      }

      LocalDate sampleDate = reportResultList.get(0).getSampleBeginDate();
      String sampleDateStr = DateTimeUtils.formatLocalDate(sampleDate, "yyyy年MM月dd日");

      for (int i = 0; i < 3; i++) {
         secondRowDataList.add("采样日期：" + sampleDateStr);
      }

      mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, 4));
      tableDataList.add(secondRowDataList);
      List<Object> thirdRowDataList = new ArrayList<>();
      rowIndex.incrementAndGet();

      for (int i = 0; i < 2; i++) {
         thirdRowDataList.add("");
      }

      thirdRowDataList.add("检测项目");
      thirdRowDataList.add("探测下限Lᴅ(Bq/L)");
      thirdRowDataList.add("活度浓度(Bq/L)");
      tableDataList.add(thirdRowDataList);
      mergedRowList.add(new MergedRow(0, 0, 2));
      mergedRowList.add(new MergedRow(1, 0, 2));
      List<String> monitorPointNameList = reportResultList.stream().map(e -> e.getMonitorPointName()).distinct().collect(Collectors.toList());
      monitorPointNameList.sort(Comparator.comparing(String::toString));

      for (String monitorPointName : monitorPointNameList) {
         List<EmcReportResultBean> monitorPointNameReportResultList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getMonitorPointName(), monitorPointName))
            .collect(Collectors.toList());
         monitorPointNameReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         Collections.reverse(monitorPointNameReportResultList);
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(monitorPointName);
               rowDataList.add(curReportResultList.get(0).getOrderDesc());
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               rowDataList.add(itemgroup);
               rowDataList.add(
                  this.transScientificCounting(
                     curReportResultList.stream().filter(res -> "探测限".equals(res.getItemName())).collect(Collectors.toList()).get(0).getReportResult()
                  )
               );
               rowDataList.add(
                  this.transScientificCounting(
                     curReportResultList.stream().filter(res -> "样品浓度".equals(res.getItemName())).collect(Collectors.toList()).get(0).getReportResult()
                  )
               );
               tableDataList.add(rowDataList);
            }
         );
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableCount + 1) + " " + orderCategory + "总α、β放射性检测结果");
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
      table.getRow(1).setRepeatHeader(true);
      table.getRow(2).setRepeatHeader(true);
      return tableCount;
   }

   private int tushuiCommonSendReportTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int tableCount,
      int littleTableCount,
      List<EmcReportResultBean> reportResultList,
      String orderCategory,
      boolean fangshexing
   ) {
      reportResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
      List<String> orderNoList = reportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());
      List<List<String>> splitList = this.splitList(orderNoList, 4);
      List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();

      for (List<String> curOrderNoList : splitList) {
         List<EmcReportResultBean> orderNoReportResultList = reportResultList.stream()
            .filter(e -> curOrderNoList.contains(e.getClientOrderName()))
            .collect(Collectors.toList());
         List<EmcOrderBean> curOrderList = orderList.stream().filter(e -> curOrderNoList.contains(e.getClientOrderName())).collect(Collectors.toList());
         AtomicInteger rowIndex = new AtomicInteger(0);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();

         for (int i = 0; i < 4; i++) {
            firstRowDataList.add("样品名称");
         }

         for (String orderNo : curOrderNoList) {
            firstRowDataList.add(orderNo);
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 3));
         rowIndex.incrementAndGet();
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 4; i++) {
            secondRowDataList.add("样品状态");
         }

         for (EmcOrderBean order : curOrderList) {
            secondRowDataList.add(order.getOrderDesc());
         }

         tableDataList.add(secondRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 3));
         rowIndex.incrementAndGet();
         List<Object> RowDataList3 = new ArrayList<>();
         RowDataList3.add("序号");
         RowDataList3.add("检测项目");
         RowDataList3.add("单位");
         if (fangshexing) {
            RowDataList3.add("探测下限");
         } else {
            RowDataList3.add("检出限");
         }

         for (int i = 0; i < curOrderNoList.size(); i++) {
            RowDataList3.add("检测结果");
         }

         tableDataList.add(RowDataList3);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 4, 3 + curOrderNoList.size()));
         rowIndex.incrementAndGet();

         for (String itemGroupName : orderNoReportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            List<Object> resultRowDataList = new ArrayList<>();
            resultRowDataList.add(rowIndex.get() - 2);
            resultRowDataList.add(itemGroupName);
            EmcReportResultBean resultBean = orderNoReportResultList.stream()
               .filter(e -> StringUtils.equals(e.getExt$Item("reportTestName"), itemGroupName))
               .findFirst()
               .orElse(new EmcReportResultBean());
            resultRowDataList.add(resultBean.getUnitName());
            if (fangshexing) {
               EmcReportResultBean tancexianResultBean = orderNoReportResultList.stream()
                  .filter(e -> StringUtils.equals(e.getItemName(), "探测限"))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               resultRowDataList.add(this.transScientificCounting(tancexianResultBean.getReportResult()));
            } else if ("1".equals(resultBean.getExt$Item("limitscientific"))) {
               resultRowDataList.add(this.transScientificCounting(NumberUtils.rounding(resultBean.getLowLimit(), "ISO,1")));
            } else {
               resultRowDataList.add(resultBean.getLowLimit());
            }

            for (String orderNo : curOrderNoList) {
               EmcReportResultBean curOrderNoReportResult = reportResultList.stream()
                  .filter(e -> StringUtils.equals(e.getExt$Item("reportTestName"), itemGroupName) && ObjectUtils.equals(orderNo, e.getClientOrderName()))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               resultRowDataList.add(this.transScientificCounting(curOrderNoReportResult.getReportResult()));
            }

            tableDataList.add(resultRowDataList);
            rowIndex.incrementAndGet();
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         List<Integer> columnWidthList = new ArrayList<>();
         columnWidthList.add(1000);
         columnWidthList.add(2000);
         columnWidthList.add(1000);
         columnWidthList.add(1000);

         for (EmcOrderBean order : curOrderList) {
            columnWidthList.add(0);
         }

         wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableCount + 1) + "-" + (littleTableCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
         table.getRow(2).setRepeatHeader(true);
         littleTableCount++;
      }

      return littleTableCount;
   }

   private int tushuiMultipleSendReportTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int tableCount,
      int littleTableCount,
      List<EmcReportResultBean> reportResultList,
      String orderCategory
   ) {
      List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();
      List<String> testNameList = reportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList());
      testNameList.sort(Comparator.comparing(String::toString));

      for (String testName : testNameList) {
         List<EmcReportResultBean> testNameReportResultList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
            .collect(Collectors.toList());
         testNameReportResultList.sort(Comparator.comparing(EmcReportResultBean::getOrderNo));
         List<String> orderNoList = testNameReportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());

         for (List<String> curOrderNoList : this.splitList(orderNoList, 4)) {
            List<EmcReportResultBean> orderNoReportResultList = testNameReportResultList.stream()
               .filter(e -> curOrderNoList.contains(e.getClientOrderName()))
               .collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(e -> curOrderNoList.contains(e.getClientOrderName())).collect(Collectors.toList());
            if (curOrderList.size() != curOrderNoList.size()) {
               throw new InvalidDataException("客户样品名称重复");
            }

            AtomicInteger rowIndex = new AtomicInteger(0);
            List<List<Object>> tableDataList = new ArrayList<>();
            List<MergedColumn> mergedColumnList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
               firstRowDataList.add("样品名称");
            }

            for (String orderNo : curOrderNoList) {
               firstRowDataList.add(orderNo);
            }

            tableDataList.add(firstRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 3));
            rowIndex.incrementAndGet();
            List<Object> secondRowDataList = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
               secondRowDataList.add("样品状态");
            }

            for (EmcOrderBean order : curOrderList) {
               secondRowDataList.add(order.getOrderDesc());
            }

            tableDataList.add(secondRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 3));
            rowIndex.incrementAndGet();
            List<Object> RowDataList3 = new ArrayList<>();
            RowDataList3.add("序号");
            RowDataList3.add("检测项目");
            RowDataList3.add("单位");
            RowDataList3.add("检出限");

            for (int i = 0; i < curOrderNoList.size(); i++) {
               RowDataList3.add("检测结果");
            }

            tableDataList.add(RowDataList3);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 4, 3 + curOrderNoList.size()));
            rowIndex.incrementAndGet();

            for (String itemGroupName : orderNoReportResultList.stream().map(e -> e.getExt$Item("itemGroup")).distinct().collect(Collectors.toList())) {
               List<Object> resultRowDataList = new ArrayList<>();
               resultRowDataList.add(rowIndex.get() - 2);
               resultRowDataList.add(itemGroupName);
               EmcReportResultBean resultBean = orderNoReportResultList.stream()
                  .filter(e -> StringUtils.equals(e.getExt$Item("itemGroup"), itemGroupName))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               resultRowDataList.add(resultBean.getUnitName());
               if ("1".equals(resultBean.getExt$Item("limitscientific"))) {
                  resultRowDataList.add(this.transScientificCounting(NumberUtils.rounding(resultBean.getLowLimit(), "ISO,1")));
               } else {
                  resultRowDataList.add(resultBean.getLowLimit());
               }

               for (String orderNo : curOrderNoList) {
                  EmcReportResultBean curOrderNoReportResult = orderNoReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(orderNo, e.getClientOrderName()) && StringUtils.equals(e.getExt$Item("itemGroup"), itemGroupName))
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  resultRowDataList.add(this.transScientificCounting(curOrderNoReportResult.getReportResult()));
               }

               tableDataList.add(resultRowDataList);
               rowIndex.incrementAndGet();
            }

            this.listListNullToString(tableDataList, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
            List<Integer> columnWidthList = new ArrayList<>();
            columnWidthList.add(700);
            columnWidthList.add(2000);
            columnWidthList.add(900);
            columnWidthList.add(900);

            for (EmcOrderBean order : curOrderList) {
               columnWidthList.add(0);
            }

            wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableCount + 1) + "-" + (littleTableCount + 1) + " " + orderCategory + testName + "检测结果");
            wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
            XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            table.getRow(0).setRepeatHeader(true);
            table.getRow(1).setRepeatHeader(true);
            table.getRow(2).setRepeatHeader(true);
            littleTableCount++;
         }
      }

      return littleTableCount;
   }

   private int dioxinSumTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, List<EmcReportResultBean> reportResultList) {
      List<EmcReportResultBean> itemReportResultList = reportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getItemAliasName(), "二噁英测定浓度"))
         .collect(Collectors.toList());
      if (itemReportResultList.isEmpty()) {
         return tabelCount;
      } else {
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

         for (String orderCategory : itemReportResultList.stream().map(e -> e.getOrderCategory()).distinct().collect(Collectors.toList())) {
            List<EmcReportResultBean> orderCategoryReportResultList = itemReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
               .collect(Collectors.toList());
            List<List<Object>> tableDataList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();
            firstRowDataList.add("采样日期");
            firstRowDataList.add("样品编号");
            firstRowDataList.add("检测点位");
            firstRowDataList.add("二噁英类毒性当量(TEQ)质量浓度ng/m3");
            tableDataList.add(firstRowDataList);
            List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
            orderNoList.sort(Comparator.comparing(String::toString));

            for (String orderNo : orderNoList) {
               List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
                  .collect(Collectors.toList());
               List<Object> resultRowDataList = new ArrayList<>();
               if (orderNoReportResultList.get(0).getSampleBeginDate() == null) {
                  resultRowDataList.add("");
               } else {
                  String formattedDate = orderNoReportResultList.get(0).getSampleBeginDate().format(formatter);
                  resultRowDataList.add(formattedDate);
               }

               resultRowDataList.add(orderNo);
               resultRowDataList.add(orderNoReportResultList.get(0).getMonitorPointName());
               resultRowDataList.add(this.transScientificCounting(orderNoReportResultList.get(0).getReportResult()));
               tableDataList.add(resultRowDataList);
            }

            this.listListNullToString(tableDataList, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果汇总表");
            XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            table.getRow(0).setRepeatHeader(true);
            tabelCount++;
         }

         return tabelCount;
      }
   }

   private void dioxinReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, List<EmcReportResultBean> dtoReportResultList) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
      List<String> itemAliasNameList = ArrayUtils.asList(
         new String[]{
            "2,3,7,8-T₄CDD",
            "1,2,3,7,8-P₅CDD",
            "1,2,3,4,7,8-H₆CDD",
            "1,2,3,6,7,8-H₆CDD",
            "1,2,3,7,8,9-H₆CDD",
            "1,2,3,4,6,7,8-H₇CDD",
            "O₈CDD",
            "2,3,7,8-T₄CDF",
            "1,2,3,7,8-P₅CDF",
            "2,3,4,7,8-P₅CDF",
            "1,2,3,4,7,8-H₆CDF",
            "1,2,3,6,7,8-H₆CDF",
            "2,3,4,6,7,8-H₆CDF",
            "1,2,3,7,8,9-H₆CDF",
            "1,2,3,4,6,7,8-H₇CDF",
            "1,2,3,4,7,8,9-H₇CDF",
            "O₈CDF"
         }
      );
      List<String> itemNameList = ArrayUtils.asList(new String[]{"组分浓度", "毒性当量因子", "换算浓度"});
      List<String> bzyCodeList = Arrays.asList("P-DUP");
      List<EmcOrderTaskResultBean> qcResultLists = templateSourceDTO.getQcTaskResultList()
         .stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && "1".equals(r.getReportDisplay()) && bzyCodeList.contains(r.getQcCategoryCode()))
         .collect(Collectors.toList());
      List<EmcReportResultBean> qcReportResultList = this.changResultToReport(qcResultLists);
      List<EmcReportResultBean> reportResultList = new ArrayList<>();
      reportResultList.addAll(dtoReportResultList);
      reportResultList.addAll(qcReportResultList);
      reportResultList = reportResultList.stream()
         .filter(e -> itemNameList.stream().anyMatch(t -> StringUtils.contains(e.getItemName(), t) || StringUtils.contains(e.getItemName(), "二噁英测定浓度")))
         .collect(Collectors.toList());
      List<String> orderNoList = reportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
      orderNoList.sort(Comparator.comparing(String::toString));

      for (String orderNo : orderNoList) {
         List<EmcReportResultBean> orderNoReportResultList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
            .collect(Collectors.toList());
         AtomicInteger rowIndex = new AtomicInteger(0);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("序号");
         firstRowDataList.add("检测项目");

         for (int i = 0; i < 3; i++) {
            firstRowDataList.add("检测结果");
         }

         String unitName = orderNoReportResultList.stream()
            .filter(e -> StringUtils.startsWith(e.getItemName(), "组分浓度"))
            .findFirst()
            .orElse(new EmcReportResultBean())
            .getUnitName();
         String unitName2 = orderNoReportResultList.stream()
            .filter(e -> StringUtils.startsWith(e.getItemName(), "换算浓度"))
            .findFirst()
            .orElse(new EmcReportResultBean())
            .getUnitName();
         if (unitName2 == null) {
            unitName2 = "ng/kg";
         } else {
            unitName2 = StringUtils.replace(unitName2, "TEQ", "");
         }

         String test = "实测质量分数（w）";
         String test2 = "毒性当量（TEQ）质量分数";
         String remark = "1.毒性当量分数（TEQ）：折算为2,3,7,8-T₄CDD的质量分数。\n2.毒性当量因子（TEF）：采用国际毒性当量因子I-TEF定义。";
         if (!StringUtils.isEmpty(unitName) && unitName.endsWith("L")) {
            test = "实测质量浓度（ρ）";
            test2 = "毒性当量（TEQ）质量浓度";
            remark = "1.毒性当量浓度（TEQ）：折算为2,3,7,8-T₄CDD的质量浓度。\n2.毒性当量因子（TEF）：采用国际毒性当量因子I-TEF定义。";
         }

         firstRowDataList.add("样品检出限\n（" + unitName + "）");
         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, 4));
         rowIndex.incrementAndGet();
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            secondRowDataList.add("");
         }

         if (!"1".equals(templateSourceDTO.getProject().getSample())) {
            LocalDate planSampleDate = DateTimeUtils.parseLocalDate(orderNoReportResultList.get(0).getExt$Item("senddate"));

            for (int i = 0; i < 3; i++) {
               secondRowDataList.add("送样日期：" + planSampleDate.format(formatter));
            }
         } else {
            LocalDate planSampleDate = orderNoReportResultList.get(0).getSampleBeginDate();

            for (int i = 0; i < 3; i++) {
               secondRowDataList.add("采样日期：" + planSampleDate.format(formatter));
            }
         }

         secondRowDataList.add("");
         tableDataList.add(secondRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, 4));
         rowIndex.incrementAndGet();
         List<Object> RowDataList3 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList3.add("");
         }

         String monitorPointName = orderNoReportResultList.get(0).getMonitorPointName();
         String orderCategory = orderNoReportResultList.get(0).getOrderCategory();
         if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
            monitorPointName = monitorPointName + "(" + orderNoReportResultList.get(0).getExt$Item("samplingdepth") + "m)";
         }

         if (!"1".equals(templateSourceDTO.getProject().getSample())) {
            monitorPointName = orderNoReportResultList.get(0).getClientOrderName();
         }

         for (int i = 0; i < 3; i++) {
            RowDataList3.add(monitorPointName);
         }

         RowDataList3.add("");
         tableDataList.add(RowDataList3);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, 4));
         rowIndex.incrementAndGet();
         List<Object> RowDataList4 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList4.add("");
         }

         RowDataList4.add(test);

         for (int i = 0; i < 2; i++) {
            RowDataList4.add(test2);
         }

         RowDataList4.add("");
         tableDataList.add(RowDataList4);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 3, 4));
         rowIndex.incrementAndGet();
         List<Object> RowDataList5 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList5.add("");
         }

         RowDataList5.add(unitName);
         RowDataList5.add("TEF");
         RowDataList5.add(unitName2);
         RowDataList5.add("");
         tableDataList.add(RowDataList5);
         rowIndex.incrementAndGet();
         mergedRowList.add(new MergedRow(0, 0, 4));
         mergedRowList.add(new MergedRow(1, 0, 4));
         mergedRowList.add(new MergedRow(5, 0, 4));

         for (String itemAliasName : itemAliasNameList) {
            List<Object> resultRowDataList = new ArrayList<>();
            resultRowDataList.add(rowIndex.get() - 4);
            resultRowDataList.add(itemAliasName);

            for (String itemName : itemNameList) {
               EmcReportResultBean resultBean = orderNoReportResultList.stream()
                  .filter(e -> StringUtils.startsWith(e.getItemName(), itemName) && StringUtils.startsWith(e.getItemAliasName(), itemAliasName))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               resultRowDataList.add(this.transScientificCounting(resultBean.getReportResult()));
            }

            EmcReportResultBean resultBean = orderNoReportResultList.stream()
               .filter(e -> StringUtils.startsWith(e.getItemName(), "组分浓度") && StringUtils.startsWith(e.getItemAliasName(), itemAliasName))
               .findFirst()
               .orElse(new EmcReportResultBean());
            resultRowDataList.add(resultBean.getLowLimit());
            tableDataList.add(resultRowDataList);
            rowIndex.incrementAndGet();
         }

         List<Object> totalRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            totalRowDataList.add("二噁英测定浓度/单位：" + unitName);
         }

         EmcReportResultBean resultBean = orderNoReportResultList.stream()
            .filter(e -> StringUtils.startsWith(e.getItemName(), "二噁英测定浓度"))
            .findFirst()
            .orElse(new EmcReportResultBean());

         for (int i = 0; i < 3; i++) {
            totalRowDataList.add(this.transScientificCounting(resultBean.getReportResult()));
         }

         totalRowDataList.add("/");
         tableDataList.add(totalRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, 4));
         rowIndex.incrementAndGet();
         List<Object> remarkRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            remarkRowDataList.add("备注");
         }

         for (int i = 0; i < 4; i++) {
            remarkRowDataList.add(remark);
         }

         tableDataList.add(remarkRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, 5));
         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderNoReportResultList.get(0).getOrderCategory() + "二噁英类检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
         table.getRow(2).setRepeatHeader(true);
         table.getRow(3).setRepeatHeader(true);
         table.getRow(4).setRepeatHeader(true);
         tabelCount++;
         List<XWPFTableRow> rows = table.getRows();
         XWPFTableRow row = rows.get(rows.size() - 1);
         XWPFTableCell cell = row.getCell(2);
         ((CTP)cell.getCTTc().getPList().get(0)).addNewPPr().addNewJc().setVal(STJc.LEFT);
         int posForTable = document.getPosOfTable(table);
         XWPFParagraph paragraphAfterTable = (XWPFParagraph)document.getBodyElements().get(posForTable + 1);
         XWPFParagraph turnParagraph = document.insertNewParagraph(paragraphAfterTable.getCTP().newCursor());
         turnParagraph.createRun().addBreak(BreakType.PAGE);
      }
   }

   private int createSamplePointTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableIndex) {
      List<EmcReportResultBean> allReportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> reportResultList = allReportResultList.stream().filter(e -> "土壤".equals(e.getOrderCategory())).collect(Collectors.toList());
      if (reportResultList.isEmpty()) {
         return tableIndex;
      } else {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> fourthRowDataList = new ArrayList<>();
         fourthRowDataList.add("序号");
         fourthRowDataList.add("检测点位");
         fourthRowDataList.add("经度");
         fourthRowDataList.add("纬度");
         AtomicInteger rowIndex = new AtomicInteger(0);
         tableDataList.add(fourthRowDataList);
         rowIndex.incrementAndGet();

         for (String itemGroupName : reportResultList.stream()
            .sorted(Comparator.comparing(EmcReportResultBean::getMonitorPointName))
            .map(e -> e.getMonitorPointName())
            .distinct()
            .collect(Collectors.toList())) {
            EmcReportResultBean resultBean = reportResultList.stream()
               .filter(e -> StringUtils.startsWith(e.getMonitorPointName(), itemGroupName))
               .findFirst()
               .orElse(new EmcReportResultBean());
            List<Object> resultRowDataList = new ArrayList<>();
            resultRowDataList.add(rowIndex.get());
            resultRowDataList.add(itemGroupName);
            resultRowDataList.add(resultBean.getExt$Item("longitude"));
            resultRowDataList.add(resultBean.getExt$Item("latitude"));
            tableDataList.add(resultRowDataList);
            rowIndex.incrementAndGet();
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-1 检测点位经度纬度");
         XWPFTable table2 = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
         table2.getRow(0).setRepeatHeader(true);
         return tableIndex + 1;
      }
   }

   private int wnSampleTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int indexTable, String orderCategory) {
      String showLimit = templateSourceDTO.getReport().getExt$Item("showlimit");
      List<String> bzyCodeList = Arrays.asList("P-DUP", "Q-DUP");
      List<EmcOrderTaskResultBean> qcResultLists = templateSourceDTO.getQcTaskResultList()
         .stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && "1".equals(r.getReportDisplay()) && bzyCodeList.contains(r.getQcCategoryCode()))
         .collect(Collectors.toList());
      List<EmcReportResultBean> qcReportResultList = this.changResultToReport(qcResultLists);
      AtomicInteger tableIndex = new AtomicInteger(indexTable);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      reportResultList.addAll(qcReportResultList);
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory) && !"192".equals(e.getTestId() + ""))
         .collect(Collectors.toList());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
      List<EmcReportResultBean> multleReportResultList = orderCategoryReportResultList.stream()
         .filter(r -> r.getTestName() != null && "1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(multleReportResultList)) {
         Map<LocalDate, List<EmcReportResultBean>> dateReportResultListMap = multleReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getSampleBeginDate()));

         for (LocalDate date : dateReportResultListMap.keySet()) {
            List<EmcReportResultBean> reportResultTempList = dateReportResultListMap.get(date);
            List<EmcReportResultBean> reportResultLists = reportResultTempList.stream()
               .sorted(Comparator.comparing(e -> e.getMethodId(), Comparator.nullsLast(Long::compareTo)))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> testReportResultListMap = reportResultLists.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

            for (String testName : testReportResultListMap.keySet()) {
               List<EmcReportResultBean> testReportResultLists = testReportResultListMap.get(testName);
               List<String> monitorPointNameList = testReportResultLists.stream()
                  .sorted(Comparator.comparing(EmcReportResultBean::getOrderNo))
                  .map(e -> e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode"))
                  .distinct()
                  .collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> monitorPointReportResultListMap = testReportResultLists.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode")));
               List<String> itemNames = testReportResultLists.stream().map(e -> e.getExt$Item("itemgroup")).distinct().collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> itemReportResultListMap = testReportResultLists.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("itemgroup")));
               List<List<String>> naPartitionList = ListUtils.partition(monitorPointNameList, 4);
               IntStream.range(0, naPartitionList.size())
                  .forEach(
                     index -> {
                        List<String> curNaPartitionList = naPartitionList.get(index);
                        List<List<Object>> tableDataList = new ArrayList<>();
                        new ArrayList();
                        List<MergedColumn> mergedColumnList = new ArrayList<>();
                        AtomicInteger rowIndex = new AtomicInteger(0);
                        List<Object> firstRowDataList = new ArrayList<>();
                        firstRowDataList.add("采样日期");
                        firstRowDataList.add("");
                        firstRowDataList.add("");
                        firstRowDataList.add(date.format(formatter));

                        for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                           firstRowDataList.add("");
                        }

                        tableDataList.add(firstRowDataList);
                        rowIndex.incrementAndGet();
                        mergedColumnList.add(new MergedColumn(0, 0, 2));
                        mergedColumnList.add(new MergedColumn(0, 3, 3 + curNaPartitionList.size() - 1));
                        List<Object> secondRowDataList = new ArrayList<>();
                        secondRowDataList.add("检测点位");
                        secondRowDataList.add("");
                        secondRowDataList.add("");

                        for (String monitorPointName : curNaPartitionList) {
                           secondRowDataList.add(
                              monitorPointName.replace("-null", " ").replace("-N/A", " ").replace("P-DUP", "XP").replace("Q-DUP", "XP").replace("(", "\n(")
                           );
                        }

                        tableDataList.add(secondRowDataList);
                        rowIndex.incrementAndGet();
                        mergedColumnList.add(new MergedColumn(1, 0, 2));
                        List<Object> fourthRowDataList = new ArrayList<>();
                        fourthRowDataList.add("序号");
                        fourthRowDataList.add("检测项目");
                        fourthRowDataList.add("检出限");
                        fourthRowDataList.add("检测结果");

                        for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                           fourthRowDataList.add("");
                        }

                        mergedColumnList.add(new MergedColumn(2, 3, 3 + curNaPartitionList.size() - 1));
                        tableDataList.add(fourthRowDataList);
                        rowIndex.incrementAndGet();

                        for (String itemName : itemNames) {
                           List<Object> resultRowDataList = new ArrayList<>();
                           resultRowDataList.add(rowIndex.get() - 2);
                           List<EmcReportResultBean> collect = itemReportResultListMap.get(itemName)
                              .stream()
                              .filter(e -> !StringUtils.isEmpty(e.getLowLimit()))
                              .collect(Collectors.toList());
                           EmcReportResultBean res = itemReportResultListMap.get(itemName).get(0);
                           if (collect.size() > 1) {
                              res = collect.get(0);
                           }

                           resultRowDataList.add(itemName + "(" + res.getUnitName() + ")");
                           resultRowDataList.add(res.getLowLimit());

                           for (String monitorPointName : curNaPartitionList) {
                              EmcReportResultBean resultBean = testReportResultLists.stream()
                                 .filter(
                                    e -> StringUtils.equals(e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode"), monitorPointName)
                                          && itemName.equals(e.getExt$Item("itemgroup"))
                                 )
                                 .findFirst()
                                 .orElse(new EmcReportResultBean());
                              resultRowDataList.add(this.transScientificCounting(resultBean.getReportResult()));
                           }

                           tableDataList.add(resultRowDataList);
                           rowIndex.incrementAndGet();
                        }

                        this.listListNullToString(tableDataList, "/");
                        WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
                        wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
                        wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
                        List<Integer> columnWidthList = new ArrayList<>();
                        columnWidthList.add(650);
                        columnWidthList.add(1400);
                        columnWidthList.add(1400);

                        for (String monitorPointName : curNaPartitionList) {
                           columnWidthList.add(0);
                        }

                        wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
                        wordTemplateDynamicTableDTO.setCellHeight(400);
                        wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + testName + orderCategory + "检测结果");
                        tableIndex.incrementAndGet();
                        wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
                        XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
                        table.getRow(0).setRepeatHeader(true);
                        table.getRow(1).setRepeatHeader(true);
                        table.getRow(2).setRepeatHeader(true);
                     }
                  );
            }
         }
      }

      List<EmcReportResultBean> singleReportResultList = orderCategoryReportResultList.stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && !"1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(singleReportResultList)) {
         Map<LocalDate, List<EmcReportResultBean>> dateReportResultListMap = singleReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getSampleBeginDate()));

         for (LocalDate date : dateReportResultListMap.keySet()) {
            List<EmcReportResultBean> reportResultTempList = dateReportResultListMap.get(date);
            List<EmcReportResultBean> reportResultLists = reportResultTempList.stream()
               .sorted(Comparator.comparing(e -> e.getMethodId(), Comparator.nullsLast(Long::compareTo)))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> testReportResultListMap = reportResultLists.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
            List<String> testNames = reportResultLists.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList());
            List<List<String>> naPartitionList = ListUtils.partition(testNames, 8);
            IntStream.range(0, naPartitionList.size())
               .forEach(
                  index -> {
                     List<String> curNaPartitionList = naPartitionList.get(index);
                     List<List<Object>> tableDataList = new ArrayList<>();
                     new ArrayList();
                     List<MergedColumn> mergedColumnList = new ArrayList<>();
                     AtomicInteger rowIndex = new AtomicInteger(0);
                     List<Object> firstRowDataList = new ArrayList<>();
                     firstRowDataList.add("采样日期");
                     firstRowDataList.add("");
                     firstRowDataList.add(date.format(formatter));

                     for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                        firstRowDataList.add("");
                     }

                     tableDataList.add(firstRowDataList);
                     rowIndex.incrementAndGet();
                     mergedColumnList.add(new MergedColumn(0, 0, 1));
                     mergedColumnList.add(new MergedColumn(0, 2, 2 + curNaPartitionList.size() - 1));
                     List<Object> secondRowDataList = new ArrayList<>();
                     secondRowDataList.add("检测项目");
                     secondRowDataList.add("");
                     List<Object> thirdRowDataList = new ArrayList<>();
                     thirdRowDataList.add("检出限");
                     thirdRowDataList.add("");

                     for (String testName : curNaPartitionList) {
                        List<EmcReportResultBean> collect = testReportResultListMap.get(testName)
                           .stream()
                           .filter(e -> !StringUtils.isEmpty(e.getLowLimit()))
                           .collect(Collectors.toList());
                        EmcReportResultBean res = testReportResultListMap.get(testName).get(0);
                        if (collect.size() > 1) {
                           res = collect.get(0);
                        }

                        secondRowDataList.add(testName + "(" + res.getUnitName() + ")");
                        thirdRowDataList.add(res.getLowLimit() == null ? "/" : res.getLowLimit());
                     }

                     tableDataList.add(secondRowDataList);
                     rowIndex.incrementAndGet();
                     mergedColumnList.add(new MergedColumn(1, 0, 1));
                     mergedColumnList.add(new MergedColumn(2, 0, 1));
                     tableDataList.add(thirdRowDataList);
                     rowIndex.incrementAndGet();
                     List<Object> fourthRowDataList = new ArrayList<>();
                     fourthRowDataList.add("序号");
                     fourthRowDataList.add("检测点位");
                     fourthRowDataList.add("检测结果");

                     for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                        fourthRowDataList.add("");
                     }

                     mergedColumnList.add(new MergedColumn(3, 2, 2 + curNaPartitionList.size() - 1));
                     tableDataList.add(fourthRowDataList);
                     rowIndex.incrementAndGet();

                     for (String itemGroupName : reportResultLists.stream()
                        .sorted(Comparator.comparing(EmcReportResultBean::getOrderNo))
                        .map(e -> e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode"))
                        .distinct()
                        .collect(Collectors.toList())) {
                        List<Object> resultRowDataList = new ArrayList<>();
                        resultRowDataList.add(rowIndex.get() - 3);
                        resultRowDataList.add(
                           itemGroupName.replace("-null", " ").replace("-N/A", " ").replace("P-DUP", "XP").replace("Q-DUP", "XP").replace("(", "\n(")
                        );

                        for (String testName : curNaPartitionList) {
                           EmcReportResultBean resultBean = reportResultLists.stream()
                              .filter(
                                 e -> StringUtils.equals(e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode"), itemGroupName)
                                       && testName.equals(e.getExt$Item("reportTestName"))
                              )
                              .findFirst()
                              .orElse(new EmcReportResultBean());
                           resultRowDataList.add(this.transScientificCounting(resultBean.getReportResult()));
                        }

                        tableDataList.add(resultRowDataList);
                        rowIndex.incrementAndGet();
                     }

                     List<Object> lastRowDataList = new ArrayList<>();
                     lastRowDataList.add("标准限值");
                     lastRowDataList.add("标准限值");

                     for (String itemName : curNaPartitionList) {
                        EmcReportResultBean resultBean = reportResultLists.stream()
                           .filter(e -> itemName.equals(e.getExt$Item("itemgroup")))
                           .findFirst()
                           .orElse(new EmcReportResultBean());
                        lastRowDataList.add(this.transScientificCounting(resultBean.getIndexHighLimit()));
                     }

                     if ("1".equals(showLimit)) {
                        tableDataList.add(lastRowDataList);
                     }

                     rowIndex.incrementAndGet();
                     this.listListNullToString(tableDataList, "/");
                     WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
                     wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
                     wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
                     List<Integer> columnWidthList = new ArrayList<>();
                     columnWidthList.add(650);
                     columnWidthList.add(1400);

                     for (String testName : curNaPartitionList) {
                        columnWidthList.add(0);
                     }

                     wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
                     wordTemplateDynamicTableDTO.setCellHeight(400);
                     wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + orderCategory + "检测结果");
                     tableIndex.incrementAndGet();
                     wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
                     XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
                  }
               );
         }
      }

      return tableIndex.get();
   }

   private int sampleCommonReportTableNew(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int indexTable, String orderCategory) {
      String showLimit = templateSourceDTO.getReport().getExt$Item("showlimit");
      List<String> bzyCodeList = Arrays.asList("P-DUP", "Q-DUP");
      List<EmcOrderTaskResultBean> qcResultLists = templateSourceDTO.getQcTaskResultList()
         .stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && "1".equals(r.getReportDisplay()) && bzyCodeList.contains(r.getQcCategoryCode()))
         .collect(Collectors.toList());
      List<EmcReportResultBean> qcReportResultList = this.changResultToReport(qcResultLists);
      AtomicInteger tableIndex = new AtomicInteger(indexTable);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      reportResultList.addAll(qcReportResultList);
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory) && !"192".equals(e.getTestId() + ""))
         .collect(Collectors.toList());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
      List<EmcReportResultBean> multleReportResultList = orderCategoryReportResultList.stream()
         .filter(r -> r.getTestName() != null && "1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(multleReportResultList)) {
         Map<LocalDate, List<EmcReportResultBean>> dateReportResultListMap = multleReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getSampleBeginDate()));

         for (LocalDate date : dateReportResultListMap.keySet()) {
            List<EmcReportResultBean> reportResultTempList = dateReportResultListMap.get(date);
            List<EmcReportResultBean> reportResultLists = reportResultTempList.stream()
               .sorted(Comparator.comparing(e -> e.getMethodId(), Comparator.nullsLast(Long::compareTo)))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> testReportResultListMap = reportResultLists.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

            for (String testName : testReportResultListMap.keySet()) {
               List<EmcReportResultBean> testReportResultLists = testReportResultListMap.get(testName);
               List<String> monitorPointNameList = testReportResultLists.stream()
                  .sorted(Comparator.comparing(EmcReportResultBean::getOrderNo))
                  .map(e -> e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode") + "(" + e.getExt$Item("samplingdepth") + "m)")
                  .distinct()
                  .collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> monitorPointReportResultListMap = testReportResultLists.stream()
                  .collect(
                     Collectors.groupingBy(
                        e -> e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode") + "(" + e.getExt$Item("samplingdepth") + "m)"
                     )
                  );
               List<String> itemNames = testReportResultLists.stream().map(e -> e.getExt$Item("itemgroup")).distinct().collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> itemReportResultListMap = testReportResultLists.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("itemgroup")));
               List<List<String>> naPartitionList = ListUtils.partition(monitorPointNameList, 4);
               IntStream.range(0, naPartitionList.size())
                  .forEach(
                     index -> {
                        List<String> curNaPartitionList = naPartitionList.get(index);
                        List<List<Object>> tableDataList = new ArrayList<>();
                        new ArrayList();
                        List<MergedColumn> mergedColumnList = new ArrayList<>();
                        AtomicInteger rowIndex = new AtomicInteger(0);
                        List<Object> firstRowDataList = new ArrayList<>();
                        firstRowDataList.add("采样日期");
                        firstRowDataList.add("");
                        firstRowDataList.add("");
                        firstRowDataList.add(date.format(formatter));

                        for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                           firstRowDataList.add("");
                        }

                        tableDataList.add(firstRowDataList);
                        rowIndex.incrementAndGet();
                        mergedColumnList.add(new MergedColumn(0, 0, 2));
                        mergedColumnList.add(new MergedColumn(0, 3, 3 + curNaPartitionList.size() - 1));
                        List<Object> secondRowDataList = new ArrayList<>();
                        secondRowDataList.add("检测点位");
                        secondRowDataList.add("");
                        secondRowDataList.add("");

                        for (String monitorPointName : curNaPartitionList) {
                           secondRowDataList.add(
                              monitorPointName.replace("-null", " ").replace("-N/A", " ").replace("P-DUP", "XP").replace("Q-DUP", "XP").replace("(", "\n(")
                           );
                        }

                        tableDataList.add(secondRowDataList);
                        rowIndex.incrementAndGet();
                        mergedColumnList.add(new MergedColumn(1, 0, 2));
                        List<Object> fourthRowDataList = new ArrayList<>();
                        fourthRowDataList.add("序号");
                        fourthRowDataList.add("检测项目");
                        fourthRowDataList.add("检出限");
                        fourthRowDataList.add("检测结果");

                        for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                           fourthRowDataList.add("");
                        }

                        mergedColumnList.add(new MergedColumn(2, 3, 3 + curNaPartitionList.size() - 1));
                        tableDataList.add(fourthRowDataList);
                        rowIndex.incrementAndGet();

                        for (String itemName : itemNames) {
                           List<Object> resultRowDataList = new ArrayList<>();
                           resultRowDataList.add(rowIndex.get() - 2);
                           List<EmcReportResultBean> collect = itemReportResultListMap.get(itemName)
                              .stream()
                              .filter(e -> !StringUtils.isEmpty(e.getLowLimit()))
                              .collect(Collectors.toList());
                           EmcReportResultBean res = itemReportResultListMap.get(itemName).get(0);
                           if (collect.size() > 1) {
                              res = collect.get(0);
                           }

                           resultRowDataList.add(itemName + "(" + res.getUnitName() + ")");
                           resultRowDataList.add(res.getLowLimit());

                           for (String monitorPointName : curNaPartitionList) {
                              EmcReportResultBean resultBean = testReportResultLists.stream()
                                 .filter(
                                    e -> StringUtils.equals(
                                             e.getExt$Item("reportmonitorpointname")
                                                + "-"
                                                + e.getExt$Item("qccategorycode")
                                                + "("
                                                + e.getExt$Item("samplingdepth")
                                                + "m)",
                                             monitorPointName
                                          )
                                          && itemName.equals(e.getExt$Item("itemgroup"))
                                 )
                                 .findFirst()
                                 .orElse(new EmcReportResultBean());
                              resultRowDataList.add(this.transScientificCounting(resultBean.getReportResult()));
                           }

                           tableDataList.add(resultRowDataList);
                           rowIndex.incrementAndGet();
                        }

                        this.listListNullToString(tableDataList, "/");
                        WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
                        wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
                        wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
                        List<Integer> columnWidthList = new ArrayList<>();
                        columnWidthList.add(650);
                        columnWidthList.add(1400);
                        columnWidthList.add(1400);

                        for (String monitorPointName : curNaPartitionList) {
                           columnWidthList.add(0);
                        }

                        wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
                        wordTemplateDynamicTableDTO.setCellHeight(400);
                        wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " 土壤" + testName + "检测结果");
                        tableIndex.incrementAndGet();
                        wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
                        XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
                        table.getRow(0).setRepeatHeader(true);
                        table.getRow(1).setRepeatHeader(true);
                        table.getRow(2).setRepeatHeader(true);
                     }
                  );
            }
         }
      }

      List<EmcReportResultBean> singleReportResultList = orderCategoryReportResultList.stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && !"1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(singleReportResultList)) {
         Map<LocalDate, List<EmcReportResultBean>> dateReportResultListMap = singleReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getSampleBeginDate()));

         for (LocalDate date : dateReportResultListMap.keySet()) {
            List<EmcReportResultBean> reportResultTempList = dateReportResultListMap.get(date);
            List<EmcReportResultBean> reportResultLists = reportResultTempList.stream()
               .sorted(Comparator.comparing(e -> e.getMethodId(), Comparator.nullsLast(Long::compareTo)))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> testReportResultListMap = reportResultLists.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
            List<String> testNames = reportResultLists.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList());
            List<List<String>> naPartitionList = ListUtils.partition(testNames, 8);
            IntStream.range(0, naPartitionList.size())
               .forEach(
                  index -> {
                     List<String> curNaPartitionList = naPartitionList.get(index);
                     List<List<Object>> tableDataList = new ArrayList<>();
                     new ArrayList();
                     List<MergedColumn> mergedColumnList = new ArrayList<>();
                     AtomicInteger rowIndex = new AtomicInteger(0);
                     List<Object> firstRowDataList = new ArrayList<>();
                     firstRowDataList.add("采样日期");
                     firstRowDataList.add("");
                     firstRowDataList.add(date.format(formatter));

                     for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                        firstRowDataList.add("");
                     }

                     tableDataList.add(firstRowDataList);
                     rowIndex.incrementAndGet();
                     mergedColumnList.add(new MergedColumn(0, 0, 1));
                     mergedColumnList.add(new MergedColumn(0, 2, 2 + curNaPartitionList.size() - 1));
                     List<Object> secondRowDataList = new ArrayList<>();
                     secondRowDataList.add("检测项目");
                     secondRowDataList.add("");
                     List<Object> thirdRowDataList = new ArrayList<>();
                     thirdRowDataList.add("检出限");
                     thirdRowDataList.add("");

                     for (String testName : curNaPartitionList) {
                        List<EmcReportResultBean> collect = testReportResultListMap.get(testName)
                           .stream()
                           .filter(e -> !StringUtils.isEmpty(e.getLowLimit()))
                           .collect(Collectors.toList());
                        EmcReportResultBean res = testReportResultListMap.get(testName).get(0);
                        if (collect.size() > 1) {
                           res = collect.get(0);
                        }

                        secondRowDataList.add(testName + "(" + res.getUnitName() + ")");
                        thirdRowDataList.add(res.getLowLimit() == null ? "/" : res.getLowLimit());
                     }

                     tableDataList.add(secondRowDataList);
                     rowIndex.incrementAndGet();
                     mergedColumnList.add(new MergedColumn(1, 0, 1));
                     mergedColumnList.add(new MergedColumn(2, 0, 1));
                     tableDataList.add(thirdRowDataList);
                     rowIndex.incrementAndGet();
                     List<Object> fourthRowDataList = new ArrayList<>();
                     fourthRowDataList.add("序号");
                     fourthRowDataList.add("检测点位");
                     fourthRowDataList.add("检测结果");

                     for (long l = 0L; l < (long)(curNaPartitionList.size() - 1); l++) {
                        fourthRowDataList.add("");
                     }

                     mergedColumnList.add(new MergedColumn(3, 2, 2 + curNaPartitionList.size() - 1));
                     tableDataList.add(fourthRowDataList);
                     rowIndex.incrementAndGet();

                     for (String itemGroupName : reportResultLists.stream()
                        .sorted(Comparator.comparing(EmcReportResultBean::getOrderNo))
                        .map(e -> e.getExt$Item("reportmonitorpointname") + "-" + e.getExt$Item("qccategorycode") + "(" + e.getExt$Item("samplingdepth") + "m)")
                        .distinct()
                        .collect(Collectors.toList())) {
                        List<Object> resultRowDataList = new ArrayList<>();
                        resultRowDataList.add(rowIndex.get() - 3);
                        resultRowDataList.add(
                           itemGroupName.replace("-null", " ").replace("-N/A", " ").replace("P-DUP", "XP").replace("Q-DUP", "XP").replace("(", "\n(")
                        );

                        for (String testName : curNaPartitionList) {
                           EmcReportResultBean resultBean = reportResultLists.stream()
                              .filter(
                                 e -> StringUtils.equals(
                                          e.getExt$Item("reportmonitorpointname")
                                             + "-"
                                             + e.getExt$Item("qccategorycode")
                                             + "("
                                             + e.getExt$Item("samplingdepth")
                                             + "m)",
                                          itemGroupName
                                       )
                                       && testName.equals(e.getExt$Item("reportTestName"))
                              )
                              .findFirst()
                              .orElse(new EmcReportResultBean());
                           resultRowDataList.add(this.transScientificCounting(resultBean.getReportResult()));
                        }

                        tableDataList.add(resultRowDataList);
                        rowIndex.incrementAndGet();
                     }

                     List<Object> lastRowDataList = new ArrayList<>();
                     lastRowDataList.add("标准限值");
                     lastRowDataList.add("标准限值");

                     for (String itemName : curNaPartitionList) {
                        EmcReportResultBean resultBean = reportResultLists.stream()
                           .filter(e -> itemName.equals(e.getExt$Item("itemgroup")))
                           .findFirst()
                           .orElse(new EmcReportResultBean());
                        lastRowDataList.add(this.transScientificCounting(resultBean.getIndexHighLimit()));
                     }

                     if ("1".equals(showLimit)) {
                        tableDataList.add(lastRowDataList);
                     }

                     rowIndex.incrementAndGet();
                     this.listListNullToString(tableDataList, "/");
                     WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
                     wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
                     wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
                     List<Integer> columnWidthList = new ArrayList<>();
                     columnWidthList.add(650);
                     columnWidthList.add(1400);

                     for (String testName : curNaPartitionList) {
                        columnWidthList.add(0);
                     }

                     wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
                     wordTemplateDynamicTableDTO.setCellHeight(400);
                     wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " 土壤检测结果");
                     tableIndex.incrementAndGet();
                     wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
                     XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
                     table.getRow(0).setRepeatHeader(true);
                     table.getRow(1).setRepeatHeader(true);
                     table.getRow(2).setRepeatHeader(true);
                     table.getRow(3).setRepeatHeader(true);
                  }
               );
         }
      }

      return tableIndex.get();
   }

   private List<EmcReportResultBean> changResultToReport(List<EmcOrderTaskResultBean> qcResultLists) {
      List<EmcReportResultBean> qcReportResultList = new ArrayList<>();
      if (!CollectionUtils.isEmpty(qcResultLists)) {
         List<EmcOrderBean> orderList = this.orderService
            .selectListByFilter(SearchFilter.instance().match("PROJID", qcResultLists.get(0).getProjId()).filter(MatchPattern.OR), new Order[0]);
         Map<Long, EmcOrderBean> orderIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));
         qcResultLists.stream().forEach(taskResult -> {
            EmcReportResultBean reportResult = new EmcReportResultBean();
            reportResult.setId(ApplicationContextHelper.getNextIdentity());
            reportResult.setDataType("sample");
            this.copyProperties(reportResult, orderIdMap.get(taskResult.getOrderId()), taskResult);
            reportResult.setExt$Item("multipleitemgroup", taskResult.getExt$Item("multipleitemgroup"));
            reportResult.setExt$Item("reportmonitorpointname", taskResult.getExt$Item("reportmonitorpointname"));
            reportResult.setMonitorPointName(taskResult.getExt$Item("monitorpointname"));
            reportResult.setExt$Item("reportTestName", taskResult.getExt$Item("reportTestName"));
            reportResult.setExt$Item("itemgroup", taskResult.getExt$Item("itemgroup"));
            reportResult.setExt$Item("samplingdepth", taskResult.getExt$Item("samplingdepth"));
            reportResult.setExt$Item("qccategorycode", taskResult.getQcCategoryCode());
            reportResult.setExt$Item("allowableerror", taskResult.getExt$Item("allowableerror"));
            qcReportResultList.add(reportResult);
         });
      }

      return qcReportResultList;
   }

   private int sampleCommonReportTableWithMultipleAndSingle(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory
   ) {
      List<String> bzyCodeList = Arrays.asList("P-DUP", "Q-DUP");
      AtomicInteger tableIndex = new AtomicInteger(tabelCount);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcOrderTaskResultBean> qcTaskResultList = templateSourceDTO.getQcTaskResultList();
      qcTaskResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
      List<EmcReportResultBean> singleReportResultList = reportResultList.stream()
         .filter(
            result -> StringUtils.equals(result.getDataType(), "sample")
                  && ObjectUtils.equals(result.getOrderCategory(), orderCategory)
                  && !"1".equals(result.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!singleReportResultList.isEmpty()) {
         singleReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         List<EmcOrderTaskResultBean> orderCategoryQcTaskResultList = qcTaskResultList.stream()
            .filter(
               e -> ObjectUtils.equals(e.getExt$Item("orderCategory"), orderCategory)
                     && bzyCodeList.contains(e.getQcCategoryCode())
                     && "1".equals(e.getReportDisplay())
                     && !"1".equals(e.getExt$Item("multipleitemgroup"))
            )
            .collect(Collectors.toList());
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("样品编号");
         firstRowDataList.add("样品状态");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("点位名称");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);
         List<String> orderNoList = singleReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
         orderNoList.sort(Comparator.comparing(String::toString));

         for (String orderNo : orderNoList) {
            List<EmcReportResultBean> orderNoReportResultList = singleReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName"), LinkedHashMap::new, Collectors.toList()));
            itemGroupReportResultListMap.forEach((key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(orderNo);
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               EmcReportResultBean curReportResult = curReportResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
               if (curReportResult == null) {
                  curReportResult = curReportResultList.stream().filter(e -> e.getUnitName() != null).findFirst().orElse(curReportResultList.get(0));
               }

               String resultTitle = StringUtils.isBlank(curReportResult.getUnitName()) ? itemgroup : itemgroup + "(" + curReportResult.getUnitName() + ")";
               rowDataList.add(curReportResult.getOrderDesc());
               rowDataList.add(resultTitle);
               rowDataList.add(curReportResult.getExt$Item("reportmonitorpointname"));
               String showResult = this.transScientificCounting(curReportResult.getReportResult());
               rowDataList.add(showResult);
               tableDataList.add(rowDataList);
            });
         }

         List<String> qcOrderNoList = orderCategoryQcTaskResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
         qcOrderNoList.sort(Comparator.comparing(String::toString));

         for (String qcOrderNo : qcOrderNoList) {
            List<EmcOrderTaskResultBean> qcOrderNoResultList = orderCategoryQcTaskResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getOrderNo(), qcOrderNo))
               .collect(Collectors.toList());
            Map<String, List<EmcOrderTaskResultBean>> itemGroupResultListMap = qcOrderNoResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName"), LinkedHashMap::new, Collectors.toList()));
            itemGroupResultListMap.forEach((key, curResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(qcOrderNo);
               String itemgroup = curResultList.get(0).getExt$Item("reportTestName");
               EmcOrderTaskResultBean curResult = curResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
               if (curResult == null) {
                  curResult = curResultList.stream().filter(e -> e.getUnitName() != null).findFirst().orElse(curResultList.get(0));
               }

               String resultTitle = StringUtils.isBlank(curResult.getUnitName()) ? itemgroup : itemgroup + "(" + curResult.getUnitName() + ")";
               rowDataList.add(curResult.getExt$Item("orderdesc"));
               rowDataList.add(resultTitle);
               rowDataList.add(curResult.getExt$Item("monitorpointname"));
               String result = StringUtils.isBlank(curResult.getAvgResult()) ? curResult.getReportResult() : curResult.getAvgResult();
               String showResult = this.transScientificCounting(result);
               rowDataList.add(showResult);
               tableDataList.add(rowDataList);
            });
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + orderCategory + "检测结果");
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         tableIndex.incrementAndGet();
      }

      List<EmcReportResultBean> mulitResultList = reportResultList.stream()
         .filter(
            result -> StringUtils.equals(result.getDataType(), "sample")
                  && ObjectUtils.equals(result.getOrderCategory(), orderCategory)
                  && "1".equals(result.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!mulitResultList.isEmpty()) {
         List<EmcOrderTaskResultBean> qcMulitResultList = qcTaskResultList.stream()
            .filter(
               e -> ObjectUtils.equals(e.getExt$Item("orderCategory"), orderCategory)
                     && bzyCodeList.contains(e.getQcCategoryCode())
                     && "1".equals(e.getReportDisplay())
                     && "1".equals(e.getExt$Item("multipleitemgroup"))
            )
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> testReportResultListMap = mulitResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName"), LinkedHashMap::new, Collectors.toList()));

         for (String testName : mulitResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            List<List<Object>> tableDataList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();
            firstRowDataList.add("样品编号");
            firstRowDataList.add("样品状态");
            firstRowDataList.add("检测项目");
            firstRowDataList.add("点位名称");
            firstRowDataList.add("检测结果");
            tableDataList.add(firstRowDataList);
            List<EmcReportResultBean> testReportResultList = testReportResultListMap.get(testName);
            List<String> orderNoList = testReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
            orderNoList.sort(Comparator.comparing(String::toString));

            for (String orderNo : orderNoList) {
               List<EmcReportResultBean> orderNoReportResultList = testReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
                  .collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup"), LinkedHashMap::new, Collectors.toList()));
               itemGroupReportResultListMap.forEach((key, curReportResultList) -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(orderNo);
                  String itemgroup = curReportResultList.get(0).getExt$Item("itemGroup");
                  EmcReportResultBean curReportResult = curReportResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
                  if (curReportResult == null) {
                     curReportResult = curReportResultList.stream().filter(e -> e.getUnitName() != null).findFirst().orElse(curReportResultList.get(0));
                  }

                  String resultTitle = StringUtils.isBlank(curReportResult.getUnitName()) ? itemgroup : itemgroup + "(" + curReportResult.getUnitName() + ")";
                  rowDataList.add(curReportResult.getOrderDesc());
                  rowDataList.add(resultTitle);
                  rowDataList.add(curReportResult.getExt$Item("reportmonitorpointname"));
                  String showResult = this.transScientificCounting(curReportResult.getReportResult());
                  rowDataList.add(showResult);
                  tableDataList.add(rowDataList);
               });
            }

            List<EmcOrderTaskResultBean> qcTestReportResultList = qcMulitResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), testName))
               .collect(Collectors.toList());
            List<String> qcOrderNoList = qcTestReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
            qcOrderNoList.sort(Comparator.comparing(String::toString));

            for (String qcOrderNo : qcOrderNoList) {
               List<EmcOrderTaskResultBean> qcOrderNoResultList = qcTestReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getOrderNo(), qcOrderNo))
                  .collect(Collectors.toList());
               Map<String, List<EmcOrderTaskResultBean>> itemGroupResultListMap = qcOrderNoResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup"), LinkedHashMap::new, Collectors.toList()));
               itemGroupResultListMap.forEach((key, curResultList) -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(qcOrderNo);
                  String itemgroup = curResultList.get(0).getExt$Item("itemGroup");
                  EmcOrderTaskResultBean curResult = curResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
                  if (curResult == null) {
                     curResult = curResultList.stream().filter(e -> e.getUnitName() != null).findFirst().orElse(curResultList.get(0));
                  }

                  String resultTitle = StringUtils.isBlank(curResult.getUnitName()) ? itemgroup : itemgroup + "(" + curResult.getUnitName() + ")";
                  rowDataList.add(curResult.getExt$Item("orderdesc"));
                  rowDataList.add(resultTitle);
                  rowDataList.add(curResult.getExt$Item("monitorpointname"));
                  String result = StringUtils.isBlank(curResult.getAvgResult()) ? curResult.getReportResult() : curResult.getAvgResult();
                  String showResult = this.transScientificCounting(result);
                  rowDataList.add(showResult);
                  tableDataList.add(rowDataList);
               });
            }

            this.listListNullToString(tableDataList, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + orderCategory + testName + "检测结果");
            XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            table.getRow(0).setRepeatHeader(true);
            tableIndex.incrementAndGet();
         }
      }

      return tableIndex.get();
   }

   private int sampleCommonSplitReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<String> bzyCodeList = Arrays.asList("P-DUP", "Q-DUP");
      if ("地表水".equals(orderCategory)) {
         bzyCodeList = Arrays.asList("Q-DUP");
      }

      List<String> finalBzyCodeList = bzyCodeList;
      List<EmcOrderTaskResultBean> qcResultLists = templateSourceDTO.getQcTaskResultList()
         .stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && "1".equals(r.getReportDisplay()) && finalBzyCodeList.contains(r.getQcCategoryCode()))
         .collect(Collectors.toList());
      List<EmcReportResultBean> qcReportResultList = this.changResultToReport(qcResultLists);
      List<EmcReportResultBean> allResultList = new ArrayList<>();
      allResultList.addAll(reportResultList);
      allResultList.addAll(qcReportResultList);
      List<EmcReportResultBean> fsxReportResultList = reportResultList.stream()
         .filter(s -> ("总α放射性".equals(s.getTestName()) || "总β放射性".equals(s.getTestName())) && ObjectUtils.equals(s.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      if (!fsxReportResultList.isEmpty()) {
         tableCount = this.fsxSampleReportTable(document, templateSourceDTO, tableCount, fsxReportResultList, orderCategory);
      }

      AtomicInteger tableIndex = new AtomicInteger(tableCount);
      String showLimit = templateSourceDTO.getReport().getExt$Item("showlimit");
      int mergeIndex = 2;
      int startIndex = 3;
      if ("1".equals(showLimit)) {
         mergeIndex = 3;
         startIndex = 4;
      }

      List<EmcReportResultBean> orderCategoryReportResultList = allResultList.stream()
         .filter(
            result -> StringUtils.equals(result.getDataType(), "sample")
                  && ObjectUtils.equals(result.getOrderCategory(), orderCategory)
                  && !"总α放射性".equals(result.getTestName())
                  && !"总β放射性".equals(result.getTestName())
                  && !"1".equals(result.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(orderCategoryReportResultList)) {
         List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
         orderNoList.sort(Comparator.comparing(String::toString));

         for (List<String> curOrderNoList : this.splitList(orderNoList, 4)) {
            List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
               .filter(e -> curOrderNoList.contains(e.getOrderNo()))
               .collect(Collectors.toList());
            AtomicInteger rowIndex = new AtomicInteger(0);
            List<MergedColumn> mergedColumnList = new ArrayList<>();
            List<List<Object>> tableDataList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();

            for (int i = 0; i < startIndex; i++) {
               firstRowDataList.add("样品编号");
            }

            for (String orderNo : curOrderNoList) {
               firstRowDataList.add(orderNo);
            }

            tableDataList.add(firstRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
            rowIndex.incrementAndGet();
            List<Object> secondRowDataList = new ArrayList<>();

            for (int i = 0; i < startIndex; i++) {
               secondRowDataList.add("检测地点");
            }

            for (String orderNo : curOrderNoList) {
               EmcReportResultBean oderRes = orderNoReportResultList.stream()
                  .filter(e -> StringUtils.equals(e.getOrderNo(), orderNo))
                  .collect(Collectors.toList())
                  .get(0);
               secondRowDataList.add(oderRes.getExt$Item("reportmonitorpointname"));
            }

            tableDataList.add(secondRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
            rowIndex.incrementAndGet();
            List<Object> thirdRowDataList = new ArrayList<>();

            for (int i = 0; i < startIndex; i++) {
               thirdRowDataList.add("样品状态");
            }

            for (String orderNo : curOrderNoList) {
               EmcReportResultBean oderRes = orderNoReportResultList.stream()
                  .filter(e -> StringUtils.equals(e.getOrderNo(), orderNo))
                  .collect(Collectors.toList())
                  .get(0);
               thirdRowDataList.add(oderRes.getOrderDesc());
            }

            tableDataList.add(thirdRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
            rowIndex.incrementAndGet();
            List<Object> fourthRowDataList = new ArrayList<>();

            for (int i = 0; i < startIndex; i++) {
               fourthRowDataList.add("采样时间");
            }

            for (String orderNo : curOrderNoList) {
               EmcReportResultBean oderRes = orderNoReportResultList.stream()
                  .filter(e -> StringUtils.equals(e.getOrderNo(), orderNo))
                  .collect(Collectors.toList())
                  .get(0);
               fourthRowDataList.add(
                  DateTimeUtils.formatLocalDate(oderRes.getSampleBeginDate(), "yyyy年MM月dd日")
                     + "\n"
                     + (oderRes.getSampleBeginTime() == null ? "/" : oderRes.getSampleBeginTime())
               );
            }

            tableDataList.add(fourthRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
            rowIndex.incrementAndGet();
            List<Object> fifthRowDataList = new ArrayList<>();
            fifthRowDataList.add("检测项目");
            fifthRowDataList.add("单位");
            if ("1".equals(showLimit)) {
               fifthRowDataList.add("检测限值");
            }

            fifthRowDataList.add("检出限");

            for (String orderNo : curOrderNoList) {
               fifthRowDataList.add("检测结果");
            }

            tableDataList.add(fifthRowDataList);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), startIndex, mergeIndex + curOrderNoList.size()));
            rowIndex.incrementAndGet();

            for (String itemGroupName : orderNoReportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
               List<Object> resultRowDataList = new ArrayList<>();
               EmcReportResultBean resultBean = orderNoReportResultList.stream()
                  .filter(e -> StringUtils.equals(e.getExt$Item("reportTestName"), itemGroupName))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               resultRowDataList.add(itemGroupName);
               resultRowDataList.add(resultBean.getUnitName());
               if ("1".equals(showLimit)) {
                  resultRowDataList.add(resultBean.getIndexHighLimit());
               }

               resultRowDataList.add(resultBean.getLowLimit());

               for (String orderNo : curOrderNoList) {
                  EmcReportResultBean curOrderNoReportResult = allResultList.stream()
                     .filter(
                        e -> (
                                 StringUtils.equals(e.getExt$Item("reportTestName"), itemGroupName)
                                    || StringUtils.equals(e.getExt$Item("itemGroup"), itemGroupName)
                              )
                              && ObjectUtils.equals(orderNo, e.getOrderNo())
                     )
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  resultRowDataList.add(this.transScientificCounting(curOrderNoReportResult.getReportResult()));
               }

               tableDataList.add(resultRowDataList);
               rowIndex.incrementAndGet();
            }

            this.listListNullToString(tableDataList, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
            List<Integer> columnWidthList = new ArrayList<>();
            columnWidthList.add(1400);
            columnWidthList.add(1000);
            columnWidthList.add(1000);
            if ("1".equals(showLimit)) {
               columnWidthList.add(1000);
            }

            for (String orderNo : curOrderNoList) {
               columnWidthList.add(0);
            }

            wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + orderCategory + "检测结果");
            wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
            XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            tableIndex.incrementAndGet();
            table.getRow(0).setRepeatHeader(true);
            table.getRow(1).setRepeatHeader(true);
            table.getRow(2).setRepeatHeader(true);
            table.getRow(3).setRepeatHeader(true);
            table.getRow(4).setRepeatHeader(true);
         }
      }

      List<EmcReportResultBean> mulitResultList = allResultList.stream()
         .filter(
            result -> StringUtils.equals(result.getDataType(), "sample")
                  && ObjectUtils.equals(result.getOrderCategory(), orderCategory)
                  && !"总α放射性".equals(result.getTestName())
                  && !"总β放射性".equals(result.getTestName())
                  && "1".equals(result.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(mulitResultList)) {
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = mulitResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : mulitResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            List<EmcReportResultBean> reportResultBeans = itemGroupReportResultListMap.get(testName);
            List<String> orderNoList = reportResultBeans.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
            orderNoList.sort(Comparator.comparing(String::toString));

            for (List<String> curOrderNoList : this.splitList(orderNoList, 4)) {
               List<EmcReportResultBean> orderNoReportResultList = reportResultBeans.stream()
                  .filter(e -> curOrderNoList.contains(e.getOrderNo()))
                  .collect(Collectors.toList());
               AtomicInteger rowIndex = new AtomicInteger(0);
               List<MergedColumn> mergedColumnList = new ArrayList<>();
               List<List<Object>> tableDataList = new ArrayList<>();
               List<Object> firstRowDataList = new ArrayList<>();

               for (int i = 0; i < startIndex; i++) {
                  firstRowDataList.add("样品编号");
               }

               for (String orderNo : curOrderNoList) {
                  firstRowDataList.add(orderNo);
               }

               tableDataList.add(firstRowDataList);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
               rowIndex.incrementAndGet();
               List<Object> secondRowDataList = new ArrayList<>();

               for (int i = 0; i < startIndex; i++) {
                  secondRowDataList.add("检测地点");
               }

               for (String orderNo : curOrderNoList) {
                  EmcReportResultBean oderRes = orderNoReportResultList.stream()
                     .filter(e -> StringUtils.equals(e.getOrderNo(), orderNo))
                     .collect(Collectors.toList())
                     .get(0);
                  secondRowDataList.add(oderRes.getExt$Item("reportmonitorpointname"));
               }

               tableDataList.add(secondRowDataList);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
               rowIndex.incrementAndGet();
               List<Object> thirdRowDataList = new ArrayList<>();

               for (int i = 0; i < startIndex; i++) {
                  thirdRowDataList.add("样品状态");
               }

               for (String orderNo : curOrderNoList) {
                  EmcReportResultBean oderRes = orderNoReportResultList.stream()
                     .filter(e -> StringUtils.equals(e.getOrderNo(), orderNo))
                     .collect(Collectors.toList())
                     .get(0);
                  thirdRowDataList.add(oderRes.getOrderDesc());
               }

               tableDataList.add(thirdRowDataList);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
               rowIndex.incrementAndGet();
               List<Object> fourthRowDataList = new ArrayList<>();

               for (int i = 0; i < startIndex; i++) {
                  fourthRowDataList.add("采样时间");
               }

               for (String orderNo : curOrderNoList) {
                  EmcReportResultBean oderRes = orderNoReportResultList.stream()
                     .filter(e -> StringUtils.equals(e.getOrderNo(), orderNo))
                     .collect(Collectors.toList())
                     .get(0);
                  fourthRowDataList.add(
                     DateTimeUtils.formatLocalDate(oderRes.getSampleBeginDate(), "yyyy年MM月dd日")
                        + "\n"
                        + (oderRes.getSampleBeginTime() == null ? "/" : oderRes.getSampleBeginTime())
                  );
               }

               tableDataList.add(fourthRowDataList);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, mergeIndex));
               rowIndex.incrementAndGet();
               List<Object> fifthRowDataListx = new ArrayList<>();
               fifthRowDataListx.add("检测项目");
               fifthRowDataListx.add("单位");
               if ("1".equals(showLimit)) {
                  fifthRowDataListx.add("检测限值");
               }

               fifthRowDataListx.add("检出限");

               for (String orderNo : curOrderNoList) {
                  fifthRowDataListx.add("检测结果");
               }

               tableDataList.add(fifthRowDataListx);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), startIndex, mergeIndex + curOrderNoList.size()));
               rowIndex.incrementAndGet();

               for (String itemGroupName : orderNoReportResultList.stream().map(e -> e.getExt$Item("itemgroup")).distinct().collect(Collectors.toList())) {
                  List<Object> resultRowDataList = new ArrayList<>();
                  List<EmcReportResultBean> curResultBeanList = orderNoReportResultList.stream()
                     .filter(e -> StringUtils.equals(e.getExt$Item("itemGroup"), itemGroupName))
                     .collect(Collectors.toList());
                  EmcReportResultBean resultBean = curResultBeanList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(curResultBeanList.get(0));
                  resultRowDataList.add(itemGroupName);
                  resultRowDataList.add(resultBean.getUnitName());
                  if ("1".equals(showLimit)) {
                     resultRowDataList.add(resultBean.getIndexHighLimit());
                  }

                  resultRowDataList.add(resultBean.getLowLimit());

                  for (String orderNo : curOrderNoList) {
                     List<EmcReportResultBean> curOrderNoReportResultList = curResultBeanList.stream()
                        .filter(e -> ObjectUtils.equals(orderNo, e.getOrderNo()))
                        .collect(Collectors.toList());
                     if (curOrderNoReportResultList.isEmpty()) {
                        resultRowDataList.add("/");
                     } else {
                        EmcReportResultBean curOrderNoReportResult = curOrderNoReportResultList.stream()
                           .filter(e -> e.getLowLimit() != null)
                           .findFirst()
                           .orElse(curOrderNoReportResultList.get(0));
                        resultRowDataList.add(this.transScientificCounting(curOrderNoReportResult.getReportResult()));
                     }
                  }

                  tableDataList.add(resultRowDataList);
                  rowIndex.incrementAndGet();
               }

               this.listListNullToString(tableDataList, "/");
               WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
               wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
               wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
               List<Integer> columnWidthList = new ArrayList<>();
               columnWidthList.add(1400);
               columnWidthList.add(1000);
               if ("1".equals(showLimit)) {
                  columnWidthList.add(1000);
               }

               columnWidthList.add(1000);

               for (String orderNox : curOrderNoList) {
                  columnWidthList.add(0);
               }

               wordTemplateDynamicTableDTO.setColumnWidthList(columnWidthList);
               wordTemplateDynamicTableDTO.setCellHeight(400);
               wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + orderCategory + testName + "检测结果");
               wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
               XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
               tableIndex.incrementAndGet();
               table.getRow(0).setRepeatHeader(true);
               table.getRow(1).setRepeatHeader(true);
               table.getRow(2).setRepeatHeader(true);
               table.getRow(3).setRepeatHeader(true);
               table.getRow(4).setRepeatHeader(true);
            }
         }
      }

      return tableIndex.get();
   }

   private int hjkqSampleTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("样品编号");
      firstRowDataList.add("采样日期");
      firstRowDataList.add("采样时间");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("点位名称");
      firstRowDataList.add("检测结果");
      tableDataList.add(firstRowDataList);
      List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
      orderNoList.sort(Comparator.comparing(String::toString));

      for (String orderNo : orderNoList) {
         List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(orderNo);
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               EmcReportResultBean curReportResult = curReportResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
               if (curReportResult == null) {
                  curReportResult = curReportResultList.stream().filter(e -> e.getUnitName() != null).findFirst().orElse(curReportResultList.get(0));
               }

               rowDataList.add(DateTimeUtils.formatLocalDate(curReportResult.getSampleBeginDate(), "yyyy年MM月dd日"));
               EmcReportResultBean beginReportResultBean = curReportResultList.stream()
                  .filter(e -> ObjectUtils.equals("采样开始时间", e.getItemName()))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               EmcReportResultBean endReportResultBean = curReportResultList.stream()
                  .filter(e -> ObjectUtils.equals("采样结束时间", e.getItemName()))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               rowDataList.add(
                  (StringUtils.isBlank(beginReportResultBean.getReportResult()) ? "" : beginReportResultBean.getReportResult())
                     + (StringUtils.isBlank(endReportResultBean.getReportResult()) ? "" : "~" + endReportResultBean.getReportResult())
               );
               String resultTitle = StringUtils.isBlank(curReportResult.getUnitName()) ? itemgroup : itemgroup + "(" + curReportResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               rowDataList.add(curReportResult.getMonitorPointName());
               rowDataList.add(this.transScientificCounting(curReportResult.getReportResult()));
               tableDataList.add(rowDataList);
            }
         );
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
      return tabelCount + 1;
   }

   private void sampleCommonReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("样品编号");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("点位名称");
      firstRowDataList.add("检测结果");
      tableDataList.add(firstRowDataList);
      List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
      orderNoList.sort(Comparator.comparing(String::toString));

      for (String orderNo : orderNoList) {
         List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach((key, curReportResultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(orderNo);
            String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
            EmcReportResultBean curReportResult = curReportResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
            if (curReportResult == null) {
               curReportResult = curReportResultList.stream().filter(e -> e.getUnitName() != null).findFirst().orElse(curReportResultList.get(0));
            }

            String resultTitle = StringUtils.isBlank(curReportResult.getUnitName()) ? itemgroup : itemgroup + "(" + curReportResult.getUnitName() + ")";
            rowDataList.add(resultTitle);
            rowDataList.add(curReportResult.getMonitorPointName());
            rowDataList.add(this.transScientificCounting(curReportResult.getReportResult()));
            tableDataList.add(rowDataList);
         });
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
   }

   private void czwsclcwnSampleCommonTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("样品编号");
      firstRowDataList.add("点位名称");
      firstRowDataList.add("样品状态");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测结果");
      tableDataList.add(firstRowDataList);
      List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
      orderNoList.sort(Comparator.comparing(String::toString));

      for (String orderNo : orderNoList) {
         List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(orderNo);
               rowDataList.add(curReportResultList.get(0).getMonitorPointName());
               rowDataList.add(curReportResultList.get(0).getOrderDesc());
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                  .collect(Collectors.toList());
               EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
               String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                  .collect(Collectors.toList());
               EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
               String showResult = this.transScientificCounting(result.getReportResult());
               rowDataList.add(showResult);
               tableDataList.add(rowDataList);
            }
         );
      }

      List<EmcOrderTaskResultBean> qcTaskResultList = templateSourceDTO.getQcTaskResultList();
      List<EmcOrderTaskResultBean> orderCategoryQcTaskResultList = qcTaskResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getExt$Item("orderCategory"), orderCategory) && ObjectUtils.equals(e.getQcCategoryCode(), "P-DUP"))
         .collect(Collectors.toList());
      List<String> qcOrderNoList = orderCategoryQcTaskResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
      qcOrderNoList.sort(Comparator.comparing(String::toString));

      for (String qcOrderNo : qcOrderNoList) {
         List<EmcOrderTaskResultBean> qcOrderNoResultList = orderCategoryQcTaskResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getOrderNo(), qcOrderNo))
            .collect(Collectors.toList());
         Map<String, List<EmcOrderTaskResultBean>> itemGroupResultListMap = qcOrderNoResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupResultListMap.forEach(
            (key, curResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(qcOrderNo);
               rowDataList.add(curResultList.get(0).getExt$Item("monitorpointname"));
               rowDataList.add(curResultList.get(0).getExt$Item("orderdesc"));
               String itemgroup = curResultList.get(0).getExt$Item("reportTestName");
               List<EmcOrderTaskResultBean> unitNotEmptyList = curResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                  .collect(Collectors.toList());
               EmcOrderTaskResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcOrderTaskResultBean() : unitNotEmptyList.get(0);
               String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               List<EmcOrderTaskResultBean> resultNotEmptyList = curResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                  .collect(Collectors.toList());
               EmcOrderTaskResultBean result = resultNotEmptyList.isEmpty() ? new EmcOrderTaskResultBean() : resultNotEmptyList.get(0);
               String showResult = this.transScientificCounting(result.getReportResult());
               rowDataList.add(showResult);
               tableDataList.add(rowDataList);
            }
         );
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
   }

   private void zsCommonReportTableNew(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedColumn> mergedColumnList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测日期与时间");
      firstRowDataList.add("点位名称");
      firstRowDataList.add("检测结果");
      tableDataList.add(firstRowDataList);
      List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
      orderNoList.sort(Comparator.comparing(String::toString));

      for (String orderNo : orderNoList) {
         List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                  .collect(Collectors.toList());
               EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
               String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               rowDataList.add(this.getNoiseSampleDateTimeRange(curReportResultList.get(0)));
               rowDataList.add(curReportResultList.get(0).getMonitorPointName() + "▲" + curReportResultList.get(0).getMonitorPointNo());
               List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                  .collect(Collectors.toList());
               EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
               rowDataList.add(this.transScientificCounting(result.getReportResult()));
               tableDataList.add(rowDataList);
            }
         );
      }

      List<Object> remarkRowDataList = new ArrayList<>();
      remarkRowDataList.add("备注");
      StringBuffer remark = new StringBuffer();
      List<EmcPropertyDataBean> propertyDataList = templateSourceDTO.getPropertyDataList();
      List<EmcPropertyFieldBean> propertyFieldList = templateSourceDTO.getPropertyFieldList();
      List<Long> sampleIdList = orderCategoryReportResultList.stream()
         .filter(e -> e.getSampleId() != null)
         .map(e -> e.getSampleId())
         .distinct()
         .collect(Collectors.toList());
      List<EmcPropertyDataBean> samplePropertyDataList = propertyDataList.stream()
         .filter(e -> sampleIdList.contains(e.getBizId()))
         .collect(Collectors.toList());
      Long propertyTmplId = samplePropertyDataList.isEmpty() ? null : samplePropertyDataList.get(0).getTmplId();
      List<EmcPropertyFieldBean> curPropertyFieldList = propertyFieldList.stream()
         .filter(e -> ObjectUtils.equals(propertyTmplId, e.getTmplId()))
         .collect(Collectors.toList());
      remark.append("检测期间：");

      for (int i = 0; i < samplePropertyDataList.size(); i++) {
         EmcPropertyDataBean propertyData = samplePropertyDataList.get(i);
         Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
         String value1 = this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "昼间天气");
         if (!StringUtils.isBlank(value1) && !"/".equals(value1)) {
            remark.append("昼间天气").append(value1).append(",");
            break;
         }
      }

      for (int ix = 0; ix < samplePropertyDataList.size(); ix++) {
         EmcPropertyDataBean propertyData = samplePropertyDataList.get(ix);
         Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
         String value2 = this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "昼间风速(m/s)");
         if (!StringUtils.isBlank(value2) && !"/".equals(value2)) {
            remark.append("昼间风速").append(value2).append("m/s。");
         }
      }

      for (int ixx = 0; ixx < samplePropertyDataList.size(); ixx++) {
         EmcPropertyDataBean propertyData = samplePropertyDataList.get(ixx);
         Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
         String value3 = this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "夜间天气");
         if (!StringUtils.isBlank(value3) && !"/".equals(value3)) {
            remark.append("夜间天气").append(value3).append(",");
         }
      }

      for (int ixxx = 0; ixxx < samplePropertyDataList.size(); ixxx++) {
         EmcPropertyDataBean propertyData = samplePropertyDataList.get(ixxx);
         Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
         String value4 = this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "夜间风速(m/s)");
         if (!StringUtils.isBlank(value4) && !"/".equals(value4)) {
            remark.append("夜间风速").append(value4).append("m/s。");
         }
      }

      remarkRowDataList.add(remark);
      tableDataList.add(remarkRowDataList);
      mergedColumnList.add(new MergedColumn(tableDataList.size() - 1, 1, 3));
      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
      wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
      List<XWPFTableRow> rows = table.getRows();
      XWPFTableRow row = rows.get(rows.size() - 1);
      XWPFTableCell cell = row.getCell(1);
      ((CTP)cell.getCTTc().getPList().get(0)).addNewPPr().addNewJc().setVal(STJc.LEFT);
   }

   private void wzzfqSampleCommonTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<LocalDate> sampleBeginDateList = orderCategoryReportResultList.stream().map(e -> e.getSampleBeginDate()).distinct().collect(Collectors.toList());

      for (int j = 1; j <= sampleBeginDateList.size(); j++) {
         LocalDate sampleBeginDate = sampleBeginDateList.get(j - 1);
         List<EmcReportResultBean> sampleBeginDateReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getSampleBeginDate(), sampleBeginDate))
            .collect(Collectors.toList());
         int frequency = sampleBeginDateReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getFrequency(), 1)).max().getAsInt();
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("采样日期");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("检测点位");

         for (int i = 1; i <= frequency; i++) {
            firstRowDataList.add("检测结果");
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(0, 3, 3 + frequency - 1));
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 3; i++) {
            secondRowDataList.add("");
         }

         for (int i = 1; i <= frequency; i++) {
            int finalFrequency = i;
            List<EmcReportResultBean> frequencyReportResultList = sampleBeginDateReportResultList.stream()
               .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getFrequency()), finalFrequency))
               .collect(Collectors.toList());
            EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
               .filter(e -> ObjectUtils.equals("采样开始时间", e.getItemName()))
               .findFirst()
               .orElse(new EmcReportResultBean());
            secondRowDataList.add(this.numberToChinese(finalFrequency) + "时段（" + emcReportResultBean.getReportResult() + "）");
         }

         tableDataList.add(secondRowDataList);
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         mergedRowList.add(new MergedRow(2, 0, 1));
         List<String> monitorPointNameList = sampleBeginDateReportResultList.stream().map(e -> e.getMonitorPointName()).distinct().collect(Collectors.toList());
         monitorPointNameList.sort(Comparator.comparing(String::toString));
         Map<Integer, String> maxResultMap = new HashMap<>();

         for (String monitorPointName : monitorPointNameList) {
            List<EmcReportResultBean> folderNoReportResultList = sampleBeginDateReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getMonitorPointName(), monitorPointName))
               .collect(Collectors.toList());
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(DateTimeUtils.formatLocalDate(folderNoReportResultList.get(0).getSampleBeginDate(), "yyyy年MM月dd日"));
            String itemgroup = folderNoReportResultList.get(0).getExt$Item("reportTestName");
            List<EmcReportResultBean> unitNotEmptyList = folderNoReportResultList.stream()
               .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
               .collect(Collectors.toList());
            EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
            String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
            rowDataList.add(resultTitle);
            rowDataList.add(folderNoReportResultList.get(0).getMonitorPointName() + "▲" + folderNoReportResultList.get(0).getMonitorPointNo());

            for (int i = 1; i <= frequency; i++) {
               int finalFrequency = i;
               List<EmcReportResultBean> frequencyReportResultList = folderNoReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(NumberUtils.parseInt(e.getFrequency()), finalFrequency))
                  .collect(Collectors.toList());
               EmcReportResultBean emcReportResultBean = frequencyReportResultList.stream()
                  .filter(e -> ObjectUtils.equals("样品浓度", e.getItemName()))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               String reportResult = emcReportResultBean.getReportResult();
               rowDataList.add(this.transScientificCounting(reportResult));
               String maxResult = maxResultMap.get(i);
               if (NumberUtils.isNumber(reportResult) && (maxResult == null || NumberUtils.parseDouble(reportResult) > NumberUtils.parseDouble(maxResult))) {
                  maxResultMap.put(i, reportResult);
               }
            }

            tableDataList.add(rowDataList);
         }

         List<Object> maxRowDataList = new ArrayList<>();

         for (int ix = 0; ix < 2; ix++) {
            maxRowDataList.add("");
         }

         maxRowDataList.add("最大值");

         for (int ix = 1; ix <= frequency; ix++) {
            String maxResult = maxResultMap.get(ix);
            maxRowDataList.add(maxResult);
         }

         tableDataList.add(maxRowDataList);
         mergedRowList.add(new MergedRow(0, 2, 2 + monitorPointNameList.size()));
         mergedRowList.add(new MergedRow(1, 2, 2 + monitorPointNameList.size()));
         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + "-" + j + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
      }
   }

   private String getPropertyDataByFieldName(List<EmcPropertyFieldBean> propertyFieldList, Map<String, Object> propertyDataMap, String fieldName) {
      EmcPropertyFieldBean propertyField = propertyFieldList.stream().filter(e -> ObjectUtils.equals(fieldName, e.getFieldName())).findFirst().orElse(null);
      if (propertyField == null) {
         return "";
      } else {
         String internalField = propertyField.getInternalField();
         Object value = propertyDataMap.get(internalField);
         return value == null ? "" : (String)value;
      }
   }

   private String getReportResultByItemName(List<EmcReportResultBean> curReportResultList, String itemName) {
      EmcReportResultBean emcReportResultBean = curReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getItemName(), itemName))
         .findFirst()
         .orElse(new EmcReportResultBean());
      String reportResult = emcReportResultBean.getReportResult();
      return StringUtils.isBlank(reportResult) ? "" : this.transScientificCounting(reportResult);
   }

   private EmcReportResultBean getReportResultBeanByItemName(List<EmcReportResultBean> curReportResultList, String itemName) {
      return curReportResultList.stream().filter(e -> ObjectUtils.equals(e.getItemName(), itemName)).findFirst().orElse(new EmcReportResultBean());
   }

   private int youzuzhiSendTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<EmcReportResultBean> multleReportResultList = orderCategoryReportResultList.stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && "1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      List<EmcReportResultBean> singleReportResultList = orderCategoryReportResultList.stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && !"1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(multleReportResultList)) {
         Map<String, List<EmcReportResultBean>> testReportResultListMap = multleReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : testReportResultListMap.keySet()) {
            List<EmcReportResultBean> testReportResultList = testReportResultListMap.get(testName);
            List<List<Object>> tableDataList = new ArrayList<>();
            List<MergedRow> mergedRowList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();
            firstRowDataList.add("样品名称");
            firstRowDataList.add("检测项目");
            firstRowDataList.add("容器");
            firstRowDataList.add("检出限");
            firstRowDataList.add("检测结果");
            tableDataList.add(firstRowDataList);
            List<EmcReportResultBean> allMonitorPointNameReportResultList = testReportResultList.stream()
               .filter(e -> e.getClientOrderName() != null)
               .collect(Collectors.toList());
            List<String> monitorPointNameList = allMonitorPointNameReportResultList.stream()
               .map(e -> e.getClientOrderName())
               .distinct()
               .collect(Collectors.toList());
            monitorPointNameList.sort(Comparator.comparing(String::toString));
            AtomicInteger rowIndex = new AtomicInteger(1);

            for (String monitorPointName : monitorPointNameList) {
               int rowStartIndex = rowIndex.get();
               List<EmcReportResultBean> monitorPointNameReportResultList = testReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
                  .collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
               List<Entry<String, List<EmcReportResultBean>>> sortedEntries = new ArrayList<>(itemGroupReportResultListMap.entrySet());
               sortedEntries.sort(Comparator.comparing(e -> e.getValue().get(0).getExt$Item("sortno"), Comparator.nullsLast(Comparator.naturalOrder())));
               Map<String, List<EmcReportResultBean>> sortedMap = new LinkedHashMap<>();
               sortedEntries.forEach(entry -> {
                  List var10000 = sortedMap.put(entry.getKey(), entry.getValue());
               });
               sortedMap.forEach(
                  (key, curReportResultList) -> {
                     List<Object> rowDataList = new ArrayList<>();
                     rowDataList.add(monitorPointName);
                     String itemgroup = curReportResultList.get(0).getExt$Item("itemGroup");
                     List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                        .collect(Collectors.toList());
                     EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                     String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                     rowDataList.add(resultTitle);
                     rowDataList.add(curReportResultList.get(0).getExt$Item("containercategoryname"));
                     List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                        .collect(Collectors.toList());
                     EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
                     EmcReportResultBean indexResult = curReportResultList.stream()
                        .filter(e -> e.getIndexLowLimit() != null)
                        .findFirst()
                        .orElse(new EmcReportResultBean());
                     rowDataList.add(indexResult != null ? indexResult.getIndexLowLimit() : "/");
                     rowDataList.add(this.transScientificCounting(result.getReportResult()));
                     tableDataList.add(rowDataList);
                     rowIndex.incrementAndGet();
                  }
               );
               mergedRowList.add(new MergedRow(0, rowStartIndex, rowIndex.get() - 1));
            }

            this.listListNullToString(tableDataList, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + testName + "  " + orderCategory + "检测结果");
            wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
            XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            table.getRow(0).setRepeatHeader(true);
            tabelCount++;
         }
      }

      if (!CollectionUtils.isEmpty(singleReportResultList)) {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("样品名称");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("容器");
         firstRowDataList.add("检出限");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);
         List<EmcReportResultBean> allMonitorPointNameReportResultList = singleReportResultList.stream()
            .filter(e -> e.getClientOrderName() != null)
            .collect(Collectors.toList());
         List<String> monitorPointNameList = allMonitorPointNameReportResultList.stream()
            .map(e -> e.getClientOrderName())
            .distinct()
            .collect(Collectors.toList());
         monitorPointNameList.sort(Comparator.comparing(String::toString));
         AtomicInteger rowIndex = new AtomicInteger(1);

         for (String monitorPointName : monitorPointNameList) {
            int rowStartIndex = rowIndex.get();
            List<EmcReportResultBean> monitorPointNameReportResultList = singleReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
            itemGroupReportResultListMap.forEach(
               (key, curReportResultList) -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(monitorPointName);
                  String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
                  List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                     .collect(Collectors.toList());
                  EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                  String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                  rowDataList.add(resultTitle);
                  rowDataList.add(curReportResultList.get(0).getExt$Item("containercategoryname"));
                  List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                     .collect(Collectors.toList());
                  EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
                  EmcReportResultBean indexResult = curReportResultList.stream()
                     .filter(e -> e.getIndexLowLimit() != null)
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  rowDataList.add(indexResult != null ? indexResult.getIndexLowLimit() : "/");
                  rowDataList.add(this.transScientificCounting(result.getReportResult()));
                  tableDataList.add(rowDataList);
                  rowIndex.incrementAndGet();
               }
            );
            mergedRowList.add(new MergedRow(0, rowStartIndex, rowIndex.get() - 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         tabelCount++;
      }

      return tabelCount;
   }

   private int sendCommonReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<EmcReportResultBean> allMonitorPointNameReportResultList = orderCategoryReportResultList.stream()
         .filter(e -> e.getClientOrderName() != null)
         .collect(Collectors.toList());
      List<EmcReportResultBean> allSendDateReportResultList = allMonitorPointNameReportResultList.stream()
         .filter(e -> e.getExt$Item("senddate") != null)
         .collect(Collectors.toList());
      if (allSendDateReportResultList.isEmpty()) {
         return tabelCount;
      } else {
         long sendDateCount = allSendDateReportResultList.stream().map(e -> e.getExt$Item("senddate")).distinct().count();
         if (sendDateCount == 1L) {
            this.sendCommonNoDateTable(document, tabelCount, orderCategory, allSendDateReportResultList);
         } else {
            this.sendCommonWithDateTable(document, tabelCount, orderCategory, allSendDateReportResultList);
         }

         return tabelCount + 1;
      }
   }

   private void sendCommonWithDateTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> allSendDateReportResultList) {
      List<EmcReportResultBean> specReportResultList = allSendDateReportResultList.stream()
         .filter(s -> "总α放射性".equals(s.getTestName()) || "总β放射性".equals(s.getTestName()))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(specReportResultList)) {
         this.specSendCommonWithDateTable(document, tabelCount, orderCategory, specReportResultList);
      }

      List<EmcReportResultBean> multleReportResultList = allSendDateReportResultList.stream()
         .filter(r -> r.getTestName() != null && "1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(multleReportResultList)) {
         this.multleSendCommonWithDateTable(document, tabelCount, orderCategory, multleReportResultList);
      }

      List<EmcReportResultBean> reportResultList = allSendDateReportResultList.stream()
         .filter(s -> !"总α放射性".equals(s.getTestName()) && !"总β放射性".equals(s.getTestName()) && !"1".equals(s.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!reportResultList.isEmpty()) {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("送样日期");
         firstRowDataList.add("样品名称");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("样品状态");
         firstRowDataList.add("检出限");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);
         List<String> sendDateStrList = reportResultList.stream().map(e -> e.getExt$Item("senddate")).distinct().collect(Collectors.toList());
         sendDateStrList.sort(Comparator.comparing(String::toString));
         AtomicInteger sendDateRowIndex = new AtomicInteger(1);
         AtomicInteger monitorPointNameRowIndex = new AtomicInteger(1);

         for (String sendDateStr : sendDateStrList) {
            int sendDateRowStartIndex = sendDateRowIndex.get();
            List<EmcReportResultBean> sendDateReportResultList = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getExt$Item("senddate"), sendDateStr))
               .collect(Collectors.toList());
            List<String> monitorPointNameList = sendDateReportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());
            monitorPointNameList.sort(Comparator.comparing(String::toString));

            for (String monitorPointName : monitorPointNameList) {
               int monitorPointNameRowStartIndex = monitorPointNameRowIndex.get();
               List<EmcReportResultBean> monitorPointNameReportResultList = sendDateReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
                  .collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
               itemGroupReportResultListMap.forEach(
                  (key, curReportResultList) -> {
                     List<Object> rowDataList = new ArrayList<>();
                     rowDataList.add(DateTimeUtils.formatLocalDate(DateTimeUtils.parseLocalDate(sendDateStr), "yyyy年MM月dd日"));
                     rowDataList.add(monitorPointName);
                     String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
                     List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                        .collect(Collectors.toList());
                     EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                     String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                     rowDataList.add(resultTitle);
                     rowDataList.add(curReportResultList.get(0).getOrderDesc());
                     rowDataList.add(curReportResultList.get(0).getLowLimit());
                     List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                        .collect(Collectors.toList());
                     EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
                     String reportResult = this.getNdValue2(result);
                     rowDataList.add(this.transScientificCounting(reportResult));
                     tableDataList.add(rowDataList);
                     sendDateRowIndex.incrementAndGet();
                     monitorPointNameRowIndex.incrementAndGet();
                  }
               );
               mergedRowList.add(new MergedRow(1, monitorPointNameRowStartIndex, monitorPointNameRowIndex.get() - 1));
            }

            mergedRowList.add(new MergedRow(0, sendDateRowStartIndex, sendDateRowIndex.get() - 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
      }
   }

   private void specSendCommonWithDateTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> reportResultList) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedRow> mergedRowList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("送样日期");
      firstRowDataList.add("样品名称");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("样品状态");
      firstRowDataList.add("检测结果");
      firstRowDataList.add("探测下限");
      tableDataList.add(firstRowDataList);
      List<String> sendDateStrList = reportResultList.stream().map(e -> e.getExt$Item("senddate")).distinct().collect(Collectors.toList());
      sendDateStrList.sort(Comparator.comparing(String::toString));
      AtomicInteger sendDateRowIndex = new AtomicInteger(1);
      AtomicInteger monitorPointNameRowIndex = new AtomicInteger(1);

      for (String sendDateStr : sendDateStrList) {
         int sendDateRowStartIndex = sendDateRowIndex.get();
         List<EmcReportResultBean> sendDateReportResultList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getExt$Item("senddate"), sendDateStr))
            .collect(Collectors.toList());
         List<String> monitorPointNameList = sendDateReportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());
         monitorPointNameList.sort(Comparator.comparing(String::toString));

         for (String monitorPointName : monitorPointNameList) {
            int monitorPointNameRowStartIndex = monitorPointNameRowIndex.get();
            List<EmcReportResultBean> monitorPointNameReportResultList = sendDateReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
            itemGroupReportResultListMap.forEach(
               (key, curReportResultList) -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(DateTimeUtils.formatLocalDate(DateTimeUtils.parseLocalDate(sendDateStr), "yyyy年MM月dd日"));
                  rowDataList.add(monitorPointName);
                  String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
                  List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                     .collect(Collectors.toList());
                  EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                  String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                  rowDataList.add(resultTitle);
                  rowDataList.add(curReportResultList.get(0).getOrderDesc());
                  rowDataList.add(
                     this.transScientificCounting(
                        curReportResultList.stream().filter(res -> "样品浓度".equals(res.getItemName())).collect(Collectors.toList()).get(0).getReportResult()
                     )
                  );
                  rowDataList.add(
                     this.transScientificCounting(
                        curReportResultList.stream().filter(res -> "探测限".equals(res.getItemName())).collect(Collectors.toList()).get(0).getReportResult()
                     )
                  );
                  tableDataList.add(rowDataList);
                  sendDateRowIndex.incrementAndGet();
                  monitorPointNameRowIndex.incrementAndGet();
               }
            );
            mergedRowList.add(new MergedRow(1, monitorPointNameRowStartIndex, monitorPointNameRowIndex.get() - 1));
         }

         mergedRowList.add(new MergedRow(0, sendDateRowStartIndex, sendDateRowIndex.get() - 1));
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
   }

   private void sendCommonNoDateTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> allSendDateReportResultList) {
      List<EmcReportResultBean> specReportResultList = allSendDateReportResultList.stream()
         .filter(s -> "总α放射性".equals(s.getTestName()) || "总β放射性".equals(s.getTestName()))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(specReportResultList)) {
         this.specSendCommonNoDateTable(document, tabelCount, orderCategory, specReportResultList);
      }

      List<EmcReportResultBean> multleReportResultList = allSendDateReportResultList.stream()
         .filter(r -> r.getTestName() != null && "1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(multleReportResultList)) {
         tabelCount = this.multleSendCommonNoDateTable(document, tabelCount, orderCategory, multleReportResultList);
      }

      List<EmcReportResultBean> reportResultList = allSendDateReportResultList.stream()
         .filter(s -> !"总α放射性".equals(s.getTestName()) && !"总β放射性".equals(s.getTestName()) && !"1".equals(s.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(reportResultList)) {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("样品名称");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("样品状态");
         firstRowDataList.add("检出限");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);
         List<String> monitorPointNameList = reportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());
         monitorPointNameList.sort(Comparator.comparing(String::toString));
         AtomicInteger rowIndex = new AtomicInteger(1);

         for (String monitorPointName : monitorPointNameList) {
            int rowStartIndex = rowIndex.get();
            List<EmcReportResultBean> monitorPointNameReportResultList = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
            itemGroupReportResultListMap.forEach((key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(monitorPointName);
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               EmcReportResultBean curReportResult = curReportResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(null);
               if (curReportResult == null) {
                  curReportResult = curReportResultList.stream().filter(e -> e.getUnitName() != null).findFirst().orElse(curReportResultList.get(0));
               }

               String resultTitle = StringUtils.isBlank(curReportResult.getUnitName()) ? itemgroup : itemgroup + "(" + curReportResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               rowDataList.add(curReportResult.getOrderDesc());
               rowDataList.add(curReportResult.getLowLimit());
               String reportResult = this.getNdValue2(curReportResult);
               rowDataList.add(this.transScientificCounting(reportResult));
               tableDataList.add(rowDataList);
               rowIndex.incrementAndGet();
            });
            mergedRowList.add(new MergedRow(0, rowStartIndex, rowIndex.get() - 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
      }
   }

   private void multleSendCommonWithDateTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> multleReportResultList) {
      Map<String, List<EmcReportResultBean>> testReportResultListMap = multleReportResultList.stream()
         .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

      for (String testName : testReportResultListMap.keySet()) {
         List<EmcReportResultBean> testReportResultList = testReportResultListMap.get(testName);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("送样日期");
         firstRowDataList.add("样品名称");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("样品状态");
         firstRowDataList.add("检出限");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);
         List<String> sendDateStrList = testReportResultList.stream().map(e -> e.getExt$Item("senddate")).distinct().collect(Collectors.toList());
         sendDateStrList.sort(Comparator.comparing(String::toString));
         AtomicInteger sendDateRowIndex = new AtomicInteger(1);
         AtomicInteger monitorPointNameRowIndex = new AtomicInteger(1);

         for (String sendDateStr : sendDateStrList) {
            int sendDateRowStartIndex = sendDateRowIndex.get();
            List<EmcReportResultBean> sendDateReportResultList = testReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getExt$Item("senddate"), sendDateStr))
               .collect(Collectors.toList());
            List<String> monitorPointNameList = sendDateReportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());
            monitorPointNameList.sort(Comparator.comparing(String::toString));

            for (String monitorPointName : monitorPointNameList) {
               int monitorPointNameRowStartIndex = monitorPointNameRowIndex.get();
               List<EmcReportResultBean> monitorPointNameReportResultList = sendDateReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
                  .collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
               itemGroupReportResultListMap.forEach(
                  (key, curReportResultList) -> {
                     List<Object> rowDataList = new ArrayList<>();
                     rowDataList.add(DateTimeUtils.formatLocalDate(DateTimeUtils.parseLocalDate(sendDateStr), "yyyy年MM月dd日"));
                     rowDataList.add(monitorPointName);
                     String itemgroup = curReportResultList.get(0).getExt$Item("itemgroup");
                     List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                        .collect(Collectors.toList());
                     EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                     String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                     rowDataList.add(resultTitle);
                     rowDataList.add(curReportResultList.get(0).getOrderDesc());
                     rowDataList.add(curReportResultList.get(0).getLowLimit());
                     List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                        .collect(Collectors.toList());
                     EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
                     String reportResult = this.getNdValue2(result);
                     rowDataList.add(this.transScientificCounting(reportResult));
                     tableDataList.add(rowDataList);
                     sendDateRowIndex.incrementAndGet();
                     monitorPointNameRowIndex.incrementAndGet();
                  }
               );
               mergedRowList.add(new MergedRow(1, monitorPointNameRowStartIndex, monitorPointNameRowIndex.get() - 1));
            }

            mergedRowList.add(new MergedRow(0, sendDateRowStartIndex, sendDateRowIndex.get() - 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
      }
   }

   private int multleSendCommonNoDateTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> multleReportResultList) {
      Map<String, List<EmcReportResultBean>> testReportResultListMap = multleReportResultList.stream()
         .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

      for (String testName : testReportResultListMap.keySet()) {
         List<EmcReportResultBean> testReportResultList = testReportResultListMap.get(testName);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("样品名称");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("样品状态");
         firstRowDataList.add("检出限");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);
         List<String> monitorPointNameList = testReportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());
         monitorPointNameList.sort(Comparator.comparing(String::toString));
         AtomicInteger rowIndex = new AtomicInteger(1);

         for (String monitorPointName : monitorPointNameList) {
            int rowStartIndex = rowIndex.get();
            List<EmcReportResultBean> monitorPointNameReportResultList = testReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
            itemGroupReportResultListMap.forEach(
               (key, curReportResultList) -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(monitorPointName);
                  String itemgroup = curReportResultList.get(0).getExt$Item("itemgroup");
                  List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                     .collect(Collectors.toList());
                  EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                  String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                  rowDataList.add(resultTitle);
                  rowDataList.add(curReportResultList.get(0).getOrderDesc());
                  rowDataList.add(curReportResultList.get(0).getLowLimit());
                  List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                     .collect(Collectors.toList());
                  EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
                  String reportResult = this.getNdValue2(result);
                  rowDataList.add(this.transScientificCounting(reportResult));
                  tableDataList.add(rowDataList);
                  rowIndex.incrementAndGet();
               }
            );
            mergedRowList.add(new MergedRow(0, rowStartIndex, rowIndex.get() - 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + testName + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         tabelCount++;
      }

      return tabelCount;
   }

   private void specSendCommonNoDateTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> allSendDateReportResultList) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedRow> mergedRowList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("样品名称");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("样品状态");
      firstRowDataList.add("检测结果");
      firstRowDataList.add("探测下限");
      tableDataList.add(firstRowDataList);
      List<String> monitorPointNameList = allSendDateReportResultList.stream().map(e -> e.getClientOrderName()).distinct().collect(Collectors.toList());
      monitorPointNameList.sort(Comparator.comparing(String::toString));
      AtomicInteger rowIndex = new AtomicInteger(1);

      for (String monitorPointName : monitorPointNameList) {
         int rowStartIndex = rowIndex.get();
         List<EmcReportResultBean> monitorPointNameReportResultList = allSendDateReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getClientOrderName(), monitorPointName))
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = monitorPointNameReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(monitorPointName);
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                  .collect(Collectors.toList());
               EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
               String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               rowDataList.add(curReportResultList.get(0).getOrderDesc());
               rowDataList.add(
                  this.transScientificCounting(
                     curReportResultList.stream().filter(res -> "样品浓度".equals(res.getItemName())).collect(Collectors.toList()).get(0).getReportResult()
                  )
               );
               rowDataList.add(
                  this.transScientificCounting(
                     curReportResultList.stream().filter(res -> "探测限".equals(res.getItemName())).collect(Collectors.toList()).get(0).getReportResult()
                  )
               );
               tableDataList.add(rowDataList);
               rowIndex.incrementAndGet();
            }
         );
         mergedRowList.add(new MergedRow(0, rowStartIndex, rowIndex.get() - 1));
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
   }

   private void feishuiSampleReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      String showLimit = templateSourceDTO.getReport().getExt$Item("showlimit");
      List<String> bzyCodeList = Arrays.asList("Q-DUP");
      List<EmcOrderTaskResultBean> qcResultLists = templateSourceDTO.getQcTaskResultList()
         .stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && bzyCodeList.contains(r.getQcCategoryCode()))
         .collect(Collectors.toList());
      List<EmcReportResultBean> qcReportResultList = this.changResultToReport(qcResultLists);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      reportResultList.addAll(qcReportResultList);
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("样品编号");
      firstRowDataList.add("点位名称");
      firstRowDataList.add("采样日期及时间");
      firstRowDataList.add("样品状态");
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测结果");
      if ("1".equals(showLimit)) {
         firstRowDataList.add("标准限值");
      }

      List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
      orderNoList.sort(Comparator.comparing(String::toString));

      for (String orderNo : orderNoList) {
         List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
            .collect(Collectors.toList());
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               rowDataList.add(orderNo);
               rowDataList.add(curReportResultList.get(0).getMonitorPointName() + "★" + curReportResultList.get(0).getMonitorPointNo());
               String sampleBeginTime = curReportResultList.get(0).getSampleBeginTime();
               if (!StringUtils.isBlank(sampleBeginTime) && sampleBeginTime.length() > 4) {
                  sampleBeginTime = sampleBeginTime.substring(0, 5);
               }

               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
               if (curReportResultList.get(0).getSampleBeginDate() == null) {
                  rowDataList.add(" ");
               } else {
                  String formattedDate = curReportResultList.get(0).getSampleBeginDate().format(formatter);
                  rowDataList.add(formattedDate + "\n" + sampleBeginTime);
               }

               rowDataList.add(curReportResultList.get(0).getOrderDesc());
               String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
               List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                  .collect(Collectors.toList());
               EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
               String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
               rowDataList.add(resultTitle);
               List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                  .collect(Collectors.toList());
               EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
               rowDataList.add(this.transScientificCounting(result.getReportResult()));
               if ("1".equals(showLimit)) {
                  rowDataList.add(result.getIndexHighLimit());
               }

               tableDataList.add(rowDataList);
            }
         );
      }

      List<MergedRow> mergedRowList = new ArrayList<>();
      tableDataList.sort(Comparator.comparing(l -> (String)l.get(0), String::compareTo));
      tableDataList.add(0, firstRowDataList);

      for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
         int biginIndex = 1;

         for (int index = 2; index <= tableDataList.size(); index++) {
            if (index == tableDataList.size() || !ObjectUtils.equals(tableDataList.get(index).get(columnIndex), tableDataList.get(index - 1).get(columnIndex))) {
               mergedRowList.add(new MergedRow(columnIndex, biginIndex, index - 1));
               biginIndex = index;
            }
         }
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      if ("1".equals(showLimit)) {
         wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(2000, 2000, 1800, 0, 0, 0, 0, 0));
      } else {
         wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(2000, 2000, 1800, 0, 0, 0, 0));
      }

      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
   }

   private int shyysSampleReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableCount, String orderCategory) {
      String showLimit = templateSourceDTO.getReport().getExt$Item("showlimit");
      List<String> bzyCodeList = Arrays.asList("Q-DUP");
      List<EmcOrderTaskResultBean> qcResultLists = templateSourceDTO.getQcTaskResultList()
         .stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && bzyCodeList.contains(r.getQcCategoryCode()))
         .collect(Collectors.toList());
      List<EmcReportResultBean> qcReportResultList = this.changResultToReport(qcResultLists);
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      reportResultList.addAll(qcReportResultList);
      List<EmcReportResultBean> fsxReportResultList = reportResultList.stream()
         .filter(s -> ("总α放射性".equals(s.getTestName()) || "总β放射性".equals(s.getTestName())) && ObjectUtils.equals(s.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      if (!fsxReportResultList.isEmpty()) {
         tableCount = this.fsxSampleReportTable(document, templateSourceDTO, tableCount, fsxReportResultList, orderCategory);
      }

      AtomicInteger tableIndex = new AtomicInteger(tableCount);
      List<EmcReportResultBean> orderCategoryReportResultList = reportResultList.stream()
         .filter(
            result -> StringUtils.equals(result.getDataType(), "sample")
                  && ObjectUtils.equals(result.getOrderCategory(), orderCategory)
                  && !"总α放射性".equals(result.getTestName())
                  && !"总β放射性".equals(result.getTestName())
                  && !"1".equals(result.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(orderCategoryReportResultList)) {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("样品编号");
         firstRowDataList.add("点位名称");
         firstRowDataList.add("采样日期");
         firstRowDataList.add("样品状态");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("检测结果");
         firstRowDataList.add("检出限");
         if ("1".equals(showLimit)) {
            firstRowDataList.add("标准限值");
         }

         List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
         orderNoList.sort(Comparator.comparing(String::toString));

         for (String orderNo : orderNoList) {
            List<EmcReportResultBean> orderNoReportResultList = orderCategoryReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
            itemGroupReportResultListMap.forEach(
               (key, curReportResultList) -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(orderNo);
                  rowDataList.add(curReportResultList.get(0).getMonitorPointName() + curReportResultList.get(0).getMonitorPointNo());
                  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
                  if (curReportResultList.get(0).getSampleBeginDate() == null) {
                     rowDataList.add("");
                  } else {
                     String formattedDate = curReportResultList.get(0).getSampleBeginDate().format(formatter);
                     rowDataList.add(formattedDate);
                  }

                  rowDataList.add(curReportResultList.get(0).getOrderDesc());
                  String itemgroup = curReportResultList.get(0).getExt$Item("reportTestName");
                  List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                     .collect(Collectors.toList());
                  EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                  String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                  rowDataList.add(resultTitle);
                  List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                     .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                     .collect(Collectors.toList());
                  EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
                  String reportResult = this.getNdValue2(result);
                  rowDataList.add(this.transScientificCounting(reportResult));
                  rowDataList.add(result.getLowLimit());
                  if ("1".equals(showLimit)) {
                     rowDataList.add(result.getIndexHighLimit());
                  }

                  tableDataList.add(rowDataList);
               }
            );
         }

         List<MergedRow> mergedRowList = new ArrayList<>();
         tableDataList.sort(Comparator.comparing(l -> (String)l.get(0), String::compareTo));
         tableDataList.add(0, firstRowDataList);

         for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
            int biginIndex = 1;

            for (int index = 2; index <= tableDataList.size(); index++) {
               if (index == tableDataList.size()
                  || !ObjectUtils.equals(tableDataList.get(index).get(columnIndex), tableDataList.get(index - 1).get(columnIndex))) {
                  mergedRowList.add(new MergedRow(columnIndex, biginIndex, index - 1));
                  biginIndex = index;
               }
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         if ("1".equals(showLimit)) {
            wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(2000, 2000, 1800, 0, 0, 0, 0, 0));
         } else {
            wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(2000, 2000, 1800, 0, 0, 0, 0));
         }

         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + orderCategory + "检测结果");
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         tableIndex.incrementAndGet();
         table.getRow(0).setRepeatHeader(true);
      }

      List<EmcReportResultBean> mulitResultList = reportResultList.stream()
         .filter(
            result -> StringUtils.equals(result.getDataType(), "sample")
                  && ObjectUtils.equals(result.getOrderCategory(), orderCategory)
                  && !"总α放射性".equals(result.getTestName())
                  && !"总β放射性".equals(result.getTestName())
                  && "1".equals(result.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(mulitResultList)) {
         Map<String, List<EmcReportResultBean>> testNameReportResultListMap = mulitResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : mulitResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            List<EmcReportResultBean> reportResultBeans = testNameReportResultListMap.get(testName);
            List<List<Object>> tableDataListx = new ArrayList<>();
            List<Object> firstRowDataListx = new ArrayList<>();
            firstRowDataListx.add("样品编号");
            firstRowDataListx.add("点位名称");
            firstRowDataListx.add("采样日期");
            firstRowDataListx.add("样品状态");
            firstRowDataListx.add("检测项目");
            firstRowDataListx.add("检测结果");
            firstRowDataListx.add("检出限");
            if ("1".equals(showLimit)) {
               firstRowDataListx.add("标准限值");
            }

            List<String> orderNoList = reportResultBeans.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
            orderNoList.sort(Comparator.comparing(String::toString));

            for (String orderNo : orderNoList) {
               List<EmcReportResultBean> orderNoReportResultList = reportResultBeans.stream()
                  .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
                  .collect(Collectors.toList());
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderNoReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
               itemGroupReportResultListMap.forEach(
                  (key, curReportResultList) -> {
                     List<Object> rowDataList = new ArrayList<>();
                     rowDataList.add(orderNo);
                     rowDataList.add(curReportResultList.get(0).getMonitorPointName() + curReportResultList.get(0).getMonitorPointNo());
                     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
                     if (curReportResultList.get(0).getSampleBeginDate() == null) {
                        rowDataList.add("");
                     } else {
                        String formattedDate = curReportResultList.get(0).getSampleBeginDate().format(formatter);
                        rowDataList.add(formattedDate);
                     }

                     rowDataList.add(curReportResultList.get(0).getOrderDesc());
                     String itemgroup = curReportResultList.get(0).getExt$Item("itemGroup");
                     List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                        .collect(Collectors.toList());
                     EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
                     String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? itemgroup : itemgroup + "(" + unitResult.getUnitName() + ")";
                     rowDataList.add(resultTitle);
                     List<EmcReportResultBean> resultNotEmptyList = curReportResultList.stream()
                        .filter(e -> !StringUtils.isEmpty(e.getReportResult()))
                        .collect(Collectors.toList());
                     EmcReportResultBean result = resultNotEmptyList.isEmpty() ? new EmcReportResultBean() : resultNotEmptyList.get(0);
                     String reportResult = this.getNdValue2(result);
                     rowDataList.add(this.transScientificCounting(reportResult));
                     rowDataList.add(result.getLowLimit());
                     if ("1".equals(showLimit)) {
                        rowDataList.add(result.getIndexHighLimit());
                     }

                     tableDataListx.add(rowDataList);
                  }
               );
            }

            List<MergedRow> mergedRowList = new ArrayList<>();
            tableDataListx.sort(Comparator.comparing(l -> (String)l.get(0), String::compareTo));
            tableDataListx.add(0, firstRowDataListx);

            for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
               int biginIndex = 1;

               for (int indexx = 2; indexx <= tableDataListx.size(); indexx++) {
                  if (indexx == tableDataListx.size()
                     || !ObjectUtils.equals(tableDataListx.get(indexx).get(columnIndex), tableDataListx.get(indexx - 1).get(columnIndex))) {
                     mergedRowList.add(new MergedRow(columnIndex, biginIndex, indexx - 1));
                     biginIndex = indexx;
                  }
               }
            }

            this.listListNullToString(tableDataListx, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataListx);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            if ("1".equals(showLimit)) {
               wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(2000, 2000, 1800, 0, 0, 0, 0, 0));
            } else {
               wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(2000, 2000, 1800, 0, 0, 0, 0));
            }

            wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
            wordTemplateDynamicTableDTO.setTableTitle("表 " + (tableIndex.get() + 1) + " " + orderCategory + testName + "检测结果");
            XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            tableIndex.incrementAndGet();
            table.getRow(0).setRepeatHeader(true);
         }
      }

      return tableIndex.get();
   }

   private void feishuiSendReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      sampleReportResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("itemGroup"))).forEach(r -> r.setExt$Item("itemGroup", r.getTestName()));
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedRow> mergedRowList = new ArrayList<>();
      List<MergedColumn> mergedColumnList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("采样日期");
      firstRowDataList.add("检测地点");
      firstRowDataList.add("样品状态");
      firstRowDataList.add("检测项目");
      List<EmcReportResultBean> unitNotEmptyList = orderCategoryReportResultList.stream()
         .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
         .collect(Collectors.toList());
      EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
      String resultTitle = StringUtils.isBlank(unitResult.getUnitName()) ? "检测结果" : "检测结果(" + unitResult.getUnitName() + ")";
      firstRowDataList.add(resultTitle);
      Long firstFolderId = orderCategoryReportResultList.get(0).getFolderId();
      List<EmcReportResultBean> firstFolderReportResultList = orderCategoryReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getFolderId(), firstFolderId))
         .collect(Collectors.toList());
      List<EmcReportResultBean> firstFolderSampleBeginTimeReportResultList = firstFolderReportResultList.stream()
         .filter(e -> e.getSampleBeginTime() != null)
         .sorted(Comparator.comparing(e -> e.getSampleBeginTime(), Comparator.nullsLast(String::compareTo)))
         .collect(Collectors.toList());
      List<String> sampleBeginTimeList = firstFolderSampleBeginTimeReportResultList.stream()
         .map(e -> e.getSampleBeginTime())
         .distinct()
         .collect(Collectors.toList());
      if (!sampleBeginTimeList.isEmpty()) {
         int resultCount = sampleBeginTimeList.size();

         for (long l = 0L; l < (long)(resultCount - 1); l++) {
            firstRowDataList.add("");
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(0, 4, 4 + resultCount - 1));
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 4; i++) {
            secondRowDataList.add("");
         }

         for (int i = 0; i < sampleBeginTimeList.size(); i++) {
            String sbt = sampleBeginTimeList.get(i);
            secondRowDataList.add("第" + this.numberToChinese(i + 1) + "次（" + sbt + "）");
         }

         tableDataList.add(secondRowDataList);
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         mergedRowList.add(new MergedRow(2, 0, 1));
         mergedRowList.add(new MergedRow(3, 0, 1));
         List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();
         orderCategoryReportResultList.sort(Comparator.comparing(e -> e.getFolderId()));
         List<Long> folderIdList = orderCategoryReportResultList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
         List<EmcFolderBean> folderList = templateSourceDTO.getFolderList();
         AtomicInteger rowIndex = new AtomicInteger(2);

         for (Long folderId : folderIdList) {
            int mergedRowStartIndex = rowIndex.get();
            EmcOrderBean folderOder = orderList.stream().filter(e -> ObjectUtils.equals(e.getFolderId(), folderId)).findFirst().orElse(null);
            LocalDate sampleBeginDate = folderOder.getSampleBeginDate();
            EmcFolderBean folder = folderList.stream().filter(e -> ObjectUtils.equals(e.getId(), folderId)).findFirst().orElse(null);
            String monitorPointName = folder.getMonitorPointName();
            String orderDesc = folderOder.getOrderDesc();
            List<EmcReportResultBean> folderReportResultList = orderCategoryReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getFolderId(), folderId))
               .collect(Collectors.toList());
            Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = folderReportResultList.stream()
               .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
            itemGroupReportResultListMap.forEach(
               (key, curReportResultList) -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(sampleBeginDate);
                  rowDataList.add(monitorPointName);
                  rowDataList.add(orderDesc);
                  rowDataList.add(curReportResultList.get(0).getExt$Item("itemgroup"));

                  for (String sbt : sampleBeginTimeList) {
                     EmcReportResultBean reportResult = curReportResultList.stream()
                        .filter(e -> ObjectUtils.equals(e.getSampleBeginTime(), sbt))
                        .findFirst()
                        .orElse(null);
                     if (reportResult == null) {
                        rowDataList.add("");
                     } else {
                        rowDataList.add(this.transScientificCounting(reportResult.getReportResult()));
                     }
                  }

                  tableDataList.add(rowDataList);
                  rowIndex.incrementAndGet();
               }
            );
            int mergedRowEndIndex = rowIndex.get() - 1;
            mergedRowList.add(new MergedRow(0, mergedRowStartIndex, mergedRowEndIndex));
            mergedRowList.add(new MergedRow(1, mergedRowStartIndex, mergedRowEndIndex));
            mergedRowList.add(new MergedRow(2, mergedRowStartIndex, mergedRowEndIndex));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
      }
   }

   private void dixiashuiReportTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedRow> mergedRowList = new ArrayList<>();
      List<MergedColumn> mergedColumnList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测项目");
      firstRowDataList.add("检测结果");
      List<Long> folderIdList = orderCategoryReportResultList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
      if (!folderIdList.isEmpty()) {
         int resultCount = folderIdList.size();

         for (long l = 0L; l < (long)(resultCount - 1); l++) {
            firstRowDataList.add("");
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(0, 1, 1 + resultCount - 1));
         List<Object> secondRowDataList = new ArrayList<>();
         secondRowDataList.add("检测地点");
         List<EmcFolderBean> folderList = templateSourceDTO.getFolderList();
         List<EmcFolderBean> reportFolderList = folderList.stream().filter(e -> folderIdList.contains(e.getId())).collect(Collectors.toList());
         List<EmcFolderBean> sortReportFolderList = reportFolderList.stream()
            .sorted(Comparator.comparing(e -> e.getFolderNo(), Comparator.nullsLast(String::compareTo)))
            .collect(Collectors.toList());

         for (int i = 0; i < sortReportFolderList.size(); i++) {
            EmcFolderBean rf = sortReportFolderList.get(i);
            secondRowDataList.add(rf.getMonitorPointName());
         }

         tableDataList.add(secondRowDataList);
         List<Object> thirdRowDataList = new ArrayList<>();
         thirdRowDataList.add("采样日期");
         List<Object> fourthRowDataList = new ArrayList<>();
         fourthRowDataList.add("样品状态");
         List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();

         for (EmcFolderBean f : sortReportFolderList) {
            EmcOrderBean folderOder = orderList.stream().filter(e -> ObjectUtils.equals(e.getFolderId(), f.getId())).findFirst().orElse(null);
            thirdRowDataList.add(
               (folderOder.getSampleBeginDate() == null ? "" : folderOder.getSampleBeginDate())
                  + (folderOder.getSampleBeginTime() == null ? "" : "(" + folderOder.getSampleBeginTime() + ")")
            );
            fourthRowDataList.add(folderOder.getOrderDesc());
         }

         tableDataList.add(thirdRowDataList);
         tableDataList.add(fourthRowDataList);
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = orderCategoryReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> rowDataList = new ArrayList<>();
               List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
                  .filter(e -> !StringUtils.isEmpty(e.getUnitName()))
                  .collect(Collectors.toList());
               EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
               String resultTitle = StringUtils.isBlank(unitResult.getUnitName())
                  ? curReportResultList.get(0).getExt$Item("reportTestName")
                  : curReportResultList.get(0).getExt$Item("reportTestName") + "(" + unitResult.getUnitName() + ")";
               rowDataList.add(resultTitle);

               for (EmcFolderBean f : sortReportFolderList) {
                  EmcReportResultBean reportResult = curReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getFolderId(), f.getId()))
                     .findFirst()
                     .orElse(null);
                  if (reportResult == null) {
                     rowDataList.add("");
                  } else {
                     rowDataList.add(this.transScientificCounting(reportResult.getReportResult()));
                  }
               }

               tableDataList.add(rowDataList);
            }
         );
         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
      }
   }

   private void youzuzhiSampleTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableCount, String orderCategory) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), orderCategory))
         .collect(Collectors.toList());
      List<EmcReportResultBean> commonReportResultList = orderCategoryReportResultList.stream()
         .filter(e -> !ObjectUtils.equals(e.getExt$Item("reportTestName"), "挥发性有机物"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> multleReportResultList = commonReportResultList.stream()
         .filter(e -> "1".equals(e.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      this.youzuzhiMultleTable(document, tableCount, orderCategory, multleReportResultList);
      List<EmcReportResultBean> singleReportResultList = commonReportResultList.stream()
         .filter(e -> !"1".equals(e.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      this.youzuzhiCommonTable(document, tableCount, orderCategory, singleReportResultList);
      List<EmcReportResultBean> hfxyjwReportResultList = orderCategoryReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getExt$Item("reportTestName"), "挥发性有机物"))
         .collect(Collectors.toList());
      this.hfxyjwSamplenumReportTable(document, templateSourceDTO, 3, tableCount, 0, hfxyjwReportResultList);
   }

   private void youzuzhiMultleTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> multleReportResultList) {
      if (!multleReportResultList.isEmpty()) {
         Map<String, List<EmcReportResultBean>> testReportResultListMap = multleReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : testReportResultListMap.keySet()) {
            List<EmcReportResultBean> testReportResultList = testReportResultListMap.get(testName);
            List<List<Object>> tableDataList = new ArrayList<>();
            List<MergedRow> mergedRowList = new ArrayList<>();
            List<MergedColumn> mergedColumnList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();
            firstRowDataList.add("采样日期");
            firstRowDataList.add("检测点位");
            firstRowDataList.add("检测项目");
            firstRowDataList.add("");
            firstRowDataList.add("检出限");
            firstRowDataList.add("检测结果");
            int frequency = testReportResultList.stream().mapToInt(e -> e.getFrequency().intValue()).max().orElse(1);
            int sampleNum = testReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getExt$Item("samplenum"))).max().orElse(1);
            int resultColumnNum = 1;
            String tableType = "frequency";
            if (frequency > 1) {
               tableType = "frequency";
               resultColumnNum = frequency;
            } else {
               tableType = "sampleNum";
               resultColumnNum = sampleNum;
            }

            for (long l = 0L; l < (long)resultColumnNum; l++) {
               firstRowDataList.add("");
            }

            tableDataList.add(firstRowDataList);
            mergedColumnList.add(new MergedColumn(0, 5, 5 + resultColumnNum));
            List<Object> secondRowDataList = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
               secondRowDataList.add("");
            }

            if ("sampleNum".equals(tableType)) {
               for (int i = 0; i < resultColumnNum; i++) {
                  int num = i + 1;
                  EmcReportResultBean resultBean = testReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  EmcReportResultBean resultBean2 = testReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getItemName(), "采样结束时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  secondRowDataList.add(
                     "第"
                        + this.numberToChinese(i + 1)
                        + "次"
                        + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n(" + resultBean.getReportResult())
                        + (StringUtils.isBlank(resultBean2.getReportResult()) ? "" : "\n-" + resultBean2.getReportResult())
                        + ")"
                  );
               }
            } else {
               for (int i = 0; i < resultColumnNum; i++) {
                  int num = i + 1;
                  EmcReportResultBean resultBean = testReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  EmcReportResultBean resultBean2 = testReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getItemName(), "采样结束时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                     .findFirst()
                     .orElse(new EmcReportResultBean());
                  secondRowDataList.add(
                     this.numberToChinese(i + 1)
                        + "时段"
                        + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n(" + resultBean.getReportResult())
                        + (StringUtils.isBlank(resultBean2.getReportResult()) ? "" : "\n-" + resultBean2.getReportResult())
                        + ")"
                  );
               }
            }

            secondRowDataList.add("平均值");
            tableDataList.add(secondRowDataList);
            mergedRowList.add(new MergedRow(0, 0, 1));
            mergedRowList.add(new MergedRow(1, 0, 1));
            mergedRowList.add(new MergedRow(2, 0, 1));
            mergedRowList.add(new MergedRow(3, 0, 1));
            mergedRowList.add(new MergedRow(4, 0, 1));
            mergedColumnList.add(new MergedColumn(0, 2, 3));
            mergedColumnList.add(new MergedColumn(1, 2, 3));
            int finalResultColumnNum = resultColumnNum;
            String finalTableType = tableType;
            List<EmcReportResultBean> planSampleDateReportResultList = testReportResultList.stream()
               .filter(e -> e.getSampleBeginDate() != null)
               .collect(Collectors.toList());
            planSampleDateReportResultList.sort(Comparator.comparing(e -> e.getSampleBeginDate()));
            List<LocalDate> planSampleDateStrList = planSampleDateReportResultList.stream()
               .map(e -> e.getSampleBeginDate())
               .distinct()
               .collect(Collectors.toList());
            AtomicInteger rowIndex = new AtomicInteger(2);

            for (LocalDate planSampleDateStr : planSampleDateStrList) {
               int planSampleDateMergedRowStartIndex = rowIndex.get();
               List<EmcReportResultBean> curPlanSampleDateReportResultList = planSampleDateReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getSampleBeginDate(), planSampleDateStr))
                  .collect(Collectors.toList());
               String formatDate = planSampleDateStr.format(this.sdf);
               curPlanSampleDateReportResultList.sort(Comparator.comparing(e -> e.getFolderId()));

               for (Long folderId : curPlanSampleDateReportResultList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList())) {
                  int folderMergedRowStartIndex = rowIndex.get();
                  List<EmcReportResultBean> folderReportResultList = curPlanSampleDateReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getFolderId(), folderId))
                     .collect(Collectors.toList());
                  String monitorPointName = folderReportResultList.get(0).getMonitorPointName();
                  Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = folderReportResultList.stream()
                     .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
                  itemGroupReportResultListMap.forEach(
                     (key, curReportResultList) -> {
                        int itemGroupMergedRowStartIndex = rowIndex.get();
                        this.youzuzhiMultleResultData(
                           "废气实测浓度",
                           (List<EmcReportResultBean>)curReportResultList,
                           formatDate,
                           monitorPointName,
                           finalResultColumnNum,
                           finalTableType,
                           tableDataList,
                           rowIndex
                        );
                        this.youzuzhiMultleResultData(
                           "废气折算浓度",
                           (List<EmcReportResultBean>)curReportResultList,
                           formatDate,
                           monitorPointName,
                           finalResultColumnNum,
                           finalTableType,
                           tableDataList,
                           rowIndex
                        );
                        int itemGroupMergedRowEndIndex = rowIndex.get() - 1;
                        mergedRowList.add(new MergedRow(2, itemGroupMergedRowStartIndex, itemGroupMergedRowEndIndex));
                     }
                  );
                  int folderMergedRowEndIndex = rowIndex.get() - 1;
                  mergedRowList.add(new MergedRow(1, folderMergedRowStartIndex, folderMergedRowEndIndex));
               }

               int planSampleDateMergedRowEndIndex = rowIndex.get() - 1;
               mergedRowList.add(new MergedRow(0, planSampleDateMergedRowStartIndex, planSampleDateMergedRowEndIndex));
            }

            this.listListNullToString(tableDataList, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + testName + "检测结果");
            wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
            wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
            XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            table.getRow(0).setRepeatHeader(true);
            table.getRow(1).setRepeatHeader(true);
         }
      }
   }

   private void youzuzhiCommonTable(XWPFDocument document, int tabelCount, String orderCategory, List<EmcReportResultBean> orderCategoryReportResultList) {
      if (!orderCategoryReportResultList.isEmpty()) {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("采样日期");
         firstRowDataList.add("检测点位");
         firstRowDataList.add("检测项目");
         firstRowDataList.add("");
         firstRowDataList.add("检出限");
         firstRowDataList.add("检测结果");
         int frequency = orderCategoryReportResultList.stream().mapToInt(e -> e.getFrequency().intValue()).max().orElse(1);
         int sampleNum = orderCategoryReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getExt$Item("samplenum"))).max().orElse(1);
         int resultColumnNum = 1;
         String tableType = "frequency";
         if (frequency > 1) {
            tableType = "frequency";
            resultColumnNum = frequency;
         } else {
            tableType = "sampleNum";
            resultColumnNum = sampleNum;
         }

         for (long l = 0L; l < (long)resultColumnNum; l++) {
            firstRowDataList.add("");
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(0, 5, 5 + resultColumnNum));
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 5; i++) {
            secondRowDataList.add("");
         }

         if ("sampleNum".equals(tableType)) {
            for (int i = 0; i < resultColumnNum; i++) {
               int num = i + 1;
               EmcReportResultBean resultBean = orderCategoryReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               EmcReportResultBean resultBean2 = orderCategoryReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getItemName(), "采样结束时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               secondRowDataList.add(
                  "第"
                     + this.numberToChinese(i + 1)
                     + "次"
                     + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n(" + resultBean.getReportResult())
                     + (StringUtils.isBlank(resultBean2.getReportResult()) ? "" : "\n-" + resultBean2.getReportResult())
                     + ")"
               );
            }
         } else {
            for (int i = 0; i < resultColumnNum; i++) {
               int num = i + 1;
               EmcReportResultBean resultBean = orderCategoryReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               EmcReportResultBean resultBean2 = orderCategoryReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getItemName(), "采样结束时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                  .findFirst()
                  .orElse(new EmcReportResultBean());
               secondRowDataList.add(
                  this.numberToChinese(i + 1)
                     + "时段"
                     + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n(" + resultBean.getReportResult())
                     + (StringUtils.isBlank(resultBean2.getReportResult()) ? "" : "\n-" + resultBean2.getReportResult())
                     + ")"
               );
            }
         }

         secondRowDataList.add("平均值");
         tableDataList.add(secondRowDataList);
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         mergedRowList.add(new MergedRow(2, 0, 1));
         mergedRowList.add(new MergedRow(3, 0, 1));
         mergedRowList.add(new MergedRow(4, 0, 1));
         mergedColumnList.add(new MergedColumn(0, 2, 3));
         mergedColumnList.add(new MergedColumn(1, 2, 3));
         int finalResultColumnNum = resultColumnNum;
         String finalTableType = tableType;
         List<EmcReportResultBean> planSampleDateReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> e.getSampleBeginDate() != null)
            .collect(Collectors.toList());
         planSampleDateReportResultList.sort(Comparator.comparing(e -> e.getSampleBeginDate()));
         List<LocalDate> planSampleDateStrList = planSampleDateReportResultList.stream()
            .map(e -> e.getSampleBeginDate())
            .distinct()
            .collect(Collectors.toList());
         AtomicInteger rowIndex = new AtomicInteger(2);

         for (LocalDate planSampleDateStr : planSampleDateStrList) {
            int planSampleDateMergedRowStartIndex = rowIndex.get();
            List<EmcReportResultBean> curPlanSampleDateReportResultList = planSampleDateReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getSampleBeginDate(), planSampleDateStr))
               .collect(Collectors.toList());
            String formatDate = planSampleDateStr.format(this.sdf);
            curPlanSampleDateReportResultList.sort(Comparator.comparing(e -> e.getFolderId()));

            for (Long folderId : curPlanSampleDateReportResultList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList())) {
               int folderMergedRowStartIndex = rowIndex.get();
               List<EmcReportResultBean> folderReportResultList = curPlanSampleDateReportResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getFolderId(), folderId))
                  .collect(Collectors.toList());
               String monitorPointName = folderReportResultList.get(0).getMonitorPointName();
               Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = folderReportResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
               itemGroupReportResultListMap.forEach(
                  (key, curReportResultList) -> {
                     int itemGroupMergedRowStartIndex = rowIndex.get();
                     this.youzuzhiCommonResultData(
                        "废气实测浓度",
                        (List<EmcReportResultBean>)curReportResultList,
                        formatDate,
                        monitorPointName,
                        finalResultColumnNum,
                        finalTableType,
                        tableDataList,
                        rowIndex
                     );
                     this.youzuzhiCommonResultData(
                        "废气折算浓度",
                        (List<EmcReportResultBean>)curReportResultList,
                        formatDate,
                        monitorPointName,
                        finalResultColumnNum,
                        finalTableType,
                        tableDataList,
                        rowIndex
                     );
                     int itemGroupMergedRowEndIndex = rowIndex.get() - 1;
                     mergedRowList.add(new MergedRow(2, itemGroupMergedRowStartIndex, itemGroupMergedRowEndIndex));
                  }
               );
               int folderMergedRowEndIndex = rowIndex.get() - 1;
               mergedRowList.add(new MergedRow(1, folderMergedRowStartIndex, folderMergedRowEndIndex));
            }

            int planSampleDateMergedRowEndIndex = rowIndex.get() - 1;
            mergedRowList.add(new MergedRow(0, planSampleDateMergedRowStartIndex, planSampleDateMergedRowEndIndex));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("resultTables");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("表 " + (tabelCount + 1) + " " + orderCategory + "检测结果");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
      }
   }

   private void youzuzhiOtherResultData(
      String itemName,
      List<EmcReportResultBean> curReportResultList,
      String planSampleDate,
      String monitorPointName,
      int finalResultColumnNum,
      String finalTableType,
      List<List<Object>> tableDataList,
      AtomicInteger rowIndex
   ) {
      if (!curReportResultList.stream().noneMatch(e -> e.getItemName().contains(itemName))) {
         List<Object> rowDataList = new ArrayList<>();
         rowDataList.add(planSampleDate);
         rowDataList.add(monitorPointName);
         List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
            .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getUnitName()))
            .collect(Collectors.toList());
         EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
         String resultTitle = StringUtils.isBlank(unitResult.getUnitName())
            ? curReportResultList.get(0).getExt$Item("reportTestName")
            : curReportResultList.get(0).getExt$Item("reportTestName") + "(" + unitResult.getUnitName() + ")";
         rowDataList.add(resultTitle);

         for (int i = 0; i < finalResultColumnNum; i++) {
            EmcReportResultBean resultBean = null;
            int num = i + 1;
            if ("sampleNum".equals(finalTableType)) {
               resultBean = curReportResultList.stream()
                  .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                  .findFirst()
                  .orElse(null);
            } else {
               resultBean = curReportResultList.stream()
                  .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
                  .findFirst()
                  .orElse(null);
            }

            if (resultBean == null) {
               rowDataList.add("");
            } else {
               String result = this.transScientificCounting(resultBean.getReportResult());
               rowDataList.add(result);
            }
         }

         tableDataList.add(rowDataList);
         rowIndex.incrementAndGet();
      }
   }

   private void youzuzhiMultleResultData(
      String itemName,
      List<EmcReportResultBean> curReportResultList,
      String planSampleDate,
      String monitorPointName,
      int finalResultColumnNum,
      String finalTableType,
      List<List<Object>> tableDataList,
      AtomicInteger rowIndex
   ) {
      if (!curReportResultList.stream().noneMatch(e -> e.getItemName().contains(itemName))) {
         if (!curReportResultList.stream().filter(e -> e.getItemName().contains(itemName)).allMatch(e -> "/".equals(e.getReportResult()))) {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(planSampleDate);
            rowDataList.add(monitorPointName);
            rowDataList.add(curReportResultList.get(0).getExt$Item("itemGroup"));
            List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getUnitName()))
               .collect(Collectors.toList());
            EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
            String resultTitle = StringUtils.isBlank(unitResult.getUnitName())
               ? unitResult.getItemAliasName()
               : unitResult.getItemAliasName() + "(" + unitResult.getUnitName() + ")";
            rowDataList.add(resultTitle.replace("废气", "").replace(curReportResultList.get(0).getExt$Item("itemGroup"), ""));
            rowDataList.add(unitResult.getLowLimit());
            String total = "0";
            int totalNum = 0;
            Boolean isScientificNumber = false;
            List<String> ndList = new ArrayList<>();

            for (int i = 0; i < finalResultColumnNum; i++) {
               EmcReportResultBean resultBean = null;
               int num = i + 1;
               if ("sampleNum".equals(finalTableType)) {
                  resultBean = curReportResultList.stream()
                     .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                     .findFirst()
                     .orElse(null);
               } else {
                  resultBean = curReportResultList.stream()
                     .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
                     .findFirst()
                     .orElse(null);
               }

               if (resultBean == null) {
                  rowDataList.add("");
                  ndList.add("/");
               } else {
                  String reportResult = resultBean.getReportResult();
                  String showResult = this.transScientificCounting(reportResult);
                  rowDataList.add(showResult);
                  if ("ND".equals(reportResult) || "/".equals(reportResult)) {
                     ndList.add(reportResult);
                  }

                  String ndResult = this.getNdValue(resultBean);
                  if (NumberUtils.isNumber(ndResult) || NumberUtils.isScientificNumber(ndResult)) {
                     total = ArithUtils.add(total, ndResult) + "";
                     totalNum++;
                     if (NumberUtils.isScientificNumber(ndResult)) {
                        isScientificNumber = true;
                     }
                  }
               }
            }

            List<EmcReportResultBean> revisionRuleNotEmptyList = curReportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getRevisionRule()))
               .collect(Collectors.toList());
            EmcReportResultBean revisionRuleResult = revisionRuleNotEmptyList.isEmpty() ? new EmcReportResultBean() : revisionRuleNotEmptyList.get(0);
            String revisionRule = revisionRuleResult.getRevisionRule();
            if (StringUtils.isBlank(revisionRule)) {
               revisionRule = "ISO,2";
            }

            String averageResult = "";
            if (totalNum > 0) {
               averageResult = NumberUtils.rounding(ArithUtils.div(total, totalNum + "") + "", revisionRule);
            }

            if (isScientificNumber) {
               averageResult = this.transScientificCounting(getScientificValue(averageResult, revisionRule));
            }

            if (ndList.size() > 2) {
               rowDataList.add("ND");
            } else {
               rowDataList.add(averageResult);
            }

            tableDataList.add(rowDataList);
            rowIndex.incrementAndGet();
         }
      }
   }

   private void youzuzhiCommonResultData(
      String itemName,
      List<EmcReportResultBean> curReportResultList,
      String planSampleDate,
      String monitorPointName,
      int finalResultColumnNum,
      String finalTableType,
      List<List<Object>> tableDataList,
      AtomicInteger rowIndex
   ) {
      if (!curReportResultList.stream().noneMatch(e -> e.getItemName().contains(itemName))) {
         if (!curReportResultList.stream().filter(e -> e.getItemName().contains(itemName)).allMatch(e -> "/".equals(e.getReportResult()))) {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(planSampleDate);
            rowDataList.add(monitorPointName);
            rowDataList.add(curReportResultList.get(0).getExt$Item("reportTestName"));
            EmcReportResultBean itemNameResult = curReportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName))
               .findFirst()
               .orElse(new EmcReportResultBean());
            String itemAliasName = StringUtils.isBlank(itemNameResult.getItemAliasName()) ? "" : itemNameResult.getItemAliasName();
            List<EmcReportResultBean> unitNotEmptyList = curReportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getUnitName()))
               .collect(Collectors.toList());
            EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
            String unitName = StringUtils.isBlank(unitResult.getUnitName()) ? "" : unitResult.getUnitName();
            String resultTitle = StringUtils.isBlank(unitName) ? itemAliasName : itemAliasName + "(" + unitName + ")";
            rowDataList.add(resultTitle.replace("废气", ""));
            rowDataList.add(unitResult.getLowLimit());
            String total = "0";
            int totalNum = 0;
            Boolean isScientificNumber = false;
            List<String> ndList = new ArrayList<>();

            for (int i = 0; i < finalResultColumnNum; i++) {
               EmcReportResultBean resultBean = null;
               int num = i + 1;
               if ("sampleNum".equals(finalTableType)) {
                  resultBean = curReportResultList.stream()
                     .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                     .findFirst()
                     .orElse(null);
               } else {
                  resultBean = curReportResultList.stream()
                     .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
                     .findFirst()
                     .orElse(null);
               }

               if (resultBean == null) {
                  rowDataList.add("");
                  ndList.add("/");
               } else {
                  String reportResult = resultBean.getReportResult();
                  String showResult = this.transScientificCounting(reportResult);
                  rowDataList.add(showResult);
                  if ("ND".equals(reportResult) || "/".equals(reportResult)) {
                     ndList.add(reportResult);
                  }

                  String ndResult = this.getNdValue(resultBean);
                  if (NumberUtils.isNumber(ndResult) || NumberUtils.isScientificNumber(ndResult)) {
                     total = ArithUtils.add(total, ndResult) + "";
                     totalNum++;
                     if (NumberUtils.isScientificNumber(ndResult)) {
                        isScientificNumber = true;
                     }
                  }
               }
            }

            List<EmcReportResultBean> revisionRuleNotEmptyList = curReportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getRevisionRule()))
               .collect(Collectors.toList());
            EmcReportResultBean revisionRuleResult = revisionRuleNotEmptyList.isEmpty() ? new EmcReportResultBean() : revisionRuleNotEmptyList.get(0);
            String revisionRule = revisionRuleResult.getRevisionRule();
            if (StringUtils.isBlank(revisionRule)) {
               revisionRule = "ISO,2";
            }

            String averageResult = "";
            if (totalNum > 0) {
               averageResult = NumberUtils.rounding(ArithUtils.div(total, totalNum + "") + "", revisionRule);
            }

            if (isScientificNumber) {
               averageResult = this.transScientificCounting(getScientificValue(averageResult, revisionRule));
            }

            if (ndList.size() > 2) {
               rowDataList.add("ND");
            } else {
               rowDataList.add(averageResult);
            }

            tableDataList.add(rowDataList);
            rowIndex.incrementAndGet();
         }
      }
   }

   private void createResultDescTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, boolean isSend) {
      int tableCount = 1;
      tableCount = this.youzuzhiDescTable(document, templateSourceDTO, tableCount);
      if (!isSend) {
         tableCount = this.zsDescTable(document, templateSourceDTO, tableCount);
         tableCount = this.wzzfqDescTable(document, templateSourceDTO, tableCount);
      }

      tableCount = this.qcResultTable(document, templateSourceDTO, isSend, tableCount);
      if (!isSend) {
         tableCount = this.hfxyjwDescTable(document, templateSourceDTO, tableCount);
         this.trDescTable(document, templateSourceDTO, tableCount);
      }

      Word2024Utils.removeKey(document, "qcQuantityTable");
   }

   private int hfxyjwDescTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableIndex) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> wzzReportResultList = reportResultList.stream()
         .filter(
            e -> ObjectUtils.equals(e.getOrderCategory(), "无组织废气")
                  && ObjectUtils.equals(e.getExt$Item("reportTestName"), "挥发性有机物")
                  && StringUtils.equals(e.getDataType(), "sample")
         )
         .collect(Collectors.toList());
      tableIndex = this.hfxyjwDescTable(document, templateSourceDTO, tableIndex, wzzReportResultList, "无组织废气");
      List<EmcReportResultBean> yzzReportResultList = reportResultList.stream()
         .filter(
            e -> ObjectUtils.equals(e.getOrderCategory(), "有组织废气")
                  && ObjectUtils.equals(e.getExt$Item("reportTestName"), "挥发性有机物")
                  && StringUtils.equals(e.getDataType(), "sample")
         )
         .collect(Collectors.toList());
      return this.hfxyjwDescTable(document, templateSourceDTO, tableIndex, yzzReportResultList, "有组织废气");
   }

   private int hfxyjwDescTable(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableIndex, List<EmcReportResultBean> reportResultList, String orderCategory
   ) {
      if (reportResultList.isEmpty()) {
         return tableIndex;
      } else {
         int frequency = reportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getFrequency(), 1)).max().orElse(1);

         for (int i = 1; i <= frequency; i++) {
            int finalFrequency = i;
            List<EmcReportResultBean> frequencyReportResultList = reportResultList.stream()
               .filter(e -> (long)finalFrequency == e.getFrequency())
               .collect(Collectors.toList());
            this.hfxyjwFrequencyDescTable(document, templateSourceDTO, tableIndex, finalFrequency, frequencyReportResultList, orderCategory);
         }

         return tableIndex + 1;
      }
   }

   private void hfxyjwFrequencyDescTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      int tableCount,
      int frequency,
      List<EmcReportResultBean> reportResultList,
      String orderCategory
   ) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<MergedColumn> mergedColumnList = new ArrayList<>();
      List<MergedRow> mergedRowList = new ArrayList<>();
      List<String> monitorPointNameList = reportResultList.stream().map(e -> e.getMonitorPointName()).distinct().collect(Collectors.toList());
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测项目");

      for (int i = 1; i <= monitorPointNameList.size(); i++) {
         firstRowDataList.add("检测结果（μg/m3）");
      }

      firstRowDataList.add("检出限\n（μg/m3）");
      tableDataList.add(firstRowDataList);
      mergedColumnList.add(new MergedColumn(0, 1, monitorPointNameList.size()));
      List<Object> secondRowDataList = new ArrayList<>();
      secondRowDataList.add("");

      for (int i = 1; i <= monitorPointNameList.size(); i++) {
         secondRowDataList.add(
            "采样日期："
               + DateTimeUtils.formatLocalDate(reportResultList.get(0).getSampleBeginDate(), "yyyy年MM月dd日")
               + "（"
               + this.numberToChinese(frequency)
               + "时段）"
         );
      }

      secondRowDataList.add("");
      tableDataList.add(secondRowDataList);
      mergedColumnList.add(new MergedColumn(1, 1, monitorPointNameList.size()));
      List<Object> thirdRowDataList = new ArrayList<>();
      thirdRowDataList.add("");

      for (String monitorPointName : monitorPointNameList) {
         thirdRowDataList.add(monitorPointName);
      }

      thirdRowDataList.add("");
      tableDataList.add(thirdRowDataList);
      mergedRowList.add(new MergedRow(0, 0, 2));
      mergedRowList.add(new MergedRow(monitorPointNameList.size() + 1, 0, 2));
      List<String> itemGroupList = reportResultList.stream().map(e -> e.getExt$Item("itemGroup")).distinct().collect(Collectors.toList());
      itemGroupList.sort(Comparator.comparing(String::toString));

      for (String itemGroup : itemGroupList) {
         List<EmcReportResultBean> itemGroupReportResultList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getExt$Item("itemGroup"), itemGroup))
            .collect(Collectors.toList());
         List<Object> rowDataList = new ArrayList<>();
         String itemgroup = itemGroupReportResultList.get(0).getExt$Item("itemGroup");
         rowDataList.add(itemgroup);

         for (String monitorPointName : monitorPointNameList) {
            List<EmcReportResultBean> monitorPointReportResultList = itemGroupReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getMonitorPointName(), monitorPointName))
               .collect(Collectors.toList());
            EmcReportResultBean emcReportResultBean = monitorPointReportResultList.stream()
               .filter(e -> e.getLowLimit() != null)
               .findFirst()
               .orElse(monitorPointReportResultList.isEmpty() ? new EmcReportResultBean() : monitorPointReportResultList.get(0));
            String reportResult = emcReportResultBean.getReportResult();
            rowDataList.add(this.transScientificCounting(reportResult));
         }

         Map<String, Long> lowLimitCountMap = itemGroupReportResultList.stream()
            .filter(e -> e.getLowLimit() != null)
            .collect(Collectors.groupingBy(EmcReportResultBean::getLowLimit, Collectors.counting()));
         String mostFrequentLowLimit = lowLimitCountMap.entrySet().stream().max(Entry.comparingByValue()).map(Entry::getKey).orElse(null);
         rowDataList.add(mostFrequentLowLimit);
         tableDataList.add(rowDataList);
      }

      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("表 " + tableCount + "-" + frequency + " 挥发性有机物检测结果（" + orderCategory + "）");
      wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
      wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      table.getRow(0).setRepeatHeader(true);
      table.getRow(1).setRepeatHeader(true);
      table.getRow(2).setRepeatHeader(true);
   }

   private void youzuzhiDescCommonResultData(
      String itemName,
      String showName,
      List<EmcReportResultBean> reportResultList,
      int resultColumnNum,
      String tableType,
      List<List<Object>> tableDataList,
      AtomicInteger rowIndex,
      List<MergedColumn> mergedColumnList,
      boolean showLimit
   ) {
      List<Object> rowDataList = new ArrayList<>();

      for (int i = 0; i < 2; i++) {
         rowDataList.add(showName);
      }

      String total = "0";
      int totalNum = 0;
      Boolean isScientificNumber = false;

      for (int i = 0; i < resultColumnNum; i++) {
         EmcReportResultBean resultBean = null;
         int num = i + 1;
         if ("sampleNum".equals(tableType)) {
            resultBean = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getItemName(), itemName) && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
               .findFirst()
               .orElse(null);
         } else {
            resultBean = reportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getItemName(), itemName) && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
               .findFirst()
               .orElse(null);
         }

         if (resultBean == null) {
            rowDataList.add("");
         } else {
            String reportResult = resultBean.getReportResult();
            String showResult = this.transScientificCounting(reportResult);
            rowDataList.add(showResult);
            String ndResult = this.getNdValue(resultBean);
            if (NumberUtils.isNumber(ndResult) || NumberUtils.isScientificNumber(ndResult)) {
               total = ArithUtils.add(total, ndResult) + "";
               totalNum++;
               if (NumberUtils.isScientificNumber(ndResult)) {
                  isScientificNumber = true;
               }
            }
         }
      }

      if (resultColumnNum > 1) {
         List<EmcReportResultBean> revisionRuleNotEmptyList = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getItemName(), itemName) && !StringUtils.isEmpty(e.getRevisionRule()))
            .collect(Collectors.toList());
         EmcReportResultBean revisionRuleResult = revisionRuleNotEmptyList.isEmpty() ? new EmcReportResultBean() : revisionRuleNotEmptyList.get(0);
         String revisionRule = revisionRuleResult.getRevisionRule();
         if (StringUtils.isBlank(revisionRule)) {
            revisionRule = "ISO,2";
         }

         String averageResult = "";
         if (totalNum > 0) {
            averageResult = NumberUtils.rounding(ArithUtils.div(total, totalNum + "") + "", revisionRule);
         }

         if (isScientificNumber) {
            averageResult = this.transScientificCounting(getScientificValue(averageResult, revisionRule));
         }

         if ("标干流量".equals(itemName)) {
            rowDataList.add(averageResult);
         } else {
            rowDataList.add("");
         }
      }

      if (showLimit) {
         EmcReportResultBean indexResult = reportResultList.stream()
            .filter(e -> ObjectUtils.equals(e.getItemName(), itemName) && e.getIndexLowLimit() != null)
            .findFirst()
            .orElse(new EmcReportResultBean());
         rowDataList.add(indexResult.getIndexLowLimit());
      }

      tableDataList.add(rowDataList);
      mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
      rowIndex.incrementAndGet();
   }

   private void youzuzhiDescCommonResultData2(
      String itemName,
      List<EmcReportResultBean> reportResultList,
      int resultColumnNum,
      String tableType,
      List<List<Object>> tableDataList,
      AtomicInteger rowIndex,
      boolean showLimit
   ) {
      List<Object> rowDataList = new ArrayList<>();
      rowDataList.add(reportResultList.get(0).getExt$Item("itemgroup"));
      List<EmcReportResultBean> unitNotEmptyList = reportResultList.stream()
         .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getUnitName()))
         .collect(Collectors.toList());
      EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
      String resultTitle = StringUtils.isBlank(unitResult.getUnitName())
         ? unitResult.getItemAliasName()
         : unitResult.getItemAliasName() + "(" + unitResult.getUnitName() + ")";
      rowDataList.add(resultTitle);
      String total = "0";
      int totalNum = 0;
      Boolean isScientificNumber = false;
      List<String> ndList = new ArrayList<>();

      for (int i = 0; i < resultColumnNum; i++) {
         EmcReportResultBean resultBean = null;
         int num = i + 1;
         if ("sampleNum".equals(tableType)) {
            resultBean = reportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
               .findFirst()
               .orElse(null);
         } else {
            resultBean = reportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
               .findFirst()
               .orElse(null);
         }

         if (resultBean == null) {
            rowDataList.add("");
            ndList.add("/");
         } else {
            String reportResult = resultBean.getReportResult();
            String showResult = this.transScientificCounting(reportResult);
            rowDataList.add(showResult);
            String ndResult = this.getNdValue(resultBean);
            if ("ND".equals(reportResult)) {
               ndList.add(reportResult);
            }

            if (NumberUtils.isNumber(ndResult) || NumberUtils.isScientificNumber(ndResult)) {
               total = ArithUtils.add(total, ndResult) + "";
               totalNum++;
               if (NumberUtils.isScientificNumber(ndResult)) {
                  isScientificNumber = true;
               }
            }
         }
      }

      if (resultColumnNum > 1) {
         List<EmcReportResultBean> revisionRuleNotEmptyList = reportResultList.stream()
            .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getRevisionRule()))
            .collect(Collectors.toList());
         EmcReportResultBean revisionRuleResult = revisionRuleNotEmptyList.isEmpty() ? new EmcReportResultBean() : revisionRuleNotEmptyList.get(0);
         String revisionRule = revisionRuleResult.getRevisionRule();
         if (StringUtils.isBlank(revisionRule)) {
            revisionRule = "ISO,2";
         }

         String averageResult = "";
         if (totalNum > 0) {
            averageResult = NumberUtils.rounding(ArithUtils.div(total, totalNum + "") + "", revisionRule);
         }

         if (isScientificNumber) {
            averageResult = this.transScientificCounting(getScientificValue(averageResult, revisionRule));
         }

         if (ndList.size() > 2) {
            rowDataList.add("ND");
         } else {
            rowDataList.add(averageResult);
         }
      }

      if (showLimit) {
         EmcReportResultBean indexResult = reportResultList.stream()
            .filter(e -> e.getItemName().contains(itemName) && e.getIndexLowLimit() != null)
            .findFirst()
            .orElse(new EmcReportResultBean());
         rowDataList.add(indexResult.getIndexLowLimit());
      }

      tableDataList.add(rowDataList);
      rowIndex.incrementAndGet();
   }

   private void youzuzhiDescCommonResultDataMax(
      String itemName,
      List<EmcReportResultBean> reportResultList,
      int resultColumnNum,
      String tableType,
      List<List<Object>> tableDataList,
      AtomicInteger rowIndex,
      boolean showLimit
   ) {
      List<Object> rowDataList = new ArrayList<>();
      rowDataList.add(reportResultList.get(0).getExt$Item("itemgroup"));
      List<EmcReportResultBean> unitNotEmptyList = reportResultList.stream()
         .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getUnitName()))
         .collect(Collectors.toList());
      EmcReportResultBean unitResult = unitNotEmptyList.isEmpty() ? new EmcReportResultBean() : unitNotEmptyList.get(0);
      String resultTitle = StringUtils.isBlank(unitResult.getUnitName())
         ? unitResult.getItemAliasName()
         : unitResult.getItemAliasName() + "(" + unitResult.getUnitName() + ")";
      rowDataList.add(resultTitle);
      Double max = 0.0;
      int totalNum = 0;
      Boolean isScientificNumber = false;
      List<String> ndList = new ArrayList<>();

      for (int i = 0; i < resultColumnNum; i++) {
         EmcReportResultBean resultBean = null;
         int num = i + 1;
         if ("sampleNum".equals(tableType)) {
            resultBean = reportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
               .findFirst()
               .orElse(null);
         } else {
            resultBean = reportResultList.stream()
               .filter(e -> e.getItemName().contains(itemName) && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
               .findFirst()
               .orElse(null);
         }

         if (resultBean == null) {
            rowDataList.add("");
            ndList.add("/");
         } else {
            String reportResult = resultBean.getReportResult();
            String showResult = this.transScientificCounting(reportResult);
            rowDataList.add(showResult);
            String ndResult = this.getNdValue(resultBean);
            if ("ND".equals(reportResult)) {
               ndList.add(reportResult);
            }

            if (NumberUtils.isNumber(ndResult) || NumberUtils.isScientificNumber(ndResult)) {
               totalNum++;
               max = NumberUtils.parseDouble(ndResult) > max ? NumberUtils.parseDouble(ndResult) : max;
               if (NumberUtils.isScientificNumber(ndResult)) {
                  isScientificNumber = true;
               }
            }
         }
      }

      if (resultColumnNum > 1) {
         List<EmcReportResultBean> revisionRuleNotEmptyList = reportResultList.stream()
            .filter(e -> e.getItemName().contains(itemName) && !StringUtils.isEmpty(e.getRevisionRule()))
            .collect(Collectors.toList());
         EmcReportResultBean revisionRuleResult = revisionRuleNotEmptyList.isEmpty() ? new EmcReportResultBean() : revisionRuleNotEmptyList.get(0);
         String revisionRule = revisionRuleResult.getRevisionRule();
         if (StringUtils.isBlank(revisionRule)) {
            revisionRule = "ISO,2";
         }

         String maxStr = "";
         if (totalNum > 0) {
            maxStr = max + "";
         }

         if (isScientificNumber) {
            maxStr = this.transScientificCounting(getScientificValue(maxStr, revisionRule));
         }

         if (ndList.size() > 2) {
            rowDataList.add("ND");
         } else {
            rowDataList.add(maxStr);
         }
      }

      if (showLimit) {
         EmcReportResultBean indexResult = reportResultList.stream()
            .filter(e -> e.getItemName().contains(itemName) && e.getIndexLowLimit() != null)
            .findFirst()
            .orElse(new EmcReportResultBean());
         rowDataList.add(indexResult.getIndexLowLimit());
      }

      tableDataList.add(rowDataList);
      rowIndex.incrementAndGet();
   }

   private void trDescTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tableCount) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), "土壤"))
         .collect(Collectors.toList());
      if (!orderCategoryReportResultList.isEmpty()) {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("点位名称");
         firstRowDataList.add("样品编号");
         firstRowDataList.add("采样日期");
         firstRowDataList.add("采样时间");
         firstRowDataList.add("土壤颜色、质地及气味");
         tableDataList.add(firstRowDataList);
         List<String> orderNoList = orderCategoryReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
         orderNoList.sort(Comparator.comparing(String::toString));

         for (String orderNo : orderNoList) {
            EmcReportResultBean curReportResult = orderCategoryReportResultList.stream()
               .filter(e -> ObjectUtils.equals(orderNo, e.getOrderNo()))
               .findFirst()
               .orElse(new EmcReportResultBean());
            List<Object> resultDataList = new ArrayList<>();
            resultDataList.add(curReportResult.getMonitorPointName());
            resultDataList.add(orderNo);
            resultDataList.add(DateTimeUtils.formatLocalDate(curReportResult.getSampleBeginDate(), "yyyy年MM月dd日"));
            resultDataList.add(curReportResult.getSampleBeginTime());
            resultDataList.add(curReportResult.getOrderDesc());
            tableDataList.add(resultDataList);
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(0, 0, 0));
         wordTemplateDynamicTableDTO.setTableTitle("附表 " + tableCount + " 土壤样品描述");
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
      }
   }

   private int zsDescTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), "噪声"))
         .collect(Collectors.toList());
      if (orderCategoryReportResultList.isEmpty()) {
         return tabelCount;
      } else {
         List<EmcPropertyDataBean> propertyDataList = templateSourceDTO.getPropertyDataList();
         List<EmcPropertyFieldBean> propertyFieldList = templateSourceDTO.getPropertyFieldList();
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测日期");
         firstRowDataList.add("校准设备");
         firstRowDataList.add("声校准器校准值");

         for (int i = 0; i < 2; i++) {
            firstRowDataList.add("声级计校准值");
         }

         mergedColumnList.add(new MergedColumn(0, 3, 4));
         firstRowDataList.add("校准情况");
         tableDataList.add(firstRowDataList);
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 3; i++) {
            secondRowDataList.add("");
            mergedRowList.add(new MergedRow(i, 0, 1));
         }

         secondRowDataList.add("检测前");
         secondRowDataList.add("检测后");
         secondRowDataList.add("");
         tableDataList.add(secondRowDataList);
         List<Long> sampleIdList = orderCategoryReportResultList.stream()
            .filter(e -> e.getSampleId() != null)
            .map(e -> e.getSampleId())
            .distinct()
            .collect(Collectors.toList());
         List<EmcPropertyDataBean> samplePropertyDataList = propertyDataList.stream()
            .filter(e -> sampleIdList.contains(e.getBizId()))
            .collect(Collectors.toList());
         Long propertyTmplId = samplePropertyDataList.isEmpty() ? null : samplePropertyDataList.get(0).getTmplId();
         List<EmcPropertyFieldBean> curPropertyFieldList = propertyFieldList.stream()
            .filter(e -> ObjectUtils.equals(propertyTmplId, e.getTmplId()))
            .collect(Collectors.toList());

         for (int i = 0; i < samplePropertyDataList.size(); i++) {
            EmcPropertyDataBean propertyData = samplePropertyDataList.get(i);
            Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
            String value1 = this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "昼间检测前校准值");
            if (!StringUtils.isBlank(value1) && !"/".equals(value1)) {
               List<Object> resultDataList = new ArrayList<>();
               resultDataList.add(DateTimeUtils.formatLocalDate(orderCategoryReportResultList.get(0).getSampleBeginDate(), "yyyy年MM月dd日"));
               resultDataList.add(orderCategoryReportResultList.get(0).getExt$Item("sampleassistequiptname"));
               resultDataList.add(this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "声校准器校准值"));
               resultDataList.add(value1);
               resultDataList.add(this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "昼间检测后校准值"));
               resultDataList.add("合格");
               tableDataList.add(resultDataList);
               break;
            }
         }

         for (int ix = 0; ix < samplePropertyDataList.size(); ix++) {
            EmcPropertyDataBean propertyData = samplePropertyDataList.get(ix);
            Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
            String value2 = this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "夜间检测前校准值");
            if (!StringUtils.isBlank(value2) && !"/".equals(value2)) {
               List<Object> resultDataList = new ArrayList<>();
               resultDataList.add(DateTimeUtils.formatLocalDate(orderCategoryReportResultList.get(0).getSampleBeginDate(), "yyyy年MM月dd日"));
               resultDataList.add(orderCategoryReportResultList.get(0).getExt$Item("sampleassistequiptname"));
               resultDataList.add(this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "声校准器校准值"));
               resultDataList.add(value2);
               resultDataList.add(this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "夜间检测后校准值"));
               resultDataList.add("合格");
               tableDataList.add(resultDataList);
            }
         }

         if (tableDataList.size() > 3) {
            mergedRowList.add(new MergedRow(0, 2, 3));
            mergedRowList.add(new MergedRow(1, 2, 3));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(2500, 2500, 0, 0, 0, 0));
         wordTemplateDynamicTableDTO.setTableTitle("附表 " + tabelCount + " 噪声校准表");
         wordTemplateDynamicTableDTO.setTableUnit("单位:dB(A)");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
         return tabelCount + 1;
      }
   }

   private int wzzfqDescTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount) {
      List<String> orderCategoryList = ArrayUtils.asList(new String[]{"无组织废气", "环境空气"});
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> orderCategoryList.contains(e.getOrderCategory()) && e.getSampleId() != null && e.getSampleBeginDate() != null)
         .collect(Collectors.toList());
      if (orderCategoryReportResultList.isEmpty()) {
         return tabelCount;
      } else {
         List<EmcOrderTaskResultBean> orderTaskResultList = templateSourceDTO.getOrderTaskResultList();
         List<EmcOrderTaskResultBean> orderCategoryOrderTaskResultList = orderTaskResultList.stream()
            .filter(
               e -> orderCategoryList.contains(e.getExt$Item("ordercategory")) && e.getExt$Item("sampleid") != null && e.getExt$Item("samplebegindate") != null
            )
            .collect(Collectors.toList());
         List<EmcPropertyDataBean> propertyDataList = templateSourceDTO.getPropertyDataList();
         List<EmcPropertyFieldBean> propertyFieldList = templateSourceDTO.getPropertyFieldList();
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("采样日期");
         firstRowDataList.add("采样时间");
         firstRowDataList.add("气温（℃）");
         firstRowDataList.add("气压（kPa）");
         firstRowDataList.add("风向");
         firstRowDataList.add("风速（m/s）");
         firstRowDataList.add("湿度（%）");
         firstRowDataList.add("天气");
         tableDataList.add(firstRowDataList);
         List<LocalDate> sampleBeginDateList = orderCategoryReportResultList.stream().map(e -> e.getSampleBeginDate()).distinct().collect(Collectors.toList());
         Collections.sort(sampleBeginDateList);

         for (int i = 0; i < sampleBeginDateList.size(); i++) {
            LocalDate sampleBeginDate = sampleBeginDateList.get(i);
            List<EmcReportResultBean> sampleBeginDateReportResultList = orderCategoryReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getSampleBeginDate(), sampleBeginDate))
               .collect(Collectors.toList());
            List<EmcReportResultBean> beginTimeReportResultList = sampleBeginDateReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && e.getReportResult() != null)
               .collect(Collectors.toList());
            if (!beginTimeReportResultList.isEmpty()) {
               List<String> beginTimeList = beginTimeReportResultList.stream().map(e -> e.getReportResult()).distinct().collect(Collectors.toList());
               Collections.sort(beginTimeList);

               for (int j = 0; j < beginTimeList.size(); j++) {
                  String beginTime = beginTimeList.get(j);
                  List<EmcReportResultBean> curBeginTimeReportResultList = beginTimeReportResultList.stream()
                     .filter(e -> ObjectUtils.equals(e.getReportResult(), beginTime))
                     .collect(Collectors.toList());
                  Long orderTaskId = curBeginTimeReportResultList.get(0).getOrderTaskId();
                  EmcReportResultBean endTimeReportResult = sampleBeginDateReportResultList.stream()
                     .filter(
                        e -> ObjectUtils.equals(e.getItemName(), "采样结束时间")
                              && e.getReportResult() != null
                              && ObjectUtils.equals(orderTaskId, e.getOrderTaskId())
                     )
                     .findFirst()
                     .orElse(null);
                  String endTime = endTimeReportResult == null ? beginTime : endTimeReportResult.getReportResult();
                  List<EmcOrderTaskResultBean> taskOrderTaskResultList = orderCategoryOrderTaskResultList.stream()
                     .filter(e -> ObjectUtils.equals(orderTaskId, e.getOrderTaskId()))
                     .collect(Collectors.toList());
                  Long sampleId = curBeginTimeReportResultList.get(0).getSampleId();
                  EmcPropertyDataBean propertyData = propertyDataList.stream()
                     .filter(e -> ObjectUtils.equals(sampleId, e.getBizId()))
                     .findFirst()
                     .orElse(new EmcPropertyDataBean());
                  Long propertyTmplId = propertyData.getTmplId();
                  List<EmcPropertyFieldBean> curPropertyFieldList = propertyFieldList.stream()
                     .filter(e -> ObjectUtils.equals(propertyTmplId, e.getTmplId()))
                     .collect(Collectors.toList());
                  Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
                  List<Object> resultDataList = new ArrayList<>();
                  resultDataList.add(DateTimeUtils.formatLocalDate(sampleBeginDate, "yyyy年MM月dd日"));
                  resultDataList.add(beginTime + "~" + endTime);
                  String value1 = this.getWzzfqDescTableResultValue(curPropertyFieldList, propertyDataMap, taskOrderTaskResultList, "气温", "气温");
                  resultDataList.add(value1);
                  String value2 = this.getWzzfqDescTableResultValue(curPropertyFieldList, propertyDataMap, taskOrderTaskResultList, "气压", "气压");
                  resultDataList.add(value2);
                  String value3 = this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "风向");
                  if (StringUtils.isBlank(value3)) {
                     EmcOrderTaskResultBean curOrderTaskResultBean = taskOrderTaskResultList.stream()
                        .filter(e -> ObjectUtils.equals("风向", e.getItemAliasName()))
                        .findFirst()
                        .orElse(new EmcOrderTaskResultBean());
                     value3 = curOrderTaskResultBean.getReportResult();
                  }

                  resultDataList.add(value3);
                  String value4 = this.getWzzfqDescTableResultValue(curPropertyFieldList, propertyDataMap, taskOrderTaskResultList, "风速", "风速");
                  resultDataList.add(value4);
                  String value5 = this.getWzzfqDescTableResultValue(curPropertyFieldList, propertyDataMap, taskOrderTaskResultList, "湿度", "相对湿度");
                  resultDataList.add(value5);
                  resultDataList.add(this.getPropertyDataByFieldName(curPropertyFieldList, propertyDataMap, "天气"));
                  tableDataList.add(resultDataList);
               }
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setColumnWidthList(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
         wordTemplateDynamicTableDTO.setTableTitle("附表 " + tabelCount + " 气象参数一览表");
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         return tabelCount + 1;
      }
   }

   private String getWzzfqDescTableResultValue(
      List<EmcPropertyFieldBean> propertyFieldList,
      Map<String, Object> propertyDataMap,
      List<EmcOrderTaskResultBean> orderTaskResultList,
      String fieldName,
      String itemName
   ) {
      String value1 = this.getPropertyDataByFieldName(propertyFieldList, propertyDataMap, fieldName);
      if (StringUtils.isBlank(value1)) {
         List<EmcOrderTaskResultBean> curResultList = orderTaskResultList.stream()
            .filter(e -> ObjectUtils.equals(itemName, e.getItemAliasName()) && NumberUtils.isNumber(e.getReportResult()))
            .collect(Collectors.toList());
         EmcOrderTaskResultBean max = curResultList.stream().max(Comparator.comparing(e -> NumberUtils.parseDouble(e.getReportResult()))).orElse(null);
         EmcOrderTaskResultBean min = curResultList.stream().min(Comparator.comparing(e -> NumberUtils.parseDouble(e.getReportResult()))).orElse(null);
         if (min == null) {
            if (max == null) {
               value1 = "/";
            } else {
               value1 = max.getReportResult();
            }
         } else if (max != null && NumberUtils.parseDouble(min.getReportResult()) != NumberUtils.parseDouble(max.getReportResult())) {
            value1 = min.getReportResult() + "-" + max.getReportResult();
         } else {
            value1 = min.getReportResult();
         }
      }

      return value1;
   }

   private int youzuzhiDescTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, int tabelCount) {
      List<EmcReportResultBean> reportResultList = templateSourceDTO.getReportResultList();
      List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
         .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
         .collect(Collectors.toList());
      List<EmcReportResultBean> orderCategoryReportResultList = sampleReportResultList.stream()
         .filter(e -> ObjectUtils.equals(e.getOrderCategory(), "有组织废气"))
         .collect(Collectors.toList());
      if (orderCategoryReportResultList.isEmpty()) {
         return tabelCount;
      } else {
         List<EmcReportResultBean> planSampleDateReportResultList = orderCategoryReportResultList.stream()
            .filter(e -> e.getSampleBeginDate() != null)
            .collect(Collectors.toList());
         planSampleDateReportResultList.sort(Comparator.comparing(e -> e.getFolderId()));
         planSampleDateReportResultList.sort(Comparator.comparing(e -> e.getSampleBeginDate()));
         List<Long> otherTestIdList1 = Arrays.asList(2249L, 1402L);
         List<Long> otherTestIdList2 = Arrays.asList(2256L);
         List<Long> folderIdList = planSampleDateReportResultList.stream().map(e -> e.getFolderId()).distinct().collect(Collectors.toList());
         int tableIndex = 1;

         for (Long folderId : folderIdList) {
            List<EmcReportResultBean> folderReportResultList = planSampleDateReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getFolderId(), folderId))
               .collect(Collectors.toList());
            List<EmcReportResultBean> commonReportResultList = folderReportResultList.stream()
               .filter(e -> !otherTestIdList1.contains(e.getTestId()) && !otherTestIdList2.contains(e.getTestId()))
               .collect(Collectors.toList());
            if (!commonReportResultList.isEmpty()) {
               this.youzuzhiDescCommonTable(document, templateSourceDTO, commonReportResultList, tableIndex);
               tableIndex++;
            }

            List<EmcReportResultBean> otherReportResultList1 = folderReportResultList.stream()
               .filter(e -> otherTestIdList1.contains(e.getTestId()))
               .collect(Collectors.toList());
            if (!otherReportResultList1.isEmpty()) {
               this.youzuzhiDescOtherTable1(document, templateSourceDTO, otherReportResultList1, tableIndex);
               tableIndex++;
            }

            List<EmcReportResultBean> otherReportResultList2 = folderReportResultList.stream()
               .filter(e -> otherTestIdList2.contains(e.getTestId()))
               .collect(Collectors.toList());
            if (!otherReportResultList2.isEmpty()) {
               this.youzuzhiDescOtherTable2(document, templateSourceDTO, otherReportResultList2, tableIndex);
               tableIndex++;
            }
         }

         return tabelCount + 1;
      }
   }

   private void youzuzhiDescOtherTable1(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, List<EmcReportResultBean> folderReportResultList, int tabelCount
   ) {
      if (!folderReportResultList.isEmpty()) {
         AtomicInteger rowIndex = new AtomicInteger(0);
         List<EmcPropertyDataBean> propertyDataList = templateSourceDTO.getPropertyDataList();
         List<EmcPropertyFieldBean> propertyFieldList = templateSourceDTO.getPropertyFieldList();
         List<EmcSampleBean> sampleList = templateSourceDTO.getSampleList();
         List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
         List<EmcIndexCategoryBean> indexCategoryList = templateSourceDTO.getIndexCategoryList();
         EmcReportBean report = templateSourceDTO.getReport();
         String showlimit = report.getExt$Item("showlimit");
         boolean showLimit = "1".equals(showlimit);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            firstRowDataList.add("项目类别");
         }

         int frequency = folderReportResultList.stream().mapToInt(e -> e.getFrequency().intValue()).max().orElse(1);
         int sampleNum = folderReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getExt$Item("samplenum"))).max().orElse(1);
         int resultColumnNum = 1;
         int avgColumnNum = 1;
         String tableType = "frequency";
         if (frequency > 1) {
            tableType = "frequency";
            resultColumnNum = frequency;
         } else {
            tableType = "sampleNum";
            resultColumnNum = sampleNum;
         }

         if (resultColumnNum == 1) {
            avgColumnNum = 0;
         }

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            firstRowDataList.add("项目参数");
         }

         if (showLimit) {
            firstRowDataList.add("标准限值");
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            secondRowDataList.add("");
         }

         LocalDate planSampleDate = folderReportResultList.get(0).getSampleBeginDate();
         String formatDate = planSampleDate.format(this.sdf);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            secondRowDataList.add("采样日期：" + formatDate);
         }

         if (showLimit) {
            secondRowDataList.add("");
         }

         tableDataList.add(secondRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         if (showLimit) {
            mergedRowList.add(new MergedRow(resultColumnNum + 2 + avgColumnNum, 0, 1));
         } else {
            mergedRowList.add(new MergedRow(resultColumnNum + 1 + avgColumnNum, 0, 1));
         }

         rowIndex.incrementAndGet();
         List<Object> RowDataList3 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList3.add("测点位置");
         }

         String monitorPointName = folderReportResultList.get(0).getMonitorPointName();

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList3.add(monitorPointName);
         }

         if (showLimit) {
            RowDataList3.add("/");
         }

         tableDataList.add(RowDataList3);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList4 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList4.add("燃料种类");
         }

         Long sampleId = folderReportResultList.get(0).getSampleId();
         EmcPropertyDataBean propertyData = propertyDataList.stream()
            .filter(e -> ObjectUtils.equals(sampleId, e.getBizId()))
            .findFirst()
            .orElse(new EmcPropertyDataBean());
         EmcSampleBean sample = sampleList.stream().filter(e -> ObjectUtils.equals(sampleId, e.getId())).findFirst().orElse(new EmcSampleBean());
         Long propertyTmplId = propertyData.getTmplId();
         List<EmcPropertyFieldBean> curPropertyFieldList = propertyFieldList.stream()
            .filter(e -> ObjectUtils.equals(propertyTmplId, e.getTmplId()))
            .collect(Collectors.toList());
         Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
         EmcPropertyFieldBean propertyField1 = curPropertyFieldList.stream().filter(e -> ObjectUtils.equals("燃料种类", e.getFieldName())).findFirst().orElse(null);
         String propertyFieldName1 = propertyField1 == null ? "" : propertyField1.getInternalField();
         String propertyValue1 = propertyDataMap.get(propertyFieldName1) == null ? "-" : (String)propertyDataMap.get(propertyFieldName1);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList4.add(propertyValue1);
         }

         if (showLimit) {
            RowDataList4.add("/");
         }

         tableDataList.add(RowDataList4);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         EmcPropertyFieldBean propertyField45 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("净化装置", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName45 = propertyField45 == null ? "" : propertyField45.getInternalField();
         String propertyValue45 = propertyDataMap.get(propertyFieldName45) == null ? "-" : (String)propertyDataMap.get(propertyFieldName45);
         if (!StringUtils.isBlank(propertyValue45)) {
            List<Object> RowDataList45 = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
               RowDataList45.add("净化装置");
            }

            for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
               RowDataList45.add(propertyValue45);
            }

            if (showLimit) {
               RowDataList45.add("/");
            }

            tableDataList.add(RowDataList45);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
            rowIndex.incrementAndGet();
         }

         List<Object> RowDataList5 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList5.add("排气筒高度（m）");
         }

         EmcPropertyFieldBean propertyField2 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("烟囱高度(m)", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName2 = propertyField2 == null ? "" : propertyField2.getInternalField();
         String propertyValue2 = propertyDataMap.get(propertyFieldName2) == null ? "-" : (String)propertyDataMap.get(propertyFieldName2);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList5.add(propertyValue2);
         }

         if (showLimit) {
            RowDataList5.add("/");
         }

         tableDataList.add(RowDataList5);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList6 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList6.add("测点截面积（m²）");
         }

         EmcPropertyFieldBean propertyField3 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("截面积(m²)", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName3 = propertyField3 == null ? "" : propertyField3.getInternalField();
         String propertyValue3 = propertyDataMap.get(propertyFieldName3) == null ? "-" : (String)propertyDataMap.get(propertyFieldName3);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList6.add(propertyValue3);
         }

         if (showLimit) {
            RowDataList6.add("/");
         }

         tableDataList.add(RowDataList6);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList7 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList7.add("运行负荷");
         }

         EmcPropertyFieldBean propertyField4 = curPropertyFieldList.stream().filter(e -> ObjectUtils.equals("运行负荷", e.getFieldName())).findFirst().orElse(null);
         String propertyFieldName4 = propertyField4 == null ? "" : propertyField4.getInternalField();
         String propertyValue4 = propertyDataMap.get(propertyFieldName4) == null ? "-" : (String)propertyDataMap.get(propertyFieldName4);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList7.add(propertyValue4);
         }

         if (showLimit) {
            RowDataList7.add("/");
         }

         tableDataList.add(RowDataList7);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         this.youzuzhiDescCommonResultData(
            "基准含氧量", "基准含氧量(%)", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "烟气温度", "测点废气温度（℃）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "平均流速", "测点废气平均流速（m/s）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "含湿量", "测点废气含湿量（%）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "含氧量", "测点废气含氧量（%）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "标干流量", "标态废气流量（m³/h）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = folderReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
         String finalTableType = tableType;
         int finalResultColumnNum = resultColumnNum;
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> RowDataList8 = new ArrayList<>();

               for (int i = 0; i < 2; i++) {
                  RowDataList8.add("检测频次");
               }

               if ("sampleNum".equals(finalTableType)) {
                  for (int i = 0; i < finalResultColumnNum; i++) {
                     int num = i + 1;
                     EmcReportResultBean resultBean = curReportResultList.stream()
                        .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                        .findFirst()
                        .orElse(new EmcReportResultBean());
                     RowDataList8.add(
                        "第"
                           + this.numberToChinese(i + 1)
                           + "次"
                           + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n" + resultBean.getReportResult())
                     );
                  }
               } else {
                  for (int i = 0; i < finalResultColumnNum; i++) {
                     int num = i + 1;
                     EmcReportResultBean resultBean = curReportResultList.stream()
                        .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
                        .findFirst()
                        .orElse(new EmcReportResultBean());
                     RowDataList8.add(
                        this.numberToChinese(i + 1) + "时段" + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n" + resultBean.getReportResult())
                     );
                  }
               }

               if (finalResultColumnNum > 1) {
                  RowDataList8.add("最大值");
               }

               if (showLimit) {
                  RowDataList8.add("/");
               }

               tableDataList.add(RowDataList8);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
               rowIndex.incrementAndGet();
               int itemGroupMergedRowStartIndex = rowIndex.get();
               this.youzuzhiDescCommonResultDataMax(
                  "废气实测浓度", (List<EmcReportResultBean>)curReportResultList, finalResultColumnNum, finalTableType, tableDataList, rowIndex, showLimit
               );
               this.youzuzhiDescCommonResultDataMax(
                  "废气折算浓度", (List<EmcReportResultBean>)curReportResultList, finalResultColumnNum, finalTableType, tableDataList, rowIndex, showLimit
               );
               this.youzuzhiDescCommonResultDataMax(
                  "废气排放速率", (List<EmcReportResultBean>)curReportResultList, finalResultColumnNum, finalTableType, tableDataList, rowIndex, showLimit
               );
               int itemGroupMergedRowEndIndex = rowIndex.get() - 1;
               mergedRowList.add(new MergedRow(0, itemGroupMergedRowStartIndex, itemGroupMergedRowEndIndex));
            }
         );
         List<Object> RowDataList9 = new ArrayList<>();
         RowDataList9.add("备注");
         String remark = "1、测点位置名称、燃料种类名称由受检单位提供；\n2、排气筒高度、测点截面积、废气流量、排放速率不在本公司资质认定范围内，仅供委托方参考，对社会不具有证明作用；\n3、参考";
         List<Long> orderTaskIdList = folderReportResultList.stream().map(e -> e.getOrderTaskId()).distinct().collect(Collectors.toList());
         List<Long> indexCategoryIdList = orderTaskList.stream()
            .filter(task -> orderTaskIdList.contains(task.getId()) && !ObjectUtils.isEmpty(task.getIndexCategoryId()))
            .<Long>map(EmcOrderTaskBean::getIndexCategoryId)
            .distinct()
            .collect(Collectors.toList());
         EmcIndexCategoryBean indexCategory = indexCategoryList.stream()
            .filter(e -> indexCategoryIdList.contains(e.getId()))
            .findFirst()
            .orElse(new EmcIndexCategoryBean());
         remark = remark + (StringUtils.isBlank(indexCategory.getStdName()) ? "/" : indexCategory.getStdName());

         for (long l = 0L; l < (long)(resultColumnNum + 1 + avgColumnNum); l++) {
            RowDataList9.add(remark);
         }

         if (showLimit) {
            RowDataList9.add("/");
         }

         tableDataList.add(RowDataList9);
         if (showLimit) {
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, resultColumnNum + 2 + avgColumnNum));
         } else {
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, resultColumnNum + 1 + avgColumnNum));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("附表1-" + tabelCount + " 有组织废气排气参数");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
         List<XWPFTableRow> rows = table.getRows();
         XWPFTableRow row = rows.get(rows.size() - 1);
         XWPFTableCell cell = row.getCell(1);
         ((CTP)cell.getCTTc().getPList().get(0)).addNewPPr().addNewJc().setVal(STJc.LEFT);
      }
   }

   private void youzuzhiDescOtherTable2(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, List<EmcReportResultBean> folderReportResultList, int tabelCount
   ) {
      if (!folderReportResultList.isEmpty()) {
         AtomicInteger rowIndex = new AtomicInteger(0);
         List<EmcPropertyDataBean> propertyDataList = templateSourceDTO.getPropertyDataList();
         List<EmcPropertyFieldBean> propertyFieldList = templateSourceDTO.getPropertyFieldList();
         List<EmcSampleBean> sampleList = templateSourceDTO.getSampleList();
         List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
         List<EmcIndexCategoryBean> indexCategoryList = templateSourceDTO.getIndexCategoryList();
         EmcReportBean report = templateSourceDTO.getReport();
         String showlimit = report.getExt$Item("showlimit");
         boolean showLimit = "1".equals(showlimit);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            firstRowDataList.add("项目类别");
         }

         int frequency = folderReportResultList.stream().mapToInt(e -> e.getFrequency().intValue()).max().orElse(1);
         int sampleNum = folderReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getExt$Item("samplenum"))).max().orElse(1);
         int resultColumnNum = 1;
         int avgColumnNum = 1;
         String tableType = "frequency";
         if (frequency > 1) {
            tableType = "frequency";
            resultColumnNum = frequency;
         } else {
            tableType = "sampleNum";
            resultColumnNum = sampleNum;
         }

         if (resultColumnNum == 1) {
            avgColumnNum = 0;
         }

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            firstRowDataList.add("项目参数");
         }

         if (showLimit) {
            firstRowDataList.add("标准限值");
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            secondRowDataList.add("");
         }

         LocalDate planSampleDate = folderReportResultList.get(0).getSampleBeginDate();

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            secondRowDataList.add("采样日期：" + planSampleDate);
         }

         if (showLimit) {
            secondRowDataList.add("");
         }

         tableDataList.add(secondRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, avgColumnNum + 1 + resultColumnNum));
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         if (showLimit) {
            mergedRowList.add(new MergedRow(resultColumnNum + avgColumnNum + 2, 0, 1));
         } else {
            mergedRowList.add(new MergedRow(resultColumnNum + avgColumnNum + 1, 0, 1));
         }

         rowIndex.incrementAndGet();
         List<Object> RowDataList3 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList3.add("测点位置");
         }

         String monitorPointName = folderReportResultList.get(0).getMonitorPointName();

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList3.add(monitorPointName);
         }

         if (showLimit) {
            RowDataList3.add("/");
         }

         tableDataList.add(RowDataList3);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, avgColumnNum + 1 + resultColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList4 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList4.add("燃料种类");
         }

         Long sampleId = folderReportResultList.get(0).getSampleId();
         EmcPropertyDataBean propertyData = propertyDataList.stream()
            .filter(e -> ObjectUtils.equals(sampleId, e.getBizId()))
            .findFirst()
            .orElse(new EmcPropertyDataBean());
         EmcSampleBean sample = sampleList.stream().filter(e -> ObjectUtils.equals(sampleId, e.getId())).findFirst().orElse(new EmcSampleBean());
         Long propertyTmplId = propertyData.getTmplId();
         List<EmcPropertyFieldBean> curPropertyFieldList = propertyFieldList.stream()
            .filter(e -> ObjectUtils.equals(propertyTmplId, e.getTmplId()))
            .collect(Collectors.toList());
         Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
         EmcPropertyFieldBean propertyField1 = curPropertyFieldList.stream().filter(e -> ObjectUtils.equals("燃料种类", e.getFieldName())).findFirst().orElse(null);
         String propertyFieldName1 = propertyField1 == null ? "" : propertyField1.getInternalField();
         String propertyValue1 = propertyDataMap.get(propertyFieldName1) == null ? "-" : (String)propertyDataMap.get(propertyFieldName1);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList4.add(propertyValue1);
         }

         if (showLimit) {
            RowDataList4.add("/");
         }

         tableDataList.add(RowDataList4);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, avgColumnNum + 1 + resultColumnNum));
         rowIndex.incrementAndGet();
         EmcPropertyFieldBean propertyField45 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("净化装置", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName45 = propertyField45 == null ? "" : propertyField45.getInternalField();
         String propertyValue45 = propertyDataMap.get(propertyFieldName45) == null ? "-" : (String)propertyDataMap.get(propertyFieldName45);
         if (!StringUtils.isBlank(propertyValue45)) {
            List<Object> RowDataList45 = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
               RowDataList45.add("净化装置");
            }

            for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
               RowDataList45.add(propertyValue45);
            }

            if (showLimit) {
               RowDataList45.add("/");
            }

            tableDataList.add(RowDataList45);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, avgColumnNum + 1 + resultColumnNum));
            rowIndex.incrementAndGet();
         }

         List<Object> RowDataList5 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList5.add("排气筒高度（m）");
         }

         EmcPropertyFieldBean propertyField2 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("烟囱高度(m)", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName2 = propertyField2 == null ? "" : propertyField2.getInternalField();
         String propertyValue2 = propertyDataMap.get(propertyFieldName2) == null ? "-" : (String)propertyDataMap.get(propertyFieldName2);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList5.add(propertyValue2);
         }

         if (showLimit) {
            RowDataList5.add("/");
         }

         tableDataList.add(RowDataList5);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, avgColumnNum + 1 + resultColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList6 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList6.add("测点截面积（m²）");
         }

         EmcPropertyFieldBean propertyField3 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("截面积(m²)", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName3 = propertyField3 == null ? "" : propertyField3.getInternalField();
         String propertyValue3 = propertyDataMap.get(propertyFieldName3) == null ? "-" : (String)propertyDataMap.get(propertyFieldName3);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList6.add(propertyValue3);
         }

         if (showLimit) {
            RowDataList6.add("/");
         }

         tableDataList.add(RowDataList6);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, avgColumnNum + 1 + resultColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList7 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList7.add("运行负荷");
         }

         EmcPropertyFieldBean propertyField4 = curPropertyFieldList.stream().filter(e -> ObjectUtils.equals("运行负荷", e.getFieldName())).findFirst().orElse(null);
         String propertyFieldName4 = propertyField4 == null ? "" : propertyField4.getInternalField();
         String propertyValue4 = propertyDataMap.get(propertyFieldName4) == null ? "-" : (String)propertyDataMap.get(propertyFieldName4);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList7.add(propertyValue4);
         }

         if (showLimit) {
            RowDataList7.add("/");
         }

         tableDataList.add(RowDataList7);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, avgColumnNum + 1 + resultColumnNum));
         rowIndex.incrementAndGet();
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = folderReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
         String finalTableType = tableType;
         int finalResultColumnNum = resultColumnNum;
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> RowDataList8 = new ArrayList<>();

               for (int i = 0; i < 2; i++) {
                  RowDataList8.add("检测频次");
               }

               if ("sampleNum".equals(finalTableType)) {
                  for (int i = 0; i < finalResultColumnNum; i++) {
                     int num = i + 1;
                     EmcReportResultBean resultBean = curReportResultList.stream()
                        .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                        .findFirst()
                        .orElse(new EmcReportResultBean());
                     RowDataList8.add(
                        "第"
                           + this.numberToChinese(i + 1)
                           + "次"
                           + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n" + resultBean.getReportResult())
                     );
                  }
               } else {
                  for (int i = 0; i < finalResultColumnNum; i++) {
                     int num = i + 1;
                     EmcReportResultBean resultBean = curReportResultList.stream()
                        .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
                        .findFirst()
                        .orElse(null);
                     RowDataList8.add(
                        this.numberToChinese(i + 1) + "时段" + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n" + resultBean.getReportResult())
                     );
                  }
               }

               if (finalResultColumnNum > 1) {
                  RowDataList8.add("平均值");
               }

               if (showLimit) {
                  RowDataList8.add("/");
               }

               tableDataList.add(RowDataList8);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
               rowIndex.incrementAndGet();
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
               this.youzuzhiDescCommonResultData2(
                  "样品浓度", (List<EmcReportResultBean>)curReportResultList, finalResultColumnNum, finalTableType, tableDataList, rowIndex, showLimit
               );
            }
         );
         List<Object> RowDataList9 = new ArrayList<>();
         RowDataList9.add("备注");
         String remark = "1、测点位置名称、燃料种类名称由受检单位提供；\n2、排气筒高度、测点截面积、废气流量、排放速率不在本公司资质认定范围内，仅供委托方参考，对社会不具有证明作用；\n3、参考";
         List<Long> orderTaskIdList = folderReportResultList.stream().map(e -> e.getOrderTaskId()).distinct().collect(Collectors.toList());
         List<Long> indexCategoryIdList = orderTaskList.stream()
            .filter(task -> orderTaskIdList.contains(task.getId()) && !ObjectUtils.isEmpty(task.getIndexCategoryId()))
            .<Long>map(EmcOrderTaskBean::getIndexCategoryId)
            .distinct()
            .collect(Collectors.toList());
         EmcIndexCategoryBean indexCategory = indexCategoryList.stream()
            .filter(e -> indexCategoryIdList.contains(e.getId()))
            .findFirst()
            .orElse(new EmcIndexCategoryBean());
         remark = remark + (StringUtils.isBlank(indexCategory.getStdName()) ? "/" : indexCategory.getStdName());

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum + 1); l++) {
            RowDataList9.add(remark);
         }

         if (showLimit) {
            RowDataList9.add("/");
         }

         tableDataList.add(RowDataList9);
         if (showLimit) {
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, resultColumnNum + avgColumnNum + 2));
         } else {
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, resultColumnNum + avgColumnNum + 1));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("附表1-" + tabelCount + " 有组织废气排气参数");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
         List<XWPFTableRow> rows = table.getRows();
         XWPFTableRow row = rows.get(rows.size() - 1);
         XWPFTableCell cell = row.getCell(1);
         ((CTP)cell.getCTTc().getPList().get(0)).addNewPPr().addNewJc().setVal(STJc.LEFT);
      }
   }

   private void youzuzhiDescCommonTable(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, List<EmcReportResultBean> folderReportResultList, int tabelCount
   ) {
      if (!folderReportResultList.isEmpty()) {
         AtomicInteger rowIndex = new AtomicInteger(0);
         List<EmcPropertyDataBean> propertyDataList = templateSourceDTO.getPropertyDataList();
         List<EmcPropertyFieldBean> propertyFieldList = templateSourceDTO.getPropertyFieldList();
         List<EmcSampleBean> sampleList = templateSourceDTO.getSampleList();
         List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
         List<EmcIndexCategoryBean> indexCategoryList = templateSourceDTO.getIndexCategoryList();
         EmcReportBean report = templateSourceDTO.getReport();
         String showlimit = report.getExt$Item("showlimit");
         boolean showLimit = "1".equals(showlimit);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<MergedColumn> mergedColumnList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            firstRowDataList.add("项目类别");
         }

         int frequency = folderReportResultList.stream().mapToInt(e -> e.getFrequency().intValue()).max().orElse(1);
         int sampleNum = folderReportResultList.stream().mapToInt(e -> NumberUtils.parseInt(e.getExt$Item("samplenum"))).max().orElse(1);
         int resultColumnNum = 1;
         int avgColumnNum = 1;
         String tableType = "frequency";
         if (frequency > 1) {
            tableType = "frequency";
            resultColumnNum = frequency;
         } else {
            tableType = "sampleNum";
            resultColumnNum = sampleNum;
         }

         if (resultColumnNum == 1) {
            avgColumnNum = 0;
         }

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            firstRowDataList.add("项目参数");
         }

         if (showLimit) {
            firstRowDataList.add("标准限值");
         }

         tableDataList.add(firstRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> secondRowDataList = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            secondRowDataList.add("");
         }

         LocalDate planSampleDate = folderReportResultList.get(0).getSampleBeginDate();
         String formatDate = planSampleDate.format(this.sdf);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            secondRowDataList.add("采样日期：" + formatDate);
         }

         if (showLimit) {
            secondRowDataList.add("");
         }

         tableDataList.add(secondRowDataList);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         mergedRowList.add(new MergedRow(0, 0, 1));
         mergedRowList.add(new MergedRow(1, 0, 1));
         if (showLimit) {
            mergedRowList.add(new MergedRow(resultColumnNum + 2 + avgColumnNum, 0, 1));
         } else {
            mergedRowList.add(new MergedRow(resultColumnNum + 1 + avgColumnNum, 0, 1));
         }

         rowIndex.incrementAndGet();
         List<Object> RowDataList3 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList3.add("测点位置");
         }

         String monitorPointName = folderReportResultList.get(0).getMonitorPointName();

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList3.add(monitorPointName);
         }

         if (showLimit) {
            RowDataList3.add("/");
         }

         tableDataList.add(RowDataList3);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList4 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList4.add("燃料种类");
         }

         Long sampleId = folderReportResultList.get(0).getSampleId();
         EmcPropertyDataBean propertyData = propertyDataList.stream()
            .filter(e -> ObjectUtils.equals(sampleId, e.getBizId()))
            .findFirst()
            .orElse(new EmcPropertyDataBean());
         EmcSampleBean sample = sampleList.stream().filter(e -> ObjectUtils.equals(sampleId, e.getId())).findFirst().orElse(new EmcSampleBean());
         Long propertyTmplId = propertyData.getTmplId();
         List<EmcPropertyFieldBean> curPropertyFieldList = propertyFieldList.stream()
            .filter(e -> ObjectUtils.equals(propertyTmplId, e.getTmplId()))
            .collect(Collectors.toList());
         Map<String, Object> propertyDataMap = BeanUtils.toMap(propertyData);
         EmcPropertyFieldBean propertyField1 = curPropertyFieldList.stream().filter(e -> ObjectUtils.equals("燃料种类", e.getFieldName())).findFirst().orElse(null);
         String propertyFieldName1 = propertyField1 == null ? "" : propertyField1.getInternalField();
         String propertyValue1 = propertyDataMap.get(propertyFieldName1) == null ? "-" : (String)propertyDataMap.get(propertyFieldName1);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList4.add(propertyValue1);
         }

         if (showLimit) {
            RowDataList4.add("/");
         }

         tableDataList.add(RowDataList4);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         EmcPropertyFieldBean propertyField45 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("净化装置", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName45 = propertyField45 == null ? "" : propertyField45.getInternalField();
         String propertyValue45 = propertyDataMap.get(propertyFieldName45) == null ? "-" : (String)propertyDataMap.get(propertyFieldName45);
         if (!StringUtils.isBlank(propertyValue45)) {
            List<Object> RowDataList45 = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
               RowDataList45.add("净化装置");
            }

            for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
               RowDataList45.add(propertyValue45);
            }

            if (showLimit) {
               RowDataList45.add("/");
            }

            tableDataList.add(RowDataList45);
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
            rowIndex.incrementAndGet();
         }

         List<Object> RowDataList5 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList5.add("排气筒高度（m）");
         }

         EmcPropertyFieldBean propertyField2 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("烟囱高度(m)", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName2 = propertyField2 == null ? "" : propertyField2.getInternalField();
         String propertyValue2 = propertyDataMap.get(propertyFieldName2) == null ? "-" : (String)propertyDataMap.get(propertyFieldName2);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList5.add(propertyValue2);
         }

         if (showLimit) {
            RowDataList5.add("/");
         }

         tableDataList.add(RowDataList5);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList6 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList6.add("测点截面积（m²）");
         }

         EmcPropertyFieldBean propertyField3 = curPropertyFieldList.stream()
            .filter(e -> ObjectUtils.equals("截面积(m²)", e.getFieldName()))
            .findFirst()
            .orElse(null);
         String propertyFieldName3 = propertyField3 == null ? "" : propertyField3.getInternalField();
         String propertyValue3 = propertyDataMap.get(propertyFieldName3) == null ? "-" : (String)propertyDataMap.get(propertyFieldName3);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList6.add(propertyValue3);
         }

         if (showLimit) {
            RowDataList6.add("/");
         }

         tableDataList.add(RowDataList6);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         List<Object> RowDataList7 = new ArrayList<>();

         for (int i = 0; i < 2; i++) {
            RowDataList7.add("运行负荷");
         }

         EmcPropertyFieldBean propertyField4 = curPropertyFieldList.stream().filter(e -> ObjectUtils.equals("运行负荷", e.getFieldName())).findFirst().orElse(null);
         String propertyFieldName4 = propertyField4 == null ? "" : propertyField4.getInternalField();
         String propertyValue4 = propertyDataMap.get(propertyFieldName4) == null ? "-" : (String)propertyDataMap.get(propertyFieldName4);

         for (long l = 0L; l < (long)(resultColumnNum + avgColumnNum); l++) {
            RowDataList7.add(propertyValue4);
         }

         if (showLimit) {
            RowDataList7.add("/");
         }

         tableDataList.add(RowDataList7);
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
         mergedColumnList.add(new MergedColumn(rowIndex.get(), 2, resultColumnNum + 1 + avgColumnNum));
         rowIndex.incrementAndGet();
         this.youzuzhiDescCommonResultData(
            "基准含氧量", "基准含氧量(%)", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "烟气温度", "测点废气温度（℃）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "平均流速", "测点废气平均流速（m/s）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "含湿量", "测点废气含湿量（%）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "含氧量", "测点废气含氧量（%）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         this.youzuzhiDescCommonResultData(
            "标干流量", "标态废气流量（m³/h）", folderReportResultList, resultColumnNum, tableType, tableDataList, rowIndex, mergedColumnList, showLimit
         );
         Map<String, List<EmcReportResultBean>> itemGroupReportResultListMap = folderReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("itemGroup")));
         String finalTableType = tableType;
         int finalResultColumnNum = resultColumnNum;
         itemGroupReportResultListMap.forEach(
            (key, curReportResultList) -> {
               List<Object> RowDataList8 = new ArrayList<>();

               for (int i = 0; i < 2; i++) {
                  RowDataList8.add("检测频次");
               }

               if ("sampleNum".equals(finalTableType)) {
                  for (int i = 0; i < finalResultColumnNum; i++) {
                     int num = i + 1;
                     EmcReportResultBean resultBean = curReportResultList.stream()
                        .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getExt$Item("samplenum"), num + ""))
                        .findFirst()
                        .orElse(new EmcReportResultBean());
                     RowDataList8.add(
                        "第"
                           + this.numberToChinese(i + 1)
                           + "次"
                           + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n" + resultBean.getReportResult())
                     );
                  }
               } else {
                  for (int i = 0; i < finalResultColumnNum; i++) {
                     int num = i + 1;
                     EmcReportResultBean resultBean = curReportResultList.stream()
                        .filter(e -> ObjectUtils.equals(e.getItemName(), "采样开始时间") && ObjectUtils.equals(e.getFrequency(), NumberUtils.parseLong(num)))
                        .findFirst()
                        .orElse(new EmcReportResultBean());
                     RowDataList8.add(
                        this.numberToChinese(i + 1) + "时段" + (StringUtils.isBlank(resultBean.getReportResult()) ? "" : "\n" + resultBean.getReportResult())
                     );
                  }
               }

               if (finalResultColumnNum > 1) {
                  RowDataList8.add("平均值");
               }

               if (showLimit) {
                  RowDataList8.add("/");
               }

               tableDataList.add(RowDataList8);
               mergedColumnList.add(new MergedColumn(rowIndex.get(), 0, 1));
               rowIndex.incrementAndGet();
               int itemGroupMergedRowStartIndex = rowIndex.get();
               this.youzuzhiDescCommonResultData2(
                  "废气实测浓度", (List<EmcReportResultBean>)curReportResultList, finalResultColumnNum, finalTableType, tableDataList, rowIndex, showLimit
               );
               this.youzuzhiDescCommonResultData2(
                  "废气折算浓度", (List<EmcReportResultBean>)curReportResultList, finalResultColumnNum, finalTableType, tableDataList, rowIndex, showLimit
               );
               this.youzuzhiDescCommonResultData2(
                  "废气排放速率", (List<EmcReportResultBean>)curReportResultList, finalResultColumnNum, finalTableType, tableDataList, rowIndex, showLimit
               );
               int itemGroupMergedRowEndIndex = rowIndex.get() - 1;
               mergedRowList.add(new MergedRow(0, itemGroupMergedRowStartIndex, itemGroupMergedRowEndIndex));
            }
         );
         List<Object> RowDataList9 = new ArrayList<>();
         RowDataList9.add("备注");
         String remark = "1、测点位置名称、燃料种类名称由受检单位提供；\n2、排气筒高度、测点截面积、废气流量、排放速率不在本公司资质认定范围内，仅供委托方参考，对社会不具有证明作用；\n3、参考";
         List<Long> orderTaskIdList = folderReportResultList.stream().map(e -> e.getOrderTaskId()).distinct().collect(Collectors.toList());
         List<Long> indexCategoryIdList = orderTaskList.stream()
            .filter(task -> orderTaskIdList.contains(task.getId()) && !ObjectUtils.isEmpty(task.getIndexCategoryId()))
            .<Long>map(EmcOrderTaskBean::getIndexCategoryId)
            .distinct()
            .collect(Collectors.toList());
         EmcIndexCategoryBean indexCategory = indexCategoryList.stream()
            .filter(e -> indexCategoryIdList.contains(e.getId()))
            .findFirst()
            .orElse(new EmcIndexCategoryBean());
         remark = remark + (StringUtils.isBlank(indexCategory.getStdName()) ? "/" : indexCategory.getStdName());

         for (long l = 0L; l < (long)(resultColumnNum + 1 + avgColumnNum); l++) {
            RowDataList9.add(remark);
         }

         if (showLimit) {
            RowDataList9.add("/");
         }

         tableDataList.add(RowDataList9);
         if (showLimit) {
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, resultColumnNum + 2 + avgColumnNum));
         } else {
            mergedColumnList.add(new MergedColumn(rowIndex.get(), 1, resultColumnNum + 1 + avgColumnNum));
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("附表1-" + tabelCount + " 有组织废气排气参数");
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setMergedColumnList(mergedColumnList);
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
         List<XWPFTableRow> rows = table.getRows();
         XWPFTableRow row = rows.get(rows.size() - 1);
         XWPFTableCell cell = row.getCell(1);
         ((CTP)cell.getCTTc().getPList().get(0)).addNewPPr().addNewJc().setVal(STJc.LEFT);
      }
   }

   private int qcResultTable2(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, List<EmcOrderTaskResultBean> orderTaskResultList, String orderCategory, int tableIndex
   ) {
      List<String> bzyCodeList = Arrays.asList("QC-STD", "PQC-STD");
      Map<Long, List<EmcOrderTaskResultBean>> taskReportResultListMap = orderTaskResultList.stream().collect(Collectors.groupingBy(e -> e.getTestMethodId()));
      List<EmcOrderTaskResultBean> reportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && bzyCodeList.contains(r.getQcCategoryCode())
                  && !"1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> repeatReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && r.getExt$Item("itemGroup") != null
                  && "1".equals(r.getReportDisplay())
                  && bzyCodeList.contains(r.getQcCategoryCode())
                  && "1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      int index = 1;
      if (!CollectionUtils.isEmpty(reportResultList)) {
         reportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("质控样编号");
         firstRowDataList.add("测试浓度");
         firstRowDataList.add("质控样范围");
         firstRowDataList.add("评定结果");
         tableDataList.add(firstRowDataList);
         reportResultList.stream()
            .forEach(
               s -> {
                  List<Object> rowDataList = new ArrayList<>();
                  rowDataList.add(s.getExt$Item("reportTestName") + "(" + s.getUnitName() + ")");
                  String stockbatchno = StringUtils.isBlank(s.getExt$Item("stockbatchno")) ? s.getRn13() : s.getExt$Item("stockbatchno");
                  rowDataList.add(stockbatchno);
                  rowDataList.add(this.transScientificCounting(s.getReportResult()));
                  List<EmcOrderTaskResultBean> taskResults = taskReportResultListMap.get(s.getTestMethodId())
                     .stream()
                     .filter(t -> s.getOrderNo().equals(t.getOrderNo()) && !StringUtils.isEmpty(t.getStandQty()) && !StringUtils.isEmpty(t.getRn16()))
                     .collect(Collectors.toList());
                  if (!CollectionUtils.isEmpty(taskResults)) {
                     rowDataList.add(taskResults.get(0).getStandQty() + "±" + taskResults.get(0).getRn16());
                  } else {
                     rowDataList.add("");
                  }

                  if ("Done".equals(s.getResultStatus())) {
                     rowDataList.add("合格");
                  } else {
                     rowDataList.add("不合格");
                  }

                  if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
                     tableDataList.add(rowDataList);
                  }
               }
            );
         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         if (!CollectionUtils.isEmpty(repeatReportResultList)) {
            wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "标准样品质量控制情况表");
         } else {
            wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + " " + orderCategory + "标准样品质量控制情况表");
         }

         index++;
         XWPFTable table2 = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
         table2.getRow(0).setRepeatHeader(true);
      }

      if (!CollectionUtils.isEmpty(repeatReportResultList)) {
         Map<String, List<EmcOrderTaskResultBean>> itemGroupReportResultListMap = repeatReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : repeatReportResultList.stream()
            .filter(e -> e.getExt$Item("reportTestName") != null)
            .map(e -> e.getExt$Item("reportTestName"))
            .distinct()
            .collect(Collectors.toList())) {
            List<List<Object>> tableDataList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();
            firstRowDataList.add("检测项目");
            firstRowDataList.add("质控样编号");
            firstRowDataList.add("测试浓度");
            firstRowDataList.add("质控样范围");
            firstRowDataList.add("评定结果");
            tableDataList.add(firstRowDataList);
            List<EmcOrderTaskResultBean> taskResultBeans = itemGroupReportResultListMap.get(testName)
               .stream()
               .filter(r -> "1".equals(r.getExt$Item("multipleitemgroup")))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskResultBeans)) {
               taskResultBeans.stream()
                  .forEach(
                     s -> {
                        List<Object> rowDataList = new ArrayList<>();
                        rowDataList.add(s.getExt$Item("itemGroup") + "(" + s.getUnitName() + ")");
                        rowDataList.add(s.getExt$Item("stockbatchno"));
                        rowDataList.add(this.transScientificCounting(s.getReportResult()));
                        List<EmcOrderTaskResultBean> taskResults = taskReportResultListMap.get(s.getTestMethodId())
                           .stream()
                           .filter(t -> !StringUtils.isEmpty(t.getStandQty()) && !StringUtils.isEmpty(t.getRn16()))
                           .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(taskResults)) {
                           rowDataList.add(taskResults.get(0).getStandQty() + "±" + taskResults.get(0).getRn16());
                        } else {
                           rowDataList.add("");
                        }

                        if ("Done".equals(s.getResultStatus())) {
                           rowDataList.add("合格");
                        } else {
                           rowDataList.add("不合格");
                        }

                        if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
                           tableDataList.add(rowDataList);
                        }
                     }
                  );
               this.listListNullToString(tableDataList, "/");
               WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
               wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
               wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
               wordTemplateDynamicTableDTO2.setCellHeight(400);
               wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + ++index + " " + orderCategory + testName + "标准样品质量控制情况表");
               XWPFTable table2 = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
               table2.getRow(0).setRepeatHeader(true);
            }
         }
      }

      if (!CollectionUtils.isEmpty(reportResultList) || !CollectionUtils.isEmpty(repeatReportResultList)) {
         tableIndex++;
      }

      return tableIndex;
   }

   private int qcResultTable3(
      XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, List<EmcOrderTaskResultBean> orderTaskResultList, String orderCategory, int tableIndex
   ) {
      List<String> kbCodeList = Arrays.asList("P-BLANK", "P-YSKB", "L-BLANK", "L-CZKB", "L-SJKB");
      List<EmcOrderTaskResultBean> reportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && kbCodeList.contains(r.getQcCategoryCode())
                  && !"1".equals(r.getExt$Item("multipleitemgroup"))
                  && !"192".equals(r.getTestId() + "")
         )
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> dioxinReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && kbCodeList.contains(r.getQcCategoryCode())
                  && "192".equals(r.getTestId() + "")
                  && StringUtils.contains(r.getItemName(), "组分")
         )
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> repeatReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && kbCodeList.contains(r.getQcCategoryCode())
                  && "1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      int index = 1;
      if (!CollectionUtils.isEmpty(reportResultList)) {
         reportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         AtomicInteger rowIndex = new AtomicInteger(1);
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("空白样编号");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);

         for (String testName : reportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            int mergedRowStartIndex = rowIndex.get();

            for (EmcOrderTaskResultBean res : reportResultList.stream()
               .filter(e -> StringUtils.equals(testName, e.getExt$Item("reportTestName")))
               .collect(Collectors.toList())) {
               List<Object> testNameRowDataList = new ArrayList<>();
               String reportTestName = res.getExt$Item("reportTestName");
               if (!StringUtils.isBlank(res.getUnitName())) {
                  reportTestName = reportTestName + "(" + res.getUnitName() + ")";
               }

               testNameRowDataList.add(reportTestName);
               testNameRowDataList.add(res.getOrderNo());
               testNameRowDataList.add(this.transScientificCounting(res.getReportResult()));
               tableDataList.add(testNameRowDataList);
               rowIndex.incrementAndGet();
            }

            int var55 = rowIndex.get() - 1;
         }

         List<MergedRow> mergedRowList = new ArrayList<>();

         for (int columnIndex = 0; columnIndex < 1; columnIndex++) {
            int biginIndex = 1;

            for (int idx = 2; idx <= tableDataList.size(); idx++) {
               if (idx == tableDataList.size() || !ObjectUtils.equals(tableDataList.get(idx).get(columnIndex), tableDataList.get(idx - 1).get(columnIndex))) {
                  mergedRowList.add(new MergedRow(columnIndex, biginIndex, idx - 1));
                  biginIndex = idx;
               }
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "空白样品质量控制情况表");
         index++;
         XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         equiptTable.getRow(0).setRepeatHeader(true);
      }

      if (!CollectionUtils.isEmpty(dioxinReportResultList)) {
         dioxinReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         AtomicInteger rowIndex = new AtomicInteger(1);
         List<MergedRow> mergedRowList = new ArrayList<>();
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("空白样编号");
         firstRowDataList.add("检测结果");
         tableDataList.add(firstRowDataList);

         for (String testName : dioxinReportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            int mergedRowStartIndex = rowIndex.get();

            for (EmcOrderTaskResultBean res : dioxinReportResultList.stream()
               .filter(e -> StringUtils.equals(testName, e.getExt$Item("reportTestName")))
               .collect(Collectors.toList())) {
               List<Object> testNameRowDataList = new ArrayList<>();
               String reportTestName = res.getItemAliasName().replace("直读", "");
               if (!StringUtils.isBlank(res.getUnitName())) {
                  reportTestName = reportTestName + "(" + res.getUnitName() + ")";
               }

               testNameRowDataList.add(reportTestName);
               testNameRowDataList.add(res.getOrderNo());
               testNameRowDataList.add(this.transScientificCounting(res.getReportResult()));
               tableDataList.add(testNameRowDataList);
               rowIndex.incrementAndGet();
            }

            int var59 = rowIndex.get() - 1;
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "二噁英空白样品质量控制情况表");
         index++;
         XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
         equiptTable.getRow(0).setRepeatHeader(true);
      }

      if (!CollectionUtils.isEmpty(repeatReportResultList)) {
         Map<String, List<EmcOrderTaskResultBean>> itemGroupReportResultListMap = repeatReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : repeatReportResultList.stream().map(e -> e.getExt$Item("reportTestName")).distinct().collect(Collectors.toList())) {
            List<EmcOrderTaskResultBean> testNameResultList2 = itemGroupReportResultListMap.get(testName)
               .stream()
               .filter(r -> "1".equals(r.getExt$Item("multipleitemgroup")))
               .collect(Collectors.toList());
            AtomicInteger rowIndex = new AtomicInteger(1);
            List<MergedRow> mergedRowList = new ArrayList<>();
            List<List<Object>> tableDataList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();
            firstRowDataList.add("检测项目");
            firstRowDataList.add("空白样编号");
            firstRowDataList.add("检测结果");
            tableDataList.add(firstRowDataList);

            for (String groupName : testNameResultList2.stream().map(e -> e.getExt$Item("itemGroup")).distinct().collect(Collectors.toList())) {
               int mergedRowStartIndex = rowIndex.get();
               List<EmcOrderTaskResultBean> testNameResultList = testNameResultList2.stream()
                  .filter(e -> StringUtils.equals(groupName, e.getExt$Item("itemGroup")))
                  .collect(Collectors.toList());

               for (Long o : testNameResultList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList())) {
                  List<EmcOrderTaskResultBean> curResultList = testNameResultList.stream()
                     .filter(e -> ObjectUtils.equals(o, e.getOrderId()))
                     .collect(Collectors.toList());
                  EmcOrderTaskResultBean res = curResultList.stream().filter(e -> e.getLowLimit() != null).findFirst().orElse(curResultList.get(0));
                  List<Object> testNameRowDataList = new ArrayList<>();
                  testNameRowDataList.add(res.getExt$Item("itemGroup") + "(" + res.getUnitName() + ")");
                  testNameRowDataList.add(res.getOrderNo());
                  testNameRowDataList.add(this.transScientificCounting(res.getReportResult()));
                  tableDataList.add(testNameRowDataList);
                  rowIndex.incrementAndGet();
               }

               int mergedRowEndIndex = rowIndex.get() - 1;
               mergedRowList.add(new MergedRow(0, mergedRowStartIndex, mergedRowEndIndex));
            }

            this.listListNullToString(tableDataList, "/");
            WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
            wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
            wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
            wordTemplateDynamicTableDTO.setCellHeight(400);
            wordTemplateDynamicTableDTO.setMergedRowList(mergedRowList);
            wordTemplateDynamicTableDTO.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + testName + "空白样品质量控制情况表");
            index++;
            XWPFTable equiptTable = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
            equiptTable.getRow(0).setRepeatHeader(true);
         }
      }

      if (!CollectionUtils.isEmpty(reportResultList) || !CollectionUtils.isEmpty(repeatReportResultList)) {
         tableIndex++;
      }

      return tableIndex;
   }

   private int qcResultTable4(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      List<EmcOrderTaskResultBean> orderTaskResultList,
      String orderCategory,
      int tableIndex,
      boolean isSend
   ) {
      AtomicInteger type = new AtomicInteger(7);
      List<EmcItemQcIndexBean> itemQcIndexList = templateSourceDTO.getItemQcIndexList();
      Map<Long, List<EmcItemQcIndexBean>> indexListMap = itemQcIndexList.stream().collect(Collectors.groupingBy(e -> e.getItemId()));
      List<String> pxCodeList = Arrays.asList("L-DUP");
      Long qcCategoryId = ((EmcQcCategoryBean)this.qcCategoryService.getDao().selectListByOneColumnValue("L-DUP", "QCCATEGORYID", new Order[0]).get(0)).getId();
      int index = 1;
      List<EmcOrderTaskResultBean> reportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && pxCodeList.contains(r.getQcCategoryCode())
                  && !"1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> repeatReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && pxCodeList.contains(r.getQcCategoryCode())
                  && "1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(reportResultList)) {
         List<List<Object>> tableDataList = new ArrayList<>();
         reportResultList.stream()
            .forEach(
               s -> {
                  List<Object> rowDataList = new ArrayList<>();
                  EmcOrderTaskResultBean task1 = orderTaskResultList.stream()
                     .filter(o -> o.getOrderNo().equals(s.getParentOrderNo()))
                     .collect(Collectors.toList())
                     .get(0);
                  rowDataList.add(s.getExt$Item("reportTestName"));
                  if (!isSend) {
                     if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
                        rowDataList.add(task1.getExt$Item("monitorpointname") + "(" + task1.getExt$Item("samplingdepth") + "m)");
                     } else {
                        rowDataList.add(task1.getExt$Item("monitorpointname"));
                     }
                  } else {
                     rowDataList.add(s.getExt$Item("clientordername"));
                  }

                  EmcOrderTaskResultBean task = reportResultList.stream()
                     .filter(
                        o -> o.getTestMethodId().equals(s.getTestMethodId())
                              && o.getOrderId().equals(s.getOrderId())
                              && o.getRn4() != null
                              && pxCodeList.contains(o.getQcCategoryCode())
                              && !StringUtils.isEmpty(o.getIndexHighLimit())
                              && !StringUtils.isEmpty(o.getIndexLowLimit())
                     )
                     .findFirst()
                     .orElse(null);
                  if (task == null) {
                     task = orderTaskResultList.stream()
                        .filter(
                           o -> o.getTestMethodId().equals(s.getTestMethodId())
                                 && o.getOrderId().equals(s.getOrderId())
                                 && o.getRn4() != null
                                 && pxCodeList.contains(o.getQcCategoryCode())
                                 && !StringUtils.isEmpty(o.getIndexHighLimit())
                                 && !StringUtils.isEmpty(o.getIndexLowLimit())
                        )
                        .findFirst()
                        .orElse(new EmcOrderTaskResultBean());
                  }

                  rowDataList.add(task.getUnitName());
                  rowDataList.add(this.transScientificCounting(StringUtils.isBlank(s.getRn4()) ? task.getRn4() : s.getRn4()));
                  rowDataList.add(this.transScientificCounting(s.getReportResult()));
                  String res = task.getRn7();
                  String qctype = "相对偏差(%)";
                  if (!CollectionUtils.isEmpty(indexListMap.get(s.getItemId()))) {
                     List<EmcItemQcIndexBean> emcItemQcIndexBeans = indexListMap.get(s.getItemId())
                        .stream()
                        .filter(idxx -> qcCategoryId.equals(idxx.getQcCategoryId()))
                        .collect(Collectors.toList());
                     if (!CollectionUtils.isEmpty(emcItemQcIndexBeans)) {
                        if ("RN7".equals(emcItemQcIndexBeans.get(0).getDecisionCategory())) {
                           res = task.getRn7();
                           qctype = "相对偏差(%)";
                        } else if ("RN8".equals(emcItemQcIndexBeans.get(0).getDecisionCategory())) {
                           res = task.getRn8();
                           type.set(8);
                           qctype = "绝对相差";
                        } else if ("RN9".equals(emcItemQcIndexBeans.get(0).getDecisionCategory())) {
                           res = task.getRn9();
                           type.set(9);
                           qctype = "相对相差(%)";
                        }
                     }
                  }

                  rowDataList.add(this.transScientificCounting(res));
                  rowDataList.add(task.getIndexLowLimit() + "~" + task.getIndexHighLimit());
                  rowDataList.add(qctype);
                  if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
                     tableDataList.add(rowDataList);
                  }
               }
            );
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("点位/样品名称");
         firstRowDataList.add("单位");
         firstRowDataList.add("室内平行样结果");
         firstRowDataList.add("");
         firstRowDataList.add("质控结果");
         firstRowDataList.add("质控范围");
         firstRowDataList.add("质控类型");
         List<Object> secondRowDataList = new ArrayList<>();
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("样品值");
         secondRowDataList.add("样品值-sp");
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("");
         tableDataList.add(0, firstRowDataList);
         tableDataList.add(1, secondRowDataList);
         List<MergedRow> mergedRowList = new ArrayList<>();

         for (int columnIndex = 0; columnIndex < 8; columnIndex++) {
            if (columnIndex == 0 || columnIndex == 6) {
               int biginIndex = 1;

               for (int idx = 2; idx <= tableDataList.size(); idx++) {
                  if (idx == tableDataList.size() || !ObjectUtils.equals(tableDataList.get(idx).get(columnIndex), tableDataList.get(idx - 1).get(columnIndex))) {
                     mergedRowList.add(new MergedRow(columnIndex, biginIndex, idx - 1));
                     biginIndex = idx;
                  }
               }
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         wordTemplateDynamicTableDTO2.setMergedRowList(mergedRowList);
         if (!CollectionUtils.isEmpty(repeatReportResultList)) {
            wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "实验室平行样品质量控制情况表");
         } else {
            wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + " " + orderCategory + "实验室平行样品质量控制情况表");
         }

         index++;
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
         Word2024Utils.mergeTableRows(table, 0, 0, 1);
         Word2024Utils.mergeTableRows(table, 1, 0, 1);
         Word2024Utils.mergeTableRows(table, 2, 0, 1);
         Word2024Utils.mergeTableRows(table, 5, 0, 1);
         Word2024Utils.mergeTableRows(table, 6, 0, 1);
         Word2024Utils.mergeTableRows(table, 7, 0, 1);
         Word2024Utils.mergeTableColumns(table, 0, 3, 4);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
      }

      if (!CollectionUtils.isEmpty(repeatReportResultList)) {
         repeatReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         Map<String, List<EmcOrderTaskResultBean>> itemGroupReportResultListMap = repeatReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : repeatReportResultList.stream()
            .filter(e -> e.getExt$Item("reportTestName") != null)
            .map(e -> e.getExt$Item("reportTestName"))
            .distinct()
            .collect(Collectors.toList())) {
            List<EmcOrderTaskResultBean> taskResultBeans2 = itemGroupReportResultListMap.get(testName)
               .stream()
               .filter(r -> "1".equals(r.getExt$Item("multipleitemgroup")))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskResultBeans2)) {
               List<List<Object>> tableDataList = new ArrayList<>();
               taskResultBeans2.stream()
                  .forEach(
                     s -> {
                        List<Object> rowDataList = new ArrayList<>();
                        rowDataList.add(s.getExt$Item("itemGroup"));
                        EmcOrderTaskResultBean task1 = orderTaskResultList.stream()
                           .filter(o -> o.getOrderNo().equals(s.getParentOrderNo()))
                           .collect(Collectors.toList())
                           .get(0);
                        if (!isSend) {
                           if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
                              rowDataList.add(task1.getExt$Item("monitorpointname") + "(" + task1.getExt$Item("samplingdepth") + "m)");
                           } else {
                              rowDataList.add(task1.getExt$Item("monitorpointname"));
                           }
                        } else {
                           rowDataList.add(s.getExt$Item("clientordername"));
                        }

                        EmcOrderTaskResultBean task = repeatReportResultList.stream()
                           .filter(
                              o -> o.getTestMethodId().equals(s.getTestMethodId())
                                    && o.getOrderId().equals(s.getOrderId())
                                    && o.getRn4() != null
                                    && pxCodeList.contains(o.getQcCategoryCode())
                                    && !StringUtils.isEmpty(o.getIndexHighLimit())
                                    && !StringUtils.isEmpty(o.getIndexLowLimit())
                                    && StringUtils.equals(s.getExt$Item("itemGroup"), o.getExt$Item("itemGroup"))
                           )
                           .findFirst()
                           .orElse(new EmcOrderTaskResultBean());
                        rowDataList.add(task.getUnitName());
                        rowDataList.add(this.transScientificCounting(StringUtils.isBlank(s.getRn4()) ? task.getRn4() : s.getRn4()));
                        rowDataList.add(this.transScientificCounting(s.getReportResult()));
                        String res = task.getRn7();
                        String qctype = "相对偏差(%)";
                        if (!CollectionUtils.isEmpty(indexListMap.get(s.getItemId()))) {
                           List<EmcItemQcIndexBean> emcItemQcIndexBeans = indexListMap.get(s.getItemId())
                              .stream()
                              .filter(idxx -> qcCategoryId.equals(idxx.getQcCategoryId()))
                              .collect(Collectors.toList());
                           if (!CollectionUtils.isEmpty(emcItemQcIndexBeans)) {
                              if ("RN7".equals(emcItemQcIndexBeans.get(0).getDecisionCategory())) {
                                 res = task.getRn7();
                                 qctype = "相对偏差(%)";
                              } else if ("RN8".equals(emcItemQcIndexBeans.get(0).getDecisionCategory())) {
                                 res = task.getRn8();
                                 type.set(8);
                                 qctype = "绝对相差";
                              } else if ("RN9".equals(emcItemQcIndexBeans.get(0).getDecisionCategory())) {
                                 res = task.getRn9();
                                 type.set(9);
                                 qctype = "相对相差(%)";
                              }
                           }
                        }

                        rowDataList.add(this.transScientificCounting(res));
                        rowDataList.add(task.getIndexLowLimit() + "~" + task.getIndexHighLimit());
                        rowDataList.add(qctype);
                        if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
                           tableDataList.add(rowDataList);
                        }
                     }
                  );
               List<Object> firstRowDataList = new ArrayList<>();
               firstRowDataList.add("检测项目");
               firstRowDataList.add("点位/样品名称");
               firstRowDataList.add("单位");
               firstRowDataList.add("室内平行样结果");
               firstRowDataList.add("");
               firstRowDataList.add("质控结果");
               firstRowDataList.add("质控范围");
               firstRowDataList.add("质控类型");
               List<Object> secondRowDataList = new ArrayList<>();
               secondRowDataList.add("");
               secondRowDataList.add("");
               secondRowDataList.add("");
               secondRowDataList.add("样品值");
               secondRowDataList.add("样品值-sp");
               secondRowDataList.add("");
               secondRowDataList.add("");
               secondRowDataList.add("");
               tableDataList.add(0, firstRowDataList);
               tableDataList.add(1, secondRowDataList);
               List<MergedRow> mergedRowList = new ArrayList<>();

               for (int columnIndexx = 0; columnIndexx < 8; columnIndexx++) {
                  if (columnIndexx == 0 || columnIndexx == 6) {
                     int biginIndex = 1;

                     for (int idxx = 2; idxx <= tableDataList.size(); idxx++) {
                        if (idxx == tableDataList.size()
                           || !ObjectUtils.equals(tableDataList.get(idxx).get(columnIndexx), tableDataList.get(idxx - 1).get(columnIndexx))) {
                           mergedRowList.add(new MergedRow(columnIndexx, biginIndex, idxx - 1));
                           biginIndex = idxx;
                        }
                     }
                  }
               }

               this.listListNullToString(tableDataList, "/");
               WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
               wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
               wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
               wordTemplateDynamicTableDTO2.setCellHeight(400);
               wordTemplateDynamicTableDTO2.setMergedRowList(mergedRowList);
               wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + testName + "实验室平行样品质量控制情况表");
               index++;
               XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
               Word2024Utils.mergeTableRows(table, 0, 0, 1);
               Word2024Utils.mergeTableRows(table, 1, 0, 1);
               Word2024Utils.mergeTableRows(table, 2, 0, 1);
               Word2024Utils.mergeTableRows(table, 5, 0, 1);
               Word2024Utils.mergeTableRows(table, 6, 0, 1);
               Word2024Utils.mergeTableRows(table, 7, 0, 1);
               Word2024Utils.mergeTableColumns(table, 0, 3, 4);
               table.getRow(0).setRepeatHeader(true);
               table.getRow(1).setRepeatHeader(true);
            }
         }
      }

      if (!CollectionUtils.isEmpty(reportResultList) || !CollectionUtils.isEmpty(repeatReportResultList)) {
         tableIndex++;
      }

      return tableIndex;
   }

   private int qcResultTable4_XP(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      List<EmcOrderTaskResultBean> orderTaskResultList,
      String orderCategory,
      int tableIndex,
      boolean isSend
   ) {
      List<EmcItemQcIndexBean> itemQcIndexList = templateSourceDTO.getItemQcIndexList();
      List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();
      List<EmcOrderBean> qcOrderList = templateSourceDTO.getQcOrderList();
      Map<Long, List<EmcItemQcIndexBean>> indexListMap = itemQcIndexList.stream().collect(Collectors.groupingBy(e -> e.getItemId()));
      List<String> pxCodeList = Arrays.asList("P-DUP");
      Long qcCategoryId = ((EmcQcCategoryBean)this.qcCategoryService.getDao().selectListByOneColumnValue("P-DUP", "QCCATEGORYID", new Order[0]).get(0)).getId();
      int index = 1;
      List<EmcOrderTaskResultBean> reportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && pxCodeList.contains(r.getQcCategoryCode())
                  && !"1".equals(r.getExt$Item("multipleitemgroup"))
                  && !"192".equals(r.getTestId() + "")
         )
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> dioxinReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && pxCodeList.contains(r.getQcCategoryCode())
                  && "192".equals(r.getTestId() + "")
                  && StringUtils.contains(r.getItemName(), "组分")
                  && !StringUtils.contains(r.getItemName(), "直读")
         )
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> repeatReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && pxCodeList.contains(r.getQcCategoryCode())
                  && "1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(reportResultList)) {
         List<List<Object>> tableDataList = new ArrayList<>();
         reportResultList.stream()
            .forEach(
               s -> {
                  List<Object> rowDataList = new ArrayList<>();
                  EmcOrderBean curQcOrder = qcOrderList.stream()
                     .filter(e -> ObjectUtils.equals(s.getOrderId(), e.getId()))
                     .findFirst()
                     .orElse(new EmcOrderBean());
                  List<EmcOrderTaskResultBean> list = orderTaskResultList.stream()
                     .filter(
                        o -> o.getOrderNo().equals(curQcOrder.getParentOrderNo())
                              && "1".equals(o.getReportDisplay())
                              && o.getExt$Item("reportTestName").equals(s.getExt$Item("reportTestName"))
                     )
                     .collect(Collectors.toList());
                  EmcOrderTaskResultBean task1 = s;
                  if (!CollectionUtils.isEmpty(list)) {
                     task1 = list.get(0);
                  }

                  rowDataList.add(s.getExt$Item("reportTestName"));
                  if (!isSend) {
                     if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
                        rowDataList.add(task1.getExt$Item("monitorpointname") + "(" + task1.getExt$Item("samplingdepth") + "m)");
                     } else {
                        rowDataList.add(task1.getExt$Item("monitorpointname"));
                     }
                  } else {
                     rowDataList.add(s.getExt$Item("clientordername"));
                  }

                  EmcOrderTaskResultBean task = orderTaskResultList.stream()
                     .filter(
                        o -> o.getItemId().equals(s.getItemId())
                              && o.getOrderId().equals(s.getOrderId())
                              && o.getRn4() != null
                              && pxCodeList.contains(o.getQcCategoryCode())
                              && !StringUtils.isEmpty(o.getIndexHighLimit())
                              && !StringUtils.isEmpty(o.getIndexLowLimit())
                     )
                     .findFirst()
                     .orElse(null);
                  if (task == null) {
                     task = orderTaskResultList.stream()
                        .filter(
                           o -> o.getTestMethodId().equals(s.getTestMethodId())
                                 && o.getOrderId().equals(s.getOrderId())
                                 && o.getRn4() != null
                                 && pxCodeList.contains(o.getQcCategoryCode())
                                 && !StringUtils.isEmpty(o.getIndexHighLimit())
                                 && !StringUtils.isEmpty(o.getIndexLowLimit())
                        )
                        .findFirst()
                        .orElse(new EmcOrderTaskResultBean());
                  }

                  rowDataList.add(task.getUnitName());
                  rowDataList.add(this.transScientificCounting(StringUtils.isBlank(s.getRn4()) ? task.getRn4() : s.getRn4()));
                  rowDataList.add(this.transScientificCounting(s.getReportResult()));
                  String res = task.getRn7();
                  String qctype = "相对偏差(%)";
                  String qcDecisionCategory = task.getQcDecisionCategory();
                  if ("RN8".equals(qcDecisionCategory)) {
                     res = task.getRn8();
                     qctype = "绝对相差";
                  } else if ("RN9".equals(qcDecisionCategory)) {
                     res = task.getRn9();
                     qctype = "相对相差(%)";
                  }

                  rowDataList.add(this.transScientificCounting(res));
                  rowDataList.add(task.getIndexLowLimit() + "~" + task.getIndexHighLimit());
                  rowDataList.add(qctype);
                  if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
                     tableDataList.add(rowDataList);
                  }
               }
            );
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("点位/样品名称");
         firstRowDataList.add("单位");
         firstRowDataList.add("现场平行样结果");
         firstRowDataList.add("");
         firstRowDataList.add("质控结果");
         firstRowDataList.add("质控范围");
         firstRowDataList.add("质控类型");
         List<Object> secondRowDataList = new ArrayList<>();
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("样品值");
         secondRowDataList.add("样品值-xp");
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("");
         tableDataList.add(0, firstRowDataList);
         tableDataList.add(1, secondRowDataList);
         List<MergedRow> mergedRowList = new ArrayList<>();

         for (int columnIndex = 0; columnIndex < 8; columnIndex++) {
            if (columnIndex == 0 || columnIndex == 6) {
               int biginIndex = 1;

               for (int idx = 2; idx <= tableDataList.size(); idx++) {
                  if (idx == tableDataList.size() || !ObjectUtils.equals(tableDataList.get(idx).get(columnIndex), tableDataList.get(idx - 1).get(columnIndex))) {
                     mergedRowList.add(new MergedRow(columnIndex, biginIndex, idx - 1));
                     biginIndex = idx;
                  }
               }
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         wordTemplateDynamicTableDTO2.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "现场平行样品质量控制情况表");
         index++;
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
         Word2024Utils.mergeTableRows(table, 0, 0, 1);
         Word2024Utils.mergeTableRows(table, 1, 0, 1);
         Word2024Utils.mergeTableRows(table, 2, 0, 1);
         Word2024Utils.mergeTableRows(table, 5, 0, 1);
         Word2024Utils.mergeTableRows(table, 6, 0, 1);
         Word2024Utils.mergeTableRows(table, 7, 0, 1);
         Word2024Utils.mergeTableColumns(table, 0, 3, 4);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
      }

      if (!CollectionUtils.isEmpty(dioxinReportResultList)) {
         List<List<Object>> tableDataList = new ArrayList<>();
         dioxinReportResultList.stream()
            .forEach(
               s -> {
                  List<Object> rowDataList = new ArrayList<>();
                  EmcOrderBean curQcOrder = qcOrderList.stream()
                     .filter(e -> ObjectUtils.equals(s.getOrderId(), e.getId()))
                     .findFirst()
                     .orElse(new EmcOrderBean());
                  List<EmcOrderTaskResultBean> list = orderTaskResultList.stream()
                     .filter(o -> o.getOrderNo().equals(curQcOrder.getParentOrderNo()) && o.getItemName().equals(s.getItemName()))
                     .collect(Collectors.toList());
                  EmcOrderTaskResultBean parentResult = s;
                  if (!CollectionUtils.isEmpty(list)) {
                     parentResult = list.get(0);
                  }

                  String reportTestName = s.getItemAliasName().replace("直读", "");
                  rowDataList.add(reportTestName);
                  if (!isSend) {
                     if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
                        rowDataList.add(parentResult.getExt$Item("monitorpointname") + "(" + parentResult.getExt$Item("samplingdepth") + "m)");
                     } else {
                        rowDataList.add(parentResult.getExt$Item("monitorpointname"));
                     }
                  } else {
                     rowDataList.add(s.getExt$Item("clientordername"));
                  }

                  rowDataList.add(s.getUnitName());
                  rowDataList.add(this.transScientificCounting(s.getRn4()));
                  rowDataList.add(this.transScientificCounting(s.getReportResult()));
                  String res = s.getRn7();
                  String qctype = "相对偏差(%)";
                  String qcDecisionCategory = s.getQcDecisionCategory();
                  if ("RN8".equals(qcDecisionCategory)) {
                     res = s.getRn8();
                     qctype = "绝对相差";
                  } else if ("RN9".equals(qcDecisionCategory)) {
                     res = s.getRn9();
                     qctype = "相对相差(%)";
                  }

                  rowDataList.add(this.transScientificCounting(res));
                  if (!StringUtils.isBlank(s.getIndexLowLimit()) && !StringUtils.isBlank(s.getIndexHighLimit())) {
                     rowDataList.add(s.getIndexLowLimit() + "~" + s.getIndexHighLimit());
                  } else if (!StringUtils.isBlank(s.getIndexLowLimit())) {
                     rowDataList.add(s.getIndexLowLimit());
                  } else if (!StringUtils.isBlank(s.getIndexHighLimit())) {
                     rowDataList.add(s.getIndexHighLimit());
                  } else {
                     rowDataList.add("/");
                  }

                  rowDataList.add(qctype);
                  if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
                     tableDataList.add(rowDataList);
                  }
               }
            );
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("点位/样品名称");
         firstRowDataList.add("单位");
         firstRowDataList.add("现场平行样结果");
         firstRowDataList.add("");
         firstRowDataList.add("质控结果");
         firstRowDataList.add("质控范围");
         firstRowDataList.add("质控类型");
         List<Object> secondRowDataList = new ArrayList<>();
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("样品值");
         secondRowDataList.add("样品值-xp");
         secondRowDataList.add("");
         secondRowDataList.add("");
         secondRowDataList.add("");
         tableDataList.add(0, firstRowDataList);
         tableDataList.add(1, secondRowDataList);
         List<MergedRow> mergedRowList = new ArrayList<>();

         for (int columnIndexx = 0; columnIndexx < 8; columnIndexx++) {
            if (columnIndexx == 0 || columnIndexx == 6) {
               int biginIndex = 1;

               for (int idxx = 2; idxx <= tableDataList.size(); idxx++) {
                  if (idxx == tableDataList.size()
                     || !ObjectUtils.equals(tableDataList.get(idxx).get(columnIndexx), tableDataList.get(idxx - 1).get(columnIndexx))) {
                     mergedRowList.add(new MergedRow(columnIndexx, biginIndex, idxx - 1));
                     biginIndex = idxx;
                  }
               }
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         wordTemplateDynamicTableDTO2.setMergedRowList(mergedRowList);
         wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "二噁英现场平行样品质量控制情况表");
         index++;
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
         Word2024Utils.mergeTableRows(table, 0, 0, 1);
         Word2024Utils.mergeTableRows(table, 1, 0, 1);
         Word2024Utils.mergeTableRows(table, 2, 0, 1);
         Word2024Utils.mergeTableRows(table, 5, 0, 1);
         Word2024Utils.mergeTableRows(table, 6, 0, 1);
         Word2024Utils.mergeTableRows(table, 7, 0, 1);
         Word2024Utils.mergeTableColumns(table, 0, 3, 4);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
      }

      if (!CollectionUtils.isEmpty(repeatReportResultList)) {
         repeatReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         Map<String, List<EmcOrderTaskResultBean>> itemGroupReportResultListMap = repeatReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : repeatReportResultList.stream()
            .filter(e -> e.getExt$Item("reportTestName") != null)
            .map(e -> e.getExt$Item("reportTestName"))
            .distinct()
            .collect(Collectors.toList())) {
            List<EmcOrderTaskResultBean> taskResultBeans2 = itemGroupReportResultListMap.get(testName)
               .stream()
               .filter(r -> "1".equals(r.getExt$Item("multipleitemgroup")))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskResultBeans2)) {
               List<List<Object>> tableDataList = new ArrayList<>();
               taskResultBeans2.stream()
                  .forEach(
                     s -> {
                        List<Object> rowDataList = new ArrayList<>();
                        rowDataList.add(s.getExt$Item("itemGroup"));
                        EmcOrderBean curQcOrder = qcOrderList.stream()
                           .filter(e -> ObjectUtils.equals(s.getOrderId(), e.getId()))
                           .findFirst()
                           .orElse(new EmcOrderBean());
                        List<EmcOrderTaskResultBean> list = orderTaskResultList.stream()
                           .filter(
                              o -> o.getOrderNo().equals(curQcOrder.getParentOrderNo())
                                    && "1".equals(o.getReportDisplay())
                                    && o.getExt$Item("reportTestName").equals(s.getExt$Item("reportTestName"))
                                    && o.getExt$Item("itemGroup").equals(s.getExt$Item("itemGroup"))
                           )
                           .collect(Collectors.toList());
                        EmcOrderTaskResultBean task1 = s;
                        if (!CollectionUtils.isEmpty(list)) {
                           task1 = list.get(0);
                        }

                        if (!isSend) {
                           if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
                              rowDataList.add(task1.getExt$Item("monitorpointname") + "(" + task1.getExt$Item("samplingdepth") + "m)");
                           } else {
                              rowDataList.add(task1.getExt$Item("monitorpointname"));
                           }
                        } else {
                           rowDataList.add(s.getExt$Item("clientordername"));
                        }

                        EmcOrderTaskResultBean task = orderTaskResultList.stream()
                           .filter(
                              o -> o.getItemId().equals(s.getItemId())
                                    && o.getOrderId().equals(s.getOrderId())
                                    && o.getRn4() != null
                                    && pxCodeList.contains(o.getQcCategoryCode())
                                    && !StringUtils.isEmpty(o.getIndexHighLimit())
                                    && !StringUtils.isEmpty(o.getIndexLowLimit())
                           )
                           .findFirst()
                           .orElse(null);
                        if (task == null) {
                           task = orderTaskResultList.stream()
                              .filter(
                                 o -> o.getTestMethodId().equals(s.getTestMethodId())
                                       && o.getOrderId().equals(s.getOrderId())
                                       && o.getRn4() != null
                                       && pxCodeList.contains(o.getQcCategoryCode())
                                       && !StringUtils.isEmpty(o.getIndexHighLimit())
                                       && !StringUtils.isEmpty(o.getIndexLowLimit())
                              )
                              .findFirst()
                              .orElse(new EmcOrderTaskResultBean());
                        }

                        rowDataList.add(task.getUnitName());
                        rowDataList.add(this.transScientificCounting(StringUtils.isBlank(s.getRn4()) ? task.getRn4() : s.getRn4()));
                        rowDataList.add(this.transScientificCounting(s.getReportResult()));
                        String res = task.getRn7();
                        String qctype = "相对偏差(%)";
                        String qcDecisionCategory = task.getQcDecisionCategory();
                        if ("RN8".equals(qcDecisionCategory)) {
                           res = task.getRn8();
                           qctype = "绝对相差";
                        } else if ("RN9".equals(qcDecisionCategory)) {
                           res = task.getRn9();
                           qctype = "相对相差(%)";
                        }

                        rowDataList.add(this.transScientificCounting(res));
                        rowDataList.add(task.getIndexLowLimit() + "~" + task.getIndexHighLimit());
                        rowDataList.add(qctype);
                        if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
                           tableDataList.add(rowDataList);
                        }
                     }
                  );
               List<Object> firstRowDataList = new ArrayList<>();
               firstRowDataList.add("检测项目");
               firstRowDataList.add("点位/样品名称");
               firstRowDataList.add("单位");
               firstRowDataList.add("现场平行样结果");
               firstRowDataList.add("");
               firstRowDataList.add("质控结果");
               firstRowDataList.add("质控范围");
               firstRowDataList.add("质控类型");
               List<Object> secondRowDataList = new ArrayList<>();
               secondRowDataList.add("");
               secondRowDataList.add("");
               secondRowDataList.add("");
               secondRowDataList.add("样品值");
               secondRowDataList.add("样品值-xp");
               secondRowDataList.add("");
               secondRowDataList.add("");
               secondRowDataList.add("");
               tableDataList.add(0, firstRowDataList);
               tableDataList.add(1, secondRowDataList);
               List<MergedRow> mergedRowList = new ArrayList<>();

               for (int columnIndexxx = 0; columnIndexxx < 8; columnIndexxx++) {
                  if (columnIndexxx == 0 || columnIndexxx == 6) {
                     int biginIndex = 1;

                     for (int idxxx = 2; idxxx <= tableDataList.size(); idxxx++) {
                        if (idxxx == tableDataList.size()
                           || !ObjectUtils.equals(tableDataList.get(idxxx).get(columnIndexxx), tableDataList.get(idxxx - 1).get(columnIndexxx))) {
                           mergedRowList.add(new MergedRow(columnIndexxx, biginIndex, idxxx - 1));
                           biginIndex = idxxx;
                        }
                     }
                  }
               }

               this.listListNullToString(tableDataList, "/");
               WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
               wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
               wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
               wordTemplateDynamicTableDTO2.setCellHeight(400);
               wordTemplateDynamicTableDTO2.setMergedRowList(mergedRowList);
               wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + testName + "现场平行样品质量控制情况表");
               index++;
               XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
               Word2024Utils.mergeTableRows(table, 0, 0, 1);
               Word2024Utils.mergeTableRows(table, 1, 0, 1);
               Word2024Utils.mergeTableRows(table, 2, 0, 1);
               Word2024Utils.mergeTableRows(table, 5, 0, 1);
               Word2024Utils.mergeTableRows(table, 6, 0, 1);
               Word2024Utils.mergeTableRows(table, 7, 0, 1);
               Word2024Utils.mergeTableColumns(table, 0, 3, 4);
               table.getRow(0).setRepeatHeader(true);
               table.getRow(1).setRepeatHeader(true);
            }
         }
      }

      if (!CollectionUtils.isEmpty(reportResultList) || !CollectionUtils.isEmpty(repeatReportResultList)) {
         tableIndex++;
      }

      return tableIndex;
   }

   private int qcResultTable6(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      List<EmcOrderTaskResultBean> orderTaskResultList,
      String orderCategory,
      int tableIndex,
      boolean isSend
   ) {
      List<String> jbCodeList = Arrays.asList("zcsbkb");
      int index = 1;
      List<EmcOrderTaskResultBean> reportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && jbCodeList.contains(r.getQcCategoryCode())
                  && !"1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> repeatReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && "1".equals(r.getReportDisplay())
                  && jbCodeList.contains(r.getQcCategoryCode())
                  && "1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测项目");
      firstRowDataList.add("设备淋洗液样编号");
      firstRowDataList.add("检测结果");
      firstRowDataList.add("检出限");
      tableDataList.add(firstRowDataList);
      if (!CollectionUtils.isEmpty(reportResultList)) {
         Map<String, List<EmcOrderTaskResultBean>> taskReportResultListMap2 = reportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String key : taskReportResultListMap2.keySet()) {
            EmcOrderTaskResultBean s = taskReportResultListMap2.get(key)
               .stream()
               .filter(t -> jbCodeList.contains(t.getQcCategoryCode()))
               .collect(Collectors.toList())
               .get(0);
            List<Object> rowDataList = new ArrayList<>();
            String itemName = StringUtils.isBlank(s.getUnitName())
               ? s.getExt$Item("reportTestName")
               : s.getExt$Item("reportTestName") + "(" + s.getUnitName() + ")";
            rowDataList.add(itemName);
            rowDataList.add(s.getOrderNo());
            rowDataList.add(this.transScientificCounting(s.getReportResult()));
            rowDataList.add(s.getLowLimit());
            tableDataList.add(rowDataList);
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "设备淋洗液检测结果表");
         index++;
         XWPFTable var24 = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
      }

      if (!CollectionUtils.isEmpty(repeatReportResultList)) {
         for (String testName : repeatReportResultList.stream()
            .filter(e -> e.getExt$Item("reportTestName") != null)
            .map(e -> e.getExt$Item("reportTestName"))
            .distinct()
            .collect(Collectors.toList())) {
            List<List<Object>> tableDataList2 = new ArrayList<>();
            tableDataList2.add(firstRowDataList);
            List<EmcOrderTaskResultBean> taskResultBeans = repeatReportResultList.stream()
               .filter(r -> testName.equals(r.getExt$Item("reportTestName")) && "1".equals(r.getExt$Item("multipleitemgroup")))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskResultBeans)) {
               taskResultBeans.stream()
                  .forEach(
                     s -> {
                        List<Object> rowDataList = new ArrayList<>();
                        String itemName = StringUtils.isBlank(s.getUnitName())
                           ? s.getExt$Item("itemGroup")
                           : s.getExt$Item("itemGroup") + "(" + s.getUnitName() + ")";
                        rowDataList.add(itemName);
                        rowDataList.add(s.getOrderNo());
                        rowDataList.add(this.transScientificCounting(s.getReportResult()));
                        rowDataList.add(s.getLowLimit());
                        tableDataList2.add(rowDataList);
                     }
                  );
               this.listListNullToString(tableDataList2, "/");
               WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
               wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
               wordTemplateDynamicTableDTO2.setTableDataList(tableDataList2);
               wordTemplateDynamicTableDTO2.setCellHeight(400);
               wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + testName + "设备淋洗液检测结果表");
               index++;
               XWPFTable table2 = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
               table2.getRow(0).setRepeatHeader(true);
            }
         }
      }

      return tableIndex++;
   }

   private int qcResultTable5(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      List<EmcOrderTaskResultBean> orderTaskResultList,
      String orderCategory,
      int tableIndex,
      boolean isSend
   ) {
      List<String> jbCodeList = Arrays.asList("L-SPKC", "L-SPKCK");
      List<EmcOrderTaskResultBean> commonOrderTaskResultList = templateSourceDTO.getOrderTaskResultList();
      int index = 1;
      Map<Long, List<EmcOrderTaskResultBean>> taskReportResultListMap = orderTaskResultList.stream().collect(Collectors.groupingBy(e -> e.getTestMethodId()));
      List<EmcOrderTaskResultBean> reportResultList = orderTaskResultList.stream()
         .filter(r -> r.getExt$Item("reportTestName") != null && jbCodeList.contains(r.getQcCategoryCode()) && !"1".equals(r.getExt$Item("multipleitemgroup")))
         .collect(Collectors.toList());
      List<EmcOrderTaskResultBean> repeatReportResultList = orderTaskResultList.stream()
         .filter(
            r -> r.getExt$Item("reportTestName") != null
                  && !StringUtils.isEmpty(r.getStandQty())
                  && jbCodeList.contains(r.getQcCategoryCode())
                  && "1".equals(r.getExt$Item("multipleitemgroup"))
         )
         .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(reportResultList)) {
         reportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         Map<String, List<EmcOrderTaskResultBean>> taskReportResultListMap2 = reportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getTestMethodId() + "," + e.getOrderId(), TreeMap::new, Collectors.toList()));
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("点位/样品名称");
         firstRowDataList.add("加标值");
         firstRowDataList.add("加标回收率（%）");
         firstRowDataList.add("回收率控制范围（%）");
         tableDataList.add(firstRowDataList);

         for (String key : taskReportResultListMap2.keySet()) {
            EmcOrderTaskResultBean s = taskReportResultListMap2.get(key).get(0);
            List<EmcOrderTaskResultBean> taskResultBeans = taskReportResultListMap2.get(key)
               .stream()
               .filter(t -> !StringUtils.isEmpty(t.getStandQty()))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskResultBeans)) {
               s = taskResultBeans.get(0);
            }

            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(s.getExt$Item("reportTestName") + "(" + s.getRn12() + ")");
            EmcOrderTaskResultBean infoResult = s;
            String parentOrderNo = s.getParentOrderNo();
            if (parentOrderNo != null) {
               EmcOrderTaskResultBean parentOrderResult = commonOrderTaskResultList.stream()
                  .filter(e -> ObjectUtils.equals(e.getOrderNo(), parentOrderNo))
                  .findFirst()
                  .orElse(new EmcOrderTaskResultBean());
               infoResult = parentOrderResult;
            }

            String monitorPointName = infoResult.getExt$Item("monitorpointname");
            String samplingDepth = infoResult.getExt$Item("samplingdepth");
            if ("L-SPKCK".equals(s.getQcCategoryCode())) {
               rowDataList.add(parentOrderNo + "(实验室空白)");
            } else if (!isSend) {
               if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
                  rowDataList.add(monitorPointName + "(" + samplingDepth + "m)");
               } else {
                  rowDataList.add(monitorPointName);
               }
            } else {
               rowDataList.add(s.getExt$Item("clientordername"));
            }

            rowDataList.add(s.getStandQty());
            rowDataList.add(s.getRn6());
            rowDataList.add(s.getIndexLowLimit() + "~" + s.getIndexHighLimit());
            if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
               tableDataList.add(rowDataList);
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         if (!CollectionUtils.isEmpty(repeatReportResultList)) {
            wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + "加标样品质量控制情况表");
         } else {
            wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + " " + orderCategory + "加标样品质量控制情况表");
         }

         index++;
         XWPFTable table2 = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
         table2.getRow(0).setRepeatHeader(true);
      }

      if (!CollectionUtils.isEmpty(repeatReportResultList)) {
         repeatReportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
         Map<String, List<EmcOrderTaskResultBean>> itemGroupReportResultListMap = repeatReportResultList.stream()
            .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));

         for (String testName : repeatReportResultList.stream()
            .filter(e -> e.getExt$Item("reportTestName") != null)
            .map(e -> e.getExt$Item("reportTestName"))
            .distinct()
            .collect(Collectors.toList())) {
            List<List<Object>> tableDataList = new ArrayList<>();
            List<Object> firstRowDataList = new ArrayList<>();
            firstRowDataList.add("检测项目");
            firstRowDataList.add("点位/样品名称");
            firstRowDataList.add("加标值");
            firstRowDataList.add("加标回收率（%）");
            firstRowDataList.add("回收率控制范围（%）");
            tableDataList.add(firstRowDataList);
            List<EmcOrderTaskResultBean> taskResultBeansx = itemGroupReportResultListMap.get(testName)
               .stream()
               .filter(r -> "1".equals(r.getExt$Item("multipleitemgroup")))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskResultBeansx)) {
               taskResultBeansx.stream()
                  .forEach(
                     sx -> {
                        List<Object> rowDataListx = new ArrayList<>();
                        rowDataListx.add(sx.getExt$Item("itemGroup") + "(" + sx.getUnitName() + ")");
                        EmcOrderTaskResultBean infoResultx = sx;
                        String parentOrderNox = sx.getParentOrderNo();
                        if (parentOrderNox != null) {
                           EmcOrderTaskResultBean parentOrderResult = commonOrderTaskResultList.stream()
                              .filter(e -> ObjectUtils.equals(e.getOrderNo(), parentOrderNox))
                              .findFirst()
                              .orElse(new EmcOrderTaskResultBean());
                           infoResultx = parentOrderResult;
                        }

                        String monitorPointNamex = infoResultx.getExt$Item("monitorpointname");
                        String samplingDepthx = infoResultx.getExt$Item("samplingdepth");
                        if ("L-SPKCK".equals(sx.getQcCategoryCode())) {
                           rowDataListx.add(sx.getParentOrderNo() + "(实验室空白)");
                        } else if (!isSend) {
                           if (!StringUtils.isBlank(orderCategory) && orderCategory.contains("土壤")) {
                              rowDataListx.add(monitorPointNamex + "(" + samplingDepthx + "m)");
                           } else {
                              rowDataListx.add(monitorPointNamex);
                           }
                        } else {
                           rowDataListx.add(sx.getExt$Item("clientordername"));
                        }

                        rowDataListx.add(sx.getStandQty());
                        rowDataListx.add(sx.getRn6());
                        rowDataListx.add(sx.getIndexLowLimit() + "~" + sx.getIndexHighLimit());
                        if (rowDataListx.subList(2, rowDataListx.size()).stream().anyMatch(e -> !"/".equals(e))) {
                           tableDataList.add(rowDataListx);
                        }
                     }
                  );
               this.listListNullToString(tableDataList, "/");
               WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
               wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
               wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
               wordTemplateDynamicTableDTO2.setCellHeight(400);
               wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + "-" + index + " " + orderCategory + testName + "加标样品质量控制情况表");
               index++;
               XWPFTable table2 = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
               table2.getRow(0).setRepeatHeader(true);
            }
         }
      }

      if (!CollectionUtils.isEmpty(reportResultList) || !CollectionUtils.isEmpty(repeatReportResultList)) {
         tableIndex++;
      }

      return tableIndex;
   }

   private int qcDioxinRecycleResultTable(
      XWPFDocument document,
      WordTemplateSourceDTO templateSourceDTO,
      List<EmcReportResultBean> reportResultList,
      String orderCategory,
      int tableIndex,
      boolean isSend
   ) {
      List<EmcReportResultBean> dioxinReportResultList = reportResultList.stream()
         .filter(e -> "192".equals(e.getTestId() + "") && StringUtils.equals(e.getDataType(), "sample") && StringUtils.contains(e.getItemAliasName(), "回收率"))
         .collect(Collectors.toList());
      if (dioxinReportResultList.isEmpty()) {
         return tableIndex;
      } else {
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测项目");
         firstRowDataList.add("样品名称");
         firstRowDataList.add("加标回收率（%）");
         firstRowDataList.add("回收率控制范围（%）");
         tableDataList.add(firstRowDataList);
         List<String> orderNoList = dioxinReportResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
         orderNoList.sort(Comparator.comparing(String::toString));

         for (String orderNo : orderNoList) {
            for (EmcReportResultBean res : dioxinReportResultList.stream()
               .filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo))
               .collect(Collectors.toList())) {
               List<Object> rowDataList = new ArrayList<>();
               String reportTestName = res.getItemAliasName().replace("回收率", "");
               rowDataList.add(reportTestName);
               rowDataList.add(res.getOrderNo());
               rowDataList.add(this.transScientificCounting(res.getReportResult()));
               rowDataList.add(res.getExt$Item("allowableerror"));
               tableDataList.add(rowDataList);
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO2 = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO2.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO2.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO2.setCellHeight(400);
         wordTemplateDynamicTableDTO2.setTableTitle("附表 " + tableIndex + " " + orderCategory + "二噁英内标回收率质量控制情况表");
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO2);
         table.getRow(0).setRepeatHeader(true);
         return tableIndex + 1;
      }
   }

   private int qcResultTable(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO, boolean isSend, int tableCount) {
      List<EmcOrderTaskResultBean> allOrderTaskResultList = new ArrayList<>();
      List<EmcOrderTaskResultBean> qcTaskResultList = templateSourceDTO.getQcTaskResultList();
      allOrderTaskResultList.addAll(qcTaskResultList);
      Set<Long> existingIds = allOrderTaskResultList.stream().<Long>map(EmcOrderTaskResultBean::getId).collect(Collectors.toSet());

      for (EmcOrderTaskResultBean r : templateSourceDTO.getOrderTaskResultList()) {
         if (!existingIds.contains(r.getId())) {
            allOrderTaskResultList.add(r);
            existingIds.add(r.getId());
         }
      }

      templateSourceDTO.setAllOrderTaskResultList(allOrderTaskResultList);
      List<EmcOrderTaskResultBean> reportResultList = allOrderTaskResultList.stream()
         .filter(rx -> rx.getExt$Item("reportTestName") != null)
         .collect(Collectors.toList());
      int tableIndex = tableCount;
      List<String> orderCategoryList = reportResultList.stream()
         .filter(e -> e.getExt$Item("ordercategory") != null)
         .map(e -> e.getExt$Item("ordercategory"))
         .distinct()
         .collect(Collectors.toList());
      List<String> orderCategorySortList = Arrays.asList("土壤", "地下水", "沉积物");
      orderCategoryList = orderCategoryList.stream()
         .sorted(Comparator.comparing(e -> (String)e, Comparator.comparing(orderCategorySortList::indexOf)))
         .collect(Collectors.toList());

      for (int i = 0; i < orderCategoryList.size(); i++) {
         String orderCategory = orderCategoryList.get(i);
         if (!"噪声".equals(orderCategory)) {
            List<EmcOrderTaskResultBean> caTaskResults = reportResultList.stream()
               .filter(t -> orderCategory.equals(t.getExt$Item("ordercategory")) && "0".equals(t.getExt$Item("testflag")))
               .collect(Collectors.toList());
            this.qcNumTable(document, caTaskResults, tableIndex, orderCategory);
            tableIndex++;
         }
      }

      if ("1".equals(templateSourceDTO.getReport().getQcReport())) {
         for (int ix = 0; ix < orderCategoryList.size(); ix++) {
            String orderCategory = orderCategoryList.get(ix);
            List<EmcOrderTaskResultBean> curOrderTaskResultList = allOrderTaskResultList.stream()
               .filter(t -> orderCategory.equals(t.getExt$Item("ordercategory")))
               .collect(Collectors.toList());
            tableIndex = this.qcResultTable2(document, templateSourceDTO, curOrderTaskResultList, orderCategory, tableIndex);
         }

         for (int ix = 0; ix < orderCategoryList.size(); ix++) {
            String orderCategory = orderCategoryList.get(ix);
            List<EmcOrderTaskResultBean> curOrderTaskResultList = allOrderTaskResultList.stream()
               .filter(t -> orderCategory.equals(t.getExt$Item("ordercategory")))
               .collect(Collectors.toList());
            tableIndex = this.qcResultTable3(document, templateSourceDTO, curOrderTaskResultList, orderCategory, tableIndex);
         }

         for (int ix = 0; ix < orderCategoryList.size(); ix++) {
            String orderCategory = orderCategoryList.get(ix);
            List<EmcOrderTaskResultBean> curOrderTaskResultList = allOrderTaskResultList.stream()
               .filter(t -> orderCategory.equals(t.getExt$Item("ordercategory")))
               .collect(Collectors.toList());
            tableIndex = this.qcResultTable4(document, templateSourceDTO, curOrderTaskResultList, orderCategory, tableIndex, isSend);
         }

         for (int ix = 0; ix < orderCategoryList.size(); ix++) {
            String orderCategory = orderCategoryList.get(ix);
            List<EmcOrderTaskResultBean> curOrderTaskResultList = allOrderTaskResultList.stream()
               .filter(t -> orderCategory.equals(t.getExt$Item("ordercategory")))
               .collect(Collectors.toList());
            tableIndex = this.qcResultTable4_XP(document, templateSourceDTO, curOrderTaskResultList, orderCategory, tableIndex, isSend);
         }

         for (int ix = 0; ix < orderCategoryList.size(); ix++) {
            String orderCategory = orderCategoryList.get(ix);
            List<EmcOrderTaskResultBean> curOrderTaskResultList = allOrderTaskResultList.stream()
               .filter(t -> orderCategory.equals(t.getExt$Item("ordercategory")))
               .collect(Collectors.toList());
            tableIndex = this.qcResultTable5(document, templateSourceDTO, curOrderTaskResultList, orderCategory, tableIndex, isSend);
         }

         List<EmcReportResultBean> allReportResultList = templateSourceDTO.getReportResultList();

         for (int ix = 0; ix < orderCategoryList.size(); ix++) {
            String orderCategory = orderCategoryList.get(ix);
            List<EmcReportResultBean> curReportResultList = allReportResultList.stream()
               .filter(t -> orderCategory.equals(t.getOrderCategory()))
               .collect(Collectors.toList());
            tableIndex = this.qcDioxinRecycleResultTable(document, templateSourceDTO, curReportResultList, orderCategory, tableIndex, isSend);
         }

         for (int ix = 0; ix < orderCategoryList.size(); ix++) {
            String orderCategory = orderCategoryList.get(ix);
            List<EmcOrderTaskResultBean> curOrderTaskResultList = allOrderTaskResultList.stream()
               .filter(t -> orderCategory.equals(t.getExt$Item("ordercategory")))
               .collect(Collectors.toList());
            tableIndex = this.qcResultTable6(document, templateSourceDTO, curOrderTaskResultList, orderCategory, tableIndex, isSend);
         }
      }

      return this.createSamplePointTable(document, templateSourceDTO, tableIndex);
   }

   private void qcNumTable(XWPFDocument document, List<EmcOrderTaskResultBean> reportResultList, int tableIndex, String orderCategory) {
      Map<String, List<EmcOrderTaskResultBean>> itemGroupReportResultListMap = reportResultList.stream()
         .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
      List<String> commonCodeList = Arrays.asList("N/A");
      List<Entry<String, List<EmcOrderTaskResultBean>>> entries = new ArrayList<>(itemGroupReportResultListMap.entrySet());
      int groupSize = 6;

      for (int i = 0; i < entries.size(); i += groupSize) {
         List<Entry<String, List<EmcOrderTaskResultBean>>> subEntries = entries.subList(i, Math.min(i + groupSize, entries.size()));
         List<List<Object>> tableDataList = new ArrayList<>();
         List<Object> firstRowDataList = new ArrayList<>();
         firstRowDataList.add("检测因子名称");
         firstRowDataList.add("");
         tableDataList.add(firstRowDataList);
         List<Object> secondRowDataList = new ArrayList<>();
         secondRowDataList.add("样品数");
         secondRowDataList.add("");
         tableDataList.add(secondRowDataList);
         List<String> qcNameList = Arrays.asList("现场平行", "实验室平行", "样品加标样", "空白加标样", "有证标准物质", "校核点", "实验室空白", "全程序空白", "运输空白", "试剂空白");
         Map<String, List<String>> qcNameToCodeMap = new HashMap<>();
         qcNameToCodeMap.put("现场平行", Arrays.asList("P-DUP"));
         qcNameToCodeMap.put("实验室平行", Arrays.asList("L-DUP"));
         qcNameToCodeMap.put("样品加标样", Arrays.asList("L-SPKC"));
         qcNameToCodeMap.put("空白加标样", Arrays.asList("L-SPKCK"));
         qcNameToCodeMap.put("有证标准物质", Arrays.asList("QC-STD"));
         qcNameToCodeMap.put("校核点", Arrays.asList("L-QXCH"));
         qcNameToCodeMap.put("实验室空白", Arrays.asList("L-BLANK"));
         qcNameToCodeMap.put("全程序空白", Arrays.asList("P-BLANK"));
         qcNameToCodeMap.put("运输空白", Arrays.asList("P-YSKB"));
         qcNameToCodeMap.put("试剂空白", Arrays.asList("L-SJKB"));

         for (String qcName : qcNameList) {
            List<Object> rowDataList1 = new ArrayList<>();
            rowDataList1.add(qcName);
            rowDataList1.add("质控数（个）");
            List<Object> rowDataList2 = new ArrayList<>();
            rowDataList2.add("");
            rowDataList2.add("质控比例（%）");
            List<Object> rowDataList3 = new ArrayList<>();
            rowDataList3.add("");
            rowDataList3.add("合格率（%）");
            tableDataList.add(rowDataList1);
            tableDataList.add(rowDataList2);
            tableDataList.add(rowDataList3);
         }

         for (Entry<String, List<EmcOrderTaskResultBean>> subEntry : subEntries) {
            firstRowDataList.add(subEntry.getKey());
            List<EmcOrderTaskResultBean> curReportResultList = subEntry.getValue();
            Long commonOrderNum = this.getQcOrderNum(curReportResultList, commonCodeList);
            secondRowDataList.add(commonOrderNum);

            for (int j = 0; j < qcNameList.size(); j++) {
               String qcName = qcNameList.get(j);
               List<String> qcCodeList = qcNameToCodeMap.get(qcName);
               Long qcOrderNum = this.getQcOrderNum(curReportResultList, qcCodeList);
               List<Object> rowDataList1 = tableDataList.get(3 * j + 2);
               List<Object> rowDataList2 = tableDataList.get(3 * j + 3);
               List<Object> rowDataList3 = tableDataList.get(3 * j + 4);
               if (qcOrderNum == 0L) {
                  rowDataList1.add("/");
                  rowDataList2.add("/");
                  rowDataList3.add("/");
               } else {
                  rowDataList1.add(qcOrderNum);
                  if (commonOrderNum > 0L) {
                     rowDataList2.add(
                        NumberUtils.rounding(
                           ArithUtils.mul(ArithUtils.div((double)qcOrderNum.longValue(), (double)commonOrderNum.longValue()) + "", "100") + "", "ISO,0"
                        )
                     );
                  } else {
                     rowDataList2.add("/");
                  }

                  rowDataList3.add("100");
               }
            }
         }

         this.listListNullToString(tableDataList, "/");
         WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
         wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
         wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
         wordTemplateDynamicTableDTO.setCellHeight(400);
         wordTemplateDynamicTableDTO.setTableTitle("附表 " + tableIndex + " 质量控制情况表(" + orderCategory + ")");
         XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);

         for (int jx = 0; jx < qcNameList.size(); jx++) {
            Word2024Utils.mergeTableRows(table, 0, 3 * jx + 2, 3 * jx + 4);
         }

         Word2024Utils.mergeTableColumns(table, 0, 0, 1);
         Word2024Utils.mergeTableColumns(table, 1, 0, 1);
         table.getRow(0).setRepeatHeader(true);
         table.getRow(1).setRepeatHeader(true);
      }
   }

   private void extracted(XWPFDocument document, List<EmcOrderTaskResultBean> reportResultList, int tableIndex, String orderCategory) {
      List<List<Object>> tableDataList = new ArrayList<>();
      List<Object> firstRowDataList = new ArrayList<>();
      firstRowDataList.add("检测因子名称");
      firstRowDataList.add("样品数");
      firstRowDataList.add("空白样");
      firstRowDataList.add("");
      firstRowDataList.add("平行样");
      firstRowDataList.add("");
      firstRowDataList.add("平行样");
      firstRowDataList.add("加标样");
      firstRowDataList.add("");
      firstRowDataList.add("");
      firstRowDataList.add("标样或自配标准溶液");
      firstRowDataList.add("");
      tableDataList.add(firstRowDataList);
      List<Object> secondRowDataList = new ArrayList<>();
      secondRowDataList.add("");
      secondRowDataList.add("");
      secondRowDataList.add("空白样（个）");
      secondRowDataList.add("合格率（%）");
      secondRowDataList.add("平行样（个）");
      secondRowDataList.add("检查率（%）");
      secondRowDataList.add("合格率（%）");
      secondRowDataList.add("加标样（个）");
      secondRowDataList.add("检查率（%）");
      secondRowDataList.add("合格率（%）");
      secondRowDataList.add("标样或自配标准溶液（个）");
      secondRowDataList.add("合格率（%）");
      tableDataList.add(secondRowDataList);
      reportResultList.sort(Comparator.comparing(e -> e.getExt$Item("reportTestName")));
      List<String> commonCodeList = Arrays.asList("N/A");
      List<String> kbCodeList = Arrays.asList("P-BLANK", "P-YSKB", "L-BLANK", "L-CZKB", "L-SJKB");
      List<String> pxCodeList = Arrays.asList("L-DUP");
      List<String> jbCodeList = Arrays.asList("L-SPKC", "L-SPKCK");
      List<String> bzyCodeList = Arrays.asList("QC-STD", "PQC-STD");
      Map<String, List<EmcOrderTaskResultBean>> itemGroupReportResultListMap = reportResultList.stream()
         .collect(Collectors.groupingBy(e -> e.getExt$Item("reportTestName")));
      itemGroupReportResultListMap.forEach((key, curReportResultList) -> {
         List<Object> rowDataList = new ArrayList<>();
         rowDataList.add(curReportResultList.get(0).getExt$Item("reportTestName"));
         Long commonOrderNum = this.getQcOrderNum((List<EmcOrderTaskResultBean>)curReportResultList, commonCodeList);
         rowDataList.add(commonOrderNum);
         this.qcResultData2((List<EmcOrderTaskResultBean>)curReportResultList, kbCodeList, rowDataList);
         this.qcResultData3((List<EmcOrderTaskResultBean>)curReportResultList, pxCodeList, rowDataList, commonOrderNum);
         this.qcResultData3((List<EmcOrderTaskResultBean>)curReportResultList, jbCodeList, rowDataList, commonOrderNum);
         this.qcResultData2((List<EmcOrderTaskResultBean>)curReportResultList, bzyCodeList, rowDataList);
         if (rowDataList.subList(2, rowDataList.size()).stream().anyMatch(e -> !"/".equals(e))) {
            tableDataList.add(rowDataList);
         }
      });
      this.listListNullToString(tableDataList, "/");
      WordTemplateDynamicTableDTO wordTemplateDynamicTableDTO = new WordTemplateDynamicTableDTO();
      wordTemplateDynamicTableDTO.setReplaceKeyword("qcQuantityTable");
      wordTemplateDynamicTableDTO.setTableDataList(tableDataList);
      wordTemplateDynamicTableDTO.setCellHeight(400);
      wordTemplateDynamicTableDTO.setTableTitle("附表 " + tableIndex + " 质量控制情况表(" + orderCategory + ")");
      XWPFTable table = Word2024Utils.createTable(document, wordTemplateDynamicTableDTO);
      Word2024Utils.mergeTableRows(table, 0, 0, 1);
      Word2024Utils.mergeTableRows(table, 1, 0, 1);
      Word2024Utils.mergeTableColumns(table, 0, 2, 3);
      Word2024Utils.mergeTableColumns(table, 0, 4, 6);
      Word2024Utils.mergeTableColumns(table, 0, 7, 9);
      Word2024Utils.mergeTableColumns(table, 0, 10, 11);
      table.getRow(0).setRepeatHeader(true);
      table.getRow(1).setRepeatHeader(true);
   }

   private void qcResultData2(List<EmcOrderTaskResultBean> curReportResultList, List<String> kbCodeList, List<Object> rowDataList) {
      Long kbOrderNum = this.getQcOrderNum(curReportResultList, kbCodeList);
      if (kbOrderNum == 0L) {
         rowDataList.add("/");
         rowDataList.add("/");
      } else {
         rowDataList.add(kbOrderNum);
         rowDataList.add(100);
      }
   }

   private void qcResultData3(List<EmcOrderTaskResultBean> curReportResultList, List<String> pxCodeList, List<Object> rowDataList, Long commonOrderNum) {
      Long pxOrderNum = this.getQcOrderNum(curReportResultList, pxCodeList);
      if (pxOrderNum != 0L && commonOrderNum != 0L) {
         rowDataList.add(pxOrderNum);
         if (ObjectUtils.equals(commonOrderNum, 0L)) {
            rowDataList.add("");
         } else {
            rowDataList.add(
               NumberUtils.rounding(
                  ArithUtils.mul(ArithUtils.div((double)pxOrderNum.longValue(), (double)commonOrderNum.longValue()) + "", "100") + "", "ISO,0"
               )
            );
         }

         rowDataList.add(100);
      } else {
         rowDataList.add("/");
         rowDataList.add("/");
         rowDataList.add("/");
      }
   }

   private Long getQcOrderNum(List<EmcOrderTaskResultBean> reportResultList, List<String> codeList) {
      List<EmcOrderTaskResultBean> codeResutList = reportResultList.stream().filter(e -> codeList.contains(e.getQcCategoryCode())).collect(Collectors.toList());
      long orderNum = codeResutList.stream().map(e -> e.getOrderId()).distinct().count();
      return orderNum;
   }

   private String getNoiseSampleDateTimeRange(EmcReportResultBean reportResult) {
      String begin = this.getNoiseSampleDateTime(reportResult.getSampleBeginDate(), reportResult.getSampleBeginTime());
      LocalDate completeDate = reportResult.getSampleCompleteDate() == null ? reportResult.getSampleBeginDate() : reportResult.getSampleCompleteDate();
      String completeTime = StringUtils.isBlank(reportResult.getSampleCompleteTime())
         ? reportResult.getSampleBeginTime()
         : reportResult.getSampleCompleteTime();
      String complete = this.getNoiseSampleDateTime(completeDate, completeTime);
      if (StringUtils.isBlank(begin)) {
         return complete;
      } else {
         return !StringUtils.isBlank(complete) && !StringUtils.equals(begin, complete) ? begin + "——" + complete : begin;
      }
   }

   private String getNoiseSampleDateTime(LocalDate date, String time) {
      String dateText = date == null ? "" : DateTimeUtils.formatLocalDate(date, "yyyy年MM月dd日");
      String timeText = StringUtils.isBlank(time) ? "" : time;
      if (StringUtils.isBlank(dateText)) {
         return timeText;
      } else {
         return StringUtils.isBlank(timeText) ? dateText : dateText + " " + timeText;
      }
   }

   private String getSampleDate(EmcProjectBean project, List<EmcOrderBean> orderList) {
      if ("1".equals(project.getSample())) {
         List<LocalDate> sampleBeginDateList = orderList.stream().<LocalDate>map(EmcOrderBean::getSampleBeginDate).distinct().collect(Collectors.toList());
         if (orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), "噪声"))) {
            List<LocalDate> sampleCompleteDateList = orderList.stream()
               .<LocalDate>map(EmcOrderBean::getSampleCompleteDate)
               .distinct()
               .collect(Collectors.toList());
            return WordTemplateUtils.getDate(sampleBeginDateList, sampleCompleteDateList);
         } else {
            return WordTemplateUtils.getDate(sampleBeginDateList, true);
         }
      } else {
         return WordTemplateUtils.getDate(
            orderList.stream()
               .filter(order -> !ObjectUtils.isEmpty(order.getSendDate()))
               .map(EmcOrderBean::getSendDate)
               .distinct()
               .collect(Collectors.toList()),
            true
         );
      }
   }

   protected String getAnalysisDate(WordTemplateSourceDTO templateSourceDTO) {
      List<EmcRunBean> runList = templateSourceDTO.getRunList();
      List<LocalDate> startDateList = runList.stream()
         .filter(run -> !ObjectUtils.isEmpty(run.getAnalysisBeginDate()))
         .<LocalDate>map(EmcRunBean::getAnalysisBeginDate)
         .distinct()
         .collect(Collectors.toList());
      List<LocalDate> endDateList = runList.stream()
         .filter(run -> !ObjectUtils.isEmpty(run.getAnalysisEndDate()))
         .<LocalDate>map(EmcRunBean::getAnalysisEndDate)
         .distinct()
         .collect(Collectors.toList());
      return WordTemplateUtils.getDate(startDateList, endDateList);
   }

   public void writeDataToReport(File templateFile, XWPFDocument document, EmcReportBean report, EmcProjectBean project) {
      List<EmcReportLineBean> reportLineList = this.reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId", new Order[0]);
      List<Long> reportFolderIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getFolderId).distinct().collect(Collectors.toList());
      List<Long> reportOrderTaskIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderTaskId).collect(Collectors.toList());
      if (!reportFolderIdList.isEmpty() && !reportOrderTaskIdList.isEmpty()) {
         List<EmcFolderBean> allFolderList = this.folderService
            .selectListByFilter(
               SearchFilter.instance().match("id", reportFolderIdList).filter(MatchPattern.OR).match("termination", "0").filter(MatchPattern.EQ), new Order[0]
            );
         List<Long> allFolderIdList = allFolderList.stream().<Long>map(EmcFolderBean::getId).collect(Collectors.toList());
         List<EmcSampleBean> sampleList = this.sampleService
            .getDao()
            .selectListByOneColumnValue(
               project.getId(),
               "PROJID",
               Arrays.asList("ID", "SAMPLINGEQUIPTID", "SAMPLINGEQUIPTNAME", "ACCEPTEDDATE", "ACCEPTEUSERNAME", "ORDERCATEGORY"),
               new Order[0]
            );
         List<String> qcCategoryCodeList = new ArrayList<>();
         qcCategoryCodeList.add("N/A");
         String bizCategoryCode = project.getBizCategoryCode();
         boolean isDioxin = StringUtils.equalsIgnoreCase(bizCategoryCode, "C");
         if (isDioxin) {
            qcCategoryCodeList.add("P-DUP");
         }

         boolean sampling = StringUtils.equals("1", project.getSample());
         List<EmcOrderBean> orderList = sampling
            ? this.orderService
               .selectListByFilter(
                  SearchFilter.instance()
                     .match("folderId", allFolderIdList)
                     .filter(MatchPattern.OR)
                     .match("termination", "0")
                     .filter(MatchPattern.EQ)
                     .match("QCCATEGORYCODE", qcCategoryCodeList)
                     .filter(MatchPattern.OR),
                  new Order[0]
               )
            : this.orderService.getDao().selectListByOneColumnValues(allFolderIdList, "folderId", new Order[0]);
         List<Long> folderIdList = orderList.stream().<Long>map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
         List<EmcFolderBean> folderList = allFolderList.stream().filter(f -> folderIdList.contains(f.getId())).collect(Collectors.toList());
         List<EmcOrderTaskBean> orderTaskList = this.orderTaskService
            .selectListByFilter(SearchFilter.instance().match("id", reportOrderTaskIdList).filter(MatchPattern.OR), new Order[0]);
         List<EmcOrderTaskResultBean> orderTaskResultList = this.orderTaskResultService
            .selectListByFilter(SearchFilter.instance().match("orderTaskId", reportOrderTaskIdList).filter(MatchPattern.OR), new Order[0]);
         List<String> runIdList = orderTaskList.stream()
            .filter(a -> !StringUtils.isEmpty(a.getRunId()))
            .<String>map(EmcOrderTaskBean::getRunId)
            .distinct()
            .collect(Collectors.toList());
         List<Long> testMethodIdList = orderTaskList.stream()
            .filter(task -> !ObjectUtils.isEmpty(task.getTestMethodId()))
            .<Long>map(EmcOrderTaskBean::getTestMethodId)
            .distinct()
            .collect(Collectors.toList());
         List<EmcItemGroupBean> itemGroupList = this.itemGroupService
            .getDao()
            .selectListByOneColumnValues(testMethodIdList, "testMethodId", Arrays.asList("ID", "TESTMETHODID", "ITEMGROUP", "ACTIVATEDFLAG"), new Order[0]);
         List<Long> itemGroupIdList = itemGroupList.stream()
            .filter(group -> "1".equals(group.getActivatedFlag()))
            .<Long>map(EmcItemGroupBean::getId)
            .collect(Collectors.toList());
         List<EmcGroupItemBean> groupItemList = this.groupItemService
            .getDao()
            .selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Arrays.asList("ITEMGROUPID", "ITEMID"), new Order[0]);
         Map<Long, String> itemGroupMap = itemGroupList.stream()
            .filter(g -> !StringUtils.isEmpty(g.getItemGroup()))
            .collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));
         List<EmcRunBean> runList = new ArrayList<>();
         if (!runIdList.isEmpty()) {
            runList = this.runService.selectListByFilter(SearchFilter.instance().match("Id", runIdList).filter(MatchPattern.OR), new Order[0]);
         }

         List<String> dataTypeList = Arrays.asList("sample", "dayaverage", "totalaverage");
         List<EmcReportResultBean> reportBeforeResultList = this.reportResultService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("reportId", report.getId())
                  .filter(MatchPattern.SEQ)
                  .match("reportDisplay", "1")
                  .filter(MatchPattern.EQ)
                  .match("DATATYPE", dataTypeList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         ArrayList<EmcReportResultBean> reportResultList = reportBeforeResultList.stream()
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(r -> r.getId()))), ArrayList::new));
         List<EmcTestMethodBean> testMethodList = this.testMethodService
            .getDao()
            .selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "reportLimitType"), new Order[0]);
         testMethodList.forEach(
            testMethod -> reportResultList.stream()
                  .filter(r -> ObjectUtils.equals(r.getTestMethodId(), testMethod.getId()))
                  .forEach(r -> r.setExt$Item("reportLimitType", testMethod.getReportLimitType()))
         );
         reportResultList.stream()
            .filter(r -> "1".equals(r.getReportDisplay()))
            .forEach(
               r -> groupItemList.stream()
                     .filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId()))
                     .findFirst()
                     .ifPresent(g -> r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId())))
            );
         List<String> monitorTmplIdList = orderList.stream()
            .filter(f -> !StringUtils.isEmpty(f.getMonitorTmplId()))
            .<String>map(EmcOrderBean::getMonitorTmplId)
            .distinct()
            .collect(Collectors.toList());
         List<EmcMonitorTmplBean> monitorTmplList = this.monitorTmplService.getDao().selectListByIds(monitorTmplIdList, new Order[0]);
         List<Long> sampleIdList = reportResultList.stream().<Long>map(EmcReportResultBean::getSampleId).distinct().collect(Collectors.toList());
         List<EmcPropertyDataBean> propertyDataList = this.propertyDataService.getDao().selectListByOneColumnValues(sampleIdList, "bizId", new Order[0]);
         List<String> targetIdList = sampleIdList.stream().map(i -> "T_EMC_SAMPLE$" + i).collect(Collectors.toList());
         List<CoreFileBean> coreFileList = new ArrayList<>();
         if (!targetIdList.isEmpty()) {
            coreFileList = this.coreFileService
               .selectListByFilter(
                  SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR).match("BIZCATEGORY", "photo").filter(MatchPattern.EQ),
                  new Order[0]
               );
         }

         List<EmcReportBean> reportList = this.getDao().selectListByOneColumnValue(project.getId(), "PROJID", new Order[0]);
         List<String> sampleEquipmentIdList = orderTaskList.stream()
            .filter(task -> !StringUtils.isEmpty(task.getSamplingEquiptId()))
            .<String>map(EmcOrderTaskBean::getSamplingEquiptId)
            .collect(Collectors.toList());
         List<Long> equiptIdList = orderTaskList.stream()
            .filter(task -> !ObjectUtils.isEmpty(task.getEquiptId()))
            .<Long>map(EmcOrderTaskBean::getEquiptId)
            .collect(Collectors.toList());
         List<Long> parsedEquiptIdList = this.parseMultiEquipt(sampleEquipmentIdList);
         parsedEquiptIdList.addAll(equiptIdList);
         List<String> weighingEquiptIdList = runList.stream()
            .filter(run -> !StringUtils.isEmpty(run.getExt$Item("weighingEquiptId")))
            .map(run -> run.getExt$Item("weighingEquiptId"))
            .distinct()
            .collect(Collectors.toList());
         parsedEquiptIdList.addAll(this.parseMultiEquipt(weighingEquiptIdList));
         List<EmcEquiptBean> equiptList = this.equiptService
            .getDao()
            .selectListByIds(parsedEquiptIdList, Arrays.asList("ID", "EQUIPTID", "EQUIPTNAME", "SPECIFICATION"), new Order[0]);
         CoreUserBean loginUser = LocalContextHelper.getLoginUser();
         String parentOrgId = loginUser.getExt$Item("parentOrgId");
         CoreOrgBean coreOrg = (CoreOrgBean)this.coreOrgService.selectById(parentOrgId);
         List<CoreFileBean> logoFileList = this.coreFileService.getDao().selectListByOneColumnValue("T_CORE_ORG$" + parentOrgId, "targetId", new Order[0]);
         if (!logoFileList.isEmpty()) {
            logoFileList.forEach(file -> file.setExt$Item("orgLogo", "1"));
            coreFileList.addAll(logoFileList);
         }

         List<CoreFileBean> qrCodeFileList = this.coreFileService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("targetId", "T_EMC_PROJECT$" + project.getId())
                  .filter(MatchPattern.SEQ)
                  .match("bizCategory", Arrays.asList("qrCode", "photo"))
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         if (!qrCodeFileList.isEmpty()) {
            coreFileList.addAll(qrCodeFileList);
         }

         List<String> codeCategoryList = Collections.singletonList("testitemsort");
         List<CoreCodeBean> coreCodeList = this.coreCodeService
            .getDao()
            .selectListByOneColumnValues(codeCategoryList, "codeCategoryId", Arrays.asList("id", "codeName", "codeCategoryId", "code"), new Order[0]);
         List<CoreCodeBean> pretreatmentCodeList = this.coreCodeService
            .getDao()
            .selectListByOneColumnValue("pretreatmentRemark", "codeCategoryId", new Order[0]);
         pretreatmentCodeList.stream()
            .filter(c -> StringUtils.equals(c.getCode(), project.getPretreatment()))
            .findFirst()
            .ifPresent(c -> project.setPretreatment(c.getCodeName()));
         Map<String, Long> testItemSortNumMap = coreCodeList.stream()
            .filter(code -> StringUtils.equals(code.getCodeCategoryId(), "testitemsort"))
            .collect(Collectors.toMap(CoreCodeBean::getCode, code -> Optional.ofNullable(code.getOrderNo()).orElse(code.getId()), (v1, v2) -> v1));
         Map<Long, EmcOrderTaskBean> hasItemSortTaskMap = orderTaskList.stream()
            .filter(task -> !StringUtils.isEmpty(task.getExt$Item("testitemsort")))
            .collect(Collectors.toMap(EmcOrderTaskBean::getId, Function.identity(), (v1, v2) -> v1));
         Set<Long> hasItemSortTaskIdSet = hasItemSortTaskMap.keySet();
         orderTaskList.stream()
            .filter(task -> hasItemSortTaskIdSet.contains(task.getId()))
            .forEach(task -> task.setExt$Item("testItemSortNum", String.valueOf(testItemSortNumMap.get(task.getExt$Item("testItemSort")))));
         reportResultList.stream().filter(result -> hasItemSortTaskIdSet.contains(result.getOrderTaskId())).forEach(result -> {
            EmcOrderTaskBean orderTask = hasItemSortTaskMap.get(result.getOrderTaskId());
            result.setExt$Item("testItemSortNum", String.valueOf(testItemSortNumMap.get(orderTask.getExt$Item("testItemSort"))));
         });
         List<EmcMonitorSchemeBean> monitorSchemeList = this.monitorSchemeService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("monitorTmplId", monitorTmplIdList)
                  .filter(MatchPattern.OR)
                  .match("schemeName", "default")
                  .filter(MatchPattern.SEQ)
                  .match("testMethodId", testMethodIdList)
                  .filter(MatchPattern.OR),
               new Order[0]
            );
         List<EmcMonitorSchemeBean> hasSortNumSchemeList = monitorSchemeList.stream()
            .filter(scheme -> !ObjectUtils.isEmpty(scheme.getSortNum()))
            .collect(Collectors.toList());
         List<String> hasSortNumItemGroupList = hasSortNumSchemeList.stream()
            .<String>map(EmcMonitorSchemeBean::getItemGroup)
            .distinct()
            .collect(Collectors.toList());
         Map<String, Integer> itemGroupSortNumMap = hasSortNumSchemeList.stream()
            .collect(Collectors.toMap(EmcMonitorSchemeBean::getItemGroup, EmcMonitorSchemeBean::getSortNum, (v1, v2) -> v1));
         orderTaskList.stream()
            .filter(task -> hasSortNumItemGroupList.contains(task.getExt$Item("itemGroup")))
            .forEach(task -> task.setExt$Item("schemeSortNum", String.valueOf(itemGroupSortNumMap.get(task.getExt$Item("itemGroup")))));
         reportResultList.stream()
            .filter(result -> hasSortNumItemGroupList.contains(result.getExt$Item("itemGroup")))
            .forEach(result -> result.setExt$Item("schemeSortNum", String.valueOf(itemGroupSortNumMap.get(result.getExt$Item("itemGroup")))));
         WordTemplateSourceDTO templateSourceDTO = new WordTemplateSourceDTO();
         templateSourceDTO.setProject(project);
         templateSourceDTO.setFolderList(folderList);
         templateSourceDTO.setSampleList(sampleList);
         templateSourceDTO.setOrderList(orderList);
         templateSourceDTO.setOrderTaskList(orderTaskList);
         templateSourceDTO.setOrderTaskResultList(orderTaskResultList);
         templateSourceDTO.setItemGroupList(itemGroupList);
         templateSourceDTO.setGroupItemList(groupItemList);
         templateSourceDTO.setRunList(runList);
         templateSourceDTO.setReport(report);
         templateSourceDTO.setReportList(reportList);
         templateSourceDTO.setReportResultList(reportResultList);
         templateSourceDTO.setMonitorTmplList(monitorTmplList);
         templateSourceDTO.setPropertyDataList(propertyDataList);
         templateSourceDTO.setEquiptList(equiptList);
         templateSourceDTO.setCoreFileList(coreFileList);
         templateSourceDTO.setCoreOrg(coreOrg);
         templateSourceDTO.setCoreCodeList(coreCodeList);
         List<CrmOpportunityContractBean> opportunityContractList = this.opportunityContractService
            .getDao()
            .selectListByOneColumnValue(project.getExt$Item("contractNo"), "contractNo", Arrays.asList("id", "opportunityId"), new Order[0]);
         if (!opportunityContractList.isEmpty()) {
            CrmOpportunityMainBean crmOpportunityMain = (CrmOpportunityMainBean)this.opportunityMainService
               .getDao()
               .selectById(opportunityContractList.get(0).getOpportunityId());
            templateSourceDTO.setOpportunityMain(crmOpportunityMain);
         }

         List<PfRunTempHumidityBean> filterTempList = new ArrayList<>(runIdList.size());
         runIdList.forEach(runId -> {
            PfRunTempHumidityBean filterTemp = new PfRunTempHumidityBean();
            filterTemp.setRunId(runId);
            filterTemp.setProjId(project.getId());
            filterTempList.add(filterTemp);
         });
         List<PfRunTempHumidityBean> tempHumidityList = this.tempHumidityService
            .getDao()
            .selectList(filterTempList, Arrays.asList("runId", "projId"), Collections.emptyList(), new Order[0]);
         templateSourceDTO.setTempHumidityList(tempHumidityList);
         String var62 = templateFile.getName();
         switch (var62) {
            case "综合报告.docx":
               this.queryIndexCategory(templateSourceDTO);
               this.initQcReportDataSource(templateSourceDTO);
               if (!"1".equals(project.getSample())) {
                  templateSourceDTO.getFolderList()
                     .forEach(
                        folder -> templateSourceDTO.getOrderList()
                              .stream()
                              .filter(order -> ObjectUtils.equals(order.getFolderId(), folder.getId()))
                              .findFirst()
                              .ifPresent(order -> folder.setMonitorTmplId(order.getMonitorTmplId()))
                     );
               }

               ComplexWordTemplate complexWordTemplate = new ComplexWordTemplate(document, templateSourceDTO);
               complexWordTemplate.generate();
               break;
            case "质控报告.docx":
               this.initQcReportDataSource(templateSourceDTO);
               QualityControlWordTemplate qualityControlWordTemplate = new QualityControlWordTemplate(document, templateSourceDTO);
               qualityControlWordTemplate.generate();
               break;
            case "二噁英报告.docx":
               this.queryIndexCategory(templateSourceDTO);
               DioxinWordTemplate2 dioxinWordTemplate = new DioxinWordTemplate2(document, templateSourceDTO);
               dioxinWordTemplate.generate();
               break;
            case "二噁英质控报告.docx":
               this.initDioxinQcReportDataSource(templateSourceDTO);
               DioxinQcWordTemplate dioxinQcWordTemplate = new DioxinQcWordTemplate(document, templateSourceDTO);
               dioxinQcWordTemplate.generate();
         }

         folderList.clear();
         sampleList.clear();
         orderList.clear();
         orderTaskList.clear();
         itemGroupList.clear();
         orderTaskResultList.clear();
         tempHumidityList.clear();
         propertyDataList.clear();
         reportResultList.clear();
         groupItemList.clear();
         coreFileList.clear();
         coreCodeList.clear();
      }
   }

   @Transactional
   public void createMergeReport(RestJsonWrapperBean wrapper) {
      List<EmcFolderBean> selectedFolderList = wrapper.parse(EmcFolderBean.class);
      List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
      List<Long> selectedTestIdList = selectedOrderTaskList.stream().<Long>map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());
      Long originProjId = Long.parseLong(wrapper.getFilterValue("projId"));
      EmcProjectBean projOriginBean = (EmcProjectBean)this.projectService.getDao().selectById(originProjId);
      if (!selectedFolderList.isEmpty()) {
         List<Long> folderIdList = selectedFolderList.stream().<Long>map(EmcFolderBean::getId).collect(Collectors.toList());
         EmcProjectBean project = (EmcProjectBean)this.projectService.selectById(selectedFolderList.get(0).getProjId());
         EmcReportBean newReport = new EmcReportBean();
         newReport.setId(ApplicationContextHelper.getNextIdentity());
         newReport.setProjId(originProjId);
         newReport.setStatus("reportEdit");
         newReport.setExt$Item("mergeflag", "1");
         newReport.setReportNo(projOriginBean.getProjNo());
         List<EmcOrderTaskBean> orderTaskList = this.orderTaskService
            .getDao()
            .selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "projId", "folderId", "orderId", "testId"), new Order[0]);
         List<EmcOrderTaskBean> validOrderTaskList = selectedTestIdList.isEmpty()
            ? orderTaskList
            : orderTaskList.stream().filter(task -> selectedTestIdList.contains(task.getTestId())).collect(Collectors.toList());
         List<EmcReportLineBean> reportLineList = new ArrayList<>(validOrderTaskList.size());
         validOrderTaskList.forEach(task -> {
            EmcReportLineBean reportLine = new EmcReportLineBean();
            reportLine.setId(ApplicationContextHelper.getNextIdentity());
            reportLine.setReportId(newReport.getId());
            reportLine.setProjId(task.getProjId());
            reportLine.setFolderId(task.getFolderId());
            reportLine.setOrderId(task.getOrderId());
            reportLine.setOrderTaskId(task.getId());
            reportLineList.add(reportLine);
         });
         this.getDao().insert(newReport);
         this.reportLineService.getDao().insert(reportLineList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public void generateMergeReportResult(RestJsonWrapperBean wrapper) {
      EmcReportBean report = (EmcReportBean)wrapper.parseUnique(EmcReportBean.class);
      List<EmcReportLineBean> reportLineList = this.reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId", new Order[0]);
      List<Long> orderTaskIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderTaskId).collect(Collectors.toList());
      if (!orderTaskIdList.isEmpty()) {
         EmcReportResultBean deletedReportResult = new EmcReportResultBean();
         deletedReportResult.setReportId(report.getId());
         this.reportResultService.getDao().deleteBy(Collections.singletonList(deletedReportResult), new String[]{"reportId"});
         List<List<Long>> naPartitionList = ListUtils.partition(orderTaskIdList, 200);
         List<EmcOrderTaskBean> orderTaskList = new ArrayList<>();
         IntStream.range(0, naPartitionList.size())
            .forEach(
               index -> {
                  List<Long> curNaPartitionList = naPartitionList.get(index);
                  List<EmcOrderTaskBean> taskList = this.orderTaskService
                     .getDao()
                     .selectListByOneColumnValues(curNaPartitionList, "id", Arrays.asList("id", "runId"), new Order[0]);
                  orderTaskList.addAll(taskList);
               }
            );
         List<Long> validOrderTaskIdList = orderTaskList.stream()
            .filter(task -> !StringUtils.equals(task.getExt$Item("virtual"), "1"))
            .<Long>map(EmcOrderTaskBean::getId)
            .collect(Collectors.toList());
         if (validOrderTaskIdList.size() > 0) {
            List<EmcOrderTaskResultBean> orderTaskResultList = this.orderTaskResultService
               .selectListByFilter(
                  SearchFilter.instance()
                     .match("orderTaskId", validOrderTaskIdList)
                     .filter(MatchPattern.OR)
                     .match("REPORTDISPLAY", "1")
                     .filter(MatchPattern.SEQ)
                     .match("RESULTSTATUS", "Done")
                     .filter(MatchPattern.SEQ),
                  new Order[0]
               );
            if (orderTaskResultList.isEmpty()) {
               return;
            }

            List<Long> orderIdList = reportLineList.stream().<Long>map(EmcReportLineBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderList = this.orderService
               .selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR), new Order[0]);
            Map<Long, EmcOrderBean> orderIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getId, Function.identity()));
            List<Long> itemIdList = orderTaskResultList.stream().<Long>map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
            List<EmcItemRevisionBean> itemRevisionList = this.itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID", new Order[0]);
            List<EmcItemBean> itemList = this.itemService.getDao().selectListByIds(itemIdList, new Order[0]);
            List<EmcReportResultBean> reportResultList = new ArrayList<>();
            boolean existLampblack = orderList.stream().anyMatch(order -> StringUtils.contains(order.getOrderCategory(), "油烟"));
            String bizCategoryId = report.getExt$Item("bizCategoryId");
            boolean isDioxin = StringUtils.equalsIgnoreCase(bizCategoryId, "C");
            boolean sampling = StringUtils.equals("1", report.getExt$Item("sample"));
            List<EmcOrderTaskResultBean> finalResultList = isDioxin
               ? this.calcDioxinParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList)
               : this.calcParallelAverageValue(sampling, orderIdList, orderTaskResultList, itemRevisionList, itemList);
            Map<Long, Long> taskReportResultIdMap = new HashMap<>();
            finalResultList.forEach(result -> {
               EmcReportResultBean reportResult = new EmcReportResultBean();
               reportResult.setId(ApplicationContextHelper.getNextIdentity());
               reportResult.setReportId(report.getId());
               reportResult.setDataType("sample");
               this.copyProperties(reportResult, orderIdMap.get(result.getOrderId()), result);
               reportResultList.add(reportResult);
               if (existLampblack) {
                  taskReportResultIdMap.put(result.getId(), reportResult.getId());
               }
            });
            boolean calcStatistic = "1".equals(report.getIfStatistics());
            if (calcStatistic) {
               Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
               totalTaskResultMap.forEach(
                  (key, resultList) -> {
                     resultList.forEach(result -> this.replaceNdHdValue(result, result.getReportResult()));
                     Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream()
                        .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                     dayResultListMap.forEach(
                        (day, dayResultList) -> {
                           List<String> dayResultValueList = dayResultList.stream()
                              .<String>map(EmcOrderTaskResultBean::getReportResult)
                              .collect(Collectors.toList());
                           dayResultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                              EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                              BeanUtils.copyProperties(dayResultList.get(0), totalResult, new String[0]);
                              totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));
                              this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                              EmcReportResultBean reportResult = new EmcReportResultBean();
                              reportResult.setId(ApplicationContextHelper.getNextIdentity());
                              reportResult.setReportId(report.getId());
                              reportResult.setDataType("dayaverage");
                              EmcOrderBean currentOrder = orderIdMap.get(((EmcOrderTaskResultBean)dayResultList.get(0)).getOrderId());
                              this.copyProperties(reportResult, currentOrder, totalResult);
                              reportResultList.add(reportResult);
                           });
                        }
                     );
                  }
               );
            }

            List<Long> invalidLampblackResultIdList = new ArrayList<>();
            if (calcStatistic) {
               Map<String, List<EmcOrderTaskResultBean>> totalTaskResultMap = finalResultList.stream()
                  .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getItemId() + "$" + e.getExt$Item("testname")));
               totalTaskResultMap.forEach(
                  (key, resultList) -> {
                     resultList.forEach(result -> this.replaceNdHdValue(result, result.getReportResult()));
                     List<String> resultValueList = resultList.stream().<String>map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());
                     EmcOrderBean currentOrder = orderIdMap.get(resultList.get(0).getOrderId());
                     if (resultList.size() == 1) {
                        EmcReportResultBean reportResult = new EmcReportResultBean();
                        reportResult.setId(ApplicationContextHelper.getNextIdentity());
                        reportResult.setReportId(report.getId());
                        reportResult.setDataType("totalaverage");
                        this.copyProperties(reportResult, currentOrder, resultList.get(0));
                        reportResultList.add(reportResult);
                     } else if (StringUtils.contains(currentOrder.getOrderCategory(), "油烟")) {
                        Map<String, List<EmcOrderTaskResultBean>> dayResultListMap = resultList.stream()
                           .collect(Collectors.groupingBy(e -> e.getFolderId() + "$" + e.getCycle()));
                        dayResultListMap.forEach((cycle, dayResultList) -> {
                           String lampblackAvgResult = this.calcLampblackTotalAvgResult((List<EmcOrderTaskResultBean>)dayResultList);
                           if (lampblackAvgResult != null) {
                              EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                              BeanUtils.copyProperties(dayResultList.get(0), totalResult, new String[0]);
                              totalResult.setOriginalResult(NumberUtils.formatNumber(NumberUtils.parseDouble(lampblackAvgResult)));
                              this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                              EmcReportResultBean reportResult = new EmcReportResultBean();
                              reportResult.setId(ApplicationContextHelper.getNextIdentity());
                              reportResult.setReportId(report.getId());
                              reportResult.setDataType("dayaverage");
                              this.copyProperties(reportResult, orderIdMap.get(dayResultList.get(0).getOrderId()), totalResult);
                              reportResultList.add(reportResult);
                           }
                        });
                        resultList.stream().filter(r -> "1".equals(r.getExt$Item("invalidresult"))).forEach(r -> invalidLampblackResultIdList.add(r.getId()));
                     } else {
                        resultValueList.stream().filter(NumberUtils::isNumber).mapToDouble(NumberUtils::parseDouble).average().ifPresent(avgResult -> {
                           EmcOrderTaskResultBean totalResult = new EmcOrderTaskResultBean();
                           BeanUtils.copyProperties(resultList.get(0), totalResult, new String[0]);
                           totalResult.setOriginalResult(NumberUtils.formatNumber(avgResult));
                           this.reviseOriginalValue(totalResult, itemRevisionList, itemList);
                           EmcReportResultBean reportResult = new EmcReportResultBean();
                           reportResult.setId(ApplicationContextHelper.getNextIdentity());
                           reportResult.setReportId(report.getId());
                           reportResult.setDataType("totalaverage");
                           this.copyProperties(reportResult, currentOrder, totalResult);
                           reportResultList.add(reportResult);
                        });
                     }
                  }
               );
            }

            if (!reportResultList.isEmpty()) {
               if (!invalidLampblackResultIdList.isEmpty()) {
                  List<Long> invalidReportResultIdList = new ArrayList<>();
                  invalidLampblackResultIdList.forEach(id -> invalidReportResultIdList.add(taskReportResultIdMap.get(id)));
                  reportResultList.stream()
                     .filter(result -> invalidReportResultIdList.contains(result.getId()))
                     .forEach(result -> result.setInvalidResult("1"));
               }

               this.reportResultService.getDao().insert(reportResultList);
            }
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public String cancelCompleteReport(RestJsonWrapperBean wrapper) {
      return null;
   }

   public EmcReportSendInfoBean selectSendInfo(Long projId) {
      List<EmcReportSendInfoBean> sendInfoList = this.reportSendInfoService
         .selectListByFilter(SearchFilter.instance().match("projId", projId).filter(MatchPattern.SEQ), new Order[0]);
      if (sendInfoList.isEmpty()) {
         EmcReportSendInfoBean reportSendInfo = new EmcReportSendInfoBean();
         reportSendInfo.setProjId(projId);
         reportSendInfo.setId(ApplicationContextHelper.getNextIdentity());
         this.reportSendInfoService.getDao().insert(reportSendInfo);
         return (EmcReportSendInfoBean)this.reportSendInfoService
            .selectFirstByFilter(SearchFilter.instance().match("projId", projId).filter(MatchPattern.SEQ), new Order[0]);
      } else {
         return sendInfoList.get(0);
      }
   }

   private void initQcReportDataSource(WordTemplateSourceDTO templateSourceDTO) {
      List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();
      List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
      List<Long> orderIdList = orderList.stream().<Long>map(EmcOrderBean::getId).collect(Collectors.toList());
      List<String> orderNoList = orderList.stream().<String>map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
      EmcOrderBean filterOrder = new EmcOrderBean();
      filterOrder.setProjId(orderList.get(0).getProjId());
      List<EmcOrderBean> projectOrderList = this.orderService
         .getDao()
         .selectList(filterOrder, Collections.singletonList("projId"), Collections.emptyList(), new Order[0]);
      List<EmcOrderBean> samplingQcOrderList = projectOrderList.stream()
         .filter(order -> !ObjectUtils.isEmpty(order.getQcCategoryId()))
         .collect(Collectors.toList());
      List<Long> samplingQcOrderIdList = samplingQcOrderList.stream().<Long>map(EmcOrderBean::getId).collect(Collectors.toList());
      List<String> samplingQcOrderNoList = samplingQcOrderList.stream().<String>map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
      orderIdList.addAll(samplingQcOrderIdList);
      orderNoList.addAll(samplingQcOrderNoList);
      List<EmcOrderTaskBean> samplingTaskBeforeList = new ArrayList<>();
      if (!samplingQcOrderIdList.isEmpty()) {
         samplingTaskBeforeList = this.orderTaskService
            .selectListByFilter(SearchFilter.instance().match("orderId", samplingQcOrderIdList).filter(MatchPattern.OR), new Order[0]);
      }

      List<EmcOrderTaskResultBean> samplingResultList = this.orderTaskResultService
         .selectListByFilter(SearchFilter.instance().match("orderId", samplingQcOrderIdList).filter(MatchPattern.OR), new Order[0]);
      List<Long> existQcOrderTaskIdList = samplingResultList.stream().<Long>map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
      List<EmcOrderTaskBean> samplingTaskList = samplingTaskBeforeList.stream()
         .filter(ok -> existQcOrderTaskIdList.contains(ok.getId()))
         .collect(Collectors.toList());
      List<EmcQcRelatedOrderBean> qcRelatedOrderList = this.qcRelatedOrderService
         .getDao()
         .selectListByOneColumnValues(orderIdList, "relatedOrderId", new Order[0]);
      List<Long> qcRelatedOrderIdList = qcRelatedOrderList.stream().<Long>map(EmcQcRelatedOrderBean::getQcOrderId).distinct().collect(Collectors.toList());
      List<Long> orderTaskIdList = orderTaskList.stream().<Long>map(EmcOrderTaskBean::getId).collect(Collectors.toList());
      List<Long> testMethodIdList = orderTaskList.stream().<Long>map(EmcOrderTaskBean::getTestMethodId).collect(Collectors.toList());
      List<String> runIdList = orderTaskList.stream()
         .filter(ok -> !StringUtils.isEmpty(ok.getRunId()))
         .<String>map(EmcOrderTaskBean::getRunId)
         .distinct()
         .collect(Collectors.toList());
      List<EmcOrderTaskBean> runTaskList = new ArrayList<>();
      if (!runIdList.isEmpty()) {
         runTaskList = this.orderTaskService.selectListByFilter(SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR), new Order[0]);
      }

      List<EmcOrderTaskBean> runQcTaskBeforeList = runTaskList.stream()
         .filter(
            task -> !ObjectUtils.isEmpty(task.getQcCategoryId())
                  && !orderTaskIdList.contains(task.getId())
                  && testMethodIdList.contains(task.getTestMethodId())
         )
         .collect(Collectors.toList());
      List<Long> runQcOrderIdBeforeList = runQcTaskBeforeList.stream().<Long>map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
      List<EmcOrderBean> allRunQcOrderList = this.orderService.getDao().selectListByIds(runQcOrderIdBeforeList, new Order[0]);
      List<EmcOrderBean> runQcOrderList = new ArrayList<>();
      List<EmcOrderBean> validRunQcOrderList1 = allRunQcOrderList.stream().filter(e -> e.getParentOrderNo() == null).collect(Collectors.toList());
      List<String> validRunQcOrderNoList1 = validRunQcOrderList1.stream().<String>map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
      List<EmcOrderBean> validRunQcOrderList4 = allRunQcOrderList.stream()
         .filter(e -> validRunQcOrderNoList1.contains(e.getParentOrderNo()))
         .collect(Collectors.toList());
      List<EmcOrderBean> validRunQcOrderList2 = allRunQcOrderList.stream().filter(e -> orderNoList.contains(e.getParentOrderNo())).collect(Collectors.toList());
      List<String> validRunQcOrderNoList2 = validRunQcOrderList2.stream().<String>map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
      List<EmcOrderBean> validRunQcOrderList3 = allRunQcOrderList.stream()
         .filter(e -> validRunQcOrderNoList2.contains(e.getParentOrderNo()))
         .collect(Collectors.toList());
      runQcOrderList.addAll(validRunQcOrderList1);
      runQcOrderList.addAll(validRunQcOrderList2);
      runQcOrderList.addAll(validRunQcOrderList3);
      runQcOrderList.addAll(validRunQcOrderList4);
      List<Long> runQcOrderIdList = runQcOrderList.stream().<Long>map(EmcOrderBean::getId).collect(Collectors.toList());
      List<EmcOrderTaskBean> runQcTaskList = runQcTaskBeforeList.stream().filter(ok -> runQcOrderIdList.contains(ok.getOrderId())).collect(Collectors.toList());
      List<Long> runQcTaskIdList = runQcTaskList.stream().<Long>map(EmcOrderTaskBean::getId).collect(Collectors.toList());
      List<EmcOrderTaskResultBean> runQcResultList = this.orderTaskResultService
         .selectListByFilter(SearchFilter.instance().match("orderTaskId", runQcTaskIdList).filter(MatchPattern.OR), new Order[0]);
      List<Long> existRunQcOrderTaskIdList = runQcResultList.stream().<Long>map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
      runQcTaskList = runQcTaskList.stream().filter(ok -> existRunQcOrderTaskIdList.contains(ok.getId())).collect(Collectors.toList());
      List<EmcOrderBean> reportOrderList = new ArrayList<>(orderList);
      List<EmcOrderTaskBean> reportTaskList = new ArrayList<>(orderTaskList);
      List<EmcOrderTaskResultBean> reportResultList = new ArrayList<>(templateSourceDTO.getOrderTaskResultList());
      templateSourceDTO.setOrderList(reportOrderList);
      templateSourceDTO.setOrderTaskList(reportTaskList);
      List<EmcOrderBean> qcOrderList = new ArrayList<>(samplingQcOrderList);
      qcOrderList.addAll(runQcOrderList);
      List<EmcOrderTaskBean> qcOrderTaskList = new ArrayList<>(samplingTaskList);
      qcOrderTaskList.addAll(runQcTaskList);
      List<EmcOrderTaskResultBean> qcTaskResultList = new ArrayList<>(samplingResultList);
      qcTaskResultList.addAll(runQcResultList);
      List<String> stockBatchNoList = qcOrderTaskList.stream()
         .filter(task -> !StringUtils.isEmpty(task.getStockBatchNo()))
         .<String>map(EmcOrderTaskBean::getStockBatchNo)
         .distinct()
         .collect(Collectors.toList());
      if (!stockBatchNoList.isEmpty()) {
         List<EmcStockBean> stockList = this.stockService
            .getDao()
            .selectListByOneColumnValues(stockBatchNoList, "batchNo", Arrays.asList("id", "batchNo", "standardLevel"), new Order[0]);
         List<EmcStockBean> validStockList = stockList.stream()
            .filter(stock -> !StringUtils.isEmpty(stock.getExt$Item("standardLevel")))
            .collect(Collectors.toList());
         Map<String, String> stockBatchNoMap = validStockList.stream()
            .collect(Collectors.toMap(EmcStockBean::getBatchNo, stock -> stock.getExt$Item("standardLevel"), (v1, v2) -> v1));
         qcOrderTaskList.stream()
            .filter(task -> !StringUtils.isEmpty(task.getStockBatchNo()))
            .forEach(task -> task.setExt$Item("uniqueNo", stockBatchNoMap.get(task.getStockBatchNo())));
      }

      List<EmcOrderTaskResultBean> differentUnitResultList = qcTaskResultList.stream()
         .filter(
            r -> "QC-STD".equals(r.getQcCategoryCode())
                  && !StringUtils.isEmpty(r.getRn12())
                  && !StringUtils.equals(r.getUnitName(), r.getRn12())
                  && NumberUtils.isNumber(r.getReportResult())
         )
         .collect(Collectors.toList());
      if (!differentUnitResultList.isEmpty()) {
         Map<String, List<Pair<String, Double>>> unitConvertMap = this.queryUnitConvert();
         qcTaskResultList.stream()
            .filter(
               r -> "QC-STD".equals(r.getQcCategoryCode())
                     && !StringUtils.isEmpty(r.getRn12())
                     && !StringUtils.equals(r.getUnitName(), r.getRn12())
                     && NumberUtils.isNumber(r.getReportResult())
            )
            .forEach(r -> {
               String finalValue = WordTemplateUtils.convertUnit(unitConvertMap, r.getUnitName(), r.getReportResult(), r.getRn12());
               r.setReportResult(finalValue);
            });
      }

      ArrayList<EmcOrderBean> uniqueQcOrderList = new ArrayList<>(qcOrderList.stream()
         .collect(Collectors.toCollection(() -> new TreeSet<EmcOrderBean>(Comparator.comparing(EmcOrderBean::getId)))));
      ArrayList<EmcOrderTaskBean> uniqueQcOrderTaskList = new ArrayList<>(qcOrderTaskList.stream()
         .collect(Collectors.toCollection(() -> new TreeSet<EmcOrderTaskBean>(Comparator.comparing(EmcOrderTaskBean::getId)))));
      ArrayList<EmcOrderTaskResultBean> uniqueQcOrderTaskResultList = new ArrayList<>(qcTaskResultList.stream()
         .collect(Collectors.toCollection(() -> new TreeSet<EmcOrderTaskResultBean>(Comparator.comparing(EmcOrderTaskResultBean::getId)))));
      templateSourceDTO.setQcOrderList(uniqueQcOrderList);
      templateSourceDTO.setQcOrderTaskList(uniqueQcOrderTaskList);
      List<EmcItemGroupBean> itemGroupList = templateSourceDTO.getItemGroupList();
      List<EmcTestCapacityBean> testCapacityList = templateSourceDTO.getTestCapacityList();
      List<EmcItemGroupBean> allItemGroupList = new ArrayList<>();
      List<EmcGroupItemBean> allGroupItemList = new ArrayList<>();
      List<Long> commonTestMethodIdList = itemGroupList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());
      List<Long> qcTestMethodIdList = uniqueQcOrderTaskResultList.stream()
         .filter(e -> !commonTestMethodIdList.contains(e.getTestMethodId()))
         .map(e -> e.getTestMethodId())
         .distinct()
         .collect(Collectors.toList());
      List<EmcItemGroupBean> qcItemGroupList = this.itemGroupService
         .getDao()
         .selectListByOneColumnValues(qcTestMethodIdList, "testMethodId", Arrays.asList("ID", "TESTMETHODID", "ITEMGROUP", "ACTIVATEDFLAG"), new Order[0]);
      allItemGroupList.addAll(itemGroupList);
      allItemGroupList.addAll(qcItemGroupList);
      Map<Long, String> itemGroupMap = allItemGroupList.stream()
         .filter(g -> !StringUtils.isEmpty(g.getItemGroup()))
         .collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));
      Map<Long, Integer> itemGroupSortMap = allItemGroupList.stream()
         .filter(g -> g.getSortNo() != null)
         .collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getSortNo, (v1, v2) -> v1));
      List<Long> qcItemGroupIdList = qcItemGroupList.stream()
         .filter(group -> "1".equals(group.getActivatedFlag()))
         .<Long>map(EmcItemGroupBean::getId)
         .collect(Collectors.toList());
      List<EmcGroupItemBean> qcGroupItemList = this.groupItemService
         .getDao()
         .selectListByOneColumnValues(qcItemGroupIdList, "ITEMGROUPID", Arrays.asList("ITEMGROUPID", "ITEMID"), new Order[0]);
      List<EmcGroupItemBean> groupItemList = templateSourceDTO.getGroupItemList();
      allGroupItemList.addAll(groupItemList);
      allGroupItemList.addAll(qcGroupItemList);
      uniqueQcOrderTaskResultList.forEach(
         r -> allGroupItemList.stream()
               .filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId()))
               .findFirst()
               .ifPresent(g -> r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId())))
      );
      reportResultList.stream()
         .filter(r -> "1".equals(r.getReportDisplay()))
         .forEach(r -> groupItemList.stream().filter(g -> ObjectUtils.equals(r.getItemId(), g.getItemId())).findFirst().ifPresent(g -> {
               r.setExt$Item("itemGroup", itemGroupMap.get(g.getItemGroupId()));
               if (itemGroupSortMap.get(g.getItemGroupId()) != null) {
                  r.setExt$Item("itemGroupSort", itemGroupSortMap.get(g.getItemGroupId()) + "");
               } else {
                  r.setExt$Item("itemGroupSort", "0");
               }
            }));
      List<Long> testIdQcSearchList = uniqueQcOrderTaskResultList.stream().<Long>map(EmcOrderTaskResultBean::getTestId).distinct().collect(Collectors.toList());
      List<Long> testIdSearchList = reportResultList.stream().<Long>map(EmcOrderTaskResultBean::getTestId).distinct().collect(Collectors.toList());
      List<EmcTestBean> testMethodQcSearchList = this.testService.selectListByIds(testIdQcSearchList);
      List<EmcTestBean> testMethodSearchList = this.testService.selectListByIds(testIdSearchList);
      uniqueQcOrderTaskResultList.stream()
         .filter(r -> "1".equals(r.getReportDisplay()))
         .forEach(
            r -> {
               String testName = testMethodQcSearchList.stream()
                  .filter(tm -> ObjectUtils.equals(r.getTestId(), tm.getId()))
                  .collect(Collectors.toList())
                  .get(0)
                  .getTestName();
               r.setExt$Item("testName", testName);
            }
         );
      testCapacityList.forEach(
         testCapacity -> uniqueQcOrderTaskResultList.stream()
               .filter(r -> ObjectUtils.equals(r.getTestMethodId(), testCapacity.getTestMethodId()))
               .forEach(r -> r.setExt$Item("reportTestName", testCapacity.getReportTestName()))
      );
      uniqueQcOrderTaskResultList.stream()
         .filter(r -> StringUtils.isEmpty(r.getExt$Item("reportTestName")))
         .forEach(r -> r.setExt$Item("reportTestName", r.getExt$Item("testName")));
      templateSourceDTO.setQcTaskResultList(uniqueQcOrderTaskResultList);
      reportResultList.stream()
         .filter(r -> "1".equals(r.getReportDisplay()))
         .forEach(
            r -> {
               String testName = testMethodSearchList.stream()
                  .filter(tm -> ObjectUtils.equals(r.getTestId(), tm.getId()))
                  .collect(Collectors.toList())
                  .get(0)
                  .getTestName();
               r.setExt$Item("testName", testName);
            }
         );
      testCapacityList.forEach(
         testCapacity -> reportResultList.stream()
               .filter(r -> ObjectUtils.equals(r.getTestMethodId(), testCapacity.getTestMethodId()))
               .forEach(r -> r.setExt$Item("reportTestName", testCapacity.getReportTestName()))
      );
      reportResultList.stream()
         .filter(r -> StringUtils.isEmpty(r.getExt$Item("reportTestName")))
         .forEach(r -> r.setExt$Item("reportTestName", r.getExt$Item("testName")));
      templateSourceDTO.setOrderTaskResultList(reportResultList);
      templateSourceDTO.setQcRelatedOrderList(qcRelatedOrderList);
      this.queryItemQcIndex(templateSourceDTO);
      this.queryIndexCategory(templateSourceDTO);
      this.queryEquiptPerCheck(templateSourceDTO);
   }

   private void initDioxinQcReportDataSource(WordTemplateSourceDTO templateSourceDTO) {
      List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
      List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream()
         .filter(
            r -> !StringUtils.contains(r.getTestName(), "参数")
                  && !StringUtils.contains(r.getTestName(), "含水率")
                  && !StringUtils.contains(r.getExt$Item("relatedTest"), "1")
         )
         .collect(Collectors.toList());
      List<String> runIdList = validOrderTaskList.stream()
         .filter(a -> !StringUtils.isEmpty(a.getRunId()))
         .<String>map(EmcOrderTaskBean::getRunId)
         .distinct()
         .collect(Collectors.toList());
      List<EmcOrderTaskBean> qcOrderTaskBeforeList = runIdList.isEmpty()
         ? Collections.emptyList()
         : this.orderTaskService
            .selectListByFilter(
               SearchFilter.instance().match("runId", runIdList).filter(MatchPattern.OR).match("qcCategoryCode", "N/A").filter(MatchPattern.DIFFER),
               new Order[0]
            );
      List<Long> qcOrderTaskIdList = qcOrderTaskBeforeList.stream().<Long>map(EmcOrderTaskBean::getId).collect(Collectors.toList());
      List<EmcOrderTaskResultBean> qcTaskResultList = qcOrderTaskIdList.isEmpty()
         ? Collections.emptyList()
         : this.orderTaskResultService
            .selectListByFilter(SearchFilter.instance().match("orderTaskId", qcOrderTaskIdList).filter(MatchPattern.OR), new Order[0]);
      List<Long> existQcOrderTaskIdList = qcTaskResultList.stream().<Long>map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
      List<EmcOrderTaskBean> qcOrderTaskList = qcOrderTaskBeforeList.stream()
         .filter(ok -> existQcOrderTaskIdList.contains(ok.getId()))
         .collect(Collectors.toList());
      List<Long> qcOrderIdList = qcOrderTaskList.stream().<Long>map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
      List<EmcOrderBean> qcOrderList = this.orderService.getDao().selectListByIds(qcOrderIdList, new Order[0]);
      templateSourceDTO.setQcOrderList(qcOrderList);
      templateSourceDTO.setQcOrderTaskList(qcOrderTaskList);
      templateSourceDTO.setQcTaskResultList(qcTaskResultList);
   }

   private void queryItemQcIndex(WordTemplateSourceDTO templateSourceDTO) {
      List<EmcOrderTaskResultBean> qcTaskResultList = templateSourceDTO.getQcTaskResultList();
      List<Long> itemIdList = qcTaskResultList.stream().<Long>map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
      int batchSize = 1000;
      List<EmcItemQcIndexBean> itemQcIndexList = new ArrayList<>();

      for (int i = 0; i < itemIdList.size(); i += batchSize) {
         List<Long> subList = itemIdList.subList(i, Math.min(i + batchSize, itemIdList.size()));
         List<EmcItemQcIndexBean> batchResult = this.itemQcIndexService
            .getDao()
            .selectListByOneColumnValues(subList, "itemId", Arrays.asList("id", "itemId", "qcCategoryId", "DECISIONCATEGORY"), new Order[0]);
         itemQcIndexList.addAll(batchResult);
      }

      templateSourceDTO.setItemQcIndexList(itemQcIndexList);
   }

   private void queryIndexCategory(WordTemplateSourceDTO templateSourceDTO) {
      List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
      List<Long> indexCategoryIdList = orderTaskList.stream()
         .filter(task -> !ObjectUtils.isEmpty(task.getIndexCategoryId()))
         .<Long>map(EmcOrderTaskBean::getIndexCategoryId)
         .distinct()
         .collect(Collectors.toList());
      List<EmcIndexCategoryBean> indexCategoryList = this.indexCategoryService
         .getDao()
         .selectListByOneColumnValues(indexCategoryIdList, "id", Arrays.asList("id", "stdName"), new Order[0]);
      templateSourceDTO.setIndexCategoryList(indexCategoryList);
   }

   private void queryEquiptPerCheck(WordTemplateSourceDTO templateSourceDTO) {
      List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
      List<Long> equiptIdList = orderTaskList.stream()
         .filter(task -> !ObjectUtils.isEmpty(task.getEquiptId()))
         .<Long>map(EmcOrderTaskBean::getEquiptId)
         .distinct()
         .collect(Collectors.toList());
      List<EmcEquiptPerCheckPlanBean> checkPlanList = this.perCheckPlanService.getDao().selectListByOneColumnValues(equiptIdList, "equiptId", new Order[0]);
      List<EmcEquiptPerCheckPlanBean> validCheckPlanList = checkPlanList.stream()
         .filter(plan -> !ObjectUtils.isEmpty(plan.getMaintEndDate()))
         .collect(Collectors.toList());
      Map<Long, String> checkPlanMap = validCheckPlanList.stream()
         .collect(Collectors.toMap(EmcEquiptPerCheckPlanBean::getEquiptId, plan -> DateTimeUtils.formatLocalDateTime(plan.getMaintEndDate()), (v1, v2) -> v1));
      orderTaskList.forEach(task -> Optional.ofNullable(checkPlanMap.get(task.getEquiptId())).ifPresent(date -> task.setExt$Item("checkDate", date)));
      templateSourceDTO.setOrderTaskList(orderTaskList);
   }

   private Map<String, List<Pair<String, Double>>> queryUnitConvert() {
      Map<String, List<Pair<String, Double>>> convertMap = new HashMap<>();
      List<EmcUnitConvertBean> unitConvertList = this.unitConvertService.getDao().selectAll();
      List<EmcUnitConvertBean> validUnitConvertList = unitConvertList.stream()
         .filter(uc -> !StringUtils.isEmpty(uc.getUnitId()) && !StringUtils.isEmpty(uc.getConvertUnitId()) && !ObjectUtils.isEmpty(uc.getConvertFactor()))
         .collect(Collectors.toList());
      List<String> sourceUnitIdList = validUnitConvertList.stream().<String>map(EmcUnitConvertBean::getUnitId).distinct().collect(Collectors.toList());
      List<String> targetUnitIdList = validUnitConvertList.stream().<String>map(EmcUnitConvertBean::getConvertUnitId).distinct().collect(Collectors.toList());
      List<String> unitIdList = new ArrayList<>(sourceUnitIdList);
      unitIdList.addAll(targetUnitIdList);
      List<EmcUnitBean> unitList = this.unitService.selectListByIds(unitIdList);
      Map<String, EmcUnitBean> unitMap = unitList.stream().collect(Collectors.toMap(EmcUnitBean::getId, Function.identity()));
      Map<String, List<EmcUnitConvertBean>> unitConvertMap = validUnitConvertList.stream().collect(Collectors.groupingBy(EmcUnitConvertBean::getUnitId));
      unitConvertMap.forEach(
         (sourceUnitId, curConvertList) -> Optional.ofNullable(unitMap.get(sourceUnitId))
               .ifPresent(
                  unit -> {
                     List<Pair<String, Double>> convertList = new ArrayList<>();
                     curConvertList.forEach(
                        uc -> Optional.ofNullable(unitMap.get(uc.getConvertUnitId()))
                              .ifPresent(convertUnit -> convertList.add(Pair.of(convertUnit.getUnitName(), uc.getConvertFactor())))
                     );
                     convertMap.put(unit.getUnitName(), convertList);
                  }
               )
      );
      return convertMap;
   }

   public EmcReportRecordDTO selectReportRecordDTO(RestJsonWrapperBean wrapper) {
      EmcReportRecordDTO reportRecordDTO = new EmcReportRecordDTO();
      String reportId = wrapper.getParamValue("reportId");
      String targetId = this.getDao().getTable() + "$" + reportId;
      CoreFileBean wordReportFile = (CoreFileBean)this.coreFileService
         .selectFirstByFilter(
            SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.SEQ).match("bizCategory", "wordReport").filter(MatchPattern.EQ),
            new Order[0]
         );
      reportRecordDTO.setReportFile(wordReportFile);
      List<CoreFileBean> otherRecordList = new ArrayList<>();
      List<CoreFileBean> analysisRecordList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.SEQ).match("ext", "analysisRecordPdf").filter(MatchPattern.EQ),
            new Order[0]
         );
      otherRecordList.addAll(analysisRecordList);
      List<CoreFileBean> sampleRecordList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.SEQ).match("ext", "sampleRecordPdf").filter(MatchPattern.EQ), new Order[0]
         );
      otherRecordList.addAll(sampleRecordList);
      reportRecordDTO.setOtherRecordList(otherRecordList);
      return reportRecordDTO;
   }

   public Page<EmcReportSendInfoBean> selectSendInfoPagination(RestJsonWrapperBean wrapper) {
      return this.reportSendInfoService.selectPagination(wrapper);
   }

   public CoreFileBean composeProjectRunRecord(RestJsonWrapperBean wrapper) {
      EmcProjectBean project = (EmcProjectBean)wrapper.parseUnique(EmcProjectBean.class);
      CoreFileBean fileFilter = new CoreFileBean();
      fileFilter.setTargetId("T_EMC_PROJECT$" + project.getId());
      fileFilter.setExt("analysisRecordPdf");
      List<CoreFileBean> projectRunRecordFiles = this.coreFileService
         .getDao()
         .selectList(fileFilter, Arrays.asList("targetId", "ext"), Collections.emptyList(), new Order[0]);
      if (!projectRunRecordFiles.isEmpty()) {
         return projectRunRecordFiles.get(0);
      } else {
         EmcOrderTaskBean orderTaskFilter = new EmcOrderTaskBean();
         orderTaskFilter.setProjId(project.getId());
         orderTaskFilter.setAnalysisType("analysis");
         List<EmcOrderTaskBean> orderTaskList = this.orderTaskService
            .getDao()
            .selectList(orderTaskFilter, Arrays.asList("projId", "analysisType"), Collections.emptyList(), new Order[0]);
         List<EmcOrderTaskBean> distinctOrderTaskList = orderTaskList.stream()
            .filter(e -> !ObjectUtils.isEmpty(e.getProjId()) && !ObjectUtils.isEmpty(e.getRunId()))
            .collect(
               Collectors.collectingAndThen(
                  Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProjId() + "#" + o.getRunId()))), ArrayList::new
               )
            );
         List<CoreFileBean> fileFilters = new ArrayList<>();
         distinctOrderTaskList.forEach(r -> {
            CoreFileBean coreFileBeanx = new CoreFileBean();
            coreFileBeanx.setTargetId("T_EMC_RUN$" + r.getRunId() + "-" + r.getProjId());
            coreFileBeanx.setExt("composePdf");
            fileFilters.add(coreFileBeanx);
         });
         List<CoreFileBean> allRunFileList = this.coreFileService
            .getDao()
            .selectList(fileFilters, Arrays.asList("TARGETID", "EXT"), Collections.emptyList(), new Order[0]);
         if (org.thymeleaf.util.ListUtils.isEmpty(allRunFileList)) {
            return null;
         } else {
            List<FilePathDTO> fileRepresentations = allRunFileList.stream().<FilePathDTO>map(CoreFileUtils::toFilePath).collect(Collectors.toList());
            List<FilePathDTO> validFileRepresentations = fileRepresentations.stream()
               .filter(fr -> FilePathManager.getLocalDirPath(fr).toFile().exists())
               .collect(Collectors.toList());
            if (validFileRepresentations.isEmpty()) {
               return null;
            } else {
               String projectReportPdfName = "分析记录-" + project.getProjNo() + ".pdf";
               FilePathDTO composedPdfFile = FilePathDTO.of(
                  FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName
               );
               this.coreFileService.mergePdfList(validFileRepresentations, composedPdfFile);
               String tempFilePath = FilePathManager.getLocalPath(composedPdfFile).toString();
               if (!StringUtils.isEmpty(tempFilePath)) {
                  CoreFileBean coreFileBean = new CoreFileBean();
                  coreFileBean.setTargetId("T_EMC_PROJECT$" + project.getId());
                  coreFileBean.setScope(FileScope.secure.name());
                  coreFileBean.setExt("analysisRecordPdf");
                  Long fileId = this.coreFileService.upload(coreFileBean, new File(tempFilePath));
                  return (CoreFileBean)this.coreFileService.getDao().selectById(fileId);
               } else {
                  return null;
               }
            }
         }
      }
   }

   @Transactional
   public void seal(RestJsonWrapperBean wrapper) {
      List<CoreFileBean> coreFileList = wrapper.parse(CoreFileBean.class);
      this.seal(coreFileList);
   }

   @Transactional
   public void editItem(RestJsonWrapperBean wrapper) {
      List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
      List<EmcItemBean> itemList = wrapper.parse(EmcItemBean.class);
      List<Long> orderIdList = orderTaskResultList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
      Long testMethodId = orderTaskResultList.get(0).getTestMethodId();
      List<EmcOrderBean> orderList = this.orderService.getDao().selectListByIds(orderIdList, new Order[0]);
      Map<Long, List<EmcOrderTaskResultBean>> orderTaskResultMap = this.orderTaskResultService
         .selectListByFilter(
            SearchFilter.instance()
               .match("ORDERID", orderIdList)
               .filter(MatchPattern.OR)
               .match("testMethodId", testMethodId)
               .filter(MatchPattern.SEQ)
               .match("AnalysisType", "calculate")
               .filter(MatchPattern.SEQ),
            new Order[0]
         )
         .stream()
         .collect(Collectors.groupingBy(b -> b.getOrderId()));
      List<EmcOrderTaskResultBean> addList = new ArrayList<>();
      List<EmcOrderTaskResultBean> delList = new ArrayList<>();
      orderTaskResultMap.forEach((key, value) -> {
         List<EmcOrderTaskResultBean> tempList = new ArrayList<>();
         Long sampleNum = orderList.stream().filter(a -> ObjectUtils.equals(key, a.getId())).findAny().orElse(null).getSampleNum();
         value.stream().forEach(a -> {
            if (itemList.stream().anyMatch(b -> ObjectUtils.equals(a.getItemId(), b.getId()))) {
               tempList.add(a);
            }
         });
         value.stream().forEach(a -> {
            if (tempList.stream().noneMatch(b -> ObjectUtils.equals(a.getItemId(), b.getItemId()))) {
               delList.add(a);
            }
         });
         itemList.stream().forEach(a -> {
            if (tempList.stream().noneMatch(b -> ObjectUtils.equals(a.getId(), b.getItemId()))) {
               Long repeatItem = a.getRepeatItem() == null ? EmcConstant.ONE : a.getRepeatItem();
               EmcOrderTaskResultBean orderTaskResult = (EmcOrderTaskResultBean)value.get(0);
               String pnFunc = a.getPnFunc();
               if (!"peraverage".equalsIgnoreCase(pnFunc) && !"dayaverage".equalsIgnoreCase(pnFunc) || ObjectUtils.equals(1L, sampleNum)) {
                  for (int i = 1; (long)i <= repeatItem; i++) {
                     EmcOrderTaskResultBean orderTaskResultBean = new EmcOrderTaskResultBean();
                     BeanUtils.copyProperties(orderTaskResult, orderTaskResultBean, EmcConstant.CREATED_COLUMNS);
                     orderTaskResultBean.setTestMethodId(a.getTestMethodId());
                     orderTaskResultBean.setItemId(a.getId());
                     orderTaskResultBean.setItemOrderNo(a.getOrderNo());
                     orderTaskResultBean.setItemName(a.getItemName());
                     orderTaskResultBean.setItemAliasName(a.getItemAliasName());
                     orderTaskResultBean.setScriptFlag(a.getScriptFlag());
                     orderTaskResultBean.setResultCategory(a.getResultCategory());
                     orderTaskResultBean.setRevisionRule(a.getRevisionRule());
                     orderTaskResultBean.setUnitName(a.getUnitName());
                     orderTaskResultBean.setReportDisplay(a.getReportDisplay());
                     orderTaskResultBean.setLowLimit(a.getLowLimit());
                     orderTaskResultBean.setHighLimit(a.getHighLimit());
                     orderTaskResultBean.setOriginalResult(a.getDefaultResult());
                     orderTaskResultBean.setRepeatNo(NumberUtils.parseLong(i));
                     orderTaskResultBean.setId(ApplicationContextHelper.getNextIdentity());
                     addList.add(orderTaskResultBean);
                  }
               }
            }
         });
      });
      this.orderTaskResultService.getDao().insert(addList);
      List<Long> delIdList = delList.stream().map(a -> a.getId()).collect(Collectors.toList());
      this.orderTaskResultService.getDao().deleteByIdList(delIdList);
      if (!addList.isEmpty()) {
         EmcOrderTaskResultComputingEngine.getEngine(addList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList())).compute();
      }
   }

   private void seal(List<CoreFileBean> coreFileList) {
      List<CoreFileBean> fileFilters = new ArrayList<>();
      coreFileList.forEach(r -> {
         CoreFileBean coreFileBean = new CoreFileBean();
         coreFileBean.setTargetId(r.getTargetId());
         coreFileBean.setExt("sealPdf");
         fileFilters.add(coreFileBean);
      });
      this.coreFileService.getDao().deleteBy(fileFilters, new String[]{"TARGETID", "EXT"});

      for (CoreFileBean cf : coreFileList) {
         File originalFile = CoreFileUtils.getLocalPath(cf).toFile();
         FilePathDTO archivePdfPathDTO = CoreFileUtils.toFilePath(FileScope.temp, FileUtils.getFileBaseName(cf.getName()) + "-盖章.pdf");
         Path sealFilePath = FilePathManager.getLocalPath(archivePdfPathDTO);
         FileUtils.makeDirs(sealFilePath);
         FileSealUtils.seal(originalFile.toString(), sealFilePath.toString(), true, originalFile.getName());
         CoreFileBean sealCoreFile = new CoreFileBean();
         sealCoreFile.setTargetId(cf.getTargetId());
         sealCoreFile.setScope(FileScope.secure.name());
         sealCoreFile.setExt("sealPdf");
         this.coreFileService.upload(sealCoreFile, sealFilePath.toFile());
      }
   }

   private void mapNullToString(Map<String, Object> map, String replaceText) {
      Set<String> set = map.keySet();
      if (set != null && !set.isEmpty()) {
         for (String key : set) {
            if (map.get(key) == null) {
               map.put(key, replaceText);
            }
         }
      }
   }

   private void listListNullToString(List<List<Object>> resultTableByRowsDataList, String replaceText) {
      resultTableByRowsDataList.forEach(list -> {
         for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null || StringUtils.isBlank(list.get(i).toString())) {
               list.set(i, replaceText);
            }
         }
      });
   }

   private String numberToChinese(int num) {
      String chinese = "";
      switch (num) {
         case 1:
            chinese = "一";
            break;
         case 2:
            chinese = "二";
            break;
         case 3:
            chinese = "三";
            break;
         case 4:
            chinese = "四";
            break;
         case 5:
            chinese = "五";
            break;
         case 6:
            chinese = "六";
            break;
         case 7:
            chinese = "七";
      }

      return chinese;
   }

   private String transScientificCounting(String input) {
      if ("ND".equals(input)) {
         return "ND";
      } else if (StringUtils.isEmpty(input)) {
         return input;
      } else {
         Pattern pattern = Pattern.compile("^(>|<|>=|<=|≥|≤)?[-+]*(\\.\\d+|\\d+\\.{0,1}\\d*)[Ee]{1}[+-]?[\\d]+$");
         if (pattern.matcher(input).matches() && input.contains("E")) {
            String head = input.substring(0, input.indexOf("E"));
            String tail = input.substring(input.indexOf("E") + 1);
            if (NumberUtils.parseDouble(tail) == 0.0) {
               return head;
            } else {
               tail = tail.replace("0", "⁰")
                  .replace("1", "¹")
                  .replace("2", "²")
                  .replace("3", "³")
                  .replace("4", "⁴")
                  .replace("5", "⁵")
                  .replace("6", "⁶")
                  .replace("7", "⁷")
                  .replace("8", "⁸")
                  .replace("9", "⁹")
                  .replace("+", "")
                  .replace("-", "⁻");
               return head + "×10" + tail;
            }
         } else {
            return input;
         }
      }
   }

   private String getNdValue(EmcReportResultBean reportResultBean) {
      String reportResult = reportResultBean.getReportResult();
      if ("ND".equals(reportResult) && NumberUtils.isNumber(reportResultBean.getLowLimit())) {
         reportResult = NumberUtils.parseDouble(reportResultBean.getLowLimit()) / 2.0 + "";
      }

      return reportResult;
   }

   private String getNdValue2(EmcReportResultBean reportResultBean) {
      String reportResult = reportResultBean.getReportResult();
      if ("ND".equals(reportResult) && NumberUtils.isNumber(reportResultBean.getLowLimit()) && "生活饮用水".equals(reportResultBean.getOrderCategory())) {
         reportResult = "<" + reportResultBean.getLowLimit();
      }

      return reportResult;
   }

   private static String getScientificValue(String result, String revisionRule) {
      int radix = getRadix(result);
      String scientificResult = NumberUtils.scientificNotationFormat(NumberUtils.parseDouble(result), radix);
      if (!StringUtils.isEmpty(revisionRule)) {
         scientificResult = NumberUtils.rounding(scientificResult, revisionRule);
      }

      return scientificResult;
   }

   private static int getRadix(String number) {
      String num = number;
      boolean scientificNumber = NumberUtils.isScientificNumber(number);
      if (scientificNumber) {
         num = NumberUtils.formatNumber(new BigDecimal(number));
      }

      return !num.contains(".") ? 0 : num.length() - num.indexOf(".") - 1;
   }

   private void changeFont(File source, File target) {
      try (
         InputStream inputStream = Files.newInputStream(source.toPath());
         OutputStream os = new BufferedOutputStream(new FileOutputStream(target));
      ) {
         Document document = new Document();
         document.loadFromStream(Files.newInputStream(source.toPath()), FileFormat.Auto);
         TextSelection[] sections = document.findAllString("", true, true);

         for (Object obj : sections) {
            if (obj instanceof TextSelection) {
               try {
                  TextSelection textSelection = (TextSelection)obj;
                  TextRange range = textSelection.getAsOneRange();
                  if (range != null) {
                     String text = range.getText();
                     if (!"第 ".equals(text)) {
                        range.getCharacterFormat().setFontNameFarEast("宋体");
                        range.getCharacterFormat().setFontNameAscii("Times New Roman");
                     }
                  }
               } catch (Exception var41) {
               }
            }
         }

         document.saveToStream(os, document.getDetectedFormatType());
      } catch (IOException var46) {
         throw new FileException(var46);
      }
   }

   private List<List<String>> splitList(List<String> list, int chunkSize) {
      return IntStream.range(0, (int)Math.ceil((double)list.size() / (double)chunkSize))
         .mapToObj(i -> list.subList(i * chunkSize, Math.min(list.size(), (i + 1) * chunkSize)))
         .collect(Collectors.toList());
   }
}
