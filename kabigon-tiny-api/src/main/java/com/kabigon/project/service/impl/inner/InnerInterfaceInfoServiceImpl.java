package com.kabigon.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kabigon.commonKabigon.model.entity.InterfaceInfo;
import com.kabigon.commonKabigon.model.service.InnerInterfaceInfoService;
import com.kabigon.project.common.ErrorCode;
import com.kabigon.project.exception.BusinessException;
import com.kabigon.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author kabigon
 * @version 2023/1/28/19:58
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(InterfaceInfo::getUrl, url)
                .ge(InterfaceInfo::getMethod, method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
