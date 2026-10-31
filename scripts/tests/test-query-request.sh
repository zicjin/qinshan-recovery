#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/test-classes:$ROOT/owned-framework/target/public-deps/*" com.sunwayworld.framework.QueryRequestContractTest
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/test-classes:$ROOT/owned-framework/target/public-deps/*" \
  com.sunwayworld.framework.QueryRequestMapperContractTest \
  "$ROOT/build/owned-contract-reference/global-oracle-mapper.xml" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/mapper/conf/emc-emp-oracle-mapper.xml"
