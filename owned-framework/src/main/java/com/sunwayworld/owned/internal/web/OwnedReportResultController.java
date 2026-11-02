package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

/** Owned read seam for the report-result GenericResource query family. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedReportResultController {
    private static final String PATH = "/secure/emc/module/bp/report/reports/results/queries";
    private static final String SOURCE = "SELECT A.*,B.HORIZONTALCODE,B.ORDERDESC,C.INDEXNAME,D.ANALYSISUSERNAME,E.EQUIPTID AS ANALYSISEQUIPTNO,E.EQUIPTNAME AS ANALYSISEQUIPTNAME,H.SAMPLEUSERNAME,H.MONITORPOINTCATEGORYID,H.MONITORPOINTCATEGORY,H.MONITORPOINTNAME AS FOLDERMONITORPOINTNAME,H.PROCESSSTATUS,H.LONGITUDE,H.LATITUDE,G.EQUIPTID AS SAMPLEEQUIPTNO,G.EQUIPTNAME AS SAMPLEEQUIPTNAME,I.REGISTDATE,I.PROJNO,M.TESTMETHODBASIS,A.ITEMALIASNAME AS ITEMGROUP,F.STOCKBATCHNO,N.LIMITSCIENTIFIC FROM T_EMC_REPORT_RESULT A LEFT JOIN T_EMC_ORDER B ON A.ORDERID=B.ID LEFT JOIN T_EMC_ORDER_INDEX C ON A.INDEXID=C.ID LEFT JOIN T_EMC_RUN D ON D.ID=A.RUNID LEFT JOIN T_EMC_EQUIPT E ON E.ID=D.EQUIPTID LEFT JOIN T_EMC_ORDER_TASK F ON F.ID=A.ORDERTASKID LEFT JOIN T_EMC_EQUIPT G ON G.ID=F.EQUIPTID LEFT JOIN T_EMC_FOLDER H ON H.ID=A.FOLDERID LEFT JOIN T_EMC_PROJECT I ON I.ID=A.PROJID LEFT JOIN T_EMC_METHOD M ON M.ID=A.METHODID LEFT JOIN T_EMC_ITEM N ON N.ID=A.ITEMID WHERE EXISTS (SELECT 1 FROM T_EMC_PROJECT P WHERE P.ID=A.PROJID AND P.CREATEDBYORGID=?)";
    private final OwnedIdentityHttpBoundary boundary; private final DataSource dataSource;
    public OwnedReportResultController(OwnedIdentityHttpBoundary boundary, DataSource dataSource){if(boundary==null||dataSource==null)throw new IllegalArgumentException("report-result dependencies are required");this.boundary=boundary;this.dataSource=dataSource;}
    @PostMapping(path=PATH,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,@RequestBody(required=false) String body){try{boundary.requirePermission(request.getSession(false),OwnedIdentityProvider.RESULT_READ);String org=boundary.currentPrincipal(request.getSession(false)).getOrgId();return ok(select(org,Page.parse(body)));}catch(AccessDeniedException e){return forbidden();}catch(IllegalArgumentException e){return badRequest();}catch(SQLException e){return unavailable();}}
    private String select(String org,Page page)throws SQLException{String count="SELECT COUNT(1) FROM ("+SOURCE+") Q",paged="SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("+SOURCE+") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";int total;List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();Map<String,String> types=new LinkedHashMap<String,String>();try(Connection c=dataSource.getConnection()){try(PreparedStatement q=c.prepareStatement(count)){q.setString(1,org);try(ResultSet r=q.executeQuery()){if(!r.next())throw new SQLException("report-result count unavailable");total=r.getInt(1);}}try(PreparedStatement q=c.prepareStatement(paged)){q.setString(1,org);q.setInt(2,page.start());q.setInt(3,page.end());try(ResultSet r=q.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int i=1;i<=m.getColumnCount();i++){String l=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(l))types.put(l.toLowerCase(Locale.ROOT),type(m.getColumnType(i)));}types.put("rn_","number");while(r.next())rows.add(row(r,m));}}}Map<String,Object> e=new LinkedHashMap<String,Object>();e.put("columnTypeMap",types);e.put("first",page.number==1);e.put("last",total==0||page.number>=pages(total,page.size));e.put("numberOfElements",rows.size());e.put("pageNumber",page.number);e.put("pageSize",page.size);e.put("rows",rows);e.put("total",total);Map<String,Object> tm=new LinkedHashMap<String,Object>();tm.put("qty_",total);e.put("totalMap",tm);e.put("totalPages",pages(total,page.size));return JSON.toJSONString(e,SerializerFeature.WriteMapNullValue);}
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object>v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String l=m.getColumnLabel(i);if("RN_".equalsIgnoreCase(l))continue;Object x=JdbcValue.normalize(r.getObject(i));if(x!=null)v.put(camel(l),x);}v.put("ext$",new LinkedHashMap<String,Object>());return v;}
    private static String camel(String l){StringBuilder b=new StringBuilder();boolean u=false;for(char c:l.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){u=true;continue;}b.append(u?Character.toUpperCase(c):c);u=false;}return b.toString();} private static String type(int t){return t==java.sql.Types.DATE||t==java.sql.Types.TIMESTAMP||t==java.sql.Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==java.sql.Types.NUMERIC||t==java.sql.Types.DECIMAL||t==java.sql.Types.INTEGER||t==java.sql.Types.BIGINT?"number":"string");} private static int pages(int t,int s){return t==0?0:(t+s-1)/s;} private static ResponseEntity<String> ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String b){JSONObject r=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=r==null?null:r.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
