/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.text.FileToTextExtractor;
import com.sunwayworld.framework.io.text.FileToTextExtractorFactory;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.EncodingUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public final class FileUtils {
    public static final void makeDirs(Path regularFilePath) {
        if (regularFilePath == null) {
            return;
        }
        Path dirPath = regularFilePath.getParent();
        if (dirPath != null && !Files.exists(dirPath, new LinkOption[0])) {
            try {
                Files.createDirectories(dirPath, new FileAttribute[0]);
            }
            catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    public static final void write(Path regularFilePath, String content) {
        FileUtils.makeDirs(regularFilePath);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(regularFilePath, new OpenOption[0]);){
            bufferedWriter.write(content);
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final void write(Path regularFilePath, InputStream is) {
        FileUtils.makeDirs(regularFilePath);
        try (OutputStream os = Files.newOutputStream(regularFilePath, new OpenOption[0]);){
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = is.read(buffer)) != -1) {
                os.write(buffer, 0, size);
            }
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final void write(File file, String content) {
        FileUtils.createFile(file);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file), "UTF-8"));){
            writer.write(content);
            ((Writer)writer).flush();
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final void write(File file, InputStream is) {
        FileUtils.createFile(file);
        try (FileOutputStream fos = new FileOutputStream(file);){
            byte[] buffer = new byte[4096048];
            int size = 0;
            while ((size = is.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static final List<String> readAllLines(File file) {
        try (FileInputStream fis = new FileInputStream(file);){
            String content = StringUtils.read(fis);
            List<String> list = Arrays.asList(StringUtils.regexSplit(content, "(\n|\r)+"));
            return list;
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final File createTempFile() {
        try {
            return File.createTempFile(StringUtils.randomUUID(), "");
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final File createTempFile(String extension) {
        try {
            if (extension != null && !StringUtils.startsWith(extension, ".")) {
                extension = "." + extension;
            }
            return File.createTempFile(StringUtils.randomUUID(), extension);
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final void createFile(Path path) {
        if (Files.exists(path, new LinkOption[0])) {
            return;
        }
        FileUtils.makeDirs(path);
        if (!Files.exists(path, new LinkOption[0])) {
            try {
                Files.createFile(path, new FileAttribute[0]);
            }
            catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    public static final void createFile(File file) {
        if (file.exists()) {
            return;
        }
        file.getParentFile().mkdirs();
        if (file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    public static final String getFileExtension(String fileName) {
        if (fileName.indexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static final String getFileExtension(File file) {
        return FileUtils.getFileExtension(file.toString());
    }

    public static final String getFileExtension(Path path) {
        return FileUtils.getFileExtension(path.getFileName().toString());
    }

    public static final String getFileBaseName(File file) {
        return FileUtils.getFileBaseName(file.toPath());
    }

    public static final String getFileBaseName(Path path) {
        String fileName = path.getFileName().toString();
        return FileUtils.getFileBaseName(fileName);
    }

    public static String getFileBaseName(String fileName) {
        if (fileName.indexOf(".") == -1) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileNameWithNewExtension(String fileName, String newExtension) {
        return FileUtils.getFileBaseName(fileName) + "." + newExtension;
    }

    public static void deleteRecursively(Path path) {
        block17: {
            if (Files.isDirectory(path, new LinkOption[0])) {
                try (Stream<Path> list = Files.list(path);){
                    list.forEach(p -> FileUtils.deleteRecursively(p));
                    Files.deleteIfExists(path);
                    break block17;
                }
                catch (IOException ioe) {
                    throw new FileException(ioe);
                }
            }
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    public static void deleteQuietly(File file) {
        if (file != null) {
            try {
                file.delete();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static final boolean isInJarURL(URL url) {
        return StringUtils.contains(url.getPath(), ".jar!");
    }

    public static List<File> getFileList(Path dir) {
        if (!Files.isDirectory(dir, new LinkOption[0])) {
            return CollectionUtils.emptyList();
        }
        final ArrayList<File> fileList = new ArrayList<File>();
        try {
            Files.walkFileTree(dir, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fileList.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }
            });
            return fileList;
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }

    public static final String makeValidFilename(String name) {
        StringBuilder sb = new StringBuilder();
        block4: for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            switch (c) {
                case ' ': {
                    sb.append("_");
                    continue block4;
                }
                case '\"':
                case '*':
                case '/':
                case ':':
                case '<':
                case '>':
                case '?':
                case '\\':
                case '|': {
                    continue block4;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public static final void encode(Path srcPath, Path destPath, Charset targetCharset) {
        try (InputStream is = Files.newInputStream(srcPath, new OpenOption[0]);
             DataInputStream in = new DataInputStream(is);){
            BufferedReader d = new BufferedReader(new InputStreamReader((InputStream)in, EncodingUtils.detect(srcPath.toFile())));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = d.readLine()) != null) {
                if (sb.length() != 0) {
                    sb.append(System.lineSeparator());
                }
                sb.append(line);
            }
            FileUtils.makeDirs(destPath);
            try (BufferedWriter ow = Files.newBufferedWriter(destPath, targetCharset, new OpenOption[0]);){
                ow.write(sb.toString());
            }
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static String extractTextFromFile(File file) {
        FileToTextExtractor textExtractor = FileToTextExtractorFactory.getFileToTextExtractor(FileUtils.getFileExtension(file.getName()));
        if (textExtractor == null) {
            return "";
        }
        return textExtractor.extract(file);
    }

    public static String extractText(InputStream is, String extension) {
        FileToTextExtractor textExtractor = FileToTextExtractorFactory.getFileToTextExtractor(extension);
        if (textExtractor == null) {
            return "";
        }
        return textExtractor.extract(is);
    }

    public static String extractFullTextFromFile(File file) {
        FileToTextExtractor textExtractor = FileToTextExtractorFactory.getFileToFullTextExtractor(FileUtils.getFileExtension(file.getName()));
        if (textExtractor == null) {
            return "";
        }
        return textExtractor.extract(file);
    }

    public static String extractFullText(InputStream is, String extension) {
        FileToTextExtractor textExtractor = FileToTextExtractorFactory.getFileToFullTextExtractor(extension);
        if (textExtractor == null) {
            return "";
        }
        return textExtractor.extract(is);
    }

    public static boolean isImage(File file) {
        String extension = FileUtils.getFileExtension(file).toLowerCase();
        return "jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension) || "bmp".equals(extension) || "gif".equals(extension);
    }

    public static boolean isExtension(File file, String extension) {
        return extension.equalsIgnoreCase(FileUtils.getFileExtension(file).toLowerCase());
    }

    public static final MultipartFile makeMultipartFile(File file) {
        DiskFileItem fileItem = new DiskFileItem("file", "multipart/form-data", true, file.getName(), 0xA00000, file.getParentFile());
        try (OutputStream os = fileItem.getOutputStream();
             FileInputStream is = new FileInputStream(file);){
            int length = 0;
            byte[] bytes = new byte[4096];
            while ((length = ((InputStream)is).read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }
        }
        catch (IOException ex) {
            throw new FileException(ex);
        }
        return new CommonsMultipartFile(fileItem);
    }

    public static final MultipartFile makeMultipartFile(String name, InputStream is) {
        DiskFileItem fileItem = new DiskFileItem("file", "multipart/form-data", true, name, 0xA00000, FileUtils.getTempDir());
        try (OutputStream os = fileItem.getOutputStream();){
            int length = 0;
            byte[] bytes = new byte[4096];
            while ((length = is.read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }
        }
        catch (IOException ex) {
            throw new FileException(ex);
        }
        return new CommonsMultipartFile(fileItem);
    }

    public static final File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}
