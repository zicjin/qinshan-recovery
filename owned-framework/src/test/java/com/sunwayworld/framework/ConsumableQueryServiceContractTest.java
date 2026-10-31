package com.sunwayworld.framework;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.owned.internal.data.ConsumableQueryService;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;

/** Public consumable read seam exercised against a disposable synthetic Oracle. */
public final class ConsumableQueryServiceContractTest {
    private static int checks;
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setURL(properties.getProperty("url")); dataSource.setUser(properties.getProperty("user")); dataSource.setPassword(properties.getProperty("password"));
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement()) {
            sql.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP)");
            sql.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, LASTSUSPENDEDFLAG VARCHAR2(1))");
            sql.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            sql.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            sql.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
            sql.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY, ORGNAME VARCHAR2(100))");
            sql.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            sql.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            sql.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            sql.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            sql.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            sql.execute("INSERT INTO T_CORE_MENU VALUES ('430201')");
            sql.execute("INSERT INTO T_CORE_MENU VALUES ('20250800003')");
            sql.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_CORE_ORG VALUES ('org-b','Fixture Org B')");
            sql.execute("INSERT INTO T_CORE_USER VALUES ('allowed','Allowed User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL)");
            sql.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')");
            sql.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            sql.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','430201')");
            sql.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
            sql.execute("CREATE TABLE T_EMC_CONSUME (ID VARCHAR2(64) PRIMARY KEY, MTLNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), STOCKQTY NUMBER, MTLCATEGORYID VARCHAR2(64), HARMFULNESS VARCHAR2(36), PRODUCTNO VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            sql.execute("INSERT INTO T_EMC_CONSUME VALUES ('CSM-001','Alpha Bottle','100 ml',7,'glass','normal','P-001','allowed','Allowed User',TIMESTAMP '2025-01-01 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CONSUME VALUES ('CSM-002','Hidden Solvent','500 ml',3,'chemical','harmful','P-002','allowed','Allowed User',TIMESTAMP '2025-01-02 09:00:00','org-b','Fixture Org B')");
            sql.execute("INSERT INTO T_EMC_CONSUME VALUES ('CSM-003','Beta Bottle','250 ml',0,'glass','normal','P-003','allowed','Allowed User',TIMESTAMP '2025-01-03 09:00:00','org-a','Fixture Org A')");
            sql.execute("CREATE TABLE T_EMC_CSM_STOCK (ID NUMBER PRIMARY KEY, MTLID VARCHAR2(64), BATCHNO VARCHAR2(64), STOCKQTY NUMBER, UNITNAME VARCHAR2(64), EXPIREDATE TIMESTAMP, LOWLIMIT NUMBER, STATUS VARCHAR2(4), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK VALUES (11,'CSM-001','A-1',5,'bottle',TIMESTAMP '2026-01-01 00:00:00',1,'0','allowed','Allowed User',TIMESTAMP '2025-01-04 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK VALUES (12,'CSM-001','A-2',0,'bottle',TIMESTAMP '2026-02-01 00:00:00',1,'1','allowed','Allowed User',TIMESTAMP '2025-01-05 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK VALUES (13,'CSM-002','H-1',8,'litre',TIMESTAMP '2026-03-01 00:00:00',1,'0','allowed','Allowed User',TIMESTAMP '2025-01-06 09:00:00','org-b','Fixture Org B')");
            sql.execute("CREATE TABLE T_EMC_CSM_STOCK_LOG (ID NUMBER PRIMARY KEY, STOCKID NUMBER, LOGTYPE VARCHAR2(36), PREVIOUSQTY NUMBER, QTY NUMBER, VENDORID VARCHAR2(64), PRICE NUMBER, LOGCONTENT VARCHAR2(200), RECEIVEDBYID VARCHAR2(64), RECEIVEDBYORGID VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK_LOG VALUES (101,11,'stockin',0,5,'vendor-a',2.5,'initial','allowed','org-a','allowed','Allowed User',TIMESTAMP '2025-01-04 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK_LOG VALUES (102,11,'receive',5,4,NULL,0,'use','allowed','org-a','allowed','Allowed User',TIMESTAMP '2025-01-05 09:00:00','org-a','Fixture Org A')");
            sql.execute("INSERT INTO T_EMC_CSM_STOCK_LOG VALUES (103,13,'stockin',0,8,'vendor-b',3,'hidden','allowed','org-b','allowed','Allowed User',TIMESTAMP '2025-01-06 09:00:00','org-b','Fixture Org B')");
        }
        JdbcPersonnelIdentity identity = new JdbcPersonnelIdentity(dataSource, "MD5");
        JdbcPersonnelIdentity.Principal principal = identity.authenticate("allowed", "secret".toCharArray());
        ConsumableQueryService service = new ConsumableQueryService(dataSource, identity);
        Pagination<Map<String,Object>> page = service.list(QueryRequest.parse("{\"p\":{\"n\":1,\"s\":1}}", null), principal);
        check(page.getTotal() == 2 && page.getRows().size() == 1 && "CSM-003".equals(page.getRows().get(0).get("ID")),
                "consumable list applies organization scope, descending order and pagination");
        check("number".equals(page.getColumnTypeMap().get("stockqty"))
                        && "dateTime".equals(page.getColumnTypeMap().get("createdtime"))
                        && "number".equals(page.getColumnTypeMap().get("rn_")),
                "consumable pagination preserves supplier column type metadata");
        check(page.getTotalMap().get("qty_") != null && page.getTotalMap().get("qty_").intValue() == 2,
                "consumable pagination preserves supplier total map");
        check(service.list(QueryRequest.parse("{\"p\":{\"f\":{\"MTLNAME_CISC\":\"alpha\"}}}", null), principal).getTotal() == 1,
                "case-insensitive consumable name filter");
        check(service.list(QueryRequest.parse("{\"p\":{\"f\":{\"MTLNAME_EQ\":\"missing\"}}}", null), principal).getTotal() == 0,
                "consumable no-match");
        check("Alpha Bottle".equals(service.detail("CSM-001", principal).get("MTLNAME")), "consumable detail projection");
        check(service.detail("CSM-002", principal) == null, "out-of-scope consumable detail");
        Map<String,Object> ownedMaterial = service.detail("CSM-001", principal);
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement();
                java.sql.ResultSet rows = sql.executeQuery("SELECT ID,MTLNAME,SPECIFICATION,STOCKQTY,MTLCATEGORYID,HARMFULNESS,PRODUCTNO,CREATEDBYORGID FROM T_EMC_CONSUME WHERE ID='CSM-001'")) {
            check(rows.next() && "CSM-001".equals(ownedMaterial.get("ID"))
                    && rows.getString("MTLNAME").equals(ownedMaterial.get("MTLNAME"))
                    && rows.getString("SPECIFICATION").equals(ownedMaterial.get("SPECIFICATION"))
                    && rows.getDouble("STOCKQTY") == ((Number) ownedMaterial.get("STOCKQTY")).doubleValue()
                    && rows.getString("MTLCATEGORYID").equals(ownedMaterial.get("MTLCATEGORYID"))
                    && rows.getString("HARMFULNESS").equals(ownedMaterial.get("HARMFULNESS"))
                    && rows.getString("PRODUCTNO").equals(ownedMaterial.get("PRODUCTNO"))
                    && rows.getString("CREATEDBYORGID").equals(ownedMaterial.get("CREATEDBYORGID")),
                    "owned material projection matches legacy Oracle columns");
        }
        Pagination<Map<String,Object>> stock = service.stock("CSM-001", QueryRequest.parse("{\"p\":{\"status_SEQ\":\"0\",\"stockQty_NG\":0}}", null), principal);
        check(stock.getTotal() == 1 && ((Number) stock.getRows().get(0).get("ID")).longValue() == 11L, "stock MTLID and direct UI filters");
        Map<String,Object> ownedStock = service.stockDetail(11L, principal);
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement();
                java.sql.ResultSet rows = sql.executeQuery("SELECT ID,MTLID,BATCHNO,STOCKQTY,UNITNAME,STATUS,CREATEDBYORGID FROM T_EMC_CSM_STOCK WHERE ID=11")) {
            check(rows.next() && rows.getLong("ID") == ((Number) ownedStock.get("ID")).longValue()
                    && rows.getString("MTLID").equals(ownedStock.get("MTLID"))
                    && rows.getString("BATCHNO").equals(ownedStock.get("BATCHNO"))
                    && rows.getDouble("STOCKQTY") == ((Number) ownedStock.get("STOCKQTY")).doubleValue()
                    && rows.getString("UNITNAME").equals(ownedStock.get("UNITNAME"))
                    && rows.getString("STATUS").equals(ownedStock.get("STATUS"))
                    && rows.getString("CREATEDBYORGID").equals(ownedStock.get("CREATEDBYORGID")),
                    "owned stock projection matches legacy Oracle columns");
        }
        check(service.stock("CSM-002", QueryRequest.parse("{}", null), principal).getTotal() == 0, "out-of-scope stock suppression");
        check(service.stock("CSM-001", QueryRequest.parse("{\"p\":{\"status_SEQ\":null,\"stockQty_NG\":null}}", null), principal).getTotal() == 2,
                "show-all stock request omits null direct filters");
        check(service.stock("CSM-001", QueryRequest.parse("{\"p\":{\"f\":{\"STOCKQTY_NEQ\":5}}}", null), principal).getTotal() == 1,
                "numeric NEQ retains the observed equality operator");
        check(service.stock("CSM-001", QueryRequest.parse("{\"p\":{\"f\":{\"STATUS_DIFFER\":\"1\"}}}", null), principal).getTotal() == 1,
                "DIFFER excludes the matching status");
        check(service.stock("CSM-001", QueryRequest.parse("{\"p\":{\"f\":{\"EXPIREDATE_DGOE\":\"2026-01-01\"}}}", null), principal).getTotal() == 2,
                "date greater-than-or-equal includes the boundary day");
        check(service.stock("CSM-001", QueryRequest.parse("{\"p\":{\"f\":{\"EXPIREDATE_DLOE\":\"2026-01-01\"}}}", null), principal).getTotal() == 1,
                "date less-than-or-equal includes the entire boundary day");
        check(service.stock("CSM-001", QueryRequest.parse("{\"p\":{\"f\":{\"CREATEDTIME_TGOE\":\"2025-01-05 09:00:00\"}}}", null), principal).getTotal() == 1,
                "timestamp greater-than-or-equal includes the boundary instant");
        check(service.stockDetail(11L, principal) != null && service.stockDetail(13L, principal) == null, "stock detail organization scope");
        Pagination<Map<String,Object>> log = service.stockLog(11L, QueryRequest.parse("{}", null), principal);
        check(log.getTotal() == 2 && "Allowed User".equals(log.getRows().get(0).get("USERNAME"))
                && "Fixture Org A".equals(log.getRows().get(0).get("ORGNAME")), "stock log joins receiver and organization");
        Map<String,Object> ownedLog = service.stockLogDetail(101L, principal);
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement();
                java.sql.ResultSet rows = sql.executeQuery("SELECT L.ID,L.STOCKID,L.LOGTYPE,L.PREVIOUSQTY,L.QTY,L.VENDORID,U.USERNAME,O.ORGNAME FROM T_EMC_CSM_STOCK_LOG L LEFT JOIN T_CORE_USER U ON L.RECEIVEDBYID=U.ID LEFT JOIN T_CORE_ORG O ON L.RECEIVEDBYORGID=O.ID WHERE L.ID=101")) {
            check(rows.next() && rows.getLong("ID") == ((Number) ownedLog.get("ID")).longValue()
                    && rows.getLong("STOCKID") == ((Number) ownedLog.get("STOCKID")).longValue()
                    && rows.getString("LOGTYPE").equals(ownedLog.get("LOGTYPE"))
                    && rows.getDouble("PREVIOUSQTY") == ((Number) ownedLog.get("PREVIOUSQTY")).doubleValue()
                    && rows.getDouble("QTY") == ((Number) ownedLog.get("QTY")).doubleValue()
                    && rows.getString("VENDORID").equals(ownedLog.get("VENDORID"))
                    && rows.getString("USERNAME").equals(ownedLog.get("USERNAME"))
                    && rows.getString("ORGNAME").equals(ownedLog.get("ORGNAME")),
                    "owned stock-log projection matches legacy joined columns");
        }
        check(service.stockLog(13L, QueryRequest.parse("{}", null), principal).getTotal() == 0, "out-of-scope stock log suppression");
        check(service.stockLogDetail(101L, principal) != null && service.stockLogDetail(103L, principal) == null,
                "stock log detail organization scope");
        check(service.list(QueryRequest.parse("{\"p\":{\"f\":{\"org_filter\":\"org-b\"}}}", null), principal).getTotal() == 2,
                "untrusted organization scope hint cannot widen trusted grants");
        try { service.list(QueryRequest.parse("{\"p\":{\"org_filter\":\"org-b\"}}", null), principal); throw new AssertionError("top-level org filter accepted"); }
        catch (IllegalArgumentException expected) { checks++; }
        try { service.list(QueryRequest.parse("{\"p\":{\"unsupported\":\"x\"}}", null), principal); throw new AssertionError("unsupported parameter accepted"); }
        catch (IllegalArgumentException expected) { checks++; }
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement()) {
            sql.execute("UPDATE T_CORE_ROLE_PERMISSION SET TARGETID = '20250800003' WHERE TARGETID = '430201'");
        }
        check(service.list(QueryRequest.parse("{}", null), principal).getTotal() == 2, "read-only consumable menu grant");
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement()) {
            sql.execute("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG = '1' WHERE ID = 1");
        }
        try { service.list(QueryRequest.parse("{}", null), principal); throw new AssertionError("suspended consumable role remained authorized"); }
        catch (org.springframework.security.access.AccessDeniedException expected) { checks++; }
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement()) {
            sql.execute("UPDATE T_CORE_ROLE SET LASTSUSPENDEDFLAG = '0' WHERE ID = 1");
            sql.execute("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID = 1");
        }
        try { service.stockLog(11L, QueryRequest.parse("{}", null), principal); throw new AssertionError("revoked consumable role remained authorized"); }
        catch (org.springframework.security.access.AccessDeniedException expected) { checks++; }
        System.out.println("consumables query service: PASS (" + checks + " checks; synthetic Oracle only)");
    }
}
