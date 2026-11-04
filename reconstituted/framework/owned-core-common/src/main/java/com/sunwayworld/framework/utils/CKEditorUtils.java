/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class CKEditorUtils {
    public static void writeAsWord(File target, String name, String content) {
        try {
            if (StringUtils.isEmpty(content)) {
                content = "";
            }
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head></head><body>" + CKEditorUtils.removeIgnoreTags(content) + "</body></html>";
            byte[] bytes = content.getBytes("UTF-8");
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            wordMLPackage.getMainDocumentPart().getContent().addAll(xhtmlImporter.convert(bais, null));
            wordMLPackage.save(target);
        }
        catch (IOException | Docx4JException ex) {
            throw new FileException(ex);
        }
    }

    private static String removeIgnoreTags(String content) {
        return content.replaceAll("<((?i)st1:place)[^>]*>|</((?i)st1:place)\\s*>", "").replaceAll("<((?i)st1:chsdate)[^>]*>|</((?i)st1:chsdate)\\s*>", "").replaceAll("<((?i)m:omathpara)[^>]*>|</((?i)m:omathpara)\\s*>", "").replaceAll("<((?i)m:omath)[^>]*>|</((?i)m:omath)\\s*>", "").replaceAll("<((?i)m:r)[^>]*>|</((?i)m:r)\\s*>", "").replaceAll("<((?i)m:rpr)[^>]*>|</((?i)m:rpr)\\s*>", "").replaceAll("<((?i)m:scr)[^>]*>|</((?i)m:scr)\\s*>", "").replaceAll("<((?i)m:sty)[^>]*>|</((?i)m:sty)\\s*>", "").replaceAll("<((?i)m:d)[^>]*>|</((?i)m:d)\\s*>", "").replaceAll("<((?i)m:dpr)[^>]*>|</((?i)m:dpr)\\s*>", "").replaceAll("<((?i)m:begchr)[^>]*>|</((?i)m:begchr)\\s*>", "").replaceAll("<((?i)m:ctrlpr)[^>]*>|</((?i)m:ctrlpr)\\s*>", "").replaceAll("<((?i)img)\\s+(.+\n)*.*/>", "").replaceAll("(windowtext)", "");
    }
}
