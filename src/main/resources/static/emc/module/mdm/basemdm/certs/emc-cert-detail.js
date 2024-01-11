emcCert.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit(emcCert.workflow.dbTable, _this.param.id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcCertServiceImpl',
                    id : 'emc-cert-detail-base-info-form',
                    fields : emcCert.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getLineGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-cert-detail-line-list-grid',
                    url : _this.getCompUrl('lineGrid'),
                    service : 'emcCertLineServiceImpl',
                    checkOnActive : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.save(_this.insertLineRow);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.save(_this.deleteLineRow);
                        }
                    } ],
                    columns : emcCert.getLineListColumns()
                } ]
            } ]
        }
    },

    insertLineRow : function() {
        var formData = Gikam.getComp('emc-cert-detail-base-info-form').getData();
        var url = Gikam.printf(emcCert.baseUrl + '/{id}/v/{version}/lines', {
            id : formData.id,
            version : formData.version
        });
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('line').title,
            url : Gikam.choose.getConfig('line').url,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        lineId : item.id
                    }
                });
                Gikam.getComp('emc-cert-detail-line-list-grid').insert(url, chooseData);
            }
        });

    },

    deleteLineRow : function() {
        var formData = Gikam.getComp('emc-cert-detail-base-info-form').getData();
        var url = Gikam.printf(emcCert.baseUrl + '/{id}/v/{version}/lines', {
            id : formData.id,
            version : formData.version
        });
        Gikam.getComp('emc-cert-detail-line-list-grid').deleteRows(url);
    },

    urlManager : {
        baseInfoForm : emcCert.baseUrl + '/{id}',
        lineGrid : emcCert.baseUrl + '/{id}/lines/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-cert-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'emc-cert-detail-line-list-grid' : this.getCompUrl('lineGrid')
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

    save : function(callback, showLoading) {
        workspace.window.save({
            url : emcCert.baseUrl + (Gikam.isEmpty(this.param.id) ? '' : '/' + this.param.id + '/v/' + Gikam.getComp('emc-cert-detail-base-info-form').getData().version),
            page : this,
            showLoading : showLoading
        }).done(function() {
            callback && callback();
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getLineGridParam() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
