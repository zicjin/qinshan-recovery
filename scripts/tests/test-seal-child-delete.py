#!/usr/bin/env python3
"""Exercise the original seal rule child deletion with Spring transactions and temporary JDBC fixtures.

Prerequisites are caller-selected, read-only source-only class outputs and public
dependencies. The changed service, bean and interfaces are recompiled into a
temporary directory placed before those outputs. Only a temporary in-process H2 database is started; no application or live database is touched.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
PACKAGE = 'com/sunwayworld/emc/module/mdm/basemdm/sealrule'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--case',default='all',choices=['create-absent', 'update-reparent', 'create-null', 'create-empty', 'create-valid', 'create-rollback', 'update-valid', 'update-mixed', 'update-null-parent', 'update-missing', 'update-null-id', 'update-raw', 'update-rollback',  'foreign', 'mixed', 'forged', 'null-id', 'null-parent', 'stored-null', 'valid', 'valid-forged', 'missing', 'empty', 'rollback', 'all'])
    args = parser.parse_args()
    prereq = args.prerequisite_root.resolve()
    jdk = prereq / 'build/jdk1.8.0_391/bin'
    public_deps = prereq / 'build/reconstituted-public-deps'
    application = prereq / 'build/source-only-application/classes'
    framework = sorted((prereq / 'reconstituted/framework').glob('*/target/classes'))
    required = [args.h2, jdk / 'java', jdk / 'javac', application / (PACKAGE + '/service/EmcSealRuleService.class'),
                public_deps / 'spring-context-5.2.9.RELEASE.jar']
    for path in required:
        if not path.is_file():
            parser.error('missing read-only test prerequisite: ' + str(path))
    if len(framework) != 7:
        parser.error('expected seven precompiled source-only framework module outputs')
    dependencies = sorted(public_deps.glob('*.jar'))
    if any(path.name.startswith('gikam-') or path.name.endswith('-run.jar') for path in dependencies):
        parser.error('supplier/private JAR is forbidden in the public dependency directory')
    sources = [ROOT / 'project/src/main/java' / PACKAGE / name for name in [
        'bean/EmcSealRuleBean.java','bean/EmcSealRuleDetailsBean.java',
        'dao/EmcSealRuleDao.java','dao/EmcSealRuleDetailsDao.java',
        'service/EmcSealRuleService.java','service/EmcSealRuleDetailsService.java',
        'service/impl/EmcSealRuleServiceImpl.java','service/impl/EmcSealRuleDetailsServiceImpl.java',
        'resource/EmcSealRuleResource.java','resource/impl/EmcSealRuleResourceImpl.java']]
    sources.extend([ROOT / 'scripts/tests/SealChildDeleteTest.java',
        ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mybatis/dao/MybatisDaoSupport.java',
        ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/at/aspect/AuditTrailAspect.java',
        ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/module/sys/metadata/service/impl/CoreTableServiceImpl.java'])
    with tempfile.TemporaryDirectory(prefix='seal-child-delete-') as directory:
        temporary = Path(directory)
        logging = temporary / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Loggers><Root level="OFF"/></Loggers></Configuration>')
        cp = ':'.join(map(str, [temporary, application, *framework, args.h2.resolve(), *dependencies]))
        subprocess.run([str(jdk / 'javac'), '-encoding', 'UTF-8', '-sourcepath', str(temporary),
                        '-cp', cp, '-d', str(temporary), *map(str, sources)], check=True)
        scenarios = ['create-absent', 'update-reparent', 'create-null', 'create-empty', 'create-valid', 'create-rollback', 'update-valid', 'update-mixed', 'update-null-parent', 'update-missing', 'update-null-id', 'update-raw', 'update-rollback',  'foreign', 'mixed', 'forged', 'null-id', 'null-parent', 'stored-null', 'valid', 'valid-forged', 'missing', 'empty', 'rollback'] if args.case == 'all' else [args.case]
        for scenario in scenarios:
            result = subprocess.run([str(jdk / 'java'), '-Dlog4j.configurationFile=' + str(logging),
                                     '-cp', cp, 'SealChildDeleteTest', scenario])
            if result.returncode:
                return result.returncode
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
