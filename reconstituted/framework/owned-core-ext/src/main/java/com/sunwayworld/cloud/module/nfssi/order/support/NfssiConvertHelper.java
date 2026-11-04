/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderTestItemBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestItemDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonDTO;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportDTO;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemDTO;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NfssiConvertHelper {
    public static <S, T> void convert(S source, T target) {
        if (source instanceof CoreNfssiOrderDTO && target instanceof CoreNfssiOrderBean) {
            NfssiConvertHelper.convertOrderDtoToBean((CoreNfssiOrderDTO)source, (CoreNfssiOrderBean)target);
        }
        if (source instanceof CoreNfssiOrderBean && target instanceof CoreNfssiOrderDTO) {
            NfssiConvertHelper.convertOrderBeanToDto((CoreNfssiOrderBean)source, (CoreNfssiOrderDTO)target);
        }
        if (source instanceof CoreNfssiTestItemDTO && target instanceof CoreNfssiOrderTestItemBean) {
            NfssiConvertHelper.convertOrderItemDtoToBean((CoreNfssiTestItemDTO)source, (CoreNfssiOrderTestItemBean)target);
        }
        if (source instanceof CoreNfssiOrderTestItemBean && target instanceof CoreNfssiTestItemDTO) {
            NfssiConvertHelper.convertOrderItemBeanToDto((CoreNfssiOrderTestItemBean)source, (CoreNfssiTestItemDTO)target);
        }
        if (source instanceof CoreNfssiTestReasonDTO && target instanceof CoreNfssiTestReasonBean) {
            NfssiConvertHelper.convertTestReasonDtoToBean((CoreNfssiTestReasonDTO)source, (CoreNfssiTestReasonBean)target);
        }
        if (source instanceof CoreNfssiTestReasonBean && target instanceof CoreNfssiTestReasonDTO) {
            NfssiConvertHelper.convertTestReasonBeanToDto((CoreNfssiTestReasonBean)source, (CoreNfssiTestReasonDTO)target);
        }
        if (source instanceof CoreNfssiVerifyReasonDTO && target instanceof CoreNfssiVerifyReasonBean) {
            NfssiConvertHelper.convertVerifyReasonDtoToBean((CoreNfssiVerifyReasonDTO)source, (CoreNfssiVerifyReasonBean)target);
        }
        if (source instanceof CoreNfssiVerifyReasonBean && target instanceof CoreNfssiVerifyReasonDTO) {
            NfssiConvertHelper.convertVerifyReasonBeanToDto((CoreNfssiVerifyReasonBean)source, (CoreNfssiVerifyReasonDTO)target);
        }
        if (source instanceof CoreNfssiReportDTO && target instanceof CoreNfssiReportBean) {
            NfssiConvertHelper.convertReportDtoToBean((CoreNfssiReportDTO)source, (CoreNfssiReportBean)target);
        }
        if (source instanceof CoreNfssiReportBean && target instanceof CoreNfssiReportDTO) {
            NfssiConvertHelper.convertReportBeanToDto((CoreNfssiReportBean)source, (CoreNfssiReportDTO)target);
        }
        if (source instanceof CoreNfssiReportItemDTO && target instanceof CoreNfssiReportItemBean) {
            NfssiConvertHelper.convertReportItemDtoToBean((CoreNfssiReportItemDTO)source, (CoreNfssiReportItemBean)target);
        }
        if (source instanceof CoreNfssiReportItemBean && target instanceof CoreNfssiReportItemDTO) {
            NfssiConvertHelper.convertReportItemBeanToDto((CoreNfssiReportItemBean)source, (CoreNfssiReportItemDTO)target);
        }
    }

    public static CoreNfssiOrderDTO paraseCoreNfssiOrderDTO(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        CoreNfssiOrderDTO coreNfssiOrderDTO = JsonUtils.parse(jsonObject.getString("sampleInfo"), CoreNfssiOrderDTO.class);
        coreNfssiOrderDTO.setLastUpdatedTime(LocalDateTime.now());
        List<CoreNfssiTestItemDTO> nfssiTestItemDTOList = JsonUtils.parseList(jsonObject.getString("itemInfo"), CoreNfssiTestItemDTO.class);
        nfssiTestItemDTOList.forEach(e -> {
            e.setSp_s_16(coreNfssiOrderDTO.getSp_s_16());
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.getTestReason().forEach(t -> {
                t.setId(ApplicationContextHelper.getNextIdentity());
                t.setTestItemId(e.getId());
            });
            e.getVerifyReason().forEach(v -> {
                v.setId(ApplicationContextHelper.getNextIdentity());
                v.setTestItemId(e.getId());
            });
        });
        coreNfssiOrderDTO.setItemInfo(nfssiTestItemDTOList);
        return coreNfssiOrderDTO;
    }

    public static List<String> getOrderUpdateColList() {
        ArrayList<String> colList = new ArrayList<String>();
        ReflectionUtils.getFiledList(CoreNfssiOrderBean.class, new int[0]).forEach(field -> {
            if (!("id".equals(field.getName().toLowerCase()) || "serialversionuid".equals(field.getName().toLowerCase()) || "ext$".equals(field.getName().toLowerCase()))) {
                colList.add(field.getName());
            }
        });
        return colList;
    }

    private static void convertOrderDtoToBean(CoreNfssiOrderDTO coreNfssiOrderDTO, CoreNfssiOrderBean coreNfssiOrderBean) {
        coreNfssiOrderBean.setId(coreNfssiOrderDTO.getSp_s_16());
        coreNfssiOrderBean.setBack(coreNfssiOrderDTO.getSp_i_jgback());
        coreNfssiOrderBean.setSamplingPlaceOrgName(coreNfssiOrderDTO.getSp_s_1());
        coreNfssiOrderBean.setSamplingPlace(coreNfssiOrderDTO.getSp_s_2());
        coreNfssiOrderBean.setProvince(coreNfssiOrderDTO.getSp_s_3());
        coreNfssiOrderBean.setCity(coreNfssiOrderDTO.getSp_s_4());
        coreNfssiOrderBean.setCounty(coreNfssiOrderDTO.getSp_s_5());
        coreNfssiOrderBean.setSamplingUnit(coreNfssiOrderDTO.getSp_s_6());
        coreNfssiOrderBean.setAddress(coreNfssiOrderDTO.getSp_s_7());
        coreNfssiOrderBean.setLegalPerson(coreNfssiOrderDTO.getSp_s_8());
        coreNfssiOrderBean.setPostcode(coreNfssiOrderDTO.getSp_s_10());
        coreNfssiOrderBean.setContact(coreNfssiOrderDTO.getSp_s_11());
        coreNfssiOrderBean.setContactMobile(coreNfssiOrderDTO.getSp_s_12());
        coreNfssiOrderBean.setProductionLicenseNo(coreNfssiOrderDTO.getSp_s_13());
        coreNfssiOrderBean.setSampleName(coreNfssiOrderDTO.getSp_s_14());
        coreNfssiOrderBean.setSamplingQty(coreNfssiOrderDTO.getSp_n_15());
        coreNfssiOrderBean.setFirstCategory(coreNfssiOrderDTO.getSp_s_17());
        coreNfssiOrderBean.setSecondCategory(coreNfssiOrderDTO.getSp_s_18());
        coreNfssiOrderBean.setThirdCategory(coreNfssiOrderDTO.getSp_s_19());
        coreNfssiOrderBean.setFourthCategory(coreNfssiOrderDTO.getSp_s_20());
        coreNfssiOrderBean.setSampleSource(coreNfssiOrderDTO.getSp_s_21());
        coreNfssiOrderBean.setAnnualSales(coreNfssiOrderDTO.getSp_s_23());
        coreNfssiOrderBean.setSamplingMethod(coreNfssiOrderDTO.getSp_s_24());
        coreNfssiOrderBean.setSamplingInstrument(coreNfssiOrderDTO.getSp_s_25());
        coreNfssiOrderBean.setSpecification(coreNfssiOrderDTO.getSp_s_26());
        coreNfssiOrderBean.setSampleBatchNo(coreNfssiOrderDTO.getSp_s_27());
        coreNfssiOrderBean.setProductDate(coreNfssiOrderDTO.getSp_d_28());
        coreNfssiOrderBean.setQualityGuaranteePeriod(coreNfssiOrderDTO.getSp_n_29());
        coreNfssiOrderBean.setSamplingKeepCondition(coreNfssiOrderDTO.getSp_s_30());
        coreNfssiOrderBean.setSamplePack(coreNfssiOrderDTO.getSp_s_33());
        coreNfssiOrderBean.setRemark(coreNfssiOrderDTO.getSp_s_34());
        coreNfssiOrderBean.setSamplingOrgName(coreNfssiOrderDTO.getSp_s_35());
        coreNfssiOrderBean.setSamplingDate(coreNfssiOrderDTO.getSp_d_38());
        coreNfssiOrderBean.setSamplingMobile(coreNfssiOrderDTO.getSp_s_39());
        coreNfssiOrderBean.setSamplingOrgContact(coreNfssiOrderDTO.getSp_s_40());
        coreNfssiOrderBean.setTestPurpose(coreNfssiOrderDTO.getSp_s_44());
        coreNfssiOrderBean.setSampleShap(coreNfssiOrderDTO.getSp_s_61());
        coreNfssiOrderBean.setSampleCategory(coreNfssiOrderDTO.getSp_s_62());
        coreNfssiOrderBean.setPackageCategory(coreNfssiOrderDTO.getSp_s_63());
        coreNfssiOrderBean.setManufacturer(coreNfssiOrderDTO.getSp_s_64());
        coreNfssiOrderBean.setManufacturerAdress(coreNfssiOrderDTO.getSp_s_65());
        coreNfssiOrderBean.setSamplingTache(coreNfssiOrderDTO.getSp_s_68());
        coreNfssiOrderBean.setExecutiveStandard(coreNfssiOrderDTO.getSp_s_72());
        coreNfssiOrderBean.setQualityLevel(coreNfssiOrderDTO.getSp_s_73());
        coreNfssiOrderBean.setSampleLogo(coreNfssiOrderDTO.getSp_s_74());
        coreNfssiOrderBean.setManufacturerContact(coreNfssiOrderDTO.getSp_s_75());
        coreNfssiOrderBean.setManufacturerMobile(coreNfssiOrderDTO.getSp_s_76());
        coreNfssiOrderBean.setReceivedDate(coreNfssiOrderDTO.getSp_d_86());
        coreNfssiOrderBean.setAreaCategory(coreNfssiOrderDTO.getSp_s_201());
        coreNfssiOrderBean.setManufacturerProvince(coreNfssiOrderDTO.getSp_s_202());
        coreNfssiOrderBean.setSampleProperty(coreNfssiOrderDTO.getSp_s_203());
        coreNfssiOrderBean.setPrice(coreNfssiOrderDTO.getSp_s_204());
        coreNfssiOrderBean.setSamplingBaseNum(coreNfssiOrderDTO.getSp_s_206());
        coreNfssiOrderBean.setSpareNum(coreNfssiOrderDTO.getSp_s_208());
        coreNfssiOrderBean.setSamplingOrgAdress(coreNfssiOrderDTO.getSp_s_211());
        coreNfssiOrderBean.setSamplingOrgPostCode(coreNfssiOrderDTO.getSp_s_212());
        coreNfssiOrderBean.setSamplingOrgFax(coreNfssiOrderDTO.getSp_s_213());
        coreNfssiOrderBean.setTestPlaceCreditCode(coreNfssiOrderDTO.getSp_s_215());
        coreNfssiOrderBean.setManufacturerCity(coreNfssiOrderDTO.getSp_s_220());
        coreNfssiOrderBean.setManufacturerCounty(coreNfssiOrderDTO.getSp_s_221());
        coreNfssiOrderBean.setSampleBarCode(coreNfssiOrderDTO.getSp_s_222());
        coreNfssiOrderBean.setTaskSource(coreNfssiOrderDTO.getSp_s_2_1());
        coreNfssiOrderBean.setTestPlaceFax(coreNfssiOrderDTO.getSp_s_bsfl());
        coreNfssiOrderBean.setEntrustContact(coreNfssiOrderDTO.getSp_s_lxr());
        coreNfssiOrderBean.setEntrustProductionLicenseNo(coreNfssiOrderDTO.getSp_s_qs());
        coreNfssiOrderBean.setEntrustEnterpriseAdress(coreNfssiOrderDTO.getSp_s_qydz());
        coreNfssiOrderBean.setEntrustEnterprise(coreNfssiOrderDTO.getSp_s_qymc());
        coreNfssiOrderBean.setImportation(coreNfssiOrderDTO.getSp_s_sfjk());
        coreNfssiOrderBean.setHasEntrustEnterpriseInfo(coreNfssiOrderDTO.getSp_s_sfwtsc());
        coreNfssiOrderBean.setEntrustPhone(coreNfssiOrderDTO.getSp_s_tel());
        coreNfssiOrderBean.setEntrustProvince(coreNfssiOrderDTO.getSp_s_wtsheng());
        coreNfssiOrderBean.setEntrustCity(coreNfssiOrderDTO.getSp_s_wtshi());
        coreNfssiOrderBean.setEntrustCountry(coreNfssiOrderDTO.getSp_s_wtxian());
        coreNfssiOrderBean.setOriginPlace(coreNfssiOrderDTO.getSp_s_ycg());
        coreNfssiOrderBean.setLicenseType(coreNfssiOrderDTO.getSp_xkz());
        coreNfssiOrderBean.setLicenseNo(coreNfssiOrderDTO.getSp_xkz_id());
        coreNfssiOrderBean.setBarCode(coreNfssiOrderDTO.getBarcode());
        coreNfssiOrderBean.setRainbowCode(coreNfssiOrderDTO.getRainbowcode());
        coreNfssiOrderBean.setRainbowCodePath(coreNfssiOrderDTO.getRainbowcode_url());
        coreNfssiOrderBean.setApprovalNo(coreNfssiOrderDTO.getHealth_code());
        coreNfssiOrderBean.setFuncCategoryCollection(coreNfssiOrderDTO.getHealth_func_cat());
        coreNfssiOrderBean.setInspectOrgName(coreNfssiOrderDTO.getSp_s_43());
        coreNfssiOrderBean.setInspectionDate(coreNfssiOrderDTO.getSp_d_46());
        coreNfssiOrderBean.setInspectionOrgContact(coreNfssiOrderDTO.getSp_s_49());
        coreNfssiOrderBean.setInspectOrgContactMobile(coreNfssiOrderDTO.getSp_s_50());
        coreNfssiOrderBean.setInspectOrgContactEmail(coreNfssiOrderDTO.getSp_s_51());
        coreNfssiOrderBean.setInspectSealPerson(coreNfssiOrderDTO.getSp_s_85());
        coreNfssiOrderBean.setInspectSealPersonMobile(coreNfssiOrderDTO.getSp_s_87());
        coreNfssiOrderBean.setInspectSealPersonEmail(coreNfssiOrderDTO.getSp_s_88());
        coreNfssiOrderBean.setSampler(coreNfssiOrderDTO.getSp_s_37());
        coreNfssiOrderBean.setSamplerMobile(coreNfssiOrderDTO.getSp_s_41());
        coreNfssiOrderBean.setSamplerEmail(coreNfssiOrderDTO.getSp_s_42());
        coreNfssiOrderBean.setSamplingOrgLevel(coreNfssiOrderDTO.getSp_s_36());
        coreNfssiOrderBean.setSamplingOrgProvince(coreNfssiOrderDTO.getSp_s_52());
        coreNfssiOrderBean.setSampleUrl(coreNfssiOrderDTO.getSp_s_dwwz());
        coreNfssiOrderBean.setSamplingPicPath(coreNfssiOrderDTO.getSp_s_wcmc());
        coreNfssiOrderBean.setNetworkLicenseNo(coreNfssiOrderDTO.getSp_s_wcyyzzh());
        coreNfssiOrderBean.setIcpNo(coreNfssiOrderDTO.getSp_s_wcicp());
        coreNfssiOrderBean.setNetworkPlatformProvince(coreNfssiOrderDTO.getSp_s_wcsheng());
        coreNfssiOrderBean.setNetworkPlatformCity(coreNfssiOrderDTO.getSp_s_wcshi());
        coreNfssiOrderBean.setNetworkPlatformCounty(coreNfssiOrderDTO.getSp_s_wcxian());
        coreNfssiOrderBean.setNetworkPlatformAdress(coreNfssiOrderDTO.getSp_s_wcdz());
        coreNfssiOrderBean.setNotificationPicPath(coreNfssiOrderDTO.getSp_s_wcwz());
        coreNfssiOrderBean.setNetworkPlatformContact(coreNfssiOrderDTO.getSp_s_wclxr());
        coreNfssiOrderBean.setNetworkPlatformContactMobile(coreNfssiOrderDTO.getSp_s_wctel());
        coreNfssiOrderBean.setNetworkOrderNo(coreNfssiOrderDTO.getSp_s_wcbh());
        coreNfssiOrderBean.setSubmit(coreNfssiOrderDTO.getIs_submit());
        coreNfssiOrderBean.setOnlineShopName(coreNfssiOrderDTO.getSp_s_wcdpmc());
        coreNfssiOrderBean.setDateCategory(coreNfssiOrderDTO.getSp_d_28_name());
        coreNfssiOrderBean.setSamplingCategory(coreNfssiOrderDTO.getSp_s_cycj());
        coreNfssiOrderBean.setEntrustProperty(coreNfssiOrderDTO.getSp_s_wtxz());
        coreNfssiOrderBean.setReportTaskB(coreNfssiOrderDTO.getScbname());
        coreNfssiOrderBean.setReportTaskA(coreNfssiOrderDTO.getScaname());
        coreNfssiOrderBean.setTestState(coreNfssiOrderDTO.getSp_i_state());
        coreNfssiOrderBean.setSampleType(coreNfssiOrderDTO.getSample_type());
        coreNfssiOrderBean.setNffsiUpdatedTime(coreNfssiOrderDTO.getUpdated_at());
        coreNfssiOrderBean.setLastUpdatedTime(coreNfssiOrderDTO.getLastUpdatedTime());
    }

    private static void convertOrderBeanToDto(CoreNfssiOrderBean coreNfssiOrderBean, CoreNfssiOrderDTO coreNfssiOrderDTO) {
        coreNfssiOrderDTO.setSp_s_16(coreNfssiOrderBean.getId());
        coreNfssiOrderDTO.setSp_i_jgback(coreNfssiOrderBean.getBack());
        coreNfssiOrderDTO.setSp_s_1(coreNfssiOrderBean.getSamplingPlaceOrgName());
        coreNfssiOrderDTO.setSp_s_2(coreNfssiOrderBean.getSamplingPlace());
        coreNfssiOrderDTO.setSp_s_3(coreNfssiOrderBean.getProvince());
        coreNfssiOrderDTO.setSp_s_4(coreNfssiOrderBean.getCity());
        coreNfssiOrderDTO.setSp_s_5(coreNfssiOrderBean.getCounty());
        coreNfssiOrderDTO.setSp_s_6(coreNfssiOrderBean.getSamplingUnit());
        coreNfssiOrderDTO.setSp_s_7(coreNfssiOrderBean.getAddress());
        coreNfssiOrderDTO.setSp_s_8(coreNfssiOrderBean.getLegalPerson());
        coreNfssiOrderDTO.setSp_s_10(coreNfssiOrderBean.getPostcode());
        coreNfssiOrderDTO.setSp_s_11(coreNfssiOrderBean.getContact());
        coreNfssiOrderDTO.setSp_s_12(coreNfssiOrderBean.getContactMobile());
        coreNfssiOrderDTO.setSp_s_13(coreNfssiOrderBean.getProductionLicenseNo());
        coreNfssiOrderDTO.setSp_s_14(coreNfssiOrderBean.getSampleName());
        coreNfssiOrderDTO.setSp_n_15(coreNfssiOrderBean.getSamplingQty());
        coreNfssiOrderDTO.setSp_s_17(coreNfssiOrderBean.getFirstCategory());
        coreNfssiOrderDTO.setSp_s_18(coreNfssiOrderBean.getSecondCategory());
        coreNfssiOrderDTO.setSp_s_19(coreNfssiOrderBean.getThirdCategory());
        coreNfssiOrderDTO.setSp_s_20(coreNfssiOrderBean.getFourthCategory());
        coreNfssiOrderDTO.setSp_s_21(coreNfssiOrderBean.getSampleSource());
        coreNfssiOrderDTO.setSp_s_23(coreNfssiOrderBean.getAnnualSales());
        coreNfssiOrderDTO.setSp_s_24(coreNfssiOrderBean.getSamplingMethod());
        coreNfssiOrderDTO.setSp_s_25(coreNfssiOrderBean.getSamplingInstrument());
        coreNfssiOrderDTO.setSp_s_26(coreNfssiOrderBean.getSpecification());
        coreNfssiOrderDTO.setSp_s_27(coreNfssiOrderBean.getSampleBatchNo());
        coreNfssiOrderDTO.setSp_d_28(coreNfssiOrderBean.getProductDate());
        coreNfssiOrderDTO.setSp_n_29(coreNfssiOrderBean.getQualityGuaranteePeriod());
        coreNfssiOrderDTO.setSp_s_30(coreNfssiOrderBean.getSamplingKeepCondition());
        coreNfssiOrderDTO.setSp_s_33(coreNfssiOrderBean.getSamplePack());
        coreNfssiOrderDTO.setSp_s_34(coreNfssiOrderBean.getRemark());
        coreNfssiOrderDTO.setSp_s_35(coreNfssiOrderBean.getSamplingOrgName());
        coreNfssiOrderDTO.setSp_d_38(coreNfssiOrderBean.getSamplingDate());
        coreNfssiOrderDTO.setSp_s_39(coreNfssiOrderBean.getSamplingMobile());
        coreNfssiOrderDTO.setSp_s_40(coreNfssiOrderBean.getSamplingOrgContact());
        coreNfssiOrderDTO.setSp_s_44(coreNfssiOrderBean.getTestPurpose());
        coreNfssiOrderDTO.setSp_s_61(coreNfssiOrderBean.getSampleShap());
        coreNfssiOrderDTO.setSp_s_62(coreNfssiOrderBean.getSampleCategory());
        coreNfssiOrderDTO.setSp_s_63(coreNfssiOrderBean.getPackageCategory());
        coreNfssiOrderDTO.setSp_s_64(coreNfssiOrderBean.getManufacturer());
        coreNfssiOrderDTO.setSp_s_65(coreNfssiOrderBean.getManufacturerAdress());
        coreNfssiOrderDTO.setSp_s_68(coreNfssiOrderBean.getSamplingTache());
        coreNfssiOrderDTO.setSp_s_72(coreNfssiOrderBean.getExecutiveStandard());
        coreNfssiOrderDTO.setSp_s_73(coreNfssiOrderBean.getQualityLevel());
        coreNfssiOrderDTO.setSp_s_74(coreNfssiOrderBean.getSampleLogo());
        coreNfssiOrderDTO.setSp_s_75(coreNfssiOrderBean.getManufacturerContact());
        coreNfssiOrderDTO.setSp_s_76(coreNfssiOrderBean.getManufacturerMobile());
        coreNfssiOrderDTO.setSp_d_86(coreNfssiOrderBean.getReceivedDate());
        coreNfssiOrderDTO.setSp_s_201(coreNfssiOrderBean.getAreaCategory());
        coreNfssiOrderDTO.setSp_s_202(coreNfssiOrderBean.getManufacturerProvince());
        coreNfssiOrderDTO.setSp_s_203(coreNfssiOrderBean.getSampleProperty());
        coreNfssiOrderDTO.setSp_s_204(coreNfssiOrderBean.getPrice());
        coreNfssiOrderDTO.setSp_s_206(coreNfssiOrderBean.getSamplingBaseNum());
        coreNfssiOrderDTO.setSp_s_208(coreNfssiOrderBean.getSpareNum());
        coreNfssiOrderDTO.setSp_s_211(coreNfssiOrderBean.getSamplingOrgAdress());
        coreNfssiOrderDTO.setSp_s_212(coreNfssiOrderBean.getSamplingOrgPostCode());
        coreNfssiOrderDTO.setSp_s_213(coreNfssiOrderBean.getSamplingOrgFax());
        coreNfssiOrderDTO.setSp_s_215(coreNfssiOrderBean.getTestPlaceCreditCode());
        coreNfssiOrderDTO.setSp_s_220(coreNfssiOrderBean.getManufacturerCity());
        coreNfssiOrderDTO.setSp_s_221(coreNfssiOrderBean.getManufacturerCounty());
        coreNfssiOrderDTO.setSp_s_222(coreNfssiOrderBean.getSampleBarCode());
        coreNfssiOrderDTO.setSp_s_2_1(coreNfssiOrderBean.getTaskSource());
        coreNfssiOrderDTO.setSp_s_bsfl(coreNfssiOrderBean.getTestPlaceFax());
        coreNfssiOrderDTO.setSp_s_lxr(coreNfssiOrderBean.getEntrustContact());
        coreNfssiOrderDTO.setSp_s_qs(coreNfssiOrderBean.getEntrustProductionLicenseNo());
        coreNfssiOrderDTO.setSp_s_qydz(coreNfssiOrderBean.getEntrustEnterpriseAdress());
        coreNfssiOrderDTO.setSp_s_qymc(coreNfssiOrderBean.getEntrustEnterprise());
        coreNfssiOrderDTO.setSp_s_sfjk(coreNfssiOrderBean.getImportation());
        coreNfssiOrderDTO.setSp_s_sfwtsc(coreNfssiOrderBean.getHasEntrustEnterpriseInfo());
        coreNfssiOrderDTO.setSp_s_tel(coreNfssiOrderBean.getEntrustPhone());
        coreNfssiOrderDTO.setSp_s_wtsheng(coreNfssiOrderBean.getEntrustProvince());
        coreNfssiOrderDTO.setSp_s_wtshi(coreNfssiOrderBean.getEntrustCity());
        coreNfssiOrderDTO.setSp_s_wtxian(coreNfssiOrderBean.getEntrustCountry());
        coreNfssiOrderDTO.setSp_s_ycg(coreNfssiOrderBean.getOriginPlace());
        coreNfssiOrderDTO.setSp_xkz(coreNfssiOrderBean.getLicenseType());
        coreNfssiOrderDTO.setSp_xkz_id(coreNfssiOrderBean.getLicenseNo());
        coreNfssiOrderDTO.setBarcode(coreNfssiOrderBean.getBarCode());
        coreNfssiOrderDTO.setRainbowcode(coreNfssiOrderBean.getRainbowCode());
        coreNfssiOrderDTO.setRainbowcode_url(coreNfssiOrderBean.getRainbowCodePath());
        coreNfssiOrderDTO.setHealth_code(coreNfssiOrderBean.getApprovalNo());
        coreNfssiOrderDTO.setHealth_func_cat(coreNfssiOrderBean.getFuncCategoryCollection());
        coreNfssiOrderDTO.setSp_s_43(coreNfssiOrderBean.getInspectOrgName());
        coreNfssiOrderDTO.setSp_d_46(coreNfssiOrderBean.getInspectionDate());
        coreNfssiOrderDTO.setSp_s_49(coreNfssiOrderBean.getInspectionOrgContact());
        coreNfssiOrderDTO.setSp_s_50(coreNfssiOrderBean.getInspectOrgContactMobile());
        coreNfssiOrderDTO.setSp_s_51(coreNfssiOrderBean.getInspectOrgContactEmail());
        coreNfssiOrderDTO.setSp_s_85(coreNfssiOrderBean.getInspectSealPerson());
        coreNfssiOrderDTO.setSp_s_87(coreNfssiOrderBean.getInspectSealPersonMobile());
        coreNfssiOrderDTO.setSp_s_88(coreNfssiOrderBean.getInspectSealPersonEmail());
        coreNfssiOrderDTO.setSp_s_37(coreNfssiOrderBean.getSampler());
        coreNfssiOrderDTO.setSp_s_41(coreNfssiOrderBean.getSamplerMobile());
        coreNfssiOrderDTO.setSp_s_42(coreNfssiOrderBean.getSamplerEmail());
        coreNfssiOrderDTO.setSp_s_36(coreNfssiOrderBean.getSamplingOrgLevel());
        coreNfssiOrderDTO.setSp_s_52(coreNfssiOrderBean.getSamplingOrgProvince());
        coreNfssiOrderDTO.setSp_s_dwwz(coreNfssiOrderBean.getSampleUrl());
        coreNfssiOrderDTO.setSp_s_wcmc(coreNfssiOrderBean.getSamplingPicPath());
        coreNfssiOrderDTO.setSp_s_wcyyzzh(coreNfssiOrderBean.getNetworkLicenseNo());
        coreNfssiOrderDTO.setSp_s_wcicp(coreNfssiOrderBean.getIcpNo());
        coreNfssiOrderDTO.setSp_s_wcsheng(coreNfssiOrderBean.getNetworkPlatformProvince());
        coreNfssiOrderDTO.setSp_s_wcshi(coreNfssiOrderBean.getNetworkPlatformCity());
        coreNfssiOrderDTO.setSp_s_wcxian(coreNfssiOrderBean.getNetworkPlatformCounty());
        coreNfssiOrderDTO.setSp_s_wcdz(coreNfssiOrderBean.getNetworkPlatformAdress());
        coreNfssiOrderDTO.setSp_s_wcwz(coreNfssiOrderBean.getNotificationPicPath());
        coreNfssiOrderDTO.setSp_s_wclxr(coreNfssiOrderBean.getNetworkPlatformContact());
        coreNfssiOrderDTO.setSp_s_wctel(coreNfssiOrderBean.getNetworkPlatformContactMobile());
        coreNfssiOrderDTO.setSp_s_wcbh(coreNfssiOrderBean.getNetworkOrderNo());
        coreNfssiOrderDTO.setIs_submit(coreNfssiOrderBean.getSubmit());
        coreNfssiOrderDTO.setSp_s_wcdpmc(coreNfssiOrderBean.getOnlineShopName());
        coreNfssiOrderDTO.setSp_d_28_name(coreNfssiOrderBean.getDateCategory());
        coreNfssiOrderDTO.setSp_s_cycj(coreNfssiOrderBean.getSamplingCategory());
        coreNfssiOrderDTO.setSp_s_wtxz(coreNfssiOrderBean.getEntrustProperty());
        coreNfssiOrderDTO.setScbname(coreNfssiOrderBean.getReportTaskB());
        coreNfssiOrderDTO.setScaname(coreNfssiOrderBean.getReportTaskA());
        coreNfssiOrderDTO.setSp_i_state(coreNfssiOrderBean.getTestState());
        coreNfssiOrderDTO.setSample_type(coreNfssiOrderBean.getSampleType());
        coreNfssiOrderDTO.setUpdated_at(coreNfssiOrderBean.getNffsiUpdatedTime());
        coreNfssiOrderDTO.setLastUpdatedTime(coreNfssiOrderBean.getLastUpdatedTime());
    }

    private static void convertOrderItemDtoToBean(CoreNfssiTestItemDTO coreNfssiTestItemDTO, CoreNfssiOrderTestItemBean coreNfssiOrderTestItemBean) {
        coreNfssiOrderTestItemBean.setId(coreNfssiTestItemDTO.getId());
        coreNfssiOrderTestItemBean.setOrderId(coreNfssiTestItemDTO.getSp_s_16());
        coreNfssiOrderTestItemBean.setItemName(coreNfssiTestItemDTO.getItem());
        coreNfssiOrderTestItemBean.setItemType(coreNfssiTestItemDTO.getItemType());
        coreNfssiOrderTestItemBean.setFuncCategory(coreNfssiTestItemDTO.getGnlb());
    }

    private static void convertOrderItemBeanToDto(CoreNfssiOrderTestItemBean coreNfssiOrderTestItemBean, CoreNfssiTestItemDTO coreNfssiTestItemDTO) {
        coreNfssiTestItemDTO.setId(coreNfssiOrderTestItemBean.getId());
        coreNfssiTestItemDTO.setSp_s_16(coreNfssiOrderTestItemBean.getOrderId());
        coreNfssiTestItemDTO.setItem(coreNfssiOrderTestItemBean.getItemName());
        coreNfssiTestItemDTO.setItemType(coreNfssiOrderTestItemBean.getItemType());
        coreNfssiTestItemDTO.setGnlb(coreNfssiOrderTestItemBean.getFuncCategory());
    }

    private static void convertTestReasonDtoToBean(CoreNfssiTestReasonDTO coreNfssiTestReasonDTO, CoreNfssiTestReasonBean coreNfssiTestReasonBean) {
        coreNfssiTestReasonBean.setId(coreNfssiTestReasonDTO.getId());
        coreNfssiTestReasonBean.setTestItemId(coreNfssiTestReasonDTO.getTestItemId());
        coreNfssiTestReasonBean.setRemark(coreNfssiTestReasonDTO.getBz());
        coreNfssiTestReasonBean.setInspectStandard(coreNfssiTestReasonDTO.getSpdata_3());
        coreNfssiTestReasonBean.setResultUnit(coreNfssiTestReasonDTO.getSpdata_18());
        coreNfssiTestReasonBean.setShortInspectStandard(coreNfssiTestReasonDTO.getSm());
        coreNfssiTestReasonBean.setStdCheckLimitUnit(coreNfssiTestReasonDTO.getSpdata_6());
        coreNfssiTestReasonBean.setStdCheckLimit(coreNfssiTestReasonDTO.getSpdata_5());
        coreNfssiTestReasonBean.setReportTaskA(coreNfssiTestReasonDTO.getSp_s_70());
        coreNfssiTestReasonBean.setReportTaskB(coreNfssiTestReasonDTO.getSp_s_67());
        coreNfssiTestReasonBean.setNecessary(coreNfssiTestReasonDTO.getInReserve());
    }

    private static void convertTestReasonBeanToDto(CoreNfssiTestReasonBean coreNfssiTestReasonBean, CoreNfssiTestReasonDTO coreNfssiTestReasonDTO) {
        coreNfssiTestReasonDTO.setId(coreNfssiTestReasonBean.getId());
        coreNfssiTestReasonDTO.setTestItemId(coreNfssiTestReasonBean.getTestItemId());
        coreNfssiTestReasonDTO.setBz(coreNfssiTestReasonBean.getRemark());
        coreNfssiTestReasonDTO.setSpdata_3(coreNfssiTestReasonBean.getInspectStandard());
        coreNfssiTestReasonDTO.setSpdata_18(coreNfssiTestReasonBean.getResultUnit());
        coreNfssiTestReasonDTO.setSm(coreNfssiTestReasonBean.getShortInspectStandard());
        coreNfssiTestReasonDTO.setSpdata_6(coreNfssiTestReasonBean.getStdCheckLimitUnit());
        coreNfssiTestReasonDTO.setSpdata_5(coreNfssiTestReasonBean.getStdCheckLimit());
        coreNfssiTestReasonDTO.setSp_s_70(coreNfssiTestReasonBean.getReportTaskA());
        coreNfssiTestReasonDTO.setSp_s_67(coreNfssiTestReasonBean.getReportTaskB());
        coreNfssiTestReasonDTO.setInReserve(coreNfssiTestReasonBean.getNecessary());
    }

    private static void convertVerifyReasonDtoToBean(CoreNfssiVerifyReasonDTO coreNfssiVerifyReasonDTO, CoreNfssiVerifyReasonBean coreNfssiVerifyReasonBean) {
        coreNfssiVerifyReasonBean.setId(coreNfssiVerifyReasonDTO.getId());
        coreNfssiVerifyReasonBean.setTestItemId(coreNfssiVerifyReasonDTO.getTestItemId());
        coreNfssiVerifyReasonBean.setStdMinLimitUnit(coreNfssiVerifyReasonDTO.getSpdata_10());
        coreNfssiVerifyReasonBean.setStdMinLimit(coreNfssiVerifyReasonDTO.getSpdata_9());
        coreNfssiVerifyReasonBean.setMaxLimit(coreNfssiVerifyReasonDTO.getSpdata_15());
        coreNfssiVerifyReasonBean.setStdMaxLimit(coreNfssiVerifyReasonDTO.getSpdata_13());
        coreNfssiVerifyReasonBean.setStdMaxLimitUnit(coreNfssiVerifyReasonDTO.getSpdata_14());
        coreNfssiVerifyReasonBean.setJudgeStandard(coreNfssiVerifyReasonDTO.getSpdata_4());
        coreNfssiVerifyReasonBean.setMinLimitUnit(coreNfssiVerifyReasonDTO.getSpdata_11());
    }

    private static void convertVerifyReasonBeanToDto(CoreNfssiVerifyReasonBean coreNfssiVerifyReasonBean, CoreNfssiVerifyReasonDTO coreNfssiVerifyReasonDTO) {
        coreNfssiVerifyReasonDTO.setId(coreNfssiVerifyReasonBean.getId());
        coreNfssiVerifyReasonDTO.setTestItemId(coreNfssiVerifyReasonBean.getTestItemId());
        coreNfssiVerifyReasonDTO.setSpdata_10(coreNfssiVerifyReasonBean.getStdMinLimitUnit());
        coreNfssiVerifyReasonDTO.setSpdata_9(coreNfssiVerifyReasonBean.getStdMinLimit());
        coreNfssiVerifyReasonDTO.setSpdata_15(coreNfssiVerifyReasonBean.getMaxLimit());
        coreNfssiVerifyReasonDTO.setSpdata_13(coreNfssiVerifyReasonBean.getStdMaxLimit());
        coreNfssiVerifyReasonDTO.setSpdata_14(coreNfssiVerifyReasonBean.getStdMaxLimitUnit());
        coreNfssiVerifyReasonDTO.setSpdata_4(coreNfssiVerifyReasonBean.getJudgeStandard());
        coreNfssiVerifyReasonDTO.setSpdata_11(coreNfssiVerifyReasonBean.getMinLimitUnit());
    }

    private static void convertReportDtoToBean(CoreNfssiReportDTO coreNfssiReportDTO, CoreNfssiReportBean coreNfssiReportBean) {
        coreNfssiReportBean.setId(coreNfssiReportDTO.getReportNo());
        coreNfssiReportBean.setOrderId(coreNfssiReportDTO.getSampleNo());
        coreNfssiReportBean.setSuperviseReportRemark(coreNfssiReportDTO.getJdBz());
        coreNfssiReportBean.setRiskReportRemark(coreNfssiReportDTO.getFxBz());
        coreNfssiReportBean.setInspectOrgContactMobile(coreNfssiReportDTO.getContactTel());
        coreNfssiReportBean.setInspectOrgContact(coreNfssiReportDTO.getContact());
        coreNfssiReportBean.setInspectOrgContactEmail(coreNfssiReportDTO.getContactEmail());
        coreNfssiReportBean.setSealSampler(coreNfssiReportDTO.getFyPerson());
        coreNfssiReportBean.setSealSamplerEmail(coreNfssiReportDTO.getFyEmail());
        coreNfssiReportBean.setSealSamplerMobile(coreNfssiReportDTO.getFyTel());
    }

    private static void convertReportBeanToDto(CoreNfssiReportBean coreNfssiReportBean, CoreNfssiReportDTO coreNfssiReportDTO) {
        coreNfssiReportDTO.setReportNo(coreNfssiReportBean.getId());
        coreNfssiReportDTO.setSampleNo(coreNfssiReportBean.getOrderId());
        coreNfssiReportDTO.setJdBz(coreNfssiReportBean.getSuperviseReportRemark());
        coreNfssiReportDTO.setFxBz(coreNfssiReportBean.getRiskReportRemark());
        coreNfssiReportDTO.setContactTel(coreNfssiReportBean.getInspectOrgContactMobile());
        coreNfssiReportDTO.setContact(coreNfssiReportBean.getInspectOrgContact());
        coreNfssiReportDTO.setContactEmail(coreNfssiReportBean.getInspectOrgContactEmail());
        coreNfssiReportDTO.setFyPerson(coreNfssiReportBean.getSealSampler());
        coreNfssiReportDTO.setFyEmail(coreNfssiReportBean.getSealSamplerEmail());
        coreNfssiReportDTO.setFyTel(coreNfssiReportBean.getSealSamplerMobile());
    }

    private static void convertReportItemDtoToBean(CoreNfssiReportItemDTO coreNfssiReportItemDTO, CoreNfssiReportItemBean coreNfssiReportItemBean) {
        coreNfssiReportItemBean.setOrderId(coreNfssiReportItemDTO.getSampleNo());
        coreNfssiReportItemBean.setTestItem(coreNfssiReportItemDTO.getSpdata_0());
        coreNfssiReportItemBean.setTestResult(coreNfssiReportItemDTO.getSpdata_1());
        coreNfssiReportItemBean.setResultJudgment(coreNfssiReportItemDTO.getSpdata_2());
        coreNfssiReportItemBean.setInspectStandard(coreNfssiReportItemDTO.getSpdata_3());
        coreNfssiReportItemBean.setJudgeStandard(coreNfssiReportItemDTO.getSpdata_4());
        coreNfssiReportItemBean.setMethodLimit(coreNfssiReportItemDTO.getSpdata_7());
        coreNfssiReportItemBean.setDetectionLimitOrg(coreNfssiReportItemDTO.getSpdata_8());
        coreNfssiReportItemBean.setMinLimit(coreNfssiReportItemDTO.getSpdata_11());
        coreNfssiReportItemBean.setMinLimitUnit(coreNfssiReportItemDTO.getSpdata_12());
        coreNfssiReportItemBean.setMaxLimit(coreNfssiReportItemDTO.getSpdata_15());
        coreNfssiReportItemBean.setMaxLimitUnit(coreNfssiReportItemDTO.getSpdata_16());
        coreNfssiReportItemBean.setExplain(coreNfssiReportItemDTO.getSpdata_17());
        coreNfssiReportItemBean.setResultUnit(coreNfssiReportItemDTO.getSpdata_18());
        coreNfssiReportItemBean.setShortInspectStandard(coreNfssiReportItemDTO.getSpdata_19());
        coreNfssiReportItemBean.setRemark(coreNfssiReportItemDTO.getSpdata_20());
        coreNfssiReportItemBean.setOldItemName(coreNfssiReportItemDTO.getSpdata_0_old());
        coreNfssiReportItemBean.setItemCategory(coreNfssiReportItemDTO.getJylx());
        coreNfssiReportItemBean.setNecessary(coreNfssiReportItemDTO.getInReserve());
        coreNfssiReportItemBean.setFuncCategory(coreNfssiReportItemDTO.getGnlb());
        coreNfssiReportItemBean.setStatus(coreNfssiReportItemDTO.getStatus());
    }

    private static void convertReportItemBeanToDto(CoreNfssiReportItemBean coreNfssiReportItemBean, CoreNfssiReportItemDTO coreNfssiReportItemDTO) {
        coreNfssiReportItemDTO.setSampleNo(coreNfssiReportItemBean.getOrderId());
        coreNfssiReportItemDTO.setSpdata_0(coreNfssiReportItemBean.getTestItem());
        coreNfssiReportItemDTO.setSpdata_1(coreNfssiReportItemBean.getTestResult());
        coreNfssiReportItemDTO.setSpdata_2(coreNfssiReportItemBean.getResultJudgment());
        coreNfssiReportItemDTO.setSpdata_3(coreNfssiReportItemBean.getInspectStandard());
        coreNfssiReportItemDTO.setSpdata_4(coreNfssiReportItemBean.getJudgeStandard());
        coreNfssiReportItemDTO.setSpdata_7(coreNfssiReportItemBean.getMethodLimit());
        coreNfssiReportItemDTO.setSpdata_8(coreNfssiReportItemBean.getDetectionLimitOrg());
        coreNfssiReportItemDTO.setSpdata_11(coreNfssiReportItemBean.getMinLimit());
        coreNfssiReportItemDTO.setSpdata_12(coreNfssiReportItemBean.getMinLimitUnit());
        coreNfssiReportItemDTO.setSpdata_15(coreNfssiReportItemBean.getMaxLimit());
        coreNfssiReportItemDTO.setSpdata_16(coreNfssiReportItemBean.getMaxLimitUnit());
        coreNfssiReportItemDTO.setSpdata_17(coreNfssiReportItemBean.getExplain());
        coreNfssiReportItemDTO.setSpdata_18(coreNfssiReportItemBean.getResultUnit());
        coreNfssiReportItemDTO.setSpdata_19(coreNfssiReportItemBean.getShortInspectStandard());
        coreNfssiReportItemDTO.setSpdata_20(coreNfssiReportItemBean.getRemark());
        coreNfssiReportItemDTO.setSpdata_0_old(coreNfssiReportItemBean.getOldItemName());
        coreNfssiReportItemDTO.setJylx(coreNfssiReportItemBean.getItemCategory());
        coreNfssiReportItemDTO.setInReserve(coreNfssiReportItemBean.getNecessary());
        coreNfssiReportItemDTO.setGnlb(coreNfssiReportItemBean.getFuncCategory());
        coreNfssiReportItemDTO.setStatus(coreNfssiReportItemBean.getStatus());
    }
}
