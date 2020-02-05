package com.momo.warehouse.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.momo.warehouse.sys.bean.Dept;
import com.momo.warehouse.sys.bean.Permission;
import com.momo.warehouse.sys.bean.User;
import com.momo.warehouse.sys.common.Constast;
import com.momo.warehouse.sys.common.TreeNode.TreeNode;
import com.momo.warehouse.sys.common.TreeNode.TreeNodeBuilder;
import com.momo.warehouse.sys.common.json.DataGridView;
import com.momo.warehouse.sys.common.WebUtils;
import com.momo.warehouse.sys.common.json.ResultObj;
import com.momo.warehouse.sys.service.PermissionService;
import com.momo.warehouse.sys.service.RoleService;
import com.momo.warehouse.sys.service.UserService;
import com.momo.warehouse.sys.vo.DeptVo;
import com.momo.warehouse.sys.vo.PermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 菜单管理前端控制器
 *
 * @author momo
 * @create 2019-12-06 下午 19:21
 */
@RestController
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    /**
     * 首页左侧菜单
     * @param permissionVo
     * @return
     */
    @RequestMapping("/loadIndexLeftMenuJson")
    public DataGridView loadIndexLeftMenuJson(PermissionVo permissionVo) {
        //查询所有菜单
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        //设置只能查菜单
        queryWrapper.eq("type", Constast.TYPE_MENU);
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);

        //获取用户信息
        User user = (User) WebUtils.getSession().getAttribute("user");
        //判断其是否为超级用户
        List<Permission> list = null;
        if (user.getType() == Constast.USER_TYPE_SUPER) {
            list = permissionService.list(queryWrapper);
        } else {
            //根据用户ID+角色+权限去查询
            Integer userId = user.getId();
            //根据用户ID查询角色id集合
            List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(userId);
            //根据角色ID取到权限和菜单ID
            Set<Integer> pids = new HashSet<>();   //去重
            for (Integer rid : currentUserRoleIds){
                List<Integer> permissionIds = roleService.queryRolePermissionIdsByRid(rid);
                pids.addAll(permissionIds);
            }
            //根据菜单ID查询对象的菜单数据
            if (pids.size()>0){
                queryWrapper.in("id",pids);
                list = permissionService.list(queryWrapper);
            }else{
                list = new ArrayList<>();
            }
        }

        //构造节点
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission p : list) {
            Integer id = p.getId();
            Integer pid = p.getPid();
            String title = p.getTitle();
            String icon = p.getIcon();
            String href = p.getHref();
            Boolean spread = p.getOpen() == Constast.OPEN_TRUE ? true : false;
            treeNodes.add(new TreeNode(id, pid, title, icon, href, spread));
        }
        //构造层级关系
        List<TreeNode> list2 = TreeNodeBuilder.build(treeNodes,1);
        return new DataGridView(list2);
    }

    /*****************菜单管理开始*******************/

    /**
     * 加载菜单管理左边的菜单树的json
     */
    @RequestMapping("/loadMenuManagerLeftTreeJson")
    public DataGridView loadMenuManagerLeftTreeJson(PermissionVo permissionVo){
        //根据排序码进行左边菜单树的排序
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("ordernum");
        queryWrapper.eq("type",Constast.TYPE_MENU);//只查 menu
        List<Permission> list = permissionService.list(queryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission menu : list){
            Boolean spread = menu.getOpen()==1?true:false;
            treeNodes.add(new TreeNode(menu.getId(),menu.getPid(),menu.getTitle(),spread));
        }
        return new DataGridView(treeNodes);
    }

    /**
     * 模糊查询以及全查询与分页
     * @param
     * @return
     */
    @RequestMapping("/loadAllMenu")
    public DataGridView loadAllMenu(PermissionVo permissionVo){
        IPage<Permission> page = new Page<>(permissionVo.getPage(), permissionVo.getLimit());
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constast.TYPE_MENU);//只查 menu
        queryWrapper.like(StringUtils.isNotBlank(permissionVo.getTitle()),"title",permissionVo.getTitle());
        if(permissionVo.getId()!=null){  //与上面的eq用 xx AND (xx=xx OR xx=xx) 区分
            queryWrapper.and(i->i.eq("id",permissionVo.getId()).or().eq("pid",permissionVo.getId()));
        }
        queryWrapper.orderByAsc("ordernum");
        permissionService.page(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     *加载最大的排序码:菜单和权限的排序码是一起的
     */
    @RequestMapping("loadMenuMaxOrderNum")
    public Map<String, Object> loadMenuMaxOrderNum(){
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
     *添加菜单
     */
    @RequestMapping("/addMenu")
    public ResultObj addMenu(PermissionVo permissionVo){
        try{
            //设置添加类型的菜单
            permissionVo.setType(Constast.TYPE_MENU);
            permissionService.save(permissionVo);
            return ResultObj.ADD_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     *修改菜单
     */
    @RequestMapping("/updateMenu")
    public ResultObj updateMenu(PermissionVo permissionVo){
        try{   //只会更新非空的字段
            if(permissionVo.getId()==1) {   //顶级菜单只能为0
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
     * 查询当前的ID的菜单有没有子菜单
     */
    @RequestMapping("/checkMenuHasChildrenNode")
    public Map<String,Object> checkMenuHasChildrenNode(PermissionVo permissionVo){
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(permissionVo.getId()!=null,"pid",permissionVo.getId());
        List<Permission> list = permissionService.list(queryWrapper);
        if (list.size()>0){
            map.put("value",true);
        }else {
            map.put("value",false);
        }
        return map;
    }

    /**
     * 删除子菜单
     */
    @RequestMapping("/deleteMenu")
    public ResultObj deleteMenu(PermissionVo permissionVo){
        try{
            permissionService.removeById(permissionVo.getId());
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
    /*****************菜单管理结束*******************/
}
