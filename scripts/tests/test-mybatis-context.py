#!/usr/bin/env python3
"""Compile the actual interceptor and exercise cold-user recursion through MyBatis/H2."""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT / 'build/jdk1.8.0_391')
    parser.add_argument('--public-deps', type=Path, default=ROOT / 'build/reconstituted-public-deps')
    parser.add_argument('--h2', type=Path, default=ROOT / 'private/test-deps/h2-2.2.224.jar')
    args = parser.parse_args()
    classes = sorted((ROOT / 'reconstituted/framework').glob('*/target/classes'))
    sources = [ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mybatis/interceptor/MybatisInterceptor.java',
               ROOT / 'scripts/tests/fixtures/mybatis-context/MybatisContextCheck.java']
    for path in [args.jdk / 'bin/javac', args.public_deps, args.h2, *classes, *sources]:
        if not path.exists():
            parser.error('missing source-only test prerequisite: ' + str(path))
    if not classes:
        parser.error('framework must be built from source first')
    temporary_root = ROOT / 'private/test-tmp'
    temporary_root.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='mybatis-context-', dir=temporary_root) as temporary:
        temp = Path(temporary)
        config = temp / 'log4j2-test.xml'
        config.write_text('<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>\n')
        cp = ':'.join(map(str, [temp, *classes, args.h2, args.public_deps / '*']))
        subprocess.run([str(args.jdk / 'bin/javac'), '-encoding', 'UTF-8', '-parameters',
                        '-cp', cp, '-d', str(temp), *map(str, sources)], check=True)
        subprocess.run([str(args.jdk / 'bin/java'), '-Dlog4j.configurationFile=' + str(config),
                        '-cp', cp, 'mybatiscontextfixture.MybatisContextCheck'], check=True)


if __name__ == '__main__':
    main()
