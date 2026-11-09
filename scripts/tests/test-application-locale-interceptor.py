#!/usr/bin/env python3
"""Exercise actual application locale beans and MVC registration via DispatcherServlet.

Both current configuration sources are compiled ahead of previously source-built
framework classes. No application, database, Redis or supplier artifact is used.
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
    sources = [ROOT/'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/boot'/name
               for name in ('ApplicationConfiguration.java', 'ApplicationMvcConfiguration.java')]
    sources.append(ROOT/'scripts/tests/fixtures/locale-interceptor/ApplicationLocaleInterceptorCheck.java')
    for path in [args.spring_test, args.jdk/'bin/javac', args.public_deps, *sources, *classes]:
        if not path.exists():
            parser.error('missing source-only test prerequisite: ' + str(path))
    if not classes:
        parser.error('framework must be built from source first')
    with tempfile.TemporaryDirectory(prefix='application-locale-interceptor-') as temporary:
        temp = Path(temporary)
        (temp/'log4j2-test.xml').write_text(
            '<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>\n')
        cp = ':'.join(map(str, [temp, *classes, args.spring_test, args.public_deps/'*']))
        subprocess.run([str(args.jdk/'bin/javac'), '-encoding', 'UTF-8', '-parameters',
                        '-cp', cp, '-d', str(temp), *map(str, sources)], check=True)
        subprocess.run([str(args.jdk/'bin/java'), '-Dlog4j.configurationFile='+str(temp/'log4j2-test.xml'),
                        '-cp', cp, 'ApplicationLocaleInterceptorCheck'], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
