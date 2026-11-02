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
import java.util.ArrayList;
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

/** Source-owned read seam for the inherited subpackage resource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedSubpackageQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/subpackages";
    private static final String BASE_SQL =
            "SELECT T.* FROM (SELECT A.*,B.PROJNAME,B.BIZCATEGORYID,B.TESTORGNAME "
            + "FROM T_EMC_SUBPACKAGE A LEFT JOIN T_EMC_PROJECT B ON A.PROJID=B.ID) T WHERE ";
    private static final Map<String, String> TYPE_PROFILE = profile(
            "bizcategoryid=number;completedate=dateTime;contact=string;contactemail=string;contactphone=string;"
            + "contractno=string;createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;"
            + "createdtime=dateTime;entrustorgid=string;entrustorgname=string;havecontract=string;id=number;"
            + "innersubcontractorid=string;innersubcontractorname=string;orgid=string;projid=number;projname=string;"
            + "projno=string;remark=string;rn_=number;sampling=string;sitecontact=string;sitephone=string;status=string;"
            + "subcontractorid=number;subpackagecategory=string;subpackageno=string;subpackagetype=string;"
            + "subpackagetotalprice=string;testorgname=string");

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedSubpackageQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("subpackage dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable",
            PREFIX + "/queries/roles/{roleId}"}, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(principal.getOrgId(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/{id}", PREFIX + "/{id}/action/print"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request); long numericId = numericId(id);
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(BASE_SQL + "T.ORGID=? AND T.ID=?")) {
                statement.setString(1, principal.getOrgId()); statement.setLong(2, numericId);
                try (ResultSet result = statement.executeQuery()) {
                    return ok(result.next() ? JSON.toJSONString(row(result, result.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}");
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}/columns/{column}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request, @PathVariable("id") String id, @PathVariable("column") String column) {
        try {
            OwnedPrincipal principal = require(request); long numericId = numericId(id); String selected = column(column);
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(
                    "SELECT " + selected + " FROM T_EMC_SUBPACKAGE WHERE ID=? AND ORGID=?")) {
                statement.setLong(1, numericId); statement.setString(2, principal.getOrgId());
                try (ResultSet result = statement.executeQuery()) { return ok(JSON.toJSONString(result.next() ? JdbcValue.normalize(result.getObject(1)) : null)); }
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
        String countSql = "SELECT COUNT(1) FROM (" + BASE_SQL + "T.ORGID=?) Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + BASE_SQL
                + "T.ORGID=?) Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                statement.setString(1, organization);
                try (ResultSet result = statement.executeQuery()) { if (!result.next()) throw new SQLException("subpackage count returned no row"); total = result.getInt(1); }
            }
            try (PreparedStatement statement = connection.prepareStatement(pageSql)) {
                statement.setString(1, organization); statement.setInt(2, page.start()); statement.setInt(3, page.end());
                try (ResultSet result = statement.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); while (result.next()) rows.add(row(result, metadata)); }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("columnTypeMap", TYPE_PROFILE);
        envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size);
        envelope.put("rows", rows); envelope.put("total", total); Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total);
        envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>(); Map<String, Object> ext = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) { String label = metadata.getColumnLabel(i); if ("RN_".equalsIgnoreCase(label)) continue; Object item = JdbcValue.normalize(result.getObject(i)); if (item == null) continue; value.put(camel(label), item); }
        value.put("ext$", ext); return value;
    }

    private static String column(String value) {
        String key = value == null ? "" : value.toUpperCase(Locale.ROOT);
        String[] allowed = {"ID", "SUBPACKAGENO", "PROJID", "PROJNO", "CONTRACTNO", "ORGID", "STATUS", "CREATEDTIME", "SUBPACKAGECATEGORY"};
        for (String candidate : allowed) if (candidate.equals(key)) return candidate;
        throw new IllegalArgumentException("unsupported subpackage column");
    }
    private static String camel(String label) { StringBuilder result = new StringBuilder(); boolean upper = false; for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) { if (value == '_') { upper = true; continue; } result.append(upper ? Character.toUpperCase(value) : value); upper = false; } return result.toString(); }
    private static long numericId(String value) { if (value == null || !value.matches("[0-9]{1,30}")) throw new IllegalArgumentException("subpackage id is invalid"); return Long.parseLong(value); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static Map<String, String> profile(String encoded) { Map<String, String> result = new LinkedHashMap<String, String>(); for (String token : encoded.split(";")) { int index = token.indexOf('='); if (index > 0) result.put(token.substring(0, index), token.substring(index + 1)); } return Collections.unmodifiableMap(result); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Page {
        final int number; final int size; Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) { JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body); JSONObject page = root == null ? null : root.getJSONObject("p"); int number = page == null ? 1 : Math.max(1, page.getIntValue("n")); int size = page == null ? 50 : Math.min(500, Math.max(1, page.getIntValue("s"))); return new Page(number, size); }
        int start() { return (number - 1) * size; } int end() { return number * size; }
    }
}
