/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.user.resource.impl;

import com.sunwayworld.cloud.module.user.user.resource.CoreUserPasswordResource;
import com.sunwayworld.cloud.module.user.user.service.CoreUserPasswordService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5bc6\u7801\u91cd\u7f6e")
@RestController
@GikamBean
@RequestMapping(value={"/open/core/module/mdm/user-passwords"})
public class CoreUserPasswordResourceImpl
implements CoreUserPasswordResource {
    @Autowired
    private CoreUserPasswordService userPasswordService;

    public CoreUserPasswordService getService() {
        return this.userPasswordService;
    }

    @Override
    @Log(value="\u53d1\u9001\u5bc6\u7801\u91cd\u7f6e\u90ae\u4ef6", type=LogType.EXPORT)
    public void sendEmail(String userId, String email) {
        this.getService().sendEmail(userId, email);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5bc6\u7801\u8981\u91cd\u7f6e\u7684\u7528\u6237\u4fe1\u606f", type=LogType.SELECT)
    public void validUrl(String userId, String key) {
        this.getService().validUrl(userId, key);
    }

    @Override
    @Log(value="\u8bbe\u7f6e\u7528\u6237\u5bc6\u7801", type=LogType.UPDATE)
    @ResponseStatus(value=HttpStatus.OK)
    public void resetUserPwd(RestJsonWrapperBean wrapper) {
        this.getService().resetPassword(wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u7528\u6237\u7684\u5bc6\u7801", type=LogType.VALIDATE)
    public String validateUserPassword(@PathVariable String userId, RestJsonWrapperBean wrapper) {
        return this.getService().validateUserPassword(userId, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u7528\u6237\u7f16\u7801\u67e5\u8be2\u5bc6\u7801\u7b56\u7565", type=LogType.SELECT)
    public CorePasswordPolicyBean selectPasswordPolicyByUserId(String userId) {
        return this.getService().selectPasswordPolicyByUserId(userId);
    }

    @Override
    @Log(value="\u53d1\u9001\u7528\u4e8e\u91cd\u7f6e\u5bc6\u7801\u7684\u77ed\u4fe1\u9a8c\u8bc1\u7801", type=LogType.DEFAULT)
    public RestHttpTextEntity sendSmsVerificationCode(@PathVariable String userId, @PathVariable String mobile) {
        return this.getService().sendSmsVerification(userId, mobile);
    }

    @Override
    @Log(value="\u6821\u9a8c\u7528\u4e8e\u91cd\u7f6e\u5bc6\u7801\u7684\u77ed\u4fe1\u9a8c\u8bc1\u7801", type=LogType.DEFAULT)
    public boolean checkSmsVerificationCode(@PathVariable String userId, RestJsonWrapperBean wrapper) {
        return this.getService().checkSmsVerificationCode(userId, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u77ed\u4fe1\u9a8c\u8bc1\u7801\u91cd\u7f6e\u5bc6\u7801", type=LogType.DEFAULT)
    public RestHttpTextEntity resetPasswordBySms(@PathVariable String userId, RestJsonWrapperBean wrapper) {
        return this.getService().resetPasswordBySms(userId, wrapper);
    }
}
