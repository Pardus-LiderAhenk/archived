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
import tr.org.liderahenk.lider.core.api.persistence.dao.IMailAddressDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.persistence.entities.MailAddressImpl;

public class MailAdressDaoImpl implements IMailAddressDao  {

	
	private static Logger logger = LoggerFactory.getLogger(MailAdressDaoImpl.class);
	
	
	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing MailManagementDao");
	}

	public void destroy() {
		logger.info("Destroying MailManagementDao");
	}

	@Override
	public IMailAddress save(IMailAddress mailAddress) {
		MailAddressImpl mailAddressImpl = new MailAddressImpl(mailAddress);
		mailAddressImpl.setCreateDate(new Date());
		mailAddressImpl.setModifyDate(null);
		entityManager.persist(mailAddressImpl);
		logger.debug("IMailAddress object persisted: {}", mailAddressImpl.toString());
		return mailAddressImpl;
	}

	@Override
	public IMailAddress update(IMailAddress mailAddress) {
		MailAddressImpl mailAddressImpl = new MailAddressImpl(mailAddress);
		mailAddressImpl.setModifyDate(new Date());
		mailAddressImpl = entityManager.merge(mailAddressImpl);
		logger.debug("IMailAddress object merged: {}", mailAddressImpl.toString());
		return mailAddressImpl;
	}

	@Override
	public void delete(Long mailAddressId) {
		MailAddressImpl mailAddressImpl = entityManager.find(MailAddressImpl.class, mailAddressId);
		// Never truly delete, just mark as deleted!
		mailAddressImpl.setDeleted(true);
		mailAddressImpl.setModifyDate(new Date());
		mailAddressImpl = entityManager.merge(mailAddressImpl);
		logger.debug("IMailAddress object marked as deleted: {}", mailAddressImpl.toString());
		
	}

	@Override
	public IMailAddress find(Long mailAddressId) {
		MailAddressImpl mailAddressImpl = entityManager.find(MailAddressImpl.class, mailAddressId);
		logger.debug("IMailAddress object found: {}", mailAddressImpl.toString());
		return mailAddressImpl;
	}

	@Override
	public List<? extends IMailAddress> findAll(Class<? extends IMailAddress> obj, Integer maxResults) {
		List<MailAddressImpl> mailAddressImplList = entityManager
				.createQuery("select t from " + MailAddressImpl.class.getSimpleName() + " t", MailAddressImpl.class)
				.getResultList();
		logger.debug("IMailAddress objects found: {}", mailAddressImplList);
		return mailAddressImplList;
	}

	@Override
	public List<? extends IMailAddress> findByProperty(Class<? extends IMailAddress> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		
		
		TypedQuery<MailAddressImpl> query = entityManager.createQuery("select t from " + MailAddressImpl.class.getSimpleName()
				+ " t where t." + propertyName + "= :propertyValue" , MailAddressImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<MailAddressImpl> mailAddressImplList = query.getResultList();

		logger.debug("IMailAddress objects found: {}", mailAddressImplList);
		return mailAddressImplList;
		
		
	}

	@Override
	public List<? extends IMailAddress> findByProperties(Class<? extends IMailAddress> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults) {
		
		orders = new ArrayList<PropertyOrder>();
		
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<MailAddressImpl> criteria = (CriteriaQuery<MailAddressImpl>) builder.createQuery(MailAddressImpl.class);
		
		Root<MailAddressImpl> from = (Root<MailAddressImpl>) criteria.from(MailAddressImpl.class);
		
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

		List<MailAddressImpl> list = null;
		
		
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
