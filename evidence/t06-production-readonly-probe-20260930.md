# T06 生产只读边界探针（2026-09-30）

本记录只保存只读 SSH 探针结果，不是附件 inventory，也没有修改生产。

## 已确认

- SSH alias `sss` 可连接；探针只执行 `hostname`、`stat`、`findmnt`、`lsblk` 和受限路径下的文件名查找。
- 生产附件根：`/data/LIMS/jiankeyuan/lims-czjky-emc/files`，目录 mode `755`、owner `root:root`。
- 设备采集根：`/data/LIMS/Equiptfile`，目录 mode `755`、owner `root:root`。
- 两个路径都位于 `/dev/vdb1`、XFS、约 900G 的 `/data` 文件系统；当前 mount options 为 `rw,relatime,attr2,inode64,noquota`。
- 主机具有 `lvs`、`btrfs`、`xfs_freeze` 命令，但本探针没有调用它们，也没有执行 freeze、snapshot、递归 walk、复制或 hash。
- 只读 `pvs`/`vgs`/`lvs` 输出为空；未发现 LVM snapshot。`xfs_info` 显示 `/dev/vdb1` 为普通 XFS（8 个 allocation groups），
  不能据此推断存在可用的一致性快照。
- 在两个受限根目录两层范围内未发现名称包含 `snapshot`、`scn`、`inventory` 或 `readonly` 的现成输入文件。
- 生产应用配置文件存在，但没有把应用 schema 配置转换为 T06 inventory 输入；应用 schema 不能作为 inventory 边界。

## 结论与下一步

当前事实不足以定义一个一致性窗口。执行 T06 仍需要：

1. DBA 提供 mode `0600` 的专用 SELECT-only inventory 配置（含批准的 SCN 或一致性 reference）；
2. 存储管理员提供 `/dev/vdb1` 上附件树和设备采集树的批准 snapshot/window ID；
3. 在批准窗口内分别运行 `t06-attachment-inventory.sh` 与 `t06-filesystem-inventory.sh`，再运行本地-only reconciliation。

在这些输入到位前，不执行生产递归扫描或 freeze，不把现有单附件样本扩大为 AC-04 完成证据。
