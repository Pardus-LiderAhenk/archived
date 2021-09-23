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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.persistence.entities.ProfileImpl;

/**
 * Provides database access for profiles. CRUD operations for profiles and their
 * plugin or policy records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IProfile
 *
 */
public class ProfileDaoImpl implements IProfileDao {

	private static Logger logger = LoggerFactory.getLogger(ProfileDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing profile DAO.");
	}

	public void destroy() {
		logger.info("Destroying profile DAO.");
	}

	@Override
	public IProfile save(IProfile profile) {
		ProfileImpl profileImpl = new ProfileImpl(profile);
		profileImpl.setCreateDate(new Date());
		profileImpl.setModifyDate(null);
		entityManager.persist(profileImpl);
		logger.debug("IProfile object persisted: {}", profileImpl.toString());
		return profileImpl;
	}

	@Override
	public ProfileImpl update(IProfile profile) {
		ProfileImpl profileImpl = new ProfileImpl(profile);
		profileImpl.setModifyDate(new Date());
		profileImpl = entityManager.merge(profileImpl);
		logger.debug("IProfile object merged: {}", profileImpl.toString());
		return profileImpl;
	}

	@Override
	public void delete(Long profileId) {
		ProfileImpl profileImpl = entityManager.find(ProfileImpl.class, profileId);
		// Never truly delete, just mark as deleted!
		profileImpl.setDeleted(true);
		profileImpl.setModifyDate(new Date());
		profileImpl = entityManager.merge(profileImpl);
		logger.debug("IProfile object marked as deleted: {}", profileImpl.toString());
	}

	@Override
	public ProfileImpl find(Long profileId) {
		ProfileImpl profileImpl = entityManager.find(ProfileImpl.class, profileId);
		logger.debug("IProfile object found: {}", profileImpl.toString());
		return profileImpl;
	}

	@Override
	public List<? extends IProfile> findAll(Class<? extends IProfile> obj, Integer maxResults) {
		List<ProfileImpl> profileList = entityManager
				.createQuery("select t from " + ProfileImpl.class.getSimpleName() + " t", ProfileImpl.class)
				.getResultList();
		logger.debug("IProfile objects found: {}", profileList);
		return profileList;
	}

	@Override
	public List<? extends IProfile> findByProperty(Class<? extends IProfile> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		TypedQuery<ProfileImpl> query = entityManager.createQuery("select t from " + ProfileImpl.class.getSimpleName()
				+ " t where t." + propertyName + "= :propertyValue", ProfileImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<ProfileImpl> profileList = query.getResultList();
		logger.debug("IProfile objects found: {}", profileList);
		return profileList;
	}

	@Override
	public List<? extends IProfile> findByProperties(Class<? extends IProfile> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		orders = new ArrayList<PropertyOrder>();
		// TODO
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProfileImpl> criteria = (CriteriaQuery<ProfileImpl>) builder.createQuery(ProfileImpl.class);
		Root<ProfileImpl> from = (Root<ProfileImpl>) criteria.from(ProfileImpl.class);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey() != null ? entry.getKey().split("\\.") : null;
					if (key != null && key.length > 1) {
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

		List<ProfileImpl> list = null;
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
