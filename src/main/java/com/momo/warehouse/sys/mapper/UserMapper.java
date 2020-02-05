package com.momo.warehouse.sys.mapper;

import com.momo.warehouse.sys.bean.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author morning
 * @since 2019-12-04
 */
public interface UserMapper extends BaseMapper<User> {

    Integer findCount(@Param("column")String column);

    //根据用户id删除用户角色中间表的数据
    void deleteUserRoleByUid(@Param("uid")Serializable uid);

    //保存用户与角色的关系
    void saveRolePermission(@Param("uid")Integer uid, @Param("rid")Integer rid);
}
