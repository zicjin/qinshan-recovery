var mobileMenu = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + '/secure/core/module/sys/mobile/menus',
    basePageUrl: IFM_CONTEXT + '/secure/core/module/sys/mobile/pages',
    change: false,

    // 列项->主列表
    getEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'id',
            title: 'T_CORE_MOBILE_MENU.ID',
            readonly: true
        }, {
            field: 'menuName',
            title: 'T_CORE_MOBILE_MENU.MENUNAME',
            readonly: true
        }, {
            field: 'activatedFlag',
            title: 'T_CORE_MOBILE_MENU.ACTIVATEDFLAG',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'activatedFlag'
            },
            readonly: true
        }]
    },

    // 菜单详情页
    getBaseInfoFormFields: function () {
        var _this = this;
        return [{
            field: 'id',
            title: 'T_CORE_MOBILE_MENU.ID',
            readonly: true
        }, {
            field: 'menuName',
            title: 'T_CORE_MOBILE_MENU.MENUNAME',
            validators: ['notEmpty', 'strLength[0,64]']
        }, {
            field: 'menuCategoryCode',
            title: 'T_CORE_MOBILE_MENU.MENUCATEGORYCODE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'mobileMenuCategory'
            },
            validators: ['notEmpty'],
            onChange: function(field, value, rowIndex, oldValue, text) {
                var form = Gikam.getComp('mobile-menu-edit-list-base-info-form');

                form.setData({
                    menuCategoryCode : value,
                    menuCategoryName : text
                });
                if(value !== oldValue){
                    _this.change = true
                }
            }
        }, {
            field: 'activatedFlag',
            title: 'T_CORE_MOBILE_MENU.ACTIVATEDFLAG',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'activatedFlag'
            },
            validators: ['notEmpty']
        }, {
            field: 'ext$.menuiconurl',
            title: 'T_CORE_MOBILE_MENU.ICON',
            type: 'image',
            height: 30,
            align: 'center',
            onClick: function (img) {
                mobileMenu.editPage.getShowImgPanel('menuIconUrl')
            }
        }, {
            field: 'badgeFlag',
            title: 'T_CORE_MOBILE_MENU.BADGEFLAG',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
            validators: ['notEmpty']
        }, {
            field: 'badgeConfig',
            title: 'T_CORE_MOBILE_MENU.BADGECONFIG',
            type: 'textarea',
            colspan: 3,
            height: 150
        }, {
            field: 'clickConfig',
            title: 'T_CORE_MOBILE_MENU.CILCKCONFIG',
            type: 'textarea',
            colspan: 3,
            height: 100
        }, {
            title: 'T_CORE_MOBILE_MENU.REMARK',
            field: 'remark',
            type: 'textarea',
            colspan: 3
        }];
    },

    getPreInsertFields: function(){
        return [{
            field: 'menuName',
            title: 'T_CORE_MOBILE_MENU.MENUNAME',
            validators: ['notEmpty', 'strLength[0,64]'],
            preInsert : true
        }, {
            field: 'menuCategoryCode',
            title: 'T_CORE_MOBILE_MENU.MENUCATEGORYCODE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'mobileMenuCategory'
            },
            validators: ['notEmpty'],
            onChange: function(field, value, rowIndex, oldValue, text) {
                // var form = Gikam.getComp('mobile-menu-edit-list-preinsert-form');
                //
                // form.setData({
                //     menuCategoryCode : value,
                //     menuCategoryName : text
                // });
                this.setData({
                    menuCategoryCode : value,
                    menuCategoryName : text
                });
            },
            preInsert : true
        }, {
            field: 'activatedFlag',
            title: 'T_CORE_MOBILE_MENU.ACTIVATEDFLAG',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'activatedFlag'
            },
            validators: ['notEmpty'],
            preInsert : true
        }]
    },

    //用户权限选择菜单
    getChooseListColumns : function() {
        return [ {
            title : 'T_CORE_MOBILE_MENU.ID',
            field : 'id',
            search: true
        }, {
            title : 'T_CORE_MOBILE_MENU.MENUNAME',
            field : 'menuName'
        }, {
            title : 'T_CORE_MOBILE_MENU.REMARK',
            field : 'remark'
        } ]
    }
});