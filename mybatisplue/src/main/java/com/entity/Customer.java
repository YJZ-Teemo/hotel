package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String password;
    private String phone;
    private String cardlevel;
    private String points;
    private String hobby;
    private String time;
}
