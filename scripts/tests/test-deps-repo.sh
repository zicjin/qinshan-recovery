#!/usr/bin/env bash
# tools/deps_repo.py 的自检：
#   1) 从 run.jar 的 BOOT-INF/lib 提取依赖 jar 到本地 Maven 仓库布局
#   2) GAV 判定：META-INF/maven 元数据优先，文件名回退（gikam-* 归 com.sunwayworld.cloud）
#   3) 生成重建的 gikam-core-parent（含全部依赖、编译参数与资源打包规则）
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOL="$SCRIPT_DIR/../../tools/deps_repo.py"

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

# --- fixture run.jar：3 个依赖 jar（2 个带 maven 元数据，1 个仅文件名可判定）---
python3 - "$tmp" <<'EOF'
import io, sys, zipfile, pathlib
tmp = pathlib.Path(sys.argv[1])

def make_jar(entries):
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w") as z:
        for name, data in entries.items():
            z.writestr(name, data)
    return buf.getvalue()

alpha = make_jar({
    "META-INF/maven/org.example/alpha/pom.properties":
        b"groupId=org.example\nartifactId=alpha\nversion=1.2.3\n",
    "org/example/Alpha.class": b"\xca\xfe\xba\xbe alpha",
})
alpha2 = make_jar({
    "META-INF/maven/org.example/alpha/pom.properties":
        b"groupId=org.example\nartifactId=alpha\nversion=1.2.3\n",
    "org/example/Alpha2.class": b"\xca\xfe\xba\xbe alpha2",
})
privsdk = make_jar({
    "META-INF/maven/net.qiyuesuo.sdk/private-sdk/pom.properties":
        b"groupId=net.qiyuesuo.sdk\nartifactId=private-sdk\nversion=RELEASE\n",
    "sdk/Sdk.class": b"\xca\xfe\xba\xbe sdk",
})
gikam = make_jar({"com/sunwayworld/web/Web.class": b"\xca\xfe\xba\xbe web"})

with zipfile.ZipFile(tmp / "run.jar", "w") as z:
    z.writestr("BOOT-INF/classes/com/a/Foo.class", b"ignored")
    z.writestr("BOOT-INF/lib/alpha-9.9.9.jar", alpha)          # 内嵌元数据版本 1.2.3 不可信 -> 以文件名为准
    z.writestr("BOOT-INF/lib/alpha-extra-7.7.7.jar", alpha2)   # 同元数据不同文件 -> 各自保留文件名身份
    z.writestr("BOOT-INF/lib/private-sdk-RELEASE.jar", privsdk)
    z.writestr("BOOT-INF/lib/gikam-core-web-14.1.5.jar", gikam)
    z.writestr("BOOT-INF/lib/nomaven-2.0.1.jar", make_jar({"x/X.class": b"\xca\xfe"}))
    z.writestr("BOOT-INF/lib/bad+name-1.0.jar", make_jar({"y/Y.class": b"\xca\xfe"}))
    z.writestr("BOOT-INF/lib/jna-4.5.1.jar", make_jar({"j/J.class": b"\xca\xfe"}))
    z.writestr("BOOT-INF/lib/jna-5.8.0.jar", make_jar({"j/J2.class": b"\xca\xfe"}))
EOF

python3 "$TOOL" --jar "$tmp/run.jar" --repo "$tmp/repo" --parent-out "$tmp/parent.xml" >/dev/null

assert_file() {
  local name="$1" path="$2"
  [ -f "$path" ] || { echo "自检失败 [$name]: 缺少 $path"; find "$tmp/repo" -type f | sed 's/^/  /'; exit 1; }
  echo "自检通过 [$name]"
}

assert_contains() {
  local name="$1" path="$2" pattern="$3"
  grep -qF "$pattern" "$path" || { echo "自检失败 [$name]: $path 未包含「$pattern」"; exit 1; }
  echo "自检通过 [$name]"
}

assert_file "元数据组名 + 文件名身份" "$tmp/repo/org/example/alpha/9.9.9/alpha-9.9.9.jar"
assert_file "private-sdk GAV"      "$tmp/repo/net/qiyuesuo/sdk/private-sdk/RELEASE/private-sdk-RELEASE.jar"
assert_file "gikam 归组"           "$tmp/repo/com/sunwayworld/cloud/gikam-core-web/14.1.5/gikam-core-web-14.1.5.jar"
assert_file "文件名回退"           "$tmp/repo/recovered/bundle/nomaven/2.0.1/nomaven-2.0.1.jar"
assert_file "artifactId 清洗"      "$tmp/repo/recovered/bundle/bad-name/1.0/bad-name-1.0.jar"
assert_file "同名不同版本 1"       "$tmp/repo/recovered/bundle/jna/4.5.1/jna-4.5.1.jar"
assert_file "同名不同版本 2"       "$tmp/repo/recovered/bundle/compat1/jna/5.8.0/jna-5.8.0.jar"
assert_file "parent 已安装"        "$tmp/repo/com/sunwayworld/cloud/gikam-core-parent/14.1.5/gikam-core-parent-14.1.5.pom"
assert_file "RELEASE 版本元数据"   "$tmp/repo/net/qiyuesuo/sdk/private-sdk/maven-metadata.xml"
assert_contains "RELEASE 元数据内容" "$tmp/repo/net/qiyuesuo/sdk/private-sdk/maven-metadata.xml" "<release>RELEASE</release>"

assert_contains "parent 含依赖 alpha"        "$tmp/parent.xml" "<artifactId>alpha</artifactId>"
assert_contains "parent 含依赖 gikam-core-web" "$tmp/parent.xml" "<artifactId>gikam-core-web</artifactId>"
assert_contains "parent 含 -parameters"      "$tmp/parent.xml" "<parameters>true</parameters>"
assert_contains "parent 禁止隐式编译"          "$tmp/parent.xml" "<arg>-implicit:none</arg>"
assert_contains "parent 含 java 打包规则"    "$tmp/parent.xml" "<include>**/*.xml</include>"
assert_contains "parent 排除框架 FileUtils 源码副本" "$tmp/parent.xml" "<exclude>com/sunwayworld/framework/utils/FileUtils.java</exclude>"
assert_contains "版本以文件名为准(9.9.9)"    "$tmp/parent.xml" "<version>9.9.9</version>"

n_alpha="$(grep -cF '<artifactId>alpha</artifactId>' "$tmp/parent.xml")"
[ "$n_alpha" = "1" ] || { echo "自检失败 [parent GA 唯一]: alpha 出现 $n_alpha 次"; exit 1; }
echo "自检通过 [parent GA 唯一]"

n_jars="$(find "$tmp/repo" -name '*.jar' | wc -l)"
[ "$n_jars" = "8" ] || { echo "自检失败 [依赖不丢失]: 期望 8 个 jar，实际 $n_jars"; exit 1; }
echo "自检通过 [依赖不丢失]"

first="$(find "$tmp/repo" -type f | sort | xargs sha256sum | sha256sum)"
python3 "$TOOL" --jar "$tmp/run.jar" --repo "$tmp/repo" --parent-out "$tmp/parent.xml" >/dev/null
second="$(find "$tmp/repo" -type f | sort | xargs sha256sum | sha256sum)"
[ "$first" = "$second" ] || { echo "自检失败 [重复执行幂等]"; exit 1; }
echo "自检通过 [重复执行幂等]"

echo "deps_repo.py 自检全部通过"
