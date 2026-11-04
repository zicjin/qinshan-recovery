package com.sunwayworld.framework.support.base.service;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.executor.ResourceMonitoringExecutor;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.extractor.DownloadParamExtractor;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.base.service.GenericExcelBuilderService.DownloadExcelDTO;
import com.sunwayworld.framework.support.matrix.bean.MatrixRowMap;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.notification.helper.CoreNotificationHelper;
import com.sunwayworld.module.sys.log.service.CoreExportLogService;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface GenericDownloadableService {
   default <T> DownloadExcelDTO generateExcelDTO(
      RestJsonWrapperBean wrapper, Class<? extends Object> itemClass, ResultSet rs, String exportUserId, String requestMode
   ) {
      HttpServletResponse response = ServletUtils.getCurrentResponse();
      return ResourceMonitoringExecutor.run(() -> {
         DownloadParamExtractor paramExtractor = new DownloadParamExtractor(wrapper);
         GenericExcelBuilderService excelBuilderService = ApplicationContextHelper.getBean(GenericExcelBuilderService.class);
         String[] sumProperties = Arrays.stream(wrapper.getSumProperties()).map(f -> StringUtils.removeStart(f, "ext$.")).toArray(String[]::new);
         DownloadExcelDTO excel = excelBuilderService.buildExcel(paramExtractor, itemClass, rs, exportUserId, sumProperties);
         String exportLog = wrapper.getParamValue("exportLog");
         if ("1".equals(exportLog)) {
            CoreExportLogService coreExportLogService = ApplicationContextHelper.getBean(CoreExportLogService.class);
            coreExportLogService.insertByExcelDTO(wrapper, excel, exportUserId, requestMode);
         }

         if (response != null) {
            response.setHeader("GIKAM-EXCEL-URL", StringUtils.encodeURL(excel.getUrl()));
         }

         return excel;
      });
   }

   default <T> DownloadExcelDTO generateExcelDTO(RestJsonWrapperBean wrapper, Page<T> page, String exportUserId, String requestMode) {
      HttpServletResponse response = ServletUtils.getCurrentResponse();
      LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
      loginUser.setLoginToken(AuthenticationHelper.getLoginUserToken());
      return ResourceMonitoringExecutor.run(() -> {
         DownloadExcelDTO var18;
         try {
            LocalContextHelper.setUserLogin(loginUser);
            DownloadParamExtractor paramExtractor = new DownloadParamExtractor(wrapper);
            GenericExcelBuilderService excelBuilderService = ApplicationContextHelper.getBean(GenericExcelBuilderService.class);
            List<T> rows = page.getRows();
            if (!rows.isEmpty() && rows.get(0) instanceof AbstractTreeNode) {
               List<AbstractTreeNode> newRows = this.buildListByTree((List<AbstractTreeNode>)(List<?>)rows);
               page.setRows((List<T>)newRows);
               page.setTotal(newRows.size());
            }

            if (!rows.isEmpty() && rows.get(0) instanceof MatrixRowMap) {
               List<MatrixRowMap> newRows = this.buildListByMatrix((List<MatrixRowMap>)(List<?>)rows);
               page.setRows((List<T>)newRows);
               page.setTotal(newRows.size());
            }

            DownloadExcelDTO excel = excelBuilderService.buildExcel(paramExtractor, page, exportUserId);
            String exportLog = wrapper.getParamValue("exportLog");
            if ("1".equals(exportLog)) {
               CoreExportLogService coreExportLogService = ApplicationContextHelper.getBean(CoreExportLogService.class);
               coreExportLogService.insertByExcelDTO(wrapper, excel, exportUserId, requestMode);
            }

            if (response != null) {
               response.setHeader("GIKAM-EXCEL-URL", StringUtils.encodeURL(excel.getUrl()));
            }

            var18 = excel;
         } finally {
            LocalContextHelper.removeUserLogin();
         }

         return var18;
      });
   }

   default <T> String getExcelUrl(RestJsonWrapperBean wrapper, Page<T> page, String exportUserId, String requestMode) {
      return this.generateExcelDTO(wrapper, page, LocalContextHelper.getLoginUserId(), "sync").getUrl();
   }

   default <T> String generateExportUrl(RestJsonWrapperBean wrapper, Page<T> page, String exportUserId, String requestMode) {
      return this.generateExcelDTO(wrapper, page, LocalContextHelper.getLoginUserId(), "sync").getUrl();
   }

   default <T> void generateExportUrlAndNotification(
      RestJsonWrapperBean wrapper, Class<? extends Object> itemClass, ResultSet rs, String exportUserId, String requestMode
   ) {
      DownloadExcelDTO excel = this.generateExcelDTO(wrapper, itemClass, rs, exportUserId, requestMode);
      Pair<String, String> filePair = Pair.of(excel.getFileName(), excel.getUrl());
      CoreNotificationHelper.sendNotification("EXPORT_EXCEL", ArrayUtils.asList(exportUserId), null, ArrayUtils.asList(filePair));
   }

   default <T> void generateExportUrlAndNotification(RestJsonWrapperBean wrapper, Page<T> page, String exportUserId, String requestMode) {
      DownloadExcelDTO excel = this.generateExcelDTO(wrapper, page, exportUserId, requestMode);
      Pair<String, String> filePair = Pair.of(excel.getFileName(), excel.getUrl());
      CoreNotificationHelper.sendNotification("EXPORT_EXCEL", ArrayUtils.asList(exportUserId), null, ArrayUtils.asList(filePair));
   }

   default List<AbstractTreeNode> buildListByTree(List<AbstractTreeNode> rows) {
      List<AbstractTreeNode> list = new ArrayList<>();
      rows.forEach(e -> {
         List<AbstractTreeNode> children = e.getChildren();
         e.setChildren(CollectionUtils.emptyList());
         list.add(e);
         if (children != null && !children.isEmpty()) {
            list.addAll(this.buildListByTree(children));
         }
      });
      return list;
   }

   default List<MatrixRowMap> buildListByMatrix(List<MatrixRowMap> rows) {
      List<MatrixRowMap> list = new ArrayList<>();
      rows.forEach(e -> {
         List<MatrixRowMap> children = (List<MatrixRowMap>)e.get("children");
         list.add(e);
         if (children != null && !children.isEmpty()) {
            list.addAll(this.buildListByMatrix(children));
         }
      });
      return list;
   }
}
