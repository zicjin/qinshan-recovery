package com.sunwayworld.owned.report;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** The small report-line contract recovered from T_CORE_REPORT_CONFIG_LINE. */
public final class OwnedReportRecord {
    private final String id, name, category, path, parameterSpec;

    public OwnedReportRecord(String id, String name, String category, String path, String parameterSpec) {
        this.id = require(id, "id");
        this.name = require(name, "name");
        this.category = require(category, "category");
        this.path = path == null ? "" : path;
        this.parameterSpec = parameterSpec == null ? "" : parameterSpec;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPath() { return path; }
    public String getParameterSpec() { return parameterSpec; }

    /** Maps external report names to request parameter names, preserving order. */
    public Map<String, String> parameterNames() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (parameterSpec.trim().length() == 0) return result;
        for (String pair : parameterSpec.split("&", -1)) {
            String[] values = pair.split("=", -1);
            if (values.length != 2 || !values[0].matches("[A-Za-z][A-Za-z0-9_]{0,63}")
                    || !values[1].matches("[A-Za-z][A-Za-z0-9_]{0,63}")
                    || result.put(values[0].trim(), values[1].trim()) != null) {
                throw new IllegalArgumentException("Malformed report parameter specification");
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static String require(String value, String name) {
        if (value == null || value.trim().length() == 0) throw new IllegalArgumentException(name + " is required");
        return value;
    }
}
