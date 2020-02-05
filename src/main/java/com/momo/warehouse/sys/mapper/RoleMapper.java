package com.momo.warehouse.sys.mapper;

import com.momo.warehouse.sys.bean.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author morning
 * @since 2020-01-13
 */
public interface RoleMapper extends BaseMapper<Role> {

    //根据角色ID删除sys_role_permission
    void deleteRolePermissionByRid(@Param("id") Serializable id);

    //根据角色ID删除sys_role_user
    void deleteRoleUserByRid(@Param("id") Serializable id);

    /**
     * 根据角色ID查询当前角色拥有的权限或菜单ID
     * @param roleId
     * @return
     */
    List<Integer> queryRolePermissionIdsByRid(@Param("roleId")Integer roleId);

    /**
     * 保存角色和菜单权限之间的关系
     * @param rid
     * @param id
     */
    void saveRolePermission(@Param("rid")Integer rid, @Param("pid")Integer id);

    /**
     * 查询当前用户拥有的角色ID集合
     * @param id
     * @return
     */
    List<Integer> queryUserRoleIdsByUid(@Param("uid") Integer id);
}
