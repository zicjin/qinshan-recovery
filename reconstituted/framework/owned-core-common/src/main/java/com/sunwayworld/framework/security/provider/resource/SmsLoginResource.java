/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.resource;

import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/open/sms-logins"})
public interface SmsLoginResource {
    @RequestMapping(value={"/action/country-code/{countryCode}/mobile/{mobile}/send-sms-code"}, method={RequestMethod.POST})
    public void sendSmsCode(@PathVariable(value="countryCode") String var1, @PathVariable(value="mobile") String var2);

    @RequestMapping(value={"/action/mobile/{mobile}/sms/{smsCode}/validate"}, method={RequestMethod.POST})
    public void validSms(@PathVariable(value="mobile") String var1, @PathVariable(value="smsCode") String var2);

    @RequestMapping(value={"/country-code/queries/selectable"}, method={RequestMethod.GET})
    public List<CoreCodeBean> getCountryCode();
}
