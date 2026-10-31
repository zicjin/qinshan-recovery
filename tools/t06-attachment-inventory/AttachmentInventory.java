import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Restricted, production-read-only attachment metadata inventory.
 *
 * The program deliberately has no SQL input argument. Every statement below
 * is a fixed SELECT, and every user-controlled value is a bind variable or a
 * validated Oracle identifier. It never selects attachment content/result
 * LOBs and never writes to the database.
 */
public final class AttachmentInventory {
    private static final List<String> TABLES = Arrays.asList(
            "T_CORE_FILE", "T_EMC_EQUIPT_ACQ_FILE");
    private static final List<String> SAFE_COLUMNS = Arrays.asList(
            "ID", "TARGETID", "SCOPE", "CREATEDTIME", "MD5NAME", "NAME",
            "FILEEXT", "EXT", "SIZE", "BIZCATEGORY", "CATEGORY", "TYPE",
            "FILECATEGORY", "FILENAME", "FILE_NAME", "SOURCEFILENAME", "TARGETFILENAME",
            "EQUIPTID", "CREATEDBY", "CREATEDBYNAME", "REMARK", "STATUS");
    private static final Set<String> FORBIDDEN_LOB_NAMES = new HashSet<>(Arrays.asList(
            "CONTENT", "RESULT", "BLOB", "CLOB", "FILECONTENT", "FILE_CONTENT"));
    private static final int BATCH = 5000;
    private static final DateTimeFormatter PATH_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Set<String> CONFIG_KEYS = new HashSet<>(Arrays.asList(
            "PRODUCTION_READ_ONLY", "DB_ACCOUNT_PURPOSE", "JDBC_URL", "DB_USER",
            "DB_PASSWORD", "JDBC_DRIVER_CLASS", "JDBC_DRIVER_SHA256", "SCHEMA",
            "CONSISTENCY_MODE", "CONSISTENCY_REFERENCE", "FLASHBACK_SCN"));

    private AttachmentInventory() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 3 || !"--production-read-only".equals(args[0])) {
            throw new IllegalArgumentException(
                    "usage: AttachmentInventory --production-read-only <restricted-config> <restricted-output-dir>");
        }
        Map<String, String> cfg = readConfig(Paths.get(args[1]));
        Path out = Paths.get(args[2]).toAbsolutePath().normalize();
        if (Files.exists(out, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("output directory already exists");
        }
        Files.createDirectory(out);
        assertPrivateDirectory(out);
        Class.forName(required(cfg, "JDBC_DRIVER_CLASS"));

        Instant started = Instant.now();
        try (Connection c = DriverManager.getConnection(
                required(cfg, "JDBC_URL"), required(cfg, "DB_USER"), required(cfg, "DB_PASSWORD"))) {
            c.setReadOnly(true);
            c.setAutoCommit(false);
            assertNoWriteSystemPrivileges(c);
            writeRunMetadata(out, c, cfg, started);
            writeColumns(c, out, cfg.get("SCHEMA"));
            writeTables(c, out, cfg.get("SCHEMA"));
            writeIndexes(c, out, cfg.get("SCHEMA"));
            long core = writeAttachmentTable(c, out, cfg, "T_CORE_FILE");
            long acq = writeAttachmentTable(c, out, cfg, "T_EMC_EQUIPT_ACQ_FILE");
            try (BufferedWriter w = Files.newBufferedWriter(out.resolve("summary.txt"), StandardCharsets.UTF_8)) {
                w.write("inventory_started_utc=" + started.toString()); w.newLine();
                w.write("inventory_completed_utc=" + Instant.now().toString()); w.newLine();
                w.write("consistency_mode=" + cfg.get("CONSISTENCY_MODE")); w.newLine();
                w.write("consistency_reference=" + cfg.get("CONSISTENCY_REFERENCE")); w.newLine();
                if (cfg.get("FLASHBACK_SCN") != null) {
                    w.write("flashback_scn=" + cfg.get("FLASHBACK_SCN")); w.newLine();
                }
                w.write("t_core_file_rows=" + core); w.newLine();
                w.write("t_emc_equipt_acq_file_rows=" + acq); w.newLine();
                w.write("content_lobs_selected=false"); w.newLine();
                w.write("database_writes=false"); w.newLine();
            }
            c.rollback();
        }
    }

    private static Map<String, String> readConfig(Path p) throws IOException {
        if (!Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) throw new IOException("config is not a file");
        Map<String, String> m = new LinkedHashMap<>();
        for (String raw : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int i = line.indexOf('=');
            if (i <= 0) throw new IOException("invalid config line");
            String k = line.substring(0, i);
            String v = line.substring(i + 1);
            if (!k.matches("[A-Z][A-Z0-9_]*") || !CONFIG_KEYS.contains(k) || m.put(k, v) != null) {
                throw new IOException("duplicate/unsupported config key");
            }
        }
        if (!"1".equals(m.get("PRODUCTION_READ_ONLY"))
                || !"t06_attachment_inventory_select_only".equals(m.get("DB_ACCOUNT_PURPOSE"))) {
            throw new IOException("read-only account attestation missing");
        }
        for (String k : Arrays.asList("JDBC_URL", "DB_USER", "DB_PASSWORD", "JDBC_DRIVER_CLASS", "JDBC_DRIVER_SHA256", "SCHEMA", "CONSISTENCY_MODE", "CONSISTENCY_REFERENCE")) {
            if (m.get(k) == null || m.get(k).isEmpty()) throw new IOException("missing config key: " + k);
        }
        String schema = m.get("SCHEMA");
        if (!schema.matches("[A-Z][A-Z0-9_$#]*")) throw new IOException("invalid schema identifier");
        String mode = m.get("CONSISTENCY_MODE");
        if (!"flashback-scn".equals(mode) && !"best-effort".equals(mode)) {
            throw new IOException("CONSISTENCY_MODE must be flashback-scn or best-effort");
        }
        String scn = m.get("FLASHBACK_SCN");
        if ("flashback-scn".equals(mode)) {
            if (scn == null || !scn.matches("[0-9]{1,20}")) {
                throw new IOException("flashback-scn mode requires a numeric FLASHBACK_SCN");
            }
        } else if (scn != null && !scn.isEmpty()) {
            throw new IOException("best-effort mode must not set FLASHBACK_SCN");
        }
        return m;
    }

    private static String required(Map<String, String> m, String k) {
        String v = m.get(k);
        if (v == null || v.isEmpty()) throw new IllegalArgumentException("missing " + k);
        return v;
    }

    private static void assertPrivateDirectory(Path p) throws IOException {
        if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) throw new IOException("output is not a directory");
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(p, LinkOption.NOFOLLOW_LINKS);
            if (perms.contains(PosixFilePermission.GROUP_READ) || perms.contains(PosixFilePermission.OTHERS_READ)
                    || perms.contains(PosixFilePermission.GROUP_WRITE) || perms.contains(PosixFilePermission.OTHERS_WRITE)
                    || perms.contains(PosixFilePermission.GROUP_EXECUTE) || perms.contains(PosixFilePermission.OTHERS_EXECUTE)) {
                throw new IOException("output directory permissions are too broad");
            }
        } catch (UnsupportedOperationException ignored) { }
    }

    private static String owner(String schema) { return schema; }
    private static String q(String identifier) { return "\"" + identifier.replace("\"", "\"\"") + "\""; }

    private static void writeRunMetadata(Path out, Connection c, Map<String, String> cfg, Instant started) throws Exception {
        try (BufferedWriter w = Files.newBufferedWriter(out.resolve("run-metadata.txt"), StandardCharsets.UTF_8)) {
            w.write("started_utc=" + started.toString()); w.newLine();
            w.write("production_read_only=true"); w.newLine();
            w.write("db_user=" + cfg.get("DB_USER")); w.newLine();
            w.write("schema=" + owner(cfg.get("SCHEMA"))); w.newLine();
            w.write("database_product=" + c.getMetaData().getDatabaseProductName()); w.newLine();
            w.write("database_version=" + c.getMetaData().getDatabaseProductVersion().replace('\n', ' ')); w.newLine();
            w.write("no_content_lobs=true"); w.newLine();
            w.write("consistency_mode=" + cfg.get("CONSISTENCY_MODE")); w.newLine();
            w.write("consistency_reference=" + cfg.get("CONSISTENCY_REFERENCE")); w.newLine();
        }
    }

    private static void assertNoWriteSystemPrivileges(Connection c) throws SQLException {
        String sql = "SELECT privilege FROM session_privs WHERE privilege IN ("
                + "'INSERT ANY TABLE', 'UPDATE ANY TABLE', 'DELETE ANY TABLE', 'ALTER ANY TABLE', "
                + "'DROP ANY TABLE', 'CREATE ANY TABLE', 'EXECUTE ANY PROCEDURE')";
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) throw new SQLException("inventory account has write-capable system privilege: " + rs.getString(1));
        }
        String objectSql = "SELECT privilege FROM user_tab_privs_recd "
                + "WHERE privilege IN ('INSERT', 'UPDATE', 'DELETE', 'ALTER', 'INDEX') AND ROWNUM = 1";
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(objectSql)) {
            if (rs.next()) throw new SQLException("inventory account has write-capable object privilege: " + rs.getString(1));
        }
    }

    private static void writeColumns(Connection c, Path out, String schema) throws Exception {
        String sql = "SELECT owner, table_name, column_name, data_type, data_length, data_precision, data_scale, nullable "
                + "FROM all_tab_columns WHERE owner = ? AND table_name IN (?, ?) ORDER BY table_name, column_id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setQueryTimeout(120);
            ps.setString(1, owner(schema)); ps.setString(2, TABLES.get(0)); ps.setString(3, TABLES.get(1));
            writeResult(ps, out.resolve("columns.tsv"));
        }
    }

    private static void writeTables(Connection c, Path out, String schema) throws Exception {
        String sql = "SELECT owner, table_name, num_rows, blocks, last_analyzed FROM all_tables "
                + "WHERE owner = ? AND table_name IN (?, ?) ORDER BY table_name";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setQueryTimeout(120);
            ps.setString(1, owner(schema)); ps.setString(2, TABLES.get(0)); ps.setString(3, TABLES.get(1));
            writeResult(ps, out.resolve("tables.tsv"));
        }
    }

    private static void writeIndexes(Connection c, Path out, String schema) throws Exception {
        String sql = "SELECT owner, index_name, table_name, status, blevel, leaf_blocks, distinct_keys, last_analyzed "
                + "FROM all_indexes WHERE owner = ? AND table_name IN (?, ?) ORDER BY table_name, index_name";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setQueryTimeout(120);
            ps.setString(1, owner(schema)); ps.setString(2, TABLES.get(0)); ps.setString(3, TABLES.get(1));
            writeResult(ps, out.resolve("indexes.tsv"));
        }
    }

    private static long writeAttachmentTable(Connection c, Path out, Map<String, String> cfg, String table) throws Exception {
        String schema = cfg.get("SCHEMA");
        Map<String, String> types = new HashMap<>();
        String meta = "SELECT column_name, data_type FROM all_tab_columns WHERE owner = ? AND table_name = ? ORDER BY column_id";
        try (PreparedStatement ps = c.prepareStatement(meta)) {
            ps.setQueryTimeout(120);
            ps.setString(1, owner(schema)); ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) types.put(rs.getString(1).toUpperCase(), rs.getString(2).toUpperCase());
            }
        }
        if (!types.containsKey("ID")) throw new SQLException(table + " has no ID column");
        List<String> cols = new ArrayList<>();
        for (String col : SAFE_COLUMNS) {
            String type = types.get(col);
            if (type != null && !FORBIDDEN_LOB_NAMES.contains(col) && !type.contains("LOB")) cols.add(col);
        }
        if (cols.isEmpty()) throw new SQLException("no safe scalar columns for " + table);
        String schemaPrefix = q(owner(schema)) + ".";
        String projection = joinQuoted(cols, "t");
        String flashback = "flashback-scn".equals(cfg.get("CONSISTENCY_MODE"))
                ? " AS OF SCN " + cfg.get("FLASHBACK_SCN") : "";
        String inner = "SELECT " + projection + ", ROW_NUMBER() OVER (ORDER BY t." + q("ID") + ") rn FROM "
                + schemaPrefix + q(table) + flashback + " t";
        String sql = "SELECT " + joinBare(cols) + " FROM (" + inner + ") WHERE rn > ? AND rn <= ? ORDER BY rn";
        Path file = out.resolve(table.toLowerCase() + ".tsv");
        long offset = 0, total = 0;
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write(String.join("\t", cols) + "\texpected_relative_path\ttarget_class"); w.newLine();
            while (true) {
                long got = 0;
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setQueryTimeout(120);
                    ps.setLong(1, offset); ps.setLong(2, offset + BATCH);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Map<String, String> row = new HashMap<>();
                            for (int i = 1; i <= cols.size(); i++) {
                                if (i > 1) w.write('\t');
                                String value = rs.getString(i);
                                row.put(cols.get(i - 1), value);
                                if ("CREATEDTIME".equals(cols.get(i - 1))) {
                                    Timestamp timestamp = rs.getTimestamp(i);
                                    if (timestamp != null) {
                                        row.put("__CREATED_DATE", PATH_DATE.format(timestamp.toLocalDateTime().toLocalDate()));
                                    }
                                }
                                w.write(escape(value));
                            }
                            w.write('\t');
                            w.write(escape(expectedPath(table, row)));
                            w.write('\t');
                            w.write(escape(targetClass(row.get("TARGETID"))));
                            w.newLine(); got++; total++;
                        }
                    }
                }
                if (got == 0) break;
                offset += got;
                if (got < BATCH) break;
            }
        }
        return total;
    }

    private static String expectedPath(String table, Map<String, String> row) {
        if (!"T_CORE_FILE".equals(table)) return "";
        String scope = row.get("SCOPE");
        String md5 = row.get("MD5NAME");
        String name = row.get("NAME");
        String ext = row.get("EXT");
        if (scope == null || name == null) return "";
        if ("finereport".equalsIgnoreCase(scope)) {
            if (ext == null || ext.isEmpty()) return "";
            return "files/" + scope + "/" + ext + "/" + name;
        }
        String date = row.get("__CREATED_DATE");
        if (date != null && md5 != null && !md5.isEmpty()) {
            return "files/" + scope + "/" + date + "/" + md5 + "/" + name;
        }
        return "";
    }

    private static String targetClass(String target) {
        if (target == null || target.isEmpty()) return "empty";
        if (target.startsWith("X")) return "soft-delete-marker";
        if (target.startsWith("T_")) return "known-table-prefix";
        return "unknown-or-legacy-prefix";
    }

    private static String joinQuoted(List<String> cols, String alias) {
        List<String> out = new ArrayList<>();
        for (String col : cols) out.add(alias + "." + q(col));
        return String.join(", ", out);
    }

    private static String joinBare(List<String> cols) {
        List<String> out = new ArrayList<>();
        for (String col : cols) out.add(q(col));
        return String.join(", ", out);
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
    }

    private static void writeResult(PreparedStatement ps, Path out) throws Exception {
        ps.setQueryTimeout(120);
        try (ResultSet rs = ps.executeQuery(); BufferedWriter w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) { if (i > 1) w.write('\t'); w.write(md.getColumnLabel(i)); }
            w.newLine();
            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) { if (i > 1) w.write('\t'); w.write(escape(rs.getString(i))); }
                w.newLine();
            }
        }
    }
}
