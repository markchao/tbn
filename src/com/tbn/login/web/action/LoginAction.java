package com.tbn.login.web.action;

import com.common.web.action.BaseAction;
import com.tbn.login.service.LoginService;

public class LoginAction extends BaseAction{

	private LoginService loginService;
	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	@Override
	public String doAdd() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doDelete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doEdit() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String query() throws Exception {
		// TODO Auto-generated method stub
		System.out.println(loginService.getUserAll().size());
		System.out.println("query");
		return "query";
	}

	@Override
	public String toAdd() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toEdit() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toMain() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
