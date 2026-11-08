#!/usr/bin/env python3
"""Exercise actual Java session/audit/filter handoff without runtime or DB access."""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--dependency-root', type=Path, default=ROOT,
                        help='existing source-only build outputs; read only')
    args = parser.parse_args()
    dependency_root = args.dependency_root.resolve()
    jdk = dependency_root / 'build/jdk1.8.0_391/bin'
    with tempfile.TemporaryDirectory(prefix='original-session-audit-handoff-') as temporary:
        cp = ':'.join(map(str, [Path(temporary),
            dependency_root / 'owned-framework/target/classes',
            dependency_root / 'reconstituted/framework/owned-core-common/target/classes',
            dependency_root / 'build/source-only-application/classes',
            dependency_root / 'owned-framework/target/public-deps/*']))
        sources = [ROOT / path for path in [
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpBoundary.java',
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedQueryAccessFilter.java',
            'project/src/main/java/com/sunwayworld/emc/framework/security/OriginalAuditTrailReadScope.java',
            'owned-framework/src/test/java/com/sunwayworld/framework/OwnedIdentityHttpBoundaryContractTest.java',
            'owned-framework/src/test/java/com/sunwayworld/framework/OwnedQueryAccessFilterContractTest.java',
            'scripts/tests/fixtures/identity-handoff/OriginalSessionAuditHandoffCheck.java']]
        subprocess.run([str(jdk/'javac'), '-source', '8', '-target', '8', '-encoding', 'UTF-8',
                        '-cp', cp, '-d', temporary, *map(str, sources)], check=True)
        subprocess.run([str(jdk/'java'), '-cp', cp,
                        'identityhandoff.OriginalSessionAuditHandoffCheck'], check=True)
        for name in ('OwnedIdentityHttpBoundaryContractTest', 'OwnedQueryAccessFilterContractTest'):
            subprocess.run([str(jdk/'java'), '-cp', cp,
                            'com.sunwayworld.framework.' + name], check=True)


if __name__ == '__main__':
    main()
