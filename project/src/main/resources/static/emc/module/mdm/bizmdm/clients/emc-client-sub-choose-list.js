emcClient.subChoosePage = {
    getGridParam : function() {
        var _this = this, columns = emcClient.getClientSubChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-client-sub-choose-list-grid',
            url : emcClient.baseUrl + '/sub-clients/queries/choosable',
            // requestData : requestData,
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
                    Gikam.getLastModal().close(Gikam.getComp('emc-client-sub-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.CLIENT_ADD',
                        fields : emcClient.getPreInsertFormFields(),
                        url : emcClient.baseUrl,
                        height : 360
                    }).done(function () {
                        Gikam.getComp('emc-client-sub-choose-list-grid').refresh();
                    });
                }
            }, {
                type : 'form',
                id : 'emc-client-sub-choose-list-form',
                fields : emcClient.getClientSubFormFields(requestData),
                autoSave : false,
                onRendered : function(){
                    Gikam.getComp('emc-client-sub-choose-list-form').setData({
                        showFlag : '1',
                    });
                    // workspace.window.showMask(true);
                    // Gikam.getComp('emc-client-sub-choose-list-grid').refresh({
                    //     requestData : {
                    //         id_SEQ : '',
                    //     }
                    // }).done(function() {
                    //     workspace.window.closeMask();
                    // });
                }
            }, ]
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
                Gikam.getComp('emc-client-sub-choose-list-grid').refresh();
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
