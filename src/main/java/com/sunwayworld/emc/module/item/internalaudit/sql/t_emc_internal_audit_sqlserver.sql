/*
 Navicat Premium Data Transfer

 Source Server         : mssql
 Source Server Type    : SQL Server
 Source Server Version : 12005000
 Source Host           : 58.56.21.146:1142
 Source Catalog        : emc_dev
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 12005000
 File Encoding         : 65001

 Date: 06/12/2019 10:42:07
*/


-- ----------------------------
-- Table structure for t_emc_internal_audit
-- ----------------------------


CREATE TABLE [dbo].[t_emc_internal_audit] (
  [id] decimal(32)  NOT NULL,
  [qualityControlNo] nvarchar(18) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditPurpose] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditBasis] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditOrg] nvarchar(270) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditGroupUser] nvarchar(360) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditTime] datetime2(7)  NULL,
  [auditContent] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [userTrain] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [recordTable] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [startTime] datetime2(7)  NULL,
  [endTime] datetime2(7)  NULL,
  [auditDate] datetime2(7)  NULL,
  [lastAuditDate] datetime2(7)  NULL,
  [division] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditSituation] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [inconformity] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [manageSystem] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [correct] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditRequest] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [auditReview] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [sendScope] nvarchar(540) COLLATE Chinese_PRC_CI_AS  NULL,
  [CREATEDBYID] nvarchar(32) COLLATE Chinese_PRC_CI_AS  NULL,
  [CREATEDBYNAME] nvarchar(64) COLLATE Chinese_PRC_CI_AS  NULL,
  [CREATEDTIME] datetime2(7)  NULL,
  [CREATEDBYORGID] nvarchar(32) COLLATE Chinese_PRC_CI_AS  NULL,
  [CREATEDBYORGNAME] nvarchar(256) COLLATE Chinese_PRC_CI_AS  NULL,
  [PROCESSSTATUS] nvarchar(36) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[t_emc_internal_audit] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'质控编号',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'qualityControlNo'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审核目的',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditPurpose'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审核依据',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditBasis'
GO

EXEC sp_addextendedproperty
'MS_Description', N'被审核部门',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditOrg'
GO

EXEC sp_addextendedproperty
'MS_Description', N'内审组成员',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditGroupUser'
GO

EXEC sp_addextendedproperty
'MS_Description', N'拟计划审核时间',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'拟计划审核内容',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditContent'
GO

EXEC sp_addextendedproperty
'MS_Description', N'内审人员培训（必要时）',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'userTrain'
GO

EXEC sp_addextendedproperty
'MS_Description', N'记录表格',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'recordTable'
GO

EXEC sp_addextendedproperty
'MS_Description', N'内审开始时间',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'startTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'内审结束时间',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'endTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审核日期',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditDate'
GO

EXEC sp_addextendedproperty
'MS_Description', N'上次审核日期',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'lastAuditDate'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审核成员分工及审核部门',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'division'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审核情况综述',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditSituation'
GO

EXEC sp_addextendedproperty
'MS_Description', N'不符合项目归纳分类及分布项',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'inconformity'
GO

EXEC sp_addextendedproperty
'MS_Description', N'受审方的管理体系执行情况的整体综合评价',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'manageSystem'
GO

EXEC sp_addextendedproperty
'MS_Description', N'纠正措施或预防措施执行情况',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'correct'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审核后续活动要求',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditRequest'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审核综述',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'auditReview'
GO

EXEC sp_addextendedproperty
'MS_Description', N'发送范围',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'sendScope'
GO

EXEC sp_addextendedproperty
'MS_Description', N'制单人ID',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'CREATEDBYID'
GO

EXEC sp_addextendedproperty
'MS_Description', N'制单人名称',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'CREATEDBYNAME'
GO

EXEC sp_addextendedproperty
'MS_Description', N'制单日期',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'CREATEDTIME'
GO

EXEC sp_addextendedproperty
'MS_Description', N'制单人单位编码',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'CREATEDBYORGID'
GO

EXEC sp_addextendedproperty
'MS_Description', N'制单人单位名称',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'CREATEDBYORGNAME'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程状态',
'SCHEMA', N'dbo',
'TABLE', N't_emc_internal_audit',
'COLUMN', N'PROCESSSTATUS'
GO

INSERT INTO t_core_table(ID, AUDITABLE, ATTACHMENT) VALUES ('T_EMC_INTERNAL_AUDIT', '1', '1');

INSERT INTO t_core_at_config(ID, AUDITTRAIL, VERSION) VALUES ('T_EMC_INTERNAL_AUDIT', '1', NULL);

INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'qualityControlNo', '1', NULL);

INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditPurpose', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditBasis', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditOrg', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditGroupUser', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditTime', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditContent', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'userTrain', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'recordTable', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'startTime', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'endTime', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditDate', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'lastAuditDate', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'division', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditSituation', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'inconformity', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'manageSystem', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'correct', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditRequest', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'auditReview', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (NEXT VALUE FOR SEQ_CORE, 'T_EMC_INTERNAL_AUDIT', 'sendScope', '1', NULL);


INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('4030', '内审计划', '/secure/emc/module/item/internal-audits', '40', 4030, '内审计划', NULL, '001.svg', NULL);
INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('403001', '内审计划申请', '/secure/emc/module/item/internal-audits/page/edit-list', '4030', 403001, '内审计划申请', NULL, '001.svg', NULL);
INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('403002', '内审计划审批', '/secure/emc/module/item/internal-audits/page/audit-list', '4030', 403002, '内审计划审批', NULL, '001.svg', NULL);
INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('403003', '内审计划查询', '/secure/emc/module/item/internal-audits/page/search-list', '4030', 403003, '内审计划查询', NULL, '001.svg', NULL);


INSERT INTO t_core_sequence(ID, PATTERN, JSONPARAM) VALUES ('T_EMC_Internal_Audit', 'NBSH[yyyy][nnn]', '');


INSERT INTO t_core_menu_ext(ID, SERVICENAME, TABLENAME, TYPE, ENABLE, EXT) VALUES ('9330', '内审计划', 'T_EMC_INTERNAL_AUDIT', 'audit_def', '1', NULL);

