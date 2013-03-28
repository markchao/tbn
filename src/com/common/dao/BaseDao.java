package com.common.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao {
	  public void saveOrUpdate(Object o);
	  public void update(Object o);
	  public void remove(Object o);
	  public <T> T load(Class cls,Serializable id);
	  public <T> List<T> getHqlQueryList(String Hql,Map paramsMap);
	  public <T> List<T> getSqlQueryList(String Sql,Map paramsMap);
}
