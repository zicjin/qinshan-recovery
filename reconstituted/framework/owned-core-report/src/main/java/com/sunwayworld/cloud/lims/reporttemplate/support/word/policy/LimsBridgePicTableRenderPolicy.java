package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.sunwayworld.cloud.lims.reporttemplate.support.function.FileFunction;
import com.sunwayworld.cloud.lims.reporttemplate.support.render.LimsCustomerRender;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.PictureContractionUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.utils.WordUtil;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class LimsBridgePicTableRenderPolicy implements LimsCustomerRender {
   private static final Logger logger = LogManager.getLogger(LimsBridgePicTableRenderPolicy.class);
   private String prefix;
   private String suffix;
   private static final String TITLE_SPLIT_TAG = ",";

   public LimsBridgePicTableRenderPolicy() {
      this("[", "]");
   }

   public LimsBridgePicTableRenderPolicy(String prefix, String suffix) {
      this.prefix = prefix;
      this.suffix = suffix;
   }

   @Override
   public void render(ElementTemplate eleTemplate, Object renderData, XWPFTemplate template) {
      RunTemplate runTemplate = (RunTemplate)eleTemplate;
      XWPFRun run = runTemplate.getRun();
      if (renderData != null) {
         List<CoreFileBean> fileBeanList = (List<CoreFileBean>)renderData;
         CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);

         for (CoreFileBean coreFileBean : fileBeanList) {
            try {
               coreFileManager.download(coreFileBean);
            } catch (Exception var40) {
               logger.error("some error happened when download file {}", JSONObject.toJSONString(coreFileBean), var40);
               throw new FileException(var40);
            }
         }

         XWPFTableCell tagCell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
         XWPFTableRow tagRow = tagCell.getTableRow();
         String scaleType = fileBeanList.get(0).getExt$Item("scaleType");
         BigInteger cellW = tagCell.getCTTc().getTcPr().getTcW().getW();
         int width = cellW.divide(new BigInteger("11")).intValue();
         int cellHeight = tagRow.getHeight();
         int height = NumberUtils.parseInt(ArithUtils.div(cellHeight, 22.0));
         int columnSize = tagRow.getTableCells().size();
         List<PictureRenderData> poiPicList = new ArrayList<>();

         for (CoreFileBean coreFileBean : fileBeanList) {
            File picFile = null;

            try {
               picFile = LimsFileUtils.getFilePath(coreFileBean).toFile();
            } catch (Exception var39) {
               logger.error("get picture error {}", JSONObject.toJSONString(coreFileBean), var39);
            }

            if (picFile != null && picFile.exists()) {
               BufferedImage image = null;

               try {
                  if (!"1".equals(coreFileBean.getExt$Item("notcontraction"))) {
                     image = PictureContractionUtils.contraction(picFile);
                  } else {
                     image = ImageIO.read(picFile);
                  }
               } catch (Exception var38) {
                  logger.error("get pic to bufferImage error");
               }

               if (image != null) {
                  int imgWidth = image.getWidth();
                  int imgHeight = image.getHeight();
                  int finalPicWidth;
                  int finalPicHeight;
                  if (StringUtils.equals(scaleType, "0") && imgHeight > height && ArithUtils.div(height, imgHeight) < ArithUtils.div(width, imgWidth)) {
                     double rate = ArithUtils.div(height, imgHeight);
                     finalPicHeight = height;
                     finalPicWidth = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
                  } else {
                     double rate = ArithUtils.div(width, imgWidth);
                     finalPicWidth = width;
                     finalPicHeight = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
                  }

                  try {
                     ImageIO.write(image, "png", picFile);
                  } catch (IOException var37) {
                     throw new RuntimeException(var37);
                  }

                  if (NumberUtils.isNumber(coreFileBean.getExt$Item("accuracy"))) {
                     poiPicList.add(
                        FileFunction.getCompressPicFile(picFile, finalPicWidth, finalPicHeight, Double.valueOf(coreFileBean.getExt$Item("accuracy")))
                     );
                  } else {
                     poiPicList.add(new PictureRenderData(finalPicWidth, finalPicHeight, picFile));
                  }

                  System.err.println(imgWidth);
               }
            }

            logger.error("PIC FILE NOT FOUND : {}", coreFileBean.getName());
         }

         int rowSize = poiPicList.size() / columnSize + (poiPicList.size() % columnSize == 0 ? 0 : 1);
         XWPFTable table = tagCell.getTableRow().getTable();
         int needCopyTimes = rowSize - 1;
         if (needCopyTimes > 0) {
            for (int i = 0; i < needCopyTimes; i++) {
               XWPFTableRow currentRow = table.insertNewTableRow(table.getRows().size());
               XWPFTableRow currentTextRow = table.insertNewTableRow(table.getRows().size());
               WordUtil.copyTableRow(currentRow, table.getRows().get(0));
               WordUtil.copyTableRow(currentTextRow, table.getRows().get(1));
            }
         }

         String textPrefix = table.getRows().get(1).getTableCells().get(0).getText();

         for (int i = 0; i < poiPicList.size(); i++) {
            int currentRunNo = i / columnSize;
            int colNo = i % columnSize;
            XWPFTableCell picCell = table.getRows().get(currentRunNo * 2).getTableCells().get(colNo);
            List<XWPFParagraph> picCellParagraphs = picCell.getParagraphs();

            for (int k = picCellParagraphs.size() - 1; k > 0; k--) {
               picCell.removeParagraph(k);
            }

            XWPFParagraph picPara = picCellParagraphs.get(0);
            picPara.setAlignment(ParagraphAlignment.CENTER);
            if (picPara.getRuns() == null || picPara.getRuns().isEmpty()) {
               picPara.createRun();
            }

            List<XWPFRun> picRuns = picPara.getRuns();

            for (int k = picRuns.size() - 1; k > 0; k--) {
               picPara.removeRun(k);
            }

            picRuns.get(0).setText("", 0);

            try {
               ByteArrayInputStream inputStream = new ByteArrayInputStream(poiPicList.get(i).getPictureSupplier().get());
               picRuns.get(0)
                  .addPicture(
                     inputStream,
                     5,
                     "Generated",
                     Units.toEMU(poiPicList.get(i).getPictureStyle().getWidth()),
                     Units.toEMU(poiPicList.get(i).getPictureStyle().getHeight())
                  );
            } catch (Exception var36) {
               throw new RuntimeException(var36);
            }

            XWPFTableCell textCell = table.getRows().get(currentRunNo * 2 + 1).getTableCells().get(colNo);
            CoreFileBean fileBean = fileBeanList.get(i);
            String fileName = FileUtils.getFileBaseName(fileBean.getName());
            String showText = (!StringUtils.isEmpty(textPrefix) && !StringUtils.equals("/", textPrefix) ? textPrefix : "") + fileName;
            if (!StringUtils.isBlank(fileBean.getExt$Item("showname"))) {
               showText = fileBean.getExt$Item("showname");
            }

            List<XWPFParagraph> textCellParagraphs = textCell.getParagraphs();

            for (int k = textCellParagraphs.size() - 1; k > 0; k--) {
               picCell.removeParagraph(k);
            }

            XWPFParagraph paragraph = textCellParagraphs.get(0);
            if (paragraph.getRuns() == null || paragraph.getRuns().isEmpty()) {
               paragraph.createRun();
            }

            List<XWPFRun> runs = paragraph.getRuns();

            for (int k = runs.size() - 1; k > 0; k--) {
               paragraph.removeRun(k);
            }

            runs.get(0).setText(showText, 0);
         }
      }
   }
}
