coreQualityControl.pageConfig = {
    detailPageEdit : {
        readonly : false,
        button : {
            'core-quality-control-detail-pass-button' : {
                hidden : true
            },
            'core-quality-control-detail-reject-button' : {
                hidden : true
            },
        }
    },

    detailPageReview : {
        readonly : true,
        button : {
            'core-quality-control-detail-pass-button' : {
                hidden : false
            },
            'core-quality-control-detail-reject-button' : {
                hidden : false
            },
            'core-quality-control-detail-print-button' : {
                hidden : false
            },
            'core-quality-control-detail-back-button' : {
                hidden : false
            }
        }
    },

    registItemsPageReview : {
        readonly : true,
        button : {
            'core-quality-control-detail-submit-button' : {
                hidden : false
            },
            'core-quality-control-detail-reject-button' : {
                hidden : false
            },
            'core-quality-control-detail-print-button' : {
                hidden : false
            },
            'core-quality-control-detail-back-button' : {
                hidden : false
            }
        }
    },

    auditPageReview : {
        readonly : true,
        button : {
            'core-quality-control-detail-pass-button' : {
                hidden : false
            },
            'core-quality-control-detail-reject-button' : {
                hidden : false
            },
            'core-quality-control-detail-print-button' : {
                hidden : false
            },
            'core-quality-control-detail-back-button' : {
                hidden : false
            }
        }
    },

    execDetailPage : {
        readonly : true,
        button : {
            'core-quality-control-detail-submit-button' : {
                hidden : false
            },
            'core-quality-control-detail-reject-button' : {
                hidden : false
            },
            'core-quality-control-detail-print-button' : {
                hidden : false
            },
            'core-quality-control-detail-back-button' : {
                hidden : false
            }
        }
    },

    groupAuditDetailPage : {
        readonly : true,
        button : {
            'core-quality-control-detail-pass-button' : {
                hidden : false
            },
            'core-quality-control-detail-reject-button' : {
                hidden : false
            },
            'core-quality-control-detail-print-button' : {
                hidden : false
            },
            'core-quality-control-detail-back-button' : {
                hidden : false
            }
        },
        form : {
            'core-quality-control-detail-qm-comment-form' : {
                'evnCondition' : {
                    readonly : false
                },
                'resourceConfig' : {
                    readonly : false
                },
                'planFeasibility' : {
                    readonly : false
                },
                'qcResult' : {
                    readonly : false
                }
            }
        }
    },

    audit : {},

    qmAudit : {},

    registItems : {},

    execute : {},

    groupAudit : {},

    techManagerAudit : {},

    resultRelease : {},

    archive : {
        readonly : true,
        button : {
            'core-quality-control-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-quality-control-audit-list-grid-print-button' : {
                hidden : false
            }
        },
    },

    search:{
        readonly : true
    }
}