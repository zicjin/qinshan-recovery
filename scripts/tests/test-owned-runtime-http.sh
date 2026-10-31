#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
PORT="${PORT:-18082}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
log="$(mktemp)"
smoke_classes="$(mktemp -d)"
"$JDK/bin/javac" -source 8 -target 8 \
  -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" \
  -d "$smoke_classes" \
  "$ROOT/owned-framework/src/test/java/com/sunwayworld/owned/internal/smoke/OwnedRuntimeSmokeApplication.java" \
  "$ROOT/owned-framework/src/test/java/com/sunwayworld/owned/internal/smoke/OwnedRuntimeSmokeController.java"
CP="$smoke_classes:$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*"
"$JDK/bin/java" -cp "$CP" com.sunwayworld.owned.internal.smoke.OwnedRuntimeSmokeApplication \
  --server.address=127.0.0.1 --server.port="$PORT" --spring.main.banner-mode=off \
  --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration \
  >"$log" 2>&1 &
pid=$!
ready=0
for attempt in $(seq 1 40); do
  if body="$(curl --max-time 1 -fsS "http://127.0.0.1:$PORT/owned-runtime/health" 2>/dev/null)" && [ "$body" = owned-runtime ]; then ready=1; break; fi
  sleep 0.25
done
kill "$pid" 2>/dev/null || true
wait "$pid" 2>/dev/null || true
[ "$ready" = 1 ] || { cat "$log" >&2; exit 1; }
bash "$ROOT/scripts/reject-supplier-runtime.sh" "$ROOT/owned-framework/target/lims-owned-framework-1.0.0.jar" "$ROOT/owned-framework/target/public-deps"
echo "owned runtime HTTP bootstrap: PASS (endpoint=/owned-runtime/health)"
