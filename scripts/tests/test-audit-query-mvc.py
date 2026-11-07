#!/usr/bin/env python3
"""Compile the audit read seam in isolation and exercise DispatcherServlet/MyBatis/H2.

No application, Oracle, Redis, supplier binary or network listener is started.
H2 Oracle mode checks regression semantics; real Oracle HTTP remains a separate gate.
The separator is absent by default; --separator preserves an explicit configuration.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    for name in ('jdk', 'public-deps', 'framework-classes', 'app-classes', 'h2', 'spring-test'):
        parser.add_argument('--' + name, type=Path, required=True)
    parser.add_argument("--separator", help="wrapper separator; omit to test missing configuration")
    args = parser.parse_args()
    for value in (v for v in vars(args).values() if isinstance(v, Path)):
        if not value.exists():
            parser.error('missing prerequisite: ' + str(value))
    with tempfile.TemporaryDirectory(prefix='audit-query-mvc-') as temp:
        cp = ':'.join(map(str, (temp, args.framework_classes, args.app_classes, args.h2,
                               args.spring_test, args.public_deps / '*')))
        sources = [ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/restful/data/RestJsonWrapperBean.java',
                   ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/AuditTrailReadOperations.java',
                   ROOT / 'project/src/main/java/com/sunwayworld/emc/framework/security/OriginalAuditTrailReadOperations.java',
                   ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedAuditTrailController.java',
                   ROOT / 'scripts/tests/fixtures/audit-query/AuditQueryMvcCheck.java']
        audit_base = ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/module/sys/at'
        sources += [audit_base / 'service/CoreAuditTrailRecordService.java',
                    ROOT / 'project/src/main/java/com/sunwayworld/emc/framework/security/OriginalAuditTrailReadScope.java']
        for kind, names in {'service/impl': ['CoreAuditTrailRecord', 'CoreAuditTrailRecordLine'],
                            'dao': ['CoreAuditTrailRecord', 'CoreAuditTrailRecordLine'],
                            'dao/impl': ['CoreAuditTrailRecord', 'CoreAuditTrailRecordLine'],
                            'mapper': ['CoreAuditTrailRecord', 'CoreAuditTrailRecordLine']}.items():
            suffix = 'ServiceImpl' if kind == 'service/impl' else 'DaoImpl' if kind == 'dao/impl' else 'Dao' if kind == 'dao' else 'Mapper'
            sources += [audit_base / kind / (name + suffix + '.java') for name in names]
        subprocess.run([str(args.jdk / 'bin/javac'), '-encoding', 'UTF-8', '-parameters', '-cp', cp, '-d', temp,
                        *map(str, sources)], check=True)
        subprocess.run([str(args.jdk / 'bin/java'), *(['-Daudit.fixture.separator=' + args.separator] if args.separator is not None else []), '-cp', cp,
                        'auditqueryfixture.AuditQueryMvcCheck', str(ROOT)], check=True)


if __name__ == '__main__':
    main()
