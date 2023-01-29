package com.kabigon.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kabigon.commonKabigon.model.entity.InterfaceInfo;

/**
 * @author kabigon
 * @version 2023/1/14/22:32
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean isadd);
}
