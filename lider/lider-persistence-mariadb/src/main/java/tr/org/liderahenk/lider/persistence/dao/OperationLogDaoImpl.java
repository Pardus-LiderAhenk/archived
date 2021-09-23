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
package tr.org.liderahenk.lider.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.directory.api.util.exception.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IOperationLogDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.persistence.entities.OperationLogImpl;

public class OperationLogDaoImpl implements IOperationLogDao {

	private final static Logger logger = LoggerFactory.getLogger(OperationLogDaoImpl.class.getName());

	EntityManager entityManager;

	public void init() {
		logger.info("Initializing operation log DAO.");
	}

	public void destroy() {
		logger.info("Destroying operation log DAO.");
	}

	@Override
	public IOperationLog save(IOperationLog log) {
		OperationLogImpl logImpl = new OperationLogImpl(log);
		entityManager.persist(logImpl);
		logger.debug("IOperationLog object persisted: {}", logImpl.toString());
		return logImpl;
	}

	@Override
	public OperationLogImpl update(IOperationLog log) {
		OperationLogImpl logImpl = new OperationLogImpl(log);
		logImpl = entityManager.merge(logImpl);
		logger.debug("IOperationLog object merged: {}", logImpl.toString());
		return logImpl;
	}

	@Override
	public void delete(Long logId) {
		throw new NotImplementedException();
	}

	@Override
	public OperationLogImpl find(Long logId) {
		OperationLogImpl logImpl = entityManager.find(OperationLogImpl.class, logId);
		logger.debug("IOperationLog object found: {}", logImpl.toString());
		return logImpl;
	}

	@Override
	public List<? extends IOperationLog> findAll(Class<? extends IOperationLog> obj, Integer maxResults) {
		List<OperationLogImpl> logList = entityManager
				.createQuery("select t from " + OperationLogImpl.class.getSimpleName() + " t", OperationLogImpl.class)
				.getResultList();
		logger.debug("IOperationLog objects found: {}", logList);
		return logList;
	}

	@Override
	public List<? extends IOperationLog> findByProperty(Class<? extends IOperationLog> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		TypedQuery<OperationLogImpl> query = entityManager
				.createQuery("select t from " + OperationLogImpl.class.getSimpleName() + " t where t." + propertyName
						+ "= :propertyValue", OperationLogImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<OperationLogImpl> logList = query.getResultList();
		logger.debug("IOperationLog objects found: {}", logList);
		return logList;
	}

	@Override
	public List<? extends IOperationLog> findByProperties(Class<? extends IOperationLog> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults) {
		orders = new ArrayList<PropertyOrder>();
		// TODO
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OperationLogImpl> criteria = (CriteriaQuery<OperationLogImpl>) builder
				.createQuery(OperationLogImpl.class);
		Root<OperationLogImpl> from = (Root<OperationLogImpl>) criteria.from(OperationLogImpl.class);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey().split(".");
					if (key.length > 1) {
						Join<Object, Object> join = null;
						for (int i = 0; i < key.length - 1; i++) {
							join = join != null ? join.join(key[i]) : from.join(key[i]);
						}
						pred = builder.equal(join.get(key[key.length - 1]), entry.getValue());
					} else {
						pred = builder.equal(from.get(entry.getKey()), entry.getValue());
					}
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

		List<OperationLogImpl> list = null;
		if (null != maxResults) {
			list = entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
		} else {
			list = entityManager.createQuery(criteria).getResultList();
		}

		return list;
	}

	/**
	 * 
	 * @param entityManager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
