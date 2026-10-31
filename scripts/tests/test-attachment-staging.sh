#!/usr/bin/env bash
# Verify the attachment staging boundary and, when Docker/private sample
# inputs are available, exercise one real copy/hash receipt.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
STAGE="$ROOT/scripts/stage-attachments.sh"
APPCTL="$ROOT/scripts/app-ctl.sh"
REPLAY="$ROOT/scripts/stack-demo-restore.sh"
INSTANCE_ENV="$ROOT/scripts/instance-env.sh"

[ -x "$STAGE" ] || { echo "attachment staging test: helper is not executable" >&2; exit 1; }
for shell_script in "$STAGE" "$APPCTL" "$REPLAY" "$INSTANCE_ENV"; do
  bash -n "$shell_script"
done

grep -Fq 'cp -a /input/. /output/' "$STAGE" || {
  echo "attachment staging test: copy boundary missing" >&2; exit 1;
}
grep -Fq 'tree_sha256=' "$STAGE" || {
  echo "attachment staging test: tree receipt missing" >&2; exit 1;
}
grep -Fq "{{json .Options}}" "$STAGE" || {
  echo "attachment staging test: local-driver bind/options guard missing" >&2; exit 1;
}
grep -Fq 'baseline directory tree differs' "$STAGE" || {
  echo "attachment staging test: exact directory-tree verifier missing" >&2; exit 1;
}
grep -Fq 'injected extra tree was accepted' "$0" || {
  echo "attachment staging test: extra-file mutation case missing" >&2; exit 1;
}
grep -Fq 'local bind volume was accepted' "$0" || {
  echo "attachment staging test: bind-volume mutation case missing" >&2; exit 1;
}
grep -Fq 'type=volume,src=$ATTACHMENT_VOLUME,dst=$APP_ROOT/files' "$APPCTL" || {
  echo "attachment staging test: app does not use a named volume" >&2; exit 1;
}
grep -Fq 'ATTACHMENT_REQUIRED="${ATTACHMENT_REQUIRED:-1}"' "$APPCTL" || {
  echo "attachment staging test: safe required attachment default missing" >&2; exit 1;
}
grep -Fq -- '--verify-baseline' "$APPCTL" || {
  echo "attachment staging test: existing-volume baseline verification missing" >&2; exit 1;
}
grep -Fq 'assert_local_only_config' "$APPCTL" || {
  echo "attachment staging test: app-ctl config preflight missing" >&2; exit 1;
}
grep -Fq 'volume_options' "$APPCTL" || {
  echo "attachment staging test: app-ctl bind-volume options guard missing" >&2; exit 1;
}
grep -Fq 'docker container inspect lims-app' "$APPCTL" || {
  echo "attachment staging test: replacement existence check missing" >&2; exit 1;
}
grep -Fq 'instance_env_replay_subnet' "$REPLAY" || {
  echo "attachment staging test: replay subnet derivation missing" >&2; exit 1;
}
if grep -Fq 'src=$ATTACHMENT_ROOT,dst=$APP_ROOT/files' "$APPCTL" \
  || grep -Fq '$ATTACHMENT_ROOT:$APP_ROOT_PATH/files' "$REPLAY"; then
  echo "attachment staging test: host source is mounted directly at /files" >&2
  exit 1
fi

if ! command -v docker >/dev/null 2>&1 \
  || ! docker image inspect lims-app:14.1.5-built >/dev/null 2>&1 \
  || [ ! -d "$ROOT/private/instances/current06c2/files" ]; then
  echo "attachment staging test PASS (static; Docker/sample inputs unavailable)"
  exit 0
fi

volume="lims-test-stage-$RANDOM-$$"
guard_volume="lims-test-stage-guard-$RANDOM-$$"
extra_volume="lims-test-stage-extra-$RANDOM-$$"
bind_volume="lims-test-stage-bind-$RANDOM-$$"
bind_root=""
appctl_volume="lims-test-appctl-$RANDOM-$$"
appctl_network="lims-iso"
appctl_source=""
appctl_receipt=""
appctl_container_id=""
image_id="$(docker image inspect lims-app:14.1.5-built --format '{{.Id}}')"
docker volume create "$volume" >/dev/null
cleanup() {
  docker volume rm "$volume" >/dev/null 2>&1 || true
  docker volume rm "$guard_volume" >/dev/null 2>&1 || true
  docker volume rm "$extra_volume" >/dev/null 2>&1 || true
  docker volume rm "$bind_volume" >/dev/null 2>&1 || true
  docker volume rm "$appctl_volume" >/dev/null 2>&1 || true
  if [ -n "$appctl_container_id" ] \
    && [ "$(docker inspect lims-app --format '{{.Id}}' 2>/dev/null || true)" = "$appctl_container_id" ]; then
    docker rm -f lims-app >/dev/null 2>&1 || true
  fi
  [ -z "$appctl_source" ] || rm -rf "$appctl_source"
  [ -z "$appctl_receipt" ] || rm -f "$appctl_receipt"
  [ -z "$bind_root" ] || rm -rf "$bind_root"
}
trap cleanup EXIT
receipt="$($STAGE "$volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id")"
[[ "$receipt" =~ files=1[[:space:]]bytes=30053[[:space:]]tree_sha256=[0-9a-f]{64}$ ]] || {
  echo "attachment staging test: unexpected receipt: $receipt" >&2; exit 1;
}

docker volume create "$guard_volume" >/dev/null
$STAGE "$guard_volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id" >/dev/null
$STAGE --verify-baseline "$guard_volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id" >/dev/null
guard_before="$(docker run --rm --pull=never --network none -v "$guard_volume:/output:ro" --entrypoint sh "$image_id" -c 'sha256sum /output/secure/20240717/*/* | awk "{print \$1}"')"
if retry_output="$($STAGE "$guard_volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id" 2>&1)"; then
  echo "attachment staging test: non-empty volume was overwritten" >&2; exit 1
fi
guard_after="$(docker run --rm --pull=never --network none -v "$guard_volume:/output:ro" --entrypoint sh "$image_id" -c 'sha256sum /output/secure/20240717/*/* | awk "{print \$1}"')"
[ "$guard_before" = "$guard_after" ] || {
  echo "attachment staging test: failed retry changed existing volume" >&2; exit 1
}
docker run --rm --pull=never --network none --user 0 \
  -v "$guard_volume:/output:rw" --entrypoint sh "$image_id" -c \
  'file="$(find /output -type f -print -quit)"; printf tampered >> "$file"'
if $STAGE --verify-baseline "$guard_volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id" >/dev/null 2>&1; then
  echo "attachment staging test: baseline mutation was accepted" >&2; exit 1
fi

docker volume create "$extra_volume" >/dev/null
$STAGE "$extra_volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id" >/dev/null
docker run --rm --pull=never --network none --user 0 \
  -v "$extra_volume:/output:rw" --entrypoint sh "$image_id" -c \
  'mkdir -p /output/injected && printf injected > /output/injected/file.txt'
if $STAGE --verify-baseline "$extra_volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id" >/dev/null 2>&1; then
  echo "attachment staging test: injected extra tree was accepted" >&2; exit 1
fi

bind_root="$(mktemp -d)"
docker volume create --driver local --opt type=none --opt "device=$bind_root" --opt o=bind "$bind_volume" >/dev/null
if $STAGE "$bind_volume" "$(realpath "$ROOT/private/instances/current06c2/files")" "$image_id" >/dev/null 2>&1; then
  echo "attachment staging test: local bind volume was accepted" >&2; exit 1
fi

# A changed attachment volume must fail before app-ctl stops/replaces lims-app.
if docker container inspect lims-app >/dev/null 2>&1; then
  echo "attachment staging test: lims-app already exists; refusing lifecycle test" >&2
  exit 1
fi
appctl_source="$(mktemp -d)"
appctl_receipt="$(mktemp)"
mkdir -p "$appctl_source/secure"
printf 'approved baseline\n' > "$appctl_source/secure/sample.txt"
chmod -R go-rwx "$appctl_source"
docker network inspect "$appctl_network" >/dev/null
docker run -d --pull=never --name lims-app --network "$appctl_network" \
  --entrypoint sh "$image_id" -c 'sleep 600' >/dev/null
appctl_container_id="$(docker inspect lims-app --format '{{.Id}}')"
docker volume create \
  --label 'com.sunway.lims.isolated=true' \
  --label 'com.sunway.lims.isolated.component=attachments' \
  "$appctl_volume" >/dev/null
$STAGE "$appctl_volume" "$appctl_source" "$image_id" >/dev/null
source_receipt="$($STAGE --source-receipt "$appctl_source")"
{
  printf 'volume=%s\n' "$appctl_volume"
  printf 'volume_id=%s\n' "$(docker volume inspect "$appctl_volume" --format '{{.Name}}')"
  printf 'image_id=%s\n' "$image_id"
  printf 'stage_script_sha256=%s\n' "$(sha256sum "$STAGE" | awk '{print $1}')"
  printf '%s\n' "$source_receipt"
} > "$appctl_receipt"
chmod 600 "$appctl_receipt"
docker run --rm --pull=never --network none --user 0 \
  -v "$appctl_volume:/output:rw" --entrypoint sh "$image_id" -c \
  'printf tampered >> /output/secure/sample.txt'
appctl_output=""
appctl_status=0
appctl_output="$(NET_ISO="$appctl_network" PORT=29991 \
  ATTACHMENT_ROOT="$appctl_source" ATTACHMENT_VOLUME="$appctl_volume" \
  ATTACHMENT_RECEIPT="$appctl_receipt" bash "$APPCTL" up 2>&1)" || appctl_status=$?
[ "$appctl_status" -ne 0 ] || {
  echo "attachment staging test: app-ctl accepted a changed attachment baseline" >&2; exit 1;
}
grep -Fq '基线内容不匹配' <<<"$appctl_output" || {
  echo "attachment staging test: app-ctl failed before baseline verifier: $appctl_output" >&2; exit 1;
}
[ "$(docker inspect lims-app --format '{{.Id}}')" = "$appctl_container_id" ] || {
  echo "attachment staging test: app-ctl replaced lims-app before rejecting changed volume" >&2; exit 1;
}
echo "attachment staging test PASS"
