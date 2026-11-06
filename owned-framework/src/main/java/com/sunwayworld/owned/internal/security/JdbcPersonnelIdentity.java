package com.sunwayworld.owned.internal.security;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

/** ID/password and personnel-menu checks using real LIMS tables. No HTTP/session policy is implied. */
public final class JdbcPersonnelIdentity {
    private static final String INCORRECT = "GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD";
    private static final String DENIED = "GIKAM.PERMISSION.UNAUTHORIZED";
    private final DataSource dataSource;
    private final Object issuer = new Object();
    private final String[] attachmentMenuIds;
    private final String[] roleMenuAdministrationMenuIds;
    private final String[] resultMenuIds;
    private final String[] orderRetainMenuIds;
    private final String[] orderRetainWriteMenuIds;
    private final String[] resultWriteMenuIds;
    private final String[] auditMenuIds;

    /** Only the evidenced explicitly configured MD5 mode is supported by this narrow adapter. */
    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType) {
        this(dataSource, passwordEncoderType, new String[0], new String[0]);
    }

    /** Attachment management is fail-closed until a deployment supplies its observed menu IDs. */
    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType, String... attachmentMenuIds) {
        this(dataSource, passwordEncoderType, attachmentMenuIds, new String[0]);
    }

    /**
     * Explicit menu IDs for the two administrative seams.  The role/menu
     * matrix is never enabled by an implicit wildcard; a deployment must
     * identify the observed administration menu ID before this permission can
     * be granted.
     */
    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType,
            String[] attachmentMenuIds, String[] roleMenuAdministrationMenuIds) {
        this(dataSource, passwordEncoderType, attachmentMenuIds, roleMenuAdministrationMenuIds, new String[0]);
    }

    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType,
            String[] attachmentMenuIds, String[] roleMenuAdministrationMenuIds, String[] resultMenuIds) {
        this(dataSource, passwordEncoderType, attachmentMenuIds, roleMenuAdministrationMenuIds, resultMenuIds, new String[0]);
    }

    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType,
            String[] attachmentMenuIds, String[] roleMenuAdministrationMenuIds, String[] resultMenuIds,
            String[] orderRetainMenuIds) {
        this(dataSource, passwordEncoderType, attachmentMenuIds, roleMenuAdministrationMenuIds,
                resultMenuIds, orderRetainMenuIds, new String[0]);
    }

    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType,
            String[] attachmentMenuIds, String[] roleMenuAdministrationMenuIds, String[] resultMenuIds,
            String[] orderRetainMenuIds, String[] resultWriteMenuIds) {
        this(dataSource, passwordEncoderType, attachmentMenuIds, roleMenuAdministrationMenuIds,
                resultMenuIds, orderRetainMenuIds, resultWriteMenuIds, new String[0]);
    }

    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType,
            String[] attachmentMenuIds, String[] roleMenuAdministrationMenuIds, String[] resultMenuIds,
            String[] orderRetainMenuIds, String[] resultWriteMenuIds, String[] orderRetainWriteMenuIds) {
        this(dataSource, passwordEncoderType, attachmentMenuIds, roleMenuAdministrationMenuIds,
                resultMenuIds, orderRetainMenuIds, resultWriteMenuIds, orderRetainWriteMenuIds,
                new String[0]);
    }

    public JdbcPersonnelIdentity(DataSource dataSource, String passwordEncoderType,
            String[] attachmentMenuIds, String[] roleMenuAdministrationMenuIds, String[] resultMenuIds,
            String[] orderRetainMenuIds, String[] resultWriteMenuIds, String[] orderRetainWriteMenuIds,
            String[] auditMenuIds) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        if (!"MD5".equals(passwordEncoderType)) throw new IllegalArgumentException("Unsupported password encoder");
        this.attachmentMenuIds = attachmentMenuIds == null ? new String[0] : attachmentMenuIds.clone();
        this.roleMenuAdministrationMenuIds = roleMenuAdministrationMenuIds == null
                ? new String[0] : roleMenuAdministrationMenuIds.clone();
        this.resultMenuIds = resultMenuIds == null ? new String[0] : resultMenuIds.clone();
        this.orderRetainMenuIds = orderRetainMenuIds == null ? new String[0] : orderRetainMenuIds.clone();
        this.resultWriteMenuIds = resultWriteMenuIds == null ? new String[0] : resultWriteMenuIds.clone();
        this.orderRetainWriteMenuIds = orderRetainWriteMenuIds == null ? new String[0] : orderRetainWriteMenuIds.clone();
        this.auditMenuIds = auditMenuIds == null ? new String[0] : auditMenuIds.clone();
    }

    public Principal authenticate(String userId, char[] password) throws SQLException {
        if (userId == null || userId.length() == 0 || password == null) throw new BadCredentialsException(INCORRECT);
        try (Connection connection = dataSource.getConnection()) {
            checkLoginConfiguration(connection);
            UserRecord record = readUser(connection, userId);
            if (record == null || !matches(password, record.password)) throw new BadCredentialsException(INCORRECT);
            checkActive(record);
            return new Principal(issuer, record.id, record.orgId, record.orgName);
        }
    }

    /**
     * Re-read an identity that the original project authentication chain has
     * already accepted. No password is accepted here and every account state
     * check is repeated against the live database.
     */
    public Principal authenticatedPrincipal(String userId) throws SQLException {
        if (userId == null || userId.trim().length() == 0) throw new AccessDeniedException(DENIED);
        try (Connection connection = dataSource.getConnection()) {
            checkLoginConfiguration(connection);
            UserRecord record = readUser(connection, userId);
            if (record == null) throw new AccessDeniedException(DENIED);
            checkActive(record);
            return new Principal(issuer, record.id, record.orgId, record.orgName);
        }
    }

    /** Return current database identity fields, keeping the prior snapshot immutable. */
    public Principal refreshPrincipal(Principal principal) throws SQLException {
        requireIssued(principal);
        try (Connection connection = dataSource.getConnection()) {
            checkLoginConfiguration(connection);
            UserRecord record = readUser(connection, principal.id);
            if (record == null) throw new AccessDeniedException(DENIED);
            checkActive(record);
            return new Principal(issuer, record.id, record.orgId, record.orgName);
        }
    }

    /** Account reads use the same revalidation as protected API requests. */
    public java.util.Map<String, Object> accountProfile(Principal principal) throws SQLException {
        Principal current = refreshPrincipal(principal);
        return new com.sunwayworld.owned.internal.data.AccountQueryService(
                new com.sunwayworld.owned.internal.data.AccountQueryDao(dataSource)).load(current.id);
    }

    /** Re-read current account and grants; a cached principal never freezes revoked permission. */
    public void authorizePersonnel(Principal principal) throws SQLException {
        requireIssued(principal);
        try (Connection connection = dataSource.getConnection()) {
            checkLoginConfiguration(connection);
            UserRecord record = readUser(connection, principal.id);
            if (record == null) throw new AccessDeniedException(DENIED);
            checkActive(record);
            String sql = "SELECT 1 FROM T_CORE_MENU M WHERE M.ID = ? AND EXISTS ("
                    + "SELECT 1 FROM T_CORE_ROLE_USER RU JOIN T_CORE_ROLE R ON R.ID = RU.ROLEID "
                    + "JOIN T_CORE_ROLE_PERMISSION P ON P.ROLEID = R.ID "
                    + "WHERE RU.USERID = ? AND R.LASTSUSPENDEDFLAG = '0' "
                    + "AND P.PERMISSIONTYPEID = 'T_CORE_MENU' AND (P.TARGETID = M.ID OR P.TARGETID = '0'))";
            try (PreparedStatement query = connection.prepareStatement(sql)) {
                query.setString(1, "100001"); query.setString(2, principal.id);
                try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException(DENIED); }
            }
        }
    }

    /**
     * Re-read the instrument menu grant before equipment data access.  The
     * existing URL guard permits either the management menu or the separate
     * read-only query menu for GET and POST query requests.
     */
    public void authorizeEquipment(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, "101501", "20251000001");
    }

    public void authorizeEquipmentManagement(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, "101501");
    }

    /** Existing management or read-only consumable menu grants permit reads only. */
    public void authorizeConsumable(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, "430201", "20250800003");
    }

    public void authorizeConsumableManagement(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, "430201");
    }

    public void authorizeAttachmentManagement(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, attachmentMenuIds);
    }

    public void authorizeRoleMenuAdministration(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, roleMenuAdministrationMenuIds);
    }

    public void authorizeResult(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, resultMenuIds);
    }

    public void authorizeResultWrite(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, resultWriteMenuIds);
    }

    public void authorizeOrderRetain(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, orderRetainMenuIds);
    }

    public void authorizeOrderRetainWrite(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, orderRetainWriteMenuIds);
    }

    public void authorizeAudit(Principal principal) throws SQLException {
        authorizeAnyMenu(principal, auditMenuIds);
    }

    /** Concrete organization grants only, as consumed by the existing personnel org_filter SQL. */
    public Set<String> permittedOrganizationIds(Principal principal) throws SQLException {
        requireIssued(principal);
        try (Connection connection = dataSource.getConnection()) {
            checkLoginConfiguration(connection);
            UserRecord record = readUser(connection, principal.id);
            if (record == null) throw new AccessDeniedException(DENIED);
            checkActive(record);
            Set<String> organizations = new LinkedHashSet<String>();
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT DISTINCT O.ID FROM T_CORE_USER_PERMISSION P JOIN T_CORE_ORG O ON O.ID = P.TARGETID "
                    + "WHERE P.USERID = ? AND P.PERMISSIONTYPEID = 'T_CORE_ORG'")) {
                query.setString(1, principal.id);
                try (ResultSet rows = query.executeQuery()) { while (rows.next()) organizations.add(rows.getString(1)); }
            }
            return Collections.unmodifiableSet(organizations);
        }
    }

    private void requireIssued(Principal principal) {
        if (principal == null || principal.issuer != issuer) throw new AccessDeniedException(DENIED);
    }

    private void authorizeAnyMenu(Principal principal, String... menuIds) throws SQLException {
        requireIssued(principal);
        if (menuIds == null || menuIds.length == 0) throw new AccessDeniedException(DENIED);
        try (Connection connection = dataSource.getConnection()) {
            checkLoginConfiguration(connection);
            UserRecord record = readUser(connection, principal.id);
            if (record == null) throw new AccessDeniedException(DENIED);
            checkActive(record);
            StringBuilder sql = new StringBuilder("SELECT 1 FROM T_CORE_MENU M WHERE M.ID IN (");
            for (int i = 0; i < menuIds.length; i++) {
                if (i > 0) sql.append(',');
                sql.append('?');
            }
            sql.append(") AND EXISTS ("
                    + "SELECT 1 FROM T_CORE_ROLE_USER RU JOIN T_CORE_ROLE R ON R.ID = RU.ROLEID "
                    + "JOIN T_CORE_ROLE_PERMISSION P ON P.ROLEID = R.ID "
                    + "WHERE RU.USERID = ? AND R.LASTSUSPENDEDFLAG = '0' "
                    + "AND P.PERMISSIONTYPEID = 'T_CORE_MENU' AND (P.TARGETID = M.ID OR P.TARGETID = '0'))");
            try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
                int index = 1;
                for (String menuId : menuIds) query.setString(index++, menuId);
                query.setString(index, principal.id);
                try (ResultSet rows = query.executeQuery()) { if (!rows.next()) throw new AccessDeniedException(DENIED); }
            }
        }
    }
    private static UserRecord readUser(Connection connection, String id) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT ID,PASSWORD,ORGID,ORGNAME,STATUS,LASTSUSPENDEDFLAG,EXPIRYDATE FROM T_CORE_USER WHERE ID = ?")) {
            query.setString(1, id);
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) return null;
                UserRecord record = new UserRecord(rows.getString(1), rows.getString(2), rows.getString(3),
                        rows.getString(4), rows.getString(5), rows.getString(6), rows.getTimestamp(7));
                if (rows.next()) throw new SQLException("Non-unique authentication identity");
                return record;
            }
        }
    }
    /**
     * This adapter deliberately supports only the baseline system login mode.  A nonzero
     * organization/role/tenant selector or captcha/auth method would require request fields
     * and policy checks owned by the HTTP layer, so accepting it here would be fail-open.
     */
    private static void checkLoginConfiguration(Connection connection) throws SQLException {
        boolean hasAuthMethod = hasColumn(connection, "T_CORE_LOGIN_CONFIG", "LOGINAUTHMETHOD");
        String sql = hasAuthMethod
                ? "SELECT LOGINORG,LOGINROLE,LOGINTENANT,LOGINAUTHMETHOD,CAPTCHA,ADDOMAINSUPPORT,SHOWLOGINAUTHOPTION,QCCODE "
                    + "FROM T_CORE_LOGIN_CONFIG WHERE ID = 'login-config'"
                : "SELECT LOGINORG,LOGINROLE,LOGINTENANT,CAPTCHA,ADDOMAINSUPPORT,SHOWLOGINAUTHOPTION,QRCODE "
                    + "FROM T_CORE_LOGIN_CONFIG WHERE ID = 'login-config'";
        try (PreparedStatement query = connection.prepareStatement(sql)) {
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) throw new IllegalStateException("Missing login configuration");
                String loginOrg = rows.getString(1);
                String loginRole = rows.getString(2);
                String loginTenant = rows.getString(3);
                String authMethod = hasAuthMethod ? rows.getString(4) : null;
                String captcha = rows.getString(hasAuthMethod ? 5 : 4);
                if (!"0".equals(loginOrg) || !"0".equals(loginRole) || !isOff(loginTenant)
                        || !"0".equals(captcha) || (authMethod != null && !"system".equals(authMethod))
                        || !"0".equals(rows.getString(hasAuthMethod ? 6 : 5))
                        || !"0".equals(rows.getString(hasAuthMethod ? 7 : 6))
                        || !"0".equals(rows.getString(hasAuthMethod ? 8 : 7))) {
                    throw new IllegalStateException("Unsupported login configuration");
                }
                if (rows.next()) throw new IllegalStateException("Non-unique login configuration");
            }
        }
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT COUNT(*) FROM T_CORE_PASSWORD_POLICY WHERE (LASTSUSPENDEDFLAG IS NULL OR LASTSUSPENDEDFLAG <> '1') "
                        + "AND (NVL(MAXFAILEDATTEMPTS,0) > 0 OR NVL(EXPIRYINTERVAL,0) > 0 "
                        + "OR EXPIREDPOLICY = 'mandatory')")) {
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next() || rows.getInt(1) != 0) throw new IllegalStateException("Unsupported password policy");
            }
        }
    }
    private static boolean hasColumn(Connection connection, String table, String column) throws SQLException {
        try (ResultSet columns = connection.getMetaData().getColumns(null, null, table, column)) {
            return columns.next();
        }
    }
    private static boolean isOff(String value) { return value == null || "0".equals(value); }
    private static void checkActive(UserRecord user) {
        if ("1".equals(user.suspended)) throw new DisabledException("GIKAM.SECURITY.LOGIN.DEACTIVATED");
        if ("locked".equals(user.status)) throw new LockedException("GIKAM.SECURITY.LOGIN.LOCKED");
        if ("deprecated".equals(user.status)) throw new LockedException("GIKAM.SECURITY.LOGIN.DEPRECATED");
        if (user.expiry != null && user.expiry.toInstant().isBefore(Instant.now()))
            throw new AccountExpiredException("GIKAM.SECURITY.LOGIN.EXPIRED");
        // Unknown/non-active account states are not admitted by the owned query adapter.
        if (!"activated".equals(user.status) || !"0".equals(user.suspended)) throw new DisabledException("GIKAM.SECURITY.LOGIN.DEACTIVATED");
    }
    private static boolean matches(char[] password, String stored) {
        if (stored == null || !stored.matches("[0-9a-f]{32}")) return false;
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
        byte[] bytes = new byte[buffer.remaining()]; buffer.get(bytes);
        byte[] digest = null; byte[] expected = new byte[16];
        try {
            digest = MessageDigest.getInstance("MD5").digest(bytes);
            for (int i=0; i<16; i++) expected[i] = (byte) Integer.parseInt(stored.substring(i*2,i*2+2),16);
            return MessageDigest.isEqual(digest, expected);
        } catch (NoSuchAlgorithmException impossible) { throw new IllegalStateException("MD5 unavailable", impossible); }
        finally {
            Arrays.fill(bytes,(byte)0); Arrays.fill(expected,(byte)0);
            if (digest != null) Arrays.fill(digest,(byte)0);
            if (buffer.hasArray()) Arrays.fill(buffer.array(),(byte)0);
        }
    }
    private static final class UserRecord {
        final String id,password,orgId,orgName,status,suspended; final Timestamp expiry;
        UserRecord(String id,String password,String orgId,String orgName,String status,String suspended,Timestamp expiry) {
            this.id=id;this.password=password;this.orgId=orgId;this.orgName=orgName;this.status=status;this.suspended=suspended;this.expiry=expiry;
        }
    }
    public static final class Principal implements java.security.Principal {
        private final Object issuer; private final String id,orgId,orgName;
        private Principal(Object issuer,String id,String orgId,String orgName) { this.issuer=issuer;this.id=id;this.orgId=orgId;this.orgName=orgName; }
        public String getName() { return id; }
        public String getId() { return id; }
        public String getOrgId() { return orgId; }
        public String getOrgName() { return orgName; }
        @Override public String toString() { return "Authenticated personnel principal"; }
    }
}
