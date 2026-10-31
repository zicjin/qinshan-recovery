import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * Read-only JDBC query helper for recovery evidence. The supplied SQL must be
 * a SELECT (or an Oracle data-dictionary query); no update/DDL API is exposed.
 * Usage: java DbQuery <url> <user> <password> <sql>
 */
public final class DbQuery {
    private DbQuery() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            throw new IllegalArgumentException("usage: DbQuery <url> <user> <password> <sql>");
        }
        String sql = args[3].trim();
        if (!sql.regionMatches(true, 0, "select", 0, 6)
                && !sql.regionMatches(true, 0, "with", 0, 4)) {
            throw new IllegalArgumentException("read-only helper accepts SELECT/WITH only");
        }
        try (Connection c = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                if (i > 1) System.out.print("\t");
                System.out.print(md.getColumnLabel(i));
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    if (i > 1) System.out.print("\t");
                    String value = rs.getString(i);
                    System.out.print(value == null ? "" : value.replace('\t', ' '));
                }
                System.out.println();
            }
        }
    }
}
