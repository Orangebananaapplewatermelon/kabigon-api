package com.kabigon.commonKabigon.model.service;

/**
 * @author kabigon
 * @version 2023/1/28/19:22
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用计数
     * @param interfaceId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceId, long userId);

}
