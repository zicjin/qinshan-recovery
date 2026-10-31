#!/usr/bin/env bash
# tools/recover_resources.py 的自检：
#   1) changed 资源以生产内容为准更新（文本换行归一入库）
#   2) prod-only 资源补入源码树（com/ 进 src/main/java，其余进 src/main/resources）
#   3) 二进制按字节；eol-only 与 identical 不动
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOL="$SCRIPT_DIR/../../tools/recover_resources.py"
INV="$SCRIPT_DIR/../../tools/source_inventory.py"

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

mkdir -p "$tmp/src/main/java/com/a/mapper/conf" "$tmp/src/main/resources/static/emc"
echo 'old js'        > "$tmp/src/main/resources/static/emc/x.js"
echo 'same mapper'   > "$tmp/src/main/java/com/a/mapper/conf/m.xml"
printf 'eol js\n'    > "$tmp/src/main/resources/static/emc/z.js"

python3 - "$tmp" <<'EOF'
import sys, zipfile, pathlib
tmp = pathlib.Path(sys.argv[1])
with zipfile.ZipFile(tmp / "prod.jar", "w") as z:
    z.writestr("BOOT-INF/classes/com/a/mapper/conf/m.xml", b"same mapper\n")
    z.writestr("BOOT-INF/classes/static/emc/x.js", b"prod js\r\n")         # changed -> 更新为 LF
    z.writestr("BOOT-INF/classes/static/emc/z.js", b"eol js\r\n")          # eol-only -> 不动
    z.writestr("BOOT-INF/classes/com/a/new.xml", b"prod only\r\n")         # prod-only -> 补入 java 树
    z.writestr("BOOT-INF/classes/static/emc/logo.png", b"\x89PNG\r\nfake") # 二进制 -> 原样
EOF

python3 "$INV" --jar "$tmp/prod.jar" --src "$tmp" --out "$tmp/inv.tsv" >/dev/null

out="$(python3 "$TOOL" --jar "$tmp/prod.jar" --src "$tmp" --inventory "$tmp/inv.tsv" --apply 2>&1)" || {
  echo "自检失败: 工具退出非 0"; echo "$out"; exit 1; }

assert_text() {
  local name="$1" path="$2" expected="$3"
  [ -f "$path" ] || { echo "自检失败 [$name]: 缺少 $path"; exit 1; }
  grep -qF "$expected" "$path" || { echo "自检失败 [$name]: $path 不含「$expected」"; od -c "$path"; exit 1; }
  echo "自检通过 [$name]"
}

assert_text "changed 更新为 LF"  "$tmp/src/main/resources/static/emc/x.js" "prod js"
grep -q $'\r' "$tmp/src/main/resources/static/emc/x.js" && { echo "自检失败 [文本无 CR]: 仍有 CR"; exit 1; }
echo "自检通过 [文本无 CR]"
assert_text "prod-only 补入 java 树" "$tmp/src/main/java/com/a/new.xml" "prod only"

printf '\x89PNG\r\nfake' | cmp -s - "$tmp/src/main/resources/static/emc/logo.png" || {
  echo "自检失败 [二进制原样补入]"; exit 1; }
echo "自检通过 [二进制原样补入]"

cmp -s "$tmp/src/main/resources/static/emc/z.js" <(printf 'eol js\n') || {
  echo "自检失败 [eol-only 不动]: z.js 被改动"; exit 1; }
echo "自检通过 [eol-only 不动]"

echo "recover_resources.py 自检全部通过"
