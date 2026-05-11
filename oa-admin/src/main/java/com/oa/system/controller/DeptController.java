package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Dept;
import com.oa.system.security.RequirePerms;
import com.oa.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @GetMapping("/tree")
    @RequirePerms("system:dept:list")
    public R tree() {
        List<Dept> tree = deptService.getDeptTree();
        return R.ok(tree);
    }

    @GetMapping("/list")
    @RequirePerms("system:dept:list")
    public R list(PageDTO<Dept> pageDTO) {
        PageR<Dept> result = deptService.getDeptPage(pageDTO);
        return R.ok(result);
    }

    @GetMapping("/all")
    @RequirePerms({"system:dept:list", "system:user:list"})
    public R getAll() {
        List<Dept> depts = deptService.getAllDepts();
        return R.ok(depts);
    }

    @GetMapping("/{id}")
    @RequirePerms("system:dept:list")
    public R getById(@PathVariable Long id) {
        Dept dept = deptService.getDeptById(id);
        return R.ok(dept);
    }

    @PostMapping
    @RequirePerms("system:dept:list")
    public R add(@RequestBody Dept dept) {
        Long id = deptService.addDept(dept);
        return R.ok(id);
    }

    @PutMapping
    @RequirePerms("system:dept:list")
    public R update(@RequestBody Dept dept) {
        deptService.updateDept(dept);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePerms("system:dept:list")
    public R delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return R.ok();
    }
}
