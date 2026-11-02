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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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

/** Owned read seam for the inherited result-curve GenericResource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedCurveQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/result/curves";
    private static final String BASE_SQL =
            "SELECT T.* FROM (SELECT A.*, B.EQUIPTID AS EQUIPTNO, B.EQUIPTNAME "
            + "FROM T_EMC_CURVE A LEFT JOIN T_EMC_EQUIPT B ON A.EQUIPTID=B.ID) T "
            + "WHERE T.CREATEDBYORGID=?";
    private static final Map<String, String> TYPE_PROFILE = profile(
            "coefficient=string;colorvolume=string;confdate=dateTime;createdbyid=string;createdbyname=string;"
            + "createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;curvecategory=string;"
            + "curvecondition=string;curveno=string;curvevalue=string;equiptid=number;equiptname=string;"
            + "equiptno=string;expiredate=dateTime;folderid=number;id=number;intercept=string;internumber=string;"
            + "itemid=number;itemname=string;markingconcent=string;markingmanufacturer=string;markingname=string;"
            + "markingno=string;markingunitname=string;markingvalidate=number;methodid=number;methodname=string;"
            + "remark=string;rn_=number;slope=string;solconf=string;standqty=string;standqtyunit=string;"
            + "status=string;stdstockconcentration=string;stduserconcentration=string;testid=number;testname=string;"
            + "useconcent=string;useconcentunitname=string");
    private static final Map<String, String> RESOURCE_KEYS = resourceKeys();

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedCurveQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("curve dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(selectPage(principal.getOrgId(), Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]{1,30}")) return badRequest();
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(
                    BASE_SQL + " AND T.ID=?")) {
                statement.setString(1, principal.getOrgId());
                statement.setLong(2, Long.parseLong(id));
                try (ResultSet rows = statement.executeQuery()) {
                    return ok(rows.next() ? JSON.toJSONString(row(rows, rows.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}");
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

    private String selectPage(String organization, Page page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + BASE_SQL + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + BASE_SQL + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                statement.setString(1, organization);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("curve count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(pageSql)) {
                statement.setString(1, organization);
                statement.setInt(2, page.start());
                statement.setInt(3, page.end());
                try (ResultSet result = statement.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", TYPE_PROFILE);
        envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", page.number);
        envelope.put("pageSize", page.size);
        envelope.put("rows", rows);
        envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>();
        totals.put("qty_", total);
        envelope.put("totalMap", totals);
        envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> ext = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
            Object item = JdbcValue.normalize(result.getObject(i));
            if (item == null) continue;
            String key = RESOURCE_KEYS.get(label.toUpperCase(Locale.ROOT));
            if (key == null) ext.put(label.toLowerCase(Locale.ROOT), item);
            else value.put(key, item);
        }
        value.put("ext$", ext);
        return value;
    }

    private static Map<String, String> profile(String encoded) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) {
            int index = token.indexOf('=');
            if (index > 0) result.put(token.substring(0, index), token.substring(index + 1));
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> resourceKeys() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        String[] names = {
                "ID=id", "CURVENO=curveNo", "FOLDERID=folderId", "EQUIPTID=equiptId", "EQUIPTNO=equiptNo",
                "EQUIPTNAME=equiptName", "TESTID=testId", "TESTNAME=testName", "METHODID=methodId", "METHODNAME=methodName",
                "ITEMID=itemId", "ITEMNAME=itemName", "INTERCEPT=intercept", "SLOPE=slope", "COEFFICIENT=coefficient",
                "CURVETYPE=curveType", "CONFDATE=confDate", "EXPIREDATE=expireDate", "MARKINGNO=markingNo",
                "MARKINGNAME=markingName", "MARKINGCONCENT=markingConcent", "MARKINGUNITNAME=markingUnitName",
                "MARKINGVALIDATE=markingValidate", "MARKINGMANUFACTURER=markingManufacturer", "USECONCENT=useConcent",
                "USECONCENTUNITNAME=useConcentUnitName", "COLORVOLUME=colorVolume", "SOLCONF=solConf", "CURVECONDITION=curveCondition",
                "STATUS=status", "REMARK=remark", "CURVECATEGORY=curveCategory", "CREATEDBYID=createdById",
                "CREATEDBYNAME=createdByName", "CREATEDTIME=createdTime", "CREATEDBYORGID=createdByOrgId", "CREATEDBYORGNAME=createdByOrgName",
                "STANDQTY=standQty", "STANDQTYUNIT=standQtyUnit", "STOCKCONCENTRATION=stdStockConcentration",
                "STOCKUSERCONCENTRATION=stdUserConcentration", "CURVEVALUE=curveValue", "INTERNUMBER=internumber"
        };
        for (String name : names) {
            int index = name.indexOf('=');
            result.put(name.substring(0, index), name.substring(index + 1));
        }
        return Collections.unmodifiableMap(result);
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Page {
        final int number, size;
        Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject page = root == null ? null : root.getJSONObject("p");
            int number = page == null ? 1 : Math.max(1, page.getIntValue("n"));
            int size = page == null ? 50 : Math.min(500, Math.max(1, page.getIntValue("s")));
            return new Page(number, size);
        }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }
}
