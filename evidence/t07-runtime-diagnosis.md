# 当前容器运行时诊断记录

日期：2026-09-30（Asia/Singapore）。范围仅为本地隔离 replay；生产未访问。

## 历史现象与最小复现（旧镜像 `706fcc...`）

- 当前自包含镜像 `lims-app:14.1.5-built`（image ID `706fcc...`）使用当前 JAR、JDK 8u391、
  原始 `sunway-app.so` 和同一 MAC/许可证输入时，直接把宿主附件目录 bind-mount 到应用 `/files`，
  日志稳定结束于 `[Get Class Error][org/springframework/boot/web/servlet/MultipartConfigFactory]`，容器 exit 255。
- 同一镜像、同一 JAR、同一配置/许可证/MAC，把附件源先复制到临时可写目录再挂载到 `/files` 后，
  原生初始化通过、Oracle/Redis 可达并出现 `Started GikamWpjcApplication`。
- 只读探针曾单独调用原生初始化和 `multipartConfigElement()`，均返回成功；未修改或绕过许可证校验。

## 处置

`scripts/stage-attachments.sh` 将源附件树在无网络临时容器中复制到每实例本地 Docker volume，核对文件数、
总字节数和树 SHA-256；应用只挂载该 volume 的 `/files`，不再直接挂载宿主源目录。现有 volume 复用时，
`app-ctl.sh` 还严格校验 mode `0600` 收据、源统计、staging 脚本哈希和每个基线文件的内容；镜像 ID 只作
来源记录，不作为正常重建后的复用阻断条件。基线不匹配会在停止/替换既有应用前失败。`r06c2final4` 已用当时的
staging/显式子网版本脚本启动成功并通过显式隔离检查；其精确脚本哈希记录在该实例的受限 manifest 中。

后续启动器又增加了应用/Oracle/Redis image ID 和 snapshot-volume Oracle-ID label 的强绑定，并加入
clean-source identity、RFC1918 子网和 Java-properties 防绕过检查；这是启动前安全收紧。`r06c2final4` 的
旧 manifest/volume 是收紧前证据，不能表述为已通过新版强绑定。要重做新版回放，必须从已验证快照重新
restore 新 volume，使用干净来源或受限 source identity，并填入八键 runtime 输入。

Docker socket 恢复后，已用最新 `0285f909...` JAR、全新 restore volume
`lims-snapshot-r0930final3`、clean source tree 和八键 runtime 输入完成 `r0930final3` 动态 replay。
该实例通过 image-ID、snapshot marker/archive hash、clean-source identity、RFC1918 子网、properties
防绕过、附件 staging、启动、isolation-check 和浏览器回归；精确绑定见
`evidence/t07-current-r0930final3.md`。早先 `r06c2final4`/`r0930latest` 仍是旧版本或失败现场，不能与新实例 manifest 混用。

历史 `c3f4` 基础镜像/快照身份仍只作为历史记录；最新回放使用当前已验证的四张本地镜像并保持
`--pull=never`，未通过 retag、关闭校验或替换许可证规避。
