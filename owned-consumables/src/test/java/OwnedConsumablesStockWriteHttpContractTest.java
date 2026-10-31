import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Comparator;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.sql.DataSource;

/** Isolated consumable stock write contract; default mode remains fail-closed. */
public final class OwnedConsumablesStockWriteHttpContractTest {
    private static int checks;
    private static final String BASE = "/secure/emc/module/item/consume/consumes";
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    public static void main(String[] args) throws Exception {
        String mode = args.length > 1 ? args[1] : "isolated";
        boolean isolated = "isolated".equalsIgnoreCase(mode);
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        Path root = Files.createTempDirectory("owned-consumable-write-");
        Path audit = root.resolve("audit.jsonl");
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-consumable-write-test",
                "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
                "--spring.datasource.url=" + properties.getProperty("url"),
                "--spring.datasource.username=" + properties.getProperty("user"),
                "--spring.datasource.password=" + properties.getProperty("password"),
                "--sunway.user.password.encoder.type=MD5",
                "--owned.identity.audit.type=file", "--owned.identity.audit.path=" + audit.toAbsolutePath(),
                "--owned.consumables.write.mode=" + mode});
        org.springframework.context.ConfigurableApplicationContext app = com.sunwayworld.owned.internal.context.OwnedApplicationContext.getApplicationContext();
        int port = ((org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext) app).getWebServer().getPort();
        try {
            DataSource source = app.getBean(DataSource.class);
            seed(source);
            String key = http(port, "GET", "/open/security/public-key", null, null, null, 200).body;
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String encrypted = URLEncoder.encode(Base64.getEncoder().encodeToString(cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))), "UTF-8");
            String cookie = http(port, "POST", "/core/module/sys/login", "id=allowed&password=" + encrypted, null, null, 200, key).cookie;
            check(cookie != null, "isolated consumable write login creates session");
            if (!isolated) {
                String deniedBody = "{\"b\":[\"\",[{\"qty\":1}]]}";
                http(port, "POST", BASE, deniedBody, cookie, "application/json", 403);
                http(port, "POST", BASE + "/m-1/stocks", deniedBody, cookie, "application/json", 403);
                http(port, "PUT", BASE + "/m-1/stocks/action/inventory", deniedBody, cookie, "application/json", 403);
                http(port, "PUT", BASE + "/stocks/999/action/recieve", deniedBody, cookie, "application/json", 403);
                http(port, "PUT", BASE + "/m-1/stocks/action/abandon", deniedBody, cookie, "application/json", 403);
                http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-1/general-mtl-stocks", deniedBody, cookie, "application/json", 403);
                http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stocks/201/action/stockin", deniedBody, cookie, "application/json", 403);
                http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks/action/inventory", deniedBody, cookie, "application/json", 403);
                http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks/action/abandon", deniedBody, cookie, "application/json", 403);
                http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stockConcent/calculate", deniedBody, cookie, "application/json", 403);
                http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-1/maintains", deniedBody, cookie, "application/json", 403);
                http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/maintains/8001/maintain-items", deniedBody, cookie, "application/json", 403);
                http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/m-1/maintains", "{\"b\":[\"\",[{\"id\":8001}]]}", cookie, "application/json", 403);
                http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/maintains/8001/maintain-items", "{\"b\":[\"\",[{\"id\":8101}]]}", cookie, "application/json", 403);
                http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks", "{\"b\":[\"\",[{\"id\":999}]]}", cookie, "application/json", 403);
                http(port, "DELETE", BASE + "/m-1/stocks", "{\"b\":[\"\",[{\"id\":999}]]}", cookie, "application/json", 403);
                System.out.println("consumable stock default mode: PASS (" + checks + " checks; write routes denied)");
                return;
            }
            String mdmBody = "{\"b\":[\"\",[{\"batchNo\":\"MDM-W\",\"stockQty\":3.0,\"unitName\":\"box\",\"lowLimit\":1.0,\"qty\":3.0,\"vendorId\":\"mdm-vendor\",\"logContent\":\"mdm general\"}]]}";
            long mdmStockId = Long.parseLong(http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-1/general-mtl-stocks", mdmBody, cookie, "application/json", 200).body.trim());
            check(mdmStockId >= 1000L, "mdm general stock write uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT MTLID,BATCHNO,STOCKQTY,STATUS,CREATEDBYID FROM T_EMC_STOCK WHERE ID=" + mdmStockId)) {
                check(rows.next() && "m-1".equals(rows.getString(1)) && "MDM-W".equals(rows.getString(2)) && rows.getDouble(3) == 3.0d && "0".equals(rows.getString(4)) && "allowed".equals(rows.getString(5)), "mdm general stock stores scoped fields and actor");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_STOCK_LOG WHERE STOCKID=" + mdmStockId + " AND LOGTYPE='添加'")) {
                rows.next(); check(rows.getInt(1) == 1, "mdm general stock writes history log");
            }
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stocks/" + mdmStockId + "/action/stockin", "{\"b\":[\"\",[{\"qty\":2.0,\"logContent\":\"mdm storage\"}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_STOCK WHERE ID=" + mdmStockId)) {
                rows.next(); check(rows.getDouble(1) == 5.0d, "mdm stock-in increments the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_STOCK_LOG WHERE STOCKID=" + mdmStockId + " AND LOGTYPE='入库'")) {
                rows.next(); check(rows.getInt(1) == 1, "mdm stock-in writes history log");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
                statement.execute("INSERT INTO T_EMC_STOCK_CONCENT (ID,STOCKID,CATEGORY,STDVALUE,UNCERTAINTY,UNCERTAINTYPERCENT,CONCENTUNITNAME,REMARK,LOWLIMIT,HIGHLIMIT,MTLMAINTAINID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,REVISIONRULE) VALUES (7001," + mdmStockId + ",'purity','10','1',NULL,'%','fixture',NULL,NULL,NULL,'allowed','Allowed User',SYSTIMESTAMP,'org-a','Fixture Org A','FDA,2')");
                statement.execute("INSERT INTO T_EMC_STOCK_CONCENT (ID,STOCKID,CATEGORY,STDVALUE,UNCERTAINTY,UNCERTAINTYPERCENT,CONCENTUNITNAME,REMARK,LOWLIMIT,HIGHLIMIT,MTLMAINTAINID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,REVISIONRULE) VALUES (7002,901,'purity','5','1',NULL,'%','hidden',NULL,NULL,NULL,'other','Other User',SYSTIMESTAMP,'org-b','Fixture Org B','FDA,2')");
            }
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stockConcent/calculate", "{\"b\":[\"\",[{\"id\":7001,\"stockId\":" + mdmStockId + ",\"stdValue\":\"10\",\"uncertainty\":\"1\",\"revisionRule\":\"FDA,2\"}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT LOWLIMIT,HIGHLIMIT FROM T_EMC_STOCK_CONCENT WHERE ID=7001")) {
                rows.next(); check("9".equals(rows.getString(1)) && "11".equals(rows.getString(2)), "mdm concentration calculation updates limits");
            }
            Reply concentrationPage = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/stocks/" + mdmStockId + "/concents/queries", "{}", cookie, "application/json", 200);
            check(concentrationPage.body.contains("purity") && concentrationPage.body.contains("lowLimit"), "mdm concentration stock query returns projected row");
            Reply concentrationAll = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/stocks/concents/queries", "{}", cookie, "application/json", 200);
            check(concentrationAll.body.contains("purity"), "mdm concentration all-stock query returns scoped row");
            Reply hiddenConcentration = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/stocks/901/concents/queries", "{}", cookie, "application/json", 200);
            check(!hiddenConcentration.body.contains("hidden"), "mdm concentration query suppresses cross-organization row");
            Reply qcStocks = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/queries/qc-std-stock", "{}", cookie, "application/json", 200);
            check(qcStocks.body.contains("MDM material") && qcStocks.body.contains("purity"), "mdm qc standard-stock query returns scoped projection");
            Reply maintenancePage = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-1/maintains/queries", "{}", cookie, "application/json", 200);
            check(maintenancePage.body.contains("quality") && maintenancePage.body.contains("m-1"), "mdm maintenance query returns scoped row");
            Reply hiddenMaintenance = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-2/maintains/queries", "{}", cookie, "application/json", 200);
            check(!hiddenMaintenance.body.contains("hidden-quality"), "mdm maintenance query suppresses cross-organization row");
            Reply maintenanceItems = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/maintains/8001/maintain-items/queries", "{}", cookie, "application/json", 200);
            check(maintenanceItems.body.contains("8101"), "mdm maintenance-item query returns scoped row");
            Reply hiddenMaintenanceItems = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/maintains/8002/maintain-items/queries", "{}", cookie, "application/json", 200);
            check(!hiddenMaintenanceItems.body.contains("8102"), "mdm maintenance-item query suppresses cross-organization row");
            Reply insertedMaintenance = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-1/maintains", "{\"b\":[\"\",[{\"category\":\"new-quality\",\"mtlName\":\"client value\"}]]}", cookie, "application/json", 200);
            long insertedMaintenanceId = Long.parseLong(insertedMaintenance.body.trim()); check(insertedMaintenanceId >= 1000L, "mdm maintenance insert uses owned sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement(); java.sql.ResultSet rows = statement.executeQuery("SELECT MTLID,CATEGORY,MTLNAME FROM T_EMC_MTL_MAINTAIN WHERE ID=" + insertedMaintenanceId)) {
                check(rows.next() && "m-1".equals(rows.getString(1)) && "new-quality".equals(rows.getString(2)) && "MDM material".equals(rows.getString(3)), "mdm maintenance insert stores scoped server-owned fields");
            }
            http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/maintains/" + insertedMaintenanceId + "/maintain-items", "{\"b\":[\"\",[{\"itemId\":103,\"testMethodId\":203,\"methodId\":303}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement(); java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_MTL_MAINTAIN_ITEM WHERE MTLMAITAINID=" + insertedMaintenanceId + " AND ITEMID=103")) {
                rows.next(); check(rows.getInt(1) == 1, "mdm maintenance-item insert stores scoped row");
            }
            long insertedItemId;
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement(); java.sql.ResultSet rows = statement.executeQuery("SELECT ID FROM T_EMC_MTL_MAINTAIN_ITEM WHERE MTLMAITAINID=" + insertedMaintenanceId + " AND ITEMID=103")) {
                rows.next(); insertedItemId = rows.getLong(1);
            }
            http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/maintains/" + insertedMaintenanceId + "/maintain-items", "{\"b\":[\"\",[{\"id\":" + insertedItemId + "}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement(); java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_MTL_MAINTAIN_ITEM WHERE ID=" + insertedItemId)) {
                rows.next(); check(rows.getInt(1) == 0, "mdm maintenance-item delete removes scoped row");
            }
            http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/m-1/maintains", "{\"b\":[\"\",[{\"id\":" + insertedMaintenanceId + "}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement(); java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_MTL_MAINTAIN WHERE ID=" + insertedMaintenanceId)) {
                rows.next(); check(rows.getInt(1) == 0, "mdm maintenance delete removes scoped row");
            }
            http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/m-1/maintains", "{\"b\":[\"\",[{\"id\":8002}]]}", cookie, "application/json", 403);
            http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/maintains/8002/maintain-items", "{\"b\":[\"\",[{\"id\":8102}]]}", cookie, "application/json", 403);
            http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-2/maintains", "{\"b\":[\"\",[{\"category\":\"denied\"}]]}", cookie, "application/json", 403);
            http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/maintains/8002/maintain-items", "{\"b\":[\"\",[{\"itemId\":999}]]}", cookie, "application/json", 403);
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stockConcent/calculate", "{\"b\":[\"\",[{\"id\":7002,\"stockId\":901,\"stdValue\":\"5\",\"uncertainty\":\"1\"}]]}", cookie, "application/json", 403);
            String mdmReceiveBody = "{\"b\":[\"\",[{\"batchNo\":\"MDM-R\",\"qty\":5.0,\"unitName\":\"box\",\"logContent\":\"mdm receive source\"}]]}";
            long mdmReceiveStockId = Long.parseLong(http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-1/general-mtl-stocks", mdmReceiveBody, cookie, "application/json", 200).body.trim());
            check(http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/stocks/" + mdmReceiveStockId + "/action/validate-unique/BATCHNO/MDM-W", "{}", cookie, "application/json", 200).body.contains("false"), "mdm stock uniqueness rejects duplicate batch");
            check(http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/stocks/" + mdmReceiveStockId + "/action/validate-unique/BATCHNO/MDM-UNIQUE", "{}", cookie, "application/json", 200).body.contains("true"), "mdm stock uniqueness accepts new batch");
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stocks/" + mdmReceiveStockId + "/action/recieve", "{\"b\":[\"\",[{\"qty\":2.0,\"logContent\":\"mdm receive\",\"receivedById\":\"allowed\",\"receivedByOrgId\":\"org-a\"}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_STOCK WHERE ID=" + mdmReceiveStockId)) {
                rows.next(); check(rows.getDouble(1) == 3.0d, "mdm receive decrements the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_STOCK_LOG WHERE STOCKID=" + mdmReceiveStockId + " AND LOGTYPE='领取'")) {
                rows.next(); check(rows.getInt(1) == 1, "mdm receive writes history log");
            }
            Reply receivedQcStocks = http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/queries/qc-std-stock?isReceive=1", "{}", cookie, "application/json", 200);
            check(receivedQcStocks.body.contains("MDM-R") && !receivedQcStocks.body.contains("MDM-W"), "mdm qc standard-stock received filter follows owned log");
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stocks/" + mdmReceiveStockId + "/action/recieve", "{\"b\":[\"\",[{\"qty\":99}]]}", cookie, "application/json", 400);
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks/action/abandon", "{\"b\":[\"\",[{\"logContent\":\"mdm receive cleanup\",\"ext$\":{\"stockId\":\"" + mdmReceiveStockId + "\"}}]]}", cookie, "application/json", 200);
            String mdmDeleteBody = "{\"b\":[\"\",[{\"batchNo\":\"MDM-D\",\"qty\":2.0,\"unitName\":\"box\",\"logContent\":\"mdm delete source\"}]]}";
            long mdmDeleteStockId = Long.parseLong(http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/m-1/general-mtl-stocks", mdmDeleteBody, cookie, "application/json", 200).body.trim());
            http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks", "{\"b\":[\"\",[{\"id\":" + mdmDeleteStockId + "}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_STOCK WHERE ID=" + mdmDeleteStockId)) {
                rows.next(); check(rows.getInt(1) == 0, "mdm delete removes the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_STOCK_LOG WHERE STOCKID=" + mdmDeleteStockId)) {
                rows.next(); check(rows.getInt(1) == 0, "mdm delete removes stock history rows");
            }
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/stocks/901/action/recieve", "{\"b\":[\"\",[{\"qty\":1.0}]]}", cookie, "application/json", 403);
            http(port, "POST", "/secure/emc/module/mdm/basemdm/mtls/stocks/901/action/validate-unique/BATCHNO/HIDDEN", "{}", cookie, "application/json", 403);
            http(port, "DELETE", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks", "{\"b\":[\"\",[{\"id\":901}]]}", cookie, "application/json", 403);
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks/action/inventory", "{\"b\":[\"\",[{\"qty\":7.0,\"logContent\":\"mdm inventory\",\"ext$\":{\"stockId\":\"" + mdmStockId + "\"}}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_STOCK WHERE ID=" + mdmStockId)) {
                rows.next(); check(rows.getDouble(1) == 7.0d, "mdm inventory overwrites the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_STOCK_LOG WHERE STOCKID=" + mdmStockId + " AND LOGTYPE='盘存'")) {
                rows.next(); check(rows.getInt(1) == 1, "mdm inventory writes history log");
            }
            http(port, "PUT", "/secure/emc/module/mdm/basemdm/mtls/m-1/stocks/action/abandon", "{\"b\":[\"\",[{\"logContent\":\"mdm abandon\",\"ext$\":{\"stockId\":\"" + mdmStockId + "\"}}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STATUS FROM T_EMC_STOCK WHERE ID=" + mdmStockId)) {
                rows.next(); check("1".equals(rows.getString(1)), "mdm abandon marks the scoped stock discarded");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_STOCK_LOG WHERE STOCKID=" + mdmStockId + " AND LOGTYPE='废弃'")) {
                rows.next(); check(rows.getInt(1) == 1, "mdm abandon writes history log");
            }
            String material = "{\"b\":[\"\",[{\"id\":\"m-3\",\"mtlName\":\"New material\",\"specification\":\"10 ml\",\"mtlCategoryId\":\"glass\",\"harmfulness\":\"normal\",\"productNo\":\"P-003\"}]]}";
            Reply materialReply = http(port, "POST", BASE, material, cookie, "application/json", 200);
            check(materialReply.body.contains("m-3"), "material creation returns the owned material id");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT MTLNAME,SPECIFICATION,STOCKQTY,MTLCATEGORYID,HARMFULNESS,PRODUCTNO,CREATEDBYID,CREATEDBYORGID FROM T_EMC_CONSUME WHERE ID='m-3'")) {
                check(rows.next() && "New material".equals(rows.getString(1)) && "10 ml".equals(rows.getString(2))
                        && rows.getDouble(3) == 0.0d && "glass".equals(rows.getString(4)) && "normal".equals(rows.getString(5))
                        && "P-003".equals(rows.getString(6)) && "allowed".equals(rows.getString(7)) && "org-a".equals(rows.getString(8)), "material creation stores scoped fields and actor");
            }
            String body = "{\"b\":[\"\",[{\"qty\":12.5,\"vendorId\":\"vendor-1\",\"price\":3.5,\"logContent\":\"initial stock\",\"ext$\":{\"batchno\":\"B-1\",\"unitname\":\"box\"}}]]}";
            Reply inserted = http(port, "POST", BASE + "/m-1/stocks", body, cookie, "application/json", 200);
            long stockId = Long.parseLong(inserted.body.trim());
            check(stockId >= 1000L, "stock write uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT MTLID,BATCHNO,STOCKQTY,UNITNAME,STATUS,CREATEDBYID FROM T_EMC_CSM_STOCK WHERE ID=" + stockId)) {
                check(rows.next() && "m-1".equals(rows.getString(1)) && "B-1".equals(rows.getString(2))
                        && rows.getDouble(3) == 12.5d && "box".equals(rows.getString(4)) && "0".equals(rows.getString(5))
                        && "allowed".equals(rows.getString(6)), "stock write stores scoped fields and actor");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT LOGTYPE,STOCKID,QTY,CREATEDBYID FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=" + stockId)) {
                check(rows.next() && "添加".equals(rows.getString(1)) && rows.getLong(2) == stockId
                        && rows.getDouble(3) == 12.5d && "allowed".equals(rows.getString(4)), "stock creation writes history log");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CONSUME WHERE ID='m-1'")) {
                rows.next(); check(rows.getDouble(1) == 12.5d, "material aggregate stock quantity is updated");
            }
            String storage = "{\"b\":[\"\",[{\"qty\":5.0,\"vendorId\":\"vendor-2\",\"price\":4.0,\"logContent\":\"stock in\"}]]}";
            http(port, "PUT", BASE + "/stocks/" + stockId + "/action/stockin", storage, cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CSM_STOCK WHERE ID=" + stockId)) {
                rows.next(); check(rows.getDouble(1) == 17.5d, "stock-in increments the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=" + stockId + " AND LOGTYPE='入库'")) {
                rows.next(); check(rows.getInt(1) == 1, "stock-in appends one history log");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CONSUME WHERE ID='m-1'")) {
                rows.next(); check(rows.getDouble(1) == 17.5d, "stock-in updates material aggregate stock quantity");
            }
            String inventory = "{\"b\":[\"\",[{\"qty\":20.0,\"logContent\":\"inventory\",\"ext$\":{\"stockId\":\"" + stockId + "\"}}]]}";
            http(port, "PUT", BASE + "/m-1/stocks/action/inventory", inventory, cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CSM_STOCK WHERE ID=" + stockId)) {
                rows.next(); check(rows.getDouble(1) == 20.0d, "inventory overwrites the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=" + stockId + " AND LOGTYPE='盘存'")) {
                rows.next(); check(rows.getInt(1) == 1, "inventory appends one history log");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CONSUME WHERE ID='m-1'")) {
                rows.next(); check(rows.getDouble(1) == 20.0d, "inventory updates material aggregate stock quantity");
            }
            String receive = "{\"b\":[\"\",[{\"qty\":3.0,\"logContent\":\"receive\",\"receivedById\":\"allowed\",\"receivedByOrgId\":\"org-a\"}]]}";
            http(port, "PUT", BASE + "/stocks/" + stockId + "/action/recieve", receive, cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CSM_STOCK WHERE ID=" + stockId)) {
                rows.next(); check(rows.getDouble(1) == 17.0d, "receive decrements the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=" + stockId + " AND LOGTYPE='领取'")) {
                rows.next(); check(rows.getInt(1) == 1, "receive appends one history log");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CONSUME WHERE ID='m-1'")) {
                rows.next(); check(rows.getDouble(1) == 17.0d, "receive updates material aggregate stock quantity");
            }
            http(port, "PUT", BASE + "/stocks/" + stockId + "/action/recieve", "{\"b\":[\"\",[{\"qty\":99}]]}", cookie, "application/json", 400);
            http(port, "PUT", BASE + "/m-2/stocks/action/inventory", inventory, cookie, "application/json", 403);
            http(port, "PUT", BASE + "/stocks/999/action/stockin", storage, cookie, "application/json", 403);
            http(port, "POST", BASE + "/m-2/stocks", body, cookie, "application/json", 403);
            http(port, "POST", BASE + "/m-1/stocks", "{\"b\":[\"\",[{\"qty\":-1}]]}", cookie, "application/json", 400);
            String secondBody = "{\"b\":[\"\",[{\"qty\":4.0,\"logContent\":\"second stock\",\"ext$\":{\"batchno\":\"B-2\"}}]]}";
            long secondStockId = Long.parseLong(http(port, "POST", BASE + "/m-1/stocks", secondBody, cookie, "application/json", 200).body.trim());
            String abandon = "{\"b\":[\"\",[{\"logContent\":\"abandon\",\"ext$\":{\"stockId\":\"" + stockId + "\"}}]]}";
            http(port, "PUT", BASE + "/m-1/stocks/action/abandon", abandon, cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STATUS FROM T_EMC_CSM_STOCK WHERE ID=" + stockId)) {
                rows.next(); check("1".equals(rows.getString(1)), "abandon marks the scoped stock as discarded");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=" + stockId + " AND LOGTYPE='废弃'")) {
                rows.next(); check(rows.getInt(1) == 1, "abandon appends one history log");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CONSUME WHERE ID='m-1'")) {
                rows.next(); check(rows.getDouble(1) == 4.0d, "abandon excludes discarded stock from material aggregate");
            }
            String delete = "{\"b\":[\"\",[{\"id\":" + secondStockId + "}]]}";
            http(port, "DELETE", BASE + "/m-1/stocks", delete, cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_CSM_STOCK WHERE ID=" + secondStockId)) {
                rows.next(); check(rows.getInt(1) == 0, "delete removes the scoped stock row");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=" + secondStockId)) {
                rows.next(); check(rows.getInt(1) == 0, "delete removes the stock history rows");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT STOCKQTY FROM T_EMC_CONSUME WHERE ID='m-1'")) {
                rows.next(); check(rows.getDouble(1) == 0.0d, "delete updates material aggregate stock quantity");
            }
            String low = http(port, "POST", "/core/module/sys/login", "id=low&password=" + encrypted, null, null, 200, key).cookie;
            http(port, "POST", BASE + "/m-1/stocks", body, low, "application/json", 403);
            http(port, "POST", BASE, material, low, "application/json", 403);
            http(port, "PUT", BASE + "/m-1/stocks/action/abandon", abandon, low, "application/json", 403);
            http(port, "DELETE", BASE + "/m-1/stocks", delete, low, "application/json", 403);
            String records = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
            check(records.contains("consumable-material-insert") && records.contains("consumable-stock-insert") && records.contains("consumable-stock-storage") && records.contains("consumable-stock-inventory") && records.contains("consumable-stock-receive") && records.contains("consumable-stock-abandon") && records.contains("consumable-stock-delete") && records.contains("mdm-stock-delete") && records.contains("mdm-stock-concentration-calculate") && records.contains("mdm-maintenance-insert") && records.contains("mdm-maintenance-item-insert") && records.contains("mdm-maintenance-delete") && records.contains("mdm-maintenance-item-delete"), "consumable writes are audited");
            check(!records.contains("secret") && !records.contains(properties.getProperty("password")), "write audit omits credentials");
        } finally {
            app.close();
            Files.walk(root).sorted(Comparator.reverseOrder()).forEach(path -> { try { Files.deleteIfExists(path); } catch (IOException failure) { throw new java.io.UncheckedIOException(failure); } });
        }
        System.out.println("consumable stock write: PASS (" + checks + " checks; isolated Oracle loopback only)");
    }

    private static void seed(DataSource source) throws SQLException {
        try (Connection c = source.getConnection(); Statement s = c.createStatement()) {
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
            s.execute("CREATE TABLE T_EMC_CONSUME (ID VARCHAR2(64) PRIMARY KEY, MTLNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), STOCKQTY NUMBER, MTLCATEGORYID VARCHAR2(64), HARMFULNESS VARCHAR2(36), PRODUCTNO VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_CSM_STOCK (ID NUMBER PRIMARY KEY, MTLID VARCHAR2(64), BATCHNO VARCHAR2(100), STOCKQTY NUMBER, UNITNAME VARCHAR2(64), EXPIREDATE DATE, LOWLIMIT NUMBER, STATUS VARCHAR2(1), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_CSM_STOCK_LOG (ID NUMBER PRIMARY KEY, STOCKID NUMBER, LOGTYPE VARCHAR2(20), PREVIOUSQTY NUMBER, QTY NUMBER, VENDORID VARCHAR2(64), PRICE NUMBER, LOGCONTENT VARCHAR2(200), RECEIVEDBYID VARCHAR2(64), RECEIVEDBYORGID VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_MTL (ID VARCHAR2(64) PRIMARY KEY, MTLNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), STOCKQTY NUMBER, MTLCATEGORYID NUMBER, HARMFULNESS VARCHAR2(36), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), ORGID VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_MTL_CATEGORY (ID NUMBER PRIMARY KEY, MANAGEMENTTYPE VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_STOCK (ID NUMBER PRIMARY KEY, MTLID VARCHAR2(64), BATCHNO VARCHAR2(100), STOCKQTY NUMBER, UNITNAME VARCHAR2(64), EXPIREDATE TIMESTAMP, LOWLIMIT NUMBER, STATUS VARCHAR2(1), FINENESS VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), VERDORNAME VARCHAR2(100), REMINDDAYS NUMBER)");
            s.execute("CREATE TABLE T_EMC_STOCK_LOG (ID NUMBER PRIMARY KEY, STOCKID NUMBER, LOGTYPE VARCHAR2(36), PREVIOUSQTY NUMBER, QTY NUMBER, VENDORID VARCHAR2(64), PRICE NUMBER, LOGCONTENT VARCHAR2(200), RECEIVEDBYID VARCHAR2(64), RECEIVEDBYORGID VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_STOCK_CONCENT (ID NUMBER PRIMARY KEY, STOCKID NUMBER, CATEGORY VARCHAR2(64), STDVALUE VARCHAR2(100), UNCERTAINTY VARCHAR2(100), UNCERTAINTYPERCENT VARCHAR2(100), CONCENTUNITNAME VARCHAR2(64), REMARK VARCHAR2(200), LOWLIMIT VARCHAR2(100), HIGHLIMIT VARCHAR2(100), MTLMAINTAINID NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100), REVISIONRULE VARCHAR2(64))");
            s.execute("CREATE TABLE T_EMC_MTL_MAINTAIN (ID NUMBER PRIMARY KEY, MTLID VARCHAR2(64), CATEGORY VARCHAR2(64), MTLNAME VARCHAR2(100), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE TABLE T_EMC_MTL_MAINTAIN_ITEM (ID NUMBER PRIMARY KEY, MTLMAITAINID NUMBER, ITEMID NUMBER, TESTMETHODID NUMBER, METHODID NUMBER, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            s.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('430201')"); s.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('allowed','Allowed User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_USER VALUES ('low','Low User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')"); s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','430201')"); s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
            s.execute("INSERT INTO T_EMC_CONSUME VALUES ('m-1','Fixture material',NULL,0,NULL,NULL,NULL,'allowed','Allowed User',SYSTIMESTAMP,'org-a','Fixture Org A')");
            s.execute("INSERT INTO T_EMC_CONSUME VALUES ('m-2','Hidden material',NULL,0,NULL,NULL,NULL,'other','Other User',SYSTIMESTAMP,'org-b','Fixture Org B')");
            s.execute("INSERT INTO T_EMC_MTL_CATEGORY VALUES (1,'QC')"); s.execute("INSERT INTO T_EMC_MTL_CATEGORY VALUES (2,'QC')");
            s.execute("INSERT INTO T_EMC_MTL VALUES ('m-1','MDM material',NULL,0,1,'normal','allowed','Allowed User',SYSTIMESTAMP,'org-a','Fixture Org A','org-a')");
            s.execute("INSERT INTO T_EMC_MTL VALUES ('m-2','Hidden MDM material',NULL,0,1,'normal','other','Other User',SYSTIMESTAMP,'org-b','Fixture Org B','org-b')");
            s.execute("INSERT INTO T_EMC_STOCK VALUES (901,'m-2','HIDDEN',4,'box',SYSTIMESTAMP,1,'0','99.9','other','Other User',SYSTIMESTAMP,'org-b','Fixture Org B','Vendor B',30)");
            s.execute("INSERT INTO T_EMC_MTL_MAINTAIN VALUES (8001,'m-1','quality','Fixture quality','allowed','Allowed User',SYSTIMESTAMP,'org-a','Fixture Org A')");
            s.execute("INSERT INTO T_EMC_MTL_MAINTAIN VALUES (8002,'m-2','hidden-quality','Hidden quality','other','Other User',SYSTIMESTAMP,'org-b','Fixture Org B')");
            s.execute("INSERT INTO T_EMC_MTL_MAINTAIN_ITEM VALUES (8101,8001,101,201,301,'allowed','Allowed User',SYSTIMESTAMP,'org-a','Fixture Org A')");
            s.execute("INSERT INTO T_EMC_MTL_MAINTAIN_ITEM VALUES (8102,8002,102,202,302,'other','Other User',SYSTIMESTAMP,'org-b','Fixture Org B')");
        }
    }

    private static Reply http(int port, String method, String path, String body, String cookie, String type, int expected) throws Exception { return http(port, method, path, body, cookie, type, expected, null); }
    private static Reply http(int port, String method, String path, String body, String cookie, String type, int expected, String key) throws Exception {
        HttpURLConnection c = (HttpURLConnection)new URL("http://127.0.0.1:" + port + path).openConnection(); c.setRequestMethod(method); c.setConnectTimeout(5000); c.setReadTimeout(10000); c.setInstanceFollowRedirects(false);
        if (cookie != null) c.setRequestProperty("Cookie", cookie); if (key != null) c.setRequestProperty("SECURITY-RSA-KEY", key);
        if (body != null) { c.setDoOutput(true); c.setRequestProperty("Content-Type", type == null ? "application/x-www-form-urlencoded" : type); c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8)); }
        int status = c.getResponseCode(); java.io.InputStream stream = status >= 400 ? c.getErrorStream() : c.getInputStream(); String text = stream == null ? "" : new String(read(stream), StandardCharsets.UTF_8);
        check(status == expected, method + " " + path + " HTTP " + expected + " actual=" + status + " body=" + text); String set = c.getHeaderField("Set-Cookie"); return new Reply(status, text, set == null ? cookie : set.split(";",2)[0]);
    }
    private static byte[] read(java.io.InputStream input) throws IOException { java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(); byte[] buffer = new byte[4096]; int n; while ((n=input.read(buffer))!=-1) out.write(buffer,0,n); return out.toByteArray(); }
    private static final class Reply { final int status; final String body,cookie; Reply(int status,String body,String cookie){this.status=status;this.body=body;this.cookie=cookie;} }
}
