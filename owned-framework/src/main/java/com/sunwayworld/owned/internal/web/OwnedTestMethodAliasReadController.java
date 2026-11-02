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
import java.sql.Types;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Shared read implementation for test-method and related analysis-item aliases. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedTestMethodAliasReadController {
    private static final Map<String, Resource> RESOURCES = resources();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedTestMethodAliasReadController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("test-method dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/mdm/bizmdm/tests/beforehandles/queries/choosable",
            "/secure/emc/module/mdm/bizmdm/tests/group-items/queries/choosable",
            "/secure/emc/module/mdm/bizmdm/tests/item-groups/queries/choosable",
            "/secure/emc/module/mdm/bizmdm/tests/items/qc-indexes/queries",
            "/secure/emc/module/mdm/bizmdm/tests/items/queries",
            "/secure/emc/module/mdm/bizmdm/tests/items/queries/choosable",
            "/secure/emc/module/mdm/bizmdm/tests/log/queries",
            "/secure/emc/module/mdm/bizmdm/tests/tests/items/queries/selectable",
            "/secure/emc/module/mdm/bizmdm/tests/tests/queries/choosable",
            "/secure/emc/module/mdm/bizmdm/tests/tests/queries/raw",
            "/secure/emc/module/mdm/bizmdm/tests/tests/queries/selectable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { return ok(selectPage(resource(request), require(request).getOrgId(), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/mdm/bizmdm/tests/beforehandles/info/status",
            "/secure/emc/module/mdm/bizmdm/tests/group-items/info/status",
            "/secure/emc/module/mdm/bizmdm/tests/item-groups/info/status",
            "/secure/emc/module/mdm/bizmdm/tests/items/info/status",
            "/secure/emc/module/mdm/bizmdm/tests/tests/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static Resource resource(HttpServletRequest request) {
        String path = request == null ? "" : request.getRequestURI();
        for (Map.Entry<String, Resource> entry : RESOURCES.entrySet()) if (path.equals(entry.getKey())) return entry.getValue();
        throw new IllegalArgumentException("unsupported test-method alias");
    }

    private String selectPage(Resource resource, String organization, Page page) throws SQLException {
        String base = "SELECT T.* FROM " + resource.table + " T WHERE T.CREATEDBYORGID=?";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement count = connection.prepareStatement(countSql)) { count.setString(1, organization); try (ResultSet result = count.executeQuery()) { if (!result.next()) throw new SQLException("test-method count unavailable"); total = result.getInt(1); } }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization); query.setInt(2, page.start()); query.setInt(3, page.end());
                try (ResultSet result = query.executeQuery()) { ResultSetMetaData metadata = result.getMetaData(); for (int i=1;i<=metadata.getColumnCount();i++) { String label=metadata.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(label)) types.put(label.toLowerCase(Locale.ROOT), type(metadata.getColumnType(i))); } while (result.next()) rows.add(row(result, metadata)); }
            }
        }
        Map<String,Object> body = new LinkedHashMap<String,Object>(); body.put("columnTypeMap", types); body.put("first", page.number == 1); body.put("last", total == 0 || page.number >= pages(total,page.size)); body.put("numberOfElements", rows.size()); body.put("pageNumber",page.number); body.put("pageSize",page.size); body.put("rows",rows); body.put("total",total); Map<String,Object> totals=new LinkedHashMap<String,Object>(); totals.put("qty_",total); body.put("totalMap",totals); body.put("totalPages",pages(total,page.size)); return JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String,Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String,Object> value=new LinkedHashMap<String,Object>(); for(int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);if(!"RN_".equalsIgnoreCase(label))value.put(camel(label),JdbcValue.normalize(result.getObject(i)));} return value; }
    private static String camel(String label){String key=label==null?"":label.toUpperCase(Locale.ROOT); if("CREATEDBYID".equals(key))return "createdById"; if("CREATEDBYNAME".equals(key))return "createdByName"; if("CREATEDTIME".equals(key))return "createdTime"; if("CREATEDBYORGID".equals(key))return "createdByOrgId"; if("CREATEDBYORGNAME".equals(key))return "createdByOrgName"; StringBuilder value=new StringBuilder();boolean upper=false;for(char c:label.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){upper=true;continue;}value.append(upper?Character.toUpperCase(c):c);upper=false;}return value.toString();}
    private static String type(int value){return value==Types.DATE||value==Types.TIMESTAMP||value==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(value==Types.NUMERIC||value==Types.DECIMAL||value==Types.INTEGER||value==Types.BIGINT?"number":"string");}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.QUERY.INVALID\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static Map<String,Resource> resources(){Map<String,Resource> v=new LinkedHashMap<String,Resource>();v.put("/secure/emc/module/mdm/bizmdm/tests/beforehandles/queries/choosable",new Resource("T_EMC_TESTMETHOD_BEFOREHANDLE"));v.put("/secure/emc/module/mdm/bizmdm/tests/group-items/queries/choosable",new Resource("T_EMC_GROUP_ITEM"));v.put("/secure/emc/module/mdm/bizmdm/tests/item-groups/queries/choosable",new Resource("T_EMC_ITEM_GROUP"));v.put("/secure/emc/module/mdm/bizmdm/tests/items/qc-indexes/queries",new Resource("T_EMC_ITEM_QCINDEX"));v.put("/secure/emc/module/mdm/bizmdm/tests/items/queries",new Resource("T_EMC_ITEM"));v.put("/secure/emc/module/mdm/bizmdm/tests/items/queries/choosable",new Resource("T_EMC_ITEM"));v.put("/secure/emc/module/mdm/bizmdm/tests/log/queries",new Resource("T_EMC_TEST_METHOD_LOG"));v.put("/secure/emc/module/mdm/bizmdm/tests/tests/items/queries/selectable",new Resource("T_EMC_ITEM"));v.put("/secure/emc/module/mdm/bizmdm/tests/tests/queries/choosable",new Resource("T_EMC_TEST_METHOD"));v.put("/secure/emc/module/mdm/bizmdm/tests/tests/queries/raw",new Resource("T_EMC_TEST_METHOD"));v.put("/secure/emc/module/mdm/bizmdm/tests/tests/queries/selectable",new Resource("T_EMC_TEST_METHOD"));return Collections.unmodifiableMap(v);}
    private static final class Resource{final String table;Resource(String table){this.table=table;}}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String body){com.alibaba.fastjson.JSONObject root=body==null||body.trim().isEmpty()?new com.alibaba.fastjson.JSONObject():JSON.parseObject(body);com.alibaba.fastjson.JSONObject p=root==null?null:root.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
