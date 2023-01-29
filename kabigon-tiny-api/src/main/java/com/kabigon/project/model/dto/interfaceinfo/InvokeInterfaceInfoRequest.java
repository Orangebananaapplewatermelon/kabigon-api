package com.kabigon.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 调用请求接口
 */
@Data
public class InvokeInterfaceInfoRequest implements Serializable {

    private Long id;

    /**
     * 请求参数
     */
    private String requestParams;

    private static final long serialVersionUID = 1L;
}