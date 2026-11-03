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
import java.io.IOException;
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

/** Owned read seam for the inherited oil-film GenericResource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOilFilmQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/result/oil-films";
    private static final String BASE_SQL = "SELECT A.* FROM T_EMC_OIL_FILM A WHERE A.CREATEDBYORGID=?";
    private static final Map<String, String> TYPE_PROFILE = profile(
            "createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;"
            + "createdtime=dateTime;emptyfilterbarrelmonefive=string;emptyfilterbarrelmonefour=string;"
            + "emptyfilterbarrelmoneone=string;emptyfilterbarrelmonesix=string;emptyfilterbarrelmonethree=string;"
            + "emptyfilterbarrelmonetwo=string;id=number;oilfilmno=string;orderid=number;orderno=string;orgid=string;"
            + "projid=number;projno=string;rn_=number;weighingdate=dateTime;weighinguserid=string;weighingusername=string");
    private static final Map<String, String> RESOURCE_KEYS = resourceKeys();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOilFilmQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("oil film dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); Page page; try { page = Page.parse(body); } catch (RuntimeException invalid) { throw new MalformedOilFilmQueryException(invalid); } return ok(selectPage(principal.getOrgId(), page, body)); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedOilFilmQueryException malformed) { return unexpected(response, request.getRequestURI()); }
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
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(BASE_SQL + " AND A.ID=?")) {
                statement.setString(1, principal.getOrgId()); statement.setLong(2, Long.parseLong(id));
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

    private String selectPage(String organization, Page page, String body) throws SQLException {
        String orderId = orderId(body);
        String base = BASE_SQL;
        if (orderId != null) base += " AND A.OILFILMNO IN (SELECT O.OILFILMNO FROM T_EMC_ORDER O WHERE O.ID=?)";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                int i = 1; statement.setString(i++, organization); if (orderId != null) statement.setString(i, orderId);
                try (ResultSet result = statement.executeQuery()) { if (!result.next()) throw new SQLException("oil film count returned no row"); total = result.getInt(1); }
            }
            try (PreparedStatement statement = connection.prepareStatement(pageSql)) {
                int i = 1; statement.setString(i++, organization); if (orderId != null) statement.setString(i++, orderId); statement.setInt(i++, page.start()); statement.setInt(i, page.end());
                try (ResultSet result = statement.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); while (result.next()) rows.add(row(result, metadata)); }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>(); envelope.put("columnTypeMap", TYPE_PROFILE); envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total); Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total); envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static String orderId(String body) {
        JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
        String id = text(root, "orderId"); if (id == null && root != null) id = text(root.getJSONObject("p"), "orderId"); if (id == null && root != null) id = text(root.getJSONObject("f"), "orderId"); return id;
    }
    private static String text(JSONObject root, String key) { if (root == null || root.get(key) == null) return null; String value = String.valueOf(root.get(key)).trim(); return value.isEmpty() ? null : value; }
    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String, Object> value = new LinkedHashMap<String, Object>(); Map<String, Object> ext = new LinkedHashMap<String, Object>(); for (int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);if("RN_".equalsIgnoreCase(label))continue;Object item=JdbcValue.normalize(result.getObject(i));if(item==null)continue;String key=RESOURCE_KEYS.get(label.toUpperCase(Locale.ROOT));if(key==null)ext.put(label.toLowerCase(Locale.ROOT),item);else value.put(key,item);}value.put("ext$",ext);return value; }
    private static Map<String, String> profile(String encoded) { Map<String,String> result=new LinkedHashMap<String,String>(); for(String token:encoded.split(";")){int i=token.indexOf('=');if(i>0)result.put(token.substring(0,i),token.substring(i+1));}return Collections.unmodifiableMap(result); }
    private static Map<String, String> resourceKeys() { Map<String,String> result=new LinkedHashMap<String,String>(); String[] names={"ID=id","OILFILMNO=oilFilmNo","WEIGHINGUSERID=weighingUserId","WEIGHINGUSERNAME=weighingUserName","PROJID=projId","ORDERID=orderId","ORDERNO=orderNo","PROJNO=projNo","WEIGHINGDATE=weighingDate","EMPTYFILTERBARRELMONEONE=emptyFilterBarrelMOneOne","EMPTYFILTERBARRELMONETWO=emptyFilterBarrelMOneTwo","EMPTYFILTERBARRELMONETHREE=emptyFilterBarrelMOneThree","EMPTYFILTERBARRELMONEFOUR=emptyFilterBarrelMOneFour","EMPTYFILTERBARRELMONEFIVE=emptyFilterBarrelMOneFive","EMPTYFILTERBARRELMONESIX=emptyFilterBarrelMOneSix","CREATEDBYID=createdById","CREATEDBYNAME=createdByName","CREATEDTIME=createdTime","CREATEDBYORGID=createdByOrgId","CREATEDBYORGNAME=createdByOrgName","ORGID=orgId"};for(String name:names){int i=name.indexOf('=');result.put(name.substring(0,i),name.substring(i+1));}return Collections.unmodifiableMap(result); }
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static ResponseEntity<String> unexpected(HttpServletResponse response,String path){String body=unexpectedBody(path);if(response!=null){try{response.resetBuffer();response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());response.getWriter().write(body);response.getWriter().flush();return null;}catch(IOException ignored){}}return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);}
    private static String unexpectedBody(String path){String safe=path==null?"":path.replace("\\","\\\\").replace("\"","\\\"");return "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""+safe+"\",\"stacktrace\":\"owned-oil-film-malformed-query\",\"status\":500,\"timestamp\":\""+new java.util.Date().toString()+"\"}";}
    private static final class MalformedOilFilmQueryException extends IllegalArgumentException{MalformedOilFilmQueryException(Throwable cause){super("malformed oil-film query",cause);}}
    private static final class Page { final int number,size; Page(int n,int s){number=n;size=s;} static Page parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;} }
}
