package com.kabigon.commonKabigon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 接口信息
 *
 * @author kabigon
 * @version 2023/1/14/22:32
 */
@Data
@TableName(value = "interface_info")
public class InterfaceInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 接口地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 请求头
     */
    @TableField(value = "requestHeader")
    private String requestHeader;

    /**
     * 响应头
     */
    @TableField(value = "responseHeader")
    private String responseHeader;

    /**
     * 请求参数
     */
    @TableField(value = "requestParams")
    private String requestParams;
    /**
     * 接口状态[0-关闭、1-开启]
     */
    @TableField(value = "`status`")
    private Integer status;

    /**
     * 请求类型
     */
    @TableField(value = "`method`")
    private String method;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 是否删除[0-有效、1-无效]
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Byte isDelete;

    private static final long serialVersionUID = 1L;
}