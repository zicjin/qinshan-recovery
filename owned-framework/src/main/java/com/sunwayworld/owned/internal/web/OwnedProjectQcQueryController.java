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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

/** Owned Java read seam for the project quality-control GenericResource family. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedProjectQcQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/project/project-qcs";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedProjectQcQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("project-qc dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); Query query; try { query = Query.parse(body); } catch (IllegalArgumentException invalid) { throw new MalformedProjectQcQueryException(invalid); } return ok(page(principal.getOrgId(), query)); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (MalformedProjectQcQueryException malformed) { return unexpected(response, request.getRequestURI()); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/lines/table-heads/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tableHeads(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(JSON.toJSONString(heads(principal.getOrgId(), Query.parse(body)))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/lines/table-rows/queries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tableRows(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(JSON.toJSONString(rows(principal.getOrgId(), Query.parse(body)), SerializerFeature.WriteMapNullValue)); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String page(String org, Query q) throws SQLException {
        String source = "SELECT Q.* FROM T_EMC_PROJECT_QC Q JOIN T_EMC_PROJECT P ON P.ID=Q.PROJECTID WHERE P.CREATEDBYORGID=?";
        int total; List<Map<String,Object>> values = new ArrayList<Map<String,Object>>(); Map<String,String> types = new LinkedHashMap<String,String>();
        String count = "SELECT COUNT(1) FROM (" + source + ") X";
        String page = "SELECT X.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + source + ") Q) X WHERE X.RN_>? AND X.RN_<=? ORDER BY X.RN_";
        try (Connection c=dataSource.getConnection()) {
            try (PreparedStatement s=c.prepareStatement(count)) { s.setString(1,org); try(ResultSet r=s.executeQuery()){if(!r.next())throw new SQLException("project-qc count unavailable"); total=r.getInt(1);} }
            try (PreparedStatement s=c.prepareStatement(page)) { s.setString(1,org); s.setInt(2,q.start()); s.setInt(3,q.end()); try(ResultSet r=s.executeQuery()){ResultSetMetaData m=r.getMetaData();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))types.put(camel(n),type(m.getColumnType(i)));}while(r.next())values.add(row(r,m));} }
        }
        Map<String,Object> out=new LinkedHashMap<String,Object>();out.put("columnTypeMap",types);out.put("first",q.number==1);out.put("last",total==0||q.number>=pages(total,q.size));out.put("numberOfElements",values.size());out.put("pageNumber",q.number);out.put("pageSize",q.size);out.put("rows",values);out.put("total",total);Map<String,Object>tm=new LinkedHashMap<String,Object>();tm.put("qty_",total);out.put("totalMap",tm);out.put("totalPages",pages(total,q.size));return JSON.toJSONString(out,SerializerFeature.WriteMapNullValue);
    }

    private List<Map<String,Object>> heads(String org, Query q) throws SQLException {
        String sql="SELECT DISTINCT L.QCCATEGORYID,L.QCNAME FROM T_EMC_PROJECT_QC Q JOIN T_EMC_PROJECT P ON P.ID=Q.PROJECTID JOIN T_EMC_PROJECT_QC_LINE L ON L.QCID=Q.ID WHERE P.CREATEDBYORGID=? AND Q.PROJECTID=? AND Q.MONITORTMPLID=? ORDER BY L.QCCATEGORYID";
        List<Map<String,Object>> out=new ArrayList<Map<String,Object>>(); try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)){s.setString(1,org);s.setLong(2,q.projectId);s.setString(3,q.monitorTmplId);try(ResultSet r=s.executeQuery()){while(r.next()){Map<String,Object>h=new LinkedHashMap<String,Object>();h.put("field",String.valueOf(r.getObject(1)));h.put("title",r.getObject(2));out.add(h);}}} return out;
    }

    private List<Map<String,Object>> rows(String org, Query q) throws SQLException {
        String sql="SELECT Q.TESTID,Q.TESTNAME,Q.TOTALQTY,L.ID,L.QCCATEGORYID,L.QCQTY FROM T_EMC_PROJECT_QC Q JOIN T_EMC_PROJECT P ON P.ID=Q.PROJECTID JOIN T_EMC_PROJECT_QC_LINE L ON L.QCID=Q.ID WHERE P.CREATEDBYORGID=? AND Q.PROJECTID=? AND Q.MONITORTMPLID=? ORDER BY Q.TESTID,L.QCCATEGORYID";
        Map<Long,Map<String,Object>> byTest=new LinkedHashMap<Long,Map<String,Object>>(); try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)){s.setString(1,org);s.setLong(2,q.projectId);s.setString(3,q.monitorTmplId);try(ResultSet r=s.executeQuery()){while(r.next()){long testId=r.getLong(1);Map<String,Object> row=byTest.get(testId);if(row==null){row=new LinkedHashMap<String,Object>();row.put("testId",testId);row.put("rowNo",String.valueOf(testId));row.put("testName",r.getObject(2));row.put("totalQty",r.getObject(3));byTest.put(testId,row);}row.put(String.valueOf(r.getObject(5)),r.getObject(6));}}} return new ArrayList<Map<String,Object>>(byTest.values());
    }

    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object>v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(n))v.put(camel(n),JdbcValue.normalize(r.getObject(i)));}return v;}
    private static String camel(String n){StringBuilder b=new StringBuilder();boolean up=false;for(char c:n.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){up=true;continue;}b.append(up?Character.toUpperCase(c):c);up=false;}return b.toString();}
    private static String type(int t){return t==Types.DATE||t==Types.TIMESTAMP||t==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==Types.NUMERIC||t==Types.DECIMAL||t==Types.INTEGER||t==Types.BIGINT?"number":"string");}
    private static int pages(int t,int s){return t==0?0:(t+s-1)/s;} private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().header("Cache-Control","no-store").body(b);} private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");} private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.PROJECT_QC.INVALID_QUERY\"}");} private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.PROJECT_QC.UNAVAILABLE\"}");}
    private static ResponseEntity<String> unexpected(HttpServletResponse response,String path){String body=unexpectedBody(path);if(response!=null){try{response.resetBuffer();response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());response.getWriter().write(body);response.getWriter().flush();return null;}catch(IOException ignored){}}return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);}
    private static String unexpectedBody(String path){String safe=path==null?"":path.replace("\\","\\\\").replace("\"","\\\"");return "{\"error\":\"Internal Server Error\",\"message\":\"GIKAM.EXCEPTION.UNEXPECTED\",\"path\":\""+safe+"\",\"stacktrace\":\"owned-project-qc-malformed-query\",\"status\":500,\"timestamp\":\""+new java.util.Date().toString()+"\"}";}
    private static final class MalformedProjectQcQueryException extends IllegalArgumentException{MalformedProjectQcQueryException(Throwable cause){super("malformed project-qc query",cause);}}
    private static final class Query{final int number,size;final long projectId;final String monitorTmplId;Query(int n,int s,long p,String m){number=n;size=s;projectId=p;monitorTmplId=m;}static Query parse(String b){JSONObject o=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=o==null?null:o.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));Long project=o==null?null:o.getLong("projectId");if(project==null&&o!=null&&o.getJSONObject("f")!=null)project=o.getJSONObject("f").getLong("projectId");String mt=o==null?null:o.getString("monitorTmplId");if(mt==null&&o!=null&&o.getJSONObject("f")!=null)mt=o.getJSONObject("f").getString("monitorTmplId");return new Query(n,s,project==null?0L:project.longValue(),mt==null?"":mt); }int start(){return(number-1)*size;}int end(){return number*size;}}
}
