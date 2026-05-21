package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("inventory")
public class Inventory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("Num")
    private String num;
    @TableField("Name")
    private String name;
    @TableField("Type")
    private String type;
    @TableField("Quantity")
    private String quantity;
    @TableField("unit")
    private String unit;

}
