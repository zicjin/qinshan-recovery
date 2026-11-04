/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.render.compute.DefaultELRenderDataCompute;
import com.deepoove.poi.render.compute.EnvModel;
import com.deepoove.poi.render.compute.RenderDataCompute;
import com.deepoove.poi.render.processor.DocumentProcessor;
import com.deepoove.poi.render.processor.EnvIterator;
import com.deepoove.poi.resolver.TemplateResolver;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.MetaTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.deepoove.poi.util.ReflectionUtils;
import com.deepoove.poi.util.TableTools;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class LimsNewHackLoopTableRenderPolicy
extends LoopRowTableRenderPolicy {
    private String prefix;
    private String suffix;
    private static final String TITLE_SPLIT_TAG = ",";
    private static final Logger logger = LogManager.getLogger(LimsNewHackLoopTableRenderPolicy.class);

    public LimsNewHackLoopTableRenderPolicy() {
        this("[", "]");
    }

    public LimsNewHackLoopTableRenderPolicy(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void render(ElementTemplate eleTemplate, Object initrenderData, XWPFTemplate template) {
        List<LimsMergeTemplateRenderData.MergedColumn> mergedColumnList;
        LimsMergeTemplateRenderData renderData = (LimsMergeTemplateRenderData)initrenderData;
        RunTemplate runTemplate = (RunTemplate)eleTemplate;
        XWPFRun run = runTemplate.getRun();
        if (!TableTools.isInsideTable(run)) {
            throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
        }
        if (renderData == null || CollectionUtils.isEmpty(renderData.getData())) {
            this.renderNullData(run, eleTemplate, template);
            return;
        }
        XWPFTableCell tagCell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
        XWPFTableRow tagRow = tagCell.getTableRow();
        XWPFTable table = tagCell.getTableRow().getTable();
        this.renderOriginal(renderData.getData(), table, template, eleTemplate, run, tagCell, tagRow);
        int initRowIndex = WordExportUtil.getTagRowIndex(table, tagRow, WordExportUtil.getCellIndex(tagRow, tagCell)) + 1;
        List<LimsMergeTemplateRenderData.MergedRow> mergedRowList = renderData.getMergedRowList();
        if (!CollectionUtils.isEmpty(mergedRowList)) {
            mergedRowList.forEach(mergedRow -> LimsNewHackLoopTableRenderPolicy.mergeTableRows(table, mergedRow.getColumnIndex(), mergedRow.getStartRowIndex() + initRowIndex, mergedRow.getEndRowIndex() + initRowIndex));
        }
        if (!CollectionUtils.isEmpty(mergedColumnList = renderData.getMergedColumnList())) {
            mergedColumnList.forEach(mergedColumn -> LimsNewHackLoopTableRenderPolicy.mergeTableColumns(table, mergedColumn.getRowIndex() + initRowIndex, mergedColumn.getStartColumnIndex(), mergedColumn.getEndColumnIndex()));
        }
    }

    private void renderNullData(XWPFRun run, ElementTemplate runTemplate, XWPFTemplate template) {
        String tagName = runTemplate.getTagName();
        XWPFTableCell tagCell = WordExportUtil.getCurrentCell(run);
        run.setText("", 0);
        XWPFTableRow titleTemplateRow = tagCell.getTableRow();
        XWPFTable table = titleTemplateRow.getTable();
        int startRowIndex = WordExportUtil.getTagRowIndex(table, titleTemplateRow, WordExportUtil.getCellIndex(titleTemplateRow, tagCell));
        XWPFTableRow dataRow = table.getRow(startRowIndex + 1);
        try {
            TemplateResolver resolver = new TemplateResolver(template.getConfig().copy(this.prefix, this.suffix));
            DefaultELRenderDataCompute dataELCompute = new DefaultELRenderDataCompute(EnvModel.of(new LimsTemplateDataMap(), EnvIterator.makeEnv(0, false)), false);
            RenderDataCompute dataSPELCompute = template.getConfig().getRenderDataComputeFactory().newCompute(EnvModel.ofModel(new LimsTemplateDataMap()));
            dataRow.getTableCells().forEach(cell -> {
                List<MetaTemplate> templates = resolver.resolveBodyElements(cell.getBodyElements());
                try {
                    new DocumentProcessor(template, resolver, dataSPELCompute).process(templates);
                }
                catch (IllegalStateException e) {
                    new DocumentProcessor(template, resolver, dataELCompute).process(templates);
                }
            });
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (StringUtils.containsIgnoreCase(tagName, "mergeVNoHead")) {
            table.removeRow(startRowIndex);
        }
    }

    private void renderOriginal(List<LimsTemplateDataMap> dataMapList, XWPFTable table, XWPFTemplate template, ElementTemplate eleTemplate, XWPFRun run, XWPFTableCell tagCell, XWPFTableRow tagRow) {
        try {
            boolean hasNext;
            int templateRowIndex = WordExportUtil.getTagRowIndex(table, tagRow, WordExportUtil.getCellIndex(tagRow, tagCell)) + 1;
            Iterator<LimsTemplateDataMap> dataMapIterator = dataMapList.iterator();
            while (hasNext = dataMapIterator.hasNext()) {
                LimsTemplateDataMap dataMap = dataMapIterator.next();
                XWPFTableRow templateRow = table.getRow(templateRowIndex);
                int insertPosition = templateRowIndex++;
                XWPFTableRow nextRow = table.insertNewTableRow(insertPosition);
                this.setTableRow(table, templateRow, insertPosition);
                XmlCursor newCursor = templateRow.getCtRow().newCursor();
                newCursor.toPrevSibling();
                XmlObject object = newCursor.getObject();
                nextRow = new XWPFTableRow((CTRow)object, table);
                this.setTableRow(table, nextRow, insertPosition);
                TemplateResolver resolver = new TemplateResolver(template.getConfig().copy(this.prefix, this.suffix));
                int index = 1;
                RenderDataCompute dataCompute = template.getConfig().getRenderDataComputeFactory().newCompute(EnvModel.of(dataMap, EnvIterator.makeEnv(index++, hasNext)));
                List<XWPFTableCell> cells = nextRow.getTableCells();
                cells.forEach(cell -> {
                    List<MetaTemplate> templates = resolver.resolveBodyElements(cell.getBodyElements());
                    new DocumentProcessor(template, resolver, dataCompute).process(templates);
                });
            }
            run.setText("", 0);
            table.removeRow(templateRowIndex);
            this.afterloop(table, dataMapList);
        }
        catch (Exception e) {
            throw new RenderException("HackLoopTable for " + eleTemplate + "error: " + e.getMessage(), e);
        }
    }

    private void setTableRow(XWPFTable table, XWPFTableRow templateRow, int pos) {
        List rows = (List)ReflectionUtils.getValue("tableRows", table);
        rows.set(pos, templateRow);
        table.getCTTbl().setTrArray(pos, templateRow.getCtRow());
    }

    public static void mergeTableRows(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; ++rowIndex) {
            XWPFTableCell cell;
            XWPFTableRow row = table.getRow(rowIndex);
            if (row == null || (cell = row.getCell(col)) == null) continue;
            if (rowIndex == fromRow) {
                cell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.RESTART);
                continue;
            }
            cell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
        }
    }

    public static void mergeTableColumns(XWPFTable table, int rowIndex, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; ++cellIndex) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(cellIndex);
            if (cellIndex == fromCell) {
                cell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.RESTART);
                continue;
            }
            cell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }

    public static void mergeTableColumnsUseGridSpan(XWPFTable table, int rowIndex, int fromCell, int toCell) {
        XWPFTableRow row = table.getRow(rowIndex);
        int totalSpan = 0;
        int totalW = 0;
        for (int i = fromCell; i <= toCell; ++i) {
            XWPFTableCell c = row.getCell(i);
            int span = 1;
            if (c != null && c.getCTTc().getTcPr() != null && c.getCTTc().getTcPr().getGridSpan() != null && c.getCTTc().getTcPr().getGridSpan().getVal() != null) {
                span = c.getCTTc().getTcPr().getGridSpan().getVal().intValue();
            }
            totalSpan += span;
            CTTblWidth tcW = WordExportUtil.getTcPr(c).getTcW();
            if (tcW == null) {
                tcW = WordExportUtil.getTcPr(c).addNewTcW();
            }
            totalW += tcW.getW().intValue();
        }
        XWPFTableCell first = row.getCell(fromCell);
        if (first != null) {
            CTTcPr pr = first.getCTTc().getTcPr() != null ? first.getCTTc().getTcPr() : first.getCTTc().addNewTcPr();
            CTDecimalNumber gs = pr.getGridSpan() != null ? pr.getGridSpan() : pr.addNewGridSpan();
            gs.setVal(BigInteger.valueOf(totalSpan));
            CTTblWidth tcw = pr.getTcW() != null ? pr.getTcW() : pr.addNewTcW();
            tcw.setType(STTblWidth.DXA);
            tcw.setW(BigInteger.valueOf(totalW));
            if (pr.isSetHMerge()) {
                pr.unsetHMerge();
            }
        }
        for (int i = toCell; i > fromCell; --i) {
            row.removeCell(i);
            row.getCtRow().removeTc(i);
        }
    }
}
