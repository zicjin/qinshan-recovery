package com.sunwayworld.owned.report;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Read-only report-line query used by the imported workspace report panel. */
@RestController
public final class OwnedHomeReportQueryController {
    private final OwnedIdentityHttpBoundary identity;
    private final DataSource dataSource;

    public OwnedHomeReportQueryController(OwnedIdentityHttpBoundary identity, DataSource dataSource) {
        this.identity = identity;
        this.dataSource = dataSource;
    }

    @PostMapping(path = "/secure/core/module/sys/report-configs/homePageReport/lines/queries",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            identity.currentPrincipal(request.getSession(false));
            PageRequest page = PageRequest.parse(body);
            return ResponseEntity.ok(JSON.toJSONString(read(page)));
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (IllegalArgumentException invalid) {
            return ResponseEntity.badRequest()
                    .body("{\"message\":\"GIKAM.REPORT.INVALID_REQUEST\"}");
        } catch (SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"GIKAM.REPORT.UNAVAILABLE\"}");
        }
    }

    private Map<String, Object> read(PageRequest page) throws SQLException {
        List<Map<String, Object>> all = new ArrayList<Map<String, Object>>();
        String sql = "SELECT * FROM T_CORE_REPORT_CONFIG_LINE WHERE REPORTCONFIGID=? ORDER BY ID";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, "homePageReport");
            try (ResultSet result = query.executeQuery()) {
                ResultSetMetaData metadata = result.getMetaData();
                while (result.next()) all.add(row(result, metadata));
            }
        }
        int from = Math.min((page.number - 1) * page.size, all.size());
        int to = Math.min(from + page.size, all.size());
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(all.subList(from, to));
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> ext = new LinkedHashMap<String, Object>();
            ext.put("rn_", String.valueOf(from + i + 1));
            rows.get(i).put("ext$", ext);
        }
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("columnTypeMap", columnTypes(rows));
        response.put("first", page.number == 1);
        response.put("last", to >= all.size());
        response.put("numberOfElements", rows.size());
        response.put("pageNumber", page.number);
        response.put("pageSize", page.size);
        response.put("rows", rows);
        response.put("total", all.size());
        Map<String, Object> totalMap = new LinkedHashMap<String, Object>();
        totalMap.put("qty_", all.size());
        response.put("totalMap", totalMap);
        response.put("totalPages", all.isEmpty() ? 0 : (all.size() + page.size - 1) / page.size);
        return response;
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String name = metadata.getColumnLabel(i);
            Object value = result.getObject(i);
            if (value instanceof Timestamp) value = ((Timestamp) value).toLocalDateTime().toLocalDate().toString();
            row.put(camel(name), JdbcValue.normalize(value));
        }
        return row;
    }

    private static Map<String, String> columnTypes(List<Map<String, Object>> rows) {
        Map<String, String> types = new LinkedHashMap<String, String>();
        if (rows.isEmpty()) return types;
        for (Map.Entry<String, Object> entry : rows.get(0).entrySet()) {
            if ("ext$".equals(entry.getKey())) continue;
            Object value = entry.getValue();
            types.put(entry.getKey(), value instanceof Number ? "number" : "string");
        }
        return types;
    }

    private static String camel(String value) {
        StringBuilder result = new StringBuilder(); boolean upper = false;
        for (char character : value.toLowerCase(Locale.ROOT).toCharArray()) {
            if (character == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(character) : character); upper = false;
        }
        return result.toString();
    }

    private static final class PageRequest {
        final int number; final int size;
        PageRequest(int number, int size) { this.number = number; this.size = size; }
        static PageRequest parse(String body) {
            if (body == null || body.trim().isEmpty()) return new PageRequest(1, 20);
            Object parsed = JSON.parse(body);
            if (!(parsed instanceof Map)) throw new IllegalArgumentException("request must be an object");
            Object p = ((Map<?, ?>) parsed).get("p");
            Map<?, ?> values = p instanceof Map ? (Map<?, ?>) p : (Map<?, ?>) parsed;
            int number = integer(values.get("n"), 1); int size = integer(values.get("s"), 20);
            // The imported Gikam grid sends n=-1 when it wants the first
            // complete page.  Treat it as page one while retaining the
            // explicit page size bound.
            if (number < 1) number = 1;
            if (size < 1 || size > 200) throw new IllegalArgumentException("invalid page");
            return new PageRequest(number, size);
        }
        private static int integer(Object value, int fallback) {
            if (value == null) return fallback;
            try { return Integer.parseInt(String.valueOf(value)); }
            catch (NumberFormatException failure) { throw new IllegalArgumentException("invalid page"); }
        }
    }
}
