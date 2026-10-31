# 自有分页契约

日期：2026-10-19。

从受控只读 `materials/gikam-third-czjky-14.1.5-run.jar` 的 `gikam-core-common-14.1.5.jar` 通过 JDK 8 `javap -public -s` 核对：`Page<T>` 为 Serializable 接口，`getTotal()` 返回 `int`；包括 rows、页大小、页码、总页数、前后页标志和列/合计元数据方法。

`owned-framework` 实现此接口及首批 Pagination、Pageable 和 PageRowBounds 构造器。分页的 Java 8 编译和行为测试是局部证据，尚未证明整个框架、业务工程、HTTP 协议或页面行为兼容。其他供应商类型未被此模块替代。

命令：`bash scripts/tests/test-owned-framework.sh`。

增量契约证据：`pagination-contract.md` 记录 61 项分页和 58 项 `PageRowBounds` Java 8 差分观察；`personnel-domain-contract.md` 记录真实人员 DTO 使用自有基类编译和 41 项差分观察。最终应用启动、数据库和页面兼容性仍待验证。

独立构建还运行 `scripts/tests/test-owned-public-contracts.py`，将产物中的自有兼容类与已归档供应商清单逐项核对；当前完整构建报告 212 个声明契约。检查包括类标志、父类/接口、public/protected 字段、构造器/方法描述符、static/final/abstract/bridge/varargs 等标志。缺失、多出或无参照的声明均使构建失败。这不核对泛型 Signature、序列化字节或运行时行为；行为仍由差分测试支持。

页面控制器门面、注解及条件选择的 70 项差分观察和真实控制器五条 Spring 路由见 `personnel-page-controller-contract.md`；身份对象的 44 项差分观察见 `login-user-contract.md`。两者都不是完整应用的登录或页面验收。
