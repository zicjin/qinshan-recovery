#!/usr/bin/env python3
"""从生产 run.jar 的 BOOT-INF/lib 提取固定版本依赖，安装为本地 Maven 仓库，并生成重建的 gikam-core-parent。

用法：
    deps_repo.py --jar <run.jar> --repo <本地仓库目录> [--parent-out <生成的 parent pom 副本>] [--manifest <sha256 清单>]

- GAV 判定：jar 内 META-INF/maven/<groupId>/<artifactId>/pom.properties 优先；
  否则按文件名 `<artifactId>-<version>.jar` 回退，gikam-* 归组 com.sunwayworld.cloud，
  其余归组 recovered.bundle（回退件仅作本地标识，真实版本以文件名记录）。
- 坐标身份以文件名为准（内嵌元数据可能撒谎：实测 jaxb-xjc-2.3.1.jar 内实为 dtd-parser），
  内嵌元数据仅补全 groupId 并在不一致时记录；坐标清洗为 [A-Za-z0-9_.-]；
  groupId:artifactId 冲突时（如生产同时携带 jna 4.5.1 与 5.8.0）改写 groupId 去重，
  保证 402 个 jar 全部进入依赖集，不静默丢失。
- 生成的 gikam-core-parent 为【重建件】（私有 nexus 不可达、私有 jar 无内嵌 POM）：
  携带全部固定版本依赖、Java 8 编译参数（含 -parameters）与资源打包规则。
- 输出确定性（依赖排序、无时间戳），重复执行幂等。
"""
import argparse
import hashlib
import io
import re
import sys
import zipfile
from pathlib import Path, PurePosixPath

PARENT_GAV = ("com.sunwayworld.cloud", "gikam-core-parent", "14.1.5")

PARENT_TEMPLATE = """\
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <!--
    重建件：原 gikam-core-parent:14.1.5 不可得（私有 nexus 不可达、私有 jar 无内嵌 POM）。
    本 POM 由 tools/deps_repo.py 从生产 run.jar 的 BOOT-INF/lib 生成：
      - 依赖块 = 生产 BOOT-INF/lib 的 402 个固定版本 jar（取得方式与校验见 build-support 文档）
      - 编译与资源打包规则按生产字节码证据复刻（-parameters、src/main/java 下 xml/xls 随包）
  -->
  <groupId>{group}</groupId>
  <artifactId>{artifact}</artifactId>
  <version>{version}</version>
  <packaging>pom</packaging>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <build>
    <resources>
      <resource>
        <directory>src/main/resources</directory>
      </resource>
      <resource>
        <directory>src/main/java</directory>
        <includes>
          <include>**/*.xml</include>
          <include>**/*.xls</include>
          <include>**/*.xlsx</include>
        </includes>
      </resource>
    </resources>
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.1</version>
          <configuration>
            <source>1.8</source>
            <target>1.8</target>
            <encoding>UTF-8</encoding>
            <parameters>true</parameters>
            <debug>true</debug>
            <!-- Do not implicitly compile excluded historical source copies. -->
            <compilerArgs>
              <arg>-implicit:none</arg>
            </compilerArgs>
            <!--
              Historical source copy only: the production runtime class is
              supplied by gikam-core-common, so do not shadow it in the
              rebuilt business JAR.
            -->
            <excludes>
              <exclude>com/sunwayworld/framework/utils/FileUtils.java</exclude>
            </excludes>
          </configuration>
        </plugin>
        <plugin>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-maven-plugin</artifactId>
          <version>2.3.4.RELEASE</version>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-antrun-plugin</artifactId>
          <version>3.1.0</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>

  <dependencies>
{dependencies}
  </dependencies>
</project>
"""

DEPENDENCY_TEMPLATE = """\
    <dependency>
      <groupId>{group}</groupId>
      <artifactId>{artifact}</artifactId>
      <version>{version}</version>
    </dependency>"""

MINIMAL_POM_TEMPLATE = """\
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <!-- 由 tools/deps_repo.py 从生产 run.jar BOOT-INF/lib 提取生成（固定版本依赖，无传递依赖声明） -->
  <groupId>{group}</groupId>
  <artifactId>{artifact}</artifactId>
  <version>{version}</version>
  <packaging>jar</packaging>
</project>
"""

# RELEASE/LATEST 是 Maven 的特殊版本字面量，需要构件级 maven-metadata.xml 才能解析
VERSION_METADATA_TEMPLATE = """\
<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>{group}</groupId>
  <artifactId>{artifact}</artifactId>
  <versioning>
    <release>{version}</release>
    <versions>
      <version>{version}</version>
    </versions>
    <lastUpdated>20260924000000</lastUpdated>
  </versioning>
</metadata>
"""


def parse_maven_metadata(data: bytes):
    """META-INF/maven 元数据 -> GAV 或 None。"""
    with zipfile.ZipFile(io.BytesIO(data)) as inner:
        props = [
            n for n in inner.namelist()
            if n.startswith("META-INF/maven/") and n.endswith("/pom.properties")
        ]
        if not props:
            return None
        text = inner.read(props[0]).decode("utf-8", errors="replace")
    kv = {}
    for line in text.splitlines():
        if "=" in line and not line.strip().startswith("#"):
            k, v = line.split("=", 1)
            kv[k.strip()] = v.strip()
    if all(kv.get(k) for k in ("groupId", "artifactId", "version")):
        return kv["groupId"], kv["artifactId"], kv["version"]
    return None


FILENAME_VERSION = re.compile(r"^(?P<artifact>.+)-(?P<version>\d[\w.]*(?:-[\w.]+)*)$")
FILENAME_TAIL = re.compile(r"^(?P<artifact>.+)-(?P<version>[^-]+)$")


def parse_filename_gav(stem: str):
    """文件名 <artifactId>-<version> 拆分：版本优先取结尾的数字段（可带 -后缀）。"""
    m = FILENAME_VERSION.match(stem) or FILENAME_TAIL.match(stem)
    if m:
        return m.group("artifact"), m.group("version")
    return stem, "unknown"


def resolve_gav(jar_bytes: bytes, filename: str):
    """坐标以文件名为部署身份（内嵌元数据可能与文件名不符，实测 jaxb-xjc-2.3.1.jar 内为 dtd-parser）；
    内嵌元数据仅用于补全 groupId，并在不一致时记录。"""
    meta = parse_maven_metadata(jar_bytes)
    stem = filename[: -len(".jar")] if filename.endswith(".jar") else filename
    artifact, version = parse_filename_gav(stem)
    if meta:
        group = meta[0]
        if (meta[1], meta[2]) != (artifact, version):
            return (group, artifact, version), "filename, metadata=%s:%s:%s" % meta
        return (group, artifact, version), "maven-metadata"
    if artifact.startswith("gikam-"):
        group = "com.sunwayworld.cloud"
    else:
        group = "recovered.bundle"
    return (group, artifact, version), "filename"


def sanitize(coord: str) -> str:
    """Maven 坐标清洗：仅保留 [A-Za-z0-9_.-]。"""
    return re.sub(r"[^A-Za-z0-9_.\-]", "-", coord)


def dedup_gavs(entries):
    """groupId:artifactId 唯一化；冲突时改写 groupId（保留文件名身份），保证依赖不丢失。"""
    seen = {}
    out = []
    for filename, gav, origin in entries:
        group, artifact, version = (sanitize(c) for c in gav)
        if (group, artifact) in seen:
            n = seen[(group, artifact)]
            seen[(group, artifact)] = n + 1
            group = "%s.compat%d" % (group, n)
            origin += ", ga-uniquified-group"
        else:
            seen[(group, artifact)] = 1
        out.append((filename, (group, artifact, version), origin))
    return out


def write_repo_file(repo: Path, gav, content: bytes, ext: str):
    group, artifact, version = gav
    d = repo.joinpath(*group.split("."), artifact, version)
    d.mkdir(parents=True, exist_ok=True)
    (d / ("%s-%s.%s" % (artifact, version, ext))).write_bytes(content)


def write_version_metadata(repo: Path, gav):
    """RELEASE/LATEST 等特殊版本字面量的构件级元数据。"""
    group, artifact, version = gav
    d = repo.joinpath(*group.split("."), artifact)
    d.mkdir(parents=True, exist_ok=True)
    meta = VERSION_METADATA_TEMPLATE.format(group=group, artifact=artifact, version=version).encode("utf-8")
    (d / "maven-metadata.xml").write_bytes(meta)
    (d / "maven-metadata-local.xml").write_bytes(meta)


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--jar", required=True, help="生产 run.jar")
    ap.add_argument("--repo", required=True, help="输出的本地 Maven 仓库目录")
    ap.add_argument("--parent-out", help="生成的 parent pom 副本输出路径")
    ap.add_argument("--manifest", help="提取依赖的 sha256 清单输出路径")
    args = ap.parse_args()

    repo = Path(args.repo)
    deps = []
    manifest_lines = []
    entries = []

    with zipfile.ZipFile(args.jar) as z:
        lib_names = sorted(
            n for n in z.namelist()
            if n.startswith("BOOT-INF/lib/") and n.endswith(".jar")
        )
        for name in lib_names:
            filename = PurePosixPath(name).name
            jar_bytes = z.read(name)
            gav, origin = resolve_gav(jar_bytes, filename)
            entries.append((filename, gav, origin, jar_bytes))

    resolved = dedup_gavs([(f, g, o) for f, g, o, _ in entries])
    for (filename, _, _, jar_bytes), (_, gav, origin) in zip(entries, resolved):
        write_repo_file(repo, gav, jar_bytes, "jar")
        pom = MINIMAL_POM_TEMPLATE.format(group=gav[0], artifact=gav[1], version=gav[2])
        write_repo_file(repo, gav, pom.encode("utf-8"), "pom")
        if gav[2].upper() in ("RELEASE", "LATEST"):
            write_version_metadata(repo, gav)
        deps.append(gav)
        manifest_lines.append(
            "%s  %s:%s:%s  (%s, from %s)"
            % (hashlib.sha256(jar_bytes).hexdigest(), gav[0], gav[1], gav[2], origin, filename)
        )

    deps.sort()
    parent_pom = PARENT_TEMPLATE.format(
        group=PARENT_GAV[0],
        artifact=PARENT_GAV[1],
        version=PARENT_GAV[2],
        dependencies="\n".join(
            DEPENDENCY_TEMPLATE.format(group=g, artifact=a, version=v) for g, a, v in deps
        ),
    ).encode("utf-8")
    write_repo_file(repo, PARENT_GAV, parent_pom, "pom")
    if args.parent_out:
        Path(args.parent_out).parent.mkdir(parents=True, exist_ok=True)
        Path(args.parent_out).write_bytes(parent_pom)
    if args.manifest:
        Path(args.manifest).parent.mkdir(parents=True, exist_ok=True)
        Path(args.manifest).write_text("\n".join(manifest_lines) + "\n", encoding="utf-8")

    # Keep the CLI result safe on deployment hosts whose Python stdout encoding
    # is ASCII (for example, LC_ALL=C).  The generated POM and manifest remain
    # UTF-8; only this human-readable status line is deliberately ASCII.
    print("Dependencies installed: %d jars; rebuilt parent %s" % (len(deps), ":".join(PARENT_GAV)))
    return 0


if __name__ == "__main__":
    sys.exit(main())
