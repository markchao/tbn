package com.common.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BaseDaoImpl implements BaseDao{
	private SessionFactory sessionFactory;
	private Session session;
	
	public Session getSession() {
		if(this.sessionFactory!=null)
			session=this.sessionFactory.getCurrentSession();
		else 
			session=null;
		return session;
	}

	public void remove(Object o) {
		// TODO Auto-generated method stub
		session=this.getSession();
		session.delete(o);
		session.flush();

	}

	public void saveOrUpdate(Object o) {
		// TODO Auto-generated method stub
		session=this.getSession();
		session.saveOrUpdate(o);
		session.flush();
	}

	public void update(Object o) {
		// TODO Auto-generated method stub
		 session=this.getSession();
		 session.update(o);
		 session.flush();
	}

	public <T> T load(Class cls, Serializable id) {
		// TODO Auto-generated method stub
		session=this.getSession();
		T o=(T)session.load(cls, id);
		return o;
	}

	public <T> List<T> getHqlQueryList(String Hql,Map paramsMap) {
		// TODO Auto-generated method stub
		
		Query query=this.createQuery(Hql,paramsMap,true);
		List list=query.list();
		return list;
	}
	public <T> List<T> getSqlQueryList(String Sql,Map paramsMap) {
		// TODO Auto-generated method stub
		
		Query query=this.createQuery(Sql,paramsMap,false);
		List list=query.list();
		return list;
	}

	private Query createQuery(String hql,Map paramsMap,Boolean isHql){
		Query query=null;
		session=this.getSession();
		if(isHql){
			query=session.createQuery(hql);
		}else{
			query=session.createSQLQuery(hql);
		}
		if(paramsMap!=null && paramsMap.size()>0){
			for(Iterator it=paramsMap.keySet().iterator();it.hasNext();){
				String name=(String)it.next();
				query.setParameter(name, paramsMap.get(name));
			}
		}
		return query;
	}

	public void setSessionFactory(SessionFactory seessionFactory) {
		this.sessionFactory = seessionFactory;
	}
	

}
