package com.momo.warehouse.sys.common;

import lombok.Data;

/**
 * @author momo
 * @create 2019-12-06 下午 16:16
 */
public interface Constast {

    /**
     * 状态码
     */
    Integer Ok = 200;
    Integer ERROR = -1;

    /**
     * 用户默认密码
     */
    String USER_DEFAULT_PWD="123456";

    /**
     * 菜单权限类型
     */
    String TYPE_MENU = "menu";
    String TYPE_PERMISSION = "permission";
    /**
     * 可用状态
     */
    Object AVAILABLE_TRUE = 1;
    Object AVAILABLE_FALSE = 0;

    /**
     * 用户类型
     */
    Integer USER_TYPE_SUPER = 0;
    Integer USER_TYPE_NORMAL = 1;

    /**
     *展开类型
     */
    Integer OPEN_TRUE = 1;
    Integer OPEN_FALSE = 0;

}
