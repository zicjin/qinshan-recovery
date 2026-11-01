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

/** Additional read-only order-retain aliases backed by the real Oracle tables. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderRetainQueryExtensionController {
    private static final String PREFIX = "/secure/emc/module/bp/order/order-retains";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrderRetainQueryExtensionController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("order retain query dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = PREFIX + "/queries/tasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tasks(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { return ok(page(rows(request, TASK_SQL, false), TASK_PROFILE, pageSize(body), true)); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/queries-overdue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> overdue(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { List<Map<String,Object>> values=rows(request, OVERDUE_SQL, false); return ok(page(values, new LinkedHashMap<String,String>(), values.size(), false)); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (SQLException failure) { return unavailable(); }
    }

    private static final String TASK_SQL = "SELECT E.*,O.ORDERNO,O.SAMPLECOMPLETEDATE,P.PROJNO,P.PROJNAME,F.FOLDERNO FROM T_EMC_ORDER_RETAIN E LEFT JOIN T_EMC_ORDER O ON O.ID=E.ORDERID LEFT JOIN T_EMC_PROJECT P ON P.ID=E.PROJID LEFT JOIN T_EMC_FOLDER F ON F.ID=E.FOLDERID WHERE O.CREATEDBYORGID=? ORDER BY E.ID DESC";
    private static final String OVERDUE_SQL = "SELECT E.*,O.CREATEDBYORGID AS ORDERORGID,O.ORDERNO,O.SAMPLECOMPLETEDATE FROM T_EMC_ORDER_RETAIN E LEFT JOIN T_EMC_ORDER O ON O.ID=E.ORDERID WHERE O.CREATEDBYORGID=? AND E.RETAINTIMELIMIT < SYSDATE ORDER BY E.ID DESC";

    private List<Map<String,Object>> rows(HttpServletRequest request, String sql, boolean ignored) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.ORDER_RETAIN_READ);
        OwnedPrincipal principal=boundary.currentPrincipal(request.getSession(false));
        List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();
        try(Connection connection=dataSource.getConnection();PreparedStatement query=connection.prepareStatement(sql)){
            query.setString(1,principal.getOrgId());
            try(ResultSet result=query.executeQuery()){ResultSetMetaData metadata=result.getMetaData();while(result.next()){Map<String,Object> row=new LinkedHashMap<String,Object>();for(int i=1;i<=metadata.getColumnCount();i++)row.put(camel(metadata.getColumnLabel(i)),JdbcValue.normalize(result.getObject(i)));rows.add(row);}}
        }
        return rows;
    }
    private static int pageSize(String body){if(body==null||body.trim().isEmpty())return 50;try{com.alibaba.fastjson.JSONObject root=JSON.parseObject(body);com.alibaba.fastjson.JSONObject p=root==null?null:root.getJSONObject("p");int size=p==null?50:p.getIntValue("s");return size<=0?50:Math.min(size,500);}catch(RuntimeException ignored){return 50;}}
    private static String page(List<Map<String,Object>> rows, Map<String,String> profile,int size,boolean quantity){Map<String,Object> body=new LinkedHashMap<String,Object>();body.put("columnTypeMap",profile);body.put("first",true);body.put("last",true);body.put("numberOfElements",rows.size());body.put("pageNumber",1);body.put("pageSize",size);body.put("rows",rows);body.put("total",rows.size());Map<String,Object> total=new LinkedHashMap<String,Object>();if(quantity)total.put("qty_",rows.size());body.put("totalMap",total);body.put("totalPages",rows.isEmpty()?0:1);return JSON.toJSONString(body,SerializerFeature.WriteMapNullValue);}
    private static String camel(String name){StringBuilder b=new StringBuilder();boolean up=false;for(char c:name.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){up=true;continue;}b.append(up?Character.toUpperCase(c):c);up=false;}return b.toString();}
    private static final Map<String,String> TASK_PROFILE=profile();
    private static Map<String,String> profile(){String e="processstatus=string;orderno=string;projno=string;samplecompletedate=dateTime;createdbyid=string;retaintimelimit=dateTime;handlerecord=string;folderid=number;retaindate=dateTime;handleuserid=string;retainuserid=string;projname=string;retainusername=string;createdbyorgname=string;id=number;projid=number;rn_=number;orderid=number;createdbyorgid=string;handledate=dateTime;folderno=string;retainindex=string;createdtime=dateTime;handleusernames=string;handleusername=string;retainqty=number;isretain=string;retaindays=number;createdbyname=string;retainloc=string";Map<String,String> r=new LinkedHashMap<String,String>();for(String t:e.split(";")){int i=t.indexOf('=');if(i>0)r.put(t.substring(0,i),t.substring(i+1));}return r;}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().header("Cache-Control","no-store").body(body);}
    private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");}
    private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ORDER_RETAIN.UNAVAILABLE\"}");}
}
