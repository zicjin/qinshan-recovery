package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

/** 计划合同下单返回结果 */
public class MidPlanContractCreateResultDTO {


    /**
     * contractGbId : 0
     * contractNumber : string
     * orders : [{"orderGbId":0,"orderNumber":"string","relateOut":"string","samples":[{"instrumentSkus":[{"gbId":0,"relateOut":"string","sampleGbId":0}],"relateOut":"string","sampleGbId":0,"subInspectItems":[{"gbId":0,"relateOut":"string","sampleGbId":0}]}]}]
     * relateOut : string
     */

    private Long contractGbId;
    private String contractNumber;
    private String relateOut;
    private List<OrdersBean> orders;

    public Long getContractGbId() {
        return contractGbId;
    }

    public void setContractGbId(Long contractGbId) {
        this.contractGbId = contractGbId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getRelateOut() {
        return relateOut;
    }

    public void setRelateOut(String relateOut) {
        this.relateOut = relateOut;
    }

    public List<OrdersBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrdersBean> orders) {
        this.orders = orders;
    }

    public static class OrdersBean {
        /**
         * orderGbId : 0
         * orderNumber : string
         * relateOut : string
         * samples : [{"instrumentSkus":[{"gbId":0,"relateOut":"string","sampleGbId":0}],"relateOut":"string","sampleGbId":0,"subInspectItems":[{"gbId":0,"relateOut":"string","sampleGbId":0}]}]
         */

        private Long orderGbId;
        private String orderNumber;
        private String relateOut;
        private List<SamplesBean> samples;

        public Long getOrderGbId() {
            return orderGbId;
        }

        public void setOrderGbId(Long orderGbId) {
            this.orderGbId = orderGbId;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getRelateOut() {
            return relateOut;
        }

        public void setRelateOut(String relateOut) {
            this.relateOut = relateOut;
        }

        public List<SamplesBean> getSamples() {
            return samples;
        }

        public void setSamples(List<SamplesBean> samples) {
            this.samples = samples;
        }

        public static class SamplesBean {
            /**
             * instrumentSkus : [{"gbId":0,"relateOut":"string","sampleGbId":0}]
             * relateOut : string
             * sampleGbId : 0
             * subInspectItems : [{"gbId":0,"relateOut":"string","sampleGbId":0}]
             */

            private String relateOut;
            private Long sampleGbId;
            private List<InstrumentSkusBean> instrumentSkus;
            private List<SubInspectItemsBean> subInspectItems;

            public String getRelateOut() {
                return relateOut;
            }

            public void setRelateOut(String relateOut) {
                this.relateOut = relateOut;
            }

            public Long getSampleGbId() {
                return sampleGbId;
            }

            public void setSampleGbId(Long sampleGbId) {
                this.sampleGbId = sampleGbId;
            }

            public List<InstrumentSkusBean> getInstrumentSkus() {
                return instrumentSkus;
            }

            public void setInstrumentSkus(List<InstrumentSkusBean> instrumentSkus) {
                this.instrumentSkus = instrumentSkus;
            }

            public List<SubInspectItemsBean> getSubInspectItems() {
                return subInspectItems;
            }

            public void setSubInspectItems(List<SubInspectItemsBean> subInspectItems) {
                this.subInspectItems = subInspectItems;
            }

            public static class InstrumentSkusBean {
                /**
                 * gbId : 0
                 * relateOut : string
                 * sampleGbId : 0
                 */

                private Long gbId;
                private String relateOut;
                private Long sampleGbId;

                public Long getGbId() {
                    return gbId;
                }

                public void setGbId(Long gbId) {
                    this.gbId = gbId;
                }

                public String getRelateOut() {
                    return relateOut;
                }

                public void setRelateOut(String relateOut) {
                    this.relateOut = relateOut;
                }

                public Long getSampleGbId() {
                    return sampleGbId;
                }

                public void setSampleGbId(Long sampleGbId) {
                    this.sampleGbId = sampleGbId;
                }
            }

            public static class SubInspectItemsBean {
                /**
                 * gbId : 0
                 * relateOut : string
                 * sampleGbId : 0
                 */

                private Long gbId;
                private String relateOut;
                private Long sampleGbId;

                public Long getGbId() {
                    return gbId;
                }

                public void setGbId(Long gbId) {
                    this.gbId = gbId;
                }

                public String getRelateOut() {
                    return relateOut;
                }

                public void setRelateOut(String relateOut) {
                    this.relateOut = relateOut;
                }

                public Long getSampleGbId() {
                    return sampleGbId;
                }

                public void setSampleGbId(Long sampleGbId) {
                    this.sampleGbId = sampleGbId;
                }
            }
        }
    }
}
