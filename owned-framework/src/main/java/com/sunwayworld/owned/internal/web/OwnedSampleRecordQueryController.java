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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned implementation of the sample-record lookup used by the
 * original workspace report actions.  The lookup stays in the same Spring
 * application and follows the order-to-sample relation from the project
 * service; it is not a compatibility process or an HTTP bridge.
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedSampleRecordQueryController {
    private static final String PATH = "/secure/emc/module/bp/sample/samples/queries/sample-record";
    private static final String SQL = "SELECT S.* FROM T_EMC_SAMPLE S "
            + "JOIN T_EMC_ORDER O ON O.SAMPLEID=S.ID "
            + "WHERE O.ID=? AND O.CREATEDBYORGID=? AND S.CREATEDBYORGID=?";
    private static final Map<String, String> KEYS = keys();

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedSampleRecordQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("sample-record dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            String orderId = orderId(body);
            if (orderId == null) return ok("null");
            if (!orderId.matches("[0-9]{1,30}")) throw new IllegalArgumentException("orderId is invalid");
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(SQL)) {
                statement.setLong(1, Long.parseLong(orderId));
                statement.setString(2, principal.getOrgId());
                statement.setString(3, principal.getOrgId());
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) return ok("null");
                    return ok(JSON.toJSONString(row(result, result.getMetaData()), SerializerFeature.WriteMapNullValue));
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static String orderId(String body) {
        if (body == null || body.trim().length() == 0) return null;
        JSONObject root;
        try { root = JSON.parseObject(body); }
        catch (RuntimeException invalid) { throw new IllegalArgumentException("invalid sample-record request", invalid); }
        String value = text(root, "orderId");
        if (value == null) value = text(root == null ? null : root.getJSONObject("f"), "orderId");
        if (value == null) value = text(root == null ? null : root.getJSONObject("p"), "orderId");
        return value;
    }

    private static String text(JSONObject object, String key) {
        if (object == null || object.get(key) == null) return null;
        String value = String.valueOf(object.get(key)).trim();
        return value.length() == 0 ? null : value;
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        Map<String, Object> ext = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            Object item = JdbcValue.normalize(result.getObject(i));
            if (item == null) continue;
            String key = KEYS.get(label.toUpperCase(Locale.ROOT));
            if (key == null) ext.put(label.toLowerCase(Locale.ROOT), item);
            else value.put(key, item);
        }
        if (!ext.isEmpty()) value.put("ext$", ext);
        return value;
    }

    private static Map<String, String> keys() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        String[] names = {
            "ID=id", "SAMPLENO=sampleNo", "PROJID=projId", "PROJNO=projNo", "MONITORTMPLID=monitorTmplId",
            "ORDERCATEGORY=orderCategory", "SOURCE=source", "STATUS=status", "HANDSTATUS=handStatus", "REMARK=remark",
            "PROPERTYTMPLID=propertyTmplId", "REPORTCONFIGLINEID=reportConfigLineId", "SEPSAMPLETIME=sepSampleTime",
            "ACCEPTEDUSERID=acceptedUserId", "ACCEPTEUSERNAME=accepteUserName", "ACCEPTEDDATE=acceptedDate",
            "ACCEPTEDTIME=acceptedTime", "MOBILEID=mobileId", "SAMPLECHECK=sampleCheck", "USEDCONTAINER=usedContainer",
            "STORAGEVALID=storageValid", "EXCHANGEVALID=exchangeValid", "SAMPLINGVALID=samplingValid", "MARKVALID=markValid",
            "CONTAINERVALID=containerValid", "QCUSERID=qcUserId", "QCUSERNAME=qcUserName", "CALIBRATIONID=calibrationId",
            "CALIREPORTLINEID=caliReportLineId", "RECEIVEREPORTLINEID=receiveReportLineId", "PRINTFLAG=printFlag",
            "CREATEDBYID=createdById", "CREATEDBYNAME=createdByName", "CREATEDTIME=createdTime",
            "CREATEDBYORGID=createdByOrgId", "CREATEDBYORGNAME=createdByOrgName", "ORGID=orgId", "ORGNAME=orgName",
            "ACCEPTSTATUS=acceptStatus", "ASSIGNSTATUS=assignStatus", "UNDOREASON=undoReason", "UNDOFLAG=undoFlag",
            "SAMPLESCENEUSERID=sampleSceneUserId", "SAMPLESCENEUSERNAME=sampleSceneUserName"
        };
        for (String name : names) {
            int split = name.indexOf('=');
            result.put(name.substring(0, split), name.substring(split + 1));
        }
        return Collections.unmodifiableMap(result);
    }

    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }
}
