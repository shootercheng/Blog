package com.scd.realm;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.scd.entity.Blogger;
import com.scd.service.BloggerService;

/**
 * 自定义Realm
 * @author scd_小锋
 *
 */
public class MyRealm extends AuthorizingRealm{

	@Resource
	private BloggerService bloggerService;
	
	/**
	 * 为当限前登录的用户授予角色和权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

	/**
	 * 验证当前登录的用户
	 */
	protected AuthenticationInfo doGetAuthenticationInfoOld(AuthenticationToken token) throws AuthenticationException {
		String userName=(String)token.getPrincipal();
		Blogger blogger=bloggerService.getByUserName(userName);
		if(blogger!=null){
			SecurityUtils.getSubject().getSession().setAttribute("currentUser", blogger);
			AuthenticationInfo authcInfo=new SimpleAuthenticationInfo(blogger.getUserName(),blogger.getPassword(),"xx");
			return authcInfo;
		}else{
			return null;				
		}
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String userName=(String)token.getPrincipal();
		String password = new String((char[]) token.getCredentials());
		Blogger blogger=bloggerService.getByUserName(userName);
		if(blogger == null){
			throw new UnknownAccountException("用户不存在!");
		}
		if(password == null || !password.equals(blogger.getPassword())){
			throw new IncorrectCredentialsException("密码错误!");
		}
		SecurityUtils.getSubject().getSession().setAttribute("currentUser", blogger);
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userName, password, getName());
		return info;
	}

}
