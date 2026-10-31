package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptUseRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptUseRecordDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Isolated Oracle adapter for the original equipment-use-record routes. */
public final class OwnedEquipmentUseRecordDao extends MybatisDaoSupport<EmcEquiptUseRecordBean, Long>
        implements EmcEquiptUseRecordDao {
    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;

    public OwnedEquipmentUseRecordDao(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.dataSource = dataSource; this.provider = provider; this.boundary = boundary; this.audit = audit;
    }

    public Long insertRecords(RestJsonWrapperBean wrapper) {
        List<EmcEquiptUseRecordBean> values = parseValues(wrapper, false);
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long firstId = OwnedWriteSupport.transaction(dataSource, connection -> {
                    long first = -1L;
                    try (PreparedStatement insert = connection.prepareStatement(insertSql())) {
                        for (EmcEquiptUseRecordBean value : values) {
                            requireEquipmentScope(connection, value.getEquiptId().longValue(), organizations);
                            long id = OwnedWriteSupport.nextIdentity(connection); if (first < 0L) first = id;
                            bindInsert(insert, id, value, principal); insert.addBatch();
                        }
                        insert.executeBatch();
                    }
                    return Long.valueOf(first);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-use-record-insert", principal.getId(),
                    String.valueOf(firstId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return firstId;
        } catch (SQLException failure) { throw new IllegalStateException("equipment use record write failed", failure); }
    }

    public void updateRecords(RestJsonWrapperBean wrapper) {
        List<EmcEquiptUseRecordBean> values = parseValues(wrapper, true);
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                    for (EmcEquiptUseRecordBean value : values) {
                        if (value.getId() != null) {
                            requireRecordScope(connection, value.getId().longValue(), value.getEquiptId().longValue(), organizations);
                        } else {
                            if (value.getEquiptId() == null || value.getSampleId() == null || value.getTestId() == null)
                                throw new IllegalArgumentException("use record identity is required");
                            requireEquipmentScope(connection, value.getEquiptId().longValue(), organizations);
                        }
                    }
                    try (PreparedStatement find = connection.prepareStatement(
                            "SELECT ID FROM T_EMC_EQUIPT_USE_RECORD WHERE EQUIPTID=? AND SAMPLEID=? AND TESTID=?");
                         PreparedStatement update = connection.prepareStatement(updateSql());
                         PreparedStatement insert = connection.prepareStatement(insertSql())) {
                        for (EmcEquiptUseRecordBean value : values) {
                            Long id = value.getId();
                            if (id == null) {
                                find.setLong(1, value.getEquiptId().longValue()); find.setLong(2, value.getSampleId().longValue()); find.setString(3, value.getTestId());
                                try (ResultSet rows = find.executeQuery()) { if (rows.next()) id = Long.valueOf(rows.getLong(1)); }
                            }
                            if (id == null) {
                                id = Long.valueOf(OwnedWriteSupport.nextIdentity(connection)); bindInsert(insert, id.longValue(), value, principal); insert.executeUpdate();
                            } else {
                                bindUpdate(update, id.longValue(), value); if (update.executeUpdate() != 1) throw denied();
                            }
                        }
                    }
                    return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-use-record-update", principal.getId(),
                    String.valueOf(values.size()), AuditEvent.Outcome.ALLOWED, "isolated-write; upsert", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment use record update failed", failure); }
    }

    public void deleteRecords(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("use record ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("use record ids are required");
        try {
            OwnedPrincipal principal = trustedManagePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                    for (Long id : ids) { if (id == null || !recordBelongsToScope(connection, id.longValue(), organizations)) throw denied(); }
                    try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EQUIPT_USE_RECORD WHERE ID=?")) {
                        for (Long id : ids) { delete.setLong(1, id.longValue()); delete.addBatch(); }
                        delete.executeBatch();
                    }
                    return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-use-record-delete", principal.getId(), ids.toString(),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment use record delete failed", failure); }
    }

    public Page<EmcEquiptUseRecordBean> readByEquipmentOrSample(Long id, RestJsonWrapperBean wrapper) {
        if (id == null || id.longValue() < 0L) throw new IllegalArgumentException("equipment or sample id is invalid");
        HttpServletRequest request = currentRequest();
        String selectKey = request.getParameter("selectKey");
        if (selectKey == null || selectKey.trim().isEmpty()) selectKey = "EQUIPTID";
        if (!"EQUIPTID".equalsIgnoreCase(selectKey) && !"SAMPLEID".equalsIgnoreCase(selectKey))
            throw new IllegalArgumentException("unsupported use record selection key");
        return read(wrapper, selectKey.toUpperCase(Locale.ROOT), id);
    }

    public Page<EmcEquiptUseRecordBean> readAll(RestJsonWrapperBean wrapper) { return read(wrapper, null, null); }

    private Page<EmcEquiptUseRecordBean> read(RestJsonWrapperBean wrapper, String key, Long id) {
        if (wrapper == null) throw new IllegalArgumentException("use record query is required");
        try {
            OwnedPrincipal principal = trustedReadPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper.getRequestJson(), wrapper.getParamMap());
            if (organizations.isEmpty()) return empty(request);
            List<String> clauses = new ArrayList<String>(); List<Object> values = new ArrayList<Object>();
            String scope = scopeSql(organizations, values);
            if (key != null) { clauses.add("R." + key + "=?"); values.add(id); }
            appendFilters(request, clauses, values);
            String where = scope + (clauses.isEmpty() ? "" : " AND " + String.join(" AND ", clauses));
            String countSql = "SELECT COUNT(1) FROM T_EMC_EQUIPT_USE_RECORD R WHERE " + where;
            String pageSql = "SELECT * FROM (SELECT R.*, ROW_NUMBER() OVER (ORDER BY R.ID DESC) RN FROM T_EMC_EQUIPT_USE_RECORD R WHERE " + where + ") WHERE RN BETWEEN ? AND ? ORDER BY ID DESC";
            int from = (request.getPageNumber() - 1) * request.getPageSize() + 1, to = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total; try (PreparedStatement count = connection.prepareStatement(countSql)) { bind(count, values); try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); } }
                List<EmcEquiptUseRecordBean> rows = new ArrayList<EmcEquiptUseRecordBean>(); List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(Integer.valueOf(from)); pageValues.add(Integer.valueOf(to));
                try (PreparedStatement query = connection.prepareStatement(pageSql)) { bind(query, pageValues); try (ResultSet result = query.executeQuery()) { while (result.next()) rows.add(convert(result)); } }
                return new Pagination<EmcEquiptUseRecordBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (IllegalArgumentException failure) { throw failure; }
        catch (SQLException failure) { throw new IllegalStateException("equipment use record query failed", failure); }
    }

    private List<EmcEquiptUseRecordBean> parseValues(RestJsonWrapperBean wrapper, boolean requireSampleAndTest) {
        if (wrapper == null) throw new IllegalArgumentException("use records are required");
        List<EmcEquiptUseRecordBean> values = wrapper.parse(EmcEquiptUseRecordBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("use records are required");
        for (EmcEquiptUseRecordBean value : values) {
            if (value == null || value.getEquiptId() == null || value.getEquiptId().longValue() < 0L) throw new IllegalArgumentException("equipment id is required");
            if (requireSampleAndTest && (value.getSampleId() == null || value.getSampleId().longValue() < 0L)) throw new IllegalArgumentException("sample id is required");
            if (requireSampleAndTest && (value.getTestId() == null || value.getTestId().trim().isEmpty())) throw new IllegalArgumentException("test id is required");
        }
        return values;
    }

    private void appendFilters(QueryRequest request, List<String> clauses, List<Object> values) {
        for (com.sunwayworld.framework.mybatis.mapper.FilterParamPattern group : request.getFilterPatterns()) {
            String pattern = group.getMatchPattern();
            for (Map.Entry<String,Object> entry : group.getMap().entrySet()) {
                String field = useRecordColumn(entry.getKey()); Object value = entry.getValue();
                if ("CISC".equals(pattern) || "SC".equals(pattern)) { clauses.add("UPPER(R." + field + ") LIKE ?"); values.add("%" + String.valueOf(value).toUpperCase(Locale.ROOT) + "%"); }
                else if ("EQ".equals(pattern) || "SEQ".equals(pattern)) { clauses.add("R." + field + (value == null ? " IS NULL" : "=?")); if (value != null) values.add(value); }
                else throw new IllegalArgumentException("unsupported equipment use record filter pattern: " + pattern);
            }
        }
        for (String key : request.getMapperParameters().keySet()) if (!"target_type".equals(key) && !"orgid_seq".equals(key) && !"selectKey".equalsIgnoreCase(key)) throw new IllegalArgumentException("unsupported equipment use record query parameter: " + key);
    }

    private static String useRecordColumn(String key) {
        String normalized = key == null ? "" : key.toUpperCase(Locale.ROOT);
        String[] suffixes = {"_CISC", "_EQ", "_SEQ", "_DG", "_DGOE", "_DL", "_DLOE"};
        for (String suffix : suffixes) if (normalized.endsWith(suffix)) normalized = normalized.substring(0, normalized.length() - suffix.length());
        if ("ID".equals(normalized) || "PROJID".equals(normalized) || "SAMPLEID".equals(normalized) || "TESTID".equals(normalized)
                || "EQUIPTID".equals(normalized) || "RUNID".equals(normalized) || "USERID".equals(normalized)
                || "BEFOREUSESTATUS".equals(normalized) || "AFTERUSESTATUS".equals(normalized) || "EQUIPTCONDITION".equals(normalized)
                || "ORDERQTY".equals(normalized) || "STARTDATE".equals(normalized) || "STARTTIME".equals(normalized)
                || "ENDDATE".equals(normalized) || "ENDTIME".equals(normalized)) return normalized;
        throw new IllegalArgumentException("unsupported equipment use record filter column: " + key);
    }

    private String scopeSql(Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("EXISTS (SELECT 1 FROM T_EMC_EQUIPT E JOIN T_EMC_ORG_PERMISSION OP ON OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID WHERE E.ID=R.EQUIPTID AND OP.ORGID IN (");
        int index = 0; for (String organization : organizations) { if (index++ > 0) sql.append(','); sql.append('?'); values.add(organization); }
        return sql.append("))").toString();
    }

    private static String insertSql() {
        return "INSERT INTO T_EMC_EQUIPT_USE_RECORD (ID,PROJID,PROJNO,SAMPLEID,SAMPLENO,TESTID,TESTNAME,EQUIPTID,USEDTIME,BEFOREUSESTATUS,AFTERUSESTATUS,USERID,USERNAME,RUNID,EQUIPTCONDITION,ORDERQTY,STARTDATE,STARTTIME,ENDDATE,ENDTIME,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)";
    }
    private static String updateSql() {
        return "UPDATE T_EMC_EQUIPT_USE_RECORD SET PROJID=?,PROJNO=?,SAMPLEID=?,SAMPLENO=?,TESTID=?,TESTNAME=?,EQUIPTID=?,USEDTIME=?,BEFOREUSESTATUS=?,AFTERUSESTATUS=?,USERID=?,USERNAME=?,RUNID=?,EQUIPTCONDITION=?,ORDERQTY=?,STARTDATE=?,STARTTIME=?,ENDDATE=?,ENDTIME=? WHERE ID=?";
    }
    private static void bindInsert(PreparedStatement s, long id, EmcEquiptUseRecordBean v, OwnedPrincipal p) throws SQLException {
        int i=1; s.setLong(i++,id); setLong(s,i++,v.getProjId()); s.setString(i++,v.getProjNo()); setLong(s,i++,v.getSampleId()); s.setString(i++,v.getSampleNo()); s.setString(i++,v.getTestId()); s.setString(i++,v.getTestName()); setLong(s,i++,v.getEquiptId()); setDate(s,i++,v.getUsedTime()); s.setString(i++,v.getBeforeUseStatus()==null?"正常":v.getBeforeUseStatus()); s.setString(i++,v.getAfterUseStatus()==null?"正常":v.getAfterUseStatus()); s.setString(i++,v.getUserId()); s.setString(i++,v.getUserName()); s.setString(i++,v.getRunId()); s.setString(i++,v.getEquiptCondition()); s.setString(i++,v.getOrderQty()); s.setString(i++,v.getStartDate()); s.setString(i++,v.getStartTime()); s.setString(i++,v.getEndDate()); s.setString(i++,v.getEndTime()); s.setString(i++,p.getId()); s.setString(i++,p.getId()); s.setString(i++,p.getOrgId()); s.setString(i,p.getOrgName());
    }
    private static void bindUpdate(PreparedStatement s, long id, EmcEquiptUseRecordBean v) throws SQLException {
        int i=1; setLong(s,i++,v.getProjId()); s.setString(i++,v.getProjNo()); setLong(s,i++,v.getSampleId()); s.setString(i++,v.getSampleNo()); s.setString(i++,v.getTestId()); s.setString(i++,v.getTestName()); setLong(s,i++,v.getEquiptId()); setDate(s,i++,v.getUsedTime()); s.setString(i++,v.getBeforeUseStatus()); s.setString(i++,v.getAfterUseStatus()); s.setString(i++,v.getUserId()); s.setString(i++,v.getUserName()); s.setString(i++,v.getRunId()); s.setString(i++,v.getEquiptCondition()); s.setString(i++,v.getOrderQty()); s.setString(i++,v.getStartDate()); s.setString(i++,v.getStartTime()); s.setString(i++,v.getEndDate()); s.setString(i++,v.getEndTime()); s.setLong(i,id);
    }
    private static void setLong(PreparedStatement s,int i,Long v)throws SQLException{if(v==null)s.setNull(i,java.sql.Types.NUMERIC);else s.setLong(i,v.longValue());}
    private static void setDate(PreparedStatement s,int i,LocalDateTime v)throws SQLException{if(v==null)s.setNull(i,java.sql.Types.TIMESTAMP);else s.setTimestamp(i,Timestamp.valueOf(v));}
    private static void bind(PreparedStatement s,List<Object> values)throws SQLException{int i=1;for(Object v:values){if(v==null)s.setNull(i++,java.sql.Types.VARCHAR);else if(v instanceof Number)s.setLong(i++,((Number)v).longValue());else s.setString(i++,String.valueOf(v));}}
    private static Long getLong(ResultSet r,String c)throws SQLException{long v=r.getLong(c);return r.wasNull()?null:Long.valueOf(v);}
    private static LocalDateTime date(ResultSet r,String c)throws SQLException{Timestamp v=r.getTimestamp(c);return v==null?null:v.toLocalDateTime();}
    private static EmcEquiptUseRecordBean convert(ResultSet r)throws SQLException{EmcEquiptUseRecordBean b=new EmcEquiptUseRecordBean();b.setId(getLong(r,"ID"));b.setProjId(getLong(r,"PROJID"));b.setProjNo(r.getString("PROJNO"));b.setSampleId(getLong(r,"SAMPLEID"));b.setSampleNo(r.getString("SAMPLENO"));b.setTestId(r.getString("TESTID"));b.setTestName(r.getString("TESTNAME"));b.setEquiptId(getLong(r,"EQUIPTID"));b.setUsedTime(date(r,"USEDTIME"));b.setBeforeUseStatus(r.getString("BEFOREUSESTATUS"));b.setAfterUseStatus(r.getString("AFTERUSESTATUS"));b.setUserId(r.getString("USERID"));b.setUserName(r.getString("USERNAME"));b.setRunId(r.getString("RUNID"));b.setEquiptCondition(r.getString("EQUIPTCONDITION"));b.setOrderQty(r.getString("ORDERQTY"));b.setStartDate(r.getString("STARTDATE"));b.setStartTime(r.getString("STARTTIME"));b.setEndDate(r.getString("ENDDATE"));b.setEndTime(r.getString("ENDTIME"));b.setCreatedById(r.getString("CREATEDBYID"));b.setCreatedByName(r.getString("CREATEDBYNAME"));b.setCreatedTime(date(r,"CREATEDTIME"));b.setCreatedByOrgId(r.getString("CREATEDBYORGID"));b.setCreatedByOrgName(r.getString("CREATEDBYORGNAME"));return b;}
    private static Pagination<EmcEquiptUseRecordBean> empty(QueryRequest r){return new Pagination<EmcEquiptUseRecordBean>(0,r.getPageSize(),r.getPageNumber(),Collections.<EmcEquiptUseRecordBean>emptyList());}

    private OwnedPrincipal trustedReadPrincipal()throws SQLException{HttpServletRequest r=currentRequest();HttpSession s=r.getSession(false);boundary.requirePermission(s,OwnedIdentityProvider.EQUIPMENT_READ);return boundary.currentPrincipal(s);}
    private OwnedPrincipal trustedManagePrincipal()throws SQLException{HttpServletRequest r=currentRequest();HttpSession s=r.getSession(false);boundary.requirePermission(s,OwnedIdentityProvider.EQUIPMENT_MANAGE);return boundary.currentPrincipal(s);}
    private static HttpServletRequest currentRequest(){RequestAttributes a=RequestContextHolder.getRequestAttributes();if(!(a instanceof ServletRequestAttributes))throw denied();HttpServletRequest r=((ServletRequestAttributes)a).getRequest();if(r==null)throw denied();return r;}
    private static AccessDeniedException denied(){return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");}
    private static void requireEquipmentScope(Connection c,long id,Set<String> orgs)throws SQLException{if(orgs==null||orgs.isEmpty())throw denied();StringBuilder q=new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");int i=0;for(String ignored:orgs){if(i++>0)q.append(',');q.append('?');}q.append("))");try(PreparedStatement s=c.prepareStatement(q.toString())){i=1;s.setLong(i++,id);for(String o:orgs)s.setString(i++,o);try(ResultSet r=s.executeQuery()){if(!r.next())throw denied();}}}
    private static void requireRecordScope(Connection c,long id,long equipmentId,Set<String> orgs)throws SQLException{if(orgs==null||orgs.isEmpty())throw denied();StringBuilder q=new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT_USE_RECORD R JOIN T_EMC_EQUIPT E ON E.ID=R.EQUIPTID WHERE R.ID=? AND R.EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");int i=0;for(String ignored:orgs){if(i++>0)q.append(',');q.append('?');}q.append("))");try(PreparedStatement s=c.prepareStatement(q.toString())){i=1;s.setLong(i++,id);s.setLong(i++,equipmentId);for(String o:orgs)s.setString(i++,o);try(ResultSet r=s.executeQuery()){if(!r.next())throw denied();}}}
    private static boolean recordBelongsToScope(Connection c,long id,Set<String> orgs)throws SQLException{if(orgs==null||orgs.isEmpty())return false;StringBuilder q=new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT_USE_RECORD R JOIN T_EMC_EQUIPT E ON E.ID=R.EQUIPTID WHERE R.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION OP WHERE OP.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND OP.TARGETID=E.EQUIPTCATEGORYID AND OP.ORGID IN (");int i=0;for(String ignored:orgs){if(i++>0)q.append(',');q.append('?');}q.append("))");try(PreparedStatement s=c.prepareStatement(q.toString())){i=1;s.setLong(i++,id);for(String o:orgs)s.setString(i++,o);try(ResultSet r=s.executeQuery()){return r.next();}}}
    @Override public List<Map<String,Object>> selectByCondition(MapperParameter p){throw new UnsupportedOperationException("Equipment use record mapper query is not implemented");}
}
