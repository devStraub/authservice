package com.jbase.generic;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * @author michel.pech
 */

@Component
public class BaseQuery {
	
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}	
	
	public Long getNextId(String entityName) {
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select max(tab.id) ");
		sql.append(" from " + entityName + " as tab");
		
		TypedQuery<Long> query = entityManager.createQuery(sql.toString(), Long.class);
		
		Long currentId =(Long) query.getSingleResult();
		return currentId + 1;
	}	
}
