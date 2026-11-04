/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.NumberingFormat;
import com.deepoove.poi.data.NumberingItemRenderData;
import com.deepoove.poi.data.NumberingRenderData;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.policy.NumberingRenderPolicy;
import com.deepoove.poi.policy.ParagraphRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.util.StyleUtils;
import com.deepoove.poi.xwpf.BodyContainer;
import com.deepoove.poi.xwpf.BodyContainerFactory;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.deepoove.poi.xwpf.NumFormat;
import java.math.BigInteger;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

public class LimsNumberingRenderPolicy
extends NumberingRenderPolicy {
    @Override
    public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
        NumberingRenderData model;
        try {
            model = (NumberingRenderData)data;
        }
        catch (ClassCastException e) {
            throw new RenderException("Error Render Data format for template: " + eleTemplate.getSource(), e);
        }
        RenderContext<NumberingRenderData> context = new RenderContext<NumberingRenderData>(eleTemplate, model, template);
        try {
            if (!this.validate(model)) {
                this.postValidError(context);
                this.clearPlaceholder(context, true);
                return;
            }
            this.beforeRender(context);
            this.doRender(context);
            this.afterRender(context);
        }
        catch (Exception e) {
            this.reThrowException(context, e);
        }
    }

    @Override
    public void doRender(RenderContext<NumberingRenderData> context) throws Exception {
        Helper.renderNumbering(context.getRun(), context.getData());
    }

    public static class Helper {
        public static void renderNumbering(XWPFRun run, NumberingRenderData data) throws Exception {
            CTDecimalNumber ctNumId;
            CTNumPr numPr;
            List<NumberingItemRenderData> items = data.getItems();
            NiceXWPFDocument doc = (NiceXWPFDocument)run.getParent().getDocument();
            XWPFParagraph currentParagraph = (XWPFParagraph)run.getParent();
            CTPPr pPr = currentParagraph.getCTP().getPPr();
            BigInteger numID = null;
            if (pPr != null && (numPr = pPr.getNumPr()) != null && (ctNumId = numPr.getNumId()) != null) {
                BigInteger numIdVal = ctNumId.getVal();
                XWPFNumbering numbering = doc.getNumbering();
                XWPFNum num = numbering.getNum(numIdVal);
                CTDecimalNumber abstractNumId = num.getCTNum().getAbstractNumId();
                BigInteger abstractNumIdVal = abstractNumId.getVal();
                XWPFAbstractNum abstractNum = numbering.getAbstractNum(abstractNumIdVal);
                CTAbstractNum ctAbstractNum = abstractNum.getCTAbstractNum();
                CTLvl cTLvl = ctAbstractNum.getLvlArray(0);
                STNumberFormat.Enum fmt = cTLvl.getNumFmt().getVal();
                String lvlText = cTLvl.getLvlText().getVal();
                NumberingFormat numberingFormat = NumberingFormat.builder(lvlText).withNumFmt(NumFormat.valueOf(fmt.intValue())).build(0);
                numID = doc.addNewNumberingId(numberingFormat);
            }
            BodyContainer bodyContainer = BodyContainerFactory.getBodyContainer(run);
            for (NumberingItemRenderData item : items) {
                XWPFParagraph paragraph = bodyContainer.insertNewParagraph(run);
                int level = item.getLevel();
                if (-1 != level) {
                    paragraph.setNumID(numID);
                    paragraph.setNumILvl(BigInteger.valueOf(level));
                }
                XWPFRun createRun = paragraph.createRun();
                StyleUtils.styleRun(createRun, run);
                ParagraphRenderPolicy.Helper.renderParagraph(createRun, item.getItem());
            }
        }
    }
}
