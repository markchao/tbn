package com.common.web.action;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public abstract class BaseAction extends ActionSupport{
	private String method;
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	@Override
	public String execute() {
		// TODO Auto-generated method stub
		Method mymethod=null;
		String result="";
		try {
			mymethod=this.getClass().getMethod(this.getMethod());
			result=mymethod.invoke(this, null).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public abstract String toMain() throws Exception;
	//public abstract String toQuery() throws Exception;
	public abstract String query() throws Exception;
	public abstract String toAdd() throws Exception;
	public abstract String doAdd() throws Exception;
	public abstract String toEdit() throws Exception;
	public abstract String doEdit() throws Exception;
	public abstract String doDelete() throws Exception;
}
