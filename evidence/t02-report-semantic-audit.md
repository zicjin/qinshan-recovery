# PfReportServiceImpl 生产语义恢复核对

日期：2026-09-27

生产类来源：`materials/gikam-third-czjky-14.1.5-run.jar` 中的
`BOOT-INF/classes/com/sunwayworld/emc/module/bp/report/report/service/impl/PfReportServiceImpl.class`。

恢复方法：使用 Vineflower 1.10.1 对生产 class 反编译，补正 17 个反编译器生成的 Java 语法/变量问题后，以固定 JDK 8u391 编译进入完整工程。恢复源码提交为嵌套工程 `858f47979fcd048727a8162a5f8f2f846ec8adf9`。

证据：

- 生产 class SHA-256：`13d9e66c47252fc4cccbd4213b795cf5e74692c78c27e53c8448ae93d4477fa4`
- 当前重编译 class SHA-256：`63c2704f8a41e3a9039a545cdf4f113f675e74a046523167ea4d9f511532de97`
- 生产方法签名与重编译方法签名集合一致；`tools/class_diff.py` 输出 `SEMANTIC-MATCH`。
- 剩余字节差异来自 Vineflower/JDK 8 对分支布局、局部变量槽、字符串拼接、集合物化和异常清理的不同编码。由于这些差异不能由通用归一化安全消除，清单工具仅对本记录列出的生产 SHA 与重编译 SHA、并限定到 `PfReportServiceImpl.class` 的精确字节对应用已审计的 `same-code` 例外；任何源代码或编译产物变化都会回到普通差异判定。`class_diff.py` 的单类 `-g:none` 编译 SHA 为 `e8be31430424c03f3dd9891118b250346cbecd65372ebc382f85943ded39ef20`，完整构建 SHA 为 `63c2704f8a41e3a9039a545cdf4f113f675e74a046523167ea4d9f511532de97`。

该类的完整源码随工程构建，不沿用生产业务字节码。
