#!/usr/bin/env python3
"""Exercise actual application Redis beans against an in-memory wire boundary.

Freshly compile the configuration, helper and key serializer ahead of existing
source-built framework classes. Spring supplies the real template, cache writer
and @Cacheable interceptor; no running Redis, application or supplier JAR is used.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--public-deps', type=Path, default=ROOT/'build/reconstituted-public-deps')
    args = parser.parse_args()
    framework = ROOT/'reconstituted/framework'
    classes = sorted(framework.glob('*/target/classes'))
    common = framework/'owned-core-common/src/main/java/com/sunwayworld'
    sources = [common/path for path in (
        'boot/ApplicationConfiguration.java',
        'framework/cache/redis/RedisHelper.java',
        'framework/cache/redis/serializer/RedisKeySerializer.java',
    )]
    sources.append(ROOT/'scripts/tests/fixtures/redis-config/ApplicationRedisConfigCheck.java')
    for path in [args.jdk/'bin/javac', args.public_deps, *sources, *classes]:
        if not path.exists():
            parser.error('missing source-only test prerequisite: ' + str(path))
    if not classes:
        parser.error('framework must be built from source first')
    private_tmp = ROOT/'private/test-tmp'
    private_tmp.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='application-redis-config-', dir=private_tmp) as temporary:
        temp = Path(temporary)
        (temp/'log4j2-test.xml').write_text(
            '<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>\n')
        cp = ':'.join(map(str, [temp, *classes, args.public_deps/'*']))
        subprocess.run([str(args.jdk/'bin/javac'), '-encoding', 'UTF-8', '-parameters',
                        '-cp', cp, '-d', str(temp), *map(str, sources)], check=True)
        subprocess.run([str(args.jdk/'bin/java'), '-Djava.io.tmpdir='+str(temp),
                        '-Dlog4j.configurationFile='+str(temp/'log4j2-test.xml'),
                        '-cp', cp, 'ApplicationRedisConfigCheck'], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
