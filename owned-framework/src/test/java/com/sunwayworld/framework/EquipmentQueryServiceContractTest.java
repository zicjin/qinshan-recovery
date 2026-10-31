package com.sunwayworld.framework;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.owned.internal.data.EquipmentQueryService;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;

/** Owned equipment list/detail seam against a disposable synthetic Oracle schema. */
public final class EquipmentQueryServiceContractTest {
    private static int checks;
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(args[0])) { p.load(in); }
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(p.getProperty("url")); ds.setUser(p.getProperty("user")); ds.setPassword(p.getProperty("password"));
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP, PARENTORGID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
            s.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY, ORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            s.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            s.execute("CREATE TABLE T_EMC_ORG_PERMISSION (PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64), ORGID VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_EQUIPT_CATEGORY (ID VARCHAR2(64) PRIMARY KEY, EQUIPTCATEGORY VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_EQUIPT (ID NUMBER PRIMARY KEY, EQUIPTID VARCHAR2(64), EQUIPTNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), FACTORYNO VARCHAR2(100), MANUFACTURER VARCHAR2(100), STATUS VARCHAR2(36), EQUIPTCATEGORYID VARCHAR2(64), USEDDEPARTMENTID VARCHAR2(64), USEDDEPOSTID VARCHAR2(64), SHARING VARCHAR2(36), MAINTAIN VARCHAR2(36), SOURCEMETHOD VARCHAR2(36), SOURCEDATE DATE, EQUIPTBELONG VARCHAR2(36), MAINTAINUSERID VARCHAR2(64), KEEPUSERID VARCHAR2(64), ORGID VARCHAR2(64))");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('101501')");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('20251000001')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('org-b','Fixture Org B')");
            s.execute("INSERT INTO T_CORE_ORG VALUES ('post-c','Fixture Post C')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('allowed','Allowed User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','101501')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
            s.execute("INSERT INTO T_EMC_EQUIPT_CATEGORY VALUES ('cat-a','Microscope')");
            s.execute("INSERT INTO T_EMC_EQUIPT_CATEGORY VALUES ('cat-b','Balance')");
            s.execute("INSERT INTO T_EMC_ORG_PERMISSION VALUES ('T_EMC_EQUIPT_CATEGORY','cat-a','org-a')");
            s.execute("INSERT INTO T_EMC_ORG_PERMISSION VALUES ('T_EMC_EQUIPT_CATEGORY','cat-b','org-b')");
            s.execute("INSERT INTO T_EMC_EQUIPT VALUES (1,'EQ-001','Alpha Scope','M-1','F-1','Maker','inUse','cat-a','org-a','post-c','owned','normal','purchase',DATE '2025-01-01','owned',NULL,NULL,'org-a')");
            s.execute("INSERT INTO T_EMC_EQUIPT VALUES (2,'EQ-002','Beta Balance','B-1','F-2','Maker','inUse','cat-b','org-b','org-b','owned','normal','purchase',DATE '2025-01-02','owned',NULL,NULL,'org-b')");
            s.execute("INSERT INTO T_EMC_EQUIPT VALUES (3,'EQ-003','Alpha Scope','M-2','F-3','Maker','retired','cat-a','org-a','post-c','shared','normal','gift',DATE '2025-01-03','shared',NULL,NULL,'org-b')");
        }
        JdbcPersonnelIdentity identity = new JdbcPersonnelIdentity(ds, "MD5");
        JdbcPersonnelIdentity.Principal principal = identity.authenticate("allowed", "secret".toCharArray());
        EquipmentQueryService service = new EquipmentQueryService(ds, identity);
        Pagination<Map<String,Object>> page = service.list(QueryRequest.parse("{\"p\":{\"n\":1,\"s\":10,\"target_type\":\"equipList\"}}", null), principal);
        check(page.getTotal() == 2 && page.getRows().size() == 2, "category organization scope");
        check("EQ-003".equals(page.getRows().get(0).get("EQUIPTID")), "descending equipment order");
        check(page.getRows().get(0).containsKey("MAINTAINUSERID") && !page.getRows().get(0).containsKey("RN"), "full equipment projection and page envelope");
        Pagination<Map<String,Object>> filtered = service.list(QueryRequest.parse("{\"p\":{\"s\":10,\"f\":{\"EQUIPTNAME_CISC\":\"scope\"}}}", null), principal);
        check(filtered.getTotal() == 2, "case-insensitive equipment filter");
        Pagination<Map<String,Object>> empty = service.list(QueryRequest.parse("{\"p\":{\"f\":{\"EQUIPTNAME_EQ\":\"missing\"}}}", null), principal);
        check(empty.getTotal() == 0 && empty.getRows().isEmpty(), "equipment no-match");
        Map<String,Object> detail = service.detail(1L, principal);
        check("Alpha Scope".equals(detail.get("EQUIPTNAME")) && "Microscope".equals(detail.get("EQUIPTCATEGORYNAME"))
                && "Fixture Org A".equals(detail.get("USEDDEPARTMENTNAME"))
                && "Fixture Post C".equals(detail.get("USEDDEPOSTNAME")), "equipment detail joins distinct department and post");
        check(service.detail(2L, principal) == null, "out-of-scope equipment detail");
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("INSERT INTO T_EMC_EQUIPT VALUES (4,'EQ-004','Join Probe','J-1','F-4','Maker','inUse','cat-a','org-a','org-a','owned','normal','purchase',DATE '2025-01-04','owned',NULL,NULL,'org-a')");
            try (java.sql.ResultSet rows = s.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT E LEFT JOIN T_CORE_ORG C ON E.USEDDEPARTMENTID = C.ID LEFT JOIN T_CORE_ORG O ON E.USEDDEPOSTID = C.ID WHERE E.ID = 4")) {
                check(rows.next() && rows.getInt(1) == 3, "historical post join fans out across organization rows");
            }
        }
        Pagination<Map<String,Object>> distinctPost = service.list(QueryRequest.parse("{\"p\":{\"f\":{\"ID_EQ\":4}}}", null), principal);
        check(distinctPost.getTotal() == 1 && distinctPost.getRows().size() == 1
                && "Fixture Org A".equals(service.detail(4L, principal).get("USEDDEPOSTNAME")),
                "owned post join preserves one equipment row");
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) { s.execute("DELETE FROM T_EMC_EQUIPT WHERE ID = 4"); }
        try {
            service.list(QueryRequest.parse("{\"p\":{\"unsupported\":\"x\"}}", null), principal);
            throw new AssertionError("unsupported equipment parameter was silently ignored");
        } catch (IllegalArgumentException expected) { checks++; }
        check(service.list(QueryRequest.parse("{\"p\":{\"orgid_seq\":\"org-a\",\"target_type\":\"equipList\"}}", null), principal).getTotal() == 1,
                "page organization selection narrows category scope");
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("UPDATE T_CORE_ROLE_PERMISSION SET TARGETID = '20251000001' WHERE TARGETID = '101501'");
        }
        check(service.list(QueryRequest.parse("{}", null), principal).getTotal() == 2, "read-only equipment query menu grants reads");
        check(service.list(QueryRequest.parse("{\"p\":{\"f\":{\"ID_NEQ\":1}}}", null), principal).getTotal() == 1
                && "EQ-001".equals(service.list(QueryRequest.parse("{\"p\":{\"f\":{\"ID_NEQ\":1}}}", null), principal).getRows().get(0).get("EQUIPTID")),
                "NEQ means numeric equality in original GlobalMapper");
        check(service.list(QueryRequest.parse("{\"p\":{\"af\":[{\"children\":[{\"name\":\"ID\",\"match\":\"NEQ\",\"value\":1},{\"name\":\"ID\",\"match\":\"NEQ\",\"value\":3,\"link\":\"OR\"}]}]}}", null), principal).getTotal() == 2,
                "advanced OR keeps both in-scope equipment rows");
        check(service.list(QueryRequest.parse("{\"p\":{\"f\":{\"SOURCEDATE_DLOE\":\"2025-01-01\"}}}", null), principal).getTotal() == 1,
                "inclusive date filter preserves final day");
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG = '1' WHERE ID = 1");
        }
        try {
            service.list(QueryRequest.parse("{}", null), principal);
            throw new AssertionError("suspended equipment role remained authorized");
        } catch (org.springframework.security.access.AccessDeniedException expected) { checks++; }
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG = '0' WHERE ID = 1");
            s.execute("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID = 1");
        }
        try {
            service.detail(1L, principal);
            throw new AssertionError("revoked equipment menu remained authorized");
        } catch (org.springframework.security.access.AccessDeniedException expected) { checks++; }
        System.out.println("equipment query service: PASS (" + checks + " checks; synthetic Oracle only)");
    }
}
