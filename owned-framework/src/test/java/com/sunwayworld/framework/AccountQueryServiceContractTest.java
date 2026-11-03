package com.sunwayworld.framework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sunwayworld.owned.internal.data.AccountQueryDao;
import com.sunwayworld.owned.internal.data.AccountQueryService;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetProvider;
import org.springframework.security.access.AccessDeniedException;

/** Public account service/DAO seam; no Oracle schema or business writes. */
public final class AccountQueryServiceContractTest {
    private static int checks;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Fixture fixture = new Fixture();
        AccountQueryService service = new AccountQueryService(new AccountQueryDao(fixture.source()));
        Map<String, Object> account = service.load("test-user");
        check("display name".equals(account.get("userName")) && "staff-001".equals(account.get("userNo")), "real display name and user number, not copied id");
        check("admin".equals(account.get("userType")), "actual user type retained");
        check(!account.containsKey("roles") && !account.containsKey("isAdmin") && !account.containsKey("password"), "original DTO envelope and credential exclusion");
        Map<String, Object> extension = (Map<String, Object>) account.get("ext$");
        check("parent-org".equals(extension.get("parentorgid")) && !account.containsKey("parentOrgId"), "parent organization under extension");
        check("configured-owned-app-id".equals(extension.get("appuuid")), "real config app id, not session or license mock");
        check("null".equals(extension.get("passwordexpireremainingdays")), "supported no-expiry policy representation");
        check(extension.get("roles") instanceof String, "role assignment JSON remains a string");
        JSONArray roles = JSON.parseArray((String) extension.get("roles"));
        check(roles.size() == 2 && roles.getJSONObject(0).getLongValue("roleId") == 2, "actual role assignments projected");
        check(roles.getJSONObject(0).getJSONObject("ext$").isEmpty(), "role Bean extension is present");
        check("2026-11-03".equals(roles.getJSONObject(0).getString("createdTime")), "role timestamp formatting");
        check(!roles.getJSONObject(0).containsKey("createdByOrgId"), "null role Bean properties omitted");
        check("1".equals(extension.get("lcdpdesignflag")), "designer presentation reflects actual role2 assignment");
        check(fixture.parameters.equals(Arrays.asList("test-user", "test-user", "appuuid")), "user and config lookup values bound");
        check(fixture.sql.stream().noneMatch(s -> s.contains("PASSWORD")), "profile reads never request password");

        fixture.roleCount = 1;
        Map<String, Object> updated = service.load("test-user");
        Map<String, Object> changed = (Map<String, Object>) updated.get("ext$");
        check(JSON.parseArray((String) changed.get("roles")).size() == 1 && "0".equals(changed.get("lcdpdesignflag")), "revoked role removed on next read, no login snapshot cache");
        check(JSON.parseArray((String) extension.get("roles")).size() == 2, "prior response remains immutable from later read");
        fixture.roleCount = 0;
        fixture.appId = null;
        changed = (Map<String, Object>) service.load("test-user").get("ext$");
        check(!changed.containsKey("roles") && "0".equals(changed.get("lcdpdesignflag")), "no assignments use original absent roles contract");
        check(!changed.containsKey("appuuid"), "absent app configuration is not fabricated");
        fixture.userPresent = false;
        try { service.load("test-user"); throw new AssertionError("removed account was accepted"); }
        catch (AccessDeniedException expected) { check(true, "removed account rejected"); }
        int queries = fixture.sql.size();
        try { service.load(null); throw new AssertionError("anonymous account accepted"); }
        catch (AccessDeniedException expected) { check(fixture.sql.size() == queries, "anonymous account rejected before DAO"); }
        System.out.println("owned account service/DAO contract: " + checks + " checks passed");
    }

    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    private static final class Fixture {
        int roleCount = 2;
        boolean userPresent = true;
        String appId = "configured-owned-app-id";
        final List<String> sql = new ArrayList<String>();
        final List<Object> parameters = new ArrayList<Object>();
        DataSource source() {
            return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{DataSource.class}, (p, m, args) -> {
                if (!"getConnection".equals(m.getName())) throw new AssertionError(m.getName());
                return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Connection.class}, (c, method, inputs) -> {
                    if ("close".equals(method.getName())) return null;
                    if (!"prepareStatement".equals(method.getName())) throw new AssertionError("unexpected connection operation " + method.getName());
                    String query = (String) inputs[0]; sql.add(query);
                    return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{PreparedStatement.class}, (s, operation, values) -> {
                        if ("setString".equals(operation.getName())) { parameters.add(values[1]); return null; }
                        if ("close".equals(operation.getName())) return null;
                        if ("executeQuery".equals(operation.getName())) return result(query);
                        throw new AssertionError("only read-only account queries allowed: " + operation.getName());
                    });
                });
            });
        }
        ResultSet result(String query) throws Exception {
            if (query.contains("FROM T_CORE_USER ")) {
                return rows(new String[]{"ID", "USERNO", "USERNAME", "USERTYPE", "ORGID", "ORGNAME", "PARENTORGID", "PASSWORD"},
                        userPresent ? new Object[][]{{"test-user", "staff-001", "display name", "admin", "home-org", "home name", "parent-org", "must-not-leave-dao"}} : new Object[0][]);
            }
            if (query.contains("FROM T_CORE_ROLE_USER ")) {
                List<Object[]> entries = new ArrayList<Object[]>();
                if (roleCount == 2) entries.add(new Object[]{1L, 2L, "test-user", "0", "creator", null, Timestamp.valueOf("2026-11-03 12:34:56")});
                if (roleCount > 0) entries.add(new Object[]{2L, 9L, "test-user", "1", "creator", null, Timestamp.valueOf("2026-11-03 12:34:56")});
                return rows(new String[]{"ID", "ROLEID", "USERID", "DEFAULTFLAG", "CREATEDBYID", "CREATEDBYORGNAME", "CREATEDTIME"}, entries.toArray(new Object[entries.size()][]));
            }
            if (query.contains("FROM T_LCDP_CONFIG_PARAM ")) return rows(new String[]{"PARAMVALUE"}, appId == null ? new Object[0][] : new Object[][]{{appId}});
            throw new AssertionError("unexpected query: " + query);
        }
        ResultSet rows(String[] fields, Object[][] values) throws Exception {
            CachedRowSet result = RowSetProvider.newFactory().createCachedRowSet();
            RowSetMetaDataImpl metadata = new RowSetMetaDataImpl();metadata.setColumnCount(fields.length);
            for (int i = 0; i < fields.length; i++) {
                metadata.setColumnName(i + 1, fields[i]); metadata.setColumnLabel(i + 1, fields[i]);
                metadata.setColumnType(i + 1, "CREATEDTIME".equals(fields[i]) ? Types.TIMESTAMP : ("ROLEID".equals(fields[i]) || "ID".equals(fields[i]) && fields.length == 7 ? Types.NUMERIC : Types.VARCHAR));
            }
            result.setMetaData(metadata);
            for (int n = values.length - 1; n >= 0; n--) {
                result.moveToInsertRow();
                for (int i = 0; i < fields.length; i++) { if (values[n][i] == null) result.updateNull(i + 1); else result.updateObject(i + 1, values[n][i]); }
                result.insertRow(); result.moveToCurrentRow();
            }
            result.beforeFirst();return result;
        }
    }
}
