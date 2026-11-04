var coreBpmnPresetComment = Gikam.getPageObject({

    getEditListColumns : function () {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'commentCategory',
            title : 'T_CORE_BPMN_PRESET_COMMENT.COMMENTCATEGORY',
            type : 'select',
            category : 'bpmn-preset-comment-category',
            width : 150
        }, {
            field : 'presetComment',
            title : 'T_CORE_BPMN_PRESET_COMMENT.PRESETCOMMENT',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,40]' ],
            width : 800
        }, {
            field : 'defaultFlag',
            title : 'T_CORE_BPMN_PRESET_COMMENT.DEFAULTFLAG',
            type : 'simpleCheckbox',
            editor : true,
            onChange : function (field, value, rowIndex) {
                var grid = Gikam.getComp('core-bpmn-preset-comment-edit-list-grid')

                grid.setData({
                    index : rowIndex,
                    defaultFlag : value
                });

                //是否默认勾选时需将同类型的默认的预置意见取消勾选
                if (value === '1') {
                    var gridData = grid.getData();
                    var commentCategory = gridData[ rowIndex ].commentCategory;

                    //同类型默认预置意见
                    var filterData = gridData.filter(function (row, index) {
                        return commentCategory === row.commentCategory && row.defaultFlag === '1' && index !== rowIndex;
                    });

                    if (!Gikam.isEmpty(filterData)) {
                        grid.setData({
                            id : filterData[ 0 ].id,
                            defaultFlag : '0'
                        })
                    }
                }

            }
        } ];
    },

    getBaseInfoFormFields : function () {
        return [ {
            field : 'commentCategory',
            title : 'T_CORE_BPMN_PRESET_COMMENT.COMMENTCATEGORY',
            type : 'select',
            preInsert : true,
            validators : [ 'notEmpty' ],
            category : 'bpmn-preset-comment-category',
        }, {
            field : 'presetComment',
            title : 'T_CORE_BPMN_PRESET_COMMENT.PRESETCOMMENT',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,40]' ],
            editor : true,
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/bpmn/preset-comments'

});
