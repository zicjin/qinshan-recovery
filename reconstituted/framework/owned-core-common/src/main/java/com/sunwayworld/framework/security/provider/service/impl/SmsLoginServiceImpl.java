/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.provider.service.SmsLoginService;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.sms.core.CoreSmsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class SmsLoginServiceImpl
implements SmsLoginService {
    private static final Logger logger = LogManager.getLogger(SmsLoginServiceImpl.class);
    @Autowired
    private CoreUserService userService;
    @Autowired(required=false)
    private CoreSmsManager smsManager;
    public static final String SMS_LOGIN_CACHE = "SMS_LOGIN_CACHE";
    private static final int SMS_LOGIN_EXPIRED_TIME = 600;

    @Override
    public void sendSmsCode(String countryCode, String mobile) {
        CoreUserBean user = (CoreUserBean)this.userService.selectFirstByFilter(SearchFilter.instance().match("MOBILE", mobile).filter(MatchPattern.EQ), Order.desc("ID"));
        DefaultUserDetailsChecker.check(user);
        Integer smsCode = this.getSmsCode();
        RedisHelper.put(SMS_LOGIN_CACHE, mobile, smsCode.toString(), 600);
        String smsContent = I18nHelper.getMessage("SMS_LOGING.SMS_CONTENT", smsCode.toString());
        try {
            this.smsManager.send(countryCode + mobile, smsContent);
        }
        catch (Exception e) {
            logger.warn(e);
            throw new InvalidDataException("SMS_LOGIN.TIP.SEND_SMS_FAILED");
        }
    }

    @Override
    public void validSms(String mobile, String smsCode) {
        CoreUserBean user = (CoreUserBean)this.userService.selectFirstByFilter(SearchFilter.instance().match("MOBILE", mobile).filter(MatchPattern.EQ), Order.desc("ID"));
        if (user == null) {
            throw new InvalidDataException("SMS_LOGIN.TIP.MOBILE_DOESNOT_EXIST");
        }
        if (!smsCode.equals(RedisHelper.get(SMS_LOGIN_CACHE, mobile))) {
            throw new InvalidDataException("SMS_LOGIN.TIP.SMSCODE_MOBILE_DOESNOT_MATACH");
        }
    }

    private Integer getSmsCode() {
        return (int)((Math.random() * 9.0 + 1.0) * 100000.0);
    }
}
