package com.oa.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Position;
import com.oa.system.entity.Dept;
import com.oa.system.mapper.PositionMapper;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.security.RequirePerms;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 职位管理控制器
 */
@RestController
@RequestMapping("/api/system/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionMapper positionMapper;
    private final DeptMapper deptMapper;

    @GetMapping("/list")
    @RequirePerms("system:position:list")
    public R list(PageDTO<Position> pageDTO) {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Position::getSort);
        List<Position> list = positionMapper.selectList(wrapper);
        list.forEach(p -> {
            if (p.getDeptId() != null) {
                Dept dept = deptMapper.selectById(p.getDeptId());
                if (dept != null) {
                    p.setDeptName(dept.getDeptName());
                }
            }
        });
        return R.ok(list);
    }

    @GetMapping("/{id}")
    @RequirePerms("system:position:list")
    public R getById(@PathVariable Long id) {
        Position position = positionMapper.selectById(id);
        return R.ok(position);
    }

    @PostMapping
    @RequirePerms("system:position:list")
    public R add(@RequestBody Position position) {
        positionMapper.insert(position);
        return R.ok(position.getId());
    }

    @PutMapping
    @RequirePerms("system:position:list")
    public R update(@RequestBody Position position) {
        positionMapper.updateById(position);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePerms("system:position:list")
    public R delete(@PathVariable Long id) {
        positionMapper.deleteById(id);
        return R.ok();
    }
}
