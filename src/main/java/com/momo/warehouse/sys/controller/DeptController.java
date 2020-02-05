package com.momo.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.momo.warehouse.sys.bean.Dept;
import com.momo.warehouse.sys.bean.Notice;
import com.momo.warehouse.sys.common.TreeNode.TreeNode;
import com.momo.warehouse.sys.common.json.DataGridView;
import com.momo.warehouse.sys.common.json.ResultObj;
import com.momo.warehouse.sys.service.DeptService;
import com.momo.warehouse.sys.vo.DeptVo;
import com.momo.warehouse.sys.vo.NoticeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.ObjDoubleConsumer;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author morning
 * @since 2020-01-09
 */
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 加载部门管理左边的部门树的json
     */
    @RequestMapping("/loadDeptManagerLeftTreeJson")
    public DataGridView loadDeptManagerLeftTreeJson(DeptVo deptVo){
        //根据排序码进行左边部门树的排序
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("ordernum");
        List<Dept> list = deptService.list(queryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Dept dept : list){
            Boolean spread = dept.getOpen()==1?true:false;
            treeNodes.add(new TreeNode(dept.getId(),dept.getPid(),dept.getTitle(),spread));
        }
        return new DataGridView(treeNodes);
    }

    /**
     * 模糊查询以及全查询与分页
     * @param
     * @return
     */
    @RequestMapping("/loadAllDept")
    public DataGridView loadAllDept(DeptVo deptVo){
        IPage<Dept> page = new Page<>(deptVo.getPage(), deptVo.getLimit());
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(deptVo.getTitle()),"title",deptVo.getTitle());
        queryWrapper.like(StringUtils.isNotBlank(deptVo.getAddress()),"address",deptVo.getAddress());
        queryWrapper.like(StringUtils.isNotBlank(deptVo.getRemark()),"remark",deptVo.getRemark());
        queryWrapper.eq(deptVo.getId()!=null,"id",deptVo.getId()).or().eq(deptVo.getId()!=null,"pid",deptVo.getId());
        queryWrapper.orderByAsc("ordernum");
        deptService.page(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     *加载最大的排序码
     */
    @RequestMapping("loadDeptMaxOrderNum")
    public Map<String, Object> loadDeptMaxOrderNum(){
        Map<String, Object> map = new HashMap<>();
        int count = deptService.findCount("ordernum");
        if(count > 0){
            map.put("value",count+1);
        }else {
            map.put("value",1);
        }
        return map;
    }

    /**
     *添加部门
     */
    @RequestMapping("/addDept")
    public ResultObj addDept(DeptVo deptVo){
        try{
            deptVo.setCreatetime(new Date());
            deptService.save(deptVo);
            return ResultObj.ADD_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     *修改部门
     */
    @RequestMapping("/updateDept")
    public ResultObj updateDept(DeptVo deptVo){
        try{   //只会更新非空的字段
            if(deptVo.getId()==1) {
                deptVo.setPid(0);
            }
            deptService.updateById(deptVo);
            return ResultObj.UPDATE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 查询当前的ID的部门有没有子部门
     */
    @RequestMapping("/checkDeptHasChildrenNode")
    public Map<String,Object> checkDeptHasChildrenNode(DeptVo deptVo){
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(deptVo.getId()!=null,"pid",deptVo.getId());
        List<Dept> list = deptService.list(queryWrapper);
        if (list.size()>0){
            map.put("value",true);
        }else {
            map.put("value",false);
        }
        return map;
    }

    /**
     * 删除子部门
     */
    @RequestMapping("/deleteDept")
    public ResultObj deleteDept(DeptVo deptVo){
        try{
            deptService.removeById(deptVo.getId());
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}

