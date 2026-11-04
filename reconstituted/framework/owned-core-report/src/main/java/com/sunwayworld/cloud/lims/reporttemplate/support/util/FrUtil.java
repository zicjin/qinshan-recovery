package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.itextpdf.text.pdf.PdfReader;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.env.Environment;
import org.yaml.snakeyaml.util.UriEncoder;

public interface FrUtil {
   static void createPdf(CoreReportConfigLineBean configLine, CoreFileBean cptFile, File uploadFile, Map<String, String> param) {
      String fileUrl = createFile(configLine, cptFile, uploadFile, param, "&format=pdf");
      PdfReader reader = null;

      try {
         reader = new PdfReader(uploadFile.getAbsolutePath());
      } catch (Exception var10) {
         throw new InvalidDataException(
            "报告文件生成失败:" + fileUrl.substring(0, fileUrl.indexOf("?") + 1) + UriEncoder.encode(fileUrl.substring(fileUrl.indexOf("?") + 1))
         );
      } finally {
         if (reader != null) {
            reader.close();
         }
      }
   }

   static void createExcel(CoreReportConfigLineBean configLine, CoreFileBean cptFile, File uploadFile, Map<String, String> param) {
      createFile(configLine, cptFile, uploadFile, param, "&format=excel");
   }

   static void createWord(CoreReportConfigLineBean configLine, CoreFileBean cptFile, File uploadFile, Map<String, String> param) {
      createFile(configLine, cptFile, uploadFile, param, "&format=word");
   }

   static String createFile(CoreReportConfigLineBean configLine, CoreFileBean cptFile, File uploadFile, Map<String, String> param, String finePdfType) {
      StringBuilder sb = new StringBuilder();
      param.forEach((k, v) -> sb.append("&").append(k).append("=").append(v));
      String fileUrl = getFineServer() + "/decision/view/report?viewlet=" + configLine.getReportConfigLineNo() + "/" + cptFile.getName() + sb + finePdfType;
      HttpClientManager.getInstance()
         .download(fileUrl.substring(0, fileUrl.indexOf("?") + 1) + UriEncoder.encode(fileUrl.substring(fileUrl.indexOf("?") + 1)), uploadFile);
      return fileUrl;
   }

   static String getFineServer() {
      return Optional.ofNullable(ApplicationContextHelper.getBean(Environment.class).getProperty("fine.context"))
         .orElse("http://localhost:" + Optional.ofNullable(ApplicationContextHelper.getBean(Environment.class).getProperty("server.port")).orElse("80"));
   }
}
