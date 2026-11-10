#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/render-external-logical-mappers.py"
spec = importlib.util.spec_from_file_location("render_external_logical_mappers", SCRIPT)
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class ExternalLogicalMapperRenderTest(unittest.TestCase):
    def setUp(self):
        self.manifest = json.loads((ROOT / "api-fixtures/schema/external-schema-clone-manifest.json").read_text())
        self.profile = json.loads((ROOT / "api-fixtures/schema/external-schema-runtime-profile.example.json").read_text())
        self.links = module._manifest_links(self.manifest)

    def test_direct_schema_preserves_join_and_qualifies_only_approved_tables(self):
        source = (ROOT / module.TARGETS[0]).read_text()
        rendered = module.render_mapper(source, "direct-schema", self.profile["qualifier_schemas"], self.links)
        self.assertIn("LIMS_EXT_HT.T_CRM_CONTRACT", rendered)
        self.assertIn("LIMS_EXT_SP.T_LIMS_CLEARANCEFORM", rendered)
        self.assertIn("LIMS_EXT_XFP.T_XFP_PROJECT", rendered)
        self.assertIn("LEFT JOIN", rendered)
        self.assertNotRegex(rendered, r"@LIMS_CZJKY_")

    def test_local_view_removes_only_approved_link_qualifiers(self):
        source = "SELECT * FROM T_CRM_CONTRACT@LIMS_CZJKY_HT LEFT JOIN T_XFP_PROJECT @LIMS_CZJKY_XFP ON 1=1"
        rendered = module.render_mapper(source, "local-view", self.profile["qualifier_schemas"], self.links)
        self.assertEqual(rendered, "SELECT * FROM T_CRM_CONTRACT LEFT JOIN T_XFP_PROJECT ON 1=1")

    def test_unknown_qualifier_is_rejected(self):
        with self.assertRaises(module.MapperProfileError):
            module.render_mapper("SELECT * FROM T_CRM_CONTRACT@OTHER_LINK", "direct-schema", self.profile["qualifier_schemas"], self.links)

    def test_profile_render_writes_only_classpath_overlay(self):
        with tempfile.TemporaryDirectory() as temp:
            output = Path(temp)
            written = module.render(ROOT, output)
            self.assertEqual(set(written), {
                "com/sunwayworld/emc/module/bp/project/project/mapper/conf/pf-project-oracle-mapper.xml",
                "com/sunwayworld/emc/module/mdm/contract/mapper/conf/emc-contract-oracle-mapper.xml",
            })
            self.assertTrue((output / written[0]).is_file())


if __name__ == "__main__":
    unittest.main()
