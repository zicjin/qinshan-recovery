var corePasswordPolicy = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/password-policies',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_PASSWORD_POLICY.NAME',
            field : 'name',
            type : 'link',
            genericQuery : true,
            width : 200
        }, {
            title : 'T_CORE_PASSWORD_POLICY.TYPE',
            field : 'type',
            genericQuery : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'passwordPolicy'
            }
        }, Gikam.status.getSuspendColumn() ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'name',
            title : 'T_CORE_PASSWORD_POLICY.NAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[1,32]' ]
        }, {
            field : 'type',
            title : 'T_CORE_PASSWORD_POLICY.TYPE',
            type : 'select',
            readonly : true,
            category : 'sys',
            param : {
                codeCategoryId : 'passwordPolicy'
            }
        }, {}, {
            field : 'minLength',
            title : 'T_CORE_PASSWORD_POLICY.MINLENGTH',
            validators : [ 'number' ]
        }, {
            field : 'maxLength',
            title : 'T_CORE_PASSWORD_POLICY.MAXLENGTH',
            validators : [ 'number' ]
        }, {
            field : 'minNumber',
            title : 'T_CORE_PASSWORD_POLICY.MINNUMBER',
            validators : [ 'number' ]
        }, {
            field : 'minLowercase',
            title : 'T_CORE_PASSWORD_POLICY.MINLOWERCASE',
            validators : [ 'number' ]
        }, {
            field : 'minUppercase',
            title : 'T_CORE_PASSWORD_POLICY.MINUPPERCASE',
            validators : [ 'number' ]
        }, {
            field : 'minSpecialCharacters',
            title : 'T_CORE_PASSWORD_POLICY.MINSPECIALCHARACTERES',
            validators : [ 'number' ]
        }, {
            field : 'composition',
            title : 'T_CORE_PASSWORD_POLICY.COMPOSITION',
            validators : [ 'number', 'numRange[0,4]' ]
        }, {
            field : 'failedAttemptPolicy',
            title : 'T_CORE_PASSWORD_POLICY.FAILEDATTEMPTPOLICY',
            category : 'failed-attempt-policy',
            type : 'select',
            firstBlank : false,
            validators : [ 'notEmpty' ]
        }, {
            field : 'maxFailedAttempts',
            title : 'T_CORE_PASSWORD_POLICY.MAXFAILEDATTEMPTS',
            validators : [ 'number' ]
        }, {
            field : 'expiryInterval',
            title : 'T_CORE_PASSWORD_POLICY.EXPIRYINTERVAL',
            validators : [ 'number' ]
        }, {
            field : 'expiryWarningInterval',
            title : 'T_CORE_PASSWORD_POLICY.EXPIRYWARNINGINTERVAL',
            validators : [ 'number' ]
        }, {
            
        }, {
            field : 'expiredPolicy',
            title : 'T_CORE_PASSWORD_POLICY.EXPIREDPOLICY',
            category : 'expired-policy',
            type : 'select',
            firstBlank : false,
            validators : [ 'notEmpty' ]
        }, {
            field : 'expiredMaxUse',
            title : 'T_CORE_PASSWORD_POLICY.EXPIREDMAXUSE',
            validators : [ 'number' ]
        }, {
            
        }, {
            field : 'reuseInterval',
            title : 'T_CORE_PASSWORD_POLICY.REUSEINTERVAL',
            validators : [ 'number' ]
        }, {
            field : 'historyLength',
            title : 'T_CORE_PASSWORD_POLICY.HISTORYLENGTH',
            validators : [ 'number' ]
        } ]
    }
});