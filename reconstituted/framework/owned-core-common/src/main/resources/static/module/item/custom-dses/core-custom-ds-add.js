coreCustomDs.addPage = {

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    createPage : function() {
        Gikam.create('layout', {
            west : {
                width : '50%',
                items : [ this.getBaseInfoTab() ]
            },
            center : {
                items : [ this.getFieldGridParam() ]
            },
            south : {
                height : 30,
                items : [ this.getBtnGroup() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    getBtnGroup : function() {
        var _this = this;
        var modal = Gikam.getLastModal()
        return {
            type : 'btnGroup',
            margin : '0px 0px 0px 1300px',
            items : [  {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'save',
                onClick : function() {
                    var form = Gikam.getComp('core-custom-ds-add-base-info-form').getData();
                    form['activatedFlag'] = '1';
                    var grid = Gikam.getComp('core-custom-ds-field-list-grid').getData();
                    if(Gikam.getComp('core-custom-ds-add-base-info-form').validate()){
                        Gikam.post(coreCustomDs.baseUrl,Gikam.getJsonWrapper(null, [ 'coreCustomDsServiceImpl', [form]])).done(function (data) {
                            Gikam.postSync(Gikam.printf(coreCustomDs.baseUrl + '/{dsId}/action/insert-ds-fields',{
                                dsId : data
                            }),Gikam.getJsonWrapper(null, [ 'coreCustomDsFieldServiceImpl', grid]))
                            if (modal) {
                                modal.close()
                            }
                        })
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    if (modal) {
                        modal.close()
                    }
                }
            } ]
        }
    },


    baseForm : null,
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.CUSTOM_DSES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-custom-ds-add-base-info-form',
                    autoSave : false,
                    service : 'coreCustomDsServiceImpl',
                    fields : coreCustomDs.getAddFormFields(),
                    columns : 3,
                } ]
            } ]
        }
    },

    fieldGrid : null,
    getFieldGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.CUSTOM_DSES.TAB.DS_FIELD',
                items : [ {
                    type : 'grid',
                    id : 'core-custom-ds-field-list-grid',
                    url : _this.getCompUrl('fieldGrid'),
                    autoSave : false,
                    service : 'coreCustomDsFieldServiceImpl',
                    columns : coreCustomDs.getFieldEditListColumns(),
                    page : false,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-custom-ds-field-list-grid').moveUp(coreCustomDs.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-custom-ds-field-list-grid').moveDown(coreCustomDs.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    } ]
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreCustomDs.baseUrl + '/{id}',
        fieldGrid : coreCustomDs.baseUrl + '/{id}/fields/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-custom-ds-add-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-custom-ds-field-list-grid' : this.getCompUrl('fieldGrid'),
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    getUrl : function(url) {
        var data = this.baseForm.getData();
        return Gikam.printf(coreCustomDs.baseUrl + '/{id}' + url, {
            id : data.id
        });
    }

}
