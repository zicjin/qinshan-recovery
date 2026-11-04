/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public abstract class ArchiveUtils {
    private static final int MAX_BUFFER_SIZE = 1024;

    public static final void extractResourceIfNotPresent(String resourceName, Path destPath) {
        File f = destPath.toFile();
        if (f.isDirectory()) {
            throw new FileException("core.zip.dest-not-be-directory");
        }
        if (!f.exists()) {
            try (InputStream exeIs = ArchiveUtils.class.getResourceAsStream(resourceName);){
                if (exeIs != null) {
                    Files.copy(exeIs, destPath, new CopyOption[0]);
                }
            }
            catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    public static void zip(Path dest, Path ... sources) {
        if (sources == null) {
            ArchiveUtils.zip(dest.toFile(), new File[0]);
        } else {
            ArchiveUtils.zip(dest.toFile(), (File[])Arrays.stream(sources).map(s -> s.toFile()).toArray(File[]::new));
        }
    }

    public static void zip(File dest, File ... sources) {
        if (dest.isDirectory()) {
            throw new FileException("GIKAM.FILE.ZIP.DEST-MUST-BE-A-FILE");
        }
        if (dest.exists()) {
            dest.delete();
            try {
                dest.createNewFile();
            }
            catch (IOException e) {
                throw new FileException(e);
            }
        } else {
            FileUtils.createFile(dest);
        }
        if (sources == null || sources.length == 0) {
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(dest);
             ZipOutputStream out = new ZipOutputStream(fos);){
            for (File source : sources) {
                ArchiveUtils.putFile(out, source, null);
            }
        }
        catch (IOException ioe) {
            throw new com.sunwayworld.framework.exception.ZipException(ioe);
        }
    }

    private static final void putFile(ZipOutputStream out, File file, String dir) {
        if (file.isDirectory()) {
            String dirPath = StringUtils.isEmpty(dir) ? file.getName() : dir + File.separator + file.getName();
            File[] subFiles = file.listFiles();
            if (subFiles.length == 0) {
                try {
                    out.putNextEntry(new ZipEntry(dirPath));
                    out.closeEntry();
                }
                catch (IOException ioe) {
                    throw new com.sunwayworld.framework.exception.ZipException(ioe);
                }
            } else {
                for (File subFile : subFiles) {
                    ArchiveUtils.putFile(out, subFile, dirPath);
                }
            }
        } else if (file.isFile()) {
            try (FileInputStream is = new FileInputStream(file);){
                String zipEntryName = StringUtils.isEmpty(dir) ? file.getName() : dir + File.separator + file.getName();
                String extension = FileUtils.getFileExtension(file);
                String zipEntryNameCopy = zipEntryName;
                String zipEntryBaseName = StringUtils.isEmpty(extension) ? zipEntryName : StringUtils.removeEnd(zipEntryName, "." + extension);
                while (true) {
                    try {
                        out.putNextEntry(new ZipEntry(zipEntryName));
                    }
                    catch (ZipException e) {
                        if (StringUtils.contains(e.getMessage(), "duplicate entry:")) {
                            if (zipEntryName.equals(zipEntryNameCopy)) {
                                zipEntryName = zipEntryBaseName + "-1" + (StringUtils.isEmpty(extension) ? "" : "." + extension);
                                continue;
                            }
                            String suffix = StringUtils.removeStart(zipEntryName, zipEntryBaseName + "-");
                            suffix = StringUtils.isEmpty(extension) ? suffix : StringUtils.removeEnd(suffix, "." + extension);
                            int no = NumberUtils.parseInt(suffix);
                            zipEntryName = zipEntryBaseName + "-" + (no + 1) + (StringUtils.isEmpty(extension) ? "" : "." + extension);
                            continue;
                        }
                        throw e;
                    }
                    break;
                }
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = ((InputStream)is).read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.closeEntry();
            }
            catch (IOException ioe) {
                throw new com.sunwayworld.framework.exception.ZipException(ioe);
            }
        }
    }
}
