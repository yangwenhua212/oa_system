package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.AddressShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通讯录分享Mapper接口
 */
@Mapper
public interface AddressShareMapper extends BaseMapper<AddressShare> {

    /**
     * 查询我分享的联系人（含联系人信息）
     */
    List<AddressShare> selectBySharedBy(@Param("sharedBy") Long sharedBy);

    /**
     * 查询分享给我的联系人（含联系人信息）
     */
    List<AddressShare> selectBySharedWith(@Param("sharedWith") Long sharedWith);
}
