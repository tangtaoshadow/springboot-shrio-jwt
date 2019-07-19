package com.tangtao.springbootshirojwt.shiro;

import com.tangtao.springbootshirojwt.mapper.UserMapper;
import com.tangtao.springbootshirojwt.util.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CustomRealm extends AuthorizingRealm {

    private final UserMapper userMapper;

    @Autowired
    public CustomRealm(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        System.out.println(">执行 doGetAuthenticationInfo 身份认证");

        String token = (String) authenticationToken.getCredentials();
        System.out.println(">token "+token);
        // 解密获得 username 用于和数据库进行对比
        String username = JWTUtil.getUsername(token);

        if (username == null || !JWTUtil.verify(token, username)) {
            throw new AuthenticationException("token 认证失败");
        }

        // 检查用户是否存在 通过密码来判断
        String password = userMapper.getPassword(username);
        if (password == null) {
            throw new AuthenticationException("该用户不存在 ");
        }

        // 自定义数据库中的状态值来控制 用户的状态
        int ban = userMapper.checkUserBanStatus(username);
        if (ban == 1) {
            throw new AuthenticationException("该用户已被封号 ");
        }

        System.out.println(">getName "+getName());
        // return new SimpleAuthenticationInfo(token, token, "MyRealm");
        return new SimpleAuthenticationInfo(token, token, getName());

    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        System.out.println(">执行 doGetAuthorizationInfo 权限认证");
        // principals 传过来的是 token
        // System.out.println(">principals = "+principals);
        String username = JWTUtil.getUsername(principals.toString());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //获得该用户角色
        String role = userMapper.getRole(username);

        // 每个角色拥有默认的权限
        String rolePermission = userMapper.getRolePermission(username);

        // 每个用户可以设置新的权限
        String permission = userMapper.getPermission(username);

        Set<String> roleSet = new HashSet<>();
        Set<String> permissionSet = new HashSet<>();
        // 需要将 role, permission 封装到 Set 作为 info.setRoles(), info.setStringPermissions() 的参数
        roleSet.add(role);
        permissionSet.add(rolePermission);
        permissionSet.add(permission);
        // 设置该用户拥有的角色和权限
        info.setRoles(roleSet);
        info.setStringPermissions(permissionSet);
        return info;
    }

}
