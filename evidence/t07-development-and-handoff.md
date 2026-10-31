# 任务 07 二次开发与交接记录

记录更新至 2026-10-12。本记录覆盖 AC-09 的源码小改动、构建、隔离部署和真实页面结果，
以及 2026-10-12 同机独立回放；正式 AC-10 的独立交接仍有明确未完成项。

## AC-09 二次开发验证

### 修改与回退

- 源码：`project/src/main/resources/static/emc/module/item/consume/consumes/emc-consume.js`。
- 修改：耗材查询表的材料名称列从 i18n 键 `T_EMC_CONSUME.MTLNAME` 改为用户可见文本
  `材料名称（构建验证）`。
- 选择理由：这是已验收耗材列表中可见的只读表头，不改变查询条件、接口、库存计算或业务数据。
- 回退：将该一行恢复为 `title : 'T_EMC_CONSUME.MTLNAME',`，重新执行本节构建和部署步骤。

### 构建与部署

- 嵌套源码基线：`36dc967` 加上本次未提交的一行 AC-09 表头改动。
- 构建入口：`bash scripts/build.sh`，实际 `BUILD SUCCESS`。
- 产物：`project/target/gikam-third-czjky-14.1.5-run.jar`，SHA-256
  `ce1b81945e47f878e7ce7500221b7abe6661b4871f28b12ce9a339f978a38a52`。
- 部署：`docker build -f docker/Dockerfile.built -t lims-app:14.1.5-built .`，随后
  `IMAGE=lims-app:14.1.5-built bash scripts/app-ctl.sh restart`。
- 容器内同名 JAR 的 SHA-256 与宿主产物相同；日志记录 Tomcat 8081 和
  `Started GikamWpjcApplication in 95.697 seconds`。
- 部署后隔离检查：`bash scripts/isolation-check.sh evidence/t06-isolation-final.txt` 为 PASS。

### 真实页面结果

1. 以工号 `00550` 登录最终隔离副本，进入「全面资源管理 -> 耗材管理 -> 耗材查询」。
2. 页面表头显示 `材料名称（构建验证）`，同时列表仍显示 `共206条` 和既有耗材数据。
3. 截图（受限业务数据）：`private/screenshots/t07-ac09-consumables-header-final.png`。

预期为源码中的单行可见文本经构建、镜像替换和启动后出现在真实查询页面；实际结果一致。
未执行新增、删除、库存操作、导入、上传或任何生产写入。

## AC-10 交接状态

当前已具备可重复的构建、隔离配置、材料校验、数据库演示副本、样本附件恢复、启动和页面验收脚本。
完整交接仍未完成，因为尚未在另一份独立工作目录中进行完整重建，也尚未建立可移交的受限
`materials/`/`private/` 安全交付包。`project` 是嵌套 Git 仓库且其远程为受限地址，交接者还需获得
明确的源码和受限材料取得渠道。

现有步骤和限制应以 `scripts/verify-materials.sh`、`scripts/build-prep.sh`、
`scripts/build.sh`、`scripts/make-isolated-config.sh`、`scripts/restore-attachment-sample.sh`、
`scripts/stack-isolate.sh`、`scripts/isolation-check.sh`、`scripts/app-ctl.sh` 及任务 01-06
证据为准。不得把演示数据副本、单个附件样本或三类查询验收表述为全量生产数据/附件或全部业务功能
的一致性保证。

## 最终运行绑定（2026-09-28）

- 最终嵌套源码提交：`5447e439dc9a89014b03f759504ea0844b6856ff`，其中包含 AC-09 表头改动、查询
  权限守卫及隔离人员签名附件的 URL/路径规范化守卫。
- 最终构建和隔离部署产物：`project/target/gikam-third-czjky-14.1.5-run.jar`，宿主与运行容器的
  SHA-256 均为 `c3f4c5eecccaad8003d6a775c3f02de80a1323b70eb29814edd783046564ecf1`。
- 启动日志记录 `Started GikamWpjcApplication in 106.812 seconds`；
  `bash scripts/isolation-check.sh evidence/t07-isolation-c3f4.txt` 为 PASS。
- 新的真实页面复验仍显示 `材料名称（构建验证）`、206 条耗材和无分页控件；三类查询与附件授权
  的精确运行绑定分别见 `evidence/t03-personnel-query.md`、`t04-equipment-query.md`、
  `t05-consumables-query.md`、`t06-data-and-attachments.md`。

该补记更新 AC-09 的最终可运行产物绑定，不改变 AC-10 的未完成判定。2026-10-12 的同机 Docker 回放
已执行并记录于 `evidence/t07-ac10-replay-20261012.md`；它证明了新恢复卷和新资源命名下的启动路径，
但尚未在另一名开发者的独立工作目录/主机中完成完整交接验收。
