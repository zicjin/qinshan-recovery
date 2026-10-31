# 源码历史调查

调查日期：2026-09-23。仅复制生产目录的 `.git` 到本机 `evidence/deployment.git` 进行读取，没有在生产目录执行 checkout、修改或启动应用。

- Git HEAD：`01f9a20`，提交日期 `2024-01-15`；可读取 HEAD 下的 `pom.xml` 和业务 Java、JS 源文件。尚未验证所有对象完整性，也未核对当前部署字节码与历史源码的差异。
- 部署 Git 索引有 5,212 个路径，其中 Java 2,848、JS 784、HTML 627、XML 517；这些是索引统计，不能等同于当前部署版本的源码覆盖率。
- Maven 父工程为 `com.sunwayworld.cloud:gikam-core-parent:14.1.5`；包含 core-web、core-common、core-base-service、core-file-default、core-report-service、core-ext-service、core-schedule-service、core-lcdp-service、core-fr-service10.0 等私有依赖。
- 已读取人事 EmcEmpDaoImpl、设备 EmcEquiptDaoImpl、耗材 EmcConsumeDaoImpl，均继承私有框架的 `MybatisDaoSupport`。
- 已读取三个模块的页面 JS，均调用 `Gikam.getPageObject` 及相关组件接口。

结论：存在可读取的历史源码，应该优先评估原始源码复用，再补齐与生产版本的差异。不能声称现有源码已与生产一致或已经能够构建运行。

用户已确定：恢复完整应用及必要依赖；本机 /home/ts/qinshan 下保存材料、恢复与隔离验证；允许完整复制数据库及附件；生产仅只读；首轮验收聚焦人事、设备、耗材查询。私有框架的可修改范围待用户在了解调查结果后决定。
