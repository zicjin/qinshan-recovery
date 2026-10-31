# 任务 06 页面附件验收记录（2026-10-12）

## 运行基准

- 应用：源码构建产物 `project/target/gikam-third-czjky-14.1.5-run.jar`。
- 宿主与容器 JAR SHA-256 均为
  `be95dbf42f0a09903237412ac80be70e4cd31714eb94b4741156b66a1c18029c`。
- Docker 应用镜像：`lims-app:14.1.5-built`；Oracle/Redis/应用均在 `lims-iso`。
- `bash scripts/isolation-check.sh evidence/t06-isolation-final-20261012.txt`：PASS。
- 浏览器：Playwright Chromium，真实页面地址 `http://127.0.0.1:8081/`。

## 授权页面流程

1. 使用受限隔离验收账号登录真实页面；首页显示授权用户和“全面资源管理”。口令仅通过
   `private/test-account.env` 提供，不写入公开证据。
   登录使用本次隔离配置中的 `sunway.user.password.encoder.type=MD5`，账号摘要仅写入隔离 Oracle。
2. 打开“全面资源管理 -> 人员管理”，列表页面真实渲染；使用受限验收记录筛选后显示一条
   已激活人员记录，关键组织字段与同一隔离数据基准一致。
3. 详情接口 HTTP 200；页面详情显示人员信息。页面请求的签名附件 URL 返回：

   ```text
   GET /files/secure/20240717/6f458cd8b5830c7e6bc8aed5a97ce264/6(%E5%B7%B2%E5%A4%84%E7%90%86).png?security=<受限>&token=<受限>
   HTTP 200
   ```

   浏览器网络记录确认响应类型为图片，文件来自隔离应用；受限样本基准为 30,053 字节，
   SHA-256 `b1f3084fda54ae2042969b800aaa45103fb97b6931757d92a7253f5af9eca065`。

## 未授权访问

- 对同一规范文件路径发送不带签名参数的匿名请求：HTTP 500，响应体 272 字节，未返回 PNG
  文件内容；这是私有文件过滤器的拒绝形态。
- 页面附件入口使用签名参数，匿名裸路径不返回文件字节，符合任务 06 的访问边界。

## 证据与限制

- 页面截图：运行环境临时保存为 `/tmp/t06-personnel-attachment.png`；受限业务截图不进入公开文档。
- 任务 06 已证明一个实际人员签名附件的数据库关联、隔离文件定位、授权页面访问和匿名拒绝。
- 本记录不宣称完整生产附件目录已恢复；设备采集文件、完整 `T_CORE_FILE` 和全量生产 `files/`
  目录仍属于未交付范围。
