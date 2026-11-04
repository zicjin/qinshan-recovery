/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.user.resource;

import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreUserPasswordResource {
    @RequestMapping(value={"/users/{userId}/email/{email}/action/send-email"}, method={RequestMethod.POST})
    public void sendEmail(@PathVariable String var1, @PathVariable String var2);

    @RequestMapping(value={"/users/{userId}/key/{key}/action/validate-url"}, method={RequestMethod.POST})
    public void validUrl(@PathVariable String var1, @PathVariable String var2);

    @RequestMapping(value={"/action/reset"}, method={RequestMethod.POST})
    public void resetUserPwd(RestJsonWrapperBean var1);

    @RequestMapping(value={"/users/{userId}/password-policies/queries"}, method={RequestMethod.GET})
    public CorePasswordPolicyBean selectPasswordPolicyByUserId(@PathVariable String var1);

    @RequestMapping(value={"/users/{userId}/action/validate-password"}, method={RequestMethod.POST})
    public String validateUserPassword(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/users/{userId}/{mobile}/action/send-sms"}, method={RequestMethod.POST})
    public RestHttpTextEntity sendSmsVerificationCode(@PathVariable String var1, @PathVariable String var2);

    @RequestMapping(value={"/users/{userId}/action/check-sms-vc"}, method={RequestMethod.POST})
    public boolean checkSmsVerificationCode(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/users/{userId}/action/reset-by-sms"}, method={RequestMethod.POST})
    public RestHttpTextEntity resetPasswordBySms(@PathVariable String var1, RestJsonWrapperBean var2);
}
