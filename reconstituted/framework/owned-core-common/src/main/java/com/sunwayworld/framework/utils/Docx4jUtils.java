/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

public abstract class Docx4jUtils {
    public static void writeAsWord(OutputStream os, String content) {
        try {
            if (StringUtils.isEmpty(content)) {
                content = "";
            }
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head></head><body>" + Docx4jUtils.removeIgnoreTags(content) + "</body></html>";
            byte[] bytes = content.getBytes("UTF-8");
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            wordMLPackage.getMainDocumentPart().getContent().addAll(xhtmlImporter.convert(bais, null));
            wordMLPackage.save(os);
        }
        catch (IOException | Docx4JException ex) {
            throw new FileException(ex);
        }
    }

    public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        ArrayList<Object> result = new ArrayList<Object>();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement)obj).getValue();
        }
        if (obj.getClass().equals(toSearch)) {
            result.add(obj);
        } else if (obj instanceof ContentAccessor) {
            List<Object> children = ((ContentAccessor)obj).getContent();
            for (Object child : children) {
                result.addAll(Docx4jUtils.getAllElementFromObject(child, toSearch));
            }
        }
        return result;
    }

    public static void fillTextData(WordprocessingMLPackage wordMLPackage, Object data, Class<?> beanType) throws Docx4JException, JAXBException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Object> texts = Docx4jUtils.getAllElementFromObject(wordMLPackage.getMainDocumentPart(), Text.class);
        for (Object text : texts) {
            List<String> propertyNameList;
            Text textElement = (Text)text;
            if (!textElement.getValue().startsWith("$") || (propertyNameList = StringUtils.substringsBetween(textElement.getValue(), "${", "}")).size() == 0) continue;
            String propertyName = propertyNameList.get(0);
            String value = null;
            if (data instanceof BaseData) {
                Map<String, String> ext$ = ((BaseData)data).getExt$();
                value = ext$.get(propertyName.toLowerCase());
            } else {
                Object originalValue = ReflectionUtils.invokeReadMethod(data, propertyName);
                value = originalValue instanceof LocalDateTime ? ((LocalDateTime)originalValue).format(DateTimeFormatter.ofPattern("yyyy\u5e74MM\u6708dd\u65e5")) : (originalValue == null ? null : originalValue.toString());
            }
            textElement.setValue(value == null ? "" : value);
        }
    }

    public static void fillTableData(Tbl tempTable, List data, Class<?> beanType) throws IllegalArgumentException, InvocationTargetException, Exception {
        List<Object> rows = Docx4jUtils.getAllElementFromObject(tempTable, Tr.class);
        if (rows.size() > 2) {
            Tr templateRow = (Tr)rows.get(1);
            for (int index = 0; index < data.size(); ++index) {
                Tr workingRow = Docx4jUtils.addRowToTable(tempTable, templateRow, data.get(index), beanType, index + 1);
                tempTable.getContent().add(1 + index, workingRow);
            }
            tempTable.getContent().remove(templateRow);
        }
    }

    public static Tr addRowToTable(Tbl reviewtable, Tr templateRow, Object data, Class<?> beanType, int index) throws Exception, IllegalArgumentException, InvocationTargetException {
        Tr workingRow = XmlUtils.deepCopy(templateRow);
        List<Object> textElements = Docx4jUtils.getAllElementFromObject(workingRow, Text.class);
        for (Object text : textElements) {
            List<String> propertyNameList;
            Text textElement = (Text)text;
            if (!textElement.getValue().startsWith("T{") || (propertyNameList = StringUtils.substringsBetween(textElement.getValue(), "T{", "}")).size() == 0) continue;
            String propertyName = propertyNameList.get(0);
            if ("index".equals(propertyName)) {
                textElement.setValue(index + "");
                continue;
            }
            String value = null;
            if (data instanceof BaseData) {
                Map<String, String> ext$ = ((BaseData)data).getExt$();
                value = ext$.get(propertyName.toLowerCase());
            } else {
                value = (String)ReflectionUtils.invokeReadMethod(data, propertyName);
            }
            textElement.setValue(value == null ? "" : value);
        }
        return workingRow;
    }

    private static String removeIgnoreTags(String content) {
        content = content.replaceAll("<((?i)st1:chsdate)[^>]*>|</((?i)st1:chsdate)\\s*>", "");
        content = content.replaceAll("<((?i)m:omathpara)[^>]*>|</((?i)m:omathpara)\\s*>", "");
        content = content.replaceAll("<((?i)m:omath)[^>]*>|</((?i)m:omath)\\s*>", "");
        content = content.replaceAll("<((?i)m:r)[^>]*>|</((?i)m:r)\\s*>", "");
        content = content.replaceAll("<((?i)m:rpr)[^>]*>|</((?i)m:rpr)\\s*>", "");
        content = content.replaceAll("<((?i)m:scr)[^>]*>|</((?i)m:scr)\\s*>", "");
        content = content.replaceAll("<((?i)m:sty)[^>]*>|</((?i)m:sty)\\s*>", "");
        content = content.replaceAll("<((?i)m:d)[^>]*>|</((?i)m:d)\\s*>", "");
        content = content.replaceAll("<((?i)m:dpr)[^>]*>|</((?i)m:dpr)\\s*>", "");
        content = content.replaceAll("<((?i)m:begchr)[^>]*>|</((?i)m:begchr)\\s*>", "");
        content = content.replaceAll("<((?i)m:ctrlpr)[^>]*>|</((?i)m:ctrlpr)\\s*>", "");
        content = content.replaceAll("<((?i)img)\\s+(.+\n)*.*/>", "");
        return StringUtils.replace(content, " windowtext ", " ");
    }
}
