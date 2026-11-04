package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.policy.DocxRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import com.deepoove.poi.util.ParagraphUtils;
import com.deepoove.poi.xwpf.BodyContainer;
import com.deepoove.poi.xwpf.BodyContainerFactory;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsDocxRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.xwpf.LimsXmlXWPFDocumentMerge;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class LimsMultiAbleDocxRenderPolicy extends DocxRenderPolicy {
   @Override
   public void doRender(RenderContext<DocxRenderData> context) throws Exception {
      NiceXWPFDocument doc = context.getXWPFDocument();
      XWPFTemplate template = context.getTemplate();
      Boolean pageBreak = false;
      Integer pageBreakLevel = 7;
      if (context.getData() instanceof LimsDocxRenderData) {
         pageBreak = ((LimsDocxRenderData)context.getData()).getBreakPage();
         pageBreakLevel = ((LimsDocxRenderData)context.getData()).getPageBreakLevel();
      }

      doc = this.merge(
         new LimsMultiAbleDocxRenderPolicy.XWPFDocumentIterator(context.getData(), context.getConfig()), context.getRun(), doc, pageBreak, pageBreakLevel
      );
      template.reload(doc);
   }

   public NiceXWPFDocument merge(List<NiceXWPFDocument> docMerges, XWPFRun run, NiceXWPFDocument source) throws Exception {
      return null != docMerges && !docMerges.isEmpty() && null != run ? this.merge(docMerges.iterator(), run, source, false, 7) : source;
   }

   public NiceXWPFDocument merge(Iterator<NiceXWPFDocument> iterator, XWPFRun run, NiceXWPFDocument source, Boolean pageBreak, Integer pageBreakLevel) throws Exception {
      XWPFRun newRun = run;
      String paragraphText = ParagraphUtils.trimLine((XWPFParagraph)run.getParent());
      boolean havePictures = ParagraphUtils.havePictures((XWPFParagraph)run.getParent());
      if (!ParagraphUtils.trimLine(run.text()).equals(paragraphText) || havePictures) {
         BodyContainer container = BodyContainerFactory.getBodyContainer(run);
         XWPFParagraph paragraph = container.insertNewParagraph(run);
         newRun = paragraph.createRun();
      }

      LimsXmlXWPFDocumentMerge limsXmlXWPFDocumentMerge = new LimsXmlXWPFDocumentMerge();
      limsXmlXWPFDocumentMerge.setBreakPage(pageBreak);
      limsXmlXWPFDocumentMerge.setPageBreakLevel(pageBreakLevel);
      return limsXmlXWPFDocumentMerge.merge(source, iterator, newRun);
   }

   class XWPFDocumentIterator implements Iterator<NiceXWPFDocument> {
      private Configure config;
      private byte[] bytes;
      private List<?> datas;
      private int length;
      int cursor = 0;

      XWPFDocumentIterator(DocxRenderData data, Configure config) {
         this.bytes = data.getMergedDoc();
         this.datas = data.getDataModels();
         this.config = config;
         this.length = null == this.datas ? 1 : this.datas.size();
      }

      @Override
      public boolean hasNext() {
         return this.cursor < this.length;
      }

      public NiceXWPFDocument next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException("No instance of NiceXWPFDocument");
         } else if (CollectionUtils.isEmpty(this.datas) && 0 == this.cursor) {
            try {
               this.cursor++;
               return new NiceXWPFDocument(new ByteArrayInputStream(this.bytes));
            } catch (IOException var5) {
               throw new RenderException("Create XWPFDocument error", var5);
            }
         } else {
            Object data = this.datas.get(this.cursor++);
            if (this.bytes.length == 0 && data instanceof DocxRenderData) {
               DocxRenderData docxData = (DocxRenderData)data;
               XWPFTemplate temp = XWPFTemplate.compile(new ByteArrayInputStream(docxData.getMergedDoc()), this.config);
               Object subData = new LimsTemplateDataMap();
               if (docxData.getDataModels() != null) {
                  subData = docxData.getDataModels().get(0);
               }

               temp.render(subData);
               return temp.getXWPFDocument();
            } else {
               XWPFTemplate temp = XWPFTemplate.compile(new ByteArrayInputStream(this.bytes), this.config);
               temp.render(data);
               return temp.getXWPFDocument();
            }
         }
      }
   }
}
