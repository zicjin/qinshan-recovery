package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.QueryRequest;
import com.sunwayworld.owned.internal.web.OwnedReadRequest;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class QueryRequestContractTest {
    private static void rejects(String body, String expected) {
        try { QueryRequest.parse(body, Collections.<String, Object>emptyMap()); throw new AssertionError("accepted " + expected); }
        catch (IllegalArgumentException expectedError) { if (!expectedError.getMessage().contains(expected)) throw expectedError; }
    }
    public static void main(String[] args) {
        String body = "{\"requestJson\":\"{\\\"p\\\":{\\\"n\\\":\\\"2\\\",\\\"s\\\":\\\"25\\\",\\\"f\\\":{\\\"USERNAME_CISC\\\":\\\"alice\\\",\\\"status\\\":\\\"1\\\"},\\\"af\\\":[{\\\"link\\\":\\\"AND\\\",\\\"children\\\":[{\\\"name\\\":\\\"AGE\\\",\\\"match\\\":\\\"NG\\\",\\\"value\\\":18}]}],\\\"mf\\\":{\\\"name\\\":\\\"USERNAME,NAME\\\",\\\"value\\\":\\\"li\\\",\\\"caseSensitive\\\":false},\\\"o\\\":[{\\\"CREATEDTIME\\\":\\\"desc\\\"}]}}\"}";
        QueryRequest request = QueryRequest.parse(body, Collections.<String, Object>emptyMap());
        if (request.getPageNumber() != 2 || request.getPageSize() != 25) throw new AssertionError("pagination");
        if (request.getFilterPatterns().size() != 1 || request.getAdvancedPatterns().size() != 1 || request.getMultiPropertyPattern() == null) throw new AssertionError("filter groups");
        if (request.getOrderParameters().size() != 1 || !"desc".equals(request.getOrderParameters().get(0).getSecond())) throw new AssertionError("order");
        Map<String, Object> query = new HashMap<String, Object>(); query.put("departmentId_filter", "org-1");
        if (!"org-1".equals(QueryRequest.parse("{}", query).getMapperParameters().get("departmentId_filter"))) throw new AssertionError("allowed query parameter");
        rejects("{\"p\":{\"loginUser\":\"attacker\"}}", "server-owned");
        Map<String,Object> hints = QueryRequest.parse("{\"p\":{\"f\":{\"org_filter\":\"1\",\"userOrg_filter\":\"1\"}}}", null).getMapperParameters();
        QueryRequest scopeRequest = QueryRequest.parse("{\"p\":{\"f\":{\"org_filter\":\"1\",\"userOrg_filter\":\"1\"}}}", null);
        if (!scopeRequest.getMapperParameters().isEmpty() || !"1".equals(scopeRequest.getScopeHints().get("org_filter"))) throw new AssertionError("page scope hints");
        Map<String,String> frontendScope = new HashMap<String,String>();
        frontendScope.put("userOrg_filter", "1");
        frontendScope.put("orgid_SEQ", "dept_2052");
        QueryRequest frontendRequest = OwnedReadRequest.parse(new RestJsonWrapperBean("{}", frontendScope, null, null));
        if (!"dept_2052".equals(frontendRequest.getMapperParameters().get("orgid_SEQ"))) throw new AssertionError("frontend scope marker handling");
        QueryRequest nestedFrontendRequest = OwnedReadRequest.parse(new RestJsonWrapperBean("{\"p\":{\"userOrg_filter\":\"1\",\"orgid_SEQ\":\"dept_2052\"}}", null, null, null));
        if (!"dept_2052".equals(nestedFrontendRequest.getMapperParameters().get("orgid_SEQ"))) throw new AssertionError("nested frontend scope marker handling");
        Map<String,String> chooser = new HashMap<String,String>();
        chooser.put("targetType", "samplePlan"); chooser.put("sampleOrgId", "dept_2052");
        QueryRequest chooserRequest = OwnedReadRequest.parse(new RestJsonWrapperBean("{}", chooser, null, null));
        if (!"samplePlan".equals(chooserRequest.getMapperParameters().get("targetType"))) throw new AssertionError("chooser target type");
        QueryRequest mixedSampleRequest = OwnedReadRequest.parse(new RestJsonWrapperBean("{\"p\":{\"n\":1,\"s\":10},\"planSampleDate\":\"2026-10-27\"}", null, null, null));
        if (!"2026-10-27".equals(mixedSampleRequest.getMapperParameters().get("planSampleDate"))) throw new AssertionError("mixed sample chooser envelope");
        rejects("{\"p\":{\"o\":[{\"NAME;DROP TABLE X\":\"asc\"}]}}", "unsafe");
        rejects("{\"p\":{\"testids\":\"1\"}}", "server-owned");
        rejects("{\"p\":{\"s\":\"501\"}}", "exceeds");
        System.out.println("owned QueryRequest: PASS (envelope, pagination, filters, order and server-owned rejection)");
    }
}
