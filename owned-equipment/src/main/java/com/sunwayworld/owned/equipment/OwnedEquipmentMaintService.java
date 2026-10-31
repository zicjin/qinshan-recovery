package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.EmcEquiptMaintDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Source-owned maintenance-event service used by inherited equipment workflows. */
public final class OwnedEquipmentMaintService implements EmcEquiptMaintService {
    private final OwnedEquipmentMaintDao dao;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;
    private final Environment environment;

    public OwnedEquipmentMaintService(OwnedEquipmentMaintDao dao, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit, Environment environment) {
        this.dao = dao; this.provider = provider; this.boundary = boundary; this.audit = audit; this.environment = environment;
    }

    @Override public EmcEquiptMaintDao getDao() { return dao; }

    @Override
    public Long insert(Long categoryId, RestJsonWrapperBean wrapper) {
        requireWrite();
        if (categoryId == null || categoryId.longValue() < 0L || wrapper == null) throw new IllegalArgumentException("maintenance category and body are required");
        EmcEquiptMaintBean value = wrapper.parseUnique(EmcEquiptMaintBean.class); validate(value);
        try {
            OwnedPrincipal principal = writePrincipal(); Long id = dao.insert(value, categoryId, principal);
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-maint-event-insert", principal.getId(), String.valueOf(id), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return id;
        } catch (java.sql.SQLException failure) { throw new IllegalStateException("equipment maintenance event write failed", failure); }
    }

    @Override public Page<EmcEquiptMaintBean> selectPaginationByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
        return dao.readPage(categoryId(filter), wrapper);
    }

    @Override public EmcEquiptMaintBean selectById(Long id) { return dao.selectByIdIfPresent(id); }

    @Override public List<EmcEquiptMaintBean> selectListByIds(List<Long> ids) { return dao.selectListByIds(ids); }

    @Override public EmcEquiptMaintBean selectFirst(MapperParameter parameter) {
        Long categoryId = null; String event = null;
        if (parameter != null) {
            categoryId = longValue(parameter.get("equiptCategoryId"));
            event = text(parameter.get("maintEvent"));
            if ((categoryId == null || event == null) && parameter.getFilterParamPatterns() != null) {
                for (com.sunwayworld.framework.mybatis.mapper.FilterParamPattern pattern : parameter.getFilterParamPatterns()) {
                    for (Map.Entry<String,Object> entry : pattern.getMap().entrySet()) {
                        if ("EQUIPTCATEGORYID".equalsIgnoreCase(entry.getKey())) categoryId = longValue(entry.getValue());
                        if ("MAINTEVENT".equalsIgnoreCase(entry.getKey())) event = text(entry.getValue());
                    }
                }
            }
        }
        if (categoryId == null || event == null) throw new IllegalArgumentException("unsupported maintenance first filter");
        return dao.selectFirst(categoryId, event);
    }

    @Override public void delete(RestJsonWrapperBean wrapper) {
        requireWrite(); if (wrapper == null) throw new IllegalArgumentException("maintenance event ids are required");
        List<Long> ids = wrapper.parseId(Long.class); if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("maintenance event ids are required");
        try { OwnedPrincipal principal = writePrincipal(); dao.delete(ids, principal); audit.record(new AuditEvent(java.time.Instant.now(), "equipment-maint-event-delete", principal.getId(), ids.toString(), AuditEvent.Outcome.ALLOWED, "isolated-write", null)); }
        catch (java.sql.SQLException failure) { throw new IllegalStateException("equipment maintenance event delete failed", failure); }
    }

    private OwnedPrincipal writePrincipal() throws java.sql.SQLException { HttpSession session = current().getSession(false); boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_MANAGE); return boundary.currentPrincipal(session); }
    private void requireWrite() { if (environment == null || !"isolated".equals(environment.getProperty("owned.equipment.write.mode"))) throw denied(); OwnedWriteSupport.requireLoopback(current()); }
    private static javax.servlet.http.HttpServletRequest current() { RequestAttributes attributes = RequestContextHolder.getRequestAttributes(); if (!(attributes instanceof ServletRequestAttributes)) throw denied(); return ((ServletRequestAttributes) attributes).getRequest(); }
    private static AccessDeniedException denied() { return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
    private static Long categoryId(SearchFilter filter) { if (filter == null || filter.getStdFilterList().isEmpty()) throw new IllegalArgumentException("maintenance category id is required"); Object value = filter.getStdFilterList().get(0).getMap().values().iterator().next(); Long id = longValue(value); if (id == null) throw new IllegalArgumentException("maintenance category id is invalid"); return id; }
    private static void validate(EmcEquiptMaintBean value) { if (value == null || value.getMaintEvent() == null || value.getMaintEvent().trim().isEmpty() || value.getMaintEvent().length() > 64) throw new IllegalArgumentException("maintenance event is required"); if (value.getEventDesc() != null && value.getEventDesc().length() > 256) throw new IllegalArgumentException("event description is too long"); if (value.getWarningDays() != null && (value.getWarningDays() < 0L || value.getWarningDays() > 36500L)) throw new IllegalArgumentException("warning days is invalid"); }
    private static Long longValue(Object value) { try { return value == null ? null : Long.valueOf(String.valueOf(value)); } catch (NumberFormatException failure) { return null; } }
    private static String text(Object value) { String result = value == null ? null : String.valueOf(value).trim(); return result == null || result.isEmpty() ? null : result; }
}
