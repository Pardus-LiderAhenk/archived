/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.persistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;

/***
 * Provides database access and CRUD methods for plugins.
 * 
 * @author <a href="mailto:basaran.ismaill@gmail.com">ismail BASARAN</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PluginDbServiceImpl implements IPluginDbService {

	// TODO deleteByProperties
	// TODO provide overload methods for delete & find (use order, offset,
	// maxResults)

	private static Logger logger = LoggerFactory.getLogger(PluginDbServiceImpl.class);

	private EntityManager entityManager;

	@Override
	public void save(Object entity) {
		entityManager.persist(entity);
		logger.debug("Object persisted: {}", entity.toString());
	}

	@Override
	public Object update(Object entity) {
		Object obj = entityManager.merge(entity);
		logger.debug("Object merged: {}", obj.toString());
		return obj;
	}

	@Override
	public Object saveOrUpdate(Object entity) {
		Object obj = entityManager.merge(entity);
		logger.debug("Object merged: {}", obj.toString());
		return obj;
	}

	@Override
	public void delete(Class entityClass, Object id) {
		Object entity = entityManager.find(entityClass, id);
		entityManager.remove(entity);
		logger.debug("Object removed with ID: {}", id.toString());
	}

	@Override
	public void deleteByProperty(Class entityClass, String propertyName, Object propertyValue) {
		Query qDelete = entityManager
				.createQuery("delete from " + entityClass.getSimpleName() + " t where t." + propertyName + " = ?1");
		qDelete.setParameter(1, propertyValue);
		qDelete.executeUpdate();
		logger.debug("Object removed with condition: {}={}", new Object[] { propertyName, propertyValue });
	}

	@Override
	public <T> T find(Class<T> entityClass, Object id) {
		T t = entityManager.find(entityClass, id);
		logger.debug("Object found: {}", t.toString());
		return t;
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		List<T> list = entityManager.createQuery("select t from " + entityClass.getSimpleName() + " t", entityClass)
				.getResultList();
		logger.debug("Objects found: {}", list);
		return list;
	}

	@Override
	public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object propertyValue,
			Integer maxResults) {
		TypedQuery<T> query = entityManager.createQuery(
				"select t from " + entityClass.getSimpleName() + " t where t." + propertyName + "= :propertyValue",
				entityClass).setParameter("propertyValue", propertyValue);
		if (maxResults != null && maxResults.intValue() > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<T> list = query.getResultList();
		logger.debug("Objects found: {}", list);
		return list;
	}

	@Override
	public <T> List<T> findByProperties(Class<T> entityClass, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> from = criteria.from(entityClass);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					Predicate pred = builder.equal(from.get(entry.getKey()), entry.getValue());
					predicate = predicate == null ? pred : builder.and(predicate, pred);
				}
			}
			if (predicate != null) {
				criteria.where(predicate);
			}
		}

		if (orders != null && !orders.isEmpty()) {
			List<Order> orderList = new ArrayList<Order>();
			for (PropertyOrder order : orders) {
				orderList.add(order.getOrderType() == OrderType.ASC ? builder.asc(from.get(order.getPropertyName()))
						: builder.desc(from.get(order.getPropertyName())));
			}
			criteria.orderBy(orderList);
		}

		TypedQuery<T> query = entityManager.createQuery(criteria);
		if (maxResults != null) {
			query = query.setMaxResults(maxResults);
		}

		List<T> list = query.getResultList();
		logger.debug("Objects found: {}", list);
		return list;
	}

	@Override
	public <T> List<T> findByPropertiesAndOperators(Class<T> entityClass, Map<String, ArrayList> propertiesMap,
			List<PropertyOrder> orders, Integer offset, Integer maxResults) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> from = criteria.from(entityClass);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			for (Entry<String, ArrayList> entry : propertiesMap.entrySet()) {
				if (entry.getKey() != null && entry.getValue() != null) {
					ArrayList list = entry.getValue();
					Object value = list.get(0);
					String operator = (String) list.get(1);
					Predicate pred = null;

					if (operator.equals("equal")) {
						pred = builder.equal(from.get(entry.getKey()), value);
					} else if (operator.equals("lessThanOrEqualTo")) {
						try {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date date = format.parse((String) value);
							Path<Date> dateCreatedPath = from.get(entry.getKey().replace("_", ""));
							pred = builder.lessThanOrEqualTo(dateCreatedPath, date);
						} catch (Exception e) {

						}
					} else if (operator.equals("greaterThanOrEqualTo")) {
						try {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date date = format.parse((String) value);
							Path<Date> dateCreatedPath = from.get(entry.getKey().replace("_", ""));
							pred = builder.greaterThanOrEqualTo(dateCreatedPath, date);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					predicate = predicate == null ? pred : builder.and(predicate, pred);
				}
			}
			if (predicate != null)
				criteria.where(predicate);
		}

		if (orders != null && !orders.isEmpty()) {
			List<Order> orderList = new ArrayList<Order>();
			for (PropertyOrder order : orders) {
				orderList.add(order.getOrderType() == OrderType.ASC ? builder.asc(from.get(order.getPropertyName()))
						: builder.desc(from.get(order.getPropertyName())));
			}
			criteria.orderBy(orderList);
		}

		TypedQuery<T> query = entityManager.createQuery(criteria);
		if (maxResults != null) {
			query = query.setFirstResult(offset).setMaxResults(maxResults);
		}

		List<T> list = query.getResultList();
		logger.debug("Objects found: {}", list);
		return list;
	}

	/**
	 * Returns the table name for a given entity type in the
	 * {@link EntityManager}.
	 * 
	 * @param entityClass
	 * @return
	 */
	public <T> String getTableName(Class<T> entityClass) {
		/*
		 * Check if the specified class is present in the metamodel. Throws
		 * IllegalArgumentException if not.
		 */
		Metamodel meta = entityManager.getMetamodel();
		EntityType<T> entityType = meta.entity(entityClass);

		// Check whether @Table annotation is present on the class.
		Table t = entityClass.getAnnotation(Table.class);

		String tableName = (t == null) ? entityType.getName().toUpperCase() : t.name();
		logger.debug("Table name found: {}", tableName);
		return tableName;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
