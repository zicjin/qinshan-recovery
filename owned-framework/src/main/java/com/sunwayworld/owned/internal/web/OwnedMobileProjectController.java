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
import java.util.Arrays;
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

/**
 * Source-owned read seam for the mobile project resource.
 *
 * <p>The mobile resource is a project route family in the same Spring
 * application.  This slice deliberately implements the stable list,
 * operation-type and missing-query-type contracts first; state-changing
 * upload/create/delete routes remain separate HTTP work items.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMobileProjectController {
    private static final String PREFIX = "/secure/emc/module/bp/mobile/projects";
    private static final List<String> OPERATION_TYPES = Arrays.asList(
            "equipt", "equiptTestMethod", "emp", "propertyField", "monitorTmpl",
            "coreCode", "itemResult", "monitorPoint", "client", "monitorTmplGroup",
            "monitorScheme", "test", "testCategory", "testMethod", "method", "item",
            "itemGroup", "groupItem", "container", "calibration", "equiptCategory",
            "unit", "qcCategory", "monitorPointCategory", "testOrderCategory",
            "testCapacity", "acqScript", "easParam", "easPreAnaly", "easResult",
            "equiptTestItem", "capacityItemGroup", "capacityItem", "relatedTestMethod");

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedMobileProjectController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("mobile project dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = PREFIX + "/queries", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> projects(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(projectList(principal.getOrgId()));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = PREFIX + "/operation-types/queries", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> operationTypes(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            require(request);
            Map<String, Object> response = new LinkedHashMap<String, Object>();
            response.put("operateTypeList", OPERATION_TYPES);
            return ok(JSON.toJSONString(response));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {PREFIX + "/static-data/queries", PREFIX + "/static-data/pagination/queries"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> staticData(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            require(request);
            // The supplier contract rejects a request without a concrete
            // operation type, including an otherwise valid pagination body.
            return ok("{\"message\":\"查询类型不能为空\"}");
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String projectList(String organization) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        String sql = "SELECT P.* FROM T_EMC_PROJECT P WHERE P.CREATEDBYORGID LIKE ? ORDER BY P.ID DESC";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, (organization == null ? "__NO_ORG__" : organization) + "%");
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                while (result.next()) rows.add(row(result, metadata));
            }
        }
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("projectList", rows);
        return JSON.toJSONString(response, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            value.put(camel(metadata.getColumnLabel(i)), JdbcValue.normalize(result.getObject(i)));
        }
        return value;
    }

    private static String camel(String name) {
        StringBuilder value = new StringBuilder();
        boolean upper = false;
        for (char character : name.toLowerCase(Locale.ROOT).toCharArray()) {
            if (character == '_') { upper = true; continue; }
            value.append(upper ? Character.toUpperCase(character) : character);
            upper = false;
        }
        return value.toString();
    }

    private static ResponseEntity<String> ok(String body) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(body);
    }
    private static ResponseEntity<String> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
    }
    private static ResponseEntity<String> unavailable() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MOBILE.PROJECT.UNAVAILABLE\"}");
    }
}
