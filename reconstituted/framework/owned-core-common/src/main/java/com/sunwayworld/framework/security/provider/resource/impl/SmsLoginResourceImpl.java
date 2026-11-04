/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.resource.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.provider.resource.SmsLoginResource;
import com.sunwayworld.framework.security.provider.service.SmsLoginService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class SmsLoginResourceImpl
implements SmsLoginResource {
    @Autowired
    private SmsLoginService smsService;
    @Autowired
    private CoreCodeService codeService;

    @Override
    public void sendSmsCode(String countryCode, String mobile) {
        this.smsService.sendSmsCode(countryCode, mobile);
    }

    @Override
    public void validSms(String mobile, String smsCode) {
        this.smsService.validSms(mobile, smsCode);
    }

    @Override
    public List<CoreCodeBean> getCountryCode() {
        return this.codeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "countryCode").filter(MatchPattern.EQ), Order.asc("ORDERNO"));
    }
}
