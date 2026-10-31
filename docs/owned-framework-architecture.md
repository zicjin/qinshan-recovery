# 自有兼容框架实现边界

目标架构遵循 ADR-0002 和重建规格：兼容门面保持真实业务包名及方法签名，内部按能力拆分，最终删除供应商运行时及许可证能力。

目前 `owned-framework` 提供从只读供应商材料核对的 `Page`、`Pagination`、`Pageable`、`PageRowBounds`，以及人员 DTO 所需的领域基类、扩展字段 Map、数据注解和页面控制器门面。真实 `EmcEmpBean` 已使用自有类重新编译，并通过独立加载器的 41 项差分观察；分页通过 61 项观察，行边界通过 58 项观察；真实 `EmcEmpControllerImpl` 的五条 Spring 路由和 `GikamBean` 条件选择也有独立测试。证据见 `evidence/personnel-domain-contract.md`、`evidence/pagination-contract.md` 和 `evidence/personnel-page-controller-contract.md`。公共 Spring/Fastjson 依赖校验固定哈希并单独存放，供应商 JAR 只进入参照加载器。

先前添加的身份、启动、DAO、资源和工具占位代码没有足够契约证据，已移除。当前已恢复 `ArrayUtils`、`CollectionUtils` 和 `ConvertUtils` 的局部公共契约，但没有注册 HTTP 入口，没有连接数据库，也没有业务认证实现。人员的泛型资源/服务/DAO 链路仍待实现，DTO 编译和局部契约观察不能关闭人员页面验收。

身份对象 `LoginUser` 已按独立参照实现并通过 44 项差分观察（`evidence/login-user-contract.md`），依赖固定哈希的公共 Spring Security 5.3.4。它提供原有身份数据接口；业务认证流程、会话、角色授权和数据范围仍待实现。

后续实现必须由契约清单和真实业务调用推动。`evidence/vertical-slices-java-seams.md` 标明第一条人员查询源码路径和权限缺口。生产只读；写入测试、任务和外部回放必须在隔离环境进行。
