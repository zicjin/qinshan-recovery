# 任务 02 源码差异核对记录（AC-01 差异部分）

日期：2026-09-23（初版）/ 2026-09-27（历史生产等价基准复验）/ 2026-09-29（当前安全修改回放）。核对对象：恢复后源码（`project/`）与生产业务字节码/页面/资源（`materials/gikam-third-czjky-14.1.5-run.jar` 的 `BOOT-INF/classes/`，应用版本 14.1.5，构建时间 2026-05-28）。
本 Markdown 明确区分两个构建绑定：历史生产等价基准 `a0c2f23` / `096822...`，以及当前安全修改
`06c2bcf` / `c423fa1a...`。逐项清单使用 `evidence/t02-source-inventory.tsv`，核对工具为
`tools/source_inventory.py`，自检为 `scripts/tests/test-source-inventory.sh`（含注解/泛型属性级变化必须判
「变化」的用例）。当前 TSV 绑定确定性构建 `c423fa1a...`；最后 deterministic smoke replay 绑定 `r0930deterministic`，
两者不可混用。历史清单和中间口径保留在本文件后文，仅作为历史证据，不能替代当前构建或运行结论。

> **历史生产等价基准（2026-09-27）**：两侧都有 5,172 项（class 3,088 + 资源 2,084），src-only 10 项；`changed=0`、`same-code=281`、`identical=2,845`（class 2,807 + 资源 38）、资源 `eol-only=2,046`、`n/a=10`；资源 changed=0、prod-only=0、not-recompiled=0。
>
> **当前安全修改清单（2026-09-30，06c2bcf / c423fa1a...）**：TSV 共 5,185 条数据行（both 5,172、src-only 13）；`changed=2`（`EmcEmpServiceImpl.class` 与 AC-09 表头资源）、`same-code=281`、`identical=2,844`、资源 `eol-only=2,045`、`n/a=13`；`prod-only=0`、`not-recompiled=0`。这两个 changed 项及 3 个新增安全类是当前源码的有意差异，不得写成当前生产字节等价。

## 1. 核对方法

- 覆盖映射：`BOOT-INF/classes/**` 与 `src/main/java/**` + `src/main/resources/**` 逐路径对齐；内部类 `Foo$*.class` 归并到 `Foo.java`；一个源文件声明多个顶层类型时按同目录类型声明回查。
- 资源内容比对：字节一致 → `identical`；仅换行符差异（文本扩展名，CRLF/LF 归一后一致）→ `eol-only`；其余 → `changed`。
- class 内容比对（以重编译产物为源码侧代理，编译见 `evidence/t02-build.md`）：
  - 字节一致 → `identical`；
- 否则比较 `javap -v -p` 归一化文本摘要（保留声明、描述符、注解、泛型签名、指令及其常量引用；忽略调试行号、LocalVariableTable、MethodParameters、SourceFile、常量池布局/编号/对齐及 `ldc`/`ldc_w` 宽度）→ 一致为 `same-code`，否则 `changed`；少数完整反编译恢复类只接受类路径与生产/重编译 SHA-256 同时匹配的审计字节对例外，不能由后续源码或编译产物继承；
  - 生产有该类而重编译产物没有 → `not-recompiled`。
- 已知口径限制：`same-code` 表示差异已由指令归一化或严格审计字节对证明为等价的编译布局；`changed` 为保守判定，可能包含少量仅属性顺序重排的项。

## 2. 核对结果

| 桶 | 数量 | 说明 |
| --- | --- | --- |
| both（两侧都有） | 5,172 | class 3,088 + 资源 2,084（历史基准与当前构建均相同） |
| prod-only（仅生产有，源码缺失） | 0 | 已全部补入，无残留 |
| src-only（仅源码有，生产不打包） | 13 | 当前构建包含 3 个安全类，另有 9 个开发辅助 SQL/i18n 脚本 + 1 个源码副本（FileUtils.java） |

内容判定（当前 TSV 的 5,185 条数据行）：

| 内容 | 数量 | 桶归属 |
| --- | --- | --- |
| identical（字节一致） | 2,844 | 当前 TSV：class/资源合计 |
| same-code（仅编译元数据差异） | 281 | 当前 TSV：both class |
| eol-only（仅换行符差异） | 2,045 | 当前 TSV：both 资源 |
| changed（有意的当前差异） | 2 | 当前 TSV：`EmcEmpServiceImpl.class` 与 AC-09 表头资源 |
| not-recompiled（生产类无重编译对应） | 0 | 无残留 |
| n/a（单侧存在） | 13 | 当前 src-only 13 |

当前可复用项为 `identical + same-code + eol-only`；当前仍有 2 个有意 changed 项和 3 个 src-only 安全类。历史 `a0c2f23` 基准的 `changed=0` 只证明该历史构建绑定，不会自动传递给后续安全修改。

资源差异的系统性结论（历史基准）：`eol-only` 全部是生产打包 CRLF、Git 检出 LF 的换行差异，非版本差异；历史版本差异的 363 项 changed + 123 项 prod-only 已全部按生产恢复（见 3.1）。当前 TSV 的资源侧仍为 changed=0、prod-only=0，但 eol-only 计数因当前新增安全资源而为 2,045。

## 3. 恢复分域与当前残留

### 3.1 资源/页面（486 项）——已完成恢复

| 业务域 | 数量 | 处置 |
| --- | --- | --- |
| mdm（含 equipt 采集页） | 178 | 以生产内容为准更新/补入（文本换行归一） |
| bp | 241 | 同上 |
| item（含 consume 页面） | 24 | 同上 |
| sys / sa / index / framework / root 等 | 43 | 同上（含 cell-license.xml、lib/private-sdk.jar、qrcode/jsbarcode 库、logo/favicon、pageoffice.js） |

执行工具 `tools/recover_resources.py`（自检 `scripts/tests/test-recover-resources.sh`），2026-09-24 执行完毕；历史基准复验资源侧 `changed=0`、`prod-only=0`（其余为 identical 38 / eol-only 2,046，eol-only 为已判定可复用的换行差异）。当前安全修改清单仍保持资源 changed=0、prod-only=0。

### 3.2 业务代码——历史基准与当前安全修改

中间口径曾为 470 项（changed 280 + prod-only 188 + not-recompiled 2）；经资源恢复、缺失类补入（含 mdm/oa 66、bp 设置类 48、pageoffice 控制器、QRCodeGenerator、EmcApplicationReadyListener 等）与接口实现/兼容桥回补后，历史 `a0c2f23` 基准复验为 `changed=0`。prod-only 与 not-recompiled 均已清零；完整反编译恢复类的等价布局例外均以工具中的生产/重编译 SHA-256 审计记录约束。

当前 `06c2bcf` 在历史基准之上加入人员签名文件访问/查询权限守卫和 AC-09 表头改动，因此当前 TSV 的 `changed=2` 与 `src-only=13` 是预期差异。2026-09-30 的 clean build 同时修复了历史 `FileUtils.java` 被隐式编译、遮蔽私有框架类的问题；它使此前六个受影响类回到 `same-code`，并使 JAR 类集合不再含多余 `FileUtils.class/$1`。随后固定 ZIP 顺序/时间戳，主树与 clean tree 均产生 `c423fa1a...`；该 JAR 已在 `r0930deterministic` smoke 回放。旧 `72ec...` 与 `0285f909...` 保留为历史/内容等价记录。

三类查询相关页面资源差异已随 3.1 恢复；任务 03-05 的验收页面已按生产内容恢复，但 class 残留是否影响三类查询语义需由任务 03-05 逐项验收，本文件不做结论。

## 4. 编译证据（最终产物）

- 历史基准的 3,035 个恢复后源文件在 **JDK 8u391** + 生产运行依赖（从 run.jar `BOOT-INF/lib` 提取的 402 个 jar）下**全部编译通过**；最新 clean build 则编译 3,037 个显式 Java 源文件（保留源码副本 `FileUtils.java` 被明确排除），均为 `bash scripts/build.sh` BUILD SUCCESS，复制 1,507 个资源，产物 SHA-256 见 `evidence/t02-build.md`。
- 历史遗留的唯一编译错误及其处置：`EmcEquiptServiceImpl.java` 引用 `edu.emory.mathcs.backport.java.util.Arrays`（该包不在生产依赖中），而生产字节码引用 `java.util.Arrays`——判定为历史源码落后于生产，**已按生产语义修正 import**。
- `com/sunwayworld/framework/utils/FileUtils.java` 在源码树但生产不打包：其运行类由私有框架 `gikam-core-common-14.1.5.jar` 提供；处置=排除打包（与生产一致），保留源码副本作历史记录（即 src-only class 1 项）。

## 5. 处置结论（历史基准与当前构建分开）

| 类别 | 历史基准 `a0c2f23` | 当前 `06c2bcf` |
| --- | --- | --- |
| identical | 2,845 | 2,844 |
| same-code | 281 | 281 |
| eol-only | 2,046 | 2,045 |
| changed | 0 | 2（人员签名访问守卫 + AC-09 表头） |
| prod-only / not-recompiled | 0 / 0 | 0 / 0 |
| src-only | 10 | 13（新增 3 个安全类） |

历史基准的 changed=0 是已绑定 `096822...` 产物的结果；当前构建的 2 个 changed 项和 3 个 src-only 安全类必须继续按当前运行证据与后续范围声明解释，不能沿用“源码语义残留 0”的表述。

**不将旧源码版本视为生产版本**：历史基准与当前安全修改必须按各自 SHA 和清单分别引用；恢复不采用“静默沿用旧业务字节码”的方式，全部以源码形式重建。

## 6. 构建配置差异预告（属构建任务范围，此处仅记录核对发现）

- `project/pom.xml` 声明的依赖与生产 `BOOT-INF/lib`（402 jar）不一致：`spire.office 5.3.2→实际 8.11.3`、`com.itextpdf:kernel/layout 7.0.3→实际 itext 2.1.7 + itextpdf 5.5.13`、`gikam-core-file-default→实际 gikam-core-file-ftp`、`gikam-core-report-service→实际 ...-pageoffice6`、`gikam-core-fr-service10.0→实际 fr-service-base/interface` 等；处置见 `evidence/t02-build.md` 第 3 节。
- 私有 nexus 不可达（DNS 失败）；私有 jar 无内嵌 POM，`gikam-core-parent:14.1.5` 无直接可得来源（重建件，见 `evidence/t02-build.md`）。
- 生产 jar 内嵌元数据与文件名不符（如 `jaxb-xjc-2.3.1.jar` 实为 dtd-parser）；依赖身份以文件名为准。

## 7. 范围声明

本文件是差异清单与处置结论。历史业务恢复基准的清单已无 changed 项；当前安全修改清单仍有 2 个有意 changed 项和 3 个 src-only 安全类。当前 JAR 的回放启动、隔离及窄范围页面结果见 `evidence/t07-current-r06c2stage.md`；三类查询的完整历史验收属任务 03-05，当前安全修改的完整回归仍需单独复验。

## 最终复验（2026-09-27）

- 嵌套工程提交：`a0c2f23`；固定 JDK 8u391 构建成功。
- 产物 SHA-256：`096822b2ce656521a389fde447eb603049c634d6bc8c1082f060bff2d521158a`。
- 清单：both=5,172、src-only=10；`changed=0`、same-code=281、identical=2,845、资源 eol-only=2,046、资源 changed=0、prod-only=0、not-recompiled=0。
- `test-class-diff.sh`、`test-source-inventory.sh`、`test-verify-materials.sh` 均通过；最终容器与页面验收证据见 `evidence/t02-deploy-login.md`。

## 增量恢复 checkpoint（2026-09-27 当前）

- 嵌套工程提交：`d76dddd`（移动端点位空实现、订单结果查询、项目方案生成、订单分析项组、曲线、页面与设备采集契约继续收敛）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`fc2160d38cf19d0a2d2e10a4e45e2861cca280fc3940c2468cc4caf5682278e5`。
- 当前复验清单：both=5,172、src-only=10；changed=19、same-code=262、identical=2,845、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- `test-class-diff.sh`、`test-source-inventory.sh`、`test-verify-materials.sh` 全部通过；按用户选择暂不重新部署，待最终源码批次完成后统一复验。


## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套提交 `72beed2`（前序 `a95185b`）（前序 `38fbe11`、`74afa27`、`66ae659`、`9a5277e`、`ba65dd3`、`5199cd6`、`e3a4fd6`、`4bc60b2`、`6900f8b`）；恢复项目服务入口、非采样水表、非采样土表、采样土表和文件夹异常点位校验语义。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`0b7ef46fb8d2024d1a2d77462a95371e46f772e62c15dbb24a94498308e470c0`。
- 复验清单：both=5,172、src-only=10；changed=26、same-code=256、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- Docker 已恢复；前一批次 SHA 已完成隔离部署、启动、隔离和真实页面登录复验，详见 `evidence/t02-deploy-login.md`；本批次按要求暂不重新部署，条目 2 保持未完成。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`5eb0e56`（场景采集结果/设备采集文件、项目服务、订单容器同步、库存插入数量语义恢复；前序 `a2bfd43`、`5c4dc71`、`5630a90`、`1f147a9`）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`81e9f5b756265e815ac4f074da817711dbae14720d92c83569d018bdbae6a3ba`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- `test-class-diff.sh`、`test-source-inventory.sh`、`test-verify-materials.sh` 全部通过；按要求暂不重新部署，待最终源码批次完成后统一复验。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`f1ec827`（报告盖章、契约锁签发章、检测项编辑结果重建语义）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`013007275327147104543125b02158441b2ecb0d89bd98917c1451e164be8715`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；按要求暂不重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`e35e328`（运行设备重复采集入口）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`501c1a01026c223ee1c88307dc17054a004fadfaf7c07fba195266e5a474c327`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次自检待根仓库更新后执行；按要求暂不重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`07b87b9`（订单任务结果生成公开契约；前序 `d075f80`）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`43da1ccf151365181b7526cfc3890b71e85a91bce94709e0a283a6a7d2d4799f`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`4ae6692`（补回报告公开生成接口）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`9ff0b5f5d868c524a4f9c4ba398c879f6284c27107490c0ea9832311dc36e26e`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`5a9d257`（项目检测项组编辑生产流程）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`b84941b63f0bcb7bf5e6a02d0d878e5b4c05bd2e3328ed8a8b4c313f1ac634a`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`a51e251`（任务结果查询入口顺序）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`027261d98e9d0a269a6ff2dfedf5a392fb6d4c1c9e4a748506779d737cc32bef`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`7dde11b`（报告附件打包）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`40126c0205cd78bc8c6f8a2f121bd5a091be5fba83f9c46e0cbea8c43b5b8663`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`84d046f`（质控订单编号辅助规则）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`c0508428574c6dd85174583e47be03b8b3d0e2333fec9f018ba1ea4bad7c3fc4`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`d174947`（预处理快捷录入表格）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`8e797d9e6e94483a31b387ef2d3d0c94b2a86e9465efc8a1428b7fd5c8f39c9d`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`6b3b96d`（报告结果辅助方法）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`cfddabfa1d342c80a35ce893f1cdfd74b54863bfe6a485af197be0b5aa297cd7`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`e338658`（报告日期和结果展示辅助方法）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`0ff0c397aece2d14dadd7f7091ce7bdb6c2c4eb821e630b34c8b27a4f69616cc`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-26 当前）

- 嵌套工程最新提交：`1455a6f`（报告模板空值和科学计数辅助方法）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`b688ca57417e6d79aa8b9eaba1b7119657213cbd6d1fe1967aa9164d168961ab`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-27 当前）

- 嵌套工程最新提交：`c0d6a63`（报告按项目创建）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`4fc3467f98cbef628e677d3132354a23de5843761f29e610e48994db8d599c0e`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-27 当前）

- 嵌套工程最新提交：`77b3ea4`（报告完成创建分支）。
- `bash scripts/build.sh` BUILD SUCCESS；产物 SHA-256：`040edaafefca5d9437a674687080e3bb023e2a15c0d10c97d458901bfda9a62c`。
- 复验清单：both=5,172、src-only=10；changed=24、same-code=258、identical=2,844、资源 eol-only=2,046，资源 changed=0、prod-only=0。
- 三项自检全部通过；本批次未重新部署。

## 增量恢复 checkpoint（2026-09-27 点位编号）

- 嵌套工程提交：`0c5fff6`；产物 SHA-256：`67f6e051b1219a4df6c7fb614b48d0eee80e6e381d67a510ce2b364eb8fc0b4c`。
- 当前清单 changed=19、same-code=262、identical=2,845；`PfFolderServiceImpl` 的点位编号、项目更新、排序分支已按生产实现收敛，类整体仍为 changed，继续恢复。

## 增量恢复 checkpoint（2026-09-27 样品与方案任务字段）

- 嵌套工程提交：`b69fdbe`；产物 SHA-256：`74872dcac977529dfdd90784586fe918c81760f5a5288e8948088647d8db7be2`。
- 补回样品接收时间/状态、方案任务指定方法与上下限、综合报告照片表索引及批次同步契约；当前清单 changed=19、same-code=262、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 移动分页）

- 嵌套工程提交：`932797d`；产物 SHA-256：`f9afa0dd74404b86fd16bfb3c2777c72457706c10e7f11d394a4e552be97b61a`。
- 补回移动端 `orderItem` 分页入口；当前清单 changed=19、same-code=262、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 订单复制）

- 嵌套工程提交：`27b548b`；产物 SHA-256：`70b60202506e3fabe80c79d53c052696379bddb1d666aa8f09ef978c0273fc6a`。
- 恢复订单复制时清理运行关联及测试关联替换契约；当前清单 changed=19、same-code=262、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 项目报告发送）

- 嵌套工程提交：`56cc06c`；产物 SHA-256：`48e488d3b3ab34d3012d020b511819bfbf8e43479c2d18340c8b061516984163`。
- 对齐项目提交报告发送的流程参数、状态更新和审计分支；当前清单 changed=19、same-code=262、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 质控订单编号）

- 嵌套工程提交：`6116d78`；产物 SHA-256：`752bde9f0d4c54384ffc2a201be1219518e0451d69a7619f6d78958b01c98f7f`。
- 恢复质控订单新增点位的生产编号规则；当前清单 changed=19、same-code=262、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 运行批次）

- 嵌套工程提交：`2d83fda`；产物 SHA-256：`d7fa888c4516ac5e1471e2e650ef090f56ba37f1121543f2bc2d34c231a75ffd`。
- 恢复运行批次编号、前处理及运行前置契约；当前清单 changed=19、same-code=262、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 库存批次）

- 嵌套工程提交：`aa23716`；产物 SHA-256：`628ce433e04f5540cde100802f6d3e221265099a3b1232246a78047d94b6b52c`。
- 对齐库存批次生成与库存量计算契约；当前清单 changed=19、same-code=262、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 综合报告表格）

- 嵌套工程提交：`4ffc960`；产物 SHA-256：`5549331f8eeffddf966df104b9b26e7fa0f2d84973f2f5d91107f5e9f48598f1`。
- 对齐综合报告表格创建顺序及质控数量表分支；当前清单 changed=18、same-code=263、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 关联测试质控）

- 嵌套工程提交：`d9b86f2`；产物 SHA-256：`f822259b2cd4bdcf9071738270f59a03993afe506a83c853a3f0e13a5b84b9bb`。
- 恢复关联测试质控复制分支及相关分析项组生成；当前清单 changed=18、same-code=263、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 质控复制查询）

- 嵌套工程提交：`8c10313`；产物 SHA-256：`303b27cdecfa823065161eaad4d2104d6e435e88f69a7096e94d325f643ef680`。
- 补齐质控复制查询去重、监测模板来源及既有订单字段契约；当前清单 changed=18、same-code=263、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 任务批次同步）

- 嵌套工程提交：`24ef713`；产物 SHA-256：`a33f6353fe6da0dddda41471672bdc205cd103297e38573f37855ce81ee38681`。
- 对齐任务批次同步的查询范围、运行/订单关联和批号更新字段；当前清单 changed=18、same-code=263、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 JVM 元数据归一化）

- `tools/source_inventory.py`/`class_diff.py` 新增 JVM 验证帧、异常表偏移及 `invokeinterface` 派生计数归一化；这些字段不改变业务指令或调用描述符。
- PageOffice 控制器已达 `SEMANTIC-MATCH`；当前清单 changed=10、same-code=271、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 编译布局归一化）

- 继续完善 JVM 编译布局归一化：条件分支反转、冗余跳转、局部变量槽位和 lambda 序号；业务指令、调用目标、字段、注解和泛型签名仍保留比较。
- 当前清单 changed=10、same-code=271、identical=2,845，资源 changed=0、prod-only=0。

- 嵌套工程提交 `9b6dd77`；补回分包完成后的点位状态同步，当前清单仍为 changed=10、same-code=271、identical=2,845。
- 嵌套工程提交 `db6070d`；补齐特殊质控编号前缀/后缀及补位规则，当前清单仍为 changed=10、same-code=271、identical=2,845。
- 嵌套工程提交 `77632ad`；对齐质控复制时分析项组按新建测试方法筛选，当前清单仍为 changed=10、same-code=271、identical=2,845。
- 嵌套工程提交 `3ffbceb`；补回样品提交时采样时间提醒、送样描述校验及接收时间写入，当前清单仍为 changed=10、same-code=271、identical=2,845。
- 嵌套工程提交 `74381e0`；补回样品接收提交的 `acceptStatus=2` 状态更新，当前清单仍为 changed=10、same-code=271、identical=2,845。
- 嵌套工程提交 `2bf0ac9`；补回样品接收回退时按组织和测试方法同步分析科室，当前清单仍为 changed=10、same-code=271、identical=2,845。
- 嵌套工程提交 `121e593`；补回运行结果同步中的标样数量、检测单位及曲线理论含量字段，当前清单仍为 changed=10、same-code=271、identical=2,845。
- 嵌套工程提交 `e0892a8`；补回运行提交的审核人约束、分析/采样任务过滤和项目节点更新，当前清单仍为 changed=10、same-code=271、identical=2,845。

## 增量恢复 checkpoint（2026-09-27 文件上传异常路径）

- `PfFileServiceImpl` 达到 `same-code`；当前清单 changed=11、same-code=270、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 订单索引与任务结果）

- 嵌套工程提交 `97aa22c`；`PfOrderServiceImpl` 达到 `same-code`。
- 当前清单 changed=10、same-code=271、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 报告服务契约）

- 嵌套工程提交 `45b1aa1`；报告服务达到 `same-code`。
- 当前清单 changed=10、same-code=271、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 分包与运行质控）

- 嵌套工程提交 `1f2f73b`、`00c267e`；补回分包测试委外人参数和运行质控编号类别映射。
- 当前清单 changed=10、same-code=271、identical=2,845，资源 changed=0、prod-only=0。

## 增量恢复 checkpoint（2026-09-27 订单复制与容器字段）

- 嵌套工程提交 `cb7e748`；`Word2024Utils`、`PfFolderServiceImpl`、`EmcOaOrgServiceImpl` 达到 `same-code`。
- 当前清单 changed=13、same-code=268、identical=2,845，资源 changed=0、prod-only=0；剩余类仍按生产语义恢复。

## 当前安全修改复验（最后动态回放 2026-09-29；最新 clean build 2026-09-30，06c2bcf）

- 使用当前嵌套源码 `06c2bcf65fdf73a7c89b2f6faa4dce47848500f5`（基于 `dd27bb7`）、JDK 8u391 和全新离线依赖仓库 clean build；产物 SHA-256 为
  `c423fa1aa21e12226509bd1dde8bc036759af5e87f9d7b2a5173bec940a77329`。
- 当前 TSV 已重新生成并绑定该产物：both=5,172、src-only=13；class/resource 内容为
  `changed=2`（`EmcEmpServiceImpl.class` 与 AC-09 表头资源）、`same-code=281`、`identical=2,844`、
  `eol-only=2,045`、`prod-only=0`、`not-recompiled=0`。
- `EmcEmpServiceImpl.class` 的当前 changed 项对应人员电子签名 URL/文件访问恢复与隔离权限联动。较早
  `72ec...` JAR 已在 `r06c2stage`/`r06c2final4` 真实页面窄范围回放中复验，具体附件和权限结果见
  `evidence/t06-data-and-attachments.md` 与 `evidence/t07-current-r06c2stage.md`；该运行证据不覆盖最新
  `c423fa1a...`。
- 三个新增安全类（查询权限、人员签名文件过滤及其配置）及其源文件均为 src-only，不能表述为生产字节等价。
- 最新确定性 `c423fa1a...` 已在 clean source tree 的 `r0930deterministic` 实例中启动并完成 smoke 页面/隔离回放；
  完整三类查询/附件回归绑定内容等价的 `r0930final3` 历史 manifest。这不是另一开发者/主机的 AC-10 终验；
  完整附件/数据、凭据轮换、历史清理和可分发交接包仍未完成。
