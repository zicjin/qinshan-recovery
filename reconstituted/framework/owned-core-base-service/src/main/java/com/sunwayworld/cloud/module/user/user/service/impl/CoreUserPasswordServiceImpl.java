/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.user.service.impl;

import com.sunwayworld.cloud.module.user.user.service.CoreUserPasswordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.ResultEntity;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mail.ExchangeMailHelper;
import com.sunwayworld.framework.mail.MailHelper;
import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.service.CorePasswordHisService;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import com.sunwayworld.module.sys.sms.service.CoreSmsService;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreUserPasswordServiceImpl
implements CoreUserPasswordService {
    private static final int RESET_PASSWORD_EXPIRED_TIME = 600;
    private static final String RESET_PASSWORD_EXPIRED_REDIS_NAME = "RETRIEVE_PASSWORD";
    private static final String MAIL_PROTOCOL_TYPE = "sunway.mail.protocol.type";
    @Autowired
    @Lazy
    private CoreUserService userService;
    @Autowired
    @Lazy
    private CorePasswordHisService passwordHisService;
    @Autowired
    @Lazy
    private CorePasswordPolicyService passwordPolicyService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CoreSmsService smsService;

    @Override
    public void sendEmail(String userId, String email) {
        CoreUserBean selectUser = new CoreUserBean();
        selectUser.setId(userId);
        selectUser.setEmail(email);
        CoreUserBean user = this.userService.getDao().selectOneIfPresent((CoreUserBean)selectUser, new String[0]);
        if (user == null) {
            throw new InvalidDataException("RETRIEVE_PASSWORD.TIP.USERID_EMAIL_DOESNOT_MATACH");
        }
        String subject = I18nHelper.getMessage("RETRIEVE_PASSWORD_EMAIL.SUBJECT", new String[0]);
        String content = this.createEmailLink(user);
        try {
            if ("exchange".equals(ApplicationContextHelper.getEnvironment().getProperty(MAIL_PROTOCOL_TYPE))) {
                ExchangeMailHelper.sendHtmlMail(subject, content, Arrays.asList(email), new File[0]);
            } else {
                MailHelper.sendHtmlMail(subject, content, Arrays.asList(email), new File[0]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new InvalidDataException("RETRIEVE_PASSWORD.TIP.SEND_EMAIL_FAILED");
        }
    }

    @Override
    public void validUrl(String userId, String key) {
        if (!key.equals(RedisHelper.get(RESET_PASSWORD_EXPIRED_REDIS_NAME, userId))) {
            throw new InvalidDataException("RETRIEVE_PASSWORD.TIP.USERID_EMAIL_DOESNOT_MATACH");
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void resetPassword(RestJsonWrapperBean wrapper) {
        CoreUserBean user = wrapper.parseUnique(CoreUserBean.class);
        this.userService.changePassword(user.getId(), user.getPassword());
        RedisHelper.evict(RESET_PASSWORD_EXPIRED_REDIS_NAME, user.getId());
    }

    @Override
    public String validUserAndEmail(String userId, String email) {
        CoreUserBean selectUser = new CoreUserBean();
        selectUser.setId(userId);
        selectUser.setEmail(email);
        CoreUserBean user = this.userService.getDao().selectOneIfPresent((CoreUserBean)selectUser, new String[0]);
        if (user != null && email.equals(user.getEmail())) {
            return "1";
        }
        return "0";
    }

    @Override
    public CorePasswordPolicyBean selectPasswordPolicyByUserId(String userId) {
        return this.passwordPolicyService.selectMatchingPasswordPolicy(userId);
    }

    @Override
    public String validateUserPassword(String userId, RestJsonWrapperBean wrapper) {
        LocalContextHelper.isUserLogin();
        CoreUserBean user = new CoreUserBean();
        user.setId(userId);
        user.setPassword(this.passwordEncoder.encode(AuthenticationHelper.getRawPassword(wrapper.getParamValue("password"))));
        List<CoreUserBean> userList = this.userService.getDao().selectList((CoreUserBean)user, new Order[0]);
        if (userList.isEmpty()) {
            return "0";
        }
        return "1";
    }

    @Override
    public RestHttpTextEntity sendSmsVerification(String userId, String mobile) {
        CoreUserBean selectUser = new CoreUserBean();
        selectUser.setId(userId);
        selectUser.setMobile(mobile);
        CoreUserBean user = this.userService.getDao().selectOneIfPresent((CoreUserBean)selectUser, new String[0]);
        if (user == null) {
            return new RestHttpTextEntity(HttpStatus.BAD_REQUEST, "RESET_PASSWORD.WARNING.TIP.INVALID_USERID_OR_MOBILE");
        }
        String verificationCode = this.createSmsVerificationCode();
        ResultEntity resultEntity = this.smsService.send("RESET_PASSWORD", user.getUserName(), mobile, I18nHelper.getMessage("RESET_PASSWORD.SMS.MESSAGE_TEMPLATE", verificationCode));
        if (resultEntity.isSucess()) {
            RedisHelper.put(RESET_PASSWORD_EXPIRED_REDIS_NAME, userId, verificationCode, 600);
            return new RestHttpTextEntity();
        }
        return new RestHttpTextEntity(HttpStatus.BAD_REQUEST, resultEntity.getMessage());
    }

    @Override
    public boolean checkSmsVerificationCode(String userId, RestJsonWrapperBean wrapper) {
        String verificationCode = wrapper.getParamValue("verificationCode");
        if (StringUtils.isBlank(verificationCode)) {
            return false;
        }
        String cachedVerificationCode = (String)RedisHelper.get(RESET_PASSWORD_EXPIRED_REDIS_NAME, userId);
        if (cachedVerificationCode == null) {
            return false;
        }
        return verificationCode.equals(cachedVerificationCode);
    }

    @Override
    public RestHttpTextEntity resetPasswordBySms(String userId, RestJsonWrapperBean wrapper) {
        String verificationCode = wrapper.getParamValue("verificationCode");
        if (StringUtils.isBlank(verificationCode)) {
            return new RestHttpTextEntity(HttpStatus.BAD_REQUEST, "RESET_PASSWORD.WARNING.TIP.INVALID_VERIFICATION_CODE");
        }
        String cachedVerificationCode = (String)RedisHelper.get(RESET_PASSWORD_EXPIRED_REDIS_NAME, userId);
        if (cachedVerificationCode == null) {
            return new RestHttpTextEntity(HttpStatus.BAD_REQUEST, "RESET_PASSWORD.WARNING.TIP.VERIFICATION_CODE_EXPIRED");
        }
        if (!cachedVerificationCode.equals(verificationCode)) {
            return new RestHttpTextEntity(HttpStatus.BAD_REQUEST, "RESET_PASSWORD.WARNING.TIP.INVALID_VERIFICATION_CODE");
        }
        RestHttpTextEntity textEntity = this.userService.changePassword(userId, wrapper.getParamValue("password"));
        if (HttpStatus.OK.equals((Object)textEntity.getStatusCode())) {
            RedisHelper.evict(RESET_PASSWORD_EXPIRED_REDIS_NAME, userId);
        }
        return textEntity;
    }

    private String createEmailLink(CoreUserBean user) {
        String secretKey = UUID.randomUUID().toString();
        RedisHelper.put(RESET_PASSWORD_EXPIRED_REDIS_NAME, user.getId(), secretKey, 600);
        String url = ApplicationContextHelper.getEnvironment().getProperty("sunway.app.url");
        String contextPath = ApplicationContextHelper.getEnvironment().getProperty("server.servlet.context-path");
        StringBuilder sb = new StringBuilder(url).append(StringUtils.isEmpty(contextPath) ? "" : contextPath).append("?userId=").append(user.getId()).append("&secretKey=").append(secretKey).append("#resetPassword");
        return I18nHelper.getMessage("RETRIEVE_PASSWORD_EMAIL.CONTENT", sb.toString(), sb.toString());
    }

    private String createSmsVerificationCode() {
        int random = Double.valueOf(Math.random() * 1000000.0).intValue();
        return StringUtils.leftPad("" + random, 6);
    }
}
