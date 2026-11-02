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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Shared inherited GenericResource read seam for deliveries and order labels. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedDeliveryOrderLabelQueryController {
    private static final String DELIVERY = "/secure/emc/module/bp/deliveries";
    private static final String LABEL = "/secure/emc/module/bp/order/order-labels";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedDeliveryOrderLabelQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("query dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {DELIVERY + "/queries", DELIVERY + "/queries/raw", DELIVERY + "/queries/searchable",
            LABEL + "/queries", LABEL + "/queries/raw", LABEL + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            boolean delivery = request.getRequestURI().startsWith(DELIVERY);
            return ok(page(read(delivery, principal.getOrgId(), Query.parse(body))));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {DELIVERY + "/info/status", LABEL + "/info/status",
            DELIVERY + "/{id}/info/status", LABEL + "/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"auditStatus\":{},\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {DELIVERY + "/{id}", LABEL + "/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("invalid id");
            boolean delivery = request.getRequestURI().startsWith(DELIVERY);
            String sql = delivery ? "SELECT A.*,B.PROJNO,B.PROJNAME,B.TESTORGNAME FROM T_EMC_DELIVERY A LEFT JOIN T_EMC_PROJECT B ON A.PROJID=B.ID WHERE A.CREATEDBYORGID=? AND A.ID=?"
                    : "SELECT A.* FROM T_EMC_ORDER_LABEL A WHERE A.ID=?";
            try (Connection c = dataSource.getConnection(); PreparedStatement q = c.prepareStatement(sql)) {
                int i=1; if (delivery) q.setString(i++, principal.getOrgId()); q.setLong(i, Long.parseLong(id));
                try (ResultSet r=q.executeQuery()) { return ok(r.next() ? JSON.toJSONString(row(r,r.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}"); }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private QueryResult read(boolean delivery, String org, Query q) throws SQLException {
        String source = delivery ? "SELECT A.*,B.PROJNO,B.PROJNAME,B.TESTORGNAME FROM T_EMC_DELIVERY A LEFT JOIN T_EMC_PROJECT B ON A.PROJID=B.ID WHERE A.CREATEDBYORGID=?"
                : "SELECT A.* FROM T_EMC_ORDER_LABEL A";
        int total; List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>(); Map<String,String> types=new LinkedHashMap<String,String>();
        String count = "SELECT COUNT(1) FROM (" + source + ") Q";
        String page = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID " + (delivery ? "ASC" : "DESC") + ") RN_ FROM (" + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection c=dataSource.getConnection()) {
            try (PreparedStatement x=c.prepareStatement(count)) { int i=1; if(delivery)x.setString(i++,org); try(ResultSet r=x.executeQuery()){if(!r.next())throw new SQLException("count missing"); total=r.getInt(1);} }
            try (PreparedStatement x=c.prepareStatement(page)) { int i=1; if(delivery)x.setString(i++,org); x.setInt(i++,q.start()); x.setInt(i,q.end()); try(ResultSet r=x.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int j=1;j<=m.getColumnCount();j++){String n=m.getColumnLabel(j);if(!"RN_".equalsIgnoreCase(n))types.put(camel(n),type(m.getColumnType(j)));}while(r.next())rows.add(row(r,m));} }
        }
        return new QueryResult(rows,types,total,q.number,q.size);
    }
    private static String page(QueryResult r){Map<String,Object>b=new LinkedHashMap<String,Object>();b.put("columnTypeMap",r.types);b.put("first",r.number==1);b.put("last",r.total==0||r.number>=pages(r.total,r.size));b.put("numberOfElements",r.rows.size());b.put("pageNumber",r.number);b.put("pageSize",r.size);b.put("rows",r.rows);b.put("total",r.total);Map<String,Object>t=new LinkedHashMap<String,Object>();t.put("qty_",r.total);b.put("totalMap",t);b.put("totalPages",pages(r.total,r.size));return JSON.toJSONString(b,SerializerFeature.WriteMapNullValue);}
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object>v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))v.put(camel(n),JdbcValue.normalize(r.getObject(i)));}return v;}
    private static String camel(String n){StringBuilder b=new StringBuilder();boolean u=false;for(char c:n.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){u=true;continue;}b.append(u?Character.toUpperCase(c):c);u=false;}return b.toString();}
    private static String type(int t){return t==Types.DATE||t==Types.TIMESTAMP||t==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==Types.NUMERIC||t==Types.DECIMAL||t==Types.INTEGER||t==Types.BIGINT?"number":"string");}
    private static int pages(int t,int s){return t==0?0:(t+s-1)/s;} private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().header("Cache-Control","no-store").body(b);} private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");} private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.QUERY.INVALID\"}");} private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.QUERY.UNAVAILABLE\"}");}
    private static final class Query{final int number,size;Query(int n,int s){number=n;size=s;}static Query parse(String b){JSONObject o=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=o==null?null:o.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Query(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
    private static final class QueryResult{final List<Map<String,Object>> rows;final Map<String,String>types;final int total,number,size;QueryResult(List<Map<String,Object>>r,Map<String,String>t,int c,int n,int s){rows=r;types=t;total=c;number=n;size=s;}}
}
