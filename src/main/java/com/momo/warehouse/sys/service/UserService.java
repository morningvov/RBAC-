package com.momo.warehouse.sys.service;

import com.momo.warehouse.sys.bean.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author morning
 * @since 2019-12-04
 */
public interface UserService extends IService<User> {

    /**
     *加载最大的排序码
     */
    Integer findCount(String ordernum);

    /**
     * 保存用户和角色的关系
     * @param uid
     * @param ids
     */
    void saveUserRole(Integer uid, Integer[] ids);
}
