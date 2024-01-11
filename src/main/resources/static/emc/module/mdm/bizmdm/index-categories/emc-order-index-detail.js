emcIndexCategory.fieldDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var data = Gikam.getComp('emc-order-index-detail-form').getData();

                    if(data.index1 || data.index2 || data.index3 || data.index4 || data.index5 || data.index6){
                        var url = Gikam.printf(emcIndexCategory.baseUrl + '/{id}/order-indexes', {
                            id : _this.param.id
                        });
                        if(!Gikam.isEmpty(_this.param.insertFlag)){
                            Gikam.getComp('emc-order-index-detail-field-grid').insert(url, [ data ]);
                        }else{
                            Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderIndexServiceImpl', [data] ])).always(function () {
                                Gikam.getComp('emc-order-index-detail-field-grid').refresh();
                            });
                        }
                        Gikam.getLastModal().close();
                    }else{
                        Gikam.alert('EMC.MODULE.MDM.BIZMDM.ORDERINDEXES.DETAIL.BUTTON.SAVE.INFO');
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.INDEX',
                items : [ {
                    type : 'form',
                    id : 'emc-order-index-detail-form',
                    fields : emcIndexCategory.getPropertyOrderIndexFields(),
                    columns : 1,
                    autoSave : false,
                    service : 'emcOrderIndexServiceImpl',
                    url : Gikam.printf(emcIndexCategory.baseUrl + '/order-indexes/{itemId}', {
                        itemId : _this.param.id
                    })
                } ]
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [this.getBtnToolbar(), this.getBaseInfoForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
