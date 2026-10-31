# 已被 supersede 的安全构建回放记录（r06c2stage）

> 本记录绑定旧 `72ec...`/旧启动器；最终 `0285f909...` clean replay 请见 `t07-current-r0930final3.md`。

日期：2026-09-29（Asia/Singapore）。本记录按实例分段，绑定当时的嵌套工程提交
`06c2bcf65fdf73a7c89b2f6faa4dce47848500f5` 与当时回放 JAR SHA-256
`72ecb1466cefbd10aa42a36d551da43789da4399f491c576db0e23d20bc51e77`。
生产只读；回放使用新恢复卷 `lims-snapshot-r06c2stage`、internal 网络和本地镜像，
不访问生产或外部端点。

## 实例 A：`r06c2stage`

本节对应 `private/instances/r06c2stage/replay-manifest.env`、`isolation.txt` 和
`lims-snapshot-r06c2stage`。它记录 staging volume 版本的首次当前-JAR 回放。

## 启动与边界（`r06c2stage`）

- `scripts/oracle-snapshot.sh verify private/snapshots/current-06c2bcf` PASS；新卷由
  `oracle-snapshot.sh restore` 创建，Oracle 启动前本地 `cdb_db_links` 计数为 0。
- `scripts/stack-demo-restore.sh r06c2stage lims-snapshot-r06c2stage` 启动成功；应用日志出现
  `Started GikamWpjcApplication`，宿主端口为 `127.0.0.1:18083`。
- 宿主 JAR 与应用容器内 JAR SHA-256 相同（`72ec...`）。显式
  `APP_CONTAINER`/`ORACLE_CONTAINER`/`REDIS_CONTAINER`/`NET_ISO` 的
  `scripts/isolation-check.sh private/instances/r06c2stage/isolation.txt` 为 PASS。
- 应用的 `/files` 使用 `lims-replay-r06c2stage-files` 本地读写卷；宿主附件目录未直接挂载。
  staging 收据：1 个文件、30,053 字节，树 SHA-256
  `7e1b91038b5de5b8ae1543a931dee3e7514f9264ca554abde2382c64c421d6d2`。

## 页面结果

- 授权账号 `<authorized-account>` 从真实登录页登录成功；「全面资源管理」菜单显示人员、仪器、耗材和材料领用入口。
- 人员管理真实页面显示 `共 323 条`、`50条/页`；用户名筛选 `<authorized-account>` 后显示一条记录并打开详情。
- 仪器查询真实页面打开，显示列表、筛选列、`100条/页` 分页和详情标签。
- 耗材查询真实页面打开，显示耗材列表、当前数量/数量单位/失效日期/数量低限筛选列，以及「历史记录」和附件标签。
- 人员详情「签名」字段生成带 `security` 与 `token` 的隔离 URL；浏览器 `fetch` 返回 HTTP 200、`image/png`、30,053 字节，SHA-256
  `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`。
- 当前 Oracle 只读核对显示样本 `T_CORE_FILE` 元数据存在；授权人员记录存在且签名字段非空，并与受限样本 URL 精确匹配。本文不记录具体账号标识；精确值仍属于受限运行材料。
- 低权限账号 `<low-privilege-account>` 登录后首页仅显示「主业务流程」；直接访问人员详情路径返回 HTTP 403
  `GIKAM.PERMISSION.UNAUTHORIZED`，未返回人员数据。

## 未完成范围

本回放只绑定一个受限附件样本。`T_CORE_FILE`/设备采集文件的完整副本、生产 `files/` 全量树、
DB→文件缺失/孤儿分类和一致性清单仍未取得，因此 AC-04/任务 06 保持开放。
另一名开发者在独立工作目录或主机上的完整重建、凭据轮换和清理后的交接仍未执行，因此 AC-10/任务 07 保持开放。

## 实例 B：`r06c2final4`（同一记录中的独立实例段）

为验证 staging 收据和显式私有子网逻辑，使用全新实例 `r06c2final4` 再次运行启动器。
该实例的 `private/instances/r06c2final4/replay-manifest.env` 明确记录它绑定的恢复卷和脚本版本：

- 子网 `172.206.0.0/16`，internal 网络，应用/Oracle/Redis 均只接入该网络；
- `snapshot_volume=lims-snapshot-r06c2final2`、`snapshot_restore_nonce=20260929T050711Z-62947-20242`；该卷是在本回放序列前由 `oracle-snapshot.sh restore` 产生并由 manifest 绑定，不能把 `r06c2final4` 描述为自己执行了新的 restore；
- `startup_result=ready`、`database_link_count=0`，宿主/容器 JAR SHA-256 均为 `72ec...`；
- 当时 manifest 记录 `stack_script_sha256=5859053c...`、`instance_env_script_sha256=e9a46fb7...`、`attachment_stage_script_sha256=b7b084aa...`；这些是收紧前的运行脚本哈希，不覆盖当前工作树后续启动器改动；
- 附件 staging 收据 `files=1`、`bytes=30053`、树 SHA-256
  `7e1b91038b5de5b8ae1543a931dee3e7514f9264ca554abde2382c64c421d6d2`；
- 应用挂载 `/files` 为 `lims-replay-r06c2final4-files` 读写 volume，未挂载宿主源目录。

### 回放 JAR 的页面复验

- 以授权账号从 `http://127.0.0.1:18087/` 的真实登录页进入耗材查询；页面显示
  `材料名称（构建验证）` 和 `共206条`。该可见表头来自当前源码的 AC-09 改动，而非历史
  `c3f4` 产物。
- 以同一账号进入人员管理，用户名筛选 `<authorized-account>` 后页面显示 `共 1 条` 和人员详情；签名图片的
  URL 带有会话 `security`、`token` 参数，浏览器实际取得 HTTP 200、`image/png`、30,053 字节，
  SHA-256 为 `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`。
- 低权限账号 `<low-privilege-account>` 的真实登录页只显示「主业务流程」；在该已认证会话直接访问
  `/secure/emc/module/mdm/emp/emps/<authorized-account>` 返回 JSON
  `GIKAM.PERMISSION.UNAUTHORIZED`，未返回人员数据。
- 页面截图保存在 mode `0600` 的受限 `private/diagnostics/` 中；浏览器会话和保存的凭据配置均已在
  验证结束后删除。
- 当前实例随后再次执行显式隔离检查，报告为
  `private/instances/r06c2final4/isolation-rerun.txt`，结果 `PASS`；应用、Oracle、Redis 仍只接入
  自己的 internal 网络，生产/外部端点全部不可达，隔离 Oracle/Redis 正向连通。

这次回归证明当时启动器版本和 `72ec...` JAR 的关键页面行为可重复，但仍不改变完整附件/独立交接未完成的范围判断；它不覆盖 2026-09-30 的 `0285f909...` JAR 或后续启动器收紧。
