package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.AddressBook;
import com.oa.system.entity.AddressGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通讯录Mapper接口
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

    /**
     * 查询通讯录列表
     */
    List<AddressBook> selectAddressBookList(AddressBook addressBook);

    /**
     * 查询我的通讯录
     */
    List<AddressBook> selectMyAddressBookList(@Param("userId") Long userId);

    /**
     * 查询通讯录分组
     */
    List<AddressGroup> selectAddressGroupList(@Param("userId") Long userId);
}
