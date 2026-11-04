/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.audit.aspect;

import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Stack;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile(value={"audit"})
public class AuditLogAspect {
    public static final String REQUEST_HEADER_COMMENT_KEY = "auditTrailComment";
    public static final String REQUEST_HEADER_SIGNATURE_KEY = "auditSignature";
    public static final String REQUEST_HEADER_AUDIT_ACTIVITY_KEY = "auditActivity";
    public static final String REQUEST_HEADER_AUDIT_WITNESS_ID_KEY = "witness";
    public static final String REQUEST_HEADER_AUDIT_WITNESS_NAME_KEY = "witnessName";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="@annotation(audit)")
    public Object auditService(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
        boolean init;
        boolean bl = init = SunwayAopContext.getCache("AUDIT_KEY") == null;
        if (init) {
            SunwayAopContext.putCache("AUDIT_KEY", "1");
            SunwayAopContext.putCache("AUDIT_LOG_LIST_KEY", new ArrayList());
        }
        try {
            HttpServletRequest request = ServletUtils.getCurrentRequest();
            if (request != null) {
                if (SunwayAopContext.getCache("AUDIT_COMMENT_KEY") == null) {
                    if (StringUtils.isEmpty(request.getHeader(REQUEST_HEADER_COMMENT_KEY))) {
                        SunwayAopContext.putCache("AUDIT_COMMENT_KEY", request.getHeader(REQUEST_HEADER_COMMENT_KEY));
                    } else {
                        SunwayAopContext.putCache("AUDIT_COMMENT_KEY", URLDecoder.decode(request.getHeader(REQUEST_HEADER_COMMENT_KEY), "utf-8"));
                    }
                }
                if (SunwayAopContext.getCache("AUDIT_SIGNATURE_KEY") == null) {
                    if (StringUtils.isEmpty(request.getHeader(REQUEST_HEADER_SIGNATURE_KEY))) {
                        SunwayAopContext.putCache("AUDIT_SIGNATURE_KEY", request.getHeader(REQUEST_HEADER_SIGNATURE_KEY));
                    } else {
                        SunwayAopContext.putCache("AUDIT_SIGNATURE_KEY", URLDecoder.decode(request.getHeader(REQUEST_HEADER_SIGNATURE_KEY), "utf-8"));
                    }
                }
                if (SunwayAopContext.getCache("AUDIT_WITNESS_ID_KEY") == null) {
                    if (StringUtils.isEmpty(request.getHeader(REQUEST_HEADER_AUDIT_WITNESS_ID_KEY))) {
                        SunwayAopContext.putCache("AUDIT_WITNESS_ID_KEY", request.getHeader(REQUEST_HEADER_AUDIT_WITNESS_ID_KEY));
                        SunwayAopContext.putCache("AUDIT_WITNESS_NAME_KEY", request.getHeader(REQUEST_HEADER_AUDIT_WITNESS_NAME_KEY));
                    } else {
                        SunwayAopContext.putCache("AUDIT_WITNESS_ID_KEY", URLDecoder.decode(request.getHeader(REQUEST_HEADER_AUDIT_WITNESS_ID_KEY), "utf-8"));
                        SunwayAopContext.putCache("AUDIT_WITNESS_NAME_KEY", URLDecoder.decode(request.getHeader(REQUEST_HEADER_AUDIT_WITNESS_NAME_KEY), "utf-8"));
                    }
                }
            }
            if (SunwayAopContext.getCache("AUDIT_ACTIVITY_STACK_KEY") == null) {
                SunwayAopContext.putCache("AUDIT_ACTIVITY_STACK_KEY", new Stack());
            }
            if (request == null || StringUtils.isEmpty(request.getHeader(REQUEST_HEADER_AUDIT_ACTIVITY_KEY))) {
                SunwayAopContext.putCache("AUDIT_ACTIVITY_KEY", audit.value());
            } else {
                SunwayAopContext.putCache("AUDIT_ACTIVITY_KEY", URLDecoder.decode(request.getHeader(REQUEST_HEADER_AUDIT_ACTIVITY_KEY), "utf-8"));
            }
            Stack stack = (Stack)SunwayAopContext.getCache("AUDIT_ACTIVITY_STACK_KEY");
            stack.push(SunwayAopContext.getCache("AUDIT_ACTIVITY_KEY"));
            Object object = pjp.proceed();
            return object;
        }
        finally {
            if (init) {
                AuditLogHelper.flush();
                SunwayAopContext.removeCache("AUDIT_KEY");
                SunwayAopContext.removeCache("AUDIT_LOG_LIST_KEY");
                SunwayAopContext.removeCache("AUDIT_ACTIVITY_KEY");
            } else {
                Stack stack = (Stack)SunwayAopContext.getCache("AUDIT_ACTIVITY_STACK_KEY");
                if (stack != null) {
                    stack.pop();
                    if (stack.size() > 0) {
                        SunwayAopContext.putCache("AUDIT_ACTIVITY_KEY", stack.peek());
                    } else {
                        SunwayAopContext.removeCache("AUDIT_ACTIVITY_KEY");
                    }
                }
            }
        }
    }
}
