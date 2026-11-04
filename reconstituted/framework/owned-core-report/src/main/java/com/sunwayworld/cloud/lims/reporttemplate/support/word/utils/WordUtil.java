/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.util.CollectionUtils;

public class WordUtil {
    public static final String rgex = "<[\\s]*?w:sectPr[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?w:sectPr[\\s]*?>";

    public static void copyTableRow(XWPFTableRow target, XWPFTableRow source) {
        int i;
        int tempRowCellsize = source.getTableCells().size();
        for (i = 0; i < tempRowCellsize; ++i) {
            target.addNewTableCell();
        }
        target.getCtRow().setTrPr(source.getCtRow().getTrPr());
        for (i = 0; i < target.getTableCells().size(); ++i) {
            WordUtil.copyTableCell(target.getCell(i), source.getCell(i));
        }
    }

    public static void copyTableCell(XWPFTableCell newTableCell, XWPFTableCell templateTableCell) {
        newTableCell.getCTTc().setTcPr(templateTableCell.getCTTc().getTcPr());
        for (int pos = 0; pos < newTableCell.getParagraphs().size(); ++pos) {
            newTableCell.removeParagraph(pos);
        }
        for (XWPFParagraph sp : templateTableCell.getParagraphs()) {
            XWPFParagraph targetP = newTableCell.addParagraph();
            WordUtil.copyParagraph(targetP, sp);
        }
    }

    public static void copyParagraph(XWPFParagraph newParagraph, XWPFParagraph templateParagraph) {
        newParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
        for (int pos = 0; pos < newParagraph.getRuns().size(); ++pos) {
            newParagraph.removeRun(pos);
        }
        for (XWPFRun s : templateParagraph.getRuns()) {
            XWPFRun targetrun = newParagraph.createRun();
            WordUtil.copyRun(targetrun, s);
        }
    }

    public static void copyRun(XWPFRun newRun, XWPFRun templateRun) {
        newRun.getCTR().setRPr(templateRun.getCTR().getRPr());
        newRun.setText(templateRun.text());
    }

    public static boolean setCellText(XWPFTableCell cell, String value) {
        List<XWPFRun> runs;
        boolean modify = false;
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        if (!CollectionUtils.isEmpty(paragraphs) && !CollectionUtils.isEmpty(runs = paragraphs.get(0).getRuns())) {
            modify = true;
            if (value != null && (value.contains("<sub>") && value.contains("</sub>") || value.contains("<sup>") && value.contains("</sup>"))) {
                WordUtil.setSubSUpValue(cell, value, paragraphs.get(0), runs);
            } else if (value != null && value.contains("\u2103")) {
                WordUtil.setNewRomanContent(cell, value, paragraphs.get(0), runs);
            } else {
                WordUtil.breakSetValue(value, runs.get(0));
            }
        }
        return modify;
    }

    public static void setCellText(XWPFParagraph paragraph, XWPFRun currentRun, String value) {
        if (value != null && (value.contains("<sub>") && value.contains("</sub>") || value.contains("<sup>") && value.contains("</sup>")) || value.contains("\u2103")) {
            currentRun.setText("", 0);
            List<XWPFRun> runs = paragraph.getRuns();
            ArrayList<XWPFRun> nruns = new ArrayList<XWPFRun>();
            for (int i = runs.size() - 1; i >= 0; --i) {
                runs.get(i).setText("", 0);
                nruns.add(runs.get(i));
                if (currentRun.equals(runs.get(i))) break;
            }
            if (value.contains("\u2103")) {
                WordUtil.setNewRomanContent(null, value, paragraph, nruns);
            } else {
                WordUtil.setSubSUpValue(null, value, paragraph, nruns);
            }
        } else {
            WordUtil.breakSetValue(value, currentRun);
        }
    }

    private static void setNewRomanContent(XWPFTableCell cell, String value, XWPFParagraph paragraph, List<XWPFRun> runs) {
        int index = value.indexOf("\u2103");
        CTRPr rPr = runs.get(0).getCTR().getRPr();
        if (index > 0) {
            String preStr = value.substring(0, index);
            XWPFRun preRun = paragraph.createRun();
            preRun.getCTR().setRPr(rPr);
            WordUtil.breakSetValue(preStr, preRun);
        }
        XWPFRun rejexRun = paragraph.createRun();
        rejexRun.getCTR().setRPr(rPr);
        String font = "Times New Roman";
        rejexRun.getCTR().getRPr().getRFonts().setEastAsia(font);
        WordUtil.breakSetValue("\u2103", rejexRun);
        if (index < value.length() - 1) {
            String aftStr = value.substring(index + 1, value.length());
            XWPFRun aftRun = paragraph.createRun();
            aftRun.getCTR().setRPr(rPr);
            WordUtil.breakSetValue(aftStr, aftRun);
        }
    }

    public static void setSubSUpValue(XWPFTableCell cell, String value, XWPFParagraph paragraph, List<XWPFRun> runs) {
        if (value != null && (value.contains("<sub>") && value.contains("</sub>") || value.contains("<sup>") && value.contains("</sup>"))) {
            String showObj = new String(value);
            int times = 0;
            boolean existRunsQty = false;
            while (showObj.indexOf("<sup>") != -1 || showObj.indexOf("<sub>") != -1) {
                XWPFRun cr;
                XWPFRun createRun;
                XWPFRun cr2;
                String se;
                int end;
                String sb;
                int supindex = showObj.indexOf("<sup>");
                int subindex = showObj.indexOf("<sub>");
                if (subindex == -1 || supindex != -1 && supindex < subindex) {
                    sb = showObj.substring(0, supindex);
                    end = showObj.indexOf("</sup>");
                    String sup = showObj.substring(supindex + 5, end);
                    showObj = se = showObj.substring(end + 6, showObj.length());
                    if (times < 0) {
                        cr2 = runs.get(times);
                        WordUtil.breakSetValue(sb, cr2);
                    } else {
                        cr2 = paragraph.createRun();
                        cr2.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                        WordUtil.breakSetValue(sb, cr2);
                    }
                    if (++times < 0) {
                        runs.get(times).setText(sup);
                        runs.get(times).setSubscript(VerticalAlign.SUPERSCRIPT);
                    } else {
                        createRun = paragraph.createRun();
                        createRun.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                        createRun.setText(sup);
                        createRun.setSubscript(VerticalAlign.SUPERSCRIPT);
                    }
                    ++times;
                } else {
                    sb = showObj.substring(0, subindex);
                    end = showObj.indexOf("</sub>");
                    String sub = showObj.substring(subindex + 5, end);
                    showObj = se = showObj.substring(end + 6, showObj.length());
                    if (times < 0) {
                        cr2 = runs.get(times);
                        WordUtil.breakSetValue(sb, cr2);
                    } else {
                        cr2 = paragraph.createRun();
                        cr2.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                        WordUtil.breakSetValue(sb, cr2);
                    }
                    if (++times < 0) {
                        runs.get(times).setText(sub);
                        runs.get(times).setSubscript(VerticalAlign.SUBSCRIPT);
                    } else {
                        createRun = paragraph.createRun();
                        createRun.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                        createRun.setText(sub);
                        createRun.setSubscript(VerticalAlign.SUBSCRIPT);
                    }
                    ++times;
                }
                if (showObj.indexOf("<sub>") == 0 || showObj.indexOf("<sup>") != 0) {
                    // empty if block
                }
                if (showObj.indexOf("<sup>") != -1 || showObj.indexOf("<sub>") != -1) continue;
                sb = showObj;
                if (times < 0) {
                    cr = runs.get(times);
                    WordUtil.breakSetValue(sb, cr);
                } else {
                    cr = paragraph.createRun();
                    cr.getCTR().setRPr(runs.get(0).getCTR().getRPr());
                    WordUtil.breakSetValue(sb, cr);
                }
                ++times;
            }
        }
    }

    private static void breakSetValue(String sb, XWPFRun cr) {
        if (sb.contains("\n") || sb.contains("\r") || sb.contains("\r\n")) {
            String[] split = sb.split("\n");
            if (split.length == 1) {
                split = sb.split("\r");
            }
            if (split.length == 1) {
                split = sb.split("\r\n");
            }
            for (int i = 0; i < split.length; ++i) {
                if (i == 0) {
                    cr.setText(split[i], 0);
                } else {
                    cr.setText(split[i]);
                }
                if (i == split.length - 1) continue;
                cr.addBreak();
            }
        } else {
            cr.setText(sb, 0);
        }
    }

    private static String emptyStringByLength(int length) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            temp.append(" ");
        }
        return temp.toString();
    }

    public static CTAnchor getAnchorWithGraphic(CTGraphicalObject ctGraphicalObject, String deskFileName, int width, int height, int leftOffset, int topOffset, boolean behind) {
        String anchorXML = "<wp:anchor xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" simplePos=\"0\" relativeHeight=\"0\" behindDoc=\"" + (behind ? 1 : 0) + "\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\"><wp:simplePos x=\"0\" y=\"0\"/><wp:positionH relativeFrom=\"character\"><wp:posOffset>" + leftOffset + "</wp:posOffset></wp:positionH><wp:positionV relativeFrom=\"paragraph\"><wp:posOffset>" + topOffset + "</wp:posOffset></wp:positionV><wp:extent cx=\"" + width + "\" cy=\"" + height + "\"/><wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/><wp:wrapNone/><wp:docPr id=\"1\" name=\"Drawing 0\" descr=\"" + deskFileName + "\"/><wp:cNvGraphicFramePr/></wp:anchor>";
        CTDrawing drawing = null;
        try {
            drawing = CTDrawing.Factory.parse(anchorXML);
        }
        catch (XmlException e) {
            e.printStackTrace();
        }
        CTAnchor anchor = drawing.getAnchorArray(0);
        anchor.setGraphic(ctGraphicalObject);
        return anchor;
    }

    public static void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
        BigInteger w = new BigInteger("0");
        Boolean check = true;
        STTblWidth.Enum type = null;
        try {
            type = table.getRow(row).getCell(fromCell).getCTTc().getTcPr().getTcW().getType();
        }
        catch (Exception e) {
            check = false;
        }
        for (int cellIndex = fromCell; cellIndex <= toCell; ++cellIndex) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            try {
                BigInteger cellW = cell.getCTTc().getTcPr().getTcW().getW();
                System.err.println("cellW" + cellW);
                w = w.add(cellW);
            }
            catch (Exception e) {
                e.printStackTrace();
                check = false;
            }
            if (cellIndex == fromCell) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                continue;
            }
            cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }
        if (check.booleanValue() && type != null) {
            CTTcPr tcpr = table.getRow(row).getCell(fromCell).getCTTc().addNewTcPr();
            CTTblWidth cellw = tcpr.addNewTcW();
            cellw.setW(w);
            cellw.setType(type);
            System.err.println(w);
        }
    }

    public static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; ++rowIndex) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
                continue;
            }
            cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
        }
    }

    public static void cleanData(XWPFTableCell cell) {
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        Object x = null;
        for (int ii = 0; ii < paragraphs.size(); ++ii) {
            List<XWPFRun> runs = paragraphs.get(ii).getRuns();
            for (int k = runs.size() - 1; k >= 0; --k) {
                paragraphs.get(ii).removeRun(k);
            }
        }
    }

    public static void mergeWord(XWPFDocument src, XWPFDocument append) throws Exception {
        CTBody src1Body = src.getDocument().getBody();
        CTBody src2Body = append.getDocument().getBody();
        List<XWPFPictureData> allPictures = append.getAllPictures();
        HashMap<String, String> map = new HashMap<String, String>();
        for (XWPFPictureData picture : allPictures) {
            String before = append.getRelationId(picture);
            String after = src.addPictureData(picture.getData(), 6);
            map.put(before, after);
        }
        WordUtil.appendBody(src1Body, src2Body, map);
    }

    private static void appendBody(CTBody src, CTBody append, Map<String, String> map) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        appendString = appendString.replaceAll(rgex, "");
        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> set : map.entrySet()) {
                addPart = addPart.replace(set.getKey(), set.getValue());
            }
        }
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
        src.set(makeBody);
    }
}
