package com.momo.warehouse.sys.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.momo.warehouse.sys.bean.Dept;
import com.momo.warehouse.sys.bean.Role;
import com.momo.warehouse.sys.bean.User;
import com.momo.warehouse.sys.common.Constast;
import com.momo.warehouse.sys.common.PinyinUtils;
import com.momo.warehouse.sys.common.json.DataGridView;
import com.momo.warehouse.sys.common.json.ResultObj;
import com.momo.warehouse.sys.service.DeptService;
import com.momo.warehouse.sys.service.RoleService;
import com.momo.warehouse.sys.service.UserService;
import com.momo.warehouse.sys.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author morning
 * @since 2019-12-04
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;

    /**
     * 用户全查询（模糊分页）
     */
    @RequestMapping("/loadAllUser")
    public DataGridView loadAllUser(UserVo userVo) {
        IPage<User> page = new Page<>(userVo.getPage(), userVo.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(userVo.getLoginname()), "loginname", userVo.getLoginname()).or().eq(StringUtils.isNotBlank(userVo.getName()), "name", userVo.getName());
        queryWrapper.eq(StringUtils.isNotBlank(userVo.getAddress()), "address", userVo.getAddress());
        queryWrapper.eq(userVo.getDeptid() != null, "deptid", userVo.getDeptid());
        queryWrapper.eq("type", Constast.USER_TYPE_NORMAL); //查询系统用户
        userService.page(page, queryWrapper);

        //数据处理
        List<User> list = page.getRecords();
        for (User user : list) {
            //翻译部门名称
            Integer deptid = user.getDeptid();
            if (deptid != null) {
                Dept one = deptService.getById(deptid);
                user.setDeptname(one.getTitle());
            }
            //上级(mgr-->id)
            Integer mgr = user.getMgr();
            if (mgr != null) {
                User one = userService.getById(mgr);
                user.setLeadername(one.getName());
            }
        }
        return new DataGridView(page.getTotal(), list);
    }

    /**
     * 加载最大的排序码
     */
    @RequestMapping("/loadUserMaxOrderNum")
    public Map<String, Object> loadUserMaxOrderNum() {
        Map<String, Object> map = new HashMap<>();
        int count = userService.findCount("ordernum");
        if (count > 0) {
            map.put("value", count + 1);
        } else {
            map.put("value", 1);
        }
        return map;
    }

    /**
     * 根据部门id查询用户
     */
    @RequestMapping("/loadUserByDeptId")
    public DataGridView loadUserByDeptId(Integer deptid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(deptid != null, "deptid", deptid);
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
        queryWrapper.eq("type", Constast.USER_TYPE_NORMAL);
        List<User> list = userService.list(queryWrapper);
        return new DataGridView(list);
    }

    /**
     * 把用户名转换为拼音
     */
    @RequestMapping("/changeChineseToPinyin")
    public Map<String, Object> changeChineseToPinyin(String username) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(username)) {
            map.put("value", PinyinUtils.getPingYin(username));
        } else {
            map.put("value", "");
        }
        return map;
    }

    /**
     * 添加用户
     */
    @RequestMapping("/addUser")
    public ResultObj addUser(UserVo userVo) {
        try {
            userVo.setType(Constast.USER_TYPE_NORMAL);  //设置类型
            userVo.setHiredate(new Date()); //设置入职时间
            String salt = IdUtil.simpleUUID().toUpperCase();
            userVo.setSalt(salt);  //设置盐
            userVo.setPwd(new Md5Hash(Constast.USER_DEFAULT_PWD, salt, 2).toString()); //设置密码
            userService.save(userVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 根据用户id查询用户
     */
    @RequestMapping("/loadUserById")
    public DataGridView loadUserById(Integer id) {
        if (id != null) {
            return new DataGridView(userService.getById(id));
        }
        return new DataGridView();
    }

    /**
     * 修改用户
     */
    @RequestMapping("/updateUser")
    public ResultObj updateUser(UserVo userVo) {
        try {
            userService.updateById(userVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除用户
     */
    @RequestMapping("/deleteUser")
    public ResultObj deleteUser(Integer id) {
        try {
            userService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 重置用户密码(默认密码)
     */
    @RequestMapping("/resetPwd")
    public ResultObj resetPwd(Integer id) {
        try {
            if (id != null) {
                User user = new User();
                user.setId(id);
                String salt = IdUtil.simpleUUID().toUpperCase();
                user.setSalt(salt);  //设置盐
                user.setPwd(new Md5Hash(Constast.USER_DEFAULT_PWD, salt, 2).toString()); //设置密码
                userService.updateById(user);
                return ResultObj.RESET_SUCCESS;
            } else {
                return ResultObj.RESET_ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.RESET_ERROR;
        }
    }


    /**
     * 根据用户id查询角色并选中已拥有的角色
     */
    @RequestMapping("/initRoleByUserId")
    public DataGridView initRoleByUserId(Integer id) {
        //查询所有可用的角色
        QueryWrapper<Role> querywrapper = new QueryWrapper<>();
        querywrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Map<String, Object>> listMaps = roleService.listMaps(querywrapper);
        //查询当前用户拥有的角色ID集合
        List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(id);
        for (Map<String, Object> map : listMaps) {
            Boolean LAY_CHECKED =false;
            //角色的id
            Integer roleId = (Integer) map.get("id");
            //用户所拥有的角色id与所有可用角色的id进行匹配，相等的选中
            for (Integer rid: currentUserRoleIds) {
                if(rid==roleId){
                    LAY_CHECKED =true;
                    break;
                }
            }
            map.put("LAY_CHECKED",LAY_CHECKED);
        }
        return new DataGridView(Long.valueOf(listMaps.size()),listMaps);
    }

    /**
     * 保存用户和角色的关系
     */
    @RequestMapping("/saveUserRole")
    public ResultObj saveUserRole(Integer uid,Integer[] ids){
        try {
            userService.saveUserRole(uid,ids);
            return ResultObj.DISPATCH_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }
}

