package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Normalizes only the observed read envelopes and rejects ambiguous/unknown transport input. */
public final class OwnedReadRequest {
    private OwnedReadRequest() {}
    private static final Set<String> DIRECT = new HashSet<String>(Arrays.asList("f", "qf", "af", "mf", "o", "n", "s"));
    private static final Set<String> CHOOSER = new HashSet<String>(Arrays.asList(
            "targetType", "targetFilter", "sampleOrgId", "analysisUserId",
            "emc_role_assigned", "status_DIFFER", "planSampleDate"));
    public static QueryRequest parse(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("query wrapper is required");
        Map<String, Object> params = normalize(object(wrapper.getRequestJson()), true);
        Map<String, Object> query = new LinkedHashMap<String, Object>();
        query.putAll(wrapper.getParamMap());
        for (Map.Entry<String, Object> entry : normalize(query, false).entrySet()) {
            if (params.containsKey(entry.getKey())) throw new IllegalArgumentException("duplicate query input");
            params.put(entry.getKey(), entry.getValue());
        }
        JSONObject envelope = new JSONObject(); envelope.put("p", params);
        return QueryRequest.parse(envelope.toJSONString(), null);
    }

    private static Map<String, Object> normalize(Map<String, ?> input, boolean nestedBody) {
        if (input.containsKey("requestJson")) {
            if (!nestedBody || input.size() != 1 || !(input.get("requestJson") instanceof String)) {
                throw new IllegalArgumentException("invalid requestJson envelope");
            }
            return normalize(object((String) input.get("requestJson")), false);
        }
        if (input.containsKey("p")) {
            Object value = input.get("p");
            Map<String, Object> result;
            if (value instanceof String) result = normalize(object((String) value), false);
            else if (value instanceof Map<?, ?>) {
                Map<String, Object> nested = new LinkedHashMap<String, Object>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) nested.put(String.valueOf(entry.getKey()), entry.getValue());
                result = normalize(nested, false);
            } else throw new IllegalArgumentException("invalid p envelope");
            for (Map.Entry<String, ?> entry : input.entrySet()) {
                if ("p".equals(entry.getKey())) continue;
                if (isTrustedScopeMarker(entry.getKey(), entry.getValue())) continue;
                if (!DIRECT.contains(entry.getKey()) && !CHOOSER.contains(entry.getKey())
                        && !isSafeSequenceParameter(entry.getKey())) throw new IllegalArgumentException("mixed query envelope");
                if (result.containsKey(entry.getKey())) throw new IllegalArgumentException("duplicate query input");
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, ?> entry : input.entrySet()) {
            // The original Gikam personnel grid sends userOrg_filter=1 as a
            // request marker.  It is not trusted scope input: the personnel
            // service always derives organization IDs from the authenticated
            // principal.  Accept and discard the marker so the original
            // frontend contract remains usable without widening SQL scope.
            if (isTrustedScopeMarker(entry.getKey(), entry.getValue())) continue;
            if (!DIRECT.contains(entry.getKey()) && !CHOOSER.contains(entry.getKey())
                    && !isSafeSequenceParameter(entry.getKey())) {
                throw new IllegalArgumentException("unknown query envelope field");
            }
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static boolean isTrustedScopeMarker(String key, Object value) {
        if (key == null || !("userOrg_filter".equalsIgnoreCase(key) || "org_filter".equalsIgnoreCase(key))) return false;
        if (value == null) return true;
        String marker = String.valueOf(value).trim();
        if (marker.isEmpty() || "1".equals(marker) || "true".equalsIgnoreCase(marker)) return true;
        throw new IllegalArgumentException("invalid organization scope marker");
    }

    private static boolean isSafeSequenceParameter(String key) {
        return key != null && key.matches("[A-Za-z][A-Za-z0-9_$.]*_SEQ");
    }

    private static JSONObject object(String json) {
        if (json == null || json.trim().isEmpty()) return new JSONObject();
        try {
            JSONObject value = JSON.parseObject(json);
            if (value == null) throw new IllegalArgumentException("query JSON object is required");
            return value;
        } catch (RuntimeException failure) { throw new IllegalArgumentException("invalid query JSON object", failure); }
    }
}
