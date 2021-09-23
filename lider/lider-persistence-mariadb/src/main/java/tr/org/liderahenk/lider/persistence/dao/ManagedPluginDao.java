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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IManagedPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.plugin.deployer.IManagedPlugin;
import tr.org.liderahenk.lider.persistence.entities.ManagedPlugin;

public class ManagedPluginDao implements IManagedPluginDao {
	private static Logger logger = LoggerFactory.getLogger(PluginDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing plugin DAO.");
	}

	public void destroy() {
		logger.info("Destroying plugin DAO.");
	}

	@Override
	public IManagedPlugin save(IManagedPlugin plugin) {
		ManagedPlugin mplugin = new ManagedPlugin(plugin);
		mplugin.setInstallationDate(new Date());
		mplugin.setActive(true);
		entityManager.persist(mplugin);
		logger.debug("IPlugin object persisted: {}", mplugin.toString());
		return mplugin;
	}

	@Override
	public IManagedPlugin update(IManagedPlugin plugin) {
		ManagedPlugin mplugin = new ManagedPlugin(plugin);
		mplugin.setInstallationDate(new Date());
		mplugin.setActive(true);
		logger.debug("IPlugin object merged: {}", mplugin.toString());
		return mplugin;
	}

	@Override
	public void delete(Long pluginId) {
		ManagedPlugin mplugin = entityManager.find(ManagedPlugin.class, pluginId);
		// Never truly delete, just mark as deleted!
		mplugin.setActive(false);
		mplugin = entityManager.merge(mplugin);
		logger.debug("IPlugin object marked as deleted: {}", mplugin.toString());
	}

	@Override
	public ManagedPlugin find(Long pluginId) {
		ManagedPlugin mplugin = entityManager.find(ManagedPlugin.class, pluginId);
		logger.debug("IPlugin object found: {}", mplugin.toString());
		return mplugin;
	}

	@Override
	public List<? extends IManagedPlugin> findAll(Class<? extends IManagedPlugin> obj, Integer maxResults) {
		List<ManagedPlugin> pluginList = entityManager
				.createQuery("select t from " + ManagedPlugin.class.getSimpleName() + " t", ManagedPlugin.class)
				.getResultList();
		logger.debug("IPlugin objects found: {}", pluginList);
		return pluginList;
	}

	@Override
	public List<? extends IManagedPlugin> findByProperty(Class<? extends IManagedPlugin> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		TypedQuery<ManagedPlugin> query = entityManager
				.createQuery("select t from " + ManagedPlugin.class.getSimpleName() + " t where t." + propertyName
						+ "= :propertyValue", ManagedPlugin.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<ManagedPlugin> pluginList = query.getResultList();
		logger.debug("IPlugin objects found: {}", pluginList);
		return pluginList;
	}

	@Override
	public List<? extends IManagedPlugin> findByProperties(Class<? extends IManagedPlugin> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults) {
		orders = new ArrayList<PropertyOrder>();
		// TODO
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ManagedPlugin> criteria = (CriteriaQuery<ManagedPlugin>) builder.createQuery(ManagedPlugin.class);
		Root<ManagedPlugin> from = (Root<ManagedPlugin>) criteria.from(ManagedPlugin.class);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					Predicate pred = builder.equal(from.get(entry.getKey()), entry.getValue());
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

		List<ManagedPlugin> list = null;
		if (null != maxResults) {
			list = entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
		} else {
			list = entityManager.createQuery(criteria).getResultList();
		}

		return list;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
