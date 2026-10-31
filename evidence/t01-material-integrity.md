# 任务 01 材料与版本基线记录

日期：2026-09-23。来源：`ssh sss` 生产部署目录 `/data/LIMS/jiankeyuan/lims-czjky-emc`（JDK 位于 `/usr/java`）。
生产侧仅使用 ls、stat、sha256sum、cat、rsync 拉取等只读操作；未修改、未删除、未在生产执行任何程序，未重启生产应用。

## 1. 运行材料完整性（AC-01）

首次采集曾中断。核对结果与处置：

| 材料 | 生产大小 | 首次采集 | 核对结论 | 处置 |
| --- | --- | --- | --- | --- |
| gikam-third-czjky-14.1.5-run.jar | 546,766,277 | 420,315,136 | 截断（本地内容与生产前 420,315,136 字节哈希一致，确认为同一文件的完整前缀） | 已重新完整取得 |
| jdk-8u391-linux-x64.tar.gz | 141,887,242 | 123,011,072 | 截断（gzip 自检失败） | 已重新完整取得 |
| lic/posetup_6.4.1.1.exe | 104,739,944 | 85,393,408 | 截断 | 已重新完整取得 |
| application.properties | 11,917 | 11,917 | 完整且与生产一致 | 无需处置 |
| 01startLims.sh / lic/license.lic / lic/vendor.lic / lic/poserver_log.txt | — | — | 完整且与生产一致 | 无需处置 |
| sunway-app.so、simsun.ttc、log4j2.xml、02/03/04/05/moveto164 脚本 | — | 未采集 | 缺失 | 已采集 |

全部 16 项材料的 sha256 见 `evidence/t01-manifest.sha256`，校验命令 `scripts/verify-materials.sh` 输出为「全部 16 项材料校验通过」。
采集命令固化在 `scripts/fetch-materials.sh`（对生产仅 rsync 拉取）。

版本标识：应用包为 `gikam-third-czjky-14.1.5-run.jar`（Spring Boot 2.3.4.RELEASE，构建时间 2026-05-28）；运行时 Oracle JDK 8u391（`java -version` 实测）；私有原生库 `sunway-app.so`（186,512 字节）随 jar 另有内置版本（`third-party/sunway-app/linux/`）。

受限材料（凭据、许可证、业务数据）保存在 `private/`，不在本文档展开内容。

## 2. 数据库基线（AC-01、AC-04 前置）

只读探查记录：`private/t01-db-inventory.txt`（探查工具 `scripts/db-probe.sh`，仅执行 SELECT）。

- Oracle Database 19c Enterprise Edition 19.0.0.0.0（19.3），生产配置指向生产 Oracle 服务（连接串属受限材料，见 `private/application.properties`）。
- 业务 schema：783 表、2161 索引、883 LOB 段、10 函数、3 存储过程、1 序列（SEQ_CORE）、1 类型、2 个数据库链路。
- 段空间合计约 34.6 GB：表 20.2 GB、索引 11.4 GB、LOB 2.9 GB。
- 大表集中在订单与检测结果类（T_EMC_ORDER_ITEM_GROUP 6.1 GB、T_EMC_ORDER_TASK_RESULT 4.2 GB、T_EMC_REPORT_RESULT 1.3 GB 等）；登录、人事、设备、耗材相关表均在其下。
- schema 内存在 2 个数据库链路，属潜在外部写入通道，明细记录在受限材料，隔离副本导入时排除（`EXCLUDE=DB_LINK`）。

## 3. 复制范围与依据

用户 2026-09-23 决定：**只需项目可演示运行，无需完整项目数据**。依据：expdp 数据路径与 SQL 直拉在数据链路上实测约 68KB/s，全量数据需 6 小时以上；演示目标不依赖订单、检测结果等大表数据。

本次复制：

- 结构：全部对象（表、索引、约束、LOB、触发器、函数、存储过程、类型、序列），排除数据库链路；表 `QYJWDDR` 除外（Data Pump 导入在该表 DDL 上崩溃 ORA-39126/ORA-01403，属未恢复对象，与人事/设备/耗材及登录无关）。
- 数据（演示范围）：
  - 权限、菜单、组织、字典、元数据类表**全量**（保持「用户-角色-菜单-权限」链路完整，否则演示账号无法登录或看不到入口）；
  - 其余表每表最多 100 行（`scripts/db-copy-demo.sh` 的 `DEMO_ROWS`）；
  - 分配尺寸 ≥300MB 的表整表跳过：`T_EMC_ORDER_ITEM_GROUP`、`T_EMC_ORDER_TASK_RESULT`、`T_EMC_REPORT_RESULT`、`T_CORE_AT_RECORD_LINE`、`T_CORE_FILE`、`T_EMC_ORDER_TASK_LOG`、`T_EMC_ORDER_TASK`、`T_CORE_LOG`、`T_EMC_EQUIPT_ACQ_FILE`、`T_EMC_ORDER_CONTAINER`、`T_EMC_QC_RELATED_ORDER`、`T_EMC_RUN_TEMP_HUMIDITY`。
  - 其中 `T_CORE_FILE`（文件索引）与 `T_EMC_EQUIPT_ACQ_FILE`（设备采集文件）属附件范围，留待附件任务处理。
- 数据通道：database link 上的 SQL 直拉（`insert into t select * from t@PRODLINK`），替代 expdp 数据导出（该路径经外部表逐行取数，实测约 68KB/s）。注意：直接路径加载 `/*+ append */` 与远程表源互斥（ORA-12840），必须用普通 insert。
- 链路吞吐实测（决定了限量范围的必要性）：`T_EMC_ITEM` 168MB / 96,958 行经普通 SQL 直拉耗时 38 分 10 秒（约 75KB/s），与 expdp 路径的 68KB/s 一致——瓶颈在到生产库的链路本身，与取数方式无关；全量数据复制在该链路下需 6 小时以上，超出演示所需。
- 已知偏差：限量数据不满足引用完整性，填充前禁用外键与触发器；演示数据基准（每表实际行数）记录在 `private/t01-demo-rows.txt`。

用户已授权完整复制数据库与附件；本次按演示必需范围执行，未复制部分如上明确记录，不得表述为「已完成全量复制」。

## 4. 隔离配置差异（AC-03 前置）

隔离配置由 `scripts/make-isolated-config.sh` 从生产配置生成（`private/application-isolated.properties`），差异仅限：

| 项 | 生产 | 隔离副本 | 目的 |
| --- | --- | --- | --- |
| spring.profiles.active | 含 schedule、equipt-acq-file、ngtodo | 移除这三项 | 停用定时任务与设备文件采集 |
| spring.datasource.url | 生产 Oracle 连接串（受限材料） | oracle:1521/XEPDB1 | 指向隔离副本数据库 |
| spring.redis.url | localhost:6379 | redis:6379 | 指向隔离副本 Redis |
| 文件服务、帆软、OA、NC、客商、邮件、FTP 等端点 | 生产与外部地址 | 127.0.0.1（容器内回环） | 无法触达真实外部系统 |
| JVM 参数 | -Xms5G -Xmx40G（private/01startLims.sh） | -Xms1g -Xmx2g | 适配本机内存 |

其余配置与生产逐字一致。凭据、许可证内容不在本文档记录。

## 5. 网络边界（AC-03 前置）

- 应用、Oracle、Redis 全部运行在 docker internal 网络 `lims-iso`，无出网路由。
- 数据复制阶段 Oracle 临时挂接 `lims-copy` 网络，复制完成后由 `scripts/stack-isolate.sh` 断开。
- 可重复验证：`scripts/isolation-check.sh`，覆盖静态配置扫描、对 10 个生产/外部端点的运行时连通性检查（必须全部失败）与隔离网内服务的正向对照（必须成功）。

## 6. 尚未验证 / 未解决项（截至本文档）

- 私有框架、原生库与许可证在本机的实际启动可用性：待首次启动验证。
- 许可证是否机器绑定、是否需要联网校验：待验证；不以绕过许可证作为手段。
- Oracle 本地实例为 XE 21c，生产为 19c EE：版本与规格差异属已记录偏差。
- 表 `QYJWDDR` 结构未恢复（Data Pump 缺陷）；其数据亦未复制。
- 未复制的大表数据、附件文件、以及生产 schema 内数据库链路指向的外部库内容：未采集。
