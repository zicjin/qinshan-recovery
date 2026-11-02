package com.sunwayworld.owned.personnel;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.data.OwnedReadDao;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.data.PersonnelQueryService;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedReadRequest;
import com.sunwayworld.owned.internal.web.QueryRequest;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Bounded personnel adapter for the unchanged EmcEmpDao contract. Reads are
 * always available within the observed scope; qualification writes are only
 * enabled by the explicit loopback isolated-write configuration.
 */
public final class OwnedPersonnelReadDao extends MybatisDaoSupport<EmcEmpBean, String>
        implements EmcEmpDao, OwnedReadDao<EmcEmpBean, String> {
    private static final String PERSONNEL_READ = OwnedIdentityProvider.PERSONNEL_READ;
    private final PersonnelQueryService query;
    private final OwnedPersonnelAuxiliaryReadService auxiliary;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final AuditEventSink audit;
    private final Environment environment;

    public OwnedPersonnelReadDao(DataSource dataSource, JdbcPersonnelIdentity identity,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this(dataSource, identity, provider, boundary, audit, null);
    }

    public OwnedPersonnelReadDao(DataSource dataSource, JdbcPersonnelIdentity identity,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit,
            Environment environment) {
        this.dataSource = dataSource;
        this.query = new PersonnelQueryService(dataSource, identity);
        this.auxiliary = new OwnedPersonnelAuxiliaryReadService(dataSource, identity);
        this.provider = provider;
        this.boundary = boundary;
        this.audit = audit;
        this.environment = environment;
    }

    @Override
    public Page<EmcEmpBean> readPage(RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = trustedPrincipal();
            JdbcPersonnelIdentity.Principal delegate = provider.delegatePrincipal(principal);
            return convert(query.list(OwnedReadRequest.parse(normalizeQuery(wrapper)), delegate));
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel read failed", failure);
        }
    }

    /** Supplier's inherited employee resource ignores the generic q envelope for these reads. */
    private static RestJsonWrapperBean normalizeQuery(RestJsonWrapperBean wrapper) {
        if (wrapper == null || wrapper.getRequestJson() == null || wrapper.getRequestJson().trim().isEmpty()) return wrapper;
        try {
            JSONObject root = JSON.parseObject(wrapper.getRequestJson());
            if (root != null && root.containsKey("q")) {
                root.remove("q");
                return new RestJsonWrapperBean(root.toJSONString());
            }
        } catch (RuntimeException ignored) { return wrapper; }
        return wrapper;
    }

    @Override
    public EmcEmpBean readDetail(String id) {
        try {
            OwnedPrincipal principal = trustedPrincipal();
            JdbcPersonnelIdentity.Principal delegate = provider.delegatePrincipal(principal);
            Map<String, Object> row = query.detail(id, delegate);
            return row == null ? null : convert(row);
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel detail failed", failure);
        }
    }

    /**
     * Original selectable-list contract, restricted to the caller's employee
     * organizations. This is used by project/report choosers and never trusts
     * a client supplied organization hint.
     */
    public List<EmcEmpBean> readSelectableList() {
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) return new ArrayList<EmcEmpBean>();
            String sql = "SELECT A.* FROM T_EMC_EMP A WHERE A.ORGID IN (" + placeholders(organizations.size()) + ") ORDER BY A.ID";
            List<EmcEmpBean> result = new ArrayList<EmcEmpBean>();
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
                int index = 1;
                for (String organization : organizations) query.setString(index++, organization);
                try (ResultSet rows = query.executeQuery()) {
                    while (rows.next()) result.add(convert(resultRow(rows)));
                }
            }
            return result;
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel selectable list failed", failure);
        }
    }

    /** Original report-editor chooser enrichment over the scoped personnel page. */
    public Page<EmcEmpBean> readReportEditors(RestJsonWrapperBean wrapper) {
        Page<EmcEmpBean> page = readPage(wrapper);
        if (page.getRows().isEmpty()) return page;
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Map<String, Long> counts = unfinishedProjectCounts(page.getRows(), organizations);
            for (EmcEmpBean employee : page.getRows()) {
                Long count = counts.get(employee.getId());
                employee.setExt$Item("unfinishedProjectNum", String.valueOf(count == null ? 0L : count.longValue()));
            }
            return page;
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel report chooser failed", failure);
        }
    }

    /** Original sample chooser enrichment over folders planned for a date. */
    public Page<EmcEmpBean> readSampleEmployees(RestJsonWrapperBean wrapper) {
        Page<EmcEmpBean> page = readPage(wrapper);
        String planSampleDate = wrapper == null ? null : wrapper.getFilterValue("planSampleDate");
        if ((planSampleDate == null || planSampleDate.trim().isEmpty()) && wrapper != null) {
            QueryRequest request = OwnedReadRequest.parse(wrapper);
            Object value = request.getMapperParameters().get("planSampleDate");
            if (value != null) planSampleDate = String.valueOf(value);
        }
        if (planSampleDate == null || planSampleDate.trim().isEmpty() || page.getRows().isEmpty()) return page;
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Map<String, SampleProjects> projects = sampleProjects(page.getRows(), organizations, planSampleDate.trim());
            for (EmcEmpBean employee : page.getRows()) {
                SampleProjects value = projects.get(employee.getId());
                if (value == null || value.projectNumbers.isEmpty()) continue;
                employee.setExt$Item("projno", join(value.projectNumbers));
                employee.setExt$Item("projname", join(value.projectNames));
                // Preserve the original chooser's observable value.
                employee.setExt$Item("plansampledate", LocalDate.now().toString());
            }
            return page;
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel sample chooser failed", failure);
        }
    }

    private Map<String, Long> unfinishedProjectCounts(List<EmcEmpBean> employees, Set<String> organizations) throws SQLException {
        Map<String, Long> result = new HashMap<String, Long>();
        if (organizations == null || organizations.isEmpty()) return result;
        List<String> ids = employeeIds(employees);
        if (ids.isEmpty()) return result;
        String sql = "SELECT P.REPORTEDITUSERID,COUNT(1) FROM T_EMC_PROJECT P "
                + "WHERE P.REPORTEDITUSERID IN (" + placeholders(ids.size()) + ") "
                + "AND P.ORGID IN (" + placeholders(organizations.size()) + ") "
                + "AND (P.STATUS IS NULL OR P.STATUS <> 'cancel') "
                + "AND (P.STATUS IS NULL OR P.STATUS <> 'reportEdit' OR NOT EXISTS (SELECT 1 FROM T_EMC_REPORT R "
                + "WHERE R.PROJID=P.ID AND R.STATUS NOT IN ('draft','reportEdit'))) "
                + "GROUP BY P.REPORTEDITUSERID";
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            int index = 1;
            for (String id : ids) query.setString(index++, id);
            for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) result.put(rows.getString(1), Long.valueOf(rows.getLong(2)));
            }
        }
        return result;
    }

    private Map<String, SampleProjects> sampleProjects(List<EmcEmpBean> employees, Set<String> organizations,
            String planSampleDate) throws SQLException {
        Map<String, SampleProjects> result = new HashMap<String, SampleProjects>();
        if (organizations == null || organizations.isEmpty()) return result;
        String sql = "SELECT F.PLANSAMPLEUSERID,P.PROJNO,P.PROJNAME FROM T_EMC_FOLDER F "
                + "JOIN T_EMC_PROJECT P ON P.ID=F.PROJID "
                + "WHERE TO_CHAR(F.PLANSAMPLEDATE,'yyyy-MM-dd')=? AND F.PLANSAMPLEUSERID IS NOT NULL "
                + "AND P.ORGID IN (" + placeholders(organizations.size()) + ")";
        Set<String> allowed = new java.util.HashSet<String>(employeeIds(employees));
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
            int index = 1; query.setString(index++, planSampleDate);
            for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    String csv = rows.getString(1);
                    if (csv == null) continue;
                    for (String raw : csv.split(",")) {
                        String employeeId = raw.trim();
                        if (employeeId.isEmpty() || !allowed.contains(employeeId)) continue;
                        SampleProjects value = result.get(employeeId);
                        if (value == null) { value = new SampleProjects(); result.put(employeeId, value); }
                        addDistinct(value.projectNumbers, rows.getString(2));
                        addDistinct(value.projectNames, rows.getString(3));
                    }
                }
            }
        }
        return result;
    }

    private static List<String> employeeIds(List<EmcEmpBean> employees) {
        List<String> result = new ArrayList<String>();
        for (EmcEmpBean employee : employees) if (employee != null && employee.getId() != null && !result.contains(employee.getId())) result.add(employee.getId());
        return result;
    }

    private static void addDistinct(List<String> values, String value) {
        if (value != null && !value.trim().isEmpty() && !values.contains(value)) values.add(value);
    }

    private static String join(List<String> values) { return String.join(",", values); }

    private static final class SampleProjects {
        private final List<String> projectNumbers = new ArrayList<String>();
        private final List<String> projectNames = new ArrayList<String>();
    }

    /** Isolated Oracle write seam for the personnel master table. */
    public String insertEmployees(RestJsonWrapperBean wrapper) {
        requireIsolatedWrite();
        if (wrapper == null) throw new IllegalArgumentException("employee body is required");
        List<EmcEmpBean> values = wrapper.parse(EmcEmpBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("employee body is required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (principal.getOrgId() == null || !organizations.contains(principal.getOrgId())) throw denied();
            String first = OwnedWriteSupport.transaction(dataSource, connection -> {
                for (EmcEmpBean value : values) {
                    validateEmployee(value, null);
                    try (PreparedStatement insert = connection.prepareStatement(employeeInsertSql())) {
                        bindEmployeeInsert(insert, value, principal);
                        insert.executeUpdate();
                    }
                }
                return values.get(0).getId();
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-insert", principal.getId(), first,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return first;
        } catch (SQLException failure) { throw new IllegalStateException("personnel write failed", failure); }
    }

    /** Isolated Oracle master-table update; organization and audit columns are server-owned. */
    public void updateEmployee(String id, RestJsonWrapperBean wrapper) {
        requireIsolatedWrite();
        validateEmployeeId(id);
        if (wrapper == null) throw new IllegalArgumentException("employee body is required");
        try {
            EmcEmpBean value = wrapper.parseUnique(EmcEmpBean.class);
            validateEmployee(value, id);
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEmployeeScope(connection, id, organizations);
                try (PreparedStatement update = connection.prepareStatement(employeeUpdateSql())) {
                    bindEmployeeUpdate(update, value);
                    update.setString(16, id);
                    if (update.executeUpdate() != 1) throw denied();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-update", principal.getId(), id,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel update failed", failure); }
    }

    /** Isolated Oracle master-table delete; all requested rows must be in scope. */
    public void deleteEmployees(RestJsonWrapperBean wrapper) {
        requireIsolatedWrite();
        if (wrapper == null) throw new IllegalArgumentException("employee ids are required");
        List<String> ids = wrapper.parseId(String.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("employee ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (String id : ids) { validateEmployeeId(id); requireEmployeeScope(connection, id, organizations); }
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EMP WHERE ID=?")) {
                    for (String id : ids) { delete.setString(1, id); delete.addBatch(); }
                    delete.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-delete", principal.getId(), ids.get(0),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel delete failed", failure); }
    }

    /** Isolated visible-test assignment for the original employee detail tab. */
    public void insertTests(String employeeId, RestJsonWrapperBean wrapper) {
        validateEmployeeId(employeeId);
        if (wrapper == null) throw new IllegalArgumentException("visible-test body is required");
        List<EmcEmpTestBean> values = wrapper.parse(EmcEmpTestBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("visible-test body is required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEmployeeScope(connection, employeeId, organizations);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_EMP_TEST (ID,EMPID,TESTID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    for (EmcEmpTestBean value : values) {
                        validateTestId(value.getTestId());
                        insert.setLong(1, OwnedWriteSupport.nextIdentity(connection));
                        insert.setString(2, employeeId); insert.setLong(3, value.getTestId().longValue());
                        insert.setString(4, principal.getId()); insert.setString(5, principal.getId());
                        insert.setString(6, principal.getOrgId()); insert.setString(7, principal.getOrgName());
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-test-insert", principal.getId(), employeeId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel visible-test write failed", failure); }
    }

    /** Delete only visible-test rows belonging to the in-scope employee. */
    public void deleteTests(String employeeId, RestJsonWrapperBean wrapper) {
        validateEmployeeId(employeeId);
        if (wrapper == null) throw new IllegalArgumentException("visible-test ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("visible-test ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEmployeeScope(connection, employeeId, provider.permittedOrganizationIds(principal));
                for (Long id : ids) {
                    if (id == null || id.longValue() < 1L || !testBelongsTo(connection, id.longValue(), employeeId))
                        throw denied();
                }
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EMP_TEST WHERE ID=? AND EMPID=?")) {
                    for (Long id : ids) { delete.setLong(1, id.longValue()); delete.setString(2, employeeId); delete.addBatch(); }
                    delete.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-test-delete", principal.getId(), employeeId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel visible-test delete failed", failure); }
    }

    /** Isolated training insert for the original personnel detail tab. */
    public void insertTraining(String employeeId, RestJsonWrapperBean wrapper) {
        validateEmployeeId(employeeId);
        if (wrapper == null) throw new IllegalArgumentException("training body is required");
        List<EmcEmpTrainBean> values = wrapper.parse(EmcEmpTrainBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("training body is required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEmployeeScope(connection, employeeId, provider.permittedOrganizationIds(principal));
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_EMP_TRAIN (ID,EMPID,TRAINTIME,TRAINCONTENT,TRAINRESULT,COURSECODE,COURSENAME,TRAINDEPT,TRAINDEPTCODE,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,TRAINBEGINTIME,TRAINENDTIME) VALUES (?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?,?,?)")) {
                    for (EmcEmpTrainBean value : values) {
                        validateTraining(value);
                        int index = 1;
                        insert.setLong(index++, OwnedWriteSupport.nextIdentity(connection));
                        insert.setString(index++, employeeId); setDate(insert, index++, value.getTrainTime());
                        insert.setString(index++, value.getTrainContent()); insert.setString(index++, value.getTrainResult());
                        insert.setString(index++, value.getCourseCode()); insert.setString(index++, value.getCourseName());
                        insert.setString(index++, value.getTrainDept()); insert.setString(index++, value.getTrainDeptCode());
                        insert.setString(index++, principal.getId()); insert.setString(index++, principal.getId());
                        insert.setString(index++, principal.getOrgId()); insert.setString(index++, principal.getOrgName());
                        setDate(insert, index++, value.getTrainBeginTime()); setDate(insert, index++, value.getTrainEndTime());
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-training-insert", principal.getId(), employeeId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel training write failed", failure); }
    }

    /** Delete only training rows attached to the requested in-scope employee. */
    public void deleteTraining(String employeeId, RestJsonWrapperBean wrapper) {
        validateEmployeeId(employeeId);
        if (wrapper == null) throw new IllegalArgumentException("training ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("training ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEmployeeScope(connection, employeeId, provider.permittedOrganizationIds(principal));
                for (Long id : ids) if (id == null || id.longValue() < 1L || !trainingBelongsTo(connection, id.longValue(), employeeId)) throw denied();
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EMP_TRAIN WHERE ID=? AND EMPID=?")) {
                    for (Long id : ids) { delete.setLong(1, id.longValue()); delete.setString(2, employeeId); delete.addBatch(); }
                    delete.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-training-delete", principal.getId(), employeeId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel training delete failed", failure); }
    }

    private static void validateTraining(EmcEmpTrainBean value) {
        if (value == null) throw new IllegalArgumentException("training body is required");
        if (value.getTrainContent() != null && value.getTrainContent().length() > 200) throw new IllegalArgumentException("training content is too long");
        if (value.getTrainResult() != null && value.getTrainResult().length() > 100) throw new IllegalArgumentException("training result is too long");
        if (value.getCourseCode() != null && value.getCourseCode().length() > 64) throw new IllegalArgumentException("course code is too long");
        if (value.getCourseName() != null && value.getCourseName().length() > 128) throw new IllegalArgumentException("course name is too long");
        if (value.getTrainDept() != null && value.getTrainDept().length() > 128) throw new IllegalArgumentException("training department is too long");
        if (value.getTrainDeptCode() != null && value.getTrainDeptCode().length() > 64) throw new IllegalArgumentException("training department code is too long");
    }

    private static boolean trainingBelongsTo(Connection connection, long id, String employeeId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT EMPID FROM T_EMC_EMP_TRAIN WHERE ID=?")) {
            query.setLong(1, id);
            try (ResultSet rows = query.executeQuery()) { return rows.next() && employeeId.equals(rows.getString(1)); }
        }
    }

    private static void setDate(PreparedStatement statement, int index, LocalDate value) throws SQLException {
        if (value == null) statement.setDate(index, null); else statement.setDate(index, Date.valueOf(value));
    }

    private static void validateTestId(Long testId) {
        if (testId == null || testId.longValue() < 1L) throw new IllegalArgumentException("visible-test id is invalid");
    }

    private static boolean testBelongsTo(Connection connection, long id, String employeeId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT EMPID FROM T_EMC_EMP_TEST WHERE ID=?")) {
            query.setLong(1, id);
            try (ResultSet rows = query.executeQuery()) { return rows.next() && employeeId.equals(rows.getString(1)); }
        }
    }

    private void requireIsolatedWrite() {
        if (environment == null || !"isolated".equals(environment.getProperty("owned.personnel.write.mode")))
            throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    /** Uses the same gate as writes, including a concrete destination organization. */
    public void requireMainWriteAvailable() throws SQLException {
        OwnedPrincipal principal = trustedWritePrincipal();
        if (principal.getOrgId() == null || !provider.permittedOrganizationIds(principal).contains(principal.getOrgId()))
            throw denied();
    }

    /** Original add flow chooses an existing, unassigned account in the destination organization. */
    public List<Map<String, String>> unassignedAccounts() throws SQLException {
        OwnedPrincipal principal = trustedWritePrincipal();
        if (principal.getOrgId() == null || !provider.permittedOrganizationIds(principal).contains(principal.getOrgId()))
            throw denied();
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(
                "SELECT U.ID,U.ID FROM T_CORE_USER U WHERE U.ORGID=? AND U.STATUS='activated' "
                + "AND U.LASTSUSPENDEDFLAG='0' AND (U.EXPIRYDATE IS NULL OR U.EXPIRYDATE>CURRENT_TIMESTAMP) "
                + "AND NOT EXISTS (SELECT 1 FROM T_EMC_EMP E WHERE E.ID=U.ID) ORDER BY U.ID")) {
            query.setString(1, principal.getOrgId());
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    Map<String, String> account = new LinkedHashMap<String, String>();
                    account.put("id", rows.getString(1)); account.put("userName", rows.getString(2));
                    result.add(account);
                }
            }
        }
        return result;
    }

    /** Personnel picker projection of the historical test-method target_unassigned query. */
    public Page<Map<String, String>> unassignedTests(String employeeId, int pageNumber, int pageSize, String name)
            throws SQLException {
        validateEmployeeId(employeeId);
        if (pageNumber < 1 || pageNumber > 100000 || pageSize < 1 || pageSize > 100
                || name != null && name.length() > 100) throw new IllegalArgumentException("invalid test selection query");
        OwnedPrincipal principal = trustedWritePrincipal();
        String from = " FROM T_EMC_TEST_METHOD B LEFT JOIN T_EMC_TEST_CATEGORY C ON B.TESTCATEGORYID=C.ID "
                + "LEFT JOIN T_EMC_TEST D ON B.TESTID=D.ID LEFT JOIN T_EMC_METHOD M ON B.METHODID=M.ID "
                + "WHERE NOT EXISTS (SELECT 1 FROM T_EMC_EMP_TEST E WHERE E.TESTID=B.ID AND E.EMPID=?) "
                + "AND (? IS NULL OR INSTR(UPPER(D.TESTNAME),UPPER(?))>0)";
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        try (Connection connection = dataSource.getConnection()) {
            requireEmployeeScope(connection, employeeId, provider.permittedOrganizationIds(principal));
            int total;
            try (PreparedStatement count = connection.prepareStatement("SELECT COUNT(*)" + from)) {
                count.setString(1, employeeId); count.setString(2, name); count.setString(3, name);
                try (ResultSet rows = count.executeQuery()) { rows.next(); total = rows.getInt(1); }
            }
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT B.ID,D.TESTNAME,C.TESTCATEGORYDESC,M.METHODNAME,M.TESTMETHODBASIS" + from
                            + " ORDER BY B.ID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
                query.setString(1, employeeId); query.setString(2, name); query.setString(3, name);
                query.setInt(4, (pageNumber - 1) * pageSize); query.setInt(5, pageSize);
                try (ResultSet rows = query.executeQuery()) {
                    while (rows.next()) {
                        Map<String, String> item = new LinkedHashMap<String, String>();
                        item.put("id", rows.getString(1)); item.put("testName", rows.getString(2));
                        item.put("testCategory", rows.getString(3)); item.put("methodName", rows.getString(4));
                        item.put("methodBasis", rows.getString(5)); result.add(item);
                    }
                }
            }
            return new Pagination<Map<String, String>>(total, pageSize, pageNumber, result);
        }
    }

    private static String employeeInsertSql() {
        // The cloned production table derives the display organization name
        // through T_CORE_ORG and does not contain a denormalized ORGNAME
        // column.  Keep the write aligned with that schema; the query seam
        // restores the historical ORGNAME projection from the organization
        // join when needed.
        return "INSERT INTO T_EMC_EMP (ID,USERNAME,BIRTHDAY,AGE,ORGID,MOBILE,SEX,FILTERTEST,PHONE,NATION,STARTWORKDATE,TITLEDATE,PROFESSIONALTITLE,POSITION,REMARK,ELECTRONICSIGNATURE,PIC,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)";
    }

    private static String employeeUpdateSql() {
        return "UPDATE T_EMC_EMP SET USERNAME=?,BIRTHDAY=?,AGE=?,MOBILE=?,SEX=?,FILTERTEST=?,PHONE=?,NATION=?,STARTWORKDATE=?,TITLEDATE=?,PROFESSIONALTITLE=?,POSITION=?,REMARK=?,ELECTRONICSIGNATURE=?,PIC=? WHERE ID=?";
    }

    private static void bindEmployeeInsert(PreparedStatement statement, EmcEmpBean value, OwnedPrincipal principal)
            throws SQLException {
        int index = 1;
        statement.setString(index++, value.getId());
        statement.setString(index++, value.getUserName());
        if (value.getBirthday() == null) statement.setDate(index++, null); else statement.setDate(index++, Date.valueOf(value.getBirthday()));
        if (value.getAge() == null) statement.setObject(index++, null); else statement.setInt(index++, value.getAge().intValue());
        statement.setString(index++, principal.getOrgId());
        statement.setString(index++, value.getMobile()); statement.setString(index++, value.getSex());
        statement.setString(index++, value.getFilterTest()); statement.setString(index++, value.getPhone());
        statement.setString(index++, value.getNation());
        if (value.getStartWorkDate() == null) statement.setDate(index++, null); else statement.setDate(index++, Date.valueOf(value.getStartWorkDate()));
        if (value.getTitleDate() == null) statement.setDate(index++, null); else statement.setDate(index++, Date.valueOf(value.getTitleDate()));
        statement.setString(index++, value.getProfessionalTitle()); statement.setString(index++, value.getPosition());
        statement.setString(index++, value.getRemark()); statement.setString(index++, value.getElectronicSignature());
        statement.setString(index++, value.getPic());
        statement.setString(index++, principal.getId()); statement.setString(index++, principal.getId());
        statement.setString(index++, principal.getOrgId()); statement.setString(index, principal.getOrgName());
    }

    private static void bindEmployeeUpdate(PreparedStatement statement, EmcEmpBean value) throws SQLException {
        int index = 1;
        statement.setString(index++, value.getUserName());
        if (value.getBirthday() == null) statement.setDate(index++, null); else statement.setDate(index++, Date.valueOf(value.getBirthday()));
        if (value.getAge() == null) statement.setObject(index++, null); else statement.setInt(index++, value.getAge().intValue());
        statement.setString(index++, value.getMobile()); statement.setString(index++, value.getSex());
        statement.setString(index++, value.getFilterTest()); statement.setString(index++, value.getPhone());
        statement.setString(index++, value.getNation());
        if (value.getStartWorkDate() == null) statement.setDate(index++, null); else statement.setDate(index++, Date.valueOf(value.getStartWorkDate()));
        if (value.getTitleDate() == null) statement.setDate(index++, null); else statement.setDate(index++, Date.valueOf(value.getTitleDate()));
        statement.setString(index++, value.getProfessionalTitle()); statement.setString(index++, value.getPosition());
        statement.setString(index++, value.getRemark()); statement.setString(index++, value.getElectronicSignature());
        statement.setString(index, value.getPic());
    }

    private static void validateEmployee(EmcEmpBean value, String expectedId) {
        if (value == null) throw new IllegalArgumentException("employee body is required");
        validateEmployeeId(value.getId());
        if (expectedId != null && !expectedId.equals(value.getId())) throw new IllegalArgumentException("employee id mismatch");
        if (value.getUserName() == null || value.getUserName().trim().isEmpty() || value.getUserName().length() > 64)
            throw new IllegalArgumentException("employee name is required");
        if (value.getAge() != null && (value.getAge().intValue() < 0 || value.getAge().intValue() > 200))
            throw new IllegalArgumentException("employee age is invalid");
        if (value.getMobile() != null && value.getMobile().length() > 32) throw new IllegalArgumentException("employee mobile is too long");
        if (value.getSex() != null && value.getSex().length() > 32) throw new IllegalArgumentException("employee sex is too long");
        if (value.getRemark() != null && value.getRemark().length() > 512) throw new IllegalArgumentException("employee remark is too long");
    }

    private static void validateEmployeeId(String id) {
        if (id == null || !id.matches("[A-Za-z0-9_-]{1,64}")) throw new IllegalArgumentException("employee id is invalid");
    }

    /** Isolated Oracle write seam for the original qualification endpoint. */
    public Long insertQualification(String employeeId, RestJsonWrapperBean wrapper) {
        if (employeeId == null || !employeeId.matches("[A-Za-z0-9_-]{1,64}"))
            throw new IllegalArgumentException("employee id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("qualification body is required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            EmcEmpQualBean value = wrapper.parseUnique(EmcEmpQualBean.class);
            validateQualification(value);
            long insertedId = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEmployeeScope(connection, employeeId, provider.permittedOrganizationIds(principal));
                long id = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_EMP_QUAL (ID,EMPID,QUALNO,QUALNAME,QUALORG,VALIDDATE,AUTHDATE,EXPIREDATE,WARNINGDAYS,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) "
                                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    int index = 1;
                    insert.setLong(index++, id); insert.setString(index++, employeeId);
                    insert.setString(index++, value.getQualNo()); insert.setString(index++, value.getQualName());
                    insert.setString(index++, value.getQualOrg());
                    if (value.getValidDate() == null) insert.setDate(index++, null); else insert.setDate(index++, Date.valueOf(value.getValidDate()));
                    if (value.getAuthDate() == null) insert.setDate(index++, null); else insert.setDate(index++, Date.valueOf(value.getAuthDate()));
                    if (value.getExpireDate() == null) insert.setDate(index++, null); else insert.setDate(index++, Date.valueOf(value.getExpireDate()));
                    if (value.getWarningDays() == null) insert.setLong(index++, 0L); else insert.setLong(index++, value.getWarningDays().longValue());
                    insert.setString(index++, principal.getId()); insert.setString(index++, principal.getId());
                    insert.setString(index++, principal.getOrgId()); insert.setString(index, principal.getOrgName());
                    insert.executeUpdate();
                }
                return Long.valueOf(id);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-qualification-insert", principal.getId(), employeeId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return insertedId;
        } catch (SQLException failure) { throw new IllegalStateException("personnel qualification write failed", failure); }
    }

    /** Delete only qualification rows owned by the requested in-scope employee. */
    public void deleteQualifications(String employeeId, RestJsonWrapperBean wrapper) {
        if (employeeId == null || !employeeId.matches("[A-Za-z0-9_-]{1,64}"))
            throw new IllegalArgumentException("employee id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("qualification ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("qualification ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEmployeeScope(connection, employeeId, provider.permittedOrganizationIds(principal));
                for (Long id : ids) {
                    if (id == null || !qualificationBelongsTo(connection, id.longValue(), employeeId)) {
                        throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                    }
                }
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EMP_QUAL WHERE ID=? AND EMPID=?")) {
                    for (Long id : ids) { delete.setLong(1, id.longValue()); delete.setString(2, employeeId); delete.addBatch(); }
                    delete.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-qualification-delete", principal.getId(), employeeId,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel qualification delete failed", failure); }
    }

    /** Isolated Oracle write seam for the original certificate-method route. */
    public void insertCertTestMethods(Long certId, RestJsonWrapperBean wrapper) {
        if (certId == null || certId.longValue() < 1L) throw new IllegalArgumentException("certificate id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("certificate method body is required");
        List<EmcCertTestMethodBean> values = wrapper.parse(EmcCertTestMethodBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("certificate method body is required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireCredentialScope(connection, certId.longValue(), provider.permittedOrganizationIds(principal));
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_CERT_TEST_METHOD (ID,CERTID,TESTID,METHODID,REMARK,TESTMETHODID,ITEMGROUP,TYPE,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                    for (EmcCertTestMethodBean value : values) {
                        if (value == null || value.getTestMethodId() == null || value.getTestMethodId().longValue() < 1L)
                            throw new IllegalArgumentException("test method id is invalid");
                        int index = 1;
                        insert.setLong(index++, OwnedWriteSupport.nextIdentity(connection)); insert.setLong(index++, certId.longValue());
                        setLong(insert, index++, value.getTestId()); setLong(insert, index++, value.getMethodId());
                        insert.setString(index++, value.getRemark()); setLong(insert, index++, value.getTestMethodId());
                        insert.setString(index++, value.getItemGroup()); insert.setString(index++, value.getType());
                        insert.setString(index++, principal.getId()); insert.setString(index++, principal.getId());
                        insert.setString(index++, principal.getOrgId()); insert.setString(index, principal.getOrgName());
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-cert-test-method-insert", principal.getId(), String.valueOf(certId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel certificate method write failed", failure); }
    }

    /** Delete certificate-method rows only when their certificate is in scope. */
    public void deleteCertTestMethods(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("certificate method ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("certificate method ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            OwnedWriteSupport.transaction(dataSource, connection -> {
                Set<String> organizations = provider.permittedOrganizationIds(principal);
                for (Long id : ids) {
                    if (id == null || id.longValue() < 1L) throw new IllegalArgumentException("certificate method id is invalid");
                    String sql = "SELECT 1 FROM T_EMC_CERT_TEST_METHOD M JOIN T_EMC_CERT C ON C.ID=M.CERTID JOIN T_EMC_EMP E ON E.ID=C.EMPID WHERE M.ID=? AND E.ORGID IN (" + placeholders(organizations.size()) + ")";
                    try (PreparedStatement query = connection.prepareStatement(sql)) {
                        int index = 1; query.setLong(index++, id.longValue()); for (String organization : organizations) query.setString(index++, organization);
                        try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); }
                    }
                }
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_CERT_TEST_METHOD WHERE ID=?")) {
                    for (Long id : ids) { delete.setLong(1, id.longValue()); delete.addBatch(); }
                    delete.executeBatch();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "personnel-cert-test-method-delete", principal.getId(), String.valueOf(ids.get(0)), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("personnel certificate method delete failed", failure); }
    }

    private OwnedPrincipal trustedWritePrincipal() throws SQLException {
        HttpServletRequest request = currentRequest();
        requireIsolatedWrite();
        OwnedWriteSupport.requireLoopback(request);
        HttpSession session = request.getSession(false);
        boundary.requirePermission(session, PERSONNEL_READ);
        return boundary.currentPrincipal(session);
    }

    private static void setLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.NUMERIC); else statement.setLong(index, value.longValue());
    }

    private static String placeholders(int count) {
        if (count < 1) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) { if (i > 0) result.append(','); result.append('?'); }
        return result.toString();
    }

    private static void requireCredentialScope(Connection connection, long certId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw denied();
        String sql = "SELECT 1 FROM T_EMC_CERT C JOIN T_EMC_EMP E ON E.ID=C.EMPID WHERE C.ID=? AND E.ORGID IN (" + placeholders(organizations.size()) + ")";
        try (PreparedStatement query = connection.prepareStatement(sql)) {
            int index = 1; query.setLong(index++, certId); for (String organization : organizations) query.setString(index++, organization);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw denied(); }
        }
    }

    private static void validateQualification(EmcEmpQualBean value) {
        if (value == null || value.getQualName() == null || value.getQualName().trim().isEmpty()
                || value.getQualName().length() > 200) throw new IllegalArgumentException("qualification name is required");
        if (value.getQualNo() != null && value.getQualNo().length() > 100) throw new IllegalArgumentException("qualification number is too long");
        if (value.getQualOrg() != null && value.getQualOrg().length() > 200) throw new IllegalArgumentException("qualification issuer is too long");
        if (value.getWarningDays() != null && (value.getWarningDays().longValue() < 0 || value.getWarningDays().longValue() > 3650))
            throw new IllegalArgumentException("warning days are out of range");
    }

    private static void requireEmployeeScope(Connection connection, String employeeId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EMP WHERE ID=? AND ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append(')');
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setString(index++, employeeId); for (String org : organizations) query.setString(index++, org);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
        }
    }

    private static boolean qualificationBelongsTo(Connection connection, long id, String employeeId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT EMPID FROM T_EMC_EMP_QUAL WHERE ID=?")) {
            query.setLong(1, id);
            try (ResultSet rows = query.executeQuery()) { return rows.next() && employeeId.equals(rows.getString(1)); }
        }
    }

    public com.sunwayworld.framework.data.page.Page<com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean>
            readQualifications(String id, RestJsonWrapperBean wrapper) {
        try {
            JdbcPersonnelIdentity.Principal principal = trustedDelegate();
            return auxiliary.qualifications(id, OwnedReadRequest.parse(wrapper), principal);
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel qualification read failed", failure);
        }
    }

    public com.sunwayworld.framework.data.page.Page<com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean>
            readTests(String id, RestJsonWrapperBean wrapper) {
        try {
            JdbcPersonnelIdentity.Principal principal = trustedDelegate();
            return auxiliary.tests(id, OwnedReadRequest.parse(wrapper), principal);
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel visible-test read failed", failure);
        }
    }

    public com.sunwayworld.framework.data.page.Page<EmcEmpTrainBean>
            readTraining(String id, RestJsonWrapperBean wrapper) {
        try {
            JdbcPersonnelIdentity.Principal principal = trustedDelegate();
            return auxiliary.training(id, OwnedReadRequest.parse(wrapper), principal);
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel training read failed", failure);
        }
    }

    public com.sunwayworld.framework.data.page.Page<com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean>
            readCredentials(String id, RestJsonWrapperBean wrapper) {
        try {
            JdbcPersonnelIdentity.Principal principal = trustedDelegate();
            return auxiliary.credentials(id, OwnedReadRequest.parse(wrapper), principal);
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel credential read failed", failure);
        }
    }

    public com.sunwayworld.framework.data.page.Page<com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean>
            readMethods(Long id, RestJsonWrapperBean wrapper) {
        try {
            JdbcPersonnelIdentity.Principal principal = trustedDelegate();
            return auxiliary.methods(id, OwnedReadRequest.parse(wrapper), principal);
        } catch (SQLException failure) {
            throw new IllegalStateException("personnel method read failed", failure);
        }
    }

    private OwnedPrincipal trustedPrincipal() throws SQLException {
        HttpServletRequest request = currentRequest();
        HttpSession session = request.getSession(false);
        boundary.requirePermission(session, PERSONNEL_READ);
        return boundary.currentPrincipal(session);
    }

    private JdbcPersonnelIdentity.Principal trustedDelegate() throws SQLException {
        return provider.delegatePrincipal(trustedPrincipal());
    }

    private static HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) throw denied();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        if (request == null) throw denied();
        return request;
    }

    private static AccessDeniedException denied() {
        return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    private static Page<EmcEmpBean> convert(Pagination<Map<String, Object>> page) {
        List<EmcEmpBean> rows = new ArrayList<EmcEmpBean>();
        for (Map<String, Object> row : page.getRows()) rows.add(convert(row));
        Pagination<EmcEmpBean> result = new Pagination<EmcEmpBean>(page.getTotal(), page.getPageSize(),
                page.getPageNumber(), rows, page.getTotalMap());
        result.setColumnTypeMap(page.getColumnTypeMap());
        return result;
    }

    private static Map<String, Object> resultRow(ResultSet rows) throws SQLException {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        ResultSetMetaData metadata = rows.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if (label != null && !"RN".equalsIgnoreCase(label)) {
                result.put(label.toUpperCase(Locale.ROOT), JdbcValue.normalize(rows.getObject(i)));
            }
        }
        return result;
    }

    private static EmcEmpBean convert(Map<String, Object> row) {
        EmcEmpBean bean = new EmcEmpBean();
        Map<String, Method> setters = setters();
        Map<String, Object> extensions = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            if (key == null || "RN".equalsIgnoreCase(key)) continue;
            Method setter = setters.get(normalize(key));
            Object value = entry.getValue();
            if (setter == null) {
                if (value != null) extensions.put(key, value);
                continue;
            }
            if (value == null) continue;
            try {
                setter.invoke(bean, convertValue(value, setter.getParameterTypes()[0]));
            } catch (IllegalAccessException failure) {
                throw new IllegalStateException("personnel DTO setter inaccessible: " + key, failure);
            } catch (InvocationTargetException failure) {
                throw new IllegalStateException("personnel DTO setter failed: " + key, failure.getCause());
            } catch (RuntimeException failure) {
                throw new IllegalStateException("personnel DTO conversion failed: " + key, failure);
            }
        }
        for (Map.Entry<String, Object> extension : extensions.entrySet()) {
            bean.setExt$Item(extension.getKey(), String.valueOf(extension.getValue()));
        }
        return bean;
    }

    private static Map<String, Method> setters() {
        Map<String, Method> result = new HashMap<String, Method>();
        for (Method method : EmcEmpBean.class.getMethods()) {
            if (!method.getName().startsWith("set") || method.getParameterTypes().length != 1) continue;
            if ("setExt$".equals(method.getName()) || "setExt$Item".equals(method.getName())) continue;
            String property = method.getName().substring(3);
            result.put(normalize(property), method);
        }
        return result;
    }

    private static String normalize(String key) {
        return key.replace("_", "").replace(".", "").toUpperCase(Locale.ROOT);
    }

    private static Object convertValue(Object value, Class<?> target) {
        if (target.isInstance(value)) return value;
        if (target == String.class) return String.valueOf(value);
        if (target == Integer.class || target == Integer.TYPE) return ((Number) number(value)).intValue();
        if (target == Long.class || target == Long.TYPE) return ((Number) number(value)).longValue();
        if (target == Short.class || target == Short.TYPE) return ((Number) number(value)).shortValue();
        if (target == Double.class || target == Double.TYPE) return ((Number) number(value)).doubleValue();
        if (target == Float.class || target == Float.TYPE) return ((Number) number(value)).floatValue();
        if (target == LocalDateTime.class) {
            if (value instanceof Timestamp) return ((Timestamp) value).toLocalDateTime();
            if (value instanceof java.util.Date) return new Timestamp(((java.util.Date) value).getTime()).toLocalDateTime();
            String text = String.valueOf(value).trim();
            try { return LocalDateTime.parse(text); }
            catch (java.time.format.DateTimeParseException ignored) {
                return Timestamp.valueOf(text.replace('T', ' ')).toLocalDateTime();
            }
        }
        if (target == LocalDate.class) {
            if (value instanceof Date) return ((Date) value).toLocalDate();
            if (value instanceof Timestamp) return ((Timestamp) value).toLocalDateTime().toLocalDate();
            if (value instanceof java.util.Date) return new Date(((java.util.Date) value).getTime()).toLocalDate();
            String text = String.valueOf(value).trim();
            try { return LocalDate.parse(text); }
            catch (java.time.format.DateTimeParseException ignored) {
                return Timestamp.valueOf(text.replace('T', ' ')).toLocalDateTime().toLocalDate();
            }
        }
        if (target.isEnum()) return Enum.valueOf(target.asSubclass(Enum.class), String.valueOf(value));
        throw new IllegalArgumentException("unsupported DTO conversion to " + target.getName());
    }

    private static Number number(Object value) {
        if (value instanceof Number) return (Number) value;
        return new java.math.BigDecimal(String.valueOf(value));
    }
}
