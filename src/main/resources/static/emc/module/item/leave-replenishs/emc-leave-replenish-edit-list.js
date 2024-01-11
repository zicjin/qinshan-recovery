emcLeaveReplenish.editPage = {

        getGridParam : function() {
            var _this = this;
            return {
                type : 'grid',
                id : 'emc-leave-replenish-edit-list-grid',
                url : emcLeaveReplenish.baseUrl + '/queries/raw',
                service : 'emcLeaveReplenishServiceImpl',
                columns : emcLeaveReplenish.getEditListColumns(),
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
                            modalTitle : 'EMC.MODULE.ITEM.LEAVEREPLENISH.MODAL.ADD',
                            fields : emcLeaveReplenish.getBaseInfoFormFields(),
                            url : emcLeaveReplenish.baseUrl,
                            width : 400,
                            height : 350,
                            titleWidth : 100
                        }).done(function(id) {
                            Gikam.getComp('emc-leave-replenish-edit-list-grid').refresh();
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-leave-replenish-edit-list-grid').deleteRows(emcLeaveReplenish.baseUrl);
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
                        Gikam.viewAudit('T_EMC_INFO_TMPL', Gikam.getComp('emc-leave-replenish-edit-list-grid').getSelections());
                    }
                } ]
            }
        },
        
        submit : function() {
            var grid = Gikam.getComp('emc-leave-replenish-edit-list-grid');
            var selections = grid.getSelections();
            if (!grid.validate(selections))
                return;

            for(var item of selections) {
                if(item.startDate>item.endDate) {
                    Gikam.alert(Gikam.propI18N('EMC.MODULE.ITEM.LEAVEREPLENISH.TIP.ID_IS') + item.id + '  ' + Gikam.propI18N('EMC.MODULE.ITEM.LEAVEREPLENISH.TIP.START_DATE_IS_AFTER_END_DATE'));
                    return;
                }
            }

            Gikam.create('workflow').submit({
                data : grid.getSelections(),
                pageObject : emcLeaveReplenish
            }).done(function() {
                grid.refresh();
            });
        },

        create : function() {
            var _this = this;
            Gikam.create('layout', {
                id : 'emc-leave-replenish-edit-list-layout',
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
