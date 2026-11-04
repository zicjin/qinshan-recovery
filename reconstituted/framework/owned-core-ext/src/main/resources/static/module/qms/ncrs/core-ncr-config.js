coreNcr.pageConfig = {

    detailPageEdit : {
        readonly : false,
        button : {
            'core-ncr-detail-pass-button' : {
                hidden : true
            },
            'core-ncr-detail-reject-button' : {
                hidden : true
            },
        }
    },

    detailPageReview : {
        readonly : true,
        button : {
            'core-ncr-detail-delete-button' : {
                hidden : false
            },
            'core-ncr-detail-submit-button' : {
                hidden : false
            }
        },
        form : {
            'core-ncr-detail-base-info-form' : {
                'ncrRemark' : {
                    readonly : false
                }
            }
        }
    },

    auditPageReview : {
        readonly : true,
        button : {
            'core-ncr-detail-delete-button' : {
                hidden : false
            },
            'core-ncr-detail-submit-button' : {
                hidden : false
            }
        },
        form : {
            'core-ncr-detail-deal-form' : {
                'remark' : {
                    readonly : false
                }
            }
        }
    },

    ncrConfirmDetailPage : {
        readonly : true,
        button : {
            'core-ncr-detail-delete-button' : {
                hidden : false
            },
            'core-ncr-detail-submit-button' : {
                hidden : false
            }
        },
        form : {
            'core-ncr-detail-deal-form' : {
                'ncrConfirm' : {
                    readonly : false
                },
                'ncrReason' : {
                    readonly : false
                },
                'rectifyPlan' : {
                    readonly : false
                },
                'remark' : {
                    readonly : false
                }
            }
        }
    },

    executeDetailPage : {
        readonly : true,
        button : {
            'core-ncr-detail-delete-button' : {
                hidden : false
            },
            'core-ncr-detail-submit-button' : {
                hidden : false
            }
        },
        form : {
            'core-ncr-detail-deal-form' : {
                'rectifyCarry' : {
                    readonly : false
                },
                'remark' : {
                    readonly : false
                }
            }
        }
    },

    executeAuditDetailPage : {
        readonly : true,
        button : {
            'core-ncr-detail-delete-button' : {
                hidden : false
            },
            'core-ncr-detail-submit-button' : {
                hidden : false
            }
        },
        form : {
            'core-ncr-detail-deal-form' : {
                'verifyDesc' : {
                    readonly : false
                },
                'verifyResult' : {
                    readonly : false
                },
                'nextVerifyResult' : {
                    readonly : false
                },
                'nextVerifyDeadline' : {
                    readonly : false
                },
                'verifyUserName' : {
                    readonly : false
                },
                'verifyDate' : {
                    readonly : false
                },
                'remark' : {
                    readonly : false
                }
            }
        }
    },

    //科室审核
    audit : {

    },

    //质量负责人登记审核
    qmRegistAudit : {

    },

    //不符合项确认
    ncrConfirm : {

    },

    //质量负责人确认审核
    qmConfirmAudit : {

    },

    //纠正措施实施
    execute : {

    },

    //审核/验证
    executeAudit : {

    },

    //归档
    archive : {
        readonly : true,
        button : {
            'core-ncr-audit-list-grid-pass-button' : {
                hidden : false
            }
        }
    },

    search:{
        readonly : true
    }
}