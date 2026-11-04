/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.XHTMLValidationType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public class SpireUtils {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void htmlContentToWord(String htmlContent, Path wordFilePath) {
        File temp = null;
        try {
            String searchPattern = "src=\"files";
            StringBuilder replacement = new StringBuilder();
            String serverAddress = ApplicationContextHelper.getServerAddress();
            String contextPath = ServletUtils.getContextPath();
            replacement.append("src=\"").append(serverAddress).append(contextPath).append("/files");
            htmlContent = StringUtils.replace(htmlContent, searchPattern, replacement.toString());
            temp = FileUtils.createTempFile("html");
            FileUtils.write(temp, htmlContent);
            SpireUtils.htmlFileToWord(temp.toPath(), wordFilePath);
        }
        catch (Throwable throwable) {
            FileUtils.deleteQuietly(temp);
            throw throwable;
        }
        FileUtils.deleteQuietly(temp);
    }

    public static void htmlFileToWord(Path htmlFilePath, Path wordFilePath) {
        if (htmlFilePath == null || !Files.exists(htmlFilePath, new LinkOption[0])) {
            return;
        }
        FileUtils.makeDirs(wordFilePath);
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(wordFilePath.toFile()));){
            Document document = new Document();
            document.loadFromStream(Files.newInputStream(htmlFilePath, new OpenOption[0]), FileFormat.Html, XHTMLValidationType.None);
            document.saveToStream((OutputStream)os, FileFormat.Docx);
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }
}
