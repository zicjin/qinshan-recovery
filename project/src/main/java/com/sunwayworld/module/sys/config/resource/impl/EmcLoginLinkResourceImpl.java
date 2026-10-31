package com.sunwayworld.module.sys.config.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.config.bean.CoreLoginLinkBean;
import com.sunwayworld.module.sys.config.resource.impl.CoreLoginLinkResourceImpl;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class EmcLoginLinkResourceImpl
extends CoreLoginLinkResourceImpl {
    @Override
    public Page<CoreLoginLinkBean> selectPagination(RestJsonWrapperBean wrapper) {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        Cookie[] cookies = request.getCookies();
        String requestCookie = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("sunway-remember-me-cookie".equals(cookie.getName())) {
                    requestCookie = cookie.getValue();
                }
            }
        }
        String loginUserToken = AuthenticationHelper.getLoginUserToken();
        if (StringUtils.isBlank(requestCookie) || StringUtils.isBlank(loginUserToken) || !StringUtils.equals(requestCookie, loginUserToken)) {
            throw new AuthenticationServiceException("core.security.login.unauthorized");
        }
        return this.getService().selectPagination(wrapper);
    }
}
