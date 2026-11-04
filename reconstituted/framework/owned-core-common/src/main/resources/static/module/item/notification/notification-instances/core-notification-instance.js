var coreNotificationInstance = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + '/secure/core/module/item/notification/notification-instances',

    getEditListColumns: function() {
        return [{
            checkbox: true
        }, {
            field: 'title',
            title: 'T_CORE_NOTIFICATION_INST.TITLE',
            readonly: true,
        }, {
            field: 'content',
            title: 'T_CORE_NOTIFICATION_INST.CONTENT',
            readonly: true,
            width: 400,
            formatter: function(index, value, row) {
                if (row.viewMethod === "menu") {
                    return '<a href="javascript:;" onclick="workspace.activeNodeById(\'' + row.menuId + '\')">' + value + '</a>';
                }
                if (row.viewMethod === "page") {
                    return '<a href="javascript:;" onclick="coreNotificationInstance.openPage(\'' + row.pageUrl + '\',\'' + row.title + '\')">' + value + '</a>';
                }
                return value;
            },
            sort : false
        }, {
            field: 'createdTime',
            title: 'T_CORE_NOTIFICATION_INST.CREATEDTIME',
            readonly: true,
        }];
    },

    getBaseInfoFormFields: function() {
        return [{
            field: 'title',
            title: 'T_CORE_NOTIFICATION_INST.TITLE',
            readonly: true
        }, {
            field: 'content',
            title: 'T_CORE_NOTIFICATION_INST.CONTENT',
            type: 'ckeditor',
            readonly: true
        }];
    },

    getInstanceFileFields: function() {
        return [{
            field: 'fileName',
            title: 'T_CORE_NOTIFICATION_INST_FILE.FILENAME',
            readonly: true,
            formatter(rowIndex, cellValue, row) {
                return '<a href="' + (IFM_CONTEXT + row.fileDownloadUrl) + '">' + cellValue + '</a>';
            }
        }];
    },

    openPage: function(pageUrl, title) {
        if (pageUrl) {
            var initWindow = window.workspace.window;
            var modal = Gikam.create("modal", {
                url: IFM_CONTEXT + pageUrl,
                title: title,
                onAfterClose() {
                    window.workspace.window = initWindow;
                }
            });
            window.workspace.window = modal.window;
        }
    }
});
