/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.user.service;

import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import org.springframework.web.bind.annotation.PathVariable;

public interface CoreUserPasswordService {
    public void sendEmail(String var1, String var2);

    public void validUrl(String var1, String var2);

    public void resetPassword(RestJsonWrapperBean var1);

    public String validUserAndEmail(String var1, String var2);

    public String validateUserPassword(String var1, RestJsonWrapperBean var2);

    public CorePasswordPolicyBean selectPasswordPolicyByUserId(String var1);

    public RestHttpTextEntity sendSmsVerification(String var1, String var2);

    public boolean checkSmsVerificationCode(String var1, RestJsonWrapperBean var2);

    public RestHttpTextEntity resetPasswordBySms(@PathVariable String var1, RestJsonWrapperBean var2);
}
