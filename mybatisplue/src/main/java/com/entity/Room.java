package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("room")
public class Room {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("Num")
    private String num;
    private String type;
    private String status;
    private String price;
    private String people;
    private String area;
    private String details;
}
