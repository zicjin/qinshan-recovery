#!/usr/bin/env python3
"""Compile the original selectable service in isolation and exercise request parameters/MyBatis/H2.

No application, Oracle, Redis, supplier binary or network listener is started.
H2 Oracle mode checks regression semantics; real Oracle HTTP remains a separate gate.
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
    args = parser.parse_args()
    for value in (v for v in vars(args).values() if isinstance(v, Path)):
        if not value.exists():
            parser.error('missing prerequisite: ' + str(value))
    with tempfile.TemporaryDirectory(prefix='selectable-query-') as temp:
        cp = ':'.join(map(str, (temp, args.framework_classes, args.app_classes, args.h2,
                               args.spring_test, args.public_deps / '*')))
        sources = [ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/support/selectable/service/GenericSelectableService.java',
                   ROOT / 'scripts/tests/fixtures/selectable-query/SelectableQueryCheck.java']
        subprocess.run([str(args.jdk / 'bin/javac'), '-encoding', 'UTF-8', '-parameters', '-cp', cp, '-d', temp,
                        *map(str, sources)], check=True)
        subprocess.run([str(args.jdk / 'bin/java'), '-cp', cp,
                        'selectablefixture.SelectableQueryCheck', str(ROOT)], check=True)


if __name__ == '__main__':
    main()
