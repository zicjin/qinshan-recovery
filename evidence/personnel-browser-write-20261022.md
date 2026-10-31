# 人员主表真实工作台验证（2026-10-22）

`python3 owned-web/src/test/browser/run-browser.py --output .scratch/sunway-supplier-exit-api-first/archive/historical/personnel-browser-write-20261022.json` 通过 33 项检查，另包含 15 项合成数据查询服务基线。完整源码、恢复源码、公开依赖和产物哈希见同名 JSON；运行开始时源码提交为 `5e8b0aa`，工作树仍含未提交集成改动，`source_dirty=true` 如实保留。

真实浏览器登录后打开已有人员页面与历史脚本，通过自有 dialog 从已有账号 `e-4` 新增人员、改名和确认删除。新增和修改分别直接核对 Oracle 的姓名；删除后页面恢复 23 条基线，Oracle 中 `e-4` 与额外 API 探针行均不存在，越组织原始行不变。浏览器同时验证 PUT 越组织拒绝、非法 POST 拒绝、退出失效和低权限拒绝。写入审计包含新增、修改、删除且不含测试凭据。

失败诊断保留在 `.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/failed/personnel-browser-write-dialog-diagnostic-20261022.json`：保存后立即显示成功、再异步刷新列表，使下一次筛选与旧列表刷新交错。修复后成功提示等待刷新完成，脚本还核对改名持久化及新行投影。通知回调受页面 revision 约束，退出后不再显示旧写入提示。

Oracle 使用固定摘要的 Linux amd64 镜像，仅有一个内部网络，无端口发布或 bind mount。浏览器会话、应用进程、容器、匿名卷和网络均完成清理，无生产访问。该结果仅证明显式隔离模式的人员基本信息主表路径；默认模式拒绝另见人员 HTTP 主表证据。培训、可见测试写入、人员附件、导入导出、报表和 `sss2` 写入观察仍开放。
