#!/usr/bin/env python3
"""Compile the real personnel/signature filters and exercise their servlet chain."""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--framework-jar', type=Path, default=ROOT/'reconstituted/framework/owned-core-common/target/owned-core-common-1.0.0-SNAPSHOT.jar')
    parser.add_argument('--public-deps', type=Path, default=ROOT/'build/reconstituted-public-deps')
    parser.add_argument('--spring-test', type=Path, required=True)
    args = parser.parse_args()
    for path in (args.jdk/'bin/javac', args.framework_jar, args.public_deps, args.spring_test):
        if not path.exists():
            parser.error('missing source-only test prerequisite: '+str(path))
    with tempfile.TemporaryDirectory(prefix='temp-file-content-guard-') as temporary:
        target = Path(temporary)
        cp = ':'.join(map(str, (target, args.framework_jar, args.public_deps/'*', args.spring_test)))
        sources = [ROOT/'project/src/main/java/com/sunwayworld/emc/framework/security/PersonnelSignatureFileFilter.java',
                   ROOT/'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/io/filter/FileSecureFilter.java',
                   ROOT/'scripts/tests/fixtures/file-resources/TempFileContentGuardCheck.java']
        subprocess.run([str(args.jdk/'bin/javac'), '-source', '8', '-target', '8', '-encoding', 'UTF-8',
                        '-cp', cp, '-d', str(target), *map(str, sources)], check=True)
        subprocess.run([str(args.jdk/'bin/java'), '-cp', cp,
                        'com.sunwayworld.emc.framework.security.TempFileContentGuardCheck'], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
