package com.kabigon.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kabigon.commonKabigon.model.entity.UserInterfaceInfo;

/**
* 用户接口信息关系 service
* @version 2023/1/26/13:50
* @author kabigon
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo>{

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean isadd);

    /**
     * 调用计数
     * @param interfaceId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceId, long userId);
}
