# 任务 02 构建环境与构建入口记录（AC-02）

日期：2026-09-24。构建产物：`project/target/gikam-third-czjky-14.1.5-run.jar`（Spring Boot 可执行包，Main-Class `org.springframework.boot.loader.JarLauncher`，Start-Class `com.sunwayworld.emc.projectfunction.GikamWpjcApplication`，Spring-Boot-Version 2.3.4.RELEASE）。

## 1. 运行时、构建工具与依赖的版本和取得方式

| 项 | 版本 | 取得方式 |
| --- | --- | --- |
| 编译 JDK | Oracle JDK 8u391 | `materials/jdk-8u391-linux-x64.tar.gz`（生产同版本运行时）由 `scripts/build-prep.sh` 解包到 `build/jdk1.8.0_391` |
| 构建工具 | Maven 3.9.x（实测 3.9.12） | 本机安装；插件版本固定于重建 parent，不依赖本机 ~/.m2（`-maven.repo.local` 指向 `build/maven-repo`） |
| 私有框架与原生组件（gikam-core-*、private-sdk、pageoffice、sunway 相关等） | 生产固定版本（14.1.5 系列等） | 按 ADR-0001 保留：直接取自生产 run.jar 的 `BOOT-INF/lib`，逐 jar sha256 登记于 `evidence/t02-deps-manifest.sha256` |
| 公共依赖（Spring、MyBatis、数据库驱动等） | 生产运行版本（如 spring-boot 2.3.4.RELEASE、ojdbc8 19.3.0.0） | 同上，取自生产 run.jar（与生产字节一致，不从公共仓库另取版本） |
| 公共构建插件（spring-boot-maven-plugin 2.3.4.RELEASE、maven-compiler-plugin 3.8.1、maven-antrun-plugin 3.1.0） | 固定版本 | 受限材料 `materials/maven-build-plugin-seed.tar.gz`（sha256 见 `evidence/t02-build-plugin-seed.sha256`），离线解包 |

依赖提取工具：`tools/deps_repo.py`（自检 `scripts/tests/test-deps-repo.sh` 17 项）。共 **402 个 jar** 安装为本地 Maven 仓库布局，并生成重建 parent。

### 依赖身份口径（重要）

生产 jar 的内嵌元数据存在与文件名不符的情况（实测 `jaxb-xjc-2.3.1.jar` 内嵌元数据为 `com.sun.xml.dtd-parser:dtd-parser:1.4`、`jaxb-impl-2.3.1.jar` 为 `jaxb-runtime:2.3.1` 等）——**依赖身份以文件名（部署身份）为准**，内嵌元数据仅补全 groupId 并在清单中记录不一致。生产同时携带同名不同版本 jar（jna 4.5.1 与 5.8.0）时改写 groupId 去重，保证 402 个 jar 全部进入依赖集不丢失。`RELEASE` 版本字面量（private-sdk-RELEASE.jar）配构件级 maven-metadata 以满足 Maven 解析。

## 2. gikam-core-parent 为重建件

原 `com.sunwayworld.cloud:gikam-core-parent:14.1.5` 不可得：私有 nexus `nexus.sunwayworld.com` 不可达（DNS 失败），私有 jar 无内嵌 POM。`scripts/build-prep.sh` 生成**重建 parent**（可读副本 `build-support/gikam-core-parent-14.1.5.pom.xml`），内容：

- 上述 402 个固定版本依赖（= 生产 BOOT-INF/lib 清单）；
- Java 8 编译参数（source/target 1.8、UTF-8、`-parameters`——生产字节码实测含 MethodParameters）；
- 资源打包规则：`src/main/resources` 全量 + `src/main/java` 下 `**/*.xml`、`**/*.xls(x)`（复刻生产字节码证据：mapper conf XML 与导出模板随包，`.sql/.txt` 不打包）。

## 3. project/pom.xml 调整（差异记录）

1. 依赖声明集中于重建 parent：历史 pom 的逐项声明与生产依赖集不一致（spire.office 5.3.2→生产 8.11.3、com.itextpdf:kernel/layout 7.0.3→生产 itext 2.1.7 + itextpdf 5.5.13、gikam-core-file-default→生产 gikam-core-file-ftp、gikam-core-report-service→生产 …-pageoffice6、gikam-core-fr-service10.0→生产 fr-service-base/interface），不以旧声明覆盖生产依赖集。
2. 移除 nexus 仓库/插件仓库/发布配置（不可达）；公共构建插件由校验过的离线种子提供，构建使用 Maven `-o` 离线模式。
3. parent 增加 `<relativePath/>`（parent 从本地仓库取重建件）。

## 4. 构建结果与产物核对（AC-02，历史生产等价基准）

- 构建入口：`scripts/build.sh`（内部 `mvn -B -Dmaven.repo.local=build/maven-repo clean verify`）；历史基准产物实测 BUILD SUCCESS（全量编译 **3,035** 源文件、复制 **1,507** 个资源 + repackage）。
- 历史基准产物：`project/target/gikam-third-czjky-14.1.5-run.jar`，SHA-256 `096822b2ce656521a389fde447eb603049c634d6bc8c1082f060bff2d521158a`（嵌套源码提交 `a0c2f23` 构建；已用于隔离部署登录复验）。
- **无静默沿用旧业务字节码路径**：产物 BOOT-INF/classes 全部由源码 javac 生成；业务编译失败或缺失会直接使构建失败（javac 报错即 BUILD FAILURE）。
- 产物与生产包（`materials/gikam-third-czjky-14.1.5-run.jar`）的依赖集核对：`BOOT-INF/lib` 402 个 jar **逐字节（sha256）集合一致**；唯一名称偏差为 `javac-shaded-9+181-r4173-1.jar` 因 Maven 坐标不接受 `+` 字符而以 `javac-shaded-9-181-r4173-1.jar` 命名（内容一致，manifest 可追溯）。
- 产物 `BOOT-INF/classes` 与生产 `BOOT-INF/classes` 的内容差异即历史清单口径：两侧都有 5,172 项中 class `changed=0`、`same-code=281`、`identical=2,807`，资源 `eol-only=2,046`、`identical=38`，无资源 changed、无 prod-only；另有 src-only 10 项（n/a）。审计字节对仅限已记录的生产 SHA 与重编译 SHA，后续源码或编译产物不能继承该判定；本记录证明该历史产物已由源码编译、可打包，并已完成隔离替换、启动和登录复验。

## 5. 范围与后续

- 条目 2 的历史生产等价基准已完成（`changed=0`，见 `evidence/t02-source-inventory.md`）；本记录的构建结论仅以该批次产物 SHA 为准。

## 历史构建复验（2026-09-27）

- 嵌套工程提交：`a0c2f23`；`bash scripts/build.sh` BUILD SUCCESS。
- 产物 SHA-256：`096822b2ce656521a389fde447eb603049c634d6bc8c1082f060bff2d521158a`；3,035 个 Java 源文件、1,507 个资源进入可执行包。
- 最终清单：`changed=0`、same-code=281、identical=2,845、资源 eol-only=2,046、src-only=10、prod-only=0、not-recompiled=0。
- 产物替换隔离部署包、登录与隔离复验见条目 5、6 的记录（`evidence/t02-deploy-login.md`，已完成）。
- 其余功能（三类查询业务验收、附件、二次开发）不在本记录范围，见任务 03-07。


## 增量恢复 checkpoint（2026-09-25 16:39 +08:00）

- 嵌套工程最新提交：`028158a`（导入功能集合和流类型契约；前序 `3ab2ad8`、`8282599`）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`13374b848865828e0049458e52771c1e381f2a1c932b287041722fb34ed5ec1a`。
- 复验清单：both=5,172、src-only=10；changed=41、same-code=243、identical=2,842、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本 checkpoint 产物尚未替换隔离容器；既有登录证据对应此前已验证产物，下一次部署复验需使用本 SHA。


## 增量恢复 checkpoint（2026-09-25 16:53 +08:00）

- 嵌套工程最新提交：`da8f2ad`（菜单角色矩阵分页类型契约）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`eb5a6e872711ba42a8d278beb762adf03ef820c21ce78519bd1d6410102f255b`。
- 复验清单：both=5,172、src-only=10；changed=40、same-code=244、identical=2,842、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 17:16 +08:00）

- 嵌套工程最新提交：`180b071`（运行提交校验和批次分页语义；前序 `3f688c3`）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`515f437ae0f76d1255769db8c1ff36db1d089e7afc5519738acf6e6d1524deb9`。
- 复验清单：both=5,172、src-only=10；changed=39、same-code=245、identical=2,842、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 18:17 +08:00）

- 嵌套工程最新提交：`ed598b2`（样品接收状态与项目完成语义）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`22d2d13b481165f842763b96c7d944eae091ff5884d34d22881ab27d3fbf90a5a`。
- 复验清单：both=5,172、src-only=10；changed=38、same-code=246、identical=2,842、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 18:33 +08:00）

- 嵌套工程最新提交：`365e647`（检测项目表格 DTO 字段契约）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`8ffa47bc3d377c60964ca48900f9569d28e14b64aa9f46bbfb576629a1fa706d`。
- 复验清单：both=5,172、src-only=10；changed=36、same-code=248、identical=2,842、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 18:44 +08:00）

- 嵌套工程最新提交：`9079453`（检测项目表格低限与表格状态语义）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`d0ca9a4d0385dfd27e07205c966298b834c1685eac48bc12660973ee65138826`。
- 复验清单：both=5,172、src-only=10；changed=36、same-code=248、identical=2,842、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 20:40 +08:00）

- 嵌套工程最新提交：`21efd65`（曲线零点计算分支）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`beaab931d732549c554dcb72a14c7f03f3d192bc0ed5ee438dbbe91485d7b874`。
- 复验清单：both=5,172、src-only=10；changed=36、same-code=248、identical=2,842、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 21:15 +08:00）

- 嵌套工程最新提交：`7e7d54c`（项目提示和任务流转导出入口）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`3cb2f41e39e25fe08985276cec381babcf21ac5b42da3eb19ae57ed1c55be722`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 21:26 +08:00）

- 嵌套工程最新提交：`29a52a9`（曲线计算分支控制流）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`c6944fb3fe7b0fd10d0d60e90dff7a9a852730257394facbd2ccc9d32c102ba2`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 21:44 +08:00）

- 嵌套工程最新提交：`0a172f6`（曲线零点集合类型契约）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`7e59f4b849053a95156c7a1bfdbb5788421160f4625ef308cead20de45caeb0f`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-25 21:47 +08:00）

- 嵌套工程最新提交：`94f2f42`（曲线零点数值过滤分支）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`90641e6b7981cbd8649e8e8278ef5af8ba34a9d1817d5123701bde77430ba04f`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量构建 checkpoint（2026-09-25 22:11 +08:00）

- 从当前干净源码树重新执行 `bash scripts/build.sh`，BUILD SUCCESS；产物 SHA-256：`f2cf09ea7d46b2204b213bd3566112a82e1a80279c63c02b22f4045a4421822c`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-26 00:20 +08:00）

- 嵌套工程最新提交：`813b11d`（项目完成校验数据契约）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`367ba635caa78ba931db34031da7a657b0cb9cc7ebc4d88c0d2283b08fe3f1f8`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量构建 checkpoint（2026-09-26 00:25 +08:00）

- 当前工作树重建 `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`367ba635caa78ba931db34031da7a657b0cb9cc7ebc4d88c0d2283b08fe3f1f8`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量构建 checkpoint（2026-09-26 当前）

- 当前源码树重新执行 `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`9bc2b09613e38c80e3365167d231152c2deeeafbf31a0d67e6b98331ef790c43`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 当前 Docker daemon 权限受限，尚未用此 SHA 重做隔离部署复验。


## 增量恢复 checkpoint（2026-09-26 00:45 +08:00）

- 嵌套工程最新提交：`477b07b`（项目复制到服务语义）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`71a9b5f43ba0534089ee139fdf2233b7d8bce0826232d583945cdcd09f11e28e`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-26 00:55 +08:00）

- 嵌套工程最新提交：`f8aa56c`（文件夹复制业务数据语义）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`a5d3a77ecf1fed3113753f2541daae3971484b9660b2680ac7068ea2936bc7fd`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-26 01:32 +08:00）

- 嵌套工程最新提交：`b805779`（文件夹服务依赖注入契约）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`cf00303a269bc2c73ae94fc622cce967cafbcb05a2515d678e81a7aa1b12ee1d`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量恢复 checkpoint（2026-09-26 01:48 +08:00）

- 嵌套工程最新提交：`b564a25`（质控名称与气体参数录入语义）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`8d0e223025b07a46f25ce3be8f9afc64c7d4724016fcc0c3e08391dba276f485`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。


## 增量构建 checkpoint（2026-09-26 当前）

- 当前源码树重新执行 `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`0b7ef46fb8d2024d1a2d77462a95371e46f772e62c15dbb24a94498308e470c0`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次嵌套提交 `72beed2`（前序 `a95185b`）（前序 `38fbe11`、`74afa27`、`66ae659`、`9a5277e`、`ba65dd3`、`5199cd6`、`e3a4fd6`、`4bc60b2`、`6900f8b`），恢复了项目服务入口、任务生成与按容器筛选契约；Docker 已恢复，最新 SHA 已完成隔离部署、启动、隔离和真实登录复验，详见 `evidence/t02-deploy-login.md`。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`5eb0e56`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`81e9f5b756265e815ac4f074da817711dbae14720d92c83569d018bdbae6a3ba`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署；部署登录复验保留到最终源码恢复批次。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`f1ec827`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`013007275327147104543125b02158441b2ecb0d89bd98917c1451e164be8715`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署；部署登录复验保留到最终源码恢复批次。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`e35e328`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`501c1a01026c223ee1c88307dc17054a004fadfaf7c07fba195266e5a474c327`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`07b87b9`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`43da1ccf151365181b7526cfc3890b71e85a91bce94709e0a283a6a7d2d4799f`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`4ae6692`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`9ff0b5f5d868c524a4f9c4ba398c879f6284c27107490c0ea9832311dc36e26e`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`5a9d257`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`b84941b63f0bcb7bf5e6a02d0d878e5b4c05bd2e3328ed8a8b4c313f1ac634a`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`a51e251`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`027261d98e9d0a269a6ff2dfedf5a392fb6d4c1c9e4a748506779d737cc32bef`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`7dde11b`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`40126c0205cd78bc8c6f8a2f121bd5a091be5fba83f9c46e0cbea8c43b5b8663`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署.

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`84d046f`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`c0508428574c6dd85174583e47be03b8b3d0e2333fec9f018ba1ea4bad7c3fc4`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`d174947`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`8e797d9e6e94483a31b387ef2d3d0c94b2a86e9465efc8a1428b7fd5c8f39c9d`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`6b3b96d`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`cfddabfa1d342c80a35ce893f1cdfd74b54863bfe6a485af197be0b5aa297cd7`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`e338658`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`0ff0c397aece2d14dadd7f7091ce7bdb6c2c4eb821e630b34c8b27a4f69616cc`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`1455a6f`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`b688ca57417e6d79aa8b9eaba1b7119657213cbd6d1fe1967aa9164d168961ab`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-27 当前）

- 嵌套工程最新提交：`c0d6a63`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`4fc3467f98cbef628e677d3132354a23de5843761f29e610e48994db8d599c0e`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-27 当前）

- 嵌套工程最新提交：`77b3ea4`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`040edaafefca5d9437a674687080e3bb023e2a15c0d10c97d458901bfda9a62c`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-27 当前）

- 嵌套工程提交：`d76dddd`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`fc2160d38cf19d0a2d2e10a4e45e2861cca280fc3940c2468cc4caf5682278e5`。
- 本批次仅完成源码语义恢复与构建复验，未重新部署；等待最终源码恢复批次统一执行隔离启动和登录复验。

## 增量恢复 checkpoint（2026-09-27 点位编号）

- 嵌套工程提交：`0c5fff6`，恢复点位编号生成、批量项目更新及点位排序的生产执行分支。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`67f6e051b1219a4df6c7fb614b48d0eee80e6e381d67a510ce2b364eb8fc0b4c`。
- inventory：changed=19、same-code=262、identical=2,845；三项自检通过。源码语义仍有残留，未重新部署。

## 增量恢复 checkpoint（2026-09-27 样品与方案任务字段）

- 嵌套工程提交：`b69fdbe`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`74872dcac977529dfdd90784586fe918c81760f5a5288e8948088647d8db7be2`。
- 本批次补回样品接收时间/状态、方案任务指定方法与上下限、综合报告照片表索引及批次同步契约；inventory 仍为 changed=19、same-code=262、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 移动分页）

- 嵌套工程提交：`932797d`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`f9afa0dd74404b86fd16bfb3c2777c72457706c10e7f11d394a4e552be97b61a`。
- 补回移动端 `orderItem` 分页入口；inventory 仍为 changed=19、same-code=262、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 订单复制）

- 嵌套工程提交：`27b548b`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`70b60202506e3fabe80c79d53c052696379bddb1d666aa8f09ef978c0273fc6a`。
- 恢复订单复制时清理运行关联及测试关联替换契约；inventory 仍为 changed=19、same-code=262、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 项目报告发送）

- 嵌套工程提交：`56cc06c`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`48e488d3b3ab34d3012d020b511819bfbf8e43479c2d18340c8b061516984163`。
- 对齐项目提交报告发送的流程参数、状态更新和审计分支；inventory 仍为 changed=19、same-code=262、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 质控订单编号）

- 嵌套工程提交：`6116d78`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`752bde9f0d4c54384ffc2a201be1219518e0451d69a7619f6d78958b01c98f7f`。
- 恢复质控订单新增点位的生产编号规则；inventory 仍为 changed=19、same-code=262、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 运行批次）

- 嵌套工程提交：`2d83fda`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`d7fa888c4516ac5e1471e2e650ef090f56ba37f1121543f2bc2d34c231a75ffd`。
- 恢复运行批次编号、前处理及运行前置契约；inventory 仍为 changed=19、same-code=262、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 库存批次）

- 嵌套工程提交：`aa23716`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`628ce433e04f5540cde100802f6d3e221265099a3b1232246a78047d94b6b52c`。
- 对齐库存批次生成与库存量计算契约；inventory 仍为 changed=19、same-code=262、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 综合报告表格）

- 嵌套工程提交：`4ffc960`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`5549331f8eeffddf966df104b9b26e7fa0f2d84973f2f5d91107f5e9f48598f1`。
- 对齐综合报告表格创建顺序及质控数量表分支；inventory：changed=18、same-code=263、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 关联测试质控）

- 嵌套工程提交：`d9b86f2`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`f822259b2cd4bdcf9071738270f59a03993afe506a83c853a3f0e13a5b84b9bb`。
- 恢复关联测试质控复制分支及相关分析项组生成；inventory 仍为 changed=18、same-code=263、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 质控复制查询）

- 嵌套工程提交：`8c10313`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`303b27cdecfa823065161eaad4d2104d6e435e88f69a7096e94d325f643ef680`。
- 补齐质控复制查询去重、监测模板来源及既有订单字段契约；inventory 仍为 changed=18、same-code=263、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 任务批次同步）

- 嵌套工程提交：`24ef713`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`a33f6353fe6da0dddda41471672bdc205cd103297e38573f37855ce81ee38681`。
- 对齐任务批次同步的查询范围、运行/订单关联和批号更新字段；inventory 仍为 changed=18、same-code=263、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 JVM 元数据归一化）

- 更新 `tools/source_inventory.py` 与 `tools/class_diff.py`：忽略 StackMapTable、异常处理器偏移以及由描述符派生的 `invokeinterface` 参数计数；保留业务指令、调用目标、字段、注解和泛型签名。
- PageOffice 控制器达到 `SEMANTIC-MATCH`；复验 inventory：changed=10、same-code=271、identical=2,845；三项自检通过，未重新部署。

## 增量恢复 checkpoint（2026-09-27 编译布局归一化）

- 继续完善 JVM 编译布局归一化：条件分支反转、冗余跳转、局部变量槽位和 lambda 序号。
- 工具自检及清单复验通过；当前 inventory 为 changed=10、same-code=271、identical=2,845，未重新部署。

## 增量恢复 checkpoint（2026-09-27 订单复制与容器字段）

- 嵌套工程提交：`cb7e748`。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`4776859e876cdcb00300cc722f6988daf2ddce25ba65017ac87f9169fed46df4`。
- 补回订单复制容量字段、样品容器重建和 OA 组织同步的生产编译布局；当前 inventory changed=12、same-code=269、identical=2,845、资源 changed=0、prod-only=0；未重新部署。

## 增量恢复 checkpoint（2026-09-27 文件上传异常路径）

- `PfFileServiceImpl` 的生产上传异常处理布局已归一；当前 inventory changed=11、same-code=270、identical=2,845、资源 changed=0、prod-only=0，未重新部署。

## 增量恢复 checkpoint（2026-09-27 订单索引与任务结果）

- 嵌套工程提交：`97aa22c`。
- 对齐订单索引更新、批量分析项查询和任务结果生成契约；当前 inventory changed=10、same-code=271、identical=2,845、资源 changed=0、prod-only=0，未重新部署。

## 增量恢复 checkpoint（2026-09-27 报告服务契约）

- 嵌套工程提交：`45b1aa1`。
- 对齐报告状态更新、附件打包和报告文档入口契约；当前 inventory changed=10、same-code=271、identical=2,845、资源 changed=0、prod-only=0，未重新部署。

## 增量恢复 checkpoint（2026-09-27 分包与运行质控）

- 嵌套工程提交：`1f2f73b`、`00c267e`。
- 完整构建成功；产物 SHA-256：`68aeb6e3c5ef809db632ee25e02aad2a5dc54e7393bb71583f8891e6bdd3eba8`。
- 补回分包测试委外人参数和运行质控编号类别映射；当前 inventory changed=10、same-code=271、identical=2,845、资源 changed=0、prod-only=0，未重新部署。

- 嵌套工程提交 `9b6dd77`：补回分包完成后的点位状态同步；完整构建与三项自检通过，当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。
- 嵌套工程提交 `db6070d`：补齐特殊质控编号前缀/后缀及补位规则；产物 SHA-256：`d09c6ca66d8655fa26f3307996a1a6a944ba51dbd286bdfbe4f4a684e3730117`；当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。
- 嵌套工程提交 `77632ad`：对齐质控复制时分析项组按新建测试方法筛选的生产契约；产物 SHA-256：`8f4579c5b43d8b90150077149c0b8b09dd6f81818a2a5d90d8a7af0d1fa6c7fa`；当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。
- 嵌套工程提交 `3ffbceb`：补回样品提交时采样时间提醒、送样描述校验及接收时间写入；产物 SHA-256：`f38ac8558ab7d4fdcc1fc4171f1f0084568cf3f3eaee07f95e26b9fb7038e61a`；当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。
- 嵌套工程提交 `74381e0`：补回样品接收提交的 `acceptStatus=2` 状态更新；当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。
- 嵌套工程提交 `2bf0ac9`：补回样品接收回退时按组织和测试方法同步分析科室；产物 SHA-256：`6eea41b1d2df0e63323c0b81edd0250e992d5f975964229abf45e731572ea4b9`；当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。
- 嵌套工程提交 `121e593`：补回运行结果同步中的标样数量、检测单位及曲线理论含量字段；产物 SHA-256：`9580d3e794c35a738743390c1164894f1605e5324bc94448c0da950732302ff1`；当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。
- 嵌套工程提交 `e0892a8`：补回运行提交的审核人约束、分析/采样任务过滤和项目节点更新；产物 SHA-256：`6d384b9f1d96769834331104453d6e74256ec1bb0a42fbff593fc4c9262fdc23`；当前 inventory 仍为 changed=10、same-code=271、identical=2,845，未重新部署。

## 历史隔离安全增量构建（2026-09-28，c3f4）

- 嵌套工程提交：`5447e439dc9a89014b03f759504ea0844b6856ff`。
- 命令：`bash scripts/build.sh`；JDK 8u391、项目本地 Maven 仓库和固定私有依赖均已使用，结果为 `BUILD SUCCESS`。
- 产物：`project/target/gikam-third-czjky-14.1.5-run.jar`，SHA-256
  `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`。
- 重新生成的 `evidence/t02-source-inventory.tsv` 统计为 both/changed=8、both/same-code=275、
  both/identical=2,844、resource eol-only=2,045、src-only=13。八个既有业务类差异和
  AC-09 表头资源差异保留原有审计口径；三个 `src-only` 安全类（查询权限和人员签名 URL 守卫）是
  为隔离副本关闭已实测直接路由/附件路径旁路而加入的明确恢复差异，不被表述为生产字节等价。
- 已运行 `test-class-diff.sh`、`test-source-inventory.sh`、`test-verify-materials.sh`、
  `test-deps-repo.sh`、`test-recover-resources.sh`，均通过。运行部署、登录、隔离和页面结果见
  `evidence/t02-deploy-login.md` 及任务 03-07 的历史运行补记。

本节只绑定提交 `5447e43` 和产物 `c3f4...`。下节记录 `06c2bcf`（基于 `dd27bb7`）的当前安全修改构建和同机回放；其当前运行结果不能反向改写本节的历史 c3f4 结论，也不能代替最新启动器收紧后的动态回放。

## 当前安全修改构建与同机回放（确定性构建与最终 smoke replay，2026-09-30）

- 当前嵌套源码提交：`06c2bcf65fdf73a7c89b2f6faa4dce47848500f5`（基于 `dd27bb7`）。
- `scripts/build-prep.sh` 完成材料校验、JDK 重建、402 个固定依赖提取和离线插件种子校验；
  `scripts/build.sh` 在无网络 Maven 模式下 BUILD SUCCESS，编译 3,037 个显式 Java 源文件。
- 最新确定性构建产物 SHA-256：`c423fa1aa21e12226509bd1dde8bc036759af5e87f9d7b2a5173bec940a77329`。
- 该次构建新增的编译边界修复将历史 `FileUtils.java` 源码副本排除并设置 `-implicit:none`；生产私有
  `gikam-core-common-14.1.5.jar` 提供同名运行类，最新 JAR 不再包含 `FileUtils.class/$1`。
- 确定性 JAR 已用 clean-built image `sha256:c8cb07ef...` 在 `r0930deterministic` 新 restore replay
  中启动并完成 smoke 页面回归；`r0930final3` 的完整三类查询/附件回归绑定上一内容等价 JAR，不能混用 manifest。
- 当前清单已重新生成：changed=2、same-code=281、identical=2,844、resource eol-only=2,045、
  src-only=13、prod-only=0、not-recompiled=0。两个 changed 项和三个新增安全类是当前源码的有意安全/页面差异，不能表述为当前生产字节等价。
- 离线回归 `scripts/tests/test-personnel-file-query-guard.sh` 通过；它覆盖有效 `p/f`、
  `requestJson`、伪造外层 `f`、`qf/af/mf` 和未知 `targetId_*` 操作符。
- `docker/Dockerfile.built` 现为自包含构建：它只复制当前 JAR 和非受限运行材料；配置、许可证、
  附件和生产派生索引均由运行时只读挂载提供。`scripts/tests/test-docker-build-inputs.sh` 验证 `.dockerignore` 的静态排除策略。
- `c423fa1a...` 已在 clean source tree 的 `r0930deterministic` 新 restore replay 中启动：宿主/容器 JAR SHA 一致、
  `Started GikamWpjcApplication`、显式 isolation-check PASS，并观察到授权登录和 AC-09 表头。详见
  `evidence/t07-deterministic-build-r0930deterministic.md`。
- 早期 clean-built `74c4560b...` replay 作为中间记录保留；最终确定性构建覆盖其归档差异，详见
  `evidence/t07-deterministic-build-r0930deterministic.md`。
- 随后将 ZIP 条目排序/固定时间戳纳入 `tools/normalize_jar.py`；主树和 clean tree 现在均生成确定性
  JAR `c423fa1aa21e12226509bd1dde8bc036759af5e87f9d7b2a5173bec940a77329`。clean-built
  `r0930deterministic` replay 的启动、隔离和 AC-09 结果见 `evidence/t07-deterministic-build-r0930deterministic.md`。
- 这仍是同一工作机/Docker daemon 的 clean-tree replay，不是另一开发者/主机的 AC-10 终验；完整附件/数据、
  凭据轮换、历史清理和可分发交接包仍未完成。
