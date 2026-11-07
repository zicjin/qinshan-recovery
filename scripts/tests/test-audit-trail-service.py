#!/usr/bin/env python3
"""Exercise the tracked audit aspect through Spring services and transactional Mappers.

Compile the changed aspect and fixture into a temporary directory. Existing owned
classes and public dependencies are read-only inputs; no application, Oracle,
Redis, supplier binary or network listener is started. H2 tests transaction/AOP
semantics, not Oracle SQL or real HTTP strict closure.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, required=True)
    parser.add_argument('--public-deps', type=Path, required=True)
    parser.add_argument('--framework-classes', type=Path, required=True)
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--case', default='all', choices=['long', 'all'])
    args = parser.parse_args()
    for path in (args.jdk / 'bin/javac', args.public_deps, args.framework_classes, args.h2):
        if not path.exists():
            parser.error('missing prerequisite: ' + str(path))
    with tempfile.TemporaryDirectory(prefix='audit-trail-service-') as temp:
        cp = ':'.join(map(str, (temp, args.framework_classes, args.h2, args.public_deps / '*')))
        aspect = ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/at/aspect/AuditTrailAspect.java'
        fixture = ROOT / 'scripts/tests/fixtures/audit-trail/AuditTrailServiceCheck.java'
        subprocess.run([str(args.jdk / 'bin/javac'), '-encoding', 'UTF-8', '-cp', cp, '-d', temp, str(aspect), str(fixture)], check=True)
        subprocess.run([str(args.jdk / 'bin/java'), '-cp', cp, 'auditfixture.AuditTrailServiceCheck', args.case], check=True)


if __name__ == '__main__':
    main()
