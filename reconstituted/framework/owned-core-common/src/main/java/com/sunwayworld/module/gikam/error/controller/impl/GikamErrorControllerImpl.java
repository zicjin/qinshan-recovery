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

@GikamBean
@Controller
public class GikamErrorControllerImpl
implements GikamErrorController {
    private static final Logger logger = LogManager.getLogger(GikamErrorController.class);

    @Override
    public String thymeleafPage() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        StringBuilder sb = new StringBuilder("Request Uri:").append(request.getAttribute("javax.servlet.error.request_uri")).append(" Status Code:").append(request.getAttribute("javax.servlet.error.status_code"));
        logger.warn(sb.toString());
        return "error/thymeleaf";
    }
}
