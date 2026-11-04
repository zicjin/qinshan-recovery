package com.sunwayworld.framework.utils;

import java.nio.file.Files;
import java.nio.file.Path;

/** Focused checks for methods required by reconstructed framework source. */
public final class FileUtilsReconstructionTest {
    private FileUtilsReconstructionTest() {
    }

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("file-utils-reconstruction-");
        try {
            Path nested = root.resolve("a").resolve("b").resolve("value.txt");
            FileUtils.createFile(nested);
            require(Files.isRegularFile(nested), "createFile(Path) did not create the file");
            FileUtils.createFile(nested);
            require(Files.size(nested) == 0, "createFile(Path) changed an existing file");

            require(FileUtils.isImage(new java.io.File("photo.JPG")), "JPG image was not recognized");
            require(FileUtils.isImage(new java.io.File("photo.jpeg")), "JPEG image was not recognized");
            require(FileUtils.isImage(new java.io.File("photo.png")), "PNG image was not recognized");
            require(FileUtils.isImage(new java.io.File("photo.bmp")), "BMP image was not recognized");
            require(FileUtils.isImage(new java.io.File("photo.gif")), "GIF image was not recognized");
            require(!FileUtils.isImage(new java.io.File("report.pdf")), "non-image was recognized");
            require(
                    FileUtils.isExtension(new java.io.File("report.PDF"), "pdf"),
                    "isExtension did not ignore case");
            require(
                    !FileUtils.isExtension(new java.io.File("report.pdf"), "png"),
                    "isExtension accepted a different extension");
        } finally {
            FileUtils.deleteRecursively(root);
        }
        System.out.println("FileUtilsReconstructionTest PASS");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
