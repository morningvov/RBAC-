package com.momo.warehouse.sys.service;

import com.momo.warehouse.sys.bean.Dept;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author morning
 * @since 2020-01-09
 */
public interface DeptService extends IService<Dept> {

    Integer findCount(String column);
}
