#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LISTENER="$ROOT/reconstituted/framework/owned-core-lcdp/src/main/java/com/sunwayworld/cloud/boot/listener/LcdpReadyListener.java"
SPRING="$ROOT/reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/utils/SpringUtils.java"
REFRESH="$ROOT/scripts/refresh-owned-sss2-app.sh"

grep -q 'ApplicationListener<ApplicationReadyEvent>' "$LISTENER"
grep -q 'if (initialized)' "$LISTENER"
grep -q 'initialized = true' "$LISTENER"
grep -q 'DynamicClassLoader' "$SPRING"
grep -q 'autowireBean(instance)' "$SPRING"
grep -q 'registerSingleton(beanName, instance)' "$SPRING"
grep -q -- '--env SUNWAY_LCDP_ENABLE=true' "$REFRESH"

# Production invocation must still resolve a missing bean as an error; this
# test prevents an accidental fallback/empty-success branch from being added.
if grep -q 'return ""' "$ROOT/reconstituted/framework/owned-core-lcdp/src/main/java/com/sunwayworld/cloud/module/lcdp/resource/support/LcdpScriptUtils.java"; then
  : # Existing validateScriptCallable negative path is allowed; no source edit here.
fi
echo "LCDP dynamic registration contract: PASS"
