# AC-01 至 AC-10 最终汇总（2026-10-12）

## 已完成证据

| 验收项 | 结果 | 主要证据 |
| --- | --- | --- |
| AC-01 材料与版本基线 | 通过 | `evidence/t01-material-integrity.md`、`t02-source-inventory.md`、`t02-build.md` |
| AC-02 构建 | 通过 | `evidence/t02-build.md`；源码构建产物 SHA-256 `be95dbf42f0a09903237412ac80be70e4cd31714eb94b4741156b66a1c18029c` |
| AC-03 隔离启动 | 通过 | `evidence/t06-isolation-final-20261012.txt`、`private/instances/ac10/isolation.txt` |
| AC-04 数据与附件 | 演示范围通过 | `evidence/t06-page-acceptance-20261012.md`、`t07-ac10-replay-20261012.md`；不是全量生产副本 |
| AC-05 登录与权限 | 通过 | `evidence/t01-isolated-login.md`、`t03-personnel-query.md`、低权限回归记录 |
| AC-06 人事查询 | 通过 | `evidence/t03-personnel-query.md`；回放实例人员入口可打开 |
| AC-07 设备查询 | 通过 | `evidence/t04-equipment-query.md`；最终源码隔离实例证据 |
| AC-08 耗材查询 | 通过 | `evidence/t05-consumables-query.md`；回放实例耗材列表可打开 |
| AC-09 二次开发 | 通过 | `evidence/t07-development-and-handoff.md`；源码表头改动、构建、部署、页面可见 |
| AC-10 可交接 | 同机独立回放通过，跨主机未完成 | `evidence/t07-ac10-replay-20261012.md`；仍需另一主机/干净 checkout 的正式交接复验 |

## 交接输入与重建入口

- 材料校验：`bash scripts/verify-materials.sh`。
- 构建准备/构建：`bash scripts/build-prep.sh`、`bash scripts/build.sh`。
- 隔离配置：`bash scripts/make-isolated-config.sh`。
- 基础服务和演示复制：按 `docs/recovery-handoff.md`，复制完成后必须执行 `scripts/stack-isolate.sh`。
- 单附件样本：`bash scripts/restore-attachment-sample.sh`。
- 独立回放：先验证快照，再 `oracle-snapshot.sh restore` 创建新卷；使用 `instance-env.sh` 初始化和校验运行输入；最后运行 `stack-demo-restore.sh`。
- 56 项固定构建/运行材料按 `evidence/t01-manifest.sha256`，以及离线 Maven 插件种子按
  `evidence/t02-build-plugin-seed.sha256` 纳入受控私有 Git；其他口令、MAC、隔离配置、
  数据库快照和附件仍属于受限材料，不进入公开文档或 Git。

## 已知限制

- 数据库是演示范围；中断的持续全量 Data Pump dump 未使用，`QYJWDDR` 缺失，部分专用对象/字符集注释有导入警告。
- 仅恢复并验证一个真实人员签名附件样本，不代表完整附件归档。
- 本地 Oracle XE 21c 与生产 Oracle 19c EE 不同。
- 其他业务流程、写入流程、全部生产一致性和完整附件目录未系统验收。
- AC-10 本轮是在同一工作机/同一 Docker daemon 创建独立新回放实例；未在另一台主机或完全干净 checkout 完成交接，因此保留为部分完成状态。
