/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FtpException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtils {
    private static final String FTP_ENCODING = "ISO-8859-1";
    private static final String FTP_DEFAULT_ENCODING = "GBK";

    public static FTPClient getFtpClient(String ip, int port, String user, String password) {
        return FtpUtils.getFtpClient(ip, port, user, password, null);
    }

    public static FTPClient getFtpClient(String ip, int port, String user, String password, String folder) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, port);
            ftpClient.enterLocalPassiveMode();
            boolean loginSuccess = ftpClient.login(user, password);
            if (!loginSuccess) {
                throw new FtpException("GIKAM.EXCEPTION.FTP.CONNECTION");
            }
            ftpClient.setFileTransferMode(10);
            ftpClient.setFileType(2);
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
                ftpClient.setControlEncoding(StandardCharsets.UTF_8.name());
            } else {
                ftpClient.setControlEncoding(FTP_DEFAULT_ENCODING);
            }
            if (!StringUtils.isBlank(folder)) {
                Paths.get(folder, new String[0]).spliterator().forEachRemaining(p -> {
                    try {
                        ftpClient.changeWorkingDirectory(p.toString());
                    }
                    catch (IOException ioe) {
                        throw new FtpException("GIKAM.EXCEPTION.FTP.CONNECTION", ioe);
                    }
                });
            }
        }
        catch (IOException ioe) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.CONNECTION", ioe);
        }
        return ftpClient;
    }

    public static void disconnect(FTPClient ftpClient) {
        if (ftpClient != null) {
            try {
                ftpClient.disconnect();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public static void upload(FTPClient ftpClient, FilePathDTO filePath, InputStream is) {
        try {
            FtpUtils.makeAndChangeDirectory(ftpClient, filePath);
            ftpClient.storeFile(FtpUtils.getEncodedName(ftpClient, filePath.getFileName()), is);
        }
        catch (IOException ioe) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.UPLOAD", ioe);
        }
    }

    public static void upload(FTPClient ftpClient, FilePathDTO filePath) {
        try (InputStream is = Files.newInputStream(FilePathManager.getLocalPath(filePath), new OpenOption[0]);){
            FtpUtils.upload(ftpClient, filePath, is);
        }
        catch (IOException ioe) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.UPLOAD", ioe);
        }
    }

    public static void download(FTPClient ftpClient, FilePathDTO filePath) {
        Path path = FilePathManager.getLocalPath(filePath);
        FileUtils.makeDirs(path);
        FtpUtils.changeDirectory(ftpClient, filePath);
        try (OutputStream os = Files.newOutputStream(path, new OpenOption[0]);){
            ftpClient.retrieveFile(FtpUtils.getEncodedName(ftpClient, filePath.getFileName()), os);
        }
        catch (IOException ioe) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.DOWNLOAD", ioe);
        }
    }

    public static void download(FTPClient ftpClient, FilePathDTO filePath, Path toPath) {
        FileUtils.makeDirs(toPath);
        FtpUtils.changeDirectory(ftpClient, filePath);
        try (OutputStream os = Files.newOutputStream(toPath, new OpenOption[0]);){
            ftpClient.retrieveFile(FtpUtils.getEncodedName(ftpClient, filePath.getFileName().toString()), os);
        }
        catch (IOException ioe) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.DOWNLOAD", ioe);
        }
    }

    public static void copy(FTPClient ftpClient, FilePathDTO fromFilePath, FilePathDTO toFilePath) {
        String sourcePath = FtpUtils.getEncodedName(ftpClient, FtpUtils.getFtpFilePath(fromFilePath).toString());
        ByteArrayOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ftpClient.retrieveFile(sourcePath, outputStream);
            FtpUtils.makeAndChangeDirectory(ftpClient, toFilePath);
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ftpClient.storeFile(FtpUtils.getEncodedName(ftpClient, toFilePath.getFileName()), inputStream);
        }
        catch (Exception e) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.COPY");
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException iOException) {}
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private static void makeAndChangeDirectory(FTPClient ftpClient, FilePathDTO filePath) {
        try {
            ftpClient.makeDirectory("files");
            ftpClient.changeWorkingDirectory("files");
            ftpClient.makeDirectory(filePath.getScope().name());
            ftpClient.changeWorkingDirectory(filePath.getScope().name());
            if (!FileScope.finereport.equals((Object)filePath.getScope())) {
                ftpClient.makeDirectory(filePath.getTimestamp());
                ftpClient.changeWorkingDirectory(filePath.getTimestamp());
            }
            String directoryName = FtpUtils.getEncodedName(ftpClient, filePath.getIrregularName());
            ftpClient.makeDirectory(directoryName);
            ftpClient.changeWorkingDirectory(directoryName);
        }
        catch (IOException ioe) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.MAKEDIRECTORY", ioe);
        }
    }

    private static void changeDirectory(FTPClient ftpClient, FilePathDTO filePath) {
        try {
            ftpClient.changeWorkingDirectory("files");
            ftpClient.changeWorkingDirectory(filePath.getScope().name());
            if (!FileScope.finereport.equals((Object)filePath.getScope())) {
                ftpClient.changeWorkingDirectory(filePath.getTimestamp());
            }
            ftpClient.changeWorkingDirectory(filePath.getIrregularName());
        }
        catch (IOException ioe) {
            throw new FtpException("GIKAM.EXCEPTION.FTP.CHANGEDIRECTORY", ioe);
        }
    }

    private static String getEncodedName(FTPClient ftpClient, String name) {
        try {
            return new String(name.toString().getBytes(ftpClient.getControlEncoding()), FTP_ENCODING);
        }
        catch (IOException ioe) {
            throw new FtpException(ioe);
        }
    }

    private static Path getFtpFilePath(FilePathDTO filePath) {
        return Paths.get("files", filePath.getScope().name(), filePath.getTimestamp(), filePath.getIrregularName(), filePath.getFileName());
    }
}
