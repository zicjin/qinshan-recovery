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
import java.io.IOException;
import java.util.ArrayList;
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

/** Owned Java read seam for the inherited order GenericResource family. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/bp/orders";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrderQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("order dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable",
            PREFIX + "/queries/roles/{roleId}"}, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            RequestFilter filter;
            try { filter = RequestFilter.parse(body); }
            catch (IllegalArgumentException invalid) { throw new MalformedOrderQueryException(invalid); }
            return ok(page(read(principal.getOrgId(), filter)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedOrderQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
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
            if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("order id is invalid");
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(
                    BASE_SQL + " WHERE O.CREATEDBYORGID=? AND O.ID=?")) {
                query.setString(1, principal.getOrgId()); query.setLong(2, Long.parseLong(id));
                try (ResultSet rows = query.executeQuery()) {
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

    private static final String BASE_SQL =
            "SELECT O.*,F.PROCESSSTATUS AS FOLDERSTATUS,F.MONITORPOINTNAME,F.MONITORPOINTNO,"
            + "P.PROJNO,P.PROJNAME,P.SAMPLE,F.FOLDERNO,F.SAMPLEUSERID,F.SAMPLEUSERNAME,F.PLANSAMPLEDATE,"
            + "MT.ORDERCATEGORYCODE,S.ACCEPTEDTIME FROM T_EMC_ORDER O "
            + "LEFT JOIN T_EMC_FOLDER F ON F.ID=O.FOLDERID "
            + "LEFT JOIN T_EMC_PROJECT P ON P.ID=O.PROJID "
            + "LEFT JOIN T_EMC_SAMPLE S ON O.SAMPLEID=S.ID "
            + "LEFT JOIN T_EMC_MONITOR_TMPL MT ON O.MONITORTMPLID=MT.ID";

    private QueryResult read(String organization, RequestFilter filter) throws SQLException {
        String scoped = BASE_SQL + " WHERE O.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + scoped + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.FOLDERID ASC,Q.ORDERNO ASC,Q.ID DESC) RN_ FROM ("
                + scoped + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) {
                count.setString(1, organization);
                try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("order count missing"); total = result.getInt(1); }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization); query.setInt(2, filter.start()); query.setInt(3, filter.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData meta = result.getMetaData();
                    for (int i=1;i<=meta.getColumnCount();i++) types.put(camel(meta.getColumnLabel(i)), type(meta.getColumnType(i)));
                    while (result.next()) rows.add(row(result, meta));
                }
            }
        }
        return new QueryResult(rows, types, total, filter.number, filter.size);
    }

    private static String page(QueryResult result) {
        Map<String,Object> body=new LinkedHashMap<String,Object>(); body.put("columnTypeMap",result.types);
        body.put("first",result.number==1); body.put("last",result.total==0 || result.number>=pages(result.total,result.size));
        body.put("numberOfElements",result.rows.size()); body.put("pageNumber",result.number); body.put("pageSize",result.size);
        body.put("rows",result.rows); body.put("total",result.total); Map<String,Object> total=new LinkedHashMap<String,Object>(); total.put("qty_",result.total);
        body.put("totalMap",total); body.put("totalPages",pages(result.total,result.size)); return JSON.toJSONString(body,SerializerFeature.WriteMapNullValue);
    }
    private static Map<String,Object> row(ResultSet result, ResultSetMetaData meta) throws SQLException { Map<String,Object> v=new LinkedHashMap<String,Object>(); for(int i=1;i<=meta.getColumnCount();i++){String n=meta.getColumnLabel(i);if("RN_".equalsIgnoreCase(n))continue;v.put(camel(n),JdbcValue.normalize(result.getObject(i)));}return v; }
    private static String camel(String name){StringBuilder b=new StringBuilder();boolean up=false;for(char c:name.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){up=true;continue;}b.append(up?Character.toUpperCase(c):c);up=false;}return b.toString();}
    private static String type(int t){return t==Types.DATE||t==Types.TIMESTAMP||t==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==Types.NUMERIC||t==Types.DECIMAL||t==Types.INTEGER||t==Types.BIGINT?"number":"string");}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().header("Cache-Control","no-store").body(body);}
    private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");}
    private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.ORDER.INVALID_QUERY\"}");}
    private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ORDER.UNAVAILABLE\"}");}

    private static ResponseEntity<String> unexpected(HttpServletResponse response, String path) {
        if (response != null) {
            try {
                response.resetBuffer();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write(unexpectedBody(path));
                response.getWriter().flush();
                return null;
            } catch (IOException ignored) { /* fall through to a normal response */ }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(unexpectedBody(path));
    }
    private static String unexpectedBody(String path) {
        String safePath = path == null ? "" : path.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""
                + safePath + "\",\"stacktrace\":\"owned-order-malformed-query\",\"status\":500,\"timestamp\":\""
                + new java.util.Date().toString() + "\"}";
    }
    private static final class MalformedOrderQueryException extends IllegalArgumentException {
        MalformedOrderQueryException(Throwable cause) { super("malformed order query", cause); }
    }

    private static final class RequestFilter { final int number,size; RequestFilter(int n,int s){number=n;size=s;} static RequestFilter parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new RequestFilter(n,s);} int start(){return(number-1)*size;}int end(){return number*size;}int pageSize(){return size;} }
    private static final class QueryResult { final List<Map<String,Object>> rows;final Map<String,String> types;final int total,number,size;QueryResult(List<Map<String,Object>> r,Map<String,String> t,int total,int n,int s){rows=r;types=t;this.total=total;number=n;size=s;} }
}
