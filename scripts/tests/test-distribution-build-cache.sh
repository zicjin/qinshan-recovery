#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

first="$TMP/first.log"
second="$TMP/second.log"
OWNED_DISTRIBUTION_REUSE=1 "$ROOT/scripts/build-owned-distribution.sh" >"$first" 2>&1
OWNED_DISTRIBUTION_REUSE=1 "$ROOT/scripts/build-owned-distribution.sh" >"$second" 2>&1
grep -Fq 'owned distribution build reused:' "$second"
test -f "$ROOT/owned-distribution/target/.distribution-build-fingerprint"
test -f "$ROOT/owned-distribution/target/distribution/manifest.json"
key="$(cat "$ROOT/owned-distribution/target/.distribution-build-fingerprint")"
test -n "$key"
test -d "$ROOT/owned-distribution/target/$key/distribution"
test -L "$ROOT/owned-distribution/target/distribution"
test ! -e "$ROOT/owned-distribution/target/distribution/.build-fingerprint"
echo 'owned distribution build cache: PASS (content-addressed build, fingerprint hit, shipped output clean)'
