var coreHelpCenter = Gikam.getPageObject({

    getBaseInfoFormFields : function() {
        return [ {
            field : 'manualVideoFileId',
            title : 'MODULE.ADMIN.HELPCENTERS.FIELD.VIDEO',
            type : 'file',
            autoSave : false,
            accept : ['audio'],
            id : 'core-help-center-base-info-form-manual-video-file',
            onChange : function(field, name, value) {
                var form = Gikam.getComp('core-help-center-base-info-form');
                var formData = form.getData();
                if (Gikam.isNotEmpty(formData.id)) {
                    workspace.window.showMask(true);
                    form.uploadSingleFile("T_CORE_HELP_CENTER", formData.id, {
                        bizCategory : 'HELPCENTER-VEDIO',
                        scope : 'helpcenter',
                        field : 'manualVideoFileId'
                    }).done(function(data) {
                        var vedioField = Gikam.getComp('core-help-center-base-info-form-manual-video-file');
                        vedioField.setText(name);
                        vedioField.setFileId(data);
                        Gikam.put(coreHelpCenter.baseUrl + '/instant', Gikam.getJsonWrapper(null, [ 'coreHelpCenterServiceImpl', [ {
                            id : formData.id,
                            manualVideoFileId : data
                        } ] ]));
                    }).always(function() {
                        workspace.window.closeMask();
                    });;
                }
            },
            onDelete : function(fileId) {
                if (!fileId) {
                    return;
                }
                var form = Gikam.getComp('core-help-center-base-info-form');
                var formData = form.getData();
                if (Gikam.isNotEmpty(formData.id)) {
                    Gikam.del(Gikam.IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper({}, [ '', [ {
                        id : fileId
                    } ] ])).done(function() {
                        formData['manualVideoFileId'] = '';
                        form.setData(formData);
                    });
                }
            }
        }, {

        }, {
            title : 'T_CORE_HELP_CENTER.MANUALEDITINGMETHOD',
            field : 'manualEditingMethod',
            type : 'select',
            firstBlank : false,
            category : 'manual-editing-method',
            onChange : function(field, value, index) {
                if (Gikam.isNotEmpty(value) && value === 'online') {
                    Gikam.getComp('core-help-center-base-info-form').showFields([ 'manualContent' ]);
                    Gikam.getComp('core-help-center-base-info-form').hideFields([ 'manualDocFileId' ]);
                } else if (Gikam.isNotEmpty(value) && value === 'file') {
                    Gikam.getComp('core-help-center-base-info-form').showFields([ 'manualDocFileId' ]);
                    Gikam.getComp('core-help-center-base-info-form').hideFields([ 'manualContent' ]);
                } else {
                    Gikam.getComp('core-help-center-base-info-form').hideFields([ 'manualDocFileId', 'manualContent' ]);
                }
            }
        }, {

        }, {
            field : 'manualDocFileId',
            title : 'MODULE.ADMIN.HELPCENTERS.FIELD.MANUAL_DOC',
            type : 'file',
            id : 'core-help-center-base-info-form-manual-doc-file',
            autoSave : false,
            accept : ['pdf'],
            onChange : function(field, name, value) {
                var form = Gikam.getComp('core-help-center-base-info-form');
                var formData = form.getData();
                if (Gikam.isNotEmpty(formData.id)) {
                    workspace.window.showMask(true);
                    form.uploadSingleFile("T_CORE_HELP_CENTER", formData.id, {
                        bizCategory : 'HELPCENTER-DOC',
                        scope : 'helpcenter',
                        field : 'manualDocFileId'
                    }).done(function(data) {
                        Gikam.put(coreHelpCenter.baseUrl + '/instant', Gikam.getJsonWrapper(null, [ 'coreHelpCenterServiceImpl', [ {
                            id : formData.id,
                            manualDocFileId : data
                        } ] ])).done(function(){
                            Gikam.alert(Gikam.propI18N('MODULE.ADMIN.HELPCENTERS.FILE.UPLOAD_SUCCESS'));
                        }).fail(function(r) {
                            Gikam.alert(Gikam.propI18N(JSON.parse(r.responseText).message));
                        }).always(function(){
                            workspace.window.closeMask();
                        });
                    }).fail(function(r) {
                        Gikam.alert(Gikam.propI18N(JSON.parse(r.responseText).message));
                        workspace.window.closeMask();
                    });
                }
            },
            onDelete : function(fileId) {
                var form = Gikam.getComp('core-help-center-base-info-form');
                var formData = form.getData();
                if (Gikam.isNotEmpty(formData.id)) {
                    Gikam.del(Gikam.IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper({}, [ '', [ {
                        id : fileId
                    } ] ])).done(function() {
                        formData['manualDocFileId'] = '';
                        form.setData(formData);
                    });
                }
            }
        }, {

        }, {
            field : 'manualContent',
            title : 'MODULE.ADMIN.HELPCENTERS.FIELD.MANUAL',
            type : 'ckeditor',
            colspan : 2
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/module/admin/help-centers'
});
