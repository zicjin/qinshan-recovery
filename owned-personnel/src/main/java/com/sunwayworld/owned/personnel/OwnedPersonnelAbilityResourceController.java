package com.sunwayworld.owned.personnel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
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

/** Owned Java read-only resource seam for personnel ability tables. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedPersonnelAbilityResourceController {
    private static final String ABILITY = "/secure/emc/module/item/staff-abilitys";
    private static final String CHECK = "/secure/emc/module/item/staff-abality-checks";
    private static final String EVALUATION = "/secure/emc/module/item/staff-ability-evaluations";
    private static final Map<String, Descriptor> RESOURCES = descriptors();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedPersonnelAbilityResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("personnel ability dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {ABILITY + "/queries", ABILITY + "/queries/raw", ABILITY + "/queries/searchable",
            CHECK + "/queries", CHECK + "/queries/raw", CHECK + "/queries/searchable",
            EVALUATION + "/queries", EVALUATION + "/queries/raw", EVALUATION + "/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { requireRead(request); return ok(selectPage(resource(request), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {ABILITY + "/info/status", CHECK + "/info/status", EVALUATION + "/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { requireRead(request); resource(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {ABILITY + "/{id}", CHECK + "/{id}", EVALUATION + "/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @org.springframework.web.bind.annotation.PathVariable("id") String id) {
        try { requireRead(request); Descriptor descriptor = resource(request); long numeric = numericId(id); try (Connection connection=dataSource.getConnection(); PreparedStatement query=connection.prepareStatement("SELECT T.* FROM "+descriptor.table+" T WHERE T.ID=?")) { query.setLong(1,numeric); try(ResultSet rows=query.executeQuery()){return ok(JSON.toJSONString(rows.next()?row(rows,rows.getMetaData(),descriptor):Collections.emptyMap()));} } }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private void requireRead(HttpServletRequest request) throws SQLException { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ); }
    private Descriptor resource(HttpServletRequest request) { String uri=request.getRequestURI(); for (Map.Entry<String,Descriptor> entry:RESOURCES.entrySet()) if(uri.startsWith(entry.getKey()+"/") || uri.equals(entry.getKey())) return entry.getValue(); throw new IllegalArgumentException("unsupported personnel ability resource"); }
    private String selectPage(Descriptor descriptor, Page page) throws SQLException { String base="SELECT T.* FROM "+descriptor.table+" T"; String countSql="SELECT COUNT(1) FROM ("+base+") Q"; String pageSql="SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("+base+") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_"; try(Connection connection=dataSource.getConnection()){int total;try(PreparedStatement count=connection.prepareStatement(countSql);ResultSet rows=count.executeQuery()){if(!rows.next())throw new SQLException("personnel ability count unavailable");total=rows.getInt(1);}ArrayList<Map<String,Object>> values=new ArrayList<Map<String,Object>>();Map<String,String> types=new LinkedHashMap<String,String>();try(PreparedStatement query=connection.prepareStatement(pageSql)){query.setInt(1,page.start());query.setInt(2,page.end());try(ResultSet rows=query.executeQuery()){ResultSetMetaData meta=rows.getMetaData();for(int i=1;i<=meta.getColumnCount();i++)types.put(meta.getColumnLabel(i).toLowerCase(Locale.ROOT),type(meta.getColumnType(i)));while(rows.next())values.add(row(rows,meta,descriptor));}}Map<String,Object> envelope=new LinkedHashMap<String,Object>();envelope.put("columnTypeMap",types);envelope.put("first",page.number==1);envelope.put("last",total==0||page.number>=pages(total,page.size));envelope.put("numberOfElements",values.size());envelope.put("pageNumber",page.number);envelope.put("pageSize",page.size);envelope.put("rows",values);envelope.put("total",total);Map<String,Object> totals=new LinkedHashMap<String,Object>();totals.put("qty_",total);envelope.put("totalMap",totals);envelope.put("totalPages",pages(total,page.size));return JSON.toJSONString(envelope);}}
    private static Map<String,Object> row(ResultSet rows,ResultSetMetaData meta,Descriptor descriptor)throws SQLException{Map<String,Object> value=new LinkedHashMap<String,Object>(),ext=new LinkedHashMap<String,Object>();for(int i=1;i<=meta.getColumnCount();i++){String label=meta.getColumnLabel(i);Object item=JdbcValue.normalize(rows.getObject(i));if(item==null)continue;if("RN_".equalsIgnoreCase(label))ext.put("rn_",String.valueOf(item));else{String wire=descriptor.columns.get(label.toUpperCase(Locale.ROOT));if(wire==null)ext.put(label.toLowerCase(Locale.ROOT),item instanceof Date?new SimpleDateFormat("yyyy-MM-dd").format((Date)item):item);else value.put(wire,item instanceof Date?new SimpleDateFormat("yyyy-MM-dd").format((Date)item):item);}}if(!ext.isEmpty())value.put("ext$",ext);return value;}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}private static long numericId(String id){if(id==null||!id.matches("[0-9]{1,30}"))throw new IllegalArgumentException("personnel ability id invalid");return Long.parseLong(id);}private static String type(int type){if(type==Types.DATE||type==Types.TIMESTAMP||type==Types.TIMESTAMP_WITH_TIMEZONE)return "dateTime";if(type==Types.NUMERIC||type==Types.DECIMAL||type==Types.INTEGER||type==Types.BIGINT)return "number";return "string";}private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);}private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");}private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");}private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static Map<String,Descriptor> descriptors(){Map<String,Descriptor> v=new LinkedHashMap<String,Descriptor>();Map<String,String> common=new LinkedHashMap<String,String>();common.put("ID","id");common.put("NAME","name");common.put("DEPT","dept");common.put("JOB","job");common.put("TECHNICALTITLE","technicalTitle");common.put("PLANCATEGORY","plancategory");common.put("CREATEDBYID","createdById");common.put("CREATEDBYNAME","createdByName");common.put("CREATEDTIME","createdTime");common.put("CREATEDBYORGID","createdByOrgId");common.put("CREATEDBYORGNAME","createdByOrgName");v.put(ABILITY,new Descriptor("T_EMC_STAFF_ABILITY",common));v.put(CHECK,new Descriptor("T_EMC_STAFF_ABALITY_CHECK",common));v.put(EVALUATION,new Descriptor("T_EMC_STAFF_ABILITY_EVALUATION",common));return Collections.unmodifiableMap(v);}
    private static final class Descriptor{final String table;final Map<String,String> columns;Descriptor(String table,Map<String,String> columns){this.table=table;this.columns=columns;}}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
