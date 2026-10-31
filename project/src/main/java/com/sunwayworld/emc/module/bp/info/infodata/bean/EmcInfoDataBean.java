package com.sunwayworld.emc.module.bp.info.infodata.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 综合信息表
 *
 * @author qianc@sunwayworld.com 2019年11月18日
 */
@Table("T_EMC_INFO_DATA")
public class EmcInfoDataBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -2713508750160991005L;

    @Id
    private Long id;// 主键

    private Long infoConfigId;// 综合信息配置id

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate monitorDate; //监测日期

    private String rn01;
    private String rn02;
    private String rn03;
    private String rn04;
    private String rn05;
    private String rn06;
    private String rn07;
    private String rn08;
    private String rn09;
    private String rn10;
    private String rn11;
    private String rn12;
    private String rn13;
    private String rn14;
    private String rn15;
    private String rn16;
    private String rn17;
    private String rn18;
    private String rn19;
    private String rn20;
    private String rn21;
    private String rn22;
    private String rn23;
    private String rn24;
    private String rn25;
    private String rn26;
    private String rn27;
    private String rn28;
    private String rn29;
    private String rn30;
    private String rn31;
    private String rn32;
    private String rn33;
    private String rn34;
    private String rn35;
    private String rn36;
    private String rn37;
    private String rn38;
    private String rn39;
    private String rn40;
    private String rn41;
    private String rn42;
    private String rn43;
    private String rn44;
    private String rn45;
    private String rn46;
    private String rn47;
    private String rn48;
    private String rn49;
    private String rn50;
    private String rn51;
    private String rn52;
    private String rn53;
    private String rn54;
    private String rn55;
    private String rn56;
    private String rn57;
    private String rn58;
    private String rn59;
    private String rn60;
    private String rn61;
    private String rn62;
    private String rn63;
    private String rn64;
    private String rn65;
    private String rn66;
    private String rn67;
    private String rn68;
    private String rn69;
    private String rn70;
    private String rn71;
    private String rn72;
    private String rn73;
    private String rn74;
    private String rn75;
    private String rn76;
    private String rn77;
    private String rn78;
    private String rn79;
    private String rn80;
    private String rn81;
    private String rn82;
    private String rn83;
    private String rn84;
    private String rn85;
    private String rn86;
    private String rn87;
    private String rn88;
    private String rn89;
    private String rn90;
    private String rn91;
    private String rn92;
    private String rn93;
    private String rn94;
    private String rn95;
    private String rn96;
    private String rn97;
    private String rn98;
    private String rn99;
    private String rn100;
    private String rn101;
    private String rn102;
    private String rn103;
    private String rn104;
    private String rn105;
    private String rn106;
    private String rn107;
    private String rn108;
    private String rn109;
    private String rn110;
    private String rn111;
    private String rn112;
    private String rn113;
    private String rn114;
    private String rn115;
    private String rn116;
    private String rn117;
    private String rn118;
    private String rn119;
    private String rn120;
    private String rn121;
    private String rn122;
    private String rn123;
    private String rn124;
    private String rn125;
    private String rn126;
    private String rn127;
    private String rn128;
    private String rn129;
    private String rn130;

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInfoConfigId() {
        return infoConfigId;
    }

    public void setInfoConfigId(Long infoConfigId) {
        this.infoConfigId = infoConfigId;
    }

    public String getRn01() {
        return rn01;
    }

    public void setRn01(String rn01) {
        this.rn01 = rn01;
    }

    public String getRn02() {
        return rn02;
    }

    public void setRn02(String rn02) {
        this.rn02 = rn02;
    }

    public String getRn03() {
        return rn03;
    }

    public void setRn03(String rn03) {
        this.rn03 = rn03;
    }

    public String getRn04() {
        return rn04;
    }

    public void setRn04(String rn04) {
        this.rn04 = rn04;
    }

    public String getRn05() {
        return rn05;
    }

    public void setRn05(String rn05) {
        this.rn05 = rn05;
    }

    public String getRn06() {
        return rn06;
    }

    public void setRn06(String rn06) {
        this.rn06 = rn06;
    }

    public String getRn07() {
        return rn07;
    }

    public void setRn07(String rn07) {
        this.rn07 = rn07;
    }

    public String getRn08() {
        return rn08;
    }

    public void setRn08(String rn08) {
        this.rn08 = rn08;
    }

    public String getRn09() {
        return rn09;
    }

    public void setRn09(String rn09) {
        this.rn09 = rn09;
    }

    public String getRn10() {
        return rn10;
    }

    public void setRn10(String rn10) {
        this.rn10 = rn10;
    }

    public String getRn11() {
        return rn11;
    }

    public void setRn11(String rn11) {
        this.rn11 = rn11;
    }

    public String getRn12() {
        return rn12;
    }

    public void setRn12(String rn12) {
        this.rn12 = rn12;
    }

    public String getRn13() {
        return rn13;
    }

    public void setRn13(String rn13) {
        this.rn13 = rn13;
    }

    public String getRn14() {
        return rn14;
    }

    public void setRn14(String rn14) {
        this.rn14 = rn14;
    }

    public String getRn15() {
        return rn15;
    }

    public void setRn15(String rn15) {
        this.rn15 = rn15;
    }

    public String getRn16() {
        return rn16;
    }

    public void setRn16(String rn16) {
        this.rn16 = rn16;
    }

    public String getRn17() {
        return rn17;
    }

    public void setRn17(String rn17) {
        this.rn17 = rn17;
    }

    public String getRn18() {
        return rn18;
    }

    public void setRn18(String rn18) {
        this.rn18 = rn18;
    }

    public String getRn19() {
        return rn19;
    }

    public void setRn19(String rn19) {
        this.rn19 = rn19;
    }

    public String getRn20() {
        return rn20;
    }

    public void setRn20(String rn20) {
        this.rn20 = rn20;
    }

    public String getRn21() {
        return rn21;
    }

    public void setRn21(String rn21) {
        this.rn21 = rn21;
    }

    public String getRn22() {
        return rn22;
    }

    public void setRn22(String rn22) {
        this.rn22 = rn22;
    }

    public String getRn23() {
        return rn23;
    }

    public void setRn23(String rn23) {
        this.rn23 = rn23;
    }

    public String getRn24() {
        return rn24;
    }

    public void setRn24(String rn24) {
        this.rn24 = rn24;
    }

    public String getRn25() {
        return rn25;
    }

    public void setRn25(String rn25) {
        this.rn25 = rn25;
    }

    public String getRn26() {
        return rn26;
    }

    public void setRn26(String rn26) {
        this.rn26 = rn26;
    }

    public String getRn27() {
        return rn27;
    }

    public void setRn27(String rn27) {
        this.rn27 = rn27;
    }

    public String getRn28() {
        return rn28;
    }

    public void setRn28(String rn28) {
        this.rn28 = rn28;
    }

    public String getRn29() {
        return rn29;
    }

    public void setRn29(String rn29) {
        this.rn29 = rn29;
    }

    public String getRn30() {
        return rn30;
    }

    public void setRn30(String rn30) {
        this.rn30 = rn30;
    }

    public String getRn31() {
        return rn31;
    }

    public void setRn31(String rn31) {
        this.rn31 = rn31;
    }

    public String getRn32() {
        return rn32;
    }

    public void setRn32(String rn32) {
        this.rn32 = rn32;
    }

    public String getRn33() {
        return rn33;
    }

    public void setRn33(String rn33) {
        this.rn33 = rn33;
    }

    public String getRn34() {
        return rn34;
    }

    public void setRn34(String rn34) {
        this.rn34 = rn34;
    }

    public String getRn35() {
        return rn35;
    }

    public void setRn35(String rn35) {
        this.rn35 = rn35;
    }

    public String getRn36() {
        return rn36;
    }

    public void setRn36(String rn36) {
        this.rn36 = rn36;
    }

    public String getRn37() {
        return rn37;
    }

    public void setRn37(String rn37) {
        this.rn37 = rn37;
    }

    public String getRn38() {
        return rn38;
    }

    public void setRn38(String rn38) {
        this.rn38 = rn38;
    }

    public String getRn39() {
        return rn39;
    }

    public void setRn39(String rn39) {
        this.rn39 = rn39;
    }

    public String getRn40() {
        return rn40;
    }

    public void setRn40(String rn40) {
        this.rn40 = rn40;
    }

    public String getRn41() {
        return rn41;
    }

    public void setRn41(String rn41) {
        this.rn41 = rn41;
    }

    public String getRn42() {
        return rn42;
    }

    public void setRn42(String rn42) {
        this.rn42 = rn42;
    }

    public String getRn43() {
        return rn43;
    }

    public void setRn43(String rn43) {
        this.rn43 = rn43;
    }

    public String getRn44() {
        return rn44;
    }

    public void setRn44(String rn44) {
        this.rn44 = rn44;
    }

    public String getRn45() {
        return rn45;
    }

    public void setRn45(String rn45) {
        this.rn45 = rn45;
    }

    public String getRn46() {
        return rn46;
    }

    public void setRn46(String rn46) {
        this.rn46 = rn46;
    }

    public String getRn47() {
        return rn47;
    }

    public void setRn47(String rn47) {
        this.rn47 = rn47;
    }

    public String getRn48() {
        return rn48;
    }

    public void setRn48(String rn48) {
        this.rn48 = rn48;
    }

    public String getRn49() {
        return rn49;
    }

    public void setRn49(String rn49) {
        this.rn49 = rn49;
    }

    public String getRn50() {
        return rn50;
    }

    public void setRn50(String rn50) {
        this.rn50 = rn50;
    }

    public String getRn51() {
        return rn51;
    }

    public void setRn51(String rn51) {
        this.rn51 = rn51;
    }

    public String getRn52() {
        return rn52;
    }

    public void setRn52(String rn52) {
        this.rn52 = rn52;
    }

    public String getRn53() {
        return rn53;
    }

    public void setRn53(String rn53) {
        this.rn53 = rn53;
    }

    public String getRn54() {
        return rn54;
    }

    public void setRn54(String rn54) {
        this.rn54 = rn54;
    }

    public String getRn55() {
        return rn55;
    }

    public void setRn55(String rn55) {
        this.rn55 = rn55;
    }

    public String getRn56() {
        return rn56;
    }

    public void setRn56(String rn56) {
        this.rn56 = rn56;
    }

    public String getRn57() {
        return rn57;
    }

    public void setRn57(String rn57) {
        this.rn57 = rn57;
    }

    public String getRn58() {
        return rn58;
    }

    public void setRn58(String rn58) {
        this.rn58 = rn58;
    }

    public String getRn59() {
        return rn59;
    }

    public void setRn59(String rn59) {
        this.rn59 = rn59;
    }

    public String getRn60() {
        return rn60;
    }

    public void setRn60(String rn60) {
        this.rn60 = rn60;
    }

    public String getRn61() {
        return rn61;
    }

    public void setRn61(String rn61) {
        this.rn61 = rn61;
    }

    public String getRn62() {
        return rn62;
    }

    public void setRn62(String rn62) {
        this.rn62 = rn62;
    }

    public String getRn63() {
        return rn63;
    }

    public void setRn63(String rn63) {
        this.rn63 = rn63;
    }

    public String getRn64() {
        return rn64;
    }

    public void setRn64(String rn64) {
        this.rn64 = rn64;
    }

    public String getRn65() {
        return rn65;
    }

    public void setRn65(String rn65) {
        this.rn65 = rn65;
    }

    public String getRn66() {
        return rn66;
    }

    public void setRn66(String rn66) {
        this.rn66 = rn66;
    }

    public String getRn67() {
        return rn67;
    }

    public void setRn67(String rn67) {
        this.rn67 = rn67;
    }

    public String getRn68() {
        return rn68;
    }

    public void setRn68(String rn68) {
        this.rn68 = rn68;
    }

    public String getRn69() {
        return rn69;
    }

    public void setRn69(String rn69) {
        this.rn69 = rn69;
    }

    public String getRn70() {
        return rn70;
    }

    public void setRn70(String rn70) {
        this.rn70 = rn70;
    }

    public String getRn71() {
        return rn71;
    }

    public void setRn71(String rn71) {
        this.rn71 = rn71;
    }

    public String getRn72() {
        return rn72;
    }

    public void setRn72(String rn72) {
        this.rn72 = rn72;
    }

    public String getRn73() {
        return rn73;
    }

    public void setRn73(String rn73) {
        this.rn73 = rn73;
    }

    public String getRn74() {
        return rn74;
    }

    public void setRn74(String rn74) {
        this.rn74 = rn74;
    }

    public String getRn75() {
        return rn75;
    }

    public void setRn75(String rn75) {
        this.rn75 = rn75;
    }

    public String getRn76() {
        return rn76;
    }

    public void setRn76(String rn76) {
        this.rn76 = rn76;
    }

    public String getRn77() {
        return rn77;
    }

    public void setRn77(String rn77) {
        this.rn77 = rn77;
    }

    public String getRn78() {
        return rn78;
    }

    public void setRn78(String rn78) {
        this.rn78 = rn78;
    }

    public String getRn79() {
        return rn79;
    }

    public void setRn79(String rn79) {
        this.rn79 = rn79;
    }

    public String getRn80() {
        return rn80;
    }

    public void setRn80(String rn80) {
        this.rn80 = rn80;
    }

    public String getRn81() {
        return rn81;
    }

    public void setRn81(String rn81) {
        this.rn81 = rn81;
    }

    public String getRn82() {
        return rn82;
    }

    public void setRn82(String rn82) {
        this.rn82 = rn82;
    }

    public String getRn83() {
        return rn83;
    }

    public void setRn83(String rn83) {
        this.rn83 = rn83;
    }

    public String getRn84() {
        return rn84;
    }

    public void setRn84(String rn84) {
        this.rn84 = rn84;
    }

    public String getRn85() {
        return rn85;
    }

    public void setRn85(String rn85) {
        this.rn85 = rn85;
    }

    public String getRn86() {
        return rn86;
    }

    public void setRn86(String rn86) {
        this.rn86 = rn86;
    }

    public String getRn87() {
        return rn87;
    }

    public void setRn87(String rn87) {
        this.rn87 = rn87;
    }

    public String getRn88() {
        return rn88;
    }

    public void setRn88(String rn88) {
        this.rn88 = rn88;
    }

    public String getRn89() {
        return rn89;
    }

    public void setRn89(String rn89) {
        this.rn89 = rn89;
    }

    public String getRn90() {
        return rn90;
    }

    public void setRn90(String rn90) {
        this.rn90 = rn90;
    }

    public String getRn91() {
        return rn91;
    }

    public void setRn91(String rn91) {
        this.rn91 = rn91;
    }

    public String getRn92() {
        return rn92;
    }

    public void setRn92(String rn92) {
        this.rn92 = rn92;
    }

    public String getRn93() {
        return rn93;
    }

    public void setRn93(String rn93) {
        this.rn93 = rn93;
    }

    public String getRn94() {
        return rn94;
    }

    public void setRn94(String rn94) {
        this.rn94 = rn94;
    }

    public String getRn95() {
        return rn95;
    }

    public void setRn95(String rn95) {
        this.rn95 = rn95;
    }

    public String getRn96() {
        return rn96;
    }

    public void setRn96(String rn96) {
        this.rn96 = rn96;
    }

    public String getRn97() {
        return rn97;
    }

    public void setRn97(String rn97) {
        this.rn97 = rn97;
    }

    public String getRn98() {
        return rn98;
    }

    public void setRn98(String rn98) {
        this.rn98 = rn98;
    }

    public String getRn99() {
        return rn99;
    }

    public void setRn99(String rn99) {
        this.rn99 = rn99;
    }

    public String getRn100() {
        return rn100;
    }

    public void setRn100(String rn100) {
        this.rn100 = rn100;
    }

    public String getRn101() {
        return rn101;
    }

    public void setRn101(String rn101) {
        this.rn101 = rn101;
    }

    public String getRn102() {
        return rn102;
    }

    public void setRn102(String rn102) {
        this.rn102 = rn102;
    }

    public String getRn103() {
        return rn103;
    }

    public void setRn103(String rn103) {
        this.rn103 = rn103;
    }

    public String getRn104() {
        return rn104;
    }

    public void setRn104(String rn104) {
        this.rn104 = rn104;
    }

    public String getRn105() {
        return rn105;
    }

    public void setRn105(String rn105) {
        this.rn105 = rn105;
    }

    public String getRn106() {
        return rn106;
    }

    public void setRn106(String rn106) {
        this.rn106 = rn106;
    }

    public String getRn107() {
        return rn107;
    }

    public void setRn107(String rn107) {
        this.rn107 = rn107;
    }

    public String getRn108() {
        return rn108;
    }

    public void setRn108(String rn108) {
        this.rn108 = rn108;
    }

    public String getRn109() {
        return rn109;
    }

    public void setRn109(String rn109) {
        this.rn109 = rn109;
    }

    public String getRn110() {
        return rn110;
    }

    public void setRn110(String rn110) {
        this.rn110 = rn110;
    }

    public String getRn111() {
        return rn111;
    }

    public void setRn111(String rn111) {
        this.rn111 = rn111;
    }

    public String getRn112() {
        return rn112;
    }

    public void setRn112(String rn112) {
        this.rn112 = rn112;
    }

    public String getRn113() {
        return rn113;
    }

    public void setRn113(String rn113) {
        this.rn113 = rn113;
    }

    public String getRn114() {
        return rn114;
    }

    public void setRn114(String rn114) {
        this.rn114 = rn114;
    }

    public String getRn115() {
        return rn115;
    }

    public void setRn115(String rn115) {
        this.rn115 = rn115;
    }

    public String getRn116() {
        return rn116;
    }

    public void setRn116(String rn116) {
        this.rn116 = rn116;
    }

    public String getRn117() {
        return rn117;
    }

    public void setRn117(String rn117) {
        this.rn117 = rn117;
    }

    public String getRn118() {
        return rn118;
    }

    public void setRn118(String rn118) {
        this.rn118 = rn118;
    }

    public String getRn119() {
        return rn119;
    }

    public void setRn119(String rn119) {
        this.rn119 = rn119;
    }

    public String getRn120() {
        return rn120;
    }

    public void setRn120(String rn120) {
        this.rn120 = rn120;
    }

    public String getRn121() {
        return rn121;
    }

    public void setRn121(String rn121) {
        this.rn121 = rn121;
    }

    public String getRn122() {
        return rn122;
    }

    public void setRn122(String rn122) {
        this.rn122 = rn122;
    }

    public String getRn123() {
        return rn123;
    }

    public void setRn123(String rn123) {
        this.rn123 = rn123;
    }

    public String getRn124() {
        return rn124;
    }

    public void setRn124(String rn124) {
        this.rn124 = rn124;
    }

    public String getRn125() {
        return rn125;
    }

    public void setRn125(String rn125) {
        this.rn125 = rn125;
    }

    public String getRn126() {
        return rn126;
    }

    public void setRn126(String rn126) {
        this.rn126 = rn126;
    }

    public String getRn127() {
        return rn127;
    }

    public void setRn127(String rn127) {
        this.rn127 = rn127;
    }

    public String getRn128() {
        return rn128;
    }

    public void setRn128(String rn128) {
        this.rn128 = rn128;
    }

    public String getRn129() {
        return rn129;
    }

    public void setRn129(String rn129) {
        this.rn129 = rn129;
    }

    public String getRn130() {
        return rn130;
    }

    public void setRn130(String rn130) {
        this.rn130 = rn130;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public LocalDate getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(LocalDate monitorDate) {
        this.monitorDate = monitorDate;
    }

}
