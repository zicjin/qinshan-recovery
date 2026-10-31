emcProject.searchDetailPage = {
    getBaseInfoTab : function(row) {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-project-search-deatil-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-search-deatil-base-info-panel',
                items : [ {
                    type : 'form',
                    columns : 3,
                    id : 'emc-project-search-deatil-base-info-form',
                    fields : _this.getBaseInfoFormFields(row.ext$.formstyle),
                    url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                        id : row.id
                    }),
                    readonly : true
                }, ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-search-deatil-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    bizId : row.id,
                    readonly : true
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.AUDIT_RECORD',
                items : [ {
                    type : 'grid',
                    id : 'emc-project-search-deatil-record-grid',
                    columns : emcProject.getAuditRecordListColumns(),
                    url : IFM_CONTEXT + '/secure/emc/module/bp/audit-records/queries',
                    requestData : {
                        projId_SEQ : row.id
                    },
                    group : {
                        groupCheckbox : false,
                        fields : [ 'auditCategory' ],
                        formatter : function(row) {
                            return row.stepCode + '、' + row.auditCategory;
                        }
                    }
                } ]
            } ]
        }
    },
    
    getBaseInfoFormFields : function(formStyle) {
        // 根据表单样式显示不同的字段
        if (formStyle === 'form1') {
            return emcProject.getBaseInfoFormFields1();
        } else if (formStyle === 'form2') {
            return emcProject.getBaseInfoFormFields2();
        } else {
            return emcProject.getBaseInfoFormFields3();
        }
    },

    create : function() {
        var _this = this;
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/project/projects/queries', Gikam.getJsonWrapper({
            f : {
                projNo_SEQ : this.param.projNo
            },
            n : -1,
            s : 50
        })).done(function(res) {
            if (!Gikam.isEmpty(res.total) && !Gikam.isEmpty(res.rows)) {
                Gikam.create('layout', {
                    id : 'emc-project-search-deatil-layout',
                    renderTo : Gikam.getLastModal().window.$dom,
                    center : {
                        items : [ _this.getBaseInfoTab(res.rows[0]) ]
                    }
                });
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
