package com.kabigon.commonKabigon.model.service;

import com.kabigon.commonKabigon.model.entity.User;

/**
 * @author kabigon
 * @version 2023/1/28/19:23
 */
public interface InnerUserService {

    User getInvokeUser(String accessKey);

}
