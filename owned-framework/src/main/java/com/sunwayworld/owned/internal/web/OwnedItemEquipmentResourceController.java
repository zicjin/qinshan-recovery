package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
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

/** Owned Java read-only resource seam for the remaining equipment item tables. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedItemEquipmentResourceController {
    private static final Map<String, Descriptor> RESOURCES = descriptors();
    private final OwnedIdentityHttpBoundary boundary; private final DataSource dataSource;
    public OwnedItemEquipmentResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("item equipment dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }
    @PostMapping(path = {
            "/secure/emc/module/item/emc-patrols/queries",
            "/secure/emc/module/item/equipt-assay-plans/queries",
            "/secure/emc/module/item/scm/equipt-purchs/queries",
            "/secure/emc/module/item/emc-patrols/queries/raw",
            "/secure/emc/module/item/equipt-assay-plans/queries/raw",
            "/secure/emc/module/item/scm/equipt-purchs/queries/raw",
            "/secure/emc/module/item/emc-patrols/queries/searchable",
            "/secure/emc/module/item/equipt-assay-plans/queries/searchable",
            "/secure/emc/module/item/scm/equipt-purchs/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        String resource = resource(request);
        try { Descriptor descriptor = descriptor(resource); requireRead(request); return ok(selectPage(descriptor, Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }
    /**
     * The patrol resource has three month views outside GenericResource. Keep
     * them on the same owned Java controller and preserve the supplier's
     * current-month and current-user filters without introducing another
     * process or mapper runtime.
     */
    @PostMapping(path = {
            "/secure/emc/module/item/emc-patrols/queries/raw/month",
            "/secure/emc/module/item/emc-patrols/queries/raw/month/patrolUser",
            "/secure/emc/module/item/emc-patrols/queries/raw/month/handleUser"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> patrolMonth(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            requireRead(request);
            com.sunwayworld.owned.internal.security.OwnedPrincipal principal =
                    boundary.currentPrincipal(request.getSession(false));
            String path = request.getRequestURI();
            String month = new SimpleDateFormat("yyyy-MM", Locale.ROOT).format(new Date());
            List<Object> parameters = new ArrayList<Object>();
            StringBuilder where = new StringBuilder("T.PATROLDATE LIKE ?");
            parameters.add(month + "%");
            if (path.endsWith("/patrolUser")) {
                return ok(selectPatrolFilteredPage(Page.parse(body), month, principal.getId(), "patrolUserId"));
            } else if (path.endsWith("/handleUser")) {
                // HANDLEUSERIDS is a CLOB in the clone. Read the current
                // month's rows and apply the supplier's exact comma-separated
                // ID filter in Java; this avoids an Oracle CLOB bind cast and
                // keeps the response envelope/pagination deterministic.
                return ok(selectPatrolFilteredPage(Page.parse(body), month, principal.getId(), "handleUserIds"));
            }
            return ok(selectPage(descriptor("emc-patrols"), Page.parse(body), where.toString(), parameters));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }
    @GetMapping(path = {
            "/secure/emc/module/item/emc-patrols/info/status",
            "/secure/emc/module/item/equipt-assay-plans/info/status",
            "/secure/emc/module/item/scm/equipt-purchs/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        String resource = resource(request);
        try { descriptor(resource); requireRead(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }
    @GetMapping(path = {
            "/secure/emc/module/item/emc-patrols/{id}",
            "/secure/emc/module/item/equipt-assay-plans/{id}",
            "/secure/emc/module/item/scm/equipt-purchs/{id}"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        String resource = resource(request);
        try { Descriptor descriptor = descriptor(resource); requireRead(request); return ok(JSON.toJSONString(selectDetail(descriptor, numericId(id)))); }
        catch (AccessDeniedException denied) { return forbidden(); } catch (IllegalArgumentException invalid) { return badRequest(); } catch (SQLException failure) { return unavailable(); }
    }
    private static String resource(HttpServletRequest request) {
        String path = request.getRequestURI();
        String scm = "/secure/emc/module/item/scm/";
        String item = "/secure/emc/module/item/";
        String prefix = path != null && path.startsWith(scm) ? scm : item;
        if (path == null || !path.startsWith(prefix)) throw new IllegalArgumentException("item equipment resource path is invalid");
        int start = prefix.length();
        int slash = path.indexOf('/', start);
        if (slash <= start) throw new IllegalArgumentException("item equipment resource path is invalid");
        return path.substring(start, slash);
    }

    private void requireRead(HttpServletRequest request) throws SQLException { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.EQUIPMENT_READ); }
    private String selectPage(Descriptor descriptor, Page page) throws SQLException {
        return selectPage(descriptor, page, "", Collections.<Object>emptyList());
    }
    private String selectPage(Descriptor descriptor, Page page, String where, List<Object> parameters) throws SQLException {
        String base = "SELECT T.* FROM " + descriptor.table + " T" + (where == null || where.trim().isEmpty() ? "" : " WHERE " + where);
        String order = descriptor.ascending ? "Q.ID ASC" : "Q.ID DESC";
        String count = "SELECT COUNT(1) FROM (" + base + ") Q"; String paged = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY " + order + ") RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection connection = dataSource.getConnection()) {
            int total; try (PreparedStatement query = connection.prepareStatement(count)) {
                bind(query, parameters, 1);
                try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new SQLException("item equipment count unavailable"); total = rows.getInt(1); }
            }
            ArrayList<Map<String,Object>> values = new ArrayList<Map<String,Object>>(); Map<String,String> types = new LinkedHashMap<String,String>();
            try (PreparedStatement query = connection.prepareStatement(paged)) {
                int index = bind(query, parameters, 1); query.setInt(index, page.start()); query.setInt(index + 1, page.end());
                try (ResultSet rows=query.executeQuery()) { ResultSetMetaData meta=rows.getMetaData(); for(int i=1;i<=meta.getColumnCount();i++) types.put(meta.getColumnLabel(i).toLowerCase(Locale.ROOT), type(meta.getColumnType(i))); while(rows.next()) values.add(row(rows,meta,descriptor)); }
            }
            Map<String,Object> envelope=new LinkedHashMap<String,Object>(); envelope.put("columnTypeMap",types); envelope.put("first",page.number==1); envelope.put("last",total==0||page.number>=pages(total,page.size)); envelope.put("numberOfElements",values.size()); envelope.put("pageNumber",page.number); envelope.put("pageSize",page.size); envelope.put("rows",values); envelope.put("total",total); Map<String,Object> tm=new LinkedHashMap<String,Object>(); tm.put("qty_",total); envelope.put("totalMap",tm); envelope.put("totalPages",pages(total,page.size)); return JSON.toJSONString(envelope);
        }
    }
    private static int bind(PreparedStatement query, List<Object> parameters, int index) throws SQLException {
        for (Object parameter : parameters) query.setObject(index++, parameter);
        return index;
    }
    private String selectPatrolFilteredPage(Page page, String month, String userId, String userField) throws SQLException {
        String sql = "SELECT T.* FROM T_EMC_PATROL T WHERE T.PATROLDATE LIKE ? ORDER BY T.ID ASC";
        ArrayList<Map<String,Object>> filtered = new ArrayList<Map<String,Object>>();
        Map<String,String> types = new LinkedHashMap<String,String>();
        Descriptor descriptor = descriptor("emc-patrols");
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, month + "%");
            try (ResultSet rows = query.executeQuery()) {
                ResultSetMetaData meta = rows.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) types.put(meta.getColumnLabel(i).toLowerCase(Locale.ROOT), type(meta.getColumnType(i)));
                types.put("rn_", "number");
                while (rows.next()) {
                    Map<String,Object> value = row(rows, meta, descriptor);
                    Object selected = value.get(userField);
                    if ("handleUserIds".equals(userField)) {
                        if (selected != null) for (String item : String.valueOf(selected).split(",")) if (item.trim().equals(userId)) { filtered.add(value); break; }
                    } else if (selected != null && String.valueOf(selected).equals(userId)) filtered.add(value);
                }
            }
        }
        int total = filtered.size();
        int from = Math.min(page.start(), total), to = Math.min(page.end(), total);
        Map<String,Object> envelope = new LinkedHashMap<String,Object>();
        envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", to - from); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size);
        envelope.put("rows", filtered.subList(from, to)); envelope.put("total", total);
        Map<String,Object> totalMap = new LinkedHashMap<String,Object>(); totalMap.put("qty_", total); envelope.put("totalMap", totalMap);
        envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope);
    }
    private Map<String,Object> selectDetail(Descriptor descriptor,long id) throws SQLException { try(Connection connection=dataSource.getConnection(); PreparedStatement query=connection.prepareStatement("SELECT T.* FROM "+descriptor.table+" T WHERE T.ID=?")){query.setLong(1,id);try(ResultSet rows=query.executeQuery()){return rows.next()?row(rows,rows.getMetaData(),descriptor):Collections.emptyMap();}} }
    private static Map<String,Object> row(ResultSet rows,ResultSetMetaData meta,Descriptor descriptor)throws SQLException{Map<String,Object> value=new LinkedHashMap<String,Object>(),ext=new LinkedHashMap<String,Object>();for(int i=1;i<=meta.getColumnCount();i++){String label=meta.getColumnLabel(i);Object item=normalize(JdbcValue.normalize(rows.getObject(i)));if(item==null)continue;if("RN_".equalsIgnoreCase(label))ext.put("rn_",String.valueOf(item));else{String name=descriptor.columns.get(label.toUpperCase(Locale.ROOT));if(name==null)ext.put(label.toLowerCase(Locale.ROOT),item);else value.put(name,item);}}if(!ext.isEmpty())value.put("ext$",ext);return value;}
    private static Object normalize(Object value){return value instanceof Date?new SimpleDateFormat("yyyy-MM-dd").format((Date)value):value;}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;} private static long numericId(String id){if(id==null||!id.matches("[0-9]{1,30}"))throw new IllegalArgumentException("item equipment id invalid");return Long.parseLong(id);} private static String type(int type){if(type==Types.DATE||type==Types.TIMESTAMP||type==Types.TIMESTAMP_WITH_TIMEZONE)return "dateTime";if(type==Types.NUMERIC||type==Types.DECIMAL||type==Types.INTEGER||type==Types.BIGINT)return "number";return "string";}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
    private static Descriptor descriptor(String resource){Descriptor value=RESOURCES.get(resource);if(value==null)throw new IllegalArgumentException("unsupported item equipment resource");return value;}
    private static Map<String,Descriptor> descriptors(){Map<String,Descriptor> v=new LinkedHashMap<String,Descriptor>();v.put("emc-patrols",new Descriptor("T_EMC_PATROL",true,map("ID","id","PATROLDATE","patrolDate","PATROLWEEKDAY","patrolWeekday","PATROLTIME","patrolTime","PATROLUSERNAME","patrolUserName","PATROLUSERID","patrolUserId","PROBLEM","problem","HANDLEUSERIDS","handleUserIds","HANDLEUSERNAMES","handleUserNames","HANDLEREBACK","handleReback","HANDLERESULT","handleResult","CREATEDBYID","createdById","CREATEDBYNAME","createdByName","CREATEDTIME","createdTime","CREATEDBYORGID","createdByOrgId","CREATEDBYORGNAME","createdByOrgName")));v.put("equipt-assay-plans",new Descriptor("T_EMC_EQUIPT_ASSAY_PLAN",false,map("ID","id","CODENAME","codename","VERIFYORG","verifyOrg","CYCLE","cycle","MAINTDATE","maintDate","MAINTENDDATE","maintEndDate","WARNINGDAYS","warningDays","GIST","gist","CERTCATEGORY","certCategory","REMARK","remark","PLANCATEGORY","plancategory","CREATEDBYID","createdById","CREATEDBYNAME","createdByName","CREATEDTIME","createdTime","CREATEDBYORGID","createdByOrgId","CREATEDBYORGNAME","createdByOrgName")));v.put("equipt-purchs",new Descriptor("T_EMC_EQUIPT_PURCH",false,map("ID","id","EQUIPTNAME","equiptName","MODEL","model","MANUFACTURER","manufacturer","PURCHLOC","purchLoc","PURCHQTY","purchQty","PRICE","price","PURCHATTACHNAME","purchAttachName","FEE","fee","PAYWAY","payWay","TOTALAMOUNT","totalAmount","USEDESC","useDesc","FACTORYNO","factoryNo","FACTORYDATE","factoryDate","PURCHDATE","purchDate","CONTRACTNO","contractNo","AMOUNT","amount","CHECKRECORD","checkRecord","CHECKRESULT","checkResult","CHECKGROUP","checkGroup","DEBUGUSER","debugUser","DEBUGDATE","debugDate","CHECKRESPUSER","checkRespUser","CHECKRESPDATE","checkRespDate","RECEIVEUSER","receiveUser","RECEIVEDATE","receiveDate","CREATEDBYID","createdById","CREATEDBYNAME","createdByName","CREATEDTIME","createdTime","CREATEDBYORGID","createdByOrgId","CREATEDBYORGNAME","createdByOrgName")));return Collections.unmodifiableMap(v);}
    private static Map<String,String> map(String... values){Map<String,String> r=new LinkedHashMap<String,String>();for(int i=0;i<values.length;i+=2)r.put(values[i],values[i+1]);return r;} private static final class Descriptor{final String table;final boolean ascending;final Map<String,String> columns;Descriptor(String table,boolean ascending,Map<String,String> columns){this.table=table;this.ascending=ascending;this.columns=columns;}}
    private static final class Page{final int number,size;Page(int n,int s){number=n;size=s;}static Page parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int n=p==null?1:Math.max(1,p.getIntValue("n"));int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Page(n,s);}int start(){return(number-1)*size;}int end(){return number*size;}}
}
