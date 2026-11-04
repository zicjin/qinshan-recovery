lcdpBizProcessStatus.detailPage = {
    getBpCommentPanel : function () {
        var _this = this;
        return [ {
            title : 'LCDP.MODULE.BPS.TAB.COMMENT',
            items : [ {
                id : 'lcdp-biz-process-status-detail-comment-grid',
                type : 'grid',
                filter : false,
                page : false,
                toolbarHidden : true,
                url : IFM_CONTEXT + '/secure/core/module/lcdp/bps/comments/queries',
                requestData: {
                    bpId_EQ : _this.param.bpId,
                    bizId_EQ : _this.param.bizId
                },
                columns : lcdpBizProcessStatus.getCommentListColumns(),
            } ]
        } ];
    },

    getBpViewerInfoTab : function () {
        var _this = this;

        var url = Gikam.printf(IFM_CONTEXT + '/secure/core/module/lcdp/bps/page/diagram-viewer?bizId={bizId}&bpId={bpId}', {
            bizId : _this.param.bizId,
            bpId : _this.param.bpId
        })

        return {
            type : 'tab',
            panels : [ {
                title : 'LCDP.MODULE.BPS.TAB.BPVIWERINFO',
                items : [ {
                    type : 'iframe',
                    src : url
                } ]
            } ]
        }
    },

    getBpNodeUserPanel : function () {
        var _this = this;
        return [ {
            title : 'LCDP.MODULE.BPS.TAB.AUDIT_USER',
            items : [ {
                id : 'lcdp-biz-process-status-detail-node-user-grid',
                type : 'grid',
                filter : false,
                page : false,
                toolbarHidden : true,
                url : Gikam.printf(IFM_CONTEXT + '/secure/core/module/lcdp/bps/{bpId}/node-candidators/queries', {
                    bpId : _this.param.bpId
                }),
                columns : lcdpBizProcessStatus.getNodeCandidatorListColumns(),
            } ]
        } ];
    },

    getBpAuditTab : function () {
        var _this = this;

        var tab = {
            type : 'tab',
            panels : []
        }

        if (Gikam.isNotEmpty(_this.param.bizId)) {
            tab.panels = tab.panels.concat(_this.getBpCommentPanel());
        }
        tab.panels = tab.panels.concat(_this.getBpNodeUserPanel());

        return tab;
    },

    createPage : function () {
        var _this = this;

        if(Gikam.isEmpty(_this.param.bpId)){
            Gikam.postSync(IFM_CONTEXT +'/secure/core/module/lcdp/bps/instances/queries',Gikam.getJsonWrapper({
                f:{
                    bizId_EQ : _this.param.bizId,
                    bpCode_EQ : _this.param.bpCode
                }
            })).done(function (data) {
                if(Gikam.isNotEmpty(data.rows)){
                    _this.param = data.rows[0]
                }else {
                    Gikam.postSync(IFM_CONTEXT +'/secure/core/module/lcdp/bps/queries',Gikam.getJsonWrapper({
                        f:{
                            ACTIVATEDFLAG_SEQ : '1',
                            bpCode_EQ : _this.param.bpCode
                        },
                        latest_only: '1'
                    })).done(function (data) {
                        if(Gikam.isNotEmpty(data.rows)){
                            _this.param.bpId = data.rows[0].id;
                        }
                    })
                }
            })
        }

        Gikam.create('layout', {
            north : {
                height : '50%',
                items : [ _this.getBpViewerInfoTab() ]
            },
            center : {
                items : [ _this.getBpAuditTab() ]
            },

            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function (param) {
        this.param = param;
        var _this = this;

        _this.createPage();
    }
}