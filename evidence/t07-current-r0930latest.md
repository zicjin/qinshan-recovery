# 已被 supersede 的 clean-source replay 记录（r0930latest）

> 本记录是早一轮启动器版本的现场；最终绑定请以 `evidence/t07-current-r0930final3.md` 为准。

日期：2026-09-30（Asia/Singapore）。本记录绑定最新 clean build、独立 source-tree
identity、全新 restore volume 和新的 replay instance；生产保持只读。

## 输入与运行绑定

- clean source tree：`/home/ts/qinshan/lims-recovery-clean-20260930`；
  `verify-source-handoff.py --emit-attestation` 结果：
  `ROOT_SOURCE_ID=sha256:97970f8084e643127a782e8144da813a4607eaef7a891e90d51de60caaa617b4`、
  `PROJECT_SOURCE_ID=sha256:8af57689f414c2c81799fd3720d9a627d76c67ab4f1a912c38e39d9975f5111c`。
- 最新源码构建 JAR SHA-256：
  `0285f909562608df4d25d2ed7088b874e56c1cd6edbee96dfd99b4d4a5edafbf`。
- 应用镜像 ID：`sha256:9326c0558ff7f702937cc22b4685cfcee35a9437ed37164654cb63e443dca5fc`；
  Oracle：`sha256:ecdf4302ac3d134e1bac5ef6e0c223c2d0f4d4d2b6d551aa79b2346f1ab8f792`；
  Redis：`sha256:858f009f9709ce576febc734aa78b8f6d624b82571f9ddb6bda4377c833b3499`；
  archiver：`sha256:e5b6442dd2e9684cf5e87d8338b5968f3b348636fc0be6d7850a381e3731a2bd`。
  四者均为 `linux/amd64`，启动使用 `--pull=never`。
- 快照经 `oracle-snapshot.sh verify` 通过；新 restore volume 为
  `lims-snapshot-r0930latest`，restore nonce、archive hash、Oracle/archiver image labels 与完成 marker
  均一致。回放实例为 `r0930latest`，宿主绑定 `127.0.0.1:18089`，网络为 internal
  `lims-replay-r0930latest-net`（`10.27.0.0/16`）。
- replay manifest：`private/instances/r0930latest/replay-manifest.env`（clean tree 中）；记录
  `source_identity_kind=restricted-archive-manifest`、三个 runtime image ID、archiver ID、snapshot lease、
  `database_link_count=0`、`startup_result=ready`。
- 应用日志记录 `Tomcat started on port(s): 8081`、`Started GikamWpjcApplication in 100.228 seconds`；
  容器内 JAR SHA 与宿主相同。

## 隔离边界

`private/instances/r0930latest/isolation.txt` 为 PASS：应用、Oracle、Redis 仅接入自己的 internal 网络；
应用和 Oracle 对生产 Oracle、文件服务、FTP、OA、帆软、NC、客商、邮件及历史测试库等 10 个端点均不可达，
且可正向连通隔离 Oracle/Redis。附件源只读 staging 到
`lims-replay-r0930latest-files`，树收据为 1 个文件、30,053 字节、
`7e1b91038b5de5b8ae1543a931dee3e7514f9264ca554abde2382c64c421d6d2`。

## 真实页面验收

使用受限授权账号（文档不记录账号标识）从真实登录页登录成功，菜单显示人员、仪器和耗材查询入口。

- 人员：列表 `共 323 条`、`50条/页`；用户名筛选返回 `共 1 条`，详情可打开；切换到第 2 页后首条记录变化，分页有效。
- 设备：仪器列表 `共 72 条`、`100条/页`；仪器名称筛选返回 `共 5 条`，列表与详情区域可用。
- 耗材：页面显示 `共206条`，表头真实显示 `材料名称（构建验证）`；库存和历史记录区域可打开；名称筛选返回 1 条，
  库存无匹配状态显示 `共0条/暂无数据`。
- 附件：人员详情签名 URL 含会话 `security`/`token` 参数；浏览器 fetch 返回 HTTP 200、`image/png`、30,053 字节，
  SHA-256 `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`。

使用受限低权限账号登录后首页只有「主业务流程」。对人员、设备、耗材页面入口以及三类原始查询路径的直接请求均返回
HTTP 403、`application/json;charset=UTF-8`、`{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}`，未返回业务数据。

## 范围说明

本记录证明最新 clean-source/JAR 的同机 replay、隔离、首轮三类查询和单个附件样本；不证明完整 `T_CORE_FILE`、设备采集文件、
生产 `files/` 全量树或 DB→文件 reconciliation，也不替代凭据/许可证轮换和仓库历史清理。AC-04 的全量数据/附件仍开放；
AC-10 的“另一开发者/主机”及可分发清理包仍需单独完成。
