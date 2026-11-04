var lcdpBizProcessStatus = Gikam.getPageObject({
    getCommentListColumns : function () {
        return [ {
            index : true
        }, {
            title : 'T_LCDP_BIZ_PROCESS_COMMENT.BPNODENAME',
            field : 'bpNodeName',
            width : 180
        }, {
            title : 'T_LCDP_BIZ_PROCESS_COMMENT.AUDITUSERNAME',
            field : 'auditUserName',
            width : 200
        }, {
            title : 'T_LCDP_BIZ_PROCESS_COMMENT.AUDITTIME',
            field : 'auditTime',
            width : 200
        }, {
            title : 'T_LCDP_BIZ_PROCESS_COMMENT.NEXTBPNODENAME',
            field : 'nextBpNodeName',
            width : 200
        }, {
            title : 'T_LCDP_BIZ_PROCESS_COMMENT.COMMENTS',
            field : 'comments',
            width : 300
        } ]
    },

    getNodeCandidatorListColumns : function () {
        return [ {
            index : true
        }, {
            title : 'T_LCDP_BIZ_PROCESS_NODE.BPNODENAME',
            field : 'bpNodeName',
            width : 180
        }, {
            title : 'T_LCDP_BIZ_PROCESS_NODE.ROLE',
            field : 'role',
            width : 400
        }, {
            title : 'T_LCDP_BIZ_PROCESS_NODE.USER',
            field : 'user',
            width : 400
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/lcdp/bps'
});