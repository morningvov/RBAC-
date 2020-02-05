package com.momo.warehouse.sys.mapper;

import com.momo.warehouse.sys.bean.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author morning
 * @since 2019-12-06
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 根据权限或菜单id删除权限角色中间表里的数据
     * @param id
     */
    void deleteRolePermissionByPid(@Param("id") Serializable id);

    Integer findCount(@Param("column")String column);
}
