package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned read validation for the original material-issue workflow. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedMaterialApplyController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedMaterialApplyController(OwnedIdentityHttpBoundary boundary, DataSource dataSource,
            Environment environment) {
        if (boundary == null || dataSource == null || environment == null)
            throw new IllegalArgumentException("material apply dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource; this.environment = environment;
    }

    @PostMapping(path = {"/secure/core/module/item/mtl-applies/action/validate-stock",
            "/secure/emc/module/item/mtl-applies/action/validate-stock"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validateStock(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireRead(request);
            Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
            return ok(JSON.toJSONString(validate(body, organizations, dataSource)));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @PostMapping(path = {"/secure/core/module/item/mtl-applies", "/secure/emc/module/item/mtl-applies"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insert(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            List<JSONObject> rows = rowObjects(body);
            if (rows.isEmpty()) throw new IllegalArgumentException("material apply rows are required");
            Set<String> organizations = boundary.permittedOrganizationIds(request.getSession(false));
            if (!(Boolean.TRUE.equals(validate(body, organizations, dataSource).get("valid")))) throw new IllegalArgumentException("stock quantity is insufficient");
            List<Long> ids = OwnedWriteSupport.transaction(dataSource, c -> insertRows(c, rows, principal));
            boundary.recordAllowed("material-apply-insert", principal, ids.toString(), "isolated-write");
            return ok(JSON.toJSONString(Collections.singletonMap("ids", ids)));
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = {"/secure/core/module/item/mtl-applies/action/start-process",
            "/secure/emc/module/item/mtl-applies/action/start-process"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> startProcess(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            List<Long> ids = workflowIds(body);
            OwnedWriteSupport.transaction(dataSource, c -> updateProcessStatus(c, ids, principal, "draft", "approve"));
            boundary.recordAllowed("material-apply-start-process", principal, ids.toString(), "isolated-write");
            return ok("");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = {"/secure/core/module/item/mtl-applies/action/reject-task",
            "/secure/emc/module/item/mtl-applies/action/reject-task"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rejectTask(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            List<Long> ids = workflowIds(body);
            OwnedWriteSupport.transaction(dataSource, c -> updateProcessStatus(c, ids, principal, "approve", "draft"));
            boundary.recordAllowed("material-apply-reject-task", principal, ids.toString(), "isolated-write");
            return ok("");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = {"/secure/core/module/item/mtl-applies/action/odd-reject-task",
            "/secure/emc/module/item/mtl-applies/action/odd-reject-task"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> oddRejectTask(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            List<Long> ids = workflowIds(body);
            // CoreBpmnRuntimeOddRejectProcessSupport emits DRAFT_REJECTED and
            // removes the active instance; the source workflow accepts the
            // active approval state without the ordinary reject validator.
            OwnedWriteSupport.transaction(dataSource,
                    c -> updateProcessStatus(c, ids, principal, "approve", "draft_rejected"));
            boundary.recordAllowed("material-apply-odd-reject-task", principal, ids.toString(), "isolated-write");
            return ok("");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = {"/secure/core/module/item/mtl-applies/action/undo",
            "/secure/emc/module/item/mtl-applies/action/undo"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> undo(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            List<Long> ids = workflowIds(body);
            // GenericAuditableService exposes undoable rows only at DONE and
            // CoreBpmnRuntimeUndoProcessSupport emits DRAFT_UNDO. The original
            // material service does not override undo, so stock quantities and
            // stock logs remain unchanged by this workflow-only transition.
            OwnedWriteSupport.transaction(dataSource,
                    c -> updateProcessStatus(c, ids, principal, "done", "draft_undo"));
            boundary.recordAllowed("material-apply-undo", principal, ids.toString(), "isolated-write");
            return ok("");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = {"/secure/core/module/item/mtl-applies/action/withdraw-process",
            "/secure/emc/module/item/mtl-applies/action/withdraw-process"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> withdrawProcess(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            List<Long> ids = workflowIds(body);
            // GenericAuditableService selects withdrawable rows with PROCESSSTATUS=approve;
            // the original BPMN withdraw support emits ProcessStatus.DRAFT_WITHDRAWED
            // after its SUBMIT-comment validation succeeds.
            OwnedWriteSupport.transaction(dataSource,
                    c -> updateProcessStatus(c, ids, principal, "approve", "draft_withdrawed"));
            boundary.recordAllowed("material-apply-withdraw-process", principal, ids.toString(), "isolated-write");
            return ok("");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = {"/secure/core/module/item/mtl-applies/action/complete-task",
            "/secure/emc/module/item/mtl-applies/action/complete-task"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> completeTask(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            List<Long> ids = workflowIds(body);
            OwnedWriteSupport.transaction(dataSource, c -> completeRows(c, ids, principal));
            boundary.recordAllowed("material-apply-complete-task", principal, ids.toString(), "isolated-write");
            return ok("");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @DeleteMapping(path = {"/secure/core/module/item/mtl-applies", "/secure/emc/module/item/mtl-applies"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request); List<Long> ids = ids(body);
            int deleted = OwnedWriteSupport.transaction(dataSource, c -> deleteRows(c, ids, principal));
            boundary.recordAllowed("material-apply-delete", principal, ids.toString(), "isolated-write");
            return ok("{\"deleted\":" + deleted + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    static Map<String, Object> validate(String body, Set<String> organizations, DataSource dataSource) throws SQLException {
        Map<Long, Double> requested = requested(body);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (requested.isEmpty()) { result.put("valid", Boolean.TRUE); return result; }
        if (organizations == null || organizations.isEmpty()) { result.put("valid", Boolean.FALSE); return result; }
        StringBuilder sql = new StringBuilder("SELECT S.ID,S.STOCKQTY FROM T_EMC_STOCK S WHERE S.ID IN (");
        int index = 0; for (Long ignored : requested.keySet()) { if (index++ > 0) sql.append(','); sql.append('?'); }
        sql.append(") AND S.CREATEDBYORGID IN ("); index = 0; for (String ignored : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); }
        sql.append(')');
        Map<Long, Double> available = new LinkedHashMap<Long, Double>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
            index = 1; for (Long id : requested.keySet()) query.setLong(index++, id);
            for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) { while (rows.next()) available.put(rows.getLong(1), rows.getDouble(2)); }
        }
        boolean valid = available.size() == requested.size();
        if (valid) for (Map.Entry<Long, Double> item : requested.entrySet()) {
            Double stock = available.get(item.getKey());
            if (item.getValue() < 0d || stock == null || item.getValue() > stock) { valid = false; break; }
        }
        result.put("valid", valid); return result;
    }

    private OwnedPrincipal requireRead(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private OwnedPrincipal requireWrite(HttpServletRequest request) throws SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.sss2.write.mode"))) throw denied();
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_MANAGE);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static List<Long> insertRows(Connection connection, List<JSONObject> rows, OwnedPrincipal principal) throws SQLException {
        List<Long> ids = new ArrayList<Long>();
        String sql = "INSERT INTO T_EMC_MTL_APPLY (ID,MTLID,APPLYQTY,REMARK,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,PROCESSSTATUS,STOCKID) VALUES (?,?,?,?,?,?,SYSTIMESTAMP,?,?,?,?)";
        for (JSONObject row : rows) {
            long stockId = number(row.get("stockId"), "stockId");
            String mtlId = text(row.get("mtlId"));
            if (mtlId.length() == 0) throw new IllegalArgumentException("mtlId is required");
            double quantity = decimal(row.get("applyQty"), "applyQty");
            long id = OwnedWriteSupport.nextIdentity(connection);
            try (PreparedStatement insert = connection.prepareStatement(sql)) {
                insert.setLong(1, id); insert.setString(2, mtlId); insert.setDouble(3, quantity);
                insert.setString(4, text(row.get("remark"))); insert.setString(5, principal.getId());
                insert.setString(6, principal.getId()); insert.setString(7, principal.getOrgId());
                insert.setString(8, principal.getOrgName()); insert.setString(9, "draft"); insert.setLong(10, stockId);
                if (insert.executeUpdate() != 1) throw new SQLException("material apply insert failed");
            }
            ids.add(id);
        }
        return ids;
    }

    private static int deleteRows(Connection connection, List<Long> ids, OwnedPrincipal principal) throws SQLException {
        int deleted = 0;
        for (Long id : ids) {
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM T_EMC_MTL_APPLY WHERE ID=? AND CREATEDBYORGID=?")) {
                delete.setLong(1, id); delete.setString(2, principal.getOrgId());
                int count = delete.executeUpdate(); if (count != 1) throw denied(); deleted += count;
            }
        }
        return deleted;
    }

    private static int updateProcessStatus(Connection connection, List<Long> ids,
            OwnedPrincipal principal, String expectedStatus, String status) throws SQLException {
        int updated = 0;
        for (Long id : ids) {
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT PROCESSSTATUS FROM T_EMC_MTL_APPLY WHERE ID=? AND CREATEDBYORGID=? FOR UPDATE")) {
                query.setLong(1, id); query.setString(2, principal.getOrgId());
                try (ResultSet rows = query.executeQuery()) {
                    if (!rows.next()) throw denied();
                    String current = rows.getString(1);
                    if (!expectedStatus.equalsIgnoreCase(current)) throw new IllegalArgumentException("material apply is not " + expectedStatus);
                }
            }
            try (PreparedStatement update = connection.prepareStatement(
                    "UPDATE T_EMC_MTL_APPLY SET PROCESSSTATUS=? WHERE ID=? AND CREATEDBYORGID=?")) {
                update.setString(1, status); update.setLong(2, id); update.setString(3, principal.getOrgId());
                if (update.executeUpdate() != 1) throw denied();
                updated++;
            }
        }
        return updated;
    }

    private static int completeRows(Connection connection, List<Long> ids,
            OwnedPrincipal principal) throws SQLException {
        Map<Long, ApplyRow> applies = new LinkedHashMap<Long, ApplyRow>();
        String applySql = "SELECT ID,MTLID,STOCKID,APPLYQTY,CREATEDBYID,CREATEDBYORGID,PROCESSSTATUS "
                + "FROM T_EMC_MTL_APPLY WHERE ID IN (" + placeholders(ids.size()) + ") AND CREATEDBYORGID=? FOR UPDATE";
        try (PreparedStatement query = connection.prepareStatement(applySql)) {
            int index = 1; for (Long id : ids) query.setLong(index++, id); query.setString(index, principal.getOrgId());
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    ApplyRow row = new ApplyRow(rows.getLong(1), rows.getString(2), rows.getLong(3),
                            rows.getDouble(4), rows.getString(5), rows.getString(6), rows.getString(7));
                    if (!"approve".equalsIgnoreCase(row.processStatus)) throw new IllegalArgumentException("material apply is not awaiting approval");
                    if (row.applyQty <= 0d || row.mtlId == null || row.mtlId.trim().length() == 0) {
                        throw new IllegalArgumentException("material apply row is invalid");
                    }
                    applies.put(row.id, row);
                }
            }
        }
        if (applies.size() != ids.size()) throw denied();

        Map<Long, Double> requestedStock = new LinkedHashMap<Long, Double>();
        Map<String, Double> requestedMtl = new LinkedHashMap<String, Double>();
        for (ApplyRow row : applies.values()) {
            requestedStock.put(row.stockId, plus(requestedStock.get(row.stockId), row.applyQty));
            requestedMtl.put(row.mtlId, plus(requestedMtl.get(row.mtlId), row.applyQty));
        }
        Map<Long, Double> stockQty = new LinkedHashMap<Long, Double>();
        Map<Long, String> stockMtl = new LinkedHashMap<Long, String>();
        String stockSql = "SELECT ID,MTLID,STOCKQTY FROM T_EMC_STOCK WHERE ID IN (" + placeholders(requestedStock.size()) + ") FOR UPDATE";
        try (PreparedStatement query = connection.prepareStatement(stockSql)) {
            int index = 1; for (Long id : requestedStock.keySet()) query.setLong(index++, id);
            try (ResultSet rows = query.executeQuery()) { while (rows.next()) { stockMtl.put(rows.getLong(1), rows.getString(2)); stockQty.put(rows.getLong(1), rows.getDouble(3)); } }
        }
        if (stockQty.size() != requestedStock.size()) throw new IllegalArgumentException("material stock is missing");
        for (Map.Entry<Long, Double> item : requestedStock.entrySet()) {
            Double available = stockQty.get(item.getKey());
            if (available == null || available < item.getValue()) throw new IllegalArgumentException("stock quantity is insufficient");
        }
        Map<String, Double> mtlQty = new LinkedHashMap<String, Double>();
        String mtlSql = "SELECT ID,STOCKQTY FROM T_EMC_MTL WHERE ID IN (" + placeholders(requestedMtl.size()) + ") FOR UPDATE";
        try (PreparedStatement query = connection.prepareStatement(mtlSql)) {
            int index = 1; for (String id : requestedMtl.keySet()) query.setString(index++, id);
            try (ResultSet rows = query.executeQuery()) { while (rows.next()) mtlQty.put(rows.getString(1), rows.getDouble(2)); }
        }
        if (mtlQty.size() != requestedMtl.size()) throw new IllegalArgumentException("material is missing");
        for (Map.Entry<String, Double> item : requestedMtl.entrySet()) {
            Double available = mtlQty.get(item.getKey());
            if (available == null || available < item.getValue()) throw new IllegalArgumentException("material quantity is insufficient");
        }
        for (ApplyRow row : applies.values()) {
            String materialId = stockMtl.get(row.stockId);
            if (materialId != null && row.mtlId != null && !row.mtlId.equals(materialId)) throw new IllegalArgumentException("stock material mismatch");
            double previous = stockQty.get(row.stockId);
            double next = previous - row.applyQty;
            stockQty.put(row.stockId, next);
            try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_STOCK SET STOCKQTY=? WHERE ID=?")) {
                update.setDouble(1, next); update.setLong(2, row.stockId); if (update.executeUpdate() != 1) throw new SQLException("stock update failed");
            }
            double materialNext = mtlQty.get(row.mtlId) - row.applyQty;
            mtlQty.put(row.mtlId, materialNext);
            try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_MTL SET STOCKQTY=? WHERE ID=?")) {
                update.setDouble(1, materialNext); update.setString(2, row.mtlId); if (update.executeUpdate() != 1) throw new SQLException("material update failed");
            }
            long logId = OwnedWriteSupport.nextIdentity(connection);
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO T_EMC_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?, ?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                int index = 1; insert.setLong(index++, logId); insert.setLong(index++, row.stockId); insert.setString(index++, "领取");
                insert.setDouble(index++, previous); insert.setDouble(index++, next); insert.setString(index++, row.createdById);
                insert.setString(index++, row.createdByOrgId); insert.setString(index++, principal.getId()); insert.setString(index++, principal.getId());
                insert.setString(index++, principal.getOrgId()); insert.setString(index++, principal.getOrgName());
                if (insert.executeUpdate() != 1) throw new SQLException("stock log insert failed");
            }
            try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_MTL_APPLY SET PROCESSSTATUS='done' WHERE ID=? AND CREATEDBYORGID=?")) {
                update.setLong(1, row.id); update.setString(2, principal.getOrgId()); if (update.executeUpdate() != 1) throw new SQLException("material apply update failed");
            }
        }
        return applies.size();
    }

    private static String placeholders(int count) {
        if (count <= 0) throw new IllegalArgumentException("at least one value is required");
        StringBuilder text = new StringBuilder("?"); for (int i = 1; i < count; i++) text.append(",?"); return text.toString();
    }

    private static double plus(Double value, double amount) { return (value == null ? 0d : value) + amount; }

    public static List<Long> workflowIds(String body) {
        if (body == null || body.trim().length() == 0) throw new IllegalArgumentException("workflow rows are required");
        Object parsed = JSON.parse(body); if (!(parsed instanceof JSONObject)) throw new IllegalArgumentException("workflow request is invalid");
        JSONObject object = (JSONObject) parsed; Object raw = object.get("b");
        if (raw instanceof JSONArray) { JSONArray tuple = (JSONArray) raw; raw = tuple.size() > 1 ? tuple.get(1) : null; }
        if (!(raw instanceof JSONArray)) raw = object.get("rows");
        if (!(raw instanceof JSONArray)) raw = object.get("ids");
        if (!(raw instanceof JSONArray)) throw new IllegalArgumentException("workflow rows are required");
        LinkedHashSet<Long> unique = new LinkedHashSet<Long>();
        for (Object value : (JSONArray) raw) {
            Object id = value instanceof JSONObject ? ((JSONObject) value).get("id") : value;
            long parsedId = number(id, "id"); if (!unique.add(parsedId)) throw new IllegalArgumentException("duplicate workflow id");
        }
        if (unique.isEmpty()) throw new IllegalArgumentException("workflow rows are required");
        return new ArrayList<Long>(unique);
    }

    private static final class ApplyRow {
        private final long id; private final String mtlId; private final long stockId; private final double applyQty;
        private final String createdById; private final String createdByOrgId; private final String processStatus;
        private ApplyRow(long id, String mtlId, long stockId, double applyQty, String createdById,
                String createdByOrgId, String processStatus) {
            this.id = id; this.mtlId = mtlId; this.stockId = stockId; this.applyQty = applyQty;
            this.createdById = createdById; this.createdByOrgId = createdByOrgId; this.processStatus = processStatus;
        }
    }

    private static List<Long> ids(String body) {
        Object parsed = body == null || body.trim().isEmpty() ? null : JSON.parse(body);
        Object raw = parsed instanceof JSONObject ? ((JSONObject) parsed).get("ids") : parsed;
        if (raw == null && parsed instanceof JSONObject) raw = ((JSONObject) parsed).get("p");
        if (!(raw instanceof JSONArray)) throw new IllegalArgumentException("ids are required");
        List<Long> ids = new ArrayList<Long>(); for (Object value : (JSONArray) raw) ids.add(number(value, "id"));
        if (ids.isEmpty()) throw new IllegalArgumentException("ids are required"); return ids;
    }

    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }

    public static Map<Long, Double> requested(String body) {
        JSONArray values = new JSONArray(); values.addAll(rowObjects(body));
        Map<Long, Double> requested = new LinkedHashMap<Long, Double>();
        for (Object value : values) {
            if (!(value instanceof JSONObject)) throw new IllegalArgumentException("material apply rows are invalid");
            JSONObject row = (JSONObject) value; long stockId = number(row.get("stockId"), "stockId");
            double quantity = decimal(row.get("applyQty"), "applyQty"); requested.put(stockId, requested.containsKey(stockId) ? requested.get(stockId) + quantity : quantity);
        }
        return requested;
    }

    private static List<JSONObject> rowObjects(String body) {
        Object parsed = body == null || body.trim().isEmpty() ? new JSONArray() : JSON.parse(body);
        Object rows = parsed;
        if (parsed instanceof JSONObject) {
            JSONObject object = (JSONObject) parsed; rows = object.get("p");
            if (rows == null) rows = object.get("rows");
            if (rows == null) rows = object;
        }
        JSONArray values = rows instanceof JSONArray ? (JSONArray) rows : new JSONArray();
        if (!(rows instanceof JSONArray)) values.add(rows);
        List<JSONObject> result = new ArrayList<JSONObject>();
        for (Object value : values) { if (!(value instanceof JSONObject)) throw new IllegalArgumentException("material apply rows are invalid"); result.add((JSONObject) value); }
        return result;
    }

    private static long number(Object value, String name) { try { long parsed = Long.parseLong(String.valueOf(value)); if (parsed <= 0) throw new Exception(); return parsed; } catch (Exception e) { throw new IllegalArgumentException(name + " is invalid"); } }
    private static double decimal(Object value, String name) { try { double parsed = Double.parseDouble(String.valueOf(value)); if (Double.isNaN(parsed) || Double.isInfinite(parsed)) throw new Exception(); return parsed; } catch (Exception e) { throw new IllegalArgumentException(name + " is invalid"); } }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private void denied(HttpServletRequest request) { boundary.recordRequestDenied(request == null ? null : request.getSession(false), "material-apply.write", "permission-denied"); }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().header("Cache-Control", "no-store").body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.MTL_APPLY.INVALID_REQUEST\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.MTL_APPLY.UNAVAILABLE\"}"); }
}
