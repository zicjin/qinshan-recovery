# 委托汇总与合同汇总的本机报表依赖

该页面使用当前生产版本的原 CPT 和 FineReport 11。引擎是独立第三方产品，
不属于 Sunway native/license 替换范围。部署不包含生产连接、FineDB、授权文件或业务行。

使用安装包中的 `scripts/install-report-engine.py --help`，从运维已授权的
Fine11 library 与公共依赖目录准备一个新的绝对路径。脚本离线校验原引擎 hash，
编译自有 session listener，复制两份 CPT，将命名连接绑定到私密配置中的隔离 schema。
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

安装脚本为两份模板分别生成 `install-metadata.sql` 与 `install-contract-metadata.sql`，
已按绑定 CPT 的实际大小解析占位符，用于补齐现有 homePageReport/kf_htrwtjb 和
homePageReport/kf_htxxb 行的文件关联。脚本只接受原配置行和精确附件标识，遇到冲突失败，
不覆盖业务数据。合同模板还依赖 `T_CRM_CONTRACT_PACKPRICE` 的完整逻辑结构，其生产字典
来源与窄迁移位于 `dependencies/external-contract-packprice-20261111.json` 和同名 SQL；
先以应用隔离 schema 的账号执行 stager 输出的 `dependencies/external-contract-packprice-20261111.sql`，
再执行两个 metadata SQL。依赖迁移只创建已核对的空结构并拒绝冲突；Oracle DDL 隐式提交，
不是业务数据迁移或事务 rollback。结构未准备时不能用空结果代替报表查询。新设备若缺少
对应报表配置，必须先恢复已审阅配置，不用此脚本创建替代页面。

`/report-engine` 的所有请求先验证真实门户登录和目标菜单。按两个菜单分别授权，仅开放相应模板与所需资源；
报表会话通过服务器生成的一次性关联参数绑定门户 token 摘要、用户、组织、角色和模板；
关联在原引擎的异步创建事件中消费，后续不能认领
未知或他人的会话。管理、配置、登录、其它模板和未验证导出动作不开放。

本目录只覆盖这两份报表，不能作为全部报表/导出或 API strict 闭包完成依据。

合法的第三方报表授权须由部署方按 FineReport 正常安装流程放在私有运行目录；
不属于 distribution 内容，也不能与应用源码一起提交。复制旧部署的原文件不保证
新机器适用，必须检查引擎的正常授权状态与注册提示，不能用偶发 trial 查询成功
判定可用。当前修复保留第三方原始校验，不修改机器标识、期限或并发限制。
