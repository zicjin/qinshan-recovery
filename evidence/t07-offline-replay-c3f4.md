# AC-10 同机离线回放历史试验记录（未宣称通过）

日期：2026-09-28（Asia/Singapore）。本记录证明离线回放入口已进行一次真实试验，
但不把一次同机回放等同于完整独立交接验收。

## 输入与资源

- Oracle 冷快照：`private/snapshots/demo-c3f4/oracle-volume.tar.gz`，SHA-256
  `5c3bdf668f36fea3a344dfe41de7b98efae8d3079dadd8a1e74356d4d539e1cf`，638,795,959 字节。
- 快照生成前在隔离 XE 中发现并清理 `SYSTEM.PRODLINK`；清理后 `APP_DB_LINK_COUNT=0`、
  `CDB_DB_LINK_COUNT=0`，记录见 `evidence/t07-db-links-before-snapshot.txt` 和
  `evidence/t07-db-links-after-scrub.txt`。
- 恢复卷：`lims-snapshot-handoff-c3f4b`，恢复 nonce 记录在受限
  `private/instances/handoffc3b/replay-manifest.env`。
- 回放实例：`handoffc3b`，地址 `http://127.0.0.1:18081/`；容器、网络和卷均使用
  `lims-replay-handoffc3b-*` / `lims-snapshot-*` 独立命名，未发布 Docker 端口。
- 应用 JAR：宿主与回放容器内均为 SHA-256
  `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`。

## 结果

- 回放 Oracle 就绪，启动前本地 `cdb_db_links` 计数为 0。
- 应用启动日志出现 `Started GikamWpjcApplication`；回放网络检查
  `private/instances/handoffc3b/isolation.txt` 为 PASS：三个回放容器只接入自己的 internal
  网络，生产/外部端点不可达，回放 Oracle/Redis 可达。
- 使用真实页面以 `<authorized-account>` 登录成功，首页显示<restricted-person>；展开「全面资源管理」后可见人员管理、仪器
  管理、标样试剂管理、耗材管理和材料领用，人员管理页面显示 323 条列表与 50 条/页。
- 回放资源在试验结束后已停止并清理；受限快照归档、实例收据和隔离报告保留供审计。

## 边界

这次试验发生在同一工作机和 Docker daemon，使用受限本地材料与同一源码工作区；没有在另一份
独立干净目录/主机完成完整 AC-10 复建，也没有交付可供另一名开发者使用的完整 `materials/`、
`private/` 和全量数据库/附件包。因此 AC-10 仍为未完成，任务 07 保持开放。

本记录只绑定当时的 `c3f4...` 产物；后续 `06c2bcf`（基于 `dd27bb7`）修改及其本地构建输出尚未在回放或主隔离实例中运行，不能继承本试验结果。
