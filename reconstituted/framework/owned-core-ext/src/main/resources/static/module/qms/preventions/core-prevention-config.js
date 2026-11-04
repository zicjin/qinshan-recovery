corePrevention.pageConfig = {

    detailPageEdit : {
        readonly : false
    },

    detailPageReview : {
        readonly : true,
        button : {
            'core-prevention-detail-back-button' : {
                hidden : false
            }
        }
    },

    audit : {
        readonly : true,
        button : {
            'core-prevention-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-prevention-audit-list-grid-reject-button' : {
                hidden : false
            }
        }
    },

    qmAudit : {
        readonly : true,
        button : {
            'core-prevention-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-prevention-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-prevention-audit-list-item-grid' : {
                'beAuditOrgName' : {
                    readonly : false
                },
                'preventionGrade' : {
                    readonly : false
                },
                'deadline' : {
                    readonly : false
                },
                'riskAssessment' : {
                    readonly : false
                },
                'preventionGuidance' : {
                    readonly : false
                }
            }
        }
    },

    groupReview : {
        readonly : true,
        button : {
            'core-prevention-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-prevention-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-prevention-audit-list-grid' : {
                'reviewResult' : {
                    readonly : false
                }
            }
        }
    },

    solutionsPlan : {
        readonly : true,
        button : {
            'core-prevention-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-prevention-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-prevention-audit-list-item-grid' : {
                'causeanAlysis' : {
                    readonly : false
                },
                'preventionCarryPlan' : {
                    readonly : false
                },
                'expectedDate' : {
                    readonly : false
                }
            }
        }
    },

    solutionsPlanAudit : {
        readonly : true,
        button : {
            'core-prevention-audit-listt-grid-pass-button' : {
                hidden : false
            },
            'core-prevention-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
    },

    execute : {
        readonly : true,
        button : {
            'core-prevention-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-prevention-audit-list-grid-reject-button' : {
                hidden : false
            }
        },
        grid : {
            'core-prevention-audit-list-item-grid' : {
                'preventionResultDesc' : {
                    readonly : false
                }
            }
        }
    },

    executeAudit : {
        readonly : true,
        button : {
            'core-prevention-audit-list-grid-pass-button' : {
                hidden : false
            },
            'core-prevention-audit-list-grid-reject-button' : {
                hidden : false
            }
        }
    },

    archive : {
        readonly : true,
        button : {
            'core-prevention-audit-list-grid-pass-button' : {
                hidden : false
            }
        }
    }
}