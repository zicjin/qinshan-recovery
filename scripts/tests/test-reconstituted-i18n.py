#!/usr/bin/env python3
"""Validate reconstituted framework message resource discovery."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]


class ReconstitutedI18nTest(unittest.TestCase):
    def test_default_message_basename_fallback_covers_project_resources(self):
        source = (
            ROOT
            / "reconstituted/framework/owned-core-common/src/main/java/"
            "com/sunwayworld/module/sys/i18n/CoreI18nCache.java"
        ).read_text(encoding="utf-8")
        self.assertIn(
            "static/emc/i18n/messages-emc,"
            "static/emc/projectfunction/i18n/messages-weipu",
            source,
        )
        self.assertIn("if (StringUtils.isBlank(baseNames))", source)

    def test_business_translation_resources_exist(self):
        resources = ROOT / "project/src/main/resources"
        for relative in (
            "static/emc/i18n/messages-emc_zh-cn.properties",
            "static/emc/projectfunction/i18n/messages-weipu_zh-cn.properties",
        ):
            self.assertTrue((resources / relative).is_file(), relative)


if __name__ == "__main__":
    unittest.main()
