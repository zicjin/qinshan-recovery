#!/usr/bin/env python3
"""Unit tests for SqlHelper and MybatisInterceptor SQL syntax/parameter enhancements."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]


class TestSqlHelperRegex(unittest.TestCase):
    def setUp(self):
        ws_or_comment = r"(?:/\*[\s\S]*?\*/|--[^\r\n]*[\r\n]|\s)"
        self.dangling_where_boundary = re.compile(
            r"\s*\bwhere(" + ws_or_comment + r"*)(?=$|\)|\bgroup\s+by\b|\bhaving\b|\border\s+by\b|\bunion\b|\bintersect\b|\bminus\b)",
            re.IGNORECASE,
        )
        self.dangling_and_or_boundary = re.compile(
            r"\s*\b(and|or)(" + ws_or_comment + r"*)(?=$|\)|\bgroup\s+by\b|\bhaving\b|\border\s+by\b|\bunion\b|\bintersect\b|\bminus\b)",
            re.IGNORECASE,
        )
        self.where_and_or = re.compile(
            r"\bwhere" + ws_or_comment + r"*\b(and|or)\b\s*",
            re.IGNORECASE,
        )
        self.consecutive_and_or = re.compile(
            r"\b(and|or)" + ws_or_comment + r"+\b(and|or)\b\s*",
            re.IGNORECASE,
        )
        self.empty_parens = re.compile(r"\(\s*\)")
        self.open_paren_and_or = re.compile(
            r"\(" + ws_or_comment + r"*\b(and|or)\b\s*",
            re.IGNORECASE,
        )
        self.ext_prefix = re.compile(r"ext\$\.", re.IGNORECASE)
        self.ending_order = re.compile(
            r"\s+order\s+by\s+([^()]*[(][^()]*([(][^()]*([(][^()]*[)][^()]*)*[)][^()]*)*[)][^()]*)*[^()]*$",
            re.IGNORECASE,
        )

    def clean_sql(self, sql: str) -> str:
        prev = None
        while sql != prev:
            prev = sql
            sql = self.empty_parens.sub("", sql)
            sql = self.open_paren_and_or.sub("(", sql)
            sql = self.consecutive_and_or.sub(r"\2 ", sql)
            sql = self.where_and_or.sub("WHERE ", sql)
            sql = self.dangling_and_or_boundary.sub(r"\2", sql)
            sql = self.dangling_where_boundary.sub(r"\1", sql)
        return sql

    def find_outer_order_by_pos(self, sql: str) -> int:
        n = len(sql)
        depth = 0
        in_single_quote = False
        in_block_comment = False
        in_line_comment = False
        order_pos = -1
        i = 0
        while i < n:
            c = sql[i]
            if in_single_quote:
                if c == "'":
                    if i + 1 < n and sql[i + 1] == "'":
                        i += 1
                    else:
                        in_single_quote = False
                i += 1
                continue
            if in_block_comment:
                if c == "*" and i + 1 < n and sql[i + 1] == "/":
                    in_block_comment = False
                    i += 2
                    continue
                i += 1
                continue
            if in_line_comment:
                if c in ("\r", "\n"):
                    in_line_comment = False
                i += 1
                continue
            if c == "'":
                in_single_quote = True
                i += 1
                continue
            if c == "/" and i + 1 < n and sql[i + 1] == "*":
                in_block_comment = True
                i += 2
                continue
            if c == "-" and i + 1 < n and sql[i + 1] == "-":
                in_line_comment = True
                i += 2
                continue
            if c == "(":
                depth += 1
                i += 1
                continue
            if c == ")":
                depth -= 1
                i += 1
                continue
            if depth == 0:
                if i + 5 <= n and sql[i : i + 5].upper() == "ORDER":
                    prev_ok = i == 0 or (not sql[i - 1].isalnum() and sql[i - 1] != "_")
                    next_idx = i + 5
                    next_ok = next_idx >= n or (not sql[next_idx].isalnum() and sql[next_idx] != "_")
                    if prev_ok and next_ok:
                        j = next_idx
                        while j < n and sql[j].isspace():
                            j += 1
                        if j + 2 <= n and sql[j : j + 2].upper() == "BY":
                            after_by = j + 2
                            after_ok = after_by >= n or (not sql[after_by].isalnum() and sql[after_by] != "_")
                            if after_ok:
                                order_pos = i
                                i = after_by
                                continue
            i += 1
        return order_pos

    def test_strip_dangling_where_with_comments(self):
        sql = "SELECT T.* FROM T_STOCK T WHERE /* comment 1 */ /* comment 2 */"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT T.* FROM T_STOCK T /* comment 1 */ /* comment 2 */")

    def test_fix_where_comment_and(self):
        sql = "SELECT T.* FROM T_STOCK T WHERE /* comment */ AND T.ID = 1"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT T.* FROM T_STOCK T WHERE T.ID = 1")

    def test_remove_ending_order_and_clean_dangling_where(self):
        sql = (
            "SELECT T.* FROM (SELECT A.* FROM T_STOCK A) T\n"
            "WHERE\n"
            "    /*查询关联该批下测试的标样*/\n"
            "ORDER BY T.ID DESC"
        )
        pos = self.find_outer_order_by_pos(sql)
        self.assertNotEqual(pos, -1)
        sql_no_order = sql[:pos]
        cleaned = self.clean_sql(sql_no_order).strip()
        self.assertNotIn("ORDER BY", cleaned)
        self.assertNotIn("WHERE", cleaned)

    def test_nested_subquery_order_by_preserved_in_subquery(self):
        sql = "SELECT * FROM (SELECT id FROM t ORDER BY id) T"
        pos = self.find_outer_order_by_pos(sql)
        self.assertEqual(pos, -1, "Inner subquery order by must not be identified as outer order by")

    def test_outer_order_by_stripped_when_inner_present(self):
        sql = "SELECT * FROM (SELECT id FROM t ORDER BY id) T ORDER BY name"
        pos = self.find_outer_order_by_pos(sql)
        self.assertNotEqual(pos, -1)
        stripped = sql[:pos].strip()
        self.assertEqual(stripped, "SELECT * FROM (SELECT id FROM t ORDER BY id) T")

    def test_deep_parentheses_order_by_handled(self):
        sql = "SELECT * FROM T ORDER BY F1(F2(F3(F4(X)))) DESC"
        pos = self.find_outer_order_by_pos(sql)
        self.assertNotEqual(pos, -1, "Scanner must find outer ORDER BY with deep nesting (> 3 levels)")
        self.assertEqual(sql[:pos].strip(), "SELECT * FROM T")

    def test_dangling_where_before_group_by(self):
        sql = "SELECT id, count(1) FROM t WHERE GROUP BY id"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT id, count(1) FROM t GROUP BY id")

    def test_dangling_where_before_having(self):
        sql = "SELECT id, count(1) FROM t WHERE HAVING count(1) > 0"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT id, count(1) FROM t HAVING count(1) > 0")

    def test_dangling_where_before_closing_paren(self):
        sql = "SELECT * FROM (SELECT * FROM t WHERE /* comment */) T"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT * FROM (SELECT * FROM t /* comment */) T")

    def test_dangling_and_before_group_by(self):
        sql = "SELECT id, count(1) FROM t WHERE a = 1 AND GROUP BY id"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT id, count(1) FROM t WHERE a = 1 GROUP BY id")

    def test_loop_cleaning_dangling_where_and(self):
        sql = "SELECT * FROM t WHERE AND"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT * FROM t")

    def test_loop_cleaning_dangling_where_or(self):
        sql = "SELECT * FROM t WHERE OR"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT * FROM t")

    def test_loop_cleaning_multiple_dangling_ands(self):
        sql = "SELECT * FROM t WHERE a = 1 AND AND"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT * FROM t WHERE a = 1")

    def test_empty_parentheses_cleaned(self):
        sql = "SELECT * FROM t WHERE () AND a = 1"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT * FROM t WHERE a = 1")

    def test_open_paren_and_cleaned(self):
        sql = "SELECT * FROM t WHERE ( AND a = 1 )"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT * FROM t WHERE (a = 1 )")

    def test_open_paren_or_cleaned(self):
        sql = "SELECT * FROM t WHERE ( OR a = 1 )"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT * FROM t WHERE (a = 1 )")

    def test_clean_ext_prefix_case_insensitive(self):
        sql = "SELECT ext$.NAME, EXT$.AGE, Ext$.ID FROM t"
        cleaned = self.ext_prefix.sub("", sql)
        self.assertEqual(cleaned, "SELECT NAME, AGE, ID FROM t")


class TestMapperXmlHygiene(unittest.TestCase):
    def test_emc_stock_oracle_mapper_no_raw_sql_comments_in_where(self):
        path = (
            ROOT
            / "project/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/mtl/mapper/conf/emc-stock-oracle-mapper.xml"
        )
        text = path.read_text(encoding="utf-8")
        m = re.search(
            r'<select\s+id="selectQcStdStockPagination"[^>]*>([\s\S]*?)</select>',
            text,
        )
        self.assertIsNotNone(m, "selectQcStdStockPagination must exist")
        body = m.group(1)
        where_m = re.search(r"<where>([\s\S]*?)</where>", body)
        self.assertIsNotNone(where_m, "<where> must exist in selectQcStdStockPagination")
        where_body = where_m.group(1)
        self.assertNotIn(
            "/*",
            where_body,
            "Raw SQL comments /* */ inside <where> cause empty WHERE in MyBatis",
        )


class TestMybatisInterceptorSource(unittest.TestCase):
    def test_executor_signature_and_additional_params_copy(self):
        path = (
            ROOT
            / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mybatis/interceptor/MybatisInterceptor.java"
        )
        text = path.read_text(encoding="utf-8")
        self.assertIn(
            "invocation.getTarget() instanceof Executor",
            text,
            "Interceptor must check Executor to support CachingExecutor",
        )
        self.assertIn(
            "additionalParams.forEach(newBoundSql::setAdditionalParameter)",
            text,
            "getNewBoundSql must copy all additional parameters",
        )
        self.assertIn(
            'newBoundSql.setAdditionalParameter("loginUser", LocalContextHelper.getLoginUser())',
            text,
            "getNewBoundSql must inject loginUser for pagination total count",
        )
        self.assertIn(
            'newBoundSql.setAdditionalParameter("loginUserId", LocalContextHelper.getLoginUserId())',
            text,
            "getNewBoundSql must inject loginUserId for pagination total count",
        )
        self.assertIn(
            'newBoundSql.setAdditionalParameter("loginUserName", LocalContextHelper.getLoginUserName())',
            text,
            "getNewBoundSql must inject loginUserName for pagination total count",
        )
        self.assertIn(
            '@Signature(type=Executor.class, method="update", args={MappedStatement.class, Object.class})',
            text,
            "Interceptor must intercept Executor.update to inject parameters for write operations",
        )
        self.assertIn(
            'if (!map.containsKey("loginOrgId"))',
            text,
            "enhanceParameter must avoid overwriting existing loginOrgId",
        )
        self.assertIn(
            'if (!map.containsKey("loginUserId"))',
            text,
            "enhanceParameter must inject loginUserId into map",
        )
        self.assertIn(
            'if (paramObj instanceof Map)',
            text,
            "enhanceParameter on statement handler must enhance parameter map",
        )
        self.assertLess(
            text.index("DaoMapper.class.getName()"),
            text.index("cleanDanglingWhereClause(boundSql.getSql())"),
            "framework-owned DaoMapper SQL must bypass dangling-clause cleanup",
        )


class TestSqlHelperSource(unittest.TestCase):
    def test_sql_helper_loop_cleaning_and_order_by_cleaning(self):
        path = (
            ROOT
            / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/database/sql/SqlHelper.java"
        )
        text = path.read_text(encoding="utf-8")
        self.assertIn(
            "do {",
            text,
            "cleanDanglingWhereClause must loop until stable",
        )
        self.assertIn(
            "findOuterOrderByPos",
            text,
            "SqlHelper must define findOuterOrderByPos scanner",
        )
        self.assertIn(
            "int pos = findOuterOrderByPos(sql);",
            text,
            "removeEndingOrderIfPresent must use findOuterOrderByPos scanner",
        )
        self.assertNotIn(
            "SQL_ENDING_ORDER_BY_PATTERN",
            text,
            "SqlHelper must not retain the catastrophic backtracking pattern",
        )
        self.assertIn(
            "cleanExtPrefix",
            text,
            "SqlHelper must define case-insensitive cleanExtPrefix",
        )
        self.assertIn(
            'append(" ").append(c.toUpperCase())',
            text,
            "SqlHelper.getSelectTotalSql must place space before sum alias",
        )


class TestOracleDialectSource(unittest.TestCase):
    def test_oracle_datatype_coverage(self):
        path = (
            ROOT
            / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/database/dialect/OracleDialect.java"
        )
        text = path.read_text(encoding="utf-8")
        for dt in ["raw", "rowid", "urowid", "long", "xmltype", "binary_float", "binary_double"]:
            self.assertIn(f'case "{dt}":', text)
        self.assertIn('case "nclob":', text, "isClob must support nclob")
        self.assertIn('case "long raw":', text, "isBlob must support long raw")
        self.assertIn("return Object.class;", text)


if __name__ == "__main__":
    unittest.main()
