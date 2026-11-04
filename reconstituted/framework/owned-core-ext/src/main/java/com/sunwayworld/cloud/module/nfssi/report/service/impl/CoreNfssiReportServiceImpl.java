package com.sunwayworld.cloud.module.nfssi.report.service.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiSystemResponseDTO;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiOrderService;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiSystemService;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.cloud.module.nfssi.report.persistent.dao.CoreNfssiReportDao;
import com.sunwayworld.cloud.module.nfssi.report.service.CoreNfssiReportItemService;
import com.sunwayworld.cloud.module.nfssi.report.service.CoreNfssiReportService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreNfssiReportServiceImpl implements CoreNfssiReportService {
   @Autowired
   private CoreNfssiReportDao coreNfssiReportDao;
   @Autowired
   private CoreNfssiOrderService coreNfssiOrderService;
   @Autowired
   @Lazy
   private CoreNfssiReportItemService coreNfssiReportItemService;
   @Autowired
   @Lazy
   private CoreNfssiSystemService coreNfssiSystemOrderService;

   public CoreNfssiReportDao getDao() {
      return this.coreNfssiReportDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public String insert(RestJsonWrapperBean jsonWrapper) {
      CoreNfssiReportBean coreNfssiReport = jsonWrapper.parseUnique(CoreNfssiReportBean.class);
      coreNfssiReport.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
      this.getDao().insert(coreNfssiReport);
      return coreNfssiReport.getId();
   }

   @Override
   public Page<CoreNfssiReportItemBean> selectReportItemPaginationByReportId(String id, RestJsonWrapperBean wrapper) {
      return this.coreNfssiReportItemService.selectPaginationByFilter(SearchFilter.instance().match("REPORTID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public String push(CoreNfssiReportBean coreNfssiReport, List<CoreNfssiReportItemBean> coreNfssiReportItemList) {
      if (StringUtils.isEmpty(coreNfssiReport.getId())) {
         throw new InvalidDataException("CORE.MODULE.NFSSI.COLUMN_REPORTID_CAN_NOT_BE_EMPTY");
      } else if (StringUtils.isEmpty(coreNfssiReport.getOrderId())) {
         throw new InvalidDataException("CORE.MODULE.NFSSI.COLUMN_ORDERID_CAN_NOT_BE_EMPTY");
      } else {
         CoreNfssiOrderBean coreNfssiOrder = this.coreNfssiOrderService.selectById(coreNfssiReport.getOrderId());
         String sampleType = coreNfssiOrder.getSampleType();
         List<Field> filedList = ReflectionUtils.getFiledList(CoreNfssiReportItemBean.class);
         coreNfssiReportItemList.forEach(
            e -> {
               e.setId(ApplicationContextHelper.getNextIdentity());
               e.setReportId(coreNfssiReport.getId());
               if (!"合格项".equals(e.getResultJudgment())
                  && !"问题项".equals(e.getResultJudgment())
                  && !"不合格项".equals(e.getResultJudgment())
                  && !"不判定项".equals(e.getResultJudgment())
                  && !"未检验".equals(e.getResultJudgment())) {
                  throw new InvalidDataException("CORE.MODULE.NFSSI.RESULT_JUDGMENT_CAN_ONLY_INPUT_EXPECTED_VALUE");
               } else {
                  if ("2".equals(sampleType)) {
                     if (StringUtils.isEmpty(e.getFuncCategory())) {
                        throw new InvalidDataException("CORE.MODULE.NFSSI.HEALTH_PRODUCTS_MUST_INPUT_FUNC_CATEGORY");
                     }

                     if (e.getStatus() == null) {
                        throw new InvalidDataException("CORE.MODULE.NFSSI.HEALTH_PRODUCTS_MUST_INPUT_STATE");
                     }
                  }

                  if ("3".equals(sampleType) && StringUtils.isEmpty(e.getNecessary())) {
                     throw new InvalidDataException("CORE.MODULE.NFSSI.FARM_PRODUCE_MUST_INPUT_NECESSARY");
                  } else {
                     filedList.forEach(filed -> {
                        Column column = filed.getAnnotation(Column.class);
                        if (column != null && !column.nullable()) {
                           String value = (String)ReflectionUtils.getFieldValue(e, filed.getName());
                           if (StringUtils.isEmpty(value)) {
                              throw new InvalidDataException(column.name() + I18nHelper.getMessage("CORE.MODULE.NFSSI.COLUMN_MUST_INPUT"));
                           }
                        }
                     });
                  }
               }
            }
         );
         CoreNfssiSystemResponseDTO coreNfssiSystemResponseDTO = this.coreNfssiSystemOrderService.pushReport(coreNfssiReport, coreNfssiReportItemList);
         if (coreNfssiSystemResponseDTO.getCode() == 0L) {
            coreNfssiReport.setState("1");
         } else {
            coreNfssiReport.setState("0");
            coreNfssiReport.setErrorInfo(coreNfssiSystemResponseDTO.getData());
         }

         CoreNfssiReportBean oriNfssiReport = this.selectByIdIfPresent(coreNfssiReport.getId());
         if (oriNfssiReport != null) {
            this.getDao().update(coreNfssiReport, new String[0]);
            List<CoreNfssiReportItemBean> deleteCoreNfssiReportItemList = this.coreNfssiReportItemService
               .getDao()
               .selectListByOneColumnValue(coreNfssiReport.getId(), "REPORTID");
            List<Long> reportItemIdList = deleteCoreNfssiReportItemList.stream().map(m -> m.getId()).collect(Collectors.toList());
            this.coreNfssiReportItemService.getDao().deleteByIdList(reportItemIdList);
         } else {
            this.getDao().insert(coreNfssiReport);
         }

         this.coreNfssiReportItemService.getDao().insert(coreNfssiReportItemList);
         return coreNfssiReport.getId();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void rePush(RestJsonWrapperBean wrapper) {
      CoreNfssiReportBean coreNfssiReport = wrapper.parseUnique(CoreNfssiReportBean.class);
      List<CoreNfssiReportItemBean> coreNfssiReportItemList = this.coreNfssiReportItemService
         .getDao()
         .selectListByOneColumnValue(coreNfssiReport.getId(), "REPORTID");
      CoreNfssiSystemResponseDTO coreNfssiSystemResponseDTO = this.coreNfssiSystemOrderService.pushReport(coreNfssiReport, coreNfssiReportItemList);
      if (coreNfssiSystemResponseDTO.getCode() == 0L) {
         coreNfssiReport.setState("1");
      } else {
         coreNfssiReport.setState("0");
         coreNfssiReport.setErrorInfo(coreNfssiSystemResponseDTO.getData());
      }

      this.getDao().update(coreNfssiReport, new String[0]);
      this.coreNfssiReportItemService.getDao().update(coreNfssiReportItemList);
   }
}
