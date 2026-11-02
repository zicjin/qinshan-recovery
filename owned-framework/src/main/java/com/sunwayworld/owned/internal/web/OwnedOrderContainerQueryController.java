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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned read seam for the two inherited order-container query aliases. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderContainerQueryController {
    private static final String CONTAINERS = "/secure/emc/module/bp/bp/orders/containers/queries";
    private static final String RECEIVE = "/secure/emc/module/bp/bp/orders/receive/containers/queries";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrderContainerQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("order-container dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {CONTAINERS, RECEIVE}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(principal.getOrgId(), Page.parse(body), request.getRequestURI().equals(RECEIVE))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page, boolean receive) throws SQLException {
        String base = "SELECT T.* FROM T_EMC_ORDER_CONTAINER T WHERE T.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>(); Map<String,String> types = new LinkedHashMap<String,String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) { count.setString(1, organization); try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("order-container count unavailable"); total = result.getInt(1); } }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) { query.setString(1, organization); query.setInt(2, page.start()); query.setInt(3, page.end()); try (ResultSet result = query.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); for (int i=1;i<=metadata.getColumnCount();i++) { String label=metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) types.put(camel(label), type(metadata.getColumnType(i))); } while (result.next()) rows.add(row(result, metadata)); } }
        }
        Map<String,Object> envelope = new LinkedHashMap<String,Object>(); envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total); Map<String,Object> totals = new LinkedHashMap<String,Object>(); totals.put("qty_", total); envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size)); return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String,Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String,Object> value=new LinkedHashMap<String,Object>(); for(int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(label))value.put(camel(label),JdbcValue.normalize(result.getObject(i)));}return value; }
    private static String camel(String label){StringBuilder b=new StringBuilder();boolean upper=false;for(char c:label.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){upper=true;continue;}b.append(upper?Character.toUpperCase(c):c);upper=false;}return b.toString();}
    private static String type(int value){return value==Types.DATE||value==Types.TIMESTAMP||value==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(value==Types.NUMERIC||value==Types.DECIMAL||value==Types.INTEGER||value==Types.BIGINT?"number":"string");}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.QUERY.INVALID\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
