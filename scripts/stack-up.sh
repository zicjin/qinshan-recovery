#!/usr/bin/env bash
# 搭建隔离副本基础设施：Oracle（数据复制阶段临时允许出网）、Redis、应用镜像。
#
# 用法: scripts/stack-up.sh
#
# 网络约定：
#   lims-iso   —— internal 网络，无出网路由；应用、数据库、Redis 的常驻网络。
#   lims-copy  —— 数据复制阶段的临时通道，复制完成后由 scripts/stack-isolate.sh 断开，
#                 之后数据库即无法触达生产或任何外部系统。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NET_ISO="${NET_ISO:-lims-iso}"
NET_COPY="${NET_COPY:-lims-copy}"
COPY_MODE="${COPY_MODE:-offline}"
# 本机隔离实例的 SYS 口令属于受限运行输入，必须由调用者通过环境变量提供；
# 生产凭据只在 private/application.properties 中，且该文件不进入 Git。
: "${ORACLE_SYS_PASSWORD:?请通过受限环境变量设置 ORACLE_SYS_PASSWORD}"

if [ "$COPY_MODE" = "production" ]; then
  [ "${ALLOW_PRODUCTION_COPY:-}" = "1" ] \
    || { echo "生产复制模式必须显式设置 ALLOW_PRODUCTION_COPY=1；不会连接 $NET_COPY" >&2; exit 1; }
else
  echo "stack-up.sh 默认只允许离线模式；它不会创建或连接复制网络。生产复制请使用 COPY_MODE=production ALLOW_PRODUCTION_COPY=1。" >&2
  exit 1
fi
# Redis 口令与生产一致，从受限材料读取，不写入脚本
REDIS_PW="$(grep -E '^spring\.redis\.url=redis://:[^@[:space:]]+@' "$ROOT/private/application.properties" | tail -1 | sed -E 's#^spring\.redis\.url=redis://:([^@[:space:]]+)@.*#\1#' | tr -d '\r')"
[ -n "$REDIS_PW" ] || { echo "受限配置中的 Redis URL 格式非法" >&2; exit 1; }

docker network inspect "$NET_ISO"  >/dev/null 2>&1 || docker network create --internal "$NET_ISO"
docker network inspect "$NET_COPY" >/dev/null 2>&1 || docker network create "$NET_COPY"

if [ "$(docker network inspect "$NET_ISO" --format '{{.Internal}}')" != "true" ]; then
  echo "隔离网络 $NET_ISO 不是 internal 网络；拒绝连接容器" >&2
  exit 1
fi
if [ "$(docker network inspect "$NET_COPY" --format '{{.Internal}}')" = "true" ]; then
  echo "复制网络 $NET_COPY 是 internal 网络；无法作为生产只读复制通道" >&2
  exit 1
fi

docker image inspect gvenzl/oracle-xe:21-slim >/dev/null 2>&1 \
  || { echo "本地缺少 Oracle 镜像；不会在复制阶段拉取镜像" >&2; exit 1; }
docker image inspect redis:7.4-alpine >/dev/null 2>&1 \
  || { echo "本地缺少 Redis 镜像；不会在复制阶段拉取镜像" >&2; exit 1; }
ORACLE_IMAGE_ID="$(docker image inspect gvenzl/oracle-xe:21-slim --format '{{.Id}}')"
REDIS_IMAGE_ID="$(docker image inspect redis:7.4-alpine --format '{{.Id}}')"

if ! docker ps -a --format '{{.Names}}' | grep -qx lims-oracle; then
  echo "[stack-up] 启动 Oracle XE 21c（数据目录: 卷 lims-oracle-data）"
  docker volume create lims-oracle-data >/dev/null
  docker run -d --name lims-oracle \
    --pull=never \
    --network "$NET_ISO" --network-alias oracle \
    -e ORACLE_PASSWORD="$ORACLE_SYS_PASSWORD" \
    -e ORACLE_MEMORY=2048 \
    -v lims-oracle-data:/opt/oracle/oradata \
    "$ORACLE_IMAGE_ID"
fi
oracle_networks="$(docker inspect lims-oracle --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}')"
case " $oracle_networks" in *" $NET_ISO "*) ;; *) echo "lims-oracle 未连接 $NET_ISO；拒绝继续复制阶段" >&2; exit 1 ;; esac
for existing_network in $oracle_networks; do
  [ "$existing_network" = "$NET_ISO" ] || [ "$existing_network" = "$NET_COPY" ] \
    || { echo "lims-oracle 已连接未允许网络 $existing_network；拒绝继续复制阶段" >&2; exit 1; }
done

copy_connected_by_script=0
rollback_copy_network() {
  local status="$?"
  trap - EXIT
  if [ "$copy_connected_by_script" = 1 ]; then
    if docker network disconnect "$NET_COPY" lims-oracle >/dev/null 2>&1; then
      echo "[stack-up] 失败回滚：已断开 lims-oracle <-> $NET_COPY" >&2
    else
      echo "[stack-up] 失败回滚失败：请立即检查并断开 lims-oracle <-> $NET_COPY" >&2
      status=1
    fi
  fi
  exit "$status"
}
trap rollback_copy_network EXIT
case " $oracle_networks" in
  *" $NET_COPY "*) ;;
  *) docker network connect "$NET_COPY" lims-oracle; copy_connected_by_script=1 ;;
esac

if ! oracle_networks="$(docker inspect lims-oracle --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}')"; then
  echo "无法确认 lims-oracle 的复制网络连接" >&2
  exit 1
fi
case " $oracle_networks" in *" $NET_ISO "*) ;; *)
  echo "lims-oracle 未连接 $NET_ISO；拒绝继续复制阶段" >&2
  exit 1
  ;;
esac
case " $oracle_networks" in *" $NET_COPY "*) ;; *)
  echo "lims-oracle 未连接 $NET_COPY；拒绝继续复制阶段" >&2
  exit 1
  ;;
esac

if ! docker ps -a --format '{{.Names}}' | grep -qx lims-redis; then
  echo "[stack-up] 启动 Redis（隔离网内，口令与生产一致，取自受限材料）"
  docker run -d --name lims-redis \
    --pull=never \
    --network "$NET_ISO" --network-alias redis \
    "$REDIS_IMAGE_ID" redis-server --requirepass "$REDIS_PW" --save ''
else
  redis_networks="$(docker inspect lims-redis --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}')"
  [ "$redis_networks" = "$NET_ISO " ] || {
    echo "lims-redis 未仅连接 $NET_ISO: ${redis_networks:-none}" >&2
    exit 1
  }
fi

echo "[stack-up] 等待 Oracle 就绪（首次初始化约 2-5 分钟）"
for i in $(seq 1 120); do
  if docker exec lims-oracle healthcheck.sh >/dev/null 2>&1; then
    echo "[stack-up] Oracle 已就绪"
    break
  fi
  [ "$i" = 120 ] && { echo "Oracle 初始化超时" >&2; docker logs --tail 50 lims-oracle >&2; exit 1; }
  sleep 5
done

copy_connected_by_script=0
echo "[stack-up] 基础服务已准备。下一步: scripts/db-copy.sh 复制数据；隔离后按交接手册构建 lims-app:14.1.5-built。"
