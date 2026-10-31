#!/usr/bin/env bash
# 生产数据库只读探查：报告版本、账号权限、对象构成与登录相关表。
#
# 用法: scripts/db-probe.sh [output-file]
#   连接信息取自 private/application.properties 的 spring.datasource.*（受限材料，不会写入输出之外的地方）。
#   驱动取自应用包内 BOOT-INF/lib/ojdbc8-19.3.0.0.jar（与生产同版本）。
#
# 只读边界: 本脚本运行的 DbProbe 仅执行 SELECT，不修改生产数据或结构。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROPS="$ROOT/private/application.properties"
DRIVER="$ROOT/tools/db-probe/lib/ojdbc8-19.3.0.0.jar"
OUT="${1:-/dev/stdout}"

prop() {
  grep -E "^$1=" "$PROPS" | tail -1 | cut -d= -f2- | tr -d '\r'
}

URL="$(prop spring.datasource.url)"
USER_NAME="$(prop spring.datasource.username)"
PASSWORD="$(prop spring.datasource.password)"

if [ -z "$URL" ] || [ -z "$USER_NAME" ]; then
  echo "无法从 $PROPS 读取 spring.datasource.*" >&2
  exit 1
fi

mkdir -p "$(dirname "$DRIVER")"
if [ ! -f "$DRIVER" ]; then
  echo "[db-probe] 从应用包提取 Oracle 驱动"
  unzip -o -j -q "$ROOT/materials/gikam-third-czjky-14.1.5-run.jar" "BOOT-INF/lib/ojdbc8-19.3.0.0.jar" -d "$(dirname "$DRIVER")"
fi

echo "[db-probe] 目标: ${URL} (user=${USER_NAME})，仅执行 SELECT" >&2
{
  echo "# 生产 Oracle 只读探查 $(date -Iseconds)"
  echo "# 连接: ${URL}  账号: ${USER_NAME}"
  java -cp "$DRIVER" "$ROOT/tools/db-probe/DbProbe.java" "$URL" "$USER_NAME" "$PASSWORD"
} > "$OUT"

echo "[db-probe] 结果写入 $OUT" >&2
