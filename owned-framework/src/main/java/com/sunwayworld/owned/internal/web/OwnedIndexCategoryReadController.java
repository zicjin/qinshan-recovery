package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned read seam for the two project index-category chooser aliases. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedIndexCategoryReadController {
    private static final String CHOOSABLE = "/secure/emc/module/mdm/bizmdm/index-categories/queries/order-index/choosable";
    private static final String BY_ORDER = "/secure/emc/module/mdm/bizmdm/index-categories/queries/select-by-order";
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedIndexCategoryReadController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("index-category dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {CHOOSABLE, BY_ORDER}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            if (request.getRequestURI().endsWith("/select-by-order")) return ok(selectByOrder(principal.getOrgId(), body));
            return ok(selectChoosable(principal.getOrgId(), Page.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectChoosable(String org, Page page) throws SQLException {
        String source = "SELECT A.*,B.STDNAME,B.MONITORTMPLGROUPID,B.PROPERTYTMPLID AS CATEGORYPROPERTYTMPLID "
                + "FROM T_EMC_ORDER_INDEX A LEFT JOIN T_EMC_INDEX_CATEGORY B ON A.INDEXCATEGORYID=B.ID "
                + "WHERE A.CREATEDBYORGID=? AND A.ACTIVATEDFLAG='1'";
        return paged(source, new Object[]{org}, page);
    }

    private String selectByOrder(String org, String body) throws SQLException {
        List<Long> orderIds = orderIds(body);
        if (orderIds.isEmpty()) return empty(new Page(1, 50));
        StringBuilder marks = new StringBuilder();
        for (int i = 0; i < orderIds.size(); i++) { if (i > 0) marks.append(','); marks.append('?'); }
        String source = "SELECT C.* FROM T_EMC_INDEX_CATEGORY C WHERE C.CREATEDBYORGID=? AND C.ID IN "
                + "(SELECT S.INDEXCATEGORYID FROM T_EMC_INDEX_SET S WHERE S.INDEXITEMID IN "
                + "(SELECT I.INDEXITEMID FROM T_EMC_INDEX_TEST_ITEM I WHERE I.TESTID IN "
                + "(SELECT DISTINCT G.TESTID FROM T_EMC_ORDER_ITEM_GROUP G WHERE G.ORDERID IN (" + marks + ")))) "
                + "ORDER BY C.ID DESC";
        Object[] values = new Object[orderIds.size() + 1]; values[0] = org;
        for (int i = 0; i < orderIds.size(); i++) values[i + 1] = orderIds.get(i);
        return paged(source, values, new Page(1, 500));
    }

    private String paged(String source, Object[] values, Page page) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + source + ") Q";
        String pageSql = "SELECT X.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + source
                + ") Q) X WHERE X.RN_>? AND X.RN_<=? ORDER BY X.RN_";
        int total; List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>(); Map<String,String> types = new LinkedHashMap<String,String>();
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement q = c.prepareStatement(countSql)) { bind(q, values, 1); try (ResultSet r = q.executeQuery()) { if (!r.next()) throw new SQLException("index-category count unavailable"); total = r.getInt(1); } }
            try (PreparedStatement q = c.prepareStatement(pageSql)) {
                int next = bind(q, values, 1); q.setInt(next++, page.start()); q.setInt(next, page.end());
                try (ResultSet r = q.executeQuery()) { ResultSetMetaData m = r.getMetaData(); for (int i=1;i<=m.getColumnCount();i++) { String n=m.getColumnLabel(i); if (!"RN_".equalsIgnoreCase(n)) types.put(camel(n), type(m.getColumnType(i))); } while (r.next()) rows.add(row(r,m)); }
            }
        }
        Map<String,Object> out = new LinkedHashMap<String,Object>(); out.put("columnTypeMap",types); out.put("first",page.number==1); out.put("last",total==0||page.number>=pages(total,page.size)); out.put("numberOfElements",rows.size()); out.put("pageNumber",page.number); out.put("pageSize",page.size); out.put("rows",rows); out.put("total",total); Map<String,Object> tm=new LinkedHashMap<String,Object>(); tm.put("qty_",total); out.put("totalMap",tm); out.put("totalPages",pages(total,page.size)); return JSON.toJSONString(out, SerializerFeature.WriteMapNullValue);
    }

    private static String empty(Page page) { Map<String,Object> out=new LinkedHashMap<String,Object>(); out.put("columnTypeMap",new LinkedHashMap<String,String>()); out.put("first",true); out.put("last",true); out.put("numberOfElements",0); out.put("pageNumber",page.number); out.put("pageSize",page.size); out.put("rows",new ArrayList<Object>()); out.put("total",0); Map<String,Object> tm=new LinkedHashMap<String,Object>(); tm.put("qty_",0); out.put("totalMap",tm); out.put("totalPages",0); return JSON.toJSONString(out); }
    private static int bind(PreparedStatement q,Object[] values,int start)throws SQLException{int i=start;for(Object v:values){if(v instanceof Number)q.setLong(i++,((Number)v).longValue());else q.setString(i++,String.valueOf(v));}return i;}
    private static Map<String,Object> row(ResultSet r,ResultSetMetaData m)throws SQLException{Map<String,Object>v=new LinkedHashMap<String,Object>();for(int i=1;i<=m.getColumnCount();i++){String n=m.getColumnLabel(i);if("RN_".equalsIgnoreCase(n))continue;Object x=JdbcValue.normalize(r.getObject(i));if(x!=null)v.put(camel(n),x);}return v;}
    private static List<Long> orderIds(String body){List<Long> ids=new ArrayList<Long>();if(body==null||body.trim().isEmpty())return ids;Object parsed=JSON.parse(body);JSONArray values=parsed instanceof JSONArray?(JSONArray)parsed:null;if(values==null&&parsed instanceof JSONObject){JSONObject root=(JSONObject)parsed;values=root.getJSONArray("rows");if(values==null)values=root.getJSONArray("data");if(values==null&&root.get("id")!=null){values=new JSONArray();values.add(root);}}if(values==null)return ids;for(Object item:values){if(!(item instanceof JSONObject))continue;Object id=((JSONObject)item).get("id");if(id==null)continue;try{ids.add(Long.valueOf(String.valueOf(id)));}catch(NumberFormatException ignored){}}return ids;}
    private static String camel(String n){StringBuilder b=new StringBuilder();boolean up=false;for(char c:n.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){up=true;continue;}b.append(up?Character.toUpperCase(c):c);up=false;}return b.toString();}
    private static String type(int t){return t==Types.DATE||t==Types.TIMESTAMP||t==Types.TIMESTAMP_WITH_TIMEZONE?"dateTime":(t==Types.NUMERIC||t==Types.DECIMAL||t==Types.INTEGER||t==Types.BIGINT?"number":"string");}
    private static int pages(int t,int s){return t==0?0:(t+s-1)/s;}
    private static ResponseEntity<String>ok(String b){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(b);} private static ResponseEntity<String>forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String>badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");} private static ResponseEntity<String>unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String b){JSONObject r=b==null||b.trim().isEmpty()?new JSONObject():JSON.parseObject(b);JSONObject p=r==null?null:r.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
