package com.tbn.login.service;

import java.util.List;

import com.tbn.login.dao.UserDao;


public class LoginService {
	
	private UserDao userDao;
   
	public List getUserAll(){
	   return userDao.findAll();
   }
	public boolean verifUser(String name,String passward){
		List list=userDao.findUserByNameAndPass(name, passward);
		if(list!=null && list.size()>0){
			return true;
		}else{
			return false;
		}
			
	}


}
