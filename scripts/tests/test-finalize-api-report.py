#!/usr/bin/env python3
import importlib.util
from pathlib import Path
import unittest
ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location('finalize_api_report', ROOT / 'tools/finalize_api_report.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

class FinalizeApiReportTest(unittest.TestCase):
    def test_declaration_only_disposable_and_persistent_cleanup_are_rejected(self):
        report = {'status':'executed-pass-pending-cleanup','strict_evidence':True,'production_access':False}
        for cleanup in ({key:True for key in ('application_stopped','oracle_stopped','redis_stopped','network_removed','volumes_removed','temporary_files_removed')},
            dict(cleanup_mode='persistent-oracle',application_stopped=True,redis_preserved=True,oracle_reused=True,oracle_preserved=True,baseline_schema_immutable=True,runtime_schema_restored=True,runtime_dirty=False,network_preserved=True,volume_preserved=True,temporary_files_removed=True)):
            with self.subTest(cleanup=cleanup), self.assertRaises(ValueError):
                module.finalize(report, cleanup)

if __name__ == '__main__': unittest.main()
