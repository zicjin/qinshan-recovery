# Clean-tree build and replay supplement（r0930cleanbuild）

日期：2026-09-30（Asia/Singapore）。这是 `r0930final3` 之后的独立 clean-tree 构建补充，
用于证明 clean source tree 自己执行了 build，而不是只运行主工作树产物。

## 独立构建

- 在 `/home/ts/qinshan/lims-recovery-clean-20260930` 注入经 `verify-materials.sh` 校验的材料后，执行：
  `bash scripts/build-prep.sh`、`bash scripts/build.sh`。
- clean build 结果：`BUILD SUCCESS`，JDK 8u391，Maven 3.9.12，编译 3,037 个显式 Java 源文件。
- clean-tree JAR SHA-256：`74c4560b14d1e125ede0a6bb6c45b9a3775164165cbb5bdc6c4832002e4d543f`。
- 主树 JAR `0285f909...` 与 clean-tree JAR 的 7,868 个 ZIP 条目名称及内容 SHA-256 全部一致；差异仅为
  ZIP 元数据时间，因此页面/类/资源内容绑定相同，但两个归档文件的外层 SHA 不同。
- clean-tree source inventory：`both=5172`、`src-only=13`、`changed=2`、`same-code=281`、
  `identical=2844`、`eol-only=2045`，与主树清单一致。

## clean-built 镜像与运行

- clean-built image tag：`lims-app:14.1.5-clean-r0930f`；image ID
  `sha256:6f358ee180c4586ed40c9753e249be6c688ea311540435cd758415126b129a56`，`linux/amd64`。
- 新 restore volume：`lims-snapshot-r0930cleanbuild`；新 replay instance：`r0930cleanbuild`，
  `127.0.0.1:18094`，internal subnet `10.61.0.0/16`。
- replay manifest 记录 exact image IDs、snapshot archive hash/labels/marker、source identity、launcher
  hashes、`database_link_count=0` 和 `startup_result=ready`。
- clean-built 容器内 JAR SHA-256 为 `74c4560b14d1e125ede0a6bb6c45b9a3775164165cbb5bdc6c4832002e4d543f`；
  日志记录 `Tomcat started on port(s): 8081`、`Started GikamWpjcApplication in 92.715 seconds`。
- clean-built instance isolation-check 为 PASS；应用/Oracle 仅接入自己的 internal 网络，生产/外部端点不可达，
  隔离 Oracle/Redis 可达。
- 授权真实页面再次显示耗材 `共206条` 和完整表头 `材料名称（构建验证）`。

这项补充增强 AC-02/AC-03/AC-09/AC-10 的 clean-tree 可重建证据，但仍不完成 AC-04 全量数据/附件、
凭据/许可证轮换、历史清理或另一开发者/主机终验。
