package com.kabigon.commonKabigon.model.service;

import com.kabigon.commonKabigon.model.entity.InterfaceInfo;

/**
 * @author kabigon
 * @version 2023/1/28/19:23
 */
public interface InnerInterfaceInfoService {

    /**
     * 获取接口信息
     * @param url
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String url, String method);

}
