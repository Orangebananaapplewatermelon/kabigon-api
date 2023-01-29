package com.kabigon.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabigon.commonKabigon.model.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* 用户接口信息关系 mapper
* @version 2023/1/26/13:50
* @author kabigon
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(@Param("num") int num);
}