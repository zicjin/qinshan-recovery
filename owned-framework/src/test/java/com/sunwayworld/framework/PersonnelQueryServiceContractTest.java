package com.sunwayworld.framework;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.owned.internal.data.PersonnelQueryService;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;

/** Public personnel read seam against a disposable synthetic Oracle schema. */
public final class PersonnelQueryServiceContractTest {
    private static int checks;
    private static int filterChecks;
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    private static void filterCheck(boolean value, String label) { filterChecks++; if (!value) throw new AssertionError(label); }

    private static void assertRows(PersonnelQueryService service, JdbcPersonnelIdentity.Principal principal,
                                   String json, String label, String... expected) throws Exception {
        Pagination<Map<String,Object>> page = service.list(QueryRequest.parse(json, null), principal);
        java.util.List<String> ids = new java.util.ArrayList<String>();
        for (Map<String,Object> row : page.getRows()) ids.add(String.valueOf(row.get("ID")));
        check(page.getTotal() == expected.length && ids.equals(java.util.Arrays.asList(expected)), label + " expected=" + java.util.Arrays.toString(expected) + " actual=" + ids);
    }

    private static void rejected(PersonnelQueryService service, JdbcPersonnelIdentity.Principal principal, String json, String label) throws Exception {
        try { service.list(QueryRequest.parse(json, null), principal); }
        catch (IllegalArgumentException expected) { check(true, label); return; }
        throw new AssertionError(label + " was silently accepted");
    }

    private static void assertFilterRows(PersonnelQueryService service, JdbcPersonnelIdentity.Principal principal,
                                         String json, String label, String... expected) throws Exception {
        Pagination<Map<String,Object>> page = service.list(QueryRequest.parse(json, null), principal);
        java.util.List<String> ids = new java.util.ArrayList<String>();
        for (Map<String,Object> row : page.getRows()) ids.add(String.valueOf(row.get("ID")));
        filterCheck(page.getTotal() == expected.length && ids.equals(java.util.Arrays.asList(expected)),
                label + " expected=" + java.util.Arrays.toString(expected) + " actual=" + ids);
    }

    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(args[0])) { p.load(in); }
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(p.getProperty("url")); ds.setUser(p.getProperty("user")); ds.setPassword(p.getProperty("password"));
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP, PARENTORGID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, LASTSUSPENDEDFLAG VARCHAR2(1), USEDBYORGID VARCHAR2(100))");
            s.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
            s.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY, ORGNAME VARCHAR2(100), PARENTID VARCHAR2(64), SAMPLEDEPARTMENT VARCHAR2(100))");
            s.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            s.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            s.execute("CREATE TABLE T_EMC_EMP (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, ORGID VARCHAR2(64), AGE NUMBER, SEX VARCHAR2(10), MOBILE VARCHAR2(100), REMARK VARCHAR2(100), CUSTOMFIELD VARCHAR2(100), FILTERTEST VARCHAR2(1), REPORTAUDIT VARCHAR2(1), REPORTISSUE VARCHAR2(1), STATION VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_EMP_QUAL (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), EXPIREDATE DATE, WARNINGDAYS NUMBER)");
            s.execute("CREATE TABLE T_EMC_CERT (ID NUMBER PRIMARY KEY, EMPID VARCHAR2(64), EXPIREDATE DATE, WARNINGDAYS NUMBER)");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('100001')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A',NULL,'1')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('org-b','Fixture Org B','org-a','0')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('allowed','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('other','5ebe2294ecd0e0f08eab7690d2a6ee69','org-b','Fixture Org B','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0',NULL)");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100001')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,CREATEDTIME,ORGID,AGE,SEX,MOBILE,REMARK,CUSTOMFIELD,FILTERTEST,REPORTAUDIT,REPORTISSUE,STATION) VALUES ('e-1','Alice',TIMESTAMP '2026-01-02 10:00:00','org-a',30,'F','123',NULL,'extension-a','1','0','1','Analyst')");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,CREATEDTIME,ORGID,AGE,SEX,MOBILE,REMARK,CUSTOMFIELD,FILTERTEST,REPORTAUDIT,REPORTISSUE,STATION) VALUES ('e-2','Alicia',TIMESTAMP '2026-01-01 10:00:00','org-a',40,'F','456','some remark','extension-b','0','1','0','Reviewer')");
            s.execute("INSERT INTO T_EMC_EMP (ID,USERNAME,CREATEDTIME,ORGID,AGE,SEX,MOBILE,REMARK,CUSTOMFIELD,FILTERTEST,REPORTAUDIT,REPORTISSUE,STATION) VALUES ('e-3','Bob',TIMESTAMP '2025-12-31 10:00:00','org-b',50,'M','789',NULL,'extension-c','1','0','0','Analyst')");
            s.execute("INSERT INTO T_EMC_EMP_QUAL VALUES (1,'e-1',SYSDATE-2,0)");
            s.execute("INSERT INTO T_EMC_EMP_QUAL VALUES (2,'e-2',SYSDATE+30,0)");
            s.execute("INSERT INTO T_EMC_CERT VALUES (1,'e-1',SYSDATE+30,0)");
            s.execute("INSERT INTO T_EMC_CERT VALUES (2,'e-2',SYSDATE-2,0)");
            s.execute("UPDATE T_CORE_USER SET ID='e-1' WHERE ID='allowed'");
            s.execute("INSERT INTO T_CORE_USER VALUES ('e-3','5ebe2294ecd0e0f08eab7690d2a6ee69','org-b','Fixture Org B','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('e-1','T_CORE_ORG','org-a')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'e-1')");
            s.execute("DELETE FROM T_CORE_USER_PERMISSION WHERE USERID='allowed'");
            s.execute("DELETE FROM T_CORE_ROLE_USER WHERE USERID='allowed'");
            s.execute("INSERT INTO T_CORE_USER VALUES ('e-2','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('e-2','T_CORE_ORG','org-a')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'e-2')");
        }
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            // Employee organization is the row scope; user department may differ.
            s.execute("UPDATE T_CORE_USER SET ORGID='org-b',ORGNAME='Fixture Org B' WHERE ID='e-2'");
            s.execute("UPDATE T_CORE_USER SET ORGID='org-a',ORGNAME='Fixture Org A' WHERE ID='e-3'");
        }
        JdbcPersonnelIdentity identity = new JdbcPersonnelIdentity(ds, "MD5");
        JdbcPersonnelIdentity.Principal principal = identity.authenticate("e-1", "secret".toCharArray());
        PersonnelQueryService service = new PersonnelQueryService(ds, identity);

        Pagination<Map<String,Object>> all = service.list(QueryRequest.parse("{\"p\":{\"n\":1,\"s\":2}}", null), principal);
        check(all.getTotal() == 2 && all.getRows().size() == 2, "organization scope and first page");
        check("string".equals(all.getColumnTypeMap().get("username"))
                && "dateTime".equals(all.getColumnTypeMap().get("createdtime"))
                && "string".equals(all.getColumnTypeMap().get("orgname"))
                && "number".equals(all.getColumnTypeMap().get("rn_"))
                && !all.getColumnTypeMap().containsKey("parentorgname"),
                "supplier-compatible column type map");
        check("e-1".equals(all.getRows().get(0).get("ID")), "stable created-time ordering");
        Pagination<Map<String,Object>> second = service.list(QueryRequest.parse("{\"p\":{\"n\":2,\"s\":1}}", null), principal);
        check(second.getTotal() == 2 && second.getRows().size() == 1 && "e-2".equals(second.getRows().get(0).get("ID")), "page two");
        Pagination<Map<String,Object>> filtered = service.list(QueryRequest.parse("{\"p\":{\"s\":50,\"f\":{\"USERNAME_CISC\":\"ali\"}}}", null), principal);
        check(filtered.getTotal() == 2, "case-insensitive filter total=" + filtered.getTotal() + " rows=" + filtered.getRows().size());
        Pagination<Map<String,Object>> empty = service.list(QueryRequest.parse("{\"p\":{\"f\":{\"USERNAME_EQ\":\"nobody\"}}}", null), principal);
        check(empty.getTotal() == 0 && empty.getRows().isEmpty(), "no-match result");
        check("e-1".equals(service.detail("e-1", principal).get("ID")), "in-scope detail");
        check(service.detail("e-3", principal) == null, "out-of-scope detail denied by row scope");
        check(identity.permittedOrganizationIds(principal).equals(Collections.singleton("org-a")), "concrete organization grant");
        Map<String,Object> row = service.detail("e-2", principal);
        check("Alicia".equals(row.get("USERNAME")) && "456".equals(row.get("MOBILE"))
                && "extension-b".equals(row.get("CUSTOMFIELD")) && "org-a".equals(row.get("ORGID"))
                && "Fixture Org B".equals(row.get("DEPARTMENTNAME")) && "Fixture Org A".equals(row.get("ORGNAME")),
                "full employee and joined detail projection");
        Map<String,Object> listRow = all.getRows().get(1);
        check("org-b".equals(listRow.get("DEPARTMENTID")) && "org-a".equals(listRow.get("PARENTORGID"))
                && "org-a".equals(listRow.get("USERORGID")) && "0".equals(listRow.get("SAMPLEDEPARTMENT"))
                && "extension-b".equals(listRow.get("CUSTOMFIELD")), "list joins and extension projection");
        check(!listRow.containsKey("RN") && listRow.containsKey("RN_"),
                "supplier row-number projection is normalized to rn_");
        assertRows(service, principal, "{\"p\":{\"departmentId_filter\":\"org-b\"}}", "department exact filter", "e-2");
        assertRows(service, principal, "{\"p\":{\"departmentId_filter\":\"org-a\"}}", "department direct child included", "e-1", "e-2");
        assertRows(service, principal, "{\"p\":{\"orgid_SEQ\":\"org-b\"}}", "page organization filter cannot override granted scope");
        assertRows(service, principal, "{\"p\":{\"af\":[{\"children\":[{\"name\":\"USERNAME\",\"match\":\"EQ\",\"value\":\"Alice\"},{\"link\":\"OR\",\"name\":\"USERNAME\",\"match\":\"EQ\",\"value\":\"Alicia\"}]}]}}", "advanced OR group", "e-1", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_CISE\":\"ICE\"}}}", "case-insensitive suffix filter", "e-1");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_CISEQ\":\"alice\"}}}", "case-insensitive exact filter", "e-1");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_OCISB\":\"AL\"}}}", "case-insensitive OR prefix filter", "e-1", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_OCISC\":\"ALI\"}}}", "case-insensitive OR contains filter", "e-1", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_OCISE\":\"ICE\"}}}", "case-insensitive OR suffix filter", "e-1");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_OCISEQ\":\"alice\",\"ID_OCISEQ\":\"e-2\"}}}", "case-insensitive OR exact filter", "e-1", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"mf\":{\"name\":\"USERNAME,ID\",\"value\":\"e-2\",\"caseSensitive\":false}}}", "case-insensitive multi-property filter", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_OR\":\"Alice\",\"ID_OR\":\"e-2\"}}}", "multi-field OR filter", "e-1", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"FILTERTEST_EQ\":\"1\"}}}", "filterable employee flag", "e-1");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"REPORTAUDIT_EQ\":\"1\"}}}", "filterable report flag", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"AGE_NGOE\":40}}}", "numeric range filter", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"AGE_NLOE\":30}}}", "numeric inclusive upper bound", "e-1");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"CREATEDTIME_DEQ\":\"2026-01-02\"}}}", "date equality by day", "e-1");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"CREATEDTIME_DLOE\":\"2026-01-01\"}}}", "date range filter", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"f\":{\"USERNAME_NSC\":\"zz\"}}}", "string not-contains filter", "e-1", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"overdueFilter\":\"1\"}}", "overdue certificate filter", "e-1");
        assertFilterRows(service, principal, "{\"p\":{\"remind\":\"overdue\"}}", "historical overdue selector", "e-2");
        assertFilterRows(service, principal, "{\"p\":{\"o\":[{\"USERNAME\":\"asc\"}]}}", "explicit name ordering", "e-1", "e-2");
        filterChecks++;
        try { service.list(QueryRequest.parse("{\"p\":{\"f\":{\"UNKNOWN_CISC\":\"Anal\"}}}", null), principal); throw new AssertionError("unsupported filter column was accepted"); }
        catch (IllegalArgumentException expected) { }
        System.out.println("personnel filter parity: PASS (" + filterChecks + " checks; synthetic Oracle only)");
        System.out.println("personnel query service: PASS (" + checks + " checks; synthetic Oracle only)");
    }
}
