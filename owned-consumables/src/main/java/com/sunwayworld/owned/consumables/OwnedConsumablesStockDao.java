package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.data.OwnedReadDao;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Bounded original DAO contract; inherited writes fail explicitly. */
public final class OwnedConsumablesStockDao extends MybatisDaoSupport<EmcCsmStockBean,Long>
        implements EmcCsmStockDao, OwnedReadDao<EmcCsmStockBean,Long> {
    private final OwnedConsumablesReads reads;
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;
    public OwnedConsumablesStockDao(OwnedConsumablesReads reads, DataSource dataSource,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.reads = reads; this.dataSource = dataSource; this.provider = provider; this.boundary = boundary; this.audit = audit;
    }
    @Override public Page<EmcCsmStockBean> readPage(RestJsonWrapperBean wrapper) { return reads.stock("0", wrapper, EmcCsmStockBean.class); }
    @Override public EmcCsmStockBean readDetail(Long id) { return reads.stockDetail(id, EmcCsmStockBean.class); }
    public Page<EmcCsmStockBean> readPage(String id, RestJsonWrapperBean wrapper) { return reads.stock(id, wrapper, EmcCsmStockBean.class); }

    /** Isolated Oracle write seam for the original stock creation route. */
    public Long insertStock(String materialId, RestJsonWrapperBean wrapper) {
        if (materialId == null || !materialId.matches("[A-Za-z0-9_-]{1,64}")) throw new IllegalArgumentException("material id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("stock body is required");
        EmcCsmStockLogBean log = wrapper.parseUnique(EmcCsmStockLogBean.class);
        if (log.getQty() == null || log.getQty().doubleValue() < 0 || log.getQty().doubleValue() > 1000000000d)
            throw new IllegalArgumentException("stock quantity is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long result = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireMaterialScope(connection, materialId, organizations);
                long stockId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_CSM_STOCK (ID,MTLID,BATCHNO,STOCKQTY,UNITNAME,EXPIREDATE,LOWLIMIT,STATUS,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    insert.setLong(1, stockId); insert.setString(2, materialId); insert.setString(3, log.getExt$Item("batchno"));
                    insert.setDouble(4, log.getQty().doubleValue()); insert.setString(5, log.getExt$Item("unitname"));
                    insert.setDate(6, null); insert.setDouble(7, 0d); insert.setString(8, "0");
                    insert.setString(9, principal.getId()); insert.setString(10, principal.getId());
                    insert.setString(11, principal.getOrgId()); insert.setString(12, principal.getOrgName()); insert.executeUpdate();
                }
                long logId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_CSM_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                    insert.setLong(1, logId); insert.setLong(2, stockId); insert.setString(3, "添加"); insert.setNull(4, java.sql.Types.NUMERIC);
                    insert.setDouble(5, log.getQty().doubleValue()); insert.setString(6, log.getVendorId());
                    if (log.getPrice() == null) insert.setNull(7, java.sql.Types.NUMERIC); else insert.setDouble(7, log.getPrice().doubleValue());
                    insert.setString(8, log.getLogContent()); insert.setString(9, log.getReceivedById()); insert.setString(10, log.getReceivedByOrgId());
                    insert.setString(11, principal.getId()); insert.setString(12, principal.getId()); insert.setString(13, principal.getOrgId()); insert.setString(14, principal.getOrgName()); insert.executeUpdate();
                }
                updateMaterialStock(connection, materialId);
                return Long.valueOf(stockId);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-stock-insert", principal.getId(), materialId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return result;
        } catch (SQLException failure) { throw new IllegalStateException("consumable stock write failed", failure); }
    }

    /** Isolated Oracle write seam for the original stock-in route. */
    public void storageStock(Long stockId, RestJsonWrapperBean wrapper) {
        if (stockId == null || stockId.longValue() < 0L) throw new IllegalArgumentException("stock id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("stock-in body is required");
        EmcCsmStockLogBean log = wrapper.parseUnique(EmcCsmStockLogBean.class);
        if (log.getQty() == null || log.getQty().doubleValue() <= 0 || log.getQty().doubleValue() > 1000000000d)
            throw new IllegalArgumentException("stock-in quantity is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                StockSnapshot stock = scopedStock(connection, stockId.longValue(), organizations);
                double previous = stock.quantity;
                double current = previous + log.getQty().doubleValue();
                long logId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CSM_STOCK SET STOCKQTY=? WHERE ID=?")) {
                    update.setDouble(1, current); update.setLong(2, stockId.longValue()); update.executeUpdate();
                }
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_CSM_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                    insert.setLong(1, logId); insert.setLong(2, stockId.longValue()); insert.setString(3, "入库"); insert.setDouble(4, previous); insert.setDouble(5, current);
                    insert.setString(6, log.getVendorId()); if (log.getPrice() == null) insert.setNull(7, java.sql.Types.NUMERIC); else insert.setDouble(7, log.getPrice().doubleValue());
                    insert.setString(8, log.getLogContent()); insert.setString(9, log.getReceivedById()); insert.setString(10, log.getReceivedByOrgId());
                    insert.setString(11, principal.getId()); insert.setString(12, principal.getId()); insert.setString(13, principal.getOrgId()); insert.setString(14, principal.getOrgName()); insert.executeUpdate();
                }
                updateMaterialStock(connection, stock.materialId);
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-stock-storage", principal.getId(), String.valueOf(stockId),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("consumable stock-in failed", failure); }
    }

    /** Isolated Oracle write seam for the original inventory route. */
    public void inventoryStock(String materialId, RestJsonWrapperBean wrapper) {
        if (materialId == null || !materialId.matches("[A-Za-z0-9_-]{1,64}")) throw new IllegalArgumentException("material id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("inventory body is required");
        EmcCsmStockLogBean log = wrapper.parseUnique(EmcCsmStockLogBean.class);
        if (log.getQty() == null || log.getQty().doubleValue() < 0 || log.getQty().doubleValue() > 1000000000d)
            throw new IllegalArgumentException("inventory quantity is invalid");
        String rawIds = log.getExt$Item("stockId");
        if (rawIds == null || rawIds.trim().isEmpty()) throw new IllegalArgumentException("inventory stock ids are required");
        String[] parts = rawIds.split(",");
        if (parts.length == 0 || parts.length > 100) throw new IllegalArgumentException("inventory stock ids are invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (String part : parts) {
                    long stockId;
                    try { stockId = Long.parseLong(part.trim()); } catch (NumberFormatException failure) { throw new IllegalArgumentException("inventory stock ids are invalid"); }
                    StockSnapshot stock = scopedStock(connection, stockId, organizations);
                    if (!materialId.equals(stock.materialId)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                    long logId = OwnedWriteSupport.nextIdentity(connection);
                    try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CSM_STOCK SET STOCKQTY=? WHERE ID=?");
                            PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_CSM_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                        update.setDouble(1, log.getQty().doubleValue()); update.setLong(2, stockId); update.executeUpdate();
                        insert.setLong(1, logId); insert.setLong(2, stockId); insert.setString(3, "盘存"); insert.setDouble(4, stock.quantity); insert.setDouble(5, log.getQty().doubleValue());
                        insert.setNull(6, java.sql.Types.VARCHAR); insert.setNull(7, java.sql.Types.NUMERIC); insert.setString(8, log.getLogContent());
                        insert.setString(9, log.getReceivedById()); insert.setString(10, log.getReceivedByOrgId()); insert.setString(11, principal.getId()); insert.setString(12, principal.getId()); insert.setString(13, principal.getOrgId()); insert.setString(14, principal.getOrgName()); insert.executeUpdate();
                    }
                }
                updateMaterialStock(connection, materialId);
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-stock-inventory", principal.getId(), materialId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("consumable inventory write failed", failure); }
    }

    /** Isolated Oracle write seam for the original receive route. */
    public void receiveStock(Long stockId, RestJsonWrapperBean wrapper) {
        if (stockId == null || stockId.longValue() < 0L) throw new IllegalArgumentException("stock id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("receive body is required");
        EmcCsmStockLogBean log = wrapper.parseUnique(EmcCsmStockLogBean.class);
        if (log.getQty() == null || log.getQty().doubleValue() <= 0 || log.getQty().doubleValue() > 1000000000d)
            throw new IllegalArgumentException("receive quantity is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                StockSnapshot stock = scopedStock(connection, stockId.longValue(), organizations);
                double previous = stock.quantity;
                double current = previous - log.getQty().doubleValue();
                if (current < 0d) throw new IllegalArgumentException("receive quantity exceeds stock");
                requireRecipient(connection, log.getReceivedById(), log.getReceivedByOrgId(), organizations);
                long logId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CSM_STOCK SET STOCKQTY=? WHERE ID=?");
                        PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_CSM_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                    update.setDouble(1, current); update.setLong(2, stockId.longValue()); update.executeUpdate();
                    insert.setLong(1, logId); insert.setLong(2, stockId.longValue()); insert.setString(3, "领取"); insert.setDouble(4, previous); insert.setDouble(5, current);
                    insert.setNull(6, java.sql.Types.VARCHAR); insert.setNull(7, java.sql.Types.NUMERIC); insert.setString(8, log.getLogContent()); insert.setString(9, log.getReceivedById()); insert.setString(10, log.getReceivedByOrgId());
                    insert.setString(11, principal.getId()); insert.setString(12, principal.getId()); insert.setString(13, principal.getOrgId()); insert.setString(14, principal.getOrgName()); insert.executeUpdate();
                }
                updateMaterialStock(connection, stock.materialId);
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-stock-receive", principal.getId(), String.valueOf(stockId),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("consumable stock receive failed", failure); }
    }

    /** Isolated Oracle write seam for the original stock abandon route. */
    public void abandonStock(String materialId, RestJsonWrapperBean wrapper) {
        if (materialId == null || !materialId.matches("[A-Za-z0-9_-]{1,64}")) throw new IllegalArgumentException("material id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("abandon body is required");
        EmcCsmStockLogBean log = wrapper.parseUnique(EmcCsmStockLogBean.class);
        long[] stockIds = stockIds(log.getExt$Item("stockId"));
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (long stockId : stockIds) {
                    StockSnapshot stock = scopedStock(connection, stockId, organizations);
                    if (!materialId.equals(stock.materialId)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                    long logId = OwnedWriteSupport.nextIdentity(connection);
                    try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CSM_STOCK SET STATUS=? WHERE ID=?");
                            PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_CSM_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,VENDORID,PRICE,LOGCONTENT,RECEIVEDBYID,RECEIVEDBYORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,SYSTIMESTAMP,?,?)")) {
                        update.setString(1, "1"); update.setLong(2, stockId); update.executeUpdate();
                        insert.setLong(1, logId); insert.setLong(2, stockId); insert.setString(3, "废弃"); insert.setDouble(4, stock.quantity); insert.setDouble(5, stock.quantity);
                        insert.setNull(6, java.sql.Types.VARCHAR); insert.setNull(7, java.sql.Types.NUMERIC); insert.setString(8, log.getLogContent());
                        insert.setString(9, log.getReceivedById()); insert.setString(10, log.getReceivedByOrgId()); insert.setString(11, principal.getId()); insert.setString(12, principal.getId()); insert.setString(13, principal.getOrgId()); insert.setString(14, principal.getOrgName()); insert.executeUpdate();
                    }
                }
                updateMaterialStock(connection, materialId);
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-stock-abandon", principal.getId(), materialId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("consumable stock abandon failed", failure); }
    }

    /** Isolated Oracle write seam for the original stock delete route. */
    public void deleteStocks(String materialId, RestJsonWrapperBean wrapper) {
        if (materialId == null || !materialId.matches("[A-Za-z0-9_-]{1,64}")) throw new IllegalArgumentException("material id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("delete body is required");
        java.util.List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("stock ids are invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (Long id : ids) {
                    if (id == null || id.longValue() < 0L) throw new IllegalArgumentException("stock id is invalid");
                    StockSnapshot stock = scopedStock(connection, id.longValue(), organizations);
                    if (!materialId.equals(stock.materialId)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                }
                for (Long id : ids) {
                    try (PreparedStatement logs = connection.prepareStatement("DELETE FROM T_EMC_CSM_STOCK_LOG WHERE STOCKID=?");
                            PreparedStatement stock = connection.prepareStatement("DELETE FROM T_EMC_CSM_STOCK WHERE ID=?")) {
                        logs.setLong(1, id.longValue()); logs.executeUpdate(); stock.setLong(1, id.longValue()); stock.executeUpdate();
                    }
                }
                updateMaterialStock(connection, materialId);
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-stock-delete", principal.getId(), materialId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("consumable stock delete failed", failure); }
    }

    private OwnedPrincipal trustedWritePrincipal() throws SQLException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        HttpSession session = request.getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.CONSUMABLE_MANAGE);
        return boundary.currentPrincipal(session);
    }

    private static void requireMaterialScope(Connection connection, String materialId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_CONSUME WHERE ID=? AND CREATEDBYORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); } sql.append(')');
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setString(index++, materialId); for (String org : organizations) query.setString(index++, org);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
        }
    }

    private static void requireRecipient(Connection connection, String id, String orgId, Set<String> organizations) throws SQLException {
        if (id == null || orgId == null) throw new IllegalArgumentException("stock recipient is required");
        if (!organizations.contains(orgId)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_CORE_USER WHERE ID=? AND ORGID=? AND STATUS='activated' AND NVL(LASTSUSPENDEDFLAG,'0')='0'")) {
            query.setString(1,id); query.setString(2,orgId);
            try (ResultSet rows=query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
        }
    }

    private static void updateMaterialStock(Connection connection, String materialId) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CONSUME SET STOCKQTY=(SELECT NVL(SUM(STOCKQTY),0) FROM T_EMC_CSM_STOCK WHERE MTLID=? AND STATUS='0') WHERE ID=?")) {
            update.setString(1, materialId); update.setString(2, materialId); update.executeUpdate();
        }
    }

    private static long[] stockIds(String rawIds) {
        if (rawIds == null || rawIds.trim().isEmpty()) throw new IllegalArgumentException("stock ids are required");
        String[] parts = rawIds.split(",");
        if (parts.length == 0 || parts.length > 100) throw new IllegalArgumentException("stock ids are invalid");
        long[] result = new long[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try { result[i] = Long.parseLong(parts[i].trim()); }
            catch (NumberFormatException failure) { throw new IllegalArgumentException("stock ids are invalid"); }
            if (result[i] < 0L) throw new IllegalArgumentException("stock ids are invalid");
        }
        return result;
    }

    private static StockSnapshot scopedStock(Connection connection, long stockId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder sql = new StringBuilder("SELECT S.MTLID,S.STOCKQTY FROM T_EMC_CSM_STOCK S JOIN T_EMC_CONSUME C ON C.ID=S.MTLID WHERE S.ID=? AND C.CREATEDBYORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); } sql.append(") FOR UPDATE");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setLong(index++, stockId); for (String org : organizations) query.setString(index++, org);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); return new StockSnapshot(rows.getString(1), rows.getDouble(2)); }
        }
    }

    private static final class StockSnapshot {
        final String materialId; final double quantity;
        StockSnapshot(String materialId, double quantity) { this.materialId = materialId; this.quantity = quantity; }
    }
}
