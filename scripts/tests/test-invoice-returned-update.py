#!/usr/bin/env python3
"""Exercise the original invoice return update with Spring transactions and temporary JDBC fixtures.

Prerequisites are caller-selected, read-only source-only class outputs and public
dependencies. The changed service, bean and interfaces are recompiled into a
temporary directory placed before those outputs. Only a temporary in-process H2 database is started; no application or live database is touched.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
PACKAGE = 'com/sunwayworld/emc/module/mdm/invoices'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--case', default='all', choices=['partial', 'delete-absent', 'delete-mixed', 'delete-rollback', 'parent-overpay', 'parent-transitions', 'parent-notes', 'parent-forged-status', 'parent-forged-unpaid', 'parent-forged-direct', 'parent-missing-amount', 'parent-batch-reject', 'parent-batch-rollback', 'parent-direct', 'parent-no-returns', 'parent-duplicate', 'overpay', 'batch-net', 'batch-partial', 'batch-overpay', 'multiple-parent', 'multiple-parent-valid', 'forged', 'notes', 'clear-note', 'raw-notes', 'direct', 'rollback', 'direct-rollback', 'missing', 'null-id', 'null-amount', 'missing-amount', 'duplicate', 'empty', 'all'])
    args = parser.parse_args()
    prereq = args.prerequisite_root.resolve()
    jdk = prereq / 'build/jdk1.8.0_391/bin'
    public_deps = prereq / 'build/reconstituted-public-deps'
    application = prereq / 'build/source-only-application/classes'
    framework = sorted((prereq / 'reconstituted/framework').glob('*/target/classes'))
    required = [args.h2, jdk / 'java', jdk / 'javac', application / (PACKAGE + '/service/EmcInvoiceService.class'),
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
        'bean/EmcInvoiceBean.java', 'dao/EmcInvoiceDao.java', 'service/EmcInvoiceService.java',
        'service/impl/EmcInvoiceServiceImpl.java', 'bean/EmcReturnedBean.java',
        'dao/EmcReturnedDao.java', 'service/EmcReturnedService.java',
        'service/impl/EmcReturnedServiceImpl.java', 'resource/EmcInvoiceResource.java',
        'resource/impl/EmcInvoiceResourceImpl.java']]
    sources.extend([ROOT / 'scripts/tests/InvoiceReturnedUpdateTest.java',
        ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mybatis/dao/MybatisDaoSupport.java',
        ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/at/aspect/AuditTrailAspect.java',
        ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/module/sys/metadata/service/impl/CoreTableServiceImpl.java'])
    with tempfile.TemporaryDirectory(prefix='invoice-returned-update-') as directory:
        temporary = Path(directory)
        logging = temporary / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Loggers><Root level="OFF"/></Loggers></Configuration>')
        cp = ':'.join(map(str, [temporary, application, *framework, args.h2.resolve(), *dependencies]))
        subprocess.run([str(jdk / 'javac'), '-encoding', 'UTF-8', '-sourcepath', str(temporary),
                        '-cp', cp, '-d', str(temporary), *map(str, sources)], check=True)
        scenarios = ['partial', 'delete-absent', 'delete-mixed', 'delete-rollback', 'parent-overpay', 'parent-transitions', 'parent-notes', 'parent-forged-status', 'parent-forged-unpaid', 'parent-forged-direct', 'parent-missing-amount', 'parent-batch-reject', 'parent-batch-rollback', 'parent-direct', 'parent-no-returns', 'parent-duplicate', 'overpay', 'batch-net', 'batch-partial', 'batch-overpay', 'multiple-parent', 'multiple-parent-valid', 'forged', 'notes', 'clear-note', 'raw-notes', 'direct', 'rollback', 'direct-rollback', 'missing', 'null-id', 'null-amount', 'missing-amount', 'duplicate', 'empty'] if args.case == 'all' else [args.case]
        for scenario in scenarios:
            result = subprocess.run([str(jdk / 'java'), '-Dlog4j.configurationFile=' + str(logging),
                                     '-cp', cp, 'InvoiceReturnedUpdateTest', scenario])
            if result.returncode:
                return result.returncode
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
