package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.config.GramerSymbol;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.style.TableStyle;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.plugin.comment.XWPFComments;
import com.deepoove.poi.policy.PictureRenderPolicy.Helper;
import com.deepoove.poi.render.compute.EnvModel;
import com.deepoove.poi.render.compute.RenderDataCompute;
import com.deepoove.poi.render.processor.DocumentProcessor;
import com.deepoove.poi.resolver.Resolver;
import com.deepoove.poi.template.MetaTemplate;
import com.deepoove.poi.util.ReflectionUtils;
import com.deepoove.poi.util.StyleUtils;
import com.deepoove.poi.util.TableTools;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.vml.CTFill;
import com.microsoft.schemas.vml.CTGroup;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTStroke;
import com.microsoft.schemas.vml.CTTextPath;
import com.microsoft.schemas.vml.STExt;
import com.microsoft.schemas.vml.STTrueFalse;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.WaterMarkBean;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.compute.LimsSpELRenderDataCompute;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsCustomizeRenderPolicy;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsHackLoopTableRenderPolicy;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsMultiAbleDocxRenderPolicy;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsNumberingRenderPolicy;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsSuspensionPicRenderPolicy;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsTextRenderPolicy;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.IRunBody;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlipCompression;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPictureNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WordExportUtil {
   private static final Logger LOGGER = LoggerFactory.getLogger(WordExportUtil.class);
   public static int LINE_HEIGHT = 20;
   private static final String SUP_MARK_START = "<sup>";
   private static final String SUP_MARK_END = "</sup>";
   private static final String SUB_MARK_START = "<sub>";
   private static final String SUB_MARK_END = "</sub>";
   private static final String SUB_MARK_REG = "</?su[b,p]>";
   private static final String REGEX_LINE_CHARACTOR = "\\n";

   public static XWPFParagraph createWatermarkParagraph(XWPFDocument doc, String text, int idx) {
      CTP p = Factory.newInstance();
      CTR r = p.addNewR();
      org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture pict = r.addNewPict();
      CTGroup group = com.microsoft.schemas.vml.CTGroup.Factory.newInstance();
      CTShape shape = group.addNewShape();
      shape.setId("PowerPlusWaterMarkObject" + idx);
      shape.setSpid("_x0000_s102" + (4 + idx));
      shape.setType("#_x0000_t136");
      shape.setStyle(
         "position:absolute;left:0pt;height:130.25pt;width:456.95pt;mso-position-horizontal:center;mso-position-horizontal-relative:margin;mso-position-vertical:center;mso-position-vertical-relative:margin;rotation:20643840f;z-index:-251653120;mso-width-relative:page;mso-height-relative:page;"
      );
      shape.setFillcolor("#C0C0C0");
      shape.setCoordsize("21600,21600");
      shape.setAdj("10800");
      shape.setFilled(STTrueFalse.T);
      shape.setStroked(STTrueFalse.F);
      shape.addNewPath();
      CTFill ctFill = shape.addNewFill();
      ctFill.setOn(STTrueFalse.T);
      ctFill.setOpacity("32768f");
      ctFill.setFocussize("0,0");
      CTStroke ctStroke = shape.addNewStroke();
      ctStroke.setOn(STTrueFalse.F);
      shape.addNewImagedata();
      CTLock lock = shape.addNewLock();
      lock.setExt(STExt.EDIT);
      lock.setGrouping(com.microsoft.schemas.office.office.STTrueFalse.F);
      lock.setRotation(com.microsoft.schemas.office.office.STTrueFalse.F);
      lock.setText(com.microsoft.schemas.office.office.STTrueFalse.F);
      lock.setAspectratio(com.microsoft.schemas.office.office.STTrueFalse.T);
      CTTextPath shapeTextPath = shape.addNewTextpath();
      shapeTextPath.setOn(STTrueFalse.T);
      shapeTextPath.setFitshape(STTrueFalse.T);
      shapeTextPath.setFitpath(STTrueFalse.T);
      shapeTextPath.setTrim(STTrueFalse.T);
      shapeTextPath.setXscale(STTrueFalse.F);
      shapeTextPath.setString(text);
      shapeTextPath.setStyle("font-family:宋体;font-size:36pt;v-text-align:center;");
      pict.set(group);
      return new XWPFParagraph(p, doc);
   }

   public static XWPFPicture addPicture(XWPFRun run, PictureRenderData pictureRenderData, String filename) throws Exception {
      IRunBody parent = run.getParent();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureRenderData.getPictureSupplier().get());
      int width = pictureRenderData.getPictureStyle().getWidth();
      int height = pictureRenderData.getPictureStyle().getHeight();
      if (!(parent.getPart() instanceof XWPFComments)) {
         return run.addPicture(inputStream, pictureRenderData.getPictureType().type(), filename, width, height);
      } else {
         XWPFComments comments = (XWPFComments)parent.getPart();
         String relationId = comments.addPictureData(inputStream, pictureRenderData.getPictureType().type());
         XWPFPictureData picData = (XWPFPictureData)comments.getRelationById(relationId);

         try {
            CTDrawing drawing = run.getCTR().addNewDrawing();
            CTInline inline = drawing.addNewInline();
            String xml = "<a:graphic xmlns:a=\""
               + CTGraphicalObject.type.getName().getNamespaceURI()
               + "\"><a:graphicData uri=\""
               + CTPicture.type.getName().getNamespaceURI()
               + "\"><pic:pic xmlns:pic=\""
               + CTPicture.type.getName().getNamespaceURI()
               + "\" /></a:graphicData></a:graphic>";
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = DocumentHelper.readDocument(is);
            inline.set(org.apache.xmlbeans.XmlToken.Factory.parse(doc.getDocumentElement(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            inline.setDistT(0L);
            inline.setDistR(0L);
            inline.setDistB(0L);
            inline.setDistL(0L);
            CTNonVisualDrawingProps docPr = inline.addNewDocPr();
            long id = ((NiceXWPFDocument)run.getParent().getDocument()).getDocPrIdenifierManager().reserveNew();
            docPr.setId(id);
            docPr.setName("Drawing " + id);
            docPr.setDescr(filename);
            CTPositiveSize2D extent = inline.addNewExtent();
            extent.setCx(width);
            extent.setCy(height);
            CTGraphicalObject graphic = inline.getGraphic();
            CTGraphicalObjectData graphicData = graphic.getGraphicData();
            CTPicture pic = getCTPictures(graphicData).get(0);
            CTPictureNonVisual nvPicPr = pic.addNewNvPicPr();
            CTNonVisualDrawingProps cNvPr = nvPicPr.addNewCNvPr();
            cNvPr.setId(0L);
            cNvPr.setName("Picture " + id);
            cNvPr.setDescr(filename);
            CTNonVisualPictureProperties cNvPicPr = nvPicPr.addNewCNvPicPr();
            cNvPicPr.addNewPicLocks().setNoChangeAspect(true);
            CTBlipFillProperties blipFill = pic.addNewBlipFill();
            CTBlip blip = blipFill.addNewBlip();
            blip.setEmbed(parent.getPart().getRelationId(picData));
            blip.setCstate(STBlipCompression.PRINT);
            blipFill.addNewStretch().addNewFillRect();
            CTShapeProperties spPr = pic.addNewSpPr();
            CTTransform2D xfrm = spPr.addNewXfrm();
            CTPoint2D off = xfrm.addNewOff();
            off.setX(0L);
            off.setY(0L);
            CTPositiveSize2D ext = xfrm.addNewExt();
            ext.setCx(width);
            ext.setCy(height);
            CTPresetGeometry2D prstGeom = spPr.addNewPrstGeom();
            prstGeom.setPrst(STShapeType.RECT);
            prstGeom.addNewAvLst();
            XWPFPicture xwpfPicture = new XWPFPicture(pic, run);
            run.getEmbeddedPictures().add(xwpfPicture);
            return xwpfPicture;
         } catch (SAXException | XmlException var33) {
            throw new IllegalStateException(var33);
         }
      }
   }

   public static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
      if (toRow > fromRow) {
         for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            CTTcPr tcPr = getTcPr(cell);
            CTVMerge vMerge = tcPr.addNewVMerge();
            if (rowIndex == fromRow) {
               vMerge.setVal(STMerge.RESTART);
            } else {
               vMerge.setVal(STMerge.CONTINUE);
               cell.getParagraphs().forEach(paragraph -> {
                  List<CTR> rList = paragraph.getCTP().getRList();

                  for (int i = 0; i < rList.size(); i++) {
                     paragraph.getCTP().removeR(i);
                  }
               });
            }
         }
      }
   }

   public static void insertDataByBookMark(XWPFDocument document, LimsTemplateDataMap<Object> replaceDataMap) {
      List<XWPFParagraph> paragraphs = document.getParagraphs();
      LimsSpELRenderDataCompute spELRenderDataCompute = new LimsSpELRenderDataCompute(EnvModel.ofModel(replaceDataMap), false);
      paragraphs.forEach(xwpfParagraph -> xwpfParagraph.getCTP().getBookmarkStartList().forEach(ctBookmark -> {
         String bookmarkName = ctBookmark.getName();
         bookmarkName = replaceBookmark(bookmarkName);
         Object replaceData = spELRenderDataCompute.compute(bookmarkName);
         if (replaceData != null) {
            if (replaceData instanceof PictureRenderData) {
               XWPFRun xwpfRun = xwpfParagraph.getRuns().stream().filter(run -> !CollectionUtils.isEmpty(run.getEmbeddedPictures())).findFirst().orElse(null);
               PictureRenderData pictureRenderData = (PictureRenderData)replaceData;
               if (xwpfRun != null) {
                  XWPFPicture picture = xwpfRun.getEmbeddedPictures().get(0);
                  int format = pictureRenderData.getPictureType().type();
                  byte[] data = pictureRenderData.getPictureSupplier().get();

                  try {
                     setPictureReference(picture, document.addPictureData(data, format));
                  } catch (InvalidFormatException var13) {
                     var13.printStackTrace();
                  }
               } else {
                  XWPFRun run = xwpfParagraph.createRun();

                  try {
                     Helper.renderPicture(run, pictureRenderData);
                  } catch (Exception var12) {
                     LOGGER.error("render pic ", (Throwable)var12);
                  }
               }
            } else {
               List<XWPFRun> runs = xwpfParagraph.getRuns();
               XWPFRun xwpfRun;
               if (CollectionUtils.isEmpty(runs)) {
                  xwpfRun = xwpfParagraph.createRun();
               } else {
                  for (int i = runs.size() - 1; i > 0; i--) {
                     xwpfParagraph.removeRun(i);
                  }

                  xwpfRun = runs.get(0);
               }

               com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsTextRenderPolicy.Helper.renderTextRun(xwpfRun, replaceData);
            }
         }
      }));
      document.getTables()
         .forEach(
            xwpfTable -> xwpfTable.getRows()
               .forEach(
                  tableRow -> {
                     for (XWPFTableCell tableCell : tableRow.getTableCells()) {
                        tableCell.getParagraphs()
                           .forEach(
                              xwpfParagraph -> xwpfParagraph.getCTP()
                                 .getBookmarkStartList()
                                 .forEach(
                                    ctBookmark -> {
                                       String bookmarkName = replaceBookmark(ctBookmark.getName());
                                       Object replaceData = spELRenderDataCompute.compute(bookmarkName);
                                       if (replaceData != null) {
                                          if (replaceData instanceof PictureRenderData) {
                                             XWPFRun xwpfRun = xwpfParagraph.getRuns()
                                                .stream()
                                                .filter(run -> !CollectionUtils.isEmpty(run.getEmbeddedPictures()))
                                                .findFirst()
                                                .orElse(null);
                                             PictureRenderData pictureRenderData = (PictureRenderData)replaceData;
                                             if (xwpfRun != null) {
                                                XWPFPicture picture = xwpfRun.getEmbeddedPictures().get(0);
                                                int format = pictureRenderData.getPictureType().type();
                                                byte[] data = pictureRenderData.getPictureSupplier().get();

                                                try {
                                                   setPictureReference(picture, document.addPictureData(data, format));
                                                } catch (InvalidFormatException var13) {
                                                   var13.printStackTrace();
                                                }
                                             } else {
                                                XWPFRun run = xwpfParagraph.createRun();

                                                try {
                                                   Helper.renderPicture(run, pictureRenderData);
                                                } catch (Exception var12) {
                                                   LOGGER.error("render pic ", (Throwable)var12);
                                                }
                                             }
                                          } else {
                                             List<XWPFRun> runs = xwpfParagraph.getRuns();
                                             XWPFRun xwpfRun;
                                             if (CollectionUtils.isEmpty(runs)) {
                                                xwpfRun = xwpfParagraph.createRun();
                                             } else {
                                                for (int i = runs.size() - 1; i > 0; i--) {
                                                   xwpfParagraph.removeRun(i);
                                                }

                                                xwpfRun = runs.get(0);
                                             }

                                             com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsTextRenderPolicy.Helper.renderTextRun(
                                                xwpfRun, replaceData
                                             );
                                          }
                                       }
                                    }
                                 )
                           );
                     }
                  }
               )
         );
   }

   private static void setPictureReference(XWPFPicture t, String relationId) {
      CTPicture ctPic = t.getCTPicture();
      CTBlipFillProperties bill = ctPic.getBlipFill();
      CTBlip blip = bill.getBlip();
      blip.setEmbed(relationId);
   }

   public static void addPicInRun(XWPFRun run, InputStream inputStream, int width, int height) {
      try {
         run.addPicture(inputStream, 5, "Generated", Units.pixelToEMU(width), Units.pixelToEMU(height));
         CTDrawing drawing = run.getCTR().getDrawingArray(0);
         CTGraphicalObject graphicalObject = drawing.getInlineArray(0).getGraphic();
         int textPosition = run.getTextPosition();
         CTAnchor anchor = getAnchorWithGraphic(
            graphicalObject,
            "Generated",
            Units.pixelToEMU(width),
            Units.pixelToEMU(height),
            Units.pixelToEMU(textPosition),
            Units.pixelToEMU(-height / 2),
            false
         );
         drawing.setAnchorArray(new CTAnchor[]{anchor});
         drawing.removeInline(0);
      } catch (InvalidFormatException var8) {
         LOGGER.error("close io error", (Throwable)var8);
      } catch (IOException var9) {
         var9.printStackTrace();
      }
   }

   public static void addCellValue(XWPFTableCell cell, String text) {
      text = StringUtils.isEmpty(text) ? "" : text;
      CTTc ctTc = cell.getCTTc();
      CTP ctP = ctTc.sizeOfPArray() == 0 ? ctTc.addNewP() : ctTc.getPArray(0);
      XWPFParagraph par = new XWPFParagraph(ctP, cell);
      XWPFRun run = par.createRun();
      com.sunwayworld.cloud.lims.reporttemplate.support.word.policy.LimsTextRenderPolicy.Helper.renderTextRun(run, text);
   }

   public static void addCellValue(XWPFTableCell cell, XWPFTemplate template, final Resolver resolver, RenderDataCompute dataCompute) {
      List<MetaTemplate> templates = resolver.resolveBodyElements(cell.getBodyElements());
      addCellValue(template, templates, resolver, dataCompute);
   }

   public static void addCellValue(XWPFTemplate template, List<MetaTemplate> templates, final Resolver resolver, RenderDataCompute dataCompute) {
      new DocumentProcessor(template, resolver, dataCompute).process(templates);
   }

   public static void addText(XWPFRun run, String text) {
      if (text.contains("</?su[b,p]>")) {
         addTextWithRun(run, text);
      } else {
         com.deepoove.poi.policy.TextRenderPolicy.Helper.renderTextRun(run, text);
      }
   }

   public static void addTextWithRun(XWPFRun run, String text) {
      Pattern pattern = Pattern.compile("</?su[b,p]>");
      Matcher matcher = pattern.matcher(text);
      int startPos = 0;

      while (matcher.find()) {
         String search = matcher.group();
         int searchPos = text.indexOf(search);
         String value = text.substring(startPos, searchPos);
         if (startPos != searchPos) {
            XWPFRun newRun = ((XWPFParagraph)run.getParent()).createRun();
            StyleUtils.styleRun(newRun, run);
            switch (search) {
               case "</sub>":
                  newRun.setSubscript(VerticalAlign.SUBSCRIPT);
                  break;
               case "</sup>":
                  newRun.setSubscript(VerticalAlign.SUPERSCRIPT);
            }

            String[] split = value.split("\\n", -1);
            if (split.length > 0) {
               newRun.setText(split[0], 0);
               boolean lineAtTable = split.length > 1 && TableTools.isInsideTable(run);

               for (int i = 1; i < split.length; i++) {
                  if (lineAtTable) {
                     newRun.addBreak(BreakType.TEXT_WRAPPING);
                  } else {
                     newRun.addCarriageReturn();
                  }

                  newRun.setText(split[i]);
               }
            }
         }

         startPos = searchPos + search.length();
      }

      if (startPos < text.length() - 1) {
         XWPFRun newRun = ((XWPFParagraph)run.getParent()).createRun();
         StyleUtils.styleRun(newRun, run);
         newRun.setText(text.substring(startPos));
      }
   }

   public static void build(String templatePath, OutputStream targetOutStream, Function<ConfigureBuilder, Object> paramsFunc, boolean useSpel) {
      try (InputStream is = new BufferedInputStream(new FileInputStream(templatePath))) {
         build(is, targetOutStream, paramsFunc, useSpel, true);
      } catch (IOException var17) {
         var17.printStackTrace();
      }
   }

   public static void build(
      String templatePath, OutputStream targetOutStream, Function<ConfigureBuilder, Object> paramsFunc, boolean useSpel, boolean focusUpdateFields
   ) {
      try (InputStream is = new BufferedInputStream(new FileInputStream(templatePath))) {
         build(is, targetOutStream, paramsFunc, useSpel, focusUpdateFields);
      } catch (IOException var18) {
         var18.printStackTrace();
      }
   }

   public static void build(
      InputStream templateStream, OutputStream targetOutStream, Function<ConfigureBuilder, Object> paramsFunc, boolean useSpel, boolean focusUpdateFields
   ) {
      build(templateStream, targetOutStream, paramsFunc, useSpel, focusUpdateFields, null);
   }

   public static void build(
      InputStream templateStream,
      OutputStream targetOutStream,
      Function<ConfigureBuilder, Object> paramsFunc,
      boolean useSpel,
      boolean focusUpdateFields,
      WaterMarkBean waterMarkBean
   ) {
      ConfigureBuilder builder = getConfigureBuilder(useSpel);
      Object params = paramsFunc.apply(builder);
      LOGGER.info("** BEGIN TO RENDER REPORT **");
      long renderStartTime = System.currentTimeMillis();
      XWPFTemplate template = XWPFTemplate.compile(templateStream, builder.build());
      template = template.render(params);
      LOGGER.info("** RENDER REPORT FINISHED - USED [{}s]", (System.currentTimeMillis() - renderStartTime) / 1000L);

      try {
         if (focusUpdateFields) {
            template.getXWPFDocument().getXWPFDocument().enforceUpdateFields();
         }

         if (waterMarkBean != null && !StringUtils.isEmpty(waterMarkBean.getText())) {
            XWPFDocument xwpfDocument = template.getXWPFDocument().getXWPFDocument();
            List<XWPFHeader> headerList = xwpfDocument.getHeaderList();
            XWPFHeaderFooterPolicy headerFooterPolicy = xwpfDocument.createHeaderFooterPolicy();
            AtomicInteger index = new AtomicInteger(1);
            if (CollectionUtils.isEmpty(headerList)) {
               XWPFHeader defaultHeader = headerFooterPolicy.createHeader(STHdrFtr.DEFAULT);
               CTHdrFtr defaultHeaderCtHdrFtr = defaultHeader._getHdrFtr();
               defaultHeaderCtHdrFtr.addNewP();
               defaultHeaderCtHdrFtr.setPArray(
                  defaultHeaderCtHdrFtr.getPList().size() - 1,
                  createWatermarkParagraph(xwpfDocument, waterMarkBean.getText(), index.getAndIncrement()).getCTP()
               );
               defaultHeader.setHeaderFooter(defaultHeaderCtHdrFtr);
               XWPFHeader evenHeader = headerFooterPolicy.createHeader(STHdrFtr.EVEN);
               CTHdrFtr evenHeaderCtHdrFtr = evenHeader._getHdrFtr();
               evenHeaderCtHdrFtr.addNewP();
               evenHeaderCtHdrFtr.setPArray(
                  evenHeaderCtHdrFtr.getPList().size() - 1, createWatermarkParagraph(xwpfDocument, waterMarkBean.getText(), index.getAndIncrement()).getCTP()
               );
               evenHeader.setHeaderFooter(evenHeaderCtHdrFtr);
               XWPFHeader firstHeader = headerFooterPolicy.createHeader(STHdrFtr.FIRST);
               CTHdrFtr firstHeaderCtHdrFtr = firstHeader._getHdrFtr();
               firstHeaderCtHdrFtr.addNewP();
               firstHeaderCtHdrFtr.setPArray(
                  firstHeaderCtHdrFtr.getPList().size() - 1, createWatermarkParagraph(xwpfDocument, waterMarkBean.getText(), index.getAndIncrement()).getCTP()
               );
               firstHeader.setHeaderFooter(firstHeaderCtHdrFtr);
            } else {
               headerList.forEach(
                  header -> {
                     CTHdrFtr defaultHeaderCtHdrFtr = header._getHdrFtr();
                     defaultHeaderCtHdrFtr.addNewP();
                     defaultHeaderCtHdrFtr.setPArray(
                        defaultHeaderCtHdrFtr.getPList().size() - 1,
                        createWatermarkParagraph(xwpfDocument, waterMarkBean.getText(), index.getAndIncrement()).getCTP()
                     );
                     header.setHeaderFooter(defaultHeaderCtHdrFtr);
                  }
               );
               XWPFHeader defaultHeader = headerFooterPolicy.getDefaultHeader();
               if (defaultHeader == null) {
                  headerFooterPolicy.createHeader(
                     STHdrFtr.DEFAULT, new XWPFParagraph[]{createWatermarkParagraph(xwpfDocument, waterMarkBean.getText(), index.getAndIncrement())}
                  );
               }

               XWPFHeader evenHeader = headerFooterPolicy.getEvenPageHeader();
               if (evenHeader == null) {
                  headerFooterPolicy.createHeader(
                     STHdrFtr.EVEN, new XWPFParagraph[]{createWatermarkParagraph(xwpfDocument, waterMarkBean.getText(), index.getAndIncrement())}
                  );
               }

               XWPFHeader firstHeader = headerFooterPolicy.getFirstPageHeader();
               if (firstHeader == null) {
                  headerFooterPolicy.createHeader(
                     STHdrFtr.FIRST, new XWPFParagraph[]{createWatermarkParagraph(xwpfDocument, waterMarkBean.getText(), index.getAndIncrement())}
                  );
               }
            }
         }

         template.write(targetOutStream);
      } catch (IOException var29) {
         LOGGER.error("WRITE TO FILE ERROR", (Throwable)var29);
      } finally {
         try {
            template.close();
         } catch (IOException var28) {
            LOGGER.error(var28.getMessage());
         }
      }
   }

   private static ConfigureBuilder getConfigureBuilder(boolean useSpel) {
      ConfigureBuilder builder = Configure.builder();
      builder.buildGramer("${", "}");
      builder.addPlugin(GramerSymbol.TEXT.getSymbol(), new LimsTextRenderPolicy());
      builder.addPlugin('%', new LimsSuspensionPicRenderPolicy());
      builder.addPlugin('*', new LimsNumberingRenderPolicy());
      builder.addPlugin('#', new LimsHackLoopTableRenderPolicy());
      builder.addPlugin('+', new LimsMultiAbleDocxRenderPolicy());
      builder.addPlugin('^', new LimsCustomizeRenderPolicy());
      if (useSpel) {
         builder.useSpringEL(false);
         builder.setRenderDataComputeFactory(model -> new LimsSpELRenderDataCompute(model, false));
      }

      return builder;
   }

   public static CTAnchor getAnchorWithGraphic(
      CTGraphicalObject ctGraphicalObject, String deskFileName, int width, int height, int leftOffset, int topOffset, boolean behind
   ) {
      String anchorXML = "<wp:anchor xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" simplePos=\"0\" relativeHeight=\"0\" behindDoc=\""
         + (behind ? 1 : 0)
         + "\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\"><wp:simplePos x=\"0\" y=\"0\"/><wp:positionH relativeFrom=\"column\"><wp:posOffset>"
         + leftOffset
         + "</wp:posOffset></wp:positionH><wp:positionV relativeFrom=\"paragraph\"><wp:posOffset>"
         + topOffset
         + "</wp:posOffset></wp:positionV><wp:extent cx=\""
         + width
         + "\" cy=\""
         + height
         + "\"/><wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/><wp:wrapNone/><wp:docPr id=\"1\" name=\"Drawing 0\" descr=\""
         + deskFileName
         + "\"/><wp:cNvGraphicFramePr/></wp:anchor>";

      try {
         CTDrawing drawing = org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing.Factory.parse(anchorXML);
         CTAnchor anchor = drawing.getAnchorArray(0);
         anchor.setGraphic(ctGraphicalObject);
         return anchor;
      } catch (XmlException var10) {
         var10.printStackTrace();
         return null;
      }
   }

   private static int getColNum(XWPFTableCell cell) {
      int colNum = 0;
      XWPFTableRow row = cell.getTableRow();
      List<XWPFTableCell> cells = row.getTableCells();

      for (int i = 0; i < cells.size(); i++) {
         XWPFTableCell tableCell = cells.get(i);
         if (tableCell.getCTTc() == cell.getCTTc()) {
            colNum = i + 1;
         }
      }

      return colNum;
   }

   private static int getRowNum(XWPFTableRow row) {
      XWPFTable table = row.getTable();
      int rowNum = 0;
      List<XWPFTableRow> rows = table.getRows();

      for (int i = 0; i < rows.size(); i++) {
         XWPFTableRow tableRow = rows.get(i);
         if (tableRow.getCtRow() == row.getCtRow()) {
            rowNum = i + 1;
         }
      }

      return rowNum;
   }

   public static CTTcPr getTcPr(CTTc ctTc) {
      return ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
   }

   public static CTDecimalNumber getGridSpan(CTTcPr tcPr) {
      return tcPr.isSetGridSpan() ? tcPr.getGridSpan() : tcPr.addNewGridSpan();
   }

   public static void splitCell(
      XWPFTable table, XWPFTableRow row, Integer splitCellIndex, Integer splitSize, List<String> splitValue, TableStyle tableStyle, boolean initOtherGridSpan
   ) {
      XWPFTableCell splitCell = row.getCell(splitCellIndex);
      CTTcPr splitTcPr = getTcPr(splitCell);
      CTVMerge vMerge = splitTcPr.getVMerge();
      int gridSpanCountOnBefore = getGridSpanCountOnBefore(row, splitCell);
      if (splitSize == 1) {
         if (!ObjectUtils.isEmpty(splitValue)) {
            addCellValue(splitCell, splitValue.get(0));
         }
      } else {
         CTTblGrid ctTblGrid = table.getCTTbl().getTblGrid();
         CTTblGridCol splitGridCol = ctTblGrid.getGridColArray(gridSpanCountOnBefore);
         BigInteger cellWidth = splitGridCol.getW();
         if (initOtherGridSpan) {
            cellWidth = cellWidth.divide(BigInteger.valueOf(splitSize.intValue()));
            int addEndPosition = splitCellIndex + splitSize;
            ctTblGrid.removeGridCol(splitCellIndex);

            for (int i = 0; i < splitSize; i++) {
               CTTblGridCol gridCol = ctTblGrid.insertNewGridCol(splitCellIndex + i);
               gridCol.setW(cellWidth);
            }
         }

         splitCell(table, row, splitCell, splitSize.intValue(), splitCellIndex, cellWidth, splitValue);
         if (vMerge != null) {
            int i = getRowIndex(table, row) + 1;

            while (true) {
               XWPFTableRow nextRow = table.getRow(i);
               int nextCellIndex = getCellIndexByGridSpanCountOnBefore(nextRow, gridSpanCountOnBefore);
               XWPFTableCell gridSpanCell = nextRow.getCell(nextCellIndex);
               CTTcPr tcPr = getTcPr(gridSpanCell);
               if (tcPr.getVMerge() == null) {
                  break;
               }

               splitCell(table, nextRow, gridSpanCell, splitSize.intValue(), nextCellIndex, cellWidth, splitValue);
               i++;
            }
         }

         AtomicInteger index = new AtomicInteger(0);
         int rowIndex = getRowIndex(table, row);
         if (initOtherGridSpan) {
            table.getRows().forEach(nextRow -> {
               if (rowIndex > index.getAndIncrement()) {
                  int nextCellIndexx = getCellIndexByGridSpanCountOnBefore(nextRow, gridSpanCountOnBefore);
                  CTTcPr tcPrx = getTcPr(nextRow.getCell(nextCellIndexx));
                  if (!tcPrx.isSetGridSpan()) {
                     tcPrx.addNewGridSpan();
                  }

                  BigInteger oldValue = Optional.ofNullable(tcPrx.getGridSpan().getVal()).orElse(BigInteger.valueOf(1L));
                  tcPrx.getGridSpan().setVal(oldValue.add(BigInteger.valueOf(splitSize - 1)));
               }
            });
         }
      }
   }

   public static void addCellWidth(XWPFTableCell tableCell, BigInteger width) {
      CTTblWidth tcW = tableCell.getCTTc().getTcPr().getTcW();
      if (tcW == null) {
         tcW = tableCell.getCTTc().getTcPr().addNewTcW();
      }

      tcW.setW(width);
   }

   public static XWPFTableCell getGridSpanCell(XWPFTableRow row, int splitCellIndex) {
      int totalGridSanIndex = 0;

      for (XWPFTableCell cell : row.getTableCells()) {
         CTTcPr tcPr = cell.getCTTc().getTcPr();
         if (tcPr.isSetGridSpan()) {
            totalGridSanIndex += tcPr.getGridSpan().getVal().intValue() == 0 ? 1 : tcPr.getGridSpan().getVal().intValue();
         } else {
            totalGridSanIndex++;
         }

         if (totalGridSanIndex >= splitCellIndex + 1) {
            return cell;
         }
      }

      return row.getCell(splitCellIndex);
   }

   public static XWPFTableCell getCurrentCell(XWPFRun run) {
      try {
         if (!TableTools.isInsideTable(run)) {
            throw new IllegalStateException("The template tag  must be inside a table");
         } else {
            return (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
         }
      } catch (Exception var2) {
         throw new RenderException("dynamic table error:" + var2.getMessage(), var2);
      }
   }

   public static int getRowIndex(XWPFTable table, XWPFTableRow row) {
      List<XWPFTableRow> rows = table.getRows();
      return rows.indexOf(row);
   }

   public static int getGridSpanCountOnBefore(XWPFTableRow row, XWPFTableCell cell) {
      int cellIndex = getCellIndex(row, cell);
      int gridSpanCount = 0;

      for (int i = 0; i < cellIndex; i++) {
         XWPFTableCell checkCell = row.getCell(i);
         CTTcPr tcPr = getTcPr(checkCell);
         CTDecimalNumber gridSpan = tcPr.getGridSpan();
         if (gridSpan != null) {
            BigInteger val = gridSpan.getVal();
            gridSpanCount += val.intValue();
         } else {
            gridSpanCount++;
         }
      }

      return gridSpanCount;
   }

   public static int getCellIndexByGridSpanCountOnBefore(XWPFTableRow row, int gridSpanCount) {
      List<XWPFTableCell> cellList = row.getTableCells();
      AtomicInteger cellIndex = new AtomicInteger(0);
      AtomicInteger checkGridSpanIndex = new AtomicInteger(0);
      cellList.stream().anyMatch(cell -> {
         if (checkGridSpanIndex.get() == gridSpanCount) {
            cellIndex.set(getCellIndex(row, cell));
            return true;
         } else {
            CTTcPr tcPr = getTcPr(cell);
            CTDecimalNumber gridSpan = tcPr.getGridSpan();
            if (gridSpan == null) {
               checkGridSpanIndex.getAndIncrement();
            } else {
               int spanNum = gridSpan.getVal().intValue();
               checkGridSpanIndex.addAndGet(spanNum);
            }

            cellIndex.getAndIncrement();
            return false;
         }
      });
      return cellIndex.get();
   }

   public static int getTagRowIndex(XWPFTable table, XWPFTableRow row, int cellIndex) {
      XWPFTableCell cell = row.getCell(cellIndex);
      CTTcPr tcPr = getTcPr(cell);
      CTVMerge vMerge = tcPr.getVMerge();
      List<XWPFTableRow> rows = table.getRows();
      int gridSpanCountOnBefore = getGridSpanCountOnBefore(row, cell);
      int currentRowIndex = rows.indexOf(row);
      if (vMerge != null) {
         for (int index = currentRowIndex + 1; index < rows.size(); index++) {
            XWPFTableRow nextRow = table.getRow(index);
            XWPFTableCell checkCell = nextRow.getCell(getCellIndexByGridSpanCountOnBefore(nextRow, gridSpanCountOnBefore));
            CTVMerge checkVMerge = getTcPr(checkCell).getVMerge();
            if (checkVMerge == null || STMerge.RESTART.equals(checkVMerge.getVal())) {
               currentRowIndex = index - 1;
               break;
            }
         }
      }

      return currentRowIndex;
   }

   public static void splitCellsVertically(XWPFTableCell cell, int cellNum, int rowNum) {
      XWPFTableRow tableRow = cell.getTableRow();
      XWPFTable table = tableRow.getTable();
      List<XWPFTableCell> tableCells = tableRow.getTableCells();
      int cellIndex = tableCells.indexOf(cell);
      splitCell(table, tableRow, cellIndex, cellNum, null, null, true);
      int rowIndex = getRowIndex(table, tableRow);
      boolean initRowCell = false;

      for (int i = 0; i < rowNum - 1; i++) {
         XWPFTableRow newRow = copyTableRowFromTemplateRow(table, tableRow, rowIndex + 1);

         for (int j = 0; j < tableCells.size(); j++) {
            if (cellIndex > j || j >= cellIndex + cellNum) {
               if (!initRowCell) {
                  setCellMerge(tableRow.getCell(i), true);
               }

               XWPFTableCell newCell = newRow.getCell(i);

               for (XWPFParagraph p : newCell.getParagraphs()) {
                  newCell.removeParagraph(0);
               }

               setCellMerge(newCell, false);
            }
         }
      }
   }

   public static void setCellMerge(XWPFTableCell cell, boolean restart) {
      CTTcPr tcPr = getTcPr(cell);
      CTVMerge vMerge = tcPr.getVMerge();
      if (vMerge == null) {
         vMerge = tcPr.addNewVMerge();
      }

      if (restart) {
         vMerge.setVal(STMerge.RESTART);
      } else {
         vMerge.setVal(STMerge.CONTINUE);
         cell.getParagraphs().forEach(paragraph -> {
            List<CTR> rList = paragraph.getCTP().getRList();

            for (int i = 0; i < rList.size(); i++) {
               paragraph.getCTP().removeR(i);
            }
         });
      }
   }

   public static void splitCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
      if (toRow > fromRow) {
         for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            CTTcPr tcPr = getTcPr(cell);
            if (tcPr.isSetVMerge()) {
               tcPr.unsetVMerge();
            }
         }
      }
   }

   public static XWPFTableRow copyTableRowFromTemplateRow(XWPFTable table, XWPFTableRow templateRow, int insertPosition) {
      XWPFTableRow copyRow = table.insertNewTableRow(insertPosition);
      if (templateRow.getCtRow() != null) {
         copyRow.getCtRow().setTrPr(templateRow.getCtRow().getTrPr());
         CTTrPr trPr = copyRow.getCtRow().getTrPr();
         if (trPr.isNil()) {
            copyRow.getCtRow().unsetTrPr();
         }
      }

      for (int i = 0; i < templateRow.getTableCells().size(); i++) {
         XWPFTableCell templateCell = templateRow.getCell(i);
         XWPFTableCell copyCell = copyRow.getCell(i);
         if (copyCell == null) {
            copyCell = copyRow.addNewTableCell();
         }

         copyTableCell(templateCell, copyCell);
      }

      return copyRow;
   }

   public static void setTableRow(XWPFTable table, XWPFTableRow templateRow, int pos) {
      List<XWPFTableRow> rows = (List<XWPFTableRow>)ReflectionUtils.getValue("tableRows", table);
      rows.add(pos, templateRow);
      table.getCTTbl().getTrList().add(pos, templateRow.getCtRow());
   }

   public static CTTcPr getTcPr(XWPFTableCell cell) {
      return cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
   }

   private static void splitCell(
      XWPFTable table, XWPFTableRow row, XWPFTableCell splitCell, int splitSize, int splitCellIndex, BigInteger cellWidth, List<String> splitValue
   ) {
      BigInteger leftBorderSz = null;
      if (splitCell.getCTTc().getTcPr() != null && splitCell.getCTTc().getTcPr().getTcBorders() != null) {
         CTTcBorders tcBorders = splitCell.getCTTc().getTcPr().getTcBorders();
         leftBorderSz = tcBorders.getLeft() == null ? null : tcBorders.getLeft().getSz();
      }

      CTTcPr splitCellTcPr = getTcPr(splitCell);

      for (int i = 1; i <= splitSize; i++) {
         CTTc ctTc = row.getCtRow().insertNewTc(splitCellIndex + i);
         XWPFTableCell tableCell = new XWPFTableCell(ctTc, row, table.getBody());
         copyTableCell(splitCell, tableCell);
         addCellWidth(tableCell, cellWidth);
         row.getTableCells().add(splitCellIndex + i, tableCell);
         tableCell.setVerticalAlignment(XWPFVertAlign.CENTER);
         if (!ObjectUtils.isEmpty(splitValue)) {
            addCellValue(tableCell, splitValue.get(i - 1));
         }

         CTTcPr tcPr = tableCell.getCTTc().getTcPr();
         if (tcPr != null && leftBorderSz != null) {
            CTTcBorders tcBorders = tcPr.getTcBorders();
            if (tcBorders == null) {
               tcBorders = tcPr.addNewTcBorders();
            }

            CTBorder right = tcBorders.getRight();
            if (right == null) {
               right = tcBorders.addNewRight();
            }

            if (i < splitSize) {
               right.setSz(leftBorderSz);
            }
         }

         if (getTcPr(splitCell).getVMerge() != null) {
            getTcPr(tableCell).setVMerge(getTcPr(splitCell).getVMerge());
         }
      }

      row.getCtRow().removeTc(splitCellIndex);
      row.removeCell(splitCellIndex);
   }

   public static void copyTableCell(XWPFTableCell source, XWPFTableCell target) {
      if (source.getCTTc() != null) {
         target.getCTTc().setTcPr(source.getCTTc().getTcPr());
      }

      for (int pos = 0; pos < target.getParagraphs().size(); pos++) {
         target.removeParagraph(pos);
      }

      for (XWPFParagraph sp : source.getParagraphs()) {
         XWPFParagraph targetP = target.addParagraph();
         copyParagraph(targetP, sp);
         XmlCursor xmlCursor = targetP.getCTP().newCursor();
         xmlCursor.toPrevSibling();
         XWPFParagraph paragraph = target.insertNewParagraph(xmlCursor);
         copyParagraph(paragraph, targetP);
         target.removeParagraph(1);
      }
   }

   public static void copyParagraph(XWPFParagraph target, XWPFParagraph source) {
      target.getCTP().setPPr(source.getCTP().getPPr());

      for (int pos = target.getRuns().size() - 1; pos >= 0; pos--) {
         target.removeRun(pos);
      }

      for (XWPFRun s : source.getRuns()) {
         XWPFRun targetrun = target.createRun();
         copyRun(targetrun, s);
      }
   }

   public static void copyParagraphWithPre(XWPFParagraph target, XWPFParagraph source, String text) {
      target.getCTP().setPPr(source.getCTP().getPPr());

      for (int pos = target.getRuns().size() - 1; pos >= 0; pos--) {
         target.removeRun(pos);
      }

      List<XWPFRun> runList = source.getRuns();

      for (int i = 0; i < runList.size(); i++) {
         XWPFRun run = runList.get(i);
         XWPFRun targetrun = target.createRun();
         if (i == 0) {
            copyRunWithPre(targetrun, run, text);
         } else {
            copyRun(targetrun, run);
         }
      }
   }

   public static void copyRun(XWPFRun target, XWPFRun source) {
      target.getCTR().setRPr(source.getCTR().getRPr());
      target.setText(source.getText(0));
   }

   public static void copyRunWithPre(XWPFRun target, XWPFRun source, String text) {
      target.getCTR().setRPr(source.getCTR().getRPr());
      target.setText(text + (StringUtils.isEmpty(source.getText(0)) ? "" : source.getText(0)));
   }

   public static int getCellIndex(XWPFTableRow row, XWPFTableCell cell) {
      List<XWPFTableCell> cells = row.getTableCells();
      return cells.indexOf(cell);
   }

   public static boolean containCornerMark(String text) {
      return StringUtils.contains(text, "<sub>") || StringUtils.contains(text, "<sup>");
   }

   private static List<CTPicture> getCTPictures(XmlObject o) {
      List<CTPicture> pics = new ArrayList<>();
      XmlObject[] picts = o.selectPath("declare namespace pic='" + CTPicture.type.getName().getNamespaceURI() + "' .//pic:pic");

      for (XmlObject pict : picts) {
         if (pict instanceof XmlAnyTypeImpl) {
            try {
               pict = org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture.Factory.parse(pict.toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            } catch (XmlException var8) {
               throw new POIXMLException(var8);
            }
         }

         if (pict instanceof CTPicture) {
            pics.add((CTPicture)pict);
         }
      }

      return pics;
   }

   private static String replaceBookmark(String bookmarkName) {
      bookmarkName = StringUtils.replace(bookmarkName, "（", "(");
      bookmarkName = StringUtils.replace(bookmarkName, "）", ")");
      bookmarkName = StringUtils.replace(bookmarkName, "，", ",");
      bookmarkName = StringUtils.replace(bookmarkName, "“", "\"");
      return StringUtils.replace(bookmarkName, "”", "\"");
   }
}
