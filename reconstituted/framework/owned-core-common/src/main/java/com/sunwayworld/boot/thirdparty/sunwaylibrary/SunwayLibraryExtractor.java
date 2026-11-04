/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.boot.thirdparty.sunwaylibrary;

import com.sunwayworld.boot.thirdparty.Extractor;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SunwayLibraryExtractor
implements Extractor {
    @Override
    public void extractForWindows() {
        Path workingPath = SystemUtils.getWorkingPath();
        Path targetPath = workingPath.resolve("sunway-app.dll");
        if ("1".equals(System.getProperty("sunway-app-reset"))) {
            try {
                Files.deleteIfExists(targetPath);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        ArchiveUtils.extractResourceIfNotPresent("/third-party/sunway-app/windows/sunway-app.dll", targetPath);
        System.load(targetPath.toString());
    }

    @Override
    public void extractForLinux() {
        Path workingPath = SystemUtils.getWorkingPath();
        Path targetPath = workingPath.resolve("sunway-app.so");
        if ("1".equals(System.getProperty("sunway-app-reset"))) {
            try {
                Files.deleteIfExists(targetPath);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        ArchiveUtils.extractResourceIfNotPresent("/third-party/sunway-app/linux/sunway-app.so", targetPath);
        SystemUtils.exec("chmod +x " + targetPath.toString());
        System.load(targetPath.toString());
    }

    @Override
    public void extractForArm() {
        Path workingPath = SystemUtils.getWorkingPath();
        Path targetPath = workingPath.resolve("sunway-app.so");
        if ("1".equals(System.getProperty("sunway-app-reset"))) {
            try {
                Files.deleteIfExists(targetPath);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        ArchiveUtils.extractResourceIfNotPresent("/third-party/sunway-app/arm/sunway-app.so", targetPath);
        SystemUtils.exec("chmod +x " + targetPath.toString());
        System.load(targetPath.toString());
    }

    @Override
    public void extractForLoongArch() {
        Path workingPath = SystemUtils.getWorkingPath();
        Path targetPath = workingPath.resolve("sunway-app.so");
        if ("1".equals(System.getProperty("sunway-app-reset"))) {
            try {
                Files.deleteIfExists(targetPath);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        ArchiveUtils.extractResourceIfNotPresent("/third-party/sunway-app/loongarch/sunway-app.so", targetPath);
        SystemUtils.exec("chmod +x " + targetPath.toString());
        System.load(targetPath.toString());
    }

    @Override
    public void extractForMac() {
        Path workingPath = SystemUtils.getWorkingPath();
        Path targetPath = workingPath.resolve("sunway-app.o");
        if ("1".equals(System.getProperty("sunway-app-reset"))) {
            try {
                Files.deleteIfExists(targetPath);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        ArchiveUtils.extractResourceIfNotPresent("/third-party/sunway-app/mac/sunway-app.o", targetPath);
        System.load(targetPath.toString());
    }
}
