package com.momo.warehouse.sys.realm;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.momo.warehouse.sys.bean.Permission;
import com.momo.warehouse.sys.bean.User;
import com.momo.warehouse.sys.common.ActiverUser;
import com.momo.warehouse.sys.common.Constast;
import com.momo.warehouse.sys.service.PermissionService;
import com.momo.warehouse.sys.service.RoleService;
import com.momo.warehouse.sys.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author momo
 * @create 2019-12-04 下午 22:18
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private PermissionService permissionService;


    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        ActiverUser activerUser = (ActiverUser) principals.getPrimaryPrincipal();
        User user = activerUser.getUser();
        List<String> permissions = activerUser.getPermissions();
        //若是超级管理员，则拥有所有权限
        if (user.getType() == Constast.USER_TYPE_SUPER) {
            authorizationInfo.addStringPermission("*:*");
        } else {
            if (null != permissions && permissions.size() > 0) {
                authorizationInfo.addStringPermissions(permissions);
            }
        }
        return authorizationInfo;
    }

    /**
     * 认证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //先根据用户名查询出该用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loginname", token.getPrincipal().toString());
        User user = userService.getOne(queryWrapper);
        //判断
        if (null != user) {
            ActiverUser activerUser = new ActiverUser();
            activerUser.setUser(user);
            List<String> percodes = getPercode(user);
            activerUser.setPermissions(percodes);
            //盐
            ByteSource credentialsSalt = ByteSource.Util.bytes(user.getSalt());
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(activerUser, user.getPwd(),
                    credentialsSalt, this.getName());
            return info;
        }
        return null;
    }

    //查询用户对应的角色权限
    private List<String> getPercode(User user) {

        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("type", Constast.TYPE_PERMISSION);
        qw.eq("available", Constast.AVAILABLE_TRUE);

        //根据用户ID+角色+权限去查询
        Integer userId = user.getId();
        //根据用户ID查询角色id集合
        List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(userId);
        //根据角色ID取到权限ID
        Set<Integer> pids = new HashSet<>();
        for (Integer rid : currentUserRoleIds) {
            List<Integer> permissionIds = roleService.queryRolePermissionIdsByRid(rid);
            pids.addAll(permissionIds);
        }
        //根据菜单ID查询对象的权限数据
        List<Permission> list = new ArrayList<>();
        if (pids.size() > 0) {
            qw.in("id", pids);
            list = permissionService.list(qw);
        }

        //取出 percode
        List<String> percodes = new ArrayList<>();
        for (Permission permission : list) {
            percodes.add(permission.getPercode());
        }
        return percodes;
    }
}
