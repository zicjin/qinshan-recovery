package com.sunwayworld.owned.attachments;

import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentBean;
import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentPermissionBean;
import com.sunwayworld.emc.module.item.homeattachment.dao.EmcHomeAttachmentDao;
import com.sunwayworld.emc.module.item.homeattachment.service.EmcHomeAttachmentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Read adapter plus bounded metadata/permission writes with commit-ordered file cleanup. */
public final class OwnedAttachmentReadService implements EmcHomeAttachmentService {
    private final OwnedAttachmentReadDao dao;
    private final DataSource dataSource;
    private final OwnedIdentityHttpBoundary identity;
    private final AuditEventSink audit;
    private final Environment environment;
    private final OwnedAttachmentFileStore files;

    public OwnedAttachmentReadService(OwnedAttachmentReadDao dao, DataSource dataSource,
            OwnedIdentityHttpBoundary identity, AuditEventSink audit, Environment environment) {
        this.dao = dao; this.dataSource = dataSource; this.identity = identity; this.audit = audit; this.environment = environment;
        String configured = environment == null ? null : environment.getProperty("owned.attachments.root");
        if (configured == null || configured.trim().isEmpty()) throw new IllegalStateException("Explicit owned.attachments.root required");
        this.files = new OwnedAttachmentFileStore(Paths.get(configured));
    }

    @Override public EmcHomeAttachmentDao getDao() { return dao; }
    @Override public String getDownloadUrl(Long id, RestJsonWrapperBean request) { return dao.downloadUrl(id); }

    @Override public Long insert(RestJsonWrapperBean request) {
        OwnedPrincipal principal = writablePrincipal();
        List<EmcHomeAttachmentBean> values = request == null ? Collections.<EmcHomeAttachmentBean>emptyList() : request.parse(EmcHomeAttachmentBean.class);
        EmcHomeAttachmentBean value = values.isEmpty() ? new EmcHomeAttachmentBean() : values.get(0);
        Long id = OwnedWriteSupport.transactionUnchecked(dataSource, connection -> {
            long next = OwnedWriteSupport.nextIdentity(connection);
            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_HOME_ATTACHMENT (ID,ENDDATE,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,ACTIVATEDFLAG) VALUES (?,?,?,?,SYSTIMESTAMP,?,?,?)")) {
                insert.setLong(1, next); setTimestamp(insert, 2, value.getEndDate()); insert.setString(3, principal.getId()); insert.setString(4, principal.getId());
                insert.setString(5, principal.getOrgId()); insert.setString(6, principal.getOrgName()); insert.setString(7, value.getActivatedFlag() == null ? "0" : value.getActivatedFlag()); insert.executeUpdate();
            }
            return Long.valueOf(next);
        });
        record("attachment-metadata-insert", principal, String.valueOf(id)); return id;
    }

    @Override public void delete(RestJsonWrapperBean request) {
        OwnedPrincipal principal = writablePrincipal();
        List<EmcHomeAttachmentBean> values = request.parse(EmcHomeAttachmentBean.class);
        List<Path> stale = OwnedWriteSupport.transactionUnchecked(dataSource, connection -> {
            List<Path> paths = new ArrayList<Path>();
            for (EmcHomeAttachmentBean value : values) {
                long id = requiredOwned(connection, value.getId(), principal);
                try (PreparedStatement p = connection.prepareStatement("DELETE FROM T_EMC_HOME_ATTA_PERMISSION WHERE ATTACHMENTID=?")) { p.setLong(1, id); p.executeUpdate(); }
                try (PreparedStatement old = connection.prepareStatement("SELECT SCOPE,MD5NAME,NAME,TO_CHAR(CREATEDTIME,'YYYYMMDD') FROM T_CORE_FILE WHERE TARGETID=?")) {
                    old.setString(1, "T_EMC_HOME_ATTACHMENT$" + id);
                    try (ResultSet rows = old.executeQuery()) { while (rows.next()) paths.add(files.resolve(rows.getString(1), rows.getString(4), rows.getString(2), rows.getString(3))); }
                }
                try (PreparedStatement f = connection.prepareStatement("UPDATE T_CORE_FILE SET TARGETID=REPLACE(TARGETID,'T_EMC_HOME_ATTACHMENT$','T_EMC_HOME_ATTACHMENT_DELETE$') WHERE TARGETID=?")) { f.setString(1, "T_EMC_HOME_ATTACHMENT$" + id); f.executeUpdate(); }
                try (PreparedStatement a = connection.prepareStatement("DELETE FROM T_EMC_HOME_ATTACHMENT WHERE ID=? AND CREATEDBYORGID=?")) { a.setLong(1, id); a.setString(2, principal.getOrgId()); a.executeUpdate(); }
            }
            return paths;
        });
        for (Path path : stale) files.deleteQuietly(path);
        record("attachment-metadata-delete", principal, "batch");
    }

    @Override public void activate(RestJsonWrapperBean request) { changeActivation(request, true); }
    @Override public void deactivate(RestJsonWrapperBean request) { changeActivation(request, false); }

    private void changeActivation(RestJsonWrapperBean request, boolean active) {
        OwnedPrincipal principal = writablePrincipal(); List<EmcHomeAttachmentBean> values = request.parse(EmcHomeAttachmentBean.class);
        OwnedWriteSupport.transactionUnchecked(dataSource, connection -> {
            for (EmcHomeAttachmentBean value : values) {
                long id = requiredOwned(connection, value.getId(), principal);
                try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_HOME_ATTACHMENT SET ACTIVATEDFLAG=?,ACTIVATEDBYID=?,ACTIVATEDBYNAME=?,ACTIVATEDTIME=SYSTIMESTAMP WHERE ID=? AND CREATEDBYORGID=?")) {
                    update.setString(1, active ? "1" : "0"); update.setString(2, principal.getId()); update.setString(3, principal.getId()); update.setLong(4, id); update.setString(5, principal.getOrgId()); update.executeUpdate();
                }
            }
            return null;
        });
        record(active ? "attachment-activate" : "attachment-deactivate", principal, "batch");
    }

    @Override public void insertAttachmentPermission(RestJsonWrapperBean request) {
        OwnedPrincipal principal = writablePrincipal(); List<EmcHomeAttachmentPermissionBean> values = request.parse(EmcHomeAttachmentPermissionBean.class);
        OwnedWriteSupport.transactionUnchecked(dataSource, connection -> { for (EmcHomeAttachmentPermissionBean value : values) insertPermission(connection, value, principal); return null; });
        record("attachment-permission-insert", principal, "batch");
    }

    @Override public void deleteAttachmentPermission(RestJsonWrapperBean request) {
        OwnedPrincipal principal = writablePrincipal(); List<EmcHomeAttachmentPermissionBean> values = request.parse(EmcHomeAttachmentPermissionBean.class);
        OwnedWriteSupport.transactionUnchecked(dataSource, connection -> { for (EmcHomeAttachmentPermissionBean value : values) { requiredOwned(connection, value.getAttachmentId(), principal); try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_HOME_ATTA_PERMISSION WHERE ID=? AND ATTACHMENTID=?")) { delete.setLong(1, value.getId()); delete.setLong(2, value.getAttachmentId()); delete.executeUpdate(); } } return null; });
        record("attachment-permission-delete", principal, "batch");
    }

    @Override public Page<EmcHomeAttachmentPermissionBean> selectPermissionPagination(Long id, RestJsonWrapperBean request) {
        OwnedPrincipal principal = writablePrincipal(); List<EmcHomeAttachmentPermissionBean> values = new ArrayList<EmcHomeAttachmentPermissionBean>();
        try (Connection connection = dataSource.getConnection()) {
            requiredOwned(connection, id, principal);
            try (PreparedStatement query = connection.prepareStatement("SELECT ID,ATTACHMENTID,USERID FROM T_EMC_HOME_ATTA_PERMISSION WHERE ATTACHMENTID=? ORDER BY ID")) {
                query.setLong(1, id); try (ResultSet rows = query.executeQuery()) { while (rows.next()) { EmcHomeAttachmentPermissionBean value = new EmcHomeAttachmentPermissionBean(); value.setId(rows.getLong(1)); value.setAttachmentId(rows.getLong(2)); value.setUserId(rows.getString(3)); values.add(value); } }
            }
        } catch (SQLException failure) { throw new IllegalStateException("Attachment permission read failed", failure); }
        return new Pagination<EmcHomeAttachmentPermissionBean>(values.size(), values.size(), 1, values);
    }

    @Override public void batchEmpower(RestJsonWrapperBean request) {
        String users = request.getParamValue("userId"); if (users == null || users.trim().isEmpty()) throw new IllegalArgumentException("userId required");
        OwnedPrincipal principal = writablePrincipal(); List<EmcHomeAttachmentBean> attachments = request.parse(EmcHomeAttachmentBean.class);
        OwnedWriteSupport.transactionUnchecked(dataSource, connection -> { for (EmcHomeAttachmentBean attachment : attachments) for (String user : users.split(",")) { EmcHomeAttachmentPermissionBean value = new EmcHomeAttachmentPermissionBean(); value.setAttachmentId(attachment.getId()); value.setUserId(user.trim()); insertPermission(connection, value, principal); } return null; });
        record("attachment-permission-empower", principal, "batch");
    }

    private void insertPermission(Connection connection, EmcHomeAttachmentPermissionBean value, OwnedPrincipal principal) throws SQLException {
        requiredOwned(connection, value.getAttachmentId(), principal); if (value.getUserId() == null || value.getUserId().trim().isEmpty()) throw new IllegalArgumentException("user id required");
        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_EMC_HOME_ATTA_PERMISSION (ID,ATTACHMENTID,USERID) SELECT ?,?,? FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM T_EMC_HOME_ATTA_PERMISSION WHERE ATTACHMENTID=? AND USERID=?)")) {
            insert.setLong(1, OwnedWriteSupport.nextIdentity(connection)); insert.setLong(2, value.getAttachmentId()); insert.setString(3, value.getUserId()); insert.setLong(4, value.getAttachmentId()); insert.setString(5, value.getUserId()); insert.executeUpdate();
        }
    }

    private OwnedPrincipal writablePrincipal() {
        if (environment == null || !"isolated".equals(environment.getProperty("owned.attachments.write.mode"))) throw denied();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); if (attributes == null) throw denied();
        HttpServletRequest request = attributes.getRequest(); OwnedWriteSupport.requireLoopback(request); HttpSession session = request.getSession(false);
        try { identity.requirePermission(session, OwnedIdentityProvider.ATTACHMENT_MANAGE); return identity.currentPrincipal(session); }
        catch (SQLException failure) { throw new IllegalStateException("Attachment identity unavailable", failure); }
    }

    private long requiredOwned(Connection connection, Long id, OwnedPrincipal principal) throws SQLException {
        if (id == null || id.longValue() < 1) throw new IllegalArgumentException("attachment id required");
        try (PreparedStatement query = connection.prepareStatement("SELECT ID FROM T_EMC_HOME_ATTACHMENT WHERE ID=? AND CREATEDBYORGID=?")) { query.setLong(1, id); query.setString(2, principal.getOrgId()); try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); return rows.getLong(1); } }
    }

    private void record(String action, OwnedPrincipal principal, String resource) { audit.record(new AuditEvent(Instant.now(), action, principal.getId(), resource, AuditEvent.Outcome.ALLOWED, "isolated-write", null)); }
    private static void setTimestamp(PreparedStatement statement, int index, java.time.LocalDateTime value) throws SQLException { if (value == null) statement.setTimestamp(index, null); else statement.setTimestamp(index, Timestamp.valueOf(value)); }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
}
