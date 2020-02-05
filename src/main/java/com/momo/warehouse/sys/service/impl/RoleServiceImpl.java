package com.momo.warehouse.sys.service.impl;

import com.momo.warehouse.sys.bean.Role;
import com.momo.warehouse.sys.mapper.RoleMapper;
import com.momo.warehouse.sys.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author morning
 * @since 2020-01-13
 */
@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    /**
     * 删除角色得先删除角色关联的表中的字段
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        //根据角色ID删除sys_role_permission
        getBaseMapper().deleteRolePermissionByRid(id);
        //根据角色ID删除sys_role_user
        getBaseMapper().deleteRoleUserByRid(id);
        return super.removeById(id);
    }

    /**
     * 根据角色ID查询当前角色拥有的权限或菜单ID
     * @param roleId
     * @return
     */
    @Override
    public List<Integer> queryRolePermissionIdsByRid(Integer roleId) {
        return getBaseMapper().queryRolePermissionIdsByRid(roleId);
    }

    /**
     * 保存角色和菜单权限之间的关系
     * @param rid
     * @param ids
     */
    @Override
    public void saveRolePermission(Integer rid, Integer[] ids) {
        RoleMapper roleMapper = getBaseMapper();
        //先删除该角色原来的关系，不能重复保存
        roleMapper.deleteRolePermissionByRid(rid);
        if (ids!=null && ids.length>0){   //ids不能为null且小于零，不然没有意义
            for (Integer id : ids){
                roleMapper.saveRolePermission(rid,id);
            }
        }
    }

    /**
     * 查询当前用户拥有的角色ID集合
     * @param id
     * @return
     */
    @Override
    public List<Integer> queryUserRoleIdsByUid(Integer id) {
        return getBaseMapper().queryUserRoleIdsByUid(id);
    }

}
