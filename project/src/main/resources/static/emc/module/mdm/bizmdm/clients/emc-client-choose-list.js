emcClient.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcClient.getClientChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-client-choose-list-grid',
            url : emcClient.baseUrl + '/queries/choosable',
            requestData : requestData,
            columns : columns,
            filterOpen : true,
            page : true,
            onCellClick : function(field, row) {
                field === 'clientNo' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-client-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            },{
                            type: 'button',
                            text: '按客户同步',
                            icon: 'add',
                            onClick: function () {
                            var modal = Gikam.create('modal', {
                                   title : "客户名称",
                                   width : 400,
                                   height : 200
                               });
                               var layout = Gikam.create('layout', {
                                   renderTo : modal.window.$dom,
                                   center : {
                                       items : [{
                                           type : 'btnToolbar',
                                           items : [{
                                               type : 'button',
                                               text : 'GIKAM.BUTTON.CONFIRM',
                                               icon : 'select',
                                               onClick : function () {
                                                      debugger;
                                                     var wrapper = Gikam.getJsonWrapper({"contractName":form.getData().contractName }, [ '', [] ]);
                                                       workspace.window.showMask(true);
                                                         Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/oa-orgs/sync-byname', wrapper).done(function() {

                                                         }).always(function() {
                                                             workspace.window.closeMask();
                                                              modal.close();
                                                         });
                                               }
                                           }, {
                                               type : 'button',
                                               text : 'GIKAM.BUTTON.CANCEL',
                                               icon : 'cancel',
                                               onClick : function () {
                                                   modal.close();
                                               }
                                           }]
                                       }]
                                   }
                               });
                                 var form = Gikam.create('form', {
                                      renderTo: layout.options.center.$dom,
                                      columns: 1,
                                      titleWidth: 80,
                                      fields: [{
                                          field: 'contractName',
                                          title: '客户名称',
                                      }],
                                  });
                        }
                    },

//            {
//                type : 'button',
//                text : 'GIKAM.BUTTON.INSERT',
//                icon : 'add',
//                onClick : function() {
//                    Gikam.preInsert({
//                        modalTitle : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.CLIENT_ADD',
//                        fields : emcClient.getPreInsertFormFields(),
//                        url : emcClient.baseUrl,
//                        height : 360
//                    }).done(function () {
//                        Gikam.getComp('emc-client-choose-list-grid').refresh();
//                    });
//                }
//            },
             ]
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('emc-client-choose-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            } ]
        }
    },
    
    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.BASE_INFO',
            height : 400,
            url : emcClient.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-client-choose-list-grid').refresh();
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