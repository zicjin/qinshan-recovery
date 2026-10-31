# `sss2` 观察与代码回归记录

这是 ticket 06 的可重复记录格式。`sss2` 指可读写的测试克隆；生产环境仍然只读。脚本只保存脱敏元数据和证据引用，不读取或复制凭据、请求正文、生产数据库行、附件内容或外部端点。

## 建立观察收据

```bash
cd lims-recovery
bash scripts/sss2-observe.sh init clone-a private/sss2/clone-a fixture-baseline-20261019
bash scripts/sss2-observe.sh record private/sss2/clone-a personnel.query read-flow-verified evidence/t03-personnel-query.md authorized fixture-data
bash scripts/sss2-observe.sh record private/sss2/clone-a tasks.scheduler disabled-in-isolated-env evidence/t06-isolation-current.txt operator fixture-data
```

`init` 只允许测试克隆标识，并写入 `production_read_only=true`、`external_side_effects=blocked` 和基准标签。每项记录必须带能力、角色、数据基准和相对证据引用。状态保留 `unknown` 与 `disabled-in-isolated-env`，隔离环境看不到的行为不能被推断为缺失或通过。

## 代码垂直切片

完成一个经审阅的查询相关小改动并在隔离实例复验后，记录前后源码引用：

```bash
bash scripts/sss2-code-regression.sh private/sss2/clone-a personnel.write \
  <before-source-ref> <after-source-ref> evidence/sss2-code-regression.md
bash scripts/sss2-observe.sh verify private/sss2/clone-a
```

`sss2-code-regression.sh` 不启动应用、不连接数据库、不执行外部命令；它将前后引用和 `write-flow-verified` 状态追加到收据。实际构建、页面操作、写入和回滚必须由隔离部署流程在批准的测试数据上完成，并将构建产物、角色、数据基准、预期、实际和变更记录作为独立证据引用。

## 变更与回滚要求

每次写入观察前应保存回滚基线或最小变更记录，记录实例、源码/产物版本、角色、数据基准和时间。任务触发、附件操作和外部集成只能在隔离配置下执行；OA、ERP、FTP、邮件使用带来源标记的 mock/replay。低磁盘时只保留最小测试数据和收据，不创建生产迁移快照。

`summary.env` 包含每种状态的计数和观察文件 SHA-256。修改或截断观察文件会导致 `verify` 失败。收据目录及文件为 owner-only 权限，适合交接前归档到受限材料区。
