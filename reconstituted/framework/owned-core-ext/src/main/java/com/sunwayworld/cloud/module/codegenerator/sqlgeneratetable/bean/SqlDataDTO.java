/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean;

public class SqlDataDTO {
    private String oracle;
    private String mysql;
    private String sqlserver;
    private String pgsql;

    public String getOracle() {
        return this.oracle;
    }

    public void setOracle(String oracle) {
        this.oracle = oracle;
    }

    public String getMysql() {
        return this.mysql;
    }

    public void setMysql(String mysql) {
        this.mysql = mysql;
    }

    public String getSqlserver() {
        return this.sqlserver;
    }

    public void setSqlserver(String sqlserver) {
        this.sqlserver = sqlserver;
    }

    public String getPgsql() {
        return this.pgsql;
    }

    public void setPgsql(String pgsql) {
        this.pgsql = pgsql;
    }
}
