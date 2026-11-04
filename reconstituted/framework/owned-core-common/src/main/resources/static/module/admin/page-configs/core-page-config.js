var corePageConfig = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/admin/page-configs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.menuname',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.MENUNAME',
            readonly : true,
        }, {
            field : 'requestName',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.REQUESTNAME',
            readonly : true,
        } ];
    },
    
    getBaseInfoPreInsertFormFields : function() {
        return [ {
            field : 'menuId',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.MENUID',
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'menu',
            single : true,
            onBeforeChoose : function() {
                return {
                    filter_reachable : '1'
                }
            },
            onAfterChoose : function(rows) {
                if (rows.length > 0) {
                    var row = rows[0];

                    this.setData({
                        menuId : row.id,
                        ext$ : {
                            menuname : row.menuName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    menuId : '',
                    ext$ : {
                        menuname : ''
                    }
                });
            },
            preInsert : true
        }, {
            field : 'ext$.menuname',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.MENUNAME',
            readonly : true,
            preInsert : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.ID'
        }, {
            field : 'menuId',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.MENUID',
            readonly : true,
        }, {
            field : 'ext$.menuname',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.MENUNAME',
            readonly : true
        }, {
            field : 'requestName',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.REQUESTNAME',
            type : 'choose',
            category : 'gikam-reqentry',
            single : true,
            onBeforeChoose : function() {
                return {
                    url : encodeURIComponent(this.getData().ext$['menuurl'].replace(/\/page\/.+/g, "")),
                    filter_page_config : '1'
                }
            },
            onAfterChoose : function(rows) {
                if (rows && rows.length > 0) {
                    var row = rows[0];
                    this.setData({
                        requestName : row.requestName,
                        requestUrl : row.requestUrl
                    });
                }
            },
            onClean : function() {
                this.setData({
                    requestName : '',
                    requestUrl : ''
                });
            },
        }, {
            field : 'requestUrl',
            title : 'T_CORE_ADMIN_PAGE_CONFIG.REQUESTURL',
            readonly : true,
            colspan : 2
        } ];
    }
});
