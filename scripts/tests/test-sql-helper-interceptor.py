#!/usr/bin/env python3
"""Unit tests for SqlHelper and MybatisInterceptor SQL syntax/parameter enhancements."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]


class TestSqlHelperRegex(unittest.TestCase):
    def setUp(self):
        self.dangling_where = re.compile(
            r"\bwhere\s*(?:/\*[\s\S]*?\*/|--[^\r\n]*[\r\n]|\s)*$",
            re.IGNORECASE,
        )
        self.where_and_or = re.compile(
            r"\bwhere\s*(?:/\*[\s\S]*?\*/|--[^\r\n]*[\r\n]|\s)*\b(and|or)\b\s+",
            re.IGNORECASE,
        )
        self.dangling_and_or = re.compile(
            r"\b(and|or)\s*(?:/\*[\s\S]*?\*/|--[^\r\n]*[\r\n]|\s)*$",
            re.IGNORECASE,
        )
        self.ending_order = re.compile(
            r"\s+order\s+by\s+([^()]*[(][^()]*([(][^()]*([(][^()]*[)][^()]*)*[)][^()]*)*[)][^()]*)*[^()]*$",
            re.IGNORECASE,
        )

    def clean_sql(self, sql: str) -> str:
        sql = self.where_and_or.sub("WHERE ", sql)
        sql = self.dangling_where.sub("", sql)
        sql = self.dangling_and_or.sub("", sql)
        return sql

    def test_strip_dangling_where_with_comments(self):
        sql = "SELECT T.* FROM T_STOCK T WHERE /* comment 1 */ /* comment 2 */"
        cleaned = self.clean_sql(sql).strip()
        self.assertEqual(cleaned, "SELECT T.* FROM T_STOCK T")

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
        sql_no_order = self.ending_order.sub("", sql, count=1)
        cleaned = self.clean_sql(sql_no_order).strip()
        self.assertNotIn("ORDER BY", cleaned)
        self.assertNotIn("WHERE", cleaned)
        self.assertEqual(cleaned, "SELECT T.* FROM (SELECT A.* FROM T_STOCK A) T")

    def test_nested_subquery_order_by_preserved_in_subquery(self):
        sql = "SELECT * FROM (SELECT id FROM t ORDER BY id) T"
        # Should not strip inner order by because it ends with ) T
        m = self.ending_order.search(sql)
        self.assertIsNone(m)

    def test_outer_order_by_stripped_when_inner_present(self):
        sql = "SELECT * FROM (SELECT id FROM t ORDER BY id) T ORDER BY name"
        m = self.ending_order.search(sql)
        self.assertIsNotNone(m)
        stripped = self.ending_order.sub("", sql, count=1).strip()
        self.assertEqual(stripped, "SELECT * FROM (SELECT id FROM t ORDER BY id) T")


class TestMapperXmlHygiene(unittest.TestCase):
    def test_emc_stock_oracle_mapper_no_raw_sql_comments_in_where(self):
        path = (
            ROOT
            / "project/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/mtl/mapper/conf/emc-stock-oracle-mapper.xml"
        )
        text = path.read_text(encoding="utf-8")
        # Match any <select id="selectQcStdStockPagination" ...> ... </select>
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


class TestOracleDialectSource(unittest.TestCase):
    def test_oracle_datatype_coverage(self):
        path = (
            ROOT
            / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/database/dialect/OracleDialect.java"
        )
        text = path.read_text(encoding="utf-8")
        for dt in ["raw", "rowid", "urowid", "long", "xmltype", "binary_float", "binary_double"]:
            self.assertIn(f'case "{dt}":', text)
        self.assertIn("return Object.class;", text)


if __name__ == "__main__":
    unittest.main()
