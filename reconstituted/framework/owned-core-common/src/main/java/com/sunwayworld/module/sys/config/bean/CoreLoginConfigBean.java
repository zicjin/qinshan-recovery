/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_LOGIN_CONFIG")
public class CoreLoginConfigBean
extends AbstractPersistable<String>
implements Persistable<String> {
    @Transient
    private static final long serialVersionUID = -3643458253923297662L;
    @Id
    private String id;
    private String pageStyle;
    private String pageSkin;
    private String loginOrg;
    private String loginRole;
    private String qrCode;
    private String retrievePassword;
    private String retrievePasswordWay;
    private String smsLogin;
    private String mobileClient;
    private String iphoneQrCode;
    private String androidMobileQrCode;
    private String androidPadQrCode;
    private String loginAuthMethod;
    private String adDomainServer;
    private Long adDomainPort;
    private String adDomainUpnSuffix;
    private String adUserIdIgnoreCase;
    private String captcha;
    private String loginTenant;
    private String grayMode;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getPageStyle() {
        return this.pageStyle;
    }

    public void setPageStyle(String pageStyle) {
        this.pageStyle = pageStyle;
    }

    public String getPageSkin() {
        return this.pageSkin;
    }

    public void setPageSkin(String pageSkin) {
        this.pageSkin = pageSkin;
    }

    public String getLoginOrg() {
        return this.loginOrg;
    }

    public void setLoginOrg(String loginOrg) {
        this.loginOrg = loginOrg;
    }

    public String getLoginRole() {
        return this.loginRole;
    }

    public void setLoginRole(String loginRole) {
        this.loginRole = loginRole;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getRetrievePassword() {
        return this.retrievePassword;
    }

    public void setRetrievePassword(String retrievePassword) {
        this.retrievePassword = retrievePassword;
    }

    public String getRetrievePasswordWay() {
        return this.retrievePasswordWay;
    }

    public void setRetrievePasswordWay(String retrievePasswordWay) {
        this.retrievePasswordWay = retrievePasswordWay;
    }

    public String getSmsLogin() {
        return this.smsLogin;
    }

    public void setSmsLogin(String smsLogin) {
        this.smsLogin = smsLogin;
    }

    public String getMobileClient() {
        return this.mobileClient;
    }

    public void setMobileClient(String mobileClient) {
        this.mobileClient = mobileClient;
    }

    public String getIphoneQrCode() {
        return this.iphoneQrCode;
    }

    public void setIphoneQrCode(String iphoneQrCode) {
        this.iphoneQrCode = iphoneQrCode;
    }

    public String getAndroidMobileQrCode() {
        return this.androidMobileQrCode;
    }

    public void setAndroidMobileQrCode(String androidMobileQrCode) {
        this.androidMobileQrCode = androidMobileQrCode;
    }

    public String getAndroidPadQrCode() {
        return this.androidPadQrCode;
    }

    public void setAndroidPadQrCode(String androidPadQrCode) {
        this.androidPadQrCode = androidPadQrCode;
    }

    public String getLoginAuthMethod() {
        return this.loginAuthMethod;
    }

    public void setLoginAuthMethod(String loginAuthMethod) {
        this.loginAuthMethod = loginAuthMethod;
    }

    public String getAdDomainServer() {
        return this.adDomainServer;
    }

    public void setAdDomainServer(String adDomainServer) {
        this.adDomainServer = adDomainServer;
    }

    public Long getAdDomainPort() {
        return this.adDomainPort;
    }

    public void setAdDomainPort(Long adDomainPort) {
        this.adDomainPort = adDomainPort;
    }

    public String getAdDomainUpnSuffix() {
        return this.adDomainUpnSuffix;
    }

    public void setAdDomainUpnSuffix(String adDomainUpnSuffix) {
        this.adDomainUpnSuffix = adDomainUpnSuffix;
    }

    public String getAdUserIdIgnoreCase() {
        return this.adUserIdIgnoreCase;
    }

    public void setAdUserIdIgnoreCase(String adUserIdIgnoreCase) {
        this.adUserIdIgnoreCase = adUserIdIgnoreCase;
    }

    public String getCaptcha() {
        return this.captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getLoginTenant() {
        return this.loginTenant;
    }

    public void setLoginTenant(String loginTenant) {
        this.loginTenant = loginTenant;
    }

    public String getGrayMode() {
        return this.grayMode;
    }

    public void setGrayMode(String grayMode) {
        this.grayMode = grayMode;
    }
}
