/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.sql;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.utils.StringUtils;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;

public abstract class SqlHelper {
    public static final String QTY_COLUMN_NAME = "QTY_";
    private static final Pattern SQL_ENDING_ORDER_BY_PATTERN = Pattern.compile("\\s+(?i)order\\s+(?i)by\\s+([^()]*[(][^()]*([(][^()]*([(][^()]*[)][^()]*)*[)][^()]*)*[)][^()]*)*[^()]*$");
    private static final Pattern SQL_ORDER_BY_PATTERN = Pattern.compile("(?i)order\\s+by");
    private static final String WS_OR_COMMENT = "(?:/\\*[\\s\\S]*?\\*/|--[^\\r\\n]*[\\r\\n]|\\s)";
    private static final Pattern DANGLING_WHERE_PATTERN = Pattern.compile("(?i)\\bwhere(" + WS_OR_COMMENT + "*)(?=$|\\)|\\bgroup\\s+by\\b|\\bhaving\\b|\\border\\s+by\\b|\\bunion\\b|\\bintersect\\b|\\bminus\\b)");
    private static final Pattern WHERE_AND_OR_PATTERN = Pattern.compile("(?i)\\bwhere" + WS_OR_COMMENT + "*\\b(and|or)\\b\\s*");
    private static final Pattern DANGLING_AND_OR_PATTERN = Pattern.compile("(?i)\\b(and|or)(" + WS_OR_COMMENT + "*)(?=$|\\)|\\bgroup\\s+by\\b|\\bhaving\\b|\\border\\s+by\\b|\\bunion\\b|\\bintersect\\b|\\bminus\\b)");
    private static final Pattern CONSECUTIVE_AND_OR = Pattern.compile("(?i)\\b(and|or)" + WS_OR_COMMENT + "+\\b(and|or)\\b\\s*");
    private static final Pattern EMPTY_PARENS = Pattern.compile("\\(\\s*\\)");
    public static final String REPLACE_KEY_EXT = "EXT$.";

    public static int findOuterOrderByPos(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return -1;
        }
        int n = sql.length();
        int depth = 0;
        boolean inSingleQuote = false;
        boolean inBlockComment = false;
        boolean inLineComment = false;
        int orderPos = -1;
        int i = 0;
        while (i < n) {
            char c = sql.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    if (i + 1 < n && sql.charAt(i + 1) == '\'') {
                        i++;
                    } else {
                        inSingleQuote = false;
                    }
                }
                i++;
                continue;
            }
            if (inBlockComment) {
                if (c == '*' && i + 1 < n && sql.charAt(i + 1) == '/') {
                    inBlockComment = false;
                    i += 2;
                    continue;
                }
                i++;
                continue;
            }
            if (inLineComment) {
                if (c == '\r' || c == '\n') {
                    inLineComment = false;
                }
                i++;
                continue;
            }
            if (c == '\'') {
                inSingleQuote = true;
                i++;
                continue;
            }
            if (c == '/' && i + 1 < n && sql.charAt(i + 1) == '*') {
                inBlockComment = true;
                i += 2;
                continue;
            }
            if (c == '-' && i + 1 < n && sql.charAt(i + 1) == '-') {
                inLineComment = true;
                i += 2;
                continue;
            }
            if (c == '(') {
                depth++;
                i++;
                continue;
            }
            if (c == ')') {
                depth--;
                i++;
                continue;
            }
            if (depth == 0) {
                if (i + 5 <= n && sql.substring(i, i + 5).equalsIgnoreCase("ORDER")) {
                    boolean prevWordBoundary = (i == 0 || (!Character.isLetterOrDigit(sql.charAt(i - 1)) && sql.charAt(i - 1) != '_'));
                    int next = i + 5;
                    boolean nextWordBoundary = (next >= n || (!Character.isLetterOrDigit(sql.charAt(next)) && sql.charAt(next) != '_'));
                    if (prevWordBoundary && nextWordBoundary) {
                        int j = next;
                        while (j < n && Character.isWhitespace(sql.charAt(j))) {
                            j++;
                        }
                        if (j + 2 <= n && sql.substring(j, j + 2).equalsIgnoreCase("BY")) {
                            int afterBy = j + 2;
                            boolean afterByBoundary = (afterBy >= n || (!Character.isLetterOrDigit(sql.charAt(afterBy)) && sql.charAt(afterBy) != '_'));
                            if (afterByBoundary) {
                                orderPos = i;
                                i = afterBy;
                                continue;
                            }
                        }
                    }
                }
            }
            i++;
        }
        return orderPos;
    }

    public static final String cleanDanglingWhereClause(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return sql;
        }
        String prev;
        do {
            prev = sql;
            sql = EMPTY_PARENS.matcher(sql).replaceAll("");
            sql = CONSECUTIVE_AND_OR.matcher(sql).replaceAll("$2 ");
            sql = WHERE_AND_OR_PATTERN.matcher(sql).replaceAll("WHERE ");
            sql = DANGLING_AND_OR_PATTERN.matcher(sql).replaceAll("$2");
            sql = DANGLING_WHERE_PATTERN.matcher(sql).replaceAll("$1");
        } while (!sql.equals(prev));
        return sql;
    }

    public static final String removeEndingOrderIfPresent(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return sql;
        }
        int pos = findOuterOrderByPos(sql);
        if (pos != -1) {
            return cleanDanglingWhereClause(sql.substring(0, pos));
        }
        Matcher matcher = SQL_ENDING_ORDER_BY_PATTERN.matcher(sql);
        if (matcher.find()) {
            return cleanDanglingWhereClause(matcher.replaceFirst(""));
        }
        return cleanDanglingWhereClause(sql);
    }

    public static final boolean hasOrderSegment(String sql) {
        Objects.requireNonNull(sql);
        int pos = findOuterOrderByPos(sql);
        if (pos != -1) {
            return true;
        }
        Matcher matcher = SQL_ENDING_ORDER_BY_PATTERN.matcher(sql);
        return matcher.find();
    }

    public static final String getOrderSegment(String sql) {
        Objects.requireNonNull(sql);
        int pos = findOuterOrderByPos(sql);
        if (pos != -1) {
            String orderBySql = sql.substring(pos).trim();
            return SQL_ORDER_BY_PATTERN.matcher(orderBySql).replaceFirst("").trim();
        }
        Matcher matcher = SQL_ENDING_ORDER_BY_PATTERN.matcher(sql);
        if (matcher.find()) {
            int posOld = matcher.start();
            String orderBySql = sql.substring(posOld).trim();
            return SQL_ORDER_BY_PATTERN.matcher(orderBySql).replaceFirst("").trim();
        }
        throw new UnexpectedException("Not an ordered sql.");
    }

    public static final String getSelectTotalSql(String sql, String[] sumProperties, String[] countProperties) {
        Dialect dialect = DialectRepository.getDialect();
        StringBuilder newSql = new StringBuilder("SELECT COUNT(1) ").append(QTY_COLUMN_NAME);
        if (sumProperties.length > 0) {
            Arrays.asList(sumProperties).forEach(c -> newSql.append(", ").append(dialect.getSumColumnSql(c.toUpperCase(), "0")).append(c.toUpperCase()));
        }
        if (countProperties.length > 0) {
            Arrays.asList(countProperties).forEach(c -> newSql.append(", COUNT(DISTINCT ").append(c.toUpperCase()).append(") ").append(c.toUpperCase()));
        }
        String innerSql = cleanDanglingWhereClause(SqlHelper.removeEndingOrderIfPresent(sql));
        newSql.append(" FROM (\n").append(innerSql).append("\n").append(") T");
        return newSql.toString().replace(REPLACE_KEY_EXT, "");
    }

    public static final boolean isRiskySqlSegment(String sqlSegment) {
        if (StringUtils.isEmpty(sqlSegment)) {
            return false;
        }
        return StringUtils.contains(sqlSegment, "'") || StringUtils.contains(sqlSegment, " ");
    }

    public static final String getWrappedValue(String value) {
        if (StringUtils.contains(value, "'")) {
            value = DialectRepository.getDialect().getDatabase().equals("mysql") ? StringUtils.replace(value, "'", "\\'") : StringUtils.replace(value, "'", "''");
        }
        return value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final List<String> getSqlColumnList(String sql) {
        ArrayList<String> columnList = new ArrayList<String>();
        SqlSessionTemplate template = ApplicationContextHelper.getMybatisSqlSessionTemplate();
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(template.getSqlSessionFactory(), template.getExecutorType(), template.getPersistenceExceptionTranslator());
            try (Statement stmt = sqlSession.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(DialectRepository.getDialect().getLimitSql(sql, 0, 0));){
                ResultSetMetaData rsmd = rs.getMetaData();
                int j = rsmd.getColumnCount();
                for (int i = 0; i < j; ++i) {
                    columnList.add(rsmd.getColumnName(i + 1).toUpperCase());
                }
            }
            catch (SQLException sqle) {
                throw new JdbcException(sqle.getMessage());
            }
        }
        finally {
            if (sqlSession != null) {
                SqlSessionUtils.closeSqlSession(sqlSession, template.getSqlSessionFactory());
            }
        }
        return columnList;
    }

    public static final boolean containsEscapedValue(Object value) {
        return DialectRepository.getDialect().containsEscapedValue(value);
    }

    public static final String getEscapedStrValue(Object value) {
        return DialectRepository.getDialect().getEscapedStrValue(value);
    }
}
