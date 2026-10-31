# 任务 06 数据与附件验收记录

日期：2026-09-27（Asia/Singapore）。环境：历史隔离源码构建镜像
`lims-app:14.1.5-built`，应用与 Oracle/Redis 仅接入内部网络 `lims-iso`。
本记录只覆盖一个经过验证的人员电子签名附件样本；不把它表述为完整附件归档。

## 基准与范围

- 生产只读查询确认 `T_CORE_FILE` 中的样本：ID `<restricted-file-id>`，关联
  `T_CORE_USER$<authorized-account>`，范围 `secure`，时间 `<restricted-file-time>`，文件名
  `6(已处理).png`，记录大小 `30,053` 字节。
- 生产文件路径按私有框架规则解析为
  `files/secure/20240717/<restricted-file-key>/6(已处理).png`。
  生产只读 `stat` 和 `sha256sum`：30,053 字节，
  `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`。
- 使用仅拉取的 `scp` 将该文件置于受限材料
  `private/t06-attachment-sample/`；本地 SHA-256 与生产相同。
- 历史源码构建产物（含当时权限/文件-url修复）SHA-256：
  `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`；容器内同值。
- 历史 c3f4 运行路径中，`scripts/restore-attachment-sample.sh` 会先校验受限样本的 SHA-256，随后仅向
  隔离 Oracle 恢复该原始 `T_CORE_FILE` 元数据和 `T_EMC_EMP(<authorized-account>).ELECTRONICSIGNATURE`
  关联；该历史镜像曾从受限样本目录复制此文件。生产没有写入。

## 页面验证

1. 以具有人员管理权限的 `<authorized-account>` 从真实登录页面进入「全面资源管理 -> 人员管理」。
2. 使用已有用户名筛选 `<authorized-account>`，页面显示<restricted-person>及其人员基本信息。
3. 页面「签名」字段渲染带框架 `security` 与会话 `token` 参数的隔离 URL
   `/files/secure/20240717/<restricted-file-key>/6(%E5%B7%B2%E5%A4%84%E7%90%86).png?...`。
   具有权限的浏览器实际取得 HTTP 200、`image/png`、`Content-Length: 30053`；该值与采集基准一致。
   不带参数的匿名请求返回 HTTP 500 `ApplicationRuntimeException`（私有文件过滤器拒绝），
   没有泄露文件内容。
4. 截图（受限业务数据）保存在受限会话归档中，文件名不公开。

## 历史 URL 权限回归（c3f4）

日期：2026-09-28（Asia/Singapore）。当时隔离 JAR SHA-256：
`c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`；
Tomcat 与应用已成功启动。

- 以 `<authorized-account>` 从真实登录页登录后，样本元数据接口返回 HTTP 200，带签名 URL 的下载返回
  HTTP 200、`image/png`、30,053 字节，SHA-256 为
  `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`。
- 匿名的规范路径、首段矩阵参数路径和重复斜杠路径（`/files/...`、
  `/files;foo/...`、`//files//...`）均被拒绝，未返回文件字节；规范路径由私有文件过滤器返回
  HTTP 500 JSON，非规范路径由本地守卫返回 HTTP 403 JSON。
- 以无人员菜单权限的活动账号 `<low-privilege-account>` 验证：核心文件详情、文件管理 `get-url`、旧版
  `sunwayoffices`、PageOffice 及 Cover PageOffice 的规范、矩阵参数和重复斜杠路径均返回
  HTTP 403 `GIKAM.PERMISSION.UNAUTHORIZED`，未返回样本 URL 或内容。

## 隔离与限制

- `bash scripts/isolation-check.sh evidence/t03-05-isolation-final.txt` PASS：应用和
  Oracle 无法连接生产 Oracle、生产文件服务、FTP、OA、邮件和其他受测外部端点；仅可连接隔离
  Oracle/Redis。
- 受限样本只证明一个实际数据库关联、文件定位和受权页面渲染在隔离存储中可用。当前演示副本
  仍未恢复完整 `T_CORE_FILE`、设备采集文件或生产 `files/` 全量目录；这些是明确的交接限制。
- 历史回归已使用无人员菜单权限的活动账号 `<low-privilege-account>` 完成同一样本的带会话拒绝检查；但任务 06
  仍只证明一个样本，不构成完整附件恢复、完整权限覆盖或完整副本验收，任务保持开放。

历史 `c3f4` 页面结果不能替代后续回放 JAR；`72ec...` 的独立回放结果补充在本记录末节，并仍只证明该单一样本。

## 可重复操作

- `bash scripts/restore-attachment-sample.sh`
- `docker build --pull=false -f docker/Dockerfile.built -t lims-app:14.1.5-built .`
- `IMAGE=lims-app:14.1.5-built bash scripts/app-ctl.sh restart`
- `bash scripts/isolation-check.sh evidence/t03-05-isolation-final.txt`

## 2026-09-29 回放 JAR 的隔离运行绑定

本节只记录当时嵌套提交 `06c2bcf`、回放 JAR SHA-256
`72ecb1466cefbd10aa42a36d551da43789da4399f491c576db0e23d20bc51e77` 的运行边界和窄样本回归，
不把演示样本扩大表述为完整附件交付。2026-09-30 的内容等价 JAR 已在 `r0930final3` 回放；确定性
JAR `c423fa1a...` 的 smoke 回放见 `r0930deterministic`。本节历史样本结果不与最新 manifest 混用。

- 回放实例 `r06c2stage` 使用新恢复卷和独立 internal 网络；`private/instances/r06c2stage/isolation.txt`
  为 PASS，Oracle/Redis/应用均无法访问生产或外部端点。
- `scripts/stage-attachments.sh` 将实例输入中的 1 个文件、30,053 字节复制到
  `lims-replay-r06c2stage-files`，树 SHA-256 为
  `7e1b91038b5de5b8ae1543a931dee3e7514f9264ca554abde2382c64c421d6d2`；应用容器只挂载该本地卷，
  未挂载宿主附件源目录。对应挂载和收据见 `private/instances/r06c2stage/replay-manifest.env`。
- 真实页面以授权账号登录成功，人员管理列表显示 323 条记录、50 条/页，并能打开人员详情；截图保存在
  受限 `private/diagnostics/`。随后在 `r06c2final4` 回放中，以用户名 `<authorized-account>` 筛选到一条
  人员详情；签名图片 URL 带有会话 `security`/`token` 参数，浏览器实际取得 HTTP 200、`image/png`、
  30,053 字节，SHA-256
  `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`，与受限样本采集基准一致。
- 同一 `r06c2final4` 实例中，低权限账号 `<low-privilege-account>` 的真实登录页只显示「主业务流程」；该已认证会话直接
  访问 `/secure/emc/module/mdm/emp/emps/<authorized-account>` 返回
  `{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}`，未返回人员数据。该检查只涉及隔离回放地址。

完整 `T_CORE_FILE`、设备采集文件、生产 `files/` 全量树和 DB→文件一致性清单仍未取得，AC-04/任务 06 保持开放。

## 生产只读 inventory 准备（2026-09-29）

- 仅用无副作用的 SSH probe 确认生产应用 `files/` 根及配置的设备采集根均为存在的本地目录；没有执行
  生产写入、文件复制、数据库 link、Docker、FTP 或递归全量扫描。
- 对生产 `files/` 的限时元数据 walk 在 15 秒内未完成，因此没有把不完整的计数或目录枚举写入证据。
  完整文件树必须在存储侧批准的一致性窗口或快照内完成，并使用 NUL 分隔、`find -P -xdev` 的只读采集。
- 新增 `scripts/t06-attachment-inventory.sh` 和固定查询 helper。它只在调用者显式提供
  `--production-read-only`、mode `0600` 的 DBA 专用 SELECT-only 配置时运行，拒绝与本地应用 schema
  相同的账户，不接受任意 SQL，不选取 `CONTENT`/`RESULT` LOB，也不会创建 `PRODLINK`、调用 Docker 或复制文件。
  原始附件元数据清单只会写入受限 `private/t06-inventory/<UTC>/`；公开证据只能使用脱敏汇总。
- 仍缺 DBA 交付的只读 inventory 账号、授权范围确认、完整 `T_CORE_FILE`/设备采集 metadata、生产
  文件树快照和 DB-to-filesystem reconciliation 结果。以上条件取得前，本任务不能标记完成。
- 2026-09-30 的只读 SSH 边界探针确认两个生产根目录都位于同一 `/dev/vdb1` XFS `/data` 文件系统，
  当前为 `rw`，且没有现成 snapshot/window 标识；探针没有执行 freeze、递归扫描、复制或 hash。详见
  `evidence/t06-production-readonly-probe-20260930.md`。
- `scripts/tests/test-t06-attachment-inventory-jdbc.sh` 使用本地 fake JDBC driver 验证固定 SELECT、
  flashback SCN、只读连接生命周期和路径/目标分类输出；该测试不连接生产，也不替代 DBA 账号下的实际 inventory。
- 新增 `scripts/t06-filesystem-inventory.sh` 作为独立的存储侧元数据阶段；它要求显式
  `--storage-snapshot-id`，只采 `find -P -xdev` 的大小、mtime 和路径 triple-NUL 记录，不读取文件内容，
  不复制附件。当前尚未执行，因为本会话没有存储侧批准的一致性窗口/快照标识。
- 新增本地-only `tools/t06-reconcile/reconcile.py`。在两类受限 inventory 实际完成后，它会把
  `T_CORE_FILE` 的规范路径与文件树元数据分为 matched、missing、possible orphan、size mismatch 和
  unresolved；`X` 前缀目标和没有安全路径契约的设备采集文件保持 unresolved，不猜测或吞掉差异。
