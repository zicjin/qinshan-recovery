/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard;

import com.sunwayworld.cloud.lims.reporttemplate.support.spel.ast.MapPropertyOrFieldReference;
import com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard.LimsToken;
import com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard.LimsTokenKind;
import com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard.LimsTokenizer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.ConstructorReference;
import org.springframework.expression.spel.ast.Elvis;
import org.springframework.expression.spel.ast.FunctionReference;
import org.springframework.expression.spel.ast.Identifier;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.expression.spel.ast.InlineList;
import org.springframework.expression.spel.ast.InlineMap;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.expression.spel.ast.OpDec;
import org.springframework.expression.spel.ast.OpDivide;
import org.springframework.expression.spel.ast.OpEQ;
import org.springframework.expression.spel.ast.OpGE;
import org.springframework.expression.spel.ast.OpGT;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.expression.spel.ast.OpLE;
import org.springframework.expression.spel.ast.OpLT;
import org.springframework.expression.spel.ast.OpMinus;
import org.springframework.expression.spel.ast.OpModulus;
import org.springframework.expression.spel.ast.OpMultiply;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.expression.spel.ast.OpPlus;
import org.springframework.expression.spel.ast.OperatorBetween;
import org.springframework.expression.spel.ast.OperatorInstanceof;
import org.springframework.expression.spel.ast.OperatorMatches;
import org.springframework.expression.spel.ast.OperatorNot;
import org.springframework.expression.spel.ast.OperatorPower;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.expression.spel.ast.QualifiedIdentifier;
import org.springframework.expression.spel.ast.Selection;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.ast.Ternary;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class LimsInternalSpelExpressionParser
extends TemplateAwareExpressionParser {
    private static final Pattern VALID_QUALIFIED_ID_PATTERN = Pattern.compile("[\\p{L}\\p{N}_$]+");
    private final SpelParserConfiguration configuration;
    private final Deque<SpelNodeImpl> constructedNodes = new ArrayDeque<SpelNodeImpl>();
    private String expressionString = "";
    private List<LimsToken> limsTokenStream = Collections.emptyList();
    private int tokenStreamLength;
    private int tokenStreamPointer;

    public LimsInternalSpelExpressionParser(SpelParserConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
        try {
            this.expressionString = expressionString;
            LimsTokenizer limsTokenizer = new LimsTokenizer(expressionString);
            this.limsTokenStream = limsTokenizer.process();
            this.tokenStreamLength = this.limsTokenStream.size();
            this.tokenStreamPointer = 0;
            this.constructedNodes.clear();
            SpelNodeImpl ast = this.eatExpression();
            Assert.state(ast != null, "No node");
            LimsToken t = this.peekToken();
            if (t != null) {
                throw new SpelParseException(t.startPos, SpelMessage.MORE_INPUT, this.toString(this.nextToken()));
            }
            Assert.isTrue(this.constructedNodes.isEmpty(), "At least one node expected");
            return new SpelExpression(expressionString, ast, this.configuration);
        }
        catch (InternalParseException ex) {
            throw ex.getCause();
        }
    }

    @Nullable
    private SpelNodeImpl eatExpression() {
        SpelNodeImpl expr = this.eatLogicalOrExpression();
        LimsToken t = this.peekToken();
        if (t != null) {
            if (t.kind == LimsTokenKind.ASSIGN) {
                if (expr == null) {
                    expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
                }
                this.nextToken();
                SpelNodeImpl assignedValue = this.eatLogicalOrExpression();
                return new Assign(t.startPos, t.endPos, expr, assignedValue);
            }
            if (t.kind == LimsTokenKind.ELVIS) {
                if (expr == null) {
                    expr = new NullLiteral(t.startPos - 1, t.endPos - 2);
                }
                this.nextToken();
                SpelNodeImpl valueIfNull = this.eatExpression();
                if (valueIfNull == null) {
                    valueIfNull = new NullLiteral(t.startPos + 1, t.endPos + 1);
                }
                return new Elvis(t.startPos, t.endPos, expr, valueIfNull);
            }
            if (t.kind == LimsTokenKind.QMARK) {
                if (expr == null) {
                    expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
                }
                this.nextToken();
                SpelNodeImpl ifTrueExprValue = this.eatExpression();
                this.eatToken(LimsTokenKind.COLON);
                SpelNodeImpl ifFalseExprValue = this.eatExpression();
                return new Ternary(t.startPos, t.endPos, expr, ifTrueExprValue, ifFalseExprValue);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatLogicalOrExpression() {
        SpelNodeImpl expr = this.eatLogicalAndExpression();
        while (this.peekIdentifierToken("or") || this.peekToken(LimsTokenKind.SYMBOLIC_OR)) {
            LimsToken t = this.takeToken();
            SpelNodeImpl rhExpr = this.eatLogicalAndExpression();
            this.checkOperands(t, expr, rhExpr);
            expr = new OpOr(t.startPos, t.endPos, expr, rhExpr);
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatLogicalAndExpression() {
        SpelNodeImpl expr = this.eatRelationalExpression();
        while (this.peekIdentifierToken("and") || this.peekToken(LimsTokenKind.SYMBOLIC_AND)) {
            LimsToken t = this.takeToken();
            SpelNodeImpl rhExpr = this.eatRelationalExpression();
            this.checkOperands(t, expr, rhExpr);
            expr = new OpAnd(t.startPos, t.endPos, expr, rhExpr);
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatRelationalExpression() {
        SpelNodeImpl expr = this.eatSumExpression();
        LimsToken relationalOperatorLimsToken = this.maybeEatRelationalOperator();
        if (relationalOperatorLimsToken != null) {
            LimsToken t = this.takeToken();
            SpelNodeImpl rhExpr = this.eatSumExpression();
            this.checkOperands(t, expr, rhExpr);
            LimsTokenKind tk = relationalOperatorLimsToken.kind;
            if (relationalOperatorLimsToken.isNumericRelationalOperator()) {
                if (tk == LimsTokenKind.GT) {
                    return new OpGT(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == LimsTokenKind.LT) {
                    return new OpLT(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == LimsTokenKind.LE) {
                    return new OpLE(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == LimsTokenKind.GE) {
                    return new OpGE(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == LimsTokenKind.EQ) {
                    return new OpEQ(t.startPos, t.endPos, expr, rhExpr);
                }
                Assert.isTrue(tk == LimsTokenKind.NE, "Not-equals token expected");
                return new OpNE(t.startPos, t.endPos, expr, rhExpr);
            }
            if (tk == LimsTokenKind.INSTANCEOF) {
                return new OperatorInstanceof(t.startPos, t.endPos, expr, rhExpr);
            }
            if (tk == LimsTokenKind.MATCHES) {
                return new OperatorMatches(t.startPos, t.endPos, expr, rhExpr);
            }
            Assert.isTrue(tk == LimsTokenKind.BETWEEN, "Between token expected");
            return new OperatorBetween(t.startPos, t.endPos, expr, rhExpr);
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatSumExpression() {
        SpelNodeImpl expr = this.eatProductExpression();
        while (this.peekToken(LimsTokenKind.PLUS, LimsTokenKind.MINUS, LimsTokenKind.INC)) {
            LimsToken t = this.takeToken();
            SpelNodeImpl rhExpr = this.eatProductExpression();
            this.checkRightOperand(t, rhExpr);
            if (t.kind == LimsTokenKind.PLUS) {
                expr = new OpPlus(t.startPos, t.endPos, expr, rhExpr);
                continue;
            }
            if (t.kind != LimsTokenKind.MINUS) continue;
            expr = new OpMinus(t.startPos, t.endPos, expr, rhExpr);
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatProductExpression() {
        SpelNodeImpl expr = this.eatPowerIncDecExpression();
        while (this.peekToken(LimsTokenKind.STAR, LimsTokenKind.DIV, LimsTokenKind.MOD)) {
            LimsToken t = this.takeToken();
            SpelNodeImpl rhExpr = this.eatPowerIncDecExpression();
            this.checkOperands(t, expr, rhExpr);
            if (t.kind == LimsTokenKind.STAR) {
                expr = new OpMultiply(t.startPos, t.endPos, expr, rhExpr);
                continue;
            }
            if (t.kind == LimsTokenKind.DIV) {
                expr = new OpDivide(t.startPos, t.endPos, expr, rhExpr);
                continue;
            }
            Assert.isTrue(t.kind == LimsTokenKind.MOD, "Mod token expected");
            expr = new OpModulus(t.startPos, t.endPos, expr, rhExpr);
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatPowerIncDecExpression() {
        SpelNodeImpl expr = this.eatUnaryExpression();
        if (this.peekToken(LimsTokenKind.POWER)) {
            LimsToken t = this.takeToken();
            SpelNodeImpl rhExpr = this.eatUnaryExpression();
            this.checkRightOperand(t, rhExpr);
            return new OperatorPower(t.startPos, t.endPos, expr, rhExpr);
        }
        if (expr != null && this.peekToken(LimsTokenKind.INC, LimsTokenKind.DEC)) {
            LimsToken t = this.takeToken();
            if (t.getKind() == LimsTokenKind.INC) {
                return new OpInc(t.startPos, t.endPos, true, expr);
            }
            return new OpDec(t.startPos, t.endPos, true, expr);
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatUnaryExpression() {
        if (this.peekToken(LimsTokenKind.PLUS, LimsTokenKind.MINUS, LimsTokenKind.NOT)) {
            LimsToken t = this.takeToken();
            SpelNodeImpl expr = this.eatUnaryExpression();
            Assert.state(expr != null, "No node");
            if (t.kind == LimsTokenKind.NOT) {
                return new OperatorNot(t.startPos, t.endPos, expr);
            }
            if (t.kind == LimsTokenKind.PLUS) {
                return new OpPlus(t.startPos, t.endPos, expr);
            }
            Assert.isTrue(t.kind == LimsTokenKind.MINUS, "Minus token expected");
            return new OpMinus(t.startPos, t.endPos, expr);
        }
        if (this.peekToken(LimsTokenKind.INC, LimsTokenKind.DEC)) {
            LimsToken t = this.takeToken();
            SpelNodeImpl expr = this.eatUnaryExpression();
            if (t.getKind() == LimsTokenKind.INC) {
                return new OpInc(t.startPos, t.endPos, false, expr);
            }
            return new OpDec(t.startPos, t.endPos, false, expr);
        }
        return this.eatPrimaryExpression();
    }

    @Nullable
    private SpelNodeImpl eatPrimaryExpression() {
        SpelNodeImpl start = this.eatStartNode();
        ArrayList<SpelNodeImpl> nodes = null;
        SpelNodeImpl node = this.eatNode();
        while (node != null) {
            if (nodes == null) {
                nodes = new ArrayList<SpelNodeImpl>(4);
                nodes.add(start);
            }
            nodes.add(node);
            node = this.eatNode();
        }
        if (start == null || nodes == null) {
            return start;
        }
        return new CompoundExpression(start.getStartPosition(), ((SpelNodeImpl)nodes.get(nodes.size() - 1)).getEndPosition(), nodes.toArray(new SpelNodeImpl[0]));
    }

    @Nullable
    private SpelNodeImpl eatNode() {
        return this.peekToken(LimsTokenKind.DOT, LimsTokenKind.SAFE_NAVI) ? this.eatDottedNode() : this.eatNonDottedNode();
    }

    @Nullable
    private SpelNodeImpl eatNonDottedNode() {
        if (this.peekToken(LimsTokenKind.LSQUARE) && this.maybeEatIndexer()) {
            return this.pop();
        }
        return null;
    }

    private SpelNodeImpl eatDottedNode() {
        boolean nullSafeNavigation;
        LimsToken t = this.takeToken();
        boolean bl = nullSafeNavigation = t.kind == LimsTokenKind.SAFE_NAVI;
        if (this.maybeEatMethodOrProperty(nullSafeNavigation) || this.maybeEatFunctionOrVar() || this.maybeEatProjection(nullSafeNavigation) || this.maybeEatSelection(nullSafeNavigation)) {
            return this.pop();
        }
        if (this.peekToken() == null) {
            throw this.internalException(t.startPos, SpelMessage.OOD, new Object[0]);
        }
        throw this.internalException(t.startPos, SpelMessage.UNEXPECTED_DATA_AFTER_DOT, this.toString(this.peekToken()));
    }

    private boolean maybeEatFunctionOrVar() {
        if (!this.peekToken(LimsTokenKind.HASH)) {
            return false;
        }
        LimsToken t = this.takeToken();
        LimsToken functionOrVariableName = this.eatToken(LimsTokenKind.IDENTIFIER);
        SpelNodeImpl[] args = this.maybeEatMethodArgs();
        if (args == null) {
            this.push(new VariableReference(functionOrVariableName.stringValue(), t.startPos, functionOrVariableName.endPos));
            return true;
        }
        this.push(new FunctionReference(functionOrVariableName.stringValue(), t.startPos, functionOrVariableName.endPos, args));
        return true;
    }

    @Nullable
    private SpelNodeImpl[] maybeEatMethodArgs() {
        if (!this.peekToken(LimsTokenKind.LPAREN)) {
            return null;
        }
        ArrayList<SpelNodeImpl> args = new ArrayList<SpelNodeImpl>();
        this.consumeArguments(args);
        this.eatToken(LimsTokenKind.RPAREN);
        return args.toArray(new SpelNodeImpl[0]);
    }

    private void eatConstructorArgs(List<SpelNodeImpl> accumulatedArguments) {
        if (!this.peekToken(LimsTokenKind.LPAREN)) {
            throw new InternalParseException(new SpelParseException(this.expressionString, this.positionOf(this.peekToken()), SpelMessage.MISSING_CONSTRUCTOR_ARGS, new Object[0]));
        }
        this.consumeArguments(accumulatedArguments);
        this.eatToken(LimsTokenKind.RPAREN);
    }

    private void consumeArguments(List<SpelNodeImpl> accumulatedArguments) {
        LimsToken next;
        LimsToken t = this.peekToken();
        Assert.state(t != null, "Expected token");
        int pos = t.startPos;
        do {
            this.nextToken();
            t = this.peekToken();
            if (t == null) {
                throw this.internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
            }
            if (t.kind == LimsTokenKind.RPAREN) continue;
            accumulatedArguments.add(this.eatExpression());
        } while ((next = this.peekToken()) != null && next.kind == LimsTokenKind.COMMA);
        if (next == null) {
            throw this.internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
        }
    }

    private int positionOf(@Nullable LimsToken t) {
        if (t == null) {
            return this.expressionString.length();
        }
        return t.startPos;
    }

    @Nullable
    private SpelNodeImpl eatStartNode() {
        if (this.maybeEatLiteral()) {
            return this.pop();
        }
        if (this.maybeEatParenExpression()) {
            return this.pop();
        }
        if (this.maybeEatTypeReference() || this.maybeEatNullReference() || this.maybeEatConstructorReference() || this.maybeEatMethodOrProperty(false) || this.maybeEatFunctionOrVar()) {
            return this.pop();
        }
        if (this.maybeEatBeanReference()) {
            return this.pop();
        }
        if (this.maybeEatProjection(false) || this.maybeEatSelection(false) || this.maybeEatIndexer()) {
            return this.pop();
        }
        if (this.maybeEatInlineListOrMap()) {
            return this.pop();
        }
        return null;
    }

    private boolean maybeEatBeanReference() {
        if (this.peekToken(LimsTokenKind.BEAN_REF) || this.peekToken(LimsTokenKind.FACTORY_BEAN_REF)) {
            BeanReference beanReference;
            LimsToken beanRefLimsToken = this.takeToken();
            LimsToken beanNameLimsToken = null;
            String beanName = null;
            if (this.peekToken(LimsTokenKind.IDENTIFIER)) {
                beanNameLimsToken = this.eatToken(LimsTokenKind.IDENTIFIER);
                beanName = beanNameLimsToken.stringValue();
            } else if (this.peekToken(LimsTokenKind.LITERAL_STRING)) {
                beanNameLimsToken = this.eatToken(LimsTokenKind.LITERAL_STRING);
                beanName = beanNameLimsToken.stringValue();
                beanName = beanName.substring(1, beanName.length() - 1);
            } else {
                throw this.internalException(beanRefLimsToken.startPos, SpelMessage.INVALID_BEAN_REFERENCE, new Object[0]);
            }
            if (beanRefLimsToken.getKind() == LimsTokenKind.FACTORY_BEAN_REF) {
                String beanNameString = String.valueOf(LimsTokenKind.FACTORY_BEAN_REF.tokenChars) + beanName;
                beanReference = new BeanReference(beanRefLimsToken.startPos, beanNameLimsToken.endPos, beanNameString);
            } else {
                beanReference = new BeanReference(beanNameLimsToken.startPos, beanNameLimsToken.endPos, beanName);
            }
            this.constructedNodes.push(beanReference);
            return true;
        }
        return false;
    }

    private boolean maybeEatTypeReference() {
        if (this.peekToken(LimsTokenKind.IDENTIFIER)) {
            LimsToken typeName = this.peekToken();
            Assert.state(typeName != null, "Expected token");
            if (!"T".equals(typeName.stringValue())) {
                return false;
            }
            LimsToken t = this.takeToken();
            if (this.peekToken(LimsTokenKind.RSQUARE)) {
                this.push(new MapPropertyOrFieldReference(false, t.stringValue(), t.startPos, t.endPos));
                return true;
            }
            this.eatToken(LimsTokenKind.LPAREN);
            SpelNodeImpl node = this.eatPossiblyQualifiedId();
            int dims = 0;
            while (this.peekToken(LimsTokenKind.LSQUARE, true)) {
                this.eatToken(LimsTokenKind.RSQUARE);
                ++dims;
            }
            this.eatToken(LimsTokenKind.RPAREN);
            this.constructedNodes.push(new TypeReference(typeName.startPos, typeName.endPos, node, dims));
            return true;
        }
        return false;
    }

    private boolean maybeEatNullReference() {
        if (this.peekToken(LimsTokenKind.IDENTIFIER)) {
            LimsToken nullLimsToken = this.peekToken();
            Assert.state(nullLimsToken != null, "Expected token");
            if (!"null".equalsIgnoreCase(nullLimsToken.stringValue())) {
                return false;
            }
            this.nextToken();
            this.constructedNodes.push(new NullLiteral(nullLimsToken.startPos, nullLimsToken.endPos));
            return true;
        }
        return false;
    }

    private boolean maybeEatProjection(boolean nullSafeNavigation) {
        LimsToken t = this.peekToken();
        if (!this.peekToken(LimsTokenKind.PROJECT, true)) {
            return false;
        }
        Assert.state(t != null, "No token");
        SpelNodeImpl expr = this.eatExpression();
        Assert.state(expr != null, "No node");
        this.eatToken(LimsTokenKind.RSQUARE);
        this.constructedNodes.push(new Projection(nullSafeNavigation, t.startPos, t.endPos, expr));
        return true;
    }

    private boolean maybeEatInlineListOrMap() {
        LimsToken t = this.peekToken();
        if (!this.peekToken(LimsTokenKind.LCURLY, true)) {
            return false;
        }
        Assert.state(t != null, "No token");
        SpelNodeImpl expr = null;
        LimsToken closingCurly = this.peekToken();
        if (this.peekToken(LimsTokenKind.RCURLY, true)) {
            Assert.state(closingCurly != null, "No token");
            expr = new InlineList(t.startPos, closingCurly.endPos, new SpelNodeImpl[0]);
        } else if (this.peekToken(LimsTokenKind.COLON, true)) {
            closingCurly = this.eatToken(LimsTokenKind.RCURLY);
            expr = new InlineMap(t.startPos, closingCurly.endPos, new SpelNodeImpl[0]);
        } else {
            SpelNodeImpl firstExpression = this.eatExpression();
            if (this.peekToken(LimsTokenKind.RCURLY)) {
                ArrayList<SpelNodeImpl> elements = new ArrayList<SpelNodeImpl>();
                elements.add(firstExpression);
                closingCurly = this.eatToken(LimsTokenKind.RCURLY);
                expr = new InlineList(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
            } else if (this.peekToken(LimsTokenKind.COMMA, true)) {
                ArrayList<SpelNodeImpl> elements = new ArrayList<SpelNodeImpl>();
                elements.add(firstExpression);
                do {
                    elements.add(this.eatExpression());
                } while (this.peekToken(LimsTokenKind.COMMA, true));
                closingCurly = this.eatToken(LimsTokenKind.RCURLY);
                expr = new InlineList(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
            } else if (this.peekToken(LimsTokenKind.COLON, true)) {
                ArrayList<SpelNodeImpl> elements = new ArrayList<SpelNodeImpl>();
                elements.add(firstExpression);
                elements.add(this.eatExpression());
                while (this.peekToken(LimsTokenKind.COMMA, true)) {
                    elements.add(this.eatExpression());
                    this.eatToken(LimsTokenKind.COLON);
                    elements.add(this.eatExpression());
                }
                closingCurly = this.eatToken(LimsTokenKind.RCURLY);
                expr = new InlineMap(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
            } else {
                throw this.internalException(t.startPos, SpelMessage.OOD, new Object[0]);
            }
        }
        this.constructedNodes.push(expr);
        return true;
    }

    private boolean maybeEatIndexer() {
        LimsToken t = this.peekToken();
        if (!this.peekToken(LimsTokenKind.LSQUARE, true)) {
            return false;
        }
        Assert.state(t != null, "No token");
        SpelNodeImpl expr = this.eatExpression();
        Assert.state(expr != null, "No node");
        this.eatToken(LimsTokenKind.RSQUARE);
        this.constructedNodes.push(new Indexer(t.startPos, t.endPos, expr));
        return true;
    }

    private boolean maybeEatSelection(boolean nullSafeNavigation) {
        LimsToken t = this.peekToken();
        if (!this.peekSelectToken()) {
            return false;
        }
        Assert.state(t != null, "No token");
        this.nextToken();
        SpelNodeImpl expr = this.eatExpression();
        if (expr == null) {
            throw this.internalException(t.startPos, SpelMessage.MISSING_SELECTION_EXPRESSION, new Object[0]);
        }
        this.eatToken(LimsTokenKind.RSQUARE);
        if (t.kind == LimsTokenKind.SELECT_FIRST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 1, t.startPos, t.endPos, expr));
        } else if (t.kind == LimsTokenKind.SELECT_LAST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 2, t.startPos, t.endPos, expr));
        } else {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 0, t.startPos, t.endPos, expr));
        }
        return true;
    }

    private SpelNodeImpl eatPossiblyQualifiedId() {
        ArrayDeque<Identifier> qualifiedIdPieces = new ArrayDeque<Identifier>();
        LimsToken node = this.peekToken();
        while (this.isValidQualifiedId(node)) {
            this.nextToken();
            if (node.kind != LimsTokenKind.DOT) {
                qualifiedIdPieces.add(new Identifier(node.stringValue(), node.startPos, node.endPos));
            }
            node = this.peekToken();
        }
        if (qualifiedIdPieces.isEmpty()) {
            if (node == null) {
                throw this.internalException(this.expressionString.length(), SpelMessage.OOD, new Object[0]);
            }
            throw this.internalException(node.startPos, SpelMessage.NOT_EXPECTED_TOKEN, "qualified ID", node.getKind().toString().toLowerCase());
        }
        return new QualifiedIdentifier(((SpelNodeImpl)qualifiedIdPieces.getFirst()).getStartPosition(), ((SpelNodeImpl)qualifiedIdPieces.getLast()).getEndPosition(), qualifiedIdPieces.toArray(new SpelNodeImpl[0]));
    }

    private boolean isValidQualifiedId(@Nullable LimsToken node) {
        if (node == null || node.kind == LimsTokenKind.LITERAL_STRING) {
            return false;
        }
        if (node.kind == LimsTokenKind.DOT || node.kind == LimsTokenKind.IDENTIFIER) {
            return true;
        }
        String value = node.stringValue();
        return StringUtils.hasLength(value) && VALID_QUALIFIED_ID_PATTERN.matcher(value).matches();
    }

    private boolean maybeEatMethodOrProperty(boolean nullSafeNavigation) {
        if (this.peekToken(LimsTokenKind.IDENTIFIER)) {
            LimsToken methodOrPropertyName = this.takeToken();
            SpelNodeImpl[] args = this.maybeEatMethodArgs();
            if (args == null) {
                this.push(new MapPropertyOrFieldReference(nullSafeNavigation, methodOrPropertyName.stringValue(), methodOrPropertyName.startPos, methodOrPropertyName.endPos));
                return true;
            }
            this.push(new MethodReference(nullSafeNavigation, methodOrPropertyName.stringValue(), methodOrPropertyName.startPos, methodOrPropertyName.endPos, args));
            return true;
        }
        return false;
    }

    private boolean maybeEatConstructorReference() {
        if (this.peekIdentifierToken("new")) {
            LimsToken newLimsToken = this.takeToken();
            if (this.peekToken(LimsTokenKind.RSQUARE)) {
                this.push(new MapPropertyOrFieldReference(false, newLimsToken.stringValue(), newLimsToken.startPos, newLimsToken.endPos));
                return true;
            }
            SpelNodeImpl possiblyQualifiedConstructorName = this.eatPossiblyQualifiedId();
            ArrayList<SpelNodeImpl> nodes = new ArrayList<SpelNodeImpl>();
            nodes.add(possiblyQualifiedConstructorName);
            if (this.peekToken(LimsTokenKind.LSQUARE)) {
                ArrayList<SpelNodeImpl> dimensions = new ArrayList<SpelNodeImpl>();
                while (this.peekToken(LimsTokenKind.LSQUARE, true)) {
                    if (!this.peekToken(LimsTokenKind.RSQUARE)) {
                        dimensions.add(this.eatExpression());
                    } else {
                        dimensions.add(null);
                    }
                    this.eatToken(LimsTokenKind.RSQUARE);
                }
                if (this.maybeEatInlineListOrMap()) {
                    nodes.add(this.pop());
                }
                this.push(new ConstructorReference(newLimsToken.startPos, newLimsToken.endPos, dimensions.toArray(new SpelNodeImpl[0]), nodes.toArray(new SpelNodeImpl[0])));
            } else {
                this.eatConstructorArgs(nodes);
                this.push(new ConstructorReference(newLimsToken.startPos, newLimsToken.endPos, nodes.toArray(new SpelNodeImpl[0])));
            }
            return true;
        }
        return false;
    }

    private void push(SpelNodeImpl newNode) {
        this.constructedNodes.push(newNode);
    }

    private SpelNodeImpl pop() {
        return this.constructedNodes.pop();
    }

    private boolean maybeEatLiteral() {
        LimsToken t = this.peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == LimsTokenKind.LITERAL_INT) {
            this.push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 10));
        } else if (t.kind == LimsTokenKind.LITERAL_LONG) {
            this.push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 10));
        } else if (t.kind == LimsTokenKind.LITERAL_HEXINT) {
            this.push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 16));
        } else if (t.kind == LimsTokenKind.LITERAL_HEXLONG) {
            this.push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 16));
        } else if (t.kind == LimsTokenKind.LITERAL_REAL) {
            this.push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, false));
        } else if (t.kind == LimsTokenKind.LITERAL_REAL_FLOAT) {
            this.push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, true));
        } else if (this.peekIdentifierToken("true")) {
            this.push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, true));
        } else if (this.peekIdentifierToken("false")) {
            this.push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, false));
        } else if (t.kind == LimsTokenKind.LITERAL_STRING) {
            this.push(new StringLiteral(t.stringValue(), t.startPos, t.endPos, t.stringValue()));
        } else {
            return false;
        }
        this.nextToken();
        return true;
    }

    private boolean maybeEatParenExpression() {
        if (this.peekToken(LimsTokenKind.LPAREN)) {
            this.nextToken();
            SpelNodeImpl expr = this.eatExpression();
            Assert.state(expr != null, "No node");
            this.eatToken(LimsTokenKind.RPAREN);
            this.push(expr);
            return true;
        }
        return false;
    }

    @Nullable
    private LimsToken maybeEatRelationalOperator() {
        LimsToken t = this.peekToken();
        if (t == null) {
            return null;
        }
        if (t.isNumericRelationalOperator()) {
            return t;
        }
        if (t.isIdentifier()) {
            String idString = t.stringValue();
            if (idString.equalsIgnoreCase("instanceof")) {
                return t.asInstanceOfToken();
            }
            if (idString.equalsIgnoreCase("matches")) {
                return t.asMatchesToken();
            }
            if (idString.equalsIgnoreCase("between")) {
                return t.asBetweenToken();
            }
        }
        return null;
    }

    private LimsToken eatToken(LimsTokenKind expectedKind) {
        LimsToken t = this.nextToken();
        if (t == null) {
            int pos = this.expressionString.length();
            throw this.internalException(pos, SpelMessage.OOD, new Object[0]);
        }
        if (t.kind != expectedKind) {
            throw this.internalException(t.startPos, SpelMessage.NOT_EXPECTED_TOKEN, expectedKind.toString().toLowerCase(), t.getKind().toString().toLowerCase());
        }
        return t;
    }

    private boolean peekToken(LimsTokenKind desiredLimsTokenKind) {
        return this.peekToken(desiredLimsTokenKind, false);
    }

    private boolean peekToken(LimsTokenKind desiredLimsTokenKind, boolean consumeIfMatched) {
        LimsToken t = this.peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == desiredLimsTokenKind) {
            if (consumeIfMatched) {
                ++this.tokenStreamPointer;
            }
            return true;
        }
        return desiredLimsTokenKind == LimsTokenKind.IDENTIFIER && t.kind.ordinal() >= LimsTokenKind.DIV.ordinal() && t.kind.ordinal() <= LimsTokenKind.NOT.ordinal() && t.data != null;
    }

    private boolean peekToken(LimsTokenKind possible1, LimsTokenKind possible2) {
        LimsToken t = this.peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == possible1 || t.kind == possible2;
    }

    private boolean peekToken(LimsTokenKind possible1, LimsTokenKind possible2, LimsTokenKind possible3) {
        LimsToken t = this.peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == possible1 || t.kind == possible2 || t.kind == possible3;
    }

    private boolean peekIdentifierToken(String identifierString) {
        LimsToken t = this.peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == LimsTokenKind.IDENTIFIER && identifierString.equalsIgnoreCase(t.stringValue());
    }

    private boolean peekSelectToken() {
        LimsToken t = this.peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == LimsTokenKind.SELECT || t.kind == LimsTokenKind.SELECT_FIRST || t.kind == LimsTokenKind.SELECT_LAST;
    }

    private LimsToken takeToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            throw new IllegalStateException("No token");
        }
        return this.limsTokenStream.get(this.tokenStreamPointer++);
    }

    @Nullable
    private LimsToken nextToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.limsTokenStream.get(this.tokenStreamPointer++);
    }

    @Nullable
    private LimsToken peekToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.limsTokenStream.get(this.tokenStreamPointer);
    }

    public String toString(@Nullable LimsToken t) {
        if (t == null) {
            return "";
        }
        if (t.getKind().hasPayload()) {
            return t.stringValue();
        }
        return t.kind.toString().toLowerCase();
    }

    private void checkOperands(LimsToken limsToken, @Nullable SpelNodeImpl left, @Nullable SpelNodeImpl right) {
        this.checkLeftOperand(limsToken, left);
        this.checkRightOperand(limsToken, right);
    }

    private void checkLeftOperand(LimsToken limsToken, @Nullable SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw this.internalException(limsToken.startPos, SpelMessage.LEFT_OPERAND_PROBLEM, new Object[0]);
        }
    }

    private void checkRightOperand(LimsToken limsToken, @Nullable SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw this.internalException(limsToken.startPos, SpelMessage.RIGHT_OPERAND_PROBLEM, new Object[0]);
        }
    }

    private InternalParseException internalException(int startPos, SpelMessage message, Object ... inserts) {
        return new InternalParseException(new SpelParseException(this.expressionString, startPos, message, inserts));
    }
}
