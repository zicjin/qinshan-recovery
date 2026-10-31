import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
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

/** Isolated equipment test-method write contract; default mode remains fail-closed. */
public final class OwnedEquipmentTestMethodWriteHttpContractTest {
    private static int checks;
    private static final String BASE = "/secure/emc/module/mdm/equipt/equipts";

    private static void check(boolean value, String label) {
        checks++;
        if (!value) throw new AssertionError(label);
    }

    private static int countOccurrences(String text, String needle) {
        int count = 0;
        for (int offset = 0; (offset = text.indexOf(needle, offset)) >= 0; offset += needle.length()) count++;
        return count;
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) { properties.load(input); }
        Path root = Files.createTempDirectory("owned-equipment-write-");
        Path audit = root.resolve("audit.jsonl");
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
                "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-equipment-write-test",
                "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
                "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
                "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
                "--spring.datasource.url=" + properties.getProperty("url"),
                "--spring.datasource.username=" + properties.getProperty("user"),
                "--spring.datasource.password=" + properties.getProperty("password"),
                "--sunway.user.password.encoder.type=MD5",
                "--owned.identity.audit.type=file", "--owned.identity.audit.path=" + audit.toAbsolutePath(),
                "--owned.equipment.write.mode=isolated"});
        org.springframework.context.ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
        int port = ((org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext) app).getWebServer().getPort();
        try {
            DataSource source = app.getBean(DataSource.class);
            seedWriteFixture(source);
            String key = http(port, "GET", "/open/security/public-key", null, null, null, 200).body;
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String encrypted = URLEncoder.encode(Base64.getEncoder().encodeToString(
                    cipher.doFinal("secret".getBytes(StandardCharsets.UTF_8))), "UTF-8");
            String cookie = http(port, "POST", "/core/module/sys/login", "id=allowed&password=" + encrypted,
                    null, null, 200, key).cookie;
            check(cookie != null, "isolated equipment write login creates session");

            String maintBody = "{\"b\":[\"\",[{\"maintEvent\":\"equiptCalibrationExtra\",\"eventDesc\":\"Fixture extra event\",\"warningDays\":21,\"sameDay\":\"1\"}]]}";
            Reply maintInsert = http(port, "POST", "/secure/emc/module/mdm/basemdm/equipt-categories/11/maints", maintBody, cookie, "application/json", 200);
            long maintId = Long.parseLong(maintInsert.body.trim());
            JSONObject maints = JSON.parseObject(http(port, "POST", "/secure/emc/module/mdm/basemdm/equipt-categories/11/maints/queries", "{\"p\":{\"n\":1,\"s\":20}}", cookie, "application/json", 200).body);
            check(maints.getIntValue("total") == 3 && "equiptCalibrationExtra".equals(maints.getJSONArray("rows").getJSONObject(0).getString("maintEvent")),
                    "equipment-category maintenance API returns scoped DTOs and new event");
            Reply warningDays = http(port, "POST", BASE + "/" + maintId + "/maint-warningdays/queries", "{}", cookie, "application/json", 200);
            check("21".equals(warningDays.body.trim()), "inherited equipment warning-days route reads the owned maintenance service");
            http(port, "POST", "/secure/emc/module/mdm/basemdm/equipt-categories/12/maints", maintBody, cookie, "application/json", 403);
            http(port, "DELETE", "/secure/emc/module/mdm/basemdm/equipt-categories/11/maints", "{\"b\":[\"\",[{\"id\":" + maintId + "}]]}", cookie, "application/json", 200);

            JSONObject choosable = JSON.parseObject(http(port, "POST", BASE + "/queries/choosable",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(choosable.getIntValue("total") == 1 && choosable.getJSONArray("rows").size() == 1
                    && "EQ-001".equals(choosable.getJSONArray("rows").getJSONObject(0).getString("equiptId")),
                    "generic equipment choosable query returns scoped equipment");
            JSONObject dcu = JSON.parseObject(http(port, "GET", BASE + "/1/DCU-confs/queries",
                    null, cookie, null, 200).body);
            check(dcu.getLongValue("id") == 1L && dcu.getLongValue("acqScriptId") == 701L
                    && "serial".equals(dcu.getString("interfaceCategory"))
                    && "9600".equals(dcu.getString("baudRate")),
                    "equipment DCU configuration detail returns scoped DTO projection");

            JSONObject overdue = JSON.parseObject(http(port, "POST", BASE + "/action/select-equipt-maintain-repair-search",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(overdue.getIntValue("total") == 1 && overdue.getJSONArray("rows").size() == 1
                    && "EQ-001".equals(overdue.getJSONArray("rows").getJSONObject(0).getString("equiptNo"))
                    && "fixture maintenance".equals(overdue.getJSONArray("rows").getJSONObject(0).getString("maintainRepairConcent")),
                    "maintenance-repair summary applies organization scope and original DTO projection");
            JSONObject users = JSON.parseObject(http(port, "POST", BASE + "/action/select-equipt-user",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(users.getIntValue("total") == 1 && users.getJSONArray("rows").size() == 1
                    && "EQ-001".equals(users.getJSONArray("rows").getJSONObject(0).getString("equiptNo"))
                    && "Allowed User".equals(users.getJSONArray("rows").getJSONObject(0).getString("users")),
                    "equipment-user summary applies organization scope and original DTO projection");

            String planBody = "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":51,\"cycle\":\"annual\",\"warningDays\":14,\"orgId\":\"org-b\",\"remark\":\"fixture plan\"}]]}";
            Reply plan = http(port, "POST", BASE + "/plans", planBody, cookie, "application/json", 200);
            long planId = Long.parseLong(plan.body.trim());
            check(planId >= 1000L, "equipment plan uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT EQUIPTID,MAINTID,CYCLE,WARNINGDAYS,ORGID,CREATEDBYID,CREATEDBYORGID FROM T_EMC_EQUIPT_PLAN WHERE ID=" + planId)) {
                check(rows.next() && rows.getLong(1) == 1L && rows.getLong(2) == 51L && "annual".equals(rows.getString(3))
                        && rows.getLong(4) == 14L && "org-a".equals(rows.getString(5)) && "allowed".equals(rows.getString(6))
                        && "org-a".equals(rows.getString(7)), "equipment plan stores scoped fields and session actor");
            }
            JSONObject plans = JSON.parseObject(http(port, "POST", BASE + "/1/plans/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(plans.getIntValue("total") == 1 && plans.getJSONArray("rows").size() == 1
                    && plans.getJSONArray("rows").getJSONObject(0).getLongValue("maintId") == 51L,
                    "equipment plan query returns scoped plan");
            JSONObject planSummary = JSON.parseObject(http(port, "POST", BASE + "/action/select-equipt-plan",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(planSummary.getIntValue("total") == 1 && planSummary.getJSONArray("rows").size() == 1
                    && "EQ-001".equals(planSummary.getJSONArray("rows").getJSONObject(0).getString("equiptNo"))
                    && "annual".equals(planSummary.getJSONArray("rows").getJSONObject(0).getString("cycle"))
                    && "Allowed User".equals(planSummary.getJSONArray("rows").getJSONObject(0).getString("keepUserName")),
                    "equipment-plan summary applies organization scope and original DTO projection");
            JSONObject caliChoose = JSON.parseObject(http(port, "POST", BASE + "/cali-queries/choosable",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(caliChoose.getIntValue("total") == 1 && caliChoose.getJSONArray("rows").size() == 1
                    && "EQ-001".equals(caliChoose.getJSONArray("rows").getJSONObject(0).getString("equiptId"))
                    && "Calibration".equals(caliChoose.getJSONArray("rows").getJSONObject(0).getJSONObject("ext$").getString("codename")),
                    "calibration choice query returns scoped equipment and maintenance projection");
            JSONObject verifyChoose = JSON.parseObject(http(port, "POST", BASE + "/verify-queries/choosable",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            JSONObject perCheckChoose = JSON.parseObject(http(port, "POST", BASE + "/per-check-queries/choosable",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(verifyChoose.getIntValue("total") == 0 && perCheckChoose.getIntValue("total") == 0,
                    "empty verification and period-check choice queries stay scoped");
            http(port, "POST", BASE + "/plans", "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":52}]]}", cookie, "application/json", 403);
            JSONObject hiddenPlans = JSON.parseObject(http(port, "POST", BASE + "/2/plans/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(hiddenPlans.getIntValue("total") == 0, "equipment plan query suppresses out-of-scope equipment");

            String perCheckPlanBody = "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":51,\"maintDate\":\"2026-02-01\",\"cycle\":\"2\",\"warningDays\":7,\"checkReason\":\"ISO\",\"checkEquipt\":\"Reference\",\"remark\":\"period fixture\",\"checkWay\":\"visual\",\"checkUserId\":\"allowed\",\"checkUserName\":\"Allowed User\"}]]}";
            Reply perCheckPlan = http(port, "POST", BASE + "/per-check-plan", perCheckPlanBody, cookie, "application/json", 200);
            long perCheckPlanId = Long.parseLong(perCheckPlan.body.trim());
            check(perCheckPlanId >= 1000L, "period-check plan uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT EQUIPTID,MAINTID,CYCLE,WARNINGDAYS,CHECKREASON,CHECKEQUIPT,CREATEDBYID,CREATEDBYORGID FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=" + perCheckPlanId)) {
                check(rows.next() && rows.getLong(1) == 1L && rows.getLong(2) == 51L && "2".equals(rows.getString(3))
                        && rows.getLong(4) == 7L && "ISO".equals(rows.getString(5)) && "Reference".equals(rows.getString(6))
                        && "allowed".equals(rows.getString(7)) && "org-a".equals(rows.getString(8)), "period-check plan stores scoped fields and actor");
            }
            JSONObject perCheckPlans = JSON.parseObject(http(port, "POST", BASE + "/1/per-check-plan/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(perCheckPlans.getIntValue("total") == 1 && perCheckPlans.getJSONArray("rows").getJSONObject(0).getLongValue("maintId") == 51L
                    && "equiptCalibration".equals(perCheckPlans.getJSONArray("rows").getJSONObject(0).getString("maintEvent"))
                    && "Calibration".equals(perCheckPlans.getJSONArray("rows").getJSONObject(0).getJSONObject("ext$").getString("codename")), "period-check plan query returns scoped row and code label");
            JSONObject perCheckSummary = JSON.parseObject(http(port, "POST", BASE + "/action/select-equipt-per-check-search",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(perCheckSummary.getIntValue("total") == 1 && perCheckSummary.getJSONArray("rows").size() == 1
                    && "EQ-001".equals(perCheckSummary.getJSONArray("rows").getJSONObject(0).getString("equiptNo"))
                    && "visual".equals(perCheckSummary.getJSONArray("rows").getJSONObject(0).getString("checkWay"))
                    && "ISO".equals(perCheckSummary.getJSONArray("rows").getJSONObject(0).getString("checkReason"))
                    && perCheckSummary.getJSONArray("rows").getJSONObject(0).getString("maintDate").startsWith("2026-02-01"),
                    "period-check summary applies organization scope and original DTO projection");
            http(port, "POST", BASE + "/per-check-plan", "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":52}]]}", cookie, "application/json", 403);
            check(JSON.parseObject(http(port, "POST", BASE + "/2/per-check-plan/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body).getIntValue("total") == 0,
                    "period-check plan query suppresses out-of-scope equipment");
            String secondPlanBody = "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":53,\"cycle\":\"3\",\"warningDays\":9,\"checkReason\":\"SECOND\",\"checkEquipt\":\"Reference-2\",\"remark\":\"period fixture 2\"}]]}";
            Reply secondPlan = http(port, "POST", BASE + "/per-check-plan", secondPlanBody, cookie, "application/json", 200);
            long secondPlanId = Long.parseLong(secondPlan.body.trim());
            JSONObject filteredPlans = JSON.parseObject(http(port, "POST", BASE + "/1/per-check-plan/queries",
                    "{\"p\":{\"f\":{\"CHECKREASON_CISC\":\"second\"},\"o\":[{\"WARNINGDAYS\":\"asc\"}],\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(filteredPlans.getIntValue("total") == 1 && filteredPlans.getJSONArray("rows").getJSONObject(0).getLongValue("id") == secondPlanId,
                    "period-check plan query applies filter and order");

            String perCheckRecordBody = "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":51,\"cycle\":\"2\",\"checkReason\":\"ISO\",\"checkEquipt\":\"Reference\",\"checkUsers\":\"allowed\",\"checkUsernames\":\"Allowed User\",\"remark\":\"period record\"}]]}";
            Reply perCheckRecord = http(port, "POST", BASE + "/per-check-record", perCheckRecordBody, cookie, "application/json", 200);
            long perCheckRecordId = Long.parseLong(perCheckRecord.body.trim());
            check(perCheckRecordId >= 1000L, "period-check record uses owned Oracle sequence");
            JSONObject perCheckRecords = JSON.parseObject(http(port, "POST", BASE + "/1/per-check-record/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(perCheckRecords.getIntValue("total") == 1 && "1".equals(perCheckRecords.getJSONArray("rows").getJSONObject(0).getString("completeFlag"))
                    && "equiptCalibration".equals(perCheckRecords.getJSONArray("rows").getJSONObject(0).getString("maintEvent"))
                    && "Calibration".equals(perCheckRecords.getJSONArray("rows").getJSONObject(0).getJSONObject("ext$").getString("codename")), "period-check record query returns incomplete row and code label");
            String selectableBody = http(port, "GET", BASE + "/1/per-check-records/maints/queries/selectable", null, cookie, null, 200).body;
            check(JSON.parseArray(selectableBody).size() == 2
                    && "Calibration".equals(JSON.parseArray(selectableBody).getJSONObject(0).getString("maintEvent")),
                    "period-check selectable maintenance list follows authorized plans and code labels");
            String perCheckComplete = "{\"b\":[\"\",[{\"id\":" + perCheckRecordId + ",\"equiptId\":1,\"maintId\":51,\"maintDate\":\"2026-10-20\",\"cycle\":\"2\",\"checkReason\":\"ISO-updated\",\"checkEquipt\":\"Reference-updated\",\"checkUsers\":\"allowed\",\"checkUsernames\":\"Allowed User\",\"remark\":\"completed period\"}]]}";
            check(http(port, "PUT", BASE + "/per-check-records/action/complete", perCheckComplete, cookie, "application/json", 200).body.trim().isEmpty(),
                    "period-check completion commits through original route");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COMPLETEFLAG,MAINTDATE,CHECKREASON FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=" + perCheckRecordId)) {
                check(rows.next() && "2".equals(rows.getString(1)) && "2026-10-20".equals(rows.getDate(2).toString()) && "ISO-updated".equals(rows.getString(3)),
                        "completed period-check record stores completion state");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT CYCLE,MAINTDATE,MAINTENDDATE,CHECKREASON,CHECKEQUIPT,REMARK FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=" + perCheckPlanId)) {
                check(rows.next() && "2".equals(rows.getString(1)) && "2026-10-20".equals(rows.getDate(2).toString())
                        && "2026-10-26".equals(rows.getDate(3).toString()) && "ISO-updated".equals(rows.getString(4))
                        && "Reference-updated".equals(rows.getString(5)) && "completed period".equals(rows.getString(6)), "period-check completion updates linked plan");
            }
            http(port, "PUT", BASE + "/per-check-records/action/complete", perCheckComplete, cookie, "application/json", 400);
            http(port, "DELETE", BASE + "/per-check-record", "{\"b\":[\"\",[{\"id\":" + perCheckRecordId + "}]]}", cookie, "application/json", 400);
            http(port, "DELETE", BASE + "/per-check-plan", "{\"b\":[\"\",[{\"id\":" + perCheckPlanId + "}]]}", cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT_PER_CHECK_PLAN WHERE ID=" + perCheckPlanId)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted period-check plan is absent");
            }
            String secondRecordBody = "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":53,\"cycle\":\"3\",\"checkReason\":\"SECOND\",\"checkEquipt\":\"Reference-2\",\"checkUsers\":\"allowed\",\"checkUsernames\":\"Allowed User\",\"remark\":\"period record 2\"}]]}";
            Reply secondRecord = http(port, "POST", BASE + "/per-check-record", secondRecordBody, cookie, "application/json", 200);
            long secondRecordId = Long.parseLong(secondRecord.body.trim());
            String invalidCycle = "{\"b\":[\"\",[{\"id\":" + secondRecordId + ",\"equiptId\":1,\"maintId\":53,\"maintDate\":\"2026-10-20\",\"cycle\":\"annual\"}]]}";
            http(port, "PUT", BASE + "/per-check-records/action/complete", invalidCycle, cookie, "application/json", 400);
            String missingCycle = "{\"b\":[\"\",[{\"id\":" + secondRecordId + ",\"equiptId\":1,\"maintId\":53,\"maintDate\":\"2026-10-20\"}]]}";
            http(port, "PUT", BASE + "/per-check-records/action/complete", missingCycle, cookie, "application/json", 400);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COMPLETEFLAG FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=" + secondRecordId)) {
                check(rows.next() && "1".equals(rows.getString(1)), "invalid or missing cycle leaves period-check record incomplete");
            }
            String auditBeforeBatch = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
            int completionAuditsBeforeBatch = countOccurrences(auditBeforeBatch, "equipment-per-check-record-complete");
            String duplicateBatch = "{\"b\":[\"\",[{\"id\":" + secondRecordId + ",\"equiptId\":1,\"maintId\":53,\"maintDate\":\"2026-10-20\",\"cycle\":\"3\"},{\"id\":" + secondRecordId + ",\"equiptId\":1,\"maintId\":53,\"maintDate\":\"2026-10-20\",\"cycle\":\"3\"}]]}";
            http(port, "PUT", BASE + "/per-check-records/action/complete", duplicateBatch, cookie, "application/json", 400);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COMPLETEFLAG FROM T_EMC_EQUIPT_PER_CHECK_RECORD WHERE ID=" + secondRecordId)) {
                check(rows.next() && "1".equals(rows.getString(1)), "failed completion batch rolls back every record update");
            }
            String auditAfterBatch = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
            check(countOccurrences(auditAfterBatch, "equipment-per-check-record-complete") == completionAuditsBeforeBatch,
                    "failed completion batch emits no allowed audit");
            String validSecondComplete = "{\"b\":[\"\",[{\"id\":" + secondRecordId + ",\"equiptId\":1,\"maintId\":53,\"maintDate\":\"2026-10-20\",\"cycle\":\"3\",\"checkReason\":\"SECOND-done\"}]]}";
            http(port, "PUT", BASE + "/per-check-records/action/complete", validSecondComplete, cookie, "application/json", 200);

            String recordBody = "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":51,\"verifyOrg\":\"Fixture Lab\",\"certNo\":\"CERT-1\",\"certCategory\":\"calibration\",\"status\":\"ok\",\"remark\":\"fixture record\"}]]}";
            Reply record = http(port, "POST", BASE + "/records", recordBody, cookie, "application/json", 200);
            long recordId = Long.parseLong(record.body.trim());
            check(recordId >= 1000L, "equipment maintenance record uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT EQUIPTID,MAINTID,VERIFYORG,CERTNO,CERTCATEGORY,COMPLETEFLAG,CREATEDBYID,CREATEDBYORGID FROM T_EMC_EQUIPT_RECORD WHERE ID=" + recordId)) {
                check(rows.next() && rows.getLong(1) == 1L && rows.getLong(2) == 51L && "Fixture Lab".equals(rows.getString(3))
                        && "CERT-1".equals(rows.getString(4)) && "calibration".equals(rows.getString(5))
                        && "1".equals(rows.getString(6)) && "allowed".equals(rows.getString(7)) && "org-a".equals(rows.getString(8)),
                        "equipment maintenance record stores scoped fields and incomplete actor");
            }
            JSONObject recordPage = JSON.parseObject(http(port, "POST", BASE + "/1/records/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(recordPage.getIntValue("total") == 1 && recordPage.getJSONArray("rows").size() == 1
                    && recordPage.getJSONArray("rows").getJSONObject(0).getLongValue("maintId") == 51L
                    && "equiptCalibration".equals(recordPage.getJSONArray("rows").getJSONObject(0).getString("maintEvent")),
                    "equipment maintenance record query returns scoped record");
            check(JSON.parseArray(http(port, "GET", BASE + "/1/records/maints/queries/selectable", null, cookie, null, 200).body).size() == 1,
                    "equipment maintenance selectable list follows authorized plans");
            String completeBody = "{\"b\":[\"\",[{\"id\":" + recordId + ",\"equiptId\":1,\"maintId\":51,\"completeFlag\":\"1\",\"mainDate\":\"2026-10-20\",\"maintEndDate\":\"2027-10-20\",\"verifyOrg\":\"Updated Lab\",\"certCategory\":\"calibration\",\"remark\":\"completed\",\"ext$\":{\"cycle\":\"annual\",\"gist\":\"ISO-17025\"}}]]}";
            check(http(port, "PUT", BASE + "/records/action/complete", completeBody, cookie, "application/json", 200).body.trim().isEmpty(),
                    "maintenance record completion commits through original route");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COMPLETEFLAG,MAINDATE,MAINTENDDATE,VERIFYORG,CERTCATEGORY,REMARK FROM T_EMC_EQUIPT_RECORD WHERE ID=" + recordId)) {
                check(rows.next() && "2".equals(rows.getString(1)),
                        "completed record stores the supplier completion state");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT CYCLE,MAINTDATE,MAINTENDDATE,VERIFYORG,GIST,CERTCATEGORY,REMARK FROM T_EMC_EQUIPT_PLAN WHERE EQUIPTID=1 AND MAINTID=51")) {
                check(rows.next() && "annual".equals(rows.getString(1)) && "2026-10-20".equals(rows.getDate(2).toString())
                        && "2027-10-20".equals(rows.getDate(3).toString()) && "Updated Lab".equals(rows.getString(4))
                        && "ISO-17025".equals(rows.getString(5)) && "calibration".equals(rows.getString(6))
                        && "completed".equals(rows.getString(7)), "completion updates the linked maintenance plan");
            }
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT SOURCEDATE FROM T_EMC_EQUIPT WHERE ID=1")) {
                check(rows.next() && "2027-10-20".equals(rows.getDate(1).toString()), "completion updates equipment source date");
            }
            http(port, "PUT", BASE + "/records/action/complete", completeBody, cookie, "application/json", 400);
            http(port, "POST", BASE + "/records", "{\"b\":[\"\",[{\"equiptId\":1,\"maintId\":52}]]}", cookie, "application/json", 403);
            JSONObject hiddenRecords = JSON.parseObject(http(port, "POST", BASE + "/2/records/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(hiddenRecords.getIntValue("total") == 0, "equipment maintenance record query suppresses out-of-scope equipment");
            Reply deletable = http(port, "POST", BASE + "/records", recordBody.replace("CERT-1", "CERT-DELETE"), cookie, "application/json", 200);
            long deletableId = Long.parseLong(deletable.body.trim());
            check(http(port, "DELETE", BASE + "/records", "{\"b\":[\"\",[{\"id\":" + deletableId + "}]]}", cookie,
                    "application/json", 200).body.trim().isEmpty(), "maintenance record delete commits through original route");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT_RECORD WHERE ID=" + deletableId)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted maintenance record is absent");
            }
            http(port, "DELETE", BASE + "/records", "{\"b\":[\"\",[{\"id\":999999999}]]}", cookie,
                    "application/json", 403);

            String equipmentBody = "{\"b\":[\"\",[{\"equiptId\":\"EQ-NEW\",\"equiptName\":\"New Scope\",\"specification\":\"1x\",\"manufacturer\":\"Acme\",\"factoryNo\":\"F-1\",\"equiptCategoryId\":11,\"status\":\"active\",\"equiptBelong\":\"自有\",\"equiptRecord\":\"1\"}]]}";
            Reply equipment = http(port, "POST", BASE, equipmentBody, cookie, "application/json", 200);
            long equipmentId = Long.parseLong(equipment.body.trim());
            check(equipmentId >= 1000L, "equipment write uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT EQUIPTID,EQUIPTNAME,SPECIFICATION,MANUFACTURER,FACTORYNO,EQUIPTCATEGORYID,STATUS,EQUIPTBELONG,EQUIPTRECORD,ORGID FROM T_EMC_EQUIPT WHERE ID=" + equipmentId)) {
                check(rows.next() && "EQ-NEW".equals(rows.getString(1)) && "New Scope".equals(rows.getString(2))
                        && "1x".equals(rows.getString(3)) && "Acme".equals(rows.getString(4)) && "F-1".equals(rows.getString(5))
                        && "11".equals(rows.getString(6)) && "active".equals(rows.getString(7)) && "自有".equals(rows.getString(8))
                        && "1".equals(rows.getString(9)) && "org-a".equals(rows.getString(10)), "equipment creation stores scoped fields and actor");
            }
            String hiddenEquipment = equipmentBody.replace("\"equiptCategoryId\":11", "\"equiptCategoryId\":12");
            http(port, "POST", BASE, hiddenEquipment, cookie, "application/json", 403);
            http(port, "POST", BASE, "{\"b\":[\"\",[{\"equiptId\":\"\",\"equiptName\":\"bad\",\"equiptCategoryId\":11}]]}", cookie, "application/json", 400);

            String useBody = "{\"b\":[\"\",[{\"projId\":71,\"projNo\":\"P-71\",\"sampleId\":81,\"sampleNo\":\"S-81\",\"testId\":\"T-81\",\"testName\":\"Acidity\",\"equiptId\":1,\"runId\":\"RUN-81\",\"orderQty\":\"2\"}]]}";
            check(http(port, "POST", BASE + "/use-records", useBody, cookie, "application/json", 200).body.trim().isEmpty(),
                    "equipment use-record insert commits through original route");
            JSONObject usePage = JSON.parseObject(http(port, "POST", BASE + "/1/use-records/queries?selectKey=EQUIPTID",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(usePage.getIntValue("total") == 1 && "RUN-81".equals(usePage.getJSONArray("rows").getJSONObject(0).getString("runId")),
                    "equipment use-record query returns scoped row");
            JSONObject sampleUse = JSON.parseObject(http(port, "POST", BASE + "/81/use-records/queries?selectKey=SAMPLEID",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(sampleUse.getIntValue("total") == 1, "sample-key equipment use-record query returns scoped row");
            long useId;
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT ID,BEFOREUSESTATUS,AFTERUSESTATUS,CREATEDBYID,CREATEDBYORGID FROM T_EMC_EQUIPT_USE_RECORD WHERE EQUIPTID=1")) {
                check(rows.next() && "正常".equals(rows.getString(2)) && "正常".equals(rows.getString(3))
                        && "allowed".equals(rows.getString(4)) && "org-a".equals(rows.getString(5)),
                        "equipment use-record insert stores defaults and actor");
                useId = rows.getLong(1);
            }
            String useUpdate = "{\"b\":[\"\",[{\"id\":" + useId + ",\"equiptId\":1,\"sampleId\":81,\"testId\":\"T-81\",\"userId\":\"u-1\",\"userName\":\"Operator\",\"startTime\":\"09:00\",\"endTime\":\"10:00\",\"equiptCondition\":\"normal\"}]]}";
            check(http(port, "PUT", BASE + "/use-records", useUpdate, cookie, "application/json", 200).body.trim().isEmpty(),
                    "equipment use-record update commits through original route");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT USERID,USERNAME,STARTTIME,ENDTIME,EQUIPTCONDITION FROM T_EMC_EQUIPT_USE_RECORD WHERE ID=" + useId)) {
                check(rows.next() && "u-1".equals(rows.getString(1)) && "Operator".equals(rows.getString(2))
                        && "09:00".equals(rows.getString(3)) && "10:00".equals(rows.getString(4))
                        && "normal".equals(rows.getString(5)), "equipment use-record update stores editable fields");
            }
            JSONObject globalUse = JSON.parseObject(http(port, "POST", BASE + "/use-records/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie,
                    "application/json", 200).body);
            check(globalUse.getIntValue("total") == 1 && "Operator".equals(globalUse.getJSONArray("rows").getJSONObject(0).getString("userName")),
                    "global equipment use-record query returns scoped row: " + globalUse.toJSONString());
            check(http(port, "DELETE", BASE + "/delete-records", "{\"b\":[\"\",[{\"id\":" + useId + "}]]}", cookie,
                    "application/json", 200).body.trim().isEmpty(), "equipment use-record delete commits through original route");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT_USE_RECORD WHERE ID=" + useId)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted equipment use-record is absent");
            }

            String body = "{\"b\":[\"\",[{\"testCategoryId\":11,\"testMethodId\":21,\"testId\":31,\"methodId\":41,\"curveNo\":\"curve-a\",\"curveId\":\"curve-id-a\",\"orgId\":\"org-a\",\"analysisCategory\":\"analysis\",\"defaultFlag\":\"0\"}]]}";
            Reply inserted = http(port, "POST", BASE + "/1/test-methods", body, cookie, "application/json", 200);
            long id = Long.parseLong(inserted.body.trim());
            check(id >= 1000L, "test-method write uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT EQUIPTID,TESTCATEGORYID,TESTMETHODID,TESTID,METHODID,CURVENO,CURVEID,ORGID,ANALYSISCATEGORY,DEFAULTFLAG,CREATEDBYID FROM T_EMC_EQUIPT_TEST_METHOD WHERE ID=" + id)) {
                check(rows.next() && rows.getLong(1) == 1L && rows.getLong(2) == 11L && rows.getLong(3) == 21L
                        && rows.getLong(4) == 31L && rows.getLong(5) == 41L && "curve-a".equals(rows.getString(6))
                        && "curve-id-a".equals(rows.getString(7)) && "org-a".equals(rows.getString(8))
                        && "analysis".equals(rows.getString(9)) && "0".equals(rows.getString(10))
                        && "allowed".equals(rows.getString(11)), "test-method write stores scoped fields and audit actor");
            }
            JSONObject methodPage = JSON.parseObject(http(port, "POST", BASE + "/1/test-methods/queries",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(methodPage.getIntValue("total") == 1 && methodPage.getJSONArray("rows").size() == 1
                    && methodPage.getJSONArray("rows").getJSONObject(0).getLongValue("testMethodId") == 21L
                    && "curve-a".equals(methodPage.getJSONArray("rows").getJSONObject(0).getString("curveNo")),
                    "test-method query returns scoped DTO projection");
            http(port, "POST", BASE + "/2/test-methods", body, cookie, "application/json", 403);
            String invalid = "{\"b\":[\"\",[{\"defaultFlag\":\"invalid\"}]]}";
            http(port, "POST", BASE + "/1/test-methods", invalid, cookie, "application/json", 400);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT_TEST_METHOD")) {
                rows.next(); check(rows.getInt(1) == 1, "invalid test-method body rolls back without a row");
            }
            check(http(port, "DELETE", BASE + "/1/test-methods", "{\"b\":[\"\",[{\"id\":" + id + "}]]}", cookie,
                    "application/json", 200).body.trim().isEmpty(), "test-method delete commits through original route");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT_TEST_METHOD WHERE ID=" + id)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted test method is absent");
            }
            check(http(port, "POST", BASE + "/1/test-methods", body, cookie, "application/json", 200).status == 200,
                    "second isolated insert remains available");
            Reply second = http(port, "POST", BASE + "/1/test-methods", body, cookie, "application/json", 200);
            long secondId = Long.parseLong(second.body.trim());
            String copyBody = "{\"b\":[\"\",[{\"id\":1,\"testMethodId\":21,\"testId\":31,\"methodId\":41,\"curveNo\":\"curve-copy\",\"curveId\":\"curve-id-copy\",\"orgId\":\"org-a\",\"analysisCategory\":\"analysis\",\"defaultFlag\":\"0\"}]]}";
            http(port, "POST", BASE + "/test-methods/action/copyto", copyBody, cookie, "application/json", 200);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT_TEST_METHOD")) {
                rows.next(); check(rows.getInt(1) == 3, "test-method copy-to route writes an owned scoped row");
            }
            JSONObject methodChooser = JSON.parseObject(http(port, "POST", BASE + "/queries/choosable",
                    "{\"p\":{\"testMethodId\":\"21\",\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(methodChooser.getIntValue("total") == 1 && "EQ-001".equals(methodChooser.getJSONArray("rows").getJSONObject(0).getString("equiptId")),
                    "generic equipment chooser honors scoped testMethodId filter");
            String targetFilter = "{\"type\":\"assigned\",\"targetTable\":\"T_EMC_EQUIPT_TEST_METHOD\",\"targetMatchColumn\":\"EQUIPTID\",\"filter\":[{\"targetFilterColumn\":\"testMethodId\",\"targetFilterValue\":\"21\"},{\"targetFilterColumn\":\"ANALYSISCATEGORY\",\"targetFilterValue\":\"analysis\"}]}";
            JSONObject targetChooser = JSON.parseObject(http(port, "POST", BASE + "/queries/choosable",
                    "{\"p\":{\"target_filter\":" + JSON.toJSONString(targetFilter) + ",\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(targetChooser.getIntValue("total") == 1 && "EQ-001".equals(targetChooser.getJSONArray("rows").getJSONObject(0).getString("equiptId")),
                    "generic equipment chooser honors whitelisted assigned target filter");
            JSONObject maintenanceChooser = JSON.parseObject(http(port, "POST", BASE + "/queries/choosable",
                    "{\"p\":{\"maintenanceUser_assigned\":\"1\",\"maintenanceUserId\":\"allowed\",\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(maintenanceChooser.getIntValue("total") == 1 && "EQ-001".equals(maintenanceChooser.getJSONArray("rows").getJSONObject(0).getString("equiptId")),
                    "generic equipment chooser honors assigned maintenance user filter");
            String itemBody = "{\"b\":[\"\",[{\"itemId\":51,\"testMethodId\":" + secondId + ",\"easResultId\":61,\"matchItemName\":\"Fixture item\",\"matchResultName\":\"Fixture result\"}]]}";
            Reply item = http(port, "POST", BASE + "/1/test-items", itemBody, cookie, "application/json", 200);
            long itemId = Long.parseLong(item.body.trim());
            check(itemId >= 1000L, "test-item write uses owned Oracle sequence");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT EQUIPTID,ITEMID,TESTMETHODID,EASRESULTID,MATCHITEMNAME,MATCHRESULTNAME,CREATEDBYID FROM T_EMC_EQUIPT_TEST_ITEM WHERE ID=" + itemId)) {
                check(rows.next() && rows.getLong(1) == 1L && rows.getLong(2) == 51L && rows.getLong(3) == secondId
                        && rows.getLong(4) == 61L && "Fixture item".equals(rows.getString(5))
                        && "Fixture result".equals(rows.getString(6)) && "allowed".equals(rows.getString(7)),
                        "test-item write stores scoped fields and audit actor");
            }
            JSONObject itemPage = JSON.parseObject(http(port, "POST", BASE + "/1/test-items/queries",
                    "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            check(itemPage.getIntValue("total") == 1 && itemPage.getJSONArray("rows").size() == 1
                    && itemPage.getJSONArray("rows").getJSONObject(0).getLongValue("itemId") == 51L
                    && "Fixture item".equals(itemPage.getJSONArray("rows").getJSONObject(0).getString("matchItemName")),
                    "test-item query returns scoped DTO projection");
            JSONObject dcuValidation = JSON.parseObject(http(port, "POST", BASE + "/1/action/validate-dcu-config",
                    null, cookie, null, 200).body);
            check(dcuValidation.getBooleanValue("valid"), "DCU validation accepts equipment with a scoped test item");
            http(port, "POST", BASE + "/2/test-items", itemBody, cookie, "application/json", 403);
            check(http(port, "DELETE", BASE + "/1/test-items", "{\"b\":[\"\",[{\"id\":" + itemId + "}]]}", cookie,
                    "application/json", 200).body.trim().isEmpty(), "test-item delete commits through original route");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement();
                    java.sql.ResultSet rows = statement.executeQuery("SELECT COUNT(1) FROM T_EMC_EQUIPT_TEST_ITEM WHERE ID=" + itemId)) {
                rows.next(); check(rows.getInt(1) == 0, "deleted test item is absent");
            }
            http(port, "DELETE", BASE + "/2/test-methods", "{\"b\":[\"\",[{\"id\":" + secondId + "}]]}", cookie,
                    "application/json", 403);
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM T_CORE_USER_PERMISSION WHERE USERID='allowed' AND PERMISSIONTYPEID='T_CORE_ORG'");
            }
            JSONObject zeroOrgPlans = JSON.parseObject(http(port, "POST", BASE + "/1/per-check-plan/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            JSONObject zeroOrgRecords = JSON.parseObject(http(port, "POST", BASE + "/1/per-check-record/queries", "{\"p\":{\"n\":1,\"s\":10}}", cookie, "application/json", 200).body);
            String zeroOrgSelectable = http(port, "GET", BASE + "/1/per-check-records/maints/queries/selectable", null, cookie, null, 200).body;
            check(zeroOrgPlans.getIntValue("total") == 0 && zeroOrgRecords.getIntValue("total") == 0
                    && JSON.parseArray(zeroOrgSelectable).isEmpty(), "zero organization grants return empty period-check results");
            String lowCookie = http(port, "POST", "/core/module/sys/login", "id=low&password=" + encrypted,
                    null, null, 200, key).cookie;
            http(port, "POST", BASE + "/plans", planBody, lowCookie, "application/json", 403);
            http(port, "POST", BASE + "/records", recordBody, lowCookie, "application/json", 403);
            http(port, "POST", BASE + "/per-check-plan", perCheckPlanBody, lowCookie, "application/json", 403);
            http(port, "POST", BASE + "/per-check-record", perCheckRecordBody, lowCookie, "application/json", 403);
            http(port, "POST", BASE, equipmentBody, lowCookie, "application/json", 403);
            http(port, "POST", BASE + "/use-records", useBody, lowCookie, "application/json", 403);
            http(port, "PUT", BASE + "/use-records", useUpdate, lowCookie, "application/json", 403);
            http(port, "POST", BASE + "/1/test-methods", body, lowCookie, "application/json", 403);
            http(port, "POST", BASE + "/1/test-items", itemBody, lowCookie, "application/json", 403);
            String records = new String(Files.readAllBytes(audit), StandardCharsets.UTF_8);
            check(records.contains("equipment-insert") && records.contains("equipment-plan-insert") && records.contains("equipment-record-insert")
                    && records.contains("equipment-per-check-plan-insert") && records.contains("equipment-per-check-record-insert")
                    && records.contains("equipment-per-check-record-complete") && records.contains("equipment-per-check-plan-delete")
                    && records.contains("equipment-record-complete") && records.contains("equipment-record-delete")
                    && records.contains("equipment-use-record-insert") && records.contains("equipment-use-record-update")
                    && records.contains("equipment-use-record-delete")
                    && records.contains("equipment-test-method-insert") && records.contains("equipment-test-method-delete")
                    && records.contains("equipment-test-item-insert") && records.contains("equipment-test-item-delete"),
                    "equipment test-method and test-item writes are audited");
            check(!records.contains("secret") && !records.contains(properties.getProperty("password")),
                    "write audit omits credentials");
        } finally {
            app.close();
            Files.walk(root).sorted(Comparator.reverseOrder()).forEach(path -> {
                try { Files.deleteIfExists(path); } catch (IOException failure) { throw new java.io.UncheckedIOException(failure); }
            });
        }
        System.out.println("equipment test-method/item write: PASS (" + checks + " checks; isolated Oracle loopback only)");
    }

    private static void seedWriteFixture(DataSource source) throws SQLException {
        try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, USERNAME VARCHAR2(100), PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP, PARENTORGID VARCHAR2(64))");
            statement.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, LASTSUSPENDEDFLAG VARCHAR2(1))");
            statement.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            statement.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            statement.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
            statement.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY, ORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            statement.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            statement.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            statement.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            statement.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            statement.execute("CREATE TABLE T_CORE_CODE (ID NUMBER PRIMARY KEY, CODE VARCHAR2(64), CODECATEGORYID VARCHAR2(64), CODENAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_ORG_PERMISSION (PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64), ORGID VARCHAR2(64))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_CATEGORY (ID VARCHAR2(64) PRIMARY KEY, EQUIPTCATEGORY VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_MAINT (ID NUMBER PRIMARY KEY, EQUIPTCATEGORYID NUMBER, MAINTEVENT VARCHAR2(64), EVENTDESC VARCHAR2(256), WARNINGDAYS NUMBER, SAME_DAY VARCHAR2(1))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_PLAN (ID NUMBER PRIMARY KEY, EQUIPTID NUMBER, MAINTID NUMBER, MAINTDATE TIMESTAMP, CYCLE VARCHAR2(64), WARNINGDAYS NUMBER, ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), MAINTENDDATE TIMESTAMP, REMARK VARCHAR2(200), VERIFYORG VARCHAR2(200), GIST VARCHAR2(200), CERTCATEGORY VARCHAR2(100), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_RECORD (ID NUMBER PRIMARY KEY, EQUIPTID NUMBER, MAINTID NUMBER, VERIFYORG VARCHAR2(200), VERIFYORGNAME VARCHAR2(200), CERTNO VARCHAR2(128), CERTCATEGORY VARCHAR2(100), CERTEFFECTDATE TIMESTAMP, STATUS VARCHAR2(64), STARTSTATUS VARCHAR2(64), AIRSATURATION VARCHAR2(64), ADDICE VARCHAR2(64), MAINTSITUATION VARCHAR2(200), REMARK VARCHAR2(200), COMPLETEFLAG VARCHAR2(1), MAINDATE TIMESTAMP, MAINTENDDATE TIMESTAMP, CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_MAINTAIN_REPAIR (ID NUMBER PRIMARY KEY, EQUIPTID NUMBER, MAINTAINREPAIRDATE DATE, MAINTAINREPAIRCONCENT VARCHAR2(200), MAINTAINREPAIRSIGN VARCHAR2(200), EQUIPTFAILURE VARCHAR2(200), MAINTAINREPAIRUNIT VARCHAR2(200), PLANCATEGORY VARCHAR2(100), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_PER_CHECK_PLAN (ID NUMBER PRIMARY KEY, EQUIPTID NUMBER, MAINTID NUMBER, MAINTDATE TIMESTAMP, CYCLE VARCHAR2(64), WARNINGDAYS NUMBER, CHECKREASON VARCHAR2(200), CHECKEQUIPT VARCHAR2(200), MAINTENDDATE TIMESTAMP, REMARK VARCHAR2(200), CHECKWAY VARCHAR2(100), CHECKUSERID VARCHAR2(64), CHECKUSERNAME VARCHAR2(100), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_PER_CHECK_RECORD (ID NUMBER PRIMARY KEY, EQUIPTID NUMBER, MAINTID NUMBER, MAINTDATE TIMESTAMP, CYCLE VARCHAR2(64), CHECKREASON VARCHAR2(200), CHECKEQUIPT VARCHAR2(200), COMPLETEFLAG VARCHAR2(1), CHECKUSERS VARCHAR2(200), CHECKUSERNAMES VARCHAR2(200), REMARK VARCHAR2(200), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT (ID NUMBER PRIMARY KEY, EQUIPTID VARCHAR2(64), EQUIPTNAME VARCHAR2(100), SPECIFICATION VARCHAR2(100), MANUFACTURER VARCHAR2(100), FACTORYNO VARCHAR2(100), EQUIPTCATEGORYID VARCHAR2(64), USEDDEPARTMENTID VARCHAR2(64), USEDDEPOSTID VARCHAR2(64), USEDUSERNAME VARCHAR2(128), MAINTAINUSERID VARCHAR2(64), KEEPUSERID VARCHAR2(64), STATUS VARCHAR2(36), EQUIPTBELONG VARCHAR2(36), EQUIPTRECORD VARCHAR2(1), ORGID VARCHAR2(64), SOURCEDATE DATE)");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_TEST_METHOD (ID NUMBER PRIMARY KEY, EQUIPTID NUMBER, TESTCATEGORYID NUMBER, TESTMETHODID NUMBER, TESTID NUMBER, METHODID NUMBER, CURVENO VARCHAR2(128), CURVEID VARCHAR2(128), ORGID VARCHAR2(64), ANALYSISCATEGORY VARCHAR2(64), DEFAULTFLAG VARCHAR2(1), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_TEST_ITEM (ID NUMBER PRIMARY KEY, EQUIPTID NUMBER, ITEMID NUMBER, TESTMETHODID NUMBER, EASRESULTID NUMBER, MATCHITEMNAME VARCHAR2(200), MATCHRESULTNAME VARCHAR2(200), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_DCU_CONF (ID NUMBER PRIMARY KEY, ACQSCRIPTID NUMBER, INTERFACECATEGORY VARCHAR2(64), BAUDRATE VARCHAR2(32), TERMINATOR VARCHAR2(64), DATABIT VARCHAR2(32), PARITYCHECK VARCHAR2(32), COM VARCHAR2(32), STOPBIT VARCHAR2(32), BEGINSYMBOL VARCHAR2(64), ENDSYMBOL VARCHAR2(64), ANALYTICFORMULA VARCHAR2(200), IP VARCHAR2(64), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE TABLE T_EMC_EQUIPT_USE_RECORD (ID NUMBER PRIMARY KEY, PROJID NUMBER, PROJNO VARCHAR2(64), SAMPLEID NUMBER, SAMPLENO VARCHAR2(64), TESTID VARCHAR2(64), TESTNAME VARCHAR2(200), EQUIPTID NUMBER, USEDTIME TIMESTAMP, BEFOREUSESTATUS VARCHAR2(64), AFTERUSESTATUS VARCHAR2(64), USERID VARCHAR2(64), USERNAME VARCHAR2(100), RUNID VARCHAR2(64), EQUIPTCONDITION VARCHAR2(200), ORDERQTY VARCHAR2(64), STARTDATE VARCHAR2(32), STARTTIME VARCHAR2(32), ENDDATE VARCHAR2(32), ENDTIME VARCHAR2(32), CREATEDBYID VARCHAR2(64), CREATEDBYNAME VARCHAR2(100), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR2(64), CREATEDBYORGNAME VARCHAR2(100))");
            statement.execute("CREATE SEQUENCE SEQ_CORE START WITH 1000 INCREMENT BY 1");
            statement.execute("INSERT INTO T_CORE_MENU VALUES ('101501')");
            statement.execute("INSERT INTO T_CORE_ORG VALUES ('org-a','Fixture Org A')");
            statement.execute("INSERT INTO T_CORE_USER VALUES ('allowed','Allowed User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            statement.execute("INSERT INTO T_CORE_USER VALUES ('low','Low User','5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org A','activated','0',NULL,'org-a')");
            statement.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0')");
            statement.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");
            statement.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','101501')");
            statement.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");
            statement.execute("INSERT INTO T_EMC_EQUIPT_CATEGORY VALUES ('11','Microscope')");
            statement.execute("INSERT INTO T_EMC_ORG_PERMISSION VALUES ('T_EMC_EQUIPT_CATEGORY','11','org-a')");
            statement.execute("INSERT INTO T_EMC_EQUIPT_MAINT (ID,EQUIPTCATEGORYID,MAINTEVENT,WARNINGDAYS,SAME_DAY) VALUES (51,11,'equiptCalibration',30,'0')");
            statement.execute("INSERT INTO T_EMC_EQUIPT_MAINT (ID,EQUIPTCATEGORYID,MAINTEVENT,WARNINGDAYS,SAME_DAY) VALUES (53,11,'equiptCalibration',30,'0')");
            statement.execute("INSERT INTO T_EMC_EQUIPT_MAINT (ID,EQUIPTCATEGORYID,MAINTEVENT,WARNINGDAYS,SAME_DAY) VALUES (52,12,'equiptCalibration',30,'0')");
            statement.execute("INSERT INTO T_CORE_CODE VALUES (1,'equiptCalibration','maintainEvent','Calibration')");
            statement.execute("INSERT INTO T_EMC_EQUIPT (ID,EQUIPTID,EQUIPTNAME,EQUIPTCATEGORYID,ORGID) VALUES (1,'EQ-001','Alpha Scope','11','org-a')");
            statement.execute("INSERT INTO T_EMC_EQUIPT (ID,EQUIPTID,EQUIPTNAME,EQUIPTCATEGORYID,ORGID) VALUES (2,'EQ-002','Hidden Scope','12','org-b')");
            statement.execute("UPDATE T_EMC_EQUIPT SET MAINTAINUSERID='allowed' WHERE ID=1");
            statement.execute("INSERT INTO T_EMC_EQUIPT_DCU_CONF (ID,ACQSCRIPTID,INTERFACECATEGORY,BAUDRATE,TERMINATOR,DATABIT,PARITYCHECK,COM,STOPBIT,BEGINSYMBOL,ENDSYMBOL,ANALYTICFORMULA,IP,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (1,701,'serial','9600','\\n','8','None','COM1','1','<','>','x*2','127.0.0.1','allowed','Allowed User',TIMESTAMP '2025-01-01 00:00:00','org-a','Fixture Org A')");
            statement.execute("UPDATE T_EMC_EQUIPT SET USEDUSERNAME='Allowed User' WHERE ID=1");
            statement.execute("UPDATE T_EMC_EQUIPT SET KEEPUSERID='allowed' WHERE ID=1");
            statement.execute("INSERT INTO T_EMC_EQUIPT_MAINTAIN_REPAIR (ID,EQUIPTID,MAINTAINREPAIRDATE,MAINTAINREPAIRCONCENT,MAINTAINREPAIRSIGN,EQUIPTFAILURE,MAINTAINREPAIRUNIT,PLANCATEGORY,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (800,1,DATE '2025-02-01','fixture maintenance','signed','fixture failure','Fixture Unit','routine','allowed','Allowed User',TIMESTAMP '2025-02-01 00:00:00','org-a','Fixture Org A')");
        }
    }

    private static Reply http(int port, String method, String path, String body, String cookie, String type, int expected) throws Exception {
        return http(port, method, path, body, cookie, type, expected, null);
    }

    private static Reply http(int port, String method, String path, String body, String cookie, String type,
            int expected, String rsaKey) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://127.0.0.1:" + port + path).openConnection();
        connection.setRequestMethod(method); connection.setConnectTimeout(5000); connection.setReadTimeout(10000);
        connection.setInstanceFollowRedirects(false);
        if (cookie != null) connection.setRequestProperty("Cookie", cookie);
        if (rsaKey != null) connection.setRequestProperty("SECURITY-RSA-KEY", rsaKey);
        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", type == null ? "application/x-www-form-urlencoded" : type);
            connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }
        int status = connection.getResponseCode();
        java.io.InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
        String text = stream == null ? "" : new String(read(stream), StandardCharsets.UTF_8);
        check(status == expected, method + " " + path + " HTTP " + expected + " actual=" + status + " body=" + text);
        String set = connection.getHeaderField("Set-Cookie");
        return new Reply(status, text, set == null ? cookie : set.split(";", 2)[0]);
    }

    private static byte[] read(java.io.InputStream input) throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096]; int size;
        while ((size = input.read(buffer)) != -1) out.write(buffer, 0, size);
        return out.toByteArray();
    }

    private static final class Reply {
        final int status; final String body; final String cookie;
        Reply(int status, String body, String cookie) { this.status = status; this.body = body; this.cookie = cookie; }
    }
}
