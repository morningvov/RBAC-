package com.momo.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.momo.warehouse.sys.bean.Permission;
import com.momo.warehouse.sys.bean.Role;
import com.momo.warehouse.sys.bean.User;
import com.momo.warehouse.sys.common.Constast;
import com.momo.warehouse.sys.common.TreeNode.TreeNode;
import com.momo.warehouse.sys.common.WebUtils;
import com.momo.warehouse.sys.common.json.DataGridView;
import com.momo.warehouse.sys.common.json.ResultObj;
import com.momo.warehouse.sys.service.PermissionService;
import com.momo.warehouse.sys.service.RoleService;
import com.momo.warehouse.sys.vo.RoleVo;
import org.antlr.v4.runtime.InterpreterRuleContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.tree.Tree;

import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author morning
 * @since 2020-01-13
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询所有role
     * @param roleVo
     * @return
     */
    @RequestMapping("/loadAllRole")
    public DataGridView loadAllRole(RoleVo roleVo){
        IPage<Role> page = new Page<>(roleVo.getPage(), roleVo.getLimit());
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getName()),"name",roleVo.getName());
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getRemark()),"remark",roleVo.getRemark());
        queryWrapper.eq(roleVo.getAvailable()!=null,"available",roleVo.getAvailable());
        queryWrapper.orderByDesc("createtime");
        roleService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 添加
     */
    @RequestMapping("/addRole")
    public ResultObj addRole(RoleVo roleVo){
        try {
            roleVo.setCreatetime(new Date());
            roleService.save(roleVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_SUCCESS;
        }
    }
    /**
     * 更新
     */
    @RequestMapping("/updateRole")
    public ResultObj updateRole(RoleVo roleVo){
        try {
            roleService.updateById(roleVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除(删除角色的同时要删除两张关联表中的相关数据)
     */
    @RequestMapping("/deleteRole")
    public ResultObj deleteRole(Integer id){
        try {
            roleService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_SUCCESS;
        }
    }

    /**
     * 根据角色ID加载菜单和权限的数的json串
     */
    @RequestMapping("/initPermissionByRoleId")
    public DataGridView initPermissionByRoleId(Integer roleId){
        //查询所有可用的菜单和权限
        QueryWrapper<Permission> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Permission> allPermissions = permissionService.list(queryWrapper);
        /**
         * 不连表查询
         * 1.根据角色ID查询当前角色拥有的权限或菜单ID
         * 2.再根据查询出的权限或菜单ID查询权限和菜单的数据
         */
        List<Permission> carrentPermissions = null;
        List<Integer> currentRolePermissions = roleService.queryRolePermissionIdsByRid(roleId);
        if(currentRolePermissions.size()>0){    //如果有权限或菜单ID的时候才查询
            queryWrapper.in("id",currentRolePermissions);
            carrentPermissions = permissionService.list(queryWrapper);
        }else {
            carrentPermissions = new ArrayList<>();
        }
        //构造List<TreeNode>：若查询出来的权限和菜单有相等的，则将checkArr=1
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission p1 : allPermissions){
            String checkArr = "0";
            for (Permission p2 : carrentPermissions){
                if (p1.getId() == p2.getId()){
                    checkArr = "1";
                    break;
                }
            }
            Boolean spread = (p1.getOpen() == null||p1.getOpen() == 1) ? true:false;
            treeNodes.add(new TreeNode(p1.getId(),p1.getPid(),p1.getTitle(),spread,checkArr));
        }
        return new DataGridView(treeNodes);
    }

    /**
     * 保存角色和菜单权限之间的关系
     */
    @RequestMapping("/saveRolePermission")
    public ResultObj saveRolePermission(Integer rid,Integer[] ids){
        try {
            roleService.saveRolePermission(rid,ids);
            return ResultObj.DISPATCH_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }
}

