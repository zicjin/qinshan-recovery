# 任务 06 当前运行恢复记录（2026-10-10）

## 数据恢复

- 离线导入的镜像：`gvenzl/oracle-xe:21-slim`、`redis:7.4-alpine`、`debian:bullseye-slim`。
- 结构导出已完成后，持续增长的 `t01_data.dmp` 按用户决定停止；中断 dump 未导入并已删除。
- 元数据通过 `remap_tablespace=LIMS2_CZJKY_EMC_DEV:USERS` 导入隔离 XE；`QYJWDDR` 作为历史已知缺失表排除。
  结构导入后隔离 schema 有 799 张表，`T_CORE_USER`、`T_CORE_ROLE`、`T_CORE_MENU`、`T_EMC_EMP`、
  `T_EMC_EQUIPT`、`T_EMC_CONSUME` 均存在。导入日志中的 44 个错误/警告保留在
  `private/t01-impdp-meta-retry.log`，主要是已有对象、字符集注释和专用对象差异。
- `scripts/db-copy-demo.sh` 已先创建/复用隔离业务用户，再通过生产只读 DB link 生成演示数据；510 条表填充语句中
  509 个表有成功插入结果，行数基准写入 `private/t01-demo-rows.txt`。生产没有执行 DML/DDL。
- `scripts/stack-isolate.sh` 已断开 `lims-oracle` 的 `lims-copy` 网络；后续运行仅使用 `lims-iso`。

## 运行与隔离

- 生产只读读取许可证要求的网卡标识，写入受限 `private/license-machine.env`（0600），许可证校验通过；具体标识不写入公开证据。
- `lims-app`、`lims-oracle`、`lims-redis` 均仅接入内部网络 `lims-iso`。
- `bash scripts/isolation-check.sh evidence/t06-isolation-current.txt`：PASS。
- 应用容器保持运行，首页通过本机转发返回 HTTP 200，响应 2,710 字节。

## 页面验收阻塞

- `agent-browser` 已安装，但本机没有 Chrome/Chromium；`agent-browser install` 从 Chrome for Testing 下载超时。
- 因此本记录不宣称真实页面登录、附件查看/下载或无权限拒绝已经通过。应用 HTTP 200 只能证明端口和基础响应可用。
- 待取得本地 Chrome/Chromium 可执行文件或离线浏览器包后，继续使用真实页面完成任务 06 验收。

## 后续浏览器安装尝试

- Ubuntu 软件源的 `chromium-browser` 仅为 Snap 转换包，当前 WSL 未安装 snapd，无法提供浏览器二进制。
- Windows 已发现 Edge 154，但从 WSL 调用其调试端口时 Windows 仅绑定 `127.0.0.1`，
  `172.30.16.1` 无法访问；这属于 Windows 防火墙/回环策略限制，不能据此宣称浏览器验收完成。
- 当前仍保留应用运行和隔离证据，不修改生产，不把 HTTP 接口探测替代真实页面验收。

## 当前结论

任务 06 的数据结构、演示数据、单附件文件材料、隔离运行和应用启动已取得本轮证据；附件数据库关联和页面授权/拒绝仍待浏览器验收。任务 07 的独立重建仍未完成。
