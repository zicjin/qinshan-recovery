coreFileManage.detailPage = {

    getBtnToolbar : function() {
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getForm : function() {
        var _this = this;
        return {
            type : 'form',
            url : Gikam.printf(coreFileManage.baseUrl + '/versions/{id}', {
                id : _this.param.id
            }),
            service : 'coreFileManageVersionServiceImpl',
            id : 'core-file-manage-version-detail-base-info-form',
            fields : coreFileManage.getFileVersionInfoFields(),
            columns : 4,
            onLoadSuccess : function() {
                var versionFileId = Gikam.getComp('core-file-manage-version-detail-base-info-form').getData().refFileId;
                if (Gikam.isEmpty(versionFileId)) {
                    Gikam.alert('CORE.MODULE.QMS.FILE_MANAGE.TIP.NO_FILE_ENABLE_OPEN_ONLYOFFICE');
                    return;
                }
                Gikam.openFileByOffice({
                    fileId: versionFileId,
                    mode: 'edit',
                    iframe: Gikam.getComp('core-file-manage-version-detail-iframe').iframe,
                    viewType: 'iframe'
                });
            }
        }
    },
    
    getOnlyOfficeIframe : function() {
        return { 
            type : 'iframe',
            id : 'core-file-manage-version-detail-iframe',
            src : ''
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'core-dile-manage-version-layout',
            north : {
                height : '24%',
                items : [ this.getBtnToolbar(), this.getForm() ]
            },
            center : {
                items : [ this.getOnlyOfficeIframe() ]
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
