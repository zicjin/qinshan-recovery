#!/usr/bin/env bash
# 隔离副本应用的启动 / 停止 / 重启 / 状态，供各任务复用同一运行参照。
#
# 用法: scripts/app-ctl.sh {up|down|restart|forward|status|logs}
#   up      启动应用，并在宿主 127.0.0.1:8081 开启到容器的转发（internal 网络不支持 -p 发布）
#   down    停止应用与转发
#   restart 重启（停止后重新启动）
#   forward 仅修复宿主到当前应用容器的转发，不重启应用
#   status  容器与转发状态
#   logs    跟随应用日志
#
# 启动/停止/重启操作与结果记录在 private/t01-run-record.txt。
set -euo pipefail
umask 077

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RECORD="$ROOT/private/t01-run-record.txt"
FWD_PID_FILE="$ROOT/private/t01-portfwd.pid"
FWD_LOG="$ROOT/private/t01-portfwd.log"
LOCK_FILE="$ROOT/private/t01-app-ctl.lock"
NET_ISO="${NET_ISO:-lims-iso}"
PORT="${PORT:-8081}"
# 运行镜像：默认使用当前自包含构建镜像；可用 IMAGE 显式选择已审计本地镜像。
IMAGE="${IMAGE:-lims-app:14.1.5-built}"

# 许可证与机器网卡绑定（见 .scratch/sunway-supplier-exit-api-first/evidence-archive/t01-license-blocker.md）：容器复刻被许可机器的网卡标识，
# 许可证校验照常真实执行。严格解析受限材料，不把它作为 shell 脚本执行。
MAC_ADDRESS="${MAC_ADDRESS:-}"
if [ -z "$MAC_ADDRESS" ] && [ -f "$ROOT/private/license-machine.env" ]; then
  MAC_ADDRESS="$(awk -F= '
    /^[[:space:]]*#/ || /^[[:space:]]*$/ { next }
    $1 == "MAC_ADDRESS" { value = substr($0, index($0, "=") + 1); count += 1 }
    END { if (count == 1) print value; else exit 1 }
  ' "$ROOT/private/license-machine.env")" \
    || { echo "受限许可证机器标识格式非法" >&2; exit 1; }
fi
[ -z "$MAC_ADDRESS" ] || [[ "$MAC_ADDRESS" =~ ^([[:xdigit:]]{2}:){5}[[:xdigit:]]{2}$ ]] \
  || { echo "MAC_ADDRESS 格式非法" >&2; exit 1; }

APP_ROOT="/data/LIMS/jiankeyuan/lims-czjky-emc"
CONFIG_FILE="$ROOT/private/application-isolated.properties"
LICENSE_FILE="$ROOT/private/lic/license.lic"
VENDOR_LICENSE_FILE="$ROOT/private/lic/vendor.lic"
ATTACHMENT_ROOT="${ATTACHMENT_ROOT:-$ROOT/private/t06-attachment-sample/files}"
ATTACHMENT_REQUIRED="${ATTACHMENT_REQUIRED:-1}"
INDEX_ROOT="$ROOT/materials/index"
ATTACHMENT_VOLUME="${ATTACHMENT_VOLUME:-lims-app-files}"
ATTACHMENT_RECEIPT="${ATTACHMENT_RECEIPT:-$ROOT/private/lims-app-files.receipt}"

acquire_mutation_lock() {
  local lock_fd
  exec {lock_fd}>"$LOCK_FILE"
  flock -n "$lock_fd" || {
    echo "已有另一个 app-ctl 变更操作运行中；拒绝并发操作" >&2
    exit 1
  }
  # Keep the descriptor open until this shell exits.  The lock file itself is
  # harmless metadata; never delete it while another invocation may hold it.
}

assert_restricted_input() {
  local path="$1" mode owner
  [ -f "$path" ] && [ ! -L "$path" ] || { echo "缺少、非普通文件或链接化的受限运行输入: $path" >&2; exit 1; }
  mode="$(stat -c '%a' "$path")"
  owner="$(stat -c '%u' "$path")"
  (( (8#$mode & 0077) == 0 )) || { echo "受限运行输入权限过宽: $path" >&2; exit 1; }
  [ "$owner" = "$(id -u)" ] || { echo "受限运行输入所有者不匹配: $path" >&2; exit 1; }
}

assert_restricted_tree() {
  local root="$1" entry mode owner listing tree_error=""
  [ -d "$root" ] || { echo "受限运行输入不是目录: $root" >&2; exit 1; }
  listing="$(mktemp)"
  if ! find -P "$root" -print0 > "$listing"; then
    rm -f "$listing"
    echo "无法枚举受限运行输入目录: $root" >&2
    exit 1
  fi
  while IFS= read -r -d '' entry; do
    if { [ ! -d "$entry" ] && [ ! -f "$entry" ]; } || [ -L "$entry" ]; then tree_error="包含特殊文件或链接: $entry"; break; fi
    if ! mode="$(stat -c '%a' "$entry")" || ! owner="$(stat -c '%u' "$entry")"; then tree_error="无法检查权限: $entry"; break; fi
    if (( (8#$mode & 0077) != 0 )); then tree_error="权限过宽: $entry"; break; fi
    if [ "$owner" != "$(id -u)" ]; then tree_error="所有者不匹配: $entry"; break; fi
  done < "$listing"
  rm -f "$listing"
  [ -z "$tree_error" ] || { echo "受限运行输入 $tree_error" >&2; exit 1; }
}

assert_plain_tree() {
  local root="$1" entry listing tree_error=""
  [ -d "$root" ] || { echo "运行输入不是目录: $root" >&2; exit 1; }
  listing="$(mktemp)"
  if ! find -P "$root" -print0 > "$listing"; then
    rm -f "$listing"
    echo "无法枚举运行输入目录: $root" >&2
    exit 1
  fi
  while IFS= read -r -d '' entry; do
    if { [ ! -d "$entry" ] && [ ! -f "$entry" ]; } || [ -L "$entry" ]; then tree_error="$entry"; break; fi
  done < "$listing"
  rm -f "$listing"
  [ -z "$tree_error" ] || { echo "运行输入包含特殊文件或链接: $tree_error" >&2; exit 1; }
}

assert_local_only_config() {
  local active
  if LC_ALL=C grep -q $'\f' "$CONFIG_FILE" || LC_ALL=C grep -q $'\v' "$CONFIG_FILE" \
    || grep -q '\\' "$CONFIG_FILE"; then
    echo "隔离配置包含不允许的 Java-properties 转义、续行或控制字符" >&2
    exit 1
  fi
  active="$(grep -vE '^[[:space:]]*#|^[[:space:]]*$|^[[:space:]]*spring\.mail\.(username|password)[[:space:]]*[=:]' "$CONFIG_FILE")"
  if grep -qE '10\.100\.|218\.93\.|192\.168\.|58\.56\.|czjky\.com|exmail\.qq\.com|lims\.sunwayworld\.com' <<<"$active"; then
    echo "隔离配置仍包含生产或外部端点" >&2
    exit 1
  fi
  if awk '
    function property(line, key, value) {
      sub(/\r$/, "", line)
      sub(/^[ \t]*/, "", line)
      if (line == "" || line ~ /^[#!]/) return ""
      key = line
      if (line ~ /[=:]/) {
        sub(/[ \t]*[=:].*$/, "", key)
        value = line
        sub(/^[^:=]*[=:][ \t]*/, "", value)
      } else {
        sub(/[ \t].*$/, "", key)
        value = line
        sub(/^[^ \t]+[ \t]+/, "", value)
      }
      if (key == line || key == "") return ""
      return key SUBSEP value
    }
    {
      parsed = property($0)
      if (parsed == "") next
      split(parsed, fields, SUBSEP)
      if (fields[1] == "spring.profiles.active" || fields[1] == "spring.profiles.include" || fields[1] ~ /^spring\.profiles\.(active|include)\[/ || fields[1] ~ /^spring\.profiles\.group\./) {
        value = fields[2]
        gsub(/[[:space:]]/, "", value)
        if (value ~ /(^|,)(schedule|equipt-acq-file|ngtodo)(,|$)/) bad = 1
      }
    }
    END { exit bad }
  ' "$CONFIG_FILE"; then :; else
    echo "隔离配置启用了定时任务、文件采集或业务同步 profile" >&2
    exit 1
  fi
}

assert_runtime_inputs() {
  local restricted_input
  [[ "$ATTACHMENT_VOLUME" =~ ^[a-z][a-z0-9_.-]{0,127}$ ]] \
    || { echo "附件 staging volume 名称非法: $ATTACHMENT_VOLUME" >&2; exit 1; }
  for restricted_input in "$CONFIG_FILE" "$LICENSE_FILE" "$VENDOR_LICENSE_FILE"; do
    assert_restricted_input "$restricted_input"
  done
  assert_local_only_config
  [[ "$ATTACHMENT_REQUIRED" = 0 || "$ATTACHMENT_REQUIRED" = 1 ]] \
    || { echo "ATTACHMENT_REQUIRED 必须为 0 或 1" >&2; exit 1; }
  if [ -d "$ATTACHMENT_ROOT" ]; then
    assert_restricted_tree "$ATTACHMENT_ROOT"
    if [ "$ATTACHMENT_REQUIRED" = 1 ] && ! find -P "$ATTACHMENT_ROOT" -type f -print -quit | grep -q .; then
      echo "附件源目录为空且 ATTACHMENT_REQUIRED=1: $ATTACHMENT_ROOT" >&2
      exit 1
    fi
  elif [ "$ATTACHMENT_REQUIRED" = 1 ]; then
    echo "附件源目录缺失且 ATTACHMENT_REQUIRED=1: $ATTACHMENT_ROOT" >&2
    exit 1
  fi
  assert_plain_tree "$INDEX_ROOT"
  [[ "$IMAGE" =~ ^[a-z0-9][a-z0-9._-]{0,95}:[a-z0-9][a-z0-9._-]{0,95}$ ]] \
    || { echo "IMAGE 必须是本地 image:tag 引用" >&2; exit 1; }
  docker image inspect "$IMAGE" >/dev/null 2>&1 \
    || { echo "本地镜像不存在: $IMAGE；不会拉取远程镜像" >&2; exit 1; }
  IMAGE_ID="$(docker image inspect "$IMAGE" --format '{{.Id}}')"
  [ -n "$IMAGE_ID" ] || { echo "无法解析本地镜像身份: $IMAGE" >&2; exit 1; }
}

assert_internal_network() {
  docker network inspect "$NET_ISO" >/dev/null 2>&1 \
    || { echo "缺少隔离网络 $NET_ISO" >&2; exit 1; }
  [ "$(docker network inspect "$NET_ISO" --format '{{.Internal}}')" = "true" ] \
    || { echo "隔离网络 $NET_ISO 不是 internal 网络" >&2; exit 1; }
}

ensure_attachment_volume() {
  local created=false references reference driver unexpected stage_output source_receipt key value recorded
  local -A receipt=() seen=()
  if ! docker volume inspect "$ATTACHMENT_VOLUME" >/dev/null 2>&1; then
    docker volume create \
      --label 'com.sunway.lims.isolated=true' \
      --label 'com.sunway.lims.isolated.component=attachments' \
      "$ATTACHMENT_VOLUME" >/dev/null
    created=true
  else
    driver="$(docker volume inspect "$ATTACHMENT_VOLUME" --format '{{.Driver}}')"
    [ "$driver" = local ] \
      || { echo "附件 staging volume 不是 local driver: $ATTACHMENT_VOLUME" >&2; exit 1; }
    volume_options="$(docker volume inspect "$ATTACHMENT_VOLUME" --format '{{json .Options}}')"
    case "$volume_options" in
      null|{}) ;;
      *) echo "附件 staging volume 含 local-driver bind/options，拒绝使用: $ATTACHMENT_VOLUME" >&2; exit 1 ;;
    esac
    [ "$(docker volume inspect "$ATTACHMENT_VOLUME" --format '{{index .Labels "com.sunway.lims.isolated"}}')" = true ] \
      || { echo "附件 staging volume 缺少隔离标签: $ATTACHMENT_VOLUME" >&2; exit 1; }
    [ "$(docker volume inspect "$ATTACHMENT_VOLUME" --format '{{index .Labels "com.sunway.lims.isolated.component"}}')" = attachments ] \
      || { echo "附件 staging volume 组件标签不匹配: $ATTACHMENT_VOLUME" >&2; exit 1; }
    unexpected="$(docker run --rm --pull=never --network none --read-only \
      -v "$ATTACHMENT_VOLUME:/output:ro" --entrypoint sh "$IMAGE_ID" -c \
      'find -P /output -mindepth 1 ! \( -type f -o -type d \) -print -quit')"
    [ -z "$unexpected" ] \
      || { echo "附件 staging volume 包含特殊文件: $unexpected" >&2; exit 1; }
  fi

  references="$(docker ps -a --filter "volume=$ATTACHMENT_VOLUME" --format '{{.Names}}')"
  while IFS= read -r reference; do
    [ -z "$reference" ] && continue
    [ "$reference" = lims-app ] \
      || { echo "附件 staging volume 正被其他容器引用，拒绝启动: $reference" >&2; exit 1; }
  done <<< "$references"

  if [ "$created" = true ]; then
    if [ ! -d "$ATTACHMENT_ROOT" ]; then
      echo "附件源目录未提供；创建空的隔离附件 volume: $ATTACHMENT_VOLUME"
      return
    fi
    if ! stage_output="$(bash "$ROOT/scripts/stage-attachments.sh" "$ATTACHMENT_VOLUME" "$ATTACHMENT_ROOT" "$IMAGE_ID")"; then
      if docker volume rm "$ATTACHMENT_VOLUME" >/dev/null 2>&1; then
        echo "附件 staging 失败；新建 volume 已清理，既有应用未被停止" >&2
      else
        echo "附件 staging 失败且新建 volume 清理失败；保留现场，既有应用未被停止: $ATTACHMENT_VOLUME" >&2
      fi
      exit 1
    fi
    source_receipt="$(bash "$ROOT/scripts/stage-attachments.sh" --source-receipt "$ATTACHMENT_ROOT")"
    {
      printf 'volume=%s\n' "$ATTACHMENT_VOLUME"
      printf 'volume_id=%s\n' "$(docker volume inspect "$ATTACHMENT_VOLUME" --format '{{.Name}}')"
      printf 'image_id=%s\n' "$IMAGE_ID"
      printf 'stage_script_sha256=%s\n' "$(sha256sum "$ROOT/scripts/stage-attachments.sh" | awk '{print $1}')"
      printf '%s\n' "$source_receipt"
    } > "$ATTACHMENT_RECEIPT"
    chmod 600 "$ATTACHMENT_RECEIPT"
    printf '%s\n' "$stage_output"
    return
  fi

  [ -d "$ATTACHMENT_ROOT" ] || {
    [ "$ATTACHMENT_REQUIRED" = 1 ] && { echo "附件源目录缺失且 staging volume 已存在" >&2; exit 1; }
    return
  }

  [ -f "$ATTACHMENT_RECEIPT" ] && [ "$(stat -c '%a' "$ATTACHMENT_RECEIPT")" = 600 ] \
    || { echo "附件 staging receipt 缺失或权限不安全: $ATTACHMENT_RECEIPT" >&2; exit 1; }
  assert_restricted_input "$ATTACHMENT_RECEIPT"
  while IFS= read -r key || [ -n "$key" ]; do
    [[ "$key" =~ ^([a-z][a-z0-9_]*)=([^[:space:]]+)$ ]] \
      || { echo "附件 staging receipt 格式非法" >&2; exit 1; }
    line="$key"
    key="${line%%=*}"
    value="${line#*=}"
    case "$key" in
      volume|volume_id|image_id|stage_script_sha256|files|bytes|tree_sha256) ;;
      *) echo "附件 staging receipt 含未知键: $key" >&2; exit 1 ;;
    esac
    [ -z "${seen[$key]+set}" ] \
      || { echo "附件 staging receipt 含重复键: $key" >&2; exit 1; }
    seen["$key"]=1
    receipt["$key"]="$value"
  done < "$ATTACHMENT_RECEIPT"
  for key in volume volume_id image_id stage_script_sha256 files bytes tree_sha256; do
    [ -n "${seen[$key]+set}" ] \
      || { echo "附件 staging receipt 缺少键: $key" >&2; exit 1; }
  done
  [ "${receipt[volume]}" = "$ATTACHMENT_VOLUME" ] \
    || { echo "附件 staging receipt volume 不匹配" >&2; exit 1; }
  [ "${receipt[volume_id]}" = "$(docker volume inspect "$ATTACHMENT_VOLUME" --format '{{.Name}}')" ] \
    || { echo "附件 staging receipt volume_id 不匹配" >&2; exit 1; }
  [[ "${receipt[volume_id]}" =~ ^[a-zA-Z0-9_.-]+$ ]] \
    || { echo "附件 staging receipt volume_id 非法" >&2; exit 1; }
  [[ "${receipt[image_id]}" =~ ^sha256:[0-9a-f]{64}$ ]] \
    || { echo "附件 staging receipt image_id 非法" >&2; exit 1; }
  [[ "${receipt[stage_script_sha256]}" =~ ^[0-9a-f]{64}$ ]] \
    || { echo "附件 staging receipt 脚本哈希非法" >&2; exit 1; }
  [ "${receipt[stage_script_sha256]}" = "$(sha256sum "$ROOT/scripts/stage-attachments.sh" | awk '{print $1}')" ] \
    || { echo "附件 staging receipt 脚本哈希不匹配；需显式重新 staging" >&2; exit 1; }
  [[ "${receipt[files]}" =~ ^[0-9]+$ && "${receipt[bytes]}" =~ ^[0-9]+$ ]] \
    || { echo "附件 staging receipt 文件统计非法" >&2; exit 1; }
  [[ "${receipt[tree_sha256]}" =~ ^[0-9a-f]{64}$ ]] \
    || { echo "附件 staging receipt 树哈希非法" >&2; exit 1; }
  source_receipt="$(bash "$ROOT/scripts/stage-attachments.sh" --source-receipt "$ATTACHMENT_ROOT")"
  while IFS='=' read -r key value; do
    [ -z "$key" ] && continue
    [ "$key" = files ] || [ "$key" = bytes ] || [ "$key" = tree_sha256 ] \
      || { echo "附件源收据含未知键: $key" >&2; exit 1; }
    [ "${receipt[$key]}" = "$value" ] \
      || { echo "附件源已变化，拒绝复用 staging volume: $key" >&2; exit 1; }
  done <<< "$source_receipt"

  # The recorded image ID is provenance only: a rebuilt/tagged application
  # image may legitimately have a new ID.  The source receipt, staging-script
  # hash, and verified volume baseline are the reuse invariants.
  bash "$ROOT/scripts/stage-attachments.sh" --verify-baseline \
    "$ATTACHMENT_VOLUME" "$ATTACHMENT_ROOT" "$IMAGE_ID" \
    || { echo "附件 staging volume 基线内容不匹配，拒绝替换应用: $ATTACHMENT_VOLUME" >&2; exit 1; }

  docker run --rm --pull=never --network none --read-only \
    -v "$ATTACHMENT_VOLUME:/output:ro" --entrypoint sh "$IMAGE_ID" -c \
    'test -n "$(find /output -mindepth 1 -maxdepth 1 -print -quit)"' \
    || { echo "附件 staging volume 为空，拒绝启动: $ATTACHMENT_VOLUME" >&2; exit 1; }
}

docker_run_app() {
  local name_args=(--name lims-app --network "$NET_ISO" --network-alias app)
  [ -n "$MAC_ADDRESS" ] && name_args+=(--mac-address "$MAC_ADDRESS")
  docker run -d "${name_args[@]}" \
    --pull=never \
    --mount "type=bind,src=$CONFIG_FILE,dst=$APP_ROOT/application.properties,readonly" \
    --mount "type=bind,src=$LICENSE_FILE,dst=$APP_ROOT/lic/license.lic,readonly" \
    --mount "type=bind,src=$VENDOR_LICENSE_FILE,dst=$APP_ROOT/lic/vendor.lic,readonly" \
    --mount "type=volume,src=$ATTACHMENT_VOLUME,dst=$APP_ROOT/files" \
    --mount "type=bind,src=$INDEX_ROOT,dst=$APP_ROOT/index,readonly" \
    "$IMAGE_ID" >/dev/null
}

record() { echo "$(date -Iseconds) $*" >> "$RECORD"; }

forwarder_pid() {
  local pid
  [ -f "$FWD_PID_FILE" ] || return 1
  read -r pid < "$FWD_PID_FILE" || return 1
  case "$pid" in
    ''|*[!0-9]*) return 1 ;;
  esac
  printf '%s\n' "$pid"
}

is_our_forwarder() {
  local pid="$1" command
  [ -r "/proc/$pid/cmdline" ] || return 1
  command="$(tr '\0' ' ' < "/proc/$pid/cmdline")"
  [[ "$command" == *"$ROOT/tools/portfwd.py $PORT "* ]]
}

port_in_use() {
  ss -ltnH "( sport = :$PORT )" 2>/dev/null | grep -q .
}

start_forwarder() {
  local ip pid attempt
  if ! docker ps --format '{{.Names}}' | grep -qx lims-app; then
    echo "无法启动转发：lims-app 未运行" >&2
    return 1
  fi
  ip="$(docker inspect lims-app --format "{{(index .NetworkSettings.Networks \"$NET_ISO\").IPAddress}}")"
  if [ -z "$ip" ]; then
    echo "无法启动转发：lims-app 未接入 $NET_ISO" >&2
    return 1
  fi
  stop_forwarder
  if port_in_use; then
    echo "无法启动转发：127.0.0.1:$PORT 已被其他监听器占用；不会终止未知进程" >&2
    return 1
  fi

  # Detach from the invoking shell so the bridge survives a non-interactive controller exit.
  if command -v setsid >/dev/null 2>&1; then
    nohup setsid python3 "$ROOT/tools/portfwd.py" "$PORT" "$ip" "$PORT" \
      </dev/null >> "$FWD_LOG" 2>&1 &
  else
    nohup python3 "$ROOT/tools/portfwd.py" "$PORT" "$ip" "$PORT" \
      </dev/null >> "$FWD_LOG" 2>&1 &
  fi
  pid=$!
  printf '%s\n' "$pid" > "$FWD_PID_FILE"

  for attempt in $(seq 1 20); do
    if kill -0 "$pid" 2>/dev/null && is_our_forwarder "$pid" && port_in_use; then
      record "转发 127.0.0.1:$PORT -> $ip:$PORT (pid $pid)"
      return 0
    fi
    sleep 0.1
  done

  rm -f "$FWD_PID_FILE"
  echo "转发未能在 127.0.0.1:$PORT 监听；最近日志：" >&2
  tail -20 "$FWD_LOG" >&2 || true
  return 1
}

stop_forwarder() {
  local pid attempt
  if ! pid="$(forwarder_pid)"; then
    rm -f "$FWD_PID_FILE"
    return 0
  fi

  if kill -0 "$pid" 2>/dev/null; then
    if ! is_our_forwarder "$pid"; then
      echo "转发 PID 文件指向未知进程 $pid；不会终止它" >&2
      rm -f "$FWD_PID_FILE"
      return 0
    fi
    kill "$pid"
    for attempt in $(seq 1 20); do
      if ! kill -0 "$pid" 2>/dev/null; then
        rm -f "$FWD_PID_FILE"
        return 0
      fi
      sleep 0.1
    done
    echo "无法停止转发进程 $pid；为避免错误替换，不继续操作" >&2
    return 1
  fi
  rm -f "$FWD_PID_FILE"
}

cmd="${1:-status}"

requested_cmd="$cmd"
restart_requested=false
if [ "$cmd" = restart ]; then
  cmd=up
  restart_requested=true
fi

case "$cmd" in
  up|down|forward) acquire_mutation_lock ;;
esac

case "$cmd" in
  up)
    assert_internal_network
    assert_runtime_inputs
    ensure_attachment_volume
    stop_forwarder
    if docker container inspect lims-app >/dev/null 2>&1; then
      docker rm -f lims-app >/dev/null \
        || { echo "无法移除既有 lims-app；不会启动替代容器" >&2; exit 1; }
    fi
    docker_run_app
    record "up   image=$IMAGE networks=$NET_ISO mac=${MAC_ADDRESS:-auto}（许可证校验照常执行）"
    start_forwarder
    [ "$restart_requested" = true ] && record "restart"
    ;;
  down)
    stop_forwarder
    if docker container inspect lims-app >/dev/null 2>&1; then
      docker stop lims-app >/dev/null \
        || { echo "无法停止 lims-app" >&2; exit 1; }
    fi
    record "down"
    ;;
  forward)
    assert_internal_network
    start_forwarder
    ;;
  status)
    docker ps -a --filter name=lims --format '{{.Names}}\t{{.Status}}'
    if pid="$(forwarder_pid)" \
      && kill -0 "$pid" 2>/dev/null \
      && is_our_forwarder "$pid" \
      && port_in_use; then
      echo "转发 127.0.0.1:$PORT 运行中 (pid $pid)"
    else
      echo "转发 127.0.0.1:$PORT 未运行"
    fi
    ;;
  logs)
    docker logs -f --tail 100 lims-app
    ;;
  *)
    echo "用法: scripts/app-ctl.sh {up|down|restart|forward|status|logs}" >&2
    exit 2
    ;;
esac
