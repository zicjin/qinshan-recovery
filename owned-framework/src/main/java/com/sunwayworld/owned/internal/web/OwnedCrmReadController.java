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

/** Owned Java read seam for CRM folder and opportunity-order resources. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedCrmReadController {
    private static final String FOLDER = "/secure/emc/module/mdm/crm/crmfolder";
    private static final String ORDERS = "/secure/emc/module/mdm/crm/orders";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedCrmReadController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("CRM dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {FOLDER + "/queries", FOLDER + "/queries/raw", FOLDER + "/queries/searchable", ORDERS + "/queries", ORDERS + "/queries/raw", ORDERS + "/queries/searchable"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(table(request.getRequestURI()), principal.getOrgId(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {FOLDER + "/info/status", ORDERS + "/info/status", FOLDER + "/{id}/info/status", ORDERS + "/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) { try { require(request); return ok("{\"ext$\":{}}"); } catch (AccessDeniedException denied) { return forbidden(); } catch (SQLException failure) { return unavailable(); } }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ); return boundary.currentPrincipal(request.getSession(false)); }
    private static String table(String path) { if (path != null && path.startsWith(FOLDER + "/")) return "T_CRM_OPPORTUNITY_FOLDERS"; if (path != null && path.startsWith(ORDERS + "/")) return "T_CRM_OPPORTUNITY_ORDERS"; throw new IllegalArgumentException("unsupported CRM path"); }
    private String selectPage(String table, String organization, Page page) throws SQLException { String base="SELECT T.* FROM "+table+" T WHERE T.CREATEDBYORGID=?";String count="SELECT COUNT(1) FROM ("+base+") Q";String paged="SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("+base+") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";int total;List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();Map<String,String> types=new LinkedHashMap<String,String>();try(Connection c=dataSource.getConnection()){try(PreparedStatement q=c.prepareStatement(count)){q.setString(1,organization);try(ResultSet r=q.executeQuery()){if(!r.next())throw new SQLException("CRM count unavailable");total=r.getInt(1);}}try(PreparedStatement q=c.prepareStatement(paged)){q.setString(1,organization);q.setInt(2,page.start());q.setInt(3,page.end());try(ResultSet r=q.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))types.put(camel(n),type(m.getColumnType(i)));}while(r.next())rows.add(row(r,m));}}}Map<String,Object> e=new LinkedHashMap<String,Object>();e.put("columnTypeMap",types);e.put("first",page.number==1);e.put("last",total==0||page.number>=pages(total,page.size));e.put("numberOfElements",rows.size());e.put("pageNumber",page.number);e.put("pageSize",page.size);e.put("rows",rows);e.put("total",total);Map<String,Object> tm=new LinkedHashMap<String,Object>();tm.put("qty_",total);e.put("totalMap",tm);e.put("totalPages",pages(total,page.size));return JSON.toJSONString(e,SerializerFeature.WriteMapNullValue);}
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object> v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))v.put(camel(n),JdbcValue.normalize(r.getObject(i)));}return v;}
    private static String camel(String label){StringBuilder b=new StringBuilder();boolean upper=false;for(char c:label.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){upper=true;continue;}b.append(upper?Character.toUpperCase(c):c);upper=false;}return b.toString();}
    private static String type(int value){return value==Types.DATE||value==Types.TIMESTAMP||value==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(value==Types.NUMERIC||value==Types.DECIMAL||value==Types.INTEGER||value==Types.BIGINT?"number":"string");}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);}private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");}private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.CRM.INVALID_QUERY\"}");}private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.CRM.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String body){JSONObject r=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=r==null?null:r.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
