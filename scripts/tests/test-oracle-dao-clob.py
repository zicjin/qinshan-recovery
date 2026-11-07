#!/usr/bin/env python3
"""Execute tracked Oracle DAO Mappers using real contexts, MyBatis and local JDBC.

Public dependencies/previously compiled owned classes are caller-selected read-only
inputs. The fixture compiles only test Java into its own temporary directory.
H2 plus a full-stream DBMS_LOB.COMPARE adapter checks semantics, not Oracle syntax.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, required=True)
    parser.add_argument('--public-deps', type=Path, required=True)
    parser.add_argument('--framework-classes', type=Path, required=True)
    parser.add_argument('--h2', type=Path, required=True)
    parser.add_argument('--emit-oracle-sql', type=Path)
    args = parser.parse_args()
    for path in [args.jdk/'bin/javac', args.public_deps, args.framework_classes, args.h2]:
        if not path.exists(): parser.error('missing prerequisite: '+str(path))
    with tempfile.TemporaryDirectory(prefix='oracle-dao-clob-') as tmp:
        cp=':'.join(map(str,[tmp,args.framework_classes,args.h2,args.public_deps/'*']))
        source=ROOT/'scripts/tests/fixtures/oracle-dao-clob/OracleDaoClobCheck.java'
        subprocess.run([str(args.jdk/'bin/javac'),'-encoding','UTF-8','-cp',cp,'-d',tmp,str(source)],check=True)
        resources=ROOT/'reconstituted/framework/owned-core-common/src/main/resources/com/sunwayworld/framework/mybatis/mapper/conf'
        subprocess.run([str(args.jdk/'bin/java'),'-cp',cp,'com.sunwayworld.framework.mybatis.mapper.OracleDaoClobCheck',str(resources/'dao-oracle-mapper.xml'),str(resources/'map-dao-oracle-mapper.xml')]+([str(args.emit_oracle_sql)] if args.emit_oracle_sql else []),check=True)

if __name__=='__main__': main()
