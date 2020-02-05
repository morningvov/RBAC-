package com.momo.warehouse.sys.service;

import com.momo.warehouse.sys.bean.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author morning
 * @since 2019-12-06
 */
public interface PermissionService extends IService<Permission> {

    Integer findCount(String column);
}
