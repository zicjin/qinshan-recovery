/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.permission.aspect;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.permission.PermissionValidator;
import com.sunwayworld.framework.validator.permission.annotation.ValidatePermissionWith;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value=100)
public class PermissionValidatorAspect {
    private ExpressionParser parser = new SpelExpressionParser();

    @Around(value="@annotation(validatePermissionWith)")
    public Object doValidate(ProceedingJoinPoint pjp, ValidatePermissionWith validatePermissionWith) throws Throwable {
        Object[] args = pjp.getArgs();
        PermissionValidator validator = ApplicationContextHelper.getBean(validatePermissionWith.value());
        String table = null;
        if (StringUtils.contains(validatePermissionWith.table(), ".") || StringUtils.contains(validatePermissionWith.table(), "#")) {
            StandardEvaluationContext context = new StandardEvaluationContext(pjp.getTarget());
            table = (String)this.parser.parseExpression(validatePermissionWith.table()).getValue(context);
        } else {
            table = validatePermissionWith.table();
        }
        String category = validatePermissionWith.category();
        Object arg = args[validatePermissionWith.pos()];
        if (arg != null) {
            if (RestJsonWrapperBean.class.equals(arg.getClass())) {
                List<String> idList = ((RestJsonWrapperBean)arg).parseId(String.class);
                for (String id : idList) {
                    if (validator.isValid(table, category, id)) continue;
                    return null;
                }
            } else if (!validator.isValid(table, category, arg)) {
                return null;
            }
        }
        return pjp.proceed();
    }
}
