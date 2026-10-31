#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
STACK="$ROOT/scripts/stack-demo-restore.sh"
bash -n "$STACK"
grep -Fq 'assert_archiver_image' "$STACK" || { echo "replay config guard: archiver identity preflight missing" >&2; exit 1; }
grep -Fq 'snapshot volume archiver image identity does not match' "$STACK" || { echo "replay config guard: snapshot archiver pin check missing" >&2; exit 1; }
if grep -Fq ' -v "$SNAPSHOT_VOLUME:/data:ro" "$ARCHIVER_IMAGE"' "$STACK"; then
  echo "replay config guard: snapshot marker probe uses mutable archiver tag" >&2
  exit 1
fi
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
config="$tmp/application-isolated.properties"
write_base() {
  cat > "$config" <<'EOF'
spring.datasource.url = jdbc:oracle:thin:@oracle:1521/XEPDB1
spring.redis.url: redis://:redispass@redis:6379
spring.profiles.active = dev, oracle, logging
sunway.security.file-access-permission.enabled=true
sunway.api.check-access-permission=false
EOF
}
check_config() {
  INSTANCE_CONFIG_FILE="$config" INSTANCE_REDIS_PASSWORD=redispass bash -c 'source "$1"; assert_instance_isolated_config' _ "$STACK"
}
write_base
check_config
printf 'spring.mail.username = user@exmail.qq.com\n' >> "$config"
check_config
printf 'spring.profiles.include : schedule\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: colon/whitespace included scheduler was accepted" >&2
  exit 1
fi
write_base
printf 'spring.profiles.active = dev, equipt-acq-file\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: whitespace active acquisition profile was accepted" >&2
  exit 1
fi
write_base
printf 'spring.profiles.include schedule\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: whitespace-delimited included scheduler was accepted" >&2
  exit 1
fi
write_base
printf 'spring.profiles.group.safe = schedule\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: profile-group scheduler was accepted" >&2
  exit 1
fi
write_base
printf 'spring.profiles.include[0]=schedule\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: indexed included scheduler was accepted" >&2
  exit 1
fi
write_base
printf 'spring.profiles.active=dev,\\\n schedule\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: continued scheduler profile was accepted" >&2
  exit 1
fi
write_base
printf 'sunway.config.cloud.file-server.url=http://10.100.\\x32\\x30\\x30\\x31\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: escaped external endpoint was accepted" >&2
  exit 1
fi
write_base
printf 'spring.profiles.include\f=\fschedule\n' >> "$config"
if check_config >/dev/null 2>&1; then
  echo "replay config guard: control-character profile was accepted" >&2
  exit 1
fi
echo "replay config guard test PASS"
