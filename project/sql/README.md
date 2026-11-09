# SQL 材料与当前结构的边界

本目录保存原始 project 发布、初始化和升级 SQL，属于历史版本材料，**不是当前 production
数据库的完整 DDL 快照，也不是可整目录顺序执行的初始化入口**。文件中的版本号不证明它与
当前部署制品、生产结构属于同一检查点。保留历史原件及已有来源 hash，不覆盖成另一版本。

当前已核对的结构差异及可执行增量维护在
[api-fixtures/schema](../../api-fixtures/schema/README.md)。生产只读提取的脱敏来源子集为
[production-ddl-source-20261109.json](../../api-fixtures/schema/production-ddl-source-20261109.json)，
对应两批迁移是 `production-ddl-alignment-20261109.sql` 和
`production-ddl-field-alignment-20261109.sql`。这些迁移有目标 owner/container 和原结构检查，
仅用于命名测试 runtime，不能直接在 production 或只读 baseline 执行。

已确认的具体版本差异：历史 14.1.5 发布 SQL 和 stock 报表树 Mapper 定义／读取
`T_CORE_REPORT_CONFIG.CATEGORY`，2026-11-09 production 实际结构没有该列。
`report-config-category-restoration.sql` 是本机运行该 stock 查询契约的显式兼容扩展，
不能称生产漏导恢复，也不从生产无该列推导出应删掉 stock 查询所需的列。

生产结构对齐不复制 Data Pump 作业表、连接秘密、外部 DB link 或序列当前值；测试专用的
source-backed 扩展与未解决的版本／编码差异单列。DDL 与应用契约须成对核对，不能仅凭
历史 CREATE TABLE 或一份生产快照覆盖现有测试结构和业务数据。
