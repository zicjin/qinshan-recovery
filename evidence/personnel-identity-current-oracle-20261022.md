# 当前身份与 Session 回归（2026-10-22）

`python3 scripts/tests/test-personnel-identity-oracle.py` 本次报告位于 `build/personnel-identity-oracle.json`，现按原始字节归档为同名日期 JSON：JDBC 身份 63 项、真实 Boot HTTP 281 项及 servlet HTTP 桥接均通过。源码基线为 `3f40a9f`，工作树包含身份修改，`source_dirty=true` 与源码哈希保留；Oracle 无错误，容器、匿名卷、内部网络全部清理。

另执行 `build/jdk1.8.0_391/bin/java -cp 'owned-framework/target/classes:owned-framework/target/test-classes:owned-framework/target/public-deps/*' com.sunwayworld.framework.OwnedIdentityHttpBoundaryContractTest`，通过 24 项，包含失效 Session 的拒绝和审计。此命令直接验证提交 `1bfd15e` 的幂等清理修复。

更正提交说明的证据范围：`1bfd15e` 附带的 `personnel-identity-rsa-oracle-20261020.json` 是 10 月 20 日的历史 RSA 报告，不是本次 Oracle 运行输出；本次应引用 `personnel-identity-current-oracle-20261022.json`。本次 63/281 检查不声称覆盖附件菜单授权；该能力由附件 70 项 HTTP 与 30 项浏览器证据覆盖。当前回归仅限合成 Oracle、loopback HTTP 和边界契约，不是全量角色或生产等价验收。
