var coreNotificationAudio = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'audioName',
            width : 300,
            editor : true,
            validators : [ {
                type : 'unique',
                rule : '/secure/core/module/item/notification/audios/action/validate-unique'
            }, 'notEmpty', 'strLength[0,40]' ],
            title : 'T_CORE_NOTIFICATION_AUDIO.AUDIONAME',
            onBeforeEditorRender : function (row) {
                if (row.audioType === 'system') {
                    return false;
                }
            },
        }, {
            field : 'audioType',
            title : 'T_CORE_NOTIFICATION_AUDIO.AUDIOTYPE',
            type : 'select',
            category : 'audio-type',
            contentAlign : 'center',
            width : 100,
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.FILENAME',
            field : 'ext$.filename',
            formatter: function(index, value, row) {
                if (row.audioType === 'system') {
                    return row.url.substr(row.url.lastIndexOf('/') + 1);
                } else {
                    return value
                }
            }
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'audioName',
            title : 'T_CORE_NOTIFICATION_AUDIO.AUDIONAME',
            validators : [ {
                type : 'unique',
                rule : '/secure/core/module/item/notification/audios/action/validate-unique'
            }, 'notEmpty', 'strLength[0,40]' ],
            preInsert : true
        }, {
            field : 'audioType',
            title : 'T_CORE_NOTIFICATION_AUDIO.AUDIOTYPE',
            type : 'select',
            readonly : true,
            category : 'audio-type',
            selectedIndex : 1,
            firstBlank : false,
            preInsert : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/notification/audios'
});
