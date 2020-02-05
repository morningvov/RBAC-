package com.momo.warehouse.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.momo.warehouse.sys.bean.User;
import com.momo.warehouse.sys.mapper.DeptMapper;
import com.momo.warehouse.sys.mapper.UserMapper;
import com.momo.warehouse.sys.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author morning
 * @since 2019-12-04
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean save(User entity) {
        return super.save(entity);
    }

    @Override
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        UserMapper userMapper = getBaseMapper();
        //根据用户id删除用户角色中间表的数据
        userMapper.deleteUserRoleByUid(id);
        //删除用户头[如果是默认头像不删除，否则删除]
        return super.removeById(id);
    }

    /**
     * 查询排序码的最大值
     * @param column
     * @return
     */
    @Override
    public Integer findCount(String column) {
        UserMapper userMapper = getBaseMapper();
        return userMapper.findCount(column);
    }

    /**
     * 保存用户与角色的关系
     * @param uid
     * @param ids
     */
    @Override
    public void saveUserRole(Integer uid, Integer[] ids) {
        //根据用户ID删除sys_role_user里面的数据
        UserMapper userMapper = getBaseMapper();
        userMapper.deleteUserRoleByUid(uid);
        if (ids!=null && ids.length>0){   //ids不能为null且小于零，不然没有意义
            for (Integer rid : ids){
                userMapper.saveRolePermission(uid,rid);
            }
        }
    }
}
