package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
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

/** Shared read seam for small MDM resource extensions with real Oracle tables. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMdmSpecializedReadController {
    private static final Map<String, Resource> RESOURCES = resources();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    public OwnedMdmSpecializedReadController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) { if (boundary == null || dataSource == null) throw new IllegalArgumentException("MDM specialized dependencies are required"); this.boundary = boundary; this.dataSource = dataSource; }

    @PostMapping(path = {
            "/secure/emc/module/mdm/basemdm/biz-categories/biz-class/queries",
            "/secure/emc/module/mdm/basemdm/certs/certs/queries/choosable",
            "/secure/emc/module/mdm/basemdm/seal-rules/rule-detailss/queries"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { return ok(selectPage(resource(request), require(request).getOrgId(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = "/secure/emc/module/mdm/basemdm/biz-categories/biz-class/queries/selectable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> selectable(HttpServletRequest request) {
        try { return ok(selectPage(RESOURCES.get("/secure/emc/module/mdm/basemdm/biz-categories/biz-class/queries"), require(request).getOrgId(), new Page(1, 50))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/mdm/basemdm/biz-categories/biz-class/info/status",
            "/secure/emc/module/mdm/basemdm/certs/certs/info/status",
            "/secure/emc/module/mdm/basemdm/seal-rules/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) { try { require(request); return ok("{\"ext$\":{}}"); } catch (AccessDeniedException denied) { return forbidden(); } catch (SQLException failure) { return unavailable(); } }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ); return boundary.currentPrincipal(request.getSession(false)); }
    private static Resource resource(HttpServletRequest request) { String path=request==null?"":request.getRequestURI(); for(Map.Entry<String,Resource> e:RESOURCES.entrySet())if(path.startsWith(e.getKey()))return e.getValue(); throw new IllegalArgumentException("unsupported MDM specialized resource"); }
    private String selectPage(Resource resource,String org,Page page)throws SQLException{String base="SELECT T.* FROM "+resource.table+" T WHERE T.CREATEDBYORGID=?";String countSql="SELECT COUNT(1) FROM ("+base+") Q";String pageSql="SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("+base+") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";int total;List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();Map<String,String> types=new LinkedHashMap<String,String>();try(Connection c=dataSource.getConnection()){try(PreparedStatement q=c.prepareStatement(countSql)){q.setString(1,org);try(ResultSet r=q.executeQuery()){if(!r.next())throw new SQLException("specialized count unavailable");total=r.getInt(1);}}try(PreparedStatement q=c.prepareStatement(pageSql)){q.setString(1,org);q.setInt(2,page.start());q.setInt(3,page.end());try(ResultSet r=q.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))types.put(n.toLowerCase(Locale.ROOT),type(m.getColumnType(i)));}while(r.next())rows.add(row(r,m));}}}Map<String,Object>b=new LinkedHashMap<String,Object>();b.put("columnTypeMap",types);b.put("first",page.number==1);b.put("last",total==0||page.number>=pages(total,page.size));b.put("numberOfElements",rows.size());b.put("pageNumber",page.number);b.put("pageSize",page.size);b.put("rows",rows);b.put("total",total);Map<String,Object>tm=new LinkedHashMap<String,Object>();tm.put("qty_",total);b.put("totalMap",tm);b.put("totalPages",pages(total,page.size));return JSON.toJSONString(b,SerializerFeature.WriteMapNullValue);}
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object>v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))v.put(camel(n),JdbcValue.normalize(r.getObject(i)));}return v;}
    private static String camel(String n){String k=n==null?"":n.toUpperCase(Locale.ROOT);if("CREATEDBYID".equals(k))return "createdById";if("CREATEDBYNAME".equals(k))return "createdByName";if("CREATEDTIME".equals(k))return "createdTime";if("CREATEDBYORGID".equals(k))return "createdByOrgId";if("CREATEDBYORGNAME".equals(k))return "createdByOrgName";StringBuilder b=new StringBuilder();boolean u=false;for(char c:n.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){u=true;continue;}b.append(u?Character.toUpperCase(c):c);u=false;}return b.toString();}
    private static String type(int t){return t==Types.DATE||t==Types.TIMESTAMP||t==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==Types.NUMERIC||t==Types.DECIMAL||t==Types.INTEGER||t==Types.BIGINT?"number":"string");}
    private static int pages(int t,int s){return t==0?0:(t+s-1)/s;}private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);}private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");}private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.QUERY.INVALID\"}");}private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static Map<String,Resource> resources(){Map<String,Resource>v=new LinkedHashMap<String,Resource>();v.put("/secure/emc/module/mdm/basemdm/biz-categories/biz-class/queries",new Resource("T_EMC_BIZ_CATEGORY"));v.put("/secure/emc/module/mdm/basemdm/certs/certs/queries/choosable",new Resource("T_EMC_CERT"));v.put("/secure/emc/module/mdm/basemdm/seal-rules/rule-detailss/queries",new Resource("T_EMC_SEAL_RULE_DETAILS"));return Collections.unmodifiableMap(v);}
    private static final class Resource{final String table;Resource(String t){table=t;}}private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String b){com.alibaba.fastjson.JSONObject o=b==null||b.trim().isEmpty()?new com.alibaba.fastjson.JSONObject():JSON.parseObject(b);com.alibaba.fastjson.JSONObject p=o==null?null:o.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
