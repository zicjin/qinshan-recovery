CREATE TABLE "t_emc_internal_audit" (
  "id" NUMBER(32) NOT NULL ,
  "qualityControlNo" NVARCHAR2(18) ,
  "auditPurpose" NVARCHAR2(540) ,
  "auditBasis" NVARCHAR2(540) ,
  "auditOrg" NVARCHAR2(270) ,
  "auditGroupUser" NVARCHAR2(360) ,
  "auditTime" DATE ,
  "auditContent" NVARCHAR2(540) ,
  "userTrain" NVARCHAR2(540) ,
  "recordTable" NVARCHAR2(540) ,
  "startTime" DATE ,
  "endTime" DATE ,
  "auditDate" DATE ,
  "lastAuditDate" DATE ,
  "division" NVARCHAR2(540) ,
  "auditSituation" NVARCHAR2(540) ,
  "inconformity" NVARCHAR2(540) ,
  "manageSystem" NVARCHAR2(540) ,
  "correct" NVARCHAR2(540) ,
  "auditRequest" NVARCHAR2(540) ,
  "auditReview" NVARCHAR2(540) ,
  "sendScope" NVARCHAR2(540) ,
  "CREATEDBYID" NVARCHAR2(32) ,
  "CREATEDBYNAME" NVARCHAR2(64) ,
  "CREATEDTIME" DATE ,
  "CREATEDBYORGID" NVARCHAR2(32) ,
  "CREATEDBYORGNAME" NVARCHAR2(256) ,
  "PROCESSSTATUS" NVARCHAR2(36) 
);

COMMENT ON COLUMN "t_emc_internal_audit"."id" IS '主键';
COMMENT ON COLUMN "t_emc_internal_audit"."qualityControlNo" IS '质控编号';
COMMENT ON COLUMN "t_emc_internal_audit"."auditPurpose" IS '审核目的';
COMMENT ON COLUMN "t_emc_internal_audit"."auditBasis" IS '审核依据';
COMMENT ON COLUMN "t_emc_internal_audit"."auditOrg" IS '被审核部门';
COMMENT ON COLUMN "t_emc_internal_audit"."auditGroupUser" IS '内审组成员';
COMMENT ON COLUMN "t_emc_internal_audit"."auditTime" IS '拟计划审核时间';
COMMENT ON COLUMN "t_emc_internal_audit"."auditContent" IS '拟计划审核内容';
COMMENT ON COLUMN "t_emc_internal_audit"."userTrain" IS '内审人员培训（必要时）';
COMMENT ON COLUMN "t_emc_internal_audit"."recordTable" IS '记录表格';
COMMENT ON COLUMN "t_emc_internal_audit"."startTime" IS '内审开始时间';
COMMENT ON COLUMN "t_emc_internal_audit"."endTime" IS '内审结束时间';
COMMENT ON COLUMN "t_emc_internal_audit"."auditDate" IS '审核日期';
COMMENT ON COLUMN "t_emc_internal_audit"."lastAuditDate" IS '上次审核日期';
COMMENT ON COLUMN "t_emc_internal_audit"."division" IS '审核成员分工及审核部门';
COMMENT ON COLUMN "t_emc_internal_audit"."auditSituation" IS '审核情况综述';
COMMENT ON COLUMN "t_emc_internal_audit"."inconformity" IS '不符合项目归纳分类及分布项';
COMMENT ON COLUMN "t_emc_internal_audit"."manageSystem" IS '受审方的管理体系执行情况的整体综合评价';
COMMENT ON COLUMN "t_emc_internal_audit"."correct" IS '纠正措施或预防措施执行情况';
COMMENT ON COLUMN "t_emc_internal_audit"."auditRequest" IS '审核后续活动要求';
COMMENT ON COLUMN "t_emc_internal_audit"."auditReview" IS '审核综述';
COMMENT ON COLUMN "t_emc_internal_audit"."sendScope" IS '发送范围';
COMMENT ON COLUMN "t_emc_internal_audit"."CREATEDBYID" IS '制单人ID';
COMMENT ON COLUMN "t_emc_internal_audit"."CREATEDBYNAME" IS '制单人名称';
COMMENT ON COLUMN "t_emc_internal_audit"."CREATEDTIME" IS '制单日期';
COMMENT ON COLUMN "t_emc_internal_audit"."CREATEDBYORGID" IS '制单人单位编码';
COMMENT ON COLUMN "t_emc_internal_audit"."CREATEDBYORGNAME" IS '制单人单位名称';
COMMENT ON COLUMN "t_emc_internal_audit"."PROCESSSTATUS" IS '流程状态';

INSERT INTO t_core_table(ID, AUDITABLE, ATTACHMENT) VALUES ('T_EMC_INTERNAL_AUDIT', '1', '1');

INSERT INTO t_core_at_config(ID, AUDITTRAIL, VERSION) VALUES ('T_EMC_INTERNAL_AUDIT', '1', NULL);

INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'qualityControlNo', '1', NULL);

INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditPurpose', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditBasis', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditOrg', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditGroupUser', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditTime', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditContent', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'userTrain', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'recordTable', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'startTime', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'endTime', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditDate', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'lastAuditDate', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'division', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditSituation', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'inconformity', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'manageSystem', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'correct', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditRequest', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'auditReview', '1', NULL);
INSERT INTO t_core_at_config_line(ID, CONFIGID, COLUMNID, AUDITTRAIL, VERSION) VALUES (SEQ_CORE.NEXTVAL, 'T_EMC_INTERNAL_AUDIT', 'sendScope', '1', NULL);


INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('4030', '内审计划', '/secure/emc/module/item/internal-audits', '40', 4030, '内审计划', NULL, '001.svg', NULL);
INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('403001', '内审计划申请', '/secure/emc/module/item/internal-audits/page/edit-list', '4030', 403001, '内审计划申请', NULL, '001.svg', NULL);
INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('403002', '内审计划审批', '/secure/emc/module/item/internal-audits/page/audit-list', '4030', 403002, '内审计划审批', NULL, '001.svg', NULL);
INSERT INTO t_core_menu(ID, MENUNAME, HREF, PARENTID, ORDERNO, MENUINITNAME, ICON, MENUICON, TODOICON) VALUES ('403003', '内审计划查询', '/secure/emc/module/item/internal-audits/page/search-list', '4030', 403003, '内审计划查询', NULL, '001.svg', NULL);


INSERT INTO t_core_sequence(ID, PATTERN, JSONPARAM) VALUES ('T_EMC_Internal_Audit', 'NBSH[yyyy][nnn]', '');


INSERT INTO t_core_menu_ext(ID, SERVICENAME, TABLENAME, TYPE, ENABLE, EXT) VALUES ('9330', '内审计划', 'T_EMC_INTERNAL_AUDIT', 'audit_def', '1', NULL);

