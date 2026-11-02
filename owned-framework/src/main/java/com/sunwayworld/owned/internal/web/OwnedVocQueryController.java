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

/** Owned read seam for the inherited VOC weighting GenericResource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedVocQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/voc";
    private static final String BASE_SQL = "SELECT T.* FROM T_EMC_VOC_WEIGHTING T WHERE T.ORGID=?";
    private static final Map<String, String> TYPE_PROFILE = profile("createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;emptybottleweight=string;id=number;orgid=string;projno=string;rn_=number;vocnumber=string;weighingusername=string;weighttime=dateTime");
    private static final Map<String, String> RESOURCE_KEYS = resourceKeys();
    private final OwnedIdentityHttpBoundary boundary; private final DataSource dataSource;
    public OwnedVocQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource){if(boundary==null||dataSource==null)throw new IllegalArgumentException("VOC dependencies are required");this.boundary=boundary;this.dataSource=dataSource;}
    @PostMapping(path={PREFIX+"/queries",PREFIX+"/queries/raw",PREFIX+"/queries/searchable"},consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,@RequestBody(required=false)String body){try{OwnedPrincipal p=require(request);return ok(selectPage(p.getOrgId(),Page.parse(body)));}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    @GetMapping(path={PREFIX+"/info/status",PREFIX+"/{id}/info/status"},produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request){try{require(request);return ok("{\"ext$\":{}}");}catch(AccessDeniedException e){return forbidden();}catch(SQLException e){return unavailable();}}
    @GetMapping(path=PREFIX+"/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request,@PathVariable("id")String id){try{OwnedPrincipal p=require(request);if(id==null||!id.matches("[0-9]{1,30}"))return badRequest();try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(BASE_SQL+" AND T.ID=?")){s.setString(1,p.getOrgId());s.setLong(2,Long.parseLong(id));try(ResultSet r=s.executeQuery()){return ok(r.next()?JSON.toJSONString(row(r,r.getMetaData()),SerializerFeature.WriteMapNullValue):"{}");}}}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    private OwnedPrincipal require(HttpServletRequest r)throws SQLException{boundary.requirePermission(r.getSession(false),OwnedIdentityProvider.RESULT_READ);return boundary.currentPrincipal(r.getSession(false));}
    private String selectPage(String org,Page page)throws SQLException{String countSql="SELECT COUNT(1) FROM ("+BASE_SQL+") Q";String pageSql="SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("+BASE_SQL+") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";int total;List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();try(Connection c=dataSource.getConnection()){try(PreparedStatement s=c.prepareStatement(countSql)){s.setString(1,org);try(ResultSet r=s.executeQuery()){if(!r.next())throw new SQLException("VOC count unavailable");total=r.getInt(1);}}try(PreparedStatement s=c.prepareStatement(pageSql)){s.setString(1,org);s.setInt(2,page.start());s.setInt(3,page.end());try(ResultSet r=s.executeQuery()){while(r.next())rows.add(row(r,r.getMetaData()));}}}Map<String,Object> out=new LinkedHashMap<String,Object>();out.put("columnTypeMap",TYPE_PROFILE);out.put("first",page.number==1);out.put("last",total==0||page.number>=pages(total,page.size));out.put("numberOfElements",rows.size());out.put("pageNumber",page.number);out.put("pageSize",page.size);out.put("rows",rows);out.put("total",total);Map<String,Object>tm=new LinkedHashMap<String,Object>();tm.put("qty_",total);out.put("totalMap",tm);out.put("totalPages",pages(total,page.size));return JSON.toJSONString(out,SerializerFeature.WriteMapNullValue);}
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object>v=new LinkedHashMap<String,Object>();Map<String,Object>ext=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if("RN_".equalsIgnoreCase(n))continue;Object x=JdbcValue.normalize(r.getObject(i));if(x==null)continue;String k=RESOURCE_KEYS.get(n.toUpperCase(Locale.ROOT));if(k==null)ext.put(n.toLowerCase(Locale.ROOT),x);else v.put(k,x);}v.put("ext$",ext);return v;}
    private static Map<String,String> profile(String encoded){Map<String,String>m=new LinkedHashMap<String,String>();for(String t:encoded.split(";")){int i=t.indexOf('=');if(i>0)m.put(t.substring(0,i),t.substring(i+1));}return Collections.unmodifiableMap(m);}
    private static Map<String,String> resourceKeys(){Map<String,String>m=new LinkedHashMap<String,String>();String[]a={"ID=id","VOCNUMBER=vocNumber","WEIGHINGUSERNAME=weighingUserName","EMPTYBOTTLEWEIGHT=emptyBottleWeight","WEIGHTTIME=weightTime","PROJNO=projNo","CREATEDBYID=createdById","CREATEDBYNAME=createdByName","CREATEDTIME=createdTime","CREATEDBYORGID=createdByOrgId","CREATEDBYORGNAME=createdByOrgName","ORGID=orgId"};for(String x:a){int i=x.indexOf('=');m.put(x.substring(0,i),x.substring(i+1));}return Collections.unmodifiableMap(m);}
    private static int pages(int t,int s){return t==0?0:(t+s-1)/s;}private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);}private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");}private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");}private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String b){JSONObject o=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=o==null?null:o.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
