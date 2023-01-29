package com.kabigon.project.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kabigon.commonKabigon.model.entity.InterfaceInfo;
import com.kabigon.commonKabigon.model.entity.UserInterfaceInfo;
import com.kabigon.project.annotation.AuthCheck;
import com.kabigon.project.common.BaseResponse;
import com.kabigon.project.common.ResultUtils;
import com.kabigon.project.constant.UserConstant;
import com.kabigon.project.mapper.UserInterfaceInfoMapper;
import com.kabigon.project.model.vo.InterfaceInfoVO;
import com.kabigon.project.service.InterfaceInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析接口信息 controller
 * @author kabigon
 * @version 2023/1/28/22:00
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisInterfaceInfoController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 分析接口调用信息
     * @return
     */
    @GetMapping("/top/interfaceInfoInvoke")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfoVO>> listAnalysisInterfaceInfo() {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        Map<Long, List<UserInterfaceInfo>> interfaceInfoObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(InterfaceInfo::getId, interfaceInfoObjMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = BeanUtil.copyProperties(interfaceInfo, InterfaceInfoVO.class);
            interfaceInfoVO.setTotalNum(interfaceInfoObjMap.get(interfaceInfo.getId()).get(0).getTotalNum());
            return interfaceInfoVO;
        }).collect(Collectors.toList());

        return ResultUtils.success(interfaceInfoVOList);

    }

}
