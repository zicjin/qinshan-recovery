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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned read seam for report send-info pagination. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedReportSendInfoController {
    private static final String PATH = "/secure/emc/module/bp/report/reports/report-send-info/queries";
    private final OwnedIdentityHttpBoundary boundary; private final DataSource dataSource;
    public OwnedReportSendInfoController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) { if (boundary == null || dataSource == null) throw new IllegalArgumentException("send-info dependencies are required"); this.boundary = boundary; this.dataSource = dataSource; }

    @PostMapping(path = PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ); String org = boundary.currentPrincipal(request.getSession(false)).getOrgId(); return ok(select(org, Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }

    private String select(String organization, Page page) throws SQLException {
        String source = "SELECT A.ID,A.PROJID,A.NEEDCNREPORT,A.EXPRESSNO,A.EXPRESSTYPE,A.REMARK,A.CREATEDBYID,A.CREATEDBYNAME,A.CREATEDBYORGID,A.CREATEDBYORGNAME,A.CREATEDTIME FROM T_EMC_REPORT_SEND_INFO A JOIN T_EMC_PROJECT B ON B.ID=A.PROJID WHERE B.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + source + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>(); Map<String,String> types = metadataTypes();
        try (Connection c=dataSource.getConnection()) {
            try (PreparedStatement q=c.prepareStatement(countSql)) { q.setString(1,organization); try(ResultSet r=q.executeQuery()){ if(!r.next()) throw new SQLException("send-info count unavailable"); total=r.getInt(1); } }
            try (PreparedStatement q=c.prepareStatement(pageSql)) { q.setString(1,organization); q.setInt(2,page.start()); q.setInt(3,page.end()); try(ResultSet r=q.executeQuery()){ ResultSetMetaData m=r.getMetaData(); while(r.next()) rows.add(row(r,m)); } }
        }
        Map<String,Object> e=new LinkedHashMap<String,Object>(); e.put("columnTypeMap",types); e.put("first",page.number==1); e.put("last",total==0||page.number>=pages(total,page.size)); e.put("numberOfElements",rows.size()); e.put("pageNumber",page.number); e.put("pageSize",page.size); e.put("rows",rows); e.put("total",total); Map<String,Object> tm=new LinkedHashMap<String,Object>(); tm.put("qty_",total); e.put("totalMap",tm); e.put("totalPages",pages(total,page.size)); return JSON.toJSONString(e,SerializerFeature.WriteMapNullValue);
    }
    private static Map<String,String> metadataTypes(){ Map<String,String> m=new LinkedHashMap<String,String>(); String[] s={"address","addressee","backsendno","cnenreportcnt","cnreportcnt","createdbyid","createdbyname","createdbyorgid","createdbyorgname","createdtime","enreportcnt","entrustorgaddress","entrustorgcontact","entrustorgphone","expressno","expresstype","id","importdatetime","isreceived","issurer","issurerdate","needcnenreport","needcnreport","needenreport","orgid","phone","pic","projid","projno","recipientphone","remark","reportpostaddress","rn_","sendemail","sendtoquoter","sendtosaler","username"}; for(String k:s)m.put(k,k.endsWith("cnt")||"projid".equals(k)?"number":("createdtime".equals(k)||"importdatetime".equals(k)||"issurerdate".equals(k)?"dateTime":("id".equals(k)?"number":"string"))); return m; }
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object> v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String l=m.getColumnLabel(i);if("RN_".equalsIgnoreCase(l))continue;Object x=JdbcValue.normalize(r.getObject(i));if(x!=null)v.put(field(l),x);}v.put("ext$",new LinkedHashMap<String,Object>());return v;}
    private static String field(String l){String k=l.toUpperCase(Locale.ROOT);if("NEEDCNREPORT".equals(k))return"needCnReport";if("EXPRESSNO".equals(k))return"expressNo";if("EXPRESSTYPE".equals(k))return"expressType";if("PROJID".equals(k))return"projId";if("CREATEDBYID".equals(k))return"createdById";if("CREATEDBYNAME".equals(k))return"createdByName";if("CREATEDBYORGID".equals(k))return"createdByOrgId";if("CREATEDBYORGNAME".equals(k))return"createdByOrgName";if("CREATEDTIME".equals(k))return"createdTime";return camel(l);}
    private static String camel(String l){StringBuilder b=new StringBuilder();boolean u=false;for(char c:l.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){u=true;continue;}b.append(u?Character.toUpperCase(c):c);u=false;}return b.toString();}
    private static String type(int t){return t==Types.DATE||t==Types.TIMESTAMP||t==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==Types.NUMERIC||t==Types.DECIMAL||t==Types.INTEGER||t==Types.BIGINT?"number":"string");}
    private static int pages(int t,int s){return t==0?0:(t+s-1)/s;}
    private static ResponseEntity<String> ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String b){JSONObject r=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=r==null?null:r.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
