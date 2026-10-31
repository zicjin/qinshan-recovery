package com.sunwayworld.owned.internal.web;

import com.sunwayworld.owned.internal.data.JdbcValue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Bounded Oracle read seam for order-retain records and organization scope. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderRetainController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrderRetainController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("order retain dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {"/secure/emc/module/bp/order/order-retains/queries",
            "/secure/emc/module/bp/order/order-retains/queries/raw",
            "/secure/emc/module/bp/order/order-retains/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            JSONObject filter = object(body);
            return ok(page(read(principal.getOrgId(), text(filter.get("orderNo")))));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = "/secure/emc/module/bp/order/order-retains/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            if (id == null || !id.matches("[0-9]+")) throw new IllegalArgumentException("order retain id is invalid");
            List<Map<String, Object>> rows = read(principal.getOrgId(), id);
            return ok(JSON.toJSONString(rows.isEmpty() ? new LinkedHashMap<String, Object>() : rows.get(0), SerializerFeature.WriteMapNullValue));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** GenericResource status is an empty DTO for non-activatable retain records. */
    @GetMapping(path = {"/secure/emc/module/bp/order/order-retains/info/status",
            "/secure/emc/module/bp/order/order-retains/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try {
            boundary.requireOrderRetainRead(request.getSession(false));
            return ok("{\"ext$\":{}}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** The recovered print endpoint returns the same safe detail projection. */
    @GetMapping(path = "/secure/emc/module/bp/order/order-retains/{id}/action/print",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> print(HttpServletRequest request, @PathVariable("id") String id) {
        return detail(request, id);
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requireOrderRetainRead(request.getSession(false));
        return boundary.currentPrincipal(request.getSession(false));
    }

    private List<Map<String, Object>> read(String organization, String value) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        StringBuilder sql = new StringBuilder("SELECT E.*, O.CREATEDBYORGID AS ORDERORGID, O.ORDERNO, O.SAMPLECOMPLETEDATE FROM T_EMC_ORDER_RETAIN E LEFT JOIN T_EMC_ORDER O ON O.ID=E.ORDERID WHERE O.CREATEDBYORGID=?");
        List<String> params = new ArrayList<String>(); params.add(organization);
        if (value != null && value.trim().length() != 0) { sql.append(" AND (TO_CHAR(E.ID)=? OR O.ORDERNO=?)"); params.add(value.trim()); params.add(value.trim()); }
        sql.append(" ORDER BY E.ID DESC");
        try (Connection connection=dataSource.getConnection(); PreparedStatement query=connection.prepareStatement(sql.toString())) {
            for (int i=0;i<params.size();i++) query.setString(i+1,params.get(i));
            try(ResultSet result=query.executeQuery()) { ResultSetMetaData metadata=result.getMetaData(); while(result.next()){Map<String,Object> row=new LinkedHashMap<String,Object>(); for(int i=1;i<=metadata.getColumnCount();i++) row.put(friendly(metadata.getColumnLabel(i)),JdbcValue.normalize(result.getObject(i))); rows.add(row);} }
        }
        return rows;
    }
    private static JSONObject object(String body){if(body==null||body.trim().length()==0)return new JSONObject();Object value=JSON.parse(body);if(!(value instanceof JSONObject))throw new IllegalArgumentException("request must be an object");JSONObject object=(JSONObject)value;return object.containsKey("p")?object.getJSONObject("p"):object;}
    private static String text(Object value){return value==null?"":String.valueOf(value).trim();}
    private static String page(List<Map<String,Object>> rows){Map<String,Object> body=new LinkedHashMap<String,Object>();body.put("total",rows.size());body.put("pageNumber",1);body.put("pageSize",rows.size());body.put("rows",rows);return JSON.toJSONString(body,SerializerFeature.WriteMapNullValue);}
    private static String friendly(String name){String upper=name.toUpperCase();if("ID".equals(upper))return "id";if("ORDERID".equals(upper))return "orderId";if("ORDERNO".equals(upper))return "orderNo";if("ORDERORGID".equals(upper))return "orgId";if("RETAINQTY".equals(upper))return "retainQty";if("RETAINLOC".equals(upper))return "retainLoc";if("RETAINTIMELIMIT".equals(upper))return "retainTimeLimit";StringBuilder result=new StringBuilder();boolean next=false;for(char c:name.toLowerCase().toCharArray()){if(c=='_'){next=true;continue;}result.append(next?Character.toUpperCase(c):c);next=false;}return result.toString();}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().header("Cache-Control","no-store").body(body);}
    private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");}
    private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.ORDER_RETAIN.INVALID_REQUEST\"}");}
    private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.ORDER_RETAIN.UNAVAILABLE\"}");}
}
