# LoginUser 身份对象契约

日期：2026-10-19。执行：`bash scripts/tests/test-login-user-contract.sh`。

自有 `com.sunwayworld.framework.security.bean.LoginUser` 保留公共 Spring Security `User` 基类，以及组织、角色、租户、委托身份、检查标志、登录类型、token 和扩展 Map 契约。参照来自固定哈希的 `gikam-core-common-14.1.5.jar`，记录见 `framework-contract/jars.json`。两个独立类加载器共享同版本的公共依赖；自有加载器不含供应商 JAR。

44 项差分观察通过：默认值、字符串和 Long 字段、两个布尔标志、委托身份引用、扩展字段引用与大小写、null 扩展 Map、用户名合法性、同名身份的 equals/hashCode、公共基类状态和凭据擦除。测试全部使用合成输入。

默认 `additionalCheck=true`、`badPassword=false`，构造器把 null 密码转换为空字符串；用户名校验和凭据擦除继续由公共 Spring Security 基类执行。以上行为来自参照观察，不代表已实现密码验证或授权。

该结果只支持身份对象兼容。真实登录/退出、会话、权限树、数据范围、审计、页面及低权限拒绝仍未验收，票据 08 保持开放。
