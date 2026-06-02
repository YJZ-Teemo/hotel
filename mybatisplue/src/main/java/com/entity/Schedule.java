package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("cleantask")
public class Schedule {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("RNumber")
    private String RNumber;

    @TableField("RoomType")
    private String RoomType;

    @TableField("CleanType")
    private String CleanType;

    @TableField("Urgent")
    private String Urgent;

    @TableField("EstimateMin")
    private Integer EstimateMin;

    @TableField("Remark")
    private String Remark;

    @TableField("Assignee")
    private String Assignee;

    @TableField("AssignTime")
    private String AssignTime;

    @TableField("Status")
    private String Status;

    @TableField("CompletionTime")
    private String CompletionTime;

    @TableField("CreateTime")
    private String CreateTime;
}
