#!/usr/bin/env python3
"""Exercise the actual MVC converter configuration through DispatcherServlet.

Only public test dependencies and previously source-built framework classes are
used. No application, Oracle, Redis, listener or supplier binary is started.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--spring-test', type=Path, required=True)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--public-deps', type=Path, default=ROOT/'build/reconstituted-public-deps')
    args = parser.parse_args()
    classes = sorted((ROOT/'reconstituted/framework').glob('*/target/classes'))
    for p in [args.spring_test, args.jdk/'bin/javac', args.public_deps, *classes]:
        if not p.exists():
            parser.error('missing source-only test prerequisite: ' + str(p))
    if not classes:
        parser.error('framework must be built from source first')
    with tempfile.TemporaryDirectory(prefix='application-mvc-converters-') as temporary:
        temp = Path(temporary)
        (temp/'log4j2-test.xml').write_text(
            '<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>\n')
        cp = ':'.join(map(str, [temp, *classes, args.spring_test, args.public_deps/'*']))
        sources = [ROOT/'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/boot/ApplicationMvcConfiguration.java',
                   ROOT/'scripts/tests/fixtures/mvc-converters/ApplicationMvcConvertersCheck.java']
        subprocess.run([str(args.jdk/'bin/javac'), '-encoding', 'UTF-8', '-parameters',
                        '-cp', cp, '-d', str(temp), *map(str, sources)], check=True)
        subprocess.run([str(args.jdk/'bin/java'), '-Dlog4j.configurationFile='+str(temp/'log4j2-test.xml'),
                        '-cp', cp, 'ApplicationMvcConvertersCheck'], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
