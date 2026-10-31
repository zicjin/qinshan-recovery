package com.sunwayworld.owned.consumables;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Source-owned read facade for the original mdm/basemdm/mtl stock routes. */
@RestController
@RequestMapping("/secure/emc/module/mdm/basemdm/mtls")
public final class OwnedMdmStockReadController {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final Environment environment;
    private final AuditEventSink audit;

    public OwnedMdmStockReadController(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, Environment environment, AuditEventSink audit) {
        this.dataSource = dataSource; this.provider = provider; this.boundary = boundary;
        this.environment = environment; this.audit = audit;
    }

    @PostMapping("/{id}/stocks/queries")
    public Page<EmcStockBean> stocks(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return stockPage("0".equals(id) ? null : id, wrapper);
    }

    @PostMapping("/queries/detail-choosable")
    public Page<EmcStockBean> detailChoosable(RestJsonWrapperBean wrapper) { return stockPage(null, wrapper); }

    @PostMapping("/queries/raw")
    public Page<EmcMtlBean> materials(RestJsonWrapperBean wrapper) { return materialPage(wrapper); }

    @PostMapping("/queries/choosable")
    public Page<EmcMtlBean> materialChoosable(RestJsonWrapperBean wrapper) { return materialPage(wrapper); }

    @PostMapping("/{id}/general-mtl-stocks")
    public Long insertGeneralStock(@PathVariable String id, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (id == null || id.trim().isEmpty() || wrapper == null) throw new IllegalArgumentException("material and stock body are required");
        EmcStockBean stock = wrapper.parseUnique(EmcStockBean.class); EmcStockLogBean log = wrapper.parseUnique(EmcStockLogBean.class);
        if (log.getQty() == null || log.getQty().doubleValue() <= 0d || log.getQty().doubleValue() > 1000000000d)
            throw new IllegalArgumentException("stock quantity is invalid");
        try {
            OwnedPrincipal principal = managePrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long result = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireMaterialScope(connection, id, organizations);
                long stockId = OwnedWriteSupport.nextIdentity(connection); long logId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_STOCK (ID,MTLID,BATCHNO,STOCKQTY,UNITNAME,EXPIREDATE,LOWLIMIT,STATUS,FINENESS,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,VERDORNAME,REMINDDAYS) VALUES (?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?,?,?)")) {
                    insert.setLong(1, stockId); insert.setString(2, id); insert.setString(3, stock.getBatchNo()); insert.setDouble(4, log.getQty().doubleValue());
                    insert.setString(5, stock.getUnitName()); if (stock.getExpireDate() == null) insert.setNull(6, java.sql.Types.TIMESTAMP); else insert.setTimestamp(6, Timestamp.valueOf(stock.getExpireDate()));
                    if (stock.getLowLimit() == null) insert.setNull(7, java.sql.Types.NUMERIC); else insert.setDouble(7, stock.getLowLimit()); insert.setString(8, "0"); insert.setString(9, stock.getFineness());
                    insert.setString(10, principal.getId()); insert.setString(11, principal.getId()); insert.setString(12, principal.getOrgId()); insert.setString(13, principal.getOrgName()); insert.setString(14, stock.getVerdorName());
                    if (stock.getRemindDays() == null) insert.setNull(15, java.sql.Types.NUMERIC); else insert.setLong(15, stock.getRemindDays()); insert.executeUpdate();
                }
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                    insert.setLong(1, logId); insert.setLong(2, stockId); insert.setString(3, log.getLogType() == null ? "添加" : log.getLogType()); insert.setNull(4, java.sql.Types.NUMERIC); insert.setDouble(5, log.getQty()); insert.setString(6, log.getVendorId());
                    if (log.getPrice() == null) insert.setNull(7, java.sql.Types.NUMERIC); else insert.setDouble(7, log.getPrice()); insert.setString(8, log.getLogContent()); insert.setString(9, log.getReceivedById()); insert.setString(10, log.getReceivedByOrgId()); insert.setString(11, principal.getId()); insert.setString(12, principal.getId()); insert.setString(13, principal.getOrgId()); insert.setString(14, principal.getOrgName()); insert.executeUpdate();
                }
                try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_MTL SET STOCKQTY=(SELECT NVL(SUM(STOCKQTY),0) FROM T_EMC_STOCK WHERE MTLID=? AND STATUS='0') WHERE ID=?")) { update.setString(1, id); update.setString(2, id); if (update.executeUpdate() != 1) throw denied(); }
                return Long.valueOf(stockId);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-stock-general-insert", principal.getId(), String.valueOf(result), AuditEvent.Outcome.ALLOWED, "isolated-write", null)); return result;
        } catch (SQLException failure) { throw new IllegalStateException("mdm stock general insert failed", failure); }
    }

    @PutMapping("/stocks/{stockId}/action/stockin")
    public Long stockIn(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        requireWrite(); if (stockId == null || stockId.longValue() < 0L || wrapper == null) throw new IllegalArgumentException("stock and stock-in body are required");
        EmcStockLogBean log = wrapper.parseUnique(EmcStockLogBean.class);
        if (log.getQty() == null || log.getQty().doubleValue() <= 0d || log.getQty().doubleValue() > 1000000000d) throw new IllegalArgumentException("stock-in quantity is invalid");
        try {
            OwnedPrincipal principal = managePrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            String materialId = OwnedWriteSupport.transaction(dataSource, connection -> {
                StockSnapshot stock = scopedStock(connection, stockId.longValue(), organizations); double current = stock.quantity + log.getQty().doubleValue(); long logId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement update=connection.prepareStatement("UPDATE T_EMC_STOCK SET STOCKQTY=? WHERE ID=?"); PreparedStatement insert=connection.prepareStatement("INSERT INTO T_EMC_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                    update.setDouble(1,current);update.setLong(2,stockId);update.executeUpdate(); insert.setLong(1,logId);insert.setLong(2,stockId);insert.setString(3,"入库");insert.setDouble(4,stock.quantity);insert.setDouble(5,current);insert.setString(6,log.getVendorId());if(log.getPrice()==null)insert.setNull(7,java.sql.Types.NUMERIC);else insert.setDouble(7,log.getPrice());insert.setString(8,log.getLogContent());insert.setString(9,log.getReceivedById());insert.setString(10,log.getReceivedByOrgId());insert.setString(11,principal.getId());insert.setString(12,principal.getId());insert.setString(13,principal.getOrgId());insert.setString(14,principal.getOrgName());insert.executeUpdate();
                }
                updateMaterialAggregate(connection, stock.materialId); return stock.materialId;
            });
            audit.record(new AuditEvent(java.time.Instant.now(),"mdm-stock-storage",principal.getId(),String.valueOf(stockId),AuditEvent.Outcome.ALLOWED,"isolated-write",null)); return stockId;
        } catch(SQLException failure){throw new IllegalStateException("mdm stock-in failed",failure);}
    }

    @PutMapping("/{id}/stocks/action/inventory")
    public void inventory(@PathVariable String id, RestJsonWrapperBean wrapper) {
        requireWrite(); if (id == null || id.trim().isEmpty() || wrapper == null) throw new IllegalArgumentException("material and inventory body are required");
        EmcStockLogBean log = wrapper.parseUnique(EmcStockLogBean.class); if (log.getQty() == null || log.getQty().doubleValue() < 0d || log.getQty().doubleValue() > 1000000000d) throw new IllegalArgumentException("inventory quantity is invalid");
        String rawIds = log.getExt$Item("stockId"); if (rawIds == null || rawIds.trim().isEmpty()) throw new IllegalArgumentException("inventory stock ids are required"); String[] parts = rawIds.split(","); if (parts.length == 0 || parts.length > 100) throw new IllegalArgumentException("inventory stock ids are invalid");
        try {
            OwnedPrincipal principal=managePrincipal(); Set<String> organizations=provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource,connection->{for(String part:parts){long stockId;try{stockId=Long.parseLong(part.trim());}catch(NumberFormatException failure){throw new IllegalArgumentException("inventory stock ids are invalid");}StockSnapshot stock=scopedStock(connection,stockId,organizations);if(!id.equals(stock.materialId))throw denied();try(PreparedStatement update=connection.prepareStatement("UPDATE T_EMC_STOCK SET STOCKQTY=? WHERE ID=?");PreparedStatement insert=connection.prepareStatement("INSERT INTO T_EMC_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")){long logId=OwnedWriteSupport.nextIdentity(connection);update.setDouble(1,log.getQty());update.setLong(2,stockId);update.executeUpdate();insert.setLong(1,logId);insert.setLong(2,stockId);insert.setString(3,"盘存");insert.setDouble(4,stock.quantity);insert.setDouble(5,log.getQty());insert.setString(6,log.getVendorId());if(log.getPrice()==null)insert.setNull(7,java.sql.Types.NUMERIC);else insert.setDouble(7,log.getPrice());insert.setString(8,log.getLogContent());insert.setString(9,log.getReceivedById());insert.setString(10,log.getReceivedByOrgId());insert.setString(11,principal.getId());insert.setString(12,principal.getId());insert.setString(13,principal.getOrgId());insert.setString(14,principal.getOrgName());insert.executeUpdate();}}updateMaterialAggregate(connection,id);return null;});
            audit.record(new AuditEvent(java.time.Instant.now(),"mdm-stock-inventory",principal.getId(),id,AuditEvent.Outcome.ALLOWED,"isolated-write",null));
        } catch(SQLException failure){throw new IllegalStateException("mdm stock inventory failed",failure);}
    }

    @PutMapping("/{id}/stocks/action/abandon")
    public void abandon(@PathVariable String id, RestJsonWrapperBean wrapper) {
        requireWrite(); if (id == null || id.trim().isEmpty() || wrapper == null) throw new IllegalArgumentException("material and abandon body are required");
        EmcStockLogBean log=wrapper.parseUnique(EmcStockLogBean.class); String rawIds=log.getExt$Item("stockId"); if(rawIds==null||rawIds.trim().isEmpty())throw new IllegalArgumentException("stock ids are required"); String[] parts=rawIds.split(","); if(parts.length==0||parts.length>100)throw new IllegalArgumentException("stock ids are invalid");
        try { OwnedPrincipal principal=managePrincipal(); Set<String> organizations=provider.permittedOrganizationIds(principal); OwnedWriteSupport.transaction(dataSource,connection->{for(String part:parts){long stockId;try{stockId=Long.parseLong(part.trim());}catch(NumberFormatException failure){throw new IllegalArgumentException("stock ids are invalid");}StockSnapshot stock=scopedStock(connection,stockId,organizations);if(!id.equals(stock.materialId))throw denied();long logId=OwnedWriteSupport.nextIdentity(connection);try(PreparedStatement update=connection.prepareStatement("UPDATE T_EMC_STOCK SET STATUS='1' WHERE ID=?");PreparedStatement insert=connection.prepareStatement("INSERT INTO T_EMC_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")){update.setLong(1,stockId);update.executeUpdate();insert.setLong(1,logId);insert.setLong(2,stockId);insert.setString(3,"废弃");insert.setDouble(4,stock.quantity);insert.setDouble(5,stock.quantity);insert.setString(6,log.getVendorId());if(log.getPrice()==null)insert.setNull(7,java.sql.Types.NUMERIC);else insert.setDouble(7,log.getPrice());insert.setString(8,log.getLogContent());insert.setString(9,log.getReceivedById());insert.setString(10,log.getReceivedByOrgId());insert.setString(11,principal.getId());insert.setString(12,principal.getId());insert.setString(13,principal.getOrgId());insert.setString(14,principal.getOrgName());insert.executeUpdate();}}updateMaterialAggregate(connection,id);return null;}); audit.record(new AuditEvent(java.time.Instant.now(),"mdm-stock-abandon",principal.getId(),id,AuditEvent.Outcome.ALLOWED,"isolated-write",null)); } catch(SQLException failure){throw new IllegalStateException("mdm stock abandon failed",failure);}
    }

    /** Source-owned equivalent of the original material stock delete route. */
    @DeleteMapping("/{id}/stocks")
    public void deleteStocks(@PathVariable String id, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (id == null || id.trim().isEmpty() || wrapper == null)
            throw new IllegalArgumentException("material and delete body are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("stock ids are invalid");
        try {
            OwnedPrincipal principal = managePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (Long stockId : ids) {
                    if (stockId == null || stockId.longValue() < 0L) throw new IllegalArgumentException("stock id is invalid");
                    StockSnapshot stock = scopedStock(connection, stockId.longValue(), organizations);
                    if (!id.equals(stock.materialId)) throw denied();
                }
                for (Long stockId : ids) {
                    try (PreparedStatement logs = connection.prepareStatement("DELETE FROM T_EMC_STOCK_LOG WHERE STOCKID=?");
                         PreparedStatement stock = connection.prepareStatement("DELETE FROM T_EMC_STOCK WHERE ID=?")) {
                        logs.setLong(1, stockId.longValue()); logs.executeUpdate();
                        stock.setLong(1, stockId.longValue());
                        if (stock.executeUpdate() != 1) throw denied();
                    }
                }
                updateMaterialAggregate(connection, id);
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-stock-delete", principal.getId(), id,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) {
            throw new IllegalStateException("mdm stock delete failed", failure);
        }
    }

    /** Source-owned concentration limit calculation for stock quality records. */
    @PutMapping("/stockConcent/calculate")
    public void calculateConcentration(RestJsonWrapperBean wrapper) {
        requireWrite();
        if (wrapper == null) throw new IllegalArgumentException("concentration body is required");
        List<Map<String, Object>> rows = concentrationRows(wrapper);
        if (rows.isEmpty() || rows.size() > 100) throw new IllegalArgumentException("concentration body is required");
        try {
            OwnedPrincipal principal = managePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (Map<String, Object> row : rows) {
                    Long rowId = longValue(row.get("id"), "concentration id");
                    Long stockId = longValue(row.get("stockId"), "stock id");
                    if (rowId == null || stockId == null || stockId < 0L)
                        throw new IllegalArgumentException("concentration identity is invalid");
                    scopedStock(connection, stockId.longValue(), organizations);
                    String stdValue = stringValue(row.get("stdValue"));
                    String uncertainty = stringValue(row.get("uncertainty"));
                    String uncertaintyPercent = stringValue(row.get("uncertaintyPercent"));
                    String revisionRule = stringValue(row.get("revisionRule"));
                    String low = calculateLimit(stdValue, uncertainty, uncertaintyPercent, revisionRule, false);
                    String high = calculateLimit(stdValue, uncertainty, uncertaintyPercent, revisionRule, true);
                    try (PreparedStatement update = connection.prepareStatement(
                            "UPDATE T_EMC_STOCK_CONCENT SET LOWLIMIT=?,HIGHLIMIT=? WHERE ID=? AND STOCKID=?")) {
                        if (low == null) update.setNull(1, java.sql.Types.VARCHAR); else update.setString(1, low);
                        if (high == null) update.setNull(2, java.sql.Types.VARCHAR); else update.setString(2, high);
                        update.setLong(3, rowId.longValue()); update.setLong(4, stockId.longValue());
                        if (update.executeUpdate() != 1) throw denied();
                    }
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-stock-concentration-calculate", principal.getId(),
                    "stock-concentration", AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) {
            throw new IllegalStateException("mdm stock concentration calculation failed", failure);
        }
    }

    /** Source-owned equivalent of the original stock receive/withdraw route. */
    @PutMapping("/stocks/{stockId}/action/recieve")
    public void receive(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (stockId == null || stockId.longValue() < 0L || wrapper == null)
            throw new IllegalArgumentException("stock and receive body are required");
        List<EmcStockLogBean> logs = wrapper.parse(EmcStockLogBean.class);
        if (logs.isEmpty() || logs.size() > 100) throw new IllegalArgumentException("receive body is required");
        try {
            OwnedPrincipal principal = managePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            String materialId = OwnedWriteSupport.transaction(dataSource, connection -> {
                String scopedMaterialId = scopedStock(connection, stockId.longValue(), organizations).materialId;
                for (EmcStockLogBean log : logs) {
                    if (log == null || log.getQty() == null || log.getQty().doubleValue() <= 0d
                            || log.getQty().doubleValue() > 1000000000d)
                        throw new IllegalArgumentException("receive quantity is invalid");
                    StockSnapshot stock = scopedStock(connection, stockId.longValue(), organizations);
                    double current = stock.quantity - log.getQty().doubleValue();
                    if (current < 0d) throw new IllegalArgumentException("receive quantity exceeds stock");
                    long logId = OwnedWriteSupport.nextIdentity(connection);
                    try (PreparedStatement update = connection.prepareStatement(
                            "UPDATE T_EMC_STOCK SET STOCKQTY=? WHERE ID=?");
                         PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO T_EMC_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                        update.setDouble(1, current); update.setLong(2, stockId.longValue());
                        if (update.executeUpdate() != 1) throw denied();
                        insert.setLong(1, logId); insert.setLong(2, stockId.longValue()); insert.setString(3, "领取");
                        insert.setDouble(4, stock.quantity); insert.setDouble(5, current); insert.setString(6, log.getVendorId());
                        if (log.getPrice() == null) insert.setNull(7, java.sql.Types.NUMERIC); else insert.setDouble(7, log.getPrice());
                        insert.setString(8, log.getLogContent()); insert.setString(9, log.getReceivedById());
                        insert.setString(10, log.getReceivedByOrgId()); insert.setString(11, principal.getId());
                        insert.setString(12, principal.getId()); insert.setString(13, principal.getOrgId());
                        insert.setString(14, principal.getOrgName()); insert.executeUpdate();
                    }
                }
                updateMaterialAggregate(connection, scopedMaterialId);
                return scopedMaterialId;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-stock-receive", principal.getId(),
                    String.valueOf(stockId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) {
            throw new IllegalStateException("mdm stock receive failed", failure);
        }
    }

    /** Source-owned uniqueness check used by the original stock editor. */
    @PostMapping("/stocks/{id}/action/validate-unique/{columnName}/{columnValue}")
    public com.sunwayworld.framework.restful.data.RestValidationResultBean validateUnique(
            @PathVariable Long id, @PathVariable String columnName, @PathVariable String columnValue) {
        if (id == null || id.longValue() < 0L || columnName == null || columnValue == null)
            throw new IllegalArgumentException("stock uniqueness input is invalid");
        String column = uniqueColumn(columnName);
        try {
            OwnedPrincipal principal = readPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            try (Connection connection = dataSource.getConnection()) {
                scopedStock(connection, id.longValue(), organizations);
            }
            List<Object> values = new ArrayList<Object>();
            String scope = scope("M.ORGID", organizations, values);
            values.add(columnValue); values.add(id);
            String sql = "SELECT COUNT(1) FROM T_EMC_STOCK S JOIN T_EMC_MTL M ON M.ID=S.MTLID WHERE "
                    + scope + " AND S." + column + "=? AND S.ID<>?";
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
                bind(query, values);
                try (ResultSet rows = query.executeQuery()) {
                    rows.next();
                    return rows.getInt(1) == 0
                            ? new com.sunwayworld.framework.restful.data.RestValidationResultBean(Boolean.TRUE)
                            : new com.sunwayworld.framework.restful.data.RestValidationResultBean(Boolean.FALSE, "core.validator.column.not-unique");
                }
            }
        } catch (SQLException failure) {
            throw new IllegalStateException("mdm stock uniqueness query failed", failure);
        }
    }

    @RequestMapping(value = "/{id}")
    public EmcMtlBean materialDetail(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("material id is invalid");
        try {
            OwnedPrincipal principal = readPrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            StringBuilder sql = new StringBuilder("SELECT M.* FROM T_EMC_MTL M WHERE M.ID=? AND ");
            List<Object> values = new ArrayList<Object>(); values.add(id); sql.append(scope("M.ORGID", organizations, values));
            try (Connection connection=dataSource.getConnection(); PreparedStatement query=connection.prepareStatement(sql.toString())) { bind(query,values); try(ResultSet rows=query.executeQuery()){return rows.next()?material(rows):null;} }
        } catch (SQLException failure) { throw new IllegalStateException("mdm material detail query failed", failure); }
    }

    @PostMapping("/stocks/{stockId}/logs/queries")
    public Page<EmcStockLogBean> logs(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        if (stockId == null || stockId.longValue() < 0L) throw new IllegalArgumentException("stock id is invalid");
        try {
            OwnedPrincipal principal = readPrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(), wrapper == null ? null : wrapper.getParamMap());
            List<Object> values = new ArrayList<Object>();
            String scope = scope("M.ORGID", organizations, values); values.add(0, stockId);
            String base = "SELECT L.*,U.USERNAME AS USERNAME,O.ORGNAME AS ORGNAME FROM T_EMC_STOCK_LOG L JOIN T_EMC_STOCK S ON S.ID=L.STOCKID JOIN T_EMC_MTL M ON M.ID=S.MTLID LEFT JOIN T_CORE_USER U ON U.ID=L.RECEIVEDBYID LEFT JOIN T_CORE_ORG O ON O.ID=L.RECEIVEDBYORGID WHERE L.STOCKID=? AND " + scope;
            return pageLogs(base, values, request);
        } catch (SQLException failure) { throw new IllegalStateException("mdm stock-log query failed", failure); }
    }

    /** Original concentration query by stock, with the material organization boundary. */
    @PostMapping("/stocks/{stockId}/concents/queries")
    public Page<Map<String, Object>> concentrationsByStock(@PathVariable Long stockId, RestJsonWrapperBean wrapper) {
        if (stockId == null || stockId.longValue() < 0L) throw new IllegalArgumentException("stock id is invalid");
        return concentrationPage(stockId, wrapper);
    }

    /** Original all-stock concentration chooser query. */
    @PostMapping("/stocks/concents/queries")
    public Page<Map<String, Object>> concentrations(RestJsonWrapperBean wrapper) { return concentrationPage(null, wrapper); }

    /** Original QC standard-stock chooser, with owned active-stock and receive filters. */
    @PostMapping("/queries/qc-std-stock")
    public Page<Map<String, Object>> qcStandardStocks(RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = readPrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (wrapper != null && wrapper.getParamValue("runId") != null && !wrapper.getParamValue("runId").trim().isEmpty())
                throw new IllegalArgumentException("qc run filter is not available in the owned isolated schema");
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(), wrapper == null ? null : wrapper.getParamMap());
            List<Object> values = new ArrayList<Object>();
            String base = "SELECT A.*,B.ORGID,B.MTLNAME,C.MANAGEMENTTYPE,D.CATEGORY AS CONCENTCATEGORY,D.STDVALUE,D.UNCERTAINTY,D.UNCERTAINTYPERCENT,D.CONCENTUNITNAME FROM T_EMC_STOCK A JOIN T_EMC_MTL B ON B.ID=A.MTLID LEFT JOIN T_EMC_MTL_CATEGORY C ON C.ID=B.MTLCATEGORYID LEFT JOIN T_EMC_STOCK_CONCENT D ON D.STOCKID=A.ID WHERE A.STATUS='0' AND " + scope("B.ORGID", organizations, values);
            base = appendQcFilters(request, base, values);
            if (wrapper != null && "1".equals(wrapper.getParamValue("isReceive"))) { base += " AND EXISTS (SELECT 1 FROM T_EMC_STOCK_LOG F WHERE F.STOCKID=A.ID AND F.LOGTYPE='领取' AND F.RECEIVEDBYID=?)"; values.add(principal.getId()); }
            return pageQc(base, values, request);
        } catch (SQLException failure) { throw new IllegalStateException("mdm qc standard-stock query failed", failure); }
    }

    /** Original material-maintenance query, scoped through the parent material. */
    @PostMapping("/{id}/maintains/queries")
    public Page<Map<String, Object>> maintains(@PathVariable String id, RestJsonWrapperBean wrapper) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("material id is invalid");
        return maintainPage(id, wrapper);
    }

    /** Original maintenance-analysis-item query, scoped through its material parent. */
    @PostMapping("/maintains/{maintainId}/maintain-items/queries")
    public Page<Map<String, Object>> maintainItems(@PathVariable Long maintainId, RestJsonWrapperBean wrapper) {
        if (maintainId == null || maintainId.longValue() < 0L) throw new IllegalArgumentException("maintenance id is invalid");
        return maintainItemPage(maintainId, wrapper);
    }

    /** Source-owned maintenance insert with the original material-name and concentration initialization seam. */
    @PostMapping("/{id}/maintains")
    public Long insertMaintain(@PathVariable String id, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (id == null || id.trim().isEmpty() || wrapper == null) throw new IllegalArgumentException("material and maintenance body are required");
        List<Map<String, Object>> rows = concentrationRows(wrapper); if (rows.size() != 1) throw new IllegalArgumentException("maintenance body is required");
        Map<String, Object> row = rows.get(0);
        try {
            OwnedPrincipal principal = managePrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long result = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireMaterialScope(connection, id, organizations);
                String materialName = null;
                try (PreparedStatement find = connection.prepareStatement("SELECT MTLNAME FROM T_EMC_MTL WHERE ID=?")) { find.setString(1, id); try (ResultSet resultSet = find.executeQuery()) { if (resultSet.next()) materialName = resultSet.getString(1); } }
                long maintainId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_MTL_MAINTAIN (ID,MTLID,CATEGORY,MTLNAME,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    insert.setLong(1, maintainId); insert.setString(2, id); insert.setString(3, stringValue(row.get("category"))); insert.setString(4, materialName);
                    insert.setString(5, principal.getId()); insert.setString(6, principal.getId()); insert.setString(7, principal.getOrgId()); insert.setString(8, principal.getOrgName()); insert.executeUpdate();
                }
                initializeConcentrations(connection, id, maintainId, stringValue(row.get("category")), principal);
                return Long.valueOf(maintainId);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-maintenance-insert", principal.getId(), String.valueOf(result), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return result;
        } catch (SQLException failure) { throw new IllegalStateException("mdm maintenance insert failed", failure); }
    }

    /** Source-owned maintenance-item insert with parent material scope. */
    @PostMapping("/maintains/{maintainId}/maintain-items")
    public Long insertMaintainItems(@PathVariable Long maintainId, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (maintainId == null || maintainId.longValue() < 0L || wrapper == null) throw new IllegalArgumentException("maintenance and item body are required");
        List<Map<String, Object>> rows = concentrationRows(wrapper); if (rows.isEmpty() || rows.size() > 100) throw new IllegalArgumentException("maintenance item body is required");
        try {
            OwnedPrincipal principal = managePrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                maintainMaterialId(connection, maintainId.longValue(), organizations);
                for (Map<String, Object> row : rows) {
                    long itemId = OwnedWriteSupport.nextIdentity(connection);
                    try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_MTL_MAINTAIN_ITEM (ID,MTLMAITAINID,ITEMID,TESTMETHODID,METHODID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                        insert.setLong(1, itemId); insert.setLong(2, maintainId.longValue()); setNullableLong(insert, 3, longValue(row.get("itemId"), "item id")); setNullableLong(insert, 4, longValue(row.get("testMethodId"), "test method id")); setNullableLong(insert, 5, longValue(row.get("methodId"), "method id"));
                        insert.setString(6, principal.getId()); insert.setString(7, principal.getId()); insert.setString(8, principal.getOrgId()); insert.setString(9, principal.getOrgName()); insert.executeUpdate();
                    }
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-maintenance-item-insert", principal.getId(), String.valueOf(maintainId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return maintainId;
        } catch (SQLException failure) { throw new IllegalStateException("mdm maintenance-item insert failed", failure); }
    }

    /** Source-owned maintenance delete; dependent items and concentration rows are removed atomically. */
    @DeleteMapping("/{id}/maintains")
    public void deleteMaintains(@PathVariable String id, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (id == null || id.trim().isEmpty() || wrapper == null) throw new IllegalArgumentException("material and maintenance delete body are required");
        List<Long> ids = wrapper.parseId(Long.class); if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("maintenance ids are invalid");
        try {
            OwnedPrincipal principal = managePrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (Long maintainId : ids) { if (maintainId == null || maintainId < 0L || !id.equals(maintainMaterialId(connection, maintainId.longValue(), organizations))) throw denied(); }
                for (Long maintainId : ids) {
                    try (PreparedStatement concentrations = connection.prepareStatement("DELETE FROM T_EMC_STOCK_CONCENT WHERE MTLMAINTAINID=?"); PreparedStatement items = connection.prepareStatement("DELETE FROM T_EMC_MTL_MAINTAIN_ITEM WHERE MTLMAITAINID=?"); PreparedStatement maintain = connection.prepareStatement("DELETE FROM T_EMC_MTL_MAINTAIN WHERE ID=?")) {
                        concentrations.setLong(1, maintainId.longValue()); concentrations.executeUpdate(); items.setLong(1, maintainId.longValue()); items.executeUpdate(); maintain.setLong(1, maintainId.longValue()); if (maintain.executeUpdate() != 1) throw denied();
                    }
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-maintenance-delete", principal.getId(), id, AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("mdm maintenance delete failed", failure); }
    }

    /** Source-owned maintenance-item delete with parent ownership validation. */
    @DeleteMapping("/maintains/{maintainId}/maintain-items")
    public void deleteMaintainItems(@PathVariable Long maintainId, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (maintainId == null || maintainId < 0L || wrapper == null) throw new IllegalArgumentException("maintenance and item delete body are required");
        List<Long> ids = wrapper.parseId(Long.class); if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("maintenance item ids are invalid");
        try {
            OwnedPrincipal principal = managePrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                maintainMaterialId(connection, maintainId.longValue(), organizations);
                for (Long itemId : ids) {
                    if (itemId == null || itemId < 0L) throw new IllegalArgumentException("maintenance item id is invalid");
                    try (PreparedStatement check = connection.prepareStatement("SELECT MTLMAITAINID FROM T_EMC_MTL_MAINTAIN_ITEM WHERE ID=?")) {
                        check.setLong(1, itemId.longValue()); try (ResultSet rows = check.executeQuery()) { if (!rows.next() || rows.getLong(1) != maintainId.longValue()) throw denied(); }
                    }
                }
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_MTL_MAINTAIN_ITEM WHERE ID=?")) { for (Long itemId : ids) { delete.setLong(1, itemId.longValue()); if (delete.executeUpdate() != 1) throw denied(); } }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "mdm-maintenance-item-delete", principal.getId(), String.valueOf(maintainId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("mdm maintenance-item delete failed", failure); }
    }

    private Page<EmcStockBean> stockPage(String materialId, RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = readPrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(), wrapper == null ? null : wrapper.getParamMap());
            List<Object> values = new ArrayList<Object>(); String scope = scope("M.ORGID", organizations, values);
            String base = "SELECT S.* FROM T_EMC_STOCK S JOIN T_EMC_MTL M ON M.ID=S.MTLID WHERE " + (materialId == null ? "" : "S.MTLID=? AND ") + scope;
            if (materialId != null) values.add(0, materialId);
            base = appendFilters(request, base, values);
            return pageStocks(base, values, request);
        } catch (SQLException failure) { throw new IllegalStateException("mdm stock query failed", failure); }
    }

    private Page<Map<String, Object>> concentrationPage(Long stockId, RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = readPrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(), wrapper == null ? null : wrapper.getParamMap());
            List<Object> values = new ArrayList<Object>();
            String base = "SELECT C.* FROM T_EMC_STOCK_CONCENT C JOIN T_EMC_STOCK S ON S.ID=C.STOCKID JOIN T_EMC_MTL M ON M.ID=S.MTLID WHERE ";
            if (stockId != null) { base += "S.ID=? AND "; values.add(stockId); }
            base += scope("M.ORGID", organizations, values);
            base = appendConcentrationFilters(request, base, values);
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total = count(connection, base, values); List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(from); pageValues.add(to);
                String sql = "SELECT * FROM (SELECT C.*,ROW_NUMBER() OVER (ORDER BY C.ID DESC) RN FROM (" + base + ") C) WHERE RN BETWEEN ? AND ? ORDER BY RN";
                try (PreparedStatement query = connection.prepareStatement(sql)) { bind(query, pageValues); try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(concentration(result)); } }
                return new Pagination<Map<String, Object>>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (SQLException failure) { throw new IllegalStateException("mdm stock concentration query failed", failure); }
    }

    private Page<Map<String, Object>> pageQc(String base, List<Object> values, QueryRequest request) throws SQLException {
        int from = (request.getPageNumber() - 1) * request.getPageSize() + 1; int to = request.getPageNumber() * request.getPageSize();
        try (Connection connection = dataSource.getConnection()) {
            int total = count(connection, base, values); List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(); List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(from); pageValues.add(to);
            String sql = "SELECT * FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.MTLID,Q.BATCHNO,Q.ID DESC) RN FROM (" + base + ") Q) WHERE RN BETWEEN ? AND ? ORDER BY RN";
            try (PreparedStatement query = connection.prepareStatement(sql)) { bind(query, pageValues); try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(qcStock(result)); } }
            return new Pagination<Map<String, Object>>(total, request.getPageSize(), request.getPageNumber(), rows);
        }
    }

    private Page<Map<String, Object>> maintainPage(String materialId, RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = readPrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(), wrapper == null ? null : wrapper.getParamMap());
            List<Object> values = new ArrayList<Object>();
            String base = "SELECT X.* FROM T_EMC_MTL_MAINTAIN X JOIN T_EMC_MTL M ON M.ID=X.MTLID WHERE X.MTLID=? AND " + scope("M.ORGID", organizations, values);
            values.add(0, materialId);
            return pageMaps(base, values, request, false);
        } catch (SQLException failure) { throw new IllegalStateException("mdm material maintenance query failed", failure); }
    }

    private Page<Map<String, Object>> maintainItemPage(Long maintainId, RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = readPrincipal(); Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(), wrapper == null ? null : wrapper.getParamMap());
            List<Object> values = new ArrayList<Object>();
            String base = "SELECT X.* FROM T_EMC_MTL_MAINTAIN_ITEM X JOIN T_EMC_MTL_MAINTAIN P ON P.ID=X.MTLMAITAINID JOIN T_EMC_MTL M ON M.ID=P.MTLID WHERE P.ID=? AND " + scope("M.ORGID", organizations, values);
            values.add(0, maintainId);
            return pageMaps(base, values, request, true);
        } catch (SQLException failure) { throw new IllegalStateException("mdm material maintenance-item query failed", failure); }
    }

    private Page<Map<String, Object>> pageMaps(String base, List<Object> values, QueryRequest request, boolean item) throws SQLException {
        if (!request.getFilterPatterns().isEmpty()) throw new IllegalArgumentException("unsupported maintenance query filter");
        int from = (request.getPageNumber() - 1) * request.getPageSize() + 1; int to = request.getPageNumber() * request.getPageSize();
        try (Connection connection = dataSource.getConnection()) {
            int total = count(connection, base, values); List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(from); pageValues.add(to);
            String sql = "SELECT * FROM (SELECT X.*,ROW_NUMBER() OVER (ORDER BY X.ID DESC) RN FROM (" + base + ") X) WHERE RN BETWEEN ? AND ? ORDER BY RN";
            try (PreparedStatement query = connection.prepareStatement(sql)) { bind(query, pageValues); try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(item ? maintenanceItem(result) : maintenance(result)); } }
            return new Pagination<Map<String, Object>>(total, request.getPageSize(), request.getPageNumber(), rows);
        }
    }

    private Page<EmcMtlBean> materialPage(RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal=readPrincipal(); Set<String> organizations=provider.permittedOrganizationIds(principal);
            QueryRequest request=QueryRequest.parse(wrapper==null?null:wrapper.getRequestJson(),wrapper==null?null:wrapper.getParamMap());
            List<Object> values=new ArrayList<Object>(); String base="SELECT M.* FROM T_EMC_MTL M WHERE "+scope("M.ORGID",organizations,values);
            return pageMaterials(base,values,request);
        } catch(SQLException failure){throw new IllegalStateException("mdm material query failed",failure);}
    }

    private Page<EmcMtlBean> pageMaterials(String base,List<Object> values,QueryRequest request)throws SQLException{
        int from=(request.getPageNumber()-1)*request.getPageSize()+1,to=request.getPageNumber()*request.getPageSize();
        try(Connection connection=dataSource.getConnection()){
            int total=count(connection,base,values);List<EmcMtlBean> rows=new ArrayList<EmcMtlBean>();List<Object> pageValues=new ArrayList<Object>(values);pageValues.add(from);pageValues.add(to);
            String sql="SELECT * FROM (SELECT M.*,ROW_NUMBER() OVER (ORDER BY M.ID DESC) RN FROM ("+base+") M) WHERE RN BETWEEN ? AND ? ORDER BY RN";
            try(PreparedStatement query=connection.prepareStatement(sql)){bind(query,pageValues);try(ResultSet result=query.executeQuery()){while(result.next())rows.add(material(result));}}
            return new Pagination<EmcMtlBean>(total,request.getPageSize(),request.getPageNumber(),rows);
        }
    }

    private Page<EmcStockBean> pageStocks(String base, List<Object> values, QueryRequest request) throws SQLException {
        int from = (request.getPageNumber() - 1) * request.getPageSize() + 1;
        int to = request.getPageNumber() * request.getPageSize();
        try (Connection connection = dataSource.getConnection()) {
            int total = count(connection, base, values);
            List<EmcStockBean> rows = new ArrayList<EmcStockBean>(); List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(from); pageValues.add(to);
            String sql = "SELECT * FROM (SELECT S.*,ROW_NUMBER() OVER (ORDER BY S.ID DESC) RN FROM (" + base + ") S) WHERE RN BETWEEN ? AND ? ORDER BY RN";
            try (PreparedStatement query = connection.prepareStatement(sql)) { bind(query, pageValues); try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(stock(result)); } }
            return new Pagination<EmcStockBean>(total, request.getPageSize(), request.getPageNumber(), rows);
        }
    }

    private Page<EmcStockLogBean> pageLogs(String base, List<Object> values, QueryRequest request) throws SQLException {
        int from = (request.getPageNumber() - 1) * request.getPageSize() + 1; int to = request.getPageNumber() * request.getPageSize();
        try (Connection connection = dataSource.getConnection()) {
            int total = count(connection, base, values); List<EmcStockLogBean> rows = new ArrayList<EmcStockLogBean>(); List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(from); pageValues.add(to);
            String sql = "SELECT * FROM (SELECT S.*,ROW_NUMBER() OVER (ORDER BY S.ID DESC) RN FROM (" + base + ") S) WHERE RN BETWEEN ? AND ? ORDER BY RN";
            try (PreparedStatement query = connection.prepareStatement(sql)) { bind(query, pageValues); try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(log(result)); } }
            return new Pagination<EmcStockLogBean>(total, request.getPageSize(), request.getPageNumber(), rows);
        }
    }

    private static int count(Connection connection, String base, List<Object> values) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT COUNT(1) FROM (" + base + ") X")) { bind(query, values); try (ResultSet rows = query.executeQuery()) { rows.next(); return rows.getInt(1); } }
    }

    private static String appendFilters(QueryRequest request, String base, List<Object> values) {
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern != MatchPattern.EQ && pattern != MatchPattern.SEQ && pattern != MatchPattern.CISC && pattern != MatchPattern.NG && pattern != MatchPattern.NGOE && pattern != MatchPattern.NL && pattern != MatchPattern.NLOE)
                throw new IllegalArgumentException("unsupported mdm stock filter pattern");
            for (java.util.Map.Entry<String,Object> entry : filter.getMap().entrySet()) {
                String column = column(entry.getKey()); Object value = entry.getValue();
                if (pattern == MatchPattern.EQ || pattern == MatchPattern.SEQ) { base += " AND S." + column + "=?"; values.add(value); }
                else if (pattern == MatchPattern.CISC) { base += " AND UPPER(S." + column + ") LIKE ?"; values.add("%" + String.valueOf(value).toUpperCase(java.util.Locale.ROOT) + "%"); }
                else { base += " AND S." + column + (pattern == MatchPattern.NG ? ">" : pattern == MatchPattern.NGOE ? ">=" : pattern == MatchPattern.NL ? "<" : "<=") + "?"; values.add(value); }
            }
        }
        return base;
    }

    private static String appendConcentrationFilters(QueryRequest request, String base, List<Object> values) {
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern != MatchPattern.EQ && pattern != MatchPattern.SEQ && pattern != MatchPattern.CISC)
                throw new IllegalArgumentException("unsupported mdm concentration filter pattern");
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) {
                String column = concentrationColumn(entry.getKey()); Object value = entry.getValue();
                if (pattern == MatchPattern.CISC) { base += " AND UPPER(C." + column + ") LIKE ?"; values.add("%" + String.valueOf(value).toUpperCase(java.util.Locale.ROOT) + "%"); }
                else { base += " AND C." + column + "=?"; values.add(value); }
            }
        }
        return base;
    }
    private static String appendQcFilters(QueryRequest request, String base, List<Object> values) {
        for (FilterParamPattern filter : request.getFilterPatterns()) {
            MatchPattern pattern = MatchPattern.getPatternIfPossible(filter.getMatchPattern());
            if (pattern != MatchPattern.EQ && pattern != MatchPattern.SEQ && pattern != MatchPattern.CISC) throw new IllegalArgumentException("unsupported qc stock filter pattern");
            for (Map.Entry<String, Object> entry : filter.getMap().entrySet()) {
                String key = entry.getKey() == null ? "" : entry.getKey().toUpperCase(java.util.Locale.ROOT); Object value = entry.getValue();
                String column;
                if ("BATCHNO".equals(key) || "MTLID".equals(key) || "STATUS".equals(key)) column = "A." + key;
                else if ("MTLNAME".equals(key)) column = "B.MTLNAME";
                else throw new IllegalArgumentException("unsupported qc stock filter column: " + entry.getKey());
                if (pattern == MatchPattern.CISC) { base += " AND UPPER(" + column + ") LIKE ?"; values.add("%" + String.valueOf(value).toUpperCase(java.util.Locale.ROOT) + "%"); }
                else { base += " AND " + column + "=?"; values.add(value); }
            }
        }
        return base;
    }

    private static String column(String key) { String value = key == null ? "" : key.toUpperCase(java.util.Locale.ROOT); if ("ID".equals(value)||"MTLID".equals(value)||"BATCHNO".equals(value)||"STOCKQTY".equals(value)||"UNITNAME".equals(value)||"STATUS".equals(value)||"LOWLIMIT".equals(value)) return value; throw new IllegalArgumentException("unsupported mdm stock filter column: " + key); }
    private static String concentrationColumn(String key) { String value = key == null ? "" : key.toUpperCase(java.util.Locale.ROOT); if ("ID".equals(value)||"STOCKID".equals(value)||"CATEGORY".equals(value)||"STDVALUE".equals(value)||"LOWLIMIT".equals(value)||"HIGHLIMIT".equals(value)||"MTLMAINTAINID".equals(value)) return value; throw new IllegalArgumentException("unsupported mdm concentration filter column: " + key); }
    private static String scope(String column, Set<String> organizations, List<Object> values) { if (organizations == null || organizations.isEmpty()) return "1=0"; StringBuilder sql = new StringBuilder(column + " IN ("); int i=0; for(String org:organizations){if(i++>0)sql.append(',');sql.append('?');values.add(org);} return sql.append(')').toString(); }
    private static void bind(PreparedStatement query, List<Object> values) throws SQLException { int i=1; for(Object value:values) query.setObject(i++,value); }
    private OwnedPrincipal readPrincipal() throws SQLException { RequestAttributes attributes=RequestContextHolder.getRequestAttributes(); if(!(attributes instanceof ServletRequestAttributes)) throw denied(); HttpSession session=((ServletRequestAttributes)attributes).getRequest().getSession(false); boundary.requirePermission(session,OwnedIdentityProvider.CONSUMABLE_READ); return boundary.currentPrincipal(session); }
    private OwnedPrincipal managePrincipal() throws SQLException { RequestAttributes attributes=RequestContextHolder.getRequestAttributes(); if(!(attributes instanceof ServletRequestAttributes)) throw denied(); HttpSession session=((ServletRequestAttributes)attributes).getRequest().getSession(false); boundary.requirePermission(session,OwnedIdentityProvider.CONSUMABLE_MANAGE); return boundary.currentPrincipal(session); }
    private void requireWrite() { if(environment == null || !"isolated".equals(environment.getProperty("owned.consumables.write.mode"))) throw denied(); OwnedWriteSupport.requireLoopback(currentRequest()); }
    private static javax.servlet.http.HttpServletRequest currentRequest() { RequestAttributes attributes=RequestContextHolder.getRequestAttributes(); if(!(attributes instanceof ServletRequestAttributes)) throw denied(); return ((ServletRequestAttributes)attributes).getRequest(); }
    private static void requireMaterialScope(Connection connection,String materialId,Set<String> organizations)throws SQLException { if(organizations==null||organizations.isEmpty())throw denied(); StringBuilder sql=new StringBuilder("SELECT 1 FROM T_EMC_MTL M WHERE M.ID=? AND M.ORGID IN ("); for(int i=0;i<organizations.size();i++){if(i>0)sql.append(',');sql.append('?');}sql.append(')'); try(PreparedStatement query=connection.prepareStatement(sql.toString())){int index=1;query.setString(index++,materialId);for(String org:organizations)query.setString(index++,org);try(ResultSet rows=query.executeQuery()){if(!rows.next())throw denied();}} }
    private static StockSnapshot scopedStock(Connection connection,long stockId,Set<String> organizations)throws SQLException{StringBuilder sql=new StringBuilder("SELECT S.MTLID,S.STOCKQTY FROM T_EMC_STOCK S JOIN T_EMC_MTL M ON M.ID=S.MTLID WHERE S.ID=? AND M.ORGID IN (");for(int i=0;i<organizations.size();i++){if(i>0)sql.append(',');sql.append('?');}sql.append(')');try(PreparedStatement q=connection.prepareStatement(sql.toString())){int index=1;q.setLong(index++,stockId);for(String org:organizations)q.setString(index++,org);try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();return new StockSnapshot(r.getString(1),r.getDouble(2));}}}
    private static String uniqueColumn(String raw){String value=raw.trim().toUpperCase(java.util.Locale.ROOT);if("BATCHNO".equals(value)||"UNITNAME".equals(value)||"VERDORNAME".equals(value)||"FINENESS".equals(value))return value;throw new IllegalArgumentException("unsupported stock uniqueness column: "+raw);}
    private static List<Map<String, Object>> concentrationRows(RestJsonWrapperBean wrapper) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (List<String> group : wrapper.getBodyList()) for (String value : group) {
            Map<String, Object> row = JSON.parseObject(value, Map.class);
            if (row == null) throw new IllegalArgumentException("invalid concentration record");
            rows.add(row);
        }
        return rows;
    }
    private static Long longValue(Object value, String label) {
        if (value == null) return null;
        try { return Long.valueOf(String.valueOf(value)); } catch (NumberFormatException failure) { throw new IllegalArgumentException(label + " is invalid"); }
    }
    private static String stringValue(Object value) { return value == null ? null : String.valueOf(value); }
    private static Map<String, Object> concentration(ResultSet rows) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        value.put("id", rows.getLong("ID")); value.put("stockId", rows.getLong("STOCKID")); value.put("category", rows.getString("CATEGORY"));
        value.put("stdValue", rows.getString("STDVALUE")); value.put("uncertainty", rows.getString("UNCERTAINTY"));
        value.put("uncertaintyPercent", rows.getString("UNCERTAINTYPERCENT")); value.put("concentUnitName", rows.getString("CONCENTUNITNAME"));
        value.put("remark", rows.getString("REMARK")); value.put("lowLimit", rows.getString("LOWLIMIT")); value.put("highLimit", rows.getString("HIGHLIMIT"));
        value.put("mtlMaintainId", rows.getLong("MTLMAINTAINID")); value.put("createdById", rows.getString("CREATEDBYID"));
        value.put("createdByName", rows.getString("CREATEDBYNAME")); Timestamp created = rows.getTimestamp("CREATEDTIME");
        if (created != null) value.put("createdTime", created.toLocalDateTime()); value.put("createdByOrgId", rows.getString("CREATEDBYORGID"));
        value.put("createdByOrgName", rows.getString("CREATEDBYORGNAME")); value.put("revisionRule", rows.getString("REVISIONRULE"));
        return value;
    }
    private static Map<String, Object> qcStock(ResultSet rows) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>(); value.put("id", rows.getLong("ID")); value.put("mtlId", rows.getString("MTLID")); value.put("batchNo", rows.getString("BATCHNO")); value.put("stockQty", rows.getDouble("STOCKQTY")); value.put("unitName", rows.getString("UNITNAME")); value.put("status", rows.getString("STATUS")); value.put("orgId", rows.getString("ORGID")); value.put("mtlName", rows.getString("MTLNAME")); value.put("managementType", rows.getString("MANAGEMENTTYPE")); value.put("category", rows.getString("CONCENTCATEGORY")); value.put("stdValue", rows.getString("STDVALUE")); value.put("uncertainty", rows.getString("UNCERTAINTY")); value.put("uncertaintyPercent", rows.getString("UNCERTAINTYPERCENT")); value.put("concentUnitName", rows.getString("CONCENTUNITNAME")); return value;
    }
    private static Map<String, Object> maintenance(ResultSet rows) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>(); value.put("id", rows.getLong("ID")); value.put("mtlId", rows.getString("MTLID"));
        value.put("category", rows.getString("CATEGORY")); value.put("mtlName", rows.getString("MTLNAME")); value.put("createdById", rows.getString("CREATEDBYID"));
        value.put("createdByName", rows.getString("CREATEDBYNAME")); Timestamp created = rows.getTimestamp("CREATEDTIME"); if (created != null) value.put("createdTime", created.toLocalDateTime());
        value.put("createdByOrgId", rows.getString("CREATEDBYORGID")); value.put("createdByOrgName", rows.getString("CREATEDBYORGNAME")); return value;
    }
    private static Map<String, Object> maintenanceItem(ResultSet rows) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>(); value.put("id", rows.getLong("ID")); value.put("mtlMaitainId", rows.getLong("MTLMAITAINID"));
        value.put("itemId", rows.getLong("ITEMID")); value.put("testMethodId", rows.getLong("TESTMETHODID")); value.put("methodId", rows.getLong("METHODID"));
        value.put("createdById", rows.getString("CREATEDBYID")); value.put("createdByName", rows.getString("CREATEDBYNAME")); Timestamp created = rows.getTimestamp("CREATEDTIME");
        if (created != null) value.put("createdTime", created.toLocalDateTime()); value.put("createdByOrgId", rows.getString("CREATEDBYORGID")); value.put("createdByOrgName", rows.getString("CREATEDBYORGNAME")); return value;
    }
    private static void initializeConcentrations(Connection connection, String materialId, long maintainId, String category, OwnedPrincipal principal) throws SQLException {
        try (PreparedStatement stocks = connection.prepareStatement("SELECT ID FROM T_EMC_STOCK WHERE MTLID=?"); PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_STOCK_CONCENT (ID,STOCKID,CATEGORY,MTLMAINTAINID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
            stocks.setString(1, materialId); try (ResultSet rows = stocks.executeQuery()) { while (rows.next()) { insert.setLong(1, OwnedWriteSupport.nextIdentity(connection)); insert.setLong(2, rows.getLong(1)); insert.setString(3, category); insert.setLong(4, maintainId); insert.setString(5, principal.getId()); insert.setString(6, principal.getId()); insert.setString(7, principal.getOrgId()); insert.setString(8, principal.getOrgName()); insert.executeUpdate(); } }
        }
    }
    private static String maintainMaterialId(Connection connection, long maintainId, Set<String> organizations) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT P.MTLID FROM T_EMC_MTL_MAINTAIN P JOIN T_EMC_MTL M ON M.ID=P.MTLID WHERE P.ID=? AND M.ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); } sql.append(')');
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) { int index = 1; query.setLong(index++, maintainId); for (String org : organizations) query.setString(index++, org); try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); return rows.getString(1); } }
    }
    private static void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException { if (value == null) statement.setNull(index, java.sql.Types.NUMERIC); else statement.setLong(index, value.longValue()); }
    private static String calculateLimit(String stdValue, String uncertainty, String uncertaintyPercent, String revisionRule, boolean high) {
        if (stdValue == null || stdValue.trim().isEmpty()) return null;
        BigDecimal standard = decimal(stdValue, "standard value");
        BigDecimal deviation = uncertainty == null || uncertainty.trim().isEmpty() ? null : decimal(uncertainty, "uncertainty");
        if (deviation == null && uncertaintyPercent != null && !uncertaintyPercent.trim().isEmpty())
            deviation = standard.multiply(decimal(uncertaintyPercent, "uncertainty percent")).divide(BigDecimal.valueOf(100L));
        BigDecimal result = standard;
        if (deviation != null) result = high ? standard.add(deviation) : standard.subtract(deviation);
        int scale = roundingScale(revisionRule);
        if (scale >= 0) result = result.setScale(scale, RoundingMode.HALF_UP);
        return result.stripTrailingZeros().toPlainString();
    }
    private static BigDecimal decimal(String value, String label) { try { return new BigDecimal(value.trim()); } catch (NumberFormatException failure) { throw new IllegalArgumentException(label + " is invalid"); } }
    private static int roundingScale(String rule) {
        if (rule == null || rule.trim().isEmpty()) return -1;
        String[] parts = rule.split(","); String candidate = parts[parts.length - 1].trim();
        try { int scale = Integer.parseInt(candidate); return scale < 0 || scale > 12 ? -1 : scale; }
        catch (NumberFormatException ignored) { return -1; }
    }
    private static void updateMaterialAggregate(Connection connection,String materialId)throws SQLException{try(PreparedStatement q=connection.prepareStatement("UPDATE T_EMC_MTL SET STOCKQTY=(SELECT NVL(SUM(STOCKQTY),0) FROM T_EMC_STOCK WHERE MTLID=? AND STATUS='0') WHERE ID=?")){q.setString(1,materialId);q.setString(2,materialId);if(q.executeUpdate()!=1)throw denied();}}
    private static final class StockSnapshot{final String materialId;final double quantity;StockSnapshot(String materialId,double quantity){this.materialId=materialId;this.quantity=quantity;}}
    private static AccessDeniedException denied(){return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");}

    private static EmcStockBean stock(ResultSet rows) throws SQLException { EmcStockBean value=new EmcStockBean(); value.setId(rows.getLong("ID")); value.setMtlId(rows.getString("MTLID")); value.setBatchNo(rows.getString("BATCHNO")); value.setStockQty(rows.getDouble("STOCKQTY")); value.setUnitName(rows.getString("UNITNAME")); value.setStatus(rows.getString("STATUS")); value.setLowLimit(rows.getDouble("LOWLIMIT")); value.setCreatedById(rows.getString("CREATEDBYID")); value.setCreatedByName(rows.getString("CREATEDBYNAME")); Timestamp created=rows.getTimestamp("CREATEDTIME"); if(created!=null)value.setCreatedTime(created.toLocalDateTime()); Timestamp expire=rows.getTimestamp("EXPIREDATE"); if(expire!=null)value.setExpireDate(expire.toLocalDateTime()); return value; }
    private static EmcStockLogBean log(ResultSet rows) throws SQLException { EmcStockLogBean value=new EmcStockLogBean(); value.setId(rows.getLong("ID")); value.setStockId(rows.getLong("STOCKID")); value.setLogType(rows.getString("LOGTYPE")); value.setPreviousQty(rows.getDouble("PREVIOUSQTY")); value.setQty(rows.getDouble("QTY")); value.setVendorId(rows.getString("VENDORID")); value.setPrice(rows.getDouble("PRICE")); value.setLogContent(rows.getString("LOGCONTENT")); value.setReceivedById(rows.getString("RECEIVEDBYID")); value.setReceivedByOrgId(rows.getString("RECEIVEDBYORGID")); value.setExt$Item("username",rows.getString("USERNAME")); value.setExt$Item("orgname",rows.getString("ORGNAME")); return value; }
    private static EmcMtlBean material(ResultSet rows)throws SQLException{EmcMtlBean value=new EmcMtlBean();value.setId(rows.getString("ID"));value.setMtlName(rows.getString("MTLNAME"));value.setSpecification(rows.getString("SPECIFICATION"));value.setStockQty(rows.getDouble("STOCKQTY"));value.setMtlCategoryId(longValue(rows,"MTLCATEGORYID"));value.setHarmfulness(rows.getString("HARMFULNESS"));value.setCreatedById(rows.getString("CREATEDBYID"));value.setCreatedByName(rows.getString("CREATEDBYNAME"));value.setOrgId(rows.getString("ORGID"));Timestamp created=rows.getTimestamp("CREATEDTIME");if(created!=null)value.setCreatedTime(created.toLocalDateTime());return value;}
    private static Long longValue(ResultSet rows,String column)throws SQLException{long value=rows.getLong(column);return rows.wasNull()?null:Long.valueOf(value);}
}
