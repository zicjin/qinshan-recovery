# 许可证机器绑定阻碍记录（任务 01）

日期：2026-09-23。范围：隔离副本首次启动。

## 现象

应用容器启动后（`scripts/app-ctl.sh up`），Spring 初始化阶段输出一行：

```
[Mac Address Mismatch]<sunway.app.license 密文，去敏不记录>
```

随后 JVM 退出（容器 Exited(255)），应用不可用。

## 判定

`sunway.app.license`（application.properties 中的许可证密文）与机器网卡地址绑定。生产主机 eth0 的 MAC 记录在受限材料（本文档只记录前缀 `fa:16:3e:`）；隔离副本容器网卡由 docker 自动生成，二者不一致导致校验失败。校验逻辑位于私有原生库 `sunway-app.so`（`SunwaySecurityConfiguration`/`ApplicationContextHelper` 相关 JNI 入口），属 ADR-0001 允许保留的私有组件。

## 可复现步骤

1. `bash scripts/app-ctl.sh up`
2. `docker logs lims-app` 观察到 `[Mac Address Mismatch]`，容器数秒内退出。

## 明确未采取的手段

- 未修改、反编译补丁或替换 `sunway-app.so` 中的校验实现。
- 未关闭认证或跳过许可证校验启动。
- 未使用任何非本项目的许可证、注册机或伪造许可证文件。
- 未在生产上执行任何操作。

## 可选解决路径（待决策）

1. **复刻生产网卡标识**：以 `docker run --mac-address <生产 eth0 MAC>` 启动应用容器。许可证校验仍按原样真实执行，通过原因是隔离副本复刻了被许可机器的网卡标识（与已复刻的 `/data/LIMS/...` 目录布局同类）。
2. **向软件供应商申请本机/测试许可证**：正式路径，但需外部流程，不在本次实施可控范围。
3. **维持阻塞**：记录现状，登录验收无法完成，任务 01 的条目 4、5、6 记为受阻。

## 状态

用户 2026-09-23 决策采用选项 1（复刻生产网卡标识），由 `scripts/app-ctl.sh` 读取受限材料 `private/license-machine.env` 实施；许可证校验本身未被修改、未被跳过。决策人：用户；未采取手段见上节。
