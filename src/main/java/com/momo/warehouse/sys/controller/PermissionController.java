package com.momo.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.momo.warehouse.sys.bean.Permission;
import com.momo.warehouse.sys.common.Constast;
import com.momo.warehouse.sys.common.TreeNode.TreeNode;
import com.momo.warehouse.sys.common.json.DataGridView;
import com.momo.warehouse.sys.common.json.ResultObj;
import com.momo.warehouse.sys.service.PermissionService;
import com.momo.warehouse.sys.vo.PermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  权限前端控制器
 * </p>
 *
 * @author morning
 * @since 2019-12-06
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

/*****************权限管理开始*******************/

    /**
     * 加载权限管理左边的权限菜单树的json
     */
    @RequestMapping("/loadPermissionManagerLeftTreeJson")
    public DataGridView loadPermissionManagerLeftTreeJson(PermissionVo permissionVo){
        //根据排序码进行左边权限树的排序
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("ordernum");
        queryWrapper.eq("type", Constast.TYPE_MENU);//只查 permission
        List<Permission> list = permissionService.list(queryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission permission : list){
            Boolean spread = permission.getOpen()==1?true:false;
            treeNodes.add(new TreeNode(permission.getId(),permission.getPid(),permission.getTitle(),spread));
        }
        return new DataGridView(treeNodes);
    }

    /**
     * 模糊查询以及全查询与分页
     * @param
     * @return
     */
    @RequestMapping("/loadAllPermission")
    public DataGridView loadAllPermission(PermissionVo permissionVo){
        IPage<Permission> page = new Page<>(permissionVo.getPage(), permissionVo.getLimit());
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",Constast.TYPE_PERMISSION);
        queryWrapper.like(StringUtils.isNotBlank(permissionVo.getTitle()),"title",permissionVo.getTitle());
        queryWrapper.like(StringUtils.isNotBlank(permissionVo.getPercode()),"percode",permissionVo.getPercode());
        if(permissionVo.getId()!=null){  //与上面的eq用 xx AND (xx=xx OR xx=xx) 区分
            queryWrapper.and(i->i.eq("id",permissionVo.getId()).or().eq("pid",permissionVo.getId()));
        }
        // queryWrapper.eq(permissionVo.getId()!=null,"id",permissionVo.getId()).or().eq(permissionVo.getId()!=null,"pid",permissionVo.getId());
        queryWrapper.orderByAsc("ordernum");
        permissionService.page(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     *加载最大的排序码:权限和权限的排序码是一起的
     */
    @RequestMapping("loadPermissionMaxOrderNum")
    public Map<String, Object> loadPermissionMaxOrderNum(){
        Map<String, Object> map = new HashMap<>();
        int count = permissionService.findCount("ordernum");
        if(count > 0){
            map.put("value",count+1);
        }else {
            map.put("value",1);
        }
        return map;
    }

    /**
     *添加权限
     */
    @RequestMapping("/addPermission")
    public ResultObj addPermission(PermissionVo permissionVo){
        try{
            //设置添加类型的权限
            permissionVo.setType(Constast.TYPE_PERMISSION);
            permissionService.save(permissionVo);
            return ResultObj.ADD_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     *修改权限
     */
    @RequestMapping("/updatePermission")
    public ResultObj updatePermission(PermissionVo permissionVo){
        try{   //只会更新非空的字段
            if(permissionVo.getId()==1) {   //顶级权限只能为0
                permissionVo.setPid(0);
            }
            permissionService.updateById(permissionVo);
            return ResultObj.UPDATE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/deletePermission")
    public ResultObj deletePermission(PermissionVo permissionVo){
        try{
            permissionService.removeById(permissionVo.getId());
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /*****************权限管理结束*******************/
}

