/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.style.PictureStyle;
import com.deepoove.poi.policy.AbstractRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import com.deepoove.poi.render.WhereDelegate;
import com.deepoove.poi.util.TableTools;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

public class LimsSuspensionPicRenderPolicy
extends AbstractRenderPolicy<PictureRenderData> {
    @Override
    protected void afterRender(RenderContext<PictureRenderData> context) {
        this.clearPlaceholder(context, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doRender(RenderContext<PictureRenderData> context) throws Exception {
        XWPFRun run = context.getRun();
        WhereDelegate where = context.getWhereDelegate();
        PictureRenderData picture = context.getThing();
        int fontOffset = run.getFontSize();
        if (picture == null) {
            return;
        }
        byte[] picData = picture.getPictureSupplier().get();
        PictureStyle pictureStyle = picture.getPictureStyle();
        try (ByteArrayInputStream pictureInputStream = new ByteArrayInputStream(picData);){
            where.addPicture(pictureInputStream, 5, pictureStyle.getWidth(), pictureStyle.getHeight());
            CTDrawing drawing = run.getCTR().getDrawingArray(0);
            CTGraphicalObject graphicalObject = drawing.getInlineArray(0).getGraphic();
            int leftOffset = this.calculateLeftOffset(run);
            int topOffset = TableTools.isInsideTable(run) ? 0 : Units.pixelToEMU(-pictureStyle.getHeight() / 2 - fontOffset);
            CTAnchor anchor = WordExportUtil.getAnchorWithGraphic(graphicalObject, "Generated", Units.pixelToEMU(pictureStyle.getWidth()), Units.pixelToEMU(pictureStyle.getHeight()), Units.pixelToEMU(leftOffset), topOffset, false);
            drawing.setAnchorArray(new CTAnchor[]{anchor});
            drawing.removeInline(0);
        }
        finally {
            this.clearPlaceholder(context, false);
        }
    }

    private int calculateLeftOffset(XWPFRun currentRun) {
        boolean insideTable = TableTools.isInsideTable(currentRun);
        int offset = 0;
        if (insideTable) {
            offset = this.getCurrentPositionInCell(currentRun);
        } else {
            CTPageSz pgSz;
            XWPFParagraph paragraph = (XWPFParagraph)currentRun.getParent();
            XWPFDocument document = (XWPFDocument)paragraph.getPart();
            CTDocument1 document1 = document.getDocument();
            CTBody body = document1.getBody();
            CTSectPr sectPr = body.getSectPr();
            if (sectPr != null && (pgSz = sectPr.getPgSz()) != null) {
                BigInteger w = pgSz.getW();
                int pageWidth = w.intValue();
                offset = this.getCurrentPositionInParagraph(paragraph, currentRun, pageWidth);
            }
        }
        return offset;
    }

    private int getCurrentPositionInCell(XWPFRun run) {
        XWPFTableCell cell = WordExportUtil.getCurrentCell(run);
        int cellWidth = cell.getWidth();
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        int pos = 0;
        paragraphs.forEach(paragraph -> this.getCurrentPositionInParagraph((XWPFParagraph)paragraph, run, cellWidth));
        return pos;
    }

    private int getCurrentPositionInParagraph(XWPFParagraph paragraph, XWPFRun run, int parentWidth) {
        ParagraphAlignment alignment = paragraph.getAlignment();
        List<XWPFRun> runList = paragraph.getRuns();
        int textWidth = 0;
        if (!CollectionUtils.isEmpty(runList)) {
            for (XWPFRun xwpfRun : runList) {
                int fontSize;
                if (xwpfRun == run) break;
                int n = fontSize = xwpfRun.getFontSize() == -1 ? 10 : xwpfRun.getFontSize() * 2 / 3;
                String text = xwpfRun.toString();
                if (StringUtils.isEmpty(text)) continue;
                textWidth += fontSize * text.length();
            }
        }
        int pos = 0;
        switch (alignment) {
            case BOTH:
            case LEFT: {
                pos = textWidth % parentWidth;
                break;
            }
            case CENTER: {
                pos = textWidth % parentWidth / 2 + parentWidth / 2;
                break;
            }
            case RIGHT: {
                pos = parentWidth - textWidth % parentWidth;
                break;
            }
            default: {
                pos = parentWidth / 2;
            }
        }
        return pos;
    }
}
