/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.gikam.error.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.gikam.error.controller.GikamErrorController;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@GikamBean
@Controller
public class GikamErrorControllerImpl
implements GikamErrorController {
    private static final Logger logger = LogManager.getLogger(GikamErrorController.class);

    @Override
    public String thymeleafPage() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
            }
        }
        if (request != null) {
            Object uri = request.getAttribute("javax.servlet.error.request_uri");
            Object code = request.getAttribute("javax.servlet.error.status_code");
            StringBuilder sb = new StringBuilder("Request Uri:").append(uri).append(" Status Code:").append(code);
            logger.warn(sb.toString());
        }
        return "error/thymeleaf";
    }
}
