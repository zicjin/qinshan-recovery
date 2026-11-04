coreExternalAudit.pageConfig = {

    detailPageEdit : {
        readonly : false,
        button : {
            'core-external-audit-detail-pass-button' : {
                hidden : true
            },
            'core-external-audit-detail-reject-button' : {
                hidden : true
            },
        }
    },

    detailPageReview : {
        readonly : true,
        button : {
            'core-external-audit-detail-pass-button' : {
                hidden : false
            },
            'core-external-audit-detail-reject-button' : {
                hidden : false
            }
        }
    },

    detailPageReadonly : {
        readonly : true
    },

    //科室审核
    audit : {
        readonly : true,
        button : {
            'core-external-audit-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-external-audit-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        uploader : {
            'core-external-audit-audit-list-attachment-uploader' : {
                readonly : false
            }
        }
    },

    //质量负责人审核
    qmAudit : {
        readonly : true,
        button : {
            'core-external-audit-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-external-audit-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        uploader : {
            'core-external-audit-audit-list-attachment-uploader' : {
                readonly : false
            }
        }
    },

    //归档
    archive : {
        readonly : true,
        button : {
            'core-external-audit-audit-list-grid-pass-button' : {
                hidden : false
            }
        }
    }
}