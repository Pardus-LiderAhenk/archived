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
import tr.org.liderahenk.lider.core.api.persistence.dao.IMailParameterDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.persistence.entities.MailParameterImpl;

public class MailParameterDaoImpl implements IMailParameterDao  {

	
	private static Logger logger = LoggerFactory.getLogger(MailParameterDaoImpl.class);
	
	
	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing MailManagementDao");
	}

	public void destroy() {
		logger.info("Destroying MailManagementDao");
	}

	@Override
	public IMailParameter save(IMailParameter mailContent) {
		MailParameterImpl impl = new MailParameterImpl(mailContent);
		impl.setCreateDate(new Date());
		impl.setModifyDate(null);
		entityManager.persist(impl);
		logger.debug("IMailAddress object persisted: {}", impl.toString());
		return impl;
	}

	@Override
	public IMailParameter update(IMailParameter mailContent) {
		MailParameterImpl impl = new MailParameterImpl(mailContent);
		impl.setModifyDate(new Date());
		impl = entityManager.merge(impl);
		logger.debug("IMailAddress object merged: {}", impl.toString());
		return impl;
	}

	@Override
	public void delete(Long id) {
		MailParameterImpl impl = entityManager.find(MailParameterImpl.class, id);
		// Never truly delete, just mark as deleted!
		impl.setDeleted(true);
		impl.setModifyDate(new Date());
		impl = entityManager.merge(impl);
		logger.debug("IMailAddress object marked as deleted: {}", impl.toString());
		
	}

	@Override
	public IMailParameter find(Long id) {
		MailParameterImpl impl = entityManager.find(MailParameterImpl.class, id);
		logger.debug("IMailAddress object found: {}", impl.toString());
		return impl;
	}

	@Override
	public List<? extends IMailParameter> findAll(Class<? extends IMailParameter> obj, Integer maxResults) {
		List<MailParameterImpl> list = entityManager
				.createQuery("select t from " + MailParameterImpl.class.getSimpleName() + " t", MailParameterImpl.class)
				.getResultList();
		logger.debug("IMailAddress objects found: {}", list);
		return list;
	}

	@Override
	public List<? extends IMailParameter> findByProperty(Class<? extends IMailParameter> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		
		
		TypedQuery<MailParameterImpl> query = entityManager.createQuery("select t from " + MailParameterImpl.class.getSimpleName()
				+ " t where t." + propertyName + "= :propertyValue", MailParameterImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<MailParameterImpl> impl = query.getResultList();
		logger.debug("IMailAddress objects found: {}", impl);
		return impl;
		
		
	}

	@Override
	public List<? extends IMailParameter> findByProperties(Class<? extends IMailParameter> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults) {
		
		orders = new ArrayList<PropertyOrder>();
		
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<MailParameterImpl> criteria = (CriteriaQuery<MailParameterImpl>) builder.createQuery(MailParameterImpl.class);
		
		Root<MailParameterImpl> from = (Root<MailParameterImpl>) criteria.from(MailParameterImpl.class);
		
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

		List<MailParameterImpl> list = null;
		
		
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
