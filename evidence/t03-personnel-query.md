# 任务 03 人事查询验收记录

日期：2026-09-27（Asia/Singapore）。本记录只覆盖隔离副本；生产未访问、未写入。页面操作只包括登录、菜单、列表、筛选、分页和查看详情，未点击新增、删除、导入、上传或保存。

## 结论

任务 03 的历史运行验收通过。授权账号能从真实页面进入人员管理，完成列表、已有筛选、无匹配结果、分页和详情核对；低权限账号没有人员菜单，且不能通过人员页面、详情或原始列表端点读取人员数据。该结论绑定本记录所列的历史 JAR，不代表当前 `06c2bcf`（基于 `dd27bb7`）的本地构建输出已重新运行。

本结论只覆盖 AC-04 的人事查询数据切片；完整数据和附件 AC-04 仍由任务 06 保持开放。

此前 `096822...` 构建中的低权限详情旁路不作为本结论证据。该旁路已由当时构建中的查询权限守卫关闭，并在本记录所列的历史 JAR 上重新验证。

## 历史运行与数据基准

- 嵌套工程基线：提交 `36dc967`（查询路由权限守卫及隔离附件签名）；本记录绑定的历史 JAR 还包含 AC-09 的仅展示文字改动，未改变人员查询行为。
- 历史构建产物：`project/target/gikam-third-czjky-14.1.5-run.jar`，SHA-256 `ce1b81945e47f878e7ce7500221b7abe6661b4871f28b12ce9a339f978a38a52`。
- 容器：`lims-app:14.1.5-built`，容器 ID `597705e4d83f`，于 `2026-09-27T13:10:21Z` 启动；容器内同路径 JAR SHA-256 与宿主一致。`logs/app.log` 记录 `Started GikamWpjcApplication in 95.697 seconds`。
- 隔离边界：应用、Oracle、Redis 仅接入 internal 网络 `lims-iso`，宿主仅通过 `127.0.0.1:8081` 转发访问。`bash scripts/isolation-check.sh evidence/t03-personnel-isolation-ce1.txt` 于 21:17 +08:00 通过，外部/生产端点不可达、隔离 Oracle/Redis 可达。
- 数据基准：隔离 Oracle 演示副本（只读复核于 21:09 +08:00）中 `T_EMC_EMP=482`；人员页面已有 `userOrg_filter=1` 组织过滤，页面应显示 `323` 条，实测一致。
- 参照样本：`<authorized-account> / <restricted-person>`。只读联表确认用户为激活状态（`LASTSUSPENDEDFLAG=0`），员工组织为 `<restricted-department-id>`、`REPORTISSUE=1`；当时授权详情接口和页面显示受限部门和组织字段，与该隔离数据样本一致。

## 授权账号真实页面

浏览器会话：`t03-personnel-ce1-auth-20260927`。账号 `<authorized-account>`（<restricted-person>，具备人员管理菜单）。

| 检查 | 预期 | 实际 | 证据 |
| --- | --- | --- | --- |
| 登录与菜单 | 有权限账号能看到人员管理入口 | 登录后首页显示受限账号身份；展开“全面资源管理”可见“人员管理” | 受限截图（会话归档，文件名不公开） |
| 初始列表 | 显示既有列表字段和分页 | 页面显示序号、用户名、姓名、部门、岗位、状态；`共 323 条`，`50条/页`，`/ 7`，第一页首行为受限演示账号 | 受限截图（会话归档，文件名不公开） |
| 匹配筛选与详情 | 现有用户名筛选返回参照人员及关键详情 | 输入受限授权账号并回车后仅 1 条；右侧基本信息显示用户名、姓名、科室、单位和报告签发人字段 | 受限截图（会话归档，文件名不公开） |
| 无匹配筛选 | 不保留旧列表记录 | 输入 `__t03_no_match_20260927_ce1__` 后显示“暂无数据”和 `共 0 条`；列表无数据行。右侧保留先前选中详情是既有组件行为，不是列表匹配结果 | 受限截图（会话归档，文件名不公开） |
| 分页 | 既有下一页控件改变页码和记录范围 | 清空筛选后第 1 页为受限演示账号；点击“下一页”后页码为 2，仍为 `共 323 条 / 7`，首行变为另一受限样本 | 受限截图（会话归档，文件名不公开） |
| 详情页签 | 已有详情可读 | 页面呈现基本信息、人员培训、资格证书、上岗证、可见测试、附件页签；本次未操作其中任何写入控件 | 上述筛选详情截图 |

授权详情辅助核对：同一浏览器会话的只读人员详情 GET 返回 HTTP 200，字段包括受限账号、姓名、部门、单位、激活状态和报告签发人标记。该接口只用于核对真实页面所示字段，不替代页面验收。

## 低权限直接访问检查

浏览器会话：`t03-personnel-ce1-low-20260927`。隔离 Oracle 只读核对显示 `<low-privilege-account> / <restricted-person>` 为激活账号，仅有“项目查询”角色，人员管理角色计数为 0；人员菜单为 `100001`。

- 真实登录后首页顶级菜单只有“主业务流程”，没有“全面资源管理”或“人员管理”。截图保存在受限会话归档中，文件名不公开。
- 在同一已认证浏览器会话中直接导航到人员查询页面，浏览器显示 `{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}`，而不是页面壳或人员数据；截图保存在受限会话归档中。
- 同一会话的只读探针全部返回 HTTP 403、`application/json;charset=UTF-8` 和同一未授权消息，没有业务数据：

| 请求 | 结果 |
| --- | --- |
| `GET /secure/emc/module/mdm/emp/emps/page/edit-list` | 403，拒绝页面入口 |
| `GET /secure/emc/module/mdm/emp/emps/{restricted-person-id}` | 403，拒绝另一人员详情 |
| `POST /secure/emc/module/mdm/emp/emps/queries/raw`，body `{"f":{"readonly":"1"},"n":1,"s":100}` | 403，拒绝原始列表 |

上述端点由 `QueryAccessFilter` 按人员管理菜单 `100001` 守卫；本任务仅验收查询入口、详情和原始查询，未对人员新增或其他写入路由发请求。

## 复现步骤与范围

1. 运行 `bash scripts/app-ctl.sh status`，核对容器运行；分别对宿主和容器内 JAR 运行 `sha256sum`，预期均为本记录的 `ce1b8194...`。
2. 运行 `bash scripts/isolation-check.sh evidence/t03-personnel-isolation-ce1.txt`，预期 PASS；实际 PASS。
3. 以 `<authorized-account>` 从 `http://127.0.0.1:8081/` 登录，依次执行菜单、初始列表、`<authorized-account>` 匹配、无匹配和下一页操作，预期及实际如上表。
4. 以 `<low-privilege-account>` 登录，确认菜单范围，再直接导航/只读请求三个人员查询路径，预期及实际均为 403 且不返回人员数据。

## 数据变化与限定

- 数据变化：0。本记录没有业务写入、账号/角色/口令变更、文件上传或生产访问。
- 数据是隔离演示副本，非持续变化的生产实时快照；因此只对同一隔离基准的页面、详情和只读参照样本作一致性判断。
- `<authorized-account>` 的受限签名附件样本已随隔离环境存在，且当前详情中可见签名；文件下载/附件完整性验收仍归任务 06，本任务不把它扩展为附件交付结论。
- 人员新增和其他写入流程不在任务 03 范围；无未解决的人事查询差异。

## 2026-09-28 historical runtime addendum (c3f4)

- At 10:48-10:55 +08:00, the host artifact and the running `lims-app` copy both had SHA-256 `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`. `evidence/t07-isolation-c3f4.txt` records a PASS at 10:18 +08:00; no production endpoint or write operation was used.
- In a new real-page session `finalc3f4-personnel`, the authorized account opened personnel management. The list, match/detail, and no-match states were captured in the restricted session archive; filenames are not published. The match showed one restricted sample row; the no-match showed `暂无数据` and `共 0 条`.
- In separate low-privilege session `finalc3f4-low`, the account showed only `主业务流程`. Direct page navigation and the personnel page/detail/raw-read probes each returned HTTP 403, `application/json;charset=UTF-8`, and `{"message":"GIKAM.PERMISSION.UNAUTHORIZED"}` with no personnel data. Screenshots remain in the restricted session archive.
- This is a runtime/hash and guard recheck. The earlier full pagination sequence remains historical evidence at its recorded hash and is not relabeled as a c3f4 observation by this addendum.
