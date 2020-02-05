package com.momo.warehouse.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.momo.warehouse.sys.bean.Dept;
import com.momo.warehouse.sys.mapper.DeptMapper;
import com.momo.warehouse.sys.service.DeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类 做缓存，由于用户中有dept的id，尽量用单表查询，因为后面mycat不支持连表查询
 * </p>
 *
 * @author morning
 * @since 2020-01-09
 */
@Service
@Transactional
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public boolean save(Dept entity) {
        return super.save(entity);
    }

    @Override
    public Dept getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean updateById(Dept entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    /**
     * 查询排序码的最大值
     * @param column
     * @return
     */
    @Override
    public Integer findCount(String column) {
        DeptMapper deptMapper = getBaseMapper();
        return deptMapper.findCount(column);
    }
}
