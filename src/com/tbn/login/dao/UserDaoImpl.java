package com.tbn.login.dao;

import java.util.List;

import com.common.dao.BaseDaoImpl;
import com.po.User;


public class UserDaoImpl extends BaseDaoImpl implements UserDao{

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return this.getHqlQueryList("from User", null);
	}

	public List<User> findUserByNameAndPass(String name, String pass) {
		// TODO Auto-generated method stub
		String hql="from User where name='"+name+"' and passward ='"+pass+"'";
		
		return this.getHqlQueryList(hql, null);
	}
	
}
