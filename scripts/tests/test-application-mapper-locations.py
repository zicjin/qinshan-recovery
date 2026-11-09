#!/usr/bin/env python3
"""Exercise the actual application SqlSessionFactory with original Oracle XML.

Only the production configuration source and a small Java fixture are compiled.
Compiler outputs and H2 live in a persistent temporary root; no server, Oracle,
Redis, distribution build or supplier class is used.
"""
import argparse
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--public-deps', type=Path, default=ROOT/'build/reconstituted-public-deps')
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--configuration-source', type=Path,
                        default=ROOT/'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/boot/ApplicationConfiguration.java')
    parser.add_argument('--case', choices=['registration', 'all'], default='all')
    args = parser.parse_args()
    classes = sorted((ROOT/'reconstituted/framework').glob('*/target/classes'))
    for path in [args.jdk/'bin/javac', args.public_deps, args.h2, args.configuration_source, *classes]:
        if not path.exists():
            parser.error('missing source-only prerequisite: ' + str(path))
    if not classes:
        parser.error('no previously compiled source-only framework classes')
    with tempfile.TemporaryDirectory(prefix='application-mapper-locations-') as temporary:
        temp = Path(temporary)
        # Each original resource appears once. Framework target/classes are copied
        # as bytecode-only inputs so their old XML cannot mask or duplicate source.
        bytecode = temp/'compiled-inputs'
        for folder in classes:
            for path in folder.rglob('*.class'):
                target = bytecode/path.relative_to(folder)
                target.parent.mkdir(parents=True, exist_ok=True)
                if target.exists() and target.read_bytes() != path.read_bytes():
                    raise AssertionError('conflicting compiled framework class: ' + str(target))
                shutil.copy2(path, target)
        resources = sorted((ROOT/'reconstituted/framework').glob('*/src/main/resources'))
        (temp/'log4j2-test.xml').write_text(
            '<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>\n')
        cp = ':'.join(map(str, [temp, bytecode, *resources, args.h2, args.public_deps/'*']))
        fixture = ROOT/'scripts/tests/fixtures/mapper-locations/ApplicationMapperLocationsCheck.java'
        subprocess.run([str(args.jdk/'bin/javac'), '-encoding', 'UTF-8', '-parameters', '-cp', cp,
                        '-d', str(temp), str(args.configuration_source), str(fixture)], check=True)
        subprocess.run([str(args.jdk/'bin/java'), '-Dlog4j.configurationFile=' + str(temp/'log4j2-test.xml'),
                        '-cp', cp, 'ApplicationMapperLocationsCheck', args.case], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
