#!/usr/bin/env python3
"""Exercise menu ordering through real MyBatis XML -> BoundSql.

Only the public MyBatis JAR is needed. Optional --h2-jar runs a bounded,
in-memory Oracle-mode pagination fixture; it is not Oracle/HTTP acceptance.
All compiled files and fixture state stay in a temporary directory.
"""
import argparse
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
RESOURCES = ROOT / "reconstituted/framework/owned-core-common/src/main/resources"

JAVA = r'''
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

public final class MenuPaginationContract {
    private static final String MENU = "com.sunwayworld.module.sys.menu.mapper.CoreMenuMapper.";

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void parse(Configuration config, Path path) throws Exception {
        try (InputStream input = Files.newInputStream(path)) {
            new XMLMapperBuilder(input, config, path.toString(), config.getSqlFragments()).parse();
        }
    }

    private static BoundSql bound(Configuration config, String statement, Map<String, Object> params) {
        return config.getMappedStatement(MENU + statement).getBoundSql(params);
    }

    private static String sql(Configuration config, String statement, Map<String, Object> params) {
        return bound(config, statement, params).getSql().replaceAll("\\s+", " ").trim();
    }

    private static Map<String, Object> order(String field, String direction) {
        Map<String, Object> pair = new HashMap<>();
        pair.put("first", field);
        pair.put("second", direction);
        return pair;
    }

    private static void suffix(String sql, String expected) {
        check(sql.endsWith(expected), "Expected suffix [" + expected + "] but got [" + sql + "]");
    }

    private static void oracleFixture(String sql) throws Exception {
        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:menu;MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE T_CORE_MENU (ID INT PRIMARY KEY, ORDERNO INT, PARENTID INT)");
            }
            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO T_CORE_MENU VALUES (?, ?, NULL)")) {
                // Reverse insertion order, 205 equal values spanning two page boundaries,
                // plus text-vs-numeric order (10 before 2) and a final null group.
                for (int id = 406; id >= 1; id--) {
                    insert.setInt(1, id);
                    if (id <= 205) insert.setInt(2, 10);
                    else if (id <= 400) insert.setInt(2, 2);
                    else insert.setNull(2, java.sql.Types.INTEGER);
                    insert.addBatch();
                }
                insert.executeBatch();
            }
            List<Integer> ids = new ArrayList<>();
            for (int page = 1; page <= 5; page++) {
                // Same Oracle ROWNUM boundaries used by application pagination.
                String paged = "SELECT * FROM (SELECT PAGE_.*, ROWNUM RN_ FROM (" + sql
                    + ") PAGE_ WHERE ROWNUM <= " + page * 100 + ") WHERE RN_ > " + (page - 1) * 100;
                int pageSize = 0;
                try (Statement statement = connection.createStatement(); ResultSet rows = statement.executeQuery(paged)) {
                    while (rows.next()) { ids.add(rows.getInt("ID")); pageSize++; }
                }
                check(pageSize == (page < 5 ? 100 : 6), "Unexpected size on page " + page);
            }
            check(ids.size() == 406 && new HashSet<>(ids).size() == 406, "Pages must contain 406 unique IDs");
            for (int index = 0; index < 406; index++) {
                check(ids.get(index) == index + 1, "Text ORDERNO then unique ID ordering changed at " + index);
            }
        }
        System.out.println("Oracle-mode fixture: PASS (5 pages, 406 unique IDs, tied/text/null ORDERNO)");
    }

    public static void main(String[] args) throws Exception {
        Path resources = Paths.get(args[0]);
        String[] dialects = {"oracle", "mysql", "postgresql", "sqlserver"};
        String[] defaults = {"TO_CHAR(T.ORDERNO), T.ID", "CONVERT(T.ORDERNO,CHAR), T.ID",
                             "T.ORDERNO, T.ID", "CAST(T.ORDERNO AS VARCHAR), T.ID"};
        String[] explicit = {"\"ORDERNO\" DESC NULLS LAST , \"ID\" ASC NULLS LAST",
                             "ORDERNO IS NULL, ORDERNO DESC , ID IS NULL, ID ASC",
                             "ORDERNO DESC NULLS LAST , ID ASC NULLS LAST",
                             "CASE WHEN ORDERNO IS NULL THEN 1 ELSE 0 END ASC, ORDERNO DESC , CASE WHEN ID IS NULL THEN 1 ELSE 0 END ASC, ID ASC"};
        String[] idOnly = {"\"ID\" ASC NULLS LAST", "ID IS NULL, ID ASC", "ID ASC NULLS LAST",
                           "CASE WHEN ID IS NULL THEN 1 ELSE 0 END ASC, ID ASC"};
        for (int index = 0; index < dialects.length; index++) {
            String dialect = dialects[index];
            Configuration config = new Configuration();
            parse(config, resources.resolve("com/sunwayworld/framework/mybatis/mapper/conf/global-" + dialect + "-mapper.xml"));
            parse(config, resources.resolve("com/sunwayworld/module/sys/menu/mapper/conf/core-menu-" + dialect + "-mapper.xml"));
            Map<String, Object> params = new HashMap<>();
            String defaultSql = sql(config, "selectByCondition", params);
            suffix(defaultSql, "ORDER BY " + defaults[index]);
            params.put("orderParams", Collections.emptyList());
            check(defaultSql.equals(sql(config, "selectByCondition", params)), dialect + " empty order must use default");

            params.put("orderParams", Collections.singletonList(order("ID", "ASC")));
            suffix(sql(config, "selectByCondition", params), "ORDER BY " + idOnly[index]);
            params.put("orderParams", Arrays.asList(order("ORDERNO", "DESC"), order("ID", "ASC")));
            suffix(sql(config, "selectByCondition", params), "ORDER BY " + explicit[index]);
            // Explicit sort must not receive an extra default/tie-break expression.
            params.remove("orderParams");
            params.put("parentId", "root");
            String root = sql(config, "selectByCondition", params);
            check(root.contains("WHERE T.PARENTID IS NULL"), dialect + " root filter changed");
            suffix(root, "ORDER BY " + defaults[index]);
            params.put("parentId", "17");
            params.put("childrenOnly", "1");
            BoundSql children = bound(config, "selectByCondition", params);
            suffix(sql(config, "selectByCondition", params), "ORDER BY " + defaults[index]);
            check(children.getSql().contains("T.PARENTID = ?"), dialect + " child filter missing");
            check(children.getParameterMappings().size() == 1
                && children.getParameterMappings().get(0).getProperty().equals("parentId"), dialect + " child binding changed");

            params.put("childrenOnly", "0");
            String branch = (dialect.equals("oracle") || dialect.equals("sqlserver")) ? "ORDER SIBLINGS BY " : "ORDER BY ";
            suffix(sql(config, "selectByCondition", params), branch + defaults[index]);
            params.put("orderParams", Arrays.asList(order("ORDERNO", "DESC"), order("ID", "ASC")));
            suffix(sql(config, "selectByCondition", params), branch + explicit[index]);

            // Existing tree entry points retain their original ordering contract.
            suffix(sql(config, "selectTreeNodeList", params), "ORDER BY T.ORDERNO");
            params.clear();
            params.put("menuIdList", Arrays.asList("1", "2"));
            suffix(sql(config, "selectJsonTree", params), "ORDER BY T.ORDERNO");
            System.out.println(dialect + " BoundSql: PASS (default, explicit, parent/root and siblings/tree branches)");
            if (dialect.equals("oracle") && args.length > 1) oracleFixture(defaultSql);
        }
    }
}
'''


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--mybatis-jar", type=Path,
                        default=ROOT / "build/reconstituted-public-deps/mybatis-3.5.6.jar")
    parser.add_argument("--h2-jar", type=Path, help="optional public H2 JAR for in-memory Oracle-mode fixture")
    parser.add_argument("--jdk", type=Path, help="JDK containing bin/java and bin/javac")
    args = parser.parse_args()
    for path in [args.mybatis_jar, *([args.h2_jar] if args.h2_jar else [])]:
        if not path.is_file():
            parser.error(f"missing public test dependency: {path}")
    javac = str(args.jdk / "bin/javac") if args.jdk else shutil.which("javac")
    java = str(args.jdk / "bin/java") if args.jdk else shutil.which("java")
    if not javac or not java:
        parser.error("Java compiler/runtime required; provide --jdk")
    with tempfile.TemporaryDirectory(prefix="menu-pagination-contract-") as temporary:
        folder = Path(temporary)
        source = folder / "MenuPaginationContract.java"
        source.write_text(JAVA, encoding="utf-8")
        classpath = ":".join(str(path.resolve()) for path in
                             [folder, args.mybatis_jar, *([args.h2_jar] if args.h2_jar else [])])
        subprocess.run([javac, "-source", "8", "-target", "8", "-encoding", "UTF-8", "-cp", classpath,
                        "-d", str(folder), str(source)], check=True)
        subprocess.run([java, "-cp", classpath, "MenuPaginationContract", str(RESOURCES),
                        *(["fixture"] if args.h2_jar else [])], check=True)
    if not args.h2_jar:
        print("Oracle-mode fixture: not run (provide --h2-jar); BoundSql checks only")


if __name__ == "__main__":
    main()
