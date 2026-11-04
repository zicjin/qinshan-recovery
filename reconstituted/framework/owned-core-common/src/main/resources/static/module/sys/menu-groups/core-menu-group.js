var coreMenuGroup = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/menu-groups',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_MENU_GROUP.ID',
            field : 'id',
            type : 'link',
            genericQuery : true
        }, {
            title : 'T_CORE_MENU_GROUP.GROUPNAME',
            field : 'groupName',
            genericQuery : true,
            width : 300
        }, Gikam.status.getSuspendColumn() ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_MENU_GROUP.ID',
            readonly : true
        }, {
            field : 'groupName',
            title : 'T_CORE_MENU_GROUP.GROUPNAME',
            validators : [ 'notEmpty', 'strLength[1,32]' ],
            preInsert : true
        }, {
            field : 'remark',
            title : 'T_CORE_MENU_GROUP.REMARK',
            type : 'textarea',
            validators : [ 'strLength[0,256]' ],
            colspan : 2
        } ]
    },

    getGroupLineListColumns : function() {
        return [ {
            title : 'T_CORE_MENU.MENUID',
            field : 'menuId',
            type : 'tree',
            width : 200
        }, {
            title : 'T_CORE_MENU.MENUNAME',
            field : 'menuName',
            editor : true,
            width : 300
        }, {
            title : 'T_CORE_MENU_GROUP.MENUINITNAME',
            field : 'ext$.menuinitname'
        }, {
            title : 'T_CORE_MENU_GROUP.HREF',
            field : 'ext$.href',
            width : 300
        }, {
            title : 'T_CORE_MENU_GROUP.PARAM',
            field : 'param',
            editor : true,
            width : 300
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_MENU_GROUP.ID',
            field : 'id',
            genericQuery : true
        }, {
            title : 'T_CORE_MENU_GROUP.GROUPNAME',
            field : 'groupName',
            genericQuery : true,
            width : 300
        } ]
    },

    workflow : {
        dbTable : 'T_CORE_MENU_GROUP',
        columns : [ {
            field : 'groupName',
            title : 'T_CORE_MENU_GROUP.GROUPNAME'
        } ]
    }
});