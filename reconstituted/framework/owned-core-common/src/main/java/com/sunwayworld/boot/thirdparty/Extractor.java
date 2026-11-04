/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.boot.thirdparty;

import com.sunwayworld.framework.exception.UnsupportedOperationSystemException;
import com.sunwayworld.framework.utils.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Extractor {
    public static final Logger logger = LogManager.getLogger(Extractor.class);

    default public void extract() {
        if (SystemUtils.isWindows()) {
            this.extractForWindows();
        } else if (SystemUtils.isArm()) {
            this.extractForArm();
        } else if (SystemUtils.isLinux()) {
            this.extractForLinux();
        } else if (SystemUtils.isMac()) {
            this.extractForMac();
        } else {
            throw new UnsupportedOperationSystemException(SystemUtils.getOSName());
        }
    }

    public void extractForWindows();

    public void extractForLinux();

    default public void extractForArm() {
        this.extractForLinux();
    }

    default public void extractForLoongArch() {
        this.extractForLinux();
    }

    default public void extractForMac() {
        logger.warn(this.getClass() + " Unsupported.");
    }
}
