var coreTodoConfig = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/todo-configs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'name',
            title : 'T_CORE_TODO_CONFIG.NAME',
            width : 280,
            readonly : true,
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_TODO_CONFIG.ACTIVATEDFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            },
            readonly : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_TODO_CONFIG.ID'
        }, {
            field : 'name',
            title : 'T_CORE_TODO_CONFIG.NAME',
            validators : [ 'notEmpty', 'strLength[0,64]' ],
            preInsert : true
        }, {
            field : 'category',
            title : 'T_CORE_TODO_CONFIG.CATEGORY',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            selectedIndex : 0,
            param : {
                codeCategoryId : 'todo',
            },
            validators : [ 'notEmpty' ],
            preInsert : true,
        }, {
            field : 'menuId',
            title : 'T_CORE_TODO_CONFIG.MENUID',
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
                        },
                        pageUrl : ''
                    });
                }
            },
            onClean : function() {
                this.setData({
                    menuId : '',
                    ext$:{menuname : ''}
                });
            }
        }, {
            field : 'ext$.menuname',
            title : 'T_CORE_TODO_CONFIG.MENUNAME',
            readonly : true
        }, {
            field : 'inheritParam',
            title : 'T_CORE_TODO_CONFIG.INHERITPARAM',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            selectedIndex : 1,
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'orderNo',
            title : 'T_CORE_TODO_CONFIG.ORDERNO',
            type : 'number',
        }, {
            field : 'voiceWarning',
            title : 'T_CORE_TODO_CONFIG.VOICEWARNING',
            type : 'select',
            category : 'yesorno',
            selectedIndex : 1
        }, {
        }, {
            field : 'descExpression',
            title : 'T_CORE_TODO_CONFIG.DESCEXPRESSION',
            validators : [ 'strLength[0,256]' ],
            colspan : 3
        }, {
            field : 'jsonParam',
            title : 'T_CORE_TODO_CONFIG.JSONPARAM',
            validators : [ 'strLength[0,256]' ],
            colspan : 3
        }, {
            field : 'serviceMethod',
            title : 'T_CORE_TODO_CONFIG.SERVICEMETHOD',
            validators : [ 'strLength[0,512]' ],
            colspan : 3
        }, {
            field : 'lcdpServiceMethod',
            type : 'choose',
            category : 'lcdp-method',
            single : true,
            customData : {
                MAPPINGTYPE_SEQ : 'SELECT',
                METHODNAME_OCISC : 'PAGE,PAGINATION'
            },
            targetFields : [ {
                lcdpServiceMethod : 'methodPath'
            } ],
            title : 'T_CORE_TODO_CONFIG.LCDPSERVICEMETHOD',
            validators : [ 'strLength[0,512]' ],
            colspan : 3
        },  {
            field : 'selectSql',
            title : 'T_CORE_TODO_CONFIG.SELECTSQL',
            type : 'codeEditor',
            height : 300,
            language: 'sql',
            colspan : 3
        }, {
            field : 'i18nCode',
            title : 'T_CORE_TODO_CONFIG.I18NCODE',
            validators : [ 'strLength[0,128]' ],
            colspan : 3
        }, {
            field : 'ext$.todoiconurl',
            title : 'T_CORE_TODO_CONFIG.ICON',
            type : 'image',
            height : 30,
            align : 'center',
            onClick : function(img) {
                coreTodoConfig.editPage.getShowImgPanel('todoIconUrl')
            }
        }, {}, {}, {
             field : 'mobileSelectSql',
             title : 'T_CORE_TODO_CONFIG.MOBILESELECTSQL',
             type : 'codeEditor',
             height : 300,
             language: 'sql',
             colspan : 3
         } ];
    },

    workflow : {
        dbTable : 'T_EMC_PROJECT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_PROJECT.ID'
        } ]
    }
});
