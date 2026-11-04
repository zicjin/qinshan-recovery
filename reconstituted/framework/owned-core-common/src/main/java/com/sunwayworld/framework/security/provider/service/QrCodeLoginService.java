/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.service;

import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.security.bean.QrCodeLoginDTO;
import com.sunwayworld.framework.security.bean.QrCodeLoginStatusDTO;
import com.sunwayworld.framework.security.bean.QrCodeScanStatusDTO;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

public interface QrCodeLoginService {
    public static final String QR_LOGIN_STATUS_NONE = "0";
    public static final String QR_LOGIN_STATUS_NOCONFIRMED = "1";
    public static final String QR_LOGIN_STATUS_SUCCESS = "2";
    public static final String QR_LOGIN_STATUS_EXPIRED = "-1";
    public static final String QR_LOGIN_SCAN_STATUS_QRTOKEN_EMPTY = "0";
    public static final String QR_LOGIN_SCAN_STATUS_NOCONFIRMED = "1";
    public static final String QR_LOGIN_SCAN_STATUS_SUCCESS = "2";
    public static final String QR_LOGIN_SCAN_STATUS_EXPIRED = "-1";
    public static final String QR_LOGIN_SCAN_STATUS_USER_EXCEPTION = "-2";
    public static final String QR_LOGIN_SCAN_STATUS_SUCCESS_KEY_EXCEPTION = "-3";

    public QrCodeLoginDTO getQrCode();

    public QrCodeLoginStatusDTO getLoginStatus(String var1);

    public QrCodeScanStatusDTO scan();

    public QrCodeScanStatusDTO confirm();

    default public CoreUserBean getScanedUser() {
        throw new UnsupportedException("You should implements this method if required.");
    }
}
