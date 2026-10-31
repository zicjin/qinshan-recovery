#!/usr/bin/env bash
# 切断隔离副本的出网通道：数据库脱离复制网络，仅保留 internal 网络 lims-iso。
# 此后 Oracle/Redis/应用均无出网路由，无法写入生产与外部系统（由 scripts/isolation-check.sh 证明）。
set -euo pipefail

NET_COPY="${NET_COPY:-lims-copy}"
NET_ISO="${NET_ISO:-lims-iso}"

docker network inspect "$NET_ISO" >/dev/null 2>&1 \
  || { echo "[stack-isolate] 缺少隔离网络 $NET_ISO" >&2; exit 1; }
[ "$(docker network inspect "$NET_ISO" --format '{{.Internal}}')" = "true" ] \
  || { echo "[stack-isolate] 隔离网络 $NET_ISO 不是 internal 网络" >&2; exit 1; }

for net in $(docker inspect lims-oracle --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}'); do
  if [ "$net" != "$NET_ISO" ]; then
    echo "[stack-isolate] 断开 lims-oracle <-> $net"
    docker network disconnect "$net" lims-oracle
  fi
done

oracle_networks="$(docker inspect lims-oracle --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}')"
[ "$oracle_networks" = "$NET_ISO " ] || {
  echo "[stack-isolate] lims-oracle 未仅连接 $NET_ISO: ${oracle_networks:-none}" >&2
  exit 1
}
echo "[stack-isolate] lims-oracle 当前网络: $oracle_networks"
echo "[stack-isolate] 完成。下一步: scripts/isolation-check.sh 验证隔离边界。"
