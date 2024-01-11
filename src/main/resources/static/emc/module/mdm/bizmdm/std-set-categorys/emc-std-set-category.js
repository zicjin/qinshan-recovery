var emcStdSetCategory = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'standardSet',
            title : 'T_EMC_STD_SET_CATEGORY.STANDARDSET'
        }, {
            field : 'standardSetDesc',
            title : 'T_EMC_STD_SET_CATEGORY.STANDARDSETDESC',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'standardSet',
            title : 'T_EMC_STD_SET_CATEGORY.STANDARDSET',
            validators : [ 'strLength[0,36]' ],
            preInsert : true
        }, {
            field : 'standardSetDesc',
            title : 'T_EMC_STD_SET_CATEGORY.STANDARDSETDESC'
        }, ]
    },

    getStandardSetListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'setName',
            title : 'T_EMC_STANDARD_SET.SETNAME',
            validators : [ 'strLength[0,36]' ],
            editor : true
        }, {
            field : 'setDesc',
            title : 'T_EMC_STANDARD_SET.SETDESC',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'script1',
            title : 'T_EMC_STANDARD_SET.SCRIPT1',
            filter : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcStdSetCategory.editPage.popScript(\'{id}\',\'{field}\')">{name}</a>', {
                    id : r.id,
                    field : 'script1',
                    name : Gikam.isEmpty(r.script1) ? '点击维护' : '已维护'
                });
            }
        }, {
            field : 'validateScript',
            title : 'T_EMC_STANDARD_SET.VALIDATESCRIPT',
            filter : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcStdSetCategory.editPage.popScript(\'{id}\',\'{field}\')">{name}</a>', {
                    id : r.id,
                    field : 'validateScript',
                    name : Gikam.isEmpty(r.validateScript) ? '点击维护' : '已维护'
                });
            }
        }, {
            field : 'script2',
            title : 'T_EMC_STANDARD_SET.SCRIPT2',
            filter : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcStdSetCategory.editPage.popScript(\'{id}\',\'{field}\')">{name}</a>', {
                    id : r.id,
                    field : 'script2',
                    name : Gikam.isEmpty(r.script2) ? '点击维护' : '已维护'
                });
            }
        }, {
            field : 'startDate',
            title : 'T_EMC_STANDARD_SET.STARTDATE',
            type : 'date',
            editor : true
        }, {
            field : 'validDate',
            title : 'T_EMC_STANDARD_SET.VALIDDATE',
            type : 'date',
            editor : true
        }, ]
    },

    getFieldSetListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'fieldName',
            title : 'T_EMC_FIELD_SET.FIELDNAME'
        }, {
            field : 'fieldTitle',
            title : 'T_EMC_FIELD_SET.FIELDTITLE',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'display',
            title : 'T_EMC_FIELD_SET.DISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'displayFieldStatus'
            },
            editor : true
        }, {
            field : 'width',
            title : 'T_EMC_FIELD_SET.WIDTH',
            type : 'number',
            hasArrow : true,
            range : '[0,9999]',
            isInteger : true,
            editor : true
        } ]
    },

    getFieldChooseListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'name',
            title : 'T_EMC_TABLE_FIELD.FIELDNAME'
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/std-set-categorys'

});
