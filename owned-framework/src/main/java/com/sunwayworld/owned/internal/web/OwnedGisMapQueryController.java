package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

/** Owned Java read seam for the GIS map filter queries. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedGisMapQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/info/gis-maps";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedGisMapQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("GIS dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/monitorPointCategories/queries", PREFIX + "/testNames/queries", PREFIX + "/itemAliasNames/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Filter filter = Filter.parse(body);
            String path = request.getRequestURI();
            if (path.endsWith("/monitorPointCategories/queries")) return ok(monitorPointCategories(principal.getOrgId(), filter));
            if (path.endsWith("/testNames/queries")) return ok(testNames(principal.getOrgId(), filter));
            return ok(itemAliasNames(principal.getOrgId(), filter));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String monitorPointCategories(String organization, Filter filter) throws SQLException {
        String sql = "SELECT DISTINCT F.MONITORPOINTCATEGORYID,F.MONITORPOINTCATEGORY " + from() + where(filter, false, false) + " ORDER BY F.MONITORPOINTCATEGORYID";
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        try (Connection connection=dataSource.getConnection(); PreparedStatement query=connection.prepareStatement(sql)) {
            int index=bind(query, organization, filter, 1, null, null);
            try (ResultSet result=query.executeQuery()) { while(result.next()){Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("monitorPointCategoryId",result.getObject(1));row.put("monitorPointCategory",result.getObject(2));rows.add(row);} }
        }
        return JSON.toJSONString(rows, SerializerFeature.WriteMapNullValue);
    }

    private String testNames(String organization, Filter filter) throws SQLException {
        String sql = "SELECT DISTINCT A.TESTMETHODID,A.TESTNAME " + from() + where(filter, true, false) + " ORDER BY A.TESTNAME";
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        try (Connection connection=dataSource.getConnection(); PreparedStatement query=connection.prepareStatement(sql)) {
            bind(query, organization, filter, 1, null, null);
            try (ResultSet result=query.executeQuery()) { while(result.next()){Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("testMethodId",result.getObject(1));row.put("testName",result.getObject(2));rows.add(row);} }
        }
        return JSON.toJSONString(rows, SerializerFeature.WriteMapNullValue);
    }

    private String itemAliasNames(String organization, Filter filter) throws SQLException {
        String sql = "SELECT DISTINCT A.ITEMID,A.ITEMALIASNAME " + from() + where(filter, true, true) + " ORDER BY A.ITEMALIASNAME";
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        try (Connection connection=dataSource.getConnection(); PreparedStatement query=connection.prepareStatement(sql)) {
            bind(query, organization, filter, 1, null, "1");
            try (ResultSet result=query.executeQuery()) { while(result.next()){Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("itemId",result.getObject(1));row.put("itemAliasName",result.getObject(2));rows.add(row);} }
        }
        return JSON.toJSONString(rows, SerializerFeature.WriteMapNullValue);
    }

    private static String from(){return "FROM T_EMC_REPORT_RESULT A LEFT JOIN T_EMC_FOLDER F ON F.ID=A.FOLDERID";}
    private static String where(Filter filter, boolean testMethod, boolean item) { StringBuilder sql=new StringBuilder(" WHERE A.CREATEDBYORGID=?"); if(filter.beginDate!=null)sql.append(" AND A.SAMPLEBEGINDATE>=?"); if(filter.endDate!=null)sql.append(" AND A.SAMPLEBEGINDATE<=?"); if(filter.monitorPointCategory!=null)sql.append(" AND F.MONITORPOINTCATEGORYID=?"); if(testMethod && filter.testMethodId!=null)sql.append(" AND A.TESTMETHODID=?"); if(item)sql.append(" AND A.REPORTDISPLAY=?"); return sql.toString(); }
    private static int bind(PreparedStatement query,String organization,Filter filter,int index,String ignored,String reportDisplay)throws SQLException { query.setString(index++,organization); if(filter.beginDate!=null)query.setDate(index++,filter.beginDate); if(filter.endDate!=null)query.setDate(index++,filter.endDate); if(filter.monitorPointCategory!=null)query.setString(index++,filter.monitorPointCategory); if(filter.testMethodId!=null && ignored==null)query.setLong(index++,filter.testMethodId.longValue()); if(reportDisplay!=null)query.setString(index++,reportDisplay); return index; }
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.GIS.INVALID_QUERY\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.GIS.UNAVAILABLE\"}");}
    private static final class Filter { final Date beginDate,endDate; final String monitorPointCategory; final Long testMethodId; Filter(Date b,Date e,String m,Long t){beginDate=b;endDate=e;monitorPointCategory=m;testMethodId=t;} static Filter parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject q=root==null?null:root.getJSONObject("q");if(q==null&&root!=null)q=root.getJSONObject("f");if(q==null)q=root;return new Filter(date(q==null?null:q.getString("beginDate")),date(q==null?null:q.getString("endDate")),value(q,"monitorPointCategory"),longValue(value(q,"testName")));} static String value(JSONObject o,String key){return o==null?null:o.getString(key);} static Date date(String value){if(value==null||value.trim().isEmpty())return null;try{return Date.valueOf(value.trim());}catch(IllegalArgumentException e){throw new IllegalArgumentException("invalid GIS date");}} static Long longValue(String value){if(value==null||value.trim().isEmpty())return null;try{return Long.valueOf(value.trim());}catch(NumberFormatException e){return null;}} }
}
