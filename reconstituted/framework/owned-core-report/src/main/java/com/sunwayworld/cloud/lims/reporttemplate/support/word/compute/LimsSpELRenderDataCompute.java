/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.compute;

import com.deepoove.poi.render.compute.EnvModel;
import com.deepoove.poi.render.compute.ReadMapAccessor;
import com.deepoove.poi.render.compute.RenderDataCompute;
import com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard.LimsSpelExpressionParser;
import com.sunwayworld.framework.utils.StringUtils;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class LimsSpELRenderDataCompute
implements RenderDataCompute {
    private static final Logger LOGGER = LoggerFactory.getLogger(LimsSpELRenderDataCompute.class);
    private final ExpressionParser parser;
    private final EvaluationContext context;
    private EvaluationContext envContext;
    private boolean isStrict;

    public LimsSpELRenderDataCompute(EnvModel model) {
        this(model, true);
    }

    public LimsSpELRenderDataCompute(EnvModel model, boolean isStrict) {
        this(model, isStrict, Collections.emptyMap());
    }

    public LimsSpELRenderDataCompute(EnvModel model, boolean isStrict, Map<String, Method> spELFunction) {
        this.isStrict = isStrict;
        this.parser = new LimsSpelExpressionParser();
        if (null != model.getEnv() && !model.getEnv().isEmpty()) {
            this.envContext = new StandardEvaluationContext(model.getEnv());
            ((StandardEvaluationContext)this.envContext).addPropertyAccessor(new ReadMapAccessor());
        }
        this.context = new StandardEvaluationContext(model.getRoot());
        ((StandardEvaluationContext)this.context).addPropertyAccessor(new ReadMapAccessor());
        spELFunction.forEach(((StandardEvaluationContext)this.context)::registerFunction);
    }

    @Override
    public Object compute(String el) {
        if (StringUtils.isEmpty(el)) {
            return el;
        }
        try {
            Object value = this.parser.parseExpression(el).getValue(this.context);
            if (value == null && null != this.envContext && !el.contains("#this")) {
                try {
                    Object val = this.parser.parseExpression(el).getValue(this.envContext);
                    if (null != val) {
                        return val;
                    }
                }
                catch (Exception val) {
                    // empty catch block
                }
            }
            return value;
        }
        catch (SpelEvaluationException e) {
            if (null != this.envContext && !el.contains("#this")) {
                try {
                    Object val = this.parser.parseExpression(el).getValue(this.envContext);
                    if (null != val) {
                        return val;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (this.isStrict) {
                LOGGER.error("EXECUTE SPEL ERROR :{}", (Object)el, (Object)e);
                throw e;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error("EXECUTE SPEL ERROR :{}", (Object)el);
            throw e;
        }
    }
}
