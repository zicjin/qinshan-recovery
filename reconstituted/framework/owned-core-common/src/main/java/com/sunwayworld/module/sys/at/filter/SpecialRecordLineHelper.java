/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.framework.exception.UnsupportedOperationSystemException;
import java.util.HashMap;
import java.util.Map;

public class SpecialRecordLineHelper {
    private static final Map<String, String> AVAILABLE_TABLE_FIELD_MAP = new HashMap<String, String>();
    public static final String T_CORE_WK_SCHEME = "T_CORE_WK_SCHEME";
    public static final String T_CORE_ORG = "T_CORE_ORG";
    public static final String T_CORE_USER = "T_CORE_USER";
    public static final String T_CORE_ROLE = "T_CORE_ROLE";
    private static final Map<String, String> AVAILABLE_SPECIAL_FIELD_TABLE_MAP;
    public static final String USER = "USER";
    public static final String ORG = "ORG";
    public static final String ROLE = "ROLE";
    public static final String T_CORE_MENU = "T_CORE_MENU";

    public static void checkAvailable(String tableName) {
        assert (tableName != null);
        if (AVAILABLE_TABLE_FIELD_MAP.containsKey(tableName)) {
            return;
        }
        throw new UnsupportedOperationSystemException("unsupported tableName: " + tableName);
    }

    public static String getNameColumnByTableName(String tableName) {
        return AVAILABLE_TABLE_FIELD_MAP.get(tableName);
    }

    public static String getTableNameBySpecialFieldName(String nameColumn) {
        return AVAILABLE_SPECIAL_FIELD_TABLE_MAP.get(nameColumn);
    }

    static {
        AVAILABLE_TABLE_FIELD_MAP.put(T_CORE_USER, "USERNAME");
        AVAILABLE_TABLE_FIELD_MAP.put(T_CORE_ORG, "ORGNAME");
        AVAILABLE_TABLE_FIELD_MAP.put(T_CORE_ROLE, "ROLENAME");
        AVAILABLE_TABLE_FIELD_MAP.put(T_CORE_WK_SCHEME, "SCHEMENAME");
        AVAILABLE_SPECIAL_FIELD_TABLE_MAP = new HashMap<String, String>();
        AVAILABLE_SPECIAL_FIELD_TABLE_MAP.put(USER, T_CORE_USER);
        AVAILABLE_SPECIAL_FIELD_TABLE_MAP.put(ORG, T_CORE_ORG);
        AVAILABLE_SPECIAL_FIELD_TABLE_MAP.put(ROLE, T_CORE_ROLE);
        AVAILABLE_SPECIAL_FIELD_TABLE_MAP.put(T_CORE_WK_SCHEME, T_CORE_WK_SCHEME);
        AVAILABLE_SPECIAL_FIELD_TABLE_MAP.put(T_CORE_ORG, T_CORE_ORG);
        AVAILABLE_SPECIAL_FIELD_TABLE_MAP.put(T_CORE_MENU, T_CORE_MENU);
    }
}
