var emcDataCorrelation = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/data-correlations',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_DATA_CORRELATION.ACTIVATEDFLAG',
            width : 80,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            },
            onChange : function(field, value, rowIndex, oldValue, text){
                this.setData({
                    index : rowIndex,
                    activatedFlag : value,
                    activatedById : workspace.user.id,
                    activatedByName : workspace.user.userName,
                    activatedTime : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd hh:mm:ss')
                });
            }
        }, {
            field : 'relateCode',
            title : 'T_EMC_DATA_CORRELATION.RELATECODE',
            width : 200
        }, {
            field : 'relateName',
            title : 'T_EMC_DATA_CORRELATION.RELATENAME',
            width : 200,
            editor : true
        }, {
            field : 'relateScript',
            title : 'T_EMC_DATA_CORRELATION.RELATESCRIPT',
            width : 120,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcDataCorrelation.editPage.editRelateScript(\'{index}\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.relateScript) ? '点击维护' : '已维护'
                });
            },
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'relateCode',
            title : 'T_EMC_DATA_CORRELATION.RELATECODE',
            preInsert : true
        }, {
            field : 'relateName',
            title : 'T_EMC_DATA_CORRELATION.RELATENAME',
            preInsert : true
        } ];
    },


    getItemEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testId',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTID',
            width : 130
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTNAME',
            width : 130
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_DATA_CORRELATION_ITEM.METHODNAME',
            width : 130
        }, {
            field : 'ext$.itemaliasname',
            title : 'T_EMC_DATA_CORRELATION_ITEM.ITEMALIASNAME',
            width : 130
        }, {
            field : 'ext$.lowlimit',
            title : 'T_EMC_DATA_CORRELATION_ITEM.LOWLIMIT',
            width : 130
        }, {
            field : 'ext$.highlimit',
            title : 'T_EMC_DATA_CORRELATION_ITEM.HIGHLIMIT',
            width : 130
        } ];
    },

    getScriptFormFields : function() {
        return [ {
            field : 'func',
            title : 'T_EMC_DATA_CORRELATION.FUNC',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'calfunc'
            },
            onChange : function() {
                var formData = Gikam.getComp('emc-data-correlation-script-detail-form').getData();
                var script = formData.func + "(); ";
                if (Gikam.isEmpty(formData.script)) {
                    script = script;
                } else {
                    script = formData.script + '\r' + script;
                }
                this.setData({
                    'relateScript' : script
                });
            }
        }, {
            field : 'relateScript',
            title : 'T_EMC_DATA_CORRELATION.RELATESCRIPT',
            type : 'textarea',
            rows : 10,
            height : 300,
            onChange : function(field, value) {
                this.setData({
                    'relateScript' : value
                });
            }
        } ]
    },
    
    getItemChooseMultEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testcategoryid',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTCATEGORYID',
            width : 130,
            type : 'select',
            search : true,
            category : 'testCategory'
        }, {
            field : 'testId',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTID',
            width : 80
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTNAME',
            width : 130
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_DATA_CORRELATION_ITEM.METHODNAME',
            width : 130
        }, {
            field : 'ext$.itemname',
            title : 'T_EMC_DATA_CORRELATION_ITEM.ITEMNAME',
            width : 130
        }, {
            field : 'ext$.itemaliasname',
            title : 'T_EMC_DATA_CORRELATION_ITEM.ITEMALIASNAME',
            width : 130
        } ];
    }
});
