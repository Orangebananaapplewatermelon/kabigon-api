package com.kabigon.project.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.kabigon.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询请求
 *
 * @author xu.biao
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态[0-关闭、1-开启]
     */
    @TableField(value = "`status`")
    private Byte status;

    /**
     * 请求类型
     */
    @TableField(value = "`method`")
    private String method;

    private static final long serialVersionUID = 1L;
}