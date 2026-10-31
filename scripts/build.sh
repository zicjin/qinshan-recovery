#!/usr/bin/env bash
# 文档化构建入口：从恢复源码生成完整可部署产物（Spring Boot 可执行 run.jar）。
#
# 用法: scripts/build.sh
# 前置: scripts/build-prep.sh（依赖提取与 JDK 准备）
#
# 构建环境（AC-02）：
#   - 运行时/编译 JDK：Oracle JDK 8u391（materials/jdk-8u391-linux-x64.tar.gz 解包）
#   - 构建工具：Maven 3.9.x（本机 3.9.12）
#   - 依赖：全部来自 build/maven-repo（固定版本，见 evidence/t02-deps-manifest.sha256）；
#     公共构建插件来自 build-prep 校验并解包的离线种子包。
#
# 产物：project/target/gikam-third-czjky-14.1.5-run.jar（由 project/pom.xml 的
#   spring-boot-maven-plugin repackage + antrun 改名步骤生成，再由 tools/normalize_jar.py
#   固定 ZIP 条目顺序和时间戳，保持与生产包同名同布局并支持字节级可复现构建）。
#
# 业务编译失败或缺失会直接使构建失败（javac 报错），不存在静默沿用旧业务字节码的路径。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MAVEN_REPO="${MAVEN_REPO:-$ROOT/build/maven-repo}"
JAVA_HOME="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"

[ -x "$JAVA_HOME/bin/javac" ] || { echo "缺少 JDK，请先运行 scripts/build-prep.sh" >&2; exit 1; }
[ -d "$MAVEN_REPO" ] || { echo "缺少本地依赖仓库，请先运行 scripts/build-prep.sh" >&2; exit 1; }

export JAVA_HOME
echo "[build] JAVA_HOME=$JAVA_HOME"
"$JAVA_HOME/bin/java" -version 2>&1 | head -1
mvn -version | head -1

cd "$ROOT/project"
mvn -o -B -Dmaven.repo.local="$MAVEN_REPO" clean verify

cd "$ROOT"
PYTHONDONTWRITEBYTECODE=1 python3 tools/normalize_jar.py \
  project/target/gikam-third-czjky-14.1.5-run.jar

echo "[build] 产物: $ROOT/project/target/gikam-third-czjky-14.1.5-run.jar"
