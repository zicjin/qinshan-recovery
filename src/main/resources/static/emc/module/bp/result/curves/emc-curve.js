var emcCurve = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/curves',

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'status',
            title : 'T_EMC_CURVE.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'publishStatus',
            },
            width : 100
        }, {
            field : 'curveNo',
            title : 'T_EMC_CURVE.CURVENO',
            type : 'link',
            width : 100
        }, {
            field : 'equation',
            title : 'T_EMC_CURVE.EQUATION',
            formatter : function(i, v, row) {
                var intercept = row.intercept;
                var slope = row.slope;
                var coefficient = row.coefficient;
                if (!Gikam.isEmpty(intercept) && !Gikam.isEmpty(slope) && !Gikam.isEmpty(coefficient)) {
                    intercept = intercept.startsWith('-') ? intercept : '+' + intercept;
                    var equation = 'y=' + slope + 'x' + intercept + ',r=' + coefficient;
                    return equation;
                }
            },
            width : 200
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_CURVE.EQUIPTID',
            width : 100
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_CURVE.EQUIPTNAME'
        }, {
            field : 'testName',
            title : 'T_EMC_CURVE.TESTNAME'
        }, {
            field : 'methodName',
            title : 'T_EMC_CURVE.METHODNAME'
        }, {
            field : 'itemName',
            title : 'T_EMC_CURVE.ITEMNAME',
            width : 100
        }, {
            field : 'ext$.stdstockconcentration',
            title : 'T_EMC_CURVE.STDSTOCKCONCENTRATION',
            editor : true,
        }, {
            field : 'ext$.stduserconcentration',
            title : 'T_EMC_CURVE.STDUSERCONCENTRATION',
            editor : true,
        }, {
            field : 'ext$.internumber',
            title : 'T_EMC_CURVE.INTERNUMBER',
            editor : true,
        }, {
            field : 'markingNo',
            title : 'T_EMC_CURVE.MARKINGNO',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_CURVE.CREATEDBYNAME',
            width : 100
        }, {
            field : 'confDate',
            title : 'T_EMC_CURVE.CONFDATE',
            width : 100
        }, {
            field : 'expireDate',
            title : 'T_EMC_CURVE.EXPIREDATE',
            width : 100
        }, {
            field : 'remark',
            title : 'T_EMC_CURVE.REMARK',
            editor : true,
        } ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'status',
            title : 'T_EMC_CURVE.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'publishStatus',
            },
            width : 100
        }, {
            field : 'curveNo',
            title : 'T_EMC_CURVE.CURVENO',
            width : 100
        }, {
            field : 'confDate',
            title : 'T_EMC_CURVE.CONFDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'expireDate',
            title : 'T_EMC_CURVE.EXPIREDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'remark',
            title : 'T_EMC_CURVE.REMARK',
            type : 'richText',
            editor : true
        }, {
            field : 'ext$.standqty',
            title : 'T_EMC_CURVE.STANDQTY',
            editor : true
        }, {
            field : 'ext$.standqtyunit',
            title : 'T_EMC_CURVE.STANDQTYUNIT',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        ext$ : {
                            standqtyunit : rows[0].unitName
                        }
                    })
                }
            }
        }, {
            field : 'ext$.curvevalue',
            title : 'T_EMC_CURVE.CURVEVALUE',
            editor : true
        }, ];
    },

    getItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testid',
            title : 'T_EMC_CURVE.TESTID'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_CURVE.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_CURVE.METHODNAME'
        }, {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME'
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME'
        } ];
    },

    getQcCategoryListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'qcName',
            title : 'T_EMC_QC_CATEGORY.QCNAME'
        } ];
    },

    getResultEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_TASK_RESULT.QCNAME',
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
        }, {
            field : 'rn5',
            title : 'T_EMC_ORDER_TASK_RESULT.RN5',
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'rn6',
            title : 'T_EMC_ORDER_TASK_RESULT.RN6',
            editor : true,
            validators : [ 'number' ],
            textStyleFormatter : function() {
                return {
                    backgroundColor : 'palegreen'
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.ext$.setname === 'NUMERIC_CON' || row.ext$.setname === 'NUMERIC_CON_NOB'|| row.ext$.setname === 'NUMERIC_CON_NOI') {
                    return true;
                }
                return false;
            },
        }, {
            field : 'standQty',
            title : 'T_EMC_ORDER_TASK_RESULT.STANDQTY',
            editor : true,
            validators : [ 'number' ],
            textStyleFormatter : function() {
                return {
                    backgroundColor : 'palegreen'
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.ext$.setname === 'NUMERIC_CON' || row.ext$.setname === 'NUMERIC_CON_NOB' || row.ext$.setname === 'NUMERIC_CON_NOI') {
                    return true;
                }
                return false;
            },
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor : true,
            textStyleFormatter : function() {
                return {
                    backgroundColor : 'palegreen'
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.scriptFlag == '1') {
                    return false;
                }
                return true;
            },
            onFocus : function(field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width : 400,
                        height : 300,
                        title : Gikam.choose.getConfig('itemResult').title,
                        url : Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single : true,
                            itemId_SEQ : data.itemId,
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-run-edit-list-result-grid').setData({
                                index : index,
                                originalResult : rows[0].result,
                                reportResult : rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange : function(field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                        }

                        if (forbidden) {
                            Gikam.alert(value + '格式错误！请输入正确格式数值！');
                            this.setData({
                                index : index,
                                originalResult : ''
                            });
                        }

                    }

                }
            }
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
        }, {
            field : 'rn2',
            title : 'T_EMC_ORDER_TASK_RESULT.RN2'
        }, {
            field : 'rn3',
            title : 'T_EMC_ORDER_TASK_RESULT.RN3'
        }, {
            field : 'rn4',
            title : 'T_EMC_ORDER_TASK_RESULT.RN4'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        } ];
    },
});
