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
import java.util.Collections;
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
import org.springframework.web.bind.annotation.RestController;

/** Source-owned detail/status/column reads for the inherited sample resource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedSampleMainReadController {
    private static final String PREFIX = "/secure/emc/module/bp/sample/samples";
    private static final String BASE_SQL = "SELECT S.* FROM T_EMC_SAMPLE S WHERE S.CREATEDBYORGID=? AND S.ID=?";
    private static final Map<String, String> KEYS = keys();
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedSampleMainReadController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("sample read dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }

    @GetMapping(path = {PREFIX + "/{id}", PREFIX + "/{id}/action/print"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            long sampleId = numericId(id);
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(BASE_SQL)) {
                query.setString(1, principal.getOrgId()); query.setLong(2, sampleId);
                try (ResultSet rows = query.executeQuery()) {
                    return ok(rows.next() ? JSON.toJSONString(row(rows, rows.getMetaData()), SerializerFeature.WriteMapNullValue) : "{}");
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request,
            @PathVariable(value = "id", required = false) String id) {
        try { require(request); if (id != null) numericId(id); return ok("{\"auditStatus\":{},\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}/columns/{column}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> column(HttpServletRequest request, @PathVariable("id") String id,
            @PathVariable("column") String column) {
        try {
            OwnedPrincipal principal = require(request); long sampleId = numericId(id);
            String selected = column(column);
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(
                    "SELECT " + selected + " FROM T_EMC_SAMPLE WHERE CREATEDBYORGID=? AND ID=?")) {
                query.setString(1, principal.getOrgId()); query.setLong(2, sampleId);
                try (ResultSet rows = query.executeQuery()) { return ok(JSON.toJSONString(rows.next() ? JdbcValue.normalize(rows.getObject(1)) : null)); }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }
    private static long numericId(String id) { if (id == null || !id.matches("[0-9]{1,30}")) throw new IllegalArgumentException("sample id is invalid"); return Long.parseLong(id); }
    private static String column(String value) { String key = value == null ? "" : value.toUpperCase(Locale.ROOT); if (!KEYS.containsKey(key)) throw new IllegalArgumentException("unsupported sample column"); return key; }
    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String,Object> value=new LinkedHashMap<String,Object>(); Map<String,Object> ext=new LinkedHashMap<String,Object>(); for(int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);Object item=JdbcValue.normalize(result.getObject(i));if(item==null)continue;String key=KEYS.get(label.toUpperCase(Locale.ROOT));if(key==null)ext.put(label.toLowerCase(Locale.ROOT),item);else value.put(key,item);}if(!ext.isEmpty())value.put("ext$",ext);return value; }
    private static Map<String,String> keys(){ Map<String,String> result=new LinkedHashMap<String,String>(); String[] names={"ID=id","SAMPLENO=sampleNo","PROJID=projId","PROJNO=projNo","MONITORTMPLID=monitorTmplId","ORDERCATEGORY=orderCategory","SOURCE=source","STATUS=status","HANDSTATUS=handStatus","REMARK=remark","PROPERTYTMPLID=propertyTmplId","REPORTCONFIGLINEID=reportConfigLineId","SEPSAMPLETIME=sepSampleTime","ACCEPTEDUSERID=acceptedUserId","ACCEPTEUSERNAME=accepteUserName","ACCEPTEDDATE=acceptedDate","ACCEPTEDTIME=acceptedTime","MOBILEID=mobileId","SAMPLECHECK=sampleCheck","USEDCONTAINER=usedContainer","STORAGEVALID=storageValid","EXCHANGEVALID=exchangeValid","SAMPLINGVALID=samplingValid","MARKVALID=markValid","CONTAINERVALID=containerValid","QCUSERID=qcUserId","QCUSERNAME=qcUserName","CALIBRATIONID=calibrationId","CALIREPORTLINEID=caliReportLineId","RECEIVEREPORTLINEID=receiveReportLineId","PRINTFLAG=printFlag","CREATEDBYID=createdById","CREATEDBYNAME=createdByName","CREATEDTIME=createdTime","CREATEDBYORGID=createdByOrgId","CREATEDBYORGNAME=createdByOrgName","ORGID=orgId","ORGNAME=orgName","ACCEPTSTATUS=acceptStatus","ASSIGNSTATUS=assignStatus","UNDOREASON=undoReason","UNDOFLAG=undoFlag","SAMPLESCENEUSERID=sampleSceneUserId","SAMPLESCENEUSERNAME=sampleSceneUserName"}; for(String name:names){int split=name.indexOf('=');result.put(name.substring(0,split),name.substring(split+1));} return Collections.unmodifiableMap(result); }
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");}
}
