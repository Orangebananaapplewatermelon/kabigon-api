package com.kabigon.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kabigon.commonKabigon.model.entity.User;
import com.kabigon.commonKabigon.model.service.InnerUserService;
import com.kabigon.project.common.ErrorCode;
import com.kabigon.project.exception.BusinessException;
import com.kabigon.project.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author kabigon
 * @version 2023/1/28/20:03
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getAccessKey, accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
