var mobilePage = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/mobile/pages',

    // 列项->主列表
    getEditListColumns : function() {
        return [{
            title : 'T_CORE_MOBILE_PAGE.PAGENO',
            field : 'pageNo',
            type : 'tree'
        },{
            title : 'T_CORE_MOBILE_PAGE.PAGENAME',
            field : 'pageName'
        }]
    },

    // 菜单详情页
    getBaseInfoFormFields : function() {
        return [  {
            field : 'id',
            title : 'T_CORE_MOBILE_PAGE.ID',
            readonly : true
        }, {
            field : 'pageName',
            title : 'T_CORE_MOBILE_PAGE.PAGENAME',
            validators : [ 'notEmpty', 'strLength[0,64]' ]
        }, {
            title : 'T_CORE_MOBILE_PAGE.LISTSHOWCONFIG',
            field : 'listShowConfig',
            type: 'textarea',
            colspan: 3,
            height: 150
        }, {
            title : 'T_CORE_MOBILE_PAGE.LISTCLICKCONFIG',
            field : 'listClickConfig',
            type: 'textarea',
            colspan: 3,
            height: 100
        }, {
            title : 'T_CORE_MOBILE_PAGE.LISTSEARCHCONFIG',
            field : 'listSearchConfig',
            type: 'textarea',
            colspan: 3,
            height: 100
        }, {
            title : 'T_CORE_MOBILE_PAGE.CHOOSEBTNCONFIG',
            field : 'chooseBtnConfig',
            type: 'textarea',
            colspan: 3,
            height: 150
        },{
            title : 'T_CORE_MOBILE_PAGE.MENUBTNCONFIG',
            field : 'menuBtnConfig',
            type: 'textarea',
            colspan: 3,
            height: 100
        },{
            title : 'T_CORE_MOBILE_PAGE.REMARK',
            field : 'remark',
            type: 'textarea',
            colspan: 3,
            height: 100
        }];
    }

});