package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.DictData;
import com.oa.system.entity.DictType;
import com.oa.system.mapper.DictDataMapper;
import com.oa.system.mapper.DictTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 */
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;

    /**
     * 分页查询字典类型
     */
    @GetMapping("/type/list")
    public R typeList(PageDTO<DictType> pageDTO) {
        PageR<DictType> result = new PageR<>();
        List<DictType> list = dictTypeMapper.selectDictTypeList(pageDTO.getParams());
        result.setTotal((long) list.size());
        result.setRecords(list);
        return R.ok(result);
    }

    /**
     * 获取所有字典类型
     */
    @GetMapping("/type/all")
    public R typeAll() {
        List<DictType> list = dictTypeMapper.selectDictTypeList(new DictType());
        return R.ok(list);
    }

    /**
     * 新增字典类型
     */
    @PostMapping("/type")
    public R addType(@RequestBody DictType dictType) {
        dictTypeMapper.insert(dictType);
        return R.ok(dictType.getId());
    }

    /**
     * 修改字典类型
     */
    @PutMapping("/type")
    public R updateType(@RequestBody DictType dictType) {
        dictTypeMapper.updateById(dictType);
        return R.ok();
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/type/{id}")
    public R deleteType(@PathVariable Long id) {
        dictTypeMapper.deleteById(id);
        return R.ok();
    }

    /**
     * 查询字典数据列表
     */
    @GetMapping("/data/list")
    public R dataList(DictData dictData) {
        List<DictData> list = dictDataMapper.selectDictDataList(dictData);
        return R.ok(list);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @GetMapping("/data/type/{dictType}")
    public R dataByType(@PathVariable String dictType) {
        List<DictData> list = dictDataMapper.selectDictDataByType(dictType);
        return R.ok(list);
    }

    /**
     * 新增字典数据
     */
    @PostMapping("/data")
    public R addData(@RequestBody DictData dictData) {
        dictDataMapper.insert(dictData);
        return R.ok(dictData.getId());
    }

    /**
     * 修改字典数据
     */
    @PutMapping("/data")
    public R updateData(@RequestBody DictData dictData) {
        dictDataMapper.updateById(dictData);
        return R.ok();
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/data/{id}")
    public R deleteData(@PathVariable Long id) {
        dictDataMapper.deleteById(id);
        return R.ok();
    }
}
