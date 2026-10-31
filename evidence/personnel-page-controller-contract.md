# 人员页面控制器契约

日期：2026-10-19。

未修改的 `EmcEmpController` 和 `EmcEmpControllerImpl` 使用 `owned-framework` 及固定哈希的公共 Spring 依赖重新编译。自有实现提供了 `GenericEditListPageController`、`GenericDetailPageController`、`GenericChoosePageController`、`NotImplementedException`、`Log`/`LogType` 和 `GikamBean`/`GikamCondition`。

源码绑定：`EmcEmpController.java` SHA-256 `a9198bacb7330398a4453ee6c77546981d8e12e43b897d83cad8a2610b340999`；`EmcEmpControllerImpl.java` SHA-256 `3bf2504423fdf932fb3454d0f70e772ca69b2a58e885cd1855b4ced3cd2865f7`。供应商参照为已记录的 `gikam-core-common-14.1.5.jar`，测试脚本按 `evidence/framework-contract/jars.json` 校验其哈希后仅放入参照加载器。

`GikamBean` 的条件选择与供应商可观察规则一致：只有 Controller、RestController、Component、Service、Repository、`.mapper` 包或名称以 `Mapper` 结尾的类型参与选择；活动 profile 按字面值逐项匹配；同一兼容类型存在已启用的可赋值子类时，基类被排除。`GikamConditionTest` 通过 Spring `AnnotationConfigApplicationContext` 验证子类覆盖和两个 profile 的启用/缺失 profile 的禁用。

`test-personnel-page-controller.sh` 的证据包括：

- 五个真实模板方法返回值核对通过。
- Spring `RequestMappingHandlerMapping` 注册五条真实路径：`/edit-list`、`/detail`、`/choose-list`、`/report-users/choose-list`、`/sample/choose-list`，均带有实际 `/secure/emc/module/mdm/emp/emps/page` 前缀。
- 控制器条件选择测试通过；控制器/注解差分观察为 72 项。

该证据没有证明登录、角色、数据范围、审计执行、服务/DAO、Oracle 查询、浏览器页面或外部集成。`Log` 只恢复方法元数据，不等于已实现审计写入行为。

验证命令：

```bash
bash scripts/tests/test-personnel-page-controller.sh
```

该脚本最终输出 `controller differential: PASS (72 observations)`；Spring 日志实现缺失提示来自刻意未引入的 `log4j-core`，不影响路由或条件结果。
