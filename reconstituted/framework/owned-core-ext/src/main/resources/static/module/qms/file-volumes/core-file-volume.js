var coreFileVolume = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_VOLUME.SORTOR'
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_VOLUME.ACTIVATEDFLAG',
            type : 'select',
            category : 'file-activated-flag'
        }, {
            field : 'fileVolumeNo',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENO'
        }, {
            field : 'fileVolumeName',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENAME'
        }, {
            field : 'serialNumber',
            title : 'T_CORE_FILE_VOLUME.SERIALNUMBER',
            editor : true
        }, {
            field : 'activatedTime',
            title : 'T_CORE_FILE_VOLUME.ACTIVATEDTIME',
            type : 'date',
            editor : true
        }, {
            field : 'deactivatedTime',
            title : 'T_CORE_FILE_VOLUME.DEACTIVATEDTIME',
            type : 'date',
            editor : true
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_VOLUME.REMARK',
            editor : true
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_TYPE.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_TYPE.CREATEDTIME',
            width : 80
        } ];
    },
    
    getInsertFields : function() {
        return [ {
            field : 'fileVolumeNo',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENO',
            validators : [ 'notEmpty', 'strLength[0,20]' ]
        }, {
            field : 'fileVolumeName',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        } ];
    },
    
    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortor',
            title : 'T_CORE_FILE_VOLUME.SORTOR'
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_VOLUME.ACTIVATEDFLAG',
            type : 'select',
            category : 'activatedFlag'
        }, {
            field : 'fileVolumeNo',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENO'
        }, {
            field : 'fileVolumeName',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENAME'
        }, {
            field : 'serialNumber',
            title : 'T_CORE_FILE_VOLUME.SERIALNUMBER'
        }, {
            field : 'activatedTime',
            title : 'T_CORE_FILE_VOLUME.ACTIVATEDTIME',
            type : 'date'
        }, {
            field : 'deactivatedTime',
            title : 'T_CORE_FILE_VOLUME.DEACTIVATEDTIME',
            type : 'date'
        }, {
            field : 'remark',
            title : 'T_CORE_FILE_VOLUME.REMARK'
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE_TYPE.CREATEDBYNAME',
            width : 80
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE_TYPE.CREATEDTIME',
            width : 80
        } ];
    },

    getChooseListColumns : function(isSingle) {
        var columns = [ {
            field : 'id',
            title : 'T_CORE_FILE_VOLUME.ID'
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_FILE_VOLUME.ACTIVATEDFLAG',
            type : 'select',
            category : 'file-activated-flag'
        }, {
            field : 'fileVolumeNo',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENO'
        }, {
            field : 'fileVolumeName',
            title : 'T_CORE_FILE_VOLUME.FILEVOLUMENAME'
        }, {
            field : 'serialNumber',
            title : 'T_CORE_FILE_VOLUME.SERIALNUMBER'
        } ];
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/file-volumes'
});
