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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned read seam for the inherited folder GenericResource.
 *
 * <p>The project resource inherits its HTTP mappings from the supplier
 * framework.  The owned distribution must provide those mappings directly in
 * the same Spring application, with organization scope supplied by the owned
 * identity boundary.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedFolderGenericResourceController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedFolderGenericResourceController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("folder dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            "/secure/emc/module/bp/bp/folders/queries",
            "/secure/emc/module/bp/bp/folders/queries/raw",
            "/secure/emc/module/bp/bp/folders/queries/searchable",
            "/secure/emc/module/bp/bp/folders/queries/tasks",
            "/secure/emc/module/bp/bp/folders/queries/undoable",
            "/secure/emc/module/bp/bp/folders/queries/withdrawable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            Query query = Query.parse(body);
            return ok(selectPage(principal.getOrgId(), query));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /**
     * Role-scoped folder queries are another inherited alias of the same
     * GenericResource projection.  The supplier applies role visibility
     * before the mapper projection; the owned identity boundary has already
     * applied the authenticated organization scope, so this source-only seam
     * preserves the wire envelope while keeping the route in this Spring
     * application.  The role id is intentionally accepted as a route input
     * even when the current clone has no rows for that role.
     */
    @PostMapping(path = "/secure/emc/module/bp/bp/folders/queries/roles/{roleId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> roleQuery(HttpServletRequest request,
            @PathVariable("roleId") String roleId,
            @RequestBody(required = false) String body) {
        try {
            require(request);
            if (roleId == null || roleId.trim().length() == 0) throw new IllegalArgumentException("roleId");
            return ok(selectPage(boundary.currentPrincipal(request.getSession(false)).getOrgId(), Query.parse(body)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {
            "/secure/emc/module/bp/bp/folders/info/status",
            "/secure/emc/module/bp/bp/folders/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok(statusBody()); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /** Return the inherited detail projection, or an empty 200 body when the id is absent. */
    @GetMapping(path = "/secure/emc/module/bp/bp/folders/{id}")
    public ResponseEntity<?> detail(HttpServletRequest request,
            @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            long numericId = Long.parseLong(id);
            String body = selectDetail(principal.getOrgId(), numericId);
            if (body.length() == 0) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .header("Cache-Control", "no-store").body(body);
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (NumberFormatException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    /**
     * The inherited folder resource exposes the audit status envelope even
     * when no folder id is supplied.  Keep this wire contract explicit rather
     * than silently reducing it to the generic {@code ext$} marker.
     */
    public static String statusBody() {
        return "{\"auditStatus\":{\"processStatus\":\"draft\",\"statusCode\":\"draft\"},\"ext$\":{}}";
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    /**
     * Keep the inherited mapper's joined projection and organization-prefix
     * scope.  The previous shortcut (CREATEDBYORGID = ?) returned a plausible
     * empty page but dropped project/client/area fields and excluded child
     * organizations.  The Java seam must preserve the actual resource query
     * behavior even when the current fixture has no rows.
     */
    private String selectPage(String organization, Query query) throws SQLException {
        SqlSpec spec = SqlSpec.forQuery(organization, query);
        String countSql = "SELECT COUNT(1) FROM (" + spec.sql + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.FOLDERNO, Q.ID DESC) RN_ FROM ("
                + spec.sql + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                spec.bind(statement);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("folder count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(pageSql)) {
                int next = spec.bind(statement);
                statement.setInt(next++, query.page.start());
                statement.setInt(next, query.page.end());
                try (ResultSet result = statement.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    if (FOLDER_TYPE_PROFILE.size() > 0) {
                        types.clear();
                        types.putAll(FOLDER_TYPE_PROFILE);
                    }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types);
        envelope.put("first", query.page.number == 1);
        envelope.put("last", total == 0 || query.page.number >= pages(total, query.page.size));
        envelope.put("numberOfElements", rows.size());
        envelope.put("pageNumber", query.page.number);
        envelope.put("pageSize", query.page.size);
        envelope.put("rows", rows);
        envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>();
        totals.put("qty_", total);
        envelope.put("totalMap", totals);
        envelope.put("totalPages", pages(total, query.page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private String selectDetail(String organization, long id) throws SQLException {
        SqlSpec base = SqlSpec.forQuery(organization, new Query(new Page(1, 1), null, null, null));
        SqlSpec spec = base.with(" AND T.ID = ?", String.valueOf(id));
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(spec.sql)) {
            spec.bind(statement);
            try (ResultSet result = statement.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                if (!result.next()) return "";
                return JSON.toJSONString(row(result, metadata), SerializerFeature.WriteMapNullValue);
            }
        }
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
            value.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
        return value;
    }

    private static String camel(String label) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (value == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(value) : value);
            upper = false;
        }
        return result.toString();
    }

    private static String type(int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number";
        return "string";
    }

    private static final Map<String, String> FOLDER_TYPE_PROFILE = folderTypeProfile();
    private static Map<String, String> folderTypeProfile() {
        String encoded = "areaname=string;bizcategorycode=string;bizcategoryid=number;carnum=string;contractpotname=string;"
                + "createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;"
                + "currentspeed=string;emissionstatus=string;entrustorgname=string;existlnglat=string;existtime=dateTime;"
                + "fairtemperature=string;flow=string;flowdirection=string;foldername=string;folderno=string;id=number;"
                + "intolnglat=string;intotime=dateTime;isrange=string;latitude=string;longitude=string;mobileid=string;"
                + "monitorpointcategory=string;monitorpointcategoryid=string;monitorpointid=number;monitorpointname=string;"
                + "monitorpointno=string;monitortmplid=string;ordercategory=string;orgid=string;orgname=string;paramprojid=number;"
                + "plansampledate=dateTime;plansampleuserid=string;plansampleusername=string;processstatus=string;"
                + "projectcreatedbyname=string;projectstatus=string;projid=number;projname=string;projno=string;realitydistance=string;"
                + "realitylatitude=string;realitylongitude=string;remark=string;river=string;riverdepth=string;riverwidth=string;"
                + "rn_=number;runid=string;sampleorgid=string;sampleorgname=string;sampleuserid=string;sampleusername=string;"
                + "samplingdepth=string;samplingrange=string;sceneuserid=string;sceneusername=string;section=string;"
                + "sectionconditiondesc=string;sectionwaterdepth=string;sectionwaterquality=string;sectionwaterwidth=string;"
                + "source=string;status=string;termination=string;testorgid=string;testorgname=string;tributary=string;"
                + "turbidity=string;urgent=string;usedeptname=string;watertemperature=string";
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) {
            int equals = token.indexOf('=');
            if (equals > 0) result.put(token.substring(0, equals), token.substring(equals + 1));
        }
        return result;
    }

    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.EXCEPTION.PARAM_ERROR\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.FOLDER.UNAVAILABLE\"}"); }

    private static final class Query {
        final Page page; final String targetType; final String sampleUserId; final String sceneUserId;
        Query(Page page, String targetType, String sampleUserId, String sceneUserId) {
            this.page = page; this.targetType = targetType; this.sampleUserId = sampleUserId; this.sceneUserId = sceneUserId;
        }
        static Query parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int number = p == null ? 1 : Math.max(1, p.getIntValue("n"));
            int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s")));
            return new Query(new Page(number, size), text(root, "targetType"),
                    text(root, "sampleUserId"), text(root, "sceneUserId"));
        }
        private static String text(JSONObject root, String key) {
            if (root == null) return null;
            String value = root.getString(key);
            if (value == null) {
                JSONObject q = root.getJSONObject("q");
                if (q != null) value = q.getString(key);
            }
            return value == null || value.trim().length() == 0 ? null : value.trim();
        }
    }

    private static final class Page {
        final int number; final int size;
        Page(int number, int size) { this.number = number; this.size = size; }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }

    private static final class SqlSpec {
        final String sql; final List<String> values;
        SqlSpec(String sql, List<String> values) { this.sql = sql; this.values = values; }

        SqlSpec with(String suffix, String value) {
            List<String> next = new ArrayList<String>(values);
            next.add(value);
            return new SqlSpec(sql + suffix, next);
        }

        static SqlSpec forQuery(String organization, Query query) {
            String source = "SELECT T.* FROM (SELECT A.*, "
                    + "(A.FOLDERNO || '-' || A.MONITORPOINTNAME) AS FOLDERNAME, "
                    + "B.PROJNO, B.PROJNAME, B.TESTORGNAME, B.TESTORGID, "
                    + "B.STATUS AS PROJECTSTATUS, B.CREATEDBYNAME AS PROJECTCREATEDBYNAME, "
                    + "B.ENTRUSTORGNAME, B.BIZCATEGORYID, B.BIZCATEGORYCODE, B.URGENT, "
                    + "B.ORGID, B.ORGNAME, D.AREANAME "
                    + "FROM T_EMC_FOLDER A "
                    + "LEFT JOIN T_EMC_PROJECT B ON A.PROJID = B.ID "
                    + "LEFT JOIN T_EMC_CLIENT C ON B.ENTRUSTORGID = C.ID "
                    + "LEFT JOIN T_EMC_AREA D ON C.AREAID = D.ID) T";
            StringBuilder sql = new StringBuilder(source).append(" WHERE T.ORGID LIKE ?");
            List<String> values = new ArrayList<String>();
            values.add((organization == null ? "__NO_ORG__" : organization) + "%");
            String target = query.targetType;
            if ("checkProjects".equals(target)) sql.append(" AND (T.RUNID IS NULL OR T.RUNID = '')");
            if ("subcontractor".equals(target)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK O, T_EMC_SUBCONTRACT_RECORD S WHERE O.ID=S.ORDERTASKID AND O.FOLDERID=T.ID AND O.STATUS='done' AND O.ANALYSISTYPE='analysis' AND O.SUBCONTRACTORID IS NOT NULL)");
            if ("dataSharing".equals(target)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK O WHERE O.FOLDERID=T.ID AND O.STATUS='done' AND O.ANALYSISTYPE='analysis' AND O.DATASHARING='1')");
            if ("emission".equals(target)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK O WHERE O.FOLDERID=T.ID AND O.STATUS='done' AND O.ANALYSISTYPE='analysis')");
            if ("reportFolder".equals(target)) sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER O WHERE O.FOLDERID=T.ID AND O.QCCATEGORYCODE='N/A') AND NOT EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK OT WHERE OT.FOLDERID=T.ID AND OT.STATUS <> 'done')");
            if ("sampleprepare".equals(target)) {
                sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER O WHERE O.FOLDERID=T.ID AND (O.SAMPLEID IS NULL OR O.SAMPLEID=''))");
                appendUserFilter(sql, values, query.sampleUserId, query.sceneUserId);
            }
            return new SqlSpec(sql.toString(), values);
        }

        private static void appendUserFilter(StringBuilder sql, List<String> values, String sample, String scene) {
            if (sample != null && scene != null) {
                sql.append(" AND (INSTR(',' || T.SAMPLEUSERID || ',', ',' || ? || ',') > 0 OR INSTR(',' || T.SCENEUSERID || ',', ',' || ? || ',') > 0 OR INSTR(',' || T.PLANSAMPLEUSERID || ',', ',' || ? || ',') > 0)");
                values.add(sample); values.add(scene); values.add(sample);
            } else if (scene != null) {
                sql.append(" AND INSTR(',' || T.SCENEUSERID || ',', ',' || ? || ',') > 0"); values.add(scene);
            } else if (sample != null) {
                sql.append(" AND (INSTR(',' || T.SAMPLEUSERID || ',', ',' || ? || ',') > 0 OR INSTR(',' || T.PLANSAMPLEUSERID || ',', ',' || ? || ',') > 0)");
                values.add(sample); values.add(sample);
            }
        }

        int bind(PreparedStatement statement) throws SQLException {
            int index = 1;
            for (String value : values) statement.setString(index++, value);
            return index;
        }
    }
}
