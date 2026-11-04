/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.firewall;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.security.exception.FirewallDeniedException;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@WebFilter
@Component
public class SecurityFirewallFilter
implements Filter {
    private static final Logger logger = LogManager.getLogger(SecurityFirewallFilter.class);
    @Value(value="#{'${sunway.security.access-host-list}'.split(',')}")
    private List<String> accessHostList;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String userId;
        if (LocalContextHelper.isUserLogin() && !StringUtils.isEmpty(userId = ((HttpServletRequest)request).getHeader("user")) && !userId.equals(EncryptUtils.MD5Encrypt(LocalContextHelper.getLoginUserId()))) {
            logger.error("User Invalid: userId MD5\uff1a" + userId + "-- loginUserId: " + LocalContextHelper.getLoginUserId());
            ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, "GIKAM.SECURITY.LOGIN.USER_INVALID"));
            throw new CheckedException("GIKAM.SECURITY.LOGIN.USER_INVALID");
        }
        if (!this.isAccessHostListEmpty()) {
            this.hostHeaderFirewall(request);
            this.csrfFirewall(request);
        }
        chain.doFilter(request, response);
    }

    private void hostHeaderFirewall(ServletRequest request) {
        String host = ((HttpServletRequest)request).getHeader("host");
        if (!StringUtils.isBlank(host) && !this.isAccessableHost(host)) {
            logger.error("SecurityFirewallFilter #doFilter #hostHeaderFirewall, deny host: " + host);
            throw new FirewallDeniedException();
        }
    }

    private void csrfFirewall(ServletRequest request) {
        String referer = ((HttpServletRequest)request).getHeader("referer");
        if (!StringUtils.isBlank(referer) && !this.isAcccessableReferer(referer)) {
            logger.error("SecurityFirewallFilter #doFilter #csrfFirewall, deny referer: " + referer);
            throw new FirewallDeniedException();
        }
    }

    private boolean isAccessHostListEmpty() {
        if (this.accessHostList == null || this.accessHostList.isEmpty()) {
            return true;
        }
        return this.accessHostList.size() == 1 && StringUtils.isBlank(this.accessHostList.get(0));
    }

    private boolean isAccessableHost(String host) {
        for (String accessHost : this.accessHostList) {
            if (accessHost.equals(host)) {
                return true;
            }
            if (StringUtils.endsWith(accessHost, ":80") && StringUtils.removeEnd(accessHost, ":80").equals(host)) {
                return true;
            }
            if (StringUtils.endsWith(accessHost, ":443") && StringUtils.removeEnd(accessHost, ":443").equals(host)) {
                return true;
            }
            if (StringUtils.endsWith(host, ":80") && accessHost.equals(StringUtils.removeEnd(host, ":80"))) {
                return true;
            }
            if (!StringUtils.endsWith(host, ":443") || !accessHost.equals(StringUtils.removeEnd(host, ":443"))) continue;
            return true;
        }
        return false;
    }

    private boolean isAcccessableReferer(String referer) {
        for (String accessHost : this.accessHostList) {
            if (StringUtils.startsWithIgnoreCase(referer, "http://" + accessHost + "/") || StringUtils.startsWithIgnoreCase(referer, "https://" + accessHost + "/")) {
                return true;
            }
            if (StringUtils.endsWith(accessHost, ":80")) {
                String noPortHost = StringUtils.removeEnd(accessHost, ":80");
                if (StringUtils.startsWithIgnoreCase(referer, "http://" + noPortHost + "/") || StringUtils.startsWithIgnoreCase(referer, "https://" + noPortHost + "/")) {
                    return true;
                }
            }
            if (StringUtils.endsWith(accessHost, ":443") && (StringUtils.startsWithIgnoreCase(referer, "http://" + accessHost + "/") || StringUtils.startsWithIgnoreCase(referer, "https://" + accessHost + "/"))) {
                return true;
            }
            if (StringUtils.contains(accessHost, ":") || !(!StringUtils.contains(accessHost, "http") ? StringUtils.startsWithIgnoreCase(referer, "http://" + accessHost + ":80/") : !StringUtils.contains(accessHost, "https") && StringUtils.startsWithIgnoreCase(referer, "https://" + accessHost + ":443/"))) continue;
            return true;
        }
        return false;
    }
}
