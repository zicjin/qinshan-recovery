var emcPretreatment = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/pretreatments',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field: "ext$.projno",
            title: "T_EMC_PROJECT.PROJNO",
        }, {
            field: "ext$.ordercontainerno",
            title: "T_EMC_ORDER.ORDERNO",
        }, {
            field: "ext$.labelno",
            title: "T_EMC_ORDER.LABELNO2",
        }, {
            field: "ext$.accepteddate",
            title: "T_EMC_ORDER.ACCEPTEDDATE",
        }, {
            field: "ext$.testname",
            title: "T_EMC_ORDER_TASK.TESTNAME",
        }, {
            field: "purifyStd",
            title: "T_EMC_PRETREATMENT.PURIFYSTD",
            editor: true
        }, {
            field: "purifyStdAdditive",
            title: "T_EMC_PRETREATMENT.PURIFYSTDADDITIVE",
            editor: true
        }, {
            field: "purifyStdUnit",
            title: "T_EMC_PRETREATMENT.PURIFYSTDUNIT",
            editor: true,
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        purifyStdUnit : rows[0].unitName,
                    });
                }
            },
            width : 100,
        }, {
            field: "purifyStdTime",
            title: "T_EMC_PRETREATMENT.PURIFYSTDTIME",
            editor: true,
            type : 'dateTime'
        }, {
            field: "purifyStdUserName",
            title: "T_EMC_PRETREATMENT.PURIFYSTDUSERNAME",
            editor: true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: this.getActivedRow().id,
                        purifyStdUserId : rows[0].id,
                        purifyStdUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id: this.getActivedRow().id,
                    purifyStdUserId : '',
                    purifyStdUserName : '',
                });
            },
        }, {
            field: "ext$.extractmethod",
            title: "T_EMC_PRETREATMENT.EXTRACTMETHODID",
            formatter : function(i, v ,r) {
                let value = r.ext$.extractmethod ? r.ext$.extractmethod : '编辑提取方法'
                return '<a href="javascript:;">' + value + '</a>'
            },
        }, {
            field: "orderDivision",
            title: "T_EMC_PRETREATMENT.ORDERDIVISION",
            editor: true
        }, {
            field: "purifyMethod",
            title: "T_EMC_PRETREATMENT.PURIFYMETHODID",
            editor : true,
            type : 'richText'
        }, /*{
            field: "ext$.purifymethod",
            title: "T_EMC_PRETREATMENT.PURIFYMETHODID",
            formatter : function(i, v ,r) {
                let value = r.ext$.purifymethod ? r.ext$.purifymethod : '编辑净化方法'
                return '<a href="javascript:;">' + value + '</a>'
            },
        },*/
        {
            field: "injectStd",
            title: "T_EMC_PRETREATMENT.INJECTSTD",
            editor: true
        }, {
            field: "injectStdAdditive",
            title: "T_EMC_PRETREATMENT.INJECTSTDADDITIVE",
            editor: true
        }, {
                field: "injectStdUnit",
                title: "T_EMC_PRETREATMENT.INJECTSTDUNIT",
                editor: true,
                type : 'choose',
                category : 'unit',
                single : true,
                onAfterChoose : function(rows, index) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            index : index,
                            injectStdUnit : rows[0].unitName,
                        });
                    }
                },
                width : 100,
        }, {
            field: "injectStdVolume",
            title: "T_EMC_PRETREATMENT.INJECTSTDVOLUME",
            editor: true
        },
        {
            field: "injectStdUserName",
            title: "T_EMC_PRETREATMENT.INJECTSTDUSERNAME",
            editor: true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: this.getActivedRow().id,
                        injectStdUserId : rows[0].id,
                        injectStdUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id: this.getActivedRow().id,
                    injectStdUserId : '',
                    injectStdUserName : '',
                });
            },
        },
        {
            field: "remark",
            title: "T_EMC_PRETREATMENT.REMARK",
            editor: true
        } ];
    },

    getPurifyMethodListColumns : function() {
        return [ {
            radio : true
        }, {
            field: "purifyMethod",
            title: "T_EMC_PURIFY_METHOD.PURIFYMETHOD",
            editor: true
        },
            {
                field: "model",
                title: "T_EMC_PURIFY_METHOD.MODEL",
                editor: true
            },
            {
                field: "eluent",
                title: "T_EMC_PURIFY_METHOD.ELUENT",
                editor: true
            },
            {
                field: "eluentAmount",
                title: "T_EMC_PURIFY_METHOD.ELUENTAMOUNT",
                editor: true
            } ];
    },

    getExtractMethodListColumns : function() {
        return [ {
            radio : true
        }, {
            field: "extractMethod",
            title: "T_EMC_EXTRACT_METHOD.EXTRACTMETHOD",
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'extractMethod',
            },
            onChange : function(field, value, rowIndex, oldValue, text) {
                if (!Gikam.isEmpty(value)) {
                    var selectItem = [];
                    switch (value) {
                        case '索氏提取':
                            selectItem = [
                                { text: '200mL正己烷+200mL二氯甲烷', value : '200mL正己烷+200mL二氯甲烷'},
                                { text: '甲苯400mL', value : '甲苯400mL'},
                            ];
                            break;
                        case '快速溶剂萃取仪':
                            selectItem = [
                                { text: '50mL正己烷+50mL二氯甲烷', value : '50mL正己烷+50mL二氯甲烷'},
                                { text: '甲苯100mL', value : '甲苯100mL'},
                            ];
                            break;
                        case '液液萃取':
                            selectItem = [
                                { text: '二氯甲烷300mL', value : '二氯甲烷300mL'},
                                { text: '二氯甲烷3000mL', value : '二氯甲烷3000mL'},
                            ];
                            break;
                        default:
                            break;
                    }
                    var solventColumn = {
                        field : 'solvent',
                        editor : true,
                        type : 'select',
                        title : 'T_EMC_EXTRACT_METHOD.SOLVENT',
                        editable : true,
                        items: selectItem
                    };
                    Gikam.getComp('emc-extract-method-list-grid').getColumn('solvent').setOptions(solventColumn);
                }
            },
            editor: true
        },
        {
            field: "solvent",
            title: "T_EMC_EXTRACT_METHOD.SOLVENT",
            type : 'select',
            editor: true
        },
        // {
        //     field: "solventAmount",
        //     title: "T_EMC_EXTRACT_METHOD.SOLVENTAMOUNT",
        //     editor: true
        // },
        {
            field: "startTime",
            title: "T_EMC_EXTRACT_METHOD.STARTTIME",
            editor: true,
            type : 'dateTime'
        },
        {
            field: "endTime",
            title: "T_EMC_EXTRACT_METHOD.ENDTIME",
            editor: true,
            type : 'dateTime'
        },
        {
            field: "ext$.equiptname",
            title: "T_EMC_EXTRACT_METHOD.EQUIPTID",
            editor: true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    org_filter : '1',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: this.getActivedRow().id,
                        equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id: this.getActivedRow().id,
                    equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        } ];
    },

});
