package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@TableName("checkinlist")
public class Checkinlist {
    @TableId(type = IdType.AUTO)
    private String id;
    @TableField("checkinNum")
    private String checkinNum;
    @TableField("CName")
    private String Cname;
    @TableField("RNumber")
    private String rnumber;

    @TableField("RType")
    private String rtype;

    @TableField("checkindate")
    private LocalDate checkindate;

    @TableField("checkoutdate")
    private LocalDate checkoutdate;

    @TableField("Status")
    private String status;

    @TableField("totalamount")
    private BigDecimal totalamount;

    @TableField("CPhone")
    private String cphone;

    @TableField("remark")
    private String remark;

    @TableField("points")
    private String points;

    @TableField(exist = false)
    private String Rid;
}
