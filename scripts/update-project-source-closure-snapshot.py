#!/usr/bin/env python3
"""Refresh the tracked diagnostic project closure from a verified source-only build."""
from __future__ import annotations
import argparse, json
from pathlib import Path

def main() -> int:
    parser=argparse.ArgumentParser()
    parser.add_argument('--application-report', type=Path, required=True)
    parser.add_argument('--snapshot', type=Path, required=True)
    parser.add_argument('--commit', required=True)
    args=parser.parse_args()
    report=json.loads(args.application_report.read_text(encoding='utf-8'))
    if report.get('status') != 'compiled-source-only' or report.get('compile_exit_code') != 0 or report.get('compile_error_count') != 0:
        raise SystemExit('source-only application report is not clean')
    if report.get('supplier_private_jars_on_classpath') is not False:
        raise SystemExit('source-only report uses supplier/private classpath')
    snapshot=json.loads(args.snapshot.read_text(encoding='utf-8'))
    snapshot['generated_from']='scripts/build-source-only-application.py current source-only application compile'
    snapshot['source_commit']=args.commit
    snapshot['project_compile']={
      'project_source_file_count': report.get('project_source_file_count'),
      'provided_project_class_count': len(report.get('provided_by_reconstituted_modules', [])),
      'compile_target_source_file_count': report.get('compile_target_source_file_count'),
      'java_version': report.get('java_version'), 'exit_code': report.get('compile_exit_code'),
      'error_count': report.get('compile_error_count'), 'failed_source_count': 0,
      'missing_symbol_queue': [], 'source_only_framework_classpath': True,
      'supplier_private_jars_on_classpath': report.get('supplier_private_jars_on_classpath'),
      'status':'pass'
    }
    args.snapshot.write_text(json.dumps(snapshot,ensure_ascii=False,indent=2,sort_keys=True)+'\n',encoding='utf-8')
    print(json.dumps({'status':'pass','snapshot':str(args.snapshot),'project_source_file_count':report.get('project_source_file_count'),'compile_target_source_file_count':report.get('compile_target_source_file_count')}))
    return 0
if __name__=='__main__': raise SystemExit(main())
