package com.kabigon.project.model.vo;

import com.kabigon.commonKabigon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kabigon
 * @version 2023/1/14/23:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoVO extends InterfaceInfo {

    private Integer totalNum;

}
