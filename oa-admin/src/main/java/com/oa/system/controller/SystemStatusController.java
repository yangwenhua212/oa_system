package com.oa.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.common.R;
import com.oa.system.entity.DictData;
import com.oa.system.mapper.DictDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统状态管理控制器
 */
@RestController
@RequestMapping("/api/system/status")
@RequiredArgsConstructor
public class SystemStatusController {

    private final DictDataMapper dictDataMapper;

    /**
     * 查询状态列表
     */
    @GetMapping("/list")
    public R list() {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DictData::getSort);
        List<DictData> list = dictDataMapper.selectList(wrapper);
        return R.ok(list);
    }

    /**
     * 新增状态
     */
    @PostMapping
    public R add(@RequestBody DictData dictData) {
        dictDataMapper.insert(dictData);
        return R.ok(dictData.getId());
    }

    /**
     * 修改状态
     */
    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody DictData dictData) {
        dictData.setId(id);
        dictDataMapper.updateById(dictData);
        return R.ok();
    }

    /**
     * 删除状态
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        dictDataMapper.deleteById(id);
        return R.ok();
    }
}
