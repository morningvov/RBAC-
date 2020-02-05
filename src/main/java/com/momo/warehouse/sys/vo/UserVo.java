package com.momo.warehouse.sys.vo;

import com.momo.warehouse.sys.bean.Dept;
import com.momo.warehouse.sys.bean.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对实体类的封装
 * @author momo
 * @create 2019-12-06 下午 19:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVo extends User {

    private static final long serialVersionUID=1L;

    //分页参数
    private Integer page = 1;
    private Integer limit = 10;

}
