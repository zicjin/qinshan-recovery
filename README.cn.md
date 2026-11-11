# LIMS 自有纯源码发行包

本独立仓库包含用于 Sunway 供应商退出的纯源码应用。目前已提供应用 build/package、
基于生产环境提取的部分 Oracle DDL/技术配置初始化包、整合核心与外部对象的安装计划，
以及首位管理员安装工具。由于 schema、配置、grants 和安装验收尚未完成，
目前仍无法在新服务器上完整恢复系统。在配置新环境之前，请先阅读
[部署边界](#全新设备部署边界)。仓库包含原始 `project/` 源码、重建的 framework 模块、
自有 Java 模块、纯源码 build/package 工具、仅刷新应用的部署工具，
以及完成闭环仍需通过的 API/证据 gates。

历史恢复、Data Pump、CFR/Vineflower、native 取证和一次性迁移工具保留在父级规划仓库的
`.scratch/sunway-supplier-exit-api-first/migration-tooling/` 中。它们不属于构建或部署输入，
全新的独立 checkout 也不需要这些工具。

## 源码布局

- `project/`：原始应用源码和资源。
- `reconstituted/framework/`：纳入版本控制的重建 framework 源码、资源、provenance（来源记录）和模块构建元数据。
- `owned-*/`：自有 Java 衔接模块及其资源。
- `owned-distribution/`：确定性的纯源码打包工具和启动入口。
- `scripts/`：构建、部署、运行和验证入口。
- `tools/`：API 规划/闭环和源码验证工具。
- `build-support/`：受控外部构建输入的完整性 manifests。
- `deployment/oracle/core-schema/`：基于生产环境提取的部分 DDL 和技术配置初始化内容，带有阻止不完整安装的保护机制。
- `deployment/oracle/external-schema/`：选定的 HT/SP/XFP 表 DDL、默认值、索引、约束及精确子集验证。
- `deployment/oracle/installation-manifest.json` 和 `install.sql`：面向一个空应用 schema 的整合式部分安装计划及受保护的入口。
- `docs/reconstitution/`：源码恢复的 provenance 和编译快照。

## 受控构建输入

最终构建不读取供应商运行 JAR、CFR、Vineflower 或 native 取证工具。
请从外部提供以下受控输入：

- 构建主机和运行主机都需要通过 `JAVA_HOME` 提供完整的 JDK 8（包括 `java`、`javac` 和 `jar`）；原始 LCDP Java 资源会在应用启动和运行期间编译；
- Python 3.10+、Maven 3.9.12、Bash、`jq`、`flock` 和 GNU core utilities；
- 通过 `LIMS_PUBLIC_DEPENDENCY_DIR` 提供公共依赖 JAR，且须与 `reconstituted/framework/public-dependency-allowlist.json` 一致；
- 通过 `LIMS_EXTERNAL_ARTIFACT_ROOT/maven-build-plugin-seed.tar.gz` 提供由 hash 固定的 Maven plugin seed。

供应商运行 JAR 仅作为 `verify-source-only-checkpoint.py --run-jar` 的可选、只读 provenance 输入；
它绝不是 runtime 依赖，也不是常规构建依赖。

上述依赖目录、Maven 及其 plugin seed 都属于构建主机的输入。
使用已构建发行包的运行主机使用包内 `lib/` classpath，无需 Maven、plugin seed 或单独的公共 JAR 目录。
由于已发布的 LCDP Java 会在 runtime 编译，运行主机仍需完整的 JDK 8。
包内安装完整性检查需要 Python 3.10+，执行已完成的 Oracle 安装包还需要匹配的 SQLPlus 客户端。

## 构建与验证

在独立仓库根目录运行以下命令。构建前先选择数据库访问模式。
下面的示例选择已批准的同 schema 逻辑克隆模式，并渲染两个 CRM Mapper overlays；
它不会创建 schema，也不会提供外部业务数据：

```bash
export JAVA_HOME=/opt/jdk1.8.0_391
export LIMS_PUBLIC_DEPENDENCY_DIR=/opt/lims-public-dependencies
export LIMS_EXTERNAL_ARTIFACT_ROOT=/opt/lims-build-artifacts
export EXTERNAL_SCHEMA_PROFILE="$PWD/api-fixtures/schema/external-schema-runtime-profile-local-view.example.json"
export EXTERNAL_SCHEMA_MANIFEST="$PWD/api-fixtures/schema/external-schema-clone-manifest.json"

bash scripts/verify-external-artifacts.sh --source-only
OWNED_DISTRIBUTION_REUSE=1 bash scripts/build-owned-distribution.sh
LIMS_DEPLOY_DIST="$(readlink -f owned-distribution/target/distribution)"
python3 scripts/verify-source-only-checkpoint.py \
  --distribution "$LIMS_DEPLOY_DIST"
SOURCE_ONLY_CHECKPOINT=1 python3 owned-distribution/tests/test-package.py
```

package gate 会在临时快照中执行两次 clean build 和一次重新打包。
请为 `TMPDIR` 预留足够磁盘空间，以容纳这些快照和公共库；
容量较小、基于 RAM 的 `/tmp` 可能会报 `No space left on device`。
例如，运行 package gate 前，可将 `TMPDIR` 设置为构建磁盘上已存在、由当前任务管理的目录。
这不会改变检查内容。

使用 `LIMS_DEPLOY_DIST` 作为解析后的部署/挂载目录，并在应用运行期间保持该目录完整；
激活 build cache 可能会改变 `target/distribution` 链接。
在私有 runtime properties 中，必须且只能设置一条
`owned.external-schema.access-mode=local-view`。
如果采用直接访问 schema 的部署方式，请使用经过审查的 `direct-schema` profile 及匹配的属性。
构建时若 `EXTERNAL_SCHEMA_PROFILE` 和 `EXTERNAL_SCHEMA_MANIFEST` 均未设置，
则保留原始 DB-link SQL，并要求使用 `original-db-link`。
launcher 会在启动 Java 前检查包内模式及其内嵌 contract，并拒绝缺失或不匹配的选择。
设置属性无法改写已构建的 artifact。执行首位管理员命令之前，必须先取得并验证此发行包。
只有在目标 schema、配置 bootstrap 和身份初始化就绪后才能启动应用；
请遵循[全新设备部署顺序](docs/deployment/clean-device-contract.md)。

编译 provenance 和 package manifest 会记录选定的模式、profile、生效的 manifest、renderer，
以及渲染后的 Mapper hashes。这些记录用于确定构建输入；
Oracle schema、数据、权限和真实 HTTP 行为仍需分别验证。

## 全新设备部署边界

仓库目前尚未包含完整、对应当前生产环境的 Oracle DDL/bootstrap 包。
因此，仅凭一次 clean clone，无法在新服务器上重建完整的 LIMS schema。
`project/sql/` 下的历史文件用于记录发布、升级和源码来源，并不是有序的初始化脚本；
不要将整个目录作为 bootstrap 执行。

| SQL 材料 | 当前用途 | 能否初始化空的 LIMS 数据库？ |
| --- | --- | --- |
| `project/sql/` | 历史发布、升级和源码 provenance | 不能 |
| `api-fixtures/schema/production-ddl-*-20261109.sql` | 对现有测试 schema 进行经过审查的增量对齐 | 不能 |
| 其他 `api-fixtures/schema/*.sql` | 在明确前置条件下进行定向测试恢复和元数据 fixture 设置 | 不能 |
| `api-fixtures/schema/external-logical-clone.sql` | 在现有本地 runtime schema 中创建十张 HT/SP/XFP 表的结构 | 不能 |
| `deployment/oracle/core-schema/` | 基于生产环境提取的部分结构包：737 张表、有序的索引/类型/例程及对象验证器 | 部分支持；阻塞项未清除时，常规 installer 会拒绝执行 |
| `deployment/oracle/external-schema/` | 可移植的部分 HT/SP/XFP 十表包，包含采集的默认值、原始索引关联和精确子集验证 | 部分支持；要求空的诊断用户，并拒绝完整安装 |
| `deployment/oracle/installation-manifest.json` 和 `install.sql` | 整合外部/核心阶段顺序、命名空间检查和精确的 747 表并集验证器 | 部分支持；常规入口会在执行任何子级 SQL 或 DDL 前拒绝安装 |

目前已提供与源码绑定的选定 737 张核心表和十张外部表的 DDL，以及整合安装计划。
这只是范围明确的结构子集，不能证明已具备完整的当前生产 DDL、配置或独立新服务器部署条件。

最简离线计划检查命令如下：

```bash
python3 scripts/verify-combined-oracle-installation.py --check-plan
```

它无需 Oracle 或私有采集文件，即可检查固定版本的子包、精确的阶段顺序、共享 preflight、
跨包对象命名空间和最终并集验证器。退出码 0 表示 `plan_valid=true`；
结果仍为 `status=blocked`、`deployment_ready=false` 和 `complete_external_domains=false`。
不带 `--check-plan` 时，验证器以退出码 2 阻止部署。
在这些阻塞项仍然存在时，常规 SQL 入口 `deployment/oracle/install.sql`
会在执行子级 SQL 或 DDL 之前抛出 `ORA-20100`。没有 override 模式。

整合后的顺序是：先执行一次空用户/字符集 preflight，接着执行外部表、索引、约束和
`90-verify.sql`，然后按原始顺序执行核心 DDL、技术配置 bootstrap 及其验证、
核心 `90-verify.sql`，最后执行 `99-verify.sql`，验证精确的 747 表逻辑对象并集。
外部 `90-verify.sql` 要求恰好存在十张表，因此必须在核心对象创建之前运行。
直接串联两个子包的 `install.sql` 也会因重复执行空用户 preflight 而失败。
具体阶段见[整合计划](deployment/oracle/README.md)；这一顺序并不意味着可以绕过部分安装保护。
整合计划将选定的外部对象放入应用 schema，与 `local-view` 构建 profile 一致。
它不会配置独立的 `direct-schema` owners，也不会创建原始 database links。

项目剩余工作包括：补齐有序、经过脱敏且对应当前生产环境的核心 DDL 包、
对象/grant 验证器，以及版本化的配置 bootstrap（菜单、权限、字典、工作流和动态 LCDP 资源）。
现有测试数据库或历史发布 SQL 无法满足这一 contract。
普通业务数据可以单独添加；运行原始应用所必需的配置必须具备可复现的初始化路径。
交付该包仍属于项目剩余工作。它必须包含在独立仓库中，或作为版本化、由 hash 固定的部署 bundle 提供；
新服务器安装不得依赖父级 checkout、私有采集目录或开发者现有的 Oracle volume。

部分结构与技术配置 bootstrap 包位于
[deployment/oracle/core-schema](deployment/oracle/core-schema/README.md)。
无需私有材料或父级 checkout，即可检查其完整性：

```bash
python3 scripts/verify-core-schema-package.py --check-package
```

此检查通过并不表示具备部署条件。常规 package gate 和 SQL installer
仍会在应用 DDL 之前拒绝这个不完整的包。
其中范围有限的 `70-bootstrap.sql` 提供了分布在 31 张表中的 8,254 条经审查技术配置记录，
涵盖 locale、菜单、实体和 UI 配置、28 个字典类别、120 个选项及其翻译依赖。
采集内容分别绑定；生产环境中为空的类别仍保持为空。
此外，包内还包含七个经过审查、已发布的 LCDP 模块，覆盖设备、文件夹/委托单/项目/样品信息、
报告查询和交付，附带十个菜单、设备字典/选择器，以及两个原始文件服务 Java 资源。
`lcdp.file.server.FileScript` 保留其采集到的仅有活动版本的资源及 root sentinel；
不会虚构已发布的历史版本。原始上传验证器中的 `FILE-EXTENSIONS` 和 `FILE-SPACELIMIT`
常量也在 bootstrap 中与源码绑定。包中还包含两个原始导入模板定义、十一条列配置和一条文件元数据记录。
独立的[模板资源包](deployment/oracle/import-templates/README.md)
提供一个已清理元数据、仅含表头的 workbook，以及离线放置命令。
另一个源模板既没有文件，也没有映射列，仍明确处于 blocked 状态。
其他动态模块、工作流和权限 bootstrap 尚未完成；含有内嵌业务数据的 draft scripts 已被排除。
包中还保留了 188 条采集的请求到菜单映射和六个权限类型定义，
包括原始停用标志，以及七处指向生产环境缺失菜单的引用。
它不会复制生产环境授权，也不会启用可选的 `sunway.api.check-access-permission` 设置。
对应匹配规则、权限面板定义和安装顺序见
[access-control.md](docs/deployment/access-control.md)。
完整的权限、工作流和动态 LCDP 初始化仍未完成；此阶段不能替代完整的 bootstrap。
bootstrap 还提供生产环境采集的 41 个工作流服务定义，存放于 `T_CORE_MENU_EXT`，
保留 40 个启用项和一个禁用项。
服务定义与流程 XML、版本、组织范围和候选人分配相互独立；
这些元数据记录不会安装上述工作流依赖。
包中还包含四张原始审计配置表及全部 3,336 条采集的技术配置记录。
现有启用/禁用状态，以及源数据中的孤立或重复目标均予以保留。
这不会启用 audit profiles 或字段开关。
两种不同审计模式及其安装边界见[审计配置](docs/deployment/audit-configuration.md)。
包中还包含 45 个采集的编号定义和两个分段定义。
可变的生产计数器已被排除：只有在经过验证的空安装中，`JSONPARAM` 才会以 NULL 初始化。
详见[编号配置](docs/deployment/numbering-configuration.md)。
发行包现已包含一次性的[首位管理员命令](docs/deployment/first-administrator.md)，
接受私有的安装身份信息、密码和策略。它使用原始 Java password encoder，
在经过验证的空身份集合中执行初始化；它不是公开的 bootstrap endpoint，也不是完整的应用配置安装工具。

独立的[工作流安装工具](docs/deployment/workflows.md)使用包内的符号化包，
安装 20 个非空的最新流程定义，并保留七个源内容为空的阻塞项。
请先准备声明的目标身份和成员关系，停止目标应用，再向 `initialize-workflows.sh`
提供完整的私有映射。它只写入工作流配置，保留版本以及候选人/组织边界，
不会授予权限，也不能证明业务流程已通过验收。

纯源码发行包包含 `installation/`：整合计划所需的完整公共 Oracle SQL/元数据、
两个离线验证器，以及其自身的 README。包括嵌套 package manifests 在内的每个文件，
都在 distribution manifest 中记录了 hash。
其中不包含父级 checkout、私有采集内容或历史 SQL。请从解析后的发行包目录验证随包材料：

```bash
python3 "$LIMS_DEPLOY_DIST/installation/scripts/verify-combined-oracle-installation.py" --check-plan
```

应用 JAR 和 runtime classpath 不使用这些文件。
这个 bundle 补齐了现有选定 SQL 的交付缺口；其常规 installer 仍会拒绝不完整的安装包。
目前它还不能初始化一个完整的新数据库。
完整部署指南仍位于本独立 checkout 的 `docs/deployment/` 下；
补齐缺失安装内容属于项目工作，并不是要求运维人员手工重建这些内容。

部署前，运维人员必须提供一个 Oracle PDB、一个 Redis 实例、带有所需 grants 的应用 schema/user、
私有 runtime properties 文件、项目已完成且与目标版本对应的 schema 包、
经过验证且匹配的发行包，以及完整的 JDK 8。
从源码构建的主机还需提供前述公共依赖 JAR 和 Maven plugin seed。
凭据、license 材料、私钥、DB-link 字符串和业务数据均须置于 Git 和发行包之外。

现在，无需私有采集回执或父级文件，即可检查可移植的
[外部表包](deployment/oracle/external-schema/README.md)：

```bash
python3 scripts/verify-core-schema-package.py \
  --manifest deployment/oracle/external-schema/manifest.json --check-package
```

它保留了选定表的 33 个默认值，以及原始约束/索引关联。
其精确阶段现已纳入整合安装计划。
选定的十表包还绑定了源数据库/session NLS、collation、表/列属性和约束标志，
并提供只读目标验证。生产应用可达的完整外部根对象、非表 DDL、依赖、目标 grants
以及 runtime 访问验收仍未解决。
无论此结构检查还是整合计划检查，都不能证明存在外部业务正例数据，或已实现真实 HTTP 等价。
此包不会更新现有 runtime schema。

较早的外部逻辑克隆 fixture 仅提供结构。它要求已有应用 schema，
会创建十张已批准的 HT/SP/XFP 表及索引，不创建 DB link，也不插入业务记录。
下面的就绪检查目前会输出 `status=blocked`、`deployment_ready=false`，并以退出码 **2** 结束，
使 shell 部署序列在应用 fixtures 之前停止：

```bash
python3 scripts/verify-clean-device-contract.py
```

如仅需离线验证仓库声明的限制，请显式使用 contract-check 模式。
退出码 0 表示 contract 一致；`deployment_ready` 仍为 false：

```bash
python3 scripts/verify-clean-device-contract.py --check-contract
python3 scripts/verify-external-logical-clone-fixture.py
```

fixture 验证器还会读取原始的私有生产元数据采集回执。
目前它是恢复工作区的 provenance 审计命令，并不是适用于独立 clean clone 的部署验证器。
最终可部署 schema 包的验证路径必须移除这一依赖。

新安装应先补齐并使用整合的
`deployment/oracle/installation-manifest.json` / `install.sql` contract。
较早的 `api-fixtures/schema/external-logical-clone.sql` 仅为现有测试 fixtures 保留；
不要对同一个 schema 同时应用这两个包。
其配套 restore script 会删除这十张表，**包括其中的全部记录**。
这是破坏性的 fixture 清理操作，不是保留数据的 rollback，也不是新服务器安装步骤。
预期顺序及其当前前置条件见 `docs/deployment/clean-device-contract.md`。

当核心 schema/配置安装包仍不完整时，`verify-clean-device-contract.py` 会按设计拒绝部署。
这是部署阻塞项，不是可选警告。不能将纯源码构建成功或外部克隆结构检查通过，
报告为已完成全新设备上的完整恢复。

发行包包含一个 Spring 应用，启动时需要外部配置。
其中不含供应商运行 JAR、私有 `gikam-core-*` JAR、Sunway native loader、license 输入、
兼容进程、内部 HTTP bridge 或 JNI daemon。

## 部署与回滚

对于已准备好的服务器，请在不可变发行包目录之外创建持久、可写的应用工作目录。
原始应用会相对于进程工作目录解析 `files/`、`temp/` 和 `multipart/`，
因此每次启动和应用回滚都应使用同一目录。启动时使用私有配置文件的绝对路径：

```bash
LIMS_APP_WORKDIR=/var/lib/lims-owned
install -d -m 0700 "$LIMS_APP_WORKDIR"
cd "$LIMS_APP_WORKDIR"
"$LIMS_DEPLOY_DIST/start.sh" /private/path/runtime.properties
```

运行 Java 的账号必须拥有此目录。容器必须显式设置 working directory，
并在该位置挂载可写的持久 volume。文件 volume 应与数据库备份一同保留；
仅替换应用 JAR 不会恢复已上传的文件内容。不要将不可变发行包目录用作工作目录。
如果安装环境不依赖 FTP，请显式设置 `owned.files.storage=local`；
原始上传服务随后会将文件内容存入持久的 `files/` 目录树。
权限、原始下载安全机制、上传常量和保留策略见[文件存储](docs/deployment/file-storage.md)。
若不设置该属性，仍会选择原始 FTP manager，并要求提供相应配置的后端。

launcher 默认监听 `127.0.0.1`。如需从其他主机直接访问，
请显式选择预期的监听接口，例如
`LIMS_BIND_ADDRESS=0.0.0.0 "$LIMS_DEPLOY_DIST/start.sh" /private/path/runtime.properties`，
并相应配置服务器/容器网络。
`LIMS_BIND_ADDRESS` 设置 JVM 的 `server.address` 属性，并覆盖 properties 文件中的值。
应用端口由 `server.port` 决定；在 WSL 与 Windows 宿主机之间转发时，请使用相同端口。
本地开发使用 18087，并复用现有 Oracle/Redis。
正在进行的验证结束后，可以执行仅刷新应用的操作；
回滚时使用匹配的私有配置启动之前的不可变发行包。
这些操作不会初始化或恢复数据库。

私有 runtime 文件必须包含 datasource 设置，并显式设置与 Redis URL 或 host/port 配置一致的
`spring.redis.database`。对于已观察到的原始客户端，仅在 Redis URL 中添加数据库后缀是不够的。
不要 flush 现有应用使用的 Redis 数据库。
就绪检查应等待 `GET /open/security/public-key` 返回非空且可解析的 RSA X.509 公钥；
启动期间返回空的 HTTP 200 不代表已就绪。
随后登录，并在同一 session 中读取 `/core/module/sys/logins/account`。
初始身份和登录 contract 见[首位管理员指南](docs/deployment/first-administrator.md)。

已准备好的 Docker 安装应同时保留停止的上一版应用容器及其不可变发行目录。
在 Docker 宿主机运行应用切换入口。它先验证两版发行物，要求 image、配置、工作目录及
持久挂载完全匹配，只允许只读发行目录 bind 不同；保留隔离网络 IP，并核对同网络的
其他运行容器身份不变。先收尾活动验证，再执行：

```bash
python3 scripts/switch-owned-application.py \
  --current "$CURRENT_APP_ID" --target "$RETAINED_APP_ID" \
  --network "$APP_NETWORK" \
  --readiness-url http://127.0.0.1:18087/open/security/public-key \
  --receipt /private/path/switch-plan.json
```

使用完整容器 ID。默认 dry-run；增加 `--execute` 并换用新的 receipt 路径才实际切换。
原容器停止后保留，反向操作可切回。就绪检查要求可解析 RSA 公钥，之后还须实际登录与
业务 readback。切换失败会尝试恢复原容器，即使恢复成功也明确报告切换失败。
该入口不创建候选容器、不迁移 schema，也不恢复数据库或文件。

旧 `refresh-owned-sss2-app.sh` / `rollback-owned-sss2-app.sh` 对应历史远端布局，
不能保留当前报表与工作目录挂载，不作为候选部署步骤。
容器准备条件、反向切换命令及当前本机/远端边界见
[应用切换与回滚](docs/owned-sss2-deployment.md)。

## API 与证据

fast differential 输出仍属于诊断结果。
只有在完成真实 HTTP 执行、当前 binding 检查、fixture/audit/rollback 观察，
以及 milestone 清理后，strict 报告才能提升为正式证据。
文档、registry 和 archive 变更不会使行为证据过期；
实现依赖范围的变更会通过 `scripts/source_binding.py` 选择受影响的 replay。

canonical evidence registry 在独立 checkout 中是可选的，
仅在 canonical 报告被提升为正式证据时创建。
父级 `.scratch/` 历史绝不属于构建或部署输入。
