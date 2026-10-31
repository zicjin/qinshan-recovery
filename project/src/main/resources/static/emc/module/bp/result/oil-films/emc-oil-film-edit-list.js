emcOilFilm.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-oil-film-edit-list-grid',
            service : 'emcOilFilmServiceImpl',
            columns : emcOilFilm.getEditListColumns(),
            url : emcOilFilm.baseUrl + '/queries/raw',
            page : true,
            filterOpen : true,
            onRendered : function() {
                this.hideColumns([ 'projId']);
            },
	        requestData : {
	            org_filter : '1',
	        },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-oil-film-edit-list-grid').insert(IFM_CONTEXT + '/secure/emc/module/bp/result/oil-films/insert');
                }
            },{
                type : 'button',
                text : '批量新增',
                icon : 'add',
                onClick : function() {
                	_this.batchAdd();
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-oil-film-edit-list-grid').deleteRows(emcOilFilm.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('oil', Gikam.getComp('emc-oil-film-edit-list-grid').getSelections());
                }
            },{
                type : 'button',
                text : '批录入',
                icon : 'edit',
                onClick : function() {
                    _this.batchUpdate();
                }
            }  ]
        }
    },
    
    // 批录入样品修改
    batchUpdate : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-oil-film-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = selections[0];
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.weighingUserId = formData.weighingUserId;
                                    item.weighingUserName = formData.weighingUserName;
                                    item.projId = formData.projId;
                                    item.projNo = formData.projNo;
                                    item.weighingDate = formData.weighingDate;

                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/result/oil-films/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcOilFilmServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-oil-film-edit-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    }  ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 100,
            fields: emcOilFilm.getBatchInputColumns(),
            autoSave: false
        });

    },
    
    batchAdd : function() {
        Gikam.create('modal', {
            title : '批量新增',
            width : 400,
            height : 200,
            url : IFM_CONTEXT + '/secure/emc/module/bp/result/runs/page/oil-film-batch-add',
            onAfterClose : function(data) {
                var addnum = data.addnum;
                if(addnum == null || addnum == ''){
                	return;
                }
                workspace.window.showMask(true);
                var url = IFM_CONTEXT + '/secure/emc/module/bp/result/oil-films/batch-add';
                Gikam.post(url, Gikam.getJsonWrapper({
                    f : {
                    	addnum: addnum
                    }
                }, [])).always(function()
                {                    
                	workspace.window.closeMask(true);
                	Gikam.getComp('emc-oil-film-edit-list-grid').refresh();
                });
                }
            
        });
    
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
