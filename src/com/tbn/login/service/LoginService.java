package com.tbn.login.service;

import java.util.List;

import com.tbn.login.dao.UserDao;

public class LoginService {
   private UserDao userDao;
   
   public List getUserAll(){
	   return userDao.findAll();
   }

public UserDao getUserDao() {
	return userDao;
}

public void setUserDao(UserDao userDao) {
	this.userDao = userDao;
}
}
