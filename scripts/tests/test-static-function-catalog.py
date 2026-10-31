#!/usr/bin/env python3
"""Source-backed extraction fixtures; no network, database or private materials."""
import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'tools/function_catalog/extract.py'
spec = importlib.util.spec_from_file_location('extract', SCRIPT)
extract = importlib.util.module_from_spec(spec)
spec.loader.exec_module(extract)

class CatalogTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(); self.root = Path(self.tmp.name) / 'source'
        self.root.mkdir()
    def tearDown(self): self.tmp.cleanup()
    def write(self, name, text):
        path = self.root / 'src/main' / name
        path.parent.mkdir(parents=True, exist_ok=True); path.write_text(text, encoding='utf-8')
        return path
    def fixture(self):
        self.write('java/p/resource/People.java', '''package p.resource;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping(path = {"/people", "/persons"})
public interface People {
 @RequestMapping(value = {"/{id}", "/detail/{id}"}, method = {RequestMethod.GET, RequestMethod.HEAD})
 String get(String id);
}
''')
        self.write('java/p/resource/impl/PeopleImpl.java', '''package p.resource.impl;
import p.resource.People;
public class PeopleImpl implements People {
 // @RequestMapping("/comment-must-not-appear")
 @PostMapping(path = "/save")
 @Override
 public String save() {return "people/detail";}
 @RequestMapping(PATH_CONSTANT)
 public void unresolved() {}
}
''')
        self.write('java/p/service/PeopleService.java', 'package p.service; public interface PeopleService extends GenericService<Person, Long> {}')
        self.write('java/p/mapper/PeopleMapper.java', 'package p.mapper; public interface PeopleMapper {}')
        self.write('java/p/mapper/conf/people-oracle-mapper.xml', '''<?xml version="1.0"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="p.mapper.PeopleMapper">
<!-- FROM T_NOT_REAL -->
<select id="query">SELECT * FROM T_PEOPLE P JOIN T_ORG O ON P.ORG=O.ID</select>
<update id="change">UPDATE T_PEOPLE SET NAME=#{name}</update>
</mapper>''')
        self.write('resources/templates/people/detail.html', '<h1>People</h1>')
    def test_arrays_inheritance_comments_and_dynamic_expression(self):
        self.fixture(); result = extract.extract(self.root)
        self.assertEqual(result['counts']['mapping_declarations'], 4)
        by_member = {r['member']: r for r in result['endpoints']}
        self.assertEqual(by_member['get']['http_methods'], ['GET', 'HEAD'])
        self.assertEqual({r['path'] for r in by_member['get']['candidate_routes']}, {'/people/{id}', '/persons/{id}', '/people/detail/{id}', '/persons/detail/{id}'})
        self.assertEqual({r['path'] for r in by_member['save']['candidate_routes']}, {'/people/save', '/persons/save'})
        self.assertEqual(by_member['save']['http_methods'], ['POST'])
        self.assertTrue(by_member['unresolved']['unresolved_expression'])
        self.assertEqual(by_member['unresolved']['candidate_routes'], [])
        self.assertTrue(all(r['dynamic_status'] == 'unknown' for r in result['endpoints']))
        declaration = by_member['save']['source']
        self.assertEqual(declaration['line'], 5)
        self.assertEqual(declaration['sha256'], extract.sha((self.root / declaration['path'].removeprefix('project/')).read_bytes()))
    def test_xml_templates_interfaces_and_hash(self):
        self.fixture(); result = extract.extract(self.root)
        self.assertEqual(result['templates'][0]['view_name'], 'people/detail')
        self.assertEqual({r['symbol'] for r in result['interfaces']}, {'p.service.PeopleService', 'p.mapper.PeopleMapper'})
        mapper = result['mapper_xml'][0]
        self.assertEqual(mapper['namespace'], 'p.mapper.PeopleMapper')
        self.assertEqual({r['name'] for r in mapper['table_candidates']}, {'T_PEOPLE', 'T_ORG'})
        self.assertEqual({r['id'] for r in mapper['statements']}, {'query', 'change'})
        before = result['source_tree_sha256']
        self.write('resources/templates/people/detail.html', '<h1>Changed</h1>')
        self.assertNotEqual(extract.extract(self.root)['source_tree_sha256'], before)
    def test_cli_determinism_and_missing_source(self):
        self.fixture(); out = Path(self.tmp.name) / 'catalog.json'
        cmd = [sys.executable, str(SCRIPT), '--source-root', str(self.root), '--output', str(out)]
        subprocess.run(cmd, check=True, capture_output=True); first = out.read_bytes()
        subprocess.run(cmd, check=True, capture_output=True); self.assertEqual(out.read_bytes(), first)
        failed = subprocess.run([sys.executable, str(SCRIPT), '--source-root', str(out), '--output', str(out)], capture_output=True)
        self.assertNotEqual(failed.returncode, 0)
        self.assertEqual(out.read_bytes(), first)

if __name__ == '__main__': unittest.main()
