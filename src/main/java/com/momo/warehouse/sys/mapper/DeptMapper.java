package com.momo.warehouse.sys.mapper;

import com.momo.warehouse.sys.bean.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author morning
 * @since 2020-01-09
 */
public interface DeptMapper extends BaseMapper<Dept> {

    Integer findCount(@Param("column")String column);
}
