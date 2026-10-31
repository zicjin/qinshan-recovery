package com.sunwayworld.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * Loads the supplier GlobalMapper XML read-only and the unchanged personnel
 * Oracle mapper XML, then emits executable BoundSql for a disposable schema.
 * No supplier runtime class is loaded; the output is reference evidence only.
 */
public final class PersonnelMapperBoundSqlReferenceTest {
    private static final String STATEMENT = "com.sunwayworld.emc.module.mdm.emp.emp.mapper.EmcEmpMapper.selectByCondition";
    private static final String DETAIL = "com.sunwayworld.emc.module.mdm.emp.emp.mapper.EmcEmpMapper.selectDetail";

    private static final class Case {
        final String name;
        final Map<String, Object> params;
        Case(String name, Map<String, Object> params) { this.name = name; this.params = params; }
    }

    private static Map<String, Object> base() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("isolateAbandon", "1");
        params.put("loginUser", null);
        return params;
    }

    private static String json(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n") + "\"";
    }

    private static Object value(Configuration configuration, BoundSql bound, Map<String, Object> params, String property) {
        if (bound.hasAdditionalParameter(property)) return bound.getAdditionalParameter(property);
        MetaObject meta = configuration.newMetaObject(params);
        return meta.hasGetter(property) ? meta.getValue(property) : null;
    }

    private static String literal(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return ((Boolean) value) ? "1" : "0";
        return "'" + value.toString().replace("'", "''") + "'";
    }

    private static String executableSql(Configuration configuration, BoundSql bound, Map<String, Object> params) {
        String sql = bound.getSql().replaceAll("\\s+", " ").trim();
        for (ParameterMapping mapping : bound.getParameterMappings()) {
            sql = sql.replaceFirst("\\?", java.util.regex.Matcher.quoteReplacement(literal(value(configuration, bound, params, mapping.getProperty()))));
        }
        return sql;
    }

    private static void emit(Configuration configuration, MappedStatement statement, Case test, PrintWriter out) {
        BoundSql bound = statement.getBoundSql(test.params);
        out.println("{\"case\":" + json(test.name) + ",\"statement\":" + json(statement.getId())
                + ",\"sql\":" + json(executableSql(configuration, bound, test.params))
                + ",\"parameter_count\":" + bound.getParameterMappings().size() + "}");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) throw new IllegalArgumentException("global mapper, personnel mapper, output JSONL");
        Configuration configuration = new Configuration();
        XMLMapperBuilder global = new XMLMapperBuilder(new java.io.FileInputStream(args[0]), configuration, args[0], configuration.getSqlFragments());
        global.parse();
        XMLMapperBuilder personnel = new XMLMapperBuilder(new java.io.FileInputStream(args[1]), configuration, args[1], configuration.getSqlFragments());
        personnel.parse();
        MappedStatement list = configuration.getMappedStatement(STATEMENT);
        MappedStatement detail = configuration.getMappedStatement(DETAIL);
        Map<String, Object> allowed = base();
        allowed.put("org_filter", "1");
        Map<String, Object> user = new HashMap<String, Object>(); user.put("id", "u-a");
        allowed.put("loginUser", user);
        Map<String, Object> noMatch = base(); noMatch.put("departmentId_filter", "missing-department");
        Map<String, Object> standard = base();
        standard.put("filterParamPatterns", new com.sunwayworld.framework.mybatis.mapper.FilterParamPattern[]{
                new com.sunwayworld.framework.mybatis.mapper.FilterParamPattern("EQ", "NAME", "Alice")});
        Map<String, Object> advanced = base();
        advanced.put("advancedFilterPatterns", new com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern[]{
                new com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern("NAME", "Bob", "EQ")});
        Map<String, Object> multi = base();
        multi.put("multiPropertyFilterPattern", new com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern(
                new String[]{"NAME", "ID"}, "ali", Boolean.FALSE));
        Map<String, Object> ordered = base();
        ordered.put("orderParams", Arrays.asList(com.sunwayworld.framework.data.Pair.of("NAME", "DESC")));
        Map<String, Object> detailParams = new HashMap<String, Object>(); detailParams.put("id", "e-1");
        Map<String, Object> restrictedDetail = new HashMap<String, Object>(allowed); restrictedDetail.put("id", "e-2");
        List<Case> cases = Arrays.asList(new Case("list-all", base()), new Case("list-org-filter", allowed), new Case("list-no-match", noMatch), new Case("list-standard-name", standard), new Case("list-advanced-name", advanced), new Case("list-multi-name", multi), new Case("ordered-name-desc", ordered));
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[2]), StandardCharsets.UTF_8))) {
            for (Case test : cases) emit(configuration, list, test, out);
            emit(configuration, detail, new Case("detail-by-id", detailParams), out);
            emit(configuration, detail, new Case("detail-org-filter-e2", restrictedDetail), out);
        }
        System.out.println("personnel BoundSql reference: PASS (" + cases.size() + " list cases + detail)");
    }
}
