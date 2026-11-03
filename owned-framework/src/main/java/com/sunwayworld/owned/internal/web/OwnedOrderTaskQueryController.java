package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned read seam for the inherited order-task resource.
 *
 * <p>The project resource exposes many aliases over one query family.  Keep
 * those aliases on one controller so they share the same identity and
 * organization boundary and do not require a compatibility process.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderTaskQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/bp/order-tasks";
    private static final String BASE_SQL =
            "SELECT DISTINCT T.ORDERNO,T.ID,T.FOLDERID,T.ORDERID,T.PROJID,T.PARENTID," +
            "T.SCHEMENAME,T.TESTID,T.TESTNAME,T.TESTMETHODID,T.TESTMETHODBASIS," +
            "T.EQUIPTNAME,T.METHODID,T.METHODNAME,T.CONTAINERCATEGORYID," +
            "T.CONTAINERCATEGORYCODE,T.CONTAINERCATEGORYNAME,T.CONTAINERGROUP," +
            "T.QCCATEGORYID,T.QCCATEGORYCODE,T.QCNAME,T.SUBPACKAGENO,T.ANALYSISTYPE," +
            "T.EQUIPTID,T.CUPNO,T.LABELNO,T.RUNID,T.MONITORTMPLID,T.ORDERCATEGORY," +
            "T.ANALYSISUSERID,T.PARTAKEUSERID,T.ANALYSISORGID,T.ANALYSISORGNAME," +
            "T.EQUIPTACQUNO,T.STOCKID,T.STOCKBATCHNO,T.INDEXCATEGORYID,T.INPUTUSERID," +
            "T.INDEXID,T.STATUS,T.ORDERDESC,T.SAMPLEBEGINDATE,T.SAMPLEBEGINTIME," +
            "T.PARENTORDERNO,T.MONITORPOINTNAME " +
            "FROM (SELECT A.*,B.ORDERNO,B.LABELNO,B.ORDERDESC,B.SAMPLEBEGINDATE," +
            "B.SAMPLEBEGINTIME,B.PARENTORDERNO,C.MONITORPOINTNAME,D.EQUIPTNAME " +
            "FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_ORDER B ON A.ORDERID=B.ID " +
            "LEFT JOIN T_EMC_FOLDER C ON A.FOLDERID=C.ID " +
            "LEFT JOIN T_EMC_EQUIPT D ON A.EQUIPTID=D.ID) T " +
            "WHERE T.ANALYSISORGID=?";

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrderTaskQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("order-task dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable",
            PREFIX + "/queries/runnable", PREFIX + "/queries/un-runnable", PREFIX + "/queries/analysis",
            PREFIX + "/queries/order-group", PREFIX + "/queries/order-group-runs",
            PREFIX + "/queries/group-by-test", PREFIX + "/queries/order-task",
            PREFIX + "/queries/select-order-container-info", PREFIX + "/queries/selectContainerInfo",
            PREFIX + "/unique-tasks/queries", PREFIX + "/unique-item-groups/queries",
            PREFIX + "/unique-orders/queries", PREFIX + "/subpackages/queries",
            PREFIX + "/analysis-department-statistics/queries",
            PREFIX + "/personnel-workload-statistics/queries",
            PREFIX + "/proj-order-categories/queries",
            "/secure/emc/module/bp/bp/orders/order-tasks/queries",
            "/secure/emc/module/bp/edit/project-change/orders/order-tasks/queries",
            "/secure/emc/module/bp/result/runs/blank-order-tasks/queries",
            "/secure/emc/module/bp/sample/samples/order-tasks/queries",
            "/secure/emc/module/bp/sample/samples/{id}/order-tasks/queries",
            PREFIX + "/queries/runnableanalassign"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Page page = Page.parse(body);
            String specialized = selectSpecializedPage(principal.getOrgId(), page, request.getRequestURI(), body);
            return ok(specialized == null ? selectPage(principal.getOrgId(), page, request.getRequestURI()) : specialized);
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return queryInvalid(request, response); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            long numericId = numericId(id);
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(
                    BASE_SQL + " AND T.ID=?")) {
                query.setString(1, principal.getOrgId());
                query.setLong(2, numericId);
                try (ResultSet rows = query.executeQuery()) {
                    if (!rows.next()) return ok("{}");
                    return ok(JSON.toJSONString(row(rows, rows.getMetaData()), SerializerFeature.WriteMapNullValue));
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page, String requestPath) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + BASE_SQL + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + BASE_SQL + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement query = connection.prepareStatement(countSql)) {
                query.setString(1, organization);
                try (ResultSet result = query.executeQuery()) {
                    if (!result.next()) throw new SQLException("order-task count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization); query.setInt(2, page.start()); query.setInt(3, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    Map<String, String> supplierTypes = TYPE_PROFILES.get(profileKey(requestPath));
                    if (supplierTypes != null) {
                        types.clear();
                        types.putAll(supplierTypes);
                    }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number);
        boolean emptyOrderResource = total == 0 && (requestPath.endsWith("/orders/order-tasks/queries")
                || requestPath.endsWith("/edit/project-change/orders/order-tasks/queries")
                || requestPath.endsWith("/blank-order-tasks/queries")
                || requestPath.endsWith("/samples/order-tasks/queries")
                || (requestPath.contains("/sample/samples/") && requestPath.endsWith("/order-tasks/queries")));
        envelope.put("pageSize", emptyOrderResource ? 0 : page.size);
        envelope.put("rows", rows); envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total);
        envelope.put("totalMap", emptyOrderResource ? new LinkedHashMap<String, Object>() : totals);
        envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    /**
     * The inherited resource has several aliases whose SQL is an aggregate or
     * a narrow projection rather than the normal order-task page.  They used
     * to be mapped to BASE_SQL, which made an empty fixture look plausible but
     * returned the wrong rows as soon as a real order/task was present.  Keep
     * those behaviours in the same controller and identity boundary.
     *
     * <p>A null return means that the normal inherited projection is still the
     * correct implementation for the alias.  For the aggregate routes, a
     * missing required filter intentionally returns the supplier-shaped empty
     * page instead of broadening a query over the whole tenant.</p>
     */
    private String selectSpecializedPage(String organization, Page page, String requestPath, String body) throws SQLException {
        if (requestPath == null) return null;
        if (requestPath.endsWith("/queries/group-by-test")) {
            String sampleId = queryValue(body, "sampleId");
            if (sampleId == null) return emptyProfilePage(page, profileKey(requestPath), true);
            return executeProjectionPage(
                    "SELECT LISTAGG(TO_CHAR(A.ID), ',') WITHIN GROUP (ORDER BY A.ID) AS IDS, "
                    + "A.TESTMETHODID,A.TESTID,A.TESTNAME,A.METHODID,A.METHODNAME,P.PROJNO,COUNT(A.ID) AS NUM "
                    + "FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_PROJECT P ON A.PROJID=P.ID "
                    + "LEFT JOIN T_EMC_ORDER O ON A.ORDERID=O.ID "
                    + "WHERE (P.STATUS IS NULL OR P.STATUS <> 'cancel') AND A.ANALYSISTYPE='sampling' "
                    + "AND O.SAMPLEID=? AND A.ANALYSISORGID=? "
                    + "GROUP BY A.TESTMETHODID,A.TESTID,A.TESTNAME,A.METHODID,A.METHODNAME,P.PROJNO "
                    + "ORDER BY A.TESTMETHODID DESC",
                    values(sampleId, organization), page, profileKey(requestPath), true);
        }
        if (requestPath.endsWith("/queries/selectContainerInfo")) {
            String orderId = queryValue(body, "orderId");
            if (orderId == null) return emptyProfilePage(page, profileKey(requestPath), true);
            return executeProjectionPage(
                    "SELECT A.CONTAINERGROUP,A.FIXATIVE,A.CONTAINERCATEGORYNAME,A.CONTAINERCATEGORYID,"
                    + "A.STORECONDITION,A.SAMPLEWEIGHT,LISTAGG(A.TESTNAME, ',') WITHIN GROUP (ORDER BY A.ID) AS TESTNAME,"
                    + "LISTAGG(TO_CHAR(A.ID), ',') WITHIN GROUP (ORDER BY A.ID) AS MULTITESTID,"
                    + "LISTAGG(A.ITEMGROUP, ',') WITHIN GROUP (ORDER BY A.ID) AS ITEMGROUP,A.ORDERCONTAINERNO "
                    + "FROM (SELECT DISTINCT T.ID,T.TESTNAME,T.SUBCONTRACTORID,T.CONTAINERGROUP,T.FIXATIVE,"
                    + "T.CONTAINERCATEGORYNAME,T.CONTAINERCATEGORYID,T.STORECONDITION,C.SAMPLEWEIGHT,D.ITEMGROUP,"
                    + "E.ORDERCONTAINERNO FROM T_EMC_ORDER_TASK T "
                    + "LEFT JOIN T_EMC_TEST_ORDER_CATEGORY C ON T.CONTAINERCATEGORYID=C.CONTAINERCATEGORYID "
                    + "AND T.MONITORTMPLID=C.MONITORTMPLID AND T.TESTMETHODID=C.TESTMETHODID AND C.ORGID=? "
                    + "LEFT JOIN T_EMC_ORDER_ITEM_GROUP D ON T.ORDERID=D.ORDERID AND T.TESTID=D.TESTID "
                    + "LEFT JOIN T_EMC_ORDER_CONTAINER E ON T.ORDERCONTAINERID=E.ID WHERE T.ORDERID=? "
                    + ") A GROUP BY A.CONTAINERGROUP,A.FIXATIVE,A.CONTAINERCATEGORYNAME,A.CONTAINERCATEGORYID,"
                    + "A.STORECONDITION,A.SAMPLEWEIGHT,A.ORDERCONTAINERNO",
                    values(organization, orderId), page, profileKey(requestPath), true);
        }
        if (requestPath.endsWith("/queries/select-order-container-info")) {
            String orderId = queryValue(body, "orderId");
            if (orderId == null) return emptyProfilePage(page, profileKey(requestPath), true);
            return executeProjectionPage(
                    "SELECT LISTAGG(TO_CHAR(A.ID), ',') WITHIN GROUP (ORDER BY A.ID) AS IDS,"
                    + "LISTAGG(A.TESTNAME, ',') WITHIN GROUP (ORDER BY A.ID) AS TESTNAME,COUNT(DISTINCT A.ORDERID) AS NUM,"
                    + "A.PROJID,A.STORECONDITION,A.FOLDERID,A.CONTAINERCATEGORYID,A.CONTAINERCATEGORYCODE,"
                    + "A.CONTAINERCATEGORYNAME,A.CONTAINERGROUP,A.FIXATIVE,A.QCCATEGORYCODE,QC.QCNAME,"
                    + "A.ORDERCATEGORY,A.ANALYSISORGID,A.ANALYSISORGNAME,A.PRETREATWAY,O.ORDERNO,O.ORDERDESC,"
                    + "O.PARENTORDERNO,O.SAMPLEBEGINDATE,F.FOLDERNO,F.SAMPLEORGID,F.SAMPLEORGNAME,S.SAMPLENO,"
                    + "CC.MATERIAL,CC.COLOR,CC.CAPACITY "
                    + "FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_ORDER O ON A.ORDERID=O.ID "
                    + "LEFT JOIN T_EMC_SAMPLE S ON O.SAMPLEID=S.ID LEFT JOIN T_EMC_FOLDER F ON A.FOLDERID=F.ID "
                    + "LEFT JOIN T_EMC_CONTAINER_CATEGORY CC ON A.CONTAINERCATEGORYID=CC.ID "
                    + "LEFT JOIN T_EMC_QC_CATEGORY QC ON A.QCCATEGORYID=QC.ID "
                    + "WHERE A.ORDERID=? AND A.ANALYSISORGID=? AND (A.REMARKS IS NULL OR A.REMARKS <> '历史数据') "
                    + "GROUP BY A.PROJID,A.STORECONDITION,A.FOLDERID,A.CONTAINERCATEGORYID,A.CONTAINERCATEGORYCODE,"
                    + "A.CONTAINERCATEGORYNAME,A.CONTAINERGROUP,A.FIXATIVE,A.QCCATEGORYCODE,QC.QCNAME,A.ORDERCATEGORY,"
                    + "A.ANALYSISORGID,A.ANALYSISORGNAME,A.PRETREATWAY,O.ORDERNO,O.ORDERDESC,O.PARENTORDERNO,"
                    + "O.SAMPLEBEGINDATE,F.FOLDERNO,F.SAMPLEORGID,F.SAMPLEORGNAME,S.SAMPLENO,CC.MATERIAL,CC.COLOR,CC.CAPACITY",
                    values(orderId, organization), page, profileKey(requestPath), true);
        }
        if (requestPath.endsWith("/analysis-department-statistics/queries")) {
            return executeProjectionPage(
                    "SELECT A.ANALYSISDATE,COUNT(*) AS TESTCOUNT FROM T_EMC_ORDER_TASK A "
                    + "WHERE A.ANALYSISORGID=? GROUP BY A.ANALYSISDATE ORDER BY A.ANALYSISDATE ASC",
                    values(organization), page, profileKey(requestPath), false);
        }
        if (requestPath.endsWith("/personnel-workload-statistics/queries")) {
            return executeProjectionPage(
                    "SELECT A.ANALYSISUSERID,COUNT(*) AS TESTCOUNT FROM T_EMC_ORDER_TASK A "
                    + "WHERE A.ANALYSISORGID=? GROUP BY A.ANALYSISUSERID ORDER BY A.ANALYSISUSERID ASC",
                    values(organization), page, profileKey(requestPath), false);
        }
        if (requestPath.endsWith("/queries/analysis")) {
            return executeProjectionPage(
                    "SELECT A.TESTID,A.TESTNAME,O.ORDERNO,O.ORDERDESC,O.SAMPLEBEGINDATE,O.SAMPLEBEGINTIME "
                    + "FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_ORDER O ON A.ORDERID=O.ID "
                    + "LEFT JOIN T_EMC_PROJECT P ON A.PROJID=P.ID "
                    + "WHERE A.ANALYSISORGID=? AND (P.STATUS IS NULL OR P.STATUS <> 'cancel') "
                    + "ORDER BY A.TESTID DESC",
                    values(organization), page, profileKey(requestPath), true);
        }
        if (requestPath.endsWith("/unique-orders/queries")) {
            return executeProjectionPage(
                    "SELECT DISTINCT B.ID AS ORDERID,B.ORDERNO,C.PROJNO,C.PROJNAME,B.SAMPLEBEGINDATE,"
                    + "TO_CHAR(B.SAMPLEBEGINDATE,'YYYY-MM-DD') AS ORDERDATESTR,B.ORDERCATEGORY,B.LABELNO,"
                    + "B.PHFLAG,S.ACCEPTEDDATE,S.ACCEPTEDTIME,B.QCCATEGORYCODE,"
                    + "CASE WHEN C.COMPLEDATE IS NOT NULL AND C.COMPLEDATE <= TRUNC(SYSDATE)+3 THEN '1' ELSE '0' END AS PROJECTREMIND,"
                    + "C.COMPLEDATE,A.ANALYSISORGID FROM T_EMC_ORDER_TASK A "
                    + "LEFT JOIN T_EMC_ORDER B ON A.ORDERID=B.ID LEFT JOIN T_EMC_PROJECT C ON A.PROJID=C.ID "
                    + "LEFT JOIN T_EMC_SAMPLE S ON B.SAMPLEID=S.ID "
                    + "WHERE A.ANALYSISORGID=? ORDER BY B.ORDERNO",
                    values(organization), page, profileKey(requestPath), true);
        }
        if (requestPath.endsWith("/unique-tasks/queries")) {
            return executeProjectionPage(
                    "SELECT DISTINCT A.TESTID,A.TESTNAME,A.METHODNAME,A.TESTMETHODBASIS,A.SCHEMENAME,"
                    + "A.ASSIGNDATE,A.ANALYSISTYPE FROM T_EMC_ORDER_TASK A "
                    + "WHERE A.ANALYSISORGID=? ORDER BY A.TESTID DESC",
                    values(organization), page, profileKey(requestPath), true);
        }
        return null;
    }

    private String executeProjectionPage(String sql, List<Object> arguments, Page page,
            String profile, boolean emptyTotalMap) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + sql + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY 1) RN_ FROM ("
                + sql + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement query = connection.prepareStatement(countSql)) {
                bind(query, arguments, 1);
                try (ResultSet result = query.executeQuery()) { if (!result.next()) throw new SQLException("aggregate count missing"); total = result.getInt(1); }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                int index = bind(query, arguments, 1);
                query.setInt(index++, page.start()); query.setInt(index, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", TYPE_PROFILES.get(profile) == null ? new LinkedHashMap<String, String>() : TYPE_PROFILES.get(profile));
        envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size);
        envelope.put("rows", rows); envelope.put("total", total);
        envelope.put("totalMap", emptyTotalMap ? new LinkedHashMap<String, Object>() : singletonTotal(total));
        envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> singletonTotal(int total) {
        Map<String, Object> value = new LinkedHashMap<String, Object>(); value.put("qty_", total); return value;
    }

    private String emptyProfilePage(Page page, String profile, boolean emptyTotalMap) {
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", TYPE_PROFILES.get(profile) == null ? new LinkedHashMap<String, String>() : TYPE_PROFILES.get(profile));
        envelope.put("first", true); envelope.put("last", true); envelope.put("numberOfElements", 0);
        envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", new ArrayList<Object>());
        envelope.put("total", 0); envelope.put("totalMap", emptyTotalMap ? new LinkedHashMap<String, Object>() : singletonTotal(0)); envelope.put("totalPages", 0);
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static List<Object> values(Object... values) {
        List<Object> result = new ArrayList<Object>(); Collections.addAll(result, values); return result;
    }

    private static int bind(PreparedStatement statement, List<Object> values, int index) throws SQLException {
        for (Object value : values) statement.setObject(index++, value); return index;
    }

    private static String queryValue(String body, String key) {
        if (body == null || body.trim().isEmpty()) return null;
        JSONObject root = JSON.parseObject(body); if (root == null) return null;
        String value = root.getString(key);
        for (String nested : new String[] {"q", "param", "f"}) {
            if (value == null && root.getJSONObject(nested) != null) value = root.getJSONObject(nested).getString(key);
        }
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
            value.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
        return value;
    }

    private static final Map<String, Map<String, String>> TYPE_PROFILES = buildTypeProfiles();

    private static String profileKey(String requestPath) {
        if (requestPath == null) return "";
        if (requestPath.startsWith(PREFIX)) {
            String suffix = requestPath.substring(PREFIX.length());
            return suffix.isEmpty() ? "/queries" : suffix;
        }
        String ordersPrefix = "/secure/emc/module/bp/bp/orders";
        if (requestPath.startsWith(ordersPrefix)) return requestPath.substring(ordersPrefix.length());
        String projectChangePrefix = "/secure/emc/module/bp/edit/project-change/orders";
        if (requestPath.startsWith(projectChangePrefix)) return requestPath.substring(projectChangePrefix.length());
        String resultRunsPrefix = "/secure/emc/module/bp/result/runs";
        if (requestPath.startsWith(resultRunsPrefix)) return requestPath.substring(resultRunsPrefix.length());
        String samplePrefix = "/secure/emc/module/bp/sample/samples";
        if (requestPath.startsWith(samplePrefix)) return requestPath.substring(samplePrefix.length());
        return "";
    }

    private static Map<String, Map<String, String>> buildTypeProfiles() {
        Map<String, Map<String, String>> profiles = new LinkedHashMap<String, Map<String, String>>();
        addTypeProfile(profiles, "/queries",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=str" +
                "ing;containercategorycode=string;containercategoryid=number;containercategoryname=string;containergroup=string" +
                ";createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cu" +
                "pno=number;datasharing=string;equiptacquno=string;equiptid=number;equiptname=string;equiptsid=string;factoryno" +
                "=string;fixative=string;folderid=number;folderno=string;id=number;index1=string;index2=string;index3=string;in" +
                "dex4=string;index5=string;index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=str" +
                "ing;inputusername=string;itemgroup=string;itemgroupid=number;labelno=string;methodid=number;methodname=string;" +
                "methodversionid=number;mobileid=string;monitorpointname=string;monitorpointno=string;monitortmplgroupid=number" +
                ";monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=n" +
                "umber;orderno=string;orgid=string;originalorderno=string;parentflag=string;parentid=number;parentorderno=strin" +
                "g;partakeuserid=string;partakeusername=string;pretreatmentstatus=string;pretreatway=string;projid=number;projn" +
                "ame=string;projno=string;projstatus=string;qccategorycode=string;qccategoryid=number;qcname=string;receiveuser" +
                "name=string;relatedtest=string;remark=string;remarks=string;rn_=number;runid=string;samplebegindate=dateTime;s" +
                "amplebegintime=string;samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;sam" +
                "plereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=string;sampleweigh" +
                "t=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=string;schemename" +
                "=string;source=string;specification=string;specifymethod=string;status=string;stockbatchno=string;stockid=numb" +
                "er;storecondition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string" +
                ";tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;testmethodbasi" +
                "s=string;testmethodid=number;testname=string;usecategory=string;virtual=string;vocbottle=string;weighingdate=d" +
                "ateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/raw",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=str" +
                "ing;containercategorycode=string;containercategoryid=number;containercategoryname=string;containergroup=string" +
                ";createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cu" +
                "pno=number;datasharing=string;equiptacquno=string;equiptid=number;equiptname=string;equiptsid=string;factoryno" +
                "=string;fixative=string;folderid=number;folderno=string;id=number;index1=string;index2=string;index3=string;in" +
                "dex4=string;index5=string;index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=str" +
                "ing;inputusername=string;itemgroup=string;itemgroupid=number;labelno=string;methodid=number;methodname=string;" +
                "methodversionid=number;mobileid=string;monitorpointname=string;monitorpointno=string;monitortmplgroupid=number" +
                ";monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=n" +
                "umber;orderno=string;orgid=string;originalorderno=string;parentflag=string;parentid=number;parentorderno=strin" +
                "g;partakeuserid=string;partakeusername=string;pretreatmentstatus=string;pretreatway=string;projid=number;projn" +
                "ame=string;projno=string;projstatus=string;qccategorycode=string;qccategoryid=number;qcname=string;receiveuser" +
                "name=string;relatedtest=string;remark=string;remarks=string;rn_=number;runid=string;samplebegindate=dateTime;s" +
                "amplebegintime=string;samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;sam" +
                "plereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=string;sampleweigh" +
                "t=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=string;schemename" +
                "=string;source=string;specification=string;specifymethod=string;status=string;stockbatchno=string;stockid=numb" +
                "er;storecondition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string" +
                ";tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;testmethodbasi" +
                "s=string;testmethodid=number;testname=string;usecategory=string;virtual=string;vocbottle=string;weighingdate=d" +
                "ateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/searchable",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=str" +
                "ing;containercategorycode=string;containercategoryid=number;containercategoryname=string;containergroup=string" +
                ";createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cu" +
                "pno=number;datasharing=string;equiptacquno=string;equiptid=number;equiptname=string;equiptsid=string;factoryno" +
                "=string;fixative=string;folderid=number;folderno=string;id=number;index1=string;index2=string;index3=string;in" +
                "dex4=string;index5=string;index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=str" +
                "ing;inputusername=string;itemgroup=string;itemgroupid=number;labelno=string;methodid=number;methodname=string;" +
                "methodversionid=number;mobileid=string;monitorpointname=string;monitorpointno=string;monitortmplgroupid=number" +
                ";monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=n" +
                "umber;orderno=string;orgid=string;originalorderno=string;parentflag=string;parentid=number;parentorderno=strin" +
                "g;partakeuserid=string;partakeusername=string;pretreatmentstatus=string;pretreatway=string;projid=number;projn" +
                "ame=string;projno=string;projstatus=string;qccategorycode=string;qccategoryid=number;qcname=string;receiveuser" +
                "name=string;relatedtest=string;remark=string;remarks=string;rn_=number;runid=string;samplebegindate=dateTime;s" +
                "amplebegintime=string;samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;sam" +
                "plereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=string;sampleweigh" +
                "t=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=string;schemename" +
                "=string;source=string;specification=string;specifymethod=string;status=string;stockbatchno=string;stockid=numb" +
                "er;storecondition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string" +
                ";tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;testmethodbasi" +
                "s=string;testmethodid=number;testname=string;usecategory=string;virtual=string;vocbottle=string;weighingdate=d" +
                "ateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/runnable",
                "methodid=number;methodname=string;methodversionid=number;ordercategory=string;orderdate=dateTime;projectremind" +
                "=string;projid=number;projno=string;requiredcompletedate=dateTime;rn_=number;testclass=string;testid=number;te" +
                "stmethodbasis=string;testmethodid=number;testname=string;urgent=number"
        );
        addTypeProfile(profiles, "/queries/un-runnable",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;capacityinputuserid" +
                "=string;capacityinputusername=string;confirmusername=string;containercategorycode=string;containercategoryid=n" +
                "umber;containercategoryname=string;containergroup=string;createdbyid=string;createdbyname=string;createdbyorgi" +
                "d=string;createdbyorgname=string;createdtime=dateTime;cupno=number;datasharing=string;equiptacquno=string;equi" +
                "ptid=number;fixative=string;folderid=number;id=number;indexcategoryid=number;indexid=string;inputuserid=string" +
                ";inputusername=string;itemgroup=string;labelno=string;methodid=number;methodname=string;methodversionid=number" +
                ";mobileid=string;monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderid=nu" +
                "mber;orderno=string;originalorderno=string;parentflag=string;parentid=number;partakeuserid=string;partakeusern" +
                "ame=string;pretreatmentstatus=string;pretreatway=string;projid=number;qccategorycode=string;qccategoryid=numbe" +
                "r;qcname=string;receiveusername=string;relatedtest=string;remarks=string;rn_=number;runid=string;samplebottlen" +
                "o=string;sampleid=number;samplereceiverdate=dateTime;samplereceiverflag=string;samplereceiverid=string;sampler" +
                "eceivername=string;samplereceivertime=string;sampleweight=string;samplingequiptid=string;samplingequiptname=st" +
                "ring;samplingsubpackage=string;scanned=string;schemename=string;source=string;specifymethod=string;status=stri" +
                "ng;stockbatchno=string;stockid=number;storecondition=string;subcontractorid=number;subpackageno=string;subpack" +
                "agetype=string;taskhighlimit=string;tasklowlimit=string;testid=number;testmethodbasis=string;testmethodid=numb" +
                "er;testmethodtype=string;testname=string;virtual=string;vocbottle=string;weighingdate=dateTime;weightenddate=d" +
                "ateTime"
        );
        addTypeProfile(profiles, "/queries/analysis",
                "orderdesc=string;orderno=string;rn_=number;samplebegindate=dateTime;samplebegintime=string;testid=number;testn" +
                "ame=string"
        );
        addTypeProfile(profiles, "/queries/order-group",
                "analysisorgid=string;analysisorgname=string;analysistype=string;analysisuserid=string;containercategorycode=st" +
                "ring;containercategoryid=number;containercategoryname=string;containergroup=string;cupno=number;equiptacquno=s" +
                "tring;equiptid=number;equiptname=string;folderid=number;folderno=string;id=number;indexcategoryid=number;index" +
                "id=string;inputuserid=string;labelno=string;methodid=number;methodname=string;monitorpointname=string;monitort" +
                "mplid=string;ordercategory=string;orderdesc=string;orderid=number;orderno=string;parentid=number;parentorderno" +
                "=string;partakeuserid=string;projid=number;qccategorycode=string;qccategoryid=number;qcname=string;runid=strin" +
                "g;samplebegindate=dateTime;samplebegintime=string;schemename=string;status=string;stockbatchno=string;stockid=" +
                "number;subpackageno=string;testid=number;testmethodbasis=string;testmethodid=number;testname=string"
        );
        addTypeProfile(profiles, "/queries/order-group-runs",
                "accepteddate=dateTime;analysisorgid=string;analysisorgname=string;analysistype=string;analysisuserid=string;cl" +
                "ientordername=string;containercategorycode=string;containercategoryid=number;containercategoryname=string;cont" +
                "ainergroup=string;cupno=number;cycle=number;equiptacquno=string;equiptid=number;equiptname=string;folderid=num" +
                "ber;frequency=number;id=number;indexcategoryid=number;indexid=string;inputuserid=string;isconvert=string;label" +
                "no=string;methodid=number;methodname=string;monitorpointname=string;monitortmplid=string;ordercategory=string;" +
                "ordercontainerno=string;orderdesc=string;orderid=number;orderno=string;ordertaskequiptno=string;originalorder=" +
                "string;parentid=number;parentorderno=string;partakeuserid=string;projid=number;projno=string;qccategorycode=st" +
                "ring;qccategoryid=number;qcname=string;relatedorderno=string;relatedqcorderno=string;rn_=number;runid=string;s" +
                "amplebegindate=dateTime;samplebegintime=string;sampledate=dateTime;samplereceiverdate=dateTime;samplereceivert" +
                "ime=string;sampletime=string;schemename=string;status=string;stockbatchno=string;stockid=number;subpackageno=s" +
                "tring;testid=number;testmethodbasis=string;testmethodid=number;testname=string;usecategory=string"
        );
        addTypeProfile(profiles, "/unique-tasks/queries",
                "analysistype=string;assigndate=dateTime;methodname=string;schemename=string;testid=number;testmethodbasis=stri" +
                "ng;testname=string"
        );
        addTypeProfile(profiles, "/subpackages/queries",
                "addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;analysistype=string;ana" +
                "lysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequiptid=string;assistequip" +
                "tname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=string;containercategoryc" +
                "ode=string;containercategoryid=number;containercategoryname=string;containergroup=string;createdbyid=string;cr" +
                "eatedbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cupno=number;datasharing" +
                "=string;equiptacquno=string;equiptid=number;fixative=string;folderid=number;id=number;indexcategoryid=number;i" +
                "ndexid=string;inputuserid=string;inputusername=string;methodid=number;methodname=string;methodversionid=number" +
                ";mobileid=string;monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderid=nu" +
                "mber;originalorderno=string;parentflag=string;parentid=number;partakeuserid=string;partakeusername=string;pret" +
                "reatmentstatus=string;pretreatway=string;projid=number;qccategorycode=string;qccategoryid=number;qcname=string" +
                ";receiveusername=string;relatedtest=string;remarks=string;runid=string;samplebottleno=string;samplereceiverdat" +
                "e=dateTime;samplereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=stri" +
                "ng;sampleweight=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=str" +
                "ing;schemename=string;source=string;specifymethod=string;status=string;stockbatchno=string;stockid=number;stor" +
                "econdition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string;tasklo" +
                "wlimit=string;testid=number;testmethodbasis=string;testmethodid=number;testname=string;virtual=string;vocbottl" +
                "e=string;weighingdate=dateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/order-task",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;" +
                "analysisorgname=string;analysistype=string;analysisuserid=string;analysisusername=string;" +
                "assign=string;assigndate=dateTime;assistequiptid=string;assistequiptname=string;" +
                "calibrationequiptid=string;calibrationequiptname=string;confirmusername=string;" +
                "containercategorycode=string;containercategoryid=number;containercategoryname=string;" +
                "containergroup=string;createdbyid=string;createdbyname=string;createdbyorgid=string;" +
                "createdbyorgname=string;createdtime=dateTime;cupno=number;datasharing=string;equiptacquno=string;" +
                "equiptid=number;equiptname=string;equiptsid=string;factoryno=string;fixative=string;folderid=number;" +
                "folderno=string;id=number;index1=string;index2=string;index3=string;index4=string;index5=string;" +
                "index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=string;" +
                "inputusername=string;labelno=string;methodid=number;methodname=string;methodversionid=number;" +
                "mobileid=string;monitorpointname=string;monitortmplgroupid=number;monitortmplid=string;" +
                "oilfilmno=string;ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=number;" +
                "orderno=string;orgid=string;originalorderno=string;parentflag=string;parentid=number;" +
                "parentorderno=string;partakeuserid=string;partakeusername=string;pretreatmentstatus=string;" +
                "pretreatway=string;projid=number;projname=string;projno=string;projstatus=string;" +
                "qccategorycode=string;qccategoryid=number;qcname=string;receiveusername=string;relatedtest=string;" +
                "remark=string;remarks=string;rn_=number;runid=string;samplebegindate=dateTime;samplebegintime=string;" +
                "samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;" +
                "samplereceiverflag=string;samplereceiverid=string;samplereceivername=string;" +
                "samplereceivertime=string;sampleweight=string;samplingequiptid=string;samplingequiptname=string;" +
                "samplingsubpackage=string;scanned=string;schemename=string;source=string;specification=string;" +
                "specifymethod=string;status=string;stockbatchno=string;stockid=number;storecondition=string;" +
                "subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string;" +
                "tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;" +
                "testmethodbasis=string;testmethodid=number;testname=string;usecategory=string;virtual=string;" +
                "vocbottle=string;weighingdate=dateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/group-by-test",
                "ids=number;methodid=number;methodname=string;num=number;projno=string;testid=number;" +
                "testmethodid=number;testname=string"
        );
        addTypeProfile(profiles, "/queries/select-order-container-info",
                "analysisorgid=string;analysisorgname=string;capacity=string;color=string;" +
                "containercategorycode=string;containercategoryid=number;containercategoryname=string;" +
                "containergroup=string;fixative=string;folderid=number;folderno=string;ids=number;material=string;" +
                "num=number;ordercategory=string;orderdesc=string;orderno=string;parentorderno=string;" +
                "pretreatway=string;projid=number;qccategorycode=string;qcname=string;rn_=number;" +
                "samplebegindate=dateTime;sampleno=string;sampleorgid=string;sampleorgname=string;" +
                "storecondition=string;testname=string"
        );
        addTypeProfile(profiles, "/queries/selectContainerInfo",
                "containercategoryid=number;containercategoryname=string;containergroup=string;fixative=string;" +
                "itemgroup=string;multitestid=string;ordercontainerno=string;rn_=number;sampleweight=string;" +
                "storecondition=string;testname=string"
        );
        addTypeProfile(profiles, "/unique-item-groups/queries",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;" +
                "analysisorgname=string;analysistype=string;analysisuserid=string;analysisusername=string;" +
                "assign=string;assigndate=dateTime;assistequiptid=string;assistequiptname=string;" +
                "calibrationequiptid=string;calibrationequiptname=string;confirmusername=string;" +
                "containercategorycode=string;containercategoryid=number;containercategoryname=string;" +
                "containergroup=string;createdbyid=string;createdbyname=string;createdbyorgid=string;" +
                "createdbyorgname=string;createdtime=dateTime;cupno=number;datasharing=string;equiptacquno=string;" +
                "equiptid=number;equiptname=string;equiptsid=string;factoryno=string;fixative=string;folderid=number;" +
                "folderno=string;id=number;index1=string;index2=string;index3=string;index4=string;index5=string;" +
                "index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=string;" +
                "inputusername=string;itemgroup=string;itemgroupid=number;labelno=string;methodid=number;" +
                "methodname=string;methodversionid=number;mobileid=string;monitorpointname=string;" +
                "monitorpointno=string;monitortmplgroupid=number;monitortmplid=string;oilfilmno=string;" +
                "ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=number;orderno=string;" +
                "orgid=string;originalorderno=string;parentflag=string;parentid=number;parentorderno=string;" +
                "partakeuserid=string;partakeusername=string;pretreatmentstatus=string;pretreatway=string;" +
                "projid=number;projname=string;projno=string;projstatus=string;qccategorycode=string;" +
                "qccategoryid=number;qcname=string;receiveusername=string;relatedtest=string;remark=string;" +
                "remarks=string;rn_=number;runid=string;samplebegindate=dateTime;samplebegintime=string;" +
                "samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;" +
                "samplereceiverflag=string;samplereceiverid=string;samplereceivername=string;" +
                "samplereceivertime=string;sampleweight=string;samplingequiptid=string;samplingequiptname=string;" +
                "samplingsubpackage=string;scanned=string;schemename=string;source=string;specification=string;" +
                "specifymethod=string;status=string;stockbatchno=string;stockid=number;storecondition=string;" +
                "subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string;" +
                "tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;" +
                "testmethodbasis=string;testmethodid=number;testname=string;usecategory=string;virtual=string;" +
                "vocbottle=string;weighingdate=dateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/unique-orders/queries",
                "accepteddate=dateTime;acceptedtime=string;analysisorgid=string;compledate=dateTime;labelno=string;" +
                "ordercategory=string;orderdatestr=string;orderid=number;orderno=string;phflag=string;" +
                "projectremind=string;projname=string;projno=string;qccategorycode=string;rn_=number;" +
                "samplebegindate=dateTime"
        );
        addTypeProfile(profiles, "/order-tasks/queries", "");
        addTypeProfile(profiles, "/blank-order-tasks/queries", "");
        addTypeProfile(profiles, "/{id}/order-tasks/queries", "");
        addTypeProfile(profiles, "/analysis-department-statistics/queries",
                "analysisdate=dateTime;rn_=number;testcount=number"
        );
        addTypeProfile(profiles, "/personnel-workload-statistics/queries",
                "analysisuserid=string;rn_=number;testcount=number"
        );
        addTypeProfile(profiles, "/proj-order-categories/queries",
                "bizcategoryname=string;entrustorgname=string;monitortmplid=string;ordercategory=string;" +
                "projcreatedbyname=string;projid=number;projname=string;projno=string;rn_=number;testorgname=string"
        );
        return profiles;
    }

    private static void addTypeProfile(Map<String, Map<String, String>> profiles, String path, String encoded) {
        Map<String, String> types = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) {
            int separator = token.indexOf('=');
            if (separator > 0) types.put(token.substring(0, separator), token.substring(separator + 1));
        }
        profiles.put(path, types);
    }

    private static String camel(String label) {
        StringBuilder result = new StringBuilder(); boolean upper = false;
        for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (value == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(value) : value); upper = false;
        }
        return result.toString();
    }

    private static String type(int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number";
        return "string";
    }
    private static long numericId(String value) { if (value == null || !value.matches("[0-9]{1,30}")) throw new IllegalArgumentException("order-task id is invalid"); return Long.parseLong(value); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> queryInvalid(HttpServletRequest request, HttpServletResponse response) {
        String path = request == null ? "" : request.getRequestURI();
        String body = "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""
                + path.replace("\\", "\\\\").replace("\"", "\\\"")
                + "\",\"status\":500,\"timestamp\":" + System.currentTimeMillis() + ",\"stacktrace\":\"\"}";
        try { response.resetBuffer(); response.setStatus(500); response.setHeader("Content-Type", ""); response.getWriter().write(body); response.flushBuffer(); return null; }
        catch (java.io.IOException error) { return ResponseEntity.status(500).body(body); }
    }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Page {
        final int number; final int size;
        Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int number = p == null ? 1 : Math.max(1, p.getIntValue("n"));
            int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s")));
            return new Page(number, size);
        }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }
}
