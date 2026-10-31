var emcMethod = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/methods',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'methodName',
            title : 'T_EMC_METHOD.METHODNAME',
            editor : true,
            width : 238,
        },/*{
            field : 'ext$.methodtype',
            title : 'T_EMC_METHOD.METHODTYPE',
            editor : true,
            width : 120,
        }*/ ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'methodName',
            title : 'T_EMC_METHOD.METHODNAME',
            type : 'link',
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_METHOD_VERSION.TESTMETHODBASIS',
        }, {
            field : 'maxVersionNo',
            title : 'T_EMC_METHOD.LATESTVERSION',
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'methodName',
            title : 'T_EMC_METHOD.METHODNAME',
            validators : [ 'unique[' + emcMethod.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,60]' ],
            preInsert : true
        } ];
    },

    getVersionListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'versionNo',
            title : 'T_EMC_METHOD_VERSION.VERSIONNO'
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_METHOD_VERSION.TESTMETHODBASIS',
            editor : true,
            validators : [ 'strLength[0,200]' ],
        }, {
            field : 'trialScope',
            title : '适用范围',
            editor : true,
        }, {
            field : 'beginDate',
            title : 'T_EMC_METHOD_VERSION.BEGINDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'expireDate',
            title : 'T_EMC_METHOD_VERSION.EXPIREDATE',
            editor : true,
            type : 'date'
        }, /*{
            field : 'ext$.elntmplname',
            title : 'T_EMC_METHOD_VERSION.ELNTMPLNAME',
            editor : true,
            type : 'choose',
            category : 'eln-tmpl',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        elnTmplId : rows[0].id,
                        ext$ : {
                            elntmplname : rows[0].elnTmplName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    elnTmplId : '',
                    ext$ : {
                        elntmplname : ''
                    }
                });
            },
            formatter : function(field, value) {
                if (value) {
                    var data = Gikam.getComp('emc-method-detail-version-list-grid').getData()[this.rowIndex];
                    return '<a href="javascript:;" onclick="Gikam.viewElnTmpl(\'' + data.elnTmplId + '\')">' + value + '</a>';
                } else {
                    return '';
                }

            }
        },*/ {
            field : 'reportConfigLineId',
            title : 'T_EMC_METHOD_VERSION.REPORTCONFIGLINEID',
            editor : true,
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'analReport'
            },
            validators : [ 'notEmpty' ]
        }, {
            title : 'T_EMC_METHOD_VERSION.FILE',
            field : 'ext$.filename',
            type : 'link'
        }, {
            title : 'T_EMC_METHOD_VERSION.DOWNLOAD',
            field : 'ext$.fileid',
            formatter : function(i, v, r) {
                if (Gikam.isEmpty(r.ext$.fileid)) {
                    return ;
                }
                return Gikam.printf('<a href="javascript:;" onclick="emcMethod.editPage.downloadVersionFile(\'{fileid}\');">{name}</a>', {
                    fileid : r.ext$.fileid,
                    name : Gikam.propI18N('T_EMC_METHOD_VERSION.DOWNLOAD')
                });
            }
        } ];
    },

    getEstandardListColumns : function(){
        return [ {
            checkbox : true,
        }, {
            field : 'ext$.status',
            title : 'T_EMC_METHOD.STATUS',
            width : 100
        }, {
            field : 'methodName',
            title : 'T_EMC_METHOD.METHODNAME',
            width : 300
        } ];
    }
});
