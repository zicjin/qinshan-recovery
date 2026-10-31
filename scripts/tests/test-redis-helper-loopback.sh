#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
PORT="${REDIS_HELPER_TEST_PORT:-16379}"
TMP="$(mktemp -d)"
REDIS_PID=""
cleanup() {
  if [ -n "$REDIS_PID" ]; then
    redis-cli -h 127.0.0.1 -p "$PORT" shutdown nosave >/dev/null 2>&1 || true
    kill "$REDIS_PID" 2>/dev/null || true
    wait "$REDIS_PID" 2>/dev/null || true
  fi
  rm -rf "$TMP"
}
trap cleanup EXIT

bash "$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
redis-server --bind 127.0.0.1 --port "$PORT" --save '' --appendonly no --daemonize no >"$TMP/redis.log" 2>&1 &
REDIS_PID=$!
for _ in $(seq 1 40); do
  if redis-cli -h 127.0.0.1 -p "$PORT" ping 2>/dev/null | grep -qx PONG; then break; fi
  sleep 0.1
done
redis-cli -h 127.0.0.1 -p "$PORT" ping 2>/dev/null | grep -qx PONG

classes="$TMP/classes"
mkdir -p "$classes"
"$JDK/bin/javac" -source 8 -target 8 \
  -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" \
  -d "$classes" "$ROOT/scripts/tests/RedisHelperLoopbackTest.java"
CP="$classes:$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*"
"$JDK/bin/java" -cp "$CP" RedisHelperLoopbackTest "$PORT"
echo "test-redis-helper-loopback: PASS"
