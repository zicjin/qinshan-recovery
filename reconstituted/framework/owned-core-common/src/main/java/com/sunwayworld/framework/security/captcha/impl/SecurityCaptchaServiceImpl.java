/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.captcha.impl;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.security.captcha.SecurityCaptchaService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.config.helper.SystemConfigHelper;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class SecurityCaptchaServiceImpl
implements SecurityCaptchaService {
    public static final String CAPTCHA_COOKIE_KEY = "captchaKey";
    public static final String CAPTCHA_COOKIE_CACHE_KEY = "captchaCacheKey";
    public static final String CAPTCHA_PARAM_NAME = "captcha";
    public static final String CAPTCHA_RELOGIN_HEADER = "relogin";
    public static final String CAPTCHA_REQUIRE = "captchaRequire";
    private volatile Producer producer;

    @Override
    public final void captchaImage() {
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        response.setDateHeader("Expires", 0L);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            String captchaCode = this.outputAndReturnCode(SecurityCaptchaServiceImpl.getServletOutputStreamProxy(os));
            String captchaCookieKey = StringUtils.randomUUID();
            RedisHelper.put(CAPTCHA_COOKIE_CACHE_KEY, captchaCookieKey, captchaCode, 300);
            ServletUtils.addCookie(ServletUtils.getCurrentRequest(), response, CAPTCHA_COOKIE_KEY, captchaCookieKey, -1);
        }
        catch (IOException ioe) {
            throw new NetworkException(ioe);
        }
        finally {
            if (os != null) {
                try {
                    os.flush();
                }
                catch (Exception exception) {}
                try {
                    os.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    protected String outputAndReturnCode(ServletOutputStream os) {
        String captcha = this.getCaptchaProducer().createText().toUpperCase();
        BufferedImage challenge = this.getCaptchaProducer().createImage(captcha);
        try {
            ImageIO.write((RenderedImage)challenge, "jpeg", os);
        }
        catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
        return captcha;
    }

    @Override
    public final void checkCaptcha() {
        if (!"1".equals(SystemConfigHelper.getLoginConfig().getCaptcha())) {
            return;
        }
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) {
            throw new AuthenticationServiceException("GIKAM.SECURITY.CAPTCHA.CAPTCHA_NOT_PRESENT");
        }
        if ("0".equals(request.getParameter(CAPTCHA_REQUIRE))) {
            return;
        }
        if ("1".equals(request.getHeader(CAPTCHA_RELOGIN_HEADER))) {
            return;
        }
        Cookie captchaCookie = ServletUtils.getCookie(request, CAPTCHA_COOKIE_KEY);
        if (captchaCookie == null) {
            throw new AuthenticationServiceException("GIKAM.SECURITY.CAPTCHA.COOKIE_NOT_PRESENT");
        }
        String captchaCookieKey = captchaCookie.getValue();
        String captcha = (String)RedisHelper.get(CAPTCHA_COOKIE_CACHE_KEY, captchaCookieKey);
        String userCaptcha = request.getParameter(CAPTCHA_PARAM_NAME);
        if (StringUtils.isBlank(userCaptcha) || StringUtils.isEmpty(captcha)) {
            throw new AuthenticationServiceException("GIKAM.SECURITY.CAPTCHA.CAPTCHA_NOT_PRESENT");
        }
        RedisHelper.evict(CAPTCHA_COOKIE_CACHE_KEY, captchaCookieKey);
        if (!StringUtils.equals(userCaptcha, captcha)) {
            throw new AuthenticationServiceException("GIKAM.SECURITY.CAPTCHA.INCORRECT_CAPTCHA");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Producer getCaptchaProducer() {
        if (this.producer != null) return this.producer;
        Class<SecurityCaptchaServiceImpl> clazz = SecurityCaptchaServiceImpl.class;
        synchronized (SecurityCaptchaServiceImpl.class) {
            if (this.producer != null) return this.producer;
            this.producer = new DefaultKaptcha();
            Properties properties = new Properties();
            properties.setProperty("kaptcha.border", "yes");
            properties.setProperty("kaptcha.border.color", "105,179,90");
            properties.setProperty("kaptcha.textproducer.font.color", "201,94,49");
            properties.setProperty("kaptcha.image.width", "100");
            properties.setProperty("kaptcha.image.height", "40");
            properties.setProperty("kaptcha.textproducer.char.string", "023456789");
            properties.setProperty("kaptcha.textproducer.font.size", "25");
            properties.setProperty("kaptcha.textproducer.char.length", "4");
            properties.setProperty("kaptcha.textproducer.char.space", "6");
            properties.setProperty("kaptcha.textproducer.font.names", "\u5b8b\u4f53,\u6977\u4f53,\u5fae\u8f6f\u96c5\u9ed1");
            properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
            properties.setProperty("kaptcha.background.impl", "com.google.code.kaptcha.impl.DefaultBackground");
            properties.setProperty("kaptcha.background.clear.from", "247,254,236");
            properties.setProperty("kaptcha.background.clear.to", "247,254,236");
            properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
            Config config = new Config(properties);
            ((DefaultKaptcha)this.producer).setConfig(config);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.producer;
        }
    }

    private static final ServletOutputStream getServletOutputStreamProxy(ServletOutputStream target) {
        ServletOutputStreamMethodInterceptor interceptor = new ServletOutputStreamMethodInterceptor(target);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ServletOutputStream.class);
        enhancer.setCallback(interceptor);
        return (ServletOutputStream)enhancer.create();
    }

    private static class ServletOutputStreamMethodInterceptor
    implements MethodInterceptor {
        private ServletOutputStream target;

        private ServletOutputStreamMethodInterceptor(ServletOutputStream target) {
            this.target = target;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if ("flush".equals(method.getName()) || "close".equals(method.getName())) {
                return null;
            }
            return method.invoke(this.target, args);
        }
    }
}
