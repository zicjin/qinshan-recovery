/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard;

import com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard.LimsInternalSpelExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;

public class LimsSpelExpressionParser
extends SpelExpressionParser {
    private final SpelParserConfiguration configuration = new SpelParserConfiguration();

    @Override
    protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
        return new LimsInternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
    }
}
