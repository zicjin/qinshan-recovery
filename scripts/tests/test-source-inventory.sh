#!/usr/bin/env bash
# tools/source_inventory.py 的自检：
#   1) 覆盖核对桶：both / prod-only / src-only；内部类映射外部类源文件；多顶层类型映射同一源文件
#   2) 资源内容比对：identical / changed
#   3) class 比对（提供重编译目录时）：identical / same-code / changed；缺失为 not-recompiled
#   4) 未提供重编译目录时 class 内容状态为 pending
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOL="$SCRIPT_DIR/../../tools/source_inventory.py"

command -v javac >/dev/null || { echo "自检需要 javac"; exit 1; }

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

mkdir -p "$tmp/src/main/java/com/a/mapper/conf" \
         "$tmp/src/main/resources/static/emc" "$tmp/src/main/resources/templates/emc"

# --- 源码侧 fixture ---
# Bar.java 内含匿名内部类（生成 Bar$1.class）与第二个顶层类型 Helper
cat > "$tmp/src/main/java/com/a/Foo.java" <<'EOF'
package com.a;
public class Foo {
    public String hello() { return "hello"; }
}
EOF
cat > "$tmp/src/main/java/com/a/Bar.java" <<'EOF'
package com.a;
public class Bar {
    public Runnable r() {
        return new Runnable() {
            public void run() { }
        };
    }
}
class Helper {
    public int m() { return 2; }
}
EOF
# Hoo.java 只在源码侧存在
cat > "$tmp/src/main/java/com/a/Hoo.java" <<'EOF'
package com.a;
public class Hoo { }
EOF
echo 'same mapper'  > "$tmp/src/main/java/com/a/mapper/conf/m.xml"
echo 'source js'    > "$tmp/src/main/resources/static/emc/x.js"
echo 'only source'  > "$tmp/src/main/resources/static/emc/only-src.js"
printf 'eol js\n'   > "$tmp/src/main/resources/static/emc/z.js"

# --- 重编译侧 fixture（等价于构建产物 target/classes）---
mkdir -p "$tmp/classes-same" "$tmp/classes-debug" "$tmp/classes-mod" \
         "$tmp/prod2/classes" "$tmp/classes-annot"
# classes-same：与“生产字节码”逐字节一致（javac 默认输出）
javac -d "$tmp/classes-same" -encoding UTF-8 \
  "$tmp/src/main/java/com/a/Foo.java" "$tmp/src/main/java/com/a/Bar.java" 2>/dev/null
# classes-debug：仅调试行号信息不同 -> same-code
javac -g:none -d "$tmp/classes-debug" -encoding UTF-8 \
  "$tmp/src/main/java/com/a/Foo.java" "$tmp/src/main/java/com/a/Bar.java" 2>/dev/null
# classes-mod：Foo 字符串常量与指令变化 -> changed；Bar 系列未重编译 -> not-recompiled
mkdir -p "$tmp/alt/com/a"
cat > "$tmp/alt/com/a/Foo.java" <<'EOF'
package com.a;
public class Foo {
    public String hello() { return "changed"; }
}
class Helper {
    public int m() { return 2; }
}
EOF
javac -g:none -d "$tmp/classes-mod" -encoding UTF-8 "$tmp/alt/com/a/Foo.java" 2>/dev/null
# classes-annot：仅注解与泛型 Signature 属性变化（擦除后描述符、指令、字面量均不变）也必须判为 changed
# 生产侧与源码侧的 hello() 擦除后均为 ()Ljava/util/List; 且方法体一致
mkdir -p "$tmp/src2/src/main/java/com/a" "$tmp/prod2/com/a"
cat > "$tmp/src2/src/main/java/com/a/Foo.java" <<'EOF'
package com.a;
import java.util.List;
public class Foo {
    public List<String> hello() { return null; }
}
EOF
cat > "$tmp/prod2/com/a/Foo.java" <<'EOF'
package com.a;
import java.util.List;
public class Foo {
    @Deprecated
    public List<Integer> hello() { return null; }
}
EOF
javac -g:none -d "$tmp/prod2/classes" -encoding UTF-8 "$tmp/prod2/com/a/Foo.java" 2>/dev/null
javac -g:none -d "$tmp/classes-annot" -encoding UTF-8 "$tmp/src2/src/main/java/com/a/Foo.java" 2>/dev/null
python3 - "$tmp" <<'EOF'
import sys, zipfile, pathlib
tmp = pathlib.Path(sys.argv[1])
with zipfile.ZipFile(tmp / "prod2.jar", "w") as z:
    z.writestr("BOOT-INF/classes/com/a/Foo.class", (tmp / "prod2/classes/com/a/Foo.class").read_bytes())
EOF

# --- 生产侧 fixture：打包成 run.jar 风格 ZIP（仅 BOOT-INF/classes 参与核对）---
python3 - "$tmp" <<'EOF'
import sys, zipfile, pathlib
tmp = pathlib.Path(sys.argv[1])
same = tmp / "classes-same/com/a"
prod = {
    "com/a/Foo.class":      (same / "Foo.class").read_bytes(),
    "com/a/Bar.class":      (same / "Bar.class").read_bytes(),
    "com/a/Bar$1.class":    (same / "Bar$1.class").read_bytes(),
    "com/a/Helper.class":   (same / "Helper.class").read_bytes(),
    # 仅生产侧存在（内容随便复用一个 class，覆盖核对只看映射）
    "com/a/Goo.class":      (same / "Foo.class").read_bytes(),
    "com/a/mapper/conf/m.xml": b"same mapper\n",
    "static/emc/x.js":      b"prod js\n",
    "static/emc/z.js":      b"eol js\r\n",
    "templates/emc/y.html": b"prod only\n",
}
with zipfile.ZipFile(tmp / "prod.jar", "w") as z:
    z.writestr("BOOT-INF/lib/dummy.jar", b"ignored")
    for p, data in prod.items():
        z.writestr("BOOT-INF/classes/" + p, data)
EOF

assert_row() {
  local name="$1" classes_dir="$2" expected="$3" jar="${4:-$tmp/prod.jar}" srcroot="${5:-$tmp}"
  local got
  got="$(python3 "$TOOL" --jar "$jar" --src "$srcroot" ${classes_dir:+--classes "$classes_dir"} 2>&1)" || {
    echo "自检失败 [$name]: 工具退出非 0"; echo "$got"; exit 1; }
  if ! grep -qF "$expected" <<<"$got"; then
    echo "自检失败 [$name]: 输出未包含「$expected」"
    echo "$got"
    exit 1
  fi
  echo "自检通过 [$name]"
}

# 覆盖核对桶
assert_row "both(class)"        "$tmp/classes-same" "$(printf 'com/a/Foo.class\tclass\tboth')"
assert_row "内部类映射"          "$tmp/classes-same" "$(printf 'com/a/Bar$1.class\tclass\tboth')"
assert_row "多顶层类型映射"      "$tmp/classes-same" "$(printf 'com/a/Helper.class\tclass\tboth')"
assert_row "prod-only"          "$tmp/classes-same" "$(printf 'com/a/Goo.class\tclass\tprod-only')"
assert_row "src-only(java)"     "$tmp/classes-same" "$(printf 'com/a/Hoo.java\tclass\tsrc-only')"
assert_row "src-only(资源)"      "$tmp/classes-same" "$(printf 'static/emc/only-src.js\tresource\tsrc-only')"
assert_row "prod-only(资源)"     "$tmp/classes-same" "$(printf 'templates/emc/y.html\tresource\tprod-only')"

# 内容比对
assert_row "资源 identical"      "$tmp/classes-same" "$(printf 'com/a/mapper/conf/m.xml\tresource\tboth\tidentical')"
assert_row "资源 changed"        "$tmp/classes-same" "$(printf 'static/emc/x.js\tresource\tboth\tchanged')"
assert_row "资源 eol-only"       "$tmp/classes-same" "$(printf 'static/emc/z.js\tresource\tboth\teol-only')"
assert_row "class identical"    "$tmp/classes-same" "$(printf 'com/a/Foo.class\tclass\tboth\tidentical')"
assert_row "class same-code"    "$tmp/classes-debug" "$(printf 'com/a/Foo.class\tclass\tboth\tsame-code')"
assert_row "class changed"      "$tmp/classes-mod"  "$(printf 'com/a/Foo.class\tclass\tboth\tchanged')"
assert_row "class not-recompiled" "$tmp/classes-mod" "$(printf 'com/a/Bar.class\tclass\tboth\tnot-recompiled')"
assert_row "class pending"      ""                  "$(printf 'com/a/Foo.class\tclass\tboth\tpending')"
assert_row "注解/泛型属性变化判 changed" "$tmp/classes-annot" "$(printf 'com/a/Foo.class\tclass\tboth\tchanged')" "$tmp/prod2.jar" "$tmp/src2"

# 汇总计数
assert_row "汇总 both=7"         "$tmp/classes-same" "both=7"
assert_row "汇总 prod-only=2"    "$tmp/classes-same" "prod-only=2"
assert_row "汇总 src-only=2"     "$tmp/classes-same" "src-only=2"

# 审计过的反编译类只允许精确的生产/重编译字节对触发例外；错误的类路径
# 或任一侧字节变化必须回到普通差异判定。
python3 - "$TOOL" <<'EOF'
import hashlib
import importlib.util
import sys

spec = importlib.util.spec_from_file_location("source_inventory", sys.argv[1])
mod = importlib.util.module_from_spec(spec)
spec.loader.exec_module(mod)
rel = "test/Audited.class"
prod = b"production"
rebuilt = b"rebuilt"
mod._AUDITED_CLASS_EQUIVALENCE[rel] = {
    "production": hashlib.sha256(prod).hexdigest(),
    "rebuilt": {hashlib.sha256(rebuilt).hexdigest()},
}
assert mod.audited_class_equivalence(rel, prod, rebuilt)
assert not mod.audited_class_equivalence(rel, prod + b"x", rebuilt)
assert not mod.audited_class_equivalence("test/Other.class", prod, rebuilt)

# Spill normalization must not erase a receiver/value movement for an
# unrelated class.  Keep the bytecode shape visible unless an exact reviewed
# class pair is selected above.
spill = "\\n".join([
    "astore LOCAL",
    "new // class java/lang/StringBuilder",
    "dup",
    'invokespecial // Method java/lang/StringBuilder."<init>":()V',
    "aload LOCAL",
    "invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;",
])
assert "astore LOCAL" in mod._normalize_javap(spill)
assert "aload LOCAL" in mod._normalize_javap(spill)
scheme_spill = "\\n".join([
    "checkcast // class com/sunwayworld/emc/module/bp/project/projscheme/bean/EmcProjSchemeBean",
    "astore LOCAL",
    "new // class java/lang/StringBuilder",
])
assert "astore LOCAL" in mod._normalize_javap(scheme_spill)
setter_spill = "\\n".join([
    'invokevirtual // Method java/lang/StringBuilder.toString:()Ljava/lang/String;',
    "aload LOCAL",
    "invokevirtual // Method com/sunwayworld/emc/module/bp/project/projscheme/bean/EmcProjSchemeBean.setFolderNo:(Ljava/lang/String;)V",
])
assert "aload LOCAL" in mod._normalize_javap(setter_spill)
print("自检通过 [审计字节对例外严格绑定]")
EOF

echo "source_inventory.py 自检全部通过"
