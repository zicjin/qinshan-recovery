/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.text.impl;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.text.FileToTextExtractor;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfFileToTextExtractor
implements FileToTextExtractor {
    public static final PdfFileToTextExtractor instance = new PdfFileToTextExtractor();

    @Override
    public boolean match(String fileExtension) {
        return "pdf".equalsIgnoreCase(fileExtension);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String extract(InputStream is) {
        try (PDDocument pdfDocument = PDDocument.load(is);){
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String string = stripper.getText(pdfDocument);
            return string;
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }
}
