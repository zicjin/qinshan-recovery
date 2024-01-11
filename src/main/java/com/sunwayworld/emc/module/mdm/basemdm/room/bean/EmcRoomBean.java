package com.sunwayworld.emc.module.mdm.basemdm.room.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 房间管理
 * @author zhengxy@sunwayworld.com 2020年07月03日
 */
@Table("T_EMC_ROOM")
public class EmcRoomBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -8116752116236100590L;

    @Id
    private Long id;  // 主键
    private String roomNo;  // 房间号
    private String standardTemperature;  // 温度标准值
    private String standardHumidity;  // 湿度标准值
    private String thermometerNo; // 干湿温度计编号

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getStandardTemperature() {
        return standardTemperature;
    }

    public void setStandardTemperature(String standardTemperature) {
        this.standardTemperature = standardTemperature;
    }

    public String getStandardHumidity() {
        return standardHumidity;
    }

    public void setStandardHumidity(String standardHumidity) {
        this.standardHumidity = standardHumidity;
    }

    public String getThermometerNo() {
        return thermometerNo;
    }

    public void setThermometerNo(String thermometerNo) {
        this.thermometerNo = thermometerNo;
    }
}
