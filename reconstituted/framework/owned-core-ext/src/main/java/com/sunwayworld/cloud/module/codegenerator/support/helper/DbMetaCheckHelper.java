/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.support.helper;

public class DbMetaCheckHelper {
    public static final int MAX_MYSQL_IDENTIFIER_LEN = 64;
    public static final int MAX_PGSQL_IDENTIFIER_LEN = 63;
    public static final int MAX_ORACLE_IDENTIFIER_LEN = 128;
    public static final int MAX_SQLSERVER_IDENTIFIER_LEN = 128;
    public static final int MAX_ORACLE_LT_12_2_IDENTIFIER_LEN = 30;
    public static final int ERR_IDENTIFIER_LEN = Math.min(64, Math.min(63, Math.min(128, 128)));
    public static final int WARN_IDENTIFIER_LEN = Math.min(30, Math.min(64, Math.min(63, Math.min(128, 128))));

    public static boolean validateMysql(String identifier) {
        return identifier.length() <= 64;
    }

    public static boolean validateSqlServer(String identifier) {
        return identifier.length() <= 128;
    }

    public static boolean validatePgsql(String identifier) {
        return identifier.length() <= 63;
    }

    public static boolean validateOracleOLessThan12_2(String identifier) {
        return identifier.length() <= 30;
    }

    public static boolean validateOracleGreaterOrEquals12_2(String identifier) {
        return identifier.length() <= 128;
    }

    public static boolean validate(String identifier) {
        return identifier.length() < ERR_IDENTIFIER_LEN;
    }

    public static boolean validateWarn(String identifier) {
        return identifier.length() < WARN_IDENTIFIER_LEN;
    }
}
