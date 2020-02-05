package com.momo.warehouse.sys.cache;

import com.momo.warehouse.sys.bean.Dept;
import com.momo.warehouse.sys.bean.User;
import com.momo.warehouse.sys.vo.DeptVo;
import com.momo.warehouse.sys.vo.UserVo;
import com.sun.org.apache.bcel.internal.generic.I2F;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author momo
 * @create 2020-01-12 下午 16:27
 */
@Aspect
@Component
@EnableAspectJAutoProxy
public class CacheAspect {

    /**
     * 日志处理
     */
    private final Logger logger = LoggerFactory.getLogger(CacheAspect.class);

    //声明一个缓存容器
    private Map<String, Object> CACHE_CONTAINER = new HashMap<>();

    //声明部门切面表达式
    private static final String POINTCUT_DEPT_ADD = "execution(* com.momo.warehouse.sys.service.impl.DeptServiceImpl.save(..))";
    private static final String POINTCUT_DEPT_UPDATE = "execution(* com.momo.warehouse.sys.service.impl.DeptServiceImpl.updateById(..))";
    private static final String POINTCUT_DEPT_GET = "execution(* com.momo.warehouse.sys.service.impl.DeptServiceImpl.getById(..))";
    private static final String POINTCUT_DEPT_REMOVEBYID = "execution(* com.momo.warehouse.sys.service.impl.DeptServiceImpl.removeById(..))";

    private static final String CACHE_DEPT_PROFIX = "dept:";

    /**
     * 添加切入
     */
    @Around(value = POINTCUT_DEPT_ADD)
    public Object cacheDeptAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数(通过id进行查询)
        Dept dept = (Dept) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {   //添加成功之后，id会自动返回，并将数据放入缓存当中
            logger.info("部门对象添加成功并放入缓存当中");
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX + dept.getId(), dept);
        }
        return res;
    }

    /**
     * 查询切入:根据id查询相应的部门
     */
    @Around(value = POINTCUT_DEPT_GET)
    public Object cacheDeptGet(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数(通过id进行查询)
        Integer object = (Integer) joinPoint.getArgs()[0];
        //从缓存里面取查询
        Object res1 = CACHE_CONTAINER.get(CACHE_DEPT_PROFIX + object);
        if (res1 != null) {  //若缓存中有，直接返回
            logger.info("已从缓存里面找到部门对象" + CACHE_DEPT_PROFIX + object);
            return res1;
        } else {
            //放行获取方法的返回值,并将其放入map容器中去
            Dept res2 = (Dept) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX + res2.getId(), res2);
            logger.info("未从缓存里面找到部门对象，从数据库中查询并放入到缓存" + CACHE_DEPT_PROFIX + res2.getId());
            return res2;
        }
    }

    /**
     * 更新切入:更新数据库，成功之后更新缓存
     */
    @Around(value = POINTCUT_DEPT_UPDATE)
    public Object cacheDeptUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数
        Dept deptVo = (Dept) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {  //更新成功之后，判断容器内是否为空，若为空，则创建一个新的dept，用来替换
            Dept dept = (Dept) CACHE_CONTAINER.get(CACHE_DEPT_PROFIX + deptVo.getId());
            if (null == dept) {
                dept = new Dept();
            }
            //spring中的工具类
            BeanUtils.copyProperties(deptVo, dept);
            logger.info("部门对象缓存已更新" + CACHE_DEPT_PROFIX + dept);
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX + dept.getId(), dept);
        }
        return isSuccess;
    }

    /**
     * 删除切入:删除部门的缓存
     */
    @Around(value = POINTCUT_DEPT_REMOVEBYID)
    public Object POINTCUT_DEPT_REMOVEBYID(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {  //删除成功之后，移除容器中的值
            //删除缓存
            CACHE_CONTAINER.remove(CACHE_DEPT_PROFIX + id);
            logger.info("部门对象缓存已删除" + CACHE_DEPT_PROFIX + id);
        }
        return isSuccess;
    }


    //声明用户切面表达式
    private static final String POINTCUT_USER_UPDATE = "execution(* com.momo.warehouse.sys.service.impl.UserServiceImpl.updateById(..))";
    private static final String POINTCUT_USER_ADD = "execution(* com.momo.warehouse.sys.service.impl.UserServiceImpl.save(..))";
    private static final String POINTCUT_USER_GET = "execution(* com.momo.warehouse.sys.service.impl.UserServiceImpl.getById(..))";
    private static final String POINTCUT_USER_REMOVEBYID = "execution(* com.momo.warehouse.sys.service.impl.UserServiceImpl.removeById(..))";

    private static final String CACHE_USER_PROFIX = "user:";

    /**
     * 用户添加切入
     */
    @Around(value = POINTCUT_USER_ADD)
    public Object cacheUserAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数(通过id进行查询)
        User user = (User) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {   //添加成功之后，id会自动返回，并将数据放入缓存当中
            logger.info("用户对象添加成功并放入缓存当中");
            CACHE_CONTAINER.put(CACHE_USER_PROFIX + user.getId(), user);
        }
        return res;
    }

    /**
     * 用户查询切入:根据id查询相应的用户
     */
    @Around(value = POINTCUT_USER_GET)
    public Object cacheUserGet(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数(通过id进行查询)
        Integer object = (Integer) joinPoint.getArgs()[0];
        //从缓存里面取查询
        Object res1 = CACHE_CONTAINER.get(CACHE_USER_PROFIX + object);
        if (res1 != null) {  //若缓存中有，直接返回
            logger.info("已从缓存里面找到用户对象" + CACHE_USER_PROFIX + object);
            return res1;
        } else {
            //放行获取方法的返回值,并将其放入map容器中去
            User res2 = (User) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_USER_PROFIX + res2.getId(), res2);
            logger.info("未从缓存里面找到用户对象，从数据库中查询并放入到缓存" + CACHE_USER_PROFIX + res2.getId());
            return res2;
        }
    }

    /**
     * 用户更新切入:更新数据库，成功之后更新缓存
     */
    @Around(value = POINTCUT_USER_UPDATE)
    public Object cacheUserUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数
        User userVo = (User) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {  //更新成功之后，判断容器内是否有值，没有则放，有则替换
            User user = (User) CACHE_CONTAINER.get(CACHE_USER_PROFIX + userVo.getId());
            if (null == user) {
                user = new User();
            }
            //spring中的工具类
            BeanUtils.copyProperties(userVo, user);
            logger.info("用户对象缓存已更新" + CACHE_USER_PROFIX + user);
            CACHE_CONTAINER.put(CACHE_USER_PROFIX + user.getId(), user);
        }
        return isSuccess;
    }

    /**
     * 用户删除切入:删除用户的缓存
     */
    @Around(value = POINTCUT_USER_REMOVEBYID)
    public Object POINTCUT_USER_REMOVEBYID(ProceedingJoinPoint joinPoint) throws Throwable {
        //取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {  //删除成功之后，移除容器中的值
            //删除缓存
            CACHE_CONTAINER.remove(CACHE_USER_PROFIX + id);
            logger.info("用户对象缓存已删除" + CACHE_USER_PROFIX + id);
        }
        return isSuccess;
    }
}
