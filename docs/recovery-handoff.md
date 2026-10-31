# LIMS 恢复工程交接手册

本手册描述如何在隔离环境重建、启动和验证恢复工程。它不授权对生产写入、重启、部署或执行
业务写入。生产仅可作为经过批准的只读材料来源。

## 交接边界

- 源码根目录：本仓库根目录；应用源码是嵌套仓库 `project/`，必须记录其独立提交号。
- 私有框架、原生库和许可证按 [ADR-0001](adr/0001-retain-private-framework.md) 保持固定依赖；
  不尝试重写、篡改或绕过许可证。
- `evidence/t01-manifest.sha256` 列出的 56 项 `materials/` 和固定 `private/` 材料，以及
  `evidence/t02-build-plugin-seed.sha256` 列出的离线 Maven 插件种子，进入受控私有 Git，以便另一台设备
  直接检出后完成材料校验和离线构建。其他 `private/` 输入、构建缓存、数据库卷、快照、附件和验收
  账号信息不进入 Git，必须通过受限渠道取得。
- 首轮验收是人员、设备和耗材的查询行为；它不代表全部业务流程或生产全量数据、附件的一致性。

## 受限输入清单

在开始前确认受限材料已由授权渠道交付到工作目录，且不上传到公开位置：

- `materials/` 以及材料清单中的 `private/` 文件：由受控私有 Git 提供，先运行校验。
- `private/application.properties`：已纳入材料清单；只能用于本地生成隔离副本，不能写入文档。
- `private/lic/`：已纳入材料清单；`private/license-machine.env` 等其他机器标识材料仍走受限渠道。
- `private/test-account.env`：隔离验收账号口令。
- 数据副本或批准的只读复制通道，以及 `private/t01-demo-rows.txt` 数据基准。
- 如需任务 06 样本：`private/t06-attachment-sample/` 和
  `private/t06-attachment-sample.env`。

先运行：

```bash
bash scripts/verify-materials.sh
```

该命令必须全部通过；中断复制、缺失文件或校验不一致时不得继续启动应用。

若已有任务 06 的已校验文件样本，但缺少其元数据清单，可在获准的生产只读连接可用时执行
`bash scripts/collect-attachment-sample.sh`。该入口需要 Java 11+ 和已解包 JDK 8 中的 `jar`
工具；Oracle 驱动来自已校验部署包。它只查询既有样本记录及人员签名链接，校验本地样本大小与
固定 SHA-256，并生成 0600 的 `private/t06-attachment-sample.env`；已有清单时拒绝覆盖。
它不复制数据库、不恢复隔离关联，也不代替附件页面验收。

## 构建恢复工程

1. 检出根仓库和 `project/` 的记录提交。不要只依赖嵌套仓库的私有远程；交接记录必须同时给出
   根提交、嵌套提交和受控源码包/访问路径。
2. 检查 JDK 8u391、Maven 3.9.x 和 Docker 可用。
3. 生成隔离配置：

   ```bash
   bash scripts/make-isolated-config.sh
   ```

   该配置必须指向 `oracle` 和 `redis`，停用 `schedule`、`equipt-acq-file`、`ngtodo`，并将外部
   端点改为容器内回环地址。
4. 重建私有依赖仓库和 parent：

   ```bash
   bash scripts/build-prep.sh
   ```

5. 从源码构建：

   ```bash
   bash scripts/build.sh
   sha256sum project/target/gikam-third-czjky-14.1.5-run.jar
   ```

6. 执行验证工具：

   ```bash
   bash scripts/tests/test-class-diff.sh
   bash scripts/tests/test-source-inventory.sh
   bash scripts/tests/test-verify-materials.sh
   ```

记录嵌套源码提交、产物 SHA-256 和每个命令的结果。

## 隔离运行

1. 初次准备基础服务时运行 `bash scripts/stack-up.sh`。该命令会临时给 Oracle 接入复制网络，
   因此只可在批准的数据复制阶段使用。
2. 如需复制数据，仅使用获准的只读路径。演示数据复制入口为 `scripts/db-copy-demo.sh`；完整
   复制和逐表核对入口为 `scripts/db-copy.sh`。记录采集时间、范围、跳过表和一致性限制。
3. 复制完成后立即切断复制网络：

   ```bash
   bash scripts/stack-isolate.sh
   ```

4. 如需已验证的人员签名样本，执行：

   ```bash
   bash scripts/restore-attachment-sample.sh
   ```

   它只向隔离 Oracle 写入受限样本的元数据关联，并先校验文件 SHA-256。
5. 构建镜像并启动：

   ```bash
   docker build -f docker/Dockerfile -t lims-app:14.1.5 .
   docker build -f docker/Dockerfile.built -t lims-app:14.1.5-built .
   IMAGE=lims-app:14.1.5-built bash scripts/app-ctl.sh restart
   bash scripts/app-ctl.sh status
   ```

6. 等待容器 `logs/app.log` 出现 `Started GikamWpjcApplication`；比较宿主与容器 JAR
   SHA-256；执行：

   ```bash
   bash scripts/isolation-check.sh evidence/<new-isolation-report>.txt
   ```

   该检查必须 PASS，证明应用、Oracle、Redis 仅在 `lims-iso`，且生产/外部端点不可达。

### 可选的本地 Oracle 冷快照

`scripts/oracle-snapshot.sh` 只处理本机隔离 Oracle 的 Docker 卷 `lims-oracle-data`，不读取生产
配置、不访问生产，也不会启动或停止现有的应用、Oracle 或 Redis 服务容器（仅可能运行短时、无网络的
本地归档容器）。它是保留当前演示数据基准的可选手段，不替代数据复制、完整附件交付或独立重建验收。

导出前，在仍只接入 `lims-iso` 的本地 Oracle 中做一次**只读元数据**检查并保留结果；它不会连接任何
database link 或生产端点：

```bash
docker exec -i lims-oracle bash -c \
  'export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s / as sysdba' <<'SQL'
set pages 100 lines 240
select con_id, owner, db_link from cdb_db_links order by con_id, owner, db_link;
exit
SQL
```

结果必须没有数据库链接；尤其不能保留复制阶段创建的 `PRODLINK`。有任何结果时，不得导出，也不得把
快照作为交接材料；应在单独批准的本地隔离操作中清理后重新检查。随后操作者必须按批准的维护步骤让
`lims-oracle` 正常停止。脚本会拒绝运行中的容器、非零退出或 OOM 退出的容器、非 `lims-iso` 内部网络
或非预期卷，并要求 `--database-links-removed` 作为操作者声明。该声明不是查询证据的替代品。导出通过
只读、无网络的临时容器读取卷，生成受限归档和 SHA-256 清单到 `private/snapshots/`：

```bash
bash scripts/oracle-snapshot.sh export --after-clean-stop --database-links-removed <snapshot-name>
bash scripts/oracle-snapshot.sh verify private/snapshots/<snapshot-name>
```

归档含 Oracle 数据文件和口令文件，应按受限数据处理。恢复也先校验哈希，且只会新建未挂载的
`lims-snapshot-*` 卷；它永不覆盖 `lims-oracle-data`，也不会启动 Oracle：

```bash
bash scripts/oracle-snapshot.sh restore private/snapshots/<snapshot-name> \
  lims-snapshot-<name> --create-empty-volume
```

恢复后的卷必须在单独、经批准的隔离实例中启动并完成数据、附件、登录、页面和网络边界复验后，才能
作为任何交接证据。2026-10-12 已实际完成冷快照导出、校验、新卷恢复和同机回放启动，见
[回放记录](../evidence/t07-ac10-replay-20261012.md)；正式 AC-10 仍待独立交接复验。
快照不包含应用镜像、`private/` 配置、Redis 状态、`files/` 附件或生产数据采集过程。

### 独立离线回放实例

`scripts/stack-demo-restore.sh` 是一个单次、拒绝覆盖的离线回放启动入口。它不调用
`stack-up.sh`，不创建或连接 `lims-copy`，不读取生产配置、不执行 SSH、不拉取镜像，也不停止、删除、
重启或改写现有 `lims-*` 容器、网络或卷。它只会创建带
`com.sunway.lims.offline-replay.*` 标签的 `lims-replay-<instance>-*` 容器和 internal 网络；页面通过
一个仅绑定 `127.0.0.1:<port>` 的转发器访问。任何已存在的目标名称、实例标签、端口、清单、转发器
记录或已挂载快照卷都会使它在创建资源前失败。

此入口的精确前提如下：

1. 在接收者自己的工作目录中取得本仓库、受限材料和已构建的**本地**应用镜像。应用镜像必须由
   `application-isolated.properties` 构建，仍使用内部别名 `oracle` 和 `redis`，且其中的外部端点已按
   本手册改写。三张镜像必须已经在本机，脚本以 `--pull=never` 运行：

   ```bash
   docker image inspect lims-app:14.1.5-built gvenzl/oracle-xe:21-slim redis:7.4-alpine
   ```

2. 先在受限材料中校验冷快照，再用 `oracle-snapshot.sh restore` 创建一个新的、未挂载的
   `lims-snapshot-*` 卷。回放入口不接受归档目录，也不会替调用者创建或恢复卷；绝不能传入
   `lims-oracle-data`。该卷将被新 Oracle 以可写方式挂载，因此一次启动即消耗该副本，不能把它当作
   未使用的快照再次复用。

   ```bash
   bash scripts/oracle-snapshot.sh verify private/snapshots/<snapshot-name>
   bash scripts/oracle-snapshot.sh restore private/snapshots/<snapshot-name> \
     lims-snapshot-<new-name> --create-empty-volume
   ```

3. 为一个从未使用过的小写实例标识和一个空闲的本机高端口创建受限运行输入。`init` 只写入一个
   mode `0600` 的模板；经批准的操作者必须填入它允许的五个键，不能把
   `application.properties`、任意 shell 语句或其他键放进去。还必须把只含本地 `oracle`/`redis`
   地址的隔离配置放到同一目录的 `application-isolated.properties`（mode `0600`）。
   `REDIS_PASSWORD` 必须与该配置一致；`MAC_ADDRESS` 必须是许可证材料所记录的值；两个口令都只能
   通过受限渠道提供。

   ```bash
   bash scripts/instance-env.sh init handoff-a 18081
   cp <approved-local-only-config> private/instances/handoff-a/application-isolated.properties
   chmod 600 private/instances/handoff-a/application-isolated.properties
   # Fill private/instances/handoff-a/runtime.env through the restricted channel.
   bash scripts/instance-env.sh validate handoff-a
   ```

   模板严格限定为：

   ```text
   IMAGE_REF=lims-app:14.1.5-built
   ORACLE_SYS_PASSWORD=<isolated Oracle password>
   REDIS_PASSWORD=<matches isolated application config>
   MAC_ADDRESS=<licensed container MAC address>
   HOST_PORT=18081
   ```

4. 启动新的实例。启动前它检查快照卷的 `oracle-snapshot.sh restore` 标签、资源名称、标签、端口、
   运行输入权限和本地镜像；启动后它检查每个新容器仅连接自己的 internal 网络、没有发布端口或额外
   主机映射，并在启动应用前对新 Oracle 执行本地、只读的 `cdb_db_links` 计数检查。它把不含口令的
   镜像标识、Git 修订/脏状态、脚本哈希、Docker 版本、资源 ID、卷名和恢复 nonce 记录到受限目录的
   `replay-manifest.env`。

   ```bash
   bash scripts/stack-demo-restore.sh handoff-a lims-snapshot-<new-name>
   ```

   页面地址为 `http://127.0.0.1:18081/`。若启动在任意一步失败，脚本不会删除或复用已创建的新资源；
   需先保留并检查该实例目录、标签和容器日志，随后使用一个新的实例标识和新的恢复卷重试。

   回放实例的隔离复验须显式传入它自己的资源名；不要让默认检查器误读现有 `lims-*` 实例：

   ```bash
   APP_CONTAINER=lims-replay-handoff-a-app \
   ORACLE_CONTAINER=lims-replay-handoff-a-oracle \
   REDIS_CONTAINER=lims-replay-handoff-a-redis \
   NET_ISO=lims-replay-handoff-a-net \
   CFG=private/instances/handoff-a/application-isolated.properties \
   bash scripts/isolation-check.sh private/instances/handoff-a/isolation.txt
   ```

   该命令仍使用受限端点清单和隔离配置模板；实际运行前应为回放实例准备对应的本地配置路径，
   并把报告、JAR 校验、启动日志和浏览器结果写入同一实例目录。

   2026-09-28 已在同一 Docker daemon 做过一次受限回放试验，结果和边界见
   [evidence/t07-offline-replay-c3f4.md](../evidence/t07-offline-replay-c3f4.md)；该试验明确不替代
   另一份独立工作目录/主机的 AC-10 验收。

2026-10-12 已在同一工作目录和 Docker daemon 使用新恢复卷实际运行该回放入口；启动、隔离、登录、人员
页面和耗材页面结果见 [evidence/t07-ac10-replay-20261012.md](../evidence/t07-ac10-replay-20261012.md)。
这证明了同机独立回放路径，但不等同于正式 AC-10：另一名开发者仍必须在独立工作目录或主机取得受控材料，
完成上述启动，并重新记录启动日志、宿主/容器 JAR 标识、隔离边界、登录、权限、三类查询和附件样本的页面结果。

## 页面验收

使用 `http://127.0.0.1:8081/`，用受限验收账号进行真实页面操作。每次记录数据基准、账号角色、
源码/产物 SHA、步骤、预期、实际、截图和差异。

- `00550`：人员、仪器和耗材查询授权入口；验证列表、匹配筛选、无匹配、适用分页和详情。
- `05304`：低权限对照账号；验证没有相关菜单，且直接页面、详情、原始列表和相关查询端点不返回
  业务数据。
- 附件样本：人员 `00550` 的签名 URL 需由框架带 `security`/`token` 参数生成并返回文件；裸 URL
  不应返回文件内容。

详见 `evidence/t03-personnel-query.md`、`evidence/t04-equipment-query.md`、
`evidence/t05-consumables-query.md`、`evidence/t06-data-and-attachments.md`。

## 二次开发与回退

选择一个可见且不影响数据/查询契约的小改动，修改源码而非运行产物，依次重新构建、重建镜像、
重启隔离容器、复验隔离边界和真实页面。记录变更前后提交与 JAR SHA。回退应是明确的源码提交
回退后同样的构建/部署流程，绝不可用生产覆盖来回退。

## 已知限制

- 本地 Oracle 是 XE 21c，生产是 Oracle 19c EE。
- 当前数据副本是演示范围，不是生产全量；部分大表、完整附件目录和数据库链路内容未恢复。
- 任务 06 当前仅验证一个受限附件样本，不代表完整附件归档。
- `stack-up.sh` 只能用于复制阶段，例行重启应使用 `app-ctl.sh`，避免意外重新接入 `lims-copy`。
- 许可证依赖已记录的容器网卡标识；不得通过关闭认证或改写原生库绕过。
- 正式交接验收仍要求另一名开发者在独立目录或主机使用受控材料包完成本手册的重建，并记录结果；当前同机回放
  证据不能替代该复验。
