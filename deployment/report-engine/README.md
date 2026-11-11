# 首页全部 27 份报表的本机依赖

该页面使用当前生产版本的原 CPT 和 FineReport 11。引擎是独立第三方产品，
不属于 Sunway native/license 替换范围。部署不包含生产连接、FineDB、授权文件或业务行。

使用安装包中的 `scripts/install-report-engine.py --help`，从运维已授权的
Fine11 library 与公共依赖目录准备一个新的绝对路径。脚本离线校验原引擎 hash，
编译自有 session listener，复制 manifest 中全部 27 份 CPT，将命名连接绑定到私密配置中的隔离 schema。
它不连接数据库、不改变第三方许可、不读取生产配置。

在私密 application properties 设置：

```properties
owned.report.engine.enabled=true
owned.report.engine.root=/var/lib/lims-owned/report-engine
sunway.config.fine.version=11.0
sunway.config.fine.deploy-mode=independent
sunway.config.fine.independent-deploy-server.url=/report-engine
sunway.config.fine.independent-deploy-path=/var/lib/lims-owned/report-engine
```

这里的 independent 是原前端 URL helper 的配置名；实际部署位于同一 Spring
application JVM 的隔离 Servlet context，使用同一端口，不启动独立进程或 HTTP bridge。
数据源使用本应用的隔离 Oracle 配置，通过 Fine 正常公开配置事务注册。

现有核心 bootstrap 已含统计菜单。新设备还必须先以应用隔离 schema 的账号执行
stager 输出的 `install-config.sql`：它安装生产来源绑定的报表主配置1行、27条行配置，
保留原参数/启用/排序语义，不复制来源制单人、用户、组织或权限。已有记录必须逐字段一致，
冲突时在任何新增前失败；重复安装不改已有行。`config-provenance.json` 记录来源与逐行 hash。
这28行是本包专属配置补充，不解除全系统 installer 的 partial 阻断。

安装脚本为两份模板分别生成 `install-metadata.sql` 与 `install-contract-metadata.sql`，
已按绑定 CPT 的实际大小解析占位符，用于补齐现有 homePageReport/kf_htrwtjb 和
homePageReport/kf_htxxb 行的文件关联。脚本只接受原配置行和精确附件标识，遇到冲突失败，
不覆盖业务数据。合同模板还依赖 `T_CRM_CONTRACT_PACKPRICE` 的完整逻辑结构，其生产字典
来源与窄迁移位于 `dependencies/external-contract-packprice-20261111.json` 和同名 SQL；
先以应用隔离 schema 的账号执行 stager 输出的 `dependencies/external-contract-packprice-20261111.sql`，
再执行两个 metadata SQL。依赖迁移只创建已核对的空结构并拒绝冲突；Oracle DDL 隐式提交，
不是业务数据迁移或事务 rollback。结构未准备时不能用空结果代替报表查询。新设备使用上面的固定配置补充恢复对应原配置，不创建替代页面。

metadata SQL 只登记附件，不复制文件内容。stager 同时生成
`attachment-files/finereport/<报表代码>/<原文件名>.cpt`，其内容与同批
`WEB-INF/reportlets/` 模板完全一致；`staging-manifest.json` 的 `attachment_files`
记录有限路径、大小及 SHA-256。原 `CoreFileUtils` 对 finereport 使用 `EXT/NAME`
构造下载路径，因此附件目录不使用 `MD5NAME`，也不使用日期目录。

执行全部 metadata SQL 后、启动应用前，使用应用文件卷的操作系统账号安装附件。
`REPORT_STAGE` 是本次 stager 输出目录，`APP_FILES` 是应用实际 working directory 下的
`files` 目录（例如 `/var/lib/lims-owned/files`），必须已经存在且为绝对路径：

```bash
REPORT_STAGE=/var/lib/lims-owned/report-engine
APP_FILES=/var/lib/lims-owned/files
python3 installation/scripts/install-report-engine.py \
  --install-attachments-from "$REPORT_STAGE" \
  --files-root "$APP_FILES" \
  --application-config /run/config/local-application.properties
```

该模式用同一私密 application properties 读取目标 schema，重新核对原模板的有限变换，
不复制或记录配置、不连接数据库，只接受当前发行 manifest 声明的全部 27 份附件。它先校验整套模板/附件 hash 和全部已有目标，再复制缺失文件；内容相同
时可重复执行，任一已有文件不同或路径含符号链接即失败，不覆盖旧文件。冲突须先核对版本、
备份并明确迁移，不用覆盖参数跳过。安装后通过原文件 API 查询并下载对应 CPT，不能用
metadata 存在或引擎模板可读代替附件恢复验收。

`/report-engine` 的所有请求先验证真实门户登录和目标菜单。原有两份汇总表保持对应菜单授权；其他模板按当前门户身份、选中角色、报表权限和配置启用状态授权，仅开放已安装且获授权的模板与所需资源；
请求的 userid 绑定当前门户用户编号，拒绝其他用户参数。原用户契约允许用户编号为空；
仅委托汇总表和合同汇总表的有效 SQL 不依赖 userid，因此这两份原模板允许已授权门户用户的
用户编号为空，不回退使用用户 ID，也不补造编号。其他 25 份模板仍要求非空用户编号；
真实登录、配置启用状态、菜单/报表授权及会话归属校验保持生效。
报表会话通过服务器生成的一次性关联参数绑定门户 token 摘要、用户、组织、角色和模板；
关联在原引擎的异步创建事件中消费，后续不能认领
未知或他人的会话。管理、配置、登录、其它模板和未验证导出动作不开放。

本目录覆盖当前 production 的全部 27 份 homePageReport 模板及其内部模板链接；不包含生产 reportlets 中未被这些配置引用的其他业务模板，也不能作为全部报表/导出或 API strict 闭包完成依据。

合法的第三方报表授权须由部署方按 FineReport 正常安装流程放在私有运行目录；
不属于 distribution 内容，也不能与应用源码一起提交。复制旧部署的原文件不保证
新机器适用，必须检查引擎的正常授权状态与注册提示，不能用偶发 trial 查询成功
判定可用。当前修复保留第三方原始校验，不修改机器标识、期限或并发限制。

安装全部模板时，在 `install-config.sql` 成功后执行 stager 输出的
`staging-manifest.json` 中 **全部** `metadata_sql_files`，不要只执行两个旧汇总表 metadata SQL。
`metadata/` 下的 25 份补充 SQL 只接受对应原配置和来源绑定文件标识；冲突失败，重复执行安全。
模板内命名数据源统一指向隔离 Oracle；三个模板的 DB-link qualifier 仅按逐对象 allowlist
转换为已准备的 local-view。保留原查询、公式、布局和跳转目标；不复制来源用户默认值、
生产连接、FineDB 或第三方授权文件。源 hash 与变换记录在 manifest 的逐模板 provenance 中。
新设备仍需另行准备真实身份/组织/角色及其报表授权；本安装不复制生产授权或自动授予全部模板。
