import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.Properties;

/** Read only the known attachment and employee link; save restricted restore inputs. */
public class AttachmentSample {
    private static String quoted(String value) {
        if (value == null || value.isEmpty() || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("Missing or multiline sample value");
        }
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }

    public static void main(String[] args) throws Exception {
        Path root = Paths.get(args[0]);
        Path output = root.resolve("private/t06-attachment-sample.env");
        if (Files.exists(output)) throw new IllegalStateException("Sample manifest already exists");
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(root.resolve("private/application.properties"))) {
            props.load(in);
        }
        DriverManager.setLoginTimeout(15);
        StringBuilder manifest = new StringBuilder("# Read-only collection: " + OffsetDateTime.now() + "\n");
        try (Connection c = DriverManager.getConnection(props.getProperty("spring.datasource.url"),
                props.getProperty("spring.datasource.username"), props.getProperty("spring.datasource.password"))) {
            String sql = "select id,targetid,md5name,fileext,scope,to_char(createdtime,'YYYY-MM-DD HH24:MI:SS') created_time,"
                    + "createdbyid,createdbyname,createdbyorgid,createdbyorgname,\"SIZE\",version,name "
                    + "from t_core_file where id=?";
            try (PreparedStatement st = c.prepareStatement(sql)) {
                st.setQueryTimeout(30);
                st.setString(1, "1433617553888416");
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Attachment sample missing");
                    if (!"T_CORE_USER$00550".equals(rs.getString(2))) {
                        throw new IllegalStateException("Attachment target changed");
                    }
                    String[] keys = {"ID", "TARGET_ID", "MD5_NAME", "FILE_EXT", "SCOPE", "CREATED_TIME",
                            "CREATED_BY_ID", "CREATED_BY_NAME", "CREATED_BY_ORG_ID", "CREATED_BY_ORG_NAME",
                            "SIZE", "VERSION", "NAME"};
                    for (int i = 0; i < keys.length; i++) {
                        String key = keys[i];
                        if (key.equals("ID") || key.equals("SIZE") || key.equals("VERSION") || key.equals("NAME")) key = "FILE_" + key;
                        manifest.append("T06_").append(key).append("=").append(quoted(rs.getString(i + 1))).append('\n');
                    }
                    String relative = "files/" + rs.getString(5) + "/" + rs.getString(6).substring(0, 10).replace("-", "")
                            + "/" + rs.getString(3) + "/" + rs.getString(13);
                    Path file = root.resolve("private/t06-attachment-sample").resolve(relative).normalize();
                    if (!file.startsWith(root.resolve("private/t06-attachment-sample")) || !Files.isRegularFile(file)) {
                        throw new IllegalStateException("Collected sample path does not match metadata");
                    }
                    byte[] bytes = Files.readAllBytes(file);
                    if (bytes.length != rs.getLong(11)) throw new IllegalStateException("Sample size mismatch");
                    byte[] hash = java.security.MessageDigest.getInstance("SHA-256").digest(bytes);
                    StringBuilder hex = new StringBuilder();
                    for (byte b : hash) hex.append(String.format("%02x", b & 255));
                    if (!hex.toString().equals("b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065")) {
                        throw new IllegalStateException("Sample hash mismatch");
                    }
                    manifest.append("T06_FILE_PATH=").append(quoted(relative)).append('\n');
                    manifest.append("T06_FILE_SHA256=").append(quoted(hex.toString())).append('\n');
                    if (rs.next()) throw new IllegalStateException("Duplicate sample ID");
                }
            }
            try (PreparedStatement st = c.prepareStatement("select id,electronicsignature from t_emc_emp where id=?")) {
                st.setQueryTimeout(30);
                st.setString(1, "00550");
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Employee sample missing");
                    if (rs.getString(2) == null || !rs.getString(2).contains("6f458cd8b5830c7e6bc8aed5a97ce264")) {
                        throw new IllegalStateException("Employee attachment link changed");
                    }
                    manifest.append("T06_EMP_ID=").append(quoted(rs.getString(1))).append('\n');
                    manifest.append("T06_EMP_FILE_URL=").append(quoted(rs.getString(2))).append('\n');
                    if (rs.next()) throw new IllegalStateException("Duplicate employee ID");
                }
            }
        }
        Files.createFile(output, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
        Files.write(output, manifest.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("Restricted sample manifest collected; file size and SHA-256 verified.");
    }
}
