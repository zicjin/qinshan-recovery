import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/** Local fake-JDBC integration test for AttachmentInventory; no network or DB. */
public final class AttachmentInventoryTest {
    private AttachmentInventoryTest() {}

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("attachment-inventory-test-");
        Path config = root.resolve("inventory.env");
        Path out = root.resolve("out");
        StringBuilder zeros = new StringBuilder();
        for (int i = 0; i < 64; i++) zeros.append('0');
        String sha = zeros.toString();
        Files.write(config, Arrays.asList(
                "PRODUCTION_READ_ONLY=1",
                "DB_ACCOUNT_PURPOSE=t06_attachment_inventory_select_only",
                "JDBC_URL=jdbc:fake:inventory",
                "DB_USER=T06_READONLY",
                "DB_PASSWORD=not-real",
                "JDBC_DRIVER_CLASS=AttachmentInventoryTest$FakeDriver",
                "JDBC_DRIVER_SHA256=" + sha,
                "SCHEMA=LIMS",
                "CONSISTENCY_MODE=flashback-scn",
                "CONSISTENCY_REFERENCE=fixture-snapshot",
                "FLASHBACK_SCN=12345"), StandardCharsets.UTF_8);
        Files.setPosixFilePermissions(config, java.util.EnumSet.of(
                java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                java.nio.file.attribute.PosixFilePermission.OWNER_WRITE));
        DriverManager.registerDriver(new FakeDriver());
        AttachmentInventory.main(new String[] {"--production-read-only", config.toString(), out.toString()});

        assertContains(read(out.resolve("summary.txt")), "flashback_scn=12345");
        assertContains(read(out.resolve("t_core_file.tsv")), "files/secure/20240101/hash/name.txt");
        assertContains(read(out.resolve("t_core_file.tsv")), "known-table-prefix");
        assertContains(read(out.resolve("t_core_file.tsv")), "soft-delete-marker");
        assertContains(read(out.resolve("t_emc_equipt_acq_file.tsv")), "input.csv");
        if (!FakeDriver.seenFlashback || !FakeDriver.seenReadOnly || !FakeDriver.seenRollback) {
            throw new AssertionError("inventory did not use expected read-only/flashback lifecycle");
        }
        System.out.println("AttachmentInventoryTest PASS");
    }

    private static void assertContains(String actual, String expected) {
        if (!actual.contains(expected)) throw new AssertionError("missing: " + expected + " in " + actual);
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    public static final class FakeDriver implements Driver {
        static boolean seenFlashback;
        static boolean seenReadOnly;
        static boolean seenRollback;

        @Override public Connection connect(String url, Properties info) {
            return proxy(Connection.class, new ConnectionHandler());
        }
        @Override public boolean acceptsURL(String url) { return "jdbc:fake:inventory".equals(url); }
        @Override public int getMajorVersion() { return 1; }
        @Override public int getMinorVersion() { return 0; }
        @Override public boolean jdbcCompliant() { return false; }
        @Override public Logger getParentLogger() { return Logger.getGlobal(); }
        @Override public java.sql.DriverPropertyInfo[] getPropertyInfo(String u, Properties p) {
            return new java.sql.DriverPropertyInfo[0];
        }
    }

    private static final class ConnectionHandler implements InvocationHandler {
        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("setReadOnly".equals(name)) { FakeDriver.seenReadOnly = Boolean.TRUE.equals(args[0]); return null; }
            if ("setAutoCommit".equals(name) || "close".equals(name)) return null;
            if ("rollback".equals(name)) { FakeDriver.seenRollback = true; return null; }
            if ("getMetaData".equals(name)) return metadata();
            if ("createStatement".equals(name)) return statement();
            if ("prepareStatement".equals(name)) return prepared((String) args[0]);
            if ("isClosed".equals(name)) return false;
            return defaultValue(method.getReturnType());
        }
    }

    private static Statement statement() {
        return proxy(Statement.class, (p, m, a) -> {
            if ("executeQuery".equals(m.getName())) return resultFor((String) a[0], Collections.emptyList());
            if ("setQueryTimeout".equals(m.getName()) || "close".equals(m.getName())) return null;
            return defaultValue(m.getReturnType());
        });
    }

    private static PreparedStatement prepared(String sql) {
        List<Object> params = new ArrayList<>();
        return proxy(PreparedStatement.class, (p, m, a) -> {
            if ("setString".equals(m.getName()) || "setLong".equals(m.getName())) {
                int index = (Integer) a[0];
                while (params.size() < index) params.add(null);
                params.set(index - 1, a[1]);
                return null;
            }
            if ("setQueryTimeout".equals(m.getName()) || "close".equals(m.getName())) return null;
            if ("executeQuery".equals(m.getName())) return resultFor(sql, params);
            return defaultValue(m.getReturnType());
        });
    }

    private static ResultSet resultFor(String sql, List<Object> params) throws SQLException {
        if (sql.contains("WHERE AND")) throw new SQLException("malformed WHERE");
        if (sql.contains("session_privs") || sql.contains("user_tab_privs_recd")) return rows(new String[] {"PRIVILEGE"});
        if (sql.contains("all_tab_columns") && sql.contains("data_type")) {
            if (sql.contains("data_length")) return rows(
                    new String[] {"OWNER", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "DATA_LENGTH", "DATA_PRECISION", "DATA_SCALE", "NULLABLE"},
                    new String[] {"LIMS", "T_CORE_FILE", "ID", "VARCHAR2", "32", "", "", "N"});
            String table = String.valueOf(params.get(1));
            if ("T_CORE_FILE".equals(table)) return rows(
                    new String[] {"COLUMN_NAME", "DATA_TYPE"},
                    new String[] {"ID", "VARCHAR2"}, new String[] {"TARGETID", "VARCHAR2"},
                    new String[] {"SCOPE", "VARCHAR2"}, new String[] {"CREATEDTIME", "TIMESTAMP"},
                    new String[] {"MD5NAME", "VARCHAR2"}, new String[] {"NAME", "VARCHAR2"},
                    new String[] {"SIZE", "NUMBER"});
            return rows(new String[] {"COLUMN_NAME", "DATA_TYPE"},
                    new String[] {"ID", "VARCHAR2"}, new String[] {"SOURCEFILENAME", "VARCHAR2"},
                    new String[] {"TARGETFILENAME", "VARCHAR2"}, new String[] {"CONTENT", "CLOB"},
                    new String[] {"RESULT", "CLOB"});
        }
        if (sql.contains("all_tables")) return rows(new String[] {"OWNER", "TABLE_NAME", "NUM_ROWS", "BLOCKS", "LAST_ANALYZED"});
        if (sql.contains("all_indexes")) return rows(new String[] {"OWNER", "INDEX_NAME", "TABLE_NAME", "STATUS", "BLEVEL", "LEAF_BLOCKS", "DISTINCT_KEYS", "LAST_ANALYZED"});
        if (sql.contains("ROW_NUMBER()")) {
            if (!sql.contains("AS OF SCN 12345")) throw new SQLException("missing flashback SCN");
            long offset = ((Number) params.get(0)).longValue();
            if (offset != 0) return rows(new String[] {"ID"});
            if (sql.contains("T_CORE_FILE")) {
                FakeDriver.seenFlashback = true;
                return rows(new String[] {"ID", "TARGETID", "SCOPE", "CREATEDTIME", "MD5NAME", "NAME", "SIZE"},
                        new String[] {"1", "T_EMC_EMP$1", "secure", "2024-01-01 12:00:00.0", "hash", "name.txt", "10"},
                        new String[] {"2", "X_DELETED$2", "secure", "2024-01-01 12:00:00.0", "gone", "deleted.txt", "2"});
            }
            return rows(new String[] {"ID", "SOURCEFILENAME", "TARGETFILENAME"},
                    new String[] {"9", "input.csv", "processed.csv"});
        }
        throw new SQLException("unexpected SQL: " + sql);
    }

    private static DatabaseMetaData metadata() {
        return proxy(DatabaseMetaData.class, (p, m, a) -> {
            if ("getDatabaseProductName".equals(m.getName())) return "Fake Oracle";
            if ("getDatabaseProductVersion".equals(m.getName())) return "21.0";
            return defaultValue(m.getReturnType());
        });
    }

    private static ResultSet rows(String[] headers, String[]... values) {
        return proxy(ResultSet.class, new InvocationHandler() {
            int row = -1;
            @Override public Object invoke(Object p, Method m, Object[] a) {
                if ("next".equals(m.getName())) return ++row < values.length;
                if ("getString".equals(m.getName())) return values[row][((Integer) a[0]) - 1];
                if ("getTimestamp".equals(m.getName())) {
                    int index = ((Integer) a[0]) - 1;
                    return "CREATEDTIME".equals(headers[index])
                            ? Timestamp.valueOf(values[row][index]) : null;
                }
                if ("getMetaData".equals(m.getName())) return proxy(ResultSetMetaData.class, (mp, mm, ma) -> {
                    if ("getColumnCount".equals(mm.getName())) return headers.length;
                    if ("getColumnLabel".equals(mm.getName())) return headers[((Integer) ma[0]) - 1];
                    return defaultValue(mm.getReturnType());
                });
                if ("close".equals(m.getName())) return null;
                return defaultValue(m.getReturnType());
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        if (type == char.class) return (char) 0;
        return null;
    }
}
