#!/usr/bin/env python3
"""CLI boundary tests; mock Docker only, never assert SQL success from a mock."""
import json, os, shutil, subprocess, tempfile, unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
class ProbeCliTest(unittest.TestCase):
 def test_missing_image_writes_not_executed_without_starting_database(self):
  with tempfile.TemporaryDirectory() as d:
   d=Path(d); fake=d/'docker'; fake.write_text('#!/bin/sh\nif [ "$1 $2" = "image inspect" ]; then exit 1; fi\necho unexpected >&2\nexit 97\n');fake.chmod(0o755)
   output=d/'result.json'
   result=subprocess.run(['python3',str(ROOT/'scripts/run-database-probe.py'),'--dialect','oracle','--image','example/oracle@sha256:'+'a'*64,'--output',str(output)],env=dict(os.environ,PATH=str(d)+':'+os.environ['PATH']),capture_output=True,text=True)
   self.assertEqual(result.returncode,2,result.stderr)
   report=json.loads(output.read_text());self.assertEqual(report['status'],'not-executed');self.assertEqual(report['checks'],[]);self.assertEqual(report['reason'],'pinned-image-unavailable')
 def test_unpinned_image_is_rejected_before_docker(self):
  with tempfile.TemporaryDirectory() as d:
   output=Path(d)/'result.json'
   result=subprocess.run(['python3',str(ROOT/'scripts/run-database-probe.py'),'--dialect','postgresql','--image','postgres:16','--output',str(output)],capture_output=True,text=True)
   self.assertEqual(result.returncode,2)
   self.assertEqual(json.loads(output.read_text())['reason'],'image-must-be-digest-pinned')
 def test_fixture_sql_cannot_be_replaced_with_arbitrary_input(self):
  with tempfile.TemporaryDirectory() as d:
   output=Path(d)/'result.json'
   result=subprocess.run(['python3',str(ROOT/'scripts/run-database-probe.py'),'--dialect','oracle','--image','example/oracle@sha256:'+'a'*64,'--output',str(output),'--sql','select 1'],capture_output=True,text=True)
   self.assertEqual(result.returncode,2)
   self.assertFalse(output.exists())
 def test_non_amd64_image_never_starts(self):
  with tempfile.TemporaryDirectory() as d:
   d=Path(d); fake=d/'docker'; fake.write_text('#!/bin/sh\nif [ "$1 $2" = "image inspect" ]; then echo \'[{"Id":"sha256:fake","Os":"linux","Architecture":"arm64"}]\'; exit 0; fi\nexit 97\n');fake.chmod(0o755)
   output=d/'result.json'
   result=subprocess.run(['python3',str(ROOT/'scripts/run-database-probe.py'),'--dialect','oracle','--image','example/oracle@sha256:'+'a'*64,'--output',str(output)],env=dict(os.environ,PATH=str(d)+':'+os.environ['PATH']),capture_output=True,text=True)
   self.assertEqual(result.returncode,2,result.stderr)
   self.assertEqual(json.loads(output.read_text())['reason'],'requires-linux-amd64')
 def test_oracle_non_internal_network_aborts_before_container(self):
  with tempfile.TemporaryDirectory() as d:
   d=Path(d); fake=d/'docker'; fake.write_text("""#!/bin/sh
case "$1 $2" in
 'image inspect') echo '[{"Id":"sha256:fake","Os":"linux","Architecture":"amd64"}]';;
 'network create') echo test-network;;
 'network inspect') echo '[{"Internal":false,"Containers":{}}]';;
 'network rm') exit 0;;
 *) exit 97;;
esac
""");fake.chmod(0o755)
   output=d/'result.json'
   result=subprocess.run(['python3',str(ROOT/'scripts/run-database-probe.py'),'--dialect','oracle','--image','example/oracle@sha256:'+'a'*64,'--output',str(output)],env=dict(os.environ,PATH=str(d)+':'+os.environ['PATH']),capture_output=True,text=True)
   self.assertEqual(result.returncode,2,result.stderr)
   report=json.loads(output.read_text())
   self.assertEqual(report['reason'],'internal-network-verification-failed')
   self.assertTrue(report['cleanup']['network_removed'])
 @unittest.skipUnless(os.environ.get('DBPROBE_POSTGRES_IMAGE'),'set DBPROBE_POSTGRES_IMAGE to a locally available digest for real SQL negative control')
 def test_sql_error_is_a_failed_run_and_cleans_up(self):
  with tempfile.TemporaryDirectory() as d:
   root=Path(d);(root/'scripts').mkdir()
   shutil.copy(ROOT/'scripts/run-database-probe.py',root/'scripts/run-database-probe.py')
   shutil.copytree(ROOT/'scripts/db-slice',root/'scripts/db-slice')
   probe=root/'scripts/db-slice/postgresql-probe.sql'
   probe.write_text('select definitely_missing_column from definitely_missing_table;\n'+probe.read_text())
   output=root/'result.json'
   result=subprocess.run(['python3',str(root/'scripts/run-database-probe.py'),'--dialect','postgresql','--image',os.environ['DBPROBE_POSTGRES_IMAGE'],'--output',str(output),'--ready-timeout','90'],capture_output=True,text=True)
   self.assertEqual(result.returncode,1,result.stderr)
   report=json.loads(output.read_text())
   self.assertEqual(report['status'],'executed-fail')
   self.assertTrue(all(x['status']=='fail' for x in report['checks']))
   self.assertTrue(report['cleanup']['container_removed'])
 @unittest.skipUnless(os.environ.get('DBPROBE_ORACLE_IMAGE'),'set DBPROBE_ORACLE_IMAGE for real Oracle negative control')
 def test_oracle_sql_error_fails_and_removes_internal_network(self):
  with tempfile.TemporaryDirectory() as d:
   root=Path(d);(root/'scripts').mkdir()
   shutil.copy(ROOT/'scripts/run-database-probe.py',root/'scripts/run-database-probe.py')
   shutil.copytree(ROOT/'scripts/db-slice',root/'scripts/db-slice')
   probe=root/'scripts/db-slice/oracle-probe.sql'
   probe.write_text('select definitely_missing_column from definitely_missing_table;\n'+probe.read_text())
   output=root/'result.json'
   result=subprocess.run(['python3',str(root/'scripts/run-database-probe.py'),'--dialect','oracle','--image',os.environ['DBPROBE_ORACLE_IMAGE'],'--output',str(output),'--ready-timeout','180'],capture_output=True,text=True)
   self.assertEqual(result.returncode,1,result.stderr)
   report=json.loads(output.read_text())
   self.assertEqual(report['status'],'executed-fail')
   self.assertTrue(report['cleanup']['container_removed'])
   self.assertTrue(report['cleanup']['network_removed'])
   self.assertTrue(report['isolation']['docker_network_internal'])
if __name__=='__main__':unittest.main()
