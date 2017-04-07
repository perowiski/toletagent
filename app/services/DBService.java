package services;

import models.Model;
import org.springframework.util.StringUtils;
import play.db.jpa.JPAApi;
import pojos.Param;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Singleton
public class DBService {
	private JPAApi jpa;

	public static DBService Q;
	public static JPAApi J;

	@Inject
	public DBService(JPAApi jpa) {
		this.jpa = jpa;
		Q = this;
		J = this.jpa;
	}

	public void withTransaction(Runnable run) {
		jpa.withTransaction(run);
	}
    
	public void save(Model model) {
		if(model.id == null) {
			jpa.em().persist(model);
		} else {
			jpa.em().merge(model);
		}
	}

	public void persist(Object model) {
		jpa.em().persist(model);
	}

	public void merge(Object model) {
		jpa.em().merge(model);
	}

	public <T> Long count(Class<T> clazz, DBFilter filter) {
		String sql = "SELECT COUNT(t) FROM " + clazz.getSimpleName() + " t ";
		sql += filter.getSql();
		TypedQuery<Long> q = jpa.em().createQuery(sql, Long.class);
		addParam(q, filter);
		return q.getSingleResult();
	}

	public <T> List<T> find(Class<T> clazz, DBFilter filter) {
		String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
		sql += filter.getSql();
		TypedQuery<T> q = jpa.em().createQuery(sql, clazz);
		addParam(q, filter);
		return q.getResultList();
	}

	public <T> List<T> find(Class<T> clazz, DBFilter filter, String sort) {
		return find(clazz, filter, new Param(0, Integer.MAX_VALUE, sort));
	}

	public <T> List<T> find(Class<T> clazz, DBFilter filter, Param param) {
		String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
		sql += filter.getSql() + getOrder(param);
		TypedQuery<T> q = jpa.em().createQuery(sql, clazz);
		addParam(q, filter);
		q.setFirstResult(param.getOffset()).setMaxResults(param.getSize());
		return q.getResultList();
	}

    public <T> List<T> findD(Class<T> clazz, DBFilter filter, Param param) {
        String sql = "SELECT DISTINCT t FROM " + clazz.getSimpleName() + " t ";
        sql += filter.getSql() + getOrder(param);
        TypedQuery<T> q = jpa.em().createQuery(sql, clazz);
        addParam(q, filter);
        q.setFirstResult(param.getOffset()).setMaxResults(param.getSize());
        return q.getResultList();
    }

	public <T> T findOne(Class<T> clazz, DBFilter filter) {
		try {
			String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
			sql += filter.getSql();
			TypedQuery<T> q = jpa.em().createQuery(sql, clazz);
			addParam(q, filter);
			return q.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public <T> T findOne(Class<T> clazz, DBFilter filter, String sort) {
		List<T> list =  find(clazz, filter, new Param(0, 1, sort));
		if(!list.isEmpty()) return list.get(0);
		return null;
	}

	public <T> T findOne(Class<T> clazz, Long id) {
		try {
			T t = jpa.em().find(clazz, id);
			return t;
		} catch(Exception e) {
			return null;
		}
	}

	public <T> T findOne(Class<T> clazz, String id) {
		try {
			T t = jpa.em().find(clazz, Long.valueOf(id));
			return t;
		} catch(Exception e) {
			return null;
		}
	}

	public <T> T findOne(Class<T> clazz, String key, Object value) {
		try{
			 T t = jpa.em().createQuery("SELECT t FROM "+clazz.getSimpleName()+" t WHERE t."+key+"=:value", clazz)
				.setParameter("value", value)
				.getSingleResult();
			return t;
		} catch(Exception e){
			return null;
		}
	}

	public <T> T findOne(Class<T> clazz, String key, Object value, String sort) {
		List<T> list = jpa.em().createQuery("SELECT t FROM " + clazz.getSimpleName() + " t WHERE t." + key + "=:value ORDER BY " + sort, clazz)
				.setParameter("value", value)
				.setMaxResults(1)
				.getResultList();
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public <T> void delete(T t){
		jpa.em().remove(t);
	}

	public void delete(Model model){
		jpa.em().remove(model);
	}

	public <T> void delete(Class<T> clazz, Long id){
		delete(findOne(clazz, id));
	}

	private <T> void addParam(TypedQuery<T> q, DBFilter filter) {
		for(Map.Entry<String, Object> entry: filter.getParams().entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof Date) {
				q.setParameter(key, (Date)value, TemporalType.DATE);
			} else {
				q.setParameter(key, value);
			}
		}
	}

	private String getOrder(Param param) {
		return StringUtils.hasText(param.getSort()) ? " ORDER BY " + param.getSort(): "";
	}

	private String cleanKey(String key) {
		String k = key.replace(">", "").replace("<", "").replace("=", "").substring(key.indexOf('.')+1);
		return k;
	}
}
