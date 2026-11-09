#!/usr/bin/env python3
"""Test the actual multipart bean and public Tomcat parser without a server."""
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
    classes = sorted((ROOT/'reconstituted/framework').glob('*/target/classes'))
    for p in [args.jdk/'bin/javac', args.public_deps, *classes]:
        if not p.exists():
            parser.error('missing source-only test prerequisite: '+str(p))
    with tempfile.TemporaryDirectory(prefix='application-multipart-') as temporary:
        temp = Path(temporary)
        (temp/'log4j2-test.xml').write_text('<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>\n')
        cp = ':'.join(map(str, [temp, *classes, args.public_deps/'*']))
        source = ROOT/'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/boot/ApplicationConfiguration.java'
        fixture = ROOT/'scripts/tests/fixtures/multipart-config/ApplicationMultipartConfigCheck.java'
        subprocess.run([str(args.jdk/'bin/javac'), '-encoding', 'UTF-8', '-parameters', '-cp', cp,
                        '-d', str(temp), str(source), str(fixture)], check=True)
        for mode in ['defaults', 'configured', 'empty', 'invalid', 'directory-failure', 'parser']:
            work = temp/mode
            work.mkdir()
            subprocess.run([str(args.jdk/'bin/java'), '-Dlog4j.configurationFile='+str(temp/'log4j2-test.xml'),
                            '-Duser.dir='+str(work), '-cp', cp, 'ApplicationMultipartConfigCheck', mode], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
