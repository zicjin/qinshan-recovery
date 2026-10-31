#!/usr/bin/env bash
# scripts/verify-materials.sh 的自检：
#   1) 全部一致 -> 退出码 0
#   2) 任一文件内容损坏 -> 退出码 1 且打印 FAIL
#   3) 任一文件缺失 -> 退出码 1 且打印 FAIL
#   4) 清单缺失 -> 退出码 1
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERIFY="$SCRIPT_DIR/../verify-materials.sh"

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

mkdir -p "$tmp/materials" "$tmp/private"
echo alpha > "$tmp/materials/alpha.jar"
echo beta  > "$tmp/private/beta.lic"

cat > "$tmp/manifest.sha256" <<EOF
$(sha256sum "$tmp/materials/alpha.jar" | cut -d' ' -f1)  materials/alpha.jar
$(sha256sum "$tmp/private/beta.lic"  | cut -d' ' -f1)  private/beta.lic
EOF

assert() {
  local name="$1" want_code="$2" want_out="$3" got_code=0 got_out=""
  got_out="$(ROOT="$tmp" MANIFEST="$tmp/manifest.sha256" bash "$VERIFY" 2>&1)" || got_code=$?
  if [ "$got_code" != "$want_code" ]; then
    echo "自检失败 [$name]: 期望退出码 $want_code，实际 $got_code"
    echo "$got_out"
    exit 1
  fi
  if [ -n "$want_out" ] && ! grep -q "$want_out" <<<"$got_out"; then
    echo "自检失败 [$name]: 输出未包含「$want_out」"
    echo "$got_out"
    exit 1
  fi
  echo "自检通过 [$name]"
}

assert "全部一致" 0 "全部 2 项材料校验通过"

echo corrupt > "$tmp/private/beta.lic"
assert "内容损坏" 1 "FAIL 校验不符"
echo beta > "$tmp/private/beta.lic"

rm "$tmp/materials/alpha.jar"
assert "文件缺失" 1 "FAIL 缺失"

got_code=0
got_out="$(ROOT="$tmp" MANIFEST="$tmp/missing.sha256" bash "$VERIFY" 2>&1)" || got_code=$?
[ "$got_code" = "1" ] || { echo "自检失败 [清单缺失]: 期望退出码 1，实际 $got_code"; exit 1; }
echo "自检通过 [清单缺失]"

echo "verify-materials.sh 自检全部通过"
