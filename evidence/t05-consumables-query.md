# 任务 05 耗材查询验收记录

日期：2026-09-27（Asia/Singapore）。对象：当时源码构建并部署到隔离副本的应用。生产未访问、未写入；本次页面操作和接口探针均为只读。

## 结论

耗材查询的授权入口、列表、已有筛选、无匹配状态、详情及页面实际展示的库存/历史信息均已在本记录所列历史构建产物上观察到。低权限账号（仅「项目查询」角色）在该部署中不能通过耗材页面、详情、原始列表、库存或库存历史接口绕过权限。本结论只覆盖 AC-04 的耗材查询数据切片；完整 AC-04 仍由任务 06 保持开放。

耗材页面当前配置没有分页（源码 `emcConsume.editPage.getGridParam()` 未设置 `page: true`，真实页面渲染 206 行且没有页码/上一页/下一页控件）。AC-08 要求验证适用的分页行为；这里已记录为不适用，未虚构产品原本不存在的分页功能。

## 运行与数据基准

- 历史源码基线：嵌套工程提交 `a0c2f23` 及当时的权限修复工作树改动（耗材、人事、设备资源显式实现通用详情/原始列表方法，以及应用本地查询权限守卫）。
- 历史构建产物：`project/target/gikam-third-czjky-14.1.5-run.jar`；宿主与容器内 SHA-256 均为 `7e97fefb81417fa10c35f1d49b54ed5ec8debb0121326874c780a3cc7a96dbdb`。
- 镜像：`lims-app:14.1.5-built`；容器仅接入 `lims-iso`，宿主入口 `http://127.0.0.1:8081/`。最终复验时 `bash scripts/isolation-check.sh evidence/t03-05-isolation-final.txt` 为 PASS。
- 演示副本采集基准：`private/t01-demo-rows.txt`（2026-09-23 记录）；当前隔离 Oracle 只读核对为 `T_EMC_CONSUME=206`、`T_EMC_CSM_STOCK=157`、`T_EMC_CSM_STOCK_LOG=278`。
- 参照样本：耗材编码 `<restricted-consumable-code>`，名称“<restricted-consumable>”，规格“<restricted-specification>”，页面库存数量 `965`；隔离 Oracle 对应 1 个库存行、库存合计 965、2 条库存历史记录。样本与页面核对时间：2026-09-27 18:34（+08:00）左右。
- 数据复制是演示范围，不是生产全量：权限/菜单等基础链路全量，其余业务表按脚本口径限量；该限制及未复制附件范围见 `evidence/t01-material-integrity.md`。

## 授权账号真实页面

浏览器会话：`consumables-final-auth-20260927`。账号 `<authorized-account>`（<restricted-person>，具备耗材查询角色）。未点击新增、删除、批录入、入库、盘点、废弃、领取、导入、上传或保存。

| 检查 | 预期 | 实际 | 证据 |
| --- | --- | --- | --- |
| 登录与菜单 | 账号能登录并看到耗材查询入口 | 登录成功；「全面资源管理」显示「耗材管理」，展开后显示「耗材查询」 | 受限截图（会话归档，文件名不公开） |
| 初始列表 | 显示耗材列表及已有字段 | 页面显示 `共206条`；列为材料编码、材料名称、规格型号、库存数量、材料类型、存放位置、货号、供应商；首行为受限耗材样本 | 受限截图（会话归档，文件名不公开） |
| 页面分页 | 若页面存在分页，应能切换页码并改变记录范围 | 未发现分页控件；DOM 中 206 条记录全部呈现，未出现页码、上一页、下一页或页大小控件。源码配置也未启用 `page: true` | 上述列表截图；`project/src/main/resources/static/emc/module/item/consume/consumes/emc-consume-edit-list.js` |
| 匹配筛选 | 使用已有筛选返回匹配结果 | 在“材料名称”筛选输入受限耗材样本后回车，显示 `共1条`，唯一行库存为 `0` | 受限截图（会话归档，文件名不公开） |
| 无匹配筛选 | 使用已有筛选返回空结果 | 输入 `__t05_no_match_20260927__` 后显示 `共0条` 与“暂无数据”；列表没有残留匹配行（右侧二维码详情保留旧选中项是组件现有行为） | 受限截图（会话归档，文件名不公开） |
| 详情与库存 | 能打开详情并核对页面实际展示字段 | 选中受限耗材样本后，详情、库存页签和历史记录均可读；库存页签显示 `共1条`、当前数量 `965`，历史记录显示 `共2条` | 受限截图（会话归档，文件名不公开） |

库存信息只按页面现有列和记录核对，没有新增库存计算口径；数据库的 965 合计仅用于核对页面样本。

## 低权限直接访问检查

浏览器会话：`consumables-final-low4-20260927`。账号为受限低权限账号（数据库角色仅“项目查询”）。登录后的真实首页顶级菜单只有「主业务流程」，没有「全面资源管理」/「耗材管理」。截图保存在受限会话归档中，文件名不公开。

在同一已登录会话中，只读请求以下路径，均返回 HTTP 400 JSON，消息为 `GIKAM.PERMISSION.UNAUTHORIZED`，没有返回耗材业务数据：

| 请求 | 实际 |
| --- | --- |
| `GET /secure/emc/module/item/consume/consumes/page/edit-list` | 400，未返回页面壳 |
| `GET /secure/emc/module/item/consume/consumes/{restricted-consumable-id}` | 400，未返回详情 |
| `POST /secure/emc/module/item/consume/consumes/queries/raw`，body `{"f":{"readonly":"1"},"n":1,"s":100` | 400，未返回列表 |
| `POST /secure/emc/module/item/consume/consumes/{restricted-consumable-id}/stocks/queries` | 400，未返回库存 |
| `POST /secure/emc/module/item/consume/consumes/stocks/{restricted-stock-id}/logs/queries` | 400，未返回历史 |

该结果是在当时记录的 SHA-256 产物、容器和隔离数据库上取得；没有修改账号、角色、密码或业务数据。此前旧构建曾出现直接路由返回业务数据的旁路，该历史守卫复验确认当时差异已关闭。

## 可复现步骤与预期/实际

1. `bash scripts/app-ctl.sh status`，确认 `lims-app`、Oracle、Redis 运行；核对宿主/容器 JAR SHA-256。
2. `bash scripts/isolation-check.sh evidence/t03-05-isolation-final.txt`，预期 PASS；实际 PASS。
3. 用 `<authorized-account>` 登录 `http://127.0.0.1:8081/`，展开「全面资源管理 → 耗材管理 → 耗材查询」；预期入口和列表可见，实际可见。
4. 记录默认列表总数/字段；在“材料名称”列执行匹配和无匹配筛选；预期结果分别为 1 行和 0 行，实际如此。
5. 以编码 `<restricted-consumable-code>` 选中行，核对详情、库存和历史；预期页面可读且字段与隔离 Oracle 样本一致，实际如此。
6. 用 `<low-privilege-account>` 登录后，分别请求上表五个只读路径；预期拒绝且无业务数据，实际全部 400 `GIKAM.PERMISSION.UNAUTHORIZED`。

## 数据变化与未解决差异

- 数据变化：0。仅执行登录、菜单、列表、筛选、选中行、详情读取和 HTTP 只读探针；未执行任何写入按钮或写入 HTTP 方法。
- 已解决差异：最终守卫关闭了低权限用户对耗材页面、详情、原始列表及库存相关只读接口的直接访问旁路。
- 分页适用性：耗材查询页面没有分页控件/分页配置，故不存在分页切页、记录范围变化或页码条数可验收；该结果已按 AC-08 的“适用的分页行为”记录为不适用。
- 其他演示副本限量、附件未恢复和未验收写入流程不在本任务范围；详见任务 06/07 与 `evidence/t01-material-integrity.md`。

## 2026-09-28 historical runtime addendum (c3f4)

- At 10:48-10:55 +08:00, the host artifact and the running `lims-app` copy both had SHA-256 `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`. `evidence/t07-isolation-c3f4.txt` records the isolated boundary PASS; no production endpoint or write operation was used.
- In a new real-page session `finalc3f4-consumables`, the authorized account opened `全面资源管理 → 耗材管理 → 耗材查询`. The page rendered `共206条`, the `材料名称（构建验证）` marker, known list samples, and no pager; screenshots remain in the restricted session archive and filenames are not published.
- In separate low-privilege session `finalc3f4-low`, the account had no resource-management menu. The consumables page/detail/raw-read, stock, and stock-history read probes all returned HTTP 403, `application/json;charset=UTF-8`, and `{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}`, with no consumable data.
- This is a runtime/hash and guard recheck. The earlier match/no-match, selected-detail, stock, and history walkthrough remains historical evidence at its recorded hash and is not relabeled as c3f4 observation by this addendum.

The active nested source `06c2bcf` (based on `dd27bb7`) and its local build output have not been deployed or browser-tested. They cannot inherit this historical runtime result.
