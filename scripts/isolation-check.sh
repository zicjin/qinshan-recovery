#!/usr/bin/env bash
# 隔离边界检查：证明隔离副本不能写入生产与外部系统。
#
# 用法: scripts/isolation-check.sh [报告文件]
#
# 三类检查：
#   1) 静态配置：隔离配置中不含任何生产或外部地址；
#   2) 运行时出网：从应用容器和数据库容器发起对生产与外部端点的 TCP 连接必须全部失败；
#   3) 正向对照：同一容器到隔离副本内 Oracle/Redis 的连接必须成功（证明失败不是网络整体故障）。
# 任一项失败即退出码 1。定时任务、业务同步、邮件与文件传输的停用见 scripts/make-isolated-config.sh。
set -euo pipefail
umask 077

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CFG="${CFG:-$ROOT/private/application-isolated.properties}"
REPORT="${1:-}"
NET_ISO="${NET_ISO:-lims-iso}"
APP_CONTAINER="${APP_CONTAINER:-lims-app}"
ORACLE_CONTAINER="${ORACLE_CONTAINER:-lims-oracle}"
REDIS_CONTAINER="${REDIS_CONTAINER:-lims-redis}"

fail=0
REPORT_TMP="${REPORT}.tmp.$$"
out() { printf '%s\n' "$*" | tee -a "$REPORT_TMP"; }

assert_restricted_file() {
  local path="$1" label="$2" mode owner
  [ -f "$path" ] && [ ! -L "$path" ] || {
    out "FAIL 缺少、非普通文件或符号链接化的$label: $path"
    exit 1
  }
  mode="$(stat -c '%a' "$path")"
  owner="$(stat -c '%u' "$path")"
  if (( (8#$mode & 0077) != 0 )) || [ "$owner" != "$(id -u)" ]; then
    out "FAIL $label 权限或所有者不安全: $path"
    exit 1
  fi
}

[ -n "$REPORT" ] && : > "$REPORT_TMP"
out "=== 隔离边界检查 $(date -Iseconds) ==="
assert_restricted_file "$CFG" "隔离配置"

out ""
out "--- 1) 静态配置扫描: $CFG"
# spring.mail.username / password 是账号凭据而非可路由端点，邮件主机已指向容器内回环，故不纳入端点扫描
if grep -vE '^[[:space:]]*#' "$CFG" | grep -vE '^spring\.mail\.(username|password)=' \
   | grep -nE '10\.100\.|218\.93\.|192\.168\.|58\.56\.|czjky\.com|exmail\.qq\.com|lims\.sunwayworld\.com'; then
  out "FAIL 隔离配置仍指向生产或外部地址"
  fail=1
else
  out "PASS 隔离配置不含生产与外部地址"
fi
if grep -vE '^[[:space:]]*#' "$CFG" | grep -qE '^spring\.profiles\.active=.*(schedule|equipt-acq-file|ngtodo)'; then
  out "FAIL 定时任务/文件采集 profile 未停用"
  fail=1
else
  out "PASS 定时任务、业务同步、文件采集相关 profile 已停用"
fi

# 生产与外部端点清单（含私有 IP，属受限材料，保存在 private/isolation-endpoints.txt）
ENDPOINT_FILE="${ENDPOINT_FILE:-$ROOT/private/isolation-endpoints.txt}"
assert_restricted_file "$ENDPOINT_FILE" "端点清单"
endpoint_count="$(awk '
  /^[[:space:]]*#/ || /^[[:space:]]*$/ { next }
  {
    if (NF < 3 || $2 !~ /^[0-9]+$/ || $2 < 1 || $2 > 65535) bad = 1
    else count++
  }
  END { if (bad || count < 1) exit 1; print count }
' "$ENDPOINT_FILE")" || {
  out "FAIL 端点清单为空或格式非法（每行必须为 host port name）"
  exit 1
}
out "PASS 端点清单格式有效（$endpoint_count 个负向检查端点）"
POSITIVE=(
  "oracle 1521 隔离Oracle"
  "redis 6379 隔离Redis"
)
REQUIRED_CONTAINERS=("$APP_CONTAINER" "$ORACLE_CONTAINER" "$REDIS_CONTAINER")

probe() { # 容器 host port -> 0=连通 1=不通
  local c="$1" h="$2" p="$3"
  docker exec "$c" bash -c "timeout 3 bash -c 'cat < /dev/null > /dev/tcp/$h/$p' 2>/dev/null"
}

out ""
out "--- 0) 运行时前置条件"
if ! docker network inspect "$NET_ISO" >/dev/null 2>&1; then
  out "FAIL 缺少隔离网络 $NET_ISO"
  fail=1
elif [ "$(docker network inspect "$NET_ISO" --format '{{.Internal}}')" != "true" ]; then
  out "FAIL 隔离网络 $NET_ISO 不是 internal 网络"
  fail=1
else
  out "PASS 隔离网络 $NET_ISO 为 internal 网络"
fi

for c in "${REQUIRED_CONTAINERS[@]}"; do
  if ! docker ps --format '{{.Names}}' | grep -qx "$c"; then
    out "FAIL $c 容器未运行"
    fail=1
    continue
  fi
  nets="$(docker inspect "$c" --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}')"
  if [ "$nets" = "$NET_ISO " ] || [ "$nets" = "$NET_ISO" ]; then
    out "PASS $c 仅接入 $NET_ISO 网络（$nets）"
  else
    out "FAIL $c 接入了隔离网之外的网络: $nets"
    fail=1
  fi
done

for c in "$APP_CONTAINER" "$ORACLE_CONTAINER"; do
  if ! docker ps --format '{{.Names}}' | grep -qx "$c"; then
    out "FAIL $c 容器未运行，无法执行运行时出网检查"
    fail=1
    continue
  fi
  out ""
  out "--- 2) 运行时出网检查（容器 $c）"
  while read -r h p name extra; do
    [ -z "${h:-}" ] && continue
    case "$h" in \#*) continue ;; esac
    if probe "$c" "$h" "$p"; then
      out "FAIL $c 可连通 $name"
      fail=1
    else
      out "PASS $c 无法连通 $name"
    fi
  done < "$ENDPOINT_FILE"
  out ""
  out "--- 3) 正向对照（容器 $c 到隔离副本内服务）"
  for e in "${POSITIVE[@]}"; do
    read -r h p name <<<"$e"
    if probe "$c" "$h" "$p"; then
      out "PASS $c 可连通 $name"
    else
      out "FAIL $c 无法连通 $name（隔离网内服务应可达）"
      fail=1
    fi
  done
  out ""
  out "--- 网络路由（容器 $c）"
  out "$(docker exec "$c" bash -c 'ip route 2>/dev/null | sed "s/^/    /"')"
done

out ""
if [ "$fail" -ne 0 ]; then
  out "结果: FAIL —— 隔离运行前置条件不满足或存在可触达生产/外部系统的通道"
else
  out "结果: PASS —— 应用与数据库均无法写入生产或外部系统"
fi

if [ -n "$REPORT" ]; then
  mv "$REPORT_TMP" "$REPORT"
  echo "报告写入 $REPORT"
else
  rm -f "$REPORT_TMP"
fi
exit "$fail"
