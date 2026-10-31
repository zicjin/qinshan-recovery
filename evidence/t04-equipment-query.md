# 任务 04：设备查询验收记录

日期：2026-09-27（Asia/Singapore）。本记录取代同日旧构建 `096822...` 上的失败权限探针；以下结论只基于当时 AC-09 产物及其历史运行容器。

## 结论

设备查询已在本记录所列的历史源码构建、隔离运行产物上通过验收。授权账号能从真实菜单进入仪器查询，查看列表、已有筛选、无匹配状态、分页和详情；低权限账号没有该入口，且不能通过页面、详情或原始列表接口读取设备数据。测试全程未使用新增、删除、导入、上传、保存或任何写入 HTTP 操作。本结论只覆盖 AC-04 的设备查询数据切片；完整 AC-04 仍由任务 06 保持开放。

## 运行产物、边界与数据基准

- 历史嵌套源码工程 HEAD：`36dc967 保护查询路由与隔离附件签名`。历史 JAR：`project/target/gikam-third-czjky-14.1.5-run.jar`，SHA-256 `ce1b81945e47f878e7ce7500221b7abe6661b4871f28b12ce9a339f978a38a52`。
- 当时容器：`lims-app`，镜像 `lims-app:14.1.5-built`，于 2026-09-27 21:10:21 +08:00 启动；容器内同路径 JAR 的 SHA-256 与宿主产物一致。启动日志已出现 Spring Boot banner，真实页面登录和以下请求均成功完成。
- `bash scripts/isolation-check.sh /tmp/t04-ce1b-isolation.txt` 于 21:17 +08:00 为 PASS：`lims-app`、Oracle、Redis 仅接入 internal `lims-iso`；生产 Oracle、文件服务、FTP、OA、报表、NC、邮件和其他外部端点均不可达，隔离 Oracle/Redis 可达。生产没有被访问或写入。
- 演示副本基准为 `private/t01-demo-rows.txt`（2026-09-23，`T_EMC_EQUIPT` 限量 100 行）。当前隔离 Oracle 的只读复核仍为 `T_EMC_EQUIPT=100`；页面按既有单位/类别权限过滤后显示 `共 72 条`，两者不应混同。
- 参照设备为隔离 Oracle 的 `id=<restricted-equipment-id>`：`<restricted-equipment-code> / <restricted-equipment-name> / <restricted-equipment-model> / inUse / 自有`。同名设备共 5 条；均与页面筛选结果一致。该记录的类别关联在数据库中为“实验”，页面也显示“实验”。`equiptEnglishName` 与 `factoryDate` 是 SQL `NULL`，详情表单显示为空白；这类正常空值与无匹配筛选的“暂无数据 / 共 0 条”状态分开记录，未见关联缺失错误。

## 授权账号的真实页面检查

浏览器会话：`equipment-ce1b-auth-<authorized-account>`。账号 `<authorized-account>`（<restricted-person>）；所有行为均为登录、菜单、筛选、选中行、分页和只读查看。

| 检查 | 预期 | 实际 | 证据 |
| --- | --- | --- | --- |
| 菜单入口 | 授权账号能看到并打开仪器查询 | 登录成功；展开“全面资源管理 → 仪器管理”后可见并打开“仪器查询” | 受限截图（会话归档，文件名不公开） |
| 初始列表 | 设备列表和已有字段可读 | 页面显示 72 条；列表包含站内编号、资产编号、资产 ID、仪器名称、规格型号、仪器状态、仪器类型、使用科室、仪器归属等字段；初始为 100 条/页 | 受限截图（会话归档，文件名不公开） |
| 匹配筛选 | 已有名称筛选返回对应设备 | 在“仪器名称”输入受限设备名称并回车，显示 `共 5 条`；五行均为该名称，且规格一致 | 受限截图（会话归档，文件名不公开） |
| 无匹配筛选 | 不存在的名称返回空集合 | 输入 `__t04_no_match_20260927__` 后，主列表 0 行，显示“暂无数据”和 `共 0 条`；旧详情被清空 | 受限截图（会话归档，文件名不公开） |
| 分页 | 已有分页能改变记录范围 | 将页大小改为 50 后，第 1 页显示 50 行、`/ 2`、共 72 条；点击“下一页”后为第 2 页 22 行，首行和末行均改变，下一页按钮禁用 | 受限截图（会话归档，文件名不公开） |
| 详情 | 选定设备关键字段可读且与参照样本一致 | 选中受限设备样本后，基本信息显示名称、规格、生产厂家、仪器状态、仪器类型和仪器归属；既有 DCU 配置、仪器测试、校准、使用记录、附件等页签也出现 | 受限截图（会话归档，文件名不公开） |

## 低权限直接访问复验

浏览器会话：`equipment-ce1b-low`。账号为受限低权限账号（只有“项目查询”角色）；真实首页仅显示“主业务流程”，没有“全面资源管理”“仪器管理”或“仪器查询”。截图保存在受限会话归档中，文件名不公开。

在同一已登录低权限浏览器上下文发出以下只读请求。全部得到 HTTP 403、`application/json;charset=UTF-8`、`{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}`，未返回页面壳、设备详情或列表数据：

| 请求 | 实际 |
| --- | --- |
| `GET /secure/emc/module/mdm/equipt/equipts/page/edit-list` | 403，拒绝页面入口 |
| `GET /secure/emc/module/mdm/equipt/equipts/{restricted-equipment-id}` | 403，拒绝受限设备详情 |
| `POST /secure/emc/module/mdm/equipt/equipts/queries/raw`，body `{"f":{"readonly":"1"},"n":1,"s":100}` | 403，拒绝原始列表 |

该复验关闭了旧构建中这三条路径的 200/数据泄露旁路。请求只读取隔离副本；未改变账号、角色、密码或业务数据。

## 可重复步骤

1. `sha256sum project/target/gikam-third-czjky-14.1.5-run.jar`，并以 `docker exec lims-app sha256sum /data/LIMS/jiankeyuan/lims-czjky-emc/gikam-third-czjky-14.1.5-run.jar` 核对容器副本；预期均为本记录的 `ce1b8194...`，实际一致。
2. `bash scripts/app-ctl.sh status` 和 `bash scripts/isolation-check.sh /tmp/t04-ce1b-isolation.txt`；预期应用、Oracle、Redis 和隔离边界正常，实际均通过。
3. 用 `<authorized-account>` 登录 `http://127.0.0.1:8081/`，打开“全面资源管理 → 仪器管理 → 仪器查询”；记录列表及 `<restricted-equipment-code>` 详情。预期入口、72 条受限页面列表和参照字段可读，实际如此。
4. 在“仪器名称”执行 `<restricted-equipment-name>` 与 `__t04_no_match_20260927__` 筛选；预期依次为 5 行和 0 行/“暂无数据”，实际如此。
5. 清空筛选，选 50 条/页，点击下一页；预期页码变为 2 且记录范围变为 22 行，实际如此。
6. 用 `<low-privilege-account>` 登录，确认没有仪器入口；在同一会话仅以 `GET`/只读 `POST` 请求上表三条路径。预期 403 且没有业务数据，实际三条均为 403 `GIKAM.PERMISSION.UNAUTHORIZED`。

## 数据变化与范围

- 数据变化：0。页面操作仅是查询；接口探针也仅是详情与带 `readonly=1` 的列表读取。
- 本任务不验收设备新增、采购、维护、校验、采集写入或附件独立访问；后者由任务 06 负责。
- 演示数据仍是限量副本而非生产全量。该限制不影响本记录中的页面、筛选、分页、详情和权限验收结论。

## 2026-09-28 historical runtime addendum (c3f4)

- At 10:48-10:55 +08:00, the host artifact and the running `lims-app` copy both had SHA-256 `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`. The then-current isolated boundary is independently recorded as PASS in `evidence/t07-isolation-c3f4.txt`; no production endpoint or write operation was used.
- In a new real-page session `finalc3f4-equipment`, the authorized account opened instrument query. The 72-row list, 50-per-page control, name match, and no-match state were captured in the restricted session archive; filenames and record identifiers are not published.
- In separate low-privilege session `finalc3f4-low`, the account had no resource-management menu. The equipment page/detail/raw-read probes each returned HTTP 403, `application/json;charset=UTF-8`, and `{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}`, with no equipment data.
- This is a runtime/hash and guard recheck. The earlier two-page pagination and detailed field walkthrough remain historical evidence at their recorded hash and are not relabeled as c3f4 observations by this addendum.

The active nested source `06c2bcf` (based on `dd27bb7`) and its local build output have not been deployed or browser-tested. They cannot inherit this historical runtime result.
