package com.sunwayworld.framework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.data.AdmQuotationQueryDao;
import com.sunwayworld.owned.internal.data.AdmQuotationQueryService;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.serial.SerialClob;
import org.springframework.security.access.AccessDeniedException;

/** JDBC seam contract, not HTTP/Oracle evidence. No schema or business rows are written. */
public final class AdmQuotationQueryServiceContractTest {
    private static final String PREFIX = "/secure/emc/module/item/";
    private static final String DETECTION = PREFIX + "quotation/detectionfactors/queries";
    private static int checks;

    public static void main(String[] args) throws Exception {
        for (String resource : Arrays.asList("adm-areas", "quotation/addprices", "quotation/detectionfactors", "quotation/discounts", "quotation/quotationinformations")) {
            for (String alias : Arrays.asList("/queries", "/queries/raw", "/queries/searchable")) {
                Fixture fixture = new Fixture();
                fixture.call(PREFIX + resource + alias, "{\"p\":{\"f\":{\"id_SEQ\":\"-987654321\"},\"o\":[{\"id\":\"asc\"}],\"n\":2,\"s\":2}}");
                check(fixture.sql.get(0).contains("T.ID = ?"), "every alias applies filter to count");
                check(fixture.sql.get(1).contains("T.ID = ?") && fixture.sql.get(1).contains("ORDER BY Q.ID ASC"), "every alias applies filter and order to rows");
                List<Object> expected = new ArrayList<Object>(resource.contains("detectionfactors") ? Arrays.<Object>asList("org-a", "org-child") : Arrays.<Object>asList("org-a"));
                expected.add("-987654321");
                check(fixture.values.get(0).equals(expected), "count filter bound after trusted organization");
                expected.add(2L); expected.add(4L);
                check(fixture.values.get(1).equals(expected), "page boundaries and same scope/filter bound");
            }
        }

        Fixture projection = new Fixture();
        JSONObject page = projection.call(DETECTION, "{\"p\":{\"n\":2,\"s\":1}}");
        JSONObject row = page.getJSONArray("rows").getJSONObject(0);
        check("2026-11-03".equals(row.getString("createdTime")), "supplier date-only bean field");
        check(!row.containsKey("methodNamee") && "joined method".equals(row.getJSONObject("ext$").getString("methodnamee")), "joined fields in ext$");
        check("large remark".equals(row.getString("remark")), "CLOB is materialized before connection closes");
        check("2".equals(row.getJSONObject("ext$").get("rn_")), "supplier row-number string");
        check(!row.containsKey("testName"), "null bean values omitted");
        check(page.getIntValue("total") == 3 && page.getIntValue("totalPages") == 3
                && !page.getBooleanValue("first") && !page.getBooleanValue("last"), "page envelope derived from count");
        check(page.getJSONObject("totalMap").getIntValue("qty_") == 3
                && "dateTime".equals(page.getJSONObject("columnTypeMap").getString("createdtime")), "aggregate and metadata contract");

        Fixture filtered = new Fixture();
        filtered.call(DETECTION, "{\"p\":{\"f\":{\"ext$.methodnamee_CISC\":\"MeThOd' OR 1=1 --\",\"org_filter\":\"untrusted-org\"},\"o\":[{\"ext$.methodno\":\"desc\"}]}}");
        check(filtered.sql.get(0).contains("T.ORGID IN (?,?)") && filtered.sql.get(0).contains("UPPER(T.METHODNAMEE) LIKE ?"), "projection filter and trusted scope combined");
        check(!filtered.sql.get(0).contains("OR 1=1") && !filtered.values.get(0).contains("untrusted-org"), "untrusted values cannot become SQL or authorization");
        check(filtered.values.get(0).contains("%METHOD' OR 1=1 --%"), "filter payload parameterized");
        check(filtered.sql.get(1).contains("Q.METHODNO DESC,Q.ID DESC"), "joined sort with stable tie-breaker");

        Fixture advanced = new Fixture();
        advanced.call(DETECTION, "{\"p\":{\"af\":[{\"children\":[{\"name\":\"id\",\"match\":\"NEQ\",\"value\":1},{\"link\":\"OR\",\"name\":\"testName\",\"match\":\"SC\",\"value\":\"test\"}]}],\"mf\":{\"name\":\"testName,itemGroupName\",\"value\":\"group\",\"caseSensitive\":false}}}");
        check(advanced.sql.get(0).contains("AND ((T.ID = ? OR T.TESTNAME LIKE ?))") && advanced.sql.get(0).contains("UPPER(T.ITEMGROUPNAME) LIKE ?"), "advanced grouping and multi-column filters");

        Fixture noScope = new Fixture();
        noScope.scope = Collections.emptySet();
        noScope.call(DETECTION, "{}");
        check(noScope.sql.get(0).contains("WHERE 1=0") && noScope.values.get(0).isEmpty(), "no grants fail closed even with valid session");

        for (String invalid : Arrays.asList("null", "[]", "{\"p\":{\"f\":{\"unknown_SEQ\":1}}}",
                "{\"p\":{\"o\":[{\"id;DROP TABLE X\":\"asc\"}]}}", "{\"p\":{\"o\":[{\"id\":\"sideways\"}]}}",
                "{\"p\":{\"loginorgid\":\"other\"}}", "{\"p\":{\"s\":501}}", "{\"p\":{\"f\":{\"sql\":\"anything\"}}}")) {
            Fixture fixture = new Fixture();
            try { fixture.call(DETECTION, invalid); throw new AssertionError("invalid query accepted"); }
            catch (IllegalArgumentException expected) { check(fixture.sql.isEmpty(), "invalid query rejected before SQL"); }
        }
        try {
            Fixture fixture = new Fixture();
            fixture.service().query(DETECTION, null, fixture.scope, QueryRequest.parse("{}", null));
            throw new AssertionError("anonymous query accepted");
        } catch (AccessDeniedException expected) { check(true, "anonymous denied"); }
        Fixture highPage = new Fixture();
        highPage.call(DETECTION, "{\"p\":{\"n\":2147483647,\"s\":500}}");
        check(highPage.values.get(1).contains(1073741823500L), "pagination cannot overflow 32-bit range");
        System.out.println("owned administrative/quotation service/DAO contract: " + checks + " checks passed");
    }

    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    private static final class Fixture {
        final List<String> sql = new ArrayList<String>();
        final List<List<Object>> values = new ArrayList<List<Object>>();
        Set<String> scope = new LinkedHashSet<String>(Arrays.asList("org-a", "org-child"));
        AdmQuotationQueryService service() {
            DataSource source = (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{DataSource.class}, (p, m, a) -> {
                if (!"getConnection".equals(m.getName())) throw new AssertionError("unexpected DataSource call: " + m.getName());
                return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Connection.class}, (c, method, args) -> {
                    if ("close".equals(method.getName())) return null;
                    if (!"prepareStatement".equals(method.getName())) throw new AssertionError("unexpected connection call: " + method.getName());
                    final String text = (String) args[0];
                    sql.add(text);
                    List<Object> parameters = new ArrayList<Object>(); values.add(parameters);
                    return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{PreparedStatement.class}, (q, operation, inputs) -> {
                        if (operation.getName().startsWith("set")) {
                            int index = (Integer) inputs[0]; while (parameters.size() < index) parameters.add(null);
                            parameters.set(index - 1, inputs[1]); return null;
                        }
                        if ("close".equals(operation.getName())) return null;
                        if ("executeQuery".equals(operation.getName())) return result(text.startsWith("SELECT COUNT"));
                        throw new AssertionError("only read-only JDBC queries allowed: " + operation.getName());
                    });
                });
            });
            return new AdmQuotationQueryService(new AdmQuotationQueryDao(source));
        }
        JSONObject call(String path, String body) throws Exception {
            return JSON.parseObject(service().query(path, new OwnedPrincipal("reader", "org-a", "Org A"), scope, QueryRequest.parse(body, null)));
        }
        ResultSet result(boolean count) throws Exception {
            CachedRowSet rows = RowSetProvider.newFactory().createCachedRowSet();
            RowSetMetaDataImpl metadata = new RowSetMetaDataImpl();
            String[] labels = count ? new String[]{"TOTAL"} : new String[]{"ID", "CREATEDTIME", "METHODNAMEE", "REMARK", "TESTNAME", "RN_"};
            int[] types = count ? new int[]{Types.NUMERIC} : new int[]{Types.NUMERIC, Types.TIMESTAMP, Types.VARCHAR, Types.CLOB, Types.VARCHAR, Types.NUMERIC};
            metadata.setColumnCount(labels.length);
            for (int i = 0; i < labels.length; i++) { metadata.setColumnName(i + 1, labels[i]); metadata.setColumnLabel(i + 1, labels[i]); metadata.setColumnType(i + 1, types[i]); }
            rows.setMetaData(metadata);
            rows.moveToInsertRow();
            if (count) rows.updateLong(1, 3);
            else { rows.updateLong(1, 7); rows.updateTimestamp(2, Timestamp.valueOf("2026-11-03 12:34:56")); rows.updateString(3, "joined method"); rows.updateObject(4, new SerialClob("large remark".toCharArray())); rows.updateNull(5); rows.updateLong(6, 2); }
            rows.insertRow(); rows.moveToCurrentRow(); rows.beforeFirst();
            return rows;
        }
    }
}
