# 最新最终 clean-source replay 回放记录（r0930final3）

日期：2026-09-30（Asia/Singapore）。本记录绑定最终 clean-source replay；旧的
`r06c2*`、`r0930latest`、`r0930secure2` 仅作为历史/失败现场，不与本记录混用。

## 精确绑定

- source-only sanitized tree（`/home/ts/qinshan/lims-source-package-r0930f`）attestation：
  `ROOT_SOURCE_ID=sha256:ff22141e49a9dd163c040faca365c62e0b1b24efffcd55f04ea12dca8c54d8b5`；
  `PROJECT_SOURCE_ID=sha256:8af57689f414c2c81799fd3720d9a627d76c67ab4f1a912c38e39d9975f5111c`。
  这些值来自 `verify-source-handoff.py --emit-attestation` 对排除 `private/`、`materials/`、构建缓存和 Git
  元数据后的 source-only tree；runtime 输入另经受限渠道放入 clean replay tree。直接对含 `private/`、
  `materials/` 的执行树运行 verifier 会按设计拒绝，不能把两者混为一棵可分发树。固定 receipt 见
  `evidence/source-handoff-r0930final3.txt`。
- latest JAR：`0285f909562608df4d25d2ed7088b874e56c1cd6edbee96dfd99b4d4a5edafbf`。
- app image：`sha256:9326c0558ff7f702937cc22b4685cfcee35a9437ed37164654cb63e443dca5fc`。
- Oracle/Redis/archiver images：`sha256:ecdf4302ac3d134e1bac5ef6e0c223c2d0f4d4d2b6d551aa79b2346f1ab8f792`、
  `sha256:858f009f9709ce576febc734aa78b8f6d624b82571f9ddb6bda4377c833b3499`、
  `sha256:e5b6442dd2e9684cf5e87d8338b5968f3b348636fc0be6d7850a381e3731a2bd`；全部 `linux/amd64`，`--pull=never`。
- snapshot：`current-06c2bcf` 经 `oracle-snapshot.sh verify` 通过；新 restore volume
  `lims-snapshot-r0930final3` 的 archive SHA-256 为
  `95eacaa8a0e17a10fe342e28387880040c0f974e25c1524a20419ce41a4337ae`，Oracle/archiver labels 和 volume marker
  一致，lease 已保留。
- instance：`r0930final3`，`127.0.0.1:18093`，internal network `lims-replay-r0930final3-net`，
  subnet `10.145.0.0/16`（以 manifest 为准）。最终 manifest 还绑定：
  `stack_script_sha256=974be4cca9738596e519153b42842742004eada01399f75c0b619276f12ae652`、
  `oracle_snapshot_script_sha256=c8728c3a332e644fc2d3029cb167cc7da0f7e4ef613b9fd51dabeda29e37188b`、
  `isolation_script_sha256=6c5051e82e5e63ddb075e1c665a5397ad419ca130bc120b1f1a7929fa6543c3a`。
  `oracle_snapshot_script_sha256` is a launcher provenance field for the current input; the restricted
  snapshot manifest/verify output and restore command were executed from the recovery worktree and are not
  embedded in the source-only package.

## 启动与隔离

- 应用日志：`Tomcat started on port(s): 8081`、`Started GikamWpjcApplication in 91.07 seconds`；容器内 JAR SHA 与宿主相同。
- 应用容器没有 published host ports 或 `ExtraHosts`；页面仅通过 `127.0.0.1:18093` forwarder 暴露。
- `private/instances/r0930final3/isolation.txt` 为 PASS：应用/Oracle/Redis 仅在自己的 internal 网络；应用和 Oracle
  对 10 个生产/外部端点全部不可达，并可连通隔离 Oracle/Redis。
- staging 收据：1 个文件、30,053 字节、树 SHA-256
  `7e1b91038b5de5b8ae1543a931dee3e7514f9264ca554abde2382c64c421d6d2`；应用未直接挂载宿主附件源。

## 页面和权限观察

授权账号从真实登录页进入并看到人员、设备、耗材入口。

- 人员：初始 `共 323 条`、`50条/页`；用户名筛选 `<authorized-account>` 返回 `共 1 条` 和详情；输入不存在的筛选值返回
  `共 0 条`；页码输入切换到第 2 页后首行由一个演示账号变为 `<restricted-personnel-sample>`，证明分页请求改变了记录范围。
- 设备：初始 `共 72 条`、`100条/页`；名称筛选“烟气预处理器”返回 `共 5 条`；不存在的筛选值返回
  `共 0 条`。选定详情显示站内编号 `<restricted-equipment-code>`、仪器名称、规格型号、出厂编号、仪器状态和仪器类型等关键字段。
- 耗材：初始 `共206条`，表头真实 DOM 文本为 `材料名称（构建验证）`；选定样本编码 `<restricted-consumable-code>`、
  名称 `<restricted-consumable-name>`、库存数量 `0`、材料类型“实验耗材”；名称筛选该样本返回 `共1条`，
  不存在的筛选值返回 `共0条`；库存和历史记录区域可见，库存无记录状态显示 `共0条`/`暂无数据`。
- 附件：人员详情电子签名 URL 带 `security`/`token`；浏览器 fetch 返回 HTTP 200、`image/png`、30,053 字节，
  SHA-256 `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`。
- 低权限账号首页只有「主业务流程」。人员、设备、耗材页面入口及设备/耗材原始查询请求均返回
  HTTP 403、`application/json;charset=UTF-8`、`{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}`；未返回业务数据。
- 受限浏览器 DOM/HTTP 摘要与截图收据保存在 clean replay tree 的
  `private/diagnostics/r0930final3-browser-observations.json` 及同目录 PNG 文件中（mode 0600）。

## 限制

本记录覆盖最新 clean-source/JAR 的同机动态 replay 和窄范围页面验收，不等于完整生产数据/附件恢复；完整
`T_CORE_FILE`、设备采集文件、生产 `files/` 全量树、DB→文件 reconciliation、凭据/许可证轮换、历史清理、
可分发交接包以及另一开发者/另一主机验收仍未完成，AC-04 和 AC-10 保持开放。受限 snapshot archive/manifest
仍需按 handoff 规则单独交付；source-only verifier 不应直接运行在含 `private/`/`materials/` 的执行树上。
