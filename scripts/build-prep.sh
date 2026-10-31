#!/usr/bin/env bash
# 构建准备：从生产 run.jar 提取固定版本依赖到本地 Maven 仓库，生成重建的 gikam-core-parent。
#
# 用法: scripts/build-prep.sh
#
# 产物：
#   build/maven-repo/                        本地 Maven 仓库（-Dmaven.repo.local 指向它）
#   build/jdk1.8.0_391/                      JDK 8（自外部受控 artifact 解出）
#   build-support/gikam-core-parent-14.1.5.pom.xml   重建 parent 的可读副本
#   evidence/t02-deps-manifest.sha256        依赖完整性清单
#
# 依赖取得方式（AC-02）：
#   - 私有框架与原生组件按 ADR-0001 以固定版本保留：直接取自生产 run.jar 的 BOOT-INF/lib，
#     版本即生产运行版本，逐 jar 记录 sha256。
#   - 公共构建插件（spring-boot-maven-plugin 2.3.4.RELEASE 等，版本固定于重建 parent）
#     来自校验过的离线种子包；不依赖构建时访问 Maven Central。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ARTIFACT_ROOT="${LIMS_EXTERNAL_ARTIFACT_ROOT:-$ROOT/.external-materials}"
RUN_JAR="${SUPPLIER_RUN_JAR:-$ARTIFACT_ROOT/gikam-third-czjky-14.1.5-run.jar}"
JDK_TGZ="${JDK_ARCHIVE:-$ARTIFACT_ROOT/jdk-8u391-linux-x64.tar.gz}"
PLUGIN_SEED="${MAVEN_PLUGIN_SEED:-$ARTIFACT_ROOT/maven-build-plugin-seed.tar.gz}"
PLUGIN_SEED_MANIFEST="$ROOT/evidence/t02-build-plugin-seed.sha256"

[ -f "$RUN_JAR" ] || { echo "缺少 $RUN_JAR" >&2; exit 1; }
[ -f "$JDK_TGZ" ] || { echo "缺少 $JDK_TGZ" >&2; exit 1; }
[ -f "$PLUGIN_SEED" ] || { echo "缺少离线构建插件种子 $PLUGIN_SEED" >&2; exit 1; }
[ -f "$PLUGIN_SEED_MANIFEST" ] || { echo "缺少插件种子校验清单 $PLUGIN_SEED_MANIFEST" >&2; exit 1; }

LIMS_EXTERNAL_ARTIFACT_ROOT="$ARTIFACT_ROOT" "$ROOT/scripts/verify-external-artifacts.sh"

validate_tar() {
  local archive="$1"; shift
  python3 - "$archive" "$@" <<'PY'
import posixpath
import sys
import tarfile

archive, allow_jdk_links = sys.argv[1], sys.argv[2] == "jdk"
with tarfile.open(archive, "r:*") as tar:
    for member in tar.getmembers():
        name = member.name
        if name in ("", ".", "./"):
            continue
        if name.startswith("/") or "\\" in name:
            raise SystemExit("unsafe archive path: " + name)
        normalized = posixpath.normpath(name[2:] if name.startswith("./") else name)
        if normalized == ".." or normalized.startswith("../"):
            raise SystemExit("archive path escapes root: " + name)
        if member.isdir() or member.isreg():
            continue
        if member.issym() and allow_jdk_links:
            target = member.linkname
            if target.startswith("/") or "\\" in target:
                raise SystemExit("unsafe symlink target: " + target)
            resolved = posixpath.normpath(posixpath.join(posixpath.dirname(normalized), target))
            if resolved == ".." or resolved.startswith("../"):
                raise SystemExit("symlink escapes archive root: " + name)
            continue
        raise SystemExit("unsupported archive member: " + name)
PY
}

validate_tar "$JDK_TGZ" jdk
validate_tar "$PLUGIN_SEED" seed

mkdir -p "$ROOT/build"

echo "[build-prep] 重建 JDK 8u391 -> build/jdk1.8.0_391"
rm -rf "$ROOT/build/jdk1.8.0_391"
tar -xzf "$JDK_TGZ" -C "$ROOT/build"
"$ROOT/build/jdk1.8.0_391/bin/java" -version 2>&1 | head -1

echo "[build-prep] 提取依赖并生成重建 parent -> build/maven-repo"
rm -rf "$ROOT/build/maven-repo"
mkdir -m 700 "$ROOT/build/maven-repo"
python3 "$ROOT/tools/deps_repo.py" \
  --jar "$RUN_JAR" \
  --repo "$ROOT/build/maven-repo" \
  --parent-out "$ROOT/build-support/gikam-core-parent-14.1.5.pom.xml" \
  --manifest "$ROOT/evidence/t02-deps-manifest.sha256"

echo "[build-prep] 校验并解包离线 Maven 插件种子"
expected_seed_hash="$(awk '$2 ~ /maven-build-plugin-seed/ {print $1; exit}' "$PLUGIN_SEED_MANIFEST")"
actual_seed_hash="$(sha256sum "$PLUGIN_SEED" | awk '{print $1}')"
[ -n "$expected_seed_hash" ] && [ "$actual_seed_hash" = "$expected_seed_hash" ] || {
  echo "offline Maven plugin seed hash mismatch" >&2
  exit 1
}
tar -xzf "$PLUGIN_SEED" -C "$ROOT/build/maven-repo"

echo "[build-prep] 完成。下一步: scripts/build.sh"
