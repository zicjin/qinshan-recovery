/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestItemDTO;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreNfssiOrderDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -3567544549225983532L;
    private String sp_s_16;
    private String sp_i_jgback;
    private String sp_s_1;
    private String sp_s_2;
    private String sp_s_3;
    private String sp_s_4;
    private String sp_s_5;
    private String sp_s_6;
    private String sp_s_7;
    private String sp_s_8;
    private String sp_s_10;
    private String sp_s_11;
    private String sp_s_12;
    private String sp_s_13;
    private String sp_s_14;
    private Double sp_n_15;
    private String sp_s_17;
    private String sp_s_18;
    private String sp_s_19;
    private String sp_s_20;
    private String sp_s_21;
    private String sp_s_23;
    private String sp_s_24;
    private String sp_s_25;
    private String sp_s_26;
    private String sp_s_27;
    private LocalDate sp_d_28;
    private String sp_n_29;
    private String sp_s_30;
    private String sp_s_33;
    private String sp_s_34;
    private String sp_s_35;
    private LocalDate sp_d_38;
    private String sp_s_39;
    private String sp_s_40;
    private String sp_s_44;
    private String sp_s_61;
    private String sp_s_62;
    private String sp_s_63;
    private String sp_s_64;
    private String sp_s_65;
    private String sp_s_68;
    private String sp_s_72;
    private String sp_s_73;
    private String sp_s_74;
    private String sp_s_75;
    private String sp_s_76;
    private LocalDate sp_d_86;
    private String sp_s_201;
    private String sp_s_202;
    private String sp_s_203;
    private String sp_s_204;
    private String sp_s_206;
    private String sp_s_208;
    private String sp_s_211;
    private String sp_s_212;
    private String sp_s_213;
    private String sp_s_215;
    private String sp_s_220;
    private String sp_s_221;
    private String sp_s_222;
    private String sp_s_2_1;
    private String sp_s_bsfl;
    private String sp_s_lxr;
    private String sp_s_qs;
    private String sp_s_qydz;
    private String sp_s_qymc;
    private String sp_s_sfjk;
    private String sp_s_sfwtsc;
    private String sp_s_tel;
    private String sp_s_wtsheng;
    private String sp_s_wtshi;
    private String sp_s_wtxian;
    private String sp_s_ycg;
    private String sp_xkz;
    private String sp_xkz_id;
    private String barcode;
    private String rainbowcode;
    private String rainbowcode_url;
    private String health_code;
    @Clob
    private String health_func_cat;
    private String sp_s_43;
    private LocalDate sp_d_46;
    private String sp_s_49;
    private String sp_s_50;
    private String sp_s_51;
    private String sp_s_85;
    private String sp_s_87;
    private String sp_s_88;
    private String sp_s_37;
    private String sp_s_41;
    private String sp_s_42;
    private String sp_s_36;
    @Clob
    private String sp_s_52;
    private String sp_s_dwwz;
    private String sp_s_wcmc;
    private String sp_s_wcyyzzh;
    private String sp_s_wcicp;
    private String sp_s_wcsheng;
    private String sp_s_wcshi;
    private String sp_s_wcxian;
    private String sp_s_wcdz;
    private String sp_s_wcwz;
    private String sp_s_wclxr;
    private String sp_s_wctel;
    private String sp_s_wcbh;
    private String is_submit;
    private String sp_s_wcdpmc;
    private String sp_d_28_name;
    private String sp_s_cycj;
    private String sp_s_wtxz;
    private String scbname;
    private String scaname;
    private String sp_i_state;
    private String sample_type;
    private LocalDateTime updated_at;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedTime;
    private List<CoreNfssiTestItemDTO> itemInfo;

    public String getSp_s_16() {
        return this.sp_s_16;
    }

    public void setSp_s_16(String sp_s_16) {
        this.sp_s_16 = sp_s_16;
    }

    public String getSp_s_1() {
        return this.sp_s_1;
    }

    public void setSp_s_1(String sp_s_1) {
        this.sp_s_1 = sp_s_1;
    }

    public String getSp_s_2() {
        return this.sp_s_2;
    }

    public void setSp_s_2(String sp_s_2) {
        this.sp_s_2 = sp_s_2;
    }

    public String getSp_s_3() {
        return this.sp_s_3;
    }

    public void setSp_s_3(String sp_s_3) {
        this.sp_s_3 = sp_s_3;
    }

    public String getSp_s_4() {
        return this.sp_s_4;
    }

    public void setSp_s_4(String sp_s_4) {
        this.sp_s_4 = sp_s_4;
    }

    public String getSp_s_5() {
        return this.sp_s_5;
    }

    public void setSp_s_5(String sp_s_5) {
        this.sp_s_5 = sp_s_5;
    }

    public String getSp_s_6() {
        return this.sp_s_6;
    }

    public void setSp_s_6(String sp_s_6) {
        this.sp_s_6 = sp_s_6;
    }

    public String getSp_s_7() {
        return this.sp_s_7;
    }

    public void setSp_s_7(String sp_s_7) {
        this.sp_s_7 = sp_s_7;
    }

    public String getSp_s_8() {
        return this.sp_s_8;
    }

    public void setSp_s_8(String sp_s_8) {
        this.sp_s_8 = sp_s_8;
    }

    public String getSp_s_10() {
        return this.sp_s_10;
    }

    public void setSp_s_10(String sp_s_10) {
        this.sp_s_10 = sp_s_10;
    }

    public String getSp_s_11() {
        return this.sp_s_11;
    }

    public void setSp_s_11(String sp_s_11) {
        this.sp_s_11 = sp_s_11;
    }

    public String getSp_s_12() {
        return this.sp_s_12;
    }

    public void setSp_s_12(String sp_s_12) {
        this.sp_s_12 = sp_s_12;
    }

    public String getSp_s_13() {
        return this.sp_s_13;
    }

    public void setSp_s_13(String sp_s_13) {
        this.sp_s_13 = sp_s_13;
    }

    public String getSp_s_14() {
        return this.sp_s_14;
    }

    public void setSp_s_14(String sp_s_14) {
        this.sp_s_14 = sp_s_14;
    }

    public Double getSp_n_15() {
        return this.sp_n_15;
    }

    public void setSp_n_15(Double sp_n_15) {
        this.sp_n_15 = sp_n_15;
    }

    public String getSp_s_17() {
        return this.sp_s_17;
    }

    public void setSp_s_17(String sp_s_17) {
        this.sp_s_17 = sp_s_17;
    }

    public String getSp_s_18() {
        return this.sp_s_18;
    }

    public void setSp_s_18(String sp_s_18) {
        this.sp_s_18 = sp_s_18;
    }

    public String getSp_s_19() {
        return this.sp_s_19;
    }

    public void setSp_s_19(String sp_s_19) {
        this.sp_s_19 = sp_s_19;
    }

    public String getSp_s_20() {
        return this.sp_s_20;
    }

    public void setSp_s_20(String sp_s_20) {
        this.sp_s_20 = sp_s_20;
    }

    public String getSp_s_21() {
        return this.sp_s_21;
    }

    public void setSp_s_21(String sp_s_21) {
        this.sp_s_21 = sp_s_21;
    }

    public String getSp_s_23() {
        return this.sp_s_23;
    }

    public void setSp_s_23(String sp_s_23) {
        this.sp_s_23 = sp_s_23;
    }

    public String getSp_s_24() {
        return this.sp_s_24;
    }

    public void setSp_s_24(String sp_s_24) {
        this.sp_s_24 = sp_s_24;
    }

    public String getSp_s_25() {
        return this.sp_s_25;
    }

    public void setSp_s_25(String sp_s_25) {
        this.sp_s_25 = sp_s_25;
    }

    public String getSp_s_26() {
        return this.sp_s_26;
    }

    public void setSp_s_26(String sp_s_26) {
        this.sp_s_26 = sp_s_26;
    }

    public String getSp_s_27() {
        return this.sp_s_27;
    }

    public void setSp_s_27(String sp_s_27) {
        this.sp_s_27 = sp_s_27;
    }

    public LocalDate getSp_d_28() {
        return this.sp_d_28;
    }

    public void setSp_d_28(LocalDate sp_d_28) {
        this.sp_d_28 = sp_d_28;
    }

    public String getSp_n_29() {
        return this.sp_n_29;
    }

    public void setSp_n_29(String sp_n_29) {
        this.sp_n_29 = sp_n_29;
    }

    public String getSp_s_30() {
        return this.sp_s_30;
    }

    public void setSp_s_30(String sp_s_30) {
        this.sp_s_30 = sp_s_30;
    }

    public String getSp_s_33() {
        return this.sp_s_33;
    }

    public void setSp_s_33(String sp_s_33) {
        this.sp_s_33 = sp_s_33;
    }

    public String getSp_s_34() {
        return this.sp_s_34;
    }

    public void setSp_s_34(String sp_s_34) {
        this.sp_s_34 = sp_s_34;
    }

    public String getSp_s_35() {
        return this.sp_s_35;
    }

    public void setSp_s_35(String sp_s_35) {
        this.sp_s_35 = sp_s_35;
    }

    public LocalDate getSp_d_38() {
        return this.sp_d_38;
    }

    public void setSp_d_38(LocalDate sp_d_38) {
        this.sp_d_38 = sp_d_38;
    }

    public String getSp_s_39() {
        return this.sp_s_39;
    }

    public void setSp_s_39(String sp_s_39) {
        this.sp_s_39 = sp_s_39;
    }

    public String getSp_s_40() {
        return this.sp_s_40;
    }

    public void setSp_s_40(String sp_s_40) {
        this.sp_s_40 = sp_s_40;
    }

    public String getSp_s_44() {
        return this.sp_s_44;
    }

    public void setSp_s_44(String sp_s_44) {
        this.sp_s_44 = sp_s_44;
    }

    public String getSp_s_61() {
        return this.sp_s_61;
    }

    public void setSp_s_61(String sp_s_61) {
        this.sp_s_61 = sp_s_61;
    }

    public String getSp_s_62() {
        return this.sp_s_62;
    }

    public void setSp_s_62(String sp_s_62) {
        this.sp_s_62 = sp_s_62;
    }

    public String getSp_s_63() {
        return this.sp_s_63;
    }

    public void setSp_s_63(String sp_s_63) {
        this.sp_s_63 = sp_s_63;
    }

    public String getSp_s_64() {
        return this.sp_s_64;
    }

    public void setSp_s_64(String sp_s_64) {
        this.sp_s_64 = sp_s_64;
    }

    public String getSp_s_65() {
        return this.sp_s_65;
    }

    public void setSp_s_65(String sp_s_65) {
        this.sp_s_65 = sp_s_65;
    }

    public String getSp_s_68() {
        return this.sp_s_68;
    }

    public void setSp_s_68(String sp_s_68) {
        this.sp_s_68 = sp_s_68;
    }

    public String getSp_s_72() {
        return this.sp_s_72;
    }

    public void setSp_s_72(String sp_s_72) {
        this.sp_s_72 = sp_s_72;
    }

    public String getSp_s_73() {
        return this.sp_s_73;
    }

    public void setSp_s_73(String sp_s_73) {
        this.sp_s_73 = sp_s_73;
    }

    public String getSp_s_74() {
        return this.sp_s_74;
    }

    public void setSp_s_74(String sp_s_74) {
        this.sp_s_74 = sp_s_74;
    }

    public String getSp_s_75() {
        return this.sp_s_75;
    }

    public void setSp_s_75(String sp_s_75) {
        this.sp_s_75 = sp_s_75;
    }

    public String getSp_s_76() {
        return this.sp_s_76;
    }

    public void setSp_s_76(String sp_s_76) {
        this.sp_s_76 = sp_s_76;
    }

    public LocalDate getSp_d_86() {
        return this.sp_d_86;
    }

    public void setSp_d_86(LocalDate sp_d_86) {
        this.sp_d_86 = sp_d_86;
    }

    public String getSp_s_201() {
        return this.sp_s_201;
    }

    public void setSp_s_201(String sp_s_201) {
        this.sp_s_201 = sp_s_201;
    }

    public String getSp_s_202() {
        return this.sp_s_202;
    }

    public void setSp_s_202(String sp_s_202) {
        this.sp_s_202 = sp_s_202;
    }

    public String getSp_s_203() {
        return this.sp_s_203;
    }

    public void setSp_s_203(String sp_s_203) {
        this.sp_s_203 = sp_s_203;
    }

    public String getSp_s_204() {
        return this.sp_s_204;
    }

    public void setSp_s_204(String sp_s_204) {
        this.sp_s_204 = sp_s_204;
    }

    public String getSp_s_206() {
        return this.sp_s_206;
    }

    public void setSp_s_206(String sp_s_206) {
        this.sp_s_206 = sp_s_206;
    }

    public String getSp_s_208() {
        return this.sp_s_208;
    }

    public void setSp_s_208(String sp_s_208) {
        this.sp_s_208 = sp_s_208;
    }

    public String getSp_s_211() {
        return this.sp_s_211;
    }

    public void setSp_s_211(String sp_s_211) {
        this.sp_s_211 = sp_s_211;
    }

    public String getSp_s_212() {
        return this.sp_s_212;
    }

    public void setSp_s_212(String sp_s_212) {
        this.sp_s_212 = sp_s_212;
    }

    public String getSp_s_213() {
        return this.sp_s_213;
    }

    public void setSp_s_213(String sp_s_213) {
        this.sp_s_213 = sp_s_213;
    }

    public String getSp_s_215() {
        return this.sp_s_215;
    }

    public void setSp_s_215(String sp_s_215) {
        this.sp_s_215 = sp_s_215;
    }

    public String getSp_s_220() {
        return this.sp_s_220;
    }

    public void setSp_s_220(String sp_s_220) {
        this.sp_s_220 = sp_s_220;
    }

    public String getSp_s_221() {
        return this.sp_s_221;
    }

    public void setSp_s_221(String sp_s_221) {
        this.sp_s_221 = sp_s_221;
    }

    public String getSp_s_222() {
        return this.sp_s_222;
    }

    public void setSp_s_222(String sp_s_222) {
        this.sp_s_222 = sp_s_222;
    }

    public String getSp_s_2_1() {
        return this.sp_s_2_1;
    }

    public void setSp_s_2_1(String sp_s_2_1) {
        this.sp_s_2_1 = sp_s_2_1;
    }

    public String getSp_s_bsfl() {
        return this.sp_s_bsfl;
    }

    public void setSp_s_bsfl(String sp_s_bsfl) {
        this.sp_s_bsfl = sp_s_bsfl;
    }

    public String getSp_s_lxr() {
        return this.sp_s_lxr;
    }

    public void setSp_s_lxr(String sp_s_lxr) {
        this.sp_s_lxr = sp_s_lxr;
    }

    public String getSp_s_qs() {
        return this.sp_s_qs;
    }

    public void setSp_s_qs(String sp_s_qs) {
        this.sp_s_qs = sp_s_qs;
    }

    public String getSp_s_qydz() {
        return this.sp_s_qydz;
    }

    public void setSp_s_qydz(String sp_s_qydz) {
        this.sp_s_qydz = sp_s_qydz;
    }

    public String getSp_s_qymc() {
        return this.sp_s_qymc;
    }

    public void setSp_s_qymc(String sp_s_qymc) {
        this.sp_s_qymc = sp_s_qymc;
    }

    public String getSp_s_sfjk() {
        return this.sp_s_sfjk;
    }

    public void setSp_s_sfjk(String sp_s_sfjk) {
        this.sp_s_sfjk = sp_s_sfjk;
    }

    public String getSp_s_sfwtsc() {
        return this.sp_s_sfwtsc;
    }

    public void setSp_s_sfwtsc(String sp_s_sfwtsc) {
        this.sp_s_sfwtsc = sp_s_sfwtsc;
    }

    public String getSp_s_tel() {
        return this.sp_s_tel;
    }

    public void setSp_s_tel(String sp_s_tel) {
        this.sp_s_tel = sp_s_tel;
    }

    public String getSp_s_wtsheng() {
        return this.sp_s_wtsheng;
    }

    public void setSp_s_wtsheng(String sp_s_wtsheng) {
        this.sp_s_wtsheng = sp_s_wtsheng;
    }

    public String getSp_s_wtshi() {
        return this.sp_s_wtshi;
    }

    public void setSp_s_wtshi(String sp_s_wtshi) {
        this.sp_s_wtshi = sp_s_wtshi;
    }

    public String getSp_s_wtxian() {
        return this.sp_s_wtxian;
    }

    public void setSp_s_wtxian(String sp_s_wtxian) {
        this.sp_s_wtxian = sp_s_wtxian;
    }

    public String getSp_s_ycg() {
        return this.sp_s_ycg;
    }

    public void setSp_s_ycg(String sp_s_ycg) {
        this.sp_s_ycg = sp_s_ycg;
    }

    public String getSp_xkz() {
        return this.sp_xkz;
    }

    public void setSp_xkz(String sp_xkz) {
        this.sp_xkz = sp_xkz;
    }

    public String getSp_xkz_id() {
        return this.sp_xkz_id;
    }

    public void setSp_xkz_id(String sp_xkz_id) {
        this.sp_xkz_id = sp_xkz_id;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getRainbowcode() {
        return this.rainbowcode;
    }

    public void setRainbowcode(String rainbowcode) {
        this.rainbowcode = rainbowcode;
    }

    public String getRainbowcode_url() {
        return this.rainbowcode_url;
    }

    public void setRainbowcode_url(String rainbowcode_url) {
        this.rainbowcode_url = rainbowcode_url;
    }

    public String getHealth_code() {
        return this.health_code;
    }

    public void setHealth_code(String health_code) {
        this.health_code = health_code;
    }

    public String getHealth_func_cat() {
        return this.health_func_cat;
    }

    public void setHealth_func_cat(String health_func_cat) {
        this.health_func_cat = health_func_cat;
    }

    public String getSp_s_43() {
        return this.sp_s_43;
    }

    public void setSp_s_43(String sp_s_43) {
        this.sp_s_43 = sp_s_43;
    }

    public LocalDate getSp_d_46() {
        return this.sp_d_46;
    }

    public void setSp_d_46(LocalDate sp_d_46) {
        this.sp_d_46 = sp_d_46;
    }

    public String getSp_s_49() {
        return this.sp_s_49;
    }

    public void setSp_s_49(String sp_s_49) {
        this.sp_s_49 = sp_s_49;
    }

    public String getSp_s_50() {
        return this.sp_s_50;
    }

    public void setSp_s_50(String sp_s_50) {
        this.sp_s_50 = sp_s_50;
    }

    public String getSp_s_51() {
        return this.sp_s_51;
    }

    public void setSp_s_51(String sp_s_51) {
        this.sp_s_51 = sp_s_51;
    }

    public String getSp_s_85() {
        return this.sp_s_85;
    }

    public void setSp_s_85(String sp_s_85) {
        this.sp_s_85 = sp_s_85;
    }

    public String getSp_s_87() {
        return this.sp_s_87;
    }

    public void setSp_s_87(String sp_s_87) {
        this.sp_s_87 = sp_s_87;
    }

    public String getSp_s_88() {
        return this.sp_s_88;
    }

    public void setSp_s_88(String sp_s_88) {
        this.sp_s_88 = sp_s_88;
    }

    public String getSp_s_37() {
        return this.sp_s_37;
    }

    public void setSp_s_37(String sp_s_37) {
        this.sp_s_37 = sp_s_37;
    }

    public String getSp_s_41() {
        return this.sp_s_41;
    }

    public void setSp_s_41(String sp_s_41) {
        this.sp_s_41 = sp_s_41;
    }

    public String getSp_s_42() {
        return this.sp_s_42;
    }

    public void setSp_s_42(String sp_s_42) {
        this.sp_s_42 = sp_s_42;
    }

    public String getSp_s_36() {
        return this.sp_s_36;
    }

    public void setSp_s_36(String sp_s_36) {
        this.sp_s_36 = sp_s_36;
    }

    public String getSp_s_52() {
        return this.sp_s_52;
    }

    public void setSp_s_52(String sp_s_52) {
        this.sp_s_52 = sp_s_52;
    }

    public String getSp_s_dwwz() {
        return this.sp_s_dwwz;
    }

    public void setSp_s_dwwz(String sp_s_dwwz) {
        this.sp_s_dwwz = sp_s_dwwz;
    }

    public String getSp_s_wcmc() {
        return this.sp_s_wcmc;
    }

    public void setSp_s_wcmc(String sp_s_wcmc) {
        this.sp_s_wcmc = sp_s_wcmc;
    }

    public String getSp_s_wcyyzzh() {
        return this.sp_s_wcyyzzh;
    }

    public void setSp_s_wcyyzzh(String sp_s_wcyyzzh) {
        this.sp_s_wcyyzzh = sp_s_wcyyzzh;
    }

    public String getSp_s_wcicp() {
        return this.sp_s_wcicp;
    }

    public void setSp_s_wcicp(String sp_s_wcicp) {
        this.sp_s_wcicp = sp_s_wcicp;
    }

    public String getSp_s_wcsheng() {
        return this.sp_s_wcsheng;
    }

    public void setSp_s_wcsheng(String sp_s_wcsheng) {
        this.sp_s_wcsheng = sp_s_wcsheng;
    }

    public String getSp_s_wcshi() {
        return this.sp_s_wcshi;
    }

    public void setSp_s_wcshi(String sp_s_wcshi) {
        this.sp_s_wcshi = sp_s_wcshi;
    }

    public String getSp_s_wcxian() {
        return this.sp_s_wcxian;
    }

    public void setSp_s_wcxian(String sp_s_wcxian) {
        this.sp_s_wcxian = sp_s_wcxian;
    }

    public String getSp_s_wcdz() {
        return this.sp_s_wcdz;
    }

    public void setSp_s_wcdz(String sp_s_wcdz) {
        this.sp_s_wcdz = sp_s_wcdz;
    }

    public String getSp_s_wcwz() {
        return this.sp_s_wcwz;
    }

    public void setSp_s_wcwz(String sp_s_wcwz) {
        this.sp_s_wcwz = sp_s_wcwz;
    }

    public String getSp_s_wclxr() {
        return this.sp_s_wclxr;
    }

    public void setSp_s_wclxr(String sp_s_wclxr) {
        this.sp_s_wclxr = sp_s_wclxr;
    }

    public String getSp_s_wctel() {
        return this.sp_s_wctel;
    }

    public void setSp_s_wctel(String sp_s_wctel) {
        this.sp_s_wctel = sp_s_wctel;
    }

    public String getSp_s_wcbh() {
        return this.sp_s_wcbh;
    }

    public void setSp_s_wcbh(String sp_s_wcbh) {
        this.sp_s_wcbh = sp_s_wcbh;
    }

    public String getIs_submit() {
        return this.is_submit;
    }

    public void setIs_submit(String is_submit) {
        this.is_submit = is_submit;
    }

    public String getSp_s_wcdpmc() {
        return this.sp_s_wcdpmc;
    }

    public void setSp_s_wcdpmc(String sp_s_wcdpmc) {
        this.sp_s_wcdpmc = sp_s_wcdpmc;
    }

    public String getSp_d_28_name() {
        return this.sp_d_28_name;
    }

    public void setSp_d_28_name(String sp_d_28_name) {
        this.sp_d_28_name = sp_d_28_name;
    }

    public String getSp_s_cycj() {
        return this.sp_s_cycj;
    }

    public void setSp_s_cycj(String sp_s_cycj) {
        this.sp_s_cycj = sp_s_cycj;
    }

    public String getSp_s_wtxz() {
        return this.sp_s_wtxz;
    }

    public void setSp_s_wtxz(String sp_s_wtxz) {
        this.sp_s_wtxz = sp_s_wtxz;
    }

    public String getScbname() {
        return this.scbname;
    }

    public void setScbname(String scbname) {
        this.scbname = scbname;
    }

    public String getScaname() {
        return this.scaname;
    }

    public void setScaname(String scaname) {
        this.scaname = scaname;
    }

    public String getSp_i_state() {
        return this.sp_i_state;
    }

    public void setSp_i_state(String sp_i_state) {
        this.sp_i_state = sp_i_state;
    }

    public String getSample_type() {
        return this.sample_type;
    }

    public void setSample_type(String sample_type) {
        this.sample_type = sample_type;
    }

    public LocalDateTime getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public LocalDateTime getLastUpdatedTime() {
        return this.lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public List<CoreNfssiTestItemDTO> getItemInfo() {
        return this.itemInfo;
    }

    public void setItemInfo(List<CoreNfssiTestItemDTO> itemInfo) {
        this.itemInfo = itemInfo;
    }

    public String getSp_i_jgback() {
        return this.sp_i_jgback;
    }

    public void setSp_i_jgback(String sp_i_jgback) {
        this.sp_i_jgback = sp_i_jgback;
    }
}
