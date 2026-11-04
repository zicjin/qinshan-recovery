/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.security.bean.QrCodeLoginDTO;
import com.sunwayworld.framework.security.bean.QrCodeLoginStatusDTO;
import com.sunwayworld.framework.security.bean.QrCodeScanStatusDTO;
import com.sunwayworld.framework.security.provider.service.QrCodeLoginService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class QrCodeLoginServiceImpl
implements QrCodeLoginService {
    private static final String QR_CODE_LOGIN_CACHE = "QR_CODE_LOGIN_CACHE";

    @Override
    public QrCodeLoginDTO getQrCode() {
        QrCodeLoginDTO login = new QrCodeLoginDTO();
        String qrToken = StringUtils.randomUUID();
        login.setQrToken(qrToken);
        login.setLoginStatusUrl(this.getLoginStatusUrl(qrToken));
        login.setScanUrl(this.getScanUrl(qrToken));
        QrCodeLoginStatusDTO loginStatus = new QrCodeLoginStatusDTO();
        loginStatus.setStatus("0");
        RedisHelper.put(QR_CODE_LOGIN_CACHE, qrToken, loginStatus, 60);
        return login;
    }

    @Override
    public QrCodeLoginStatusDTO getLoginStatus(String qrToken) {
        QrCodeLoginStatusDTO loginStatus = (QrCodeLoginStatusDTO)RedisHelper.get(QR_CODE_LOGIN_CACHE, qrToken);
        if (null == loginStatus) {
            loginStatus = new QrCodeLoginStatusDTO();
            loginStatus.setStatus("-1");
        }
        if ("2".equals(loginStatus.getStatus())) {
            loginStatus.setLoginSuccessUrl(this.getLoginSuccessUrl(qrToken));
        }
        return loginStatus;
    }

    @Override
    public QrCodeScanStatusDTO scan() {
        QrCodeScanStatusDTO scanStatus = new QrCodeScanStatusDTO();
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String qrToken = request.getParameter("qrToken");
        if (StringUtils.isEmpty(qrToken)) {
            scanStatus.setStatus("0");
            return scanStatus;
        }
        QrCodeLoginStatusDTO loginStatus = (QrCodeLoginStatusDTO)RedisHelper.get(QR_CODE_LOGIN_CACHE, qrToken);
        if (null == loginStatus) {
            scanStatus.setStatus("-1");
            return scanStatus;
        }
        CoreUserBean user = this.getScanedUser();
        if (null == user) {
            scanStatus.setStatus("-2");
            return scanStatus;
        }
        String scanSuccessKey = StringUtils.randomUUID();
        loginStatus.setStatus("1");
        loginStatus.setUserId(user.getId());
        loginStatus.setUserName(user.getUserName());
        loginStatus.setScanSuccessKey(scanSuccessKey);
        RedisHelper.put(QR_CODE_LOGIN_CACHE, qrToken, loginStatus, 60);
        scanStatus.setStatus("1");
        scanStatus.setConfirmUrl(this.getConfirmUrl(qrToken, scanSuccessKey));
        return scanStatus;
    }

    @Override
    public QrCodeScanStatusDTO confirm() {
        QrCodeScanStatusDTO scanStatus = new QrCodeScanStatusDTO();
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String qrToken = request.getParameter("qrToken");
        if (StringUtils.isEmpty(qrToken)) {
            scanStatus.setStatus("0");
            return scanStatus;
        }
        QrCodeLoginStatusDTO loginStatus = (QrCodeLoginStatusDTO)RedisHelper.get(QR_CODE_LOGIN_CACHE, qrToken);
        if (null == loginStatus) {
            scanStatus.setStatus("-1");
            return scanStatus;
        }
        String scanSuccessKey = request.getParameter("scanSuccessKey");
        if (!StringUtils.equals(loginStatus.getScanSuccessKey(), scanSuccessKey)) {
            scanStatus.setStatus("-3");
            return scanStatus;
        }
        loginStatus.setStatus("2");
        RedisHelper.put(QR_CODE_LOGIN_CACHE, qrToken, loginStatus, 60);
        scanStatus.setStatus("2");
        return scanStatus;
    }

    private String getLoginStatusUrl(String qrToken) {
        return this.getUrl() + "/open/qrcode-logins/statuses/" + qrToken;
    }

    private String getLoginSuccessUrl(String qrToken) {
        return this.getUrl() + "?loginType=qrCodeLogin&qrToken=" + qrToken;
    }

    private String getScanUrl(String qrToken) {
        return this.getUrl() + "/open/qrcode-logins/action/scan?qrToken=" + qrToken;
    }

    private String getConfirmUrl(String qrToken, String scanSuccessKey) {
        return this.getUrl() + "/open/qrcode-logins/action/confirm?qrToken=" + qrToken + "&scanSuccessKey=" + scanSuccessKey;
    }

    private String getUrl() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        return StringUtils.removeEnd(request.getRequestURL().toString(), request.getRequestURI());
    }
}
