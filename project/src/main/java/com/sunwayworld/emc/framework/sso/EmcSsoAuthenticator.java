package com.sunwayworld.emc.framework.sso;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.OkHttpClientUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;

/**
 * 千帆玉符单点登录
 * @author
 */
public class EmcSsoAuthenticator implements SsoAuthenticator {

    private static final Logger logger = LogManager.getLogger(EmcSsoAuthenticator.class);

    @Override
    public LoginUser authenticate() {
        String rememberMeCookieToken = AuthenticationHelper.getLoginUserToken();
        if (Objects.nonNull(rememberMeCookieToken)) {
            return null;
        }

        String clientId = ApplicationContextHelper.getConstantValue("WP_SSO_CLIENT_ID");
        String clientKey = ApplicationContextHelper.getConstantValue("WP_SSO_CLIENT_SECRET");
        String authorizationEndpoint = ApplicationContextHelper.getConstantValue("WP_SSO_AUTHORIZATION_ENDPOINT");
        String tokenEndpoint = ApplicationContextHelper.getConstantValue("WP_SSO_TOKEN_ENDPOINT");
        String userInfoEndpoint = ApplicationContextHelper.getConstantValue("WP_SSO_USERINFO_ENDPOINT");
        String redirectUri = ApplicationContextHelper.getConstantValue("WP_SSO_LIMS_REDIRECT_URI");

        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientKey)
                || StringUtils.isEmpty(authorizationEndpoint) || StringUtils.isEmpty(tokenEndpoint) || StringUtils.isEmpty(userInfoEndpoint)
                || StringUtils.isEmpty(redirectUri)) {
            logger.error("系统参数单点登录配置信息缺失");
            return null;
        }

        // 如果没有携带code值直接访问单点登录地址时，重定向到千帆玉符登录页面
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String code = request.getParameter("code");
        if (StringUtils.isEmpty(code)) {
            String ssoRedirectUri = buildSsoRedirectUri(authorizationEndpoint, clientId, redirectUri);
            if (StringUtils.isEmpty(ssoRedirectUri)) {
                logger.error("构建单点登录重定向地址失败");
                return null;
            }

            try {
                ServletUtils.getCurrentResponse().sendRedirect(ssoRedirectUri);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("重定向千帆玉符登录页面失败");
            }
            return null;
        }

        // 通过code去获取access token
        IdaasAccessTokenResponse accessTokenResponse = fetchAccessToken(tokenEndpoint, clientId, clientKey, code, redirectUri);
        if (Objects.isNull(accessTokenResponse)) {
            logger.error("获取access token 失败");
            return null;
        }

        // 通过 access token 获取用户信息
        IdaasUserInfoResponse userInfoResponse = fetchUserInfo(userInfoEndpoint, accessTokenResponse.getAccessToken());
        if (Objects.isNull(userInfoResponse)) {
            logger.error("获取用户信息失败");
            return null;
        }

        return queryUser(userInfoResponse);
    }

    /**
     * 查询用户信息，如果不存在则新增
     * @param userInfoResponse
     * @return
     */
    private LoginUser queryUser(IdaasUserInfoResponse userInfoResponse) {
        CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
        String userId = userInfoResponse.getSub();
        List<CoreUserBean> existedUserList = userService.getDao().selectListByOneColumnValue(userId, "userNo");
        if (!existedUserList.isEmpty()) {
            return new LoginUser(existedUserList.get(0).getId(), existedUserList.get(0).getPassword());
        }

        CoreUserBean user = new CoreUserBean();
        user.setId(userId);
        user.setUserName(userInfoResponse.getName());
        user.setEmail(userInfoResponse.getEmail());
        user.setMobile(userInfoResponse.getPhoneNumber());
        userService.getDao().insert(user);
        userService.resetPassword(userId);

        user = userService.selectByLoginId(userId);

        return new LoginUser(userId, user.getPassword());
    }

    /**
     * 构建千帆玉符重定向地址
     * @param authorizeUri 认证接口
     * @param clientId 应用标识
     * @param redirectUri lims的回调地址
     * @return
     */
    private String buildSsoRedirectUri(String authorizeUri, String clientId, String redirectUri) {
        try {
            String encodeRedirectUri = URLEncoder.encode(redirectUri, "UTF-8");

            return authorizeUri + "?response_type=code&client_id=" + clientId + "&scope=openid%20offline_access" + "&redirect_uri=" + encodeRedirectUri;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("URL Encode 出错 -> {}", redirectUri);
        }

        return null;
    }

    /**
     * 获取 access token
     * @param accessTokenUri 请求地址
     * @param clientId 应用标识
     * @param clientKey 应用key
     * @param code 授权码
     * @return
     */
    private IdaasAccessTokenResponse fetchAccessToken(String accessTokenUri, String clientId, String clientKey, String code, String redirectUri) {
        String clientBase64Value = Base64.getEncoder().encodeToString(String.join(":", clientId, clientKey).getBytes());
        logger.info("client base64 value : {}", clientBase64Value);

        FormBody.Builder builder = new FormBody.Builder();
        FormBody formBody = builder.add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .build();
        Request.Builder requestBuilder = new Request.Builder().url(accessTokenUri).post(formBody);
        requestBuilder.header("Authorization", "Basic " + clientBase64Value);
        requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");

        Response response = OkHttpClientUtils.execute(requestBuilder.build());
        if (Objects.nonNull(response.body())) {
            try {
                String bodyResult = response.body().string();
                logger.info("获取Access Token 返回结果 -> {}", bodyResult);

                if (response.isSuccessful()) {
                    return JSON.parseObject(bodyResult, IdaasAccessTokenResponse.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("获取Access Token 失败 -> {}", e.getMessage());
            }
        }

        return null;
    }

    /**
     * 获取用户信息
     * @param userInfoUri 请求地址
     * @param accessToken 访问TOKEN
     * @return
     */
    private IdaasUserInfoResponse fetchUserInfo(String userInfoUri, String accessToken) {
        logger.info("请求用户信息 -> {}", userInfoUri);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.header("Authorization", "Bearer " + accessToken);
        requestBuilder.header("Content-Type", "application/json");

        Request request = requestBuilder.url(userInfoUri).get().build();
        Response response = OkHttpClientUtils.execute(request);

        if (Objects.nonNull(response.body())) {
            try {
                String bodyResult = response.body().string();
                logger.info("获取用户信息返回结果 -> {}", bodyResult);

                if (response.isSuccessful()) {
                    return JSON.parseObject(bodyResult, IdaasUserInfoResponse.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("获取用户信息失败 -> {}", e.getMessage());
            }
        }

        return null;
    }
}
