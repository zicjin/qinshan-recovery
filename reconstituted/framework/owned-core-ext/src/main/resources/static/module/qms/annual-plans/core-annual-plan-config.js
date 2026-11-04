coreAnnualPlan.detailPage.pageConfig = {

    review : {
        readonly : true,
        button : {
            'core-qms-annual-plan-detail-back-button' : {
                hidden : false
            },
            'core-qms-annual-plan-detail-pass-button' : {
                hidden : false
            },
            'core-qms-annual-plan-detail-reject-button' : {
                hidden : false
            },
            'core-qms-annual-plan-detail-print-button' : {
                hidden : false
            }
        }
    },

    edit : {
        readonly : false,
        button : {
            'core-qms-annual-plan-detail-pass-button' : {
                hidden : true
            },
            'core-qms-annual-plan-detail-reject-button' : {
                hidden : true
            }
        }
    },
    
    edit_dailysupervision : {
        readonly : false,
        button : {
            'core-qms-annual-plan-detail-pass-button' : {
                hidden : true
            }
        }
    },

    search : {
        readonly : true,
        button : {
            'core-qms-annual-plan-detail-print-button' : {
                hidden : false
            }
        }
    }
}