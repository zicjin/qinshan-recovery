coreRectify.pageConfig = {

    detailPageEdit : {
        readonly : false
    },

    detailPageReview : {
        readonly : true,
        button : {
            'core-rectify-detail-back-button' : {
                hidden : false
            }
        }
    },

    audit : {
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-rectify-audit-list-grid-reject-button' : {
                hidden : false
            }
        }
    },

    qmAudit : {
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-rectify-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-rectify-audit-list-item-grid' : {
                'beAuditOrgName' : {
                    readonly : false
                },
                'rectifyGrade' : {
                    readonly : false
                },
                'deadline' : {
                    readonly : false
                },
                'riskAssessment' : {
                    readonly : false
                },
                'rectifyGuidance' : {
                    readonly : false
                }
            }
        }
    },

    groupReview : {
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-rectify-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-rectify-audit-list-grid' : {
                'reviewResult' : {
                    readonly : false
                }
            }
        }
    },

    rectifyCarryPlan : {
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-rectify-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-rectify-audit-list-item-grid' : {
                'deviationReason' : {
                    readonly : false
                },
                'causeanAlysis' : {
                    readonly : false
                },
                'rectifyCarryPlan' : {
                    readonly : false
                },
                'expectedDate' : {
                    readonly : false
                }
            }
        }
    },

    rectifyCarryPlanAudit : {
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-rectify-audit-list-grid-reject-button' : {
                hidden : false
            }
        }
    },

    execute : {
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-rectify-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-rectify-audit-list-item-grid' : {
                'rectifyResultDesc' : {
                    readonly : false
                }
            }
        }
    },

    executeAudit:{
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-rectify-audit-list-grid-reject-button' : {
                hidden : false
            }
        }
    },

    archive:{
        readonly : true,
        button : {
            'core-rectify-audit-list-grid-pass-button' : {
                hidden : false
            }
        }
    }

}