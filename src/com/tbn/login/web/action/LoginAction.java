package com.tbn.login.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.common.method.PubMethod;
import com.common.web.action.BaseAction;
import com.tbn.login.service.LoginService;
@Scope("prototype")
@Controller("loginAction")
public class LoginAction extends BaseAction{

	@Autowired
	private LoginService loginService;
	

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
		boolean flog=false;
		String name=this.getRequest().getParameter("userName");
		String passward=this.getRequest().getParameter("userPass");
		if(!PubMethod.isEmpty(name) && !PubMethod.isEmpty(passward))
			flog=loginService.verifUser(name,passward);
		
		if(flog)
			return "query";
		else
			return "login";
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
