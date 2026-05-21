package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("reserveinfo")
public class Reserveinfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("ReserveId")
    private String reserveId;
    @TableField("CName")
    private String Cname;
    @TableField("RoomType")
    private String RoomType;
    @TableField("Price")
    private String price;
    @TableField("CheckIn")
    private String checkIn;
    @TableField("CheckOut")
    private String checkOut;
    @TableField("ReservationDate")
    private String ReservationDate;
    @TableField("points")
    private String points;
    @TableField("State")
    private String state;
    @TableField("Remark")
    private String remark;
    @TableField(exist = false)
    private String phone;
    @TableField(exist = false)
    private String cardlevel;
}
