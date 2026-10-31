package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 属性数据
 *
 * @author zhangsy@sunwayworld.com 2019/9/10
 *
 */
@Table("T_EMC_PROPERTY_DATA")
public class EmcPropertyDataBean extends AbstractInsertable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -6847584758476265822L;

    @Id
    private Long id; // 模板编码<主键>

    private Long tmplId; // 属性模板编码

    private Long bizId; // 业务编码

    private String bizId2; //

    private String tableName; // 来源表

    private String ext1; // 字段1

    private String ext2; // 字段2

    private String ext3; // 字段3

    private String ext4; // 字段4

    private String ext5; // 字段5

    private String ext6; // 字段6

    private String ext7; // 字段7

    private String ext8; // 字段8

    private String ext9; // 字段9

    private String ext10; // 字段10

    private String ext11; // 字段11

    private String ext12; // 字段12

    private String ext13; // 字段13

    private String ext14; // 字段14

    private String ext15; // 字段15

    private String ext16; // 字段16

    private String ext17; // 字段17

    private String ext18; // 字段18

    private String ext19; // 字段19

    private String ext20; // 字段20

    private String ext21; // 字段21

    private String ext22; // 字段22

    private String ext23; // 字段23

    private String ext24; // 字段24

    private String ext25; // 字段25

    private String ext26; // 字段26

    private String ext27; // 字段27

    private String ext28; // 字段28

    private String ext29; // 字段29

    private String ext30; // 字段30

    private String ext31; // 字段31

    private String ext32; // 字段32

    private String ext33; // 字段33

    private String ext34; // 字段34

    private String ext35; // 字段35

    private String ext36; // 字段36

    private String ext37; // 字段37

    private String ext38; // 字段38

    private String ext39; // 字段39

    private String ext40; // 字段40

    private String ext41; // 字段41

    private String ext42; // 字段42

    private String ext43; // 字段43

    private String ext44; // 字段44

    private String ext45; // 字段45

    private String ext46; // 字段46

    private String ext47; // 字段47

    private String ext48; // 字段48

    private String ext49; // 字段49

    private String ext50; // 字段50

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getBizId2() {
        return bizId2;
    }

    public void setBizId2(String bizId2) {
        this.bizId2 = bizId2;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }

    public String getExt6() {
        return ext6;
    }

    public void setExt6(String ext6) {
        this.ext6 = ext6;
    }

    public String getExt7() {
        return ext7;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }

    public String getExt8() {
        return ext8;
    }

    public void setExt8(String ext8) {
        this.ext8 = ext8;
    }

    public String getExt9() {
        return ext9;
    }

    public void setExt9(String ext9) {
        this.ext9 = ext9;
    }

    public String getExt10() {
        return ext10;
    }

    public void setExt10(String ext10) {
        this.ext10 = ext10;
    }

    public String getExt11() {
        return ext11;
    }

    public void setExt11(String ext11) {
        this.ext11 = ext11;
    }

    public String getExt12() {
        return ext12;
    }

    public void setExt12(String ext12) {
        this.ext12 = ext12;
    }

    public String getExt13() {
        return ext13;
    }

    public void setExt13(String ext13) {
        this.ext13 = ext13;
    }

    public String getExt14() {
        return ext14;
    }

    public void setExt14(String ext14) {
        this.ext14 = ext14;
    }

    public String getExt15() {
        return ext15;
    }

    public void setExt15(String ext15) {
        this.ext15 = ext15;
    }

    public String getExt16() {
        return ext16;
    }

    public void setExt16(String ext16) {
        this.ext16 = ext16;
    }

    public String getExt17() {
        return ext17;
    }

    public void setExt17(String ext17) {
        this.ext17 = ext17;
    }

    public String getExt18() {
        return ext18;
    }

    public void setExt18(String ext18) {
        this.ext18 = ext18;
    }

    public String getExt19() {
        return ext19;
    }

    public void setExt19(String ext19) {
        this.ext19 = ext19;
    }

    public String getExt20() {
        return ext20;
    }

    public void setExt20(String ext20) {
        this.ext20 = ext20;
    }

    public String getExt21() {
        return ext21;
    }

    public void setExt21(String ext21) {
        this.ext21 = ext21;
    }

    public String getExt22() {
        return ext22;
    }

    public void setExt22(String ext22) {
        this.ext22 = ext22;
    }

    public String getExt23() {
        return ext23;
    }

    public void setExt23(String ext23) {
        this.ext23 = ext23;
    }

    public String getExt24() {
        return ext24;
    }

    public void setExt24(String ext24) {
        this.ext24 = ext24;
    }

    public String getExt25() {
        return ext25;
    }

    public void setExt25(String ext25) {
        this.ext25 = ext25;
    }

    public String getExt26() {
        return ext26;
    }

    public void setExt26(String ext26) {
        this.ext26 = ext26;
    }

    public String getExt27() {
        return ext27;
    }

    public void setExt27(String ext27) {
        this.ext27 = ext27;
    }

    public String getExt28() {
        return ext28;
    }

    public void setExt28(String ext28) {
        this.ext28 = ext28;
    }

    public String getExt29() {
        return ext29;
    }

    public void setExt29(String ext29) {
        this.ext29 = ext29;
    }

    public String getExt30() {
        return ext30;
    }

    public void setExt30(String ext30) {
        this.ext30 = ext30;
    }

    public String getExt31() {
        return ext31;
    }

    public void setExt31(String ext31) {
        this.ext31 = ext31;
    }

    public String getExt32() {
        return ext32;
    }

    public void setExt32(String ext32) {
        this.ext32 = ext32;
    }

    public String getExt33() {
        return ext33;
    }

    public void setExt33(String ext33) {
        this.ext33 = ext33;
    }

    public String getExt34() {
        return ext34;
    }

    public void setExt34(String ext34) {
        this.ext34 = ext34;
    }

    public String getExt35() {
        return ext35;
    }

    public void setExt35(String ext35) {
        this.ext35 = ext35;
    }

    public String getExt36() {
        return ext36;
    }

    public void setExt36(String ext36) {
        this.ext36 = ext36;
    }

    public String getExt37() {
        return ext37;
    }

    public void setExt37(String ext37) {
        this.ext37 = ext37;
    }

    public String getExt38() {
        return ext38;
    }

    public void setExt38(String ext38) {
        this.ext38 = ext38;
    }

    public String getExt39() {
        return ext39;
    }

    public void setExt39(String ext39) {
        this.ext39 = ext39;
    }

    public String getExt40() {
        return ext40;
    }

    public void setExt40(String ext40) {
        this.ext40 = ext40;
    }

    public String getExt41() {
        return ext41;
    }

    public void setExt41(String ext41) {
        this.ext41 = ext41;
    }

    public String getExt42() {
        return ext42;
    }

    public void setExt42(String ext42) {
        this.ext42 = ext42;
    }

    public String getExt43() {
        return ext43;
    }

    public void setExt43(String ext43) {
        this.ext43 = ext43;
    }

    public String getExt44() {
        return ext44;
    }

    public void setExt44(String ext44) {
        this.ext44 = ext44;
    }

    public String getExt45() {
        return ext45;
    }

    public void setExt45(String ext45) {
        this.ext45 = ext45;
    }

    public String getExt46() {
        return ext46;
    }

    public void setExt46(String ext46) {
        this.ext46 = ext46;
    }

    public String getExt47() {
        return ext47;
    }

    public void setExt47(String ext47) {
        this.ext47 = ext47;
    }

    public String getExt48() {
        return ext48;
    }

    public void setExt48(String ext48) {
        this.ext48 = ext48;
    }

    public String getExt49() {
        return ext49;
    }

    public void setExt49(String ext49) {
        this.ext49 = ext49;
    }

    public String getExt50() {
        return ext50;
    }

    public void setExt50(String ext50) {
        this.ext50 = ext50;
    }

    public Long getTmplId() {
        return tmplId;
    }

    public void setTmplId(Long tmplId) {
        this.tmplId = tmplId;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
