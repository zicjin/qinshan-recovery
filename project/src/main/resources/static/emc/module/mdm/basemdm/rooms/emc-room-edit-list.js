emcRoom.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-room-edit-list-grid',
            url : emcRoom.baseUrl + '/queries/raw',
            columns : emcRoom.getEditListColumns(),
            service : 'emcRoomServiceImpl',
	        requestData : {
	            org_filter : '1',
                createdByOrgId_SEQ : workspace.user.orgId,
	        },
            checkOnActive : false,
            deleteFormatter : function(row) {
                return row.roomNo || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.ROOM.MODAL.ROOM_ADD',
                        fields : emcRoom.getBaseInfoFormFields(),
                        url : emcRoom.baseUrl,
                        onFormRendered : function(form) {
                            setTimeout(() => {
                                var orgId = workspace.user.ext$.parentorgid;
                                this.setData({orgId : orgId},false);
                            }, 10)
                        },
                        titleWidth : 120
                    }).done(function() {
                        Gikam.getComp('emc-room-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-room-edit-list-grid').deleteRows(emcRoom.baseUrl);
                }
            } ]
        }
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
