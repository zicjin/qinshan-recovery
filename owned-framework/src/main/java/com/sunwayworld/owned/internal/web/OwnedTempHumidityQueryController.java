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

/** Owned read seam for the inherited run temperature/humidity resource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedTempHumidityQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/result/temphum";
    private static final String BASE_SQL =
            "SELECT A.*, P.PROJNO, T.TESTNAME, E.EQUIPTNAME, E.EQUIPTID AS TEMPHUMIDITYEQUIPT, O.ORDERNO "
            + "FROM T_EMC_RUN_TEMP_HUMIDITY A LEFT JOIN T_EMC_PROJECT P ON P.ID=A.PROJID "
            + "LEFT JOIN T_EMC_ORDER_TASK T ON T.ID=A.ORDERTASKID LEFT JOIN T_EMC_RUN R ON R.ID=A.RUNID "
            + "LEFT JOIN T_EMC_EQUIPT E ON E.ID=A.EQUIPTID LEFT JOIN T_EMC_ORDER O ON O.ID=A.ORDERID "
            + "WHERE (A.CREATEDBYORGID=? OR A.ORGID=?)";
    private static final Map<String, String> TYPE_PROFILE = profile(
            "begindate=dateTime;begindateone=dateTime;begindatetwo=dateTime;createdbyid=string;createdbyname=string;"
            + "createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;enddate=dateTime;enddateone=dateTime;"
            + "enddatetwo=dateTime;equiptid=number;equiptname=string;humidity=string;id=number;orderid=number;"
            + "orderno=string;ordertaskid=number;orgid=string;projid=number;projno=string;rn_=number;runid=string;"
            + "temperature=string;temphumidityequipt=string;testname=string");
    private static final Map<String, String> RESOURCE_KEYS = resourceKeys();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedTempHumidityQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("temperature/humidity dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); return ok(selectPage(principal.getOrgId(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]{1,30}")) return badRequest();
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(BASE_SQL + " AND A.ID=?")) {
                statement.setString(1, principal.getOrgId()); statement.setString(2, principal.getOrgId()); statement.setLong(3, Long.parseLong(id));
                try (ResultSet rows = statement.executeQuery()) { return ok(rows.next() ? JSON.toJSONString(row(rows, rows.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}"); }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ); return boundary.currentPrincipal(request.getSession(false)); }
    private String selectPage(String organization, Page page) throws SQLException {
        String countSql="SELECT COUNT(1) FROM ("+BASE_SQL+") Q";
        String pageSql="SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("+BASE_SQL+") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();
        try(Connection connection=dataSource.getConnection()){
            try(PreparedStatement statement=connection.prepareStatement(countSql)){statement.setString(1,organization);statement.setString(2,organization);try(ResultSet result=statement.executeQuery()){if(!result.next())throw new SQLException("temperature/humidity count returned no row");total=result.getInt(1);}}
            try(PreparedStatement statement=connection.prepareStatement(pageSql)){statement.setString(1,organization);statement.setString(2,organization);statement.setInt(3,page.start());statement.setInt(4,page.end());try(ResultSet result=statement.executeQuery()){ResultSetMetaData metadata=result.getMetaData();while(result.next())rows.add(row(result,metadata));}}
        }
        Map<String,Object> envelope=new LinkedHashMap<String,Object>();envelope.put("columnTypeMap",TYPE_PROFILE);envelope.put("first",page.number==1);envelope.put("last",total==0||page.number>=pages(total,page.size));envelope.put("numberOfElements",rows.size());envelope.put("pageNumber",page.number);envelope.put("pageSize",page.size);envelope.put("rows",rows);envelope.put("total",total);Map<String,Object> totals=new LinkedHashMap<String,Object>();totals.put("qty_",total);envelope.put("totalMap",totals);envelope.put("totalPages",pages(total,page.size));return JSON.toJSONString(envelope,SerializerFeature.WriteMapNullValue);
    }
    private static Map<String,Object> row(ResultSet result,ResultSetMetaData metadata)throws SQLException{Map<String,Object> value=new LinkedHashMap<String,Object>();Map<String,Object> ext=new LinkedHashMap<String,Object>();for(int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);if("RN_".equalsIgnoreCase(label))continue;Object item=JdbcValue.normalize(result.getObject(i));if(item==null)continue;String key=RESOURCE_KEYS.get(label.toUpperCase(Locale.ROOT));if(key==null)ext.put(label.toLowerCase(Locale.ROOT),item);else value.put(key,item);}value.put("ext$",ext);return value;}
    private static Map<String,String> profile(String encoded){Map<String,String> result=new LinkedHashMap<String,String>();for(String token:encoded.split(";")){int i=token.indexOf('=');if(i>0)result.put(token.substring(0,i),token.substring(i+1));}return Collections.unmodifiableMap(result);}
    private static Map<String,String> resourceKeys(){Map<String,String> result=new LinkedHashMap<String,String>();String[] names={"ID=id","RUNID=runId","PROJID=projId","TEMPERATURE=temperature","HUMIDITY=humidity","ORDERTASKID=orderTaskId","EQUIPTID=equiptId","ORGID=orgid","ORDERID=orderId","BEGINDATE=beginDate","ENDDATE=endDate","BEGINDATEONE=beginDateOne","ENDDATEONE=endDateOne","BEGINDATETWO=beginDateTwo","ENDDATETWO=endDateTwo","CREATEDBYID=createdById","CREATEDBYNAME=createdByName","CREATEDTIME=createdTime","CREATEDBYORGID=createdByOrgId","CREATEDBYORGNAME=createdByOrgName","PROJNO=projNo","TESTNAME=testName","EQUIPTNAME=equiptName","TEMPHUMIDITYEQUIPT=tempHumidityEquipt","ORDERNO=orderNo"};for(String name:names){int i=name.indexOf('=');result.put(name.substring(0,i),name.substring(i+1));}return Collections.unmodifiableMap(result);}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}private static ResponseEntity<String>ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);}private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");}private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");}private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
