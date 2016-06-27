package com.frank.soong.jaws.qunar.mappers;

import com.frank.soong.jaws.qunar.bean.QunarInfoList;

public interface QunarInfoListMapper {
    int insert(QunarInfoList record);

    int insertSelective(QunarInfoList record);
}