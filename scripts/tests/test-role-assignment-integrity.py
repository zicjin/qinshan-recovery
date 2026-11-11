#!/usr/bin/env python3
"""Exercise the real role assignment service with in-memory DAO/cache boundaries.

Existing compiled owned classes and public dependencies are read-only inputs.
Only the changed service and fixture compile into an isolated temporary directory;
no application, database, Redis, network listener or shared build is started.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, required=True)
    parser.add_argument('--framework-jar', type=Path, required=True)
    parser.add_argument('--public-deps', type=Path, required=True)
    parser.add_argument('--temp-root', type=Path, required=True)
    args = parser.parse_args()
    for path in (args.jdk / 'bin/javac', args.framework_jar, args.public_deps, args.temp_root):
        if not path.exists():
            parser.error('missing prerequisite: ' + str(path))
    with tempfile.TemporaryDirectory(prefix='role-assignment-', dir=args.temp_root) as temp:
        cp = ':'.join(map(str, (temp, args.framework_jar, args.public_deps / '*')))
        sources = ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/module/sys/role/service/impl'
        fixture = ROOT / 'scripts/tests/fixtures/role-assignment/RoleAssignmentIntegrityCheck.java'
        subprocess.run([str(args.jdk / 'bin/javac'), '-parameters', '-encoding', 'UTF-8',
                        '-cp', cp, '-d', temp, str(sources / 'CoreRoleUserServiceImpl.java'),
                        str(sources / 'CoreRoleServiceImpl.java'), str(fixture)], check=True)
        subprocess.run([str(args.jdk / 'bin/java'), '-cp', cp, 'RoleAssignmentIntegrityCheck'], check=True)


if __name__ == '__main__':
    main()
