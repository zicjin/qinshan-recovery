emcLeaveApply.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-leave-apply-edit-list-grid',
            url : emcLeaveApply.baseUrl + '/queries/raw',
            service : 'emcLeaveApplyServiceImpl',
            columns : emcLeaveApply.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            deleteFormatter : function(row) {
                return row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.LEAVEAPPLIES.MODAL.ADD',
                        fields : emcLeaveApply.getBaseInfoFormFields(),
                        url : emcLeaveApply.baseUrl,
                        width : 400,
                        height : 380,
                        titleWidth : 100
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-leave-apply-edit-list-grid').deleteRows(emcLeaveApply.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_LEAVE_APPLY', Gikam.getComp('emc-leave-apply-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-leave-apply-edit-list-grid');
        var selections = grid.getSelections();
        if (!grid.validate(selections))
            return;

        for(var item of selections) {
            if(item.startDate>item.endDate) {
                Gikam.alert(Gikam.propI18N('EMC.MODULE.ITEM.LEAVEAPPLIES.TIP.ID_IS') + item.id + '  ' + Gikam.propI18N('EMC.MODULE.ITEM.LEAVEAPPLIES.TIP.START_DATE_IS_AFTER_END_DATE'));
                return;
            }
        }

        var ids = '';
        for(var item of selections){
            if(item.needCredentials === '1' && !item.credentials){
                ids += '[' + item.id + ']';
            }
        }
        if(!Gikam.isEmpty(ids)){
            Gikam.alert(Gikam.propI18N('T_EMC_LEAVE_APPLY.ID') + Gikam.propI18N('EMC.MODULE.ITEM.LEAVEAPPLIES.TIP.CREDENTIALS_IS') + ids + Gikam.propI18N('EMC.MODULE.ITEM.LEAVEAPPLIES.TIP.CREDENTIALS_NOTEMPTY'));
            return;
        }
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcLeaveApply
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.LEAVEAPPLIES.MODAL.DETAIL',
            url : emcLeaveApply.baseUrl + '/page/detail' + Gikam.param({
                id : id,
                operType : 'edit'
            }),
            width : 900,
            height : 300,
            onAfterClose : function() {
                Gikam.getComp('emc-leave-apply-edit-list-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-leave-apply-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
