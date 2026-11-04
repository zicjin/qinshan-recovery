package com.sunwayworld.cloud.module.pageoffice;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.SpireWatermarkUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.zhuozhengsoft.pageoffice.BorderStyleType;
import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PDFCtrl;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import com.zhuozhengsoft.pageoffice.ThemeType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping({"/open/page-office"})
@Profile({"pageoffice"})
public class PageOfficeController {
   @Autowired
   private CoreFileService fileService;
   @Autowired
   private CoreFileManager fileManager;
   @Autowired
   private CoreFileLogService fileLogService;
   @Value("${sunway.config.pageoffice.toolbar.enable:false}")
   private Boolean toolbar;
   @Value("${sunway.config.file.watermark.enabled:false}")
   private Boolean watermarkEnable;

   @RequestMapping(
      value = {"/document"},
      method = {RequestMethod.GET}
   )
   public ModelAndView showWord(HttpServletRequest request, Map<String, Object> map) throws IOException {
      String readonly = request.getParameter("readonly");
      if (!StringUtils.isEmpty(request.getParameter("fileId"))) {
         long fileId = Long.parseLong(request.getParameter("fileId"));
         CoreFileBean coreFile = this.fileService.selectById(fileId);
         FilePathDTO filePathDTO = CoreFileUtils.toFilePath(coreFile);
         if (this.watermarkEnable) {
            String fileBaseName = FileUtils.getFileBaseName(filePathDTO.getFileName());
            String fileExt = FileUtils.getFileExtension(filePathDTO.getFileName());
            String bakName = fileBaseName + "_REVERT_WATERMARK_BAK." + fileExt;
            FilePathDTO bakFilePathDTO = FilePathDTO.of(filePathDTO.getScope().name(), filePathDTO.getTimestamp(), filePathDTO.getIrregularName(), bakName);
            Path bakFilePath = FilePathManager.getLocalPath(bakFilePathDTO);
            if (Files.exists(bakFilePath)) {
               SpireWatermarkUtils.revertBakSourceFile(filePathDTO);
            }
         }

         this.fileManager.download(coreFile);
         if ("pdf".equalsIgnoreCase(coreFile.getFileExt())) {
            this.openPdfFileByPageOffice(request, map, filePathDTO);
         } else {
            PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
            poCtrl.setCustomToolbar(false);
            poCtrl.setCaption("三维天地");
            poCtrl.setServerPage("/poserver.zz");
            poCtrl.setSaveFilePage("/open/page-office/save?businessId=" + fileId);
            poCtrl.setJsFunction_AfterDocumentSaved("AfterDocumentSaved()");
            poCtrl.setAllowCopy(true);
            poCtrl.setTheme(ThemeType.Office2010);
            poCtrl.setBorderStyle(BorderStyleType.BorderThin);
            if (!StringUtils.isEmpty(readonly) && "true".equals(readonly)) {
               poCtrl.setOfficeToolbars(this.toolbar);
            } else {
               poCtrl.setCustomToolbar(true);
               poCtrl.addCustomToolButton("保存", "Save", 1);
               poCtrl.addCustomToolButton("显示工具栏", "showToolBar", 9);
               poCtrl.addCustomToolButton("隐藏工具栏", "hideToolBar", 18);
            }

            OpenModeType openModeType = this.getFileType(coreFile.getFileExt().toLowerCase(), readonly);
            poCtrl.webOpen("/open/page-office/file-stream?fileId=" + fileId, openModeType, "");
            map.put("pageoffice", poCtrl.getHtmlCode(""));
            String pageOfficeParam = request.getParameter("pageOfficeParam");
            if (!StringUtils.isEmpty(pageOfficeParam) && !"undefined".equals(pageOfficeParam)) {
               JSONObject jsonObject = JSONObject.parseObject(pageOfficeParam);
               String tempId = String.valueOf(jsonObject.get("tempId"));
               map.put("tempId", tempId);
               String operationWidth = String.valueOf(jsonObject.get("operationWidth"));
               if (!"undefined".equals(operationWidth) && !"null".equals(operationWidth)) {
                  map.put("operationWidth", operationWidth);
               }
            }
         }
      } else if (!StringUtils.isEmpty(request.getParameter("fileUrl"))) {
         String fileUrl = request.getParameter("fileUrl");
         String fileExt = FileUtils.getFileExtension(fileUrl);
         if ("pdf".equalsIgnoreCase(fileExt)) {
            this.openPdfFileUrlByPageOffice(request, map, fileUrl);
         } else {
            PageOfficeCtrl poCtrlx = new PageOfficeCtrl(request);
            poCtrlx.setCustomToolbar(false);
            poCtrlx.setCaption("三维天地");
            poCtrlx.setServerPage("/poserver.zz");
            poCtrlx.setSaveFilePage("/open/page-office/save?fileUrl=" + fileUrl);
            poCtrlx.setJsFunction_AfterDocumentSaved("AfterDocumentSaved()");
            poCtrlx.setAllowCopy(true);
            poCtrlx.setTheme(ThemeType.Office2010);
            poCtrlx.setBorderStyle(BorderStyleType.BorderThin);
            if (!StringUtils.isEmpty(readonly) && "true".equals(readonly)) {
               poCtrlx.setOfficeToolbars(this.toolbar);
            } else {
               poCtrlx.setCustomToolbar(true);
               poCtrlx.addCustomToolButton("保存", "Save", 1);
               poCtrlx.addCustomToolButton("显示工具栏", "showToolBar", 9);
               poCtrlx.addCustomToolButton("隐藏工具栏", "hideToolBar", 18);
            }

            OpenModeType openModeType = this.getFileType(fileExt.toLowerCase(), readonly);
            poCtrlx.webOpen(fileUrl, openModeType, "");
            map.put("pageoffice", poCtrlx.getHtmlCode(""));
            String pageOfficeParam = request.getParameter("pageOfficeParam");
            if (!StringUtils.isEmpty(pageOfficeParam) && !"undefined".equals(pageOfficeParam)) {
               JSONObject jsonObject = JSONObject.parseObject(pageOfficeParam);
               String tempId = String.valueOf(jsonObject.get("tempId"));
               map.put("tempId", tempId);
               String operationWidth = String.valueOf(jsonObject.get("operationWidth"));
               if (!"undefined".equals(operationWidth) && !"null".equals(operationWidth)) {
                  map.put("operationWidth", operationWidth);
               }
            }
         }
      }

      ModelAndView modelAndView = new ModelAndView("module/pageoffice/pageOffice");
      modelAndView.addObject("readonly", readonly);
      return modelAndView;
   }

   @RequestMapping(
      value = {"/file-stream"},
      method = {RequestMethod.GET}
   )
   public void getFileStream(HttpServletRequest request, HttpServletResponse response) {
      if (!StringUtils.isEmpty(request.getParameter("fileId"))) {
         long fileId = Long.parseLong(request.getParameter("fileId"));
         CoreFileBean coreFile = this.fileService.selectById(fileId);
         FilePathDTO filePathDTO = CoreFileUtils.toFilePath(coreFile);
         Path localPath = FilePathManager.getLocalPath(filePathDTO);

         try (
            InputStream fileInputStream = Files.newInputStream(localPath);
            OutputStream outputStream = response.getOutputStream();
         ) {
            response.reset();
            response.setContentType(this.getResponseContentType(coreFile.getFileExt()));
            response.setHeader("Content-Disposition", "attachment; filename=" + StringUtils.encodeURL(filePathDTO.getFileName()));
            response.setContentLength((int)localPath.toFile().length());
            byte[] buffer = new byte[4096048];
            int size = 0;

            while ((size = fileInputStream.read(buffer)) != -1) {
               outputStream.write(buffer, 0, size);
            }

            outputStream.flush();
         } catch (IOException var41) {
         }
      }
   }

   @RequestMapping({"/save"})
   public void saveFile(HttpServletRequest request, HttpServletResponse response) {
      Long fileId = Long.parseLong(request.getParameter("businessId"));
      CoreFileBean coreFile = this.fileService.selectById(fileId);
      FilePathDTO filePathDTO = CoreFileUtils.toFilePath(coreFile);
      String targetId = coreFile.getTargetId();
      FileSaver fs = new FileSaver(request, response);
      fs.saveToFile(coreFile.getName());
      this.fileManager.upload(filePathDTO, fs.getFileStream());
      coreFile.setVersion(coreFile.getVersion() + 1L);
      this.fileService.getDao().update(coreFile, "VERSION");
      this.fileLogService.insert(fileId, CoreFileOperation.UPDATE.name());
      FileUtils.deleteQuietly(new File(coreFile.getName()));
      fs.close();
   }

   private String getResponseContentType(String fileExt) {
      if (fileExt.endsWith("xls") || fileExt.endsWith("xlsx") || fileExt.endsWith("xlsm")) {
         return "application/x-excel";
      } else if (fileExt.endsWith("ppt") || fileExt.endsWith("pptx")) {
         return "application/ms-powerpoint";
      } else {
         return fileExt.endsWith("pdf") ? "application/pdf" : "application/msword";
      }
   }

   private OpenModeType getFileType(String fileExt, String readonly) {
      if (fileExt.endsWith("xls") || fileExt.endsWith("xlsx") || fileExt.endsWith("xlsm")) {
         return "false".equals(readonly) ? OpenModeType.xlsNormalEdit : OpenModeType.xlsReadOnly;
      } else if (!fileExt.endsWith("ppt") && !fileExt.endsWith("pptx")) {
         return "false".equals(readonly) ? OpenModeType.docAdmin : OpenModeType.docReadOnly;
      } else {
         return "false".equals(readonly) ? OpenModeType.pptNormalEdit : OpenModeType.pptReadOnly;
      }
   }

   private void openPdfFileByPageOffice(HttpServletRequest request, Map<String, Object> map, FilePathDTO filePathDTO) {
      if (!StringUtils.isEmpty(request.getParameter("fileId"))) {
         long fileId = Long.parseLong(request.getParameter("fileId"));
         PDFCtrl pdfCtrl = new PDFCtrl(request);
         pdfCtrl.setServerPage("/poserver.zz");
         pdfCtrl.addCustomToolButton("打印", "PrintFile()", 6);
         pdfCtrl.addCustomToolButton("-", "", 0);
         pdfCtrl.addCustomToolButton("实际大小", "SetPageReal()", 16);
         pdfCtrl.addCustomToolButton("适合页面", "SetPageFit()", 17);
         pdfCtrl.addCustomToolButton("适合宽度", "SetPageWidth()", 18);
         pdfCtrl.addCustomToolButton("-", "", 0);
         pdfCtrl.addCustomToolButton("首页", "FirstPage()", 8);
         pdfCtrl.addCustomToolButton("上一页", "PreviousPage()", 9);
         pdfCtrl.addCustomToolButton("下一页", "NextPage()", 10);
         pdfCtrl.addCustomToolButton("尾页", "LastPage()", 11);
         pdfCtrl.addCustomToolButton("-", "", 0);
         pdfCtrl.addCustomToolButton("左转", "RotateLeft()", 12);
         pdfCtrl.addCustomToolButton("右转", "RotateRight()", 13);
         pdfCtrl.addCustomToolButton("-", "", 0);
         pdfCtrl.addCustomToolButton("放大", "ZoomIn()", 14);
         pdfCtrl.addCustomToolButton("缩小", "ZoomOut()", 15);
         pdfCtrl.setCaption("三维天地");
         pdfCtrl.setTheme(ThemeType.Office2007);
         pdfCtrl.setBorderStyle(BorderStyleType.BorderFlat);
         pdfCtrl.webOpen("/open/page-office/file-stream?fileId=" + fileId);
         map.put("pageoffice", pdfCtrl.getHtmlCode(""));
      }
   }

   private void openPdfFileUrlByPageOffice(HttpServletRequest request, Map<String, Object> map, String fileUrl) {
      PDFCtrl pdfCtrl = new PDFCtrl(request);
      pdfCtrl.setServerPage("/poserver.zz");
      pdfCtrl.setCaption("三维天地");
      pdfCtrl.setTheme(ThemeType.Office2007);
      pdfCtrl.setBorderStyle(BorderStyleType.BorderFlat);
      pdfCtrl.webOpen(fileUrl);
      map.put("pageoffice", pdfCtrl.getHtmlCode(""));
   }
}
