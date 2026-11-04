/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.data.HyperlinkTextRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.policy.TextRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import com.deepoove.poi.util.StyleUtils;
import com.deepoove.poi.util.TableTools;
import com.deepoove.poi.xwpf.XWPFParagraphWrapper;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTextRenderData;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSym;

public class LimsTextRenderPolicy
extends TextRenderPolicy {
    private static final String SUP_MARK_START = "<sup>";
    private static final String SUP_MARK_END = "</sup>";
    private static final String SUB_MARK_START = "<sub>";
    private static final String SUB_MARK_END = "</sub>";
    private static final String SUB_MARK_REG = "</?su[b,p]>";
    private static final String CHECKED = "R";
    private static final String UNCHECKED = "\u00a3";
    private static final String CHECK_FONT = "Wingdings 2";
    private static final String CLEAR_PARAGRAPH = "clear_paragraph";

    @Override
    public void doRender(RenderContext<Object> context) throws Exception {
        Helper.renderTextRun(context.getRun(), context.getData());
    }

    @Override
    protected void postValidError(RenderContext<Object> context) {
        String tagName = context.getEleTemplate().getTagName();
        if (StringUtils.equalsIgnoreCase(tagName, CLEAR_PARAGRAPH)) {
            this.clearPlaceholder(context, true);
        } else {
            super.postValidError(context);
        }
    }

    public static class Helper {
        static final String REGEX_LINE_CHARACTOR = "\\n";

        public static void renderTextRun(XWPFRun run, Object data) {
            boolean lineAtTable;
            XWPFRun textRun = run;
            if (data instanceof HyperlinkTextRenderData) {
                textRun = Helper.createHyperLinkRun(run, data);
            }
            textRun.setText("", 0);
            boolean bl = lineAtTable = !(data instanceof HyperlinkTextRenderData) && TableTools.isInsideTable(run);
            if (data instanceof LimsTemplateDataList) {
                ((LimsTemplateDataList)data).forEach(renderData -> {
                    XWPFRun newRun = ((XWPFParagraph)run.getParent()).createRun();
                    StyleUtils.styleRun(newRun, run);
                    Helper.renderText(run, newRun, renderData, lineAtTable);
                });
            } else {
                Helper.renderText(run, textRun, data, lineAtTable);
            }
        }

        private static void renderText(XWPFRun templateRun, XWPFRun textRun, Object data, boolean lineAtTable) {
            TextRenderData wrapperData = Helper.wrapper(data);
            StyleUtils.styleRun(templateRun, wrapperData.getStyle());
            if (data instanceof LimsTextRenderData) {
                String separator = ((LimsTextRenderData)data).getSeparator();
                if (!StringUtils.isEmpty(separator)) {
                    int i = ((XWPFParagraph)templateRun.getParent()).getRuns().indexOf(textRun);
                    XWPFRun separateRun = ((XWPFParagraph)templateRun.getParent()).insertNewRun(i);
                    StyleUtils.styleRun(separateRun, templateRun);
                    separateRun.setText(separator, 0);
                }
                boolean addCheckbox = ((LimsTextRenderData)data).getCheckBox();
                boolean checkedFlag = ((LimsTextRenderData)data).getChecked();
                if (addCheckbox) {
                    int i = ((XWPFParagraph)templateRun.getParent()).getRuns().indexOf(textRun);
                    XWPFRun checkBoxRun = ((XWPFParagraph)templateRun.getParent()).insertNewRun(i);
                    StyleUtils.styleRun(checkBoxRun, templateRun);
                    CTSym ctSym = checkBoxRun.getCTR().addNewSym();
                    ctSym.setFont(LimsTextRenderPolicy.CHECK_FONT);
                    byte checkChar = checkedFlag ? (byte)LimsTextRenderPolicy.CHECKED.toCharArray()[0] : (byte)LimsTextRenderPolicy.UNCHECKED.toCharArray()[0];
                    ctSym.setChar(new byte[]{checkChar});
                }
            }
            String text = null == wrapperData.getText() ? "" : wrapperData.getText();
            Pattern pattern = Pattern.compile(LimsTextRenderPolicy.SUB_MARK_REG);
            Matcher matcher = pattern.matcher(text);
            int startPos = 0;
            while (matcher.find()) {
                String search = matcher.group();
                int searchPos = text.indexOf(search, startPos);
                String value = text.substring(startPos, searchPos);
                if (startPos > 0) {
                    textRun = ((XWPFParagraph)templateRun.getParent()).createRun();
                    StyleUtils.styleRun(textRun, templateRun);
                }
                if (startPos != searchPos) {
                    switch (search) {
                        case "</sub>": {
                            textRun.setSubscript(VerticalAlign.SUBSCRIPT);
                            break;
                        }
                        case "</sup>": {
                            textRun.setSubscript(VerticalAlign.SUPERSCRIPT);
                        }
                    }
                    Helper.renderText(textRun, value, lineAtTable);
                }
                startPos = searchPos + search.length();
            }
            if (startPos == 0) {
                Helper.renderText(textRun, text.substring(startPos), lineAtTable);
            } else if (startPos < text.length()) {
                textRun = ((XWPFParagraph)templateRun.getParent()).createRun();
                StyleUtils.styleRun(textRun, templateRun);
                Helper.renderText(textRun, text.substring(startPos), lineAtTable);
            }
        }

        private static void renderText(XWPFRun textRun, String text, boolean lineAtTable) {
            String[] split = text.split(REGEX_LINE_CHARACTOR, -1);
            if (split.length > 0) {
                textRun.setText(split[0], 0);
                lineAtTable = split.length > 1 && lineAtTable;
                for (int i = 1; i < split.length; ++i) {
                    if (lineAtTable) {
                        textRun.addBreak(BreakType.TEXT_WRAPPING);
                    } else {
                        textRun.addCarriageReturn();
                    }
                    textRun.setText(split[i]);
                }
            }
        }

        private static TextRenderData wrapper(Object obj) {
            return obj instanceof TextRenderData ? (TextRenderData)obj : new TextRenderData(obj.toString());
        }

        private static XWPFRun createHyperLinkRun(XWPFRun run, Object data) {
            XWPFParagraphWrapper paragraph = new XWPFParagraphWrapper((XWPFParagraph)run.getParent());
            XWPFRun hyperLinkRun = paragraph.insertNewHyperLinkRun(run, ((HyperlinkTextRenderData)data).getUrl());
            StyleUtils.styleRun(hyperLinkRun, run);
            run.setText("", 0);
            return hyperLinkRun;
        }
    }
}
