import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * 生产 Oracle 只读探查工具。
 *
 * 仅执行 SELECT：了解数据库版本、账号权限、对象构成与登录相关表，为隔离副本的数据恢复确定范围。
 * 不执行任何 DML/DDL/PLSQL 调用，不对生产库产生写入。
 *
 * 用法: java -cp lib/ojdbc8-19.3.0.0.jar DbProbe.java <url> <user> <password>
 */
public class DbProbe {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("用法: DbProbe <jdbc-url> <user> <password>");
            System.exit(2);
        }
        String url = args[0];
        try (Connection c = DriverManager.getConnection(url, args[1], args[2])) {
            DatabaseMetaData md = c.getMetaData();
            kv("database.product", md.getDatabaseProductName());
            kv("database.version", md.getDatabaseProductVersion());
            kv("driver", md.getDriverName() + " " + md.getDriverVersion());
            kv("connected.user", args[1]);

            q(c, "current-schema", "select sys_context('USERENV','CURRENT_SCHEMA') s, ora_database_name db from dual");
            q(c, "objects-by-type", "select object_type, count(*) cnt from user_objects group by object_type order by cnt desc");
            q(c, "space-usage", "select round(sum(bytes)/1024/1024) mb, count(*) segments from user_segments");
            q(c, "tables-by-rows", "select table_name, num_rows from user_tables where rownum <= 30 order by num_rows desc nulls last");
            q(c, "login-related-tables",
                "select table_name, num_rows from user_tables "
                        + "where regexp_like(table_name, '(USER|ACCOUNT|LOGIN|ROLE|MENU|RESOURCE|DICT|SECURITY|AUTH|EMP)', 'i') "
                        + "and rownum <= 60 order by table_name");
            q(c, "space-by-type", "select segment_type, round(sum(bytes)/1024/1024) mb, count(*) segments from user_segments group by segment_type order by mb desc");
            q(c, "all-table-sizes", "select segment_name table_name, round(sum(bytes)/1024/1024) mb from user_segments where segment_type = 'TABLE' group by segment_name order by mb desc");
            q(c, "space-by-tablespace", "select tablespace_name, round(sum(bytes)/1024/1024) mb from user_segments group by tablespace_name order by mb desc");
            q(c, "top-segments", "select segment_name, segment_type, round(sum(bytes)/1024/1024) mb from user_segments group by segment_name, segment_type having sum(bytes) > 64*1024*1024 order by mb desc");
            q(c, "top-lob-tables", "select l.table_name, l.column_name, round(s.bytes/1024/1024) mb from user_lobs l join user_segments s on s.segment_name = l.segment_name where s.bytes > 16*1024*1024 order by mb desc");
            q(c, "top-indexes", "select index_name, table_name, round(sum(bytes)/1024/1024) mb from user_segments where segment_type like 'INDEX%' group by index_name, table_name having sum(bytes) > 64*1024*1024 order by mb desc");
            q(c, "views", "select view_name from user_views where rownum <= 30 order by view_name");
            q(c, "sequences", "select sequence_name, last_number from user_sequences where rownum <= 30 order by sequence_name");
            q(c, "db-links", "select db_link, username, host from user_db_links");
        }
    }

    static void q(Connection c, String label, String sql) {
        System.out.println();
        System.out.println("== " + label + " ==");
        System.out.println("-- " + sql);
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            StringBuilder head = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                head.append(i > 1 ? " | " : "").append(md.getColumnLabel(i));
            }
            System.out.println(head);
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= n; i++) {
                    row.append(i > 1 ? " | " : "").append(rs.getString(i));
                }
                System.out.println(row);
            }
        } catch (Exception e) {
            System.out.println("查询失败: " + e.getMessage());
        }
    }

    static void kv(String k, String v) {
        System.out.println(k + ": " + v);
    }
}
