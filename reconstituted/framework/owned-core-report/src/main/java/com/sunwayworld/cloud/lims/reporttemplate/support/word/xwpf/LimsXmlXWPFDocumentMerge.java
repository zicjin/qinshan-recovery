package com.sunwayworld.cloud.lims.reporttemplate.support.word.xwpf;

import com.deepoove.poi.xwpf.AbstractXWPFDocumentMerge;
import com.deepoove.poi.xwpf.BodyContainer;
import com.deepoove.poi.xwpf.BodyContainerFactory;
import com.deepoove.poi.xwpf.DefaultXmlOptions;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.deepoove.poi.xwpf.XWPFNumberingWrapper;
import com.deepoove.poi.xwpf.XWPFOnOff;
import com.sunwayworld.cloud.lims.reporttemplate.support.constant.MergeDocxPageBreakEnum;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.poi.ooxml.POIXMLDocumentPart.RelationPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFChart;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP.Factory;

public class LimsXmlXWPFDocumentMerge extends AbstractXWPFDocumentMerge {
   private static final String CROSS_REPLACE_STRING = "@PoiTL@";
   private Map<String, String> styleCacheThreadLocal = new HashMap<>();
   private Boolean breakPage = false;
   private int pageBreakLevel = 7;

   @Override
   public NiceXWPFDocument merge(NiceXWPFDocument source, Iterator<NiceXWPFDocument> mergeIterator, XWPFRun run) throws Exception {
      CTBody body = source.getDocument().getBody();
      List<String> addParts = this.createMergeableStrings(source, mergeIterator);
      String[] startEnd = this.truncatedStartEndXmlFragment(body);
      XWPFParagraph paragraph = (XWPFParagraph)run.getParent();
      if (this.breakPage && MergeDocxPageBreakEnum.isAdd(this.pageBreakLevel, MergeDocxPageBreakEnum.FIRST)) {
         BodyContainer container = BodyContainerFactory.getBodyContainer(run);
         XWPFRun breakRun = container.insertNewParagraph(run).createRun();
         breakRun.addBreak(BreakType.PAGE);
      }

      CTP mergedContainer = paragraph.getCTP();
      CTP mergedBody = Factory.parse(startEnd[0] + String.join("", addParts) + startEnd[1]);
      mergedContainer.set(mergedBody);
      String xmlText = this.truncatedOverlapWP(body);
      CTBody parse = null;

      try {
         parse = org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody.Factory.parse(xmlText);
      } catch (Exception var13) {
         xmlText = this.truncatedOverlapWPzbf(body);
         parse = org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody.Factory.parse(xmlText);
      }

      body.set(parse);
      this.styleCacheThreadLocal.clear();
      return source.generate(true);
   }

   private String truncatedOverlapWP(CTBody body) {
      String xmlText = body.xmlText(DefaultXmlOptions.OPTIONS_INNER);
      xmlText = xmlText.replaceAll("<w:p><w:p>", "<w:p>")
         .replaceAll("<w:p><w:p\\s", "<w:p ")
         .replaceAll("<w:p><w:tbl>", "<w:tbl>")
         .replaceAll("<w:p><w:tbl\\s", "<w:tbl ");
      return xmlText.replaceAll("</w:sectPr></w:p>", "</w:sectPr>")
         .replaceAll("</w:p></w:p>", "</w:p>")
         .replaceAll("</w:tbl></w:p>", "</w:tbl>")
         .replaceAll("<w:p(\\s[A-Za-z0-9:\\s=\"]*)?/></w:p>", "")
         .replaceAll("</w:p><w:bookmarkEnd(\\s[A-Za-z0-9:\\s=\"]*)?/></w:p>", "</w:p>");
   }

   private String truncatedOverlapWPzbf(CTBody body) {
      return body.xmlText(DefaultXmlOptions.OPTIONS_INNER);
   }

   private String[] truncatedStartEndXmlFragment(CTBody body) {
      String srcString = body.xmlText(DefaultXmlOptions.OPTIONS_INNER);
      if (!srcString.startsWith("<xml-fragment")) {
         body.addNewSectPr();
         srcString = body.xmlText(DefaultXmlOptions.OPTIONS_INNER);
      }

      String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
      String suffix = srcString.substring(srcString.lastIndexOf("<"));
      return new String[]{prefix, suffix};
   }

   private List<String> createMergeableStrings(NiceXWPFDocument source, Iterator<NiceXWPFDocument> iterator) throws OpenXML4JException, IOException {
      List<String> addParts = new ArrayList<>();
      if (!iterator.hasNext()) {
         return addParts;
      } else {
         NiceXWPFDocument next = iterator.next();
         this.styleCacheThreadLocal.putAll(this.mergeStyles(source, next));
         this.mergeNamespaces(source, next);

         while (true) {
            if (this.breakPage && iterator.hasNext() && MergeDocxPageBreakEnum.isAdd(this.pageBreakLevel, MergeDocxPageBreakEnum.MIDDLE)
               || this.breakPage && !iterator.hasNext() && MergeDocxPageBreakEnum.isAdd(this.pageBreakLevel, MergeDocxPageBreakEnum.LAST)) {
               XWPFParagraph lastParagraph = next.getLastParagraph();
               XWPFRun breakRun = lastParagraph.createRun();
               breakRun.addBreak(BreakType.PAGE);
            }

            addParts.add(this.createMergeableString(source, next));

            try {
               next.close();
            } catch (Exception var7) {
               this.logger.warn("close merged doc failed!", (Throwable)var7);
            }

            if (!iterator.hasNext()) {
               return addParts;
            }

            next = iterator.next();
         }
      }
   }

   private void mergeNamespaces(NiceXWPFDocument source, NiceXWPFDocument docMerge) {
      CTDocument1 document = source.getDocument();
      XmlCursor newCursor = document.newCursor();
      if (this.toStartCursor(newCursor)) {
         CTDocument1 documentMerge = docMerge.getDocument();
         XmlCursor mergeCursor = documentMerge.newCursor();
         if (this.toStartCursor(mergeCursor)) {
            Map<String, String> addToThis = new HashMap<>();
            mergeCursor.getAllNamespaces(addToThis);
            addToThis.forEach(newCursor::insertNamespace);
         }

         mergeCursor.dispose();
      }

      newCursor.dispose();
   }

   private boolean toStartCursor(XmlCursor newCursor) {
      while (!newCursor.currentTokenType().isStart()) {
         if (!newCursor.hasNextToken()) {
            return false;
         }

         newCursor.toNextToken();
      }

      return true;
   }

   private String createMergeableString(NiceXWPFDocument source, NiceXWPFDocument merged) throws OpenXML4JException, IOException {
      CTBody mergedBody = merged.getDocument().getBody();
      Map<String, String> numIdsMap = this.mergeNumbering(source, merged);
      Map<String, String> blipIdsMap = this.mergePicture(source, merged);
      Map<String, String> externalBlipIdsMap = this.mergeExternalPicture(source, merged);
      Map<String, String> embeddedPartIdMap = this.mergeEmbeddedPart(source, merged);
      Map<String, String> hyperlinkMap = this.mergeHyperlink(source, merged);
      Map<String, String> chartIdsMap = this.mergeChart(source, merged);
      String appendString = mergedBody.xmlText(DefaultXmlOptions.OPTIONS_OUTER);
      String addPart = this.ridSectPr(appendString);
      Map<String, String> styleIdsMap = this.styleCacheThreadLocal;

      for (String styleId : styleIdsMap.keySet()) {
         addPart = addPart.replaceAll("<w:pStyle\\sw:val=\"" + styleId + "\"", "<w:pStyle w:val=\"" + styleIdsMap.get(styleId) + "\"")
            .replaceAll("<w:tblStyle\\sw:val=\"" + styleId + "\"", "<w:tblStyle w:val=\"" + styleIdsMap.get(styleId) + "\"")
            .replaceAll("<w:rStyle\\sw:val=\"" + styleId + "\"", "<w:rStyle w:val=\"" + styleIdsMap.get(styleId) + "\"");
      }

      Map<String, String> placeHolderblipIdsMap = new HashMap<>();

      for (String relaId : blipIdsMap.keySet()) {
         placeHolderblipIdsMap.put(relaId, blipIdsMap.get(relaId) + "@PoiTL@");
      }

      for (String relaId : placeHolderblipIdsMap.keySet()) {
         addPart = addPart.replaceAll("r:embed=\"" + relaId + "\"", "r:embed=\"" + placeHolderblipIdsMap.get(relaId) + "\"");
         addPart = addPart.replaceAll("r:id=\"" + relaId + "\"", "r:id=\"" + placeHolderblipIdsMap.get(relaId) + "\"");
      }

      Map<String, String> placeHolderExternalblipIdsMap = new HashMap<>();

      for (String relaId : externalBlipIdsMap.keySet()) {
         placeHolderExternalblipIdsMap.put(relaId, blipIdsMap.get(relaId) + "@PoiTL@");
      }

      for (String relaId : placeHolderExternalblipIdsMap.keySet()) {
         addPart = addPart.replaceAll("r:link=\"" + relaId + "\"", "r:link=\"" + placeHolderExternalblipIdsMap.get(relaId) + "\"");
      }

      for (String sourceRid : embeddedPartIdMap.keySet()) {
         addPart = addPart.replaceAll("r:id=\"" + sourceRid + "\"", "r:id=\"" + embeddedPartIdMap.get(sourceRid) + "\"");
      }

      for (String relaId : hyperlinkMap.keySet()) {
         hyperlinkMap.put(relaId, hyperlinkMap.get(relaId) + "@PoiTL@");
      }

      for (String relaId : hyperlinkMap.keySet()) {
         addPart = addPart.replaceAll("r:id=\"" + relaId + "\"", "r:id=\"" + hyperlinkMap.get(relaId) + "\"");
      }

      for (String relaId : chartIdsMap.keySet()) {
         chartIdsMap.put(relaId, chartIdsMap.get(relaId) + "@PoiTL@");
      }

      for (String relaId : chartIdsMap.keySet()) {
         addPart = addPart.replaceAll("r:id=\"" + relaId + "\"", "r:id=\"" + chartIdsMap.get(relaId) + "\"");
      }

      for (String relaId : numIdsMap.keySet()) {
         numIdsMap.put(relaId, numIdsMap.get(relaId) + "@PoiTL@");
      }

      for (String numId : numIdsMap.keySet()) {
         addPart = addPart.replaceAll("<w:numId\\sw:val=\"" + numId + "\"", "<w:numId w:val=\"" + numIdsMap.get(numId) + "\"");
      }

      return addPart.replaceAll("@PoiTL@", "");
   }

   private String ridSectPr(String appendString) {
      String addPart = "";
      int begin = appendString.indexOf(">") + 1;
      int end = appendString.lastIndexOf("<");
      if (begin >= end) {
         return addPart;
      } else {
         addPart = appendString.substring(begin, end);

         while (-1 != addPart.lastIndexOf("<w:sectPr")) {
            String prefix = addPart.substring(0, addPart.lastIndexOf("<w:sectPr"));
            String suffix = addPart.substring(addPart.lastIndexOf("</w:sectPr>") + 11, addPart.length());
            addPart = prefix + suffix;
         }

         return addPart;
      }
   }

   private Map<String, String> mergePicture(NiceXWPFDocument source, NiceXWPFDocument merged) throws InvalidFormatException {
      Map<String, String> blipIdsMap = new HashMap<>();

      for (XWPFPictureData xwpfPictureData : merged.getAllPictures()) {
         String relationId = merged.getRelationId(xwpfPictureData);
         String blidId = source.addPictureData(xwpfPictureData.getData(), xwpfPictureData.getPictureType());
         blipIdsMap.put(relationId, blidId);
      }

      return blipIdsMap;
   }

   private Map<String, String> mergeEmbeddedPart(NiceXWPFDocument source, NiceXWPFDocument merged) throws OpenXML4JException {
      Map<String, String> packageIdsMap = new HashMap<>();
      List<PackagePart> allEmbeddedParts = merged.getAllEmbeddedParts();
      PackagePart part = merged.getPackagePart();
      PackagePart packagePart = source.getPackagePart();

      for (PackageRelationship rel : merged.getPackagePart()
         .getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject")) {
         PackagePart relatedPart = part.getRelatedPart(rel);
         PackageRelationship newRelationship = packagePart.addRelationship(relatedPart.getPartName(), rel.getTargetMode(), relatedPart.getContentType());
         String id = newRelationship.getId();
         packageIdsMap.put(rel.getId(), id);
      }

      for (PackageRelationship rel : merged.getPackagePart()
         .getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package")) {
         PackagePart relatedPart = part.getRelatedPart(rel);
         PackageRelationship newRelationship = packagePart.addRelationship(relatedPart.getPartName(), rel.getTargetMode(), relatedPart.getContentType());
         String id = newRelationship.getId();
         packageIdsMap.put(rel.getId(), id);
      }

      return packageIdsMap;
   }

   private Map<String, String> mergeExternalPicture(NiceXWPFDocument source, NiceXWPFDocument merged) throws InvalidFormatException {
      Map<String, String> blipIdsMap = new HashMap<>();

      for (PackageRelationship relationship : merged.getPackagePart()
         .getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/image")) {
         if (relationship.getTargetMode() == TargetMode.EXTERNAL) {
            PackageRelationship relationshipNew = source.getPackagePart()
               .addExternalRelationship(relationship.getTargetURI().toString(), XWPFRelation.IMAGES.getRelation());
            blipIdsMap.putIfAbsent(relationship.getId(), relationshipNew.getId());
         }
      }

      return blipIdsMap;
   }

   private Map<String, String> mergeNumbering(NiceXWPFDocument source, NiceXWPFDocument merged) {
      Map<String, String> numIdsMap = new HashMap<>();
      XWPFNumbering numberingMerge = merged.getNumbering();
      if (null == numberingMerge) {
         return numIdsMap;
      } else {
         XWPFNumberingWrapper wrapperMerge = new XWPFNumberingWrapper(numberingMerge);
         List<XWPFNum> nums = wrapperMerge.getNums();
         if (null == nums) {
            return numIdsMap;
         } else {
            XWPFNumbering numbering = source.getNumbering();
            if (null == numbering) {
               numbering = source.createNumbering();
            }

            XWPFNumberingWrapper wrapper = new XWPFNumberingWrapper(numbering);
            Map<BigInteger, CTAbstractNum> cache = new HashMap<>();
            Map<BigInteger, CTAbstractNum> ret = new HashMap<>();

            for (XWPFNum xwpfNum : nums) {
               BigInteger mergeNumId = xwpfNum.getCTNum().getNumId();
               CTAbstractNum cTAbstractNum = cache.get(xwpfNum.getCTNum().getAbstractNumId().getVal());
               if (null == cTAbstractNum) {
                  XWPFAbstractNum xwpfAbstractNum = numberingMerge.getAbstractNum(xwpfNum.getCTNum().getAbstractNumId().getVal());
                  if (null == xwpfAbstractNum) {
                     this.logger.warn("cannot find cTAbstractNum by XWPFNum.");
                     continue;
                  }

                  cTAbstractNum = xwpfAbstractNum.getCTAbstractNum();
                  if (cTAbstractNum.isSetNsid()) {
                     cTAbstractNum.unsetNsid();
                  }

                  if (cTAbstractNum.isSetTmpl()) {
                     cTAbstractNum.unsetTmpl();
                  }

                  cache.put(xwpfNum.getCTNum().getAbstractNumId().getVal(), cTAbstractNum);
               }

               ret.put(mergeNumId, cTAbstractNum);
            }

            long nextId = wrapper.getNextAbstractNumID().longValue();

            for (CTAbstractNum abnum : new HashSet<>(ret.values())) {
               abnum.setAbstractNumId(BigInteger.valueOf(nextId++));
            }

            XWPFNumbering finalNumbering = numbering;
            ret.forEach((mergeNumId, abnumx) -> {
               BigInteger numID = finalNumbering.addNum(finalNumbering.addAbstractNum(new XWPFAbstractNum(abnumx)));
               numIdsMap.put(mergeNumId.toString(), numID.toString());
            });
            return numIdsMap;
         }
      }
   }

   private Map<String, String> mergeStyles(NiceXWPFDocument source, NiceXWPFDocument merged) {
      Map<String, String> styleIdsMap = new HashMap<>();
      XWPFStyles styles = source.getStyles();
      if (null == styles) {
         styles = source.createStyles();
      }

      XWPFStyles stylesMerge = merged.getStyles();
      if (null == stylesMerge) {
         return styleIdsMap;
      } else {
         try {
            Field listStyleField = XWPFStyles.class.getDeclaredField("listStyle");
            listStyleField.setAccessible(true);
            List<XWPFStyle> lists = (List<XWPFStyle>)listStyleField.get(stylesMerge);
            String defaultParaStyleId = null;

            for (XWPFStyle xwpfStyle : lists) {
               if (styles.styleExist(xwpfStyle.getStyleId())) {
                  String id = xwpfStyle.getStyleId();
                  xwpfStyle.setStyleId(UUID.randomUUID().toString().substring(0, 8));
                  styleIdsMap.put(id, xwpfStyle.getStyleId());
               }

               CTStyle ctStyle = xwpfStyle.getCTStyle();
               if (ctStyle.isSetDefault() && ctStyle.getDefault() == XWPFOnOff.X_1 && ctStyle.getType() == STStyleType.PARAGRAPH) {
                  defaultParaStyleId = ctStyle.getStyleId();
               }

               if (ctStyle.isSetDefault()) {
                  ctStyle.unsetDefault();
               }

               if (ctStyle.isSetName()) {
                  ctStyle.getName().setVal(ctStyle.getName().getVal() + xwpfStyle.getStyleId());
               }

               if (ctStyle.isSetBasedOn()) {
                  String newId = styleIdsMap.get(ctStyle.getBasedOn().getVal());
                  if (null != newId) {
                     ctStyle.getBasedOn().setVal(newId);
                  }
               }

               styles.addStyle(xwpfStyle);
            }

            if (null != defaultParaStyleId) {
               String dpid = defaultParaStyleId;
               merged.getParagraphs().stream().filter(p -> null == p.getStyle()).forEach(p -> p.setStyle(dpid));
            }
         } catch (Exception var13) {
            this.logger.error("merge style error", (Throwable)var13);
         }

         return styleIdsMap;
      }
   }

   private Map<String, String> mergeHyperlink(NiceXWPFDocument source, NiceXWPFDocument merged) throws InvalidFormatException {
      Map<String, String> map = new HashMap<>();

      for (PackageRelationship relationship : merged.getPackagePart()
         .getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink")) {
         PackageRelationship relationshipNew = source.getPackagePart()
            .addExternalRelationship(relationship.getTargetURI().toString(), XWPFRelation.HYPERLINK.getRelation());
         map.put(relationship.getId(), relationshipNew.getId());
      }

      return map;
   }

   private Map<String, String> mergeChart(NiceXWPFDocument source, NiceXWPFDocument merged) throws InvalidFormatException, IOException {
      Map<String, String> map = new HashMap<>();

      for (XWPFChart chart : merged.getCharts()) {
         String relationId = merged.getRelationId(chart);
         RelationPart addChartData = source.addChartData(chart);
         map.put(relationId, addChartData.getRelationship().getId());
      }

      return map;
   }

   public Boolean isBreakPage() {
      return this.breakPage;
   }

   public void setBreakPage(Boolean breakPage) {
      this.breakPage = breakPage;
   }

   public int getPageBreakLevel() {
      return this.pageBreakLevel;
   }

   public void setPageBreakLevel(int pageBreakLevel) {
      this.pageBreakLevel = pageBreakLevel;
   }
}
