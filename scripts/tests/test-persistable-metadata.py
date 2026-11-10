#!/usr/bin/env python3
"""Check metadata clearing through the real source-built PersistableHelper.

The caller supplies a JDK, controlled public dependencies and previously
source-built framework classes. Current helper/bean sources and the fixture
are compiled into a disposable directory; no shared target or runtime is used.
"""
import argparse
import os
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, required=True)
    parser.add_argument('--public-deps', type=Path, required=True)
    parser.add_argument('--framework-classes', type=Path, required=True)
    parser.add_argument('--case', choices=['reset-request', 'remove-created', 'all'], default='all')
    args = parser.parse_args()
    jdk = args.jdk.resolve() / 'bin'
    classes = args.framework_classes.resolve()
    dependencies = sorted(args.public_deps.resolve().glob('*.jar'))
    for path in [jdk / 'java', jdk / 'javac',
                 classes / 'com/sunwayworld/framework/beans/BeanPropertyHelper.class']:
        if not path.is_file():
            parser.error('missing source-only prerequisite: ' + str(path))
    if not dependencies or any(p.name.startswith(('gikam-', 'lims2-report-template-'))
                               or p.name.endswith('-run.jar') for p in dependencies):
        parser.error('only controlled public dependencies may be supplied')
    source_root = ROOT / 'reconstituted/framework/owned-core-common/src/main/java'
    sources = [source_root / relative for relative in [
        'com/sunwayworld/framework/support/PersistableHelper.java',
        'com/sunwayworld/module/admin/request/bean/CoreRequestUrlBean.java',
        'com/sunwayworld/module/sys/code/bean/CoreCodeBean.java',
    ]]
    sources.append(ROOT / 'scripts/tests/fixtures/persistable-metadata/PersistableMetadataCheck.java')
    with tempfile.TemporaryDirectory(prefix='persistable-metadata-') as directory:
        temporary = Path(directory)
        classpath = os.pathsep.join(map(str, [temporary, classes, *dependencies]))
        compiled = subprocess.run([
            str(jdk / 'javac'), '-source', '8', '-target', '8', '-encoding', 'UTF-8',
            '-sourcepath', str(temporary), '-cp', classpath, '-d', str(temporary),
            *map(str, sources),
        ], timeout=45)
        if compiled.returncode:
            return compiled.returncode
        return subprocess.run([
            str(jdk / 'java'), '-cp', classpath, 'PersistableMetadataCheck', args.case,
        ], timeout=30).returncode


if __name__ == '__main__':
    raise SystemExit(main())
