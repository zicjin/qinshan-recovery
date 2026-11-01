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

/** Owned Java read-only seam for equipment MDM GenericResource families. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedEquipmentMdmResourceController {
    private static final Map<String, String> TABLES = tables();
    private static final Map<String, String> WIRE = wireNames();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedEquipmentMdmResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("equipment MDM dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/mdm/basemdm/equipt-categories/queries",
            "/secure/emc/module/mdm/basemdm/equipt-requisitions/queries",
            "/secure/emc/module/mdm/basemdm/equipt-temp/queries",
            "/secure/emc/module/mdm/equipt/equip-appearances/queries",
            "/secure/emc/module/mdm/equipt/equip-checks/queries",
            "/secure/emc/module/mdm/equipt/equip-verifications/queries",
            "/secure/emc/module/mdm/equipt/equipt-acq-params/queries",
            "/secure/emc/module/mdm/equipt/equipt-acq-results/queries",
            "/secure/emc/module/mdm/equipt/equipt-acq-scripts/queries",
            "/secure/emc/module/mdm/equipt/equipt-cali-result-norms/queries",
            "/secure/emc/module/mdm/basemdm/equipt-categories/queries/raw",
            "/secure/emc/module/mdm/basemdm/equipt-requisitions/queries/raw",
            "/secure/emc/module/mdm/basemdm/equipt-temp/queries/raw",
            "/secure/emc/module/mdm/equipt/equip-appearances/queries/raw",
            "/secure/emc/module/mdm/equipt/equip-checks/queries/raw",
            "/secure/emc/module/mdm/equipt/equip-verifications/queries/raw",
            "/secure/emc/module/mdm/equipt/equipt-acq-params/queries/raw",
            "/secure/emc/module/mdm/equipt/equipt-acq-results/queries/raw",
            "/secure/emc/module/mdm/equipt/equipt-acq-scripts/queries/raw",
            "/secure/emc/module/mdm/equipt/equipt-cali-result-norms/queries/raw",
            "/secure/emc/module/mdm/basemdm/equipt-categories/queries/searchable",
            "/secure/emc/module/mdm/basemdm/equipt-requisitions/queries/searchable",
            "/secure/emc/module/mdm/basemdm/equipt-temp/queries/searchable",
            "/secure/emc/module/mdm/equipt/equip-appearances/queries/searchable",
            "/secure/emc/module/mdm/equipt/equip-checks/queries/searchable",
            "/secure/emc/module/mdm/equipt/equip-verifications/queries/searchable",
            "/secure/emc/module/mdm/equipt/equip-checks/queries/tasks",
            "/secure/emc/module/mdm/equipt/equip-verifications/queries/tasks",
            "/secure/emc/module/mdm/equipt/equipt-cali-results/queries/tasks",
            "/secure/emc/module/mdm/equipt/equipt-per-checks/queries/tasks",
            "/secure/emc/module/mdm/equipt/equipt-acq-params/queries/searchable",
            "/secure/emc/module/mdm/equipt/equipt-acq-results/queries/searchable",
            "/secure/emc/module/mdm/equipt/equipt-acq-scripts/queries/searchable",
            "/secure/emc/module/mdm/equipt/equipt-cali-result-norms/queries/searchable"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        String resource = resource(request);
        try { requireRead(request); return ok(selectPage(table(resource), Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = "/secure/emc/module/mdm/bizmdm/tests/container-maintenance/queries/raw",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> containerMaintenance(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { requireRead(request); return ok(selectPage("T_EMC_TEST_METHOD_ORDER_CATEGORY", Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = "/secure/emc/module/mdm/equipt/equipt-acq-scripts/eas-results/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> acquisitionResults(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { requireRead(request); return ok(selectPage("T_EMC_EAS_RESULT", Page.parse(body))); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/mdm/basemdm/equipt-categories/info/status",
            "/secure/emc/module/mdm/basemdm/equipt-requisitions/info/status",
            "/secure/emc/module/mdm/basemdm/equipt-temp/info/status",
            "/secure/emc/module/mdm/equipt/equip-appearances/info/status",
            "/secure/emc/module/mdm/equipt/equip-checks/info/status",
            "/secure/emc/module/mdm/equipt/equip-verifications/info/status",
            "/secure/emc/module/mdm/equipt/equipt-cali-results/info/status",
            "/secure/emc/module/mdm/equipt/equipt-per-checks/info/status",
            "/secure/emc/module/mdm/equipt/equipt-acq-params/info/status",
            "/secure/emc/module/mdm/equipt/equipt-acq-results/info/status",
            "/secure/emc/module/mdm/equipt/equipt-acq-scripts/info/status",
            "/secure/emc/module/mdm/equipt/equipt-cali-result-norms/info/status"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        String resource = resource(request);
        try { requireRead(request); table(resource); return ok(("equip-checks".equals(resource) || "equip-verifications".equals(resource)) ? "{\"auditStatus\":{\"processStatus\":\"draft\",\"statusCode\":\"draft\"},\"ext$\":{}}" : "{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/mdm/basemdm/equipt-categories/{id}",
            "/secure/emc/module/mdm/basemdm/equipt-requisitions/{id}",
            "/secure/emc/module/mdm/basemdm/equipt-temp/{id}",
            "/secure/emc/module/mdm/equipt/equip-appearances/{id}",
            "/secure/emc/module/mdm/equipt/equip-checks/{id}",
            "/secure/emc/module/mdm/equipt/equip-verifications/{id}",
            "/secure/emc/module/mdm/equipt/equipt-acq-params/{id}",
            "/secure/emc/module/mdm/equipt/equipt-acq-results/{id}",
            "/secure/emc/module/mdm/equipt/equipt-acq-scripts/{id}",
            "/secure/emc/module/mdm/equipt/equipt-cali-result-norms/{id}"
            }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request,
            @PathVariable("id") String id) {
        String resource = resource(request);
        try {
            requireRead(request); String table = table(resource); long numeric = numericId(id);
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement("SELECT T.* FROM " + table + " T WHERE T.ID=?")) {
                query.setLong(1, numeric);
                try (ResultSet rows = query.executeQuery()) { return ok(JSON.toJSONString(rows.next() ? row(rows, rows.getMetaData()) : Collections.emptyMap())); }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private static String resource(HttpServletRequest request) {
        String path = request.getRequestURI();
        String basemdm = "/secure/emc/module/mdm/basemdm/";
        String equipt = "/secure/emc/module/mdm/equipt/";
        String prefix = path != null && path.startsWith(basemdm) ? basemdm : equipt;
        if (path == null || !path.startsWith(prefix)) throw new IllegalArgumentException("equipment MDM resource path is invalid");
        int start = prefix.length();
        int slash = path.indexOf('/', start);
        if (slash <= start) throw new IllegalArgumentException("equipment MDM resource path is invalid");
        return path.substring(start, slash);
    }

    private void requireRead(HttpServletRequest request) throws SQLException { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.EQUIPMENT_READ); }
    private String table(String resource) { String table = TABLES.get(resource); if (table == null) throw new IllegalArgumentException("unsupported equipment MDM resource"); return table; }
    private String selectPage(String table, Page page) throws SQLException {
        String base = "SELECT T.* FROM " + table + " T";
        String countSql = "SELECT COUNT(1) FROM (" + base + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + base + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        try (Connection connection = dataSource.getConnection()) {
            int total;
            try (PreparedStatement count = connection.prepareStatement(countSql); ResultSet rows = count.executeQuery()) { if (!rows.next()) throw new SQLException("equipment MDM count unavailable"); total = rows.getInt(1); }
            ArrayList<Map<String,Object>> values = new ArrayList<Map<String,Object>>(); Map<String,String> types = new LinkedHashMap<String,String>();
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setInt(1, page.start()); query.setInt(2, page.end());
                try (ResultSet rows = query.executeQuery()) { ResultSetMetaData meta = rows.getMetaData(); for (int i=1;i<=meta.getColumnCount();i++) types.put(meta.getColumnLabel(i).toLowerCase(Locale.ROOT), type(meta.getColumnType(i))); while (rows.next()) values.add(row(rows, meta)); }
            }
            Map<String,Object> envelope = new LinkedHashMap<String,Object>(); envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1); envelope.put("last", total == 0 || page.number >= pages(total, page.size)); envelope.put("numberOfElements", values.size()); envelope.put("pageNumber", page.number); envelope.put("pageSize", page.size); envelope.put("rows", values); envelope.put("total", total); Map<String,Object> totals = new LinkedHashMap<String,Object>(); totals.put("qty_", total); envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size)); return JSON.toJSONString(envelope);
        }
    }
    private static Map<String,Object> row(ResultSet rows, ResultSetMetaData meta) throws SQLException { Map<String,Object> value = new LinkedHashMap<String,Object>(), ext = new LinkedHashMap<String,Object>(); for (int i=1;i<=meta.getColumnCount();i++) { String label=meta.getColumnLabel(i); Object item=JdbcValue.normalize(rows.getObject(i)); if (item == null) continue; if ("RN_".equalsIgnoreCase(label)) ext.put("rn_",String.valueOf(item)); else { Object wire = WIRE.get(label.toUpperCase(Locale.ROOT)); if (wire == null) ext.put(label.toLowerCase(Locale.ROOT), item instanceof Date ? new SimpleDateFormat("yyyy-MM-dd").format((Date)item) : item); else value.put(String.valueOf(wire), item instanceof Date ? new SimpleDateFormat("yyyy-MM-dd").format((Date)item) : item); } } if (!ext.isEmpty()) value.put("ext$", ext); return value; }
    private static int pages(int total,int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static long numericId(String id) { if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("equipment MDM id is invalid"); return Long.parseLong(id); }
    private static String type(int type) { if (type == Types.DATE || type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime"; if (type == Types.NUMERIC || type == Types.DECIMAL || type == Types.INTEGER || type == Types.BIGINT) return "number"; return "string"; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
    private static Map<String,String> tables() { Map<String,String> v=new LinkedHashMap<String,String>(); v.put("equipt-categories","T_EMC_EQUIPT_CATEGORY"); v.put("equipt-requisitions","T_EMC_EQUIPT_REQUISITION"); v.put("equipt-temp","T_EMC_EQUIPT_TEMP"); v.put("equip-appearances","T_EMC_EQUIP_APPEARANCE"); v.put("equip-checks","T_EMC_EQUIP_CHECK"); v.put("equip-verifications","T_EMC_EQUIP_VERIFICATION"); v.put("equipt-cali-results","T_EMC_EQUIPT_CALI_RESULT"); v.put("equipt-per-checks","T_EMC_EQUIPT_PER_CHECK"); v.put("equipt-acq-params","T_EMC_EQUIPT_ACQ_PARAM"); v.put("equipt-acq-results","T_EMC_EQUIPT_ACQ_RESULT"); v.put("equipt-acq-scripts","T_EMC_EQUIPT_ACQ_SCRIPT"); v.put("equipt-cali-result-norms","T_EMC_EQUIPT_CALI_RESULT_NORM"); return Collections.unmodifiableMap(v); }
    private static Map<String,String> wireNames() { Map<String,String> v=new LinkedHashMap<String,String>();
        v.put("ID","id"); v.put("CREATEDBYID","createdById"); v.put("CREATEDBYNAME","createdByName"); v.put("CREATEDBYORGID","createdByOrgId"); v.put("CREATEDBYORGNAME","createdByOrgName"); v.put("CREATEDTIME","createdTime");
        v.put("EQUIPTCATEGORY","equiptCategory"); v.put("EQUIPTCATEGORYDESC","equiptCategoryDesc"); v.put("MAINT","maint"); v.put("EQUIPTID","equiptId"); v.put("EQUIPTNAME","equiptName"); v.put("MODEL","model"); v.put("MANUFACTURER","manufacturer"); v.put("NAME","name"); v.put("REMARK","remark"); v.put("DESCRIPTION","description"); v.put("RESULT","result"); v.put("SAMPLEID","sampleId"); v.put("TEMPERATURE","temperature"); v.put("METHODNAME","methodName"); v.put("TESTNAME","testName"); v.put("TESTMETHODID","testMethodId"); v.put("TESTID","testId"); v.put("SCRIPTID","scriptId"); v.put("PARAMID","paramId"); v.put("RESULTID","resultId"); v.put("PROCESSSTATUS","processStatus"); v.put("STARTDATE","startDate"); v.put("ENDDATE","endDate"); v.put("RECORDDATE","recordDate"); v.put("RECORDUSERID","recordUserId"); v.put("RECORDUSERNAME","recordUserName"); v.put("VALIDITY","validity"); v.put("UNIT","unit"); v.put("ACCURACY","accuracy"); v.put("CERTNO","certNo"); v.put("CERTCATEGORY","certCategory"); v.put("CALIBRATIONID","calibrationId"); v.put("CALIRESULTID","caliResultId"); v.put("EQUIPTMANAGER","equiptManager"); v.put("EQUIPTMANAGERID","equiptManagerId"); v.put("ACCESSORY","accessory"); v.put("APPEARANCEINSPECTION","appearanceInspection"); v.put("ARRIVALDATE","arrivalDate"); v.put("BACKDATE","backDate"); v.put("BACKORG","backOrg"); v.put("BACKSTATUS","backStatus"); v.put("BACKTIME","backTime"); v.put("BACKUSER","backUser"); v.put("BACKUSERID","backUserId"); v.put("BIZNO","bizNo"); v.put("CONTRACTNUMBER","contractNumber"); v.put("CONTAINERCATEGORYID","containerCategoryId"); v.put("CONTAINERGROUP","containerGroup"); v.put("FACTORYNUMBER","factoryNumber"); v.put("FIXATIVE","fixative"); v.put("HUMIDITY","humidity"); v.put("ISBACK","isBack"); v.put("ITEMNAME","itemName"); v.put("MONITORTMPLID","monitorTmplId"); v.put("NUMBER","number"); v.put("ORGID","orgId"); v.put("RENTDATE","rentDate"); v.put("RENTORG","rentOrg"); v.put("RENTSTATUS","rentStatus"); v.put("RENTTIME","rentTime"); v.put("RENTUSER","rentUser"); v.put("RENTUSERID","rentUserId"); v.put("SAMPLEQTY","sampleqty"); v.put("SAMPLEWEIGHT","sampleWeight"); v.put("SCENEEQUIPTNO","sceneEquiptNo"); v.put("SCENEFILENO","sceneFileNo"); v.put("SCRIPTDESC","scriptDesc"); v.put("SECTION","section"); v.put("SERIALACQ","serialAcq"); v.put("SORTNUM","sortNum"); v.put("SOURCE","source"); v.put("STORECONDITION","storeCondition"); v.put("TESTCATEGORYDESC","testCategoryDesc"); v.put("TESTFORMAT","testFormat"); v.put("TESTITEMCOLUMNNO","testItemColumnNo"); v.put("TESTMETHODBASIS","testMethodBasis"); v.put("DATAFORMAT","dataFormat"); v.put("DYNAMICCREATING","dynamicCreating"); v.put("FIELD","field"); v.put("FIELDNAME","fieldName"); v.put("FIELDREPLACESYMBOL","fieldReplaceSymbol"); v.put("FIELDSYMBOL","fieldSymbol"); v.put("FILENAME","fileName"); v.put("ORDERNOFORMAT","orderNoFormat"); v.put("PREANALYCATEGORY","preAnalyCategory"); v.put("RESULTCOLUMNNO","resultColumnNo"); v.put("RESULTFORMAT","resultFormat"); v.put("ROWDATASEPARATOR","rowDataSeparator"); v.put("ROWNO","rowNo"); v.put("RUNNINGCONDITION","runningCondition"); v.put("SHORTNOCOLUMNNO","shortNoColumnNo"); v.put("SORTNO","sortNo"); v.put("BEGINSYMBOL","beginSymbol"); v.put("ENDSYMBOL","endSymbol"); v.put("ANALYSISWAY","analysisWay");
        return Collections.unmodifiableMap(v); }
    private static final class Page { final int number,size; Page(int n,int s){number=n;size=s;} static Page parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body); JSONObject p=root==null?null:root.getJSONObject("p"); int n=p==null?1:Math.max(1,p.getIntValue("n")); int s=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s"))); return new Page(n,s);} int start(){return(number-1)*size;} int end(){return number*size;} }
}
