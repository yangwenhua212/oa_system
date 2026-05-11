package com.oa.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.common.R;
import com.oa.system.entity.DictType;
import com.oa.system.entity.DictData;
import com.oa.system.mapper.DictTypeMapper;
import com.oa.system.mapper.DictDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统类型管理控制器
 */
@RestController
@RequestMapping("/api/system/type")
@RequiredArgsConstructor
public class SystemTypeController {

    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;

    /**
     * 查询类型列表
     */
    @GetMapping("/list")
    public R list() {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DictType::getId);
        List<DictType> list = dictTypeMapper.selectList(wrapper);
        return R.ok(list);
    }

    /**
     * 新增类型
     */
    @PostMapping
    public R add(@RequestBody DictType dictType) {
        dictTypeMapper.insert(dictType);
        return R.ok(dictType.getId());
    }

    /**
     * 修改类型
     */
    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody DictType dictType) {
        dictType.setId(id);
        dictTypeMapper.updateById(dictType);
        return R.ok();
    }

    /**
     * 删除类型
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        dictTypeMapper.deleteById(id);
        return R.ok();
    }
}
