# Ticket 11 consumables query: owned Java read seam evidence

日期：2026-10-20（Asia/Singapore）。工作树：`ticket-11-consumables-query`；提交：`77ae00465f163d8a5275ad0055a8ae8688831099`，已包含 integration tip `8af4c0d`。生产未访问、未写入。

## 已实现的 Java 读取边界

`ConsumableQueryService` 提供耗材列表/详情、库存列表/详情、库存日志列表/详情。每个入口重新检查当前账号、支持的登录配置、活动状态、耗材菜单（管理 `430201` 或只读查询 `20250800003`）和当前组织授权。SQL 标识符来自服务端白名单；值使用绑定参数；组织范围来自当前账号的可信 `T_CORE_USER_PERMISSION` 记录。请求中的 `org_filter` 只能作为可观察筛选提示，不能扩大组织范围；顶层服务器参数、未知字段、未知排序和不支持的高级查询形状会拒绝。

默认列表顺序为 ID 降序，分页使用 Oracle `ROW_NUMBER`。保留参照中库存直接筛选 `status_SEQ` 和 `stockQty_NG`；覆盖字符串、空值、日期/时间边界、数值比较、参照的 `NEQ` 等筛选语义。日志读取保留接收人和组织名称连接，并通过库存到耗材组织范围约束列表和详情。

## 回归证据

运行：

```
python3 scripts/tests/test-consumables-query-oracle.py \
  --output evidence/consumables-query-oracle-20261020.json
```

结果：`consumables query service: PASS (23 checks; synthetic Oracle only)`。报告记录了源码哈希、提交 `77ae004...`、自有框架构建和产物哈希、无生产访问、digest 固定的 Linux amd64 Oracle 镜像、内部单端点网络、无端口发布/宿主挂载，以及容器和网络清理成功。报告中 `source_dirty=false`，Oracle 错误码为空。

23 项检查覆盖组织范围、列表分页/默认顺序、名称筛选和无匹配、详情、库存直接筛选/空值显示全部、`NEQ` 与 `DIFFER`、日期/时间边界、库存和日志详情越组织拒绝、日志接收人/组织连接、组织提示不可扩大范围、未知参数拒绝、只读菜单授权、角色暂停和权限撤销。

## 未闭合范围

本证据只证明自有 Java 读取 seam 和合成 Oracle 行为。它没有把服务接入原有业务 resource/service/DAO、HTTP 路由或浏览器页面，也没有重新执行真实页面验收。票据中的页面打开、页面字段/筛选/详情核对仍保持开放；库存写入、入库、盘点、废弃、领取、导入和其他出入库流程未验证。历史页面证据 `evidence/t05-consumables-query.md` 不继承到本提交的运行时产物。

## Integration regression

2026-10-20: integration commit `22d66055ded52c13da12dfd2ad4ad5b970c7459c` reran the same disposable synthetic Oracle read seam after personnel generic prerequisites were merged. All 23 checks passed with `source_dirty=false`; the report records artifact/source hashes, internal single-endpoint network, no published ports or host binds, and successful container/volume/network cleanup. See `consumables-query-oracle-20261020-postmerge.json`. HTTP, browser, original resource/service/DAO wiring and writes remain unaccepted.
