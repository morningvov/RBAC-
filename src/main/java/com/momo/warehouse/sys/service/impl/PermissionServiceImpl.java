package com.momo.warehouse.sys.service.impl;

import com.momo.warehouse.sys.bean.Permission;
import com.momo.warehouse.sys.mapper.PermissionMapper;
import com.momo.warehouse.sys.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author morning
 * @since 2019-12-06
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public boolean removeById(Serializable id) {
        //根据权限或菜单id删除权限角色中间表里的数据
        PermissionMapper permissionMapper = getBaseMapper();
        permissionMapper.deleteRolePermissionByPid(id);
        return super.removeById(id);
    }

    //查找某列的最大值
    public Integer findCount(String column){
        PermissionMapper permissionMapper = getBaseMapper();
        return permissionMapper.findCount(column);
    }
}
