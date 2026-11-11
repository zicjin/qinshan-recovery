#!/usr/bin/env python3
"""Test public custom SQL service methods with controlled, in-process fixtures.

The JDBC metadata and query fixture is H2 in Oracle mode; DAO writes are held
only in memory. This is neither an HTTP/production test nor a transaction
rollback test. Precompiled source-only prerequisites are read without rebuilding.
"""
import argparse
import importlib.util
import os
from pathlib import Path
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[2]
PACKAGE = 'com/sunwayworld/cloud/module/item/customds/service/impl'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--case', choices=['preview', 'validate', 'save', 'reject', 'all'], default='all')
    args = parser.parse_args()
    prerequisite = args.prerequisite_root.resolve()
    jdk = prerequisite / 'build/jdk1.8.0_391/bin'
    public = prerequisite / 'build/reconstituted-public-deps'
    framework = sorted((prerequisite / 'reconstituted/framework').glob('*/target/classes'))
    for path in [args.h2, jdk / 'java', jdk / 'javac', public / 'spring-context-5.2.9.RELEASE.jar']:
        if not path.is_file():
            parser.error('missing read-only test prerequisite: ' + str(path))
    if len(framework) != 7:
        parser.error('expected seven precompiled source-only framework modules')
    dependencies = sorted(public.glob('*.jar'))
    if any(path.name.startswith('gikam-') or path.name.endswith('-run.jar') for path in dependencies):
        parser.error('supplier/private runtime JAR is forbidden')
    spec = importlib.util.spec_from_file_location('custom_sql_test_builder', ROOT / 'scripts/build-source-only-application.py')
    builder = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = builder
    spec.loader.exec_module(builder)
    sources = [ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/database/sql/ExternalLogicalSql.java',
               ROOT / 'reconstituted/framework/owned-core-base-service/src/main/java' / PACKAGE / 'CoreCustomDsSqlServiceImpl.java',
               ROOT / 'scripts/tests/CustomSqlExternalTest.java']
    with tempfile.TemporaryDirectory(prefix='custom-sql-external-') as directory:
        temporary = Path(directory)
        classes = temporary / 'classes'
        classes.mkdir()
        logging = temporary / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Loggers><Root level="OFF"/></Loggers></Configuration>')
        prerequisites = [*framework, args.h2.resolve(), *dependencies]
        compile_cp = os.pathsep.join(map(str, prerequisites))
        subprocess.run([str(jdk / 'javac'), '-proc:none', '-source', '8', '-target', '8', '-encoding', 'UTF-8',
                        '-sourcepath', str(temporary), '-cp', compile_cp, '-d', str(classes), *map(str, sources)], check=True)
        for mode, profile in [('local-view', 'external-schema-runtime-profile-local-view.example.json'),
                              ('direct-schema', 'external-schema-runtime-profile.example.json'),
                              ('original-db-link', None)]:
            resources = temporary / mode
            for relative in builder.EXTERNAL_SCHEMA_MAPPERS:
                target = resources / relative
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes((ROOT / 'project/src/main/java' / relative).read_bytes())
            builder.write_external_schema_build_contract(resources, ROOT / 'api-fixtures/schema' / profile if profile else None)
            cp = os.pathsep.join(map(str, [classes, resources, *prerequisites]))
            result = subprocess.run([str(jdk / 'java'), '-Dlog4j.configurationFile=' + str(logging), '-cp', cp,
                                     'CustomSqlExternalTest', mode, args.case])
            if result.returncode:
                return result.returncode
    print('custom SQL public-service external profile regression: PASS (controlled fixtures only)')


if __name__ == '__main__':
    raise SystemExit(main())
