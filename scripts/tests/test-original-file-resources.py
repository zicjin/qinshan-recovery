#!/usr/bin/env python3
"""Run actual MVC resource registration/handlers in memory, without DB or sockets."""
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
    parser.add_argument('--case', choices=['mapping', 'security', 'all'], default='all')
    args = parser.parse_args()
    for path in [args.jdk/'bin/javac', args.framework_jar, args.public_deps, args.spring_test]:
        if not path.exists():
            parser.error(f'missing source-only test prerequisite: {path}')
    with tempfile.TemporaryDirectory(prefix='original-file-resources-') as temporary:
        temp = Path(temporary)
        (temp/'static').mkdir()
        (temp/'static/file-resource-fixture.txt').write_text('static-fixture\n')
        cp = ':'.join(map(str, [temp, args.framework_jar, args.public_deps/'*', args.spring_test]))
        source_root = ROOT/'reconstituted/framework/owned-core-common/src/main/java'
        sources = [source_root/'com/sunwayworld/boot/ApplicationMvcConfiguration.java',
                   source_root/'com/sunwayworld/framework/io/filter/FileSecureFilter.java',
                   ROOT/'scripts/tests/fixtures/file-resources/OriginalFileResourcesCheck.java']
        subprocess.run([str(args.jdk/'bin/javac'), '-source', '8', '-target', '8', '-encoding', 'UTF-8',
                        '-cp', cp, '-d', str(temp), *map(str, sources)], check=True)
        subprocess.run([str(args.jdk/'bin/java'), '-cp', cp, 'OriginalFileResourcesCheck', str(temp), args.case], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
