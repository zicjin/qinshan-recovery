# 全量功能覆盖目录种子

机器可读目录位于 [`function-coverage-catalog.tsv`](function-coverage-catalog.tsv)。它是 **30 项用户能力的目录种子**，不是全量完成证明，也不是从生产部署自动抽取的最终目录。每行保留候选能力名称、缺口和证据引用；菜单、路由、页面、handler、service、mapper、数据库对象、权限和集成字段在尚未有精确来源时均为 `unknown`。已核对的关系可以用分号分隔的仓库相对路径记录，既包括 `project/` 历史源码，也包括 `owned-*` 自有实现；这些路径只证明静态来源，不提升行为状态。

目录把范围和来源分开：`scope=owned-runtime` 表示自有运行时；`historical_status` 与 `provenance` 单独记录历史构建、供应商运行时或隔离观察。自有人员工作台的干净浏览器报告已支持 `identity.login` 和 `personnel.query` 的 `read-flow-verified`；打包查询守卫与实际三域资源检查支持 `identity.permission` 的 `permission-checked`。这些状态限于条目中记录的合成数据和只读范围，不包含全量角色、写入或生产等价。

设备、耗材和报表的有界浏览器回归已录入，目录只将对应只读能力标为 `read-flow-verified`；耗材模板导出与材料/库存导入已在显式隔离 Oracle loopback 中标为 `write-flow-verified`，附件元数据、启停、人员授权、受保护文件上传/替换以及管理页写入路径也已单独标记为隔离 `write-flow-verified`。其他采购、维护、校准、通用业务导入导出、第三方报表等范围仍保持未知。附件浏览器点击下载和管理页回归已录入为有界证据；更广泛关联和归档保留仍开放，附件替换、删除的提交后物理清理和失败事务孤儿保护已由隔离 Oracle 回归覆盖。ERP 文件 loopback 回放仍只作为有边界的证据记录，不能代表全部 ERP/NC 集成通过。主动停用的历史任务和集成仍在 `historical_status` 中标为 `disabled-in-isolated-env`，未知行为保持 `unknown`。

状态使用规格定义的八个值：`static-present`、`menu-observed`、`page-opened`、`read-flow-verified`、`write-flow-verified`、`permission-checked`、`disabled-in-isolated-env`、`unknown`。`state_history` 记录有证据支持的状态；后续获得精确来源和行为证据后，才可更新自有运行时状态。

验证目录和来源清单结构：

```bash
python3 lims-recovery/tools/validate_function_catalog.py \
  lims-recovery/evidence/function-coverage-catalog.tsv \
  --source-inventory lims-recovery/evidence/t02-source-inventory.tsv
```

验证器拒绝把候选 handler/service/mapper 或表名写成事实，拒绝凭据、生产端点和缺少范围/历史状态的条目。目录不保存密码、token、生产端点、业务记录或附件正文。生产菜单/角色导出尚未取得时，SQL、源码和历史页面证据只作为候选或历史来源，不冒充当前部署状态。验证器还会逐项确认已填写的来源路径是当前恢复仓库中的普通文件，并接受受控的集成类别值（例如 `erp`、`oa`、`ftp` 和 `mail`）；来源被删除、改名或误写时，目录检查会失败，而 `unknown` 仍明确保留给尚未建立来源关系的字段。

静态来源索引已生成到 [`function-catalog/recovered-source.json`](function-catalog/recovered-source.json)，包含完整源码树的映射注解、模板、service/mapper 接口和 Mapper XML 的路径、行号与哈希。它提供目录扩展的来源，不能自行改变动态状态。经真实自有运行时验证的状态必须在 `provenance` 中引用 `owned-runtime-evidence:`，不能用历史供应商证据晋升自有运行时状态。
