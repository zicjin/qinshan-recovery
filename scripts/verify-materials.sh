#!/usr/bin/env bash
# 校验本机材料库完整性：逐项比对 build-support/t01-manifest.sha256 中记录的 sha256。
#
# 用法: scripts/verify-materials.sh [manifest]
#   manifest 默认为 build-support/t01-manifest.sha256；可通过参数或 MANIFEST 环境变量指向其他清单（供测试）。
#
# 退出码: 0 = 全部一致；1 = 存在缺失或校验不符（逐项打印 FAIL）。
set -euo pipefail

ROOT="${ROOT:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}"
MANIFEST="${1:-${MANIFEST:-$ROOT/build-support/t01-manifest.sha256}}"

if [ ! -f "$MANIFEST" ]; then
  echo "FAIL 清单不存在: $MANIFEST" >&2
  exit 1
fi

fail=0
checked=0
while read -r want path; do
  [ -z "${want:-}" ] && continue
  case "$want" in \#*) continue ;; esac
  file="$ROOT/$path"
  checked=$((checked + 1))
  if [ ! -f "$file" ]; then
    echo "FAIL 缺失   $path"
    fail=1
    continue
  fi
  got="$(sha256sum "$file" | cut -d' ' -f1)"
  if [ "$got" = "$want" ]; then
    echo "OK   $path"
  else
    echo "FAIL 校验不符 $path"
    echo "     期望 $want"
    echo "     实际 $got"
    fail=1
  fi
done < "$MANIFEST"

if [ "$checked" -eq 0 ]; then
  echo "FAIL 清单为空: $MANIFEST" >&2
  exit 1
fi

if [ "$fail" -ne 0 ]; then
  echo "结果: 存在缺失或损坏的材料（共检查 $checked 项）" >&2
  exit 1
fi
echo "结果: 全部 $checked 项材料校验通过"
