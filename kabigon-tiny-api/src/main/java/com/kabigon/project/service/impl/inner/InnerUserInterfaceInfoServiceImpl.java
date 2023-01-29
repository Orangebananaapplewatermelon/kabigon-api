package com.kabigon.project.service.impl.inner;

import com.kabigon.commonKabigon.model.service.InnerUserInterfaceInfoService;
import com.kabigon.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author kabigon
 * @version 2023/1/28/19:50
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceId, userId);
    }
}
