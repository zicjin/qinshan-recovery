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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned read seam for the inherited project-edit-log GenericResource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedProjectEditLogQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/project/project-edit-logs";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedProjectEditLogQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("project-edit-log dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(principal.getOrgId(), Query.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Query query) throws SQLException {
        String source = "SELECT T.* FROM T_EMC_PROJECT_EDIT_LOG T WHERE T.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>(); Map<String,String> types = new LinkedHashMap<String,String>();
        try (Connection connection=dataSource.getConnection()) {
            try (PreparedStatement count=connection.prepareStatement(countSql)) { count.setString(1,organization); try(ResultSet result=count.executeQuery()){if(!result.next())throw new SQLException("project-edit-log count unavailable");total=result.getInt(1);} }
            try (PreparedStatement page=connection.prepareStatement(pageSql)) { page.setString(1,organization); page.setInt(2,query.start()); page.setInt(3,query.end()); try(ResultSet result=page.executeQuery()){ResultSetMetaData metadata=result.getMetaData();for(int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(label))types.put(camel(label),type(metadata.getColumnType(i)));}while(result.next())rows.add(row(result,metadata));} }
        }
        Map<String,Object> envelope=new LinkedHashMap<String,Object>(); envelope.put("columnTypeMap",types); envelope.put("first",query.number==1); envelope.put("last",total==0||query.number>=pages(total,query.size)); envelope.put("numberOfElements",rows.size()); envelope.put("pageNumber",query.number); envelope.put("pageSize",query.size); envelope.put("rows",rows); envelope.put("total",total); Map<String,Object> totals=new LinkedHashMap<String,Object>(); totals.put("qty_",total); envelope.put("totalMap",totals); envelope.put("totalPages",pages(total,query.size)); return JSON.toJSONString(envelope,SerializerFeature.WriteMapNullValue);
    }

    private static Map<String,Object> row(ResultSet result,ResultSetMetaData metadata)throws SQLException{Map<String,Object> value=new LinkedHashMap<String,Object>();for(int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(label))value.put(camel(label),JdbcValue.normalize(result.getObject(i)));}value.put("ext$",new LinkedHashMap<String,Object>());return value;}
    private static String camel(String label){String n=label.toLowerCase(Locale.ROOT);StringBuilder value=new StringBuilder();boolean upper=false;for(char c:n.toCharArray()){if(c=='_'){upper=true;continue;}value.append(upper?Character.toUpperCase(c):c);upper=false;}String compact=value.toString();if("createdbyid".equals(compact))return "createdById";if("createdbyname".equals(compact))return "createdByName";if("createdtime".equals(compact))return "createdTime";if("createdbyorgid".equals(compact))return "createdByOrgId";if("createdbyorgname".equals(compact))return "createdByOrgName";if("projid".equals(compact))return "projId";if("projno".equals(compact))return "projNo";return compact;}
    private static String type(int sqlType){return sqlType==Types.DATE||sqlType==Types.TIMESTAMP||sqlType==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(sqlType==Types.NUMERIC||sqlType==Types.DECIMAL||sqlType==Types.INTEGER||sqlType==Types.BIGINT?"number":"string");}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;} private static ResponseEntity<String>ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).header("Cache-Control","no-store").body(body);} private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");} private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PROJECT_EDIT_LOG.INVALID_QUERY\"}");} private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PROJECT_EDIT_LOG.UNAVAILABLE\"}");}
    private static final class Query{final int number,size;Query(int n,int s){number=n;size=s;}static Query parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int number=p==null?1:Math.max(1,p.getIntValue("n"));int size=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Query(number,size);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
