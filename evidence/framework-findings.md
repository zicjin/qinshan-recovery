# 私有框架只读调查

调查日期：2026-09-23。来源：`ssh sss`，`/data/LIMS/jiankeyuan/lims-czjky-emc/gikam-third-czjky-14.1.5-run.jar` 及同目录 `sunway-app.so`、启动脚本和 lic 目录文件名。远端仅使用 ls、unzip、strings 等读取；未执行应用、未修改文件。嵌套 JAR 流入本机 Python 内存读取 ZIP 条目，无安装与恢复实施。

## 已证实的模块边界

| 组件 | 直接证据 | 功能判断及限制 |
| --- | --- | --- |
| 主应用 BOOT-INF/classes | 7376 个目录/资源条目；`com/sunwayworld/emc/module/` 下有业务 bean、resource、controller、service、dao、mapper；另有 templates | 业务逻辑与业务页面位于主包，可作为优先恢复边界；条目数量不是源码文件数 |
| gikam-core-web-14.1.5.jar | 5888 ZIP 条目，无 class；`static/gikam/main/js`、css、ckeditor4、pdfjs、scheduler、spreadjs 等 | 提供公共前端资源与控件；是否需重建前端编译链尚未调查 |
| gikam-core-common-14.1.5.jar | 4095 条目；含 framework、用户管理、菜单、BPMN、元数据、设计器、审计、通知、许可证服务 | 核心框架覆盖通用数据访问和平台业务，直接关联查询与登录，不能仅按可选插件看待 |
| gikam-core-base-service-14.1.5.jar | 1791 条目；customquery、customds、pagefield、chart、monitor、email、mobile/wechat、equipt/fileacqconfig | 包含自定义查询、数据源、页面字段、图表、监控和外部集成；具体启用情况待配置及数据库核对 |
| gikam-core-lcdp-service-14.1.5.jar | 889 条目；lcdp 下 bp、resource、table、pagetmpl、moduletmpl、apiintegration 等 | 含低代码平台功能；数据库中的页面、表、流程和资源定义是否为运行必需待调查 |

其他私有依赖条目包括 core-base-interface、core-file-ftp、core-report-service/interface（pageoffice6）、core-ext-service/interface、core-schedule-service/interface、core-lcdp-interface、core-fr-service-base/interface、lims2-report-template、private-sdk-RELEASE；不能仅据名称认定具体业务已启用。第三方依赖包括 PageOffice、Spire、Aspose、Oracle/MySQL/PostgreSQL/SQL Server 驱动、Redis/Elasticsearch 客户端等；依赖存在不等于服务必需。

## 人事、设备、耗材查询与框架关系

主包存在以下业务类和模板目录：

- 人事：`com/sunwayworld/emc/module/mdm/emp/emp/`，包括 EmcEmpResourceImpl、EmcEmpServiceImpl、EmcEmpDaoImpl、EmcEmpMapper、资格/考试/培训类；模板 `templates/emc/module/mdm/emp/emps/`。
- 设备：`com/sunwayworld/emc/module/mdm/equipt/equipt/`，包括 EmcEquiptResourceImpl、设备台账及维护/使用/校验相关类；模板 `templates/emc/module/mdm/equipt/`。
- 耗材：`com/sunwayworld/emc/module/item/consume/consume/`，包括 EmcConsumeResourceImpl、EmcConsumeDaoImpl、库存与库存日志类；模板 `templates/emc/module/item/consume/consumes/`。

对三个 ResourceImpl 的 class 常量进行 strings 只读检查，直接出现 `com/sunwayworld/framework/restful/data/RestJsonWrapperBean`、`framework/data/page/Page`、`framework/support/base/service/GenericService`、GikamBean/Log 注解等引用；耗材类还出现 MyBatis SearchFilter、MatchPattern。说明查询协议、分页、基础服务、数据过滤与私有框架耦合。独立恢复业务源文件并不等于脱离框架即可启动。

## sunway-app.so 与许可证

`strings sunway-app.so` 发现 JNI 符号：

- `Java_com_sunwayworld_boot_ApplicationConfiguration_*`：init/initApplicationContext、dataSource、sqlSessionFactory、sqlSessionTemplate、Redis/cache、任务调度、HTTP/MVC、国际化配置等。
- `Java_com_sunwayworld_boot_security_SunwaySecurityConfiguration_*`：认证、用户详情、密码编码、rememberMe 等。
- `Java_com_sunwayworld_framework_security_authentication_AuthenticationHelper_*`：login、logout、缓存认证。
- `Java_com_sunwayworld_framework_context_ApplicationContextHelper_*`：getLicenseDeadline、getLicenseCountdownDays、getLoginUserLimitation、getNumberOfOnlineUsers。
- `Java_com_sunwayworld_framework_tenant_TenantManager_dataSource`。

据此可以确认该原生库承载广泛的框架 JNI 入口，涉及启动、认证、数据源和许可证查询，不只是文件/报告辅助库。尚未动态验证每个符号的调用路径与实际实现，不能声称已证明全部入口是启动必需。

core-common 内存在 CoreLicenseServiceImpl、CoreLicenseDTO、ApplicationContextHelper。抽检 ApplicationConfiguration、ApplicationContextHelper、CoreLicenseServiceImpl 的 class 魔数均为 CAFEBABE，说明这些文件具有标准 Java class 头；不代表完整实现可从字节码恢复（存在原生入口）。

lic 目录文件名：license.lic（约5.4KB）、vendor.lic（约3.5KB）、poserver_log.txt、posetup_6.4.1.1.exe（约100MB）。主包另有 cell-license.xml。未读取/公开许可证内容，未确认各许可证供应商归属、期限、授权范围、机器绑定。启动脚本使用普通 `java -jar`，没有显式 `-agentpath`；库如何载入仍未知。

## 供源码范围决策的结论

可行的分层目标是先恢复业务层与页面的可修改工程，并将现有私有 JAR、前端资源、原生库作为明确版本的运行依赖；这仍要求验证框架和许可证能在隔离机器正常启动。若要求连私有框架底层也全部可修改，工作将超出普通 Java 反编译，至少包含上述 JNI 库相关边界，需要进一步确认供应商源码可获得性或另行评估替代实现范围。

本轮不对合法授权、跨机器许可证可用性、原生库操作系统兼容性、全部资源是否齐全作推断。用户只要求先查明框架功能，尚未决定必须恢复全部框架源码。
