/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.text.impl;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.text.FileToTextExtractor;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class DocFileToTextExtractor
implements FileToTextExtractor {
    public static final DocFileToTextExtractor instance = new DocFileToTextExtractor();

    @Override
    public boolean match(String fileExtension) {
        return "doc".equalsIgnoreCase(fileExtension);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String extract(InputStream is) {
        try (WordExtractor we = new WordExtractor(is);){
            String string = we.getText();
            return string;
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }
}
