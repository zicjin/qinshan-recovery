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

/** Owned read seam for ordinary project scheme GenericResource routes. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedProjectSchemeQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/project/proj-schemes";
    private static final String SOURCE = "SELECT A.*,C.ORGNAME,D.MONITORTMPLNAME,D.MONITORTMPLGROUPID,D.ORDERCATEGORYCODE,D.MONITORPOINTCATEGORYID AS POINTCATEGORY FROM T_EMC_PROJ_SCHEME A LEFT JOIN T_CORE_ORG C ON A.SAMPLEORGID=C.ID LEFT JOIN T_EMC_MONITOR_TMPL D ON A.MONITORTMPLID=D.ID WHERE A.CREATEDBYORGID=?";
    private final OwnedIdentityHttpBoundary boundary; private final DataSource dataSource;
    public OwnedProjectSchemeQueryController(OwnedIdentityHttpBoundary boundary,DataSource dataSource){if(boundary==null||dataSource==null)throw new IllegalArgumentException("project-scheme dependencies are required");this.boundary=boundary;this.dataSource=dataSource;}
    @GetMapping(path=PREFIX+"/info/status",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request){try{require(request);return ok("{\"ext$\":{}}");}catch(AccessDeniedException e){return forbidden();}catch(SQLException e){return unavailable();}}
    @PostMapping(path={PREFIX+"/queries",PREFIX+"/queries/raw",PREFIX+"/queries/searchable"},consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,@RequestBody(required=false)String body){try{OwnedPrincipal p=require(request);return ok(selectPage(p.getOrgId(),Page.parse(body)));}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    @PostMapping(path=PREFIX+"/proj-orders/queries",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> projOrders(HttpServletRequest request,@RequestBody(required=false)String body){try{OwnedPrincipal p=require(request);return ok(selectOrderPage(p.getOrgId(),Page.parse(body)));}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    private OwnedPrincipal require(HttpServletRequest request)throws SQLException{boundary.requirePermission(request.getSession(false),OwnedIdentityProvider.RESULT_READ);return boundary.currentPrincipal(request.getSession(false));}
    private String selectPage(String org,Page page)throws SQLException{String count="SELECT COUNT(1) FROM ("+SOURCE+") Q";String sql="SELECT X.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.FOLDERNO ASC) RN_ FROM ("+SOURCE+") Q) X WHERE X.RN_>? AND X.RN_<=? ORDER BY X.RN_";int total;List<Map<String,Object>>rows=new ArrayList<Map<String,Object>>();Map<String,String>types=new LinkedHashMap<String,String>();try(Connection c=dataSource.getConnection()){try(PreparedStatement s=c.prepareStatement(count)){s.setString(1,org);try(ResultSet r=s.executeQuery()){if(!r.next())throw new SQLException("project-scheme count unavailable");total=r.getInt(1);}}try(PreparedStatement s=c.prepareStatement(sql)){s.setString(1,org);s.setInt(2,page.start());s.setInt(3,page.end());try(ResultSet r=s.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))types.put(camel(n),type(m.getColumnType(i)));}while(r.next())rows.add(row(r,m));}}}Map<String,Object>e=new LinkedHashMap<String,Object>();e.put("columnTypeMap",types);e.put("first",page.number==1);e.put("last",total==0||page.number>=pages(total,page.size));e.put("numberOfElements",rows.size());e.put("pageNumber",page.number);e.put("pageSize",page.size);e.put("rows",rows);e.put("total",total);Map<String,Object>tm=new LinkedHashMap<String,Object>();tm.put("qty_",total);e.put("totalMap",tm);e.put("totalPages",pages(total,page.size));return JSON.toJSONString(e,SerializerFeature.WriteMapNullValue);}
    private String selectOrderPage(String org,Page page)throws SQLException{String source="SELECT A.*,B.METHODNAME,B.TESTMETHODBASIS,B.MAXVERSIONID AS METHODVERSIONID,D.STDNAME,E.INDEXNAME,E.INDEX1,E.INDEX2,E.INDEX3,F.TESTMETHODID,G.TESTNAME,S.FOLDERNO,S.PROJID,M.TESTMETHODTYPE,M.SAMPLEPARAM,M.TESTCLASS FROM T_EMC_PROJ_ORDER A LEFT JOIN T_EMC_METHOD B ON A.METHODID=B.ID LEFT JOIN T_EMC_INDEX_CATEGORY D ON A.INDEXCATEGORYID=D.ID LEFT JOIN T_EMC_ORDER_INDEX E ON A.INDEXID=E.ID LEFT JOIN T_EMC_CAPACITY_ITEM_GROUP F ON A.ITEMGROUPID=F.ID LEFT JOIN T_EMC_PROJ_SCHEME S ON A.PROJSCHEMEID=S.ID LEFT JOIN T_EMC_TEST G ON A.TESTID=G.ID LEFT JOIN T_EMC_TEST_METHOD M ON A.TESTID=M.TESTID AND A.METHODID=M.METHODID AND F.TESTMETHODID=M.ID WHERE S.CREATEDBYORGID=?";String count="SELECT COUNT(1) FROM ("+source+") Q";String sql="SELECT X.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID ASC) RN_ FROM ("+source+") Q) X WHERE X.RN_>? AND X.RN_<=? ORDER BY X.RN_";int total;List<Map<String,Object>>rows=new ArrayList<Map<String,Object>>();Map<String,String>types=new LinkedHashMap<String,String>();try(Connection c=dataSource.getConnection()){try(PreparedStatement s=c.prepareStatement(count)){s.setString(1,org);try(ResultSet r=s.executeQuery()){if(!r.next())throw new SQLException("project-order count unavailable");total=r.getInt(1);}}try(PreparedStatement s=c.prepareStatement(sql)){s.setString(1,org);s.setInt(2,page.start());s.setInt(3,page.end());try(ResultSet r=s.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))types.put(camel(n),type(m.getColumnType(i)));}while(r.next())rows.add(row(r,m));}}}Map<String,Object>e=new LinkedHashMap<String,Object>();e.put("columnTypeMap",types);e.put("first",page.number==1);e.put("last",total==0||page.number>=pages(total,page.size));e.put("numberOfElements",rows.size());e.put("pageNumber",page.number);e.put("pageSize",page.size);e.put("rows",rows);e.put("total",total);Map<String,Object>tm=new LinkedHashMap<String,Object>();tm.put("qty_",total);e.put("totalMap",tm);e.put("totalPages",pages(total,page.size));return JSON.toJSONString(e,SerializerFeature.WriteMapNullValue);}
    private static Map<String,Object>row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object>v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))v.put(camel(n),JdbcValue.normalize(r.getObject(i)));}v.put("ext$",new LinkedHashMap<String,Object>());return v;}
    private static String camel(String n){String s=n.toLowerCase(Locale.ROOT),compact=s.replace("_","");if("folderno".equals(compact))return"folderNo";if("sampleorgid".equals(compact))return"sampleOrgId";if("monitortmplid".equals(compact))return"monitorTmplId";if("monitortmplname".equals(compact))return"monitorTmplName";if("monitortmplgroupid".equals(compact))return"monitorTmplGroupId";if("monitorpointcategoryid".equals(compact))return"monitorPointCategoryId";if("monitorpointcategory".equals(compact))return"monitorPointCategory";if("monitorpointname".equals(compact))return"monitorPointName";if("monitorpointno".equals(compact))return"monitorPointNo";if("ordercategorycode".equals(compact))return"orderCategoryCode";if("createdbyorgid".equals(compact))return"createdByOrgId";return s.indexOf('_')<0?compact:toCamel(s);}
    private static String toCamel(String s){StringBuilder b=new StringBuilder();boolean u=false;for(char c:s.toCharArray()){if(c=='_'){u=true;continue;}b.append(u?Character.toUpperCase(c):c);u=false;}return b.toString();}
    private static String type(int t){return t==Types.DATE||t==Types.TIMESTAMP||t==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==Types.NUMERIC||t==Types.DECIMAL||t==Types.INTEGER||t==Types.BIGINT?"number":"string");}private static int pages(int t,int s){return t==0?0:(t+s-1)/s;}private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);}private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");}private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PROJECT_SCHEME.INVALID_QUERY\"}");}private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PROJECT_SCHEME.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String b){JSONObject o=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=o==null?null:o.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
