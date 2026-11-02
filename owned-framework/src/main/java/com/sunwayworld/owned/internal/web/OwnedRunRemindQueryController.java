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

/** Owned read seam for the inherited run-remind resource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedRunRemindQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/result/run-reminds";
    private static final String BASE_SQL = "SELECT A.* FROM T_EMC_RUN_REMIND A WHERE A.CREATEDBYORGID=?";
    private static final Map<String, String> TYPE_PROFILE = profile("abnormalexplain=string;checkresult=string;checktype=string;createdbyorgname=string;fieldname=string;id=number;qccategorycode=string;remind=string;rn_=number;value=string");
    private static final Map<String, String> RESOURCE_KEYS = resourceKeys();
    private final OwnedIdentityHttpBoundary boundary; private final DataSource dataSource;
    public OwnedRunRemindQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) { if (boundary == null || dataSource == null) throw new IllegalArgumentException("run-remind dependencies are required"); this.boundary=boundary; this.dataSource=dataSource; }

    @PostMapping(path={PREFIX+"/queries",PREFIX+"/queries/raw",PREFIX+"/queries/searchable"},consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,@RequestBody(required=false) String body){try{OwnedPrincipal p=require(request);return ok(selectPage(p.getOrgId(),Page.parse(body)));}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    @PostMapping(path=PREFIX+"/queries/runRemind/{runId}",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> runRemind(HttpServletRequest request,@PathVariable("runId") String runId,@RequestBody(required=false) String body){try{OwnedPrincipal p=require(request);if(runId==null||!runId.matches("[A-Za-z0-9_-]{1,80}"))return badRequest();return ok(selectRunRemind(p.getOrgId(),runId));}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    @GetMapping(path=PREFIX+"/info/status",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request){try{require(request);return ok("{\"ext$\":{}}");}catch(AccessDeniedException e){return forbidden();}catch(SQLException e){return unavailable();}}
    @GetMapping(path=PREFIX+"/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request,@PathVariable("id") String id){try{OwnedPrincipal p=require(request);if(id==null||!id.matches("[0-9]{1,30}"))return badRequest();try(Connection c=dataSource.getConnection();PreparedStatement q=c.prepareStatement(BASE_SQL+" AND A.ID=?")){q.setString(1,p.getOrgId());q.setLong(2,Long.parseLong(id));try(ResultSet r=q.executeQuery()){return ok(r.next()?JSON.toJSONString(row(r,r.getMetaData()),SerializerFeature.WriteMapNullValue):"{}");}}}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    private OwnedPrincipal require(HttpServletRequest r)throws SQLException{boundary.requirePermission(r.getSession(false),OwnedIdentityProvider.RESULT_READ);return boundary.currentPrincipal(r.getSession(false));}
    private String selectPage(String org,Page page)throws SQLException{String count="SELECT COUNT(1) FROM ("+BASE_SQL+") Q";String paged="SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("+BASE_SQL+") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";int total;List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();try(Connection c=dataSource.getConnection()){try(PreparedStatement q=c.prepareStatement(count)){q.setString(1,org);try(ResultSet r=q.executeQuery()){if(!r.next())throw new SQLException("run-remind count returned no row");total=r.getInt(1);}}try(PreparedStatement q=c.prepareStatement(paged)){q.setString(1,org);q.setInt(2,page.start());q.setInt(3,page.end());try(ResultSet r=q.executeQuery()){ResultSetMetaData m=r.getMetaData();while(r.next())rows.add(row(r,m));}}}Map<String,Object> e=new LinkedHashMap<String,Object>();e.put("columnTypeMap",TYPE_PROFILE);e.put("first",page.number==1);e.put("last",total==0||page.number>=pages(total,page.size));e.put("numberOfElements",rows.size());e.put("pageNumber",page.number);e.put("pageSize",page.size);e.put("rows",rows);e.put("total",total);Map<String,Object> t=new LinkedHashMap<String,Object>();t.put("qty_",total);e.put("totalMap",t);e.put("totalPages",pages(total,page.size));return JSON.toJSONString(e,SerializerFeature.WriteMapNullValue);}
    private String selectRunRemind(String org, String runId) throws SQLException {
        Map<String, Object> run = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement q = c.prepareStatement("SELECT * FROM T_EMC_RUN WHERE ID=? AND CREATEDBYORGID=?")) {
                q.setString(1, runId); q.setString(2, org);
                try (ResultSet r = q.executeQuery()) {
                    if (!r.next()) return envelope(0, new Page(1, 1), rows);
                    ResultSetMetaData m = r.getMetaData();
                    for (int i = 1; i <= m.getColumnCount(); i++) {
                        Object current = JdbcValue.normalize(r.getObject(i));
                        if (current != null) run.put(m.getColumnLabel(i).toUpperCase(Locale.ROOT), current);
                    }
                }
            }
            try (PreparedStatement q = c.prepareStatement("SELECT A.* FROM T_EMC_RUN_REMIND A WHERE A.CREATEDBYORGID=? AND LOWER(A.CHECKTYPE)=? ORDER BY A.ID")) {
                q.setString(1, org); q.setString(2, "run");
                try (ResultSet r = q.executeQuery()) {
                    ResultSetMetaData m = r.getMetaData();
                    while (r.next()) {
                        Map<String, Object> value = row(r, m);
                        Object field = value.get("fieldName");
                        Object current = field == null ? null : run.get(String.valueOf(field).toUpperCase(Locale.ROOT));
                        if (current == null || String.valueOf(current).trim().isEmpty()) value.put("checkResult", "异常");
                        else { value.put("value", current); value.put("abnormalExplain", null); }
                        rows.add(value);
                    }
                }
            }
        }
        return envelope(rows.size(), new Page(1, rows.size() == 0 ? 1 : rows.size()), rows);
    }
    private static String envelope(int total,Page page,List<Map<String,Object>> rows){return envelope(total,page,rows,TYPE_PROFILE);}
    private static String envelope(int total,Page page,List<Map<String,Object>> rows,Map<String,String> types){Map<String,Object> e=new LinkedHashMap<String,Object>();e.put("columnTypeMap",types);e.put("first",page.number==1);e.put("last",total==0||page.number>=pages(total,page.size));e.put("numberOfElements",rows.size());e.put("pageNumber",page.number);e.put("pageSize",page.size);e.put("rows",rows);e.put("total",total);Map<String,Object> t=new LinkedHashMap<String,Object>();t.put("qty_",total);e.put("totalMap",t);e.put("totalPages",pages(total,page.size));return JSON.toJSONString(e,SerializerFeature.WriteMapNullValue);}
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object> v=new LinkedHashMap<String,Object>();Map<String,Object> x=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if("RN_".equalsIgnoreCase(n))continue;Object o=JdbcValue.normalize(r.getObject(i));if(o==null)continue;String k=RESOURCE_KEYS.get(n.toUpperCase(Locale.ROOT));if(k==null)x.put(n.toLowerCase(Locale.ROOT),o);else v.put(k,o);}v.put("ext$",x);return v;}
    private static Map<String,String> profile(String e){Map<String,String> r=new LinkedHashMap<String,String>();for(String t:e.split(";")){int i=t.indexOf('=');if(i>0)r.put(t.substring(0,i),t.substring(i+1));}return Collections.unmodifiableMap(r);}
    private static Map<String,String> resourceKeys(){Map<String,String>r=new LinkedHashMap<String,String>();String[] n={"ID=id","REMIND=remind","FIELDNAME=fieldName","VALUE=value","CHECKRESULT=checkResult","ABNORMALEXPLAIN=abnormalExplain","CHECKTYPE=checkType","QCCATEGORYCODE=qcCategoryCode","QCFIELDNAME=qcFieldName","CREATEDBYID=createdById","CREATEDBYNAME=createdByName","CREATEDBYORGID=createdByOrgId","CREATEDBYORGNAME=createdByOrgName"};for(String s:n){int i=s.indexOf('=');r.put(s.substring(0,i),s.substring(i+1));}return Collections.unmodifiableMap(r);}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);}private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");}private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");}private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String b){JSONObject r=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=r==null?null:r.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
