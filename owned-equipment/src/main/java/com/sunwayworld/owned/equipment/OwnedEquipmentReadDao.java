package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.owned.internal.data.OwnedReadDao;
import com.sunwayworld.owned.internal.data.EquipmentQueryService;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import com.sunwayworld.owned.internal.web.OwnedReadRequest;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.QueryRequest;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Bounded equipment adapter for the unchanged EmcEquiptDao contract.
 * Reads are always available within the observed scope; only the explicit
 * loopback isolated test-method write seam is enabled.
 */
public final class OwnedEquipmentReadDao extends MybatisDaoSupport<EmcEquiptBean, Long>
        implements EmcEquiptDao, OwnedReadDao<EmcEquiptBean, Long> {
    private final EquipmentQueryService query;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final AuditEventSink audit;

    public OwnedEquipmentReadDao(DataSource dataSource, JdbcPersonnelIdentity identity,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit) {
        this.dataSource = dataSource;
        this.query = new EquipmentQueryService(dataSource, identity);
        this.provider = provider;
        this.boundary = boundary;
        this.audit = audit;
    }

    @Override
    public Page<EmcEquiptBean> readPage(RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal = trustedPrincipal();
            JdbcPersonnelIdentity.Principal delegate = provider.delegatePrincipal(principal);
            return convert(query.list(OwnedReadRequest.parse(wrapper), delegate));
        } catch (SQLException failure) {
            throw new IllegalStateException("equipment read failed", failure);
        }
    }

    @Override
    public EmcEquiptBean readDetail(Long id) {
        try {
            OwnedPrincipal principal = trustedPrincipal();
            JdbcPersonnelIdentity.Principal delegate = provider.delegatePrincipal(principal);
            Map<String, Object> row = query.detail(id, delegate);
            return row == null ? null : convert(row);
        } catch (SQLException failure) {
            throw new IllegalStateException("equipment detail failed", failure);
        }
    }

    /** Read the original DCU configuration detail for an authorized equipment. */
    public EmcEquiptDCUConfBean readDcuConfig(Long equipmentId) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) return null;
            List<Object> values = new ArrayList<Object>();
            String scope = categoryScopeSql("E", organizations, values);
            values.add(equipmentId);
            String sql = "SELECT D.ID,D.ACQSCRIPTID,D.INTERFACECATEGORY,D.BAUDRATE,D.TERMINATOR,D.DATABIT,D.PARITYCHECK,"
                    + "D.COM,D.STOPBIT,D.BEGINSYMBOL,D.ENDSYMBOL,D.ANALYTICFORMULA,D.IP,D.CREATEDBYID,D.CREATEDBYNAME,"
                    + "D.CREATEDTIME,D.CREATEDBYORGID,D.CREATEDBYORGNAME FROM T_EMC_EQUIPT_DCU_CONF D JOIN T_EMC_EQUIPT E ON E.ID=D.ID "
                    + "WHERE " + scope + " AND D.ID=?";
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
                bindValues(query, values);
                try (ResultSet rows = query.executeQuery()) { return rows.next() ? dcu(rows) : null; }
            }
        } catch (SQLException failure) { throw new IllegalStateException("equipment DCU configuration read failed", failure); }
    }

    /** Validate that an authorized equipment has at least one DCU test item. */
    public RestValidationResultBean validateDcuConfig(Long equipmentId) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) return new RestValidationResultBean(Boolean.FALSE,
                    "EMC.MODULE.BP.RESULT.RUNS.TIP.EQUIPT_HAS_NO_DCU_CONFIG");
            List<Object> values = new ArrayList<Object>();
            String scope = categoryScopeSql("E", organizations, values);
            values.add(equipmentId);
            String sql = "SELECT COUNT(1) FROM T_EMC_EQUIPT_TEST_ITEM I JOIN T_EMC_EQUIPT E ON E.ID=I.EQUIPTID WHERE "
                    + scope + " AND I.EQUIPTID=?";
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql)) {
                bindValues(query, values);
                try (ResultSet rows = query.executeQuery()) {
                    rows.next();
                    return rows.getInt(1) > 0 ? new RestValidationResultBean(Boolean.TRUE)
                            : new RestValidationResultBean(Boolean.FALSE, "EMC.MODULE.BP.RESULT.RUNS.TIP.EQUIPT_HAS_NO_DCU_CONFIG");
                }
            }
        } catch (SQLException failure) { throw new IllegalStateException("equipment DCU configuration validation failed", failure); }
    }

    /** Read the original test-method rows for an authorized equipment. */
    public Page<EmcEquiptTestMethodBean> readTestMethods(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            rejectAuxiliaryQuery(request, "test-method");
            if (organizations == null || organizations.isEmpty()) return new Pagination<EmcEquiptTestMethodBean>(
                    0, request.getPageSize(), request.getPageNumber(), new ArrayList<EmcEquiptTestMethodBean>());
            List<Object> values = new ArrayList<Object>();
            String scope = equipmentScopeSql(equipmentId.longValue(), organizations, values, "M");
            String from = "FROM T_EMC_EQUIPT_TEST_METHOD M WHERE " + scope;
            int start = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int end = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total = count(connection, "SELECT COUNT(1) " + from, values);
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(start); pageValues.add(end);
                String sql = "SELECT * FROM (SELECT M.ID,M.EQUIPTID,M.TESTCATEGORYID,M.TESTMETHODID,M.TESTID,M.METHODID,"
                        + "M.CURVENO,M.CURVEID,M.ORGID,M.ANALYSISCATEGORY,M.DEFAULTFLAG,M.CREATEDBYID,M.CREATEDBYNAME,"
                        + "M.CREATEDTIME,M.CREATEDBYORGID,M.CREATEDBYORGNAME,ROW_NUMBER() OVER (ORDER BY M.ID DESC) RN "
                        + from + ") WHERE RN BETWEEN ? AND ? ORDER BY RN";
                List<EmcEquiptTestMethodBean> rows = new ArrayList<EmcEquiptTestMethodBean>();
                try (PreparedStatement select = connection.prepareStatement(sql)) {
                    bindValues(select, pageValues);
                    try (ResultSet result = select.executeQuery()) { while (result.next()) rows.add(testMethod(result)); }
                }
                return new Pagination<EmcEquiptTestMethodBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (SQLException failure) { throw new IllegalStateException("equipment test-method read failed", failure); }
    }

    /** Read the original test-item rows for an authorized equipment. */
    public Page<EmcEquiptTestItemBean> readTestItems(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            rejectAuxiliaryQuery(request, "test-item");
            if (organizations == null || organizations.isEmpty()) return new Pagination<EmcEquiptTestItemBean>(
                    0, request.getPageSize(), request.getPageNumber(), new ArrayList<EmcEquiptTestItemBean>());
            List<Object> values = new ArrayList<Object>();
            String scope = equipmentScopeSql(equipmentId.longValue(), organizations, values, "I");
            String from = "FROM T_EMC_EQUIPT_TEST_ITEM I WHERE " + scope;
            int start = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int end = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total = count(connection, "SELECT COUNT(1) " + from, values);
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(start); pageValues.add(end);
                String sql = "SELECT * FROM (SELECT I.ID,I.EQUIPTID,I.ITEMID,I.TESTMETHODID,I.EASRESULTID,"
                        + "I.MATCHITEMNAME,I.MATCHRESULTNAME,I.CREATEDBYID,I.CREATEDBYNAME,I.CREATEDTIME,"
                        + "I.CREATEDBYORGID,I.CREATEDBYORGNAME,ROW_NUMBER() OVER (ORDER BY I.ID DESC) RN "
                        + from + ") WHERE RN BETWEEN ? AND ? ORDER BY RN";
                List<EmcEquiptTestItemBean> rows = new ArrayList<EmcEquiptTestItemBean>();
                try (PreparedStatement select = connection.prepareStatement(sql)) {
                    bindValues(select, pageValues);
                    try (ResultSet result = select.executeQuery()) { while (result.next()) rows.add(testItem(result)); }
                }
                return new Pagination<EmcEquiptTestItemBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (SQLException failure) { throw new IllegalStateException("equipment test-item read failed", failure); }
    }

    /** Read the original calibration/verification/period-check choice pages. */
    public Page<EmcEquiptBean> readChoice(RestJsonWrapperBean wrapper, String event, boolean periodCheck) {
        if (event == null || event.trim().isEmpty()) throw new IllegalArgumentException("equipment choice event is required");
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            QueryRequest request = QueryRequest.parse(wrapper == null ? null : wrapper.getRequestJson(),
                    wrapper == null ? null : wrapper.getParamMap());
            rejectAuxiliaryQuery(request, "choice");
            if (organizations == null || organizations.isEmpty()) return new Pagination<EmcEquiptBean>(
                    0, request.getPageSize(), request.getPageNumber(), new ArrayList<EmcEquiptBean>());
            List<Object> values = new ArrayList<Object>();
            values.add(event);
            String scope = categoryScopeSql("E", organizations, values);
            String planTable = periodCheck ? "T_EMC_EQUIPT_PER_CHECK_PLAN" : "T_EMC_EQUIPT_PLAN";
            String from = "FROM T_EMC_EQUIPT E JOIN " + planTable + " P ON P.EQUIPTID=E.ID "
                    + "JOIN T_EMC_EQUIPT_MAINT M ON M.ID=P.MAINTID AND M.MAINTEVENT=? "
                    + "LEFT JOIN T_CORE_CODE C ON C.CODE=M.MAINTEVENT AND C.CODECATEGORYID='maintainEvent' WHERE " + scope;
            int start = (request.getPageNumber() - 1) * request.getPageSize() + 1;
            int end = request.getPageNumber() * request.getPageSize();
            try (Connection connection = dataSource.getConnection()) {
                int total = count(connection, "SELECT COUNT(1) " + from, values);
                List<Object> pageValues = new ArrayList<Object>(values); pageValues.add(start); pageValues.add(end);
                String selected = periodCheck
                        ? "E.ID,E.EQUIPTID,E.EQUIPTNAME,E.SPECIFICATION,P.CYCLE,P.WARNINGDAYS,P.MAINTENDDATE,P.CHECKREASON,C.CODENAME"
                        : "E.ID,E.EQUIPTID,E.EQUIPTNAME,E.SPECIFICATION,P.CYCLE,P.WARNINGDAYS,P.MAINTENDDATE,P.GIST,P.VERIFYORG,C.CODENAME";
                String sql = "SELECT * FROM (SELECT " + selected + ",ROW_NUMBER() OVER (ORDER BY E.ID DESC,P.ID DESC) RN "
                        + from + ") WHERE RN BETWEEN ? AND ? ORDER BY RN";
                List<EmcEquiptBean> rows = new ArrayList<EmcEquiptBean>();
                try (PreparedStatement select = connection.prepareStatement(sql)) {
                    bindValues(select, pageValues);
                    try (ResultSet result = select.executeQuery()) {
                        while (result.next()) rows.add(choice(result, periodCheck));
                    }
                }
                return new Pagination<EmcEquiptBean>(total, request.getPageSize(), request.getPageNumber(), rows);
            }
        } catch (SQLException failure) { throw new IllegalStateException("equipment choice read failed", failure); }
    }

    /** Isolated Oracle write seam for the original equipment creation route. */
    public List<Map<String, String>> availableCategories() throws SQLException {
        OwnedWriteSupport.requireLoopback(currentRequest());
        OwnedPrincipal principal = trustedWritePrincipal();
        Set<String> organizations = provider.permittedOrganizationIds(principal);
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        if (organizations.isEmpty()) return result;
        StringBuilder sql = new StringBuilder("SELECT C.ID,C.EQUIPTCATEGORY FROM T_EMC_EQUIPT_CATEGORY C WHERE EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=C.ID AND P.ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append(")) ORDER BY C.EQUIPTCATEGORY,C.ID");
        try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1;
            for (String org : organizations) query.setString(index++, org);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    Map<String, String> row = new LinkedHashMap<String, String>();
                    row.put("id", rows.getString(1)); row.put("name", rows.getString(2)); result.add(row);
                }
            }
        }
        return result;
    }

    /** Isolated Oracle write seam for the original equipment creation route. */
    public Long insertEquipment(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("equipment body is required");
        EmcEquiptBean value = wrapper.parseUnique(EmcEquiptBean.class);
        if (value.getEquiptCategoryId() == null || value.getEquiptCategoryId().longValue() < 0L)
            throw new IllegalArgumentException("equipment category is required");
        if (text(value.getEquiptId()).length() == 0 || text(value.getEquiptId()).length() > 64)
            throw new IllegalArgumentException("equipment code is invalid");
        if (text(value.getEquiptName()).length() == 0 || text(value.getEquiptName()).length() > 100)
            throw new IllegalArgumentException("equipment name is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            long id = OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEquipmentCategoryScope(connection, value.getEquiptCategoryId().longValue(), organizations);
                long generatedId = OwnedWriteSupport.nextIdentity(connection);
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO T_EMC_EQUIPT (ID,EQUIPTID,EQUIPTNAME,SPECIFICATION,MANUFACTURER,FACTORYNO,EQUIPTCATEGORYID,USEDDEPARTMENTID,USEDDEPOSTID,STATUS,EQUIPTBELONG,EQUIPTRECORD,ORGID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    insert.setLong(1, generatedId); insert.setString(2, value.getEquiptId()); insert.setString(3, value.getEquiptName());
                    insert.setString(4, value.getSpecification()); insert.setString(5, value.getManufacturer()); insert.setString(6, value.getFactoryNo());
                    insert.setLong(7, value.getEquiptCategoryId().longValue()); insert.setString(8, value.getUsedDepartmentId());
                    insert.setString(9, value.getUsedDepostId()); insert.setString(10, value.getStatus());
                    insert.setString(11, value.getEquiptBelong() == null ? "自有" : value.getEquiptBelong());
                    insert.setString(12, value.getEquiptRecord() == null ? "1" : value.getEquiptRecord());
                    insert.setString(13, principal.getOrgId()); insert.executeUpdate();
                }
                return generatedId;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-insert", principal.getId(), String.valueOf(id),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return Long.valueOf(id);
        } catch (SQLException failure) { throw new IllegalStateException("equipment write failed", failure); }
    }

    /** Isolated Oracle master-table delete; every requested row must be in scope. */
    public void deleteEquipment(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new IllegalArgumentException("equipment ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("equipment ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                for (Long id : ids) {
                    if (id == null || id.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
                    requireEquipmentScope(connection, id.longValue(), organizations);
                }
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM T_EMC_EQUIPT WHERE ID=?")) {
                    for (Long id : ids) { delete.setLong(1, id.longValue()); delete.addBatch(); }
                    int[] counts = delete.executeBatch();
                    for (int count : counts) if (count != 1) throw denied();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-delete", principal.getId(), ids.toString(),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment delete failed", failure); }
    }

    /** Isolated Oracle master-table update for the original equipment route. */
    public void updateEquipment(Long id, RestJsonWrapperBean wrapper) {
        if (id == null || id.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("equipment body is required");
        EmcEquiptBean value = wrapper.parseUnique(EmcEquiptBean.class);
        if (text(value.getEquiptName()).length() == 0 || text(value.getEquiptName()).length() > 100)
            throw new IllegalArgumentException("equipment name is invalid");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                requireEquipmentScope(connection, id.longValue(), organizations);
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE T_EMC_EQUIPT SET EQUIPTNAME=?,SPECIFICATION=?,MANUFACTURER=?,FACTORYNO=?,STATUS=?,EQUIPTBELONG=?,EQUIPTRECORD=? WHERE ID=?")) {
                    update.setString(1, value.getEquiptName()); update.setString(2, value.getSpecification());
                    update.setString(3, value.getManufacturer()); update.setString(4, value.getFactoryNo());
                    update.setString(5, value.getStatus()); update.setString(6, value.getEquiptBelong());
                    update.setString(7, value.getEquiptRecord()); update.setLong(8, id.longValue());
                    if (update.executeUpdate() != 1) throw denied();
                }
                return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-update", principal.getId(), String.valueOf(id),
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment update failed", failure); }
    }

    /** Isolated Oracle write seam for the original equipment test-method endpoint. */
    public Long insertTestMethod(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("test method body is required");
        List<EmcEquiptTestMethodBean> values = wrapper.parse(EmcEquiptTestMethodBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("test method body is required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long firstId = OwnedWriteSupport.transaction(dataSource, connection -> {
                    requireEquipmentScope(connection, equipmentId.longValue(), organizations);
                    long first = -1L;
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO T_EMC_EQUIPT_TEST_METHOD (ID,EQUIPTID,TESTCATEGORYID,TESTMETHODID,TESTID,METHODID,CURVENO,CURVEID,ORGID,ANALYSISCATEGORY,DEFAULTFLAG,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,?,SYSTIMESTAMP,?,?)")) {
                        for (EmcEquiptTestMethodBean value : values) {
                            validateTestMethod(value);
                            long id = OwnedWriteSupport.nextIdentity(connection);
                            if (first < 0L) first = id;
                            int index = 1;
                            insert.setLong(index++, id);
                            insert.setLong(index++, equipmentId.longValue());
                            setLong(insert, index++, value.getTestCategoryId());
                            setLong(insert, index++, value.getTestMethodId());
                            setLong(insert, index++, value.getTestId());
                            setLong(insert, index++, value.getMethodId());
                            insert.setString(index++, value.getCurveNo());
                            insert.setString(index++, value.getCurveId());
                            insert.setString(index++, value.getOrgId());
                            insert.setString(index++, value.getAnalysisCategory());
                            insert.setString(index++, value.getDefaultFlag() == null ? "0" : value.getDefaultFlag());
                            insert.setString(index++, principal.getId());
                            insert.setString(index++, principal.getId());
                            insert.setString(index++, principal.getOrgId());
                            insert.setString(index, principal.getOrgName());
                            insert.addBatch();
                        }
                        insert.executeBatch();
                    }
                    return Long.valueOf(first);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-test-method-insert", principal.getId(),
                    String.valueOf(equipmentId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return firstId;
        } catch (SQLException failure) { throw new IllegalStateException("equipment test method write failed", failure); }
    }

    /** Isolated Oracle write seam for the original equipment test-item endpoint. */
    public Long insertTestItem(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("test item body is required");
        List<EmcEquiptTestItemBean> values = wrapper.parse(EmcEquiptTestItemBean.class);
        if (values.isEmpty() || values.size() > 100) throw new IllegalArgumentException("test item body is required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            Long firstId = OwnedWriteSupport.transaction(dataSource, connection -> {
                    requireEquipmentScope(connection, equipmentId.longValue(), organizations);
                    long first = -1L;
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO T_EMC_EQUIPT_TEST_ITEM (ID,EQUIPTID,ITEMID,TESTMETHODID,EASRESULTID,MATCHITEMNAME,MATCHRESULTNAME,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                        for (EmcEquiptTestItemBean value : values) {
                            validateTestItem(value);
                            if (value.getTestMethodId() != null && !testMethodBelongsTo(connection, value.getTestMethodId().longValue(), equipmentId.longValue())) {
                                throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                            }
                            long id = OwnedWriteSupport.nextIdentity(connection);
                            if (first < 0L) first = id;
                            int index = 1;
                            insert.setLong(index++, id); insert.setLong(index++, equipmentId.longValue());
                            setLong(insert, index++, value.getItemId()); setLong(insert, index++, value.getTestMethodId());
                            setLong(insert, index++, value.getEasResultId()); insert.setString(index++, value.getMatchItemName());
                            insert.setString(index++, value.getMatchResultName()); insert.setString(index++, principal.getId());
                            insert.setString(index++, principal.getId()); insert.setString(index++, principal.getOrgId());
                            insert.setString(index, principal.getOrgName()); insert.addBatch();
                        }
                        insert.executeBatch();
                    }
                    return Long.valueOf(first);
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-test-item-insert", principal.getId(),
                    String.valueOf(equipmentId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return firstId;
        } catch (SQLException failure) { throw new IllegalStateException("equipment test item write failed", failure); }
    }

    /** Delete only test-item rows attached to the requested in-scope equipment. */
    public void deleteTestItem(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("test item ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("test item ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                    requireEquipmentScope(connection, equipmentId.longValue(), organizations);
                    for (Long id : ids) {
                        if (id == null || !testItemBelongsTo(connection, id.longValue(), equipmentId.longValue())) {
                            throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                        }
                    }
                    try (PreparedStatement delete = connection.prepareStatement(
                            "DELETE FROM T_EMC_EQUIPT_TEST_ITEM WHERE ID=? AND EQUIPTID=?")) {
                        for (Long id : ids) { delete.setLong(1, id.longValue()); delete.setLong(2, equipmentId.longValue()); delete.addBatch(); }
                        delete.executeBatch();
                    }
                    return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-test-item-delete", principal.getId(),
                    String.valueOf(equipmentId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment test item delete failed", failure); }
    }

    /** Delete only test-method rows attached to the requested in-scope equipment. */
    public void deleteTestMethod(Long equipmentId, RestJsonWrapperBean wrapper) {
        if (equipmentId == null || equipmentId.longValue() < 0L) throw new IllegalArgumentException("equipment id is invalid");
        if (wrapper == null) throw new IllegalArgumentException("test method ids are required");
        List<Long> ids = wrapper.parseId(Long.class);
        if (ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("test method ids are required");
        try {
            OwnedPrincipal principal = trustedWritePrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            OwnedWriteSupport.transaction(dataSource, connection -> {
                    requireEquipmentScope(connection, equipmentId.longValue(), organizations);
                    for (Long id : ids) {
                        if (id == null || !testMethodBelongsTo(connection, id.longValue(), equipmentId.longValue())) {
                            throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
                        }
                    }
                    try (PreparedStatement delete = connection.prepareStatement(
                            "DELETE FROM T_EMC_EQUIPT_TEST_METHOD WHERE ID=? AND EQUIPTID=?")) {
                        for (Long id : ids) { delete.setLong(1, id.longValue()); delete.setLong(2, equipmentId.longValue()); delete.addBatch(); }
                        delete.executeBatch();
                    }
                    return null;
            });
            audit.record(new AuditEvent(java.time.Instant.now(), "equipment-test-method-delete", principal.getId(),
                    String.valueOf(equipmentId), AuditEvent.Outcome.ALLOWED, "isolated-write", null));
        } catch (SQLException failure) { throw new IllegalStateException("equipment test method delete failed", failure); }
    }

    private OwnedPrincipal trustedPrincipal() throws SQLException {
        HttpServletRequest request = currentRequest();
        HttpSession session = request.getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_READ);
        return boundary.currentPrincipal(session);
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

    private OwnedPrincipal trustedWritePrincipal() throws SQLException {
        HttpServletRequest request = currentRequest();
        HttpSession session = request.getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.EQUIPMENT_MANAGE);
        return boundary.currentPrincipal(session);
    }

    private static void requireEquipmentCategoryScope(Connection connection, long categoryId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_ORG_PERMISSION WHERE PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND TARGETID=? AND ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append(')');
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setString(index++, String.valueOf(categoryId)); for (String org : organizations) query.setString(index++, org);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
        }
    }

    private static String text(String value) { return value == null ? "" : value.trim(); }

    private static void validateTestMethod(com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean value) {
        if (value == null) throw new IllegalArgumentException("test method is required");
        if (value.getDefaultFlag() != null && value.getDefaultFlag().length() > 1)
            throw new IllegalArgumentException("default flag is invalid");
        if (value.getAnalysisCategory() != null && value.getAnalysisCategory().length() > 64)
            throw new IllegalArgumentException("analysis category is too long");
        if (value.getCurveNo() != null && value.getCurveNo().length() > 128)
            throw new IllegalArgumentException("curve number is too long");
        if (value.getCurveId() != null && value.getCurveId().length() > 128)
            throw new IllegalArgumentException("curve id is too long");
        if (value.getOrgId() != null && value.getOrgId().length() > 64)
            throw new IllegalArgumentException("organization id is too long");
    }

    private static void validateTestItem(EmcEquiptTestItemBean value) {
        if (value == null) throw new IllegalArgumentException("test item is required");
        if (value.getMatchItemName() != null && value.getMatchItemName().length() > 200)
            throw new IllegalArgumentException("matching item name is too long");
        if (value.getMatchResultName() != null && value.getMatchResultName().length() > 200)
            throw new IllegalArgumentException("matching result name is too long");
    }

    private static void setLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.NUMERIC); else statement.setLong(index, value.longValue());
    }

    private static void requireEquipmentScope(Connection connection, long equipmentId, Set<String> organizations) throws SQLException {
        if (organizations == null || organizations.isEmpty()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        StringBuilder sql = new StringBuilder("SELECT 1 FROM T_EMC_EQUIPT E WHERE E.ID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");
        for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
        sql.append("))");
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            int index = 1; query.setLong(index++, equipmentId); for (String org : organizations) query.setString(index++, org);
            try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED"); }
        }
    }

    private static boolean testMethodBelongsTo(Connection connection, long id, long equipmentId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_EMC_EQUIPT_TEST_METHOD WHERE ID=? AND EQUIPTID=?")) {
            query.setLong(1, id); query.setLong(2, equipmentId);
            try (ResultSet rows = query.executeQuery()) { return rows.next(); }
        }
    }

    private static boolean testItemBelongsTo(Connection connection, long id, long equipmentId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM T_EMC_EQUIPT_TEST_ITEM WHERE ID=? AND EQUIPTID=?")) {
            query.setLong(1, id); query.setLong(2, equipmentId);
            try (ResultSet rows = query.executeQuery()) { return rows.next(); }
        }
    }

    private static void rejectAuxiliaryQuery(QueryRequest request, String kind) {
        if (!request.getFilterPatterns().isEmpty() || !request.getMapperParameters().isEmpty()
                || !request.getAdvancedPatterns().isEmpty() || request.getMultiPropertyPattern() != null
                || !request.getOrderParameters().isEmpty()) {
            throw new IllegalArgumentException("unsupported equipment " + kind + " query shape");
        }
    }

    private static String equipmentScopeSql(long equipmentId, Set<String> organizations,
            List<Object> values, String alias) {
        StringBuilder sql = new StringBuilder(alias).append(".EQUIPTID=? AND EXISTS (SELECT 1 FROM T_EMC_EQUIPT E "
                + "WHERE E.ID=").append(alias).append(".EQUIPTID AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P "
                + "WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");
        values.add(Long.valueOf(equipmentId));
        int index = 0;
        for (String organization : organizations) {
            if (index++ > 0) sql.append(',');
            sql.append('?');
            values.add(organization);
        }
        return sql.append(")))").toString();
    }

    private static String categoryScopeSql(String equipmentAlias, Set<String> organizations, List<Object> values) {
        StringBuilder sql = new StringBuilder("EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE "
                + "P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=" + equipmentAlias
                + ".EQUIPTCATEGORYID AND P.ORGID IN (");
        int index = 0;
        for (String organization : organizations) {
            if (index++ > 0) sql.append(',');
            sql.append('?');
            values.add(organization);
        }
        return sql.append("))").toString();
    }

    private static int count(Connection connection, String sql, List<Object> values) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindValues(statement, values);
            try (ResultSet rows = statement.executeQuery()) { rows.next(); return rows.getInt(1); }
        }
    }

    private static void bindValues(PreparedStatement statement, List<Object> values) throws SQLException {
        int index = 1;
        for (Object value : values) statement.setObject(index++, value);
    }

    private static EmcEquiptTestMethodBean testMethod(ResultSet rows) throws SQLException {
        EmcEquiptTestMethodBean value = new EmcEquiptTestMethodBean();
        value.setId(longValue(rows.getObject("ID")));
        value.setEquiptId(longValue(rows.getObject("EQUIPTID")));
        value.setTestCategoryId(longValue(rows.getObject("TESTCATEGORYID")));
        value.setTestMethodId(longValue(rows.getObject("TESTMETHODID")));
        value.setTestId(longValue(rows.getObject("TESTID")));
        value.setMethodId(longValue(rows.getObject("METHODID")));
        value.setCurveNo(rows.getString("CURVENO")); value.setCurveId(rows.getString("CURVEID"));
        value.setOrgId(rows.getString("ORGID")); value.setAnalysisCategory(rows.getString("ANALYSISCATEGORY"));
        value.setDefaultFlag(rows.getString("DEFAULTFLAG"));
        value.setCreatedById(rows.getString("CREATEDBYID")); value.setCreatedByName(rows.getString("CREATEDBYNAME"));
        Timestamp created = rows.getTimestamp("CREATEDTIME");
        if (created != null) value.setCreatedTime(created.toLocalDateTime());
        value.setCreatedByOrgId(rows.getString("CREATEDBYORGID")); value.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME"));
        return value;
    }

    private static EmcEquiptTestItemBean testItem(ResultSet rows) throws SQLException {
        EmcEquiptTestItemBean value = new EmcEquiptTestItemBean();
        value.setId(longValue(rows.getObject("ID"))); value.setEquiptId(longValue(rows.getObject("EQUIPTID")));
        value.setItemId(longValue(rows.getObject("ITEMID"))); value.setTestMethodId(longValue(rows.getObject("TESTMETHODID")));
        value.setEasResultId(longValue(rows.getObject("EASRESULTID")));
        value.setMatchItemName(rows.getString("MATCHITEMNAME")); value.setMatchResultName(rows.getString("MATCHRESULTNAME"));
        value.setCreatedById(rows.getString("CREATEDBYID")); value.setCreatedByName(rows.getString("CREATEDBYNAME"));
        Timestamp created = rows.getTimestamp("CREATEDTIME");
        if (created != null) value.setCreatedTime(created.toLocalDateTime());
        value.setCreatedByOrgId(rows.getString("CREATEDBYORGID")); value.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME"));
        return value;
    }

    private static EmcEquiptBean choice(ResultSet rows, boolean periodCheck) throws SQLException {
        EmcEquiptBean value = new EmcEquiptBean();
        value.setId(longValue(rows.getObject("ID")));
        value.setEquiptId(rows.getString("EQUIPTID"));
        value.setEquiptName(rows.getString("EQUIPTNAME"));
        value.setSpecification(rows.getString("SPECIFICATION"));
        value.setExt$Item("cycle", rows.getString("CYCLE"));
        Object warning = rows.getObject("WARNINGDAYS");
        if (warning != null) value.setExt$Item("warningdays", String.valueOf(warning));
        Timestamp date = rows.getTimestamp("MAINTENDDATE");
        if (date != null) value.setExt$Item("maintenddate", date.toLocalDateTime().toLocalDate().toString());
        value.setExt$Item("codename", rows.getString("CODENAME"));
        if (periodCheck) {
            value.setExt$Item("checkreason", rows.getString("CHECKREASON"));
        } else {
            value.setExt$Item("gist", rows.getString("GIST"));
            value.setExt$Item("verifyorg", rows.getString("VERIFYORG"));
        }
        return value;
    }

    private static EmcEquiptDCUConfBean dcu(ResultSet rows) throws SQLException {
        EmcEquiptDCUConfBean value = new EmcEquiptDCUConfBean();
        value.setId(longValue(rows.getObject("ID"))); value.setAcqScriptId(longValue(rows.getObject("ACQSCRIPTID")));
        value.setInterfaceCategory(rows.getString("INTERFACECATEGORY")); value.setBaudRate(rows.getString("BAUDRATE"));
        value.setTerminator(rows.getString("TERMINATOR")); value.setDataBit(rows.getString("DATABIT"));
        value.setParityCheck(rows.getString("PARITYCHECK")); value.setCom(rows.getString("COM"));
        value.setStopBit(rows.getString("STOPBIT")); value.setBeginSymbol(rows.getString("BEGINSYMBOL"));
        value.setEndSymbol(rows.getString("ENDSYMBOL")); value.setAnalyticFormula(rows.getString("ANALYTICFORMULA"));
        value.setIp(rows.getString("IP")); value.setCreatedById(rows.getString("CREATEDBYID"));
        value.setCreatedByName(rows.getString("CREATEDBYNAME"));
        Timestamp created = rows.getTimestamp("CREATEDTIME");
        if (created != null) value.setCreatedTime(created.toLocalDateTime());
        value.setCreatedByOrgId(rows.getString("CREATEDBYORGID")); value.setCreatedByOrgName(rows.getString("CREATEDBYORGNAME"));
        return value;
    }

    private static Long longValue(Object value) { return value == null ? null : Long.valueOf(((Number) value).longValue()); }

    @Override public List<EmcEquiptBean> selectByConditionByTestMethodIdList(List<String> ids) {
        if (ids == null || ids.isEmpty() || ids.size() > 100) throw new IllegalArgumentException("test method ids are invalid");
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) return new ArrayList<EmcEquiptBean>();
            StringBuilder sql = new StringBuilder("SELECT E.* FROM T_EMC_EQUIPT E WHERE EXISTS (SELECT 1 FROM T_EMC_EQUIPT_TEST_METHOD T WHERE T.EQUIPTID=E.ID AND T.TESTMETHODID IN (");
            for (int i = 0; i < ids.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
            sql.append(") ) AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");
            for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
            sql.append(")) ORDER BY E.ID DESC");
            List<EmcEquiptBean> result = new ArrayList<EmcEquiptBean>();
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
                int index = 1; for (String id : ids) { try { query.setLong(index++, Long.parseLong(id)); } catch (NumberFormatException failure) { throw new IllegalArgumentException("test method ids are invalid"); } }
                for (String organization : organizations) query.setString(index++, organization);
                try (ResultSet rows = query.executeQuery()) { while (rows.next()) result.add(convert(resultRow(rows))); }
            }
            return result;
        } catch (SQLException failure) { throw new IllegalStateException("equipment test-method chooser query failed", failure); }
    }
    @Override public List<Map<String, Object>> selectCaliChoose() { throw unsupported(); }
    @Override public List<Map<String, Object>> selectVerifyChoose() { throw unsupported(); }
    @Override public List<Map<String, Object>> selectPerCheckChoose() { throw unsupported(); }
    @Override public List<EmcEquiptBean> selectMaxEquiptIdByEquiptCategoryId(long id) {
        if (id < 0L) throw new IllegalArgumentException("equipment category id is invalid");
        try {
            OwnedPrincipal principal = trustedPrincipal();
            Set<String> organizations = provider.permittedOrganizationIds(principal);
            if (organizations == null || organizations.isEmpty()) return new ArrayList<EmcEquiptBean>();
            StringBuilder sql = new StringBuilder("SELECT E.EQUIPTCATEGORYID,MAX(E.EQUIPTID) EQUIPTID FROM T_EMC_EQUIPT E WHERE E.EQUIPTCATEGORYID=? AND EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=E.EQUIPTCATEGORYID AND P.ORGID IN (");
            for (int i = 0; i < organizations.size(); i++) { if (i > 0) sql.append(','); sql.append('?'); }
            sql.append(") ) GROUP BY E.EQUIPTCATEGORYID");
            List<EmcEquiptBean> result = new ArrayList<EmcEquiptBean>();
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(sql.toString())) {
                int index = 1; query.setLong(index++, id); for (String organization : organizations) query.setString(index++, organization);
                try (ResultSet rows = query.executeQuery()) { while (rows.next()) result.add(convert(resultRow(rows))); }
            }
            return result;
        } catch (SQLException failure) { throw new IllegalStateException("equipment category maximum query failed", failure); }
    }
    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Equipment auxiliary query is not implemented");
    }

    private static Page<EmcEquiptBean> convert(Pagination<Map<String, Object>> page) {
        List<EmcEquiptBean> rows = new ArrayList<EmcEquiptBean>();
        for (Map<String, Object> row : page.getRows()) rows.add(convert(row));
        return new Pagination<EmcEquiptBean>(page.getTotal(), page.getPageSize(), page.getPageNumber(), rows,
                page.getTotalMap());
    }

    private static EmcEquiptBean convert(Map<String, Object> row) {
        EmcEquiptBean bean = new EmcEquiptBean();
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
                throw new IllegalStateException("equipment DTO setter inaccessible: " + key, failure);
            } catch (InvocationTargetException failure) {
                throw new IllegalStateException("equipment DTO setter failed: " + key, failure.getCause());
            } catch (RuntimeException failure) {
                throw new IllegalStateException("equipment DTO conversion failed: " + key, failure);
            }
        }
        for (Map.Entry<String, Object> extension : extensions.entrySet()) {
            bean.setExt$Item(extension.getKey(), String.valueOf(extension.getValue()));
        }
        return bean;
    }

    private static Map<String, Object> resultRow(ResultSet rows) throws SQLException {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        ResultSetMetaData metadata = rows.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) result.put(metadata.getColumnLabel(i), com.sunwayworld.owned.internal.data.JdbcValue.normalize(rows.getObject(i)));
        return result;
    }

    private static Map<String, Method> setters() {
        Map<String, Method> result = new HashMap<String, Method>();
        for (Method method : EmcEquiptBean.class.getMethods()) {
            if (method.isBridge() || method.isSynthetic()) continue;
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
        if (target == String.class) return String.valueOf(value);
        if (target == Integer.class || target == Integer.TYPE) return Integer.valueOf(number(value).intValue());
        if (target == Long.class || target == Long.TYPE) return Long.valueOf(number(value).longValue());
        if (target == Short.class || target == Short.TYPE) return Short.valueOf(number(value).shortValue());
        if (target == Double.class || target == Double.TYPE) return Double.valueOf(number(value).doubleValue());
        if (target == Float.class || target == Float.TYPE) return Float.valueOf(number(value).floatValue());
        if (target.isInstance(value)) return value;
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
