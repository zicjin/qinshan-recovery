package com.sunwayworld.framework;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

/**
 * Exercises the owned request boundary at the unchanged personnel mapper seam.
 * Trusted identity/scope values are injected by the caller after parsing.
 */
public final class QueryRequestMapperContractTest {
    private static final String STATEMENT = "com.sunwayworld.emc.module.mdm.emp.emp.mapper.EmcEmpMapper.selectByCondition";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("global mapper and personnel mapper");
        Configuration configuration = new Configuration();
        XMLMapperBuilder global = new XMLMapperBuilder(new FileInputStream(args[0]), configuration, args[0], configuration.getSqlFragments());
        global.parse();
        XMLMapperBuilder personnel = new XMLMapperBuilder(new FileInputStream(args[1]), configuration, args[1], configuration.getSqlFragments());
        personnel.parse();

        String body = "{\"requestJson\":\"{\\\"p\\\":{\\\"n\\\":\\\"2\\\",\\\"s\\\":\\\"25\\\",\\\"f\\\":{\\\"NAME_EQ\\\":\\\"Alice\\\"}}}\"}";
        QueryRequest request = QueryRequest.parse(body, null);
        if (request.getPageNumber() != 2 || request.getPageSize() != 25 || request.getFilterPatterns().size() != 1) {
            throw new AssertionError("owned request did not preserve personnel query shape");
        }
        Map<String, Object> loginUser = new HashMap<String, Object>();
        loginUser.put("id", "u-a");
        Map<String, Object> trusted = new HashMap<String, Object>();
        trusted.put("isolateAbandon", "1");
        trusted.put("org_filter", "1");
        trusted.put("loginUser", loginUser);
        Map<String, Object> params = request.toMapperParameters(trusted);
        BoundSql bound = configuration.getMappedStatement(STATEMENT).getBoundSql(params);
        String sql = bound.getSql().replaceAll("\\s+", " ").trim();
        if (!sql.contains("\"NAME\" = ?")) throw new AssertionError("standard filter did not reach mapper");
        if (!sql.contains("T_CORE_USER_PERMISSION") || !sql.contains("UP.USERID = ?")) {
            throw new AssertionError("trusted organization scope did not reach mapper");
        }
        if (!sql.contains("ORDER BY")) throw new AssertionError("personnel order clause missing");
        if (JSON.toJSONString(params).contains("server-owned")) throw new AssertionError("unexpected parser marker");
        System.out.println("owned QueryRequest -> personnel BoundSql: PASS (filter, trusted scope, ordering)");
    }
}
