/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.text;

import com.sunwayworld.framework.exception.FileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileToTextExtractor {
    public boolean match(String var1);

    public String extract(InputStream var1);

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    default public String extract(File file) {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));){
            String string = this.extract(is);
            return string;
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    default public String extract(Path path) {
        return this.extract(path.toFile());
    }
}
