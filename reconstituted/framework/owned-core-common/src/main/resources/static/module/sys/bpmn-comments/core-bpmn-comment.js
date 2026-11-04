var coreBpmnComment = Gikam.getPageObject({
    getCommentListColumns : function() {
        return [ {
            title : '节点名称',
            field : 'taskName',
        }, {
            title : '审核人',
            field : 'auditedByName'
        }, {
            title : '审核时间',
            field : 'auditedTime'
        }, {
            title : '审核意见',
            field : 'comment',
            type : 'richText',
            width : 300
        }, {
            title : '审核路径',
            field : 'route',
            width : 200
        }, {
            title : '附件',
            field : 'attachmentName',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="Gikam.openFile(\'{fileId}\',true)">{name}</a>', {
                    fileId : r.attachmentId,
                    name : r.attachmentName
                });
            }
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/bpmn-exts'
});