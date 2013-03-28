package com.tbn.login.dao;

import java.util.List;

import com.common.dao.BaseDao;
import com.po.User;


public interface UserDao extends BaseDao{
    public List<User> findAll();
    
}
