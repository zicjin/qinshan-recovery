var limsReportTemplate = Gikam.getPageObject({

    getEditListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'activatedFlag',
            title : 'T_LIMS_REPORT_TEMPLATE.ACTIVATEDFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            }
        }, {
            field : 'templateCode',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATECODE',
            width : 100,
        }, {
            field : 'templateName',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATENAME',
            editor : true,
            type :  'richText',
            validators : ['notEmpty', 'strLength(0,200)'],
            width : 100,
        }, {
            field : 'createdByName',
            title : '制作人',
        }, {
            field : 'param',
            title : 'T_LIMS_REPORT_TEMPLATE.PARAM',
            editor : true,
            type :  'richText',
            validators : ['strLength(0, 200)'],
            width : 100,
        }, {
            field : 'templateType',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATETYPE',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            editor : true,
            param : {
                codeCategoryId : 'templateType'
            }
        }, {
            field : 'templateFileName',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATEFILENAME',
            type : 'link',
            width : 150
        }, {
            field : 'option',
            title : 'T_LIMS_REPORT_TEMPLATE.OPTION',
            contentAlign : 'center',
            formatter : function(index, value, row) {
                var fileFlag = Gikam.isNotEmpty(row.templateFileId);
                return Gikam.printf('<a href="javascript:;" onclick="limsReportTemplate.editPage.uploadTemplateFile(\'{id}\', \'{index}\', \'{dbTable}\')">{name}</a>&nbsp;&nbsp;<a href="javascript:;" onclick="limsReportTemplate.download(\'{fileId}\', false)">{download}</a>', {
                    id : row.id,
                    index : index,
                    dbTable : 'T_LIMS_REPORT_TEMPLATE',
                    fileId : row.templateFileId,
                    name : fileFlag ? Gikam.propI18N('LIMS.MODULE.REPORTTEMPLATES.BUTTON.UPDATE') : Gikam.propI18N('LIMS.MODULE.REPORTTEMPLATES.BUTTON.UPLOADER'),
                    download : Gikam.propI18N('LIMS.BASEMODULE.BUTTON.FILE_DOWNLOAD'),
                });
            },
            width : 200
        }, {
            field : 'remark',
            title : 'T_LIMS_REPORT_TEMPLATE.REMARK',
            type : 'richText',
            editor : true,
            validators : ['strLength(0, 300)']
        }, {
            field : 'defaultValue',
            title : 'T_LIMS_REPORT_TEMPLATE.DEFAULTVALUE',
            validators : ['strLength(0, 360)'],
            editor : true,
        }, ];
    },

    getChooseListColumns : function() {
        return [{
            field : 'templateCode',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATECODE',
            width : 100,
        }, {
            field : 'templateName',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATENAME',
            width : 100,
        }, {
            field : 'param',
            title : 'T_LIMS_REPORT_TEMPLATE.PARAM',
            width : 100,
        }/*, {
            field : 'templateType',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATETYPE',
        }*/, {
            field : 'templateFileName',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATEFILENAME',
            type : 'link',
            width : 150
        }, {
            field : 'remark',
            title : 'T_LIMS_REPORT_TEMPLATE.REMARK'
        }];
    },

    getAddFormFields : function() {
        return [{
            field : 'templateName',
            title : 'T_LIMS_REPORT_TEMPLATE.TEMPLATENAME',
            preInsert : true,
            validators : ['notEmpty']
        }, {
            field : 'remark',
            title : 'T_LIMS_REPORT_TEMPLATE.REMARK',
            preInsert : true,
            validators : ['notEmpty']
        }];
    },

    getTemplateSourceListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'sourceCode',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCECODE',
            //type : 'link',
            editor : true,
            validators : ['notEmpty'],
        }, {
            field : 'sourceTable',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCETABLE',
            editor : true,
        }, {
            field : 'sourceName',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCENAME',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'sourceType',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCETYPE',
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'templateSourceType'
            },
            validators : ['notEmpty']
        }, {
            field : 'param',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.PARAM',
            editor : true,
        }, {
            field : 'parentSourceCode',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.PARENTSOURCECODE',
            editor : true,
            validators : ['strLength(0,30]']
        }, {
            field : 'remark',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.REMARK',
            type : 'richText',
            editor : true,
        }];
    },
    getTemplateSourceFormFields : function() {
        return [{
            field : 'sourceCode',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCECODE',
            preInsert : true,
            validators : ['notEmpty'],
        }, {
            field : 'sourceTable',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCETABLE',
            preInsert : true,
        }, {
            field : 'sourceName',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCENAME',
            preInsert : true,
            validators : ['notEmpty']
        }, {
            field : 'sourceType',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SOURCETYPE',
            preInsert : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'templateSourceType'
            },
            validators : ['notEmpty']
        }, {
            field : 'param',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.PARAM',
        }, {
            field : 'parentSourceCode',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.PARENTSOURCECODE',
            editor : true,
            validators : ['strLength(0,30]']
        }, {
            field : 'remark',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.REMARK',
            colspan : 2
        }, {
            field : 'resolveFunction',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.RESOLVEFUNCTION',
            colspan : 2
        }, {
            field : 'sql',
            title : 'T_LIMS_REPORT_TEMPLATE_SOURCE.SQL',
            type : 'textarea',
            height : 260,
            rows : 10,
            colspan : 2,
            validators : ['notEmpty', 'strLength(0,]'],
        }];
    },

    getTemplateColumnListColumns : function() {
        return [{
            field : 'columnName',
            title : 'T_LIMS_REPORT_TEMPLATE_COLUMN.COLUMNNAME',
        }, {
            field : 'columnDesc',
            title : 'T_LIMS_REPORT_TEMPLATE_COLUMN.COLUMNDESC',
            editor : true
        }];
    },

    download : function(fileId) {
        Gikam.getJson(Gikam.IFM_CONTEXT + '/core/module/item/files/' + fileId).done(function (file) {
            Gikam.download(file.downloadUrl);
        });
    },

    baseUrl : IFM_CONTEXT + '/secure/lims/module/report-templates',
    fileUrl : IFM_CONTEXT + '/core/module/item/files',
});
