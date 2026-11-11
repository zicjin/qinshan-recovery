#!/usr/bin/env python3
"""Exercise the local CoreFileManager with real files and source-built classes."""
import argparse
import hashlib
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
SPRING_TEST_SHA256 = 'aa16626fc8a56e7c05a55735f73c92c4ae3793f347c6713eec3ff3615430b5ee'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    parser.add_argument('--spring-test', type=Path, required=True,
                        help='controlled public spring-test 5.2.9.RELEASE JAR; SHA-256 is pinned')
    parser.add_argument('--case', choices=['bytes', 'paths', 'atomic', 'urls', 'selection', 'missing', 'all'], default='all')
    args = parser.parse_args()
    prerequisites = args.prerequisite_root.resolve()
    jdk = prerequisites / 'build/jdk1.8.0_391/bin'
    common = prerequisites / 'reconstituted/framework/owned-core-common/target/owned-core-common-1.0.0-SNAPSHOT.jar'
    extension = prerequisites / 'reconstituted/framework/owned-core-ext/target/owned-core-ext-1.0.0-SNAPSHOT.jar'
    dependencies = sorted((prerequisites / 'build/reconstituted-public-deps').glob('*.jar'))
    spring_test = args.spring_test.resolve()
    for path in [jdk / 'javac', jdk / 'java', common, extension, spring_test]:
        if not path.is_file():
            parser.error(f'missing source-only test prerequisite: {path}')
    if not dependencies or any(p.name.startswith('gikam-') or p.name.endswith('-run.jar') for p in dependencies):
        parser.error('only controlled public dependencies may be used')
    if hashlib.sha256(spring_test.read_bytes()).hexdigest() != SPRING_TEST_SHA256:
        parser.error('spring-test JAR does not match pinned public artifact')
    source = ROOT / 'owned-distribution/src/main/java/com/sunwayworld/emc/framework/file/LocalCoreFileManager.java'
    filter_source = ROOT / 'reconstituted/framework/owned-core-ext/src/main/java/com/sunwayworld/module/item/file/filter/CoreFileFtpFilter.java'
    fixture = ROOT / 'scripts/tests/fixtures/local-core-file-manager/LocalCoreFileManagerCheck.java'
    with tempfile.TemporaryDirectory(prefix='local-core-file-manager-') as directory:
        temporary = Path(directory)
        classpath = ':'.join(map(str, [temporary, common, extension, *dependencies, spring_test]))
        logging = temporary / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Loggers><Root level="OFF"/></Loggers></Configuration>')
        compiled = subprocess.run([str(jdk / 'javac'), '-source', '8', '-target', '8', '-encoding', 'UTF-8',
                        '-sourcepath', str(temporary), '-cp', classpath, '-d', str(temporary),
                        str(source), str(filter_source), str(fixture)])
        if compiled.returncode:
            return compiled.returncode
        return subprocess.run([str(jdk / 'java'), '-Dlog4j.configurationFile=' + str(logging), '-cp', classpath,
                               'LocalCoreFileManagerCheck', str(temporary), args.case], timeout=45).returncode
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
