package com.kabigon.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kabigon.commonKabigon.model.entity.UserInterfaceInfo;
import com.kabigon.project.common.ErrorCode;
import com.kabigon.project.exception.BusinessException;
import com.kabigon.project.mapper.UserInterfaceInfoMapper;
import com.kabigon.project.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;
/**
* 用户接口信息关系 impl
* @version 2023/1/26/13:50
* @author kabigon
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo> implements UserInterfaceInfoService{

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean isadd) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，所有参数必须非空
        if (isadd) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或哦用户不存在");
            }
        }

        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数小于 0");
        }
    }

    @Override
    public boolean invokeCount(long interfaceId, long userId) {
        //判断
        if (interfaceId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(UserInterfaceInfo::getInterfaceInfoId, interfaceId)
                .eq(UserInterfaceInfo::getUserId, userId)
                .ge(UserInterfaceInfo::getLeftNum, 0)
                .setSql("totalNum = totalNum + 1, leftNum = leftNum - 1");

        return this.update(updateWrapper);

    }

}
