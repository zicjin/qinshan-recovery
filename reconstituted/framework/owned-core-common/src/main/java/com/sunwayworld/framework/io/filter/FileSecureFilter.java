/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.filter;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@WebFilter
@Component
@Order(value=2)
public class FileSecureFilter
implements Filter {
    private static String DOWNLOAD_URL_PREFIX = "/" + "files" + "/";
    @Autowired
    private CoreFileManager fileManager;
    @Value(value="${sunway.security.file-access-permission.enabled:false}")
    private boolean accessPermissionEnabled;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestUri;
        HttpServletRequest hsr;
        String method;
        if (this.accessPermissionEnabled && "GET".equals(method = (hsr = (HttpServletRequest)request).getMethod()) && StringUtils.startsWith(requestUri = ServletUtils.getRequestUri(hsr), DOWNLOAD_URL_PREFIX)) {
            String security = hsr.getParameter("security");
            String token = hsr.getParameter("token");
            if (StringUtils.isBlank(security)) {
                throw new ApplicationRuntimeException("You are not permitted to download file.", new String[0]);
            }
            FilePathDTO filePath = FilePathManager.toFilePath(requestUri);
            if (filePath != null) {
                if (StringUtils.isBlank(token)) {
                    if (!Objects.equals(security, this.fileManager.getSecurity(filePath))) {
                        throw new ApplicationRuntimeException("You are not permitted to download file.", new String[0]);
                    }
                } else {
                    Authentication authentication = AuthenticationHelper.getCacheAuthentication(token);
                    LoginUser loginUser = (LoginUser)authentication.getPrincipal();
                    try {
                        LocalContextHelper.setUserLogin(loginUser);
                        if (!Objects.equals(security, this.fileManager.getSecurity(filePath))) {
                            throw new ApplicationRuntimeException("You are not permitted to download file.", new String[0]);
                        }
                    }
                    finally {
                        LocalContextHelper.removeUserLogin();
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
