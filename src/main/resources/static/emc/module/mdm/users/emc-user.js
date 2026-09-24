Gikam.extend(coreUser, {
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'id',
            title : 'T_CORE_USER.ID',
            type : 'link'
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO'
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME'
        }, {
            field : 'orgName',
            title : 'T_CORE_USER.DEPARTMENTNAME',
        }, {
            field : 'ext$.parentorgname',
            title : 'T_CORE_USER.ORGNAME'
        }, {
            field : 'status',
            title : 'T_CORE_USER.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'userStatus'
            }
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'id',
            title : 'T_CORE_USER.ID',
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO',
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
        }, {
            field : 'orgName',
            title : 'T_CORE_USER.DEPARTMENTNAME',
        }, {
            field : 'ext$.parentorgname',
            title : 'T_CORE_USER.ORGNAME'
        } ]
    },

    getBaseInfoFormFields : function() {
        var _this = this;
        return [ {
            field : 'id',
            title : 'T_CORE_USER.ID',
            validators : [ 'remote[/secure/core/module/mdm/users/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ],
            preInsert : true
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            preInsert : true
        }, {
            field : 'userNo',
            title : 'T_CORE_USER.USERNO',
            validators : [ 'strLength[0,32]' ]
        }, {
            field : 'idCard',
            title : 'T_CORE_USER.IDCARD',
            validators : [ 'idCard' ]
        }, {
            field : 'ext$.parentorgname',
            title : 'T_CORE_USER.ORGNAME',
            preInsert : true,
            type : 'choose',
            category : 'org-simple',
            single : true,
            validators : [ 'notEmpty' ],
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'company'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        /*orgId : rows[0].id,
                        orgName : rows[0].orgName,*/
                        ext$ : {
                            parentorgname : rows[0].orgName,
                            parentorgid : rows[0].id
                        }
                    });
                }
            },
        }, {
            field : 'orgName',
            title : 'T_CORE_USER.DEPARTMENTNAME',
            type : 'choose',
            category : 'org-simple',
            single : true,
            validators : [ 'notEmpty' ],
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        orgId : rows[0].id,
                        orgName : rows[0].orgName,
                        /*ext$ : {
                            parentorgname : rows[0].ext$.parentorgname,
                            parentorgid : rows[0].parentId
                        }*/
                    });
                }
            },
            preInsert : true
        }, {
            field : 'mobile',
            title : 'T_CORE_USER.MOBILE',
            validators : [ 'phone' ]
        }, {
            field : 'email',
            title : 'T_CORE_USER.EMAIL',
            validators : [ 'email' ]
        }, {
            field : 'ext$.sealid',
            title : 'T_CORE_USER.SEALID',
            validators : [ 'strLength[0,64]' ]
        }, {
            field : 'expiryDate',
            title : 'T_CORE_USER.EXPIRYDATE',
            type : 'date'
        }, {
            field : 'status',
            title : 'T_CORE_USER.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'userStatus'
            }
        }, {
            field : 'multiLogin',
            title : 'T_CORE_USER.MULTILOGIN',
            type : 'simpleCheckbox'
        }, {}, {
            field : 'electronicSignature',
            type : 'image',
            title : 'T_CORE_USER.ELECTRONICSIGNATURE',
            onClick : function() {
                _this.detailPage.uploadElectronicSignature();
            }
        } ]
    }
});
