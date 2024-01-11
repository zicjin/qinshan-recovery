package com.sunwayworld.framework.security.firewall;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.security.exception.FirewallDeniedException;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;

@WebFilter
@Component
public class SecurityFirewallFilter implements Filter {
    @Value("#{'${sunway.security.access-host-list}'.split(',')}")
    private List<String> accessHostList;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
//        if (LocalContextHelper.isUserLogin()) {
//            String userId = ((HttpServletRequest) request).getHeader("user");
//
//            if (!StringUtils.isEmpty(userId) && !userId.equals(EncryptUtils.MD5Encrypt(LocalContextHelper.getLoginUserId()))) {
//                ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, "GIKAM.SECURITY.LOGIN.USER_INVALID"));
//                throw new CheckedException("GIKAM.SECURITY.LOGIN.USER_INVALID");
//            }
//
//        }
//

        if (!isAccessHostListEmpty()) {
            // host header attack
            hostHeaderFirewall(request);

            // csrf attack
            csrfFirewall(request);
        }

        chain.doFilter(request, response);
    }

    // --------------------------------------------------------------------------
    // 私有方法
    // --------------------------------------------------------------------------
    private void hostHeaderFirewall(ServletRequest request) {
        String host = ((HttpServletRequest) request).getHeader("host");

        if (!StringUtils.isBlank(host)) {
            if (!isAccessableHost(host)) {
                throw new FirewallDeniedException();
            }
        }
    }

    private void csrfFirewall(ServletRequest request) {
        String referer = ((HttpServletRequest) request).getHeader("referer");

        if (!StringUtils.isBlank(referer)) {
            if (!isAcccessableReferer(referer)) {
                throw new FirewallDeniedException();
            }
        }
    }

    private boolean isAccessHostListEmpty() {
        if (accessHostList == null || accessHostList.isEmpty()) {
            return true;
        }

        return (accessHostList.size() == 1 && StringUtils.isBlank(accessHostList.get(0)));
    }

    private boolean isAccessableHost(String host) {
        for (String accessHost : accessHostList) {
            if (accessHost.equals(host)) {
                return true;
            }

            if (StringUtils.endsWith(accessHost, ":80")) {
                if (StringUtils.removeEnd(accessHost, ":80").equals(host)) {
                    return true;
                }
            }
            if (StringUtils.endsWith(accessHost, ":443")) {
                if (StringUtils.removeEnd(accessHost, ":443").equals(host)) {
                    return true;
                }
            }

            if (StringUtils.endsWith(host, ":80")) {
                if (accessHost.equals(StringUtils.removeEnd(host, ":80"))) {
                    return true;
                }
            }
            if (StringUtils.endsWith(host, ":443")) {
                if (accessHost.equals(StringUtils.removeEnd(host, ":443"))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isAcccessableReferer(String referer) {
        for (String accessHost : accessHostList) {
            if (StringUtils.startsWithIgnoreCase(referer, "http://" + accessHost + "/")
                    || StringUtils.startsWithIgnoreCase(referer, "https://" + accessHost + "/")) {
                return true;
            }
            //默认端口为80，就去掉端口（含端口）
            if (StringUtils.endsWith(accessHost, ":80")) {
                String noPortHost = StringUtils.removeEnd(accessHost, ":80");

                if (StringUtils.startsWithIgnoreCase(referer, "http://" + noPortHost + "/")
                        || StringUtils.startsWithIgnoreCase(referer, "https://" + noPortHost + "/")) {
                    return true;
                }
            }
            //当使用https时默认端口为443
            if (StringUtils.endsWith(accessHost, ":443")) {
                if (StringUtils.startsWithIgnoreCase(referer, "http://" + accessHost + "/")
                        || StringUtils.startsWithIgnoreCase(referer, "https://" + accessHost + "/")) {
                    return true;
                }
            }
            //无端口只有ip时
            if (!StringUtils.contains(accessHost, ":")) {
                if (!StringUtils.contains(accessHost, "http")) {
                    if (StringUtils.startsWithIgnoreCase(referer, "http://" + accessHost + ":80/")) {
                        return true;
                    }
                } else if (!StringUtils.contains(accessHost, "https")) {
                    if (StringUtils.startsWithIgnoreCase(referer, "https://" + accessHost + ":443/")) {
                        return true;
                    }
                }

            }
        }

        return false;
    }
}
