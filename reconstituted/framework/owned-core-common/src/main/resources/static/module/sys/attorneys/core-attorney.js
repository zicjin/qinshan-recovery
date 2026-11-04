var coreAttorney = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_ATTORNEY.TRUSTORNAME',
            field : 'ext$.trustorname',
            width : 100
        }, {
            title : 'T_CORE_ATTORNEY.TRUSTEENAME',
            field : 'ext$.trusteename',
            width : 100,
        }, {
            title : 'T_CORE_ATTORNEY.STARTDATE',
            field : 'startDate',
            width : 115,
        }, {
            title : 'T_CORE_ATTORNEY.ENDDATE',
            field : 'endDate',
            width : 115,
        } ]
    },
    
    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_CORE_ATTORNEY.TRUSTORNAME',
            field : 'ext$.trustorname',
            preInsert : false,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [ { trustor : 'id' }, {  'ext$.trustorname' : 'userName' } ],
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY.TRUSTEENAME',
            field : 'ext$.trusteename',
            preInsert : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [ { trustee : 'id' }, { 'ext$.trusteename' : 'userName' } ],
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY.STARTDATE',
            field : 'startDate',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            title : 'T_CORE_ATTORNEY.ENDDATE',
            field : 'endDate',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            title : 'T_CORE_ATTORNEY.LASTSUSPENDEDFLAG',
            field : 'lastSuspendedFlag',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        } ]
    },
    
    getMenuListColumns : function() {
        return [{
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_ATTORNEY_MENU.MENUID',
            field : 'menuId',
            genericQuery : true,
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY_MENU.MENUNAME',
            field : 'ext$.menuname',
            genericQuery : true,
            readonly : true
        } ]
    },
    
    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_ATTORNEY.TRUSTORNAME',
            field : 'ext$.trustorname',
            width : 100
        }, {
            title : 'T_CORE_ATTORNEY.TRUSTEENAME',
            field : 'ext$.trusteename',
            width : 100,
        }, {
            title : 'T_CORE_ATTORNEY.STARTDATE',
            field : 'startDate',
            width : 115,
        }, {
            title : 'T_CORE_ATTORNEY.ENDDATE',
            field : 'endDate',
            width : 115,
        }, {
            title : 'T_CORE_ATTORNEY.LASTSUSPENDEDFLAG',
            field : 'lastSuspendedFlag',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 100,
        } ]
    },
    
    getSearchBaseInfoFormFields : function() {
        return [ {
            title : 'T_CORE_ATTORNEY.TRUSTORNAME',
            field : 'ext$.trustorname',
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY.TRUSTEENAME',
            field : 'ext$.trusteename',
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY.STARTDATE',
            field : 'startDate',
            type : 'date',
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY.ENDDATE',
            field : 'endDate',
            type : 'date',
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY.LASTSUSPENDEDFLAG',
            field : 'lastSuspendedFlag',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        } ]
    },
    
    getSearchMenuListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_ATTORNEY_MENU.MENUID',
            field : 'menuId',
            genericQuery : true,
            readonly : true
        }, {
            title : 'T_CORE_ATTORNEY_MENU.MENUNAME',
            field : 'ext$.menuname',
            genericQuery : true,
            readonly : true
        } ]
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/attorneys'
});