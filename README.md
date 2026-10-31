# LIMS 恢复工程

本目录用于将线上 LIMS 部署恢复为可构建、可隔离运行、可修改和可验证的工程。生产环境仅作为
只读观察来源；恢复、构建、数据副本和验收均在本机隔离环境中进行。

## 线上环境

- SSH 主机别名：`sss`
- 部署目录：`~/lims-czjky-emc`，实际指向 `/data/LIMS/jiankeyuan/lims-czjky-emc`
- Web 地址：`http://10.100.2.196:8081`
- 只读观察账号：`Test`
- 密码：保存在受限本地材料中，不写入 README、提交消息或共享文档。

上述账号仅用于本次只读现象对照，不得产生写入操作。

## 工作区结构

- `project/`：可修改的业务源码和页面；它是独立 Git 仓库。
- `materials/`：受控私有 Git 中的运行和构建材料，例如部署包、JDK、原生库和索引。
- `private/`：受限配置、许可证、验收账号信息、数据副本和附件样本；只有材料清单列出的固定基线文件进入 Git。
- `scripts/`：材料校验、构建、隔离启动、数据恢复和验证入口。
- `evidence/`：构建、隔离、页面验收和已知限制的记录。
- `.scratch/lims-application-recovery/`：恢复规格与本地任务跟踪。

## 从 `project/` 构建和启动

`project/` 是嵌套的 Git/Maven 工程（父仓库中的 submodule），不是可以单独部署的目录。
所有构建、镜像和启动命令都必须从 `lims-recovery/` 根目录执行；不要在 `project/` 内直接运行
`mvn` 或 `java -jar`。父 POM、固定依赖、隔离配置、许可证、附件和搜索索引都由根目录脚本按受限
材料准备并挂载。

### 先决条件

- Linux `amd64`、可用的 Docker daemon，以及获准提供的 `materials/` 和 `private/` 材料。
- 本地已有以下基础镜像；脚本使用 `--pull=never`，不会自动联网下载：

  - `debian:bullseye-slim`（构建基础镜像和离线快照读取）
  - `gvenzl/oracle-xe:21-slim`（隔离 Oracle）
  - `redis:7.4-alpine`（隔离 Redis）
  - `lims-app:14.1.5-built`（下面由当前源码构建）

- `evidence/t01-manifest.sha256` 列出的 56 项固定材料，以及 `evidence/t02-build-plugin-seed.sha256` 列出的
  离线 Maven 插件种子，由受控私有 Git 提供；其他 `private/` 配置、口令、
  快照、附件和验收输入仍只通过受限渠道提供，不写入 README、提交消息或公开仓库。

首次检出时，若 `project/` 为空，使用经过批准的来源初始化 submodule，并记录两个仓库的提交号：

```bash
cd /path/to/lims-recovery
git submodule update --init --recursive
git -C project rev-parse --short HEAD
```

### 构建应用镜像

```bash
cd /path/to/lims-recovery
bash scripts/verify-materials.sh

# 仅在没有已审核的隔离配置，或明确需要重新生成时执行；输出仍属于 private/ 受限材料。
bash scripts/make-isolated-config.sh

# 准备 Oracle JDK 8u391、离线 Maven 仓库和固定版本依赖。
bash scripts/build-prep.sh
bash scripts/build.sh
sha256sum project/target/gikam-third-czjky-14.1.5-run.jar

docker image inspect debian:bullseye-slim gvenzl/oracle-xe:21-slim redis:7.4-alpine
docker build --pull=false -f docker/Dockerfile.built -t lims-app:14.1.5-built .
```

`docker/Dockerfile.built` 只把 JAR、JDK/原生运行材料放入镜像；配置、许可证、附件和索引仍由
启动脚本以受限挂载提供。

### 方式 A：已有隔离副本基础栈的日常启动

此方式用于已经有一个隔离副本的日常启动。`lims-oracle`、`lims-redis` 和应用必须只接入
`lims-iso`；这个网络必须是 `internal`。`app-ctl.sh` **只负责应用容器和本地转发，不会创建或启动
Oracle/Redis**。

如果本机还没有这两个基础容器，先在恢复工程根目录执行下面的命令一次。它只创建本机的隔离网络、
Oracle 数据卷、Oracle 容器和 Redis 容器；它不会复制生产基线，也不会连接生产环境。

```bash
cd /path/to/lims-recovery

# ORACLE_SYS_PASSWORD 由受限运行环境注入，不要写入 README 或提交历史。
: "${ORACLE_SYS_PASSWORD:?请先从受限材料注入 ORACLE_SYS_PASSWORD}"
REDIS_PW="$(grep -E '^spring\.redis\.url=redis://:[^@[:space:]]+@' private/application.properties \
  | tail -1 | sed -E 's#^spring\.redis\.url=redis://:([^@[:space:]]+)@.*#\1#' | tr -d '\r')"
test -n "$REDIS_PW"

docker network inspect lims-iso >/dev/null 2>&1 \
  || docker network create --internal lims-iso
test "$(docker network inspect lims-iso --format '{{.Internal}}')" = true
docker volume inspect lims-oracle-data >/dev/null 2>&1 \
  || docker volume create lims-oracle-data

# 仅当对应容器尚不存在时创建；已有容器不会被 docker run 覆盖。
if ! docker container inspect lims-oracle >/dev/null 2>&1; then
  docker run -d --name lims-oracle --pull=never \
    --network lims-iso --network-alias oracle \
    -e ORACLE_PASSWORD="$ORACLE_SYS_PASSWORD" \
    -e ORACLE_MEMORY=2048 \
    -v lims-oracle-data:/opt/oracle/oradata \
    gvenzl/oracle-xe:21-slim
fi
if ! docker container inspect lims-redis >/dev/null 2>&1; then
  docker run -d --name lims-redis --pull=never \
    --network lims-iso --network-alias redis \
    redis:7.4-alpine redis-server --requirepass "$REDIS_PW" --save ''
fi

docker ps --format '{{.Names}}\t{{.Status}}'
```

如果 `lims-oracle-data` 是刚创建的空卷，Oracle 只会初始化一个空的本地数据库，不会出现生产基线数据。
需要已有隔离副本数据时，先完成获准的数据复制，或改用下面的方式 B 从冷快照回放。Oracle 首次初始化
通常需要数分钟；用 `docker exec lims-oracle healthcheck.sh` 确认它已就绪。

如果容器已经存在但处于停止状态，使用 `docker start lims-oracle lims-redis`，不要再次执行同名的
`docker run`。Oracle 首次初始化完成后，再确认 `private/application-isolated.properties`、许可证、
`materials/index` 和附件样本均已准备好，然后启动应用：

```bash
docker network inspect lims-iso --format '{{.Internal}}'
docker ps --format '{{.Names}}\t{{.Status}}'

IMAGE=lims-app:14.1.5-built bash scripts/app-ctl.sh restart
bash scripts/app-ctl.sh status
# 浏览器访问：http://127.0.0.1:8081/
bash scripts/app-ctl.sh logs
```

启动日志应出现 `Started GikamWpjcApplication`。完成隔离检查后，使用与这三个容器对应的默认名称
生成报告：

```bash
bash scripts/isolation-check.sh evidence/<isolation-report>.txt
```

停止日常应用（不停止 Oracle/Redis）：

```bash
bash scripts/app-ctl.sh down
```

### 方式 B：从冷快照创建独立离线回放

这里的“新恢复卷”就是一个新的 Docker named volume；方式 B 会一次性消耗它。它的名字必须以
`lims-snapshot-` 开头，例如
`lims-snapshot-demo-a-20261019`。流程先从已验证的 Oracle 冷快照归档创建一个空卷，再把归档解压到该卷；
最后由 `stack-demo-restore.sh` 把这个卷挂载到新的回放实例。这个卷不是新的目录，也不是
`lims-oracle-data` 的别名；它不会修改日常隔离副本的数据卷。

`--create-empty-volume` 的含义是“创建一个尚未挂载且开始时为空的目标卷”，不是“恢复后保持为空”。
恢复完成后，该卷应包含 Oracle 数据目录和恢复标记。

```bash
cd /path/to/lims-recovery
SNAPSHOT_DIR=private/snapshots/current-snapshot
SNAPSHOT_VOLUME=lims-snapshot-demo-a-20261019
# 将 current-snapshot 替换为 private/snapshots/ 下实际通过校验的快照目录。

docker image inspect debian:bullseye-slim lims-app:14.1.5-built \
  gvenzl/oracle-xe:21-slim redis:7.4-alpine

bash scripts/oracle-snapshot.sh verify "$SNAPSHOT_DIR"
bash scripts/oracle-snapshot.sh restore "$SNAPSHOT_DIR" "$SNAPSHOT_VOLUME" \
  --create-empty-volume

bash scripts/instance-env.sh init demo-a 18081
# 通过受限渠道填写 private/instances/demo-a/runtime.env 的 8 个键，
# 并放入 mode 0600 的隔离配置、许可证和批准的 files/ 内容。
bash scripts/instance-env.sh validate demo-a
bash scripts/stack-demo-restore.sh demo-a "$SNAPSHOT_VOLUME"
# 浏览器访问：http://127.0.0.1:18081/
```

`stack-demo-restore.sh` 会创建带实例前缀的 internal 网络、Oracle、Redis、应用和转发器；它不会拉取
镜像、创建快照卷，也不会复用 `lims-oracle-data`。

```bash
# 可选：确认新卷由快照恢复脚本创建，且当前没有被容器挂载。
docker volume inspect "$SNAPSHOT_VOLUME" \
  --format 'name={{.Name}} driver={{.Driver}} snapshot={{index .Labels "lims.oracle-snapshot.format"}}'
docker ps -a --filter "volume=$SNAPSHOT_VOLUME" --format '{{.Names}}'
```

回放失败会保留现场和 snapshot lease；不要手工复用该卷或清除 lease，应从已验证归档创建新卷并使用
新的实例标识重试。回放没有通用的 `down` 命令，停止或清理前必须依据该实例的 manifest/标签逐项确认，
不要使用通配符删除容器或卷。针对回放实例执行隔离检查时，必须显式指定实例资源，避免误检查日常栈：

```bash
APP_CONTAINER=lims-replay-demo-a-app \
ORACLE_CONTAINER=lims-replay-demo-a-oracle \
REDIS_CONTAINER=lims-replay-demo-a-redis \
NET_ISO=lims-replay-demo-a-net \
CFG=private/instances/demo-a/application-isolated.properties \
bash scripts/isolation-check.sh private/instances/demo-a/isolation.txt
```

### 数据复制与日常启动的边界

`scripts/stack-up.sh` 不是普通启动命令。它只用于获准的数据复制阶段，默认 fail-closed；只有在明确
批准、只读复制窗口内才可显式设置 `COPY_MODE=production`、`ALLOW_PRODUCTION_COPY=1` 和受限的
`ORACLE_SYS_PASSWORD`。复制完成后必须立即运行 `bash scripts/stack-isolate.sh`，再按上面的方式 A
启动。生产环境始终只读，不能用 README 中的命令对生产部署、重启或写入。

更完整的材料清单、快照契约、隔离限制和验收步骤见
[交接手册](docs/recovery-handoff.md) 及
[恢复规格](.scratch/lims-application-recovery/spec.md)。

## 常用入口

```bash
bash scripts/verify-materials.sh
bash scripts/build-prep.sh
bash scripts/build.sh
bash scripts/app-ctl.sh {status|logs|down}
```

## 当前边界

首轮页面验收范围为人员、设备和耗材查询。该范围不代表全部业务流程、生产全量数据库或全部附件
已经恢复或与生产完全一致。实际恢复范围、验收依据和未完成项以 `evidence/` 与交接手册为准。

## 自有兼容框架重建

`owned-framework/` 提供已核对的兼容契约、身份和请求守卫；`owned-personnel/`、
`owned-equipment/`、`owned-consumables/` 将未修改的历史业务资源接到自有只读服务。
以下契约检查仍可单独运行：

```bash
bash scripts/tests/test-owned-framework.sh
bash scripts/tests/test-pagination-contract.sh
bash scripts/tests/test-personnel-domain-contract.sh
bash scripts/tests/test-page-row-bounds-contract.sh
bash scripts/tests/test-page-request-contract.sh
bash scripts/tests/test-personnel-page-controller.sh
bash scripts/tests/test-login-user-contract.sh
bash scripts/tests/test-filter-contract.sh
bash scripts/tests/test-object-utils-contract.sh
bash scripts/tests/test-mapper-value-contract.sh
bash scripts/tests/test-converter-leaves.sh
bash scripts/tests/test-convert-utils.sh
bash scripts/tests/test-array-collection-utils.sh
bash scripts/tests/test-date-time-utils.sh
bash scripts/tests/test-bootstrap-cache-contract.sh
bash scripts/tests/test-sunway-application-selection.sh
bash scripts/tests/test-sunway-application-run-contract.sh
```

真实 Oracle/PostgreSQL 合成 schema 探针与镜像来源见 [数据库探针说明](scripts/db-slice/README.md) 和 [结果边界](evidence/t15-database-mainline.md)。合成 SQL 结果只证明声明范围；随后实际 PostgreSQL 应用止损记录了登录 `NVL` 与报表 `TO_CHAR` 不兼容，已选择 Oracle 单一主线，完整应用兼容性仍未验收。

人员原始 Oracle Mapper 的六场景 BoundSql 与隔离执行记录见 [personnel-mapper-reference.md](evidence/personnel-mapper-reference.md)。其中详情 SQL 对组织过滤参数没有追加限制；该结果是参照行为，不是权限验收。

现有 `scripts/build.sh` 和 `docker/Dockerfile.built` 仍构建供应商运行时，属于行为参照和迁移输入。
自有运行时已有显式身份自动配置、RSA 登录/退出、文件审计及三类查询的当前会话/菜单/组织范围检查。Oracle 合成身份与菜单回归记录见 [身份/菜单 Oracle 报告](.scratch/sunway-supplier-exit-api-first/archive/historical/owned-menu-oracle-integration-20261020.json)。
`GET /secure/core/module/sys/menus` 提供当前会话获授权的菜单 ID；`POST /secure/core/module/sys/menus/tree/root` 已提供当前授权的名称、层级、排序和可用元数据树。菜单管理写入与完整权限矩阵仍未完成。
原始人员、设备、耗材资源/服务/DAO 接线分别通过实际 `GikamWpjcApplication` 和隔离合成 Oracle
的 62、61、72 项 HTTP 检查；人员、设备和耗材的只读浏览器回归也已分别绑定到干净源码和
合成数据。它们只覆盖声明的只读范围，不代表完整业务流程。

`owned-web/` 的人员只读页面已通过可重复的 22 项浏览器检查，覆盖 RSA 登录、23 行列表、
筛选、无匹配、分页、详情、低权限拒绝和退出。源码与产物哈希、隔离检查和清理结果见
[人员浏览器报告](.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-browser-integration-20261020.json)。运行：

```bash
python3 owned-web/src/test/browser/run-browser.py \
  --output build/personnel-browser-local.json
```

该命令需本机已有报告中固定 digest 的 Oracle 镜像、Docker、Java 8 和 `agent-browser`；
它创建并删除独立合成数据库，不使用生产数据。人员写入和关联标签、设备/耗材写入及完整应用
部署与全量业务仍待完成。

`owned-attachments/` 已提供显式启用的附件只读路径和 ERP 文件隔离回放。附件关联、
授权撤销、缺失文件及安全路径通过 43 项真实 HTTP 检查；ERP 回放通过 18 项本机检查。
附件页面已通过 23 项真实浏览器检查：授权用户点击原始下载列表中的真实文件名得到准确字节，
缺失、撤权、无授权和退出后的内容访问均按预期拒绝；原始脚本哈希保持不变。ERP 文件回放仍为
显式 loopback 隔离边界，未声称全部外部集成等价。见
[附件浏览器报告](.scratch/sunway-supplier-exit-api-first/archive/historical/attachment-browser-integration-20261020.json)和
[附件和回放边界](evidence/attachment-replay-runtime-20261020.md)。

设备/耗材的 55 项组合浏览器检查及人员 22 项回归见
[库存浏览器报告](.scratch/sunway-supplier-exit-api-first/archive/historical/inventory-browser-clean-20261020.json)。报表的本地回放页面 25 项检查与
调度测试入口 39 项检查分别见 [报表边界](evidence/report-adapter-boundary-20261020.md) 和
[调度说明](owned-scheduler/README.md)；第三方渲染器与历史采集任务等价仍未证明。

独立分发包使用 `scripts/build-owned-distribution.sh` 构建，外置配置后由
`owned-distribution/target/distribution/start.sh` 启动真实主类。双次字节一致重建、供应商残留
扫描和集成附件前端后的人员分发浏览器 19 项记录见 [分发证据](evidence/owned-distribution-package-20261020.md)
及 [集成后浏览器报告](.scratch/sunway-supplier-exit-api-first/archive/historical/owned-distribution-browser-attachment-integration-20261020.json)。
完整进度与未完成项见
[实施状态](evidence/rebuild-implementation-status.md)。

`tools/vertical_slices/` 提供隔离 fixture 的身份、查询、附件、回放、报表和任务契约测试。它明确标为 `unavailable-runtime`，不会冒充真实页面或数据库验收：

```bash
bash scripts/tests/test-vertical-slices-08-14.sh
```


## Standalone root and external inputs

This repository is the deployable `lims-recovery` root. `project/` is ordinary
source materialized from the project commit recorded in
`docs/standalone-root-migration.json`; it is not a nested submodule.

Build and deployment require an externally supplied `LIMS_EXTERNAL_ARTIFACT_ROOT`
containing only the artifacts listed in `evidence/external-artifacts.sha256`.
Private configuration, credentials, Oracle/Redis data, attachments, supplier
JARs and native/license inputs are injected through an approved restricted
channel. They are not part of this Git tree or the owned distribution.

Run `bash scripts/verify-external-artifacts.sh` before any source build.
