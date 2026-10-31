# 任务 02 产物替换、启动登录与隔离复验记录（AC-03 源码构建部分、AC-05 登录部分）

日期：2026-09-25。对象：本批次源码构建产物 `project/target/gikam-third-czjky-14.1.5-run.jar`（SHA-256 `0536d2e257535a2bdc5c0fcf7dc218247a9506c3ed30badfaf7ce0ee1fd1c24c`，3,035 源文件 / 1,507 资源，见 `evidence/t02-build.md`）。

## 1. 产物替换与启动确认（条目 5）

- 历史镜像流程（当前 Dockerfile.built 已 supersede）：当时在运行参照镜像 `lims-app:14.1.5` 之上仅替换应用 jar，重建 `lims-app:14.1.5-built`；`IMAGE=lims-app:14.1.5-built bash scripts/app-ctl.sh restart` 重启 `lims-app`。
- 容器内 jar 与宿主产物同值（均为上述 SHA-256）；容器 IP `172.18.0.4`（Oracle `172.18.0.2`、Redis `172.18.0.3`，网络 `lims-iso`）。
- 应用日志在容器内 `logs/app.log`（stdout 仅有 banner，属已知形态）：`Tomcat started on port(s): 8081`、`Started GikamWpjcApplication in 97.74 seconds`；agent-browser 直接加载 `http://172.18.0.4:8081/` 得到登录页并完成真实登录。宿主转发器仍由 `scripts/app-ctl.sh` 管理（`127.0.0.1:8081 -> 172.18.0.4:8081`，pid 见 `private/t01-portfwd.pid`）。
- 结论：运行的即此次生成的最终产物；启动正常。

## 2. 真实页面登录与授权入口（条目 5）

- 方式：agent-browser 会话 `lims-accept-20260925z` 驱动 Chrome 加载隔离容器地址 `http://172.18.0.4:8081/`，执行页面自身的 RSA 登录流程。
- 账号：工号 `<authorized-account>`（姓名<restricted-person>，持有人员管理/仪器查询/试剂耗材查询等角色；见 `private/test-account.env`，口令为受限材料，此处不记录）。注意登录框要求填工号（ID/USERNO），填姓名会被 `WHERE ID=? OR USERNO=?` 判无用户而 401；此前一次填姓名失败即为此因，非产物缺陷。
- 结果：登录成功，进入主界面，右上角显示<restricted-person>；「全面资源管理」下可见人员管理、仪器管理、耗材管理（另有标样试剂管理、材料领用等）；人员管理模块可打开。
- 截图保存在受限会话归档中，文件名不公开；此前旧构建 jar 的截图 `t02-built-login.png` 作废，不作为历史证据。
- 结论：最终产物登录与授权入口正常。

## 3. 隔离复验（条目 6）

- `bash scripts/isolation-check.sh` 结果 PASS：应用与数据库容器仅接入 `lims-iso`，无法连通生产 Oracle/文件服务/FTP/OA/帆软/NC/客商/外部邮件/历史测试库；到隔离 Oracle/Redis 的正向对照连通。
- 操作记录追加于 `private/t01-run-record.txt`（app-ctl 自动记录）。

## 4. 范围声明

- 本节早期 checkpoint 记录当时产物的启动、登录和隔离结果；最终源码恢复批次的结果见下节。三类查询的内容正确性、附件、二次开发分别见任务 03-07。

## 历史源码恢复批次复验（2026-09-27）

- 历史嵌套源码提交：`a0c2f23`；构建入口 `bash scripts/build.sh` 成功。
- 历史产物 SHA-256：`096822b2ce656521a389fde447eb603049c634d6bc8c1082f060bff2d521158a`；容器内同名 jar SHA-256 一致。
- 镜像：`lims-app:14.1.5-built`，镜像 ID `sha256:64140607f61009191a8c2446c79cf475a634d53da4853444e1ded40088327b6c`；容器仅接入 `lims-iso`，IP `172.18.0.4`。
- 日志确认 `Tomcat started on port(s): 8081` 与 `Started GikamWpjcApplication in 107.166 seconds`。
- `bash scripts/isolation-check.sh evidence/t02-isolation-check-final.txt` PASS；应用和 Oracle 无法触达生产或外部端点，隔离 Oracle/Redis 正向连通。
- agent-browser 会话 `lims-t02-final-96f58edfce14` 以工号 `<authorized-account>` 完成真实页面登录；首页显示<restricted-person>；「全面资源管理」下人员、仪器、标样试剂、耗材和材料领用入口可见；人员管理列表可打开。
- 截图保存在受限会话归档中，文件名不公开。
- 该历史清单 `changed=0`；三类查询的内容验收仍由任务 03-05 覆盖。

## 5. 增量恢复 checkpoint（2026-09-25 08:31 +08:00）

- 当前源码提交：嵌套工程 `e90f79c`（`对齐报告指派与页面契约及生产语义差异`）；`scripts/build.sh` BUILD SUCCESS。
- 当前产物 SHA-256：`0536d2e257535a2bdc5c0fcf7dc218247a9506c3ed30badfaf7ce0ee1fd1c24c`；隔离容器内同名 jar SHA-256 一致。
- 当前容器已用 `lims-app:14.1.5-built` 重启，日志出现 `Started GikamWpjcApplication in 100.977 seconds`，agent-browser 会话 `lims-t02-96f58edfce14` 以工号 `<authorized-account>` 直接访问宿主转发地址并完成登录；全面资源管理下人员管理、仪器管理、标样试剂管理、耗材管理和材料领用入口可见，人员管理入口可打开。
- 当前隔离边界复验：`bash scripts/isolation-check.sh` PASS（2026-09-25 07:53）。
- 当前源码语义残留为 47 个 changed class；本次会话已针对当前容器产物以工号 <authorized-account> 登录成功。

## 增量部署 checkpoint（2026-09-25 16:45 +08:00）

- 以最新产物重建镜像 `lims-app:14.1.5-built` 并重启隔离容器；宿主与容器内应用 jar SHA-256 均为 `13374b848865828e0049458e52771c1e381f2a1c932b287041722fb34ed5ec1a`。
- 容器日志确认 `Started GikamWpjcApplication in 102.297 seconds`；`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 通过真实页面以授权账号登录成功，首页显示受限身份与「全面资源管理」入口；截图保存在受限会话归档中，文件名不公开。
- 本次只复验启动、登录、授权入口和隔离边界；41 个 changed class 仍未完成语义恢复，业务查询内容仍由任务 03-05 验收。


## 增量部署 checkpoint（2026-09-25 16:53 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `eb5a6e872711ba42a8d278beb762adf03ef820c21ce78519bd1d6410102f255b`。
- 日志确认 Tomcat 8081 与 `Started GikamWpjcApplication`；`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录，首页显示受限身份及「全面资源管理」入口；截图保存在受限会话归档中，文件名不公开。


## 增量部署 checkpoint（2026-09-25 17:16 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `515f437ae0f76d1255769db8c1ff36db1d089e7afc5519738acf6e6d1524deb9`。
- 日志确认应用启动；`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录，首页显示受限身份及「全面资源管理」入口；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 18:18 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `22d2d13b481165f842763b96c7d944eae091ff5884d34d22881ab27d3fbf90a5`。
- 日志确认应用启动；`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录，首页显示受限身份及「全面资源管理」入口；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 18:34 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `8ffa47bc3d377c60964ca48900f9569d28e14b64aa9f46bbfb576629a1fa706d`。
- 日志确认应用启动；`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录，首页显示受限身份及「全面资源管理」入口；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 18:50 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `d0ca9a4d0385dfd27e07205c966298b834c1685eac48bc12660973ee65138826`。
- 日志确认 `Started GikamWpjcApplication`；转发器恢复后页面返回 200，`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 20:41 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `beaab931d732549c554dcb72a14c7f03f3d192bc0ed5ee438dbbe91485d7b874`。
- 日志确认应用启动；转发器恢复后页面返回 200，`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 21:17 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `3cb2f41e39e25fe08985276cec381babcf21ac5b42da3eb19ae57ed1c55be722`。
- 日志确认应用启动；转发器恢复后页面返回 200，`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 21:27 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `c6944fb3fe7b0fd10d0d60e90dff7a9a852730257394facbd2ccc9d32c102ba2`。
- 日志确认应用启动；转发器恢复后页面返回 200，`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 21:45 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `7e59f4b849053a95156c7a1bfdbb5788421160f4625ef308cead20de45caeb0f`。
- 日志确认应用启动；转发器恢复后页面返回 200，`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录；截图保存在受限会话归档中，文件名不公开。

## 增量部署 checkpoint（2026-09-25 21:48 +08:00）

- 镜像 `lims-app:14.1.5-built` 已用最新产物重建并重启；宿主与容器内 jar SHA-256 均为 `90641e6b7981cbd8649e8e8278ef5af8ba34a9d1817d5123701bde77430ba04f`。
- 日志确认应用启动；转发器恢复后页面返回 200，`bash scripts/isolation-check.sh` PASS。
- agent-browser 会话 `lims-t02-latest-96f58edfce14` 完成真实页面登录；截图保存在受限会话归档中，文件名不公开。

## 2026-09-26 当前产物隔离部署复验

- Docker daemon 访问恢复；隔离 Oracle、Redis 已启动，Oracle 仅接入 `lims-iso`。
- 从嵌套源码提交 `a95185b` 构建的 jar SHA-256 为 `93d1bf7fc1e53bf01f034dbac5a320cbaf66cc501b757dfbf4c710ce9498eed6`；镜像 `lims-app:14.1.5-built` 重建，容器内 jar 校验值与该 SHA 一致。
- 容器日志出现 Tomcat 8081 与 `Started GikamWpjcApplication in 86.466 seconds`；`scripts/isolation-check.sh` PASS，应用和 Oracle 仅在内部网络，外部端点不可达，隔离 Oracle/Redis 可达。
- agent-browser 会话 `t02login-96f58edfce14` 经真实页面登录成功，首页显示受限身份。展开「全面资源管理」后可见人员管理、仪器管理、标样试剂管理、耗材管理和材料领用；进入人员管理后可见人员列表与基本信息详情。截图保存在受限会话归档中，文件名不公开。
- 该批次源码语义仍有 26 个 changed class，条目 2 不标记完成。后续源码修复将产生新 SHA，需对最终产物再次执行本节的部署和登录复验。

## 2026-09-26 当前语义归一化产物复验

- 最新构建产物 SHA-256：`21cbf2410c11680ec63b349639bb1ca691e34f16dd0994a0a0a8dc5f4dec1ac1`；镜像重建后容器内 jar 同 SHA。
- 应用重启并完成 `Started GikamWpjcApplication`；`scripts/isolation-check.sh` PASS。
- agent-browser 会话登录成功，首页显示受限身份；展开「全面资源管理」可见人员管理、仪器管理、标样试剂管理、耗材管理和材料领用。截图保存在受限会话归档中，文件名不公开。
- 当前源码 inventory：changed=26、same-code=256、identical=2,844，资源 changed=0、prod-only=0。

## 2026-09-28 历史安全增量运行绑定（c3f4）

- 当时嵌套源码提交 `5447e439dc9a89014b03f759504ea0844b6856ff` 已重新构建并部署；宿主与容器内
  JAR SHA-256 均为 `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`。
- 日志记录 `Tomcat started on port(s): 8081` 和 `Started GikamWpjcApplication in 106.812 seconds`；
  `bash scripts/isolation-check.sh evidence/t07-isolation-c3f4.txt` 为 PASS。
- 新的真实页面登录/查询与低权限拒绝结果分别补记在任务 03、04、05；签名附件及路径规范化回归补记
  在任务 06。生产未访问、未写入。

本节的 c3f4 历史运行结论不自动传递给后续 `06c2bcf`（基于 `dd27bb7`）。`06c2bcf` 的较早
`72ec...` JAR 已在 2026-09-29 的同机 `r06c2stage` 与 `r06c2final4` 回放中完成窄范围启动、隔离和页面复验，见
`evidence/t07-current-r06c2stage.md`；但三类查询的完整筛选/分页/详情/低权限回归仍保留其历史 SHA 绑定，且最新
`0285f909...` JAR 与最新启动器收紧已在 `r0930final3` 新 restore 卷动态验证；完整附件/交接范围仍未完成。
