#!/usr/bin/env bash
# tools/class_diff.py 的自检：
#   1) 语义一致 -> 输出 SEMANTIC-MATCH 且退出码 0
#   2) 语义不一致 -> 打印归一化 javap 差异且退出码 1
#   3) 重编译缺类（如生产匿名内部类）-> 标记 MISSING 且退出码 1
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOL="$SCRIPT_DIR/../../tools/class_diff.py"

command -v javac >/dev/null || { echo "自检需要 javac"; exit 1; }

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

mkdir -p "$tmp/src/main/java/com/a" "$tmp/lib"
cat > "$tmp/src/main/java/com/a/Foo.java" <<'EOF'
package com.a;
public class Foo {
    public String hello() { return "hello"; }
}
EOF
# 生产侧：语义一致（含匿名内部类场景之外的简单类）
mkdir -p "$tmp/prodsrc/com/a" "$tmp/prodclasses"
cp "$tmp/src/main/java/com/a/Foo.java" "$tmp/prodsrc/com/a/Foo.java"
cat > "$tmp/prodsrc/com/a/Bar.java" <<'EOF'
package com.a;
public class Bar {
    public Runnable r() { return new Runnable() { public void run() { } }; }
}
EOF
javac -d "$tmp/prodclasses" -encoding UTF-8 "$tmp/prodsrc/com/a/Foo.java" "$tmp/prodsrc/com/a/Bar.java" 2>/dev/null
python3 - "$tmp" <<'EOF'
import sys, zipfile, pathlib, os
tmp = pathlib.Path(sys.argv[1])
with zipfile.ZipFile(tmp / "prod.jar", "w") as z:
    base = tmp / "prodclasses/com/a"
    for p in sorted(base.glob("*.class")):
        z.writestr("BOOT-INF/classes/com/a/" + p.name, p.read_bytes())
EOF

run_diff() {
  local name="$1" want_code="$2" want_out="$3"; shift 3
  local got_code=0 got_out
  got_out="$(python3 "$TOOL" --jar "$tmp/prod.jar" --src "$tmp" "$@" 2>&1)" || got_code=$?
  [ "$got_code" = "$want_code" ] || { echo "自检失败 [$name]: 期望退出码 $want_code，实际 $got_code"; echo "$got_out"; exit 1; }
  if [ -n "$want_out" ] && ! grep -qF "$want_out" <<<"$got_out"; then
    echo "自检失败 [$name]: 输出不含「$want_out」"; echo "$got_out"; exit 1
  fi
  echo "自检通过 [$name]"
}

run_diff "语义一致" 0 "SEMANTIC-MATCH" --src-path com/a/Foo.java

# 改源码 -> 语义不一致
cat > "$tmp/src/main/java/com/a/Foo.java" <<'EOF'
package com.a;
public class Foo {
    public String hello() { return "changed"; }
}
EOF
run_diff "语义不一致" 1 "String changed" --src-path com/a/Foo.java

# 生产含匿名内部类、源码缺失 -> MISSING
cat > "$tmp/src/main/java/com/a/Bar.java" <<'EOF'
package com.a;
public class Bar {
    public Runnable r() { return null; }
}
EOF
run_diff "缺内部类" 1 "MISSING" --src-path com/a/Bar.java

echo "class_diff.py 自检全部通过"
