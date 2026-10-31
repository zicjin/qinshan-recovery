-- 建表语句
CREATE TABlE DBO.T_EMC_FIXEDASSET(
    ID BIGINT  NOT NULL,
     CREATEDBYID VARCHAR(32)  NULL ,
     CREATEDBYNAME VARCHAR(64)  NULL ,
     CREATEDTIME DATETIME NULL ,
     CREATEDBYORGID VARCHAR(32)  NULL ,
     CREATEDBYORGNAME VARCHAR(256)  NULL ,
     ASSETCATEGORY VARCHAR(36)  NULL ,
     ASSETNAME VARCHAR(36)  NULL ,
     MODEL VARCHAR(36)  NULL ,
     QUANTITY BIGINT NULL ,
     PRICE decimal(15,4) NULL ,
     TOTALAMOUNT decimal(15,4) NULL ,
     PURCHASETIME DATETIME  NULL ,
     FACTORYNO VARCHAR(36)  NULL ,
     RESPONSITYORGID VARCHAR(36)  NULL ,
     USEORGID VARCHAR(36)  NULL ,
     USEMODIFY VARCHAR(36)  NULL ,
     STOREPLACE VARCHAR(36)  NULL ,
     REMARK VARCHAR(540)  NULL ,
 CONSTRAINT PK_EMC_FIXEDASSET PRIMARY KEY(ID));
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'固定资产管理表' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET';
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'主键' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'id';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'制单人编码' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'createdById';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'制单人名称' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'createdByName';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'制单时间' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'createdTime';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'制单人单位编码' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'createdByOrgId';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'制单人单位名称' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'createdByOrgName';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'资产类型' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'assetCategory';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'资产名称' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'ASSETNAME';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'型号' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'model';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'数量' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'quantity';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'单价金额' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'price';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'总价金额' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'totalAmount';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'购入时间' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'purchaseTime';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'出厂编号' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'factoryNo';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'责任科室Id' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'responsityOrgId';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'使用科室Id' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'useOrgId';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'使用变更' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'useModify';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'存放地点' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'storePlace';
 
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_EMC_FIXEDASSET', @level2type=N'COLUMN',@level2name=N'remark';
 


-- 创建菜单语句
INSERT INTO T_CORE_MENU(ID,MENUNAME,HREF,PARENTID,ORDERNO, APPLICABLE, MENUINITNAME )
VALUES ('100534', '固定资产管理', '/secure/emc/module/mdm/basemdm/fixedassets/page/edit-list', '1005','100534', 'org','固定资产管理');
    
INSERT INTO T_CORE_TABLE (ID,AUDITABLE,ATTACHMENT)
VALUES ('T_EMC_FIXEDASSET', '0', '1');

INSERT INTO t_core_code_category(ID, CODECATEGORYNAME, CODECLASSID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME) VALUES ('assetCategory', '资产类型', 'sys', '资产类型', 83, NULL, NULL, NULL, NULL);


INSERT INTO t_core_code(ID, CODENAME, CODECATEGORYID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME, LASTSUSPENDEDFLAG, LASTSUSPENDEDBYID, LASTSUSPENDEDBYNAME, LASTSUSPENDEDTIME, CODE) 
VALUES (NEXT VALUE FOR SEQ_CORE, '电器设备', 'assetCategory', NULL, 1, 0, null, null, null,0, NULL, NULL, NULL, '1');

INSERT INTO t_core_code(ID, CODENAME, CODECATEGORYID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME, LASTSUSPENDEDFLAG, LASTSUSPENDEDBYID, LASTSUSPENDEDBYNAME, LASTSUSPENDEDTIME, CODE) 
VALUES (NEXT VALUE FOR SEQ_CORE, '电子产品及通信设备', 'assetCategory', NULL, 2, 0, null, null, null,0, NULL, NULL, NULL, '2');

INSERT INTO t_core_code(ID, CODENAME, CODECATEGORYID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME, LASTSUSPENDEDFLAG, LASTSUSPENDEDBYID, LASTSUSPENDEDBYNAME, LASTSUSPENDEDTIME, CODE) 
VALUES (NEXT VALUE FOR SEQ_CORE, '房屋', 'assetCategory', NULL, 3, 0, null, null, null,0, NULL, NULL, NULL, '3');

INSERT INTO t_core_code(ID, CODENAME, CODECATEGORYID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME, LASTSUSPENDEDFLAG, LASTSUSPENDEDBYID, LASTSUSPENDEDBYNAME, LASTSUSPENDEDTIME, CODE) 
VALUES (NEXT VALUE FOR SEQ_CORE, '家具用具等', 'assetCategory', NULL, 4, 0, null, null, null,0, NULL, NULL, NULL, '4');

INSERT INTO t_core_code(ID, CODENAME, CODECATEGORYID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME, LASTSUSPENDEDFLAG, LASTSUSPENDEDBYID, LASTSUSPENDEDBYNAME, LASTSUSPENDEDTIME, CODE) 
VALUES (NEXT VALUE FOR SEQ_CORE, '交通运输设备', 'assetCategory', NULL, 5, 0, null, null, null,0, NULL, NULL, NULL, '5');

INSERT INTO t_core_code(ID, CODENAME, CODECATEGORYID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME, LASTSUSPENDEDFLAG, LASTSUSPENDEDBYID, LASTSUSPENDEDBYNAME, LASTSUSPENDEDTIME, CODE) 
VALUES (NEXT VALUE FOR SEQ_CORE, '通用', 'assetCategory', NULL, 6, 0, null, null, null,0, NULL, NULL, NULL, '6');

INSERT INTO t_core_code(ID, CODENAME, CODECATEGORYID, REMARK, ORDERNO, VERSION, CREATEDBYID, CREATEDBYNAME, CREATEDTIME, LASTSUSPENDEDFLAG, LASTSUSPENDEDBYID, LASTSUSPENDEDBYNAME, LASTSUSPENDEDTIME, CODE) 
VALUES (NEXT VALUE FOR SEQ_CORE, '仪器仪表、计量标准器', 'assetCategory', NULL, 7, 0, null, null, null,0, NULL, NULL, NULL, '7');
    
